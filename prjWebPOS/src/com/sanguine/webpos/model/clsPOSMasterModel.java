package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.HashSet;
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
@Table(name = "tblposmaster")
@IdClass(clsPOSMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllPOSMaster", 
query = "select m.strPosCode,m.strPosName " 
		+ "from clsPOSMasterModel m where m.strClientCode=:clientCode and m.strOperationalYN='Y' "),

		@NamedQuery(name = "getPOSMaster", query = "from clsPOSMasterModel where strPosCode=:posCode and strClientCode=:clientCode") })
public class clsPOSMasterModel extends clsBaseModel  implements Serializable
{
	private static final long serialVersionUID = 1L;

	public clsPOSMasterModel()
	{
	}

	public clsPOSMasterModel(clsPOSMasterModel_ID objModelID)
	{
		strPosCode = objModelID.getStrPosCode();
		strClientCode=objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblpossettlementdtl" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strPOSCode")})
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "strPosCode", column = @Column(name = "strPosCode")),
		@AttributeOverride(name="strClientCode",column= @Column(name="strClientCode")) })
	
	Set<clsPosSettlementDetailsModel> listsettlementDtl = new HashSet<clsPosSettlementDetailsModel>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblreordertime" , joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strPOSCode")})
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "strPosCode", column = @Column(name = "strPosCode")),
		@AttributeOverride(name="strClientCode",column= @Column(name="strClientCode")) })
	
	Set<clsReorderTimeModel> listReorderTimeDtl = new HashSet<clsReorderTimeModel>();
	
	

	public Set<clsPosSettlementDetailsModel> getListsettlementDtl() {
		return listsettlementDtl;
	}

	public void setListsettlementDtl(
			Set<clsPosSettlementDetailsModel> listsettlementDtl) {
		this.listsettlementDtl = listsettlementDtl;
	}

	public Set<clsReorderTimeModel> getListReorderTimeDtl() {
		return listReorderTimeDtl;
	}

	public void setListReorderTimeDtl(Set<clsReorderTimeModel> listReorderTimeDtl) {
		this.listReorderTimeDtl = listReorderTimeDtl;
	}

	// Variable Declaration
	@Column(name = "strPosCode")
	private String strPosCode;

	@Column(name = "strClientCode")
	private String strClientCode;
	
	@Column(name = "strPosName")
	private String strPosName;

	@Column(name = "strPosType")
	private String strPosType;

	@Column(name = "strDebitCardTransactionYN")
	private String strDebitCardTransactionYN;

	@Column(name = "strPropertyPOSCode")
	private String strPropertyPOSCode;

	@Column(name = "strUserCreated", updatable = false)
	private String strUserCreated;

	@Column(name = "strUserEdited")
	private String strUserEdited;

	@Column(name = "dteDateCreated", updatable = false)
	private String dteDateCreated;

	@Column(name = "dteDateEdited")
	private String dteDateEdited;

	@Column(name = "strCounterWiseBilling")
	private String strCounterWiseBilling;

	@Column(name = "strDelayedSettlementForDB")
	private String strDelayedSettlementForDB;

	@Column(name = "strBillPrinterPort")
	private String strBillPrinterPort;

	@Column(name = "strAdvReceiptPrinterPort")
	private String strAdvReceiptPrinterPort;

	@Column(name = "strOperationalYN")
	private String strOperationalYN;

	@Column(name = "strPrintVatNo")
	private String strPrintVatNo;

	@Column(name = "strPrintServiceTaxNo")
	private String strPrintServiceTaxNo;

	@Column(name = "strVatNo")
	private String strVatNo;

	@Column(name = "strServiceTaxNo")
	private String strServiceTaxNo;

	@Column(name = "strRoundOff")
	private String strRoundOff;

	@Column(name = "strTip")
	private String strTip;

	@Column(name = "strDiscount")
	private String strDiscount;

	@Column(name = "strWSLocationCode")
	private String strWSLocationCode;

	@Column(name = "strExciseLicenceCode")
	private String strExciseLicenceCode;

	@Column(name = "strEnableShift")
	private String strEnableShift;

	@Column(name="strPlayZonePOS")
	private String strPlayZonePOS;
	
	
	public String getStrPlayZonePOS() {
		return strPlayZonePOS;
	}

	public void setStrPlayZonePOS(String strPlayZonePOS) {
		this.strPlayZonePOS = strPlayZonePOS;
	}

	// Setter-Getter Methods
	public String getStrPosCode()
	{
		return strPosCode;
	}

	public void setStrPosCode(String strPosCode)
	{
		this.strPosCode = (String) setDefaultValue(strPosCode, " ");
	}

	public String getStrPosName()
	{
		return strPosName;
	}

	public void setStrPosName(String strPosName)
	{
		this.strPosName = (String) setDefaultValue(strPosName, " ");
	}

	public String getStrPosType()
	{
		return strPosType;
	}

	public void setStrPosType(String strPosType)
	{
		this.strPosType = (String) setDefaultValue(strPosType, " ");
	}

	public String getStrDebitCardTransactionYN()
	{
		return strDebitCardTransactionYN;
	}

	public void setStrDebitCardTransactionYN(String strDebitCardTransactionYN)
	{
		this.strDebitCardTransactionYN = (String) setDefaultValue(strDebitCardTransactionYN, " ");
	}

	public String getStrPropertyPOSCode()
	{
		return strPropertyPOSCode;
	}

	public void setStrPropertyPOSCode(String strPropertyPOSCode)
	{
		this.strPropertyPOSCode = (String) setDefaultValue(strPropertyPOSCode, " ");
	}

	public String getStrUserCreated()
	{
		return strUserCreated;
	}

	public void setStrUserCreated(String strUserCreated)
	{
		this.strUserCreated = (String) setDefaultValue(strUserCreated, " ");
	}

	public String getStrUserEdited()
	{
		return strUserEdited;
	}

	public void setStrUserEdited(String strUserEdited)
	{
		this.strUserEdited = (String) setDefaultValue(strUserEdited, " ");
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

	public String getStrCounterWiseBilling()
	{
		return strCounterWiseBilling;
	}

	public void setStrCounterWiseBilling(String strCounterWiseBilling)
	{
		this.strCounterWiseBilling = (String) setDefaultValue(strCounterWiseBilling, " ");
	}

	public String getStrDelayedSettlementForDB()
	{
		return strDelayedSettlementForDB;
	}

	public void setStrDelayedSettlementForDB(String strDelayedSettlementForDB)
	{
		this.strDelayedSettlementForDB = (String) setDefaultValue(strDelayedSettlementForDB, " ");
	}

	public String getStrBillPrinterPort()
	{
		return strBillPrinterPort;
	}

	public void setStrBillPrinterPort(String strBillPrinterPort)
	{
		this.strBillPrinterPort = (String) setDefaultValue(strBillPrinterPort, " ");
	}

	public String getStrAdvReceiptPrinterPort()
	{
		return strAdvReceiptPrinterPort;
	}

	public void setStrAdvReceiptPrinterPort(String strAdvReceiptPrinterPort)
	{
		this.strAdvReceiptPrinterPort = (String) setDefaultValue(strAdvReceiptPrinterPort, " ");
	}

	public String getStrOperationalYN()
	{
		return strOperationalYN;
	}

	public void setStrOperationalYN(String strOperationalYN)
	{
		this.strOperationalYN = (String) setDefaultValue(strOperationalYN, " ");
	}

	public String getStrPrintVatNo()
	{
		return strPrintVatNo;
	}

	public void setStrPrintVatNo(String strPrintVatNo)
	{
		this.strPrintVatNo = (String) setDefaultValue(strPrintVatNo, " ");
	}

	public String getStrPrintServiceTaxNo()
	{
		return strPrintServiceTaxNo;
	}

	public void setStrPrintServiceTaxNo(String strPrintServiceTaxNo)
	{
		this.strPrintServiceTaxNo = (String) setDefaultValue(strPrintServiceTaxNo, " ");
	}

	public String getStrVatNo()
	{
		return strVatNo;
	}

	public void setStrVatNo(String strVatNo)
	{
		this.strVatNo = (String) setDefaultValue(strVatNo, " ");
	}

	public String getStrServiceTaxNo()
	{
		return strServiceTaxNo;
	}

	public void setStrServiceTaxNo(String strServiceTaxNo)
	{
		this.strServiceTaxNo = (String) setDefaultValue(strServiceTaxNo, " ");
	}

	public String getStrRoundOff()
	{
		return strRoundOff;
	}

	public void setStrRoundOff(String strRoundOff)
	{
		this.strRoundOff = (String) setDefaultValue(strRoundOff, " ");
	}

	public String getStrTip()
	{
		return strTip;
	}

	public void setStrTip(String strTip)
	{
		this.strTip = (String) setDefaultValue(strTip, " ");
	}

	public String getStrDiscount()
	{
		return strDiscount;
	}

	public void setStrDiscount(String strDiscount)
	{
		this.strDiscount = (String) setDefaultValue(strDiscount, " ");
	}

	public String getStrWSLocationCode()
	{
		return strWSLocationCode;
	}

	public void setStrWSLocationCode(String strWSLocationCode)
	{
		this.strWSLocationCode = (String) setDefaultValue(strWSLocationCode, " ");
	}

	public String getStrExciseLicenceCode()
	{
		return strExciseLicenceCode;
	}

	public void setStrExciseLicenceCode(String strExciseLicenceCode)
	{
		this.strExciseLicenceCode = (String) setDefaultValue(strExciseLicenceCode, " ");
	}

	public String getStrEnableShift()
	{
		return strEnableShift;
	}

	public void setStrEnableShift(String strEnableShift)
	{
		this.strEnableShift = (String) setDefaultValue(strEnableShift, " ");
	}
	
	

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}


}
