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

<style type="text/css">
	table.edit-news { width: 100%; }
	table.edit-news td { font-size: 1.1em; }
	table.edit-news td.instruction { color: #666; font-size: 1em; text-align: center; vertical-align: bottom; }
</style>

<portlet:actionURL var="postUrl"/>
<table class="edit-news">
	<tr>
		<td><h3><spring:message code="administration.preconf"/></h3></td>
		<td class="instruction"><spring:message code="edit.news.edit"/></td>
		<td class="instruction"><spring:message code="edit.news.delete"/></td>
	</tr>
	<c:forEach items="${ model.feeds }" var="feed">
    	<tr>
    		<td>${ feed.name }</td>
    		<td class="instruction">
    			<a href="<portlet:renderURL><portlet:param name="action" value="editNewsDefinition"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>"
    					title="<spring:message code="edit.news.edit.title"/>">
					<img alt="<spring:message code="edit.news.edit.alt"/>" src="<c:url value="/images/pencil.png"/>"/>
				</a>
    		</td>
    		<td class="instruction">
    			<a href="<portlet:actionURL><portlet:param name="action" value="deletePredefinedFeed"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>"
    					title="<spring:message code="edit.news.delete.title"/>">
    				<img alt="<spring:message code="edit.news.delete.alt"/>" src="<c:url value="/images/rss_delete.png"/>"/>
    			</a>
    		</td>
    	</tr>
	</c:forEach>
</table>

<p>
	<a href="<portlet:renderURL><portlet:param name="action" value="editNewsDefinition"/></portlet:renderURL>">
		<img src="<c:url value="/images/rss_add.png"/>" style="vertical-align: middle"/> <spring:message code="edit.news.add"/>
	</a>
</p>

<br />
<hr />
<p>
	<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align:middle;"> <spring:message code="back.list"/></a>
</p>
