
package com.sanguine.webpos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsTaxSettlementDetailsModel{

	
//Variable Declaration


	
	private String strSettlementCode;
	
	private String strSettlementName;
	
	private String strApplicable;

	private String dteFrom;

	private String dteTo;
	
	private String strUserCreated;
	
	private String strUserEdited;

	private String dteDateCreated;
	
	private String dteDateEdited;
	

	public String getStrSettlementCode() {
		return strSettlementCode;
	}
	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}
	public String getStrSettlementName() {
		return strSettlementName;
	}
	public void setStrSettlementName(String strSettlementName) {
		this.strSettlementName = strSettlementName;
	}
	public String getStrApplicable() {
		return strApplicable;
	}
	public void setStrApplicable(String strApplicable) {
		this.strApplicable = strApplicable;
	}
	public String getDteFrom() {
		return dteFrom;
	}
	public void setDteFrom(String dteFrom) {
		this.dteFrom = dteFrom;
	}
	public String getDteTo() {
		return dteTo;
	}
	public void setDteTo(String dteTo) {
		this.dteTo = dteTo;
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
	
	
	
}

	