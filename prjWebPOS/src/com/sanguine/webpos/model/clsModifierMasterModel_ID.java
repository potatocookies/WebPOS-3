package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsModifierMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strModifierCode")
	private String strModifierCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsModifierMasterModel_ID(){}
	public clsModifierMasterModel_ID(String strModifierCode,String strClientCode){
		this.strModifierCode=strModifierCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrModifierCode(){
		return strModifierCode;
	}
	public void setStrModifierCode(String strModifierCode){
		this. strModifierCode = strModifierCode;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsModifierMasterModel_ID objModelId = (clsModifierMasterModel_ID)obj;
		if(this.strModifierCode.equals(objModelId.getStrModifierCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strModifierCode.hashCode()+this.strClientCode.hashCode();
	}

}
