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

var news = news || {};
if (!news.init) {
    news.init = function ($, fluid) {

        // Top-level news reader component.
        // contains logic for retrieving feed data as well as
        // as subcomponent for presenting that data on the page
        fluid.defaults("news.reader", {
            url: null,
            gradeNames: ["fluid.viewComponent", "autoInit"],
            selectors: {
                feedListContainer: ".news-feeds-container",
                storyListContainer: ".news-stories-container",
                storyDetailContainer: ".story-container"
            },
            events: {
                onReady: null,
                onFeedReturn: null,
                onFeedSelect: null,
                onStorySelect: null
            },
            components: {
                feedListView: {
                    type: "news.feedListView",
                    createOnEvent: "onReady",
                    container: "{reader}.dom.feedListContainer",
                    options: {
                        model: "{reader}.model",
                        url: "{reader}.options.url",
                        events: {
                            onFeedSelect: "{reader}.events.onFeedSelect",
                            onFeedReturn: "{reader}.events.onFeedReturn"
                        }
                    }
                },
                // define a subcomponent controlling the presentation of
                // the feed data
                feedDetailView: {
                    type: "news.feedDetailView",
                    createOnEvent: "onReady",
                    container: "{reader}.dom.storyListContainer",
                    options: {
                        model: "{reader}.model",
                        events: {
                            onFeedSelect: "{reader}.events.onFeedSelect",
                            onFeedReturn: "{reader}.events.onFeedReturn",
                            onStorySelect: "{reader}.events.onStorySelect"
                        }
                    }
                },
                storyDetailView: {
                    type: "news.storyDetailView",
                    createOnEvent: "onReady",
                    container: "{reader}.dom.storyDetailContainer",
                    options: {
                        model: "{reader}.model",
                        events: {
                            onStorySelect: "{reader}.events.onStorySelect"
                        }
                    }
                }
            },
            finalInitFunction: function(that) {
                // retrieve the JSON feed data from the server
                $.get(
                    that.options.url, 
                    function(data) {
                        // set the component data model to the returned feed data
                        that.model = data;
                        // fire an even indicating that the component has finished initializing
                        // and the subcomponent can now be created
                        that.events.onReady.fire();
                        // show the populated story container div
                        that.locate("feedListContainer").show();
                    }, 
                    "json"
                );
            }
        });
    
        fluid.defaults("news.feedListView", {
            gradeNames: ["fluid.rendererComponent", "autoInit"],
            renderOnInit: true,
            // jQuery selectors mapping HTML nodes to the renderer
            selectors: {
                feed: ".news-feed",
                feedLink: ".news-feed-link",
                feedTitle: ".news-feed-title"
            },
            listeners: {
                hideFeed: function(that) {
                    that.hideList();
                },
                showFeed: function(that) {
                    that.showList();
                }
            },
            events: {
                onFeedSelect: null,
                onFeedReturn: null,
                showFeed: {
                    event: "onFeedReturn",
                    args: [ "{feedListView}" ]
                },
                hideFeed: { 
                    event: "onFeedSelect",
                    args: [ "{feedListView}" ]
                }
            },
            repeatingSelectors: [ "feed" ],
            // renderer proto-tree defining how data should be bound
            protoTree: {
                expander: {
                    type: "fluid.renderer.repeat",
                    repeatID: "feed",
                    controlledBy: "feeds",
                    pathAs: "feed",
                    tree: {
                        feedTitle: { value: "${{feed}.name}" },
                        feedLink: { target: "javascript:;" }
                    }
                }
            },
            finalInitFunction: function(that) {
                $(that.options.selectors.feedLink).live('click', function () {
                    var feedIndex, feed, feedId;
                    
                    // get the feed object matching this link
                    feedIndex = $(this).index(that.options.selectors.feedLink);
                    feedId = that.model.feeds[feedIndex].id;
                    
                    if (feedId !== that.model.activeFeed) {
                        console.log("updating");
                        $.ajax({
                            url: that.options.url, 
                            data: { activeateNews: feedId },
                            dataType: "json",
                            success: function(data) {
                                // set the component data model to the returned feed data
                                that.model = data;
                                // fire the feed selection event, passing this feed to the event
                                that.events.onFeedSelect.fire(that.model.feed);
                            }
                        });
                    } else {
                        // fire the feed selection event, passing this feed to the event
                        that.events.onFeedSelect.fire(that.model.feed);
                    }
                    
                });
                
                that.hideList = function () {
                    $(that.container).hide();
                };
                
                that.showList = function () {
                    $(that.container).show();
                };
            }
        });
    
        // Feed view subcomponent
        fluid.defaults("news.feedDetailView", {
            gradeNames: ["fluid.rendererComponent", "autoInit"],
            renderOnInit: false,
            // jQuery selectors mapping HTML nodes to the renderer
            selectors: {
                feedTitle: ".news-feed-title",
                backLink: ".news-reader-back-link",
                story: ".news-story",
                storyLink: ".news-story-link",
                storyTitle: ".news-story-title",
                storySummary: ".news-story-summary"
            },
            events: {
                onFeedSelect: null,
                onFeedReturn: null,
                onStoryReturn: null,
                showDetails: { 
                    event: "onFeedSelect",
                    args: [ "{feedDetailView}", "{arguments}.0" ]
                },
                hideDetails: {
                    event: "onFeedReturn",
                    args: [ "{feedDetailView}" ]
                }
            },
            listeners: {
                showDetails: function(that, feed) {
                    that.showDetails(feed);
                },
                hideDetails: function(that) {
                    that.hideDetails();
                }
            },
            repeatingSelectors: [ "story" ],
            // renderer proto-tree defining how data should be bound
            protoTree: {
                feedTitle: { value: "${feed.title}" },
                backLink: { target: "javascript:;" },
                expander: {
                    type: "fluid.renderer.repeat",
                    repeatID: "story",
                    controlledBy: "feed.entries",
                    pathAs: "story",
                    tree: {
                        storyTitle: { value: "${{story}.title}" },
                        storyLink: { target: "${{story}.link}" },
                        storySummary: { value: "${{story}.description}" }
                    }
                }
            },
            finalInitFunction: function(that) {
                $(that.options.selectors.storyLink).live('click', function () {
                    var storyIndex, story;
                    
                    // get the feed object matching this link
                    storyIndex = $(this).index(that.options.selectors.feedLink);
                    story = that.model.entries[storyIndex];
                    
                    // fire the feed selection event, passing this feed to the event
                    that.events.onStorySelect.fire(story);
                });
                
                that.showDetails = function (feed) {
                    that.model.feed = feed;
                    that.refreshView();
                    $(that.container).show();
                };
                
                that.hideDetails = function () {
                    $(that.container).hide();
                };
                
                $(that.options.selectors.backLink).live('click', function () {
                    that.events.onFeedReturn.fire();
                });         
            }
        });
    
        // Feed view subcomponent
        fluid.defaults("news.storyDetailView", {
            gradeNames: ["fluid.rendererComponent", "autoInit"],
            renderOnInit: true,
            // jQuery selectors mapping HTML nodes to the renderer
            selectors: {
                storyContent: ".story-content"
            },
            events: {
                onStorySelect: null,
                onStoryReturn: null,
                showStory: { 
                    event: "onStorySelect",
                    args: [ "{storyDetailView}", "{arguments}.0" ]
                },
                hideStory: { 
                    event: "onStoryReturn",
                    args: [ "{storyDetailView}" ]
                }
            },
            listeners: {
                showStory: function(that, feed) {
                    that.showStory(feed);
                },
                hideStory: function(that) {
                    that.hideStory();
                }
            },
            repeatingSelectors: [ "story" ],
            // renderer proto-tree defining how data should be bound
            protoTree: {
                expander: {
                    type: "fluid.renderer.repeat",
                    repeatID: "story",
                    controlledBy: "entries",
                    pathAs: "story",
                    tree: {
                        storyContent: { value: "${{story}.description}" }
                    }
                }
            },
            finalInitFunction: function(that) {
                that.showStory = function (story) {
                    $(that.container).show();
                };
                
                that.hideStory = function () {
                    $(that.container).hide();
                };
                
                $(that.options.selectors.backLink).live('click', function () {
                    that.events.onStoryReturn.fire();
                });         
            }
        });
    };
        
};