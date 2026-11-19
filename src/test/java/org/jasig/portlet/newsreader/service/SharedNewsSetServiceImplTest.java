/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.newsreader.service;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.portlet.PortletRequest;

import org.hibernate.EmptyInterceptor;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.portlet.MockPortletSession;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/org/jasig/portlet/newsreader/service/sharedNewsSetTestContext.xml")
public class SharedNewsSetServiceImplTest {

    private static final String USER = "student";
    private static final String SET_NAME = "default";

    @Autowired
    NewsStore newsStore;

    @Autowired
    FlushPause flushPause;

    SharedNewsSetServiceImpl service;

    ExecutorService executor;

    @Before
    public void setUp() {
        final UserIdService userIdService = new UserIdService() {
            @Override
            public String getUserId(PortletRequest req) {
                return USER;
            }
        };
        final RolesService rolesService = new RolesService() {
            @Override
            public Set<String> getUserRoles(PortletRequest req) {
                return Collections.emptySet();
            }
        };

        service = new SharedNewsSetServiceImpl();
        service.setNewsStore(newsStore);
        ReflectionTestUtils.setField(service, "userIdService", userIdService);
        ReflectionTestUtils.setField(service, "rolesService", rolesService);

        executor = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() {
        executor.shutdownNow();
    }

    /**
     * Two portlet sessions of one user ask for the user's news set at the same
     * moment.  The second starts while the first is saving the set.
     */
    @Test
    public void twoSessionsOfOneUserCanLoadTheNewsSetAtOnce() throws Exception {
        // Create the user's set, then read it so that the store has it cached.
        assertNotNull(service.getNewsSet(SET_NAME, requestInNewSession()));
        assertNotNull(newsStore.getNewsSet(USER, SET_NAME));

        final CountDownLatch firstIsSaving = new CountDownLatch(1);
        final CountDownLatch secondIsDone = new CountDownLatch(1);
        flushPause.pauseNextFlush(firstIsSaving, secondIsDone);

        final Future<NewsSet> first = executor.submit(new Callable<NewsSet>() {
            public NewsSet call() {
                return service.getNewsSet(SET_NAME, requestInNewSession());
            }
        });
        final Future<NewsSet> second = executor.submit(new Callable<NewsSet>() {
            public NewsSet call() throws Exception {
                firstIsSaving.await(30, TimeUnit.SECONDS);
                try {
                    return service.getNewsSet(SET_NAME, requestInNewSession());
                } finally {
                    secondIsDone.countDown();
                }
            }
        });

        // Future.get() throws what the session's call threw.
        assertNotNull(first.get(60, TimeUnit.SECONDS));
        assertNotNull(second.get(60, TimeUnit.SECONDS));
    }

    private static PortletRequest requestInNewSession() {
        final MockRenderRequest request = new MockRenderRequest();
        request.setSession(new MockPortletSession());
        return request;
    }

    /**
     * Holds one flush open: it reports that the flush has started, then waits
     * a few seconds for the other session to finish.  A flush comes after
     * <code>saveOrUpdate</code>, so the set is attached to the first Hibernate
     * session during the wait.
     */
    public static class FlushPause extends EmptyInterceptor {

        private static final long serialVersionUID = 1L;

        private volatile CountDownLatch started;
        private volatile CountDownLatch resume;

        void pauseNextFlush(CountDownLatch started, CountDownLatch resume) {
            this.resume = resume;
            this.started = started;
        }

        @Override
        public void preFlush(@SuppressWarnings("rawtypes") Iterator entities) {
            final CountDownLatch started = this.started;
            if (started == null) {
                return;
            }
            this.started = null;
            started.countDown();
            try {
                resume.await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
