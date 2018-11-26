package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
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
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSWaiterMasterBean;
import com.sanguine.webpos.model.clsWaiterMasterModel;
import com.sanguine.webpos.model.clsWaiterMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSWaiterMasterController 
{

	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;

	
	@RequestMapping(value = "/frmPOSWaiterMaster", method = RequestMethod.GET)

	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSWaiterMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		
		
		Map map=new HashMap();
		
		List list = objMasterService.funFullPOSCombo(clientCode);
		map.put("All", "All");
		if(list!=null)
		{
			for(int i =0 ;i<list.size();i++)
			{
				Object obj =  list.get(i);
			
				map.put(Array.get(obj,0),Array.get(obj, 1));
			}
		}
			model.put("posList",map);
	
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWaiterMaster_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWaiterMaster");
		}else {
			return null;
		}
		 
	}
	
	
	
	
	
	@RequestMapping(value = "/savePOSWaiterMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSWaiterMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String posCode="";
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String waiterNo = objBean.getStrWaiterNo();
			
			if (waiterNo.trim().isEmpty())
		    {
		    	List list=objUtilityController.funGetDocumentCode("POSWaiterMaster");
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
							waiterNo = "W0" + intCode;
						}
						else if (intCode < 100)
						{
							waiterNo = "W" + intCode;
						}
						
						
					}
				    else
				    {
				    	waiterNo = "W01";
				    }
		    }
		    
		    clsWaiterMasterModel objModel=new clsWaiterMasterModel(new clsWaiterMasterModel_ID(waiterNo, clientCode));
		    objModel.setStrWShortName(objBean.getStrWShortName());
		    objModel.setStrWFullName(objBean.getStrWShortName());
		    objModel.setStrDebitCardString(objBean.getStrDebitCardString());
		    objModel.setStrPOSCode(objBean.getStrPOSCode());
		   
		    objModel.setStrOperational(objGlobal.funIfNull(objBean.getStrOperational(),"N","Y"));
		   
		    objModel.setStrStatus("Normal");
		    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    
		    objMasterService.funSaveUpdateWaiterMaster(objModel);

						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+waiterNo);
									
			return new ModelAndView("redirect:/frmPOSWaiterMaster.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	
	
	
	//Assign filed function to set data onto form for edit transaction.
		@RequestMapping(value = "/loadPOSWaiterMasterData", method = RequestMethod.GET)
		public @ResponseBody clsPOSWaiterMasterBean funSetSearchFields(@RequestParam("POSWaiterCode") String waiterCode,HttpServletRequest req)throws Exception
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			clsPOSWaiterMasterBean objPOSWaiterMaster = new clsPOSWaiterMasterBean();
			String posName="";
			
			clsWaiterMasterModel objWaiterMasterModel = objMasterService.funGetSelectedWaiterMasterData(waiterCode, clientCode);
				objPOSWaiterMaster = new clsPOSWaiterMasterBean();
				objPOSWaiterMaster.setStrWaiterNo(objWaiterMasterModel.getStrWaiterNo());
				objPOSWaiterMaster.setStrWShortName(objWaiterMasterModel.getStrWShortName());
				objPOSWaiterMaster.setStrWFullName(objWaiterMasterModel.getStrWFullName());
				objPOSWaiterMaster.setStrOperational(objWaiterMasterModel.getStrOperational());
				objPOSWaiterMaster.setStrDebitCardString(objWaiterMasterModel.getStrDebitCardString());
				objPOSWaiterMaster.setStrPOSCode(objWaiterMasterModel.getStrPOSCode());
			
			if(null==objPOSWaiterMaster)
			{
				objPOSWaiterMaster = new clsPOSWaiterMasterBean();
				objPOSWaiterMaster.setStrWaiterNo("Invalid Code");
			}
			return objPOSWaiterMaster;
		}
	
		 @RequestMapping(value ="/checkWaiterName" ,method =RequestMethod.GET)
			public  @ResponseBody boolean funCheckAreaName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
			{
				String clientCode =req.getSession().getAttribute("gClientCode").toString();

				int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSWaiterMaster");
				if(count>0)
				 return false;
				else
					return true;
				
			}
}
