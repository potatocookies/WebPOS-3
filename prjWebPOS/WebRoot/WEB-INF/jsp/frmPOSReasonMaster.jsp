<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>REASON MASTER</title>
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
var paramTick="unticked";
/*On form Load It Reset form :Ritesh 22 Nov 2014*/
 $(document).ready(function () {
    

		  $('input#txtReasonCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtReasonName').mlKeyboard({layout: 'en_US'});
}); 


 	/**
	* Reset The REASON Name TextField
	**/
	function funResetFields()
	{
	
		
		$("#chkCashManagement").attr('checked', false);
		$("#chkUnsettleBill").attr('checked', false);
		$("#chkComplementary").attr('checked', false);
		$("#chkDiscount").attr('checked', false);
		$("#chkNonChargableKOT").attr('checked', false);
		$("#chkVoidAdvanceOrder").attr('checked', false);
		$("#chkReprint").attr('checked', false); 
		$("#chkStockIn").attr('checked', false);
		
 		$("#chkStockOut").attr('checked', false);
		$("#chkVoidBill").attr('checked', false);
		$("#chkModifyBill").attr('checked', false);
		$("#chkPSP").attr('checked', false);
		$("#chkVoidKOT").attr('checked', false);
		$("#chkVoidStockIn").attr('checked', false);
		$("#chkVoidStockOut").attr('checked', false); 
 } 
	
	
		/**
		* Open Help
		**/
		function funHelp(transactionName)
		{	       
	       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
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
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		* Get and Set data from help file and load data Based on Selection Passing Value(Reason Code)
		**/
		function funSetData(code)
		{
			$("#txtReasonCode").val(code);
			var searchurl=getContextPath()+"/loadPOSReasonMasterData.html?POSReasonCode="+code;
			 $.ajax({                        
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	if(response.strGCode=='Invalid Code')
				        	{
				        		alert("Invalid Reason Code");
				        		$("#txtReasonCode").val('');
				        	}
				        	else
				        	{
					        	$("#txtReasonCode").val(response.strReasonCode);
					        	$("#txtReasonName").val(response.strReasonName);
					        	$("#txtReasonName").focus();
					        	$("#cmbTransferType").val(response.strTransferType);
					        	$("#cmbTransferEntry").val(response.strTransferEntry);
					        	if(response.strStkIn=='Y')
					        	{
					        		$("#chkStockIn").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkStockIn").attr('unchecked', false);
					        	}
					        	if(response.strStkOut=='Y')
					        	{
					        		$("#chkStockOut").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkStockOut").attr('unchecked', false);
					        	}
					        	if(response.strVoidBill=='Y')
					        	{
					        		$("#chkVoidBill").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkVoidBill").attr('unchecked', false);
					        	}
					        	if(response.strModifyBill=='Y')
					        	{
					        		$("#chkModifyBill").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkModifyBill").attr('unchecked', false);
					        	}
					        	if(response.strPSP=='Y')
					        	{
					        		$("#chkPSP").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkPSP").attr('unchecked', false);
					        	}
					        	if(response.strKot=='Y')
					        	{
					        		$("#chkVoidKOT").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkVoidKOT").attr('unchecked', false);
					        	}
					        	if(response.strCashMgmt=='Y')
					        	{
					        		$("#chkCashManagement").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkCashManagement").attr('unchecked', false);
					        	}
					        	if(response.strVoidStkIn=='Y')
					        	{
					        		$("#chkVoidStockIn").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkVoidStockIn").attr('unchecked', false);
					        	}
					        	if(response.strVoidStkOut=='Y')
					        	{
					        		$("#chkVoidStockOut").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkVoidStockOut").attr('unchecked', false);
					        	}
					        	if(response.strUnsettleBill=='Y')
					        	{
					        		$("#chkUnsettleBill").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkUnsettleBill").attr('unchecked', false);
					        	}
					        	if(response.strComplementary=='Y')
					        	{
					        		$("#chkComplementary").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkComplementary").attr('unchecked', false);
					        	}
					        	if(response.strDiscount=='Y')
					        	{
					        		$("#chkDiscount").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkDiscount").attr('unchecked', false);
					        	}
					        	if(response.strNCKOT=='Y')
					        	{
					        		$("#chkNonChargableKOT").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkNonChargableKOT").attr('unchecked', false);
					        	}
					        	if(response.strVoidAdvOrder=='Y')
					        	{
					        		$("#chkVoidAdvanceOrder").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkVoidAdvanceOrder").attr('unchecked', false);
					        	}
					        	if(response.strReprint=='Y')
					        	{
					        		$("#chkReprint").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkReprint").attr('unchecked', false);
					        	}
					        	if(response.strHashTagLoyaltyIntf=='Y')
					        	{
					        		$("#chkHashTagLoyaltyIntf").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkHashTagLoyaltyIntf").attr('unchecked', false);
					        	}
					        	if(response.strMoveKOT=='Y')
					        	{
					        		$("#chkMoveKOT").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkMoveKOT").attr('unchecked', false);
					        	}
					        	if(response.strOperational=='Y')
					        	{
					        		$("#chkOperational").attr('checked', true);
					        	}
					        	else
					        	{
					        		$("#chkOperational").attr('unchecked', false);
					        	}
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
		
		
		$(function()
		{
			
			$('#txtReasonCode').blur(function() 
			{
					var code = $('#txtReasonCode').val();
					if (code.trim().length > 0 && code !="?" && code !="/")
					{				
						funSetData(code);							
					}
			});
			
			$('#txtReasonName').blur(function () {
				 var strReasonName=$('#txtReasonName').val();
			      var st = strReasonName.replace(/\s{2,}/g, ' ');
			      $('#txtReasonName').val(st);
				});
			
		});
	
		
	
	
		
		
		
		/**
		*  Check Validation Before Saving Record
		**/
		
		function funCallFormAction(actionName,object) 
		{
			var flg=true;
			
			if($('#txtReasonCode').val()=='')
			{
				var code = $('#txtReasonName').val();
				 $.ajax({
				        type: "GET",
				        url: getContextPath()+"/checkPOSReasonName.html?reasonName="+code,
				        async: false,
				        dataType: "text",
				        success: function(response)
				        {
				        	if(response=="true")
				        		{
				        			alert("Reason Name Already Exist!");
				        			$('#txtReasonName').focus();
				        			flg= false;
					    		}
					    	else
					    		{
					    			flg=true;
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
			
			
			return flg;
		}
		
		function funSelectAll()
		{	
			
			if(paramTick=="unticked")
			{
				paramTick="ticked";
				funsTickUnTick(true);
			}else{
				paramTick="unticked";
				funsTickUnTick(false);
			}
			
			
		}
		
		function funsTickUnTick(tick){
			$("#chkStockIn").attr('checked', tick);
			$("#chkStockOut").attr('checked', tick);
			$("#chkVoidBill").attr('checked', tick);
			$("#chkModifyBill").attr('checked', tick);
			$("#chkPSP").attr('checked', tick);
			$("#chkVoidKOT").attr('checked', tick);
			$("#chkVoidStockIn").attr('checked', tick);
			$("#chkVoidStockOut").attr('checked', tick);
			$("#chkCashManagement").attr('checked', tick);
			$("#chkUnsettleBill").attr('checked', tick);
			$("#chkComplementary").attr('checked', tick);
			$("#chkDiscount").attr('checked', tick);
			$("#chkNonChargableKOT").attr('checked', tick);
			$("#chkVoidAdvanceOrder").attr('checked', tick);
			$("#chkReprint").attr('checked', tick);
			$("#chkCashManagement").attr('checked', tick);
			$("#chkUnsettleBill").attr('checked', tick);
			$("#chkMoveKOT").attr('checked', tick);
			$("#chkHashTagLoyaltyIntf").attr('checked', tick);
		}
</script>


</head>

<body onload="funOnLoad();">
	<div id="formHeading">
		<label>Reason Master</label>
	</div>
	<s:form name="reasonForm" method="POST" action="savePOSReasonMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
    
		
		<div class="title" style="margin-left: 25%;">
		
			<div class="row" style="background-color: #fff;display: -webkit-box;"">
				<div class="element-input col-lg-6" style="width: 25%;" > 
    				<label class="title">Reason Code</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;" > 
					<s:input type="text" id="txtReasonCode" path="strReasonCode"  ondblclick="funHelp('POSReasonMaster')"/>
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;"">
				<div class="element-input col-lg-6" style="width: 25%;"> 
    				<label class="title">Reason Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;" > 
					<s:input type="text" id="txtReasonName" path="strReasonName" />
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;">
			 	<div class="element-input col-lg-6" style="width: 25%;"> 
			 	</div>
				<div class="element-input col-lg-6" > 
    				<label class="title" id="selectAll" onclick="funSelectAll()" style="color:#59bced;">Click here for Select All</label>
    			</div>
    			
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;">
				<div class="element-input col-lg-6" style="width: 25%;" > 
    				<label class="title">Transaction</label>
    			</div>
    			<div class="element-input col-lg-6" style="width: 100%;" >
    				 <s:input type="checkbox"  id="chkStockIn"  path="strStkIn" style="width: 3%"></s:input>Stock In
    				 <s:input type="checkbox"  id="chkStockOut" path="strStkOut" style="width: 3%" ></s:input>Stock Out
				     <s:input type="checkbox"  id="chkModifyBill" path="strModifyBill" style="width: 3%" ></s:input>Modify Bill
				     <s:input type="checkbox"  id="chkCashManagement" path="strCashMgmt"  style="width: 3%"></s:input>Cash Management
				     <s:input type="checkbox"  id="chkNonChargableKOT" path="strNCKOT" style="width: 3%"  ></s:input>Non Chargable KOT
				    
			 	</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
				<div class="element-input col-lg-6" style="width: 25%;"> 
    				 <label class="title"></label>
    			</div>
    			<div class="element-input col-lg-6" style="width: 100%;">
			  		 <s:input type="checkbox"  id="chkVoidKOT" path="strKot"  style="width: 3%" ></s:input>Void KOT
				     <s:input type="checkbox"  id="chkVoidStockIn" path="strVoidStkIn" style="width: 3%"  ></s:input>Void Stock In
				     <s:input type="checkbox"  id="chkVoidStockOut" path="strVoidStkOut" style="width: 3%"  ></s:input>Void Stock Out
			 	     <s:input type="checkbox"  id="chkVoidBill" path="strVoidBill" style="width: 3%"  ></s:input>Void Bill
				     <s:input type="checkbox"  id="chkUnsettleBill" path="strUnsettleBill" style="width: 3%" ></s:input>Unsettle Bill
			 	</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
				<div class="element-input col-lg-6" style="width: 25%;"> 
    				<label class="title"></label>
    			</div>
    			<div class="element-input col-lg-6" style="width: 100%;">
				     <s:input type="checkbox"  id="chkComplementary" path="strComplementary" style="width: 3%" ></s:input>Complementary
				     <s:input type="checkbox"  id="chkDiscount" path="strDiscount" style="width: 3%"  ></s:input>Discount
				     <s:input type="checkbox"  id="chkPSP" path="strPSP" style="width: 3%" ></s:input>PSP
				     <s:input type="checkbox"  id="chkVoidAdvanceOrder" path="strVoidAdvOrder" style="width: 3%" ></s:input>Void Advance Order
				     <s:input type="checkbox"  id="chkReprint" path="strReprint" style="width: 3%"  ></s:input>Reprint	
			 	</div>
			 </div>
			 
			<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
				<div class="element-input col-lg-6" style="width: 25%;"> 
    				<label class="title"></label>
    			</div>
    			<div class="element-input col-lg-6" style="width: 100%;">
				     <s:input type="checkbox"  id="chkMoveKOT" path="strMoveKOT" style="width: 3%" ></s:input>Move KOT
				     <s:input type="checkbox"  id="chkHashTagLoyaltyIntf" path="strHashTagLoyaltyIntf" style="width: 3%"  ></s:input>Hash Tag Loyalty Interface
				   
			 	</div>
			 </div>
			
			  <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
			 	<div class="element-input col-lg-6" style="width: 25%;">
			 		<label class="title">Transfer Type</label>
			 	</div>
				<div class="element-input col-lg-6" style="width: 30%;">
					<s:select id="cmbTransferType" path="strTransferType" >
							<option selected="selected" value="Purchase">Purchase</option>
						   	<option value="Purchase Return">Purchase Return</option>
						   	<option value="Other">Other</option>
					</s:select>
				</div>	
			</div>
			
			 <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
			 	<div class="element-input col-lg-6" style="width: 25%;">
			 		<label class="title">Transfer Entry</label>
			 	</div>
				<div class="element-input col-lg-6" style="width: 30%;">
					<s:select id="cmbTransferEntry" path="strTransferEntry" >
							<option value="Other">Other</option>
					</s:select>
				</div>	
			</div>
			
			 <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
				<div class="element-input col-lg-6" style="width: 25%;"> 
    				<label class="title">Operational</label>
    			</div>
    			<div class="element-input col-lg-6" >
		    				<s:input type="checkbox"  id="chkOperational" path="strOperational"></s:input>
		    	</div>
    		</div>	
			
			<br/>
			
			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
     			<p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="SUBMIT"/></div>
          
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" onclick="funResetFields()"></div>
     			</p>
   			</div>
			 
		</div>
		

	</s:form>

</body>
</html>