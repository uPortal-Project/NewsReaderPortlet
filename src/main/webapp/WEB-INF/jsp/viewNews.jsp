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

<style>
    ul.news-stories li { padding-bottom:0.5em; list-style-image:url('<rs:resourceURL value="/rs/famfamfam/silk/1.3/bullet_feed.png"/>');  }
    .ui-tooltip {
        padding:8px;
        position:absolute;
        z-index:9999;
        -o-box-shadow: 0 0 5px #aaa;
        -moz-box-shadow: 0 0 5px #aaa;
        -webkit-box-shadow: 0 0 5px #aaa;
        box-shadow: 0 0 5px #aaa;
        max-width: 400px;
    }
    * html .ui-tooltip { background-image: none; }
    body .ui-tooltip { border-width:2px; }
</style>
<portlet:resourceURL var="feedUrl"/>
    
<div class="org-jasig-portlet-newsreader">

    <div id="${n}">
        <div class="news-reader-feed-list portlet ptl-newsreader view-news">
            <div class="news-feeds-container">
                <div>
                    <select class="news-feed-select">
                    </select>
                </div>
            </div>
            <div class="news-stories-container" style="display:none;">
                <div class="titlebar portlet-titlebar">
                    <h2 class="title news-feed-title">Feed Title</h2>
                </div>
                <div data-role="content" class="portlet-content">
                    <c:choose>
                        <c:when test="${ storyView == 'flyout' }">
                            <ul class="news-stories feed">
                                <li class="news-story">
                                    <a href="javascript:;" class="news-story-link" title="">
                                        <span class="news-story-title"><span>
                                    </a>
                                </li>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <div class="news-stories feed">
                                <div class="news-story">
                                    <h3>
                                        <a href="javascript:;" class="news-story-link" title="">
                                            <span class="news-story-title"><span>
                                        </a>
                                    </h3>
                                    <p class="news-story-summary"></p>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="story-container" style="display:none">
                <div class="titlebar portlet-titlebar">
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
       
    <br/>
    <p>
        <c:if test="${supportsHelp}">
            <a href="<portlet:renderURL portletMode='help'/>">Help</a>
        </c:if>
        <c:if test="${supportsEdit && !isGuest}">
            &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>
        </c:if>
        <c:if test="${isAdmin}">
                &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
                    News Administration</a>
        </c:if>
    </p>
</div>

<rs:aggregatedResources path="${ usePortalJsLibs ? '/skin-shared.xml' : '/skin.xml' }"/>
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
            var reader = ${n}.news.reader($("#${n}"), { url: "${feedUrl}", useFlyouts: ${ storyView == 'flyout' } });
        });
    });
</rs:compressJs></script>

