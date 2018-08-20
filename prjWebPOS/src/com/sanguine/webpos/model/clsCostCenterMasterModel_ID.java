package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsCostCenterMasterModel_ID implements Serializable
{

//Variable Declaration
	@Column(name = "strCostCenterCode")
	private String	strCostCenterCode;

	@Column(name = "strClientCode")
	private String	strClientCode;

	public clsCostCenterMasterModel_ID()
	{
	}

	public clsCostCenterMasterModel_ID(String strCostCenterCode, String strClientCode)
	{
		this.strCostCenterCode = strCostCenterCode;
		this.strClientCode = strClientCode;
	}

//Setter-Getter Methods
	public String getStrCostCenterCode()
	{
		return strCostCenterCode;
	}

	public void setStrCostCenterCode(String strCostCenterCode)
	{
		this.strCostCenterCode = strCostCenterCode;
	}

	public String getStrClientCode()
	{
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = strClientCode;
	}

//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj)
	{
		clsCostCenterMasterModel_ID objModelId = (clsCostCenterMasterModel_ID) obj;
		if (this.strCostCenterCode.equals(objModelId.getStrCostCenterCode()) && this.strClientCode.equals(objModelId.getStrClientCode()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return this.strCostCenterCode.hashCode() + this.strClientCode.hashCode();
	}

}
