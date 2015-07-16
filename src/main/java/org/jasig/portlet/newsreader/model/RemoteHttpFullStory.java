package org.jasig.portlet.newsreader.model;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jasig.portlet.newsreader.adapter.NewsException;

public class RemoteHttpFullStory implements FullStory {
	 
	protected final Log log = LogFactory.getLog(getClass());
	private String remoteHttpUrl;

	public RemoteHttpFullStory(String remoteHttpUrl) {
		super();
		this.remoteHttpUrl = remoteHttpUrl;
	}

	@Override
	public String getFullStory() {
		try  {
		return fetchRemoteContent();
		}
		catch (Exception ex) {
			throw new NewsException("Failed to fetch FullStory content from "+remoteHttpUrl,ex);
		}
	}

	private String fetchRemoteContent() throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet(remoteHttpUrl);

			log.debug("executing request " + httpget.getURI());

			// Create a response handler
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody;
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

}
