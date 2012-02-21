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

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.6.1/jquery-1.6.1.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>"></script>
<script type="text/javascript"><rs:compressJs>

    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);

    ${n}.jQuery(function() {
    
        var $ = ${n}.jQuery;

        var savePrefUrl = '<portlet:actionURL/>';

    	var $p = $("#news-single-preference");	//find the root element of the protlet to scope dom seraches
    	$p.find("#max").change(function(e){
    		$.post(savePrefUrl, {prefName: 'maxStories', prefValue: $(e.target).val()});
    	});	
    	$p.find("#view").change(function(e){
    		$.post(savePrefUrl, {prefName: 'summaryView', prefValue: $(e.target).val()});
    	});	
    	$p.find("#new-window").change(function(e){
    		$.post(savePrefUrl, {prefName: 'newWindow', prefValue: $(e.target).attr("checked")});
    	});

    });

</rs:compressJs></script>
<style type="text/css" media="screen">
	.preference{
		margin:0 0 13px 13px;
	}
</style>

<div id="news-single-preference">
	<h3>Preferences</h3>
	<div class="preference">
		<label>Maximum number of stories to display</label>
		<select id="max" ${ max.readOnly ? "disabled='disabled'" : ''}>
			<c:forEach items="${max.options}" var="item">
				<option value="${item}" ${ item == max.value ? "selected='selected'" : '' }>
					${item}
				</option>
			</c:forEach>
		</select>
	</div>
	
	<div class="preference">
		<label>Display stories as</label>
		<select id="view" ${ view.readOnly ? "disabled='disabled'" : ''}>
			<c:forEach items="${view.options}" var="entry" >
				<option value="${entry.key}" ${entry.key == view.value ? "selected='selected'" : '' }>
					${entry.value}
				</option>
			</c:forEach>
		</select>
	</div>
	
	<div class="preference">
		<label>Open stories in new window</label>
		<input type="checkbox" name="new-window" value="" id="new-window" 
			${ newWindow.value == "true" ?  "checked='checked'" : '' } 
			${ newWindow.readOnly ? "disabled='disabled'" : '' }>
	</div>
	<a href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/images/arrow_left.png"/>"> Return to feed</a>
</div>
