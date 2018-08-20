package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;

public class clsPOSDirectBillerBean
{
	private String strBillNo;
    private String dteBillDate;
    private String strPosName;
    private double dblSubTotal;
    private double dblGrandTotal;
    private String strItemCode;
    private String strItemName;
    private double dblQuantity;
    private double dblAmount;
    private double dblDiscountAmt;
    private double dblDiscountPer;
    private double dblBillDiscPer;
    private String strSettelmentMode;
    private double dblTaxAmt;
    private double dblSettlementAmt;
    private String strDiscValue;
    private String strDiscType;
    private String billTransType; 
    
    private String customerName;
    private String strCustomerType;
    private String takeAway;
    private String custMobileNo;
    private String strDeliveryBoyCode;
    private String strDeliveryBoyName;
    private String strPosCode;
    private String strHomeDeliveryAddress="Home";
    
    
    private String strCustomerCode;
    private String strCustomerName;
    private String strCustMobileNo;

    
    private List<clsPOSPricingMasterBean> listOfDirectBillerMenuItemPricing=new  LinkedList<>();
    private List<clsPOSMenuHeadBean> listOfDirectBillerMenuHeads=new  LinkedList<>();
    
    private List<clsPOSBillItemDtlBean>listOfDirectBillerBillItemDtl=new  LinkedList<>();
    private List<clsPOSItemsDtlsInBill> listItemsDtlInBill=new ArrayList<clsPOSItemsDtlsInBill>();
   
        
    private JSONArray jsonArrForDirectBillerMenuItemPricing=new JSONArray();   
	private JSONArray jsonArrForDirectBillerMenuHeads=new JSONArray();
	private JSONArray jsonArrForDirectBillerBillItemDtl=new JSONArray();
	private JSONArray jsonArrForDirectBillerFooterButtons=new JSONArray();
	private JSONArray jsonArrForPopularItems;
	private JSONArray jsonArrForSettleButtons;
	
    
    
	
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
	public String getStrDiscValue()
	{
		return strDiscValue;
	}
	public void setStrDiscValue(String strDiscValue)
	{
		this.strDiscValue = strDiscValue;
	}
	public String getStrDiscType()
	{
		return strDiscType;
	}
	public void setStrDiscType(String strDiscType)
	{
		this.strDiscType = strDiscType;
	}
	public List<clsPOSPricingMasterBean> getListOfDirectBillerMenuItemPricing()
	{
		return listOfDirectBillerMenuItemPricing;
	}
	public void setListOfDirectBillerMenuItemPricing(List<clsPOSPricingMasterBean> listOfDirectBillerMenuItemPricing)
	{
		this.listOfDirectBillerMenuItemPricing = listOfDirectBillerMenuItemPricing;
	}
	
	public List<clsPOSMenuHeadBean> getListOfDirectBillerMenuHeads()
	{
		return listOfDirectBillerMenuHeads;
	}
	public void setListOfDirectBillerMenuHeads(List<clsPOSMenuHeadBean> listOfDirectBillerMenuHeads)
	{
		this.listOfDirectBillerMenuHeads = listOfDirectBillerMenuHeads;
	}
	
	public JSONArray getJsonArrForDirectBillerMenuItemPricing()
	{
			return jsonArrForDirectBillerMenuItemPricing;
	}
	public void setJsonArrForDirectBillerMenuItemPricing(JSONArray jsonArrForDirectBillerMenuItemPricing)
	{
		this.jsonArrForDirectBillerMenuItemPricing = jsonArrForDirectBillerMenuItemPricing;
	}
	public JSONArray getJsonArrForDirectBillerMenuHeads()
	{
			return jsonArrForDirectBillerMenuHeads;
	}
	public void setJsonArrForDirectBillerMenuHeads(JSONArray jsonArrForDirectBillerMenuHeads)
	{
			this.jsonArrForDirectBillerMenuHeads = jsonArrForDirectBillerMenuHeads;
	}
	public JSONArray getJsonArrForDirectBillerBillItemDtl()
	{
		return jsonArrForDirectBillerBillItemDtl;
	}
	public void setJsonArrForDirectBillerBillItemDtl(JSONArray jsonArrForDirectBillerBillItemDtl)
	{
		this.jsonArrForDirectBillerBillItemDtl = jsonArrForDirectBillerBillItemDtl;
	}
	public List<clsPOSBillItemDtlBean> getListOfDirectBillerBillItemDtl()
	{
		return listOfDirectBillerBillItemDtl;
	}
	public void setListOfDirectBillerBillItemDtl(List<clsPOSBillItemDtlBean> listOfDirectBillerBillItemDtl)
	{
		this.listOfDirectBillerBillItemDtl = listOfDirectBillerBillItemDtl;
	}
	public JSONArray getJsonArrForDirectBillerFooterButtons()
	{
		return jsonArrForDirectBillerFooterButtons;
	}
	public void setJsonArrForDirectBillerFooterButtons(JSONArray jsonArrForDirectBillerFooterButtons)
	{
		this.jsonArrForDirectBillerFooterButtons = jsonArrForDirectBillerFooterButtons;
	}
	public JSONArray getJsonArrForPopularItems() {
		return jsonArrForPopularItems;
	}
	public void setJsonArrForPopularItems(JSONArray jsonArrForPopularItems) {
		this.jsonArrForPopularItems = jsonArrForPopularItems;
	}
	public String getStrCustomerCode() {
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getStrCustomerType() {
		return strCustomerType;
	}
	public void setStrCustomerType(String strCustomerType) {
		this.strCustomerType = strCustomerType;
	}
	
	public String getTakeAway() {
		return takeAway;
	}
	public void setTakeAway(String takeAway) {
		this.takeAway = takeAway;
	}
	public String getBillTransType() {
		return billTransType;
	}
	public void setBillTransType(String billTransType) {
		this.billTransType = billTransType;
	}
	public List<clsPOSItemsDtlsInBill> getListItemsDtlInBill() {
		return listItemsDtlInBill;
	}
	public void setListItemsDtlInBill(List<clsPOSItemsDtlsInBill> listItemsDtlInBill) {
		this.listItemsDtlInBill = listItemsDtlInBill;
	}
	public String getCustMobileNo() {
		return custMobileNo;
	}
	public void setCustMobileNo(String custMobileNo) {
		this.custMobileNo = custMobileNo;
	}
	public String getStrDeliveryBoyCode() {
		return strDeliveryBoyCode;
	}
	public void setStrDeliveryBoyCode(String strDeliveryBoyCode) {
		this.strDeliveryBoyCode = strDeliveryBoyCode;
	}
	public String getStrDeliveryBoyName() {
		return strDeliveryBoyName;
	}
	public void setStrDeliveryBoyName(String strDeliveryBoyName) {
		this.strDeliveryBoyName = strDeliveryBoyName;
	}
	public JSONArray getJsonArrForSettleButtons() {
		return jsonArrForSettleButtons;
	}
	public void setJsonArrForSettleButtons(JSONArray jsonArrForSettleButtons) {
		this.jsonArrForSettleButtons = jsonArrForSettleButtons;
	}
	public String getStrPosCode()
	{
		return strPosCode;
	}
	public void setStrPosCode(String strPosCode)
	{
		this.strPosCode = strPosCode;
	}
	public String getStrCustomerName()
	{
		return strCustomerName;
	}
	public void setStrCustomerName(String strCustomerName)
	{
		this.strCustomerName = strCustomerName;
	}
	public String getStrCustMobileNo()
	{
		return strCustMobileNo;
	}
	public void setStrCustMobileNo(String strCustMobileNo)
	{
		this.strCustMobileNo = strCustMobileNo;
	}
	public String getStrHomeDeliveryAddress()
	{
		return strHomeDeliveryAddress;
	}
	public void setStrHomeDeliveryAddress(String strHomeDeliveryAddress)
	{
		this.strHomeDeliveryAddress = strHomeDeliveryAddress;
	}
	
	
	
	
}
