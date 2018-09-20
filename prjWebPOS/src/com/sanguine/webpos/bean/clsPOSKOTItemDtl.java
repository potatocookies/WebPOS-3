package com.sanguine.webpos.bean;

public class clsPOSKOTItemDtl implements Cloneable
{

	
	private String strSerialNo;
	private String strTableNo;
	private String strCardNo;
	private String strHomeDelivery;
	private String strCustomerCode;
	private String strPOSCode;
	private String strItemCode;
	private String strItemName;
	private String strWaiterNo;
	private String strKOTNo;
	private String strPrintYN;
	private String strManualKOTNo;
	private String strUserCreated;
	private String strUserEdited;
	private String strOrderBefore;
	private String strTakeAwayYesNo;
	private String tdhComboItemYN;
	private String strDelBoyCode;
	private String strNCKotYN;
	private String strCustomerName;
	private String strActiveYN;
	private String dblBalance;
	private String dblCreditLimit;
	private String strCounterCode;
	private String strPromoCode;
	private String strKOTDateTime;
	private String strOrderProcessTime;
	private String strOrderPickupTime;
	private String strAdvBookingNo;

	private String strDataPostFlag;
	private String strTdhYN;
	private String strSequenceNo;

	private String strMMSDataPostFlag;

	private String dteDateCreated;
	private String dteDateEdited;
	private String dteBillDate;

	private double dblItemQuantity;
	private double dblAmount;
	private double dblRate;
	private double dblTaxAmount;
	private double dblRedeemAmt;
	private double dblDiscAmt;
	private double dblDiscPer;

	private int intPaxNo;
	
	
	
	
	
	
	
	
	

	public String getDteBillDate()
	{
		return dteBillDate;
	}

	public void setDteBillDate(String dteBillDate)
	{
		this.dteBillDate = dteBillDate;
	}

	public double getDblTaxAmount()
	{
		return dblTaxAmount;
	}

	public void setDblTaxAmount(double dblTaxAmount)
	{
		this.dblTaxAmount = dblTaxAmount;
	}

	public String getStrDataPostFlag()
	{
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag)
	{
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrMMSDataPostFlag()
	{
		return strMMSDataPostFlag;
	}

	public void setStrMMSDataPostFlag(String strMMSDataPostFlag)
	{
		this.strMMSDataPostFlag = strMMSDataPostFlag;
	}

	public String getStrSerialNo()
	{
		return strSerialNo;
	}

	public void setStrSerialNo(String strSerialNo)
	{
		this.strSerialNo = strSerialNo;
	}

	public String getStrTableNo()
	{
		return strTableNo;
	}

	public void setStrTableNo(String strTableNo)
	{
		this.strTableNo = strTableNo;
	}

	public String getStrCardNo()
	{
		return strCardNo;
	}

	public void setStrCardNo(String strCardNo)
	{
		this.strCardNo = strCardNo;
	}

	public double getDblRedeemAmt()
	{
		return dblRedeemAmt;
	}

	public void setDblRedeemAmt(double dblRedeemAmt)
	{
		this.dblRedeemAmt = dblRedeemAmt;
	}

	public String getStrHomeDelivery()
	{
		return strHomeDelivery;
	}

	public void setStrHomeDelivery(String strHomeDelivery)
	{
		this.strHomeDelivery = strHomeDelivery;
	}

	public String getStrCustomerCode()
	{
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode)
	{
		this.strCustomerCode = strCustomerCode;
	}

	public String getStrPOSCode()
	{
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode)
	{
		this.strPOSCode = strPOSCode;
	}

	public String getStrItemCode()
	{
		return strItemCode;
	}

	public void setStrItemCode(String strItemCode)
	{
		this.strItemCode = strItemCode;
	}

	public String getStrItemName()
	{
		return strItemName;
	}

	public void setStrItemName(String strItemName)
	{
		this.strItemName = strItemName;
	}

	public double getDblItemQuantity()
	{
		return dblItemQuantity;
	}

	public void setDblItemQuantity(double dblItemQuantity)
	{
		this.dblItemQuantity = dblItemQuantity;
	}

	public double getDblAmount()
	{
		return dblAmount;
	}

	public void setDblAmount(double dblAmount)
	{
		this.dblAmount = dblAmount;
	}

	public String getStrWaiterNo()
	{
		return strWaiterNo;
	}

	public void setStrWaiterNo(String strWaiterNo)
	{
		this.strWaiterNo = strWaiterNo;
	}

	public String getStrKOTNo()
	{
		return strKOTNo;
	}

	public void setStrKOTNo(String strKOTNo)
	{
		this.strKOTNo = strKOTNo;
	}

	public int getIntPaxNo()
	{
		return intPaxNo;
	}

	public void setIntPaxNo(int intPaxNo)
	{
		this.intPaxNo = intPaxNo;
	}

	public String getStrPrintYN()
	{
		return strPrintYN;
	}

	public void setStrPrintYN(String strPrintYN)
	{
		this.strPrintYN = strPrintYN;
	}

	public String getStrManualKOTNo()
	{
		return strManualKOTNo;
	}

	public void setStrManualKOTNo(String strManualKOTNo)
	{
		this.strManualKOTNo = strManualKOTNo;
	}

	public String getStrUserCreated()
	{
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated)
	{
		this.strUserCreated = strUserCreated;
	}

	public String getStrUserEdited()
	{
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited)
	{
		this.strUserEdited = strUserEdited;
	}

	public String getDteDateCreated()
	{
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated)
	{
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited()
	{
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited)
	{
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrOrderBefore()
	{
		return strOrderBefore;
	}

	public void setStrOrderBefore(String strOrderBefore)
	{
		this.strOrderBefore = strOrderBefore;
	}

	public String getStrTakeAwayYesNo()
	{
		return strTakeAwayYesNo;
	}

	public void setStrTakeAwayYesNo(String strTakeAwayYesNo)
	{
		this.strTakeAwayYesNo = strTakeAwayYesNo;
	}

	public String getTdhComboItemYN()
	{
		return tdhComboItemYN;
	}

	public void setTdhComboItemYN(String tdhComboItemYN)
	{
		this.tdhComboItemYN = tdhComboItemYN;
	}

	public String getStrDelBoyCode()
	{
		return strDelBoyCode;
	}

	public void setStrDelBoyCode(String strDelBoyCode)
	{
		this.strDelBoyCode = strDelBoyCode;
	}

	public String getStrNCKotYN()
	{
		return strNCKotYN;
	}

	public void setStrNCKotYN(String strNCKotYN)
	{
		this.strNCKotYN = strNCKotYN;
	}

	public String getStrCustomerName()
	{
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName)
	{
		this.strCustomerName = strCustomerName;
	}

	public String getStrActiveYN()
	{
		return strActiveYN;
	}

	public void setStrActiveYN(String strActiveYN)
	{
		this.strActiveYN = strActiveYN;
	}

	public String getDblBalance()
	{
		return dblBalance;
	}

	public void setDblBalance(String dblBalance)
	{
		this.dblBalance = dblBalance;
	}

	public String getDblCreditLimit()
	{
		return dblCreditLimit;
	}

	public void setDblCreditLimit(String dblCreditLimit)
	{
		this.dblCreditLimit = dblCreditLimit;
	}

	public String getStrCounterCode()
	{
		return strCounterCode;
	}

	public void setStrCounterCode(String strCounterCode)
	{
		this.strCounterCode = strCounterCode;
	}

	public String getStrPromoCode()
	{
		return strPromoCode;
	}

	public void setStrPromoCode(String strPromoCode)
	{
		this.strPromoCode = strPromoCode;
	}

	public double getDblRate()
	{
		return dblRate;
	}

	public void setDblRate(double dblRate)
	{
		this.dblRate = dblRate;
	}

	public String getStrKOTDateTime()
	{
		return strKOTDateTime;
	}

	public void setStrKOTDateTime(String strKOTDateTime)
	{
		this.strKOTDateTime = strKOTDateTime;
	}

	public String getStrOrderProcessTime()
	{
		return strOrderProcessTime;
	}

	public void setStrOrderProcessTime(String strOrderProcessTime)
	{
		this.strOrderProcessTime = strOrderProcessTime;
	}

	public String getStrOrderPickupTime()
	{
		return strOrderPickupTime;
	}

	public void setStrOrderPickupTime(String strOrderPickupTime)
	{
		this.strOrderPickupTime = strOrderPickupTime;
	}

	public String getStrAdvBookingNo()
	{
		return strAdvBookingNo;
	}

	public void setStrAdvBookingNo(String strAdvBookingNo)
	{
		this.strAdvBookingNo = strAdvBookingNo;
	}

	public String getStrTdhYN()
	{
		return strTdhYN;
	}

	public void setStrTdhYN(String strTdhYN)
	{
		this.strTdhYN = strTdhYN;
	}

	public String getStrSequenceNo()
	{
		return strSequenceNo;
	}

	public void setStrSequenceNo(String strSequenceNo)
	{
		this.strSequenceNo = strSequenceNo;
	}

	public double getDblDiscAmt()
	{
		return dblDiscAmt;
	}

	public void setDblDiscAmt(double dblDiscAmt)
	{
		this.dblDiscAmt = dblDiscAmt;
	}

	public double getDblDiscPer()
	{
		return dblDiscPer;
	}

	public void setDblDiscPer(double dblDiscPer)
	{
		this.dblDiscPer = dblDiscPer;
	}
	
	@Override
	public clsPOSKOTItemDtl clone() throws CloneNotSupportedException
	{		
		return (clsPOSKOTItemDtl)super.clone();
	}

	

}
