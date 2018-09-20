<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Customer On Bill</title>
<script type="text/javascript">
 	var fieldName,textValue2="",selectedRowIndex=0,delTableNo="",delKotNo="",delItemcode="", delAmount="",delQuatity="",cnt=0;
 	var gCMSIntegrationYN="${gCMSIntegrationYN}";
 	
	$(document).ready(function()
			{
		        funShowDiv("");
		        $('#lblCustCodeValue').val("");
				$('#lblCustNameValue').val("");
				$("#lblBillNo").val("");
				funFillGrid();
				
				 $("form").submit(function(event){
			    	 var billNo=$("#lblBillNo").val(); 
			    	 if(billNo=='')
                 	{
                 		alert("Please Select Bill"); 
                 		return false;
                 	}
			    	 else  if($("#lblCustCodeValue").val().trim()=="")
						{
							alert("Please Select Customer");
							return false;
						}
					  	
					  	
					});
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
						        	
						        	 $('#lblCustCode').val("Customer Code");
				                     $('#lblCustCodeValue').val(code);
				                     $('#lblCustName').val("Customer Name");
				                     $('#lblCustNameValue').val(response.strCustomerName);
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

			 
			 function funCustomerMaster(strMobNo)
				{
					 fieldName="NewCustomer";
					 <%session.setAttribute("frmName", "frmPOSChangeCustomerOnBill");%>

					
					window.open("frmPOSCustomerMaster.html?intlongMobileNo="+strMobNo,"","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
				}
			 
				function funFillGrid()
				{
				    var searchUrl="";
				    
					searchUrl=getContextPath()+"/loadBillForChangeCustomer.html?";
					$.ajax({
					        type: "GET",
					        url: searchUrl,
					        async:false,
					        dataType: "json",
						    success: function(response)
						    {
								
						    	$.each(response, function(i,item)
								{
						    		funAddFullRow(response);
								});
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
				
				
				function funRemoveTableRows(tableId)
				{	
					var table = document.getElementById(tableId);
					var rowCount = table.rows.length;
					while(rowCount>0)
					{
						table.deleteRow(0);
						rowCount--;
					}
				}
				
			     
			     
			     function funAddItemTableData(itemDataList,taxAmt,subTotal,grandTotal,gShowItemDetailsGrid,userCreated,customerCode,customerName){
			    		$('#tblData tbody').empty()

			    		var table = document.getElementById("tblData");
			    		var rowCount = table.rows.length;

			    		var row = table.insertRow(rowCount);
			    		  	for(var i=0;i<itemDataList.length;i++){
			    	    	  row = table.insertRow(rowCount);
			    	    	  var rowItemData=itemDataList[i];
			    	    	
			    	    		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+rowItemData[0]+"\" value='"+rowItemData[0]+"' / >";
			    	    		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+rowItemData[1]+"\" value='"+rowItemData[1]+"'/>";
			    	    		rowCount++;
			    	    	}
			    	
			     	    if(gShowItemDetailsGrid=='N')
			     	    {
			     	    	$('#tblData tbody').empty();
			     		}else{
			    	    	
			    	    	$("#lblSubTotlal").val(subTotal);
			    	    	$("#lblTotal").val(grandTotal);
			    	    	$("#lblCustCodeValue").val(customerCode);
			    	    	$("#lblCustNameValue").val(customerName);
			    	    	$("#lblTotal").val(parseFloat(subTotal)+parseFloat(taxAmt));
			    	    	
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
			    
					function funOnClickSettleName(obj)
					{
						$('#lblPaymentMode').text("Payment Mode  "+obj.id);
						<c:forEach items="${settlementList}" var="settlementList1">
						  if(obj.id=="${settlementList1.value.split("#")[0]}")
						   {
							    alert("${settlementList1.value.split("#")[1]}");
							    if("${settlementList1.value.split("#")[1]}"=="Cash")
								{
									funShowDiv("divCashSettle")
								}
							    else if("${settlementList1.value.split("#")[1]}"=="Credit Card")
								{
									funShowDiv("divCreditCardSettle")
								}
							    else if("${settlementList1.value.split("#")[1]}"=="Credit")
								{
									funShowDiv("divCreditSettle")
								}
								else 
								{
									funShowDiv("divComplimentrySettle")
							    }
							
						   }
			            </c:forEach>
						 
					}	
					
					
					function funShowDiv(divID) {
						document.all["divCashSettle"].style.display = 'none';
						document.all["divCreditSettle"].style.display = 'none';
						document.all["divComplimentrySettle"].style.display = 'none';
						document.all["divCreditCardSettle"].style.display = 'none';
						document.all[divID].style.display = 'block';
					}
			    
 	</script>
<body>
       
     <div id="formHeading" >
		<label>Change Customer On Bill</label>
			</div>

	<s:form name="Customer Change On Bill" method="POST" action="saveChangeCustomerOnBill.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:65%;min-width:150px;margin-top:2%;">
	   
	   <div class="title"  >
	   
	   		
			<div style=" width: 40%; height: 570px;float:left;  overflow-x: scroll; border-collapse: separate; border: 3px solid #ccc; overflow-y: auto;">
				
				<div class="row" style="background-color: #fff;margin-top:2%;margin-bottom:2%;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 25%;"> 
		    				<label class="title" >Bill Date.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 26%;">
		    				<input type="text"  id="txtBillDate" path="" style="width: 100px; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;"> 
		    				<label class="title" >Bill No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%;">
		    				<input type="text"  id="txtBillNo" path="" style="width: 100px; height: 25px;" ondblclick="funHelp('BillForChangeSettlement')" required="true" readonly="true"/>
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
						
						<table id="tblData" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
						<tbody>    
								<col style="width:50%;"><!--  COl1   -->
								<col style="width:50%"><!--  COl2   -->								
						</tbody>			
					</table>
				</div>
				
				<div style="margin-top: 40%">	
						<hr>
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 60%;"> 
			    				<label class="title" >Bill Amount</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="lblSubTotlal" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
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
						</tbody>			
					</table>
				</div>
				
				<div style="margin-top: 40%;">	
						<hr>
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 60%;"> 
			    				<label class="title" >Total</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="lblTotlal" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
			    			</div>
			    		</div>
				</div>
			
			</div>
			
   		 		 	
			<div style=" width: 60%; height: 570px;float:left;  overflow-x: scroll; border: 3px solid #ccc;  overflow-y: auto;">
					
				<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box;">
						<c:forEach items="${settlementList}" var="settlementList1">
				              <input type="button" id="${settlementList1.value.split("#")[0]}" value="${settlementList1.value.split("#")[0]}" style="margin-bottom:5px" onclick="funOnClickSettleName(this)"/>	
					    </c:forEach>
		    	</div>
		    			
		    	<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box;">
						<label class="title" id="lblPaymentMode" ></label>
		    	</div>
   		 		<div id="divCashSettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:140px;">
					    <div class="element-input col-lg-6" style="width: 36%;margin-top:2%"> 
		    				<label class="title" >Bill Amount.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBillAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Paid Amount.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtPaidAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Balance.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBalanceAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;"> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
				</div>	
				
					<div id="divCreditCardSettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:200px;">
					    <div class="element-input col-lg-6" style="width: 36%;margin-top:2%"> 
		    				<label class="title" >Bill Amount.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBillAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Paid Amount.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtPaidAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Balance.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtBalanceAmt" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Slip No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtSlipNo" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;margin-top:2%;"> 
		    				<label class="title" >Expiry Date.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtExpiryDte" path="" style="width:80%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;"> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
				</div>
				
				
				<div id="divCreditSettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:140px;">
					    <div class="element-input col-lg-6" style="width:36%;"> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
		    			<div class="element-input col-lg-6" style="width:36%;"> 
		    				<label class="title" >Cust Name.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtCustomerName" path="" style="width:140%; height: 25px;"/>
		    			</div>
				</div>	
				<div id="divComplimentrySettle" class="row" style="background-color:#D3D3D3; margin-top:2%; margin-left: 1%;display: -webkit-box;width:50%;height:140px;"> 
		    			<div class="element-input col-lg-6" style="width:36%;"> 
		    				<label class="title" >Remark.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="margin-top:2%;">
		    				<input type="text"  id="txtRemark" path="" style="width:140%; height: 25px;"/>
		    			</div>
		    			
				</div>
   		 			
			</div>
			<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						
				<p align="center">
				<div class="row" style="background-color: #fff; margin-top:2%; margin-left: 1%;display: -webkit-box; ">
		     		     <input id="btnSave" type="submit" value="Save" ></input>
		                 <input id="btnReset" type="reset" value="Reset"></input>
		                 <input id="btnClose" type="button" value="Close" onclick="funPOSHome();"></input>
		            
  		 			 </div>
		     	 </p>
		     	 
		     	 
   		   </div>
			
	</div>

	</s:form> 
    
<br /><br />       
 
</body>
</html>