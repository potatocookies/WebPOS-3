package com.sanguine.webpos.model;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name = "tblmenuhd")
@NamedQueries({
	@NamedQuery (name="POSMenuHeadMaster" ,query="select m.strMenuCode ,m.strMenuName ,m.strOperational"
			+ " from clsMenuHeadMasterModel m where m.strClientCode=:clientCode" ),
	@NamedQuery (name="getMenuHeadMaster", query="from clsMenuHeadMasterModel where strMenuCode=:menuCode and strClientCode=:clientCode")})
@IdClass(clsMenuHeadMasterModel_ID.class)
public class clsMenuHeadMasterModel extends clsBaseModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public clsMenuHeadMasterModel()
    {
    }
    
    public clsMenuHeadMasterModel(clsMenuHeadMasterModel_ID objModelID)
    {
	strMenuCode = objModelID.getStrMenuCode();
	strClientCode = objModelID.getStrClientCode();
    }
    @CollectionOfElements(fetch=FetchType.EAGER)
    @Id
    @AttributeOverrides(
    { @AttributeOverride(name = "strMenuCode", column = @Column(name = "strMenuCode")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")) })
    // Variable Declaration
    @Column(name = "strMenuCode")
    private String strMenuCode;
    
    @Column(name = "strMenuName")
    private String strMenuName;
    
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
    
    @Column(name = "intSequence")
    private long intSequence;
    
    @Column(name = "strOperational")
    private String strOperational;
    
    @Column(name="imgImage")
    @Lob @Basic(fetch = FetchType.LAZY)
	@JsonIgnore
    private Blob imgImage;
    
    public Blob getImgImage() {
		return imgImage;
	}

	public void setImgImage(Blob imgImage) {
		this.imgImage = imgImage;
	}

	// Setter-Getter Methods
    public String getStrMenuCode()
    {
	return strMenuCode;
    }
    
    public void setStrMenuCode(String strMenuCode)
    {
	this.strMenuCode = (String) setDefaultValue(strMenuCode, "NA");
    }
    
    public String getStrMenuName()
    {
	return strMenuName;
    }
    
    public void setStrMenuName(String strMenuName)
    {
	this.strMenuName = (String) setDefaultValue(strMenuName, "NA");
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
    
    public long getIntSequence()
    {
	return intSequence;
    }
    
    public void setIntSequence(long intSequence)
    {
	this.intSequence = (Long) setDefaultValue(intSequence, "0");
    }
    
    public String getStrOperational()
    {
	return strOperational;
    }
    
    public void setStrOperational(String strOperational)
    {
	this.strOperational = (String) setDefaultValue(strOperational, "NA");
    }
    
 
    
}
