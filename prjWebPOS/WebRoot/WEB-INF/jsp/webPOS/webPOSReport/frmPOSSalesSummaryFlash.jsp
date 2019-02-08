<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sales Summary Flash</title>
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
	var message='';
	<%if (session.getAttribute("success") != null) {
        if(session.getAttribute("successMessage") != null){%>
        message='<%=session.getAttribute("successMessage").toString()%>';
        <%
        	session.removeAttribute("successMessage");
        }
		boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
		session.removeAttribute("success");
		if (test) {
		%>	
		alert(message);
	<%
	}}%>		

	$(document).ajaxStart(function() {
		$("#wait").css("display", "block");
	});
	$(document).ajaxComplete(function() {
		$("#wait").css("display", "none");
	});

	 $(function() 
	 	{		
	        var POSDate="${POSDate}"
		    var startDate="${POSDate}";
		  	var Date = startDate.split(" ");
			var arr = Date[0].split("-");
			Dat=arr[2]+"-"+arr[1]+"-"+arr[0];	
			$("#txtFromDate" ).datepicker({ dateFormat: 'dd-mm-yy' });		
			$("#txtFromDate" ).datepicker('setDate', Dat); 
			$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
			$("#txtToDate" ).datepicker('setDate', Dat); 
			funExecute();
	 	});
	 
	 $("form").submit(function(event)
		{
			var table = document.getElementById("tblsalesSumFlash");
			var rowCount = table.rows.length;
			if (rowCount > 1){
				$("#txtFromDate").val(fDate);
				$("#txtToDate").val(tDate);
				return true;
			} else {
				alert("Data Not Available");
				return false;
			}
		});
});
	 
	 function funExecute(){
		 
		 var fromDate = $('#txtFromDate').val();
		 var toDate = $('#txtToDate').val();
		 var payMode = $('#cmbPaymentMode').val();
		 var posName = $('#cmbPOSName').val();
		 var reportType=$('#cmbDocType').val();
		 $('#tblsalesSumFlash tbody').empty();		 
		 $('#tblTotal tbody').empty();
		 funFillTableData(fromDate,toDate,payMode,posName,reportType); 
		 
	 }
		
	 
	 
	 function funFillTableData(fromDate,toDate,payMode,posName,reportType)
	 {
		 var searchurl=getContextPath()+"/loadColumnData.html?payMode=" + payMode;
		 $.ajax({
	        type: "GET",
	        url: searchurl,
	        dataType: "json",
	        
	        success: function (response) 
	        {
	        	funfillColumnData(response,reportType);
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
	       
 		 funFillTableRow(fromDate,toDate,payMode,posName,reportType);
	 }
	
	 /* function funfillColumnData(colData,reportType)
	 {
			var table = document.getElementById("tblsalesSumFlash");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
			if(reportType=='Daily')  
			{
		    row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosCode >";
		    row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosName >";
		    row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosDate >";
			}else{
				row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosName >";
			    row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=Month >";
			    row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=Year >";		
			}
		    
		    for(var i=0;i<colData.length;i++){
			row.insertCell(i+3).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value='"+colData[i]+"' >";		
		}
	 } */
	 
	 function funfillColumnData(colData,reportType)
	 {
			var table = document.getElementById("tblsalesSumFlash");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    var paymod=$('#cmbPaymentMode').val();
			if(reportType=='Daily')  
			{
				if(paymod=="ALL")
				{
					 row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosCode >";
			         row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosName >";
			         row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosDate >";
					
	            }
				else
				{
					 row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosCode >";
					 row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosName >";
					 row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosDate >";  
				}
					
			
			}
			else
			{
				if(paymod=="ALL")
				{
					 row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosCode >";
			         row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosName >";
			         row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value=PosDate >";
					
	            }
				else
				{
					 row.insertCell(0).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosCode >";
					 row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosName >";
					 row.insertCell(2).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value=PosDate >";  
				}
				
			}
		    
		    for(var i=0;i<colData.length;i++){
		    	if(paymod=="ALL")
		    	{
		    		row.insertCell(i+3).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"15%\" id=\""+(rowCount)+"\" value='"+colData[i]+"' >";	
		    	
		    	}
		    	else
		    	{
		    		row.insertCell(i+3).innerHTML= "<input type=\"text\" readonly=\"readonly\"  class=\"header \" size=\"26%\" id=\""+(rowCount)+"\" value='"+colData[i]+"' >";			
		    	}
			
		}
	 }
	 

	 
	 /* function funFillTableRow(fromDate,toDate,payMode,posName,reportType){
		 
		 var searchurl=getContextPath()+"/loadPaymentData.html?fromDate=" + fromDate+"&toDate="+toDate+"&payMode="+payMode+"&posName="+posName+"&reportType="+reportType;
		 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        
		        success: function (response) {
		        
		        	var tableHeaderData = document.getElementById("tblsalesSumFlash");
		    		var colCount=tableHeaderData.rows[0].cells.length;
		    		var table = document.getElementById("tblsalesSumFlash");
		    	 	var rowCount = table.rows.length;
		        	
		        	$.each(response.List,function(i,item)
		        	{
		              row = table.insertRow(rowCount);
		  		 	  for(var i=0;i<colCount;i++)
		  		 	  {
		  		 		  if(i>2)
		  		 		  {
		  		 			row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"15%\" id=\""+(i)+"\" value='"+item[i]+"'>";
		  		 		  }
		  		 		  else
		  		 		  {
		  		 			row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(i)+"\" value='"+item[i]+"'>";  
		  		 		  }	
		  		 	  }
			  		 	 rowCount++;
			  		 	 i--;
 		            });
		        	
		        	
		        	funFillTotalCol(response.totalList); 
		        
		    
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
	 } */
	 
function funFillTableRow(fromDate,toDate,payMode,posName,reportType){
		 
		 var searchurl=getContextPath()+"/loadPaymentData.html?fromDate=" + fromDate+"&toDate="+toDate+"&payMode="+payMode+"&posName="+posName+"&reportType="+reportType;
		 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        
		        success: function (response) {
		        
		        	var tableHeaderData = document.getElementById("tblsalesSumFlash");
		    		var colCount=tableHeaderData.rows[0].cells.length;
		    		var table = document.getElementById("tblsalesSumFlash");
		    	 	var rowCount = table.rows.length;
		    	 	var paymod1=$('#cmbPaymentMode').val();
		        	
		        	$.each(response.List,function(i,item)
		        	{
		              row = table.insertRow(rowCount);
		  		 	  for(var i=0;i<colCount;i++)
		  		 	  {
		  		 		if(paymod1=="ALL")
		  		 		{

		  		 			if(i==2)
		  		 			{
		  		 				   var arr1 = item[i].split("-");
		  		 				   var date1=arr1[2]+"-"+arr1[1]+"-"+arr1[0];
				  			       row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:center;\" id=\""+(i)+"\" value='"+date1+"'>";
				  			}
		  		 			else
		  		 			{
		  		 				
				  			       row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"15%\" id=\""+(i)+"\" value='"+item[i]+"'>";

		  		 			}

		  		 		}
		  		 		else
		  		 		{ 
		  		 			if(i==2)
		  		 			{
		  		 				   var arr2 = item[i].split("-");
		  		 				   var date2=arr2[2]+"-"+arr2[1]+"-"+arr2[0];
				  			       row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:center;\" id=\""+(i)+"\" value='"+date2+"'>";

		  		 			}
		  		 			else
		  		 			{

			  		 			if(i==3)
			  		 				{
			  		 			         row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"32%\" id=\""+(i)+"\" value='"+item[i]+"'>";
					  			    }
			  		 			else
			  		 				{
			  		                     row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"38%\" id=\""+(i)+"\" value='"+item[i]+"'>";
                                    }
			  		 				
			  		 	    }
                       }
		  		 			
		            }
			  		 	 rowCount++;
			  		 	 i--;
 		            });
		        	
		        	
		        	funFillTotalCol(response.totalList); 
		        
		    
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
	 
	 
	 function funFillTotalCol(rowData) 
		{
		 	var table = document.getElementById("tblTotal");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    for(var i=0;i<rowData.length;i++)
	    	 {
	   		    if(i==0)
	   			{
	   				row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \"  style=\"border:none;border-radius:0px; text-align:left;color:black;font;font-weight: bold;\" size=\"18%\" id=\""+(i)+"\" value='Total'>";
	   			}
	   			else if(i==1)
	   			{
	   				row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \"  style=\"border:none;border-radius:0px; text-align:left;color:black;font;font-weight: bold;\" size=\"18%\" id=\""+(i)+"\" value=''>";
	   			}
	   			else if(i==2)
	   			{
	   				row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \"  style=\"border:none;border-radius:0px; text-align:left;color:black;font;font-weight: bold;\" size=\"18%\" id=\""+(i)+"\" value=''>";
	   			}
	   			else
	   			{
		 			row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \"  style=\"border:none;border-radius:0px; text-align:right;color:black;font;font-weight: bold;\" size=\"19%\" id=\""+(i)+"\" value='"+rowData[i]+"'>";
		 		}	
	   			
	   		 }
		}
	 
// 	 function funfillRowData(dteBillDate,strPosName,strSettelmentDesc,strPOSCode,dblSettlementAmt)
 function funfillRowData(data)
	 {
 	 	
	    var tableHeaderData = document.getElementById("tblsalesSumFlash");
		var colCount=tableHeaderData.rows[0].cells.length;
		var table = document.getElementById("tblsalesSumFlash");
	 	var rowCount = table.rows.length;
	 	
	 	
	 	var row ;
		
		$.each(data.listArr,function(i,item){
        	
			row = table.insertRow(rowCount);
		 	  for(var i=0;i<colCount;i++)
		 	  {
		 		row.insertCell(i).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(cnt)+"\" value='"+data[cnt]+"'>";
		 		cnt++;
		 	  }
		 	 rowCount++;
		 	 cnt--;
    	});
 	
		
		var table = document.getElementById("tblsalesSumFlash");
		var col=table.rows[0].cells.length;
	 	var rowCoun = table.rows.length;
	 	var row1 = table.insertRow(rowCoun);
	 	row1.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(j)+"\" value='"+ +"'>";
		row1.insertCell(1).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(j)+"\" value='"+ +"'>";
		row1.insertCell(2).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(j)+"\" value='Total'>";
	 	for(var j=3;j<col;j++)
		{
			var amount=0.0;
			
		   for(var i=1;i<rowCoun;i++)
		   {
			 
				  var description=table.rows[i].cells[j].innerHTML;
				  var data=description.split('value');
				  var dblAount=data[1].split('=');
				  var amountData=parseFloat(dblAount[1].substring(1,dblAount[1].length-2));
				  amount=amount+amountData;
		   }
		   row1.insertCell(j).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"15%\" id=\""+(j)+"\" value='"+amount+"'>";
		}
		
	 }
		
 function funRemoveTableRows(tableId)
	{
		var table = document.getElementById(tableId);
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}	 	 	 

 function DeleteRows() {
     var rowCount = tblsalesSumFlash.rows.length;
     for (var i = rowCount - 1; i > 0; i--) {
    	 tblsalesSumFlash.deleteRow(i);
     }
     $('#tblTotal tbody').empty();
 }
 
	 
	</script>






</head>

<body>
	<div id="formHeading">
		<label>Sales Summary Flash</label>
	</div>
	
	<s:form name="SalesSummaryFlash" method="POST"
		action="processSalesSummeryFlash1.html?saddr=${urlHits}" target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">


	<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}"  style="width: 100%;"/>	
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Payment Mode</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbPaymentMode" name="cmbReportType" path="strPayMode" items="${payModeList}" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Report Type</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbDocType" path="strDocType">
									<s:option value="Daily">Daily</s:option>
									<s:option value="Monthly">Monthly</s:option>
								</s:select>
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 200px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblsalesSumFlash" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 50px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblTotal" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>	
					
					<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%; margin-left: 10%;">
					
						<p align="center">
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="EXECUTE" onClick="funExecute()" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="EXPORT" id="submit" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" onclick="funResetFields()" /></div>
						</p>
						
				    </div>
			
	 </div>
	
	</s:form>

</body>
</html>