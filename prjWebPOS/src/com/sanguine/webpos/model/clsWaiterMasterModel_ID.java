package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")

public class clsWaiterMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strWaiterNo")
	private String strWaiterNo;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsWaiterMasterModel_ID(){}
	public clsWaiterMasterModel_ID(String strWaiterNo,String strClientCode){
		this.strWaiterNo=strWaiterNo;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrWaiterNo(){
		return strWaiterNo;
	}
	public void setStrWaiterNo(String strWaiterNo){
		this. strWaiterNo = strWaiterNo;
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
		clsWaiterMasterModel_ID objModelId = (clsWaiterMasterModel_ID)obj;
		if(this.strWaiterNo.equals(objModelId.getStrWaiterNo())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strWaiterNo.hashCode()+this.strClientCode.hashCode();
	}

}
