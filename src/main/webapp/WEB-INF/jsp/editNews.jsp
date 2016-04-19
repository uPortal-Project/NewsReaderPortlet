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
<c:set var="n"><portlet:namespace/></c:set>

<link href="<c:url value="/css/newsreader.css"/>" rel="stylesheet" type="text/css" />

<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>
<script type="text/javascript"><rs:compressJs>
    ${n}.jQuery(function() {

        var $ = ${n}.jQuery;

        var savePrefUrl = '<portlet:resourceURL/>';

        $('#${n}feedView').change(function(){
            $.post(savePrefUrl, { prefName: 'feedView', prefValue: $('#${n}feedView').val() }, null, 'json');
        });
        $('#${n}storyView').change(function(){
            $.post(savePrefUrl, { prefName: 'summaryView', prefValue: $('#${n}storyView').val() }, null, 'json');
        });
        $("#${n}newWindow").change(function(e){
            $.post(savePrefUrl, { prefName: 'newWindow', prefValue: ($(e.target).attr("checked") == 'checked') }, null, 'json');
        });
        $('#${n}maxStories').change(function(){
            $.post(savePrefUrl, { prefName: 'maxStories', prefValue: $('#${n}maxStories').val() }, null, 'json');
        });

    });
</rs:compressJs></script>

<div class="container-fluid newsreader-container">
	<div class="row newsreader-portlet-toolbar">
		<div class="col-md-6 no-col-padding">
			<h3><spring:message code="edit.news.pref"/></h3>
		</div>
		<div class="col-md-6 no-col-padding">
			<div class="nav-links">
				<a href="<portlet:renderURL portletMode="help"/>"><i class="fa fa-info-circle"></i> <spring:message code="help.need"/></a>
				&nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="view"/>"><i class="fa fa-arrow-left"></i> <spring:message code="back.list"/></a>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<table class="table table-condensed table-striped">
				<thead>
					<tr>
					<c:choose>
						<c:when test="${ not empty model.myNewsConfigurations }">
							<th class="col-md-9"><spring:message code="edit.title"/></th>
							<th class="col-md-1 text-center"><spring:message code="edit.news.edit"/></th>
							<th class="col-md-1 text-center"><spring:message code="edit.news.delete"/></th>
							<th class="col-md-1 text-center"><spring:message code="edit.news.displayed"/></th>
						</c:when>
						<c:otherwise>
							<td colspan="4">&nbsp;</td>
						</c:otherwise>
					</c:choose>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ model.myNewsConfigurations }" var="feed">
						<tr>
							<td>${ feed.newsDefinition.name }</td>
							<td class="text-center">
								<a href="<portlet:renderURL><portlet:param name="action" value="editUrl"/>
									<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>" title="<spring:message code="edit.news.edit.title"/>">
									<i class="fa fa-lg fa-edit"></i>
								</a>
							</td>
							<td class="text-center">
								<a href="<portlet:actionURL><portlet:param name="actionCode" value="delete"/>
									<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>" title="<spring:message code="edit.news.delete.title"/>">
									<i class="fa fa-lg fa-trash-o"></i>
								</a>
							</td>
							<td class="text-center">
								<c:choose>
									<c:when test="${ feed.displayed }">
										<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="hide"/>
											<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
										<a href="${ displayURL }" title="<spring:message code="edit.news.hide.title"/>">
										<i class="fa fa-lg fa-eye"></i>
										</a>
									</c:when>
									<c:otherwise>
										<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/><portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
										<a class="not-active" href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
										<i class="fa fa-lg fa-eye-slash"></i>
										</a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<table class="table table-condensed table-striped">
				<thead>
					<th class="col-md-10"><spring:message code="edit.news.exist"/></th>
					<th class="col-md-1 text-center"><spring:message code="edit.news.edit"/></th>
					<th class="col-md=1 text-center"><spring:message code="edit.news.displayed"/></th>
				</thead>
				<tbody>
					<c:forEach items="${ model.predefinedNewsConfigurations }" var="feed">
						<tr>
							<td>${ feed.newsDefinition.name }</td>
							<td class="text-center">
							<c:set var="editAction" value="${ model.predefinedEditActions[feed.newsDefinition.className] }"/>
							<c:choose>
								<c:when test="${ not empty editAction }">
									<a href="<portlet:renderURL><portlet:param name="action" value="${ editAction }"/>
									<portlet:param name="id" value="${ feed.id }"/></portlet:renderURL>" title="<spring:message code="edit.news.edit.title"/>">
									<i class="fa fa-lg fa-edit"></i>
									</a>
								</c:when>
								<c:otherwise>&nbsp;</c:otherwise>
							</c:choose>
							</td>
							<td class="text-center">
								<c:choose>
									<c:when test="${ feed.displayed }">
										<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="hide"/>
											<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
										<a href="${ displayURL }" title="<spring:message code="edit.news.hide.title"/>">
										<i class="fa fa-lg fa-eye"></i>
										</a>
									</c:when>
									<c:otherwise>
										<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="show"/>
											<portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
										<a class="not-active" href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
										<i class="fa fa-lg fa-eye-slash"></i>
										</a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
					<c:forEach items="${ model.hiddenFeeds }" var="feed">
						<tr>
							<td>${ feed.name }</td>
							<td class="text-center">
								<portlet:actionURL var="displayURL"><portlet:param name="actionCode" value="showNew"/><portlet:param name="id" value="${ feed.id }"/></portlet:actionURL>
								<a href="${ displayURL }" title="<spring:message code="edit.news.show.title"/>">
								<i class="fa fa-lg fa-eye"></i>
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
			<a href="<portlet:renderURL><portlet:param name="action" value="editUrl"/></portlet:renderURL>" class="btn btn-primary"><i class="fa fa-lg fa-rss"></i> <spring:message code="edit.news.add"/></a>
		</div>
	</div>
	<hr />
	<div class="row">
		<div class="col-md-12">
			<form class="form-horizontal">
				<div class="form-group">
					<label for="${n}feedView" class="col-md-7"><spring:message code="edit.news.feedView"/></label>
					<c:set var="feedView" value="${renderRequest.preferences.map['feedView'][0]}"/>
					<div class="col-md-5">
						<select id="${n}feedView" class="form-control">
							<option value="tabs" ${ feedView == 'tabs' ? 'selected="selected"' : '' }><spring:message code="edit.news.feedView.tab"/></option>
							<option value="select" ${ feedView == 'select' ? 'selected="selected"' : '' }><spring:message code="edit.news.feedView.menu"/></option>
							<option value="all" ${ feedView == 'all' ? 'selected="selected"' : '' }><spring:message code="edit.news.feedView.combined"/></option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label for="${n}storyView" class="col-md-7"><spring:message code="edit.news.storyview"/></label>
					<c:set var="storyView" value="${renderRequest.preferences.map['summaryView'][0]}"/>
					<div class="col-md-5">
						<select id="${n}storyView" class="form-control">
							<option value="flyout" ${ storyView == 'flyout' ? 'selected="selected"' : '' }><spring:message code="edit.news.storyview.flyout"/></option>
							<option value="summaries" ${ storyView == 'summaries' ? 'selected="selected"' : '' }><spring:message code="edit.news.storyview.summaries"/></option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label for="${n}newWindow" class="col-md-7"><spring:message code="edit.news.newwindows"/></label>
					<div class="col-md-5">
						<c:set var="newWindow" value="${renderRequest.preferences.map['newWindow'][0]}"/>
						<input type="checkbox" id="${n}newWindow" ${ newWindow == "true" ? "checked='checked'" : ""} />
					</div>
				</div>
				<div class="form-group">
					<label for="${n}maxStories" class="col-md-7"><spring:message code="edit.news.feed.maxstories"/></label>
					<div class="col-md-5">
						<c:set var="maxStories" value="${renderRequest.preferences.map['maxStories'][0]}"/>
						<input type="number" id="${n}maxStories" value="${maxStories}" step=1 min=-1 maxlength=4 />
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-12">
						<button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="edit.news.pref.sav"/></button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>