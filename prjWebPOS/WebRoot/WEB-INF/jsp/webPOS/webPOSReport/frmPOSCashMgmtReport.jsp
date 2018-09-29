<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cash Management Flash</title>
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

	var activeTab="";
	var txtVal="";
	var reportType="";
	var foundChar="N";
	
	$(document).ready(function() {
		
	    var POSDate="${POSDate}"
	    var startDate="${POSDate}";
	  	var Date = startDate.split(" ");
		var arr = Date[0].split("-");
		Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
		$("#txtdteFromDate" ).datepicker({ dateFormat: 'dd-mm-yy' });		
		$("#txtdteFromDate" ).datepicker('setDate', Dat); 
		$("#txtdteToDate").datepicker({ dateFormat: 'dd-mm-yy' });
		$("#txtdteToDate" ).datepicker('setDate', Dat);
		
		if(funDeleteTableAllRows())
		{
			var fromDate = $("#txtdteFromDate").val();
			var toDate = $("#txtdteToDate").val();
			fDate=fromDate;
			tDate=toDate;
			var strReportType=$('#cmbReportType').val();
			if(strReportType=="Detail")
			{
				funLoadDetailData();
			}
			else
			{
				funLoadSummaryData();
			}
		}
		
	$("#execute").click(function(event) {
		var fromDate = $("#txtdteFromDate").val();
		var toDate = $("#txtdteToDate").val();

		if (fromDate.trim() == '' && fromDate.trim().length == 0) {
			alert("Please Enter From Date");
			return false;
		}
		if (toDate.trim() == '' && toDate.trim().length == 0) {
			alert("Please Enter To Date");
			return false;
		}
		if(funDeleteTableAllRows())
		{
			fDate=fromDate;
			tDate=toDate;
			var strReportType=$('#cmbReportType').val();
			if(strReportType=="Detail")
			{
				funLoadDetailData();
			}
			else
			{
				funLoadSummaryData();
			}
		}
	});
	});
	function funDeleteTableAllRows()
	{
		$('#tblSales tbody').empty();
		$('#tblTotal tbody').empty();
		var table = document.getElementById("tblSales");
		var rowCount1 = table.rows.length;
		if(rowCount1==0){
			return true;
		}else{
			return false;
		}
	}
	
	function funLoadDetailData() 
	{
		var fromDate = $('#txtdteFromDate').val();
		 var toDate = $('#txtdteToDate').val();
		var strReportType=$('#cmbReportType').val();
		var posName=$('#cmbPOSName').val();
		var transType = $('#cmbTransType').val();

		
		$.ajax({
			type : "GET",
			data:{ fromDate:fromDate,
				   toDate:toDate,
				   strReportType:strReportType,
				   posCode:posName,
				   transType:transType,
			     },
			url : getContextPath() + "/getCashManagementDetailReport.html",
			dataType : "json",
			success : function(response) {
				
				
				var table = document.getElementById("tblSales");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);

				 row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"userCode."+(0)+"\" value='UserCode'/>";
			     row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"transType."+(0)+"\" value='TransType' />";
			     row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"date."+(0)+"\" value='Date'/>";
			     row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"posName."+(0)+"\" value='POSName'/>";
			     row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"reason."+(0)+"\" value='Reason' />";
			     row.insertCell(5).innerHTML= "<input class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"remarks."+(0)+"\" value='Remarks' />";
			     row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"amount."+(0)+"\" value='Amount'/>";
			     
				$.each(response.listData,function(i,item){
	            	
					funFillTableWithDetail(item.userCode,item.posCode,item.fromDate,item.posName,item.toDate,item.userName,item.balanceAmt);
	            	});
				
				funFillTotalData(response.Total,strReportType);
			},
			error : function(jqXHR, exception) {
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
	
	function funLoadSummaryData() 
	{
		var fromDate = $('#txtdteFromDate').val();
		var toDate = $('#txtdteToDate').val();
		var strReportType=$('#cmbReportType').val();
		var posName=$('#cmbPOSName').val();
		var transType = $('#cmbTransType').val();

		
		$.ajax({
			type : "GET",
			data:{ fromDate:fromDate,
				   toDate:toDate,
				   strReportType:strReportType,
				   posCode:posName,
				   transType:transType,
			     },
			url : getContextPath() + "/getCashManagementSummaryReport.html",
			dataType : "json",
			success : function(response) 
			{
				var table = document.getElementById("tblSales");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);

				row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"userCode."+(0)+"\" value='UserCode'/>";
			    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"transType."+(0)+"\" value='TransType' />";
			    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"date."+(0)+"\" value='Date'/>";
			    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"posName."+(0)+"\" value='POSName'/>";
			    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"amount."+(0)+"\" value='Amount'/>";
			     
				$.each(response.listData,function(i,item)
			    {
				  funFillTableWithSummary(item.userCode,item.posCode,item.fromDate,item.posName,item.balanceAmt);
            	});
				funFillTotalData(response.Total,strReportType);
			},
			error : function(jqXHR, exception) {
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
	
			function funFillTotalData(total,reportType) 
			{
				var table = document.getElementById("tblTotal");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    if(reportType=="Detail")
			    {
			    	row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"userCode."+(0)+"\" value=''/>";
				    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"transType."+(0)+"\" value='' />";
				    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"date."+(0)+"\" value=''/>";
				    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"posName."+(0)+"\" value=''/>";
				    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"reason."+(0)+"\" value='' />";
				    row.insertCell(5).innerHTML= "<input class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"remarks."+(0)+"\" value='' />";
				    row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"amount."+(0)+"\" value='"+total+"'/>";
				     	
			    }
			    else
			    {
			    	row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"userCode."+(0)+"\" value=''/>";
				    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"transType."+(0)+"\" value='' />";
				    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"date."+(0)+"\" value=''/>";
				    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;font-size: 13px; font-weight: bold;\" class=\"Box \" size=\"10%\" id=\"posName."+(0)+"\" value=''/>";
				    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;font-size: 13px; font-weight: bold;\" size=\"10%\" id=\"amount."+(0)+"\" value='"+total+"'/>";
				     
			    } 	
			    
			    
			}
				
				function funFillTableWithDetail(userCode,transType,date,posName,reason,remarks,amount)
				{
					var table = document.getElementById("tblSales");
					var rowCount = table.rows.length;
					var row = table.insertRow(rowCount);

					row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"userCode."+(rowCount)+"\" value='"+userCode+"'/>";
				    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"transType."+(rowCount)+"\" value='"+transType+"' />";
				    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"date."+(rowCount)+"\" value='"+date+"'/>";
				    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"posName."+(rowCount)+"\" value='"+posName+"'/>";
				    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"reason."+(rowCount)+"\" value='"+reason+"' />";
				    row.insertCell(5).innerHTML= "<input class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"remarks."+(rowCount)+"\" value='"+remarks+"' />";
				    row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
				} 
				
				function funFillTableWithSummary(userCode,transType,date,posName,amount)
				{
					var table = document.getElementById("tblSales");
					var rowCount = table.rows.length;
					var row = table.insertRow(rowCount);
					row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"userCode."+(rowCount)+"\" value='"+userCode+"'/>";
				    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"transType."+(rowCount)+"\" value='"+transType+"' />";
				    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"date."+(rowCount)+"\" value='"+date+"'/>";
				    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" style=\"text-align:left;\" class=\"Box \" size=\"10%\" id=\"posName."+(rowCount)+"\" value='"+posName+"'/>";
				    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
				}
				
				
			 function funAddHeaderRow(rowData)
			 {
				var table = document.getElementById("tblSales");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    for(var i=0;i<rowData.length;i++){
			    	if(i==0){
		    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"Box\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    		 } else {
			    			row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"Box\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
			    			
			    		}
					}
			}
			 

			 
			 
</script>

</head>
<body>
	<div id="formHeading">
		<label>Cash Management Flash</label>
	</div>
	<s:form name="POSCashManagementFlash" method="POST" action="exportCashManagement.html?saddr=${urlHits}"
		target="_blank" >
	
	<br />					
	<table  class="masterTable" style="width:80%;">
																		
			<tr>
			<td>
					<label>Transaction Type</label>
 					<s:select id="cmbTransType" name="cmbTransType" path="userName" items="${mapTransType}" cssClass="BoxW124px" />
			</td>
			<td>
					<label>From Date</label>
					<s:input id="txtdteFromDate" name="txtdteFromDate" path="fromDate"   cssClass="calenderTextBox"  />
					
				</td>

				<td>
					<label>To Date</label>
					<s:input id="txtdteToDate" name="txtdteToDate" path="toDate"  cssClass="calenderTextBox"  />
				</td>
			</tr>
			<tr>
			<td>
					<label>Report Type</label>
 				
				<s:select id="cmbReportType" name="cmbReportType" path="userCode" items="${mapReportType}" cssClass="BoxW124px" />
				
				</td>	
				<td>
					<label>POS Name</label>
 				
				<s:select id="cmbPOSName" name="cmbPOSName" path="posCode" items="${posList}" cssClass="BoxW124px" />
				
				</td>
				<td>
				</td>

			</tr>
			<tr>
			</tr>

			</table>
		
		
		    <div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 80%;">
				
				<table id="tblSales" class="transTablex"
					style="width: 100%; text-align: center !important;">
				</table>
				
			</div>
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 50px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 80%;">
				
	
				<table id="tblTotal" class="transTablex"
					style="width: 100%; text-align: center !important;">
				</table>
			</div>
		</div>	
		
		<br />
		<br />
		<p align="center">
			<input id="execute" type="button" value="Show"   /> 
			<input id="export"  type="submit" value="Export"  />
			<input id="close"   type="button" value="Close"  /> 
			
		</p>
		
		

	</s:form>

</body>
</html>