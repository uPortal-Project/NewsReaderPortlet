<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<%--
  -- This view is selected by setting the 'viewName' portlet preference to 'videos'.
  --
  -- NOTE: This view was originally designed for use with a custom video/media RSS adapter
  -- (believed to have been institution-specific) that populated a 'feed' model attribute
  -- containing entries with imageUrl, videoUrl, title, description, and link fields.
  -- The standard RomeAdapter does NOT populate the 'feed' model attribute from this
  -- controller path (NewsController.showMainView() only returns a view name), so with
  -- a standard RSS feed the videos array will always be empty.
  --
  -- If you are using this view, you will need a custom adapter or controller that puts
  -- a 'feed' object (with a populated 'entries' list) into the Spring model before
  -- this JSP renders.
  --
  -- The Fluid Infusion pager that was previously used here has been replaced with a
  -- vanilla JavaScript implementation that behaves identically with respect to empty data.
  --%>
<c:set var="n"><portlet:namespace/></c:set>

<div id="${n}" class="container-fluid newsreader-container">
    <div class="d-flex align-items-center gap-3 mb-2">
        <nav aria-label="<spring:message code='videos.previous'/> <spring:message code='videos.next'/>">
            <ul id="${n}pager-top" class="pagination mb-0">
                <li class="page-item pager-previous"><a class="page-link" href="#"><spring:message code="videos.previous"/></a></li>
                <li class="page-item pager-next"><a class="page-link" href="#"><spring:message code="videos.next"/></a></li>
            </ul>
        </nav>
        <span class="pager-summary text-muted small"></span>
        <div class="d-flex align-items-center gap-1 small">
            <span><spring:message code="videos.show"/></span>
            <select class="form-select form-select-sm pager-page-size" style="width:auto">
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
            <span><spring:message code="videos.per.page"/></span>
        </div>
    </div>

    <div class="videos"></div>
</div>

<script type="text/javascript"><rs:compressJs>
'use strict';

(function () {
    var $ = up.jQuery;

    var videos = [];
    <c:forEach items="${ feed.entries }" var="entry">
    videos.push({
        title: '<spring:escapeBody javaScriptEscape="true">${ entry.title }</spring:escapeBody>',
        description: '<spring:escapeBody javaScriptEscape="true">${ entry.description }</spring:escapeBody>',
        imageUrl: '<spring:escapeBody javaScriptEscape="true">${ entry.imageUrl }</spring:escapeBody>',
        link: '<spring:escapeBody javaScriptEscape="true">${ entry.link }</spring:escapeBody>'
    });
    </c:forEach>

    var container = document.getElementById('${n}');
    var videosEl = container.querySelector('.videos');
    var pageSizeSelect = container.querySelector('.pager-page-size');
    var prevBtn = container.querySelector('.pager-previous a');
    var nextBtn = container.querySelector('.pager-next a');
    var summaryEl = container.querySelector('.pager-summary');

    var currentPage = 0;
    var pageSize = parseInt(pageSizeSelect.value, 10);

    function renderPage() {
        if (videos.length === 0) {
            summaryEl.textContent = '0 / 0';
            prevBtn.parentElement.classList.add('disabled');
            nextBtn.parentElement.classList.add('disabled');
            return;
        }

        var start = currentPage * pageSize;
        var end = Math.min(start + pageSize, videos.length);
        var totalPages = Math.ceil(videos.length / pageSize);

        videosEl.innerHTML = '';
        videos.slice(start, end).forEach(function (video) {
            var div = document.createElement('div');
            div.className = 'video';

            var h3 = document.createElement('h3');
            var a = document.createElement('a');
            var href = video.link || '';
            if (/^https?:\/\//i.test(href)) { a.href = href; }
            a.className = 'video-title';
            a.textContent = video.title;
            h3.appendChild(a);
            div.appendChild(h3);

            if (video.imageUrl && /^https?:\/\//i.test(video.imageUrl)) {
                var img = document.createElement('img');
                img.className = 'img';
                img.src = video.imageUrl;
                img.alt = '';
                div.appendChild(img);
            }

            var p = document.createElement('p');
            p.className = 'description';
            p.textContent = video.description;
            div.appendChild(p);

            videosEl.appendChild(div);
        });

        summaryEl.textContent = (start + 1) + ' - ' + end + ' / ' + videos.length;
        prevBtn.parentElement.classList.toggle('disabled', currentPage === 0);
        nextBtn.parentElement.classList.toggle('disabled', currentPage >= totalPages - 1);
    }

    prevBtn.addEventListener('click', function (e) {
        e.preventDefault();
        if (currentPage > 0) { currentPage--; renderPage(); }
    });

    nextBtn.addEventListener('click', function (e) {
        e.preventDefault();
        var totalPages = Math.ceil(videos.length / pageSize);
        if (currentPage < totalPages - 1) { currentPage++; renderPage(); }
    });

    pageSizeSelect.addEventListener('change', function () {
        pageSize = parseInt(this.value, 10);
        currentPage = 0;
        renderPage();
    });

    renderPage();
}());
</rs:compressJs></script>
