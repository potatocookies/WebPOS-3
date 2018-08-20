package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

public class clsPOSOrderHDBean
{

	
	private String strOrderNo;
	private String strCustomerCode;
	private String strCustomerName;
	private String strDPCode;
	private String dteDate;
	private String tmeTime;
	private double dblHomeDeliCharge;
	private double dblLooseCashAmt;
	private String strPOSCode;
	private String strPOSName;
	private String strCustAddressLine1;
	private String strCustAddressLine2;
	private String strCustAddressLine3;
	private String strCustAddressLine4;
	private String strCustCity;
	private String strDataPostFlag;
	private String strClientCode;
	
	
	
	private List<clsPOSOrderDtlBean> listOfOrderDtlData = new ArrayList();
	
	
	
	
	public String getStrOrderNo()
	{
		return strOrderNo;
	}
	public void setStrOrderNo(String strOrderNo)
	{
		this.strOrderNo = strOrderNo;
	}
	public String getStrCustomerCode()
	{
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode)
	{
		this.strCustomerCode = strCustomerCode;
	}
	public String getStrDPCode()
	{
		return strDPCode;
	}
	public void setStrDPCode(String strDPCode)
	{
		this.strDPCode = strDPCode;
	}
	public String getDteDate()
	{
		return dteDate;
	}
	public void setDteDate(String dteDate)
	{
		this.dteDate = dteDate;
	}
	public String getTmeTime()
	{
		return tmeTime;
	}
	public void setTmeTime(String tmeTime)
	{
		this.tmeTime = tmeTime;
	}
	public double getDblHomeDeliCharge()
	{
		return dblHomeDeliCharge;
	}
	public void setDblHomeDeliCharge(double dblHomeDeliCharge)
	{
		this.dblHomeDeliCharge = dblHomeDeliCharge;
	}
	public double getDblLooseCashAmt()
	{
		return dblLooseCashAmt;
	}
	public void setDblLooseCashAmt(double dblLooseCashAmt)
	{
		this.dblLooseCashAmt = dblLooseCashAmt;
	}
	public String getStrPOSCode()
	{
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode)
	{
		this.strPOSCode = strPOSCode;
	}
	public String getStrCustAddressLine1()
	{
		return strCustAddressLine1;
	}
	public void setStrCustAddressLine1(String strCustAddressLine1)
	{
		this.strCustAddressLine1 = strCustAddressLine1;
	}
	public String getStrCustAddressLine2()
	{
		return strCustAddressLine2;
	}
	public void setStrCustAddressLine2(String strCustAddressLine2)
	{
		this.strCustAddressLine2 = strCustAddressLine2;
	}
	public String getStrCustAddressLine3()
	{
		return strCustAddressLine3;
	}
	public void setStrCustAddressLine3(String strCustAddressLine3)
	{
		this.strCustAddressLine3 = strCustAddressLine3;
	}
	public String getStrCustAddressLine4()
	{
		return strCustAddressLine4;
	}
	public void setStrCustAddressLine4(String strCustAddressLine4)
	{
		this.strCustAddressLine4 = strCustAddressLine4;
	}
	public String getStrCustCity()
	{
		return strCustCity;
	}
	public void setStrCustCity(String strCustCity)
	{
		this.strCustCity = strCustCity;
	}
	public String getStrDataPostFlag()
	{
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag)
	{
		this.strDataPostFlag = strDataPostFlag;
	}
	public String getStrClientCode()
	{
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = strClientCode;
	}
	public String getStrCustomerName()
	{
		return strCustomerName;
	}
	public void setStrCustomerName(String strCustomerName)
	{
		this.strCustomerName = strCustomerName;
	}
	public String getStrPOSName()
	{
		return strPOSName;
	}
	public void setStrPOSName(String strPOSName)
	{
		this.strPOSName = strPOSName;
	}
	public List<clsPOSOrderDtlBean> getListOfOrderDtlData()
	{
		return listOfOrderDtlData;
	}
	public void setListOfOrderDtlData(List<clsPOSOrderDtlBean> listOfOrderDtlData)
	{
		this.listOfOrderDtlData = listOfOrderDtlData;
	}
	
	
	
	
	
	
	
	
	
	
}
