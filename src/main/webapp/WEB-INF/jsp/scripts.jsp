<rs:aggregatedResources path="skin${ mobile ? '-mobile' : '' }${ usePortalJsLibs ? '-shared' : '' }.xml"/>

<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};
    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
            ${n}._ = _.noConflict();
            ${n}.Backbone = Backbone.noConflict();
            fluid = null;
            fluid_1_4 = null;
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
            ${n}._ = ${ ns }_;
            ${n}.Backbone = ${ ns }Backbone;
        </c:otherwise>
    </c:choose>
    if (!upnews.initialized) upnews.init(${n}.jQuery, ${n}._, ${n}.Backbone);
    ${n}.upnews = upnews;
</rs:compressJs></script>