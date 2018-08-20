package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Embeddable
public class clsCustomerAreaMasterAmountModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public clsCustomerAreaMasterAmountModel(){}



//Variable Declaration

	@Column(name="dblKilometers")
	private double dblKilometers;

	@Column(name="strSymbol")
	private String strSymbol;

	@Column(name="dblBillAmount")
	private double dblBillAmount;

	@Column(name="dblBillAmount1")
	private double dblBillAmount1;

	@Column(name="dblDeliveryCharges")
	private double dblDeliveryCharges;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;


	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strCustTypeCode")
	private String strCustTypeCode;

	
	public double getDblKilometers() {
		return dblKilometers;
	}

	public void setDblKilometers(double dblKilometers) {
		this.dblKilometers = dblKilometers;
	}

	public String getStrSymbol() {
		return strSymbol;
	}

	public void setStrSymbol(String strSymbol) {
		this.strSymbol = strSymbol;
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

	public String getStrUserCreated() {
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated) {
		this.strUserCreated = strUserCreated;
	}

	public String getStrUserEdited() {
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited) {
		this.strUserEdited = strUserEdited;
	}

	public String getDteDateCreated() {
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated) {
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited() {
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited) {
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}

	public String getStrCustTypeCode() {
		return strCustTypeCode;
	}

	public void setStrCustTypeCode(String strCustTypeCode) {
		this.strCustTypeCode = strCustTypeCode;
	}

//Setter-Getter Methods
	
	
	
	

	/*public double getDblKilometers(){
		return dblKilometers;
	}
	public void setDblKilometers(double dblKilometers){
		this. dblKilometers = (Double) setDefaultValue( dblKilometers, "0.0000");
	}

	public String getStrSymbol(){
		return strSymbol;
	}
	public void setStrSymbol(String strSymbol){
		this. strSymbol = (String) setDefaultValue( strSymbol, "NA");
	}

	public double getDblBillAmount(){
		return dblBillAmount;
	}
	public void setDblBillAmount(double dblBillAmount){
		this. dblBillAmount = (Double) setDefaultValue( dblBillAmount, "0.0000");
	}

	public double getDblBillAmount1(){
		return dblBillAmount1;
	}
	public void setDblBillAmount1(double dblBillAmount1){
		this. dblBillAmount1 = (Double) setDefaultValue( dblBillAmount1, "0.0000");
	}

	public double getDblDeliveryCharges(){
		return dblDeliveryCharges;
	}
	public void setDblDeliveryCharges(double dblDeliveryCharges){
		this. dblDeliveryCharges = (Double) setDefaultValue( dblDeliveryCharges, "0.0000");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}


	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrCustTypeCode(){
		return strCustTypeCode;
	}
	public void setStrCustTypeCode(String strCustTypeCode){
		this. strCustTypeCode = (String) setDefaultValue( strCustTypeCode, "NA");
	}
*/

/*//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}*/

}
