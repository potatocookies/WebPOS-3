<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Blind Settlement Report</title>
<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
    /* add padding to account for vertical scrollbar */
    padding-right: 20px;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
    height: 200px;
}
</style>
<script type="text/javascript">

/*On form Load It Reset form :Ritesh 22 Nov 2014*/
$(function() 
{		
	var POSDate="${POSDate}"
	var startDate="${POSDate}";
	var Date = startDate.split(" ");
	var arr = Date[0].split("-");
	Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
	$("#txtFromDate" ).datepicker({ dateFormat: 'dd-mm-yy' });		
	$("#txtFromDate" ).datepicker('setDate', Dat); 
	$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtToDate" ).datepicker('setDate', Dat);  
	
}); 
	/**
	* Reset The Item Name TextField
	**/
	function funResetFields()
	{
			location.reload();
	}
	
	function funSendReportOnMail()
	{
		var fromDate = $("#txtFromDate").val();
		var toDate = $("#txtToDate").val();
		var posCode = $("#cmbPOSName").val()
		var searchurl=getContextPath()+"/sendReportOnMail.html?fromDate="+fromDate+"&toDate="+toDate+"&posCode="+posCode;
		 $.ajax({
			        type: "POST",
			        url: searchurl,
			        dataType: "text",
			        success: function(response)
			        {
			        	if(response==1)
			        	{
			        		alert("Report Send On Mail Successfully");
			        	}
			        	
					},
					error: function(jqXHR, exception) {
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
	
</script>


</head>
<body >
	<div id="formHeading">
		<label>Bill Wise Settlement Wise Group Wise Breakup</label>
	</div>
	<s:form name="BillWiseSettlementWiseGroupWiseBreakup" method="POST" action="rptPOSBillWiseSettlementWiseGroupWiseBreakup.html?saddr=${urlHits}" target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<br />
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 10%;">
							<div class="element-input col-lg-6" style="width: 20%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 10%;">
							<div class="element-input col-lg-6" style="width: 20%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 16%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}"  style="width: 100%;"/>	
							</div>
					 </div>
					
					
					<br/>
					
					 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%; margin-left: 25%;">
					  		<p align="center">
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="View"/></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="SendOnMail" onclick="funSendReportOnMail();"/></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Close" onclick=""/></div>
							</p>
			  		  </div>
					
		 </div>
			
			


	</s:form>

</body>

</html>