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
@Table(name = "tblcustomermaster")
@NamedQueries(
{
		@NamedQuery(name = "getALLCustomer", query = "select m.strCustomerCode,m.strCustomerName,m.longMobileNo,m.strArea"
				+ " from clsCustomerMasterModel m where m.strClientCode=:clientCode order by m.strCustomerName"),

		@NamedQuery(name = "getCustomerMaster", query = "from clsCustomerMasterModel where strCustomerCode=:customerCode and strClientCode=:clientCode")
})
@IdClass(clsCustomerMasterModel_ID.class)
public class clsCustomerMasterModel extends clsBaseModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	public clsCustomerMasterModel()
	{
	}

	public clsCustomerMasterModel(clsCustomerMasterModel_ID objModelID)
	{
		strCustomerCode = objModelID.getStrCustomerCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	@Id
	@AttributeOverrides(
	{
			@AttributeOverride(name = "strCustomerCode", column = @Column(name = "strCustomerCode")),
			@AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))
	})
	// Variable Declaration
	@Column(name = "strCustomerCode")
	private String strCustomerCode;

	@Column(name = "strCustomerName")
	private String strCustomerName;

	@Column(name = "strBuldingCode")
	private String strBuldingCode;

	@Column(name = "strBuildingName")
	private String strBuildingName;

	@Column(name = "strStreetName")
	private String strStreetName;

	@Column(name = "strLandmark")
	private String strLandmark;

	@Column(name = "strArea")
	private String strArea;

	@Column(name = "strCity")
	private String strCity;

	@Column(name = "strState")
	private String strState;

	@Column(name = "intPinCode")
	private String intPinCode;

	@Column(name = "longMobileNo")
	private String longMobileNo;

	@Column(name = "strOfficeBuildingCode")
	private String strOfficeBuildingCode;

	@Column(name = "strOfficeBuildingName")
	private String strOfficeBuildingName;

	@Column(name = "strOfficeStreetName")
	private String strOfficeStreetName;

	@Column(name = "strOfficeLandmark")
	private String strOfficeLandmark;

	@Column(name = "strOfficeArea")
	private String strOfficeArea;

	@Column(name = "strOfficeCity")
	private String strOfficeCity;

	@Column(name = "strOfficePinCode")
	private String strOfficePinCode;

	@Column(name = "strOfficeState")
	private String strOfficeState;

	@Column(name = "strOfficeNo")
	private String strOfficeNo;

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

	@Column(name = "strClientCode")
	private String strClientCode;

	@Column(name = "strOfficeAddress")
	private String strOfficeAddress;

	@Column(name = "strExternalCode")
	private String strExternalCode;

	@Column(name = "strCustomerType")
	private String strCustomerType;

	@Column(name = "dteDOB")
	private String dteDOB;

	@Column(name = "strGender")
	private String strGender;

	@Column(name = "dteAnniversary")
	private String dteAnniversary;

	@Column(name = "strEmailId")
	private String strEmailId;

	@Column(name = "strCRMId")
	private String strCRMId;

	@Column(name = "strCustAddress")
	private String strCustAddress;

	@Column(name = "strTempAddress")
	private String strTempAddress;

	@Column(name = "strTempStreet")
	private String strTempStreet;

	@Column(name = "strTempLandmark")
	private String strTempLandmark;

	@Column(name = "strGSTNo")
	private String strGSTNo;
	
	

	// Setter-Getter Methods
	public String getStrCustomerCode()
	{
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode)
	{
		this.strCustomerCode = (String) setDefaultValue(strCustomerCode, "NA");
	}

	public String getStrCustomerName()
	{
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName)
	{
		this.strCustomerName = (String) setDefaultValue(strCustomerName, "NA");
	}

	public String getStrBuldingCode()
	{
		return strBuldingCode;
	}

	public void setStrBuldingCode(String strBuldingCode)
	{
		this.strBuldingCode = (String) setDefaultValue(strBuldingCode, "NA");
	}

	public String getStrBuildingName()
	{
		return strBuildingName;
	}

	public void setStrBuildingName(String strBuildingName)
	{
		this.strBuildingName = (String) setDefaultValue(strBuildingName, "NA");
	}

	public String getStrStreetName()
	{
		return strStreetName;
	}

	public void setStrStreetName(String strStreetName)
	{
		this.strStreetName = (String) setDefaultValue(strStreetName, "NA");
	}

	public String getStrLandmark()
	{
		return strLandmark;
	}

	public void setStrLandmark(String strLandmark)
	{
		this.strLandmark = (String) setDefaultValue(strLandmark, "NA");
	}

	public String getStrArea()
	{
		return strArea;
	}

	public void setStrArea(String strArea)
	{
		this.strArea = (String) setDefaultValue(strArea, "NA");
	}

	public String getStrCity()
	{
		return strCity;
	}

	public void setStrCity(String strCity)
	{
		this.strCity = (String) setDefaultValue(strCity, "NA");
	}

	public String getStrState()
	{
		return strState;
	}

	public void setStrState(String strState)
	{
		this.strState = (String) setDefaultValue(strState, "NA");
	}

	public String getIntPinCode()
	{
		return intPinCode;
	}

	public void setIntPinCode(String intPinCode)
	{
		this.intPinCode = (String) setDefaultValue(intPinCode, "0");
	}

	public String getLongMobileNo()
	{
		return longMobileNo;
	}

	public void setLongMobileNo(String longMobileNo)
	{
		this.longMobileNo = (String) setDefaultValue(longMobileNo, "0");
		;
	}

	public String getStrOfficeBuildingCode()
	{
		return strOfficeBuildingCode;
	}

	public void setStrOfficeBuildingCode(String strOfficeBuildingCode)
	{
		this.strOfficeBuildingCode = (String) setDefaultValue(strOfficeBuildingCode, "NA");
	}

	public String getStrOfficeBuildingName()
	{
		return strOfficeBuildingName;
	}

	public void setStrOfficeBuildingName(String strOfficeBuildingName)
	{
		this.strOfficeBuildingName = (String) setDefaultValue(strOfficeBuildingName, "NA");
	}

	public String getStrOfficeStreetName()
	{
		return strOfficeStreetName;
	}

	public void setStrOfficeStreetName(String strOfficeStreetName)
	{
		this.strOfficeStreetName = (String) setDefaultValue(strOfficeStreetName, "NA");
	}

	public String getStrOfficeLandmark()
	{
		return strOfficeLandmark;
	}

	public void setStrOfficeLandmark(String strOfficeLandmark)
	{
		this.strOfficeLandmark = (String) setDefaultValue(strOfficeLandmark, "NA");
	}

	public String getStrOfficeArea()
	{
		return strOfficeArea;
	}

	public void setStrOfficeArea(String strOfficeArea)
	{
		this.strOfficeArea = (String) setDefaultValue(strOfficeArea, "NA");
	}

	public String getStrOfficeCity()
	{
		return strOfficeCity;
	}

	public void setStrOfficeCity(String strOfficeCity)
	{
		this.strOfficeCity = (String) setDefaultValue(strOfficeCity, "NA");
	}

	public String getStrOfficePinCode()
	{
		return strOfficePinCode;
	}

	public void setStrOfficePinCode(String strOfficePinCode)
	{
		this.strOfficePinCode = (String) setDefaultValue(strOfficePinCode, "NA");
	}

	public String getStrOfficeState()
	{
		return strOfficeState;
	}

	public void setStrOfficeState(String strOfficeState)
	{
		this.strOfficeState = (String) setDefaultValue(strOfficeState, "NA");
	}

	public String getStrOfficeNo()
	{
		return strOfficeNo;
	}

	public void setStrOfficeNo(String strOfficeNo)
	{
		this.strOfficeNo = (String) setDefaultValue(strOfficeNo, "NA");
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

	public String getStrDataPostFlag()
	{
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag)
	{
		this.strDataPostFlag = (String) setDefaultValue(strDataPostFlag, "NA");
	}

	public String getStrClientCode()
	{
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = (String) setDefaultValue(strClientCode, "NA");
	}

	public String getStrOfficeAddress()
	{
		return strOfficeAddress;
	}

	public void setStrOfficeAddress(String strOfficeAddress)
	{
		this.strOfficeAddress = (String) setDefaultValue(strOfficeAddress, "NA");
	}

	public String getStrExternalCode()
	{
		return strExternalCode;
	}

	public void setStrExternalCode(String strExternalCode)
	{
		this.strExternalCode = (String) setDefaultValue(strExternalCode, "NA");
	}

	public String getStrCustomerType()
	{
		return strCustomerType;
	}

	public void setStrCustomerType(String strCustomerType)
	{
		this.strCustomerType = (String) setDefaultValue(strCustomerType, "NA");
	}

	public String getDteDOB()
	{
		return dteDOB;
	}

	public void setDteDOB(String dteDOB)
	{
		this.dteDOB = dteDOB;
	}

	public String getStrGender()
	{
		return strGender;
	}

	public void setStrGender(String strGender)
	{
		this.strGender = (String) setDefaultValue(strGender, "NA");
	}

	public String getDteAnniversary()
	{
		return dteAnniversary;
	}

	public void setDteAnniversary(String dteAnniversary)
	{
		this.dteAnniversary = dteAnniversary;
	}

	public String getStrEmailId()
	{
		return strEmailId;
	}

	public void setStrEmailId(String strEmailId)
	{
		this.strEmailId = (String) setDefaultValue(strEmailId, "NA");
	}

	public String getStrCRMId()
	{
		return strCRMId;
	}

	public void setStrCRMId(String strCRMId)
	{
		this.strCRMId = (String) setDefaultValue(strCRMId, "");
	}

	public String getStrCustAddress()
	{
		return strCustAddress;
	}

	public void setStrCustAddress(String strCustAddress)
	{
		this.strCustAddress = (String) setDefaultValue(strCustAddress, "NA");
	}

	public String getStrTempAddress()
	{
		return strTempAddress;
	}

	public void setStrTempAddress(String strTempAddress)
	{
		this.strTempAddress = strTempAddress;
	}

	public String getStrTempStreet()
	{
		return strTempStreet;
	}

	public void setStrTempStreet(String strTempStreet)
	{
		this.strTempStreet = strTempStreet;
	}

	public String getStrTempLandmark()
	{
		return strTempLandmark;
	}

	public void setStrTempLandmark(String strTempLandmark)
	{
		this.strTempLandmark = strTempLandmark;
	}

	public String getStrGSTNo()
	{
		return strGSTNo;
	}

	public void setStrGSTNo(String strGSTNo)
	{
		this.strGSTNo = strGSTNo;
	}



}
