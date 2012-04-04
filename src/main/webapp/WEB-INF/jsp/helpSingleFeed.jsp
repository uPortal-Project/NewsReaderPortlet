<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

<p>The news feeds area allows you to subscribe to single news feeds, specified as part of the preferences.</p>

<p>News feeds are commonly called RSS Feeds.</p>

<h3>Editing the news feed</h3>

<p>Click on &quot;Edit Preferences&quot; and the area will enter edit mode.</p>

<h3>Adding a news feed</h3>

<p>Whenever you are visiting a website that has either an <img src="<c:url value="/images/rss1.gif"/>" alt="RSS Logo"> or <img src="<c:url value="/images/rss2.gif"/>" alt="RSS Logo"> icon displayed you can subscribe to a news feed.</p>

<p>After clicking on the feed icon, copy the URL from the address bar in your browser into &quot;URL&quot; text box of the &quot;Edit Preferences&quot; area. Enter a name into the 
&quot;News feed name&quot; text area and press the submit button to save your news feed.</p>

<p>
	<a href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle"> Return to news feeds</a>
</p>
