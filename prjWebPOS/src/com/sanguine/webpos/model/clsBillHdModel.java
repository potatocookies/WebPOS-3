package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblbillhd")
@IdClass(clsBillHdModel_ID.class)
public class clsBillHdModel  extends clsBaseModel implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	public clsBillHdModel(){}
	
	public clsBillHdModel(clsBillHdModel_ID objModelID){
		strBillNo = objModelID.getStrBillNo();		
		dtBillDate = objModelID.getDtBillDate();
		strClientCode = objModelID.getStrClientCode();
	}
	
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbilldtl" , joinColumns={@JoinColumn(name="dtBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dtBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillDtlModel> listBillDtlModel= new ArrayList<clsBillDtlModel>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbilldiscdtl" , joinColumns={@JoinColumn(name="dteBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dteBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillDiscDtlModel> listBillDiscDtlModel= new ArrayList<clsBillDiscDtlModel>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbillmodifierdtl" , joinColumns={@JoinColumn(name="dteBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dteBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillModifierDtlModel> listBillModifierDtlModel= new ArrayList<clsBillModifierDtlModel>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbillcomplementrydtl" , joinColumns={@JoinColumn(name="dtBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dtBillDate"))
	})
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillComplementaryDtlModel> listBillComplementaryDtlModel= new ArrayList<clsBillComplementaryDtlModel>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbillpromotiondtl" , joinColumns={@JoinColumn(name="dteBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dteBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillPromotionDtlModel> listBillPromotionDtlModel= new ArrayList<clsBillPromotionDtlModel>();
	
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbilltaxdtl" , joinColumns={@JoinColumn(name="dteBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dteBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillTaxDtl> listBillTaxDtl= new ArrayList<clsBillTaxDtl>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(name="tblbillsettlementdtl" , joinColumns={@JoinColumn(name="dteBillDate"),@JoinColumn(name="strBillNo"),@JoinColumn(name="strClientCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strBillNo",column=@Column(name="strBillNo")),
		@AttributeOverride(name="dtBillDate",column=@Column(name="dteBillDate"))
	})
	@Embedded
	@Fetch(FetchMode.SUBSELECT)
	private List<clsBillSettlementDtlModel> listBillSettlementDtlModel= new ArrayList<clsBillSettlementDtlModel>();
	
	
	
		@Column(name="strBillNo")
	    private String strBillNo;
		@Column(name="strAdvBookingNo")
	    private String strAdvBookingNo;
		@Column(name="dteBillDate")
	    private String dteBillDate;
		@Column(name="strPOSCode")
	    private String strPOSCode;
		@Column(name="strSettelmentMode")
	    private String strSettelmentMode;
		@Column(name="dblDiscountAmt")
	    private double dblDiscountAmt;
		@Column(name="dblDiscountPer")
	    private double dblDiscountPer;
		@Column(name="dblTaxAmt")
	    private double dblTaxAmt;
		@Column(name="dblSubTotal")
	    private double dblSubTotal;
		@Column(name="dblGrandTotal")
	    private double dblGrandTotal;
		@Column(name="strTakeAway")
	    private String strTakeAway;
		@Column(name="strOperationType")
	    private String strOperationType;
		@Column(name="strUserCreated")
	    private String strUserCreated;
		@Column(name="strUserEdited")
	    private String strUserEdited;
		@Column(name="dteDateCreated")
	    private String dteDateCreated;
		@Column(name="dteDateEdited")
	    private String dteDateEdited;
		@Column(name="strClientCode")
	    private String strClientCode;
		@Column(name="strTableNo")
	    private String strTableNo;
		@Column(name="strWaiterNo")
	    private String strWaiterNo;
		@Column(name="strCustomerCode")
	    private String strCustomerCode;
		@Column(name="strManualBillNo")
	    private String strManualBillNo;
		@Column(name="intShiftCode")
	    private int intShiftCode;
		@Column(name="intPaxNo")
	    private int intPaxNo;
		@Column(name="strDataPostFlag")
		private String strDataPostFlag;
		@Column(name="strReasonCode")
	    private String strReasonCode;
		@Column(name="strRemarks")
	    private String strRemarks;
		@Column(name="dblTipAmount")
	    private double dblTipAmount;
		@Column(name="dteSettleDate")
	    private String dteSettleDate;
		@Column(name="strCounterCode")
	    private String strCounterCode;
		@Column(name="dblDeliveryCharges")
	    private double dblDeliveryCharges;
		@Column(name="strCouponCode")
	    private String strCouponCode;
		@Column(name="strAreaCode")
	    private String strAreaCode;
		@Column(name="strDiscountRemark")
	    private String strDiscountRemark;
		@Column(name="strTakeAwayRemarks")
	    private String strTakeAwayRemarks;
		@Column(name="strDiscountOn")
	    private String strDiscountOn;
		@Column(name="strCardNo")
	    private String strCardNo;
		@Column(name="strTransactionType")
	    private String strTransactionType;
		@Column(name="strJioMoneyRRefNo" ,columnDefinition="VARCHAR(255) NOT NULL default ''")
	    private String strJioMoneyRRefNo;
		@Column(name="strJioMoneyAuthCode",columnDefinition="VARCHAR(255) NOT NULL default ''")   
		private String strJioMoneyAuthCode;
		@Column(name="strJioMoneyTxnId",columnDefinition="VARCHAR(255) NOT NULL default ''")
	    private String strJioMoneyTxnId;
		@Column(name="strJioMoneyTxnDateTime",columnDefinition="VARCHAR(255) NOT NULL default ''")    
		private String strJioMoneyTxnDateTime;
		@Column(name="strJioMoneyCardNo",columnDefinition="VARCHAR(255) NOT NULL default ''")
	    private String strJioMoneyCardNo;
		@Column(name="strJioMoneyCardType",columnDefinition="VARCHAR(255) NOT NULL default ''")
	    private String strJioMoneyCardType;
		@Column(name="dblRoundOff",columnDefinition="Decimal(18,4) NOT NULL default '0.00'")
	    private double dblRoundOff;
		@Column(name="intBillSeriesPaxNo",columnDefinition="Int(8) default '0'")
	    private int intBillSeriesPaxNo;
		@Column(name="dtBillDate")
	    private String dtBillDate;
		@Column(name="intOrderNo",columnDefinition="Int(8) default '0'")
	    private int intOrderNo;

	    
	    
	    
	    
	    
	    
		public String getStrBillNo() {
			return strBillNo;
		}

		public void setStrBillNo(String strBillNo) {
			this.strBillNo = strBillNo;
		}

		public String getStrAdvBookingNo() {
			return strAdvBookingNo;
		}

		public void setStrAdvBookingNo(String strAdvBookingNo) {
			this.strAdvBookingNo = strAdvBookingNo;
		}


		public String getStrPOSCode() {
			return strPOSCode;
		}

		public void setStrPOSCode(String strPOSCode) {
			this.strPOSCode = strPOSCode;
		}

		public String getStrSettelmentMode() {
			return strSettelmentMode;
		}

		public void setStrSettelmentMode(String strSettelmentMode) {
			this.strSettelmentMode = strSettelmentMode;
		}

		public double getDblDiscountAmt() {
			return dblDiscountAmt;
		}

		public void setDblDiscountAmt(double dblDiscountAmt) {
			this.dblDiscountAmt = dblDiscountAmt;
		}

		public double getDblDiscountPer() {
			return dblDiscountPer;
		}

		public void setDblDiscountPer(double dblDiscountPer) {
			this.dblDiscountPer = dblDiscountPer;
		}

		public double getDblTaxAmt() {
			return dblTaxAmt;
		}

		public void setDblTaxAmt(double dblTaxAmt) {
			this.dblTaxAmt = dblTaxAmt;
		}

		public double getDblSubTotal() {
			return dblSubTotal;
		}

		public void setDblSubTotal(double dblSubTotal) {
			this.dblSubTotal = dblSubTotal;
		}

		public double getDblGrandTotal() {
			return dblGrandTotal;
		}

		public void setDblGrandTotal(double dblGrandTotal) {
			this.dblGrandTotal = dblGrandTotal;
		}

		public String getStrTakeAway() {
			return strTakeAway;
		}

		public void setStrTakeAway(String strTakeAway) {
			this.strTakeAway = strTakeAway;
		}

		public String getStrOperationType() {
			return strOperationType;
		}

		public void setStrOperationType(String strOperationType) {
			this.strOperationType = strOperationType;
		}

		public String getStrUserCreated() {
			return strUserCreated;
		}

		public void setStrUserCreated(String strUserCreated) {
			this.strUserCreated = strUserCreated;
		}

		public String getStrUserEdited() {
			return strUserEdited;
		}

		public void setStrUserEdited(String strUserEdited) {
			this.strUserEdited = strUserEdited;
		}

		public String getDteDateCreated() {
			return dteDateCreated;
		}

		public void setDteDateCreated(String dteDateCreated) {
			this.dteDateCreated = dteDateCreated;
		}

		public String getDteDateEdited() {
			return dteDateEdited;
		}

		public void setDteDateEdited(String dteDateEdited) {
			this.dteDateEdited = dteDateEdited;
		}

		public String getStrClientCode() {
			return strClientCode;
		}

		public void setStrClientCode(String strClientCode) {
			this.strClientCode = strClientCode;
		}

		public String getStrTableNo() {
			return strTableNo;
		}

		public void setStrTableNo(String strTableNo) {
			this.strTableNo = strTableNo;
		}

		public String getStrWaiterNo() {
			return strWaiterNo;
		}

		public void setStrWaiterNo(String strWaiterNo) {
			this.strWaiterNo = strWaiterNo;
		}

		public String getStrCustomerCode() {
			return strCustomerCode;
		}

		public void setStrCustomerCode(String strCustomerCode) {
			this.strCustomerCode = strCustomerCode;
		}

		public String getStrManualBillNo() {
			return strManualBillNo;
		}

		public void setStrManualBillNo(String strManualBillNo) {
			this.strManualBillNo = strManualBillNo;
		}

		public int getIntShiftCode() {
			return intShiftCode;
		}

		public void setIntShiftCode(int intShiftCode) {
			this.intShiftCode = intShiftCode;
		}

		public int getIntPaxNo() {
			return intPaxNo;
		}

		public void setIntPaxNo(int intPaxNo) {
			this.intPaxNo = intPaxNo;
		}

		public String getStrDataPostFlag() {
			return strDataPostFlag;
		}

		public void setStrDataPostFlag(String strDataPostFlag) {
			this.strDataPostFlag = strDataPostFlag;
		}

		public String getStrReasonCode() {
			return strReasonCode;
		}

		public void setStrReasonCode(String strReasonCode) {
			this.strReasonCode = (String) setDefaultValue( strReasonCode, "");;
		}

		public String getStrRemarks() {
			return strRemarks;
		}

		public void setStrRemarks(String strRemarks) {
			this.strRemarks =(String) setDefaultValue( strRemarks, "") ;
		}

		public double getDblTipAmount() {
			return dblTipAmount;
		}

		public void setDblTipAmount(double dblTipAmount) {
			this.dblTipAmount = dblTipAmount;
		}

		public String getDteSettleDate() {
			return dteSettleDate;
		}

		public void setDteSettleDate(String dteSettleDate) {
			this.dteSettleDate = dteSettleDate;
		}

		public String getStrCounterCode() {
			return strCounterCode;
		}

		public void setStrCounterCode(String strCounterCode) {
			this.strCounterCode = strCounterCode;
		}

		public double getDblDeliveryCharges() {
			return dblDeliveryCharges;
		}

		public void setDblDeliveryCharges(double dblDeliveryCharges) {
			this.dblDeliveryCharges = dblDeliveryCharges;
		}

		public String getStrCouponCode() {
			return strCouponCode;
		}

		public void setStrCouponCode(String strCouponCode) {
			this.strCouponCode = strCouponCode;
		}

		public String getStrAreaCode() {
			return strAreaCode;
		}

		public void setStrAreaCode(String strAreaCode) {
			this.strAreaCode = strAreaCode;
		}

		public String getStrDiscountRemark() {
			return strDiscountRemark;
		}

		public void setStrDiscountRemark(String strDiscountRemark) {
			this.strDiscountRemark = strDiscountRemark;
		}

		public String getStrTakeAwayRemarks() {
			return strTakeAwayRemarks;
		}

		public void setStrTakeAwayRemarks(String strTakeAwayRemarks) {
			this.strTakeAwayRemarks = strTakeAwayRemarks;
		}

		public String getStrDiscountOn() {
			return strDiscountOn;
		}

		public void setStrDiscountOn(String strDiscountOn) {
			this.strDiscountOn = strDiscountOn;
		}

		public String getStrCardNo() {
			return strCardNo;
		}

		public void setStrCardNo(String strCardNo) {
			this.strCardNo = strCardNo;
		}

		public String getStrTransactionType() {
			return strTransactionType;
		}

		public void setStrTransactionType(String strTransactionType) {
			this.strTransactionType = strTransactionType;
		}

		public String getStrJioMoneyRRefNo() {
			return strJioMoneyRRefNo;
		}

		public void setStrJioMoneyRRefNo(String strJioMoneyRRefNo) {
			this.strJioMoneyRRefNo = (String) setDefaultValue( strJioMoneyRRefNo, "NA") ;
		}

		public String getStrJioMoneyAuthCode() {
			return strJioMoneyAuthCode;
		}

		public void setStrJioMoneyAuthCode(String strJioMoneyAuthCode) {
			this.strJioMoneyAuthCode =(String) setDefaultValue( strJioMoneyAuthCode, "NA") ;
		}

		public String getStrJioMoneyTxnId() {
			return strJioMoneyTxnId;
		}

		public void setStrJioMoneyTxnId(String strJioMoneyTxnId) {
			this.strJioMoneyTxnId =(String) setDefaultValue( strJioMoneyTxnId, "NA") ;
		}

		public String getStrJioMoneyTxnDateTime() {
			return strJioMoneyTxnDateTime;
		}

		public void setStrJioMoneyTxnDateTime(String strJioMoneyTxnDateTime) {
			this.strJioMoneyTxnDateTime = (String) setDefaultValue( strJioMoneyTxnDateTime, "NA") ;
		}

		public String getStrJioMoneyCardNo() {
			return strJioMoneyCardNo;
		}

		public void setStrJioMoneyCardNo(String strJioMoneyCardNo) {
			this.strJioMoneyCardNo =  (String) setDefaultValue( strJioMoneyCardNo, "NA") ;
		}

		public String getStrJioMoneyCardType() {
			return strJioMoneyCardType;
		}

		public void setStrJioMoneyCardType(String strJioMoneyCardType) {
			this.strJioMoneyCardType =(String) setDefaultValue( strJioMoneyCardType, "NA") ;
		}

		public double getDblRoundOff() {
			return dblRoundOff;
		}

		public void setDblRoundOff(double dblRoundOff) {
			this.dblRoundOff =(Double) setDefaultValue( dblRoundOff, 0.000) ;
		}

		public int getIntBillSeriesPaxNo() {
			return intBillSeriesPaxNo;
		}

		public void setIntBillSeriesPaxNo(int intBillSeriesPaxNo) {
			this.intBillSeriesPaxNo =(Integer) setDefaultValue( intBillSeriesPaxNo, 0) ;
		}

		public String getDtBillDate() {
			return dtBillDate;
		}

		public void setDtBillDate(String dtBillDate) {
			this.dtBillDate =(String) setDefaultValue( dtBillDate, "NA") ;
		}

		public int getIntOrderNo() {
			return intOrderNo;
		}

		public void setIntOrderNo(int intOrderNo) {
			this.intOrderNo = (Integer) setDefaultValue( intOrderNo, 0);
		}

		public List<clsBillDtlModel> getListBillDtlModel() {
			return listBillDtlModel;
		}

		public void setListBillDtlModel(List<clsBillDtlModel> listBillDtlModel) {
			this.listBillDtlModel = listBillDtlModel;
		}

		public String getDteBillDate() {
			return dteBillDate;
		}

		public void setDteBillDate(String dteBillDate) {
			this.dteBillDate = dteBillDate;
		}
		public List<clsBillDiscDtlModel> getListBillDiscDtlModel() {
			return listBillDiscDtlModel;
		}

		public void setListBillDiscDtlModel(
				List<clsBillDiscDtlModel> listBillDiscDtlModel) {
			this.listBillDiscDtlModel = listBillDiscDtlModel;
		}

		public List<clsBillModifierDtlModel> getListBillModifierDtlModel() {
			return listBillModifierDtlModel;
		}

		public void setListBillModifierDtlModel(
				List<clsBillModifierDtlModel> listBillModifierDtlModel) {
			this.listBillModifierDtlModel = listBillModifierDtlModel;
		}

		public List<clsBillComplementaryDtlModel> getListBillComplementaryDtlModel() {
			return listBillComplementaryDtlModel;
		}

		public void setListBillComplementaryDtlModel(
				List<clsBillComplementaryDtlModel> listBillComplementaryDtlModel) {
			this.listBillComplementaryDtlModel = listBillComplementaryDtlModel;
		}

		public List<clsBillPromotionDtlModel> getListBillPromotionDtlModel() {
			return listBillPromotionDtlModel;
		}

		public void setListBillPromotionDtlModel(
				List<clsBillPromotionDtlModel> listBillPromotionDtlModel) {
			this.listBillPromotionDtlModel = listBillPromotionDtlModel;
		}


		public List<clsBillTaxDtl> getListBillTaxDtl() {
			return listBillTaxDtl;
		}

		public void setListBillTaxDtl(List<clsBillTaxDtl> listBillTaxDtl) {
			this.listBillTaxDtl = listBillTaxDtl;
		}

		public List<clsBillSettlementDtlModel> getListBillSettlementDtlModel() {
			return listBillSettlementDtlModel;
		}

		public void setListBillSettlementDtlModel(
				List<clsBillSettlementDtlModel> listBillSettlementDtlModel) {
			this.listBillSettlementDtlModel = listBillSettlementDtlModel;
		}
		
	    

}
