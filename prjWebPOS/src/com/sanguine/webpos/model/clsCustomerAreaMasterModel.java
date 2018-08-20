package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.base.model.clsBaseModel;



@Entity
@Table(name="tblbuildingmaster")
@NamedQueries({ 
	@NamedQuery(name = "getALLCustomerArea", query = "select m.strBuildingCode,m.strBuildingName,m.strAddress"
		+ " from clsCustomerAreaMasterModel m where m.strClientCode=:clientCode"),
	
	@NamedQuery(name = "getCustomerArea", query = "from clsCustomerAreaMasterModel where strBuildingCode=:buildingCode and strClientCode=:clientCode")
})




@IdClass(clsCustomerAreaMasterModel_ID.class) 

public class clsCustomerAreaMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsCustomerAreaMasterModel(){}

	public clsCustomerAreaMasterModel(clsCustomerAreaMasterModel_ID objModelID){
		strBuildingCode = objModelID.getStrBuildingCode();
		strClientCode = objModelID.getStrClientCode();
	}
	@CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(name="tblareawisedc" , joinColumns={@JoinColumn(name="strBuildingCode"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		 @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")), @AttributeOverride(name="strBuildingCode",column=@Column(name="strBuildingCode"))
	})
	
	//List<clsCustomerAreaMasterAmountModel> listcustomerDtl = new ArrayList<clsCustomerAreaMasterAmountModel>();

	Set<clsCustomerAreaMasterAmountModel> listcustomerDtl = new HashSet<clsCustomerAreaMasterAmountModel>();
	
	
	
	
	public Set<clsCustomerAreaMasterAmountModel> getListcustomerDtl() {
		return listcustomerDtl;
	}

	public void setListcustomerDtl(
			Set<clsCustomerAreaMasterAmountModel> listcustomerDtl) {
		this.listcustomerDtl = listcustomerDtl;
	}
	
	//Variable Declaration
	@Column(name="strBuildingCode")
	private String strBuildingCode;

	@Column(name="strBuildingName")
	private String strBuildingName;

	@Column(name="strAddress")
	private String strAddress;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="dblHomeDeliCharge")
	private double dblHomeDeliCharge;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="dblDeliveryBoyPayOut")
	private double dblDeliveryBoyPayOut;

	@Column(name="dblHelperPayOut")
	private double dblHelperPayOut;

	@Column(name="strZoneCode")
	private String strZoneCode;

//Setter-Getter Methods
	public String getStrBuildingCode(){
		return strBuildingCode;
	}
	public void setStrBuildingCode(String strBuildingCode){
		this. strBuildingCode = (String) setDefaultValue( strBuildingCode, "NA");
	}

	public String getStrBuildingName(){
		return strBuildingName;
	}
	public void setStrBuildingName(String strBuildingName){
		this. strBuildingName = (String) setDefaultValue( strBuildingName, "NA");
	}

	public String getStrAddress(){
		return strAddress;
	}
	public void setStrAddress(String strAddress){
		this. strAddress = (String) setDefaultValue( strAddress, "NA");
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

	public double getDblHomeDeliCharge(){
		return dblHomeDeliCharge;
	}
	public void setDblHomeDeliCharge(double dblHomeDeliCharge){
		this. dblHomeDeliCharge = (Double) setDefaultValue( dblHomeDeliCharge, "0.0000");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public double getDblDeliveryBoyPayOut(){
		return dblDeliveryBoyPayOut;
	}
	public void setDblDeliveryBoyPayOut(double dblDeliveryBoyPayOut){
		this. dblDeliveryBoyPayOut = (Double) setDefaultValue( dblDeliveryBoyPayOut, "0.0000");
	}

	public double getDblHelperPayOut(){
		return dblHelperPayOut;
	}
	public void setDblHelperPayOut(double dblHelperPayOut){
		this. dblHelperPayOut = (Double) setDefaultValue( dblHelperPayOut, "0.0000");
	}

	public String getStrZoneCode(){
		return strZoneCode;
	}
	public void setStrZoneCode(String strZoneCode){
		this. strZoneCode = (String) setDefaultValue( strZoneCode, "NA");
	}



}
