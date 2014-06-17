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

<style type="text/css">
	table.edit-news { width: 100%; }
	table.edit-news td { font-size: 1.1em; }
	table.edit-news td.instruction { color: #666; font-size: 1em; text-align: center; vertical-align: bottom; }
</style>

<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>
<script type="text/javascript"><rs:compressJs>
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
            $.post(savePrefUrl, { prefName: 'summaryView', prefValue: $('#${n}storyView').val() }, null, 'json');
        });
        $("#${n}newWindow").change(function(e){
        	$.post(savePrefUrl, { prefName: 'newWindow', prefValue: ($(e.target).attr("checked") == 'checked') }, null, 'json');
        });

    });
</rs:compressJs></script>
    
    
<portlet:actionURL var="postUrl"></portlet:actionURL>

<table class="edit-news">
	<tr>
		<td colspan="2"><h3><spring:message code="edit.title"/></h3></td>
    	<c:choose>
    		<c:when test="${ not empty model.myNewsConfigurations }">
        		<td class="instruction"><spring:message code="edit.news.edit"/></td>
        		<td class="instruction"><spring:message code="edit.news.delete"/></td>
        		<td class="instruction"><spring:message code="edit.news.displayed"/></td>
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
    					title="<spring:message code="edit.news.edit.title"/>">
    				<img alt="<spring:message code="edit.news.edit.alt"/>" src="<c:url value="/images/pencil.png"/>"/>
    			</a>
    		</td>
    		<td class="instruction">
    			<a href="<portlet:actionURL><portlet:param name="actionCode" value="delete"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>"
    					title="<spring:message code="edit.news.delete.title"/>">
    				<img alt="<spring:message code="edit.news.delete.alt"/>" src="<c:url value="/images/rss_delete.png"/>"/>
    			</a>
    		</td>
    		<td class="instruction">
    			<c:choose>
    				<c:when test="${ feed.displayed }">
    					<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="hide"/>
    						<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="<spring:message code="edit.news.hide.title"/>">
	        				<img alt="<spring:message code="edit.news.hide.alt"/>" src="<c:url value="/images/select-active.gif"/>"/>
	        			</a>
    				</c:when>
    				<c:otherwise>
						 <portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/><portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
	        				<img alt="<spring:message code="edit.news.show.alt"/>" src="<c:url value="/images/select-inactive.gif"/>"/>
	        			</a>
					</c:otherwise>
    			</c:choose>
    		</td>
    	</tr>
	</c:forEach>
	<tr>
		<td width="7px;">&nbsp;</td>
		<td colspan="4" style="padding-top: 10px; padding-bottom: 15px; padding-left:5px;">
	        	<a href="<portlet:renderURL><portlet:param name="action" value="editUrl"/></portlet:renderURL>"><img src="<c:url value="/images/rss_add.png"/>" style="vertical-align: middle"/> <spring:message code="edit.news.add"/></a>
	        	<br/>
		</td>
	</tr>
	<tr>
		<td colspan="2"><h3><spring:message code="edit.news.exist"/></h3></td>
		<td class="instruction"><spring:message code="edit.news.edit"/></td>
		<td class="instruction">&nbsp;</td>
		<td class="instruction"><spring:message code="edit.news.displayed"/></td>
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
	        					title="<spring:message code="edit.news.edit.title"/>">
        					<img alt="<spring:message code="edit.news.edit.alt"/>" src="<c:url value="/images/pencil.png"/>"/>
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
	        			<a href="${ displayURL }" title="<spring:message code="edit.news.hide.title"/>">
	        				<img alt="<spring:message code="edit.news.hide.alt"/>" src="<c:url value="/images/select-active.gif"/>"/>
	        			</a>
    				</c:when>
    				<c:otherwise>
						 <portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/>
						 	<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
	        			<a href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
	        				<img alt="<spring:message code="edit.news.show.alt"/>" src="<c:url value="/images/select-inactive.gif"/>"/>
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
    			<a href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
    				<img alt="<spring:message code="edit.news.show.alt"/>" src="<c:url value="/images/select-inactive.gif"/>"/>
    			</a>
    		</td>
		</tr>
	</c:forEach>
</table>

<h3 style="margin-left: 7px;"><spring:message code="edit.news.pref"/></h3>

<div style="margin-left: 25px">
    <p>
        <label for="${n}maxStories"><spring:message code="edit.news.maxstories"/></label>
        <c:set var="maxStories" value="${renderRequest.preferences.map['maxStories'][0]}"/>
        <select id="${n}maxStories">
            <c:forTokens items="5,10,15,20" delims="," var="item">
                <option ${ maxStories == item ? 'selected="selected"' : '' }>${item}</option>
            </c:forTokens>
        </select>
    </p>

    <p>
        <label for="${n}feedView"><spring:message code="edit.news.feedView"/></label>
        <c:set var="feedView" value="${renderRequest.preferences.map['feedView'][0]}"/>
        <select id="${n}feedView">
            <option value="tabs" ${ feedView == 'tabs' ? 'selected="selected"' : '' }><spring:message code="edit.news.feedView.tab"/></option>
            <option value="select" ${ feedView == 'select' ? 'selected="selected"' : '' }><spring:message code="edit.news.feedView.menu"/></option>
        </select>
    </p>

    <p>
        <label for="${n}storyView"><spring:message code="edit.news.storyview"/></label>
        <c:set var="storyView" value="${renderRequest.preferences.map['summaryView'][0]}"/>
        <select id="${n}storyView">
            <option value="flyout" ${ storyView == 'flyout' ? 'selected="selected"' : '' }><spring:message code="edit.news.storyview.flyout"/></option>
            <option value="summaries" ${ storyView == 'summaries' ? 'selected="selected"' : '' }><spring:message code="edit.news.storyview.scrolldiv"/></option>
        </select>
    </p>
    
    <p>
        <label for="${n}newWindow"><spring:message code="edit.news.newwindows"/></label>
        <c:set var="newWindow" value="${renderRequest.preferences.map['newWindow'][0]}"/>
        <input type="checkbox" id="${n}newWindow" ${ newWindow == "true" ? "checked='checked'" : ""} />
    </p>
</div>

<p><a href="<portlet:renderURL portletMode="help"/>"><spring:message code="help.need"/></a></p>

<br />
<hr />
<p>
	<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle;"> <spring:message code="back.list"/></a>
</p>
        
