package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

//@Entity
//@Table(name="tblbilltaxdtl")
@Embeddable
public class clsBillTaxDtl  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillTaxDtl(){}
	
	
	
//	private String strBillNo;
	
	private String strTaxCode;
	
	private   double dblTaxableAmount;
	
	private  double dblTaxAmount;
	
//	private String strClientCode;
	
	private String strDataPostFlag;
	
//	private String dteBillDate;
	

	public String getStrTaxCode() {
		return strTaxCode;
	}
	public void setStrTaxCode(String strTaxCode) {
		this.strTaxCode = strTaxCode;
	}
	public double getDblTaxAmount() {
		return dblTaxAmount;
	}
	public void setDblTaxAmount(double dblTaxAmount) {
		this.dblTaxAmount = dblTaxAmount;
	}
	
	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public double getDblTaxableAmount() {
		return dblTaxableAmount;
	}
	public void setDblTaxableAmount(double dblTaxableAmount) {
		this.dblTaxableAmount = dblTaxableAmount;
	}
}
