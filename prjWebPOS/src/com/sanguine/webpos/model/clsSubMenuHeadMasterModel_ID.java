package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsSubMenuHeadMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strSubMenuHeadCode")
    private String strSubMenuHeadCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
    public clsSubMenuHeadMasterModel_ID()
    {
    }
    
    public clsSubMenuHeadMasterModel_ID(String strSubMenuHeadCode, String strClientCode)
    {
	this.strSubMenuHeadCode = strSubMenuHeadCode;
	this.strClientCode = strClientCode;
    }
    
    // Setter-Getter Methods
    public String getStrSubMenuHeadCode()
    {
	return strSubMenuHeadCode;
    }
    
    public void setStrSubMenuHeadCode(String strSubMenuHeadCode)
    {
	this.strSubMenuHeadCode = strSubMenuHeadCode;
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
	clsSubMenuHeadMasterModel_ID objModelId = (clsSubMenuHeadMasterModel_ID) obj;
	if (this.strSubMenuHeadCode.equals(objModelId.getStrSubMenuHeadCode()) && this.strClientCode.equals(objModelId.getStrClientCode()))
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
	return this.strSubMenuHeadCode.hashCode() + this.strClientCode.hashCode();
    }
    
}
