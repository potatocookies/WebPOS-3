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
@Table(name = "tblareamaster")
@IdClass(clsAreaMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllAreaMaster", 
query = "select m.strAreaCode,m.strAreaName,m.strPOSCode " 
		+ "from clsAreaMasterModel m where m.strClientCode=:clientCode "),

		@NamedQuery(name = "getAreaMaster", 
		query = "from clsAreaMasterModel where strAreaCode=:areaCode and strClientCode=:clientCode")})
public class clsAreaMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsAreaMasterModel()
    {
    }
    
    public clsAreaMasterModel(clsAreaMasterModel_ID objModelID)
    {
	strAreaCode = objModelID.getStrAreaCode();
	strClientCode = objModelID.getStrClientCode();
	
    }
 
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strAreaCode", column = @Column(name = "strAreaCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode"))})
    // Variable Declaration
    @Column(name = "strAreaCode")
    private String strAreaCode;
    
    @Column(name = "strAreaName")
    private String strAreaName;
    
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
    
    @Column(name = "strPOSCode")
    private String strPOSCode;
    
    // Setter-Getter Methods
    public String getStrAreaCode()
    {
	return strAreaCode;
    }
    
    public void setStrAreaCode(String strAreaCode)
    {
	this.strAreaCode = (String) setDefaultValue(strAreaCode, "NA");
    }
    
    public String getStrAreaName()
    {
	return strAreaName;
    }
    
    public void setStrAreaName(String strAreaName)
    {
	this.strAreaName = (String) setDefaultValue(strAreaName, "NA");
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
	this.strDataPostFlag = (String) setDefaultValue(strDataPostFlag, "N");
    }
    
    public String getStrPOSCode()
    {
	return strPOSCode;
    }
    
    public void setStrPOSCode(String strPOSCode)
    {
	this.strPOSCode = (String) setDefaultValue(strPOSCode, "NA");
    }
    
   
    
}
