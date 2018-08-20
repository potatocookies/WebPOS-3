package com.sanguine.webpos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsPosSettlementDetailsModel {



//Variable Declaration



	private String strSettlementCode;

	private String strSettlementDesc;

	
	private String strDataPostFlag;
	
	


	public String getStrSettlementCode() {
		return strSettlementCode;
	}

	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}

	public String getStrSettlementDesc() {
		return strSettlementDesc;
	}

	public void setStrSettlementDesc(String strSettlementDesc) {
		this.strSettlementDesc = strSettlementDesc;
	}

	

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}
	
}

	