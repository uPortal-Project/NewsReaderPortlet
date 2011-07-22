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

package org.jasig.portlet.newsreader.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.UserDefinedNewsDefinition;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * HibernateNewsStore provides a hibernate implementation of the NewsStore.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class HibernateNewsStore extends HibernateDaoSupport implements
        NewsStore {

    private static Log log = LogFactory.getLog(HibernateNewsStore.class);

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

            log.debug("fetching news configurations for " + subscribeId);
            return (List<NewsConfiguration>) getHibernateTemplate().find(
                    "from NewsConfiguration config where "
                            + "subscribeId = ? and displayed = true "
                            + "order by newsDefinition.name", subscribeId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {
        try {

            String query = "from NewsConfiguration config where "
                    + "config.newsSet.id = ? and "
                    + "config.class = UserDefinedNewsConfiguration "
                    + "order by newsDefinition.name";
            if (visibleOnly)
                query = query.concat(" and visibleOnly = true");

            return (List<UserDefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query, setId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(
            Long setId, boolean visibleOnly) {
        try {
            String query = "from NewsConfiguration config "
                    + "where config.newsSet.id = ? and "
                    + "config.class = PredefinedNewsConfiguration "
                    + "order by newsDefinition.name";
            if (visibleOnly)
                query = query.concat(" and visibleOnly = true");

            return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query, setId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations() {
        try {

            String query = "from NewsDefinition def "
                    + "where def.class = PredefinedNewsDefinition "
                    + "order by def.name";
            return (List<PredefinedNewsConfiguration>) getHibernateTemplate()
                    .find(query);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(Long setId, Set<String> roles) {
        try {

            String query = "from PredefinedNewsDefinition def "
                    + "where :setId not in (select config.newsSet.id "
                    + "from def.userConfigurations config) ";
            for (int i = 0; i < roles.size(); i++) {
                query = query.concat(
                        "and :role" + i + " not in elements(def.defaultRoles) ");
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
            // chance of getting predefined newss, so just go ahead
            // and return
            if (roles.isEmpty())
                return;

            String query = "from PredefinedNewsDefinition def "
                    + "left join fetch def.defaultRoles role where "
                    + ":setId not in (select config.newsSet.id "
                    + "from def.userConfigurations config)";
            if (roles.size() > 0)
                query = query.concat("and role in (:roles)");
            Query q = this.getSession().createQuery(query);
            q.setLong("setId", set.getId());
            if (roles.size() > 0)
                q.setParameterList("roles", roles);
            List<PredefinedNewsDefinition> defs = q.list();

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

            String query = "from PredefinedNewsDefinition def "
                    + "left join fetch def.defaultRoles role where "
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

            String query = "from PredefinedNewsDefinition def "
                    + "left join fetch def.defaultRoles role where "
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
            
            String query = "from NewsConfiguration config "
                + "where config.newsDefinition.id = ? and "
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

    public List<String> getUserRoles() {
        try {

            String query = "select distinct elements(def.defaultRoles) " +
                    "from PredefinedNewsDefinition def ";

            return (List<String>) getHibernateTemplate()
                    .find(query);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

	public NewsSet getNewsSet(Long id) {

        try {

            return (NewsSet) getHibernateTemplate().get(NewsSet.class, id);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

	}

	public List<NewsSet> getNewsSetsForUser(String userId) {
        try {

            log.debug("fetching news sets for " + userId);
            return (List<NewsSet>) getHibernateTemplate().find(
                    "from NewsSet newsSet where "
                            + "newsSet.userId = ? "
                            + "order by newsSet.name", userId);

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

	public void storeNewsSet(NewsSet set) {
        try {

            getHibernateTemplate().saveOrUpdate(set);
            getHibernateTemplate().flush();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

	public NewsSet getNewsSet(String userId, String setName) {
        try {

            log.debug("fetching news sets for " + userId);
            String query = "from NewsSet newsSet where :userId = newsSet.userId and " +
            		":setName = newsSet.name order by newsSet.name";

	        Query q = this.getSession().createQuery(query);
	        q.setString("userId", userId);
	        q.setString("setName", setName);
	        return (NewsSet) q.uniqueResult();

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}

}
