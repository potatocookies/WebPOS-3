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
	padding-left: 0;
	width: 100%
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
			var table = document.getElementById("tblCallCenterOrderFlash");
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

		$("#btnSubmit").click(function(event) {
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
				if(CalculateDateDiff(fromDate,toDate)){
					fDate=fromDate;
					tDate=toDate;
					funFetchColNames();
				}
			}
		});

	});
	
	
	function funDeleteTableAllRows()
	{
		$('#tblCallCenterOrderFlash tbody').empty();
		$('#tblTotal tbody').empty();
		var table = document.getElementById("tblCallCenterOrderFlash");
		var rowCount1 = table.rows.length;
		if(rowCount1==0){
			return true;
		}else{
			return false;
		}
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
        	 return false
         }
	}

	function funLoadTableData()
	{
	
       	
		funFetchColNames();
		
	}
	
	function funFetchColNames() 
	{
		
		posCode=$("#cmbPOSName").val();		
		fDate=  $("#txtFromDate").val();
		tDate=	$("#txtToDate").val();
		cmbType=$("#cmbType").val();
		
		
		
		
		var gurl = getContextPath() + "/funLoadCallCenterOrderFlash.html";
		
		
		$.ajax({
			type : "GET",
			data:{ 
				fromDate:fDate,
				toDate:tDate,
				posCode:posCode,
				cmbType:cmbType,
				},
			url : gurl,
			dataType : "json",
			success : function(response)
			{
				if (response.RowCount == 0  )
				{
					alert("Data Not Found");
				} 
				else 
				{
					
					if(cmbType=="Summary")
					{
						//Add Sub Category Headers
						funAddSummaryHeaderRow(response.coumnNames);
		 														
						$.each(response.listOfOrderHdData,function(i,objHDOrder)
						{
					       	funSummaryFillHDOrders(objHDOrder.strOrderNo,objHDOrder.strBillNo,objHDOrder.tmeTime,objHDOrder.strCustomerName,objHDOrder.strMobileNo,objHDOrder.strDPName,objHDOrder.strCustAddressLine1);					       	           
				        });	   
					}
					else//details
					{
						//Add Sub Category Headers
						funAddHeaderRow(response.coumnNames);
		 														
						$.each(response.listOfOrderHdData,function(i,objHDOrder)
						{
					       	funFillHDOrders(objHDOrder.strOrderNo,objHDOrder.strCustomerName,objHDOrder.strPosName,objHDOrder.dteDate,objHDOrder.strCustAddressLine1);
					        $.each(objHDOrder.listOfOrderDtlData,function(j,objDtlOrder)
					        {
					        	funFillDtlOrders(objDtlOrder.strItemName,objDtlOrder.dblRate,objDtlOrder.dblQuantity,objDtlOrder.dblAmount,objDtlOrder.dblTaxAmount);
					        });	            
				        });	   
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
	
	
	function funAddSummaryHeaderRow(rowData)
 	{
		var table = document.getElementById("tblCallCenterOrderFlash");
		
		
		
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
		    for(var i=0;i<rowData.length;i++)
		    {
		    	if(i==0)
		    	{
	    			 row.insertCell(i).innerHTML= "<input type=\"text\"  readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    		} 
		    	else 
		    	{
		    		row.insertCell(i).innerHTML= "<input type=\"text\"  readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    	}
			}
		}
	
	
	function funSummaryFillHDOrders(strOrderNo,strBillNo,tmeTime,strCustomerName,strMobileNo,strDPName,strCustAddressLine1)
 	{
 		var table = document.getElementById("tblCallCenterOrderFlash");
 		var rowCount = table.rows.length;
 		var row = table.insertRow(rowCount);
 		   		
 	    row.insertCell(0).innerHTML= "<input   readonly=\"readonly\" style=\"text-align: left; color:blue;\"  class=\"Box \"   value='"+strOrderNo+"'>";
 	    row.insertCell(1).innerHTML= "<input   style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strBillNo+"'>";
 	    row.insertCell(2).innerHTML= "<input   readonly=\"readonly\" style=\"text-align: left; color:blue;\"  class=\"Box \"   value='"+tmeTime+"'>";
 	    row.insertCell(3).innerHTML= "<input   style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strCustomerName+"'>"; 	   
 	    row.insertCell(4).innerHTML= "<input    style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strMobileNo+"'>";
 	    row.insertCell(5).innerHTML= "<input    style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strDPName+"'>";
 	   row.insertCell(6).innerHTML= "<input    style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strCustAddressLine1+"'>";
 		  

 	}
	
	
	
	
	
	
 	function funAddHeaderRow(rowData)
 	{
		var table = document.getElementById("tblCallCenterOrderFlash");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
		    for(var i=0;i<rowData.length;i++)
		    {
		    	if(i==0)
		    	{
	    			 row.insertCell(i).innerHTML= "<input type=\"text\"  readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    		} 
		    	else 
		    	{
		    		row.insertCell(i).innerHTML= "<input type=\"text\"  readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    	}
			}
		}
 	
 	function funFillHDOrders(strOrderNo,strCustomerName,strPosName,dteDate,strCustAddressLine1)
 	{
 		var table = document.getElementById("tblCallCenterOrderFlash");
 		var rowCount = table.rows.length;
 		var row = table.insertRow(rowCount);
 		   		
 	    row.insertCell(0).innerHTML= "<input   readonly=\"readonly\" style=\"text-align: left; color:blue;\"  class=\"Box \"   value='"+strOrderNo+"'>";
 	    row.insertCell(1).innerHTML= "<input  style=\"text-align: left; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strCustomerName+"'>";
 	    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" style=\"text-align: right; color:blue;\"  class=\"Box \"   value='"+strPosName+"'>";
 	    row.insertCell(3).innerHTML= "<input style=\"text-align: right; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+dteDate+"'>"; 	   
 	   row.insertCell(4).innerHTML= "<input style=\"text-align: right; color:blue;\"   readonly=\"readonly\" class=\"Box \"  value='"+strCustAddressLine1+"'>";
 		  

 	}
 	
 	
 	function funFillDtlOrders(strItemName,dblRate,dblQuantity,dblAmount,dblTaxAmount)
 	{
 		var table = document.getElementById("tblCallCenterOrderFlash");
 		var rowCount = table.rows.length;
 		var row = table.insertRow(rowCount);
 		   		
 	    row.insertCell(0).innerHTML= "<input name=\"strItemName\" readonly=\"readonly\" class=\"Box \"  id=\"strItemName."+(rowCount)+"\" value='"+strItemName+"'>";
 	    row.insertCell(1).innerHTML= "<input name=\"dblRate\"  readonly=\"readonly\" style=\"text-align: right;\" class=\"Box \"  id=\"dblRate."+(rowCount)+"\" value='"+dblRate+"'>";
 	    row.insertCell(2).innerHTML= "<input name=\"dblQuantity\"  readonly=\"readonly\" style=\"text-align: right;\" class=\"Box \"  id=\"dblQuantity."+(rowCount)+"\" value='"+dblQuantity+"'>";
 	   row.insertCell(3).innerHTML= "<input name=\"dblAmount\"  readonly=\"readonly\" style=\"text-align: right;\" class=\"Box \"  id=\"dblAmount."+(rowCount)+"\" value='"+dblAmount+"'>";
 	  row.insertCell(4).innerHTML= "<input name=\"dblTaxAmount\"  readonly=\"readonly\" style=\"text-align: right;\" class=\"Box \"  id=\"dblTaxAmount."+(rowCount)+"\" value='"+dblTaxAmount+"'>";
 	    
 		  

 	}
 	
 	function funFillTotalData(rowData) 
	{
		var table = document.getElementById("tblTotal");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    for(var i=0;i<rowData.length;i++)
	    {	   		
				row.insertCell(i).innerHTML = "<input type=\"text\" readonly=\"readonly\" class=\"cell\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";	   	
	    }		 
	}
	
	
	
	function funHelp(transactionName)
	 
	{	
 		fieldName=transactionName;
 		// window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
    }
	
	function funSetData(code)
	{
		$("#txtCustomerCode").val(code);
		var searchurl=getContextPath()+"/loadPOSCustomerMasterData.html?POSCustomerCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strCustomerTypeMasterCode=='Invalid Code')
			        	{
			        		alert("Invalid Customer  Code");
			        		$("#txtCustomerCode").val('');
			        	}
			        	else
			        	{
				        	$("#txtCustomerCode").val(response.strCustomerCode);
				        	$("#txtCustomerName").val(response.strCustomerName);
				        	
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

<body>
	<div id="formHeading">
		<label>Call Center Order Flash</label>
	</div>
	<br />
	<br />
	<s:form name="POSDayWiseSalesSummeryFlash" method="POST" action="rptCallCenterOrderFlash.html?saddr=${urlHits}"		target="_blank">
		<div>
			<div>
				<table class="masterTable" style="margin:auto;">
				
				<tr>
					<td width="30%">&emsp;&ensp;&emsp;&ensp;
						<label>POS Name</label></td>
							<td><s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" cssClass="BoxW124px" items="${mapPOSName}" >
						</s:select></td>
					<td></td>
											
						<td><label>From Date</label></td>
						<td><s:input id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox" /></td>
						<td><label>To Date</label></td>
						<td><s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox" /></td>
					</tr>
					<tr>					
						<td >&emsp;&ensp;&emsp;&ensp;
							<s:select id="cmbType" path="strType" cssClass="BoxW124px">
					    		<s:option value="Summary">Summary</s:option>
					    		<s:option value="Detail">Detail</s:option>
					    	</s:select>
						</td>	
					
						<td>
							<label>Report Type</label></td>
							<td >
								<s:select id="cmbDocType" path="strDocType" cssClass="BoxW124px">
						    		<s:option value="PDF">PDF</s:option>
						    		<s:option value="XLS">EXCEL</s:option>
						    	</s:select>
						</td>	
						<td></td>
						<td></td>	
						<td></td>
						<td></td>			
				</tr>				
				</table>
			</div>		
			<div
				style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 80%;">				
				<table id="tblCallCenterOrderFlash" class="transTablex" style="width: 100%; text-align: center !important;">
				</table>				
			</div>
			<!-- <div style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 50px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 80%;">			
				<table id="tblTotal" class="transTablex" style="width: 100%; text-align: center !important;">
			</table>
			</div> -->
		</div>
		<br />
		<br />
		<p align="center">
			<input type="button" value="Execute" class="form_button" id="btnSubmit" /> 
			<input type="submit" value="Export"  class="form_button" id="submit" /> <input type="reset" value="Reset" 	class="form_button" id="btnReset" />
		</p>
		<div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 50%; left: 50%; padding: 2px;">
			<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif"	width="60px" height="60px" />
		</div>
	</s:form>

</body>
</html>