var upnews = upnews || {};

if (!upnews.init) {
    
    upnews.init = function ($, _, Backbone) {

    upnews.NewsView = Backbone.View.extend({
        onload: function () { },
        setup: function () {
        	var view = this;
        	
            $.ajax({
                url: view.url,
                dataType: "json",
                type: "POST",
                success: function (data) {
                	
                    // construct a news feed collection from the response
                    var feedList = new upnews.NewsFeedList();
                    $(data.feeds).each(function (idx, feed) {
                        feedList.add(new upnews.NewsFeed(feed));
                    });

                    // add empty detail view for each feed
                    view.storyContainers = {};
                    $(data.feeds).each(function (idx, feed) {
                    	var detail = new view.feedDetailViewFn();
                    	detail.$el.attr("id", view.namespace + "feed" + feed.id)
                    		.addClass("news-stories-container");
                    	$(".view-news").append(detail.$el);
                    	view.storyContainers["feed" + feed.id] = detail;
                    });
    
                    // construct a story list for the currently active feed
                    var entryList = new upnews.NewsStoryList();
                    $(data.feed.entries).each(function (idx, entry) {
                        entryList.add(new upnews.NewsStory(entry));
                    });
                    data.feed.stories = entryList;
                    data.feed.id = data.activeFeed;
                    view.feedDetails = new upnews.NewsFeedDetails(data.feed);                
                    
                    // render the feed list view
                    view.feedListView.model = feedList;
                    view.feedListView.render();
                    
                    // render the story list view
                    var activeStory = view.storyContainers["feed" + data.feed.id];
                    activeStory.model = view.feedDetails;
                    activeStory.populated = true;
                    activeStory.render();

                    // call any configured completion action
                    if (view.onSuccessfulSetup) {
                        view.onSuccessfulSetup();
                    }
                }
            });
        },
        getFeed: function (id) {
        	var view, data;        	
            view = this;

            if (!view.storyContainers["feed" + id].populated) {
	            
	            data = { "activeateNews": id };
	
	            $.ajax({
	                url: view.url,
	                dataType: "json",
	                data: data,
	                type: "POST",
	                success: function (data) {
	                    
	                    var entryList = new upnews.NewsStoryList();
	                    $(data.feed.entries).each(function (idx, entry) {
	                        entryList.add(new upnews.NewsStory(entry));
	                    });
	                    data.feed.stories = entryList;
	                    data.feed.id = data.activeFeed;

	                    var activeStory = view.storyContainers["feed" + data.activeFeed];
	                    activeStory.model = new upnews.NewsFeedDetails(data.feed);
	                    activeStory.populated = true;
	                    activeStory.render();
	                    
	                    // render default feed
	                    
	                    if (view.onSuccessfulRetrieval) {
	                        view.onSuccessfulRetrieval();
	                    }
	                }
	            });  
	            
            }
        }
    });
    
    upnews.NewsFeedList = Backbone.Collection.extend({
        model: upnews.NewsFeed
    });

    upnews.NewsFeed = Backbone.Model.extend({
        defaults: function () {
            return {
                id: null,
                name: null
            };
        }
    });
    
    upnews.NewsFeedDetails = Backbone.Model.extend({
        defaults: function () {
            return {
                stories: new upnews.NewsStoryList(),
                title: "Feed Title",
                id: 0
            };
        }
    });
    
    upnews.NewsStoryList = Backbone.Collection.extend({
        model: upnews.NewsStory
    });

    upnews.NewsStory = Backbone.Model.extend({
        defaults: function () {
            return {
                content: null,
                description: null,
                link: null,
                title: null,
                uri: null,
                authors: [ ],
                imageUrl: null,
                videoUrl: null
            };
        }
    });
    
    
    upnews.NewsFeedDetailView = Backbone.View.extend({
    	tagName: "div",
        postRender: function () { },
        render: function () {
            // render the main feed detail template
            this.$el.html(this.template(this.model.toJSON()));

            var view = this;
            this.$(".titlebar a").click(function () { view.trigger("showList"); });
            
            // add any jQM decorator classes
            this.postRender();
            return this;
        }
    });

    upnews.NewsFeedListView = Backbone.View.extend({
        postRender: function () { },
        render: function () {
            this.$el.html(this.template({ feeds: this.model.toJSON() }));
            
            this.postRender();
            return this;
        }
    });

    

    };

}