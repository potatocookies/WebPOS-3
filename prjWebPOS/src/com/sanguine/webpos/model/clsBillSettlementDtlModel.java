package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

//@Entity
//@Table(name="tblbillsettlementdtl")
@Embeddable
public class clsBillSettlementDtlModel    implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsBillSettlementDtlModel(){}
	
	
	
//	private String strBillNo;
	
	private String strSettlementCode;
	
	private   double dblSettlementAmt;
	
	private  double dblPaidAmt;
	
	private String strExpiryDate;
	
	private String strCardName;
	
	private String strRemark;
	
//	private String strClientCode;
	
	private String strCustomerCode;
	
	private   double dblActualAmt;
	
	private  double dblRefundAmt;
	
	private String strGiftVoucherCode;
	
	private String strDataPostFlag;
	
	private String strFolioNo;
	
	private String strRoomNo;

	
//	private String dteBillDate;



	public String getStrSettlementCode() {
		return strSettlementCode;
	}


	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}


	public double getDblSettlementAmt() {
		return dblSettlementAmt;
	}


	public void setDblSettlementAmt(double dblSettlementAmt) {
		this.dblSettlementAmt = dblSettlementAmt;
	}


	public double getDblPaidAmt() {
		return dblPaidAmt;
	}


	public void setDblPaidAmt(double dblPaidAmt) {
		this.dblPaidAmt = dblPaidAmt;
	}


	public String getStrExpiryDate() {
		return strExpiryDate;
	}


	public void setStrExpiryDate(String strExpiryDate) {
		this.strExpiryDate = strExpiryDate;
	}


	public String getStrCardName() {
		return strCardName;
	}


	public void setStrCardName(String strCardName) {
		this.strCardName = strCardName;
	}


	public String getStrRemark() {
		return strRemark;
	}


	public void setStrRemark(String strRemark) {
		this.strRemark = strRemark;
	}



	public String getStrCustomerCode() {
		return strCustomerCode;
	}


	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}


	public double getDblActualAmt() {
		return dblActualAmt;
	}


	public void setDblActualAmt(double dblActualAmt) {
		this.dblActualAmt = dblActualAmt;
	}


	public double getDblRefundAmt() {
		return dblRefundAmt;
	}


	public void setDblRefundAmt(double dblRefundAmt) {
		this.dblRefundAmt = dblRefundAmt;
	}


	public String getStrGiftVoucherCode() {
		return strGiftVoucherCode;
	}


	public void setStrGiftVoucherCode(String strGiftVoucherCode) {
		this.strGiftVoucherCode = strGiftVoucherCode;
	}


	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}


	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}


	public String getStrFolioNo() {
		return strFolioNo;
	}


	public void setStrFolioNo(String strFolioNo) {
		this.strFolioNo = strFolioNo;
	}


	public String getStrRoomNo() {
		return strRoomNo;
	}


	public void setStrRoomNo(String strRoomNo) {
		this.strRoomNo = strRoomNo;
	}


	
}
