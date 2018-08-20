package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDetails;
import com.sanguine.webpos.bean.clsPOSCustomerDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSDeliveryBoyMasterBean;
import com.sanguine.webpos.bean.clsPOSDirectBillerBean;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSModifiersOnItem;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillModifierDtlModel;
import com.sanguine.webpos.model.clsHomeDeliveryHdModel;
import com.sanguine.webpos.model.clsOrderDtlModel;
import com.sanguine.webpos.model.clsOrderHdModel;
import com.sanguine.webpos.model.clsOrderHdModel_ID;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSCallCenterController
{

	@Autowired
	private clsGlobalFunctions objGlobal;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	public List<clsPOSItemsDtlsInBill> listItemsDtlInBill;
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	@Autowired
	clsPOSUtilityController objUtility;

	public clsPOSBillDetails obBillItem;
	private HashMap<Object, Object> mapPOSName;
	private String selectedPOSCode;
	private String selectedCustomerCode;
	private String selectedCustMobileNo;
	private String selectedCustomerName;
	private String selectedBuildingName;

	// Open POSDirectBiller
	@RequestMapping(value = "/frmCallCenter", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
			request.getSession().setAttribute("customerMobile", ""); // mobile
																		// no
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		obBillItem = new clsPOSBillDetails();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posClientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		String userCode = request.getSession().getAttribute("gUserCode").toString();

		// direct biller model attribute
		clsPOSDirectBillerBean objDirectBillerBean = new clsPOSDirectBillerBean();

		// JSONObject jObj =
		// objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL
		// + "/WebPOSTransactions/funGetItemPricingDtl?posCode=" + posCode +
		// "&clientCode=" + clientCode + "&posDate=" + posDate);
		// JSONArray jsonArrForDirectBillerMenuItemPricing = (JSONArray)
		// jObj.get("MenuItemPricingDtl");

		JSONArray jsonArrForDirectBillerMenuItemPricing = new JSONArray();
		objDirectBillerBean.setJsonArrForDirectBillerMenuItemPricing(jsonArrForDirectBillerMenuItemPricing);

		// String posURL = clsPOSGlobalFunctionsController.POSWSURL +
		// "/clsMakeKOTController/funGetMenuHeads"
		// + "?posCode=" + posCode + "&userCode=" + userCode;
		// jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
		// JSONArray jsonArrForDirectBillerMenuHeads = (JSONArray)
		// jObj.get("MenuHeads");

		JSONArray jsonArrForDirectBillerMenuHeads = new JSONArray();
		objDirectBillerBean.setJsonArrForDirectBillerMenuHeads(jsonArrForDirectBillerMenuHeads);

		String posURL = clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funGetButttonList"

				+ "?transName=DirectBiller&posCode=" + posCode + "&posClientCode=" + clientCode;

		// + "?transName=DirectBiller&posCode="+ posCode+"&posClientCode="+
		// posClientCode;

		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);

		JSONArray jsonArrForDirectBillerFooterButtons = (JSONArray) jObj.get("buttonList");
		objDirectBillerBean.setJsonArrForDirectBillerFooterButtons(jsonArrForDirectBillerFooterButtons);

		// jObj =
		// objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL
		// + "/clsMakeKOTController/funPopularItem?posCode=" + posCode +
		// "&clientCode=" + clientCode + "&posDate=" + posDate);
		// JSONArray jsonArrForPopularItems = (JSONArray)
		// jObj.get("PopularItems");

		JSONArray jsonArrForPopularItems = new JSONArray();
		objDirectBillerBean.setJsonArrForPopularItems(jsonArrForPopularItems);

		model.put("urlHits", urlHits);
		model.put("billNo", "");
		model.put("billDate", posDate.split("-")[2] + "-" + posDate.split("-")[1] + "-" + posDate.split("-")[0]);
		model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));

		// function to get all POS list
		JSONArray jPOSArr = objPOSGlobalFunctionsController.funGetAllPOSForMaster(clientCode);
		mapPOSName = new HashMap<>();
		mapPOSName.put("", "");
		for (int cnt = 0; cnt < jPOSArr.size(); cnt++)
		{
			JSONObject jObjPOS = (JSONObject) jPOSArr.get(cnt);
			mapPOSName.put(jObjPOS.get("strPosCode").toString(), jObjPOS.get("strPosName").toString());
		}
		model.put("mapPOSName", mapPOSName);

		return new ModelAndView("frmCallCenter", "command", objDirectBillerBean);

	}

	@RequestMapping(value = "/funLoadMenu", method = RequestMethod.GET)
	public ModelAndView funLoadMenu(Map<String, Object> model, HttpServletRequest request)
	{
		String urlHits = "1";
		try
		{
			// direct biller model attribute
			clsPOSDirectBillerBean objDirectBillerBean = new clsPOSDirectBillerBean();

			selectedPOSCode = request.getParameter("posCode").toString();
			selectedCustomerCode = request.getParameter("customerCode").toString();
			selectedCustMobileNo = request.getParameter("mobileNo").toString();
			selectedCustomerName = request.getParameter("customerName").toString();
			selectedBuildingName = request.getParameter("buildingName").toString();

			String homeDeliveryAddress = "Home";
			if (request.getSession().getAttribute("homeDeliveryAddress") != null)
			{
				homeDeliveryAddress = request.getSession().getAttribute("homeDeliveryAddress").toString();
			}
			String buildingName = "";
			if (request.getSession().getAttribute("gAreaName") != null)
			{
				buildingName = request.getSession().getAttribute("gAreaName").toString();
			}

			model.put("Customer", selectedCustomerName);
			model.put("selectedCustomerCode", selectedCustomerCode);
			model.put("selectedCustomerName", selectedCustomerName);
			model.put("selectedCustMobileNo", selectedCustMobileNo);
			model.put("selectedBuildingName", buildingName);

			objDirectBillerBean.setStrCustomerCode(selectedCustomerCode);
			objDirectBillerBean.setStrCustomerName(selectedCustomerName);
			objDirectBillerBean.setStrCustMobileNo(selectedCustMobileNo);
			objDirectBillerBean.setStrHomeDeliveryAddress(homeDeliveryAddress);

			// no

			obBillItem = new clsPOSBillDetails();
			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			String posClientCode = request.getSession().getAttribute("gClientCode").toString();
			String posCode = request.getSession().getAttribute("gPOSCode").toString();
			String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			String userCode = request.getSession().getAttribute("gUserCode").toString();

			JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL + "/WebPOSTransactions/funGetItemPricingDtl?posCode=" + selectedPOSCode + "&clientCode=" + clientCode + "&posDate=" + posDate);
			JSONArray jsonArrForDirectBillerMenuItemPricing = (JSONArray) jObj.get("MenuItemPricingDtl");
			objDirectBillerBean.setJsonArrForDirectBillerMenuItemPricing(jsonArrForDirectBillerMenuItemPricing);

			String posURL = clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funGetMenuHeads"
					+ "?posCode=" + selectedPOSCode + "&userCode=" + userCode;
			jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
			JSONArray jsonArrForDirectBillerMenuHeads = (JSONArray) jObj.get("MenuHeads");

			objDirectBillerBean.setJsonArrForDirectBillerMenuHeads(jsonArrForDirectBillerMenuHeads);

			posURL = clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funGetButttonList"

					+ "?transName=DirectBiller&posCode=" + selectedPOSCode + "&posClientCode=" + clientCode;

			// + "?transName=DirectBiller&posCode="+ posCode+"&posClientCode="+
			// posClientCode;

			jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);

			JSONArray jsonArrForDirectBillerFooterButtons = (JSONArray) jObj.get("buttonList");
			objDirectBillerBean.setJsonArrForDirectBillerFooterButtons(jsonArrForDirectBillerFooterButtons);

			jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funPopularItem?posCode=" + selectedPOSCode + "&clientCode=" + clientCode + "&posDate=" + posDate);

			JSONArray jsonArrForPopularItems = (JSONArray) jObj.get("PopularItems");

			objDirectBillerBean.setJsonArrForPopularItems(jsonArrForPopularItems);

			model.put("urlHits", urlHits);
			model.put("billNo", "");
			model.put("billDate", posDate.split("-")[2] + "-" + posDate.split("-")[1] + "-" + posDate.split("-")[0]);
			model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
			model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
			model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));

			Map mapSelected = new HashMap<>();
			if (mapPOSName.containsKey(selectedPOSCode))
			{
				String selectedPOSName = mapPOSName.get(selectedPOSCode).toString();

				mapSelected.put(selectedPOSCode, selectedPOSName);

				model.put("mapPOSName", mapSelected);
			}

			return new ModelAndView("frmCallCenter", "command", objDirectBillerBean);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * here get direct biller bean data and forward to settlement Global in
	 * billItemBean
	 * 
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "/actionCallCenter", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSDirectBillerBean objBean, BindingResult result, HttpServletRequest req) throws Exception
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		String posClientCode = req.getSession().getAttribute("gClientCode").toString();
		String posCode = req.getSession().getAttribute("gPOSCode").toString();
		String posDate = req.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		String userCode = req.getSession().getAttribute("gUserCode").toString();

		String mobileNo = objBean.getStrCustMobileNo();

		String homeDeliveryAddress = req.getSession().getAttribute("homeDeliveryAddress").toString();
		if (homeDeliveryAddress == null || homeDeliveryAddress.isEmpty())
		{
			homeDeliveryAddress = "Home";
		}

		String posURL = clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funGetCustomerAddress"
				+ "?strMobNo=" + mobileNo;

		JSONObject jObj = objGlobal.funGETMethodUrlJosnObjectData(posURL);

		String addressLine1 = "", addressLine2 = "", addressLine3 = "", addressLine4 = "", addressCity = "";
		String customerName = jObj.get("strCustomerName").toString();

		addressLine1 = homeDeliveryAddress;

		if (homeDeliveryAddress.equalsIgnoreCase("Home"))
		{
			addressLine2 = jObj.get("strHomeBuildingName").toString();
			addressLine3 = jObj.get("strCustAddress").toString();
			addressLine4 = jObj.get("strStreetName").toString() + " " + jObj.get("strLandmark").toString();
			addressCity = jObj.get("strCity").toString() + " " + jObj.get("intPinCode").toString();
		}
		else if (homeDeliveryAddress.equalsIgnoreCase("Office"))
		{
			addressLine2 = jObj.get("strHomeBuildingName").toString();
			addressLine3 = jObj.get("strOfficeAddress").toString();
			addressLine4 = jObj.get("strOfficeStreetName").toString() + " " + jObj.get("strOfficeLandmark").toString();
			addressCity = jObj.get("strOfficeCity").toString() + " " + jObj.get("intOfficePinCode").toString();
		}
		else
		{
			addressLine2 = jObj.get("strTempBuildingName").toString();
			addressLine3 = jObj.get("strTempAddress").toString();
			addressLine4 = jObj.get("strTempStreet").toString() + " " + jObj.get("strTempLandmark").toString();
			addressCity = "";
		}

		clsPOSDirectBillerBean objDirectBillerBean = objBean; // use this bean in
															// settlement form
		req.getSession().setAttribute("customerMobile", mobileNo);

		listItemsDtlInBill = new ArrayList<clsPOSItemsDtlsInBill>();
		String orderNo = "";

		if (objBean != null)
		{
			if (objBean.getListItemsDtlInBill().size() > 0)
			{
				obBillItem = new clsPOSBillDetails();
				List<clsPOSItemsDtlsInBill> listItemsDtlInBill = new ArrayList<clsPOSItemsDtlsInBill>();
				clsPOSItemsDtlsInBill objItemsDtlsInBill = new clsPOSItemsDtlsInBill();

				List<clsPOSCustomerDtlsOnBill> listCustomerDtlOnBill = new ArrayList<clsPOSCustomerDtlsOnBill>();
				clsPOSCustomerDtlsOnBill obCustomerDtl = new clsPOSCustomerDtlsOnBill();
				obCustomerDtl.setCustomerCode(objDirectBillerBean.getStrCustomerCode());
				obCustomerDtl.setStrCustomerName(objDirectBillerBean.getCustomerName());
				obCustomerDtl.setBillTransType(objDirectBillerBean.getBillTransType());
				obCustomerDtl.setStrCustomerType(objDirectBillerBean.getStrCustomerType());
				obCustomerDtl.setCustMobileNo(objDirectBillerBean.getCustMobileNo());
				// fill customer list
				listCustomerDtlOnBill.add(obCustomerDtl);

				obBillItem.setListCustomerDtlOnBill(listCustomerDtlOnBill);

				// /Fill Delivery Boy Data
				List<clsPOSDeliveryBoyMasterBean> listDeliveryBoyMasterBean = new ArrayList<clsPOSDeliveryBoyMasterBean>();
				clsPOSDeliveryBoyMasterBean objDeliveryBoy = new clsPOSDeliveryBoyMasterBean();
				objDeliveryBoy.setStrDPCode(objDirectBillerBean.getStrDeliveryBoyCode());
				objDeliveryBoy.setStrDPName(objDirectBillerBean.getStrDeliveryBoyName());
				listDeliveryBoyMasterBean.add(objDeliveryBoy);

				obBillItem.setListDeliveryBoyMasterBeanl(listDeliveryBoyMasterBean);
				obBillItem.setTakeAwayYN(objDirectBillerBean.getTakeAway());
				obBillItem.setBillType("Direct Biller");
				obBillItem.setAreaCode("");
				List<clsPOSItemsDtlsInBill> tmplistOfDirectBillerBillItemDtl = objBean.getListItemsDtlInBill();
				for (int i = 0; i < tmplistOfDirectBillerBillItemDtl.size(); i++)
				{
					if (i > 0)
					{
						objItemsDtlsInBill = tmplistOfDirectBillerBillItemDtl.get(i);
						listItemsDtlInBill.add(objItemsDtlsInBill);
					}
				}
				obBillItem.setListItemsDtlInBill(listItemsDtlInBill);

				orderNo = funGenerateOrderNo(selectedPOSCode);

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
				String currentTimeAMPM = hh + ":" + mm + ":" + ss + ":" + ampm;//
				String currentTime = hh + ":" + mm + ":" + ss;

				clsOrderHdModel objOrderHdModel = new clsOrderHdModel(new clsOrderHdModel_ID(orderNo, clientCode));

				objOrderHdModel.setStrOrderNo(orderNo);
				objOrderHdModel.setStrCustomerCode(selectedCustomerCode);
				objOrderHdModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
				objOrderHdModel.setDteDate(posDate);
				objOrderHdModel.setTmeTime(currentTimeAMPM);
				objOrderHdModel.setStrPOSCode(selectedPOSCode);

				objOrderHdModel.setStrCustAddressLine1(addressLine1);
				objOrderHdModel.setStrCustAddressLine2(addressLine2);
				objOrderHdModel.setStrCustAddressLine3(addressLine3);
				objOrderHdModel.setStrCustAddressLine4(addressLine4);
				objOrderHdModel.setStrCustCity(addressCity);
				objOrderHdModel.setStrClientCode(clientCode);
				objOrderHdModel.setDblHomeDeliCharge(0.00);
				objOrderHdModel.setDblLooseCashAmt(0);
				objOrderHdModel.setStrDataPostFlag("N");
				objOrderHdModel.setStrBillNo("");
				objOrderHdModel.setStrMobileNo(mobileNo);
				objOrderHdModel.setStrCustomerName(customerName);

				// Insert into tblbilldtl table
				List<clsOrderDtlModel> listObjBillDtl = new ArrayList<clsOrderDtlModel>();

				for (int i = 0; i < tmplistOfDirectBillerBillItemDtl.size(); i++)
				{
					if (i > 0)
					{
						objItemsDtlsInBill = tmplistOfDirectBillerBillItemDtl.get(i);
						
						

						List<clsPOSModifiersOnItem>listOfModifiers=objItemsDtlsInBill.getListModifierDtl();
						// for modifier dtl
						if (objItemsDtlsInBill.getItemCode() == null && listOfModifiers.size() > 0)
						{
																			
							for (int j = i; j < listOfModifiers.size(); j++)
							{
								
								clsPOSModifiersOnItem objModifiersOnItem=listOfModifiers.get(j);
								
								String itemModifierCode=objModifiersOnItem.getModifierCode();
								if(itemModifierCode==null && !itemModifierCode.contains("M") && !itemModifierCode.contains("!"))
								{
									continue;
								}
								String arrItemModifierCode[]=itemModifierCode.split("!");
								if(arrItemModifierCode.length<2)
								{
									continue;
								}
								
								String itemCode=itemModifierCode.split("!")[0]+itemModifierCode.split("!")[1];
								
								double rate = objModifiersOnItem.getAmount() / objModifiersOnItem.getQuantity();
								
								clsOrderDtlModel objOrderDtl = new clsOrderDtlModel();
								objOrderDtl.setStrItemCode(itemCode);
								objOrderDtl.setStrItemName(objModifiersOnItem.getModifierDescription());
								objOrderDtl.setStrAdvBookingNo("");
								objOrderDtl.setDblRate(rate);
								objOrderDtl.setDblQuantity(objModifiersOnItem.getQuantity());
								objOrderDtl.setDblAmount(objModifiersOnItem.getAmount());
								objOrderDtl.setDblTaxAmount(0);
								objOrderDtl.setDteBillDate(posDate + " " + currentTime);
								objOrderDtl.setDtBillDate(posDate);
								objOrderDtl.setStrKOTNo("");
								objOrderDtl.setStrCounterCode("");
								objOrderDtl.setTmeOrderProcessing("12:00:00");
								objOrderDtl.setStrDataPostFlag("N");
								objOrderDtl.setStrMMSDataPostFlag("N");
								objOrderDtl.setStrManualKOTNo("");
								boolean tdYN =false;
								if (tdYN)
								{
									objOrderDtl.setTdhYN("Y");
								}
								else
								{
									objOrderDtl.setTdhYN("N");
								}
								objOrderDtl.setStrPromoCode("");
								objOrderDtl.setStrCounterCode("");
								objOrderDtl.setStrWaiterNo("");
								objOrderDtl.setSequenceNo(objModifiersOnItem.getStrSerialNo());
								objOrderDtl.setTmeOrderPickup("12:00:00");

								objOrderDtl.setDblDiscountAmt(0);
								objOrderDtl.setDblDiscountPer(0);

								listObjBillDtl.add(objOrderDtl);
							}
						}
						else
						// for item dtl
						{
							double rate = 0.00;
							if (objItemsDtlsInBill.getQuantity() == 0)
							{
								rate = objItemsDtlsInBill.getRate();
							}
							else
							{
								rate = objItemsDtlsInBill.getAmount() / objItemsDtlsInBill.getQuantity();
							}

							clsOrderDtlModel objOrderDtl = new clsOrderDtlModel();
							objOrderDtl.setStrItemCode(objItemsDtlsInBill.getItemCode());
							objOrderDtl.setStrItemName(objItemsDtlsInBill.getItemName());
							objOrderDtl.setStrAdvBookingNo("");
							objOrderDtl.setDblRate(rate);
							objOrderDtl.setDblQuantity(objItemsDtlsInBill.getQuantity());
							objOrderDtl.setDblAmount(objItemsDtlsInBill.getAmount());
							objOrderDtl.setDblTaxAmount(0);
							objOrderDtl.setDteBillDate(posDate + " " + currentTime);
							objOrderDtl.setDtBillDate(posDate);
							objOrderDtl.setStrKOTNo("");
							objOrderDtl.setStrCounterCode("");
							objOrderDtl.setTmeOrderProcessing("12:00:00");
							objOrderDtl.setStrDataPostFlag("N");
							objOrderDtl.setStrMMSDataPostFlag("N");
							objOrderDtl.setStrManualKOTNo("");
							boolean tdYN = objItemsDtlsInBill.isTdhYN();
							if (tdYN)
							{
								objOrderDtl.setTdhYN("Y");
							}
							else
							{
								objOrderDtl.setTdhYN("N");
							}
							objOrderDtl.setStrPromoCode(objItemsDtlsInBill.getPromoCode());
							objOrderDtl.setStrCounterCode("");
							objOrderDtl.setStrWaiterNo("");
							objOrderDtl.setSequenceNo(objItemsDtlsInBill.getSeqNo());
							objOrderDtl.setTmeOrderPickup("12:00:00");

							objOrderDtl.setDblDiscountAmt(objItemsDtlsInBill.getDiscountAmt() * objItemsDtlsInBill.getQuantity());
							objOrderDtl.setDblDiscountPer(objItemsDtlsInBill.getDiscountPer());

							listObjBillDtl.add(objOrderDtl);
						}
					}
				}

				objOrderHdModel.setListBillDtlModel(listObjBillDtl);

				objBaseServiceImpl.funSave(objOrderHdModel);

			}
		}

		objDirectBillerBean = new clsPOSDirectBillerBean();

		req.getSession().setAttribute("success", true);
		req.getSession().setAttribute("successMessage", "Order No. is " + orderNo);

		req.getSession().setAttribute("homeDeliveryAddress", "Home");
		req.getSession().setAttribute("gAreaName", "");

		return new ModelAndView("redirect:/frmCallCenter.html");
	}

	// generate bill no.
	private String funGenerateOrderNo(String posCode)
	{
		String orderNo = "";
		try
		{
			long code = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select longOrderNo from tblstorelastorderno where strPosCode='"+posCode+"' ");
			List listItemDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listItemDtl != null && listItemDtl.size() > 0)
			{

				Object objItemDtl = (Object) listItemDtl.get(0);

				code = Long.parseLong(objItemDtl.toString());
				code = code + 1;

				orderNo = "O" + posCode + String.format("%05d", code);
				objBaseServiceImpl.funExecuteUpdate("update tblstorelastorderno set longOrderNo='" + code + "' where strPosCode='"+posCode+"' ", "sql");
			}
			else
			{
				orderNo = "O" + posCode + "00001";
				sqlBuilder.setLength(0);
				objBaseServiceImpl.funExecuteUpdate("insert into tblstorelastorderno values('"+posCode+"','1')", "sql");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return orderNo;
		}
	}

}
