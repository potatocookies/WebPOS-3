package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name = "tblmenuitempricingdtl")
@NamedQueries({@NamedQuery(name="getMenuItemPricing",query="from clsPricingMasterHdModel where longPricingId=:longPricingId and strClientCode=:clientCode"),
@NamedQuery(name = "getALLItemPricing", query = "select m.strItemCode,m.strItemName"
+ " from clsPricingMasterHdModel m where m.strMenuCode=:clientCode"),
@NamedQuery(name="getItemPricingLoad",query="from clsPricingMasterHdModel  where strItemCode=:ItemCode and strClientCode=:clientCode"),
@NamedQuery(name="getItemList",query="from clsPricingMasterHdModel  where strMenuCode=:MenuCode and strClientCode=:clientCode"),
@NamedQuery(name="checkDuplicateItemPricing",query="from clsPricingMasterHdModel where (strPosCode= :strPosCode OR strPosCode='All' ) AND strItemCode= :strItemCode AND strAreaCode= :strAreaCode AND strHourlyPricing= :strHourlyPricing")
})
/*	@NamedQuery(name="getItemList",query="select m.strItemCode,m.strItemName from clsPricingMasterHdModel m where strMenuCode=:MenuCode and strClientCode=:clientCode")
	})*/
@IdClass(clsPricingMasterModel_ID.class)
public class clsPricingMasterHdModel extends clsBaseModel implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public clsPricingMasterHdModel()
	{
	}

	public clsPricingMasterHdModel(clsPricingMasterModel_ID objModelID)
	{
		longPricingId = objModelID.getLongPricingId();		
	}

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "longPricingId", column = @Column(name = "longPricingId"))			
	})
	@Column(name = "strItemCode")
	private String	strItemCode;

	@Column(name = "strItemName")
	private String	strItemName;

	@Column(name = "strPosCode")
	private String	strPosCode;

	@Column(name = "strMenuCode")
	private String	strMenuCode;

	@Column(name = "strPopular")
	private String	strPopular;

	@Column(name = "strPriceMonday")
	private String	strPriceMonday;

	@Column(name = "strPriceTuesday")
	private String	strPriceTuesday;

	@Column(name = "strPriceWednesday")
	private String	strPriceWednesday;

	@Column(name = "strPriceThursday")
	private String	strPriceThursday;

	@Column(name = "strPriceFriday")
	private String	strPriceFriday;

	@Column(name = "strPriceSaturday")
	private String	strPriceSaturday;

	@Column(name = "strPriceSunday")
	private String	strPriceSunday;

	@Column(name = "dteFromDate")
	private String	dteFromDate;

	@Column(name = "dteToDate")
	private String	dteToDate;

	@Column(name = "strAMPMFrom")
	private String	strAMPMFrom;

	@Column(name = "strAMPMTo")
	private String	strAMPMTo;

	@Column(name = "strCostCenterCode")
	private String	strCostCenterCode;

	@Column(name = "strTextColor")
	private String	strTextColor;

	@Column(name = "strUserCreated", updatable = false)
	private String	strUserCreated;

	@Column(name = "strUserEdited")
	private String	strUserEdited;

	@Column(name = "dteDateCreated", updatable = false)
	private String	dteDateCreated;

	@Column(name = "dteDateEdited")
	private String	dteDateEdited;

	@Column(name = "strAreaCode")
	private String	strAreaCode;

	@Column(name = "strSubMenuHeadCode")
	private String	strSubMenuHeadCode;

	@Column(name = "strHourlyPricing")
	private String	strHourlyPricing;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "longPricingId")
	private long	longPricingId;
	
	@Column(name = "tmeTimeFrom")
	private String	tmeTimeFrom;
	
	@Column(name = "tmeTimeTo")
	private String	tmeTimeTo;
	
	@Column(name= "strClientCode")
	private String strClientCode;

public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	//Setter-Getter Methods
	public String getStrItemCode()
	{
		return strItemCode;
	}

	public void setStrItemCode(String strItemCode)
	{
		this.strItemCode = (String) setDefaultValue(strItemCode, "NA");
	}

	public String getStrItemName()
	{
		return strItemName;
	}

	public void setStrItemName(String strItemName)
	{
		this.strItemName = (String) setDefaultValue(strItemName, "NA");
	}

	public String getStrPosCode()
	{
		return strPosCode;
	}

	public void setStrPosCode(String strPosCode)
	{
		this.strPosCode = (String) setDefaultValue(strPosCode, "NA");
	}

	public String getStrMenuCode()
	{
		return strMenuCode;
	}

	public void setStrMenuCode(String strMenuCode)
	{
		this.strMenuCode = (String) setDefaultValue(strMenuCode, "NA");
	}

	public String getStrPopular()
	{
		return strPopular;
	}

	public void setStrPopular(String strPopular)
	{
		this.strPopular = (String) setDefaultValue(strPopular, "NA");
	}


	public String getDteFromDate()
	{
		return dteFromDate;
	}

	public void setDteFromDate(String dteFromDate)
	{
		this.dteFromDate = dteFromDate;
	}


	public String getStrPriceMonday() {
		return strPriceMonday;
	}

	public void setStrPriceMonday(String strPriceMonday) {
		this.strPriceMonday = strPriceMonday;
	}

	public String getStrPriceTuesday() {
		return strPriceTuesday;
	}

	public void setStrPriceTuesday(String strPriceTuesday) {
		this.strPriceTuesday = strPriceTuesday;
	}

	public String getStrPriceWednesday() {
		return strPriceWednesday;
	}

	public void setStrPriceWednesday(String strPriceWednesday) {
		this.strPriceWednesday = strPriceWednesday;
	}

	public String getStrPriceThursday() {
		return strPriceThursday;
	}

	public void setStrPriceThursday(String strPriceThursday) {
		this.strPriceThursday = strPriceThursday;
	}

	public String getStrPriceFriday() {
		return strPriceFriday;
	}

	public void setStrPriceFriday(String strPriceFriday) {
		this.strPriceFriday = strPriceFriday;
	}

	public String getStrPriceSaturday() {
		return strPriceSaturday;
	}

	public void setStrPriceSaturday(String strPriceSaturday) {
		this.strPriceSaturday = strPriceSaturday;
	}

	public String getStrPriceSunday() {
		return strPriceSunday;
	}

	public void setStrPriceSunday(String strPriceSunday) {
		this.strPriceSunday = strPriceSunday;
	}

	public String getDteToDate()
	{
		return dteToDate;
	}

	public void setDteToDate(String dteToDate)
	{
		this.dteToDate = dteToDate;
	}

	public String getStrAMPMFrom()
	{
		return strAMPMFrom;
	}

	public void setStrAMPMFrom(String strAMPMFrom)
	{
		this.strAMPMFrom = (String) setDefaultValue(strAMPMFrom, "AM");
	}

	public String getStrAMPMTo()
	{
		return strAMPMTo;
	}

	public void setStrAMPMTo(String strAMPMTo)
	{
		this.strAMPMTo = (String) setDefaultValue(strAMPMTo, "AM");
	}

	public String getStrCostCenterCode()
	{
		return strCostCenterCode;
	}

	public void setStrCostCenterCode(String strCostCenterCode)
	{
		this.strCostCenterCode = (String) setDefaultValue(strCostCenterCode, "NA");
	}

	public String getStrTextColor()
	{
		return strTextColor;
	}

	public void setStrTextColor(String strTextColor)
	{
		this.strTextColor = (String) setDefaultValue(strTextColor, "");
	}

	public String getStrUserCreated()
	{
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated)
	{
		this.strUserCreated = (String) setDefaultValue(strUserCreated, "NA");
	}

	public String getStrUserEdited()
	{
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited)
	{
		this.strUserEdited = (String) setDefaultValue(strUserEdited, "NA");
	}

	public String getDteDateCreated()
	{
		return dteDateCreated;
	}

	public void setDteDateCreated(String dteDateCreated)
	{
		this.dteDateCreated = dteDateCreated;
	}

	public String getDteDateEdited()
	{
		return dteDateEdited;
	}

	public void setDteDateEdited(String dteDateEdited)
	{
		this.dteDateEdited = dteDateEdited;
	}

	public String getStrAreaCode()
	{
		return strAreaCode;
	}

	public void setStrAreaCode(String strAreaCode)
	{
		this.strAreaCode = (String) setDefaultValue(strAreaCode, "NA");
	}

	public String getStrSubMenuHeadCode()
	{
		return strSubMenuHeadCode;
	}

	public void setStrSubMenuHeadCode(String strSubMenuHeadCode)
	{
		this.strSubMenuHeadCode = (String) setDefaultValue(strSubMenuHeadCode, "");
	}

	public String getStrHourlyPricing()
	{
		return strHourlyPricing;
	}

	public void setStrHourlyPricing(String strHourlyPricing)
	{
		this.strHourlyPricing = (String) setDefaultValue(strHourlyPricing, "NO");
	}

	public long getLongPricingId()
	{
		return longPricingId;
	}

	public void setLongPricingId(long longPricingId)
	{
		this.longPricingId = longPricingId;
	}	
	
	public String getTmeTimeFrom()
	{
		return tmeTimeFrom;
	}

	public void setTmeTimeFrom(String tmeTimeFrom)
	{
		this.tmeTimeFrom = tmeTimeFrom;
	}

	public String getTmeTimeTo()
	{
		return tmeTimeTo;
	}

	public void setTmeTimeTo(String tmeTimeTo)
	{
		this.tmeTimeTo = tmeTimeTo;
	}

	
}
