<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<h2>
	<a href="${model.feed.link}" target="_blank">${model.feed.title}</a>
	${model.feed.author}
</h2>
<div class="portlet-rss-scrollable-content">
	<c:forEach items="${ model.feed.entries }" var="newsItem">
		<h3>
			<a href="${newsItem.link}" target="_blank">
				<c:out value="${newsItem.title}"/></a>
		</h3>
		<p>${newsItem.description.value}</p>
	</c:forEach>
</div>
<p><c:out value="${model.feed.copyright}"/></p>
