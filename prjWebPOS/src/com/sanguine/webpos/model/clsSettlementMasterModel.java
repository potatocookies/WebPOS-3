
package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name = "tblsettelmenthd")
@IdClass(clsSettlementMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllSettlementMaster", 
query = "select m.strSettelmentCode,m.strSettelmentDesc " 
		+ "from clsSettlementMasterModel m where m.strClientCode=:clientCode "),

		@NamedQuery(name = "getSettlementMaster", 
		query = "from clsSettlementMasterModel where strSettelmentCode=:settlementCode and strClientCode=:clientCode")  })
public class clsSettlementMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsSettlementMasterModel()
    {
    }
    
    public clsSettlementMasterModel(clsSettlementMasterModel_ID objModelID)
    {
    	strSettelmentCode = objModelID.getStrSettelmentCode();
    	strClientCode = objModelID.getStrClientCode();
	
    }
    
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strSettelmentCode", column = @Column(name = "strSettelmentCode")),
      @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))})
    // Variable Declaration
    @Column(name = "strSettelmentCode")
    private String strSettelmentCode;
    
    @Column(name = "strSettelmentDesc")
    private String strSettelmentDesc;
    
    @Column(name = "strSettelmentType")
    private String strSettelmentType;
    
    @Column(name = "strApplicable")
    private String strApplicable;
    
    @Column(name = "strBilling")
    private String strBilling;
    
    @Column(name = "strAdvanceReceipt")
    private String strAdvanceReceipt;
    
    @Column(name = "dblConvertionRatio")
    private double dblConvertionRatio;
    
    @Column(name = "strUserCreated", updatable=false)
    private String strUserCreated;
    
    @Column(name = "strUserEdited")
    private String strUserEdited;
    
    @Column(name = "dteDateCreated", updatable=false)
    private String dteDateCreated;
    
    @Column(name = "dteDateEdited")
    private String dteDateEdited;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    @Column(name = "strDataPostFlag")
    private String strDataPostFlag;
    
    
    @Column(name = "strAccountCode")
    private String strAccountCode;
    
    @Column(name = "strBillPrintOnSettlement")
    private String strBillPrintOnSettlement;
    
    @Column(name="strCreditReceiptYN")
    private String strCreditReceiptYN;
    
    @Column(name="dblThirdPartyComission")
    private double dblThirdPartyComission;
    
    @Column(name="strCustomerSelectionOnBillSettlement")
	private String strCustomerSelectionOnBillSettlement;
	
    public String getStrCustomerSelectionOnBillSettlement()
	{
		return strCustomerSelectionOnBillSettlement;
	}

	public void setStrCustomerSelectionOnBillSettlement(String strCustomerSelectionOnBillSettlement)
	{
		this.strCustomerSelectionOnBillSettlement = strCustomerSelectionOnBillSettlement;
	}
    
    public double getDblThirdPartyComission() {
		return dblThirdPartyComission;
	}

	public void setDblThirdPartyComission(double dblThirdPartyComission) {
		this.dblThirdPartyComission = dblThirdPartyComission;
	}

	public String getStrComissionType() {
		return strComissionType;
	}

	public void setStrComissionType(String strComissionType) {
		this.strComissionType = strComissionType;
	}

	public String getStrComissionOn() {
		return strComissionOn;
	}

	public void setStrComissionOn(String strComissionOn) {
		this.strComissionOn = strComissionOn;
	}

	@Column(name="strComissionType")
    private String strComissionType;
    
    @Column(name="strComissionOn")
    private String strComissionOn;
    

	public String getStrCreditReceiptYN() {
		return strCreditReceiptYN;
	}

	public void setStrCreditReceiptYN(String strCreditReceiptYN) {
		this.strCreditReceiptYN = strCreditReceiptYN;
	}

	public String getStrSettelmentCode() {
		return strSettelmentCode;
	}

	public void setStrSettelmentCode(String strSettelmentCode) {
		this.strSettelmentCode = strSettelmentCode;
	}

	public String getStrSettelmentDesc() {
		return strSettelmentDesc;
	}

	public void setStrSettelmentDesc(String strSettelmentDesc) {
		this.strSettelmentDesc = strSettelmentDesc;
	}

	public String getStrSettelmentType() {
		return strSettelmentType;
	}

	public void setStrSettelmentType(String strSettelmentType) {
		this.strSettelmentType = strSettelmentType;
	}

	public String getStrApplicable() {
		return strApplicable;
	}

	public void setStrApplicable(String strApplicable) {
		this.strApplicable = strApplicable;
	}

	public String getStrBilling() {
		return strBilling;
	}

	public void setStrBilling(String strBilling) {
		this.strBilling = strBilling;
	}

	public String getStrAdvanceReceipt() {
		return strAdvanceReceipt;
	}

	public void setStrAdvanceReceipt(String strAdvanceReceipt) {
		this.strAdvanceReceipt = strAdvanceReceipt;
	}

	public double getDblConvertionRatio() {
		return dblConvertionRatio;
	}

	public void setDblConvertionRatio(double dblConvertionRatio) {
		this.dblConvertionRatio = dblConvertionRatio;
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

	public String getStrAccountCode() {
		return strAccountCode;
	}

	public void setStrAccountCode(String strAccountCode) {
		this.strAccountCode = strAccountCode;
	}

	public String getStrBillPrintOnSettlement() {
		return strBillPrintOnSettlement;
	}

	public void setStrBillPrintOnSettlement(String strBillPrintOnSettlement) {
		this.strBillPrintOnSettlement = strBillPrintOnSettlement;
	}
    
    // Setter-Getter Methods
    
    
}
