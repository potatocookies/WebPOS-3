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
@Table(name="tblprintersetup")
@IdClass(clsPrinterSetupModel_ID.class)

public class clsPrinterSetupHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPrinterSetupHdModel(){}

	public clsPrinterSetupHdModel(clsPrinterSetupModel_ID objModelID){
		strCostCenterCode = objModelID.getStrCostCenterCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strCostCenterCode",column=@Column(name="strCostCenterCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strCostCenterCode")
	private String strCostCenterCode;

	@Column(name="strCostCenterName")
	private String strCostCenterName;

	@Column(name="strPrimaryPrinterPort")
	private String strPrimaryPrinterPort;

	@Column(name="strSecondaryPrinterPort")
	private String strSecondaryPrinterPort;

	@Column(name="strPrintOnBothPrintersYN")
	private String strPrintOnBothPrintersYN;

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

//Setter-Getter Methods
	public String getStrCostCenterCode(){
		return strCostCenterCode;
	}
	public void setStrCostCenterCode(String strCostCenterCode){
		this. strCostCenterCode = (String) setDefaultValue( strCostCenterCode, "NA");
	}

	public String getStrCostCenterName(){
		return strCostCenterName;
	}
	public void setStrCostCenterName(String strCostCenterName){
		this. strCostCenterName = (String) setDefaultValue( strCostCenterName, "NA");
	}

	public String getStrPrimaryPrinterPort(){
		return strPrimaryPrinterPort;
	}
	public void setStrPrimaryPrinterPort(String strPrimaryPrinterPort){
		this. strPrimaryPrinterPort = (String) setDefaultValue( strPrimaryPrinterPort, "NA");
	}

	public String getStrSecondaryPrinterPort(){
		return strSecondaryPrinterPort;
	}
	public void setStrSecondaryPrinterPort(String strSecondaryPrinterPort){
		this. strSecondaryPrinterPort = (String) setDefaultValue( strSecondaryPrinterPort, "NA");
	}

	public String getStrPrintOnBothPrintersYN(){
		return strPrintOnBothPrintersYN;
	}
	public void setStrPrintOnBothPrintersYN(String strPrintOnBothPrintersYN){
		this. strPrintOnBothPrintersYN = (String) setDefaultValue( strPrintOnBothPrintersYN, "NA");
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



}
