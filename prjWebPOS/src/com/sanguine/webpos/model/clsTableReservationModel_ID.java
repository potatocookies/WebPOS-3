
package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsTableReservationModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strResCode")
	private String strResCode;

	@Column(name="strCustomerCode")
	private String strCustomerCode;
	
	@Column(name="strClientCode")
	private String strClientCode;


	public clsTableReservationModel_ID(){}
	public clsTableReservationModel_ID(String strResCode,String strCustomerCode,String strClientCode){
		this.strResCode=strResCode;
		this.strCustomerCode=strCustomerCode;
		this.strClientCode=strClientCode;
	}
	
	
	
	public String getStrResCode() {
		return strResCode;
	}
	public void setStrResCode(String strResCode) {
		this.strResCode = strResCode;
	}
	public String getStrCustomerCode() {
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}
	public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}


}
