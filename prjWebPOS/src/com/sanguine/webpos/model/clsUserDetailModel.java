package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Embeddable
public class clsUserDetailModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsUserDetailModel(){}

	/*public clsUserDetailHdModel(clsUserDetailModel_ID objModelID)
	{
		strUserCode = objModelID.getStrUserCode();
		strFormName = objModelID.getStrClientCode();
	}
   
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strUserCode",column=@Column(name="strUserCode"))
		
//		, @AttributeOverride(name = "strFormName", column = @Column(name = "strFormName"))
	})*/

//Variable Declaration
	@Column(name="strUserCode")
	private String strUserCode;

	@Column(name="strFormName")
	private String strFormName;

	@Column(name="strButtonName")
	private String strButtonName;

	@Column(name="intSequence")
	private long intSequence;

	@Column(name="strAdd")
	private String strAdd;

	@Column(name="strEdit")
	private String strEdit;

	@Column(name="strDelete")
	private String strDelete;

	@Column(name="strView")
	private String strView;

	@Column(name="strPrint")
	private String strPrint;

	@Column(name="strSave")
	private String strSave;

	@Column(name="strGrant")
	private String strGrant;

	@Column(name="strTLA")
	private String strTLA;

	@Column(name="strAuditing")
	private String strAuditing;
	
	/*@Column(name="strDebitCardString")
	private String strDebitCardString;
	
	 @Column(name = "strOperationalYN")
	 private String strOperationalYN;
	 
	 @Column(name = "strUserCreated")
	  private String strUserCreated;
	    
	 @Column(name = "strUserEdited")
	 private String strUserEdited;
	    
	 @Column(name = "dteDateCreated")
	 private String dteDateCreated;
	  
	 @Column(name = "dteDateEdited")
	 private String dteDateEdited;
	 
	 @Column(name = "strDataPostFlag")
	 private String strDataPostFlag;
	 

	 @Column(name = "strSuperType")
	 private String strSuperType;
	 
	 @Column(name = "strClientCode")
	 private String strClientCode;
	    */
	    
	    

//Setter-Getter Methods
	public String getStrUserCode(){
		return strUserCode;
	}
	public void setStrUserCode(String strUserCode){
		this. strUserCode = (String) setDefaultValue( strUserCode, "");
	}

	public String getStrFormName(){
		return strFormName;
	}
	public void setStrFormName(String strFormName){
		this. strFormName = (String) setDefaultValue( strFormName, "");
	}

	public String getStrButtonName(){
		return strButtonName;
	}
	public void setStrButtonName(String strButtonName){
		this. strButtonName = (String) setDefaultValue( strButtonName, "");
	}

	public long getIntSequence(){
		return intSequence;
	}
	public void setIntSequence(long intSequence){
		this. intSequence = (Long) setDefaultValue( intSequence, "0");
	}

	public String getStrAdd(){
		return strAdd;
	}
	public void setStrAdd(String strAdd){
		this. strAdd = (String) setDefaultValue( strAdd, "");
	}

	public String getStrEdit(){
		return strEdit;
	}
	public void setStrEdit(String strEdit){
		this. strEdit = (String) setDefaultValue( strEdit, "");
	}

	public String getStrDelete(){
		return strDelete;
	}
	public void setStrDelete(String strDelete){
		this. strDelete = (String) setDefaultValue( strDelete, "");
	}

	public String getStrView(){
		return strView;
	}
	public void setStrView(String strView){
		this. strView = (String) setDefaultValue( strView, "");
	}

	public String getStrPrint(){
		return strPrint;
	}
	public void setStrPrint(String strPrint){
		this. strPrint = (String) setDefaultValue( strPrint, "");
	}

	public String getStrSave(){
		return strSave;
	}
	public void setStrSave(String strSave){
		this. strSave = (String) setDefaultValue( strSave, "");
	}

	public String getStrGrant(){
		return strGrant;
	}
	public void setStrGrant(String strGrant){
		this. strGrant = (String) setDefaultValue( strGrant, "");
	}

	public String getStrTLA(){
		return strTLA;
	}
	public void setStrTLA(String strTLA){
		this. strTLA = (String) setDefaultValue( strTLA, "");
	}

	public String getStrAuditing(){
		return strAuditing;
	}
	public void setStrAuditing(String strAuditing){
		this. strAuditing = (String) setDefaultValue( strAuditing, "");
	}
	
	/*public String getStrDebitCardString(){
		return strDebitCardString;
	}
	public void setStrDebitCardString(String strDebitCardString){
		this.strDebitCardString=strDebitCardString;
	}

	
	public String getStrOperationalYN()
    {
	return strOperationalYN;
    }
    
    public void setStrOperationalYN(String strOperationalYN)
    {
	this.strOperationalYN = (String) setDefaultValue(strOperationalYN, "");
    }
    
    public String getStrUserCreated()
    {
	return strUserCreated;
    }
    
    public void setStrUserCreated(String strUserCreated)
    {
	this.strUserCreated = (String) setDefaultValue(strUserCreated, "");
    }
    
    public String getStrUserEdited()
    {
	return strUserEdited;
    }
    
    public void setStrUserEdited(String strUserEdited)
    {
	this.strUserEdited = (String) setDefaultValue(strUserEdited, "");
    }
    
    public String getDteDateCreated()
    {
	return dteDateCreated;
    }
    
    public void setDteDateCreated(String dteDateCreated)
    {
	this.dteDateCreated = dteDateCreated;
    }
    
    public String getDteDateEdited()
    {
	return dteDateEdited;
    }
    
    public void setDteDateEdited(String dteDateEdited)
    {
	this.dteDateEdited = dteDateEdited;
    }
    
    public String getStrDataPostFlag()
    {
	return strDataPostFlag;
    }
    
    public void setStrDataPostFlag(String strDataPostFlag)
    {
	this.strDataPostFlag = (String) setDefaultValue(strDataPostFlag, "");
    }
    
    
    public String getStrSuperType(){
		return strSuperType;
	}
	public void setStrSuperType(String strSuperType){
		this.strSuperType=strSuperType;
	}
    
	public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = (String) setDefaultValue(strClientCode, "");
    }
    */
    
    
    

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
