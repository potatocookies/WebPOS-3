package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.sanguine.base.model.clsBaseModel;



@Entity
@Table(name = "tblreasonmaster")
@IdClass(clsReasonMasterModel_ID.class)
@NamedQueries({ 
	@NamedQuery(name = "getALLReason", query = "select m.strReasonCode,m.strReasonName"
		+ " from clsReasonMasterModel m where m.strClientCode=:clientCode"),
		
		@NamedQuery(name = "getReason", query = "from clsReasonMasterModel where strReasonCode=:reasonCode and strClientCode=:clientCode")
})



public class clsReasonMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsReasonMasterModel()
    {
    }
    
    public clsReasonMasterModel(clsReasonMasterModel_ID objModelID)
    {
	strReasonCode = objModelID.getStrReasonCode();
	strClientCode = objModelID.getStrClientCode();
    }
   
    @CollectionOfElements(fetch=FetchType.EAGER)
    @Id
    @AttributeOverrides(
    {
    	@AttributeOverride(name = "strReasonCode", column = @Column(name = "strReasonCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")) 
    })
   
    // Variable Declaration
    @Column(name = "strReasonCode")
    private String strReasonCode;
    
    @Column(name = "strReasonName")
    private String strReasonName;
    
    @Column(name = "strStkIn")
    private String strStkIn;
    
    @Column(name = "strStkOut")
    private String strStkOut;
    
    @Column(name = "strVoidBill")
    private String strVoidBill;
    
    @Column(name = "strModifyBill")
    private String strModifyBill;
    
    @Column(name = "strTransferEntry")
    private String strTransferEntry;
    
    @Column(name = "strTransferType")
    private String strTransferType;
    
    @Column(name = "strPSP")
    private String strPSP;
    
    @Column(name = "strKot")
    private String strKot;
    
    @Column(name = "strCashMgmt")
    private String strCashMgmt;
    
    @Column(name = "strVoidStkIn")
    private String strVoidStkIn;
    
    @Column(name = "strVoidStkOut")
    private String strVoidStkOut;
    
    @Column(name = "strUnsettleBill")
    private String strUnsettleBill;
    
    @Column(name = "strComplementary")
    private String strComplementary;
    
    @Column(name = "strDiscount")
    private String strDiscount;
    
    @Column(name = "strUserCreated")
    private String strUserCreated;
    
    @Column(name = "strUserEdited")
    private String strUserEdited;
    
    @Column(name = "dteDateCreated")
    private String dteDateCreated;
    
    @Column(name = "dteDateEdited")
    private String dteDateEdited;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    @Column(name = "strDataPostFlag")
    private String strDataPostFlag;
    
    @Column(name = "strNCKOT")
    private String strNCKOT;
    
    @Column(name = "strVoidAdvOrder")
    private String strVoidAdvOrder;
    
    @Column(name = "strReprint")
    private String strReprint;
    
    @Column(name="strMoveKOT")
    private String strMoveKOT;
    
    @Column(name="strHashTagLoyalty")
    private String strHashTagLoyalty;
    
    @Column(name="strOperational")
    private String strOperational;

    
    // Setter-Getter Methods
    public String getStrReasonCode()
    {
	return strReasonCode;
    }
    
    public void setStrReasonCode(String strReasonCode)
    {
	this.strReasonCode = (String) setDefaultValue(strReasonCode, "NA");
    }
    
    public String getStrReasonName()
    {
	return strReasonName;
    }
    
    public void setStrReasonName(String strReasonName)
    {
	this.strReasonName = (String) setDefaultValue(strReasonName, "NA");
    }
    
    public String getStrStkIn()
    {
	return strStkIn;
    }
    
    public void setStrStkIn(String strStkIn)
    {
	this.strStkIn = (String) setDefaultValue(strStkIn, "NA");
    }
    
    public String getStrStkOut()
    {
	return strStkOut;
    }
    
    public void setStrStkOut(String strStkOut)
    {
	this.strStkOut = (String) setDefaultValue(strStkOut, "NA");
    }
    
    public String getStrVoidBill()
    {
	return strVoidBill;
    }
    
    public void setStrVoidBill(String strVoidBill)
    {
	this.strVoidBill = (String) setDefaultValue(strVoidBill, "NA");
    }
    
    public String getStrModifyBill()
    {
	return strModifyBill;
    }
    
    public void setStrModifyBill(String strModifyBill)
    {
	this.strModifyBill = (String) setDefaultValue(strModifyBill, "NA");
    }
    
    public String getStrTransferEntry()
    {
	return strTransferEntry;
    }
    
    public void setStrTransferEntry(String strTransferEntry)
    {
	this.strTransferEntry = (String) setDefaultValue(strTransferEntry, "NA");
    }
    
    public String getStrTransferType()
    {
	return strTransferType;
    }
    
    public void setStrTransferType(String strTransferType)
    {
	this.strTransferType = (String) setDefaultValue(strTransferType, "NA");
    }
    
    public String getStrPSP()
    {
	return strPSP;
    }
    
    public void setStrPSP(String strPSP)
    {
	this.strPSP = (String) setDefaultValue(strPSP, "NA");
    }
    
    public String getStrKot()
    {
	return strKot;
    }
    
    public void setStrKot(String strKot)
    {
	this.strKot = (String) setDefaultValue(strKot, "NA");
    }
    
    public String getStrCashMgmt()
    {
	return strCashMgmt;
    }
    
    public void setStrCashMgmt(String strCashMgmt)
    {
	this.strCashMgmt = (String) setDefaultValue(strCashMgmt, "NA");
    }
    
    public String getStrVoidStkIn()
    {
	return strVoidStkIn;
    }
    
    public void setStrVoidStkIn(String strVoidStkIn)
    {
	this.strVoidStkIn = (String) setDefaultValue(strVoidStkIn, "NA");
    }
    
    public String getStrVoidStkOut()
    {
	return strVoidStkOut;
    }
    
    public void setStrVoidStkOut(String strVoidStkOut)
    {
	this.strVoidStkOut = (String) setDefaultValue(strVoidStkOut, "NA");
    }
    
    public String getStrUnsettleBill()
    {
	return strUnsettleBill;
    }
    
    public void setStrUnsettleBill(String strUnsettleBill)
    {
	this.strUnsettleBill = (String) setDefaultValue(strUnsettleBill, "NA");
    }
    
    public String getStrComplementary()
    {
	return strComplementary;
    }
    
    public void setStrComplementary(String strComplementary)
    {
	this.strComplementary = (String) setDefaultValue(strComplementary, "NA");
    }
    
    public String getStrDiscount()
    {
	return strDiscount;
    }
    
    public void setStrDiscount(String strDiscount)
    {
	this.strDiscount = (String) setDefaultValue(strDiscount, "NA");
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
    
    public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = (String) setDefaultValue(strClientCode, "NA");
    }
    
    public String getStrDataPostFlag()
    {
	return strDataPostFlag;
    }
    
    public void setStrDataPostFlag(String strDataPostFlag)
    {
	this.strDataPostFlag = (String) setDefaultValue(strDataPostFlag, "NA");
    }
    
    public String getStrNCKOT()
    {
	return strNCKOT;
    }
    
    public void setStrNCKOT(String strNCKOT)
    {
	this.strNCKOT = (String) setDefaultValue(strNCKOT, "NA");
    }
    
    public String getStrVoidAdvOrder()
    {
	return strVoidAdvOrder;
    }
    
    public void setStrVoidAdvOrder(String strVoidAdvOrder)
    {
	this.strVoidAdvOrder = (String) setDefaultValue(strVoidAdvOrder, "NA");
    }
    
    public String getStrReprint()
    {
	return strReprint;
    }
    
    public void setStrReprint(String strReprint)
    {
	this.strReprint = (String) setDefaultValue(strReprint, "NA");
    }
    
  

	public String getStrMoveKOT() {
		return strMoveKOT;
	}

	public void setStrMoveKOT(String strMoveKOT) {
		this.strMoveKOT = strMoveKOT;
	}

	public String getStrHashTagLoyalty() {
		return strHashTagLoyalty;
	}

	public void setStrHashTagLoyalty(String strHashTagLoyalty) {
		this.strHashTagLoyalty = strHashTagLoyalty;
	}

	public String getStrOperational() {
		return strOperational;
	}

	public void setStrOperational(String strOperational) {
		this.strOperational = strOperational;
	}
    
}
