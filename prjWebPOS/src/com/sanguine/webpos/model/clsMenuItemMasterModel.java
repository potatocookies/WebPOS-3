package com.sanguine.webpos.model;



import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblitemmaster")
@IdClass(clsMenuItemMasterModel_ID.class)
@NamedQueries({ 
	@NamedQuery (name="POSMenuItemMaster",query="select m.strItemCode,m.strItemName,m.strItemType,m.strRevenueHead,m.strTaxIndicator,m.strExternalCode,n.strSubGroupName "
		+ "from clsMenuItemMasterModel m,clsSubGroupMasterHdModel n where m.strSubGroupCode=n.strSubGroupCode and m.strClientCode=:clientCode" ),
	@NamedQuery (name="getMenuItemMaster",query="from clsMenuItemMasterModel where strItemCode=:itemCode and strClientCode=:clientCode"),

	@NamedQuery (name="getALLItemName",	query="from clsMenuItemMasterModel m where strClientCode=:clientCode" )	
})
public class clsMenuItemMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsMenuItemMasterModel(){}

	public clsMenuItemMasterModel(clsMenuItemMasterModel_ID objModelID){
		strItemCode = objModelID.getStrItemCode();
		strClientCode = objModelID.getStrClientCode();
	}
	@CollectionOfElements(fetch=FetchType.LAZY)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strItemCode",column=@Column(name="strItemCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strItemName")
	private String strItemName;

	@Column(name="strSubGroupCode")
	private String strSubGroupCode;

	@Column(name="strItemImage")
	private String strItemImage;

	@Column(name="strTaxIndicator")
	private String strTaxIndicator;

	@Column(name="strStockInEnable")
	private String strStockInEnable;

	@Column(name="dblPurchaseRate")
	private double dblPurchaseRate;

	@Column(name="intProcTimeMin")
	private long intProcTimeMin;

	@Column(name="strExternalCode")
	private String strExternalCode;

	@Column(name="strItemDetails")
	private String strItemDetails;

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

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strItemType")
	private String strItemType;

	@Column(name="strDiscountApply")
	private String strDiscountApply;

	@Column(name="strShortName")
	private String strShortName;

	@Column(name="dblMinLevel")
	private double dblMinLevel;

	@Column(name="dblMaxLevel")
	private double dblMaxLevel;

	@Column(name="intProcDay")
	private long intProcDay;

	@Column(name="strRawMaterial")
	private String strRawMaterial;

	@Column(name="dblSalePrice")
	private double dblSalePrice;

	@Column(name="strItemForSale")
	private String strItemForSale;

	@Column(name="strRevenueHead")
	private String strRevenueHead;

	@Column(name="strItemWeight")
	private String strItemWeight;

	@Column(name="strOpenItem")
	private String strOpenItem;

	@Column(name="strItemWiseKOTYN")
	private String strItemWiseKOTYN;

	@Column(name="strWSProdCode")
	private String strWSProdCode;

	@Column(name="strExciseBrandCode")
	private String strExciseBrandCode;

	@Column(name="strNoDeliveryDays")
	private String strNoDeliveryDays;

	@Column(name="intDeliveryDays")
	private long intDeliveryDays;

	@Column(name="dblIncrementalWeight")
	private double dblIncrementalWeight;

	@Column(name="dblMinWeight")
	private double dblMinWeight;

	@Column(name="strUrgentOrder")
	private String strUrgentOrder;

	@Column(name="strUOM")
	private String strUOM;
	
	@Column(name="imgImage")
	private String imgImage;
	
	@Column(name="tmeTargetMiss")
	private String tmeTargetMiss;
	
//	@Column(name="strRecipeUOM")
//	private String strRecipeUOM;
//	
//	@Column(name="dblReceivedConversion")
//	private String dblReceivedConversion;
//	
//	@Column(name="dblRecipeConversion")
//	private String dblRecipeConversion;
//	
//	@Column(name="strHSNNo")
//	private String strHSNNo;
	
	public String getImgImage() {
		return imgImage;
	}

	public void setImgImage(String imgImage) {
		this.imgImage =(String) setDefaultValue(imgImage, "");
	}

	public String getStrUOM() {
		return strUOM;
	}

	public void setStrUOM(String strUOM) {
		this.strUOM = (String) setDefaultValue( strUOM, "");;
	}

	//Setter-Getter Methods
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

	public String getStrSubGroupCode(){
		return strSubGroupCode;
	}
	public void setStrSubGroupCode(String strSubGroupCode){
		this. strSubGroupCode = (String) setDefaultValue( strSubGroupCode, "NA");
	}

	public String getStrItemImage(){
		return strItemImage;
	}
	public void setStrItemImage(String strItemImage){
		this. strItemImage = (String) setDefaultValue( strItemImage, "");
	}

	public String getStrTaxIndicator(){
		return strTaxIndicator;
	}
	public void setStrTaxIndicator(String strTaxIndicator){
		this. strTaxIndicator = (String) setDefaultValue( strTaxIndicator, "NA");
	}

	public String getStrStockInEnable(){
		return strStockInEnable;
	}
	public void setStrStockInEnable(String strStockInEnable){
		this. strStockInEnable = (String) setDefaultValue( strStockInEnable, "NA");
	}

	public double getDblPurchaseRate(){
		return dblPurchaseRate;
	}
	public void setDblPurchaseRate(double dblPurchaseRate){
		this. dblPurchaseRate = (Double) setDefaultValue( dblPurchaseRate, "0.0000");
	}

	public long getIntProcTimeMin(){
		return intProcTimeMin;
	}
	public void setIntProcTimeMin(long intProcTimeMin){
		this. intProcTimeMin = (Long) setDefaultValue( intProcTimeMin, "0");
	}

	public String getStrExternalCode(){
		return strExternalCode;
	}
	public void setStrExternalCode(String strExternalCode){
		this. strExternalCode = (String) setDefaultValue( strExternalCode, "NA");
	}

	public String getStrItemDetails(){
		return strItemDetails;
	}
	public void setStrItemDetails(String strItemDetails){
		this. strItemDetails = (String) setDefaultValue( strItemDetails, "NA");
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

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrItemType(){
		return strItemType;
	}
	public void setStrItemType(String strItemType){
		this. strItemType = (String) setDefaultValue( strItemType, "NA");
	}

	public String getStrDiscountApply(){
		return strDiscountApply;
	}
	public void setStrDiscountApply(String strDiscountApply){
		this. strDiscountApply = (String) setDefaultValue( strDiscountApply, "Y");
	}

	public String getStrShortName(){
		return strShortName;
	}
	public void setStrShortName(String strShortName){
		this. strShortName = (String) setDefaultValue( strShortName, "NA");
	}

	public double getDblMinLevel(){
		return dblMinLevel;
	}
	public void setDblMinLevel(double dblMinLevel){
		this. dblMinLevel = (Double) setDefaultValue( dblMinLevel, "0.0000");
	}

	public double getDblMaxLevel(){
		return dblMaxLevel;
	}
	public void setDblMaxLevel(double dblMaxLevel){
		this. dblMaxLevel = (Double) setDefaultValue( dblMaxLevel, "0.0000");
	}

	public long getIntProcDay(){
		return intProcDay;
	}
	public void setIntProcDay(long intProcDay){
		this. intProcDay = (Long) setDefaultValue( intProcDay, "0");
	}

	public String getStrRawMaterial(){
		return strRawMaterial;
	}
	public void setStrRawMaterial(String strRawMaterial){
		this. strRawMaterial = (String) setDefaultValue( strRawMaterial, "NA");
	}

	public double getDblSalePrice(){
		return dblSalePrice;
	}
	public void setDblSalePrice(double dblSalePrice){
		this. dblSalePrice = (Double) setDefaultValue( dblSalePrice, "0.0000");
	}

	public String getStrItemForSale(){
		return strItemForSale;
	}
	public void setStrItemForSale(String strItemForSale){
		this. strItemForSale = (String) setDefaultValue( strItemForSale, "NA");
	}

	public String getStrRevenueHead(){
		return strRevenueHead;
	}
	public void setStrRevenueHead(String strRevenueHead){
		this. strRevenueHead = (String) setDefaultValue( strRevenueHead, "NA");
	}

	public String getStrItemWeight(){
		return strItemWeight;
	}
	public void setStrItemWeight(String strItemWeight){
		this. strItemWeight = (String) setDefaultValue( strItemWeight, "NA");
	}

	public String getStrOpenItem(){
		return strOpenItem;
	}
	public void setStrOpenItem(String strOpenItem){
		this. strOpenItem = (String) setDefaultValue( strOpenItem, "NA");
	}

	public String getStrItemWiseKOTYN(){
		return strItemWiseKOTYN;
	}
	public void setStrItemWiseKOTYN(String strItemWiseKOTYN){
		this. strItemWiseKOTYN = (String) setDefaultValue( strItemWiseKOTYN, "NA");
	}

	public String getStrWSProdCode(){
		return strWSProdCode;
	}
	public void setStrWSProdCode(String strWSProdCode){
		this. strWSProdCode = (String) setDefaultValue( strWSProdCode, "NA");
	}

	public String getStrExciseBrandCode(){
		return strExciseBrandCode;
	}
	public void setStrExciseBrandCode(String strExciseBrandCode){
		this. strExciseBrandCode = (String) setDefaultValue( strExciseBrandCode, "NA");
	}

	public String getStrNoDeliveryDays(){
		return strNoDeliveryDays;
	}
	public void setStrNoDeliveryDays(String strNoDeliveryDays){
		this. strNoDeliveryDays = (String) setDefaultValue( strNoDeliveryDays, "NA");
	}

	public long getIntDeliveryDays(){
		return intDeliveryDays;
	}
	public void setIntDeliveryDays(long intDeliveryDays){
		this. intDeliveryDays = (Long) setDefaultValue( intDeliveryDays, "0");
	}

	public double getDblIncrementalWeight(){
		return dblIncrementalWeight;
	}
	public void setDblIncrementalWeight(double dblIncrementalWeight){
		this. dblIncrementalWeight = (Double) setDefaultValue( dblIncrementalWeight, "0.0000");
	}

	public double getDblMinWeight(){
		return dblMinWeight;
	}
	public void setDblMinWeight(double dblMinWeight){
		this. dblMinWeight = (Double) setDefaultValue( dblMinWeight, "0.0000");
	}

	public String getStrUrgentOrder(){
		return strUrgentOrder;
	}
	public void setStrUrgentOrder(String strUrgentOrder){
		this. strUrgentOrder = (String) setDefaultValue( strUrgentOrder, "NA");
	}




	public String getTmeTargetMiss() {
		return tmeTargetMiss;
	}

	public void setTmeTargetMiss(String tmeTargetMiss) {
		this.tmeTargetMiss = tmeTargetMiss;
	}

//	public String getStrRecipeUOM() {
//		return strRecipeUOM;
//	}
//
//	public void setStrRecipeUOM(String strRecipeUOM) {
//		this.strRecipeUOM = strRecipeUOM;
//	}
//
//	public String getDblReceivedConversion() {
//		return dblReceivedConversion;
//	}
//
//	public void setDblReceivedConversion(String dblReceivedConversion) {
//		this.dblReceivedConversion = dblReceivedConversion;
//	}
//
//	public String getDblRecipeConversion() {
//		return dblRecipeConversion;
//	}
//
//	public void setDblRecipeConversion(String dblRecipeConversion) {
//		this.dblRecipeConversion = dblRecipeConversion;
//	}
//
//	public String getStrHSNNo() {
//		return strHSNNo;
//	}
//
//	public void setStrHSNNo(String strHSNNo) {
//		this.strHSNNo = strHSNNo;
//	}

}
