<%@ page import="java.util.LinkedList"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>


<style type="text/css">

.searchTextBox {
    background: #FFF url(../images/textboxsearchimage.png) no-repeat 192px 2px;
    background-color: inherit;
    border: 1px solid #060006;
    outline: 0;
    padding-left: 5px;
    height: 25px;
    width: 203px;
}
	
	.btn{
  white-space: normal;
  
  -webkit-appearance: none;

}
</style>

<script type="text/javascript">
var gMobileNo="";
	var selectedRowIndex=0;
	var fieldName;
	var tblMenuItemDtl_MAX_ROW_SIZE=100;
	var tblMenuItemDtl_MAX_COL_SIZE=4;
	var menucode,flagPopular;

	var itemPriceDtlList=new Array();
	var hmHappyHourItems = new Map();
	var hmCMSMemberForTable=new Map();
	
	var currentDate="";
	var currentTime="";
	var dayForPricing="";
	var gDebitCardPayment="";
	var gMultiWaiterSelOnMakeKOT="${gMultiWaiterSelOnMakeKOT}";
	var gMenuItemSortingOn="${gMenuItemSortingOn}";
	var gSelectWaiterFromCardSwipe="${gSelectWaiterFromCardSwipe}";
	var gCheckDebitCardBalanceOnTrans="${gCheckDebitCardBalanceOnTrans}";
	var gCMSIntegrationYN="${gCMSIntegrationYN}";
	var gCMSMemberCodeForKOTJPOS="${gCMSMemberCodeForKOTJPOS}";
	var gCRMInterface="${gCRMInterface}";
	var gCustAddressSelectionForBill="${gCustAddressSelectionForBill}";
	var gSkipPax="${gSkipPax}";
	var gSkipWaiter="${gSkipWaiter}";
	var gPrintType="${gPrintType}";
	var gCustomerCode="",gCustomerName="";
	var flgCheckNCKOTButtonColor=false;
	var ncKot="N",reasonCode="",cmsMemCode="",cmsMemName="",globalTableNo="",globalDebitCardNo="",taxAmt=0,homeDeliveryForTax="N",gTakeAway="No";
	var arrListHomeDelDetails= new Array();
	
	var arrKOTItemDtlList=new Array();
	//start PLU Search funactionality
	$(document).ready(function()
	{
		document.getElementById("divPLU").style.display='none';
		$("#txtItemSearch").keyup(function()
		{
			searchTable($(this).val());
		});
	});
	function searchTable(inputVal)
	{
		var table = $('#tblItems');
		table.find('tr').each(function(index, row)
				{
					var allCells = $(row).find('td');
					if(allCells.length > 0)
					{
						var found = false;
						allCells.each(function(index, td)
						{
							var regExp = new RegExp(inputVal, 'i');
							if(regExp.test($(td).find('input').val()))
							{
								found = true;
								return false;
							}
						});
						if(found == true)$(row).show();else $(row).hide();
					}
				});;
	}		 
	//end PLU Search funactionality		
	
	
	
	
	//Apply Validation on Number TextFiled
	function funApplyNumberValidation() {
		$(".numeric").numeric();
		$(".integer").numeric(false, function() {
			alert("Integers only");
			this.value = "";
			this.focus();
		});
		$(".positive").numeric({
			negative : false
		}, function() {
			alert("No negative values");
			this.value = "";
			this.focus();
		});
		$(".positive-integer").numeric({
			decimal : false,
			negative : false
		}, function() {
			alert("Positive integers only");
			this.value = "";
			this.focus();
		});
		$(".decimal-places").numeric({
			decimalPlaces : maxQuantityDecimalPlaceLimit,
			negative : false
		});
		$(".decimal-places-amt").numeric({
			decimalPlaces : maxAmountDecimalPlaceLimit,
			negative : false
		});
	}
	
	
	//function to update item which is alredy order item (duplicate)
	function funUpdateTableBillItemDtlFor(objMenuItemPricingDtl,qty)
	{
		$('#tblBillItemDtl tbody tr').each(function () 
		{
			//'td:nth-child(4)' for itemcode 
		    $('td:nth-child(4)', this).each(function () 
		    {	     
		     	var element=$(this).html();	
		        	
		       	var itemCode=$(element).attr("value");
		       	if(itemCode==objMenuItemPricingDtl.strItemCode)
		       	{
		       	 	var rowIndex = this.parentNode.rowIndex;					
		    	 	var colIndex = 2; /* this.cellIndex; *///for qty
		    	 	
		    	 	var oldQty = $(this.parentNode).find(".itemQty").val(); 
		    	 	var oldAmt= $(this.parentNode).find(".itemAmt").val(); 
				
		    	 	var rate=parseFloat(oldAmt)/parseFloat(oldQty);
		    	 	
		    	 	    	 
		    	 	var newQty=parseFloat(oldQty)+parseFloat(qty);
		    	 	
		    	 	var newAmt=parseFloat(rate)*parseFloat(newQty);
		    	 	
		    	 	$(this.parentNode).find(".itemQty").val(parseFloat(newQty)); 
		    	 	$(this.parentNode).find(".itemAmt").val(parseFloat(newAmt));
		    	 			       		
		       	}		        			        
		     });		
		});	
	}
	
	
	//function to to check is alredy order item (duplicate)
	function funIsAlreadyOrderedItem(objMenuItemPricingDtl)
	{		
		var isOrdered=false;
		$('#tblBillItemDtl tbody tr').each(function () 
		{
			//'td:nth-child(4)' for itemcode 
		    $('td:nth-child(4)', this).each(function () 
		    {	     
		     	var element=$(this).html();	
		        	
		       	var itemCode=$(element).attr("value");
		       	if(itemCode==objMenuItemPricingDtl.strItemCode)
		       	{		       	 
		       		isOrdered=true;
		       	}		        			        
		     });						
		});	
		
		return isOrdered;
		
	}
	function funFillOldKOTTimeDtl(objMenuItemPricingDtl)
	{		
		var tblBillItemDtl=document.getElementById('tblOldKOTItemDtl');
		
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
		var kotNo= $('#txtKOTNo').val();
		var PaxNo= $('#txtPaxNo').val();
		var tableNo = $('#txtTableNo').val();
		var WaiterNo = $('#txtWaiterNo').val();
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"   class=\"itemName\"   style=\"text-align: left; \"    id=\"strItemName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.kotNo+"           "+objMenuItemPricingDtl.kotTime+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right;\"  id=\"dblQuantity."+(rowCount)+"\" value=' '/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right;\" id=\"dblAmount."+(rowCount)+"\" value='' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left;\" id=\"strItemCode."+(rowCount)+"\" value='' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right;\"  id=\"strSerialNo."+(rowCount)+"\" value='' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='' />";	    
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='' />";
	      
		
		
	}
	
	function funFillOldKOTItemDtl(objMenuItemPricingDtl)
	{		
		var tblBillItemDtl=document.getElementById('tblOldKOTItemDtl');
		
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
	    var kotNo= $('#txtKOTNo').val();
		var PaxNo= $('#txtPaxNo').val();
		var tableNo = $('#txtTableNo').val();
		var WaiterNo = $('#txtWaiterNo').val();		
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"   class=\"itemName\"   style=\"text-align: left;\"    id=\"strItemName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strItemName+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right;\"   id=\"dblQuantity."+(rowCount)+"\" value='"+objMenuItemPricingDtl.dblItemQuantity+"'/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right;\"  id=\"dblAmount."+(rowCount)+"\" value='"+objMenuItemPricingDtl.dblAmount+"' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left;\"  id=\"strItemCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strItemCode+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right;\"  id=\"strSerialNo."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSerialNo+"'/>";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='"+PaxNo+"' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='"+kotNo+"' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='"+WaiterNo+"' />";
	       	    
		
		
		
	}
	//function to add items to bill item table
	function funFillTableBillItemDtl(objMenuItemPricingDtl,price,qty)
	{	
		var itemName=objMenuItemPricingDtl.strItemName.replace(/&#x00A;/g," ");
		var tblBillItemDtl=document.getElementById('tblBillItemDtl');
		
		var rowCount = tblBillItemDtl.rows.length;
		
		
		
	    var kotNo= $('#txtKOTNo').val();
	    var PaxNo= $('#txtPaxNo').val();
	    var tableNo = $('#txtTableNo').val();
	    var WaiterNo = $('#txtWaiterNo').val();
	   
	    if(rowCount==1)
		{
			funFillKOTTimeDtl(rowCount,kotNo);
			rowCount++;
		}
	    else
	    {
			
			
			var currentTime = new Date();
			var kotTime=currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();
			var i=1;
// 			document.getElementById("itemName."+(i)).value=kotNo+"           "+kotTime;
			
		}
	    
	    var insertRow = tblBillItemDtl.insertRow(rowCount);
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    
	    
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].itemName\" id=\"itemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].quantity\" id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].amount\" id=\"amount."+(rowCount)+"\" value='"+price+"'/>";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].itemCode\" id=\"itemCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strItemCode+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='"+PaxNo+"' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='"+kotNo+"' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='"+WaiterNo+"' />";
	      
	    
	    
// 	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"   name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].strItemName\" id=\"strItemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
// 	    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].dblQuantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
// 	    col3.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"  name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].dblAmount\" id=\"dblAmount."+(rowCount)+"\" value='"+price+"'/>";
// 	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"   name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].strItemCode\" id=\"strItemCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strItemCode+"' />";
// 	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"  name=\"listOfMakeKOTBillItemDtl["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
// 	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
// 	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
// 	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
// 	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";	    
		
		
		
	}
	
	function funFillKOTTimeDtl(rowCount,kotNo)
	{		
		var currentTime = new Date();
		var kotTime=currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();
		
		var insertRow = tblBillItemDtl.insertRow(rowCount);
	
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"   class=\"itemName\"   style=\"text-align: left; color:blue;\"     id=\"strItemName."+(rowCount)+"\" value='"+kotNo+"           "+kotTime+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: left; color:blue;\"  id=\"dblQuantity."+(rowCount)+"\" value=' '/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: left; color:blue;\"  id=\"dblAmount."+(rowCount)+"\" value='' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"  id=\"strItemCode."+(rowCount)+"\" value='' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: left; color:blue;\"   id=\"strSerialNo."+(rowCount)+"\" value='' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='' />";	    
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='' />";
	      
		
		
	}
	
	
	function funAddModifierTableBillItemDtl(objMenuItemPricingDtl,itemCode)
	{	
		funFillKOTList();
		deleteTableRows();
		 var kotNo= $('#txtKOTNo').val();
		    var PaxNo= $('#txtPaxNo').val();
		    var tableNo = $('#txtTableNo').val();
		    var WaiterNo = $('#txtWaiterNo').val();
		var tblBillItemDtl=document.getElementById('tblBillItemDtl');
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
		var code=itemCode+"!"+objMenuItemPricingDtl.strModifierCode;
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    /* .listModifierDtl["+(rowCount)+"]. */
	      col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierDescription\" id=\"strItemName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strModifierName+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].quantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+1.00+"' />";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].amount\" id=\"dblAmount."+(rowCount)+"\" value='"+objMenuItemPricingDtl.dblRate+"' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierCode\" id=\"strItemCode."+(rowCount)+"\" value='"+code+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupCode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='"+PaxNo+"' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='"+kotNo+"' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='"+WaiterNo+"' />";
	      
		for(var i=selectedRowIndex+1;i<kotListForModifier.length;i++)
			{
			
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
		var data=kotListForModifier[i];
		
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    var col10=insertRow.insertCell(9);
	    var col11=insertRow.insertCell(10);
	    var col12=insertRow.insertCell(11);
	    var col13=insertRow.insertCell(12);
	    

	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierDescription\" id=\"strItemName."+(rowCount)+"\" value='"+data[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].quantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+data[1]+"' onclick=\"funChangeQty(this)\"/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].amount\" id=\"dblAmount."+(rowCount)+"\" value='"+data[2]+"' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierCode\" id=\"strItemCode."+(rowCount)+"\" value='"+data[3]+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+data[5]+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupCode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount-1)+"\" value='"+data[6]+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+data[7]+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+data[8]+"' />";	    
	    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"tableNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].tableNo\" id=\"tableNo."+(rowCount)+"\" value='"+tableNo+"' />";
	    col11.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"paxNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].paxNo\" id=\"paxNo."+(rowCount)+"\" value='"+PaxNo+"' />";
	    col12.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"KOTNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].KOTNo\" id=\"KOTNo."+(rowCount)+"\" value='"+kotNo+"' />";
	    col13.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"waiterNo\"  name=\"listItemsDtlInBill["+(rowCount)+"].waiterNo\" id=\"waiterNo."+(rowCount)+"\" value='"+WaiterNo+"' />";
	      
			}
		
		
	}
	function deleteTableRows()
	{
		var table = document.getElementById("tblBillItemDtl");
		var rowCount = table.rows.length;
		while(rowCount>selectedRowIndex+1)
		{
			table.deleteRow(selectedRowIndex+1);
			rowCount--;
		}
	}
	var kotListForModifier = new Array();
	 function funFillKOTList()
	{
		var i=0;
		var checkKOT=0;
		kotListForModifier = new Array();
		arrKOTItemDtlList = new Array();
		 $('#tblBillItemDtl tr').each(function() {
			 var code=$(this).find("input[class='itemCode']").val();
			
			 var itemName=$(this).find("input[class='itemName']").val();
			 var itemQty=$(this).find("input[class='itemQty']").val();
			 var itemAmt=$(this).find("input[class='itemAmt']").val();
			 var itemCode=$(this).find("input[class='itemCode']").val();
			 var itemDiscAmt=$(this).find("input[class='itemDiscAmt']").val();
			 if(checkKOT>1)
				 {
			    	code=code+"_"+itemName+"_"+itemAmt;
			    	var data=new Array();
			    	 data[0]=itemName;
			    	data[1]=itemQty;
			    	data[2]=itemAmt;
			    	data[3]=itemCode;
			    	data[4]=itemDiscAmt;
			    	kotListForModifier[i]=data; 
			    	
			    	
		    	arrKOTItemDtlList[i]=code;
		    	
		    		
				 
					i++;
				 }	checkKOT++;
				 
				 });
	} 
	

	function funGetSelectedRowIndex(obj)
	{
		 var index = obj.parentNode.parentNode.rowIndex;
		 var table = document.getElementById("tblBillItemDtl");
		 if((selectedRowIndex>0) && (index!=selectedRowIndex))
		 {
			
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#C0E4FF';
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
	         
			
		 }
		 else
		 {
			 selectedRowIndex=index;
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#ffd966';
			 row.hilite = true;
		 }
		 
		  var iteCode=table.rows[selectedRowIndex].cells[3].innerHTML;
		  
		  var codeArr = iteCode.split('value=');
		  var code=codeArr[1].split('onclick=');
		  var itemCode=code[0].substring(1, (code[0].length-2));
			funFillTopModifierButtonList(itemCode);
			funLoadModifiers(itemCode);
	}
	
	function funChangeQty(obj)
	{
		 var index = obj.parentNode.parentNode.rowIndex;
		 var table = document.getElementById("tblBillItemDtl");
		 if((selectedRowIndex>0) && (index!=selectedRowIndex))
		 {
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#C0E4FF';
				 selectedRowIndex=index;
				 row = table.rows[selectedRowIndex];
				 row.style.backgroundColor='#ffd966';
				 row.hilite = true;
		 }
		 else
		 {
			 selectedRowIndex=index;
			 row = table.rows[selectedRowIndex];
			 row.style.backgroundColor='#ffd966';
			 row.hilite = true;
		 }
		 
		  var iteCode=table.rows[selectedRowIndex].cells[3].innerHTML;
		  var rate=(tblBillItemDtl.rows[selectedRowIndex].cells[2].children[0].value)/(tblBillItemDtl.rows[selectedRowIndex].cells[1].children[0].value);
		  var codeArr = iteCode.split('value=');
		  var code=codeArr[1].split('onclick=');
		  var itemCode=code[0].substring(1, (code[0].length-2));
		  
		  var qty=prompt("Enter Quantity", obj.value);
		  if(qty!=null)
		  {
		  obj.value=qty;
		
		  var itemAmt=qty*rate;
		  document.getElementById("amount."+(selectedRowIndex)).value=itemAmt;
		  funFillKOTList();
		  funCalculateTax();
		  }
	}

	function funLoadModifiers(itemCode)
	{		
		var searchurl=getContextPath()+"/funLoadModifiers.html?itemCode="+itemCode;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {	
			        	funAddModifiersData(response.Modifiers,itemCode);
			        
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
	
	function funAddModifiersData(Modifiers,itemCode)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		var rowCount = tblMenuItemDtl.rows.length;	
		
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		var colmn=insertTR.insertCell(insertCol);
		
			colmn.innerHTML = "<td><input type=\"button\" id=\"M99\" value='Free Flow Modofier' style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funFreeFlowModifierClicked(this,"+index+",'"+itemCode+"')\" /></td>";
			insertCol++;
		var index=0;
		var item=itemCode;
		itemPriceDtlList=new Array();
		$.each(Modifiers, function(i, obj) 
		{					
			var name=obj.strModifierName.split(">");
				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				{
					index=rowCount*4+insertCol;
					var col=insertTR.insertCell(insertCol);
					
				//	col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value="+obj.strModifierName+"    style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value='-->"+name[1]+"' style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funModifierClicked(this,"+index+",'"+itemCode+"')\" /></td>";
					
					
					insertCol++;
				}
				else
				{		rowCount++;	 		
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
					index=rowCount*4+insertCol;				
					var col=insertTR.insertCell(insertCol);
					//col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value="+obj.strModifierName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					
					col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value='-->"+name[1]+"'    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funModifierClicked(this,"+index+",'"+itemCode+"')\" /></td>";
					
					insertCol++;
				}							
				itemPriceDtlList[index]=obj;
		});
	}
	function funModifierClicked(objMenuItemButton,objIndex,itemCode)
	{							

		var objMenuItemPricingDtl=itemPriceDtlList[objIndex];
		
	
			funAddModifierTableBillItemDtl(objMenuItemPricingDtl,itemCode);	
		
		funFillKOTList();
		funCalculateTax();
	}
	
	function funFreeFlowModifierClicked(objMenuItemButton,objIndex,itemCode)
	{
		var itmName=prompt("Enter Name", "");
		if(itmName.trim().length>0)
			{
			itmName="-->"+itmName;
			var amt=parseFloat(prompt("Enter Amount", 0));
			var jObj={
					strModifierName:itmName,
					dblRate:amt,
					strModifierCode:"M99",
					};
			funAddModifierTableBillItemDtl(jObj,itemCode);
			funFillKOTList();
			funCalculateTax();
			}
	}
	function funFillTopModifierButtonList(itemCode)
	{		
		
		var searchurl=getContextPath()+"/funFillTopModifierButtonList.html?itemCode="+itemCode;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {	
			        	funAddTopModifierButtonData(response.topButtonModifier);
			        	
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
	
	function funAddTopModifierButtonData(topButtonList)
	{
		var $rows = $('#tblTopButtonDtl').empty();
		var tblTopButtonDtl=document.getElementById('tblTopButtonDtl');
		var insertCol=0;
		var insertTR=tblTopButtonDtl.insertRow();
		
		$.each(topButtonList, function(i, obj) 
		{		
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierGroupCode+" value="+obj.strModifierGroupShortName+"    style=\"width: 90px;height: 30px; white-space: normal;\"  onclick=\"funTopButtonClicked(this)\" /></td>";
					insertCol++; 
		});
	}
	
	
	function funTxtTableSearchClicked(code)
	{	
		$("#txtWaiterName").val("");	
		$("#txtWaiterNo").val("");
		$("#txtPaxNo").val(0);
		var jsonArrForTableDtl=${command.jsonArrForTableDtl};
		$.each(jsonArrForTableDtl, function(i, obj) 
		{
			if(obj.strTableNo==code)
			{									
				$("#txtWaiterName").val(obj.strWShortName);	
				$("#txtWaiterNo").val(obj.strWaiterNo);	
					
				$("#txtTableNo").val(obj.strTableNo);
				$("#txtTableName").val(obj.strTableName);			
				$("#txtPaxNo").val(obj.intPaxNo);		
				
				funChekReservation(obj.strTableNo);
				funChekTableDtl(obj.strTableNo);
			}
		});
	}
	
	//function on item cliked
	function funTableClicked(objTableButton,objIndex)
	{		
		
		var jsonArrForTableDtl=${command.jsonArrForTableDtl};		
		var objselectedTableDtl=jsonArrForTableDtl[objIndex];									
		$("#txtTableNo").val(objselectedTableDtl.strTableNo);	
		$("#txtTableName").val(objselectedTableDtl.strTableName);	
		$("#txtPaxNo").val(objselectedTableDtl.intPaxNo);
		globalTableNo=objselectedTableDtl.strTableNo;
		funChekReservation(objselectedTableDtl.strTableNo);
		funChekTableDtl(objselectedTableDtl.strTableNo);
		funFillMapWithHappyHourItems();
	}
	
	function funCheckHomeDelStatus()
	{
		homeDeliveryForTax = "Y";
		if (arrListHomeDelDetails.length == 0)
        {
            arrListHomeDelDetails[0]=gCustomerCode;
            arrListHomeDelDetails[1]=gCustomerName;
            arrListHomeDelDetails[2]="HomeDelivery";
            arrListHomeDelDetails[3]="";
            arrListHomeDelDetails[4]="";
            document.getElementById("Home Delivery").style.backgroundColor = "lightblue";
            
        }else{
        	homeDeliveryForTax = "N";
        	arrListHomeDelDetails= new Array();	
        	document.getElementById("Home Delivery").style.backgroundColor = "";
        }
		if(gTakeAway=="Yes")
		{
		    gTakeAway="No";
		}
		if(gCustAddressSelectionForBill=="Y")
		{
		 	window.open("frmHomeDeliveryAddress.html?strMobNo="+gMobileNo+"","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		} 
	}
	
	function funCall()
	{
		funSetCustomerDataForHD(gCustomerCode);
	}
	
	function funCheckKOTSave()
	{
// 		var kotNo=$("#txtKOTNo").val();
// 		var flg = false;
// 		var searchurl=getContextPath()+"/funCheckKOTSave.html?strKOTNo="+kotNo;
// 		 $.ajax({
// 			        type: "GET",
// 			        url: searchurl,
// 			        dataType: "json",
// 			        async: false,
// 			        success: function(response)
// 			        {
			        	
// 			        	if(response.flag)
// 			        		return true;
// 			        	if(!response.savedKOT)
// 			        		return false;
// 			        	else
// 			        		return true;
			        	
// 					},
// 					error: function(jqXHR, exception) {
// 			            if (jqXHR.status === 0) {
// 			                alert('Not connect.n Verify Network.');
// 			            } else if (jqXHR.status == 404) {
// 			                alert('Requested page not found. [404]');
// 			            } else if (jqXHR.status == 500) {
// 			                alert('Internal Server Error [500].');
// 			            } else if (exception === 'parsererror') {
// 			                alert('Requested JSON parse failed.');
// 			            } else if (exception === 'timeout') {
// 			                alert('Time out error.');
// 			            } else if (exception === 'abort') {
// 			                alert('Ajax request aborted.');
// 			            } else {
// 			                alert('Uncaught Error.n' + jqXHR.responseText);
// 			            }		            
// 			        }
// 		      });

		return true;
	}
	function funFooterButtonClicked(objFooterButton)
	{
		switch(objFooterButton.id)
		{
		case "Done":
			funValidate();
			break;
			
		case "Home Delivery":
			funHomeDeliveryBtnClicked();
			break;
			
		case "Customer":
			funCustomerBtnClicked();
			break;
			
		case "Delivery Boy":
			funDeliveryBoyClicked();
			break;
			
		case "NC KOT":
			funNCKOTClicked();
			break;
			
		case "Check KOT":
			funCheckKOTClicked();
			break;
			
		case "Make Bill":
			funMakeBillClicked();
			break;
		
		case "Home":
			funHomeBtnclicked();
			break;
			
			
		case "PLU" :
			funPLUItemData();
			break;
		}
	}
	
	
	function funPLUItemData() {
		document.getElementById("divItemDtl").style.display='none';
		 document.getElementById("divPLU").style.display='block';
		var table = document.getElementById("tblItems");
		var cntIndex = 0;
		var jsonArrForMenuItemPricing=${command.jsonArrForMenuItemPricing};	
		var index=0;
 	    itemPriceDtlList=new Array();
		$.each(jsonArrForMenuItemPricing, function(i, obj) 
		{
			rowCount = table.rows.length;
			row = table.insertRow(rowCount);
			var itemName=obj.strItemName.replace(/&#x00A;/g," ");
		
			row.insertCell(0).innerHTML = "<input type=\"text\"  id='"
					+ itemName
					+ "' name='"
					+ itemName
					+ "' style=\"width: 490px; height: 30px; \" class=\"transForm_button \" value='"
					+ itemName + "' onclick=\"funMenuItemClicked(this,"+index+")\"/>";
			itemPriceDtlList[index]=obj;	
			index++;
		});		

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
	
	function funMakeBillClicked()
	{
		var tblBillItemDtl=document.getElementById('tblOldKOTItemDtl');
		var rowCount = tblBillItemDtl.rows.length;	
		if (globalTableNo.trim().length == 0)
        {
           alert( "Please Select Table");
            return;
        }
		if ($("#txtWaiterName").val().trim().length==0)
        {
			alert("Please select Waiter");
            return;
        }
		if (rowCount == 1)
        {
           alert("Please select Items");
           return;
        }
		else
		{
			if (funCheckKOTSave())
        	{
//             	if (gEnableBillSeries=="Y")
//             	{
              //  clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
               // ob.fun_CkeckKot_TextFile(globalTableNo, txtWaiterNo.getText().trim());
               
              $("#txtTakeAway").val(gTakeAway);
               funMakeBillBtnKOT(ncKot,gTakeAway,globalDebitCardNo,cmsMemCode,cmsMemName,reasonCode,homeDeliveryForTax,arrListHomeDelDetails);
//             	}
        	}
        	else
        	{
            	alert("Please Save KOt First");
            	return;
        	}	
		}
    }
	
	function funCheckKOTClicked()
	{
		if (globalTableNo.trim().length == 0)
        {
           alert( "Please Select Table");
            return;
        } if (!funCheckKOTSave())
        {
            if (gPrintType=="Text File")
            {
              //  clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
               // ob.fun_CkeckKot_TextFile(globalTableNo, txtWaiterNo.getText().trim());
            }
        }
        else
        {
            alert("Please Save KOt First");
            return;
        }	
    }
	
	
	function funNCKOTClicked()
	{
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		var rowCount = tblMenuItemDtl.rows.length;	
		if (rowCount > 1)
        {
			if(!flgCheckNCKOTButtonColor)
			{
			flgCheckNCKOTButtonColor=true;
		    document.getElementById("NC KOT").style.backgroundColor = "lightblue";
        }else{
        	flgCheckNCKOTButtonColor=false;
		    document.getElementById("NC KOT").style.backgroundColor = "";
        }
        }
		else
		{	
			alert("KOT Not Present.");
            return;
        }
		
	}
	
	
	function funHomeDeliveryBtnClicked()
	{
		if (globalTableNo.trim().length == 0)
        {
           alert("Please Select Table");
            return;
        }
		else{	
		if (gCustomerCode.trim().length == 0)
        {

           alert("Please Enter Customer Mobile No!");
            return;
        }
		else
        {
			funCheckHomeDelStatus();
        }
		}
	}
	
	function funDeliveryBoyClicked()
	{
		if(arrListHomeDelDetails.length > 0)
			funHelp1('POSDeliveryBoyMaster');
			
		else
        {
           alert("Please Select Customer");
           return;
        }
	}
	
	function funCustomerBtnClicked()
	{
		if(gCMSIntegrationYN=="Y")
			{
			funChekCMSCustomerDtl();
			}
		else
        {
            funNewCustomerButtonPressed();
        }
	}
	
	function funNewCustomerButtonPressed()
	{
		if (gCRMInterface=="SQY")
        {
			 var strMobNo = prompt("Enter Mobile number", "");
			 if(strMobNo.trim().length>0)
				 funCallWebService(strMobNo);
        }
		else if (gCRMInterface=="PMAM")
        {
			 var strMobNo = prompt("Enter Mobile number", "");
			 if(strMobNo.trim().length>0)
				 funSetCustMobileNo(strMobNo);
			 
       	}
		else
        {
			 var strMobNo = prompt("Enter Mobile number", "");
			 if(strMobNo.trim().length>0)
			 {
				 funSetCustMobileNo(strMobNo);
			 }
       	}
	}

	function  funSetCustMobileNo(strMobNo)
	{
		gMobileNo=strMobNo;
		var totalBillAmount = 0.00;
		  if ($("#txtTotal").val().trim().length > 0)
          {
			  totalBillAmount = parseFloat($("#txtTotal").val());
          }
		 if (strMobNo.trim().length == 0)
         {
			 funHelp1('POSCustomerMaster');
         }
		 else
			 funCheckCustomer(strMobNo);
	}
	function funCheckCustomer(strMobNo)
	{		
		var searchurl=getContextPath()+"/funCheckCustomer.html?strMobNo="+strMobNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	 if (response.flag)
			             {
			        		 gCustomerCode=response.strCustomerCode;
			        		 $("#Customer").val(response.strCustomerName);
			        		 $("#txtCustomerName").val(response.strCustomerName);
			        		 $("#txtCustomerCode").val(gCustomerCode);
			             }	
			        	 else
			        		 funCustomerMaster(strMobNo);
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
	function funCallWebService(strMobNo)
	{		
		var searchurl=getContextPath()+"/funCallWebService.html?strMobNo="+strMobNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	 if (null != response.code)
			             {
			                 if (parseInt(response.code) == 323)
			                 {
			                     alert("Discount Request Expired! Please ask the customer to regenerate discount request!");
			                     return 0;
			                 }
			             }	
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
	function funChekCMSCustomerDtl()
	{		
		var searchurl=getContextPath()+"/funChekCMSCustomerDtl.html?strTableNo="+globalTableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        	{
			        	if(response.dblAmount<1)
			        		funGetCMSMemberCode();
			        	else
			        	{
			        		cmsMemCode=response.strCustomerCode;
			        		cmsMemName=response.strCustomerName;
			        	}
			        	
			        	}	
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
	
	function funGetCMSMemberCode()
	{
		 var strCustomerCode = prompt("Enter Member Code", "");
		 if(strCustomerCode.trim().length>0)
			 {
			 var searchurl=getContextPath()+"/funCheckMemeberBalance.html?strCustomerCode="+strCustomerCode;
			 $.ajax({
				        type: "GET",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {
				        	if(response.flag)
				        	{
				        		 if (response.memberInfo.split("#")[4].trim().equals("Y"))
				                 {
				                     alert("Member is blocked");
				                     return;
				                 }
				                 else
				                 {
				                     cmsMemCode = response.memberInfo.split("#")[0];
				                     cmsMemName = response.memberInfo.split("#")[1];
				                     $("#Customer").val(cmsMemName);
				                     var creditLimit = parseFloat(response.memberInfo.split("#")[2]);
									var totalAmt;
				                     if ($("#txtTotal").val().trim().length > 0)
				                     {
				                         totalAmt = $("#txtTotal").val();
				                     }
				                     if (creditLimit < totalAmt)
				                     {
				                      alert("Credit Limit is " + creditLimit);
				                     }
				                 }
				        	
				        	}	
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
			
	}
	function funPaxClicked(objPaxButton)
	{
		if($("#txtWaiterName").val().trim().length==0)
		{
			alert("Please select waiter.")
			return;
		}
		var selctedPaxNo=objPaxButton.id;
								
		$("#txtPaxNo").val(selctedPaxNo);			
			
		funShowMenuHead();
		document.all[ 'tblPaxNo' ].style.display = 'none';		
	}
	
	
	function funTxtWaiterClicked(code)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
			
		var jsonArrForWaiterDtl=${command.jsonArrForWaiterDtl};	
		
		
		$.each(jsonArrForWaiterDtl, function(i, obj) 
		{									
			if(obj.strWaiterNo==code)
			{									
				$("#txtWaiterName").val(obj.strWShortName);	
				$("#txtWaiterNo").val(obj.strWaiterNo);	
			}
			if(gSkipPax=="Y")
				funShowMenuHead();
			else
			{
				if(parseInt($("#txtPaxNo").val().trim()) != 0)
   				{
					funShowMenuHead();
   			 	}
				else
				{
					//Enable Pax No buttons
				}
				
			}
		});
	}
	function funWaiterClicked(objWaiterButton)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
	
		var selctedWaiterCode=objWaiterButton.id;
		
		var jsonArrForWaiterDtl=${command.jsonArrForWaiterDtl};	
		
		
		$.each(jsonArrForWaiterDtl, function(i, obj) 
		{									
			if(obj.strWaiterNo==selctedWaiterCode)
			{									
				$("#txtWaiterName").val(obj.strWShortName);	
				$("#txtWaiterNo").val(obj.strWaiterNo);			
			}
		});
			if(gSkipPax=="Y")
			{
				funShowMenuHead();
			}
			else
			{
				if(parseInt($("#txtPaxNo").val().trim()) != 0)
   				{
					funShowMenuHead();
   				}
				else
				{
					//Enable Pax No buttons
				}
				
			}
		
	}
	
	function funAddWaiterDtl()
	{
		
		var $rows = $('#tblMenuItemDtl').empty();
		var $rows = $('#tblMenuHeadDtl').empty();
		var $rows = $('#tblTopButtonDtl').empty();
		
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
				
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		
		var jsonArrForWaiterDtl=${command.jsonArrForWaiterDtl};	
		
		$.each(jsonArrForWaiterDtl, function(i, obj) 
		{	
			if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
			{
				var col=insertTR.insertCell(insertCol);
				col.innerHTML = "<td><input type=\"button\" id="+obj.strWaiterNo+" value='"+obj.strWShortName+"'    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funWaiterClicked(this)\" /></td>";
				insertCol++;
			}
			else
			{					
				insertTR=tblMenuItemDtl.insertRow();									
				insertCol=0;
				var col=insertTR.insertCell(insertCol);
				col.innerHTML = "<td><input type=\"button\" id="+obj.strWaiterNo+" value='"+obj.strWShortName+"'    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funWaiterClicked(this)\" /></td>";
				insertCol++;
			}							
			  
		});
	}
	
	//function on menu Head cliked
	function funMenuHeadButtonClicked(objMenuHeadButton)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		var selctedMenuHeadCode=objMenuHeadButton.id;
		flagPopular="menuhead";
		funFillTopButtonList(selctedMenuHeadCode);
		var jsonArrForMenuItemPricing=${command.jsonArrForMenuItemPricing};	
		var rowCount = tblMenuItemDtl.rows.length;	
		itemPriceDtlList=new Array();
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		var index=0;
		$.each(jsonArrForMenuItemPricing, function(i, obj) 
		{									
			if(obj.strMenuCode==selctedMenuHeadCode)
			{									
				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				{
					index=rowCount*4+insertCol;
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					insertCol++;
				}
				else
				{		
					rowCount++;
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
					index=rowCount*4+insertCol;				
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					insertCol++;
				}	
				itemPriceDtlList[index]=obj;
			}
		});
	}
	

	function funFillMapWithHappyHourItems()
	{		
		var searchurl=getContextPath()+"/funFillMapWithHappyHourItems.html";
		$.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	for(var i=0;i<response.ItemPriceDtl.length;i++)
		        		{
// 			        		hmHappyHourItems.put(response.ItemCode[i],response.ItemPriceDtl[i]);
			        		hmHappyHourItems[response.ItemCode[i]] = response.ItemPriceDtl[i];
		        		}
			        	gDebitCardPayment=response.gDebitCardPayment;
			        	currentDate=response.CurrentDate;
			        	currentTime=response.CurrentTime;
			        	dayForPricing=response.DayForPricing;
			        	
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
	
	function funChekReservation(tableNo)
	{		
		var searchurl=getContextPath()+"/funChekReservation.html?strTableNo="+tableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        		$("#Customer").val(response.strCustomerName);
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
	
	
	function funChekTableDtl(tableNo)
	{		
		var $rows =$('#tblOldKOTItemDtl').empty();
			
		var searchurl=getContextPath()+"/funChekCustomerDtl.html?strTableNo="+tableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        	{
			        		if(response.strCustomerCode!=null)
			        		{
			        			gCustomerCode=response.strCustomerCode;
			        			gCustomerName=response.strCustomerName;
			        			$("#Customer").val(response.strCustomerName);
			        		}
			        		if(response.strHomeDelivery=="Yes")
		        			{
			        			 homeDeliveryForTax = "Y";
			        			 arrListHomeDelDetails[0]=response.strCustomerCode;
			        			 arrListHomeDelDetails[1]=response.strCustomerName;
			        			 arrListHomeDelDetails[2]="HomeDelivery";
			        			 arrListHomeDelDetails[3]="";
			        			 arrListHomeDelDetails[4]="";
					        			 
		        			}
			        		else
			        			homeDeliveryForTax = "N";
			        		
			        		funChekCardDtl(tableNo);
			        		funFillOldKOTItems(tableNo);
			        		if(gMultiWaiterSelOnMakeKOT=="Y")
		        			{
			        			$("#txtPaxNo").val(response.intPaxNo);
			        			if(gSelectWaiterFromCardSwipe=="Y" && response.posConfigSelectWaiterFromCardSwipe=="true")
			        			{
				        			 var card = prompt("please Swipe The Card", "");
				        			 funCheckDebitCardString(card);
				        		}
			        			else
			        			{
			        				if(response.waiterNo=="all")
				        			{
			        					funAddWaiterDtl();
				        			}
			        				else
			        				{
			        					var jsonArrForWaiterDtl=${command.jsonArrForWaiterDtl};	
			        					$.each(jsonArrForWaiterDtl, function(i, obj) 
			        					{									
			        						if(obj.strWaiterNo==response.strWaiterNo)
			        						{
			        							$("#txtWaiterName").val(obj.strWShortName);	
			        							$("#txtWaiterNo").val(obj.strWaiterNo);	
			        						}
			        					});	
			        					}
			        			}
		        			}

			        		else
        					{
	        					var jsonArrForWaiterDtl=${command.jsonArrForWaiterDtl};	
	        					$.each(jsonArrForWaiterDtl, function(i, obj) 
	        					{									
	        						if(obj.strWaiterNo==response.strWaiterNo)
	        						{
	        							$("#txtWaiterName").val(obj.strWShortName);	
	        							$("#txtWaiterNo").val(obj.strWaiterNo);	
	        						}
	        					});	
	        					funShowMenuHead();
	        					funCheckHomeDelivery(tableNo);
        					}
			        	}			        		
			        	else if(gSkipWaiter=="Y" && gSkipPax=="Y")
			        	{
			        		funShowMenuHead();
			        	}
			        	else if(gSkipWaiter=="Y")
			        	{
			        		document.all["tblPaxNo"].style.display = 'block';
			        	}
			        	else
			        	{
			        		document.all[ 'tblPaxNo' ].style.display = 'block';
			        		funAddWaiterDtl();
			        	}
			        	$("#lblAreaName").text(response.AreaName);
			         	if(gCMSIntegrationYN=="Y")
			        	{
			        		cmsMemCode=response.CustomerCode;
			        	 	cmsMemName=response.CustomerName;			        	 
			        	 	$("#Customer").val(response.CustomerName);
			        	}
			         	else 
			        		$("#Customer").val("New Customer");
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
	
	function funCheckHomeDelivery(tableNo)
	{		
		if(arrKOTItemDtlList.length==0)
			funRemoveTableRows("tblBillItemDtl");
		var searchurl=getContextPath()+"/funCheckHomeDelivery.html?strTableNo="+tableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        		{
			        		$("#Customer").val(response.strCustomerName);
			        		 homeDeliveryForTax = "Y";
		        			 arrListHomeDelDetails[0]=response.strCustomerCode;
		        			 arrListHomeDelDetails[1]=response.strCustomerName;
		        			 arrListHomeDelDetails[2]=response.strBuldingCode;
		        			 arrListHomeDelDetails[3]="HomeDelivery";
		        			 arrListHomeDelDetails[4]=response.strDelBoyCode;
		        			 arrListHomeDelDetails[5]=response.strDPName;
			        		
			        		}
			        	else
			        		$("#Customer").val("New Customer");
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
	

	function funChekCardDtl(tableNo)
	{		
		var searchurl=getContextPath()+"/funChekCardDtl.html?strTableNo="+tableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        		{
			        		$("#txtCardBalance").text(response.cardBalnce);
			        		$("#txtDeditCardBalance").text(response.cardBalnce);
			        		if(response.balAmt > response.kotAmt)
			        			{
			        				$("#txtCardBalance").style.backgroundColor = "#ff0d0d";
			        			}
			        		else
			        			$("#txtCardBalance").style.backgroundColor = "#ff0d0d";
			        		}
			        	
			        	
			        	
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
	
	function funFillOldKOTItems(tableNo)
	{		
		if(arrKOTItemDtlList.length==0)
			funRemoveTableRows("tblBillItemDtl");
		var searchurl=getContextPath()+"/funFillOldKOTItems.html?strTableNo="+tableNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.flag)
			        		{
			        			$.each(response.OldKOTTimeDtl, function(j, item) 
			        			{
			        				funFillOldKOTTimeDtl(item);
			        			
			        	 			$.each(response.OldKOTItems, function(i, obj) 
			        				{									
			        					if(item.kotNo==obj.strKOTNo)
			        						funFillOldKOTItemDtl(obj);
			        		
			        				});
			        			});
			        		$("#txtTotal").val(response.Total);
			        		}			        	
			        	
			        	
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
	

	function funCheckDebitCardString(debitCardNo)
	{		
		var searchurl=getContextPath()+"/funCheckDebitCardString.html?debitCardNo="+debitCardNo;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	if(response.waiterNo.trim().length>0)
			        		{
			        		var waiterInfo=response.waiterNo.split("#");
			        		$("#txtWaiterName").val(waiterInfo[1]);	
							$("#txtWaiterNo").val(waiterInfo[0]);	
			        		
							document.all["divPaxNo"].style.display = 'block';
			        		
			        		if(gSkipPax!="Y")
			        			{
			        				if(parseInt($("#txtPaxNo").val().trim()) != 0)
			        				 {
			        					funShowMenuHead();
			                         }
			        				else
			        					$("#txtPaxNo").focus();
			        			}
			        		}
			        	
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
	* Get and Set data from help file and load data Based on Selection Passing Value(Customer Code)
	**/
	function funSetCustomerDataForHD(code)
	{
		code=code.trim();
		var searchurl=getContextPath()+"/loadPOSCustomerMasterData.html?POSCustomerCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	$("#Customer").val(response.strCustomerName);
			        	gCustomerCode=response.strCustomerCode;
			        	$("#txtCustomerCode").val(code);
			        	$("#txtCustomerName").val(response.strCustomerName);
				        	funSetHomeDeliveryData(response.strCustomerCode,response.strCustomerName,response.strBuldingCode,"","");
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

	function funSetDeliveryBoy(code)
	{
		code=code.trim();
		var searchurl=getContextPath()+"/loadPOSDeliveryBoyMasterData.html?dpCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	arrListHomeDelDetails[4]=code;//4 del person code
			            arrListHomeDelDetails[5]=response.strDPName;//5 del person name
			            gDeliveryBoyCode=code;
			        	document.all["lblDpName"].style.display = 'block';
 			        	$("#dpName").text(response.strDPName);
			        	$("#hidDeliveryBoyCode").val(response.strDPCode);
			        	$("#hidDeliveryBoyName").val(response.strDPName);
			            
			        	
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

	function funSetHomeDeliveryData(custCode, custName, buildingCode, delPersonCode, delPersonName)
    {
        arrListHomeDelDetails.length=0;
        arrListHomeDelDetails[0]=custCode;//0 cust code
        arrListHomeDelDetails[1]=custName;//1 cust name
        arrListHomeDelDetails[2]=buildingCode;//2 building code
        arrListHomeDelDetails[3]="HomeDelivery";//3 home delivery
        arrListHomeDelDetails[4]=delPersonCode;//4 del person code
        arrListHomeDelDetails[5]=delPersonName;//5 del person name
    }
// 	function funSetData(code)
// 	{

// 		switch(fieldName)
// 		{
// 		case "POSTableMaster":
// 			funTxtTableSearchClicked(code);
// 			break;
// 		case "POSWaiterMaster":
// 			funTxtWaiterClicked(code);
// 			break;
// 		case "POSCustomerMaster":
// 			funSetCustomerDataForHD(code);
// 			break;
// 		case "NewCustomer":
// 			funSetCustomerDataForHD(code);
// 			break;
// 		case "POSDeliveryBoyMaster":
// 			funSetDeliveryBoy(code);
// 			break;
// 		}
// 	}
	
	function funHelp1(transactionName)
	{
		fieldName=transactionName;
		window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
	}
	 function funCustomerMaster(strMobNo)
	{
		 fieldName="NewCustomer";
		 <%session.setAttribute("frmName", "frmPOSRestaurantBill");%>

		
		window.open("frmPOSCustomerMaster.html","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
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
			{%>	
				alert("Data Save successfully\n\n"+message);
			<%}
		}%>

// 		 $("form").submit(function(event){
// 			  funValidate();
// 			});
	});
	
	function funValidate()
	{
		 if (gDebitCardPayment=="Yes")
	        {
	            if (gCheckDebitCardBalanceOnTrans=="Y")
	            {
	                if (!flgCheckNCKOTButtonColor)
	                {
	                    if ($("#txtCardBalance").text().length==0)
	                    {
	                        var debitCardBalance = parseFloat($("#txtCardBalance").text());
	                        if (parseFloat($("#txtTotal").val()) > debitCardBalance)
	                        {
	                            alert("Insufficient Balance on Card!!!");
	                            return;
	                        }
	                    }
	                    else
	                    {
	                        alert("Please Swipe Card!!!");
	                        return;
	                    }
	                }
	            }
	        }
		 
		 if (gCMSIntegrationYN=="Y")
	        {
	            if (gCMSMemberCodeForKOTJPOS=="Y")
	            {
	                if (null != hmCMSMemberForTable)
	                {
	                    if (null == hmCMSMemberForTable.get(globalTableNo))
	                    {
	                        alert("Please Select Member!!!");
	                        return;
	                    }
	                }
	                else
	                {
	                    alert("Please Select Member!!!");
	                    return;
	                }
	            }
	        }
		 var tblBillItemDtl=document.getElementById('tblBillItemDtl');
			
			var rowCount = tblBillItemDtl.rows.length;
			
			if(rowCount>1)
				{
				
				  if (homeDeliveryForTax=="N")
		            {
		                arrListHomeDelDetails.length =0;
		            }
				 var total=$("#txtTotal").val();
// 				document.frmMakeKOT.action="saveKOT.html?ncKot="+ncKot+"&takeAway="+gTakeAway+"&globalDebitCardNo="+globalDebitCardNo+"&cmsMemCode="+cmsMemCode+"&cmsMemName="+cmsMemName+"&reasonCode="+reasonCode+"&homeDeliveryForTax="+homeDeliveryForTax+"&arrListHomeDelDetails="+arrListHomeDelDetails;
// 				document.frmMakeKOT.submit();

   			
        	if (flgCheckNCKOTButtonColor) //NC KOT
       		 {
        	    ncKot = "Y";
          	    globalDebitCardNo = "";
       		 }
				  funDoneBtnKOT(ncKot,gTakeAway,globalDebitCardNo,cmsMemCode,cmsMemName,reasonCode,homeDeliveryForTax,arrListHomeDelDetails,total);

				}
		 
	}
	
	
	
	 function funDoneBtnKOT(ncKot,gTakeAway,globalDebitCardNo,cmsMemCode,cmsMemName,reasonCode,homeDeliveryForTax,arrListHomeDelDetails,total)
     {
		 
		 
		 
		   var listItmeDtl=[];
			var tblBillItemDtl=document.getElementById('tblBillItemDtl');
			var rowCount = tblBillItemDtl.rows.length;
			for(var i=2;i<rowCount;i++)
			{
				var itemName=tblBillItemDtl.rows[i].cells[0].children[0].value;
				var itemQty=tblBillItemDtl.rows[i].cells[1].children[0].value;
				if(!(itemQty==" "))
				{
				var itemAmt=tblBillItemDtl.rows[i].cells[2].children[0].value;
				var itemCode=tblBillItemDtl.rows[i].cells[3].children[0].value;
				var itemDiscAmt=tblBillItemDtl.rows[i].cells[4].children[0].value;
		 		var groupcode=tblBillItemDtl.rows[i].cells[5].innerHTML;
		 		var subgroupcode=tblBillItemDtl.rows[i].cells[6].innerHTML;
		 		var subgroupName=tblBillItemDtl.rows[i].cells[7].innerHTML;
		 		var groupName=tblBillItemDtl.rows[i].cells[8].innerHTML;
				
		 		var gCode = groupcode.split('value=');
				var strGroupCode=gCode[1].substring(1, (gCode[1].length-2));
				
				var sgCode= subgroupcode.split('value=');
				var strSubGroupCode=sgCode[1].substring(1, (sgCode[1].length-2));
				
				var gName= groupName.split('value=');
				var strGroupName=gName[1].substring(1, (gName[1].length-2));
				
				var sgName= subgroupName.split('value=');
				var strSGName=sgName[1].substring(1, (sgName[1].length-2));
				hmGroupMap.set(strGroupCode, strGroupName); 
				hmSubGroupMap.set(strSubGroupCode, strSGName);
				hmItempMap.set(itemCode,itemName);
				
				var tableNo=tblBillItemDtl.rows[i].cells[9].innerHTML;
				var bckDtl= tableNo.split('value=');
				tableNo=bckDtl[1].substring(1, (bckDtl[1].length-2));
				
				var PaxNo=tblBillItemDtl.rows[i].cells[10].innerHTML;
				var bckDtl= PaxNo.split('value=');
				PaxNo=bckDtl[1].substring(1, (bckDtl[1].length-2));
				

				var kotNo=tblBillItemDtl.rows[i].cells[11].innerHTML;
				var bckDtl= kotNo.split('value=');
				kotNo=bckDtl[1].substring(1, (bckDtl[1].length-2));
				
				var WaiterNo=tblBillItemDtl.rows[i].cells[12].innerHTML;
				var bckDtl= WaiterNo.split('value=');
				WaiterNo=bckDtl[1].substring(1, (bckDtl[1].length-2));
				alert(document.getElementById('tblBillItemDtl').rows[i].cells[10].Text);
//		 		funFillSettleTable(itemName,itemQty,itemAmt,0.0,itemDiscAmt,strGroupCode,strSubGroupCode,itemCode);
					var singleObj = {}
				    singleObj['itemName'] =itemName;
				    singleObj['quantity'] =itemQty;
				    singleObj['amount'] = itemAmt;
				    singleObj['discountPer'] = 0.0;
				    singleObj['discountAmt'] =0.0;
				    singleObj['strSubGroupCode'] =strSubGroupCode;
				    singleObj['strGroupcode'] =strGroupCode;
				    singleObj['itemCode'] =itemCode;
				    singleObj['rate'] =itemAmt/itemQty;
				    
				    singleObj['tableNo'] =tableNo;
				    singleObj['PaxNo'] =PaxNo;
				    singleObj['kotNo'] =kotNo;
				    singleObj['WaiterNo'] =WaiterNo;
				    listItmeDtl.push(singleObj);
				
				
				subTotalonAmt=subTotalonAmt+parseFloat(itemAmt);
				discountonAmt=discountonAmt+parseFloat(0);//(itemDiscAmt);
				
 					}
			}
		 
		 
		 
		 
		 
			
				
		var custcode=$("#txtCustomerCode").val();
    	var custName=$("#txtCustomerName").val();	
		var searchurl=getContextPath()+"/saveKOT.html?ncKot="+ncKot+"&takeAway="+gTakeAway+"&globalDebitCardNo="+globalDebitCardNo+"&cmsMemCode="+cmsMemCode+"&cmsMemName="+cmsMemName+"&reasonCode="+reasonCode+"&homeDeliveryForTax="+homeDeliveryForTax+
				"&arrListHomeDelDetails="+arrListHomeDelDetails+"&total="+total+"&custcode="+custcode+"&custName="+custName;
		$.ajax({
			 type: "POST",
		        url: searchurl,
		        data : JSON.stringify(listItmeDtl),
		        contentType: 'application/json',
		        async: false,
	        success: function (response){
	        	if(response=="true"){
	              alert("KOT Save Successfully");
	        	  window.location ="frmPOSRestaurantBill.html";
	        	}else{
	        		
	        		 alert("KOT Not Save ");
	        	}
	      
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
		while(rowCount>1)
		{
			table.deleteRow(1);
			rowCount--;
		}
	}
function funShowMenuHead()
{
		var jsonArrForMenuHeads=${command.jsonArrForMenuHeads};	
	
		$.each(jsonArrForMenuHeads, function(i, obj) 
		{									
			
			funAddMenuHeadData(obj.strMenuCode,obj.strMenuName);
		});
		funLoadPopularItems();
	}

	function funAddMenuHeadData(strMenuCode,strMenuName)
	{
		var table = document.getElementById("tblMenuHeadDtl");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		if(rowCount==0){
			row.insertCell(0).innerHTML = "<td><input type=\"button\" id='PopularItem' value='POPULAR ITEM'  style=\"width: 100px;height: 40px; white-space: normal;\"  onclick=\"funLoadPopularItems()\" /></td>";
			rowCount++;
			 row = table.insertRow(rowCount);
		}
			row.insertCell(0).innerHTML = "<td><input type=\"button\" id="+strMenuCode+" value="+strMenuName+"    style=\"width: 100px;height: 40px; white-space: normal;\"  onclick=\"funMenuHeadButtonClicked(this)\" /></td>";
	}

	
	function funLoadPopularItems()
	{		
		var searchurl=getContextPath()+"/funLoadPopularItems.html";
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {	
			        	funAddPopularItemsData(response.PopularItems);
			        	if(gMenuItemSortingOn!="NA")
			        		{
			        		flagPopular="Popular";
			        		funFillTopButtonList(flagPopular);
			        		}
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
	
	function funAddPopularItemsData(popularItems)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		var rowCount = tblMenuItemDtl.rows.length;	
		itemPriceDtlList=new Array();
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		var index=0;
		$.each(popularItems, function(i, obj) 
		{									
				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				{
					index=rowCount*4+insertCol;
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					
					insertCol++;
				}
				else
				{		rowCount++;	 		
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
					index=rowCount*4+insertCol;				
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					
					insertCol++;
				}							
				itemPriceDtlList[index]=obj;
			  
		});
	}
	
	function funMenuItemClicked(objMenuItemButton,objIndex)
	{				
		
		$("#txtItemSearch").val("");

		var objMenuItemPricingDtl=itemPriceDtlList[objIndex];
		
		var   price = funGetFinalPrice(objMenuItemPricingDtl);
		
		var isOrdered=funIsAlreadyOrderedItem(objMenuItemPricingDtl);
		var qty=prompt("Enter Quantity", 1);
		
		  if(qty!=null)
		  {
		if(isOrdered)
		{
			funUpdateTableBillItemDtlFor(objMenuItemPricingDtl,qty);	
		}
		else
		{
			var tblBillItemDtl=document.getElementById('tblBillItemDtl');
			
			var rowCount = tblBillItemDtl.rows.length;
			if(rowCount==1)
				funGenerateKOTNo();
			funFillTableBillItemDtl(objMenuItemPricingDtl,price,qty);	
		} 
		
		
		funFillKOTList();
		funCalculateTax();
	}
	}
	
	function funCalculateTax()
	{		
		var total=parseFloat($("#txtTotal").val());
		var searchurl=getContextPath()+"/funCalculateTax.html?arrKOTItemDtlList="+arrKOTItemDtlList;
		 $.ajax({
			        type: "POST",
			        url: searchurl,
			        dataType: "json",
			        async:false,
			        success: function(response)
			        {	
			        	
			        	$("#txtTotal").val(total+parseFloat(response));
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
	
	
	function funGenerateKOTNo()
	{		
		
		var searchurl=getContextPath()+"/funGenerateKOTNo.html";
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        async:false,
			        success: function(response)
			        {	
			        	$("#txtKOTNo").val(response.strKOTNo);
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
	
	
	function CalculateDateDiff(fromDate,toDate) {
		
		var frmDate= fromDate.split('-');
	    var fDate = new Date(frmDate[0],frmDate[1],frmDate[2]);
	    
	    var tDate= toDate.split('-');
	    var t1Date = new Date(tDate[0],tDate[1],tDate[2]);

    	var dateDiff=t1Date-fDate;
  		
        	 return dateDiff;
        
	}
	
	function  funGetFinalPrice( ob)
    {
        var Price = 0.00;
        var fromTime = ob.tmeTimeFrom;
        var toTime = ob.tmeTimeTo;
        var fromAMPM = ob.strAMPMFrom;
        var toAMPM = ob.strAMPMTo;
        var hourlyPricing = ob.strHourlyPricing;
	var strItemCode=ob.strItemCode;
        if (hmHappyHourItems.has(strItemCode))
        {
            var obHappyHourItem = hmHappyHourItems.get(ob.strItemCode);
            fromTime = obHappyHourItem.tmeTimeFrom;
            toTime = obHappyHourItem.tmeTimeTo;
            fromAMPM = obHappyHourItem.strAMPMFrom;
            toAMPM = obHappyHourItem.strAMPMTo;

            var spFromTime = fromTime.split(":");
            var spToTime = toTime.split(":");
            var fromHour = parseInt(spFromTime[0]);
            var fromMin = parseInt(spFromTime[1]);
            if (fromAMPM=="PM")
            {
                fromHour += 12;
            }
            var toHour = parseInt(spToTime[0]);
            var toMin = parseInt(spToTime[1]);
            if (toAMPM=="PM")
            {
                toHour += 12;
            }
			var spCurrTime = currentTime.split(" ");
            var spCurrentTime = spCurrTime[0].split(":");

            var currHour = parseInt(spCurrentTime[0]);
            var currMin = parseInt(spCurrentTime[1]);
            var currDate = currentDate;
            currDate = currDate + " " + currHour + ":" + currMin + ":00";

            //2014-09-09 23:35:00
            var fromDate = currentDate;
            var toDate = currentDate();
            fromDate = fromDate + " " + fromHour + ":" + fromMin + ":00";
            toDate = toDate + " " + toHour + ":" + toMin + ":00";

            var diff1 = CalculateDateDiff(fromDate, currDate);
            var diff2 = CalculateDateDiff(currDate, toDate);
            if (diff1 > 0 && diff2 > 0)
            {
                switch (dayForPricing)
                {
                    case "strPriceMonday":
                        Price = obHappyHourItem.strPriceMonday;
                        break;

                    case "strPriceTuesday":
                        Price = obHappyHourItem.strPriceTuesday;
                        break;

                    case "strPriceWednesday":
                        Price = obHappyHourItem.strPriceWednesday;
                        break;

                    case "strPriceThursday":
                        Price = obHappyHourItem.strPriceThursday;
                        break;

                    case "strPriceFriday":
                        Price = obHappyHourItem.strPriceFriday;
                        break;

                    case "strPriceSaturday":
                        Price = obHappyHourItem.strPriceSaturday;
                        break;

                    case "strPriceSunday":
                        Price = obHappyHourItem.strPriceSunday;
                        break;
                }
            }
            else
            {
                switch (dayForPricing)
                {
                    case "strPriceMonday":
                        Price = ob.strPriceMonday;
                        break;

                    case "strPriceTuesday":
                        Price = ob.strPriceTuesday;
                        break;

                    case "strPriceWednesday":
                        Price = ob.strPriceWednesday;
                        break;

                    case "strPriceThursday":
                        Price = ob.strPriceThursday;
                        break;

                    case "strPriceFriday":
                        Price = ob.strPriceFriday;
                        break;

                    case "strPriceSaturday":
                        Price = ob.strPriceSaturday;
                        break;

                    case "strPriceSunday":
                        Price = ob.strPriceSunday;
                        break;
                }
            }
        }
        else
        {
            switch (dayForPricing)
            {
                case "strPriceMonday":
                    Price = ob.strPriceMonday;
                    break;

                case "strPriceTuesday":
                    Price = ob.strPriceTuesday;
                    break;

                case "strPriceWednesday":
                    Price = ob.strPriceWednesday;
                    break;

                case "strPriceThursday":
                    Price = ob.strPriceThursday;
                    break;

                case "strPriceFriday":
                    Price = ob.strPriceFriday;
                    break;

                case "strPriceSaturday":
                    Price = ob.strPriceSaturday;
                    break;

                case "strPriceSunday":
                    Price = ob.strPriceSunday;
                    break;
            }
        }

        return Price;
    }

	function funTxtTableNoClicked()
	{
		$("#txtWaiterName").val("");	
		$("#txtWaiterNo").val("");
		$("#txtPaxNo").val(0);
		var $rows = $('#tblMenuItemDtl').empty();
		var $rows = $('#tblMenuHeadDtl').empty();
		var $rows = $('#tblTopButtonDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		
		var jsonArrTabledtl=${command.jsonArrForTableDtl};	
		
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		
		$.each(jsonArrTabledtl, function(i, obj) 
		{									
				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
				{
					var col=insertTR.insertCell(insertCol);
					if(obj.strStatus=="Occupied")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Normal")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; \"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Billed")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #0080ff; \"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Reserve")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #00ff40;\"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
						
						insertCol++;
				}
				else
				{					
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
									
					var col=insertTR.insertCell(insertCol);
					if(obj.strStatus=="Occupied")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #ff0d0d;\"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Normal")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; \"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Billed")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #0080ff; \"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
					if(obj.strStatus=="Reserve")
						col.innerHTML = "<td><input type=\"button\" id="+obj.strTableNo+" value="+obj.strTableName+"&#x00A;&#x00A;"+obj.intPaxNo+"    style=\"width: 100px;height: 100px; background: #00ff40;\"  onclick=\"funTableClicked(this,"+i+")\" /></td>";
						
					insertCol++;
				}							
		});
	}
	
	function funFillTopButtonList(menuHeadCode)
	{		
		menucode=menuHeadCode;
		var searchurl=getContextPath()+"/funFillTopButtonList.html?menuHeadCode="+menuHeadCode;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {	
			        	funAddTopButtonData(response.topButtonList);
			        	
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
	
	function funAddTopButtonData(topButtonList)
	{
		var $rows = $('#tblTopButtonDtl').empty();
		var tblTopButtonDtl=document.getElementById('tblTopButtonDtl');
		var insertCol=0;
		var insertTR=tblTopButtonDtl.insertRow();
		
		$.each(topButtonList, function(i, obj) 
		{		
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td><input type=\"button\" id="+obj.strCode+" value="+obj.strName+"    style=\"width: 90px;height: 30px; white-space: normal;\"  onclick=\"funTopButtonClicked(this)\" /></td>";
					insertCol++; 
		});
	}
	
	function funTopButtonClicked(objMenuHeadButton)
	{
		
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		
		var selctedMenuHeadCode=objMenuHeadButton.id;
	
		itemPriceDtlList=new Array();
		var searchurl=getContextPath()+"/funFillitemsSubMenuWise.html";
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        data:{ strMenuCode:menucode,
			        	flag:flagPopular,
			        	selectedButtonCode:selctedMenuHeadCode,
					},
			        dataType: "json",
			        success: function(response)
			        {	
			        
			        var rowCount = tblMenuItemDtl.rows.length;	
					
					var insertCol=0;
					var insertTR=tblMenuItemDtl.insertRow();
					var index=0;
			    		$.each(response.SubMenuWiseItemList, function(i, obj) 
			    		{									
			    												
			    				if(insertCol<tblMenuItemDtl_MAX_COL_SIZE)
			    				{
			    					index=rowCount*4+insertCol;
			    					var col=insertTR.insertCell(insertCol);
			    					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
			    					
			    					insertCol++;
			    				}
			    				else
			    				{		rowCount++;	 		
			    					insertTR=tblMenuItemDtl.insertRow();									
			    					insertCol=0;
			    					index=rowCount*4+insertCol;				
			    					var col=insertTR.insertCell(insertCol);
			    					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
			    					
			    					insertCol++;
			    				}							
			    			
			    				itemPriceDtlList[index]=obj;
			    			  
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
	
	function funDeleteBtnClicked()
	{
		var table = document.getElementById("tblBillItemDtl");
		
			table.deleteRow(selectedRowIndex);
			funFillKOTList();
		
	}
	function funChgQtyBtnClicked()
	{
		if(selectedRowIndex>0)
		{
		 	var table = document.getElementById("tblBillItemDtl");
			var rowCount = table.rows.length;
			var iteCode=table.rows[selectedRowIndex].cells[1].innerHTML;
		  
		 	var codeArr = iteCode.split('value=');
		    var code=codeArr[1].split('onclick=');
		    var oldQty=code[0].substring(1, (code[0].length-2));
		    var rate=(tblBillItemDtl.rows[selectedRowIndex].cells[2].children[0].value)/(tblBillItemDtl.rows[selectedRowIndex].cells[1].children[0].value);
		    var qty=parseFloat(prompt("Enter Quantity", oldQty));
		  //$("#dblQuantity."+selectedRowIndex).val(qty);
		     if(qty!=null)
		  {
		    document.getElementById("quantity."+(selectedRowIndex)).value=qty;
		  
			  var itemAmt=qty*rate;
			  document.getElementById("amount."+(selectedRowIndex)).value=itemAmt;
			  funFillKOTList();
			  funCalculateTax();
		  }
		}
		  
	}
	
	
	function funMoveSelectedRow(count)
	{
		if(count==1)
			{
				if (selectedRowIndex == 0)
				{
					//do nothing
				}
				else
				{
					 var tblBillItemDtl = document.getElementById("tblBillItemDtl");
				
					var itemName=tblBillItemDtl.rows[selectedRowIndex].cells[0].children[0].value;
					var itemQty=tblBillItemDtl.rows[selectedRowIndex].cells[1].children[0].value;
					var itemAmt=tblBillItemDtl.rows[selectedRowIndex].cells[2].children[0].value;
					var itemCode=tblBillItemDtl.rows[selectedRowIndex].cells[3].children[0].value;
					var itemDiscAmt=tblBillItemDtl.rows[selectedRowIndex].cells[4].children[0].value;
			 		var groupcode=tblBillItemDtl.rows[selectedRowIndex].cells[5].innerHTML;
			 		var subgroupcode=tblBillItemDtl.rows[selectedRowIndex].cells[6].innerHTML;
			 		var subgroupName=tblBillItemDtl.rows[selectedRowIndex].cells[7].innerHTML;
			 		var groupName=tblBillItemDtl.rows[selectedRowIndex].cells[8].innerHTML;
					
			 		var gCode = groupcode.split('value=');
					var strGroupCode=gCode[1].substring(1, (gCode[1].length-2));
					
					var sgCode= subgroupcode.split('value=');
					var strSubGroupCode=sgCode[1].substring(1, (sgCode[1].length-2));
					
					var gName= groupName.split('value=');
					var strGroupName=gName[1].substring(1, (gName[1].length-2));
					
					var sgName= subgroupName.split('value=');
					var strSGName=sgName[1].substring(1, (sgName[1].length-2));
					
					
					var itemName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[0].children[0].value;
					var itemQty1=tblBillItemDtl.rows[selectedRowIndex-1].cells[1].children[0].value;
					var itemAmt1=tblBillItemDtl.rows[selectedRowIndex-1].cells[2].children[0].value;
					var itemCode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[3].children[0].value;
					var itemDiscAmt1=tblBillItemDtl.rows[selectedRowIndex-1].cells[4].children[0].value;
			 		var groupcode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[5].innerHTML;
			 		var subgroupcode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[6].innerHTML;
			 		var subgroupName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[7].innerHTML;
			 		var groupName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[8].innerHTML;
					
			 		var gCode1 = groupcode1.split('value=');
					var strGroupCode1=gCode1[1].substring(1, (gCode1[1].length-2));
					
					var sgCode1= subgroupcode1.split('value=');
					var strSubGroupCode1=sgCode1[1].substring(1, (sgCode1[1].length-2));
					
					var gName1= groupName1.split('value=');
					var strGroupName1=gName1[1].substring(1, (gName1[1].length-2));
					
					var sgName1= subgroupName1.split('value=');
					var strSGName1=sgName1[1].substring(1, (sgName1[1].length-2));
					
				  
				  funMoveRowUp(itemName,itemQty,itemAmt,0.0,itemDiscAmt,strGroupCode,strSubGroupCode,gName,strSGName,itemCode,itemName1,itemQty1,itemAmt1,0.0,itemDiscAmt1,strGroupCode1,strSubGroupCode1,gName1,strSGName1,itemCode1,selectedRowIndex);
				}
				
			}
			else
			{
				 var tblBillItemDtl = document.getElementById("tblBillItemDtl");
				var rowCount = tblBillItemDtl.rows.length;
				if(rowCount>0)
				{

					var itemName=tblBillItemDtl.rows[selectedRowIndex].cells[0].children[0].value;
					var itemQty=tblBillItemDtl.rows[selectedRowIndex].cells[1].children[0].value;
					var itemAmt=tblBillItemDtl.rows[selectedRowIndex].cells[2].children[0].value;
					var itemCode=tblBillItemDtl.rows[selectedRowIndex].cells[3].children[0].value;
					var itemDiscAmt=tblBillItemDtl.rows[selectedRowIndex].cells[4].children[0].value;
			 		var groupcode=tblBillItemDtl.rows[selectedRowIndex].cells[5].innerHTML;
			 		var subgroupcode=tblBillItemDtl.rows[selectedRowIndex].cells[6].innerHTML;
			 		var subgroupName=tblBillItemDtl.rows[selectedRowIndex].cells[7].innerHTML;
			 		var groupName=tblBillItemDtl.rows[selectedRowIndex].cells[8].innerHTML;
					
			 		var gCode = groupcode.split('value=');
					var strGroupCode=gCode[1].substring(1, (gCode[1].length-2));
					
					var sgCode= subgroupcode.split('value=');
					var strSubGroupCode=sgCode[1].substring(1, (sgCode[1].length-2));
					
					var gName= groupName.split('value=');
					var strGroupName=gName[1].substring(1, (gName[1].length-2));
					
					var sgName= subgroupName.split('value=');
					var strSGName=sgName[1].substring(1, (sgName[1].length-2));
					
					
					var itemName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[0].children[0].value;
					var itemQty1=tblBillItemDtl.rows[selectedRowIndex-1].cells[1].children[0].value;
					var itemAmt1=tblBillItemDtl.rows[selectedRowIndex-1].cells[2].children[0].value;
					var itemCode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[3].children[0].value;
					var itemDiscAmt1=tblBillItemDtl.rows[selectedRowIndex-1].cells[4].children[0].value;
			 		var groupcode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[5].innerHTML;
			 		var subgroupcode1=tblBillItemDtl.rows[selectedRowIndex-1].cells[6].innerHTML;
			 		var subgroupName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[7].innerHTML;
			 		var groupName1=tblBillItemDtl.rows[selectedRowIndex-1].cells[8].innerHTML;
					
			 		var gCode1 = groupcode1.split('value=');
					var strGroupCode1=gCode1[1].substring(1, (gCode1[1].length-2));
					
					var sgCode1= subgroupcode1.split('value=');
					var strSubGroupCode1=sgCode1[1].substring(1, (sgCode1[1].length-2));
					
					var gName1= groupName1.split('value=');
					var strGroupName1=gName1[1].substring(1, (gName1[1].length-2));
					
					var sgName1= subgroupName1.split('value=');
					var strSGName1=sgName1[1].substring(1, (sgName1[1].length-2));
					
					  funMoveRowDown(itemName,itemQty,itemAmt,0.0,itemDiscAmt,strGroupCode,strSubGroupCode,gName,strSGName,itemCode,itemName1,itemQty1,itemAmt1,0.0,itemDiscAmt1,strGroupCode1,strSubGroupCode1,gName1,strSGName1,itemCode1,selectedRowIndex);
				}
				
			}
	}
	
	  function funMoveRowUp(itemName,qty,amount,itemDiscPer,itemDiscAmt,strGroupCode,strSubGroupCode,strGroupName,strSubGroupName,itemCode,itemName1,qty1,amount1,itemDiscPer1,itemDiscAmt1,strGroupCode1,strSubGroupCode1,strGroupName1,strSubGroupName1,itemCode1,rowCount)
		{
		
			

			
			var tblBillItemDtl = document.getElementById("tblBillItemDtl");
			tblBillItemDtl.deleteRow(rowCount);
		    var row = tblBillItemDtl.insertRow(rowCount-1);
		    row = tblBillItemDtl.rows[rowCount-1];

	    
		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"    id=\"itemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
		    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
		    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"    id=\"itemCode."+(rowCount)+"\" value='"+itemCode+"' />";
		    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"   id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
		    row.insertCell(5).innerHTML= "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  id=\"strGroupcode."+(rowCount-1)+"\" value='"+strGroupCode+"' />";	    
		    row.insertCell(6).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  id=\"strSubGroupCode."+(rowCount)+"\" value='"+strSubGroupCode+"' />";
		    row.insertCell(7).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"   id=\"strSubGroupName."+(rowCount)+"\" value='"+strSubGroupName+"' />";
		    row.insertCell(8).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"   id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName+"' />";
		      
		   	   
		    row = tblBillItemDtl.rows[rowCount-1];
			row.style.backgroundColor='#ffd966';
			selectedRowIndex=rowCount-1;
			var row1 = tblBillItemDtl.insertRow(rowCount+1);

		    row1.insertCell(0).innerHTML= "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"    id=\"itemName."+(rowCount)+"\" value='"+itemName1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		    row1.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty1)+"' onclick=\"funChangeQty(this)\"/>";
		    row1.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount1+"'/>";
		    row1.insertCell(3).innerHTML= "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"    id=\"itemCode."+(rowCount)+"\" value='"+itemCode1+"' />";
		    row1.insertCell(4).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"   id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
		    row1.insertCell(5).innerHTML= "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  id=\"strGroupcode."+(rowCount-1)+"\" value='"+strGroupCode1+"' />";	    
		    row1.insertCell(6).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  id=\"strSubGroupCode."+(rowCount)+"\" value='"+strSubGroupCode1+"' />";
		    row1.insertCell(7).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"   id=\"strSubGroupName."+(rowCount)+"\" value='"+strSubGroupName1+"' />";
		    row1.insertCell(8).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"   id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName1+"' />";
		    tblBillItemDtl.deleteRow(rowCount);
		}
	  
	  
		function funMoveRowDown(itemName,qty,amount,itemDiscPer,itemDiscAmt,strGroupCode,strSubGroupCode,strGroupName,strSubGroupName,itemCode,itemName1,qty1,amount1,itemDiscPer1,itemDiscAmt1,strGroupCode1,strSubGroupCode1,strGroupName1,strSubGroupName1,itemCode1,rowCount)
		{


			var tblBillItemDtl = document.getElementById("tblBillItemDtl");
			tblBillItemDtl.deleteRow(rowCount);
		    var row = tblBillItemDtl.insertRow(rowCount+1);
		    row = tblBillItemDtl.rows[rowCount+1];
		    
	
		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"    id=\"itemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"4.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
		    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"5.5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
		    row.insertCell(3).innerHTML= "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"    id=\"itemCode."+(rowCount)+"\" value='"+itemCode+"' />";
		    row.insertCell(4).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"   id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
		    row.insertCell(5).innerHTML= "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  id=\"strGroupcode."+(rowCount-1)+"\" value='"+strGroupCode+"' />";	    
		    row.insertCell(6).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  id=\"strSubGroupCode."+(rowCount)+"\" value='"+strSubGroupCode+"' />";
		    row.insertCell(7).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"   id=\"strSubGroupName."+(rowCount)+"\" value='"+strSubGroupName+"' />";
		    row.insertCell(8).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"   id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName+"' />";
		      
		
			  
			
			     row = tblBillItemDtl.rows[rowCount+1];
				row.style.backgroundColor='#ffd966';
				selectedRowIndex=rowCount+1;
				var row1 = tblBillItemDtl.insertRow(rowCount);

			    row1.insertCell(0).innerHTML= "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"    id=\"itemName."+(rowCount)+"\" value='"+itemName1+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			    row1.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty1)+"' onclick=\"funChangeQty(this)\"/>";
			    row1.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount1+"'/>";
			    row1.insertCell(3).innerHTML= "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"    id=\"itemCode."+(rowCount)+"\" value='"+itemCode1+"' />";
			    row1.insertCell(4).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"   id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
			    row1.insertCell(5).innerHTML= "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  id=\"strGroupcode."+(rowCount-1)+"\" value='"+strGroupCode1+"' />";	    
			    row1.insertCell(6).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  id=\"strSubGroupCode."+(rowCount)+"\" value='"+strSubGroupCode1+"' />";
			    row1.insertCell(7).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"   id=\"strSubGroupName."+(rowCount)+"\" value='"+strSubGroupName1+"' />";
			    row1.insertCell(8).innerHTML= "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"   id=\"strGroupName."+(rowCount)+"\" value='"+strGroupName1+"' />";
			    tblBillItemDtl.deleteRow(rowCount);
			}
		
		function funOnCloseBtnClick()
		{
			document.getElementById("divItemDtl").style.display='block';
			 document.getElementById("divPLU").style.display='none';
		
		}
		
</script>

</head>
<body>

	<div id="formHeading">
	<label></label>
	</div>

<br/>
<br/>

	<s:form name="frmMakeKOT" method="POST" commandName="command" action="" >			
			<table id="tblArea"  cellpadding="0" cellspacing="2" style=" margin-left: 350px; " > <!-- class="table table-striped table-bordered table-hover" -->
							<tr><td>
							<label id="lblAreaName"   class="transForm_button" style=" margin-left:80px; width: 150px;height: 24px; display: inline-block;  text-align: center; "></label>
							</td>
							<td>
							<s:input id="txtSearch" path="" value="" cssClass="searchTextBoxW120px jQKeyboard form-control"  style=" "/>
							</td></tr>		 									   				   									   									   							
									</table>									
						
						
						
			<div id="divMain" style=" margin-left: 50px; " >				
				<table  >
									
					<tr>
						<td>
						<div style=" border: 1px solid #ccc; height: 50px;  width: 325px;">
							<table >
								<tr>
								<td>
					<label>KOT No</label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td >
					<s:input  type="text"  id="txtKOTNo" path="strKOTNo"  cssStyle="width:115px; height:20px;" cssClass="searchTextBox jQKeyboard form-control" readonly="true" ondblclick="funHelp1('POSMaster.a')" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<label>Table</label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<input  type="text"  id="txtTableName"  style="text-transform: uppercase; width:35px; height:20px;" class="longTextBox jQKeyboard form-control" onclick="funTxtTableNoClicked()" ondblclick="funHelp1('POSTableMaster')" /> 
<%-- 				<s:input  type="hidden"  id="txtTableNo" path="strTableNo" />  --%>
				
				</td>
								</tr>
								<tr>
								<td>
					<label >Waiter</label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
<%-- 					<s:input  type="hidden"  id="txtWaiterNo" path="strWaiter" />  --%>
				
					<input  type="text"  id="txtWaiterName"  style="text-transform: uppercase; width:115px; height:20px;" class="longTextBox jQKeyboard form-control" onclick="funAddWaiterDtl()" ondblclick="funHelp1('POSWaiterMaster')" />
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<label >PAX</label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<s:input  type="text"  id="txtPaxNo" path="intPaxNo" cssStyle="width:35px; height:20px;" cssClass="longTextBox jQKeyboard form-control"  /> 
				</td>
								</tr>
								</table>
								</div>
							<div id="divBillItemDtl" style=" border: 1px solid #ccc; height: 400px;  overflow-x: auto; overflow-y: auto; width: 325px;">
								
								<table id="tblBillItemDtl"  cellpadding="0" cellspacing="0" ><!-- class="transTablex" -->
									<tr>
										  <th><input type="button" value="Description" style="width: 220px;" class="tblBillItemDtlColBtnGrp" ></input></th>
										  <th><input type="button" value="Qty" style="width: 47px;" class="tblBillItemDtlColBtnGrp" ></input></th>
										  <th><input type="button" value="Amount" style="width: 55px;" class="tblBillItemDtlColBtnGrp"></input></th>
										  <th><input type="button" value="Item Code" style="width: 60px;" class="tblBillItemDtlColBtnGrp"></input></th>
										  <th><input type="button" value="Serial No" style="width: 60px;" class="tblBillItemDtlColBtnGrp"></input></th>
										  									
									</tr>																	
								</table>
								<table id="tblOldKOTItemDtl"  cellpadding="0" cellspacing="0" ><!-- class="transTablex" -->
								</table>
								
						</div>
						
								<div id="divTotalDtl" style=" border: 1px solid #ccc; height: 60px;  width: 325px;">									
									<table>
								<tr>
				<td><input type="button" id="chgQty" value="CHG QTY" style="width: 70px;height: 30px; " onclick="funChgQtyBtnClicked()"/></td>
				<td><input type="button" id="delete" value="Delete" style="width: 80px;height: 30px; " onclick="funDeleteBtnClicked()"/></td>
				<td>
					&nbsp;&nbsp;&nbsp;<label >TOTAL</label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<s:input  type="text"  id="txtTotal" path="total" cssStyle="width:70px;" cssClass="longTextBox jQKeyboard form-control"  /> 
				</td>
								
								 <input id="btnUp" type="button" style="width: 60px;height: 30px;" value="Up" onclick="funMoveSelectedRow(1);" ></input>
			       
			        <input id="btnDown" type="button" style="width: 60px;height: 30px;" value="Down" onclick="funMoveSelectedRow(0);"></input></td>
								</tr>
								
								</table>
								</div>
						</td>		
							
						<td>
						
												<div id="divTopButtonDtl" style=" border: 1px solid #ccc; height: 50px;  overflow-x: auto; overflow-y: auto; width: 445px;">									
								
									<table id="tblTopButtonDtl"   cellpadding="0" cellspacing="2">
									</table>
									</div>
										<div id="divItemDtl" style=" border: 1px solid #ccc; height: 400px;  overflow-x: auto; overflow-y: auto; width: 445px;">									
								
									<table id="tblMenuItemDtl"   cellpadding="0" cellspacing="5">
									 <c:set var="sizeOfMenuItems" value="${fn:length(command.jsonArrForTableDtl)}"></c:set>									   
									   <c:set var="itemCounter" value="${0}"></c:set>									   									   					
										<%-- ${varMenuItemStatus.getIndex() ${varMenuItemStatus.count} ${sizeOfMenuItems} --%>																			   									  
									   <c:forEach var="objItemPriceDtl" items="${command.jsonArrForTableDtl}"  varStatus="varMenuItemStatus">																																		
												<tr>
												<%
													for(int x=0; x<4; x++)
													{
												%>														
														<c:if test="${itemCounter lt sizeOfMenuItems}">	
																
															<c:if test="${command.jsonArrForTableDtl[itemCounter].strStatus eq 'Normal'}"> 		
																<td><input type="button" id="${command.jsonArrForTableDtl[itemCounter].strTableNo}"  value="${command.jsonArrForTableDtl[itemCounter].strTableName}&#x00A;&#x00A;${command.jsonArrForTableDtl[itemCounter].intPaxNo}" style="width: 100px;height: 100px; "  onclick="funTableClicked(this,${itemCounter})" /></td>																				
															</c:if>
																 	
															<c:if test="${command.jsonArrForTableDtl[itemCounter].strStatus eq 'Occupied'}">
															 	<td><input type="button" id="${command.jsonArrForTableDtl[itemCounter].strTableNo}" value="${command.jsonArrForTableDtl[itemCounter].strTableName}&#13;&#10;${command.jsonArrForTableDtl[itemCounter].intPaxNo}" style="width: 100px;height: 100px; background: #ff0d0d;"  onclick="funTableClicked(this,${itemCounter})" /></td>																						 																											 		
															</c:if> 
																
															<c:if test="${command.jsonArrForTableDtl[itemCounter].strStatus eq 'Billed'}">
															 	<td><input type="button" id="${command.jsonArrForTableDtl[itemCounter].strTableNo}" value="${command.jsonArrForTableDtl[itemCounter].strTableName}&#13;&#10;${command.jsonArrForTableDtl[itemCounter].intPaxNo}" style="width: 100px;height: 100px; background: #0080ff;"  onclick="funTableClicked(this,${itemCounter})" /></td>																						 																											 		
															</c:if>
																
															<c:if test="${command.jsonArrForTableDtl[itemCounter].strStatus eq 'Reserve'}">
															 	<td><input type="button" id="${command.jsonArrForTableDtl[itemCounter].strTableNo}" value="${command.jsonArrForTableDtl[itemCounter].strTableName}&#x00A;&#x00A;${command.jsonArrForTableDtl[itemCounter].intPaxNo}" style="width: 100px;height: 100px; background: #00ff40;"  onclick="funTableClicked(this,${itemCounter})" /></td>																						 																											 		
															</c:if>
																
															<c:set var="itemCounter" value="${itemCounter +1}"></c:set>
																
													 	</c:if>																 															
												<%  }
												%>
											   </tr>																																
										</c:forEach>	
									</table>
								</div> 
								
									<div id="divPLU" style=" border: 1px solid #ccc; height: 425px;  overflow-x: auto; overflow-y: auto; width: 445px;">
								<s:input type="text"  id="txtItemSearch" path=""  cssStyle="width:175px; height:20px;" cssClass="searchTextBox jQKeyboard form-control" />
								<input type="button" id="btnClose"  value="Close" onclick="funOnCloseBtnClick()"  style="width:175px; height:20px;  "/>
				     		<table id="tblItems" class="transTablex" style="width: 100%;"></table>
							
								</div>
								
								
					<div id="divCardBalDtl" style=" border: 1px solid #ccc; height: 50px;  width: 445px;">									
									<table > 
									 	<tr>
										<td>
										<label id="txtCardBalance"></label>
										<s:input  type="hidden"  id="txtDeditCardBalance" path="strDeditCardBalance" /> 
										</td>
										</tr>								   				   									   									   							
									</table>
					</div>	
								<div style="width: 445px; height: 25px;">
								<table><tr><td><label style="display:none;" id="lblDpName">Delivery Boy : </label></td><td><label id="dpName"></label></td></tr></table>
								</div>
						</td>
						 <td>
						<div id="divPaxNo" style=" border: 1px solid #ccc; height: 500px;  overflow-x: auto; overflow-y: auto;  width: 40px;">									
									<table id="tblPaxNo"  cellpadding="0" cellspacing="3" style="display:none;" class="table table-striped table-bordered table-hover">
									 <tr><td><input type="button" id="1" value=1 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr>	 
									 <tr><td><input type="button" id="2" value=2 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="3" value=3 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="4" value=4 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="5" value=5 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="6" value=6 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="7" value=7 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="8" value=8 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="9" value=9 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr> 
									 <tr><td><input type="button" id="10" value=10 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr>
									 <tr><td><input type="button" id="11" value=11 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr>	
									 <tr><td><input type="button" id="12" value=12 style="width: 35px;height: 35px; " onclick="funPaxClicked(this)"/></td></tr>	
									   	
									    							   				   									   									   							
									</table>
								</div>
						</td> 
						<td>								
								<div id="divMenuHeadDtl" style=" border: 1px solid #ccc; height: 500px;  overflow-x: auto; overflow-y: auto; width: 130px;">									
									<table id="tblMenuHeadDtl"  cellpadding="0" cellspacing="5"  > <!-- class="table table-striped table-bordered table-hover" -->
									 									   				   									   									   							
									</table>
								</div>
						</td>
					</tr>
				</table>
				
				
			
			<div style="text-align: right;">
				<!-- <div id="divBottomButtonsNavigator" style="border: 1px solid #ccc; height: 40px;  overflow-x: auto; overflow-y:; width: 615px; "> -->
				 	<table id="tblFooterButtons"  cellpadding="0" cellspacing="2"  > <!-- class="table table-striped table-bordered table-hover" -->				 																																	
							<tr>							
								<c:forEach var="objFooterButtons" items="${command.jsonArrForButtonList}"  varStatus="varFooterButtons">								
										<td><input  type="button" id="${objFooterButtons}"  value="${objFooterButtons}" tabindex="${varFooterButtons.getIndex()}"  style="width: 100px;height: 35px; white-space: normal;"   onclick="funFooterButtonClicked(this)"/></td>																									   																															
								</c:forEach>
								<td><input type="button"  id="PLU" value="PLU"    style="width: 100px;height: 35px; white-space: normal;"   onclick="funFooterButtonClicked(this)"/></td>																						
						    </tr>																																				 									   				   									   									   						
					</table>			
		 		<!-- </div> -->		 				 																						
			</div>
			</div>
		 
		 	
		
		 
		

	</s:form>
</body>
</html>
