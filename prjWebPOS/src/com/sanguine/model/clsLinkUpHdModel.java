package com.sanguine.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Entity
@Table(name="tbllinkup")
@IdClass(clsLinkUpModel_ID.class)

public class clsLinkUpHdModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsLinkUpHdModel(){}

	public clsLinkUpHdModel(clsLinkUpModel_ID objModelID){
		strSGCode = objModelID.getStrSGCode();
		strClientCode = objModelID.getStrClientCode();
		strPropertyCode = objModelID.getStrPropertyCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strSGCode",column=@Column(name="strSGCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strPropertyCode",column=@Column(name="strPropertyCode"))
	})

//Variable Declaration
	@Column(name="strSGCode")
	private String strSGCode;

	@Column(name="strGDes")
	private String strGDes;

	@Column(name="strSGName")
	private String strSGName;

	@Column(name="strAccountCode")
	private String strAccountCode;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteCreatedDate")
	private String dteCreatedDate;

	@Column(name="dteLastModified")
	private String dteLastModified;

	@Column(name="strClientCode")
	private String strClientCode;
	
	@Column(name="strExSuppCode")
	private String strExSuppCode;
	
	@Column(name="strExSuppName")
	private String strExSuppName;
	
	@Column(name="strPropertyCode")
	private String strPropertyCode;
	
	
	
	

//Setter-Getter Methods
	public String getStrSGCode(){
		return strSGCode;
	}
	public void setStrSGCode(String strSGCode){
		this. strSGCode = (String) setDefaultValue( strSGCode, "NA");
	}

	public String getStrGDes(){
		return strGDes;
	}
	public void setStrGDes(String strGDes){
		this. strGDes = (String) setDefaultValue( strGDes, "");
	}

	public String getStrSGName(){
		return strSGName;
	}
	public void setStrSGName(String strSGName){
		this. strSGName = (String) setDefaultValue( strSGName, "NA");
	}

	public String getStrAccountCode(){
		return strAccountCode;
	}
	public void setStrAccountCode(String strAccountCode){
		this. strAccountCode = (String) setDefaultValue( strAccountCode, "");
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

	public String getDteCreatedDate(){
		return dteCreatedDate;
	}
	public void setDteCreatedDate(String dteCreatedDate){
		this.dteCreatedDate=dteCreatedDate;
	}

	public String getDteLastModified(){
		return dteLastModified;
	}
	public void setDteLastModified(String dteLastModified){
		this.dteLastModified=dteLastModified;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
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

	public String getStrExSuppCode() {
		return strExSuppCode;
	}

	public void setStrExSuppCode(String strExSuppCode) {
		this.strExSuppCode = strExSuppCode;
	}

	public String getStrExSuppName() {
		return strExSuppName;
	}

	public void setStrExSuppName(String strExSuppName) {
		this.strExSuppName = strExSuppName;
	}

	public String getStrPropertyCode() {
		return strPropertyCode;
	}

	public void setStrPropertyCode(String strPropertyCode) {
		this.strPropertyCode = strPropertyCode;
	}

}
