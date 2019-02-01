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
<title> Day End Consolidate </title>
</head>
<script type="text/javascript">
	
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
			var searchurl=getContextPath()+"/consolidateStartDayProcess.html?";
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
		var strURL=getContextPath()+"/ConsolidateCheckBillSettleBusyTable.html?";

		$.ajax({
			type: "GET",
    	    url: strURL,
        	dataType: "json",
			
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
							 emailReport = "N"//confirm("Do you want Email Report ?");
							
			                if( emailReport == true )
			                {
			                	emailReport="Y";
			                	//window.open(url, windowName, windowFeatures, optionalArg4)
			                	//ReportWindow= window.open("frmPOSDayEndDialog.html","","dialogHeight:400px;dialogWidth:400px;dialogLeft:400px;")
			                	
			                	//var list=response.
			                	//alert(response);
			                	funEndDayProcess(emailReport);
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
		var searchurl=getContextPath()+"/ConsolidateEndDayProcess.html?emailReport="+emailReport;
		$.ajax({
		        type: "GET",
	    	    url: searchurl,
	        	dataType: "json",
	        	success: function (response) {
	        		alert(response.msg);
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
		
<body> 

	
	<div id="formHeading">
		<label>Day End Consolidate</label>
	</div>

	
	<s:form name="POSDayEndConsolidate" method="POST" action="DayEndConsolidate.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
		
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
								<c:forEach var="objCon"  items="${command.listDayEnd}" varStatus="">
									<tr>
										<c:forEach var="obCon" items="${objCon}" varStatus="">
											<td align ="right">${obCon}</td> 
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
								<c:forEach var="objCon1"  items="${command.listDayEndTotal}" varStatus="">
									<tr>
										<c:forEach var="obCon1"  items="${objCon1}" varStatus="">
											<td align ="right">${obCon1}</td>
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
									<td align ="right" width="9%" style="border: 1px solid black;">No Of Bills</td>
								</tr>
							</thead>
						 </table>
						
						 <div style="border: 1px solid #ccc; height: 80px; margin: auto; 
									overflow-x: hidden; overflow-y: scroll; width: 99.80%;">
								<table id="tblSettlement" style="width: 100%; table-layout: fixed; overflow: scroll">
									   <c:forEach var="objCon2"  items="${command.listSettlement}" varStatus="">
											<tr>
												<c:forEach var="obCon2"  items="${objCon2}" varStatus="">
													<td align ="right">${obCon2}</td>
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
							<table id="tblSettlementTotal" style="width: 100%; table-layout: fixed; overflow: scroll">
								<tbody style="border-top: none;">
										<c:forEach var="objCon3"  items="${command.listSettlementTotal}" varStatus="">
											<tr>
												<c:forEach var="obCon3"  items="${objCon3}" varStatus="">
													<td align ="right">${obCon3}</td>
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
										<c:forEach var="objCon4"  items="${command.listSalesInProg}" varStatus="">
											<tr>
												<c:forEach var="obCon4"  items="${objCon4}" varStatus="">
													<td align ="right">${obCon4}</td>
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
									<c:forEach var="objCon5"  items="${command.listUnSettlebill}" varStatus="">
										<tr>
											<c:forEach var="obCon5"  items="${objCon5}" varStatus="">
												<td align ="right">${obCon5}</td>
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
	            			<div class="submit col-lg-4 col-sm-4 col-xs-4"><input id="btnEndDay" type="button" value="EndDay" onclick="funEndDay()"/></div>
	     		  		</p>
	   		  </div>
	    		
	    </div>
	    
	    
	    
		
<!-- 			<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td align="center"> -->
<!-- 				<b><label  id="lblShiftNo" style="font-size:15px;">Shift No - 1</label></b> -->
<!-- 				</td> -->
<!-- 				<td align="center"> -->
<!-- 				<b><label id="lblDayEndDate" style="font-size: 15px;  text-align: right;"></label></b> -->
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 			<td></td><td></td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 			<td></td><td></td> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 			<td colspan="2"> -->
<!-- 			<div id="divDayEnd" style="width: 100%; height: 200px;"> -->
<!-- 				<table class="transTablex" style="height: 20px; border: #0F0;width: 99%;font-size:11px; -->
<!-- 					font-weight: bold; table-layout: fixed; overflow: scroll" > -->
<!-- 					<tr bgcolor="#72BEFC"> -->
<!-- 						<td width="9%">Settlement Mode</td>					 -->
<!-- 						<td align ="right" width="9%">Cash(Sales)</td>	 -->
<!-- 						<td align ="right" width="9%">Float</td> -->
<!-- 						<td align ="right" width="9%">TransIn</td> -->
<!-- 						<td align ="right" width="9%">Advance</td> -->
<!-- 						<td align ="right" width="9%">TotalRec</td> -->
<!-- 						<td align ="right" width="9%">Payments</td> -->
<!-- 						<td align ="right" width="9%">TransOuts</td> -->
<!-- 						<td align ="right" width="9%">Withdraw</td> -->
<!-- 						<td align ="right" width="9%">TotalPay</td> -->
<!-- 						<td align ="right" width="9%">CashInHand</td> -->
<!-- 			 		 </tr> -->
<!-- 				</table> -->
<!-- 				<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 90px; margin: auto;  -->
<!-- 				overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 					<table id="tblDayEnd" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex"> -->
<%-- 							<c:forEach var="objCon"  items="${command.jArrDayEnd}" varStatus=""> --%>
<!-- 							<tr> -->
<%-- 							<c:forEach var="obCon" items="${objCon}" varStatus=""> --%>
<%-- 							<td align ="right">${obCon}</td>  --%>
<%-- 								</c:forEach> --%>
<!-- 							</tr>  -->
<%-- 						</c:forEach> --%>
					
						
<!-- 					</table> -->
<!-- 				</div> -->
<!-- 				&emsp;&ensp;&emsp;&ensp;  -->
<!-- 				&emsp;&ensp;&emsp;&ensp; -->
<!-- 				<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 50px;  -->
<!-- 				margin: auto; overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 				<table id="tblDayEndTotal" -->
<!-- 					style="width: 100%; height: 50%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 					class="masterTable"> -->
<%-- 					<c:forEach var="objCon1"  items="${command.jArrDayEndTotal}" varStatus=""> --%>
<!-- 						<tr> -->
<%-- 							<c:forEach var="obCon"  items="${objCon1}" varStatus=""> --%>
<%-- 								<td align ="right">${obCon1}</td> --%>
<%-- 									</c:forEach> --%>
<!-- 							</tr> -->
<%-- 						</c:forEach> --%>
<!-- 				</table> -->
<!-- 			</div> -->
<!-- 			</div> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 		<td colspan="2"></td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 			<td> -->
<!-- 				<div id="divSettlement" style="width: 100%; height: 120px;"> -->
<!-- 					<table style="height: 20px; border: #0F0;width: 99%;font-size:11px; -->
<!-- 					font-weight: bold; table-layout: fixed; overflow: scroll" class="transTablex" > -->
<!-- 					<tr bgcolor="#72BEFC"> -->
<!-- 						<td width="9%">Settlement Mode</td>					 -->
<!-- 						<td align ="right" width="9%">Amount</td>	 -->
<!-- 						<td align ="right" width="9%">No Of Bills</td> -->
<!-- 					</tr> -->
<!-- 					</table> -->
<!-- 					<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 80px; margin: auto;  -->
<!-- 					overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 					<table id="tblSettlement" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex"> -->
<%-- 						<c:forEach var="objCon2"  items="${command.jArrSettlement}" varStatus=""> --%>
<!-- 						<tr> -->
<%-- 							<c:forEach var="obCon2"  items="${objCon2}" varStatus=""> --%>
<%-- 								<td align ="right">${obCon2}</td> --%>
<%-- 									</c:forEach> --%>
<!-- 							</tr> -->
<%-- 						</c:forEach> --%>
<!-- 						</table> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 			<td> -->
<!-- 			&emsp;&ensp;&emsp;&ensp;  -->
<!-- 			&emsp;&ensp;&emsp;&ensp; -->
<!-- 			&emsp;&ensp;&emsp;&ensp; -->
<!-- 				<div id="divSettlementTotal" style="width: 100%; height: 130px;"> -->
<!-- 					<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 80px; margin: auto;  -->
<!-- 					overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 					<table id="tblSettlementTotal" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex"> -->
<!-- 						<tr></tr> -->
<%-- 						<c:forEach var="objCon3"  items="${command.jArrSettlementTotal}" varStatus=""> --%>
<!-- 						<tr> -->
<%-- 							<c:forEach var="obCon3"  items="${objCon3}" varStatus=""> --%>
<%-- 								<td align ="right">${obCon3}</td> --%>
<%-- 									</c:forEach> --%>
<!-- 							</tr> -->
<%-- 						</c:forEach> --%>
<!-- 						</table> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 		<td colspan="2"></td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 			<td> -->
<!-- 				<b><label style="font-size:15px; align:center">Sales Under Progress</label></b> -->
<!-- 				<div id="divSalesUP" style="width: 100%; height: 155px;"> -->
<!-- 					<table style="height: 20px; border: #0F0;width: 99%;font-size:11px; -->
<!-- 					font-weight: bold; table-layout: fixed; overflow: scroll" class="transTablex" > -->
<!-- 					<tr bgcolor="#72BEFC"> -->
<!-- 						<td align ="right" width="9%">Table Name</td>					 -->
<!-- 						<td align ="right" width="9%">Amount</td>	 -->
<!-- 					</tr> -->
<!-- 					</table> -->
<!-- 					<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 125px; margin: auto;  -->
<!-- 					overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 					<table id="tblSalesUP" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 						class="transTablex"> -->
<%-- 						<c:forEach var="objCon4"  items="${command.jArrSalesInProg}" varStatus=""> --%>
<!-- 						<tr> -->
<%-- 							<c:forEach var="obCon4"  items="${objCon4}" varStatus=""> --%>
<%-- 								<td align ="right">${obCon4}</td> --%>
<%-- 							</c:forEach> --%>
<%-- 									<td>${obj4}</td> --%>
<!-- 							</tr> -->
<%-- 						</c:forEach> --%>
<!-- 						</table> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 			<td> -->
<!-- 			<b><label style="font-size:15px; align:center;"> UnSettle Bills</label></b> -->
<!-- 					<div id="divUnsettleBill" style="width: 100%; height: 155px;"> -->
<!-- 						<table style="height: 20px; border: #0F0;width: 99%;font-size:11px; -->
<!-- 						font-weight: bold; table-layout: fixed; overflow: scroll" class="transTablex" > -->
<!-- 						<tr bgcolor="#72BEFC"> -->
<!-- 							<td align ="right" width="9%">No Of Bills</td>					 -->
<!-- 							<td align ="right" width="9%">Table Name</td>	 -->
<!-- 							<td align ="right" width="9%">Bill Amount</td> -->
<!-- 						</tr> -->
<!-- 						</table> -->
<!-- 					<div style="background-color: #C0E2FE; border: 1px solid #ccc; height: 125px; margin: auto;  -->
<!-- 						overflow-x: hidden; overflow-y: scroll; width: 99.80%;"> -->
<!-- 						<table id="tblUnsettleBill" style="width: 100%; border: #0F0; table-layout: fixed; overflow: scroll" -->
<!-- 							class="transTablex"> -->
<%-- 							<c:forEach var="objCon5"  items="${command.jArrUnSettlebill}" varStatus=""> --%>
<!-- 						<tr> -->
<%-- 							<c:forEach var="obCon5"  items="${objCon5}" varStatus=""> --%>
<%-- 								<td align ="right">${obCon5}</td> --%>
<%-- 									</c:forEach> --%>
<!-- 							</tr> -->
<%-- 						</c:forEach> --%>
<!-- 						</table> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 		<td>&emsp;&ensp;&emsp;&ensp;  -->
<!-- 				&emsp;&ensp;&emsp;&ensp; -->
<!-- 			<b><label style="font-size:15px; align:center;"> Total </label>&emsp;&ensp;&emsp;&ensp; -->
<%-- 			<label id="lblTotal" style="font-size:15px; align:center;">${command.total} </label> --%>
<!-- 			</b>  -->
<!-- 		</td> -->
<!-- 		<td>&emsp;&ensp;&emsp;&ensp;  -->
<!-- 				&emsp;&ensp;&emsp;&ensp; -->
<!-- 			<b><label style="font-size:15px; align:center;"> Total Pax </label>&emsp;&ensp;&emsp;&ensp; -->
<%-- 			<label id="lblTotalPax" style="font-size:15px; align:center;">${command.totalpax}</label> --%>
<!-- 			</b>  -->
<!-- 		</td> -->
<!-- 		</tr> -->
<!-- 		<tr><td colspan="2"></td> -->
<!-- 		</tr> -->
<!-- 		<tr> -->
<!-- 		<td></td> -->
<!-- 		<td> -->
<!-- 			&emsp;&ensp;&emsp;&ensp;  -->
<!-- 			&emsp;&ensp;&emsp;&ensp; -->
<!-- 			<input id="btnStart" type="button" value="START"  class="form_button1" onclick="funStart()" /> -->
				
<!-- 				&emsp;&ensp;&emsp;&ensp;	&emsp;&ensp;&emsp;&ensp; -->
			
<!-- 			<input id="btnEndDay" type="button" value="EndDay" class="form_button1" onclick="funEndDay()"/> -->
			
			
			
<!-- 			<!-- <input id="btnEnd"  type="button" value="  End"   onclick="funEnd()" style="height:30px; background: url(./resources/images/big2.png) no-repeat; -->
<!-- 				 background-size: 90px 30px;width: 90px;color: #fff;font-size: 13px; font-weight: normal;" /> --> 
<!-- 		</td> -->
<!-- 		</tr> -->
<!-- 		</table> -->
					
		
	</s:form>

</body>

</html>