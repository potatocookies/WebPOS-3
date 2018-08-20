package com.sanguine.webpos.controller;

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
import com.sanguine.webpos.bean.clsPOSShiftMasterBean;
import com.sanguine.webpos.model.clsShiftMasterModel;
import com.sanguine.webpos.model.clsShiftMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSShiftMasterController{

	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsGlobalFunctions objGlobal=null;
	
	@Autowired
	clsPOSUtilityController objUtilityController;
	
	@Autowired
	private clsPOSMasterService obMasterService;
	
	Map mapPOS=new HashMap();
//Open ShiftMaster
	
	@RequestMapping(value = "/frmPOSShiftMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		List listpos=obMasterService.funFullPOSCombo(clientCode);
		if(listpos.size()>0){
			String strPOSName="",strPOSCode="";
			for(int i =0 ;i<listpos.size();i++)
			{
				 Object ob[]=(Object[])listpos.get(i);
				 strPOSName=ob[1].toString();
				 strPOSCode=ob[0].toString();
				 mapPOS.put(strPOSCode,strPOSName);
			}
		}
		model.put("posList",mapPOS);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSShiftMaster_1","command", new clsPOSShiftMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSShiftMaster","command", new clsPOSShiftMasterBean());
		}else {
		return null;
		}
	}
	
	@RequestMapping(value = "/savePOSShiftMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSShiftMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String posCode="";
		String shiftCode = "";
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			shiftCode=objBean.getIntShiftCode();
			if (shiftCode.trim().isEmpty())
		    {
		    	List list=objUtilityController.funGetDocumentCode("POSShiftMaster");
		    	if (list.size()>(0))
				{
				    int code =  Integer.parseInt(list.get(0).toString());
				    code++;
				    shiftCode=String.valueOf(code);
				}
				else
				{
					shiftCode = "1";
				}	
		    }
			String StartTime=objBean.getStrtimeShiftStart()+" "+objBean.getStrAMPMStart();
		    String StartEnd= objBean.getStrtimeShiftEnd()+" "+objBean.getStrAMPMEnd();
			clsShiftMasterModel objModel = new clsShiftMasterModel(new clsShiftMasterModel_ID(shiftCode,clientCode));
			   
		    objModel.setStrPOSCode(objBean.getStrPOSCode());
	        objModel.setTmeShiftStart(StartTime);
	        objModel.setTmeShiftEnd(StartEnd);
	        objModel.setStrBillDateTimeType(objBean.getStrBillDateTimeType());
	     
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
	
		    obMasterService.funSaveShiftMaster(objModel);
		    
		
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+shiftCode);
									
			return new ModelAndView("redirect:/frmPOSShiftMaster.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	

	@RequestMapping(value = "/loadPOSShiftMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSShiftMasterBean funSetSearchFields(@RequestParam("POSShiftCode") String shiftCode,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSShiftMasterBean objPOSShiftMaster = null;
		String posName = null;
		
		try{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("shiftCode",shiftCode);
			hmParameters.put("clientCode",clientCode);
			
			clsShiftMasterModel objModel = obMasterService.funLoadShiftMaster(hmParameters);														
				
			objPOSShiftMaster = new clsPOSShiftMasterBean();
			objPOSShiftMaster.setIntShiftCode(objModel.getIntShiftCode());
				
			String[] strShiftStart=(String[])objModel.getTmeShiftStart().split(" ");
			String[] strShiftEnd=(String[])objModel.getTmeShiftEnd().split(" ");
			objPOSShiftMaster.setStrPOSCode(objModel.getStrPOSCode());
			objPOSShiftMaster.setStrtimeShiftStart((String) strShiftStart[0]);
			objPOSShiftMaster.setStrtimeShiftEnd((String) strShiftEnd[0]);
			objPOSShiftMaster.setStrBillDateTimeType(objModel.getStrBillDateTimeType());
			objPOSShiftMaster.setStrAMPMStart((String)  strShiftStart[1]);
			objPOSShiftMaster.setStrAMPMEnd((String) strShiftEnd[1]);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(null==objPOSShiftMaster)
		{
			objPOSShiftMaster = new clsPOSShiftMasterBean();
			objPOSShiftMaster.setIntShiftCode("Invalid Code");
		}
		
		return objPOSShiftMaster;
	}

}
