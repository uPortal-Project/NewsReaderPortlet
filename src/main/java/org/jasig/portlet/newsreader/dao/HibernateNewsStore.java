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
import org.springframework.transaction.annotation.Transactional;

/**
 * HibernateNewsStore provides a hibernate implementation of the NewsStore.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Repository
@Transactional
public class HibernateNewsStore implements NewsStore {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    private <T> Query<T> generateQuery(String hql) {
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
        String hql =
                "    FROM NewsConfiguration AS config " +
                "   WHERE subscribeId = ? " +
                "     AND displayed = TRUE " +
                "ORDER BY newsDefinition.name";
        Query<NewsConfiguration> query = generateQuery(hql);
        query.setParameter(0, subscribeId);
        return query.list();
    }

    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(Long setId, boolean visibleOnly) {
        String hql =
                "    FROM NewsConfiguration AS config " +
                "   WHERE config.newsSet.id = ? " +
                "     AND config.class = UserDefinedNewsConfiguration " +
                "ORDER BY newsDefinition.name";
        if (visibleOnly) {
            hql = hql.concat(" AND visibleOnly = TRUE");
        }

        Query<UserDefinedNewsConfiguration> query = generateQuery(hql);
        return query.setParameter(0, setId).list();
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(Long setId, boolean visibleOnly) {
        String hql =
                "    FROM NewsConfiguration AS config " +
                "   WHERE config.newsSet.id = ? " +
                "     AND config.class = PredefinedNewsConfiguration " +
                "ORDER BY newsDefinition.name";
        if (visibleOnly)
            hql = hql.concat(" AND visibleOnly = TRUE");

        Query<PredefinedNewsConfiguration> query = generateQuery(hql);
        return query.setParameter(0, setId).list();
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations() {
        String hql =
                "    FROM NewsDefinition AS def " +
                "   WHERE def.class = PredefinedNewsDefinition " +
                "ORDER BY def.name";
        Query<PredefinedNewsConfiguration> query = generateQuery(hql);
        return query.list();
    }

    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(Long setId, Set<String> roles) {
        String hql =
                " FROM PredefinedNewsDefinition AS def " +
                "WHERE :setId NOT IN " +
                "      (SELECT config.newsSet.id " +
                "         FROM def.userConfigurations AS config) ";
        for (int i = 0; i < roles.size(); i++) {
            hql = hql.concat("AND :role" + i + " NOT IN ELEMENTS(def.defaultRoles) ");
        }

        Query<PredefinedNewsDefinition> q = generateQuery(hql);
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

        String hql =
                "     FROM PredefinedNewsDefinition AS def " +
                "LEFT JOIN FETCH def.defaultRoles AS role " +
                "    WHERE :setId NOT IN " +
                "          (SELECT config.newsSet.id " +
                "             FROM def.userConfigurations AS config) " +
                "              AND role IN (:roles)";
        Query<PredefinedNewsDefinition> query = generateQuery(hql);
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
        String hql =
                "     FROM PredefinedNewsDefinition AS def " +
                "LEFT JOIN FETCH def.defaultRoles AS role " +
                "    WHERE def.id = :id";
        Query<PredefinedNewsDefinition> query = generateQuery(hql);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public PredefinedNewsDefinition getPredefinedNewsDefinitionByName(String name) {
        String hql =
                "     FROM PredefinedNewsDefinition AS def " +
                "LEFT JOIN FETCH def.defaultRoles AS role " +
                "    WHERE def.name = :name";
        Query<PredefinedNewsDefinition> query = generateQuery(hql);
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
        String hql =
                " FROM NewsConfiguration AS config " +
                "WHERE config.newsDefinition.id = ? " +
                "  AND config.class = PredefinedNewsConfiguration";

        Query<PredefinedNewsConfiguration> query = generateQuery(hql);
        List<PredefinedNewsConfiguration> configs = query.setParameter(0, definition.getId()).list();
        for (PredefinedNewsConfiguration config : configs) {
            currentSession().delete(config);
        }
        currentSession().delete(definition);
        currentSession().flush();
    }

    public List<String> getUserRoles() {
        String sql =
                "SELECT DISTINCT ELEMENTS(def.defaultRoles) " +
                "  FROM PredefinedNewsDefinition AS def ";
        Query<String> query = generateQuery(sql);
        return query.list();
    }

    public NewsSet getNewsSet(Long id) {
        return currentSession().get(NewsSet.class, id);
    }

    public List<NewsSet> getNewsSetsForUser(String userId) {
        logger.debug("fetching news sets for " + userId);
        String hql =
                "    FROM NewsSet AS newsSet " +
                "   WHERE newsSet.userId = ? " +
                "ORDER BY newsSet.name";
        Query<NewsSet> query = generateQuery(hql);
        query.setParameter(0, userId);
        return query.list();
    }

    public void storeNewsSet(NewsSet set) {
        currentSession().saveOrUpdate(set);
        currentSession().flush();
    }

    public NewsSet getNewsSet(String userId, String setName) {
        logger.debug("fetching news sets for " + userId);
        String hql =
                "    FROM NewsSet AS newsSet " +
                "   WHERE :userId = newsSet.userId " +
                "     AND :setName = newsSet.name " +
                "ORDER BY newsSet.name";

        Query<NewsSet> q = generateQuery(hql);
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
