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
            fluid = null;
            fluid_1_5 = null;
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
            ${n}.Handlebars = Handlebars;
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
        </div>
    </div>
    <div class="titlebar portlet-titlebar">
        <h3 class="title story-title">${storyTitle}</h3>
        <a style="text-decoration:none;" href="<portlet:renderURL/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle;"> <spring:message code="back.list"/></a>
    </div>
<div id="${n}feeds{activeFeed}">
    ${fullStory}
</div>
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

<script type="text/template" id="${n}feed-list-template">
    {{!-- populate either the dropdown options or list items for feeds --}}
    {{#each this}}
    <c:choose>
        <c:when test="${ feedView == 'select' }">
            <option value="{{id}}">{{name}} {{id}}</option>
        </c:when>
        <c:otherwise>
            <li><a href="#${n}feed{{id}}">{{name}}</a></li>
        </c:otherwise>
    </c:choose>
    {{/each}}
</script>

<script type="text/javascript">
    var $ = ${n}.jQuery;

    /* Apply feed template to dropdown/tabs */
    var source = $("#${n}feed-list-template").html();
    var template = ${n}.Handlebars.compile(source);
    $("#${n} .news-feeds-container").html(template(${feeds}));

    /* Select current feed in dropdown/tabs */
    if (${ feedView == 'select' }) {
        $("#${n} option").removeAttr("selected");
        $("#${n} option[value=" + ${activeFeed} + "]").attr("selected", "selected");
    } else {
    }

    /* Set up event handlers for dropdown/tabs */
    if (${ feedView == 'select' }) {
        $("#${n} select").change(function () {
            var id = $(this).val();
            /***** jump to new feed *******/
            <portlet:renderURL var="newsListUrl">
            </portlet:renderURL>
            var url = "${newsListUrl}" + "&pP_activeFeed=" + id;
            alert(url);
            window.location = url;
        });
    } else {
    }
</script>

<script type="text/javascript"><rs:compressJs>
    ${n}.jQuery(function() {
       var $ = ${n}.jQuery;

       if (${ feedView  == 'select' }) {
       } else {
            // compute the index of the currently selected feed
            var index = $("#${n} .news-stories-container").index($("#${n}feed" + ${activeFeed}));
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