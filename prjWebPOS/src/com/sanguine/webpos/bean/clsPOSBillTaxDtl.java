package com.sanguine.webpos.bean;

public class clsPOSBillTaxDtl {

	

    String strBillNo;
    
    String strTaxCode;
    
    double dblTaxableAmount;
    
    double dblTaxAmount;
    
    String strClientCode;
    
    String strDataPostFlag;

    public String getStrBillNo() {
        return strBillNo;
    }

    public void setStrBillNo(String strBillNo) {
        this.strBillNo = strBillNo;
    }

    public String getStrTaxCode() {
        return strTaxCode;
    }

    public void setStrTaxCode(String strTaxCode) {
        this.strTaxCode = strTaxCode;
    }

    public double getDblTaxableAmount() {
        return dblTaxableAmount;
    }

    public void setDblTaxableAmount(double dblTaxableAmount) {
        this.dblTaxableAmount = dblTaxableAmount;
    }

    public double getDblTaxAmount() {
        return dblTaxAmount;
    }

    public void setDblTaxAmount(double dblTaxAmount) {
        this.dblTaxAmount = dblTaxAmount;
    }

    public String getStrClientCode() {
        return strClientCode;
    }

    public void setStrClientCode(String strClientCode) {
        this.strClientCode = strClientCode;
    }

    public String getStrDataPostFlag() {
        return strDataPostFlag;
    }

    public void setStrDataPostFlag(String strDataPostFlag) {
        this.strDataPostFlag = strDataPostFlag;
    }
    
    
}
