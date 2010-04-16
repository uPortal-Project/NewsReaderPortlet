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

(function($, fluid) {
    
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
            type: "POST",
            dataType: "json",
            async: false,
            data: data,
            success: function(response, textStatus){
                feedResult = response;
            }
        });
        return feedResult;
    };
    
    var initSingle = function(feedResult, that) {
        that.container.append($(document.createElement('div')).attr('id', that.options.namespace + 'feed' + feedResult.activeFeed));        
        displayFeed(feedResult, that);
    };    

    
    /**
     * Initialized a tabbed multi-feed view
     */
    var initTabs = function(feedResult, that) {
        
        // create the tabs and add them to our container
        var tabs = $(document.createElement('ul'));
        that.container.append(tabs);
        
        // add a tab and div for each feed
        var selected = 0;
        for (var i = 0; i < feedResult.feeds.length; i++) {
            var li = $(document.createElement('li')).append(
                $(document.createElement('a')).append(
                    $(document.createElement('span')).text(feedResult.feeds[i].name)
                ).attr('href', '#' + that.options.namespace + 'feed' + feedResult.feeds[i].id)
            );
            tabs.append(li);
            that.container.append($(document.createElement('div')).attr('id', that.options.namespace + 'feed' + feedResult.feeds[i].id));
            if (feedResult.feeds[i].id == feedResult.activeFeed) selected = i;
        }
        that.initializedFeeds.push(Number(feedResult.activeFeed));
        
        // display the active news feed
        displayFeed(feedResult, that);
        
        // initialize the jQuery tabs and bind and update method
        that.container.tabs({ selected: selected });
        that.container.bind('tabsshow', function(event, ui) {
            var newFeedId = ui.panel.id.split('feed')[1];
            updateFeed(newFeedId, that);
        });
        
    };    

    /**
     * Initialized a select menu multi-feed view
     */
    var initSelect = function(feedResult, that) {
        
        // create the select menu and add it to our container
        var select = $(document.createElement('select'));
        that.container.append(select);
        
        // create a select menu option and div for each feed
        for (var i = 0; i < feedResult.feeds.length; i++) {
            var feed = feedResult.feeds[i];
            select.get(0).options[i] = new Option(feed.name, feed.id);
            that.container.append(
                $(document.createElement('div')).addClass('story-div')
                    .attr('id', that.options.namespace + 'feed' + feedResult.feeds[i].id));
        }
        // pre-select the currently active feed
        select.val(feedResult.activeFeed);
        that.initializedFeeds.push(Number(feedResult.activeFeed));
        
        // display the active news feed
        displayFeed(feedResult, that);
        
        // bind the update method to the select menu
        select.change(function(){
            var newFeedId = $(this).val();
            that.container.find('.story-div').css("display", "none");
            $('#' + that.options.namespace + 'feed' + newFeedId).css("display", "block");
            updateFeed(newFeedId, that);
        });

    };
    
    /**
     * Update and a news feed
     */
    var updateFeed = function(feedId, that) {
        
        // if this feed is already initialized, we don't need to download it
        // again
        if ($.inArray(Number(feedId), that.initializedFeeds) >= 0) {
            return;
        }
        
        // display a loading message in the story container
        var storyContainer = $('#' + that.options.namespace + 'feed' + feedId)
            .html('<p><strong>Loading...</strong></p>');
            
        // get the new feed and add it to our initialized list
        var feedResult = retrieveFeed(that, feedId);
        that.initializedFeeds.push(Number(feedId));
        
        displayFeed(feedResult, that);
    };
    
    /**
     * Display a news feed
     */
    var displayFeed = function(feedResult, that) {
        var storyContainer = $('#' + that.options.namespace + 'feed' + feedResult.activeFeed);
        
        // if an error occurred, display the message in the story container
        if(feedResult.message != undefined) {
            storyContainer.html('<p>'+feedResult.message+'</p>');
            return;
        }

        // otherwise, display the active news feed according in the preferred style
        var feed = feedResult.feed;
        
        // create the title
        var header = '<h2><a href="'+feed.link+'" rel="popup">'+feed.title+'</a>'
        if (feed.author != undefined) header += feed.author;
        header +='</h2>';
        storyContainer.html(header);
        
        // if scrolling is set to true, add the scrolling class
        var itemsContainer = $(document.createElement('div')).addClass('news-items-container');
        if (that.options.scrolling) {
            itemsContainer.addClass('portlet-rss-scrollable-content');
        }
        storyContainer.append(itemsContainer);

        // print out the feed items in the desired format
        if (that.options.summaryView == 'flyout') {
            displayFlyoutSummaries(feedResult.activeFeed, feed, that);
        } else {
            displayFullSummaries(feedResult.activeFeed, feed, that);
        }
        
        // if the feed has a defined copyright, add it to the view
        if (feed.copyright != undefined) {
            storyContainer.append(
                $(document.createElement('p')).text(feed.copyright)
                    .addClass('news-feed-copyright')
            );
        }
        
    };
    
    /**
     * Display a feed as a flyout list
     */
    var displayFlyoutSummaries = function(feedId, feed, that) {
        
        // add a list to the story container to hold our feed items 
        var storyContainer = $('#' + that.options.namespace + 'feed' + feedId).find('.news-items-container');
        var list = $(document.createElement('ul')).addClass('news-list');
        storyContainer.append(list);
        
        // add each news feed to the list
        var html = '';
        for (var i = 0; i < feed.entries.length; i++) {
            var entry = feed.entries[i];
            html += '<li><a class="news-item" href="'+entry.link+'" rel="popup">'+entry.title+'</a>';
            html += '<span style="display:none">'+entry.description+'</span></li>';
        }
        list.html(html);
        
        // initialize the tooltips
        storyContainer.find(".news-item").tooltip({
            bodyHandler: function() { 
                return $(this).next().html(); 
            },
            showURL: false
        });
        
    };
    
    /**
     * Display a feed as a scrolling div with summaries
     */
    var displayFullSummaries = function(feedId, feed, that) {
        // add a list to the story container to hold our feed items 
        var storyContainer = $('#' + that.options.namespace + 'feed' + feedId).find('.news-items-container');

        var html = '';
        for (var i = 0; i < feed.entries.length; i++) {
            var entry = feed.entries[i];
            html += '<h3><a href="'+entry.link+'" rel="popup">'+entry.title+'</a></h3>';
            html += '<p>'+entry.description+'</p>';
        }
        storyContainer.html(html);
    };
    
    // end of private methods
    
    
    // start of creator function
    
    newsreader.FeedView = function(container, options) {
        var that = fluid.initView("newsreader.MultipleFeedView", container, options);
        
        /**
         * Initialization method
         */
        that.init = function() {
            var feeds = retrieveFeed(that);
            that.container.html("");
            initSingle(feeds, that);
        };
        
        // initialization
        that.initializedFeeds = [];
        that.init();
        
        return that;
    };

    newsreader.MultipleFeedView = function(container, options) {
        var that = fluid.initView("newsreader.MultipleFeedView", container, options);
        
        /**
         * Initialization method
         */
        that.init = function() {
            var feeds = retrieveFeed(that);
            that.container.html("");
            if (that.options.feedView == 'tabs') {
                initTabs(feeds, that);
            } else {
                initSelect(feeds, that);
            }
        };
        
        // initialization
        that.initializedFeeds = [];
        that.init();
        
        return that;
    };

    //end of creator function
    
    
    //start of defaults

    fluid.defaults("newsreader.FeedView", {
        feedView: 'select',
        summaryView: 'full',
        scrolling: false,
        url: null,
        namespace: null
    });

    fluid.defaults("newsreader.MultipleFeedView", {
        feedView: 'select',
        summaryView: 'full',
        scrolling: false,
        url: null,
        namespace: null
    });

    // end of defaults

})(jQuery, fluid_1_1);
