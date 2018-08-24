package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSAreaMasterBean;
import com.sanguine.webpos.bean.clsPOSItemModifierMasterBean;
import com.sanguine.webpos.bean.clsPOSMenuItemMasterBean;
import com.sanguine.webpos.bean.clsPOSSettlementMasterBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsAreaMasterModel_ID;
import com.sanguine.webpos.model.clsItemModifierMasterModel;
import com.sanguine.webpos.model.clsItemModifierMasterModel_ID;
import com.sanguine.webpos.model.clsMenuHeadMasterModel;
import com.sanguine.webpos.model.clsMenuItemPricingHdModel;
import com.sanguine.webpos.model.clsModifierGroupMasterHdModel;
import com.sanguine.webpos.model.clsModifierMasterHdModel;
import com.sanguine.webpos.model.clsModifierMasterModel_ID;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSItemModifierMasterController{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	@Autowired
	private clsPOSMasterService objMasterService;
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	Map<String,String> hmModifierGroupName=new HashMap<>();
	Map<String, Double>  mapSelectedItems = new HashMap<>();
	//Open ItemModifierMaster
	
	@RequestMapping(value = "/frmPOSItemModifier", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		
		
		//load modifierGroup
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		List<String> lstModGroup=new ArrayList<String>();
		lstModGroup.add("--SELECT--");
		List list=objMasterService.funLoadAllModifierGroup(clientCode);
		if(list!=null)
		{
		for (int cnt = 0; cnt < list.size(); cnt++)
		{
			clsModifierGroupMasterHdModel objModel= (clsModifierGroupMasterHdModel) list.get(cnt);
			lstModGroup.add(objModel.getStrModifierGroupName());
			hmModifierGroupName.put(objModel.getStrModifierGroupCode(),objModel.getStrModifierGroupName() );
		}
		}
		model.put("ModifierGroup", lstModGroup);
	
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSItemModifierMaster_1","command", new clsPOSItemModifierMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSItemModifierMaster","command", new clsPOSItemModifierMasterBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	
	//load menu table
	@RequestMapping(value = "/LoadMenuDetails", method = RequestMethod.GET)
	public @ResponseBody List funGetMenuDetails(HttpServletRequest req) throws Exception
	{
		List<clsPOSItemModifierMasterBean> lstMenuDtl=new ArrayList<clsPOSItemModifierMasterBean>();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSItemModifierMasterBean objItemModifierMasterBean = new clsPOSItemModifierMasterBean();
		List listForMenuHeadMaster = objMasterService.funLoadAllMenuHeadForMaster(clientCode);
		List retList=new ArrayList();
		if(listForMenuHeadMaster!=null)
		{
		for (int cnt = 0; cnt < listForMenuHeadMaster.size(); cnt++)
		{
		objItemModifierMasterBean=new clsPOSItemModifierMasterBean();	
		Object[] obj = (Object[]) listForMenuHeadMaster.get(cnt);
		objItemModifierMasterBean.setStrMenuCode( obj[0].toString());
		objItemModifierMasterBean.setStrMenuName(obj[1].toString());
		retList.add(objItemModifierMasterBean);
		}
		}
		
		
		if(null==objItemModifierMasterBean)
		{
			objItemModifierMasterBean=new clsPOSItemModifierMasterBean();
			objItemModifierMasterBean.setStrMenuCode("Data not found");
			retList.add(objItemModifierMasterBean);
		}
		
	return retList;
	}

	//load Menu wise Item Details
	@RequestMapping(value = "/loadMenuWiseItemDetail", method = RequestMethod.GET)
	public @ResponseBody List funGetMenuWiseItemDetail(@RequestParam("MenuCode") String menuCode,@RequestParam("modifierCode") String modifierCode,HttpServletRequest req) throws Exception
	{
		List<clsPOSItemModifierMasterBean> lstItemDtl=new ArrayList<clsPOSItemModifierMasterBean>();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSItemModifierMasterBean objItemModifierMasterBean=null;
		clsMenuItemPricingHdModel objMenuItemPricingHdModel = null;
		
		List listForMenuItemPricingMaster = objMasterService.funLoadItemPricingMasterData(menuCode,modifierCode,clientCode);
		List retList=new ArrayList();
		if(listForMenuItemPricingMaster!=null)
		{
		for (int cnt = 0; cnt < listForMenuItemPricingMaster.size(); cnt++)
		{
			objItemModifierMasterBean=new clsPOSItemModifierMasterBean();	
		Object[] obj = (Object[])listForMenuItemPricingMaster.get(cnt);
		objItemModifierMasterBean.setStrMenuCode( obj[1].toString());
		objItemModifierMasterBean.setStrMenuName(obj[0].toString());
		objItemModifierMasterBean.setStrModifierCode(obj[2].toString());
		objItemModifierMasterBean.setStrSelectAll(obj[4].toString());//selected or not
		objItemModifierMasterBean.setStrDeselectAll(obj[3].toString());// default modifier
		retList.add(objItemModifierMasterBean);
		}
		}
		
		
		if(null==objItemModifierMasterBean)
		{
			objItemModifierMasterBean=new clsPOSItemModifierMasterBean();
			objItemModifierMasterBean.setStrMenuCode("Data not found");
			retList.add(objItemModifierMasterBean);
		}
		
		return retList;
		
	}

	// load all data
	@RequestMapping(value = "/loadModifierCode", method = RequestMethod.GET)
	public @ResponseBody clsPOSItemModifierMasterBean funSetSearchFields(@RequestParam("modCode") String modCode,HttpServletRequest req) throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSItemModifierMasterBean objPOSItemModifierMasterBean = new clsPOSItemModifierMasterBean();
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("modCode",modCode);
		hmParameters.put("clientCode",clientCode);
	    
		clsModifierMasterHdModel objModifierMasterHdModel = objMasterService.funGetItemModifierMasterData(hmParameters);
		
		Set<clsItemModifierMasterModel> listItemModifierDtl = objModifierMasterHdModel.getSetItemModifierDtl();
		Iterator itr = listItemModifierDtl.iterator();
		
		objPOSItemModifierMasterBean.setStrModifierCode(objModifierMasterHdModel.getStrModifierCode());
		objPOSItemModifierMasterBean.setStrModifierName(objModifierMasterHdModel.getStrModifierName());
		objPOSItemModifierMasterBean.setStrModifierDescription(objModifierMasterHdModel.getStrModifierDesc());
		objPOSItemModifierMasterBean.setStrModifierGroup(objModifierMasterHdModel.getStrModifierGroupCode());
		if(itr.hasNext())
        {
		 	clsItemModifierMasterModel objItemModifierMasterModel=(clsItemModifierMasterModel)itr.next();
		 	objPOSItemModifierMasterBean.setDblRate(objItemModifierMasterModel.getDblRate());
		 	objPOSItemModifierMasterBean.setStrApplicable(objItemModifierMasterModel.getStrApplicable());
		 	objPOSItemModifierMasterBean.setStrChargable(objItemModifierMasterModel.getStrChargable());
        }
	
		if(null==objPOSItemModifierMasterBean)
		{
			objPOSItemModifierMasterBean = new clsPOSItemModifierMasterBean();
			objPOSItemModifierMasterBean.setStrModifierCode("Invalid Code");
		}
		
		return objPOSItemModifierMasterBean;
	}
	
	@RequestMapping(value = "/saveItemModifierMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSItemModifierMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		try
		{
			
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String modifierCode = "",modifierName="";
			List<clsPOSMenuItemMasterBean> listItem=objBean.getListObjItemBean();
			modifierCode = objBean.getStrModifierCode();
			
			
			if (modifierCode.trim().isEmpty())
		    {
				if(objBean.getStrModifierName().startsWith("-->")){
		    		modifierName=objBean.getStrModifierName().substring(0, 3);
		    	}
				else
				{
					modifierName="-->" +objBean.getStrModifierName();
				}
				List list=objUtilityController.funGetDocumentCode("POSItemModifierMaster");
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
	                	modifierCode = "M00" + intCode;
	                }
	                else if (intCode < 100)
	                {
	                	modifierCode = "M0" + intCode;
	                }
	                else
	                {
	                	modifierCode = "M" + intCode;
	                }

	            }
	            else
	            {
	            	modifierCode = "M001";
	            }
				
		     }
			else
			{
				modifierName=objBean.getStrModifierName();
			}
			
			clsModifierMasterHdModel objModel = new clsModifierMasterHdModel(new clsModifierMasterModel_ID(modifierCode, clientCode));
			objModel.setStrModifierName(modifierName);
		    objModel.setDocCode(modifierCode);
		    objModel.setStrModifierDesc(objBean.getStrModifierDescription());
		    objModel.setStrModifierGroupCode(hmModifierGroupName.get(objBean.getStrModifierGroup()));
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    objModel.setDteDateCreated(objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobalFunctions.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setStrDataPostFlag("N");
		    
		    Set<clsItemModifierMasterModel> setItemModifierDtl = new HashSet<clsItemModifierMasterModel>();
		    if(listItem!=null)
		    {
			    for(int i=0;i<listItem.size();i++)
			    {
			    	clsPOSMenuItemMasterBean objItem=listItem.get(i);
			    	
			    		    	
			    	clsItemModifierMasterModel objItemModel = new clsItemModifierMasterModel();
			    	objItemModel.setStrItemCode(objItem.getStrItemCode());
			    	objItemModel.setStrApplicable(objGlobalFunctions.funIfNull(objBean.getStrApplicable(),"n","y"));
			    	objItemModel.setStrChargable(objGlobalFunctions.funIfNull(objBean.getStrChargable(),"n","y"));
			    	objItemModel.setDblRate(objItem.getDblPurchaseRate());
			    	objItemModel.setStrDefaultModifier("N");
			    	System.out.println(objItemModel);
			    	
			    	if(objItem.getStrSelect()!=null && objItem.getStrSelect().equalsIgnoreCase("Tick"))
			    	{
			    		setItemModifierDtl.add(objItemModel);
			    	}
			    	  
			    		
			    }
		    }
		    System.out.println(setItemModifierDtl);
		    objModel.setSetItemModifierDtl(setItemModifierDtl);
		    objMasterService.funSaveUpdateItemModifierMaster(objModel);
		    
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+modifierCode);
										
				
				return new ModelAndView("redirect:/frmPOSItemModifier.html");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmPOSItemModifier.html");
			}
		}
	
	@RequestMapping(value ="/checkModName" ,method =RequestMethod.GET)
	public  @ResponseBody boolean funCheckMenuName(@RequestParam("modName")  String modName,@RequestParam("modCode")  String modCode,HttpServletRequest req) 
	{
		String clientCode =req.getSession().getAttribute("gClientCode").toString();

		int count=objPOSGlobal.funCheckName(modName,modCode,clientCode,"POSModifier");
		if(count>0)
		 return false;
		else
			return true;
	}
}	
	