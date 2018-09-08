<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Move Items To Table</title>
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
var selectedRowIndex=0;
var toTableName;
$(document).ready(function() {


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
	
	
	$("#cmbTable").change(function() {
    	funFetchBusyTableData();
        	});
    $("#cmbPOSName").change(function() {
    	funFetchTableData();
    });	
    
    $("form").submit(function(event){
		  if($("#cmbDocType").val().trim()=="" || $("#cmbDocType").val().trim()==null)
			{
				alert("Please create reason for move kot!!!");
				return false;
			}
		  
		  else{
			  flg=funChekTable();
			  return flg;
		  }
		});
	  
});
var tblTable_MAX_ROW_SIZE=100;
var tblTable_MAX_COL_SIZE=4;
function funFetchTableData()
{
	funRemoveTableRows("tblTable");
	
	var posCode=$("#cmbPOSName").val();
	var searchurl=getContextPath()+"/loadTableData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ 
	        	posCode:posCode,
			},
	        url: searchurl,
	        dataType: "json",
	        async: false,
	        success: function(response)
	        {
	        		funAddTableData(response);
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

function funAddTableData(tableDtl)
{
	 var tblTableDtl=document.getElementById('tblTable');
		
		var insertCol=0;
		var insertTR=tblTableDtl.insertRow();
		
		
		
		$.each(tableDtl, function(i, obj) 
		{									
												
				if(insertCol<tblTable_MAX_COL_SIZE)
				{
					
					var col=insertTR.insertCell(insertCol);
					if(obj.Status=="Occupied")
						col.innerHTML = "<td><button type=\"button\" id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'    style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funGetSelectedTblTableRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					else
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'    style=\"width: 100px;height: 100px;background-color: #2FABE9;\" onclick=\"funGetSelectedTblTableRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					insertCol++;
				}
				else
				{					
					insertTR=tblTableDtl.insertRow();									
					insertCol=0;
									
					var col=insertTR.insertCell(insertCol);
					if(obj.Status=="Occupied")
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'     style=\"width: 100px;height: 100px; background: #ff0d0d;\" onclick=\"funGetSelectedTblTableRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					else
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'     style=\"width: 100px;height: 100px;background-color: #2FABE9; \"  onclick=\"funGetSelectedTblTableRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					insertCol++;
				}							
			
			  
		});
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

function funFetchBusyTableData()
{
	var code = $("#cmbTable").val();
	funRemoveTableRows("tblBussyTableItems");
	var searchurl=getContextPath()+"/fillItemForSelectedTable.html?bussyTableNo="+code;
	 $.ajax({
		        type: "GET",
		        url: searchurl,
		        dataType: "json",
		        success: function(response)
		        {
		        	$.each(response, function(i,item)
    				{			
    		    		
		        		funAddBusyTableData(item.strItemCode,item.strItemName,item.dblAmount,item.dblQuantity,item.dblGrandTotal,item.dblTaxAmt,item.strWaiterName,item.dblModifiedAmount);
    		    		
    			  	});
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

function funAddBusyTableData(strItemCode,strItemName,dblAmount,dblQuantity,dblGrandTotal,dblTaxAmt,strWaiterName,dblModifiedAmount)
{
	var table = document.getElementById("tblBussyTableItems");
	var rowCount = table.rows.length;
	var row = table.insertRow(rowCount);
	  row.insertCell(0).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].strItemName\" readonly=\"readonly\" class=\"Box \" id=\"txtItemName."+ (rowCount) +"\" size=\"40%\" value='"+strItemName+"'>";
	  row.insertCell(1).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box \" id=\"txtRate."+ (rowCount) +"\" size=\"10%\" value='"+dblAmount+"'>";
	  row.insertCell(2).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box \" id=\"txtItemQty."+ (rowCount) +"\" size=\"10%\" value='"+dblQuantity+"'>";
	  row.insertCell(3).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].freeItemQty\" readonly=\"readonly\" class=\"Box \" id=\"txtMoveQty."+ (rowCount) +"\" size=\"10%\" value='"+dblQuantity+"' onclick=\"funGetSelectedRowIndex(this)\">";
	  row.insertCell(4).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblGrandTotal\" readonly=\"readonly\" class=\"Box \" id=\"txtAmount."+ (rowCount) +"\" size=\"20%\" value='"+dblGrandTotal+"'>";
	  row.insertCell(5).innerHTML= "<input type=\"checkbox\" name=\"listBusyTableDtl["+(rowCount)+"].strApplicableYN\" size=\"10%\" value='"+true+"' >";
	  row.insertCell(6).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strItemCode\" readonly=\"readonly\" class=\"Box \" id=\"txtItemCode."+ (rowCount) +"\" size=\"0%\" value='"+strItemCode+"'>";
	  row.insertCell(7).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strWaiterName\" readonly=\"readonly\" class=\"Box \" id=\"txtWaiterNo."+ (rowCount) +"\" size=\"0%\" value='"+strWaiterName+"'>";
	  row.insertCell(8).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].dblModifiedAmount\" readonly=\"readonly\" class=\"Box \" id=\"txtFiredQty."+ (rowCount) +"\" size=\"0%\" value='"+dblModifiedAmount+"'>";

}

function funGetSelectedRowIndex(obj)
{
	var person = "";
	 var index = obj.parentNode.parentNode.rowIndex;
	 var table = document.getElementById("tblBussyTableItems");
	 if((selectedRowIndex>0) && (index!=selectedRowIndex))
	 {
		 if(selectedRowIndex%2==0)
		 {
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#A3D0F7';
			 selectedRowIndex=index;
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#ffd966';
			 row.hilite = true;
		 }
		 else
		 {
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#C0E4FF';
			 selectedRowIndex=index;
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#ffd966';
			 row.hilite = true;
         }
		
	 }
	 else
	 {
		 selectedRowIndex=index;
		 row = table.rows[selectedRowIndex];
		 row.style.backgroundColor='#ffd966';
		 var originalQty=table.rows[index].cells[2].children[0].value;
		 var enterQty="";
		 if(originalQty>1)
		 {
			 enterQty = prompt("Please enter quantity:", "");
			 if(enterQty!=null)
			 {
			 if (enterQty > 0)
			 {
				 if (enterQty > originalQty)
					{
					 alert("Please enter the valid quantity.");
					}
				 else
				{
					 document.getElementById("txtMoveQty."+index).value=(parseFloat(enterQty)); 
				}	 
			 }
			 else
			 {
				 alert("Please enter the valid quantity.");
			 }	 
			 
			 } 
			 else
			 {
				 alert("Please enter the valid quantity.");
			 }	 
			 
			 
		 }
		 
		 row.hilite = true;
	 }
	 

	   
}

function funChekTable()
{
	var flag=false;
	var table = document.getElementById("tblBussyTableItems");
	var rowCount = table.rows.length;
	if(rowCount<=0)
	{
		alert("Select items");
		flag=false;
	}	
	
	$('#tblBussyTableItems tr').each(function() {
		  var checkbox = $(this).find("input[type='checkbox']");

		    if( checkbox.prop("checked") ){
		    	 flag= true;
		    } 
			 });
	  
	  if(!flag)
		  {
		  alert("Select items");
		  return flag;
		  }
	  
	  if (toTableName == null || toTableName.trim().length() == 0)
	    {
		alert("Select table for shift.");
		flag = false;

		return flag;
	    }
	
	return flag;
}

function funGetSelectedTblTableRowIndex(obj,tableId)
{
	toTableName="";
	var index = obj.parentNode.parentNode.rowIndex;
	var cellIndex=obj.parentNode.cellIndex;
	 var tableName = document.getElementById(tableId);
	$("#txtToTableNo").val(obj.id);	
}	 
	 

</script>


</head>

<body onload="funFetchTableData()">
	<div id="formHeading">
		<label>Move Items To Table</label>
	</div>
	<s:form name="frmMoveItemsToTable" method="POST" action="saveMoveItemsToTable.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">

		<br />
		<br />
		
		<div class="title">
			<div style=" width: 50%; height: 450px;float:left;  overflow-x: hidden; border-collapse: separate; overflow-y: scroll;">
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;margin-left: 15px;">
					<div class="element-input col-lg-6" style="width: 30%;"> 
	    				<s:select id="cmbTable" path="strTableName" items="${bussyTableList}" />
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
	    				<label class="title" >BUSY TABLES</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 40%;"> 
	    				<s:select id="cmbPOSName" name="cmbPOSName" path="strPosCode" items="${posList}" />
	    			</div>
				</div>
				
				<table border="1" style="width:100%;margin: auto;"  >
							<thead>
									<tr>
										<td style="width:40%; background: #78BEF9;">Description</td>
										<td style="width:10%; background: #78BEF9;">Rate</td>
										<td style="width:10%; background: #78BEF9;">Quantity</td>
										<td style="width:10%; background: #78BEF9;">Move Quantity</td>
										<td style="width:20%; background: #78BEF9;">Amount</td>
										<td style="width:10% border: #c0c0c0 1px solid; background: #78BEF9;">Select</td>
<!-- 										<td style="width:0%; background: #78BEF9;">Item Code</td> -->
<!-- 										<td style="width:0%; background: #78BEF9;">Waiter</td> -->
<!-- 										<td style="width:0%; background: #78BEF9;">Fired Qty</td> -->
									</tr>
							</thead>
					</table>
					
					<table id="tblBussyTableItems" style="width: 100%;">
							<tbody>    

							 </tbody>							
					 </table>
			</div>
		</div>
		
		<div class="title">
			<div style=" width: 50%; height: 450px;float:left;  overflow-x: scroll; border-collapse: separate; overflow-y: scroll;">
				
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;margin-left: 15px;">
					<div class="element-input col-lg-6" style="width: 30%;"> 
	    				<label class="title" >All Tables</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 30%;"> 
	    				<input type="text" name="txtSearchTable" id="txtSearchTable" />
	    			</div>
	    			
				</div>
				<div class="row" style="background-color: #fff;margin-bottom: 10px;">
		    	 	<div class="element-input col-lg-6" style="width: 26%;margin-left: -15px;"> 
		   				<label class="title">Reason</label>
		   			</div>
		  		 	<div class="element-input col-lg-6" style="width: 75%;">
		    				<s:select path="strReasonName" items="${listReson}" id="cmbDocType" style="margin-bottom: 10px;"></s:select>
		 		    </div>
		 		    
			   </div>
				<table id="tblTable" class="transFormTable">
					<tbody style="border-top: none;"></tbody>
				</table>
			</div>
			
			<s:input type="hidden" name="txtToTableNo" id="txtToTableNo" path="strCustomerCode"/>
		</div>
		
		<br/><br/>
		
		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 240px;">
     		  <p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" /></div>
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="Close" onclick="funPOSHome()"></div>
     		  </p>
   		 </div>
	
	</s:form>

</body>
</html>
