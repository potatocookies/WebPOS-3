<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<script type="text/javascript">
$(document).ready(function () {
	  $('input#txttimeShiftStart').mlKeyboard({layout: 'en_US'});
	  $('input#txttimeShiftEnd').mlKeyboard({layout: 'en_US'});
	 
});
	
	function funHelp(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	
	

	/**
	* Get and Set data from help file and load data Based on Selection Passing Value(Group Code)
	**/
	function funSetData(code)
	{
		$("#txtShiftCode").val(code);
		var searchurl=getContextPath()+"/loadPOSShiftMasterData.html?POSShiftCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.strCustomerTypeMasterCode=='Invalid Code')
			        	{
			        		alert("Invalid Shift Code");
			        		$("#txtShiftCode").val('');
			        	}
			        	else
			        	{  
				        	$("#txtShiftCode").val(response.intShiftCode);
				        	$("#txtPOSCode").val(response.strPOSCode);
				        	document.getElementById("txtPOSCode").disabled=true;
				        	$("#txttimeShiftStart").val(response.strtimeShiftStart);
				        	$("#txttimeShiftStart").focus();
				        	$("#txttimeShiftEnd").val(response.strtimeShiftEnd);
				        	$("#cmbtimeShiftStart").val(response.strAMPMStart);
				        	$("#cmbtimeShiftEnd").val(response.strAMPMEnd);
				        	 
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

	$(function() {
		  $('#staticParent').on('keydown', '#txttimeShiftStart', function(e){-1!==$.inArray(e.keyCode,[46,8,9,27,13,110,190])||/65|67|86|88/.test(e.keyCode)&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()});
		  $('#staticParent').on('keydown', '#txttimeShiftEnd', function(e){-1!==$.inArray(e.keyCode,[46,8,9,27,13,110,190])||/65|67|86|88/.test(e.keyCode)&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()});
		  $('#staticParent').on('keydown', '#txtShiftCode', function(e){-1!==$.inArray(e.keyCode,[46,8,9,27,13,110,190])||/65|67|86|88/.test(e.keyCode)&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()});
		})
	 		
	function funOnSubmit() 
	{
		document.getElementById("txtPOSCode").disabled=false;
	}
</script>

</head>
<body>

	<div id="formHeading">
	<label>Shift Master</label>
	</div>

<br/>
<br/>

	<s:form name="ShiftMaster" method="POST" action="savePOSShiftMaster.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<div class="title">
		
			<div class="row" style="background-color: #fff;display: -webkit-box; margin-left: 25%;">
				<div class="element-input col-lg-6" style="width: 20%;"> 
    				<label class="title">Shift No</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" > 
					<s:input class="large" colspan="3" type="text" id="txtShiftCode" path="intShiftCode"  ondblclick="funHelp('POSshiMaster')"/>
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display: -webkit-box;margin-left: 25%;">
				<div class="element-input col-lg-6" style="width: 20%;" > 
    				<label class="title">POS</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;" >
					<s:select id="txtPOSCode" path="strPOSCode" items="${posList}">
					</s:select>
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display:-webkit-box;margin-left: 25%;">
				<div class="element-input col-lg-6" style="width: 20%;"> 
    				<label class="title">Shift Start</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;" > 
					<s:input class="large" colspan="3" type="text" id="txttimeShiftStart" path="strtimeShiftStart" style="width: 27%;" />
					<s:select id="cmbtimeShiftStart" path="strAMPMStart" style="width: 18%;">
				    		<option selected="selected" value="AM">AM</option>
			        		<option value="PM">PM</option>
		         	</s:select>
				</div>
			 </div>
			 
			 <div class="row" style="background-color: #fff;display:-webkit-box;margin-left: 25%;">
				<div class="element-input col-lg-6" style="width: 20%;"> 
    				<label class="title">Shift End</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;">
    				<s:input class="large" colspan="3" type="text" id="txttimeShiftEnd" path="strtimeShiftEnd" style="width: 27%;" /> 
			 		<s:select id="cmbtimeShiftEnd" path="strAMPMEnd" style="width: 18%;">
				    			<option selected="selected" value="AM">AM</option>
			        			<option value="PM">PM</option>
		         	</s:select>
		         </div>
		      </div>
		      
		      <div class="row" style="background-color: #fff;display:-webkit-box;margin-left: 25%;">
				<div class="element-input col-lg-6" style="width: 20%;"> 
    				<label class="title">Bill Date Time Type</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;">

			 		<s:select id="cmbBillDateTimeType" path="strBillDateTimeType" style="width: 30%;;">
				    			<option selected="selected" value="Pos Date">POS Date</option>
			        			<option value="System Date">System Date</option>
		         	</s:select>
		         </div>
		      </div>
		      
		      <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 25%;">
	     			<p align="center">
	            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" onclick="funOnSubmit()"/></div>
	          
	            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
	     			</p>
   			 </div>
			 
		</div>

	</s:form>
</body>
</html>
