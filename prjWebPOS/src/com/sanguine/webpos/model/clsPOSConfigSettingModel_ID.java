package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSConfigSettingModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strClientCode")
	private String strClientCode;

	public clsPOSConfigSettingModel_ID(){}
	public clsPOSConfigSettingModel_ID(String strClientCode){
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSConfigSettingModel_ID objModelId = (clsPOSConfigSettingModel_ID)obj;
		if(this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strClientCode.hashCode();
	}

}
