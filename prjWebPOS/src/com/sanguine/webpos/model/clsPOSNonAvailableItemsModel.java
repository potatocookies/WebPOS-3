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
@Table(name="tblnonavailableitems")
@IdClass(clsPOSNonAvailableItemModel_ID.class)
public class clsPOSNonAvailableItemsModel extends clsBaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSNonAvailableItemsModel(){}

	public clsPOSNonAvailableItemsModel(clsPOSNonAvailableItemModel_ID objModelID){
		strItemCode = objModelID.getStrItemCode();
		strClientCode = objModelID.getStrClientCode();
		strPOSCode = objModelID.getStrPOSCode();
	}
	@CollectionOfElements(fetch=FetchType.LAZY)
	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strItemCode",column=@Column(name="strItemCode")),
		@AttributeOverride(name="strClientCode",column=@Column(name="strClientCode")),
		@AttributeOverride(name="strPOSCode",column=@Column(name="strPOSCode"))
	})

//Variable Declaration
	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strItemName")
	private String strItemName;
	
	@Column(name="strClientCode")
	private String strClientCode;
	
	@Column(name="dteDate")
	private String dteDate;
	
	@Column(name="strPOSCode")
	private String strPOSCode;
	public String getStrItemCode() {
		return strItemCode;
	}

	public void setStrItemCode(String strItemCode) {
		this.strItemCode = strItemCode;
	}

	public String getStrItemName() {
		return strItemName;
	}

	public void setStrItemName(String strItemName) {
		this.strItemName = strItemName;
	}

	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	public String getDteDate() {
		return dteDate;
	}

	public void setDteDate(String dteDate) {
		this.dteDate = dteDate;
	}

	public String getStrPOSCode() {
		return strPOSCode;
	}

	public void setStrPOSCode(String strPOSCode) {
		this.strPOSCode = strPOSCode;
	}
	

}
