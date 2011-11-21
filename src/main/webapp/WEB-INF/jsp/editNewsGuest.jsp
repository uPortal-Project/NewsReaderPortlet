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

<div class="fl-widget portlet org-jasig-portlet-newsreader" role="section">

	<div class="fl-widget-content portlet-body" role="main">
		
		<div class="portlet-msg-info" role="status"> <!-- role changes to "alert" for error and alert messages. -->
	        <spring:message code="editnews.guest.msg"/>
		</div>
		
		<p>
		<a href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>" style="vertical-align: middle"> Return to news feeds</a>
		</p>
		
	</div>
</div>