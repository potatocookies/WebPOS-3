package com.sanguine.webpos.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAreaMasterBean;
import com.sanguine.webpos.bean.clsPOSGroupMasterBean;
import com.sanguine.webpos.bean.clsPOSMasterBean;
import com.sanguine.webpos.bean.clsPOSTaxMasterBean;
import com.sanguine.webpos.bean.clsPOSSettlementDetailsBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsTaxMasterModel;
import com.sanguine.webpos.model.clsTaxMasterModel_ID;
import com.sanguine.webpos.model.clsTaxOnGroupModel;
import com.sanguine.webpos.model.clsTaxPosDetailsModel;
import com.sanguine.webpos.model.clsTaxSettlementDetailsModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Transactional(value="webPOSTransactionManager")
@Controller
public class clsPOSTaxMasterController{
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;

	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	clsPOSUtilityController objUtility;
	
	private Map<String,String> hmPOSData=new HashMap<String, String>(); 
	private Map<String,String> hmAreaData=new HashMap<String, String>(); 
	private Map<String,String> hmTaxData=new HashMap<String, String>(); 
	//Open POSTaxMaster
	@RequestMapping(value = "/frmPOSTaxMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
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
			return new ModelAndView("frmPOSTaxMaster_1","command", new clsPOSTaxMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSTaxMaster","command", new clsPOSTaxMasterBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	@RequestMapping(value = "/savePOSTaxMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSTaxMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String taxCode = "";
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String userCode=req.getSession().getAttribute("gUserCode").toString();
			
			 
			taxCode = objBean.getStrTaxCode();
			
			if (taxCode.trim().isEmpty())
		    {
		    	List list=objUtility.funGetDocumentCode("POSTaxMaster");
		    	
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
							taxCode = "T0" + intCode;
						}
						else if (intCode < 100)
						{
							taxCode = "T" + intCode;
						}
						
					}
					else
					{
						taxCode = "T01";
					}
		    }
			
			 clsTaxMasterModel objModel=new clsTaxMasterModel(new clsTaxMasterModel_ID(taxCode,clientCode));
			    
			    objModel.setStrAccountCode(objBean.getStrAccountCode());
			    objModel.setStrDataPostFlag("Y");
			    objModel.setStrItemType(objBean.getStrItemType());
			    objModel.setStrTaxCalculation(objBean.getStrTaxCalculation());
			    objModel.setStrTaxDesc(objBean.getStrTaxDesc());
			    objModel.setStrTaxIndicator(objBean.getStrTaxIndicator());
			    objModel.setStrTaxOnGD(objBean.getStrTaxOnGD());
			    objModel.setStrTaxOnSP( objBean.getStrTaxOnSP());
			    objModel.setStrTaxOnTax(objGlobal.funIfNull(objBean.getStrTaxOnTax(),"N","Y"));
			    objModel.setStrTaxRounded(objGlobal.funIfNull(objBean.getStrTaxRounded(),"N","Y"));
			    objModel.setStrTaxShortName(objBean.getStrTaxShortName());
			    objModel.setStrTaxType(objBean.getStrTaxType());
			    objModel.setDblAmount(objBean.getDblAmount());
			    objModel.setDblPercent(objBean.getDblPercent());
			    objModel.setDteValidFrom( objBean.getDteValidFrom());
			    objModel.setDteValidTo(objBean.getDteValidTo());
			    
			    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setStrUserCreated(userCode);
			    objModel.setStrUserEdited(userCode);
			   //Area Data
			    List<clsPOSAreaMasterBean> arealist=objBean.getListAreaCode();
			    String areaCode="";
			    if(null!=arealist)
			    {
			    for(int i=0; i<arealist.size(); i++){
				    	clsPOSAreaMasterBean obj= new clsPOSAreaMasterBean();
				    	obj=(clsPOSAreaMasterBean)arealist.get(i);
				    	if(obj.getStrApplicableYN()!=null){
				    		areaCode += "," + obj.getStrAreaCode();
				    	}
				    }
			    }
			    StringBuilder sb = new StringBuilder(areaCode);
	            areaCode = sb.delete(0, 1).toString();
			    objModel.setStrAreaCode(areaCode);
			    
			    String operationType="";
			    operationType = objGlobal.funIfNull(objBean.getStrHomeDelivery(),"",",HomeDelivery")+objGlobal.funIfNull(objBean.getStrDinningInn(),"",",DineIn")+objGlobal.funIfNull(objBean.getStrTakeAway(),"",",TakeAway");
			    sb = new StringBuilder(operationType);
	            operationType = sb.delete(0, 1).toString();	
	            objModel.setStrOperationType(operationType);
	
	          //Tax Data
			    String taxOnTaxCode="";
			    List<clsPOSTaxMasterBean> taxlist=objBean.getListTaxOnTaxCode();
			    if(null!=taxlist)
			    {
				    for(int i=0; i<taxlist.size(); i++)
				    {
				    	clsPOSTaxMasterBean obj= new clsPOSTaxMasterBean();
				    	obj=(clsPOSTaxMasterBean)taxlist.get(i);
				    	if(obj.getStrApplicableYN()!=null)
				    	{
				    		taxOnTaxCode += "," + obj.getStrTaxCode();
						}
				    }
			    }
			    sb = new StringBuilder(taxOnTaxCode);
	  		  	taxOnTaxCode = sb.delete(0, 1).toString();
	  		    objModel.setStrTaxOnTaxCode(taxOnTaxCode);
			    
	  		//Settlement Data
	  		  List<clsPOSSettlementDetailsBean> list=objBean.getListSettlementCode();
	  		  Set<clsTaxSettlementDetailsModel> setSettlementDtl = new HashSet<clsTaxSettlementDetailsModel>();
			    if(null!=list)
			    {
				    for(int i=0; i<list.size(); i++)
				    {
				    	clsPOSSettlementDetailsBean obj= new clsPOSSettlementDetailsBean();
				    	obj=(clsPOSSettlementDetailsBean)list.get(i);
				    	if(obj.getStrApplicableYN()!=null)
				    	{
				    		String settlementCode=obj.getStrSettlementCode();
					    	String settlementDesc=obj.getStrSettlementDesc();
					    	clsTaxSettlementDetailsModel objSettlementModel = new clsTaxSettlementDetailsModel();
					    	
					    	objSettlementModel.setStrSettlementCode(settlementCode);
					    	objSettlementModel.setStrApplicable("true");
					    	
					    	objSettlementModel.setStrSettlementName(settlementDesc);
					    	objSettlementModel.setStrUserEdited(userCode);
					    	objSettlementModel.setStrUserCreated(userCode);
					    	  
					    	objSettlementModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					    	
					    	objSettlementModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					    	objSettlementModel.setDteFrom( objBean.getDteValidFrom());
					    	objSettlementModel.setDteTo( objBean.getDteValidTo());
					    	setSettlementDtl.add(objSettlementModel);
				    	}
				    	
				    }
			    }
			    objModel.setListsettlementDtl(setSettlementDtl);
			
			//Group Data
		    
		    List<clsPOSGroupMasterBean> gList=objBean.getListGroupCode();
		    Set<clsTaxOnGroupModel> setGroupDtl = new HashSet<clsTaxOnGroupModel>();
		    if(null!=gList)
		    {
			    for(int i=0; i<gList.size(); i++)
			    {
			    	clsPOSGroupMasterBean obj= new clsPOSGroupMasterBean();
			    	obj=(clsPOSGroupMasterBean)gList.get(i);
			    	if(obj.getStrApplicableYN()!=null)
			    	{
			    		String gpCode=obj.getStrGroupCode();
				    	String gpDesc=obj.getStrGroupName();
				    	clsTaxOnGroupModel objSettlementModel = new clsTaxOnGroupModel();
				    	
				    	objSettlementModel.setStrGroupCode(gpCode);
				    	objSettlementModel.setStrApplicable("true");
				    	
				    	objSettlementModel.setStrGroupName(gpDesc);
				    	objSettlementModel.setStrUserEdited(userCode);
				    	objSettlementModel.setStrUserCreated(userCode);
				    	  
				    	objSettlementModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    	
				    	objSettlementModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    	objSettlementModel.setDteFrom( objBean.getDteValidFrom());
				    	objSettlementModel.setDteTo( objBean.getDteValidTo());
				    	setGroupDtl.add(objSettlementModel);
			    	}
			    	
			    }
		    }
		    objModel.setListTaxGroupDtl(setGroupDtl);
		    
		    
		    //Pos Data
		    List<clsPOSMasterBean> poslist=objBean.getListPOSCode();
		    Set<clsTaxPosDetailsModel> listTaxPosDtl = new HashSet<clsTaxPosDetailsModel>();
		    
		    if(null!=poslist)
		    {
		    for(int i=0; i<poslist.size(); i++)
		    {
		    	clsPOSMasterBean obj= new clsPOSMasterBean();
		    	obj=(clsPOSMasterBean)poslist.get(i);
		    	if(obj.getStrApplicableYN()!=null)
		    	{
		    		
		    		clsTaxPosDetailsModel objPosModel = new clsTaxPosDetailsModel();
			    	objPosModel.setStrTaxDesc(objBean.getStrTaxDesc());
			    	objPosModel.setStrPOSCode(obj.getStrPosCode());
			    	listTaxPosDtl.add(objPosModel);
		    		
		    	}
		    	
		    }
		    }
		    
		    objModel.setListTaxPosDtl(listTaxPosDtl);
		   	
		    objModel.setStrBillNote(objBean.getStrBillNote()); 
		    
		    objMasterService.funSaveTaxMaster(objModel);				
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+taxCode);
									
			return new ModelAndView("redirect:/frmPOSTaxMaster.html?saddr="+urlHits);
			
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	
	@RequestMapping(value = "/loadPOSTaxMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSTaxMasterBean funSetSearchFields(@RequestParam("taxCode") String taxCode,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSTaxMasterBean objTaxBean =  new clsPOSTaxMasterBean();
		List<clsPOSSettlementDetailsBean> listSettleData=new ArrayList<clsPOSSettlementDetailsBean>();
		List<clsPOSMasterBean> listPOS=new ArrayList<clsPOSMasterBean>();
		List<clsPOSGroupMasterBean> listGroup=new ArrayList<clsPOSGroupMasterBean>();
		List<clsPOSAreaMasterBean> listArea=new ArrayList<clsPOSAreaMasterBean>();
		List<clsPOSTaxMasterBean> listTax=new ArrayList<clsPOSTaxMasterBean>();
		
		    try{
		    	
		    	clsTaxMasterModel objTaxModel =objMasterService.funSelectedTaxMasterData(taxCode, clientCode);
		        Set<clsTaxSettlementDetailsModel> listsettlementDtl =objTaxModel.getListsettlementDtl();
				Iterator itr = listsettlementDtl.iterator();
				clsPOSSettlementDetailsBean objSettlementDtl ;
		        while(itr.hasNext())
		        {
		        	clsTaxSettlementDetailsModel objSettle=(clsTaxSettlementDetailsModel)itr.next();
					objSettlementDtl = new clsPOSSettlementDetailsBean();
					objSettlementDtl.setStrSettlementCode(objSettle.getStrSettlementCode());
					objSettlementDtl.setStrSettlementDesc(objSettle.getStrSettlementName());
					objSettlementDtl.setStrApplicableYN(true);
					
					listSettleData.add(objSettlementDtl);
					
				}
		        objTaxBean.setListSettlementCode(listSettleData);
		        
		    	Set<clsTaxOnGroupModel> listTaxGroupDtl =objTaxModel.getListTaxGroupDtl();
			    itr = listTaxGroupDtl.iterator();
			        while(itr.hasNext())
			        {
			        	clsTaxOnGroupModel objTaxOnGrp=(clsTaxOnGroupModel)itr.next();
						clsPOSGroupMasterBean objPOSDtl = new clsPOSGroupMasterBean();
						objPOSDtl.setStrGroupCode(objTaxOnGrp.getStrGroupCode());
						objPOSDtl.setStrGroupName(objTaxOnGrp.getStrGroupName());
						objPOSDtl.setStrApplicableYN(true);
						
						listGroup.add(objPOSDtl);
					}
		        objTaxBean.setListGroupCode(listGroup);
				
		        Set<clsTaxPosDetailsModel> listTaxPosDtl=objTaxModel.getListTaxPosDtl();
				itr = listTaxPosDtl.iterator();
		        while(itr.hasNext())
		        {
			        clsTaxPosDetailsModel objPos=(clsTaxPosDetailsModel)itr.next();;
					clsPOSMasterBean objPOSDtl = new clsPOSMasterBean();
					objPOSDtl.setStrPosCode(objPos.getStrPOSCode());
					objPOSDtl.setStrPosName(hmPOSData.get(objPos.getStrPOSCode()));
					objPOSDtl.setStrApplicableYN(true);
						
					listPOS.add(objPOSDtl);
			    }
		        objTaxBean.setListPOSCode(listPOS);
				//Area Data
			    String[] spArea = objTaxModel.getStrAreaCode().split(",");
				for(int i=0; i<spArea.length; i++)
				{
					String areaCode=spArea[i];
					clsPOSAreaMasterBean objAreaDtl = new clsPOSAreaMasterBean();
					objAreaDtl.setStrAreaCode(areaCode);
					objAreaDtl.setStrAreaName(hmAreaData.get(areaCode));
					objAreaDtl.setStrApplicableYN(true);
					
					listArea.add(objAreaDtl);
				}
			 	objTaxBean.setListAreaCode(listArea);
			     //Tax On Tax
				 String[] spTaxOnTax = objTaxModel.getStrTaxOnTaxCode().split(",");
				 for(int i=0; i<spTaxOnTax.length; i++)
					{
						taxCode=spTaxOnTax[i];
						clsPOSTaxMasterBean objTaxDtl = new clsPOSTaxMasterBean();
						objTaxDtl.setStrTaxCode(taxCode);
						objTaxDtl.setStrTaxDesc(hmTaxData.get(taxCode));
						objTaxDtl.setStrApplicableYN(true);
						
						listTax.add(objTaxDtl);
					}
				 
				objTaxBean.setListTaxOnTaxCode(listTax);
			
				objTaxBean.setStrAccountCode(objTaxModel.getStrAccountCode());
				objTaxBean.setStrItemType(objTaxModel.getStrItemType());
				 String[] spOperation = (objTaxModel.getStrOperationType()).split(",");
				 for(int i=0; i<spOperation.length; i++)
					{
					 if(spOperation[i].equals("HomeDelivery"))
						 objTaxBean.setStrHomeDelivery("Y");
					 if(spOperation[i].equals("DineIn"))
						 objTaxBean.setStrDinningInn("Y");
					 if(spOperation[i].equals("TakeAway"))
						 objTaxBean.setStrTakeAway("Y");
					 
					}
				 
				objTaxBean.setStrTaxCalculation(objTaxModel.getStrTaxCalculation());
				objTaxBean.setStrTaxCode(objTaxModel.getStrTaxCode());
				objTaxBean.setStrTaxDesc(objTaxModel.getStrTaxDesc());
				objTaxBean.setStrTaxIndicator(objTaxModel.getStrTaxIndicator());
				objTaxBean.setStrTaxOnGD(objTaxModel.getStrTaxOnGD());
				objTaxBean.setStrTaxOnSP(objTaxModel.getStrTaxOnSP());
				objTaxBean.setStrTaxOnTax(objTaxModel.getStrTaxOnTax());
				objTaxBean.setStrTaxRounded(objTaxModel.getStrTaxRounded());
				objTaxBean.setStrTaxShortName(objTaxModel.getStrTaxShortName());

				objTaxBean.setDblAmount(objTaxModel.getDblAmount());
				objTaxBean.setDblPercent(objTaxModel.getDblPercent());
				objTaxBean.setStrTaxType(objTaxModel.getStrTaxType());
				objTaxBean.setDteValidFrom(objTaxModel.getDteValidFrom());
				objTaxBean.setDteValidTo(objTaxModel.getDteValidTo());
				
				objTaxBean.setStrBillNote(objTaxModel.getStrBillNote());
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	objTaxBean = new clsPOSTaxMasterBean();
				objTaxBean.setStrTaxCode("Invalid Code");
		    }
		
		
		return objTaxBean;
	}
	
	
	@RequestMapping(value = "/loadPOSData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSMasterBean> funLoadPOSData(HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List<clsPOSMasterBean> listPOSData=new ArrayList<clsPOSMasterBean>();
		
		List listPOSMasterModel = objMasterService.funGetAllPOSData(clientCode);
		if(null!=listPOSMasterModel){
			clsPOSMasterBean objPOSDtl;
			for(int cnt=0;cnt<listPOSMasterModel.size();cnt++)
			{
				Object ob[]=(Object[]) listPOSMasterModel.get(cnt);
				objPOSDtl = new clsPOSMasterBean();
				objPOSDtl.setStrPosCode(ob[0].toString());
				objPOSDtl.setStrPosName(ob[1].toString());
				
				listPOSData.add(objPOSDtl);
				hmPOSData.put(ob[0].toString(), ob[1].toString());
			}

		}
  	   return listPOSData;
	}
	
	@RequestMapping(value = "/loadGroupData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSGroupMasterBean> funLoadGroupData(HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List<clsPOSGroupMasterBean> listPOSData=new ArrayList<clsPOSGroupMasterBean>();
		
		List<clsGroupMasterModel> listModel=objMasterService.funLoadAllGroupDetails(clientCode);
		    if(null!=listModel)
			{
				for(int cnt=0;cnt<listModel.size();cnt++)
				{
					clsGroupMasterModel obModel= listModel.get(cnt);
					clsPOSGroupMasterBean objPOSDtl = new clsPOSGroupMasterBean();
					objPOSDtl.setStrGroupCode(obModel.getStrGroupCode());
					objPOSDtl.setStrGroupName(obModel.getStrGroupName());
					
					listPOSData.add(objPOSDtl);
				}
			}
			return listPOSData;
	}
		
	@RequestMapping(value = "/loadTaxData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSTaxMasterBean> funLoadTaxData(HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List<clsPOSTaxMasterBean> listTaxData=new ArrayList<clsPOSTaxMasterBean>();
		
		List<clsTaxMasterModel> listTaxModel = objMasterService.funGetAllTaxForMaster(clientCode);
		
		for(int cnt=0;cnt<listTaxModel.size();cnt++)
		{
			clsTaxMasterModel objTaxModel= (clsTaxMasterModel) listTaxModel.get(cnt);
		   
		    clsPOSTaxMasterBean objPOSDtl = new clsPOSTaxMasterBean();
			objPOSDtl.setStrTaxCode(objTaxModel.getStrTaxCode());
			objPOSDtl.setStrTaxDesc(objTaxModel.getStrTaxDesc());
			hmTaxData.put(objTaxModel.getStrTaxCode(), objTaxModel.getStrTaxDesc());
			listTaxData.add(objPOSDtl);
		}
	  return listTaxData;
	}
	
	@RequestMapping(value = "/loadAreaData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSAreaMasterBean> funLoadAreaData(HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List<clsPOSAreaMasterBean> listAreaData=new ArrayList<clsPOSAreaMasterBean>();
		
		List<clsAreaMasterModel> listModel=objMasterService.funLoadClientWiseArea(clientCode);
		    if(null!=listModel)
			{
				for(int cnt=0;cnt<listModel.size();cnt++)
				{
					clsAreaMasterModel obModel=listModel.get(cnt);
					clsPOSAreaMasterBean objAreaDtl = new clsPOSAreaMasterBean();
					objAreaDtl.setStrAreaCode(obModel.getStrAreaCode());
					objAreaDtl.setStrAreaName(obModel.getStrAreaName());
					hmAreaData.put(obModel.getStrAreaCode(), obModel.getStrAreaName());
					listAreaData.add(objAreaDtl);
				}
			}
			return listAreaData;
	}

	//Load All Settlement for table
	@RequestMapping(value = "/loadSettlmentData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSSettlementDetailsBean> funLoadSettlmentData(HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		
		List<clsPOSSettlementDetailsBean> listSettleData=new ArrayList<clsPOSSettlementDetailsBean>();
		
		List list=objMasterService.funLoadSettlementDtl(clientCode);
		
		clsSettlementMasterModel objModel = null;
		clsPOSSettlementDetailsBean objSettlementDtl =null;
		for(int cnt=0;cnt<list.size();cnt++)
		{
			objSettlementDtl = new clsPOSSettlementDetailsBean();
			objModel = (clsSettlementMasterModel) list.get(cnt);
			objSettlementDtl.setStrSettlementCode(objModel.getStrSettelmentCode());
			objSettlementDtl.setStrSettlementDesc(objModel.getStrSettelmentDesc());
			objSettlementDtl.setStrApplicableYN(true);
			
			listSettleData.add(objSettlementDtl);
		}
		
		return listSettleData;
	}
	
	
}
