<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Table Status Report</title>
<style>
.disabled_button{
    background:url(../images/big1.png) no-repeat;
   background-size: 96px 24px;
    cursor:pointer;
    border:none;
    width:100px;
    height:24px;
    color: #fff;
    font-weight: normal;
    background-color: #c0c0c0;
}
</style>
<script type="text/javascript">
var KOTNo="";
var selectedtblNo="";
var previousKOT="";
var previousTable="";
var prevVal="";
var prevStatus="";
var prevIndex="";
var prevCellIndex="";
var prevKOTIndex="";
var prevKOTCellIndex="";
var selectedIndx=0;
var totalPax=0;
var tblMenuItemDtl_MAX_ROW_SIZE=100;
var tblMenuItemDtl_MAX_COL_SIZE=9;
function funFillTables()
{
	funFetchTableStatusReportData();
}

function funFetchTableStatusReportData()
{
	funRemoveTableRows("tblTableStatus");
	var posCode=$("#cmbPOSName").val();
	var tableStatus=$("#cmbTableStatus").val();
	var areaCode=$("#cmbArea").val();
	var searchurl=getContextPath()+"/loadTableStatusReportData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ 
	        	POSCode:posCode,
	        	Status:tableStatus,
	        	AreaCode:areaCode,
			},
	        url: searchurl,
	        dataType: "json",
	        async: false,
	        success: function(response)
	        {
	        	 funFillTableGrid(response.TableList);
			},
			error: function(jqXHR, exception) {
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
	
	
	$("form").submit(function(event){
		  funValidate();
		});
	
});


//Combo Box Change then set value
function funOnChange() 
{
	funFillTables();
}

function funRemoveTableRows(tblId)
{
	var table = document.getElementById(tblId);
	var rowCount = table.rows.length;
	while(rowCount>0)
	{
		table.deleteRow(0);
		rowCount--;
	}
}
//Pratiksha
function funFillTableGrid(list)
{
	 var tblTableDtl=document.getElementById('tblTableStatus');
	 var insertCol=0;
	 var insertTR=tblTableDtl.insertRow();
	 $.each(list, function(i, obj) 
		{				
		 totalPax=totalPax+obj.intPaxCapacity;
			if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
			{
				var col=insertTR.insertCell(insertCol);
				if(obj.strStatus=="Occupied")
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+" "+obj.intPaxCapacity+"'  style=\"width: 85px;height: 85px; background: #ff0d0d; text-align: center;white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,"+"tblTableStatus"+")\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";

				}
				else if(obj.strStatus=="Billed")
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"  "+obj.intPaxCapacity+"'   style=\"width: 85px;height: 85px; background: #0000CD; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";
				}
				else
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"  "+obj.intPaxCapacity+"'   style=\"width: 85px;height: 85px; text-align: center;white-space:pre-wrap; background: #B0C4DE;word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";
				}	
				
				
				insertCol++;
			}
			else
			{					
				insertTR=tblTableDtl.insertRow();									
				insertCol=0;
				var col=insertTR.insertCell(insertCol);  
				if(obj.strStatus=="Occupied")
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"  "+obj.intPaxCapacity+"'  style=\"width: 85px;height: 85px; background-color: #ff0d0d; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";

				}
				else if(obj.strStatus=="Billed")
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"  "+obj.intPaxCapacity+"'  style=\"width: 85px;height: 85px; background-color: #0000CD; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";
				}
				else
				{
					col.innerHTML = "<td><button type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"  "+obj.intPaxCapacity+"'  style=\"width: 85px;height: 85px; white-space:pre-wrap; word-wrap:break-word;background: #B0C4DE; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>"+obj.strTableName+"<br>"+obj.intPaxCapacity+"</button></td>";
				}
					
				insertCol++;
			}							
		});
	 
	 $("#lblTotalPax").text("Total Pax: "+totalPax);
}


/* function funFillTableGrid(list)
{
	 var tblTableDtl=document.getElementById('tblTableStatus');
	 var insertCol=0;
	 var insertTR=tblTableDtl.insertRow();
	 $.each(list, function(i, obj) 
		{				
		 totalPax=totalPax+obj.intPaxCapacity;
		// var strVal=obj.strTableName+obj.intPaxCapacity+obj.tmeTimeDiff;
			if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
			{
				var col=insertTR.insertCell(insertCol);
				if(obj.strStatus=="Occupied")
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"time"+obj.tmeTimeDiff+"'  style=\"width: 85px;height: 85px; background: #ff0d0d; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}
				else if(obj.strStatus=="Billed")
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"time"+obj.tmeTimeDiff+"'   style=\"width: 85px;height: 85px; background: #0000CD; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}
				else
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"'   style=\"width: 85px;height: 85px; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}	
				
				
				insertCol++;
			}
			else
			{					
				insertTR=tblTableDtl.insertRow();									
				insertCol=0;
				var col=insertTR.insertCell(insertCol);  
				if(obj.strStatus=="Occupied")
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"time"+obj.tmeTimeDiff+"'  style=\"width: 85px;height: 85px; background: #ff0d0d; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}
				else if(obj.strStatus=="Billed")
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"time"+obj.tmeTimeDiff+"'  style=\"width: 85px;height: 85px; background: #0000CD; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}
				else
				{
					col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value='"+obj.strTableName+"pax"+obj.intPaxCapacity+"'  style=\"width: 85px;height: 85px; white-space:pre-wrap; word-wrap:break-word; margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblTableStatus')\"/>";
				}
					
				insertCol++;
			}							
		});
	 
	 $("#lblTotalPax").text("Total Pax: "+totalPax);
} */

function funGetSelectedRowIndex(obj,tableId)
{
	var index = obj.parentNode.parentNode.rowIndex;
	var cellIndex=obj.parentNode.cellIndex;
	if(tableId=="tblTableStatus")
	{
	  	 var tableName = document.getElementById(tableId);
		 var code=obj.id;
		 var value=obj.value;
		 KOTNo=code;
	}
}

</script>


</head>

<body onload="funFillTables()">
	<div id="formHeading">
		<label>Table Status Report</label>
	</div>
	
	<s:form name="POSTableStatusReport" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;" >
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px;">
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">POS Name</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" onchange="funOnChange();" >
				 				</s:select>
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Table Status</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbTableStatus" name="cmbTableStatus" path="strStatus" items="${statuslist}" onchange="funOnChange();" />
							</div>
							<div class="element-input col-lg-6" style="width: 15%;"> 
		    					<label class="title">Area</label>
		    				</div>
		    				<div class="element-input col-lg-6" style="width: 20%;"> 
								<s:select id="cmbArea" name="cmbArea" path="strArea" items="${areaList}" onchange="funOnChange();" />
							</div>
					</div>
					
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 400px; margin:auto; overflow-x: auto; overflow-y: scroll; width: 100%;">
						
								<table id="tblTableStatus" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;">
									</tbody>
								</table>
								
							</div>
					</div>
					
					<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%;">
					
						
							<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 22%;"><input type="button" value="OCCUPIED" style="background:#ff0d0d;" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 22%;"><input type="button" value="VACANT" style="background: #B0C4DE;" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4" style="width: 22%;"><input type="button" value="Billed" style="background: #0000CD;" /></div>
				
						
						<div class="element-input col-lg-6" style="width: 22%;">
							<label id="lblTotalPax">Total Pax</label>
						</div>
						
				  	</div>
					
		 </div>
		
		
		
<!-- 		<div> -->
<!-- 			<div style="margin-left: 30px; width: 100%;"> -->

<!--                 <table class="masterTable" style="width: 70%;"> -->
<!-- 				<tr> -->
<!-- 				    <td> -->
<!-- 					 <label>POS Name</label> -->
<!-- 					</td>  -->
<%-- 			        <td><s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" onchange="funOnChange();" cssClass="BoxW124px" /> --%>
<!-- 					</td> -->
					
<!-- 					<td> -->
<!-- 					 <label>Table Status</label> -->
<!-- 					</td>  -->
<%-- 			        <td><s:select id="cmbTableStatus" name="cmbTableStatus" path="strStatus" items="${statuslist}" onchange="funOnChange();" cssClass="BoxW124px" /> --%>
<!-- 					</td> -->
					
<!-- 					<td> -->
<!-- 					 <label>Area</label> -->
<!-- 					</td>  -->
<%-- 			        <td><s:select id="cmbArea" name="cmbArea" path="strArea" items="${areaList}" onchange="funOnChange();" cssClass="BoxW124px" /> --%>
<!-- 					</td> -->
		
<!-- 				</tr> -->
<!--                </table> -->
<!-- 				<table class="masterTable" style="width: 70%;"> -->
<!-- 				<tr>                    -->
<!-- 					<td> -->
<!-- 					<div id="divTableStatusDtl" style=" border: 1px solid #ccc; height: 425px;  overflow-x: auto; overflow-y: auto; width: 100%;">									 -->
											
<!-- 					<table id="tblTableStatus"   cellpadding="0" cellspacing="5"> -->
					
<!-- 					</table> -->
<!-- 					</div> -->
<!-- 					</td> -->
<!-- 				</tr> -->
<!-- 				</table> -->
<!-- 			</div> -->

			
<!-- 		</div> -->

<!-- 		<br /> -->
<!-- 		<br /> -->
<!-- 		<p align="center"> -->
<!-- 			    <input type="button" value="Occupied" style="background:#ff0d0d;"class="form_button" /> -->
<!-- 			    <input type="button" value="Vacant" style="background: #B0C4DE;"class="form_button" /> -->
<!-- 			    <input type="button" value="Billed" style="background: #0000CD;"class="form_button" />   -->
<!-- 			    <label id="lblTotalPax">Total Pax</label>   -->
<!-- 		</p> -->
		

	</s:form>

</body>
</html>