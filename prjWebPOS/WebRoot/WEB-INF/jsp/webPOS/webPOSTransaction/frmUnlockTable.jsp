<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Unlock Table</title>
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
var tblMenuItemDtl_MAX_ROW_SIZE=100;
var tblMenuItemDtl_MAX_COL_SIZE=4;
function funFillTables()
{
	funFetchBillingInProgressData();
}

function funFetchBillingInProgressData()
{
	funRemoveTableRows("tblKOT");
	var posCode="All";
	var searchurl=getContextPath()+"/loadBillingInProgressData.html";		
	 $.ajax({
	        type: "GET",
	        data:{ 
			},
	        url: searchurl,
	        dataType: "json",
	        async: false,
	        success: function(response)
	        {
	        	 var cnt=0;
	        	 var item1, item2,item3, item4;
	        	 funFillKOTTable(response.TableList);
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


function funValidate()
{
	//document.frmUnlockTable.action="unlockTable.html?KOTNo="+ "k0001";
	//document.frmUnlockTable.submit();
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
					col.innerHTML = "<td><input type=\"button\" id="+obj.TableNo+" value='"+obj.TableName+"'    style=\"width: 85px;height: 85px;margin-top: 5px;\"  onclick=\"funGetSelectedRowIndex(this,'tblKOT')\"/>";
				insertCol++;
			}
			else
			{					
				insertTR=tblTableDtl.insertRow();									
				insertCol=0;
				var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.TableNo+" value='"+obj.TableName+"'   style=\"width: 85px;height: 85px;margin-top: 5px;\" onclick=\"funGetSelectedRowIndex(this,'tblKOT')\"/>";
				insertCol++;
			}							
		});
}

function funGetSelectedRowIndex(obj,tableId)
{
	var index = obj.parentNode.parentNode.rowIndex;
	var cellIndex=obj.parentNode.cellIndex;
	
	 if(tableId=="tblKOT")
		{
		  	 var tableName = document.getElementById(tableId);
			 var code=obj.id;
			 var value=obj.value;
			 KOTNo=code;
			if(previousKOT=="")
			{
				prevKOTIndex=index;
				prevKOTCellIndex=cellIndex;
				tableName.rows[index].cells[cellIndex].innerHTML="<input  name=\"listOfAllTable["+0+"].strTableName\"  readonly=\"readonly\" id="+code+"  style=\"width: 100px;height: 100px; text-align: center;background: #ff0d0d;\" value='"+value+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				previousKOT=code;
			}
			else if(previousKOT==code)
			{
				tableName.rows[index].cells[cellIndex].innerHTML="<input type=\"button\" id="+code+" style=\"width: 100px;height: 100px; \" value='"+value+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				previousKOT="";
			}
			else
			{//tableName.rows[prevKOTIndex].cells[prevKOTCellIndex].innerHTML="<input type=\"button\" id="+previousKOT+" style=\"width: 100px;height: 100px;\" value='"+previousKOT+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				 
				 prevKOTIndex=index;
				 prevKOTCellIndex=cellIndex;
				 tableName.rows[index].cells[cellIndex].innerHTML="<input  name=\"listOfAllTable["+0+"].strTableName\"  readonly=\"readonly\" id="+code+" style=\"width: 100px;height: 100px;text-align: center; background: #ff0d0d;\" value='"+value+"'onclick=\"funGetSelectedRowIndex(this,'"+tableId+"')\"/>";
				 previousKOT=code;
			}
		}
}

</script>


</head>

<body onload="funFillTables()">
	<div id="formHeading">
		<label>Unlock Table</label>
	</div>

	<s:form name="POSUnlockTable" method="POST" action="unlockTable.html?saddr=${urlHits}" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">
		
		
		<div class="title" >
				
					<div class="row" style="background-color: #fff; display: -webkit-box; margin-bottom: 10px; margin-left: 0px;">
							<div style="border: 1px solid #ccc; display: block; height: 400px; margin:auto; overflow-x: auto; overflow-y: scroll; width: 100%;">
						
								<table id="tblKOT" style="width: 100%; text-align: center !important;">
									<tbody style="border-top: none;">
									</tbody>
								</table>
								
							</div>
					</div>
					
					<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 100%; margin-left: 15%;">
					
						<p align="center">
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="UNLOCK" id="Unlock" /></div>
							<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="button" value="RESET" id="Reset" /></div>
						</p>
						
				  	</div>
		</div>
		
		
<!-- 		<div> -->
<!-- 			<div style="margin-left: 200px; width: 70%;"> -->

<!-- 				<table class="masterTable" style="width: 50%;"> -->
<!--                  <tr>                    -->
<!-- 					<td> -->
<!-- 					<div id="divKOTDtl" style=" border: 1px solid #ccc; height: 425px;  overflow-x: auto; overflow-y: auto; width: 100%;">									 -->
											
<!-- 					<table id="tblKOT"   cellpadding="0" cellspacing="5"> -->
					
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
<!-- 			    <input type="submit" value="Unlock"	class="form_button"  id="Unlock" /> -->
<!-- 			    <input type="button" value="Reset" class="form_button"id="Reset" />   -->
<!-- 		</p> -->
		

	</s:form>

</body>
</html>