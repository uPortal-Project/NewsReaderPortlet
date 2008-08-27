    <jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

		<div>
			News Feeds:			
				<form action="<portlet:actionURL/>" method="post" name="<portlet:namespace/>FeedForm">
					<select name="activeateNews" onchange="document.<portlet:namespace/>FeedForm.submit();">
					<option value="0">Choose...</option>
					<c:forEach items="${ model.feeds }" var="feed">
						<c:choose>
							<c:when test="${ not empty model.feedId and model.feedId != -1}">
								<option <c:if test="${model.feedId eq feed.id }"> selected="selected" </c:if>
								value="${ feed.id }">${ feed.newsDefinition.name }</option>
							</c:when>
							<c:otherwise>
								<option value="${ feed.newsDefinition.name }">${ feed.newsDefinition.name }</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
				</form>
        </div>

        
        <c:if test="${ not empty model.errors }">
	        <p class="portlet-msg-error">
	        	<c:forEach items="${ model.errors }" var="error">
		        	${ error }<br/>
	        	</c:forEach>
	        </p>
        </c:if>
        
		<c:choose>
			<c:when test="${empty model.feed}">
				<p>Select the news you wish to read.</p>
			</c:when>
			<c:when test="${ empty model.feed.entries }">
				<p>No news.</p>
			</c:when>
			<c:otherwise>
			    <jsp:directive.include file="/WEB-INF/jsp/storySummaries.jsp"/>
			</c:otherwise>
		</c:choose>
		
	     <div>
			<a href="<portlet:renderURL portletMode='help'/>">Help</a>&nbsp;|&nbsp;
			<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>
	        <c:if test="${ model.isAdmin }">
		        	&nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
		        		News Administration</a>
	        </c:if>
		</div>
