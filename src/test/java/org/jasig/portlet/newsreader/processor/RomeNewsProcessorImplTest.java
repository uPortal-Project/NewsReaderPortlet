/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.processor;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/org/jasig/portlet/newsreader/processor/romeProcessorTestContext.xml")
public class RomeNewsProcessorImplTest {

    @Autowired(required = true)
    RomeNewsProcessorImpl processor;

    @Autowired(required = true)
    ApplicationContext context;
    
    Resource testFeed;
    
    @Before
    public void setUp() {
        testFeed = context.getResource("classpath:/org/jasig/portlet/newsreader/processor/testFeed.xml");
    }
    
    @Test
    public void testParsing() throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
        InputStream in = testFeed.getInputStream();
        NewsFeed feed = processor.getFeed(in, "antisamy-textonly", "antisamy-textonly");
        
        assertEquals(10, feed.getEntries().size());
        NewsFeedItem item = (NewsFeedItem) feed.getEntries().get(0);
        assertEquals("http://d.yimg.com/a/p/ap/20110310/capt.8a3eb82d06714371b5e5f23978453a1e-8a3eb82d06714371b5e5f23978453a1e-0.jpg?x=130&y=86&q=85&sig=PllYTfbufZhPAizTiij5GQ--", item.getImageUrl());
        
        in.close();
    }
    
}
