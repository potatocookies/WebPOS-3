/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ajjim
 */
public class clsPOSBillItemDtlBean
{
    private String strBillNo;
    private String dteBillDate;
    private String strPosName;
    private double dblSubTotal;
    private double dblGrandTotal;
    private String strItemCode;
    private String strItemName;
    private double dblQuantity;
    private double dblRate;
    private double dblAmount;
    private double dblDiscountAmt;
    private double dblDiscountPer;
    private double dblBillDiscPer;
    private String strSettelmentMode;
    private double dblTaxAmt;
    private double dblSettlementAmt;
    private String strDiscValue;
    private String strDiscType;
    private String strSerialNo;
    private double dblNetTotal;
    private String promoCode;
    private String promoType;
    private double freeItemQty;
    private double moveItemQty;
    private double billAmt;
    private boolean isModifier;
    private double itemSalePrice;
    private String strCustomerCode;
    private String customerName;
    private String strOperationType;
    private String strTakeAway;
    private String strCustomerType;
    private String strTaxName;
    private String billTransType;
    
 
    private String dteDateCreated;
   
    private String strDiscountRemark;
    private double dblTipAmount;
    private String strRemark;
    private String strReasonName;
    private String strSettelmentDesc;
    private String longMobileNo;
    private String strCustomerName;
   
    private String strPosCode;
    private String dteVoidedDate;
    private String strEntryTime;
    private String strVoidedTime;
    private String strModifiyTime;
    private double dblModifiedAmount;
    private double dblAmountTemp;
    private String strUserEdited;
    private String strUserCreated;
    private String strKotTime;
    private String strDifference;
    
    private String strTaxIndicator;
    private String strSubGroupCode;
    private String strSubGroupName;
    private String strGroupCode;
    private String strGroupName;
    
    private String strKOTNo;
   
    private String strTableCode;
    
    
    
    public String getStrGroupCode()
	{
		return strGroupCode;
	}



	public void setStrGroupCode(String strGroupCode)
	{
		this.strGroupCode = strGroupCode;
	}



	public String getStrTableCode()
	{
		return strTableCode;
	}



	public void setStrTableCode(String strTableCode)
	{
		this.strTableCode = strTableCode;
	}



	public String getStrWaiterCode()
	{
		return strWaiterCode;
	}



	public void setStrWaiterCode(String strWaiterCode)
	{
		this.strWaiterCode = strWaiterCode;
	}



	private String strTableName;
   
    private String strWaiterCode;
    private String strWaiterName;
   
    private int intPaxNo;
   
    private int noOfBills;
    
    private int intBillSeriesPaxNo;
    
    private Boolean strApplicableYN;
    
    private List<clsPOSBillItemDtlBean> listBusyTableDtl=new ArrayList<clsPOSBillItemDtlBean>();	 
    
    private List<clsPOSModifiersOnItem> listModifierDtl=new ArrayList<clsPOSModifiersOnItem>();	
    
    private List<clsPOSCustomerDtlsOnBill> listCustomerDtlOnBill=new ArrayList<clsPOSCustomerDtlsOnBill>();
    
    public clsPOSBillItemDtlBean(String strItemCode, String strItemName, double dblQuantity, double dblAmount, double itemSalePrice, boolean isModifier,String customerCode,String customerType,String operationType,String takeAway) {
        this.strItemCode = strItemCode;
        this.strItemName = strItemName;
        this.dblQuantity = dblQuantity;
        this.dblAmount = dblAmount;
        this.itemSalePrice = itemSalePrice;
        this.isModifier = isModifier;
        this.strCustomerCode= customerCode;
        this.strCustomerType = customerType;
        this.strOperationType= operationType;
        this.strTakeAway = takeAway;
    }
    
    
    
    public List<clsPOSModifiersOnItem> getListModifierDtl() {
		return listModifierDtl;
	}

	public void setListModifierDtl(List<clsPOSModifiersOnItem> listModifierDtl) {
		this.listModifierDtl = listModifierDtl;
	}

	public String getStrSerialNo() {
		return strSerialNo;
	}

	public void setStrSerialNo(String strSerialNo) {
		this.strSerialNo = strSerialNo;
	}

	public String getStrDiscValue() {
        return strDiscValue;
    }

    public String getStrDiscType() {
        return strDiscType;
    }

    public void setStrDiscType(String strDiscType) {
        this.strDiscType = strDiscType;
    }

    public void setStrDiscValue(String strDiscValue) {
        this.strDiscValue = strDiscValue;
    }
   
    public clsPOSBillItemDtlBean()
    {
    }

    public String getStrBillNo()
    {
        return strBillNo;
    }

    public void setStrBillNo(String strBillNo)
    {
        this.strBillNo = strBillNo;
    }

    public String getDteBillDate()
    {
        return dteBillDate;
    }

    public void setDteBillDate(String dteBillDate)
    {
        this.dteBillDate = dteBillDate;
    }

    public String getStrPosName()
    {
        return strPosName;
    }

    public void setStrPosName(String strPosName)
    {
        this.strPosName = strPosName;
    }

    public double getDblSubTotal()
    {
        return dblSubTotal;
    }

    public void setDblSubTotal(double dblSubTotal)
    {
        this.dblSubTotal = dblSubTotal;
    }

    public double getDblGrandTotal()
    {
        return dblGrandTotal;
    }

    public void setDblGrandTotal(double dblGrandTotal)
    {
        this.dblGrandTotal = dblGrandTotal;
    }

    public String getStrItemCode()
    {
        return strItemCode;
    }

    public void setStrItemCode(String strItemCode)
    {
        this.strItemCode = strItemCode;
    }

    public String getStrItemName()
    {
        return strItemName;
    }

    public void setStrItemName(String strItemName)
    {
        this.strItemName = strItemName;
    }

    public double getDblQuantity()
    {
        return dblQuantity;
    }

    public void setDblQuantity(double dblQuantity)
    {
        this.dblQuantity = dblQuantity;
    }

    public double getDblAmount()
    {
        return dblAmount;
    }

    public void setDblAmount(double dblAmount)
    {
        this.dblAmount = dblAmount;
    }

    public double getDblDiscountAmt()
    {
        return dblDiscountAmt;
    }

    public void setDblDiscountAmt(double dblDiscountAmt)
    {
        this.dblDiscountAmt = dblDiscountAmt;
    }

    public double getDblDiscountPer()
    {
        return dblDiscountPer;
    }

    public void setDblDiscountPer(double dblDiscountPer)
    {
        this.dblDiscountPer = dblDiscountPer;
    }

    public double getDblBillDiscPer()
    {
        return dblBillDiscPer;
    }

    public void setDblBillDiscPer(double dblBillDiscPer)
    {
        this.dblBillDiscPer = dblBillDiscPer;
    }

    public String getStrSettelmentMode()
    {
        return strSettelmentMode;
    }

    public void setStrSettelmentMode(String strSettelmentMode)
    {
        this.strSettelmentMode = strSettelmentMode;
    }

    public double getDblTaxAmt()
    {
        return dblTaxAmt;
    }

    public void setDblTaxAmt(double dblTaxAmt)
    {
        this.dblTaxAmt = dblTaxAmt;
    }

    public double getDblSettlementAmt()
    {
        return dblSettlementAmt;
    }

    public void setDblSettlementAmt(double dblSettlementAmt)
    {
        this.dblSettlementAmt = dblSettlementAmt;
    }

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCade) {
		this.promoCode = promoCade;
	}

	public String getPromoType() {
		return promoType;
	}

	public void setPromoType(String promoType) {
		this.promoType = promoType;
	}

	public double getFreeItemQty() {
		return freeItemQty;
	}

	public void setFreeItemQty(double freeItemQty) {
		this.freeItemQty = freeItemQty;
	}

	public double getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(double billAmt) {
		this.billAmt = billAmt;
	}

	public boolean isModifier() {
		return isModifier;
	}

	public void setModifier(boolean isModifier) {
		this.isModifier = isModifier;
	}

	public double getItemSalePrice() {
		return itemSalePrice;
	}

	public void setItemSalePrice(double itemSalePrice) {
		this.itemSalePrice = itemSalePrice;
	}

	public String getStrCustomerCode() {
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}

	public String getStrOperationType() {
		return strOperationType;
	}

	public void setStrOperationType(String strOperationType) {
		this.strOperationType = strOperationType;
	}

	public String getStrTakeAway() {
		return strTakeAway;
	}
	public void setStrTakeAway(String strTakeAway) {
		this.strTakeAway = strTakeAway;
	}

	public String getStrCustomerType() {
		return strCustomerType;
	}

	public void setStrCustomerType(String strCustomerType) {
		this.strCustomerType = strCustomerType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getStrTaxName() {
		return strTaxName;
	}

	public void setStrTaxName(String strTaxName) {
		this.strTaxName = strTaxName;
	}

	public String getBillTransType() {
		return billTransType;
	}
	public void setBillTransType(String billTransType) {
		this.billTransType = billTransType;
	}



	public String getDteDateCreated()
	{
		return dteDateCreated;
	}



	public void setDteDateCreated(String dteDateCreated)
	{
		this.dteDateCreated = dteDateCreated;
	}



	public String getStrDiscountRemark()
	{
		return strDiscountRemark;
	}



	public void setStrDiscountRemark(String strDiscountRemark)
	{
		this.strDiscountRemark = strDiscountRemark;
	}



	public double getDblTipAmount()
	{
		return dblTipAmount;
	}



	public void setDblTipAmount(double dblTipAmount)
	{
		this.dblTipAmount = dblTipAmount;
	}



	public String getStrRemark()
	{
		return strRemark;
	}



	public void setStrRemark(String strRemark)
	{
		this.strRemark = strRemark;
	}



	public String getStrReasonName()
	{
		return strReasonName;
	}



	public void setStrReasonName(String strReasonName)
	{
		this.strReasonName = strReasonName;
	}



	public String getStrSettelmentDesc()
	{
		return strSettelmentDesc;
	}



	public void setStrSettelmentDesc(String strSettelmentDesc)
	{
		this.strSettelmentDesc = strSettelmentDesc;
	}



	public String getLongMobileNo()
	{
		return longMobileNo;
	}



	public void setLongMobileNo(String longMobileNo)
	{
		this.longMobileNo = longMobileNo;
	}



	public String getStrCustomerName()
	{
		return strCustomerName;
	}



	public void setStrCustomerName(String strCustomerName)
	{
		this.strCustomerName = strCustomerName;
	}



	public String getStrPosCode()
	{
		return strPosCode;
	}



	public void setStrPosCode(String strPosCode)
	{
		this.strPosCode = strPosCode;
	}



	public String getDteVoidedDate()
	{
		return dteVoidedDate;
	}



	public void setDteVoidedDate(String dteVoidedDate)
	{
		this.dteVoidedDate = dteVoidedDate;
	}



	public String getStrEntryTime()
	{
		return strEntryTime;
	}



	public void setStrEntryTime(String strEntryTime)
	{
		this.strEntryTime = strEntryTime;
	}



	public String getStrVoidedTime()
	{
		return strVoidedTime;
	}



	public void setStrVoidedTime(String strVoidedTime)
	{
		this.strVoidedTime = strVoidedTime;
	}



	public double getDblModifiedAmount()
	{
		return dblModifiedAmount;
	}



	public void setDblModifiedAmount(double dblModifiedAmount)
	{
		this.dblModifiedAmount = dblModifiedAmount;
	}



	public double getDblAmountTemp()
	{
		return dblAmountTemp;
	}



	public void setDblAmountTemp(double dblAmountTemp)
	{
		this.dblAmountTemp = dblAmountTemp;
	}



	public String getStrUserEdited()
	{
		return strUserEdited;
	}



	public void setStrUserEdited(String strUserEdited)
	{
		this.strUserEdited = strUserEdited;
	}



	public String getStrTaxIndicator()
	{
		return strTaxIndicator;
	}



	public void setStrTaxIndicator(String strTaxIndicator)
	{
		this.strTaxIndicator = strTaxIndicator;
	}



	public String getStrSubGroupCode()
	{
		return strSubGroupCode;
	}



	public void setStrSubGroupCode(String strSubGroupCode)
	{
		this.strSubGroupCode = strSubGroupCode;
	}



	public String getStrSubGroupName()
	{
		return strSubGroupName;
	}



	public void setStrSubGroupName(String strSubGroupName)
	{
		this.strSubGroupName = strSubGroupName;
	}



	public String getStrGroupName()
	{
		return strGroupName;
	}



	public void setStrGroupName(String strGroupName)
	{
		this.strGroupName = strGroupName;
	}



	public List<clsPOSCustomerDtlsOnBill> getListCustomerDtlOnBill()
	{
		return listCustomerDtlOnBill;
	}



	public void setListCustomerDtlOnBill(List<clsPOSCustomerDtlsOnBill> listCustomerDtlOnBill)
	{
		this.listCustomerDtlOnBill = listCustomerDtlOnBill;
	}



	public int getNoOfBills()
	{
		return noOfBills;
	}



	public void setNoOfBills(int noOfBills)
	{
		this.noOfBills = noOfBills;
	}



	public int getIntBillSeriesPaxNo()
	{
		return intBillSeriesPaxNo;
	}



	public void setIntBillSeriesPaxNo(int intBillSeriesPaxNo)
	{
		this.intBillSeriesPaxNo = intBillSeriesPaxNo;
	}



	public String getStrKOTNo() {
		return strKOTNo;
	}



	public void setStrKOTNo(String strKOTNo) {
		this.strKOTNo = strKOTNo;
	}



	public String getStrTableName() {
		return strTableName;
	}



	public void setStrTableName(String strTableName) {
		this.strTableName = strTableName;
	}



	public String getStrWaiterName() {
		return strWaiterName;
	}



	public void setStrWaiterName(String strWaiterName) {
		this.strWaiterName = strWaiterName;
	}



	public int getIntPaxNo() {
		return intPaxNo;
	}



	public void setIntPaxNo(int intPaxNo) {
		this.intPaxNo = intPaxNo;
	}



	public String getStrUserCreated() {
		return strUserCreated;
	}



	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}



	public String getStrModifiyTime() {
		return strModifiyTime;
	}



	public void setStrModifiyTime(String strModifiyTime) {
		this.strModifiyTime = strModifiyTime;
	}



	public String getStrKotTime() {
		return strKotTime;
	}



	public void setStrKotTime(String strKotTime) {
		this.strKotTime = strKotTime;
	}



	public String getStrDifference() {
		return strDifference;
	}



	public void setStrDifference(String strDifference) {
		this.strDifference = strDifference;
	}



	public Boolean getStrApplicableYN()
	{
		return strApplicableYN;
	}



	public void setStrApplicableYN(Boolean strApplicableYN)
	{
		this.strApplicableYN = strApplicableYN;
	}



	public List<clsPOSBillItemDtlBean> getListBusyTableDtl()
	{
		return listBusyTableDtl;
	}



	public void setListBusyTableDtl(List<clsPOSBillItemDtlBean> listBusyTableDtl)
	{
		this.listBusyTableDtl = listBusyTableDtl;
	}



	public double getDblRate()
	{
		return dblRate;
	}



	public void setDblRate(double dblRate)
	{
		this.dblRate = dblRate;
	}



	public double getMoveItemQty()
	{
		return moveItemQty;
	}



	public void setMoveItemQty(double moveItemQty)
	{
		this.moveItemQty = moveItemQty;
	}



	public double getDblNetTotal()
	{
		return dblNetTotal;
	}



	public void setDblNetTotal(double dblNetTotal)
	{
		this.dblNetTotal = dblNetTotal;
	}


	
    
}
