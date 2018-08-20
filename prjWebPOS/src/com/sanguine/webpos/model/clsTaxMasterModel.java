package com.sanguine.webpos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

import com.sanguine.base.model.clsBaseModel;


@Entity
@Table(name="tbltaxhd")
@IdClass(clsTaxMasterModel_ID.class)

@NamedQueries(
	{@NamedQuery (name="getAllTaxMaster" , query="select m.strTaxCode,m.strTaxDesc from clsTaxMasterModel m "
			+ "where m.strClientCode=:clientCode" ),

	@NamedQuery(name = "getTaxMaster", query = "from clsTaxMasterModel where strTaxCode=:taxCode and strClientCode=:clientCode")}
)

public class clsTaxMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsTaxMasterModel(){}

	public clsTaxMasterModel(clsTaxMasterModel_ID objModelID){
		strTaxCode = objModelID.getStrTaxCode();
		strClientCode = objModelID.getStrClientCode();
	}
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tblsettlementtax", joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strTaxCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTaxCode",column=@Column(name="strTaxCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})
	//List<clsTaxSettlementDetailsModel> listsettlementDtl = new ArrayList<clsTaxSettlementDetailsModel>();
	Set<clsTaxSettlementDetailsModel> listsettlementDtl = new HashSet<clsTaxSettlementDetailsModel>();
	
	
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tbltaxposdtl", joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strTaxCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTaxCode",column=@Column(name="strTaxCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})
	Set<clsTaxPosDetailsModel> listTaxPosDtl = new HashSet<clsTaxPosDetailsModel>();
	
	@CollectionOfElements(fetch=FetchType.LAZY)
    @JoinTable(name="tbltaxongroup", joinColumns={@JoinColumn(name="strClientCode"),@JoinColumn(name="strTaxCode")})
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strTaxCode",column=@Column(name="strTaxCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})
	Set<clsTaxOnGroupModel> listTaxGroupDtl = new HashSet<clsTaxOnGroupModel>();
	
	public Set<clsTaxSettlementDetailsModel> getListsettlementDtl() {
		return listsettlementDtl;
	}

	public void setListsettlementDtl(Set<clsTaxSettlementDetailsModel> listsettlementDtl) {
		this.listsettlementDtl = listsettlementDtl;
	}

	public Set<clsTaxPosDetailsModel> getListTaxPosDtl() {
		return listTaxPosDtl;
	}

	public void setListTaxPosDtl(Set<clsTaxPosDetailsModel> listTaxPosDtl) {
		this.listTaxPosDtl = listTaxPosDtl;
	}
	
	
	
	public Set<clsTaxOnGroupModel> getListTaxGroupDtl() {
		return listTaxGroupDtl;
	}

	public void setListTaxGroupDtl(Set<clsTaxOnGroupModel> listTaxGroupDtl) {
		this.listTaxGroupDtl = listTaxGroupDtl;
	}
	//Variable Declaration
	@Column(name="strTaxCode")
	private String strTaxCode;

	
	@Column(name="strTaxDesc")
	private String strTaxDesc;

	@Column(name="strTaxOnSP")
	private String strTaxOnSP;

	@Column(name="strTaxType")
	private String strTaxType;

	@Column(name="dblPercent")
	private double dblPercent;

	@Column(name="dblAmount")
	private double dblAmount;

	@Column(name="dteValidFrom")
	private String dteValidFrom;

	@Column(name="dteValidTo")
	private String dteValidTo;

	@Column(name="strTaxOnGD")
	private String strTaxOnGD;

	@Column(name="strTaxCalculation")
	private String strTaxCalculation;

	@Column(name="strTaxIndicator")
	private String strTaxIndicator;

	@Column(name="strTaxRounded")
	private String strTaxRounded;

	@Column(name="strTaxOnTax")
	private String strTaxOnTax;

	@Column(name="strTaxOnTaxCode")
	private String strTaxOnTaxCode;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strAreaCode")
	private String strAreaCode;

	@Column(name="strOperationType")
	private String strOperationType;

	@Column(name="strItemType")
	private String strItemType;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strAccountCode")
	private String strAccountCode;

	@Column(name="strTaxShortName")
	private String strTaxShortName;
	
	@Column(name="strBillNote")
	private String strBillNote;

//Setter-Getter Methods
	public String getStrTaxCode(){
		return strTaxCode;
	}
	public void setStrTaxCode(String strTaxCode){
		this. strTaxCode = (String) setDefaultValue( strTaxCode, "");
	}

	public String getStrTaxDesc(){
		return strTaxDesc;
	}
	public void setStrTaxDesc(String strTaxDesc){
		this. strTaxDesc = (String) setDefaultValue( strTaxDesc, "");
	}

	public String getStrTaxOnSP(){
		return strTaxOnSP;
	}
	public void setStrTaxOnSP(String strTaxOnSP){
		this. strTaxOnSP = (String) setDefaultValue( strTaxOnSP, "");
	}

	public String getStrTaxType(){
		return strTaxType;
	}
	public void setStrTaxType(String strTaxType){
		this. strTaxType = (String) setDefaultValue( strTaxType, "");
	}

	public double getDblPercent(){
		return dblPercent;
	}
	public void setDblPercent(double dblPercent){
		this. dblPercent = (Double) setDefaultValue( dblPercent, "0.0000");
	}

	public double getDblAmount(){
		return dblAmount;
	}
	public void setDblAmount(double dblAmount){
		this. dblAmount = (Double) setDefaultValue( dblAmount, "0.0000");
	}

	public String getDteValidFrom(){
		return dteValidFrom;
	}
	public void setDteValidFrom(String dteValidFrom){
		this.dteValidFrom=dteValidFrom;
	}

	public String getDteValidTo(){
		return dteValidTo;
	}
	public void setDteValidTo(String dteValidTo){
		this.dteValidTo=dteValidTo;
	}

	public String getStrTaxOnGD(){
		return strTaxOnGD;
	}
	public void setStrTaxOnGD(String strTaxOnGD){
		this. strTaxOnGD = (String) setDefaultValue( strTaxOnGD, "");
	}

	public String getStrTaxCalculation(){
		return strTaxCalculation;
	}
	public void setStrTaxCalculation(String strTaxCalculation){
		this. strTaxCalculation = (String) setDefaultValue( strTaxCalculation, "");
	}

	public String getStrTaxIndicator(){
		return strTaxIndicator;
	}
	public void setStrTaxIndicator(String strTaxIndicator){
		this. strTaxIndicator = (String) setDefaultValue( strTaxIndicator, "");
	}

	public String getStrTaxRounded(){
		return strTaxRounded;
	}
	public void setStrTaxRounded(String strTaxRounded){
		this. strTaxRounded = (String) setDefaultValue( strTaxRounded, "");
	}

	public String getStrTaxOnTax(){
		return strTaxOnTax;
	}
	public void setStrTaxOnTax(String strTaxOnTax){
		this. strTaxOnTax = (String) setDefaultValue( strTaxOnTax, "");
	}

	public String getStrTaxOnTaxCode(){
		return strTaxOnTaxCode;
	}
	public void setStrTaxOnTaxCode(String strTaxOnTaxCode){
		this. strTaxOnTaxCode = (String) setDefaultValue( strTaxOnTaxCode, "");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "");
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

	public String getStrAreaCode(){
		return strAreaCode;
	}
	public void setStrAreaCode(String strAreaCode){
		this. strAreaCode = (String) setDefaultValue( strAreaCode, "");
	}

	public String getStrOperationType(){
		return strOperationType;
	}
	public void setStrOperationType(String strOperationType){
		this. strOperationType = (String) setDefaultValue( strOperationType, "");
	}

	public String getStrItemType(){
		return strItemType;
	}
	public void setStrItemType(String strItemType){
		this. strItemType = (String) setDefaultValue( strItemType, "");
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}

	public String getStrAccountCode(){
		return strAccountCode;
	}
	public void setStrAccountCode(String strAccountCode){
		this. strAccountCode = (String) setDefaultValue( strAccountCode, "");
	}

	public String getStrTaxShortName(){
		return strTaxShortName;
	}
	public void setStrTaxShortName(String strTaxShortName){
		this. strTaxShortName = (String) setDefaultValue( strTaxShortName, "");
	}

	
	

   public String getStrBillNote() {
		return strBillNote;
	}

	public void setStrBillNote(String strBillNote) {
		this.strBillNote = (String) setDefaultValue( strBillNote, "");
		
	}

	
	
	

}
