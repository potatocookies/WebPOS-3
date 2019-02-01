package com.sanguine.webpos.controller;

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

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSGroupMasterBean;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsGroupMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSGroupMasterController {

		
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;
	
	@RequestMapping(value = "/frmGroup", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		//return new ModelAndView("frmPOSGroupMaster");
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSGroupMaster_1","command", new clsPOSGroupMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSGroupMaster","command", new clsPOSGroupMasterBean());
		}else {
			return null;
		}
		 
	}
	
	
	@RequestMapping(value = "/savePOSGroupMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSGroupMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String groupCode = objBean.getStrGroupCode();
			
			if (groupCode.trim().isEmpty())
		    {
		    	//groupCode = objGroupMasterDao.funGenerateGroupCode();
		    	List list=objUtilityController.funGetDocumentCode("POSGroupMaster");
		    	if(list.size()>0)
		    	{
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
					    	groupCode = "G000000" + intCode;
					    }
					    else if (intCode < 100)
					    {
					    	groupCode = "G00000" + intCode;
					    }
					    else if (intCode < 1000)
					    {
					    	groupCode = "G0000" + intCode;
					    }
					    else if (intCode < 10000)
					    {
					    	groupCode = "G000" + intCode;
					    }
					    else if (intCode < 100000)
					    {
					    	groupCode = "G00" + intCode;
					    }
					    else if (intCode < 1000000)
					    {
					    	groupCode = "G0" + intCode;
					    }
					}
					else
					{
					    groupCode = "G0000001";
					}
		    	}
		    	else
				{
				    groupCode = "G0000001";
				}
		    	
		    }
		    clsGroupMasterModel objModel = new clsGroupMasterModel(new clsGroupMasterModel_ID(groupCode, clientCode));
		    objModel.setStrGroupName(objBean.getStrGroupName());
		    objModel.setStrOperationalYN(objGlobal.funIfNull(objBean.getStrOperational(),"N","Y"));
		    objModel.setStrGroupShortName(objBean.getStrShortName());
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setStrDataPostFlag("N");
		    objMasterService.funSaveUpdateGroupMaster(objModel);

			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+groupCode);
			
			String sql = "update tblmasteroperationstatus set dteDateEdited='"+objGlobal.funGetCurrentDateTime("yyyy-MM-dd")+"'  where strTableName='Group' ";
			objBaseServiceImpl.funExecuteUpdate(sql,"sql");
									
			return new ModelAndView("redirect:/frmGroup.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	
	//Assign filed function to set data onto form for edit transaction.
		@RequestMapping(value = "/loadPOSGroupMasterData", method = RequestMethod.GET)
		public @ResponseBody clsPOSGroupMasterBean funSetSearchFields(@RequestParam("POSGroupCode") String groupCode,HttpServletRequest req)throws Exception
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			clsPOSGroupMasterBean objPOSGroupMaster = new clsPOSGroupMasterBean();
			clsGroupMasterModel objGroupMasterModel = objMasterService.funSelectedGroupMasterData(groupCode, clientCode);
			objPOSGroupMaster.setStrGroupCode(objGroupMasterModel.getStrGroupCode());
			objPOSGroupMaster.setStrGroupName(objGroupMasterModel.getStrGroupName());
			objPOSGroupMaster.setStrOperational(objGroupMasterModel.getStrOperationalYN());
			objPOSGroupMaster.setStrShortName(objGroupMasterModel.getStrGroupShortName());
			objPOSGroupMaster.setStrOperationType("U");
			
			if(null==objPOSGroupMaster)
			{
				objPOSGroupMaster = new clsPOSGroupMasterBean();
				objPOSGroupMaster.setStrGroupCode("Invalid Code");
			}
			
			return objPOSGroupMaster;
		}
		
		 @RequestMapping(value ="/CheckPosGroupName" ,method =RequestMethod.GET)
			public  @ResponseBody boolean funCheckAdvOrderName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
			{
				String clientCode =req.getSession().getAttribute("gClientCode").toString();
				int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSGroupMaster");
				if(count>0)
				 return false;
				else
					return true;
				
			}
}
