<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<link href="<c:url value="/css/newsreader.css"/>" rel="stylesheet" type="text/css" />

<c:set var="n"><portlet:namespace/></c:set>

<style>
    #${n} .loading {
        width:100%;
        min-height: 20px;
        background: #ffffff url('<c:url value="/images/loading.gif"/>') no-repeat center;
        background-size: contain;
        font-size: smaller;
        text-align: center;
        padding-top: 2px;
        padding-bottom: 2px;
    }
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

<div id="${n}" class="container-fluid newsreader-container">

    <div class="row">
        <div class="news-reader-feed-list newsreader-content view-news col-md-12">
            <c:choose>
                <c:when test="${ feedView == 'select' }">
                    <select class="news-feeds-container"></select>
                </c:when>
                <c:otherwise>
                    <ul class="news-feeds-container"></ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="row newsreader-portlet-toolbar">
        <div class="col-md-12 no-col-padding">
            <div class="nav-links">
                <c:if test="${supportsHelp}">
                    <a href="<portlet:renderURL portletMode='help'/>"><i class="fa fa-info-circle"></i> <spring:message code="help" /></a>
                </c:if>
                <c:if test="${supportsEdit && !isGuest}">
                    &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode='edit'/>"><i class="fa fa-edit"></i> <spring:message code="edit.news.feed" /></a>
                </c:if>
                <c:if test="${isAdmin}">
                    &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>"><i class="fa fa-cog"></i> <spring:message code="administration" /></a>
                </c:if>
            </div>
        </div>
    </div>

</div>

<script type="text/template" id="${n}feed-list-template">

    {{#each this}}
    <c:choose>
        <c:when test="${ feedView == 'select' }">
            <option value="{{id}}">{{name}}</option>
        </c:when>
        <c:when test="${ feedView == 'all' }">
            <option value="{{id}}">{{name}}</option>
        </c:when>
        <c:otherwise>
            <li id="${n}feed{{id}}-tab"><a href="#${n}feed{{id}}">{{name}}</a></li>
        </c:otherwise>
    </c:choose>
    {{/each}}
</script>

<c:choose>
    <c:when test="${ storyView == 'flyout' }">
        <script type="text/template" id="${n}feed-detail-template">
                <div>
                    <div class="titlebar portlet-titlebar">
                        <h3 class="feed-title">{{title}}</h3>
                    </div>
                </div>
                <ul class="news-stories feed">
                {{{news_stories entries}}}
                </ul>
        </script>

        <script type="text/template" id="${n}news-story-template">
            {{#each this}}
                <li>
                    <a href="{{link}}" title="{{description}}" ${ newWindow ? "target=\"_blank\"" : "" }>{{title}}</a>
                </li>
            {{/each}}
        </script>
    </c:when>
    <c:otherwise>
        <script type="text/template" id="${n}feed-detail-template">
                <div>
                    <div class="titlebar portlet-titlebar">
                        <h3 class="feed-title">{{title}}</h3>
                    </div>
                </div>
                <div class="news-stories feed">
                    {{{news_stories entries}}}
                </div>
        </script>
        <script type="text/template" id="${n}news-story-template">
            {{#each this}}
              <div>
                <h3 class="feed-title">
                    <a href="{{link}}" ${ newWindow ? "target=\"_blank\"" : "" }>{{title}}</a>
                </h3>
                {{#if pubDate}}
                <p class="newsreader-pubdate">{{pubDate}}</p>
                {{/if}}
                <p>{{description}}</p>
              </div>
            {{/each}}
        </script>

    </c:otherwise>
</c:choose>

<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>
<script type="text/javascript"><rs:compressJs>
    ${n}.jQuery(function() {

        var $, Handlebars, newsView, upnews;

        $ = ${n}.jQuery;
        Handlebars = ${n}.Handlebars;
        upnews = ${n}.upnews;


        var newsStoryTemplate = Handlebars.compile($("#${n}news-story-template").html())
        Handlebars.registerHelper('news_stories', function(entries) {
            return newsStoryTemplate(entries);
        });

        var adjustToolTipBasedOnSize = function () {
            <c:if test="${ storyView == 'flyout' }">
                var tooltipPosition = { offset: "15 15" , collision: "fit" };

                // If there is not enough width in the window to display as a flyout, switch to display the
                // tooltip under the news item.  Set collision to none instead of 'fit' because fit has a flickering
                // display when the tip can't cleanly display under the news item (at least with jQuery 1.8.13).
                // This is still not perfect; at least with jQuery 1.8.13 if the width of the window is small and the
                // tip flows to a smaller width, the tip can overlap the top of the news item and cause flicker if the
                // cursor is underneath the tip.
                if (window.innerWidth < 400 * 2) {
                    tooltipPosition = { my: "left bottom", at: "left top", collision: "none", offset: "0 -20"}
                }
                $(".news-stories li a").tooltip({
                    showURL: false,
                    position: tooltipPosition
                });
            </c:if>
        };

        <c:if test="${ storyView == 'flyout' }">
                $(window).resize(function() {
                    adjustToolTipBasedOnSize();
                });
        </c:if>


        newsView = $.extend(upnews.NewsView, {
            newsService: new upnews.newsService("${feedUrl}"),
            onSuccessfulSetup: function () {
                if (${feedView  eq 'select'}) {
                    // set the current news feed to selected in the select menu
                    $("#${n} option").removeAttr("selected");
                    $("#${n} option[value=" + newsView.newsService.getActiveFeed() + "]").attr("selected", "selected");

                    // event handler for select menu
                    $("#${n} select").change(function () {
                        var id = $(this).val();
                        $(newsView.feedListView).trigger("feedSelected", id);
                        $("#${n} .news-stories-container").hide();
                        $("#${n}feed" + id).show();
                    });
                } else {
                    // compute the index of the currently selected feed
                    var index = $("#${n} .news-stories-container").index($("#${n}feed" + newsView.newsService.getActiveFeed()));
                    // initialize the jQueryUI tabs widget and set the initially
                    // selected tab
                    $("#${n} .view-news").tabs({
                        activate: function (event, ui) {
                            var id = ui.newPanel[0].id.split("feed")[1];
                            $(newsView.feedListView).trigger("feedSelected", id);
                        },
                        active: index
                    });
                }

            },
            feedDetailView: $.extend(upnews.NewsFeedDetailView, {
                template: Handlebars.compile($("#${n}feed-detail-template").html()),
                postRender: adjustToolTipBasedOnSize,
                loader: function(id) {
                    var view = this;
                    var deferred = $.Deferred();

                    var loadingDiv = $("<div class='loading'></div>");
                    view.$el.append(loadingDiv);

                    newsView.newsService.getFeed(id, view.page).done(function(feed) {
                        loadingDiv.remove();
                        if (!feed) {
                            $('.news-stories', view.$el).append(newsView.newsService.getErrorMessage());
                            deferred.resolve({page: view.page, success: false});
                        } else if (feed.entries.length > 0) {
                            $('.news-stories', view.$el).append(newsStoryTemplate(feed.entries));
                            deferred.resolve({page: view.page, success: true});
                        } else if (view.page === 0) {
                            $('.news-stories', view.$el).append("No stories for this feed");
                            deferred.resolve({page: view.page, success: false});
                        }
                    });

                    return deferred.promise();
                }
            }),
            feedListView: $.extend(upnews.NewsFeedListView, {
                $el: $("#${n} .news-feeds-container"),
                template: Handlebars.compile($("#${n}feed-list-template").html())
            }),
            namespace: "${n}"
        });

        $(document).ready(function() {

            $(newsView.feedListView).bind("feedSelected", function(event, id) {
                if (newsView.newsService.getActiveFeed() !== id) {
                    newsView.getFeed(id);
                }
            });

            newsView.setup("${param.activeFeed}");

        });

    });
</rs:compressJs></script>
