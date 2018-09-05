package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtl;
import com.sanguine.webpos.bean.clsPOSBillSeriesBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSSettlementDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.model.clsBillDiscDtlModel;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillHdModel_ID;
import com.sanguine.webpos.model.clsBillModifierDtlModel;
import com.sanguine.webpos.model.clsBillPromotionDtlModel;
import com.sanguine.webpos.model.clsBillSettlementDtlModel;
import com.sanguine.webpos.model.clsBillTaxDtl;
import com.sanguine.webpos.model.clsHomeDeliveryDtlModel;
import com.sanguine.webpos.model.clsHomeDeliveryHdModel;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSTextFileGenerator;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSBillingAPIController
{
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	intfBaseService objBaseService;

	@Autowired
	clsPOSUtilityController objUtility;

	@Autowired
	clsPOSTextFileGenerator objTextFileGeneration;

	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	private StringBuilder sqlBuilder = new StringBuilder();

	// JSONObject jsSettelementOptionsDtl = new JSONObject();
	// List listSettlementObject = new ArrayList<clsSettelementOptions>();
	// public static List<String> listSettelmentOptions;

	public JSONObject funGetItemPricingDtl(String clientCode, String posDate, String posCode)
	{
		JSONObject jObjTableData = new JSONObject();
		List list = null;
		String gAreaCodeForTrans = "",
				sql_ItemDtl;
		try
		{

			StringBuilder sqlBuilder = new StringBuilder();

			sqlBuilder.setLength(0);
			sqlBuilder.append("select strAreaCode from tblareamaster where strAreaName='All' ");

			list = objBaseService.funGetList(sqlBuilder, "sql");

			if (list.size() > 0)
			{
				gAreaCodeForTrans = (String) list.get(0);
			}

			String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
			if (gAreaWisePricing.equalsIgnoreCase("N"))
			{
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday," + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, " + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo," + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode ,b.strSubGroupCode,c.strGroupCode ,c.strSubGroupName,d.strGroupName" + " FROM tblmenuitempricingdtl a ,tblitemmaster b left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode " + " left outer join  tblgrouphd d  on c.strGroupCode= d.strGroupCode  " + " WHERE  a.strItemCode=b.strItemCode " + " and a.strAreaCode='" + gAreaCodeForTrans + "' " + " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') " + " and date(dteFromDate)<='" + posDate + "' and date(dteToDate)>='" + posDate + "' " + " ORDER BY b.strItemName ASC";
			}
			else
			{

				String gDirectAreaCode = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gDirectAreaCode");

				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday," + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, " + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo," + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode,b.strSubGroupCode,c.strGroupCode,c.strSubGroupName,d.strGroupName " + " FROM tblmenuitempricingdtl a ,tblitemmaster b left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode " + " left outer join  tblgrouphd d  on c.strGroupCode= d.strGroupCode  " + " WHERE a.strAreaCode='" + gDirectAreaCode + "' " + "  and a.strItemCode=b.strItemCode "
				// + "WHERE (a.strAreaCode='" + clsAreaCode + "') "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') " + " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' " + " ORDER BY b.strItemName ASC";
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append(sql_ItemDtl);

			list = objBaseService.funGetList(sqlBuilder, "sql");

			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String itemName = obj[1].toString();// .replace(" ", "&#x00A;");
					JSONObject objSettle = new JSONObject();
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", itemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());
					objSettle.put("strMenuCode", obj[20].toString());

					objSettle.put("strSubGroupCode", obj[21].toString());
					objSettle.put("strGroupcode", obj[22].toString());
					objSettle.put("strSubGroupName", obj[23].toString());
					objSettle.put("strGroupName", obj[24].toString());

					jArr.add(objSettle);
				}
			}
			jObjTableData.put("MenuItemPricingDtl", jArr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	public JSONObject funGetMenuHeads(String strPOSCode, String userCode)
	{
		LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		JSONObject jObjTableData = new JSONObject();
		List list = null;
		String strCounterWiseBilling = "";
		try
		{

			sqlBuilder.setLength(0);

			String sql = "select strCounterWiseBilling from tblposmaster";

			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");

			if (list.size() > 0)
				strCounterWiseBilling = (String) list.get(0);

			sql = "select strCounterCode from tblcounterhd " + " where strUserCode='" + userCode + "' ";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");

			String strCounterCode = "";
			if (list.size() > 0)
				strCounterCode = (String) list.get(0);

			if (strCounterWiseBilling.equalsIgnoreCase("Yes"))

			{

				sql = "select distinct(a.strMenuCode),b.strMenuName " + "from tblmenuitempricingdtl a left outer join tblmenuhd b on a.strMenuCode=b.strMenuCode " + "left outer join tblcounterdtl c on b.strMenuCode=c.strMenuCode " + "left outer join tblcounterhd d on c.strCounterCode=d.strCounterCode " + "where d.strOperational='Yes' " + "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') " + "and c.strCounterCode='" + strCounterCode + "' " + "order by b.intSequence";
			}
			else
			{
				sql = "select distinct(a.strMenuCode),b.strMenuName " + "from tblmenuitempricingdtl a left outer join tblmenuhd b " + "on a.strMenuCode=b.strMenuCode " + "where  b.strOperational='Y' " + "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') " + "order by b.intSequence";
			}
			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");

			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strMenuName = obj[1].toString();// .replace(" ", "&#x00A;");
					objSettle.put("strMenuCode", obj[0].toString());
					objSettle.put("strMenuName", strMenuName);
					jArr.add(objSettle);
				}
			}
			jObjTableData.put("MenuHeads", jArr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	@SuppressWarnings("finally")
	public JSONObject funGetButttonList(String transName, String posCode, String posClientCode)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select strButtonName from tblbuttonsequence where strTransactionName='" + transName + "' and (strPOSCode='All' or strPOSCode='" + posCode + "') and strClientCode='" + posClientCode + "' " + "  order by intSeqNo ";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			JSONArray jArrData = new JSONArray();

			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object obj = (Object) list.get(i);

					jArrData.add(obj.toString());
				}
			}
			jObjTableData.put("buttonList", jArrData);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	public JSONObject funPopularItem(String clientCode, String posDate, String strPOSCode)
	{

		JSONObject jObjTableData = new JSONObject();
		List list = null;
		String gAreaCodeForTrans = "";

		try
		{
			String sql = "select strAreaCode from tblareamaster where strAreaName='All'";

			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0)
				gAreaCodeForTrans = (String) list.get(0);

			String gDirectAreaCode = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, strPOSCode, "gDirectAreaCode");

			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday," + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, " + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo," + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable " + " FROM tblmenuitempricingdtl a ,tblitemmaster b " + " where a.strPopular='Y' and  a.strItemCode= b.strItemCode " + " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' " + " and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='All') " + " and (a.strAreaCode='" + gDirectAreaCode + "' or a.strAreaCode='" + gAreaCodeForTrans + "') ";

			sqlBuilder.setLength(0);
			sqlBuilder.append(sql);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString().replace(" ", "&#x00A;");
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", strItemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArr.add(objSettle);
				}
			}
			jObjTableData.put("PopularItems", jArr);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	public JSONObject funSettlementMode(String clientCode, String posCode, Boolean superUser)
	{
		JSONObject jsonOb = new JSONObject();

		try
		{
			jsonOb = funAddSettelementOptions(clientCode, posCode, superUser);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jsonOb;
	}

	public JSONObject funAddSettelementOptions(String clientCode, String posCode, Boolean superUser)
	{
		JSONObject jsonOb = new JSONObject();
		// hmSettelementOptionsDtl= new HashMap<>();
		List listSettelmentOptions = new ArrayList<>();
		Gson gson = new Gson();
		Type type = new TypeToken<clsPOSSettelementOptions>()
		{
		}.getType();

		String sqlSettlementModes = "";
		JSONObject jsSettelementOptionsDtl = new JSONObject();

		try
		{

			String gPickSettlementsFromPOSMaster = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPickSettlementsFromPOSMaster");

			String gEnablePMSIntegrationYN = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gEnablePMSIntegrationYN");

			if (gPickSettlementsFromPOSMaster.equals("Y"))
			{
				sqlSettlementModes = "select b.strSettelmentCode,b.strSettelmentDesc,b.strSettelmentType" + " ,b.dblConvertionRatio,b.strBillPrintOnSettlement " + " from tblpossettlementdtl a,tblsettelmenthd b " + " where a.strSettlementCode=b.strSettelmentCode and b.strApplicable='Yes' " + " and b.strBilling='Yes' and a.strPOSCode='" + posCode + "'";
			}
			else
			{
				sqlSettlementModes = "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio" + " ,strBillPrintOnSettlement " + " from tblsettelmenthd where strApplicable='Yes' and strBilling='Yes'";
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append(sqlSettlementModes);
			List listSettlement = objBaseService.funGetList(sqlBuilder, "sql");
			clsPOSSettelementOptions objSettl;
			String gsonSettlementObject = "";
			List listSettlementObject = new ArrayList<clsPOSSettelementOptions>();

			if (listSettlement.size() > 0)
			{

				for (int i = 0; i < listSettlement.size(); i++)
				{
					Object[] obj = (Object[]) listSettlement.get(i);
					List listSttleData = new ArrayList();

					if (gEnablePMSIntegrationYN.equals("Y"))
					{
						if (superUser)
						{
							listSettelmentOptions.add(obj[1].toString());
							objSettl = new clsPOSSettelementOptions(obj[0].toString(), obj[2].toString(), Double.parseDouble(obj[3].toString()), obj[1].toString(), obj[4].toString());
							listSettlementObject.add(objSettl);
							gsonSettlementObject = gson.toJson(objSettl, type);
							jsSettelementOptionsDtl.put(obj[1].toString(), gsonSettlementObject);
						}
						else
						{
							listSettelmentOptions.add(obj[1].toString());
							objSettl = new clsPOSSettelementOptions(obj[0].toString(), obj[2].toString(), Double.parseDouble(obj[3].toString()), obj[1].toString(), obj[4].toString());
							listSettlementObject.add(objSettl);
							gsonSettlementObject = gson.toJson(objSettl, type);
							jsSettelementOptionsDtl.put(obj[1].toString(), gsonSettlementObject);
						}
					}
					else
					{
						listSettelmentOptions.add(obj[1].toString());
						objSettl = new clsPOSSettelementOptions(obj[0].toString(), obj[2].toString(), Double.parseDouble(obj[3].toString()), obj[1].toString(), obj[4].toString());

						listSettlementObject.add(objSettl);
						gsonSettlementObject = gson.toJson(objSettl, type);
						jsSettelementOptionsDtl.put(obj[1].toString(), gsonSettlementObject);
					}
				}
			}

			List listSettelment = new ArrayList();

			int noOfSettlementMode = listSettelmentOptions.size();
			JSONArray jArrSettlMod = new JSONArray();
			for (int i = 0; i < noOfSettlementMode; i++)
			{
				listSettelment.add(listSettelmentOptions.get(i));
				jArrSettlMod.add(listSettelmentOptions.get(i));
			}
			jsonOb.put("SettleDesc", jArrSettlMod);
			jsonOb.put("SettleObj", jsSettelementOptionsDtl);

			Gson gson2 = new Gson();
			Type type2 = new TypeToken<List<clsPOSSettelementOptions>>()
			{
			}.getType();
			String gsonlistSettlementObject = gson2.toJson(listSettlementObject, type2);
			jsonOb.put("listSettleObj", gsonlistSettlementObject);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jsonOb;
		}
	}

	public JSONObject funLoadAllReasonMasterData(String clientCode)
	{
		JSONObject JObj = new JSONObject();
		JSONObject JObjModifyBill;
		JSONObject JObjComplementry;
		JSONObject JObjDiscount;
		JSONArray jArr = new JSONArray();
		try
		{

			String sqlModifyBill = "select strReasonCode,strReasonName from tblreasonmaster where strModifyBill='Y' and strClientCode='" + clientCode + "'";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sqlModifyBill);
			List list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0)
			{

				for (int i = 0; i < list.size(); i++)
				{
					Object[] ob = (Object[]) list.get(i);
					JObjModifyBill = new JSONObject();
					JObjModifyBill.put("strReasonCode", ob[0].toString());
					JObjModifyBill.put("strReasonName", ob[1].toString());

					jArr.add(JObjModifyBill);
				}
				JObj.put("ModifyBill", jArr);
			}
			jArr = new JSONArray();

			String sqlCmplementReason = "select strReasonCode,strReasonName from tblreasonmaster where strComplementary='Y' and strClientCode='" + clientCode + "'";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sqlCmplementReason);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0)
			{

				for (int i = 0; i < list.size(); i++)
				{
					Object[] ob = (Object[]) list.get(i);
					JObjComplementry = new JSONObject();
					JObjComplementry.put("strReasonCode", ob[0].toString());
					JObjComplementry.put("strReasonName", ob[1].toString());
					jArr.add(JObjComplementry);
				}
				JObj.put("Complementry", jArr);
			}
			jArr = new JSONArray();
			String sqlDiscount = "select strReasonCode,strReasonName from tblreasonmaster where strDiscount='Y' and strClientCode='" + clientCode + "'";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sqlDiscount);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0)
			{

				for (int i = 0; i < list.size(); i++)
				{
					Object[] ob = (Object[]) list.get(i);
					JObjDiscount = new JSONObject();
					JObjDiscount.put("strReasonCode", ob[0].toString());
					JObjDiscount.put("strReasonName", ob[1].toString());
					jArr.add(JObjDiscount);
				}
				JObj.put("Discount", jArr);
			}
			jArr = new JSONArray();
			String sqlReason = "select strReasonCode,strReasonName from tblreasonmaster where  strClientCode='" + clientCode + "'";
			sqlBuilder.setLength(0);
			sqlBuilder.append(sqlReason);
			list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0)
			{

				for (int i = 0; i < list.size(); i++)
				{
					Object[] ob = (Object[]) list.get(i);
					JObjDiscount = new JSONObject();
					JObjDiscount.put("strReasonCode", ob[0].toString());
					JObjDiscount.put("strReasonName", ob[1].toString());
					jArr.add(JObjDiscount);
				}
				JObj.put("AllReason", jArr);
			}

		}
		catch (Exception e)
		{

		}

		return JObj;
	}

	/**
	 * This method categorise the items based on bill series.
	 * 
	 * @param listOfItemDtl
	 * @param posCode
	 * @return
	 */
	public Map<String, List<clsPOSKOTItemDtl>> funGetBillSeriesList(List<clsPOSKOTItemDtl> listOfItemDtl, String posCode)
	{
		Map<String, List<clsPOSKOTItemDtl>> hmBillSeriesItemList = new HashMap<String, List<clsPOSKOTItemDtl>>();
		try
		{

			StringBuilder sqlBuilder = new StringBuilder();
			for (clsPOSKOTItemDtl objBillItemDtl : listOfItemDtl)
			{
				boolean isExistsBillSeries = false;

				sqlBuilder.setLength(0);
				sqlBuilder.append(" select * from tblbillseries where (strPOSCode='" + posCode + "' or strPOSCode='All') ");
				List listOfBillSeries = objBaseService.funGetList(sqlBuilder, "sql");
				if (listOfBillSeries != null && listOfBillSeries.size() > 0)
				{
					for (int i = 0; i < listOfBillSeries.size(); i++)
					{
						Object[] ob = (Object[]) listOfBillSeries.get(i);

						String billSeriesType = ob[1].toString();
						String billSeries = ob[2].toString();
						String billSeriesCodes = ob[4].toString();

						sqlBuilder.setLength(0);
						sqlBuilder.append("select a.strItemCode,a.strItemName,a.strRevenueHead,b.strPosCode,c.strMenuCode,c.strMenuName " + " ,d.strSubGroupCode,d.strSubGroupName,e.strGroupCode,e.strGroupName " + " from tblitemmaster a,tblmenuitempricingdtl b,tblmenuhd c,tblsubgrouphd d,tblgrouphd e " + " where a.strItemCode=b.strItemCode and b.strMenuCode=c.strMenuCode " + " and a.strSubGroupCode=d.strSubGroupCode and d.strGroupCode=e.strGroupCode ");
						sqlBuilder.append(" and (b.strPosCode='" + posCode + "' Or b.strPosCode='All') ");
						sqlBuilder.append(" and a.strItemCode='" + objBillItemDtl.getStrItemCode().substring(0, 7) + "' ");

						String filter = " e.strGroupCode ";
						if (billSeriesType.equalsIgnoreCase("Group"))
						{
							filter = " e.strGroupCode ";
						}
						else if (billSeriesType.equalsIgnoreCase("Sub Group"))
						{
							filter = " d.strSubGroupCode ";
						}
						else if (billSeriesType.equalsIgnoreCase("Menu Head"))
						{
							filter = " c.strMenuCode ";
						}
						else if (billSeriesType.equalsIgnoreCase("Revenue Head"))
						{
							filter = " a.strRevenueHead ";
						}
						else
						{
							filter = "  ";
						}
						sqlBuilder.append(" and " + filter + " IN " + funGetCodes(billSeriesCodes));
						sqlBuilder.append(" GROUP BY a.strItemCode; ");

						List isBillSeriesExists = objBaseService.funGetList(sqlBuilder, "sql");
						if (isBillSeriesExists != null && isBillSeriesExists.size() > 0)
						{
							isExistsBillSeries = true;
							Object[] objArr = (Object[]) isBillSeriesExists.get(0);

							if (hmBillSeriesItemList.containsKey(billSeries))
							{
								hmBillSeriesItemList.get(billSeries).add(objBillItemDtl);
							}
							else
							{
								List<clsPOSKOTItemDtl> listBillSeriesDtl = new ArrayList<clsPOSKOTItemDtl>();

								listBillSeriesDtl.add(objBillItemDtl);

								hmBillSeriesItemList.put(billSeries, listBillSeriesDtl);
							}
							break;
						}
					}

					if (!isExistsBillSeries)
					{
						if (hmBillSeriesItemList.containsKey("NoBillSeries"))
						{
							hmBillSeriesItemList.get("NoBillSeries").add(objBillItemDtl);
						}
						else
						{
							List<clsPOSKOTItemDtl> listBillSeriesDtl = new ArrayList<clsPOSKOTItemDtl>();

							listBillSeriesDtl.add(objBillItemDtl);

							hmBillSeriesItemList.put("NoBillSeries", listBillSeriesDtl);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			// objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			return hmBillSeriesItemList;
		}
	}

	/**
	 * This method generated the string for codes which is going to use in query as
	 * filter.
	 * 
	 * @param codes
	 * @return
	 */
	private String funGetCodes(String codes)
	{
		StringBuilder codeBuilder = new StringBuilder("(");
		try
		{
			String code[] = codes.split(",");
			for (int i = 0; i < code.length; i++)
			{
				if (i == 0)
				{
					codeBuilder.append("'" + code[i] + "'");
				}
				else
				{
					codeBuilder.append(",'" + code[i] + "'");
				}
			}
			codeBuilder.append(")");
		}
		catch (Exception e)
		{
			// objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			return codeBuilder.toString();
		}
	}

	/**
	 * This method calculates the paxes based on no bills are there.
	 * 
	 * @param totalPAX
	 * @param totalBills
	 * @return
	 */
	public Map<Integer, Integer> funGetPAXPerBill(double totalPAX, double totalBills)
	{
		Map<Integer, Integer> mapPAXPerBill = new HashMap<>();
		mapPAXPerBill.put(0, 0);

		double pax = totalPAX;

		double noOfBills = totalBills;
		for (int i = 0; i < noOfBills; i++)
		{
			int noOfBillsToBeFloor = (int) (pax % noOfBills);

			if (i < noOfBillsToBeFloor)
			{
				int paxPerBill = (int) Math.ceil(pax / noOfBills);
				// System.out.println("PAX=" + pax + "\tNo of bills=" + noOfBills + " \tpax Per
				// Bill=" + paxPerBill);

				mapPAXPerBill.put(i, paxPerBill);
			}
			else
			{
				int paxPerBill = (int) Math.floor(pax / noOfBills);
				// System.out.println("PAX=" + pax + "\tNo of bills=" + noOfBills + " \tpax Per
				// Bill=" + paxPerBill);
				mapPAXPerBill.put(i, paxPerBill);
			}
		}

		return mapPAXPerBill;
	}

	/**
	 * This method updated the table status based on KOT,Billed or settle.
	 * 
	 * @param tableNo
	 * @param status
	 */
	public void funUpdateTableStatus(String tableNo, String status)
	{
		try
		{
			String sqlTableStatus = "update tbltablemaster set strStatus='" + status + "' where strTableNo='" + tableNo + "';";
			objBaseService.funExecuteUpdate(sqlTableStatus, "sql");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to clear KOT temp table after billed or full voided.
	 * 
	 * @param tableNo
	 * @param posCode
	 */
	public void funClearRTempTable(String tableNo, String posCode)
	{
		try
		{
			String sqlDeleteKOT = "delete from tblitemrtemp where strTableNo='" + tableNo + "' and strPOSCode='" + posCode + "'";
			objBaseService.funExecuteUpdate(sqlDeleteKOT, "sql");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method takes bill Series Prefix as F,L etc.
	 * 
	 * @param billSeriesPrefix
	 * @return billNo for this bill series prefix
	 */
	public String funGetBillSeriesBillNo(String billSeriesPrefix, String posCode)
	{
		String billSeriesBillNo = "";

		try
		{
			int billSeriesLastNo = 0;

			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.intLastNo " + "from tblbillseries a " + "where a.strBillSeries='" + billSeriesPrefix + "' " + "and (a.strPOSCode='" + posCode + "' OR a.strPOSCode='All') ");

			List listOfBillSeriesLatNo = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfBillSeriesLatNo != null && listOfBillSeriesLatNo.size() > 0)
			{
				billSeriesLastNo = Integer.parseInt(listOfBillSeriesLatNo.get(0).toString());
			}

			billSeriesBillNo = billSeriesPrefix + "" + posCode + "" + String.format("%05d", (billSeriesLastNo + 1));

			// update last bill series last no
			String sqlUpdate = "update tblbillseries " + "set intLastNo='" + (billSeriesLastNo + 1) + "' " + "where (strPOSCode='" + posCode + "' OR strPOSCode='All') " + "and strBillSeries='" + billSeriesPrefix + "' ";
			objBaseService.funExecuteUpdate(sqlUpdate, "sql");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return billSeriesBillNo;
		}
	}

	/**
	 * 
	 * @param listBillSeriesBillDtl
	 * @param hdBillNo
	 * @return all other bill nos than hdbillno
	 */
	public String funGetBillSeriesDtlBillNos(List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl, String hdBillNo)
	{
		StringBuilder sbDtllBillNos = new StringBuilder("");
		try
		{
			for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
			{
				if (listBillSeriesBillDtl.get(i).getStrHdBillNo().equals(hdBillNo))
				{
					continue;
				}
				else
				{
					if (sbDtllBillNos.length() == 0)
					{
						sbDtllBillNos.append(listBillSeriesBillDtl.get(i).getStrHdBillNo());
					}
					else
					{
						sbDtllBillNos.append(",");
						sbDtllBillNos.append(listBillSeriesBillDtl.get(i).getStrHdBillNo());
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sbDtllBillNos.toString();
	}

	// generate bill no.
	public String funGenerateBillNo(String POSCode)
	{
		String voucherNo = "";
		try
		{
			long code = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strBillNo from tblstorelastbill where strPosCode='" + POSCode + "'");
			List listItemDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listItemDtl != null && listItemDtl.size() > 0)
			{
				Object objItemDtl = (Object) listItemDtl.get(0);
				code = Math.round(Double.parseDouble(objItemDtl.toString()));
				code = code + 1;
				voucherNo = POSCode + String.format("%05d", code);
				objBaseServiceImpl.funExecuteUpdate("update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + POSCode + "'", "sql");
			}
			else
			{
				voucherNo = POSCode + "00001";
				sqlBuilder.setLength(0);
				objBaseServiceImpl.funExecuteUpdate("insert into tblstorelastbill values('" + POSCode + "','1')", "sql");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return voucherNo;
		}
	}

	/* common Bill saving logic Make KOT,Home Delivery,Take Away */
	
	/*
	* common Bill saving logic Make KOT,Home Delivery,Take Away
	* 
	*  Make KOT
	* Home Delivery
	* Take Away
	**/	
	public void funSaveBill(boolean isBillSeries, String billSeriesPrefix, List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl, String voucherNo, List<clsPOSKOTItemDtl> listOfItemsKOTWiseToBeSave, clsPOSBillSettlementBean rootBeanObjectForReference, HttpServletRequest request, Map<String, clsPOSPromotionItems> hmPromoItem)
	{
		try
		{

			StringBuilder sbSql = new StringBuilder();
			sbSql.setLength(0);

			String clientCode = "",
					POSCode = "",
					POSDate = "",
					userCode = "",
					posClientCode = "";
			int totalPAXNo = rootBeanObjectForReference.getIntPaxNo();
			String tableNo = rootBeanObjectForReference.getStrTableNo();

			clientCode = request.getSession().getAttribute("gClientCode").toString();
			POSCode = request.getSession().getAttribute("gPOSCode").toString();
			POSDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			userCode = request.getSession().getAttribute("gUserCode").toString();

			String split = POSDate;
			String billDateTime = split;
			String custCode = "";

			String areaCodeForTransaction = rootBeanObjectForReference.getStrAreaCode();
			String operationTypeForBilling = rootBeanObjectForReference.getOperationType();

			Date dt = new Date();
			String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			String dateTime = POSDate + " " + currentDateTime.split(" ")[1];

			List<clsBillDtlModel> listObjBillDtl = new ArrayList<clsBillDtlModel>();
			List<clsBillModifierDtlModel> listObjBillModBillDtls = new ArrayList<clsBillModifierDtlModel>();
			List<clsBillPromotionDtlModel> listBillPromotionDtlModel = new ArrayList<clsBillPromotionDtlModel>();

			String custName = "",
					cardNo = "",
					orderProcessTime,
					orderPickupTime;
			String kotNo = "";

			/**
			 * listOfItemsKOTWiseToBeSave is the main KOT list to be save
			 */
			if (listOfItemsKOTWiseToBeSave.size() > 0)
			{
				for (int i = 0; i < listOfItemsKOTWiseToBeSave.size(); i++)
				{
					clsPOSKOTItemDtl objKOTItem = listOfItemsKOTWiseToBeSave.get(i);

					String iCode = objKOTItem.getStrItemCode();
					String iName = objKOTItem.getStrItemName();
					double iQty = objKOTItem.getDblItemQuantity();
					String iAmt = String.valueOf(objKOTItem.getDblAmount());
					double iTaxAmt = objKOTItem.getDblTaxAmount();

					double rate = objKOTItem.getDblRate();
					kotNo = objKOTItem.getStrKOTNo();
					String manualKOTNo = objKOTItem.getStrManualKOTNo();
					
					if(objKOTItem.getStrKOTDateTime()!=null && !objKOTItem.getStrKOTDateTime().isEmpty())
					{
						billDateTime = objKOTItem.getStrKOTDateTime();
					}
					else
					{
						billDateTime=dateTime;
					}
					custCode = objKOTItem.getStrCustomerCode();
					custName = objKOTItem.getStrCustomerName();
					String promoCode = objKOTItem.getStrPromoCode();
					cardNo = objKOTItem.getStrCardNo();
					orderProcessTime = objKOTItem.getStrOrderProcessTime();
					orderPickupTime = objKOTItem.getStrOrderPickupTime();
					String sqlInsertBillDtl = "";

					if (!iCode.contains("M"))
					{
						if (null != hmPromoItem)
						{
							clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
							if (null != hmPromoItem.get(iCode))
							{
								clsPOSPromotionItems objPromoItemDtl = hmPromoItem.get(iCode);
								if (objPromoItemDtl.getPromoType().equals("ItemWise"))
								{
									double freeQty = objPromoItemDtl.getFreeItemQty();
									double freeAmt = freeQty * rate;

									promoCode = objPromoItemDtl.getPromoCode();
									objPromortion.setStrItemCode(iCode);
									objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
									objPromortion.setDblAmount(freeAmt);
									objPromortion.setDblDiscountAmt(0);
									objPromortion.setDblDiscountPer(0);
									objPromortion.setDblQuantity(freeQty);
									objPromortion.setDblRate(rate);
									objPromortion.setStrDataPostFlag("N");
									objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
									objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

									hmPromoItem.remove(iCode);
								}
								else if (objPromoItemDtl.getPromoType().equals("Discount"))
								{
									if (objPromoItemDtl.getDiscType().equals("Value"))
									{
										double freeQty = objPromoItemDtl.getFreeItemQty();
										double amount = freeQty * rate;
										double discAmt = objPromoItemDtl.getDiscAmt();
										promoCode = objPromoItemDtl.getPromoCode();

										objPromortion.setStrItemCode(iCode);
										objPromortion.setStrPromotionCode("");
										objPromortion.setDblAmount(amount);
										objPromortion.setDblDiscountAmt(discAmt);
										objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
										objPromortion.setDblQuantity(0);
										objPromortion.setDblRate(rate);
										objPromortion.setStrDataPostFlag("N");
										objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
										objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
										hmPromoItem.remove(iCode);
									}
									else
									{
										iAmt = String.valueOf(iQty * rate);
										double amount = iQty * rate;
										double discAmt = amount * (objPromoItemDtl.getDiscPer() / 100);
										promoCode = objPromoItemDtl.getPromoCode();

										objPromortion.setStrItemCode(iCode);
										objPromortion.setStrPromotionCode("");
										objPromortion.setDblAmount(amount);
										objPromortion.setDblDiscountAmt(discAmt);
										objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
										objPromortion.setDblQuantity(0);
										objPromortion.setDblRate(rate);
										objPromortion.setStrDataPostFlag("N");
										objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
										objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

										hmPromoItem.remove(iCode);
									}
								}
								listBillPromotionDtlModel.add(objPromortion);
							}
						}
						String amt = iAmt;
						double discAmt = 0.00;
						double discPer = 0.00;

						if (!iCode.contains("M"))
						{
							for (clsPOSItemsDtlsInBill obj : rootBeanObjectForReference.getListOfBillItemDtl())
							{
								if (iCode.equalsIgnoreCase(obj.getItemCode()))
								{
									
									discPer = obj.getDiscountPer();
									discAmt=(discPer/100)*Double.parseDouble(amt);
									/*discAmt = obj.getDiscountAmt();*/
									
									break;
								}
							}
						}

						if (objKOTItem.getStrCounterCode() == null)
						{
							objKOTItem.setStrCounterCode("");
						}

						if (iQty > 0)
						{
							clsBillDtlModel objBillDtl = new clsBillDtlModel();
							if (iName.startsWith("=>"))
							{
								objBillDtl.setStrItemCode(iCode);
								objBillDtl.setStrItemName(iName);
								objBillDtl.setStrAdvBookingNo("");
								objBillDtl.setDblRate(rate);
								objBillDtl.setDblQuantity(iQty);
								objBillDtl.setDblAmount(Double.parseDouble(amt));
								objBillDtl.setDblTaxAmount(iTaxAmt);
								objBillDtl.setDteBillDate(billDateTime);
								objBillDtl.setStrKOTNo(kotNo);
								objBillDtl.setStrCounterCode(objKOTItem.getStrCounterCode());
								objBillDtl.setTmeOrderProcessing(orderProcessTime);
								objBillDtl.setStrDataPostFlag("N");
								objBillDtl.setStrMMSDataPostFlag("N");
								objBillDtl.setStrManualKOTNo(manualKOTNo);

								objBillDtl.setTdhYN("N");
								objBillDtl.setStrPromoCode(promoCode);

								objBillDtl.setStrWaiterNo(objKOTItem.getStrWaiterNo());
								objBillDtl.setSequenceNo("");
								objBillDtl.setTmeOrderPickup(orderPickupTime);

								objBillDtl.setDblDiscountAmt(discAmt);
								objBillDtl.setDblDiscountPer(discPer);
							}
							else
							{
								objBillDtl.setStrItemCode(iCode);
								objBillDtl.setStrItemName(iName);
								objBillDtl.setStrAdvBookingNo("");
								objBillDtl.setDblRate(rate);
								objBillDtl.setDblQuantity(iQty);
								objBillDtl.setDblAmount(Double.parseDouble(amt));
								objBillDtl.setDblTaxAmount(iTaxAmt);
								objBillDtl.setDteBillDate(billDateTime);
								objBillDtl.setStrKOTNo(kotNo);
								objBillDtl.setStrCounterCode(objKOTItem.getStrCounterCode());
								objBillDtl.setTmeOrderProcessing(orderProcessTime);
								objBillDtl.setStrDataPostFlag("N");
								objBillDtl.setStrMMSDataPostFlag("N");
								objBillDtl.setStrManualKOTNo(manualKOTNo);
								objBillDtl.setTdhYN("N");
								objBillDtl.setStrPromoCode(promoCode);

								objBillDtl.setStrWaiterNo(objKOTItem.getStrWaiterNo());
								objBillDtl.setSequenceNo("");
								objBillDtl.setTmeOrderPickup(orderPickupTime);

								objBillDtl.setDblDiscountAmt(discAmt);
								objBillDtl.setDblDiscountPer(discPer);
							}
							listObjBillDtl.add(objBillDtl);
						}
					}
					if (iCode.contains("M"))
					{
						StringBuilder sb1 = new StringBuilder(iCode);
						int seq = sb1.lastIndexOf("M");// break the string(if
														// itemcode contains
														// Itemcode with modifier
														// code then break the
														// string into substring )
						String modifierCode = sb1.substring(seq, sb1.length());// SubString
																				// modifier
																				// Code
						double amt = Double.parseDouble(iAmt);
						double modDiscAmt = 0,
								modDiscPer = 0;

						for (clsPOSItemsDtlsInBill obj : rootBeanObjectForReference.getListOfBillItemDtl())
						{
							if (iCode.equalsIgnoreCase(obj.getItemCode()))
							{
								modDiscAmt = obj.getDiscountAmt();
								modDiscPer = obj.getDiscountPer();
							}
						}
						StringBuilder sbTemp = new StringBuilder(iCode);

						clsBillModifierDtlModel objBillModDtl = new clsBillModifierDtlModel();
						objBillModDtl.setStrItemCode(iCode);
						objBillModDtl.setStrModifierCode(modifierCode);
						objBillModDtl.setStrModifierName(iName);
						objBillModDtl.setDblRate(rate);
						objBillModDtl.setDblQuantity(iQty);
						objBillModDtl.setDblAmount(amt);
						objBillModDtl.setStrCustomerCode("");
						objBillModDtl.setStrDataPostFlag("N");
						objBillModDtl.setStrMMSDataPostFlag("N");
						objBillModDtl.setStrDefaultModifierDeselectedYN("N");
						objBillModDtl.setSequenceNo("");

						objBillModDtl.setDblDiscAmt(modDiscPer);
						objBillModDtl.setDblDiscPer(modDiscAmt);

						listObjBillModBillDtls.add(objBillModDtl);
					}
				}
			}

			double subTotalForTax = 0.00,
					netTotal = 0.00,
					discPer = 0,
					discAmt = 0,
					deliveryCharge = 0.00,
					totalTaxAmt = 0.00,
					grandTotal = 0.00,
					tipAmt = 0.00,
					roudOff = 0.00,
					usdConvertionRate = 0.00;
			int intBillSeriesPax = 0;

			/**
			 * save Home Delivery
			 */
			funSaveHomeDelivery(voucherNo, rootBeanObjectForReference, POSCode, POSDate, clientCode);

			/**
			 * save discount
			 */
			List<clsBillDiscDtlModel> listBillDiscDtlModel = funSaveBillDiscountDetail(voucherNo, rootBeanObjectForReference, dateTime, POSCode, clientCode);

			/**
			 * calculating list of items for tax calculation
			 */
			List<clsPOSItemDtlForTax> arrListItemDtls = new ArrayList<clsPOSItemDtlForTax>();

			/* for bill items */
			for (clsBillDtlModel objBillDtl : listObjBillDtl)
			{
				clsPOSItemDtlForTax objItemDtl = new clsPOSItemDtlForTax();

				objItemDtl.setItemCode(objBillDtl.getStrItemCode());
				objItemDtl.setItemName(objBillDtl.getStrItemName());
				objItemDtl.setAmount(objBillDtl.getDblAmount());
				objItemDtl.setDiscAmt(objBillDtl.getDblDiscountAmt());
				objItemDtl.setDiscPer(objBillDtl.getDblDiscountPer());

				arrListItemDtls.add(objItemDtl);

				subTotalForTax += objBillDtl.getDblAmount();
				discAmt += objBillDtl.getDblDiscountAmt();
			}
			/* for bill modifiers */
			for (clsBillModifierDtlModel objBillModifierDtlModel : listObjBillModBillDtls)
			{
				clsPOSItemDtlForTax objItemDtl = new clsPOSItemDtlForTax();

				objItemDtl.setItemCode(objBillModifierDtlModel.getStrItemCode());
				objItemDtl.setItemName(objBillModifierDtlModel.getStrModifierName());
				objItemDtl.setAmount(objBillModifierDtlModel.getDblAmount());
				objItemDtl.setDiscAmt(objBillModifierDtlModel.getDblDiscAmt());
				objItemDtl.setDiscPer(objBillModifierDtlModel.getDblDiscPer());

				arrListItemDtls.add(objItemDtl);

				subTotalForTax += objBillModifierDtlModel.getDblAmount();
				discAmt += objBillModifierDtlModel.getDblDiscAmt();
			}

			String deleteBillTaxDTL = "delete from tblbilltaxdtl where strBillNo='" + voucherNo + "'";
			objBaseServiceImpl.funExecuteUpdate(deleteBillTaxDTL, "sql");

			List<clsPOSTaxCalculationDtls> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtls, POSCode, POSDate, areaCodeForTransaction, operationTypeForBilling, subTotalForTax, 0.0, "");

			List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();
			for (clsPOSTaxCalculationDtls objTaxCalculationDtls : arrListTaxDtl)
			{
				double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();

				clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
				objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
				objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
				objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
				objBillTaxDtl.setStrDataPostFlag("");

				listObjBillTaxBillDtls.add(objBillTaxDtl);

				totalTaxAmt += dblTaxAmt;
			}

			netTotal = subTotalForTax - discAmt;
			discPer = (discAmt / netTotal) * 100;

			grandTotal = subTotalForTax - discAmt + totalTaxAmt;

			// Insert into tblbillhd table
			clsBillHdModel objBillHd = new clsBillHdModel(new clsBillHdModel_ID(voucherNo, POSDate, clientCode));
			objBillHd.setStrBillNo(voucherNo);
			objBillHd.setStrAdvBookingNo("");
			objBillHd.setDteBillDate(dateTime);
			objBillHd.setStrPOSCode(POSCode);
			objBillHd.setStrSettelmentMode("");
			objBillHd.setDblDiscountAmt(discAmt);
			objBillHd.setDblDiscountPer(discPer);
			objBillHd.setDblTaxAmt(totalTaxAmt);
			objBillHd.setDblSubTotal(subTotalForTax);
			objBillHd.setDblGrandTotal(grandTotal);
			objBillHd.setStrTakeAway(rootBeanObjectForReference.getTakeAway());
			objBillHd.setStrOperationType(rootBeanObjectForReference.getOperationType());//DineIn,HomeDelivery/TakeAway
			objBillHd.setStrUserCreated(userCode);
			objBillHd.setStrUserEdited(userCode);
			objBillHd.setDteDateCreated(dateTime);
			objBillHd.setDteDateEdited(dateTime);
			objBillHd.setStrClientCode(clientCode);
			objBillHd.setStrTableNo(rootBeanObjectForReference.getStrTableNo());
			objBillHd.setStrWaiterNo(rootBeanObjectForReference.getStrWaiter());
			objBillHd.setStrCustomerCode(rootBeanObjectForReference.getStrCustomerCode());
			objBillHd.setStrManualBillNo("");
			objBillHd.setIntShiftCode(0);// /////////////////////////
			objBillHd.setIntPaxNo(rootBeanObjectForReference.getIntPaxNo());
			objBillHd.setStrDataPostFlag("N");
			objBillHd.setStrReasonCode("");
			objBillHd.setStrRemarks(rootBeanObjectForReference.getStrRemarks());
			objBillHd.setDblTipAmount(0.0);
			objBillHd.setDteSettleDate(POSDate);
			objBillHd.setStrCounterCode("");
			objBillHd.setDblDeliveryCharges(rootBeanObjectForReference.getDblDeliveryCharges());
			objBillHd.setStrAreaCode(rootBeanObjectForReference.getStrAreaCode());
			objBillHd.setStrDiscountRemark("");
			objBillHd.setStrTakeAwayRemarks("");
			objBillHd.setStrTransactionType(rootBeanObjectForReference.getTransactionType());//Make KOT,Direct Biller,Modify Bill
			objBillHd.setIntOrderNo(0);
			objBillHd.setStrCouponCode("");
			objBillHd.setStrJioMoneyRRefNo("");
			objBillHd.setStrJioMoneyAuthCode("");
			objBillHd.setStrJioMoneyTxnId("");
			objBillHd.setStrJioMoneyTxnDateTime("");
			objBillHd.setStrJioMoneyCardNo("");
			objBillHd.setStrJioMoneyCardType("");
			objBillHd.setDblRoundOff(0.00);
			objBillHd.setIntBillSeriesPaxNo(totalPAXNo);
			objBillHd.setDtBillDate(POSDate);
			objBillHd.setIntOrderNo(0);

			String discountOn = "";
			String chckDiscounton = rootBeanObjectForReference.getStrDisountOn();
			if (chckDiscounton != null)
			{
				if (chckDiscounton.equals("Total"))
				{
					discountOn = "All";
				}
				if (chckDiscounton.equals("item"))
				{
					discountOn = "Item";
				}
				if (chckDiscounton.equals("group"))
				{
					discountOn = "Group";
				}
				if (chckDiscounton.equals("subGroup"))
				{
					discountOn = "SubGroup";
				}
			}
			objBillHd.setStrDiscountOn(discountOn);
			objBillHd.setStrCardNo("");

			String gCMSIntegrationY = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, POSCode, "gCMSIntegrationYN");
			if (gCMSIntegrationY.equalsIgnoreCase("Y"))
			{
				if (rootBeanObjectForReference.getStrCustomerCode().trim().length() > 0)
				{
					String sqlDeleteCustomer = "delete from tblcustomermaster where strCustomerCode='" + custCode + "' " + "and strClientCode='" + clientCode + "'";
					objBaseServiceImpl.funExecuteUpdate(sqlDeleteCustomer, "sql");

					String sqlInsertCustomer = "insert into tblcustomermaster (strCustomerCode,strCustomerName,strUserCreated" + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) " + "values('" + custCode + "','" + custName + "','" + userCode + "','" + userCode + "'" + ",'" + dateTime + "','" + dateTime + "'" + ",'" + clientCode + "')";
					objBaseServiceImpl.funExecuteUpdate(sqlInsertCustomer, "sql");
				}
			}

			objBillHd.setListBillDtlModel(listObjBillDtl);
			objBillHd.setListBillModifierDtlModel(listObjBillModBillDtls);

			/* Save bill settlement data */
			List<clsPOSSettlementDtlsOnBill> listObjBillSettlementDtl = rootBeanObjectForReference.getListSettlementDtlOnBill();
			double totalSettlementAmt = 0.00;
			for (clsPOSSettlementDtlsOnBill objBillSettlementDtl : listObjBillSettlementDtl)
			{
				totalSettlementAmt += objBillSettlementDtl.getDblSettlementAmt();
			}

			List<clsBillSettlementDtlModel> listOfBillSettlementToBeSave = new ArrayList<clsBillSettlementDtlModel>();

			if (isBillSeries && !billSeriesPrefix.isEmpty())
			{
				for (clsPOSSettlementDtlsOnBill objBillSettlementDtl : listObjBillSettlementDtl)
				{
					double settlePerToTotalSettleAmt = (objBillSettlementDtl.getDblSettlementAmt() / totalSettlementAmt) * 100;

					double settleAmt = (settlePerToTotalSettleAmt / 100) * grandTotal;

					clsBillSettlementDtlModel objSettleModel = new clsBillSettlementDtlModel();

					objSettleModel.setStrSettlementCode(objBillSettlementDtl.getStrSettelmentCode());
					objSettleModel.setDblSettlementAmt(settleAmt);
					objSettleModel.setDblPaidAmt(objBillSettlementDtl.getDblPaidAmt());
					objSettleModel.setStrExpiryDate("");
					objSettleModel.setStrCardName("");
					objSettleModel.setStrRemark("");

					objSettleModel.setStrCustomerCode("");
					objSettleModel.setDblActualAmt(objBillSettlementDtl.getDblActualAmt());
					objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt());
					objSettleModel.setStrGiftVoucherCode("");
					objSettleModel.setStrDataPostFlag("");

					objSettleModel.setStrFolioNo("");
					objSettleModel.setStrRoomNo("");

					listOfBillSettlementToBeSave.add(objSettleModel);
				}
			}
			else
			{
				listOfBillSettlementToBeSave = funInsertBillSettlementDtlTable(listObjBillSettlementDtl, userCode, dateTime, voucherNo);
			}

			objBillHd.setStrSettelmentMode("");

			if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 0)
			{
				objBillHd.setStrSettelmentMode("");
			}
			else if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 1)
			{
				objBillHd.setStrSettelmentMode(listObjBillSettlementDtl.get(0).getStrSettelmentDesc());
			}
			else
			{
				objBillHd.setStrSettelmentMode("MultiSettle");
			}

			objBillHd.setListBillDiscDtlModel(listBillDiscDtlModel);
			objBillHd.setListBillSettlementDtlModel(listOfBillSettlementToBeSave);
			objBillHd.setListBillDtlModel(listObjBillDtl);
			objBillHd.setListBillTaxDtl(listObjBillTaxBillDtls);
			objBillHd.setListBillPromotionDtlModel(listBillPromotionDtlModel);

			/* Save Bill HD */
			objBaseServiceImpl.funSave(objBillHd);

			objUtility.funUpdateBillDtlWithTaxValues(voucherNo, "Live", POSDate);
			sbSql.setLength(0);
			sbSql.append("select dblQuantity,dblRate,strItemCode " + "from tblbillpromotiondtl " + " where strBillNo='" + voucherNo + "' and strPromoType='ItemWise' ");

			List listBillPromo = objBaseServiceImpl.funGetList(sbSql, "sql");
			if (listBillPromo.size() > 0)
			{
				for (int i = 0; i < listBillPromo.size(); i++)
				{
					Object[] objPrmo = (Object[]) listBillPromo.get(i);
					double freeQty = Double.parseDouble(objPrmo[0].toString());
					sbSql.setLength(0);
					sbSql.append("select strItemCode,dblQuantity,strKOTNo,dblAmount " + " from tblbilldtl " + " where strItemCode='" + objPrmo[2].toString() + "'" + " and strBillNo='" + voucherNo + "'");

					List listBillDetail = objBaseServiceImpl.funGetList(sbSql, "sql");
					if (listBillDetail.size() > 0)
					{
						for (int j = 0; j < listBillDetail.size(); j++)
						{
							Object[] objBillDtl = (Object[]) listBillDetail.get(j);
							if (freeQty > 0)
							{
								double saleQty = Double.parseDouble(objBillDtl[1].toString());
								double saleAmt = Double.parseDouble(objBillDtl[3].toString());
								if (saleQty <= freeQty)
								{
									freeQty = freeQty - saleQty;
									double amtToUpdate = saleAmt - (saleQty * Double.parseDouble(objPrmo[1].toString()));
									String sqlUpdate = "update tblbilldtl set dblAmount= " + amtToUpdate + " " + " where strItemCode='" + objBillDtl[0].toString() + "' " + "and strKOTNo='" + objBillDtl[2].toString() + "'";
									objBaseServiceImpl.funExecuteUpdate(sqlUpdate, "sql");
								}
								else
								{
									double amtToUpdate = saleAmt - (freeQty * Double.parseDouble(objPrmo[1].toString()));
									String sqlUpdate = "update tblbilldtl set dblAmount= " + amtToUpdate + " " + " where strItemCode='" + objBillDtl[0].toString() + "' " + "and strKOTNo='" + objBillDtl[2].toString() + "'";
									objBaseServiceImpl.funExecuteUpdate(sqlUpdate, "sql");
									freeQty = 0;
								}
							}
						}
					}
				}
			}

			if (isBillSeries && !billSeriesPrefix.isEmpty())
			{
				clsPOSBillSeriesBillDtl objBillSeriesBillDtl = new clsPOSBillSeriesBillDtl();
				objBillSeriesBillDtl.setStrHdBillNo(voucherNo);
				objBillSeriesBillDtl.setStrBillSeries(billSeriesPrefix);
				objBillSeriesBillDtl.setDblGrandTotal(grandTotal);
				objBillSeriesBillDtl.setFlgHomeDelPrint(true);

				listBillSeriesBillDtl.add(objBillSeriesBillDtl);
			}

			/* updating table status */
			if (listOfBillSettlementToBeSave != null && listOfBillSettlementToBeSave.size() > 0)
			{
				/* table billed and settled */
				funUpdateTableStatus(tableNo, "Normal");
			}
			else
			{
				/* table only billed and not settled */
				funUpdateTableStatus(tableNo, "Billed");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private List funSaveBillDiscountDetail(String voucherNo, clsPOSBillSettlementBean objBean, String dateTime, String POSCode, String userCode)
	{
		List<clsBillDiscDtlModel> listBillDiscDtlModel = new ArrayList<clsBillDiscDtlModel>();
		try
		{
			double totalDiscAmt = 0.00,
					finalDiscPer = 0.00;
			for (clsPOSDiscountDtlsOnBill objBillDiscDtl : objBean.getListDiscountDtlOnBill())
			{
				String discOnType = objBillDiscDtl.getDiscountOnType();
				String discOnValue = objBillDiscDtl.getDiscountOnValue();
				String remark = objBillDiscDtl.getDiscountRemarks();
				String reason = objBillDiscDtl.getDiscountReasonCode();
				double discPer = objBillDiscDtl.getDiscountPer();
				double discAmt = objBillDiscDtl.getDiscountAmt();
				double discOnAmt = objBillDiscDtl.getDiscountOnAmt();

				clsBillDiscDtlModel objDiscModel = new clsBillDiscDtlModel();
				objDiscModel.setStrPOSCode(POSCode);
				objDiscModel.setDblDiscAmt(discAmt);
				objDiscModel.setDblDiscPer(discPer);
				objDiscModel.setDblAmount(discOnAmt);
				objDiscModel.setStrDiscOnType(discOnType);
				objDiscModel.setStrDiscOnValue(discOnValue);
				objDiscModel.setDteDateCreated(dateTime);
				objDiscModel.setDteDateEdited(dateTime);
				objDiscModel.setStrUserCreated(userCode);
				objDiscModel.setStrUserEdited(userCode);
				objDiscModel.setStrDiscReasonCode(reason);
				objDiscModel.setStrDiscRemarks(remark);
				objDiscModel.setStrDataPostFlag("N");
				listBillDiscDtlModel.add(objDiscModel);
				totalDiscAmt += discAmt;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return listBillDiscDtlModel;
		}
	}

	private List funInsertBillSettlementDtlTable(List<clsPOSSettlementDtlsOnBill> listObjBillSettlementDtl, String userCode, String dtCurrentDate, String voucherNo) throws Exception
	{
		String sqlDelete = "delete from tblbillsettlementdtl where strBillNo='" + voucherNo + "'";
		objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql");

		List<clsBillSettlementDtlModel> listBillSettlementDtlModel = new ArrayList<clsBillSettlementDtlModel>();

		for (clsPOSSettlementDtlsOnBill objBillSettlementDtl : listObjBillSettlementDtl)
		{
			clsBillSettlementDtlModel objSettleModel = new clsBillSettlementDtlModel();

			objSettleModel.setStrSettlementCode(objBillSettlementDtl.getStrSettelmentCode());
			objSettleModel.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt());
			objSettleModel.setDblPaidAmt(objBillSettlementDtl.getDblPaidAmt());
			objSettleModel.setStrExpiryDate("");
			objSettleModel.setStrCardName("");
			objSettleModel.setStrRemark("");

			objSettleModel.setStrCustomerCode("");
			objSettleModel.setDblActualAmt(objBillSettlementDtl.getDblActualAmt());
			objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt());
			objSettleModel.setStrGiftVoucherCode("");
			objSettleModel.setStrDataPostFlag("");

			objSettleModel.setStrFolioNo("");
			objSettleModel.setStrRoomNo("");

			listBillSettlementDtlModel.add(objSettleModel);

		}

		return listBillSettlementDtlModel;

	}

	public clsBillPromotionDtlModel funInsertIntoPromotion(String voucherNo, String iCode, String promoCode, double rate, double iQty, Map<String, clsPOSPromotionItems> hmPromoItem)
	{

		clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
		clsPOSPromotionItems objPromoItemDtl = hmPromoItem.get(iCode);
		if (objPromoItemDtl.getPromoType().equals("ItemWise"))
		{
			double freeQty = objPromoItemDtl.getFreeItemQty();
			double freeAmt = freeQty * rate;

			promoCode = objPromoItemDtl.getPromoCode();
			objPromortion.setStrItemCode(iCode);
			objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
			objPromortion.setDblAmount(freeAmt);
			objPromortion.setDblDiscountAmt(0);
			objPromortion.setDblDiscountPer(0);
			objPromortion.setDblQuantity(freeQty);
			objPromortion.setDblRate(rate);
			objPromortion.setStrDataPostFlag("N");
			objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
			objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
			hmPromoItem.remove(iCode);
		}
		else if (objPromoItemDtl.getPromoType().equals("Discount"))
		{
			if (objPromoItemDtl.getDiscType().equals("Value"))
			{
				double freeQty = objPromoItemDtl.getFreeItemQty();
				double amount = freeQty * rate;
				double discAmt = objPromoItemDtl.getDiscAmt();

				promoCode = objPromoItemDtl.getPromoCode();
				objPromortion.setStrItemCode(iCode);
				objPromortion.setStrPromotionCode("");
				objPromortion.setDblAmount(amount);
				objPromortion.setDblDiscountAmt(discAmt);
				objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
				objPromortion.setDblQuantity(0);
				objPromortion.setDblRate(rate);
				objPromortion.setStrDataPostFlag("N");
				objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
				objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
				hmPromoItem.remove(iCode);
			}
			else
			{

				double amount = iQty * rate;
				double discAmt = amount * (objPromoItemDtl.getDiscPer() / 100);

				promoCode = objPromoItemDtl.getPromoCode();

				objPromortion.setStrItemCode(iCode);
				objPromortion.setStrPromotionCode("");
				objPromortion.setDblAmount(amount);
				objPromortion.setDblDiscountAmt(discAmt);
				objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
				objPromortion.setDblQuantity(0);
				objPromortion.setDblRate(rate);
				objPromortion.setStrDataPostFlag("N");
				objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
				objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

				hmPromoItem.remove(iCode);
			}
		}

		return objPromortion;
	}

	public void funSaveHomeDelivery(String voucherNo, clsPOSBillSettlementBean objBean, String POSCode, String POSDate, String clientCode) throws Exception
	{
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		sbSql.append("select strHomeDelivery,strCustomerCode,strCustomerName,strDelBoyCode " + "from tblitemrtemp where strTableNo='" + objBean.getStrTableNo() + "' " + "group by strTableNo ;");

		List listsqlCheckHomeDelivery = objBaseServiceImpl.funGetList(sbSql, "sql");
		if (listsqlCheckHomeDelivery.size() > 0)
		{
			Object[] objM = (Object[]) listsqlCheckHomeDelivery.get(0);
			String homeDeliveryYesNo = objM[0].toString();
			String customerCode = objM[1].toString();
			String custName = objM[2].toString();
			String deliveryBoyCode = objM[3].toString();

			if ("Yes".equalsIgnoreCase(homeDeliveryYesNo))
			{
				Calendar c = Calendar.getInstance();
				int hh = c.get(Calendar.HOUR);
				int mm = c.get(Calendar.MINUTE);
				int ss = c.get(Calendar.SECOND);
				int ap = c.get(Calendar.AM_PM);

				String ampm = "AM";
				if (ap == 1)
				{
					ampm = "PM";
				}
				String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;
				clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
				objHomeDeliveryHdModel.setStrBillNo(voucherNo);
				objHomeDeliveryHdModel.setStrCustomerCode(customerCode);
				objHomeDeliveryHdModel.setStrDPCode(deliveryBoyCode);
				objHomeDeliveryHdModel.setDteDate(POSDate);
				objHomeDeliveryHdModel.setTmeTime(currentTime);
				objHomeDeliveryHdModel.setStrPOSCode(POSCode);
				objHomeDeliveryHdModel.setStrCustAddressLine1("");
				objHomeDeliveryHdModel.setStrCustAddressLine2("");
				objHomeDeliveryHdModel.setStrCustAddressLine3("");
				objHomeDeliveryHdModel.setStrCustAddressLine4("");
				objHomeDeliveryHdModel.setStrCustCity("");
				objHomeDeliveryHdModel.setStrClientCode(clientCode);
				objHomeDeliveryHdModel.setDblHomeDeliCharge(objBean.getDblDeliveryCharges());
				objHomeDeliveryHdModel.setDblLooseCashAmt(0);
				objHomeDeliveryHdModel.setStrDataPostFlag("N");
				objBaseServiceImpl.funSave(objHomeDeliveryHdModel);

				// Saving for home delivery Detail data
				if (objBean.getStrDeliveryBoyCode() != null)
				{
					clsHomeDeliveryDtlModel objDtlModel = new clsHomeDeliveryDtlModel();
					objDtlModel.setStrBillNo(voucherNo);
					objDtlModel.setDblDBIncentives(0);
					objDtlModel.setDteBillDate(POSDate);
					objDtlModel.setStrClientCode(clientCode);
					objDtlModel.setStrDataPostFlag("N");
					objDtlModel.setStrDPCode(deliveryBoyCode);
					objDtlModel.setStrSettleYN("N");
					objBaseServiceImpl.funSave(objDtlModel);
				}
			}
		}
	}

}
