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
package org.jasig.portlet.newsreader.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ThemeNameViewResolverImplTest {
	
	@Mock PortletPreferences preferences;
	@Mock PortletRequest request;
	ThemeNameViewResolverImpl resolver;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		resolver = spy(new ThemeNameViewResolverImpl());
		
		when(request.getPreferences()).thenReturn(preferences);
	}
	
	@Test
	public void testGetSingleFeedView() {
		when(preferences.getValue(AbstractViewResolver.VIEW_NAME_PREFERENCE, "viewSingleFeed")).thenReturn("viewSingleFeed");
		when(preferences.getValue(AbstractViewResolver.MOBILE_VIEW_NAME_PREFERENCE, "viewSingleFeed-jQM")).thenReturn("viewSingleFeed-jQM");

		doReturn(false).when(resolver).isMobile(request);
		assertEquals("viewSingleFeed", resolver.getSingleFeedView(request));
		
		when(preferences.getValue(AbstractViewResolver.VIEW_NAME_PREFERENCE, "viewSingleFeed")).thenReturn("videos");
		assertEquals("videos", resolver.getSingleFeedView(request));

		doReturn(true).when(resolver).isMobile(request);
		assertEquals("viewSingleFeed-jQM", resolver.getSingleFeedView(request));
		
		when(preferences.getValue(AbstractViewResolver.MOBILE_VIEW_NAME_PREFERENCE, "viewSingleFeed-jQM")).thenReturn("videos-jQM");
		assertEquals("videos-jQM", resolver.getSingleFeedView(request));

	}
	
	@Test
	public void testIsMobile() {
		when(preferences.getValues(ThemeNameViewResolverImpl.MOBILE_THEMES_KEY, ThemeNameViewResolverImpl.MOBILE_THEMES_DEFAULT)).thenReturn(ThemeNameViewResolverImpl.MOBILE_THEMES_DEFAULT);
		
		when(request.getProperty(ThemeNameViewResolverImpl.THEME_NAME_PROPERTY)).thenReturn("Universality");
		assertFalse(resolver.isMobile(request));
		
		when(request.getProperty(ThemeNameViewResolverImpl.THEME_NAME_PROPERTY)).thenReturn("UniversalityMobile");
		assertTrue(resolver.isMobile(request));
	}

}
