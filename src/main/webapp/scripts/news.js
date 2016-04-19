/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// 'upnews' is only used in viewNews.jsp
var upnews = {};

(function() {

    upnews.init = function($, Handlebars) {

        $.fn.infiniteScroll = function(options) {

            var settings = $.extend({
                // These are the defaults.
                autoLoad: false,
                contentLoad: function() {
                    var deferred =  $.Deferred();
                    deferred.resolve('<h1>LOADED</h1>');
                    return deferred.promise();
                }
            }, options);

            if (settings.autoLoad) $(this).scroll();

            $(this).css({'position':'relative'});
            return this.each(function() {

                this.on = true;
                var that = this;
                $(this).scroll(function() {
                    var height = $(this).outerHeight();
                    if (this.on) {
                        var topOfLastItemRoundedDown = Math.floor($(':last', this).position().top);
                        //console.log(height + ' :: ' + topOfLastItemRoundedDown + ' (content will be appended if the 2nd number <= the 1st)');
                        if (topOfLastItemRoundedDown <= height) {
                            this.on = false;
                            settings.contentLoad().done(function() {
                                that.on = true;
                            });
                        }
                    }
                });

            });
        };

        /*
         * News Service 'class' that takes a URL
         * and maintains currentPage and activeFeed(Cached).
         */
        upnews.newsService = function(url) {

            var promise = null, activeFeedCache = null, currentPage = 0, message = "";

            /*
             * Checks if the parameters match the current values.
             * If not, then it refreshes them with an AJAX call.
             *
             * Intended to be called only by getFeeds() and getFeed().
             */
            var getNewsData = function(activeFeed, page) {
                if (promise == null || activeFeed != activeFeedCache || page != currentPage) {
                    var data = {};
                    if (activeFeed)
                        data.activeateNews = activeFeed;
                    data.page = page ? page : 0;
                    promise = $.ajax({
                        url: url,
                        dataType: 'json',
                        data: data,
                        type: 'POST'
                    }).done(function(data) {
                        //console.log(data);
                        activeFeedCache = data.activeFeed;
                        currentPage = data.page;
                        message = data.message ? data.message : "";
                    });
                }
                return promise;
            };

            /*
             * Calls getNewsData() and returns 'feeds' from the JSON
             * returned from the AJAX call.
             *
             * Active feed is set to parameter and page is set to default of 0.
             */
            this.getFeeds = function(activeFeed) {
                var deferred = $.Deferred();
                getNewsData(activeFeed).done(function(data) {
                    deferred.resolve(data.feeds);
                });
                return deferred.promise();
            };

            /*
             * Calls getNewsData() and returns 'feed' from the JSON
             * returned from the AJAX call.
             *
             * Active feed and page are set to parameters.
             */
            this.getFeed = function(feed, page) {
                var deferred = $.Deferred();
                getNewsData(feed, page).done(function(data) {
                    deferred.resolve(data.feed);
                });
                return deferred.promise();
            };

            this.getPage = function() {
                return currentPage;
            };

            this.getActiveFeed = function() {
                return activeFeedCache;
            };

            this.getErrorMessage = function() {
                return message;
            };

        };

        /*
         * "controller" for the view.
         *
         * Extended in viewNews.jsp.
         */
        upnews.NewsView = {
            onload: function() {
            },

            /*
             * Called in document.ready() with initial feed
             */
            setup: function(activeFeed) {
                var view = this;

                /*
                 * Gets all feed IDs, creates divs for each,
                 * sets up initial feed and calls render().
                 */
                return this.newsService.getFeeds(activeFeed)
                        .done(function(feeds) {
                            // add empty detail view for each feed
                            view.storyContainers = {};
                            $(feeds).each(function(idx, feed) {
                                var detail = $.extend({}, view.feedDetailView, {$el: $('<div/>')});
                                detail.$el.attr('id', view.namespace + 'feed' + feed.id)
                                        .addClass('news-stories-container');
                                $('#' + view.namespace + ' .view-news').append(detail.$el);
                                view.storyContainers['feed' + feed.id] = detail;
                            });
                            // render the feed list view
                            view.feedListView.render(feeds);

                        })
                        .done(function() {
                            view.getFeed(view.newsService.getActiveFeed(), 0).done(function() {
                                // if function defined in JSP, call post setup function
                                // Note: this check is not necessary since function is defined in viewNews.jsp
                                if (view.onSuccessfulSetup) view.onSuccessfulSetup();
                            });
                        });
            },

            /*
             * Checks if div has been populated. If not, it calls NewsService.getFeed().
             * Sets up the new feed in the view.
             */
            getFeed: function(id, page) {
                var view = this;
                if (!view.storyContainers['feed' + id].populated) {
                    return view.newsService.getFeed(id, page).done(function(feed) {
                        if (!feed) {
                            console.log("No feed object: " + id);
                            feed = {};
                            feed.title = "Error";
                            feed.id = view.newsService.getActiveFeed();
                            var activeStory = view.storyContainers['feed' + feed.id];
                            activeStory.$el.html("<div>" + view.newsService.getErrorMessage() + "</div>");
                            return;
                        }
                        feed.id = view.newsService.getActiveFeed();
                        // render the story list view
                        var activeStory = view.storyContainers['feed' + feed.id];
                        activeStory.populated = true;
                        activeStory.render(feed);
                    })
                    .done(function() {
                        // if function defined in JSP, call it (it is not defined in viewNews.jsp)
                        if (view.onSuccessfulRetrieval) {
                            view.onSuccessfulRetrieval();
                        }
                    });

                }
            }
        };

        /*
         * Detail view of feed -- list of stories.
         *
         * Extended in viewNews.jsp.
         */
        upnews.NewsFeedDetailView = {
            $el: $('<div/>'),
            postRender: function() {
            },
            page: 1,
            render: function(feed) {
                // render the main feed detail template
                this.$el.html(this.template(feed));
                var view = this;
                $('.titlebar a', this.$el).click(function() {
                    $(view).trigger('showList');
                });
                $('.news-stories', this.$el).infiniteScroll({
                    autoLoad: true,
                    contentLoad: function() {
                        return view.loader(feed.id).done(function(result) {
                            if (result.success)
                                view.page = result.page+1;
                        });
                    }
                });

                // add any jQM decorator classes
                this.postRender();
                return this;
            }
        };

        /*
         * News feeds list view (tabs or dropdown) that is extended in viewNews.jsp.
         */
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

}());
