package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsCustomerTypeMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCustTypeCode")
	private String strCustTypeCode;
	
	  @Column(name = "strClientCode")
	    private String strClientCode;
	  public clsCustomerTypeMasterModel_ID()
	    {
	    }  


	public clsCustomerTypeMasterModel_ID(String strCustTypeCode, String strClientCode)
	{
		this.strCustTypeCode=strCustTypeCode;
		this.strClientCode = strClientCode;
	}

//Setter-Getter Methods
	public String getStrCustTypeCode(){
		return strCustTypeCode;
	}
	public void setStrCustTypeCode(String strCustTypeCode){
		this. strCustTypeCode = strCustTypeCode;
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
	public boolean equals(Object obj) {
		clsCustomerTypeMasterModel_ID objModelId = (clsCustomerTypeMasterModel_ID)obj;
		if(this.strCustTypeCode.equals(objModelId.getStrCustTypeCode()) && this.strClientCode.equals(objModelId.getStrClientCode()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCustTypeCode.hashCode()+ this.strClientCode.hashCode();
	}

}
