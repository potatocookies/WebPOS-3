package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsCustomerAreaMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strBuildingCode")
	private String strBuildingCode;
	
	@Column(name = "strClientCode")
    private String strClientCode;

	public clsCustomerAreaMasterModel_ID(){}
	public clsCustomerAreaMasterModel_ID(String strBuildingCode, String strClientCode){
		this.strBuildingCode=strBuildingCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrBuildingCode(){
		return strBuildingCode;
	}
	public void setStrBuildingCode(String strBuildingCode){
		this. strBuildingCode = strBuildingCode;
	
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
		clsCustomerAreaMasterModel_ID objModelId = (clsCustomerAreaMasterModel_ID)obj;
		if(this.strBuildingCode.equals(objModelId.getStrBuildingCode()) && this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strBuildingCode.hashCode()+ this.strClientCode.hashCode();
	}

}
