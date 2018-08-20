package com.sanguine.webpos.bean;

import java.util.List;

public class clsPOSStatisticsBean 
{
	private List<clsPOSRevenueHeadWiseSalesReportBean> listOfSalesData;
	private String strPOSCode;
	private String strPOSName;
	private String dblTotalQty;
	private String dblTotalAmt;
	private String dblCoversTurned;
	private String dblTablesTurned;
	private String dblCoversServed;
	private String dblBusyTable;
	
	public String getDblCoversTurned() {
		return dblCoversTurned;
	}

	public void setDblCoversTurned(String dblCoversTurned) {
		this.dblCoversTurned = dblCoversTurned;
	}

	public String getDblTablesTurned() {
		return dblTablesTurned;
	}

	public void setDblTablesTurned(String dblTablesTurned) {
		this.dblTablesTurned = dblTablesTurned;
	}

	public String getDblCoversServed() {
		return dblCoversServed;
	}

	public void setDblCoversServed(String dblCoversServed) {
		this.dblCoversServed = dblCoversServed;
	}

	public String getDblBusyTable() {
		return dblBusyTable;
	}

	public void setDblBusyTable(String dblBusyTable) {
		this.dblBusyTable = dblBusyTable;
	}

	public String getStrPOSName() {
		return strPOSName;
	}

	public void setStrPOSName(String strPOSName) {
		this.strPOSName = strPOSName;
	}

	public String getDblTotalQty() {
		return dblTotalQty;
	}

	public void setDblTotalQty(String dblTotalQty) {
		this.dblTotalQty = dblTotalQty;
	}

	public String getDblTotalAmt() {
		return dblTotalAmt;
	}

	public void setDblTotalAmt(String dblTotalAmt) {
		this.dblTotalAmt = dblTotalAmt;
	}

	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}

	public List<clsPOSRevenueHeadWiseSalesReportBean> getListOfSalesData() {
		return listOfSalesData;
	}

	public void setListOfSalesData(
			List<clsPOSRevenueHeadWiseSalesReportBean> listOfSalesData) {
		this.listOfSalesData = listOfSalesData;
	}
	
}
