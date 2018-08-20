package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsDiscountMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strDiscCode")
    private String strDiscCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
  
    public clsDiscountMasterModel_ID()
    {
    }
    
    public clsDiscountMasterModel_ID(String strAreaCode, String strClientCode)
    {
	this.strDiscCode = strAreaCode;
	this.strClientCode = strClientCode;
	
    }
    
    // Setter-Getter Methods
    
    
    public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public String getStrDiscCode() {
		return strDiscCode;
	}

	public void setStrDiscCode(String strDiscCode) {
		this.strDiscCode = strDiscCode;
	}

	public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = strClientCode;
    }
    
   
    
}
