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
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.1.3/js/fluid-all-1.1.3.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/scripts/news-feed-view.min.js"/>"></script>

<style>
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
</style>
    
<c:set var="storyView">${renderRequest.preferences.map['storyView'][0]}</c:set>
<script type="text/javascript">
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);
    ${n}.fluid = fluid;
    fluid = null;
    fluid_1_1 = null;

    ${n}.jQuery(function() {

        var $ = ${n}.jQuery;

        var options = {
            url: '<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>',
            namespace: '${n}',
            feedView: "${renderRequest.preferences.map['feedView'][0]}",
            summaryView: "${prefs.summaryView}",
            newWindow: ${prefs.newWindow},
            scrolling: "${renderRequest.preferences.map['scrolling'][0]}"
        };
        newsreader.FeedView("#${n}newsContainer", options);

    });
</script>

<div class="org-jasig-portlet-newsreader">
    <div id="${n}newsContainer">Loading . . . </div>
    <br />

    <c:if test="${supportsEdit}">
        <a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="render" /></portlet:renderURL>"/>Edit Preferences</a>
    </c:if>
</div>
