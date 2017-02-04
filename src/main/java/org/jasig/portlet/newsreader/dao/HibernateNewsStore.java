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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Query;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * HibernateNewsStore provides a hibernate implementation of the NewsStore.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class HibernateNewsStore extends HibernateDaoSupport implements NewsStore {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void storeNewsDefinition(NewsDefinition listing) {
        getHibernateTemplate().saveOrUpdate(listing);
        getHibernateTemplate().flush();
    }

    public void storeNewsConfiguration(NewsConfiguration configuration) {
        getHibernateTemplate().saveOrUpdate(configuration);
        getHibernateTemplate().flush();
    }

    public List<NewsConfiguration> getNewsConfigurations(
            String subscribeId) {
        logger.debug("fetching news configurations for " + subscribeId);
        return (List<NewsConfiguration>) getHibernateTemplate().find(
                "from NewsConfiguration config where "
                        + "subscribeId = ? and displayed = true "
                        + "order by newsDefinition.name", subscribeId);
    }

    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {

        String query = "from NewsConfiguration config where "
                + "config.newsSet.id = ? and "
                + "config.class = UserDefinedNewsConfiguration "
                + "order by newsDefinition.name";
        if (visibleOnly)
            query = query.concat(" and visibleOnly = true");

        return (List<UserDefinedNewsConfiguration>) getHibernateTemplate()
                .find(query, setId);

    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {
        String query = "from NewsConfiguration config "
                + "where config.newsSet.id = ? and "
                + "config.class = PredefinedNewsConfiguration "
                + "order by newsDefinition.name";
        if (visibleOnly)
            query = query.concat(" and visibleOnly = true");

        return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                .find(query, setId);

    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations() {

        String query = "from NewsDefinition def "
                + "where def.class = PredefinedNewsDefinition "
                + "order by def.name";
        return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                .find(query);

    }

    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(Long setId, Set<String> roles) {
        String query = "from PredefinedNewsDefinition def "
                + "where :setId not in (select config.newsSet.id "
                + "from def.userConfigurations config) ";
        for (int i = 0; i < roles.size(); i++) {
            query = query.concat(
                    "and :role" + i + " not in elements(def.defaultRoles) ");
        }

        Query q = this.currentSession().createQuery(query);
        q.setLong("setId", setId);
        int count = 0;
        for (String role : roles) {
            q.setString("role" + count, role);
            count++;
        }
        return (List<PredefinedNewsDefinition>) q.list();

    }

    public void initNews(NewsSet set, Set<String> roles) {
        // if the user doesn't have any roles, we don't have any
        // chance of getting predefined news, so just go ahead
        // and return
        if (roles.isEmpty())
            return;

        String query = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + ":setId not in (select config.newsSet.id "
                + "from def.userConfigurations config) "
                + "and role in (:roles)";
        String[] params = {"setId", "roles"};
        Object[] values = {set.getId(), roles};
        List<PredefinedNewsDefinition> defs = (List<PredefinedNewsDefinition>)
                getHibernateTemplate().findByNamedParam(query, params, values);

        logger.debug("Found the following PredefinedNewsDefinition objects for NewsSet={} and roles={}:  {}", set.getName(), roles, defs);

        for (PredefinedNewsDefinition def : defs) {
            PredefinedNewsConfiguration config = new PredefinedNewsConfiguration();
            config.setNewsDefinition(def);
            set.addNewsConfiguration(config);
        }

    }

    public PredefinedNewsDefinition getPredefinedNewsDefinition(Long id) {
        String query = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + "def.id = :id";
        Query q = this.currentSession().createQuery(query);
        q.setLong("id", id);
        return (PredefinedNewsDefinition) q.uniqueResult();

    }

    public PredefinedNewsDefinition getPredefinedNewsDefinitionByName(String name) {
        String query = "from PredefinedNewsDefinition def "
                + "left join fetch def.defaultRoles role where "
                + "def.name = :name";
        Query q = this.currentSession().createQuery(query);
        q.setString("name", name);
        return (PredefinedNewsDefinition) q.uniqueResult();


    }

    public NewsDefinition getNewsDefinition(Long id) {
        return (NewsDefinition) getHibernateTemplate().get(NewsDefinition.class, id);
    }

    public NewsConfiguration getNewsConfiguration(Long id) {
        return (NewsConfiguration) getHibernateTemplate().load(
                NewsConfiguration.class, id);
    }

    public void deleteNewsConfiguration(NewsConfiguration configuration) {
        getHibernateTemplate().delete(configuration);
        getHibernateTemplate().flush();
    }

    public void deleteNewsDefinition(PredefinedNewsDefinition definition) {
        String query = "from NewsConfiguration config "
                + "where config.newsDefinition.id = ? and "
                + "config.class = PredefinedNewsConfiguration";

        List<PredefinedNewsConfiguration> configs = (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                .find(query, definition.getId());
        getHibernateTemplate().deleteAll(configs);

        getHibernateTemplate().delete(definition);
        getHibernateTemplate().flush();
    }

    public List<String> getUserRoles() {
        String query = "select distinct elements(def.defaultRoles) " +
                "from PredefinedNewsDefinition def ";

        return (List<String>) getHibernateTemplate()
                .find(query);
    }

    public NewsSet getNewsSet(Long id) {
        return (NewsSet) getHibernateTemplate().get(NewsSet.class, id);
    }

    public List<NewsSet> getNewsSetsForUser(String userId) {
        logger.debug("fetching news sets for " + userId);
        return (List<NewsSet>) getHibernateTemplate().find(
                "from NewsSet newsSet where "
                        + "newsSet.userId = ? "
                        + "order by newsSet.name", userId);
    }

    public void storeNewsSet(NewsSet set) {
        getHibernateTemplate().saveOrUpdate(set);
        getHibernateTemplate().flush();
    }

    public NewsSet getNewsSet(String userId, String setName) {
        logger.debug("fetching news sets for " + userId);
        String query = "from NewsSet newsSet where :userId = newsSet.userId and " +
                ":setName = newsSet.name order by newsSet.name";

        Query q = this.currentSession().createQuery(query);
        q.setString("userId", userId);
        q.setString("setName", setName);
        if (logger.isDebugEnabled()) {
            logger.debug(this.getSessionFactory().getStatistics().toString());
        }
        NewsSet set = (NewsSet) q.uniqueResult();
        if (logger.isDebugEnabled()) {
            logger.debug(this.getSessionFactory().getStatistics().toString());
        }
        return set;
    }
}
