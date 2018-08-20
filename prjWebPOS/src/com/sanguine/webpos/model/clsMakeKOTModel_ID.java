package com.sanguine.webpos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsMakeKOTModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strSerialNo")
	private String strSerialNo;

	@Column(name="strTableNo")
	private String strTableNo;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strItemName")
	private String strItemName;

	@Column(name="strKOTNo")
	private String strKOTNo;

	public clsMakeKOTModel_ID(){}
	public clsMakeKOTModel_ID(String strSerialNo,String strTableNo,String strItemCode,String strItemName,String strKOTNo){
		this.strSerialNo=strSerialNo;
		this.strTableNo=strTableNo;
		this.strItemCode=strItemCode;
		this.strItemName=strItemName;
		this.strKOTNo=strKOTNo;
	}

//Setter-Getter Methods
	public String getStrSerialNo(){
		return strSerialNo;
	}
	public void setStrSerialNo(String strSerialNo){
		this. strSerialNo = strSerialNo;
	}

	public String getStrTableNo(){
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo){
		this. strTableNo = strTableNo;
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = strItemCode;
	}

	public String getStrItemName(){
		return strItemName;
	}
	public void setStrItemName(String strItemName){
		this. strItemName = strItemName;
	}

	public String getStrKOTNo(){
		return strKOTNo;
	}
	public void setStrKOTNo(String strKOTNo){
		this. strKOTNo = strKOTNo;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsMakeKOTModel_ID objModelId = (clsMakeKOTModel_ID)obj;
		if(this.strSerialNo.equals(objModelId.getStrSerialNo())&& this.strTableNo.equals(objModelId.getStrTableNo())&& this.strItemCode.equals(objModelId.getStrItemCode())&& this.strItemName.equals(objModelId.getStrItemName())&& this.strKOTNo.equals(objModelId.getStrKOTNo())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strSerialNo.hashCode()+this.strTableNo.hashCode()+this.strItemCode.hashCode()+this.strItemName.hashCode()+this.strKOTNo.hashCode();
	}

}
