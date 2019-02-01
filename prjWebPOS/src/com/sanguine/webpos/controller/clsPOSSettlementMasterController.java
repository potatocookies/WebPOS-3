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
import com.sanguine.webpos.bean.clsPOSSettlementDetailsBean;
import com.sanguine.webpos.bean.clsPOSSettlementMasterBean;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsSettlementMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSSettlementMasterController {

	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController obUtilityController;
	
	@Autowired
	private clsPOSMasterService obMasterService;
	
	@Autowired
	private clsBaseServiceImpl objBaseServiceImpl;
	
	
	@RequestMapping(value = "/frmPOSSettlement", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSSettlementMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		
		return new ModelAndView("frmPOSSettlement");
		
	}
	
	@RequestMapping(value = "/savePOSSettlementMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSSettlementMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String settlementCode = "";
		try
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			settlementCode=objBean.getStrSettelmentCode();
			
			if (settlementCode.trim().isEmpty())
			{
				List list=obUtilityController.funGetDocumentCode("POSSettlementMaster");
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
							settlementCode = "S0" + intCode;
						}
						else if (intCode < 100)
						{
							settlementCode = "S" + intCode;
						}
						
						
					}
				    else
				    {
				    	settlementCode = "S01";
				    }
			}
			
			clsSettlementMasterModel objModel = new clsSettlementMasterModel(new clsSettlementMasterModel_ID(settlementCode, clientCode));
			
			objModel.setDblConvertionRatio(objBean.getDblConversionRatio());
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setStrAccountCode(objBean.getStrAccountCode());
			objModel.setStrAdvanceReceipt(objGlobal.funIfNull(objBean.getStrAdvanceReceipt(),"N","Y"));
			objModel.setStrApplicable(objGlobal.funIfNull(objBean.getStrApplicable(),"N","Y"));
			objModel.setStrBilling( objGlobal.funIfNull(objBean.getStrBilling(),"N","Y"));
			objModel.setStrBillPrintOnSettlement(objGlobal.funIfNull(objBean.getStrBillPrintOnSettlement(),"N","Y"));
			objModel.setStrClientCode(clientCode);
			objModel.setStrDataPostFlag("N");
			objModel.setStrSettelmentCode(settlementCode);
			objModel.setStrSettelmentDesc(objBean.getStrSettelmentDesc());
			objModel.setStrSettelmentType(objBean.getStrSettelmentType());
			objModel.setStrUserCreated(webStockUserCode);
			objModel.setStrUserEdited(webStockUserCode);
			objModel.setStrCreditReceiptYN(objGlobal.funIfNull(objBean.getStrCreditReceiptYN(),"N","Y"));
			objModel.setStrComissionOn("Net Amount");
			objModel.setStrComissionType("Per");
			objModel.setDblThirdPartyComission(objBean.getDblThirdPartyComission());
			
			obMasterService.funSaveSettlementMaster(objModel);
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+settlementCode);
			
			String sql = "update  set dteDatetblmasteroperationstatusEdited='"+objGlobal.funGetCurrentDateTime("yyyy-MM-dd")+"'  where strTableName='Settlement' ";
			objBaseServiceImpl.funExecuteUpdate(sql,"sql");
									
			return new ModelAndView("redirect:/frmPOSSettlement.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	//Assign filed function to set data onto form for edit transaction.
		@RequestMapping(value = "/loadPOSSettlementMasterData", method = RequestMethod.GET)
		public @ResponseBody clsPOSSettlementMasterBean funSetSearchFields(@RequestParam("settlementCode") String settlementCode,HttpServletRequest req)
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			clsPOSSettlementMasterBean objPOSSettlementMaster = null;
			clsSettlementMasterModel objSettlementMasterModel = null;
			
			try{
				Map<String,String> hmParameters=new HashMap<String,String>();
				hmParameters.put("settlementCode",settlementCode);
				hmParameters.put("clientCode",clientCode);
				objSettlementMasterModel=obMasterService.funLoadSettlementMaster(hmParameters);
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(null!=objSettlementMasterModel)
			{
			    
				objPOSSettlementMaster = new clsPOSSettlementMasterBean();
				objPOSSettlementMaster.setStrSettelmentCode(objSettlementMasterModel.getStrSettelmentCode());
				objPOSSettlementMaster.setStrSettelmentDesc(objSettlementMasterModel.getStrSettelmentDesc());
				objPOSSettlementMaster.setStrSettelmentType(objSettlementMasterModel.getStrSettelmentType());
				objPOSSettlementMaster.setStrApplicable(objSettlementMasterModel.getStrApplicable());
				objPOSSettlementMaster.setStrBilling(objSettlementMasterModel.getStrBilling());
				objPOSSettlementMaster.setStrAdvanceReceipt(objSettlementMasterModel.getStrAdvanceReceipt());
				objPOSSettlementMaster.setStrBillPrintOnSettlement(objSettlementMasterModel.getStrBillPrintOnSettlement());
				objPOSSettlementMaster.setDblConversionRatio(objSettlementMasterModel.getDblConvertionRatio());
				objPOSSettlementMaster.setStrAccountCode(objSettlementMasterModel.getStrAccountCode());
				objPOSSettlementMaster.setStrAccountCode(objSettlementMasterModel.getStrAccountCode());
				objPOSSettlementMaster.setStrCreditReceiptYN(objSettlementMasterModel.getStrCreditReceiptYN());
				objPOSSettlementMaster.setStrComissionType(objSettlementMasterModel.getStrComissionType());
				objPOSSettlementMaster.setStrComissionOn(objSettlementMasterModel.getStrComissionOn());
				objPOSSettlementMaster.setDblThirdPartyComission(objSettlementMasterModel.getDblThirdPartyComission());
			}
			
			if(null==objPOSSettlementMaster)
			{
				objPOSSettlementMaster = new clsPOSSettlementMasterBean();
				objPOSSettlementMaster.setStrSettelmentCode("Invalid Code");
			}
			
			return objPOSSettlementMaster;
		}
		
		//Load All Settlement for table
		@RequestMapping(value = "/LoadSettlmentData", method = RequestMethod.GET)
		public @ResponseBody List<clsPOSSettlementDetailsBean> funLoadSettlmentData(HttpServletRequest req)throws Exception
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			
			List<clsPOSSettlementDetailsBean> listSettleData=new ArrayList<clsPOSSettlementDetailsBean>();
			
			List list=obMasterService.funLoadSettlementDtl(clientCode);
			
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
			 
    	 @RequestMapping(value ="/checkSettlementName" ,method =RequestMethod.GET)
		public  @ResponseBody boolean checkSettlementName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
		{
			String clientCode =req.getSession().getAttribute("gClientCode").toString();
			int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSSettlementMaster");
			if(count>0)
			 return false;
			else
				return true;
		}
}
