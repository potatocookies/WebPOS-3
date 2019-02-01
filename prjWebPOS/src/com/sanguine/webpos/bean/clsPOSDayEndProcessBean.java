package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;



public class clsPOSDayEndProcessBean {

	
	private List listDayEnd=null;;
	private List listDayEndTotal=null;;
	private List listSettlement=null;;
	private List listSettlementTotal=null;
	private List listSalesInProg=null;
	private List listUnSettlebill=null;
	
	private List<clsPOSDayEndProcessBean> listMailReport ;
	
	private String strReportName;
	private Boolean strReportCheck;

	
	private String totalpax;
	private String total;
	private String fromDate;
	private String toDate;
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public List<clsPOSDayEndProcessBean> getListMailReport() {
		return listMailReport;
	}
	public void setListMailReport(List<clsPOSDayEndProcessBean> listMailReport) {
		this.listMailReport = listMailReport;
	}
	
	public String getTotalpax() {
		return totalpax;
	}
	public void setTotalpax(String totalpax) {
		this.totalpax = totalpax;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
	public List getListDayEnd() {
		return listDayEnd;
	}
	public void setListDayEnd(List listDayEnd) {
		this.listDayEnd = listDayEnd;
	}
	public List getListDayEndTotal() {
		return listDayEndTotal;
	}
	public void setListDayEndTotal(List listDayEndTotal) {
		this.listDayEndTotal = listDayEndTotal;
	}
	public List getListSettlement() {
		return listSettlement;
	}
	public void setListSettlement(List listSettlement) {
		this.listSettlement = listSettlement;
	}
	public List getListSettlementTotal() {
		return listSettlementTotal;
	}
	public void setListSettlementTotal(List listSettlementTotal) {
		this.listSettlementTotal = listSettlementTotal;
	}
	
	public List getListSalesInProg() {
		return listSalesInProg;
	}
	public void setListSalesInProg(List listSalesInProg) {
		this.listSalesInProg = listSalesInProg;
	}
	public List getListUnSettlebill() {
		return listUnSettlebill;
	}
	public void setListUnSettlebill(List listUnSettlebill) {
		this.listUnSettlebill = listUnSettlebill;
	}
	
	public String getStrReportName() {
		return strReportName;
	}
	public void setStrReportName(String strReportName) {
		this.strReportName = strReportName;
	}
	
	public Boolean getStrReportCheck() {
		return strReportCheck;
	}
	public void setStrReportCheck(Boolean strReportCheck) {
		this.strReportCheck = strReportCheck;
	}
	public String getMailReport() {
		return mailReport;
	}
	public void setMailReport(String mailReport) {
		this.mailReport = mailReport;
	}
	private String mailReport;

}
