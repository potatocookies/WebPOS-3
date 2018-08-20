package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsBillSeriesModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strBillSeries")
	private String strBillSeries;


	public clsBillSeriesModel_ID(){}
	public clsBillSeriesModel_ID(String strBillSeries,String strPOSCode,String strClientCode){
		this.strPOSCode=strPOSCode;
		this.strClientCode=strClientCode;
		this.strBillSeries=strBillSeries;
	}

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = strPOSCode;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}

	public String getStrBillSeries() {
		return strBillSeries;
	}
	public void setStrBillSeries(String strBillSeries) {
		this.strBillSeries = strBillSeries;
	}
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsBillSeriesModel_ID objModelId = (clsBillSeriesModel_ID)obj;
		if(this.strPOSCode.equals(objModelId.getStrPOSCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPOSCode.hashCode()+this.strClientCode.hashCode();
	}

}
