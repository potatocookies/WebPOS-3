package com.sanguine.webpos.model;

import javax.persistence.Embeddable;

@Embeddable
public class clsItemModifierMasterModel{
	
	private String strItemCode;

	private String strChargable;

	private double dblRate;

	private String strApplicable;

	private String strDefaultModifier;

//Setter-Getter Methods
	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public String getStrChargable(){
		return strChargable;
	}
	public void setStrChargable(String strChargable){
		this. strChargable = (String) setDefaultValue( strChargable, "");
	}

	public double getDblRate(){
		return dblRate;
	}
	public void setDblRate(double dblRate){
		this. dblRate = (Double) setDefaultValue( dblRate, "0.0000");
	}

	public String getStrApplicable(){
		return strApplicable;
	}
	public void setStrApplicable(String strApplicable){
		this. strApplicable = (String) setDefaultValue( strApplicable, "");
	}

	public String getStrDefaultModifier(){
		return strDefaultModifier;
	}
	public void setStrDefaultModifier(String strDefaultModifier){
		this. strDefaultModifier = (String) setDefaultValue( strDefaultModifier, "");
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
