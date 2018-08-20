package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSWiseItemIncentiveModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsPOSWiseItemIncentiveModel_ID(){}
	public clsPOSWiseItemIncentiveModel_ID(String strPOSCode,String strItemCode,String strClientCode){
		this.strPOSCode=strPOSCode;
		this.strItemCode=strItemCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = strPOSCode;
	}

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


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSWiseItemIncentiveModel_ID objModelId = (clsPOSWiseItemIncentiveModel_ID)obj;
		if(this.strPOSCode.equals(objModelId.getStrPOSCode())&& this.strItemCode.equals(objModelId.getStrItemCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPOSCode.hashCode()+this.strItemCode.hashCode()+this.strClientCode.hashCode();
	}

}
