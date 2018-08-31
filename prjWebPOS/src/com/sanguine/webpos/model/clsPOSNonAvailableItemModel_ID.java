package com.sanguine.webpos.model;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSNonAvailableItemModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strClientCode")
	private String strClientCode;
	
	@Column(name="strPOSCode")
	private String strPOSCode;

	public clsPOSNonAvailableItemModel_ID(){}
	public clsPOSNonAvailableItemModel_ID(String strItemCode,String strClientCode,String strPOSCode){
		this.strItemCode=strItemCode;
		this.strClientCode=strClientCode;
		this.strPOSCode=strPOSCode;
	}

//Setter-Getter Methods
	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = strItemCode;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}


public String getStrPOSCode() {
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSNonAvailableItemModel_ID objModelId = (clsPOSNonAvailableItemModel_ID)obj;
		if(this.strItemCode.equals(objModelId.getStrItemCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strItemCode.hashCode()+this.strClientCode.hashCode();
	}

}
