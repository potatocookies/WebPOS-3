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
	border: 0 solid #060006;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	outline: 0 none;
	font-weight: bold;
	padding-left: 0;
	width: 80%
}

.header {
	border: #c0c0c0 1px solid;
	background: #78BEF9;
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


var gEnableShiftYN="${gEnableShiftYN}";
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
			
   			
		funLoadTableData();
		
		
	    }); 
		
		$("[type='reset']").click(function(){
			location.reload(true);
		});
		
		$("form").submit(function(event){
			var table = document.getElementById("tblKDSFlash");
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
					funLoadKDSFlashData();
				}
			}
		});

	

	
	

	if(gEnableShiftYN=='Y')
	{
		document.getElementById("lblShift").style.visibility = "visible"; 
		document.getElementById("txtShiftCode").style.visibility = "visible"; 
	}
	else
	{
		document.getElementById("lblShift").style.visibility = "hidden";
		document.getElementById("txtShiftCode").style.visibility = "hidden"; 
		
	}

});

	
	function funLoadTableData()
	{
		funLoadKDSFlashData();
		
	}
	
	
	
	function funDeleteTableAllRows()
	{
		$('#tblKDSFlash tbody').empty();
		$('#tblTotal tbody').empty();
		var table = document.getElementById("tblKDSFlash");
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

	
	function funLoadKDSFlashData() {
		var fromDate = $('#txtFromDate').val();
		var toDate = $('#txtToDate').val();
		var reportType=$('#cmbReportType').val();
		var posName=$('#cmbPOSName').val();
		var waiterName=$('#cmbWaiter').val();
		var costCenterName=$('#cmbCostCenter').val();
		var strType = $('#cmbType').val();	
		
		var gurl = getContextPath() + "/loadKDSFlash.html";
		
		
		$.ajax({
			type : "GET",
			data:{  fromDate:fromDate,
					toDate:toDate,
					reportType:reportType,
					posName:posName,
					costCenterName:costCenterName,
					waiterName:waiterName,
					strType:strType,
				},
			url : gurl,
			dataType : "json",
			success : function(response) {
				if (response.status =="Not Found") 
				{
					alert("Data Not Found");
				} else 
				{
					//Add Sub Category Headers
					funAddHeaderRow(response.ColHeader);
					
					if(reportType=="Group")
					{
						$.each(response.listArr,function(i,item){
			            	
							funFillTableWith3Col(item[0],item[1],item[2]);
		            	});
					}
					else if(reportType=="SubGroup")
					{
						$.each(response.listArr,function(i,item)
						{
							if(item.length==2)
							{
								funFillTableWith10Col(item[0],item[1],"","","","","","","","",'red');
							}
							else
							{
								funFillTableWith10Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],'dark gray');
							}
		            	});
					}
					else if(reportType=="Menu Head")
					{
						$.each(response.listArr,function(i,item){
			            	
							funFillTableWith3Col(item[0],item[1],item[2]);
		            	});
					}
					else 
					{
						$.each(response.listArr,function(i,item)
						{
							if(item.length==2)
							{
								funFillTableWith12Col(item[0],item[1],"","","","","","","","","","",'red');
							}
							else
							{
								funFillTableWith12Col(item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],item[11],'dark gray');
							}
		            	});
					}
					
					
					funFillTotalData(response.totalList);
				}//Else block Of Response
				
				
			
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
	
	function funFillTotalData(rowData) 
	{
		
	    $.each(rowData,function(i,item)
	    {
	    	var table = document.getElementById("tblTotal");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:left;margin-left:2%;\" size=\"100%\" id=\"txtItemName."+(rowCount)+"\" value='"+item+"' />";

    	});

	}
	
 	function funAddHeaderRow(rowData)
 	{
		var table = document.getElementById("tblKDSFlash");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    
		    for(var i=0;i<rowData.length;i++)
		    {
		    	if(i==0){
	    			 row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
	    		 } else {
		    			row.insertCell(i).innerHTML= "<input type=\"text\" readonly=\"readonly\" class=\"header\" name=\"colRow["+(rowCount)+"].strCol"+(i)+"\" value='"+rowData[i]+"' />";
		    			
		    		}
				}
		}
 	
	function funFillTableWith12Col(item0,item1,item2,item3,item4,item5,item6,item7,item8,item9,item10,item11,colorName)
	{
		var table = document.getElementById("tblKDSFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:left;;color:"+colorName+"\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+item0+"' />";
	    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;;color:"+colorName+"\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item1+"' />";
	    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item2+"' />";
	    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"30%\" id=\"txtDate."+(rowCount)+"\" value='"+item3+"' />";
	    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item4+"' />";
	    row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item5+"' />";
	    row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item6+"' />";
	    row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item7+"' />";
	    row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item8+"' />";
	    row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item9+"' />"; 
	    row.insertCell(10).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item10+"' />";
	    row.insertCell(11).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item11+"' />"; 
	}
 
 	function funFillTableWith10Col(item0,item1,item2,item3,item4,item5,item6,item7,item8,item9,colorName)
	{
		var table = document.getElementById("tblKDSFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:left;;color:"+colorName+"\" size=\"10%\" id=\"txtItemName."+(rowCount)+"\" value='"+item0+"' />";
	    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;;color:"+colorName+"\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item1+"' />";
	    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item2+"' />";
	    row.insertCell(3).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"30%\" id=\"txtDate."+(rowCount)+"\" value='"+item3+"' />";
	    row.insertCell(4).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item4+"' />";
	    row.insertCell(5).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item5+"' />";
	    row.insertCell(6).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;\" size=\"10%\" id=\"txtCompStk."+(rowCount)+"\" value='"+item6+"' />";
	    row.insertCell(7).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item7+"' />";
	    row.insertCell(8).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item8+"' />";
	    row.insertCell(9).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:leftt;\" size=\"10%\" id=\"txtDate."+(rowCount)+"\" value='"+item9+"' />"; 
	}
 	
 	
 	function funFillTableWith3Col(strWaiterName,noOfKot,noOfVoidKot)
	{
		var table = document.getElementById("tblKDSFlash");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.insertCell(0).innerHTML= "<input   class=\"Box \" style=\"text-align:left;margin-left:2%;\" size=\"90%\" id=\"txtItemName."+(rowCount)+"\" value='"+strWaiterName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    row.insertCell(1).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:right;margin-right:2%;\" size=\"80%\" id=\"txtDate."+(rowCount)+"\" value='"+noOfKot+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    row.insertCell(2).innerHTML= "<input  readonly=\"readonly\" class=\"Box \" style=\"text-align:left;margin-left:2%;\" size=\"90%\" id=\"txtCompStk."+(rowCount)+"\" value='"+noOfVoidKot+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	     
	}
	
	
</script>


</head>

<body>
	<div id="formHeading">
		<label>POS KDS Flash</label>
	</div>
	
	<s:form name="POSKDSFlash" method="POST"
		action="processKDSFlash.html?saddr=${urlHits}"
		target="_blank" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSName" items="${posList}" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Report Type</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbReportType" name="cmbReportType" path="strReportType" items="${listReportType}" />
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">From Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:input  id="txtFromDate" required="required" path="fromDate"  style="width: 100%;"/>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">To Date</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 17%;"> 
								<s:input id="txtToDate" required="required" path="toDate"   style="width: 100%;"/>	
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px;">
							
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Cost Center</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbCostCenter" path="strItemName" items="${listCostCenter}" >
				    			</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 8%;"> 
		    					<label class="title">Waiter Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbWaiter" name="cmbWaiter" path="strWShortName" items="${listWaiter}" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
								<s:select id="cmbType" name="cmbType" path="strType" items="${listType}"/>
							</div>
							<div class="element-input col-lg-6" style="width: 30%;"> 
								<input type="button" value="Execute" id="execute" />
								<input type="submit" value="EXPORT" id="submit" />
								<input type="button" value="Close" id="close" />
							</div>
					</div>
					
		    		<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 500px; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblKDSFlash" style="width: 100%; text-align: center !important;">
								
									<tbody style="border-top: none;border-bottom: 1px solid #ccc;">
									</tbody>
								</table>
								
								
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: block; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 30%; margin:auto; overflow-x: scroll; overflow-y: scroll; width: 100%;">
						
								<table id="tblTotal" style="width: 100%; text-align: left !important;margin-top: 3px;overflow-x: scroll; overflow-y: scroll;">
								
									<tbody style="border-top: none;">
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