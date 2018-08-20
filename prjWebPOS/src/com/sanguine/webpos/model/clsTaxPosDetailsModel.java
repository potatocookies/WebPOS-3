

package com.sanguine.webpos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsTaxPosDetailsModel{

//Variable Declaration
	

	
	private String strPOSCode;

	
	private String strTaxDesc;
	
	public String getStrPOSCode() {
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	public String getStrTaxDesc() {
		return strTaxDesc;
	}
	public void setStrTaxDesc(String strTaxDesc) {
		this.strTaxDesc = strTaxDesc;
	}
	
	
	
}

	