package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblnonchargablekot")
@IdClass(clsNonChargableKOTModel_ID.class)

public class clsNonChargableKOTHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsNonChargableKOTHdModel(){}

	public clsNonChargableKOTHdModel(clsNonChargableKOTModel_ID objModelID){
		strTableNo = objModelID.getStrTableNo();
		strItemCode = objModelID.getStrItemCode();
		strKOTNo = objModelID.getStrKOTNo();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTableNo",column=@Column(name="strTableNo")),
@AttributeOverride(name="strItemCode",column=@Column(name="strItemCode")),
@AttributeOverride(name="strKOTNo",column=@Column(name="strKOTNo"))
	})

//Variable Declaration
	@Column(name="strTableNo")
	private String strTableNo;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="dblQuantity")
	private double dblQuantity;

	@Column(name="dblRate")
	private double dblRate;

	@Column(name="strKOTNo")
	private String strKOTNo;

	@Column(name="strEligibleForVoid")
	private String strEligibleForVoid;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strReasonCode")
	private String strReasonCode;

	@Column(name="strRemark")
	private String strRemark;

	@Column(name="dteNCKOTDate")
	private String dteNCKOTDate;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="strPOSCode")
	private String strPOSCode;
	
	@Column(name="strItemName")
	private String strItemName;
	
	
	@Column(name="strBillNote")
	private String strBillNote;
	

public String getStrItemName() {
		return strItemName;
	}

	public void setStrItemName(String strItemName) {
		this.strItemName = strItemName;
	}

	public String getStrBillNote() {
		return strBillNote;
	}

	public void setStrBillNote(String strBillNote) {
		this.strBillNote = strBillNote;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	//Setter-Getter Methods
	public String getStrTableNo(){
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo){
		this. strTableNo = (String) setDefaultValue( strTableNo, "NA");
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public double getDblQuantity(){
		return dblQuantity;
	}
	public void setDblQuantity(double dblQuantity){
		this. dblQuantity = (Double) setDefaultValue( dblQuantity, "0.0000");
	}

	public double getDblRate(){
		return dblRate;
	}
	public void setDblRate(double dblRate){
		this. dblRate = (Double) setDefaultValue( dblRate, "0.0000");
	}

	public String getStrKOTNo(){
		return strKOTNo;
	}
	public void setStrKOTNo(String strKOTNo){
		this. strKOTNo = (String) setDefaultValue( strKOTNo, "NA");
	}

	public String getStrEligibleForVoid(){
		return strEligibleForVoid;
	}
	public void setStrEligibleForVoid(String strEligibleForVoid){
		this. strEligibleForVoid = (String) setDefaultValue( strEligibleForVoid, "NA");
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
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}

	public String getStrReasonCode(){
		return strReasonCode;
	}
	public void setStrReasonCode(String strReasonCode){
		this. strReasonCode = (String) setDefaultValue( strReasonCode, "NA");
	}

	public String getStrRemark(){
		return strRemark;
	}
	public void setStrRemark(String strRemark){
		this. strRemark = (String) setDefaultValue( strRemark, "");
	}

	public String getDteNCKOTDate(){
		return dteNCKOTDate;
	}
	public void setDteNCKOTDate(String dteNCKOTDate){
		this.dteNCKOTDate=dteNCKOTDate;
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

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}




}
