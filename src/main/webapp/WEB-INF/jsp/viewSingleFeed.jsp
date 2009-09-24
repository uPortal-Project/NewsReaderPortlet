<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.2/jquery-1.3.2.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.1.1/js/fluid-all-1.1.1.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/scripts/news-feed-view.min.js"/>"></script>

<style>
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
</style>
    
<c:set var="storyView">${renderRequest.preferences.map['storyView'][0]}</c:set>
<script type="text/javascript">
    var newsReaderPortlet = newsReaderPortlet || {};
    newsReaderPortlet.jQuery = jQuery.noConflict(true);
    newsReaderPortlet.fluid = fluid;
    fluid = undefined;
    newsReaderPortlet.jQuery(function(){
        var $ = newsReaderPortlet.jQuery;

        $(document).ready(function(){
            var options = {
                url: '<portlet:actionURL><portlet:param name="action" value="ajax"/></portlet:actionURL>',
                namespace: '${n}',
                feedView: "${renderRequest.preferences.map['feedView'][0]}",
                summaryView: "${renderRequest.preferences.map['summaryView'][0]}",
                scrolling: "${renderRequest.preferences.map['scrolling'][0]}"
            };
            newsreader.FeedView("#${n}newsContainer", options);
        });

    });
</script>

<div class="org-jasig-portlet-newsreader">
    <div id="${n}newsContainer">Loading . . . </div>
</div>
