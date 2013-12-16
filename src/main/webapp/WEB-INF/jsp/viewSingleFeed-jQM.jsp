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
<c:set var="max" value="${ prefs.maxStories > fn:length(feed.entries) ? fn:length(feed.entries)-1 : prefs.maxStories-1 }"/>
<c:if test="${max < 0}">
	<c:set var="max" value="0"/>
</c:if>

<div class="portlet ptl-newsreader view-singlefeed">
	<div data-role="content" class="portlet-content">

		<ul data-role="listview" class="feed">
		    <c:forEach items="${ feed.entries }" var="entry" end="${ max }">
		        <li>
                    <c:choose><c:when test="${ not empty entry.imageUrl }">
                        <a href="${ entry.link }">
                        <img src="${ entry.imageUrl }"/>
                    </c:when>
                    <c:otherwise>
                        <a href="${ entry.link }" style="padding-left:0">
                    </c:otherwise></c:choose>
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
