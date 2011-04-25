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
<c:set var="max" value="${ prefs.maxStories > fn:length(feed.entries) ? fn:length(feed.entries)-1 : prefs.maxStories-1 }"/>


<style>
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
</style>
    
<div class="org-jasig-portlet-newsreader">
    <div id="${n}newsContainer">
        <h2><a href="${ feed.link }" rel="popup" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ feed.title }</a></h2>
        <div class="news-items-container">
            <c:choose>
                <c:when test="${ prefs.summaryView == 'titleAndAbstract' }">
                    <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
                        <h3>
                            <a class="news-items" href="${ entry.link }" rel="popup" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ entry.title }</a>
                        </h3>
                        <p>${ entry.description.value }</p>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <ul class="news-list">
                        <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
                            <li>
                                <a class="news-item" href="${ entry.link }" rel="popup" ${ prefs.newWindow ? 'target="_blank"' : '' }>${ entry.title }</a>
                                <c:if test="${ prefs.summaryView == 'flyout' }"><span style="display:none">${ entry.description.value }</span></c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <br />

    <c:if test="${supportsEdit}">
        <a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="render" /></portlet:renderURL>"/>Edit Preferences</a>
    </c:if>
</div>

<c:if test="${ prefs.summaryView == 'flyout' }">
    <script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.5/jquery-1.5.min.js"/>"></script>
    <script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.8/jquery-ui-1.8.min.js"/>"></script>
    <script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.3/js/fluid-all-1.3.min.js"/>"></script>
    <script type="text/javascript">
        var ${n} = ${n} || {};
        ${n}.jQuery = jQuery.noConflict(true);
        ${n}.fluid = fluid;
        fluid = null;
        fluid_1_3 = null;

        ${n}.jQuery("#${n}newsContainer .news-item").tooltip({
            bodyHandler: function() { 
                return ${n}.jQuery(this).next().html(); 
            },
            showURL: false
        });
    </script>
</c:if>