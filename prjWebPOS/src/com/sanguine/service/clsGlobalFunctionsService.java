package com.sanguine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.model.clsTreeMasterModel;
import com.sanguine.model.clsUserLogsModel;



@SuppressWarnings("rawtypes")
public interface clsGlobalFunctionsService 
{
	public long funGetLastNo(String tableName,String masterName,String columnName, String clientCode);
	
	public Long funGetCount(String tableName,String columnName);
	
	public Long funGetCountByClient(String tableName,String columnName,String clientCode);
	
	public List funGetList(String sql,String queryType,String pageNo);
	
	
	public List<clsTreeMasterModel> funGetFormList(String userCode);
	
	public List<clsTreeMasterModel> funGetFormList();
	
	public List funGetPurchaseOrderList(String POCode,String clientCode);
	
	public List funGetConsPOList(String consPOCode,String clientCode);
	
	
	public List funGetPurchaseReturnList(String PRCode,String clientCode);
		
	public List funGetCurrentStock(String prodCode,String clientCode,String userCode);
	
	public int funAddCurrentStock(String clientCode,String userCode,String locCode, String stockableItem);
	
	public int funUpdateCurrentStock(String hql);
	
	public int funAddTempItemStock(String hql,String queryType);
	
	public int funDeleteCurrentStock(String clientCode,String userCode);
	
	public int funDeleteTempItemStock(String clientCode,String userCode);
	
	public Map<String,String> funGetCompanyList(String clientCode);
	
	public Map<String,String> funGetPropertyList(String clientCode);
	
	public HashMap<String,String> funGetLocationList(String propCode,String clientCode);
	
	public Map<Integer,String> funGetFinancialYearList(String clientCode);
	
	public clsCompanyMasterModel funGetCompanyObject(String companyCode);
	
	public List funGetList(String query);
	
	public List funGetProductDataForTransaction(String sql,String prodCode,String clientCode);
	
	public List funGetSetUpProcess(String strFrom, String strPropertyCode,String clientCode);
	
	public List funGetProdQtyForStock(String sql,String queryType);
	
	public long funGet_no_Of_Pages_forSearch(String colmnName,String tableName,boolean flgQuerySelection,String clientCode);

	public List funGetList(String sql, String string);
	
	public List funGetDataList(String sql, String string);
	
	public List funCheckName(String name, String strClientCode, String formName);

	public Map<String, String> funGetUserWisePropertyList(String clientCode,
			String usercode, String userPrpoerty);

	public int funCheckFromInWorkflowforSlabbasedAuth(String strForm,
			String strPropertyCode);
	
	public int funAddUserLogEntry(clsUserLogsModel objModel);

	public int funUpdate(String sql,String queryType);
	
	public int funUpdateAllModule(String sql, String queryType);

	
	public List funGetListReportQuery(String sql);
	
	public List funGetListModuleWise(String sql, String queryType);
	
	public int funExcuteQuery(String sql);
	
	public HashMap<String,String> funGetSupplierList(String propCode,String clientCode);
	
	public long funGetMaxCountNo(String tableName,String masterName,String columnName, String clientCode);

	public long funGetPMSMasterLastNo(String tableName,String masterName,String columnName, String clientCode);

	public long funGetNextNo(String tableName, String masterName, String columnName, String clientCode);

	public long funGetNextNo(String tableName, String masterName, String columnName, String clientCode,String condition);
	
	public long funGetLastNoModuleWise(String tableName,String masterName,String columnName,String clientCode,String module);
	
//	public int funDeleteWebBookTempLedger(String clientCode,String userCode,String propertyCode);
	
	
	
	
	
	
	/*public int funAddCurrentDebtorCredtor(String clientCode,String userCode,String propertyCode);
	
	public int funAddTempLedger(String hql,String queryType);*/
	
	
}
