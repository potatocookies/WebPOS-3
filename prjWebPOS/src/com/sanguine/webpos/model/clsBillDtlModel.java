package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sanguine.base.model.clsBaseModel;
//@Entity
//@Table(name="tblbilldtl")
@Embeddable
public class clsBillDtlModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public clsBillDtlModel(){}
	private String strItemCode;
	private String strItemName;
	
//	private String strBillNo;
	private String strAdvBookingNo;
	private   double dblRate;
	private  double dblQuantity;
	private   double dblAmount;
	private  double dblTaxAmount;
	private String dteBillDate;
	private String strKOTNo;
//	private String strClientCode;
	private  String strCustomerCode;
	private  String tmeOrderProcessing;
	private  String strDataPostFlag;
	private String strMMSDataPostFlag;
	private  String strManualKOTNo;
	private  String tdhYN;
	private String strPromoCode;
	private  String strCounterCode;
	private  String strWaiterNo;
	private  double dblDiscountAmt;
	private double dblDiscountPer;

	@Transient
	private  String sequenceNo;

//	private String dtBillDate;
	
	private String tmeOrderPickup;
	
	
	public String getStrItemCode() {
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode) {
		this.strItemCode = strItemCode;
	}
	public String getStrItemName() {
		return strItemName;
	}
	public void setStrItemName(String strItemName) {
		this.strItemName = strItemName;
	}

	public String getStrAdvBookingNo() {
		return strAdvBookingNo;
	}
	public void setStrAdvBookingNo(String strAdvBookingNo) {
		this.strAdvBookingNo = strAdvBookingNo;
	}
	public double getDblRate() {
		return dblRate;
	}
	public void setDblRate(double dblRate) {
		this.dblRate = dblRate;
	}
	public double getDblQuantity() {
		return dblQuantity;
	}
	public void setDblQuantity(double dblQuantity) {
		this.dblQuantity = dblQuantity;
	}
	public double getDblAmount() {
		return dblAmount;
	}
	public void setDblAmount(double dblAmount) {
		this.dblAmount = dblAmount;
	}
	public double getDblTaxAmount() {
		return dblTaxAmount;
	}
	public void setDblTaxAmount(double dblTaxAmount) {
		this.dblTaxAmount = dblTaxAmount;
	}
	public String getDteBillDate() {
		return dteBillDate;
	}
	public void setDteBillDate(String dteBillDate) {
		this.dteBillDate = dteBillDate;
	}
	public String getStrKOTNo() {
		return strKOTNo;
	}
	public void setStrKOTNo(String strKOTNo) {
		this.strKOTNo = strKOTNo;
	}
	
	public String getStrCustomerCode() {
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}
	public String getTmeOrderProcessing() {
		return tmeOrderProcessing;
	}
	public void setTmeOrderProcessing(String tmeOrderProcessing) {
		this.tmeOrderProcessing = tmeOrderProcessing;
	}
	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}
	public String getStrMMSDataPostFlag() {
		return strMMSDataPostFlag;
	}
	public void setStrMMSDataPostFlag(String strMMSDataPostFlag) {
		this.strMMSDataPostFlag = strMMSDataPostFlag;
	}
	public String getStrManualKOTNo() {
		return strManualKOTNo;
	}
	public void setStrManualKOTNo(String strManualKOTNo) {
		this.strManualKOTNo = strManualKOTNo;
	}
	public String getTdhYN() {
		return tdhYN;
	}
	public void setTdhYN(String tdhYN) {
		this.tdhYN = (String) setDefaultValue( tdhYN, "NA");
	}
	public String getStrPromoCode() {
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode) {
		this.strPromoCode = (String) setDefaultValue( strPromoCode, "NA");
	}
	public String getStrCounterCode() {
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode) {
		this.strCounterCode = strCounterCode;
	}
	public String getStrWaiterNo() {
		return strWaiterNo;
	}
	public void setStrWaiterNo(String strWaiterNo) {
		this.strWaiterNo = strWaiterNo;
	}
	public double getDblDiscountAmt() {
		return dblDiscountAmt;
	}
	public void setDblDiscountAmt(double dblDiscountAmt) {
		this.dblDiscountAmt = dblDiscountAmt;
	}
	public double getDblDiscountPer() {
		return dblDiscountPer;
	}
	public void setDblDiscountPer(double dblDiscountPer) {
		this.dblDiscountPer = dblDiscountPer;
	}
	public String getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo =(String) setDefaultValue( sequenceNo, "NA") ;
	}

	public String getTmeOrderPickup() {
		return tmeOrderPickup;
	}
	public void setTmeOrderPickup(String tmeOrderPickup) {
		this.tmeOrderPickup = tmeOrderPickup;
	}
	
	//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}
}
