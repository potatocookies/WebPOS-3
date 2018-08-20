package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsShiftMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="intShiftCode")
	private String intShiftCode;
	
	@Column(name="strClientCode")
	private String strClientCode;


	public clsShiftMasterModel_ID(){}
	public clsShiftMasterModel_ID(String intShiftCode, String strClientCode){
		this.intShiftCode=intShiftCode;
		this.strClientCode=strClientCode;

	}

//Setter-Getter Methods
	public String getIntShiftCode(){
		return intShiftCode;
	}
	public void setIntShiftCode(String intShiftCode){
		this. intShiftCode = intShiftCode;
	}
	

	 

public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsShiftMasterModel_ID objModelId = (clsShiftMasterModel_ID)obj;
		if(this.intShiftCode==(objModelId.getIntShiftCode()) && this.strClientCode==(objModelId.getStrClientCode()))
				{
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		
		return this.intShiftCode.hashCode();
	}

}
