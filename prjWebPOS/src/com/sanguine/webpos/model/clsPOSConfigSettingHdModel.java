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
@Table(name="tblconfig")
@IdClass(clsPOSConfigSettingModel_ID.class)

public class clsPOSConfigSettingHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSConfigSettingHdModel(){}

	public clsPOSConfigSettingHdModel(clsPOSConfigSettingModel_ID objModelID){
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strServer")
	private String strServer;

	@Column(name="strDBName")
	private String strDBName;

	@Column(name="strUserID")
	private String strUserID;

	@Column(name="strPassword")
	private String strPassword;

	@Column(name="strIPAddress")
	private String strIPAddress;

	@Column(name="strPort")
	private String strPort;

	@Column(name="strBackupPath")
	private String strBackupPath;

	@Column(name="strExportPath")
	private String strExportPath;

	@Column(name="strImagePath")
	private String strImagePath;

	@Column(name="strHOWebServiceUrl")
	private String strHOWebServiceUrl;

	@Column(name="strMMSWebServiceUrl")
	private String strMMSWebServiceUrl;

	@Column(name="strOS")
	private String strOS;

	@Column(name="strDefaultPrinter")
	private String strDefaultPrinter;

	@Column(name="strPrinterType")
	private String strPrinterType;

	@Column(name="strTouchScreenMode")
	private String strTouchScreenMode;

	@Column(name="strServerFilePath")
	private String strServerFilePath;

	@Column(name="strSelectWaiterFromCardSwipe")
	private String strSelectWaiterFromCardSwipe;

	@Column(name="strMySQBackupFilePath")
	private String strMySQBackupFilePath;

	@Column(name="strHOCommunication")
	private String strHOCommunication;

	@Column(name="strAdvReceiptPrinter")
	private String strAdvReceiptPrinter;

	@Column(name="strClientCode")
	private String strClientCode;

//Setter-Getter Methods
	public String getStrServer(){
		return strServer;
	}
	public void setStrServer(String strServer){
		this. strServer = (String) setDefaultValue( strServer, "NA");
	}

	public String getStrDBName(){
		return strDBName;
	}
	public void setStrDBName(String strDBName){
		this. strDBName = (String) setDefaultValue( strDBName, "NA");
	}

	public String getStrUserID(){
		return strUserID;
	}
	public void setStrUserID(String strUserID){
		this. strUserID = (String) setDefaultValue( strUserID, "NA");
	}

	public String getStrPassword(){
		return strPassword;
	}
	public void setStrPassword(String strPassword){
		this. strPassword = (String) setDefaultValue( strPassword, "NA");
	}

	public String getStrIPAddress(){
		return strIPAddress;
	}
	public void setStrIPAddress(String strIPAddress){
		this. strIPAddress = (String) setDefaultValue( strIPAddress, "NA");
	}

	public String getStrPort(){
		return strPort;
	}
	public void setStrPort(String strPort){
		this. strPort = (String) setDefaultValue( strPort, "NA");
	}

	public String getStrBackupPath(){
		return strBackupPath;
	}
	public void setStrBackupPath(String strBackupPath){
		this. strBackupPath = (String) setDefaultValue( strBackupPath, "NA");
	}

	public String getStrExportPath(){
		return strExportPath;
	}
	public void setStrExportPath(String strExportPath){
		this. strExportPath = (String) setDefaultValue( strExportPath, "NA");
	}

	public String getStrImagePath(){
		return strImagePath;
	}
	public void setStrImagePath(String strImagePath){
		this. strImagePath = (String) setDefaultValue( strImagePath, "NA");
	}

	public String getStrHOWebServiceUrl(){
		return strHOWebServiceUrl;
	}
	public void setStrHOWebServiceUrl(String strHOWebServiceUrl){
		this. strHOWebServiceUrl = (String) setDefaultValue( strHOWebServiceUrl, "NA");
	}

	public String getStrMMSWebServiceUrl(){
		return strMMSWebServiceUrl;
	}
	public void setStrMMSWebServiceUrl(String strMMSWebServiceUrl){
		this. strMMSWebServiceUrl = (String) setDefaultValue( strMMSWebServiceUrl, "NA");
	}

	public String getStrOS(){
		return strOS;
	}
	public void setStrOS(String strOS){
		this. strOS = (String) setDefaultValue( strOS, "NA");
	}

	public String getStrDefaultPrinter(){
		return strDefaultPrinter;
	}
	public void setStrDefaultPrinter(String strDefaultPrinter){
		this. strDefaultPrinter = (String) setDefaultValue( strDefaultPrinter, "NA");
	}

	public String getStrPrinterType(){
		return strPrinterType;
	}
	public void setStrPrinterType(String strPrinterType){
		this. strPrinterType = (String) setDefaultValue( strPrinterType, "NA");
	}

	public String getStrTouchScreenMode(){
		return strTouchScreenMode;
	}
	public void setStrTouchScreenMode(String strTouchScreenMode){
		this. strTouchScreenMode = (String) setDefaultValue( strTouchScreenMode, "NA");
	}

	public String getStrServerFilePath(){
		return strServerFilePath;
	}
	public void setStrServerFilePath(String strServerFilePath){
		this. strServerFilePath = (String) setDefaultValue( strServerFilePath, "NA");
	}

	public String getStrSelectWaiterFromCardSwipe(){
		return strSelectWaiterFromCardSwipe;
	}
	public void setStrSelectWaiterFromCardSwipe(String strSelectWaiterFromCardSwipe){
		this. strSelectWaiterFromCardSwipe = (String) setDefaultValue( strSelectWaiterFromCardSwipe, "NA");
	}

	public String getStrMySQBackupFilePath(){
		return strMySQBackupFilePath;
	}
	public void setStrMySQBackupFilePath(String strMySQBackupFilePath){
		this. strMySQBackupFilePath = (String) setDefaultValue( strMySQBackupFilePath, "NA");
	}

	public String getStrHOCommunication(){
		return strHOCommunication;
	}
	public void setStrHOCommunication(String strHOCommunication){
		this. strHOCommunication = (String) setDefaultValue( strHOCommunication, "NA");
	}

	public String getStrAdvReceiptPrinter(){
		return strAdvReceiptPrinter;
	}
	public void setStrAdvReceiptPrinter(String strAdvReceiptPrinter){
		this. strAdvReceiptPrinter = (String) setDefaultValue( strAdvReceiptPrinter, "NA");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}



}
