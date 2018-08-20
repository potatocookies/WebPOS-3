package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;
import com.sanguine.webpos.model.clsBillHdModel_ID;
import com.sanguine.webpos.model.clsBillSettlementDtlModel;


@Entity
@Table(name="tblhomedelivery")
@IdClass(clsHomeDeliveryHdModel_ID.class)
	
public class clsHomeDeliveryHdModel extends clsBaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public clsHomeDeliveryHdModel(){}
	
	public clsHomeDeliveryHdModel(clsHomeDeliveryHdModel_ID objModelID){
		strBillNo=objModelID.getStrBillNo();
		strClientCode=objModelID.getStrClientCode();
	}
	
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})
	
	
	
	
	
	@Column(name="strBillNo")
    private String strBillNo;
	@Column(name="strCustomerCode")
    private String strCustomerCode;
	@Column(name="strDPCode")
    private String strDPCode;
	@Column(name="dteDate")
    private String dteDate;
	@Column(name="tmeTime")
    private String tmeTime;
	@Column(name="dblHomeDeliCharge")
    private double dblHomeDeliCharge;
	@Column(name="dblLooseCashAmt")
    private double dblLooseCashAmt;

	@Column(name="strPOSCode")
    private String strPOSCode;
	@Column(name="strCustAddressLine1")
    private String strCustAddressLine1;

	@Column(name="strCustAddressLine2")
    private String strCustAddressLine2;
	@Column(name="strCustAddressLine3")
    private String strCustAddressLine3;
	@Column(name="strCustAddressLine4")
    private String strCustAddressLine4;
	
	@Column(name="strCustCity")
    private String strCustCity;
	@Column(name="strDataPostFlag")
    private String strDataPostFlag;
	@Column(name="strClientCode")
    private String strClientCode;
	public String getStrBillNo() {
		return strBillNo;
	}

	public void setStrBillNo(String strBillNo) {
		this.strBillNo = strBillNo;
	}

	public String getStrCustomerCode() {
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}

	public String getStrDPCode() {
		return strDPCode;
	}

	public void setStrDPCode(String strDPCode) {
		this.strDPCode = strDPCode;
	}

	public String getDteDate() {
		return dteDate;
	}

	public void setDteDate(String dteDate) {
		this.dteDate = dteDate;
	}

	public String getTmeTime() {
		return tmeTime;
	}

	public void setTmeTime(String tmeTime) {
		this.tmeTime = tmeTime;
	}

	public double getDblHomeDeliCharge() {
		return dblHomeDeliCharge;
	}

	public void setDblHomeDeliCharge(double dblHomeDeliCharge) {
		this.dblHomeDeliCharge = dblHomeDeliCharge;
	}

	public double getDblLooseCashAmt() {
		return dblLooseCashAmt;
	}

	public void setDblLooseCashAmt(double dblLooseCashAmt) {
		this.dblLooseCashAmt = dblLooseCashAmt;
	}

	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}

	public String getStrCustAddressLine1() {
		return strCustAddressLine1;
	}

	public void setStrCustAddressLine1(String strCustAddressLine1) {
		this.strCustAddressLine1 = strCustAddressLine1;
	}

	public String getStrCustAddressLine2() {
		return strCustAddressLine2;
	}

	public void setStrCustAddressLine2(String strCustAddressLine2) {
		this.strCustAddressLine2 = strCustAddressLine2;
	}

	public String getStrCustAddressLine3() {
		return strCustAddressLine3;
	}

	public void setStrCustAddressLine3(String strCustAddressLine3) {
		this.strCustAddressLine3 = strCustAddressLine3;
	}

	public String getStrCustAddressLine4() {
		return strCustAddressLine4;
	}

	public void setStrCustAddressLine4(String strCustAddressLine4) {
		this.strCustAddressLine4 = strCustAddressLine4;
	}

	public String getStrCustCity() {
		return strCustCity;
	}

	public void setStrCustCity(String strCustCity) {
		this.strCustCity = strCustCity;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	
}
