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
@Table(name = "tblsubmenuhead")
@NamedQueries({
	@NamedQuery (name="POSSubMenuHeadMaster", query="select m.strSubMenuHeadCode,m.strSubMenuHeadName,m.strSubMenuHeadShortName, m.strSubMenuOperational"
		+ " from clsSubMenuHeadMasterModel m where m.strClientCode=:clientCode"), 
	@NamedQuery (name="getSubMenuHeadMaster",query="from clsSubMenuHeadMasterModel where strSubMenuHeadCode=:subMenuCode and strClientCode=:clientCode")})


@IdClass(clsSubMenuHeadMasterModel_ID.class)
public class clsSubMenuHeadMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsSubMenuHeadMasterModel()
    {
    }
    
    public clsSubMenuHeadMasterModel(clsSubMenuHeadMasterModel_ID objModelID)
    {
	strSubMenuHeadCode = objModelID.getStrSubMenuHeadCode();
	strClientCode = objModelID.getStrClientCode();
    }
    
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strSubMenuHeadCode", column = @Column(name = "strSubMenuHeadCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")) })
    // Variable Declaration
    @Column(name = "strSubMenuHeadCode")
    private String strSubMenuHeadCode;
    
    @Column(name = "strMenuCode")
    private String strMenuCode;
    
    @Column(name = "strSubMenuHeadShortName")
    private String strSubMenuHeadShortName;
    
    @Column(name = "strSubMenuHeadName")
    private String strSubMenuHeadName;
    
    @Column(name = "strSubMenuOperational")
    private String strSubMenuOperational;
    
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
    
    // Setter-Getter Methods
    public String getStrSubMenuHeadCode()
    {
	return strSubMenuHeadCode;
    }
    
    public void setStrSubMenuHeadCode(String strSubMenuHeadCode)
    {
	this.strSubMenuHeadCode = (String) setDefaultValue(strSubMenuHeadCode, "NA");
    }
    
    public String getStrMenuCode()
    {
	return strMenuCode;
    }
    
    public void setStrMenuCode(String strMenuCode)
    {
	this.strMenuCode = (String) setDefaultValue(strMenuCode, "NA");
    }
    
    public String getStrSubMenuHeadShortName()
    {
	return strSubMenuHeadShortName;
    }
    
    public void setStrSubMenuHeadShortName(String strSubMenuHeadShortName)
    {
	this.strSubMenuHeadShortName = (String) setDefaultValue(strSubMenuHeadShortName, "NA");
    }
    
    public String getStrSubMenuHeadName()
    {
	return strSubMenuHeadName;
    }
    
    public void setStrSubMenuHeadName(String strSubMenuHeadName)
    {
	this.strSubMenuHeadName = (String) setDefaultValue(strSubMenuHeadName, "NA");
    }
    
    public String getStrSubMenuOperational()
    {
	return strSubMenuOperational;
    }
    
    public void setStrSubMenuOperational(String strSubMenuOperational)
    {
	this.strSubMenuOperational = (String) setDefaultValue(strSubMenuOperational, "NA");
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
    
   
    
}
