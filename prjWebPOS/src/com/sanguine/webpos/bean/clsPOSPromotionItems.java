package com.sanguine.webpos.bean;

public class clsPOSPromotionItems 
{
private String itemCode;
    
    private String promoCode;
    
    private String promoType;
    
    private double freeItemQty;
    
    private double discAmt;
    
    private double discPer;
    
    private String discType;
    
    private String billAmt;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public double getFreeItemQty() {
        return freeItemQty;
    }

    public void setFreeItemQty(double freeItemQty) {
        this.freeItemQty = freeItemQty;
    }

    public double getDiscAmt() {
        return discAmt;
    }

    public void setDiscAmt(double discAmt) {
        this.discAmt = discAmt;
    }

    public double getDiscPer() {
        return discPer;
    }

    public void setDiscPer(double discPer) {
        this.discPer = discPer;
    }

    public String getDiscType() {
        return discType;
    }

    public void setDiscType(String discType) {
        this.discType = discType;
    }

    public String getBillAmt() {
        return billAmt;
    }

    public void setBillAmt(String billAmt) {
        this.billAmt = billAmt;
    }
    

}
