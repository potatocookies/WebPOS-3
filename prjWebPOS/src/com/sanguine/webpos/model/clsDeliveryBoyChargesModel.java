package com.sanguine.webpos.model;



import javax.persistence.Embeddable;


@Embeddable
public class clsDeliveryBoyChargesModel {

//Variable Declaration
	
	private String strCustAreaCode;

	private double dblValue;

	private String strUserCreated;

	private String strUserEdited;

	
	private String dteDateCreated;

	
	private String dteDateEdited;


	private String strDataPostFlag;

//Setter-Getter Methods
	public String getStrCustAreaCode(){
		return strCustAreaCode;
	}
	public void setStrCustAreaCode(String strCustAreaCode){
		this. strCustAreaCode = (String) setDefaultValue( strCustAreaCode, "NA");
	}

	
	public double getDblValue(){
		return dblValue;
	}
	public void setDblValue(double dblValue){
		this. dblValue = (Double) setDefaultValue( dblValue, "0.0000");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
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
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}


//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}

}
