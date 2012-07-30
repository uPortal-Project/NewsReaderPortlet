var upnews = upnews || {};

(function($, _, Backbone){

    upnews.NewsView = Backbone.View.extend({
        onload: function () { },
        getFeed: function (id) {
            var view = this;

            var data = {};
            if (id) {
                data.activeateNews = id;
            }

            $.ajax({
                url: view.url,
                dataType: "json",
                data: data,
                type: "POST",
                success: function (data) {
                    // construct a news feed collection from the response
                    var feedList = new upnews.NewsFeedList();
                    $(data.feeds).each(function (idx, feed) {
                        feedList.add(new upnews.NewsFeed(feed));
                    });
                    
                    var entryList = new upnews.NewsStoryList();
                    $(data.feed.entries).each(function (idx, entry) {
                        entryList.add(new upnews.NewsStory(entry));
                    });
                    data.feed.stories = entryList;
                    data.feed.id = data.activeFeed;
                    view.feedDetails = new upnews.NewsFeedDetails(data.feed);                
                    
                    // set the new collection as the view model and render
                    view.feedListView.model = feedList;
                    view.feedListView.render();
    
                    view.feedDetailView.model = view.feedDetails;
                    view.feedDetailView.render();
                    
                    if (view.onSuccessfulRetrieval) {
                        view.onSuccessfulRetrieval();
                    }
                }
            });  
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
                title: "Feed Title"
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
            this.$(".news-feed-list").html(this.template({ feeds: this.model.toJSON() }));
            
            this.postRender();
            return this;
        }
    });

    
})( up.jQuery, up._, up.Backbone );