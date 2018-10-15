<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Customer On Bill</title>
<script type="text/javascript">
 	var fieldName,textValue2="",selectedRowIndex=0,delTableNo="",delKotNo="",delItemcode="", delAmount="",delQuatity="";
 	var gCMSIntegrationYN="${gCMSIntegrationYN}";
 	
	$(document).ready(function()
			{

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
			function searchTable()
			{
				var table = $('#tblDataFillGrid');
				var inputVal=$("#txtItemSearch").val();
				table.find('tr').each(function(index, row)
						{
							var allCells = $(row).find('td');
							if(allCells.length > 0)
							{
								var found = false;
								allCells.each(function(index, td)
								{
									var regExp = new RegExp(inputVal, 'i');
									if(regExp.test($(td).find('input').val()))
									{
										found = true;
										return false;
									}
								});
								if(found == true)$(row).show();
								else $(row).hide();
							}
						});;
						
						
						
						
			}	
			
			
			function funCustomerBtnClicked()
			{
				if($("#lblBillNo").val()=="")
				{
					alert("Please Select Bill");
				}
				else
				{	
					if(gCMSIntegrationYN=="Y")
					{
						funGetCMSMemberCode();
					}
					else
			        {
			            funNewCustomerButtonPressed();
			        }
				}
			}
			
			
		
			
			
			
			function funGetCMSMemberCode()
			{
				 var strCustomerCode = prompt("Enter Member Code", "");
				 if(strCustomerCode.trim().length>0)
					 {
					 var searchurl=getContextPath()+"/funCheckMemeberBalance.html?strCustomerCode="+strCustomerCode;
					 $.ajax({
						        type: "GET",
						        url: searchurl,
						        dataType: "json",
						        success: function(response)
						        {
						        	if(response.flag)
						        	{
						        		 if (response.memberInfo.split("#")[4].trim().equals("Y"))
						                 {
						                     alert("Member is blocked");
						                     return;
						                 }
						                 else
						                 {
						                     cmsMemCode = response.memberInfo.split("#")[0];
						                     cmsMemName = response.memberInfo.split("#")[1];
						                     $('#lblCustCode').text("Memeber Code");
						                     $('#lblCustCodeValue').text(cmsMemCode);
						                     $('#lblCustName').text("Memeber Name");
						                     $('#lblCustNameValue').text(cmsMemName);
						        
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
					
			}
			
			
			function funNewCustomerButtonPressed()
			{
			
					 var strMobNo = prompt("Enter Mobile number", "");
					 if(strMobNo!=null)
					 {
						 if(strMobNo.length>0)
						 {	 
						 funSetCustMobileNo(strMobNo);
						 }
						 else
						 {
							 funHelp1('POSCustomerMaster');
						 }
					 }
					 	 
		       	
			}
			
			function  funSetCustMobileNo(strMobNo)
			{
				
			
				 if (strMobNo.trim().length == 0)
		         {
					 funHelp1('POSCustomerMaster');
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
					                 $('#lblCustCode').val("Customer Code");
				                     $('#lblCustCodeValue').val(response.strCustomerCode);
				                     $('#lblCustName').val("Customer Name");
				                     $('#lblCustNameValue').val(response.strCustomerName);
					             }	
					        	 else
					        	 {
					        		 //funCustomerMaster(strMobNo);
					        		 //window.location.href=getContextPath()+"/frmCustomerMasterForNewCustomer.html?mobileNo="+strMobNo;
					        		 window.open(getContextPath()+"/frmCustomerMasterForNewCustomer.html?mobileNo="+strMobNo);
					        		// window.open("frmPOSCustomerMaster.html?mobileNo="+strMobNo,"","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
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
			
			
			function funHelp1(transactionName)
			{
				fieldName=transactionName;
				window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
			}
			
			
		 	function funSetData(code)
		 	{

		 		switch(fieldName)
		 		{
				
		 		case "POSCustomerMaster":
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
				
				function funAddFullRow(data){
					$('#tblDataFillGrid tbody').empty();
					var table = document.getElementById("tblDataFillGrid");
					var rowCount = table.rows.length;
					var row = table.insertRow(rowCount);
					row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Bill No\" value=Bill No >";
					row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Time\" value=Time >";
					row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Table Name\" value=Table Name >";
					
					rowCount++;
				    for(var i=0;i<data.length;i++){
				    	row = table.insertRow(rowCount);
				    	var rowData=data[i];
				    	
				    	for(var j=0;j<rowData.length;j++){
				    		
				    		row.insertCell(j).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowData[j]+"\" value='"+rowData[j]+"' onclick=\"funGetSelectedRowData(this)\"/>";

				    		 
				    	}
				    	rowCount++;
				    }
					
					
					
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
				
			     function funGetSelectedRowData(obj)
			     {
			    	funRemoveTableRows("tblData");
			    	var i = obj.parentNode.parentNode.rowIndex;
			    	var tableName = document.getElementById("tblDataFillGrid");
			    	
			    	var billNo=tableName.rows[i].cells[0].children[0].value;
			       	var time=tableName.rows[i].cells[0].children[0].value;
			        var tableName=tableName.rows[i].cells[0].children[0].value;
			
			        $("#lblBillNo").val(billNo);
			        searchUrl=getContextPath()+"/loadSelectedBillItem.html?billNo="+billNo;
					$.ajax({
					        type: "GET",
					        url: searchUrl,
					        async:false,
					        dataType: "json",
		
						    success: function(response)
						    {
					         funAddItemTableData(response.gridData,response.taxAmt,response.subTotal,response.grandTotal,response.gShowItemDetailsGrid,response.userCreated,response.strCustomerCode,response.strCustomerName);
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
			     
			     
			     function funAddItemTableData(itemDataList,taxAmt,subTotal,grandTotal,gShowItemDetailsGrid,userCreated,customerCode,customerName){
			    		$('#tblData tbody').empty()

			    		var table = document.getElementById("tblData");
			    		var rowCount = table.rows.length;

			    		var row = table.insertRow(rowCount);
			    		  	for(var i=0;i<itemDataList.length;i++){
			    	    	  row = table.insertRow(rowCount);
			    	    	  var rowItemData=itemDataList[i];
			    	    	
			    	    		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\""+rowItemData[0]+"\" value='"+rowItemData[0]+"' / >";
			    	    		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[1]+"\" value='"+rowItemData[1]+"'/>";
			    	    		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[2]+"\" value='"+rowItemData[2]+"' />";
			    	    		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[3]+"\" value='"+rowItemData[3]+"' />";
			    	    		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+rowItemData[4]+"\" value='"+rowItemData[4]+"' />";
			    	    		rowCount++;
			    	    	}
			    	
			     	    if(gShowItemDetailsGrid=='N')
			     	    {
			     	    	$('#tblData tbody').empty();
			     		}else{
			    	    	
			    	    	$("#lblUserCreated").val(userCreated);

			    	    	$("#lblTax").val(taxAmt);
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
 	</script>
<body>
       
     <div id="formHeading">
		<label>Change Customer On Bill</label>
			</div>

	<s:form name="Customer Change On Bill" method="POST" action="saveChangeCustomerOnBill.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
	   
	   <div class="title">
	       <div>
		   		<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Bill No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<s:input  type="text" path="strBillNo" id="lblBillNo" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >User Created</label>
		    			</div>
		   				<div class="element-input col-lg-6" style="width: 15%;">
		    				<input  type="text"  id="lblUserCreated" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Date & Time</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<input  type="text"  id="lblDateTime" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
		    			</div>
		    	</div>
	    	</div>
	    	<div>
		    	<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Customer Code</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<s:input  type="text" path="strCustomerCode" id="lblCustCodeValue" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;"> 
		    				<label class="title" >Customer Name</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    			<input type="text" id="lblCustNameValue" readonly="true" style="text-transform: uppercase; width:356px; height:25px;" />
		    			</div>
		    	</div>
	   		</div>	
	
			<div style=" width: 50%; height: 500px;float:left;  overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
				
				<div>
					<table style="width: 130%; border: #0F0; table-layout: fixed; overflow: scroll">
						<thead>
								<tr >
									<th style="border: 1px  black solid;width:40%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Description</label></th>
									<th style="border: 1px  black solid;width:10%;background-color: #42b3eb;
											   color: white;text-align: center;"><label>Qty</label></th>
									<th style="border: 1px  black solid;width:30%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Amount</label></th>
									<th style="border: 1px  black solid;width:10%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Mod Code</label></th>
									<th style="border: 1px  black solid;width:10%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>KOT</label></th>
								</tr>
						</thead>
						</table>
						
						<table id="tblData" style="width: 130%; border: #0F0; table-layout: fixed; overflow: scroll">
						<tbody>    
								<col style="width:40%;"><!--  COl1   -->
								<col style="width:10%"><!--  COl2   -->
								<col style="width:30%;"><!--  COl3   -->	
								<col style="width:10%;"><!--  COl4   -->	
								<col style="width:10%;"><!--  COl5   -->								
						</tbody>			
					</table>
				</div>
				
				<div style="margin-top: 195px;">	
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 50%;"> 
			    				<label class="title" >SubTotal</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="lblSubTotlal" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
			    			</div>
			    		</div>
			    		<div >	
				      		<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
								<div class="element-input col-lg-6" style="width: 50%;"> 
				    				<label class="title" >Tax</label>
				    			</div>
				    			<div class="element-input col-lg-6">
				    				<input  type="text"  id="lblTax" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
				    			</div>
				    		</div>
			    		</div>
			    		<div >	
				      		<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
								<div class="element-input col-lg-6" style="width: 50%;"> 
				    				<label class="title" >TOtal</label>
				    			</div>
				    			<div class="element-input col-lg-6">
				    				<input  type="text"  id="lblTotal" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
				    			</div>
				    		</div>
				    	</div>	
				</div>
			
			</div>
			
   		 		 	
			<div style=" width: 50%; height: 500px;float:left;  overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
					
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 20%;margin-left: 3%;"> 
		    				<label class="title" >Bill No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 30%;">
		    				<input type="text"  id="txtItemSearch" path="" style="width: 100px; height: 25px;"/>
		    			</div>
		    		</div>	
		    		
		    		<div>	
				       <div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="col-lg-10 col-sm-10 col-xs-10">
					     		  <p align="center">
					            		<div class="submit col-lg-4 col-sm-4 col-xs-4">
					            			<input id="btnSearch" type="button" value="Search" onclick="searchTable();"></input>
					            		</div>
					            		<div class="submit col-lg-4 col-sm-4 col-xs-4">
					            			<input id="btnCustomer" type="button" value="Customer" onclick="funCustomerBtnClicked();"></input>
					            		</div>
					            		<div class="submit col-lg-4 col-sm-4 col-xs-4">
					            			<input id="btnClose" type="button" value="Close" onclick="funPOSHome();"></input>
					            		</div>
					     		  </p>
		   		 			</div>
	   		 		 	</div>
   		 		      </div>
   		 			<table id="tblDataFillGrid" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
							
					</table>
   		 			
			</div>
			<div style="margin-top: 60%;">	
				<hr>
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="col-lg-10 col-sm-10 col-xs-10">
					     		  <p align="center">
					     		  <div class="submit col-lg-4 col-sm-4 col-xs-4">
					            			<input id="btnSave" type="submit" value="Save"></input>
					              </div>
					     		  </p>
		   		 			</div>
	   		 	</div>
		   </div>
	</div>

	</s:form> 
    
<br /><br />       
 
</body>
</html>