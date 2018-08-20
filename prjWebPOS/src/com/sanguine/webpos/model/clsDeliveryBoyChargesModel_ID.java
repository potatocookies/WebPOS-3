package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsDeliveryBoyChargesModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCustAreaCode")
	private String strCustAreaCode;

	@Column(name="strDeliveryBoyCode")
	private String strDeliveryBoyCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsDeliveryBoyChargesModel_ID(){}
	public clsDeliveryBoyChargesModel_ID(String strCustAreaCode,String strDeliveryBoyCode,String strClientCode){
		this.strCustAreaCode=strCustAreaCode;
		this.strDeliveryBoyCode=strDeliveryBoyCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrCustAreaCode(){
		return strCustAreaCode;
	}
	public void setStrCustAreaCode(String strCustAreaCode){
		this. strCustAreaCode = strCustAreaCode;
	}

	public String getStrDeliveryBoyCode(){
		return strDeliveryBoyCode;
	}
	public void setStrDeliveryBoyCode(String strDeliveryBoyCode){
		this. strDeliveryBoyCode = strDeliveryBoyCode;
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
		clsDeliveryBoyChargesModel_ID objModelId = (clsDeliveryBoyChargesModel_ID)obj;
		if(this.strCustAreaCode.equals(objModelId.getStrCustAreaCode())&& this.strDeliveryBoyCode.equals(objModelId.getStrDeliveryBoyCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCustAreaCode.hashCode()+this.strDeliveryBoyCode.hashCode()+this.strClientCode.hashCode();
	}

}
