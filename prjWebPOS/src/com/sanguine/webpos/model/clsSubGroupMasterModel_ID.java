package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsSubGroupMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strSubGroupCode")
	private String strSubGroupCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsSubGroupMasterModel_ID(){}
	public clsSubGroupMasterModel_ID(String strSubGroupCode,String strClientCode){
		this.strSubGroupCode=strSubGroupCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrSubGroupCode(){
		return strSubGroupCode;
	}
	public void setStrSubGroupCode(String strSubGroupCode){
		this. strSubGroupCode = strSubGroupCode;
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
		clsSubGroupMasterModel_ID objModelId = (clsSubGroupMasterModel_ID)obj;
		if(this.strSubGroupCode.equals(objModelId.getStrSubGroupCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strSubGroupCode.hashCode()+this.strClientCode.hashCode();
	}

}
