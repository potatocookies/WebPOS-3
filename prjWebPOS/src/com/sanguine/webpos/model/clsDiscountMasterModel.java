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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;

	

@Entity
@Table(name = "tbldischd")
@IdClass(clsDiscountMasterModel_ID.class)
@NamedQueries({
 @NamedQuery(name = "getDiscountMaster", 
        		query = "from clsDiscountMasterModel where strDiscCode=:discCode and strClientCode=:clientCode")})

public class clsDiscountMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsDiscountMasterModel()
    {
    }
    
    public clsDiscountMasterModel(clsDiscountMasterModel_ID objModelID)
    {
    	strDiscCode = objModelID.getStrDiscCode();
	strClientCode = objModelID.getStrClientCode();
	
    }
 
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tbldiscdtl", joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strDiscCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strDiscCode",column=@Column(name="strDiscCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})
	//List<clsTaxSettlementDetailsModel> listsettlementDtl = new ArrayList<clsTaxSettlementDetailsModel>();
	List<clsDiscountDetailsModel> listDiscountDtl = new ArrayList<clsDiscountDetailsModel>();
	
    
    // Variable Declaration
    @Column(name = "strDiscCode")
    private String strDiscCode;
    
    @Column(name = "strDiscName")
    private String strDiscName;
    
    @Column(name = "strPOSCode")
    private String strPOSCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    @Column(name = "strDiscOn")
    private String strDiscOn;
    
    @Column(name = "dteFromDate")
    private String dteFromDate;
    
    @Column(name = "dteToDate")
    private String dteToDate;
    
    @Column(name = "strUserCreated")
    private String strUserCreated;
    
    @Column(name = "strUserEdited")
    private String strUserEdited;
    
    @Column(name = "dteDateCreated")
    private String dteDateCreated;
    
    @Column(name = "dteDateEdited")
    private String dteDateEdited;
    
    @Column(name = "strDataPostFlag")
    private String strDataPostFlag;
    
    
    @Column(name = "strDineIn")
    private String strDineIn;
    
    @Column(name = "strHomeDelivery")
    private String strHomeDelivery;
    
    @Column(name = "strTakeAway")
    private String strTakeAway;
   
    public String getStrDiscCode() {
		return strDiscCode;
	}

	public void setStrDiscCode(String strDiscCode) {
		this.strDiscCode = strDiscCode;
	}

	public String getStrDiscName() {
		return strDiscName;
	}

	public void setStrDiscName(String strDiscName) {
		this.strDiscName = strDiscName;
	}

	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public String getStrDiscOn() {
		return strDiscOn;
	}

	public void setStrDiscOn(String strDiscOn) {
		this.strDiscOn = strDiscOn;
	}

	public String getDteFromDate() {
		return dteFromDate;
	}

	public void setDteFromDate(String dteFromDate) {
		this.dteFromDate = dteFromDate;
	}

	public String getDteToDate() {
		return dteToDate;
	}

	public void setDteToDate(String dteToDate) {
		this.dteToDate = dteToDate;
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

	

	public List<clsDiscountDetailsModel> getListDiscountDtl() {
		return listDiscountDtl;
	}

	public void setListDiscountDtl(List<clsDiscountDetailsModel> listDiscountDtl) {
		this.listDiscountDtl = listDiscountDtl;
	}

	public String getStrDineIn()
	{
		return strDineIn;
	}

	public void setStrDineIn(String strDineIn)
	{
		this.strDineIn = strDineIn;
	}

	public String getStrHomeDelivery()
	{
		return strHomeDelivery;
	}

	public void setStrHomeDelivery(String strHomeDelivery)
	{
		this.strHomeDelivery = strHomeDelivery;
	}

	public String getStrTakeAway()
	{
		return strTakeAway;
	}

	public void setStrTakeAway(String strTakeAway)
	{
		this.strTakeAway = strTakeAway;
	}


    
}
