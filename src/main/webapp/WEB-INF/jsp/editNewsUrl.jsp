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

<portlet:actionURL var="postUrl">
    <portlet:param name="action" value="editUrl"/>
</portlet:actionURL>

<div class="container-fluid newsreader-container">
  <div class="row newsreader-portlet-toolbar">
    <div class="col-md-8 no-col-padding">
<h3><spring:message code="edit.news.feed.edit"/></h3>
    </div>
    <div class="col-md-4 no-col-padding">
      <div class="nav-links">
        <a href="<portlet:renderURL><portlet:param name="action" value="editPrefences"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="edit.news.feed.back"/></a>
        </a>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      <form:form name="news" commandName="newsListingCommand" action="${postUrl}" class="form-horizontal">
	<form:hidden path="id"/>
        <div class="form-group">
          <label class="col-md-3 control-label"><spring:message code="edit.news.feed.name"/></label>
          <div class="col-md-9">
            <form:input path="name" class="form-control" />
		<form:errors path="name" cssClass="portlet-msg-error"/>
          </div>
        </div>
        <div class="form-group">
          <label class="col-md-3 control-label"><spring:message code="edit.news.feed.url"/></label>
          <div class="col-md-9">
            <form:input path="url" class="form-control" />
		<form:errors path="url" cssClass="portlet-msg-error"/>
          </div>
        </div>
        <div class="form-group">
          <div class="alert alert-warning" role="alert">
		<spring:message code="edit.news.feed.note"/>
          </div>
        </div>

        <div class="form-group">
          <button type="submit" class="btn btn-primary"><i class="fa fa-save"></i> <spring:message code="edit.news.feed.sav"/></button>
        </div>
</form:form>
    </div>
  </div>
</div>
