package com.sanguine.webpos.bean;

public class clsPOSItemDetailFrTaxBean {
	
    private String itemCode;
    private String itemName;
    private double amount;
    private double discAmt;
    private double discPer;
    private String modifierCode;
    private double modifierAmount;
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	public String getModifierCode() {
		return modifierCode;
	}
	public void setModifierCode(String modifierCode) {
		this.modifierCode = modifierCode;
	}
	public double getModifierAmount() {
		return modifierAmount;
	}
	public void setModifierAmount(double modifierAmount) {
		this.modifierAmount = modifierAmount;
	}
    
    

}
