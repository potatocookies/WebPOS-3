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

$(document).ready(function () {
	  $('input#txtGroupCode').mlKeyboard({layout: 'en_US'});
	  $('input#txtGroupName').mlKeyboard({layout: 'en_US'});
	  
	  $("form").submit(function(event){
		  if($("#txtGroupName").val().trim()=="")
		  {
				alert("Please Enter Group Name");
				return false;
		  }
		 else
		 {
			
			  flg=funCallFormAction();
			  return flg;
		  }
		});
	  
	}); 
	


	/**
	* Reset The Group Name TextField
	**/
	function funResetFields()
	{
		$("#txtGroupName").focus();
		$("#txtOperationType").val('N');
    }
	
	
		/**
		* Open Help
		**/
		function funHelp(transactionName)
		{	       
	       // window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	    }
		
		/**
		* Get and Set data from help file and load data Based on Selection Passing Value(Group Code)
		**/
		function funSetData(code)
		{
			$("#txtGroupCode").val(code);
			var searchurl=getContextPath()+"/loadPOSGroupMasterData.html?POSGroupCode="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	if(response.strGCode=='Invalid Code')
				        	{
				        		alert("Invalid Group Code");
				        		$("#txtGroupCode").val('');
				        	}
				        	else
				        	{
					        	$("#txtGroupCode").val(response.strGroupCode);
					        	$("#txtGroupName").val(response.strGroupName);
					        	$("#txtGroupName").focus();
					        	$("#txtShortName").val(response.strShortName);
					        	
					        	if(response.strOperational=='Y')
				        		{
					        		$("#chkOperational").prop('checked',true);
				        		}
					        	else
					        		$("#chkOperational").prop('checked',false);
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
			/**
			* On Blur Event on TextField
			**/
// 			$('#txtGroupCode').blur(function() 
// 			{
// 					var code = $('#txtGroupCode').val();
// 					if (code.trim().length > 0 && code !="?" && code !="/")
// 					{				
// 						funSetData(code);							
// 					}
// 			});
			
			$('#txtGroupName').blur(function () {
				 var strGName=$('#txtGroupName').val();
			      var st = strGName.replace(/\s{2,}/g, ' ');
			      $('#txtGroupName').val(st);
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
		
		
		
		
		/**
		*  Check Validation Before Saving Record
		**/
		function funCallFormAction(actionName,object) 
		{
			var flg=true;
			
			var name = $('#txtGroupName').val();
			var code= $('#txtGroupCode').val();
			
			
				 $.ajax({
				        type: "GET",
				        url: getContextPath()+"/CheckPosGroupName.html?name="+name+"&code="+code,
				        async: false,
				        dataType: "text",
				        success: function(response)
				        {
				        	if(response=="false")
				        		{
				        			alert("Group Name Already Exist!");
				        			$('#txtGroupName').focus();
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

<body >
	<div id="formHeading">
		<label>Group Master</label>
	</div>
	<br/><br/>
	<s:form name="grpForm" method="POST" action="savePOSGroupMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<div class="title" style="margin-left: 190px;">
		
			<div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Group Code</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;" > 
					<s:input class="large" colspan="3" type="text" id="txtGroupCode" path="strGroupCode"  ondblclick="funHelp('POSGroupMaster')"/>
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Group Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtGroupName" path="strGroupName" />
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Short Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtShortName" path="strShortName" />
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;">
			 	<div class="element-input col-lg-6" >
					<label>Operational</label>
				</div>
				<div class="element-input col-lg-6" >
					<s:input type="checkbox"  id="chkOperational" path="strOperational" ></s:input>
			 	</div>
			 </div>
			 
			 <br/>
			 
			  <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;">
     			<p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit"/></div>
          
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
     			</p>
   			</div>
		</div>
<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<div id="jquery-script-menu"> -->

<!-- 		</div> -->
<!-- 		<table class="masterTable"> -->

<!-- 			<tr> -->
<!-- 				<td width="140px">Group Code</td> -->
<%-- 				<td><s:input id="txtGroupCode" path="strGroupCode" --%>
<%-- 						cssClass="searchTextBox jQKeyboard form-control" ondblclick="funHelp('POSGroupMaster')" /></td> --%>
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td><label>Group Name</label></td> -->
<%-- 				<td><s:input colspan="3" type="text" id="txtGroupName"  --%>
<%-- 						name="txtGroupName" path="strGroupName" required="true" --%>
<%-- 						cssStyle="text-transform: uppercase; width:200px" cssClass="longTextBox jQKeyboard form-control"  />  --%>
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td><label>Short Name</label></td> -->
<%-- 				<td><s:input colspan="1" type="text" id="txtShortName"  --%>
<%-- 						name="txtShortName" path="strShortName" --%>
<%-- 						cssStyle="text-transform: uppercase; width:200px" cssClass="longTextBox jQKeyboard form-control"  /> --%>
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td><label>Operational</label></td> -->
<%-- 				<td><s:input colspan="1" type="checkbox" id="chkOperational"  --%>
<%-- 						name="chkOperation" path="strOperational" style="width:8%" /> --%>
<!-- 			</tr> -->
			
			
<!-- 		</table> -->
<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="Submit" tabindex="3" class="form_button"/>  -->
<!-- 			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 		</p> -->
	</s:form>

</body>
</html>