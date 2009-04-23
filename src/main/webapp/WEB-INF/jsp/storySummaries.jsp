<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<h2>
	<a href="${model.feed.link}" target="_blank">${model.feed.title}</a>
	${model.feed.author}
</h2>
<div class="portlet-rss-scrollable-content">
	<dl>
	<c:forEach items="${ model.feed.entries }" var="newsItem">
		<dt>
			<a href="${newsItem.link}" target="_blank">
				<c:out value="${newsItem.title}"/></a>
		</dt>
		<dd>${newsItem.description.value}</dd>
	</c:forEach>
	</dl>
</div>
<p><c:out value="${model.feed.copyright}"/></p>
