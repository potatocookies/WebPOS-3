package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsDayEndProcessModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="dtePOSDate")
	private String dtePOSDate;

	@Column(name="intShiftCode")
	private long intShiftCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsDayEndProcessModel_ID(){}
	public clsDayEndProcessModel_ID(String strPOSCode,String dtePOSDate,long intShiftCode,String strClientCode){
		this.strPOSCode=strPOSCode;
		this.dtePOSDate=dtePOSDate;
		this.intShiftCode=intShiftCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = strPOSCode;
	}

	public String getDtePOSDate(){
		return dtePOSDate;
	}
	public void setDtePOSDate(String dtePOSDate){
		this.dtePOSDate=dtePOSDate;
	}

	public long getIntShiftCode(){
		return intShiftCode;
	}
	public void setIntShiftCode(long intShiftCode){
		this. intShiftCode = intShiftCode;
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
		clsDayEndProcessModel_ID objModelId = (clsDayEndProcessModel_ID)obj;
		if(this.strPOSCode.equals(objModelId.getStrPOSCode())&& this.dtePOSDate.equals(objModelId.getDtePOSDate())&& this.intShiftCode==objModelId.getIntShiftCode()&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPOSCode.hashCode()+this.dtePOSDate.hashCode()+String.valueOf(this.intShiftCode).hashCode()+this.strClientCode.hashCode();
	}

}
