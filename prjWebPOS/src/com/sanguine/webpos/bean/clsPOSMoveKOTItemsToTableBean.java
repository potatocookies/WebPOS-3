package com.sanguine.webpos.bean;

import java.util.List;

import org.json.simple.JSONObject;

public class clsPOSMoveKOTItemsToTableBean {
	private String strBusyTbl;

	private	String strTableNo;
	
	private String jObjKOTItemList;
	
	private List<clsPOSKOTItemdtlBean> itemDtlList;

	public String getStrBusyTbl() {
		return strBusyTbl;
	}

	public void setStrBusyTbl(String strBusyTbl) {
		this.strBusyTbl = strBusyTbl;
	}

	public String getStrTableNo() {
		return strTableNo;
	}

	public void setStrTableNo(String strTableNo) {
		this.strTableNo = strTableNo;
	}

	public List<clsPOSKOTItemdtlBean> getItemDtlList() {
		return itemDtlList;
	}

	public void setItemDtlList(List<clsPOSKOTItemdtlBean> itemDtlList) {
		this.itemDtlList = itemDtlList;
	}

	public String getjObjKOTItemList() {
		return jObjKOTItemList;
	}

	public void setjObjKOTItemList(String jObjKOTItemList) {
		this.jObjKOTItemList = jObjKOTItemList;
	}

	
	
}
