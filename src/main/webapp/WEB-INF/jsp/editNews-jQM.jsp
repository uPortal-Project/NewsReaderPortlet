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
<portlet:actionURL var="hideUrl"><portlet:param name="actionCode" value="hide"/>
    <portlet:param name="id" value="ID"/></portlet:actionURL>
<portlet:actionURL var="showUrl"><portlet:param name="actionCode" value="show"/>
    <portlet:param name="id" value="ID"/></portlet:actionURL>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.5/jquery-1.5.min.js"/>"></script>

<div class="portlet ptl-newsreader view-editnews">
	<div id="${n}" class="portlet-content" data-role="content">
	    <div data-role="fieldcontain">
	        <fieldset data-role="controlgroup">
	        	<legend>Which feeds should be displayed?</legend>
	            <c:forEach items="${ model.predefinedNewsConfigurations }" var="feed">
	                <input type="checkbox" name="${ feed.id }" id="${n}${ feed.id }" ${ feed.displayed ? 'checked' : '' } />
	                <label feedId="${ feed.id }" included="${ feed.displayed }" for="${n}${ feed.id }">${ feed.newsDefinition.name }</label>
	            </c:forEach>
	        </fieldset>
	    </div>
	    
	    <div class="utilities">
	        <a data-role="button" href="<portlet:renderURL portletMode="view"/>">Done</a>
	    </div>
	</div>
</div>
<script type="text/javascript"><rs:compressJs>
    var newsReaderPortlet = newsReaderPortlet || {};
    newsReaderPortlet.jQuery = jQuery.noConflict(true);
    newsReaderPortlet.jQuery(function(){
        var $ = newsReaderPortlet.jQuery;
        var showUrl = '${ showUrl }';
        var hideUrl = '${ hideUrl }';

        var updateNewsItem = function () {
            var link, url;
            link = $(this);
            url = (link.attr("included") == 'true') ? hideUrl : showUrl;
            window.location = url.replace('ID', link.attr("feedId")).replace('&amp;', '&');
        };
        
        $(document).ready(function () {
            $("#${n} label").click(updateNewsItem);
            $("#${n} label").live('touchstart', updateNewsItem);
        });
    });
</rs:compressJs></script>

