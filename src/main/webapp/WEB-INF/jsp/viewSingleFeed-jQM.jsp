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

<div class="portlet ptl-newsreader view-singlefeed">
	<div data-role="content" class="portlet-content">
		
		<ul data-role="listview" class="feed">
		    <c:forEach items="${ feed.entries }" var="entry">
		        <li>
		        	<a href="${ entry.link }">
			            <c:if test="${ not empty entry.imageUrl }"><img src="${ entry.imageUrl }"/></c:if>
			            <h3 class="title">${ entry.title }</h3>
			            <p>
			                ${ entry.description }
			            </p>
		            </a>
		        </li>
		    </c:forEach>
		</ul>
		
	</div>
</div>