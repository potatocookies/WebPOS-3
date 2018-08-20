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

import org.json.simple.JSONObject;
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
import com.sanguine.webpos.bean.clsPOSMasterBean;
import com.sanguine.webpos.bean.clsPOSReorderTimeBean;
import com.sanguine.webpos.bean.clsPOSSettlementDetailsBean;
import com.sanguine.webpos.model.clsPOSMasterModel;
import com.sanguine.webpos.model.clsPOSMasterModel_ID;
import com.sanguine.webpos.model.clsPosSettlementDetailsModel;
import com.sanguine.webpos.model.clsReorderTimeModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSMasterController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@RequestMapping(value = "/frmPosMaster", method = RequestMethod.GET)
	
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
	        
		Map mapOfPrinterList = new HashMap();
		List<String> printerList=new ArrayList<String>();
		mapOfPrinterList = objUtilityController.funGetPrinterList();
		printerList =(ArrayList) mapOfPrinterList.get("printerList");
		model.put("printerList", printerList);
	     
	    return new ModelAndView("frmPOSMaster");
		 
	}
	
	@RequestMapping(value = "/savePOSMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		
		try
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String posCode = objBean.getStrPosCode();
			if (posCode.trim().isEmpty())
		    {
		    	List list=objUtilityController.funGetDocumentCode("POSMaster");
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
							posCode = "P0" + intCode;
						}
						else if (intCode < 100)
						{
							posCode = "P" + intCode;
						}
						
						
					}
				    else
				    {
				    	posCode = "P01";
				    }
		    }
		    
		    clsPOSMasterModel objModel=new clsPOSMasterModel(new clsPOSMasterModel_ID(posCode,clientCode));
		   
		    objModel.setStrPosName(objBean.getStrPosName());
		    objModel.setStrPosType(objBean.getStrPosType());
		    objModel.setStrDebitCardTransactionYN(objBean.getStrDebitCardTransactionYN());
		    objModel.setStrPropertyPOSCode(objBean.getStrPropertyPOSCode());
		    objModel.setStrOperationalYN(objGlobal.funIfNull(objBean.getStrOperationalYN(),"N","Y"));
		    objModel.setStrCounterWiseBilling(objGlobal.funIfNull(objBean.getStrCounterWiseBilling(),"N","Y"));
		    objModel.setStrDelayedSettlementForDB(objGlobal.funIfNull(objBean.getStrDelayedSettlementForDB(),"N","Y"));
		    objModel.setStrBillPrinterPort(objBean.getStrBillPrinterPort());
		    objModel.setStrAdvReceiptPrinterPort(objBean.getStrAdvReceiptPrinterPort());
		    objModel.setStrVatNo(objBean.getStrVatNo());
		    objModel.setStrPrintVatNo(objGlobal.funIfNull(objBean.getStrPrintVatNo(),"N","Y"));
		    objModel.setStrServiceTaxNo(objBean.getStrServiceTaxNo());
		    objModel.setStrPrintServiceTaxNo(objGlobal.funIfNull(objBean.getStrPrintServiceTaxNo(),"N","Y"));
		    objModel.setStrRoundOff( objBean.getStrRoundOff());
		    objModel.setStrTip(objBean.getStrTip());
		    objModel.setStrDiscount(objBean.getStrDiscount());
		    objModel.setStrWSLocationCode(objBean.getStrWSLocationCode());
		    objModel.setStrExciseLicenceCode(objBean.getStrExciseLicenceCode());
		    objModel.setStrEnableShift("N");
		    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
		 
		    objModel.setStrUserCreated(webStockUserCode);
		    objModel.setStrUserEdited(webStockUserCode);
		    
		    List<clsPOSSettlementDetailsBean> list=objBean.getListSettlementDtl();
		    Set<clsPosSettlementDetailsModel> listsettlementDtl = new HashSet<clsPosSettlementDetailsModel>();
		    if(list!=null)
		    {
		    for(int i=0;i<list.size();i++)
		    {
		    	clsPOSSettlementDetailsBean obj =list.get(i);
		    	clsPosSettlementDetailsModel objSettlementModel = new clsPosSettlementDetailsModel();
		    	
		    	if(obj.getStrApplicableYN()!=null)
		    	{
		    		objSettlementModel.setStrSettlementCode(obj.getStrSettlementCode());
			    	objSettlementModel.setStrSettlementDesc(obj.getStrSettlementDesc());
			    	if(obj.getStrApplicableYN())
			    	{
			    	objSettlementModel.setStrDataPostFlag("Y");
			    	}
			    	else
			    	{
			    		objSettlementModel.setStrDataPostFlag("N");	
			    	}
			    	listsettlementDtl.add(objSettlementModel);
		    	}
		    	
		    	
		    }
		    }
		    
		    objModel.setListsettlementDtl(listsettlementDtl);
		    
		    List<clsPOSReorderTimeBean> reorderTimeList=objBean.getListReorderTime();
		    Set<clsReorderTimeModel> listReorderTimeDtl = new HashSet<clsReorderTimeModel>();
		    if(reorderTimeList!=null)
		    {
				for (int i = 0; i < reorderTimeList.size(); i++) {
					clsPOSReorderTimeBean obj = reorderTimeList.get(i);
					clsReorderTimeModel objReorderTimeModel = new clsReorderTimeModel();

					objReorderTimeModel.setTmeFromTime(obj.getTmeFromTime());
					objReorderTimeModel.setTmeToTime(obj.getTmeToTime());
					objReorderTimeModel.setStrUserCreated(webStockUserCode);
					objReorderTimeModel.setStrUserEdited(webStockUserCode);
					objReorderTimeModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					objReorderTimeModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
					objReorderTimeModel.setStrDataPostFlag("Y");
					listReorderTimeDtl.add(objReorderTimeModel);

				}
		    }
		    
		    objModel.setListReorderTimeDtl(listReorderTimeDtl);;
		    
		    objMasterService.funSaveUpdatePosMasterData(objModel);
		    
		    req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+posCode);
									
			return new ModelAndView("redirect:/frmPosMaster.html");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	@RequestMapping(value = "/loadPOSMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSMasterBean funSetSearchFields(@RequestParam("posCode") String posCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSMasterBean objPOSMaster = new clsPOSMasterBean();
		List<clsPOSSettlementDetailsBean> listSettleData=new ArrayList<clsPOSSettlementDetailsBean>();
		List<clsPOSReorderTimeBean> listReorderTime=new ArrayList<clsPOSReorderTimeBean>();
		JSONObject jObjSearchDetails=new JSONObject();

		clsPOSMasterModel objPOSMasterModel = objMasterService.funSelectedPOSMasterData(posCode, clientCode);
		objPOSMaster.setStrPosCode(objPOSMasterModel.getStrPosCode());
		objPOSMaster.setStrPosName(objPOSMasterModel.getStrPosName());
		objPOSMaster.setStrPosType(objPOSMasterModel.getStrPosType());
		objPOSMaster.setStrDebitCardTransactionYN(objPOSMasterModel.getStrDebitCardTransactionYN());
		objPOSMaster.setStrPropertyPOSCode(objPOSMasterModel.getStrPropertyPOSCode());
		objPOSMaster.setStrCounterWiseBilling(objPOSMasterModel.getStrCounterWiseBilling());
		objPOSMaster.setStrDelayedSettlementForDB(objPOSMasterModel.getStrDelayedSettlementForDB());
		objPOSMaster.setStrBillPrinterPort(objPOSMasterModel.getStrBillPrinterPort());
		objPOSMaster.setStrAdvReceiptPrinterPort(objPOSMasterModel.getStrAdvReceiptPrinterPort());
		objPOSMaster.setStrOperationalYN(objPOSMasterModel.getStrOperationalYN());
		objPOSMaster.setStrVatNo(objPOSMasterModel.getStrVatNo());
		objPOSMaster.setStrPrintVatNo(objPOSMasterModel.getStrPrintVatNo());
		
		objPOSMaster.setStrServiceTaxNo(objPOSMasterModel.getStrServiceTaxNo());
		objPOSMaster.setStrPrintServiceTaxNo(objPOSMasterModel.getStrPrintServiceTaxNo());
		objPOSMaster.setStrRoundOff(objPOSMasterModel.getStrRoundOff());
		objPOSMaster.setStrTip(objPOSMasterModel.getStrTip());
		objPOSMaster.setStrDiscount(objPOSMasterModel.getStrDiscount());
		objPOSMaster.setStrWSLocationCode(objPOSMasterModel.getStrWSLocationCode());
		objPOSMaster.setStrExciseLicenceCode(objPOSMasterModel.getStrExciseLicenceCode());
		
		// POS Settlement Details
		Set<clsPosSettlementDetailsModel> listSettlementDtl =objPOSMasterModel.getListsettlementDtl();
		Iterator itr = listSettlementDtl.iterator();
		while (itr.hasNext()) {
			clsPosSettlementDetailsModel objSettlementDetailModel = (clsPosSettlementDetailsModel) itr.next();

			clsPOSSettlementDetailsBean objSettlementDtl = new clsPOSSettlementDetailsBean();
			objSettlementDtl.setStrSettlementCode(objSettlementDetailModel.getStrSettlementCode());
			objSettlementDtl.setStrSettlementDesc(objSettlementDetailModel.getStrSettlementDesc());
			if(objSettlementDetailModel.getStrDataPostFlag().equalsIgnoreCase("Y"))
			{
			objSettlementDtl.setStrApplicableYN(true);
			}
			else
			{	
			objSettlementDtl.setStrApplicableYN(false);	
			}
			listSettleData.add(objSettlementDtl);
		}

		objPOSMaster.setListSettlementDtl(listSettleData);

		// ReorderTime
		Set<clsReorderTimeModel> listReorderTimeDtl = objPOSMasterModel
				.getListReorderTimeDtl();
		Iterator itr1 = listReorderTimeDtl.iterator();
		while (itr1.hasNext()) {
			clsReorderTimeModel objReorderTimeModel = (clsReorderTimeModel) itr1.next();
			clsPOSReorderTimeBean objReorderTime = new clsPOSReorderTimeBean();
			objReorderTime.setTmeFromTime(objReorderTimeModel.getTmeFromTime());
			objReorderTime.setTmeToTime(objReorderTimeModel.getTmeToTime());
			listReorderTime.add(objReorderTime);
		}

		objPOSMaster.setListReorderTime(listReorderTime);

		if(null==objPOSMaster)
		{
			objPOSMaster = new clsPOSMasterBean();
			objPOSMaster.setStrPosCode("Invalid Code");
		}
		
		return objPOSMaster;
	}
	
	 @RequestMapping(value ="/checkPOSName" ,method =RequestMethod.GET)
		public  @ResponseBody boolean funCheckPOSName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
		{
			String clientCode =req.getSession().getAttribute("gClientCode").toString();

			int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSMaster");
			if(count>0)
			 return false;
			else
				return true;
			
		}
}
