package com.sanguine.webpos.bean;

public class clsModifiersOnItem {

	private String modifierCode;
	
	private String modifierDescription;
	
	private String itemCode;
	
	private String strSerialNo;
	private double rate;
	
	private double quantity;
	
	private double amount;

	public String getModifierCode() {
		return modifierCode;
	}

	public void setModifierCode(String modifierCode) {
		this.modifierCode = modifierCode;
	}

	public String getModifierDescription() {
		return modifierDescription;
	}

	

	public void setModifierDescription(String modifierDescription) {
		this.modifierDescription = modifierDescription;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getStrSerialNo() {
		return strSerialNo;
	}

	public void setStrSerialNo(String strSerialNo) {
		this.strSerialNo = strSerialNo;
	}
	
	
}
