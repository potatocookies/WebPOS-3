package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
@SuppressWarnings("serial")

public class clsReorderTimeModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;
	
	@Column(name="tmeFromTime")
	private String tmeFromTime;
	
	@Column(name="tmeToTime")
	private String tmeToTime;
	

	public clsReorderTimeModel_ID(){}
	public clsReorderTimeModel_ID(String tmeFromTime, String tmeToTime, String strPOSCode){
		this.tmeFromTime=tmeFromTime;
		this.tmeToTime=tmeToTime;
		this.strPOSCode=strPOSCode;
	}

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = strPOSCode;
	}


public String getTmeFromTime() {
		return tmeFromTime;
	}
	public void setTmeFromTime(String tmeFromTime) {
		this.tmeFromTime = tmeFromTime;
	}
	public String getTmeToTime() {
		return tmeToTime;
	}
	public void setTmeToTime(String tmeToTime) {
		this.tmeToTime = tmeToTime;
	}
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsReorderTimeModel_ID objModelId = (clsReorderTimeModel_ID)obj;
		if(this.tmeFromTime.equals(objModelId.getTmeFromTime())&& this.tmeToTime.equals(objModelId.getTmeToTime())&& this.strPOSCode.equals(objModelId.getStrPOSCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.tmeFromTime.hashCode()+this.tmeToTime.hashCode()+this.strPOSCode.hashCode();
	}

}
