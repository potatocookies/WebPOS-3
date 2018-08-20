package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@SuppressWarnings("serial")
public class clsBillHdModel_ID implements Serializable{
	
	  	@Column(name="strBillNo")  
	  	private String strBillNo;
	  	
	    @Column(name="strClientCode")
	    private String strClientCode;
	    
	    @Column(name="dtBillDate")
	    private String dtBillDate;
	    
	    
	    public clsBillHdModel_ID(){}
		public clsBillHdModel_ID(String strBillNo,String dtBillDate,String strClientCode){
			this.strBillNo=strBillNo;
			this.strClientCode=strClientCode;
			this.dtBillDate=dtBillDate;
		}

		public String getStrBillNo() {
			return strBillNo;
		}

		public void setStrBillNo(String strBillNo) {
			this.strBillNo = strBillNo;
		}

		public String getStrClientCode() {
			return strClientCode;
		}

		public void setStrClientCode(String strClientCode) {
			this.strClientCode = strClientCode;
		}

		public String getDtBillDate() {
			return dtBillDate;
		}
		public void setDtBillDate(String dtBillDate) {
			this.dtBillDate = dtBillDate;
		}
		
		//HashCode and Equals Funtions
		@Override
		public boolean equals(Object obj) {
			clsBillHdModel_ID objModelId = (clsBillHdModel_ID)obj;
			if(this.strBillNo.equals(objModelId.getStrBillNo())&& this.strClientCode.equals(objModelId.getStrClientCode())&& this.dtBillDate.equals(objModelId.getDtBillDate())){
				return true;
			}
			else{
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.strBillNo.hashCode()+this.strClientCode.hashCode()+this.dtBillDate.hashCode();
		}
		

}
