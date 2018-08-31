package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSChangePasswordBean;
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsPOSNonAvailableItemModel_ID;
import com.sanguine.webpos.model.clsPOSNonAvailableItemsModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSNonAvailableItemsController {
	
	@Autowired	
	private intfBaseService objBaseService;
	
	@Autowired
	private clsPOSMasterService objPOSMasterService;
	
	@Autowired
	private clsPOSMasterService	objMasterService;
	
	
	Map map=new HashMap();
	@RequestMapping(value = "/frmPOSNonAvailableItems", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSChangePasswordBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSNonAvailableItems_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSNonAvailableItems");
		}else {
			return null;
		}
		 
	}
	
	@RequestMapping(value = "/funFillItemTable", method = RequestMethod.GET)
	public @ResponseBody Map funloadBillAndDelBoyData(HttpServletRequest req) throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		
		List list = objPOSMasterService.funFillAllItemList(clientCode);
		for(int i =0 ;i<list.size();i++)
			{
				clsMenuItemMasterModel obj =  (clsMenuItemMasterModel) list.get(i);
				
				map.put(obj.getStrItemName(), obj.getStrItemCode());
			}	

		Map mapObjRet=new JSONObject();
	        
		mapObjRet.put("itemList",list);
	       

 		return mapObjRet;
		
	}
	
	 
	@RequestMapping(value = "/funGetNonAvailableItems", method = RequestMethod.GET)
	public @ResponseBody Map funGetNonAvailableItems(HttpServletRequest req) throws Exception
	{
		Map mapRet = new HashMap();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List itemList=new ArrayList();
		StringBuilder sqlBuilder= new StringBuilder(" select strItemCode, strItemName from tblnonavailableitems where strClientCode='"+clientCode+"' ");
		List list = objBaseService.funGetList(sqlBuilder, "sql");
        if(list.size()>0)
        {
	     for(int i=0;i<list.size();i++)
	     {
        	Object[] obj = (Object[])list.get(i);
	    	Map objMap=new HashMap();
	    	objMap.put("strItemCode",obj[0].toString());
	    	objMap.put("strItemName",obj[1].toString());
	    	itemList.add(objMap);
	     }
        }
        mapRet.put("NonAvailableItemList",itemList);
	       
		return mapRet;

	}
	@RequestMapping(value ="/funItemsMouseClicked", method = RequestMethod.POST)
	public ModelAndView  funItemsMouseClicked(HttpServletRequest req)
	{
		
		String urlHits="1";
		try
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String posDate = req.getSession().getAttribute("gPOSDate").toString();
			String itemName=req.getParameter("code");
			String itemCode="";
			if(map.containsKey(itemName))
			{
				itemCode=(String) map.get(itemName);
			}
			String posCode=req.getSession().getAttribute("loginPOS").toString();
			clsPOSNonAvailableItemsModel objModel = new clsPOSNonAvailableItemsModel(new clsPOSNonAvailableItemModel_ID(itemCode, clientCode,posCode));
			objModel.setStrItemCode(itemCode);
			objModel.setStrItemName(itemName);
			objModel.setStrClientCode(clientCode);
			objModel.setDteDate(posDate);
			objModel.setStrPOSCode(posCode);
			objMasterService.funSaveUpdateAreaMaster(objModel);
			
            req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," ");			
			
									
			return new ModelAndView("redirect:/frmPOSNonAvailableItems.html?saddr="+urlHits);
		}
		catch(Exception e)
		{
			urlHits="1";
			e.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	@RequestMapping(value ="/funRemoveNonAvailableItem", method = RequestMethod.POST)
	public ModelAndView funRemoveNonAvailableItem(HttpServletRequest req)
	{
		
		String urlHits="1";
		
		try
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String itemName=req.getParameter("code");
			String posCode=req.getSession().getAttribute("loginPOS").toString();
			String itemCode="";
			
			if(map.containsKey(itemName))
			{
				itemCode=(String) map.get(itemName);
			}
			 
		    StringBuilder sqlBuilder = new StringBuilder("delete from tblnonavailableitems where"
	                 + " strItemCode='"+itemCode+"' and strClientCode='"+clientCode+"' and strPOSCode='"+posCode+"' ");
		    objBaseService.funExecuteUpdate(sqlBuilder.toString(), "sql");
		    
		    System.out.println("success");
			
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," ");
									
			return new ModelAndView("redirect:/frmPOSNonAvailableItems.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	
}
