package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSBillDiscountDtl;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillHd;
import com.sanguine.webpos.bean.clsPOSBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSBillTaxDtl;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSVoidBillHd;
import com.sanguine.webpos.bean.clsPOSVoidBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSVoidKotBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSChangeSettlementController {

	
	@Autowired 
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	clsPOSVoidKotController objVoidController;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	clsPOSUtilityController objUtility;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map hmSettle=new  HashMap();
	Map mapSettle=new TreeMap();

	@RequestMapping(value = "/frmPOSChangeSettlement", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		List settlementList=funLoadSettelementModeList();
		//model.put("settlementList",settlementList);
		model.put("settlementList",mapSettle);
		
     	if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeSettlement_1","command", new clsPOSBillDtl());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeSettlement","command", new clsPOSBillDtl());
		}else {
			return null;
		}
	}
	
	public List funLoadSettelementModeList(){
		
		List listSettleMode= new ArrayList();	
		
		try 
		{
			StringBuilder sqlBuilder=new StringBuilder();
 	       	sqlBuilder.append( "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio,strBillPrintOnSettlement "
            + " from tblsettelmenthd where strApplicable='Yes' and strBilling='Yes' ");  
    	    List listSql=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
    	    if(listSql.size()>0)
    	    {
      	       for(int j=0;j<listSql.size();j++)
      	       {
      		     Object obj = (Object ) listSql.get(j);
     	         hmSettle.put(Array.get(obj, 0).toString(),new clsPOSSettelementOptions(Array.get(obj, 0).toString(),Array.get(obj, 2).toString()
                         ,Double.valueOf(Array.get(obj, 3).toString()),Array.get(obj, 1).toString(),Array.get(obj, 4).toString()));
     	         listSettleMode.add(Array.get(obj, 1).toString());
     	         mapSettle.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString()+"#"+Array.get(obj, 2).toString());
               }
    	     }
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return listSettleMode;
	}
	
}