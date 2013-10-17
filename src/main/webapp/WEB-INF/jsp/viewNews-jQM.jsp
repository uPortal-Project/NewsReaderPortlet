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
<c:set var="n"><portlet:namespace/></c:set>
<portlet:resourceURL var="feedUrl"/>

<c:set var="mobile" value="${ true }"/>
<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>

<div id="${n}">
    <div class="news-reader-feed-list portlet ptl-newsreader view-news">
    
        <div class="news-feeds-container">
            <div data-role="content" class="portlet-content">
                <div class="news-feed-list">
                </div>
                <c:if test="${supportsEdit && !isGuest}">
                    <div class="utilities">
                        <a data-role="button" href="<portlet:renderURL portletMode='edit'/>"><spring:message code="preferences"/></a>
                    </div>
                </c:if>            
            </div>
        </div>
        
        <div class="story-container" style="display:none">
            <div data-role="header" class="titlebar portlet-titlebar">
                <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true"><spring:message code="back"/></a>
                <h2 class="title story-title"><spring:message code="viewNews.storytitle"/></h2>
            </div>
            <div data-role="content" class="portlet-content">
                <div class="story-content">
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/template" id="${n}feed-list-template">
    <ul data-role="listview">
      ${"<%"} _(feeds).each(function(feed) { ${" %>"}
        <li>
            <a href="javascript:;"><h3>${"<%="} feed.name ${"%>"}</h3></a>
        </li>
      ${"<%"} }); ${"%>"}
    </ul>
</script>

<script type="text/template" id="${n}feed-detail-template">
    <div class="titlebar portlet-titlebar ui-header ui-bar-a" data-role="header">
        <a href="javascript:;" data-role="button" data-icon="back" data-direction="reverse" data-transition="none" class="ui-btn-left ui-btn-inline feed-back-button"><spring:message code="back"/></a>
        <h2 class="ui-title" role="heading">${"<%= title %>"}</h2>
    </div>

    <!-- Module content  -->
    <div data-role="content" class="portlet-content">
        <ul class="news-stories feed" data-role="listview">
          ${"<%"} _(entries).each(function(story) { ${" %>"}
            <li>
                <a href="${"<%="} story.link ${"%>"}">
                    <h3>${"<%="} story.title ${"%>"}</h3>
                    <p>${"<%="} story.description ${"%>"}</p>
                </a>
            </li>
          ${"<%"} }); ${"%>"}
        </ul>
    </div>
</script>

<script type="text/javascript"><rs:compressJs>
    
    ${n}.jQuery(function(){
        var $, _, Backbone, newsView;
        
        $ = ${n}.jQuery;
        _ = ${n}._;
        Backbone = ${n}.Backbone;
        upnews = ${n}.upnews;
        
        var MobileNewsFeedDetailView = upnews.NewsFeedDetailView.extend({
            template: _.template($("#${n}feed-detail-template").html()),
            postRender: function () {
                this.$el.trigger("create");
                this.$(".feed-back-button").click(function () {
                    $("#${n} .news-stories-container").hide();
                    $("#${n} .news-feeds-container").show();
                });
            }
        });
        
        var MobileNewsFeedListView = upnews.NewsFeedListView.extend({
            el: "#${n} .news-feed-list",
            template: _.template($("#${n}feed-list-template").html()),
            postRender: function () {
                var view = this;
                view.$("a").click(function () {
                    var link, feedId;
                    link = $(this);
                    var div = $(link.parents("li").get(0));
                    var index = $("#${n} .news-feeds-container li").index(div);
                    feedId = view.model.at(index).get("id");
                    view.trigger("feedSelected", feedId);
                });

                this.$el.trigger("create");
            }
        });

        newsView = new upnews.NewsView();
        newsView.url = "${feedUrl}";
        newsView.feedDetailViewFn = MobileNewsFeedDetailView;
        newsView.feedListView = new MobileNewsFeedListView();
        newsView.namespace = "${n}";
        
        newsView.onSuccessfulSetup = function () {
        	$("#${n} .news-stories-container").hide();
            $.mobile.hidePageLoadingMsg();
        };
        newsView.onSuccessfulRetrieval = function () {
            $.mobile.hidePageLoadingMsg();
        };
        
        $(document).ready(function () {

            newsView.feedListView.bind("feedSelected", function (id) {
                $("#${n} .news-feeds-container").hide();
                $("#${n} .news-stories-container").hide();
                $("#${n}feed" + id).show();
                if (!newsView.feedDetails || newsView.feedDetails.get("id") !== id) {
                    $.mobile.showPageLoadingMsg();
                    newsView.getFeed(id);
                }
            });

            $.mobile.showPageLoadingMsg();
            newsView.setup();
            
        });
        
    });

</rs:compressJs></script>
