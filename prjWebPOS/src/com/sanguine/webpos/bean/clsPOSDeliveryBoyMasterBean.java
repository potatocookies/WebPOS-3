package com.sanguine.webpos.bean;

import java.util.List;

public class clsPOSDeliveryBoyMasterBean{
//Variable Declaration
	private String strDPCode;

	private String strDPName;

	private String strOperational;
	
	private List<clsPOSDeliveryBoyChargesBean> listDeliveryBoyCharges;	
	
//Setter-Getter Methods
	public String getStrDPCode(){
		return strDPCode;
	}
	public void setStrDPCode(String strDPCode){
		this.strDPCode=strDPCode;
	}

	public String getStrDPName(){
		return strDPName;
	}
	public void setStrDPName(String strDPName){
		this.strDPName=strDPName;
	}
	
	public String getStrOperational(){
		return strOperational;
	}
	public void setStrOperational(String strOperational){
		this.strOperational=strOperational;
	}

	public List<clsPOSDeliveryBoyChargesBean> getListDeliveryBoyCharges() {
		return listDeliveryBoyCharges;
	}
	public void setListDeliveryBoyCharges(List<clsPOSDeliveryBoyChargesBean> listDeliveryBoyCharges) {
		this.listDeliveryBoyCharges = listDeliveryBoyCharges;
	}

	


}
