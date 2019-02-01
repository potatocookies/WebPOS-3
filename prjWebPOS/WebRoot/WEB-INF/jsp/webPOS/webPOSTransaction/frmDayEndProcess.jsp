<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="s" %>
    <%@ taglib uri ="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Day End Process</title>
</head>
<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
    /* add padding to account for vertical scrollbar */
    padding-right: 20px;
</style>

<script type="text/javascript">
var emailReport,ReportWindow;

	$(function()
		{
			var POSDate="${gPOSDate}"
			$('#lblDayEndDate').html(POSDate);
		
			$('#lblTotalPax').val("${command.totalpax}");
			
			var POSName="${gPOSName}"
			$('#lblPOSName').html(POSName);
		});
	
	$(document).ready(function()
			{
		
			var gDayEnd='${gDayEnd}'; /*BCOZ in pos global veriale DayEnd initilize as N '${gDayEnd}'; */
			var gShiftEnd='${gShiftEnd}';
			
				if (gShiftEnd==("") && gDayEnd==("N"))
		        {
					document.getElementById("btnEndDay").style.color = "black"; 
					document.getElementById("btnEndDay").disabled=true; 
				}
		        else if (gDayEnd==("N") && gShiftEnd==("N"))
		        {
		        	document.getElementById("btnStart").style.color = "black"; 
		        	document.getElementById("btnStart").disabled=true;
		        }
			});
	
	
	function funStart()
	{
		
		var DayEnd='${gDayEnd}'  /*BCOZ in pos global veriale DayEnd initilize as N '${gDayEnd}'; */
		var ShiftEnd='${gShiftEnd}';
		//alert('DayEndShiftEnd');
		
		 if (DayEnd=="N" && ShiftEnd==("N"))
			 {  
			 		alert('Already Day started');
			 }
		 else{
			var searchurl=getContextPath()+"/StartDayProcess.html?";
			$.ajax({
			        type: "GET",
		    	    url: searchurl,
		        	dataType: "json",
		        
		        	success: function (response) {
		        		var startday=response.DayStart;
		        			alert(startday);
		        			document.getElementById("btnStart").disabled=true;
		        			location.reload();
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
		 
		 
	}
	function funEndDay()
	{
		var strPOSCode='${gPOSCode}';
		var ShiftNo="1";
		var POSDate="${gPOSDate}"
		var strURL=getContextPath()+"/CheckBillSettleBusyTable.html?";

		$.ajax({
			type: "GET",
    	    url: strURL,
        	dataType: "json",
        	contentType: 'application/json',
			success :function(response)
			{		
				
					var checkBills='Y';
					var checkTables='Y';
					
					if(response.PendingBills==true)
						{
						  alert('Please settle pending bills');	
						}
					else{
							checkBills='N';//funEndDayProcess();
						}
					if(response.BusyTables==true)
						{
							alert('Sorry Tables are Busy Now');	
						}
					else{
							checkTables='N';//funEndDayProcess();
						} 
					
					if(checkBills=='N' && checkTables=='N')
						{
							 emailReport = confirm("Do you want Email Report ?");
							
			                if( emailReport == true )
			                {
			                	emailReport="Y";
			                	//window.open(url, windowName, windowFeatures, optionalArg4)
			                	ReportWindow= window.open("frmPOSDayEndDialog.html","","dialogHeight:400px;dialogWidth:400px;dialogLeft:400px;")
			                	
			                	//var list=response.
			                	//alert(response);
			                	//funEndDayProcess(emailReport);
			                	//document.write ("User wants Email Report!");
			                  	return true;
			               	}
			                else{
			                	emailReport="N";
			                	funEndDayProcess(emailReport);
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
		
		//funEndDay();
	}
	function funEndDayProcess(emailReport)
	{
		var searchurl=getContextPath()+"/EndDayProcess.html?emailReport="+emailReport;
		$.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        async:false,
		        success: function (response) {
		        	alert(response.msg);
	        		var isdayEnd='<%=session.getAttribute("gDayEnd").toString()%>';
	        		if(response.msg=="Succesfully Day End")
	        		{
	        			window.location ="logout.html";
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

	function funSetData()
	{
		ReportWindow.close();
		funEndDayProcess(emailReport);			
	}
	</script>
	
<body> <!--  onload="funLoadAllData()"> -->
	
	<div id="formHeading">
		<label>Day End Process</label>
	</div>
	
		<s:form name="POSDayEndProcess" method="POST" action="" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
			
			<div class="title" >
			
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;">
						<div class="element-input col-lg-6" style="width: 45%;"> 
		    					<label class="title" >Shift No - 1</label>
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 35%;margin-bottom: 10px;">
		    					<label id="lblDayEndDate" />
		    			</div>
		    			<div class="element-input col-lg-6" style="width: 30%;margin-bottom: 10px;">
		    					<label id="lblPOSName" />
		    			</div>
	    		</div>
	    		
	    		<div class="row" style="background-color: #fff;display: -webkit-box;">
					<table style="height: 20px; border: 1px solid black; width: 100%;font-size:11px;
									font-weight: bold; table-layout: fixed; overflow: scroll" >
						<thead style="background-color: #85cdffe6;">
							<tr>
									<td width="9%" style="border: 1px solid black;">Settlement Mode</td>					
									<td align ="right" width="9%" style="border: 1px solid black;">Cash(Sales)</td>	
									<td align ="right" width="9%" style="border: 1px solid black;">Float</td>
									<td align ="right" width="9%" style="border: 1px solid black;">TransIn</td>
									<td align ="right" width="9%" style="border: 1px solid black;">Advance</td>
									<td align ="right" width="9%" style="border: 1px solid black;">TotalRec</td>
									<td align ="right" width="9%" style="border: 1px solid black;">Payments</td>
									<td align ="right" width="9%" style="border: 1px solid black;">TransOuts</td>
									<td align ="right" width="9%" style="border: 1px solid black;">Withdraw</td>
									<td align ="right" width="9%" style="border: 1px solid black;">TotalPay</td>
									<td align ="right" width="9%" style="border: 1px solid black;">CashInHand</td>
			 		 		</tr>
			 		 	</thead>
					</table>
				</div>	
				
				<div class="row" style="background-color: #fff;display: -webkit-box;">	
				
					<div style="height: 90px; overflow-x: hidden; overflow-y: scroll; width: 100%; border: 1px solid #ccc;">
								
						<table id="tblDayEnd" style="width: 100%;  table-layout: fixed; overflow: scroll">
							<tbody style="border-top: none;">
								<c:forEach var="obj"  items="${command.listDayEnd}" varStatus="">
							 		<tr>
										<c:forEach var="ob" items="${obj}" varStatus="">
												<td align ="right">${ob}</td> 
										</c:forEach>
							 		 </tr> 
								</c:forEach>
							</tbody>
						</table>
						
					</div>
					
	    		</div>
	    		
	    		<br/>
	    		
	    		<div class="row" style="background-color: #fff;display: -webkit-box;">
	    		
	    			<div style="border: 1px solid #ccc; height: 50px; 
								margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
								
						<table id="tblDayEndTotal" style="width: 100%; height: 50%;table-layout: fixed; overflow: scroll">
							<tbody style="border-top: none;">
								<c:forEach var="obj1"  items="${command.listDayEndTotal}" varStatus="">
									<tr>
										<c:forEach var="ob1"  items="${obj1}" varStatus="">
											<td align ="right">${ob1}</td>
										</c:forEach>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						
					</div>
					
	    		</div>
	    		
	    		<br/>
	    		
	    		<div class="row" style="background-color: #fff;display: -webkit-box;">
	    		
		    		<div id="divSettlement" style="width: 49%; height: 120px;margin-right: 5px;">
		    		
						<table style="height: 20px; width: 100%;font-size:11px;font-weight: bold; table-layout: fixed; overflow: scroll">
							<thead style="background-color: #85cdffe6;">
								<tr>
									<td width="9%" style="border: 1px solid black;">Settlement Mode</td>					
									<td align ="right" width="9%" style="border: 1px solid black;">Amount</td>	
									<td align ="right" width="9%" style="border: 1px solid black;">Discount</td>
								</tr>
							</thead>
						 </table>
						
						 <div style="border: 1px solid #ccc; height: 80px; margin: auto; 
									overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
								<table id="tblSettlement" style="width: 100%; table-layout: fixed; overflow: scroll">
									   <c:forEach var="obj2"  items="${command.listSettlement}" varStatus="">
											<tr>
												   <c:forEach var="ob2"  items="${obj2}" varStatus="">
														<td align ="right">${ob2}</td>
													</c:forEach>
											</tr>
										</c:forEach>
								</table>
						 </div>
						 
					 </div>
					 
					 <div id="divSettlementTot" style="width: 50%; height: 120px;margin-left: 5px;">
					 	<table style="height: 20px; width: 100%;font-size:11px;font-weight: bold; table-layout: fixed; overflow: scroll">
							<thead style="background-color: #85cdffe6;">
								<tr>
									<td width="9%" style="border: 1px solid black;">Description</td>					
									<td align ="right" width="9%" style="border: 1px solid black;">Amount</td>	
									<td align ="right" width="9%" style="border: 1px solid black;">Bill  &nbsp &nbsp</td>
								</tr>
							</thead>
						 </table>
					 	<div id="divSettlementTotal" style="width: 100%; height: 97px;border: 1px solid #ccc;">
							<table id="tblSettlementTotal" style="width: 97%; table-layout: fixed; overflow: scroll">
								<tbody style="border-top: none;">
										<c:forEach var="obj3"  items="${command.listSettlementTotal}" varStatus="">
											<tr>
												<c:forEach var="ob3"  items="${obj3}" varStatus="">
													<td align ="right">${ob3}</td>
												</c:forEach>
											</tr>
										</c:forEach>
								</tbody>
							</table>
						</div>
						
					</div>
					
				</div>
				
				<div class="row" style="background-color: #fff;display: -webkit-box;">
	    		
		    		<div id="divSalesUP" style="width: 49%;margin-right: 5px;">
		    		
		    			<div class="element-input col-lg-6" > 
		    					<label class="title" >Sales Under Progress</label>
		    			</div>
		    		
						<table style="height: 20px;width: 100%;font-size:11px;font-weight: bold; table-layout: fixed; overflow: scroll" >
							<thead style="background-color: #85cdffe6;">
								<tr>
									<td style="border: 1px solid black;text-align: right; width: 8%; ">Table Name</td>					
									<td style="border: 1px solid black;text-align: right; width:8.7% ">Amount</td>	
								</tr>
							</thead>
						</table>
						
						 <div style="height: 125px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 100%;border: 1px solid #ccc;">
								
								<table id="tblSalesUP" style="width: 100%; table-layout: fixed; overflow: scroll">
									<tbody style="border-top: none;">
										<c:forEach var="obj4"  items="${command.listSalesInProg}" varStatus="">
											<tr>
												<c:forEach var="ob4"  items="${obj4}" varStatus="">
													<td align ="right">${ob4}</td>
												</c:forEach>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								
						 </div>
						 
						 <div style="width: 50%;float: right;">
						 
							 <div class="element-input col-lg-6" style="width: 35%;"> 
			    					<label class="title" >Total</label>
			    			</div>
			    			<div class="element-input col-lg-6" style="width: 55%;"> 
			    					<label id="lblTotal" style="font-size:15px; text-align: right;">${command.total} </label>
			    			</div>
			    			
						</div>
				 </div>
					 
			<div id="divUnsettle" style="width: 50%;margin-left: 5px;">
					 
				 <div id="divUnsettleBill" style="width: 100%; height: 155px;">
				 		
				 		<div class="element-input col-lg-6" > 
		    					<label class="title" >UnSettle Bills</label>
		    			</div>
							
							<table style="height: 20px; width: 100%;font-size:11px; font-weight: bold; table-layout: fixed; overflow: scroll" >
								<thead style="background-color: #85cdffe6;">
									<tr>
										<td style="border: 1px solid black; text-align: right; width: 9%; ">No Of Bills</td>					
										<td style="border: 1px solid black; text-align: right; width: 9%; ">Table Name</td>	
										<td style="border: 1px solid black; text-align: right; width: 10%; ">Bill Amount</td>
									</tr>
								<thead>
							</table>
							
						<div style="height: 125px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;border: 1px solid #ccc;">
						
							<table id="tblUnsettleBill" style="width: 100%; table-layout: fixed; overflow: scroll">
								<tbody style="border-top: none;">
									<c:forEach var="obj5"  items="${command.listUnSettlebill}" varStatus="">
										<tr>
											<c:forEach var="ob5"  items="${obj5}" varStatus="">
												<td align ="right">${ob5}</td>
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						</div>
						
						<div style="float: right;width: 60%;">
						
							<div class="element-input col-lg-6" style="width: 35%;" > 
			    					<label class="title" >Total Pax</label>
			    			</div>
			    			<div class="element-input col-lg-6" style="width: 55%;"> 
			    					<label id="lblTotalPax" style="font-size:15px; text-align: right;">${command.totalpax}</label>
			    			</div>
			    			
			    		</div>
						
					</div>
						
			  </div>
					
		</div>
		
		<br/>
		
		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%; margin-left: 30%;">
     		  		<p align="center">
            			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnStart" type="button" value="START" onclick="funStart()" /></div>
            			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnEndDay" type="button" value="END DAY" onclick="funEndDay()"/></div>
     		  		</p>
   		</div>
		
	    	
	 </div>
		
	</s:form>

</body>
</html>