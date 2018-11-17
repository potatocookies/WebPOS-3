package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSWiseItemIncentiveBean;
import com.sanguine.webpos.bean.clsPOSWiseItemIncentiveDtlBean;
import com.sanguine.webpos.model.clsPOSWiseItemIncentiveModel;
import com.sanguine.webpos.model.clsPOSWiseItemIncentiveModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSWiseItemIncentiveController {
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	
	@Autowired
	clsPOSMasterService objMasterService;
	

	 Map map=new HashMap();
	 Map map1=new HashMap();
	 
	@RequestMapping(value = "/frmPOSWiseItemIncentive", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}
		catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		List list = new ArrayList();
		List poslist = new ArrayList();
		map1.put("ALL","ALL");
		
		list = objMasterService.funFillPOSCombo(clientCode);
		for(int i =0 ;i<list.size();i++)
		{
			Object[] obj = (Object[]) list.get(i);
			poslist.add(obj[1].toString());
			String POSName= obj[1].toString();
			String POSCode= obj[0].toString();
			map1.put(POSCode,POSName);
			map.put(POSName,POSCode);
		}
		model.put("posList",map1);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWiseItemIncentive_1","command", new clsPOSWiseItemIncentiveBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWiseItemIncentive","command", new clsPOSWiseItemIncentiveBean());
		}else {
			return null;
		}
}
	
	@RequestMapping(value ="/savePOSWiseItemIncentive", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSWiseItemIncentiveBean objBean,BindingResult result,HttpServletRequest req)
	{
		
		System.out.println(objBean);
		try
		{
			
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			List<clsPOSWiseItemIncentiveDtlBean> listdata=objBean.getListItemIncentive();
			clsPOSWiseItemIncentiveDtlBean obj; 
			for(int i=0; i<listdata.size(); i++)
			{	
				obj = listdata.get(i);
				String strItemCode = obj.getStrItemCode();
				String strItemName =obj.getStrItemName();
				String strIncentiveType = obj.getStrIncentiveType();;
				double strIncentiveValue = Double.parseDouble(obj.getStrIncentiveValue());
				String strPOSName = obj.getStrPOSName();
				String strPOSCode="";
				if(map.containsKey(strPOSName))
				{
					strPOSCode = (String) map.get(strPOSName);
				}
				    
				  clsPOSWiseItemIncentiveModel objModel = new clsPOSWiseItemIncentiveModel(new clsPOSWiseItemIncentiveModel_ID(strPOSCode,strItemCode,clientCode));
				   
				  objModel.setStrPOSCode(strPOSCode);
			        objModel.setStrItemCode(strItemCode);
				    objModel.setStrItemName(strItemName);
				    objModel.setStrIncentiveType(strIncentiveType);
				    objModel.setDblIncentiveValue(strIncentiveValue);
				    objModel.setStrClientCode(clientCode);
				    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModel.setStrDataPostFlag("N");
			    
				    objMasterService.funSaveUpdatePOSWiseItemIncentive(objModel);
				    
			    }
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," ");
									
			return new ModelAndView("redirect:/frmPOSWiseItemIncentive.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value={"/loadPOSWiseItemIncentiveData"}, method=RequestMethod.POST)
    @ResponseBody
    public Map FunLoadPOSwiseIncentive(HttpServletRequest req)throws Exception
    {
	  LinkedHashMap resMap = new LinkedHashMap();
	     
        
        String clientCode=req.getSession().getAttribute("gClientCode").toString();

		
		String strPOSCode=req.getParameter("POSCode");
	
		
		resMap=FunGetData(clientCode,strPOSCode);
        return resMap;
    }
	
	@SuppressWarnings({ "unchecked" })
	private LinkedHashMap FunGetData(String clientCode,String strPOSCode)throws Exception
	  {									
	    LinkedHashMap resMap = new LinkedHashMap();				     

		List list =new ArrayList();
		List totalList=new ArrayList();
		
		totalList = objMasterService.funGetListItemWiseIncentive(clientCode,strPOSCode);
	   	if(totalList!=null)
	   	{
	   		for(int i=0;i<totalList.size();i++)
	   		{
	   			clsPOSWiseItemIncentiveDtlBean objBean =(clsPOSWiseItemIncentiveDtlBean) totalList.get(i);
	   			List arrList=new ArrayList();
	   			arrList.add(objBean.getStrItemCode());
	   			arrList.add(objBean.getStrItemName());
	   			arrList.add(objBean.getStrGroupName());
	   			arrList.add(objBean.getStrSubGroupName());
	   			arrList.add(objBean.getStrPOSCode());
	   			arrList.add(objBean.getStrIncentiveType());
	   			arrList.add(objBean.getStrIncentiveValue());
	   			list.add(arrList);
	   		}
	   	}
	   		 resMap.put("list", list);

		return resMap;
   }
	
}
