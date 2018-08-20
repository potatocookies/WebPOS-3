package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsPOSAddKOTToBillBean 
{
	private String strTableName;
	private List<clsPOSTableMasterBean> listTableDtl;
	private List<clsPOSKOTItemDtl> listKOTDtl=new ArrayList<clsPOSKOTItemDtl>();
	private List<clsPOSBillItemDtlBean> listBillDtl=new ArrayList<clsPOSBillItemDtlBean>();

	
	
	
	public List<clsPOSBillItemDtlBean> getListBillDtl() {
		return listBillDtl;
	}

	public void setListBillDtl(List<clsPOSBillItemDtlBean> listBillDtl) {
		this.listBillDtl = listBillDtl;
	}

	public List<clsPOSKOTItemDtl> getListKOTDtl() {
		return listKOTDtl;
	}

	public void setListKOTDtl(List<clsPOSKOTItemDtl> listKOTDtl) {
		this.listKOTDtl = listKOTDtl;
	}

	public String getStrTableName() {
		return strTableName;
	}

	public void setStrTableName(String strTableName) {
		this.strTableName = strTableName;
	}


	public List<clsPOSTableMasterBean> getListTableDtl() {
		return listTableDtl;
	}

	public void setListTableDtl(List<clsPOSTableMasterBean> listTableDtl) {
		this.listTableDtl = listTableDtl;
	}
	
}
