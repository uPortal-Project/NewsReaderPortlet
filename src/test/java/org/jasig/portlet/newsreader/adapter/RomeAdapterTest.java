package org.jasig.portlet.newsreader.adapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.sun.syndication.io.FeedException;

public class RomeAdapterTest {
    private static final String ERROR_GETTING_FEED = "Error Getting Feed";
    private static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "httpClientConnectionTimeout";
    private static final String HTTP_CLIENT_SOCKET_TIMEOUT = "httpClientSocketTimeout";
    private static final int DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT = 10000;
    
    @Mock private NewsConfiguration config;
    @Mock private PortletRequest request;
    @Mock private PortletPreferences portletPreferences;
    @Mock private NewsDefinition newsDefinition;
    @Mock private HttpClient httpClient;
    @Mock private HttpResponse httpResponse;
    @Mock private StatusLine statusLine;
    @Mock private HttpEntity httpEntity;
    @Mock private InputStream inputStream;
    @Mock private RomeNewsProcessorImpl processor;
    @Mock private NewsFeed newsFeed;
    
    private CacheConfiguration cacheConfiguration;
    private Cache cache;
    private RomeAdapter romeAdapter;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", "test");
        cacheConfiguration = new CacheConfiguration("test", 20);
        cache = new Cache(cacheConfiguration);
        cache.initialise();
        given(request.getPreferences()).willReturn(portletPreferences);
        given(portletPreferences.getValue(HTTP_CLIENT_CONNECTION_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT))).willReturn(String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT));
        given(portletPreferences.getValue(HTTP_CLIENT_SOCKET_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT))).willReturn(String.valueOf(DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT));
        given(config.getNewsDefinition()).willReturn(newsDefinition);
        given(newsDefinition.getParameters()).willReturn(params);
        given(httpClient.execute((HttpGet)anyObject())).willReturn(httpResponse);
        given(httpResponse.getStatusLine()).willReturn(statusLine);
        given(httpResponse.getEntity()).willReturn(httpEntity);
        given(httpEntity.getContent()).willReturn(inputStream);
        given(statusLine.getStatusCode()).willReturn(HttpStatus.SC_OK);
        given(processor.getFeed(eq(inputStream), anyString(), anyString())).willReturn(newsFeed);
        romeAdapter = new RomeAdapter();
        romeAdapter.setCache(cache);
        romeAdapter.setHttpClient(httpClient);
        romeAdapter.setProcessor(processor);
    }
    
    @After
    public void after() {
        cache.dispose();
    }

    @Test
    public void getSyndFeedNewsConfigurationPortletRequestReturnsCorrectNewsFeed() {
        NewsFeed expected = newsFeed;
        NewsFeed actual = romeAdapter.getSyndFeed(config, request);
        assertEquals(expected, actual);
    }

    @Test(expected=NewsException.class)
    public void getSyndFeedNewsConfigurationPortletRequestCatchesPolicyExceptionAndThrowsNewsException() throws Exception {
        given(processor.getFeed(eq(inputStream), anyString(), anyString())).willThrow(new PolicyException(ERROR_GETTING_FEED));
        romeAdapter.getSyndFeed(config, request);
    }
    
    @Test(expected=NewsException.class)
    public void getSyndFeedNewsConfigurationPortletRequestCatchesScanExceptionAndThrowsNewsException() throws Exception {
        given(processor.getFeed(eq(inputStream), anyString(), anyString())).willThrow(new ScanException(ERROR_GETTING_FEED));
        romeAdapter.getSyndFeed(config, request);
    }
    
    @Test(expected=NewsException.class)
    public void getSyndFeedNewsConfigurationPortletRequestCatchesIOExceptionAndThrowsNewsException() throws Exception {
        given(processor.getFeed(eq(inputStream), anyString(), anyString())).willThrow(new IOException(ERROR_GETTING_FEED));
        romeAdapter.getSyndFeed(config, request);
    }
    
    @Test(expected=NewsException.class)
    public void getSyndFeedNewsConfigurationPortletRequestCatchesFeedExceptionAndThrowsNewsException() throws Exception {
        given(processor.getFeed(eq(inputStream), anyString(), anyString())).willThrow(new FeedException(ERROR_GETTING_FEED));
        romeAdapter.getSyndFeed(config, request);
    }

}
