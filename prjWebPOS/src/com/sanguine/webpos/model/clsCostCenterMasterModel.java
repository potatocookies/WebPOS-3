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
@Table(name = "tblcostcentermaster")
@IdClass(clsCostCenterMasterModel_ID.class)

@NamedQueries(
		{ @NamedQuery (name="POSCostCenter" 
		, query="select m.strCostCenterCode,m.strCostCenterName from clsCostCenterMasterModel m where m.strClientCode=:clientCode" ),


		@NamedQuery(name = "getCostCenterMaster", query = "from clsCostCenterMasterModel where strCostCenterCode=:costCenterCode and strClientCode=:clientCode")}
	)



public class clsCostCenterMasterModel extends clsBaseModel implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public clsCostCenterMasterModel()
	{
	}

	public clsCostCenterMasterModel(clsCostCenterMasterModel_ID objModelID)
	{
		strCostCenterCode = objModelID.getStrCostCenterCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch=FetchType.EAGER)	
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "strCostCenterCode", column = @Column(name = "strCostCenterCode")),
			@AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
	})
//Variable Declaration
	@Column(name = "strCostCenterCode")
	private String	strCostCenterCode;

	@Column(name = "strCostCenterName")
	private String	strCostCenterName;

	@Column(name = "strPrinterPort")
	private String	strPrinterPort;

	@Column(name = "strSecondaryPrinterPort")
	private String	strSecondaryPrinterPort;

	@Column(name = "strPrintOnBothPrinters")
	private String	strPrintOnBothPrinters;

	@Column(name = "strUserCreated",updatable=false)
	private String	strUserCreated;

	@Column(name = "strUserEdited")
	private String	strUserEdited;

	@Column(name = "dteDateCreated",updatable=false)
	private String	dteDateCreated;

	@Column(name = "dteDateEdited")
	private String	dteDateEdited;

	@Column(name = "strClientCode")
	private String	strClientCode;

	@Column(name = "strDataPostFlag")
	private String	strDataPostFlag;

	@Column(name = "strLabelOnKOT")
	private String	strLabelOnKOT;

//Setter-Getter Methods
	public String getStrCostCenterCode()
	{
		return strCostCenterCode;
	}

	public void setStrCostCenterCode(String strCostCenterCode)
	{
		this.strCostCenterCode = (String) setDefaultValue(strCostCenterCode, "NA");
	}

	public String getStrCostCenterName()
	{
		return strCostCenterName;
	}

	public void setStrCostCenterName(String strCostCenterName)
	{
		this.strCostCenterName = (String) setDefaultValue(strCostCenterName, "NA");
	}

	public String getStrPrinterPort()
	{
		return strPrinterPort;
	}

	public void setStrPrinterPort(String strPrinterPort)
	{
		this.strPrinterPort = (String) setDefaultValue(strPrinterPort, "NA");
	}

	public String getStrSecondaryPrinterPort()
	{
		return strSecondaryPrinterPort;
	}

	public void setStrSecondaryPrinterPort(String strSecondaryPrinterPort)
	{
		this.strSecondaryPrinterPort = (String) setDefaultValue(strSecondaryPrinterPort, "NA");
	}

	public String getStrPrintOnBothPrinters()
	{
		return strPrintOnBothPrinters;
	}

	public void setStrPrintOnBothPrinters(String strPrintOnBothPrinters)
	{
		this.strPrintOnBothPrinters = (String) setDefaultValue(strPrintOnBothPrinters, "NA");
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

	public String getStrLabelOnKOT()
	{
		return strLabelOnKOT;
	}

	public void setStrLabelOnKOT(String strLabelOnKOT)
	{
		this.strLabelOnKOT = (String) setDefaultValue(strLabelOnKOT, "NA");
	}


}
