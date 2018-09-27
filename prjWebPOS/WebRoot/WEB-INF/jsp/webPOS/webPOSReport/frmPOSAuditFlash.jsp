<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GROUP MASTER</title>
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


var gEnableShiftYN="${gEnableShiftYN}";
	$(document).ready(function() {
		var message='';
		<%if (session.getAttribute("success") != null) {
            if(session.getAttribute("successMessage") != null){%>
            message='<%=session.getAttribute("successMessage").toString()%>';
            <%
            	session.removeAttribute("successMessage");
            }
			boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
			session.removeAttribute("success");
			if (test) {
			%>	
			alert(message);
		<%
		}}%>		

		$(document).ajaxStart(function() {
			$("#wait").css("display", "block");
		});
		$(document).ajaxComplete(function() {
			$("#wait").css("display", "none");
		});

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
			
   			
		funLoadTableData();
		
		
	    }); 
		
		$("[type='reset']").click(function(){
			location.reload(true);
		});
		
		$("form").submit(function(event){
			var table = document.getElementById("tblAuditFlash");
			var rowCount = table.rows.length;
			if (rowCount > 2){
				$("#txtFromDate").val(fDate);
				$("#txtToDate").val(tDate);
				return true;
			} else {
				alert("Data Not Available");
				return false;
			}
		});

		$("#btnSubmit1").click(function(event) {
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
				if(funCalculateDateDiff(fromDate,toDate)){
					fDate=fromDate;
					tDate=toDate;
					funFetchColNames( $("#btnSubmit1").val());
				}
			}
		});

	
	
	$("#btnSubmit2").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit2").val());
			}
		}
	});
	
	$("#btnSubmit3").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit3").val());
			}
		}
	});
	
	$("#btnSubmit4").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit4").val());
			}
		}
	});
	
	$("#btnSubmit5").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit5").val());
			}
		}
	});
	
	$("#btnSubmit6").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit6").val());
			}
		}
	});
	
	$("#btnSubmit7").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit7").val());
			}
		}
	});
	
	$("#btnSubmit8").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit8").val());
			}
		}
	});
	
	$("#btnSubmit9").click(function(event) {
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
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames( $("#btnSubmit9").val());
			}
		}
	});

	if(gEnableShiftYN=='Y')
	{
		document.getElementById("lblShift").style.visibility = "visible"; 
		document.getElementById("txtShiftCode").style.visibility = "visible"; 
	}
	else
	{
		document.getElementById("lblShift").style.visibility = "hidden";
		document.getElementById("txtShiftCode").style.visibility = "hidden"; 
		
	}

});

	
	function funLoadTableData()
	{
		funFetchColNames("Modified Bill");
		
	}
	
	
	
	function funDeleteTableAllRows()
	{
		$('#tblAuditFlash tbody').empty();
		$('#tblTotal tbody').empty();
		var table = document.getElementById("tblAuditFlash");
		var rowCount1 = table.rows.length;
		if(rowCount1==0){
			return true;
		}else{
			return false;
		}
	}
	
	function funCalculateDateDiff(fromDate,toDate) {

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
        	 return false
         }
	}

	
	function funFetchColNames(btnId) {
		var fromDate = $('#txtFromDate').val();
		 var toDate = $('#txtToDate').val();
		var strReportType=$('#cmbReportType').val();
		var posName=$('#cmbPOSName').val();
		var strUserName=$('#cmbUserName').val();
		var strReason=$('#cmbReason').val();
		var strSorting = $('#cmbSorting').val();
		var strType = $('#cmbType').val();	
		
		var auditType=btnId;
		document.getElementById("txtAuditType").value=btnId;
			var gurl = getContextPath() + "/loadAuditFlash.html";
		
		
		$.ajax({
			type : "GET",
			data:{ fromDate:fromDate,
					toDate:toDate,
					strReportType:strReportType,
					posName:posName,
					strUserName:strUserName,
					strReason:strReason,
					auditType:btnId,
					strSorting:strSorting,
					strType:strType,
				},
			url : gurl,
			dataType : "json",
			success : function(response) {
				if (response.RowCount == 0  ) {
					alert("Data Not Found");
				} else {
					
					//Add Sub Category Headers
					funAddHeaderRow(response.ColHeader);
					
					//Add Size Names And Headers
					switch(auditType)
			 {
					case "Modified Bill":
			   {	
				   if(strReportType=="Summary")
					{
					 
				$.each(response.listArr,function(i,item){
		            	
						funFillTableWith11Col(item[0],item[1],item[2],item[3],item[4].toFixed(2),item[5].toFixed(2),item[6].toFixed(2),item[7],item[8],item[9],item[10]);
	            	});
			   }	
				   else
				   {	
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith10Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9]);
			            	});
							
					}
					funFillTotalData(response.totalList);
			   }
			   break;
					case "Voided Bill":
			   {	
				   if(strReportType=="Summary")
					{
					 
				$.each(response.listArr,function(i,item){
		            	
						funFillTableWith11Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7].toFixed(2),item[8],item[9],item[10]);
	            	});
			   }
				   else
				   {	
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith12Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11],item[12]);
			            	});
							
					   }
					funFillTotalData(response.totalList);
			   }
			   break;
					case "Voided Advanced Order":
			   {
				   if(strReportType=="Summary")
					{
					 
				$.each(response.listArr,function(i,item){
		            	
						funFillTableWith8Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7]);
	            	});
					}
				   else
				   {	
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith10Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9]);
			            	});
							
					   }
					funFillTotalData(response.totalList);
			   }
			   break;
					case "Line Void":
					   {
							 
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith8Col(item[0],item[1],item[2],item[3],item[4],item[5].toFixed(2),item[6],item[7]);
			            	});
							
						funFillTotalData(response.totalList);   
					   }
					   break;
					case "Voided KOT":
					   {
						   if(strReportType=="Summary")
							{
							 
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith10Col(item[0],item[1],item[2],item[3],item[4],item[5].toFixed(2),item[6],item[7],item[8],item[9]);
			            	});
							}
						   else
						   {	
								$.each(response.listArr,function(i,item){
						            	
									funFillTableWith12Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11]);
					            	});
									
							   }
							funFillTotalData(response.totalList);
					   }
					   break;
					case "Time Audit":
					   {
							 
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith9Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8]);
			            	});
							
						   
					   }
					   break;
					case "KOT Analysis":
					   {
						if(strReportType=="Summary")
						{	 
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith9Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8]);
			            	});
						}
						else
						{
							$.each(response.listArr,function(i,item){
				            	
								funFillTableWith11Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10]);
			            	});
						}
						funFillTotalData(response.totalList); 
					   }
					   break;
					case "Moved KOT":
					   {
						   if(strReportType=="Summary")
							{
							 
						$.each(response.listArr,function(i,item){
				            	
								funFillTableWith9Col(item[0],item[1],item[2],item[3],item[4],item[5].toFixed(2),item[6],item[7],item[8]);
			            	});
							}
						   else
						   {	
								$.each(response.listArr,function(i,item){
						            	
									funFillTableWith12Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11]);
					            	});
									
							   }
							funFillTotalData(response.totalList);
					   }
					case "Waiter Audit":
					{
						$.each(response.listArr,function(i,item){
			            	
							funFillTableWith6Col(item[0],item[1],item[2],item[3].toFixed(2),item[4],item[5].toFixed(2));
		            	});	
						funFillTotalData(response.totalList);
					}	
		
			 }	
				
				}//Else block Of Response
				
				
			
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
	
	function funFillTotalData(rowData) 
	{
		var table = document.getElementById("tblTotal");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    for(var i=0;i<rowData.length;i++)
	    	 {
	   		
	    	if(typeof rowData[i] == 'number')
	    	{
	    		row.insertCell(i).innerHTML = "<input readonly=\"readonly\" class=\"cell\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i].toFixed(2)+"' />";
	    	}	
	    	else
	    	{
	    		row.insertCell(i).innerHTML = "<input readonly=\"readonly\" class=\"cell\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    	}
	   		
	   		 }
		
	  
	}
	
 	function funAddHeaderRow(rowData){
		var table = document.getElementById("tblAuditFlash");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
		    for(var i=0;i<rowData.length;i++){
		    	if(i==0){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    		 } else {
		    			row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    		}
				}
		} 
 	function funFillTableWith12Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt,strUserCreated,strUserEdited,strReasonName,strItemName,str12)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

	      row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strUserCreated+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strUserEdited+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strReasonName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strItemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(11).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\"  value='"+str12+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	}
 	
 	function funFillTableWith11Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt,strUserCreated,strUserEdited,strReasonName,strItemName)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

	      row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strUserCreated+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strUserEdited+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strReasonName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strItemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	}
		
 	function funFillTableWith10Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt,strUserCreated,strUserEdited,strReasonName)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

		 row.insertCell(0).innerHTML= "<input  class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	     
	      row.insertCell(5).innerHTML= "<input class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strUserCreated+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strUserEdited+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strReasonName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      
	}
 	
 	function funFillTableWith9Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt,strUserCreated,strUserEdited)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

		 row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	     row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strUserCreated+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strUserEdited+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    
	      
	}
	
 	function funFillTableWith8Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt,strUserCreated)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

		 row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	     row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\"  value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strUserCreated+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      
	}
 	function funFillTableWith7Col(strBillNo,strBillDate,strModifiedDate,strEntryTime,strModifyTime,dblBillAmt,dblNetAmt)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

		 row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+strBillDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+strModifiedDate+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+strEntryTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+strModifyTime+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\"  value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+dblNetAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	     
	      
	}
 	
 	function funFillTableWith6Col(strWaiterName,noOfKot,noOfVoidKot,noOfVoidKotPer,noOfMoveKot,noOfMoveKotPer)
	{
		var table = document.getElementById("tblAuditFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);

		 row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strWaiterName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+noOfKot+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+noOfVoidKot+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtPhyStk."+(rowCount)+"\" value='"+noOfVoidKotPer+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtVariance."+(rowCount)+"\" value='"+noOfMoveKot+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      row.insertCell(5).innerHTML= "<input   class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txt6."+(rowCount)+"\" value='"+noOfMoveKotPer+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	      
	}
	
	
</script>


</head>

<body>
	<div id="formHeading">
		<label>POS Audit Flash</label>
	</div>
	
	<s:form name="POSAuditFlash" method="POST"
		action="processAuditFlash.html?saddr=${urlHits}"
		target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">User</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbUserName" name="cmbUserName" path="strSGName" items="${userList}" />
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 17%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}"  style="width: 100%;"/>	
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Type</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbType" path="strType" >
				    				<s:option value="All">All</s:option>
				    				<s:option value="Full Void">Full Void</s:option>
				    				<s:option value="Item Void">Item Void</s:option>
				    			</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Reason</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbReason" name="cmbReason" path="strReasonMaster" items="${ReasonMasterList}" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbReportType" name="cmbReportType" path="strReportType" items="${listType}"/>
							</div>
							<div class="element-input col-lg-6" style="width: 5.5%;"> 
		    					<label class="title">Sort</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 17%;"> 
								<s:select id="cmbSorting" name="cmbSorting" path="strSort" items="${listSorting}"/>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<input type="submit" value="EXPORT" id="submit" />
							</div>
					</div>
					
		    		<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblAuditFlash" style="width: 100%; text-align: center !important;">
								
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 60px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblTotal" style="width: 100%; text-align: center !important;margin-top: 3px;">
								
									<tbody style="border-top: none;">
									</tbody>
									
								</table>
								
							</div>
					</div>
		    		
		    		<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
		    			
		    				<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
					  		
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Modified Bill" id="btnSubmit1" /> </div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Voided Bill" id="btnSubmit2" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Voided Advanced Order" id="btnSubmit3" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Line Void" id="btnSubmit4" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Voided KOT" id="btnSubmit5" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Time Audit" id="btnSubmit6" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="KOT Analysis" id="btnSubmit7" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Moved KOT" id="btnSubmit8" /></div>
								<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 10%;"><input type="button" value="Waiter Audit" id="btnSubmit9" /></div>
								
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