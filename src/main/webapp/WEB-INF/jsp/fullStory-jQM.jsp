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
    
<div class="portlet ptl-newsreader org-jasig-portlet-newsreader">

    <div data-role="header" class="titlebar portlet-titlebar">
        <a href="<portlet:renderURL portletMode="view"/>" data-role="button" data-icon="back" data-inline="true"><spring:message code="back"/></a>
        <h2 class="title story-title">${storyTitle}</h2>
	</div>
	
	${fullStory}
	
</div>       

