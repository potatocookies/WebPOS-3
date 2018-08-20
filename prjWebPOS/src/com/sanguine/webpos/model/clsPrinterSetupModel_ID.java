package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPrinterSetupModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCostCenterCode")
	private String strCostCenterCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsPrinterSetupModel_ID(){}
	public clsPrinterSetupModel_ID(String strCostCenterCode,String strClientCode){
		this.strCostCenterCode=strCostCenterCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrCostCenterCode(){
		return strCostCenterCode;
	}
	public void setStrCostCenterCode(String strCostCenterCode){
		this. strCostCenterCode = strCostCenterCode;
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
		clsPrinterSetupModel_ID objModelId = (clsPrinterSetupModel_ID)obj;
		if(this.strCostCenterCode.equals(objModelId.getStrCostCenterCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCostCenterCode.hashCode()+this.strClientCode.hashCode();
	}

}
