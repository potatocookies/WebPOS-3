<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page session="True" %>
<!DOCTYPE html>
<html>
  <head>
	 <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title></title>

			<link rel="stylesheet" type="text/css" href="<spring:url value="/resources/newdesign/bootstrap/css/bootstrap.min.css"/>"/>
		    <link rel="stylesheet" type="text/css" href="<spring:url value="/resources/newdesign/css/font.css"/>"/>
		    <link rel="stylesheet" type="text/css" href="<spring:url value="/resources/newdesign/css/styles.css"/>"/>
		    <link rel="stylesheet" type="text/css" href="<spring:url value="/resources/newdesign/itemform_files/formoid1/formoid-default-skyblue.css" />"/>
		    

<script type="text/javascript">
	var fieldName;
	 $(document).ready(function () {
		  $('input#txtRate').mlKeyboard({layout: 'en_US'});
		  $('input#txtModifierCode').mlKeyboard({layout: 'en_US'});
		  $('input#txtModifierName').mlKeyboard({layout: 'en_US'});
		  $('textarea#txtModifierDescription').mlKeyboard({layout: 'en_US'});
		  
		  $('input#rdbDeselectAll').prop('checked', false);
		  $('input#rdbSelectAll').prop('checked', false);
		
		  $("form").submit(function(event){
			  if($("#txtModifierName").val().trim()=="")
				{
					alert("Please Enter Modifier Name");
					return false;
				}
			  else{
				  flg=funCallFormAction();
				  return flg;
			  }
			});
		 		
		}); 

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
		
	 
	 function funGetItemCode(value) {
			window.opener.funSetData(value);
			window.close();
		}
	
	function funSetData(code){
		$.ajax({
			type : "GET",
			url : getContextPath()+ "/loadModifierCode.html?modCode=" + code,
			dataType : "json",
			success : function(response){ 
				if(response.strModifierCode=='Invalid Code')
	        	{
	        		alert("Invalid Group Code");
	        		$("#txtModifierCode").val('');
	        	}
	        	else
	        	{
	               	$("#txtModifierCode").val(response.strModifierCode);
		        	$("#txtModifierName").val(response.strModifierName);
		        	$("#txtModifierName").focus();
		        	$("#txtModifierGroup").val(response.strModifierGroup);
		        	$("#txtModifierDescription").val(response.strModifierDescription);
		        	$("#txtRate").val(response.dblRate);
		        	
		        	if(response.strChargable=='y')
		        	{
		        		$("#chkChargable").attr('checked', true);
		        	}
		        	else  
		        	{
		        		$("#chkChargable").attr('unchecked', false);
		        	}
		        	
		        	if(response.strApplicable=='y')
		        	{
		        		$("#chkApplicable").attr('checked', true);
		        	}
		        	else
		        	{
		        		$("#chkApplicable").attr('unchecked', false);
		        	}
		        	 
		        		        						        	
	        	}
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
	function funLoadMenuHeadData()
	{
		var searchurl=getContextPath()+"/LoadMenuDetails.html";
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        
			        success: function (response) {
			        	funRemoveProductRows("tblMenuDet");
			            	$.each(response,function(i,item){
			            		funfillMenuDetail(response[i].strMenuCode,response[i].strMenuName);
			            	   
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

	function funfillMenuDetail(strMenuCode,strMenuName)
	{
		var table = document.getElementById("tblMenuDet");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"20%\" id=\"strMenuCode."+(rowCount)+"\" value='"+strMenuCode+"' onclick=\"funGetSelectedRowIndex('"+strMenuCode+"')\"/>";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"30%\" id=\"strMenuName."+(rowCount)+"\" value='"+strMenuName+"' onclick=\"funGetSelectedRowIndex('"+strMenuCode+"')\"/>";

	}
	
	function funfillItemDetail(strItemName,strItemCode,StrMenuCode)
	{
		var table = document.getElementById("tblItemDet");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		var rate=document.getElementById('txtRate').value;
		
	    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"50%\" name=\"listObjItemBean["+(rowCount)+"].strItemName\" id=\"strItemName."+(rowCount)+"\" value='"+strItemName+"'>";
	    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" class=\"Box\" size=\"15%\" name=\"listObjItemBean["+(rowCount)+"].strItemCode\" id=\"strItemCode."+(rowCount)+"\" value='"+strItemCode+"'>";
	/*     row.insertCell(2).innerHTML= "<input id=\"strSelect."+(rowCount)+"\" type=\"checkbox\" class=\"GCheckBoxClass\" name\"listObjItemBean["+(rowCount)+"].strSelect\" value='"+rate+"' >"; */
	    row.insertCell(2).innerHTML= "<input id=\"cbItemSel."+(rowCount)+"\" type=\"checkbox\" class=\"GCheckBoxClass\" name=\"listObjItemBean["+(rowCount)+"].strSelect\" size=\"15%\" value=\"Tick\" />";
	    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" class=\"Box\" style=\"text-align:right;\" size=\"12%\"name=\"listObjItemBean["+(rowCount)+"].dblPurchaseRate\" id=\"dblPurchaseRate."+(rowCount)+"\" value='"+rate+"'>";
	    row.insertCell(4).innerHTML= "<input id=\"cbDefMod."+(rowCount)+"\" type=\"checkbox\" class=\"GCheckBoxClass\"  name=\"listObjItemBean["+(rowCount)+"].DefMod\" size=\"20%\"  value='Tick' >";
	     
	}

	//Remove Table data when pass a table ID as parameter
	function funRemoveProductRows(tableName)
			{
				var table = document.getElementById(tableName);
				var rowCount = table.rows.length;
				while(rowCount>0)
				{
					table.deleteRow(0);
					rowCount--;
				}
			}


	function funGetSelectedRowIndex(menuCode)
	{
		
		var searchurl=getContextPath()+"/loadMenuWiseItemDetail.html?MenuCode="+menuCode;
		 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        success: function (response) {
		        	funRemoveProductRows("tblItemDet");
		            	$.each(response,function(i,item){
		            		funfillItemDetail(response[i].strItemName,response[i].strItemCode,menuCode);
		            	   
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
	
	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	function funSelectAllChkBox()
	{
		 $('input#rdbDeselectAll').prop('checked', false);
		
		 /*  $('ItemSel').prop('checked', true);
		 $('input#cbItem.'+i).prop('checked', true);  */
		 
		 var table = document.getElementById("tblItemDet");
		 var rowCount = table.rows.length;
		 for(i=0; i<rowCount; i++)
			 $("#tblItemDet tr:eq("+i+") td:eq(2) input:checkbox").prop("checked", true);	 
	}
	function funDeSelectAllChkBox()
	{
		 $('input#rdbSelectAll').prop('checked', false);
		 var table = document.getElementById("tblItemDet");
		 var rowCount = table.rows.length;
		 for(i=0; i<rowCount; i++)
			 $("#tblItemDet tr:eq("+i+") td:eq(2) input:checkbox").prop("checked", false);
	}
	
	function btnApply_onclick()
	{
		var rate=document.getElementById('txtRate').value;
		
		 var table = document.getElementById("tblItemDet");
		 var rowCount = table.rows.length;
		 for(i=0; i<rowCount; i++)
			 $("#tblItemDet tr:eq("+i+") td:eq(3)").text(rate);
		 /* append(rate) */
	}
	function funResetFields()
	{
		$("#txtModifierName").focus();
		$("#txtModifierCode").val('');
		$("#txtModifierName").val('');
		$("#strModifierGroup").val('');
		$("#txtModifierDescription").val('');
		$("#txtRate").val('');
	    funRemoveProductRows("tblItemDet");
	}

	
	
	function funCallFormAction(actionName,object) 
	{
		var flg=true;
		
		
			var name = $('#txtModifierName').val();
			var code=$('#txtModifierCode').val();
			 $.ajax({
			        type: "GET",
			        url: getContextPath()+"/checkModName.html?modName="+name+"&modCode="+code,
			        async: false,
			        dataType: "text",
			        success: function(response)
			        {
			        	if(response=="false")
			        		{
			        			alert("Modifier Name Already Exist!");
			        			$('#txtModifierName').focus();
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
<body onload="funLoadMenuHeadData()">

	<div id="formHeading">
	<label>Item Modifier Master</label>
	</div>

<br/>
<br/> 
	<s:form name="ItemModifierMaster" method="POST" action="saveItemModifierMaster.html?saddr=${urlHits}"  class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" > 
	
		<div class="title"><h2></h2>
		
			<div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" style="margin-bottom: 5px;"> 
    				<label class="title">Modifier Code</label>
					<s:input class="large" colspan="3" type="text" id="txtModifierCode" path="strModifierCode"  ondblclick="funHelp('POSItemModifierMaster');" style="width:60%"/>
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 5px;">
					<label class="title">Modifier Name</label>
					<s:input class="large" colspan="3" type="text" id="txtModifierName" path="strModifierName"  />
				</div> 
			</div>

			<div class="row" style="background-color: #fff;">
		  		<div class="col-lg-6" style="padding-left: 0px;">
		  	
					<div class="element-select col-lg-12" style="margin-bottom: 5px;"><label class="title">Modifier Group</label>
						<div class="large"><s:select id="txtModifierGroup" path="strModifierGroup" items="${ModifierGroup}"  cssClass="BoxW124px" style="height: 10%; width:60%"/>
						</div>
					</div>
				
					<div class="element-input col-lg-12" style="margin-bottom: 5px;"><label class="title">Rate</label>
						<s:input colspan="3" id="txtRate" path="dblRate" type="text" min="0" step="1" class="longTextBox" style="width: 60%; text-align: right;"/>
					</div>
					<!-- <div class="element-radio col-lg-12"> -->
					<div class="column column1"     style="padding-left: 15px;">
						<label><s:input type="checkbox"  id="chkApplicable" path="strApplicable"  style="width: 8%"></s:input>
						Applicable</label>
						
						<label><s:input type="checkbox"  id="chkChargable" path="strChargable" style="width: 8%"></s:input>
						Chargeable</label>
					</div>
				<!-- </div> -->
					</div>	
				
						<div class="element-textarea col-lg-6"><label class="title">Modifier Description</label>
						<s:textarea id="txtModifierDescription" path="strModifierDescription" class="medium" name="textarea" cols="20" rows="5"/>
					</div>		

				
					<div class="submit col-lg-12">&nbsp;&nbsp;&nbsp;
						<input type="Button" value="Apply" onclick="btnApply_onclick()"/>
					</div>
				
			</div>
		</div>
		
	</s:form>
			
	 <div class="container" style="background-color: #fff;">
<!--  	 <div class="row" style="background-color: #fff;"> -->
		 <div class="col-xs-4" style="margin-left: 5%;">
      	 	<div id="tableLoad">	
			
				<table class="scroll" style="width: 100%;border: 1px solid #ccc;">
    				<thead style="background-color:  #85cdffe6;">
        				<tr>
            				<th style="width: 50%";>Menu Code</th>
            				<th>Menu Head Name</th>
        				</tr>	
    				</thead>
    			</table>
    		
    			<table class="scroll" id="tblMenuDet" style="width: 100%;border: 1px solid #ccc;">
    				<tbody style="border-top: none">				
					
					</tbody>
    			</table>
    		</div>
   	 	</div>
    
   	 	<div class="col-xs-6">
   	 	
      		<div id="tableLoad">
       			 <table class="scroll" id="table2" style="width: 100%;border: 1px solid #ccc;">
          			<thead style="background-color:  #85cdffe6;">
           				 <tr>
              				<th style="width: 53%;">Item Name</th>
             			    <th>Item Code</th>
              				<th>Select</th>
              				<th>Rate</th>
              				<th>Default Modifier</th>
            			</tr>
         			 </thead>
         		</table>
        
        		<table id="tblItemDet" class="scroll" style="width: 100%;border: 1px solid #ccc;">
       				 <tbody style="border-top: none">
        			
        			 </tbody>
        
        		</table>
     		 </div>
    	</div>
    
<!--     </div> -->
    </div>
    
    	<s:form class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

   			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;">
     
            	<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit"/></div>
          
           		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
     
   			</div>
   
   			<div class="element-radio col-lg-2 col-sm-2 col-xs-2">
    			<label class="title"></label>
    				<div class="column column1">
    					<label><input type="radio" name="radio" value="Applicable" id="rdbSelectAll" path="strSelectAll" value="Y" onclick="funSelectAllChkBox()" /><span>Select All</span></label>
    					<label><input type="radio" name="radio" value="Chargeable" id="rdbDeselectAll" path="strDeselectAll" value="Y" onclick="funDeSelectAllChkBox()" /><span>Deselect All</span></label>
    				</div>
<%--     <span class="clearfix"></span> --%>
  			</div>
  
  		</s:form>

<script>
// Change the selector if needed
var $table = $('table.scroll'),
    $bodyCells = $table.find('tbody tr:first').children(),
    colWidth;

// Adjust the width of thead cells when window resizes
$(window).resize(function() {
    // Get the tbody columns width array
    colWidth = $bodyCells.map(function() {
        return $(this).width();
    }).get();
    
    // Set the width of thead columns
    $table.find('thead tr').children().each(function(i, v) {
        $(v).width(colWidth[i]);
    });    
}).resize(); // Trigger resize handler
</script>
    
    </body>
    
</html>
   





