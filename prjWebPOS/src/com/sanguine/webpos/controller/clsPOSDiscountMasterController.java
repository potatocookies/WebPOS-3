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

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsPosDiscountMasterBean;
import com.sanguine.webpos.model.clsDiscountDetailsModel;
import com.sanguine.webpos.model.clsDiscountMasterModel;
import com.sanguine.webpos.model.clsDiscountMasterModel_ID;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsMenuItemMasterModel;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSDiscountMasterController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;
	
	Map map=new HashMap();
	@RequestMapping(value = "/frmDiscountMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPosDiscountMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(Exception e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		
		List list=objMasterService.funFillPOSCombo(clientCode);
		
		map.put("All", "All");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object[] obj=(Object[])list.get(cnt);
			
			map.put(obj[0].toString(), obj[1].toString());
		}
		model.put("posList", map);
		
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmDiscountMaster_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmDiscountMaster");
		}else {
			return null;
		}
		 
	}
	
	@RequestMapping(value = "/savePOSDiscountMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPosDiscountMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		try
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String[] dteFromDate = objBean.getDteFromDate().split(" ");
			String[] arr = dteFromDate[0].split("-");
			String fromDate=arr[2]+"-"+arr[1]+"-"+arr[0];
			
			String[] arrToDt = dteFromDate[0].split("-");
			String toDate=arrToDt[2]+"-"+arrToDt[1]+"-"+arrToDt[0];	
			
			String discountCode = objBean.getStrDiscountCode();
			
			if (discountCode.trim().isEmpty())
			{
			List list=objUtilityController.funGetDocumentCode("POSDiscountMaster");
			
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
	                    discountCode = "D00000" + intCode;
	                }
	                else if (intCode < 100)
	                {
	                    discountCode = "D0000" + intCode;
	                }
	                else if (intCode < 1000)
	                {
	                    discountCode = "D000" + intCode;
	                }
	                else if (intCode < 10000)
	                {
	                    discountCode = "D00" + intCode;
	                }
	                else if (intCode < 100000)
	                {
	                    discountCode = "D0" + intCode;
	                }
	                else if (intCode < 1000000)
	                {
	                    discountCode = "D" + intCode;
	                }

	            }
	            else
	            {
	               discountCode = "D000001";
	            }
			}
			clsDiscountMasterModel objModel = new clsDiscountMasterModel(new clsDiscountMasterModel_ID(discountCode, clientCode));
			objModel.setStrDiscCode(discountCode);
			objModel.setStrDiscName(objBean.getStrDiscountName());
			objModel.setStrPOSCode(objBean.getStrPosCode());
			objModel.setStrClientCode(clientCode);
			objModel.setStrDiscOn(objBean.getStrDiscountOn());
			objModel.setDteFromDate(fromDate);
			objModel.setDteToDate(toDate);
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setStrDataPostFlag("N");
			objModel.setStrUserCreated(webStockUserCode);
			objModel.setStrUserEdited(webStockUserCode);

			List<clsPOSDiscountDtlsOnBill> listDtl=objBean.getListDiscountDtl();
			List<clsDiscountDetailsModel> listDiscDtl = new ArrayList<clsDiscountDetailsModel>();
		    if(null!=listDtl)
		    {
		    for(int i=0; i<listDtl.size(); i++)
		    {
		    	clsDiscountDetailsModel objDtlModel = new clsDiscountDetailsModel();
		    	clsPOSDiscountDtlsOnBill obj= new clsPOSDiscountDtlsOnBill();
		    	obj=(clsPOSDiscountDtlsOnBill)listDtl.get(i);
		    	
		    		if(null!=obj.getDiscountReasonCode())
		    		{
		    		objDtlModel.setDblDiscountValue(obj.getDiscountOnValue());
		    		objDtlModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    		objDtlModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    		objDtlModel.setStrDataPostFlag("N");
		    		objDtlModel.setStrDiscOnCode(obj.getDiscountReasonCode());
		    		objDtlModel.setStrDiscOnName(obj.getStrDiscoutnName());
		    		objDtlModel.setStrDiscountType(obj.getDiscountOnType());
		    		objDtlModel.setStrUserCreated(webStockUserCode);
		    		objDtlModel.setStrUserEdited(webStockUserCode);
			    	listDiscDtl.add(objDtlModel);
		    		}
		    }
		    }
		    objModel.setListDiscountDtl(listDiscDtl);
		    objMasterService.funSaveUpdateDiscountMaster(objModel);
			
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+discountCode);
			
			String sql = "update tblmasteroperationstatus set dteDateEdited='"+objGlobal.funGetCurrentDateTime("yyyy-MM-dd")+"'  where strTableName='DiscountMaster' ";
			objBaseServiceImpl.funExecuteUpdate(sql,"sql");
									
			return new ModelAndView("redirect:/frmDiscountMaster.html");
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	//Assign filed function to set data onto form for edit transaction.
			@RequestMapping(value = "/loadPOSDiscountMasterData", method = RequestMethod.GET)
			public @ResponseBody clsPosDiscountMasterBean funSetSearchFields(@RequestParam("discCode") String discCode,HttpServletRequest req)throws Exception
			{
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				clsPosDiscountMasterBean objPOSDiscountMasterBean = new clsPosDiscountMasterBean();
				
				clsDiscountMasterModel objModel= (clsDiscountMasterModel) objMasterService.funSelectedDiscountMasterData(discCode,clientCode);
				objPOSDiscountMasterBean.setStrDiscountCode(objModel.getStrDiscCode());
				objPOSDiscountMasterBean.setStrDiscountName(objModel.getStrDiscName());
				objPOSDiscountMasterBean.setStrPosCode(objModel.getStrPOSCode());
				objPOSDiscountMasterBean.setStrDiscountOn(objModel.getStrDiscOn());

				
				List<clsDiscountDetailsModel> listDiscountDtl =objModel.getListDiscountDtl();
				List<clsPOSDiscountDtlsOnBill> list=new ArrayList<clsPOSDiscountDtlsOnBill>();
				for(int i=0; i<listDiscountDtl.size(); i++)
					
				{
					clsDiscountDetailsModel objDiscDtlModel=(clsDiscountDetailsModel)listDiscountDtl.get(i);
					clsPOSDiscountDtlsOnBill obj = new clsPOSDiscountDtlsOnBill();
					obj.setDiscountReasonCode(objDiscDtlModel.getStrDiscOnCode());
					obj.setStrDiscoutnName(objDiscDtlModel.getStrDiscOnName());
					obj.setDiscountOnType(objDiscDtlModel.getStrDiscountType());
					obj.setDiscountOnValue(objDiscDtlModel.getDblDiscountValue());
					list.add(obj);
				}
				objPOSDiscountMasterBean.setListDiscountDtl(list);
				if(null==objPOSDiscountMasterBean)
				{
					objPOSDiscountMasterBean = new clsPosDiscountMasterBean();
					objPOSDiscountMasterBean.setStrDiscountCode("Invalid Code");
				}
				
				return objPOSDiscountMasterBean;
			}
			
			@RequestMapping(value = "/loadPOSDiscountDiscountOnChangeData", method = RequestMethod.GET)
			public @ResponseBody clsPosDiscountMasterBean funPOSDiscountDiscountOnChangeData(@RequestParam("code") String code,@RequestParam("discOn") String discOn,HttpServletRequest req)throws Exception
			{
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				clsPosDiscountMasterBean objPOSDiscountMasterBean = new clsPosDiscountMasterBean();
				
				if(discOn.equalsIgnoreCase("Item"))
				{
					clsMenuItemMasterModel objModel= (clsMenuItemMasterModel) objMasterService.funSelectedDiscountDiscountOnItemData(code,clientCode);
					objPOSDiscountMasterBean.setStrDiscountCode(objModel.getStrItemCode());
					objPOSDiscountMasterBean.setStrDiscountName(objModel.getStrItemName());
				}
				else if(discOn.equalsIgnoreCase("Group"))
				{
					clsGroupMasterModel objModel= (clsGroupMasterModel) objMasterService.funSelectedDiscountDiscountOnGroupData(code,clientCode);
					objPOSDiscountMasterBean.setStrDiscountCode(objModel.getStrGroupCode());
					objPOSDiscountMasterBean.setStrDiscountName(objModel.getStrGroupName());
				}
				else if(discOn.equalsIgnoreCase("SubGroup"))
				{
					clsSubGroupMasterHdModel objModel= (clsSubGroupMasterHdModel) objMasterService.funSelectedDiscountDiscountOnSubGroupData(code,clientCode);
					objPOSDiscountMasterBean.setStrDiscountCode(objModel.getStrSubGroupCode());
					objPOSDiscountMasterBean.setStrDiscountName(objModel.getStrSubGroupName());
				}
				
				if(null==objPOSDiscountMasterBean)
				{
					objPOSDiscountMasterBean = new clsPosDiscountMasterBean();
					objPOSDiscountMasterBean.setStrDiscountCode("Invalid Code");
				}
				
				return objPOSDiscountMasterBean;
			}
				

}
