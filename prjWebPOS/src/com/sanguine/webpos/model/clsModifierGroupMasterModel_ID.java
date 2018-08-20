package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsModifierGroupMasterModel_ID implements Serializable
{

//Variable Declaration
	@Column(name="strModifierGroupCode")
	private String strModifierGroupCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsModifierGroupMasterModel_ID(){}
	public clsModifierGroupMasterModel_ID(String strModifierGroupCode,String strClientCode){
		this.strModifierGroupCode=strModifierGroupCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrModifierGroupCode(){
		return strModifierGroupCode;
	}
	public void setStrModifierGroupCode(String strModifierGroupCode){
		this. strModifierGroupCode = strModifierGroupCode;
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
		clsModifierGroupMasterModel_ID objModelId = (clsModifierGroupMasterModel_ID)obj;
		if(this.strModifierGroupCode.equals(objModelId.getStrModifierGroupCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strModifierGroupCode.hashCode()+this.strClientCode.hashCode();
	}

}
