/*
Copyright (c) 2008, News Reader Portlet Development Team
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following
  disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
  disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of the News Reader Portlet Development Team nor the names of its contributors may be used to endorse or
  promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

    public void deleteNewsDefinition(NewsDefinition definition) {
        try {

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
