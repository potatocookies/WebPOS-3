package com.sanguine.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.sanguine.base.model.clsBaseModel;

public interface intfBaseService {

	public String funSave(clsBaseModel objBaseModel) throws Exception;
	
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key) throws Exception;
	
	public clsBaseModel funGet(clsBaseModel objBaseModel,Serializable key) throws Exception;
	
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode) throws Exception;

	public List funGetSerachList(String query,String clientCode) throws Exception;
	
	public List funGetList(StringBuilder query,String queryType) throws Exception;
	
	public int funExecuteUpdate(String query,String queryType) throws Exception;
	
	public List funLoadAllPOSWise(clsBaseModel objBaseModel,String clientCode,String strPOSCode) throws Exception;
	
	public List funLoadAllCriteriaWise(clsBaseModel objBaseModel, String criteriaName,
			String criteriaValue);
	public clsBaseModel funGetAllMasterDataByDocCodeWise(String sql,Map<String,String> hmParameters);
	
	public clsBaseModel funGetMenuItemPricingMaster(String sql,long id,String clientCode);
	
	public JSONObject funSalesReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String field,
			String strPayMode,String strOperator,String strFromBill,String strToBill,String reportType,String Type,String Customer,String ConsolidatePOS,String ReportName,String LoginPOSCode,String areaCode,String operationTye);
	
}
