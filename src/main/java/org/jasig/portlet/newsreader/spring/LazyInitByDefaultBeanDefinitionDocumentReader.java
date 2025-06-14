/*
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
package org.jasig.portlet.newsreader.spring;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

/**
 * Extends the Spring DefaultBeanDefinitionDocumentReader to set the {@link org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#DEFAULT_LAZY_INIT_ATTRIBUTE}
 * to true, usefull when loading a context during testing or with command line tools.
 *
 * @author Eric Dalquist
 * @version $Revision$
 * @since 5.1.1
 */
public class LazyInitByDefaultBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {
    /** {@inheritDoc} */
    @Override
    protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
        root.setAttribute(BeanDefinitionParserDelegate.DEFAULT_LAZY_INIT_ATTRIBUTE, "true");
        return super.createDelegate(readerContext, root, parentDelegate);
    }
}
