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

<style>
    ul.news-stories li { padding-bottom:0.5em; list-style-image:url('<rs:resourceURL value="/rs/famfamfam/silk/1.3/bullet_feed.png"/>');  }
    .ui-tooltip {
        padding:8px;
        position:absolute;
        z-index:9999;
        -o-box-shadow: 0 0 5px #aaa;
        -moz-box-shadow: 0 0 5px #aaa;
        -webkit-box-shadow: 0 0 5px #aaa;
        box-shadow: 0 0 5px #aaa;
        max-width: 400px;
    }
    * html .ui-tooltip { background-image: none; }
    body .ui-tooltip { border-width:2px; }
</style>
<portlet:resourceURL var="feedUrl"/>
    
<div class="org-jasig-portlet-newsreader">

    <div id="${n}">
        <div class="news-reader-feed-list portlet ptl-newsreader view-news">
            <div class="news-feeds-container">
                <div class="news-feed-list">
                </div>
            </div>
            <div class="news-stories-container">
            </div>
            <div class="story-container" style="display:none">
                <div class="titlebar portlet-titlebar">
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
       
    <br/>
    <p>
        <c:if test="${supportsHelp}">
            <a href="<portlet:renderURL portletMode='help'/>">Help</a>
        </c:if>
        <c:if test="${supportsEdit && !isGuest}">
            &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>
        </c:if>
        <c:if test="${isAdmin}">
                &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
                    News Administration</a>
        </c:if>
    </p>
</div>

<script type="text/template" id="${n}feed-list-template">
    <select>
      ${"<%"} _(feeds).each(function(feed) { ${" %>"}
        <option value="${"<%="} feed.id ${"%>"}">
            ${"<%="} feed.name ${"%>"}
        </option>
      ${"<%"} }); ${"%>"}
    </select>
</script>

<script type="text/template" id="${n}feed-detail-template">
    <div class="titlebar portlet-titlebar">
        <h2 class="title">${"<%= title %>"}</h2>
    </div>

    <c:choose>
        <c:when test="${ storyView == 'flyout' }">
            <ul class="news-stories feed">
                ${"<%"} _(entries).each(function(story) { ${" %>"}
                    <li>
                        <a href="${"<%="} story.link ${"%>"}" title="${"<%="} story.description ${"%>"}">
                            ${"<%="} story.title ${"%>"}
                        </a>
                    </li>
                ${"<%"} }); ${"%>"}
            </ul>
        </c:when>
        <c:otherwise>
            <div class="news-stories feed">
                ${"<%"} _(entries).each(function(story) { ${" %>"}
                    <h3>
                        <a href="${"<%="} story.link ${"%>"}">
                            ${"<%="} story.title ${"%>"}
                        </a>
                    </h3>
                    <p>${"<%="} story.description ${"%>"}</p>
                ${"<%"} }); ${"%>"}
            </div>
        </c:otherwise>
    </c:choose>
</script>

<c:set var="usePortalJsLibs" value="${ false }"/>
<rs:aggregatedResources path="${ usePortalJsLibs ? '/skin-shared.xml' : '/skin.xml' }"/>
<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);
    ${n}._ = _.noConflict();
    ${n}.Backbone = Backbone.noConflict();
    ${n}.fluid = null;
    
    ${n}.jQuery(function(){
        var $, _, Backbone, newsView;
        
        $ = ${n}.jQuery;
        _ = ${n}._;
        Backbone = ${n}.Backbone;
        
        var DesktopNewsFeedDetailView = upnews.NewsFeedDetailView.extend({
            el: "#${n} .news-stories-container",
            template: _.template($("#${n}feed-detail-template").html()),
            postRender: function () {
                <c:if test="${ storyView == 'flyout' }">
                    this.$("li a").tooltip({ 
                        showURL: false,
                        position: { offset: "15 15" } 
                    });
                </c:if>
            }
        });
        
        
        var DesktopNewsFeedListView = upnews.NewsFeedListView.extend({
            el: "#${n} .news-feeds-container",
            template: _.template($("#${n}feed-list-template").html()),
            postRender: function () {
                var view = this;
                
                view.$("option").removeAttr("selected");
                view.$("option[value=" + newsView.feedDetails.get("id") + "]").attr("selected", "selected");
                
                view.$("select").change(function () {
                    view.trigger("feedSelected", $(this).val());
                });
            }
        });
        
        newsView = new upnews.NewsView();
        newsView.url = "${feedUrl}";
        newsView.feedDetailView = new DesktopNewsFeedDetailView();
        newsView.feedListView = new DesktopNewsFeedListView();
        
        $(document).ready(function () {

            newsView.feedListView.bind("feedSelected", function (id) {
                if (!newsView.feedDetails || newsView.feedDetails.get("id") !== id) {
                    newsView.getFeed(id);
                }
            });
            
            newsView.getFeed();
            
        });
        
    });

</rs:compressJs></script>

