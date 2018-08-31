
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
@Table(name="tblreservation")
@IdClass(clsTableReservationModel_ID.class)

public class clsTableReservationModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsTableReservationModel(){}

	public clsTableReservationModel(clsTableReservationModel_ID objModelID){
		strResCode = objModelID.getStrResCode();
		strCustomerCode = objModelID.getStrCustomerCode();
		strClientCode=objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strResCode",column=@Column(name="strResCode")),
@AttributeOverride(name="strCustomerCode",column=@Column(name="strCustomerCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strResCode")
	private String strResCode;

	@Column(name="strCustomerCode")
	private String strCustomerCode;

	@Column(name="intPax")
	private double intPax;

	@Column(name="strSmoking")
	private String strSmoking;

	@Column(name="dteResDate")
	private String dteResDate;

	@Column(name="tmeResTime")
	private String tmeResTime;

	@Column(name="strAMPM")
	private String strAMPM;
	
	@Column(name="strSpecialInfo")
	private String strSpecialInfo;

	@Column(name="strTableNo")
	private String strTableNo;

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
	
	@Column(name="strPosCode")
	private String strPosCode;
	

	//Setter-Getter Methods

	public String getStrResCode() {
		return strResCode;
	}

	public void setStrResCode(String strResCode) {
		this.strResCode = strResCode;
	}

	public String getStrCustomerCode() {
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}

	public double getIntPax() {
		return intPax;
	}

	public void setIntPax(double intPax) {
		this.intPax = intPax;
	}

	public String getStrSmoking() {
		return strSmoking;
	}

	public void setStrSmoking(String strSmoking) {
		this.strSmoking = strSmoking;
	}

	public String getDteResDate() {
		return dteResDate;
	}

	public void setDteResDate(String dteResDate) {
		this.dteResDate = dteResDate;
	}

	public String getTmeResTime() {
		return tmeResTime;
	}

	public void setTmeResTime(String tmeResTime) {
		this.tmeResTime = tmeResTime;
	}

	public String getStrAMPM() {
		return strAMPM;
	}

	public void setStrAMPM(String strAMPM) {
		this.strAMPM = strAMPM;
	}

	public String getStrSpecialInfo() {
		return strSpecialInfo;
	}

	public void setStrSpecialInfo(String strSpecialInfo) {
		this.strSpecialInfo = strSpecialInfo;
	}

	public String getStrTableNo() {
		return strTableNo;
	}

	public void setStrTableNo(String strTableNo) {
		this.strTableNo = strTableNo;
	}

	public String getStrUserCreated() {
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}

	public String getStrUserEdited() {
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited) {
		this.strUserEdited = strUserEdited;
	}

	public String getDteDateCreated() {
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated) {
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited() {
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited) {
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrPosCode() {
		return strPosCode;
	}

	public void setStrPosCode(String strPosCode) {
		this.strPosCode = strPosCode;
	}
	

}
