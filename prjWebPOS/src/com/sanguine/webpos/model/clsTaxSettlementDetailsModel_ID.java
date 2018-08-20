
package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsTaxSettlementDetailsModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTaxCode")
	private String strTaxCode;
	
	@Column(name="strSettlementCode")
	private String strSettlementCode;
	

	public clsTaxSettlementDetailsModel_ID(){}
	public clsTaxSettlementDetailsModel_ID(String strTaxCode, String strSettlementCode){
		this.strTaxCode=strTaxCode;
		this.strSettlementCode=strSettlementCode;
		
	}

//Setter-Getter Methods
	

public String getStrSettlementCode() {
		return strSettlementCode;
	}
	public String getStrTaxCode() {
	return strTaxCode;
}
public void setStrTaxCode(String strTaxCode) {
	this.strTaxCode = strTaxCode;
}
	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}
	//HashCode and Equals Funtions
	
	@Override
	public int hashCode() {
		return this.strTaxCode.hashCode();
		
	}

}
