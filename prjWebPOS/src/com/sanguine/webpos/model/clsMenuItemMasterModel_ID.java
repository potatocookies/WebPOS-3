package com.sanguine.webpos.model;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
@SuppressWarnings("serial")

public class clsMenuItemMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsMenuItemMasterModel_ID(){}
	public clsMenuItemMasterModel_ID(String strItemCode,String strClientCode){
		this.strItemCode=strItemCode;
		this.strClientCode=strClientCode;
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


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsMenuItemMasterModel_ID objModelId = (clsMenuItemMasterModel_ID)obj;
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
