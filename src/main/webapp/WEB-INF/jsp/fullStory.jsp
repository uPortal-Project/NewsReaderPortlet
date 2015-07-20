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
    
<div class="org-jasig-portlet-newsreader">
    <div id="${n}">
        <div class="news-reader-feed-list portlet ptl-newsreader view-news">
            <c:choose>
                <c:when test="${ feedView == 'select' }">
                    <select class="news-feeds-container">
                    <c:forEach var="feed" items="${feeds}">
                        <c:choose>
                            <c:when test="${activeFeed == feed.id}">
                        <option value="${feed.id}" selected="selected">${feed.name}</option>
                            </c:when>
                            <c:otherwise>
                        <option value="${feed.id}">${feed.name}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    </select>
                </c:when>
                <c:otherwise>
                    <ul class="news-feeds-container">
                    <c:forEach var="feed" items="${feeds}">
                        <li><a href="#${n}feed${feed.id}">${feed.name}</a></li>
                    </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
	<div class="titlebar portlet-titlebar">
        <h3 class="title story-title">${storyTitle}</h3>
		<a style="text-decoration:none;" href="<portlet:renderURL/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle;"> <spring:message code="back.list"/></a>
	</div>
    <br />

	${fullStory}

    <br />
	<p>
		<a style="text-decoration:none;" href="<portlet:renderURL/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle;"> <spring:message code="back.list"/></a>
	</p>
    <p>
        <c:if test="${supportsHelp}">
            <a href="<portlet:renderURL portletMode='help'/>"><spring:message code="help" /></a>
        </c:if>
        <c:if test="${supportsEdit && !isGuest}">
            &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode='edit'/>"><spring:message code="edit.news.feed" /></a>
        </c:if>
        <c:if test="${isAdmin}">
            &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>"><spring:message code="administration" /></a>
        </c:if>
    </p>
</div>

<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>
<script type="text/javascript"><rs:compressJs>
    ${n}.jQuery(function() {
       $ = ${n}.jQuery;

       if (${ feedView  == 'select' }) {
            // event handler for select menu
            $("#${n} select").change(function () {
                var id = $(this).val();
                alert("Selected = " + id);
                /***** jump to new feed *******/
            });
       } else {
            // compute the index of the currently selected feed
            var index = $("#${n} .news-feeds-container").index($("#${n}feed" + ${activeFeed}));
            // initialize the jQueryUI tabs widget and set the initially
            // selected tab
            $("#${n} .view-news").tabs({
                select: function (event, ui) {
                    var id = ui.panel.id.split("feed")[1];
                    $(newsView.feedListView).trigger("feedSelected", id);
                },
                selected: index
            });
            // Fix focus on active tab : up to top of page
            $('html,body').animate({scrollTop: $("#portal").offset().top},'500');
       }
    });

</rs:compressJs></script>