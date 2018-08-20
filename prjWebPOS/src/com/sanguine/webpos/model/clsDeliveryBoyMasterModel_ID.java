package com.sanguine.webpos.model;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsDeliveryBoyMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strDPCode")
	private String strDPCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsDeliveryBoyMasterModel_ID(){}
	public clsDeliveryBoyMasterModel_ID(String strDPCode,String strClientCode){
		this.strDPCode=strDPCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrDPCode(){
		return strDPCode;
	}
	public void setStrDPCode(String strDPCode){
		this. strDPCode = strDPCode;
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
		clsDeliveryBoyMasterModel_ID objModelId = (clsDeliveryBoyMasterModel_ID)obj;
		if(this.strDPCode.equals(objModelId.getStrDPCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strDPCode.hashCode()+this.strClientCode.hashCode();
	}

}
