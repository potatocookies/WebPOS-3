<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Table Status Report</title>
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
var KOTNo="";
var selectedtblNo="";
var previousKOT="";
var previousTable="";
var prevVal="";
var prevStatus="";
var prevIndex="";
var prevCellIndex="";
var prevKOTIndex="";
var prevKOTCellIndex="";
var selectedIndx=0;
var totalPax=0;
var tblMenuItemDtl_MAX_ROW_SIZE=100;
var tblMenuItemDtl_MAX_COL_SIZE=9;
var totalQuantity=0;
var totalAmount=0;
function funFillTables()
{
	funFetchTableStatusReportData();
}


function funFetchTableStatusReportData()
{
	funRemoveTableRows("tblSalesAchieved");
	funRemoveTableRows("tblSalesInProgress");
	funRemoveTableRows("tblAvgCovers");
	funRemoveTableRows("tblAvgCheck");
	funRemoveTableRows("tblAvgCoversForSalesInProgress");
	funRemoveTableRows("tblAvgCheckForSalesInProgress");
	var posCode=$("#cmbPOSName").val();
	var searchurl=getContextPath()+"/loadSalesAchievedAndInProgressData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ 
	        	POSCode:posCode,
			},
	        url: searchurl,
	        dataType: "json",
	        async: false,
	        success: function(response)
	        {
	        	 funFillSalesAchievedTableGrid(response.SalesAchievedData);
	        	 funFillSalesInProgrssTableGrid(response.SalesInProgressData);
	        	 funFillAvgCoversTableGrid(response.listOfAvgCoversForSalesAchieved);
	        	 funFillAvgCheckTableGrid(response.listOfAvgCheckForSalesAchieved);
	        	 funFillAvgCoversForSalesInProgressTableGrid(response.listOfAvgCoversForSalesInProgress);
	        	 funFillAvgCheckForSalesInProgressTableGrid(response.listOfAvgCheckForSalesInProgress);
	        	 $("#lblPOSName").text("POS Name: "+posCode);
	        	 $("#lblTotalQty").text(totalQuantity);
	        	 $("#lblTotalAmt").text(totalAmount);
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
	
	
	$("form").submit(function(event){
		  funValidate();
		});
	
});


//Combo Box Change then set value
function funOnChange() 
{
	funFillTables();
}

function funRemoveTableRows(tblId)
{
	var table = document.getElementById(tblId);
	var rowCount = table.rows.length;
	while(rowCount>1)
	{
		table.deleteRow(1);
		rowCount--;
	}
}


function funFillSalesAchievedTableGrid(list)
{
	 var row=0;
	 var tableId="tblSalesAchieved";
	 var tblTableDtl=document.getElementById('tblSalesAchieved');
	 var rowCount = tblTableDtl.rows.length;
	 var totalQty=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"15%\" id=\"txtQuantity."+(rowCount)+"\" value='"+obj.dblQuantity+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   insertTR.insertCell(2).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalQty+=obj.dblQuantity;
		   totalAmt+=obj.dblAmount;
		});
	   totalQuantity+=totalQty;
	   totalAmount+=totalAmt;
	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"15%\" id=\"txtQuantity."+(rowCount)+"\" value='"+totalQty+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   insertTR.insertCell(2).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
}


function funFillSalesInProgrssTableGrid(list)
{
	 var row=0;
	 var tableId="tblSalesInProgress";
	 var tblTableDtl=document.getElementById('tblSalesInProgress');
	 var rowCount = tblTableDtl.rows.length;
	 var totalQty=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"15%\" id=\"txtQuantity."+(rowCount)+"\" value='"+obj.dblQuantity+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   insertTR.insertCell(2).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalQty+=obj.dblQuantity;
		   totalAmt+=obj.dblAmount;
		});
	   totalQuantity+=totalQty;
	   totalAmount+=totalAmt;
	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"15%\" id=\"txtQuantity."+(rowCount)+"\" value='"+totalQty+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   insertTR.insertCell(2).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	     
}

function funFillAvgCoversTableGrid(list)
{
	 var row=0;
	 var tableId="tblAvgCovers";
	 var tblTableDtl=document.getElementById('tblAvgCovers');
	 var rowCount = tblTableDtl.rows.length;
	 var totalCoversTurned=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalAmt+=obj.dblAmount;
		   totalCoversTurned=obj.dblQuantity;
		});

	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   $("#lblCoversTurned").text(totalCoversTurned);
}

function funFillAvgCheckTableGrid(list)
{
	 var row=0;
	 var tableId="tblAvgCheck";
	 var tblTableDtl=document.getElementById('tblAvgCheck');
	 var rowCount = tblTableDtl.rows.length;
	 var totalTablesTurned=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalAmt+=obj.dblAmount;
		   totalTablesTurned=obj.dblQuantity;
		});

	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   $("#lblTablesTurned").text(totalTablesTurned);
}

function funFillAvgCoversForSalesInProgressTableGrid(list)
{
	 var row=0;
	 var tableId="tblAvgCoversForSalesInProgress";
	 var tblTableDtl=document.getElementById('tblAvgCoversForSalesInProgress');
	 var rowCount = tblTableDtl.rows.length;
	 var totalCoversServed=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalAmt+=obj.dblAmount;
		   totalCoversServed=obj.dblQuantity;
		});

	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   $("#lblCoversServed").text(totalCoversServed);
}


function funFillAvgCheckForSalesInProgressTableGrid(list)
{
	 var row=0;
	 var tableId="tblAvgCheckForSalesInProgress";
	 var tblTableDtl=document.getElementById('tblAvgCheckForSalesInProgress');
	 var rowCount = tblTableDtl.rows.length;
	 var totalBusyTable=0,totalAmt=0;
	 $.each(list, function(i, obj) 
		{	
		   var insertTR=tblTableDtl.insertRow();
		   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='"+obj.strRevenueHead+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
		   row++;	
		   totalAmt+=obj.dblAmount;
		   totalBusyTable=obj.dblQuantity;
		});

	   var insertTR=tblTableDtl.insertRow();
	   insertTR.insertCell(0).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].strRevenueHead\" readonly=\"readonly\" class=\"Box\" size=\"50%\" id=\"txtRevenueHead."+(rowCount)+"\" value='Total' style=\"color: red;\"  onclick=\"funGetSelectedRowIndex(this)\"/>";
	   insertTR.insertCell(1).innerHTML= "<input name=\"listOfSalesData["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;width:100%; color: red;\" size=\"11%\" id=\"txtAmount."+(rowCount)+"\" value='"+totalAmt+"'  onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
	   $("#lblBusyTable").text(totalBusyTable);
}

function funGetSelectedRowIndex(obj,tableId)
{
	var index = obj.parentNode.parentNode.rowIndex;
	var cellIndex=obj.parentNode.cellIndex;
	/*if(tableId=="tblData")
	{
	  	 var tableName = document.getElementById(tableId);
		 var code=obj.id;
		 var value=obj.value;
		 KOTNo=code;
	}*/
}

</script>


</head>

<body onload="funFillTables()">
	<div id="formHeading">
		<label>POS Statistics</label>
	</div>

	<s:form name="POSStatistics" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" >
		
		<div class="title" >
				
				<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title">Select POS</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;"> 
							<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" onchange="funOnChange();" />
						</div>
				</div>
					
				<div style=" width: 49%; float:left;">
					
					<div class="row" style="background-color: #fff; display: contents;">
							
							<table style="width: 100%;">
								<tbody style="border-bottom: 2px solid #ccc;border-top: 2px solid #ccc;">
								 <tr>
									 <td style="width:55%;color: #4296c0"><s:label id="lblPOSName" path="strPOSName">POSName</s:label></td>
									 <td style="width:10%;color: #4296c0"><s:label id="lblTotalQty" path="dblTotalQty">Qty</s:label></td>
									 <td style="width:10%;color: #4296c0"><s:label id="lblTotalAmt" path="dblTotalAmt">Amt</s:label></td>	 			
								</tr>
							</table>
							
					</div>
							
					<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 40%;"> 
		    						<label class="title">Sales Achieved</label>
		    				</div>
						
					</div>
							
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 260px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblSalesAchieved" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Revenue Head</label></td>
											<td style="border: 1px  white solid;width:10%"><label>Quantity</label></td>
											<td style="border: 1px  white solid;width:10%"><label>Amount</label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
					 </div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 40%;"> 
		    						<label class="title">Sales In Progress</label>
		    				</div>
						
					</div>
							
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 260px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblSalesInProgress" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Revenue Head</label></td>
											<td style="border: 1px  white solid;width:10%"><label>Quantity</label></td>
											<td style="border: 1px  white solid;width:10%"><label>Amount</label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
					</div>
					
				</div>
				
				<div style=" width: 49%; float:right;">
				
						<div class="row" style="background-color: #fff; display: -webkit-box;margin-bottom: 7%;">
						</div>
				
						<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 40%;"> 
		    						<label class="title">Other Information</label>
		    				</div>
						
						</div>
				
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 100px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblAvgCovers" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Avg Covers</label></td>
											<td style="border: 1px  white solid;width:10%"><label></label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 100px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblAvgCheck" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Avg Check</label></td>
											<td style="border: 1px  white solid;width:10%"><label></label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 50%;"> 
		    						<label class="title">Covers Turned</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 50%; text-align: right;"> 
		    					<s:label id="lblCoversTurned" path="dblCoversTurned"></s:label>
							</div>
							
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 25px;">
							
							<div class="element-input col-lg-6" style="width: 50%;"> 
		    						<label class="title">Tables Turned</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 50%; text-align: right;"> 
		    					<s:label id="lblTablesTurned" path="dblTablesTurned"></s:label>
							</div>
						
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 100px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblAvgCoversForSalesInProgress" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Avg Covers</label></td>
											<td style="border: 1px  white solid;width:10%"><label></label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
						
							<div style="border: 1px solid #ccc; display: block; height: 100px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblAvgCheckForSalesInProgress" style="text-align: center !important;">
								
									<thead>
										<tr>
											<td style="border: 1px  white solid;width:55%"><label>Avg Check</label></td>
											<td style="border: 1px  white solid;width:10%"><label></label></td>
										</tr>
									</thead>
									
								</table>
							
							</div>
					
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 50%;"> 
		    						<label class="title">Covers Served</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 50%; text-align: right;"> 
		    					<s:label id="lblCoversServed" path="dblCoversServed"></s:label>
							</div>
							
						</div>
						
						<div class="row" style="background-color: #fff; display: -webkit-box;">
							
							<div class="element-input col-lg-6" style="width: 50%;"> 
		    						<label class="title">Busy Tables</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 50%; text-align: right;"> 
		    					<s:label id="lblBusyTable" path="dblBusyTable"></s:label>
							</div>
						
						</div>
				
				
				</div>
				
					<br/>
		</div>
		
		
		
<!-- 		<div> -->
<!-- 		     <table class="masterTable" style="width: 100%;"> -->
<!-- 				<tr> -->
<!-- 				    <td> -->
<!-- 					 <label>Select POS</label> -->
<%-- 					 <s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" onchange="funOnChange();" cssClass="BoxW124px" /> --%>
<!-- 					</td> -->
<!-- 			    </tr> -->
<!-- 			  </table>		 -->
<!-- 			 <div style=" width: 50%; height: 500px;float:left;background-color: #a4d7ff; ">  -->
<!-- 	          <br> -->
	          		
<!-- 	            <div style=" background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: scroll; overflow-y: scroll; width: 95%;"> -->
<!-- 						<table class="masterTable col2-right col3-right" style="width: 100%;"> -->
<!-- 						 <tr> -->
<%-- 							 <td style="width:55%;color: blue"><s:label id="lblPOSName" path="strPOSName">POSName</s:label></td> --%>
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblTotalQty" path="dblTotalQty">Qty</s:label></td> --%>
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblTotalAmt" path="dblTotalAmt">Amt</s:label></td>	 			 --%>
<!-- 						</tr> -->
<!-- 						</table> -->
<!-- 						<label>Sales Achieved</label> -->
							
<!-- 						<table id="tblSalesAchieved" -->
<!-- 								style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 								class="transTablex col2-right col3-right "> -->
<!-- 								<tr> -->
<!-- 								<td style="border: 1px  white solid;width:55%"><label>Revenue Head</label></td> -->
<!-- 								<td style="border: 1px  white solid;width:10%"><label>Quantity</label></td> -->
<!-- 								<td style="border: 1px  white solid;width:10%"><label>Amount</label></td> -->
<!-- 								</tr> -->
<!-- 						</table>	 -->
<!-- 				</div>	 -->
<!-- 				 <br> -->
<!-- 				<div style=" background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 250px; margin: auto; overflow-x: scroll; overflow-y: scroll; width: 95%;"> -->
<!-- 						<label>Sales In Progress</label> -->
<!-- 						<table id="tblSalesInProgress" -->
<!-- 								style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 								class="transTablex col2-right col3-right "> -->
								
<!-- 								<tr> -->
<!-- 								<td style="border: 1px  white solid;width:55%"><label>Revenue Head</label></td> -->
<!-- 								<td style="border: 1px  white solid;width:10%"><label>Quantity</label></td> -->
<!-- 								<td style="border: 1px  white solid;width:10%"><label>Amount</label></td> -->
<!-- 								</tr> -->
<!-- 						</table>	 -->
<!-- 				</div>		 -->
<!-- 		    </div> -->
<!-- 		    <div style=" width: 50%; height: 180px; float:right; border-collapse: separate; overflow-x: scroll; overflow-y: scroll; background-color: #C0E2FE;"> -->
<!-- 		    <br> -->
<!-- 			   <label>Other Information</label> -->
<!-- 				<table id="tblAvgCovers" -->
<!-- 						style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex col2-right col3-right "> -->
<!-- 						<tr> -->
<!-- 						<td style="border: 1px  white solid;width:55%"><label>Avg Covers</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:10%"><label></label></td> -->
<!-- 						</tr> -->
<!-- 				</table>	 -->
<!-- 				</div> -->
				
<!-- 			<div style=" width: 50%; height: 220px; float:right; border-collapse: separate; overflow-x: scroll; overflow-y: scroll; background-color: #C0E2FE;"> -->
<!-- 		    <br> -->
<!-- 			   <table id="tblAvgCheck" -->
<!-- 						style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex col2-right col3-right "> -->
<!-- 						<tr> -->
<!-- 						<td style="border: 1px  white solid;width:55%"><label>Avg Check</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:10%"><label></label></td> -->
<!-- 						</tr> -->
<!-- 				</table> -->
<!-- 				<br> -->
<!-- 					<table class="masterTable col2-right " style="width: 100%;"> -->
<!-- 						 <tr> -->
<!-- 							 <td style="width:20%;color: blue"><label>Covers Turned</label></td> -->
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblCoversTurned" path="dblCoversTurned"></s:label></td> --%>
<!-- 						</tr> -->
<!-- 						<tr> -->
<!-- 						     <td style="width:20%;color: blue"><label>Tables Turned</label></td> -->
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblTablesTurned" path="dblTablesTurned"></s:label></td>	 			 --%>
<!-- 						</tr> -->
<!-- 				    </table>	 -->
<!-- 			</div>	 -->
<!-- 			</div> -->
			
<!-- 			<div style=" width: 50%; height: 220px; float:right; border-collapse: separate; overflow-x: scroll; overflow-y: scroll; background-color: #C0E2FE;"> -->
<!-- 		    <br> -->
<!-- 		      <table id="tblAvgCoversForSalesInProgress" -->
<!-- 						style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex col2-right  "> -->
<!-- 						<tr> -->
<!-- 						<td style="border: 1px  white solid;width:55%"><label>Avg Covers</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:10%"><label></label></td> -->
<!-- 						</tr> -->
<!-- 				</table> -->
<!-- 			   <table id="tblAvgCheckForSalesInProgress" -->
<!-- 						style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex col2-right col3-right "> -->
<!-- 						<tr> -->
<!-- 						<td style="border: 1px  white solid;width:55%"><label>Avg Check</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:10%"><label></label></td> -->
<!-- 						</tr> -->
<!-- 				</table> -->
<!-- 				<br> -->
<!-- 					<table class="masterTable col2-right " style="width: 100%;"> -->
<!-- 						 <tr> -->
<!-- 							 <td style="width:20%;color: blue"><label>Covers Served</label></td> -->
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblCoversServed" path="dblCoversServed"></s:label></td> --%>
<!-- 						</tr> -->
<!-- 						<tr> -->
<!-- 						     <td style="width:20%;color: blue"><label>Busy Tables</label></td> -->
<%-- 							 <td style="width:10%;color: blue"><s:label id="lblBusyTable" path="dblBusyTable"></s:label></td>	 			 --%>
<!-- 						</tr> -->
<!-- 				    </table>	 -->
<!-- 				</div> -->
						
		    

		<br />
		<br />
		
		

	</s:form>

</body>
</html>