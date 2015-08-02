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
  <link href="<c:url value="/css/newsreader.css"/>" rel="stylesheet" type="text/css" />

  <c:set var="n"><portlet:namespace/></c:set>
    
<div id="newsreader-container" class="container-fluid">
  <div class="row newsreader-portlet-toolbar">
    <div class="col-md-6 no-col-padding">
      <h3>${storyTitle}</h3>
    </div>
    <div class="col-md-6 no-col-padding">
      <div class="nav-links">
        <a href="<portlet:renderURL/>"><i class="fa fa-arrow-left"></i> <spring:message code="back.list"/></a>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      ${fullStory}
    </div>
  </div>
</div>       

