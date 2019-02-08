<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>POS Wise Report</title>
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

.cell {
	background: inherit;
	border: 0 solid #060006;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	outline: 0 none;
	padding-left: 0;
	width: 100%
}

.header {
	background: inherit;
	border: 0 solid #060006;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-weight: bold;
	outline: 0 none;
	padding-left: 0;
	width: 100%;
	height:100%
}
</style>
<script type="text/javascript">



var date="";
$(document).ready(function() {
	
	var POSDate="${POSDate}"
    var startDate="${POSDate}";
  	var Date = startDate.split(" ");
	var arr = Date[0].split("-");
	Dat=arr[2]+"-"+arr[1]+"-"+arr[0];
	$("#txtFromDate" ).datepicker({ dateFormat: 'dd-mm-yy' });		
	$("#txtFromDate" ).datepicker('setDate', Dat); 
	$("#txtToDate").datepicker({ dateFormat: 'dd-mm-yy' });
	$("#txtToDate" ).datepicker('setDate', Dat); 
	var fromDate = Dat;
	var toDate = Dat;
	funLoadTableData(fromDate,toDate);
	
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
	
	$(document).ajaxStart(function() {
		$("#wait").css("display", "block");
	});
	$(document).ajaxComplete(function() {
		$("#wait").css("display", "none");
	});

	
	$("[type='reset']").click(function(){
		location.reload(true);
	});

	$("#execute").click(function(event) {
		var fromDate = $("#txtFromDate").val();
		var toDate = $("#txtToDate").val();

		if (fromDate.trim() == '' && fromDate.trim().length == 0) {
			alert("Please Enter From Date");
			return false;
		}
		if (toDate.trim() == '' && toDate.trim().length == 0) {
			alert("Please Enter To Date");
			return false;
		}
		if(funDeleteTableAllRows()){
			if(funCalculateDateDiff(fromDate,toDate)){
				fDate=fromDate;
				tDate=toDate;
				funFetchColNames();
			}
		}
	});

});

function funLoadTableData(fromDate,toDate)
{
	
	fDate=fromDate;
	tDate=toDate;
	funDeleteTableAllRows();
	funFetchColNames();
	
}

function funDeleteTableAllRows()
{
	$('#tblHeader tbody').empty();
	$('#tblData tbody').empty();
	$('#tblTotal tbody').empty();
	var table = document.getElementById("tblData");
	var rowCount1 = table.rows.length;
	if(rowCount1==0){
		return true;
	}else{
		return false;
	}
}

function funCalculateDateDiff(fromDate,toDate) {

	var frmDate= fromDate.split('-');
    var fDate = new Date(frmDate[2],frmDate[1],frmDate[0]);
    
    var tDate= toDate.split('-');
    var t1Date = new Date(tDate[2],tDate[1],tDate[0]);

	var dateDiff=t1Date-fDate;
		 if (dateDiff >= 0 ) 
		 {
     	return true;
     }else{
    	 alert("Please Check From Date And To Date");
    	 return false;
     }
}

		

function funFetchColNames() {
	
	var posName=$('#cmbPOSName').val();
	var strViewTypedata=$('#cmbViewType').val();
	
	var gurl = getContextPath()+"/loadPOSWiseSalesReport.html";
	
	
	$.ajax({
		type : "GET",
		data:{ fromDate:fDate,
				toDate:tDate,
				strViewTypedata:strViewTypedata,
			},
		url : gurl,
		dataType : "json",
		success : function(response) {
		 	if (response== 0) 
			{
				alert("Data Not Found");
			} 
			else 
			{ 
				funFillHeaderCol(response.listcol);
	      
				
			$.each(response.List,function(i,item)
				{
	            	
					//funFillTableCol(item[0],item[1],item[2],item[3]);
					funFillGridCol(item) 
            	});
				
				funFillTotalCol(response.totalList);
        	
			}
			
		}
	});
	}
	function funFillHeaderCol(rowData) 
	{
		var table = document.getElementById("tblHeader");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    for(var i=0;i<rowData.length;i++)
	    	 {
		    	if(i==0)
		    	{
		    		row.insertCell(i).innerHTML = "<input type=\"text\" readonly=\"readonly\" class=\"cell\" size=\"15%\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    	}	
		    	else if(i==1)
		    	{
		    		row.insertCell(i).innerHTML = "<input type=\"text\" readonly=\"readonly\" class=\"cell\" size=\"25%\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    	}
		    	else
		    	{
		    		row.insertCell(i).innerHTML = "<input type=\"text\" readonly=\"readonly\" class=\"cell\"  name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    	}	
	   		}
	}
	
	function funFillGridCol(item) 
	{
		var table = document.getElementById("tblData");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    for(var i=0;i<item.length;i++)
    	 {
	    	if(i==0)
	    	{
	    		row.insertCell(i).innerHTML = "<input readonly=\"readonly\" class=\"Box \"  size=\"15%\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+item[i]+"' />";
	    	}	
	    	else if(i>1)
	    	{
	    		row.insertCell(i).innerHTML = "<input readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+item[i]+"' />";
	    	}
	    	else
	    	{
	    		row.insertCell(i).innerHTML = "<input readonly=\"readonly\" class=\"Box \"  size=\"25%\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+item[i]+"' />";
	    	}	
   		}
	}
	
	function funFillTotalCol(rowData) 
	{
		var table = document.getElementById("tblTotal");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    for(var i=0;i<rowData.length;i++)
	    	 {
	   			if(i==0){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" size=\"15%\" style=\" border:none;border-radius:0px;color:black;font;font-weight: bold; \" class=\"header\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value=\"Total \" />";
	    		 }
	   			else if(i==1)
	   			{
	   				row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" size=\"25%\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value=\" \" />";
	   			}	
		    	else
		    	{
		    	   row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px;text-align:right ;color:black;font;font-weight: bold;\" class=\"header\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";		
		    	}
	   			
	   			
	   		 }
	  
	}

	
	
</script>


</head>

<body>
	<div id="formHeading">
		<label>POS  Wise  Sales Report</label>
	</div>
	<br />
	<br />
	<s:form name="POSWiseSalesReport" method="POST"
		action="processPOSWiseSalesReport1.html?saddr=${urlHits}"
		target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
	<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<label>Report Type</label>
							</div>
							<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbViewType" path="strViewType">
									<s:option value="ITEM WISE">ITEM WISE</s:option>
									<s:option value="GROUP WISE">GROUP WISE</s:option>
									<s:option value="SUB GROUP WISE">SUB GROUP WISE</s:option>
									<s:option value="MENU HEAD WISE">MENU HEAD WISE</s:option>
									<s:option value="POS WISE">POS WISE</s:option>
								</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;">
							 	<label>Chart</label>
							</div>
							<div class="element-input col-lg-6" style="width: 20%;"> 
								<input type="checkbox"  id="strChart"   style="width: 20%%">
							</div>
							
					</div>	
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
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
		    		
		    		<div class="row" style="background-color: #fff; display: -webkit-box;margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 35px; margin:auto; width: 100%;">
							<table id="tblHeader" class="transTablex" style="width: 100%; text-align: center !important;">
								
							</table>
				
							</div>
					</div>	
					
					<div class="row" style="background-color: #fff; display: -webkit-box;margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 400px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblData" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 33px; margin:auto; width: 100%;">
						
								<table id="tblTotal" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>						
	</div>	
	
		<br />
		<br />
		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%; margin-left: 10%;">
					
						<p align="center">
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="Exceute" id="execute"></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="EXPORT" id="submit" ></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="RESET" id="btnReset" ></div>
						</p>
						
				   </div>
				   
				   <div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 50%; left: 50%; padding: 2px;">
						<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
				   </div>

	</s:form>

</body>
</html>