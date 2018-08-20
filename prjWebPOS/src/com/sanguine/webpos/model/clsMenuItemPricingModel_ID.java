package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsMenuItemPricingModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	public clsMenuItemPricingModel_ID(){}
	public clsMenuItemPricingModel_ID(String strItemCode){
		this.strItemCode=strItemCode;
	}

//Setter-Getter Methods
	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = strItemCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsMenuItemPricingModel_ID objModelId = (clsMenuItemPricingModel_ID)obj;
		if(this.strItemCode.equals(objModelId.getStrItemCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strItemCode.hashCode();
	}

}
