package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
@SuppressWarnings("serial")

public class clsTableMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTableNo")
	private String strTableNo;
	
	 @Column(name = "strClientCode")
	 private String strClientCode;
	 
	public clsTableMasterModel_ID(){}
	
	public clsTableMasterModel_ID(String strTableNo, String strClientCode){
		this.strTableNo=strTableNo;
		this.strClientCode = strClientCode;
	}

//Setter-Getter Methods
	public String getStrTableNo(){
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo){
		this. strTableNo = strTableNo;
	}
	 public String getStrClientCode()
	    {
		return strClientCode;
	    }
	    
	    public void setStrClientCode(String strClientCode)
	    {
		this.strClientCode = strClientCode;
	    }

//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsTableMasterModel_ID objModelId = (clsTableMasterModel_ID)obj;
		if(this.strTableNo.equals(objModelId.getStrTableNo())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strTableNo.hashCode();
	}

}
