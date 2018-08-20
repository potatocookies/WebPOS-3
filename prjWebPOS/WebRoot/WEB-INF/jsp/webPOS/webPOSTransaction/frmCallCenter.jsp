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

/* .searchTextBox {
    background: #FFF url(../images/textboxsearchimage.png) no-repeat 192px 2px;
    background-color: inherit;
    border: 1px solid #060006;
    outline: 0;
    padding-left: 5px;
    height: 25px;
    width: 203px;       
}
 */
.menuItemBtn {
 width: 100px;
 height: 100px; 
 white-space: normal; 
}

.controlgroup-textinput{
				padding-top: .22em;
				padding-bottom: .22em;
	
</style>

<script type="text/javascript">
	var gMobileNo="";
	var fieldName;
	var selectedRowIndex=0;
	var gDebitCardPayment="";
	var tblMenuItemDtl_MAX_ROW_SIZE=100;
	var tblMenuItemDtl_MAX_COL_SIZE=4;
	var itemPriceDtlList=new Array();	
	var hmHappyHourItems = new Map();
	var gCustomerCode="",gCustomerName="";
	var currentDate="";
	var currentTime="";
	var dayForPricing="",flagPopular="",menucode="",homeDeliveryForTax="N",gTakeAway="No",cmsMemCode="",cmsMemName="";
	var arrListHomeDelDetails= new Array();
	var arrKOTItemDtlList=new Array();
	var arrDirectBilleritems=new Array();
	var gCustAddressSelectionForBill="${gCustAddressSelectionForBill}";
	var gCMSIntegrationYN="${gCMSIntegrationYN}";
	var gCRMInterface="${gCRMInterface}";
	var gDelBoyCompulsoryOnDirectBiller="${gDelBoyCompulsoryOnDirectBiller}";
	var gRemarksOnTakeAway="${gRemarksOnTakeAway}";
	var gNewCustomerForHomeDel=false;
	var gTotalBillAmount,gNewCustomerMobileNo;
	var gBuildingCodeForHD="",gDeliveryBoyCode="",gBuildingName="";
	var operationType="";
	var delCharges=0.0;
	var gHomeDeliveryAddress="Home";
	var gHomeBuildingName="",gOfficeBuildingName="",gTempBuildingName="";
	
	
	
//Footer btn click
	
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
			funHelp1('POSDeliveryBoyMaster');
			break;
			
		case "Take Away":
			funTakeAwayBtnClicked();
			break;
			
		case "Home":
			funHomeBtnclicked();
			break;
			
		case "Customer History" :
			funCustomerHistoryBtnClicked();
			break;
			
		case "PLU" :
			funPLUItemData();
			break;
			
		}
	}
	
	/**
	* Success Message After Saving Record
	**/
	$(document).ready(function()
	{
			
		
			$("#txtItemSearch").keyup(function()
			{
				searchTable($(this).val());
			});
			 document.getElementById("divItemDtl").style.display='block';
			 document.getElementById("divPLU").style.display='none';
			 
			 document.getElementById("divBillItemDtl").style.display='block';
			 document.getElementById("divTotalDtl").style.display='block';
			 
			 document.getElementById("divTopButtonDtl").style.display='block';
			 document.getElementById("divMenuHeadDtl").style.display='block';
		
			 $("#txtPosCode").change(function() 
		     {
				 if(gCustomerCode.trim().length<=0)
				 {
					alert("Please Select Customer.");
					return;
				 }
				 
				 var posCode=$("#txtPosCode").val()
				 window.location ="funLoadMenu.html?posCode="+posCode+"&customerCode="+gCustomerCode+"&mobileNo="+gMobileNo+"&customerName="+gCustomerName+"&buildingName="+gBuildingName;
				 
				 
			 }); 
		 		 
		 
			 gCustomerCode="${selectedCustomerCode}";
			 gCustomerName="${selectedCustomerName}";
			 gNewCustomerMobileNo="${selectedCustMobileNo}";
			 if(gCustomerName.trim().length>0)
			 {
				 $("#Customer").val(gCustomerName);	
			 }
			 
			 
			
			 document.getElementById("Home Delivery").style.backgroundColor = "lightblue";
			 
			 
			 
			 
			 setTimeout(
					  function() 
					  {
						  var message='';
							<%if (session.getAttribute("success") != null) 
							{
								if (session.getAttribute("successMessage") != null) {%>
									message='<%=session.getAttribute("successMessage").toString()%>';
								    <%session.removeAttribute("successMessage");
								}
								boolean test = ((Boolean) session.getAttribute("success")).booleanValue();
								session.removeAttribute("success");
								if (test) {%>alert("Data Saved \n\n"+message);<%}
								
								
							}%>	
					  },500);
			
				 				
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
	
	
	
	
	
	



	function funPLUItemData() 
	{
		document.getElementById("divItemDtl").style.display='none';
		 document.getElementById("divPLU").style.display='block';
		var table = document.getElementById("tblItems");
		var cntIndex = 0;
		var jsonArrForMenuItemPricing=${command.jsonArrForDirectBillerMenuItemPricing};	
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
	
	
	function funCustomerHistoryBtnClicked()
	{
		 if (gCustomerCode.trim().length==0)
         {
        	 alert("Select Customer!");
             return;
         }
		 else
		 {
				window.open("frmCustomerHistory.html?strCustCode="+gCustomerCode+"","","dialogHeight:450px;dialogWidth:500px;dialogLeft:400px;");
		 }
	}
	
	$('input[data-list]').each(function () {
		  var availableTags = $('#' + $(this).attr("data-list")).find('option').map(function () {
		    return this.value;
		  }).get();

		  $(this).autocomplete({
		    source: availableTags
		  }).on('focus', function () {
		    $(this).autocomplete('search', ' ');
		  }).on('search', function () {
		    if ($(this).val() === '') {
		      $(this).autocomplete('search', ' ');
		    }
		  });
		});
	function funAddCustomerHistory(arr,total)
	{
		$("#txtTotal").val(total);		
		for(var j=0;j<arr.length;j++)
		{
		var itmDtl=arr[j].split("#");
		
		var tblBillItemDtl=document.getElementById('tblBillItemDtl');
		
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
				
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	   
	    
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].itemName\" id=\"itemName."+(rowCount)+"\" value='"+itmDtl[1]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].quantity\" id=\"quantity."+(rowCount)+"\" value='"+parseFloat(itmDtl[2])+"' onclick=\"funChangeQty(this)\"/>";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].amount\" id=\"amount."+(rowCount)+"\" value='"+itmDtl[3]+"'/>";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"4.3px\" class=\"itemCode\"     style=\"text-align: left; color:blue;\"   name=\"listItemsDtlInBill["+(rowCount)+"].itemCode\" id=\"itemCode."+(rowCount)+"\" value='"+itmDtl[0]+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    	
		}
	}
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
	                }
	            }
	        }
		 if (homeDeliveryForTax=="Y")
         {
             if (gCustomerCode.trim().length==0)
             {
            	 alert("Select Customer for Home Delivery!!!");
                 return;
             }
         }
		 if (gCMSIntegrationYN=="Y")
	        {
	            if (gClientCode!="074.001")
	            {
	                if (cmsMemberCode.trim().length == 0)
	                {
                        alert("Please Select Member!!!");
                        return;
	                }
	            }
	        }
		 else if (!((gCustomerCode.trim().length==0) && homeDeliveryForTax=="Y"))
         {
            // billTransType = "Home Delivery";
            var totalBillAmount = 0.00;
		 	 if ($("#txtTotal").val().trim().length > 0)
       		 {
			 	 totalBillAmount = parseFloat($("#txtTotal").val());
     		 }
             if (!gNewCustomerForHomeDel)
             {
            	 delCharges=funGetDeliveryCharges(gBuildingCodeForHD, totalBillAmount, gCustomerCode);
             }
             if (gDelBoyCompulsoryOnDirectBiller=="Y")
             {
                 if (gDeliveryBoyCode == "")
                 {
                     alert( "Please Assign Delivery Boy");
                     return;
                 }
             }
         }
		 else if (gRemarksOnTakeAway=="Y")
         {
             if (gTakeAway=="Yes" && gCustomerCode.trim().length==0 )
             {
                 alert("Select Customer For Take Away!!!");
                 return;
             }
         }
		 var tblBillItemDtl=document.getElementById('tblBillItemDtl');
			
			var rowCount = tblBillItemDtl.rows.length;
			if(rowCount==0)
			{
				alert("Please Select item!");
				return;
			}
			
			
			 $("#txtTakeAway").val(gTakeAway);
			 
			if (arrDirectBilleritems.length > 0)
			{
				$("#txtCustomerName").val(gCustomerName);
        		$("#txtCustomerCode").val(gCustomerCode);
        		$("#txtCustMobileNo").val(gNewCustomerMobileNo);
				
				
				document.frmCallCenter.action="actionCallCenter.html";
				document.frmCallCenter.submit();
				
			}
			else
			{
				alert("Please Select Item first");
			}
			
			/*   $("frmCallCenter").submit(function(event){
				    return true;
				}); */
			  
			
			
			
	}
	function funGetDeliveryCharges(buildingCode, totalBillAmount, gCustomerCode)
	{
	
		var searchurl=getContextPath()+"/funCalculateDeliveryChages.html?buildingCode="+buildingCode+"&totalBillAmount="+totalBillAmount+"&gCustomerCode="+gCustomerCode;

		var delCharges=0;  
		$.ajax({
			 type: "POST",
			        url: searchurl,
			        dataType: "json",
			        async: false,
			        success: function(response)
			        {
			        	delCharges=response;
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
		return delCharges;
		
	}
	function funTakeAwayBtnClicked()
	{
		 homeDeliveryForTax = "N";
		 operationType="TakeAway";
		 $("#billTransType").val(operationType);
		 if(gTakeAway=="No")
			{
			    gTakeAway="Yes";
			    document.getElementById("Take Away").style.backgroundColor = "lightblue";
			}
		else
        {
			gTakeAway = "No";
			 document.getElementById("Take Away").style.backgroundColor = "";
        }
		
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
	function funHomeDeliveryBtnClicked()
	{
		
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
	
	
	function funCheckHomeDelStatus()
	{
		homeDeliveryForTax = "Y";

		if (arrListHomeDelDetails.length == 0)
        {
			operationType="HomeDelivery";
			$("#billTransType").val(operationType);
        
            arrListHomeDelDetails[0]=gCustomerCode;
            arrListHomeDelDetails[1]=gCustomerName;
            arrListHomeDelDetails[2]=gBuildingCodeForHD;
            arrListHomeDelDetails[3]="HomeDelivery";
            arrListHomeDelDetails[4]="";
            arrListHomeDelDetails[5]="";
            document.getElementById("Home Delivery").style.backgroundColor = "lightblue";
        
        }
		else
		{
        	
        	operationType="";
			$("#billTransType").val(operationType);
        	homeDeliveryForTax = "N";
        	arrListHomeDelDetails= new Array();	
        	//document.getElementById("Home Delivery").style.backgroundColor = "";
        	document.getElementById("Home Delivery").style.backgroundColor = "lightblue";
        	
        }
        
		if(gTakeAway=="Yes")
		{
		    gTakeAway="No";
		}
		
		
		//if(gCustAddressSelectionForBill=="Y")
		//{
			//<%session.setAttribute("frmName", "Call Center");%>			
		    //window=window.open("frmHomeDeliveryAddress.html?strMobNo="+gNewCustomerMobileNo,"_blank","height="+screen.height+", width="+screen.width);		   
		//} 
		
		
		<%session.setAttribute("frmName", "Call Center");%>
		
	    window=window.open("frmHomeDeliveryAddress.html?strMobNo="+gNewCustomerMobileNo,"_blank","height="+screen.height+", width="+screen.width);
		
	}
	
	
	
	function funSetHomeDeliveryAddress(gSelectedArea)
	{		
		
		$("#lblAreaName").text("Area:"+gSelectedArea);	 		
		
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
			 $("#txtCustMobileNo").val(strMobNo);
			
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
	
		 if (strMobNo.trim().length == 0)
         {
			 funHelp1('POSCustomerMaster');
         }
		 else
			 funCheckCustomer(strMobNo);
	}
	function funCheckCustomer(strMobNo)
	{
		var totalBillAmount = 0.00;
		  if ($("#txtTotal").val().trim().length > 0)
        {
			  totalBillAmount = parseFloat($("#txtTotal").val());
        }	
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
			        		 gBuildingCodeForHD= response.strBuldingCode;
			        		 
			        		 gBuildingName= response.strBuildingName;
			        		 
			        		 
			        		 
			        		 gNewCustomerMobileNo=strMobNo;
			        		 gMobileNo=strMobNo;
			        		 gCustomerName=response.strCustomerName;
			        		 
			        		 $("#Customer").val(response.strCustomerName);			        		 			        		
			        		 $("#txtCustomerName").val(response.strCustomerName);
			        		 $("#txtCustomerCode").val(gCustomerCode);
			        		 $("#txtCustMobileNo").val(strMobNo);			        					        			                			                 			        		
			        		 
			        		 $("#lblCustomerName").text("Customer:"+gCustomerName);
			        		 $("#lblAreaName").text("Area:"+gBuildingName);
			        		 
			        		 
			        		 funHomeDeliveryBtnClicked();
			        		 
			        		 $("#strPosCode").focus();
			        					        		 			        		
			             }	
			        	 else			        	
			        	 {
			        		 gNewCustomerForHomeDel = true;
		                     gTotalBillAmount = totalBillAmount;
		                     gNewCustomerMobileNo =gMobileNo;
		                     
		                     funCallHomeDeliveryAddressSelectionForm(strMobNo);
			        		 //funCustomerMaster(strMobNo);
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
	
	
	function funCallHomeDeliveryAddressSelectionForm(strMobNo)
	{	
		<%session.setAttribute("frmName", "Call Center");%>
		
	    window=window.open("frmHomeDeliveryAddressForNewCustomer.html?strMobNo="+strMobNo,"_blank","height="+screen.height+", width="+screen.width);
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
			        async: false,
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
		function funUpdateTableBillItemDtlFor(objMenuItemPricingDtl,price,qty)
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
// 	function funSetData(code)
// 	{

// 		switch(fieldName)
// 		{
		
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
		 <%session.setAttribute("frmName", "Call Center");%>

		 
		w=window.open("frmPOSCustomerMaster.html?intlongMobileNo="+strMobNo,"","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;");
		
		
	}
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
	function funFillMapWithHappyHourItems()
	{		
		var searchurl=getContextPath()+"/funFillMapWithHappyHourItems.html";
		$.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        async: false,
			        success: function(response)
			        {
			        	for(var i=0;i<response.ItemPriceDtl.length;i++)
		        		{
			        		hmHappyHourItems.put(response.ItemCode[i],response.ItemPriceDtl[i]);
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
	
	
	//function to add items to bill item table
	
	function funFillTableBillItemDtl(objMenuItemPricingDtl,price,qty)
	{	
			
		
		var itemName=objMenuItemPricingDtl.strItemName.replace(/&#x00A;/g," ");
		var tblBillItemDtl=document.getElementById('tblBillItemDtl');
		
		var rowCount = tblBillItemDtl.rows.length;
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
	    var amount=parseFloat(qty)*price;
	    
	    col1.innerHTML = "<input readonly=\"readonly\" /* size=\"32px\" */  class=\"itemName\"    style=\"text-align: left; color:blue; width:220px; \"   name=\"listItemsDtlInBill["+(rowCount)+"].itemName\" id=\"itemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
	    col2.innerHTML = "<input readonly=\"readonly\" /* size=\"3.5px\" */   class=\"itemQty\"      style=\"text-align: right; color:blue; width:47px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].quantity\" id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
	    col3.innerHTML = "<input readonly=\"readonly\" /* size=\"4px\" */   class=\"itemAmt\"      style=\"text-align: right; color:blue; width:66px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].amount\" id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
	    col4.innerHTML = "<input readonly=\"readonly\" /* size=\"10px\" */ class=\"itemCode\"     style=\"text-align: left; color:blue;  width:100px; \"   name=\"listItemsDtlInBill["+(rowCount)+"].itemCode\" id=\"itemCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strItemCode+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" /* size=\"9px\" */   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue; width:99px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subgroupcode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";
	      
	   
	}
	function funAddModifierTableBillItemDtl(objMenuItemPricingDtl,itemCode)
	{	
		funFillKOTList();
		deleteTableRows();
		var tblBillItemDtl=document.getElementById('tblBillItemDtl');
		var rowCount = tblBillItemDtl.rows.length;
		var insertRow = tblBillItemDtl.insertRow(rowCount);
		var code=itemCode+"!"+objMenuItemPricingDtl.strModifierCode;
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    var col8=insertRow.insertCell(7);
	    var col9=insertRow.insertCell(8);
	    /* .listModifierDtl["+(rowCount)+"]. */
	    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue; width:220px; \"   name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierDescription\" id=\"strItemName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strModifierName+"' />";
	    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"   style=\"text-align: right; color:blue; width:47px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].quantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+1.00+"' />";
	    col3.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"     style=\"text-align: right; color:blue; width:66px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].amount\" id=\"dblAmount."+(rowCount)+"\" value='"+objMenuItemPricingDtl.dblRate+"' />";
	    col4.innerHTML = "<input readonly=\"readonly\" size=\"10px\" class=\"itemCode\"     style=\"text-align: left; color:blue;  width:100px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierCode\" id=\"strItemCode."+(rowCount)+"\" value='"+code+"' />";
	    col5.innerHTML = "<input readonly=\"readonly\" size=\"9px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue; width:99px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
	    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strGroupcode+"' />";	    
	    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupCode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount-1)+"\" value='"+objMenuItemPricingDtl.strSubGroupCode+"' />";
	    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strSubGroupName+"' />";
	    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+objMenuItemPricingDtl.strGroupName+"' />";
	    
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
			    
			    col1.innerHTML = "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue; width:220px; \"    name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierDescription\" id=\"strItemName."+(rowCount)+"\" value='"+data[0]+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
			    col2.innerHTML = "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"   style=\"text-align: right; color:blue; width:47px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].quantity\" id=\"dblQuantity."+(rowCount)+"\" value='"+data[1]+"' onclick=\"funChangeQty(this)\"/>";
			    col3.innerHTML = "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"     style=\"text-align: right; color:blue; width:66px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].amount\" id=\"dblAmount."+(rowCount)+"\" value='"+data[2]+"' />";
			    col4.innerHTML = "<input readonly=\"readonly\" size=\"10px\" class=\"itemCode\"     style=\"text-align: left; color:blue;  width:100px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].modifierCode\" id=\"strItemCode."+(rowCount)+"\" value='"+data[3]+"' />";
			    col5.innerHTML = "<input readonly=\"readonly\" size=\"9px\"   class=\"itemDiscAmt\"  style=\"text-align: right; color:blue; width:99px; \"  name=\"listItemsDtlInBill["+(rowCount)+"].listModifierDtl["+(rowCount)+"].strSerialNo\" id=\"strSerialNo."+(rowCount-1)+"\" value='"+rowCount+"' />";
			    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"groupcode\"  style=\"text-align: right; color:blue;\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupcode\" id=\"strGroupcode."+(rowCount-1)+"\" value='"+data[5]+"' />";	    
			    col7.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupCode\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupCode\" id=\"strSubGroupCode."+(rowCount-1)+"\" value='"+data[6]+"' />";
			    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"subGroupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strSubGroupName\" id=\"strSubGroupName."+(rowCount)+"\" value='"+data[7]+"' />";
			    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"groupName\"  name=\"listItemsDtlInBill["+(rowCount)+"].strGroupName\" id=\"strGroupName."+(rowCount)+"\" value='"+data[8]+"' />";	    
			}
		
		
	}
	
	
	function funMenuItemClicked(objMenuItemButton,objIndex)
	{	
		$("#txtItemSearch").val("");
	
		funFillMapWithHappyHourItems();
	
		var objMenuItemPricingDtl=itemPriceDtlList[objIndex];
		
		var   price = funGetFinalPrice(objMenuItemPricingDtl);
		
		var isOrdered=funIsAlreadyOrderedItem(objMenuItemPricingDtl);
		var qty=prompt("Enter Quantity", 1);
		 if(price==0.00)
			{
			 price = funGetFinalPrice(objMenuItemPricingDtl);
			
			 price = prompt("Enter Price", 0);
			} 
		 
		 if(qty==null || price==null)
			 {
			 	return false;
			 }
		if(isOrdered)
		{
			funUpdateTableBillItemDtlFor(objMenuItemPricingDtl,price,qty);	
		}
		else
		{
			funFillTableBillItemDtl(objMenuItemPricingDtl,price,qty);	
		} 
		
		funFillKOTList();
		funCalculateTax();
	}
	//function on popular item button click
	function funPopularItemButtonClicked(objButton)
	{
		var $rows = $('#tblMenuItemDtl').empty();
		var tblMenuItemDtl=document.getElementById('tblMenuItemDtl');
		var selctedCode=objButton.id;
		flagPopular="Popular";
		funFillTopButtonList(flagPopular);
		var jsonArrForPopularItems=${command.jsonArrForPopularItems};	
		var rowCount = tblMenuItemDtl.rows.length;	
		itemPriceDtlList=new Array();
		var insertCol=0;
		var insertTR=tblMenuItemDtl.insertRow();
		var index=0;
		$.each(jsonArrForPopularItems, function(i, obj) 
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
		var jsonArrForMenuItemPricing=${command.jsonArrForDirectBillerMenuItemPricing};	
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
					col.innerHTML = "<td style=\"padding:  5px;\" ><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"  class=\"btn btn-primary\"  style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					insertCol++;
				}
				else
				{		
					rowCount++;
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
					index=rowCount*4+insertCol;				
					var col=insertTR.insertCell(insertCol);
					col.innerHTML = "<td style=\"padding:  5px;\" ><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"  class=\"btn btn-primary\"  style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					insertCol++;
				}	
				itemPriceDtlList[index]=obj;
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
					col.innerHTML = "<td style=\"padding:  5px;\"><input type=\"button\" id="+obj.strCode+" value="+obj.strName+"    style=\"width: 90px;height: 30px; white-space: normal;\"  onclick=\"funTopButtonClicked(this)\" class=\"btn btn-primary\" /></td>";
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
			    					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" class=\"btn btn-primary\" /></td>";
			    					
			    					insertCol++;
			    				}
			    				else
			    				{		rowCount++;	 		
			    					insertTR=tblMenuItemDtl.insertRow();									
			    					insertCol=0;
			    					index=rowCount*4+insertCol;				
			    					var col=insertTR.insertCell(insertCol);
			    					col.innerHTML = "<td><input type=\"button\" id="+obj.strItemCode+" value="+obj.strItemName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" class=\"btn btn-primary\" /></td>";
			    					
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
		
	
	function funAddPopularItemsData()
	{
		funFillTopButtonList("Popular");
		var jsonArrForPopularItems=${command.jsonArrForPopularItems};	
		itemPriceDtlList=new Array();
		
		$.each(jsonArrForPopularItems, function(i, obj) 
		{									
				itemPriceDtlList[i]=obj;
		});
		
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
		
		kotListForModifier = new Array();
		arrKOTItemDtlList = new Array();
		 $('#tblBillItemDtl tr').each(function() {
			 var code=$(this).find("input[class='itemCode']").val();
			
			 var itemName=$(this).find("input[class='itemName']").val();
			 var itemQty=$(this).find("input[class='itemQty']").val();
			 var itemAmt=$(this).find("input[class='itemAmt']").val();
			 var itemCode=$(this).find("input[class='itemCode']").val();
			 var itemDiscAmt=$(this).find("input[class='itemDiscAmt']").val();
			 var itemGroup=$(this).find("input[class='groupcode']").val();
			 var itemsubGroup=$(this).find("input[class='subGroupCode']").val(); 
			 var itemsubGroupName=$(this).find("input[class='subGroupName']").val(); 
			 var itemGroupName=$(this).find("input[class='groupName']").val(); 
			 var itemDetail=code+"!"+itemName+"#"+itemAmt+"#"+itemQty+"#"+itemDiscAmt;
			 code=code+"_"+itemName+"_"+itemAmt+"_"+itemQty+"_"+itemDiscAmt;
			 
			    	var data=new Array();
			    	 data[0]=itemName;  
			    	data[1]=itemQty;
			    	data[2]=itemAmt;
			    	data[3]=itemCode;
			    	data[4]=itemDiscAmt;
			    	data[5]=itemGroup;
			    	data[6]=itemsubGroup;
			    	data[7]=itemsubGroupName;
			    	data[8]=itemGroupName;
			    	kotListForModifier[i]=data; 
			    	
			    	if(i > 0)
		    		{
		    	arrKOTItemDtlList[i-1]=code;
		    	arrDirectBilleritems[i-1]=code;//itemDetail
		    		}
					i++;
			   
				 });
	} 
	
		function funCalculateTax()
		{		
			
			var searchurl=getContextPath()+"/funCalculateTax.html?arrKOTItemDtlList="+arrKOTItemDtlList;
			 $.ajax({
				        type: "POST",
				        url: searchurl,
				        dataType: "json",
				        success: function(response)
				        {	
				        	$("#txtTotal").val(response);
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

	});
	
	
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
// 		    table.rows[selectedRowIndex].cells[1].innerHTML = "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"  name=\"listOfMakeKOTBillItemDtl["+(selectedRowIndex)+"].dblQuantity\" id=\"dblQuantity."+(selectedRowIndex)+"\" value='"+qty+"' onclick=\"funChangeQty(this)\"/>";
		   if(qty!=null)
		  {
		    document.getElementById("quantity."+(selectedRowIndex)).value=qty;
		  
			  var itemAmt=qty*rate;
			  document.getElementById("amount."+(selectedRowIndex)).value=itemAmt;
			  funFillKOTList();
			  funCalculateTax();
		  }
		
		}else{
			alert("Please Select Item ");
		}
		  
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
		
			colmn.innerHTML = "<td><input type=\"button\" id=\"M99\" value='Free Flow Modofier' style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funFreeFlowModifierClicked(this,"+index+",'"+itemCode+"')\" class=\"btn btn-primary\" /></td>";
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
					col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value='-->"+name[1]+"' style=\"width: 100px;height: 100px; white-space:normal;\"  onclick=\"funModifierClicked(this,"+index+",'"+itemCode+"')\" class=\"btn btn-primary\" /></td>";
					
					
					insertCol++;
				}
				else
				{		rowCount++;	 		
					insertTR=tblMenuItemDtl.insertRow();									
					insertCol=0;
					index=rowCount*4+insertCol;				
					var col=insertTR.insertCell(insertCol);
					//col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value="+obj.strModifierName+"    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funMenuItemClicked(this,"+index+")\" /></td>";
					
					col.innerHTML = "<td><input type=\"button\" id="+obj.strModifierCode+" value='-->"+name[1]+"'    style=\"width: 100px;height: 100px; white-space: normal;\"  onclick=\"funModifierClicked(this,"+index+",'"+itemCode+"')\" class=\"btn btn-primary\" /></td>";
					
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
					col.innerHTML = "<td style=\"padding:  5px;\"><input type=\"button\" id="+obj.strModifierGroupCode+" value="+obj.strModifierGroupShortName+"    style=\"width: 90px;height: 30px; white-space: normal;\"  onclick=\"funTopButtonClicked(this)\" /></td>";
					insertCol++; 
		});
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
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
		    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
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
	  
	  
		function funMoveRowDown(itemName,qty,amount,itemDiscPer,itemDiscAmt,strGroupCode,strSubGroupCode,strGroupName,strSubGroupName,itemCode,itemName1,qty1,amount1,itemDiscPer1,itemDiscAmt1,strGroupCode1,strSubGroupCode1,strGroupName1,strSubGroupName1,itemCode1,rowCount)
		{


			var tblBillItemDtl = document.getElementById("tblBillItemDtl");
			tblBillItemDtl.deleteRow(rowCount);
		    var row = tblBillItemDtl.insertRow(rowCount+1);
		    row = tblBillItemDtl.rows[rowCount+1];
		    
	
		    row.insertCell(0).innerHTML= "<input readonly=\"readonly\" size=\"32px\"  class=\"itemName\"    style=\"text-align: left; color:blue;\"    id=\"itemName."+(rowCount)+"\" value='"+itemName+"' onclick=\"funGetSelectedRowIndex(this)\"/>";
		    row.insertCell(1).innerHTML= "<input readonly=\"readonly\" size=\"3.5px\"   class=\"itemQty\"      style=\"text-align: right; color:blue;\"   id=\"quantity."+(rowCount)+"\" value='"+parseFloat(qty)+"' onclick=\"funChangeQty(this)\"/>";
		    row.insertCell(2).innerHTML= "<input readonly=\"readonly\" size=\"4px\"   class=\"itemAmt\"      style=\"text-align: right; color:blue;\"   id=\"amount."+(rowCount)+"\" value='"+amount+"'/>";
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
<body onload="funAddPopularItemsData()">

	<div id="formHeading">
	<label>Call Center</label>
	</div>

<br/>
<br/>

	<s:form name="frmCallCenter" method="POST" commandName="command" action="actionCallCenter.html?saddr=${urlHits}" >			
			
			<div id="divMain" style=" margin-left: 50px; " >				
				<table  >
					<tr>
						<td>
							<label id="lblCustomerName" style=" display: inline-block;width: 175px;text-align: left;">Customer: ${Customer}</label>
							<label id="lblAreaName" style=" display: inline-block;width: 130px;text-align: left;">Area: ${selectedBuildingName}</label>							
						</td>
						<td><input type="button"  id="Customer" value="Customer"    style="width: 150px;height: 30px; white-space: normal;"  class="btn btn-primary" onclick="funFooterButtonClicked(this)"/>																						
					
							<s:select id="txtPosCode" path="strPosCode" items="${mapPOSName}"  cssClass="BoxW124px" style="width: 247px;" required="true" />
						</td>	
					</tr>					
					<tr>
						<td>
							<!-- <div id="divBillItemDtl" style="background-color: #a4d7ff; border: 1px solid #ccc; display: block; height: 500px;  overflow-x: scroll; overflow-y: scroll; width: 30%;"> -->
							<div id="divBillItemDtl" style=" border: 1px solid #ccc; height: 450px;  overflow-x: auto; overflow-y: auto; width: 332px;">
								
								<table id="tblBillItemDtl"  cellpadding="0" cellspacing="0" ><!-- class="transTablex" -->
								<thead>
									<tr>
										  <th><input type="button" value="Description" style="width: 220px;" class="tblBillItemDtlColBtnGrp btn btn-primary" ></input></th>
										  <th><input type="button" value="Qty" style="width: 47px;" class="tblBillItemDtlColBtnGrp btn btn-primary" ></input></th>
										  <th><input type="button" value="Amount" style="width: 66px;" class="tblBillItemDtlColBtnGrp btn btn-primary"></input></th>
										  <th><input type="button" value="Item Code" class="tblBillItemDtlColBtnGrp btn btn-primary"></input></th>
										  <th><input type="button" value="Sequence No" class="tblBillItemDtlColBtnGrp btn btn-primary"></input></th>
									</tr>
									<thead>		
									<tbody>
									</tbody>															
								</table>
		
							</div>																						
								<div id="divTotalDtl" style=" border: 1px solid rgb(204, 204, 204);height: 70px;width: 100%;display: block;">									
									<table style="border-collapse: separate;">
										<tr>
											<td style="padding-right:  3px; padding-left:  3px;"><input type="button" id="chgQty" value="CHG QTY" style="width: 63px;height: 30px; white-space: normal; " onclick="funChgQtyBtnClicked()" class="btn btn-primary" /></td>
											<td style="padding-right:  3px; padding-left:  3px;" ><input type="button" id="delete" value="Delete" style="width: 80px;height: 30px; " onclick="funDeleteBtnClicked()" class="btn btn-primary" /></td>
											<td style="padding-right:  3px; padding-left:  3px;">&nbsp;&nbsp;&nbsp;<label >TOTAL</label></td>
											<td>&nbsp;&nbsp;&nbsp;</td>
											<td style="padding-right:  3px; padding-left:  3px;"><s:input  type="text"  id="txtTotal" path="" cssStyle="width:70px;" cssClass="longTextBox jQKeyboard form-control"  class="btn btn-primary" /></td>
										</tr>
										<tr>	
											<td style="padding-right:  3px; padding-left:  3px;" ><input  type="button" id="btnUp"  style="width: 60px;height: 30px;" value="Up" onclick="funMoveSelectedRow(1);" class="btn btn-primary" ></input></td>
					        				<td style="padding-right:  3px; padding-left:  3px;" ><input id="btnDown" type="button" style="width: 60px;height: 30px;" value="Down" onclick="funMoveSelectedRow(0);" class="btn btn-primary"></input></td>
										</tr>
										<tr></tr>																		
								</table>
							</div>
								
						</td>
					
						<td>
							<div id="divTopButtonDtl" style=" border: 1px solid #ccc; height: 50px;  overflow-x: auto; overflow-y: auto; width: 445px;">									
								
									<table id="tblTopButtonDtl"   cellpadding="0" cellspacing="2">
									</table>
									</div>
						<div id="divItemDtl" style=" border: 1px solid #ccc; height: 425px;  overflow-x: auto; overflow-y: auto; width: 445px;">									
								
									<table id="tblMenuItemDtl"   cellpadding="0" cellspacing="5">
									 <c:set var="sizeOfMenuItems" value="${fn:length(command.jsonArrForPopularItems)}"></c:set>									   
									   <c:set var="itemCounter" value="${0}"></c:set>									   									   					
										<%-- ${varMenuItemStatus.getIndex() ${varMenuItemStatus.count} ${sizeOfMenuItems} --%>																			   									  
									   <c:forEach var="objItemPriceDtl" items="${command.jsonArrForPopularItems}"  varStatus="varMenuItemStatus">																																		
												<tr>
												<%
													for(int x=0; x<4; x++)
													{
												%>														
														<c:if test="${itemCounter lt sizeOfMenuItems}">	
																
																	
																<td><input type="button" id="${command.jsonArrForPopularItems[itemCounter].strItemCode}"  value="${command.jsonArrForPopularItems[itemCounter].strItemName}" style=" width: 100px; height: 100px;" class="btn btn-primary"  onclick="funMenuItemClicked(this,${itemCounter})"  /></td>																				
															
																
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
								
								<div style="width: 445px; height: 25px;">
								<table><tr><td><label style="display:none;" id="lblDpName">Delivery Boy : </label></td><td><label id="dpName"></label></td></tr></table>
								</div> 	
								
								
						</td>
						<td>								
								<div id="divMenuHeadDtl" style=" border: 1px solid #ccc; height: 500px;  overflow-x: auto; overflow-y: auto; width: 130px;">									
									<table id="tblMenuHeadDtl"  cellpadding="0" cellspacing="5"  > <!-- class="table table-striped table-bordered table-hover" -->									
									 
									 <tr>
									 <td style="padding: 3px;"><input type="button" id="PopularItem" value="POPULAR ITEM"  style="width: 100px;height: 50px; white-space: normal;"  onclick="funPopularItemButtonClicked(this)"  class="btn btn-primary" /></td>
									 </tr>
									  <c:forEach var="objMenuHeadDtl" items="${command.jsonArrForDirectBillerMenuHeads}"  varStatus="varMenuHeadStatus">																																		
												<tr>																							 													
													<td style="padding: 3px;"><input type="button"  id="${objMenuHeadDtl.strMenuCode}" value="${objMenuHeadDtl.strMenuName}"    style="width: 100px;height: 50px; white-space: normal;"   onclick="funMenuHeadButtonClicked(this)"  class="btn btn-primary" /></td>														
											   </tr>																																
										</c:forEach>
																			   				   									   									   							
									</table>
								</div>
						</td>
					</tr>
				</table>
		
				<div style="text-align: right;">
				<!-- <div id="divBottomButtonsNavigator" style="border: 1px solid #ccc; height: 40px;  overflow-x: auto; overflow-y:; width: 615px; "> -->
				 	<table id="tblFooterButtons"  cellpadding="0" cellspacing="2"  > <!-- class="table table-striped table-bordered table-hover" -->				 																																	
							<tr>							
								<c:forEach var="objFooterButtons" items="${command.jsonArrForDirectBillerFooterButtons}"  varStatus="varFooterButtons">								
										<td><input  type="button" id="${objFooterButtons}"  value="${objFooterButtons}" tabindex="${varFooterButtons.getIndex()}"  style="width: 100px;height: 50px; white-space: normal;"   onclick="funFooterButtonClicked(this)"  class="btn btn-primary" /></td>																									   																															
								</c:forEach>	
								<td><input type="button"  id="PLU" value="PLU"    style="width: 100px;height: 50px; white-space: normal;"   onclick="funFooterButtonClicked(this)"  class="btn btn-primary" /></td>																					
						    </tr>																																				 									   				   									   									   						
					</table>			
		 		<!-- </div> -->	
		 		<s:hidden id="txtCustMobileNo" path="strCustMobileNo"/>
		 		<s:hidden id="txtCustomerCode" path="strCustomerCode"/>
		 		<s:hidden id="txtCustomerName" path="strCustomerName"/>
		 		<s:hidden id="billTransType" path="billTransType"/>
		 		<s:hidden id="txtTakeAway" path="takeAway"/>
		 		<s:hidden id="hidDeliveryBoyCode" path="strDeliveryBoyCode"/>
		 		<s:hidden id="hidDeliveryBoyName" path="strDeliveryBoyName"/>
		 		<s:hidden id="hidHomeDeliveryAddress" path="strHomeDeliveryAddress"/>
		 		
		 		
		 		
		 		<%--<s:hidden id="txtCondition" path="strCondition"/> --%>	 				 																						
			</div>
			</div>
		
	</s:form>
</body>

</html>
