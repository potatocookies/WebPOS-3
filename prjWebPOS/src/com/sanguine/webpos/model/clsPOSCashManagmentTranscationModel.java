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
@Table(name="tblcashmanagement")
@IdClass(clsPOSCashManagmentTranscationModel_ID.class)

public class clsPOSCashManagmentTranscationModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSCashManagmentTranscationModel(){}

	public clsPOSCashManagmentTranscationModel(clsPOSCashManagmentTranscationModel_ID objModelID){
		strTransID = objModelID.getStrTransID();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTransID",column=@Column(name="strTransID")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strTransID")
	private String strTransID;

	@Column(name="strTransType")
	private String strTransType;

	@Column(name="dteTransDate")
	private String dteTransDate;

	@Column(name="strReasonCode")
	private String strReasonCode;

	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="dblAmount")
	private double dblAmount;

	@Column(name="strRemarks")
	private String strRemarks;

	@Column(name="strUserCreated",updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated",updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strCurrencyType")
	private String strCurrencyType;

	@Column(name="intShiftCode")
	private long intShiftCode;

	@Column(name="strAgainst")
	private String strAgainst;

	@Column(name="dblRollingAmt")
	private double dblRollingAmt;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

//Setter-Getter Methods
	public String getStrTransID(){
		return strTransID;
	}
	public void setStrTransID(String strTransID){
		this. strTransID = (String) setDefaultValue( strTransID, "NA");
	}

	public String getStrTransType(){
		return strTransType;
	}
	public void setStrTransType(String strTransType){
		this. strTransType = (String) setDefaultValue( strTransType, "NA");
	}

	public String getDteTransDate(){
		return dteTransDate;
	}
	public void setDteTransDate(String dteTransDate){
		this.dteTransDate=dteTransDate;
	}

	public String getStrReasonCode(){
		return strReasonCode;
	}
	public void setStrReasonCode(String strReasonCode){
		this. strReasonCode = (String) setDefaultValue( strReasonCode, "NA");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public double getDblAmount(){
		return dblAmount;
	}
	public void setDblAmount(double dblAmount){
		this. dblAmount = (Double) setDefaultValue( dblAmount, "0.0000");
	}

	public String getStrRemarks(){
		return strRemarks;
	}
	public void setStrRemarks(String strRemarks){
		this. strRemarks = (String) setDefaultValue( strRemarks, "NA");
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

	public String getStrCurrencyType(){
		return strCurrencyType;
	}
	public void setStrCurrencyType(String strCurrencyType){
		this. strCurrencyType = (String) setDefaultValue( strCurrencyType, "NA");
	}

	public long getIntShiftCode(){
		return intShiftCode;
	}
	public void setIntShiftCode(long intShiftCode){
		this. intShiftCode = (Long) setDefaultValue( intShiftCode, "0");
	}

	public String getStrAgainst(){
		return strAgainst;
	}
	public void setStrAgainst(String strAgainst){
		this. strAgainst = (String) setDefaultValue( strAgainst, "NA");
	}

	public double getDblRollingAmt(){
		return dblRollingAmt;
	}
	public void setDblRollingAmt(double dblRollingAmt){
		this. dblRollingAmt = (Double) setDefaultValue( dblRollingAmt, "0.0000");
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



}
