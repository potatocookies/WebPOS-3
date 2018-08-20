
package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsSettlementMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strSettelmentCode")
    private String strSettelmentCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
  
    public clsSettlementMasterModel_ID()
    {
    }
    
    public clsSettlementMasterModel_ID(String strSettelmentCode, String strClientCode)
    {
	this.strSettelmentCode = strSettelmentCode;
	this.strClientCode = strClientCode;
	 }
    
    // Setter-Getter Methods
    public String getStrSettelmentCode()
    {
	return strSettelmentCode;
    }
    
    public void setStrSettelmentCode(String strSettelmentCode)
    {
	this.strSettelmentCode = strSettelmentCode;
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
    	clsSettlementMasterModel_ID objModelId = (clsSettlementMasterModel_ID) obj;
	if (this.strSettelmentCode.equals(objModelId.getStrSettelmentCode()) && this.strClientCode.equals(objModelId.getStrClientCode()))
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
	return this.strSettelmentCode.hashCode() + this.strClientCode.hashCode();
    }
    
}
