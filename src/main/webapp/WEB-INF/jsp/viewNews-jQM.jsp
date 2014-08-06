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

<c:set var="mobile" value="${ true }"/>
<jsp:directive.include file="/WEB-INF/jsp/scripts.jsp"/>

<div id="${n}">
    <div class="news-reader-feed-list portlet ptl-newsreader view-news">
    
        <div class="news-feeds-container">
            <div data-role="content" class="portlet-content">
                <div class="news-feed-list">
                </div>
                <c:if test="${supportsEdit && !isGuest}">
                    <div class="utilities">
                        <a data-role="button" href="<portlet:renderURL portletMode='edit'/>"><spring:message code="preferences"/></a>
                    </div>
                </c:if>            
            </div>
        </div>
        
<!--        <div class="story-container" style="display:none">
            <div data-role="header" class="titlebar portlet-titlebar">
                <a class="news-reader-back-link" href="javascript:;" data-role="button" data-icon="back" data-inline="true"><spring:message code="back"/></a>
                <h2 class="title story-title"><spring:message code="viewNews.storytitle"/></h2>
            </div>
            <div data-role="content" class="portlet-content">
                <div class="story-content">
                </div>
            </div>
        </div>-->
    </div>
</div>

<script type="text/template" id="${n}feed-list-template">
    <ul data-role="listview">
       {{#each this}}
            <li rel="{{id}}"><a href="javascript:;"><h3>{{name}}</h3></a></li>
       {{/each}}
    </ul>
</script>

<script type="text/template" id="${n}feed-detail-template">
    <div class="titlebar portlet-titlebar ui-header ui-bar-a" data-role="header">
        <a href="javascript:;" data-role="button" data-icon="back" data-direction="reverse" data-transition="none" class="ui-btn-left ui-btn-inline feed-back-button"><spring:message code="back"/></a>
        <h2 class="ui-title" role="heading">{{title}}</h2>
    </div>

    <!-- Module content  -->
    <div data-role="content" class="portlet-content">
        <ul class="news-stories feed" data-role="listview">
        {{#each entries}}
            <li>
                <a href="{{link}}">
                    <h3>{{title}}</h3>
                    <p>{{description}}</p>
                </a>
            </li>
        {{/each}}
        </ul>
    </div>
</script>

<script type="text/javascript"><rs:compressJs>
    
    ${n}.jQuery(function(){
        var $, Handlebars, newsView, upnews;
        
        $ = ${n}.jQuery;
        Handlebars = ${n}.Handlebars;
        upnews = ${n}.upnews;


        newsView = $.extend(upnews.NewsView, {
            newsService: new upnews.newsService("${feedUrl}"),
            onSuccessfulSetup: function () {
        	$("#${n} .news-stories-container").hide();
                $.mobile.hidePageLoadingMsg();
            },
            onSuccessfulRetrieval: function () {
                $.mobile.hidePageLoadingMsg();
            },
            feedDetailView: $.extend(upnews.NewsFeedDetailView, {
                template: Handlebars.compile($("#${n}feed-detail-template").html()),
                postRender: function () {
                    this.$el.trigger("create");
                    $(".feed-back-button", this.$el).click(function () {
                        $("#${n} .news-stories-container").hide();
                        $("#${n} .news-feeds-container").show();
                    });
                }
            }),      
            feedListView: $.extend(upnews.NewsFeedListView, {
                $el: $("#${n} .news-feed-list"),
                template: Handlebars.compile($("#${n}feed-list-template").html()),
                postRender: function () {
                    var view = this;
                    $("a", view.$el).click(function () {
                        var link, feedId;
                        link = $(this);
                        var div = $(link.parents("li").get(0));
                        feedId = div.attr("rel");
                        $(view).trigger("feedSelected", feedId);
                    });

                    this.$el.trigger("create");
                }
            }),
            namespace: "${n}"
        });
        
        $(document).ready(function () {

            $(newsView.feedListView).bind("feedSelected", function (event,id) {
                $("#${n} .news-feeds-container").hide();
                $("#${n} .news-stories-container").hide();
                $("#${n}feed" + id).show();
                if (newsView.newsService.getActiveFeed() !== id) {
                    $.mobile.showPageLoadingMsg();
                    newsView.getFeed(id);
                }
            });

            $.mobile.loading("show");
            newsView.setup();
            
        });
        
    });

</rs:compressJs></script>
