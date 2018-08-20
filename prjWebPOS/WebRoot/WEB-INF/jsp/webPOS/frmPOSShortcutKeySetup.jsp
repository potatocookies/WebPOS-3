<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Stock Flash Report</title>
<style>
.disabled_button{
    background:url(../images/big1.png) no-repeat;
   background-size: 96px 24px;
    cursor:pointer;
    border:none;
    width:100px;
    height:24px;
    color: #fff;
    font-weight: normal;
    background-color: #c0c0c0;
}
</style>
<script type="text/javascript">
var reOrderStockList=0;
var jsonObj = [];
$(function() 
		{		
			
		}); 
/**
* Success Message After Saving Record
**/
$(document).ready(function()
{
	var message='';
	<%if (session.getAttribute("success") != null) 
	{
		if(session.getAttribute("successMessage") != null)
		{%>
			message='<%=session.getAttribute("successMessage").toString()%>';
		    <%
		    session.removeAttribute("successMessage");
		}
		boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
		session.removeAttribute("success");
		if (test) 
		{
			%>alert("Data Saved \n\n"+message);<%
		}
	}%>
	
	$(document).ajaxStart(function() {
		$("#wait").css("display", "block");
	});
	$(document).ajaxComplete(function() {
		$("#wait").css("display", "none");
	});

	
	$("[type='reset']").click(function(){
		location.reload(true);
	});
	
	

	
	$("#view").click(function(event) 
			{
				
			});

});




function funSetDate()
{
	
	var searchurl=getContextPath()+"/getPOSDate.html";
	 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        success: function(response)
		        {
		       
		        var date = new Date(response.POSDate);
		        var	dateTime=date.getDate()  + '-' + (date.getMonth() + 1)+ '-' +  date.getFullYear();
		        var posDate=dateTime.split(" ");
		        $("#txtFromDate").val(posDate[0]);
	        	$("#txtToDate").val(posDate[0]);
	        	
		        },
		        error: function(jqXHR, exception)
		        {
		            if (jqXHR.status === 0) {
		                alert('Not connect.n Verify Network.');
		            } else if (jqXHR.status == 404) {
		                alert('Requested page not found. [404]');
		            } else if (jqXHR.status == 500) {
		                alert('Internal Server Error [500].');
		            } else if (exception === 'parsererror') {
		                alert('Requested JSON parse failed.');
		            } else if (exception === 'timeout') {
		                alert('Time out error.');
		            } else if (exception === 'abort') {
		                alert('Ajax request aborted.');
		            } else {
		                alert('Uncaught Error.n' + jqXHR.responseText);
		            }		            
		        }
	 });
	 
}

function CalculateDateDiff(fromDate,toDate) {

	var frmDate= fromDate.split('-');
    var fDate = new Date(frmDate[2],frmDate[1],frmDate[0]);
    
    var tDate= toDate.split('-');
    var t1Date = new Date(tDate[2],tDate[1],tDate[0]);

	var dateDiff=t1Date-fDate;
		 if (dateDiff >= 0 ) 
		 {
     	return true;
     }else{
    	 alert("Please Check From Date And To Date");
    	 return false;
     }
}
			

</script>


</head>

<body onload="funLoadTableData()">
	<div id="formHeading">
		<label>Shortcut Key Setup</label>
	</div>

	<s:form name="POSShortcutKeySetup" method="POST" action="savePOSShortcutKeySetup.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
		
		<div class="title">
		
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">Keys</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<label class="title">Masters</label>
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<label class="title">Transactions</label>
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<label class="title">Reports</label>
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F1</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList1}" name="cmbView" path="strMasterList1" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList1}" name="cmbView" path="strTransList1" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList1}" name="cmbView" path="strReportList1" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F2</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList2}" name="cmbView" path="strMasterList2" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList2}" name="cmbView"  path="strTransList2" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList2}" name="cmbView" path="strReportList2" />
				</div>
			</div>
			 
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F3</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList3}" name="cmbView" path="strMasterList3" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList3}" name="cmbView" path="strTransList3" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList3}" name="cmbView" path="strReportList3" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F4</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList4}" name="cmbView" path="strMasterList4" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList4}" name="cmbView" path="strTransList4" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList4}" name="cmbView" path="strReportList4" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F5</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList5}" name="cmbView" path="strMasterList5" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList5}" name="cmbView" path="strTransList5" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList5}" name="cmbView" path="strReportList5" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F6</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList6}" name="cmbView" path="strMasterList6" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList6}" name="cmbView" path="strTransList6" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList6}" name="cmbView" path="strReportList6" />
				</div>
			</div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F7</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList7}" name="cmbView" path="strMasterList7" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList7}" name="cmbView" path="strTransList7" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList7}" name="cmbView" path="strReportList7" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F8</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList8}" name="cmbView" path="strMasterList8" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList8}" name="cmbView" path="strTransList8" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList8}" name="cmbView" path="strReportList8" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F9</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList9}" name="cmbView" path="strMasterList9" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList9}" name="cmbView" path="strTransList9" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList9}" name="cmbView" path="strReportList9" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F10</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList10}" name="cmbView" path="strMasterList10" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList10}" name="cmbView" path="strTransList10" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList10}" name="cmbView" path="strReportList10" />
				</div>
			</div>
			
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F11</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList11}" name="cmbView" path="strMasterList11" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList11}" name="cmbView" path="strTransList11" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList11}" name="cmbView" path="strReportList11" />
				</div>
			</div>
			
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 10%;">
				<div class="element-input col-lg-6" style="width: 10%;"> 
    				<label class="title">F12</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${masterList12}" name="cmbView" path="strMasterList12" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${transList12}" name="cmbView" path="strTransList12" />
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:select id="cmbView" items="${reportList12}" name="cmbView" path="strReportList12" />
				</div>
			</div>
			
			<br/>
			
			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 25%;">
	     			<p align="center">
	            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="OK" id="OK" /></div>
	          
	            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="CANCEL" id="Cancel" /></div>
	     			</p>
   			 </div>
   			 
   			 <div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 60%; left: 40%; padding: 2px;">
					<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
			 </div>
			
			 
		 </div>
			 
		
<!-- 		<div> -->
<!-- 			<div> -->

<!-- 				<table class="masterTable" style="margin-left: 200px; width: 65%;"> -->
<!--                  <tr>                    -->
<!-- 					<td >Keys</td> -->
<!-- 					<td >Masters</td> -->
<!-- 					<td >Transactions</td> -->
<!-- 					<td >Reports</td>	 -->
<!-- 				</tr> -->
<!-- 				<tr>                    -->
<!-- 					<td >F1</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList1}" name="cmbView"  cssClass="BoxW124px" path="strMasterList1"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList1}" name="cmbView"  cssClass="BoxW124px" path="strTransList1"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList1}" name="cmbView"  cssClass="BoxW124px" path="strReportList1"> --%>
<%-- 						</s:select></td> --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F2</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList2}" name="cmbView"  cssClass="BoxW124px" path="strMasterList2"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList2}" name="cmbView"  cssClass="BoxW124px" path="strTransList2"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList2}" name="cmbView"  cssClass="BoxW124px" path="strReportList2"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F3</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList3}" name="cmbView"  cssClass="BoxW124px" path="strMasterList3"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList3}" name="cmbView"  cssClass="BoxW124px" path="strTransList3"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList3}" name="cmbView"  cssClass="BoxW124px" path="strReportList3"> --%>
<%-- 						</s:select></td> --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F4</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList4}" name="cmbView"  cssClass="BoxW124px" path="strMasterList4"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList4}" name="cmbView"  cssClass="BoxW124px" path="strTransList4"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList4}" name="cmbView"  cssClass="BoxW124px" path="strReportList4"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F5</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList5}" name="cmbView"  cssClass="BoxW124px" path="strMasterList5"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList5}" name="cmbView"  cssClass="BoxW124px" path="strTransList5"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList5}" name="cmbView"  cssClass="BoxW124px" path="strReportList5"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F6</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList6}" name="cmbView"  cssClass="BoxW124px" path="strMasterList6"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList6}" name="cmbView"  cssClass="BoxW124px" path="strTransList6"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList6}" name="cmbView"  cssClass="BoxW124px" path="strReportList6"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F7</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList7}" name="cmbView"  cssClass="BoxW124px" path="strMasterList7"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList7}" name="cmbView"  cssClass="BoxW124px" path="strTransList7"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList7}" name="cmbView"  cssClass="BoxW124px" path="strReportList7"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F8</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList8}" name="cmbView"  cssClass="BoxW124px" path="strMasterList8"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList8}" name="cmbView"  cssClass="BoxW124px" path="strTransList8"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList8}" name="cmbView"  cssClass="BoxW124px" path="strReportList8"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F9</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList9}" name="cmbView"  cssClass="BoxW124px" path="strMasterList9"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList9}" name="cmbView"  cssClass="BoxW124px" path="strTransList9"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList9}" name="cmbView"  cssClass="BoxW124px" path="strReportList9"> --%>
<%-- 						</s:select></td> --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F10</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList10}" name="cmbView"  cssClass="BoxW124px" path="strMasterList10"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList10}" name="cmbView"  cssClass="BoxW124px" path="strTransList10"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList10}" name="cmbView"  cssClass="BoxW124px" path="strReportList10"> --%>
<%-- 						</s:select></td> --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F11</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList11}" name="cmbView"  cssClass="BoxW124px" path="strMasterList11"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList11}" name="cmbView"  cssClass="BoxW124px" path="strTransList11"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList11}" name="cmbView"  cssClass="BoxW124px" path="strReportList11"> --%>
<%-- 						</s:select></td> --%>
<!-- 				</tr> -->
				
<!-- 				<tr>                    -->
<!-- 					<td >F12</td> -->
<%-- 					<td ><s:select id="cmbView" items="${masterList12}" name="cmbView"  cssClass="BoxW124px" path="strMasterList12"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${transList12}" name="cmbView"  cssClass="BoxW124px" path="strTransList12"> --%>
<%-- 						</s:select></td> --%>
<%-- 					<td ><s:select id="cmbView" items="${reportList12}" name="cmbView"  cssClass="BoxW124px" path="strReportList12"> --%>
<%-- 						</s:select></td>	 --%>
<!-- 				</tr> -->
					
				

<!-- 				</table> -->
<!-- 			</div> -->

			
<!-- 		</div> -->

<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			    <input type="submit" value="OK"	class="form_button"  id="OK" /> -->
<!-- 			    <input type="button" value="Cancel" class="form_button"id="Cancel" />   -->
<!-- 		</p> -->
<!-- 		<div id="wait" -->
<!-- 			style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 60%; left: 40%; padding: 2px;"> -->
<!-- 			<img -->
<%-- 				src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" --%>
<!-- 				width="60px" height="60px" /> -->
<!-- 		</div> -->

	</s:form>

</body>
</html>