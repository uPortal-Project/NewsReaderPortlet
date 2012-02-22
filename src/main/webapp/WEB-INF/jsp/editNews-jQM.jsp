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
<portlet:actionURL var="hideUrl" escapeXml="false"><portlet:param name="actionCode" value="hide"/>
    <portlet:param name="id" value="ID"/></portlet:actionURL>
<portlet:actionURL var="showUrl" escapeXml="false"><portlet:param name="actionCode" value="show"/>
    <portlet:param name="id" value="ID"/></portlet:actionURL>
<portlet:actionURL var="newUrl" escapeXml="false"><portlet:param name="actionCode" value="showNew"/>
    <portlet:param name="id" value="ID"/></portlet:actionURL>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.6.1/jquery-1.6.1.min.js"/>"></script>

<div class="portlet ptl-newsreader view-editnews">

    <div data-role="header" class="titlebar portlet-titlebar">
        <a href="<portlet:renderURL portletMode="view"/>" data-role="button" data-icon="back" data-inline="true">Back</a>
        <h2>Preferences</h2>
    </div>

	<div id="${n}" class="portlet-content" data-role="content">
	    <div data-role="fieldcontain">
	        <fieldset data-role="controlgroup">
	        	<legend>Which feeds should be displayed?</legend>
                <c:set var="count" value="0"/>
	            <c:forEach items="${ model.predefinedNewsConfigurations }" var="feed" varStatus="status">
	                <input type="checkbox" name="${ feed.id }" id="${n}${ count }" ${ feed.displayed ? 'checked' : '' } />
	                <label feedId="${ feed.id }" included="${ feed.displayed }" for="${n}${ count }">${ feed.newsDefinition.name }</label>
                    <c:set var="count" value="${ count+1 }"/>
	            </c:forEach>
                <c:forEach items="${ model.hiddenFeeds }" var="feed">
                    <input type="checkbox" name="${ feed.id }" id="${n}${ count }" />
                    <label feedId="${ feed.id }" included="new" for="${n}${ count }">${ feed.name }</label>
                    <c:set var="count" value="${ count+1 }"/>
                </c:forEach>
	        </fieldset>
	    </div>
	</div>
    
</div>
<script type="text/javascript"><rs:compressJs>
    var newsReaderPortlet = newsReaderPortlet || {};
    newsReaderPortlet.jQuery = jQuery.noConflict(true);
    newsReaderPortlet.jQuery(function(){
        var $ = newsReaderPortlet.jQuery;
        var newUrl = '${ newUrl }';
        var showUrl = '${ showUrl }';
        var hideUrl = '${ hideUrl }';

        var updateNewsItem = function () {
            var link, url, included;
            link = $(this);
            included = link.attr("included");
            if (included == 'new') {
                url = newUrl;
            } else if (included == 'true') {
                url = hideUrl;
            } else {
                url = showUrl;
            }
            window.location = url.replace('ID', link.attr("feedId"));
        };
        
        $(document).ready(function () {
            $("#${n} label").click(updateNewsItem);
            $("#${n} label").live('touchstart', updateNewsItem);
        });
    });
</rs:compressJs></script>

