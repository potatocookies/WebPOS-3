<%@ page import="java.util.LinkedList"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
</style>
<script type="text/javascript">


var selctedTableNo="";


/*On form Load It Reset form :Ritesh 22 Nov 2014*/
 $(document).ready(function () {
		  $('input#txtAreaCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtAreaName').mlKeyboard({layout: 'en_US'});
		  
		  $("form").submit(function(event){
			  if($("#txtAreaName").val().trim()=="")
				{
					alert("Please Enter Area Name");
					return false;
				}
			  else{
				  flg=funCallFormAction();
				  return flg;
			  }
			});
		}); 




	var tblMenuItemDtl_MAX_ROW_SIZE=100;
	var tblMenuItemDtl_MAX_COL_SIZE=4;
	var gMobileNo="";
	var gCRMInterface="${gCRMInterface}";
	var gCustAddressSelectionForBill="${gCustAddressSelectionForBill}";
	var gPrintType="${gPrintType}";
	var gCustomerCode="";
	var fieldName="";
		/**
		* Open Help
		**/
		function funHelp(transactionName)
		{	       
			fieldName=transactionName;
	       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
		function funSetData(code)
		{

			switch(fieldName)
			{
			case "POSAreaMaster":
				funTxtAreaClicked(code);
				break;
			
			case "POSCustomerMaster":
				funSetCustomerDataForHD(code);
				break;
			case "NewCustomer":
				funSetCustomerDataForHD(code);
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
				        	$("#CustomerName").text(response.strCustomerName);
				        	gCustomerCode=response.strCustomerCode;
					        	
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
		function funTxtAreaClicked(code)
		{
			var searchurl=getContextPath()+"/loadPOSAreaMasterData.html?POSAreaCode="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        		
					        	$("#lblAreaName").text(response.strAreaName);
					        	funLoadTableForArea(code);
					        
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
		
		function funLoadTableForArea(code)
		{
			$('#tblTableDtl').empty();
			var searchurl=getContextPath()+"/funLoadTableForArea.html?areaCode="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	var tblTableDtl=document.getElementById('tblTableDtl');
							
				    		var insertCol=0;
				    		var insertTR=tblTableDtl.insertRow();
				    		
				    		
				    		
				    		$.each(response.tableDtl, function(i, obj) 
				    		{									
				    												
				    				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				    				{
				    					var col=insertTR.insertCell(insertCol);
				    					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"'    style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funTableClicked(this)\" /></td>";
				    					
				    					insertCol++;
				    				}
				    				else
				    				{					
				    					insertTR=tblTableDtl.insertRow();									
				    					insertCol=0;
				    									
				    					var col=insertTR.insertCell(insertCol);
				    					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"'     style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funTableClicked(this)\" /></td>";
				    					
				    					insertCol++;
				    				}							
				    			
				    			  
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
		
		function funTableClicked(objTableButton)
		{
			//funRemoveTableRows("tblBillItemDtl");
			
			$("#tblBillItemDtl").empty();
			
			var tblMenuItemDtl=document.getElementById('tblTableDtl');
		
			 selctedTableNo=objTableButton.id;
			
			var jsonArrForTableDtl=${command.jsonArrForTableDtl};	
			
			
			$.each(jsonArrForTableDtl, function(i, obj) 
			{									
				if(obj.strTableNo==selctedTableNo)
				{									
					$("#txtTableName").val(obj.strTableName);	
					$("#txtPaxNo").val(obj.intPaxNo);			
				}
			});
			
			var searchurl=getContextPath()+"/funFillItemTableDtl.html?tableNo="+selctedTableNo;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	$("#txtWaiterName").val(response.strWaiterName);	
				        	
				        	
				        	var totalAmt=0;
				        	$.each(response.itemDtl, function(i, obj) 
			        		{									
			        			
				        		funFillItemDtl(obj);
				        		
				        		totalAmt=totalAmt+obj.dblAmount;
				        		
			        		});
				        	
				        	$("#txtTotal").val(totalAmt);
					        
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
		
		function funFillItemDtl(obj)
		{	
			var tblTableDtl=document.getElementById('tblBillItemDtl');
			var rowCount = tblTableDtl.rows.length;
			var insertRow = tblTableDtl.insertRow(rowCount);
					
		    var col1=insertRow.insertCell(0);
		    var col2=insertRow.insertCell(1);
		    var col3=insertRow.insertCell(2);
		    col1.innerHTML = "<input readonly=\"readonly\"   class=\"itemName\"    style=\"text-align: left; width: 295px;\"   name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].strItemName\" id=\"strItemName."+(rowCount)+"\" value='"+obj.strItemName+"' />";
		    col2.innerHTML = "<input readonly=\"readonly\"    class=\"itemQty\"      style=\"text-align: right; width: 50px;\"  name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].dblQuantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+obj.dblItemQuantity+"' />";
		    col3.innerHTML = "<input readonly=\"readonly\"    class=\"itemAmt\"      style=\"text-align: right;  width: 135px;\"  name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].dblAmount\" id=\"dblAmount."+(rowCount)+"\" value='"+obj.dblAmount+"' />";
		}
	
		function funRemoveTableRows(tableId)
		{
			var table = document.getElementById(tableId);
			var rowCount = table.rows.length;
			while(rowCount>1)
			{
				table.deleteRow(1);
				rowCount--;
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
		
		function funCustomerBtnClicked()
		{
			var tblTableDtl=document.getElementById('tblBillItemDtl');
			var rowCount = tblTableDtl.rows.length;
			if(rowCount==1)
				{
				alert("Please Select Table");
				return;
				}
			if(gCRMInterface=="SQY")
				{
				var strMobNo = prompt("Enter Mobile number", "");
				}
			else
	        {
	            funNewCustomerButtonPressed();
	        }
		}
		
		function funNewCustomerButtonPressed()
		{
			if (gCRMInterface=="SQY")
	        {
				 var strMobNo = prompt("Enter Mobile number", "");
				 
	        }
			else if (gCRMInterface=="PMAM")
	        {
				 var strMobNo = prompt("Enter Mobile number", "");
				 if(strMobNo.trim().length>0)
					 funSetCustMobileNo(strMobNo);
				 
	       	}
			else
	        {
				 var strMobNo = prompt("Enter Mobile number", "");
				 if(strMobNo.trim().length>0)
				 {
					 funSetCustMobileNo(strMobNo);
				 }
	       	}
		}

		function  funSetCustMobileNo(strMobNo)
		{
			gMobileNo=strMobNo;
			var totalBillAmount = 0.00;
			  if ($("#txtTotal").val().trim().length > 0)
	          {
				  totalBillAmount = parseFloat($("#txtTotal").val());
	          }
			 if (strMobNo.trim().length == 0)
	         {
				 funHelp('POSCustomerMaster');
	         }
			 else
				 funCheckCustomer(strMobNo);
		}
		function funCheckCustomer(strMobNo)
		{		
			var searchurl=getContextPath()+"/funCheckCustomer.html?strMobNo="+strMobNo;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	 if (response.flag)
				             {
				        		 if(gCustAddressSelectionForBill=="Y")
				 				{
				 				 	window.open("frmHomeDeliveryAddress.html?strMobNo="+gMobileNo+"","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
				 				} 
				        		 else
				        		 gCustomerCode=response.strCustomerCode;
				        		 $("#CustomerName").text(response.strCustomerName);
				             }	
				        	 else
				        		 funCustomerMaster(strMobNo);
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
				 <%session.setAttribute("frmName", "frmPOSRestaurantBill");%>

				
				window.open("frmPOSCustomerMaster.html","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
			}
		
		 function funCheckKOTClicked()
			{
			 var tblTableDtl=document.getElementById('tblBillItemDtl');
				var rowCount = tblTableDtl.rows.length;
				if(rowCount==1)
					{
					alert("Please Select Table");
					return;
					}
				
		            if (gPrintType=="Text File")
		            {
		              //  clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
		               // ob.fun_CkeckKot_TextFile(globalTableNo, txtWaiterNo.getText().trim());
		            }
		        
		       
		    }
		
		 function funHomeBtnclicked()
			{
				var loginPOS="${loginPOS}";
				var tblTableDtl=document.getElementById('tblBillItemDtl');
				var rowCount = tblTableDtl.rows.length;
				if(rowCount>1)
		        {
		           if(confirm( "Do you want to end Transaction?"))
		        	   window.location ="frmGetPOSSelection.html?strPosCode="+loginPOS;
				else
		            return;
		        } 
		        else
		        {
		        	window.location ="frmGetPOSSelection.html?strPosCode="+loginPOS;
		        }	
		    }
		 
		 function funDoneBtnclicked()
		 {
			 var tblTableDtl=document.getElementById('tblBillItemDtl');
			 var rowCount = tblTableDtl.rows.length;
			 if(rowCount<=0)
			 {
					alert("Please select table");
					return;
			 } 
		     else
		     {
		    	 funMakeBillBtnClicked();
		     }	
		 }
		 
		 
			//display billsettlement window tab
			function funMakeBillBtnClicked()
			{

				var $rows = $('#tblSettleItemTable').empty();
				document.getElementById("tab2").style.display='block';		
			    document.getElementById("tab1").style.display='none';
			    
			    operationType="DineIn";
			    transactionType="Make Bill";
			    
			    finalSubTotal=0.00;
				finalDiscountAmt=0.00;
				finalNetTotal=0.00;
				taxTotal=0.00;
				taxAmt=0.00;
				finalGrandTotal=0.00;
				
				gTableNo=selctedTableNo;
				gTableName=$("#txtTableName").val();

				
				$("#tableNameForDisplay").text("Table No : "+gTableName);
			    
			    var listItmeDtl=[];
			    var mergeduplicateItm = new Map();
			    var hmItempMap=new Map();
			    
			    
			    
			    var searchurl=getContextPath()+"/funGetItemsForTable.html?bussyTableNo="+gTableNo;
				 $.ajax({
					        type: "GET",
					        url: searchurl,
					        dataType: "json",
					        async:false,
					        success: function(response)
					        {
					        	$.each(response.itemList, function(i,item)
			    				{			
					        		
					        		var itemName=item.strItemName;									
									var itemQty=item.dblQuantity;									
									var itemAmt=item.dblAmount;
									var rate=item.dblRate;
									var itemCode=item.strItemCode;
					        		
			    		    		
					        		hmItempMap.set(itemCode,itemName);
					        		
					        		var isModifier=false;
					  		    	if(itemName.startsWith("-->"))
					  				{
					  		    		isModifier=true;
					  				}
					  		    	
					  		    	
					  		    	var subgroupcode=item.strSubGroupCode;
							 		var subgroupName=item.strSubGroupName;
					  		    	var groupcode=item.strGroupCode;
							 		var groupName=item.strGroupName;
												 							
									hmSubGroupMap.set(subgroupcode, subgroupName);
									hmGroupMap.set(groupcode, groupName); 
					  		    	
					  		    	
									
									var singleObj = {}

								    singleObj['itemCode'] =itemCode;
									singleObj['itemName'] =itemName;				
									singleObj['quantity'] =itemQty;																	    								   
								    singleObj['rate'] =rate;				   								    
								    singleObj['amount'] = itemAmt;								    
								    singleObj['isModifier'] =isModifier;								    
								    singleObj['discountPer'] = 0.0;
								    singleObj['discountAmt'] =0.0;
								    singleObj['strSubGroupCode'] =subgroupcode;
								    singleObj['strGroupcode'] =groupcode;
								    
								   
								    
								    listItmeDtl.push(singleObj);
					  		    	
					  		    	
					  		    	
					  		    	
			    		    		
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
			    
				 /**
					*calculating promotions and filling data to grid for bill print	
					*/
					funCalculatePromotion(listItmeDtl);
					
					funRefreshSettlementItemGrid();
			    
		
			}
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
</script>
</head>

<body>

	<div id="formHeading">
	<label>Make Bill</label>
	</div>


	<s:form name="frmMakeBill" method="POST" commandName="command" action="saveKOT.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">			
		
	 <div class="title">
	
			<div style=" width: 50%; float:left; border-collapse: separate; ">

	    		
	    		<div style="width: 994px; margin-left: 124px;">
     					
     		  			<label class="title" >Table</label> 
     		  			<input  type="text"  id="txtTableName" readonly="true" style="text-transform: uppercase;width: 152px;height:25px;" />
     		  			
     		  			<label class="title" >Waiter</label>
     		  			<input  type="text"  id="txtWaiterName" readonly="true" style="text-transform: uppercase;width: 126px;height:25px;" />
     		  			
     		  			<label class="title" >PAX</label>
     		  			<input  type="text"  id="txtPaxNo" readonly="true" style="width: 36px;height:25px;" />
			     	
			     	     <input  type="text"  id="lblAreaName" readonly="true" style="margin-left: 150px;  width: 150px;height: 30px; display: inline-block;  text-align: center; "  onclick="funHelp('POSAreaMaster')" > ${areaName} </input>		 
     		    </div>
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
				<div id="divBillItemDtl" style="border: 1px solid #ccc;height: 429px;overflow-x: hidden;overflow-y: scroll;width: 500px;margin-top: 31px;margin-left: 130px;">
							
							<table style="width: 100%">
								<thead>
									<tr>
										  <th style="width: 51%">
										  	<input type="button" value="Description" class="tblBillItemDtlColBtnGrp" style="width: 295px;background: #78BEF9;"></input>
										  </th>
										  <th>
										  	<input type="button" value="Qty"  class="tblBillItemDtlColBtnGrp" style="width: 50px;background: #78BEF9;"></input>
										  </th>
										  <th>
										  	<input type="button" value="Amount" class="tblBillItemDtlColBtnGrp" style="width: 135px;background: #78BEF9;"></input>
										  </th>
									</tr>
								</thead>		
							</table>	
							<table id="tblBillItemDtl"   >
															
							</table>
				</div>
				
				<div style="margin-left: 129px;margin-top: 3px; ">
					 
					 	<label class="title" >Customer :</label>	    			
	    				<input  type="text"  id="CustomerName" style="width: 265px; " />					 
					 
	    				<label class="title" >Total</label>	    				    		
	    				<input  type="text"  id="txtTotal" readonly="true" style="width:100px;text-align: right;" />
	    			
	    		</div>
	    		
	    		
	    		<br/>
			</div>
			

			<div style=" width: 50%; float:left;  border-collapse: separate; ">
				
	    	    
	    	    <div id="divItemDtl" style="border: 1px solid #ccc;/* top: 78px; */height: 430px;overflow-x: auto;overflow-y: scroll;width: 500px;margin-top: 60px;">									
								
						<table id="tblTableDtl"   >
								 <c:set var="sizeOfMenuItems" value="${fn:length(command.jsonArrForTableDtl)}"></c:set>									   
								 <c:set var="itemCounter" value="${0}"></c:set>									   									   					
								 <c:forEach var="objItemPriceDtl" items="${command.jsonArrForTableDtl}"  varStatus="varMenuItemStatus">																																		
			    					<tr>
											<%
												for(int x=0; x<4; x++)
												{
											%>														
													<c:if test="${itemCounter lt sizeOfMenuItems}">	
																
													 	<td style="padding: 5px;" >													 															 
													 		
													 		<input type="button" id="${command.jsonArrForTableDtl[itemCounter].strTableNo}" value="${command.jsonArrForTableDtl[itemCounter].strTableName}" style="width: 100px;height: 100px; white-space: normal;" class="btn btn-danger"   onclick="funTableClicked(this)" /></td>																						 																											 		
																
														<c:set var="itemCounter" value="${itemCounter +1}"></c:set>
																
												 	</c:if>																 															
											  <%}
											%>
									  </tr>																																
								 </c:forEach>	 
						</table>
				</div>
				
			</div>
			
	    </div>
	    
	   
	    

		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 240px;">
     					
		<p align="center">
     		  
		 		<input  type="button" id="Home"  value="Home"  style="width: 100px;height: 35px; white-space: normal;"   onclick="funHomeBtnclicked()" class="btn btn-spos-outline-success" />
		   			
				<input  type="button" id="Check KOT"  value="Check KOT"  style="width: 100px;height: 35px; white-space: normal;"   onclick="funCheckKOTClicked()" class="btn btn-spos-outline-success" />
		    			
				<input  type="button" id="Customer"  value="Customer"  style="width: 100px;height: 35px; white-space: normal;"   onclick="funCustomerBtnClicked()" class="btn btn-spos-outline-success" />
		   			
				<input  type="button" id="Done"  value="Done"  style="width: 100px;height: 35px; white-space: normal;"   onclick="funDoneBtnclicked()" class="btn btn-spos-outline-success" />
     		  
   		</div>			
					
		 
		

	</s:form>
</body>
</html>