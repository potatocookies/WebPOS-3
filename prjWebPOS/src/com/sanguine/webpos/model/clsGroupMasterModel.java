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
@Table(name = "tblgrouphd")
@NamedQueries({
		@NamedQuery(name="POSGroupMaster",query="select strGroupCode,strGroupName,strOperationalYN from clsGroupMasterModel where strClientCode=:clientCode"),
		@NamedQuery(name="getGroupMaster",query="from clsGroupMasterModel where strGroupCode=:groupCode and strClientCode=:clientCode") })
@IdClass(clsGroupMasterModel_ID.class)
public class clsGroupMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsGroupMasterModel()
    {
    }
    
    public clsGroupMasterModel(clsGroupMasterModel_ID objModelID)
    {
	strGroupCode = objModelID.getStrGroupCode();
	strClientCode = objModelID.getStrClientCode();
    }
    
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strGroupCode", column = @Column(name = "strGroupCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")) })
    // Variable Declaration
    @Column(name = "strGroupCode")
    private String strGroupCode;
    
    @Column(name = "strGroupName")
    private String strGroupName;
    
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
    
    @Column(name = "strOperationalYN")
    private String strOperationalYN;
    
    @Column(name="strGroupShortName")
    private String strGroupShortName;
    
    public String getStrGroupShortName() {
		return strGroupShortName;
	}

	public void setStrGroupShortName(String strGroupShortName) {
		this.strGroupShortName = strGroupShortName;
	}

	// Setter-Getter Methods
    public String getStrGroupCode()
    {
	return strGroupCode;
    }
    
    public void setStrGroupCode(String strGroupCode)
    {
	this.strGroupCode = (String) setDefaultValue(strGroupCode, "NA");
    }
    
    public String getStrGroupName()
    {
	return strGroupName;
    }
    
    public void setStrGroupName(String strGroupName)
    {
	this.strGroupName = (String) setDefaultValue(strGroupName, "NA");
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
    
    public String getStrOperationalYN()
    {
	return strOperationalYN;
    }
    
    public void setStrOperationalYN(String strOperationalYN)
    {
	this.strOperationalYN = (String) setDefaultValue(strOperationalYN, "NA");
    }
  
}
