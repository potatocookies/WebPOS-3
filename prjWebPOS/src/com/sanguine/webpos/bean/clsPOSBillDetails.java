package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsPOSBillDetails {

	private String operationType;
	
	private String advanceBookingNo;
	
	private String billDateTime;
	
	private String billSettleDateTime;
	
	private String POSCode;
	
	private String clientCode;
	
	private String areaCode;
	
	private String takeAwayYN;
	
	private boolean homeDeliveryYN;
	
	private String customerCode;
	
	private String shiftNo;
	
	private String reasonCode;
	
	private String remarks;
	
	private String discountRemarks;
	
	private String takeAwayRemarks;
	
	private String discountON;
	
	private String transactionType;
	
	private String couponCode;
	
	private String tableNo;
	
	private String waiterNo;
	
	private int paxNo;
	
	private double subTotal;
	
	private double netTotal;
	
	private double grandTotal;
	
	private double taxTotal;
	
	private double discountPer;
	
	private double discountAmt;
	
	private double deliveryCharges;
	
	private String billType;
	
	private String  strGroupCode;
	
	private String strSubGroupcode;
	
	
	
	
	private List<clsPOSItemsDtlsInBill> listItemsDtlInBill=new ArrayList<clsPOSItemsDtlsInBill>();
	
	private List<clsPOSDiscountDtlsOnBill> listDiscountDtlOnBill=new ArrayList<clsPOSDiscountDtlsOnBill>();
	
	private List<clsPOSTaxDtlsOnBill> listTaxDtlOnBill=new ArrayList<clsPOSTaxDtlsOnBill>();
	
	private List<clsPOSSettlementDtlsOnBill> listSettlementDtlOnBill=new ArrayList<clsPOSSettlementDtlsOnBill>();
	
	private List<clsPOSCustomerDtlsOnBill> listCustomerDtlOnBill=new ArrayList<clsPOSCustomerDtlsOnBill>();
	
	private List<clsPOSDeliveryBoyMasterBean> listDeliveryBoyMasterBeanl=new ArrayList<clsPOSDeliveryBoyMasterBean>();
	

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getAdvanceBookingNo() {
		return advanceBookingNo;
	}

	public void setAdvanceBookingNo(String advanceBookingNo) {
		this.advanceBookingNo = advanceBookingNo;
	}

	public String getBillDateTime() {
		return billDateTime;
	}

	public void setBillDateTime(String billDateTime) {
		this.billDateTime = billDateTime;
	}

	public String getBillSettleDateTime() {
		return billSettleDateTime;
	}

	public void setBillSettleDateTime(String billSettleDateTime) {
		this.billSettleDateTime = billSettleDateTime;
	}

	public String getPOSCode() {
		return POSCode;
	}

	public void setPOSCode(String pOSCode) {
		POSCode = pOSCode;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	
	public boolean isHomeDeliveryYN() {
		return homeDeliveryYN;
	}

	public void setHomeDeliveryYN(boolean homeDeliveryYN) {
		this.homeDeliveryYN = homeDeliveryYN;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getShiftNo() {
		return shiftNo;
	}

	public void setShiftNo(String shiftNo) {
		this.shiftNo = shiftNo;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDiscountRemarks() {
		return discountRemarks;
	}

	public void setDiscountRemarks(String discountRemarks) {
		this.discountRemarks = discountRemarks;
	}

	public String getTakeAwayRemarks() {
		return takeAwayRemarks;
	}

	public void setTakeAwayRemarks(String takeAwayRemarks) {
		this.takeAwayRemarks = takeAwayRemarks;
	}

	public String getDiscountON() {
		return discountON;
	}

	public void setDiscountON(String discountON) {
		this.discountON = discountON;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getNetTotal() {
		return netTotal;
	}

	public void setNetTotal(double netTotal) {
		this.netTotal = netTotal;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public double getTaxTotal() {
		return taxTotal;
	}

	public void setTaxTotal(double taxTotal) {
		this.taxTotal = taxTotal;
	}

	
	public String getTakeAwayYN() {
		return takeAwayYN;
	}

	public void setTakeAwayYN(String takeAwayYN) {
		this.takeAwayYN = takeAwayYN;
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

	public double getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(double deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public List<clsPOSItemsDtlsInBill> getListItemsDtlInBill() {
		return listItemsDtlInBill;
	}

	public void setListItemsDtlInBill(List<clsPOSItemsDtlsInBill> listItemsDtlInBill) {
		this.listItemsDtlInBill = listItemsDtlInBill;
	}

	public List<clsPOSDiscountDtlsOnBill> getListDiscountDtlOnBill() {
		return listDiscountDtlOnBill;
	}

	public void setListDiscountDtlOnBill(List<clsPOSDiscountDtlsOnBill> listDiscountDtlOnBill) {
		this.listDiscountDtlOnBill = listDiscountDtlOnBill;
	}

	public List<clsPOSTaxDtlsOnBill> getListTaxDtlOnBill() {
		return listTaxDtlOnBill;
	}

	public void setListTaxDtlOnBill(List<clsPOSTaxDtlsOnBill> listTaxDtlOnBill) {
		this.listTaxDtlOnBill = listTaxDtlOnBill;
	}

	public List<clsPOSSettlementDtlsOnBill> getListSettlementDtlOnBill() {
		return listSettlementDtlOnBill;
	}

	public void setListSettlementDtlOnBill(List<clsPOSSettlementDtlsOnBill> listSettlementDtlOnBill) {
		this.listSettlementDtlOnBill = listSettlementDtlOnBill;
	}

	public List<clsPOSCustomerDtlsOnBill> getListCustomerDtlOnBill() {
		return listCustomerDtlOnBill;
	}

	public void setListCustomerDtlOnBill(List<clsPOSCustomerDtlsOnBill> listCustomerDtlOnBill) {
		this.listCustomerDtlOnBill = listCustomerDtlOnBill;
	}

	public List<clsPOSDeliveryBoyMasterBean> getListDeliveryBoyMasterBeanl() {
		return listDeliveryBoyMasterBeanl;
	}

	public void setListDeliveryBoyMasterBeanl(
			List<clsPOSDeliveryBoyMasterBean> listDeliveryBoyMasterBeanl) {
		this.listDeliveryBoyMasterBeanl = listDeliveryBoyMasterBeanl;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getStrGroupCode() {
		return strGroupCode;
	}

	public void setStrGroupCode(String strGroupCode) {
		this.strGroupCode = strGroupCode;
	}

	public String getStrSubGroupcode() {
		return strSubGroupcode;
	}

	public void setStrSubGroupcode(String strSubGroupcode) {
		this.strSubGroupcode = strSubGroupcode;
	}
	
}
