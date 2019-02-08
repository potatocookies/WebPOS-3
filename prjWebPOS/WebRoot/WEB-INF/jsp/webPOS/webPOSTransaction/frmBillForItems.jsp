<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bill For Items</title>
<style>
.ui-autocomplete 
{
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
* html .ui-autocomplete 
{
    height: 200px;
}



</style>


<script type="text/javascript">



var selectedRowIndex=0;
var toTableName;
var kotWiseItmeList=[];


$(document).ready(function() 
{
	$("#cmbTable").change(function() 
	{
    	funFetchBusyTableData();
        	
	});  
});


//Footer btn click

	function funFooterButtonClicked(objFooterButton)
	{
		switch(objFooterButton.id)
		{
		
			case "Home":
				funHomeBtnclicked();
				break;
				
		
				
			case "Make Bill":
				funMakeBillFromBillForItems();
				break;
			
		}
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
		
		$("#tblBussyTableItems").empty();
		kotWiseItmeList=[];
		
		
		
		var searchurl=getContextPath()+"/funGetItemsForTable.html?bussyTableNo="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	$.each(response.itemList, function(i,item)
	    				{			
	    		    		
			        		funAddBusyTableData(item);
	    		    		
	    			  	});
			        	
			        	
			        	/* Loading KOT wise item list */
			        	
			        	$.each(response.kotWiseItemList, function(i,item)
	    				{			
	    		    		
			        		var singleObj = {}
							
			        		singleObj['itemCode'] =item.strItemCode;
							singleObj['itemName'] =item.strItemName;
						    singleObj['quantity'] =item.dblQuantity;
						    singleObj['amount'] = item.dblAmount;						    						   
						    singleObj['kotNo'] =item.strKOTNo;
						    
						    
						    kotWiseItmeList.push(singleObj);
	    		    		
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

	function funAddBusyTableData(item)
	{
		var table = document.getElementById("tblBussyTableItems");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		
		row.insertCell(0).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].strItemName\" readonly=\"readonly\" class=\"Box \" id=\"txtItemName."+ (rowCount) +"\" size=\"40%\" value='"+item.strItemName+"'>";
   	    row.insertCell(1).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblRate\" readonly=\"readonly\" class=\"Box \" id=\"txtRate."+ (rowCount) +"\" size=\"10%\" value='"+item.dblRate+"'>";
		row.insertCell(2).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblQuantity\" readonly=\"readonly\" class=\"Box \" id=\"txtItemQty."+ (rowCount) +"\" size=\"10%\" value='"+item.dblQuantity+"'>";
		row.insertCell(3).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].moveItemQty\" readonly=\"readonly\" class=\"Box \" id=\"txtMoveQty."+ (rowCount) +"\" size=\"10%\" value='"+item.dblQuantity+"' onclick=\"funGetSelectedRowIndex(this)\" >";
		row.insertCell(4).innerHTML= "<input name=\"listBusyTableDtl["+(rowCount)+"].dblAmount\" readonly=\"readonly\" class=\"Box \" id=\"txtAmount."+ (rowCount) +"\" size=\"20%\" value='"+item.dblAmount+"'>";
		row.insertCell(5).innerHTML= "<input type=\"checkbox\" name=\"listBusyTableDtl["+(rowCount)+"].strApplicableYN\" size=\"10%\" value='"+false+"' onclick=\"funGetSelectedRowIndex(this)\" >";
		row.insertCell(6).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strItemCode\" readonly=\"readonly\" class=\"Box \" id=\"txtItemCode."+ (rowCount) +"\" size=\"0%\" value='"+item.strItemCode+"'>";
		row.insertCell(7).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strWaiterNo\" readonly=\"readonly\" class=\"Box \" id=\"txtWaiterNo."+ (rowCount) +"\" size=\"0%\" value='"+item.strWaiterCode+"'>";
		row.insertCell(8).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strSubGroupCode\" readonly=\"readonly\" class=\"Box \" id=\"txtSubGroupCode."+ (rowCount) +"\" size=\"0%\" value='"+item.strSubGroupCode+"'>";		  
		row.insertCell(9).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strSubGroupName\" readonly=\"readonly\" class=\"Box \" id=\"txtSubGroupName."+ (rowCount) +"\" size=\"0%\" value='"+item.strSubGroupName+"'>";		  		
		row.insertCell(10).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strGroupCode\" readonly=\"readonly\" class=\"Box \" id=\"txtGroupCode."+ (rowCount) +"\" size=\"0%\" value='"+item.strGroupCode+"'>";		  
		row.insertCell(11).innerHTML= "<input type=\"hidden\" name=\"listBusyTableDtl["+(rowCount)+"].strSubGroupName\" readonly=\"readonly\" class=\"Box \" id=\"txtGroupName."+ (rowCount) +"\" size=\"0%\" value='"+item.strGroupName+"'>";		  

	}

	function funGetSelectedRowIndex(obj)
	{
		var person = "";
		 var index = obj.parentNode.parentNode.rowIndex;
		 var table = document.getElementById("tblBussyTableItems");
		 selectedRowIndex=index;
		 
		 
		 row = table.rows[selectedRowIndex];		
		 var originalQty=table.rows[index].cells[2].children[0].value;
		 		 		
		 var typ=obj.type;
		 
		 if(typ=='checkbox')
		 {
			 var isChecked=obj.checked;
			 
			 if(!isChecked)
			 {
				 table.rows[index].cells[5].children[0].checked=false;
				 row.style.backgroundColor='#ffffff';
			 }
			 else
		     {
				 table.rows[index].cells[5].children[0].checked=true;
				 row.style.backgroundColor='#ffd966';
				 
				 
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
		     }
		 }
		 else
		 {
			 var isChecked=table.rows[index].cells[5].children[0].checked;
			 
			 if(isChecked)
			 {
				 table.rows[index].cells[5].children[0].checked=false;
				 row.style.backgroundColor='#ffffff';
			 }
			 else
		     {
				 table.rows[index].cells[5].children[0].checked=true;
				 row.style.backgroundColor='#ffd966';
				 
				 
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
		     }
		 }		
	}

	function isItemSelected()
	{
		var flag=false;
		var table = document.getElementById("tblBussyTableItems");
		var rowCount = table.rows.length;
		if(rowCount<=0)
		{			
			flag=false;
		}	
		
		$('#tblBussyTableItems tr').each(function() 
		{
			  var checkbox = $(this).find("input[type='checkbox']");

			    if( checkbox.prop("checked") )
			    {
			    	 flag= true;			    	 			    	
			    } 
		});
		
		  
		  if(!flag)
		  {
			  alert("Select items");
			  
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


	function funHomeBtnclicked()
	{
		var loginPOS="${loginPOS}";

		if (arrKOTItemDtlList.length > 0)
	    {
	       if(confirm( "Do you want to end Transaction?"))	    	   
	    	   window.location ="frmGetPOSSelection.html?strPosCode="+loginPOS;
		else
	        return;
	    } 
	    else
	    {
	    	window.location ="frmGetPOSSelection.html?strPosCode="+loginPOS;
	    }	
	}
	
	
	
	/*
	*make bill button clicked
	*/
	function funMakeBillFromBillForItems()
	{
		var isSelectedItem=isItemSelected();
		
		if(isSelectedItem)
		{
			funMakeBillForItemsClicked();			
		}
		
    }
	
	//display billsettlement window tab
	function funMakeBillForItemsClicked()
	{

		var $rows = $('#tblSettleItemTable').empty();
		document.getElementById("tab2").style.display='block';		
	    document.getElementById("tab1").style.display='none';
	    
	    operationType="DineIn";
	    transactionType="Bill For Items";
	    
	    finalSubTotal=0.00;
		finalDiscountAmt=0.00;
		finalNetTotal=0.00;
		taxTotal=0.00;
		taxAmt=0.00;
		finalGrandTotal=0.00;
		
		gTableNo=$("#cmbTable option:selected" ).val();
		gTableName=$("#cmbTable option:selected" ).text();

		
		$("#tableNameForDisplay").text("Table No : "+gTableName);
	    
	    var listItmeDtl=[];
	    var mergeduplicateItm = new Map();
	    var hmItempMap=new Map();
	    
	    var tblBillItemDtl=document.getElementById('tblBussyTableItems');
		var rowCount = tblBillItemDtl.rows.length;
	    
		for(var i=0;i<rowCount;i++)
		{
			var checkbox=tblBillItemDtl.rows[i].cells[5].children[0];
			
			 if( checkbox.checked )//if selected
	    	  {
				 
					var itemName=tblBillItemDtl.rows[i].cells[0].children[0].value;
					//rate
					var itemQty=tblBillItemDtl.rows[i].cells[2].children[0].value;
					var itemMoveQty=tblBillItemDtl.rows[i].cells[3].children[0].value;
					var itemAmt=tblBillItemDtl.rows[i].cells[4].children[0].value;
					//5 checkbox
					var itemCode=tblBillItemDtl.rows[i].cells[6].children[0].value;
   		    	
	  		    	hmItempMap.set(itemCode,itemName);
	  	         
	  		    	var isModifier=false;
	  		    	if(itemName.startsWith("-->"))
	  				{
	  		    		isModifier=true;
	  				}
 		    	
					//7 waiterNo
	  		    	var subgroupcode=tblBillItemDtl.rows[i].cells[8].children[0].value;
			 		var subgroupName=tblBillItemDtl.rows[i].cells[9].children[0].value;
	  		    	var groupcode=tblBillItemDtl.rows[i].cells[10].children[0].value;
			 		var groupName=tblBillItemDtl.rows[i].cells[11].children[0].value;
								 							
					hmSubGroupMap.set(subgroupcode, subgroupName);
					hmGroupMap.set(groupcode, groupName); 
					
					
						
						
					var singleObj = {}

				    singleObj['itemCode'] =itemCode;
					singleObj['itemName'] =itemName;
/* 				    singleObj['quantity'] =itemQty; */

					 singleObj['quantity'] =itemMoveQty;					
/* 					singleObj['moveQuantity'] =itemMoveQty; */
				    
				    var rate=itemAmt/itemQty;
				    singleObj['rate'] =rate;				   
				    
				    singleObj['amount'] = rate*itemMoveQty;
				    
				    singleObj['isModifier'] =isModifier;
				    
				    singleObj['discountPer'] = 0.0;
				    singleObj['discountAmt'] =0.0;
				    singleObj['strSubGroupCode'] =subgroupcode;
				    singleObj['strGroupcode'] =groupcode;
				    
				   
				    
				    listItmeDtl.push(singleObj);
															
					
					mergeduplicateItm.set(itemCode,singleObj);	
	    	   }
		}
	    
	    
		    /**
			*calculating promotions and filling data to grid for bill print	
			*/
			funCalculatePromotion(listItmeDtl);
			
			funRefreshSettlementItemGrid();
	}
	
	
	
	 

</script>


</head>

<body >
	<div id="formHeading">
		<label>Bill For Items</label>
	</div>
	<s:form name="frmBillForItems" method="POST" action="" class="formoid-default-skyblue" style="background-color:#FFFFFF;font-size:14px;font-family:'Open Sans','Helvetica Neue','Helvetica',Arial,Verdana,sans-serif;color:#666666;margin-top:2%;">

		<br />
		<br />
		
		<!-- Pratiksha -->
		<div class="title">
			<div style=" width: 98%; height: 450px;float:left;  overflow-x: hidden; border-collapse: separate; overflow-y: auto;padding-left: 50px;padding-right: 50px;">
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;margin-left: 15px;">
					<div class="element-input col-lg-6" style="width: 30%;margin-left: 34px; size='10'; margin-left: auto;"> 
	    				<s:select id="cmbTable" path="strTableName" items="${bussyTableList}" />
	    			</div>
	    			<div class="element-input col-lg-6" style="width: 25%; margin-left: -300px" >
	    				<label class="title" >BUSY TABLES</label>
	    			</div>
	    		
				</div>
				
				<table border="2" style="width:100%;margin: auto;"  >
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
					
					<table border="2" id="tblBussyTableItems" style="width: 100%;">
							<tbody>    

							 </tbody>							
					 </table>
			</div>
		</div>
		
		
		<%-- <div class="title">
			<div style=" width: 98%; height: 450px;float:left;  overflow-x: hidden; border-collapse: separate; overflow-y: auto;padding-left: 50px;padding-right: 50px;">
				<div class="row" style="background-color: #fff;margin-bottom: 10px;display: -webkit-box;margin-left: 15px;">
					<div class="element-input col-lg-6" style="width: 30%;margin-left: 34px;"> 
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
		</div> --%>
		
		
		
		<br/><br/>
		
		<!-- Pratiksha -->
		<div class="submit col-lg-4 col-sm-4 col-xs-4">
  				<input type="submit" value="View KOT" style="margin-left: -210px";onclick="funFooterButtonClicked(this)" class="btn btn-spos-outline-success"/></div>
              		<div class="submit col-lg-4 col-sm-4 col-xs-4">
              		<input type="submit" value="Make Bill" style="margin-left: 200px";onclick="funFooterButtonClicked(this)" class="btn btn-spos-outline-success"/></div>
     		        <div class="submit col-lg-4 col-sm-4 col-xs-4">
     		        </div><input type="button" value="Close" style="margin-left: 200px" onclick="funFooterButtonClicked(this)" class="btn btn-spos-outline-success"></div>
		
		<!-- <div class="col-lg-10 col-sm-10 col-xs-10" style="width: 70%;margin-left: 240px;">
     		<p align="center">
     		  
     			<input type="button"  id="Home" 	    value="BACK"    		style="width: 100px;height: 50px; white-space: normal;"   				onclick="funFooterButtonClicked(this)" class="btn btn-spos-outline-success"/>
            	<input type="button"  id="Make Bill"    value="Make Bill"    	style="width: 100px;height: 50px; white-space: normal; "   onclick="funFooterButtonClicked(this)" class="btn btn-spos-outline-success"/>
     		  
   		</div> -->
		
		
	
	</s:form>

</body>
</html>
