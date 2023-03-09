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

package org.jasig.portlet.newsreader.dao;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.springframework.cache.annotation.Cacheable;

/**
 * HibernateNewsStore provides a hibernate implementation of the NewsStore.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class HibernateNewsStore extends HibernateDaoSupport implements NewsStore {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void storeNewsDefinition(NewsDefinition listing) {
        try {

            getHibernateTemplate().saveOrUpdate(listing);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public void storeNewsConfiguration(NewsConfiguration configuration) {
        try {

            getHibernateTemplate().saveOrUpdate(configuration);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<NewsConfiguration> getNewsConfigurations(
            String subscribeId) {
        try {

            logger.debug("fetching news configurations for " + subscribeId);
            return (List<NewsConfiguration>) getHibernateTemplate().find(
                    "FROM NewsConfiguration config WHERE "
                            + "subscribeId = ? AND displayed = true "
                            + "ORDER BY newsDefinition.name", subscribeId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {
        try {

            String query = "FROM NewsConfiguration config WHERE "
                    + "config.newsSet.id = ? AND "
                    + "config.class = UserDefinedNewsConfiguration "
                    + "ORDER BY newsDefinition.name";
            if (visibleOnly)
                query = query.concat(" AND visibleOnly = true");

            return (List<UserDefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query, setId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {
        try {
            String query = "FROM NewsConfiguration config "
                    + "WHERE config.newsSet.id = ? AND "
                    + "config.class = PredefinedNewsConfiguration "
                    + "ORDER BY newsDefinition.name";
            if (visibleOnly)
                query = query.concat(" AND visibleOnly = true");

            return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query, setId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations() {
        try {

            String query = "FROM NewsDefinition def "
                    + "WHERE def.class = PredefinedNewsDefinition "
                    + "ORDER BY def.name";
            return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(Long setId, Set<String> roles) {
        try {

            String query = "FROM PredefinedNewsDefinition def "
                    + "WHERE NOT EXISTS (FROM def.userConfigurations config WHERE config.newsSet.id = :setId) ";
            for (int i = 0; i < roles.size(); i++) {
                query = query.concat(
                        "AND :role" + i + " NOT IN elements(def.defaultRoles) ");
            }

            Query q = this.getSession().createQuery(query);
            q.setLong("setId", setId);
            int count = 0;
            for (String role : roles) {
                q.setString("role" + count, role);
                count++;
            }
            return (List<PredefinedNewsDefinition>) q.list();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public void initNews(NewsSet set, Set<String> roles) {
        try {

            // if the user doesn't have any roles, we don't have any
            // chance of getting predefined news, so just go ahead
            // and return
            if (roles.isEmpty())
                return;

            String query = "FROM PredefinedNewsDefinition def "
                    + "LEFT JOIN FETCH def.defaultRoles role "
                    + " WHERE "
                    + "NOT EXISTS (FROM def.userConfigurations config WHERE config.newsSet.id = :setId) "
                    + "AND role IN (:roles)";
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

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public PredefinedNewsDefinition getPredefinedNewsDefinition(Long id) {

        try {

            String query = "FROM PredefinedNewsDefinition def "
                    + "LEFT JOIN FETCH def.defaultRoles role WHERE "
                    + "def.id = :id";
            Query q = this.getSession().createQuery(query);
            q.setLong("id", id);
            return (PredefinedNewsDefinition) q.uniqueResult();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

    }

    public PredefinedNewsDefinition getPredefinedNewsDefinitionByName(String name) {

        try {

            String query = "FROM PredefinedNewsDefinition def "
                    + "LEFT JOIN FETCH def.defaultRoles role WHERE "
                    + "def.name = :name";
            Query q = this.getSession().createQuery(query);
            q.setString("name", name);
            return (PredefinedNewsDefinition) q.uniqueResult();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

    }

    public NewsDefinition getNewsDefinition(Long id) {

        try {

            return (NewsDefinition) getHibernateTemplate().get(NewsDefinition.class, id);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

    }

    public NewsConfiguration getNewsConfiguration(Long id) {

        try {

            return (NewsConfiguration) getHibernateTemplate().load(
                    NewsConfiguration.class, id);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

    }

    public void deleteNewsConfiguration(NewsConfiguration configuration) {
        try {

            getHibernateTemplate().delete(configuration);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public void deleteNewsDefinition(PredefinedNewsDefinition definition) {
        try {

            String query = "FROM NewsConfiguration config "
                + "WHERE config.newsDefinition.id = ? AND "
                + "config.class = PredefinedNewsConfiguration";

            List<PredefinedNewsConfiguration> configs = (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query, definition.getId());
            getHibernateTemplate().deleteAll(configs);

            getHibernateTemplate().delete(definition);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @Cacheable("HibernateNewsStore.userRoles")
    public List<String> getUserRoles() {
        try {

            String query = "SELECT DISTINCT elements(def.defaultRoles) " +
                    "FROM PredefinedNewsDefinition def ";

            return (List<String>) getHibernateTemplate()
                    .find(query);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @Cacheable("HibernateNewsStore.newsSetById")
	public NewsSet getNewsSet(Long id) {

        try {

            return (NewsSet) getHibernateTemplate().get(NewsSet.class, id);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

	}

    @Cacheable("HibernateNewsStore.newsSetByUser")
	public List<NewsSet> getNewsSetsForUser(String userId) {
        try {

            logger.debug("fetching news sets for " + userId);
            return (List<NewsSet>) getHibernateTemplate().find(
                    "FROM NewsSet newsSet WHERE "
                            + "newsSet.userId = ? "
                            + "ORDER BY newsSet.name", userId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

    @CacheEvict(cacheNames = {
            "HibernateNewsStore.newsSetById",
            "HibernateNewsStore.newsSetByUser",
            "HibernateNewsStore.newsSetByUserAndName"
    })
	public void storeNewsSet(NewsSet set) {
        try {

            getHibernateTemplate().saveOrUpdate(set);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

    @Cacheable("HibernateNewsStore.newsSetByUserAndName")
	public NewsSet getNewsSet(String userId, String setName) {
        try {

            logger.debug("fetching news sets for " + userId);
            String query = "FROM NewsSet newsSet WHERE :userId = newsSet.userId AND " +
            		":setName = newsSet.name ORDER BY newsSet.name";

	        Query q = this.getSession().createQuery(query);
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

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

}
