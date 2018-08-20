package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;



@Entity
@Table(name="tbluserdtl")

 
@NamedQuery (name="getUserAccess",query="from clsUserDetailHdModel where strUserCode=:userCode" )
@IdClass(clsUserDetailModel_ID.class)
public class clsUserDetailHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsUserDetailHdModel(){}

	public clsUserDetailHdModel(clsUserDetailModel_ID objModelID)
	{
		strUserCode = objModelID.getStrUserCode();
		strFormName = objModelID.getStrFormName();
	}
   
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strUserCode",column=@Column(name="strUserCode")), @AttributeOverride(name = "strFormName", column = @Column(name = "strFormName"))
	})

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

//Setter-Getter Methods
	public String getStrUserCode(){
		return strUserCode;
	}
	public void setStrUserCode(String strUserCode){
		this. strUserCode = (String) setDefaultValue( strUserCode, "NA");
	}

	public String getStrFormName(){
		return strFormName;
	}
	public void setStrFormName(String strFormName){
		this. strFormName = (String) setDefaultValue( strFormName, "NA");
	}

	public String getStrButtonName(){
		return strButtonName;
	}
	public void setStrButtonName(String strButtonName){
		this. strButtonName = (String) setDefaultValue( strButtonName, "NA");
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
		this. strAdd = (String) setDefaultValue( strAdd, "NA");
	}

	public String getStrEdit(){
		return strEdit;
	}
	public void setStrEdit(String strEdit){
		this. strEdit = (String) setDefaultValue( strEdit, "NA");
	}

	public String getStrDelete(){
		return strDelete;
	}
	public void setStrDelete(String strDelete){
		this. strDelete = (String) setDefaultValue( strDelete, "NA");
	}

	public String getStrView(){
		return strView;
	}
	public void setStrView(String strView){
		this. strView = (String) setDefaultValue( strView, "NA");
	}

	public String getStrPrint(){
		return strPrint;
	}
	public void setStrPrint(String strPrint){
		this. strPrint = (String) setDefaultValue( strPrint, "NA");
	}

	public String getStrSave(){
		return strSave;
	}
	public void setStrSave(String strSave){
		this. strSave = (String) setDefaultValue( strSave, "NA");
	}

	public String getStrGrant(){
		return strGrant;
	}
	public void setStrGrant(String strGrant){
		this. strGrant = (String) setDefaultValue( strGrant, "NA");
	}

	public String getStrTLA(){
		return strTLA;
	}
	public void setStrTLA(String strTLA){
		this. strTLA = (String) setDefaultValue( strTLA, "NA");
	}

	public String getStrAuditing(){
		return strAuditing;
	}
	public void setStrAuditing(String strAuditing){
		this. strAuditing = (String) setDefaultValue( strAuditing, "NA");
	}


}
