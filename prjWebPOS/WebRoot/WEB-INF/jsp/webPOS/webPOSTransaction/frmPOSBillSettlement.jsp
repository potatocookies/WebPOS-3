<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
.test
{    border-right: 0px solid black;
}
</style>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<title>BILL SETTLEMENT</title>
<script type="text/javascript">


		var operationFrom="${operationFrom}";
		var balanceAmount=0,paidAmount=0.00,refundAmount=0,loyalityPoints=0;
		var settleType = "",debitCardNo = "";
		var takeAwayRemarks = "";
		var custAddType = "Home";
		var homeDelivery = "N";
		var tableNo = "${TableNo}" ,voucherNo="";
		var gCRMInterface="${gCRMInterface}",gSearchItemClicked=false;
		var cmsMemberCreditLimit,cmsMemberBalance;
		var cmsStopCredit;
		var discountRemarks = "";
		var couponCode = "";
		var flgApplyPromoOnBill="",flgGiftVoucherOK=false,flgEnterBtnPressed=false;
		var popUpOnPromoBill="${gPopUpToApplyPromotionsOnBill}";
		var settleName="",settlementCode="",billPrintOnSettlement="",settelmentDesc="";
		var dblSettlementAmount=0,currencyRate;
		var settlementName="",customerCodeForCredit="";
		var amountBox="";
		var giftVoucherSeriesCode="", giftVoucherCode="";
		var settleMode="",multSettle="";
		var hmSettlemetnOptions=new Map();
		var arrObjReasonCode ="";//${listReasonCode};
		var arrObjReasonName ="";//${listReasonName};
       var checkSetteType="";
       var rowCountSettle=0;
		
		var disCount=0;
		 var totalDiscAmt=0;
		 
		 var disountType="Total";
		
		$(document).ready(function(){
			settleName="Cash";
			
// 			 if(operationFrom=="directBiller")
// 			 {
				 funSetProperty();
				 funVisibleDeliveryCharges('block');
				 funSetDate();
				 document.getElementById("lblTip").style.display='none';
				 document.getElementById("txtTip").style.display='none';	 
				
				 //${command.jsonArrForSettleButtons}
				/*  if (!objListSettle.getStrSettelmentType().equals("Debit Card"))
		            {
		                procSettlementBtnClick(objListSettle);
		               
		                funSettleOptionSelected('${objSettleButtons.strSettelmentDesc}','${objSettleButtons.strSettelmentType}',
								'${objSettleButtons.strSettelmentCode}','${objSettleButtons.dblConvertionRatio}','${objSettleButtons.strBillPrintOnSettlement}')"
		                funSettleOptionSelected(settleNametmp,settleTypetmp,settlementCodetmp,currencyRatetmp,billPrintOnSettlementtmp)
		            } */
//  			 }
			
		});
		
		function loadofBillSettlemntTab()
		{
			 funSetProperty();
			 funVisibleDeliveryCharges('block');
			 funSetDate();
			 document.getElementById("lblTip").style.display='none';
			 document.getElementById("txtTip").style.display='none';
			
		}
		
		function funPrintButtonClicked(printButton)
		{					    		     
		    $("form").submit();		      	
		}
		
		
		function funSettleButtonClicked(printButton)
		{					    		     
		    $("form").submit();		      	
		}
		
	function funSetProperty()
	{
		  settlementName="other";
		  cmsStopCredit="N";
		  cmsMemberCreditLimit = 0;
		  cmsMemberBalance=0;
		 if (gCRMInterface=="PMAM"){
			 document.getElementById("btnGetOffer").style.display='block';
         }
         else{
			 document.getElementById("btnGetOffer").style.display='none';

         }
		 funSetDefaultRadioBtnForDiscount();
		 
		 document.getElementById("divCustomer").style.display='none';
		 document.getElementById("divRemarks").style.display='none';
		 document.all["divCard"].style.display='none';
		 document.all["divPaymentMode"].style.display = 'none';
		 document.getElementById("divScrTaxTable").style.display='none';
	
         document.getElementById("divTblSettlement").style.display='none';
         document.getElementById("divCheque").style.display='none';
         //document.getElementById("divAmt").style.display='block';
         document.getElementById("divCoupen").style.display='none';
         document.getElementById("divGiftVoucher").style.display='none';
         document.getElementById("divRoomSettlement").style.display='none';
         document.getElementById("divJioMoneySettlement").style.display='none';
        
        
//          document.getElementById("txtPaidAmount").focus();
        // txtPaidAmt.setFocusable(true);
         flgGiftVoucherOK=false;
        
	}
	function funNumpadButtonClicked(ObjNumPadBtn)
	{
		//alert(ObjNumPadBtn.value);
		var newValue="";
		if(ObjNumPadBtn.value=="BackSpace")
		{
			var oldValue=$('#txtPaidAmount').val();
			var len=oldValue.length;
			newValue=oldValue.substring(0,len-1);
			$("#txtPaidAmount").val(newValue);
		}
		else if(ObjNumPadBtn.value=="Enter")
		{
			
		}
		else{
			var oldValue=$('#txtPaidAmount').val();
			newValue=oldValue+""+ObjNumPadBtn.value;
			$("#txtPaidAmount").val(newValue);
		}
	}

	function funSettleOptionSelected(settleNametmp,settleTypetmp,settlementCodetmp,currencyRatetmp,billPrintOnSettlementtmp)
	{
			settleName=settleNametmp;
			settelmentDesc=settleName;
			settleType=settleTypetmp;
			settlementCode=settlementCodetmp;
			currencyRate=currencyRatetmp;
			billPrintOnSettlement=billPrintOnSettlementtmp;
			document.all["divPaymentMode"].style.display = 'block';
			document.getElementById("lblPayMentMode").innerText="PayMent Mode : "+settleType;
		   	dblSettlementAmount = 0.00;
		   	balanceAmount=$("#txtAmount").val();
           //settlementCode//use while calculating tax for settlement
            dblSettlementAmount = Math.round(balanceAmount);
            dblSettlementAmount = dblSettlementAmount * currencyRate;
            dblSettlementAmount = Math.round(dblSettlementAmount);
           
        switch (settleType)
        {
            case "Loyality Points":
	                settlementName = "others";
	                amountBox = "PaidAmount";
	                settleMode = true;
	                document.getElementById("divRemarks").style.display='none';
	                document.getElementById("divCustomer").style.display='none';
	               // document.getElementById("lblcard").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.getElementById("lblCardBalance").style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	              
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	              
	                break;
	                
	            case "Cash":
	                settlementName = "others";
	                amountBox = "PaidAmount";
	                settleMode = true;
	               
	                document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	              //  document.getElementById("lblCardBalance").style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	            
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                
	                document.getElementById("divRemarks").style.display='block';
	               // PanelRemaks.setLocation(PanelCheque.getLocation());//document.all["lblCard"].style.display='none';
	                document.getElementById("divRemarks").style.top='345px';
	                document.getElementById("divRemarks").style.left='415px';
	                $("#txtRemark").focus();
	                //document.getElementById("txtRemark").focus();
	               
	              //flgComplementarySettle = false;
	                break;
	
	            case "Credit Card":
	                settlementName = "others";
	                amountBox = "PaidAmount";
	                settleMode = true;
	                document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='block';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='block';
	                document.getElementById("txtTip").style.display='block';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='block';
	                document.getElementById("divCard").style.top='350px';
	                document.getElementById("divCard").style.left='415px';
	                $("#txtRemark").focus();
	                document.getElementById("divRemarks").style.top=' 425px';
	                document.getElementById("divRemarks").style.left='415px';
	              break;
	
	            case "Coupon":
	                settlementName = "others";
	                amountBox = "CouponAmount";
	                settleMode = true;
	                document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='none';
	                document.getElementById("divCoupen").style.display='true';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='none';
	                document.getElementById("divCoupen").style.top='145px';
	                document.getElementById("divCoupen").style.left='415px';
	                break;
	
	            case "Cheque":
	                settlementName = "others";
	                amountBox = "PaidAmount";
	                settleMode = true;
	                document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='block';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='none';
	                document.getElementById("divCoupen").style.top='145px';
	                document.getElementById("divCoupen").style.left='415px';
	              
	                break;
	
	            case "Gift Voucher":
	                settlementName = "Gift voucher";
	                amountBox = "PaidAmount";
	                settleMode = true;
	                
	                document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='block';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='block';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='none';
	                document.getElementById("divGiftVoucher").style.top='145px';
	                document.getElementById("divGiftVoucher").style.left='415px';
	                break;
	                

                case "Complementary":
                	settlementName = "Complementry";
                    amountBox = "PaidAmount";
                    settleMode = true;
                    $("#txtRemarks").focus();
                    document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='none';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='block';
	                document.getElementById("divRemarks").style.top='225px';
	                document.getElementById("divRemarks").style.left='415px';
                   break;

                case "Credit":
                	//funHelp('POSCustomerMaster');
                	settlementName = "Credit";
                    amountBox = "PaidAmount";
                   // objUtility.funCallForSearchForm("CustomerMaster");
                   // new frmSearchFormDialog(this, true).setVisible(true);
                    //funOpenCustomerMaster();

                     if (gSearchItemClicked)
                    {
                       // Object[] data = clsGlobalVarClass.gArrListSearchData.toArray();
                    
                       /*  lblCreditCustCode.setText(data[0].toString());
                        customerCodeForCredit = lblCreditCustCode.getText();
                        txtCoupenAmt.setText(String.valueOf(dblSettlementAmount));
                        hidCustomerName.setText(data[1].toString()); */
                        gSearchItemClicked = false;
                    } 
                    
                    document.getElementById("divCustomer").style.display='block';
                    document.getElementById("divCustomer").style.top='205px';
	                document.getElementById("divCustomer").style.left='415px';
	               
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='none';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='block';
	                document.getElementById("divRemarks").style.top='245px';
	                document.getElementById("divRemarks").style.left='415px';
	                $("#txtRemark").focus();
                    break;
	                
                    
                 case "Debit Card":
                	document.getElementById("divRoomSettlement").style.display='none';
 	                document.getElementById("divJioMoneySettlement").style.display='none';
 	                document.getElementById("divGiftVoucher").style.display='none';
 	                document.getElementById("divRemarks").style.display='none';
    	            document.getElementById("divAmt").style.display='none';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
                   /*
                    if (tableNo!="")  // For KOT
                    {
                        debitCardNo = funCardNo();
                        if (debitCardNo=="")
                        {
                           // new frmSwipCardPopUp(this).setVisible(true);
                            if (debitCardNo != null)
                            {
                                ResultSet rsDebitCardNo = clsGlobalVarClass.dbMysql.executeResultSet("select strCardNo from tbldebitcardmaster where strCardString='" + debitCardNo + "'");
                                if (rsDebitCardNo.next())
                                {
                                    debitCardNo = rsDebitCardNo.getString(1);
                                }
                                rsDebitCardNo.close();
                            }
                        }
                        if (!debitCardNo.isEmpty())
                        {
                            clsUtility objUtility = new clsUtility();
                            //double debitCardBalance = objUtility.funGetDebitCardBalance(debitCardNo);
                            double debitCardBalance = objUtility.funGetDebitCardBalanceWithoutLiveBills(clsGlobalVarClass.gDebitCardNo, tableNo);

                            if (objUtility.funGetDebitCardStatus(debitCardNo, "CardNo").equalsIgnoreCase("Card is Not Active"))
                            {
                                new frmOkPopUp(null, "This Card is not Activated ", "Warning", 1).setVisible(true);
                            }
                            else if (debitCardBalance < 0)
                            {
                                new frmOkPopUp(null, "Card Balance is Negative", "Warning", 1).setVisible(true);
                            }
                            else
                            {
                                lblcard.setVisible(true);
                                chkJioNotification.setSelected(false);
                                lblCardBalance.setVisible(true);
                                lblCardBalance.setText(String.valueOf(debitCardBalance));
                                amountBox = "PaidAmount";
                                settlementName = "others";
                                settleMode = true;
                                panelAmt.setVisible(true);
                                PanelGiftVoucher.setVisible(false);
                                PanelJioMoneySettlement.setVisible(false);
                                txtAmount.setText(String.valueOf(dblSettlementAmount));
                                if (dblSettlementAmount > debitCardBalance)
                                {
                                    txtPaidAmt.setText(String.valueOf(debitCardBalance));
                                    lblCardBalance.setBackground(Color.red);
                                }
                                else
                                {
                                    txtPaidAmt.setText(String.valueOf(dblSettlementAmount));
                                    lblCardBalance.setBackground(Color.yellow);
                                }
                            }
                        }
                    }
                    else // For Direct Biller
                    {
                        if (null == clsGlobalVarClass.gDebitCardNo || clsGlobalVarClass.gDebitCardNo.trim().isEmpty())
                        {
                            new frmSwipCardPopUp(this).setVisible(true);
                        }
                        if (clsGlobalVarClass.gDebitCardNo != null && !clsGlobalVarClass.gDebitCardNo.trim().isEmpty())
                        {
                            clsUtility objUtility = new clsUtility();
                            ResultSet rsDebitCardNo = clsGlobalVarClass.dbMysql.executeResultSet("select strCardNo from tbldebitcardmaster where strCardString='" + clsGlobalVarClass.gDebitCardNo + "'");
                            if (rsDebitCardNo.next())
                            {
                                debitCardNo = rsDebitCardNo.getString(1);
                                //double debitCardBalance = objUtility.funGetDebitCardBalance(debitCardNo);
                                double debitCardBalance = objUtility.funGetDebitCardBalanceWithoutLiveBills(clsGlobalVarClass.gDebitCardNo, tableNo);

                                if (objUtility.funGetDebitCardStatus(debitCardNo, "CardNo").equalsIgnoreCase("Card is Not Active"))
                                {
                                    new frmOkPopUp(null, "This Card is not Activated ", "Warning", 1).setVisible(true);
                                }
                                else if (debitCardBalance < 0)
                                {
                                    new frmOkPopUp(null, "Card Balance is Negative", "Warning", 1).setVisible(true);
                                }
                                else
                                {
                                    lblcard.setVisible(true);
                                    lblCardBalance.setVisible(true);
                                    chkJioNotification.setSelected(false);
                                    lblCardBalance.setText(String.valueOf(debitCardBalance));
                                    amountBox = "PaidAmount";
                                    settlementName = "others";
                                    settleMode = true;
                                    panelAmt.setVisible(true);
                                    PanelGiftVoucher.setVisible(false);
                                    PanelJioMoneySettlement.setVisible(false);
                                    txtAmount.setText(String.valueOf(dblSettlementAmount));
                                    if (dblSettlementAmount > debitCardBalance)
                                    {
                                        txtPaidAmt.setText(String.valueOf(debitCardBalance));
                                        lblCardBalance.setBackground(Color.red);
                                    }
                                    else
                                    {
                                        txtPaidAmt.setText(String.valueOf(dblSettlementAmount));
                                        lblCardBalance.setBackground(Color.yellow);
                                    }
                                }
                            }
                            rsDebitCardNo.close();
                        }
                        PanelCard.setLocation(PointCheque);
                    }
                    */
                    //flgComplementarySettle = false;
                    break; 
/* 
                case "Member":

                    if (clsGlobalVarClass.gCMSIntegrationYN)
                    {
                        cmsMemberCreditLimit = 0;
                        if (custCode.trim().length() == 0)
                        {
                            JOptionPane.showMessageDialog(this, "Member is Not Selected for This Bill!!!");
                            return 0;
                        }
                        else
                        {
                            if (clsGlobalVarClass.gCMSIntegrationYN)
                            {
                                //String memberInfo=funCheckMemeberBalance(custCode);
                                clsUtility objUtility = new clsUtility();
                                String memberInfo = objUtility.funCheckMemeberBalance(custCode);
                                if (memberInfo.equals("no data"))
                                {
                                    JOptionPane.showMessageDialog(null, "Member Not Found!!!");
                                }
                                else
                                {
                                    String[] spMemberInfo = memberInfo.split("#");
                                    double balance = Double.parseDouble(spMemberInfo[2]);
                                    cmsMemberBalance = Double.parseDouble(spMemberInfo[3]);
                                    String info = spMemberInfo[0] + "#" + spMemberInfo[1] + "#" + balance;
                                    txtAreaRemark.setText(info);
                                    cmsMemberCreditLimit = Double.parseDouble(spMemberInfo[3]);
                                    cmsStopCredit = spMemberInfo[6];
                                }
                            }
                        }
                    }
                    settlementName = "Member";
                    amountBox = "PaidAmount";
                    settleMode = true;
                    PanelRemaks.setLocation(PanelCheque.getLocation());
                    PanelRemaks.setVisible(true);
                    panelCustomer.setVisible(false);
                    txtRemark.requestFocus();
                    lblcard.setVisible(false);
                    lblCardBalance.setVisible(false);
                    panelAmt.setVisible(true);
                    PanelCoupen.setVisible(false);
                    PanelGiftVoucher.setVisible(false);
                    PanelCard.setVisible(false);
                    chkJioNotification.setSelected(false);
                    PanelCheque.setVisible(false);
                    txtPaidAmt.setText(String.valueOf(dblSettlementAmount));
                    txtAmount.setText(String.valueOf(dblSettlementAmount));
                    //flgComplementarySettle = false;
                    lblTipAmount.setVisible(false);
                    txtTip.setVisible(false);
                    panelRoomSettlement.setVisible(false);
                    PanelJioMoneySettlement.setVisible(false);
                    txtJioDestination.setVisible(false);
                    lblJioDestination.setVisible(false);
                    lblJioDestination.setText("");
                    break; */

             /*    case "Room":

                    clsInvokeDataFromSanguineERPModules objSangERP = new clsInvokeDataFromSanguineERPModules();
                    List<clsGuestRoomDtl> listOfGuestRoomDtl = objSangERP.funGetGuestRoomDtl();
                    new frmSearchFormDialog("Guest Room Detail", listOfGuestRoomDtl, this, true).setVisible(true);
                    objSangERP = null;
                    //funOpenCustomerMaster();

                    if (clsGlobalVarClass.gSearchItemClicked)
                    {
                        Object[] data = clsGlobalVarClass.gArrListSearchData.toArray();
                        txtGuestName.setText(data[0].toString());//guestName
                        txtRoomNo.setText(data[2].toString());//roomNo                        
                        txtFolioNo.setText(data[3].toString());//folioNo                                                
                        txtGuestCode.setText(data[4].toString());//guestCode                                                
                        clsGlobalVarClass.gSearchItemClicked = false;
                    }

                    PanelCoupen.setVisible(false);
                    panelCustomer.setVisible(false);
                    PanelRemaks.setVisible(false);
                    lblcard.setVisible(false);
                    lblCardBalance.setVisible(false);
                    panelAmt.setVisible(false);
                    PanelGiftVoucher.setVisible(false);
                    PanelCard.setVisible(false);
                    PanelCheque.setVisible(false);
                    settlementName = "Room";
                    amountBox = "PaidAmount";
                    settleMode = true;
                    chkJioNotification.setSelected(false);
                    lblTipAmount.setVisible(false);
                    txtTip.setVisible(false);
                    lblCreditCustCode.setVisible(false);
                    PanelJioMoneySettlement.setVisible(false);
                    panelRoomSettlement.setLocation(panelDiscount.getLocation());
                    panelRoomSettlement.setVisible(true);
                    txtJioDestination.setVisible(false);
                    lblJioDestination.setVisible(false);
                    lblJioDestination.setText("");

                    break; */

                case "JioMoney":

                	
                	 settlementName = "JioMoney";
                     amountBox = "PaidAmount";
                     settleMode = true;
                    $("#txtRemark").focus();
                   
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	              
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("divRoomSettlement").style.display='none';
                    document.getElementById("btnCheckStatus").style.display='none';
                    document.getElementById("divJioMoneySettlement").style.display='block';
                    if (settleName.equals("JM Code"))
                    {
                    	//$("#lblJioMoneyCode").val("Scan/Enter Code");
                    	document.getElementById("lblJioMoneyCode").innerText="Scan/Enter Code";
                    	$("#lblJioDestination").val("");
                    	$("#txtJioDestination").val("");
                    	
                    	document.getElementById("lblJioMoneySendNotific").style.display='none';
    	                document.getElementById("chkJioNotific").style.display='none';
    	                document.getElementById("txtJioDestination").style.display='none';
                    }
                    else
                    {
                    	document.getElementById("lblJioMoneyCode").innerText="SMS/Email";
                    	document.getElementById("lblJioMoneyCode").style.display='block';
                    	document.getElementById("txtJioDestination").style.display='block';
                    	$("#txtJioDestination").val("");
                    	$("#chkJioNotific").attr('checked', true);
                       
                    }
                    document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divRemarks").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                document.getElementById("divCustomer").style.display='none';
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	               
                    document.getElementById("divDiscount").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.top='150px';
	                document.getElementById("divJioMoneySettlement").style.left='740px';
	                       

                    break;
                
                case "Online Payment":
                    settlementName = "Online Payment";
                    amountBox = "PaidAmount";
                    settleMode = true;
                    $("#txtRemark").focus();
                    document.getElementById("divCustomer").style.display='none';
	                document.all["lblCard"].style.display='none';
	                document.all["lblCardBalance"].style.display='none';
	                document.getElementById("divAmt").style.display='block';
	                document.getElementById("divCoupen").style.display='none';
	                document.getElementById("divGiftVoucher").style.display='none';
	                document.getElementById("divCard").style.display='none';
	                document.getElementById("divCheque").style.display='none';
	                $("#txtPaidAmount").val(dblSettlementAmount);
	                $("#txtAmount").val(dblSettlementAmount);
	                //flgComplementarySettle = false;
	                document.getElementById("lblTip").style.display='none';
	                document.getElementById("txtTip").style.display='none';
	                document.getElementById("divRoomSettlement").style.display='none';
	                document.getElementById("divJioMoneySettlement").style.display='none';
	                document.getElementById("divRemarks").style.display='block';
	                document.getElementById("divRemarks").style.top='145px';
	                document.getElementById("divRemarks").style.left='415px';
                  
                    break;
                
        }
        
        $("#hidSettlemnetType").val(settleType);
	}
	function funHelp(transactionName)
	{	      
		fieldName=transactionName;
       window.open("searchform.html?formname="+transactionName+"&searchText=","","dialogHeight:600px;dialogWidth:600px;dialogLeft:400px;")
    }

	function funSetData(code)
	{
		
		  
		switch(fieldName)
		{
			case "POSCustomerMaster":
				
				 if(operationFrom=="directBiller" || operationFrom=="MakeKOT" )
				 {
					 funSetCustomerDataForHD(code);
				 }
				 else
				 {
					 funGetCustomerData(code);
						
			  		    $("#lblCreditCustCode").val(code);
					    customerCodeForCredit = code;
					    $("#txtCoupenAmt").val(dblSettlementAmount);
					 
				 }
						
			break;
			
			
		case "NewCustomer":
			funSetCustomerDataForHD(code);
			break;
			
			
		case "POSDeliveryBoyMaster":
			funSetDeliveryBoy(code);
			break;
			
			
		case "POSTableMaster":
			funTxtTableSearchClicked(code);
			break;
			
			
		case "POSWaiterMaster":
			funTxtWaiterClicked(code);
			break;
			
			
// 		case "POSCustomerMaster":
// 			funSetCustomerDataForHD(code);
// 			break;
// 		case "NewCustomer":
// 			funSetCustomerDataForHD(code);
// 			break;
// 		case "POSDeliveryBoyMaster":
// 			funSetDeliveryBoy(code);
// 			break;
			
		};
    }
	function funGetCustomerData(code)
	{
	 code=code.trim();
		var searchurl=getContextPath()+"/loadPOSCustomerMasterData.html?POSCustomerCode="+code;
		 $.ajax({
			        type: "GET",
			        url: searchurl,
			        dataType: "json",
			        success: function(response)
			        {
			        	$("#hidCustomerName").val(response.strCustomerName);
			        	//gCustomerCode=response.strCustomerCode;
			        	$("#lblCreditCustCode").val(code);
			        	//$("#hidCustomerName").val(response.strCustomerName);
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

	function funSetDefaultRadioBtnForDiscount()
    {
		$('input#rdbAll').prop('checked', true);
	    document.all["cmbItemCategary"].style.display = 'none';
    }
	 function funVisibleDeliveryCharges(flag)
    {
		 document.all["lblDelCharge"].style.display = flag;
		 document.all["txtDeliveryCharge"].style.display = flag;
		 document.all["btnApplyDeliveryCharge"].style.display = flag;
		 document.all["lblDelBoyname"].style.display = flag;
		 $("#txtDeliveryCharge").val('0.00');
       /*  lblDelBoyName.setVisible(flag);
        txtDeliveryCharges.setText("0.00"); */
    }
	
	 function funSetDate()
	 {
		 $("#dteCheque" ).datepicker('setDate', 'today');
		 $("#dteExpiry" ).datepicker('setDate', 'today');
		 
	 }

	 function funEnterButtonClicked()
	 {
		 
		
		 
		 
		 if ($("#txtPaidAmount").val().length==0)
	        {
	            paidAmount = 0.00;
	        }
	        else
	        {
	            paidAmount =parseFloat($("#txtPaidAmount").val());
	        }
	        if (paidAmount == 0.00 && finalGrandTotal != 0.00)
	        {
	        	alert("Please Enter Amount");
	        	
	        	return;
	        }
	        
	        
	        var txtPaidAmount=parseFloat($("#txtPaidAmount").val());
	        if(txtPaidAmount==0)
	        {
	        	alert("Bill Amount is Zero");
	        	
	        	return;
	        }
	        
	        if (settleType=="Debit Card")
	        {

	                var cardBal =parseFloat($("#lblCardBalance").val());
	                if (cardBal < paidAmount)
	                {
	                	alert("Insufficient Amount in Card");
	                	
	                	return;
	                }

	        }
	        
	        
	        if(settleType=="")
	        {
	        	alert("Please Select Settelment Mode");
	        	return;
	        }
	      

	        	if(settleType!="")
	        	{
		            switch (settleType)
		            {
		                 case "Cash":
		                	if (hmSettlemetnOptions.has(settleName))
		                    {
		                		var arrSettleOptions=[];
		                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
		                        var tempPaidAmt = parseFloat(arrSettleOptions[3]);
		                        tempPaidAmt = tempPaidAmt + paidAmount;
		                        arrSettleOptions[3]=tempPaidAmt;
		                        arrSettleOptions[5]=refundAmount;
		                     
		                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                    }
		                    else
		                    {
		                    	var arrSettleOptions=[];
		                    	 arrSettleOptions.push(settleName);
		                    	 arrSettleOptions.push(settlementCode);
		                    	 arrSettleOptions.push(dblSettlementAmount);
		                    	 arrSettleOptions.push(paidAmount);
		                    	 arrSettleOptions.push(finalGrandTotal);
		                    	 arrSettleOptions.push(refundAmount);
		                    	 arrSettleOptions.push(settelmentDesc);
		                    	 arrSettleOptions.push(settleType);
		                    	 arrSettleOptions.push("");
		                    	 arrSettleOptions.push("");
		                    	 arrSettleOptions.push("");
		                    	 arrSettleOptions.push("");
		                		
		                		 
		                        hmSettlemetnOptions.set(settleName,arrSettleOptions);   // settlementCode, dblSettlementAmount, paidAmount, "", settleName, "", "", finalGrandTotal, refundAmount, "", getStrSettelmentDesc(), getStrSettelmentType()));
		                      
		                    }
		                	break; 
		                	
		                 case "Credit Card":
		                     if (""==$("#txtPaidAmount").val())
		                     {
		                    	 alert("Please Enter Amount");
		                    	 return;
		                     }
		                     if ($("#txtPaidAmount").val() < 0)
		                     {
		                    	 alert("Invalid paid amount");
		                    	 return;
		                 	 }
		                     if ("${gCreditCardSlipNo}"=="Y")
		                     {
		                         if ($("#txtCardName").val().length <= 0)
		                         {
		                        	 prompt("Please Enter Slip No");
		                           	 $("#txtCardName").focus();
		                             return;
		                         }
		                     }
		                     var objCreditCardExpDate = $("#dteExpiry").val();
		                     var expiryDate = "";
		                     if ("${gCreditCardExpiryDate}"=="Y")
		                     {
		                         if (objCreditCardExpDate == null)
		                         {
		                            alert("Please Select Expiry Date");
		                        	return;
		                         }
		                         else
		                         {
		                             expiryDate = objCreditCardExpDate;
		                         }
		                     }
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    		var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        arrSettleOptions[9]=$("#txtCardName").val();
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                    	
		                     }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]=expiryDate;
		                		 arrSettleOptions[9]=$("#txtCardName").val();
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]="";
		                		 
		                         hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                    }
		                     break;
		                	
		                 case "Coupon":
	
		                     paidAmount =$("#txtCoupenAmt").val();
		                     if ($("#txtCoupenAmt").length <= 0)
		                     {
		                    	 alert("Please Enter Amount");
		                       //  new frmOkPopUp(null, "Please Enter Amount", "Warning", 1).setVisible(true);
		                         return;
		                     }
		                     if (paidAmount < 0)
		                     {
		                    	 alert("Invalid paid amount");
		                         //new frmOkPopUp(null, "Invalid paid amount", "Warning", 1).setVisible(true);
		                         return;
		                     }
		                     if ($("#txtCoupenAmt").val()=="")
		                     {
		                    	 alert("Please Enter Remark");
		                         //new frmOkPopUp(null, "Please Enter Remark", "Warning", 1).setVisible(true);
		                         return;
		                     }
		                     else
		                     {
		                         if (hmSettlemetnOptions.has(settleName))
		                         {
		                        	 	var arrSettleOptions=new Array();
				                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
				                        var tempPaidAmt = arrSettleOptions[3];
				                        tempPaidAmt = tempPaidAmt + paidAmount;
				                        arrSettleOptions[3]=tempPaidAmt;
				                        arrSettleOptions[5]=refundAmount;
				                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
				                 }
		                         else
		                         {
		                        	 var arrSettleOptions=new Array();
			                    	 arrSettleOptions[0]=settleName;
			                		 arrSettleOptions[1]=settlementCode;
			                		 arrSettleOptions[2]=dblSettlementAmount;
			                		 arrSettleOptions[3]=paidAmount;
			                		 arrSettleOptions[4]=finalGrandTotal;
			                		 arrSettleOptions[5]=refundAmount;
			                		 arrSettleOptions[6]=settelmentDesc;
			                		 arrSettleOptions[7]=settleType;
			                		 arrSettleOptions[8]="";
			                		 arrSettleOptions[9]="";
			                		 arrSettleOptions[10]="";
			                		 arrSettleOptions[11]="";
			                         hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                        	 
		                             //_balanceAmount = fun_get_BalanceAmount(_balanceAmount, _paidAmount, settleType);
		                         }
		                     }
		                     break;
		                     
		                 case "Cheque":
	
		                     break;
	
		                 case "Gift Voucher":
		                     if (!flgGiftVoucherOK)
		                     {
		                    	 alert("Press OK button on Gift Voucher");
		                         return;
		                     }
							  if (hmSettlemetnOptions.has(settleName))
		                     {
		                    	 	var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
			                 }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]="";
		                		 arrSettleOptions[9]="";
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]=giftVoucherSeriesCode+""+giftVoucherCode;
		                		 
		                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
		                     }
		                     break;
	
		                 case "Complementary":
	
		                     if (hmSettlemetnOptions.size > 0)
		                     {
		                    	 alert("Coplimentary Settlement is Not Allowed In MultiSettlement!!!");
		                         return;
		                     }
		                     if ($("#txtRemarks").val().length == 0)
		                     {
		                    	 alert("Please Enter Remarks");
		                         return;
		                     }
		                     /* if (arrObjReasonCode.length==0) 
		                     {
		                    	 alert("No complementary reasons are created");
		                         return;
		                     }
		                     else
		                     {
		                         var selectedReason= prompt("Please Select Reason?",arrObjReasonName);
		                        if (null == selectedReason)
		                         {
		                             alert("Please Select Reason");
		                             return;
		                         }
		                         else
		                         {
		                              for (var cntReason = 0; cntReason < arrObjReasonCode.length; cntReason++)
		                             {
		                                 if (arrObjReasonName[cntReason]==selectedReason)
		                                 {
		                                     selectedReasonCode = arrObjReasonName[cntReason];
		                                     break;
		                                 }
		                             } 
		                             refundAmount = 0.00;
		                             balanceAmount = 0.00;
		                             
		                             var arrSettleOptions=new Array();
			                    	 arrSettleOptions[0]=settleName;
			                		 arrSettleOptions[1]=settlementCode;
			                		 arrSettleOptions[2]=dblSettlementAmount;
			                		 arrSettleOptions[3]=paidAmount;
			                		 arrSettleOptions[4]=finalGrandTotal;
			                		 arrSettleOptions[5]=refundAmount;
			                		 arrSettleOptions[6]=settelmentDesc;
			                		 arrSettleOptions[7]=settleType;
			                		 arrSettleOptions[8]=expiryDate;
			                		 arrSettleOptions[9]="";
			                		 arrSettleOptions[10]=$("#txtRemark").val();
			                		 arrSettleOptions[11]="";
			                		 
			                         hmSettlemetnOptions.set(settleName,arrSettleOptions);
			                         
		                         } 
		                     } */
		                     
		                     
		                     refundAmount = 0.00;
                             balanceAmount = 0.00;
                             
                             var arrSettleOptions=new Array();
	                    	 arrSettleOptions[0]=settleName;
	                		 arrSettleOptions[1]=settlementCode;
	                		 arrSettleOptions[2]=dblSettlementAmount;
	                		 arrSettleOptions[3]=paidAmount;
	                		 arrSettleOptions[4]=finalGrandTotal;
	                		 arrSettleOptions[5]=refundAmount;
	                		 arrSettleOptions[6]=settelmentDesc;
	                		 arrSettleOptions[7]=settleType;
	                		 arrSettleOptions[8]=expiryDate;
	                		 arrSettleOptions[9]="";
	                		 arrSettleOptions[10]=$("#txtRemarks").val();
	                		 arrSettleOptions[11]="";
	                		 
	                         hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                     
		                     
		                     break;
		                     
		                 case "Credit":
	
		                     if (gCustomerCode=="")
		                     {
		                    	 alert("Please Select Customer!!!");
		                         return;
		                     }
	
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    		var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                     }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]="";
		                		 arrSettleOptions[9]="";
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]="";
		                		
		                		 hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                       }
		                     break;
	
		                 case "Debit Card":
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    	 	var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
			                  
		                     }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]="";
		                		 arrSettleOptions[9]="";
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]="";
		                		
		                		 hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                     }
		                     break;
		                 case "Loyality Points":
		                	 var flgResult=funCheckPointsAgainstCustome();
		                     if (flgResult)
		                     {
		                         if (hmSettlemetnOptions.has(settleName))
		                         {
		                        	 	var arrSettleOptions=new Array();
				                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
				                        var tempPaidAmt = arrSettleOptions[3];
				                        tempPaidAmt = tempPaidAmt + paidAmount;
				                        arrSettleOptions[3]=tempPaidAmt;
				                        arrSettleOptions[5]=refundAmount;
				                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                        
		                         }
		                         else
		                         {
		                        	 var arrSettleOptions=new Array();
			                    	 arrSettleOptions[0]=settleName;
			                		 arrSettleOptions[1]=settlementCode;
			                		 arrSettleOptions[2]=dblSettlementAmount;
			                		 arrSettleOptions[3]=paidAmount;
			                		 arrSettleOptions[4]=finalGrandTotal;
			                		 arrSettleOptions[5]=refundAmount;
			                		 arrSettleOptions[6]=settelmentDesc;
			                		 arrSettleOptions[7]=settleType;
			                		 arrSettleOptions[8]="";
			                		 arrSettleOptions[9]="";
			                		 arrSettleOptions[10]="";
			                		 arrSettleOptions[11]="";
			                		
			                		 hmSettlemetnOptions.set(settleName,arrSettleOptions);
		                         }
		                     }
		                     else
		                     {
		                         return;
		                     }
		                     break;
	
		                 case "Member":
		                     if (cmsStopCredit=="Y")
		                     {
		                    	 alert("Credit Facility Is Stopped For This Member!!!");
		                     }
		                     else if (cmsMemberCreditLimit > 0)
		                     {
		                         if (cmsMemberBalance < dblSettlementAmount)
		                         {
		                             alert("Credit Limit Exceeds, Balance Credit: " + cmsMemberBalance+"");
		                        	 return;
		                         }
		                         if (paidAmount <= cmsMemberBalance)
		                         {
		                             cmsMemberBalance = 0;
		                             if (hmSettlemetnOptions.has(settleName))
		                             {
		                            	 	var arrSettleOptions=new Array();
					                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
					                        var tempPaidAmt = arrSettleOptions[3];
					                        tempPaidAmt = tempPaidAmt + paidAmount;
					                        arrSettleOptions[3]=tempPaidAmt;
					                        arrSettleOptions[5]=refundAmount;
					                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                             }
		                             else
		                             {
		                            	 var arrSettleOptions=new Array();
				                    	 arrSettleOptions[0]=settleName;
				                		 arrSettleOptions[1]=settlementCode;
				                		 arrSettleOptions[2]=dblSettlementAmount;
				                		 arrSettleOptions[3]=paidAmount;
				                		 arrSettleOptions[4]=finalGrandTotal;
				                		 arrSettleOptions[5]=refundAmount;
				                		 arrSettleOptions[6]=settelmentDesc;
				                		 arrSettleOptions[7]=settleType;
				                		 arrSettleOptions[8]="";
				                		 arrSettleOptions[9]="";
				                		 arrSettleOptions[10]="";
				                		 arrSettleOptions[11]="";
				                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
				                	 }
		                         }
		                     }
		                     else
		                     {
		                         cmsMemberBalance = 0;
		                         if (hmSettlemetnOptions.has(settleName))
		                         {
		                        	 	var arrSettleOptions=new Array();
				                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
				                        var tempPaidAmt = arrSettleOptions[3];
				                        tempPaidAmt = tempPaidAmt + paidAmount;
				                        arrSettleOptions[3]=tempPaidAmt;
				                        arrSettleOptions[5]=refundAmount;
				                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
				                }
		                         else
		                         {
		                        	 
		                        	 var arrSettleOptions=new Array();
			                    	 arrSettleOptions[0]=settleName;
			                		 arrSettleOptions[1]=settlementCode;
			                		 arrSettleOptions[2]=dblSettlementAmount;
			                		 arrSettleOptions[3]=paidAmount;
			                		 arrSettleOptions[4]=finalGrandTotal;
			                		 arrSettleOptions[5]=refundAmount;
			                		 arrSettleOptions[6]=settelmentDesc;
			                		 arrSettleOptions[7]=settleType;
			                		 arrSettleOptions[8]="";
			                		 arrSettleOptions[9]="";
			                		 arrSettleOptions[10]="";
			                		 arrSettleOptions[11]="";
			                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
			                	 }
		                     }
		                     break;
		                     
		                 case "Room":
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    	 	var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
		                     }
		                     else
		                     {
		                    	 
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]=$("#txtFolioNo").val(); //assign for temprary
		                		 arrSettleOptions[9]=$("#txtRoomNo").val();
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]=$("#txtGuestCode").val();
		                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
		                	       
		                     }
		                     break;
	
	
		                 case "JioMoney":
	
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    	 	var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
			                 }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]="";
		                		 arrSettleOptions[9]="";
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]="";
		                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
		                   }
		                     break;
		                     
		                 case "Online Payment":
		                     if (hmSettlemetnOptions.has(settleName))
		                     {
		                    		var arrSettleOptions=new Array();
			                		arrSettleOptions = hmSettlemetnOptions.get(settleName);
			                        var tempPaidAmt = arrSettleOptions[3];
			                        tempPaidAmt = tempPaidAmt + paidAmount;
			                        arrSettleOptions[3]=tempPaidAmt;
			                        arrSettleOptions[5]=refundAmount;
			                        hmSettlemetnOptions.set(settleName, arrSettleOptions); 
			                 }
		                     else
		                     {
		                    	 var arrSettleOptions=new Array();
		                    	 arrSettleOptions[0]=settleName;
		                		 arrSettleOptions[1]=settlementCode;
		                		 arrSettleOptions[2]=dblSettlementAmount;
		                		 arrSettleOptions[3]=paidAmount;
		                		 arrSettleOptions[4]=finalGrandTotal;
		                		 arrSettleOptions[5]=refundAmount;
		                		 arrSettleOptions[6]=settelmentDesc;
		                		 arrSettleOptions[7]=settleType;
		                		 arrSettleOptions[8]="";
		                		 arrSettleOptions[9]="";
		                		 arrSettleOptions[10]="";
		                		 arrSettleOptions[11]="";
		                		 hmSettlemetnOptions.set(settleName, arrSettleOptions);
		                	 }
		                     break;
	            }
	            
	            balanceAmount=$('#txtAmount').val()-$('#txtPaidAmount').val();
	         

	        
	        flgEnterBtnPressed=true;
	        funSettlementDtlFill(); //for refresh item table
		}
}
 
	 
	function funSettlementDtlFill()
	 {
  

		    var tblSettleItemDtl=document.getElementById('tblSettleItemTable');
			var rowCount = tblSettleItemDtl.rows.length;
		   if(checkSetteType=="Balance")
			{
			   tblSettleItemDtl.deleteRow(rowCount-1);
				rowCount--;
			}
		
		
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
		    var col10=insertRow.insertCell(9);
            var remark="";//  $('#txtRemark').val();
            var dteExpiryDate=  "";//$('#dteExpiryDate').val();
            
		    col1.innerHTML = "<input   readonly=\"readonly\" size=\"32px\"  class=\"strSettelmentDesc\"    style=\"text-align: left; color:blue; height:20px;\"   name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strSettelmentDesc\" id=\"strSettelmentDesc."+(rowCount)+"\" value='"+settleName+"' />";
		    col2.innerHTML = "<input readonly=\"readonly\"  size=\"4px\"        style=\"text-align: right; color:blue; height:20px;\"  />";
		    col3.innerHTML = "<input readonly=\"readonly\" size=\"10px\"   class=\"dblSettlementAmt\"      style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].dblSettlementAmt\" id=\"dblSettlementAmt."+(rowCount)+"\" value='"+paidAmount+"'/>";
		    col4.innerHTML = "<input readonly=\"readonly\"  size=\"1px\"      style=\"text-align: right; color:blue; height:20px;\"  />";
		    col5.innerHTML = "<input readonly=\"readonly\"  size=\"1px\"      style=\"text-align: right; color:blue; height:20px;\"  />";
		    col6.innerHTML = "<input type=\"hidden\"  size=\"0px\"   class=\"strSettelmentType\"      style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strSettelmentType\" id=\"strSettelmentType."+(rowCount)+"\" value='"+settleType+"' />";
		    col7.innerHTML = "<input type=\"hidden\" size=\"0px\" class=\"dblPaidAmt\"     style=\"text-align: right; color:blue; height:20px;\"   name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].dblPaidAmt\" id=\"dblPaidAmt."+(rowCount)+"\" value="+paidAmount+" />";
		    col8.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"dblRefundAmt\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].dblRefundAmt\" id=\"dblRefundAmt."+(rowCount)+"\" value="+refundAmount+" />";
		    col9.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"strSettelmentCode\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strSettelmentCode\" id=\"strSettelmentCode."+(rowCount)+"\" value='"+settlementCode+"'/>";
		    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"strRemark\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strRemark\" id=\"strRemark."+(rowCount)+"\" value='"+remark+"'/>";
		    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"strExpiryDate\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strExpiryDate\" id=\"strExpiryDate."+(rowCount)+"\" value='"+dteExpiryDate+"'/>";
		    col10.innerHTML = "<input type=\"hidden\" size=\"0px\"   class=\"strCardName\"  style=\"text-align: right; color:blue; height:20px;\"  name=\"listSettlementDtlOnBill["+(rowCountSettle)+"].strCardName\" id=\"strCardName."+(rowCount)+"\" value=''/>";
		    
		    document.getElementById("divRemarks").style.display='none';
		    document.getElementById("divAmt").style.display='none';
		    
		    funFillBalanceAmt();
		    
		    rowCountSettle++;
		    
            }

			function funFillBalanceAmt()
			{
			    var tblSettleItemDtl=document.getElementById('tblSettleItemTable');
				var rowCount = tblSettleItemDtl.rows.length;
				var insertRow = tblSettleItemDtl.insertRow(rowCount);
				var col1=insertRow.insertCell(0);
			    var col2=insertRow.insertCell(1);
			    var col3=insertRow.insertCell(2);
			    var col4=insertRow.insertCell(3);
			    var col5=insertRow.insertCell(4);
			    col1.innerHTML = "<input   readonly=\"readonly\" size=\"32px\"     style=\"text-align: left; color:blue; height:20px;\"   value='Balance Amount' />";
			    col2.innerHTML = "<input readonly=\"readonly\"  size=\"4px\"        style=\"text-align: right; color:blue; height:20px;\"  />";
			    col3.innerHTML = "<input readonly=\"readonly\" size=\"10px\"        style=\"text-align: right; color:blue; height:20px;\"  value='"+balanceAmount+"' />";
			    col4.innerHTML = "<input readonly=\"readonly\"  size=\"1px\"      style=\"text-align: right; color:blue; height:20px;\"  />";
			    col5.innerHTML = "<input readonly=\"readonly\"  size=\"1px\"      style=\"text-align: right; color:blue; height:20px;\"  />";
			    
			    $('#txtAmount').val(balanceAmount);
			    $('#txtPaidAmount').val(balanceAmount);
			    
			    checkSetteType="Balance";
			}



	function funRemoveProductRows()
	{
		var table = document.getElementById("tblSettleItemTable");
		var rowCount = table.rows.length;
		while(rowCount>0)
		{
			table.deleteRow(0);
			rowCount--;
		}
	}

	function funSubGroupDiscClicked()
	{
		disountType="";
		disountType="subGroup";
		 document.all["cmbItemCategary"].style.display = 'block';
		 
// 		 var element="${listSubGroupName}";
// 		 var subGroupcode="${listSubGroupCode}";
// 		 var tempCode=subGroupcode.substring(1,subGroupcode.length-1);
// 		 subGroupcode=tempCode.split(",");
		 
// 		 var tmp=element.substring(1,element.length-1)
// 		 element=tmp.split(",");
// 			var html = '';
			
// 			   hmSettlemetnOptions.forEach(function(value, key) {
//        	hmGroupMap.set(strGroupCode, strGroupName); 
// 				hmSubGroupMap.set(strSubGroupCode, strSGName);
// 			for(var cnt=0;cnt<element.length;cnt++)
// 			{
// 					html += '<option value="' + subGroupcode[cnt] + '" >' + element[cnt]
// 							+ '</option>';
// 			}
				var html = '';
			hmSubGroupMap.forEach(function(value, key) {
				html += '<option value="' + key + '" >' + value
						+ '</option>';
			   });
	
			$('#cmbItemCategary').html(html);
			
		
	}
	function funGroupDiscClicked()
	{
		disountType="";
		disountType="group";
		 document.all["cmbItemCategary"].style.display = 'block';
// 		 var element="${listGroupName}";
		
// 		 var groupcode="${listGroupCode}";
// 		 var tempCode=groupcode.substring(1,groupcode.length-1);
// 		 groupcode=tempCode.split(",");
		 
		 
		
// 		 var tmp=element.substring(1,element.length-1)
// 		 element=tmp.split(",");
// 			var html = '';
// 			for(var cnt=0;cnt<element.length;cnt++)
// 			{
// 					html += '<option value="'+groupcode[cnt]+'" >' +element[cnt]
// 							+ '</option>';
// 			}
	var html = '';
			hmGroupMap.forEach(function(value, key) {
				html += '<option value="' + key + '" >' + value
						+ '</option>';
			   });
			$('#cmbItemCategary').html(html);
		
	}
	function funItemDiscClicked()
	{
		disountType="";
		disountType="item";
		 document.all["cmbItemCategary"].style.display = 'block';
// 		 var element="${listItemName}";
// 		 var itemcode="${listItemCode}";
// 		 var tempCode=itemcode.substring(1,itemcode.length-1);
// 		 itemcode=tempCode.split(",");
		
// 		 var tmp=element.substring(1,element.length-1)
// 		 element=tmp.split(",");
// 			var html = '';
// 			for(var cnt=0;cnt<element.length;cnt++)
// 			{
// 					html += '<option value="' + itemcode[cnt] + '" >' + element[cnt]
// 							+ '</option>';
// 			}
	var html = '';
	 hmItempMap.forEach(function(value, key) {
				html += '<option value="' + key + '" >' + value
						+ '</option>';
			   });
			$('#cmbItemCategary').html(html);
	}
	function funTotalDiscClicked()
	{
		 document.all["cmbItemCategary"].style.display = 'none';
	}
	function funCardNo()
	{
		var retDebitCardNo="";
		var searchurl=getContextPath()+"/funGetDebitCardNo.html?voucherNo="+voucherNo+"&tableNo="+tableNo;
		$.ajax({
			type:"GET",
		 	url: searchurl,
	        dataType: "json",
	        async : false,
	        success: function (response){
	        	retDebitCardNo=response.retDebitCardNo;
	        	debitCardNo =response.retDebitCardString;
	        	
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
		
		return retDebitCardNo;
	}
	function funCheckPointsAgainstCustomer()
	{
		var totalLoyalityPoints = 0.00, totalReedemedPoints = 0.00;
		var flgResult=false;
		var txtPaidAmt=$("#txtPaidAmount").val();
		var customerMobile="${customerMobile}";
		var searchurl=getContextPath()+"/funCheckPointsAgainstCustomer.html?CRMInterface="+gCRMInterface+"&customerMobile="+customerMobile+"&voucherNo="+voucherNo+"&txtPaidAmt="+txtPaidAmt;
		$.ajax({
			type:"GET",
		 	url: searchurl,
	        dataType: "json",
	        async : false,
	        success: function (response){
	        	totalLoyalityPoints=response.totalLoyalityPoints;
	        	totalReedemedPoints=response.totalReedemedPoints;
	        	 
	        	if (txtPaidAmt <= totalLoyalityPoints)
                 {
                     flgResult = true;
                     loyalityPoints = txtPaidAmt;
                 }
                 else
                 {
                	 alert("Your total Loyality points are "+totalLoyalityPoints+" ")
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
		
		return flgResult;
	}
// 	  function funGetTotalPaidAmount()
// 	    {
// 	        var totalPaidAmt = 0.00;
// 	        var arr=new Array();
// 	        if(hmSettlemetnOptions.size>0)
//         	{
//         		hmSettlemetnOptions.forEach(function (item, key, mapObj) 
//         		{
//         			arr=item;
//     	       		if ("Complementary"==(arr[6]))
//     	            {
//     	                totalPaidAmt = finalGrandTotal;
//     	                break;
//     	            }
//     	            totalPaidAmt =totalPaidAmt + arr[3];
//         		});
//         	}
// 	        return totalPaidAmt;
// 	    }
	  
	  
	  function funBackButtonClicked(backButton)
	  {
		  document.getElementById("tab2").style.display='none';		
		  document.getElementById("tab1").style.display='block';  
	  }
	  
	  
	  
	  function funSaveBtnClicked()
	  {

		  	if(balanceAmount>0)
		  	{
		  		 alert("Balance is not zero.");
		  		 
		  		 return false;
		  	}
		  
		  
			 if(operationType=="DineIn" && transactionType=="Make KOT" )
			 {
			 
				    var tableName=gTableName;
					var yes=confirm("Do you want to generate bill for table : "+tableName);
					if(yes)
					{
						document.frmBillSettlement.action = "actionBillSettlementKOT.html";
						document.frmBillSettlement.method = "POST";
					    document.frmBillSettlement.submit();
					}
					else
					{
						 return false;
					}		 		   
			 }
			 else if(operationType=="DineIn" && transactionType=="Make Bill" )
			 {
				 
				    var tableName=gTableName;
					var yes=confirm("Do you want to generate bill for table : "+tableName);
					if(yes)
					{
						document.frmBillSettlement.action = "actionBillSettlementKOT.html";
						document.frmBillSettlement.method = "POST";
					    document.frmBillSettlement.submit();
					}
					else
					{
						 return false;
					}		 		   
			 } 
				 
			 else  if(operationType=="HomeDelivery" && transactionType=="Direct Biller")
			 {
				 
				 
		    	 document.frmBillSettlement.action = "actionBillSettlement.html";
		    	 document.frmBillSettlement.method = "POST";
				 document.frmBillSettlement.submit();
		     }
			 else  if(operationType=="TakeAway" && transactionType=="Direct Biller")
			 {
				 
				 
		    	 document.frmBillSettlement.action = "actionBillSettlement.html";
		    	 document.frmBillSettlement.method = "POST";
				 document.frmBillSettlement.submit();
		     }
			 else  if(transactionType=="Modify Bill")
			 {
				 
				 
		    	 document.frmBillSettlement.action = "actionModifyBill.html";
		    	 document.frmBillSettlement.method = "POST";
				 document.frmBillSettlement.submit();
		     }
			 else if(operationType=="DineIn" && transactionType=="Bill For Items")
			 {				 
					var yes=confirm("Do you want to generate bill for items?");
					if(yes)
					{
						
						 
						 
						document.frmBillSettlement.action = "actionForBillForItems.html";
						
					    document.frmBillSettlement.submit();
					}
					else
					{
						 return false;
					}		 		   
			 }
			 else if((operationType=="DineIn" || operationType=="HomeDelivery" || operationType=="TakeAway" ) && transactionType=="Settle Bill" )
			 {
			 
				 	var billNo=$("#hidBillNo").val();
				 	
					var yes=confirm("Do you want to settle bill : "+billNo);
					if(yes)
					{
						document.frmBillSettlement.action = "actionBillSettle.html";
						document.frmBillSettlement.method = "POST";
					    document.frmBillSettlement.submit();
					}
					else
					{
						 return false;
					}		 		   
			 }
			 
	    }
	  
	  
	  
	  
	  
	  
	  
	  
// 	///Click on OK Button of Discount
function funDiscOkClicked()
{ 
	
	

	var discOnType="Total";
	var discOnValue="Total";
  		
	      var discGroup=disountType;
		  var totamt = 0.00;
		  var listOfDiscApplicableItems=[];

		 //Totlal Radio button is Selected
		  if(discGroup=='Total')
	      {
			  discOnType="Total";
			  discOnValue="Total";

			  $.each(listBillItem,function(i,obj)
			  { 
				
	        	    var singleObj = {}
	        	    
	        	    singleObj['itemCode'] = obj.itemCode;
	        	    singleObj['itemName'] = obj.itemName;
	        	    singleObj['quantity'] = obj.quantity;
	        	    singleObj['amount'] = obj.amount;
	        	    singleObj['discountPer'] = obj.discountPer;
	        	    singleObj['discountAmt'] = obj.discountAmt;
	        	    
	        	    
	        	    listOfDiscApplicableItems.push(singleObj);
				        	
			   })
	      }
		//Item  is Selected
		  if(discGroup=='item')
		  {
			  discOnType="Item";			  			  
			  
 			   $.each(listBillItem,function(i,obj)
		        { 
			
			      if($('#cmbItemCategary').val()==obj.itemCode)
			      {
			    	  
			      
			    	discOnValue= obj.itemName;
			    	  
	        	    var singleObj = {}
	        	    
	        	    singleObj['itemCode'] = obj.itemCode;
	        	    singleObj['itemName'] = obj.itemName;
	        	    singleObj['quantity'] = obj.quantity;
	        	    singleObj['amount'] = obj.amount;
	        	    singleObj['discountPer'] = obj.discountPer;
	        	    singleObj['discountAmt'] = obj.discountAmt;
	        	    
	        	    listOfDiscApplicableItems.push(singleObj);
			      }
		       })
		  }
		
		//Group is Selected
		
		  if(discGroup=='group')
		  {  

			  discOnType="Group";		
			  			  
			  
			  var groupcode=$('#cmbItemCategary option:selected').val();
			  var groupName=$('#cmbItemCategary option:selected').text();
			  
			  
			  $.each(listBillItem, function(i, obj) 
			  {
				  if(groupcode==obj.strGroupcode)
				  {
			       	    
					  discOnValue=groupName;
					  
					  var singleObj = {}
			       	    
			       	 singleObj['itemCode'] = obj.itemCode;
			       	    singleObj['itemName'] = obj.itemName;
			       	    singleObj['quantity'] = obj.quantity;
			       	    singleObj['amount'] = obj.amount;
			       	    singleObj['discountPer'] = obj.discountPer;
			       	    singleObj['discountAmt'] = obj.discountAmt;
			       	    
			       	    listOfDiscApplicableItems.push(singleObj);
				  } 
		      })
		  }
		
// 		//SubGroup is Selected
			
		  if(discGroup=='subGroup')
		  {
			  discOnType="SubGroup";		
			  
			  
			  
			  var subgroupcode=$('#cmbItemCategary  option:selected').val();
			  var subGroupName=$('#cmbItemCategary  option:selected').text();
			  
			  
			  $.each(listBillItem, function(i, obj) 
			  {
			      if(subgroupcode==obj.strSubGroupCode)
			      {
	        	   
			    	  discOnValue=subGroupName;
			    	  
			    	  var singleObj = {}
	        	    
	        	    singleObj['itemCode'] = obj.itemCode;
	        	    singleObj['itemName'] = obj.itemName;
	        	    singleObj['quantity'] = obj.quantity;
	        	    singleObj['amount'] = obj.amount;
	        	    singleObj['discountPer'] = obj.discountPer;
	        	    singleObj['discountAmt'] = obj.discountAmt;
	        	    
	        	    listOfDiscApplicableItems.push(singleObj);
	        	    
			      }
		       })
		  }
	
		  var perDisc=$('#txtDiscountPer').val();
		  
		  var amtDisc= $('#txtDiscountAmt').val();
		  
	
		  var listOfDicountItem=[];
		  
		  var checkTaxTotal=0;
		  var dblTaxTotal=0;
		  
		  totalDiscAmt=0;
		  
		  var dblDiscountOnAmt= getTotalAmt(listOfDiscApplicableItems) ;
		  
		  if(amtDisc>0)
		  {
			  perDisc=(amtDisc/dblDiscountOnAmt)*100;
			  
			  $('#txtDiscountPer').val(perDisc);
		  }
		  
		  
		  
		  
		  $.each(listOfDiscApplicableItems, function(i, obj) 
		  {
			 
				 var discPer=perDisc;
				 var discAmt = obj.amount * (perDisc / 100);
				 
				 totalDiscAmt=totalDiscAmt+discAmt;
				 
				 	             
			     var singleObj = {}
			     
			     singleObj['itemCode'] = obj.itemCode;
		   	     singleObj['itemName'] = obj.itemName;
		   	     singleObj['quantity'] = obj.quantity;
		   	     singleObj['amount'] = obj.amount;
		   	     singleObj['discountPer'] = discPer;
		   	     singleObj['discountAmt'] = discAmt;
		   	 	
		   	     listOfDicountItem.push(singleObj);
			 
 		  });
		  
		  $("#txtDiscountAmt").val(totalDiscAmt);
		  
		 	 
		  var listItmeDtl=[];	   
		  
		  var hmItempMap=new Map();	
			 
			 $.each(listBillItem,function(i,obj)
			 { 

	        	    hmItempMap.set(obj.itemCode,obj.itemName);
	        	    
	        	    var discAmt=0,discPer=0;
	        	    
	        	    $.each(listOfDicountItem,function(i,objDiscItem)
	        		{
	        	    	if(obj.itemCode==objDiscItem.itemCode)
	        	    	{
	        	    		discAmt=objDiscItem.discountAmt;
	        	    		discPer=objDiscItem.discountPer;
	        	    	}
	        		})
					
	        	    var singleObj = {}
	        	    
				    singleObj['itemName'] =obj.itemName;
				    singleObj['quantity'] =obj.quantity; 
				    singleObj['amount'] = obj.amount;
				    singleObj['discountPer'] = discPer;
	        	    singleObj['discountAmt'] = discAmt;				    
				    singleObj['itemCode'] = obj.itemCode;
				    singleObj['rate'] =obj.amount/obj.quantity;
				    
				    
				    listItmeDtl.push(singleObj);
				        	
			 })		  
			 
		 var totalamount=getTotalAmt(listBillItem) ;
			 
			var oTable = document.getElementById('tblSettleItemTable');
			var rowLength = oTable.rows.length;
			
			var $rows = $('#tblSettleItemTable').empty();
			listBillItem=[];
			

			
			finalSubTotal=0.00;
			finalDiscountAmt=0.00;
			finalNetTotal=0.00;
			taxTotal=0.00;
			taxAmt=0.00;
			finalGrandTotal=0.00;	
	
			 
		
			 
		/**
		*calculating promotions and filling data to grid for bill print	
		*/
		funNoPromtionCalculation(listItmeDtl);
	
		    
		funRefreshSettlementItemGrid();	
			
			
			
			
			
			
			


        var tblFillDisountList=document.getElementById('tblFillDisountList');
		var rowCount = tblFillDisountList.rows.length;
		var insertRow = tblFillDisountList.insertRow(rowCount);
			
	    var col1=insertRow.insertCell(0);
	    var col2=insertRow.insertCell(1);
	    var col3=insertRow.insertCell(2);
	    var col4=insertRow.insertCell(3);
	    var col5=insertRow.insertCell(4);
	    var col6=insertRow.insertCell(5);
	    var col7=insertRow.insertCell(6);
	    
		  var per= $("#txtDiscountPer").val();
		  var amt=$("#txtDiscountAmt").val();

	  
	    col1.innerHTML = "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountPer\" id=\"discountPer."+(disCount)+"\" value='"+per+"' />" ;;
 	    col2.innerHTML =  "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountAmt\" id=\"discountAmt."+(disCount)+"\" value='"+amt+"' />";
 	    col3.innerHTML =  "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountOnAmt\" id=\"discountOnAmt."+(disCount)+"\" value='"+dblDiscountOnAmt+"' />";
 	    col4.innerHTML = "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountOnType\" id=\"discountOnType."+(disCount)+"\" value='"+discOnType+"' />";
	    col5.innerHTML = "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountOnValue\" id=\"discountOnValue."+(disCount)+"\" value='"+discOnValue+"' />";
 	    col6.innerHTML = "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountReasonCode\" id=\"discountReasonCode."+(disCount)+"\" value='' />";	    
 	    col7.innerHTML =  "<input type=\"hidden\"  name=\"listDiscountDtlOnBill["+(disCount)+"].discountRemarks\" id=\"discountRemarks."+(disCount)+"\" value='Discount' />";
 	    
     		
 	    disCount++;
     		
	
			
 	  }
	  
	 function  getTotalAmt(listOfDiscApplicableItems) 
	 {
	        var totalDiscOnAmt = 0.00;
	        $.each(listOfDiscApplicableItems, function(i, obj) 
		  {	
	            totalDiscOnAmt += parseFloat(obj.amount);
	        });
	        return totalDiscOnAmt;
	 }
	 
	 
	 function funDiscPerFocusOut(objThis)
	 {
		if(objThis.value<=0)
		{
			objThis.value="0.00"; 
		}
		
	 }
	 
	 function funDiscAmtFocusOut(objThis)
	 {
		if(objThis.value<=0)
		{
			objThis.value="0.00"; 
		}
		
	 }

	 
	 function funTaxOnDisc()
	 {
		 var listOfItemwithDisc=[];
			var oTable = document.getElementById('tblSettleItemTable');
			var rowLength = oTable.rows.length;
			for (var i = 0; i < listBillItem.length; i++)
			{
				var singleObj = {};
			
			    singleObj['itemName'] =oTable.rows[i].cells[0].children[0].value;
			    singleObj['quantity'] =oTable.rows[i].cells[1].children[0].value;
			    singleObj['amount'] = oTable.rows[i].cells[2].children[0].value;
			    singleObj['discountPer'] = oTable.rows[i].cells[3].children[0].value;
			    singleObj['discountAmt'] =oTable.rows[i].cells[4].children[0].value;
			    var oCells = oTable.rows.item(i).cells[8].innerHTML;	
				var btnBackground=oCells.split('value=');
			    
				singleObj['itemCode']=btnBackground[1].substring(1, (btnBackground[1].length-2));
			    listOfItemwithDisc.push(singleObj);
			}
			funCalculateTaxOndiscount(listOfItemwithDisc);
	 
	 }
		function funCalculateTaxOndiscount(listBillItem)
		{
			
			var taxTotal=0;
			var rowCountTax=0;
			var searchurl=getContextPath()+"/funCalculateTaxInSettlement.html?";
			$.ajax({
				 type: "POST",
			        url: searchurl,
			        data : JSON.stringify(listBillItem),
			        contentType: 'application/json',
			        async: false,
		        success: function (response){
		        	    	$.each(response,function(i,item){
				        		taxTotal=taxTotal+response[i].taxAmount;
				        		funFillTaxTable(response[i].taxName,response[i].taxAmount,response[i].taxCode,response[i].taxCalculationType,response[i].taxableAmount ,rowCountTax);
				        		rowCountTax++;
				        	});
	             
		        	    	finalGrandTotal=finalGrandTotal+taxTotal;
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
		
		
		function funFillTaxTable(taxName,taxAmount,taxCode,taxCalculationType,taxableAmount,rowCountTax){
			
			
			 document.getElementById("taxName."+rowCountTax).value=taxName;
			 document.getElementById("taxAmount."+rowCountTax).value=taxName;
			 document.getElementById("taxCode."+rowCountTax).value=taxName;
			 document.getElementById("taxableAmount."+rowCountTax).value=taxName;
			 document.getElementById("taxCalculationType."+rowCountTax).value=taxName;

		}
	
 </script>
</head>
<body>
<!-- onload="loadofBillSettlemntTab()">  -->
<!--  -->
<s:form  name="frmBillSettlement"  method="GET"   action="" >
<div id="divMain" style="margin-left: 50px; height: 560px; width: 1000px " cssClass="test">
<table>
	<tr>
		<td><br></td>
		<td></td>
	</tr>
	<tr>
		<td>
			<div id="divOrder" style=" border: 0px solid #ccc; height: 560px;  overflow-x: auto; width: 350px;">
				<label id="billNoForDisplay" style=" display: inline-block;width: 175px;text-align: left;" >Bill No: ${billNo}</label>
				<label id="tableNameForDisplay" style=" display: inline-block;width: 175px;text-align: left;">Table No: ${gTableName}</label>
				<%-- <br>
				<label id="billDateForDisplay" style=" display: inline-block;width: 130px;text-align: left;">Date: ${billDate}</label> --%>
				
				<!--  here the div of Item table-->
				<!-- <div id="divItemTable" style=" border: 1px solid #ccc; height: 460px; width: 400px;"> -->
					<table id="tblSettleItemTableHead" style="width: 98%; height:40px; border-collapse: collapse;overflow: auto;margin-left: auto;margin-right: auto;
								font-size: 10px;font-weight: bold;"> 
						<tr>
							  <th><input type="button" value="Description" style="width: 213px;height: 37px;" class="btn" ></input></th>
							  <th><input type="button" value="Qty" style="width: 51px;height: 37px;" class="btn" ></input></th>
							  <th><input type="button" value="Amount" style="width: 82px;height: 37px;" class="btn"></input></th>
							  <th><input type="button" value="%" style="width: 1px;height: 37px;"  class="btn"></input></th>
							  <th><input type="button" value="Amt" style="width: 1px;height: 37px;"  class="btn"></input></th>
						</tr>
							
					</table> 
					<!-- <div id="divItemTableInner" style=" border: 0px solid #ccc; height: 460px;  overflow-x: auto; overflow-y: auto; width: 400px;"> -->
						<table id="tblSettleItemTable" style="width: 98%;border-collapse: collapse;overflow: auto;margin-left: auto;margin-right: auto;
									font-size: 10px;font-weight: bold;">
						</table>
					<!-- </div> -->
<!-- 				</div> --> 
				
			</div>
		</td>
		<td>
		  <div id="divSettlement" style=" border: 0px solid #ccc; height: 560px;  overflow-x: auto; overflow-y: auto; width: 600px;">
				<div id="divSettlementButtons" style="text-align: right; height:50px; overflow-x: auto; overflow-y: auto; width: 600px;">
					 	<table id="tblSettlementButtons"  cellpadding="0" cellspacing="2"  >				 																																	
								<tr>							
									<c:forEach var="objSettleButtons" items="${command.jsonArrForSettleButtons}"  varStatus="varSettleButtons">
											<td style="padding-right: 3px;">
												<input  type="button" id="${objSettleButtons.strSettelmentDesc}"  value="${objSettleButtons.strSettelmentDesc}" tabindex="${varSettleButtons.getIndex()}"  style="width: 100px;height: 30px; white-space: normal;"   onclick="funSettleOptionSelected('${objSettleButtons.strSettelmentDesc}','${objSettleButtons.strSettelmentType}','${objSettleButtons.strSettelmentCode}','${objSettleButtons.dblConvertionRatio}','${objSettleButtons.strBillPrintOnSettlement}')" class="btn btn-outline-secondary"/>
											</td>
									</c:forEach>																						
							    </tr>																																				 									   				   									   									   						
						</table>			
			 	</div>
			 	
			 	<!-- div for payment mode  -->
			 	
			 	<div id="divPaymentMode" style=" border: 0px solid #ccc;height:20px;width:230px;position:absolute; top:180px" >
			 		<label id="lblPayMentMode" style=" display: inline-block;width: 100%;text-align: left;">Payment Mode </label>
			 	</div>
			 	
			 	<!-- div form Amount -->
			 	<div id="divAmt"  style=" border: 0px solid #ccc;height:141px;width:240px; position:absolute; top:200px"  >
				 	<table style="width:95%;height:140px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblAmount" style=" display: inline-block;width: 70px;text-align: left;">Bill Amount</label></td>
					 		<td><s:input  type="text"  id="txtAmount" path="" cssStyle="width:120px; text-align: right;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblPaidAmt" style=" display: inline-block;width: 70px;text-align: left;">Paid Amount</label></td>
					 		<td><s:input  type="text"  id="txtPaidAmount" path="" cssStyle="width:120px; text-align: right;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblBalance" style=" display: inline-block;width: 70px;text-align: left;">Balance</label></td>
					 		<td><s:input  type="text"  id="txtBalance" path="" cssStyle="width:120px; text-align: right;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblCard" style="display: inline-block;width: 70px;text-align: left;">Card Balance</label></td>
							<td><label id="lblCardBalance" style=" display: inline-block;width: 100%;text-align: left;"></label></td>
				 		</tr>
				 	</table>
			 	</div>
			 	<!-- div Card  -->
			 	<div id="divCard"  style=" border: 0px solid #ccc;height:65px;width:240px; position:absolute; top:350px" >
			 		<table style="width:95%;height:60px;font-size:11px; font-weight: bold;">
			 			<tr>
					 		<td><label id="lblSlipNo" style=" display: inline-block;width: 70px;text-align: left;">Slip No </label></td>
					 		<td><s:input  type="text"  id="txtCardName" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblExpiryDate" style=" display: inline-block;width: 70px;text-align: left;">Expiry Date</label></td>
					 		
					 		<td><s:input id="dteExpiry"  path="dteExpiryDate"  cssClass="calenderTextBox hasDatepicker"/> </td>
				 		</tr>
			 		</table>
			 	</div>
			 	<!--div Coupen  -->
			 	<div id="divCoupen"  style=" border: 0px solid #ccc;height:65px;width:240px;display :block; position:absolute; top:385px;" >
				 	<table style="width:95%;height:60px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblAmountLabel" style=" display: inline-block;width: 70px;text-align: left;">Amount   </label></td>
					 		<td><s:input  type="text"  id="txtCoupenAmt" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblRemarkLabel" style=" display: inline-block;width: 70px;text-align: left;">Remark</label></td>
					 		<td><s:input  type="text"  id="txtRemark" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
			 		</table>
				</div>
				<!-- Div Cheque  -->
			 	<div id="divCheque"  style=" border: 0px solid #ccc;height:100px;width:240px; position:absolute; top:285px; " >
				 	<table style="width:95%;height:100px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblBankName" style=" display: inline-block;width: 70px;text-align: left;">Bank Name</label></td>
					 		<td><s:input  type="text"  id="txtBankName" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblChequeNo" style=" display: inline-block;width: 70px;text-align: left;">Cheque No</label></td>
					 		<td><s:input  type="text"  id="txtChequeNo" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblChqDate" style=" display: inline-block;width: 70px;text-align: left;">Date</label></td>
					 		<td><s:input id="dteCheque" path="" pattern="\d{1,2}-\d{1,2}-\d{4}" cssClass="calenderTextBox hasDatepicker"/> </td>
				 		</tr>
			 		</table>
				</div>
				
				<!-- Div Gift Voucher  -->
				<div id="divGiftVoucher"  style=" border: 0px solid #ccc;height:80px;width:240px; position:absolute; top:515px;" >
				 	<table style="width:95%;height:85px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblGVouchName" style=" display: inline-block;width: 100%;text-align: left;">Voucher Name</label></td>
					 		<td><s:input  type="text"  id="txtVoucherSeries" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblGVSeriesNo" style=" display: inline-block;width: 100%;text-align: left;">Cheque No</label></td>
					 		<td><s:input  type="text"  id="txtSeriesNo" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td></td>
					 		<td><input type="button" id="btnGiftVoucherOk" value="OK" style="width: 65px; height:25px;"  onclick="funGiftVoucherOkClicked(this)" class="btn btn-primary btn-sm" ></input> </td>
					 		
				 		</tr>
			 		</table>
				</div>
				<!-- div remarks -->
				<div id="divRemarks" style=" border: 0px solid #ccc;height:90px;width:220px; position:absolute; top:425px; left:790px;" >
					<table style="width:95%;height:88px;font-size:11px; font-weight: bold;">
						<tr>
					 		<td><label id="lblRemark" style=" display: inline-block;width: 100%;text-align: left;">Remark </label></td>
					 	</tr>
				 		<tr>
					 		<td>
					 		<s:textarea colspan="10" rowspan="5"  id="txtRemarks" path="" style="height:70px;width:200px" cssClass="longTextBox"  /> 
					 		</td>
				 		</tr>
					</table>
				</div>
				<!-- div Customer  -->
			 	<div id="divCustomer" style=" border: 0px solid #ccc;height:30px;width:300px; position:absolute;top:110px; left:650px;">
			 		<table style="width:95%;height:28px;font-size:11px; font-weight: bold;">
						<tr>
							<td><label id="lblCustName" style=" display: inline-block;width: 100%;text-align: left;">Customer Name</label></td>
					 		<td><s:input  type="text"  id="hidCustomerName" path="" cssStyle="width:150px;" cssClass="longTextBox jQKeyboard form-control"  ondblclick="funHelp('POSCustomerMaster')" /> </td>
				 		</tr>
					</table>
					
			 	</div>
			 <!-- Extra Beside discount -->	
			 	<div id="divExtraDisc" style=" border: 0px solid #ccc;height:100px;width:80px;display :none; position:absolute;top:150px; left:660px;">
					 	<label id="lblCreditCustCode" style=" display: block;width: 70px;text-align: left;"> </label>
			 	</div>
			 	<!-- div Discount  -->
			 	<div id="divDiscount" style=" border: 0px solid #ccc;height:100px;width:240px; position:absolute;top:180px; left:740px;">
			 		<table style="width:98%;height:98px;font-size:11px; font-weight: bold;">
					 		<tr>
					 			<td style="padding-bottom: 2px;"><label id="lblDisc" style=" display: inline-block;width: 100%;text-align: left;">Discount %</label></td>
						 		<td style="padding-bottom: 2px;"><s:input  type="text"  id="txtDiscountPer" value="0.00" path="dblDiscountPer"  cssStyle="width:120px; text-align: right;" cssClass="longTextBox jQKeyboard form-control"  onfocus="this.value=''" onfocusout="funDiscPerFocusOut(this)"  /> </td>						 		
					 		</tr>
					 		<tr>
					 			<td style="padding-bottom: 2px;" ><label id="lblDiscAmt" style=" display: inline-block;width: 100%;text-align: left;">Discount Amount</label></td>
			        	 		<td style="padding-bottom: 2px;"><s:input  type="text"  id="txtDiscountAmt" value="0.00" path="dblDiscountAmt" cssStyle="width:120px; text-align: right;" cssClass="longTextBox jQKeyboard form-control"   onfocus="this.value=''" onfocusout="funDiscAmtFocusOut(this)" /> </td>			        	 		
				 			</tr>
				 			<tr>
					 			<td colspan="2" style="padding-bottom: 2px;">
<%-- 					 			<s:radiobutton id="rdbSubGroupWise"  path="strDisountOn"   name ="rdbDiscgroup" value="subGroup" onclick="funSubGroupDiscClicked()"></s:radiobutton>Sub Group  --%>
<%-- 								<s:radiobutton id="rdbGroupWise" path="strDisountOn"   value="group" name ="rdbDiscgroup" onclick="funGroupDiscClicked()"></s:radiobutton>Group &emsp; --%>
<%-- 								<s:radiobutton id="rdbItemWise" path="strDisountOn" value="item" name ="rdbDiscgroup" onclick="funItemDiscClicked()"></s:radiobutton>Item &emsp; --%>
<%-- 								<s:radiobutton id="rdbAll" path="strDisountOn"   value="Total" name ="rdbDiscgroup" checked="checked" onclick="funTotalDiscClicked()"></s:radiobutton>Total	 --%>
								
								<s:input type="radio"   path="strDisountOn"   name ="rdbDiscgroup" value="subGroup" onclick="funSubGroupDiscClicked()"/>Sub Group 
								<s:input type="radio"  path="strDisountOn"   value="group" name ="rdbDiscgroup" onclick="funGroupDiscClicked()"/>Group &emsp;
								<s:input type="radio"  path="strDisountOn" value="item" name ="rdbDiscgroup" onclick="funItemDiscClicked()"/>Item &emsp;
								<s:input type="radio"  path="strDisountOn"   value="Total" checked="checked"  name ="rdbDiscgroup"  onclick="funTotalDiscClicked()"/>Total
								
								</td>
				 			</tr>
				 			<tr>				 				
					 			<td colspan="2" style="padding-bottom: 2px;">
						 			<s:select id="cmbItemCategary" name="cmbItemCategary" path="" cssClass="BoxW124px" items="${listItemName}" />	
						 		</td>	
						 		<td>				 			
						 			<input type="button" id="btnDiscOk" value="OK" style="width: 45px; height:25px;"  onclick="funDiscOkClicked(this)" class="btn btn-primary btn-sm" ></input> 
					 			</td>					 			
				 			</tr>
					</table>
				</div>
				<!--Div Room Settlement  -->
			 	<div id="divRoomSettlement" style=" border: 0px solid #ccc;height:140px;width:240px; position:absolute;top:140px; left:350;">
			 		<table style="width:98%;height:138px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblGuestName" style=" display: inline-block;width: 70px;text-align: left;">Guest Name </label></td>
					 		<td><s:input  type="text"  id="txtGuestName" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblFolioNo" style=" display: inline-block;width: 70px;text-align: left;">Folio No.</label></td>
					 		<td><s:input  type="text"  id="txtFolioNo" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblRoomNo" style=" display: inline-block;width: 70px;text-align: left;">Room No.</label></td>
					 		<td><s:input  type="text"  id="txtRoomNo" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblGuestCode" style=" display: inline-block;width: 70px;text-align: left;">Guest Code</label></td>
					 		<td><s:input  type="text"  id="txtGuestCode" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
			 		</table>
			 	</div>
			 	
			 	<!--Div Jio Money Settlement   -->
			 	<div id="divJioMoneySettlement" style=" border: 0px solid #ccc;height:100px;width:240px; position:absolute; top:515px; display:none;" >
				 	<table style="width:95%;height:85px;font-size:11px; font-weight: bold;">
				 		<tr>
					 		<td><label id="lblJioMoneyCode" style=" display: inline-block;width: 100%;text-align: left;">Scan/Enter Code</label></td>
					 		<td><s:input  type="text"  id="txtJioMoneyCode" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblJioMoneySendNotific" style=" display: inline-block;width: 100%;text-align: left;">Send Notification</label></td>
					 		<td><s:input type="checkbox"  id="chkJioNotific" path="" value="Y"  style="width: 8%"></s:input></td>
				 		</tr>
				 		<tr>
					 		<td><label id="lblJioDestination" style=" display: inline-block;width: 100%;text-align: left;">SMS/Email</label></td>
					 		<td><s:input  type="text"  id="txtJioDestination" path="" cssStyle="width:120px;" cssClass="longTextBox jQKeyboard form-control"  /> </td>
				 		</tr>
				 		<tr>
					 		<td></td>
					 		<td><input type="button" id="btnCheckStatus" value="Check Status" style="width: 85px; height:25px;"  onclick="funJioCheckStatusClicked(this)"></input> </td>
					 		
				 		</tr>
			 		</table>
				</div>
			 	
			 	<!-- Extra .. bill no and Tip -->
			 	<div id="divExtraFileds" style=" border: 0px solid #ccc;height:50px;width:350px;display :block; position:absolute; top:330px;left:660px;" >
			 		<table style="width:100%;height:100%;font-size:11px; font-weight: bold;">
				 		<tr>
				 			<td><label id="lblManualBillNO" style=" display: inline-block;width: 90%;text-align: left;">Manual Bill No.</label></td>
				 			<td><s:input type="text"  id="txtManualBillNo" path="strManualBillNo" cssStyle="width:80px;margin: 1px;padding: 13px;" cssClass="longTextBox jQKeyboard form-control"/></td>
				 			<td><label id="lblTip" style=" display: inline-block;width: 90%;text-align: left;">Tip</label></td>
				 			<td><s:input type="text"  id="txtTip" path="" cssStyle="width:70px;" cssClass="longTextBox jQKeyboard form-control"/></td>
				 		</tr>
				 		<tr>
				 			<td style="padding-right: 5px;"><label id="lblDelCharge" style=" display: inline-block;width: 90%;text-align: left;">Del Charge</label></td>
				 			<td style="padding-right: 5px;" ><s:input type="text"  id="txtDeliveryCharge" path="dblDeliveryCharges" value="0.00" cssStyle="width:80px; text-align: right;margin: 1px;" cssClass="longTextBox jQKeyboard form-control" onfocus="this.value=''" onfocusout="this.value='0.00'"  /></td>
				 			<td style="padding-right: 5px;"><label id="lblDelBoyname" style=" display: inline-block;width: 90%;text-align: left;"></label></td>
				 			<td style="padding-right: 5px;"><input type="button" id="btnApplyDeliveryCharge" value="OK" style="width: 45px; height:25px;"  onclick="funbtnApplyDeliveryChargeClicked(this)" class="btn btn-primary btn-sm" ></input></td>
				 		</tr>
				 	</table>
			 	</div>
			 	<!--Div Tbl Settlement  -->
				 <div id="divTblSettlement" style=" border: 0px solid #ccc;height:100px;width:350px; position:absolute; top:480px;left:660px;overflow-x: auto; overflow-y: auto;" >
				 	<table id="tblSettlement" class="transTable" style="width: 98%; height:98%; border-collapse: collapse;overflow: auto;margin-left: auto;margin-right: auto;
							font-size: 10px;font-weight: bold;">
				 		<tr>
						 	<th>Settlement Code</th>
						 	<th>Settlement Name</th>
						 	<th>Amount</th>
						 	<th>Card No</th>
						 	<th>Expiry Date</th>
						 	<th>Paid Amt</th>
						 	<th>Coupon Remark</th>
						 	<th>Actual Amt</th>
						 	<th>Refund Amt</th>
				 		</tr>
				 	</table>
				 </div>	
				  <div id="divScrTaxTable" style=" border: 0px solid #ccc;height:100px;width:350px; position:absolute; top:480px;left:660px;overflow-x: auto; overflow-y: auto;" >
				 	<table id="tblSettlement" style="width: 98%; height:98%; border-collapse: collapse;overflow: auto;margin-left: auto;margin-right: auto;
							font-size: 10px;font-weight: bold;">
				 		<tr>
						 	<th>Tax Code</th>
						 	<th>Tax Name</th>
						 	<th>Taxble Amt</th>
						 	<th>Tax Amt</th>
						 	<th>Item Code</th>
						 	<th>Tax Calculation</th>
				 		</tr>
				 	</table>
				 </div>	
				 <!-- div Extra 2 -->
				 <div id="divExtra2" style="border: 0px solid #ccc; height:170px; width: 115px; position:absolute;top:345px;left:660px; ">
				 	
				 	<label id="lblRefund" style=" display: block;width: 70px;text-align: left;"> </label>
				 	
				 </div>
			 	<!--Div for numeric pad  -->
			 	<div id="divNumericPad" style="border: 0px solid #ccc; height:170px; width: 235px; position:absolute;top:410px;left:780px; ">
				 	<table id="tblNumricbtn" cellpadding="0" cellspacing="2">
				 	<tr>
					 	<td width=45px style="padding-top: 3px;" >
					 		<input type="button" id="btn7" value="7" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td  width=45px style="padding-top: 3px;" >
					 		<input type="button" id="btn8" value="8" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td width=45px style="padding-top: 3px;" >
					 		<input type="button" id="btn9"  value="9" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td  width=45px style="padding-top: 3px;">
					 		<input type="button"  id="btnc"  value="c" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td  width=45px style="padding-top: 3px;" >
					 		<input type="button"  id="btn10"  value="10" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
				 	</tr>
				 	<tr>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn4"  value="4" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info"></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn5"  value="5" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn6"  value="6" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn0"  value="0" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn20"  value="20" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
				 	</tr>
				 	<tr>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn1"  value="1" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn2"  value="2" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn3"  value="3" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn00"  value="00" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td style="padding-top: 3px;" >
					 		<input type="button"  id="btn100"  value="100" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
				 	</tr>
				 	<tr>
					 	<td width=45px style="padding-top: 3px;" >
					 		<input type="button"  id="btnDot"  value="." style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td colspan="3" width=45px style="padding-top: 3px;" >
					 		<input type="button"  id="btnBackSpace"  value="X" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	
					 		<input type="button"  id="btnEnter"  value="Enter" style="width: 85px; height:40px;"  onclick="funEnterButtonClicked()" class="btn btn-outline-info" ></input>
					 	</td>
					 	<td width=45px style="padding-top: 3px;" >
					 		<input type="button"  id="btn500"  value="500" style="width: 40px; height:40px;"  onclick="funNumpadButtonClicked(this)" class="btn btn-outline-info" ></input>
					 	</td>
				 	</tr>
				 	</table>
			 	</div>
			 	
			 	<div id="divBottomButtons" style=" border: 0px solid #ccc;height:70px;width:240px;display :block; position:absolute; top:570px;" >
					 <table>
						 <tr>
						 	<td style="padding-right: 5px;" ><input type="button" id="btnBack" value="BACK" style="width: 60px; height:40px;"  onclick="funBackButtonClicked(this)" class="btn btn-outline-success" ></input></td>
						 	<td style="padding-right: 5px;"><input type="submit" id="btnPrint"  value="SAVE" style="width: 60px; height:40px;" onclick="return funSaveBtnClicked()" class="btn btn-outline-success"></input></td>
						 	<td style="padding-right: 5px;"><input type="button" id="btnSettle" name = "settleBill" value="Need To Remove" style="width: 130px; height:40px; display:none" class="btn btn-outline-success"></input></td>
						 	<td style="padding-right: 5px;"><input type="button" id="btnGetOffer" value="CHECK OFFER" style="width: 80px; height:40px;display:block"  onclick="funGetOfferButtonClicked(this)" class="btn btn-outline-success"></input></td>			 	
						 </tr> 
						 
						 <tr>
							 <td colspan="3" style="padding: 5px;"><input type="button" id="btnOpenBillItems" value="SHOW BILL ITEMS" style="width: 150px; height:40px;"  onclick="funOpenBillItemButtonClicked(this)" class="btn btn-outline-info"></input></td>
						 </tr>
					 </table>
			 	</div>
			</div>
		</td>
	
</tr>
</table>
</div>
<div id="divFillDisountList" style=" height:0px;width:0px; display:none;" >
	<table id="tblFillDisountList" class="transTable">
					</table>
</div>
                <s:hidden id="hidSubTotal" path="dblSubTotal"/>
		 		<s:hidden id="hidDiscountTotal" path="dblDicountTotal"/>
		 		<s:hidden id="hidNetTotal" path="dblNetTotal"/>
		 		<s:hidden id="hidGrandTotal" path="dblGrandTotal"/>
		 		<s:hidden id="hidRefund" path="dblRefund"/>
		 	    <s:hidden id="hidTaxTotal" path="dblTotalTaxAmt"/>
 		 		<s:hidden id="hidSettlemnetType" path="strSettlementType"/> 
 		 		<s:hidden id="hidBalanceAmt" path=""/> 
 		 		
 		 		
 		 		<s:hidden id="hidCustMobileNo" path="custMobileNo"/>
		 		<s:hidden id="hidCustomerCode" path="strCustomerCode"/>
		 		<s:hidden id="hidCustomerName" path="customerName"/>
		 		<s:hidden id="hidOperationType" path="operationType"/>
		 		<s:hidden id="hidTransactionType" path="transactionType"/>
		 		<s:hidden id="hidTakeAway" path="takeAway"/>
		 		<s:hidden id="hidDeliveryBoyCode" path="strDeliveryBoyCode"/>
		 		<s:hidden id="hidDeliveryBoyName" path="strDeliveryBoyName"/>
		 		<s:hidden id="hidTableNo" path="strTableNo"/>
				<s:hidden id="hidWaiterNo" path="strWaiter"/>
				<s:hidden id="hidAreaCode" path="strAreaCode"   />
				<s:hidden id="hidBillNo" path="strBillNo"   />
				
				
				
				
				

<%-- 		 		<s:hidden id="hidDeliveryBoyName" path="strDeliveryBoyName"/> --%>
</s:form>


</body>
</html>