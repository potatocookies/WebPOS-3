<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
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
</style>
<script type="text/javascript">
	$(document).ready(function() {
		
			$('input#txtTaxDesc').mlKeyboard({layout: 'en_US'});
		  	$('input#txtTaxShortName').mlKeyboard({layout: 'en_US'});
		 	$('input#txtAmount').mlKeyboard({layout: 'en_US'});
		  	$('input#txtPercent').mlKeyboard({layout: 'en_US'});
		
		  	$("#txtdteValidFrom").datepicker({ dateFormat: 'yy-mm-dd' });
			$("#txtdteValidFrom" ).datepicker('setDate', 'today');
			$("#txtdteValidFrom").datepicker();
			
	        $("#txtdteValidTo").datepicker({ dateFormat: 'yy-mm-dd' });
	        $("#txtdteValidTo" ).datepicker('setDate', 'today');
	        $("#txtdteValidTo").datepicker();
	            

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
			  if($("#txtTaxDesc").val().trim()=="")
				{
					alert("Please Enter tax description.");
					return false;
				}
			  if($("#txtTaxDesc").val().length > 30)
				{
					alert("Tax Description length must be less than 30.");
					return false;
				}
			  if($("#txtTaxShortName").val().length > 20)
				{
					alert("Tax Description length must be less than 20.");
					return false;
				}
			 
			  if(CalculateDateDiff())
				{
					return false;
				}
			  else{
				var flag=funChekTable();
				return flag;
			  }
			});
	});
	
	
	function CalculateDateDiff() {
		var fromDate = $("#txtdteValidFrom").val();
		var toDate = $("#txtdteValidTo").val();


		var frmDate= fromDate.split('-');
	    var fDate = new Date(frmDate[0],frmDate[1],frmDate[2]);
	    
	    var tDate= toDate.split('-');
	    var t1Date = new Date(tDate[0],tDate[1],tDate[2]);

    	var dateDiff=t1Date-fDate;
  		 if (dateDiff >= 0 ) 
  		 {
         	return false;
         }else{
        	 alert("Invalid date");
        	 return true;
         }
	}


var field;


	/**
	* Reset The Group Name TextField
	**/
	function funResetFields()
	{

		location.reload(true); 
    }
	function funChekTable()
	{
		var flag=false;
		
		
		
		
		  $('#tblPOS tr').each(function() {
			  var checkbox = $(this).find("input[type='checkbox']");

			    if( checkbox.prop("checked") ){
			    	 flag= true;
			    } 
				 });
		  
		  if(!flag)
			  {
			  alert("Please select atleast POS Code");
			  return flag;
			  }
		  
		  
		  flag=funCheckSettlement();
		  if(flag)
			  flag=funCheckGroup();
		  return flag;
	}
	function funCheckSettlement()
	{
		 var flag=false;
		  $('#tblSettlement tr').each(function() {
			  var checkbox = $(this).find("input[type='checkbox']");
			  if( checkbox.prop("checked") ){
			    	 flag= true;
			    }
				 });
		  
		  if(!flag)
			  {
				 alert("Please select atleast one Settlement Code");
				 return flag;
			  }
		  return flag;
	}
	
	function funCheckGroup()
	{
		 var flag=false;
		  $('#tblGroup tr').each(function() {
			  var checkbox = $(this).find("input[type='checkbox']");
			  if( checkbox.prop("checked") ){
			    	 flag= true;
			    }
				 });
		  
		  if(!flag)
			  {
				 alert("Please select atleast one Group Code");
				 return flag;
			  }
		  return flag;
	}
		/**
		* Open Help
		**/
		function funHelp(transactionName)
		{	   field= transactionName.split(".") ;
		 
	       // window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	       window.open("searchform.html?formname="+field[0]+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
		
		
		function funSetData(code)
		{
			switch(field[0])
			{
		case 'POSTaxMaster' : 
			funSetTaxData(code);
			break;
		case 'b' : 
			$("#txtAccountCode").val(code);
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
		
		function funSetTaxData(code){

			$("#txtTaxCode").val(code);
			var searchurl=getContextPath()+"/loadPOSTaxMasterData.html?taxCode="+code;		
			 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strTaxCode=='Invalid Code')
			        	{
			        		alert("Invalid Group Code");
			        		$("#txtTaxCode").val('');
			        	}
			        	else
			        	{
				        	$("#cmbTaxType").val(response.strTaxType);
				        	$("#txtTaxDesc").val(response.strTaxDesc);
				        	$("#txtTaxShortName").val(response.strTaxShortName);
				        	$("#txtAmount").val(response.dblAmount);
				        	$("#cmbTaxOnSP").val(response.strTaxOnSP);
				        	$("#txtPercent").val(response.dblPercent);
				        	$("#cmbTaxOnGD").val(response.strTaxOnGD);
				        	$("#cmbTaxCalculation").val(response.strTaxCalculation);
				        	if(response.strTaxRounded=='Y')
			        		{
				        		$("#chkTaxRounded").prop('checked',true);
			        		}
				        	
				        	$("#cmbTaxIndicator").val(response.strTaxIndicator);
				        	
				        	
				        	if(response.strTaxOnTax=='Y')
			        		{
				        		$("#chkTaxOnTax").prop('checked',true);
			        		}
				        	
				        	var dateTime=response.dteValidFrom
				        	var date=dateTime.split(" ");
				        	$("#txtdteValidFrom").val(date[0]);
				        	
				        	 dateTime=response.dteValidTo
				        	 date=dateTime.split(" ");
				        	$("#txtdteValidTo").val(date[0]);
				        	
				        	$("#cmbItemType").val(response.strItemType);
				        	if(response.strHomeDelivery=='Y')
			        		{
				        		$("#chkHomeDelivery").prop('checked',true);
			        		}
				        	if(response.strDinningInn=='Y')
			        		{
				        		$("#chkDinningInn").prop('checked',true);
			        		}
				        	if(response.strTakeAway=='Y')
			        		{
				        		$("#chkTakeAway").prop('checked',true);
			        		}
			        		$("#txtAccountCode").val(response.strAccountCode);
			        		
			        		$("#txtBillNote").val(response.strBillNote);
			        		
			        		//fill Settle Table
			        		funLoadSettlementDataForUpdate(response.listSettlementCode);
			        		
			        		//fill Group Table
			        		funLoadGroupDataForUpdate(response.listGroupCode);
			        		
				        	//fill POS table
				        
				        	funLoadPOSDataForUpdate(response.listPOSCode);
				        	
				        	
				        	//fill tax table
				        	
				        	funLoadTaxDataForUpdate(response.listTaxOnTaxCode);
				        	
				        	
				        	//fill Area table
				        	
				        	funLoadAreaDataForUpdate(response.listAreaCode);
				        	
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
		function funLoadTableData()
		{
			//funSetDate();
			document.getElementById("txtAmount").disabled = true;
			document.getElementById("cmbItemType").disabled = true;
			
			funLoadPOSData();
			funLoadSettlementData();
			funLoadGroupData();
			funLoadTaxData();
			funLoadAreaData();
			
		}
		
		function funLoadPOSData()
		{

			var searchurl=getContextPath()+"/loadPOSData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblPOS");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillPOSDetail(item.strPosCode,item.strPosName);
				            				            				            	
					            	 
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
		function funLoadPOSDataForUpdate(list)
		{

			var searchurl=getContextPath()+"/loadPOSData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblPOS");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillPOSDetailForUpdate(item.strPosCode,item.strPosName,list);
				            				            				            	
					            	 
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
		function funfillPOSDetail(strPOSCode,strPOSName)
		{
			var table = document.getElementById("tblPOS");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			  
			 row.insertCell(0).innerHTML= "<input type=\"hidden\" name=\"listPOSCode["+(rowCount)+"].strPosCode\" size=\"0%\" readonly=\"readonly\" class=\"Box \" id=\"txtPosCode."+(rowCount)+"\" value='"+strPOSCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listPOSCode["+(rowCount)+"].strPosName\" size=\"50%\" readonly=\"readonly\" class=\"Box \"  id=\"txtPosName."+(rowCount)+"\" value='"+strPOSName+"'>";
		      row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listPOSCode["+(rowCount)+"].strApplicableYN\" size=\"50%\" id=\"chkPosApplicable."+(rowCount)+"\" value='"+true+"'>";
			  

		}
		
		function funfillPOSDetailForUpdate(strPOSCode,strPOSName,list)
		{
			var flag=false;
		
			var table = document.getElementById("tblPOS");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			  
			 row.insertCell(0).innerHTML= "<input type=\"hidden\" name=\"listPOSCode["+(rowCount)+"].strPosCode\" size=\"0%\" readonly=\"readonly\" class=\"Box \" id=\"txtPosCode."+(rowCount)+"\" value='"+strPOSCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listPOSCode["+(rowCount)+"].strPosName\" size=\"50%\" readonly=\"readonly\" class=\"Box \"  id=\"txtPosName."+(rowCount)+"\" value='"+strPOSName+"'>";
		    
			  
		      $.each(list,function(i,item){
	          		if(item.strPosCode==strPOSCode)
		        		{
			        	flag=true;
			        	  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listPOSCode["+(rowCount)+"].strApplicableYN\" size=\"50%\" id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
	          			
		        		
		        		}		            				            	
		           
	          	});
			
			  if(!flag)
	      		{
				  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listPOSCode["+(rowCount)+"].strApplicableYN\" size=\"50%\" id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
	      		}
		}
		function funLoadSettlementData()
		{

			var searchurl=getContextPath()+"/loadSettlmentData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblSettlement");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillSettlementDetail(item.strSettlementCode,item.strSettlementDesc);
				            				            				            	
					            	 
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

		function funLoadSettlementDataForUpdate(list)
		{

			var searchurl=getContextPath()+"/loadSettlmentData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblSettlement");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillSettlementDetailForUpdate(item.strSettlementCode,item.strSettlementDesc,list);
				            				            				            	
					            	 
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
		function funfillSettlementDetail(strSettlementCode,strSettlementDesc)
		{
			var table = document.getElementById("tblSettlement");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listSettlementCode["+(rowCount)+"].strSettlementCode\" readonly=\"readonly\" class=\"Box \" size=\"10%\" id=\"txtSettlementCode."+(rowCount)+"\" value='"+strSettlementCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listSettlementCode["+(rowCount)+"].strSettlementDesc\" readonly=\"readonly\" class=\"Box \" size=\"20%\" id=\"txtSettlementDesc."+(rowCount)+"\" value='"+strSettlementDesc+"'>";
		      row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listSettlementCode["+(rowCount)+"].strApplicableYN\" size=\"10%\" id=\"chkSettleApplicable."+(rowCount)+"\" value='"+true+"'>";

		}
		
		function funfillSettlementDetailForUpdate(strSettlementCode,strSettlementDesc,list)
		{
			var flag=false;
			var table = document.getElementById("tblSettlement");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listSettlementCode["+(rowCount)+"].strSettlementCode\" readonly=\"readonly\" class=\"Box \" id=\"txtSettlementCode."+(rowCount)+"\" value='"+strSettlementCode+"'>";
			  row.insertCell(1).innerHTML= "<input name=\"listSettlementCode["+(rowCount)+"].strSettlementDesc\" readonly=\"readonly\" class=\"Box \" id=\"txtSettlementDesc."+(rowCount)+"\" value='"+strSettlementDesc+"'>";
			  $.each(list,function(i,item){
	          		if(item.strSettlementCode==strSettlementCode)
		        		{
			        	flag=true;
			        	  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listSettlementCode["+(rowCount)+"].strApplicableYN\"  id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
	          			
		        		
		        		}		            				            	
		           
	          	});
			
			  if(!flag)
	      		{
				  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listSettlementCode["+(rowCount)+"].strApplicableYN\"  id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
	      		}
		}
		function funLoadGroupData()
		{

			var searchurl=getContextPath()+"/loadGroupData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblGroup");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillGroupDetail(item.strGroupCode,item.strGroupName);
					            	 
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

		function funLoadGroupDataForUpdate(list)
		{

			var searchurl=getContextPath()+"/loadGroupData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblGroup");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillGroupDetailForUpdate(item.strGroupCode,item.strGroupName,list);
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
		function funfillGroupDetail(strGroupCode,strGroupName)
		{
			var table = document.getElementById("tblGroup");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listGroupCode["+(rowCount)+"].strGroupCode\" readonly=\"readonly\" class=\"Box \" id=\"strGroupCode."+(rowCount)+"\" value='"+strGroupCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listGroupCode["+(rowCount)+"].strGroupName\" readonly=\"readonly\" class=\"Box \" id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName+"'>";
		      row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listGroupCode["+(rowCount)+"].strApplicableYN\" id=\"chkSettleApplicable."+(rowCount)+"\" value='"+true+"'>";

		}
		
		function funfillGroupDetailForUpdate(strGroupCode,strGroupName,list)
		{
			var flag=false;
			var table = document.getElementById("tblGroup");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listGroupCode["+(rowCount)+"].strGroupCode\" readonly=\"readonly\" class=\"Box \" id=\"strGroupCode."+(rowCount)+"\" value='"+strGroupCode+"'>";
			  row.insertCell(1).innerHTML= "<input name=\"listGroupCode["+(rowCount)+"].strGroupName\" readonly=\"readonly\" class=\"Box \" id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName+"'>";
			  $.each(list,function(i,item){
	          		if(item.strGroupCode==strGroupCode)
		        		{
			        	flag=true;
			        	  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listGroupCode["+(rowCount)+"].strApplicableYN\" id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
	          			
		        		
		        		}		            				            	
		           
	          	});
			
			  if(!flag)
	      		{
				  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listGroupCode["+(rowCount)+"].strApplicableYN\" id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
	      		}
		}
		function funLoadTaxData()
		{

			var searchurl=getContextPath()+"/loadTaxData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblTax");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillTaxDetail(item.strTaxCode,item.strTaxDesc);
				            				            				            	
					            	 
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
		function funLoadTaxDataForUpdate(list)
		{

			var searchurl=getContextPath()+"/loadTaxData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblTax");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillTaxDetailForUpdate(item.strTaxCode,item.strTaxDesc,list);
				            				            				            	
					            	 
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
		
		function funfillTaxDetail(strTaxCode,strTaxDesc)
		{
			var table = document.getElementById("tblTax");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listTaxOnTaxCode["+(rowCount)+"].strTaxCode\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtTaxCode."+(rowCount)+"\" value='"+strTaxCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listTaxOnTaxCode["+(rowCount)+"].strTaxDesc\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtTaxDesc."+(rowCount)+"\" value='"+strTaxDesc+"'>";
		      row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listTaxOnTaxCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkTaxApplicable."+(rowCount)+"\" value='"+true+"'>";

		}
		function funfillTaxDetailForUpdate(strTaxCode,strTaxDesc,list)
		{
			var flag=false;
		
			var table = document.getElementById("tblTax");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listTaxOnTaxCode["+(rowCount)+"].strTaxCode\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtTaxCode."+(rowCount)+"\" value='"+strTaxCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listTaxOnTaxCode["+(rowCount)+"].strTaxDesc\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtTaxDesc."+(rowCount)+"\" value='"+strTaxDesc+"'>";
		      

		      $.each(list,function(i,item){
	          		if(item.strTaxCode==strTaxCode)
		        		{
			        	flag=true;
			        	  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listTaxOnTaxCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
	          			
		        		
		        		}		            				            	
		           
	          	});
			
			  if(!flag)
	      		{
				  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listTaxOnTaxCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
	      		}
		}
		function funLoadAreaData()
		{

			var searchurl=getContextPath()+"/loadAreaData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblArea");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillAreaDetail(item.strAreaCode,item.strAreaName);
				            				            				            	
					            	 
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
		function funLoadAreaDataForUpdate(list)
		{

			var searchurl=getContextPath()+"/loadAreaData.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	funRemoveTableRows("tblArea");
				           // for (var i in response){		            	
				            	$.each(response,function(i,item){
				            	
				            		funfillAreaDetailForUpdate(item.strAreaCode,item.strAreaName,list);
				            				            				            	
					            	 
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
		function funfillAreaDetail(strAreaCode,strAreaName)
		{
			var table = document.getElementById("tblArea");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listAreaCode["+(rowCount)+"].strAreaCode\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtAreaCode."+(rowCount)+"\" value='"+strAreaCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listAreaCode["+(rowCount)+"].strAreaName\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtAreaName."+(rowCount)+"\" value='"+strAreaName+"'>";
		      row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listAreaCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkAreaApplicable."+(rowCount)+"\" value='"+true+"'>";

		}
		function funfillAreaDetailForUpdate(strAreaCode,strAreaName,list)
		{
			var flag=false;
		
			var table = document.getElementById("tblArea");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

		      row.insertCell(0).innerHTML= "<input name=\"listAreaCode["+(rowCount)+"].strAreaCode\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtAreaCode."+(rowCount)+"\" value='"+strAreaCode+"'>";
		      row.insertCell(1).innerHTML= "<input name=\"listAreaCode["+(rowCount)+"].strAreaName\" readonly=\"readonly\" class=\"Box \" size=\"37.5%\" id=\"txtAreaName."+(rowCount)+"\" value='"+strAreaName+"'>";
		      $.each(list,function(i,item){
	          		if(item.strAreaCode==strAreaCode)
		        		{
			        	flag=true;
			        	  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listAreaCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" checked=\"checked\">";
	          			
		        		}		            				            	
		           
	          	});
			
			  if(!flag)
	      		{
				  row.insertCell(2).innerHTML= "<input type=\"checkbox\" name=\"listAreaCode["+(rowCount)+"].strApplicableYN\" size=\"25%\" id=\"chkApplicable."+(rowCount)+"\" value='"+true+"'>";
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
	function funCombo()
	{
		if(document.getElementById("cmbTaxType").value=="Fixed Amount")
		{	document.getElementById("txtAmount").disabled = false;
		    document.getElementById("txtPercent").disabled = true;
		}
		if(document.getElementById("cmbTaxType").value=="Percent")
		{	document.getElementById("txtAmount").disabled = true;
		    document.getElementById("txtPercent").disabled = false;
		}
		if(document.getElementById("cmbTaxIndicator").value!="")
		{	document.getElementById("cmbItemType").disabled = false;
		    
		}
		if(document.getElementById("cmbTaxIndicator").value=="")
		{	document.getElementById("cmbItemType").disabled = true;
			document.getElementById("cmbItemType").value="Both";
		}
		 
		if(document.getElementById("chkTaxOnTax").checked)
			{
			document.getElementById("tblTax").disabled = false;
			}
		
	}
		
</script>


</head>

<body onload="funLoadTableData()">
	<div id="formHeading">
		<label>Tax Master</label>
	</div>
	
	<s:form name="POSForm" method="POST" action="savePOSTaxMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

	<div class="title" style="margin-left: 15%">

		<div id="tab_container" style="height: 100%; overflow: hidden;" >
		
			<ul class="tabs">
						<li class="active" data-state="tab1" style="width: 15%; padding-left: 3%; height: 25px; border-radius: 4px;">Tax Details 1</li>
						<li data-state="tab2" style="width: 15%; padding-left: 3%; height: 25px; border-radius: 4px;">Tax Details 2</li>
						<li data-state="tab3" style="width: 15%; padding-left: 3%; height: 25px; border-radius: 4px;">Tax Details 3</li>
						<li data-state="tab4" style="width: 15%; padding-left: 3%; height: 25px; border-radius: 4px;">Linkup</li>
			</ul>
			
			<br/><br/>
			
<!--  Start of tab1-->
	
			<div id="tab1" class="tab_content">
			
				<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Tax Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 25%;"> 
							<s:input type="text" id="txtTaxCode" path="strTaxCode"  ondblclick="funHelp('POSTaxMaster')" style="width: 100%;" />
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Tax Type</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<s:select id="cmbTaxType" path="strTaxType" onclick="funCombo()">
	    						<option value="Percent">Percent</option>
				 				<option value="Fixed Amount">Fixed Amount</option>
	    					</s:select>
	    				</div>
				</div>
				
				<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Tax Description</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 25%;"> 
							<s:input type="text" id="txtTaxDesc" path="strTaxDesc" style="width: 100%;"/>
						</div>
				</div>
				
				<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Tax Short Name</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 25%;"> 
							<s:input class="large" colspan="3" type="text" id="txtTaxShortName" path="strTaxShortName" style="width: 100%;"/>
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Amount</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 20%;">
	    					<s:input type="text" id="txtAmount" path="dblAmount" style="text-align: right; width: 100%;" />
						</div>
				</div>
				
				<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Tax On S/P</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 25%;"> 
							<s:select id="cmbTaxOnSP" path="strTaxOnSP" >
								<option value="Sales">Sales</option>
				 				<option value="Purchase">Purchase</option>
							</s:select>
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">Percent</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 20%;"> 
							<s:input type="text" id="txtPercent" path="dblPercent" style="text-align: right; width: 100%;" />
						</div>
				</div>
				
				<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
		 
			 		<div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 60%;">

							<table style="width: 100%;background-color:  #85cdffe6;" >
		    					<thead>
									<tr>
										<td style="border: 1px solid #ccc;">POS Name</td>
										<td>Select</td>
									</tr>
								</thead>
						    </table>
				
							 <table id="tblPOS" style="width: 100%;">
			    				  <tbody style="border-top: none">
			    				  		
			    				  </tbody>
			    			 </table>
			
				     </div>
					
			    </div>
			   
			  <br/>
			  
			  <div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
								<label class="title">Valid From</label>
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" >
								<s:input id="txtdteValidFrom" path="dteValidFrom" style="width: 100%;"/>
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" >
								<label class="title">Valid To</label>
						</div>
						<div class="element-input col-lg-6" style="width: 20%;" >
								<s:input id="txtdteValidTo" path="dteValidTo" style="width: 100%;"/>
						</div>
				</div>
				<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
						<div class="element-input col-lg-6" style="width: 20%;" > 
							<label>Bill Note</label>
						</div>	
						<div class="element-input col-lg-6" style="width: 20%;" >
						<s:input type="text" id="txtBillNote" name="txtBillNote" path="strBillNote" />
				</div>		
				</div>
				
			</div>
			
<!--  End of tab1-->
		
<!--  Start of tab2-->

	<div id="tab2" class="tab_content">

			<div class="row" style="background-color: #fff;display: -webkit-box;">
					<div class="element-input col-lg-6" style="width: 15%;" > 
		    				<label class="title">Tax On G/D</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbTaxOnGD" path="strTaxOnGD" >
									<option value="Gross">Gross</option>
				 					<option value="Discount">Discount</option>
							</s:select>
					</div>
					<div class="element-input col-lg-6" style="width: 15%;" > 
		    				<label class="title">Tax Calculation</label>
		    				</div>
		    		<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;">
		    				<s:select id="cmbTaxCalculation" name="cmbTaxCalculation" path="strTaxCalculation" >
		    						<option value="Backward">Backward</option>
 				 					<option value="Forward">Forward</option>
		    				</s:select>
		    		</div>
		     </div>
		    
		    <div class="row" style="background-color: #fff;display: -webkit-box;">
		    		<div class="element-input col-lg-6" style="width: 15%;" > 
	    					<label class="title">Tax Rounded</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:input type="checkbox" id="chkTaxRounded" path="strTaxRounded" value="Yes"  />
						</div>
					<div class="element-input col-lg-6" style="width: 15%;" > 
		    				<label class="title">Tax Indicator</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:select id="cmbTaxIndicator" path="strTaxIndicator" onclick="funCombo()"  >
								   <option value=""></option><option value="A">A</option><option value="B">B</option><option value="C">C</option>
								   <option value="D">D</option><option value="E">E</option><option value="F">F</option><option value="G">G</option>
				 				   <option value="H">H</option><option value="I">I</option><option value="J">J</option><option value="K">K</option>
				 				   <option value="L">L</option><option value="M">M</option><option value="N">N</option><option value="O">O</option>
								   <option value="P">P</option><option value="Q">Q</option><option value="R">R</option><option value="S">S</option>
				 				   <option value="T">T</option><option value="U">U</option><option value="V">V</option><option value="W">W</option>
				 				   <option value="X">X</option><option value="Y">Y</option><option value="Z">Z</option>
							</s:select>
					</div>
			</div>
			
			 <div class="row" style="background-color: #fff;display: -webkit-box;">
		    			<div class="element-input col-lg-6" style="width: 15%;" > 
	    					<label class="title">Tax On Tax</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 20%;"> 
							<s:checkbox id="chkTaxOnTax" path="strTaxOnTax" value="Yes" onclick="funCombo()"  />
						</div>
			</div>
			
			 <div class="row" style="background-color: #fff;display: -webkit-box;">
		    			<div class="element-input col-lg-6" style="width: 20%;" > 
	    					<label class="title">TAX Applicable On</label>
	    				</div>
	    	</div>
	 

	    	<div class="row" style="background-color: #fff;display: -webkit-box;">
	    	
	    		
	    		<div class="element-input col-lg-6"> 
	    		
			    	<div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 100%;">
							<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;" >
				    				<thead>
											<tr>
												<td style="width:36.5%;border: 1px solid #ccc;"">Set Code</td>
												<td style="width:36.5%;border: 1px solid #ccc;"">Settlement Name</td>
												<td style="width:28%;">Applicable</td>
											</tr>
									</thead>
							</table>
					

					 
							 <table id="tblSettlement" style="width: 100%;">
			    				  <tbody style="border-top: none">

			    				  </tbody>
			    			 </table>
					  </div>
					  
				  </div>
				  
				 <div class="element-input col-lg-6" > 
				 
					  <div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 100%;">
							<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;" >
				    				<thead>
											<tr>
												<td style="width:36.5%;">Group Code</td>
												<td style="width:36.5%;">Group Name</td>
												<td style="width:28%;">Applicable</td>
											</tr>
									</thead>
							</table>
					   
							 <table id="tblGroup" style="width: 100%;">
			    				  <tbody style="border-top: none">
			    				  		
			    				  </tbody>
			    			 </table>
					  </div>
					  
				</div>
				
			</div>
			
		</div>

<!--  End of Tab2-->

<!--  Start of Tab3-->

		<div id="tab3" class="tab_content">
		
			<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 20%;" > 
		    				<label class="title">Tax On Tax</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="width: 20%;" > 
		    				<label class="title">Tax Type</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="width: 20%;" > 
		    				<s:select id="cmbItemType" path="strItemType">
										<option value="Both">Both</option>
										<option value="Food">Food</option>
										<option value="Liquor">Liquor</option>
							</s:select>
					</div>
		     </div>
		    
		    <div class="row" style="background-color: #fff;display: -webkit-box;">
	    	
	    		<div class="element-input col-lg-6" style=" width: 80%;"> 
	    		
			    	<div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 100%;">
						<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;" >
					    	 <thead>
									<tr>
										 <td style="width:36.5%;">Tax Code</td>
										 <td style="width:36.5%;">Tax Name</td>
										 <td style="width:28%;">Select</td>
									</tr>
							 </thead>
						</table>
				
						<table id="tblTax" style="width: 100%;">
			    			<tbody style="border-top: none">
			    			
							</tbody>							
						</table>
						
				   </div>
		    
				</div>
		
	 		</div>	

	 		<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
		    		<div class="element-input col-lg-6" style="width: 12%;" > 
		    				<label class="title">Tax On :</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="width: 7%;" > 
		    				<s:checkbox element="li" id="chkHomeDelivery" path="strHomeDelivery" value="Yes" />
		    		</div>
		    		<div class="element-input col-lg-6" style="width: 17%;" > 
		    				<label>Home Delivery</label>
		    		</div>
		    		<div class="element-input col-lg-6" style="width: 7%;" > 
							<s:checkbox element="li" id="chkDinningInn" path="strDinningInn" value="Yes" />
					</div>
		    		<div class="element-input col-lg-6" style="width: 15%;" > 
							<label>Dinning Inn</label>
					</div>
		    		<div class="element-input col-lg-6" style="width: 7%;" > 
							<s:checkbox element="li" id="chkTakeAway" path="strTakeAway" value="Yes" />
					</div>
		    		<div class="element-input col-lg-6" style="width: 15%;" > 
							<label>Take Away</label>
					</div>
					
			</div>	
			
			<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 20%;" > 
		    				<label class="title">Select Area</label>
		    		</div>
		    </div>

	 		<div class="row" style="background-color: #fff;display: -webkit-box;margin-bottom: 10px;">
	 			
	 			<div class="element-input col-lg-6" style=" width: 80%;"> 
	    		
			    	<div style="border: 1px solid #ccc; display: block; height: 150px; overflow-x: hidden; overflow-y: scroll; width: 100%;">
						<table style="width: 100%;background-color:  #85cdffe6;border: 1px solid #ccc;" >
					    	 <thead>
									<tr>
										 <td style="width:36.5%;">Area Code</td>
										 <td style="width:36.5%;">Area Name</td>
										 <td style="width:28%;">Select</td>
									</tr>
							 </thead>
						</table>
				
						<table id="tblArea" style="width: 100%;">
			    			<tbody style="border-top: none">
			    			
							</tbody>							
						</table>
						
				   </div>
		    
				</div>
	 		
	 		</div>
		    

  </div>
		
<!-- End of Tab 3 -->

<!-- Start of Tab4 -->
			
		<div id="tab4" class="tab_content">
		
				<div class="row" style="background-color: #fff;margin-bottom: 10px;">
		    			<div class="element-input col-lg-6" > 
	    					<label class="title">Account Code</label>
	    				</div>
	    				<div class="element-input col-lg-6" style="width: 20%;"> 
							<s:input id="txtAccountCode" path="strAccountCode" ondblclick="funHelp('WebBooksAcountMaster.b')" />
						</div>
			    </div>
			
		</div>
							
<!-- End  of Tab4 -->
				
				<br/>
								
			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
				<p align="center">
						<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="SUBMIT" /></div>
						<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="RESET" onclick="funResetFields()"/></div>
				</p>
			</div>
		
	</div>
		

	</s:form>

</body>
</html>