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

package org.jasig.portlet.newsreader.hibernate;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.jasig.portlet.newsreader.spring.PortletApplicationContextLocator;
import org.springframework.context.ApplicationContext;

/**
 * When the hibernate3-maven-plugin:hbm2ddl goal is executed, this class
 * provides connections from the Spring ApplicationContext, which is capable of
 * using encrypted database connection settings (in datasource.properties).
 *
 * @author drewwills
 */
public class ApplicationContextConnectionProvider extends DatasourceConnectionProviderImpl {

    private static final String DATA_SOURCE_BEAN_NAME = "dataSource";

    private ApplicationContext context;

    private final Logger logger = Logger.getLogger(getClass());

    public ApplicationContextConnectionProvider() {
        try {
            context = PortletApplicationContextLocator.getApplicationContext(PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION);
        } catch (Exception e) {
            logger.error("Unable to load the application context from " + PortletApplicationContextLocator.DATABASE_CONTEXT_LOCATION, e);
        }
        final DataSource dataSource = context.getBean(DATA_SOURCE_BEAN_NAME, DataSource.class);
        setDataSource(dataSource);
    }

}
