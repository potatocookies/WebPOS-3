package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsCustomerAreaMasterAmountModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strBuildingCode")
	private String strBuildingCode;
	
	 @Column(name = "strClientCode")
	    private String strClientCode;
	 
	 @Column(name = "dblBillAmount")
	    private double dblBillAmount;
	 
	 @Column(name = "dblBillAmount1")
	    private double dblBillAmount1;
	 
	 @Column(name = "dblDeliveryCharges")
	    private double dblDeliveryCharges;

	 public clsCustomerAreaMasterAmountModel_ID(){}	
	public clsCustomerAreaMasterAmountModel_ID(String strBuildingCode,String strClientCode,double dblBillAmount,double dblBillAmount1, double dblDeliveryCharges)
	{
		this.strBuildingCode=strBuildingCode;
		this.strClientCode = strClientCode;
		this.dblBillAmount = dblBillAmount;
		this.dblBillAmount1 = dblBillAmount1;
		this.dblDeliveryCharges = dblDeliveryCharges;
	}

//Setter-Getter Methods
	public String getStrBuildingCode(){
		return strBuildingCode;
	}
	public void setStrBuildingCode(String strBuildingCode){
		this. strBuildingCode = strBuildingCode;
	}


public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	
	
	public double getDblBillAmount() {
		return dblBillAmount;
	}
	public void setDblBillAmount(double dblBillAmount) {
		this.dblBillAmount = dblBillAmount;
	}
	public double getDblBillAmount1() {
		return dblBillAmount1;
	}
	public void setDblBillAmount1(double dblBillAmount1) {
		this.dblBillAmount1 = dblBillAmount1;
	}
	public double getDblDeliveryCharges() {
		return dblDeliveryCharges;
	}
	public void setDblDeliveryCharges(double dblDeliveryCharges) {
		this.dblDeliveryCharges = dblDeliveryCharges;
	}
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsCustomerAreaMasterAmountModel_ID objModelId = (clsCustomerAreaMasterAmountModel_ID)obj;
		if( this.strBuildingCode.equals(objModelId.getStrBuildingCode())&& this.strClientCode.equals(objModelId.getStrClientCode())&& this.dblBillAmount1==(objModelId.getDblBillAmount())&& this.dblBillAmount==(objModelId.getDblBillAmount1())&& this.dblDeliveryCharges==(objModelId.getDblDeliveryCharges()))
				{
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return  this.strBuildingCode.hashCode()+this.strClientCode.hashCode();//+this.dblBillAmount.hashCode()+this.dblBillAmount1.hashCode()+this.dblDeliveryCharges.hashCode();
	}

}
