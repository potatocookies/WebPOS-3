package com.sanguine.base.dao;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.model.clsBaseModel;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.webpos.bean.clsPOSBillItemDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementDtl;
import com.sanguine.webpos.bean.clsPOSCommonBeanDtl;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSOperatorDtl;
import com.sanguine.webpos.bean.clsPOSSalesFlashReportsBean;
import com.sanguine.webpos.controller.clsPOSGlobalFunctionsController;
import com.sanguine.webpos.model.clsPricingMasterHdModel;
@Repository("intfBaseDao")
@Transactional(value="webPOSTransactionManager")
public class clsBaseDaoImpl implements intfBaseDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	private clsSetupService objSetupService;
	
	Map<String, List<Map<String, clsPOSBillSettlementDtl>>> mapPOSDtlForSettlement;
	private Map<String, Map<String, clsPOSBillItemDtl>> mapPOSItemDtl;
	private Map<String, Map<String, clsPOSBillItemDtl>> mapPOSMenuHeadDtl;
	private Map<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> mapPOSDtlForGroupSubGroup;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSWaiterWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSDeliveryBoyWise;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSCostCenterWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSTableWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSHourlyWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSAreaWiseSales;
	private Map<String, clsPOSCommonBeanDtl> mapPOSDayWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSModifierWiseSales;
	private Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSMonthWiseSales;
	private Map<String, List<clsPOSOperatorDtl>> mapOperatorDtls;
	
	double TotSale = 0;
	
	@Override
	public String funSave(clsBaseModel objBaseModel) {
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objBaseModel);
		return objBaseModel.getDocCode();
	}

	@Override
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key)
	{
		return (clsBaseModel) webPOSSessionFactory.getCurrentSession().load(objBaseModel.getClass(), key);		
	}
	@Override
	public clsBaseModel funGet(clsBaseModel objBaseModel,Serializable key)
	{
		return (clsBaseModel) webPOSSessionFactory.getCurrentSession().get(objBaseModel.getClass(), key);		
	}
	
	@Override
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode)
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq("strClientCode", clientCode));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	
	@Override
	public List funGetSerachList(String sql,String clientCode) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("clientCode", clientCode);
		
		return query.list(); 
	}
	
	
	public List funGetList(StringBuilder strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equals("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery.toString());
			return query.list();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery.toString());
			return query.list();
		}
	}
	
	public int funExecuteUpdate(String strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equalsIgnoreCase("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery);
			return query.executeUpdate();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery);
			return query.executeUpdate();
		}
	}
	
	@Override
	public List funLoadAllPOSWise(clsBaseModel objBaseModel,String clientCode,String strPOSCode)throws Exception
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq("strClientCode", clientCode));
		cr.add(Restrictions.eq("strPOSCode", strPOSCode));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	@Override
	public List funLoadAllCriteriaWise(clsBaseModel objBaseModel,String criteriaName,String criteriaValue)
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq(criteriaName, criteriaValue));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	@Override
	public clsBaseModel funGetAllMasterDataByDocCodeWise(String sql,Map<String,String> hmParameters) 
   	{
   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
   		{
   			query.setParameter(entrySet.getKey(), entrySet.getValue());
   		}
   		List list=query.list();
   		
   		clsBaseModel model=new clsBaseModel();
   		if(list.size()>0)
   		{
   			model=(clsBaseModel)list.get(0);
   			
   		}
   		return model; 
   	} 
	
	@Override
	public clsBaseModel funGetMenuItemPricingMaster(String sql,long id,String clientCode)
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("longPricingId",id);
		query.setParameter("clientCode",clientCode);
		List list=query.list();
		
		clsPricingMasterHdModel model=new clsPricingMasterHdModel();
		if(list.size()>0)
		{
			model=(clsPricingMasterHdModel)list.get(0);
			
		}
		return model; 
		
	}
	
	public String funGetValue(StringBuilder strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equals("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery.toString());
			return query.toString();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery.toString());
			return query.toString();
		}
	}
	
	
	public JSONObject funSalesReport(String fromDate, String toDate,
			String strPOSCode, String strShiftNo, String strUserCode,
			String field, String strPayMode, String strOperator,
			String strFromBill, String strToBill, String reportType,
			String Type, String Customer, String ConsolidatePOS,
			String ReportName, String LoginPOSCode,String areaCode,String operationTye) 
	{

		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sbSqlDisFilters = new StringBuilder();
		DecimalFormat decimalFormat;
		decimalFormat = new DecimalFormat("#.##");
		String AreaWisePricing = "Y";
		/*
		 * try{
		 * 
		 * JSONObject JSONAreaWisePricing=new
		 * clsSetupDao().funGetParameterValuePOSWise(strUserCode, LoginPOSCode,
		 * "gAreaWisePricing");
		 * AreaWisePricing=JSONAreaWisePricing.get("gAreaWisePricing"
		 * ).toString(); } catch(Exception e){ e.printStackTrace(); }
		 */
		double totalDiscAmt = 0, totalSubTotalDWise = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

		JSONArray jArr = new JSONArray();
		JSONObject jOBjRet = new JSONObject();
		List<clsPOSSalesFlashReportsBean> arrListSalesReport;
		BigDecimal totalAmount, temp, temp1, Disc;
		int rowCount = 0;

		double totalSale = 0;

		double totalQty = 0;
		double subTotal = 0.00;
		double discountTotal = 0.00;
		StringBuilder sbSqlLiveBill = new StringBuilder();
		StringBuilder sbSqlQFileBill = new StringBuilder();
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbFilters = new StringBuilder();
		Map map = new HashMap();
		List jColHeaderArr = new ArrayList();
		int colCount = 0;
		try {
			switch (ReportName) {

			case "SettlementWise":
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Settlement Mode");
				jColHeaderArr.add("Sales Amount");
				jColHeaderArr.add("Sales %");
				colCount = 4;
				// Double totalQty;
				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");

				StringBuilder sbLive = new StringBuilder();
				StringBuilder sbQFile = new StringBuilder();

				sbLive.setLength(0);
				sbQFile.setLength(0);
				sbFilters.setLength(0);
				field="date(c.dteBillDate)";

				sbLive.append("SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
						+ " , IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'"
						+ strUserCode
						+ "'"
						+ " ,b.strSettelmentType "
						+ " from "
						+ " tblbillsettlementdtl a "
						+ " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
						+ " LEFT OUTER JOIN tblbillhd c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
						+ " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode "
						+ " WHERE "
						+ field
						+ " BETWEEN '"
						+ fromDate
						+ "' AND '"
						+ toDate
						+ "' "
						+ "AND a.dblSettlementAmt>0 ");

				sbQFile.append("SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
						+ " ,IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'"
						+ strUserCode
						+ "' "
						+ " ,b.strSettelmentType "
						+ " from "
						+ " tblqbillsettlementdtl a "
						+ " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
						+ " LEFT OUTER JOIN tblqbillhd c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
						+ " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode "
						+ " WHERE "
						+ field
						+ " BETWEEN '"
						+ fromDate
						+ "' AND '"
						+ toDate
						+ "' "
						+ " AND a.dblSettlementAmt>0 ");

				if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
					sbFilters.append("  AND d.strPOSCode = '" + strPOSCode
							+ "' and c.strUserCreated='" + strOperator + "' ");
				} else if (!strPOSCode.equals("All")
						&& strOperator.equals("All")) {
					sbFilters
							.append(" AND d.strPOSCode = '" + strPOSCode + "'");
				} else if (strPOSCode.equals("All")
						&& !strOperator.equals("All")) {
					sbFilters.append("  and c.strUserCreated='" + strOperator
							+ "'");
				}

				sbFilters.append(" AND c.intShiftCode = '" + strShiftNo + "' ");

				if (strFromBill.length() == 0 && strToBill.length() == 0) {
				} else {
					sbFilters.append(" and a.strBillNo between '" + strFromBill
							+ "' and '" + strToBill + "'");
				}
				if (!strPayMode.equalsIgnoreCase("All")) {
					sbFilters.append(" and b.strSettelmentDesc='" + strPayMode
							+ "' ");
				}

				if (ConsolidatePOS.equalsIgnoreCase("Y")) {
					sbFilters.append(" GROUP BY b.strSettelmentDesc ");
				} else {
					sbFilters
							.append(" GROUP BY b.strSettelmentDesc, d.strPosCode");
				}
				sbLive.append(" ").append(sbFilters);
				sbQFile.append(" ").append(sbFilters);

				mapPOSDtlForSettlement = new LinkedHashMap<String, List<Map<String, clsPOSBillSettlementDtl>>>();

				Query queryLiveSettlementSales = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(sbLive.toString());
				List listSettlementWiseSales = queryLiveSettlementSales.list();
				funGenerateSettlementWiseSales(listSettlementWiseSales);

				Query queryQSettlementSales = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(sbQFile.toString());
				List listSettlementWiseSalesQ = queryQSettlementSales.list();
				funGenerateSettlementWiseSales(listSettlementWiseSalesQ);

				Iterator<Map.Entry<String, List<Map<String, clsPOSBillSettlementDtl>>>> it = mapPOSDtlForSettlement
						.entrySet().iterator();
				List<clsPOSBillSettlementDtl> lstTemp = new ArrayList<clsPOSBillSettlementDtl>();

				List<clsPOSSalesFlashReportsBean> arrTempListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
				List listStockFlashModel = new ArrayList();

				while (it.hasNext()) {
					Map.Entry<String, List<Map<String, clsPOSBillSettlementDtl>>> entry = it
							.next();
					List<Map<String, clsPOSBillSettlementDtl>> listOfSettelment = entry
							.getValue();

					for (int i = 0; i < listOfSettelment.size(); i++) {

						clsPOSBillSettlementDtl objSettlementDtl = listOfSettelment
								.get(i).entrySet().iterator().next().getValue();
						lstTemp.add(objSettlementDtl);
						totalSale += objSettlementDtl.getDblSettlementAmt();
						clsPOSSalesFlashReportsBean obSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						obSalesFlashColumns.setStrField1(objSettlementDtl
								.getPosName());
						obSalesFlashColumns.setStrField2(objSettlementDtl
								.getStrSettlementName());
						obSalesFlashColumns
								.setStrField3(String.valueOf(objSettlementDtl
										.getDblSettlementAmt()));

						arrTempListSalesReport.add(obSalesFlashColumns);
						List DataList = new ArrayList<>();
						DataList.add(objSettlementDtl.getPosName());
						DataList.add(objSettlementDtl.getStrSettlementName());
						DataList.add(objSettlementDtl.getDblSettlementAmt());
						map.put(rowCount, DataList);
						rowCount++;
					}

				}
				try {
					BigDecimal bigtotalSale = new BigDecimal(totalSale);
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListSalesReport = gson.toJson(
							arrTempListSalesReport, type);
					jOBjRet.put("ListSettlementWiseSales",
							gsonarrTempListSalesReport);
					jOBjRet.put("TotalSale", bigtotalSale);
					jOBjRet.put("ColHeader", jColHeaderArr);
					jOBjRet.put("colCount", colCount);
					jOBjRet.put("RowCount", rowCount);
					// jOBjRet.put("listStockFlashModel", listStockFlashModel);

					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List list = (List) map.get(tblRow);
						list.add((Double.parseDouble(list.get(2).toString()) / Double
								.parseDouble(bigtotalSale.toString())) * 100);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						jOBjRet.put("" + tblRow, list);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "BillWise":
				List listRet = new ArrayList();
				jColHeaderArr.add("Bill No");
				jColHeaderArr.add("Date");
				jColHeaderArr.add("Bill time");
				jColHeaderArr.add("Table Name");
				jColHeaderArr.add("Cust Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Pay Mode");
				jColHeaderArr.add("Delivery Charge");
				jColHeaderArr.add("Sub Total");
				jColHeaderArr.add("Disc %");
				jColHeaderArr.add("Disc Amt");
				jColHeaderArr.add("TAX Amt");
				jColHeaderArr.add("Sales Amt");
				jColHeaderArr.add("Remark");
				jColHeaderArr.add("Tip");
				jColHeaderArr.add("Disc Remark");
				jColHeaderArr.add("Reason");
				colCount = 17;

				StringBuilder sbSqlBillWise = new StringBuilder();
				StringBuilder sbSqlBillWiseQFile = new StringBuilder();
				field="date(a.dteBillDate)";	
				
				 sbSqlBillWise.setLength(0);
		         sbSqlBillWise.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
		                    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
		                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt "
		                    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated "
		                    + " ,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo "
		                    + " ,a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA') "
		                    + " ,a.dblTipAmount,'" + strUserCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA')"
		                    + ",a.intShiftCode,a.dblRoundOff,a.intBillSeriesPaxNo,ifnull(i.dblAdvDeposite,0),ifnull(k.strAdvOrderTypeName,'') "
		                    + " from tblbillhd  a "
		                    + " left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
		                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
		                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  and date(a.dteBillDate)=date(c.dteBillDate)  "
		                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
		                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
		                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
		                    + " left outer join tbladvancereceipthd i on a.strAdvBookingNo=i.strAdvBookingNo "
		                    + " LEFT OUTER JOIN tbladvbookbillhd j ON a.strAdvBookingNo=j.strAdvBookingNo "
		                    + " left outer join tbladvanceordertypemaster k on j.strOrderType=k.strAdvOrderTypeCode "
		                    + " where " + field + " between '" + fromDate + "' and '" + toDate + "' "
		                    + "  ");

		            if (!strPOSCode.equals("All")) 
		            {
						sbSqlBillWise.append(" and a.strPOSCode='" + strPOSCode + "' ");
					}
		           // sbSqlBillWise.append(" AND a.intShiftCode = '" + strShiftNo+ "' ");

					if (!strOperator.equals("All")) 
					{
						sbSqlBillWise.append(" and  a.strUserCreated='"+ strOperator + "' ");
					}
					if (!strPayMode.equals("All")) 
					{
						sbSqlBillWise.append(" and d.strSettelmentCode='"+ strPayMode + "' ");
					}	
						
					if (strFromBill.trim().length() > 0 && strToBill.trim().length() > 0) { 
						sbSqlBillWise.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
					}
		            if (!areaCode.equals("All"))
		            {
		                sbSqlBillWise.append(" and a.strAreaCode='" + areaCode + "' ");
		            }
		            if (!operationTye.equalsIgnoreCase("All"))
		            {
		                sbSqlBillWise.append(" and a.strOperationType='" + operationTye+ "' ");
		            }
		            if (!Customer.equalsIgnoreCase(""))
		            {
		                sbSqlBillWise.append(" and a.strCustomerCode='" + Customer + "' ");

		            }
		            sbSqlBillWise.append(" order by date(a.dteBillDate),a.strBillNo desc ");
		            
		            
		            
		            sbSqlBillWiseQFile.setLength(0);
		            sbSqlBillWiseQFile.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
		                    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName"
		                    + ""
		                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
		                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
		                    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
		                    + " ,a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
		                    + " ,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
		                    + " ,a.dblTipAmount,'" + strUserCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA')"
		                    + ",a.intShiftCode,a.dblRoundOff,a.intBillSeriesPaxNo,ifnull(i.dblAdvDeposite,0),ifnull(k.strAdvOrderTypeName,'') "
		                    + " from tblqbillhd a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
		                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
		                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode   and date(a.dteBillDate)=date(c.dteBillDate)  "
		                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
		                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
		                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
		                    + " left outer join tblqadvancereceipthd i on a.strAdvBookingNo=i.strAdvBookingNo "
		                    + " LEFT OUTER JOIN tblqadvbookbillhd j ON a.strAdvBookingNo=j.strAdvBookingNo "
		                    + " left outer join tbladvanceordertypemaster k on j.strOrderType=k.strAdvOrderTypeCode "
		                    + " where " + field + " between '" + fromDate + "' and '" + toDate + "' "
		                    + "  ");

		            if (!strPOSCode.equals("All")) {
						sbSqlBillWiseQFile.append(" and a.strPOSCode='" + strPOSCode + "' ");
					}

				//	sbSqlBillWiseQFile.append(" AND a.intShiftCode = '" + strShiftNo + "' ");

					if (!strOperator.equals("All")) {
						sbSqlBillWiseQFile.append(" and  a.strUserCreated='" + strOperator + "' ");
					}
					if (!strPayMode.equals("All")) {
						sbSqlBillWiseQFile.append(" and d.strSettelmentCode='" + strPayMode + "' ");
					}
					if (strFromBill.trim().length() > 0 && strToBill.trim().length() > 0) {
						sbSqlBillWiseQFile.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
					}
					
				    if (!areaCode.equals("All"))
		            {
				    	sbSqlBillWiseQFile.append(" and a.strAreaCode='" + areaCode + "' ");
		            }
		            if (!operationTye.equalsIgnoreCase("All"))
		            {
		            	sbSqlBillWiseQFile.append(" and a.strOperationType='" + operationTye+ "' ");
		            }
		            if (!Customer.equalsIgnoreCase(""))
		            {
		            	sbSqlBillWiseQFile.append(" and a.strCustomerCode='" + Customer + "' ");
		            }
					
		            sbSqlBillWiseQFile.append(" order by date(a.dteBillDate),a.strBillNo desc ");
			
			
				boolean flgRecords = false;

				Map<String, List<clsPOSSalesFlashReportsBean>> hmBillWiseSales = new HashMap<String, List<clsPOSSalesFlashReportsBean>>();
				int seqNo = 1;

				// for live Data
				Query queryBillWiseSales = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(
								sbSqlBillWise.toString());
				List listBillWiseSales = queryBillWiseSales.list();
				if (listBillWiseSales.size() > 0) {
					for (int i = 0; i < listBillWiseSales.size(); i++) {
						Object[] obj = (Object[]) listBillWiseSales.get(i);
						List<clsPOSSalesFlashReportsBean> arrListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
						flgRecords = true;
						// objOperatorWiseSales.setStrSettlementDesc(obj[1].toString());
						String[] spDate = obj[1].toString().split("-");
						String billDate = spDate[2] + "-" + spDate[1] + "-"
								+ spDate[0];// billDate

						clsPOSSalesFlashReportsBean objSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objSalesFlashColumns.setStrField1(obj[0].toString());
						objSalesFlashColumns.setStrField2(billDate);
						objSalesFlashColumns.setStrField3(obj[2].toString());
						objSalesFlashColumns.setStrField4(obj[3].toString());
						objSalesFlashColumns.setStrField5(obj[20].toString());// Cust
																				// Name

						objSalesFlashColumns.setStrField6(obj[4].toString());
						objSalesFlashColumns.setStrField7(obj[5].toString());
						objSalesFlashColumns.setStrField8(obj[18].toString());
						objSalesFlashColumns.setStrField9(obj[6].toString());
						objSalesFlashColumns.setStrField10(obj[7].toString());
						objSalesFlashColumns.setStrField11(obj[8].toString());
						objSalesFlashColumns.setStrField12(obj[9].toString());
						objSalesFlashColumns.setStrField13(obj[10].toString());
						objSalesFlashColumns.setStrField14(obj[19].toString());
						objSalesFlashColumns.setStrField15(obj[21].toString());
						objSalesFlashColumns.setStrField16(obj[23].toString());
						objSalesFlashColumns.setStrField17(obj[24].toString());
						// objSalesFlashColumns.setSeqNo(Integer.parseInt(billNo.split("-")[0]));
						objSalesFlashColumns.setSeqNo(seqNo++);

						if (null != hmBillWiseSales.get(obj[0].toString())) {
							arrListBillWiseSales = hmBillWiseSales.get(obj[0]
									.toString());
							objSalesFlashColumns.setStrField9("0");
							objSalesFlashColumns.setStrField10("0");
							objSalesFlashColumns.setStrField11("0");
							objSalesFlashColumns.setStrField12("0");
							objSalesFlashColumns.setStrField15("0");
						}
						arrListBillWiseSales.add(objSalesFlashColumns);
						hmBillWiseSales.put(obj[0].toString(),
								arrListBillWiseSales);

						totalDiscAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField11());
						totalSubTotalDWise += Double
								.parseDouble(objSalesFlashColumns
										.getStrField9());
						totalTaxAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField12());
						totalSettleAmt += Double
								.parseDouble(objSalesFlashColumns
										.getStrField13());// Grand Total
						totalTipAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField15());// tip Amt

					}
				}

				// for qfile data
				Query queryBillWiseSalesQ = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(
								sbSqlBillWiseQFile.toString());
				List listBillWiseSalesQ = queryBillWiseSalesQ.list();
				if (listBillWiseSalesQ.size() > 0) {
					for (int i = 0; i < listBillWiseSalesQ.size(); i++) {
						Object[] obj = (Object[]) listBillWiseSalesQ.get(i);
						List<clsPOSSalesFlashReportsBean> arrListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
						flgRecords = true;

						// String billNo1=rsBillWiseSales.getString(1);
						// String billNo=billNo1.substring(1, billNo1.length());
						String[] spDate = obj[1].toString().split("-");
						String billDate = spDate[2] + "-" + spDate[1] + "-"
								+ spDate[0];// billDate

						clsPOSSalesFlashReportsBean objSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objSalesFlashColumns.setStrField1(obj[0].toString());
						objSalesFlashColumns.setStrField2(billDate);
						objSalesFlashColumns.setStrField3(obj[2].toString());
						objSalesFlashColumns.setStrField4(obj[3].toString());

						objSalesFlashColumns.setStrField5(obj[20].toString());// Cust
																				// Name
						objSalesFlashColumns.setStrField6(obj[4].toString());
						objSalesFlashColumns.setStrField7(obj[6].toString());
						objSalesFlashColumns.setStrField8(obj[18].toString());
						objSalesFlashColumns.setStrField9(obj[6].toString());
						objSalesFlashColumns.setStrField10(obj[7].toString());
						objSalesFlashColumns.setStrField11(obj[8].toString());
						objSalesFlashColumns.setStrField12(obj[9].toString());
						objSalesFlashColumns.setStrField13(obj[10].toString());
						objSalesFlashColumns.setStrField14(obj[19].toString());
						objSalesFlashColumns.setStrField15(obj[21].toString());
						objSalesFlashColumns.setStrField16(obj[23].toString());
						objSalesFlashColumns.setStrField17(obj[24].toString());
						// objSalesFlashColumns.setSeqNo(Integer.parseInt(billNo.split("-")[0]));
						objSalesFlashColumns.setSeqNo(seqNo++);

						if (null != hmBillWiseSales.get(obj[0].toString())) {
							arrListBillWiseSales = hmBillWiseSales.get(obj[0]
									.toString());
							objSalesFlashColumns.setStrField9("0");
							objSalesFlashColumns.setStrField10("0");
							objSalesFlashColumns.setStrField11("0");
							objSalesFlashColumns.setStrField12("0");
							objSalesFlashColumns.setStrField15("0");
						}
						arrListBillWiseSales.add(objSalesFlashColumns);
						hmBillWiseSales.put(obj[0].toString(),
								arrListBillWiseSales);

						totalDiscAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField11());
						totalSubTotalDWise += Double
								.parseDouble(objSalesFlashColumns
										.getStrField9());
						totalTaxAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField12());
						totalSettleAmt += Double
								.parseDouble(objSalesFlashColumns
										.getStrField13());// Grand Total
						totalTipAmt += Double.parseDouble(objSalesFlashColumns
								.getStrField15());// tip Amt
					}
				}
				System.out.println("Tip Amount->" + totalTipAmt);
				// fill arrTempListBillWiseSales from hashmap of bill
				// fill arrTempListBillWiseSales from hashmap of bill

				List<clsPOSSalesFlashReportsBean> arrTempListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
				for (Map.Entry<String, List<clsPOSSalesFlashReportsBean>> entry : hmBillWiseSales
						.entrySet()) {
					for (clsPOSSalesFlashReportsBean objSalesFlashColumns : entry
							.getValue()) {
						clsPOSSalesFlashReportsBean objTempSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objTempSalesFlashColumns
								.setStrField1(objSalesFlashColumns
										.getStrField1());
						objTempSalesFlashColumns
								.setStrField2(objSalesFlashColumns
										.getStrField2());
						objTempSalesFlashColumns
								.setStrField3(objSalesFlashColumns
										.getStrField3());
						objTempSalesFlashColumns
								.setStrField4(objSalesFlashColumns
										.getStrField4());
						objTempSalesFlashColumns
								.setStrField5(objSalesFlashColumns
										.getStrField5());
						objTempSalesFlashColumns
								.setStrField6(objSalesFlashColumns
										.getStrField6());
						objTempSalesFlashColumns
								.setStrField7(objSalesFlashColumns
										.getStrField7());
						objTempSalesFlashColumns
								.setStrField8(objSalesFlashColumns
										.getStrField8());
						objTempSalesFlashColumns
								.setStrField9(objSalesFlashColumns
										.getStrField9());
						objTempSalesFlashColumns
								.setStrField10(objSalesFlashColumns
										.getStrField10());
						objTempSalesFlashColumns
								.setStrField11(objSalesFlashColumns
										.getStrField11());
						objTempSalesFlashColumns
								.setStrField12(objSalesFlashColumns
										.getStrField12());
						objTempSalesFlashColumns
								.setStrField13(objSalesFlashColumns
										.getStrField13());
						objTempSalesFlashColumns
								.setStrField14(objSalesFlashColumns
										.getStrField14());
						objTempSalesFlashColumns
								.setStrField15(objSalesFlashColumns
										.getStrField15());
						objTempSalesFlashColumns
								.setStrField16(objSalesFlashColumns
										.getStrField16());
						objTempSalesFlashColumns
								.setStrField17(objSalesFlashColumns
										.getStrField17());
						objTempSalesFlashColumns.setSeqNo(objSalesFlashColumns
								.getSeqNo());
						arrTempListBillWiseSales.add(objTempSalesFlashColumns);

						List DataList = new ArrayList<>();
						DataList.add(objSalesFlashColumns.getStrField1());
						DataList.add(objSalesFlashColumns.getStrField2());
						DataList.add(objSalesFlashColumns.getStrField3());
						DataList.add(objSalesFlashColumns.getStrField4());
						DataList.add(objSalesFlashColumns.getStrField5());
						DataList.add(objSalesFlashColumns.getStrField6());
						DataList.add(objSalesFlashColumns.getStrField7());
						DataList.add(objSalesFlashColumns.getStrField8());
						DataList.add(objSalesFlashColumns.getStrField9());
						DataList.add(objSalesFlashColumns.getStrField10());
						DataList.add(objSalesFlashColumns.getStrField11());
						DataList.add(objSalesFlashColumns.getStrField12());
						DataList.add(objSalesFlashColumns.getStrField13());
						DataList.add(objSalesFlashColumns.getStrField14());
						DataList.add(objSalesFlashColumns.getStrField15());
						DataList.add(objSalesFlashColumns.getStrField16());
						DataList.add(objSalesFlashColumns.getStrField17());
						map.put(rowCount, DataList);
						rowCount++;
					}
				}

				// sort arrTempListBillWiseSales
				Collections.sort(arrTempListBillWiseSales,
						clsPOSGlobalFunctionsController.COMPARATOR);
				System.out.print("@Dao " + arrTempListBillWiseSales.size());
				try {
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListBillWiseSales = gson.toJson(
							arrTempListBillWiseSales, type);
					jOBjRet.put("TempListBillWiseSales",
							gsonarrTempListBillWiseSales);
					jOBjRet.put("totalDiscAmt", totalDiscAmt);
					jOBjRet.put("totalSubTotal", totalSubTotalDWise);
					jOBjRet.put("totalTaxAmt", totalTaxAmt);
					jOBjRet.put("totalSettleAmt", totalSettleAmt);
					jOBjRet.put("totalTipAmt", totalTipAmt);
					jOBjRet.put("ColHeader", jColHeaderArr);
					jOBjRet.put("colCount", colCount);
					jOBjRet.put("RowCount", rowCount);
					// jOBjRet.put("listStockFlashModel", listStockFlashModel);

					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List list = (List) map.get(tblRow);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						jOBjRet.put("" + tblRow, list);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemWise":
				jColHeaderArr.add("Item Name");
				jColHeaderArr.add("POS ");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("sub Total ");
				jColHeaderArr.add("Sales Amount ");
				jColHeaderArr.add("Discount");
				colCount = 6;

				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");

				String sqlFilters = "";
				if (field.equals("dteBillDate")) {
					field = "b.dteBillDate";
				} else {
					field = "date(b.dteBillDate)";
				}
			
				
				String sqlLive = "select a.strItemCode,a.strItemName,c.strPOSName"
                        + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
                        + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'" + strUserCode+ "' "
                        + ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                        + "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
                        + "where a.strBillNo=b.strBillNo "
                        + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  "
                        + "and b.strPOSCode=c.strPosCode "
                        + "and a.strClientCode=b.strClientCode "
                        + "and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ";

                String sqlQFile = "select a.strItemCode,a.strItemName,c.strPOSName"
                        + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
                        + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'" + strUserCode + "' "
                        + ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                        + "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
                        + "where a.strBillNo=b.strBillNo "
                        + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
                        + "and b.strPOSCode=c.strPosCode "
                        + "and a.strClientCode=b.strClientCode "
                        + "and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ";

                String sqlModLive = "select a.strItemCode,a.strModifierName,c.strPOSName"
                        + " ,sum(a.dblQuantity),'0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
                        + " ,sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                        + " from tblbillmodifierdtl a,tblbillhd b,tblposmaster c,tblitemmaster d\n"
                        + " where a.strBillNo=b.strBillNo "
                        + " AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
                        + " and b.strPOSCode=c.strPosCode "
                        + " and a.strClientCode=b.strClientCode "
                        + " and left(a.strItemCode,7)=d.strItemCode "
                        + " AND a.dblamount>0  "
                        + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ";

                String sqlModQFile = "select a.strItemCode,a.strModifierName,c.strPOSName"
                        + " ,sum(a.dblQuantity),'0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
                        + " ,sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                        + " from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d\n"
                        + " where a.strBillNo=b.strBillNo "
                        + " AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
                        + " and b.strPOSCode=c.strPosCode "
                        + " and a.strClientCode=b.strClientCode "
                        + " and left(a.strItemCode,7)=d.strItemCode  "
                        + " AND a.dblamount>0 "
                        + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
				
				
				
				
				/*if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
					sqlFilters += " AND b.strPOSCode = '" + strPOSCode
							+ "' and b.strUserCreated='" + strOperator + "' ";
				} else if (!strPOSCode.equals("All")
						&& strOperator.equals("All")) {
					sqlFilters += " AND b.strPOSCode = '" + strPOSCode + "' ";
				} else if (strPOSCode.equals("All")
						&& !strOperator.equals("All")) {
					sqlFilters += " AND b.strUserCreated='" + strOperator
							+ "' ";
				}
				sqlFilters += " AND b.intShiftCode = '" + strShiftNo + "' ";

				if (strFromBill.length() == 0 && strToBill.length() == 0) {
				} else {
					sqlFilters += " and a.strbillno between '" + strFromBill
							+ "' " + " and '" + strToBill + "'";
				}

				sqlFilters += " group by a.strItemCode,c.strPOSName "
						+ " order by b.dteBillDate ";
				*/
							
				
					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sqlFilters += " AND b.strPOSCode = '" + strPOSCode
								+ "' and b.strUserCreated='" + strOperator + "' ";
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sqlFilters += " AND b.strPOSCode = '" + strPOSCode + "' ";
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sqlFilters += " AND b.strUserCreated='" + strOperator
								+ "' ";
					}
					//sqlFilters += " AND b.intShiftCode = '" + strShiftNo + "' ";
					if (strFromBill.length() == 0 && strToBill.length() == 0) {
					} else {
						sqlFilters += " and a.strbillno between '" + strFromBill
								+ "' " + " and '" + strToBill + "'";
					}
	                if (!areaCode.equals("All"))
	                {
	                    sqlFilters += " and b.strAreaCode='" + areaCode+ "' ";
	                }
	                if (!operationTye.equalsIgnoreCase("All"))
	                {
	                    sqlFilters += " and b.strOperationType='" + operationTye+ "' ";
	                }
	                if (!Customer.equalsIgnoreCase(""))
	                {
	                    sqlFilters += " and b.strCustomerCode='" + Customer + "' ";

	                }
	                
	                sqlFilters += " group by a.strItemCode,c.strPOSName "
	                        + " order by b.dteBillDate ";
	    		
				
				
				sqlLive = sqlLive + " " + sqlFilters;
				sqlQFile = sqlQFile + " " + sqlFilters;

				sqlModLive = sqlModLive + " " + sqlFilters;
				sqlModQFile = sqlModQFile + " " + sqlFilters;

				System.out.println(sqlModQFile);

				mapPOSItemDtl = new LinkedHashMap<>();

				Query queryLiveItemSales = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(sqlLive);
				List listItemWiseSales = queryLiveItemSales.list();
				funGenerateItemWiseSales(listItemWiseSales, fromDate, toDate,
						strPOSCode, strShiftNo, strUserCode, field, strPayMode,
						strOperator, strFromBill, strToBill, reportType, Type,
						Customer, ConsolidatePOS, ReportName);

				Query queryItemSalesQ = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(sqlQFile);
				List listItemWiseSalesQ = queryItemSalesQ.list();
				funGenerateItemWiseSales(listItemWiseSalesQ, fromDate, toDate,
						strPOSCode, strShiftNo, strUserCode, field, strPayMode,
						strOperator, strFromBill, strToBill, reportType, Type,
						Customer, ConsolidatePOS, ReportName);

				Set<Entry<String, Map<String, clsPOSBillItemDtl>>> set = mapPOSItemDtl
						.entrySet();
				List<Entry<String, Map<String, clsPOSBillItemDtl>>> list = new ArrayList<Entry<String, Map<String, clsPOSBillItemDtl>>>(
						set);

				Collections
						.sort(list,
								new Comparator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>>() {

									@Override
									public int compare(
											Entry<String, Map<String, clsPOSBillItemDtl>> o1,
											Entry<String, Map<String, clsPOSBillItemDtl>> o2) {

										Iterator<Entry<String, clsPOSBillItemDtl>> it1 = o1
												.getValue().entrySet()
												.iterator();
										Iterator<Entry<String, clsPOSBillItemDtl>> it2 = o2
												.getValue().entrySet()
												.iterator();

										if (it1.hasNext()) {
											if (it1.next()
													.getValue()
													.getItemCode()
													.substring(0, 7)
													.equalsIgnoreCase(
															it1.next()
																	.getValue()
																	.getItemCode()
																	.substring(
																			0,
																			7))) {
												return 0;
											} else {
												return 1;
											}
										}
										return 0;
									}

								});

				Iterator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>> posIterator = mapPOSItemDtl
						.entrySet().iterator();

				arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

				while (posIterator.hasNext()) {
					Map<String, clsPOSBillItemDtl> mapItemDtl = posIterator.next()
							.getValue();
					Iterator<Map.Entry<String, clsPOSBillItemDtl>> itemIterator = mapItemDtl
							.entrySet().iterator();
					while (itemIterator.hasNext()) {
						clsPOSBillItemDtl objGroupDtl = itemIterator.next()
								.getValue();
						clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
						obj.setStrField1(objGroupDtl.getItemName());// itemName
						obj.setStrField2(objGroupDtl.getPosName());// posName
						obj.setStrField3(String.valueOf(objGroupDtl
								.getQuantity()));// qty
						obj.setStrField4(String.valueOf(objGroupDtl
								.getSubTotal()));// sunTotal
						obj.setStrField5(String.valueOf(objGroupDtl.getAmount()));// salesAmount
						obj.setStrField6(String.valueOf(objGroupDtl
								.getDiscountAmount()));// discount
						// records[6] = objGroupDtl.getBillDateTime();//date
						arrListSalesReport.add(obj);

						List DataList = new ArrayList<>();
						DataList.add(objGroupDtl.getItemName());
						DataList.add(objGroupDtl.getPosName());
						DataList.add(objGroupDtl.getQuantity());
						DataList.add(objGroupDtl.getSubTotal());
						DataList.add(objGroupDtl.getAmount());
						DataList.add(objGroupDtl.getDiscountAmount());
						map.put(rowCount, DataList);
						rowCount++;

						totalQty = totalQty + objGroupDtl.getQuantity();
						temp1 = new BigDecimal(objGroupDtl.getAmount());
						totalAmount = totalAmount.add(temp1);
						subTotal = subTotal + objGroupDtl.getSubTotal();
						discountTotal = discountTotal
								+ objGroupDtl.getDiscountAmount();

					}
				}
				try {
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListSalesReport = gson.toJson(
							arrListSalesReport, type);
					jOBjRet.put("ListItemWiseSales", gsonarrTempListSalesReport);
					jOBjRet.put("totalQty", totalQty);
					jOBjRet.put("totalAmount", totalAmount);
					jOBjRet.put("subTotal", subTotal);
					jOBjRet.put("discountTotal", discountTotal);
					jOBjRet.put("ColHeader", jColHeaderArr);
					jOBjRet.put("colCount", colCount);
					jOBjRet.put("RowCount", rowCount);
					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List listmap = (List) map.get(tblRow);
						// listmap.add((Double.parseDouble(listmap.get(2).toString())/totalSale)*100);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						jOBjRet.put("" + tblRow, listmap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MenuHeadWise":

				jColHeaderArr.add("Menu Name");
				jColHeaderArr.add("POS ");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("sub Total ");
				jColHeaderArr.add("Sales Amount ");
				jColHeaderArr.add("Discount");
				jColHeaderArr.add("Sales (%)");
				colCount = 7;
				String sql;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "b.dteBillDate";
					} else {
						field = "date(b.dteBillDate)";
					}

					sbSqlQFile
							.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'"
									+ strUserCode
									+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
									+ "FROM tblqbilldtl a\n"
									+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
									+ " and b.strposcode =d.strposcode ");

					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
					}
					sbSqlQFile
							.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sbSqlQFile.append(" where " + field + " BETWEEN '"
							+ fromDate + "' AND '" + toDate + "' ");

					sbSqlLive
							.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'"
									+ strUserCode
									+ "',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
									+ " FROM tblbilldtl a\n"
									+ " left outer join tblbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
									+ " and b.strposcode =d.strposcode ");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
					}
					sbSqlLive
							.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sbSqlLive.append(" where " + field + " BETWEEN '"
							+ fromDate + "' AND '" + toDate + "' ");

					sqlModLive = "";
					sqlModLive = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
							+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'"
							+ strUserCode
							+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
							+ "FROM tblbillmodifierdtl a\n"
							+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
							+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
							+ " and b.strposcode =d.strposcode ";
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sqlModLive += "and b.strAreaCode= d.strAreaCode ";
					}
					sqlModLive += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
					sqlModLive += " where " + field + " BETWEEN '" + fromDate
							+ "' AND '" + toDate + "' and a.dblAmount>0 ";

					sqlModQFile = "";
					sqlModQFile = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
							+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'"
							+ strUserCode
							+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
							+ "FROM tblqbillmodifierdtl a\n"
							+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
							+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
							+ " and b.strposcode =d.strposcode ";

					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sqlModQFile += "and b.strAreaCode= d.strAreaCode ";
					}
					sqlModQFile += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
					sqlModQFile += " where " + field + " BETWEEN '" + fromDate
							+ "' AND '" + toDate + "' and a.dblAmount>0  ";

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "' and d.strUserCreated='"
								+ strOperator.toString() + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "'");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append(" and b.strUserCreated='"
								+ strOperator.toString() + "'");
					}
					if (strFromBill.length() == 0
							&& strToBill.trim().length() == 0) {
						// sql_Filters+=" Group by b.strPoscode, d.strMenuCode,e.strMenuName";
					} else {
						sbSqlFilters.append(" and b.strBillNo between '"
								+ strFromBill + "' and '" + strToBill + "' ");
					}

					sbSqlFilters.append(" AND b.intShiftCode = '" + strShiftNo
							+ "' ");

					sbSqlFilters
							.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");
					sbSqlFilters
							.append(" order by b.strPoscode, d.strMenuCode,e.strMenuName");

					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);

					sqlModLive = sqlModLive + " " + sbSqlFilters.toString();
					sqlModQFile = sqlModQFile + " " + sbSqlFilters.toString();

					mapPOSMenuHeadDtl = new LinkedHashMap<>();

					Query queryMenuHeadSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listMenuHeadWiseSales = queryMenuHeadSales.list();
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					queryMenuHeadSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModLive);
					listMenuHeadWiseSales = queryMenuHeadSales.list();
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					queryMenuHeadSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listMenuHeadWiseSales = queryMenuHeadSales.list();
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					queryMenuHeadSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModQFile);
					listMenuHeadWiseSales = queryMenuHeadSales.list();
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					Iterator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>> posIterator1 = mapPOSMenuHeadDtl
							.entrySet().iterator();
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					totalSale = 0;
					while (posIterator1.hasNext()) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = posIterator1
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSBillItemDtl>> itemIterator = mapItemDtl
								.entrySet().iterator();
						while (itemIterator.hasNext()) {
							clsPOSBillItemDtl objGroupDtl = itemIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
							obj.setStrField1(objGroupDtl.getMenuName());// menuName
							obj.setStrField2(objGroupDtl.getPosName());// posName

							obj.setStrField3(String.valueOf(objGroupDtl
									.getQuantity()));// qty
							obj.setStrField4(String.valueOf(objGroupDtl
									.getAmount()));// salesAmt
							obj.setStrField5(String.valueOf(objGroupDtl
									.getSubTotal()));// subTotal
							obj.setStrField6(String.valueOf(objGroupDtl
									.getDiscountAmount()));// discAmt
							arrListSalesReport.add(obj);

							List DataList = new ArrayList<>();
							DataList.add(objGroupDtl.getMenuName());
							DataList.add(objGroupDtl.getPosName());
							DataList.add(objGroupDtl.getQuantity());
							DataList.add(objGroupDtl.getSubTotal());
							DataList.add(objGroupDtl.getAmount());
							DataList.add(objGroupDtl.getDiscountAmount());
							map.put(rowCount, DataList);
							rowCount++;

							totalQty = totalQty + objGroupDtl.getQuantity();
							totalSale += objGroupDtl.getAmount();
							subTotal = subTotal + objGroupDtl.getSubTotal();
							discountTotal = discountTotal
									+ objGroupDtl.getDiscountAmount();

						}
					}

					try {
						BigDecimal big = new BigDecimal(totalSale);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListMenuHeadWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("totalAmount", big);
						jOBjRet.put("subTotal", subTotal);
						jOBjRet.put("discountTotal", discountTotal);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double
									.parseDouble(listmap.get(4).toString()) / Double
									.parseDouble(big.toString())) * 100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "GroupWise":
				jColHeaderArr.add("Group Name");
				jColHeaderArr.add("POS ");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("sub Total ");
				jColHeaderArr.add("Net Total");
				jColHeaderArr.add("Discount");
				jColHeaderArr.add("Sales (%)");
				colCount = 7;

				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}

					sbSqlLive
							.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),sum( b.dblAmount)-sum(b.dblDiscountAmt) "
									+ ",f.strPosName, '"
									+ strUserCode
									+ "',b.dblRate ,sum(b.dblAmount) "
									+ ",sum(b.dblDiscountAmt),a.strPOSCode,sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount) "
									+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
									+ ",tblitemmaster e,tblposmaster f "
									+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ "and b.strItemCode=e.strItemCode "
									+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");

					sbSqlQFile
							.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),sum( b.dblAmount)-sum(b.dblDiscountAmt) "
									+ ",f.strPosName, '"
									+ strUserCode
									+ "',b.dblRate ,sum(b.dblAmount) "
									+ ",sum(b.dblDiscountAmt),a.strPOSCode,sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount) "
									+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
									+ ",tblitemmaster e,tblposmaster f "
									+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ "and b.strItemCode=e.strItemCode "
									+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");

					sqlModLive = "";
					sqlModLive = "select c.strGroupCode,c.strGroupName,sum(b.dblQuantity)"
							+ ",sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'"
							+ strUserCode
							+ "','0'"
							+ ",sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,sum(b.dblAmount)-sum(b.dblDiscAmt) "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 "
							+ " and "
							+ field
							+ " BETWEEN '"
							+ fromDate
							+ "' AND '"
							+ toDate
							+ "' ";
					sqlModQFile = "";
					sqlModQFile = "select c.strGroupCode,c.strGroupName,sum(b.dblQuantity)"
							+ ",sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'"
							+ strUserCode
							+ "'"
							+ ",'0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,sum(b.dblAmount)-sum(b.dblDiscAmt) "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 "
							+ " and "
							+ field
							+ " BETWEEN '"
							+ fromDate
							+ "' AND '"
							+ toDate
							+ "' ";

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' and a.strUserCreated='"
								+ strOperator + "' ");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append(" and a.strUserCreated='"
								+ strOperator + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "'");
					}

					sbSqlFilters.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");

					if (ConsolidatePOS.equals("Y")) {
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "'"
											+ " GROUP BY c.strGroupCode, c.strGroupName ");
						} else {
							sbSqlFilters
									.append(" WHERE "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ "and a.strBillNo between '"
											+ strFromBill
											+ "' and '"
											+ strToBill
											+ "'"
											+ " GROUP BY c.strGroupCode, c.strGroupName ");
						}
					} else {
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
						} else {
							sbSqlFilters
									.append(" WHERE "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ "and a.strBillNo between '"
											+ strFromBill
											+ "' and '"
											+ strFromBill
											+ "'"
											+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
						}
					}

					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);
					sqlModLive += " " + sbSqlFilters;
					sqlModQFile += " " + sbSqlFilters;

					mapPOSDtlForGroupSubGroup = new LinkedHashMap<>();
					subTotal = 0.00;
					discountTotal = 0.00;

					Query queryGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listGroupWiseSales = queryGroupWiseSales.list();
					funGenerateGroupWiseSales(listGroupWiseSales);

					queryGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModLive);
					listGroupWiseSales = queryGroupWiseSales.list();
					funGenerateGroupWiseSales(listGroupWiseSales);

					queryGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listGroupWiseSales = queryGroupWiseSales.list();
					funGenerateGroupWiseSales(listGroupWiseSales);

					queryGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModQFile);
					listGroupWiseSales = queryGroupWiseSales.list();
					funGenerateGroupWiseSales(listGroupWiseSales);

					double totalSalesAmt = 0, totalGrandTotal = 0;
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>>> subGroupIt1 = mapPOSDtlForGroupSubGroup
							.entrySet().iterator();
					while (subGroupIt1.hasNext()) {
						Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> entry = subGroupIt1
								.next();
						String posCode = entry.getKey();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = entry
								.getValue();
						for (int i = 0; i < listOfGroup.size(); i++) {
							if (ConsolidatePOS.equalsIgnoreCase("Y")) {
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup
										.get(i).entrySet().iterator().next()
										.getValue();

								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// POSName
																			// ...........pending
								obj.setStrField3(String.valueOf(objGroupDtl
										.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl
										.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl
										.getSubTotal()));// subTotal
								obj.setStrField6(String.valueOf(objGroupDtl
										.getDiscAmt()));// discAmt

								Object[] arrObjRows = {
										objGroupDtl.getGroupName(),
										objGroupDtl.getQty(),
										objGroupDtl.getSalesAmt(),
										objGroupDtl.getSubTotal(),
										objGroupDtl.getDiscAmt() };

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								totalSalesAmt += objGroupDtl.getSalesAmt();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal
										+ objGroupDtl.getDiscAmt();
								totalGrandTotal += objGroupDtl.getGrandTotal();
								arrListSalesReport.add(obj);

							} else {
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup
										.get(i).entrySet().iterator().next()
										.getValue();
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// POSName
								obj.setStrField3(String.valueOf(objGroupDtl
										.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl
										.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl
										.getSubTotal()));// subTotal
								obj.setStrField6(String.valueOf(objGroupDtl
										.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								totalSalesAmt += objGroupDtl.getSalesAmt();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal
										+ objGroupDtl.getDiscAmt();
								totalGrandTotal += objGroupDtl.getGrandTotal();
								arrListSalesReport.add(obj);
							}
						}
					}
					try {
						BigDecimal bigtotalSalesAmt = new BigDecimal(
								totalSalesAmt);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListGroupWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("totalAmount", bigtotalSalesAmt);
						jOBjRet.put("subTotal", subTotal);
						jOBjRet.put("discountTotal", discountTotal);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double
									.parseDouble(listmap.get(3).toString()) / Double
									.parseDouble(bigtotalSalesAmt.toString())) * 100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "SubGroupWise":

				jColHeaderArr.add("Sub Group Name");
				jColHeaderArr.add("POS ");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("sub Total ");
				jColHeaderArr.add("Sales Amount");
				jColHeaderArr.add("Discount");
				jColHeaderArr.add("Sales (%)");
				colCount = 7;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");

					temp1 = new BigDecimal("0.00");

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbSqlFilters.setLength(0);
					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}

					sbSqlQFile
							.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
									+ " , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'"
									+ strUserCode
									+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode"
									+ " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d "
									+ " ,tblposmaster f "
									+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ " and b.strItemCode=d.strItemCode "
									+ " and c.strSubGroupCode=d.strSubGroupCode ");

					sbSqlLive
							.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
									+ " , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'"
									+ strUserCode
									+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode"
									+ " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d "
									+ " ,tblposmaster f "
									+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ " and b.strItemCode=d.strItemCode "
									+ " and c.strSubGroupCode=d.strSubGroupCode ");
					sqlModLive = "";
					sqlModLive = "select c.strSubGroupCode,c.strSubGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
							+ ",'"
							+ strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd c"
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=c.strSubGroupCode "
							+ " and b.dblamount>0 " + " and " + field
							+ " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ";
					sqlModQFile = "";
					sqlModQFile = "select c.strSubGroupCode,c.strSubGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
							+ ",'"
							+ strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd c"
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=c.strSubGroupCode "
							+ " and b.dblamount>0 " + " and " + field
							+ " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ";

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' and a.strUserCreated='"
								+ strOperator + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' ");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append(" and a.strUserCreated='"
								+ strOperator + "'");
					}

					sbSqlFilters.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");

					if (ConsolidatePOS.equals("Y")) {
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ " group by c.strSubGroupCode, c.strSubGroupName");
						} else {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ " and a.strBillNo between '"
											+ strFromBill
											+ "' and '"
											+ strToBill
											+ "' "
											+ " group by c.strSubGroupCode, c.strSubGroupName");
						}
					} else {
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "'"
											+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode ");
						} else {
							sbSqlFilters
									.append(" and "
											+ field
											+ " BETWEEN '"
											+ fromDate
											+ "' AND '"
											+ toDate
											+ "' "
											+ " and a.strBillNo between '"
											+ strFromBill
											+ "' and '"
											+ strToBill
											+ "' "
											+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");
						}
					}
					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);
					sqlModLive += " " + sbSqlFilters;
					sqlModQFile += " " + sbSqlFilters;

					mapPOSDtlForGroupSubGroup = new LinkedHashMap<>();
					subTotal = 0.00;
					discountTotal = 0.00;

					Query querySubGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listSubGroupWiseSales = querySubGroupWiseSales.list();
					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					querySubGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModLive);
					listSubGroupWiseSales = querySubGroupWiseSales.list();
					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					querySubGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listSubGroupWiseSales = querySubGroupWiseSales.list();
					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					querySubGroupWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sqlModQFile);
					listSubGroupWiseSales = querySubGroupWiseSales.list();
					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					double salesAmt = 0;
					Iterator<Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>>> iteratorPOS = mapPOSDtlForGroupSubGroup
							.entrySet().iterator();
					while (iteratorPOS.hasNext()) {
						Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> entry = iteratorPOS
								.next();
						String posCode = entry.getKey();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = entry
								.getValue();
						for (int i = 0; i < listOfGroup.size(); i++) {
							if (ConsolidatePOS.equalsIgnoreCase("Y")) {
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup
										.get(i).entrySet().iterator().next()
										.getValue();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// pos
								obj.setStrField3(String.valueOf(objGroupDtl
										.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl
										.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl
										.getSubTotal()));// subtotal
								obj.setStrField6(String.valueOf(objGroupDtl
										.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;

								totalQty = totalQty + objGroupDtl.getQty();
								temp1 = temp1.add(new BigDecimal(objGroupDtl
										.getSubTotal()));
								salesAmt += salesAmt
										+ objGroupDtl.getSubTotal();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal
										+ objGroupDtl.getDiscAmt();
								arrListSalesReport.add(obj);
							} else {
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup
										.get(i).entrySet().iterator().next()
										.getValue();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// pos
								obj.setStrField3(String.valueOf(objGroupDtl
										.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl
										.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl
										.getSubTotal()));// subtotal
								obj.setStrField6(String.valueOf(objGroupDtl
										.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								temp1 = temp1.add(new BigDecimal(objGroupDtl
										.getSubTotal()));
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal
										+ objGroupDtl.getDiscAmt();
								arrListSalesReport.add(obj);
							}
						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListSubGroupWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("SalesAmt", temp1);
						jOBjRet.put("subTotal", subTotal);
						jOBjRet.put("discountTotal", discountTotal);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double
									.parseDouble(listmap.get(3).toString()) / Double
									.parseDouble(temp1.toString())) * 100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "CustWise":

				jColHeaderArr.add("Customer Name");
				jColHeaderArr.add("No Of Bills");
				jColHeaderArr.add("Sales Amount");
				colCount = 3;
				if (reportType.equalsIgnoreCase("Item Wise")) {

					// StringBuilder sbSqlFilters = new StringBuilder();
					try {
						sql = "";
						sbSqlLiveBill.setLength(0);
						sbSqlQFileBill.setLength(0);
						sbSqlFilters.setLength(0);

						sbSqlLiveBill
								.append("select a.strBillNo,date(a.dteBillDate)"
										+ ",c.strCustomerCode,c.strCustomerName,d.strItemName"
										+ ",sum(b.dblQuantity),sum(b.dblAmount),'"
										+ strUserCode
										+ "' "
										+ "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
										+ "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
										+ "and b.strItemCode=d.strItemCode and a.strCustomerCode='"
										+ Customer
										+ "'"
										+ "and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill
								.append("select a.strBillNo,date(a.dteBillDate)"
										+ ",c.strCustomerCode,c.strCustomerName,d.strItemName"
										+ ",sum(b.dblQuantity),sum(b.dblAmount),'"
										+ strUserCode
										+ "' "
										+ "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
										+ "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
										+ "and b.strItemCode=d.strItemCode and a.strCustomerCode='"
										+ Customer
										+ "'"
										+ "and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbSqlFilters.append(" and a.strPOSCode='"
									+ strPOSCode + "' ");
						}
						if (!strOperator.equals("All")) {
							sbSqlFilters.append(" and  a.strUserCreated='"
									+ strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbSqlFilters.append(" and a.strSettelmentMode='"
									+ strPayMode + "' ");
						}
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {

						} else {
							sbSqlFilters
									.append(" and a.strBillNo between '"
											+ strFromBill + "' and '"
											+ strToBill + "'");
						}

						sbSqlFilters.append(" AND a.intShiftCode = '"
								+ strShiftNo + "' ");

						sbSqlFilters.append(" group by a.strBillNo");

						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbSqlFilters);
						sbSqlQFileBill.append(sbSqlFilters);

						Query query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(
										"truncate table tbltempsalesflash1;");

						String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
								+ ",strpaymode,dblsubtotal,struser) "
								+ "("
								+ sbSqlLiveBill + ");";
						String sqlInsertQFileBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
								+ ",strpaymode,dblsubtotal,struser) "
								+ "("
								+ sbSqlQFileBill + ");";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertLiveBillSales);
						query.executeUpdate();
						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertQFileBillSales);
						query.executeUpdate();

						sql = "select * from tbltempsalesflash1 where strUser='"
								+ strUserCode + "'";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sql);
						List listSG = query.list();
						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);

								objsales.setStrField1(obj[0].toString());// Bill
																			// No
								String tempBillDate = obj[1].toString();
								String[] spDate = tempBillDate.split("-");
								String Billdate = spDate[2] + "-" + spDate[1]
										+ "-" + spDate[0];// Bill Date
								objsales.setStrField2(Billdate);
								objsales.setStrField3(obj[2].toString()); // Cust
																			// Code
								objsales.setStrField4(obj[3].toString());// Cust
																			// Name
								objsales.setStrField5(obj[4].toString());// Item
																			// Name
								objsales.setStrField6(obj[5].toString());// Qty
								objsales.setStrField7(obj[6].toString());// Amount

								List DataList = new ArrayList<>();
								DataList.add(obj[3].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());

								map.put(rowCount, DataList);
								rowCount++;
								qty += Double.parseDouble(obj[5].toString());
								amount += Double.parseDouble(obj[6].toString());
								arrListSalesReport.add(objsales);
							}
						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(
									arrListSalesReport, type);
							jOBjRet.put("ListCustWiseSales",
									gsonarrTempListSalesReport);
							jOBjRet.put("totalQty", qty);
							jOBjRet.put("SalesAmt", amount);
							jOBjRet.put("ColHeader", jColHeaderArr);
							jOBjRet.put("colCount", colCount);
							jOBjRet.put("RowCount", rowCount);
							for (int tblRow = 0; tblRow < map.size(); tblRow++) {
								List listmap = (List) map.get(tblRow);
								// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/salesAmt)*100));
								jOBjRet.put("" + tblRow, listmap);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (reportType.equalsIgnoreCase("Customer Wise")) {

					try {
						sql = "";

						sbSqlLiveBill.setLength(0);
						sbSqlQFileBill.setLength(0);
						sbSqlFilters.setLength(0);

						sbSqlLiveBill
								.append("select b.strCustomerCode,b.strCustomerName "
										+ " ,a.strBillNo,sum(a.dblGrandTotal),'"
										+ strUserCode
										+ "' "
										+ " from tblbillhd a,tblcustomermaster b "
										+ " where a.strCustomerCode=b.strCustomerCode and a.strCustomerCode='"
										+ Customer
										+ "' "
										+ " and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill
								.append("select b.strCustomerCode,b.strCustomerName "
										+ " ,a.strBillNo,sum(a.dblGrandTotal),'"
										+ strUserCode
										+ "' "
										+ " from tblqbillhd a,tblcustomermaster b "
										+ " where a.strCustomerCode=b.strCustomerCode and a.strCustomerCode='"
										+ Customer
										+ "' "
										+ " and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbSqlFilters.append(" and a.strPOSCode='"
									+ strPOSCode + "' ");
						}
						if (!strOperator.equals("All")) {
							sbSqlFilters.append(" and  a.strUserCreated='"
									+ strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbSqlFilters.append(" and a.strSettelmentMode='"
									+ strPayMode + "' ");
						}
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
						} else {
							sbSqlFilters
									.append(" and a.strBillNo between '"
											+ strFromBill + "' and '"
											+ strToBill + "'");
						}

						sbSqlFilters.append(" AND a.intShiftCode = '"
								+ strShiftNo + "' ");

						sbSqlFilters.append(" group by a.strBillNo");

						double grandTotal = 0;
						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbSqlFilters);
						sbSqlQFileBill.append(sbSqlFilters);

						Query query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(
										"truncate table tbltempsalesflash1;");

						String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
								+ "(" + sbSqlLiveBill + ");";
						String sqlInsertQFileBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
								+ "(" + sbSqlQFileBill + ");";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertLiveBillSales);
						query.executeUpdate();
						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertQFileBillSales);
						query.executeUpdate();
						sql = "select * from tbltempsalesflash1 where strUser='"
								+ strUserCode + "'";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sql);
						List listSG = query.list();
						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								flgRecords = true;

								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);

								objsales.setStrField1(obj[0].toString()); // Cust
																			// Code
								objsales.setStrField2(obj[1].toString());// Cust
																			// Name
								objsales.setStrField3(obj[2].toString());// Count
								objsales.setStrField4(obj[3].toString());// Grand
																			// tot

								grandTotal += Double.parseDouble(obj[3]
										.toString());
								arrListSalesReport.add(objsales);

							}
						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(
									arrListSalesReport, type);
							jOBjRet.put("ListCustWiseSales",
									gsonarrTempListSalesReport);
							jOBjRet.put("grandTotal", grandTotal);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {

					try {
						sbSqlLiveBill.setLength(0);
						sbSqlQFileBill.setLength(0);
						sbSqlFilters.setLength(0);

						sbSqlLiveBill
								.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
										+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'"
										+ strUserCode
										+ "' "
										+ "from tblbillhd a,tblcustomermaster b "
										+ "where a.strCustomerCode=b.strCustomerCode "
										+ "and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill
								.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
										+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'"
										+ strUserCode
										+ "' "
										+ "from tblqbillhd a,tblcustomermaster b "
										+ "where a.strCustomerCode=b.strCustomerCode "
										+ "and date(a.dteBillDate) between '"
										+ fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbSqlFilters.append(" and a.strPOSCode='"
									+ strPOSCode + "' ");
						}
						if (!strOperator.equals("All")) {
							sbSqlFilters.append(" and  a.strUserCreated='"
									+ strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbSqlFilters.append(" and a.strSettelmentMode='"
									+ strPayMode + "' ");
						}
						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
						} else {
							sbSqlFilters
									.append(" and a.strBillNo between '"
											+ strFromBill + "' and '"
											+ strToBill + "'");
						}

						sbSqlFilters.append(" AND a.intShiftCode = '"
								+ strShiftNo + "' ");

						sbSqlFilters.append(" GROUP BY b.strCustomerCode");
						double grandTotal = 0;
						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbSqlFilters);
						sbSqlQFileBill.append(sbSqlFilters);
						Query query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(
										"truncate table tbltempsalesflash1;");
						query.executeUpdate();

						String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
								+ "(" + sbSqlLiveBill + ");";
						String sqlInsertQFileBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
								+ "(" + sbSqlQFileBill + ");";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertLiveBillSales);
						query.executeUpdate();
						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sqlInsertQFileBillSales);
						query.executeUpdate();
						sql = "select * from tbltempsalesflash1 where strUser='"
								+ strUserCode + "'";

						query = webPOSSessionFactory.getCurrentSession()
								.createSQLQuery(sql);
						List listSG = query.list();
						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
						int billCount = 0;

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								flgRecords = true;

								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);
								objsales.setStrField1(obj[1].toString());// Cust
																			// Name
								objsales.setStrField2(obj[2].toString());// count
								objsales.setStrField3(obj[3].toString());// Grand
																			// Total
								List DataList = new ArrayList<>();
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());

								map.put(rowCount, DataList);
								rowCount++;
								billCount += Integer
										.parseInt(obj[2].toString());
								grandTotal += Double.parseDouble(obj[3]
										.toString());
								arrListSalesReport.add(objsales);
							}

						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(
									arrListSalesReport, type);
							jOBjRet.put("ListCustWiseSales",
									gsonarrTempListSalesReport);
							jOBjRet.put("billCount", billCount);
							jOBjRet.put("grandTotal", grandTotal);
							jOBjRet.put("ColHeader", jColHeaderArr);
							jOBjRet.put("colCount", colCount);
							jOBjRet.put("RowCount", rowCount);
							for (int tblRow = 0; tblRow < map.size(); tblRow++) {
								List listmap = (List) map.get(tblRow);
								// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/salesAmt)*100));
								jOBjRet.put("" + tblRow, listmap);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case "WaiterWise":
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Waiter Full Name");
				jColHeaderArr.add("Waiter Short Name");
				jColHeaderArr.add("Sales Amount");

				colCount = 4;
				try {
					sql = "";

					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);

					sbSqlLive
							.append("select c.strPosName,b.strWShortName,b.strWFullName"
									+ ",SUM(d.dblSettlementAmt),'"
									+ strUserCode
									+ "',b.strWaiterNo,c.strPosCode "
									+ " from tblbillhd a,tblwaitermaster b, tblposmaster c,tblbillsettlementdtl d "
									+ " where a.strWaiterNo=b.strWaiterNo and a.strPOSCode=c.strPosCode "
									+ " and date( a.dteBillDate ) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " and a.strBillNo=d.strBillNo and a.strClientCode=d.strClientCode ");

					sbSqlQFile
							.append("select c.strPosName,b.strWShortName,b.strWFullName"
									+ ",SUM(d.dblSettlementAmt),'"
									+ strUserCode
									+ "',b.strWaiterNo,c.strPosCode "
									+ " from tblqbillhd a,tblwaitermaster b, tblposmaster c,tblqbillsettlementdtl d "
									+ " where a.strWaiterNo=b.strWaiterNo and a.strPOSCode=c.strPosCode "
									+ " and date( a.dteBillDate ) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "'"
									+ " and a.strBillNo=d.strBillNo and a.strClientCode=d.strClientCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlLive.append(" and a.strPOSCode='" + strPOSCode
								+ "' ");

						sbSqlQFile.append(" and a.strPOSCode='" + strPOSCode
								+ "' ");
					}
					sbSqlLive.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");
					sbSqlQFile.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");

					sbSqlLive.append(" group by a.strWaiterNo,a.strPOSCode");
					sbSqlQFile.append(" group by a.strWaiterNo,a.strPOSCode");
					mapPOSWaiterWiseSales = new LinkedHashMap<>();

					Query queryWaiterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listWaiterWiseSales = queryWaiterWiseSales.list();
					funGenerateWaiterWiseSales(listWaiterWiseSales);

					queryWaiterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listWaiterWiseSales = queryWaiterWiseSales.list();
					funGenerateWaiterWiseSales(listWaiterWiseSales);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorWaiter = mapPOSWaiterWiseSales
							.entrySet().iterator();
					while (posIteratorWaiter.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = posIteratorWaiter
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> itemIterator = mapWaiterDtl
								.entrySet().iterator();
						while (itemIterator.hasNext()) {
							clsPOSCommonBeanDtl objWaiterDtl = itemIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objWaiterDtl.getPosName());
							objsales.setStrField2(objWaiterDtl
									.getWaiterShortName());
							objsales.setStrField3(objWaiterDtl
									.getWaiterFullName());
							objsales.setStrField4(String.valueOf(objWaiterDtl
									.getSaleAmount()));

							arrListSalesReport.add(objsales);
							totalSale += objWaiterDtl.getSaleAmount();
							List DataList = new ArrayList<>();
							DataList.add(objWaiterDtl.getPosName());
							DataList.add(objWaiterDtl.getWaiterFullName());
							DataList.add(objWaiterDtl.getWaiterShortName());
							DataList.add(objWaiterDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListWaiterWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("TotalAmount", totalSale);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "DeliveryBoyWise":
				jColHeaderArr.add("Delivery Boy Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Sales Amount");
				jColHeaderArr.add("Delivery Charges");
				colCount = 4;
				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);

					sbSqlLiveBill
							.append("select a.strDPCode,b.strDPName,d.strPOSName"
									+ " ,sum(c.dblGrandTotal),sum(a.dblHomeDeliCharge),'"
									+ strUserCode
									+ "',a.strPOSCode "
									+ " from tblhomedelivery a,tbldeliverypersonmaster b,tblbillhd c, tblposmaster d "
									+ " WHERE a.strBillNo=c.strBillNo and a.strDPCode=b.strDPCode "
									+ " and c.strPOSCode=d.strPOSCode ");

					sbSqlQFileBill
							.append("select a.strDPCode,b.strDPName,d.strPOSName"
									+ " ,sum(c.dblGrandTotal),sum(a.dblHomeDeliCharge),'"
									+ strUserCode
									+ "',a.strPOSCode "
									+ " from tblhomedelivery a,tbldeliverypersonmaster b,tblqbillhd c, tblposmaster d "
									+ " WHERE a.strBillNo=c.strBillNo and a.strDPCode=b.strDPCode "
									+ " and c.strPOSCode=d.strPOSCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' ");
						sbSqlQFileBill.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' ");
					}

					sbSqlLiveBill.append(" AND c.intShiftCode = '" + strShiftNo
							+ "' ");
					sbSqlQFileBill.append(" AND c.intShiftCode = '"
							+ strShiftNo + "' ");

					sbSqlLiveBill.append(" and date(a.dteDate) BETWEEN '"
							+ fromDate + "' AND '" + toDate + "'"
							+ " GROUP BY a.strDPCode");
					sbSqlQFileBill.append(" and date(a.dteDate) BETWEEN '"
							+ fromDate + "' AND '" + toDate + "'"
							+ " GROUP BY a.strDPCode");
					double totalAmt = 0;

					mapPOSDeliveryBoyWise = new LinkedHashMap<>();

					Query queryDeliveryBoyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLiveBill.toString());
					List listDeliveryBoyWiseSales = queryDeliveryBoyWiseSales
							.list();
					funGenerateDelBoyWiseSales(listDeliveryBoyWiseSales);

					queryDeliveryBoyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFileBill.toString());
					listDeliveryBoyWiseSales = queryDeliveryBoyWiseSales.list();
					funGenerateDelBoyWiseSales(listDeliveryBoyWiseSales);
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorDelBoy = mapPOSDeliveryBoyWise
							.entrySet().iterator();
					while (posIteratorDelBoy.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = posIteratorDelBoy
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> itemIterator = mapDBDtl
								.entrySet().iterator();
						while (itemIterator.hasNext()) {
							clsPOSCommonBeanDtl objDBDtl = itemIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objDBDtl.getDbName());
							objsales.setStrField2(objDBDtl.getPosName());
							objsales.setStrField3(String.valueOf(objDBDtl
									.getSaleAmount()));
							objsales.setStrField4(String.valueOf(objDBDtl
									.getDelCharges()));
							arrListSalesReport.add(objsales);
							totalAmt += objDBDtl.getSaleAmount();
							List DataList = new ArrayList<>();
							DataList.add(objDBDtl.getDbName());
							DataList.add(objDBDtl.getPosName());
							DataList.add(objDBDtl.getSaleAmount());
							DataList.add(objDBDtl.getDelCharges());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListDelBoyWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("TotalAmount", totalAmt);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "CostCenterWise":
				jColHeaderArr.add("Cost Center Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("Sub Total");
				jColHeaderArr.add("Sales Amount");
				jColHeaderArr.add("Discount");
				jColHeaderArr.add("Sales (%)");
				colCount = 7;
				try {
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "d.dteBillDate";
					} else {
						field = "date(d.dteBillDate)";
					}
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbSqlFilters.setLength(0);

					sbSqlLive
							.append("SELECT ifnull(a.strCostCenterCode,'ND')"
									+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
									+ " ,sum( c.dblAmount )-sum(c.dblDiscountAmt), e.strPOSName,'"
									+ strUserCode
									+ "' "
									+ ",c.dblRate  ,sum(c.dblAmount),sum(c.dblDiscountAmt),e.strPosCode  "
									+ " from tblbilldtl c left outer join tblbillhd d on c.strBillNo = d.strBillNo "
									+ " and c.strClientCode=d.strClientCode "
									+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
									+ " left outer join tblmenuitempricingdtl b on b.strItemCode = c.strItemCode \n"
									+ " and b.strposcode =d.strposcode\n"
									+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
									+ " where " + field + " BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sbSqlLive.append(" and d.strAreaCode=b.strAreaCode ");
					}

					// QFile Sql
					sbSqlQFile
							.append("SELECT ifnull(a.strCostCenterCode,'ND')"
									+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
									+ " ,sum( c.dblAmount )-sum(c.dblDiscountAmt), e.strPOSName,'"
									+ strUserCode
									+ "'"
									+ ",c.dblRate ,sum(c.dblAmount),sum(c.dblDiscountAmt),e.strPosCode "
									+ " from tblqbilldtl c left outer join tblqbillhd d on c.strBillNo = d.strBillNo "
									+ " and c.strClientCode=d.strClientCode "
									+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
									+ " left outer join tblmenuitempricingdtl b on b.strItemCode = c.strItemCode \n"
									+ " and b.strposcode =d.strposcode\n"
									+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
									+ " where " + field + " BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if ("Y".equals("Y")) {
						sbSqlQFile.append(" and d.strAreaCode=b.strAreaCode ");
					}
					sqlModLive = "";
					sqlModLive = "SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum(c.dblAmount)-sum(c.dblDiscAmt), e.strPOSName,'"
							+ strUserCode
							+ "'"
							+ ",c.dblRate ,sum( c.dblAmount ),sum(c.dblDiscAmt),e.strPosCode "
							+ " from tblbillmodifierdtl c left outer join tblbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode =LEFT(c.strItemCode,7)\n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate
							+ "' AND '" + toDate + "' " + " and c.dblAmount>0";
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sqlModLive += " and d.strAreaCode=b.strAreaCode ";
					}
					sqlModQFile = "";
					sqlModQFile = "SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum(c.dblAmount)-sum(c.dblDiscAmt), e.strPOSName,'"
							+ strUserCode
							+ "'"
							+ ",c.dblRate ,sum( c.dblAmount ),sum(c.dblDiscAmt),e.strPosCode "
							+ " from tblqbillmodifierdtl c left outer join tblqbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode =LEFT(c.strItemCode,7) \n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate
							+ "' AND '" + toDate + "' " + " and c.dblAmount>0";
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sqlModQFile += " and d.strAreaCode=b.strAreaCode ";
					}

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND d.strPOSCode = '"
								+ strPOSCode + "' and d.strUserCreated='"
								+ strOperator + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND d.strPOSCode = '"
								+ strPOSCode + "'");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append(" and d.strUserCreated='"
								+ strOperator + "'");
					}

					sbSqlFilters.append(" AND d.intShiftCode = '" + strShiftNo
							+ "' ");

					if (strFromBill.length() == 0 && strToBill.length() == 0) {
						sbSqlFilters
								.append(" GROUP BY b.strCostCenterCode,a.strCostCenterName, e.strPOSName,c.dblRate");
					} else {
						sbSqlFilters
								.append(" and d.strBillNo between '"
										+ strFromBill
										+ "' and '"
										+ strToBill
										+ "' "
										+ "GROUP BY b.strCostCenterCode,a.strCostCenterName, e.strPOSName,c.dblRate");
					}

					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);
					sqlModLive = sqlModLive + " " + sbSqlFilters.toString();
					sqlModQFile = sqlModQFile + " " + sbSqlFilters.toString();
					subTotal = 0.00;
					discountTotal = 0.00;

					mapPOSCostCenterWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					Query queryCostCenterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listCostCenterWiseSales = queryCostCenterWiseSales
							.list();
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					queryCostCenterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listCostCenterWiseSales = queryCostCenterWiseSales.list();
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					queryCostCenterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlModLive.toString());
					listCostCenterWiseSales = queryCostCenterWiseSales.list();
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					queryCostCenterWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlModQFile.toString());
					listCostCenterWiseSales = queryCostCenterWiseSales.list();
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					double totalAmt = 0;
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorCost = mapPOSCostCenterWiseSales
							.entrySet().iterator();
					while (posIteratorCost.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = posIteratorCost
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> ccIterator = mapCCDtl
								.entrySet().iterator();
						while (ccIterator.hasNext()) {
							clsPOSCommonBeanDtl objCCDtl = ccIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objCCDtl.getCostCenterName());// ccName
							objsales.setStrField2(objCCDtl.getPosName());// posName
							objsales.setStrField3(String.valueOf(objCCDtl
									.getQty()));// Qty
							objsales.setStrField4(String.valueOf(objCCDtl
									.getSaleAmount()));// salesAmt
							objsales.setStrField5(String.valueOf(objCCDtl
									.getSubTotal()));// subTotal
							objsales.setStrField6(String.valueOf(objCCDtl
									.getDiscAmount()));// discAmt
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objCCDtl.getCostCenterName());
							DataList.add(objCCDtl.getPosName());
							DataList.add(objCCDtl.getQty());
							DataList.add(objCCDtl.getSaleAmount());
							DataList.add(objCCDtl.getSubTotal());
							DataList.add(objCCDtl.getDiscAmount());
							map.put(rowCount, DataList);
							rowCount++;

							totalQty = totalQty + objCCDtl.getQty();
							totalAmt = totalAmt + objCCDtl.getSaleAmount();
							subTotal = subTotal + objCCDtl.getSubTotal();
							discountTotal = discountTotal
									+ objCCDtl.getDiscAmount();

						}
					}
					try {
						BigDecimal bigtotalAmt = new BigDecimal(totalAmt);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListCostCentWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("totalAmt", bigtotalAmt);
						jOBjRet.put("subTotal", subTotal);
						jOBjRet.put("discountTotal", discountTotal);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double
									.parseDouble(listmap.get(3).toString()) / Double
									.parseDouble(bigtotalAmt.toString())) * 100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "HomeDeliveryWise":
				jColHeaderArr.add("Bill No");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Date");
				jColHeaderArr.add("Settle Mode");
				jColHeaderArr.add("Delivery Charges");
				jColHeaderArr.add("Disc Amt");
				jColHeaderArr.add("Tax Amt");
				jColHeaderArr.add("Amount");
				jColHeaderArr.add("Customer Name ");
				jColHeaderArr.add("Bulding");
				jColHeaderArr.add("Delv Boy");
				colCount = 11;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					BigDecimal sumDisc = new BigDecimal("0.00");
					BigDecimal sumtax = new BigDecimal("0.00");
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);

					sbSqlLive
							.append("SELECT ifnull(a.strBillNo,''),ifnull(f.strPosName,''),ifnull(DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),''),ifnull(b.strSettelmentMode,'') "
									+ " ,ifnull(b.dblDeliveryCharges,'') ,ifnull(b.dblDiscountAmt,''),ifnull(b.dblTaxAmt,''),ifnull(b.dblGrandTotal,'') ,"
									+ " ifnull(c.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(d.strDPName,''),'"
									+ strUserCode
									+ "' "
									+ " FROM tblhomedelivery a INNER JOIN tblbillhd b ON a.strBillNo = b.strBillNo "
									+ " INNER JOIN tblcustomermaster c ON a.strCustomerCode = c.strCustomerCode "
									+ " left OUTER Join tbldeliverypersonmaster d on a.strDPCode=d.strDPCode "
									+ " left OUTER Join tblbuildingmaster e on e.strBuildingCode=c.strBuldingCode"
									+ " left outer join tblposmaster f on b.strPOSCode=f.strPosCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'");
					if (!strPOSCode.equals("All")) {
						sbSqlLive.append(" AND b.strPOSCode = '" + strPOSCode
								+ "'");
					}

					sbSqlLive.append(" AND b.intShiftCode = '" + strShiftNo
							+ "' ");

					sbSqlQFile
							.append("SELECT ifnull(a.strBillNo,''),ifnull(f.strPosName,''),ifnull(DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),''),ifnull(b.strSettelmentMode,'') "
									+ " ,ifnull(b.dblDeliveryCharges,'') ,ifnull(b.dblDiscountAmt,''),ifnull(b.dblTaxAmt,''),ifnull(b.dblGrandTotal,'') ,"
									+ " ifnull(c.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(d.strDPName,''),'"
									+ strUserCode
									+ "' "
									+ " FROM tblhomedelivery a INNER JOIN tblqbillhd b ON a.strBillNo = b.strBillNo "
									+ " INNER JOIN tblcustomermaster c ON a.strCustomerCode = c.strCustomerCode "
									+ " left OUTER Join tbldeliverypersonmaster d on a.strDPCode=d.strDPCode "
									+ " left OUTER Join tblbuildingmaster e on e.strBuildingCode=c.strBuldingCode "
									+ " left outer join tblposmaster f on b.strPOSCode=f.strPosCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'");
					if (!strPOSCode.equals("All")) {
						sbSqlQFile.append(" AND b.strPOSCode = '" + strPOSCode
								+ "'");
					}

					sbSqlQFile.append(" AND b.intShiftCode = '" + strShiftNo
							+ "' ");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryHomeDelWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listHomeDelWiseSales = queryHomeDelWiseSales.list();

					try {
						if (listHomeDelWiseSales.size() > 0) {
							for (int i = 0; i < listHomeDelWiseSales.size(); i++) {

								Object[] obj = (Object[]) listHomeDelWiseSales
										.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								objsales.setStrField1(obj[0].toString());// bilNo
								objsales.setStrField2(obj[1].toString());// posName
								objsales.setStrField3(obj[2].toString());// billDate
								objsales.setStrField4(obj[3].toString());// settleMode
								objsales.setStrField5(obj[4].toString());// delCharges
								objsales.setStrField6(obj[5].toString());// disc
								objsales.setStrField7(obj[6].toString());// taxAmt
								objsales.setStrField8(obj[7].toString());// totalAmt
								objsales.setStrField9(obj[8].toString());// custName
								objsales.setStrField10(obj[9].toString());// address
								objsales.setStrField11(obj[10].toString());// delBoy
								sumDisc = sumDisc.add(new BigDecimal(obj[5]
										.toString()));
								sumtax = sumtax.add(new BigDecimal(obj[6]
										.toString()));
								temp1 = temp1.add(new BigDecimal(obj[7]
										.toString()));
								arrListSalesReport.add(objsales);

								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								DataList.add(obj[4].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());
								DataList.add(obj[7].toString());
								DataList.add(obj[8].toString());
								DataList.add(obj[9].toString());
								DataList.add(obj[10].toString());
								map.put(rowCount, DataList);
								rowCount++;

							}
						}
					} catch (Exception e) {

					}

					queryHomeDelWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listHomeDelWiseSales = queryHomeDelWiseSales.list();
					try {
						if (listHomeDelWiseSales.size() > 0) {
							for (int i = 0; i < listHomeDelWiseSales.size(); i++) {
								Object[] obj = (Object[]) listHomeDelWiseSales
										.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								objsales.setStrField1(obj[0].toString());// bilNo
								objsales.setStrField2(obj[1].toString());// posName
								objsales.setStrField3(obj[2].toString());// billDate
								objsales.setStrField4(obj[3].toString());// settleMode
								objsales.setStrField5(obj[4].toString());// delCharges
								objsales.setStrField6(obj[5].toString());// disc
								objsales.setStrField7(obj[6].toString());// taxAmt
								objsales.setStrField8(obj[7].toString());// totalAmt
								objsales.setStrField9(obj[8].toString());// custName
								objsales.setStrField10(obj[9].toString());// address
								objsales.setStrField11(obj[10].toString());// delBoy
								sumDisc = sumDisc.add(new BigDecimal(obj[5]
										.toString()));
								sumtax = sumtax.add(new BigDecimal(obj[6]
										.toString()));
								temp1 = temp1.add(new BigDecimal(obj[7]
										.toString()));
								arrListSalesReport.add(objsales);

								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								DataList.add(obj[4].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());
								DataList.add(obj[7].toString());
								DataList.add(obj[8].toString());
								DataList.add(obj[9].toString());
								DataList.add(obj[10].toString());
								map.put(rowCount, DataList);
								rowCount++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListHomeDelWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("sumDisc", sumDisc);
						jOBjRet.put("sumtax", sumtax);
						jOBjRet.put("SalesAmt", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/totalAmt)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TableWise":
				jColHeaderArr.add("POS ");
				jColHeaderArr.add("Table Name");
				jColHeaderArr.add("sales Amount");
				colCount = 3;
				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");
				try {
					sqlQFile = "";
					sqlQFile = "select c.strPOSName,b.strTableName,'0',SUM(d.dblSettlementAmt)"
							+ ",'"
							+ strPOSCode
							+ "','"
							+ strUserCode
							+ "','0' ,'ND','ND',a.strTableNo "
							+ " from tblqbillhd a,tbltablemaster b,tblposmaster c,tblqbillsettlementdtl d "
							+ " where date( a.dteBillDate ) BETWEEN '"
							+ fromDate
							+ "' AND '"
							+ toDate
							+ "' "
							+ " and a.strTableNo=b.strTableNo and a.strPOSCode=c.strPOSCode"
							+ " and a.strBillNo=d.strBillNo and a.strClientCode=d.strClientCode ";

					String sqlLiveTables = "select c.strPOSName,b.strTableName,'0',SUM(d.dblSettlementAmt)"
							+ ",'"
							+ strPOSCode
							+ "','"
							+ strUserCode
							+ "','0' ,'ND','ND',a.strTableNo "
							+ " from tblbillhd a,tbltablemaster b,tblposmaster c,tblbillsettlementdtl d "
							+ " where date( a.dteBillDate ) BETWEEN '"
							+ fromDate
							+ "' AND '"
							+ toDate
							+ "' "
							+ " and a.strTableNo=b.strTableNo and a.strPOSCode=c.strPOSCode and a.strBillNo=d.strBillNo "
							+ " and a.strClientCode=d.strClientCode ";

					if (!strPOSCode.equals("All")) {
						sqlQFile += " AND a.strPOSCode = '" + strPOSCode + "'";
						sqlLiveTables += " AND a.strPOSCode = '" + strPOSCode
								+ "'";
					}

					sqlLiveTables += " AND a.intShiftCode = '" + strShiftNo
							+ "' ";
					sqlQFile += " AND a.intShiftCode = '" + strShiftNo + "' ";

					sqlQFile += " group by a.strTableNo ";
					sqlLiveTables += " group by a.strTableNo ";

					mapPOSTableWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryTableWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlLiveTables.toString());
					List listTableWiseSales = queryTableWiseSales.list();
					funGenerateTableWiseSales(listTableWiseSales);
					queryTableWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlQFile.toString());
					listTableWiseSales = queryTableWiseSales.list();
					funGenerateTableWiseSales(listTableWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorTable = mapPOSTableWiseSales
							.entrySet().iterator();
					while (posIteratorTable.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = posIteratorTable
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> tblIterator = mapTblDtl
								.entrySet().iterator();
						while (tblIterator.hasNext()) {
							clsPOSCommonBeanDtl objTblDtl = tblIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objTblDtl.getPosName());
							objsales.setStrField2(objTblDtl.getTableName());
							objsales.setStrField3(String.valueOf(objTblDtl
									.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String
									.valueOf(objTblDtl.getSaleAmount())));
							totalSale += objTblDtl.getSaleAmount();
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(objTblDtl.getPosName());
							DataList.add(objTblDtl.getTableName());
							DataList.add(objTblDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListTableWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("SalesAmt", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/totalAmt)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "HourlyWise":
				jColHeaderArr.add("Date Range");
				jColHeaderArr.add("On Of Bills Name");
				jColHeaderArr.add("sales Amount");
				jColHeaderArr.add("sales (%)");
				colCount = 4;
				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					sqlQFile = "";
					sqlQFile = "select left(right(a.dteDateCreated,8),2),left(right(a.dteDateCreated,8),2) +1"
							+ ",count(*),sum(b.dblSettlementAmt),'"
							+ strPOSCode
							+ "'  "
							+ ",'"
							+ strUserCode
							+ "','0' ,'ND','ND'  \n"
							+ " from tblqbillhd a,tblqbillsettlementdtl b";

					String sqlLiveTables = "select left(right(a.dteDateCreated,8),2),left(right(a.dteDateCreated,8),2) +1"
							+ ",count(*),sum(b.dblSettlementAmt),'"
							+ strPOSCode
							+ "'  "
							+ ",'"
							+ strUserCode
							+ "','0' ,'ND','ND'  \n"
							+ " from tblbillhd a,tblbillsettlementdtl b ";

					String EnableShiftYN = "Y";

					if (!strPOSCode.equals("All")) {
						/*JSONObject JSONEnableShiftYN = new clsSetupDao()
								.funGetParameterValuePOSWise(strUserCode,
										strPOSCode, "gAreaWisePricing");
										*/
						// String
						// EnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
						EnableShiftYN="N";

						if (EnableShiftYN.equalsIgnoreCase("Y")) {
							sqlQFile += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and a.strPOSCode='"
									+ strPOSCode
									+ "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "'"
									+ " AND a.intShiftCode = '"
									+ strShiftNo
									+ "'  "
									+ " Group By left(right(a.dteDateCreated,8),2)";

							sqlLiveTables += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode"
									+ " and a.strPOSCode='"
									+ strPOSCode
									+ "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " AND a.intShiftCode = '"
									+ strShiftNo
									+ "' "
									+ " Group By left(right(a.dteDateCreated,8),2)";
						} else {
							sqlQFile += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "and a.strPOSCode='"
									+ strPOSCode
									+ "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " Group By left(right(a.dteDateCreated,8),2)";

							sqlLiveTables += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "and a.strPOSCode='"
									+ strPOSCode
									+ "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "'"
									+ " Group By left(right(a.dteDateCreated,8),2)";
						}
					} else {
						/*JSONObject JSONEnableShiftYN = new clsSetupDao()
								.funGetParameterValuePOSWise(strUserCode,
										LoginPOSCode, "gAreaWisePricing");
										*/
						// String
						// EnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
						EnableShiftYN="N";
						if (EnableShiftYN.equalsIgnoreCase("Y")) {
							sqlQFile += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " AND a.intShiftCode = '"
									+ strShiftNo
									+ "'  "
									+ " Group By left(right(a.dteDateCreated,8),2)";

							sqlLiveTables += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " AND a.intShiftCode = '"
									+ strShiftNo
									+ "' "
									+ " Group By left(right(a.dteDateCreated,8),2)";
						} else {
							sqlQFile += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " Group By left(right(a.dteDateCreated,8),2)";

							sqlLiveTables += " WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '"
									+ fromDate
									+ "' AND '"
									+ toDate
									+ "' "
									+ " Group By left(right(a.dteDateCreated,8),2)";
						}
					}

					mapPOSHourlyWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryHourWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlLiveTables.toString());
					List listHourWiseSales = queryHourWiseSales.list();
					funGenerateHourlyWiseSales(listHourWiseSales);

					queryHourWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlQFile.toString());
					listHourWiseSales = queryHourWiseSales.list();
					funGenerateHourlyWiseSales(listHourWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorHour = mapPOSHourlyWiseSales
							.entrySet().iterator();
					while (posIteratorHour.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = posIteratorHour
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> hrsIterator = mapHrlyDtl
								.entrySet().iterator();
						while (hrsIterator.hasNext()) {
							clsPOSCommonBeanDtl objHrsDtl = hrsIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objHrsDtl.getStartHrs() + "-"
									+ objHrsDtl.getEndHrs());
							objsales.setStrField2(String.valueOf(objHrsDtl
									.getNoOfBills()));
							objsales.setStrField3(String.valueOf(objHrsDtl
									.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String
									.valueOf(objHrsDtl.getSaleAmount())));
							// totalSale+=objHrsDtl.getSaleAmount();
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(objHrsDtl.getStartHrs() + "-"
									+ objHrsDtl.getEndHrs());
							DataList.add(objHrsDtl.getNoOfBills());
							DataList.add(objHrsDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;

						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListHourWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("SalesAmt", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double
									.parseDouble(listmap.get(2).toString()) / Double
									.parseDouble(temp1.toString())) * 100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			// //

			case "AreaWise":
				// jColHeaderArr.add("POS");
				// jColHeaderArr.add("Area Name");
				// jColHeaderArr.add("sales Amount");
				// colCount=3;
				try {

					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}
					sqlQFile = "";
					sqlQFile = "select d.strPosName,c.strAreaName,'0', SUM(b.dblSettlementAmt),'"
							+ LoginPOSCode
							+ "' "
							+ " ,'"
							+ strUserCode
							+ "','0','ND','ND',a.strPosCode,a.strAreaCode "
							+ " from tblqbillhd a,tblqbillsettlementdtl b,tblareamaster c,tblposmaster d "
							+ " where "
							+ field
							+ " between '"
							+ fromDate
							+ "' and '"
							+ toDate
							+ "' "
							+ " and a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ " and a.strAreaCode=c.strAreaCode "
							+ " and a.strPOSCode=d.strPosCode ";
					sqlLive = "";
					sqlLive = "select d.strPosName,c.strAreaName,'0', SUM(b.dblSettlementAmt),'"
							+ LoginPOSCode
							+ "' "
							+ " ,'"
							+ strUserCode
							+ "','0','ND','ND',a.strPosCode,a.strAreaCode "
							+ " from tblbillhd a,tblbillsettlementdtl b,tblareamaster c,tblposmaster d "
							+ " where "
							+ field
							+ " between '"
							+ fromDate
							+ "' and '"
							+ toDate
							+ "' "
							+ " and a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ " and a.strAreaCode=c.strAreaCode and a.strPOSCode=d.strPosCode ";

					if (!strPOSCode.equals("All")) {
						sqlQFile += " and a.strPOSCode = '" + strPOSCode + "' ";
						sqlLive += " and  a.strPOSCode = '" + strPOSCode + "' ";
					}

					sqlQFile += " AND a.intShiftCode = '" + strShiftNo + "' ";
					sqlLive += " AND a.intShiftCode = '" + strShiftNo + "' ";
					sqlQFile += " group by a.strAreaCode ";
					sqlLive += " group by a.strAreaCode ";

					mapPOSAreaWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryAreaWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlLive.toString());
					List listAreaWiseSales = queryAreaWiseSales.list();
					funGenerateAreaWiseSales(listAreaWiseSales);
					queryAreaWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlQFile.toString());
					listAreaWiseSales = queryAreaWiseSales.list();
					funGenerateAreaWiseSales(listAreaWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorArea = mapPOSAreaWiseSales
							.entrySet().iterator();
					while (posIteratorArea.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = posIteratorArea
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> areaIterator = mapAreaDtl
								.entrySet().iterator();
						while (areaIterator.hasNext()) {
							clsPOSCommonBeanDtl objAreaDtl = areaIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objAreaDtl.getPosName());
							objsales.setStrField2(objAreaDtl.getAreaName());
							objsales.setStrField3(String.valueOf(objAreaDtl
									.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String
									.valueOf(objAreaDtl.getSaleAmount())));
							// totalSale+=objAreaDtl.getSaleAmount();
							arrListSalesReport.add(objsales);

							// List DataList=new ArrayList<>();
							// DataList.add(objAreaDtl.getPosName());
							// DataList.add(objAreaDtl.getAreaName());
							// DataList.add(objAreaDtl.getSaleAmount());
							// map.put(rowCount,DataList );
							// rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListAreaWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("SalesAmt", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "DayWiseSales":
				jColHeaderArr.add("Bill date");
				jColHeaderArr.add("No Of Bills");
				jColHeaderArr.add("Sub Total ");
				jColHeaderArr.add("Discount");
				jColHeaderArr.add("Tax Amount");
				jColHeaderArr.add("Grand Amount");
				colCount = 6;

				StringBuilder sbSqlForDiscount = new StringBuilder();
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");
				double totalDiscount = 0,
				totAmount = 0;
				totalSubTotalDWise = 0;
				totalTaxAmt = 0;
				int totalNoOfBills = 0;

				try {

					sbSql.setLength(0);
					sbSql.append("select  DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),count(a.strBillNo),sum(a.dblSubTotal)"
							+ ",sum(a.dblDiscountAmt),sum(a.dblTaxAmt),'"
							+ strUserCode
							+ "',date(a.dteBillDate) "
							+ " from tblbillhd a "
							+ " where date(a.dteBillDate) between '"
							+ fromDate
							+ "' and '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "'");
					}

					sbSql.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					sbSql.append(" group by date(a.dteBillDate)");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryDayWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSql.toString());
					List listDayWise = queryDayWiseSales.list();

					if (listDayWise.size() > 0) {
						for (int i = 0; i < listDayWise.size(); i++) {
							Object[] obj = (Object[]) listDayWise.get(i);
							double settlementAmt = 0;
							sbSqlForDiscount.setLength(0);
							sbSqlForDiscount
									.append("select sum(b.dblSettlementAmt) "
											+ " from tblbillhd a, tblbillsettlementdtl b "
											+ " where a.strBillNo=b.strBillNo "
											+ " and date(a.dteBillDate) = '"
											+ obj[6].toString() + "' ");
							if (!strPOSCode.equals("All")) {
								sbSqlForDiscount.append(" and a.strPOSCode='"
										+ strPOSCode + "'");
							}

							sbSqlForDiscount.append(" AND a.intShiftCode = '"
									+ strShiftNo + "' ");
							sbSqlForDiscount
									.append(" group by date(a.dteBillDate)");

							Query queryDayWiseSalesSettlementAmt = webPOSSessionFactory
									.getCurrentSession().createSQLQuery(
											sbSqlForDiscount.toString());
							List listDayWiseSt = queryDayWiseSalesSettlementAmt
									.list();

							if (listDayWiseSt.size() > 0) {
								// Object[] obj1 = (Object[])
								// listDayWiseSt.get(0);
								settlementAmt = Double
										.parseDouble(listDayWiseSt.get(0)
												.toString());

							}
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString()); // day
							objsales.setStrField2(obj[1].toString()); // noOfBills
							objsales.setStrField3(obj[2].toString()); // subTotal
							objsales.setStrField4(obj[3].toString()); // disc
							objsales.setStrField5(obj[4].toString()); // tax
							objsales.setStrField6(String.valueOf(settlementAmt));// sales

							totalNoOfBills = totalNoOfBills
									+ Integer.parseInt(obj[1].toString());
							totalSubTotalDWise = totalSubTotalDWise
									+ Double.parseDouble(obj[2].toString());
							totalDiscount = totalDiscount
									+ Double.parseDouble(obj[3].toString());
							totalTaxAmt = totalTaxAmt
									+ Double.parseDouble(obj[4].toString());
							totAmount = totAmount + settlementAmt;
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(settlementAmt);
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					sbSql.setLength(0);
					sbSql.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),count(a.strBillNo),sum(a.dblSubTotal)"
							+ ",sum(a.dblDiscountAmt),sum(a.dblTaxAmt),'"
							+ strUserCode
							+ "',date(a.dteBillDate) "
							+ " from tblqbillhd a "
							+ " where date(a.dteBillDate) between '"
							+ fromDate
							+ "' and '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "'");
					}
					sbSql.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					sbSql.append(" group by date(a.dteBillDate)");

					queryDayWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSql.toString());
					listDayWise = queryDayWiseSales.list();

					if (listDayWise.size() > 0) {
						for (int i = 0; i < listDayWise.size(); i++) {
							Object[] obj = (Object[]) listDayWise.get(i);
							double settlementAmt = 0;
							sbSqlForDiscount.setLength(0);
							sbSqlForDiscount
									.append("select sum(b.dblSettlementAmt) "
											+ " from tblqbillhd a, tblqbillsettlementdtl b "
											+ " where a.strBillNo=b.strBillNo "
											+ " and date(a.dteBillDate) = '"
											+ obj[6].toString() + "' ");
							if (!strPOSCode.equals("All")) {
								sbSqlForDiscount.append(" and a.strPOSCode='"
										+ strPOSCode + "'");
							}
							sbSqlForDiscount.append(" AND a.intShiftCode = '"
									+ strShiftNo + "' ");

							sbSqlForDiscount
									.append(" group by date(a.dteBillDate)");

							Query queryDayWiseSalesSettlementAmt = webPOSSessionFactory
									.getCurrentSession().createSQLQuery(
											sbSqlForDiscount.toString());
							List listDayWiseSt = queryDayWiseSalesSettlementAmt
									.list();

							if (listDayWiseSt.size() > 0) {
								settlementAmt = Double
										.parseDouble(listDayWiseSt.get(0)
												.toString());

							}
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString()); // day
							objsales.setStrField2(obj[1].toString()); // noOfBills
							objsales.setStrField3(obj[2].toString()); // subTotal
							objsales.setStrField4(obj[3].toString()); // disc
							objsales.setStrField5(obj[4].toString()); // tax
							objsales.setStrField6(String.valueOf(settlementAmt));// sales

							totalNoOfBills = totalNoOfBills
									+ Integer.parseInt(obj[1].toString());
							totalSubTotalDWise = totalSubTotalDWise
									+ Double.parseDouble(obj[2].toString());
							totalDiscount = totalDiscount
									+ Double.parseDouble(obj[3].toString());
							totalTaxAmt = totalTaxAmt
									+ Double.parseDouble(obj[4].toString());
							totAmount = totAmount + settlementAmt;
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(settlementAmt);
							map.put(rowCount, DataList);
							rowCount++;
						}

					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListDayWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalNoOfBills", totalNoOfBills);
						jOBjRet.put("totalSubTotal", totalSubTotalDWise);
						jOBjRet.put("totalDiscount", totalDiscount);
						jOBjRet.put("totalTaxAmt", totalTaxAmt);
						jOBjRet.put("totAmount", totAmount);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TaxWiseSales":
				jColHeaderArr.add("Bill No");
				jColHeaderArr.add("Bill date");
				jColHeaderArr.add("Tax Code");
				jColHeaderArr.add("Tax Name ");
				jColHeaderArr.add("Tax Percentage");
				jColHeaderArr.add("Taxable Amount");
				jColHeaderArr.add("Tax Amount");
				colCount = 7;
				try {
					String prevBillNo = "";
					double totalTax = 0, totalTaxableAmt = 0;
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);

					sbSqlLive
							.append("select a.strBillNo,date(a.dteBillDate),c.strTaxCode"
									+ " ,c.strTaxDesc,b.dblTaxableAmount,b.dblTaxAmount,c.dblPercent"
									+ " ,'"
									+ strUserCode
									+ "' "
									+ " from tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
									+ " where a.strBillNo=b.strBillNo  and b.strTaxCode=c.strTaxCode "
									+ " and a.strClientCode=b.strClientCode and b.strClientCode=c.strClientCode "
									+ " and date(a.dteBillDate) between '"
									+ fromDate + "' and '" + toDate + "' ");

					sbSqlQFile
							.append("select a.strBillNo,date(a.dteBillDate),c.strTaxCode"
									+ " ,c.strTaxDesc,b.dblTaxableAmount,b.dblTaxAmount,c.dblPercent "
									+ " ,'"
									+ strUserCode
									+ "' "
									+ " from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c "
									+ " where a.strBillNo=b.strBillNo  and b.strTaxCode=c.strTaxCode "
									+ " and a.strClientCode=b.strClientCode and b.strClientCode=c.strClientCode "
									+ " and date(a.dteBillDate) between '"
									+ fromDate + "' and '" + toDate + "' ");

					if (!strPOSCode.equals("All")) {
						sbSqlLive.append(" and a.strPOSCode='" + strPOSCode
								+ "' ");
						sbSqlQFile.append(" and a.strPOSCode='" + strPOSCode
								+ "' ");
					}
					sbSqlLive.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");
					sbSqlQFile.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");

					sbSqlLive.append(" order by a.strBillNo desc");
					sbSqlQFile.append(" order by a.strBillNo desc");

					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryTaxWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listTaxWise = queryTaxWiseSales.list();

					if (listTaxWise.size() > 0) {
						for (int i = 0; i < listTaxWise.size(); i++) {
							Object[] obj = (Object[]) listTaxWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString());
							objsales.setStrField2(obj[1].toString());
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[6].toString());
							objsales.setStrField6(obj[4].toString());
							objsales.setStrField7(obj[5].toString());
							totalTax = totalTax
									+ Double.parseDouble(obj[5].toString());
							if (!prevBillNo.equals(obj[0].toString())) {
								totalTaxableAmt = totalTaxableAmt
										+ Double.parseDouble(obj[5].toString());
							}
							prevBillNo = obj[0].toString();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());

							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					// for day end
					queryTaxWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listTaxWise = queryTaxWiseSales.list();

					if (listTaxWise.size() > 0) {
						for (int i = 0; i < listTaxWise.size(); i++) {
							Object[] obj = (Object[]) listTaxWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString());
							objsales.setStrField2(obj[1].toString());
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[6].toString());
							objsales.setStrField6(obj[4].toString());
							objsales.setStrField7(obj[5].toString());
							totalTax = totalTax
									+ Double.parseDouble(obj[5].toString());
							if (!prevBillNo.equals(obj[0].toString())) {
								totalTaxableAmt = totalTaxableAmt
										+ Double.parseDouble(obj[5].toString());
							}
							prevBillNo = obj[0].toString();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());

							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListTaxWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalTaxableAmt", totalTaxableAmt);
						jOBjRet.put("totalTax", totalTax);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TipReport":
				jColHeaderArr.add("Bill No");
				jColHeaderArr.add("date");
				jColHeaderArr.add("Bill Time");
				jColHeaderArr.add("POS Code");
				jColHeaderArr.add("Set Mode ");
				jColHeaderArr.add("Discount %");
				jColHeaderArr.add("Disc Amount");
				jColHeaderArr.add("Sub Total");
				jColHeaderArr.add("Tax Amount");
				jColHeaderArr.add("Tip Amount");
				jColHeaderArr.add("Sales Amount");
				colCount = 11;
				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					Disc = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					double tipAmountTotal = 0;

					sql = "select strBillNo,left(dteBillDate,10),left(right(dteDateCreated,8),5) as "
							+ "BillTime,strPOSCode,strSettelmentMode,dblDiscountPer,dblDiscountAmt,dblTaxAmt,"
							+ "dblSubTotal,dblTipAmount,dblGrandTotal,strUserCreated,strUserEdited,dteDateCreated,"
							+ "dteDateEdited,strClientCode,strTableNo,strWaiterNo,strCustomerCode from vqbillhd ";
					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sql += "where " + field + " between '" + fromDate
								+ "' and '" + toDate + "' and strPOSCode='"
								+ strPOSCode + "' and strUserCreated='"
								+ strOperator + "' and dblTipAmount>0";
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sql += "where " + field + " between '" + fromDate
								+ "' and  strUserCreated='" + strOperator
								+ "' and dblTipAmount>0";
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sql += " where " + field + " between '" + fromDate
								+ "' and '" + toDate + "' and strPOSCode='"
								+ strPOSCode + "' and dblTipAmount>0";
					} else if (strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sql += "where " + field + " between '" + fromDate
								+ "' and '" + toDate + "'  and dblTipAmount>0";
					}

					sql += " AND intShiftCode = '" + strShiftNo + "' ";
					if (strFromBill.length() == 0
							&& strToBill.trim().length() == 0) {
						sql += " order by strBillNo desc";
					} else {
						sql += " and strBillNo between '" + strFromBill
								+ "' and '" + strToBill
								+ "' order by strBillNo desc";
					}

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryTipReport = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(sql);
					// Query queryTipWiseSales =
					// webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					List listTipWise = queryTipReport.list();

					if (listTipWise.size() > 0) {
						for (int i = 0; i < listTipWise.size(); i++) {
							Object[] obj = (Object[]) listTipWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString());
							String tempBillDate = obj[1].toString();
							String[] spDate = tempBillDate.split("-");
							objsales.setStrField2(spDate[2] + "-" + spDate[1]
									+ "-" + spDate[0]);
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[4].toString());
							objsales.setStrField6(obj[5].toString());
							objsales.setStrField7(obj[6].toString());
							objsales.setStrField8(obj[8].toString());
							objsales.setStrField9(obj[7].toString());
							objsales.setStrField10(obj[9].toString());
							objsales.setStrField11(obj[10].toString());

							Disc = Disc.add(new BigDecimal(obj[6].toString()));
							temp = temp.add(new BigDecimal(obj[7].toString()));
							subTotal = subTotal
									+ Double.parseDouble(obj[8].toString());
							temp1 = temp1
									.add(new BigDecimal(obj[10].toString()));
							tipAmountTotal = tipAmountTotal
									+ Double.parseDouble(obj[9].toString());

							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(spDate[2] + "-" + spDate[1] + "-"
									+ spDate[0]);
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[7].toString());
							DataList.add(obj[8].toString());
							DataList.add(obj[9].toString());
							DataList.add(obj[10].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListTipWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("Disc", Disc);
						jOBjRet.put("totalTaxAmt", temp);
						jOBjRet.put("subTotal", subTotal);
						jOBjRet.put("SalesAmount", temp1);
						jOBjRet.put("tipAmountTotal", tipAmountTotal);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemModifierWise":
				jColHeaderArr.add("Modifier Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("Sales Amount");
				colCount = 4;
				try {

					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbSqlFilters.setLength(0);

					sbSqlLiveBill
							.append("SELECT b.strModifierCode, b.strModifierName"
									+ " ,c.strPOSName, sum( b.dblQuantity ), sum( b.dblAmount )"
									+ ",'"
									+ strUserCode
									+ "',a.strposcode "
									+ " FROM tblbillhd a, tblbillmodifierdtl b, tblposmaster c "
									+ " WHERE a.strbillno = b.strbillno and a.strClientCode=b.strClientCode and a.strposcode=c.strposcode "
									+ " and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'");

					sbSqlQFileBill
							.append("SELECT b.strModifierCode, b.strModifierName"
									+ " ,c.strPOSName, sum( b.dblQuantity ), sum( b.dblAmount ) "
									+ ",'"
									+ strUserCode
									+ "',a.strposcode "
									+ " FROM tblqbillhd a, tblqbillmodifierdtl b, tblposmaster c "
									+ " WHERE a.strbillno = b.strbillno and a.strClientCode=b.strClientCode and a.strposcode=c.strposcode "
									+ " and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' and a.strUserCreated='"
								+ strOperator.toString() + "' ");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strPOSCode = '"
								+ strPOSCode + "' ");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append(" AND a.strUserCreated='"
								+ strOperator + "' ");
					}

					sbSqlFilters.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");

					if (strFromBill.length() == 0
							&& strToBill.trim().length() == 0) {
					} else {
						sbSqlFilters.append(" and a.strbillno between '"
								+ strFromBill + "' and '" + strToBill + "'");
					}
					sbSqlFilters
							.append(" GROUP BY a.strposcode, b.strModifierCode, b.strModifierName ");

					sbSqlLiveBill.append(sbSqlFilters);
					sbSqlQFileBill.append(sbSqlFilters);

					mapPOSModifierWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryModWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLiveBill.toString());
					List listModWiseSales = queryModWiseSales.list();
					funGenerateModifierWiseSales(listModWiseSales);

					queryModWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFileBill.toString());
					listModWiseSales = queryModWiseSales.list();
					funGenerateModifierWiseSales(listModWiseSales);

					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorMod = mapPOSModifierWiseSales
							.entrySet().iterator();
					while (posIteratorMod.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = posIteratorMod
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> modiIterator = mapModiDtl
								.entrySet().iterator();
						while (modiIterator.hasNext()) {
							clsPOSCommonBeanDtl objModiDtl = modiIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objModiDtl.getModiName());
							objsales.setStrField2(objModiDtl.getPosName());
							objsales.setStrField3(String.valueOf(objModiDtl
									.getQty()));
							objsales.setStrField4(String.valueOf(objModiDtl
									.getSaleAmount()));

							totalQty = totalQty + objModiDtl.getQty();
							temp1 = new BigDecimal(objModiDtl.getSaleAmount());
							totalAmount = totalAmount.add(temp1);
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objModiDtl.getModiName());
							DataList.add(objModiDtl.getPosName());
							DataList.add(objModiDtl.getQty());
							DataList.add(objModiDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListModWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("totalAmount", totalAmount);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MenuHeadWiseWithModifier":
				jColHeaderArr.add("Menu Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("Sales Amount");
				colCount = 4;
				try {
					// StringBuilder sbSql = new StringBuilder();
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					sbSql.setLength(0);
					sbSql.append("select count(*) from vqbillhd where date(dteBillDate) between '"
							+ fromDate + "' and '" + toDate + "'   ");

					sbSql.append(" AND intShiftCode = '" + strShiftNo + "' ");
					Query queryMenuHeadModSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSql.toString());
					List listMenuHeadModWiseSales = queryMenuHeadModSales
							.list();
					int cnt = 0;
					if (listMenuHeadModWiseSales.size() > 0) {
						// System.out.println(listMenuHeadModWiseSales.get(0));
						cnt = Integer.parseInt(listMenuHeadModWiseSales.get(0)
								.toString());
						// System.out.println(cnt);
					}

					if (cnt > 0) {
						sbSql.setLength(0);
						sbSql.append("select d.strMenuName,e.strPosName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscountAmt),sum(b.dblAmount),d.strMenuCode "
								+ " from tblbillhd a,tblbilldtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
								+ " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strMenuCode=d.strMenuCode "
								+ " and a.strPOSCode=e.strPosCode and a.strPOSCode=c.strPosCode ");
						if (AreaWisePricing.equals("Y")) {
							sbSql.append(" and a.strAreaCode=c.strAreaCode ");
						}
						if (!strPOSCode.equals("All")
								&& !strOperator.equals("All")) {
							sbSql.append(" AND a.strPOSCode = '" + strPOSCode
									+ "' and a.strUserCreated='" + strOperator
									+ "'");
						} else if (!strPOSCode.equals("All")
								&& strOperator.equals("All")) {
							sbSql.append(" AND a.strPOSCode = '" + strPOSCode
									+ "'");
						} else if (strPOSCode.equals("All")
								&& !strOperator.equals("All")) {
							sbSql.append(" and a.strUserCreated='"
									+ strOperator + "'");
						}

						sbSql.append(" AND a.intShiftCode = '" + strShiftNo
								+ "' ");

						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {
							sbSql.append(" and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						} else {
							sbSql.append(" and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate
									+ "' AND a.strBillNo between '"
									+ strFromBill + "' and '" + strToBill + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						}
						queryMenuHeadModSales = webPOSSessionFactory
								.getCurrentSession().createSQLQuery(
										sbSql.toString());
						listMenuHeadModWiseSales = queryMenuHeadModSales.list();
						// System.out.println(sbSql.toString());
						if (listMenuHeadModWiseSales.size() > 0) {
							for (int i = 0; i < listMenuHeadModWiseSales.size(); i++) {
								Object[] obj = (Object[]) listMenuHeadModWiseSales
										.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								StringBuilder sbSqlMod = new StringBuilder();
								sbSqlMod.setLength(0);
								sbSqlMod.append(" select d.strMenuName,e.strPosName,sum(b.dblQuantity) "
										+ " ,sum(b.dblAmount)-sum(b.dblDiscAmt),sum(b.dblAmount) "
										+ " from tblbillhd a,tblbillmodifierdtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and left(b.strItemCode,7)=c.strItemCode "
										+ " and c.strMenuCode=d.strMenuCode and a.strPOSCode=e.strPosCode "
										+ " and a.strPOSCode=c.strPosCode  and a.strAreaCode=c.strAreaCode "
										+ " and b.dblAmount>0 and c.strMenuCode='"
										+ obj[5].toString()
										+ "' "
										+ " and date(a.dteBillDate) BETWEEN '"
										+ fromDate + "' and '" + toDate + "' ");

								sbSqlMod.append(" AND a.intShiftCode = '"
										+ strShiftNo + "' ");
								// System.out.println(sbSqlMod.toString());
								Query qModifier = webPOSSessionFactory
										.getCurrentSession().createSQLQuery(
												sbSqlMod.toString());
								List listModifier = qModifier.list();

								// ResultSet rsModifier =
								// clsGlobalVarClass.dbMysql.executeResultSet(sbSqlMod.toString());
								double temp_Modifier_Qty = 0.00;
								double temp_Modifier_Amt = 0.00;
								if (listModifier.size() > 0) {
									for (int k = 0; k < listModifier.size(); k++) {

										Object[] ob = (Object[]) listMenuHeadModWiseSales
												.get(k);
										temp_Modifier_Qty += Double
												.parseDouble(ob[2].toString());
										temp_Modifier_Amt += Double
												.parseDouble(ob[3].toString());
									}
								}
								objsales.setStrField1(obj[0].toString());
								objsales.setStrField2(obj[1].toString());
								objsales.setStrField3(String.valueOf((Double
										.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								objsales.setStrField4(String.valueOf(new BigDecimal(
										obj[3].toString()).add(new BigDecimal(
										String.valueOf(temp_Modifier_Amt)))));

								temp_Modifier_Qty = 0.00;
								temp_Modifier_Amt = 0.00;
								totalQty = totalQty
										+ new Double(
												String.valueOf((Double
														.parseDouble(obj[2]
																.toString()) + temp_Modifier_Qty)));
								temp1 = temp1
										.add(new BigDecimal(
												String.valueOf(new BigDecimal(
														obj[3].toString()).add(new BigDecimal(
														String.valueOf(temp_Modifier_Amt))))));

								arrListSalesReport.add(objsales);
								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								map.put(rowCount, DataList);
								rowCount++;
							}

						}
						sbSql.setLength(0);
						sbSql.append("select d.strMenuName,e.strPosName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscountAmt)"
								+ " ,sum(b.dblAmount),d.strMenuCode "
								+ " from tblqbillhd a,tblqbilldtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
								+ " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strMenuCode=d.strMenuCode "
								+ " and a.strPOSCode=e.strPosCode and a.strPOSCode=c.strPosCode ");
						if (AreaWisePricing.equals("Y")) {
							sbSql.append(" and a.strAreaCode=c.strAreaCode ");
						}
						if (!strPOSCode.equals("All")
								&& !strOperator.equals("All")) {
							sbSql.append(" AND a.strPOSCode = '" + strPOSCode
									+ "' and a.strUserCreated='" + strOperator
									+ "'");
						} else if (!strPOSCode.equals("All")
								&& strOperator.equals("All")) {
							sbSql.append(" AND a.strPOSCode = '" + strPOSCode
									+ "'");
						} else if (strPOSCode.equals("All")
								&& !strOperator.equals("All")) {
							sbSql.append(" and a.strUserCreated='"
									+ strOperator + "'");
						}

						sbSql.append(" AND a.intShiftCode = '" + strShiftNo
								+ "' ");

						if (strFromBill.length() == 0
								&& strToBill.trim().length() == 0) {

							sbSql.append(" and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						} else {
							sbSql.append(" and date( a.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' "
									+ " AND a.strBillNo between '"
									+ strFromBill + "' and '" + strToBill + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						}

						// System.out.println(sbSql.toString());
						queryMenuHeadModSales = webPOSSessionFactory
								.getCurrentSession().createSQLQuery(
										sbSql.toString());
						listMenuHeadModWiseSales = queryMenuHeadModSales.list();
						// System.out.println(sbSql.toString());
						if (listMenuHeadModWiseSales.size() > 0) {
							for (int i = 0; i < listMenuHeadModWiseSales.size(); i++) {
								Object[] obj = (Object[]) listMenuHeadModWiseSales
										.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

								StringBuilder sbSqlMod = new StringBuilder();
								sbSqlMod.setLength(0);
								sbSqlMod.append(" select d.strMenuName,e.strPosName,sum(b.dblQuantity) "
										+ " ,sum(b.dblAmount)-sum(b.dblDiscAmt),sum(b.dblAmount) "
										+ " from tblqbillhd a,tblqbillmodifierdtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and left(b.strItemCode,7)=c.strItemCode "
										+ " and c.strMenuCode=d.strMenuCode and a.strPOSCode=e.strPosCode "
										+ " and a.strPOSCode=c.strPosCode and a.strAreaCode=c.strAreaCode "
										+ " and b.dblAmount>0 and c.strMenuCode='"
										+ obj[5].toString()
										+ "' "
										+ " and date(a.dteBillDate) BETWEEN '"
										+ fromDate + "' and '" + toDate + "' ");

								sbSqlMod.append(" AND a.intShiftCode = '"
										+ strShiftNo + "' ");
								// System.out.println(sbSqlMod.toString());

								Query qModifier = webPOSSessionFactory
										.getCurrentSession().createSQLQuery(
												sbSqlMod.toString());
								List listModifier = qModifier.list();

								double temp_Modifier_Qty = 0.00;
								double temp_Modifier_Amt = 0.00;

								if (listModifier.size() > 0) {
									for (int k = 0; k < listModifier.size(); k++) {

										Object[] ob = (Object[]) listMenuHeadModWiseSales
												.get(k);
										temp_Modifier_Qty += Double
												.parseDouble(ob[2].toString());
										temp_Modifier_Amt += Double
												.parseDouble(ob[3].toString());
									}
								}
								objsales.setStrField1(obj[0].toString());
								objsales.setStrField2(obj[1].toString());
								objsales.setStrField3(String.valueOf((Double
										.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								objsales.setStrField4(String.valueOf(new BigDecimal(
										obj[3].toString()).add(new BigDecimal(
										String.valueOf(temp_Modifier_Amt)))));

								temp_Modifier_Qty = 0.00;
								temp_Modifier_Amt = 0.00;
								totalQty = totalQty
										+ new Double(
												String.valueOf((Double
														.parseDouble(obj[2]
																.toString()) + temp_Modifier_Qty)));
								temp1 = temp1
										.add(new BigDecimal(
												String.valueOf(new BigDecimal(
														obj[3].toString()).add(new BigDecimal(
														String.valueOf(temp_Modifier_Amt))))));

								arrListSalesReport.add(objsales);
								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								map.put(rowCount, DataList);
								rowCount++;

							}

						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListMenuHeadModWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalQty", totalQty);
						jOBjRet.put("totalAmount", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemHourlyWise":
				jColHeaderArr.add("Time Range");
				jColHeaderArr.add("Item Name");
				jColHeaderArr.add("Quantity");
				jColHeaderArr.add("Item Amount");
				jColHeaderArr.add("Discount");
				colCount = 5;
				try {

					StringBuilder sbSqlModLiveBill = new StringBuilder();
					StringBuilder sbSqlQModFileBill = new StringBuilder();
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbSqlModLiveBill.setLength(0);
					sbSqlQModFileBill.setLength(0);

					sbSqlLiveBill
							.append("select left(right(b.dteDateCreated,8),2)"
									+ " ,left(right(b.dteDateCreated,8),2)+1,a.strItemName,sum(a.dblQuantity),"
									+ " sum(a.dblAmount)-sum(a.dblDiscountAmt) as Total,"
									+ " sum(a.dblDiscountAmt) as Discount,'"
									+ strUserCode
									+ "' "
									+ " from tblbilldtl a,tblbillhd b,tblitemmaster c "
									+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strItemCode=c.strItemCode "
									+ " and date(b.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and strPOSCode='" + strPOSCode
								+ "' ");
					}

					sbSqlLiveBill.append(" AND b.intShiftCode = '" + strShiftNo
							+ "' ");
					sbSqlLiveBill.append(" group by a.strItemName");

					sbSqlQFileBill
							.append("select left(right(b.dteDateCreated,8),2)"
									+ " ,left(right(b.dteDateCreated,8),2)+1,a.strItemName,sum(a.dblQuantity),"
									+ " sum(a.dblAmount)-sum(a.dblDiscountAmt) as Total,"
									+ " sum(a.dblDiscountAmt) as Discount,'"
									+ strUserCode
									+ "' "
									+ " from tblqbilldtl a,tblqbillhd b,tblitemmaster c "
									+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strItemCode=c.strItemCode "
									+ " and date(b.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlQFileBill.append(" and strPOSCode='" + strPOSCode
								+ "' ");
					}

					sbSqlQFileBill.append(" AND b.intShiftCode = '"
							+ strShiftNo + "' ");
					sbSqlQFileBill.append(" group by a.strItemName");

					sbSqlModLiveBill
							.append("select left(right(b.dteDateCreated,8),2)"
									+ " , left(right(b.dteDateCreated,8),2)+1,a.strModifierName,sum(a.dblQuantity),"
									+ " sum(a.dblAmount)-sum(a.dblDiscAmt) as Total,"
									+ " sum(a.dblDiscAmt) as Discount,'"
									+ strUserCode
									+ "' "
									+ " from tblbillmodifierdtl a,tblbillhd b,tblitemmaster c "
									+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and Left(a.strItemCode,7)=c.strItemCode "
									+ " and date(b.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlModLiveBill.append(" and strPOSCode='"
								+ strPOSCode + "' ");
					}

					sbSqlModLiveBill.append(" AND b.intShiftCode = '"
							+ strShiftNo + "' ");
					sbSqlModLiveBill.append(" group by a.strModifierName");

					sbSqlQModFileBill
							.append("select left(right(b.dteDateCreated,8),2)"
									+ " , left(right(b.dteDateCreated,8),2)+1,a.strModifierName,sum(a.dblQuantity),"
									+ " sum(a.dblAmount)-sum(a.dblDiscAmt) as Total,"
									+ " sum(a.dblDiscAmt) as Discount,'"
									+ strUserCode
									+ "' "
									+ " from tblqbillmodifierdtl a,tblqbillhd b,tblitemmaster c "
									+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and Left(a.strItemCode,7)=c.strItemCode "
									+ " and date(b.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlQModFileBill.append(" and strPOSCode='"
								+ strPOSCode + "' ");
					}

					sbSqlQModFileBill.append(" AND b.intShiftCode = '"
							+ strShiftNo + "' ");
					sbSqlQModFileBill.append(" group by a.strModifierName");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryItemHourlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLiveBill.toString());
					List listItemHourlyWiseSales = queryItemHourlyWiseSales
							.list();
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales
									.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-"
									+ obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1
									.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-"
									+ obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					queryItemHourlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFileBill.toString());
					listItemHourlyWiseSales = queryItemHourlyWiseSales.list();
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales
									.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-"
									+ obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1
									.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-"
									+ obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					queryItemHourlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlModLiveBill.toString());
					listItemHourlyWiseSales = queryItemHourlyWiseSales.list();
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales
									.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-"
									+ obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1
									.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-"
									+ obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					queryItemHourlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQModFileBill.toString());
					listItemHourlyWiseSales = queryItemHourlyWiseSales.list();
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales
									.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-"
									+ obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1
									.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-"
									+ obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListItemHourlyWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalAmount", temp);
						jOBjRet.put("totalDisc", temp1);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "OperatorWise":
				jColHeaderArr.add("Operator Code");
				jColHeaderArr.add("Operator  Name");
				jColHeaderArr.add("POS");
				jColHeaderArr.add("Payment Mode");
				jColHeaderArr.add("Discount Amount");
				jColHeaderArr.add("Sales Amount");
				colCount = 6;
				mapOperatorDtls = new HashMap<String, List<clsPOSOperatorDtl>>();
				StringBuilder sbSqlDisLive = new StringBuilder();
				StringBuilder sbSqlQDisFile = new StringBuilder();

				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbSqlDisLive.setLength(0);
					sbSqlQDisFile.setLength(0);
					sbSqlFilters.setLength(0);
					sbSqlDisFilters.setLength(0);

					sbSqlLive
							.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
									+ " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
									+ " FROM tbluserhd a "
									+ " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
									+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
									+ " inner join tblbillsettlementdtl d on b.strBillNo=d.strBillNo and b.strClientCode=d.strClientCode "
									+ " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");

					sbSqlQFile
							.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
									+ " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
									+ " FROM tbluserhd a "
									+ " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
									+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
									+ " inner join tblqbillsettlementdtl d on b.strBillNo=d.strBillNo and b.strClientCode=d.strClientCode "
									+ " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "' and b.strUserCreated='"
								+ strOperator + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "'");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlFilters.append("  and b.strUserCreated='"
								+ strOperator + "'");
					}

					sbSqlFilters.append(" AND b.intShiftCode = '" + strShiftNo
							+ "' ");
					sbSqlFilters
							.append(" GROUP BY a.strUserCode, b.strPosCode, d.strSettlementCode");

					sbSqlLive.append(sbSqlFilters);
					sbSqlQFile.append(sbSqlFilters);

					Map<String, Map<String, clsPOSOperatorDtl>> hmOperatorWiseSales = new HashMap<String, Map<String, clsPOSOperatorDtl>>();
					Map<String, clsPOSOperatorDtl> hmSettlementDtl = null;
					clsPOSOperatorDtl objOperatorWiseSales = null;

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryOperatorWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlLive.toString());
					List listOperatorWiseSales = queryOperatorWiseSales.list();
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {

							Object[] obj = (Object[]) listOperatorWiseSales
									.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0]
									.toString())) {
								hmSettlementDtl = hmOperatorWiseSales
										.get(obj[0].toString());
								if (hmSettlementDtl.containsKey(obj[7]
										.toString())) {
									objOperatorWiseSales = hmSettlementDtl
											.get(obj[7].toString());
									objOperatorWiseSales
											.setSettleAmt(objOperatorWiseSales
													.getSettleAmt()
													+ Double.parseDouble(obj[4]
															.toString()));
								} else {
									objOperatorWiseSales = new clsPOSOperatorDtl();
									objOperatorWiseSales.setStrUserCode(obj[0]
											.toString());
									objOperatorWiseSales.setStrUserName(obj[1]
											.toString());
									objOperatorWiseSales.setStrPOSName(obj[2]
											.toString());
									objOperatorWiseSales
											.setStrSettlementDesc(obj[3]
													.toString());
									objOperatorWiseSales.setSettleAmt(Double
											.parseDouble(obj[4].toString()));
									objOperatorWiseSales.setStrPOSCode(obj[6]
											.toString());
									objOperatorWiseSales.setDiscountAmt(0);
								}
								hmSettlementDtl.put(obj[7].toString(),
										objOperatorWiseSales);
							} else {
								objOperatorWiseSales = new clsPOSOperatorDtl();
								objOperatorWiseSales.setStrUserCode(obj[0]
										.toString());
								objOperatorWiseSales.setStrUserName(obj[1]
										.toString());
								objOperatorWiseSales.setStrPOSName(obj[2]
										.toString());
								objOperatorWiseSales
										.setStrSettlementDesc(obj[3].toString());
								objOperatorWiseSales.setSettleAmt(Double
										.parseDouble(obj[4].toString()));
								objOperatorWiseSales.setStrPOSCode(obj[6]
										.toString());
								objOperatorWiseSales.setDiscountAmt(0);

								hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
								hmSettlementDtl.put(obj[7].toString(),
										objOperatorWiseSales);
							}
							hmOperatorWiseSales.put(obj[0].toString(),
									hmSettlementDtl);
						}

					}
					queryOperatorWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQFile.toString());
					listOperatorWiseSales = queryOperatorWiseSales.list();
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {

							Object[] obj = (Object[]) listOperatorWiseSales
									.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0]
									.toString())) {
								hmSettlementDtl = hmOperatorWiseSales
										.get(obj[0].toString());
								if (hmSettlementDtl.containsKey(obj[7]
										.toString())) {
									objOperatorWiseSales = hmSettlementDtl
											.get(obj[7].toString());
									objOperatorWiseSales
											.setSettleAmt(objOperatorWiseSales
													.getSettleAmt()
													+ Double.parseDouble(obj[4]
															.toString()));
								} else {
									objOperatorWiseSales = new clsPOSOperatorDtl();
									objOperatorWiseSales.setStrUserCode(obj[0]
											.toString());
									objOperatorWiseSales.setStrUserName(obj[1]
											.toString());
									objOperatorWiseSales.setStrPOSName(obj[2]
											.toString());
									objOperatorWiseSales
											.setStrSettlementDesc(obj[3]
													.toString());
									objOperatorWiseSales.setSettleAmt(Double
											.parseDouble(obj[4].toString()));
									objOperatorWiseSales.setStrPOSCode(obj[6]
											.toString());
									objOperatorWiseSales.setDiscountAmt(0);
								}
								hmSettlementDtl.put(obj[7].toString(),
										objOperatorWiseSales);
							} else {
								objOperatorWiseSales = new clsPOSOperatorDtl();
								objOperatorWiseSales.setStrUserCode(obj[0]
										.toString());
								objOperatorWiseSales.setStrUserName(obj[1]
										.toString());
								objOperatorWiseSales.setStrPOSName(obj[2]
										.toString());
								objOperatorWiseSales
										.setStrSettlementDesc(obj[3].toString());
								objOperatorWiseSales.setSettleAmt(Double
										.parseDouble(obj[4].toString()));
								objOperatorWiseSales.setStrPOSCode(obj[6]
										.toString());
								objOperatorWiseSales.setDiscountAmt(0);

								hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
								hmSettlementDtl.put(obj[7].toString(),
										objOperatorWiseSales);
							}
							hmOperatorWiseSales.put(obj[0].toString(),
									hmSettlementDtl);
						}
					}

					sbSqlDisLive
							.append("SELECT a.strUserCode, a.strUserName, c.strPOSName"
									+ " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode "
									+ " FROM tbluserhd a "
									+ " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
									+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");

					sbSqlQDisFile
							.append("  SELECT a.strUserCode, a.strUserName, c.strPOSName "
									+ " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode "
									+ " FROM tbluserhd a "
									+ " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
									+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlDisFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "' and b.strUserCreated='"
								+ strOperator + "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbSqlDisFilters.append(" AND b.strPOSCode = '"
								+ strPOSCode + "'");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbSqlDisFilters.append("  and b.strUserCreated='"
								+ strOperator.toString() + "'");
					}

					sbSqlDisFilters.append(" AND b.intShiftCode = '"
							+ strShiftNo + "' ");
					sbSqlDisFilters
							.append(" GROUP BY a.strUserCode, b.strPosCode");

					sbSqlDisLive.append(sbSqlDisFilters);
					sbSqlQDisFile.append(sbSqlDisFilters);

					queryOperatorWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlDisLive.toString());
					listOperatorWiseSales = queryOperatorWiseSales.list();
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {
							Object[] obj = (Object[]) listOperatorWiseSales
									.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0]
									.toString())) {
								hmSettlementDtl = hmOperatorWiseSales
										.get(obj[0].toString());
								Set<String> setKeys = hmSettlementDtl.keySet();
								for (String keys : setKeys) {
									objOperatorWiseSales = hmSettlementDtl
											.get(keys);
									objOperatorWiseSales
											.setDiscountAmt(objOperatorWiseSales
													.getDiscountAmt()
													+ Double.parseDouble(obj[3]
															.toString()));
									hmSettlementDtl.put(keys,
											objOperatorWiseSales);
									break;
								}
								hmOperatorWiseSales.put(obj[0].toString(),
										hmSettlementDtl);
							}
						}

					}
					queryOperatorWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sbSqlQDisFile.toString());
					listOperatorWiseSales = queryOperatorWiseSales.list();
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {
							Object[] obj = (Object[]) listOperatorWiseSales
									.get(k);

							if (hmOperatorWiseSales.containsKey(obj[0]
									.toString())) {
								hmSettlementDtl = hmOperatorWiseSales
										.get(obj[0].toString());
								Set<String> setKeys = hmSettlementDtl.keySet();
								for (String keys : setKeys) {
									objOperatorWiseSales = hmSettlementDtl
											.get(keys);
									objOperatorWiseSales
											.setDiscountAmt(objOperatorWiseSales
													.getDiscountAmt()
													+ Double.parseDouble(obj[3]
															.toString()));
									hmSettlementDtl.put(keys,
											objOperatorWiseSales);
									break;
								}
								hmOperatorWiseSales.put(obj[0].toString(),
										hmSettlementDtl);
							}
						}
					}

					double discAmt = 0, totalAmt = 0;
					// Object[] arrObjTableRowData=new Object[6];
					for (Map.Entry<String, Map<String, clsPOSOperatorDtl>> entry : hmOperatorWiseSales
							.entrySet()) {
						Map<String, clsPOSOperatorDtl> hmOpSettlementDtl = entry
								.getValue();
						for (Map.Entry<String, clsPOSOperatorDtl> entryOp : hmOpSettlementDtl
								.entrySet()) {
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							clsPOSOperatorDtl objOperatorDtl = entryOp.getValue();

							objsales.setStrField1(objOperatorDtl
									.getStrUserCode());// userCode
							objsales.setStrField2(objOperatorDtl
									.getStrUserName());// userName
							objsales.setStrField3(objOperatorDtl
									.getStrPOSName());// posName
							objsales.setStrField4(objOperatorDtl
									.getStrSettlementDesc());// payMode
							objsales.setStrField5(String.valueOf(objOperatorDtl
									.getDiscountAmt()));// disc
							objsales.setStrField6(String.valueOf(objOperatorDtl
									.getSettleAmt()));// saleAmt
							discAmt += objOperatorDtl.getDiscountAmt();
							totalAmt += objOperatorDtl.getSettleAmt();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objOperatorDtl.getStrUserCode());
							DataList.add(objOperatorDtl.getStrUserName());
							DataList.add(objOperatorDtl.getStrPOSName());
							DataList.add(objOperatorDtl.getStrSettlementDesc());
							DataList.add(objOperatorDtl.getDiscountAmt());
							DataList.add(objOperatorDtl.getSettleAmt());

							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListOperatorWiseSales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalAmount", totalAmt);
						jOBjRet.put("totalDisc", discAmt);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MonthlySalesFlash":
				jColHeaderArr.add(" Month");
				jColHeaderArr.add("Year");
				jColHeaderArr.add("Total Sales");
				colCount = 3;
				StringBuilder sqlLiveData = new StringBuilder();
				StringBuilder sqlQData = new StringBuilder();
				try {
					// 2016-11-5
					fromDate = fromDate.substring(5, 7);
					toDate = toDate.substring(5, 7);
					//Date objDate = new SimpleDateFormat("dd/MM/yyyy")
					//		.parse(fromDate);
					//fromDate = String.valueOf(objDate.getMonth() + 1);
					//objDate = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
					// toDate = String.valueOf(objDate.getMonth() + 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {

					sqlLiveData.setLength(0);
					sqlQData.setLength(0);
					sbFilters.setLength(0);

					sqlLiveData
							.append("SELECT c.strPOSName, MONTHNAME(DATE(a.dteBillDate)), YEAR(DATE(a.dteBillDate))"
									+ " ,sum(d.dblSettlementAmt),sum(a.dblGrandTotal),a.strPOSCode"
									+ " ,month(a.dteBillDate) "
									+ " FROM tblbillhd a,tblsettelmenthd b,tblposmaster c,tblbillsettlementdtl d "
									+ " WHERE d.strSettlementCode=b.strSettelmentCode AND a.strBillNo = d.strBillNo "
									+ " AND a.strPOSCode=c.strPOSCode and a.strClientCode=d.strClientCode "
									+ " AND MONTH(DATE(a.dteBillDate)) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' ");

					sqlQData.append("SELECT c.strPOSName, MONTHNAME(DATE(a.dteBillDate)), YEAR(DATE(a.dteBillDate))"
							+ " ,sum(d.dblSettlementAmt),sum(a.dblGrandTotal),a.strPOSCode"
							+ " ,month(a.dteBillDate) "
							+ " FROM tblqbillhd a,tblsettelmenthd b,tblposmaster c,tblqbillsettlementdtl d\n"
							+ " WHERE d.strSettlementCode=b.strSettelmentCode AND a.strBillNo = d.strBillNo "
							+ " AND a.strPOSCode=c.strPOSCode and a.strClientCode=d.strClientCode "
							+ " AND MONTH(DATE(a.dteBillDate)) BETWEEN '"
							+ fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode
								+ "' and a.strUserCreated='" + strOperator
								+ "'");
					} else if (!strPOSCode.equals("All")
							&& strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode
								+ "'");
					} else if (strPOSCode.equals("All")
							&& !strOperator.equals("All")) {
						sbFilters.append("  and a.strUserCreated='"
								+ strOperator + "'");
					}

					if (strFromBill.length() == 0
							&& strToBill.trim().length() == 0) {
					} else {
						sbFilters.append(" and a.strBillNo between '"
								+ strFromBill + "' and '" + strToBill + "'");
					}

					sbFilters.append(" AND a.intShiftCode = '" + strShiftNo
							+ "' ");
					sbFilters
							.append(" GROUP BY a.strPOSCode, MONTHNAME(DATE(a.dteBillDate)) ");

					sqlLiveData.append(" ").append(sbFilters);
					sqlQData.append(" ").append(sbFilters);

					mapPOSMonthWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Query queryMonthlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlLiveData.toString());
					List listMonthlyWiseSales = queryMonthlyWiseSales.list();

					Object[] arrObj = (Object[]) listMonthlyWiseSales.get(0);
					String a = arrObj[3].toString();
					System.out.println(Double.parseDouble(a));

					funGenerateMonthWiseSales(listMonthlyWiseSales);

					queryMonthlyWiseSales = webPOSSessionFactory
							.getCurrentSession().createSQLQuery(
									sqlQData.toString());
					listMonthlyWiseSales = queryMonthlyWiseSales.list();
					funGenerateMonthWiseSales(listMonthlyWiseSales);

					Double total = 0.0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorMonth = mapPOSMonthWiseSales
							.entrySet().iterator();
					while (posIteratorMonth.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = posIteratorMonth
								.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> monthIterator = mapMonthDtl
								.entrySet().iterator();
						while (monthIterator.hasNext()) {
							clsPOSCommonBeanDtl objMonthDtl = monthIterator.next()
									.getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objMonthDtl.getMonthName());// Monthname
							objsales.setStrField2(objMonthDtl.getYear());// year
							objsales.setStrField3(String.valueOf(objMonthDtl
									.getSaleAmount()));// totalamt
							total += objMonthDtl.getSaleAmount();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objMonthDtl.getMonthName());
							DataList.add(objMonthDtl.getYear());
							DataList.add(objMonthDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(
								arrListSalesReport, type);
						jOBjRet.put("ListMonthlySales",
								gsonarrTempListSalesReport);
						jOBjRet.put("totalSale", total);
						jOBjRet.put("ColHeader", jColHeaderArr);
						jOBjRet.put("colCount", colCount);
						jOBjRet.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							jOBjRet.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			/*
			 * default: throw new
			 * IllegalArgumentException("  report name not match");
			 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jOBjRet;

	}
	
	
	
	
	private void funGenerateItemWiseSales(List list, String fromDate,
			String toDate, String strPOSCode, String strShiftNo,
			String strUserCode, String field, String strPayMode,
			String strOperator, String strFromBill, String strToBill,
			String reportType, String Type, String Customer,
			String ConsolidatePOS, String ReportName) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String itemCode = obj[0].toString();// itemCode
					String itemName = obj[1].toString();// itemName
					String posName = obj[2].toString();// posName
					double qty = Double.parseDouble(obj[3].toString());// qty
					double salesAmt = Double.parseDouble(obj[7].toString());// salesAmount
					double subTotal = Double.parseDouble(obj[5].toString());// subTotal
					double discAmt = Double.parseDouble(obj[8].toString());// discount
					String date = obj[9].toString();// date
					String posCode = obj[10].toString();// posCode

					String compare = itemCode;
					if (itemCode.contains("M")) {
						compare = itemName;
					} else {
						compare = itemCode;
					}

					if (mapPOSItemDtl.containsKey(posCode)) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = mapPOSItemDtl
								.get(posCode);
						if (mapItemDtl.containsKey(compare)) {
							clsPOSBillItemDtl objItemDtl = mapItemDtl.get(compare);
							objItemDtl.setQuantity(objItemDtl.getQuantity()
									+ qty);
							objItemDtl.setAmount(objItemDtl.getAmount()
									+ salesAmt);
							objItemDtl.setSubTotal(objItemDtl.getSubTotal()
									+ subTotal);
							objItemDtl.setDiscountAmount(objItemDtl
									.getDiscountAmount() + discAmt);
						} else {
							clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(
									date, itemCode, itemName, qty, salesAmt,
									discAmt, posName, subTotal);
							mapItemDtl.put(compare, objItemDtl);
						}
					} else {
						Map<String, clsPOSBillItemDtl> mapItemDtl = new LinkedHashMap<>();
						clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(date,
								itemCode, itemName, qty, salesAmt, discAmt,
								posName, subTotal);
						mapItemDtl.put(compare, objItemDtl);
						mapPOSItemDtl.put(posCode, mapItemDtl);
					}

					if (!itemCode.contains("M")) {
						funCreateModifierQuery(itemCode, fromDate, toDate,
								strPOSCode, strShiftNo, strUserCode, field,
								strPayMode, strOperator, strFromBill,
								strToBill, reportType, Type, Customer,
								ConsolidatePOS, ReportName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void funCreateModifierQuery(String itemCode, String fromDate,
			String toDate, String strPOSCode, String strShiftNo,
			String strUserCode, String field, String strPayMode,
			String strOperator, String strFromBill, String strToBill,
			String reportType, String Type, String Customer,
			String ConsolidatePOS, String ReportName) {
		try {
			 String sqlModLive = "select a.strItemCode,a.strModifierName,c.strPOSName"
	                    + ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
	                    + ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
	                    + "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + "and left(a.strItemCode,7)='" + itemCode + "' "
	                    + "and a.dblAmount>0 ";

			// String pos = funGetSelectedPosCode();
			String sqlFilters = "";
			if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
				sqlFilters += " AND b.strPOSCode = '" + strPOSCode
						+ "' and b.strUserCreated='" + strOperator + "' ";
			} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
				sqlFilters += " AND b.strPOSCode = '" + strPOSCode + "' ";
			} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
				sqlFilters += " AND b.strUserCreated='"
						+ strOperator.toString() + "' ";
			}
			if (strFromBill.length() == 0 && strToBill.length() == 0) {

			} else {
				sqlFilters += " and a.strbillno between '" + strFromBill + "' "
						+ " and '" + strToBill + "'";
			}

			//sqlFilters += " AND b.intShiftCode = '" + strShiftNo + "' ";

			sqlFilters += " group by a.strItemCode,a.strModifierName,c.strPOSName  "
                    + " order by b.dteBillDate ";

			sqlModLive = sqlModLive + " " + sqlFilters;

			Query queryModLive = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sqlModLive.toString());
			List listModLive = queryModLive.list();
			funGenerateItemWiseSales(listModLive, fromDate, toDate, strPOSCode,
					strShiftNo, strUserCode, field, strPayMode, strOperator,
					strFromBill, strToBill, reportType, Type, Customer,
					ConsolidatePOS, ReportName);

			/*
			 * ResultSet
			 * rs=clsGlobalVarClass.dbMysql.executeResultSet(sqlModLive
			 * .toString()); funGenerateItemWiseSales(rs);
			 */

			// qmodifiers
			String sqlModQFile = "select a.strItemCode,a.strModifierName,c.strPOSName"
	                    + ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
	                    + ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
	                    + "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + "and left(a.strItemCode,7)='" + itemCode + "' "
	                    + "and a.dblAmount>0  ";

			sqlModQFile = sqlModQFile + " " + sqlFilters;

			Query queryModLiveQ = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sqlModQFile.toString());
			List listModLiveQ = queryModLiveQ.list();
			funGenerateItemWiseSales(listModLiveQ, fromDate, toDate,
					strPOSCode, strShiftNo, strUserCode, field, strPayMode,
					strOperator, strFromBill, strToBill, reportType, Type,
					Customer, ConsolidatePOS, ReportName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void funGenerateSettlementWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[0].toString();
					String posName = obj[2].toString();
					String settlementCode = obj[1].toString();
					String settlementDesc = obj[3].toString();
					double settlementAmt = Double
							.parseDouble(obj[4].toString());
					String settlementType = obj[6].toString();

					if (mapPOSDtlForSettlement.containsKey(posCode)) {
						List<Map<String, clsPOSBillSettlementDtl>> listOfSettlement = mapPOSDtlForSettlement
								.get(posCode);
						boolean isSettlementExists = false;
						int settlementIndex = 0;
						for (int j = 0; j < listOfSettlement.size(); j++) {
							if (listOfSettlement.get(j).containsKey(
									settlementCode)) {
								isSettlementExists = true;
								settlementIndex = j;
								break;
							}
						}
						if (isSettlementExists) {
							Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = listOfSettlement
									.get(settlementIndex);
							clsPOSBillSettlementDtl objBillSettlementDtl = mapSettlementCodeDtl
									.get(settlementCode);
							objBillSettlementDtl
									.setStrSettlementCode(settlementCode);
							objBillSettlementDtl
									.setDblSettlementAmt(objBillSettlementDtl
											.getDblSettlementAmt()
											+ settlementAmt);
							objBillSettlementDtl.setPosName(posName);
							TotSale = TotSale + settlementAmt;
						} else {
							Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
							clsPOSBillSettlementDtl objBillSettlementDtl = new clsPOSBillSettlementDtl(
									settlementCode, settlementDesc,
									settlementAmt, posName, settlementType);
							mapSettlementCodeDtl.put(settlementCode,
									objBillSettlementDtl);
							listOfSettlement.add(mapSettlementCodeDtl);
							TotSale = TotSale + settlementAmt;
						}
					} else {
						List<Map<String, clsPOSBillSettlementDtl>> listOfSettelment = new ArrayList<>();
						Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
						clsPOSBillSettlementDtl objBillSettlementDtl = new clsPOSBillSettlementDtl(
								settlementCode, settlementDesc, settlementAmt,
								posName, settlementType);
						mapSettlementCodeDtl.put(settlementCode,
								objBillSettlementDtl);
						listOfSettelment.add(mapSettlementCodeDtl);
						TotSale = TotSale + settlementAmt;
						mapPOSDtlForSettlement.put(posCode, listOfSettelment);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	private void funGenerateMenuHeadWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[9].toString();// posCode
					String posName = obj[4].toString();// posName
					String menuCode = obj[0].toString();// menuCode
					String menuName = obj[1].toString();// menuName
					double qty = Double.parseDouble(obj[2].toString());// qty
					double salesAmt = Double.parseDouble(obj[7].toString());// salesAmt
					double subTotal = Double.parseDouble(obj[3].toString());// subTotal
					double discAmt = Double.parseDouble(obj[8].toString());// disc

					if (mapPOSMenuHeadDtl.containsKey(posCode)) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = mapPOSMenuHeadDtl
								.get(posCode);
						if (mapItemDtl.containsKey(menuCode)) {
							clsPOSBillItemDtl objItemDtl = mapItemDtl
									.get(menuCode);
							objItemDtl.setQuantity(objItemDtl.getQuantity()
									+ qty);
							objItemDtl.setAmount(objItemDtl.getAmount()
									+ salesAmt);
							objItemDtl.setSubTotal(objItemDtl.getSubTotal()
									+ subTotal);
							objItemDtl.setDiscountAmount(objItemDtl
									.getDiscountAmount() + discAmt);
						} else {
							clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(qty,
									salesAmt, discAmt, posName, subTotal,
									menuCode, menuName);
							mapItemDtl.put(menuCode, objItemDtl);
						}
					} else {
						Map<String, clsPOSBillItemDtl> mapItemDtl = new LinkedHashMap<>();
						clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(qty,
								salesAmt, discAmt, posName, subTotal, menuCode,
								menuName);
						mapItemDtl.put(menuCode, objItemDtl);
						mapPOSMenuHeadDtl.put(posCode, mapItemDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateGroupWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					if (mapPOSDtlForGroupSubGroup
							.containsKey(obj[9].toString()))// posCode
					{
						String posCode = obj[9].toString();
						String groupCode = obj[0].toString();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = mapPOSDtlForGroupSubGroup
								.get(posCode);
						boolean isGroupExists = false;
						int groupIndex = 0;
						for (int j = 0; j < listOfGroup.size(); j++) {
							if (listOfGroup.get(j).containsKey(groupCode)) {
								isGroupExists = true;
								groupIndex = j;
								break;
							}
						}
						if (isGroupExists) {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = listOfGroup
									.get(groupIndex);
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroupCodeDtl
									.get(groupCode);
							objGroupCodeDtl.setGroupCode(obj[0].toString());
							objGroupCodeDtl.setGroupName(obj[1].toString());
							objGroupCodeDtl.setPosName(obj[4].toString());
							objGroupCodeDtl.setQty(objGroupCodeDtl.getQty()
									+ Double.parseDouble(obj[2].toString()));
							objGroupCodeDtl.setSubTotal(objGroupCodeDtl
									.getSubTotal()
									+ Double.parseDouble(obj[3].toString()));
							objGroupCodeDtl.setSalesAmt(objGroupCodeDtl
									.getSalesAmt()
									+ Double.parseDouble(obj[7].toString()));
							objGroupCodeDtl.setDiscAmt(objGroupCodeDtl
									.getDiscAmt()
									+ Double.parseDouble(obj[8].toString()));
							objGroupCodeDtl.setGrandTotal(objGroupCodeDtl
									.getGrandTotal()
									+ Double.parseDouble(obj[10].toString()));
						} else {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
									obj[0].toString(), obj[1].toString(),
									obj[4].toString(),
									Double.parseDouble(obj[2].toString()),
									Double.parseDouble(obj[3].toString()),
									Double.parseDouble(obj[7].toString()),
									Double.parseDouble(obj[8].toString()),
									Double.parseDouble(obj[10].toString()));
							mapGroupCodeDtl.put(obj[0].toString(),
									objGroupCodeDtl);
							listOfGroup.add(mapGroupCodeDtl);
						}
					} else {
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroupDtl = new ArrayList<>();
						Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
						clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
								obj[0].toString(), obj[1].toString(),
								obj[4].toString(), Double.parseDouble(obj[2]
										.toString()), Double.parseDouble(obj[3]
										.toString()), Double.parseDouble(obj[7]
										.toString()), Double.parseDouble(obj[8]
										.toString()),
								Double.parseDouble(obj[10].toString()));
						mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
						listOfGroupDtl.add(mapGroupCodeDtl);
						mapPOSDtlForGroupSubGroup.put(obj[9].toString(),
								listOfGroupDtl);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateSubGroupWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					if (mapPOSDtlForGroupSubGroup
							.containsKey(obj[9].toString()))// posCode
					{
						String posCode = obj[9].toString();
						String groupCode = obj[0].toString();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = mapPOSDtlForGroupSubGroup
								.get(posCode);

						boolean isGroupExists = false;
						int groupIndex = 0;
						for (int j = 0; j < listOfGroup.size(); j++) {
							if (listOfGroup.get(j).containsKey(groupCode)) {
								isGroupExists = true;
								groupIndex = j;
								break;
							}
						}
						if (isGroupExists) {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = listOfGroup
									.get(groupIndex);
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroupCodeDtl
									.get(groupCode);
							objGroupCodeDtl.setGroupCode(obj[0].toString());
							objGroupCodeDtl.setGroupName(obj[1].toString());
							objGroupCodeDtl.setPosName(obj[4].toString());
							objGroupCodeDtl.setQty(objGroupCodeDtl.getQty()
									+ Double.parseDouble(obj[2].toString()));
							objGroupCodeDtl.setSubTotal(objGroupCodeDtl
									.getSubTotal()
									+ Double.parseDouble(obj[3].toString()));
							objGroupCodeDtl.setSalesAmt(objGroupCodeDtl
									.getSalesAmt()
									+ Double.parseDouble(obj[7].toString()));
							objGroupCodeDtl.setDiscAmt(objGroupCodeDtl
									.getDiscAmt()
									+ Double.parseDouble(obj[8].toString()));
							objGroupCodeDtl.setGrandTotal(objGroupCodeDtl
									.getGrandTotal() + 0.00);
						} else {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
									obj[0].toString(), obj[1].toString(),
									obj[4].toString(),
									Double.parseDouble(obj[2].toString()),
									Double.parseDouble(obj[3].toString()),
									Double.parseDouble(obj[7].toString()),
									Double.parseDouble(obj[8].toString()), 0.00);
							mapGroupCodeDtl.put(obj[0].toString(),
									objGroupCodeDtl);
							listOfGroup.add(mapGroupCodeDtl);
						}
					} else {
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroupDtl = new ArrayList<>();
						Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
						clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
								obj[0].toString(), obj[1].toString(),
								obj[4].toString(), Double.parseDouble(obj[2]
										.toString()), Double.parseDouble(obj[3]
										.toString()), Double.parseDouble(obj[7]
										.toString()), Double.parseDouble(obj[8]
										.toString()), 0.00);
						mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
						listOfGroupDtl.add(mapGroupCodeDtl);
						mapPOSDtlForGroupSubGroup.put(obj[9].toString(),
								listOfGroupDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateWaiterWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String waiterCode = obj[5].toString();// waiterNo
					String waiterShortName = obj[1].toString();// waiterShortName
					String waiterFullName = obj[2].toString();// waiterFullName
					String posCode = obj[6].toString();// posCode
					String posName = obj[0].toString();// posName
					double salesAmount = Double.parseDouble(obj[3].toString());// salesAmount

					if (mapPOSWaiterWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = mapPOSWaiterWiseSales
								.get(posCode);
						if (mapWaiterDtl.containsKey(waiterCode)) {
							clsPOSCommonBeanDtl objWaiterDtl = mapWaiterDtl
									.get(waiterCode);
							objWaiterDtl.setSaleAmount(objWaiterDtl
									.getSaleAmount() + salesAmount);
						} else {
							clsPOSCommonBeanDtl objWaiterDtl = new clsPOSCommonBeanDtl(posCode, posName, waiterCode,waiterShortName, waiterFullName,salesAmount);
							mapWaiterDtl.put(waiterCode, objWaiterDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objWaiterDtl = new clsPOSCommonBeanDtl(posCode, posName, waiterCode, waiterShortName,waiterFullName, salesAmount);
						mapWaiterDtl.put(waiterCode, objWaiterDtl);
						mapPOSWaiterWiseSales.put(posCode, mapWaiterDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateDelBoyWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String dbCode = obj[0].toString(); // dbCode
					String dbName = obj[1].toString();// dbName
					String posCode = obj[6].toString();// posCode
					String posName = obj[2].toString();// posName
					double salesAmount = Double.parseDouble(obj[3].toString());// salesAmount
					double delCharges = Double.parseDouble(obj[4].toString());// delCharges

					if (mapPOSDeliveryBoyWise.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = mapPOSDeliveryBoyWise
								.get(posCode);
						if (mapDBDtl.containsKey(dbCode)) {
							clsPOSCommonBeanDtl objDelBoyDtl = mapDBDtl
									.get(dbCode);
							objDelBoyDtl.setSaleAmount(objDelBoyDtl
									.getSaleAmount() + salesAmount);
							objDelBoyDtl.setDelCharges(objDelBoyDtl
									.getDelCharges() + delCharges);
						} else {
							clsPOSCommonBeanDtl objDBDtl = new clsPOSCommonBeanDtl(
									posCode, posName, salesAmount, dbCode,
									dbName, delCharges);
							mapDBDtl.put(dbCode, objDBDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objDBDtl = new clsPOSCommonBeanDtl(
								posCode, posName, salesAmount, dbCode, dbName,
								delCharges);
						mapDBDtl.put(dbCode, objDBDtl);
						mapPOSDeliveryBoyWise.put(posCode, mapDBDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateCostCenterWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String costCenterCode = obj[0].toString();// ccCode
					String costCenterName = obj[1].toString();// ccName
					String posCode = obj[9].toString();// posCode
					String posName = obj[4].toString();// posName
					double qty = Double.parseDouble(obj[2].toString());// qty
					double subTotal = Double.parseDouble(obj[3].toString());// subTotal
					double salesAmount = Double.parseDouble(obj[7].toString());// salesAmount
					double discAmt = Double.parseDouble(obj[8].toString());// disc

					if (mapPOSCostCenterWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = mapPOSCostCenterWiseSales
								.get(posCode);
						if (mapCCDtl.containsKey(costCenterCode)) {
							clsPOSCommonBeanDtl objCCDtl = mapCCDtl
									.get(costCenterCode);

							objCCDtl.setQty(objCCDtl.getQty() + qty);
							objCCDtl.setSubTotal(objCCDtl.getSubTotal()
									+ subTotal);
							objCCDtl.setSaleAmount(objCCDtl.getSaleAmount()
									+ salesAmount);
							objCCDtl.setDiscAmount(objCCDtl.getDiscAmount()
									+ discAmt);
						} else {
							clsPOSCommonBeanDtl objCCDtl = new clsPOSCommonBeanDtl(
									posCode, posName, qty, salesAmount,
									subTotal, costCenterCode, costCenterName,
									discAmt);
							mapCCDtl.put(costCenterCode, objCCDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objCCDtl = new clsPOSCommonBeanDtl(
								posCode, posName, qty, salesAmount, subTotal,
								costCenterCode, costCenterName, discAmt);
						mapCCDtl.put(costCenterCode, objCCDtl);

						mapPOSCostCenterWiseSales.put(posCode, mapCCDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateTableWiseSales(List list)

	{
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String posName = obj[0].toString();// posName
					String tableName = obj[1].toString();// tableName
					double saleAmount = Double.parseDouble(obj[3].toString());// salesAmount
					String posCode = obj[4].toString();// posCode
					String tableNo = obj[9].toString();// tableNo

					if (mapPOSTableWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = mapPOSTableWiseSales
								.get(posCode);
						if (mapTblDtl.containsKey(tableNo)) {
							clsPOSCommonBeanDtl objTblDtl = mapTblDtl.get(tableNo);

							objTblDtl.setSaleAmount(objTblDtl.getSaleAmount()
									+ saleAmount);
						} else {
							clsPOSCommonBeanDtl objTblDtl = new clsPOSCommonBeanDtl(
									posCode, posName, saleAmount, tableNo,
									tableName);
							mapTblDtl.put(tableNo, objTblDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objTblDtl = new clsPOSCommonBeanDtl(
								posCode, posName, saleAmount, tableNo,
								tableName);
						mapTblDtl.put(tableNo, objTblDtl);

						mapPOSTableWiseSales.put(posCode, mapTblDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateHourlyWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String startHour = obj[0].toString();// startHour
					String endHour = obj[1].toString();// endHour
					double noOfBills = Double.parseDouble(obj[2].toString());
					double saleAmount = Double.parseDouble(obj[3].toString());

					if (mapPOSHourlyWiseSales.containsKey(startHour)) {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = mapPOSHourlyWiseSales
								.get(startHour);
						if (mapHrlyDtl.containsKey(startHour)) {
							clsPOSCommonBeanDtl objHrlyDtl = mapHrlyDtl
									.get(startHour);

							objHrlyDtl.setNoOfBills(objHrlyDtl.getNoOfBills()
									+ noOfBills);
							objHrlyDtl.setSaleAmount(objHrlyDtl.getSaleAmount()
									+ saleAmount);
						} else {
							clsPOSCommonBeanDtl objHrlyDtl = new clsPOSCommonBeanDtl(
									saleAmount, startHour, endHour, noOfBills);
							mapHrlyDtl.put(startHour, objHrlyDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objHrlyDtl = new clsPOSCommonBeanDtl(
								saleAmount, startHour, endHour, noOfBills);
						mapHrlyDtl.put(startHour, objHrlyDtl);
						mapPOSHourlyWiseSales.put(startHour, mapHrlyDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateAreaWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[9].toString();// posCode
					String areaCode = obj[10].toString();// areaCode
					String posName = obj[0].toString();// posName
					String areaName = obj[1].toString();// areaName
					double saleAmount = Double.parseDouble(obj[3].toString());

					if (mapPOSAreaWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = mapPOSAreaWiseSales
								.get(posCode);
						if (mapAreaDtl.containsKey(areaCode)) {
							clsPOSCommonBeanDtl objAreaDtl = mapAreaDtl
									.get(areaCode);
							objAreaDtl.setSaleAmount(objAreaDtl.getSaleAmount()
									+ saleAmount);
						} else {
							clsPOSCommonBeanDtl objAreaDtl = new clsPOSCommonBeanDtl(
									posCode, posName, areaCode, areaName,
									saleAmount);
							mapAreaDtl.put(areaCode, objAreaDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objAreaDtl = new clsPOSCommonBeanDtl(
								posCode, posName, areaCode, areaName,
								saleAmount);
						mapAreaDtl.put(areaCode, objAreaDtl);
						mapPOSAreaWiseSales.put(posCode, mapAreaDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateModifierWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[6].toString();// posCode
					String posName = obj[2].toString();// posName
					String modiCode = obj[0].toString();// modiCode
					String modiName = obj[1].toString();// modiName
					double qty = Double.parseDouble(obj[3].toString());// qty
					double saleAmount = Double.parseDouble(obj[4].toString());// saleAmount

					if (mapPOSModifierWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = mapPOSModifierWiseSales
								.get(posCode);
						if (mapModiDtl.containsKey(modiName)) {
							clsPOSCommonBeanDtl objModiDtl = mapModiDtl
									.get(modiName);
							objModiDtl.setQty(objModiDtl.getQty() + qty);
							objModiDtl.setSaleAmount(objModiDtl.getSaleAmount()
									+ saleAmount);
						} else {
							clsPOSCommonBeanDtl objModiDtl = new clsPOSCommonBeanDtl(
									posCode, posName, qty, saleAmount,
									modiCode, modiName);
							mapModiDtl.put(modiName, objModiDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objModiDtl = new clsPOSCommonBeanDtl(
								posCode, posName, qty, saleAmount, modiCode,
								modiName);
						mapModiDtl.put(modiName, objModiDtl);
						mapPOSModifierWiseSales.put(posCode, mapModiDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateMonthWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String posCode = obj[5].toString();// posCode
					String posName = obj[0].toString(); // posName
					String monthCode = obj[6].toString();// monthCode
					String monthName = obj[1].toString();// monthName
					String year = obj[2].toString();// year
					double saleAmount = Double.parseDouble(obj[3].toString()); // saleAmount

					if (mapPOSMonthWiseSales.containsKey(year)) {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = mapPOSMonthWiseSales
								.get(year);
						if (mapMonthDtl.containsKey(monthCode)) {
							clsPOSCommonBeanDtl objMonthDtl = mapMonthDtl
									.get(monthCode);
							objMonthDtl.setSaleAmount(objMonthDtl
									.getSaleAmount() + saleAmount);
							mapMonthDtl.put(monthCode, objMonthDtl);
						} else {
							clsPOSCommonBeanDtl objMonthDtl = new clsPOSCommonBeanDtl(
									saleAmount, posCode, posName, monthCode,
									monthName, year);
							mapMonthDtl.put(monthCode, objMonthDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objMonthDtl = new clsPOSCommonBeanDtl(
								saleAmount, posCode, posName, monthCode,
								monthName, year);
						mapMonthDtl.put(monthCode, objMonthDtl);
						mapPOSMonthWiseSales.put(year, mapMonthDtl);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JSONObject funDayEndFlashReportDtl(String fromDate, String toDate,
			String strPOSCode) {
		List listRet = new ArrayList();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		JSONArray jArr = new JSONArray();
		JSONObject jOBjRet = new JSONObject();
		double sumtSale = 0.00, sumFloat = 0.00, sumCash = 0.00, sumAdvance = 0.00, sumTransferIn = 0.00, sumTotalReceipt = 0.00;
		double sumPay = 0.00, sumWithDrawal = 0.00, sumTransferOut = 0.00, sumTotalPay = 0.00, sumCashInhand = 0.00;
		double sumHdAmt = 0.00, sumDining = 0.00, sumTaleAway = 0.00, sumNoOfBill = 0.00, sumNoOfVoidedBill = 0.00, sumNoOfModifyBill = 0.00, sumRefund = 0.00;

		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);

		try {

			sbSqlLive
					.append("select b.strPOSName,DATE_FORMAT(date(a.dtePOSDate),'%d-%m-%Y'),dblHDAmt,dblDiningAmt,dblTakeAway,dblTotalSale,dblFloat"
							+ ",dblCash,dblAdvance,dblTransferIn,dblTotalReceipt,dblPayments,dblWithdrawal,dblTransferOut,dblRefund"
							+ ",dblTotalPay,dblCashInHand,dblNoOfBill,dblNoOfVoidedBill,dblNoOfModifyBill "
							+ " from tbldayendprocess a,tblposmaster b where a.strPOSCode=b.strPOSCode "
							+ "and date(a.dtePOSDate) between '"
							+ fromDate
							+ "' and '" + toDate + "'  ");

			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSqlFilters
						.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
			}

			sbSqlLive.append(sbSqlFilters);
			Query querySqlLive = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sbSqlLive.toString());
			List listSqlLive = querySqlLive.list();
			if (listSqlLive.size() > 0) {

				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					JSONObject jObj = new JSONObject();
					jObj.put("strPOSName", obj[0].toString());
					jObj.put("dtePOSDate", obj[1].toString());
					jObj.put("dblHDAmt", obj[2].toString());
					jObj.put("dblDiningAmt", obj[3].toString());
					jObj.put("dblTakeAway", obj[4].toString());
					jObj.put("dblTotalSale", obj[5].toString());
					jObj.put("dblFloat", obj[6].toString());
					jObj.put("dblCash", obj[7].toString());
					jObj.put("dblAdvance", obj[8].toString());
					jObj.put("dblTransferIn", obj[9].toString());
					jObj.put("dblTotalReceipt", obj[10].toString());
					jObj.put("dblPayments", obj[11].toString());
					jObj.put("dblWithdrawal", obj[12].toString());
					jObj.put("dblTransferOut", obj[13].toString());
					jObj.put("dblRefund", obj[14].toString());
					jObj.put("dblTotalPay", obj[15].toString());
					jObj.put("dblCashInHand", obj[16].toString());
					jObj.put("dblNoOfBill", obj[17].toString());
					jObj.put("dblNoOfVoidedBill", obj[18].toString());
					jObj.put("dblNoOfModifyBill", obj[19].toString());

					sumHdAmt += Double.parseDouble(obj[2].toString());
					sumDining += Double.parseDouble(obj[3].toString());
					sumTaleAway += Double.parseDouble(obj[4].toString());
					sumtSale += Double.parseDouble(obj[5].toString());
					sumFloat += Double.parseDouble(obj[6].toString());
					sumCash += Double.parseDouble(obj[7].toString());
					sumAdvance += Double.parseDouble(obj[8].toString());
					sumTransferIn += Double.parseDouble(obj[9].toString());
					sumTotalReceipt += Double.parseDouble(obj[10].toString());
					sumPay = sumPay += Double.parseDouble(obj[11].toString());
					sumWithDrawal += Double.parseDouble(obj[12].toString());
					sumTransferOut += Double.parseDouble(obj[13].toString());
					sumRefund += Double.parseDouble(obj[14].toString());
					sumTotalPay += Double.parseDouble(obj[15].toString());
					sumCashInhand += Double.parseDouble(obj[16].toString());
					sumNoOfBill += Double.parseDouble(obj[17].toString());
					sumNoOfVoidedBill += Double.parseDouble(obj[18].toString());
					sumNoOfModifyBill += Double.parseDouble(obj[19].toString());
					jArr.add(jObj);
				}
				// jOBjRet.put("jArr", jArr);
			}
			// listRet.add(listSqlLive);

			JSONObject jObjTatol = new JSONObject();

			jObjTatol.put("sumHdAmt", sumHdAmt);
			jObjTatol.put("sumDining", sumDining);
			jObjTatol.put("sumTaleAway", sumTaleAway);
			jObjTatol.put("sumtSale", sumtSale);
			jObjTatol.put("sumFloat", sumFloat);
			jObjTatol.put("sumCash", sumCash);
			jObjTatol.put("sumAdvance", sumAdvance);
			jObjTatol.put("sumTransferIn", sumTransferIn);
			jObjTatol.put("sumTotalReceipt", sumTotalReceipt);
			jObjTatol.put("sumPay", sumPay);
			jObjTatol.put("sumWithDrawal", sumWithDrawal);
			jObjTatol.put("sumTransferOut", sumTransferOut);
			jObjTatol.put("sumRefund", sumRefund);
			jObjTatol.put("sumTotalPay", sumTotalPay);
			jObjTatol.put("sumCashInhand", sumCashInhand);
			jObjTatol.put("sumNoOfBill", sumNoOfBill);
			jObjTatol.put("sumNoOfVoidedBill", sumNoOfVoidedBill);
			jObjTatol.put("sumNoOfModifyBill", sumNoOfModifyBill);

			jOBjRet.put("jArr", jArr);
			jOBjRet.put("jObjTatol", jObjTatol);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jOBjRet;

	}
	
	
	





}
