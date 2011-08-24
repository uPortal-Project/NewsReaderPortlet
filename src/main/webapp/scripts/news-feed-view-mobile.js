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

var newsreader = newsreader || {};
if (!newsreader.init) {
    newsreader.init = function ($, fluid) {
    
    // start of private methods

    /**
     * Retrieve a news feed from the server
     */
    var retrieveFeed = function(that, feedId) {
        var data = {};
        if (feedId) data['activeateNews'] = feedId;
        var feedResult;
        $.ajax({
            url: that.options.url,
            type: "GET",
            dataType: "json",
            async: false,
            data: data,
            success: function(response, textStatus){
                feedResult = response;
            }
        });
        return feedResult;
    };

    // end of private methods
    
    
    // start of creator function

    newsreader.MobileFeedListView = function(container, options) {
        var that = fluid.initView("newsreader.MobileFeedListView", container, options);
        that.views = {};
        that.state = {};

        that.views.storyList = fluid.initSubcomponent(that, "storyListView", [container, that, fluid.COMPONENT_OPTIONS]);        
        that.views.storyContent = fluid.initSubcomponent(that, "storyContentView", [container, that, fluid.COMPONENT_OPTIONS]);        

        var cutpoints = [
            { id: "feed:", selector: that.options.selectors.feed },
            { id: "title", selector: that.options.selectors.title },
            { id: "link", selector: that.options.selectors.link }
        ];

        var feedResult = retrieveFeed(that);
        var tree = { children: [] };
        $(feedResult.feeds).each(function (idx, feed) {
            tree.children.push({
                ID: "feed:",
                children: [
                    { ID: "title", value: feed.name },
                    {
                        ID: "link",
                        decorators: [
                            { type: "jQuery", func: "click", args: function () {
                                    return that.showFeed(feed); 
                                } 
                            }
                        ]
                    }
                ]
            });
        });
        
        that.showFeed = function (feed) {
            var feedResult = retrieveFeed(that, feed.id);
            feedResult.feed.title = feed.name;
            that.views.storyList.showFeed(feedResult.feed);
            return false;
        };
        
        fluid.selfRender($(that.locate("feedList")), tree, { cutpoints: cutpoints });
        
        return that;
    };

    newsreader.MobileStoryListView = function(container, overallThat, options) {
        var that = fluid.initView("newsreader.MobileStoryListView", container, options);
        that.state = {};

        var storyCutpoints = [
            { id: "story:", selector: that.options.selectors.story },
            { id: "feedTitle", selector: that.options.selectors.feedTitle },
            { id: "title", selector: that.options.selectors.title },
            { id: "summary", selector: that.options.selectors.summary },
            { id: "link", selector: that.options.selectors.link },
            { id: "image", selector: that.options.selectors.image }
        ];

        that.showFeed = function (feed) {
            
            var tree = { children: [] };
            tree.children.push({ ID: "feedTitle", value: feed.title });
            $(feed.entries).each(function (idx, story) {
                tree.children.push({
                    ID: "story:",
                    children: [
                        { ID: "title", value: story.title },
                        { ID: "summary", markup: story.description },
                        { ID: "link", target: "javascript:;",
                            decorators: [
                                 { type: "jQuery", func: "click", args: function () {
                                         return overallThat.views.storyContent.showContent(story); 
                                     } 
                                 }
                             ]
                        },
                        { 
                            ID: "image", 
                            decorators: [{ type: "attrs", attributes: { src: story.image } }]
                        }
                    ]
                });
            });
            
            if (that.state.storyTemplates) {
                fluid.reRender(that.state.storyTemplates, $(that.locate("storyList")), tree, { cutpoints: storyCutpoints });
            } else {
                that.state.storyTemplates = fluid.selfRender($(that.locate("storyList")), tree, { cutpoints: storyCutpoints });
            }

            $(feed.entries).each(function (idx, story) {
                if (!story.image) {
                    that.locate("storyList").find("li:eq(" + idx + ")").removeClass("ui-li-has-thumb");
                }
            });

            if (overallThat.options.selectors.feedList) {
                $(overallThat.locate("feedList")).hide();
                $(that.locate("backBar")).show();
            }
            $(that.locate("storyList")).show();
            $(that.locate("backLink")).click(that.showList);

        };

        if (overallThat.options.selectors.feedList) {
            
            that.showList = function () {
                $(that.locate("storyList")).hide();
                $(that.locate("backBar")).hide();
                $(overallThat.locate("feedList")).show();
                return false;
            };
            
        }

        return that;
    };

    newsreader.MobileStoryContentView = function(container, overallThat, options) {
        var that = fluid.initView("newsreader.MobileStoryContentView", container, options);
        that.state = {};

        that.showContent = function (story) {
            if (story.videoUrl) {
                $(that.locate("contentContainer")).append(
                    $(document.createElement("video")).append(
                        $(document.createElement("source"))
                            .attr("src", story.videoUrl).attr("type", 'video/mp4; codecs="avc1.42E01E, mp4a.40.2"')
                    ).attr("controls", "controls").attr("autoplay", "autoplay")
                );
            } else if (story.content) {
                $(that.locate("content")).html(story.content);
            } else {
                window.location = story.link;
            }
            $(that.locate("contentContainer")).show();
            $(overallThat.views.storyList.locate("storyList")).hide();
            return false;
        };
        
        that.hideContent = function () {
            $(that.locate("contentContainer")).find("video").remove();
            $(that.locate("contentContainer")).hide();
            $(overallThat.views.storyList.locate("storyList")).show();
            return false;
        };

        $(that.locate("backLink")).click(that.hideContent);

        return that;
    };

    //end of creator function
    
    
    //start of defaults

    fluid.defaults("newsreader.MobileFeedListView", {
        url: null,
        storyListView: {
            type: "newsreader.MobileStoryListView"
        },
        storyContentView: {
            type: "newsreader.MobileStoryContentView"
        },
        selectors: {
            feedList: ".news-reader-feed-list",
            feed: ".news-reader-feed",
            title: ".news-reader-feed-title",
            link: ".news-reader-feed-link"
        }
    });

    fluid.defaults("newsreader.MobileStoryListView", {
        selectors: {
            backBar: ".news-reader-back-bar",
            backLink: ".news-reader-back-link",
            storyList: ".news-reader-story-list",
            story: ".news-reader-story",
            feedTitle: ".news-reader-feed-title",
            title: ".news-reader-story-title",
            summary: ".news-reader-story-summary",
            link: ".news-reader-story-link",
            image: ".news-reader-story-image"
        }
    });

    fluid.defaults("newsreader.MobileStoryContentView", {
        selectors: {
            backBar: ".news-reader-back-bar",
            backLink: ".news-reader-back-link",
            contentContainer: ".news-reader-story-container",
            content: ".news-reader-story-content",
        }
    });

    // end of defaults

    newsreader.initialized = true;
};
}