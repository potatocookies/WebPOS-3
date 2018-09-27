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
 			
	function funHelp(transactionName)
	{	       
		fieldName=transactionName;
		if(transactionName=="BillForCreditBillReceipt")
		 {
			if ($("#txtCustomerName").val()=="")
	        {
				alert("Please Select Customer");
		       	return false;
	        }
			else
			{	
				var custCode=$('#txtCustomerCode').val();
			    window.open("searchform.html?formname="+transactionName+"&searchText=&strCustomerCode="+custCode+"","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")	
			}	
		}
		else
		 {
			window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
		 }	
    }
			
			
 	function funSetData(code)
 	{

 		switch(fieldName)
 		{
		    case "POSCustomerMaster":
	 			funSetCustomerDataForHD(code);
	 			break;
	 		case "BillForCreditBillReceipt":
	 			$("#txtBillNo").val(code);
	 			funSetBillDetails();
	 			break;	
 		}
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
				        	 $("#txtCustomerCode").val(code);
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
		
		
		function funSetBillDetails()
		{
		  var customerCode=$("#txtCustomerCode").val();	
		  var billNo=$("#txtBillNo").val();	
		  var searchurl=getContextPath()+"/getCreditBillReceiptData.html?BillNo="+billNo+ "&CustomerCode=" + customerCode ;
			$.ajax({
				type : "GET",
				url : searchurl,
				dataType : "json",
				success : function(response){ 
					$.each(response, function(i,item)
					{
						$("#txtBillDate").val(item.dteBillDate);
						$("#txtCreditAmt").val(item.dblCreditAmount);
						$("#txtPaidAmt").val(item.dblPaidAmount);
						var balanceAmt=item.dblCreditAmount-item.dblPaidAmount;
						$("#txtBalanceAmt").val(balanceAmt);
						$("#txtReceiptAmt").val(balanceAmt);
						$("#txtRefundAmt").val(0);
						$('#lblPaymentMode').text("Payment Mode  Cash");
						$('#txtSettleName').val("Cash");
						$('#txtSettleCode').val("S01");
						
						
					});	
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
			$('#lblPaymentMode').text("Payment Mode  "+obj.id);
			$('#txtSettleName').val(obj.id);
			
			if(settleType=='')
			{
				<c:forEach items="${settlementList}" var="settlementList1">
				    var setteleDesc="${settlementList1.value.split('#')[0]}";
				     if(obj.id==setteleDesc)
					   {
				    	 settleType="${settlementList1.value.split('#')[1]}";
				    	 var settleCode="${settlementList1.key}";
				    	 $('#txtSettleCode').val(settleCode);
						    if(settleType=="Cash")
							{
						    	
							}
						    else if(settleType=="Credit Card")
							{
						    	
							}
						    else if(settleType=="Credit")
							{
						    	
							}
							else 
							{
								
						    }  
					   }
	            </c:forEach>
			}
			 
		}	
		
		
		function funEnterButtonPressed()
		{
			var creditAmt = parseFloat($("#txtCreditAmt").val());
			var paidAmt = parseFloat($("#txtPaidAmt").val());
			var balanceAmt = parseFloat($("#txtBalanceAmt").val());
			var receiptAmt = parseFloat($("#txtReceiptAmt").val());
			
			if (creditAmt == paidAmt || balanceAmt <= 0)
	        {
	            alert("Full Payment Received");
	            return false;
	        }
			
			if (receiptAmt > balanceAmt)
	        {
				var refundAmt = receiptAmt - balanceAmt;
				$("#txtRefundAmt").val(refundAmt);
				$("#txtReceiptAmt").val(balanceAmt);
	            receiptAmt = parseFloat($("#txtReceiptAmt").val());
	        }
		}
		
		
		function funValidateFields()
		{
			var creditAmt = parseFloat($("#txtCreditAmt").val());
			var paidAmt = parseFloat($("#txtPaidAmt").val());
			var balanceAmt = parseFloat($("#txtBalanceAmt").val());
			var receiptAmt = parseFloat($("#txtReceiptAmt").val());
			
			if (creditAmt == paidAmt || balanceAmt <= 0)
	        {
	            alert("Full Payment Received");
	            return false;
	        }
			
			if (receiptAmt > balanceAmt)
	        {
				var refundAmt = receiptAmt - balanceAmt;
				$("#txtRefundAmt").val(refundAmt);
				$("#txtReceiptAmt").val(balanceAmt);
	            receiptAmt = parseFloat($("#txtReceiptAmt").val());
	        }
		}
    	    
		
			    
 	</script>
<body>
       
     <div id="formHeading" >
		<label>Credit Receipt</label>
			</div>

	<s:form name="Credit Receipt" method="POST" action="saveCreditReceipt.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:70%;min-width:150px;margin-top:2%;">
	   
	   <div class="title"  >
	   
	   		
			<div style=" width: 80%; height: 570px;float:left;  overflow-x: scroll; border-collapse: separate; border: 3px solid #ccc; overflow-y: auto;">
				
				<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 100%; text-align:center;font-size:20px;"> 
		    				<label class="title" >Credit Bill Receipt</label>
		    			</div>
		    	</div>
				
				
				<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Receipt No</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 40%;">
		    			   <s:input type="text" id="txtReceiptNo" path="" style="width: 100%; height: 25px;"  required="true" readonly="true"/>
		    			</div>	
		    	</div>
		    	
		    	<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >customer</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 50%;">
		    			   <s:input type="text" id="txtCustomerName" path="strCustomerName" style="width: 100%; height: 25px;" ondblclick="funHelp('POSCustomerMaster')" required="true" readonly="true"/>
		    			</div>
		    				
		    	</div>
		    	
		    	<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Bill No</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    			   <s:input type="text" id="txtBillNo" path="strBillNo" style="width: 100%; height: 25px;" ondblclick="funHelp('BillForCreditBillReceipt')" required="true" readonly="true"/>
		    			</div>	
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Bill Date</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 25%;">
		    			   <s:input type="text" id="txtBillDate" path="dteBillDate" style="width: 100%; height: 25px;" readonly="true"/>
		    			</div>	
		    	</div>
		    	
		    	<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Credit Amt</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    			   <s:input type="text" id="txtCreditAmt" path="dblCreditAmount" style="width: 100%; height: 25px; text-align: right;" ondblclick="funHelp('BillForChangeSettlement')" required="true" readonly="true"/>
		    			</div>	
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Paid Amt</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    			   <s:input type="text" id="txtPaidAmt" path="dblPaidAmount" style="width: 100%; height: 25px; text-align: right;" ondblclick="funHelp('BillForChangeSettlement')" required="true" readonly="true"/>
		    			</div>
		    			
		    			<div class="element-input col-lg-6" style="width: 14%;"> 
		    				<label class="title" >Balance Amt</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 18%;">
		    			   <s:input type="text" id="txtBalanceAmt" path="dblBalanceAmount" style="width: 100%; height: 25px; text-align: right;" ondblclick="funHelp('BillForChangeSettlement')" required="true" readonly="true"/>
		    			</div>	
		    	</div>
		    	<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Payment Mode</label>
		    			</div>	
		    	</div><div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 100%;"> 
		    				<c:forEach items="${settlementList}" var="settlementList1">
				              <input type="button" id="${settlementList1.value.split('#')[0]}" value="${settlementList1.value.split('#')[0]}" style="margin-bottom:5px" onclick="funOnClickSettleName(this,'')"/>	
					        </c:forEach>
		    			</div>	
		    	</div>
		    	
		    	<div class="row" style="background-color: #fff;margin-top:5%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 35%;"> 
		    				<label class="title" id="lblPaymentMode" >Payment Mode</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Remark</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 40%;">
		    			   <s:input type="text" id="txtRemark" path="strRemark" style="width: 100%; height: 25px;" />
		    			</div>	
		    	</div>
		    	
		    	<div class="row" style="background-color: #fff;margin-top:5%;margin-bottom:2%;display: -webkit-box;">
						
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Receipt Amt</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    			   <s:input type="text" id="txtReceiptAmt" path="dblReceiptAmount" style="width: 100%; height: 25px; text-align: right;" />
		    			</div>	
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Refund Amt</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    			   <s:input type="text" id="txtRefundAmt" path="" style="width: 100%; height: 25px; text-align: right;" />
		    			</div>
		    			
		    		    <div class="submit col-lg-4 col-sm-4 col-xs-4">
				           <input id="btnEnter" type="button" value="Enter" onclick="funEnterButtonPressed();"></input>
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
   		   <s:input type="hidden" id="txtCustomerCode" path="strCustomerCode" />
   		   <s:input type="hidden" id="txtSettleCode" path="strSettleCode" />
   		   <s:input type="hidden" id="txtSettleName" path="strSettleName" />
			
	</div>

	</s:form> 
    
<br /><br />       
 
</body>
</html>