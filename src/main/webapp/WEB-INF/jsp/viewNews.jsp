<html xmlns="http://www.w3c.org/1999/xhtml" xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
    xmlns:portlet="http://java.sun.com/portlet" xmlns:html="/WEB-INF/tags/html" xml:lang="en"
    lang="en">
    <jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
    
    
    <head>
        <title>View News</title>
        <style type="text/css">
            
        </style>
    </head>

    <body>
     <div class="portlet-menu">
		<ul>
			<li class="portlet-menu-item">
				|&nbsp;<a href="<portlet:renderURL portletMode='help'/>">Help</a>&nbsp;|&nbsp;
			</li>
			<li class="portlet-menu-item">
				<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>&nbsp;|
			</li>
		</ul>
	</div>
	
	
		<div style="padding-bottom: 3px;">
			News Feeds:			
				<form action="<portlet:actionURL/>" method="post">
					<select name="activeateNews" onchange="this.form.submit();">
					<option value="0">Choose...</option>
					<c:forEach items="${ model.feeds }" var="feed">
						<option 
						<c:if test="${model.feedId eq feed.id }"> selected="selected" </c:if>
						value="${ feed.id }">${ feed.newsDefinition.name }</option>
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
				<p><a href="#" onclick="openBrWindow('<c:out value="${model.feed.link}"/>','','')">${model.feed.title}</a> ${model.feed.author}</p>
        
        		<div class="portlet-rss-scrollable-content">
        			<dl>
        			<c:forEach items="${ model.feed.entries }" var="newsItem">
	        			<dt><a href="#" onclick="openBrWindow('<c:out value="${newsItem.link}"/>','','')"><c:out value="${newsItem.title}"/></a></dt>
                		<dd>${newsItem.description.value}</dd>
        			</c:forEach>
        			</dl>
				</div>
				<p><c:out value="${model.feed.copyright}"/></p>
			</c:otherwise>
		</c:choose>
		
        
        
        
        <c:if test="${ sessionScope.isAdmin }">
        	<p>
	        	<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
	        		News Administration</a>
        	</p>
        </c:if>
        
        
    </body>
    
</html>