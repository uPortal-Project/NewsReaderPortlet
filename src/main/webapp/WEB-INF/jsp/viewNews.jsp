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
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.5/jquery-1.5.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.8/jquery-ui-1.8.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/fluid/1.3/js/fluid-all-1.3.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/scripts/news-feed-view.min.js"/>"></script>




<style>
    ul.news-list li { padding-bottom:0.5em; list-style-image:url('<c:url value="/images/bullet_feed.png"/>');  }
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
    
<c:set var="storyView">${renderRequest.preferences.map['storyView'][0]}</c:set>
<script type="text/javascript">
    var newsReaderPortlet = newsReaderPortlet || {};
    newsReaderPortlet.jQuery = jQuery.noConflict(true);
    newsReaderPortlet.fluid = fluid;
    fluid = null;
    fluid_1_3 = null;
    newsReaderPortlet.jQuery(function(){
        var $ = newsReaderPortlet.jQuery;

        $(document).ready(function(){
            var options = {
                url: '<portlet:resourceURL/>',
                namespace: '${n}',
                feedView: "${renderRequest.preferences.map['feedView'][0]}",
                summaryView: "${ storyView == 'scroll-summaries' ? 'full' : 'flyout' }",
                newWindow: ${renderRequest.preferences.map['newWindow'][0]},
                scrolling: ${ storyView == 'scroll-summaries' ? true : false }
            };
            newsreader.MultipleFeedView("#${n}newsContainer", options);
        });

    });
</script>

<div class="org-jasig-portlet-newsreader">

    <div id="${n}newsContainer">Loading . . . </div>
       
    <br/>
    <p>
        <c:if test="${supportsHelp}">
            <a href="<portlet:renderURL portletMode='help'/>">Help</a>
        </c:if>
        <c:if test="${supportsEdit}">
            &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode='edit'/>">Edit news feeds</a>
        </c:if>
        <c:if test="${isAdmin}">
                &nbsp;|&nbsp;<a href="<portlet:renderURL portletMode="edit"><portlet:param name="action" value="administration"/></portlet:renderURL>">
                    News Administration</a>
        </c:if>
    </p>
</div>
