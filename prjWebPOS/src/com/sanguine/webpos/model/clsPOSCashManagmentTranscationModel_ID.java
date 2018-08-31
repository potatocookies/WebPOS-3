package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSCashManagmentTranscationModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTransID")
	private String strTransID;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsPOSCashManagmentTranscationModel_ID(){}
	public clsPOSCashManagmentTranscationModel_ID(String strTransID,String strClientCode){
		this.strTransID=strTransID;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrTransID(){
		return strTransID;
	}
	public void setStrTransID(String strTransID){
		this. strTransID = strTransID;
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
		clsPOSCashManagmentTranscationModel_ID objModelId = (clsPOSCashManagmentTranscationModel_ID)obj;
		if(this.strTransID.equals(objModelId.getStrTransID())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strTransID.hashCode()+this.strClientCode.hashCode();
	}

}
