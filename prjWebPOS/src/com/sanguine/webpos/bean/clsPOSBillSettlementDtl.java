package com.sanguine.webpos.bean;

public class clsPOSBillSettlementDtl
{

	private String strBillNo;

	private String strSettlementCode;

	private String strSettlementName;

	private String strSettlementType;

	private double dblSettlementAmt;

	private double dblTipAmt;

	private double dblPaidAmt;

	private String strExpiryDate;

	private String strCardName;

	private String strRemark;

	private String strClientCode;

	private String strCustomerCode;

	private double dblActualAmt;

	private double dblRefundAmt;

	private String strGiftVoucherCode;

	private String strDataPostFlag;

	private String posName;

	public clsPOSBillSettlementDtl()
	{
	}



	public clsPOSBillSettlementDtl(String settlementCode,String settlementName, double dblSettlementAmt, String posName, String settlementType)
	{
		this.strSettlementCode = settlementCode;
		this.strSettlementName=settlementName;
		this.dblSettlementAmt = dblSettlementAmt;
		this.posName = posName;
		this.strSettlementType=settlementType;
	}

	public clsPOSBillSettlementDtl(String settlementCode,String settlementName, double dblSettlementAmt, String posName, String settlementType, double tipAmt)
	{
		this.strSettlementCode = settlementCode;
		this.strSettlementName=settlementName;
		this.dblSettlementAmt = dblSettlementAmt;
		this.posName = posName;
		this.strSettlementType=settlementType;
		this.dblTipAmt=tipAmt;

	}

	public String getStrSettlementName()
	{
		return strSettlementName;
	}

	public void setStrSettlementName(String strSettlementName)
	{
		this.strSettlementName = strSettlementName;
	}        

	public String getPosName()
	{
		return posName;
	}

	public void setPosName(String posName)
	{
		this.posName = posName;
	}               

	public String getStrBillNo() {
		return strBillNo;
	}

	public void setStrBillNo(String strBillNo) {
		this.strBillNo = strBillNo;
	}

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

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
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

	public String getStrSettlementType() {
		return strSettlementType;
	}

	public void setStrSettlementType(String strSettlementType) {
		this.strSettlementType = strSettlementType;
	}

	public double getDblTipAmt()
	{
		return dblTipAmt;
	}

	public void setDblTipAmt(double dblTipAmt)
	{
		this.dblTipAmt = dblTipAmt;
	}
}


