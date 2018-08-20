package com.sanguine.webpos.model;

import javax.persistence.Embeddable;



@Embeddable
public class clsReorderTimeModel{
	
//Variable Declaration
	
	private String tmeFromTime;
	
	private String tmeToTime;

	private String strUserCreated;

	private String strUserEdited;

	private String dteDateCreated;

	private String dteDateEdited;

	

	private String strDataPostFlag;

//Setter-Getter Methods

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

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = strUserCreated;
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = strUserEdited;
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}

	
	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag =strDataPostFlag;
	}


//Function to Set Default Values
	
}
