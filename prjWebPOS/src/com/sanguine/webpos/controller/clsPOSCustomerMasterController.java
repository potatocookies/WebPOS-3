package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSCustomerMasterBean;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.model.clsCustomerMasterModel_ID;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSCustomerMasterController
{

	@Autowired
	private clsGlobalFunctions objGlobal;

	@Autowired
	private clsPOSGlobalFunctionsController objGlobalFun;

	@Autowired
	private clsPOSUtilityController objUtilityController;

	@Autowired
	clsPOSMasterService objMasterService;

	// Open POSCustomerMaster
	@RequestMapping(value = "/frmPOSCustomerMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)throws Exception
	{
		String urlHits = "1";

		clsPOSCustomerMasterBean objCustomerMasterBean = new clsPOSCustomerMasterBean();

		if (request.getParameter("intlongMobileNo") != null)
		{
			objCustomerMasterBean.setIntlongMobileNo(request.getParameter("intlongMobileNo").toString());
		}

		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		List cityCode = new ArrayList();
		List cityName = new ArrayList();
		List stateName = new ArrayList();
		List customerType = new ArrayList();
		JSONArray jObj, jObj1, jObj2;
		JSONArray jArryList, jArryList1, jArryList2;

		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);

			List list = objMasterService.funFillCityCombo(clientCode);
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				Object obj = list.get(cnt);
				cityName.add(Array.get(obj, 0));
				stateName.add(Array.get(obj, 1));
			}

			clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel();
			List<clsCustomerTypeMasterModel> listOfCustomerType = objMasterService.funFillCustomerTypeCombo(clientCode);
			for (int cnt = 0; cnt < listOfCustomerType.size(); cnt++)
			{
				objModel = listOfCustomerType.get(cnt);

				customerType.add(objModel.getStrCustType());
			}
			model.put("cityName", cityName);
			model.put("stateName", stateName);
			model.put("customerType", customerType);
			model.put("mobileNoForNewCust", "NoFound");
		
		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSCustomerMaster_1", "command", objCustomerMasterBean);
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSCustomerMaster", "command", objCustomerMasterBean);
		}
		else
		{
			return null;
		}

	}
	
	
	// Open POSCustomerMaster
		@RequestMapping(value = "/frmCustomerMasterForNewCustomer", method = RequestMethod.GET)
		public ModelAndView funOpenCustomerMasterFormForNewCustomet(Map<String, Object> model, HttpServletRequest request)throws Exception
		{
			String urlHits = "1";

			String mobileNo=request.getParameter("mobileNo").toString();
			request.setAttribute("mobileNo", mobileNo);
			clsPOSCustomerMasterBean objCustomerMasterBean = new clsPOSCustomerMasterBean();

			if (request.getParameter("intlongMobileNo") != null)
			{
				objCustomerMasterBean.setIntlongMobileNo(request.getParameter("intlongMobileNo").toString());
			}

			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			List cityCode = new ArrayList();
			List cityName = new ArrayList();
			List stateName = new ArrayList();
			List customerType = new ArrayList();
			JSONArray jObj, jObj1, jObj2;
			JSONArray jArryList, jArryList1, jArryList2;

			try {
				urlHits = request.getParameter("saddr").toString();
			} catch (NullPointerException e) {
				urlHits = "1";
			}
			model.put("urlHits", urlHits);

				List list = objMasterService.funFillCityCombo(clientCode);
				for (int cnt = 0; cnt < list.size(); cnt++)
				{
					Object obj = list.get(cnt);
					cityName.add(Array.get(obj, 0));
					stateName.add(Array.get(obj, 1));
				}

				clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel();
				List<clsCustomerTypeMasterModel> listOfCustomerType = objMasterService.funFillCustomerTypeCombo(clientCode);
				for (int cnt = 0; cnt < listOfCustomerType.size(); cnt++)
				{
					objModel = listOfCustomerType.get(cnt);

					customerType.add(objModel.getStrCustType());
				}
				model.put("cityName", cityName);
				model.put("stateName", stateName);
				model.put("customerType", customerType);
				model.put("mobileNoForNewCust", mobileNo);
			
			if ("2".equalsIgnoreCase(urlHits))
			{
				return new ModelAndView("frmPOSCustomerMaster_1", "command", objCustomerMasterBean);
			}
			else if ("1".equalsIgnoreCase(urlHits))
			{
				return new ModelAndView("frmPOSCustomerMaster", "command", objCustomerMasterBean);
			}
			else
			{
				return null;
			}

		}
	
		
	

	@RequestMapping(value = "/checkExternalNo", method = RequestMethod.GET)
	public @ResponseBody boolean funCheckAreaName(@RequestParam("strMobileNo") String strMobileNo, @RequestParam("strCustCode") String strCustCode, HttpServletRequest req)
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		int count = objGlobalFun.funCheckName(strCustCode, strMobileNo, clientCode, "POSCustomerMaster");
		if (count > 0)
			return false;
		else
			return true;

	}

	@RequestMapping(value = "/savePOSCustomerMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSCustomerMasterBean objBean, BindingResult result, HttpServletRequest req)
	{
		String urlHits = "1";
		
		try
		{
			urlHits = req.getParameter("saddr").toString();
			String clientCode = req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode = req.getSession().getAttribute("gUserCode").toString();
			String strBuildingCode = objBean.getStrBuldingCode();

			String customerMasterCode = objBean.getStrCustomerCode();
			if (customerMasterCode.trim().isEmpty())
			{
				long lastNo = 1;
				String propertCode = clientCode.substring(4);
				

				List list = objUtilityController.funGetDocumentCode("POSCustomerMaster");
				if (!list.get(0).toString().equals("0"))
				{
					String strCode = "00";
					String code = list.get(0).toString();
					System.out.println("code-->"+code);
					StringBuilder sb = new StringBuilder(code);

					strCode = sb.substring(1, sb.length());

					lastNo = Long.parseLong(strCode);
					
					
					lastNo++;
					customerMasterCode = propertCode + "C" + String.format("%07d", lastNo);
				
				}
				else
				{
					customerMasterCode = propertCode + "C" + String.format("%07d", lastNo);
				}
			}
			clsCustomerMasterModel objModel = new clsCustomerMasterModel(new clsCustomerMasterModel_ID(customerMasterCode, clientCode));
	
			objModel.setStrCustomerName(objBean.getStrCustomerName());
			objModel.setStrBuldingCode(strBuildingCode);
			objModel.setStrBuildingName(objBean.getStrBuildingName());
			objModel.setStrStreetName(objBean.getStrStreetName());
			objModel.setStrLandmark(objBean.getStrLandmark());
			objModel.setStrArea(objBean.getStrArea());
			objModel.setStrCity(objBean.getStrCity());
			objModel.setStrState(objBean.getStrState());
			objModel.setIntPinCode(objBean.getIntPinCode());
			objModel.setLongMobileNo(objBean.getIntlongMobileNo());
			objModel.setStrOfficeBuildingCode(objBean.getStrOfficeBuildingCode());
			objModel.setStrOfficeBuildingName(objBean.getStrOfficeBuildingName());
			objModel.setStrOfficeStreetName(objBean.getStrOfficeStreetName());
			objModel.setStrOfficeLandmark("N");
			objModel.setStrOfficeArea(objBean.getStrOfficeArea());
			objModel.setStrOfficeCity(objBean.getStrOfficeCity());
			objModel.setStrOfficePinCode(objBean.getStrOfficePinCode());
			objModel.setStrOfficeState(objBean.getStrOfficeState());
			objModel.setStrOfficeNo(objBean.getStrOfficeNo());
			objModel.setStrOfficeAddress("N");
			objModel.setStrExternalCode(objBean.getStrExternalCode());
			objModel.setStrCustomerType(objBean.getStrCustomerType());
			objModel.setDteDOB(objBean.getDteDOB());
			objModel.setStrGender(objBean.getStrGender());
			objModel.setDteAnniversary(objBean.getDteAnniversary());
			objModel.setStrEmailId(objBean.getStrEmailId());
			objModel.setStrClientCode(clientCode);
			objModel.setStrUserCreated(webStockUserCode);
			objModel.setStrUserEdited(webStockUserCode);
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setStrCRMId("N");
			objModel.setStrCustAddress("N");
			objModel.setStrDataPostFlag("N");
			objModel.setStrGSTNo(objBean.getStrGSTNo());
			objModel.setStrTempAddress(objBean.getStrTempAddress());
			objModel.setStrTempLandmark(objBean.getStrTempLandmark());
			objModel.setStrTempStreet(objBean.getStrTempStreetName());

			objMasterService.funSaveCustomerMaster(objModel);

			req.getSession().setAttribute("success", true);

			req.getSession().setAttribute("successMessage", " " + customerMasterCode);

			return new ModelAndView("redirect:/frmPOSCustomerMaster.html?saddr=" + urlHits);
		}
		catch (Exception ex)
		{
			urlHits = "1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	// Assign filed function to set data onto form for edit transaction.
	@RequestMapping(value = "/loadPOSCustomerMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSCustomerMasterBean funSetSearchFields(@RequestParam("POSCustomerCode") String CustomerCode, HttpServletRequest req)throws Exception
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		clsPOSCustomerMasterBean objPOSCustomerMaster = new clsPOSCustomerMasterBean();

		clsCustomerMasterModel objModel = (clsCustomerMasterModel) objMasterService.funSelectedCustomerMasterData(CustomerCode, clientCode);

		objPOSCustomerMaster.setStrCustomerCode(objModel.getStrCustomerCode());
		objPOSCustomerMaster.setStrCustomerName(objModel.getStrCustomerName());
		objPOSCustomerMaster.setIntlongMobileNo(objModel.getLongMobileNo());
		objPOSCustomerMaster.setStrArea(objModel.getStrArea());
		objPOSCustomerMaster.setStrBuldingCode(objModel.getStrBuldingCode());
		objPOSCustomerMaster.setStrBuildingName(objModel.getStrBuildingName());
		objPOSCustomerMaster.setStrStreetName(objModel.getStrStreetName());
		objPOSCustomerMaster.setStrLandmark(objModel.getStrLandmark());

		objPOSCustomerMaster.setStrCity(objModel.getStrCity());
		objPOSCustomerMaster.setStrState(objModel.getStrState());
		objPOSCustomerMaster.setIntPinCode(objModel.getIntPinCode());

		objPOSCustomerMaster.setStrOfficeBuildingCode(objModel.getStrOfficeBuildingCode());
		objPOSCustomerMaster.setStrOfficeBuildingName(objModel.getStrOfficeBuildingName());
		objPOSCustomerMaster.setStrOfficeStreetName(objModel.getStrOfficeStreetName());
		objPOSCustomerMaster.setStrOfficeArea(objModel.getStrOfficeArea());
		objPOSCustomerMaster.setStrOfficeCity(objModel.getStrOfficeCity());
		objPOSCustomerMaster.setStrOfficePinCode(objModel.getStrOfficePinCode());
		objPOSCustomerMaster.setStrOfficeState(objModel.getStrOfficeState());
		objPOSCustomerMaster.setStrOfficeNo(objModel.getStrOfficeNo());
		objPOSCustomerMaster.setStrExternalCode(objModel.getStrExternalCode());
		objPOSCustomerMaster.setStrCustomerType(objModel.getStrCustomerType());
		objPOSCustomerMaster.setDteDOB(objModel.getDteDOB());
		objPOSCustomerMaster.setStrGender(objModel.getStrGender());
		objPOSCustomerMaster.setDteAnniversary(objModel.getDteAnniversary());
		objPOSCustomerMaster.setStrEmailId(objModel.getStrEmailId());
		objPOSCustomerMaster.setStrGSTNo(objModel.getStrGSTNo());
		objPOSCustomerMaster.setStrTempAddress(objModel.getStrTempAddress());
		objPOSCustomerMaster.setStrTempLandmark(objModel.getStrTempLandmark());
		objPOSCustomerMaster.setStrTempStreetName(objModel.getStrTempStreet());

		if (null == objPOSCustomerMaster)
		{
			objPOSCustomerMaster = new clsPOSCustomerMasterBean();
			objPOSCustomerMaster.setStrCustomerCode("Invalid Code");
		}

		return objPOSCustomerMaster;
	}

}
