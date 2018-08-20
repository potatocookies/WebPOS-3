package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsPOSItemsDtlsInBill {

	private String itemCode;
	
	private String itemName;
	
	private double quantity;
	
	private double rate;
	
	private double amount;
	
	private double discountPer;
	
	private double discountAmt;
	
	private double taxAmount;
	
	private String KOTNo;
	
	private boolean tdhYN;
	
	private String strSerialNo;
	 
	private boolean isModifier;
	 
	private String modifierCode;
	 
	private String promoCode;
	  
    private String seqNo;
	   
	private String strDefaultModifierDeselectedYN;
	   
	private String modifierGroupCode;
	   
	 private double purchaseRate;
	 
	 private String tdh_ComboItemCode;
	 
	 private String tdhComboItemYN;
	 
	 private String strGroupcode;
	 
	 private String strSubGroupCode;
	 
	 private String tableNo;
	 
	 private int paxNo;
	 
	 private String waiterNo;
	
	private List<clsPOSModifiersOnItem> listModifierDtl=new ArrayList<clsPOSModifiersOnItem>();

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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getDiscountPer() {
		return discountPer;
	}

	public void setDiscountPer(double discountPer) {
		this.discountPer = discountPer;
	}

	public double getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(double discountAmt) {
		this.discountAmt = discountAmt;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getKOTNo() {
		return KOTNo;
	}

	public void setKOTNo(String kOTNo) {
		KOTNo = kOTNo;
	}

	public boolean isTdhYN() {
		return tdhYN;
	}

	public void setTdhYN(boolean tdhYN) {
		this.tdhYN = tdhYN;
	}

	public List<clsPOSModifiersOnItem> getListModifierDtl() {
		return listModifierDtl;
	}

	public void setListModifierDtl(List<clsPOSModifiersOnItem> listModifierDtl) {
		this.listModifierDtl = listModifierDtl;
	}

	public String getStrSerialNo() {
		return strSerialNo;
	}

	public void setStrSerialNo(String strSerialNo) {
		this.strSerialNo = strSerialNo;
	}

	public boolean isModifier() {
		return isModifier;
	}

	public void setModifier(boolean isModifier) {
		this.isModifier = isModifier;
	}

	public String getModifierCode() {
		return modifierCode;
	}

	public void setModifierCode(String modifierCode) {
		this.modifierCode = modifierCode;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getStrDefaultModifierDeselectedYN() {
		return strDefaultModifierDeselectedYN;
	}

	public void setStrDefaultModifierDeselectedYN(
			String strDefaultModifierDeselectedYN) {
		this.strDefaultModifierDeselectedYN = strDefaultModifierDeselectedYN;
	}

	public String getModifierGroupCode() {
		return modifierGroupCode;
	}

	public void setModifierGroupCode(String modifierGroupCode) {
		this.modifierGroupCode = modifierGroupCode;
	}

	public double getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(double purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public String getTdh_ComboItemCode() {
		return tdh_ComboItemCode;
	}

	public void setTdh_ComboItemCode(String tdh_ComboItemCode) {
		this.tdh_ComboItemCode = tdh_ComboItemCode;
	}

	public String getTdhComboItemYN() {
		return tdhComboItemYN;
	}

	public void setTdhComboItemYN(String tdhComboItemYN) {
		this.tdhComboItemYN = tdhComboItemYN;
	}

	public String getStrSubGroupCode() {
		return strSubGroupCode;
	}

	public void setStrSubGroupCode(String strSubGroupCode) {
		this.strSubGroupCode = strSubGroupCode;
	}

	public String getStrGroupcode() {
		return strGroupcode;
	}

	public void setStrGroupcode(String strGroupcode) {
		this.strGroupcode = strGroupcode;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}



	public String getWaiterNo() {
		return waiterNo;
	}

	public void setWaiterNo(String waiterNo) {
		this.waiterNo = waiterNo;
	}

	public int getPaxNo() {
		return paxNo;
	}

	public void setPaxNo(int paxNo) {
		this.paxNo = paxNo;
	}
	
	
	
	
}
