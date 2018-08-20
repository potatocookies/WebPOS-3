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
@Table(name="tblitemrtemp")
@IdClass(clsMakeKOTModel_ID.class)

public class clsMakeKOTHdModel extends clsBaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	public clsMakeKOTHdModel(){}

	public clsMakeKOTHdModel(clsMakeKOTModel_ID objModelID){
		strSerialNo = objModelID.getStrSerialNo();
		strTableNo = objModelID.getStrTableNo();
		strItemCode = objModelID.getStrItemCode();
		strItemName = objModelID.getStrItemName();
		strKOTNo = objModelID.getStrKOTNo();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strSerialNo",column=@Column(name="strSerialNo")),
@AttributeOverride(name="strTableNo",column=@Column(name="strTableNo")),
@AttributeOverride(name="strItemCode",column=@Column(name="strItemCode")),
@AttributeOverride(name="strItemName",column=@Column(name="strItemName")),
@AttributeOverride(name="strKOTNo",column=@Column(name="strKOTNo"))
	})

//Variable Declaration
	@Column(name="strSerialNo")
	private String strSerialNo;

	@Column(name="strTableNo")
	private String strTableNo;

	@Column(name="strCardNo")
	private String strCardNo;

	@Column(name="dblRedeemAmt")
	private double dblRedeemAmt;

	@Column(name="strHomeDelivery")
	private String strHomeDelivery;

	@Column(name="strCustomerCode")
	private String strCustomerCode;

	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strItemName")
	private String strItemName;

	@Column(name="dblItemQuantity")
	private double dblItemQuantity;

	@Column(name="dblAmount")
	private double dblAmount;

	@Column(name="strWaiterNo")
	private String strWaiterNo;

	@Column(name="strKOTNo")
	private String strKOTNo;

	@Column(name="intPaxNo")
	private long intPaxNo;

	@Column(name="strPrintYN")
	private String strPrintYN;

	@Column(name="strManualKOTNo")
	private String strManualKOTNo;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strOrderBefore")
	private String strOrderBefore;

	@Column(name="strTakeAwayYesNo")
	private String strTakeAwayYesNo;

	@Column(name="strDelBoyCode")
	private String strDelBoyCode;

	@Column(name="strNCKotYN")
	private String strNCKotYN;

	@Column(name="strCustomerName")
	private String strCustomerName;

	@Column(name="strActiveYN")
	private String strActiveYN;

	@Column(name="dblBalance")
	private double dblBalance;

	@Column(name="dblCreditLimit")
	private double dblCreditLimit;

	@Column(name="strCounterCode")
	private String strCounterCode;

	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="dblRate")
	private double dblRate;

	@Column(name="intId")
	private long intId;

	@Column(name="strCardType")
	private String strCardType;

	@Column(name="dblTaxAmt")
	private double dblTaxAmt;

	@Column(name="strReason")
	private String strReason;

//Setter-Getter Methods
	public String getStrSerialNo(){
		return strSerialNo;
	}
	public void setStrSerialNo(String strSerialNo){
		this. strSerialNo = (String) setDefaultValue( strSerialNo, "NA");
	}

	public String getStrTableNo(){
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo){
		this. strTableNo = (String) setDefaultValue( strTableNo, "NA");
	}

	public String getStrCardNo(){
		return strCardNo;
	}
	public void setStrCardNo(String strCardNo){
		this. strCardNo = (String) setDefaultValue( strCardNo, "NA");
	}

	public double getDblRedeemAmt(){
		return dblRedeemAmt;
	}
	public void setDblRedeemAmt(double dblRedeemAmt){
		this. dblRedeemAmt = (Double) setDefaultValue( dblRedeemAmt, "0.0000");
	}

	public String getStrHomeDelivery(){
		return strHomeDelivery;
	}
	public void setStrHomeDelivery(String strHomeDelivery){
		this. strHomeDelivery = (String) setDefaultValue( strHomeDelivery, "");
	}

	public String getStrCustomerCode(){
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode){
		this. strCustomerCode = (String) setDefaultValue( strCustomerCode, "NA");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public String getStrItemName(){
		return strItemName;
	}
	public void setStrItemName(String strItemName){
		this. strItemName = (String) setDefaultValue( strItemName, "NA");
	}

	public double getDblItemQuantity(){
		return dblItemQuantity;
	}
	public void setDblItemQuantity(double dblItemQuantity){
		this. dblItemQuantity = (Double) setDefaultValue( dblItemQuantity, "0.0000");
	}

	public double getDblAmount(){
		return dblAmount;
	}
	public void setDblAmount(double dblAmount){
		this. dblAmount = (Double) setDefaultValue( dblAmount, "0.0000");
	}

	public String getStrWaiterNo(){
		return strWaiterNo;
	}
	public void setStrWaiterNo(String strWaiterNo){
		this. strWaiterNo = (String) setDefaultValue( strWaiterNo, "NA");
	}

	public String getStrKOTNo(){
		return strKOTNo;
	}
	public void setStrKOTNo(String strKOTNo){
		this. strKOTNo = (String) setDefaultValue( strKOTNo, "NA");
	}

	public long getIntPaxNo(){
		return intPaxNo;
	}
	public void setIntPaxNo(long intPaxNo){
		this. intPaxNo = (Long) setDefaultValue( intPaxNo, "0");
	}

	public String getStrPrintYN(){
		return strPrintYN;
	}
	public void setStrPrintYN(String strPrintYN){
		this. strPrintYN = (String) setDefaultValue( strPrintYN, "N");
	}

	public String getStrManualKOTNo(){
		return strManualKOTNo;
	}
	public void setStrManualKOTNo(String strManualKOTNo){
		this. strManualKOTNo = (String) setDefaultValue( strManualKOTNo, "NA");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}

	public String getStrOrderBefore(){
		return strOrderBefore;
	}
	public void setStrOrderBefore(String strOrderBefore){
		this. strOrderBefore = (String) setDefaultValue( strOrderBefore, "");
	}

	public String getStrTakeAwayYesNo(){
		return strTakeAwayYesNo;
	}
	public void setStrTakeAwayYesNo(String strTakeAwayYesNo){
		this. strTakeAwayYesNo = (String) setDefaultValue( strTakeAwayYesNo, "");
	}

	public String getStrDelBoyCode(){
		return strDelBoyCode;
	}
	public void setStrDelBoyCode(String strDelBoyCode){
		this. strDelBoyCode = (String) setDefaultValue( strDelBoyCode, "NA");
	}

	public String getStrNCKotYN(){
		return strNCKotYN;
	}
	public void setStrNCKotYN(String strNCKotYN){
		this. strNCKotYN = (String) setDefaultValue( strNCKotYN, "");
	}

	public String getStrCustomerName(){
		return strCustomerName;
	}
	public void setStrCustomerName(String strCustomerName){
		this. strCustomerName = (String) setDefaultValue( strCustomerName, "NA");
	}

	public String getStrActiveYN(){
		return strActiveYN;
	}
	public void setStrActiveYN(String strActiveYN){
		this. strActiveYN = (String) setDefaultValue( strActiveYN, "");
	}

	public double getDblBalance(){
		return dblBalance;
	}
	public void setDblBalance(double dblBalance){
		this. dblBalance = (Double) setDefaultValue( dblBalance, "0.0000");
	}

	public double getDblCreditLimit(){
		return dblCreditLimit;
	}
	public void setDblCreditLimit(double dblCreditLimit){
		this. dblCreditLimit = (Double) setDefaultValue( dblCreditLimit, "0.0000");
	}

	public String getStrCounterCode(){
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode){
		this. strCounterCode = (String) setDefaultValue( strCounterCode, "");
	}

	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = (String) setDefaultValue( strPromoCode, "NA");
	}

	public double getDblRate(){
		return dblRate;
	}
	public void setDblRate(double dblRate){
		this. dblRate = (Double) setDefaultValue( dblRate, "0.0000");
	}

	public long getIntId(){
		return intId;
	}
	public void setIntId(long intId){
		this. intId = (Long) setDefaultValue( intId, "0");
	}

	public String getStrCardType(){
		return strCardType;
	}
	public void setStrCardType(String strCardType){
		this. strCardType = (String) setDefaultValue( strCardType, "NA");
	}

	public double getDblTaxAmt(){
		return dblTaxAmt;
	}
	public void setDblTaxAmt(double dblTaxAmt){
		this. dblTaxAmt = (Double) setDefaultValue( dblTaxAmt, "0.0000");
	}

	public String getStrReason(){
		return strReason;
	}
	public void setStrReason(String strReason){
		this. strReason = (String) setDefaultValue( strReason, "NA");
	}



}
