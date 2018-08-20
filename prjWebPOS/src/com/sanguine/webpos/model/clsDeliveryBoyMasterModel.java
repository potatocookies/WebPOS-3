package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tbldeliverypersonmaster")
@IdClass(clsDeliveryBoyMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllDeliveryBoyMaster", 
query = "select m.strDPCode,m.strDPName " 
		+ "from clsDeliveryBoyMasterModel m where m.strClientCode=:clientCode"),

		@NamedQuery(name = "getDeliveryBoyMaster", 
		query = "from clsDeliveryBoyMasterModel where strDPCode=:dpCode and strClientCode=:clientCode") })
public class clsDeliveryBoyMasterModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsDeliveryBoyMasterModel(){}

	public clsDeliveryBoyMasterModel(clsDeliveryBoyMasterModel_ID objModelID){
		strDPCode = objModelID.getStrDPCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblareawisedelboywisecharges" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strDeliveryBoyCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strDPCode",column=@Column(name="strDPCode")),
@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

	List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl = new ArrayList<clsDeliveryBoyChargesModel>();
	
	public List<clsDeliveryBoyChargesModel> getListDeliveryChargesDtl() {
		return listDeliveryChargesDtl;
	}

	public void setListDeliveryChargesDtl(
			List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl) {
		this.listDeliveryChargesDtl = listDeliveryChargesDtl;
	}

	//Variable Declaration
	@Column(name="strDPCode")
	private String strDPCode;

	@Column(name="strDPName")
	private String strDPName;

	@Column(name="strAddressLine1")
	private String strAddressLine1;

	@Column(name="strAddressLine2")
	private String strAddressLine2;

	@Column(name="strAddressLine3")
	private String strAddressLine3;

	@Column(name="strCity")
	private String strCity;

	@Column(name="strState")
	private String strState;

	@Column(name="intPinCode")
	private long intPinCode;

	@Column(name="intMobileNo")
	private long intMobileNo;

	@Column(name="strDeliveryArea")
	private String strDeliveryArea;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strOperational")
	private String strOperational;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="dblIncentiveAmt")
	private double dblIncentiveAmt;

	@Column(name="strDBCategoryCode")
	private String strDBCategoryCode;

//Setter-Getter Methods
	public String getStrDPCode(){
		return strDPCode;
	}
	public void setStrDPCode(String strDPCode){
		this. strDPCode = (String) setDefaultValue( strDPCode, "NA");
	}

	public String getStrDPName(){
		return strDPName;
	}
	public void setStrDPName(String strDPName){
		this. strDPName = (String) setDefaultValue( strDPName, "NA");
	}

	public String getStrAddressLine1(){
		return strAddressLine1;
	}
	public void setStrAddressLine1(String strAddressLine1){
		this. strAddressLine1 = (String) setDefaultValue( strAddressLine1, "NA");
	}

	public String getStrAddressLine2(){
		return strAddressLine2;
	}
	public void setStrAddressLine2(String strAddressLine2){
		this. strAddressLine2 = (String) setDefaultValue( strAddressLine2, "NA");
	}

	public String getStrAddressLine3(){
		return strAddressLine3;
	}
	public void setStrAddressLine3(String strAddressLine3){
		this. strAddressLine3 = (String) setDefaultValue( strAddressLine3, "NA");
	}

	public String getStrCity(){
		return strCity;
	}
	public void setStrCity(String strCity){
		this. strCity = (String) setDefaultValue( strCity, "NA");
	}

	public String getStrState(){
		return strState;
	}
	public void setStrState(String strState){
		this. strState = (String) setDefaultValue( strState, "NA");
	}

	public long getIntPinCode(){
		return intPinCode;
	}
	public void setIntPinCode(long intPinCode){
		this. intPinCode = (Long) setDefaultValue( intPinCode, "0");
	}

	public long getIntMobileNo(){
		return intMobileNo;
	}
	public void setIntMobileNo(long intMobileNo){
		this. intMobileNo = (Long) setDefaultValue( intMobileNo, "0");
	}

	public String getStrDeliveryArea(){
		return strDeliveryArea;
	}
	public void setStrDeliveryArea(String strDeliveryArea){
		this. strDeliveryArea = (String) setDefaultValue( strDeliveryArea, "NA");
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

	public String getStrOperational(){
		return strOperational;
	}
	public void setStrOperational(String strOperational){
		this. strOperational = (String) setDefaultValue( strOperational, "");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}

	public double getDblIncentiveAmt(){
		return dblIncentiveAmt;
	}
	public void setDblIncentiveAmt(double dblIncentiveAmt){
		this. dblIncentiveAmt = (Double) setDefaultValue( dblIncentiveAmt, "0.0000");
	}

	public String getStrDBCategoryCode(){
		return strDBCategoryCode;
	}
	public void setStrDBCategoryCode(String strDBCategoryCode){
		this. strDBCategoryCode = (String) setDefaultValue( strDBCategoryCode, "");
	}



}
