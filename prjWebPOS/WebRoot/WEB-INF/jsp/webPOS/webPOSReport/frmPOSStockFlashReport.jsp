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

.cell {
	background: inherit;
	border: 0 solid #060006;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	outline: 0 none;
	font-weight: bold;
	padding-left: 0;
	width: 80%
}

.header {
	border: #c0c0c0 1px solid;
	background: #78BEF9;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-weight: bold;
	outline: 0 none;
	padding-left: 0;
	width: 100%;
	height:100%
}
</style>
<script type="text/javascript">
var reOrderStockList=0;
var jsonObj = [];
$(function() 
{	
	var POSDate="${gPOSDate}";
    var startDate="${gPOSDate}";
   	var Date = startDate.split(" ");
 	var arr = Date[0].split("-");
 	Dat=arr[2]+"-"+arr[1]+"-"+arr[0];
	$("#txtFromDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtFromDate" ).datepicker('setDate', Dat);
	$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtToDate" ).datepicker('setDate', Dat);
	
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

	/* $("#txtFromDate").datepicker({
		dateFormat : 'dd-mm-yy'
	});
	$("#txtFromDate").datepicker('setDate', Dat);

	$("#txtToDate").datepicker({
		dateFormat : 'dd-mm-yy'
	});
	$("#txtToDate").datepicker('setDate', Dat); */
	
	$("[type='reset']").click(function(){
		location.reload(true);
	});
	
	

	document.getElementById("production").disabled = true;
	
	$("#production").click(function(event) 
	{
		funGenerateProductionEntry();
		
		/*type="production";
		var gurl = getContextPath()+"/generateProductionStockEntry.html";
		var abc;
		 $.ajax({
		        type: "POST",
		        url: gurl,
		        dataType: "json",
		        data:
		        {
		        	type:type,
		        	ListData:itemList,
		        },
		        success: function(response)
		        {
			 	if (response== 0) 
				{
					alert("Data Not Found");
				} 
				else 
				{ 
					
				}
			}
	    });	
		 */
	});
	
	$("#view").click(function(event) 
			{
				var fromDate = $("#txtFromDate").val();
				var toDate = $("#txtToDate").val();

				if (fromDate.trim() == '' && fromDate.trim().length == 0) {
					alert("Please Enter From Date");
					return false;
				}
				if (toDate.trim() == '' && toDate.trim().length == 0) {
					alert("Please Enter To Date");
					return false;
				}
				if(funDeleteTableAllRows()){
					if(CalculateDateDiff(fromDate,toDate))
					{
						var callingTime="second";
						fDate=fromDate;
						tDate=toDate;
						funFetchColNames(callingTime);
					}
				}
			});

});


function funLoadTableData()
{
	var callingTime="first";
	var posdate="${gPOSDate}";
	$("#txtFromDate").val(posdate);
 	$("#txtToDate").val(posdate);
 	
	var fromDate =$('#txtFromDate');
	var toDate =$('#txtToDate').val();
	var data= $("#txtFromDate").val();
	fDate=fromDate;
	tDate=toDate;
	funDeleteTableAllRows();
	funFetchColNames(callingTime);
	
	
}

function funDeleteTableAllRows()
{
	$('#tblStockFlashReport tbody').empty();
	$('#tblStockFlashHeader tbody').empty();
	$('#tblTotal tbody').empty();
	var table = document.getElementById("tblStockFlashReport");
	var rowCount1 = table.rows.length;
	if(rowCount1==0){
		return true;
	}else{
		return false;
	}
}

function funGenerateProductionEntry()
{
	type="production";
	 var searchurl=getContextPath()+"/generateProductionStockEntry.html";
	 $.ajax({
		        type: "POST",
		        url: searchurl,
		        data:{
		        	type:type,
		        },
		        dataType: "json",
		        success: function(response)
		        {
		        	if(response==0)
		        	{
		        		alert("No data");
		        		
		        	}
		        	else
		        	{
			        	
		        		  
			        	
		        	} 
				},
		
	      });

}

function funFillheaderCol(rowData,reportType) 
{
	var table = document.getElementById("tblStockFlashHeader");
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
    
    
    var rowItem=rowData.split("#");
    row.insertCell(0).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"12%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"13%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"25%\" id=\"txtCompStk."+(rowCount)+"\" value='"+rowItem[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+rowItem[3]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[4]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[5]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[6]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[7]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    if(reportType=="Not Stock")
	{
       row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[8]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
       row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[9]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
       row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[10]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
       row.insertCell(11).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[11]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    	   
	}
    row = table.rows[0];
	row.style.backgroundColor='#87CEFA';
	row.style.height ='40px';
	row.style.fontSize = "20px";
  
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

function funFetchColNames(callingTime) {
	var posName=$('#cmbPOSName').val();
	var type=$('#cmbType').val();
	var reportType=$('#cmbReportType').val();
	var groupwise=$('#cmbGroupName').val();
	var showStockWith=$('#cmbShowStock').val();
	var showZeroBalYN=$('#cmbShowBalStock').val();		 
	var gurl = getContextPath()+"/loadPOSStockFlash.html";
	var abc;
	
	$.ajax({
		type : "GET",
		data:{  fromDate:$('#txtFromDate').val(),
				toDate:$('#txtToDate').val(),
				posName:posName,
				type:type,
				reportType:reportType,
				groupwise:groupwise,
				showStockWith:showStockWith,
				showZeroBalStockYN:showZeroBalYN,
				showZeroBalStockYN:showZeroBalYN,
				time:callingTime,
			},
		url : gurl,
		dataType : "json",
		success : function(response) {
		 	if (response== 0) 
			{
				alert("Data Not Found");
			} 
			else 
			{ 
				var row="",totalRow="";
				
				if(reportType=="Stock")
				{
					$.each(response.listHeader,function(i,item)
					{
		            	if(row=="")
	            		{
		            		row=item;
	            		}
		            	else
		            	{
		            		row=row+"#"+item;
		            	}	
	            	});
					funFillheaderCol(row,"stock");
					
					 $.each(response.listDetails,function(i,item)
					  {
		            	
						 funFillStockTableCol(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7]);
	            	  });
					 
					 $.each(response.totalList,function(i,item)
					 {
		            	if(totalRow=="")
	            		{
		            		totalRow=item;
	            		}
		            	else
		            	{
		            		totalRow=totalRow+"#"+item;
		            	}	
	            	 });
					 if(response.listDetails.length>0)
					  {
						 funFillTotalCol(totalRow,"stock");
					  }
					 
					 
								
				}
				else
				{
					if(response.listDetails.length==0)
					  {
						document.getElementById("production").disabled = true;
					  }
					else
					 {
						document.getElementById("production").disabled = false;
					 }	
						
					$.each(response.listHeader,function(i,item)
					{
		            	if(row=="")
	            		{
		            		row=item;
	            		}
		            	else
		            	{
		            		row=row+"#"+item;
		            	}	
	            	});
					
					funFillheaderCol(row,"Not Stock");
					
					 $.each(response.listDetails,function(i,item)
					  {
				            	
						 funFillReorderStockTableCol(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11]);
			          });
					 
					 $.each(response.totalList,function(i,item)
					 {
		            	if(totalRow=="")
	            		{
		            		totalRow=item;
	            		}
		            	else
		            	{
		            		totalRow=totalRow+"#"+item;
		            	}	
	            	 });
					 if(response.listDetails.length>0)
					  {
						 funFillTotalCol(totalRow,"Not Stock");
					  } 
				}
			}
		}
});
}

		 	function funFillStockTableCol(item0,item1,item2,item3,item4,item5,item6,item7)
			{
				var table = document.getElementById("tblStockFlashReport");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);

			      row.insertCell(0).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"12%\" id=\"txtDate."+(rowCount)+"\" value='"+item0+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
			      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"13%\" id=\"txtDate."+(rowCount)+"\" value='"+item1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"25%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item2+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item3+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+item4+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item5+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item6+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item7+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			}   
		 	
		 	function funFillReorderStockTableCol(item0,item1,item2,item3,item4,item5,item6,item7,item8,item9,item10,item11)
			{
				var table = document.getElementById("tblStockFlashReport");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);

			      row.insertCell(0).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"12%\" id=\"txtDate."+(rowCount)+"\" value='"+item0+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
			      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"13%\" id=\"txtDate."+(rowCount)+"\" value='"+item1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  size=\"25%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item2+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+item3+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+item4+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item5+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item6+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item7+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+item8+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item9+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item10+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(11).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item11+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			} 
		 	
		 	function funFillTotalCol(rowData,reportType) 
			{
				var table = document.getElementById("tblTotal");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    var rowItem=rowData.split("#");
			    var blankData="  ";
			    
			    if(reportType=="Not Stock")
				{
			    	  row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box \"  size=\"25%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
					  row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box \"  style=\"text-align: right\" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					  row.insertCell(2).innerHTML= "<input name=\"listPSPDtl["+(rowCount)+"].strItemName\" readonly=\"readonly\" class=\"Box \"  style=\"text-align: right\" size=\"16%\" id=\"txtCompStk."+(rowCount)+"\" value='"+rowItem[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					  row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+rowItem[3]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					  row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[4]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					  row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[5]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					  row.insertCell(6).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[6]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
				      row.insertCell(7).innerHTML= "<input name=\"listPSPDtl["+(rowCount)+"].dblCompStk\" readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[7]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
				      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[8]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			          row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align: right\" size=\"16%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[9]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			       
			    	   
				}
			    else
			    {
			    	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box \"  style=\"text-align: left\" size=\"45%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
					    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box \"  style=\"text-align: center\" size=\"25%\" id=\"txtDate."+(rowCount)+"\" value='"+rowItem[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					    row.insertCell(2).innerHTML= "<input name=\"listPSPDtl["+(rowCount)+"].strItemName\" readonly=\"readonly\" class=\"Box \"  style=\"text-align: center\" size=\"20%\" id=\"txtCompStk."+(rowCount)+"\" value='"+rowItem[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: center\" size=\"20%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+rowItem[3]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: center\" size=\"20%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[4]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
					    row.insertCell(5).innerHTML= "<input readonly=\"readonly\" class=\"Box \" style=\"text-align: center\" size=\"20%\" id=\"txtVariance."+(rowCount)+"\" value='"+rowItem[5]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
						

					   
			    }	
			    
			}
		 	
		 	function funResetFields(){
		 		$('#tblStockFlashReport tbody').empty();	
		 		$('#tblStockFlashHeader tbody').empty();	
		 		$('#tblTotal tbody').empty();
		 	}
		 	
</script>
</head>

<body>
	<div id="formHeading">
		<label>Stock Flash Report</label>
	</div>
	<br />
	<s:form name="POSStockFlashReport" method="POST" action="rptPOSStockFlashReport.html?saddr=${urlHits}" target="_blank" 
			class="formoid-default-skyblue"
			style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 10%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input  col-lg-3 col-sm-3 col-xs-3" style="width: 15%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input  col-lg-3 col-sm-3 col-xs-3" style="width: 10%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{4}-\d{1,2}-\d{1,2}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 17%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{4}-\d{1,2}-\d{1,2}"  style="width: 100%;"/>	
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Type</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbType" path="strType" items="${typeList}" >
				    				<s:option value="Raw Material">Raw Material</s:option>
				    				<s:option value="Menu Item">Menu Item</s:option>
				    				<s:option value="Both">Both</s:option>
				    			</s:select>
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							
							<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 10%;"> 
		    					<label class="title">Report Type</label>
		    				</div>
		    				<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 15%;"> 
								<s:select id="cmbReportType" path="strReportType"  items="${reportTypeList}">
				    				<s:option value="Stock">Stock</s:option>
				    				<s:option value="Reorder">Reorder</s:option>
				    			</s:select>
							</div>
							<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 10%;"> 
		    					<label class="title">Group Wise</label>
		    				</div>
		    				<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 15%;"> 
								<s:select id="cmbGroupName" path="strGroupName" items="${groupMap}">
				    				<s:option value="All">ALL</s:option>
				    				<s:option value="Beverages">BEVERAGES</s:option>
				    				<s:option value="Food">FOOD</s:option>
				    				<s:option value="Liquer">LIQUER</s:option>
				    			</s:select>
							</div>
							<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 11%;"> 
		    					<label class="title">Show Stock With</label>
		    				</div>
		    				<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 14%;"> 
								<s:select id="cmbShowStock" path="strShowStock" items="${viewByList}">
				    				<s:option value="Both">Both</s:option>
				    				<s:option value="Positive">Positive</s:option>
				    				<s:option value="Negative">Negative</s:option>
				    			</s:select>
							</div>
							<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 12%;"> 
		    					<label class="title">Show 0 Bal Stock </label>
		    				</div>
		    				<div class="element-input col-lg-3 col-sm-3 col-xs-3" style="width: 10%;"> 
								<s:select id="cmbShowBalStock" path="strShowBalStock" items="${showZeroBalList}">
				    				<s:option value="Yes">Yes</s:option>
				    				<s:option value="No">No</s:option>
				    			</s:select>
							</div>
					</div>
					
		    		<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblStockFlashHeader" class="transTablex"
										style="width: 100%; text-align: center !important; ">
					
					    		</table>
								
								
								<table id="tblStockFlashReport" class="transTablex" 
											style="width: 100%; text-align: center !important;">
								</table>
								
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 60px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblTotal" class="transTablex" bstyle="width: 100%; text-align:">
					    				<col style="width:60%"><!--  COl1   -->
					   			</table>
							</div>
					</div>
		    		
		    		<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 280px;">
		    				
		    				<div class="col-lg-4 col-sm-4 col-xs-4" style="width: 100%;">
					  		
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="View" id="view" onClick="funLoadTableData()" /> </div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Print" id="print" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="submit" value="Export" id=export" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Production" id="production" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Reset" id="reset" onClick="funResetFields()" /></div>
								
			  		  		</div>
			  		  </div>
				
			  		  <div class="row" style="background-color: #fff; display:block; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
			  		  			<s:input type="hidden" id="txtAuditType" name="txtAuditType" path="strPSPCode" />
							</div>
					  </div>			
					
					<div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 50%; left: 50%; padding: 2px;">
							<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
					</div>
		</div>

	</s:form>

</body>
</html>