package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
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

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAddKOTToBillBean;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;
import com.sanguine.webpos.bean.clsPOSUserAccessBean;
import com.sanguine.webpos.model.clsTableMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSTransactionService;



@Controller
public class clsPOSAddKOTToBillController {
	
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController; 

	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
	 private clsPOSMasterService objMasterService;
	 
	 @Autowired
	 private clsPOSTransactionService objTransactionService;
	 
	 @Autowired
	 private intfBaseService objBaseService;
	
	 Map map=new HashMap();
	 List<clsPOSTableMasterBean> listTable=new ArrayList<clsPOSTableMasterBean>();
	@RequestMapping(value = "/frmPOSAddKOTToBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		String posCode=request.getSession().getAttribute("loginPOS").toString();
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		List tableList = new ArrayList();
		tableList.add("ALL");
		funGetTableList(strClientCode,posCode);
		for(int cnt=0;cnt<listTable.size();cnt++)
		{
			clsPOSTableMasterBean obj=listTable.get(cnt);
			tableList.add(obj.getStrTableName());
			map.put(obj.getStrTableName(), obj.getStrTableNo());
		}
		model.put("tableList",tableList);
		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmPOSAddKOTToBill_1","command", new clsPOSAddKOTToBillBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSAddKOTToBill","command", new clsPOSAddKOTToBillBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	@RequestMapping(value = "/saveAddKOTToBill", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSAddKOTToBillBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			
		    JSONObject jObj=new JSONObject();
		    clsPOSUserAccessBean obj=null;
		    List<clsPOSKOTItemDtl> listKot=objBean.getListKOTDtl();
		    List<clsPOSBillItemDtlBean> listBill=objBean.getListBillDtl();
		    String billNo="";
	    	JSONArray jArrKOTList = new JSONArray();
		    
		    if(listKot.size()>0 && listBill.size()>0)
		    {
		    	for(int i=0;i<listKot.size();i++)
		    	{
		    		clsPOSKOTItemDtl objKOTDtl =listKot.get(i);
		    		String[] arrKOT = objKOTDtl.getStrKOTNo().split(",");
	                for (int cnt = 0; cnt < arrKOT.length; cnt++)
	                {
	                	JSONObject jObjData = new JSONObject();
	                	jObjData.put("KOTNo",arrKOT[cnt]);
	                	jArrKOTList.add(jObjData);
	                }
		    	}
		    	
		    	for(int i=0;i<listBill.size();i++)
		    	{
		    		clsPOSBillItemDtlBean objBillDtl =listBill.get(i);
		    		billNo=objBillDtl.getStrBillNo();
		    	}
		    	
		    }
		    StringBuilder sql = new StringBuilder();
		    sql.append("select date(a.dteBillDate),b.strAreaCode,a.strTableNo "
                    + "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "where a.strBillNo='" + billNo + "'");
		   List list = objBaseService.funGetList(sql, "sql");
		   String areaCode="",tableNo="",billDate="";
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] object = (Object[]) list.get(i);
						billDate=object[0].toString();
						areaCode=object[1].toString();
						tableNo=object[2].toString();
					}	
			     }
		    
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+billNo);
			return new ModelAndView("redirect:/frmPOSAddKOTToBill.html?saddr="+urlHits);
			
			
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	
	
	@RequestMapping(value = "/LoadADDKOTTableData", method = RequestMethod.GET)
	public @ResponseBody clsPOSAddKOTToBillBean funLoadTableData(@RequestParam("TableName") String tableName,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		clsPOSAddKOTToBillBean obj=new clsPOSAddKOTToBillBean();
		obj=funGetKOTList(tableName,posCode);
		return obj;
	}
	
	@RequestMapping(value = "/LoadUnsettleBill", method = RequestMethod.GET)
	public @ResponseBody clsPOSAddKOTToBillBean funGetBilList(HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		clsPOSAddKOTToBillBean obj=new clsPOSAddKOTToBillBean();
		obj=funGetBilList(posCode);
		return obj;
	}

	
	
	 private clsPOSAddKOTToBillBean funGetTableList(String strClientCode,String posCode)throws Exception
	 {
		    clsPOSAddKOTToBillBean obj=new clsPOSAddKOTToBillBean();
			JSONArray jArrTableList=null;
			List<clsPOSTableMasterBean> listTableData=new ArrayList<clsPOSTableMasterBean>();
			List list = objMasterService.funGetTableList(strClientCode);
		        if(null!=list)
				{
					for(int cnt=0;cnt<list.size();cnt++)
					{
						clsTableMasterModel objModel = (clsTableMasterModel)list.get(cnt);
						clsPOSTableMasterBean objTableDtl = new clsPOSTableMasterBean();
						objTableDtl.setStrTableNo(objModel.getStrTableNo());
						objTableDtl.setStrTableName(objModel.getStrTableName());	
						
						listTableData.add(objTableDtl);
						listTable.add(objTableDtl);
						
					}
					obj.setListTableDtl(listTableData);
				}
				return obj;
	 }
	 
	 
	 
	 private clsPOSAddKOTToBillBean funGetKOTList(String tableName,String posCode)
	 {
		    clsPOSAddKOTToBillBean obj=new clsPOSAddKOTToBillBean();
			JSONArray jArrKOTList=null;
			List<clsPOSKOTItemDtl> listKOTData=new ArrayList<clsPOSKOTItemDtl>();
			List list = objTransactionService.funGetKOTDtlForAddKOTTOBill(posCode, tableName);
		        if(null!=list)
				{
					for(int cnt=0;cnt<list.size();cnt++)
					{
						clsPOSBillDtl objBean = (clsPOSBillDtl) list.get(cnt);
						clsPOSKOTItemDtl objKOTDtl = new clsPOSKOTItemDtl();
						objKOTDtl.setStrTableNo(objBean.getStrTableNo());
						objKOTDtl.setStrKOTNo(objBean.getStrKOTNo());	
						listKOTData.add(objKOTDtl);
						
					}
					obj.setListKOTDtl(listKOTData);
				}
				return obj;
	 }
	 
	 
	 private clsPOSAddKOTToBillBean funGetBilList(String posCode)
	 {
		    clsPOSAddKOTToBillBean obj=new clsPOSAddKOTToBillBean();
			JSONArray jArrBillList=null;
			List<clsPOSBillItemDtlBean> listBillData=new ArrayList<clsPOSBillItemDtlBean>();
			List list = objTransactionService.funGetUnsettleBillList(posCode);
		        
		        if(null!=list)
				{
					for(int cnt=0;cnt<list.size();cnt++)
					{
						Object[] object = (Object[])list.get(cnt);
						clsPOSBillItemDtlBean objBillDtl = new clsPOSBillItemDtlBean();
						objBillDtl.setStrBillNo(object[0].toString());	
						listBillData.add(objBillDtl);
						
					}
					obj.setListBillDtl(listBillData);
				}
				return obj;
	 }
	
	
}