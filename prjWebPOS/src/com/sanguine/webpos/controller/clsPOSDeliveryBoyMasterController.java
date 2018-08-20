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
import com.sanguine.webpos.bean.clsPOSDeliveryBoyChargesBean;
import com.sanguine.webpos.bean.clsPOSDeliveryBoyMasterBean;
import com.sanguine.webpos.model.clsDeliveryBoyChargesModel;
import com.sanguine.webpos.model.clsDeliveryBoyMasterModel;
import com.sanguine.webpos.model.clsDeliveryBoyMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSDeliveryBoyMasterController {

	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	@Autowired
	private clsPOSUtilityController objUtilityController;
	@Autowired
	clsPOSMasterService objMasterService;
	
	
	Map map=new HashMap();
	
	@RequestMapping(value = "/frmPOSDeliveryPersonMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSDeliveryBoyMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
	
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDeliveryPersonMaster_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDeliveryPersonMaster");
		}else {
			return null;
		}
		 
	}
	
	
	@RequestMapping(value = "/savePOSDeliveryBoyMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSDeliveryBoyMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String posCode="";
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
	
		    String dpCode = objBean.getStrDPCode();
		    if (dpCode.trim().isEmpty())
			{
				List list=objUtilityController.funGetDocumentCode("POSDeliveryBoyMaster");
				
				   if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						String code = list.get(0).toString();
						StringBuilder sb = new StringBuilder(code);
						String ss = sb.delete(0, 2).toString();
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
							dpCode = "DB00000" + intCode;
						}
						else if (intCode < 100)
						{
							dpCode = "DB0000" + intCode;
						}
						else if (intCode < 1000)
						{
							dpCode = "DB000" + intCode;
						}
						else if (intCode < 10000)
						{
							dpCode = "DB00" + intCode;
						}
						else if (intCode < 100000)
						{
							dpCode = "DB0" + intCode;
						}
						else if (intCode < 1000000)
						{
							dpCode = "DB" + intCode;
						}
					}
					else
					{
						dpCode = "DB000001";
					}
			}

			clsDeliveryBoyMasterModel objModel = new clsDeliveryBoyMasterModel(new clsDeliveryBoyMasterModel_ID(dpCode, clientCode));
			objModel.setStrOperational(objBean.getStrOperational());
			objModel.setStrDPName(objBean.getStrDPName());
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(webStockUserCode);
			objModel.setStrUserEdited(webStockUserCode);
			objModel.setStrAddressLine1("");
			objModel.setStrAddressLine2("");
			objModel.setStrAddressLine3("");
			objModel.setStrCity("");
			objModel.setStrDBCategoryCode("");
			objModel.setStrDeliveryArea("");
			objModel.setStrState("");
			
			List<clsPOSDeliveryBoyChargesBean> list=objBean.getListDeliveryBoyCharges();
			List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl = new ArrayList<clsDeliveryBoyChargesModel>();
		    if(null!=list)
		    {
		    for(int i=0; i<list.size(); i++)
		    {
		    	clsDeliveryBoyChargesModel objDelChargesModel = new clsDeliveryBoyChargesModel();
		    	clsPOSDeliveryBoyChargesBean obj= new clsPOSDeliveryBoyChargesBean();
		    	obj=(clsPOSDeliveryBoyChargesBean)list.get(i);
		    	
		    		if(null!=obj.getStrAreaCode())
		    		{
		    		objDelChargesModel.setStrCustAreaCode(obj.getStrAreaCode());
		    		objDelChargesModel.setDblValue(obj.getDblIncentives());
		    		objDelChargesModel.setStrUserCreated(webStockUserCode);
			    	objDelChargesModel.setStrUserEdited(webStockUserCode);
			    	objDelChargesModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    	objDelChargesModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    	objDelChargesModel.setStrDataPostFlag("Y");
			    	listDeliveryChargesDtl.add(objDelChargesModel);
		    		map.put(obj.getStrAreaCode(), obj.getStrAreaName());
		    		}
		    }
		    }
		    
		    
		    objModel.setListDeliveryChargesDtl(listDeliveryChargesDtl);
		    objMasterService.funSaveUpdateDeliverPersonMaster(objModel);
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+dpCode);
									
			return new ModelAndView("redirect:/frmPOSDeliveryPersonMaster.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	 
	@RequestMapping(value = "/loadPOSDeliveryBoyMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSDeliveryBoyMasterBean funSetSearchFields(@RequestParam("dpCode") String dpCode,HttpServletRequest req)
	{
		clsPOSDeliveryBoyMasterBean objPOSWaiterMaster = new clsPOSDeliveryBoyMasterBean();
		try
		{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		
		
		List<clsPOSDeliveryBoyChargesBean> listSettleData=new ArrayList<clsPOSDeliveryBoyChargesBean>();
		
		clsDeliveryBoyMasterModel objModel= (clsDeliveryBoyMasterModel) objMasterService.funSelectedDeliveryBoyMasterData(dpCode,clientCode);
		clsPOSDeliveryBoyChargesBean objSettlementDtl = new clsPOSDeliveryBoyChargesBean();
		List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl =objModel.getListDeliveryChargesDtl();
		List<clsPOSDeliveryBoyChargesBean> list=new ArrayList<clsPOSDeliveryBoyChargesBean>();
		for(int i=0; i<listDeliveryChargesDtl.size(); i++)
			
		{
			clsDeliveryBoyChargesModel objSettle=(clsDeliveryBoyChargesModel)listDeliveryChargesDtl.get(i);
			clsPOSDeliveryBoyChargesBean obj = new clsPOSDeliveryBoyChargesBean();
			obj.setStrAreaCode(objSettle.getStrCustAreaCode());
			String areaCode=objSettle.getStrCustAreaCode();
			String	areaName="";
			if(map.containsKey(areaCode))
			{
			areaName=(String) map.get(areaCode);
				
			}
			obj.setStrAreaName(areaName);
			
			obj.setDblIncentives(objSettle.getDblValue());
			list.add(obj);
		}
		objPOSWaiterMaster.setStrDPCode(objModel.getStrDPCode());
		objPOSWaiterMaster.setStrDPName(objModel.getStrDPName());
		objPOSWaiterMaster.setStrOperational(objModel.getStrOperational());
		objPOSWaiterMaster.setListDeliveryBoyCharges(list);
		  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
//		if(null==objPOSWaiterMaster)
//		{
//			objPOSWaiterMaster = new clsDeliveryBoyMasterBean();
//			objPOSWaiterMaster.setStrDPCode("Invalid Code");
//		}
//		
		return objPOSWaiterMaster;
	}
	
	
	 @RequestMapping(value ="/checkDPName" ,method =RequestMethod.GET)
		public  @ResponseBody boolean funCheckDPName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
		{
			String clientCode =req.getSession().getAttribute("gClientCode").toString();

			int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSDeliveryBoyMaster");

			//int count=objPOSGlobal.funCheckName("",Name,clientCode,"POSDeliveryBoyMaster");

			if(count>0)
			 return false;
			else
				return true;
			
		}
		
}
