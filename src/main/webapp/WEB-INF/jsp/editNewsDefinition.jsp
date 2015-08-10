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
    <portlet:param name="action" value="editNewsDefinition"/>
</portlet:actionURL>

<script type="text/javascript"><rs:compressJs>
	function addRole(id) {
		var div = document.getElementById(id);
		var container = document.createElement('div');
		container.style.padding = "5px";
		var input = document.createElement('input');
		input.name = 'role';
		input.type = 'text';
		input.size = '20';
		container.appendChild(input);
		var remove = document.createElement('a');
		remove.href = 'javascript:;';
		remove.onclick = function(){removeRole(this)};
		remove.appendChild(document.createTextNode(' '));
		var img = document.createElement('img');
		img.src = '<c:url value="/images/delete.png"/>';
		img.style.verticalAlign = 'middle';
		remove.appendChild(img);
		container.appendChild(remove);
		div.appendChild(container);
	}
	
	function removeRole(link) {
		var div = link.parentNode;
		div.parentNode.removeChild(div);
	}

	function addParameter(id) {
		var div = document.getElementById(id);
		var container = document.createElement('div');
		container.style.padding = "5px";
		var input = document.createElement('input');
		input.name = 'parameterName';
		input.type = 'text';
		input.size = '20';
		container.appendChild(input);
		input = document.createElement('input');
		input.name = 'parameterValue';
		input.type = 'text';
		input.size = '20';
		container.appendChild(input);
		var remove = document.createElement('a');
		remove.href = 'javascript:;';
		remove.onclick = function(){removeRole(this)};
		remove.appendChild(document.createTextNode(' '));
		var img = document.createElement('img');
		img.src = '<c:url value="/images/delete.png"/>';
		img.style.verticalAlign = 'middle';
		remove.appendChild(img);
		container.appendChild(remove);
		div.appendChild(container);
	}

	function removeParameter(link) {
		var div = link.parentNode;
		div.parentNode.removeChild(div);
	}
</rs:compressJs></script>

<div class="container-fluid newsreader-container">
	<div class="row newsreader-portlet-toolbar">
		<div class="col-md-6 no-col-padding">
			<h3><spring:message code="edit.news.feed.edit"/></h3>
		</div>
		<div class="col-md-6 no-col-padding">
			<div class="nav-links">
				<a href="<portlet:renderURL><portlet:param name="action" value="administration"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="administration.feed.back"/>
        </a>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<form:form name="news" commandName="newsDefinitionForm" action="${postUrl}" class="form-horizontal">
				<form:hidden path="id"/>
				<div class="form-group">
				  <label class="col-md-4"><spring:message code="edit.news.feed.name"/></label>
          <div class="col-md-8">
            <form:input path="name" class="form-control" />
            <form:errors path="name" cssClass="portlet-msg-error"/>
          </div>
				</div>
				<div class="form-group">
				  <label class="col-md-4"><spring:message code="edit.news.feed.class"/></label>
          <div class="col-md-8">
            <form:select path="className" class="form-control">
              <c:forEach items="${availableAdapters}" var="adapter">
                <c:set var="adapterName"><spring:message code="${adapter.nameKey}"/></c:set>
                <c:set var="adapterDescription"><spring:message code="${adapter.descriptionKey}"/></c:set>
                <form:option value="${adapter.className}" label="${adapterName}" title="${adapterDescription}" />
              </c:forEach>
            </form:select>
          </div>
        </div>
		    <form:errors path="className" cssClass="portlet-msg-error"/>
        <div id="role-list">
          <label class="portlet-form-field-label"><spring:message code="edit.news.feed.roles"/></label>
          <c:forEach items="${ newsDefinitionForm.role }" var="role">
            <div style="padding-left: 5px;">
              <input name="role" type="text" value="${ role }" size="20"/>
              <a style="text-decoration: none;" href="javascript:;" onclick="removeRole(this)">
                <img style="vertical-align: middle;" src="<c:url value="/images/delete.png"/>"/>
              </a>
            </div>
          </c:forEach>
          <div style="padding: 5px;">
            <a href="javascript:;" onclick="addRole('role-list')">
              <img style="vertical-align: middle;" src="<c:url value="/images/add.png"/>"/>
              <spring:message code="edit.news.feed.roles.add"/>
            </a>
          </div>
        </div>
        <div id="parameter-list">
          <label class="portlet-form-field-label"><spring:message code="edit.news.feed.param"/></label><br />
          <c:forEach items="${ newsDefinitionForm.parameterName }" var="paramName" varStatus="status">
            <div style="padding-left: 5px">
              <input name="parameterName" type="text" value="${ paramName }" size="20"/>
              <input name="parameterValue" type="text" value="${ newsDefinitionForm.parameterValue[status.index] }" size="20"/>
              <a style="text-decoration=none;" href="javascript:;" onclick="removeParameter(this)">
                <img style="vertical-align: middle;" src="<c:url value="/images/delete.png"/>"/>
              </a>
            </div>
          </c:forEach>
          <div style="padding: 5px;">
            <a href="javascript:;" onclick="addParameter('parameter-list')">
              <img style="vertical-align: middle;" src="<c:url value="/images/add.png"/>"/>
              <spring:message code="edit.news.feed.param.add"/></a>
          </div>
				</div>
				<hr />
				<div>
					<button type="submit" class="btn btn-primary">
						<i class="fa fa-save"></i> <spring:message code="edit.news.feed.sav"/>
					</button>
					&nbsp;|&nbsp;
					<a href="<portlet:renderURL><portlet:param name="action" value="administration"/></portlet:renderURL>">
						<spring:message code="administration.cancel"/>
					</a>
				</div>
      </form:form>
    </div>
  </div>
</div>