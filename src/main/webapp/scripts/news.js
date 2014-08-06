/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var upnews = upnews || {};

if (!upnews.init) {

    upnews.init = function($, Handlebars) {

        upnews.newsService = function(url) {

            var promise = null, activeFeedCache = null, currentPage = 0, pages = 1;
            var getNewsData = function(activeFeed, page) {
                if (promise == null || activeFeed != activeFeedCache || page != currentPage) {
                    var data = {};
                    if (activeFeed)
                        data.activeateNews = activeFeed;
                    data.page = page ? page : 0;
                    promise = $.ajax({
                        url: url,
                        dataType: "json",
                        data: data,
                        type: "POST"
                    }).done(function(data) {
                        activeFeedCache = data.activeFeed;
                        currentPage = data.page;
                        pages = data.pages;
                    });
                }
                return promise;
            };

            this.getFeeds = function() {
                var deferred = $.Deferred();
                getNewsData().done(function(data) {
                    deferred.resolve(data.feeds);
                });
                return deferred.promise();
            };

            this.getFeed = function(feed, page) {
                var deferred = $.Deferred();
                getNewsData(feed, page).done(function(data) {
                    deferred.resolve(data.feed);
                });
                return deferred.promise();
            };

            this.getPage = function() {
                return {
                    current: currentPage,
                    total: pages
                };
            };
            this.getActiveFeed = function() {
                return activeFeedCache;
            }

        };

        upnews.NewsView = {
            onload: function() {
            },
            setup: function() {
                var view = this;

                return this.newsService.getFeeds()
                        .done(function(feeds) {
                            // add empty detail view for each feed
                            view.storyContainers = {};
                            $(feeds).each(function(idx, feed) {
                                var detail = $.extend({}, view.feedDetailView, {$el: $("<div/>")});
                                detail.$el.attr("id", view.namespace + "feed" + feed.id)
                                        .addClass("news-stories-container");
                                $(".view-news").append(detail.$el);
                                view.storyContainers["feed" + feed.id] = detail;
                            });
                            // render the feed list view
                            view.feedListView.render(feeds);

                        })
                        .done(function() {
                            view.getFeed(view.newsService.getActiveFeed()).done(function() {
                                if (view.onSuccessfulSetup) view.onSuccessfulSetup();
                            });
                        });
            },
            getFeed: function(id) {
                var view = this;
                if (!view.storyContainers["feed" + id].populated) {
                    return view.newsService.getFeed(id).done(function(feed) {
                        feed.id = view.newsService.getActiveFeed();
                        // render the story list view
                        var activeStory = view.storyContainers["feed" + feed.id];
                        activeStory.populated = true;
                        activeStory.render(feed);
                    })
                    .done(function() {
                        if (view.onSuccessfulRetrieval) {
                            view.onSuccessfulRetrieval();
                        }
                    });

                }
            }
        };

        upnews.NewsFeedDetailView = {
            $el: $("<div/>"),
            postRender: function() {
            },
            render: function(feed) {
                // render the main feed detail template
                this.$el.html(this.template(feed));
                var view = this;
                $(".titlebar a", this.$el).click(function() {
                    $(view).trigger("showList");
                });
                // add any jQM decorator classes
                this.postRender();
                return this;
            }
        };

        upnews.NewsFeedListView = {
            postRender: function() {
            },
            render: function(feeds) {
                this.$el.html(this.template(feeds));
                this.postRender();
                return this;
            }
        };

    };

}