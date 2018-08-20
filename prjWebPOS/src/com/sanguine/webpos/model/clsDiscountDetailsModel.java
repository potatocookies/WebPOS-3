
package com.sanguine.webpos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsDiscountDetailsModel{

	
//Variable Declaration


	
	private String strDiscOnCode;
	
	private String strDiscOnName;
	
	private String strDiscountType;

	private String dblDiscountValue;

	private String strUserCreated;
	
	private String strUserEdited;

	private String dteDateCreated;
	
	private String dteDateEdited;
	
	private String strDataPostFlag;

	public String getStrDiscOnCode() {
		return strDiscOnCode;
	}

	public void setStrDiscOnCode(String strDiscOnCode) {
		this.strDiscOnCode = strDiscOnCode;
	}

	public String getStrDiscOnName() {
		return strDiscOnName;
	}

	public void setStrDiscOnName(String strDiscOnName) {
		this.strDiscOnName = strDiscOnName;
	}

	public String getStrDiscountType() {
		return strDiscountType;
	}

	public void setStrDiscountType(String strDiscountType) {
		this.strDiscountType = strDiscountType;
	}

	public String getDblDiscountValue() {
		return dblDiscountValue;
	}

	public void setDblDiscountValue(String dblDiscountValue) {
		this.dblDiscountValue = dblDiscountValue;
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
	

	
	
	
}

	