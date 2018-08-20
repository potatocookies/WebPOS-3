package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsAreaMasterModel_ID implements Serializable
{
    
    // Variable Declaration
    @Column(name = "strAreaCode")
    private String strAreaCode;
    
    @Column(name = "strClientCode")
    private String strClientCode;
    
  
    public clsAreaMasterModel_ID()
    {
    }
    
    public clsAreaMasterModel_ID(String strAreaCode, String strClientCode)
    {
	this.strAreaCode = strAreaCode;
	this.strClientCode = strClientCode;
	
    }
    
    // Setter-Getter Methods
    public String getStrAreaCode()
    {
	return strAreaCode;
    }
    
    public void setStrAreaCode(String strAreaCode)
    {
	this.strAreaCode = strAreaCode;
    }
    
    public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = strClientCode;
    }
    
   
    
}
