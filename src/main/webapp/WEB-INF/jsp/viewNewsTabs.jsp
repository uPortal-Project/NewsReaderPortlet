<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>

    <script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>"></script>
    <script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.js"/>"></script>
    
    <script type="text/javascript">
        var newsReaderPortlet = newsReaderPortlet || {};
        newsReaderPortlet.jQuery = jQuery.noConflict(true);
        newsReaderPortlet.jQuery(function(){
            var $ = newsReaderPortlet.jQuery;

            $(document).ready(function(){
                $.post("<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>",{ }, function(j){
                    var tabs = $('#${n}newsTabs ul');
                    var news = $('#${n}newsTabs');
                    var selected = 0;
                    for (var i = 0; i < j.feeds.length; i++) {
                        var li = $(document.createElement('li')).append(
	                        $(document.createElement('a')).append(
	                            $(document.createElement('span')).text(j.feeds[i].name)
	                        ).attr('href', '#${n}feed' + j.feeds[i].id)
	                    );
                        tabs.append(li);
                        news.append($(document.createElement('div')).attr('id', '${n}feed' + j.feeds[i].id));
                        if (j.feeds[i].id == j.activeFeed) selected = i;
                    }
                    $('#${n}newsTabs').tabs({ selected: selected });

                    displayTheNews(j.activeFeed, j.feed);
                    
                },"json");

                $('#${n}newsTabs').bind('tabsselect', function(event, ui) {

                    $(ui.panel).html('<p><strong>Loading...</strong></p>');
                    var feedId = ui.panel.id.split('feed')[1];
                    $.post("<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>",{ activeateNews: feedId }, function(j) {

                        if(j.message != undefined) {
                            $(ui.panel).html('<p>'+j.message+'</p>');  
                        }
                        else {
                            displayTheNews(feedId, j.feed);
                        }                           
                    },"json");

                });
            });

            function displayTheNews(id, feed) {
                //TODO handle undefind properties better
                //TODO Optimise this code?
                
                var header = '<h2><a href="'+feed.link+'" rel="popup">'+feed.title+'</a>'
                if (feed.author != undefined) header+= feed.author;
                header +='</h2>';

                var entries = '<div class="portlet-rss-scrollable-content">';
                for (var i = 0; i < feed.entries.length; i++) {
                    entries += '<h3><a href="'+feed.entries[i].link+'" rel="popup">'+feed.entries[i].title+'</a></h3>';
                    entries += '<p>'+feed.entries[i].description+'</p>';
                }
                entries += '</div>';
                
                var footer = '';
                if (feed.copyright != undefined) footer += '<p>'+feed.copyright+'</p>';
                
                $("#${n}feed" + id).html(header+entries+footer);
            }
            

        });
    </script>

<div class="org-jasig-portlet-newsreader">
	
	<div id="${n}newsTabs" class="ui-tabs">
	    <ul>
	    </ul>
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
