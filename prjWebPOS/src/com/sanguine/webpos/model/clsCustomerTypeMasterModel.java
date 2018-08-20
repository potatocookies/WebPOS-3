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
@Table(name="tblcustomertypemaster")
@NamedQueries({ 
	@NamedQuery(name = "getALLCustomerType", query = "select m.strCustTypeCode,m.strCustType,m.dblDiscPer"
		+ " from clsCustomerTypeMasterModel m where m.strClientCode=:clientCode"),
		@NamedQuery(name = "getCustomerType", query = "from clsCustomerTypeMasterModel where strCustTypeCode=:custTypeCode and strClientCode=:clientCode")
})

@IdClass(clsCustomerTypeMasterModel_ID.class)

public class clsCustomerTypeMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsCustomerTypeMasterModel(){}

	public clsCustomerTypeMasterModel(clsCustomerTypeMasterModel_ID objModelID){
		strCustTypeCode = objModelID.getStrCustTypeCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strCustTypeCode",column=@Column(name="strCustTypeCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
	})

//Variable Declaration
	@Column(name="strCustTypeCode")
	private String strCustTypeCode;

	@Column(name="strCustType")
	private String strCustType;

	@Column(name="dblDiscPer")
	private double dblDiscPer;

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
	
	@Column(name="strPlayZoneCustType")
	private String strPlayZoneCustType;

//Setter-Getter Methods
	public String getStrCustTypeCode(){
		return strCustTypeCode;
	}
	public void setStrCustTypeCode(String strCustTypeCode){
		this. strCustTypeCode = (String) setDefaultValue( strCustTypeCode, "NA");
	}

	public String getStrCustType(){
		return strCustType;
	}
	public void setStrCustType(String strCustType){
		this. strCustType = (String) setDefaultValue( strCustType, "NA");
	}

	public double getDblDiscPer(){
		return dblDiscPer;
	}
	public void setDblDiscPer(Double discount){
		this. dblDiscPer = (Double) setDefaultValue( discount, "0.0000");
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
	


   public String getStrPlayZoneCustType() {
		return strPlayZoneCustType;
	}

	public void setStrPlayZoneCustType(String strPlayZoneCustType) {
		this.strPlayZoneCustType = strPlayZoneCustType;
	}



}
