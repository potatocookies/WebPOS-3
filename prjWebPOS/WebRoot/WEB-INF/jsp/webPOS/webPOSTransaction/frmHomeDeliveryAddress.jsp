<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home Delivery Address</title>
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
<script>
var homeDeliveryType="";
var gSelectedArea="";


$(document).ready(function() {
	
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
			%>alert("Data Saved \n\n"+message);
			window.close();
			<%}
		
	}%>
	
	
	
		
			$('input#txtTaxDesc').mlKeyboard({layout: 'en_US'});
		  	$('input#txtTaxShortName').mlKeyboard({layout: 'en_US'});
		 	$('input#txtAmount').mlKeyboard({layout: 'en_US'});
		  	$('input#txtPercent').mlKeyboard({layout: 'en_US'});
		

		$(".tab_content").hide();
		$(".tab_content:first").show();

		$("ul.tabs li").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		$("#homeTab").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		$("#officeTab").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		$("#tempTab").click(function() {
			$("ul.tabs li").removeClass("active");
			$(this).addClass("active");
			$(".tab_content").hide();

			var activeTab = $(this).attr("data-state");
			$("#" + activeTab).fadeIn();
		});
		
		$("form").submit(function(event)
		{
			if(homeDeliveryType=="Temp")
			{
		    	if($("#txtTempAddress").val().trim()=="")
				{
					alert("Please Enter Temp Address");
					return false;
				}
			}
			
			funPreviousForm();
			
			return true;
		});
		
		
		var selectedAddress="${selectedAddress}";
		
		
		
		if(selectedAddress=="Home")
		{
			var btn=document.getElementById("Home");
						
			funBtnClicked(btn);
		}
		else if(selectedAddress=="Office")
		{
			var btn=document.getElementById("Office");			
			
			funBtnClicked(btn);
		}
		else 
		{
			var btn=document.getElementById("Temp");
			
			funBtnClicked(btn);
		}
});
	
	
function funBtnClicked(objBtn)
{
	homeDeliveryType=objBtn.id;
			
	$("#hiHomeDeliveryAddressFlag").val("Home");
	document.getElementById("Home").style.backgroundColor = "";
	document.getElementById("Office").style.backgroundColor = "";
	document.getElementById("Temp").style.backgroundColor = "";
	
	
	
	if(homeDeliveryType=="Temp")
	{	
		$("#hiHomeDeliveryAddressFlag").val("Temp");
		document.getElementById("Temp").style.backgroundColor = "lightblue";
		
		$("#tempTab").trigger("click");
		
		gSelectedArea=$("#txtTempBuildingName").val();
		
		//gSelectedArea=$('#txtTempBuildingName :selected').text();
		
	}
	else if(homeDeliveryType=="Office")
	{
		$("#hiHomeDeliveryAddressFlag").val("Office");
		document.getElementById("Office").style.backgroundColor = "lightblue";
			
		$("#officeTab").trigger("click");
		
		//gSelectedArea=$("#txtOfficeBuildingName").val();
		
		gSelectedArea=$('#txtOfficeBuildingName :selected').text();
			
	}
	else
	{
		$("#hiHomeDeliveryAddressFlag").val("Home");
		document.getElementById("Home").style.backgroundColor = "lightblue";
		
		$("#homeTab").trigger("click");
		
		//gSelectedArea=$("#txtHomeBuildingName").val();
		
		gSelectedArea=$('#txtHomeBuildingName :selected').text();
	}
		
}



	

function funPreviousForm()
{	
	
	var formName='';
	<%if (session.getAttribute("frmName") != null) 
	{%>		
		formName='<%=session.getAttribute("frmName").toString()%>';
	<%}
	%>
	
	if(formName=="Call Center")
	{
		window.opener.funSetHomeDeliveryAddress(gSelectedArea);		
		
	}
	else
	{
		window.opener.funCall();
		window.close();	
	}
	
}

function funSetSelectedBuildingNames()
{	
	$("#txtHomeBuildingName").val("${strHomeBuildingName}");
	$("#txtOfficeBuildingName").val("${strOfficeBuildingName}");
}
	
</script>
</head>

<body onload="funSetSelectedBuildingNames()">
	<div id="formHeading">
		<label>Customer Address</label>
	</div>
	<s:form name="POSForm" method="POST" action="updateHomeDeliveryAddress.html">

		<br />
		<br />
	
		<table  cellpadding="0" cellspacing="3" class="table table-striped table-bordered table-hover" Style=" margin-left:150px; width: 85%; height: 130%;">
									 <tr><td><input type="button" id="Home" value="Home" style="width: 100px;height: 35px; " onclick="funBtnClicked(this)"/></td>
									 <td><input type="button" id="Office" value="Office" style="width: 100px;height: 35px; " onclick="funBtnClicked(this)"/></td>
									 <td><input type="button" id="Temp" value="Temp" style="width: 100px;height: 35px; " onclick="funBtnClicked(this)"/></td>
									 <td><input type="Submit" id="Ok" value="OK" style="width: 100px;height: 35px; "/></td>
									 </tr>	 
									</table>
							<table class="masterTable" Style="width: 85%; height: 130%; ">
								<tr>
								<td>
					<label>Customer No</label>
				</td>
				<td >
					<s:input  type="text"  id="txtTableName" path="strHomeMobileNo" cssStyle="text-transform: uppercase; " cssClass="longTextBox jQKeyboard form-control" />
					</td>
				<td>
					<label>Customer Name</label>
				</td>
				<td>
					<s:input  type="text"  id="txtTableName" path="strHomeCustomerName" cssStyle="text-transform: uppercase; " cssClass="longTextBox jQKeyboard form-control" /> 
				
				</td>
								</tr>
						</table>
		<table
				style="border: 0px solid black; width: 85%; height: 130%;  margin-left: auto; margin-right: auto;background-color:#C0E4FF;">
				
				<tr>
					<td>
						<div id="tab_container" >
							<ul class="tabs">
								<li class="active" data-state="tab1" id="homeTab" >Home Address</li>
								<li data-state="tab2" id="officeTab">Office Address</li>
								<li data-state="tab3" id="tempTab">Temporary Address </li>
								
				
							</ul>
							<br /> <br />

							<!--  Start of Generals tab-->

							<div id="tab1" class="tab_content">
								<table  class="masterTable">
					
			<tr>
			<td><label>Address/Flat No.</label></td>
				<td colspan="3"><s:input type="text" id="txtTaxDesc" 
						name="txtTaxDesc" path="strHomeAddress" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /></td>
						
				
			</tr>
		
			<tr>
			<td><label>Street Name</label></td>
				<td colspan="3"><s:input type="text" id="txtTaxShortName" 
						name="txtTaxShortName" path="strHomeStreetName" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /></td>
				
			</tr>
			<tr>
			<td><label>LandMark</label></td>
				
				<td colspan="3"><s:input  type="text" id="txtAmount" 
						name="txtAmount" path="strHomeLandmark"
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /> 
		       </td>
			</tr>
			
			<tr>
			
		      
		       
		       <td><label>Pin Code</label></td>
		       <td><s:input type="text" id="txtPercent" 
						name="txtPercent" path="strHomePinCode"
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /> 
		       </td>
		        <td ><s:input  type="text" id="txtPercent" 
						name="txtPercent" path="strHomeCity" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  placeholder="City" /> 
		       </td>
		        <td ><s:input  type="text" id="txtPercent" 
						name="txtPercent" path="strHomeState" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox" placeholder="State"  /> 
		       </td>
			</tr>
			<tr>
				 <td><label>Area</label></td>
				 <td><s:select type="text" id="txtHomeBuildingName" name="txtHomeBuildingName" path="strHomeBuildingName" items="${mapBuildingNames}"  cssClass="BoxW124px" style="width: 247px;" required="true"  />				 
			   	 </td>
			</tr>
			
			</table>
			
			
							</div>
							<!--  End of  tax details1 tab-->




							<!-- Start of tax details2 tab -->

							<div id="tab2" class="tab_content">
											<table  class="masterTable">
					
			<tr>
			<td><label>Address/Flat No.</label></td>
				<td colspan="3"><s:input type="text" id="txtTaxDesc" 
						name="txtTaxDesc" path="strOfficeCustAddress"
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /></td>
						
			</tr>
		
			<tr>
			<td><label>Street Name</label></td>
				<td colspan="3"><s:input type="text" id="txtTaxShortName" 
						name="txtTaxShortName" path="strOfficeStreetName" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /></td>
				
			</tr>
			<tr>
			<td><label>LandMark</label></td>
				
				<td colspan="3"><s:input type="text" id="txtAmount" 
						name="txtAmount" path="strOfficeLandmark" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /> 
		       </td>
			</tr>
			
			<tr>
			
		      
		       
		       <td><label>Pin Code</label></td>
		       <td ><s:input  type="text" id="txtPercent" 
						name="txtPercent" path="strOfficePinCode" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /> 
		       </td>
		        <td ><s:input  type="text" id="txtPercent" 
						name="txtPercent" path="strOfficeCity" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  placeholder="Office City" /> 
		       </td>
		        <td ><s:input  type="text" id="txtPercent" 
						name="txtPercent" path="strOfficeState" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  placeholder="Office State" /> 
		       </td>
			</tr>
			<tr>
				 <td><label>Area</label></td>				 
				 <td><s:select type="text" id="txtOfficeBuildingName" name="txtOfficeBuildingName" path="strOfficeBuildingName" items="${mapBuildingNames}"  cssClass="BoxW124px" style="width: 247px;"  />
			   	 </td>
			</tr>
			
			</table>							</div>
							<!-- End of tax details2 tab -->


							<!-- Start of tax details3 Tab -->

						<div id="tab3" class="tab_content">
												<table  class="masterTable">
					
			<tr>
			<td><label>Temp Address</label></td>
				<td colspan="3"><s:textarea  id="txtTempAddress" 
						path="strTempCustAddress"  style="height:100px"
						 cssClass="longTextBox"  /></td>
						
			</tr>
		
			<tr>
			<td><label>Street Name</label></td>
				<td colspan="3"><s:input  type="text" id="txtTaxShortName" 
						name="txtTaxShortName" path="strTempStreetName" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /></td>
				
			</tr>
			<tr>
			<td><label>LandMark</label></td>
				
				<td colspan="3"><s:input  type="text" id="txtAmount" 
						name="txtAmount" path="strTempLandmark" 
						cssStyle="text-transform: uppercase;" cssClass="longTextBox"  /> 
		       </td>
			</tr>
			
			<tr>
				 <td><label>Area</label></td>
				 <td><s:input type="text" id="txtTempBuildingName" name="strTempBuildingName" path="strTempBuildingName" cssStyle="text-transform: uppercase;" cssClass="longTextBox"  />
			   	 </td>
			</tr>
			
			</table>
						</div>

						<!-- End  of tax details3  Tab -->


						

					

						</div>
					</td>
				</tr>
			</table>
		<br />
		<br />
	<s:hidden id="hiHomeDeliveryAddressFlag" path="strHomeDeliveryAddressFlag"/>
	<s:hidden id="hiCustomerCode" path="strCustomerCode"/>
	</s:form>

</body>
</html>