<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>

<script type="text/javascript">
	var fieldName;

	$(document).ready(function() {
		 $('input#txtWaiterNo').mlKeyboard({layout: 'en_US'});
		  $('input#txtWShortName').mlKeyboard({layout: 'en_US'});
		  $('input#txtWFullName').mlKeyboard({layout: 'en_US'});
		  $('input#txtDebitCardString').mlKeyboard({layout: 'en_US'});
	});
	
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
				
				  $("form").submit(function(event){
					  if($("#txtWShortName").val().trim()=="")
						{
							alert("Please Enter Waiter Short Name");
							return false;
						}
					   if($("#txtWFullName").val().trim()=="")
						{
							alert("Please Enter Waiter Full Name");
							return false;
						}
					   if($("#txtWShortName").val().length > 10)
						{
							alert("Waiter Short Name length must be less than 10");
							return false;
						}
					  else{
						  flg=funCallFormAction();
						  return flg;
					  }
					});
			});

	function funSetData(code){

		switch(fieldName){

			case 'POSWaiterMaster' : 
				funSetWaiterNo(code);
				break;
		}
	}


	function funSetWaiterNo(code){

		$("#txtWaiterNo").val(code);
		var searchurl=getContextPath()+"/loadPOSWaiterMasterData.html?POSWaiterCode="+code;		
		 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        success: function(response)
		        {
		        	if(response.strWaiterNo=='Invalid Code')
		        	{
		        		alert("Invalid Group Code");
		        		$("#txtWaiterNo").val('');
		        	}
		        	else
		        	{
			        	
			        	$("#txtWShortName").val(response.strWShortName);
			        	$("#txtWFullName").val(response.strWFullName);
			        	$("#txtDebitCardString").val(response.strWaiterName);
			        	$("#txtWShortName").focus();
			        	if(response.strOperational=='Y')
		        		{
			        		$("#chkOperational").prop('checked',true);
		        		}
			        	
			        	$("#txtDebitCardString").val(response.strDebitCardString);
			        	$("#txtPOSCode").val(response.strPOSCode);
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



	/**
	*  Check Validation Before Saving Record
	**/
	
	
	function funCallFormAction() 
	{
		var flg=true;
		
		var name = $('#txtWShortName').val();
		var code= $('#txtWaiterNo').val();
		
			 $.ajax({
			        type: "GET",
			        url: getContextPath()+"/checkWaiterName.html?name="+name+"&code="+code,
			        async: false,
			        dataType: "text",
			        success: function(response)
			        {
			        	if(response=="false")
			        		{
			        			alert("Waiter Short Name Already Exist!");
			        			$('#txtWShortName').focus();
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








	function funHelp(transactionName)
	{
		fieldName=transactionName;
		  window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
	}
</script>

</head>
<body>

	<div id="formHeading">
	<label>Waiter Master</label>
	</div>

<br/>
<br/>

	<s:form name="WaiterMaster" method="POST" action="savePOSWaiterMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

	<div class="title">
			<div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Waiter No</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtWaiterNo" path="strWaiterNo"  ondblclick="funHelp('POSWaiterMaster')"/>
				</div>
			 </div>
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Waiter Short Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtWShortName" path="strWShortName" />
				</div>
			 </div>
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Waiter Full Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtWFullName" path="strWFullName" />
				</div>
			 </div>
			 <div class="row" style="background-color: #fff;">
			 	<div class="element-input col-lg-6" >
					<label>Operational</label>
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;">
					<s:input type="checkbox"  id="chkOperational" path="strOperational" ></s:input>
			 	</div>
			 </div>
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" > 
    				<label class="title">Swipe Card</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;"> 
					<s:input class="large" colspan="3" type="text" id="txtDebitCardString" path="strDebitCardString" />
				</div>
			 </div>
			 <div class="row" style="background-color: #fff;">
				<div class="element-input col-lg-6" >
					<label class="title">POS Name</label>
				</div>
				<div class="element-input col-lg-6" style="margin-bottom: 10px;">
						<s:select id="txtPOSCode" path="strPOSCode" items="${posList}"  style="height: 10%; width: 100%;"/>
				</div>
			 </div>
			 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;">
     			<p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit"/></div>
          
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
     			</p>
   			 </div>
	</div>
	
<!-- 		<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td width="140px"> -->
<!-- 					<label>Waiter No</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input colspan="3" type="text" id="txtWaiterNo" path="strWaiterNo" cssClass="searchTextBox jQKeyboard form-control" ondblclick="funHelp('POSWaiterMaster');"/> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Waiter Short Name</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input colspan="3" type="text" id="txtWShortName" path="strWShortName" cssClass="longTextBox jQKeyboard form-control" /> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Waiter Full Name</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input colspan="3" type="text" id="txtWFullName" path="strWFullName" cssClass="longTextBox jQKeyboard form-control" /> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Operational</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input type="checkbox" id="chkOperational" path="strOperational"  /> --%>
					
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Swipe Card</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input colspan="3" type="text" id="txtDebitCardString" path="strDebitCardString" cssClass="longTextBox jQKeyboard form-control" /> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>POS Code</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:select id="txtPOSCode" path="strPOSCode" items="${posList}"  cssClass="BoxW124px"/> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 		</table> -->

<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="Submit" tabindex="3" class="form_button" /> -->
<!-- 			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 		</p> -->

	</s:form>
</body>
</html>
