package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.sanguine.base.model.clsBaseModel;

@Entity
@Table(name="tblwaitermaster")
@IdClass(clsWaiterMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getAllWaiterMaster", 
query = "select m.strWaiterNo,m.strWShortName,m.strOperational " 
		+ "from clsWaiterMasterModel m where m.strClientCode=:clientCode "),

		@NamedQuery(name = "getWaiterMaster", 
		query = "from clsWaiterMasterModel where strWaiterNo=:waiterNo and strClientCode=:clientCode") })
public class clsWaiterMasterModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsWaiterMasterModel(){}

	public clsWaiterMasterModel(clsWaiterMasterModel_ID objModelID){
		strWaiterNo = objModelID.getStrWaiterNo();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strWaiterNo",column=@Column(name="strWaiterNo")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode"))
	})

//Variable Declaration
	@Column(name="strWaiterNo")
	private String strWaiterNo;

	@Column(name="strWShortName")
	private String strWShortName;

	@Column(name="strWFullName")
	private String strWFullName;

	@Column(name="strStatus")
	private String strStatus;

	@Column(name="strOperational")
	private String strOperational;

	@Column(name="strDebitCardString")
	private String strDebitCardString;

	@Column(name="strUserCreated",updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated",updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strPOSCode")
	private String strPOSCode;

//Setter-Getter Methods
	public String getStrWaiterNo(){
		return strWaiterNo;
	}
	public void setStrWaiterNo(String strWaiterNo){
		this. strWaiterNo = (String) setDefaultValue( strWaiterNo, "NA");
	}

	public String getStrWShortName(){
		return strWShortName;
	}
	public void setStrWShortName(String strWShortName){
		this. strWShortName = (String) setDefaultValue( strWShortName, "NA");
	}

	public String getStrWFullName(){
		return strWFullName;
	}
	public void setStrWFullName(String strWFullName){
		this. strWFullName = (String) setDefaultValue( strWFullName, "NA");
	}

	public String getStrStatus(){
		return strStatus;
	}
	public void setStrStatus(String strStatus){
		this. strStatus = (String) setDefaultValue( strStatus, "NA");
	}

	public String getStrOperational(){
		return strOperational;
	}
	public void setStrOperational(String strOperational){
		this. strOperational = (String) setDefaultValue( strOperational, "NA");
	}

	public String getStrDebitCardString(){
		return strDebitCardString;
	}
	public void setStrDebitCardString(String strDebitCardString){
		this. strDebitCardString = (String) setDefaultValue( strDebitCardString, "NA");
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

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}




}
