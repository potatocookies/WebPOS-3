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
</style>
<script type="text/javascript">
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
	    	funFetchKOTData();
	        	});
	    $("#cmbPOSName").change(function() {
	    	funFetchTableData();
	        	});
	  
        $("form").submit(function(event){
			  
			  if(KOTNo.trim()=="")
				{
				alert("Select table from Open Tables" );
				return false;
				}
			else if(selectedtblNo.trim()=="")
			{
				alert("Select table from All Tables" );
				return false;
			}
			else
			{
				document.frmMoveKOT.action="saveMoveKOT.html?KOTNo="+ KOTNo+"&tableName="+selectedtblNo+"&selectedIndx="+prevCellIndex;
				document.frmMoveKOT.submit();
				
			}
			});
        

        $('#txtSearchTable').keypress(function(event){
        	
        	var keycode = (event.keyCode ? event.keyCode : event.which);
        	if(keycode == '13'){
        		funSearchTables();	
        	}

        });
});

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
var tblMenuItemDtl_MAX_ROW_SIZE=100;
var tblMenuItemDtl_MAX_COL_SIZE=4;
function funGetSelectedRowIndex(obj,tableId)
{
	var index = obj.parentNode.parentNode.rowIndex;
	var cellIndex=obj.parentNode.cellIndex;
	
	 if(tableId=="tblKOT")
		{
		  	 var tableName = document.getElementById(tableId);
			 var code=obj.id;
			KOTNo=code;
			if(previousKOT=="")
			{
				prevKOTIndex=index;
				prevKOTCellIndex=cellIndex;
				tableName.rows[index].cells[cellIndex].innerHTML="<button type=\"button\" id="+code+"  style=\"width: 100px;height: 100px; background: #595959;\" value='"+code+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				previousKOT=code;
			}
			else if(previousKOT==code)
			{
				tableName.rows[index].cells[cellIndex].innerHTML="<button type=\"button\" id="+code+" style=\"width: 100px;height: 100px;    background-color: #2FABE9;\" value='"+code+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				previousKOT="";
			}
			else
			{
				 tableName.rows[prevKOTIndex].cells[prevKOTCellIndex].innerHTML="<button type=\"button\" id="+previousKOT+" style=\"width: 100px;height: 100px;    background-color: #2FABE9;\" value='"+previousKOT+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				 prevKOTIndex=index;
				 prevKOTCellIndex=cellIndex;
				 tableName.rows[index].cells[cellIndex].innerHTML="<button type=\"button\"  id="+code+" style=\"width: 100px;height: 100px; background: #595959;\" value='"+code+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				 previousKOT=code;
			}
		}
	 else
		{
			
			var tblNo= obj.id;
		  	var status= obj.name;
			var code = obj.value;
			var paxNo="";
			if(!code.includes(":"))
			{
				paxNo=0;
			}
			else
			{
				paxNo = code.split(":")[1];
			}	
			var val=code.split(":")[0];
  			if(previousTable=="")
			{
		  		 prevIndex=index;
				 prevCellIndex=cellIndex;
		  		 tableId.rows[index].cells[cellIndex].innerHTML="<td><button type=\"button\" id="+tblNo+" name="+status+"  value='"+val+"'     style=\"width: 100px;height: 100px; background: #595959;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
		   		
			   	previousTable=tblNo;
			   	prevVal=val;
			   	prevStatus=status;
			    selectedtblNo=tblNo;
			   
   			}
		  	else if(previousTable==tblNo)
		  	{
		  		if(status=="Occupied")
		  			tableId.rows[prevIndex].cells[prevCellIndex].innerHTML="<td><button type=\"button\" id="+tblNo+" name="+status+" value='"+val+"'     style=\"width: 100px;height: 100px; background: #ff0d0d;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
		  		else
		  			tableId.rows[prevIndex].cells[prevCellIndex].innerHTML="<td><button type=\"button\" id="+tblNo+" name="+status+" value='"+val+"'     style=\"width: 100px;height: 100px;background-color: #2FABE9; \" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
		  		
		  		prevVal="";
		   		previousTable="";
		   		prevStatus="";
		  	}
			else
			{
				if(prevStatus=="Occupied")
					tableId.rows[prevIndex].cells[prevCellIndex].innerHTML= "<td><button type=\"button\" id="+previousTable+" name="+prevStatus+" value='"+prevVal+"'     style=\"width: 100px;height: 100px; background: #ff0d0d;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
				else
					tableId.rows[prevIndex].cells[prevCellIndex].innerHTML= "<td><button type=\"button\" id="+previousTable+" name="+prevStatus+" value='"+prevVal+"'     style=\"width: 100px;height: 100px;    background-color: #2FABE9; \" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
				
				prevIndex=index;
				prevCellIndex=cellIndex;
				tableId.rows[index].cells[cellIndex].innerHTML= "<td><button type=\"button\" id="+tblNo+" name="+status+"  value='"+val+"'     style=\"width: 100px;height: 100px; background: #595959;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+code.split(":")[0]+"<br>"+paxNo+"</button></td>";
				previousTable=tblNo;
				prevVal=val;
				prevStatus=status;
				selectedtblNo=tblNo;
			}
	  }
}
function funValidate()
{
	if(KOTNo.trim()=="")
		{
		alert("Select table from Open Tables" );
		return false;
		}
	else if(selectedtblNo.trim()=="")
	{
		alert("Select table from All Tables" );
		return false;
	}
	else
	{
		document.frmMoveKOT.action="saveMoveKOT.html?KOTNo="+ KOTNo+"&tableName="+selectedtblNo+"&selectedIndx="+prevCellIndex;
		document.frmMoveKOT.submit();
		
	}
}
function funFillTables()
{
	funFetchKOTData();
	funFetchTableData();
}

function funFetchKOTData()
{
	funRemoveTableRows("tblKOT");
	var tableNo=$("#cmbTable").val();
	var posCode=$("#cmbPOSName").val();
	var searchurl=getContextPath()+"/loadKOTData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ tableNo:tableNo,
	        	posCode:posCode,
			},
	        url: searchurl,
	        dataType: "json",
	        async: false,
	        success: function(response)
	        {
	        	 var cnt=0;
	        	 var item1, item2,item3, item4;
	        	 funFillKOTTable(response.KOTList);
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
var g=0;
var rowCount;
var row;
function funAddTableData(tableDtl)
{
	 var tblTableDtl=document.getElementById('tblTable');
		
		var insertCol=0;
		var insertTR=tblTableDtl.insertRow();
		
		
		
		$.each(tableDtl, function(i, obj) 
		{									
												
				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				{
					
					var col=insertTR.insertCell(insertCol);
					if(obj.Status=="Occupied")
						col.innerHTML = "<td><button type=\"button\" id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'    style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					else
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'    style=\"width: 100px;height: 100px;background-color: #2FABE9;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					insertCol++;
				}
				else
				{					
					insertTR=tblTableDtl.insertRow();									
					insertCol=0;
									
					var col=insertTR.insertCell(insertCol);
					if(obj.Status=="Occupied")
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'     style=\"width: 100px;height: 100px; background: #ff0d0d;\" onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					else
						col.innerHTML = "<td><button type=\"button\"  id="+obj.TableNo+" name="+obj.Status+" value='"+obj.TableName+":"+obj.Pax+"'     style=\"width: 100px;height: 100px;background-color: #2FABE9; \"  onclick=\"funGetSelectedRowIndex(this,"+"tblTable"+")\" >"+obj.TableName+"<br>"+obj.Pax+"</button></td>";
					insertCol++;
				}							
			
			  
		});
}
function funFillKOTTable(list)
{
	 var tblTableDtl=document.getElementById('tblKOT');
	 var insertCol=0;
	 var insertTR=tblTableDtl.insertRow();
	 
	 $.each(list, function(i, obj) 
		{									
			if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
			{
				var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" class = \"transForm_button\" id="+obj+" value='"+obj+"'    style=\"width: 100px;height: 100px;\"  onclick=\"funGetSelectedRowIndex(this,'tblKOT')\"/>";
				insertCol++;
			}
			else
			{					
				insertTR=tblTableDtl.insertRow();									
				insertCol=0;
				var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" class = \"transForm_button\" id="+obj+" value='"+obj+"'   style=\"width: 100px;height: 100px; \" onclick=\"funGetSelectedRowIndex(this,'tblKOT')\"/>";
				insertCol++;
			}							
		});
}


function funSearchTables()
{
	var tblSearch = $("#txtSearchTable").val();

	funRemoveTableRows("tblTable");
	
	var posCode=$("#cmbPOSName").val();
	var searchurl=getContextPath()+"/loadSearchTableData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ 
	        	posCode:posCode,
	        	tblSearch:tblSearch,
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


</script>


</head>

<body onload="funFillTables()" >
	<div id="formHeading">
		<label>Move KOT To Table</label>
	</div>
	<s:form name="frmMoveKOT" method="POST" action="saveMoveKOT.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">

		<br />
		<br />
		
		<div class="title">
			<div style=" width: 50%; height: 450px;float:left;  overflow-x: hidden; border-collapse: separate; overflow-y: scroll;">
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;margin-left: 15px;">
					<div class="element-input col-lg-6" style="width: 30%;"> 
	    				<s:select id="cmbTable" path="strTableNo" items="${tableList}" />
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%;">
	    				<label class="title" >Open KOTs</label>
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 40%;"> 
	    				<s:select id="cmbPOSName" name="cmbPOSName" path="strPOSCode" items="${posList}" />
	    			</div>
				</div>
				
				<table id="tblKOT" class="transFormTable">
					<tbody style="border-top: none;"></tbody>
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
	    				<input type="text" name="txtSearchTable" id="txtSearchTable" onblur="funSearchTables()"/>
	    			</div>
	    			
				</div>
				<table id="tblTable" class="transFormTable">
					<tbody style="border-top: none;"></tbody>
				</table>
			</div>
		</div>
		
		<br/><br/>
		
		<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 240px;">
     		  <p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit" onclick="return funValidateFields();"/></div>
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="Close" onclick="funPOSHome()"></div>
     		  </p>
   		 </div>
	
	</s:form>

</body>
</html>
