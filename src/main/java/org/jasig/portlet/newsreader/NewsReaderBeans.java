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
package org.jasig.portlet.newsreader;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>NewsReaderBeans class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
@Configuration
public class NewsReaderBeans {

	/**
	 * <p>sslSocketFactory.</p>
	 *
	 * @return a {@link org.apache.http.conn.socket.ConnectionSocketFactory} object
	 * @throws java.security.KeyManagementException if any.
	 * @throws java.security.NoSuchAlgorithmException if any.
	 * @throws java.security.KeyStoreException if any.
	 */
	@Bean
	public ConnectionSocketFactory sslSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		// https://stackoverflow.com/questions/39762760/javax-net-ssl-sslexception-certificate-doesnt-match-any-of-the-subject-alterna
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
			     SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
			        NoopHostnameVerifier.INSTANCE);
		return scsf;
	}
}
