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

@Configuration
public class NewsReaderBeans {

	@Bean
	public ConnectionSocketFactory sslSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		// https://stackoverflow.com/questions/39762760/javax-net-ssl-sslexception-certificate-doesnt-match-any-of-the-subject-alterna
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
			     SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
			        NoopHostnameVerifier.INSTANCE);
		return scsf;
	}
}
