package com.sanguine.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsLinkUpModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strSGCode")
	private String strSGCode;

	@Column(name="strClientCode")
	private String strClientCode;
	
	@Column(name="strPropertyCode")
	private String strPropertyCode;
	
	

	public clsLinkUpModel_ID(){}
	public clsLinkUpModel_ID(String strSGCode,String strClientCode,String strPropertyCode){
		this.strSGCode=strSGCode;
		this.strClientCode=strClientCode;
		this.strPropertyCode=strPropertyCode;
	}

//Setter-Getter Methods
	public String getStrSGCode(){
		return strSGCode;
	}
	public void setStrSGCode(String strSGCode){
		this. strSGCode = strSGCode;
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
		clsLinkUpModel_ID objModelId = (clsLinkUpModel_ID)obj;
		if(this.strSGCode.equals(objModelId.getStrSGCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strSGCode.hashCode()+this.strClientCode.hashCode();
	}
	public String getStrPropertyCode() {
		return strPropertyCode;
	}
	public void setStrPropertyCode(String strPropertyCode) {
		this.strPropertyCode = strPropertyCode;
	}

}
