<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
 	var fieldName,textValue2="",selectedRowIndex=0,delTableNo="",delKotNo="",delItemcode="", delAmount="",delQuatity="";
 	var gCMSIntegrationYN="${gCMSIntegrationYN}";
 	
	$(document).ready(function()
			{

				$('#lblCustCodeValue').text("");
				$('#lblCustNameValue').text("");
				$("#lblBillNo").text("");
				funFillGrid();
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
			
			
				if(gCMSIntegrationYN=="Y")
					{
					funGetCMSMemberCode();
					}
				else
		        {
		            funNewCustomerButtonPressed();
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
					 if(strMobNo.trim().length>0)
					 {
						 funSetCustMobileNo(strMobNo);
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
					                 $('#lblCustCode').text("Customer Code");
				                     $('#lblCustCodeValue').text(response.strCustomerCode);
				                     $('#lblCustName').text("Customer Name");
				                     $('#lblCustNameValue').text(response.strCustomerName);
					        		 
					        		 
					             }	
					        	 else
					        		 {
					        		 funCustomerMaster(strMobNo);
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
						        	
						        	 $('#lblCustCode').text("Customer Code");
				                     $('#lblCustCodeValue').text(code);
				                     $('#lblCustName').text("Customer Name");
				                     $('#lblCustNameValue').text(response.strCustomerName);
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

					
					window.open("frmPOSCustomerMaster.html","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
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
//			 	    		 row.insertCell(j).innerHTML= "<input type=\"hidden\" readonly=\"readonly\" class=\"cell\" value='"+rowData[j]+"' />";
				    		 
				    	}
				    	rowCount++;
				    }
					
					
					
				}
				
				
			     function funGetSelectedRowData(obj)
			     {
			    	
			    	var i = obj.parentNode.parentNode.rowIndex;
			    	var tableName = document.getElementById("tblDataFillGrid");
			    	
			    	var billNo=tableName.rows[i].cells[0].children[0].value;
			       	var time=tableName.rows[i].cells[0].children[0].value;
			        var tableName=tableName.rows[i].cells[0].children[0].value;
			
			        $("#lblBillNo").text(billNo);
			        searchUrl=getContextPath()+"/loadSelectedBillItem.html?billNo="+billNo;
					$.ajax({
					        type: "GET",
					        url: searchUrl,
					        async:false,
					        dataType: "json",
					        
//			 		        data:{ kot:kot,
//			 		        	tableName:tableName,
								
								
//			 				},
						    success: function(response)
						    {
					         funAddItemTableData(response.gridData,response.taxAmt,response.subTotal,response.grandTotal,response.gShowItemDetailsGrid,response.userCreated);
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
			     
			     
			     function funAddItemTableData(itemDataList,taxAmt,subTotal,grandTotal,gShowItemDetailsGrid,userCreated){
			    		$('#tblData tbody').empty()

			    		var table = document.getElementById("tblData");
			    		var rowCount = table.rows.length;

			    		var row = table.insertRow(rowCount);
			    		row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"50%\" id=\"Description\" value=Description >";
			    		row.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Quantity\" value=Qty >";
			    		row.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Amount\" value= Amount>";
			    		row.insertCell(3).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"Item Code\" value=Mod Code >";
			    		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"KOT No\" value=KOT No >";
//			     		row.insertCell(4).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\"\" value=Select >";
			    		
			    		rowCount++;
			    	  
			    	    	
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
			     	    	$('#tblData tbody').empty()
			     		}else{
			    	    	
			    	    	$("#lblUserCreated").text(userCreated);
// 		    	    		$("#lblDateTime").text(rowItemData[5]);
			    	    	$("#lblTax").text(taxAmt);
			    	    	$("#lblSubTotlal").text(subTotal);
			    	    	$("#lblTotal").text(grandTotal);
			     		}
			     }
			     
			     function funSaveBtnClicked()
			     {
			   
                     var billNo=$("#lblBillNo").text();
                     var CustCode=$("#lblCustCodeValue").text();
                     if(CustCode=='')
                    	 {
                    	 alert("Please Select Customer");
                    	 }else{
                    	 if(billNo=='')
                    	 {
                    		 alert("Please Select Bill"); 
                    	
                    	 }else{
                    	
			         searchUrl=getContextPath()+"/saveChangeCustomerOnBill.html?billNo="+billNo+"&CustCode="+CustCode;
						$.ajax({
						        type: "GET",
						        url: searchUrl,
						        async:false,
						        dataType: "json",
							    success: function(response)
							    {
							      if(response)
							      {
						          alert("Data Save Successfuly");
						          window.location ="frmPOSChangeCustomerOnBill.html?";
							      }
							      else{
							    	  alert("Not Save");
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
			     }
			     
			     function funCloseBtnClicked()
			     {
			    	 var loginPOS=message='<%=session.getAttribute("gPOSCode").toString()%>';
			    	 window.location ="frmGetPOSSelection.html?strPosCode="+loginPOS;
			    	 
			     }
 	</script>
<body>
       
     <div id="formHeading">
		<label>Change Customer On Bill</label>
			</div>

	<s:form name="Customer Change On Bill" method="POST" action="" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
	   
	   <div class="title">
	   
	   		<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Bill No.</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 15%;">
	    				<input  type="text"  id="lblBillNo" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
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
	    	
	    	<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
					<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Customer Code</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 15%;">
	    				<input  type="text"  id="lblCustCodeValue" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 15%;"> 
	    				<label class="title" >Customer Name</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 15%;">
	    				<input  type="text"  id="lblCustNameValue" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
	    			</div>
	    	</div>
	   			
	
			<div style=" width: 50%; height: 300px;float:left;  overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
				
				<div>
					<table id="tblData" style="width: 130%; border: #0F0; table-layout: fixed; overflow: scroll">
						<thead>
								<tr >
									<td style="border: 1px  black solid;width:5%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Description</label></td>
									<td style="border: 1px  black solid;width:5%;background-color: #42b3eb;
											   color: white;text-align: center;"><label>Qty</label></td>
									<td style="border: 1px  black solid;width:5%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Amount</label></td>
									<td style="border: 1px  black solid;width:5%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>Mod Code</label></td>
									<td style="border: 1px  black solid;width:5%;background-color: #42b3eb;
    										   color: white;text-align: center;"><label>KOT</label></td>
								</tr>
						</thead>
					</table>
				</div>
				<hr>
				<div>	
						<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 50%;"> 
			    				<label class="title" >SubTotal</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="lblSubTotlal" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
			    			</div>
			    		</div>
			    		<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
							<div class="element-input col-lg-6" style="width: 50%;"> 
			    				<label class="title" >Tax</label>
			    			</div>
			    			<div class="element-input col-lg-6">
			    				<input  type="text"  id="lblTax" readonly="true" style="text-transform: uppercase; width:100px; height:25px;" />
			    			</div>
			    		</div>
				</div>
				
			</div>
			
			<div style=" width: 50%; height: 300px;float:left;  overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
					
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 20%;margin-left: 3%;"> 
		    				<label class="title" >Bill No.</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 30%;">
		    				<input type="text"  id="txtItemSearch" path="" style="width: 100px; height: 25px;"/>
		    			</div>
		    		</div>	
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
				            			<input id="btnClose" type="button" value="Close" onclick="funCloseBtnClicked();"></input>
				            		</div>
				     		  </p>
	   		 			</div>
   		 		 	</div>
   		 		
   		 			<table id="tblDataFillGrid" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll">
							
					</table>
   		 			
			</div>
			
	</div>
	
	
	
	   
<!-- 	   <div> -->
<!-- 	   <div> -->
<!-- 	   <table> -->
<!-- 	 <tr> <td> -->
<!-- 					<label>Bill No.</label> -->
<!-- 					&nbsp;&nbsp; -->
<!-- 					<label id="lblBillNo" /> -->
<!-- 			    </td> -->
			    
<!-- 			    <td> -->
<!-- 					&nbsp;&nbsp;<label>User Created</label> -->
<!-- 					&nbsp;&nbsp;<label id="lblUserCreated"  /> -->

<!-- 			    </td> -->
<!-- 			    </tr> -->
<!-- 			    <tr>   -->
<!-- 			    <td> -->
<!-- 					<label>Date & Time</label> -->
<!-- 					&nbsp;&nbsp;<label id="lblDateTime"  /> -->
					
<!-- 			    </td> -->

<!-- 			    </tr> -->
<!-- 			    <tr> -->
<!-- 			       <td> -->
<!-- 					<label id="lblCustCode" >Customer Code </label> -->
<!-- 					&nbsp;&nbsp;<label id="lblCustCodeValue"  /> -->

<!-- 			    </td> -->
<!-- 			    </tr> -->
<!-- 			    <tr> -->
<!-- 			       <td> -->
<!-- 					<label id="lblCustName">Customer Name </label> -->
<!-- 					&nbsp;&nbsp;<label id="lblCustNameValue"  /> -->

<!-- 			    </td> -->
<!-- 			    </tr> -->
<!-- 			    </table> -->
<!-- 	   </div> -->
<!-- 	     <div style=" width: 50%; height: 500px;float:left;background-color: #a4d7ff; ">  -->
<!-- 	     <br> -->
<!--             <div style=" background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 400px; margin: auto; overflow-x: scroll; overflow-y: scroll; width: 90%;"> -->
<!-- 					<table id="tblData" -->
<!-- 							style="width: 130%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 							class="transTablex col2-right col3-right col4-right col5-right"> -->
<!-- 							<tr > -->
<!-- 						<td style="border: 1px  white solid;width:5%"><label>Description</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:5%"><label>Qty</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:5%"><label>Amount</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:5%"><label>Mod Code</label></td> -->
<!-- 						<td style="border: 1px  white solid;width:5%"><label>KOT</label></td> -->
<!-- <!-- 						<td style="border: 1px  white solid;width:10%"><label>Select</label></td> --> 
						
						
<!-- 					</tr> -->
<!-- 					</table> -->
					
<!-- 			</div> -->
<!-- 			<table class=transFormTable > -->
					
<!-- 					<tr> -->
<!-- 						 <td> -->
<!-- 							<label>SubTotal</label> -->
<!-- 							&nbsp;&nbsp;<label id="lblSubTotlal"/> -->
<!-- 						</td> -->
<!-- 					</tr> -->
<!-- 						<tr > -->
<!-- 						 <td > -->
<!-- 							<label >Tax</label> -->
						
<!-- 							&nbsp;&nbsp;<label id="lblTax" /> -->
<!-- 						</td> -->
<!-- 					</tr> -->
					
<!-- 			     </table> -->
			     
<!-- 			     <div> -->
<!-- 			     <table class=transFormTable> -->
<!-- 			       <tr><td> -->
<!-- 			        <input type="button" value="Save"  class="form_button" onclick="funSaveBtnClicked();" /> -->
<!-- 			        </td> -->
<!-- 				   </tr> -->

<!-- 				   </table> -->
<!-- 			     </div> -->
            
<!--         </div> -->

<!-- 		<div style=" width: 50%; height: 500px; float:right; border-collapse: separate; overflow-x: hidden; overflow-y: scroll; background-color: #C0E2FE;"> -->
<!-- 		    <br> -->
<!-- 		   <table class=transFormTable> -->
			
<!-- 			<tr> -->
<!-- 				<td> -->
					
<%-- 					<s:input type="text"  id="txtItemSearch" path=""  cssStyle="width:175px; height:20px;" cssClass="searchTextBox jQKeyboard form-control" /> --%>
<!-- 				    <input id="btnSearch" type="button" class="smallButton" value="Search" onclick="searchTable();"></input> -->
<!-- 		          <input id="btnCustomer" type="button" class="form_button" value="Customer" onclick="funCustomerBtnClicked();"></input> -->
<!-- 		          <input id="btnClose" type="button" class="smallButton" value="Close" onclick="funCloseBtnClicked();"></input> -->
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			</table> -->
			
<!-- 			 <div style=" background-color: #C0E2FE; border: 1px solid #ccc; display: block; height: 400px; margin: auto; overflow-x: scroll; overflow-y: scroll; width: 90%;"> -->
<!-- 					<table id="tblDataFillGrid" -->
<!-- 							style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 							class="transTablex col2-right col3-right col4-right col5-right"> -->
<!-- 							<tr > -->

<!-- 					</tr> -->
<!-- 					</table> -->
<!-- 					<br/> -->
<!-- 					<br/> -->
<!-- 					<br/>	 -->
<!-- 			</div> -->
			
<!-- 			<br> -->
<!-- 		</div> -->
		
<!-- 	   </div>   -->
<!-- 		<br> <br> -->
		
	</s:form> 
    
<br /><br />       
 
</body>
</html>