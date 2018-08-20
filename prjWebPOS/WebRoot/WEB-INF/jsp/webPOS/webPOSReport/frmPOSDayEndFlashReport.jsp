<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Day End Flash Report</title>
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
	padding-left: 0;
	width: 115px
}

.header {
	background: inherit;
	border: 0 solid #060006;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-weight: bold;
	outline: 0 none;
	padding-left: 0;
	width: 60px
}
</style>
<script type="text/javascript">
 
var date="";
$(document).ready(function() {
	
	var POSDate="${gPOSDate}"
	var startDate="${gPOSDate}";
  	var Date = startDate.split(" ");
	var arr = Date[0].split("-");
	Dat=arr[2]+"-"+arr[1]+"-"+arr[0];
	date = arr[2]+"-"+arr[1]+"-"+arr[0];
	$("#txtFromDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtFromDate" ).datepicker('setDate', Dat);
	$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtToDate" ).datepicker('setDate', Dat);
	var fromDate = date;
	var toDate = date;
	funLoadTableData(fromDate,toDate);
	
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

// 	$("#txtFromDate").datepicker({
// 		dateFormat : 'dd-mm-yy'
// 	});
// 	$("#txtFromDate").datepicker('setDate', 'today');

// 	$("#txtToDate").datepicker({
// 		dateFormat : 'dd-mm-yy'
// 	});
// 	$("#txtToDate").datepicker('setDate', 'today');
	
	$("[type='reset']").click(function(){
		location.reload(true);
	});

	$("#execute").click(function(event) {
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
				funFetchColNames();
			}
		}
	});

});

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
    	 return false;
     }
}

function funLoadTableData(fromDate,toDate)
{
	
	fDate=fromDate;
	tDate=toDate;
	funDeleteTableAllRows();
	funFetchColNames();
	
}

function funDeleteTableAllRows()
{
	$('#tblDayEndFlash tbody').empty();
	$('#tblTotal tbody').empty();
	var table = document.getElementById("tblDayEndFlash");
	var rowCount1 = table.rows.length;
	if(rowCount1==0){
		return true;
	}else{
		return false;
	}
}



function funFetchColNames() {
	
	var posName=$('#cmbPOSName').val();
	var gurl = getContextPath()+"/loadDayEndFlash.html";
	var abc;
	
	$.ajax({
		type : "GET",
		data:{ fromDate:fDate,
				toDate:tDate,
				posName:posName,
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
			 
			$.each(response.List,function(i,item){
	            	
					funFillTableCol(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11],item[12],item[13],item[14],item[15],item[16],item[17],item[18],item[19],item[20],item[21],item[22],item[23],item[24]);
            	});
				
				funFillTotalCol(response.totalList);
        	
			}
			
		}
});
}

		
	
		

		 	function funFillTableCol(item0,item1,item2,item3,item4,item5,item6,item7,item8,item9,item10,item11,item12,item13,item14,item15,item16,item17,item18,item19,item20,item21,item22,item23,item24)
			{
				var table = document.getElementById("tblDayEndFlash");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);

			      /*row.insertCell(0).innerHTML= "<input   class=\"Box \" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+strBillNo+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; */
			      row.insertCell(0).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item0+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; 
			      row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item2+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item3+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item4+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      /* row.insertCell(5).innerHTML= "<input   class=\"Box \" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+dblBillAmt+"' onclick=\"funGetSelectedRowIndex(this)\"/>"; */
			      row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"  value='"+item5+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item6+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item7+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item8+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item9+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item10+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(11).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item11+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(12).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item12+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(13).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item13+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(14).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item14+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(15).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item15+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(16).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item16+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(17).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item17+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(18).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item18+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(19).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item19+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(20).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item20+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(21).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item21+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(22).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item22+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(23).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item23+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			      row.insertCell(24).innerHTML= "<input  readonly=\"readonly\" class=\"Box \"   value='"+item24+"' onclick=\"funGetSelectedRowIndex(this)\"/>";

			}

		 	
		 	function funFillTotalCol(rowData) 
			{
				var table = document.getElementById("tblTotal");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    
			    for(var i=0;i<rowData.length;i++)
			    	 {
			   		
			 	   			row.insertCell(i).innerHTML = "<input type=\"text\" style=\"text-align: right;\" readonly=\"readonly\" class=\"cell\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
			   		
			   		 }
				
			  
			}
			

</script>


</head>

<body>
	<div id="formHeading">
		<label>Day End Flash Report</label>
	</div>
	<br />
	<br />
	<s:form name="POSDayEndFlashReport" method="POST" action="processDayEndFlashReport.html?saddr=${urlHits}"
		target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}"  style="width: 100%;"/>	
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							
							<div style="border: 1px solid #ccc; display: block; height: 200px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table style="width: 342%; text-align: center !important;">
								
									<thead style="background-color: #85cdffe6;">
											<td style="width: 4%; border-right: 1px solid #ccc;">POS</td>
											<td style="width: 4%; border-right: 1px solid #ccc;">POS Date</td>
											<td style="width: 4%; border-right: 1px solid #ccc;">HD Amt</td>
											<td style="width: 3.9%; border-right: 1px solid #ccc;">Dining Amt</td>
											<td style="width: 4%; border-right: 1px solid #ccc;">Take Away</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Total Sale</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Float</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Cash</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Advance</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Transfer In</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Total Receipt</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Pay</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">With Drawal</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Tranf Out</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Refund</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Total Pay</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Cash In Hand</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">No Of Bill</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">No Of Voided Bill</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">No Of Modify Bil</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Stock Adjustment No</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Excise Bill Generation</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Net Sale</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">Gross Sale</td>
											<td style="width: 4%; border-right: 1px solid #ccc;"">APC</td>
									</thead>
								
								</table>
								
								<table id="tblDayEndFlash" style="width: 100%; text-align: center !important;">
								
									<tbody style="border-top: 1px solid #ccc;">
										
									</tbody>
								
								</table>
								
						 </div>
							
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							
							<div style="border: 1px solid #ccc; display: block; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
							
								<table id="tblTotal" style="width: 100%; text-align: center !important;">
								
									<tbody style="border-top: 1px solid #ccc;">
										
									</tbody>
								
								</table>
								
							</div>
					</div>
					
					<br/>
					
				    <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%; margin-left: 10%;">
						<p align="center">
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="EXECUTE" id="execute" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="EXPORT" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" id="reset" /></div>
						</P>					
					</div>

					<div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 60%; left: 40%; padding: 2px;">
						<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
					</div>
					
		</div>
		
		
		
<!-- 		<div style="width: 100%;"> -->
<!-- 			<div style="width: 100%;"> -->

<!-- 				<table class="masterTable" style="margin-left: 100px;"> -->
<!--                  <tr> -->
<!-- 					<td width="140px">POS Name</td> -->
<%-- 					<td colspan="3"><s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" cssClass="BoxW124px" items="${posList}" > --%>
					
<%-- 					 </s:select></td> --%>
				
						
<!-- 						<td><label>From Date</label></td> -->
<%-- 						<td><s:input id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" --%>
<%-- 								cssClass="calenderTextBox" /></td> --%>

<!-- 						<td><label>To Date</label></td> -->
<%-- 						<td><s:input id="txtToDate" required="required" path="toDate" --%>
<%-- 								pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox" /></td> --%>

<!-- 					</tr> -->
					

<!-- 				</table> -->
<!-- 			</div> -->
<!-- 			<div style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 400px; margin-left: 100px; overflow-x: scroll; width: 80%;"> -->
						
<!-- 									<table id="tblCol" -->
<!-- 										style="width: 100%; border: #0F0;margin-left: 0px; table-layout: fixed; overflow: scroll" -->
<!-- 										class="transTablex col4-center"> -->
									
<!--             						<th style="border: 1px white solid;width:50%"><label>POS</label></th> -->
<!-- 									<th style="border: 1px white solid;width:70%"><label>POS Date</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>HD Amt</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Dining Amt</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Take Away</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Total Sale</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Float</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Cash</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Advance</label></th> -->
<!-- 										<th style="border: 1px  white solid;width:50%"><label>Transfer In</label></th> -->
<!-- 										<th style="border: 1px  white solid;width:60%"><label>Total Receipt</label></th> -->
<!-- 										<th style="border: 1px  white solid;width:50%"><label>Pay</label></th> -->
<!-- 										<th style="border: 1px  white solid;width:50%"><label>With Drawal</label></th> -->
<!-- 										<th style="border: 1px  white solid;width:50%"><label>Tranf Out</label></th> -->
									
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Refund</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Total Pay</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Cash In Hand</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>No Of Bill</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>No Of Voided Bill</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>No Of Modify Bil</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Stock Adjustment No</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Excise Bill Generation</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Net Sale</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>Gross Sale</label></th> -->
<!-- 									<th style="border: 1px  white solid;width:50%"><label>APC</label></th> -->
								
<!-- 								</table> -->
								
								
									
<!-- 									<div style="background-color: #a4d7ff; border: 1px solid #ccc; display: block;margin-left: 0px; height: 550px; overflow-x: hidden; overflow-y: hidden; width: 100%;"> -->
<!-- 									<table id="tblDayEndFlash" -->
<!-- 										style="width: 100%; border: #0F0;margin-left: 0px; table-layout: fixed; overflow: scroll" -->
<!-- 										class="transTablex col11-center">	 -->
														    
<%-- 											<col style="width:50%"><!--  COl1   --> --%>
<%-- 											<col style="width:70.5%"><!--  COl2   --> --%>
<%-- 											<col style="width:50.5%"><!--  COl3   --> --%>
<%-- 											<col style="width:50%"><!--  COl4   --> --%>
<%-- 											<col style="width:50.5%"><!--  COl5   --> --%>
<%-- 											<col style="width:50%"><!--  COl6   --> --%>
<%-- 											<col style="width:50%"><!--  COl7   --> --%>
<%-- 											<col style="width:50%"><!--  COl8   --> --%>
<%-- 											<col style="width:50%"><!--  COl9   --> --%>
<%-- 											<col style="width:50%"><!--  COl10   --> --%>
<%-- 											<col style="width:60%"><!--  COl11   --> --%>
<%-- 											<col style="width:50%"><!--  COl12   --> --%>
<%-- 											<col style="width:50%"><!--  COl13   --> --%>
<%-- 											<col style="width:50%"><!--  COl14   --> --%>
<%-- 											<col style="width:50%"><!--  COl15   --> --%>
<%-- 											<col style="width:50%"><!--  COl16   --> --%>
<%-- 											<col style="width:50%"><!--  COl17   --> --%>
<%-- 											<col style="width:50%"><!--  COl18  --> --%>
<%-- 											<col style="width:50%"><!--  COl19  --> --%>
<%-- 											<col style="width:50%"><!--  COl20  --> --%>
<%-- 											<col style="width:50%"><!--  COl21  --> --%>
<%-- 											<col style="width:50%"><!--  COl22  --> --%>
<%-- 											<col style="width:50%"><!--  COl23  --> --%>
<%-- 											<col style="width:50%"><!--  COl24  --> --%>
<%-- 											<col style="width:50%"><!--  COl25  --> --%>
											 
																			
															
<!-- 									</table> -->
<!-- 									</div> -->
									
<!-- 								</div> -->
	
			
<!-- 		</div> -->
	
<!-- 			<div -->
<!-- 				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 80px; margin-left: 100px; overflow-x: scroll; overflow-y: scroll; width: 80%;"> -->
				
	
<!-- 				<table id="tblTotal" class="transTablex" -->
<!-- 					style="width: 80%; "> -->
					
<%-- 											<col style="width:50%"><!--  COl1   --> --%>
<%-- 											<col style="width:60%"><!--  COl2   --> --%>
<%-- 											<col style="width:30%"><!--  COl3   --> --%>
<%-- 											<col style="width:50%"><!--  COl4   --> --%>
<%-- 											<col style="width:30%"><!--  COl5   --> --%>
<%-- 											<col style="width:30%"><!--  COl6   --> --%>
<%-- 											<col style="width:30%"><!--  COl7   --> --%>
<%-- 											<col style="width:30%"><!--  COl8   --> --%>
<%-- 											<col style="width:40%"><!--  COl9   --> --%>
<%-- 											<col style="width:40%"><!--  COl10   --> --%>
<%-- 											<col style="width:60%"><!--  COl11   --> --%>
<%-- 											<col style="width:10%"><!--  COl12   --> --%>
<%-- 											<col style="width:40%"><!--  COl13   --> --%>
<%-- 											<col style="width:30%"><!--  COl14   --> --%>
<%-- 											<col style="width:30%"><!--  COl15   --> --%>
<%-- 											<col style="width:30%"><!--  COl16   --> --%>
<%-- 											<col style="width:50%"><!--  COl17   --> --%>
<%-- 											<col style="width:50%"><!--  COl18  --> --%>
<%-- 											<col style="width:50%"><!--  COl19  --> --%>
<%-- 											<col style="width:50%"><!--  COl20  --> --%>
<%-- 											<col style="width:50%"><!--  COl21  --> --%>
<%-- 											<col style="width:50%"><!--  COl22  --> --%>
<%-- 											<col style="width:50%"><!--  COl23  --> --%>
<%-- 											<col style="width:50%"><!--  COl24  --> --%>
<%-- 											<col style="width:50%"><!--  COl25  --> --%>
<!-- 				</table> -->
<!-- 			</div> -->
		
<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			<input type="button" value="Execute" class="form_button"id="execute" />  -->
<!-- 				<input type="submit" value="Export"	class="form_button" id="export" />  -->
<!-- 				<input type="reset" value="Reset"class="form_button" id="reset" /> -->

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