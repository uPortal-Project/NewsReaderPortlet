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

<div id="${n}">
    <div class="news-reader-feed-list portlet ptl-newsreader view-news">
    
        <div class="news-feeds-container">
            <div data-role="content" class="portlet-content">
                <div class="news-feed-list">
                </div>
                <c:if test="${supportsEdit && !isGuest}">
                    <div class="utilities">
                        <a data-role="button" href="<portlet:renderURL portletMode='edit'/>">Preferences</a>
                    </div>
                </c:if>            
            </div>
        </div>
        
        <div class="news-stories-container" style="display:none">
        </div>

        <div class="story-container" style="display:none">
            <div data-role="header" class="titlebar portlet-titlebar">
                <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true">Back</a>
                <h1 class="title story-title">Story Title</h1>
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
    <div class="titlebar portlet-wrapper-titlebar" data-role="header" data-position="inline">
        <a href="javascript:;" data-icon="home" data-direction="reverse" data-transition="none">Back</a>
        <h1 class="title">${"<%= title %>"}</h1>
    </div>

    <!-- Module content  -->
    <div data-role="content" class="portlet-content">
        <ul class="news-stories" data-role="listview">
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
        
<rs:aggregatedResources path="${ usePortalJsLibs ? '/skin-mobile-shared.xml' : '/skin-mobile.xml' }"/>
<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    <c:choose>
    <c:when test="${ !usePortalJsLibs }">
    ${n}.jQuery = jQuery.noConflict(true);
    ${n}._ = _.noConflict();
    ${n}.Backbone = Backbone.noConflict();
    </c:when>
    <c:otherwise>
    ${n}.jQuery = up.jQuery.noConflict(true);
    ${n}._ = up._;
    ${n}.Backbone = up.Backbone;
    </c:otherwise>
    </c:choose>
    
    ${n}.jQuery(function(){
        var $, _, Backbone, newsView;
        
        $ = ${n}.jQuery;
        _ = ${n}._;
        Backbone = ${n}.Backbone;
        
        var MobileNewsFeedDetailView = upnews.NewsFeedDetailView.extend({
            el: "#${n} .news-stories-container",
            template: _.template($("#${n}feed-detail-template").html()),
            postRender: function () {
                this.$el.trigger("create");
                this.$(".titlebar").header();
            }
        });
        
        var MobileNewsFeedListView = upnews.NewsFeedListView.extend({
            el: "#${n} .news-feeds-container",
            template: _.template($("#${n}feed-list-template").html()),
            postRender: function () {
                var view = this;
                view.$("a").click(function () {
                    var link, feedId;
                    link = $(this);
                    var div = $(link.parents("li").get(0));
                    var index = view.$(".news-feed-list li").index(div);
                    feedId = view.model.at(index).get("id");
                    view.trigger("feedSelected", feedId);
                });

                this.$el.trigger("create");
            }
        });

        newsView = new upnews.NewsView();
        newsView.url = "${feedUrl}";
        newsView.feedDetailView = new MobileNewsFeedDetailView();
        newsView.feedListView = new MobileNewsFeedListView();
        
        newsView.onSuccessfulRetrieval = function () {
            $.mobile.hidePageLoadingMsg();
        };
        
        $(document).ready(function () {

            newsView.feedListView.bind("feedSelected", function (id) {
                newsView.feedListView.$el.hide();
                newsView.feedDetailView.$el.show();
                if (!newsView.feedDetails || newsView.feedDetails.get("id") !== id) {
                    $.mobile.showPageLoadingMsg();
                    newsView.getFeed(id);
                }
            });
            
            newsView.feedDetailView.bind("showList", function () {
                newsView.feedDetailView.$el.hide();
                newsView.feedListView.$el.show();
            });
            
            $.mobile.showPageLoadingMsg();
            newsView.getFeed();
            
        });
        
    });

</rs:compressJs></script>
