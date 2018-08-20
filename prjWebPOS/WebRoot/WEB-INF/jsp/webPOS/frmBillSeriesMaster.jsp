<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AREA MASTER</title>
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
var count=0;
var selectedRowIndex=0;
var k=1;
 $(document).ready(function () {
// 		  $('input#txtAreaCode').mlKeyboard({layout: 'en_US'});
// 		  $('input#txtAreaName').mlKeyboard({layout: 'en_US'});
		  
		  $("form").submit(function(event){
			  
			  	
				  funCallFormAction();
				 
			  
			});
		  
		  $("#cmbBillSeriesType").change(function () {
			  funFillPOSWiseBillSeries();
		    });
		}); 


		/**
		* Success Message After Saving Record
		**/
		$(document).ready(function()
		{
			var message='';
			<%if (session.getAttribute("success") != null) {
				if (session.getAttribute("successMessage") != null) {%>
					message='<%=session.getAttribute("successMessage").toString()%>';
			<%session.removeAttribute("successMessage");
				}
				boolean test = ((Boolean) session.getAttribute("success"))
						.booleanValue();
				session.removeAttribute("success");
				if (test) {%>
				alert("Data Saved \n\n"+ message);
						<%}
			}%>
	});
		
		function funCallFormAction()
		{
			
			var table = document.getElementById("tblBillSeriesDtl");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			
			if (rowCount > 0)
            {
				var tableOdDtl = document.getElementById("tblBillSeriesTypeDtl");
				var rowCountOfDtl = tableOdDtl.rows.length;
                if (rowCountOfDtl > 0)
                {
                    alert("Please Select All Types");
                    return;
                }
                for (var i = 0; i < rowCount; i++)
                {
                	var billseries=table.rows[i].cells[1].children[0].value;
                	
                    if (billseries == "" ||billseries.length == 0)
                    {
                        alert("Bill Series Can Not Be Empty.");
                        return;
                    }
                    if (billseries != "" && billseries.length >= 3)
                    {
                       alert(billseries + " Exceeds Bill Series Length.");
                        return;
                    }

//                     String billSeries = tblBillSeriesDtl.getValueAt(i, 1).toString();
                    for (var j = i + 1; j <rowCount; j++)
                    {
                        if (billSeries.equalsIgnoreCase(table.rows[i].cells[1].children[0].value))
                        {
                           alert("Bill Series Can Not Be Duplicate.");
                            return;
                        }
                    }
                }
            }    
			 
			
		}
		
		function funFillPOSWiseBillSeries()
		{
			
			funSetSelectedBillSeries();
			funLoadBillSeriesTypeData();
		}
		
		function funSetSelectedBillSeries()
		{
		var code = $("#cmbPOSName").val();
			var searchurl=getContextPath()+"/loadSetSelectedBillSeries.html?posCode="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	$.each(response,function(i,item){
				        	var type = item.strType;
				        	if(type!="")
				        	{	
				        	$("#cmbBillSeriesType").val(type);
				        	 document.getElementById("cmbBillSeriesType").disabled=true;
				        	funLoadOldBillSeries();
				        	funFillSelectedBillSeriesTypeDtlTable(type);
				        	}
				        	 else
				             {
				        		 document.getElementById("cmbBillSeriesType").disabled=false;
				                 //funFillNameWithCode("Group");
				                 funRemoveTableRows("tblBillSeriesDtl");
				                 funRemoveTableRows("tblBillSeriesTypeDtl");
				                  $("#cmbBillSeriesType").val("Group");
				                 funFillSelectedBillSeriesTypeDtlTable("Group");
				             }	
				        	
				        	});
				        	 
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
		
		function funLoadOldBillSeries()
		{
		var posCode = $("#cmbPOSName").val();
			var searchurl=getContextPath()+"/funLoadOldBillSeries.html?posCode="+posCode;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        	$.each(response,function(i,item){
				        		funFillBillSeriesDtlData(item.serialNo,item.strBillSeries,item.strNames,item.strPrintGTOfOtherBills,item.strPrintInclusiveOfTaxOnBill)
					        	
					        	});	
				      
				     
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
		
		function funFillSelectedBillSeriesTypeDtlTable(type)
		{
			funLoadBillSeriesTypeData(type);
		}
		
		function funLoadBillSeriesTypeData()
		{
		var code = $("#cmbBillSeriesType").val();
			var searchurl=getContextPath()+"/loadBillSeriesTypeData.html?billSeriesType="+code;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        
				        success: function (response) {
				        
				        	//funFillTableCol(item[0],item[1],item[2],item[3],item[4]);
		 	             	//	});
				        	
				        	funRemoveTableRows("tblBillSeriesTypeDtl");
				           	            	
				            	$.each(response,function(i,item){
				            	
				            		//alert(item.strGroupName);
				            		funfillBillSeriesTypeDtlGrid(code,item.strGroupName,item.strGroupCode);
				            				            				            	
					            	 
				            	});
				    
				          
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
		
		function funfillBillSeriesTypeDtlGrid(code,strGroupName,strGroupCode)
		{
			var flag=false;

		
			var table = document.getElementById("tblBillSeriesTypeDtl");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

			row.insertCell(0).innerHTML= "<input name=\"listDtl["+(rowCount)+"].strShortName\" readonly=\"readonly\" class=\"Box \" size=\"22%\" id=\"txtType."+(rowCount)+"\" value='"+code+"' onclick=\"funGetSelectedRowIndex(this)\">";  
		    row.insertCell(1).innerHTML= "<input name=\"listDtl["+(rowCount)+"].strGroupName\" readonly=\"readonly\" class=\"Box \" size=\"27%\" id=\"txtGroupName."+(rowCount)+"\" value='"+strGroupName+"' onclick=\"funGetSelectedRowIndex(this)\">";
		    row.insertCell(2).innerHTML= "<input name=\"listDtl["+(rowCount)+"].strGroupCode\" readonly=\"readonly\" class=\"Box \" size=\"28%\" id=\"txtGroupCode."+(rowCount)+"\" value='"+strGroupCode+"' onclick=\"funGetSelectedRowIndex(this)\">";
		    row.insertCell(3).innerHTML= "<input type=\"checkbox\" name=\"listDtl["+(rowCount)+"].strSelect\" size=\"12%\" id=\"chkOperational."+(rowCount)+"\" value=''"+false+"' onclick=\"funGetSelectedRowIndex(this)\">";
	      	
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
			 var itemName="";
			 var itemCode ="";
			 
			 function funAddRow() 
				{
				 var table = document.getElementById("tblBillSeriesTypeDtl");
				 var rowCount = table.rows.length;
				 while(selectedRowIndex>=0)
				{
				 
						 	if(itemCode!="" && itemName!="")
						 	{
							itemName=itemName+","+table.rows[selectedRowIndex].cells[1].children[0].value;
							itemCode=itemCode+","+table.rows[selectedRowIndex].cells[2].children[0].value;
							 table.deleteRow(selectedRowIndex);
							var isSelect=true;
							count++;
							selectedRowIndex--;
						
						 	}
						 	else
						 	{
						 		itemName=table.rows[selectedRowIndex].cells[1].children[0].value;
								itemCode=table.rows[selectedRowIndex].cells[2].children[0].value;
								 table.deleteRow(selectedRowIndex);
								count++;
								selectedRowIndex--;
								
						 	}
						 		
							
// 							var description=table.rows[selectedRowIndex].cells[0].innerHTML;
// 							var qty=table.rows[selectedRowIndex].cells[1].innerHTML; 
				}	
				 funClickOnAddButton(k,itemName,itemCode);
				 funFillBillSeriesDtlData(k,'',itemName,itemCode,false,false);	
				 k++;
				}
			 function funClickOnAddButton(serialNo,itemName,itemCode)
				{
				
					var searchurl=getContextPath()+"/funClickOnAddButton.html?itemName="+itemName+"&itemCode="+itemCode+"&serialNo="+serialNo;
					 $.ajax({
						        type: "GET",
						        url: searchurl,
						        dataType: "json",
						        
						        success: function (response) {
						        $.each(response,function(i,item){
						          
						        });
						    
						          
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
			 
			 function funFillBillSeriesDtlData(serialNo,billSeries,itemName,printGST,printTax)
				{
					var table = document.getElementById("tblBillSeriesDtl");
					var rowCount = table.rows.length;
					var row = table.insertRow(rowCount);
					row.insertCell(0).innerHTML= "<input name=\readonly=\"readonly\" class=\"Box \" size=\"2%\" id=\""+serialNo+"\" value='"+serialNo+"'/>";
					if(billSeries!="")
					{
						row.insertCell(1).innerHTML= "<input type=\"text\" readonly=\"readonly\" name=\"listBillSeriesDtl["+(rowCount)+"].strBillSeries\" class=\"Box \" size=\"10%\" id=\""+"bllSeries"+"\" value='"+billSeries+"' />";
						row.insertCell(2).innerHTML= "<input readonly=\"readonly\" name=\"listBillSeriesDtl["+(rowCount)+"].strNames\" class=\"Box \" size=\"18%\" id=\""+itemName+"\" value='"+itemName+"'/>";
					}
					else
					{	
					row.insertCell(1).innerHTML= "<input type=\"text\" name=\"listBillSeriesDtl["+(rowCount)+"].strBillSeries\" class=\"Box \" size=\"10%\" id=\""+"bllSeries"+"\" value='"+billSeries+"' />";
					row.insertCell(2).innerHTML= "<input name=\"listBillSeriesDtl["+(rowCount)+"].strNames\" class=\"Box \" size=\"18%\" id=\""+itemName+"\" value='"+itemName+"'/>";
					}
					
					row.insertCell(3).innerHTML= "<input id=\""+""+"\" type=\"checkbox\" name=\"BillGroupthemes\" value='"+false+"' class=\"Box\" />";	   
					
					row.insertCell(4).innerHTML= "<input id=\"chkStrPrintGst\" type=\"checkbox\" name=\"listBillSeriesDtl["+(rowCount)+"].strPrintGTOfOtherBills\"  class=\"Box\" onclick=\"return funGetSelectedCheckBo(this)\"/>";
					row.insertCell(5).innerHTML= "<input id=\"chkStrPrintTax\" type=\"checkbox\" name=\"listBillSeriesDtl["+(rowCount)+"].strPrintInclusiveOfTaxOnBill\" value='"+printTax+"' class=\"Box\" />";
					row.insertCell(6).innerHTML= "<input type=\"hidden\" name=\"listBillSeriesDtl["+(rowCount)+"].serialNo\" class=\"Box \" size=\"15%\" id=\""+serialNo+"\" value='"+serialNo+"' />";
				}
			 function funGetSelectedCheckBo(obj)
			 {
				 var v1 = $('#chkStrPrintGst').val($(this).is(':checked'));
				 
			 }
			 
			 function funGetSelectedRowIndex(obj)
				{
					 var index = obj.parentNode.parentNode.rowIndex;
					 var table = document.getElementById("tblBillSeriesTypeDtl");
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
						 row.hilite = true;
					 }
					 
					
				}
			 function btnRemove_onclick() 
				{
					
					
					var table = document.getElementById("tblBillSeriesDtl");
				    table.deleteRow(selectedRowIndex);
					
				}			 
			 	 
</script>


</head>

<body onload="funFillPOSWiseBillSeries()">
	<div id="formHeading">
		<label>Area Master</label>
	</div>
	<s:form name="AreaForm" method="POST" action="savePOSBillSeriesMasterData.html" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;max-width:880px;min-width:150px;margin-top:2%;">

		<br />
		<br />
	
	<div class="title">
		
			<div class="row" style="background-color: #fff;display: -webkit-box;">
				<div class="element-input col-lg-6" style="width: 15%;"> 
    				<label class="title">POS Name</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;">
    				<s:select id="cmbPOSName" path="strPOSName" items="${posList}"></s:select>
    			</div>
    		</div>
    		
    		<div class="row" style="background-color: #fff;display: -webkit-box;">
				<div class="element-input col-lg-6" style="width: 15%;"> 
    				<label class="title">Bill Series Type</label>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;width: 25%;">
    				<s:select id="cmbBillSeriesType" path="strType" >
    					<option value="Group">Group</option>
				 		<option value="Sub Group">Sub Group</option>
				  		<option value="Menu Head">Menu Head</option>
				   		<option value="Revenue Head">Revenue Head</option>
    				</s:select>
    			</div>
    			<div class="element-input col-lg-6" style="margin-bottom: 10px;">
    				<input id="btnAdd" type="button" value="Add" onclick="return funAddRow();" />
    			</div>
    		</div>
    		
    		<div>
    			<table style="background-color:  #2fabe9a1;width: 100%;border: 1px solid;" >
    				<thead>
						<tr>
							<th style="width: 25%">Type</th>
							<th style="width: 30%">Name</th>
							<th style="width: 30%">Code</th>
							<th style="width: 5%">Select</th>
						</tr>
				   </thead>
    			</table>
    		</div>
    		
    		<div>
    			<table id="tblBillSeriesTypeDtl" style="background-color:  #d9edf742;width: 100%;border: 1px solid;">
    				<tbody>
    					
    				</tbody>
    			</table>
    		</div>
    		<br/>
    		
    		<div>
    			<table style="background-color:  #2fabe9a1;width: 100%;border: 1px solid;" >
    				<thead>
						<tr>
							<th style="width: 2%">Sr.No</th>
							<th style="width: 10%">Bill Series</th>
							<th style="width: 18%">Contents</th>
							<th style="width: 1%">Remove</th>
							<th style="width: 14%">Print Grand Total of Other Bills</th>
							<th style="width: 12%">Print Inclusive of All Taxes</th>
						</tr>
					</thead>
				</table>
			</div>
			
			<div>
				<table id="tblBillSeriesDtl" style="background-color:  #d9edf742;width: 100%;border: 1px solid;">
    				<tbody>
    					
    					<col style="width:6%;">
						<col style="width:15%">
						<col style="width:30%">
						<col style="width:7%;">
						<col style="width:23%;">
						<col style="width:20%;">
    					
    				</tbody>
    			</table>
			</div>
			
			<br/>
    		
    		<div class="row" style="background-color: #fff;display: -webkit-box;">
				<div class="element-input col-lg-6" style="width: 15%;"> 
					<input id="btnRemove" type="button"value="Remove" onclick="return btnRemove_onclick();"></input>
				</div>
			</div>
			<br/>
			<div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 200px;">
     			<p align="center">
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="submit" value="Submit"/></div>
          
            		<div class="submit col-lg-4 col-sm-4 col-xs-4"><input type="reset" value="Reset" onclick="funResetFields()"></div>
     			</p>
   		</div>
   
    </div>
	
	
<!-- 		<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td><label>POS Name</label></td> -->
<%-- 				<td><s:select id="cmbPOSName" name="cmbPOSName" --%>
<%-- 						path="strPOSName" items="${posList}" cssClass="BoxW124px" /></td> --%>

<!-- 			</tr> -->

<!-- 			<tr> -->
<!-- 				<td><label>Bill Series Type</label></td> -->
<!-- 				<td> -->
<%-- 				<s:select id="cmbBillSeriesType" name="cmbBillSeriesType" path="strType" cssClass="BoxW124px" > --%>
<!-- 				<option value="Group">Group</option> -->
<!-- 				 <option value="Sub Group">Sub Group</option> -->
<!-- 				  <option value="Menu Head">Menu Head</option> -->
<!-- 				   <option value="Revenue Head">Revenue Head</option> -->
<%-- 				 </s:select> --%>
<!-- 				</td> -->
<!-- 				<td colspan=""><input id="btnAdd" type="button" class="smallButton" value="Add" onclick="return funAddRow();"></input> -->
<!-- 				</td> -->
<!-- 			</tr> -->
<!-- 	</table> -->

<!-- 		<table border="1" class="myTable" style="width: 80%; margin: auto;"> -->
<!-- 			<thead> -->
<!-- 				<tr> -->
<!-- 					<th style="width: 28%">Type</th> -->
<!-- 					<th style="width: 36.5%">Name</th> -->
<!-- 					<th style="width: 28%">Code</th> -->
<!-- 					<th style="width: 5.5%">Select</th> -->
<!-- 				</tr> -->

<!-- 			</thead> -->
<!-- 		</table> -->
<!-- 		<div -->
<!-- 			style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 150px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 80%;"> -->
<!-- 			<table id="tblBillSeriesTypeDtl" class="transTablex col5-center" -->
<!-- 				style="width: 100%;"> -->
<!-- 				<tbody> -->
<%-- 					<col style="width:28.2%;"><!--  COl1   --> --%>
<%-- 					<col style="width:37%"><!--  COl2   --> --%>
<%-- 					<col style="width:28.5%"><!--  COl3   --> --%>
<%-- 					<col style="width:6%;"><!--  COl4   -->								 --%>
<!-- 			</table> -->
<!-- 		</div> -->
		
<!-- 		<table border="1" class="myTable" style="width: 80%; margin: auto;"> -->
<!-- 			<thead> -->
<!-- 				<tr> -->
<!-- 					<th style="width: 6%">Sr.No</th> -->
<!-- 					<th style="width: 16%">Bill Series</th> -->
<!-- 					<th style="width: 28%">Contents</th> -->
<!-- 					<th style="width: 6%">Remove</th> -->
<!-- 					<th style="width: 6%">Print Grand Total of Other Bills</th> -->
<!-- 					<th style="width: 6%">Print Inclusive of All Taxes</th> -->
<!-- 				</tr> -->

<!-- 			</thead> -->
<!-- 		</table> -->
<!-- 		<div -->
<!-- 			style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 150px; margin: auto; overflow-x: hidden; overflow-y: scroll; width: 80%;"> -->
<!-- 			<table id="tblBillSeriesDtl" class="transTablex col5-center" -->
<!-- 				style="width: 100%;"> -->
<!-- 				<tbody> -->
<%-- 					<col style="width:6.5%;"><!--  COl1   --> --%>
<%-- 					<col style="width:15.5%"><!--  COl2   --> --%>
<%-- 					<col style="width:30.5%"><!--  COl3   --> --%>
<%-- 					<col style="width:5.5%;"><!--  COl4   -->	 --%>
<%-- 					<col style="width:5.5%;"><!--  COl5   -->	 --%>
<%-- 					<col style="width:5.5%;"><!--  COl6   -->								 --%>
<!-- 			</table> -->
<!-- 		</div>	 -->
			
<!-- 	<table class="masterTable"> -->
<!-- 			<tr> -->
<!-- 				<td colspan=""><input id="btnRemove" type="button" -->
<!-- 								class="smallButton" value="Remove" -->
<!-- 								onclick="return btnRemove_onclick();"></input></td> -->
<!-- 			</tr> -->
<!-- 	</table> -->
<!-- 		<br /> -->
<!-- 		<br /> -->
	
<!-- 		<p align="center"> -->
<!-- 			<input type="submit" value="Submit" tabindex="3" class="form_button" /> -->
<!-- 			<input type="reset" value="Reset" class="form_button" -->
<!-- 							onclick="funResetFields()" /> -->
<!-- 		</p> -->
	
				</s:form>

</body>
</html>
