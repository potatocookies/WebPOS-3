package com.sanguine.webpos.bean;

import java.util.List;

public class clsPOSChangeSettlementBean
{
   private String strBillNo;
   private String strBillDate;
   private String strSettleDesc;
   private String strSettleCode;
   private String strSettleType;
   private String strPayMode;
   private double dblPaidAmt;
   private double dblBillAmount;
   private double dblBalAmount;
   private String strRemark;
   private String strCustomerCode;
   private String strReasonCode;
   
   private List<clsPOSBillSettlementDtl> listOfBillSettleMode;
   
   
	public List<clsPOSBillSettlementDtl> getListOfBillSettleMode()
{
	return listOfBillSettleMode;
}
public void setListOfBillSettleMode(List<clsPOSBillSettlementDtl> listOfBillSettleMode)
{
	this.listOfBillSettleMode = listOfBillSettleMode;
}
	public String getStrBillNo()
	{
		return strBillNo;
	}
	public void setStrBillNo(String strBillNo)
	{
		this.strBillNo = strBillNo;
	}
	public String getStrBillDate()
	{
		return strBillDate;
	}
	public void setStrBillDate(String strBillDate)
	{
		this.strBillDate = strBillDate;
	}
	public String getStrSettleDesc()
	{
		return strSettleDesc;
	}
	public void setStrSettleDesc(String strSettleDesc)
	{
		this.strSettleDesc = strSettleDesc;
	}
	public String getStrSettleCode()
	{
		return strSettleCode;
	}
	public void setStrSettleCode(String strSettleCode)
	{
		this.strSettleCode = strSettleCode;
	}
	public String getStrSettleType()
	{
		return strSettleType;
	}
	public void setStrSettleType(String strSettleType)
	{
		this.strSettleType = strSettleType;
	}
	public String getStrPayMode()
	{
		return strPayMode;
	}
	public void setStrPayMode(String strPayMode)
	{
		this.strPayMode = strPayMode;
	}
	public double getDblPaidAmt()
	{
		return dblPaidAmt;
	}
	public void setDblPaidAmt(double dblPaidAmt)
	{
		this.dblPaidAmt = dblPaidAmt;
	}
	
	public double getDblBillAmount()
	{
		return dblBillAmount;
	}
	public void setDblBillAmount(double dblBillAmount)
	{
		this.dblBillAmount = dblBillAmount;
	}
	public double getDblBalAmount()
	{
		return dblBalAmount;
	}
	public void setDblBalAmount(double dblBalAmount)
	{
		this.dblBalAmount = dblBalAmount;
	}
	public String getStrRemark()
	{
		return strRemark;
	}
	public void setStrRemark(String strRemark)
	{
		this.strRemark = strRemark;
	}
	public String getStrCustomerCode()
	{
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode)
	{
		this.strCustomerCode = strCustomerCode;
	}
	public String getStrReasonCode()
	{
		return strReasonCode;
	}
	public void setStrReasonCode(String strReasonCode)
	{
		this.strReasonCode = strReasonCode;
	}
   
	
	
}
