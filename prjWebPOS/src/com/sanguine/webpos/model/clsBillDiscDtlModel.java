package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sanguine.base.model.clsBaseModel;
//@Entity
//@Table(name="tblbilldiscdtl")
@Embeddable
public class clsBillDiscDtlModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillDiscDtlModel(){}
	
//	private String strBillNo;
	private String strPOSCode;
	private String strDiscOnType;
	private String strDiscOnValue;
	private String strDiscReasonCode;
	private String strDiscRemarks;
	private String strUserCreated;
	
	private String strUserEdited;
	private String dteDateCreated;
	private String dteDateEdited;
//	private String strClientCode;
	private String strDataPostFlag;
//	private String dteBillDate;
	
	
	
	private   double dblDiscAmt;
	private  double dblDiscPer;
	
	@Transient
	private   double dblAmount;
	
	private  double dblDiscOnAmt;

	public String getStrPOSCode() {
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	public String getStrDiscOnType() {
		return strDiscOnType;
	}
	public void setStrDiscOnType(String strDiscOnType) {
		this.strDiscOnType = strDiscOnType;
	}
	public String getStrDiscOnValue() {
		return strDiscOnValue;
	}
	public void setStrDiscOnValue(String strDiscOnValue) {
		this.strDiscOnValue = strDiscOnValue;
	}
	public String getStrDiscReasonCode() {
		return strDiscReasonCode;
	}
	public void setStrDiscReasonCode(String strDiscReasonCode) {
		this.strDiscReasonCode = strDiscReasonCode;
	}
	public String getStrDiscRemarks() {
		return strDiscRemarks;
	}
	public void setStrDiscRemarks(String strDiscRemarks) {
		this.strDiscRemarks = strDiscRemarks;
	}
	public String getStrUserCreated() {
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}
	public String getStrUserEdited() {
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited) {
		this.strUserEdited = strUserEdited;
	}
	public String getDteDateCreated() {
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated) {
		this.dteDateCreated = dteDateCreated;
	}
	public String getDteDateEdited() {
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited) {
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public double getDblDiscAmt() {
		return dblDiscAmt;
	}
	public void setDblDiscAmt(double dblDiscAmt) {
		this.dblDiscAmt = dblDiscAmt;
	}
	public double getDblDiscPer() {
		return dblDiscPer;
	}
	public void setDblDiscPer(double dblDiscPer) {
		this.dblDiscPer = dblDiscPer;
	}
	public double getDblAmount() {
		return dblAmount;
	}
	public void setDblAmount(double dblAmount) {
		this.dblAmount = dblAmount;
	}
	public double getDblDiscOnAmt() {
		return dblDiscOnAmt;
	}
	public void setDblDiscOnAmt(double dblDiscOnAmt) {
		this.dblDiscOnAmt = dblDiscOnAmt;
	}

}
