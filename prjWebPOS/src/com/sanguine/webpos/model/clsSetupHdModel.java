package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

import java.sql.Blob;

@Entity
@Table(name = "tblsetup")
@IdClass(clsSetupModel_ID.class)
public class clsSetupHdModel extends clsBaseModel implements Serializable 
{
	private static final long	serialVersionUID	= 1L;

	public clsSetupHdModel()
	{
	}

	public clsSetupHdModel(clsSetupModel_ID objModelID)
	{
		strClientCode = objModelID.getStrClientCode();
		strPOSCode = objModelID.getStrPOSCode();
	}

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")),
			@AttributeOverride(name = "strPOSCode", column = @Column(name = "strPOSCode"))
	})
//Variable Declaration
	@Column(name = "strClientCode")
	private String	strClientCode;

	@Column(name = "strClientName")
	private String	strClientName;

	@Column(name = "strAddressLine1")
	private String	strAddressLine1;

	@Column(name = "strAddressLine2")
	private String	strAddressLine2;

	@Column(name = "strAddressLine3")
	private String	strAddressLine3;

	@Column(name = "strEmail")
	private String	strEmail;

	@Column(name = "strBillFooter")
	private String	strBillFooter;

	@Column(name = "strBillFooterStatus")
	private String	strBillFooterStatus;

	@Column(name = "intBillPaperSize")
	private long	intBillPaperSize;

	@Column(name = "strNegativeBilling")
	private String	strNegativeBilling;

	@Column(name = "strDayEnd")
	private String	strDayEnd;

	@Column(name = "strPrintMode")
	private String	strPrintMode;

	@Column(name = "strDiscountNote")
	private String	strDiscountNote;

	@Column(name = "strCityName")
	private String	strCityName;

	@Column(name = "strState")
	private String	strState;

	@Column(name = "strCountry")
	private String	strCountry;

	@Column(name = "intTelephoneNo")
	private long	intTelephoneNo;

	@Column(name = "dteStartDate")
	private String	dteStartDate;

	@Column(name = "dteEndDate")
	private String	dteEndDate;

	@Column(name = "strNatureOfBusinnes")
	private String	strNatureOfBusinnes;

	@Column(name = "strMultipleBillPrinting")
	private String	strMultipleBillPrinting;

	@Column(name = "strEnableKOT")
	private String	strEnableKOT;

	@Column(name = "strEffectOnPSP")
	private String	strEffectOnPSP;

	@Column(name = "strPrintVatNo")
	private String	strPrintVatNo;

	@Column(name = "strVatNo")
	private String	strVatNo;

	@Column(name = "strShowBill")
	private String	strShowBill;

	@Column(name = "strPrintServiceTaxNo")
	private String	strPrintServiceTaxNo;

	@Column(name = "strServiceTaxNo")
	private String	strServiceTaxNo;

	@Column(name = "strManualBillNo")
	private String	strManualBillNo;

	@Column(name = "strMenuItemDispSeq")
	private String	strMenuItemDispSeq;

	@Column(name = "strSenderEmailId")
	private String	strSenderEmailId;

	@Column(name = "strEmailPassword")
	private String	strEmailPassword;

	@Column(name = "strConfirmEmailPassword")
	private String	strConfirmEmailPassword;

	@Column(name = "strBody")
	private String	strBody;

	@Column(name = "strEmailServerName")
	private String	strEmailServerName;

	@Column(name = "strSMSApi")
	private String	strSMSApi;

	@Column(name = "strUserCreated")
	private String	strUserCreated;

	@Column(name = "strUserEdited")
	private String	strUserEdited;

	@Column(name = "dteDateCreated")
	private String	dteDateCreated;

	@Column(name = "dteDateEdited")
	private String	dteDateEdited;

	@Column(name = "strPOSType")
	private String	strPOSType;

	@Column(name = "strWebServiceLink")
	private String	strWebServiceLink;

	@Column(name = "strDataSendFrequency")
	private String	strDataSendFrequency;

	@Column(name = "dteHOServerDate")
	private String	dteHOServerDate;

	@Column(name = "strRFID")
	private String	strRFID;

	@Column(name = "strServerName")
	private String	strServerName;

	@Column(name = "strDBUserName")
	private String	strDBUserName;

	@Column(name = "strDBPassword")
	private String	strDBPassword;

	@Column(name = "strDatabaseName")
	private String	strDatabaseName;

	@Column(name = "strEnableKOTForDirectBiller")
	private String	strEnableKOTForDirectBiller;

	@Column(name = "intPinCode")
	private long	intPinCode;

	@Column(name = "strChangeTheme")
	private String	strChangeTheme;

	@Column(name = "dblMaxDiscount")
	private double	dblMaxDiscount;

	@Column(name = "strAreaWisePricing")
	private String	strAreaWisePricing;

	@Column(name = "strMenuItemSortingOn")
	private String	strMenuItemSortingOn;

	@Column(name = "strDirectAreaCode")
	private String	strDirectAreaCode;

	@Column(name = "intColumnSize")
	private long	intColumnSize;

	@Column(name = "strPrintType")
	private String	strPrintType;

	@Column(name = "strEditHomeDelivery")
	private String	strEditHomeDelivery;

	@Column(name = "strSlabBasedHDCharges")
	private String	strSlabBasedHDCharges;

	@Column(name = "strSkipWaiterAndPax")
	private String	strSkipWaiterAndPax;

	@Column(name = "strSkipWaiter")
	private String	strSkipWaiter;

	@Column(name = "strDirectKOTPrintMakeKOT")
	private String	strDirectKOTPrintMakeKOT;

	@Column(name = "strSkipPax")
	private String	strSkipPax;

	@Column(name = "strCRMInterface")
	private String	strCRMInterface;

	@Column(name = "strGetWebserviceURL")
	private String	strGetWebserviceURL;

	@Column(name = "strPostWebserviceURL")
	private String	strPostWebserviceURL;

	@Column(name = "strOutletUID")
	private String	strOutletUID;

	@Column(name = "strPOSID")
	private String	strPOSID;

	@Column(name = "strStockInOption")
	private String	strStockInOption;

	@Column(name = "longCustSeries")
	private String	strCustSeries;

	@Column(name = "intAdvReceiptPrintCount")
	private long	intAdvReceiptPrintCount;

	@Column(name = "strHomeDeliverySMS")
	private String	strHomeDeliverySMS;

	@Column(name = "strBillStettlementSMS")
	private String	strBillStettlementSMS;

	@Column(name = "strBillFormatType")
	private String	strBillFormatType;

	@Column(name = "strActivePromotions")
	private String	strActivePromotions;

	@Column(name = "strSendHomeDelSMS")
	private String	strSendHomeDelSMS;

	@Column(name = "strSendBillSettlementSMS")
	private String	strSendBillSettlementSMS;

	@Column(name = "strSMSType")
	private String	strSMSType;

	@Column(name = "strPrintShortNameOnKOT")
	private String	strPrintShortNameOnKOT;

	@Column(name = "strShowCustHelp")
	private String	strShowCustHelp;

	@Column(name = "strPrintOnVoidBill")
	private String	strPrintOnVoidBill;

	@Column(name = "strPostSalesDataToMMS")
	private String	strPostSalesDataToMMS;

	@Column(name = "strCustAreaMasterCompulsory")
	private String	strCustAreaMasterCompulsory;

	@Column(name = "strPriceFrom")
	private String	strPriceFrom;

	@Column(name = "strShowPrinterErrorMessage")
	private String	strShowPrinterErrorMessage;

	@Column(name = "strTouchScreenMode")
	private String	strTouchScreenMode;

	@Column(name = "strCardInterfaceType")
	private String	strCardInterfaceType;

	@Column(name = "strCMSIntegrationYN")
	private String	strCMSIntegrationYN;

	@Column(name = "strCMSWebServiceURL")
	private String	strCMSWebServiceURL;

	@Column(name = "strChangeQtyForExternalCode")
	private String	strChangeQtyForExternalCode;

	@Column(name = "strPointsOnBillPrint")
	private String	strPointsOnBillPrint;

	@Column(name = "strCMSPOSCode")
	private String	strCMSPOSCode;

	@Column(name = "strManualAdvOrderNoCompulsory")
	private String	strManualAdvOrderNoCompulsory;

	@Column(name = "strPrintManualAdvOrderNoOnBill")
	private String	strPrintManualAdvOrderNoOnBill;

	@Column(name = "strPrintModifierQtyOnKOT")
	private String	strPrintModifierQtyOnKOT;

	@Column(name = "strNoOfLinesInKOTPrint")
	private long	strNoOfLinesInKOTPrint;

	@Column(name = "strMultipleKOTPrintYN")
	private String	strMultipleKOTPrintYN;

	@Column(name = "strItemQtyNumpad")
	private String	strItemQtyNumpad;

	@Column(name = "strTreatMemberAsTable")
	private String	strTreatMemberAsTable;

	@Column(name = "strKOTToLocalPrinter")
	private String	strKOTToLocalPrinter;

	@Column(name = "strSettleBtnForDirectBillerBill")
	private String	strSettleBtnForDirectBillerBill;

	@Column(name = "strDelBoySelCompulsoryOnDirectBiller")
	private String	strDelBoySelCompulsoryOnDirectBiller;

	@Column(name = "strCMSMemberForKOTJPOS")
	private String	strCMSMemberForKOTJPOS;

	@Column(name = "strCMSMemberForKOTMPOS")
	private String	strCMSMemberForKOTMPOS;

	@Column(name = "strDontShowAdvOrderInOtherPOS")
	private String	strDontShowAdvOrderInOtherPOS;

	@Column(name = "strPrintZeroAmtModifierInBill")
	private String	strPrintZeroAmtModifierInBill;

	@Column(name = "strPrintKOTYN")
	private String	strPrintKOTYN;

	@Column(name = "strCreditCardSlipNoCompulsoryYN")
	private String	strCreditCardSlipNoCompulsoryYN;

	@Column(name = "strCreditCardExpiryDateCompulsoryYN")
	private String	strCreditCardExpiryDateCompulsoryYN;

	@Column(name = "strSelectWaiterFromCardSwipe")
	private String	strSelectWaiterFromCardSwipe;

	@Column(name = "strMultiWaiterSelectionOnMakeKOT")
	private String	strMultiWaiterSelectionOnMakeKOT;

	@Column(name = "strMoveTableToOtherPOS")
	private String	strMoveTableToOtherPOS;

	@Column(name = "strMoveKOTToOtherPOS")
	private String	strMoveKOTToOtherPOS;

	@Column(name = "strCalculateTaxOnMakeKOT")
	private String	strCalculateTaxOnMakeKOT;

	@Column(name = "strReceiverEmailId")
	private String	strReceiverEmailId;

	@Column(name = "strCalculateDiscItemWise")
	private String	strCalculateDiscItemWise;

	@Column(name = "strTakewayCustomerSelection")
	private String	strTakewayCustomerSelection;

	@Column(name = "StrShowItemStkColumnInDB")
	private String	StrShowItemStkColumnInDB;

	@Column(name = "strItemType")
	private String	strItemType;

	@Column(name = "strAllowNewAreaMasterFromCustMaster")
	private String	strAllowNewAreaMasterFromCustMaster;

	@Column(name = "strCustAddressSelectionForBill")
	private String	strCustAddressSelectionForBill;

	@Column(name = "strGenrateMI")
	private String	strGenrateMI;

	@Column(name = "strFTPAddress")
	private String	strFTPAddress;

	@Column(name = "strFTPServerUserName")
	private String	strFTPServerUserName;

	@Column(name = "strFTPServerPass")
	private String	strFTPServerPass;

	@Column(name = "strAllowToCalculateItemWeight")
	private String	strAllowToCalculateItemWeight;

	@Column(name = "strShowBillsDtlType")
	private String	strShowBillsDtlType;

	@Column(name = "strPrintTaxInvoiceOnBill")
	private String	strPrintTaxInvoiceOnBill;

	@Column(name = "strPrintInclusiveOfAllTaxesOnBill")
	private String	strPrintInclusiveOfAllTaxesOnBill;

	@Column(name = "strApplyDiscountOn")
	private String	strApplyDiscountOn;

	@Column(name = "strMemberCodeForKotInMposByCardSwipe")
	private String	strMemberCodeForKotInMposByCardSwipe;

	@Column(name = "strPrintBillYN")
	private String	strPrintBillYN;

	@Column(name = "strVatAndServiceTaxFromPos")
	private String	strVatAndServiceTaxFromPos;

	@Column(name = "strMemberCodeForMakeBillInMPOS")
	private String	strMemberCodeForMakeBillInMPOS;

	@Column(name = "strItemWiseKOTYN")
	private String	strItemWiseKOTYN;

	@Column(name = "strLastPOSForDayEnd")
	private String	strLastPOSForDayEnd;

	@Column(name = "strCMSPostingType")
	private String	strCMSPostingType;

	@Column(name = "strPopUpToApplyPromotionsOnBill")
	private String	strPopUpToApplyPromotionsOnBill;

	@Column(name = "strSelectCustomerCodeFromCardSwipe")
	private String	strSelectCustomerCodeFromCardSwipe;

	@Column(name = "strCheckDebitCardBalOnTransactions")
	private String	strCheckDebitCardBalOnTransactions;

	@Column(name = "strSettlementsFromPOSMaster")
	private String	strSettlementsFromPOSMaster;

	@Column(name = "strShiftWiseDayEndYN")
	private String	strShiftWiseDayEndYN;

	@Column(name = "strProductionLinkup")
	private String	strProductionLinkup;

	@Column(name = "strLockDataOnShift")
	private String	strLockDataOnShift;

	@Column(name = "strWSClientCode")
	private String	strWSClientCode;

	@Column(name = "strPOSCode")
	private String	strPOSCode;

	@Column(name = "strEnableBillSeries")
	private String	strEnableBillSeries;

	@Column(name = "strEnablePMSIntegrationYN")
	private String	strEnablePMSIntegrationYN;

	@Column(name = "strPrintTimeOnBill")
	private String	strPrintTimeOnBill;

	@Column(name = "strPrintTDHItemsInBill")
	private String	strPrintTDHItemsInBill;

	@Column(name = "strPrintRemarkAndReasonForReprint")
	private String	strPrintRemarkAndReasonForReprint;

	@Column(name = "intDaysBeforeOrderToCancel")
	private long	intDaysBeforeOrderToCancel;

	@Column(name = "intNoOfDelDaysForAdvOrder")
	private long	intNoOfDelDaysForAdvOrder;

	@Column(name = "intNoOfDelDaysForUrgentOrder")
	private long	intNoOfDelDaysForUrgentOrder;

	@Column(name = "strSetUpToTimeForAdvOrder")
	private String	strSetUpToTimeForAdvOrder;

	@Column(name = "strSetUpToTimeForUrgentOrder")
	private String	strSetUpToTimeForUrgentOrder;

	@Column(name = "strUpToTimeForAdvOrder")
	private String	strUpToTimeForAdvOrder;

	@Column(name = "strUpToTimeForUrgentOrder")
	private String	strUpToTimeForUrgentOrder;

	@Column(name = "strEnableBothPrintAndSettleBtnForDB")
	private String	strEnableBothPrintAndSettleBtnForDB;

	@Column(name = "strInrestoPOSIntegrationYN")
	private String	strInrestoPOSIntegrationYN;

	@Column(name = "strInrestoPOSWebServiceURL")
	private String	strInrestoPOSWebServiceURL;

	@Column(name = "strInrestoPOSId")
	private String	strInrestoPOSId;

	@Column(name = "strInrestoPOSKey")
	private String	strInrestoPOSKey;

	@Column(name = "strCarryForwardFloatAmtToNextDay")
	private String	strCarryForwardFloatAmtToNextDay;

	@Column(name = "strOpenCashDrawerAfterBillPrintYN")
	private String	strOpenCashDrawerAfterBillPrintYN;

	@Column(name = "strPropertyWiseSalesOrderYN")
	private String	strPropertyWiseSalesOrderYN;

	@Column(name = "strDataPostFlag")
	private String	strDataPostFlag;

	@Column(name = "strShowItemDetailsGrid")
	private String	strShowItemDetailsGrid;

	@Column(name = "strShowPopUpForNextItemQuantity")
	private String	strShowPopUpForNextItemQuantity;

	@Column(name = "strJioMoneyIntegration")
	private String	strJioMoneyIntegration;

	@Column(name = "strJioWebServiceUrl")
	private String	strJioWebServiceUrl;
	
	@Column(name = "strJioMID")
	private String	strJioMID;

	@Column(name = "strJioTID")
	private String	strJioTID;

	@Column(name = "strJioActivationCode")
	private String	strJioActivationCode;

	@Column(name = "strJioDeviceID")
	private String	strJioDeviceID;

	@Column(name = "strNewBillSeriesForNewDay")
	private String	strNewBillSeriesForNewDay;

	@Column(name = "strShowReportsPOSWise")
	private String	strShowReportsPOSWise;

	@Column(name = "blobReportImage")
	private Blob	blobReportImage;
	
	@Column(name = "strEnableDineIn")
	private String	strEnableDineIn;

	@Column(name = "strAutoAreaSelectionInMakeKOT")
	private String	strAutoAreaSelectionInMakeKOT;


	@Column(name="intShowPopItemsOfDays")
	private long intShowPopItemsOfDays;

	@Column(name="strPostSalesCostOrLoc")
	private String strPostSalesCostOrLoc;
	
	@Column(name="dblNoOfDecimalPlace")
	private int dblNoOfDecimalPlace;
	
	@Column(name = "strHomeDeliveryAreaForDirectBiller")
	private String strHomeDeliveryAreaForDirectBiller;
	
	@Column(name = "strTakeAwayAreaForDirectBiller")
	private String strTakeAwayAreaForDirectBiller;
	
	@Column(name = "strAreaWiseCostCenterKOTPrintingYN")
	private String strAreaWiseCostCenterKOTPrintingYN;
	
	@Column(name="strRoundOffBillFinalAmt")
	private String strRoundOffBillFinalAmt;
	
	@Column(name="strPrintMoveTableMoveKOTYN")
	private String strPrintMoveTableMoveKOTYN;
	
	@Column(name="strPrintItemsOnMoveKOTMoveTable")
	private String strPrintItemsOnMoveKOTMoveTable;
	
	@Column(name="strSendDBBackupOnClientMail")
	private String strSendDBBackupOnClientMail;
	
	@Column(name="strPrintQtyTotal")
	private String strPrintQtyTotal;
	
	@Column(name="strPrintOrderNoOnBillYN")
	private String strPrintOrderNoOnBillYN;
	
	@Column(name="strAutoAddKOTToBill")
	private String strAutoAddKOTToBill;
	
	@Column(name="strPrintDeviceAndUserDtlOnKOTYN")
	private String strPrintDeviceAndUserDtlOnKOTYN;
	
	@Column(name="strFireCommunication")
	private String strFireCommunication;

	@Column(name="strRemoveSCTaxCode")
	private String strRemoveSCTaxCode;
	
	@Column(name="strLockTableForWaiter")
	private String strLockTableForWaiter;
	

	@Column(name="dblUSDConverionRate")
	private double dblUSDConverionRate;
	
	@Column(name="strShowReportsInCurrency")
	private String strShowReportsInCurrency;

	@Column(name="strPOSToMMSPostingCurrency")
	private String strPOSToMMSPostingCurrency;

	@Column(name="strPOSToWebBooksPostingCurrency")
	private String strPOSToWebBooksPostingCurrency;
	
	@Column(name="strBenowIntegrationYN")
	private String strBenowIntegrationYN;

	@Column(name="strXEmail")
	private String strXEmail;

	@Column(name="strMerchantCode")
	private String strMerchantCode;

	@Column(name="strAuthenticationKey")
	private String strAuthenticationKey;
	
	@Column(name="strSalt")
	private String strSalt;

	@Column(name="strWERAOnlineOrderIntegration")
	private String strWERAOnlineOrderIntegration;
	
	@Column(name="strWERAMerchantOutletId")
	private String strWERAMerchantOutletId;

	@Column(name="strWERAAuthenticationAPIKey")
	private String strWERAAuthenticationAPIKey;
	
	@Column(name="strEnableNFCInterface")
	private String strEnableNFCInterface;
	
	
	
	
//Setter-Getter Methods
	public String getStrClientCode()
	{
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = (String) setDefaultValue(strClientCode, " ");
	}

	public String getStrClientName()
	{
		return strClientName;
	}

	public void setStrClientName(String strClientName)
	{
		this.strClientName = (String) setDefaultValue(strClientName, " ");
	}

	public String getStrAddressLine1()
	{
		return strAddressLine1;
	}

	public void setStrAddressLine1(String strAddressLine1)
	{
		this.strAddressLine1 = (String) setDefaultValue(strAddressLine1, " ");
	}

	public String getStrAddressLine2()
	{
		return strAddressLine2;
	}

	public void setStrAddressLine2(String strAddressLine2)
	{
		this.strAddressLine2 = (String) setDefaultValue(strAddressLine2, " ");
	}

	public String getStrAddressLine3()
	{
		return strAddressLine3;
	}

	public void setStrAddressLine3(String strAddressLine3)
	{
		this.strAddressLine3 = (String) setDefaultValue(strAddressLine3, " ");
	}

	public String getStrEmail()
	{
		return strEmail;
	}

	public void setStrEmail(String strEmail)
	{
		this.strEmail = (String) setDefaultValue(strEmail, " ");
	}

	public String getStrBillFooter()
	{
		return strBillFooter;
	}

	public void setStrBillFooter(String strBillFooter)
	{
		this.strBillFooter = (String) setDefaultValue(strBillFooter, " ");
	}

	public String getStrBillFooterStatus()
	{
		return strBillFooterStatus;
	}

	public void setStrBillFooterStatus(String strBillFooterStatus)
	{
		this.strBillFooterStatus = (String) setDefaultValue(strBillFooterStatus, " ");
	}

	public long getIntBillPaperSize()
	{
		return intBillPaperSize;
	}

	public void setIntBillPaperSize(long intBillPaperSize)
	{
		this.intBillPaperSize = (Long) setDefaultValue(intBillPaperSize, "0");
	}

	public String getStrNegativeBilling()
	{
		return strNegativeBilling;
	}

	public void setStrNegativeBilling(String strNegativeBilling)
	{
		this.strNegativeBilling = (String) setDefaultValue(strNegativeBilling, " ");
	}

	public String getStrDayEnd()
	{
		return strDayEnd;
	}

	public void setStrDayEnd(String strDayEnd)
	{
		this.strDayEnd = (String) setDefaultValue(strDayEnd, " ");
	}

	public String getStrPrintMode()
	{
		return strPrintMode;
	}

	public void setStrPrintMode(String strPrintMode)
	{
		this.strPrintMode = (String) setDefaultValue(strPrintMode, " ");
	}

	public String getStrDiscountNote()
	{
		return strDiscountNote;
	}

	public void setStrDiscountNote(String strDiscountNote)
	{
		this.strDiscountNote = (String) setDefaultValue(strDiscountNote, " ");
	}

	public String getStrCityName()
	{
		return strCityName;
	}

	public void setStrCityName(String strCityName)
	{
		this.strCityName = (String) setDefaultValue(strCityName, " ");
	}

	public String getStrState()
	{
		return strState;
	}

	public void setStrState(String strState)
	{
		this.strState = (String) setDefaultValue(strState, " ");
	}

	public String getStrCountry()
	{
		return strCountry;
	}

	public void setStrCountry(String strCountry)
	{
		this.strCountry = (String) setDefaultValue(strCountry, " ");
	}

	public long getIntTelephoneNo()
	{
		return intTelephoneNo;
	}

	public void setIntTelephoneNo(long intTelephoneNo)
	{
		this.intTelephoneNo = (Long) setDefaultValue(intTelephoneNo, "0");
	}

	public String getDteStartDate()
	{
		return dteStartDate;
	}

	public void setDteStartDate(String dteStartDate)
	{
		this.dteStartDate = dteStartDate;
	}

	public String getDteEndDate()
	{
		return dteEndDate;
	}

	public void setDteEndDate(String dteEndDate)
	{
		this.dteEndDate = dteEndDate;
	}

	public String getStrNatureOfBusinnes()
	{
		return strNatureOfBusinnes;
	}

	public void setStrNatureOfBusinnes(String strNatureOfBusinnes)
	{
		this.strNatureOfBusinnes = (String) setDefaultValue(strNatureOfBusinnes, " ");
	}

	public String getStrMultipleBillPrinting()
	{
		return strMultipleBillPrinting;
	}

	public void setStrMultipleBillPrinting(String strMultipleBillPrinting)
	{
		this.strMultipleBillPrinting = (String) setDefaultValue(strMultipleBillPrinting, " ");
	}

	public String getStrEnableKOT()
	{
		return strEnableKOT;
	}

	public void setStrEnableKOT(String strEnableKOT)
	{
		this.strEnableKOT = (String) setDefaultValue(strEnableKOT, " ");
	}

	public String getStrEffectOnPSP()
	{
		return strEffectOnPSP;
	}

	public void setStrEffectOnPSP(String strEffectOnPSP)
	{
		this.strEffectOnPSP = (String) setDefaultValue(strEffectOnPSP, " ");
	}

	public String getStrPrintVatNo()
	{
		return strPrintVatNo;
	}

	public void setStrPrintVatNo(String strPrintVatNo)
	{
		this.strPrintVatNo = (String) setDefaultValue(strPrintVatNo, " ");
	}

	public String getStrVatNo()
	{
		return strVatNo;
	}

	public void setStrVatNo(String strVatNo)
	{
		this.strVatNo = (String) setDefaultValue(strVatNo, " ");
	}

	public String getStrShowBill()
	{
		return strShowBill;
	}

	public void setStrShowBill(String strShowBill)
	{
		this.strShowBill = (String) setDefaultValue(strShowBill, " ");
	}

	public String getStrPrintServiceTaxNo()
	{
		return strPrintServiceTaxNo;
	}

	public void setStrPrintServiceTaxNo(String strPrintServiceTaxNo)
	{
		this.strPrintServiceTaxNo = (String) setDefaultValue(strPrintServiceTaxNo, " ");
	}

	public String getStrServiceTaxNo()
	{
		return strServiceTaxNo;
	}

	public void setStrServiceTaxNo(String strServiceTaxNo)
	{
		this.strServiceTaxNo = (String) setDefaultValue(strServiceTaxNo, " ");
	}

	public String getStrManualBillNo()
	{
		return strManualBillNo;
	}

	public void setStrManualBillNo(String strManualBillNo)
	{
		this.strManualBillNo = (String) setDefaultValue(strManualBillNo, " ");
	}

	public String getStrMenuItemDispSeq()
	{
		return strMenuItemDispSeq;
	}

	public void setStrMenuItemDispSeq(String strMenuItemDispSeq)
	{
		this.strMenuItemDispSeq = (String) setDefaultValue(strMenuItemDispSeq, " ");
	}

	public String getStrSenderEmailId()
	{
		return strSenderEmailId;
	}

	public void setStrSenderEmailId(String strSenderEmailId)
	{
		this.strSenderEmailId = (String) setDefaultValue(strSenderEmailId, " ");
	}

	public String getStrEmailPassword()
	{
		return strEmailPassword;
	}

	public void setStrEmailPassword(String strEmailPassword)
	{
		this.strEmailPassword = (String) setDefaultValue(strEmailPassword, " ");
	}

	public String getStrConfirmEmailPassword()
	{
		return strConfirmEmailPassword;
	}

	public void setStrConfirmEmailPassword(String strConfirmEmailPassword)
	{
		this.strConfirmEmailPassword = (String) setDefaultValue(strConfirmEmailPassword, " ");
	}

	public String getStrBody()
	{
		return strBody;
	}

	public void setStrBody(String strBody)
	{
		this.strBody = (String) setDefaultValue(strBody, " ");
	}

	public String getStrEmailServerName()
	{
		return strEmailServerName;
	}

	public void setStrEmailServerName(String strEmailServerName)
	{
		this.strEmailServerName = (String) setDefaultValue(strEmailServerName, " ");
	}

	public String getStrSMSApi()
	{
		return strSMSApi;
	}

	public void setStrSMSApi(String strSMSApi)
	{
		this.strSMSApi = (String) setDefaultValue(strSMSApi, " ");
	}

	public String getStrUserCreated()
	{
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated)
	{
		this.strUserCreated = (String) setDefaultValue(strUserCreated, " ");
	}

	public String getStrUserEdited()
	{
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited)
	{
		this.strUserEdited = (String) setDefaultValue(strUserEdited, " ");
	}

	public String getDteDateCreated()
	{
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated)
	{
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited()
	{
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited)
	{
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrPOSType()
	{
		return strPOSType;
	}

	public void setStrPOSType(String strPOSType)
	{
		this.strPOSType = (String) setDefaultValue(strPOSType, " ");
	}

	public String getStrWebServiceLink()
	{
		return strWebServiceLink;
	}

	public void setStrWebServiceLink(String strWebServiceLink)
	{
		this.strWebServiceLink = (String) setDefaultValue(strWebServiceLink, " ");
	}

	public String getStrDataSendFrequency()
	{
		return strDataSendFrequency;
	}

	public void setStrDataSendFrequency(String strDataSendFrequency)
	{
		this.strDataSendFrequency = (String) setDefaultValue(strDataSendFrequency, " ");
	}

	public String getDteHOServerDate()
	{
		return dteHOServerDate;
	}

	public void setDteHOServerDate(String dteHOServerDate)
	{
		this.dteHOServerDate = dteHOServerDate;
	}

	public String getStrRFID()
	{
		return strRFID;
	}

	public void setStrRFID(String strRFID)
	{
		this.strRFID = (String) setDefaultValue(strRFID, " ");
	}

	public String getStrServerName()
	{
		return strServerName;
	}

	public void setStrServerName(String strServerName)
	{
		this.strServerName = (String) setDefaultValue(strServerName, " ");
	}

	public String getStrDBUserName()
	{
		return strDBUserName;
	}

	public void setStrDBUserName(String strDBUserName)
	{
		this.strDBUserName = (String) setDefaultValue(strDBUserName, " ");
	}

	public String getStrDBPassword()
	{
		return strDBPassword;
	}

	public void setStrDBPassword(String strDBPassword)
	{
		this.strDBPassword = (String) setDefaultValue(strDBPassword, " ");
	}

	public String getStrDatabaseName()
	{
		return strDatabaseName;
	}

	public void setStrDatabaseName(String strDatabaseName)
	{
		this.strDatabaseName = (String) setDefaultValue(strDatabaseName, " ");
	}

	public String getStrEnableKOTForDirectBiller()
	{
		return strEnableKOTForDirectBiller;
	}

	public void setStrEnableKOTForDirectBiller(String strEnableKOTForDirectBiller)
	{
		this.strEnableKOTForDirectBiller = (String) setDefaultValue(strEnableKOTForDirectBiller, " ");
	}

	public long getIntPinCode()
	{
		return intPinCode;
	}

	public void setIntPinCode(long intPinCode)
	{
		this.intPinCode = (Long) setDefaultValue(intPinCode, "0");
	}

	public String getStrChangeTheme()
	{
		return strChangeTheme;
	}

	public void setStrChangeTheme(String strChangeTheme)
	{
		this.strChangeTheme = (String) setDefaultValue(strChangeTheme, " ");
	}

	public double getDblMaxDiscount()
	{
		return dblMaxDiscount;
	}

	public void setDblMaxDiscount(double dblMaxDiscount)
	{
		this.dblMaxDiscount = (Double) setDefaultValue(dblMaxDiscount, "0.0000");
	}

	public String getStrAreaWisePricing()
	{
		return strAreaWisePricing;
	}

	public void setStrAreaWisePricing(String strAreaWisePricing)
	{
		this.strAreaWisePricing = (String) setDefaultValue(strAreaWisePricing, " ");
	}

	public String getStrMenuItemSortingOn()
	{
		return strMenuItemSortingOn;
	}

	public void setStrMenuItemSortingOn(String strMenuItemSortingOn)
	{
		this.strMenuItemSortingOn = (String) setDefaultValue(strMenuItemSortingOn, " ");
	}

	public String getStrDirectAreaCode()
	{
		return strDirectAreaCode;
	}

	public void setStrDirectAreaCode(String strDirectAreaCode)
	{
		this.strDirectAreaCode = (String) setDefaultValue(strDirectAreaCode, " ");
	}

	public long getIntColumnSize()
	{
		return intColumnSize;
	}

	public void setIntColumnSize(long intColumnSize)
	{
		this.intColumnSize = (Long) setDefaultValue(intColumnSize, "0");
	}

	public String getStrPrintType()
	{
		return strPrintType;
	}

	public void setStrPrintType(String strPrintType)
	{
		this.strPrintType = (String) setDefaultValue(strPrintType, " ");
	}

	public String getStrEditHomeDelivery()
	{
		return strEditHomeDelivery;
	}

	public void setStrEditHomeDelivery(String strEditHomeDelivery)
	{
		this.strEditHomeDelivery = (String) setDefaultValue(strEditHomeDelivery, " ");
	}

	public String getStrSlabBasedHDCharges()
	{
		return strSlabBasedHDCharges;
	}

	public void setStrSlabBasedHDCharges(String strSlabBasedHDCharges)
	{
		this.strSlabBasedHDCharges = (String) setDefaultValue(strSlabBasedHDCharges, " ");
	}

	public String getStrSkipWaiterAndPax()
	{
		return strSkipWaiterAndPax;
	}

	public void setStrSkipWaiterAndPax(String strSkipWaiterAndPax)
	{
		this.strSkipWaiterAndPax = (String) setDefaultValue(strSkipWaiterAndPax, " ");
	}

	public String getStrSkipWaiter()
	{
		return strSkipWaiter;
	}

	public void setStrSkipWaiter(String strSkipWaiter)
	{
		this.strSkipWaiter = (String) setDefaultValue(strSkipWaiter, " ");
	}

	public String getStrDirectKOTPrintMakeKOT()
	{
		return strDirectKOTPrintMakeKOT;
	}

	public void setStrDirectKOTPrintMakeKOT(String strDirectKOTPrintMakeKOT)
	{
		this.strDirectKOTPrintMakeKOT = (String) setDefaultValue(strDirectKOTPrintMakeKOT, " ");
	}

	public String getStrSkipPax()
	{
		return strSkipPax;
	}

	public void setStrSkipPax(String strSkipPax)
	{
		this.strSkipPax = (String) setDefaultValue(strSkipPax, " ");
	}

	public String getStrCRMInterface()
	{
		return strCRMInterface;
	}

	public void setStrCRMInterface(String strCRMInterface)
	{
		this.strCRMInterface = (String) setDefaultValue(strCRMInterface, " ");
	}

	public String getStrGetWebserviceURL()
	{
		return strGetWebserviceURL;
	}

	public void setStrGetWebserviceURL(String strGetWebserviceURL)
	{
		this.strGetWebserviceURL = (String) setDefaultValue(strGetWebserviceURL, " ");
	}

	public String getStrPostWebserviceURL()
	{
		return strPostWebserviceURL;
	}

	public void setStrPostWebserviceURL(String strPostWebserviceURL)
	{
		this.strPostWebserviceURL = (String) setDefaultValue(strPostWebserviceURL, " ");
	}

	public String getStrOutletUID()
	{
		return strOutletUID;
	}

	public void setStrOutletUID(String strOutletUID)
	{
		this.strOutletUID = (String) setDefaultValue(strOutletUID, " ");
	}

	public String getStrPOSID()
	{
		return strPOSID;
	}

	public void setStrPOSID(String strPOSID)
	{
		this.strPOSID = (String) setDefaultValue(strPOSID, " ");
	}

	public String getStrStockInOption()
	{
		return strStockInOption;
	}

	public void setStrStockInOption(String strStockInOption)
	{
		this.strStockInOption = (String) setDefaultValue(strStockInOption, " ");
	}

	public String getStrCustSeries() {
		return strCustSeries;
	}

	public void setStrCustSeries(String strCustSeries) {
		this.strCustSeries = strCustSeries;
	}

	public long getIntAdvReceiptPrintCount()
	{
		return intAdvReceiptPrintCount;
	}

	public void setIntAdvReceiptPrintCount(long intAdvReceiptPrintCount)
	{
		this.intAdvReceiptPrintCount = (Long) setDefaultValue(intAdvReceiptPrintCount, "0");
	}

	public String getStrHomeDeliverySMS()
	{
		return strHomeDeliverySMS;
	}

	public void setStrHomeDeliverySMS(String strHomeDeliverySMS)
	{
		this.strHomeDeliverySMS = (String) setDefaultValue(strHomeDeliverySMS, " ");
	}

	public String getStrBillStettlementSMS()
	{
		return strBillStettlementSMS;
	}

	public void setStrBillStettlementSMS(String strBillStettlementSMS)
	{
		this.strBillStettlementSMS = (String) setDefaultValue(strBillStettlementSMS, " ");
	}

	public String getStrBillFormatType()
	{
		return strBillFormatType;
	}

	public void setStrBillFormatType(String strBillFormatType)
	{
		this.strBillFormatType = (String) setDefaultValue(strBillFormatType, " ");
	}

	public String getStrActivePromotions()
	{
		return strActivePromotions;
	}

	public void setStrActivePromotions(String strActivePromotions)
	{
		this.strActivePromotions = (String) setDefaultValue(strActivePromotions, " ");
	}

	public String getStrSendHomeDelSMS()
	{
		return strSendHomeDelSMS;
	}

	public void setStrSendHomeDelSMS(String strSendHomeDelSMS)
	{
		this.strSendHomeDelSMS = (String) setDefaultValue(strSendHomeDelSMS, " ");
	}

	public String getStrSendBillSettlementSMS()
	{
		return strSendBillSettlementSMS;
	}

	public void setStrSendBillSettlementSMS(String strSendBillSettlementSMS)
	{
		this.strSendBillSettlementSMS = (String) setDefaultValue(strSendBillSettlementSMS, " ");
	}

	public String getStrSMSType()
	{
		return strSMSType;
	}

	public void setStrSMSType(String strSMSType)
	{
		this.strSMSType = (String) setDefaultValue(strSMSType, " ");
	}

	public String getStrPrintShortNameOnKOT()
	{
		return strPrintShortNameOnKOT;
	}

	public void setStrPrintShortNameOnKOT(String strPrintShortNameOnKOT)
	{
		this.strPrintShortNameOnKOT = (String) setDefaultValue(strPrintShortNameOnKOT, " ");
	}

	public String getStrShowCustHelp()
	{
		return strShowCustHelp;
	}

	public void setStrShowCustHelp(String strShowCustHelp)
	{
		this.strShowCustHelp = (String) setDefaultValue(strShowCustHelp, " ");
	}

	public String getStrPrintOnVoidBill()
	{
		return strPrintOnVoidBill;
	}

	public void setStrPrintOnVoidBill(String strPrintOnVoidBill)
	{
		this.strPrintOnVoidBill = (String) setDefaultValue(strPrintOnVoidBill, " ");
	}

	public String getStrPostSalesDataToMMS()
	{
		return strPostSalesDataToMMS;
	}

	public void setStrPostSalesDataToMMS(String strPostSalesDataToMMS)
	{
		this.strPostSalesDataToMMS = (String) setDefaultValue(strPostSalesDataToMMS, " ");
	}

	public String getStrCustAreaMasterCompulsory()
	{
		return strCustAreaMasterCompulsory;
	}

	public void setStrCustAreaMasterCompulsory(String strCustAreaMasterCompulsory)
	{
		this.strCustAreaMasterCompulsory = (String) setDefaultValue(strCustAreaMasterCompulsory, " ");
	}

	public String getStrPriceFrom()
	{
		return strPriceFrom;
	}

	public void setStrPriceFrom(String strPriceFrom)
	{
		this.strPriceFrom = (String) setDefaultValue(strPriceFrom, " ");
	}

	public String getStrShowPrinterErrorMessage()
	{
		return strShowPrinterErrorMessage;
	}

	public void setStrShowPrinterErrorMessage(String strShowPrinterErrorMessage)
	{
		this.strShowPrinterErrorMessage = (String) setDefaultValue(strShowPrinterErrorMessage, " ");
	}

	public String getStrTouchScreenMode()
	{
		return strTouchScreenMode;
	}

	public void setStrTouchScreenMode(String strTouchScreenMode)
	{
		this.strTouchScreenMode = (String) setDefaultValue(strTouchScreenMode, " ");
	}

	public String getStrCardInterfaceType()
	{
		return strCardInterfaceType;
	}

	public void setStrCardInterfaceType(String strCardInterfaceType)
	{
		this.strCardInterfaceType = (String) setDefaultValue(strCardInterfaceType, " ");
	}

	public String getStrCMSIntegrationYN()
	{
		return strCMSIntegrationYN;
	}

	public void setStrCMSIntegrationYN(String strCMSIntegrationYN)
	{
		this.strCMSIntegrationYN = (String) setDefaultValue(strCMSIntegrationYN, " ");
	}

	public String getStrCMSWebServiceURL()
	{
		return strCMSWebServiceURL;
	}

	public void setStrCMSWebServiceURL(String strCMSWebServiceURL)
	{
		this.strCMSWebServiceURL = (String) setDefaultValue(strCMSWebServiceURL, " ");
	}

	public String getStrChangeQtyForExternalCode()
	{
		return strChangeQtyForExternalCode;
	}

	public void setStrChangeQtyForExternalCode(String strChangeQtyForExternalCode)
	{
		this.strChangeQtyForExternalCode = (String) setDefaultValue(strChangeQtyForExternalCode, " ");
	}

	public String getStrPointsOnBillPrint()
	{
		return strPointsOnBillPrint;
	}

	public void setStrPointsOnBillPrint(String strPointsOnBillPrint)
	{
		this.strPointsOnBillPrint = (String) setDefaultValue(strPointsOnBillPrint, " ");
	}

	public String getStrCMSPOSCode()
	{
		return strCMSPOSCode;
	}

	public void setStrCMSPOSCode(String strCMSPOSCode)
	{
		this.strCMSPOSCode = (String) setDefaultValue(strCMSPOSCode, " ");
	}

	public String getStrManualAdvOrderNoCompulsory()
	{
		return strManualAdvOrderNoCompulsory;
	}

	public void setStrManualAdvOrderNoCompulsory(String strManualAdvOrderNoCompulsory)
	{
		this.strManualAdvOrderNoCompulsory = (String) setDefaultValue(strManualAdvOrderNoCompulsory, "");
	}

	public String getStrPrintManualAdvOrderNoOnBill()
	{
		return strPrintManualAdvOrderNoOnBill;
	}

	public void setStrPrintManualAdvOrderNoOnBill(String strPrintManualAdvOrderNoOnBill)
	{
		this.strPrintManualAdvOrderNoOnBill = (String) setDefaultValue(strPrintManualAdvOrderNoOnBill, "");
	}

	public String getStrPrintModifierQtyOnKOT()
	{
		return strPrintModifierQtyOnKOT;
	}

	public void setStrPrintModifierQtyOnKOT(String strPrintModifierQtyOnKOT)
	{
		this.strPrintModifierQtyOnKOT = (String) setDefaultValue(strPrintModifierQtyOnKOT, "");
	}

	public long getStrNoOfLinesInKOTPrint()
	{
		return strNoOfLinesInKOTPrint;
	}

	public void setStrNoOfLinesInKOTPrint(long strNoOfLinesInKOTPrint)
	{
		this.strNoOfLinesInKOTPrint = (long) setDefaultValue(strNoOfLinesInKOTPrint, "");
	}

	public String getStrMultipleKOTPrintYN()
	{
		return strMultipleKOTPrintYN;
	}

	public void setStrMultipleKOTPrintYN(String strMultipleKOTPrintYN)
	{
		this.strMultipleKOTPrintYN = (String) setDefaultValue(strMultipleKOTPrintYN, "");
	}

	public String getStrItemQtyNumpad()
	{
		return strItemQtyNumpad;
	}

	public void setStrItemQtyNumpad(String strItemQtyNumpad)
	{
		this.strItemQtyNumpad = (String) setDefaultValue(strItemQtyNumpad, "");
	}

	public String getStrTreatMemberAsTable()
	{
		return strTreatMemberAsTable;
	}

	public void setStrTreatMemberAsTable(String strTreatMemberAsTable)
	{
		this.strTreatMemberAsTable = (String) setDefaultValue(strTreatMemberAsTable, "");
	}

	public String getStrKOTToLocalPrinter()
	{
		return strKOTToLocalPrinter;
	}

	public void setStrKOTToLocalPrinter(String strKOTToLocalPrinter)
	{
		this.strKOTToLocalPrinter = (String) setDefaultValue(strKOTToLocalPrinter, "");
	}

	public String getStrSettleBtnForDirectBillerBill()
	{
		return strSettleBtnForDirectBillerBill;
	}

	public void setStrSettleBtnForDirectBillerBill(String strSettleBtnForDirectBillerBill)
	{
		this.strSettleBtnForDirectBillerBill = (String) setDefaultValue(strSettleBtnForDirectBillerBill, "");
	}

	public String getStrDelBoySelCompulsoryOnDirectBiller()
	{
		return strDelBoySelCompulsoryOnDirectBiller;
	}

	public void setStrDelBoySelCompulsoryOnDirectBiller(String strDelBoySelCompulsoryOnDirectBiller)
	{
		this.strDelBoySelCompulsoryOnDirectBiller = (String) setDefaultValue(strDelBoySelCompulsoryOnDirectBiller, "");
	}

	public String getStrCMSMemberForKOTJPOS()
	{
		return strCMSMemberForKOTJPOS;
	}

	public void setStrCMSMemberForKOTJPOS(String strCMSMemberForKOTJPOS)
	{
		this.strCMSMemberForKOTJPOS = (String) setDefaultValue(strCMSMemberForKOTJPOS, "");
	}

	public String getStrCMSMemberForKOTMPOS()
	{
		return strCMSMemberForKOTMPOS;
	}

	public void setStrCMSMemberForKOTMPOS(String strCMSMemberForKOTMPOS)
	{
		this.strCMSMemberForKOTMPOS = (String) setDefaultValue(strCMSMemberForKOTMPOS, "");
	}

	public String getStrDontShowAdvOrderInOtherPOS()
	{
		return strDontShowAdvOrderInOtherPOS;
	}

	public void setStrDontShowAdvOrderInOtherPOS(String strDontShowAdvOrderInOtherPOS)
	{
		this.strDontShowAdvOrderInOtherPOS = (String) setDefaultValue(strDontShowAdvOrderInOtherPOS, "");
	}

	public String getStrPrintZeroAmtModifierInBill()
	{
		return strPrintZeroAmtModifierInBill;
	}

	public void setStrPrintZeroAmtModifierInBill(String strPrintZeroAmtModifierInBill)
	{
		this.strPrintZeroAmtModifierInBill = (String) setDefaultValue(strPrintZeroAmtModifierInBill, "");
	}

	public String getStrPrintKOTYN()
	{
		return strPrintKOTYN;
	}

	public void setStrPrintKOTYN(String strPrintKOTYN)
	{
		this.strPrintKOTYN = (String) setDefaultValue(strPrintKOTYN, "");
	}

	public String getStrCreditCardSlipNoCompulsoryYN()
	{
		return strCreditCardSlipNoCompulsoryYN;
	}

	public void setStrCreditCardSlipNoCompulsoryYN(String strCreditCardSlipNoCompulsoryYN)
	{
		this.strCreditCardSlipNoCompulsoryYN = (String) setDefaultValue(strCreditCardSlipNoCompulsoryYN, "");
	}

	public String getStrCreditCardExpiryDateCompulsoryYN()
	{
		return strCreditCardExpiryDateCompulsoryYN;
	}

	public void setStrCreditCardExpiryDateCompulsoryYN(String strCreditCardExpiryDateCompulsoryYN)
	{
		this.strCreditCardExpiryDateCompulsoryYN = (String) setDefaultValue(strCreditCardExpiryDateCompulsoryYN, "");
	}

	public String getStrSelectWaiterFromCardSwipe()
	{
		return strSelectWaiterFromCardSwipe;
	}

	public void setStrSelectWaiterFromCardSwipe(String strSelectWaiterFromCardSwipe)
	{
		this.strSelectWaiterFromCardSwipe = (String) setDefaultValue(strSelectWaiterFromCardSwipe, "");
	}

	public String getStrMultiWaiterSelectionOnMakeKOT()
	{
		return strMultiWaiterSelectionOnMakeKOT;
	}

	public void setStrMultiWaiterSelectionOnMakeKOT(String strMultiWaiterSelectionOnMakeKOT)
	{
		this.strMultiWaiterSelectionOnMakeKOT = (String) setDefaultValue(strMultiWaiterSelectionOnMakeKOT, "");
	}

	public String getStrMoveTableToOtherPOS()
	{
		return strMoveTableToOtherPOS;
	}

	public void setStrMoveTableToOtherPOS(String strMoveTableToOtherPOS)
	{
		this.strMoveTableToOtherPOS = (String) setDefaultValue(strMoveTableToOtherPOS, "");
	}

	public String getStrMoveKOTToOtherPOS()
	{
		return strMoveKOTToOtherPOS;
	}

	public void setStrMoveKOTToOtherPOS(String strMoveKOTToOtherPOS)
	{
		this.strMoveKOTToOtherPOS = (String) setDefaultValue(strMoveKOTToOtherPOS, "");
	}

	public String getStrCalculateTaxOnMakeKOT()
	{
		return strCalculateTaxOnMakeKOT;
	}

	public void setStrCalculateTaxOnMakeKOT(String strCalculateTaxOnMakeKOT)
	{
		this.strCalculateTaxOnMakeKOT = (String) setDefaultValue(strCalculateTaxOnMakeKOT, "");
	}

	public String getStrReceiverEmailId()
	{
		return strReceiverEmailId;
	}

	public void setStrReceiverEmailId(String strReceiverEmailId)
	{
		this.strReceiverEmailId = (String) setDefaultValue(strReceiverEmailId, "");
	}

	public String getStrCalculateDiscItemWise()
	{
		return strCalculateDiscItemWise;
	}

	public void setStrCalculateDiscItemWise(String strCalculateDiscItemWise)
	{
		this.strCalculateDiscItemWise = (String) setDefaultValue(strCalculateDiscItemWise, "");
	}

	public String getStrTakewayCustomerSelection()
	{
		return strTakewayCustomerSelection;
	}

	public void setStrTakewayCustomerSelection(String strTakewayCustomerSelection)
	{
		this.strTakewayCustomerSelection = (String) setDefaultValue(strTakewayCustomerSelection, "");
	}

	public String getStrItemType()
	{
		return strItemType;
	}

	public void setStrItemType(String strItemType)
	{
		this.strItemType = (String) setDefaultValue(strItemType, "");
	}

	public String getStrAllowNewAreaMasterFromCustMaster()
	{
		return strAllowNewAreaMasterFromCustMaster;
	}

	public void setStrAllowNewAreaMasterFromCustMaster(String strAllowNewAreaMasterFromCustMaster)
	{
		this.strAllowNewAreaMasterFromCustMaster = (String) setDefaultValue(strAllowNewAreaMasterFromCustMaster, "");
	}

	public String getStrCustAddressSelectionForBill()
	{
		return strCustAddressSelectionForBill;
	}

	public void setStrCustAddressSelectionForBill(String strCustAddressSelectionForBill)
	{
		this.strCustAddressSelectionForBill = (String) setDefaultValue(strCustAddressSelectionForBill, "");
	}

	public String getStrGenrateMI()
	{
		return strGenrateMI;
	}

	public void setStrGenrateMI(String strGenrateMI)
	{
		this.strGenrateMI = (String) setDefaultValue(strGenrateMI, "");
	}

	public String getStrFTPAddress()
	{
		return strFTPAddress;
	}

	public void setStrFTPAddress(String strFTPAddress)
	{
		this.strFTPAddress = (String) setDefaultValue(strFTPAddress, "");
	}

	public String getStrFTPServerUserName()
	{
		return strFTPServerUserName;
	}

	public void setStrFTPServerUserName(String strFTPServerUserName)
	{
		this.strFTPServerUserName = (String) setDefaultValue(strFTPServerUserName, "");
	}

	public String getStrFTPServerPass()
	{
		return strFTPServerPass;
	}

	public void setStrFTPServerPass(String strFTPServerPass)
	{
		this.strFTPServerPass = (String) setDefaultValue(strFTPServerPass, "");
	}

	public String getStrAllowToCalculateItemWeight()
	{
		return strAllowToCalculateItemWeight;
	}

	public void setStrAllowToCalculateItemWeight(String strAllowToCalculateItemWeight)
	{
		this.strAllowToCalculateItemWeight = (String) setDefaultValue(strAllowToCalculateItemWeight, "");
	}

	public String getStrShowBillsDtlType()
	{
		return strShowBillsDtlType;
	}

	public void setStrShowBillsDtlType(String strShowBillsDtlType)
	{
		this.strShowBillsDtlType = (String) setDefaultValue(strShowBillsDtlType, "");
	}

	public String getStrPrintTaxInvoiceOnBill()
	{
		return strPrintTaxInvoiceOnBill;
	}

	public void setStrPrintTaxInvoiceOnBill(String strPrintTaxInvoiceOnBill)
	{
		this.strPrintTaxInvoiceOnBill = (String) setDefaultValue(strPrintTaxInvoiceOnBill, "");
	}

	public String getStrPrintInclusiveOfAllTaxesOnBill()
	{
		return strPrintInclusiveOfAllTaxesOnBill;
	}

	public void setStrPrintInclusiveOfAllTaxesOnBill(String strPrintInclusiveOfAllTaxesOnBill)
	{
		this.strPrintInclusiveOfAllTaxesOnBill = (String) setDefaultValue(strPrintInclusiveOfAllTaxesOnBill, "");
	}

	public String getStrApplyDiscountOn()
	{
		return strApplyDiscountOn;
	}

	public void setStrApplyDiscountOn(String strApplyDiscountOn)
	{
		this.strApplyDiscountOn = (String) setDefaultValue(strApplyDiscountOn, "");
	}

	public String getStrMemberCodeForKotInMposByCardSwipe()
	{
		return strMemberCodeForKotInMposByCardSwipe;
	}

	public void setStrMemberCodeForKotInMposByCardSwipe(String strMemberCodeForKotInMposByCardSwipe)
	{
		this.strMemberCodeForKotInMposByCardSwipe = (String) setDefaultValue(strMemberCodeForKotInMposByCardSwipe, "");
	}

	public String getStrPrintBillYN()
	{
		return strPrintBillYN;
	}

	public void setStrPrintBillYN(String strPrintBillYN)
	{
		this.strPrintBillYN = (String) setDefaultValue(strPrintBillYN, "");
	}

	public String getStrVatAndServiceTaxFromPos()
	{
		return strVatAndServiceTaxFromPos;
	}

	public void setStrVatAndServiceTaxFromPos(String strVatAndServiceTaxFromPos)
	{
		this.strVatAndServiceTaxFromPos = (String) setDefaultValue(strVatAndServiceTaxFromPos, "");
	}

	public String getStrMemberCodeForMakeBillInMPOS()
	{
		return strMemberCodeForMakeBillInMPOS;
	}

	public void setStrMemberCodeForMakeBillInMPOS(String strMemberCodeForMakeBillInMPOS)
	{
		this.strMemberCodeForMakeBillInMPOS = (String) setDefaultValue(strMemberCodeForMakeBillInMPOS, "");
	}

	public String getStrItemWiseKOTYN()
	{
		return strItemWiseKOTYN;
	}

	public void setStrItemWiseKOTYN(String strItemWiseKOTYN)
	{
		this.strItemWiseKOTYN = (String) setDefaultValue(strItemWiseKOTYN, "");
	}

	public String getStrLastPOSForDayEnd()
	{
		return strLastPOSForDayEnd;
	}

	public void setStrLastPOSForDayEnd(String strLastPOSForDayEnd)
	{
		this.strLastPOSForDayEnd = (String) setDefaultValue(strLastPOSForDayEnd, "");
	}

	public String getStrCMSPostingType()
	{
		return strCMSPostingType;
	}

	public void setStrCMSPostingType(String strCMSPostingType)
	{
		this.strCMSPostingType = (String) setDefaultValue(strCMSPostingType, "");
	}

	public String getStrPopUpToApplyPromotionsOnBill()
	{
		return strPopUpToApplyPromotionsOnBill;
	}

	public void setStrPopUpToApplyPromotionsOnBill(String strPopUpToApplyPromotionsOnBill)
	{
		this.strPopUpToApplyPromotionsOnBill = (String) setDefaultValue(strPopUpToApplyPromotionsOnBill, "");
	}

	public String getStrSelectCustomerCodeFromCardSwipe()
	{
		return strSelectCustomerCodeFromCardSwipe;
	}

	public void setStrSelectCustomerCodeFromCardSwipe(String strSelectCustomerCodeFromCardSwipe)
	{
		this.strSelectCustomerCodeFromCardSwipe = (String) setDefaultValue(strSelectCustomerCodeFromCardSwipe, "N");
	}

	public String getStrCheckDebitCardBalOnTransactions()
	{
		return strCheckDebitCardBalOnTransactions;
	}

	public void setStrCheckDebitCardBalOnTransactions(String strCheckDebitCardBalOnTransactions)
	{
		this.strCheckDebitCardBalOnTransactions = (String) setDefaultValue(strCheckDebitCardBalOnTransactions, "N");
	}

	public String getStrSettlementsFromPOSMaster()
	{
		return strSettlementsFromPOSMaster;
	}

	public void setStrSettlementsFromPOSMaster(String strSettlementsFromPOSMaster)
	{
		this.strSettlementsFromPOSMaster = (String) setDefaultValue(strSettlementsFromPOSMaster, "N");
	}

	public String getStrShiftWiseDayEndYN()
	{
		return strShiftWiseDayEndYN;
	}

	public void setStrShiftWiseDayEndYN(String strShiftWiseDayEndYN)
	{
		this.strShiftWiseDayEndYN = (String) setDefaultValue(strShiftWiseDayEndYN, "");
	}

	public String getStrProductionLinkup()
	{
		return strProductionLinkup;
	}

	public void setStrProductionLinkup(String strProductionLinkup)
	{
		this.strProductionLinkup = (String) setDefaultValue(strProductionLinkup, "");
	}

	public String getStrLockDataOnShift()
	{
		return strLockDataOnShift;
	}

	public void setStrLockDataOnShift(String strLockDataOnShift)
	{
		this.strLockDataOnShift = (String) setDefaultValue(strLockDataOnShift, "");
	}

	public String getStrWSClientCode()
	{
		return strWSClientCode;
	}

	public void setStrWSClientCode(String strWSClientCode)
	{
		this.strWSClientCode = (String) setDefaultValue(strWSClientCode, "");
	}

	public String getStrPOSCode()
	{
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode)
	{
		this.strPOSCode = (String) setDefaultValue(strPOSCode, "");
	}

	public String getStrEnableBillSeries()
	{
		return strEnableBillSeries;
	}

	public void setStrEnableBillSeries(String strEnableBillSeries)
	{
		this.strEnableBillSeries = (String) setDefaultValue(strEnableBillSeries, "");
	}

	public String getStrEnablePMSIntegrationYN()
	{
		return strEnablePMSIntegrationYN;
	}

	public void setStrEnablePMSIntegrationYN(String strEnablePMSIntegrationYN)
	{
		this.strEnablePMSIntegrationYN = (String) setDefaultValue(strEnablePMSIntegrationYN, "");
	}

	public String getStrPrintTimeOnBill()
	{
		return strPrintTimeOnBill;
	}

	public void setStrPrintTimeOnBill(String strPrintTimeOnBill)
	{
		this.strPrintTimeOnBill = (String) setDefaultValue(strPrintTimeOnBill, "");
	}

	public String getStrPrintTDHItemsInBill()
	{
		return strPrintTDHItemsInBill;
	}

	public void setStrPrintTDHItemsInBill(String strPrintTDHItemsInBill)
	{
		this.strPrintTDHItemsInBill = (String) setDefaultValue(strPrintTDHItemsInBill, "");
	}

	public String getStrPrintRemarkAndReasonForReprint()
	{
		return strPrintRemarkAndReasonForReprint;
	}

	public void setStrPrintRemarkAndReasonForReprint(String strPrintRemarkAndReasonForReprint)
	{
		this.strPrintRemarkAndReasonForReprint = (String) setDefaultValue(strPrintRemarkAndReasonForReprint, "");
	}

	public long getIntDaysBeforeOrderToCancel()
	{
		return intDaysBeforeOrderToCancel;
	}

	public void setIntDaysBeforeOrderToCancel(long intDaysBeforeOrderToCancel)
	{
		this.intDaysBeforeOrderToCancel = (Long) setDefaultValue(intDaysBeforeOrderToCancel, "0");
	}

	public long getIntNoOfDelDaysForAdvOrder()
	{
		return intNoOfDelDaysForAdvOrder;
	}

	public void setIntNoOfDelDaysForAdvOrder(long intNoOfDelDaysForAdvOrder)
	{
		this.intNoOfDelDaysForAdvOrder = (Long) setDefaultValue(intNoOfDelDaysForAdvOrder, "0");
	}

	public long getIntNoOfDelDaysForUrgentOrder()
	{
		return intNoOfDelDaysForUrgentOrder;
	}

	public void setIntNoOfDelDaysForUrgentOrder(long intNoOfDelDaysForUrgentOrder)
	{
		this.intNoOfDelDaysForUrgentOrder = (Long) setDefaultValue(intNoOfDelDaysForUrgentOrder, "0");
	}

	public String getStrSetUpToTimeForAdvOrder()
	{
		return strSetUpToTimeForAdvOrder;
	}

	public void setStrSetUpToTimeForAdvOrder(String strSetUpToTimeForAdvOrder)
	{
		this.strSetUpToTimeForAdvOrder = (String) setDefaultValue(strSetUpToTimeForAdvOrder, "");
	}

	public String getStrSetUpToTimeForUrgentOrder()
	{
		return strSetUpToTimeForUrgentOrder;
	}

	public void setStrSetUpToTimeForUrgentOrder(String strSetUpToTimeForUrgentOrder)
	{
		this.strSetUpToTimeForUrgentOrder = (String) setDefaultValue(strSetUpToTimeForUrgentOrder, "");
	}

	public String getStrUpToTimeForAdvOrder()
	{
		return strUpToTimeForAdvOrder;
	}

	public void setStrUpToTimeForAdvOrder(String strUpToTimeForAdvOrder)
	{
		this.strUpToTimeForAdvOrder = (String) setDefaultValue(strUpToTimeForAdvOrder, "");
	}

	public String getStrUpToTimeForUrgentOrder()
	{
		return strUpToTimeForUrgentOrder;
	}

	public void setStrUpToTimeForUrgentOrder(String strUpToTimeForUrgentOrder)
	{
		this.strUpToTimeForUrgentOrder = (String) setDefaultValue(strUpToTimeForUrgentOrder, "");
	}

	public String getStrEnableBothPrintAndSettleBtnForDB()
	{
		return strEnableBothPrintAndSettleBtnForDB;
	}

	public void setStrEnableBothPrintAndSettleBtnForDB(String strEnableBothPrintAndSettleBtnForDB)
	{
		this.strEnableBothPrintAndSettleBtnForDB = (String) setDefaultValue(strEnableBothPrintAndSettleBtnForDB, "");
	}

	public String getStrInrestoPOSIntegrationYN()
	{
		return strInrestoPOSIntegrationYN;
	}

	public void setStrInrestoPOSIntegrationYN(String strInrestoPOSIntegrationYN)
	{
		this.strInrestoPOSIntegrationYN = (String) setDefaultValue(strInrestoPOSIntegrationYN, "");
	}

	public String getStrInrestoPOSWebServiceURL()
	{
		return strInrestoPOSWebServiceURL;
	}

	public void setStrInrestoPOSWebServiceURL(String strInrestoPOSWebServiceURL)
	{
		this.strInrestoPOSWebServiceURL = (String) setDefaultValue(strInrestoPOSWebServiceURL, "");
	}

	public String getStrInrestoPOSId()
	{
		return strInrestoPOSId;
	}

	public void setStrInrestoPOSId(String strInrestoPOSId)
	{
		this.strInrestoPOSId = (String) setDefaultValue(strInrestoPOSId, "");
	}

	public String getStrInrestoPOSKey()
	{
		return strInrestoPOSKey;
	}

	public void setStrInrestoPOSKey(String strInrestoPOSKey)
	{
		this.strInrestoPOSKey = (String) setDefaultValue(strInrestoPOSKey, "");
	}

	public String getStrCarryForwardFloatAmtToNextDay()
	{
		return strCarryForwardFloatAmtToNextDay;
	}

	public void setStrCarryForwardFloatAmtToNextDay(String strCarryForwardFloatAmtToNextDay)
	{
		this.strCarryForwardFloatAmtToNextDay = (String) setDefaultValue(strCarryForwardFloatAmtToNextDay, "");
	}

	public String getStrOpenCashDrawerAfterBillPrintYN()
	{
		return strOpenCashDrawerAfterBillPrintYN;
	}

	public void setStrOpenCashDrawerAfterBillPrintYN(String strOpenCashDrawerAfterBillPrintYN)
	{
		this.strOpenCashDrawerAfterBillPrintYN = (String) setDefaultValue(strOpenCashDrawerAfterBillPrintYN, "");
	}

	public String getStrPropertyWiseSalesOrderYN()
	{
		return strPropertyWiseSalesOrderYN;
	}

	public void setStrPropertyWiseSalesOrderYN(String strPropertyWiseSalesOrderYN)
	{
		this.strPropertyWiseSalesOrderYN = (String) setDefaultValue(strPropertyWiseSalesOrderYN, "");
	}

	public String getStrDataPostFlag()
	{
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag)
	{
		this.strDataPostFlag = (String) setDefaultValue(strDataPostFlag, "");
	}

	public String getStrShowItemDetailsGrid()
	{
		return strShowItemDetailsGrid;
	}

	public void setStrShowItemDetailsGrid(String strShowItemDetailsGrid)
	{
		this.strShowItemDetailsGrid = (String) setDefaultValue(strShowItemDetailsGrid, "");
	}

	public String getStrShowPopUpForNextItemQuantity()
	{
		return strShowPopUpForNextItemQuantity;
	}

	public void setStrShowPopUpForNextItemQuantity(String strShowPopUpForNextItemQuantity)
	{
		this.strShowPopUpForNextItemQuantity = (String) setDefaultValue(strShowPopUpForNextItemQuantity, "");
	}

	public String getStrJioMoneyIntegration()
	{
		return strJioMoneyIntegration;
	}

	public void setStrJioMoneyIntegration(String strJioMoneyIntegration)
	{
		this.strJioMoneyIntegration = (String) setDefaultValue(strJioMoneyIntegration, "");
	}

	public String getStrJioWebServiceUrl()
	{
		return strJioWebServiceUrl;
	}

	public void setStrJioWebServiceUrl(String strJioWebServiceUrl)
	{
		this.strJioWebServiceUrl = (String) setDefaultValue(strJioWebServiceUrl, "");
	}

	public String getStrShowItemStkColumnInDB() {
		return StrShowItemStkColumnInDB;
	}

	public void setStrShowItemStkColumnInDB(String strShowItemStkColumnInDB) {
		StrShowItemStkColumnInDB = strShowItemStkColumnInDB;
	}

	public String getStrJioMID() {
		return strJioMID;
	}

	public void setStrJioMID(String strJioMID) {
		this.strJioMID = strJioMID;
	}

	public String getStrJioTID() {
		return strJioTID;
	}

	public void setStrJioTID(String strJioTID) {
		this.strJioTID = strJioTID;
	}

	public String getStrJioActivationCode() {
		return strJioActivationCode;
	}

	public void setStrJioActivationCode(String strJioActivationCode) {
		this.strJioActivationCode = strJioActivationCode;
	}

	public String getStrJioDeviceID() {
		return strJioDeviceID;
	}

	public void setStrJioDeviceID(String strJioDeviceID) {
		this.strJioDeviceID = strJioDeviceID;
	}

	public String getStrNewBillSeriesForNewDay() {
		return strNewBillSeriesForNewDay;
	}

	public void setStrNewBillSeriesForNewDay(String strNewBillSeriesForNewDay) {
		this.strNewBillSeriesForNewDay = strNewBillSeriesForNewDay;
	}

	public String getStrShowReportsPOSWise() {
		return strShowReportsPOSWise;
	}

	public void setStrShowReportsPOSWise(String strShowReportsPOSWise) {
		this.strShowReportsPOSWise = strShowReportsPOSWise;
	}


	public Blob getBlobReportImage() {
		return blobReportImage;
	}

	public void setBlobReportImage(Blob blobReportImage) {
		this.blobReportImage = blobReportImage;
	}

	public String getStrEnableDineIn() {
		return strEnableDineIn;
	}

	public void setStrEnableDineIn(String strEnableDineIn) {
		this.strEnableDineIn = strEnableDineIn;
	}

	public String getStrAutoAreaSelectionInMakeKOT() {
		return strAutoAreaSelectionInMakeKOT;
	}

	public void setStrAutoAreaSelectionInMakeKOT(
			String strAutoAreaSelectionInMakeKOT) {
		this.strAutoAreaSelectionInMakeKOT = strAutoAreaSelectionInMakeKOT;
	}

	public long getIntShowPopItemsOfDays(){
		return intShowPopItemsOfDays;
	}
	public void setIntShowPopItemsOfDays(long intShowPopItemsOfDays){
		this. intShowPopItemsOfDays = (Long) setDefaultValue( intShowPopItemsOfDays, "0");
	}

	public String getStrPostSalesCostOrLoc(){
		return strPostSalesCostOrLoc;
	}
	public void setStrPostSalesCostOrLoc(String strPostSalesCostOrLoc){
		this. strPostSalesCostOrLoc = (String) setDefaultValue( strPostSalesCostOrLoc, "NA");
	}

	public int getDblNoOfDecimalPlace()
	{
		return dblNoOfDecimalPlace;
	}

	public void setDblNoOfDecimalPlace(int dblNoOfDecimalPlace)
	{
		this.dblNoOfDecimalPlace = dblNoOfDecimalPlace;
	}

	public String getStrHomeDeliveryAreaForDirectBiller()
	{
		return strHomeDeliveryAreaForDirectBiller;
	}

	public void setStrHomeDeliveryAreaForDirectBiller(String strHomeDeliveryAreaForDirectBiller)
	{
		this.strHomeDeliveryAreaForDirectBiller = strHomeDeliveryAreaForDirectBiller;
	}

	public String getStrTakeAwayAreaForDirectBiller()
	{
		return strTakeAwayAreaForDirectBiller;
	}

	public void setStrTakeAwayAreaForDirectBiller(String strTakeAwayAreaForDirectBiller)
	{
		this.strTakeAwayAreaForDirectBiller = strTakeAwayAreaForDirectBiller;
	}

	public String getStrAreaWiseCostCenterKOTPrintingYN()
	{
		return strAreaWiseCostCenterKOTPrintingYN;
	}

	public void setStrAreaWiseCostCenterKOTPrintingYN(String strAreaWiseCostCenterKOTPrintingYN)
	{
		this.strAreaWiseCostCenterKOTPrintingYN = strAreaWiseCostCenterKOTPrintingYN;
	}

	public String getStrRoundOffBillFinalAmt()
	{
		return strRoundOffBillFinalAmt;
	}

	public void setStrRoundOffBillFinalAmt(String strRoundOffBillFinalAmt)
	{
		this.strRoundOffBillFinalAmt = strRoundOffBillFinalAmt;
	}

	public String getStrPrintMoveTableMoveKOTYN()
	{
		return strPrintMoveTableMoveKOTYN;
	}

	public void setStrPrintMoveTableMoveKOTYN(String strPrintMoveTableMoveKOTYN)
	{
		this.strPrintMoveTableMoveKOTYN = strPrintMoveTableMoveKOTYN;
	}

	public String getStrPrintItemsOnMoveKOTMoveTable()
	{
		return strPrintItemsOnMoveKOTMoveTable;
	}

	public void setStrPrintItemsOnMoveKOTMoveTable(String strPrintItemsOnMoveKOTMoveTable)
	{
		this.strPrintItemsOnMoveKOTMoveTable = strPrintItemsOnMoveKOTMoveTable;
	}

	public String getStrSendDBBackupOnClientMail()
	{
		return strSendDBBackupOnClientMail;
	}

	public void setStrSendDBBackupOnClientMail(String strSendDBBackupOnClientMail)
	{
		this.strSendDBBackupOnClientMail = strSendDBBackupOnClientMail;
	}

	public String getStrPrintQtyTotal()
	{
		return strPrintQtyTotal;
	}

	public void setStrPrintQtyTotal(String strPrintQtyTotal)
	{
		this.strPrintQtyTotal = strPrintQtyTotal;
	}

	public String getStrPrintOrderNoOnBillYN()
	{
		return strPrintOrderNoOnBillYN;
	}

	public void setStrPrintOrderNoOnBillYN(String strPrintOrderNoOnBillYN)
	{
		this.strPrintOrderNoOnBillYN = strPrintOrderNoOnBillYN;
	}

	public String getStrAutoAddKOTToBill()
	{
		return strAutoAddKOTToBill;
	}

	public void setStrAutoAddKOTToBill(String strAutoAddKOTToBill)
	{
		this.strAutoAddKOTToBill = strAutoAddKOTToBill;
	}

	public String getStrPrintDeviceAndUserDtlOnKOTYN()
	{
		return strPrintDeviceAndUserDtlOnKOTYN;
	}

	public void setStrPrintDeviceAndUserDtlOnKOTYN(String strPrintDeviceAndUserDtlOnKOTYN)
	{
		this.strPrintDeviceAndUserDtlOnKOTYN = strPrintDeviceAndUserDtlOnKOTYN;
	}

	public String getStrFireCommunication()
	{
		return strFireCommunication;
	}

	public void setStrFireCommunication(String strFireCommunication)
	{
		this.strFireCommunication = strFireCommunication;
	}

	public String getStrRemoveSCTaxCode()
	{
		return strRemoveSCTaxCode;
	}

	public void setStrRemoveSCTaxCode(String strRemoveSCTaxCode)
	{
		this.strRemoveSCTaxCode = strRemoveSCTaxCode;
	}

	public String getStrLockTableForWaiter()
	{
		return strLockTableForWaiter;
	}

	public void setStrLockTableForWaiter(String strLockTableForWaiter)
	{
		this.strLockTableForWaiter = strLockTableForWaiter;
	}

	public double getDblUSDConverionRate()
	{
		return dblUSDConverionRate;
	}

	public void setDblUSDConverionRate(double dblUSDConverionRate)
	{
		this.dblUSDConverionRate = dblUSDConverionRate;
	}

	public String getStrShowReportsInCurrency()
	{
		return strShowReportsInCurrency;
	}

	public void setStrShowReportsInCurrency(String strShowReportsInCurrency)
	{
		this.strShowReportsInCurrency = strShowReportsInCurrency;
	}

	public String getStrPOSToMMSPostingCurrency()
	{
		return strPOSToMMSPostingCurrency;
	}

	public void setStrPOSToMMSPostingCurrency(String strPOSToMMSPostingCurrency)
	{
		this.strPOSToMMSPostingCurrency = strPOSToMMSPostingCurrency;
	}

	public String getStrPOSToWebBooksPostingCurrency()
	{
		return strPOSToWebBooksPostingCurrency;
	}

	public void setStrPOSToWebBooksPostingCurrency(String strPOSToWebBooksPostingCurrency)
	{
		this.strPOSToWebBooksPostingCurrency = strPOSToWebBooksPostingCurrency;
	}

	public String getStrBenowIntegrationYN()
	{
		return strBenowIntegrationYN;
	}

	public void setStrBenowIntegrationYN(String strBenowIntegrationYN)
	{
		this.strBenowIntegrationYN = strBenowIntegrationYN;
	}

	public String getStrXEmail()
	{
		return strXEmail;
	}

	public void setStrXEmail(String strXEmail)
	{
		this.strXEmail = strXEmail;
	}

	public String getStrMerchantCode()
	{
		return strMerchantCode;
	}

	public void setStrMerchantCode(String strMerchantCode)
	{
		this.strMerchantCode = strMerchantCode;
	}

	public String getStrAuthenticationKey()
	{
		return strAuthenticationKey;
	}

	public void setStrAuthenticationKey(String strAuthenticationKey)
	{
		this.strAuthenticationKey = strAuthenticationKey;
	}

	public String getStrSalt()
	{
		return strSalt;
	}

	public void setStrSalt(String strSalt)
	{
		this.strSalt = strSalt;
	}

	public String getStrWERAOnlineOrderIntegration()
	{
		return strWERAOnlineOrderIntegration;
	}

	public void setStrWERAOnlineOrderIntegration(String strWERAOnlineOrderIntegration)
	{
		this.strWERAOnlineOrderIntegration = strWERAOnlineOrderIntegration;
	}

	public String getStrWERAMerchantOutletId()
	{
		return strWERAMerchantOutletId;
	}

	public void setStrWERAMerchantOutletId(String strWERAMerchantOutletId)
	{
		this.strWERAMerchantOutletId = strWERAMerchantOutletId;
	}

	public String getStrWERAAuthenticationAPIKey()
	{
		return strWERAAuthenticationAPIKey;
	}

	public void setStrWERAAuthenticationAPIKey(String strWERAAuthenticationAPIKey)
	{
		this.strWERAAuthenticationAPIKey = strWERAAuthenticationAPIKey;
	}

	public String getStrEnableNFCInterface()
	{
		return strEnableNFCInterface;
	}

	public void setStrEnableNFCInterface(String strEnableNFCInterface)
	{
		this.strEnableNFCInterface = strEnableNFCInterface;
	}

	
	

}
