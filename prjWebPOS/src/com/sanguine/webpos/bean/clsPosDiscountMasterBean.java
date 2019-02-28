package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsPosDiscountMasterBean 
{

	private String strDiscountCode;
	private String strDiscountName;
	private String strPosCode;
	private String dteFromDate;
	private String dteToDate;
	private String strDiscountOn;
	private String strDiscountOnCode;
	private String strDiscountType;
	private double dblDiscountValue;
	private String strDiscOnValue;
	private String strDineIn;
	private String strHomeDelivery;
	private String strTakeAway;
	
	private List<clsPOSDiscountDtlsOnBill> listDiscountDtl=new ArrayList<clsPOSDiscountDtlsOnBill>();
	
	public String getStrDiscountCode() {
		return strDiscountCode;
	}
	public void setStrDiscountCode(String strDiscountCode) {
		this.strDiscountCode = strDiscountCode;
	}
	public String getStrDiscountName() {
		return strDiscountName;
	}
	public void setStrDiscountName(String strDiscountName) {
		this.strDiscountName = strDiscountName;
	}
	public String getStrPosCode() {
		return strPosCode;
	}
	public void setStrPosCode(String strPosCode) {
		this.strPosCode = strPosCode;
	}
	public String getDteFromDate() {
		return dteFromDate;
	}
	public void setDteFromDate(String dteFromDate) {
		this.dteFromDate = dteFromDate;
	}
	public String getDteToDate() {
		return dteToDate;
	}
	public void setDteToDate(String dteToDate) {
		this.dteToDate = dteToDate;
	}
	public String getStrDiscountOn() {
		return strDiscountOn;
	}
	public void setStrDiscountOn(String strDiscountOn) {
		this.strDiscountOn = strDiscountOn;
	}
	public String getStrDiscountOnCode() {
		return strDiscountOnCode;
	}
	public void setStrDiscountOnCode(String strDiscountOnCode) {
		this.strDiscountOnCode = strDiscountOnCode;
	}
	public String getStrDiscountType() {
		return strDiscountType;
	}
	public void setStrDiscountType(String strDiscountType) {
		this.strDiscountType = strDiscountType;
	}
	public double getDblDiscountValue() {
		return dblDiscountValue;
	}
	public void setDblDiscountValue(double dblDiscountValue) {
		this.dblDiscountValue = dblDiscountValue;
	}
	public String getStrDiscOnValue() {
		return strDiscOnValue;
	}
	public void setStrDiscOnValue(String strDiscOnValue) {
		this.strDiscOnValue = strDiscOnValue;
	}
	public List<clsPOSDiscountDtlsOnBill> getListDiscountDtl() {
		return listDiscountDtl;
	}
	public void setListDiscountDtl(List<clsPOSDiscountDtlsOnBill> listDiscountDtl) {
		this.listDiscountDtl = listDiscountDtl;
	}
	public String getStrDineIn()
	{
		return strDineIn;
	}
	public void setStrDineIn(String strDineIn)
	{
		this.strDineIn = strDineIn;
	}
	public String getStrHomeDelivery()
	{
		return strHomeDelivery;
	}
	public void setStrHomeDelivery(String strHomeDelivery)
	{
		this.strHomeDelivery = strHomeDelivery;
	}
	public String getStrTakeAway()
	{
		return strTakeAway;
	}
	public void setStrTakeAway(String strTakeAway)
	{
		this.strTakeAway = strTakeAway;
	}
	
	
}
