<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>UnSettle Bill</title>
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
 
$(documnent).ready(function()
		 {
	var POSDate="${gPOSDate}"
		var startDate="${gPOSDate}";
	  	var Date = startDate.split(" ");
		var arr = Date[0].split("-");
		Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
		$("#txtPOSDate").datepicker({ dateFormat: 'dd-mm-yy'  });
		$("#txtPOSDate" ).datepicker('setDate', Dat);
	
		 });

 	function funHelp(transactionName)
	{	       
       // window.showModalDialog("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
    }
 	
  	
	
	function funSetData(code)
	{
		$("#txtBillNo").val(code);
		var date=$("#txtPOSDate").val;
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
				%>alert("Unsettle successfully  \n\n"+message);<%
			}
		}%>
		$("form").submit(function(event){
			
			  if($("#txtBillNo").val().trim()=="")
				{
					alert("Please Enter Bill No");
					return false;
				}
			  
			  else{
				  return true;
			  }
			});
	
	});
	
	 function funSetDate()
		{
			
			var searchurl=getContextPath()+"/getPOSDate.html";
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	/* var dateTime=response.POSDate;
				        	var date=dateTime.split(" ");
				        	$("#txtFromDate").val(date[0]);
				        	$("#txtToDate").val(date[0]); */
				        	
				        var date = new Date(response.POSDate);
				        var	dateTime=date.getDate()  + '-' + (date.getMonth() + 1)+ '-' +  date.getFullYear();
				        var posDate=dateTime.split(" ");
				        $("#txtPOSDate").val(posDate[0]);
			        
			        	
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

  	

</script>
</head>
<body >

	<div id="formHeading">
	<label>UnSettle Bill</label>
	</div>

<br/>
<br/>

	<s:form name="UnSettleBill" method="POST" action="saveUnStettleBill.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
		
		<div class="title" style="margin-left: 200px;">
			
				<div class="row" style="background-color: #fff;margin-bottom: 10px;">
					<div class="element-input col-lg-6" style="width: 40%;"> 
	    				<label class="title" >Select Bill No</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 35%;margin-bottom: 10px;">
	    				<s:input type="text" id="txtBillNo"  path="strBillNo" ondblclick="funHelp('POSUnSettleBill')" required="true" readonly="true"/>
	    			</div>
	    		</div>
	    		
	    		<div class="row" style="background-color: #fff;margin-bottom: 10px;">
	    			<div class="element-input col-lg-6" style="width: 40%;"> 
	    				<label class="title" >Reason Code</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 54%;margin-bottom: 10px;">
	    				<s:select id="txtReasonCode" path="strReasonCode" items="${ReasonNameList}" required="true"/>
	    			</div>
	    		</div>
	    		
	    		
	    		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;">
     		  		<p align="center">
            			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="UNSETTLE" tabindex="3" /></div>
            			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"/></div>
     		  		</p>
   				</div>
   		
	   	</div>
	    			
		
<!-- 		<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>select Bill No</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:input colspan="3" type="text" id="txtBillNo"  path="strBillNo" cssClass="searchTextBox" ondblclick="funHelp('POSUnSettleBill')" required="true" readonly="true"/> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td> -->
<!-- 					<label>Reason Code</label> -->
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<s:select id="txtReasonCode" path="strReasonCode" items="${ReasonNameList}"  cssClass="BoxW124px" required="true"/> --%>
<!-- 				</td> -->
<!-- 			</tr> -->
			
<!-- 		</table> -->

<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="UNSETTLE" tabindex="3" class="form_button" /> -->
<!-- 			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 		</p> -->

	</s:form>
</body>
</html>
