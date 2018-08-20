package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSeriesDtlBean;
import com.sanguine.webpos.bean.clsPOSPropertySetupBean;
import com.sanguine.webpos.bean.clsPOSPrinterSetupBean;
import com.sanguine.webpos.model.clsBillSeriesHdModel;
import com.sanguine.webpos.model.clsBillSeriesModel_ID;
import com.sanguine.webpos.model.clsPrinterSetupHdModel;
import com.sanguine.webpos.model.clsPrinterSetupModel_ID;
import com.sanguine.webpos.model.clsSetupHdModel;
import com.sanguine.webpos.model.clsSetupModel_ID;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSPropertySetupController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	clsPOSUtilityController obUtilityController; 
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private intfBaseService objBaseService;
	Date dte= new Date();
	int yy = dte.getYear() + 1900;
    int mm = dte.getMonth() + 1;
    int dd = dte.getDate();
    String dteEndDate = yy + "-" + mm + "-" + dd;
    boolean JioDeviceIDFound=false;
    String JioDeviceIDFromDB="";
    
	@RequestMapping(value = "/frmPOSPropertySetup", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSPropertySetupBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		
		Map mapPOS=new HashMap();
		Map mapPOSForDayEnd=new HashMap();
		Map mapArea=new HashMap();
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String posCode=request.getSession().getAttribute("loginPOS").toString();

		
		 JSONArray jArrList=new JSONArray();
		 jArrList =objPOSGlobal.funGetAllPOSForMaster(clientCode);
		 mapPOS.put("All","All");
		 if(jArrList!=null)
		 {
			for(int i =0 ;i<jArrList.size();i++)
			{
				JSONObject josnObjRet = (JSONObject) jArrList.get(i);
				mapPOSForDayEnd.put( josnObjRet.get("strPosCode"),josnObjRet.get("strPosName"));
				mapPOS.put( josnObjRet.get("strPosCode"),josnObjRet.get("strPosName"));
			}
		 }
		model.put("posList",mapPOS);
		model.put("posListForDayEnd",mapPOSForDayEnd);
		
		jArrList=new JSONArray();
		jArrList =objPOSGlobal.funGetAllAreaForMaster(clientCode);
		mapArea.put("All","All");
		if(jArrList!=null)
		 {
			for(int i =0 ;i<jArrList.size();i++)
			{
				JSONObject josnObjRet = (JSONObject) jArrList.get(i);
			
				mapArea.put( josnObjRet.get("strAreaCode"),josnObjRet.get("strAreaName"));
			}
		 }
		model.put("areaList",mapArea);
		
		
	 jArrList=new JSONArray();
	 jArrList =objPOSGlobal.funGetAllAreaForMaster(clientCode);
	 if(jArrList!=null)
	 {
			for(int i =0 ;i<jArrList.size();i++)
			{
				JSONObject josnObjRet = (JSONObject) jArrList.get(i);
			
				mapArea.put( josnObjRet.get("strAreaCode"),josnObjRet.get("strAreaName"));
			}
	 }
	 		model.put("areaList",mapArea);
	
			return new ModelAndView("frmPOSPropertySetup");
	 
	}
	@RequestMapping(value = "/loadPOSWisePropertySetupData", method = RequestMethod.GET)
	public @ResponseBody clsPOSPropertySetupBean funSetPOSWiseData(@RequestParam("posCode") String posCode,HttpServletRequest request)
	{
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		 
		clsPOSPropertySetupBean objBean =new clsPOSPropertySetupBean();

		FileOutputStream fileOuputStream = null;
		clsSetupModel_ID ob=new clsSetupModel_ID(clientCode,posCode);
		clsSetupHdModel objSetupHdModel= new clsSetupHdModel();
		try{
			List list =objBaseService.funLoadAllPOSWise(objSetupHdModel,clientCode,posCode);
			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objSetupHdModel = (clsSetupHdModel) list.get(cnt);
				if(objSetupHdModel.getBlobReportImage()!=null){
					 	Blob blob = objSetupHdModel.getBlobReportImage(); 
					 	byte[] byteContent = blob.toString().getBytes();
					    String imagePath=servletContext.getRealPath("/resources/images");
						int blobLength = (int) blob.length();  

						fileOuputStream = new FileOutputStream(imagePath+"/imgClientImage.jpg");
						fileOuputStream.write(byteContent);
						fileOuputStream.close();
				}
	
				objBean.setStrPosCode(objSetupHdModel.getStrPOSCode());
				objBean.setStrPrintingType(objSetupHdModel.getStrPrintType());
				objBean.setIntColumnSize(objSetupHdModel.getIntColumnSize());
			    objBean.setDblMaxDiscount((long)(objSetupHdModel.getDblMaxDiscount()));
				objBean.setChkAreaWisePricing(objSetupHdModel.getStrAreaWisePricing());
				objBean.setStrChangeTheme(objSetupHdModel.getStrChangeTheme());
				objBean.setStrClientCode(objSetupHdModel.getStrClientCode());
				objBean.setStrClientName(objSetupHdModel.getStrClientName());
				objBean.setStrAddrLine1(objSetupHdModel.getStrAddressLine1());
				objBean.setStrAddrLine2(objSetupHdModel.getStrAddressLine2());
				objBean.setStrAddrLine3(objSetupHdModel.getStrAddressLine3());
				objBean.setStrEmail(objSetupHdModel.getStrEmail());
				objBean.setStrBillFooter(objSetupHdModel.getStrBillFooter());
				objBean.setIntBiilPaperSize((long)objSetupHdModel.getIntBillPaperSize());
				objBean.setChkNegBilling(objSetupHdModel.getStrNegativeBilling());
				//objJsonObject.put("gStartDate",objSetupHdModel.getDteStartDate());
			
				//objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
				objBean.setChkDayEnd(objSetupHdModel.getStrDayEnd());
				objBean.setStrBillPrintMode(objSetupHdModel.getStrPrintMode());
				objBean.setStrCity(objSetupHdModel.getStrCityName());
				objBean.setStrState(objSetupHdModel.getStrState());
				objBean.setStrCountry(objSetupHdModel.getStrCountry());
				objBean.setStrTelephone((long)objSetupHdModel.getIntTelephoneNo());
				objBean.setStrNatureOfBussness(objSetupHdModel.getStrNatureOfBusinnes());
				objBean.setChkMultiBillPrint(objSetupHdModel.getStrMultipleBillPrinting());
				objBean.setChkEnableKOT(objSetupHdModel.getStrEnableKOT());
				objBean.setChkEffectOnPSP(objSetupHdModel.getStrEffectOnPSP());
				objBean.setChkPrintVatNo(objSetupHdModel.getStrPrintVatNo());
				objBean.setStrVatNo(objSetupHdModel.getStrVatNo());
				objBean.setChkShowBills(objSetupHdModel.getStrShowBill());
				objBean.setChkServiceTaxNo(objSetupHdModel.getStrPrintServiceTaxNo());
				objBean.setStrServiceTaxNo(objSetupHdModel.getStrServiceTaxNo());
				objBean.setChkManualBillNo(objSetupHdModel.getStrManualBillNo());
				objBean.setStrMenuItemDisSeq(objSetupHdModel.getStrMenuItemDispSeq());
				objBean.setStrSenderEmailId(objSetupHdModel.getStrSenderEmailId());
				objBean.setStrEmailPassword(objSetupHdModel.getStrEmailPassword());
				//objJsonObject.put("gLastModifiedDate",objSetupHdModel.getDteHOServerDate());
				
				objBean.setStrBodyPart(objSetupHdModel.getStrBody());
				objBean.setStrEmailServerName(objSetupHdModel.getStrEmailServerName());
				objBean.setStrAreaSMSApi(objSetupHdModel.getStrSMSApi());
				objBean.setStrPOSType(objSetupHdModel.getStrPOSType());
				objBean.setStrWebServiceLink(objSetupHdModel.getStrWebServiceLink());
				objBean.setStrDataSendFrequency(objSetupHdModel.getStrDataSendFrequency());
				objBean.setStrRFIDSetup(objSetupHdModel.getStrRFID());
				objBean.setStrRFIDServerName(objSetupHdModel.getStrServerName());
				objBean.setStrRFIDUserName(objSetupHdModel.getStrDBUserName());
				objBean.setStrRFIDPassword(objSetupHdModel.getStrDBPassword());
				objBean.setStrRFIDDatabaseName(objSetupHdModel.getStrDatabaseName());
				objBean.setChkPrintKotForDirectBiller(objSetupHdModel.getStrEnableKOTForDirectBiller());
				objBean.setStrPinCode((long)objSetupHdModel.getIntPinCode());
				
			/*	objJsonObject.put("gTheme", objSetupHdModel.getStrChangeTheme());
				objJsonObject.put("gMaxDiscount",objSetupHdModel.getDblMaxDiscount());
				objJsonObject.put("gAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
				objJsonObject.put("gDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
				objJsonObject.put("gColumnSize", objSetupHdModel.getIntColumnSize());
				objJsonObject.put("gPrintType", objSetupHdModel.getStrPrintType());*/
				//	objJsonObject.put("gBillPaperSize", list.get(60));
				
				objBean.setStrMenuItemSortingOn(objSetupHdModel.getStrMenuItemSortingOn());
				objBean.setChkEditHomeDelivery(objSetupHdModel.getStrEditHomeDelivery());
				objBean.setChkSlabBasedHomeDelCharges(objSetupHdModel.getStrSlabBasedHDCharges());
				objBean.setChkSkipWaiterSelection(objSetupHdModel.getStrSkipWaiter());
				objBean.setChkDirectKOTPrintMakeKOT(objSetupHdModel.getStrDirectKOTPrintMakeKOT());
				objBean.setChkSkipPaxSelection(objSetupHdModel.getStrSkipPax());
				objBean.setStrCRM(objSetupHdModel.getStrCRMInterface());
				objBean.setStrGetWebservice(objSetupHdModel.getStrGetWebserviceURL());
				objBean.setStrPostWebservice(objSetupHdModel.getStrPostWebserviceURL());
				objBean.setStrOutletUID(objSetupHdModel.getStrOutletUID());
				objBean.setStrPOSID(objSetupHdModel.getStrPOSID());
				objBean.setStrStockInOption(objSetupHdModel.getStrStockInOption());
				objBean.setStrCustSeries(objSetupHdModel.getStrCustSeries());
				
				
				objBean.setStrAdvRecPrintCount((long)objSetupHdModel.getIntAdvReceiptPrintCount());
				objBean.setStrAreaSendHomeDeliverySMS(objSetupHdModel.getStrHomeDeliverySMS());
				objBean.setStrAreaBillSettlementSMS(objSetupHdModel.getStrBillStettlementSMS());
				objBean.setStrBillFormat(objSetupHdModel.getStrBillFormatType());
				objBean.setChkActivePromotions(objSetupHdModel.getStrActivePromotions());
				objBean.setChkHomeDelSMS(objSetupHdModel.getStrSendHomeDelSMS());
				objBean.setChkBillSettlementSMS(objSetupHdModel.getStrSendBillSettlementSMS());
				objBean.setStrSMSType(objSetupHdModel.getStrSMSType());
				objBean.setChkPrintShortNameOnKOT(objSetupHdModel.getStrPrintShortNameOnKOT());
				objBean.setChkPrintForVoidBill(objSetupHdModel.getStrPrintOnVoidBill());
				objBean.setChkPostSalesDataToMMS(objSetupHdModel.getStrPostSalesDataToMMS());
				objBean.setChkAreaMasterCompulsory(objSetupHdModel.getStrCustAreaMasterCompulsory());
				objBean.setStrPriceFrom(objSetupHdModel.getStrPriceFrom());
				objBean.setChkPrinterErrorMessage(objSetupHdModel.getStrShowPrinterErrorMessage());
							
				//objJsonObject.put("gCustHelpOnTrans",objSetupHdModel.getStrShowCustHelp());
				
				objBean.setChkChangeQtyForExternalCode(objSetupHdModel.getStrChangeQtyForExternalCode());
				objBean.setChkPointsOnBillPrint(objSetupHdModel.getStrPointsOnBillPrint());
				objBean.setStrCardIntfType(objSetupHdModel.getStrCardInterfaceType());
				objBean.setStrCMSIntegrationYN(objSetupHdModel.getStrCMSIntegrationYN());
				objBean.setStrCMSWesServiceURL(objSetupHdModel.getStrCMSWebServiceURL());
				objBean.setChkManualAdvOrderCompulsory(objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
				objBean.setChkPrintManualAdvOrderOnBill(objSetupHdModel.getStrManualAdvOrderNoCompulsory());
				objBean.setChkPrintModifierQtyOnKOT(objSetupHdModel.getStrPrintModifierQtyOnKOT());
				objBean.setIntNoOfLinesInKOTPrint((long)objSetupHdModel.getStrNoOfLinesInKOTPrint());
				objBean.setChkMultiKOTPrint(objSetupHdModel.getStrMultipleKOTPrintYN());
				objBean.setChkItemQtyNumpad(objSetupHdModel.getStrItemQtyNumpad());
				objBean.setChkMemberAsTable(objSetupHdModel.getStrTreatMemberAsTable());
				objBean.setChkPrintKOTToLocalPrinter(objSetupHdModel.getStrKOTToLocalPrinter());
				
				//objJsonObject.put("gCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
				
				objBean.setChkEnableSettleBtnForDirectBillerBill(objSetupHdModel.getStrSettleBtnForDirectBillerBill());
				objBean.setChkDelBoyCompulsoryOnDirectBiller(objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
				objBean.setChkMemberCodeForKOTJPOS(objSetupHdModel.getStrCMSMemberForKOTJPOS());
				objBean.setChkMemberCodeForKOTMPOS(objSetupHdModel.getStrCMSMemberForKOTMPOS());
				objBean.setChkDontShowAdvOrderInOtherPOS(objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
				objBean.setChkPrintZeroAmtModifierInBill(objSetupHdModel.getStrPrintZeroAmtModifierInBill());
				objBean.setChkPrintKOTYN(objSetupHdModel.getStrPrintKOTYN());
				objBean.setChkSlipNoForCreditCardBillYN(objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
				objBean.setChkExpDateForCreditCardBillYN(objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
				objBean.setChkSelectWaiterFromCardSwipe(objSetupHdModel.getStrSelectWaiterFromCardSwipe());
				objBean.setChkMultipleWaiterSelectionOnMakeKOT(objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
				objBean.setChkMoveTableToOtherPOS(objSetupHdModel.getStrMoveTableToOtherPOS());
				objBean.setChkMoveKOTToOtherPOS(objSetupHdModel.getStrMoveKOTToOtherPOS());
				objBean.setChkCalculateTaxOnMakeKOT(objSetupHdModel.getStrCalculateTaxOnMakeKOT());
				objBean.setStrReceiverEmailId(objSetupHdModel.getStrReceiverEmailId());
				objBean.setChkCalculateDiscItemWise(objSetupHdModel.getStrCalculateDiscItemWise());
				objBean.setChkTakewayCustomerSelection(objSetupHdModel.getStrTakewayCustomerSelection());
				objBean.setChkShowItemStkColumnInDB(objSetupHdModel.getStrShowItemStkColumnInDB());
				objBean.setStrItemType(objSetupHdModel.getStrItemType());
				objBean.setChkBoxAllowNewAreaMasterFromCustMaster(objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
				objBean.setChkSelectCustAddressForBill(objSetupHdModel.getStrCustAddressSelectionForBill());
				objBean.setChkGenrateMI(objSetupHdModel.getStrGenrateMI());
				objBean.setStrFTPAddress(objSetupHdModel.getStrFTPAddress());
				objBean.setStrFTPServerUserName(objSetupHdModel.getStrFTPServerUserName());
				objBean.setStrFTPServerPass(objSetupHdModel.getStrFTPServerPass());
				objBean.setChkAllowToCalculateItemWeight(objSetupHdModel.getStrAllowToCalculateItemWeight());
				objBean.setStrShowBillsDtlType(objSetupHdModel.getStrShowBillsDtlType());
				objBean.setChkPrintInvoiceOnBill(objSetupHdModel.getStrPrintTaxInvoiceOnBill());
				objBean.setChkPrintInclusiveOfAllTaxesOnBill(objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
				objBean.setStrApplyDiscountOn(objSetupHdModel.getStrApplyDiscountOn());
				objBean.setChkMemberCodeForKotInMposByCardSwipe(objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
				objBean.setChkPrintBill(objSetupHdModel.getStrPrintBillYN());
				objBean.setChkUseVatAndServiceNoFromPos(objSetupHdModel.getStrVatAndServiceTaxFromPos());
				objBean.setChkMemberCodeForMakeBillInMPOS(objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
				objBean.setChkItemWiseKOTPrintYN(objSetupHdModel.getStrItemWiseKOTYN());
				objBean.setStrPOSForDayEnd(objSetupHdModel.getStrLastPOSForDayEnd());
				objBean.setStrCMSPostingType(objSetupHdModel.getStrCMSPostingType());
				objBean.setChkPopUpToApplyPromotionsOnBill(objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
				objBean.setChkSelectCustomerCodeFromCardSwipe(objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
				objBean.setChkCheckDebitCardBalOnTrans(objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
				objBean.setChkSettlementsFromPOSMaster(objSetupHdModel.getStrSettlementsFromPOSMaster());
				objBean.setChkShiftWiseDayEnd(objSetupHdModel.getStrShiftWiseDayEndYN());
				objBean.setChkProductionLinkup(objSetupHdModel.getStrProductionLinkup());
				objBean.setChkLockDataOnShift(objSetupHdModel.getStrLockDataOnShift());
				objBean.setStrWSClientCode(objSetupHdModel.getStrWSClientCode());
				objBean.setChkEnableBillSeries(objSetupHdModel.getStrEnableBillSeries());
				objBean.setChkEnablePMSIntegration(objSetupHdModel.getStrEnablePMSIntegrationYN());
				objBean.setChkPrintTimeOnBill(objSetupHdModel.getStrPrintTimeOnBill());
				objBean.setChkPrintTDHItemsInBill(objSetupHdModel.getStrPrintTDHItemsInBill());
				objBean.setChkPrintRemarkAndReasonForReprint(objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
				objBean.setIntDaysBeforeOrderToCancel((long)objSetupHdModel.getIntDaysBeforeOrderToCancel());
				objBean.setIntNoOfDelDaysForAdvOrder((long)objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
				objBean.setIntNoOfDelDaysForUrgentOrder((long)objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
				objBean.setChkSetUpToTimeForAdvOrder(objSetupHdModel.getStrSetUpToTimeForAdvOrder());
				objBean.setChkSetUpToTimeForUrgentOrder(objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
				
				  String upToTimeForAdvOrder = (objSetupHdModel.getStrUpToTimeForAdvOrder()).split(" ")[0];
				  objBean.setStrHours(upToTimeForAdvOrder.split(":")[0].trim());
				  objBean.setStrMinutes(upToTimeForAdvOrder.split(":")[1].trim());
				  objBean.setStrAMPM((objSetupHdModel.getStrUpToTimeForAdvOrder()).split(" ")[1]);

				  String upToTimeForUrgentOrder = (objSetupHdModel.getStrUpToTimeForUrgentOrder()).split(" ")[0];
				  objBean.setStrHoursUrgentOrder(upToTimeForUrgentOrder.split(":")[0].trim());
				  objBean.setStrMinutesUrgentOrder(upToTimeForUrgentOrder.split(":")[1].trim());
				  objBean.setStrAMPMUrgent((objSetupHdModel.getStrUpToTimeForUrgentOrder()).split(" ")[1]);

				objBean.setChkEnableBothPrintAndSettleBtnForDB(objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
				objBean.setStrInrestoPOSIntegrationYN(objSetupHdModel.getStrInrestoPOSIntegrationYN());
				objBean.setStrInrestoPOSWesServiceURL(objSetupHdModel.getStrInrestoPOSWebServiceURL());
				objBean.setStrInrestoPOSId(objSetupHdModel.getStrInrestoPOSId());
				objBean.setStrInrestoPOSKey(objSetupHdModel.getStrInrestoPOSKey());
				objBean.setChkCarryForwardFloatAmtToNextDay(objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
				objBean.setChkOpenCashDrawerAfterBillPrint(objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
				objBean.setChkPropertyWiseSalesOrder(objSetupHdModel.getStrPropertyWiseSalesOrderYN());
				
				
				objBean.setChkShowItemDtlsForChangeCustomerOnBill(objSetupHdModel.getStrShowItemDetailsGrid());
				objBean.setChkShowPopUpForNextItemQuantity(objSetupHdModel.getStrShowPopUpForNextItemQuantity());
				objBean.setStrJioPOSIntegrationYN(objSetupHdModel.getStrJioMoneyIntegration());
				objBean.setStrJioPOSWesServiceURL(objSetupHdModel.getStrJioWebServiceUrl());
				objBean.setStrJioMID(objSetupHdModel.getStrJioMID());
				objBean.setStrJioTID(objSetupHdModel.getStrJioTID());
				objBean.setStrJioActivationCode(objSetupHdModel.getStrJioActivationCode());
				objBean.setStrJioDeviceID(objSetupHdModel.getStrJioDeviceID());
				if(!objSetupHdModel.getStrJioDeviceID().toString().isEmpty())
					{
					JioDeviceIDFound=true;
					JioDeviceIDFromDB=objSetupHdModel.getStrJioDeviceID().toString();
					}
				objBean.setChkNewBillSeriesForNewDay(objSetupHdModel.getStrNewBillSeriesForNewDay());
				objBean.setChkShowReportsPOSWise(objSetupHdModel.getStrShowReportsPOSWise());
				objBean.setChkEnableDineIn(objSetupHdModel.getStrEnableDineIn());
				objBean.setChkAutoAreaSelectionInMakeKOT(objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
			
				dteEndDate=objSetupHdModel.getDteEndDate();
			
			
			
				
			}
			
		}catch(Exception e){
			
		}
        
		return objBean;
	}
	
	@RequestMapping(value = "/funGetPos", method = RequestMethod.GET)
	public @ResponseBody String funGetPos(@RequestParam("posCode") String posCode,HttpServletRequest request)
	{
		 String count="0";
		 JSONObject jObj = objGlobal.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSSetup/funGetPos?posCode="+posCode);
		 try{
			 String sqlBillSeries = "select count(*) from tblsetup where strPOSCode='" + posCode + "' ";
			 List list=objBaseService.funGetList(new StringBuilder(sqlBillSeries), "sql");
				
			 if(list.size()>0){
				 count=list.get(0).toString();
			 }
		
		 }catch(Exception e ){
			 e.printStackTrace();
		 }
		return count;
	}
	
	@RequestMapping(value = "/loadPOSPropertySetupData", method = RequestMethod.GET)
	public @ResponseBody clsPOSPropertySetupBean funSetSearchFields(HttpServletRequest request)
	{
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		String posCode=request.getSession().getAttribute("loginPOS").toString();

		clsPOSPropertySetupBean objBean=new clsPOSPropertySetupBean();
		FileOutputStream fileOuputStream = null;
		clsSetupModel_ID ob=new clsSetupModel_ID(clientCode,posCode);
		clsSetupHdModel objSetupHdModel= new clsSetupHdModel();
		
		
		try{
			List list =objBaseService.funLoadAll(objSetupHdModel,clientCode);

			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objSetupHdModel = (clsSetupHdModel) list.get(cnt);
				if(objSetupHdModel.getBlobReportImage()!=null){
					 	Blob blob = objSetupHdModel.getBlobReportImage(); 
					 	byte[] byteContent = blob.toString().getBytes();
					    String imagePath=servletContext.getRealPath("/resources/images");
						int blobLength = (int) blob.length();  

						fileOuputStream = new FileOutputStream(imagePath+"/imgClientImage.jpg");
						fileOuputStream.write(byteContent);
						fileOuputStream.close();
				}
				
			}
			objBean.setStrPosCode(objSetupHdModel.getStrPOSCode());
			objBean.setStrPrintingType(objSetupHdModel.getStrPrintType());
			objBean.setIntColumnSize(objSetupHdModel.getIntColumnSize());
		    objBean.setDblMaxDiscount((long)(objSetupHdModel.getDblMaxDiscount()));
			objBean.setChkAreaWisePricing(objSetupHdModel.getStrAreaWisePricing());
			objBean.setStrChangeTheme(objSetupHdModel.getStrChangeTheme());
			objBean.setStrClientCode(objSetupHdModel.getStrClientCode());
			objBean.setStrClientName(objSetupHdModel.getStrClientName());
			objBean.setStrAddrLine1(objSetupHdModel.getStrAddressLine1());
			objBean.setStrAddrLine2(objSetupHdModel.getStrAddressLine2());
			objBean.setStrAddrLine3(objSetupHdModel.getStrAddressLine3());
			objBean.setStrEmail(objSetupHdModel.getStrEmail());
			objBean.setStrBillFooter(objSetupHdModel.getStrBillFooter());
			objBean.setIntBiilPaperSize((long)objSetupHdModel.getIntBillPaperSize());
			objBean.setChkNegBilling(objSetupHdModel.getStrNegativeBilling());
			//objJsonObject.put("gStartDate",objSetupHdModel.getDteStartDate());
		
			//objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
			objBean.setChkDayEnd(objSetupHdModel.getStrDayEnd());
			objBean.setStrBillPrintMode(objSetupHdModel.getStrPrintMode());
			objBean.setStrCity(objSetupHdModel.getStrCityName());
			objBean.setStrState(objSetupHdModel.getStrState());
			objBean.setStrCountry(objSetupHdModel.getStrCountry());
			objBean.setStrTelephone((long)objSetupHdModel.getIntTelephoneNo());
			objBean.setStrNatureOfBussness(objSetupHdModel.getStrNatureOfBusinnes());
			objBean.setChkMultiBillPrint(objSetupHdModel.getStrMultipleBillPrinting());
			objBean.setChkEnableKOT(objSetupHdModel.getStrEnableKOT());
			objBean.setChkEffectOnPSP(objSetupHdModel.getStrEffectOnPSP());
			objBean.setChkPrintVatNo(objSetupHdModel.getStrPrintVatNo());
			objBean.setStrVatNo(objSetupHdModel.getStrVatNo());
			objBean.setChkShowBills(objSetupHdModel.getStrShowBill());
			objBean.setChkServiceTaxNo(objSetupHdModel.getStrPrintServiceTaxNo());
			objBean.setStrServiceTaxNo(objSetupHdModel.getStrServiceTaxNo());
			objBean.setChkManualBillNo(objSetupHdModel.getStrManualBillNo());
			objBean.setStrMenuItemDisSeq(objSetupHdModel.getStrMenuItemDispSeq());
			objBean.setStrSenderEmailId(objSetupHdModel.getStrSenderEmailId());
			objBean.setStrEmailPassword(objSetupHdModel.getStrEmailPassword());
			//objJsonObject.put("gLastModifiedDate",objSetupHdModel.getDteHOServerDate());
			
			objBean.setStrBodyPart(objSetupHdModel.getStrBody());
			objBean.setStrEmailServerName(objSetupHdModel.getStrEmailServerName());
			objBean.setStrAreaSMSApi(objSetupHdModel.getStrSMSApi());
			objBean.setStrPOSType(objSetupHdModel.getStrPOSType());
			objBean.setStrWebServiceLink(objSetupHdModel.getStrWebServiceLink());
			objBean.setStrDataSendFrequency(objSetupHdModel.getStrDataSendFrequency());
			objBean.setStrRFIDSetup(objSetupHdModel.getStrRFID());
			objBean.setStrRFIDServerName(objSetupHdModel.getStrServerName());
			objBean.setStrRFIDUserName(objSetupHdModel.getStrDBUserName());
			objBean.setStrRFIDPassword(objSetupHdModel.getStrDBPassword());
			objBean.setStrRFIDDatabaseName(objSetupHdModel.getStrDatabaseName());
			objBean.setChkPrintKotForDirectBiller(objSetupHdModel.getStrEnableKOTForDirectBiller());
			objBean.setStrPinCode((long)objSetupHdModel.getIntPinCode());
			
		/*	objJsonObject.put("gTheme", objSetupHdModel.getStrChangeTheme());
			objJsonObject.put("gMaxDiscount",objSetupHdModel.getDblMaxDiscount());
			objJsonObject.put("gAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
			objJsonObject.put("gDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
			objJsonObject.put("gColumnSize", objSetupHdModel.getIntColumnSize());
			objJsonObject.put("gPrintType", objSetupHdModel.getStrPrintType());*/
			//	objJsonObject.put("gBillPaperSize", list.get(60));
			
			objBean.setStrMenuItemSortingOn(objSetupHdModel.getStrMenuItemSortingOn());
			objBean.setChkEditHomeDelivery(objSetupHdModel.getStrEditHomeDelivery());
			objBean.setChkSlabBasedHomeDelCharges(objSetupHdModel.getStrSlabBasedHDCharges());
			objBean.setChkSkipWaiterSelection(objSetupHdModel.getStrSkipWaiter());
			objBean.setChkDirectKOTPrintMakeKOT(objSetupHdModel.getStrDirectKOTPrintMakeKOT());
			objBean.setChkSkipPaxSelection(objSetupHdModel.getStrSkipPax());
			objBean.setStrCRM(objSetupHdModel.getStrCRMInterface());
			objBean.setStrGetWebservice(objSetupHdModel.getStrGetWebserviceURL());
			objBean.setStrPostWebservice(objSetupHdModel.getStrPostWebserviceURL());
			objBean.setStrOutletUID(objSetupHdModel.getStrOutletUID());
			objBean.setStrPOSID(objSetupHdModel.getStrPOSID());
			objBean.setStrStockInOption(objSetupHdModel.getStrStockInOption());
			objBean.setStrCustSeries(objSetupHdModel.getStrCustSeries());
			
			
			objBean.setStrAdvRecPrintCount((long)objSetupHdModel.getIntAdvReceiptPrintCount());
			objBean.setStrAreaSendHomeDeliverySMS(objSetupHdModel.getStrHomeDeliverySMS());
			objBean.setStrAreaBillSettlementSMS(objSetupHdModel.getStrBillStettlementSMS());
			objBean.setStrBillFormat(objSetupHdModel.getStrBillFormatType());
			objBean.setChkActivePromotions(objSetupHdModel.getStrActivePromotions());
			objBean.setChkHomeDelSMS(objSetupHdModel.getStrSendHomeDelSMS());
			objBean.setChkBillSettlementSMS(objSetupHdModel.getStrSendBillSettlementSMS());
			objBean.setStrSMSType(objSetupHdModel.getStrSMSType());
			objBean.setChkPrintShortNameOnKOT(objSetupHdModel.getStrPrintShortNameOnKOT());
			objBean.setChkPrintForVoidBill(objSetupHdModel.getStrPrintOnVoidBill());
			objBean.setChkPostSalesDataToMMS(objSetupHdModel.getStrPostSalesDataToMMS());
			objBean.setChkAreaMasterCompulsory(objSetupHdModel.getStrCustAreaMasterCompulsory());
			objBean.setStrPriceFrom(objSetupHdModel.getStrPriceFrom());
			objBean.setChkPrinterErrorMessage(objSetupHdModel.getStrShowPrinterErrorMessage());
						
			//objJsonObject.put("gCustHelpOnTrans",objSetupHdModel.getStrShowCustHelp());
			
			objBean.setChkChangeQtyForExternalCode(objSetupHdModel.getStrChangeQtyForExternalCode());
			objBean.setChkPointsOnBillPrint(objSetupHdModel.getStrPointsOnBillPrint());
			objBean.setStrCardIntfType(objSetupHdModel.getStrCardInterfaceType());
			objBean.setStrCMSIntegrationYN(objSetupHdModel.getStrCMSIntegrationYN());
			objBean.setStrCMSWesServiceURL(objSetupHdModel.getStrCMSWebServiceURL());
			objBean.setChkManualAdvOrderCompulsory(objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
			objBean.setChkPrintManualAdvOrderOnBill(objSetupHdModel.getStrManualAdvOrderNoCompulsory());
			objBean.setChkPrintModifierQtyOnKOT(objSetupHdModel.getStrPrintModifierQtyOnKOT());
			objBean.setIntNoOfLinesInKOTPrint((long)objSetupHdModel.getStrNoOfLinesInKOTPrint());
			objBean.setChkMultiKOTPrint(objSetupHdModel.getStrMultipleKOTPrintYN());
			objBean.setChkItemQtyNumpad(objSetupHdModel.getStrItemQtyNumpad());
			objBean.setChkMemberAsTable(objSetupHdModel.getStrTreatMemberAsTable());
			objBean.setChkPrintKOTToLocalPrinter(objSetupHdModel.getStrKOTToLocalPrinter());
			
			//objJsonObject.put("gCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
			
			objBean.setChkEnableSettleBtnForDirectBillerBill(objSetupHdModel.getStrSettleBtnForDirectBillerBill());
			objBean.setChkDelBoyCompulsoryOnDirectBiller(objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
			objBean.setChkMemberCodeForKOTJPOS(objSetupHdModel.getStrCMSMemberForKOTJPOS());
			objBean.setChkMemberCodeForKOTMPOS(objSetupHdModel.getStrCMSMemberForKOTMPOS());
			objBean.setChkDontShowAdvOrderInOtherPOS(objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
			objBean.setChkPrintZeroAmtModifierInBill(objSetupHdModel.getStrPrintZeroAmtModifierInBill());
			objBean.setChkPrintKOTYN(objSetupHdModel.getStrPrintKOTYN());
			objBean.setChkSlipNoForCreditCardBillYN(objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
			objBean.setChkExpDateForCreditCardBillYN(objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
			objBean.setChkSelectWaiterFromCardSwipe(objSetupHdModel.getStrSelectWaiterFromCardSwipe());
			objBean.setChkMultipleWaiterSelectionOnMakeKOT(objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
			objBean.setChkMoveTableToOtherPOS(objSetupHdModel.getStrMoveTableToOtherPOS());
			objBean.setChkMoveKOTToOtherPOS(objSetupHdModel.getStrMoveKOTToOtherPOS());
			objBean.setChkCalculateTaxOnMakeKOT(objSetupHdModel.getStrCalculateTaxOnMakeKOT());
			objBean.setStrReceiverEmailId(objSetupHdModel.getStrReceiverEmailId());
			objBean.setChkCalculateDiscItemWise(objSetupHdModel.getStrCalculateDiscItemWise());
			objBean.setChkTakewayCustomerSelection(objSetupHdModel.getStrTakewayCustomerSelection());
			objBean.setChkShowItemStkColumnInDB(objSetupHdModel.getStrShowItemStkColumnInDB());
			objBean.setStrItemType(objSetupHdModel.getStrItemType());
			objBean.setChkBoxAllowNewAreaMasterFromCustMaster(objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
			objBean.setChkSelectCustAddressForBill(objSetupHdModel.getStrCustAddressSelectionForBill());
			objBean.setChkGenrateMI(objSetupHdModel.getStrGenrateMI());
			objBean.setStrFTPAddress(objSetupHdModel.getStrFTPAddress());
			objBean.setStrFTPServerUserName(objSetupHdModel.getStrFTPServerUserName());
			objBean.setStrFTPServerPass(objSetupHdModel.getStrFTPServerPass());
			objBean.setChkAllowToCalculateItemWeight(objSetupHdModel.getStrAllowToCalculateItemWeight());
			objBean.setStrShowBillsDtlType(objSetupHdModel.getStrShowBillsDtlType());
			objBean.setChkPrintInvoiceOnBill(objSetupHdModel.getStrPrintTaxInvoiceOnBill());
			objBean.setChkPrintInclusiveOfAllTaxesOnBill(objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
			objBean.setStrApplyDiscountOn(objSetupHdModel.getStrApplyDiscountOn());
			objBean.setChkMemberCodeForKotInMposByCardSwipe(objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
			objBean.setChkPrintBill(objSetupHdModel.getStrPrintBillYN());
			objBean.setChkUseVatAndServiceNoFromPos(objSetupHdModel.getStrVatAndServiceTaxFromPos());
			objBean.setChkMemberCodeForMakeBillInMPOS(objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
			objBean.setChkItemWiseKOTPrintYN(objSetupHdModel.getStrItemWiseKOTYN());
			objBean.setStrPOSForDayEnd(objSetupHdModel.getStrLastPOSForDayEnd());
			objBean.setStrCMSPostingType(objSetupHdModel.getStrCMSPostingType());
			objBean.setChkPopUpToApplyPromotionsOnBill(objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
			objBean.setChkSelectCustomerCodeFromCardSwipe(objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
			objBean.setChkCheckDebitCardBalOnTrans(objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
			objBean.setChkSettlementsFromPOSMaster(objSetupHdModel.getStrSettlementsFromPOSMaster());
			objBean.setChkShiftWiseDayEnd(objSetupHdModel.getStrShiftWiseDayEndYN());
			objBean.setChkProductionLinkup(objSetupHdModel.getStrProductionLinkup());
			objBean.setChkLockDataOnShift(objSetupHdModel.getStrLockDataOnShift());
			objBean.setStrWSClientCode(objSetupHdModel.getStrWSClientCode());
			objBean.setChkEnableBillSeries(objSetupHdModel.getStrEnableBillSeries());
			objBean.setChkEnablePMSIntegration(objSetupHdModel.getStrEnablePMSIntegrationYN());
			objBean.setChkPrintTimeOnBill(objSetupHdModel.getStrPrintTimeOnBill());
			objBean.setChkPrintTDHItemsInBill(objSetupHdModel.getStrPrintTDHItemsInBill());
			objBean.setChkPrintRemarkAndReasonForReprint(objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
			objBean.setIntDaysBeforeOrderToCancel((long)objSetupHdModel.getIntDaysBeforeOrderToCancel());
			objBean.setIntNoOfDelDaysForAdvOrder((long)objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
			objBean.setIntNoOfDelDaysForUrgentOrder((long)objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
			objBean.setChkSetUpToTimeForAdvOrder(objSetupHdModel.getStrSetUpToTimeForAdvOrder());
			objBean.setChkSetUpToTimeForUrgentOrder(objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
			
			  String upToTimeForAdvOrder = (objSetupHdModel.getStrUpToTimeForAdvOrder()).split(" ")[0];
			  objBean.setStrHours(upToTimeForAdvOrder.split(":")[0].trim());
			  objBean.setStrMinutes(upToTimeForAdvOrder.split(":")[1].trim());
			  objBean.setStrAMPM((objSetupHdModel.getStrUpToTimeForAdvOrder()).split(" ")[1]);

			  String upToTimeForUrgentOrder = (objSetupHdModel.getStrUpToTimeForUrgentOrder()).split(" ")[0];
			  objBean.setStrHoursUrgentOrder(upToTimeForUrgentOrder.split(":")[0].trim());
			  objBean.setStrMinutesUrgentOrder(upToTimeForUrgentOrder.split(":")[1].trim());
			  objBean.setStrAMPMUrgent((objSetupHdModel.getStrUpToTimeForUrgentOrder()).split(" ")[1]);

			objBean.setChkEnableBothPrintAndSettleBtnForDB(objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
			objBean.setStrInrestoPOSIntegrationYN(objSetupHdModel.getStrInrestoPOSIntegrationYN());
			objBean.setStrInrestoPOSWesServiceURL(objSetupHdModel.getStrInrestoPOSWebServiceURL());
			objBean.setStrInrestoPOSId(objSetupHdModel.getStrInrestoPOSId());
			objBean.setStrInrestoPOSKey(objSetupHdModel.getStrInrestoPOSKey());
			objBean.setChkCarryForwardFloatAmtToNextDay(objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
			objBean.setChkOpenCashDrawerAfterBillPrint(objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
			objBean.setChkPropertyWiseSalesOrder(objSetupHdModel.getStrPropertyWiseSalesOrderYN());
			
			
			objBean.setChkShowItemDtlsForChangeCustomerOnBill(objSetupHdModel.getStrShowItemDetailsGrid());
			objBean.setChkShowPopUpForNextItemQuantity(objSetupHdModel.getStrShowPopUpForNextItemQuantity());
			objBean.setStrJioPOSIntegrationYN(objSetupHdModel.getStrJioMoneyIntegration());
			objBean.setStrJioPOSWesServiceURL(objSetupHdModel.getStrJioWebServiceUrl());
			objBean.setStrJioMID(objSetupHdModel.getStrJioMID());
			objBean.setStrJioTID(objSetupHdModel.getStrJioTID());
			objBean.setStrJioActivationCode(objSetupHdModel.getStrJioActivationCode());
			objBean.setStrJioDeviceID(objSetupHdModel.getStrJioDeviceID());
			if(!objSetupHdModel.getStrJioDeviceID().toString().isEmpty())
				{
				JioDeviceIDFound=true;
				JioDeviceIDFromDB=objSetupHdModel.getStrJioDeviceID().toString();
				}
			objBean.setChkNewBillSeriesForNewDay(objSetupHdModel.getStrNewBillSeriesForNewDay());
			objBean.setChkShowReportsPOSWise(objSetupHdModel.getStrShowReportsPOSWise());
			objBean.setChkEnableDineIn(objSetupHdModel.getStrEnableDineIn());
			objBean.setChkAutoAreaSelectionInMakeKOT(objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
		
			dteEndDate=objSetupHdModel.getDteEndDate();
		
		

			//objJsonObject.put("ClientImage", objSetupHdModel.getBlobReportImage());
			
			
		}catch(Exception e){
			
		}
        
		return objBean;
	}
	
	@RequestMapping(value = "/loadPrinterDtl", method = RequestMethod.GET)
	public @ResponseBody clsPOSPropertySetupBean funSetPrinterDtl(HttpServletRequest request)
	{
		 List<clsPOSPrinterSetupBean> listBillSeriesDtl= new ArrayList<clsPOSPrinterSetupBean>();
		JSONArray jArr= new JSONArray();
		clsPOSPropertySetupBean objBean = new clsPOSPropertySetupBean();
		StringBuilder sqlStringBuilder = new StringBuilder();
		try{

            sqlStringBuilder.append(" select a.strCostCenterCode,a.strCostCenterName,ifnull(b.strPrimaryPrinterPort,'')"
                    + " ,ifnull(b.strSecondaryPrinterPort,''),ifnull(b.strPrintOnBothPrintersYN,'N')"
                    + " from tblcostcentermaster a "
                    + " left outer join tblprintersetup b on a.strCostCenterCode=b.strCostCenterCode");
         	
			
			List list = objBaseService.funGetList(sqlStringBuilder, "sql");
			JSONArray jArrData=new JSONArray();
			if (list!=null)
				{
					clsPOSPrinterSetupBean objPrinterBean;
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						JSONObject objSettle=new JSONObject();
						objPrinterBean = new clsPOSPrinterSetupBean();
						
						objPrinterBean.setStrCostCenterCode(obj[0].toString());
						objPrinterBean.setStrCostCenterName(obj[1].toString());
						objPrinterBean.setStrPrimaryPrinterPort(obj[2].toString());
						objPrinterBean.setStrSecondaryPrinterPort(obj[3].toString());
						objPrinterBean.setStrPrintOnBothPrintersYN(obj[4].toString());
				
						listBillSeriesDtl.add(objPrinterBean);
						
					}
					 objBean.setListPrinterDtl(listBillSeriesDtl);
				}
    	}catch(Exception e){
			e.printStackTrace();
		}
		return objBean;
	}
	
	
	@RequestMapping(value = "/loadOldSBillSeriesSetup", method = RequestMethod.GET)
	public @ResponseBody String funSetBillSeries(@RequestParam("posCode") String posCode,HttpServletRequest request)
	{
			String strType="";
			try{
				 String sqlBillSeries = "select a.strType from tblbillseries a where a.strPOSCode='" + posCode + "' group by a.strType  ";
	             List list =objBaseService.funGetList(new StringBuilder(sqlBillSeries),"sql");
					 if (list.size()>0)
						{
						 	strType =(String)list.get(0).toString();
						}
			}catch(Exception e){
				e.printStackTrace();
			}
		return strType;
	}
	
	@RequestMapping(value = "/loadOldBillSeries", method = RequestMethod.GET)
	public @ResponseBody clsPOSPropertySetupBean funSetSelectedBillSeries(@RequestParam("posCode") String posCode,HttpServletRequest request)
	{
		List<clsPOSBillSeriesDtlBean> listBillSeriesDtl= new ArrayList<clsPOSBillSeriesDtlBean>();
		JSONArray jArr= new JSONArray();
		clsPOSPropertySetupBean objBean = new clsPOSPropertySetupBean();
		try{
			
			StringBuilder sqlBillSeries =new StringBuilder("select a.strType,a.strBillSeries,a.strCodes,a.strNames,a.strPrintGTOfOtherBills,strPrintInclusiveOfTaxOnBill "
                    + " from tblbillseries a where strPOSCode='" + posCode + "' ");

			List list = objBaseService.funGetList(sqlBillSeries, "sql");
				 if (list.size()>0)
					{
					 clsPOSBillSeriesDtlBean objBillSeries;
					 for(int i=0;i<list.size();i++)
					 {
						Object[] obj=(Object[])list.get(i);
						objBillSeries= new clsPOSBillSeriesDtlBean();
						
						objBillSeries.setStrBillSeries(obj[1].toString());
						objBillSeries.setStrCodes(obj[2].toString());
						objBillSeries.setStrNames(obj[3].toString());
						objBillSeries.setStrPrintGTOfOtherBills(obj[4].toString());
						objBillSeries.setStrPrintInclusiveOfTaxOnBill(obj[5].toString());
						
						listBillSeriesDtl.add(objBillSeries);
						
					 }
					 objBean.setListBillSeriesDtl(listBillSeriesDtl);
					}
					
		}catch(Exception e){
			e.printStackTrace();
		}
		
	 return objBean;
	}

	@RequestMapping(value = "/loadSelectedTypeDtlTable", method = RequestMethod.GET)
	public @ResponseBody List funLoadSelectedTypeDtlTable(@RequestParam("strType") String strType,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		JSONArray jArrList=null;
		List listTypeData=new ArrayList();
		switch(strType)
		{
		case "Group":
			
			jArrList=objPOSGlobal.funGetAllGroup(clientCode);
	        
	        if(null!=jArrList)
			{
				for(int cnt=0;cnt<jArrList.size();cnt++)
				{
					JSONObject jobj=(JSONObject) jArrList.get(cnt);
					listTypeData.add(jobj);
				}
			}
			break;
		case "Sub Group":
			jArrList=objPOSGlobal.funGetAllSubGroup(clientCode);
			 if(null!=jArrList)
				{
					for(int cnt=0;cnt<jArrList.size();cnt++)
					{
						JSONObject jobj=(JSONObject) jArrList.get(cnt);
						listTypeData.add(jobj);
					}
				}
			break;
		case "Menu Head":
			jArrList=objPOSGlobal.funGetAllMenuHeadForMaster(clientCode);
			 if(null!=jArrList)
				{
					for(int cnt=0;cnt<jArrList.size();cnt++)
					{
						JSONObject jobj=(JSONObject) jArrList.get(cnt);
						listTypeData.add(jobj);
					}
				}
			break;
		case "Revenue Head":
			jArrList=objPOSGlobal.funGetAllRevenueHead(clientCode);
			 if(null!=jArrList)
				{
					for(int cnt=0;cnt<jArrList.size();cnt++)
					{
						
						listTypeData.add((String)jArrList.get(cnt));
					}
				}
			break;
		}
			return listTypeData;
	}
	
	
	
	@RequestMapping(value = "/savePOSPropertySetup", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSPropertySetupBean objBean,BindingResult result,HttpServletRequest req,@RequestParam("companyLogo") MultipartFile file)
	{
	
		String posCode="";
		try
		{
			
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("usercode").toString();
			
			 clsSetupHdModel objModel=new clsSetupHdModel(new clsSetupModel_ID(clientCode,posCode));
			 String dateTime = obUtilityController.funGetCurrentDateTime();
				if(file.getSize()!=0)
				{
					Blob blobProdImage = Hibernate.createBlob(file.getInputStream());
					objModel.setBlobReportImage(blobProdImage);
					FileOutputStream fileOuputStream = null;
					try
					{
						byte[] bytes = file.getBytes();
						String imagePath=servletContext.getRealPath("/resources/images");
						fileOuputStream = new FileOutputStream(imagePath+"/imgClientImage.jpg");
						fileOuputStream.write(bytes);
						fileOuputStream.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
				objModel.setStrActivePromotions(objBean.getChkActivePromotions());
			    objModel.setDblMaxDiscount( objBean.getDblMaxDiscount());
			    objModel.setDteEndDate(dteEndDate);
			    objModel.setDteHOServerDate(objBean.getDteHOServerDate());
			    objModel.setDteStartDate( dateTime);
			    objModel.setIntAdvReceiptPrintCount(objBean.getStrAdvRecPrintCount());
			    objModel.setIntBillPaperSize(objBean.getIntBiilPaperSize());
			    objModel.setIntColumnSize(objBean.getIntColumnSize());
			    objModel.setIntDaysBeforeOrderToCancel(objBean.getIntDaysBeforeOrderToCancel());
			    objModel.setIntNoOfDelDaysForAdvOrder(objBean.getIntNoOfDelDaysForAdvOrder());
			    objModel.setIntNoOfDelDaysForUrgentOrder(objBean.getIntNoOfDelDaysForUrgentOrder());
			    objModel.setIntPinCode(objBean.getStrPinCode());
			    objModel.setIntTelephoneNo(objBean.getStrTelephone());
			    objModel.setStrAddressLine1(objBean.getStrAddrLine1());
			    objModel.setStrAddressLine2(objBean.getStrAddrLine2());
			    objModel.setStrAddressLine3(objBean.getStrAddrLine3());
			    objModel.setStrAllowNewAreaMasterFromCustMaster(objGlobal.funIfNull(objBean.getChkBoxAllowNewAreaMasterFromCustMaster(),"N","Y"));
			    objModel.setStrAllowToCalculateItemWeight(objGlobal.funIfNull(objBean.getChkAllowToCalculateItemWeight(),"N","Y"));
			    objModel.setStrApplyDiscountOn(objBean.getStrApplyDiscountOn());
			    objModel.setStrAreaWisePricing(objGlobal.funIfNull(objBean.getChkAreaWisePricing(),"N","Y"));
			    objModel.setStrBillFooter(objBean.getStrBillFooter());
			    objModel.setStrBillFooterStatus("N");
			    objModel.setStrBillFormatType(objBean.getStrBillFormat());
			    objModel.setStrBillStettlementSMS(objBean.getStrAreaBillSettlementSMS());
			    objModel.setStrBody(objBean.getStrBodyPart());
			    objModel.setStrCalculateDiscItemWise(objGlobal.funIfNull(objBean.getChkCalculateDiscItemWise(),"N","Y"));
			    objModel.setStrCalculateTaxOnMakeKOT(objGlobal.funIfNull( objBean.getChkCalculateTaxOnMakeKOT(),"N","Y"));
			    objModel.setStrCardInterfaceType(objBean.getStrCardIntfType());
			    objModel.setStrCarryForwardFloatAmtToNextDay(objGlobal.funIfNull( objBean.getChkCarryForwardFloatAmtToNextDay(),"N","Y"));
			    objModel.setStrChangeQtyForExternalCode(objGlobal.funIfNull( objBean.getChkChangeQtyForExternalCode(),"N","Y"));
			    objModel.setStrChangeTheme(objBean.getStrChangeTheme());
			    objModel.setStrCheckDebitCardBalOnTransactions(objGlobal.funIfNull(objBean.getChkCheckDebitCardBalOnTrans(),"N","Y"));
			    objModel.setStrCityName(objBean.getStrCity());
			    objModel.setStrClientCode(objBean.getStrClientCode());
			    objModel.setStrClientName(objBean.getStrClientName());
			    objModel.setStrCMSIntegrationYN( objBean.getStrCMSIntegrationYN());
			    objModel.setStrCMSMemberForKOTJPOS(objGlobal.funIfNull( objBean.getChkMemberCodeForKOTJPOS(),"N","Y"));
			    objModel.setStrCMSMemberForKOTMPOS(objGlobal.funIfNull( objBean.getChkMemberCodeForKOTMPOS(),"N","Y"));
			    objModel.setStrCMSPOSCode(objBean.getStrPosCode());
			    objModel.setStrCMSPostingType( objBean.getStrCMSPostingType());
			    objModel.setStrCMSWebServiceURL( objBean.getStrCMSWesServiceURL());
			    objModel.setStrConfirmEmailPassword(objBean.getStrEmailPassword());
			    objModel.setStrCountry(objBean.getStrCountry());
			    objModel.setStrCreditCardExpiryDateCompulsoryYN(objGlobal.funIfNull( objBean.getChkExpDateForCreditCardBillYN(),"N","Y"));
			    objModel.setStrCreditCardSlipNoCompulsoryYN(objGlobal.funIfNull( objBean.getChkSlipNoForCreditCardBillYN(),"N","Y"));
			    objModel.setStrCRMInterface(objBean.getStrCRM());
			    objModel.setStrCustAddressSelectionForBill(objGlobal.funIfNull( objBean.getChkSelectCustAddressForBill(),"N","Y"));
			    objModel.setStrCustAreaMasterCompulsory(objGlobal.funIfNull( objBean.getChkAreaMasterCompulsory(),"N","Y"));
			    objModel.setStrCustSeries( objBean.getStrCustSeries());
			    objModel.setStrDatabaseName(objBean.getStrRFIDDatabaseName());
			    objModel.setStrDataPostFlag( "Y");
			    objModel.setStrDataSendFrequency( objBean.getStrDataSendFrequency());
			    objModel.setStrDayEnd(objGlobal.funIfNull( objBean.getChkDayEnd(),"N","Y"));
			    objModel.setStrDBPassword(objBean.getStrRFIDPassword());
			    objModel.setStrDBUserName(objBean.getStrRFIDUserName());
			    objModel.setStrDelBoySelCompulsoryOnDirectBiller(objGlobal.funIfNull( objBean.getChkDelBoyCompulsoryOnDirectBiller(),"N","Y"));
			    objModel.setStrDirectAreaCode( objBean.getStrDirectArea());
			    objModel.setStrDirectKOTPrintMakeKOT(objGlobal.funIfNull( objBean.getChkDirectKOTPrintMakeKOT(),"N","Y"));
			    objModel.setStrDiscountNote( "N");
			    objModel.setStrDontShowAdvOrderInOtherPOS(objGlobal.funIfNull( objBean.getChkDontShowAdvOrderInOtherPOS(),"N","Y"));
			    objModel.setStrEditHomeDelivery(objGlobal.funIfNull( objBean.getChkEditHomeDelivery(),"N","Y"));
			    objModel.setStrEffectOnPSP( objGlobal.funIfNull(objBean.getChkEffectOnPSP(),"N","Y"));
			    objModel.setStrEmail(objBean.getStrEmail());
			    objModel.setStrEmailPassword(objBean.getStrEmailPassword());
			    objModel.setStrEmailServerName(objBean.getStrEmailServerName());
			    objModel.setStrEnableBillSeries(objGlobal.funIfNull( objBean.getChkEnableBillSeries(),"N","Y"));
			    objModel.setStrEnableBothPrintAndSettleBtnForDB(objGlobal.funIfNull( objBean.getChkEnableBothPrintAndSettleBtnForDB(),"N","Y"));
			    objModel.setStrEnableKOT(objGlobal.funIfNull( objBean.getChkEnableKOT(),"N","Y"));
			    objModel.setStrEnableKOTForDirectBiller(objGlobal.funIfNull( objBean.getChkPrintKotForDirectBiller(),"N","Y"));
			    objModel.setStrEnablePMSIntegrationYN(objGlobal.funIfNull( objBean.getChkEnablePMSIntegration(),"N","Y"));
			    objModel.setStrFTPAddress(objBean.getStrFTPAddress());
			    objModel.setStrFTPServerPass(objBean.getStrFTPServerPass());
			    objModel.setStrFTPServerUserName( objBean.getStrFTPServerUserName());
			    objModel.setStrGenrateMI(objGlobal.funIfNull( objBean.getChkGenrateMI(),"N","Y"));
			    objModel.setStrGetWebserviceURL( objBean.getStrGetWebservice());
			    objModel.setStrHomeDeliverySMS( objBean.getStrAreaSendHomeDeliverySMS());
			    objModel.setStrInrestoPOSId(objBean.getStrInrestoPOSId());
			    objModel.setStrInrestoPOSIntegrationYN(objBean.getStrInrestoPOSIntegrationYN());
			    objModel.setStrInrestoPOSKey(objBean.getStrInrestoPOSKey());
			    objModel.setStrInrestoPOSWebServiceURL(objBean.getStrInrestoPOSWesServiceURL());
			    objModel.setStrItemQtyNumpad(objGlobal.funIfNull( objBean.getChkItemQtyNumpad(),"N","Y"));
			    objModel.setStrItemType( objBean.getStrItemType());
			    objModel.setStrItemWiseKOTYN(objGlobal.funIfNull( objBean.getChkItemWiseKOTPrintYN(),"N","Y"));
			    objModel.setStrJioActivationCode(objBean.getStrJioActivationCode());
			    objModel.setStrJioDeviceID(objBean.getStrJioDeviceID());
			    objModel.setStrJioMID(objBean.getStrJioMID());
			    objModel.setStrJioMoneyIntegration(objBean.getStrJioPOSIntegrationYN());
			    objModel.setStrJioTID(objBean.getStrJioTID());
			    objModel.setStrJioWebServiceUrl(objBean.getStrJioPOSWesServiceURL());
			    objModel.setStrKOTToLocalPrinter(objGlobal.funIfNull( objBean.getChkPrintKOTToLocalPrinter(),"N","Y"));
			    objModel.setStrLastPOSForDayEnd(objBean.getStrPOSForDayEnd());
			    objModel.setStrLockDataOnShift(objGlobal.funIfNull( objBean.getChkLockDataOnShift(),"N","Y"));
			    objModel.setStrManualAdvOrderNoCompulsory(objGlobal.funIfNull( objBean.getChkManualAdvOrderCompulsory(),"N","Y"));
			    objModel.setStrManualBillNo(objGlobal.funIfNull( objBean.getChkManualBillNo(),"N","Y"));
			    objModel.setStrMemberCodeForKotInMposByCardSwipe(objGlobal.funIfNull( objBean.getChkMemberCodeForKotInMposByCardSwipe(),"N","Y"));
			    objModel.setStrMemberCodeForMakeBillInMPOS(objGlobal.funIfNull( objBean.getChkMemberCodeForMakeBillInMPOS(),"N","Y"));
			    objModel.setStrMenuItemDispSeq(objBean.getStrMenuItemDisSeq());
			    objModel.setStrMenuItemSortingOn(objBean.getStrMenuItemSortingOn());
			    objModel.setStrMoveTableToOtherPOS(objGlobal.funIfNull(objBean.getChkMoveTableToOtherPOS(),"N","Y"));
			    objModel.setStrMoveKOTToOtherPOS(objGlobal.funIfNull( objBean.getChkMoveKOTToOtherPOS(),"N","Y"));
			    objModel.setStrMultipleBillPrinting(objGlobal.funIfNull( objBean.getChkMultiBillPrint(),"N","Y"));
			    objModel.setStrMultipleKOTPrintYN( objGlobal.funIfNull(objBean.getChkMultiKOTPrint(),"N","Y"));
			    objModel.setStrMultiWaiterSelectionOnMakeKOT(objGlobal.funIfNull( objBean.getChkMultipleWaiterSelectionOnMakeKOT(),"N","Y"));
			    objModel.setStrNatureOfBusinnes(objBean.getStrNatureOfBussness());
			    objModel.setStrNegativeBilling(objGlobal.funIfNull( objBean.getChkNegBilling(),"N","Y"));
			    objModel.setStrNewBillSeriesForNewDay(objGlobal.funIfNull( objBean.getChkNewBillSeriesForNewDay(),"N","Y"));
			    objModel.setStrNoOfLinesInKOTPrint(objBean.getIntNoOfLinesInKOTPrint());
			    objModel.setStrOpenCashDrawerAfterBillPrintYN(objGlobal.funIfNull( objBean.getChkOpenCashDrawerAfterBillPrint(),"N","Y"));
			    objModel.setStrOutletUID(objBean.getStrOutletUID());
			    objModel.setStrPointsOnBillPrint(objGlobal.funIfNull( objBean.getChkPointsOnBillPrint(),"N","Y"));
			    objModel.setStrPopUpToApplyPromotionsOnBill(objGlobal.funIfNull( objBean.getChkPopUpToApplyPromotionsOnBill(),"N","Y"));
			    objModel.setStrPOSCode(objBean.getStrPosCode());
			    objModel.setStrPOSID(objBean.getStrPOSID());
			    objModel.setStrPostSalesDataToMMS(objGlobal.funIfNull( objBean.getChkPostSalesDataToMMS(),"N","Y"));
			    objModel.setStrPostWebserviceURL(objBean.getStrPostWebservice());
			    objModel.setStrPOSType(objBean.getStrPOSType());
			    objModel.setStrPriceFrom(objBean.getStrPriceFrom());
			    objModel.setStrPrintBillYN(objGlobal.funIfNull(objBean.getChkPrintBill(),"N","Y"));
			    objModel.setStrPrintInclusiveOfAllTaxesOnBill(objGlobal.funIfNull( objBean.getChkPrintInclusiveOfAllTaxesOnBill(),"N","Y"));
			    objModel.setStrPrintKOTYN(objGlobal.funIfNull( objBean.getChkPrintKOTYN(),"N","Y"));
			    objModel.setStrPrintManualAdvOrderNoOnBill(objGlobal.funIfNull( objBean.getChkPrintManualAdvOrderOnBill(),"N","Y"));
			    objModel.setStrPrintMode( objBean.getStrBillPrintMode());
			    objModel.setStrPrintModifierQtyOnKOT(objGlobal.funIfNull( objBean.getChkPrintModifierQtyOnKOT(),"N","Y"));
			    objModel.setStrPrintOnVoidBill(objGlobal.funIfNull( objBean.getChkPrintForVoidBill(),"N","Y"));
			    objModel.setStrPrintRemarkAndReasonForReprint(objGlobal.funIfNull(objBean.getChkPrintRemarkAndReasonForReprint(),"N","Y"));
			    objModel.setStrPrintServiceTaxNo(objGlobal.funIfNull(objBean.getChkServiceTaxNo(),"N","Y"));
			    objModel.setStrPrintShortNameOnKOT(objGlobal.funIfNull( objBean.getChkPrintShortNameOnKOT(),"N","Y"));
			    objModel.setStrPrintTaxInvoiceOnBill(objGlobal.funIfNull( objBean.getChkPrintInvoiceOnBill(),"N","Y"));
			    objModel.setStrPrintTDHItemsInBill( objGlobal.funIfNull(objBean.getChkPrintTDHItemsInBill(),"N","Y"));
			    objModel.setStrPrintTimeOnBill(objGlobal.funIfNull( objBean.getChkPrintTimeOnBill(),"N","Y"));
			    objModel.setStrPrintType(objBean.getStrPrintingType());
			    objModel.setStrPrintVatNo(objGlobal.funIfNull( objBean.getChkPrintVatNo(),"N","Y"));
			    objModel.setStrPrintZeroAmtModifierInBill(objGlobal.funIfNull( objBean.getChkPrintZeroAmtModifierInBill(),"N","Y"));
			    objModel.setStrProductionLinkup(objGlobal.funIfNull( objBean.getChkProductionLinkup(),"N","Y"));
			    objModel.setStrPropertyWiseSalesOrderYN(objGlobal.funIfNull(objBean.getChkPropertyWiseSalesOrder(),"N","Y"));
			    objModel.setStrReceiverEmailId(objBean.getStrReceiverEmailId());
			    objModel.setStrRFID(objBean.getStrRFIDSetup());
			    objModel.setStrSelectCustomerCodeFromCardSwipe(objGlobal.funIfNull(objBean.getChkSelectCustomerCodeFromCardSwipe(),"N","Y"));
			    objModel.setStrSelectWaiterFromCardSwipe(objGlobal.funIfNull( objBean.getChkSelectWaiterFromCardSwipe(),"N","Y"));
			    objModel.setStrSendBillSettlementSMS(objGlobal.funIfNull(objBean.getChkBillSettlementSMS(),"N","Y"));
			    objModel.setStrSenderEmailId(objBean.getStrSenderEmailId());
			    objModel.setStrSendHomeDelSMS(objGlobal.funIfNull(objBean.getChkHomeDelSMS(),"N","Y"));
			    objModel.setStrServerName(objBean.getStrRFIDServerName());
			    objModel.setStrServiceTaxNo(objBean.getStrServiceTaxNo());
			    objModel.setStrSettleBtnForDirectBillerBill(objGlobal.funIfNull( objBean.getChkEnableSettleBtnForDirectBillerBill(),"N","Y"));
			    objModel.setStrSettlementsFromPOSMaster(objGlobal.funIfNull( objBean.getChkSettlementsFromPOSMaster(),"N","Y"));
			    objModel.setStrSetUpToTimeForAdvOrder(objGlobal.funIfNull(objBean.getChkSetUpToTimeForAdvOrder(),"N","Y"));
			    objModel.setStrSetUpToTimeForUrgentOrder(objGlobal.funIfNull( objBean.getChkSetUpToTimeForUrgentOrder(),"N","Y"));
			    objModel.setStrShiftWiseDayEndYN(objGlobal.funIfNull( objBean.getChkShiftWiseDayEnd(),"N","Y"));
			    objModel.setStrShowBill(objGlobal.funIfNull(objBean.getChkShowBills(),"N","Y"));
			    objModel.setStrShowBillsDtlType(objBean.getStrShowBillsDtlType());
			    objModel.setStrShowCustHelp( "N");
			    objModel.setStrShowItemDetailsGrid(objGlobal.funIfNull( objBean.getChkShowItemDtlsForChangeCustomerOnBill(),"N","Y"));
			    objModel.setStrShowItemStkColumnInDB(objGlobal.funIfNull( objBean.getChkShowItemStkColumnInDB(),"N","Y"));
			    objModel.setStrShowPopUpForNextItemQuantity(objGlobal.funIfNull( objBean.getChkShowPopUpForNextItemQuantity(),"N","Y"));
			    objModel.setStrShowPrinterErrorMessage(objGlobal.funIfNull( objBean.getChkPrinterErrorMessage(),"N","Y"));
			    objModel.setStrShowReportsPOSWise(objGlobal.funIfNull( objBean.getChkShowReportsPOSWise(),"N","Y"));
			    objModel.setStrSkipPax(objGlobal.funIfNull( objBean.getChkSkipPaxSelection(),"N","Y"));
			    objModel.setStrSkipWaiter(objGlobal.funIfNull(objBean.getChkSkipWaiterSelection(),"N","Y"));
			    objModel.setStrSkipWaiterAndPax( "N");
			    objModel.setStrSlabBasedHDCharges(objGlobal.funIfNull( objBean.getChkSlabBasedHomeDelCharges(),"N","Y"));
			    objModel.setStrSMSApi(objBean.getStrAreaSMSApi());
			    objModel.setStrSMSType(objBean.getStrSMSType());
			    objModel.setStrState(objBean.getStrState());
			    objModel.setStrStockInOption(objGlobal.funIfNull( objBean.getStrStockInOption(),"N","Y"));
			    objModel.setStrTakewayCustomerSelection(objGlobal.funIfNull( objBean.getChkTakewayCustomerSelection(),"N","Y"));
			    objModel.setStrTouchScreenMode( "N");
			    objModel.setStrTreatMemberAsTable(objGlobal.funIfNull(objBean.getChkMemberAsTable(),"N","Y"));
			
			    String upToTimeForAdvOrder=objBean.getStrHours()+":"+ objBean.getStrMinutes()+" "+ objBean.getStrAMPM();
				String upToTimeForUrgentOrder=objBean.getStrHoursUrgentOrder()+":"+ objBean.getStrMinutesUrgentOrder()+" "+objBean.getStrAMPMUrgent();
				
				objModel.setStrUpToTimeForAdvOrder(upToTimeForAdvOrder);
			    objModel.setStrUpToTimeForUrgentOrder(upToTimeForUrgentOrder);
			    objModel.setStrVatAndServiceTaxFromPos(objGlobal.funIfNull( objBean.getChkUseVatAndServiceNoFromPos(),"N","Y")); 
			    objModel.setStrVatNo(objBean.getStrVatNo());
			    objModel.setStrWebServiceLink(objBean.getStrWebServiceLink());
			    objModel.setStrWSClientCode(objBean.getStrWSClientCode());
			    objModel.setStrEnableDineIn(objGlobal.funIfNull( objBean.getChkEnableDineIn(),"N","Y"));
			    objModel.setStrAutoAreaSelectionInMakeKOT(objGlobal.funIfNull( objBean.getChkAutoAreaSelectionInMakeKOT(),"N","Y"));
			
			    objModel.setDteDateCreated(dateTime);
			    objModel.setDteDateEdited(dateTime);
			 
			    objModel.setStrUserCreated(webStockUserCode);
			    objModel.setStrUserEdited(webStockUserCode);
			    
			    funSaveUpdatePropertySetup(objModel);
			    
		    if (objBean.getStrJioPOSIntegrationYN().equalsIgnoreCase("Y"))
                {
                    if(!(objBean.getStrJioDeviceID().isEmpty()))
                     {
                          if(JioDeviceIDFound && ((!objBean.getStrJioDeviceID().equals(JioDeviceIDFromDB))))
                          {
                             funSaveMapMyDevice(objBean.getStrJioMID(), objBean.getStrJioTID(), objBean.getStrJioDeviceID(), objBean.getStrJioActivationCode(),  objBean.getStrPosCode());   
                          }
                          else if(!JioDeviceIDFound)
                          {
                              funSaveMapMyDevice(objBean.getStrJioMID(), objBean.getStrJioTID(), objBean.getStrJioDeviceID(), objBean.getStrJioActivationCode(),  objBean.getStrPosCode());
                          }    
                          
                     } 
                }
		    
			 List<clsPOSPrinterSetupBean> printerlist=objBean.getListPrinterDtl();
			    if(null!=printerlist)
			    {
			    	String sql="truncate table tblprintersetup";
				    objBaseService.funExecuteUpdate(sql, "sql");
			    	for(int i=0; i<printerlist.size(); i++)
				    {
				    	clsPOSPrinterSetupBean obj= new clsPOSPrinterSetupBean();
				    	obj=(clsPOSPrinterSetupBean)printerlist.get(i);
			    		clsPrinterSetupHdModel objModelprint= new clsPrinterSetupHdModel(new clsPrinterSetupModel_ID(obj.getStrCostCenterCode(),clientCode));
				    	objModelprint.setStrCostCenterCode(obj.getStrCostCenterCode());
				    	objModelprint.setStrCostCenterName(obj.getStrCostCenterName());
				    	objModelprint.setStrPrimaryPrinterPort(obj.getStrPrimaryPrinterPort());
				    	if(obj.getStrPrintOnBothPrintersYN()!=null)
				    		objModelprint.setStrPrintOnBothPrintersYN("Y");
				    	else
				    		objModelprint.setStrPrintOnBothPrintersYN("N");
				    	
				    	objModelprint.setStrSecondaryPrinterPort(obj.getStrSecondaryPrinterPort());
				    	objModelprint.setStrUserCreated(webStockUserCode);
				    	objModelprint.setStrUserEdited(webStockUserCode);
				    	objModelprint.setDteDateCreated(dateTime);
				    	objModelprint.setDteDateEdited(dateTime);
				    	
				    	objModelprint.setStrDataPostFlag("Y");
				    	
				    	 objBaseService.funSave(objModelprint);
				    }
			    }
			    
			 List<clsPOSBillSeriesDtlBean> list=objBean.getListBillSeriesDtl();
			    if(null!=list)
			    {
			    	String hql="DELETE clsBillSeriesHdModel WHERE strClientCode= "+clientCode;
			    	objBaseService.funExecuteUpdate(hql,"hql");
			    	for(int i=0; i<list.size(); i++)
				    {
				    	clsPOSBillSeriesDtlBean obj= new clsPOSBillSeriesDtlBean();
				    	obj=(clsPOSBillSeriesDtlBean)list.get(i);
			    		
			    		    clsBillSeriesHdModel objModelBillSer= new clsBillSeriesHdModel(new clsBillSeriesModel_ID(obj.getStrBillSeries(),posCode,clientCode));
					    	objModelBillSer.setStrBillSeries(obj.getStrBillSeries());
					    	objModelBillSer.setStrCodes(obj.getStrCodes());
					    	objModelBillSer.setStrNames(obj.getStrNames());
					    	
					    	if(obj.getStrPrintGTOfOtherBills()!=null)
					    		objModelBillSer.setStrPrintGTOfOtherBills("Y");
					    	else
					    		objModelBillSer.setStrPrintGTOfOtherBills("N");
					    	
					    	if(obj.getStrPrintInclusiveOfTaxOnBill()!=null)
					    		objModelBillSer.setStrPrintInclusiveOfTaxOnBill("Y");
					    	else
					    		objModelBillSer.setStrPrintInclusiveOfTaxOnBill("N");
					    	
					    	
					    	
					    	objModelBillSer.setStrPropertyCode(clientCode+"."+posCode);
					    	objModelBillSer.setStrType(objBean.getStrBillSeriesType());
					    	objModelBillSer.setStrUserCreated(webStockUserCode);
					    	objModelBillSer.setStrUserEdited(webStockUserCode);
					    	objModelBillSer.setDteCreatedDate(dateTime);
					    	objModelBillSer.setDteEditedDate(dateTime);
					    	objModelBillSer.setStrDataPostFlag("Y");
					    	objModelBillSer.setIntLastNo(0);
					    	
					    	objBaseService.funSave(objModelBillSer);
					    	 
				    }
			    }
			    
									
			return new ModelAndView("redirect:/frmPOSPropertySetup.html");
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	

	 public void funSaveUpdatePropertySetup(clsSetupHdModel objModel)
	 {	
		 String newPropertyPOSCode=objModel.getStrPOSCode();
		 String strHOPOSType=objModel.getStrPOSType();
		try
		{
			String sql = "select strClientCode from tblsetup where strPOSCode='All' ";
    	    List list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			 if (list.size()>0)
			 {
				  objBaseService.funSave(objModel);
				  if (!newPropertyPOSCode.equalsIgnoreCase("All"))
	              {
	                sql="delete from tblsetup where strPOSCode='All' ";
	                objBaseService.funExecuteUpdate(sql, "sql");
	              }
            }
		  else
	         {
			  	objBaseService.funSave(objModel);
	             if (newPropertyPOSCode.equalsIgnoreCase("All"))
	             {
		           	  sql="delete from tblsetup where strPOSCode<>'All' ";
		           	  objBaseService.funExecuteUpdate(sql, "sql");
	           	 }
	             if (strHOPOSType.equalsIgnoreCase("Client POS"))
	             {
	                 funPostPropertySetupDataToHO();
	                 funPostBillSeriesDataHO();
	             }
	         }
		  
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
    }
	
	 private void funPostPropertySetupDataToHO()
	 {
		 	boolean flgResult = false;
	        StringBuilder sql = new StringBuilder();
	    	clsSetupHdModel objSetupHdModel = new clsSetupHdModel();
    	try{
			 JSONObject rootObject = new JSONObject();
	         JSONArray dataObjectArray = new JSONArray();
	         boolean flgAllPOS = false;
	         sql.append("select strClientCode from tblsetup where strPOSCode='All' ");
	         List list = objBaseService.funGetList(sql, "sql");
			 if (list.size()>0)
	         {
	             flgAllPOS = true;
	         }
	         sql.setLength(0);
	         list = objBaseService.funLoadAllCriteriaWise(objSetupHdModel,"strDataPostFlag","N");
		
	         if (list.size()>0)
	         { 
	        	 for(int i=0; i<list.size();i++)
	        	 {
	        		 objSetupHdModel = (clsSetupHdModel) list.get(i);
	        		 if (!objSetupHdModel.getStrWSClientCode().trim().isEmpty())
		             {
		                 if (flgAllPOS)
		                 {
		                     sql.setLength(0);
		                     sql.append("select strPOSCode from tblposmaster ");
		                    
		         			 List poslist =objBaseService.funGetList(sql, "sql");
		         			 for(int j=0; j<poslist.size();j++)
		         	         {
		                         JSONObject objJsonObject = new JSONObject();
		                      
		                        objJsonObject.put("strClientCode", objSetupHdModel.getStrClientCode());//clientCode
		         				objJsonObject.put("strClientName",objSetupHdModel.getStrClientName());//clientName
		         				objJsonObject.put("strAddressLine1", objSetupHdModel.getStrAddressLine1());
		         				objJsonObject.put("strAddressLine2", objSetupHdModel.getStrAddressLine2());
		         				objJsonObject.put("strAddressLine3", objSetupHdModel.getStrAddressLine3());
		         				objJsonObject.put("strEmail", objSetupHdModel.getStrEmail());
		         				objJsonObject.put("strBillFooter", objSetupHdModel.getStrBillFooter());
		         				objJsonObject.put("strBillFooterStatus", objSetupHdModel.getStrBillFooterStatus());
		             			objJsonObject.put("intBillPaperSize", objSetupHdModel.getIntBillPaperSize());
		             			objJsonObject.put("strNegativeBilling", objSetupHdModel.getStrNegativeBilling());
		         				objJsonObject.put("strDayEnd", objSetupHdModel.getStrDayEnd());
		         				objJsonObject.put("strPrintMode", objSetupHdModel.getStrPrintMode());
		         				objJsonObject.put("strDiscountNote", objSetupHdModel.getStrDiscountNote());
		         				objJsonObject.put("strCityName", objSetupHdModel.getStrCityName());
		         				objJsonObject.put("strState",objSetupHdModel.getStrState());
		         				objJsonObject.put("strCountry",objSetupHdModel.getStrCountry());
		         				objJsonObject.put("intTelephoneNo",objSetupHdModel.getIntTelephoneNo());
		         				objJsonObject.put("gMobileNoForSMS",objSetupHdModel.getIntTelephoneNo());
		         				objJsonObject.put("dteStartDate",objSetupHdModel.getDteStartDate());
		         				objJsonObject.put("dteEndDate", objSetupHdModel.getDteEndDate());
		         				objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
		         				objJsonObject.put("strNatureOfBusinnes", objSetupHdModel.getStrNatureOfBusinnes());
		         				objJsonObject.put("strMultipleBillPrinting",objSetupHdModel.getStrMultipleBillPrinting());
		         				objJsonObject.put("strEnableKOT",objSetupHdModel.getStrEnableKOT());
		         				objJsonObject.put("strEffectOnPSP",objSetupHdModel.getStrEffectOnPSP());
		         				objJsonObject.put("strPrintVatNo", objSetupHdModel.getStrPrintVatNo());
		         				objJsonObject.put("strVatNo",objSetupHdModel.getStrVatNo());
		         				objJsonObject.put("strShowBill", objSetupHdModel.getStrShowBill());
		         				objJsonObject.put("strPrintServiceTaxNo", objSetupHdModel.getStrPrintServiceTaxNo());
		         				objJsonObject.put("strServiceTaxNo", objSetupHdModel.getStrServiceTaxNo());
		         				objJsonObject.put("strManualBillNo", objSetupHdModel.getStrManualBillNo());
		         				objJsonObject.put("strMenuItemDispSeq",objSetupHdModel.getStrMenuItemDispSeq());
		         				objJsonObject.put("strSenderEmailId", objSetupHdModel.getStrSenderEmailId());
		         				objJsonObject.put("strEmailPassword",objSetupHdModel.getStrEmailPassword());
		         				objJsonObject.put("strConfirmEmailPassword",objSetupHdModel.getStrConfirmEmailPassword());
		         				objJsonObject.put("strBody", objSetupHdModel.getStrBody());
		         				objJsonObject.put("strEmailServerName",objSetupHdModel.getStrEmailServerName());
		         				objJsonObject.put("strSMSApi", objSetupHdModel.getStrSMSApi());
		         				objJsonObject.put("strUserCreated", objSetupHdModel.getStrUserCreated());
		         				objJsonObject.put("strUserEdited", objSetupHdModel.getStrUserEdited());
		         				objJsonObject.put("dteDateCreated", objSetupHdModel.getDteDateCreated());
		         				objJsonObject.put("dteDateEdited", objSetupHdModel.getDteDateEdited());
		         				objJsonObject.put("strPOSType", objSetupHdModel.getStrPOSType());
		         				objJsonObject.put("strWebServiceLink",objSetupHdModel.getStrWebServiceLink());
		         				objJsonObject.put("strDataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
		         				objJsonObject.put("dteHOServerDate",objSetupHdModel.getDteHOServerDate());
		         				objJsonObject.put("strRFID",objSetupHdModel.getStrRFID());
		         				objJsonObject.put("strServerName",objSetupHdModel.getStrServerName());
		         				objJsonObject.put("strDBUserName", objSetupHdModel.getStrDBUserName());
		         				objJsonObject.put("strDBPassword",objSetupHdModel.getStrDBPassword());
		         				objJsonObject.put("strDatabaseName",objSetupHdModel.getStrDatabaseName());
		         				objJsonObject.put("strEnableKOTForDirectBiller", objSetupHdModel.getStrEnableKOTForDirectBiller());
		         				objJsonObject.put("intPinCode",objSetupHdModel.getIntPinCode());
		         				objJsonObject.put("strChangeTheme", objSetupHdModel.getStrChangeTheme());
		         				objJsonObject.put("dblMaxDiscount",objSetupHdModel.getDblMaxDiscount());
		         				objJsonObject.put("strAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
		         				objJsonObject.put("strMenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
		         				objJsonObject.put("strDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
		         				objJsonObject.put("intColumnSize", objSetupHdModel.getIntColumnSize());
		         				objJsonObject.put("strPrintType", objSetupHdModel.getStrPrintType());
		         				objJsonObject.put("strEditHomeDelivery", objSetupHdModel.getStrEditHomeDelivery());
		         				objJsonObject.put("strSlabBasedHDCharges", objSetupHdModel.getStrSlabBasedHDCharges());
		         				objJsonObject.put("strSkipWaiterAndPax", objSetupHdModel.getStrSkipWaiterAndPax());
		         				objJsonObject.put("strSkipWaiter",objSetupHdModel.getStrSkipWaiter());
		         				objJsonObject.put("strDirectKOTPrintMakeKOT", objSetupHdModel.getStrDirectKOTPrintMakeKOT());
		         				objJsonObject.put("strSkipPax",objSetupHdModel.getStrSkipPax());
		         				objJsonObject.put("strCRMInterface",objSetupHdModel.getStrCRMInterface());
		         				objJsonObject.put("strGetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
		         				objJsonObject.put("strPostWebserviceURL", objSetupHdModel.getStrPostWebserviceURL());
		         			//	objJsonObject.put("gOutletUID",objSetupHdModel.getStrOutletUID());
		         				objJsonObject.put("strPOSID",objSetupHdModel.getStrPOSID());
		         				objJsonObject.put("strStockInOption",objSetupHdModel.getStrStockInOption());
		         				objJsonObject.put("longCustSeries", objSetupHdModel.getStrCustSeries());
		         				objJsonObject.put("intAdvReceiptPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
		         				objJsonObject.put("strHomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
		         				objJsonObject.put("strBillStettlementSMS", objSetupHdModel.getStrBillStettlementSMS());
		           				objJsonObject.put("strBillFormatType",objSetupHdModel.getStrBillFormatType());
		         				objJsonObject.put("strActivePromotions",objSetupHdModel.getStrActivePromotions());
		         				objJsonObject.put("strSendHomeDelSMS",objSetupHdModel.getStrSendHomeDelSMS());
		         				objJsonObject.put("strSendBillSettlementSMS", objSetupHdModel.getStrSendBillSettlementSMS());
		         				objJsonObject.put("strSMSType", objSetupHdModel.getStrSMSType());
		         				objJsonObject.put("strPrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
		         				objJsonObject.put("strShowCustHelp",objSetupHdModel.getStrShowCustHelp());
		         				objJsonObject.put("strPrintOnVoidBill", objSetupHdModel.getStrPrintOnVoidBill());
		         				objJsonObject.put("strPostSalesDataToMMS", objSetupHdModel.getStrPostSalesDataToMMS());
		         				objJsonObject.put("strCustAreaMasterCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
		         				objJsonObject.put("strPriceFrom",objSetupHdModel.getStrPriceFrom());
		         				objJsonObject.put("strShowPrinterErrorMessage", objSetupHdModel.getStrShowPrinterErrorMessage());
		         				objJsonObject.put("strTouchScreenMode", objSetupHdModel.getStrTouchScreenMode());
		         				objJsonObject.put("strCardInterfaceType", objSetupHdModel.getStrCardInterfaceType());
		         				objJsonObject.put("strCMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
		         				objJsonObject.put("strCMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
		         				objJsonObject.put("strChangeQtyForExternalCode", objSetupHdModel.getStrChangeQtyForExternalCode());
		         				objJsonObject.put("strPointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
		         				objJsonObject.put("strCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
		         				objJsonObject.put("strManualAdvOrderNoCompulsory",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
		         				objJsonObject.put("strPrintManualAdvOrderNoOnBill", objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
		         				objJsonObject.put("strPrintModifierQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
		         				objJsonObject.put("strNoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
		         				objJsonObject.put("strMultipleKOTPrintYN",objSetupHdModel.getStrMultipleKOTPrintYN());
		         				objJsonObject.put("strItemQtyNumpad", objSetupHdModel.getStrItemQtyNumpad());
		         				objJsonObject.put("strTreatMemberAsTable", objSetupHdModel.getStrTreatMemberAsTable());
		         				objJsonObject.put("strKOTToLocalPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
		         				objJsonObject.put("strSettleBtnForDirectBillerBill",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
		         				objJsonObject.put("strDelBoySelCompulsoryOnDirectBiller", objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
		         				objJsonObject.put("strCMSMemberForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
		         				objJsonObject.put("strCMSMemberForKOTMPOS", objSetupHdModel.getStrCMSMemberForKOTMPOS());
		         				objJsonObject.put("strDontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
		         				objJsonObject.put("strPrintZeroAmtModifierInBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
		         				objJsonObject.put("strPrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
		         				objJsonObject.put("strCreditCardSlipNoCompulsoryYN",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
		         				objJsonObject.put("strCreditCardExpiryDateCompulsoryYN", objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
		         				objJsonObject.put("strSelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
		         				objJsonObject.put("strMultiWaiterSelectionOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
		         				objJsonObject.put("strMoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
		         				objJsonObject.put("strMoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
		         				objJsonObject.put("strCalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
		         				objJsonObject.put("strReceiverEmailId", objSetupHdModel.getStrReceiverEmailId());
		         				objJsonObject.put("strCalculateDiscItemWise",objSetupHdModel.getStrCalculateDiscItemWise());
		         				objJsonObject.put("strTakewayCustomerSelection",objSetupHdModel.getStrTakewayCustomerSelection());
		         				objJsonObject.put("StrShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
		         				objJsonObject.put("strItemType",objSetupHdModel.getStrItemType());
		         				objJsonObject.put("strAllowNewAreaMasterFromCustMaster", objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
		         				objJsonObject.put("strCustAddressSelectionForBill", objSetupHdModel.getStrCustAddressSelectionForBill());
		         				objJsonObject.put("strGenrateMI",objSetupHdModel.getStrGenrateMI());
		         				objJsonObject.put("strFTPAddress", objSetupHdModel.getStrFTPAddress());
		         				objJsonObject.put("strFTPServerUserName", objSetupHdModel.getStrFTPServerUserName());
		         				objJsonObject.put("strFTPServerPass",objSetupHdModel.getStrFTPServerPass());
		         				objJsonObject.put("strAllowToCalculateItemWeight", objSetupHdModel.getStrAllowToCalculateItemWeight());
		         				objJsonObject.put("strShowBillsDtlType", objSetupHdModel.getStrShowBillsDtlType());
		         				objJsonObject.put("strPrintTaxInvoiceOnBill", objSetupHdModel.getStrPrintTaxInvoiceOnBill());
		         				objJsonObject.put("strPrintInclusiveOfAllTaxesOnBill",objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
		         				objJsonObject.put("strApplyDiscountOn",objSetupHdModel.getStrApplyDiscountOn());
		         				objJsonObject.put("strMemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
		         				objJsonObject.put("strPrintBillYN", objSetupHdModel.getStrPrintBillYN());
		         				objJsonObject.put("strVatAndServiceTaxFromPos",objSetupHdModel.getStrVatAndServiceTaxFromPos());
		         				objJsonObject.put("strMemberCodeForMakeBillInMPOS",objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
		         				objJsonObject.put("strItemWiseKOTYN", objSetupHdModel.getStrItemWiseKOTYN());
		         				objJsonObject.put("strLastPOSForDayEnd", objSetupHdModel.getStrLastPOSForDayEnd());
		         				objJsonObject.put("strCMSPostingType", objSetupHdModel.getStrCMSPostingType());
		         				objJsonObject.put("strPopUpToApplyPromotionsOnBill",objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
		         				objJsonObject.put("strSelectCustomerCodeFromCardSwipe", objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
		         				objJsonObject.put("strCheckDebitCardBalOnTransactions", objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
		         				objJsonObject.put("strSettlementsFromPOSMaster", objSetupHdModel.getStrSettlementsFromPOSMaster());
		         				objJsonObject.put("strShiftWiseDayEndYN", objSetupHdModel.getStrShiftWiseDayEndYN());
		         				objJsonObject.put("strProductionLinkup", objSetupHdModel.getStrProductionLinkup());
		         				objJsonObject.put("strLockDataOnShift",objSetupHdModel.getStrLockDataOnShift());
		         				objJsonObject.put("strWSClientCode", objSetupHdModel.getStrWSClientCode());
		         				objJsonObject.put("strPOSCode", objSetupHdModel.getStrPOSCode());
		         				objJsonObject.put("strEnableBillSeries",objSetupHdModel.getStrEnableBillSeries());
		         				objJsonObject.put("strEnablePMSIntegrationYN",objSetupHdModel.getStrEnablePMSIntegrationYN());
		         				objJsonObject.put("strPrintTimeOnBill", objSetupHdModel.getStrPrintTimeOnBill());
		         				objJsonObject.put("strPrintTDHItemsInBill", objSetupHdModel.getStrPrintTDHItemsInBill());
		         				objJsonObject.put("strPrintRemarkAndReasonForReprint",objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
		         			 
		         				objJsonObject.put("intDaysBeforeOrderToCancel", objSetupHdModel.getIntDaysBeforeOrderToCancel());
		         				objJsonObject.put("intNoOfDelDaysForAdvOrder", objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
		         				objJsonObject.put("intNoOfDelDaysForUrgentOrder", objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
		         				objJsonObject.put("strSetUpToTimeForAdvOrder",objSetupHdModel.getStrSetUpToTimeForAdvOrder());
		         				objJsonObject.put("strSetUpToTimeForUrgentOrder", objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
		         				objJsonObject.put("strUpToTimeForAdvOrder",objSetupHdModel.getStrUpToTimeForAdvOrder());
		         				objJsonObject.put("strUpToTimeForUrgentOrder", objSetupHdModel.getStrUpToTimeForUrgentOrder());
		         				objJsonObject.put("strEnableBothPrintAndSettleBtnForDB",objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
		         				objJsonObject.put("strInrestoPOSIntegrationYN", objSetupHdModel.getStrInrestoPOSIntegrationYN());
		         				objJsonObject.put("strInrestoPOSWebServiceURL", objSetupHdModel.getStrInrestoPOSWebServiceURL());
		         				objJsonObject.put("strInrestoPOSId", objSetupHdModel.getStrInrestoPOSId());
		         				objJsonObject.put("strInrestoPOSKey",objSetupHdModel.getStrInrestoPOSKey());
		         				objJsonObject.put("strCarryForwardFloatAmtToNextDay", objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
		         				objJsonObject.put("strOpenCashDrawerAfterBillPrintYN",objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
		         				objJsonObject.put("strPropertyWiseSalesOrderYN",objSetupHdModel.getStrPropertyWiseSalesOrderYN());
		         				objJsonObject.put("strDataPostFlag",objSetupHdModel.getStrDataPostFlag());
		             			objJsonObject.put("strShowItemDetailsGrid", objSetupHdModel.getStrShowItemDetailsGrid());
		         				
		         				objJsonObject.put("strShowPopUpForNextItemQuantity", objSetupHdModel.getStrShowPopUpForNextItemQuantity());
		         				
		         				objJsonObject.put("strJioMoneyIntegration", objSetupHdModel.getStrJioMoneyIntegration());
		         				objJsonObject.put("strJioWebServiceUrl",objSetupHdModel.getStrJioWebServiceUrl());
		         				
		         				objJsonObject.put("strJioMID", objSetupHdModel.getStrJioMID());
		         				
		         				objJsonObject.put("strJioTID", objSetupHdModel.getStrJioTID());
		         				
		         				objJsonObject.put("strJioActivationCode", objSetupHdModel.getStrJioActivationCode());
		         				objJsonObject.put("strJioDeviceID",objSetupHdModel.getStrJioDeviceID());
		         				objJsonObject.put("strNewBillSeriesForNewDay", objSetupHdModel.getStrNewBillSeriesForNewDay());
		         				objJsonObject.put("strShowReportsPOSWise",objSetupHdModel.getStrShowReportsPOSWise());
		             		   
		         				objJsonObject.put("strEnableDineIn", objSetupHdModel.getStrEnableDineIn());
		        				objJsonObject.put("strAutoAreaSelectionInMakeKOT",objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
		        			
		                         dataObjectArray.add(objJsonObject);
		                     }
		                 }
		                 else
			             {
			                    JSONObject objJsonObject = new JSONObject();
			             		  
			                     objJsonObject.put("strClientCode", objSetupHdModel.getStrClientCode());//clientCode
			     				objJsonObject.put("strClientName",objSetupHdModel.getStrClientName());//clientName
			     				objJsonObject.put("strAddressLine1", objSetupHdModel.getStrAddressLine1());
			     				objJsonObject.put("strAddressLine2", objSetupHdModel.getStrAddressLine2());
			     				objJsonObject.put("strAddressLine3", objSetupHdModel.getStrAddressLine3());
			     				objJsonObject.put("strEmail", objSetupHdModel.getStrEmail());
			     				objJsonObject.put("strBillFooter", objSetupHdModel.getStrBillFooter());
			     				objJsonObject.put("strBillFooterStatus", objSetupHdModel.getStrBillFooterStatus());
			         			objJsonObject.put("intBillPaperSize", objSetupHdModel.getIntBillPaperSize());
			         			objJsonObject.put("strNegativeBilling", objSetupHdModel.getStrNegativeBilling());
			     				objJsonObject.put("strDayEnd", objSetupHdModel.getStrDayEnd());
			     				objJsonObject.put("strPrintMode", objSetupHdModel.getStrPrintMode());
			     				objJsonObject.put("strDiscountNote", objSetupHdModel.getStrDiscountNote());
			     				objJsonObject.put("strCityName", objSetupHdModel.getStrCityName());
			     				objJsonObject.put("strState",objSetupHdModel.getStrState());
			     				objJsonObject.put("strCountry",objSetupHdModel.getStrCountry());
			     				objJsonObject.put("intTelephoneNo",objSetupHdModel.getIntTelephoneNo());
			     				objJsonObject.put("gMobileNoForSMS",objSetupHdModel.getIntTelephoneNo());
			     				objJsonObject.put("dteStartDate",objSetupHdModel.getDteStartDate());
			     				objJsonObject.put("dteEndDate", objSetupHdModel.getDteEndDate());
			     				objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
			     				objJsonObject.put("strNatureOfBusinnes", objSetupHdModel.getStrNatureOfBusinnes());
			     				objJsonObject.put("strMultipleBillPrinting",objSetupHdModel.getStrMultipleBillPrinting());
			     				objJsonObject.put("strEnableKOT",objSetupHdModel.getStrEnableKOT());
			     				objJsonObject.put("strEffectOnPSP",objSetupHdModel.getStrEffectOnPSP());
			     				objJsonObject.put("strPrintVatNo", objSetupHdModel.getStrPrintVatNo());
			     				objJsonObject.put("strVatNo",objSetupHdModel.getStrVatNo());
			     				objJsonObject.put("strShowBill", objSetupHdModel.getStrShowBill());
			     				objJsonObject.put("strPrintServiceTaxNo", objSetupHdModel.getStrPrintServiceTaxNo());
			     				objJsonObject.put("strServiceTaxNo", objSetupHdModel.getStrServiceTaxNo());
			     				objJsonObject.put("strManualBillNo", objSetupHdModel.getStrManualBillNo());
			     				objJsonObject.put("strMenuItemDispSeq",objSetupHdModel.getStrMenuItemDispSeq());
			     				objJsonObject.put("strSenderEmailId", objSetupHdModel.getStrSenderEmailId());
			     				objJsonObject.put("strEmailPassword",objSetupHdModel.getStrEmailPassword());
			     				objJsonObject.put("strConfirmEmailPassword",objSetupHdModel.getStrConfirmEmailPassword());
			     				objJsonObject.put("strBody", objSetupHdModel.getStrBody());
			     				objJsonObject.put("strEmailServerName",objSetupHdModel.getStrEmailServerName());
			     				objJsonObject.put("strSMSApi", objSetupHdModel.getStrSMSApi());
			     				objJsonObject.put("strUserCreated", objSetupHdModel.getStrUserCreated());
			     				objJsonObject.put("strUserEdited", objSetupHdModel.getStrUserEdited());
			     				objJsonObject.put("dteDateCreated", objSetupHdModel.getDteDateCreated());
			     				objJsonObject.put("dteDateEdited", objSetupHdModel.getDteDateEdited());
			     				objJsonObject.put("strPOSType", objSetupHdModel.getStrPOSType());
			     				objJsonObject.put("strWebServiceLink",objSetupHdModel.getStrWebServiceLink());
			     				objJsonObject.put("strDataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
			     				objJsonObject.put("dteHOServerDate",objSetupHdModel.getDteHOServerDate());
			     				objJsonObject.put("strRFID",objSetupHdModel.getStrRFID());
			     				objJsonObject.put("strServerName",objSetupHdModel.getStrServerName());
			     				objJsonObject.put("strDBUserName", objSetupHdModel.getStrDBUserName());
			     				objJsonObject.put("strDBPassword",objSetupHdModel.getStrDBPassword());
			     				objJsonObject.put("strDatabaseName",objSetupHdModel.getStrDatabaseName());
			     				objJsonObject.put("strEnableKOTForDirectBiller", objSetupHdModel.getStrEnableKOTForDirectBiller());
			     				objJsonObject.put("intPinCode",objSetupHdModel.getIntPinCode());
			     				objJsonObject.put("strChangeTheme", objSetupHdModel.getStrChangeTheme());
			     				objJsonObject.put("dblMaxDiscount",objSetupHdModel.getDblMaxDiscount());
			     				objJsonObject.put("strAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
			     				objJsonObject.put("strMenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
			     				objJsonObject.put("strDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
			     				objJsonObject.put("intColumnSize", objSetupHdModel.getIntColumnSize());
			     				objJsonObject.put("strPrintType", objSetupHdModel.getStrPrintType());
			     				objJsonObject.put("strEditHomeDelivery", objSetupHdModel.getStrEditHomeDelivery());
			     				objJsonObject.put("strSlabBasedHDCharges", objSetupHdModel.getStrSlabBasedHDCharges());
			     				objJsonObject.put("strSkipWaiterAndPax", objSetupHdModel.getStrSkipWaiterAndPax());
			     				objJsonObject.put("strSkipWaiter",objSetupHdModel.getStrSkipWaiter());
			     				objJsonObject.put("strDirectKOTPrintMakeKOT", objSetupHdModel.getStrDirectKOTPrintMakeKOT());
			     				objJsonObject.put("strSkipPax",objSetupHdModel.getStrSkipPax());
			     				objJsonObject.put("strCRMInterface",objSetupHdModel.getStrCRMInterface());
			     				objJsonObject.put("strGetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
			     				objJsonObject.put("strPostWebserviceURL", objSetupHdModel.getStrPostWebserviceURL());
			     				objJsonObject.put("strOutletUID",objSetupHdModel.getStrOutletUID());
			     				objJsonObject.put("strPOSID",objSetupHdModel.getStrPOSID());
			     				objJsonObject.put("strStockInOption",objSetupHdModel.getStrStockInOption());
			     				objJsonObject.put("longCustSeries", objSetupHdModel.getStrCustSeries());
			     				objJsonObject.put("intAdvReceiptPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
			     				objJsonObject.put("strHomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
			     				objJsonObject.put("strBillStettlementSMS", objSetupHdModel.getStrBillStettlementSMS());
			       				objJsonObject.put("strBillFormatType",objSetupHdModel.getStrBillFormatType());
			     				objJsonObject.put("strActivePromotions",objSetupHdModel.getStrActivePromotions());
			     				objJsonObject.put("strSendHomeDelSMS",objSetupHdModel.getStrSendHomeDelSMS());
			     				objJsonObject.put("strSendBillSettlementSMS", objSetupHdModel.getStrSendBillSettlementSMS());
			     				objJsonObject.put("strSMSType", objSetupHdModel.getStrSMSType());
			     				objJsonObject.put("strPrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
			     				objJsonObject.put("strShowCustHelp",objSetupHdModel.getStrShowCustHelp());
			     				objJsonObject.put("strPrintOnVoidBill", objSetupHdModel.getStrPrintOnVoidBill());
			     				objJsonObject.put("strPostSalesDataToMMS", objSetupHdModel.getStrPostSalesDataToMMS());
			     				objJsonObject.put("strCustAreaMasterCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
			     				objJsonObject.put("strPriceFrom",objSetupHdModel.getStrPriceFrom());
			     				objJsonObject.put("strShowPrinterErrorMessage", objSetupHdModel.getStrShowPrinterErrorMessage());
			     				objJsonObject.put("strTouchScreenMode", objSetupHdModel.getStrTouchScreenMode());
			     				objJsonObject.put("strCardInterfaceType", objSetupHdModel.getStrCardInterfaceType());
			     				objJsonObject.put("strCMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
			     				objJsonObject.put("strCMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
			     				objJsonObject.put("strChangeQtyForExternalCode", objSetupHdModel.getStrChangeQtyForExternalCode());
			     				objJsonObject.put("strPointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
			     				objJsonObject.put("strCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
			     				objJsonObject.put("strManualAdvOrderNoCompulsory",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
			     				objJsonObject.put("strPrintManualAdvOrderNoOnBill", objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
			     				objJsonObject.put("strPrintModifierQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
			     				objJsonObject.put("strNoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
			     				objJsonObject.put("strMultipleKOTPrintYN",objSetupHdModel.getStrMultipleKOTPrintYN());
			     				objJsonObject.put("strItemQtyNumpad", objSetupHdModel.getStrItemQtyNumpad());
			     				objJsonObject.put("strTreatMemberAsTable", objSetupHdModel.getStrTreatMemberAsTable());
			     				objJsonObject.put("strKOTToLocalPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
			     				objJsonObject.put("strSettleBtnForDirectBillerBill",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
			     				objJsonObject.put("strDelBoySelCompulsoryOnDirectBiller", objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
			     				objJsonObject.put("strCMSMemberForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
			     				objJsonObject.put("strCMSMemberForKOTMPOS", objSetupHdModel.getStrCMSMemberForKOTMPOS());
			     				objJsonObject.put("strDontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
			     				objJsonObject.put("strPrintZeroAmtModifierInBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
			     				objJsonObject.put("strPrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
			     				objJsonObject.put("strCreditCardSlipNoCompulsoryYN",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
			     				objJsonObject.put("strCreditCardExpiryDateCompulsoryYN", objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
			     				objJsonObject.put("strSelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
			     				objJsonObject.put("strMultiWaiterSelectionOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
			     				objJsonObject.put("strMoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
			     				objJsonObject.put("strMoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
			     				objJsonObject.put("strCalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
			     				objJsonObject.put("strReceiverEmailId", objSetupHdModel.getStrReceiverEmailId());
			     				objJsonObject.put("strCalculateDiscItemWise",objSetupHdModel.getStrCalculateDiscItemWise());
			     				objJsonObject.put("strTakewayCustomerSelection",objSetupHdModel.getStrTakewayCustomerSelection());
			     				objJsonObject.put("StrShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
			     				objJsonObject.put("strItemType",objSetupHdModel.getStrItemType());
			     				objJsonObject.put("strAllowNewAreaMasterFromCustMaster", objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
			     				objJsonObject.put("strCustAddressSelectionForBill", objSetupHdModel.getStrCustAddressSelectionForBill());
			     				objJsonObject.put("strGenrateMI",objSetupHdModel.getStrGenrateMI());
			     				objJsonObject.put("strFTPAddress", objSetupHdModel.getStrFTPAddress());
			     				objJsonObject.put("strFTPServerUserName", objSetupHdModel.getStrFTPServerUserName());
			     				objJsonObject.put("strFTPServerPass",objSetupHdModel.getStrFTPServerPass());
			     				objJsonObject.put("strAllowToCalculateItemWeight", objSetupHdModel.getStrAllowToCalculateItemWeight());
			     				objJsonObject.put("strShowBillsDtlType", objSetupHdModel.getStrShowBillsDtlType());
			     				objJsonObject.put("strPrintTaxInvoiceOnBill", objSetupHdModel.getStrPrintTaxInvoiceOnBill());
			     				objJsonObject.put("strPrintInclusiveOfAllTaxesOnBill",objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
			     				objJsonObject.put("strApplyDiscountOn",objSetupHdModel.getStrApplyDiscountOn());
			     				objJsonObject.put("strMemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
			     				objJsonObject.put("strPrintBillYN", objSetupHdModel.getStrPrintBillYN());
			     				objJsonObject.put("strVatAndServiceTaxFromPos",objSetupHdModel.getStrVatAndServiceTaxFromPos());
			     				objJsonObject.put("strMemberCodeForMakeBillInMPOS",objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
			     				objJsonObject.put("strItemWiseKOTYN", objSetupHdModel.getStrItemWiseKOTYN());
			     				objJsonObject.put("strLastPOSForDayEnd", objSetupHdModel.getStrLastPOSForDayEnd());
			     				objJsonObject.put("strCMSPostingType", objSetupHdModel.getStrCMSPostingType());
			     				objJsonObject.put("strPopUpToApplyPromotionsOnBill",objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
			     				objJsonObject.put("strSelectCustomerCodeFromCardSwipe", objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
			     				objJsonObject.put("strCheckDebitCardBalOnTransactions", objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
			     				objJsonObject.put("strSettlementsFromPOSMaster", objSetupHdModel.getStrSettlementsFromPOSMaster());
			     				objJsonObject.put("strShiftWiseDayEndYN", objSetupHdModel.getStrShiftWiseDayEndYN());
			     				objJsonObject.put("strProductionLinkup", objSetupHdModel.getStrProductionLinkup());
			     				objJsonObject.put("strLockDataOnShift",objSetupHdModel.getStrLockDataOnShift());
			     				objJsonObject.put("strWSClientCode", objSetupHdModel.getStrWSClientCode());
			     				objJsonObject.put("strPOSCode", objSetupHdModel.getStrPOSCode());
			     				objJsonObject.put("strEnableBillSeries",objSetupHdModel.getStrEnableBillSeries());
			     				objJsonObject.put("strEnablePMSIntegrationYN",objSetupHdModel.getStrEnablePMSIntegrationYN());
			     				objJsonObject.put("strPrintTimeOnBill", objSetupHdModel.getStrPrintTimeOnBill());
			     				objJsonObject.put("strPrintTDHItemsInBill", objSetupHdModel.getStrPrintTDHItemsInBill());
			     				objJsonObject.put("strPrintRemarkAndReasonForReprint",objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
			     			 
			     				objJsonObject.put("intDaysBeforeOrderToCancel", objSetupHdModel.getIntDaysBeforeOrderToCancel());
			     				objJsonObject.put("intNoOfDelDaysForAdvOrder", objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
			     				objJsonObject.put("intNoOfDelDaysForUrgentOrder", objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
			     				objJsonObject.put("strSetUpToTimeForAdvOrder",objSetupHdModel.getStrSetUpToTimeForAdvOrder());
			     				objJsonObject.put("strSetUpToTimeForUrgentOrder", objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
			     				objJsonObject.put("strUpToTimeForAdvOrder",objSetupHdModel.getStrUpToTimeForAdvOrder());
			     				objJsonObject.put("strUpToTimeForUrgentOrder", objSetupHdModel.getStrUpToTimeForUrgentOrder());
			     				objJsonObject.put("strEnableBothPrintAndSettleBtnForDB",objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
			     				objJsonObject.put("strInrestoPOSIntegrationYN", objSetupHdModel.getStrInrestoPOSIntegrationYN());
			     				objJsonObject.put("strInrestoPOSWebServiceURL", objSetupHdModel.getStrInrestoPOSWebServiceURL());
			     				objJsonObject.put("strInrestoPOSId", objSetupHdModel.getStrInrestoPOSId());
			     				objJsonObject.put("strInrestoPOSKey",objSetupHdModel.getStrInrestoPOSKey());
			     				objJsonObject.put("strCarryForwardFloatAmtToNextDay", objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
			     				objJsonObject.put("strOpenCashDrawerAfterBillPrintYN",objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
			     				objJsonObject.put("strPropertyWiseSalesOrderYN",objSetupHdModel.getStrPropertyWiseSalesOrderYN());
			     				objJsonObject.put("strDataPostFlag",objSetupHdModel.getStrDataPostFlag());
			         			objJsonObject.put("strShowItemDetailsGrid", objSetupHdModel.getStrShowItemDetailsGrid());
			     				
			     				objJsonObject.put("strShowPopUpForNextItemQuantity", objSetupHdModel.getStrShowPopUpForNextItemQuantity());
			     				
			     				objJsonObject.put("strJioMoneyIntegration", objSetupHdModel.getStrJioMoneyIntegration());
			     				objJsonObject.put("strJioWebServiceUrl",objSetupHdModel.getStrJioWebServiceUrl());
			     				
			     				objJsonObject.put("strJioMID", objSetupHdModel.getStrJioMID());
			     				
			     				objJsonObject.put("strJioTID", objSetupHdModel.getStrJioTID());
			     				
			     				objJsonObject.put("strJioActivationCode", objSetupHdModel.getStrJioActivationCode());
			     				objJsonObject.put("strJioDeviceID",objSetupHdModel.getStrJioDeviceID());
			     				objJsonObject.put("strNewBillSeriesForNewDay", objSetupHdModel.getStrNewBillSeriesForNewDay());
			     				objJsonObject.put("strShowReportsPOSWise",objSetupHdModel.getStrShowReportsPOSWise());
			         		   
			     				objJsonObject.put("strEnableDineIn", objSetupHdModel.getStrEnableDineIn());
			    				objJsonObject.put("strAutoAreaSelectionInMakeKOT",objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
			    			
			                     dataObjectArray.add(objJsonObject);
			                    
			                 }
		             }
	        	 }
	         }
	           rootObject.put("tblsetup", dataObjectArray);
	           funPOSTDataToHO(rootObject);
	    }
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
	 }
	 private void funPostBillSeriesDataHO()
	 {
	        boolean flgResult = false;
	        StringBuilder sql = new StringBuilder();
	     
	        try
	        {
	            JSONObject rootObject = new JSONObject();
	            JSONArray dataObjectArray = new JSONArray();

	            sql.append("select * from tblbillseries where strDataPostFlag='N'");
	            
    			 List list = objBaseService.funGetList(sql, "sql");
    			 for(int j=0; j<list.size();j++)
    	         {	
    				 Object[] obj=(Object[])list.get(j);
	                JSONObject dataObject = new JSONObject();
	                dataObject.put("POSCode", obj[0].toString());
	                dataObject.put("Type",  obj[1].toString());
	                dataObject.put("BillSeries", obj[2].toString());
	                dataObject.put("LastNo",  obj[3].toString());
	                dataObject.put("Codes",  obj[4].toString());
	                dataObject.put("Names",  obj[5].toString());
	                dataObject.put("UserCreated",  obj[6].toString());
	                dataObject.put("UserEdited",  obj[7].toString());
	                dataObject.put("DateCreated",  obj[8].toString());
	                dataObject.put("DateEdited", obj[9].toString());
	                dataObject.put("DataPostFlag",  obj[10].toString());
	                dataObject.put("ClientCode",  obj[11].toString());
	                dataObject.put("PropertyCode",  obj[12].toString());
	                dataObject.put("PrintGTOfOtherBills", obj[13].toString());
	                dataObject.put("PrintIncOfTaxOnBill", obj[14].toString());

	                dataObjectArray.add(dataObject);
	            }
	          
	            rootObject.put("tblbillseries", dataObjectArray);
	            funPOSTDataToHO(rootObject);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	       
	    }
	
	 
	public void funPOSTDataToHO(JSONObject jObj)
	{
		String posURL = clsPOSGlobalFunctionsController.POSWSURL+"/POSIntegration/funPostPropertySetup";
		try{
			
			URL url = new URL(posURL);
			
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
	        OutputStream os = conn.getOutputStream();
	        os.write(jObj.toString().getBytes());
	        os.flush();
	        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	        {
	            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	        }
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	        String output = "", op = "";

	        while ((output = br.readLine()) != null)
	        {
	            op += output;
	        }
	        System.out.println("Result= " + op);
	        conn.disconnect();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	 
	@RequestMapping(value = "/fetchDeviceID", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFetchDeviceID(HttpServletRequest req)
    {
		String IP="localhost", PORT="5150";
		objPOSGlobal.funStartSocketBat();
		JSONObject jobj=new JSONObject();
      try
        {
            String host = IP;	//IP address of the server
		    int port = Integer.parseInt(PORT) ;	//Port on which the socket is going to connect
		    String response="";
            StringBuilder Res = new StringBuilder();
            String SendData ="getDongleId"; //getDongleId
            System.out.println("Request String:" + SendData);
            try (Socket s = new Socket(host,port)) //Creating socket class
             {
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());	//creating outputstream to send data to server
                DataInputStream din = new DataInputStream(s.getInputStream());	//creating inputstream to receive data from server
                //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                byte[] str = SendData.getBytes("UTF-8");
                dout.write(str, 0, str.length);
               // System.out.println("Send data value = "+ SendData);
                dout.flush();	//Flush the streams
                
                byte[] bs = new byte[10024];
                din.read(bs);
                char c;
                for (byte b:bs)
                {
                    c = (char)b;
                    response = Res.append("").append(c).toString();
                }
                System.out.println("Device ID: " + response);
                dout.close();	//Closing the output stream
                din.close();	//Closing the input stream
            } //creating outputstream to send data to server
            jobj.put("deviceID",response.trim()); 
            return jobj;
        }
      catch (Exception e)
      {
            System.out.println("Exception:" + e);
            return jobj;
      }
    }
    
	public void funSaveMapMyDevice( String mid, String tid, String deviceId,String strJioMoneyActivationCode,String posCode)
	    {
		 
		  objPOSGlobal.funStartSocketBat();
	        try
	        {
	           
	         
	            String RequestType="1008";
	            String Amount= "0.00";
	          
	            String manufacturer= "JioPayDevice";//JioPayDevice
	            String deviceStatus= "A";
	            String linkDate=getCurrentDate();
	            String deLinkDate= deLinkedDate();
	            String superMerchantId= mid;
	            String userName= "9820001759";
	            String businessLegalName= "Sanguine Software";

	            String requestData = "requestType=" + RequestType +
	            "&mid=" + mid +
	            "&deviceId=" + deviceId +
	            "&manufacturer=" + manufacturer +
	            "&deviceStatus=" + deviceStatus +
	            "&linkDate=" + linkDate +
	            "&deLinkDate=" + deLinkDate +
	            "&superMerchantId=" + superMerchantId +
	            "&userName=" + userName +
	            "&businessLegalName=" + businessLegalName +
	            "&tid=" + tid;

	            System.out.println("RequestData : "+requestData);
	            String Response="";
	            Response =  objPOSGlobal.funMakeTransaction(requestData, RequestType, mid, tid, Amount,"PRE_PROD","localhost","5150",posCode, strJioMoneyActivationCode);
	            //System.out.println("Server Response: " + response);
	            System.out.println(Response);

	            String strRes = Response.trim();
	            JSONParser jsonParser = new JSONParser();
	            JSONObject jsonObject = (JSONObject) jsonParser.parse(strRes);
	            // String responseCode = (String) jsonObject.get("responseCode");
	            JSONArray lang= (JSONArray) jsonObject.get("result");
	            JSONParser jsonParser1 = new JSONParser();
	            JSONObject jsonObject1 = (JSONObject) jsonParser1.parse(lang.get(0).toString());
	            // String responseCode = (String) jsonObject.get("responseCode");
	            String responseCode= (String) jsonObject1.get("messageCode");

	            //String responseCode = lang.get(8).toString();

	            
	        }catch (Exception e)
	        {
	            System.out.println("Exception:" + e);
	        }
	    }
	    
	  public String getCurrentDate()
	    {
	        Date currentDate = new Date();
	        String strCurrentDate = ( currentDate.getDate()+ "/" + (currentDate.getMonth() + 1) + "/" +(currentDate.getYear() + 1900));
	        return strCurrentDate;
	    }
	  public String deLinkedDate()
	    {
	        String currentDate=getCurrentDate();
	        String[]date1=currentDate.split("/");
	        int year=30+Integer.parseInt(date1[2]);
	        String nextDate = ( date1[0]+ "/" + date1[1] + "/" +String.valueOf(year));
	        return nextDate;
	    }
}
