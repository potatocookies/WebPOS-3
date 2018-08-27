package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtl;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
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
	 * @param tableNo
	 * @param status
	 */
	public void funUpdateTableStatus(String tableNo,String status)
	{
		try
		{
			String sqlTableStatus = "update tbltablemaster set strStatus='"+status+"' where strTableNo='" +tableNo+ "';";
			objBaseService.funExecuteUpdate(sqlTableStatus, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * This method is used to clear KOT temp table after billed or full voided.
	 * @param tableNo
	 * @param posCode
	 */
	public void funClearRTempTable(String tableNo,String posCode)
	{
		try
		{
			String sqlDeleteKOT = "delete from tblitemrtemp where strTableNo='" + tableNo + "' and strPOSCode='" + posCode + "'";
			objBaseService.funExecuteUpdate(sqlDeleteKOT, "sql");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

}
