package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;



@Entity
@Table(name="tbluserhd")

@NamedQueries(
		{@NamedQuery (name="getAllUserMaster" , query="select m.strUserCode,m.strDebitCardString,m.strSuperType from clsUserHdModel m "
				+ "where m.strClientCode=:clientCode" ),

		@NamedQuery(name = "getUserMaster", query = "from clsUserHdModel where strUserCode=:userCode and strClientCode=:clientCode")}
	)
@IdClass(clsUserHDModel_ID.class)

public class clsUserHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsUserHdModel(){}

	public clsUserHdModel(clsUserHDModel_ID objModelID){
		strUserCode = objModelID.getStrUserCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strUserCode",column=@Column(name="strUserCode"))
		,@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strUserCode")
	private String strUserCode;

	@Column(name="strUserName")
	private String strUserName;

	@Column(name="strPassword")
	private String strPassword;

	@Column(name="strSuperType")
	private String strSuperType;

	@Column(name="dteValidDate")
	private String dteValidDate;

	@Column(name="strPOSAccess")
	private String strPOSAccess;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strImgUserIconPath")
	private String strImgUserIconPath;

	@Column(name="strDebitCardString")
	private String strDebitCardString;

//Setter-Getter Methods
	public String getStrUserCode(){
		return strUserCode;
	}
	public void setStrUserCode(String strUserCode){
		this. strUserCode = (String) setDefaultValue( strUserCode, "NA");
	}

	public String getStrUserName(){
		return strUserName;
	}
	public void setStrUserName(String strUserName){
		this. strUserName = (String) setDefaultValue( strUserName, "NA");
	}

	public String getStrPassword(){
		return strPassword;
	}
	public void setStrPassword(String strPassword){
		this. strPassword = (String) setDefaultValue( strPassword, "NA");
	}

	public String getStrSuperType(){
		return strSuperType;
	}
	public void setStrSuperType(String strSuperType){
		this. strSuperType = (String) setDefaultValue( strSuperType, "NA");
	}

	public String getDteValidDate(){
		return dteValidDate;
	}
	public void setDteValidDate(String dteValidDate){
		this.dteValidDate=dteValidDate;
	}

	public String getStrPOSAccess(){
		return strPOSAccess;
	}
	public void setStrPOSAccess(String strPOSAccess){
		this. strPOSAccess = (String) setDefaultValue( strPOSAccess, "NA");
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

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrImgUserIconPath(){
		return strImgUserIconPath;
	}
	public void setStrImgUserIconPath(String strImgUserIconPath){
		this. strImgUserIconPath = (String) setDefaultValue( strImgUserIconPath, "NA");
	}

	public String getStrDebitCardString(){
		return strDebitCardString;
	}
	public void setStrDebitCardString(String strDebitCardString){
		this. strDebitCardString = (String) setDefaultValue( strDebitCardString, "NA");
	}


}
