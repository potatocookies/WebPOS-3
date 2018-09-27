package com.sanguine.base.dao;

import java.math.BigInteger;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("clsSetupDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSetupDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;

	@SuppressWarnings("finally")
	public JSONObject funGetParameterValuePOSWise(String clientCode, String posCode, String parameterName)
	{
		JSONObject objJsonObject=new JSONObject();
		
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
				
				case "gNoOfDecimalPlace" : columnName="dblNoOfDecimalPlace";
				break;
				
				
				
				
				
			}
			
			
			
			SQLQuery query=webPOSSessionFactory.getCurrentSession().createSQLQuery("select "+columnName+" from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			
			List list=query.list();				
			
			if(list!=null && list.size()>0)
			{				
				objJsonObject.put(parameterName, list.get(0));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}			
	}
	
	
	
	@SuppressWarnings("finally")
	public JSONObject funGetAllParameterValuesPOSWise(String clientCode, String posCode)
	{
		JSONObject objJsonObject=new JSONObject();
		
		try
		{
							
			SQLQuery query=webPOSSessionFactory.getCurrentSession().createSQLQuery("select * from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			
			List list=query.list();				
			
			if(list!=null && list.size()>0)
			{				
				objJsonObject.put("gClientCode", list.get(0));//clientCode
				objJsonObject.put("gClientName", list.get(1));//clientName
				objJsonObject.put("gClientAddress1", list.get(2));
				objJsonObject.put("gClientAddress2", list.get(3));
				objJsonObject.put("gClientAddress3", list.get(4));
				objJsonObject.put("gClientEmail", list.get(5));
				objJsonObject.put("gBillFooter", list.get(6));
				objJsonObject.put("gBillPaperSize", list.get(8));
				objJsonObject.put("gNegBilling", list.get(9));
				
				objJsonObject.put("chkDayEnd", list.get(10));
				objJsonObject.put("strBillPrintMode", list.get(11));
			//	objJsonObject.put("gBillPaperSize", list.get(12));
				
				objJsonObject.put("gCityName", list.get(13));
				objJsonObject.put("gStateName", list.get(14));
				objJsonObject.put("gCountryName", list.get(15));
				objJsonObject.put("gClientTelNo", list.get(16));
				objJsonObject.put("gMobileNoForSMS", list.get(16));
				objJsonObject.put("gStartDate", list.get(17));
				objJsonObject.put("gEndDate", list.get(18));
				objJsonObject.put("gEndTime", list.get(18));
				objJsonObject.put("gNatureOfBusinnes", list.get(19));
				objJsonObject.put("gMultiBillPrint", list.get(20));
				objJsonObject.put("gEnableKOT", list.get(21));
				
				objJsonObject.put("gEffectOnPSP", list.get(22));
				objJsonObject.put("gPrintVatNo", list.get(23));
				objJsonObject.put("gVatNo", list.get(24));
				objJsonObject.put("gShowBill", list.get(25));
				objJsonObject.put("gPrintServiceTaxNo", list.get(26));
				objJsonObject.put("gServiceTaxNo", list.get(27));
				objJsonObject.put("gManualBillNo", list.get(28));
				objJsonObject.put("gMenuItemSequence", list.get(29));
				objJsonObject.put("gSenderEmailId", list.get(30));
				objJsonObject.put("gSenderMailPassword", list.get(31));
				
			//	objJsonObject.put("gBillPaperSize", list.get(32));
				
				objJsonObject.put("gEmailMessage", list.get(33));
				objJsonObject.put("gEmailServerName", list.get(34));
				objJsonObject.put("gSMSApi", list.get(35));
			/*	
				objJsonObject.put("gBillPaperSize", list.get(36));
				objJsonObject.put("gBillPaperSize", list.get(37));
				objJsonObject.put("gBillPaperSize", list.get(38));
				objJsonObject.put("gBillPaperSize", list.get(39));
				*/
				objJsonObject.put("gHOPOSType", list.get(40));
				objJsonObject.put("gSanguineWebServiceURL", list.get(41));
				objJsonObject.put("gDataSendFrequency", list.get(42));
				objJsonObject.put("gLastModifiedDate", list.get(43));
				objJsonObject.put("gRFIDInterface", list.get(44));
				objJsonObject.put("gRFIDDBServerName", list.get(45));
				objJsonObject.put("gRFIDDBUserName", list.get(46));
				objJsonObject.put("gRFIDDBPassword", list.get(47));
				objJsonObject.put("gRFIDDBName", list.get(48));
				objJsonObject.put("gKOTPrintingEnableForDirectBiller", list.get(49));
				
				objJsonObject.put("pinCode", list.get(50));
				
				objJsonObject.put("gTheme", list.get(51));
				objJsonObject.put("gMaxDiscount", list.get(52));
				objJsonObject.put("gAreaWisePricing", list.get(53));
				objJsonObject.put("gMenuItemSortingOn", list.get(54));
				objJsonObject.put("gDirectAreaCode", list.get(55));
				objJsonObject.put("gColumnSize", list.get(56));
				objJsonObject.put("gPrintType", list.get(57));
				objJsonObject.put("gEditHDCharges", list.get(58));
				objJsonObject.put("gSlabBasedHDCharges", list.get(59));
				
			//	objJsonObject.put("gBillPaperSize", list.get(60));
				
				objJsonObject.put("gSkipWaiter", list.get(61));
				objJsonObject.put("gDirectKOTPrintingFromMakeKOT", list.get(62));
				objJsonObject.put("gSkipPax", list.get(63));
				objJsonObject.put("gCRMInterface", list.get(64));
				objJsonObject.put("gGetWebserviceURL", list.get(65));
				objJsonObject.put("gPostWebserviceURL", list.get(66));
				objJsonObject.put("gOutletUID", list.get(67));
				objJsonObject.put("gPOSID", list.get(68));
				objJsonObject.put("gStockInOption", list.get(69));
				
				objJsonObject.put("custSeries", list.get(70));
				
				objJsonObject.put("gAdvRecPrintCount", list.get(71));
				objJsonObject.put("gHomeDeliverySMS", list.get(72));
				objJsonObject.put("gBillSettlementSMS", list.get(73));
				objJsonObject.put("gBillFormatType", list.get(74));
				objJsonObject.put("gActivePromotions", list.get(75));
				objJsonObject.put("gHomeDelSMSYN", list.get(76));
				objJsonObject.put("gBillSettleSMSYN", list.get(77));
				objJsonObject.put("gSMSType", list.get(78));
				objJsonObject.put("gPrintShortNameOnKOT", list.get(79));
				objJsonObject.put("gCustHelpOnTrans", list.get(80));
				objJsonObject.put("gPrintOnVoidBill", list.get(81));
				objJsonObject.put("gPostSalesDataToMMS", list.get(82));
				objJsonObject.put("gCustAreaCompulsory", list.get(83));
				objJsonObject.put("gPriceFrom", list.get(84));
				objJsonObject.put("gShowPrinterErrorMsg", list.get(85));
				
			//	objJsonObject.put("gBillPaperSize", list.get(86));
				
				objJsonObject.put("gCardIntfType", list.get(87));
				objJsonObject.put("gCMSIntegrationYN", list.get(88));
				objJsonObject.put("gCMSWebServiceURL", list.get(89));
				objJsonObject.put("gChangeQtyForExternalCode", list.get(90));
				objJsonObject.put("gPointsOnBillPrint", list.get(91));
				objJsonObject.put("gCMSPOSCode", list.get(92));
				objJsonObject.put("gCompulsoryManualAdvOrderNo", list.get(93));
				objJsonObject.put("gPrintManualAdvOrderNoOnBill", list.get(94));
				objJsonObject.put("gPrintModQtyOnKOT", list.get(95));
				objJsonObject.put("gNoOfLinesInKOTPrint", list.get(96));
				objJsonObject.put("gMultipleKOTPrint", list.get(97));
				objJsonObject.put("gItemQtyNumpad", list.get(98));
				objJsonObject.put("gTreatMemberAsTable", list.get(99));
				objJsonObject.put("gPrintKotToLocaPrinter", list.get(100));
				
			//	objJsonObject.put("gBillPaperSize", list.get(101));
				
				objJsonObject.put("gEnableSettleBtnForDirectBiller", list.get(102));
				objJsonObject.put("gDelBoyCompulsoryOnDirectBiller", list.get(103));
				objJsonObject.put("gCMSMemberCodeForKOTJPOS", list.get(104));
				objJsonObject.put("gCMSMemberCodeForKOTMPOS", list.get(105));
				objJsonObject.put("gDontShowAdvOrderInOtherPOS", list.get(106));
				objJsonObject.put("gPrintZeroAmtModifierOnBill", list.get(107));
				objJsonObject.put("gPrintKOTYN", list.get(108));
				objJsonObject.put("gCreditCardSlipNo", list.get(109));
				objJsonObject.put("gCreditCardExpiryDate", list.get(110));
				objJsonObject.put("gSelectWaiterFromCardSwipe", list.get(111));
				objJsonObject.put("gMultiWaiterSelOnMakeKOT", list.get(112));
				objJsonObject.put("gMoveTableToOtherPOS", list.get(113));
				objJsonObject.put("gMoveKOTToOtherPOS", list.get(114));
				objJsonObject.put("gCalculateTaxOnMakeKOT", list.get(115));
				objJsonObject.put("gReceiverEmailIds", list.get(116));
				objJsonObject.put("gItemWiseDiscount", list.get(117));
				objJsonObject.put("gRemarksOnTakeAway", list.get(118));
				objJsonObject.put("gShowItemStkColumnInDB", list.get(119));
				objJsonObject.put("gItemType", list.get(120));
				objJsonObject.put("gAllowNewAreaMasterFromCustMaster", list.get(121));
				objJsonObject.put("gCustAddressSelectionForBill", list.get(122));
				objJsonObject.put("gGenrateMI", list.get(123));
				objJsonObject.put("gFTPAddress", list.get(124));
				objJsonObject.put("gFTPServerUserName", list.get(125));
				objJsonObject.put("gFTPServerPass", list.get(126));
				objJsonObject.put("gAllowToCalculateItemWeight", list.get(127));
				objJsonObject.put("gShowBillsType", list.get(128));
				objJsonObject.put("gPrintTaxInvoice", list.get(129));
				objJsonObject.put("gPrintInclusiveOfAllTaxes", list.get(130));
				objJsonObject.put("gApplyDiscountOn", list.get(131));
				objJsonObject.put("gMemberCodeForKotInMposByCardSwipe", list.get(132));
				objJsonObject.put("gPrintBillYN", list.get(133));
				objJsonObject.put("gUseVatAndServiceTaxFromPos", list.get(134));
				objJsonObject.put("gMemberCodeForMakeBillInMPOS", list.get(135));
				objJsonObject.put("gItemWiseKOTPrintYN", list.get(136));
				objJsonObject.put("gLastPOSForDayEnd", list.get(137));
				objJsonObject.put("gCMSPostingType", list.get(138));
				objJsonObject.put("gPopUpToApplyPromotionsOnBill", list.get(139));
				objJsonObject.put("gSelectCustomerCodeFromCardSwipe", list.get(140));
				objJsonObject.put("gCheckDebitCardBalanceOnTrans", list.get(141));
				objJsonObject.put("gPickSettlementsFromPOSMaster", list.get(142));
				objJsonObject.put("gEnableShiftYN", list.get(143));
				objJsonObject.put("gProductionLinkup", list.get(144));
				objJsonObject.put("gLockDataOnShiftYN", list.get(145));
				objJsonObject.put("gWSClientCode", list.get(146));
				
			//	objJsonObject.put("", list.get(147));
				
				objJsonObject.put("gEnableBillSeries", list.get(148));
				objJsonObject.put("gEnablePMSIntegrationYN", list.get(149));
				objJsonObject.put("gPrintTimeOnBillYN", list.get(150));
				objJsonObject.put("gPrintTDHItemsInBill", list.get(151));
				objJsonObject.put("gPrintRemarkAndReasonForReprint", list.get(152));
				objJsonObject.put("daysBeforeVoidAdvOrder", list.get(153));
				objJsonObject.put("gNoOfDelDaysForAdvOrder", list.get(154));
				objJsonObject.put("gNoOfDelDaysForUrgentOrder", list.get(155));
				objJsonObject.put("gSetUpToTimeForAdvOrder", list.get(156));
				objJsonObject.put("gSetUpToTimeForUrgentOrder", list.get(157));
				objJsonObject.put("gUpToTimeForAdvOrder", list.get(158));
				objJsonObject.put("gUpToTimeForUrgentOrder", list.get(159));
				objJsonObject.put("gEnablePrintAndSettleBtnForDB", list.get(160));
				objJsonObject.put("gInrestoPOSIntegrationYN", list.get(161));
				objJsonObject.put("gInrestoPOSWebServiceURL", list.get(162));
				objJsonObject.put("gInrestoPOSId", list.get(163));
				objJsonObject.put("gInrestoPOSKey", list.get(164));
				objJsonObject.put("flgCarryForwardFloatAmtToNextDay", list.get(165));
				objJsonObject.put("gOpenCashDrawerAfterBillPrintYN", list.get(166));
				objJsonObject.put("gPropertyWiseSalesOrderYN", list.get(167));
				
			//	objJsonObject.put("", list.get(168));
				
				objJsonObject.put("gShowItemDetailsGrid", list.get(169));
				
				objJsonObject.put("ShowPopUpForNextItemQuantity", list.get(170));
				
				objJsonObject.put("gJioPOSIntegrationYN", list.get(171));
				objJsonObject.put("gJioPOSWesServiceURL", list.get(172));
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}			
	}

	@SuppressWarnings("finally")
	public void funSetToken(String getToken,String posCode,String strJioMID)
	    {
	        // TODO add your handling code here:
	        try
	        {
	            String sql = "update tblsetup set strJioActivationCode='" + getToken + "' "
	                    + "where strPOSCode='" + posCode + "' and strJioMID='" + strJioMID + "'";
	            Query  querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     		    querySql.executeUpdate(); 

	        }
	        catch (Exception ex)
	        {

	        }
	    }
	

		@SuppressWarnings("finally")
		public JSONObject funGetPos(String newPropertyPOSCode)
		{
			List list =null;
			JSONObject jObjTableData=new JSONObject();
		       try
		        {
		        	  String sqlBillSeries = "select count(*) from tblsetup where strPOSCode='" + newPropertyPOSCode + "' ";
		              Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillSeries);
					  list = query.list();
					
						BigInteger count=new BigInteger("0");
						
								Object obj=(Object)list.get(0);
								count=(BigInteger) obj;
							
							
				          	jObjTableData.put("count", count);
					
		     	}catch(Exception ex)
				{
					ex.printStackTrace();
					
				}
				finally
				{
					return jObjTableData;
				}
		}
		
		public JSONObject funGetPOSClientCode()
		{
			List list =null;
			JSONObject jObjClientCode=new JSONObject();
		       try
		        {
					String sqlSetUpClientCode = "select DISTINCT a.strClientCode from tblsetup a ";
		            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSetUpClientCode);
						
					list = query.list();
					if(list.size()>0)
					{
						Object obj=(Object)list.get(0);
						String strClientCode=(String) obj;
						jObjClientCode.put("strClientCode", strClientCode);
						
					}else
					{
						jObjClientCode.put("strClientCode", "");
					}
						
		        }catch(Exception ex)
				{
					ex.printStackTrace();
					jObjClientCode.put("strClientCode", "");
					
				}
				finally
				{
					return jObjClientCode;
				}
		}
}
