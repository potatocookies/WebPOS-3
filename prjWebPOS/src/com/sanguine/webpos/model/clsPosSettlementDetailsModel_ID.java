package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPosSettlementDetailsModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;
	
	@Column(name="strSettlementCode")
	private String strSettlementCode;
	

	public clsPosSettlementDetailsModel_ID(){}
	public clsPosSettlementDetailsModel_ID(String strPOSCode, String strSettlementCode){
		this.strPOSCode=strPOSCode;
		this.strSettlementCode=strSettlementCode;
		
	}

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPosCode){
		this. strPOSCode = strPosCode;
	}


public String getStrSettlementCode() {
		return strSettlementCode;
	}
	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}
	//HashCode and Equals Funtions
	
	@Override
	public int hashCode() {
		return this.strPOSCode.hashCode();
		
	}

}
