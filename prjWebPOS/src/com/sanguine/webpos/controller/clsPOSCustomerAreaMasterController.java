package com.sanguine.webpos.controller;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.sanguine.webpos.bean.clsPOSCustomerAreaMasterAmountBean;
import com.sanguine.webpos.bean.clsPOSCustomerAreaMasterBean;
import com.sanguine.webpos.model.clsCustomerAreaMasterAmountModel;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel_ID;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;




@Controller
public class clsPOSCustomerAreaMasterController {
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map map=new TreeMap<>();
	
		@RequestMapping(value = "/frmPOSCustAreaMaster", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
		{
			String urlHits="1";
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			
			List<clsCustomerTypeMasterModel> list = objMasterService.funFillCustomerTypeCombo(strClientCode);
			if(null!=list)
			{
			for(int cnt=0;cnt<list.size();cnt++)
				{
				clsCustomerTypeMasterModel objModel = list.get(cnt);
					 map.put(objModel.getStrCustTypeCode(),objModel.getStrCustType());
				}
			model.put("customerTypeList",map);
			}

			
			
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSCustomerAreaMaster_1","command", new clsPOSCustomerAreaMasterBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSCustomerAreaMaster","command", new clsPOSCustomerAreaMasterBean());
			}else {
				return null;
			}
			 
		}
		
		@RequestMapping(value = "/savePOSCustomerAreaMaster", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSCustomerAreaMasterBean objBean,BindingResult result,HttpServletRequest req)
		{
			String urlHits="1";
			List<clsPOSCustomerAreaMasterAmountBean> listdata=null;
			try
			{
				urlHits=req.getParameter("saddr").toString();
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
		
				String customerAreaCode = objBean.getStrCustomerAreaCode();
				if (customerAreaCode.trim().isEmpty())
			    {
			    	
			    	List list=objUtilityController.funGetDocumentCode("POSCustAreaMaster");
			    	if (!list.get(0).toString().equals("0"))
					{
					    String strCode = "00";
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
					    	customerAreaCode = "B000000" + intCode;
					    }
					    else if (intCode < 100)
					    {
					    	customerAreaCode = "B00000" + intCode;
					    }
					    else if (intCode < 1000)
					    {
					    	customerAreaCode = "B0000" + intCode;
					    }
					    else if (intCode < 10000)
					    {
					    	customerAreaCode = "B000" + intCode;
					    }
					    else if (intCode < 100000)
					    {
					    	customerAreaCode = "B00" + intCode;
					    }
					    else if (intCode < 1000000)
					    {
					    	customerAreaCode = "B0" + intCode;
					    }
					   
					}
					else
					{
						customerAreaCode = "B0000001";
					}

			    }
			    clsCustomerAreaMasterModel objModel = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
			    
			    objModel.setStrBuildingName(objBean.getStrCustomerAreaName());
			    objModel.setStrAddress( objBean.getStrAddress());
			    objModel.setDblHomeDeliCharge(objBean.getStrHomeDeliveryCharges());
			    objModel.setStrZoneCode(objBean.getStrZone());
			    objModel.setDblDeliveryBoyPayOut(objBean.getDblDeliveryBoyPayOut());
			    objModel.setDblHelperPayOut(objBean.getStrHelperPayOut());
			   
			    
			    objModel.setStrClientCode(clientCode);
			    objModel.setStrUserCreated(webStockUserCode);
			    objModel.setStrUserEdited(webStockUserCode);
			    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setStrDataPostFlag("N");
			   
				 listdata=objBean.getListCustAreaAmount();
				 Set<clsCustomerAreaMasterAmountModel> listCustAreaDtl = new HashSet<clsCustomerAreaMasterAmountModel>();
				
				 if(listdata!=null)
				 {
				 for(int i=0; i<listdata.size(); i++)
				 {
				 clsCustomerAreaMasterAmountModel objModelAmount = new clsCustomerAreaMasterAmountModel();	
				 clsPOSCustomerAreaMasterAmountBean obj = new clsPOSCustomerAreaMasterAmountBean();
				 obj=(clsPOSCustomerAreaMasterAmountBean)listdata.get(i);
			    	
				   
		    	 	objModelAmount.setDblBillAmount(obj.getDblAmount());
				    objModelAmount.setDblBillAmount1(obj.getDblAmount1());
				    objModelAmount.setDblDeliveryCharges(obj.getDblDeliveryCharges());
				    objModelAmount.setStrCustTypeCode(obj.getStrCustomerType());						   
				    objModelAmount.setStrUserCreated(webStockUserCode);
				    objModelAmount.setStrUserEdited(webStockUserCode);
				    objModelAmount.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModelAmount.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModelAmount.setStrDataPostFlag("N");
				    objModelAmount.setStrSymbol("N");
				    objModelAmount.setDblKilometers(0);
				    
				    listCustAreaDtl.add(objModelAmount);
				  }
				 }
				 
				 objModel.setListcustomerDtl(listCustAreaDtl);
				 objMasterService.funSaveUpdateCustomerAreaMaster(objModel);
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+customerAreaCode);
				
				String sql = "update tblmasteroperationstatus set dteDateEdited='"+objGlobal.funGetCurrentDateTime("yyyy-MM-dd")+"'  where strTableName='Building' ";
				objBaseServiceImpl.funExecuteUpdate(sql,"sql");
										
				return new ModelAndView("redirect:/frmPOSCustAreaMaster.html?saddr="+urlHits);
			}
			catch(Exception ex)
			{
				urlHits="1";
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
		

		@SuppressWarnings("unused")
		@RequestMapping(value = "/loadPOSCustomerAreaMasterData", method = RequestMethod.GET)
		public @ResponseBody clsPOSCustomerAreaMasterBean funSetSearchFields(@RequestParam("POSCustomerAreaCode") String CustomerAreaCode,HttpServletRequest req) throws Exception 
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			clsPOSCustomerAreaMasterBean objPOSCustomerAreaMaster = new clsPOSCustomerAreaMasterBean();
			List<clsPOSCustomerAreaMasterAmountBean> list=new ArrayList<clsPOSCustomerAreaMasterAmountBean>();
			
			clsCustomerAreaMasterModel objCustomerAreaMasterModel = objMasterService.funSelectedCustomerAreaMasterData(CustomerAreaCode, clientCode);
			objPOSCustomerAreaMaster.setStrCustomerAreaCode(objCustomerAreaMasterModel.getStrBuildingCode());
			objPOSCustomerAreaMaster.setStrCustomerAreaName(objCustomerAreaMasterModel.getStrBuildingName());
			objPOSCustomerAreaMaster.setStrAddress(objCustomerAreaMasterModel.getStrAddress());
			objPOSCustomerAreaMaster.setStrHomeDeliveryCharges(objCustomerAreaMasterModel.getDblHomeDeliCharge());
			objPOSCustomerAreaMaster.setStrZone(objCustomerAreaMasterModel.getStrZoneCode());
			objPOSCustomerAreaMaster.setDblDeliveryBoyPayOut(objCustomerAreaMasterModel.getDblDeliveryBoyPayOut());
			objPOSCustomerAreaMaster.setStrHelperPayOut(objCustomerAreaMasterModel.getDblHelperPayOut());
			
			
			Set<clsCustomerAreaMasterAmountModel> listCustomerAreaDtl =objCustomerAreaMasterModel.getListcustomerDtl();
			Iterator itr = listCustomerAreaDtl.iterator();
	        while(itr.hasNext())
	        {
	        	clsCustomerAreaMasterAmountModel objCustomerArea=(clsCustomerAreaMasterAmountModel)itr.next();
	        	clsPOSCustomerAreaMasterAmountBean objDtl = new clsPOSCustomerAreaMasterAmountBean();
	        	objDtl.setDblAmount(objCustomerArea.getDblBillAmount());
	        	objDtl.setDblAmount1(objCustomerArea.getDblBillAmount1());
	        	objDtl.setDblDeliveryCharges(objCustomerArea.getDblDeliveryCharges());
	        	objDtl.setStrCustomerType(objCustomerArea.getStrCustTypeCode());
	        	list.add(objDtl);
	        }	
	       objPOSCustomerAreaMaster.setListCustAreaAmount(list);
			
			if(null==objPOSCustomerAreaMaster)
			{
				objPOSCustomerAreaMaster = new clsPOSCustomerAreaMasterBean();
				objPOSCustomerAreaMaster.setStrCustomerAreaCode("Invalid Code");
			}
			
			return objPOSCustomerAreaMaster;
		}

	}



