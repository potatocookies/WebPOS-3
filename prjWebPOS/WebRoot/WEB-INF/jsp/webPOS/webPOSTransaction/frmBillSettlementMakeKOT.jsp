
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>
</title>


<script type="text/javascript">


var hmGroupMap=new Map();
var hmSubGroupMap=new Map();
var hmItempMap=new Map();
var listBillItem=[];
var netTotal=0.0;
var grandTotal=0.0;
var subTotalonAmt=0,discountonAmt=0;
var gPopUpToApplyPromotionsOnBill="${gPopUpToApplyPromotionsOnBill}";


$(document).ready(function() {
	$(".tab_content").hide();
	$(".tab_content:first").show();
	$("ul.tabs li").click(function() {
		$("ul.tabs li").removeClass("active");
		$(this).addClass("active");
		$(".tab_content").hide();
		var activeTab = $(this).attr("data-state");
		$("#" + activeTab).fadeIn();
		
	});

	  
});


function funMakeBillBtnKOT(ncKot,gTakeAway,globalDebitCardNo,cmsMemCode,cmsMemName,reasonCode,homeDeliveryForTax,arrListHomeDelDetails)
{

	document.getElementById("tab2").style.display='block';
	document.getElementById("Bill").style.display='block';
    document.getElementById("tab1").style.display='none';	
    document.getElementById("KOT").style.display='none';
    var listItmeDtl=[];
    var mergeduplicateItm = new Map();
	var tblBillItemDtl=document.getElementById('tblOldKOTItemDtl');
	var rowCount = tblBillItemDtl.rows.length;
	
	for(var i=1;i<rowCount;i++)
	{
		var itemName=tblBillItemDtl.rows[i].cells[0].children[0].value;
		var itemCode=tblBillItemDtl.rows[i].cells[3].children[0].value;
		var itemQty=tblBillItemDtl.rows[i].cells[1].children[0].value;
		var itemAmt=tblBillItemDtl.rows[i].cells[2].children[0].value;
		
	    if(!(itemQty==" "))
	    {
          if (mergeduplicateItm.has(itemCode))
	      {
        	  var qtynAmt=mergeduplicateItm.get(itemCode);
        	  var prevItemQty= qtynAmt.split("!");
	    	  itemQty=parseFloat(itemQty)+parseFloat(prevItemQty[0]);
	    	  itemAmt=parseFloat(itemAmt)+parseFloat(prevItemQty[1]);
	    		$.each(listItmeDtl, function(i, obj) 
			    {	
	    		  if(itemCode==obj.itemCode)
	    			{
	    		    listItmeDtl.pop(obj);
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
			    }
			  });
	      }
          else{
	          
			
			
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
	// 		funFillSettleTable(itemName,itemQty,itemAmt,0.0,itemDiscAmt,strGroupCode,strSubGroupCode,itemCode);
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
          mergeduplicateItm.set(itemCode,itemQty+"!"+itemAmt);
  
	}
			
		
		
	}
	
	funCalculatePromotion(listItmeDtl);
// 	if(isOk)
// 	{
// 		funCalculatePromotion(listItmeDtl);
		
// 	}
// 	else{
// 		funNoPromtionCalculation(listItmeDtl);
		 	
// 	}
	
	
	
	
	
	    netTotal=subTotalonAmt+discountonAmt;
	    grandTotal=netTotal;
	    funFillTableFooterDtl("","");
 	   
 	   
 	    funFillTableFooterDtl("SubTotal",subTotalonAmt);
 	    funFillTableFooterDtl("Discount",discountonAmt);
 	    funFillTableFooterDtl("NetTotal",netTotal);
    	var taxTotal= funCalculateTaxForItemTbl();
 	    grandTotal=taxTotal+netTotal;
 	    funFillTableFooterDtl("GrandTotal",grandTotal);
 	    funFillTableFooterDtl("PaymentMode","");
	    $('#txtAmount').val(grandTotal);
	 	$('#txtPaidAmount').val(grandTotal);
	 	$('#hidSubTotal').val(subTotalonAmt);
	 	$('#hidDiscountTotal').val(discountonAmt);
	 	$('#hidNetTotal').val(netTotal);
	 	$('#hidGrandTotal').val(grandTotal);
	
	
}



// function funCalculatePromotion(listItmeDtl)
// {
// 	var searchurl=getContextPath()+"/promotionCalculateForKOT.html?";
// 	$.ajax({
// 		 type: "POST",
// 	        url: searchurl,
// 	        data : JSON.stringify(listItmeDtl),
// 	        contentType: 'application/json',
// 	        async: false,
//         success: function (response){
        
//         	    	$.each(response,function(i,item){
//         	    		funFillSettleTable(item.strItemName,item.dblQuantity,item.dblAmount,item.dblDiscountPer,item.dblDiscountAmt,item.strGroupCode,item.strSubGroupCode,item.strItemCode,item.dblRate);
// 		        	});
        
//         },
//         error: function(jqXHR, exception)
//         {
//             if (jqXHR.status === 0) {
//                 alert('Not connect.n Verify Network.');
//             } else if (jqXHR.status == 404) {
//                 alert('Requested page not found. [404]');
//             } else if (jqXHR.status == 500) {
//                 alert('Internal Server Error [500].');
//             } else if (exception === 'parsererror') {
//                 alert('Requested JSON parse failed.');
//             } else if (exception === 'timeout') {
//                 alert('Time out error.');
//             } else if (exception === 'abort') {
//                 alert('Ajax request aborted.');
//             } else {
//                 alert('Uncaught Error.n' + jqXHR.responseText);
//             }		            
//         }
	
// 	});
// 	}
	
	
function funCalculatePromotion(listItmeDtl)
{
	var searchurl=getContextPath()+"/promotionCalculateForKOT.html?";
	$.ajax({
		 type: "POST",
	        url: searchurl,
	        data : JSON.stringify(listItmeDtl),
	        contentType: 'application/json',
	        async: false,
        success: function (response){
                   if(response.checkPromotion=="Y")
                	 {	
                	   if(gPopUpToApplyPromotionsOnBill=="Y")
                		{
                			var isOk=confirm("Do want to Calculate Promotions for this Bill?");
                			
                			if(isOk)
                			{
                				$.each(response.listOfPromotionItem,function(i,item){
                					funFillSettleTable(item.strItemName,item.dblQuantity,item.dblAmount,item.dblDiscountPer,item.dblDiscountAmt,item.strGroupCode,item.strSubGroupCode,item.strItemCode,item.dblRate);
            		        	});
                			}
                			else{
                				funNoPromtionCalculation(listItmeDtl)
                				 	
                			}
                		}else{
                			
                			$.each(response.listOfPromotionItem,function(i,item){
                				funFillSettleTable(item.strItemName,item.dblQuantity,item.dblAmount,item.dblDiscountPer,item.dblDiscountAmt,item.strGroupCode,item.strSubGroupCode,item.strItemCode,item.dblRate);
        		        	});
                		}
        	    	
                	 }
                   else{
                	   
                	   funNoPromtionCalculation(listItmeDtl)
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



	function funCalculateTaxForItemTbl()
	{

		var taxTotal=0;
		var rowCountTax=0;
		var searchurl=getContextPath()+"/funCalculateTaxInSettlement.html?operationTypeForTax="+"KOT";
		$.ajax({
			 type: "POST",
		        url: searchurl,
		        data : JSON.stringify(listBillItem),
		        contentType: 'application/json',
		        async: false,
	        success: function (response){
	        	    	$.each(response,function(i,item){
			        		taxTotal=taxTotal+response[i].taxAmount;
			        		funFillTableTaxDetials(response[i].taxName,response[i].taxAmount,response[i].taxCode,response[i].taxCalculationType,response[i].taxableAmount ,rowCountTax);
			        		rowCountTax++;
			        	});
             
	        	    	grandTotal=grandTotal+taxTotal;
	        	    	 $('#hidTaxTotal').val(taxTotal);
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
		return taxTotal;
	}

	
	
      function funFillTableTaxDetials(taxName,taxAmount,taxCode,taxCalculationType,taxableAmount,rowCountTax)
        {
		var tblSettleItemDtl=document.getElementById('tblSettleItemTable');
		var rowCount = tblSettleItemDtl.rows.length;
		var insertRow = tblSettleItemDtl.insertRow(rowCount);
		
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"27px\" class=\"taxName\"  name=\"listTaxDtlOnBill["+(rowCountTax)+"].taxName\" id=\"taxName."+(rowCount)+"\" style=\"text-align: left; color:blue; height:20px;\"  value='"+taxName+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.1px\"  style=\"text-align: right; color:blue; height:20px;\"   />";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5px\" class=\"taxAmount\"  name=\"listTaxDtlOnBill["+(rowCountTax)+"].taxAmount\" id=\"taxAmount."+(rowCount)+"\"  style=\"text-align: right; color:blue; height:20px;\"  value='"+taxAmount+"'  />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   style=\"text-align: right; color:blue; height:20px;\"  />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   style=\"text-align: right; color:blue; height:20px;\"  />";
	    col6.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"taxCode\"  name=\"listTaxDtlOnBill["+(rowCountTax)+"].taxCode\" id=\"taxCode."+(rowCount)+"\" value='"+taxCode+"' />";
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"taxCalculationType\"  name=\"listTaxDtlOnBill["+(rowCountTax)+"].taxCalculationType\" id=\"taxCalculationType."+(rowCount)+"\" value='"+taxCalculationType+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"taxableAmount\"  name=\"listTaxDtlOnBill["+(rowCountTax)+"].taxableAmount\" id=\"taxableAmount."+(rowCount)+"\" value='"+taxableAmount+"' />";
	   
   }


      
   function funFillTableFooterDtl(column1,column2){
	   

		var tblSettleItemDtl=document.getElementById('tblSettleItemTable');
		var rowCount = tblSettleItemDtl.rows.length;
		var insertRow = tblSettleItemDtl.insertRow(rowCount);
		
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"27px\"  style=\"text-align: left; color:blue; height:20px;\" id=\"column1."+(rowCount)+"\" value='"+column1+"'  />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.1px\" style=\"text-align: right; color:blue; height:20px;\"   />";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"5px\"   style=\"text-align: right; color:blue; height:20px;\" id=\"column2."+(rowCount)+"\" value='"+column2+"' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   style=\"text-align: right; color:blue; height:20px;\"  />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   style=\"text-align: right; color:blue; height:20px;\"  />";
	    
	    
   }
   
   
       function funNoPromtionCalculation(listItmeDtl)
       {
		
  	    	$.each(listItmeDtl,function(i,item){
  	    		funFillSettleTable(item.itemName,item.quantity,item.amount,item.discountPer,item.discountAmt,item.strGroupcode,item.strSubGroupCode,item.itemCode,item.rate);
       	    });

	}



$(document).ready(function(){
	
 	var operationFrom="${operationFrom}";
	
	   document.getElementById("tab2").style.display='none';	
	   document.getElementById("Bill").style.display='none';	
	 
		

});


function funFillSettleTable(strItemName,dblQuantity,dblAmount,dblDiscountPer1,dblDiscountAmt1,strGroupCode,strSubGroupCode,strItemCode,dblRate)
{
	var tblSettleItemDtl=document.getElementById('tblSettleItemTable');
	var rowCount = tblSettleItemDtl.rows.length;
	var insertRow = tblSettleItemDtl.insertRow(rowCount);
			
    var col1=insertRow.insertCell(0);
    var col2=insertRow.insertCell(1);
    var col3=insertRow.insertCell(2);
    var col4=insertRow.insertCell(3);
    var col5=insertRow.insertCell(4);
    var col6=insertRow.insertCell(5);
    var col7=insertRow.insertCell(6);
    var col8=insertRow.insertCell(7);
    var col9=insertRow.insertCell(8);
    col1.innerHTML = "<input readonly=\"readonly\" size=\"27px\"  class=\"itemName\"    style=\"text-align: left; color:blue; height:20px;\"   name=\"listOfBillItemDtl["+(rowCount)+"].itemName\" id=\"strItemName."+(rowCount)+"\" value='"+strItemName+"' />";
    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.1px\"   class=\"itemQty\"      style=\"text-align: right; color:blue; height:20px;\"  name=\"listOfBillItemDtl["+(rowCount)+"].quantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+dblQuantity+"' />";
    col3.innerHTML = "<input readonly=\"readonly\" size=\"5px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue; height:20px;\"  name=\"listOfBillItemDtl["+(rowCount)+"].amount\" id=\"dblAmount."+(rowCount)+"\" value='"+dblAmount+"'/>";
    col4.innerHTML = "<input readonly=\"readonly\" size=\"4px\" class=\"discountPer\"     style=\"text-align: right; color:blue; height:20px;\"   name=\"listOfBillItemDtl["+(rowCount)+"].discountPer\" id=\"tblDiscountPer."+(rowCount)+"\" value='0' />";
    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"discountAmt\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listOfBillItemDtl["+(rowCount)+"].discountAmt\" id=\"tblDiscountAmt."+(rowCount-1)+"\" value='0' />";
    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listOfBillItemDtl["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount)+"\" value='"+strGroupCode+"' />";	    
    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupCode\"  name=\"listOfBillItemDtl["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='"+strSubGroupCode+"' />";
    
    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"itemCode\"  name=\"listOfBillItemDtl["+(rowCount)+"].itemCode\" id=\"itemCode."+(rowCount)+"\" value='"+strItemCode+"' />";
    col9innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"rate\"  name=\"listOfBillItemDtl["+(rowCount)+"].rate\" id=\"rate."+(rowCount)+"\" value='"+dblRate+"' />";

   ///For Calculaing Discount Fill the list with item Dtl
    var singleObj = {}
    singleObj['itemName'] =strItemName;
    singleObj['quantity'] =dblQuantity;
    singleObj['amount'] = dblAmount;
    singleObj['discountPer'] = dblDiscountPer1;
    singleObj['discountAmt'] =dblDiscountAmt1;
    singleObj['strSubGroupCode'] =strSubGroupCode;
    singleObj['strGroupcode'] =strGroupCode;
    singleObj['itemCode'] =strItemCode;
    listBillItem.push(singleObj);
    
    subTotalonAmt=subTotalonAmt+parseFloat(dblAmount);
	discountonAmt=discountonAmt+parseFloat(dblDiscountAmt1);//(itemDiscAmt);

}

</script>



</head>
<body>
<div id="formHeading">
<!-- 		<label>Bill Settlement </label> -->
	</div>
	<s:form name="BillSettlementKOT" method="POST" action="actionBillSettlementKOT.html?saddr=${urlHits}" >	
	
	<br>
		<table
			style="border: 0px solid black; width: 100%; height: 100%; margin-left: auto; margin-right: auto; background-color: #C0E4FF;">
			<tr>
				<td>
				
				<div id="tab_container" style="height: 800px">
						<ul class="tabs">
<!-- 							<li id ="DirectBiller" class="active" data-state="tab1" style="width: 6%; padding-left: 1.2%">General</li> -->
							<li id ="Bill" data-state="tab2" style="width: 6%; padding-left: 2%">Bill Settlement</li>
							<li id ="KOT" data-state="tab3" style="width: 6%; padding-left: 2%">Make KOT</li>
						
						</ul>
						<!--General Tab  Start-->
						<div id="tab1" class="tab_content" style="height: 700px">
							<jsp:include page="frmPOSMakeKOT.jsp" />
			    </div>
			    
			    <div id="tab2" class="tab_content" style="height: 700px">

			   			<jsp:include page="frmPOSBillSettlement.jsp" />
			   	
			    </div>
			    

			    
			    </div>
			    </td>
			   </tr>
			    </table>
			    </s:form>
</body>
</html>