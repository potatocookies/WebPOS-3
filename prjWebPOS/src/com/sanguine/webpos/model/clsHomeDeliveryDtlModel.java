package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;
@Entity
@Table(name="tblhomedeldtl")
public class clsHomeDeliveryDtlModel extends clsBaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="strBillNo")
	private String strBillNo;
	
	@Column(name="strDPCode")
	private  String strDPCode;
	
	@Column(name="strClientCode")
	private  String strClientCode;
	
	@Column(name="strDataPostFlag")
	private String strDataPostFlag;
	
	@Column(name="strSettleYN")
	private  String strSettleYN;
	
	@Column(name="dteBillDate")
	private  String dteBillDate;
	
	@Column(name="dblDBIncentives")
	private  double dblDBIncentives;

	public String getStrBillNo() {
		return strBillNo;
	}

	public void setStrBillNo(String strBillNo) {
		this.strBillNo = strBillNo;
	}

	public String getStrDPCode() {
		return strDPCode;
	}

	public void setStrDPCode(String strDPCode) {
		this.strDPCode = strDPCode;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrSettleYN() {
		return strSettleYN;
	}

	public void setStrSettleYN(String strSettleYN) {
		this.strSettleYN = strSettleYN;
	}

	public String getDteBillDate() {
		return dteBillDate;
	}

	public void setDteBillDate(String dteBillDate) {
		this.dteBillDate = dteBillDate;
	}

	public double getDblDBIncentives() {
		return dblDBIncentives;
	}

	public void setDblDBIncentives(double dblDBIncentives) {
		this.dblDBIncentives = dblDBIncentives;
	}
	

}
