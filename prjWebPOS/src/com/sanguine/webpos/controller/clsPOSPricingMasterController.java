package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.sanguine.webpos.bean.clsPOSMenuItemMasterBean;
import com.sanguine.webpos.bean.clsPOSPricingMasterBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsCostCenterMasterModel;
import com.sanguine.webpos.model.clsMenuHeadMasterModel;
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsPricingMasterHdModel;
import com.sanguine.webpos.model.clsPricingMasterModel_ID;
import com.sanguine.webpos.model.clsSubMenuHeadMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSPricingMasterController
{

	@Autowired
	private clsPOSGlobalFunctionsController	objPOSGlobalFunctionsController;
	@Autowired
	private clsGlobalFunctions				objGlobal;

	@Autowired
	private clsPOSMasterService objMasterService;
	
	// Open PricingMaster
	@RequestMapping(value = "/frmPOSPrice", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request) throws Exception
	{
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		model.put("urlHits", urlHits);

		String clientCode = request.getSession().getAttribute("gClientCode").toString();

		// function to get all POS list
		List list=objMasterService.funFillPOSCombo(clientCode);
		Map mapPOSName = new HashMap<>();
		mapPOSName.put("All", "All");
		if(list!=null)
		{
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			
			mapPOSName.put(Array.get(obj, 0), Array.get(obj, 1));
		}
		}
		model.put("mapPOSName", mapPOSName);
		
		// function to get all Menu Head list
		List list1= objMasterService.funLoadAllMenuHeadForMaster(clientCode);
		
		Map mapMenuHeadName = new HashMap<>();
		mapMenuHeadName.put("", "");
		if(list1!=null)
		{
		for (int cnt = 0; cnt < list1.size(); cnt++)
		{
			clsMenuHeadMasterModel objMenuHeadModel = (clsMenuHeadMasterModel) list1.get(cnt);
			mapMenuHeadName.put(objMenuHeadModel.getStrMenuCode(), objMenuHeadModel.getStrMenuName());
		}
		}
		model.put("mapMenuHeadName", mapMenuHeadName);

		// function to get all Sub Menu Head list
		List listForSubMenuHeadMaster = objMasterService.funLoadAllSubMenuHeadMaster(clientCode);
		Map mapSubMenuHeadName = new HashMap<>();
		mapSubMenuHeadName.put("", "");
		if(listForSubMenuHeadMaster!=null)
		{
		for (int cnt = 0; cnt < listForSubMenuHeadMaster.size(); cnt++)
		{
			clsSubMenuHeadMasterModel objModel = (clsSubMenuHeadMasterModel) listForSubMenuHeadMaster.get(cnt);
			mapSubMenuHeadName.put(objModel.getStrSubMenuHeadCode(), objModel.getStrSubMenuHeadName());
		}
		}
		model.put("mapSubMenuHeadName", mapSubMenuHeadName);
		// function to fill all item colours
		Map mapColours = new HashMap<>();
		mapColours.put("", "");
		mapColours.put("WHITE", "WHITE");
		mapColours.put("Black", "Black");
		mapColours.put("Green", "Green");
		mapColours.put("Red", "Red");
		mapColours.put("BLUE", "BLUE");
		mapColours.put("CYAN", "CYAN");
		mapColours.put("ORANGE", "ORANGE");
		mapColours.put("PINK", "PINK");
		mapColours.put("YELLOW", "YELLOW");

		model.put("mapColours", mapColours);

		// function to get all Areas
		List listForAreaMasterData = objMasterService.funGetAllAreaForMaster(clientCode);
		Map mapAreaName = new HashMap<>();
		if(listForAreaMasterData!=null)
		{
		for(int cnt=0;cnt<listForAreaMasterData.size();cnt++)
		{
			clsAreaMasterModel objAreaModel= (clsAreaMasterModel) listForAreaMasterData.get(cnt);
			mapAreaName.put(objAreaModel.getStrAreaCode(), objAreaModel.getStrAreaName());
		}
		}
		model.put("mapAreaName", mapAreaName);

		// function to get all Cost Centers
		List listForAllCostCenterMasterData = objMasterService.funGetAllCostCenterMaster(clientCode);
		Map mapCostCenterName = new HashMap<>();
		if(listForAllCostCenterMasterData!=null)
		{
		for (int i = 0; i < listForAllCostCenterMasterData.size(); i++)
		{
			clsCostCenterMasterModel objCenterMasterModel = (clsCostCenterMasterModel) listForAllCostCenterMasterData.get(i);
		
			mapCostCenterName.put(objCenterMasterModel.getStrCostCenterCode(), objCenterMasterModel.getStrCostCenterName());
		}
		}
		model.put("mapCostCenterName", mapCostCenterName);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSPrice_1", "command", new clsPOSPricingMasterBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSPrice", "command", new clsPOSPricingMasterBean());
		}
		else
		{
			return null;
		}
	}

	@RequestMapping(value = "/loadDataToCreateItemPrice", method = RequestMethod.GET)
	public @ResponseBody clsPOSMenuItemMasterBean funLoadDataToCreateItemPrice(@RequestParam("itemCode") String itemCode, HttpServletRequest req)throws Exception
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		clsPOSMenuItemMasterBean objMenuItemMasterBean = new clsPOSMenuItemMasterBean();
		clsMenuItemMasterModel objMenuItemMasterModel = objMasterService.funGetMenuItemMasterData(itemCode,clientCode);
		objMenuItemMasterBean = new clsPOSMenuItemMasterBean();
		objMenuItemMasterBean.setStrItemCode(objMenuItemMasterModel.getStrItemCode());
		objMenuItemMasterBean.setStrItemName(objMenuItemMasterModel.getStrItemName());
		
		if (null == objMenuItemMasterBean)
		{
			objMenuItemMasterBean = new clsPOSMenuItemMasterBean();
			objMenuItemMasterBean.setStrItemCode("Invalid Code");
		}

		return objMenuItemMasterBean;
	}

	@RequestMapping(value = "/loadDataToUpdateItemPrice", method = RequestMethod.GET)
	public @ResponseBody clsPOSPricingMasterBean funLoadDataToUpdateItemPrice(@RequestParam("longPricingId") String longPricingId, HttpServletRequest req)throws Exception
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		clsPOSPricingMasterBean objPricingMasterBean = null;
		
		clsPricingMasterHdModel objPricingMasterModel = objMasterService.funLoadDataToUpdateItemPrice(longPricingId,clientCode);
		
			objPricingMasterBean = new clsPOSPricingMasterBean();
			objPricingMasterBean.setStrItemCode(objPricingMasterModel.getStrItemCode());
			objPricingMasterBean.setStrItemName(objPricingMasterModel.getStrItemName());
			objPricingMasterBean.setStrPosCode(objPricingMasterModel.getStrPosCode());
			objPricingMasterBean.setStrMenuCode(objPricingMasterModel.getStrMenuCode());
			if (objPricingMasterModel.getStrPopular().equalsIgnoreCase("Y"))
			{
				objPricingMasterBean.setStrPopular(true);
			}
			else
			{
				objPricingMasterBean.setStrPopular(false);
			}
			objPricingMasterBean.setStrPriceMonday(Double.parseDouble(objPricingMasterModel.getStrPriceMonday()));
			objPricingMasterBean.setStrPriceTuesday(Double.parseDouble(objPricingMasterModel.getStrPriceTuesday()));
			objPricingMasterBean.setStrPriceWednesday(Double.parseDouble(objPricingMasterModel.getStrPriceWednesday()));
			objPricingMasterBean.setStrPriceThursday(Double.parseDouble(objPricingMasterModel.getStrPriceThursday()));
			objPricingMasterBean.setStrPriceFriday(Double.parseDouble(objPricingMasterModel.getStrPriceFriday()));
			objPricingMasterBean.setStrPriceSaturday(Double.parseDouble(objPricingMasterModel.getStrPriceSaturday()));
			objPricingMasterBean.setStrPriceSunday(Double.parseDouble(objPricingMasterModel.getStrPriceSunday()));

			objPricingMasterBean.setDteFromDate(objPricingMasterModel.getDteFromDate());
			objPricingMasterBean.setDteToDate(objPricingMasterModel.getDteToDate());

			objPricingMasterBean.setTmeTimeFrom(objPricingMasterModel.getTmeTimeFrom());
			objPricingMasterBean.setStrAMPMFrom(objPricingMasterModel.getStrAMPMFrom());
			objPricingMasterBean.setTmeTimeTo(objPricingMasterModel.getTmeTimeTo());
			objPricingMasterBean.setStrAMPMTo(objPricingMasterModel.getStrAMPMTo());
			objPricingMasterBean.setStrCostCenterCode(objPricingMasterModel.getStrCostCenterCode());
			objPricingMasterBean.setStrTextColor(objPricingMasterModel.getStrTextColor());
			objPricingMasterBean.setStrUserCreated(objPricingMasterModel.getStrUserCreated());
			objPricingMasterBean.setStrUserEdited(objPricingMasterModel.getStrUserEdited());
			objPricingMasterBean.setDteDateCreated(objPricingMasterModel.getDteDateCreated());
			objPricingMasterBean.setDteDateEdited(objPricingMasterModel.getDteDateEdited());
			objPricingMasterBean.setStrAreaCode(objPricingMasterModel.getStrAreaCode());
			objPricingMasterBean.setStrSubMenuHeadCode(objPricingMasterModel.getStrSubMenuHeadCode());
			if (objPricingMasterModel.getStrHourlyPricing().equalsIgnoreCase("Yes"))
			{
				objPricingMasterBean.setStrHourlyPricing(true);
			}
			else
			{
				objPricingMasterBean.setStrHourlyPricing(false);
			}
			objPricingMasterBean.setLongPricingId(String.valueOf(objPricingMasterModel.getLongPricingId()));

		
		if (null == objPricingMasterBean)
		{
			objPricingMasterBean = new clsPOSPricingMasterBean();
			objPricingMasterBean.setStrItemCode("Invalid Code");
		}

		return objPricingMasterBean;
	}

	@RequestMapping(value = "/savePricingMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSPricingMasterBean objBean, BindingResult result, HttpServletRequest req)
	{
		String urlHits = "1";

		try
		{
			if (result.hasErrors())
			{
				return new ModelAndView("frmPOSPrice", "command", objBean);
			}
			else
			{

				/* urlHits = req.getParameter("saddr").toString(); */
				String clientCode = req.getSession().getAttribute("gClientCode").toString();
				String userCode = req.getSession().getAttribute("gUserCode").toString();

				clsPricingMasterHdModel objModel=null;
				if(objBean.getLongPricingId().length()==0)
				{
					objModel=new  clsPricingMasterHdModel();
				}
				else
				{
					objModel=new  clsPricingMasterHdModel(new clsPricingMasterModel_ID(Long.parseLong(objBean.getLongPricingId())));
				}
				String itemCode = objBean.getStrItemCode();
				objModel.setStrItemCode(itemCode);			
				objModel.setStrItemName(objBean.getStrItemName());
				objModel.setStrPosCode(objBean.getStrPosCode());
				objModel.setStrAreaCode(objBean.getStrAreaCode());
				if (objBean.getStrHourlyPricing())
				{
					objModel.setStrHourlyPricing("Yes");
				}
				else
				{
					objModel.setStrHourlyPricing("No");
				}
				objModel.setStrMenuCode(objBean.getStrMenuCode());
				
				if (objBean.getStrPopular())
				{
					objModel.setStrPopular("Y");
				}
				else
				{
					objModel.setStrPopular("N");
				}
				objModel.setStrPriceMonday(String.valueOf(objBean.getStrPriceMonday()));
				objModel.setStrPriceTuesday(String.valueOf(objBean.getStrPriceTuesday()));
				objModel.setStrPriceWednesday(String.valueOf(objBean.getStrPriceWednesday()));
				objModel.setStrPriceThursday(String.valueOf(objBean.getStrPriceThursday()));
				objModel.setStrPriceFriday(String.valueOf(objBean.getStrPriceFriday()));
				objModel.setStrPriceSaturday(String.valueOf(objBean.getStrPriceSaturday()));
				objModel.setStrPriceSunday(String.valueOf(objBean.getStrPriceSunday()));			
				objModel.setDteFromDate(objBean.getDteFromDate());
				objModel.setDteToDate(objBean.getDteToDate());
				if (objBean.getStrHourlyPricing() && objBean.getTmeTimeFrom() != null)
				{
					String arrFromTime[] = objBean.getTmeTimeFrom().split(":");
					String fromHH = arrFromTime[0];
					String fromMM = arrFromTime[1];
					String fromAMPM = arrFromTime[2];

					objBean.setTmeTimeFrom(fromHH + ":" + fromMM);
					objBean.setStrAMPMFrom(fromAMPM);
				}
				else
				{
					objBean.setTmeTimeFrom("HH:MM");
					objBean.setStrAMPMFrom("AM");
				}

				if (objBean.getStrHourlyPricing() && objBean.getTmeTimeTo() != null)
				{
					String arrToTime[] = objBean.getTmeTimeTo().split(":");
					String toHH = arrToTime[0];
					String toMM = arrToTime[1];
					String toAMPM = arrToTime[2];

					objBean.setTmeTimeTo(toHH + ":" + toMM);
					objBean.setStrAMPMTo(toAMPM);
				}
				else
				{
					objBean.setTmeTimeTo("HH:MM");
					objBean.setStrAMPMTo("AM");
				}
				objModel.setTmeTimeFrom(objBean.getTmeTimeFrom());
				objModel.setTmeTimeTo(objBean.getTmeTimeTo());	
				objModel.setStrAMPMFrom(objBean.getStrAMPMFrom());			
				objModel.setStrAMPMTo(objBean.getStrAMPMTo());
				objModel.setStrCostCenterCode(objBean.getStrCostCenterCode());
				objModel.setStrTextColor(objBean.getStrTextColor());
				objModel.setStrUserCreated(userCode);
				objModel.setStrUserEdited(userCode);
				objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));			
				objModel.setStrSubMenuHeadCode(objBean.getStrSubMenuHeadCode());	
				objModel.setStrClientCode(clientCode);
				objMasterService.funSaveUpdatePricingMaster(objModel);
		
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage", " " + itemCode);

				return new ModelAndView("redirect:/frmPOSPrice.html?saddr=" + urlHits);
			}
		}
		catch (Exception ex)
		{
			urlHits = "1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	@RequestMapping(value = "/checkDuplicateItemPricing", method = RequestMethod.GET)
	public @ResponseBody String funCheckDuplicateItemPricing(@RequestParam("itemCode") String itemCode, @RequestParam("posCode") String posCode,
			@RequestParam("areaCode") String areaCode, @RequestParam("hourlyPricing") boolean hourlyPricing, HttpServletRequest req)
	{
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		String isDuplicate = "false";
		String hourlyPrice="";
		if (hourlyPricing)
		{
			hourlyPrice = "YES";
		}
		else
		{
			hourlyPrice = "NO";
		}
		try
		{
		clsPricingMasterHdModel objModel = objMasterService.funCheckDuplicateItemPricing(itemCode, posCode, areaCode, hourlyPrice,clientCode);
		
		if (objModel.toString().isEmpty())
		{
			isDuplicate = "false";
		}
		else if (objModel.toString().length() == 0)
		{
			isDuplicate = "false";
		}
		else
		{
			isDuplicate = "true";
		}
		}
		
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		finally
		{		
			return isDuplicate;
		}
	}
	
	
}
