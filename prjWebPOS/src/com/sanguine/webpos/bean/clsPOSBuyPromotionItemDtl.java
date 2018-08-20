package com.sanguine.webpos.bean;

public class clsPOSBuyPromotionItemDtl 
{
private String itemCode;
    
    private double buyPromoItemQty;
    
    private double totalItemQty;
    
    private double totalAmount;
    
    public String getBuyPromoOn() {
		return buyPromoOn;
	}

	public void setBuyPromoOn(String buyPromoOn) {
		this.buyPromoOn = buyPromoOn;
	}

	public String getGetPromoOn() {
		return getPromoOn;
	}

	public void setGetPromoOn(String getPromoOn) {
		this.getPromoOn = getPromoOn;
	}

	private String buyPromoOn;
    
    private String getPromoOn;
    
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public double getBuyPromoItemQty() {
        return buyPromoItemQty;
    }

    public void setBuyPromoItemQty(double buyPromoItemQty) {
        this.buyPromoItemQty = buyPromoItemQty;
    }

    public double getTotalItemQty() {
        return totalItemQty;
    }

    public void setTotalItemQty(double totalItemQty) {
        this.totalItemQty = totalItemQty;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
	
}
