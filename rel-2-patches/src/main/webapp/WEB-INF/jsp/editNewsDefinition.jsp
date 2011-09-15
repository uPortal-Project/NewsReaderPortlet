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

<portlet:actionURL var="postUrl"><portlet:param name="action" value="editNewsDefinition"/></portlet:actionURL>

<h3>Edit feed</h3>

<form:form name="news" commandName="newsDefinitionForm" action="${postUrl}">
	<form:hidden path="id"/>
	<p>
		<label class="portlet-form-field-label">Feed name:</label>
		<form:input path="name" size="50"/>
		<form:errors path="name" cssClass="portlet-msg-error"/>
	</p>
   	<p>
        <label class="portlet-form-field-label">Feed class:</label>
        <form:input path="className" size="50"/>
		<form:errors path="className" cssClass="portlet-msg-error"/>
	</p>
	<br/>
	<p id="role-list">
		<label class="portlet-form-field-label">Feed default roles:</label><br />
		<c:forEach items="${ newsDefinitionForm.role }" var="role">
			<div style="padding-left: 5px;">
				<input name="role" type="text" value="${ role }" size="20"/>
				<a href="javascript:;" onclick="removeRole(this)">
					<img style="vertical-align: middle;" src="<c:url value="/images/delete.png"/>"/>
				</a>
			</div>
		</c:forEach>
		<div style="padding: 5px;">
			<a href="javascript:;" onclick="addRole('role-list')">
				<img style="vertical-align: middle;" src="<c:url value="/images/add.png"/>"/>
				add a role</a>
		</div>
	</p>
	<p id="parameter-list">
		<label class="portlet-form-field-label">Feed parameters:</label><br />
		<c:forEach items="${ newsDefinitionForm.parameterName }" var="paramName" varStatus="status">
			<div style="padding-left: 5px">
				<input name="parameterName" type="text" value="${ paramName }" size="20"/>
				<input name="parameterValue" type="text" value="${ newsDefinitionForm.parameterValue[status.index] }" size="20"/>
				<a href="javascript:;" onclick="removeParameter(this)">
					<img style="vertical-align: middle;" src="<c:url value="/images/delete.png"/>"/>
				</a>
			</div>
		</c:forEach>
		<div style="padding: 5px;">
			<a href="javascript:;" onclick="addParameter('parameter-list')">
				<img style="vertical-align: middle;" src="<c:url value="/images/add.png"/>"/>
				add a parameter</a>
		</div>
	</p>
    <p>
        <button type="submit" class="portlet-form-button">Save feed</button>
    </p>
</form:form>
<br />
<hr />
<p>
	<a href="<portlet:renderURL><portlet:param name="action" value="administration"/></portlet:renderURL>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle"> Return to main administration page</a>
</p>
