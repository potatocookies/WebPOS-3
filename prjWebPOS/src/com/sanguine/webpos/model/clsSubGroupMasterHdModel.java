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
@Table(name="tblsubgrouphd")
@NamedQueries({ 
	@NamedQuery (name="POSSubGroupMaster",query="select strSubGroupCode,strSubGroupName,strGroupCode ,strIncentives from clsSubGroupMasterHdModel "
			+ " where strClientCode=:clientCode"),
	@NamedQuery (name="getSubGroupMaster",query="from clsSubGroupMasterHdModel where strSubGroupCode=:subGroupCode and strClientCode=:clientCode" ) })
@IdClass(clsSubGroupMasterModel_ID.class)

public class clsSubGroupMasterHdModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsSubGroupMasterHdModel(){}

	public clsSubGroupMasterHdModel(clsSubGroupMasterModel_ID objModelID){
		strSubGroupCode = objModelID.getStrSubGroupCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strSubGroupCode",column=@Column(name="strSubGroupCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strSubGroupCode")
	private String strSubGroupCode;

	@Column(name="strSubGroupName")
	private String strSubGroupName;

	@Column(name="strGroupCode")
	private String strGroupCode;

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

	@Column(name="strIncentives")
	private String strIncentives;

	@Column(name="strAccountCode")
	private String strAccountCode;

	@Column(name="strFactoryCode")
	private String strFactoryCode;

//Setter-Getter Methods
	public String getStrSubGroupCode(){
		return strSubGroupCode;
	}
	public void setStrSubGroupCode(String strSubGroupCode){
		this. strSubGroupCode = (String) setDefaultValue( strSubGroupCode, "NA");
	}

	public String getStrSubGroupName(){
		return strSubGroupName;
	}
	public void setStrSubGroupName(String strSubGroupName){
		this. strSubGroupName = (String) setDefaultValue( strSubGroupName, "NA");
	}

	public String getStrGroupCode(){
		return strGroupCode;
	}
	public void setStrGroupCode(String strGroupCode){
		this. strGroupCode = (String) setDefaultValue( strGroupCode, "NA");
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

	public String getStrIncentives(){
		return strIncentives;
	}
	public void setStrIncentives(String strIncentives){
		this. strIncentives = (String) setDefaultValue( strIncentives, "NA");
	}

	public String getStrAccountCode(){
		return strAccountCode;
	}
	public void setStrAccountCode(String strAccountCode){
		this. strAccountCode = (String) setDefaultValue( strAccountCode, "NA");
	}

	public String getStrFactoryCode(){
		return strFactoryCode;
	}
	public void setStrFactoryCode(String strFactoryCode){
		this. strFactoryCode = (String) setDefaultValue( strFactoryCode, "NA");
	}




}
