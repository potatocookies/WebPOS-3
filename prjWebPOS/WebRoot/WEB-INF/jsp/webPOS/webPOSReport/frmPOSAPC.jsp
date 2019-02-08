<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Average Per Cover Report</title>
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


$(function() 
{	
	var POSDate="${gPOSDate}"
   var startDate="${gPOSDate}";
  	var Date = startDate.split(" ");
	var arr = Date[0].split("-");
	Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
	$("#txtFromDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtFromDate" ).datepicker('setDate', Dat);
	$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtToDate" ).datepicker('setDate', Dat);
				
}); 

</script>


</head>

<body>
	<div id="formHeading">
		<label>Average Per Cover Report</label>
	</div>
	<s:form name="POSAvgPerCoverReportForm" method="POST" action="rptPOSAvgPerCover.html?saddr=${urlHits}" target="_blank">

		<br />
		<br />
		<table class="masterTable">

			<tr>
				<td width="140px">POS Name</td>
				<td colspan="3"><s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" cssClass="BoxW124px" items="${posList}" >
					
				 </s:select></td>
			</tr>
			<tr>
				<td><label>From Date</label></td>
				<td><s:input id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox"/></td>
			</tr>
			<tr>
				<td><label>To Date</label></td>
				<td><s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox"/></td>
			</tr>
			<tr>				
				<td><label>Pos Wise</label></td>
				<td >
						<s:select id="cmbPosWise" path="strPosWise" cssClass="BoxW124px">
								<s:option value="NO">NO</s:option>
					    		<s:option value="YES">YES</s:option>
					    		
				    		
				    	</s:select>
					</td>
			</tr>
			<tr>				
				<td><label>Date Wise</label></td>
				<td >
						<s:select id="cmbDateWise" path="strDateWise" cssClass="BoxW124px">
								<s:option value="NO">NO</s:option>
				    		<s:option value="YES">YES</s:option>
				    
				    		
				    	</s:select>
					</td>
			</tr>
			<tr>				
				<td><label>Waiter Wise</label></td>
				<td >
						<s:select id="cmbWaiterWise" path="strWShortName" cssClass="BoxW124px" items="${waiterlist}">
				    	</s:select>
					</td>
			</tr>
			<tr>				
				<td><label>Report Type</label></td>
				<td >
						<s:select id="cmbDocType" path="strDocType" cssClass="BoxW124px">
				    		<s:option value="PDF">PDF</s:option>
				    		<s:option value="XLS">EXCEL</s:option>
				    		
				    	</s:select>
					</td>
			</tr>
			<tr>				
				<td><label>Report Mode</label></td>
				<td >
						<s:select id="cmbReportMode" path="strReportType" cssClass="BoxW124px">
				    		<s:option value="Summary">Summary</s:option>
				    		<s:option value="Detail">Detail</s:option>
				    		
				    	</s:select>
					</td>
			</tr>
			<tr>				
				<td><label>APC On</label></td>
				<td >
						<s:select id="cmbAPCOn" path="strViewType" cssClass="BoxW124px">
				    		<s:option value="Net Sale">Net Sale</s:option>
				    		<s:option value="Gross Sale">Gross Sale</s:option>
				    		
				    	</s:select>
					</td>
			</tr>					
			
			
		</table>
		<br />
		<br />
		<p align="center">
			<input type="submit" value="Submit" tabindex="3" class="form_button"/> 
			<input type="reset" value="Reset" class="form_button" onclick="funResetFields()"/>
		</p>
	</s:form>

</body>
</html>