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
@Table(name="tbltablemaster")
@IdClass(clsTableMasterModel_ID.class)
@NamedQueries({ @NamedQuery(name = "getTableMaster", 
		query = "from clsTableMasterModel where strTableNo=:tableNo and strClientCode=:clientCode") })
public class clsTableMasterModel extends clsBaseModel  implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsTableMasterModel(){}

	public clsTableMasterModel(clsTableMasterModel_ID objModelID){
		strTableNo = objModelID.getStrTableNo();
		strClientCode = objModelID.getStrClientCode();
	}

	@Id
	@AttributeOverrides({
	
		@AttributeOverride(name = "strTableNo", column = @Column(name = "strTableNo")), @AttributeOverride(name = "strClientCode", column = @Column(name = "strClientCode")) 
		})
	
//Variable Declaration
	@Column(name="strTableNo")
	private String strTableNo;

	@Column(name="strTableName")
	private String strTableName;

	@Column(name="strStatus")
	private String strStatus;

	@Column(name="strAreaCode")
	private String strAreaCode;

	@Column(name="strWaiterNo")
	private String strWaiterNo;

	@Column(name="intPaxNo")
	private long intPaxNo;

	@Column(name="strOperational")
	private String strOperational;

	@Column(name="strUserCreated", updatable=false)
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated", updatable=false)
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="intSequence")
	private long intSequence;

	@Column(name="strPOSCode")
	private String strPOSCode;
	
	@Column(name="strNCTable")
	private String strNCTable;

	
//Setter-Getter Methods
	public String getStrTableNo(){
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo){
		this. strTableNo = (String) setDefaultValue( strTableNo, "NA");
	}

	public String getStrTableName(){
		return strTableName;
	}
	public void setStrTableName(String strTableName){
		this. strTableName = (String) setDefaultValue( strTableName, "NA");
	}

	public String getStrStatus(){
		return strStatus;
	}
	public void setStrStatus(String strStatus){
		this. strStatus = (String) setDefaultValue( strStatus, "NA");
	}

	public String getStrAreaCode(){
		return strAreaCode;
	}
	public void setStrAreaCode(String strAreaCode){
		this. strAreaCode = (String) setDefaultValue( strAreaCode, "NA");
	}

	public String getStrWaiterNo(){
		return strWaiterNo;
	}
	public void setStrWaiterNo(String strWaiterNo){
		this. strWaiterNo = (String) setDefaultValue( strWaiterNo, "NA");
	}

	public long getIntPaxNo(){
		return intPaxNo;
	}
	public void setIntPaxNo(long intPaxNo){
		this. intPaxNo = (Long) setDefaultValue( intPaxNo, "0");
	}

	public String getStrOperational(){
		return strOperational;
	}
	public void setStrOperational(String strOperational){
		this. strOperational = (String) setDefaultValue( strOperational, "NA");
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

	public long getIntSequence(){
		return intSequence;
	}
	public void setIntSequence(long intSequence){
		this. intSequence = (Long) setDefaultValue( intSequence, "0");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	
	public String getStrNCTable() {
		return strNCTable;
	}

	public void setStrNCTable(String strNCTable) {
		this.strNCTable = (String) setDefaultValue(strNCTable, 'N');
	}



}
