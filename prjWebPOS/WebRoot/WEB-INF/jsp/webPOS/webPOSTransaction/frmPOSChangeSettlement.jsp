<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Customer On Bill</title>
<script type="text/javascript">
 	var fieldName,_paidAmount=0,_grandTotal=0,settleCode="";
 	var myMap = new Map(); 
 	
	$(document).ready(function()
	{
        funShowDiv("");
	});
	
	
	$(function() 
	{
		$("#txtBillDate").datepicker({ dateFormat: 'yy-mm-dd' });
		$("#txtBillDate" ).datepicker('setDate', 'today');
	});
			
			
	function funHelp(transactionName)
	{	       
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	}
			
			
 	function funSetData(code)
 	{

 		switch(fieldName)
 		{
		
 		case "POSCustomerMaster":
 			funSetCustomerDataForHD(code);
 			 
 			break;
 		case "BillForChangeSettlement":
 			$("#txtBillNo").val(code);
 			funGetBillDate(code);
 			break;	
 			
 		
 		}
 	}
 	
 	 function funSetCustomerDataForHD(code)
		{
		 code=code.trim();
			var searchurl=getContextPath()+"/loadPOSCustomerMasterData.html?POSCustomerCode="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	
				        	 $("#txtCustomerName").val(response.strCustomerName);
		          			 $("#lblCustCode").val(code);
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
		});
    
    
		
	//Delete a All record from a grid
		function funRemoveHeaderTableRows(tableName)
		{
			var table = document.getElementById(tableName);
			var rowCount = table.rows.length;
			while(rowCount>0)
			{
				table.deleteRow(0);
				rowCount--;
			}
		}
	
		function funGetBillDate(billNo)
		{
		   var searchurl=getContextPath()+"/LoadBillDate.html?BillNo="+billNo ;
			$.ajax({
				type : "GET",
				url : searchurl,
				dataType : "json",
				async:true,
				success : function(response)
				{ 
					$("#txtBillDate").val(response.strBillDate);
					funSetBillDetails();
				},
				error : function(e){
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
			    
		function funSetBillDetails()
		{
		  var billDate=$("#txtBillDate").val();	
		  var billNo=$("#txtBillNo").val();	
		  var searchurl=getContextPath()+"/LoadBillDataForChangeSettlement.html?BillNo="+billNo+ "&BillDate=" + billDate ;
			$.ajax({
				type : "GET",
				url : searchurl,
				dataType : "json",
				success : function(response){ 
					funRemoveHeaderTableRows("tblBillData");
					var table=document.getElementById("tblBillData");
					var rowCount = table.rows.length;
					var settleDesc="",settleType;
					$.each(response.listOfBillSettleMode, function(i,item)
					{
						var row = table.insertRow(rowCount);
						row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+item.strSettlementCode+"\" value='"+item.strSettlementName+"' >";
						row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\"style=\"text-align: right\"   id=\""+item.dblSettlementAmt+"\" value='"+item.dblSettlementAmt+"' >";
						rowCount++;
						settleDesc=item.strSettlementName;
						settleType=item.strSettlementType;
						$("#txtSettleCode").val(item.strSettlementCode);
					});	
					if(response.listOfBillSettleMode.length>1)
					{
						funOnClickSettleName("Cash","Cash");
						$("#txtSettleCode").val("S01");
						funSetValues("Cash",response.dblBillAmount);
						$("#txtBillAmount").val(response.dblBillAmount);
						_grandTotal=response.dblBillAmount;
						funRemoveHeaderTableRows("tblPaymentModeData");
						myMap.set("Balance",response.dblBillAmount+"#Balance"+"#Balance"); 
						funSetSettlementTableData();
					}
					else
					{
						
						funOnClickSettleName(settleDesc,settleType);
						funSetValues(settleType,response.dblBillAmount);
						$("#txtBillAmount").val(response.dblBillAmount);
						_grandTotal=response.dblBillAmount;
						funRemoveHeaderTableRows("tblPaymentModeData");
						myMap.set("Balance",response.dblBillAmount+"#Balance"+"#Balance"); 
						funSetSettlementTableData();
					}
					//document.getElementById().focus();
					
					$("#txtRemark").val("");
					$("#txtSlipNo").val("");
					$("#txtPaidAmt").focus();
					
				},
				error : function(e){
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
			    
			    
			    
			    
		function funOnClickSettleName(obj,settleType)
		{
			if ($("#txtBillNo").val()=="")
	        {
				alert("Please Enter Bill No");
		       	return false;
	        }
			$('#lblPaymentMode').text("Payment Mode  "+obj.id);
			$('#txtPaymentMode').val("Payment Mode  "+obj.id);
			
			if(settleType=='')
			{
				<c:forEach items="${settlementList}" var="settlementList1">
				    var setteleDesc="${settlementList1.value.split('#')[0]}";
				     if(obj.id==setteleDesc)
					   {
				    	 settleType="${settlementList1.value.split('#')[1]}";
				    	 var settleCode="${settlementList1.key}";
				    	 $("#txtSettleCode").val(settleCode);
						    if(settleType=="Cash")
							{
						    	funShowDiv("divCashSettle")
							}
						    else if(settleType=="Credit Card")
							{
						    	funShowDiv("divCashSettle")
						    	document.getElementById("txtExpiryDte").style.visibility="visible";
								document.getElementById("txtSlipNo").style.visibility="visible";
								document.getElementById("divExpiryDt").style.visibility="visible";
								document.getElementById("divSlipNo").style.visibility="visible";
							}
						    else if(settleType=="Credit")
							{
						    	funHelp('POSCustomerMaster');
								funShowDiv("divCreditSettle")
								document.getElementById("divCustName").style.visibility="visible";
								document.getElementById("txtCustomerName").style.visibility="visible";
								document.getElementById("divReason").style.visibility="hidden";
								document.getElementById("divReasonName").style.visibility="hidden";
							}
							else 
							{
								funShowDiv("divCreditSettle")
								document.getElementById("divReason").style.visibility="visible";
								document.getElementById("divReasonName").style.visibility="visible";
						    }  
					   }
	            </c:forEach>
			}
			else
			{
				$('#lblPaymentMode').text("Payment Mode  "+obj);
				$('#txtPaymentMode').val("Payment Mode  "+obj);
				if(settleType=="Cash")
				{
					funShowDiv("divCashSettle")
				}
			    else if(settleType=="Credit Card")
				{
			    	funShowDiv("divCashSettle")
			    	document.getElementById("txtExpiryDte").style.visibility="visible";
					document.getElementById("txtSlipNo").style.visibility="visible";
					document.getElementById("divExpiryDt").style.visibility="visible";
					document.getElementById("divSlipNo").style.visibility="visible";
				}
			    else if(settleType=="Credit")
				{
					funShowDiv("divCreditSettle")
					document.getElementById("divCustName").style.visibility="visible";
					document.getElementById("txtCustomerName").style.visibility="visible";
					document.getElementById("divReason").style.visibility="hidden";
					document.getElementById("divReasonName").style.visibility="hidden";
					
				}
				else 
				{
					funShowDiv("divCreditSettle")
					document.getElementById("divReason").style.visibility="visible";
					document.getElementById("divReasonName").style.visibility="visible";
			    }
			}
			
			funSetValues(settleType,$("#txtPaidAmt").val());
			 
		}	
		
		
		function funShowDiv(divID) {
			document.all["divEnterButton"].style.display = 'none';
			document.all["divCashSettle"].style.display = 'none';
			document.all["divCreditSettle"].style.display = 'none';
			document.all[divID].style.display = 'block';
			if(divID!="")
			{
			 document.all["divEnterButton"].style.display = 'block';
			}
			document.getElementById("txtExpiryDte").style.visibility="hidden";
			document.getElementById("txtSlipNo").style.visibility="hidden";
			document.getElementById("divExpiryDt").style.visibility="hidden";
			document.getElementById("divSlipNo").style.visibility="hidden";
			document.getElementById("divCustName").style.visibility="hidden";
			document.getElementById("txtCustomerName").style.visibility="hidden";
			
		}
		
		function funSetValues(settleType,billAmt)
		{
			$("#txtSettleType").val(settleType);
			if(settleType=="Cash")
			{
				$("#txtBillAmt").val(billAmt);
		    	$("#txtPaidAmt").val(billAmt);
			}
			else if(settleType=="Credit Card")
			{
				$("#txtBillAmt").val(billAmt);
		    	$("#txtPaidAmt").val(billAmt);
			}
			else if(settleType=="Credit")
			{
				$("#txtBillAmt").val(billAmt);
		    	$("#txtPaidAmt").val(billAmt);
			}
			else
			{
				$("#txtBillAmt").val(billAmt);
		    	$("#txtPaidAmt").val(billAmt);
			}	
			$("#txtTotalSettleAmount").val(billAmt);
			_grandTotal=billAmt;
			
		}
		
		function funEnterButtonPressed()
		{
			var settleType=$("#txtSettleType").val();
			if ($("#txtPaidAmt").val() == 0)
	        {
	            _paidAmount = 0.00;
	        }
	        else
	        {
	            _paidAmount = $("#txtPaidAmt").val();
	        }
			if (_paidAmount == 0.00 )
	        {
				alert("Balance amount is 0");
				$("#txtCustomerName").val("");
     			$("#lblCustCode").val("");
		       	return false;
	        }
			$("#txtSettleRemark").val($("#txtRemark").val());
			if(settleType=="Credit Card")
			{
				if ($("#txtSlipNo").val()=="")
		        {
					alert("Please Enter Slip No");
			       	return false;
		        }
				$("#txtSettleRemark").val($("#txtRemark").val());
			}
			else if(settleType=="Credit")
	        {
				if ($("#txtCustomerName").val()=="")
		        {
					alert("Please Select Customer");
			       	return false;
		        }
				$("#txtSettleRemark").val($("#txtCreditRemark").val());
	        }
			else if(settleType=="Complementary")
	        {
				if(myMap.size>1)
				{
					alert("Multisettle not allowed for complimentory");
			       	return false;
				}
				if ($("#txtCreditRemark").val()=="")
		        {
					alert("Please Enter Remarks");
			       	return false;
		        }
				if ($("#txtReasonCode").val()=="")
		        {
					alert("Please Select Reason");
			       	return false;
		        }
				$("#txtSettleRemark").val($("#txtCreditRemark").val());
	        }
			if(myMap.size>0)
			{
				var settleCode=$("#txtSettleCode").val();
				 var settleDesc=$("#txtPaymentMode").val().split('Payment Mode')[1].trim();
				 if(myMap.has(settleDesc))
	  	    	 {
					 
	   			    var value=parseFloat(myMap.get(settleDesc).split("#")[0]);
	   			    myMap.set(settleDesc,parseFloat(_paidAmount)+value+"#"+myMap.get(settleDesc).split("#")[1]+"#"+myMap.get(settleDesc).split("#")[2]);
	  	    	 }
				 else
				 {
					 myMap.set(settleDesc,_paidAmount+"#"+settleType+"#"+settleCode); 
				 }
				 var balAmt=myMap.get("Balance").split("#")[0];
				 balAmt=balAmt-_paidAmount;
				 myMap.delete("Balance");
				 myMap.set("Balance",balAmt+"#Balance"+"#Balance");
				 funSetSettlementTableData();
				 $("#txtBillAmt").val(balAmt);
			     $("#txtPaidAmt").val(balAmt);
			     $("#txtPaidAmt").focus();
			     $("#txtTotalSettleAmount").val(balAmt);
			    _grandTotal=balAmt;
			 }
			
		}
		
		function funSetSettlementTableData()
		{
			funRemoveHeaderTableRows("tblPaymentModeData");
			for(var [key,value] of  myMap )
			 {	
				var table=document.getElementById("tblPaymentModeData");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				var data=value.split("#");
				//row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+key+"\" value='"+key+"' >";
				//row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\"style=\"text-align: right\"   id=\""+value+"\" value='"+value+"' >";
				row.insertCell(0).innerHTML= "<input name=\"listOfBillSettleMode["+(rowCount)+"].strSettlementName\" readonly=\"readonly\" class=\"Box\" size=\"15%\" id=\"strSettlementName."+(rowCount)+"\" value='"+key+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    		    row.insertCell(1).innerHTML= "<input name=\"listOfBillSettleMode["+(rowCount)+"].dblSettlementAmt\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"15%\" id=\"dblSettlementAmt."+(rowCount)+"\" value='"+data[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";  
    		    row.insertCell(2).innerHTML= "<input name=\"listOfBillSettleMode["+(rowCount)+"].strSettlementType\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"15%\" id=\"strSettlementType."+(rowCount)+"\" value='"+data[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    		    row.insertCell(3).innerHTML= "<input name=\"listOfBillSettleMode["+(rowCount)+"].strSettlementCode\" readonly=\"readonly\" class=\"Box\" style=\"text-align: right;\" size=\"15%\" id=\"strSettlementCode."+(rowCount)+"\" value='"+data[2]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
    		    
    		    rowCount++;
			};
		}
		
		
		function funValidateFields()
		{
			var table=document.getElementById("tblPaymentModeData");
			var rowCount = table.rows.length;
			if(rowCount>1)
			{
				if ($("#txtPaidAmt").val() == 0)
		        {
		           return true;
		        }
				else
				{
					alert("Balance is not zero");
			       	return false;
				}	
			}
			else
			{
				alert("Please select settlement mode to settle Bill");
		       	return false;
			}	
		}
			    
 	</script>
<body>
       
     <div id="formHeading" >
		<label>Change Settlement</label>
			</div>

	<s:form name="Change Settlement" method="POST" action="saveChangeSettlement.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:70%;min-width:150px;margin-top:2%;">
	   
	   <div class="title"  >
	   
	   		
			<div style=" width: 40%; height: 570px;float:left;  overflow-x: scroll; border-collapse: separate; border: 3px solid #ccc; overflow-y: auto;">
				
				<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 25%; margin-top:1%;"> 
		    				<label class="title" >Bill Date.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 26%; margin-top:1%;">
		    				<s:input type="text" id="txtBillDate" path="strBillDate" style="width: 100px; height: 25px;" required="required"  cssClass="calenderTextBox" />
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%; margin-top:1%;"> 
		    				<label class="title" >Bill No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%; margin-top:1%;">
		    			   <s:input type="text" id="txtBillNo" path="strBillNo" style="width: 100px; height: 25px;" ondblclick="funHelp('BillForChangeSettlement')" required="true" readonly="true"/>
		    			</div>
		    			
		    			
		    	</div>
				<div>
					<table style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<thead>
								<tr >
									<th style="border: 1px  black solid;width:50%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Settlement Mode</label></th>
									<th style="border: 1px  black solid;width:50%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Settlement Amt</label></th>
									
								</tr>
						</thead>
						</table>
						
						<table id="tblBillData" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<tbody>    
								<col style="width:50%;"><!--  COl1   -->
								<col style="width:50%"><!--  COl2   -->								
						</tbody>			
					</table>
				</div>
				
				<div style="margin-top: 30%">	
						<hr>
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 60%;"> 
			    				<label class="title" >Bill Amount</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="txtBillAmount" readonly="true" style="text-transform: uppercase; width:100px; height:25px; text-align: right" />
			    			</div>
			    		</div>
				</div>
				
				
				<div>
					<table style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<thead>
								<tr >
									<th style="border: 1px  black solid;width:50%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Payment Modes</label></th>
    							    <th style="border: 1px  black solid;width:50%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label></label></th>			   									
								</tr>
						</thead>
						</table>
						
						<table id="tblPaymentModeData" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<tbody>    
								<col style="width:50%;"><!--  COl1   -->
								<col style="width:50%"><!--  COl2   -->		
								<col style="width:1%"><!--  COl2   -->		
								<col style="width:1%"><!--  COl2   -->		
															
						</tbody>			
					</table>
				</div>
				
				<div style="margin-top: 20%;">	
						<hr>
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 60%;"> 
			    				<label class="title" >Total</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="txtTotalSettleAmount" readonly="true" style="text-transform: uppercase; width:100px; height:25px; text-align: right" />
			    			</div>
			    		</div>
				</div>
			
			</div>
			
   		 		 	
			<div style=" width: 60%; height: 570px;float:left;  overflow-x: scroll; border: 3px solid #ccc;  overflow-y: auto;">
					
				<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box;">
						<c:forEach items="${settlementList}" var="settlementList1">
				              <input type="button" id="${settlementList1.value.split('#')[0]}" value="${settlementList1.value.split('#')[0]}" style="margin-bottom:5px" onclick="funOnClickSettleName(this,'')"/>	
					    </c:forEach>
		    	</div>
		    			
		    	<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box;">
						<label class="title" id="lblPaymentMode" ></label>
		    	</div>
   		 		<div id="divCashSettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:190px;">
					    <div class="element-input col-lg-6" style="width: 38%;margin-top:2%"> 
		    				<label class="title" >Bill Amt.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBillAmt" path="dblBillAmount"  readonly="true" style="width:80%; height: 25px;  text-align: right"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:38%;margin-top:2%;"> 
		    				<label class="title" >Paid Amt.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtPaidAmt" path="dblPaidAmt" style="width:80%; height: 25px;  text-align: right"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:38%;margin-top:2%;"> 
		    				<label class="title" >Balance.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBalanceAmt" path="dblBalAmount" style="width:80%; height: 25px;  text-align: right"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:38%;"> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
		    			
		    			<div id="divSlipNo" class="element-input col-lg-6" style="width:38%;margin-top:2%;"> 
		    				<label class="title" >Slip No.</label>
		    			</div>
		    			<div  class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtSlipNo" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div id="divExpiryDt"  class="element-input col-lg-6" style="width:38%;margin-top:2%;"> 
		    				<label class="title" >Expiry Date.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtExpiryDte" path="" style="width:80%; height: 25px;"/>
		    			</div>
				</div>	
				
					
				
				<div id="divCreditSettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:140px;">
					    
					    <div class="element-input col-lg-6" style="width:38%; margin-top:2%; "> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtCreditRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
		    			
		    			<div id="divCustName" class="element-input col-lg-6" style="width:38%; margin-top:2%;"> 
		    				<label class="title" >Cust Name.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtCustomerName" path="" style="width:140%; height: 25px;"/>
		    			</div>
		    			<div id="divReason" class="element-input col-lg-6" style="width:38%; margin-top:2%;"> 
		    				<label class="title" >Reason.</label>
		    			</div>
		    			<div id="divReasonName"class="element-input col-lg-6" style="margin-top:2%;">
		    				<s:select id="txtReasonCode" path="strReasonCode" items="${ReasonNameList}" required="true"/>
		    			</div>
		    			<div id="divCustCode" class="element-input col-lg-6" style="width:38%; margin-top:2%;"> 
		    				<s:input type="hidden" id="lblCustCode" path="strCustomerCode" style="width:140%; height: 25px;"/>
		    			</div>
		    			
		    			
		    			
				</div>	
				
				
				<div id="divEnterButton" class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="col-lg-10 col-sm-10 col-xs-10">
				     		  <p align="center">
				            		<div class="submit col-lg-4 col-sm-4 col-xs-4">
				            			<input id="btnEnter" type="button" value="Enter" onclick="funEnterButtonPressed();"></input>
				            		</div>
				     		  </p>
	   		 			</div>
   		 		 	</div>
   		 			
			</div>
			<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						
				<p align="center">
				<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box; ">
		     		     <input id="btnSave" type="submit" value="Save"  onclick="return funValidateFields();"></input>
		                 <input id="btnReset" type="reset" value="Reset"></input>
		                 <input id="btnClose" type="button" value="Close" onclick="funPOSHome();"></input>
		            
  		 			 </div>
		     	 </p>
		     	 
		     	 
   		   </div>
   		   <s:input type="hidden" id="txtPaymentMode" path="" />
   		   <s:input type="hidden" id="txtSettleType" path="" />
   		   <s:input type="hidden" id="txtSettleCode" path="" />
   		   <s:input type="hidden" id="txtSettleRemark" path="strRemark" />
			
	</div>

	</s:form> 
    
<br /><br />       
 
</body>
</html>