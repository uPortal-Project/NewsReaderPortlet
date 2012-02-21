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

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.6.1/jquery-1.6.1.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>"></script>

<style type="text/css">
	table.edit-news { width: 100%; }
	table.edit-news td { font-size: 1.1em; }
	table.edit-news td.instruction { color: #666; font-size: 1em; text-align: center; vertical-align: bottom; }
</style>

<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);
    
    ${n}.jQuery(function(){

        var $ = ${n}.jQuery;
    
        var savePrefUrl = '<portlet:resourceURL/>';
    
        $('#${n}maxStories').change(function(){
            $.post(savePrefUrl, { prefName: 'maxStories', prefValue: $('#${n}maxStories').val() }, null, 'json');
        });
        $('#${n}feedView').change(function(){
            $.post(savePrefUrl, { prefName: 'feedView', prefValue: $('#${n}feedView').val() }, null, 'json');
        });
        $('#${n}storyView').change(function(){
            $.post(savePrefUrl, { prefName: 'storyView', prefValue: $('#${n}storyView').val() }, null, 'json');
        });
        $("#${n}newWindow").change(function(e){
        	$.post(savePrefUrl, { prefName: 'newWindow', prefValue: $(e.target).attr("checked") }, null, 'json');
        });

    });
</rs:compressJs></script>
    
    
<portlet:actionURL var="postUrl"></portlet:actionURL>

<table class="edit-news">
	<tr>
		<td colspan="2"><h3>My News</h3></td>
    	<c:choose>
    		<c:when test="${ not empty model.myNewsConfigurations }">
        		<td class="instruction">Edit</td>
        		<td class="instruction">Delete</td>
        		<td class="instruction">Displayed</td>
        	</c:when>
        	<c:otherwise>
        		<td colspan="3">&nbsp;</td>
        	</c:otherwise>
    	</c:choose>
	</tr>
	<c:forEach items="${ model.myNewsConfigurations }" var="feed">
    	<tr>
    		<td style="width: 7px;">&nbsp;</td>
    		<td>${ feed.newsDefinition.name }</td>
    		<td class="instruction">
    			<a href="<portlet:renderURL><portlet:param name="action" value="editUrl"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>"
    					title="Edit feed">
    				<img alt="edit" src="<c:url value="/images/pencil.png"/>"/>
    			</a>
    		</td>
    		<td class="instruction">
    			<a href="<portlet:actionURL><portlet:param name="actionCode" value="delete"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>"
    					title="Delete feed">
    				<img alt="delete" src="<c:url value="/images/rss_delete.png"/>"/>
    			</a>
    		</td>
    		<td class="instruction">
    			<c:choose>
    				<c:when test="${ feed.displayed }">
    					<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="hide"/>
    						<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="Hide feed">
	        				<img alt="show" src="<c:url value="/images/select-active.gif"/>"/>
	        			</a>
    				</c:when>
    				<c:otherwise>
						 <portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/><portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="Show feed">
	        				<img alt="show" src="<c:url value="/images/select-inactive.gif"/>"/>
	        			</a>
					</c:otherwise>
    			</c:choose>
    		</td>
    	</tr>
	</c:forEach>
	<tr>
		<td width="7px;">&nbsp;</td>
		<td colspan="4" style="padding-top: 10px; padding-bottom: 15px; padding-left:5px;">
	        	<a href="<portlet:renderURL><portlet:param name="action" value="editUrl"/></portlet:renderURL>"><img src="<c:url value="/images/rss_add.png"/>" style="vertical-align: middle"/> add a news feed</a>
	        	<br/>
		</td>
	</tr>
	<tr>
		<td colspan="2"><h3>Built-in news feeds</h3></td>
		<td class="instruction">Edit</td>
		<td class="instruction">&nbsp;</td>
		<td class="instruction">Displayed</td>
	</tr>
	<c:forEach items="${ model.predefinedNewsConfigurations }" var="feed">
    	<tr>
    		<td>&nbsp;</td>
    		<td>${ feed.newsDefinition.name }</td>
    		<td class="instruction">
    			<c:set var="editAction" value="${ model.predefinedEditActions[feed.newsDefinition.className] }"/>
				<c:choose>
					<c:when test="${ not empty editAction }">
	        			<a href="<portlet:renderURL><portlet:param name="action" value="${ editAction }"/>
	        					<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>"
	        					title="Edit feed">
        					<img alt="edit" src="<c:url value="/images/pencil.png"/>"/>
    					</a>
					</c:when>
					<c:otherwise>&nbsp;</c:otherwise>
				</c:choose>
    		</td>
    		<td>&nbsp;</td>
    		<td class="instruction">
    			<c:choose>
    				<c:when test="${ feed.displayed }">
    					<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="hide"/>
    						<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="Hide feed">
	        				<img alt="show" src="<c:url value="/images/select-active.gif"/>"/>
	        			</a>
    				</c:when>
    				<c:otherwise>
						 <portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/>
						 	<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="Show feed">
	        				<img alt="show" src="<c:url value="/images/select-inactive.gif"/>"/>
	        			</a>
					</c:otherwise>
    			</c:choose>
    		</td>
    	</tr>
	</c:forEach>
	<c:forEach items="${ model.hiddenFeeds }" var="feed">
		<tr>
			<td>&nbsp;</td>
    		<td>${ feed.name }</td>
    		<td>&nbsp;</td>
    		<td>&nbsp;</td>
    		<td class="instruction">
				<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="showNew"/><portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
    			<a href="${ displayURL }" title="Show feed">
    				<img alt="show" src="<c:url value="/images/select-inactive.gif"/>"/>
    			</a>
    		</td>
		</tr>
	</c:forEach>
</table>

<h3 style="margin-left: 7px;">Preferences</h3>

<div style="margin-left: 25px">
    <p>
        <label for="${n}maxStories">Maximum number of stories to display per feed</label>
        <c:set var="maxStories" value="${renderRequest.preferences.map['maxStories'][0]}"/>
        <select id="${n}maxStories">
            <c:forTokens items="5,10,15,20" delims="," var="item">
                <option ${ maxStories == item ? 'selected="selected"' : '' }>${item}</option>
            </c:forTokens>
        </select>
    </p>

    <p>
        <label for="${n}feedView">Display my feeds as</label>
        <c:set var="feedView" value="${renderRequest.preferences.map['feedView'][0]}"/>
        <select id="${n}feedView">
            <option value="tabs" ${ feedView == 'tabs' ? 'selected="selected"' : '' }>tabs</option>
            <option value="select" ${ feedView == 'select' ? 'selected="selected"' : '' }>a select menu</option>
        </select>
    </p>

    <p>
        <label for="${n}storyView">Display my stories as</label>
        <c:set var="storyView" value="${renderRequest.preferences.map['storyView'][0]}"/>
        <select id="${n}storyView">
            <option value="flyout-list" ${ storyView == 'flyout-list' ? 'selected="selected"' : '' }>a list with flyouts</option>
            <option value="scroll-summaries" ${ storyView == 'scroll-summaries' ? 'selected="selected"' : '' }>scrolling div</option>
        </select>
    </p>
    
    <p>
        <label for="${n}newWindow">Open stories in new windows</label>
        <c:set var="newWindow" value="${renderRequest.preferences.map['newWindow'][0]}"/>
        <input type="checkbox" id="${n}newWindow" ${ newWindow == "true" ? "checked='checked'" : ""} />
    </p>
</div>

<p><a href="<portlet:renderURL portletMode="help"/>">Need help?</a></p>

<br />
<hr />
<p>
	<a href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle"> Return to news feeds</a>
</p>
        
