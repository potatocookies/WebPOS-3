package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

//@Entity
//@Table(name="tblbillpromotiondtl")
@Embeddable
public class clsBillPromotionDtlModel   implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillPromotionDtlModel(){}

	
//    private String strBillNo;

    private String strItemCode;
    
    private String strPromotionCode;
	
    private double dblQuantity;
    
    private double dblRate;
	
//    private String strClientCode;
	
    private String strDataPostFlag;

    private String strPromoType;
	
    private double dblAmount;

    private double dblDiscountPer;

    private double dblDiscountAmt;

// C   private String dteBillDate;


	public String getStrItemCode() {
		return strItemCode;
	}

	public void setStrItemCode(String strItemCode) {
		this.strItemCode = strItemCode;
	}

	public String getStrPromotionCode() {
		return strPromotionCode;
	}

	public void setStrPromotionCode(String strPromotionCode) {
		this.strPromotionCode = strPromotionCode;
	}



	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrPromoType() {
		return strPromoType;
	}

	public void setStrPromoType(String strPromoType) {
		this.strPromoType = strPromoType;
	}

	public double getDblAmount() {
		return dblAmount;
	}

	public void setDblAmount(double dblAmount) {
		this.dblAmount = dblAmount;
	}

	public double getDblDiscountPer() {
		return dblDiscountPer;
	}

	public void setDblDiscountPer(double dblDiscountPer) {
		this.dblDiscountPer = dblDiscountPer;
	}

	public double getDblDiscountAmt() {
		return dblDiscountAmt;
	}

	public void setDblDiscountAmt(double dblDiscountAmt) {
		this.dblDiscountAmt = dblDiscountAmt;
	}

	public double getDblQuantity() {
		return dblQuantity;
	}

	public void setDblQuantity(double dblQuantity) {
		this.dblQuantity = dblQuantity;
	}

	public double getDblRate() {
		return dblRate;
	}

	public void setDblRate(double dblRate) {
		this.dblRate = dblRate;
	}

}
