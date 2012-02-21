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
<c:set var="n"><portlet:namespace/></c:set>
<portlet:resourceURL var="feedUrl"/>

<div id="${n}">
    <div class="news-reader-feed-list portlet ptl-newsreader view-news">
        <div class="news-feeds-container">
            <div data-role="header" data-backbtn="false" class="titlebar portlet-titlebar">
                <h1 class="title">News</h1>
            </div>
            <div data-role="content" class="portlet-content">
                <ul class="news-feeds" data-role="listview">
                    <li class="news-feed">
                        <a href="javascript:;" class="news-feed-link">
                            <h3 class="news-feed-title"></h3>
                        </a>
                    </li>
                </ul>
                <c:if test="${supportsEdit && !isGuest}">
                    <div class="utilities">
                        <a data-role="button" href="<portlet:renderURL portletMode='edit'/>">Preferences</a>
                    </div>
                </c:if>            
            </div>
        </div>
        <div class="news-stories-container" style="display:none">
            <div data-role="header" class="titlebar portlet-titlebar">
                <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true">Back</a>
                <h1 class="title news-feed-title">Feed Title</h1>
            </div>
            <div data-role="content" class="portlet-content">
                <ul class="news-stories feed" data-role="listview">
                    <li class="news-story">
                        <a href="javascript:;" class="news-story-link">
                            <h3 class="news-story-title"></h3>
                            <p class="news-story-summary"></p>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="story-container" style="display:none">
            <div data-role="header" class="titlebar portlet-titlebar">
                <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true">Back</a>
                <h1 class="title story-title">Story Title</h1>
            </div>
            <div data-role="content" class="portlet-content">
                <div class="story-content">
                </div>
            </div>
        </div>
    </div>
</div>

<rs:aggregatedResources path="${ usePortalJsLibs ? '/skin-mobile-shared.xml' : '/skin-mobile.xml' }"/>
<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
            ${n}.fluid = fluid;
            fluid = null; 
            fluid_1_4 = null;
        </c:when>
        <c:otherwise>
            ${n}.jQuery = ${ portalJsNamespace }${not empty portalJsNamespace ? '.' : ''}jQuery;
            ${n}.fluid = ${ portalJsNamespace }${not empty portalJsNamespace ? '.' : ''}fluid;
        </c:otherwise>
    </c:choose>
    if (!news.initialized) news.init(${n}.jQuery, ${n}.fluid);
    ${n}.news = news;

    ${n}.jQuery(function(){
        var $ = ${n}.jQuery;
        var fluid = ${n}.fluid;
        
        $(document).ready(function () {
            var reader = ${n}.news.reader($("#${n}"), { url: "${feedUrl}" });
        });
    });
</rs:compressJs></script>
