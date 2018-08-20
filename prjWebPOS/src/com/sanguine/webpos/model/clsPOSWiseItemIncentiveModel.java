package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblposwiseitemwiseincentives")
@NamedQueries({ 
		
		@NamedQuery(name = "getDeletedData", query = "delete clsPOSWiseItemIncentiveModel where strPOSCode=:code and strClientCode=:clientCode"),
	
		@NamedQuery(name = "getTruncateData", query = "delete clsPricingMasterHdModel")
		
//	@NamedQuery(name = "updatetable", query = "update clsmasteroperationstatusModel set dteDateEdited=:dteDateEdited where strTableName=:TableName and strClientCode=:clientCode")
})
@IdClass(clsPOSWiseItemIncentiveModel_ID.class)

public class clsPOSWiseItemIncentiveModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSWiseItemIncentiveModel(){}

	public clsPOSWiseItemIncentiveModel(clsPOSWiseItemIncentiveModel_ID objModelID){
		strPOSCode = objModelID.getStrPOSCode();
		strItemCode = objModelID.getStrItemCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPOSCode",column=@Column(name="strPOSCode")),
@AttributeOverride(name="strItemCode",column=@Column(name="strItemCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strItemName")
	private String strItemName;

	@Column(name="strIncentiveType")
	private String strIncentiveType;

	@Column(name="dblIncentiveValue")
	private double dblIncentiveValue;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public String getStrItemName(){
		return strItemName;
	}
	public void setStrItemName(String strItemName){
		this. strItemName = (String) setDefaultValue( strItemName, "NA");
	}

	public String getStrIncentiveType(){
		return strIncentiveType;
	}
	public void setStrIncentiveType(String strIncentiveType){
		this. strIncentiveType = (String) setDefaultValue( strIncentiveType, "NA");
	}

	public double getDblIncentiveValue(){
		return dblIncentiveValue;
	}
	public void setDblIncentiveValue(double dblIncentiveValue){
		this. dblIncentiveValue = (Double) setDefaultValue( dblIncentiveValue, "0.0000");
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




}
