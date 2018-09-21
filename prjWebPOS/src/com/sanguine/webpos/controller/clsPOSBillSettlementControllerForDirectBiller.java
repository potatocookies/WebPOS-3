package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSeriesBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSSettlementDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSTaxCalculation;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.bean.clsPOSTaxDtlsOnBill;
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
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSTextFileGenerator;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSBillSettlementControllerForDirectBiller
{

	private List listItemCode;
	String clientCode = "",
			posCode = "", posDate = "",
			userCode = "",
			posClientCode = "";

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	@Autowired
	clsPOSUtilityController objUtility;

	@Autowired
	clsPOSTextFileGenerator objTextFileGeneration;

	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;

	@Autowired
	clsPOSBillingAPIController objBillingAPI;

	@Autowired
	clsPOSBillSettlementControllerForMakeKOT objMakeKOT;

	@Autowired
	intfBaseService objBaseService;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	clsPOSMasterService objMasterService;

	JSONArray listReasonCode,
			listReasonName;

	private Map<String, clsPOSPromotionItems> hmPromoItem = new HashMap<String, clsPOSPromotionItems>();

	// for Direct Biller
	@RequestMapping(value = "/frmWebPOSBilling", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String urlHits = "1";
		try
		{

			urlHits = request.getParameter("saddr").toString();
			request.getSession().setAttribute("customerMobile", ""); // mobile
																		// no
		}
		catch (Exception e)
		{
			urlHits = "1";
		}
		clsPOSBillSettlementBean obBillSettlementBean = new clsPOSBillSettlementBean();
		clientCode = request.getSession().getAttribute("gClientCode").toString();
		posClientCode = request.getSession().getAttribute("gPOSCode").toString();
		posCode = request.getSession().getAttribute("gPOSCode").toString();
		posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		// //////////////// Direct biller Tab Data

		JSONObject jObj = objBillingAPI.funGetItemPricingDtl(clientCode, posDate, posCode);

		JSONArray jsonArrForDirectBillerMenuItemPricing = (JSONArray) jObj.get("MenuItemPricingDtl");
		obBillSettlementBean.setJsonArrForDirectBillerMenuItemPricing(jsonArrForDirectBillerMenuItemPricing);

		jObj = objBillingAPI.funGetMenuHeads(posCode, userCode);
		JSONArray jsonArrForDirectBillerMenuHeads = (JSONArray) jObj.get("MenuHeads");
		obBillSettlementBean.setJsonArrForMenuHeads(jsonArrForDirectBillerMenuHeads);
		obBillSettlementBean.setJsonArrForDirectBillerMenuHeads(jsonArrForDirectBillerMenuHeads);

		jObj = objBillingAPI.funGetButttonList("DirectBiller", posCode, clientCode);

		JSONArray jsonArrForDirectBillerFooterButtons = (JSONArray) jObj.get("buttonList");
		obBillSettlementBean.setJsonArrForDirectBillerFooterButtons(jsonArrForDirectBillerFooterButtons);

		jObj = objBillingAPI.funPopularItem(clientCode, posDate, posCode);
		JSONArray jsonArrForPopularItems = (JSONArray) jObj.get("PopularItems");

		obBillSettlementBean.setJsonArrForPopularItems(jsonArrForPopularItems);

		JSONArray jsonArrForTableDtl = objMakeKOT.funLoadTableDtl(clientCode, posCode);
		obBillSettlementBean.setJsonArrForTableDtl(jsonArrForTableDtl);

		JSONArray jsonArrForWaiterDtl = objMakeKOT.funGetWaiterList(posCode);
		obBillSettlementBean.setJsonArrForWaiterDtl(jsonArrForWaiterDtl);

		model.put("gPOSCode", posCode);
		model.put("gClientCode", clientCode);

		model.put("urlHits", urlHits);
		model.put("billNo", "");
		model.put("billDate", posDate.split("-")[2] + "-" + posDate.split("-")[1] + "-" + posDate.split("-")[0]);
		model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));

		// ///////////Bill Settlement tab ////////////////////////////////////

		String usertype = request.getSession().getAttribute("gUserType").toString();
		boolean isSuperUser = false;
		if (usertype.equalsIgnoreCase("yes"))
		{
			isSuperUser = true;
		}
		else
		{
			isSuperUser = false;
		}

		List listSettlementObject = new ArrayList<clsPOSSettelementOptions>();
		try
		{

			JSONObject jObj1 = objBillingAPI.funSettlementMode(clientCode, posCode, isSuperUser);

			JSONArray jArr = new JSONArray();// (JSONArray)jObj.get("SettleDesc");
			Gson gson = new Gson();
			Type objectType = new TypeToken<clsPOSSettelementOptions>()
			{
			}.getType();
			JSONObject jsSettelementOptionsDtl = (JSONObject) jObj1.get("SettleObj");

			Type listType = new TypeToken<List<clsPOSSettelementOptions>>()
			{
			}.getType();
			listSettlementObject = gson.fromJson(jObj1.get("listSettleObj").toString(), listType);

			JSONObject jsSettle = new JSONObject();
			for (int j = 0; j < listSettlementObject.size(); j++)
			{
				// if(jArr.size()==listSettlementObject.size())
				// jsSettle.put(jArr.get(j).toString(),
				// listSettlementObject.get(j));
				jArr.add(listSettlementObject.get(j));
			}
			model.put("ObSettleObject", jsSettle);
			obBillSettlementBean.setJsonArrForSettleButtons(jArr);
			// objDirectBillerBean.setListOfBillItemDtl(listOfPunchedItemsDtl);
			obBillSettlementBean.setDteExpiryDate(posDate);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		String operationFrom = "directBiller";
		model.put("operationFrom", operationFrom);
		
		String formToBeOpen="Billing";
		model.put("formToBeOpen", formToBeOpen);
		
		
		model.put("billDate", posDate);
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		String gPopUpToApplyPromotionsOnBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPopUpToApplyPromotionsOnBill");
		model.put("gPopUpToApplyPromotionsOnBill", gPopUpToApplyPromotionsOnBill);
		model.put("gCreditCardSlipNo", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardSlipNoCompulsoryYN"));
		model.put("gCreditCardExpiryDate", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardExpiryDateCompulsoryYN"));
		
		model.put("gSkipPax", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("SkipPax"));
		model.put("gSkipWaiter", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("SkipWaiter"));

		funLoadAllReasonMasterData(request);
		
		
		List listDiscountCombo = new ArrayList<List>();
		List listSubGroupName = new ArrayList<>();
		List listSubGroupCode = new ArrayList<>();
		List listGroupName = new ArrayList<>();
		List listGroupCode = new ArrayList<>();
		listDiscountCombo = funLoadItemsGroupSubGroupData();
		if (listDiscountCombo.size() > 0)
		{
			listSubGroupName = (List) listDiscountCombo.get(0);
			listSubGroupCode = (List) listDiscountCombo.get(1);
			listGroupName = (List) listDiscountCombo.get(2);
			listGroupCode = (List) listDiscountCombo.get(3);

		}

		model.put("listReasonCode", listReasonCode);
		model.put("listReasonName", listReasonName);

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select dblMaxDiscount ,strApplyDiscountOn from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') ");
		List list1 = null;
		try
		{
			list1 = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double gMaxDiscount = 0;
		String gApplyDiscountOn = "";
		if (list1 != null && list1.size() > 0)
		{
			Object[] obj = (Object[]) list1.get(0);
			gMaxDiscount = Double.parseDouble(obj[0].toString());
			gApplyDiscountOn = obj[1].toString();
		}

		return new ModelAndView("frmWebPOSBilling", "command", obBillSettlementBean);

	}

	public void funLoadAllReasonMasterData(HttpServletRequest request)
	{
		Map<String, String> mapModBill = new HashMap<String, String>();
		Map<String, String> mapComplementry = new HashMap<String, String>();
		Map<String, String> mapDiscount = new HashMap<String, String>();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();

		JSONObject jObj = objBillingAPI.funLoadAllReasonMasterData(clientCode);
		if (jObj != null)
		{
			JSONObject JObjBill = new JSONObject();
			// JSONObject JObjComplementry=new JSONObject();
			// JSONObject JObjDiscount=new JSONObject();
			JSONArray jArr = new JSONArray();

			jArr = (JSONArray) jObj.get("ModifyBill");
			if (jArr != null)
			{
				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapModBill.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}

			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("Complementry");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapComplementry.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}
			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("Discount");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapDiscount.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}
			listReasonCode = new JSONArray();
			listReasonName = new JSONArray();

			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("AllReason");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					listReasonCode.add(JObjBill.get("strReasonCode"));
					listReasonName.add(JObjBill.get("strReasonName"));
				}
			}
		}
	}

	public List funLoadItemsGroupSubGroupData()
	{
		List listDiscountCombo = new ArrayList<List>();
		try
		{

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return listDiscountCombo;
	}

	public JSONObject funFillGroupSubGroupList(ArrayList<String> arrListItemCode)
	{
		JSONObject jsonAllListsData = new JSONObject();
		StringBuilder sb = new StringBuilder();
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			List listSubGroupName = new ArrayList<>();
			List listSubGroupCode = new ArrayList<>();
			List listGroupName = new ArrayList<>();
			List listGroupCode = new ArrayList<>();
			listSubGroupName.add("--select--");
			listSubGroupCode.add("--select--");
			listGroupName.add("--select--");
			listGroupCode.add("--select--");
			boolean first = true;
			for (String test : arrListItemCode)
			{
				if (first)
				{
					sb.append("'").append(test).append("");
					first = false;
				}
				else
				{
					sb.append("','").append(test).append("");
				}
			}
			// String t1 = sb.toString()+"'";
			sb.append("'");
			if (sb.toString().trim().length() > 1)
			{

				sqlBuilder.setLength(0);
				sqlBuilder.append("select a.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName " + " from tblitemmaster a , tblsubgrouphd b,tblgrouphd c" + " where a.strItemCode IN (" + sb.toString() + ") and a.strDiscountApply='Y' " + " and a.strSubGroupCode=b.strSubGroupCode and b.strGroupCode=c.strGroupCode;");

				List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] ob = (Object[]) list.get(i);
						if (!listSubGroupCode.contains(ob[0].toString()))
						{
							listSubGroupCode.add(ob[0].toString());
							listSubGroupName.add(ob[1].toString());
						}
						if (!listGroupCode.contains(ob[2].toString()))
						{
							listGroupCode.add(ob[2].toString());
							listGroupName.add(ob[3].toString());
						}
					}
				}

			}

			Gson gson = new Gson();
			Type type = new TypeToken<List<String>>()
			{
			}.getType();
			String strSubGroupCode = gson.toJson(listSubGroupCode, type);
			String strSubGroupName = gson.toJson(listSubGroupName, type);
			String strGroupCode = gson.toJson(listGroupCode, type);
			String strGroupName = gson.toJson(listGroupName, type);

			jsonAllListsData.put("listSubGroupCode", strSubGroupCode);
			jsonAllListsData.put("listSubGroupName", strSubGroupName);
			jsonAllListsData.put("listGroupCode", strGroupCode);
			jsonAllListsData.put("listGroupName", strGroupName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			sb = null;
		}
		return jsonAllListsData;
	}

	// @RequestMapping(value = "/funCalculateTaxInSettlement", method =
	// RequestMethod.POST)
	@RequestMapping(value = "/funCalculateTaxInSettlement", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	private @ResponseBody List<clsPOSTaxDtlsOnBill> funCalculateTax(@RequestBody List listBillItem, HttpServletRequest request)
	{
		List<clsPOSTaxDtlsOnBill> listTaxDtlOnBill = new ArrayList<clsPOSTaxDtlsOnBill>();
		// if (billTransType.equalsIgnoreCase("Home Delivery"))
		// {
		// operationTypeForTax = "HomeDelivery";
		// }
		// else if (obBillItem.getTakeAwayYN().equals("Yes"))
		// {
		// operationTypeForTax = "TakeAway";
		// }
		// else
		// {
		// operationTypeForTax = "DineIn";
		// }

		// customerType=objDirectBillerBean.getStrCustomerType();
		String operationTypeForTax = "DineIn";
		clientCode = request.getSession().getAttribute("gClientCode").toString();
		posClientCode = request.getSession().getAttribute("gClientCode").toString();
		posCode = request.getSession().getAttribute("gPOSCode").toString();
		posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		JSONObject jsonSelectedItemDtl = new JSONObject();
		JSONArray jArrClass = new JSONArray();
		for (int cnt = 0; cnt < listBillItem.size(); cnt++)
		{
			Map listItemDtl = (Map) listBillItem.get(cnt);
			System.out.println(listItemDtl.get("itemName"));
			JSONObject objRows = new JSONObject();
			if (listItemDtl.get("itemCode") != null)
			{
				objRows.put("strPOSCode", posCode);
				objRows.put("strItemCode", listItemDtl.get("itemCode"));
				objRows.put("strItemName", listItemDtl.get("itemName"));
				objRows.put("dblItemQuantity", listItemDtl.get("quantity"));
				objRows.put("dblAmount", listItemDtl.get("amount"));
				objRows.put("strClientCode", clientCode);
				objRows.put("OperationType", operationTypeForTax);// operationTypeFor
																	// Tax
				objRows.put("AreaCode", "");
				objRows.put("POSDate", posDate);

				jArrClass.add(objRows);
			}

		}
		jsonSelectedItemDtl.put("TaxDtl", jArrClass);

		// call WebService

		JSONObject jObj = funCalculateTax(jsonSelectedItemDtl);
		JSONArray jArrTaxList = (JSONArray) jObj.get("listOfTax");
		String totalTaxAmt = jObj.get("totalTaxAmt").toString();
		clsPOSTaxDtlsOnBill objTaxDtl;
		JSONObject jsonTax = new JSONObject();
		for (int i = 0; i < jArrTaxList.size(); i++)
		{
			objTaxDtl = new clsPOSTaxDtlsOnBill();
			jsonTax = (JSONObject) jArrTaxList.get(i);
			objTaxDtl.setTaxName(jsonTax.get("TaxName").toString());
			objTaxDtl.setTaxAmount(Double.parseDouble(jsonTax.get("TaxAmt").toString()));
			objTaxDtl.setTaxCode(jsonTax.get("taxCode").toString());
			objTaxDtl.setTaxCalculationType(jsonTax.get("taxCalculationType").toString());
			objTaxDtl.setTaxableAmount(Double.parseDouble(jsonTax.get("taxableAmount").toString()));

			listTaxDtlOnBill.add(objTaxDtl);

		}

		return listTaxDtlOnBill;
	}

	public JSONObject funCalculateTax(JSONObject objKOTTaxData)
	{

		String taxAmt = "";
		double subTotalForTax = 0;
		double taxAmount = 0.0;
		JSONObject jsTaxDtl = new JSONObject();
		try
		{

			String posCode = "",
					areaCode = "",
					operationType = "",
					clientCode = "";

			List<clsPOSItemDtlForTax> arrListItemDtls = new ArrayList<clsPOSItemDtlForTax>();
			JSONArray mJsonArray = (JSONArray) objKOTTaxData.get("TaxDtl");
			String sql = "";
			String posDate = "";
			ResultSet rs;
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.size(); i++)
			{
				clsPOSItemDtlForTax objItemDtl = new clsPOSItemDtlForTax();
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String itemName = mJsonObject.get("strItemName").toString();
				String itemCode = mJsonObject.get("strItemCode").toString();
				System.out.println(itemName);
				double amt = Double.parseDouble(mJsonObject.get("dblAmount").toString());
				operationType = mJsonObject.get("OperationType").toString();
				posCode = mJsonObject.get("strPOSCode").toString();
				areaCode = mJsonObject.get("AreaCode").toString();
				posDate = mJsonObject.get("POSDate").toString();
				clientCode = mJsonObject.get("strClientCode").toString();
				StringBuilder sqlBuilder = new StringBuilder();
				if (areaCode.equals(""))
				{
					sqlBuilder.setLength(0);
					sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') and strClientCode='" + clientCode + "'");
					List listAreCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					if (listAreCode.size() > 0)
					{
						for (int cnt = 0; cnt < listAreCode.size(); cnt++)
						{
							Object obj = (Object) listAreCode.get(cnt);
							areaCode = (obj.toString());
						}
					}
				}

				objItemDtl.setItemCode(itemCode);
				objItemDtl.setItemName(itemName);
				objItemDtl.setAmount(amt);
				objItemDtl.setDiscAmt(0);
				objItemDtl.setDiscPer(0);
				arrListItemDtls.add(objItemDtl);
				subTotalForTax += amt;
				// tableNo=mJsonObject.get("strTableNo").toString();

			}

			Date dt = new Date();
			String date = (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
			clsPOSTaxCalculation objTaxCalculation = new clsPOSTaxCalculation();

			List<clsPOSTaxCalculationDtls> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtls, posCode, posDate, areaCode, operationType, subTotalForTax, 0.0, "");

			JSONArray jAyyTaxList = new JSONArray();
			JSONObject jsTax;
			for (int cnt = 0; cnt < arrListTaxDtl.size(); cnt++)
			{
				jsTax = new JSONObject();
				clsPOSTaxCalculationDtls obj = (clsPOSTaxCalculationDtls) arrListTaxDtl.get(cnt);
				System.out.println("Tax Dtl= " + obj.getTaxCode() + "\t" + obj.getTaxName() + "\t" + obj.getTaxAmount());
				taxAmount += obj.getTaxAmount();
				taxAmt = String.valueOf(taxAmount);
				jsTax.put("TaxName", obj.getTaxName());
				jsTax.put("TaxAmt", obj.getTaxAmount());
				jsTax.put("taxCode", obj.getTaxCode());
				jsTax.put("taxCalculationType", obj.getTaxCalculationType());
				jsTax.put("taxableAmount", obj.getTaxableAmount());

				jAyyTaxList.add(jsTax);
			}
			jsTaxDtl.put("listOfTax", jAyyTaxList);
			jsTaxDtl.put("totalTaxAmt", taxAmt);

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsTaxDtl;// Response.status(201).entity(jsTaxDtl).build();
	}

	@RequestMapping(value = "/promotionCalculate", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	private @ResponseBody Map funPromotionCalculate(@RequestBody List listItmeDtl, HttpServletRequest request) throws Exception
	{

		String areaCode = "";
		double dblDiscountAmt = 0.0;
		StringBuilder sqlBuilder = new StringBuilder();
		if (areaCode.equals(""))
		{
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') and strClientCode='" + clientCode + "'");
			List listAreCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listAreCode.size() > 0)
			{
				for (int cnt = 0; cnt < listAreCode.size(); cnt++)
				{
					Object obj = (Object) listAreCode.get(cnt);
					areaCode = (obj.toString());
				}
			}
		}
		Map mapResult = new HashMap();
		String checkPromotion = "N";
		List<clsPOSBillSettlementBean> listofItems = new ArrayList<clsPOSBillSettlementBean>();
		for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
		{
			Map listItemDtl = (Map) listItmeDtl.get(cnt);
			System.out.println(listItemDtl.get("itemName"));
			JSONObject objRows = new JSONObject();
			if (listItemDtl.get("itemCode") != null)
			{
				clsPOSBillSettlementBean objWebPOSBillSettlementBean = new clsPOSBillSettlementBean();
				objWebPOSBillSettlementBean.setStrItemCode(listItemDtl.get("itemCode").toString());
				objWebPOSBillSettlementBean.setStrItemName(listItemDtl.get("itemName").toString());
				objWebPOSBillSettlementBean.setDblQuantity(Double.parseDouble(listItemDtl.get("quantity").toString()));
				objWebPOSBillSettlementBean.setDblAmount(Double.parseDouble(listItemDtl.get("amount").toString()));
				objWebPOSBillSettlementBean.setStrSubGroupCode(listItemDtl.get("strSubGroupCode").toString());
				objWebPOSBillSettlementBean.setStrGroupCode(listItemDtl.get("strGroupcode").toString());
				objWebPOSBillSettlementBean.setStrItemCode(listItemDtl.get("itemCode").toString());
				listofItems.add(objWebPOSBillSettlementBean);

			}

		}
		// Map<String,
		// clsPOSPromotionItems>objPromotion=objUtility.funCalculatePromotions("DirectBiller",
		// "", "", new ArrayList(),"",areaCode,"",request,listofItems);

		Map<String, clsPOSPromotionItems> hmPromoItemDtl = null;
		boolean flgApplyPromoOnBill = false;
		String gActivePromotions = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gActivePromotions");
		String gPopUpToApplyPromotionsOnBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPopUpToApplyPromotionsOnBill");
		if (gActivePromotions.equalsIgnoreCase("Y"))
		{
			hmPromoItemDtl = objUtility.funCalculatePromotions("DirectBiller", "", "", new ArrayList(), "", areaCode, "", request, listofItems);
			if (null != hmPromoItemDtl)
			{
				if (hmPromoItemDtl.size() > 0)
				{
					checkPromotion = "Y";
					flgApplyPromoOnBill = true;

				}
			}
		}

		// (String transType, String billFromKOTsList, String billNo,
		// List<clsBillDtl> listOfSplitedGridItems,String tableNo,String
		// areaCode,String voucherNo,HttpServletRequest
		// request,List<clsWebPOSBillSettlementBean> listItemDtl ) throws
		// Exception

		Map<String, clsPOSPromotionItems> hmPromoItemsToDisplay = new HashMap<String, clsPOSPromotionItems>();

		List<clsPOSItemDtlForTax> arrListItemDtls = new ArrayList<clsPOSItemDtlForTax>();
		List<clsPOSBillSettlementBean> listOfPromotionItem = new ArrayList<clsPOSBillSettlementBean>();
		Map<String, clsPOSBillSettlementBean> mapPromoItemDisc = new HashMap<String, clsPOSBillSettlementBean>();
		// directbiller

		for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
		{
			Map listItemDtl = (Map) listItmeDtl.get(cnt);
			double freeAmount = 0.00;
			String item = listItemDtl.get("itemName").toString().trim();
			double quantity = Double.parseDouble(listItemDtl.get("quantity").toString());
			double amount = Double.parseDouble(listItemDtl.get("amount").toString());
			String groupCode = listItemDtl.get("strGroupcode").toString().trim();
			String subGroupCode = listItemDtl.get("strSubGroupCode").toString().trim();

			double rate = Double.parseDouble(listItemDtl.get("rate").toString().trim());
			String itemCode = listItemDtl.get("itemCode").toString();

			clsPOSItemDtlForTax objItemDtlForTax = new clsPOSItemDtlForTax();
			objItemDtlForTax.setItemCode(itemCode);
			objItemDtlForTax.setItemName(item);
			objItemDtlForTax.setAmount(0);
			objItemDtlForTax.setDiscAmt(0);

			if (gActivePromotions.equalsIgnoreCase("Y") && flgApplyPromoOnBill)
			{
				if (null != hmPromoItemDtl)
				{
					if (hmPromoItemDtl.containsKey(itemCode))
					{
						if (null != hmPromoItemDtl.get(itemCode))
						{
							clsPOSPromotionItems objPromoItemsDtl = hmPromoItemDtl.get(itemCode);
							if (objPromoItemsDtl.getPromoType().equals("ItemWise"))
							{
								double freeQty = objPromoItemsDtl.getFreeItemQty();
								if (freeQty > 0)
								{
									freeAmount = freeAmount + (rate * freeQty);
									amount = amount - freeAmount;
									hmPromoItem.put(itemCode, objPromoItemsDtl);
									hmPromoItemsToDisplay.put(itemCode + "!" + item, objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);
								}
							}
							else if (objPromoItemsDtl.getPromoType().equals("Discount"))
							{
								double discA = 0;
								double discP = 0;
								if (objPromoItemsDtl.getDiscType().equals("Value"))
								{
									discA = objPromoItemsDtl.getDiscAmt();
									discP = (discA / amount) * 100;
									hmPromoItem.put(itemCode, objPromoItemsDtl);
									hmPromoItemsToDisplay.put(itemCode + "!" + item, objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);

									clsPOSBillSettlementBean objItemPromoDiscount = new clsPOSBillSettlementBean();
									objItemPromoDiscount.setStrItemCode(itemCode);
									objItemPromoDiscount.setStrItemName(item);
									objItemPromoDiscount.setDblDiscountAmt(discA);
									objItemPromoDiscount.setDblDiscountPer(discP);
									objItemPromoDiscount.setDblAmount(amount);

									mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
								}
								else
								{
									discP = objPromoItemsDtl.getDiscPer();
									discA = (discP / 100) * amount;
									hmPromoItem.put(itemCode, objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);
									clsPOSBillSettlementBean objItemPromoDiscount = new clsPOSBillSettlementBean();
									objItemPromoDiscount.setStrItemCode(itemCode);
									objItemPromoDiscount.setStrItemName(item);
									objItemPromoDiscount.setDblDiscountAmt(discA);
									objItemPromoDiscount.setDblDiscountPer(discP);
									objItemPromoDiscount.setDblAmount(amount);

									mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
								}
							}
						}
					}
				}
			}

			// temp_Total += amount;
			objItemDtlForTax.setAmount(objItemDtlForTax.getAmount() + amount);
			arrListItemDtls.add(objItemDtlForTax);

			// listItemCode.add(itemCode);
			// hmItemList.put(item, itemCode);

			if (gActivePromotions.equalsIgnoreCase("Y") && flgApplyPromoOnBill)
			{
				double discAmt = 0;
				double discPer = 0;
				if (mapPromoItemDisc.containsKey(itemCode))
				{

					clsPOSBillSettlementBean objItemPromoDiscount = mapPromoItemDisc.get(itemCode);
					// discAmt = objItemPromoDiscount.getDiscountAmt();
					// discPer = objItemPromoDiscount.getDiscountPer();

					objItemPromoDiscount.setDblQuantity(quantity);
					objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
					objItemPromoDiscount.setStrGroupCode(groupCode);
					objItemPromoDiscount.setDblRate(rate);
					listOfPromotionItem.add(objItemPromoDiscount);
					// funFillListForItemRow(item, quantity, amount, itemCode,
					// discAmt, discPer);
					dblDiscountAmt = dblDiscountAmt + discAmt;
					// txtDiscountAmt.setText(String.valueOf(dblDiscountAmt));
					// txtDiscountPer.setText(String.valueOf(discPer));
				}
				else
				{

					clsPOSBillSettlementBean objItemPromoDiscount = new clsPOSBillSettlementBean();
					objItemPromoDiscount.setStrItemCode(itemCode);
					objItemPromoDiscount.setStrItemName(item);
					objItemPromoDiscount.setDblDiscountAmt(discAmt);
					objItemPromoDiscount.setDblDiscountPer(discPer);
					objItemPromoDiscount.setDblAmount(amount);
					objItemPromoDiscount.setDblQuantity(quantity);
					objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
					objItemPromoDiscount.setStrGroupCode(groupCode);
					objItemPromoDiscount.setDblRate(rate);
					listOfPromotionItem.add(objItemPromoDiscount);
					// funFillListForItemRow(item, quantity, amount, itemCode,
					// discAmt, discPer);
					// dblDiscountAmt = dblDiscountAmt + discAmt;
					// txtDiscountAmt.setText(String.valueOf(dblDiscountAmt));
					// txtDiscountPer.setText(String.valueOf(discPer));
				}

				// Iterator<Map.Entry<String, clsWebPOSBillSettlementBean>>
				// itPromoDisc = mapPromoItemDisc.entrySet().iterator();
				// while (clsWebPOSBillSettlementBean listOfPromotionItem)
				// {
				// clsWebPOSBillSettlementBean objItemDtl =
				// itPromoDisc.next().getValue();
				// if (mapPromoItemDisc.containsKey(objItemDtl.getItemCode()))
				// {
				// mapBillDiscDtl.put("ItemWise!" + objItemDtl.getItemName() +
				// "!P", new clsBillDiscountDtl("Promotion Discount", "R01",
				// objItemDtl.getDiscountPercentage(),
				// objItemDtl.getDiscountAmount(), objItemDtl.getAmount()));
				// }
				// }
			}
			else
			{
				checkPromotion = "N";
				clsPOSBillSettlementBean objItemPromoDiscount = new clsPOSBillSettlementBean();
				objItemPromoDiscount.setStrItemCode(itemCode);
				objItemPromoDiscount.setStrItemName(item);
				objItemPromoDiscount.setDblDiscountAmt(0);
				objItemPromoDiscount.setDblDiscountPer(0);
				objItemPromoDiscount.setDblAmount(amount);
				objItemPromoDiscount.setDblQuantity(quantity);
				objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
				objItemPromoDiscount.setStrGroupCode(groupCode);
				objItemPromoDiscount.setDblRate(rate);
				listOfPromotionItem.add(objItemPromoDiscount);
				// funFillListForItemRow(item, quantity, amount, itemCode, 0,
				// 0);
			}
			//
			// Object[] rows =
			// {
			// item, df.format(quantity), df.format(amount)
			// };
			// dm.addRow(rows);
		}

		mapResult.put("listOfPromotionItem", listOfPromotionItem);
		mapResult.put("checkPromotion", checkPromotion);
		return mapResult;
	}

	@RequestMapping(value = "/funCalculateDeliveryChages", method = RequestMethod.POST)
	public @ResponseBody double funCalclulateDeliveryCharges(@RequestParam("buildingCode") String buildingCode, @RequestParam("gCustomerCode") String gCustomerCode, @RequestParam("totalBillAmount") double totalBillAmount)
	{
		double deliverycharges = objUtility.funCalculateDeliveryChages(buildingCode, totalBillAmount, gCustomerCode, clientCode, posCode);
		return deliverycharges;
	}

	@RequestMapping(value = "/actionBillSettlement", method = RequestMethod.POST)
	public ModelAndView printBill(@ModelAttribute("command") clsPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest request) throws Exception
	{
		String clientCode = "",
				POSCode = "",
				POSDate = "",
				userCode = "",
				posClientCode = "";

		clientCode = request.getSession().getAttribute("gClientCode").toString();
		POSCode = request.getSession().getAttribute("gPOSCode").toString();
		POSDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		String split = POSDate;
		String billDateTime = split;
		String custCode = "";

		Date dt = new Date();
		String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
		String dateTime = POSDate + " " + currentDateTime.split(" ")[1];

		int totalPAXNo = 0;
		String tableNo = "";

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);

		boolean isBillSeries = false;
		if (clsPOSGlobalFunctionsController.hmPOSSetupValues.get("strEnableBillSeries").toString().equalsIgnoreCase("Y"))
		{
			isBillSeries = true;
		}

		/**
		 * Filling KOT item details in a list
		 */
		List<clsPOSKOTItemDtl> listOfWholeKOTItemDtl = new ArrayList<clsPOSKOTItemDtl>();
		for (clsPOSItemsDtlsInBill objDirectBillerItem : objBean.getListOfBillItemDtl())
		{
			String itemCode = objDirectBillerItem.getItemCode();
			String itemName = objDirectBillerItem.getItemName();

			if (itemName.contains("-->"))
			{
				String code[] = itemCode.split("!");
				itemCode = code[0] + "" + code[1];
			}

			clsPOSKOTItemDtl objBillDtl = new clsPOSKOTItemDtl();

			objBillDtl.setStrItemCode(itemCode);
			objBillDtl.setStrItemName(itemName);
			objBillDtl.setStrAdvBookingNo("");
			objBillDtl.setDblRate(objDirectBillerItem.getRate());
			objBillDtl.setDblItemQuantity(objDirectBillerItem.getQuantity());
			objBillDtl.setDblAmount(objDirectBillerItem.getAmount());
			objBillDtl.setDblTaxAmount(0);
			objBillDtl.setDteBillDate(posDate);
			objBillDtl.setStrKOTNo("");
			objBillDtl.setStrCounterCode("");
			objBillDtl.setStrOrderProcessTime("00:00:00");
			objBillDtl.setStrDataPostFlag("N");
			objBillDtl.setStrMMSDataPostFlag("N");
			objBillDtl.setStrManualKOTNo("");
			boolean tdYN = objDirectBillerItem.isTdhYN();
			if (tdYN)
			{
				objBillDtl.setStrTdhYN("Y");
			}
			else
			{
				objBillDtl.setStrTdhYN("N");
			}
			objBillDtl.setStrPromoCode(objDirectBillerItem.getPromoCode());
			objBillDtl.setStrCounterCode("");
			objBillDtl.setStrWaiterNo("");
			objBillDtl.setStrSequenceNo(objDirectBillerItem.getSeqNo());
			objBillDtl.setStrOrderPickupTime("00:00:00");

			objBillDtl.setDblDiscAmt(objDirectBillerItem.getDiscountAmt() * objDirectBillerItem.getQuantity());
			objBillDtl.setDblDiscPer(objDirectBillerItem.getDiscountPer());

			listOfWholeKOTItemDtl.add(objBillDtl);

		}

		/**
		 * Bill series code
		 */

		Map<String, List<clsPOSKOTItemDtl>> mapBillSeries = null;
		List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl = new ArrayList<clsPOSBillSeriesBillDtl>();

		if (isBillSeries)
		{
			if ((mapBillSeries = objBillingAPI.funGetBillSeriesList(listOfWholeKOTItemDtl, POSCode)).size() > 0)
			{
				if (mapBillSeries.containsKey("NoBillSeries"))
				{
					result.addError(new ObjectError("BillSeries", "Please Create Bill Series"));

					objBillingAPI.funUpdateTableStatus(tableNo, "Occupied");

					return new ModelAndView("/frmWebPOSBilling.html");
				}
				// to calculate PAX per bill if there is a bill series or bill splited
				Map<Integer, Integer> mapPAXPerBill = objBillingAPI.funGetPAXPerBill(totalPAXNo, mapBillSeries.size());

				Iterator<Map.Entry<String, List<clsPOSKOTItemDtl>>> billSeriesIt = mapBillSeries.entrySet().iterator();
				int billCount = 0;
				while (billSeriesIt.hasNext())
				{
					Map.Entry<String, List<clsPOSKOTItemDtl>> billSeriesEntry = billSeriesIt.next();
					String key = billSeriesEntry.getKey();
					List<clsPOSKOTItemDtl> listOfItemsBillSeriesWise = billSeriesEntry.getValue();

					int intBillSeriesPaxNo = 0;
					if (mapPAXPerBill.containsKey(billCount))
					{
						intBillSeriesPaxNo = mapPAXPerBill.get(billCount);
					}

					String billSeriesBillNo = objBillingAPI.funGetBillSeriesBillNo(key, POSCode);

					/* To save billseries bill */
					objBillingAPI.funSaveBill(isBillSeries, key, listBillSeriesBillDtl, billSeriesBillNo, listOfItemsBillSeriesWise, objBean, request, hmPromoItem);

					billCount++;
				}
				boolean flagBillForItems = false;
				// clear temp kot table
				if (flagBillForItems)
				{
					// objBillingAPI.funUpdateKOTTempTable();
				}
				else
				{
					objBillingAPI.funClearRTempTable(tableNo, POSCode);
				}

				// save bill series bill detail
				for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
				{
					clsPOSBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
					String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
					double grandTotal = objBillSeriesBillDtl.getDblGrandTotal();

					String sqlInsertBillSeriesDtl = "insert into tblbillseriesbilldtl " + "(strPOSCode,strBillSeries,strHdBillNo,strDtlBillNos,dblGrandTotal,strClientCode,strDataPostFlag" + ",strUserCreated,dteCreatedDate,strUserEdited,dteEditedDate,dteBillDate) " + "values ('" + POSCode + "','" + objBillSeriesBillDtl.getStrBillSeries() + "'" + ",'" + hdBillNo + "','" + objBillingAPI.funGetBillSeriesDtlBillNos(listBillSeriesBillDtl, hdBillNo) + "'" + ",'" + grandTotal + "'" + ",'" + clientCode + "','N','" + userCode + "'" + ",'" + currentDateTime + "','" + userCode + "'" + ",'" + currentDateTime + "','" + POSDate + "')";
					objBaseService.funExecuteUpdate(sqlInsertBillSeriesDtl, "sql");

					sbSql.setLength(0);
					sbSql.append("select * " + "from tblbillcomplementrydtl a " + "where a.strBillNo='" + hdBillNo + "' " + "and date(a.dteBillDate)='" + POSDate + "' " + "and a.strType='Complimentary' ");
					List listCompli = objBaseServiceImpl.funGetList(sbSql, "sql");
					if (listCompli != null && listCompli.size() > 0)
					{
						String sqlUpdate = "update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + hdBillNo + "' " + " and strPOSCode='" + POSCode + "' " + " and date(dteBillDate)='" + POSDate + "' ";
						objBaseService.funExecuteUpdate(sqlUpdate, "sql");
					}

				}

				for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
				{
					clsPOSBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
					String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
					boolean flgHomeDelPrint = objBillSeriesBillDtl.isFlgHomeDelPrint();

					/* printing bill............... */
					objTextFileGeneration.funGenerateAndPrintBill(hdBillNo, POSCode, clientCode);
				}

			}
		}
		else // No Bill Series
		{
			String voucherNo = objBillingAPI.funGenerateBillNo(POSCode);

			/* To save normal bill */
			objBillingAPI.funSaveBill(isBillSeries, "", listBillSeriesBillDtl, voucherNo, listOfWholeKOTItemDtl, objBean, request, hmPromoItem);

			/* printing bill............... */
			objTextFileGeneration.funGenerateAndPrintBill(voucherNo, POSCode, clientCode);
		}

		Map<String, Object> model = new HashMap<String, Object>();

		// return new ModelAndView("frmWebPOSBilling","command",obBillSettlementBean);
		return  new ModelAndView("redirect:/frmWebPOSBilling.html?saddr=1");

	}

	// generate bill no.
	private String funGenerateBillNo()
	{
		String voucherNo = "";
		try
		{
			long code = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strBillNo from tblstorelastbill where strPosCode='" + posCode + "'");
			List listItemDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listItemDtl != null && listItemDtl.size() > 0)
			{

				Object objItemDtl = (Object) listItemDtl.get(0);

				code = Math.round(Double.parseDouble(objItemDtl.toString()));
				code = code + 1;

				voucherNo = posCode + String.format("%05d", code);
				objBaseServiceImpl.funExecuteUpdate("update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + posCode + "'", "sql");
			}
			else
			{
				voucherNo = posCode + "00001";
				sqlBuilder.setLength(0);
				objBaseServiceImpl.funExecuteUpdate("insert into tblstorelastbill values('" + posCode + "','1')", "sql");
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

	private List funSaveBillDiscountDetail(String voucherNo, clsPOSBillSettlementBean objBean, String dateTime)
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
				objDiscModel.setStrPOSCode(posCode);
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

			// if (_subTotal == 0.00)
			// {
			// }
			// else
			// {
			// finalDiscPer = (totalDiscAmt / _subTotal) * 100;
			// }
			// dblDiscountAmt = totalDiscAmt;
			// dblDiscountPer = finalDiscPer;
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
			// objSettleModel.setStrBillNo(
			// objBillSettlementDtl.getStrBillNo());
			objSettleModel.setStrSettlementCode(objBillSettlementDtl.getStrSettelmentCode());
			objSettleModel.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt());
			objSettleModel.setDblPaidAmt(objBillSettlementDtl.getDblPaidAmt());
			objSettleModel.setStrExpiryDate("");
			objSettleModel.setStrCardName("");
			objSettleModel.setStrRemark("");
			// objSettleModel.setStrClientCode(objBillSettlementDtl.getStrClientCode());
			objSettleModel.setStrCustomerCode("");
			objSettleModel.setDblActualAmt(objBillSettlementDtl.getDblActualAmt());
			objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt());
			objSettleModel.setStrGiftVoucherCode("");
			objSettleModel.setStrDataPostFlag("");
			// objSettleModel.setDteBillDate(dtCurrentDate);
			objSettleModel.setStrFolioNo("");
			objSettleModel.setStrRoomNo("");
			listBillSettlementDtlModel.add(objSettleModel);
			// objBaseService.funSave(objSettleModel);

		}
		return listBillSettlementDtlModel;
		// StringBuilder sb1 = new StringBuilder(sqlInsertBillSettlementDtl);
		// int index1 = sb1.lastIndexOf(",");
		// sqlInsertBillSettlementDtl = sb1.delete(index1,
		// sb1.length()).toString();

	}

	public List funInsertIntoPromotion(String voucherNo, clsPOSBillSettlementBean objBean)
	{

		List<clsBillPromotionDtlModel> listBillPromotionDtlModel = new ArrayList<clsBillPromotionDtlModel>();
		for (clsPOSItemsDtlsInBill objBillDtl : objBean.getListOfBillItemDtl())
		{
			double freeQty = 0;
			if (null != hmPromoItem)
			{
				if (null != hmPromoItem.get(objBillDtl.getItemCode()))
				{
					clsPOSPromotionItems objPromoItemDtl = hmPromoItem.get(objBillDtl.getItemCode());
					if (objPromoItemDtl.getPromoType().equals("ItemWise"))
					{
						freeQty = objPromoItemDtl.getFreeItemQty();
						double freeAmt = freeQty * objBillDtl.getRate();

						clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
						objPromortion.setStrItemCode(objBillDtl.getItemCode());
						objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
						objPromortion.setDblAmount(freeAmt);
						objPromortion.setDblDiscountAmt(0);
						objPromortion.setDblDiscountPer(0);
						objPromortion.setDblQuantity(freeQty);
						objPromortion.setDblRate(objBillDtl.getRate());
						objPromortion.setStrDataPostFlag("N");
						objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
						objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

						listBillPromotionDtlModel.add(objPromortion);

						hmPromoItem.remove(objBillDtl.getItemCode());
					}
					else if (objPromoItemDtl.getPromoType().equals("Discount"))
					{
						if (objPromoItemDtl.getDiscType().equals("Value"))
						{
							double amount = freeQty * objBillDtl.getRate();
							double discAmt = objPromoItemDtl.getDiscAmt();

							clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
							objPromortion.setStrItemCode(objBillDtl.getItemCode());
							objPromortion.setStrPromotionCode("");
							objPromortion.setDblAmount(amount);
							objPromortion.setDblDiscountAmt(discAmt);
							objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
							objPromortion.setDblQuantity(0);
							objPromortion.setDblRate(objBillDtl.getRate());
							objPromortion.setStrDataPostFlag("N");
							objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
							objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

							listBillPromotionDtlModel.add(objPromortion);

							hmPromoItem.remove(objBillDtl.getItemCode());
						}
						else
						{
							double totalAmt = objBillDtl.getQuantity() * objBillDtl.getRate();
							double discAmt = totalAmt - (totalAmt * (objPromoItemDtl.getDiscPer() / 100));

							clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
							objPromortion.setStrItemCode(objBillDtl.getItemCode());
							objPromortion.setStrPromotionCode("");
							objPromortion.setDblAmount(totalAmt);
							objPromortion.setDblDiscountAmt(discAmt);
							objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
							objPromortion.setDblQuantity(0);
							objPromortion.setDblRate(objBillDtl.getRate());
							objPromortion.setStrDataPostFlag("N");
							objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
							objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

							listBillPromotionDtlModel.add(objPromortion);

							hmPromoItem.remove(objBillDtl.getItemCode());
						}
					}
				}
			}
		}

		return listBillPromotionDtlModel;
	}

	public void funSaveHomeDelivery(String voucherNo, clsPOSBillSettlementBean objBean) throws Exception
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
		String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;//
		// String sql =
		// "select
		// a.strCustomerCode,a.strCustomerName,a.longMobileNo,a.strBuldingCode,a.strCustAddress
		// as strHomeAddress "
		// + ",a.strStreetName,a.strLandmark,a.intPinCode,a.strCity,a.strState "
		// +
		// ",a.strOfficeBuildingCode,a.strOfficeBuildingName as
		// strOfficeAddress,a.strOfficeStreetName,a.strOfficeLandmark,a.intPinCode "
		// + ",a.strOfficeCity,a.strOfficeState "
		// + ",a.strTempAddress,a.strTempStreet,a.strTempLandmark "
		// + "from tblcustomermaster a "
		// + "where longMobileNo like '%" + objBean.getSt + "%' ";
		if (objBean.getStrDeliveryBoyCode() != null)
		{
			clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
			objHomeDeliveryHdModel.setStrBillNo(voucherNo);
			objHomeDeliveryHdModel.setStrCustomerCode(objBean.getStrCustomerCode());
			objHomeDeliveryHdModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
			objHomeDeliveryHdModel.setDteDate(posDate);
			objHomeDeliveryHdModel.setTmeTime(currentTime);
			objHomeDeliveryHdModel.setStrPOSCode(posCode);
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
		}
		else
		{
			// String sql_tblhomedelivery =
			// "insert into tblhomedelivery(strBillNo,strCustomerCode,dteDate,tmeTime"
			// +
			// ",strPOSCode,strCustAddressLine1,strCustAddressLine2,strCustAddressLine3,strCustAddressLine4"
			// + ",strCustCity,strClientCode,dblHomeDeliCharge)"
			// + " values('" + voucherNo + "','" + custCode + "','"
			// + clsGlobalVarClass.gPOSDateForTransaction + "','" + currentTime
			// + "','"
			// + clsGlobalVarClass.gPOSCode + "','" + custAddType + "','',''"
			// + ",'','','" + clsGlobalVarClass.gClientCode + "'," +
			// _deliveryCharge + ")";

			clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
			objHomeDeliveryHdModel.setStrBillNo(voucherNo);
			objHomeDeliveryHdModel.setStrCustomerCode(objBean.getStrCustomerCode());
			objHomeDeliveryHdModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
			objHomeDeliveryHdModel.setDteDate(posDate);
			objHomeDeliveryHdModel.setTmeTime(currentTime);
			objHomeDeliveryHdModel.setStrPOSCode(posCode);
			objHomeDeliveryHdModel.setStrCustAddressLine1("");
			objHomeDeliveryHdModel.setStrCustAddressLine2("");
			objHomeDeliveryHdModel.setStrCustAddressLine3("");
			objHomeDeliveryHdModel.setStrCustAddressLine4("");
			objHomeDeliveryHdModel.setStrCustCity("");
			objHomeDeliveryHdModel.setStrClientCode(clientCode);
			objHomeDeliveryHdModel.setDblHomeDeliCharge(objBean.getDblDeliveryCharges());
			objHomeDeliveryHdModel.setDblLooseCashAmt(0);

			objBaseServiceImpl.funSave(objHomeDeliveryHdModel);
		}
		// //Saving for home delivery Detail data

		clsHomeDeliveryDtlModel objDtlModel = new clsHomeDeliveryDtlModel();
		objDtlModel.setStrBillNo(voucherNo);
		objDtlModel.setDblDBIncentives(0);
		objDtlModel.setDteBillDate(posDate);
		objDtlModel.setStrClientCode(clientCode);
		objDtlModel.setStrDataPostFlag("N");
		objDtlModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
		objDtlModel.setStrSettleYN("N");
		objBaseServiceImpl.funSave(objDtlModel);

	}

}
