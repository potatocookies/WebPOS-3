package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsUserHDModel_ID implements Serializable{

//Variable Declaration
	private String strUserCode;
	
	private String strClientCode;

	public clsUserHDModel_ID(){}
	public clsUserHDModel_ID(String strUserCode,String strClientCode)
	{
		this.strUserCode=strUserCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrUserCode() 
	{
		return strUserCode;
	}
	
	
	public void setStrUserCode(String strUserCode) 
	{
		this.strUserCode = strUserCode;
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
	public int hashCode() {
		return this.strUserCode.hashCode();
	}

}
