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
//@Table(name="tblbillmodifierdtl")
@Embeddable
public class clsBillModifierDtlModel   implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillModifierDtlModel(){}


//    private String strBillNo;

    private String strItemCode;

    private String strModifierCode;

    private String strModifierName;

    private double dblRate;

    private double dblQuantity;

    private double dblAmount;

//    private String strClientCode;

    private String strCustomerCode;

    private String strDataPostFlag;

    private String strMMSDataPostFlag;
    
    @Transient
    private String sequenceNo;
    
    private double dblDiscPer;
    
    private double dblDiscAmt;

    private String strDefaultModifierDeselectedYN;
	
//    private String dteBillDate;


	public String getStrItemCode() {
		return strItemCode;
	}

	public void setStrItemCode(String strItemCode) {
		this.strItemCode = strItemCode;
	}

	public String getStrModifierCode() {
		return strModifierCode;
	}

	public void setStrModifierCode(String strModifierCode) {
		this.strModifierCode = strModifierCode;
	}

	public String getStrModifierName() {
		return strModifierName;
	}

	public void setStrModifierName(String strModifierName) {
		this.strModifierName = strModifierName;
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


	public String getStrCustomerCode() {
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
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

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo =(String) setDefaultValue( sequenceNo, "NA")  ;
	}

	public double getDblDiscPer() {
		return dblDiscPer;
	}

	public void setDblDiscPer(double dblDiscPer) {
		this.dblDiscPer = dblDiscPer;
	}

	public double getDblDiscAmt() {
		return dblDiscAmt;
	}

	public void setDblDiscAmt(double dblDiscAmt) {
		this.dblDiscAmt = dblDiscAmt;
	}

	public String getStrDefaultModifierDeselectedYN() {
		return strDefaultModifierDeselectedYN;
	}

	public void setStrDefaultModifierDeselectedYN(
			String strDefaultModifierDeselectedYN) {
		this.strDefaultModifierDeselectedYN =(String) setDefaultValue( strDefaultModifierDeselectedYN, "N")  ;
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
