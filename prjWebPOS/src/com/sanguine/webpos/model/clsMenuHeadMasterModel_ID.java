package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsMenuHeadMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strMenuCode")
    private String strMenuCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    public clsMenuHeadMasterModel_ID()
    {
    }
    
    public clsMenuHeadMasterModel_ID(String strMenuCode, String strClientCode)
    {
	this.strMenuCode = strMenuCode;
	this.strClientCode = strClientCode;
    }
    
    // Setter-Getter Methods
    public String getStrMenuCode()
    {
	return strMenuCode;
    }
    
    public void setStrMenuCode(String strMenuCode)
    {
	this.strMenuCode = strMenuCode;
    }
    
    public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = strClientCode;
    }
    
    // HashCode and Equals Funtions
    @Override
    public boolean equals(Object obj)
    {
	clsMenuHeadMasterModel_ID objModelId = (clsMenuHeadMasterModel_ID) obj;
	if (this.strMenuCode.equals(objModelId.getStrMenuCode()) && this.strClientCode.equals(objModelId.getStrClientCode()))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    @Override
    public int hashCode()
    {
	return this.strMenuCode.hashCode() + this.strClientCode.hashCode();
    }
    
}
