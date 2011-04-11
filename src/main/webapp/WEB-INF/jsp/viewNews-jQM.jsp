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
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="feedUrl">
    <portlet:param name="action" value="ajax"/>
</portlet:actionURL>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.5/jquery-1.5.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.8/jquery-ui-1.8.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.3/js/fluid-all-1.3.js"/>"></script>
<script type="text/javascript">
$("[data-role=page]").live('pagebeforecreate',function(event){
  return false;
});
</script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery-mobile/1.0a3/jquery.mobile-1.0a3.js"/>"></script>
<script type="text/javascript" src="<c:url value="/scripts/news-feed-view-mobile.min.js"/>"></script>

<div class="portlet ptl-newsreader view-news">
	<div data-role="content" class="portlet-content">
		<div id="${n}">
		    
		    <div class="news-reader-feed-list">
		        <ul data-role="listview" class="feed">
		            <li class="news-reader-feed">
		                <a class="news-reader-feed-link" href="#">
		                    <span class="news-reader-feed-title"></span>
		                </a>
		            </li>
		        </ul>
		    </div>
		    <div class="news-reader-story-list" style="display:none;">
		        <div data-role="header" data-theme="d" class="news-reader-back-bar">
		            <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true">Back</a>
		            <h2 class="news-reader-feed-title">News</h2>
		        </div>
		        <ul data-role="listview" class="feed">
		            <li class="news-reader-story">
		                <img class="news-reader-story-image"/>
		                <h3><a class="news-reader-story-link" href="#"></a></h3>
		                <p class="news-reader-story-summary"></p>
		            </li>
		        </ul>
		    </div>
		</div>
	</div>
</div>

<script type="text/javascript"><rs:compressJs>
    var newsReaderPortlet = newsReaderPortlet || {};
    newsReaderPortlet.jQuery = jQuery.noConflict(true);
    newsReaderPortlet.fluid = fluid;
    fluid = null;
    fluid_1_1 = null;
    newsReaderPortlet.jQuery(function(){
        var $ = newsReaderPortlet.jQuery;
        var fluid = newsReaderPortlet.fluid;
        
        $(document).ready(function () {
            newsreader.MobileFeedListView(
                $("#${n}"),
                {
                    url: "${feedUrl}"
                }
            );
        });
    });
</rs:compressJs></script>
