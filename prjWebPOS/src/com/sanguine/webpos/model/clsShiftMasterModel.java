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
@Table(name="tblshiftmaster")
@NamedQueries({ 
	@NamedQuery(name = "getALLShift", query = "select m.intShiftCode,m.strPOSCode,m.tmeShiftStart,m.tmeShiftEnd,m.strBillDateTimeType,m.dteDateCreated"
		+ " from clsShiftMasterModel m where m.strClientCode=:clientCode"),
		
		@NamedQuery(name = "getShift", query = "from clsShiftMasterModel where intShiftCode=:shiftCode and strClientCode=:clientCode")
})



@IdClass(clsShiftMasterModel_ID.class)

public class clsShiftMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsShiftMasterModel(){}

	public clsShiftMasterModel(clsShiftMasterModel_ID objModelID){
		intShiftCode =  objModelID.getIntShiftCode();
		strClientCode =  objModelID.getStrClientCode();
	}
	
	@CollectionOfElements(fetch=FetchType.EAGER)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="intShiftCode",column=@Column(name="intShiftCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="intShiftCode")
	private String intShiftCode;

	@Column(name="strPOSCode")
	private String strPOSCode;
	
	@Column(name="tmeShiftStart")
	private String tmeShiftStart;
	
	@Column(name="tmeShiftEnd")
	private String tmeShiftEnd;

	@Column(name="strBillDateTimeType")
	private String strBillDateTimeType;

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

//Setter-Getter Methods
	public String getIntShiftCode(){
		return intShiftCode;
	}
	public void setIntShiftCode(String intShiftCode){
		this. intShiftCode = (String) setDefaultValue( intShiftCode, "0");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	
	public String getTmeShiftStart() {
		return tmeShiftStart;
	}

	public void setTmeShiftStart(String tmeShiftStart) {
		this.tmeShiftStart = tmeShiftStart;
	}

	public String getTmeShiftEnd() {
		return tmeShiftEnd;
	}

	public void setTmeShiftEnd(String tmeShiftEnd) {
		this.tmeShiftEnd = tmeShiftEnd;
	}

	public String getStrBillDateTimeType(){
		return strBillDateTimeType;
	}
	public void setStrBillDateTimeType(String strBillDateTimeType){
		this. strBillDateTimeType = (String) setDefaultValue( strBillDateTimeType, "NA");
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


	
public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}



}
