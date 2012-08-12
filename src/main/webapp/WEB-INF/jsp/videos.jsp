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

<rs:aggregatedResources path="${ usePortalJsLibs ? '/skin-shared.xml' : '/skin.xml' }"/>

<div id="${n}" class="portlet">
<div class="fl-pager">
    <div class="view-pager flc-pager-top portlet-section-options">
        <ul id="pager-top" class="fl-pager-ui">
          <li class="flc-pager-previous"><a href="#">&lt; <spring:message code="previous"/></a></li>
          <li style="display:none">
            <ul class="fl-pager-links flc-pager-links" style="margin:0; display:inline">
              <li class="flc-pager-pageLink"><a href="javascript:;">1</a></li>
              <li class="flc-pager-pageLink-disabled">2</li>
              <li class="flc-pager-pageLink"><a href="javascript:;">3</a></li>
            </ul>
          </li>
          <li class="flc-pager-next"><a href="#"><spring:message code="next"/> &gt;</a></li>
          <li style="display:none">
            <span class="flc-pager-summary"><spring:message code="show"/></span>
            <span> <select class="pager-page-size flc-pager-page-size">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
            </select></span> <spring:message code="per.page"/>
          </li>
        </ul>
    </div><!-- end: portlet-section-options -->
    
    <div class="videos">
        <div class="video">
            <h3><a href="javascript:;" class="video-title"></a></h3>
            <img class="img"/>
            <p class="description"></p>
        </div>
    </div>
    </div>
</div>

<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);
    ${n}._ = _.noConflict();
    ${n}.Backbone = Backbone.noConflict();
    ${n}.fluid = fluid;
    fluid = null;
    fluid_1_4 = null;
    ${n}.jQuery(document).ready(function(){
        var $ = ${n}.jQuery;
        var fluid = ${n}.fluid;
        
        var videos = [];
        <c:forEach items="${ feed.entries }" var="entry">
        videos.push({ title: '<spring:escapeBody javaScriptEscape="true">${ entry.title }</spring:escapeBody>', description: '<spring:escapeBody javaScriptEscape="true">${ entry.description }</spring:escapeBody>', imageUrl: '<spring:escapeBody javaScriptEscape="true">${ entry.imageUrl }</spring:escapeBody>', link: '<spring:escapeBody javaScriptEscape="true">${ entry.link }</spring:escapeBody>' });
        </c:forEach>
        
        var cutpoints = [
            { id: "video:", selector: ".video" },
            { id: "title", selector: ".video-title" },
            { id: "description", selector: ".description" },
            { id: "image", selector: ".img" }
        ];
        
        var columnDefs = [
            {
                key: "title",
                valuebinding: "*.title",
                components: {
                    target: '${"${*.link}"}',
                    linktext: '${"${*.title}"}'
                }
            },
            {
                key: "description",
                valuebinding: "*.description"
            },
            {
                key: "image",
                valuebinding: "*.img",
                components: function (row) {
                    return {
                        decorators: [{ type: "attrs", attributes: { src: row.imageUrl } }]
                    };
                }
            }
        ];
                              
        var pagerOptions = {
            dataModel: videos,
            annotateColumnRange: "title",
            columnDefs: columnDefs,
            bodyRenderer: {
                type: "fluid.pager.selfRender",
                options: {
                    selectors: {
                        root: ".videos"
                    },
                    row: "video:",
                    renderOptions: {
                        cutpoints: cutpoints
                    }
                }
                
            },
            pagerBar: {
                type: "fluid.pager.pagerBar", 
                options: {
                    pageList: {
                        type: "fluid.pager.renderedPageList",
                        options: { 
                            linkBody: "a"
                        }
                    }
                }
            }
        };
        
        // initialize the pager and set it to 6 items per page.
        var pager = fluid.pager($("#${n}"), pagerOptions);
        pager.events.initiatePageSizeChange.fire(1);

    }); 
</rs:compressJs></script>
