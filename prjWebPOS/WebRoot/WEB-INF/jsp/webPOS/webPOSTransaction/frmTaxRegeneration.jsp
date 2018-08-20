<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

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
 		var POSDate="${POSDate}"
 			var startDate="${POSDate}";
 		  	var Date = startDate.split(" ");
 			var arr = Date[0].split("-");
 			Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
 			$("#txtdteToDate").datepicker({ dateFormat: 'dd-mm-yy'  });
 			$("#txtdteToDate" ).datepicker('setDate', Dat);
 			$("#txtdteFromDate").datepicker({ dateFormat: 'dd-mm-yy'  });
 			$("#txtdteFromDate" ).datepicker('setDate', Dat);
 			
 			
 	});

	
	
</script>		

</head>
<body>

<div id="formHeading">
		<label>Tax Regneration</label>
	</div>
	
	
	<s:form name="Tax Regeneration" method="POST" action="saveTaxREgeneration.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

<br/>
		<div class="title" style="margin-left: 15%;">
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 25%;"> 
								 <s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" />
							</div>
							
					 </div>
		    				
		    		 <div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 25%;"> 
								<s:input id="txtdteFromDate" name="txtdteFromDate" path="dteFromDate" value="${posDate}" style="width: 100%;" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 25%;"> 
								<s:input id="txtdteToDate" name="txtdteToDate" path="dteToDate" value="${posDate}" style="width: 100%;" />
							</div>
					 </div>
					 
					 <br/>
					 
					 <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%; margin-left: 15%;">
					
						<p align="center">
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="SUBMIT" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()" /></div>
						</p>
						
				  	</div>
				
			</div>
		
<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<table class="masterTable"> -->

<!-- 		<tr> -->
<!-- 			<td> -->
<!-- 				<label>POS Name</label> -->
<!-- 			</td> -->
<!-- 			<td> -->
<%-- 				<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" cssClass="BoxW124px" /> --%>
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 			<td> -->
<!-- 				<label>From Date</label> -->
<!-- 			</td> -->
<!-- 			<td> -->
<%-- 				<s:input id="txtdteFromDate" name="txtdteFromDate" path="dteFromDate" value="${posDate}" cssClass="calenderTextBox" /> --%>
<!-- 			</td> -->
			
<!-- 			<td> -->
<!-- 				<label>To Date</label> -->
<!-- 			</td> -->
<!-- 			<td> -->
<%-- 				<s:input id="txtdteToDate" name="txtdteToDate" path="dteToDate" value="${posDate}" cssClass="calenderTextBox" /> --%>
<!-- 			</td> -->
<!-- 		</tr> -->
		
<!-- </table> -->

<!-- <br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="Submit" tabindex="3" class="form_button" />  -->
<!-- 			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/> -->
<!-- 		</p> -->


</s:form>
</body>
</html>