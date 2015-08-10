<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
    <link href="<c:url value="/css/newsreader.css"/>" rel="stylesheet" type="text/css" />
<c:set var="n"><portlet:namespace/></c:set>
<%--
  -- Attache jQuery and Handlebars to ${n}.
  -- Similar to scripts.js but without initializing "upnews"
  --%>
<rs:aggregatedResources path="skin${ mobile ? '-mobile' : '' }${ usePortalJsLibs ? '-shared' : '' }.xml"/>
<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
            ${n}.Handlebars = Handlebars;
            Handlebars.noConflict();
            fluid = null;
            fluid_1_5 = null;
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
            ${n}.Handlebars = Handlebars;
            Handlebars.noConflict();
        </c:otherwise>
    </c:choose>
</rs:compressJs></script>

<div class="org-jasig-portlet-newsreader">
    <div id="${n}">
        <div class="news-reader-feed-list portlet ptl-newsreader view-news">
            <c:choose>
                <c:when test="${ feedView == 'select' }">
                    <select class="news-feeds-container"></select>
                </c:when>
                <c:otherwise>
                    <ul class="news-feeds-container"></ul>
                </c:otherwise>
            </c:choose>
            <br />
            <div class="titlebar portlet-titlebar">
                <h3 class="title story-title">${storyTitle}</h3>
                <a style="text-decoration:none;" href="<portlet:renderURL/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle;"> <spring:message code="back.list"/></a>
            </div>
            <br />
            <div id="${n}full-story">
            <c:choose>
                <c:when test="${ feedView == 'select' }">
                    ${fullStory}
                </c:when>
                <c:otherwise>
                    <c:forEach var="feed" items="${feeds}">
                        <c:choose>
                            <c:when test="${feed.id == activeFeed}">
                                <div id="${n}feed${feed.id}">${fullStory}</div>
                            </c:when>
                            <c:otherwise>
                                <div id="${n}feed${feed.id}"></div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
        <div class="news-footer">
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
    </div>
</div>

<script type="text/template" id="${n}feed-list-template">
    {{!-- populate either the dropdown options or list items for feeds --}}
    {{#each this}}
    <c:choose>
        <c:when test="${ feedView == 'select' }">
            <option value="{{id}}">{{name}}</option>
        </c:when>
        <c:otherwise>
            <li id="${n}feed{{id}}-tab"><a href="#${n}feed{{id}}">{{name}}</a></li>
        </c:otherwise>
    </c:choose>
    {{/each}}
</script>

<script type="text/javascript">
(function() {

    var $ = ${n}.jQuery;

    /* Apply feed template to dropdown/tabs */
    var source = $("#${n}feed-list-template").html();
    var template = ${n}.Handlebars.compile(source);
    $("#${n} .news-feeds-container").html(template(${feeds}));

    if (${ feedView == 'select' }) {
        // Select current feed in dropdown
        $("#${n} option").removeAttr("selected");
        $("#${n} option[value=" + ${activeFeed} + "]").attr("selected", "selected");

        // Set up change trigger for dropdown
        $("#${n} select").change(function () {
            var id = $(this).val();
            /***** jump to new feed *******/
            <portlet:renderURL var="newsListUrl">
            </portlet:renderURL>
            var url = "${newsListUrl}" + "&pP_activeFeed=" + id;
            window.location = url;
        });
    } else {
        // initialize the jQueryUI tabs widget and set the initially selected tab
        var index = $("#${n}feed${activeFeed}-tab").index();
        $("#${n} .view-news").tabs({
            activate: function (event, ui) {
                var id = ui.newPanel[0].id.split("feed")[1];
                var url = "${newsListUrl}" + "&pP_activeFeed=" + id;
                window.location = url;
            },
            active: index
        });
    }

}());
</script>
