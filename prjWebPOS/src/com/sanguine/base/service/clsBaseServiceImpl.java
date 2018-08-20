package com.sanguine.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.dao.intfBaseDao;
import com.sanguine.base.model.clsBaseModel;


@Service("intfBaseService")
public class clsBaseServiceImpl implements intfBaseService{
	
	@Autowired
	intfBaseDao objBaseDao;
	
	@Override
	public String funSave(clsBaseModel objBaseModel) throws Exception
	{
	return objBaseDao.funSave(objBaseModel);
	}
	
	@Override
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key) throws Exception
	{
		return objBaseDao.funLoad(objBaseModel, key);
	}
	
	@Override
	public clsBaseModel funGet(clsBaseModel objBaseModel,Serializable key) throws Exception
	{
		return objBaseDao.funGet(objBaseModel, key);
	}
	
	@Override
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode) throws Exception
	{
		return objBaseDao.funLoadAll(objBaseModel,clientCode);
	}
	
	@Override
	public List funGetSerachList(String query,String clientCode) throws Exception
	{
		return objBaseDao.funGetSerachList(query,clientCode);
	}
	
	public List funGetList(StringBuilder query,String queryType) throws Exception
	{
		return objBaseDao.funGetList(query, queryType);
	}

	@Override
	public int funExecuteUpdate(String query, String queryType)throws Exception {

		return objBaseDao.funExecuteUpdate(query,queryType);
	}
	
	
	public List funLoadAllPOSWise(clsBaseModel objBaseModel,String clientCode,String strPOSCode)throws Exception
	{
		return objBaseDao.funLoadAllPOSWise(objBaseModel,clientCode,strPOSCode);
	}
	
	public List funLoadAllCriteriaWise(clsBaseModel objBaseModel, String criteriaName,
			String criteriaValue){
		return objBaseDao.funLoadAllCriteriaWise(objBaseModel,criteriaName,criteriaValue);
	}
	
	public clsBaseModel funGetAllMasterDataByDocCodeWise(String sql,Map<String,String> hmParameters)
	{
		return objBaseDao.funGetAllMasterDataByDocCodeWise(sql, hmParameters);
	}
	
	public clsBaseModel funGetMenuItemPricingMaster(String sql,long id,String clientCode)
	{
		return objBaseDao.funGetMenuItemPricingMaster(sql,id,clientCode);
	}
	
	public JSONObject funSalesReport(String fromDate,String toDae,String strPOSCode, String strShiftNo,String strUserCode,String field,
			String strPayMode,String strOperator,String strFromBill,String strToBill,String reportType,String Type,String Customer,String ConsolidatePOS,String ReportName,String LoginPOSCode,String areaCode,String operationTye)
	{
		return objBaseDao.funSalesReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,field,strPayMode,strOperator,
				strFromBill,strToBill,reportType,Type,Customer,ConsolidatePOS,ReportName,LoginPOSCode,areaCode,operationTye);
	}
	
	
}
