package com.sanguine.webpos.model;

import java.io.Serializable;

public class clsOrderHdModel_ID implements Serializable
{

	private String strOrderNo;
	private String strClientCode;

	public clsOrderHdModel_ID()
	{
	}

	public clsOrderHdModel_ID(String strOrderNo, String strClientCode)
	{
		this.strOrderNo = strOrderNo;
		this.strClientCode = strClientCode;
	}

	public String getStrOrderNo()
	{
		return strOrderNo;
	}

	public void setStrBillNo(String strOrderNo)
	{
		this.strOrderNo = strOrderNo;
	}

	public String getStrClientCode()
	{
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = strClientCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		clsOrderHdModel_ID cp = (clsOrderHdModel_ID) obj;
		if (this.strOrderNo.equals(cp.getStrOrderNo()) && this.strClientCode.equals(cp.getStrClientCode()))
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
		return this.strOrderNo.hashCode() + this.strClientCode.hashCode();

	}
}
