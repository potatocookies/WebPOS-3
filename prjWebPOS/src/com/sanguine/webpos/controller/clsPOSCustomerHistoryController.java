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

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSCustomerHistoryFlashBean;
import com.sanguine.webpos.sevice.clsPOSTransactionService;

@Controller
public class clsPOSCustomerHistoryController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	@Autowired
	private clsPOSTransactionService objTransactionService;
	
	private String strCustCode;
	@RequestMapping(value = "/frmCustomerHistory", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSCustomerHistoryFlashBean objBean,BindingResult result,Map<String,Object> model, @RequestParam(value="strCustCode") String custCode,HttpServletRequest request)
		{
		strCustCode=custCode;
		 return new ModelAndView("frmCustomerHistory");
		}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadCustomerHistory", method = RequestMethod.GET)
	public @ResponseBody List funGetTableReservationDtl(HttpServletRequest req)
	{
		String fromDate=req.getParameter("fromDate");
		String toDate=req.getParameter("toDate");
		List jArryList=null;
			
 		/*String posURL = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSTransactions/funGetCustomerHistory"
					+ "?strCustCode="+ strCustCode+"&fromDate="+fromDate+"&toDate="+toDate;
		JSONObject jObj = objGlobal.funGETMethodUrlJosnObjectData(posURL);
		*/
		Map mapData = objTransactionService.funGetCustomerHistory(strCustCode,fromDate,toDate);
        if(null!=mapData)
		{
        	jArryList =(List)mapData.get("CustomerHistory");
		} 
	     return jArryList;
	
    }
}
