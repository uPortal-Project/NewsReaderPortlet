/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * HibernateNewsStore provides a hibernate implementation of the NewsStore.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Repository
public class HibernateNewsStore implements NewsStore {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private <T> Query<T> createQuery(String hql) {
        @SuppressWarnings("unchecked")
        Query<T> query = sessionFactory.getCurrentSession().createQuery(hql);
        return query;
    }

    public void storeNewsDefinition(NewsDefinition listing) {
        currentSession().saveOrUpdate(listing);
        currentSession().flush();
    }

    public void storeNewsConfiguration(NewsConfiguration configuration) {
        currentSession().saveOrUpdate(configuration);
        currentSession().flush();
    }

    public List<NewsConfiguration> getNewsConfigurations(String subscribeId) {
        logger.debug("fetching news configurations for " + subscribeId);
        Query<NewsConfiguration> query = createQuery(
                "from NewsConfiguration config where "
                        + "subscribeId = ? and displayed = true "
                        + "order by newsDefinition.name");
        query.setParameter(0, subscribeId);
        return query.list();
    }

    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(Long setId, boolean visibleOnly) {
        String hql = "from NewsConfiguration config where "
                + "config.newsSet.id = ? and "
                + "config.class = UserDefinedNewsConfiguration "
                + "order by newsDefinition.name";
        if (visibleOnly) {
            hql = hql.concat(" and visibleOnly = true");
        }

        Query<UserDefinedNewsConfiguration> query = createQuery(hql);
        return query.setParameter(0, setId).list();
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(Long setId, boolean visibleOnly) {
        String hql = "from NewsConfiguration config "
                + "where config.newsSet.id = ? and "
                + "config.class = PredefinedNewsConfiguration "
                + "order by newsDefinition.name";
        if (visibleOnly)
            hql = hql.concat(" and visibleOnly = true");

        Query<PredefinedNewsConfiguration> query = createQuery(hql);
        return query.setParameter(0, setId).list();
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations() {
        String hql = "from NewsDefinition def "
                + "where def.class = PredefinedNewsDefinition "
                + "order by def.name";
        Query<PredefinedNewsConfiguration> query = createQuery(hql);
        return query.list();
    }

    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(Long setId, Set<String> roles) {
        String hql = "from PredefinedNewsDefinition def "
                + "where :setId not in (select config.newsSet.id "
                + "from def.userConfigurations config) ";
        for (int i = 0; i < roles.size(); i++) {
            hql = hql.concat(
                    "and :role" + i + " not in elements(def.defaultRoles) ");
        }

        Query<PredefinedNewsDefinition> q = createQuery(hql);
        q.setParameter("setId", setId);
        int count = 0;
        for (String role : roles) {
            q.setParameter("role" + count, role);
            count++;
        }
        return q.list();

    }

    public void initNews(NewsSet set, Set<String> roles) {
        // if the user doesn't have any roles, we don't have any
        // chance of getting predefined news, so just go ahead
        // and return
        if (roles.isEmpty()) {
            return;
        }

        String hql = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + ":setId not in (select config.newsSet.id "
                + "from def.userConfigurations config) "
                + "and role in (:roles)";
        Query<PredefinedNewsDefinition> query = createQuery(hql);
        query.setParameter("setId", set.getId());
        query.setParameter("roles", roles);
        List<PredefinedNewsDefinition> defs = query.list();

        logger.debug("Found the following PredefinedNewsDefinition objects for NewsSet={} and roles={}:  {}", set.getName(), roles, defs);

        for (PredefinedNewsDefinition def : defs) {
            PredefinedNewsConfiguration config = new PredefinedNewsConfiguration();
            config.setNewsDefinition(def);
            set.addNewsConfiguration(config);
        }
    }

    public PredefinedNewsDefinition getPredefinedNewsDefinition(Long id) {
        String hql = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + "def.id = :id";
        Query<PredefinedNewsDefinition> query = createQuery(hql);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public PredefinedNewsDefinition getPredefinedNewsDefinitionByName(String name) {
        String hql = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + "def.name = :name";
        Query<PredefinedNewsDefinition> query = createQuery(hql);
        query.setParameter("name", name);
        return query.uniqueResult();
    }

    public NewsDefinition getNewsDefinition(Long id) {
        return currentSession().get(NewsDefinition.class, id);
    }

    public NewsConfiguration getNewsConfiguration(Long id) {
        return currentSession().load(NewsConfiguration.class, id);
    }

    public void deleteNewsConfiguration(NewsConfiguration configuration) {
        currentSession().delete(configuration);
        currentSession().flush();
    }

    public void deleteNewsDefinition(PredefinedNewsDefinition definition) {
        String hql = "from NewsConfiguration config "
                + "where config.newsDefinition.id = ? and "
                + "config.class = PredefinedNewsConfiguration";

        Query<PredefinedNewsConfiguration> query = createQuery(hql);
        List<PredefinedNewsConfiguration> configs = query.setParameter(0, definition.getId()).list();
        for (PredefinedNewsConfiguration config : configs) {
            currentSession().delete(config);
        }
        currentSession().delete(definition);
        currentSession().flush();
    }

    public List<String> getUserRoles() {
        String sql = "select distinct elements(def.defaultRoles) " +
                "from PredefinedNewsDefinition def ";
        Query<String> query = createQuery(sql);
        return query.list();
    }

    public NewsSet getNewsSet(Long id) {
        return currentSession().get(NewsSet.class, id);
    }

    public List<NewsSet> getNewsSetsForUser(String userId) {
        logger.debug("fetching news sets for " + userId);
        Query<NewsSet> query = createQuery(
                "from NewsSet newsSet where "
                        + "newsSet.userId = ? "
                        + "order by newsSet.name");
        query.setParameter(0, userId);
        return query.list();
    }

    public void storeNewsSet(NewsSet set) {
        currentSession().saveOrUpdate(set);
        currentSession().flush();
    }

    public NewsSet getNewsSet(String userId, String setName) {
        logger.debug("fetching news sets for " + userId);
        String hql = "from NewsSet newsSet where :userId = newsSet.userId and " +
                ":setName = newsSet.name order by newsSet.name";

        Query<NewsSet> q = createQuery(hql);
        q.setParameter("userId", userId);
        q.setParameter("setName", setName);
        q.list();
        if (logger.isDebugEnabled()) {
            logger.debug(currentSession().getStatistics().toString());
        }
        NewsSet set = q.uniqueResult();
        if (logger.isDebugEnabled()) {
            logger.debug(currentSession().getStatistics().toString());
        }
        return set;
    }
}
