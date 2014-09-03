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
<c:set var="max" value="${ prefs.maxStories > fn:length(feed.entries) ? fn:length(feed.entries)-1 : prefs.maxStories-1 }"/>
<c:if test="${max < 0}">	
	<c:set var="max" value="0"/>
</c:if>

<style>
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
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
    
<div class="org-jasig-portlet-newsreader">
	
	
    <div id="${n}newsContainer">
    	<c:if test="${prefs.showTitle}">
       		<h3><a href="${ feed.link }" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ feed.title }</a></h3>
       	</c:if>
        <div class="news-items-container">
            <c:choose>
                <c:when test="${ prefs.summaryView == 'titleAndAbstract' }">
                    <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
                        <h3>
                        	<c:choose>
	                        	<c:when test="${!empty entry.link }">
	                            	<a class="news-item" href="${ entry.link }" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ entry.title }</a>
	                            </c:when>
	                            <c:otherwise>
	                            	<span class="news-item">${ entry.title }</span>
	                            </c:otherwise>
                            </c:choose>
                        </h3>
                        <p>${ entry.description }</p>
                    </c:forEach>
                </c:when>
                <c:when test="${ prefs.summaryView == 'flyout' }">
                	<ul class="news-list">
                        <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
                            <li>
								<a class="news-item" href="${ entry.link }" title="${ entry.description }" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ entry.title }</a>
                                <span style="display:none">${ entry.description }</span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <ul class="news-list">
                        <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
                            <li>
                            	<c:choose>
		                        	<c:when test="${!empty entry.link }">
		                            	<a class="news-item" href="${ entry.link }" title="${ entry.description }" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ entry.title }</a>
		                            </c:when>
		                            <c:otherwise>
		                            	<span class="news-item" title="${ entry.description }">${ entry.title }</span>
		                            </c:otherwise>
                            	</c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <br />
	
	<c:if test='${not empty message}'>
		<div class="portlet-msg-error portlet-msg error">${message}</div><br/>
	</c:if>
	 
    <c:if test="${supportsEdit && !isGuest}">
        <a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="render" /></portlet:renderURL>"/><spring:message code="viewSingleFeed.editpreferences"/></a>
    </c:if>
</div>

<c:if test="${ prefs.summaryView == 'flyout' }">
    <c:if test="${!usePortalJsLibs}">
        <script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>"></script>
        <script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.10.3/jquery-ui-1.10.3.min.js"/>"></script>
        <script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.5.0/js/fluid-custom.min.js"/>"></script>
    </c:if>
    <script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
            ${n}.fluid = fluid;
            fluid = null;
            fluid_1_4 = null;
            fluid_1_5 = null;
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
            ${n}.fluid = ${ ns }fluid;
        </c:otherwise>
    </c:choose>

        ${n}.jQuery('#${n}newsContainer .news-item').tooltip({
            content: function() {
                return ${n}.jQuery(this).next().html();
            },
            position: {
                my: 'left center',
                at: 'right+10 center',
                collision: 'flipfit'
            }
        });
    </rs:compressJs></script>
</c:if>