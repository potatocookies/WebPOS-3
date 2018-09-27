package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tbldayendprocess")
@IdClass(clsDayEndProcessModel_ID.class)

public class clsDayEndProcessHdModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsDayEndProcessHdModel(){}

	public clsDayEndProcessHdModel(clsDayEndProcessModel_ID objModelID){
		strPOSCode = objModelID.getStrPOSCode();
		dtePOSDate = objModelID.getDtePOSDate();
		intShiftCode = objModelID.getIntShiftCode();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPOSCode",column=@Column(name="strPOSCode")),
		@AttributeOverride(name="dtePOSDate",column=@Column(name="dtePOSDate")),
		@AttributeOverride(name="intShiftCode",column=@Column(name="intShiftCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="dtePOSDate")
	private String dtePOSDate;

	@Column(name="strDayEnd")
	private String strDayEnd;

	@Column(name="dblTotalSale")
	private double dblTotalSale;

	@Column(name="dblNoOfBill")
	private double dblNoOfBill;

	@Column(name="dblNoOfVoidedBill")
	private double dblNoOfVoidedBill;

	@Column(name="dblNoOfModifyBill")
	private double dblNoOfModifyBill;

	@Column(name="dblHDAmt")
	private double dblHDAmt;

	@Column(name="dblDiningAmt")
	private double dblDiningAmt;

	@Column(name="dblTakeAway")
	private double dblTakeAway;

	@Column(name="dblFloat")
	private double dblFloat;

	@Column(name="dblCash")
	private double dblCash;

	@Column(name="dblAdvance")
	private double dblAdvance;

	@Column(name="dblTransferIn")
	private double dblTransferIn;

	@Column(name="dblTotalReceipt")
	private double dblTotalReceipt;

	@Column(name="dblPayments")
	private double dblPayments;

	@Column(name="dblWithdrawal")
	private double dblWithdrawal;

	@Column(name="dblTransferOut")
	private double dblTransferOut;

	@Column(name="dblTotalPay")
	private double dblTotalPay;

	@Column(name="dblCashInHand")
	private double dblCashInHand;

	@Column(name="dblRefund")
	private double dblRefund;

	@Column(name="dblTotalDiscount")
	private double dblTotalDiscount;

	@Column(name="dblNoOfDiscountedBill")
	private double dblNoOfDiscountedBill;

	@Column(name="intShiftCode")
	private long intShiftCode;

	@Column(name="strShiftEnd")
	private String strShiftEnd;

	@Column(name="intTotalPax")
	private long intTotalPax;

	@Column(name="intNoOfTakeAway")
	private long intNoOfTakeAway;

	@Column(name="intNoOfHomeDelivery")
	private long intNoOfHomeDelivery;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDayEndDateTime")
	private String dteDayEndDateTime;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="intNoOfNCKOT")
	private long intNoOfNCKOT;

	@Column(name="intNoOfComplimentaryKOT")
	private long intNoOfComplimentaryKOT;

	@Column(name="intNoOfVoidKOT")
	private long intNoOfVoidKOT;

	@Column(name="dblUsedDebitCardBalance")
	private double dblUsedDebitCardBalance;

	@Column(name="dblUnusedDebitCardBalance")
	private double dblUnusedDebitCardBalance;

	@Column(name="strWSStockAdjustmentNo")
	private String strWSStockAdjustmentNo;

	@Column(name="dblTipAmt")
	private double dblTipAmt;

	@Column(name="strExciseBillGeneration")
	private String strExciseBillGeneration;

	@Column(name="dblNetSale")
	private double dblNetSale;

	@Column(name="dblGrossSale")
	private double dblGrossSale;

	@Column(name="dblAPC")
	private double dblAPC;

	@Column(name="strClientCode")
	private String strClientCode;

//Setter-Getter Methods
	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getDtePOSDate(){
		return dtePOSDate;
	}
	public void setDtePOSDate(String dtePOSDate){
		this.dtePOSDate=dtePOSDate;
	}

	public String getStrDayEnd(){
		return strDayEnd;
	}
	public void setStrDayEnd(String strDayEnd){
		this. strDayEnd = (String) setDefaultValue( strDayEnd, "N");
	}

	public double getDblTotalSale(){
		return dblTotalSale;
	}
	public void setDblTotalSale(double dblTotalSale){
		this. dblTotalSale = (Double) setDefaultValue( dblTotalSale, "0.00");
	}

	public double getDblNoOfBill(){
		return dblNoOfBill;
	}
	public void setDblNoOfBill(double dblNoOfBill){
		this. dblNoOfBill = (Double) setDefaultValue( dblNoOfBill, "0.00");
	}

	public double getDblNoOfVoidedBill(){
		return dblNoOfVoidedBill;
	}
	public void setDblNoOfVoidedBill(double dblNoOfVoidedBill){
		this. dblNoOfVoidedBill = (Double) setDefaultValue( dblNoOfVoidedBill, "0.00");
	}

	public double getDblNoOfModifyBill(){
		return dblNoOfModifyBill;
	}
	public void setDblNoOfModifyBill(double dblNoOfModifyBill){
		this. dblNoOfModifyBill = (Double) setDefaultValue( dblNoOfModifyBill, "0.00");
	}

	public double getDblHDAmt(){
		return dblHDAmt;
	}
	public void setDblHDAmt(double dblHDAmt){
		this. dblHDAmt = (Double) setDefaultValue( dblHDAmt, "0.00");
	}

	public double getDblDiningAmt(){
		return dblDiningAmt;
	}
	public void setDblDiningAmt(double dblDiningAmt){
		this. dblDiningAmt = (Double) setDefaultValue( dblDiningAmt, "0.00");
	}

	public double getDblTakeAway(){
		return dblTakeAway;
	}
	public void setDblTakeAway(double dblTakeAway){
		this. dblTakeAway = (Double) setDefaultValue( dblTakeAway, "0.00");
	}

	public double getDblFloat(){
		return dblFloat;
	}
	public void setDblFloat(double dblFloat){
		this. dblFloat = (Double) setDefaultValue( dblFloat, "0.00");
	}

	public double getDblCash(){
		return dblCash;
	}
	public void setDblCash(double dblCash){
		this. dblCash = (Double) setDefaultValue( dblCash, "0.00");
	}

	public double getDblAdvance(){
		return dblAdvance;
	}
	public void setDblAdvance(double dblAdvance){
		this. dblAdvance = (Double) setDefaultValue( dblAdvance, "0.00");
	}

	public double getDblTransferIn(){
		return dblTransferIn;
	}
	public void setDblTransferIn(double dblTransferIn){
		this. dblTransferIn = (Double) setDefaultValue( dblTransferIn, "0.00");
	}

	public double getDblTotalReceipt(){
		return dblTotalReceipt;
	}
	public void setDblTotalReceipt(double dblTotalReceipt){
		this. dblTotalReceipt = (Double) setDefaultValue( dblTotalReceipt, "0.00");
	}

	public double getDblPayments(){
		return dblPayments;
	}
	public void setDblPayments(double dblPayments){
		this. dblPayments = (Double) setDefaultValue( dblPayments, "0.00");
	}

	public double getDblWithdrawal(){
		return dblWithdrawal;
	}
	public void setDblWithdrawal(double dblWithdrawal){
		this. dblWithdrawal = (Double) setDefaultValue( dblWithdrawal, "0.00");
	}

	public double getDblTransferOut(){
		return dblTransferOut;
	}
	public void setDblTransferOut(double dblTransferOut){
		this. dblTransferOut = (Double) setDefaultValue( dblTransferOut, "0.00");
	}

	public double getDblTotalPay(){
		return dblTotalPay;
	}
	public void setDblTotalPay(double dblTotalPay){
		this. dblTotalPay = (Double) setDefaultValue( dblTotalPay, "0.00");
	}

	public double getDblCashInHand(){
		return dblCashInHand;
	}
	public void setDblCashInHand(double dblCashInHand){
		this. dblCashInHand = (Double) setDefaultValue( dblCashInHand, "0.00");
	}

	public double getDblRefund(){
		return dblRefund;
	}
	public void setDblRefund(double dblRefund){
		this. dblRefund = (Double) setDefaultValue( dblRefund, "0.00");
	}

	public double getDblTotalDiscount(){
		return dblTotalDiscount;
	}
	public void setDblTotalDiscount(double dblTotalDiscount){
		this. dblTotalDiscount = (Double) setDefaultValue( dblTotalDiscount, "0.00");
	}

	public double getDblNoOfDiscountedBill(){
		return dblNoOfDiscountedBill;
	}
	public void setDblNoOfDiscountedBill(double dblNoOfDiscountedBill){
		this. dblNoOfDiscountedBill = (Double) setDefaultValue( dblNoOfDiscountedBill, "0.00");
	}

	public long getIntShiftCode(){
		return intShiftCode;
	}
	public void setIntShiftCode(long intShiftCode){
		this. intShiftCode = (Long) setDefaultValue( intShiftCode, "0");
	}

	public String getStrShiftEnd(){
		return strShiftEnd;
	}
	public void setStrShiftEnd(String strShiftEnd){
		this. strShiftEnd = (String) setDefaultValue( strShiftEnd, "");
	}

	public long getIntTotalPax(){
		return intTotalPax;
	}
	public void setIntTotalPax(long intTotalPax){
		this. intTotalPax = (Long) setDefaultValue( intTotalPax, "0");
	}

	public long getIntNoOfTakeAway(){
		return intNoOfTakeAway;
	}
	public void setIntNoOfTakeAway(long intNoOfTakeAway){
		this. intNoOfTakeAway = (Long) setDefaultValue( intNoOfTakeAway, "0");
	}

	public long getIntNoOfHomeDelivery(){
		return intNoOfHomeDelivery;
	}
	public void setIntNoOfHomeDelivery(long intNoOfHomeDelivery){
		this. intNoOfHomeDelivery = (Long) setDefaultValue( intNoOfHomeDelivery, "0");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDayEndDateTime(){
		return dteDayEndDateTime;
	}
	public void setDteDayEndDateTime(String dteDayEndDateTime){
		this.dteDayEndDateTime=dteDayEndDateTime;
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public long getIntNoOfNCKOT(){
		return intNoOfNCKOT;
	}
	public void setIntNoOfNCKOT(long intNoOfNCKOT){
		this. intNoOfNCKOT = (Long) setDefaultValue( intNoOfNCKOT, "0");
	}

	public long getIntNoOfComplimentaryKOT(){
		return intNoOfComplimentaryKOT;
	}
	public void setIntNoOfComplimentaryKOT(long intNoOfComplimentaryKOT){
		this. intNoOfComplimentaryKOT = (Long) setDefaultValue( intNoOfComplimentaryKOT, "0");
	}

	public long getIntNoOfVoidKOT(){
		return intNoOfVoidKOT;
	}
	public void setIntNoOfVoidKOT(long intNoOfVoidKOT){
		this. intNoOfVoidKOT = (Long) setDefaultValue( intNoOfVoidKOT, "0");
	}

	public double getDblUsedDebitCardBalance(){
		return dblUsedDebitCardBalance;
	}
	public void setDblUsedDebitCardBalance(double dblUsedDebitCardBalance){
		this. dblUsedDebitCardBalance = (Double) setDefaultValue( dblUsedDebitCardBalance, "0.00");
	}

	public double getDblUnusedDebitCardBalance(){
		return dblUnusedDebitCardBalance;
	}
	public void setDblUnusedDebitCardBalance(double dblUnusedDebitCardBalance){
		this. dblUnusedDebitCardBalance = (Double) setDefaultValue( dblUnusedDebitCardBalance, "0.00");
	}

	public String getStrWSStockAdjustmentNo(){
		return strWSStockAdjustmentNo;
	}
	public void setStrWSStockAdjustmentNo(String strWSStockAdjustmentNo){
		this. strWSStockAdjustmentNo = (String) setDefaultValue( strWSStockAdjustmentNo, "NA");
	}

	public double getDblTipAmt(){
		return dblTipAmt;
	}
	public void setDblTipAmt(double dblTipAmt){
		this. dblTipAmt = (Double) setDefaultValue( dblTipAmt, "0.00");
	}

	public String getStrExciseBillGeneration(){
		return strExciseBillGeneration;
	}
	public void setStrExciseBillGeneration(String strExciseBillGeneration){
		this. strExciseBillGeneration = (String) setDefaultValue( strExciseBillGeneration, "NA");
	}

	public double getDblNetSale(){
		return dblNetSale;
	}
	public void setDblNetSale(double dblNetSale){
		this. dblNetSale = (Double) setDefaultValue( dblNetSale, "0.00");
	}

	public double getDblGrossSale(){
		return dblGrossSale;
	}
	public void setDblGrossSale(double dblGrossSale){
		this. dblGrossSale = (Double) setDefaultValue( dblGrossSale, "0.00");
	}

	public double getDblAPC(){
		return dblAPC;
	}
	public void setDblAPC(double dblAPC){
		this. dblAPC = (Double) setDefaultValue( dblAPC, "0.00");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}



}
