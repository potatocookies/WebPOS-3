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
@Table(name="tblbillseries")
@IdClass(clsBillSeriesModel_ID.class)

public class clsBillSeriesHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillSeriesHdModel(){}

	public clsBillSeriesHdModel(clsBillSeriesModel_ID objModelID){
		strPOSCode = objModelID.getStrPOSCode();
		strClientCode = objModelID.getStrClientCode();
		strBillSeries= objModelID.getStrBillSeries();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strBillSeries",column=@Column(name="strBillSeries")),
		@AttributeOverride(name="strPOSCode",column=@Column(name="strPOSCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strType")
	private String strType;

	@Column(name="strBillSeries")
	private String strBillSeries;

	@Column(name="intLastNo")
	private long intLastNo;

	@Column(name="strCodes")
	private String strCodes;

	@Column(name="strNames")
	private String strNames;

	@Column(name="strUserCreated", updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteCreatedDate", updatable=false)
	private String dteCreatedDate;

	@Column(name="dteEditedDate")
	private String dteEditedDate;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strPropertyCode")
	private String strPropertyCode;

	@Column(name="strPrintGTOfOtherBills")
	private String strPrintGTOfOtherBills;

	@Column(name="strPrintInclusiveOfTaxOnBill")
	private String strPrintInclusiveOfTaxOnBill;

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getStrType(){
		return strType;
	}
	public void setStrType(String strType){
		this. strType = (String) setDefaultValue( strType, "NA");
	}

	public String getStrBillSeries(){
		return strBillSeries;
	}
	public void setStrBillSeries(String strBillSeries){
		this. strBillSeries = (String) setDefaultValue( strBillSeries, "NA");
	}

	public long getIntLastNo(){
		return intLastNo;
	}
	public void setIntLastNo(long intLastNo){
		this. intLastNo = (Long) setDefaultValue( intLastNo, "0");
	}

	public String getStrCodes(){
		return strCodes;
	}
	public void setStrCodes(String strCodes){
		this. strCodes = (String) setDefaultValue( strCodes, "NA");
	}

	public String getStrNames(){
		return strNames;
	}
	public void setStrNames(String strNames){
		this. strNames = (String) setDefaultValue( strNames, "NA");
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

	public String getDteCreatedDate(){
		return dteCreatedDate;
	}
	public void setDteCreatedDate(String dteCreatedDate){
		this.dteCreatedDate=dteCreatedDate;
	}

	public String getDteEditedDate(){
		return dteEditedDate;
	}
	public void setDteEditedDate(String dteEditedDate){
		this.dteEditedDate=dteEditedDate;
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrPropertyCode(){
		return strPropertyCode;
	}
	public void setStrPropertyCode(String strPropertyCode){
		this. strPropertyCode = (String) setDefaultValue( strPropertyCode, "NA");
	}

	public String getStrPrintGTOfOtherBills(){
		return strPrintGTOfOtherBills;
	}
	public void setStrPrintGTOfOtherBills(String strPrintGTOfOtherBills){
		this. strPrintGTOfOtherBills = (String) setDefaultValue( strPrintGTOfOtherBills, "NA");
	}

	public String getStrPrintInclusiveOfTaxOnBill(){
		return strPrintInclusiveOfTaxOnBill;
	}
	public void setStrPrintInclusiveOfTaxOnBill(String strPrintInclusiveOfTaxOnBill){
		this. strPrintInclusiveOfTaxOnBill = (String) setDefaultValue( strPrintInclusiveOfTaxOnBill, "NA");
	}




}
