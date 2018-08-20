package com.sanguine.webpos.controller;

import java.util.ArrayList;
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
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsMenuItemMasterModel_ID;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSMenuItemMasterController{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private clsGlobalFunctions objGlobal=null;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;

	Map<String,String> hmSubGroupName=new HashMap<String,String>();
	Map<String,String> hmSubGroupCode=new HashMap<String,String>();

	@RequestMapping(value = "/frmPOSMenuItem", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{

		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
			
		
		//ProcessTime
		List<String> lstProcessTime=new ArrayList<String>();
		for(int i=1;i<31;i++)
		{
				lstProcessTime.add(String.valueOf(i));
		}
		model.put("ProcessTime", lstProcessTime);
		
		//subgroup data
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		
		List list=funGetSubGroupGDetail(clientCode);
		model.put("subGroup", list);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMenuItemMaster_1","command", new clsPOSMenuItemMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMenuItemMaster","command", new clsPOSMenuItemMasterBean());
		}else {
			return null;
		}
	}
	public ArrayList<String> funGetSubGroupGDetail(String clientCode)
	{
		clsSubGroupMasterHdModel objModel=new clsSubGroupMasterHdModel();
		ArrayList<String> lstSGData=new ArrayList<String>();
    	try
    	{
			List objModelList =objMasterService.funLoadAllSubGroup(clientCode);
	    	for(int i=0; i<objModelList.size();i++)
	    	{
	    		objModel =(clsSubGroupMasterHdModel) objModelList.get(i);
	    		hmSubGroupName.put(objModel.getStrSubGroupName(),objModel.getStrSubGroupCode());
	    		hmSubGroupCode.put(objModel.getStrSubGroupCode(),objModel.getStrSubGroupName());
	    		lstSGData.add(objModel.getStrSubGroupName());	
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return lstSGData;
	}
	
	@RequestMapping(value = "/loadItemCode", method = RequestMethod.GET)
	public @ResponseBody clsPOSMenuItemMasterBean funSetSearchFields(@RequestParam("itemCode") String itemCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSMenuItemMasterBean objMenuItemMasterBean=null;
		
		clsMenuItemMasterModel objMenuItemMasterModel = objMasterService.funGetMenuItemMasterData(itemCode,clientCode);
		objMenuItemMasterBean=new clsPOSMenuItemMasterBean();
		objMenuItemMasterBean.setStrItemCode(objMenuItemMasterModel.getStrItemCode());
		objMenuItemMasterBean.setStrItemName(objMenuItemMasterModel.getStrItemName());
		objMenuItemMasterBean.setStrShortName(objMenuItemMasterModel.getStrShortName());
		objMenuItemMasterBean.setStrExternalCode(objMenuItemMasterModel.getStrExternalCode());
		objMenuItemMasterBean.setStrItemForSale(objMenuItemMasterModel.getStrItemForSale());
		objMenuItemMasterBean.setStrItemType(objMenuItemMasterModel.getStrItemType());
		if(null!=hmSubGroupCode)
			objMenuItemMasterBean.setStrSubGroupCode(hmSubGroupCode.get(objMenuItemMasterModel.getStrSubGroupCode()));
		objMenuItemMasterBean.setStrRawMaterial(objMenuItemMasterModel.getStrRawMaterial());
		objMenuItemMasterBean.setStrTaxIndicator(objMenuItemMasterModel.getStrTaxIndicator());
		objMenuItemMasterBean.setDblPurchaseRate(objMenuItemMasterModel.getDblPurchaseRate());
		objMenuItemMasterBean.setStrRevenueHead(objMenuItemMasterModel.getStrRevenueHead());
		objMenuItemMasterBean.setDblSalePrice(objMenuItemMasterModel.getDblSalePrice());
		objMenuItemMasterBean.setIntProcDay(objMenuItemMasterModel.getIntProcDay());
		objMenuItemMasterBean.setIntProcTimeMin(objMenuItemMasterModel.getIntProcTimeMin());
		objMenuItemMasterBean.setDblMinLevel(objMenuItemMasterModel.getDblMinLevel());
		objMenuItemMasterBean.setDblMaxLevel(objMenuItemMasterModel.getDblMaxLevel());
		objMenuItemMasterBean.setStrStockInEnable(objMenuItemMasterModel.getStrStockInEnable());
		objMenuItemMasterBean.setStrOpenItem(objMenuItemMasterModel.getStrOpenItem());
		objMenuItemMasterBean.setStrItemWiseKOTYN(objMenuItemMasterModel.getStrItemWiseKOTYN());
		objMenuItemMasterBean.setStrItemDetails(objMenuItemMasterModel.getStrItemDetails());
		objMenuItemMasterBean.setStrDiscountApply(objMenuItemMasterModel.getStrDiscountApply());
		objMenuItemMasterBean.setStrUOM(objMenuItemMasterModel.getStrUOM());
		objMenuItemMasterBean.setTmeTargetMiss(objMenuItemMasterModel.getTmeTargetMiss());
		
		return objMenuItemMasterBean;
	}
	

	@RequestMapping(value = "/saveMenuItemMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMenuItemMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String itemCode = objBean.getStrItemCode();
			if (itemCode.trim().isEmpty())
		    {
	        	List list=objUtilityController.funGetDocumentCode("POSMenuItemMaster");
				if (!list.get(0).toString().equals("0"))
				{
					String strCode = "0";
				    String code = list.get(0).toString();
				    StringBuilder sb = new StringBuilder(code);
				    String ss = sb.delete(0, 1).toString();
				    for (int i = 0; i < ss.length(); i++)
				    {
						if (ss.charAt(i) != '0')
						{
						    strCode = ss.substring(i, ss.length());
						    break;
						}
				    }
				    int intCode = Integer.parseInt(strCode);
		            intCode++;
		            if (intCode < 10)
		            {
		                itemCode = "I00000" + intCode;
		            }
		            else if (intCode < 100)
		            {
		                itemCode = "I0000" + intCode;
		            }
		            else if (intCode < 1000)
		            {
		                itemCode = "I000" + intCode;
		            }
		            else if (intCode < 10000)
		            {
		                itemCode = "I00" + intCode;
		            }
		            else if (intCode < 100000)
		            {
		                itemCode = "I0" + intCode;
		            }
		            else if (intCode < 1000000)
		            {
		                itemCode = "I" + intCode;
		            }
		        }
		        else
		        {
		            itemCode = "I000001";
		        }
		    }
	        clsMenuItemMasterModel objModel = new clsMenuItemMasterModel(new clsMenuItemMasterModel_ID(itemCode, clientCode));
		    objModel.setStrItemCode(itemCode);
		    objModel.setStrItemName(objBean.getStrItemName());
		    objModel.setStrShortName(objBean.getStrShortName());
		    objModel.setStrExternalCode(objBean.getStrExternalCode());
		    objModel.setStrItemForSale(objGlobal.funIfNull(objBean.getStrItemForSale(),"N","Y"));
		    objModel.setStrItemType(objBean.getStrItemType());
		    
		    objModel.setStrSubGroupCode(hmSubGroupName.get(objBean.getStrSubGroupCode()));
		    objModel.setStrRawMaterial(objGlobal.funIfNull(objBean.getStrRawMaterial(),"N","Y"));
		    String taxIndicator = objBean.getStrTaxIndicator();
		    if (taxIndicator==null)
		    {
		    	taxIndicator =" ";
		    }
		    objModel.setStrTaxIndicator(taxIndicator);
		    objModel.setDblPurchaseRate(objBean.getDblPurchaseRate());
		    objModel.setStrRevenueHead(objBean.getStrRevenueHead());
		    objModel.setDblSalePrice(objBean.getDblSalePrice());
		    objModel.setIntProcDay(objBean.getIntProcDay());
		    objModel.setIntProcTimeMin(objBean.getIntProcTimeMin());
		    objModel.setDblMinLevel(objBean.getDblMinLevel());
		    objModel.setDblMaxLevel(objBean.getDblMaxLevel());
		    objModel.setStrStockInEnable(objGlobal.funIfNull(objBean.getStrStockInEnable(),"N","Y"));
		    objModel.setStrOpenItem(objGlobal.funIfNull(objBean.getStrOpenItem(),"N","Y"));
		    objModel.setStrItemWiseKOTYN(objGlobal.funIfNull(objBean.getStrItemWiseKOTYN(),"N","Y"));
		    objModel.setStrItemDetails(objBean.getStrItemDetails());
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setStrDiscountApply(objGlobal.funIfNull(objBean.getStrDiscountApply(),"N",objBean.getStrDiscountApply()));
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrExciseBrandCode("");
		    objModel.setStrItemImage("");
		    objModel.setTmeTargetMiss(objBean.getTmeTargetMiss());
		    objModel.setStrNoDeliveryDays("");
		    objModel.setStrItemWeight("0.000");
		    objModel.setStrUrgentOrder("N");
		    objModel.setStrWSProdCode("NA");
		    objModel.setStrUOM(objBean.getStrUOM());
		    objModel.setImgImage("");
		    objMasterService.funSaveUpdateMenuItemMaster(objModel);
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+itemCode);
									
			return new ModelAndView("redirect:/frmPOSMenuItem.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
   }
	
	
	@RequestMapping(value ="/checkItemName" ,method =RequestMethod.GET)
	public  @ResponseBody boolean funCheckMenuName(@RequestParam("itemName") String itemName,@RequestParam("itemCode") String itemCode, HttpServletRequest req) 
	{
		String clientCode =req.getSession().getAttribute("gClientCode").toString();
		int count=objPOSGlobal.funCheckName(itemName,itemCode,clientCode,"POSMenuItem");

		if(count>0)
			return false;
		else
			return true;
	}
	
}


