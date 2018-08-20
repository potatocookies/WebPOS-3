package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
@SuppressWarnings("serial")

public class clsTaxMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTaxCode")
	private String strTaxCode;
	
	@Column(name="strClientCode")
	private String strClientCode;
	
	public clsTaxMasterModel_ID(){}
	public clsTaxMasterModel_ID(String strTaxCode,String strClientCode){
		this.strTaxCode=strTaxCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrTaxCode(){
		return strTaxCode;
	}
	public void setStrTaxCode(String strTaxCode){
		this. strTaxCode = strTaxCode;
	}
	public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	


//HashCode and Equals Funtions


}
