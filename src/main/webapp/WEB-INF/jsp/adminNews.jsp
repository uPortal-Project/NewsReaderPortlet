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

<div class="container-fluid newsreader-container">
	<div class="row newsreader-portlet-toolbar">
		<div class="col-md-12 no-col-padding">
			<div class="nav-links">
				<a href="<portlet:renderURL portletMode="view"/>"><i class="fa fa-arrow-left"></i> <spring:message code="back.list"/>
				</a>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<table class="table table-condensed table-striped">
				<thead>
					<tr>
						<th><spring:message code="administration.preconf"/></th>
						<th class="text-center"><spring:message code="edit.news.edit"/></th>
						<th class="text-center"><spring:message code="edit.news.delete"/></th>
					</tr>
				</thead>
				<tbody>
	<c:forEach items="${ model.feeds }" var="feed">
    	<tr>
							<td class="col-md-10">${ feed.name }</td>
							<td class="col-md-1 text-center">
								<a href="<portlet:renderURL>
									<portlet:param name="action" value="editNewsDefinition"/>
									<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>"
										title="<spring:message code="edit.news.edit.title"/>" class="text-center">
									<i class="fa fa-lg fa-edit"></i>
							</a>
							</td>
							<td class="col-md-1 text-center">
    			<a href="<portlet:actionURL><portlet:param name="action" value="deletePredefinedFeed"/>
    					<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>"
    					title="<spring:message code="edit.news.delete.title"/>">
										<i class="fa fa-lg fa-trash-o"></i>
    			</a>
    		</td>
    	</tr>
	</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<hr />
	<div class="row">
		<div class="col-md-12">
			<a href="<portlet:renderURL><portlet:param name="action" value="editNewsDefinition"/></portlet:renderURL>" class="btn btn-info"><i class="fa fa-plus-circle"></i> <spring:message code="edit.news.add"/>
			</a>
		</div>
	</div>
</div>

