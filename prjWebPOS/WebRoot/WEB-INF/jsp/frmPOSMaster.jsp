<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>POS MASTER</title>
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
	$(document).ready(function() {

		 $('input#txtPOSCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtPOSName').mlKeyboard({layout: 'en_US'});
		  $('input#txtPropertyPOSCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtVatNo').mlKeyboard({layout: 'en_US'});
		  $('input#txtPOSCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtAreaName').mlKeyboard({layout: 'en_US'});
		
		$(".tab_content").hide();
		$(".tab_content:first").show();

		$("ul.tabs li").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		  $("form").submit(function(event){
			  if($("#txtPOSName").val().trim()=="")
				{
					alert("Please Enter POS Name");
					return false;
				}
			
			});
	});
</script>
<script type="text/javascript">

	var fieldName;

	/**
	* Reset The Group Name TextField
	**/
	function funResetFields()
	{
		$("#txtPOSCode").focus();
		
    }
	
	
		/**
		* Open Help
		**/
		function funHelp(transactionName)
		{
			fieldName=transactionName;
	       	window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
		
		/**
		* Open Help For Web Book Account Master
		**/
		function funOpenWebBooksAccSearch(transactionName)
		{
			fieldName=transactionName;
	       	window.open("searchform.html?formname=WebBooksAcountMaster&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
		
		/**
		* Get and Set data from help file and load data Based on Selection Passing Value(Group Code)
		**/
		

		
		function funSetData(code)
		{
			switch(fieldName)
			{
				case 'POSMaster' : 
					funSetPOSData(code);
					break;
				case 'RoundOFF' : 
					$("#txtRoundOff").val(code);
					break;
				case 'Tip' : 
					$("#txtTip").val(code);
					break;
				case 'Discount' : 
					$("#txtDiscount").val(code);
					break;
				case 'WSLocationCode' : 
					$("#txtWSLocationCode").val(code);
					break;
				case 'ExciseLicenseMaster' : 
					$("#txtExciseLicenceCode").val(code);
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
		
		/**
		 * Ready Function for Time
		 */
			function funBtnAddOnClick() 
				{
					
					
						if(($("#cmbHH").val()=="HH") || ($("#cmbMM").val()=="MM"))
					    {
							alert("Please select From Time");
					   		
					       	return false;
						}
						else if(($("#cmbToHH").val()=="HH") || ($("#cmbToMM").val()=="MM"))
					    {
							alert("Please select To Time");
					   		
					       	return false;
						}
						else
						{
							funFillTblTime();
						}
					
				}
		
		function funSetPOSData(code)
		{
			funResetFields();
			$("#txtPOSCode").val(code);
			var searchurl=getContextPath()+"/loadPOSMasterData.html?posCode="+code;		
			 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        async: false,
			        success: function(response)
			        {
			        	if(response.strPosCode=='Invalid Code')
			        	{
			        		alert("Invalid Group Code");
			        		$("#txtPOSCode").val('');
			        	}
			        	else
			        	{
				        	$("#txtPOSName").val(response.strPosName);
				        	$("#cmbStyleOfOp").val(response.strPosType);
				        	$("#cmbDebitCardTrans").val(response.strDebitCardTransactionYN);
				        	$("#txtPropertyPOSCode").val(response.strPropertyPOSCode);
				        	
				        	if(response.strCounterWiseBilling=='Y')
			        		{
				        		$("#chkCountryWiseBilling").prop('checked',true);
			        		}
				        	
				        	if(response.strDelayedSettlementForDB=='Y')
			        		{
				        		$("#chkDelayedSettlement").prop('checked',true);
			        		}
				        	$("#cmbBillPrinterName").val(response.strBillPrinterPort);
				        	$("#cmbAdvRecPrinterName").val(response.strAdvReceiptPrinterPort);
				        	
				        	if(response.strOperationalYN=='Y')
			        		{
				        		$("#chkOperational").prop('checked',true);
			        		}
				        	
				        	if(response.strPrintVatNo=='Y')
			        		{
				        		$("#chkPrintVatNo").prop('checked',true);
			        		}
				        	$("#txtVatNo").val(response.strVatNo);
				        	if(response.strPrintServiceTaxNo=='Y')
			        		{
				        		$("#chkPrintTaxNo").prop('checked',true);
			        		}
				        	
				        	$("#txtTaxNo").val(response.strServiceTaxNo);
				        	
				        	$("#txtPOSName").focus();
				        	
				        	
				        	$("#txtRoundOff").val(response.strRoundOff);
				        	$("#txtTip").val(response.strTip);
				        	$("#txtDiscount").val(response.strDiscount);
				        	$("#txtWSLocationCode").val(response.strWSLocationCode);
				        	$("#txtExciseLicenceCode").val(response.strExciseLicenceCode);
				        	if(response.strPlayZonePOS=='Y')
			        		{
				        		$("#chkPlayZonePOS").prop('checked',true);
			        		}
				        	
				        	funFillSettlement(response.listSettlementDtl);
					    	funRemoveTableRows("tblTime");
					    	var table = document.getElementById("tblTime");
					    	$.each(response.listReorderTime, function(i,item)
							{
							    var row = table.insertRow(i);
							    row.insertCell(0).innerHTML= "<input class=\"Box\" name=\"listReorderTime["+(i)+"].tmeFromTime\" size=\"25%\"  id=\"txtFromTime."+(i)+"\" value='"+item.tmeFromTime+"' >";
							    row.insertCell(1).innerHTML= "<input class=\"Box\" name=\"listReorderTime["+(i)+"].tmeToTime\" size=\"25%\"  id=\"txtToTime."+(i)+"\" value='"+item.tmeToTime+"'>"; 
							   
						  	});
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
		
	
		
		function funFillSettlementGrid(strSettlementCode,strSettlementDesc,list)
		{
			var flag=false;
			var table = document.getElementById("tblSettlement");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

			row.insertCell(0).innerHTML= "<input name=\"listSettlementDtl["+(rowCount)+"].strSettlementCode\" readonly=\"readonly\" class=\"Box \" size=\"28%\" id=\"txtSettlementCode."+(rowCount)+"\" value='"+strSettlementCode+"'>";
			row.insertCell(1).innerHTML= "<input name=\"listSettlementDtl["+(rowCount)+"].strSettlementDesc\" readonly=\"readonly\" class=\"Box \" size=\"33%\" id=\"txtSettlementDesc."+(rowCount)+"\" value='"+strSettlementDesc+"'>";
				
			if(null!=list)
			{
				$.each(list,function(i,item)
				{
					if(item.strSettlementCode==strSettlementCode)
				    {
						flag=true;
					    row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listSettlementDtl["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
			        }  
		        });
			}
			
			if(!flag)
	      	{
				row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listSettlementDtl["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
	      	}
		}
		
		function funLoadSettlementData()
		{
			var searchurl=getContextPath()+"/LoadSettlmentData.html";
			$.ajax({
				type: "GET",
				url: searchurl,
				dataType: "json",
				        
				success: function (response) {
					funRemoveTableRows("tblSettlement");	
				    $.each(response,function(i,item){
				    	funFillSettlementGrid(item.strSettlementCode,item.strSettlementDesc,null);
				    });
				},
				error: function(jqXHR, exception)
				{
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
		
		function funFillSettlement(list)
		{
			var searchurl=getContextPath()+"/LoadSettlmentData.html";
			 $.ajax({
				type: "GET",
				url: searchurl,
				dataType: "json",
				        
				success: function (response) {
					funRemoveTableRows("tblSettlement");
				           
				    $.each(response,function(i,item){
				    	funFillSettlementGrid(item.strSettlementCode,item.strSettlementDesc,list);
				    });
				    
				},
				error: function(jqXHR, exception)
				{
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

		function funFillTblTime() 
		{
			var HH = $("#cmbHH").val();
			var MM=$("#cmbMM").val();
		    var AMPM = $("#cmbAMPM").val();
		    var fromTime=HH + ":" + MM + " "+ AMPM; 
	
		    var toHH = $("#cmbToHH").val();
			var toMM=$("#cmbToMM").val();
		    var toAMPM = $("#cmbToAMPM").val();
		    var toTime=toHH + ":" + toMM + " "+ toAMPM; 
		
		    if(funCalculateTimeDifference())		    	
		    {
			    if(funDuplicateRow(fromTime,toTime))
			    {
			        var table = document.getElementById("tblTime");
				    var rowCount = table.rows.length;
				    var row = table.insertRow(rowCount);	
			    
			    	row.insertCell(0).innerHTML= "<input class=\"Box\" name=\"listReorderTime["+(rowCount)+"].tmeFromTime\" size=\"50%\"  id=\"txtFromTime."+(rowCount)+"\" value='"+fromTime+"' >";
			    	row.insertCell(1).innerHTML= "<input class=\"Box\" name=\"listReorderTime["+(rowCount)+"].tmeToTime\" size=\"50%\"  id=\"txtToTime."+(rowCount)+"\" value='"+toTime+"'>";
			    }
		    }
		}
		

		
		function funCalculateTimeDifference()
		{
			var HH = parseInt($("#cmbHH").val());
			var MM=parseInt($("#cmbMM").val());
		    var AMPM = $("#cmbAMPM").val();
		   	if(AMPM=="PM")
				HH=HH+12;
	
		    var toHH = parseInt($("#cmbToHH").val());
			var toMM=parseInt($("#cmbToMM").val());
		    var toAMPM = $("#cmbToAMPM").val();
		    if(toAMPM=="PM")
				toHH=toHH+12;
		  	if(toHH<HH)
		  	{ 
				alert("from Time must be less than To Time..!! ");
				return false;
		  	}
		  	else if(toHH==HH)
		 	{
				if(toMM<=MM)
			  	{
					alert("from Time must be less than To Time..!! ");
			    	return false;
			  	}
			  	else 
					return true;
			}
		  	else
				return true;
		}
		
		
		function funDuplicateRow(fromTime,toTime)
		{
		    var table = document.getElementById("tblTime");
		    var rowCount = table.rows.length;
		    var flag=true;
		    if(rowCount > 0)
	    	{
			    $('#tblTime tr').each(function()
			    {
				    if(fromTime==$(this).find('input').val())// `this` is TR DOM element
    				{
				    	if(toTime==$(this).find('input').val())// `this` is TR DOM element
	    				{
				    		 alert("Already added ");
						    	
		    				flag=false;
	    				}
	    				
    				}
				});
		    }
		    return flag;
		}
		
		function funCallFormAction() 
		{
			var flg=true;
			
			var name = $('#txtPOSName').val();
			var code= $('#txtPOSCode').val();
			
				 $.ajax({
				        type: "GET",
				        url: getContextPath()+"/checkPOSName.html?name="+name+"&code="+code,
				        async: false,
				        dataType: "text",
				        success: function(response)
				        {
				        	if(response=="false")
				        		{
				        			alert("POS Name Already Exist!");
				        			$('#txtPOSName').focus();
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
			
			return flg;
		}
</script>


</head>

<body onload="funLoadSettlementData()">

	<div id="formHeading">
		<label>POS Master</label>
	</div>
	
	<s:form name="POSForm" method="POST" action="savePOSMaster.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<div style="margin-left: 20%;">
		
			<div id="tab_container" style="height: 80%; overflow: hidden;" >
					<ul class="tabs">
								<li class="active" data-state="tab1" style="width: 20%; padding-left: 5%; height: 25px; border-radius: 4px;">Generals</li>
								<li data-state="tab2" style="width: 20%; padding-left: 4%; height: 25px; border-radius: 4px;">Settlement</li>
								<li data-state="tab3" style="width: 20%; padding-left: 3%; height: 25px; border-radius: 4px;">ReOrder Time</li>
								<li data-state="tab4" style="width: 20%; padding-left: 6%; height: 25px; border-radius: 4px;">Link Up</li>
				
					</ul>
							<br /> <br />

		<!--  Start of Generals tab1-->

			<div id="tab1" class="tab_content">
					
					<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">POS Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtPOSCode" path="strPosCode"  ondblclick="funHelp('POSMaster')"/>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">POS Name</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtPOSName" path="strPosName" />
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Style of Operation</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:select id="cmbStyleOfOp" path="strPosType" >
								<option value="Direct Biller">Direct Biller</option>
						    	<option value="Dina">Dina</option>
						    </s:select>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Debit Card Transaction</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:select id="cmbDebitCardTrans" path="strDebitCardTransactionYN" >
								<option value="No">No</option>
						    	<option value="Yes">Yes</option>
						    </s:select>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Property POS Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtPropertyPOSCode" path="strPropertyPOSCode" />
						</div>
			 		</div>
					
					<div class="row" style="background-color: #fff; display: inline-flex; margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%; margin-left: 12px;">
							<label class="title" style="width: 100%">Counter Wise Billing</label>
						</div>
		    			<div class="element-input col-lg-6" style="width: 8%;">
		    				<s:input type="checkbox"  id="chkCountryWiseBilling" path="strCounterWiseBilling"></s:input>
						</div>
						<div class="element-input col-lg-6" style="width: 25%;">
							<label class="title" style="width: 100%">Delayed Settlement</label>
						</div>
						<div class="element-input col-lg-6" style="width: 8%;">
		    				<s:input type="checkbox"  id="chkDelayedSettlement" path="strDelayedSettlementForDB"></s:input>
						</div> 
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box;">
					 	<div class="element-input col-lg-6" style="width:  19.5%; margin-left: 15px;"> 
		    				<label class="title">Bill Printer Name</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%; margin-bottom: 10px;" > 
		    				<s:select id="cmbBillPrinterName" items="${printerList}" path="strBillPrinterPort" style="height: 10%; width: 100%;"/>
		    			</div>
		    			<div class="submit col-lg-12">
		    				<input id="primaryPrinter" type="button" value="TEST" />
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff; display: -webkit-box;">
					 	<div class="element-input col-lg-6" style="width:  19.5%; margin-left: 15px;"> 
		    				<label class="title">Adv. Receipt Printer Name</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 20%; margin-bottom: 10px;" > 
		    				<s:select id="cmbAdvRecPrinterName" items="${printerList}" path="strAdvReceiptPrinterPort" style="height: 10%; width: 100%;"/>
		    			</div>
		    			<div class="submit col-lg-12">
		    				<input id="primaryPrinter" type="button" value="TEST" />
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff; display: inline-flex;">
						<div class="element-input col-lg-6" style="width: 20%; margin-left: 14px;">
							<label class="title" style="width: 100%">Operational</label>
						</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<s:input type="checkbox"  id="chkOperational" path="strOperationalYN" value="Yes"></s:input>
						</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: inline-flex;">
						<div class="element-input col-lg-6" style="width: 20%; margin-left: 14px;">
							<label class="title" style="width: 100%">Print VAT No</label>
						</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<s:input type="checkbox"  id="chkPrintVatNo" path="strPrintVatNo" value="Yes"></s:input>
						</div>
						<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtVatNo" path="strVatNo" />
						</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: inline-flex;">
						<div class="element-input col-lg-6" style="width: 22%;">
							<label class="title" style="width: 100%">Print Service Tax No</label>
						</div>
		    			<div class="element-input col-lg-6" style="width: 15%;">
		    				<s:input type="checkbox"  id="chkPrintTaxNo" path="strPrintServiceTaxNo" value="Yes"></s:input>
						</div>
						<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtTaxNo" path="strServiceTaxNo" />
						</div>
					</div>
					<div class="row" style="background-color: #fff; display: inline-flex;">
						<div class="element-input col-lg-6" style="width: 20%; margin-left: 12px;">
							<label class="title" style="width: 100%">Play Zone POS</label>
						</div>
		    			<div class="element-input col-lg-6" style="width: 8%;">
		    				<s:input type="checkbox"  id="chkPlayZonePOS" path="strPlayZonePOS"></s:input>
						</div>
					</div>	
				<br/>
			</div>
			
			
							<!--  End of  Generals tab-->


							<!-- Start of Settlement tab -->

	<div id="tab2" class="tab_content">
		
		<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
		 
			 <div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 80%;">
			 	
			 		<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;">
			 				<thead>
			        					<tr> 
			            					<th style="width:40%;">SettleMent Code</th>
			            					<th style="width:45%;">SettleMent Name</th>
			            					<th>Applicable</th>
			        					</tr>	
			    			</thead>
			 		</table>
			 	
			 		<table id="tblSettlement" style="width: 100%;border: 1px solid #ccc;">
	    					<tbody style="border-top: none">				
						
							</tbody>
	    			</table>
			 	
			 </div>
			 
		</div>
		
		<br/>
		
	</div>
	
							<!-- End of Settlement tab -->


							<!-- Start of ReOrder Time Tab -->

	
	<div id="tab3" class="tab_content">
	
			<div class="row" style="background-color: #fff; display: inline-flex;">
					<div class="element-input col-lg-6"  style="width: 20%;"> 
	    					<label class="title">From Time</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbHH" path=""  >
								<option value="HH">HH</option>
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option> 
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
								<option value="11">11</option>
								<option value="12">12</option>
						    </s:select>
					</div>
					<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbMM" path=""  >
								<option value="MM">MM</option><option value="00">00</option><option value="01">01</option>
								<option value="02">02</option><option value="03">03</option><option value="04">04</option>
								<option value="05">05</option><option value="06">06</option><option value="07">07</option> 
								<option value="08">08</option><option value="09">09</option><option value="10">10</option>
								<option value="11">11</option><option value="12">12</option><option value="13">13</option>
								<option value="14">14</option><option value="15">15</option><option value="16">16</option>
								<option value="17">17</option><option value="18">18</option><option value="19">19</option>
								<option value="20">20</option><option value="21">21</option><option value="22">22</option>
								<option value="23">23</option><option value="24">24</option><option value="25">25</option>
								<option value="26">26</option><option value="27">27</option><option value="28">28</option>
								<option value="29">29</option><option value="30">30</option><option value="31">31</option>
								<option value="32">32</option><option value="33">33</option><option value="34">34</option>
								<option value="35">35</option><option value="36">36</option><option value="37">37</option>
								<option value="38">38</option><option value="39">39</option><option value="41">41</option>
								<option value="42">42</option><option value="43">43</option><option value="44">44</option>
								<option value="45">45</option><option value="46">46</option><option value="47">47</option>
								<option value="48">48</option><option value="49">49</option><option value="50">50</option>
								<option value="51">51</option><option value="52">52</option><option value="53">53</option>
								<option value="54">54</option><option value="55">55</option><option value="56">56</option>
								<option value="57">57</option><option value="58">58</option><option value="59">59</option>
						    </s:select>
					</div>
					<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 15%;"> 
							<s:select id="cmbAMPM" path="" >
								<option value="AM">AM</option>
			 					<option value="PM">PM</option>
							</s:select>
					</div>
				</div>	
				
				<div class="row" style="background-color: #fff; display: inline-flex;">
					
					<div class="element-input col-lg-6" style="width: 20%;"> 
	    					<label class="title">To Time</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbToHH" path="" >
								<option value="HH">HH</option>
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option> 
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
								<option value="11">11</option>
								<option value="12">12</option>
							</s:select>
					</div>
					<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbToMM" path="" >
								<option value="MM">MM</option><option value="00">00</option><option value="01">10</option>
								<option value="02">02</option><option value="03">03</option><option value="04">04</option>
								<option value="05">05</option><option value="06">06</option><option value="07">07</option> 
								<option value="08">08</option><option value="09">09</option><option value="10">10</option>
								<option value="11">11</option><option value="12">12</option><option value="13">13</option>
								<option value="14">14</option><option value="15">15</option><option value="16">16</option>
								<option value="17">17</option><option value="18">18</option><option value="19">19</option>
								<option value="20">20</option><option value="21">21</option><option value="22">22</option>
								<option value="23">23</option><option value="24">24</option><option value="25">25</option>
								<option value="26">26</option><option value="27">27</option><option value="28">28</option>
								<option value="29">29</option><option value="30">30</option><option value="31">31</option>
								<option value="32">32</option><option value="33">33</option><option value="34">34</option>
								<option value="35">35</option><option value="36">36</option><option value="37">37</option>
								<option value="38">38</option><option value="39">39</option><option value="41">41</option>
								<option value="42">42</option><option value="43">43</option><option value="44">44</option>
								<option value="45">45</option><option value="46">46</option><option value="47">47</option>
								<option value="48">48</option><option value="49">49</option><option value="50">50</option>
								<option value="51">51</option><option value="52">52</option><option value="53">53</option>
								<option value="54">54</option><option value="55">55</option><option value="56">56</option>
								<option value="57">57</option><option value="58">58</option><option value="59">59</option>
							</s:select>
					</div>
					<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 15%;"> 
							<s:select id="cmbToAMPM" path="" >
								<option value="AM">AM</option>
			 					<option value="PM">PM</option>
							</s:select>
					</div>
			 </div>
			 
			 <br/>
			 
			 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%; margin-bottom: 10px;margin-left: 55px;">
			 			<div class="submit col-lg-4 col-sm-4 col-xs-4">
							<input type="Button" value="Add" id="btnAdd" onclick="return funBtnAddOnClick();" style="width:100px;"/>
						</div>
						<div class="submit col-lg-4 col-sm-4 col-xs-4">
							<input type="reset" value="Reset" onclick="funResetFields()"/>
						</div>
			 </div>
	  <br/>
	  
	  		<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
		 
				 <div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 80%;">
				 	
				 		<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;">
				 				<thead>
			        					<tr> 
			            					<th style="width: 50%;">From Time</th>
			            					<th>To Time</th>
			        					</tr>	
			    				</thead>
				 		</table>
				 	
				 		<table id="tblTime" style="width: 100%;border: 1px solid #ccc;">
		    					<tbody style="border-top: none">				
							
								</tbody>
		    			</table>
				 	
				 </div>
			 
			</div>
	  

			
		<br/>
	</div>
	
	
			<!-- End  of ReOrder Time Tab -->


			<!-- Start of Link Up Tab -->
			
							
			<div id="tab4" class="tab_content">
							
					<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Round Off</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtRoundOff" path="strRoundOff"  ondblclick="funHelp('RoundOFF')"/>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Tip</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtTip" path="strTip"  ondblclick="funHelp('Tip')"/>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Discount</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtDiscount" path="strDiscount"  ondblclick="funHelp('Discount')"/>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Enter WebStock Location Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtWSLocationCode" path="strWSLocationCode" ondblclick="funHelp('WSLocationCode')"/>
						</div>
			 		</div>
			 		
			 		<div class="row" style="background-color: #fff;">
						<div class="element-input col-lg-6" > 
	    					<label class="title">Enter Excise Licence Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
							<s:input class="large" colspan="3" type="text" id="txtExciseLicenceCode" path="strExciseLicenceCode"  ondblclick="funHelp('ExciseLicenseMaster')"/>
						</div>
			 		</div>
	   </div>
	
	
	   <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 55px;">
     			<p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="SUBMIT"/></div>
          
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" onclick="funResetFields()"></div>
     			</p>
   	  </div>
   	  
   	  <br/>
   	  
   	</div>
   	
   </div>
   	
</s:form>

</body>
</html>