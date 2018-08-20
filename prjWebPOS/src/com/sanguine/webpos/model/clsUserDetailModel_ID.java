package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsUserDetailModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strUserCode")
	private String strUserCode;
	
	@Column(name="strFormName")
	private String strFormName;

	public clsUserDetailModel_ID(){}
	public clsUserDetailModel_ID(String strUserCode,String strFormName)
	{
		this.strUserCode=strUserCode;
		this.strFormName=strFormName;
	}

//Setter-Getter Methods
	public String getStrUserCode() 
	{
		return strUserCode;
	}
	public String getStrFormName() 
	{
		return strFormName;
	}
	
	public void setStrUserCode(String strUserCode) 
	{
		this.strUserCode = strUserCode;
	}
	public void setStrFormName(String strFormName) 
	{
		this.strFormName = strFormName;
	}
	
	
	
	@Override
	public int hashCode() {
		return this.strUserCode.hashCode();
	}

}
