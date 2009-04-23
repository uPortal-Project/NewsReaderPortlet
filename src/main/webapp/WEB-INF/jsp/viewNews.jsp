    <jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

	<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>"></script>
	
    <script type="text/javascript">
    	var newsReaderPortlet = newsReaderPortlet || {};
    	newsReaderPortlet.jQuery = jQuery.noConflict(true);
    	newsReaderPortlet.jQuery(function(){
    		var $ = newsReaderPortlet.jQuery;

			$(document).ready(function(){
				$.post("<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>",{ }, function(j){
					var options = '<option value="0">Choose...</option>';
					for (var i = 0; i < j.feeds.length; i++) {
				        options += '<option value="' + j.feeds[i].id + '">' + j.feeds[i].name + '</option>';
					}
					$("select#<portlet:namespace/>_feeds").html(options);

					if(j.message != undefined) {
						$("#<portlet:namespace/>_story").html('<p>'+j.message+'</p>');	
					}
					else {
						displayTheNews(j.feed);
					}

					$("select#<portlet:namespace/>_feeds").val(j.activeFeed).attr("selected","selected");
					
				},"json");
			});


			$(document).ready(function(){
				$('select#<portlet:namespace/>_feeds').change(function() {
					$("#<portlet:namespace/>_story").html('<p><strong>Loading...</strong></p>');
					
					$.post("<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>",{ activeateNews: $(this).val() }, function(j) {

						if(j.message != undefined) {
							$("#<portlet:namespace/>_story").html('<p>'+j.message+'</p>');	
						}
						else {
							displayTheNews(j.feed);
						}							
					},"json");
				});
			});

			function displayTheNews(feed) {
				//TODO handle undefind properties better
				//TODO Optimise this code?
				
				var header = '<h2><a href="'+feed.link+'" rel="popup">'+feed.title+'</a>'+feed.author+'</h2>';

				var entries = '<div class="portlet-rss-scrollable-content">';
				for (var i = 0; i < feed.entries.length; i++) {
					entries += '<h3><a href="'+feed.entries[i].link+'" rel="popup">'+feed.entries[i].title+'</a></h3>';
					entries += '<p>'+feed.entries[i].description+'</p>';
				}
				entries += '</div>';
				
				var footer = '<p>'+feed.copyright+'</p>';
				
				$("#<portlet:namespace/>_story").html(header+entries+footer);
			}
			

    	});
    </script>

<div class="org-jasig-portlet-newsreader">

		<div>
			News Feeds:			
				<form action="<portlet:actionURL/>" method="post" name="<portlet:namespace/>FeedForm">
					<select name="activeateNews" id="<portlet:namespace/>_feeds">
					</select>
				</form>
        </div>

        <div id="<portlet:namespace/>_story">
        	<p><strong>Loading...</strong></p>
        </div>
        
	     <div>
			<a href="<portlet:renderURL portletMode='help'/>">Help</a>&nbsp;|&nbsp;
			<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>
	        <c:if test="${ model.isAdmin }">
		        	&nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
		        		News Administration</a>
	        </c:if>
		</div>
</div>
