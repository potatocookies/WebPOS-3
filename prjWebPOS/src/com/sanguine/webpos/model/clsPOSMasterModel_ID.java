package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsPOSMasterModel_ID implements Serializable
{

	// Variable Declaration
	@Column(name = "strPosCode")
	private String strPosCode;

	@Column(name = "strClientCode")
	private String strClientCode;
	
	public clsPOSMasterModel_ID()
	{
	}

	public clsPOSMasterModel_ID(String strPosCode,String strClientCode)
	{
		this.strPosCode = strPosCode;
		this.strClientCode = strClientCode;
	}

	// Setter-Getter Methods
	public String getStrPosCode()
	{
		return strPosCode;
	}

	public void setStrPosCode(String strPosCode)
	{
		this.strPosCode = strPosCode;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	// HashCode and Equals Funtions
	
}
