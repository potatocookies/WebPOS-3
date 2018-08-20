package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsItemModifierMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strModifierCode")
	private String strModifierCode;

	public clsItemModifierMasterModel_ID(){}
	public clsItemModifierMasterModel_ID(String strItemCode,String strModifierCode){
		this.strItemCode=strItemCode;
		this.strModifierCode=strModifierCode;
	}

//Setter-Getter Methods
	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = strItemCode;
	}

	public String getStrModifierCode(){
		return strModifierCode;
	}
	public void setStrModifierCode(String strModifierCode){
		this. strModifierCode = strModifierCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsItemModifierMasterModel_ID objModelId = (clsItemModifierMasterModel_ID)obj;
		if(this.strItemCode.equals(objModelId.getStrItemCode())&& this.strModifierCode.equals(objModelId.getStrModifierCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strItemCode.hashCode()+this.strModifierCode.hashCode();
	}

}
