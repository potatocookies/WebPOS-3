package com.sanguine.webpos.bean;

import java.util.List;

public class clsPOSTableStatusReportBean 
{
	private List<clsPOSTableMasterBean> listOfAllTable;
	private String strPOSCode;
	private String strStatus;
	private String strArea;
	public String getStrPOSCode() {
		return strPOSCode;
	}
	public String getStrStatus() {
		return strStatus;
	}
	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}
	public String getStrArea() {
		return strArea;
	}
	public void setStrArea(String strArea) {
		this.strArea = strArea;
	}
	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	public List<clsPOSTableMasterBean> getListOfAllTable() {
		return listOfAllTable;
	}
	public void setListOfAllTable(List<clsPOSTableMasterBean> listOfAllTable) {
		this.listOfAllTable = listOfAllTable;
	}
	

}
