<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<h3>
	<a href="${model.feed.link}" target="_blank">${model.feed.title}</a>
	${model.feed.author}
</h3>
<ul>
	<c:forEach items="${ model.feed.entries }" var="newsItem">
		<li>
			<a href="${newsItem.link}" target="_blank">
				<c:out value="${newsItem.title}"/></a>
		</li>
	</c:forEach>
</ul>
<p><c:out value="${model.feed.copyright}"/></p>
