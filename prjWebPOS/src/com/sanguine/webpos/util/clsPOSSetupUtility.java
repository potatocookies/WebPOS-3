package com.sanguine.webpos.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.base.service.clsBaseServiceImpl;

/*
@Author Vinayak 
*/
@Controller
public class clsPOSSetupUtility {

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	public String funGetParameterValuePOSWise(String clientCode, String posCode, String parameterName)
	{
		String strParameterValue="";
		try
		{
			String columnName="";
			switch (parameterName)
			{
				case "gClientCode":columnName="strClientCode";						
					break;
					
				case "gClientName":columnName="strClientName";						
					break;							                
		                
				case "gClientAddress1":columnName="strAddressLine1";						
				break;	
				
				case "gClientAddress2":columnName="strAddressLine2";						
				break;	
				
				case "gClientAddress3":columnName="strAddressLine3";						
				break;	
				
				case "gClientEmail":columnName="strEmail";						
				break;	
				
				case "gBillFooter":columnName="strBillFooter";						
				break;	
				
				case "gBillPaperSize":columnName="intBillPaperSize";						
				break;	
				
				case "gRFIDInterface":columnName="strRFID";						
				break;
				
				case "gEnableShiftYN":columnName="strShiftWiseDayEndYN";						
				break;	
				
				case "gEnableBillSeries":columnName="strEnableBillSeries";						
				break;	
				  
				case "gShowBillsType":columnName="strShowBillsDtlType";						
				break;	
				
				case "gCMSIntegrationYN":columnName="strCMSIntegrationYN";						
				break;
				
				case "gTransactionType":columnName="strCMSIntegrationYN";						
				break;
				
				case "gPickSettlementsFromPOSMaster":columnName="strSettlementsFromPOSMaster";						
				break;
				
				case "gEnablePMSIntegrationYN":columnName="strEnablePMSIntegrationYN";						
				break;
				
				case "gActivePromotions":columnName="strActivePromotions";						
				break;
					
				case "gTreatMemberAsTable":columnName="strTreatMemberAsTable";						
				break;
				
				case "gJioMoneyActivationCode":columnName="strJioActivationCode";						
				break;
								
				case "gPopUpToApplyPromotionsOnBill":columnName="strPopUpToApplyPromotionsOnBill";						
				break;
			
				case "gCalculateTaxOnMakeKOT":columnName="strCalculateTaxOnMakeKOT";						
				break;
				
				case "gMultiWaiterSelOnMakeKOT":columnName="strMultiWaiterSelectionOnMakeKOT";						
				break;
				
				case "gSelectWaiterFromCardSwipe":columnName="strMultiWaiterSelectionOnMakeKOT";						
				break;
				
				case "gSkipPax":columnName="strSkipPax";						
				break;
				
				case "gSkipWaiter":columnName="strSkipWaiter";						
				break;
				
				case "gDirectAreaCode":columnName="strDirectAreaCode";						
				break;
				
				case "gMenuItemSortingOn":columnName="strMenuItemSortingOn";						
				break;
				
				case "gAreaWisePricing":columnName="strAreaWisePricing";						
				break;
				
				case "gCheckDebitCardBalanceOnTrans":columnName="strCheckDebitCardBalOnTransactions";						
				break;
				
				case "gCMSMemberCodeForKOTJPOS":columnName="strCMSMemberForKOTJPOS";						
				break;
				
				case "gInrestoPOSIntegrationYN":columnName="strInrestoPOSIntegrationYN";						
				break;
				
				case "gCRMInterface":columnName="strCRMInterface";						
				break;
				
				case "gGetWebserviceURL":columnName="strGetWebserviceURL";						
				break;
				
				case "gOutletUID":columnName="strOutletUID";						
				break;
				
				case "gCustAddressSelectionForBill":columnName="strCustAddressSelectionForBill";						
				break;
				
				case "gPrintType":columnName="strPrintType";						
				break;
				
				case "gCustAreaCompulsory":columnName="strCustAreaMasterCompulsory";						
				break;
				
				case "gNoOfLinesInKOTPrint" : columnName = "strNoOfLinesInKOTPrint";
				break;
				
				case "gShowBill" : columnName = "strShowBill";
				break;
				
				case "gPrintKOTYN" : columnName = "strPrintKOTYN";
				break;
				
				case "gMultipleKOTPrint" : columnName="strMultipleKOTPrintYN";
				break;
				
				case "gMultiBillPrint" : columnName = "strMultipleBillPrinting";
				break;
				
				case "gShowPrinterErrorMsg" : columnName = "strShowPrinterErrorMessage";
				break;
				
				case "gAdvReceiptPrinterPort" : columnName = "strOpenCashDrawerAfterBillPrintYN";
				break;
				
				case "gBillFormatType" : columnName = "strBillFormatType";
				break;
				
				case "gHOPOSType" : columnName ="strPOSType";
				break;
				
				case "gPrintTaxInvoice" : columnName="strPrintTaxInvoiceOnBill";
				break;
				
				case "gCityName" : columnName="strCityName";
				break;
				
				case "gPrintTimeOnBillYN" : columnName="strPrintTimeOnBill";
				break;
				
				case "gPrintZeroAmtModifierOnBill" : columnName="strPrintZeroAmtModifierInBill";
				break;
				
				case "gUseVatAndServiceTaxFromPos" : columnName="strVatAndServiceTaxFromPos";
				break;
				
				case "gPrintVatNo" : columnName="strPrintVatNo";
				break;
				
				case "gVatNo" : columnName="strVatNo";
				break;
				
				case "gPrintServiceTaxNo" : columnName="strPrintServiceTaxNo";
				break;
				
				case "gServiceTaxNo" : columnName="strServiceTaxNo";
				break;
				
				case "gClientTelNo" :columnName="intTelephoneNo";
				break;

				case "gBillSettleSMSYN" : columnName = "strSendBillSettlementSMS";
				break;

				case "gPrintShortNameOnKOT" : columnName ="strPrintShortNameOnKOT";
				break;
				
				case "gColumnSize" : columnName = "intColumnSize";
				break;
				
				case "gPrintModQtyOnKOT" : columnName ="strPrintModifierQtyOnKOT";
				break;
				
				case "gReceiverEmailIds" : columnName ="strReceiverEmailId";
				break;
				
				case "gLastPOSForDayEnd" : columnName ="strLastPOSForDayEnd";
				break;
				
				case "gSenderEmailId" : columnName ="strSenderEmailId";
				break;
				
				case "gCMSPostingType" : columnName ="strCMSPostingType";
				break;
				
				case "flgCarryForwardFloatAmtToNextDay" : columnName ="strCarryForwardFloatAmtToNextDay";
				break;
				
				case "gSenderMailPassword" : columnName ="strEmailPassword";
				break;
				
				case "gPostSalesDataToMMS" : columnName ="strPostSalesDataToMMS";
				break;
				
				case "gItemType" : columnName ="strItemType";
				break;
				
				case "gOpenCashDrawerAfterBillPrintYN"  : columnName ="strOpenCashDrawerAfterBillPrintYN";
				break;
				
				case "gSMSType"  : columnName ="strSMSType";
				break;
				
				case "gSMSApi"  : columnName ="strSMSApi";
				break;
				
				case "gAreaWisePromotions"  : columnName ="strAreaWisePromotions";
				break;
				
				case "gSlabBasedHDCharges"  : columnName ="strSlabBasedHDCharges";
				break;
				
				
			}
			
			StringBuilder sqlQuery= new StringBuilder("select "+columnName+" from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			
			List list=objBaseServiceImpl.funGetList(sqlQuery, "sql");				
			if(list!=null && list.size()>0)
			{	
				strParameterValue=String.valueOf(list.get(0).toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return strParameterValue;
		}		
		
	}
	
}
