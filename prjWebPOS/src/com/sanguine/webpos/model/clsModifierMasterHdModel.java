package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblmodifiermaster")
@NamedQueries({ 
	@NamedQuery (name="POSItemModifierMaster", query="select m.strModifierCode,m.strModifierName,m.strModifierDesc "
		+ "from clsModifierMasterHdModel m where m.strClientCode=:clientCode" ),
	@NamedQuery (name="getModifierMaster",query="from clsModifierMasterHdModel where strModifierCode =:modCode and strClientCode=:clientCode")})

@IdClass(clsModifierMasterModel_ID.class)
public class clsModifierMasterHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsModifierMasterHdModel(){}

	public clsModifierMasterHdModel(clsModifierMasterModel_ID objModelID){
		strModifierCode = objModelID.getStrModifierCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblitemmodofier" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strModifierCode")})
	@Id	
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strModifierCode",column=@Column(name="strModifierCode"))
		
	})
	
	Set<clsItemModifierMasterModel> setItemModifierDtl = new HashSet<clsItemModifierMasterModel>();
		


	public Set<clsItemModifierMasterModel> getSetItemModifierDtl() {
		return setItemModifierDtl;
	}

	public void setSetItemModifierDtl(
			Set<clsItemModifierMasterModel> setItemModifierDtl) {
		this.setItemModifierDtl = setItemModifierDtl;
	}

	//Variable Declaration
	@Column(name="strModifierCode")
	private String strModifierCode;

	@Column(name="strModifierName")
	private String strModifierName;

	@Column(name="strModifierDesc")
	private String strModifierDesc;

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

	@Column(name="strModifierGroupCode")
	private String strModifierGroupCode;

//Setter-Getter Methods
	public String getStrModifierCode(){
		return strModifierCode;
	}
	public void setStrModifierCode(String strModifierCode){
		this. strModifierCode = (String) setDefaultValue( strModifierCode, "NA");
	}

	public String getStrModifierName(){
		return strModifierName;
	}
	public void setStrModifierName(String strModifierName){
		this. strModifierName = (String) setDefaultValue( strModifierName, "NA");
	}

	public String getStrModifierDesc(){
		return strModifierDesc;
	}
	public void setStrModifierDesc(String strModifierDesc){
		this. strModifierDesc = (String) setDefaultValue( strModifierDesc, "NA");
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

	public String getStrModifierGroupCode(){
		return strModifierGroupCode;
	}
	public void setStrModifierGroupCode(String strModifierGroupCode){
		this. strModifierGroupCode = (String) setDefaultValue( strModifierGroupCode, "NA");
	}



}
	