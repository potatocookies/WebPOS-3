<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GROUP MASTER</title>
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
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	width: 100%
}

.header {
	border:1px solid #ccc;
	background: #85cdffe6;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	font-weight: bold;
	width: 100%;
	height:100%
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
				
	    			
				funFetchColNames();
	    			}); 
		$("[type='reset']").click(function(){
			location.reload(true);
		});
		
		$("form").submit(function(event){
			var table = document.getElementById("tblBillWiseSettlement");
			var rowCount = table.rows.length;
			if (rowCount > 2){
				$("#txtFromDate").val(fDate);
				$("#txtToDate").val(tDate);
				return true;
			} else {
				alert("Data Not Available");
				return false;
			}
		});

		$("#btnSubmit").click(function(event) {
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
	
	
	function funDeleteTableAllRows()
	{
		$('#tblBillWiseSettlement tbody').empty();
		$('#tblTotal tbody').empty();
		var table = document.getElementById("tblBillWiseSettlement");
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
        	 return false
         }
	}

	
	function funFetchColNames() {
		var operationType=$('#cmbOperationType').val();
		var settlementName=$('#cmbSettleName').val();
		var posName=$('#cmbPOSName').val();
		var viewBy=$('#cmbViewBy').val();
		var groupName = $('#cmbGroupName').val();
			var gurl = getContextPath() + "/loadBillwiseSettlementSalesSummary.html";
	
		$.ajax({
			type : "GET",
			data:{ fromDate:fDate,
					toDate:tDate,
					viewBy:viewBy,
					operationType:operationType,
					settlementName:settlementName,
					posName:posName,
					groupName:groupName,
				},
			url : gurl,
			dataType : "json",
			success : function(response) {
				if (response.RowCount == 0  ) {
					alert("Data Not Found");
				} else {
					
					//Add Sub Category Headers
					funAddHeaderRow(response.Header);
					
					//Add Size Names And Headers
					
					$.each(response,function(i,item){
						if(i<response.RowCount)				
							funAddUnderLine(item,"tblBillWiseSettlement");
						});
					
					
					
				}//Else block Of Response
				if($("#cmbViewBy").val()!="BILL REGISTER")
				{	
				funAddTotalHeaderRow(response.Header);
				}
				funAddUnderLine(response.Total,"tblTotal");
			},
			error : function(jqXHR, exception) {
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
	
	
	
 	function funAddHeaderRow(rowData){
		var table = document.getElementById("tblBillWiseSettlement");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
		    for(var i=0;i<rowData.length;i++){
		    	if(i==0){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    		 } else {
		    			row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    		}
				}
		} 
	
 	function funAddTotalHeaderRow(rowData){
		var table = document.getElementById("tblTotal");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var viewBy=$('#cmbViewBy').val();
	    var cnt=0;
	    if(viewBy=="ITEM'S GROUP WISE")
	    	cnt=rowData.length-1;
	    else
	    	cnt=rowData.length;
		    for(var i=0;i<cnt;i++){
		    	if(i==0){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value=\"Total \" />";
	    		 }
		    	else if(i==1){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value=\" \" />";
	    		 }
		    	else if(i==2){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value=\"POS \" />";
	    		 }else {
		    			row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    		}
				}
		} 
		
	
	function funAddUnderLine(rowData,tableId) 
	{
		var table = document.getElementById(tableId);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var item;
	    for(var i=0;i<rowData.length;i++)
	    	 {
	   			if(i>2)
	   			{
	   				if(typeof rowData[i] == 'number')   			
	   				{	
	   				 item=Math.round(rowData[i]);
	   				}
	   				else
	   				{
	   				 item=rowData[i];
	   				}	
	   			}
	   			else
	   			 item=rowData[i];
	 	   		row.insertCell(i).innerHTML = "<input type=\"text\" readonly=\"readonly\" style=\" border:none;border-radius:0px \" class=\"cell\" name=\"rowList["+(rowCount)+"].strCol"+(i)+"\" id=\"rowList["+(rowCount)+"].strCol"+(i)+"\" value='"+item+"' />";
	   		
	   		 }
		
	  
	}
	
	
</script>


</head>

<body>
	<div id="formHeading">
		<label>POS Bill Wise Settlement Sales Summary Flash</label>
	</div>
	
	<s:form name="POSBillWiseSettlementSalesSummeryFlash" method="POST"
		action="processPOSBillWiseSettlementSalesSummeryFlash.html?saddr=${urlHits}" target="_blank" 
		class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 12%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate" pattern="\d{1,2}-\d{1,2}-\d{4}" style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 12%;"> 
								<s:input id="txtToDate" required="required" path="toDate" pattern="\d{1,2}-\d{1,2}-\d{4}"  style="width: 100%;"/>	
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">View By</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbViewBy" path="strViewBy">
										<s:option value="ITEM'S GROUP WISE">ITEM'S GROUP WISE</s:option>
										<s:option value="NONE">NONE</s:option>
										<s:option value="BILL REGISTER">BILL REGISTER</s:option>
								</s:select>
							</div>
					</div>
					
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 12%;"> 
		    					<label class="title">Settlement Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbSettleName" name="cmbSettleName" path="strSettlementName" items="${settlementList}">
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Operation Type</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbOperationType" path="strOperationType">
										<s:option value="All">All</s:option>
										<s:option value="Dine In">Dine In</s:option>
										<s:option value="Direct Biller">Direct Biller</s:option>
										<s:option value="Home Delivery">Home Delivery</s:option>
										<s:option value="Take Away">Take Away</s:option>

								</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 9%;"> 
		    					<label class="title">Group Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 10%;"> 
								<s:select id="cmbGroupName" path="strGroupName" items="${groupList}"/>	
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<input type="button" value="DISPLAY" id="btnSubmit" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<input type="submit" value="EXPORT" id="submit" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<input type="reset" value="RESET" id="btnReset" />
							</div>
							
					</div>
					
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblBillWiseSettlement" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 100px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblTotal" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
							</div>
					</div>
					
				   
				   <div id="wait" style="display: none; width: 60px; height: 60px; border: 0px solid black; position: absolute; top: 50%; left: 50%; padding: 2px;">
						<img src="../${pageContext.request.contextPath}/resources/images/ajax-loader-light.gif" width="60px" height="60px" />
				   </div>
				   
				   
		</div>

	</s:form>

</body>
</html>