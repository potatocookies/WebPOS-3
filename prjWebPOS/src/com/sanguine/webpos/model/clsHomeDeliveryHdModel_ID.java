package com.sanguine.webpos.model;

import java.io.Serializable;

public class clsHomeDeliveryHdModel_ID implements Serializable{

	
	private String strBillNo;
	private String strClientCode;
	
	
	public clsHomeDeliveryHdModel_ID(){}
	
	public clsHomeDeliveryHdModel_ID(String strPOCode,String strClientCode){
		this.strBillNo=strPOCode;
		this.strClientCode=strClientCode;
	}

	public String getStrBillNo() {
		return strBillNo;
	}

	public void setStrBillNo(String strBillNo) {
		this.strBillNo = strBillNo;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		clsHomeDeliveryHdModel_ID cp =(clsHomeDeliveryHdModel_ID)obj;
		if(this.strBillNo.equals(cp.getStrBillNo()) && this.strClientCode.equals(cp.getStrClientCode())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strBillNo.hashCode()+this.strClientCode.hashCode();
	
}
}
