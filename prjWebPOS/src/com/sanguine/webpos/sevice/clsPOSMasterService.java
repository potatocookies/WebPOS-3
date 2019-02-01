package com.sanguine.webpos.sevice;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.model.clsBaseModel;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSWiseItemIncentiveDtlBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsCostCenterMasterModel;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel;
import com.sanguine.webpos.model.clsDeliveryBoyMasterModel;
import com.sanguine.webpos.model.clsDiscountMasterModel;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsMenuHeadMasterModel;
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsModifierGroupMasterHdModel;
import com.sanguine.webpos.model.clsModifierMasterHdModel;
import com.sanguine.webpos.model.clsPOSMasterModel;
import com.sanguine.webpos.model.clsPricingMasterHdModel;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsSetupHdModel;
import com.sanguine.webpos.model.clsShiftMasterModel;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;
import com.sanguine.webpos.model.clsSubMenuHeadMasterModel;
import com.sanguine.webpos.model.clsTableMasterModel;
import com.sanguine.webpos.model.clsTableReservationModel;
import com.sanguine.webpos.model.clsTaxMasterModel;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.model.clsWaiterMasterModel;

@Service
public class clsPOSMasterService {

	@Autowired
	intfBaseService obBaseService;
	
	 public void funSaveReasonMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel);		
	 }
	 public clsReasonMasterModel funLoadReasonMaster(Map hmParameters)throws Exception
	 {
		 clsReasonMasterModel obReasonMasterModel=null;
		 obReasonMasterModel = (clsReasonMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getReason", hmParameters);
		 return obReasonMasterModel;
	 }
	 
	 public void funSaveSettlementMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel); 	
	 }
	 public clsSettlementMasterModel funLoadSettlementMaster(Map hmParameters)throws Exception
	 {
		 clsSettlementMasterModel obSettlementMasterModel=null;
		 obSettlementMasterModel = (clsSettlementMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getSettlementMaster", hmParameters);
		 return obSettlementMasterModel;
	 }
	 
	 public List funLoadSettlementDtl(String clientCode)throws Exception
	 {
		List list=null;
		list = obBaseService.funLoadAll(new clsSettlementMasterModel(),clientCode);
		return list;
	 }
	 
	 public void funSaveUpdateAreaMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel); 
	 } 
	 
	public clsAreaMasterModel funSelectedAreaMasterData(String areaCode,String clientCode)throws Exception
	{
		clsAreaMasterModel objAreaMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("areaCode",areaCode);
		hmParameters.put("clientCode",clientCode);
		objAreaMasterModel = (clsAreaMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getAreaMaster", hmParameters);			
		System.out.println();
	    return objAreaMasterModel; 	
	 }
		
	public void	funSaveUpdateCostCenterMaster(clsBaseModel objBaseModel)throws Exception
	{
		 obBaseService.funSave(objBaseModel); 
	}
		
	public clsCostCenterMasterModel funSelectedCostCenterMasterData(String costCenterCode,String clientCode)throws Exception
	{
		clsCostCenterMasterModel objCostCenterMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("costCenterCode",costCenterCode);
		hmParameters.put("clientCode",clientCode);
		objCostCenterMasterModel = (clsCostCenterMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCostCenterMaster", hmParameters);
		return objCostCenterMasterModel;
	}
		
	public void funSaveUpdateCustomerAreaMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
		
	public clsCustomerAreaMasterModel funSelectedCustomerAreaMasterData(String custAreaCode,String clientCode)throws Exception
	{
		clsCustomerAreaMasterModel objCustomerAreaMasterModel = null;
		 Map<String,String> hmParameters=new HashMap<String,String>();
		 hmParameters.put("buildingCode",custAreaCode);
		 hmParameters.put("clientCode",clientCode);
		 objCustomerAreaMasterModel = (clsCustomerAreaMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerArea", hmParameters);
		 System.out.println();
	     return objCustomerAreaMasterModel; 	
    }
		
	public 	void funSaveCustomerMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public clsCustomerMasterModel funSelectedCustomerMasterData(String customerCode,String clientCode)throws Exception
	{
		clsCustomerMasterModel objCustomerMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("customerCode",customerCode);
		hmParameters.put("clientCode",clientCode);
		objCustomerMasterModel = (clsCustomerMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerMaster", hmParameters);	
		return objCustomerMasterModel;
	}
	
	public List funGetPOSList(String clientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y' and strClientCode='"+clientCode+"' ");
		list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	
	
	public 	void funSaveUpdateCustomerTypeMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	
	public List<clsCustomerTypeMasterModel> funFillCustomerTypeCombo(String strClientCode)throws Exception
	{
		List<clsCustomerTypeMasterModel> list=null;
		list=obBaseService.funLoadAll(new clsCustomerTypeMasterModel(),strClientCode);
		return list;
		
	}
	
	public List funGetCityList(String strClientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select a.strCityName,a.strState,a.strCountry  from tblsetup a where a.strClientCode="+strClientCode);
        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public clsCustomerTypeMasterModel funSelectedCustomerTypeMasterData(String customerCode,String clientCode)throws Exception
	{
		clsCustomerTypeMasterModel objCustomerMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("custTypeCode",customerCode);
		hmParameters.put("clientCode",clientCode);	
		objCustomerMasterModel = (clsCustomerTypeMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getCustomerType", hmParameters);															
		return objCustomerMasterModel;
	}
	
	public void funSaveUpdateDeliverPersonMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public clsDeliveryBoyMasterModel funSelectedDeliveryBoyMasterData(String dpCode,String clientCode)throws Exception
	{
		clsDeliveryBoyMasterModel objDeliveryBoyMasterModel=null; 
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("dpCode",dpCode);
		hmParameters.put("clientCode",clientCode);
		objDeliveryBoyMasterModel = (clsDeliveryBoyMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getDeliveryBoyMaster", hmParameters);
		return objDeliveryBoyMasterModel;
	}
	
	public void funSaveUpdateGroupMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);	
	}
	
	public clsGroupMasterModel funSelectedGroupMasterData(String groupCode,String clientCode)throws Exception
	{
		clsGroupMasterModel objGroupMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("groupCode",groupCode);
		hmParameters.put("clientCode",clientCode);
		objGroupMasterModel = (clsGroupMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getGroupMaster", hmParameters);	
		return objGroupMasterModel;	
	}
	
	public void funSaveUpdateMenuHeadMasterData(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public void funSaveUpdateSubMenuHeadMasterData(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public clsMenuHeadMasterModel funSelectedMenuHeadMasterData(String menuHeadCode,String clientCode)throws Exception
	{
		clsMenuHeadMasterModel objMenuHeadMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("menuCode",menuHeadCode);
		hmParameters.put("clientCode",clientCode);
		objMenuHeadMasterModel = (clsMenuHeadMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getMenuHeadMaster", hmParameters);
		return objMenuHeadMasterModel;
	}
	
	public clsSubMenuHeadMasterModel funSelectedSubMenuHeadMasterData(String subMenuHeadCode,String clientCode)throws Exception
	{
		clsSubMenuHeadMasterModel objSubMenuHeadMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("subMenuCode",subMenuHeadCode);
		hmParameters.put("clientCode",clientCode);
		objSubMenuHeadMasterModel = (clsSubMenuHeadMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getSubMenuHeadMaster", hmParameters);
		return objSubMenuHeadMasterModel;
	}
	
	public clsMenuItemMasterModel funGetMenuItemMasterData(String itemCode,String clientCode)throws Exception
	{
		clsMenuItemMasterModel objMenuItemMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("itemCode",itemCode);
		hmParameters.put("clientCode",clientCode);
		objMenuItemMasterModel = (clsMenuItemMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemMaster", hmParameters);	
		return objMenuItemMasterModel;
	}
	
	public void funSaveUpdateMenuItemMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);	
	}
	
	public List<clsSubGroupMasterHdModel> funLoadAllSubGroup(String strClientCode)throws Exception
	{
		List<clsSubGroupMasterHdModel> list=null;
		list=obBaseService.funLoadAll(new clsSubGroupMasterHdModel(),strClientCode);
		return list;
		
	}
	
	public void funSaveUpdatePosMasterData(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public clsPOSMasterModel funSelectedPOSMasterData(String posCode,String clientCode)throws Exception
	{
		clsPOSMasterModel objPOSModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("posCode",posCode);
		hmParameters.put("clientCode",clientCode);
		objPOSModel = (clsPOSMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getPOSMaster", hmParameters);
		return objPOSModel;
	}
	
	public List funLoadAllMenuHeadForMaster(String strClientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strMenuCode, strMenuName from tblmenuhd where strOperational='Y' ORDER by intSequence");  
        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	public List<clsSubMenuHeadMasterModel> funLoadAllSubMenuHeadMaster(String clientCode)throws Exception
	{
		List<clsSubMenuHeadMasterModel> list = null;
		list =obBaseService.funLoadAll(new clsSubMenuHeadMasterModel(),clientCode);
		return list;
	}
	
	public List<clsAreaMasterModel> funGetAllAreaForMaster(String clientCode)throws Exception
	{
		List<clsAreaMasterModel> list = null;
		list = obBaseService.funLoadAll(new clsAreaMasterModel(), clientCode);	
		return list;
	}
	
	public List<clsCostCenterMasterModel> funGetAllCostCenterMaster(String clientCode)throws Exception
	{
		List<clsCostCenterMasterModel> list = null;
		list = obBaseService.funLoadAll(new clsCostCenterMasterModel(), clientCode);
		return list;
	}
	
	public clsPricingMasterHdModel funGetMenuItemPricingMaster(String pricingId,String clientCode)throws Exception
	{
		clsPricingMasterHdModel objPricingMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("longPricingId",pricingId);
		hmParameters.put("clientCode",clientCode);
		objPricingMasterModel = (clsPricingMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemPricing", hmParameters);
		return objPricingMasterModel;
	}
	
	public void funSaveUpdatePricingMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	public clsPricingMasterHdModel funCheckDuplicateItemPricing(String strItemCode,String strPosCode,String strAreaCode,String strHourlyPricing,String clientCode) throws Exception
	{
		clsPricingMasterHdModel objPricingMasterHdModel = null; 
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("strPosCode",strPosCode);
		hmParameters.put("strItemCode",strItemCode);
		hmParameters.put("strAreaCode",strAreaCode);
		hmParameters.put("strHourlyPricing",strHourlyPricing);
		hmParameters.put("clientCode",clientCode);
		objPricingMasterHdModel = (clsPricingMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemPricing", hmParameters);
		return objPricingMasterHdModel;
	}
	
	public clsPricingMasterHdModel funLoadDataToUpdateItemPrice(String pricingId,String clientCode)throws Exception
	{
		clsPricingMasterHdModel objPricingMasterModel = null;
		long strPricingId=Long.parseLong(pricingId);
		objPricingMasterModel = (clsPricingMasterHdModel) obBaseService.funGetMenuItemPricingMaster("getMenuItemPricing", strPricingId, clientCode);
		return objPricingMasterModel;
	}
	
	public void funSaveUpdateWaiterMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public clsWaiterMasterModel funGetSelectedWaiterMasterData(String waiterNo,String clientCode)throws Exception
	{
		clsWaiterMasterModel objWaiterMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("waiterNo",waiterNo);
		hmParameters.put("clientCode",clientCode);
		objWaiterMasterModel = (clsWaiterMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getWaiterMaster", hmParameters);
		return objWaiterMasterModel;
	}
	
	public void funSaveUpdateItemWiseIncentiveMaster(clsBaseModel objBaseModel)throws Exception
	{
		obBaseService.funSave(objBaseModel);
	}
	
	public List<clsAreaMasterModel> funLoadClientWiseArea(String strClientCode)throws Exception
	{
		List<clsAreaMasterModel> list=null;
		list=obBaseService.funLoadAll(new clsAreaMasterModel(),strClientCode);
		return list;
		
	}
	
	public List funGetAllGroupNamesForBillSeries(String clientCode,boolean addFilter,String filter)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append(" select a.strGroupName,a.strGroupCode from tblgrouphd a ");
        if (addFilter)
        {
        	sqlBuilder.append(" where a.strGroupCode NOT IN ");
        	sqlBuilder.append(filter);
        }
        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public List funGetAllSubGroupNamesForBillSeries(String clientCode,boolean addFilter,String filter)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select a.strSubGroupName,a.strSubGroupCode from tblsubgrouphd a ");
        if (addFilter)
        {
        	sqlBuilder.append(" where a.strSubGroupCode NOT IN ");
        	sqlBuilder.append(filter);
        }

        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	public List funGetAllMenuHeadNamesForBillSeries(String clientCode,boolean addFilter,String filter)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select a.strMenuName,a.strMenuCode from tblmenuhd a ");
        if (addFilter)
        {
        	sqlBuilder.append(" where a.strMenuCode NOT IN ");
        	sqlBuilder.append(filter);
        }

        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public List funGetAllRevenueHeadNamesForBillSeries(String clientCode,boolean addFilter,String filter)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select a.strRevenueHead,a.strRevenueHead  from tblitemmaster a ");
        if (addFilter)
        {
        	sqlBuilder.append(" where a.strRevenueHead NOT IN ");
        	sqlBuilder.append(filter);
        }
        sqlBuilder.append(" group by a.strRevenueHead;");
        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public List <clsSettlementMasterModel> funLoadSettlementData(String strClientCode)throws Exception
	{
		List <clsSettlementMasterModel> list=null;
		list=obBaseService.funLoadAll(new clsSettlementMasterModel(),strClientCode);
		return list;
	}
	
	 
	public clsDiscountMasterModel funSelectedDiscountMasterData(String discCode,String clientCode)throws Exception
	{
		clsDiscountMasterModel objDiscMasterModel = null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("discCode",discCode);
		hmParameters.put("clientCode",clientCode);
		objDiscMasterModel = (clsDiscountMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getDiscountMaster", hmParameters);				
		System.out.println();
	    return objDiscMasterModel;	 	
   }

	public void funSaveUpdateDiscountMaster(clsBaseModel objBaseModel)throws Exception
	{
		 obBaseService.funSave(objBaseModel); 
	} 	
	
	public clsMenuItemMasterModel funSelectedDiscountDiscountOnItemData(String code,String clientCode)throws Exception
	{
		clsMenuItemMasterModel objMenuItemMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("itemCode",code);
		hmParameters.put("clientCode",clientCode);
		objMenuItemMasterModel = (clsMenuItemMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getMenuItemMaster", hmParameters);
		return objMenuItemMasterModel;
		
	}
	public clsGroupMasterModel funSelectedDiscountDiscountOnGroupData(String code,String clientCode)throws Exception
	{
		clsGroupMasterModel objGroupMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("groupCode",code);
		hmParameters.put("clientCode",clientCode);
		objGroupMasterModel = (clsGroupMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getGroupMaster", hmParameters);
		return objGroupMasterModel;
	}
	
	
	public clsSubGroupMasterHdModel funSelectedDiscountDiscountOnSubGroupData(String code,String clientCode)throws Exception
	{
		clsSubGroupMasterHdModel objSubGroupMasterModel=null;
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("subGroupCode",code);
		hmParameters.put("clientCode",clientCode);
		objSubGroupMasterModel = (clsSubGroupMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getSubGroupMaster", hmParameters);
		return objSubGroupMasterModel;
	}
	
	
	public clsSetupHdModel funGetPOSWisePropertySetup(String POSCode,String clientCode) throws Exception
	{
		clsSetupHdModel objSetupHdModel= new clsSetupHdModel();
		List list = obBaseService.funLoadAllPOSWise(objSetupHdModel,clientCode,POSCode);
		if(list.size()>0)
		{
			objSetupHdModel = (clsSetupHdModel) list.get(0);
		}
		return objSetupHdModel;
	}
	
	public List funFillCityCombo(String strClientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select a.strCityName,a.strState,a.strCountry  from tblsetup a where a.strClientCode="+strClientCode);  
        list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public List funFillPOSCombo(String strClientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y'");
		list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public List funFullPOSCombo(String strClientCode)throws Exception
	{
		List list = null;
		StringBuilder sqlBuilder = new StringBuilder("select strPOSCode,strPOSName from tblposmaster where strOperationalYN='Y' and strClientCode="+strClientCode);
		list=obBaseService.funGetList(sqlBuilder, "sql");
		return list;
	}
	
	public void funSaveUpdatePOSWiseItemIncentive(clsBaseModel objBaseModel)throws Exception
	{
		 obBaseService.funSave(objBaseModel); 
	} 

	public List funGetListItemWiseIncentive(String clientCode,String posCode)throws Exception 
	{
		List listRet = new ArrayList();
		boolean flgPreviousRecordFound=false;
		List list = new ArrayList();
		StringBuilder hqlQuery = new StringBuilder();
	 	hqlQuery.setLength(0);
	 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPOSCode,ifnull(b.strPosName,''),a.strIncentiveType,a.dblIncentiveValue,e.strGroupName,d.strSubGroupName "
             + " FROM tblposwiseitemwiseincentives a  left outer join tblposmaster b on (a.strPosCode=b.strPosCode or a.strPosCode='All') "
             + " JOIN tblitemmaster c ON a.strItemCode=c.strItemCode "  
             + " JOIN tblsubgrouphd d ON c.strSubGroupCode=d.strSubGroupCode "  
             + " JOIN tblgrouphd e ON d.strGroupCode=e.strGroupCode"); 
	 		
	 		if(!posCode.equalsIgnoreCase("All"))
         {
	 			
	 			hqlQuery.append(" where a.strPOSCode='").append(posCode).append("' "); 
         }
	 		hqlQuery.append(" ORDER BY a.strItemCode,e.strGroupName,d.strSubGroupName,b.strPosName");
         
	 		list=obBaseService.funGetList(hqlQuery, "sql");
 		if(list.size()>0)
 		{
 			 flgPreviousRecordFound=true;
 		    for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] obj = (Object[]) list.get(cnt);
				clsPOSWiseItemIncentiveDtlBean objBean = new clsPOSWiseItemIncentiveDtlBean();
				objBean.setStrItemCode(obj[0].toString());
				objBean.setStrItemName(obj[1].toString());
				objBean.setStrPOSCode(obj[3].toString());
				objBean.setStrIncentiveType(obj[4].toString());
				objBean.setStrIncentiveValue(obj[5].toString());
				objBean.setStrGroupName(obj[6].toString());
				objBean.setStrSubGroupName(obj[7].toString());
				listRet.add(objBean);
			}
 		}
 		
 		hqlQuery.setLength(0);
	 	hqlQuery.append("SELECT distinct(a.strItemCode),a.strItemName,a.strPosCode,ifnull(b.strPosName,'') "
	 			    + ",e.strGroupName,d.strSubGroupName "
	 			    + " FROM tblmenuitempricingdtl a  "
	 			    + " left outer join tblposmaster b on (a.strPosCode=b.strPosCode or a.strPosCode='All') "
	 			    + "join tblitemmaster c on a.strItemCode=c.strItemCode "
	 			    + "join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
	 			    + "join tblgrouphd e on d.strGroupCode=e.strGroupCode "
	 			    + " where a.strItemCode NOT IN(SELECT c.strItemCode FROM tblposwiseitemwiseincentives c   ) ");	
	 		if(!posCode.equalsIgnoreCase("All"))
         {
	 			
	 			hqlQuery.append(" and (a.strPOSCode='").append(posCode).append("' or a.strPosCode='All' )  "); 
         }
	 		hqlQuery.append("order by a.strItemCode,e.strGroupName,d.strSubGroupName,b.strPosName");
         
	 		list=obBaseService.funGetList(hqlQuery, "sql");
 		if(list.size()>0)
 		{
 			 flgPreviousRecordFound=true;
 		    for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] obj = (Object[]) list.get(cnt);
				clsPOSWiseItemIncentiveDtlBean objBean = new clsPOSWiseItemIncentiveDtlBean();
				objBean.setStrItemCode(obj[0].toString());
				objBean.setStrItemName(obj[1].toString());
				objBean.setStrPOSCode(obj[3].toString());
				objBean.setStrIncentiveType("Per");
				objBean.setStrIncentiveValue("0.0");
				objBean.setStrGroupName(obj[4].toString());
				objBean.setStrSubGroupName(obj[5].toString());
				listRet.add(objBean);
			}
 		}
 		
 		 if(!flgPreviousRecordFound)
          {
 			 hqlQuery.setLength(0);
 			 	hqlQuery.append("SELECT distinct(a.strItemCode),a.strItemName,a.strPosCode,ifnull(b.strPosName,''),e.strGroupName,d.strSubGroupName \n"  
 					    + " FROM tblmenuitempricingdtl a \n" 
 					    + " left outer join tblposmaster b on a.strPosCode=b.strPosCode \n"  
 					    + " join tblitemmaster c on a.strItemCode=c.strItemCode \n" 
 					    + " join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode \n"  
 					    + " join tblgrouphd e on d.strGroupCode=e.strGroupCode "); 
 			 		
 			 		if(!posCode.equalsIgnoreCase("All"))
 		            {
 			 			
 			 			hqlQuery.append(" Where a.strPOSCode='").append(posCode).append("' "); 
 		            }
 			 		hqlQuery.append("order by a.strItemCode,e.strGroupName,d.strSubGroupName,b.strPosName  ");
 		            
 					
 			 		list=obBaseService.funGetList(hqlQuery, "sql");
 			 		
 			 		if(list.size()>0)
 		    		{
 			 			
	 					for(int cnt=0;cnt<list.size();cnt++)
	 					{
	 						Object[] obj = (Object[]) list.get(cnt);
	 					    clsPOSWiseItemIncentiveDtlBean objBean = new clsPOSWiseItemIncentiveDtlBean();
	 					   objBean.setStrItemCode(obj[0].toString());
	 						objBean.setStrItemName(obj[1].toString());
	 						objBean.setStrPOSCode(obj[3].toString());
	 						objBean.setStrIncentiveType("amt");
	 						objBean.setStrIncentiveValue("0.0");
	 						objBean.setStrGroupName(obj[6].toString());
	 						objBean.setStrSubGroupName(obj[7].toString());
	 						listRet.add(objBean);
	 					}
 		    		}
 		  }
		return listRet;
	}

	public List funGetAllReasonMaster(String clientCode)throws Exception 
	{
		List list=null;
		list = obBaseService.funLoadAll(new clsReasonMasterModel(),clientCode);
		return list;
	}
	
	public List<clsGroupMasterModel> funLoadAllGroupDetails(String strClientCode)throws Exception 
	{
		List<clsGroupMasterModel> list=null;
		list=obBaseService.funLoadAll(new clsGroupMasterModel(),strClientCode);
		return list;
	}
	 
	public List<clsUserHdModel> funFillUserCombo(String strClientCode)throws Exception 
	{
		List<clsUserHdModel> list=null;
		list=obBaseService.funLoadAll(new clsUserHdModel(),strClientCode);
		return list;
		
	}
	
	public List<clsSettlementMasterModel> funFillSettlementCombo(String strClientCode)throws Exception 
	{
		List<clsSettlementMasterModel> list=null;
		list=obBaseService.funLoadAll(new clsSettlementMasterModel(),strClientCode);
		return list;
		
	}
	
	public List<clsWaiterMasterModel> funGetAllWaitersForMaster(String clientCode)throws Exception 
	{
		List<clsWaiterMasterModel> list = null;
		list = obBaseService.funLoadAll(new clsWaiterMasterModel(), clientCode);	
		return list;
	}
	
	public List funFillAllGroupList(String strClientCode)throws Exception 
	{
		List list=null;
		list=obBaseService.funLoadAll(new clsGroupMasterModel(),strClientCode);
		return list;
	}
	
	
	public void funSaveSubGroupMaster(clsBaseModel objBaseModel)throws Exception 
	{
		 obBaseService.funSave(objBaseModel); 
	}

   public clsSubGroupMasterHdModel funLoadSubGroupData(Map hmParameters)throws Exception
   {
		 clsSubGroupMasterHdModel objSubGroupMasterHdModel=null;
		 objSubGroupMasterHdModel = (clsSubGroupMasterHdModel)obBaseService.funGetAllMasterDataByDocCodeWise("getSubGroupMaster", hmParameters);
		 return objSubGroupMasterHdModel;
   }

	 public void funSaveShiftMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel); 
	 }

	 public clsShiftMasterModel funLoadShiftMaster(Map hmParameters)throws Exception
	 {
		 clsShiftMasterModel objShiftMasterModel=null;
		 objShiftMasterModel = (clsShiftMasterModel)obBaseService.funGetAllMasterDataByDocCodeWise("getShift", hmParameters);
		 return objShiftMasterModel;
	 }

	 public List<clsWaiterMasterModel> funGetWaiterList(String clientCode)throws Exception
	  {
		 List<clsWaiterMasterModel> listModel =null;
		 listModel=obBaseService.funLoadAll(new clsWaiterMasterModel(),clientCode);
		 return listModel;
	  }
	 
	 public void funSaveTableMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel);  	
	 }

	 public clsTableMasterModel funSelectedTableMasterData(String tableNo,String clientCode)throws Exception
	  {
		clsTableMasterModel objTableMasterModel=null; 
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("tableNo",tableNo);
		hmParameters.put("clientCode",clientCode);
		objTableMasterModel = (clsTableMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getTableMaster", hmParameters);
		return objTableMasterModel;
	 }
	 
	 public clsTaxMasterModel funSelectedTaxMasterData(String taxCode,String clientCode)throws Exception
	 {
		clsTaxMasterModel objTaxMasterModel=null; 
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("taxCode",taxCode);
		hmParameters.put("clientCode",clientCode);
		objTaxMasterModel = (clsTaxMasterModel) obBaseService.funGetAllMasterDataByDocCodeWise("getTaxMaster", hmParameters);
	    return objTaxMasterModel;
	}
	 
	 public List funGetAllPOSData(String clientCode)throws Exception
		{
			List listPOSMaster = null;
			listPOSMaster = obBaseService.funGetSerachList("getAllPOSMaster", clientCode);
		    return listPOSMaster; 	
	   }
	 
	 public List<clsTaxMasterModel> funGetAllTaxForMaster(String clientCode)throws Exception
		{
			List<clsTaxMasterModel> list=null;
			list=obBaseService.funLoadAll(new clsTaxMasterModel(),clientCode);
			return list;
		}
	 
	 public void funSaveTaxMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel); 	
	 }
	 
	 public Map funGetFormListByModuleType(String moduleType)throws Exception
	 {
		List listData = new ArrayList<>(); 
		Map hmRet = new TreeMap<>();
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select a.strFormName ,a.strModuleName from tblforms  a where a.strModuleType='"+moduleType+"'");
		List rsReports = obBaseService.funGetList(sqlBuilder, "sql");	
		if(rsReports!=null)
		{
		    for(int i=0;i<rsReports.size();i++)
		    {
		    	Map hmData = new TreeMap();
		        Boolean flag = new Boolean(false);
		        Object[] obj = (Object[]) rsReports.get(i);
		        String moduleName =obj[0].toString();
		        hmData.put("moduleName", moduleName);
		        hmData.put("flag", flag);
		        listData.add(hmData);
		    }
		}
		hmRet.put("jArr", listData);	
		return hmRet;
	  }
	 
	 public List<clsTableMasterModel> funGetTableList(String clientCode)throws Exception
	 {
	    List<clsTableMasterModel> listModel =null;
	    listModel=obBaseService.funLoadAll(new clsTableMasterModel(),clientCode);
	    return listModel;
	 }
	 
	 public List<clsModifierGroupMasterHdModel> funLoadAllModifierGroup(String strClientCode) throws Exception
		{
						
			List<clsModifierGroupMasterHdModel> list =null;
			list =obBaseService.funLoadAll(new clsModifierGroupMasterHdModel(),strClientCode);
			return list;
			
		}
	 
	 public List funLoadItemPricingMasterData(String menuCode,String modifierCode,String clientCode)throws Exception
	 {
		 List list = null;
		 StringBuilder sqlBuilder = new StringBuilder();
		 sqlBuilder.append("SELECT m.strItemName,m.strItemCode,ifnull(n.strModifierCode,''),ifnull(n.strDefaultModifier,''),if(n.strItemCode is Null,'N','Y')\n" 
		       + " FROM tblmenuitempricingdtl m \n" 
			   + " left outer join tblitemmodofier n on m.strItemCode=n.strItemCode and  n.strModifierCode='"+modifierCode+"' \n" 
		 	   + " WHERE m.strMenuCode='"+menuCode+"' group by m.strItemCode");
	
		 
		 list=obBaseService.funGetList(sqlBuilder, "sql");
		 return list;
	 }
	 
	 public void funSaveUpdateItemModifierMaster(clsBaseModel objBaseModel)throws Exception
	 {
		 obBaseService.funSave(objBaseModel); 
	 } 
	 
	 public clsModifierMasterHdModel funGetItemModifierMasterData(Map hmParameters)throws Exception
		{
		 clsModifierMasterHdModel objModifierMasterModel = null;
			
		 objModifierMasterModel = (clsModifierMasterHdModel) obBaseService.funGetAllMasterDataByDocCodeWise("getModifierMaster", hmParameters);			
			System.out.println();
		    return objModifierMasterModel; 	
		 }
	 
	 public List <clsGroupMasterModel> funLoadGrouptData(String strClientCode)throws Exception
	 {
			List <clsGroupMasterModel> list=null;
			list=obBaseService.funLoadAll(new clsGroupMasterModel(),strClientCode);
			return list;
	 }
	 
	 public List funFillAllItemList(String strClientCode)throws Exception 
		{
			List list=null;
			list=obBaseService.funLoadAll(new clsMenuItemMasterModel(),strClientCode);
			return list;
		}
	 
	 
	 public List<clsShiftMasterModel> funGetAllShiftForMaster(String clientCode)throws Exception
		{
			List<clsShiftMasterModel> list = null;
			list = obBaseService.funLoadAll(new clsShiftMasterModel(), clientCode);	
			return list;
		}
	 
	 public List<clsCustomerMasterModel> funLoadAllCustomerModel(String strClientCode)throws Exception
		{
			List<clsCustomerMasterModel> list=null;
			list=obBaseService.funLoadAll(new clsCustomerMasterModel(),strClientCode);
			return list;
			
		}
	 
	 public List funFillAllRevenuHeadCombo()throws Exception
		{
			List list = null;
			StringBuilder sqlBuilder = new StringBuilder("select distinct(strRevenueHead) from tblitemmaster order by strRevenueHead; ");
			list=obBaseService.funGetList(sqlBuilder, "sql");
			return list;
		}
	 
	 public List funGetBilledTableName(String strPosCode,String strClientCode) throws Exception 
	 {
		 List list = null;
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append("select a.strTableNo ,b.strTableName "
		    + "from tblbillhd a,tbltablemaster b "
		    + "where a.strTableNo=b.strTableNo "
		    + "and a.strPOSCode='" + strPosCode + "' and a.strClientCode='" +strClientCode+"' "
		    + "and a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) "
		    + "group by b.strTableNo "
		    + "order by b.strTableNo ");
		 list=obBaseService.funGetList(sb, "sql");
		return list;
		 
	 }
}

