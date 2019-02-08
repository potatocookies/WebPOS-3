package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSCustomerDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSCustomerHistoryFlashBean;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSCustomerHistoryFlashController 
{
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	Map posMap=new HashMap();
	
	@RequestMapping(value = "/frmPOSCustomerHistoryFlash", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request)throws Exception
	{	
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();
		String strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		String strPOSDate=request.getSession().getAttribute("gPOSDate").toString();
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		List posList= new ArrayList();
		posList.add("ALL");
		
		List lisOfPos = objMasterService.funFullPOSCombo(strClientCode);
		for(int i =0 ;i<lisOfPos.size();i++)
		{
			Object[] obj = (Object[]) lisOfPos.get(i);
			posList.add(obj[1].toString());
			String posName=obj[1].toString();
			posMap.put( obj[1].toString(), obj[0].toString());
		}
		model.put("posList",posList);
		String posDate=strPOSDate.split("-")[2]+"-"+strPOSDate.split("-")[1]+"-"+strPOSDate.split("-")[0];
		model.put("posDate", posDate);  	
		
		Map mapAmount = new HashMap<>();
		
		mapAmount.put("<=", "<=");
		mapAmount.put(">=", ">=");
		mapAmount.put("=", "=");
		model.put("mapAmount",mapAmount);
		
		Map mapReportType = new HashMap<>();
		
		mapReportType.put("Bill Wise", "Bill Wise");
		mapReportType.put("Item Wise", "Item Wise");
		
		model.put("mapReportType",mapReportType);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCustomerHistoryFlash_1","command", new clsPOSCustomerHistoryFlashBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCustomerHistoryFlash","command", new clsPOSCustomerHistoryFlashBean());
		}else {
			return null;
		} 
	}
	
	@RequestMapping(value = "/loadFunFillAllTables", method = RequestMethod.POST)
	public @ResponseBody Map loadFunFillAllTables(HttpServletRequest req)
	{
		List listmain =new ArrayList();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posName=req.getParameter("posName");
		String reportType=req.getParameter("reportType");
		String fromDate=req.getParameter("fromDate");
		String toDate=req.getParameter("toDate");
		String selectedTab=req.getParameter("selectedTab");
		String custCode="",custName="",cmbAmount="",txtAmount="";
		String posCode="";
		
		if(posName.equalsIgnoreCase("ALL"))
		{
			posCode="All";
		}
		else 
		{
			if(posMap.containsKey(posName))
			{
				posCode=(String) posMap.get(posName);
			}
		}
		if(selectedTab.equalsIgnoreCase("Customer Wise"))
		{
			 custCode=req.getParameter("custCode");
			 custName=req.getParameter("custName");
		}
		if(selectedTab.equalsIgnoreCase("Top Spenders"))
		{
			 cmbAmount=req.getParameter("custCode");
			 txtAmount=req.getParameter("custName");
		}
		String strUserCode=req.getSession().getAttribute("gUserCode").toString();
		
		Map jObjCustomerHistoryFlash=new HashMap();
		jObjCustomerHistoryFlash.put("posCode", posCode);
		jObjCustomerHistoryFlash.put("reportType", reportType);
		jObjCustomerHistoryFlash.put("selectedTab", selectedTab);
		jObjCustomerHistoryFlash.put("fromDate", fromDate);
		jObjCustomerHistoryFlash.put("toDate", toDate);
		if(selectedTab.equalsIgnoreCase("Customer Wise"))
		{
			jObjCustomerHistoryFlash.put("custCode", custCode);
		}
		if(selectedTab.equalsIgnoreCase("Top Spenders"))
		{
			jObjCustomerHistoryFlash.put("cmbAmount", cmbAmount);
			jObjCustomerHistoryFlash.put("txtAmount", txtAmount);
		}
		jObjCustomerHistoryFlash.put("UserCode", strUserCode);
		
		
		Map jObj;
		Map jObjAllTableData=new HashMap();
		List  jArrForAllTbl=null;
		List  jArrForAllTbl1=null;
		List  jArrForAllTbl2=null;
		List  jArrForAllTbl3=null;
		jObj = funFillAllTables(jObjCustomerHistoryFlash);
	
		Map jObjTblDataDtl=new HashMap();
		String  strTabName=(String) jObj.get("tabName");
		String strCmbName=(String) jObj.get("cmbName");
	    if(strTabName.equalsIgnoreCase("Customer Wise"))
	    {
	    	 if(strCmbName.equalsIgnoreCase("Bill Wise"))
	    	 {
	    		 jArrForAllTbl=(List) jObj.get("CustomerWiseTblData");
	    		 jArrForAllTbl1=(List) jObj.get("TotalTblData");
	    		 jArrForAllTbl2=(List) jObj.get("listOfHeader");
	    		 
	    		 jObjTblDataDtl.put("CustomerWiseTblData",jArrForAllTbl);
	    		 jObjTblDataDtl.put("TotalTblData",jArrForAllTbl1);
	    		 jObjTblDataDtl.put("ListOfHeader",jArrForAllTbl2);
	    	 }
	    	 else 
	    	 {
	    		jArrForAllTbl=(List) jObj.get("CustomerWiseTblData");
	    		jArrForAllTbl1=(List) jObj.get("TotalTblData");
	    		jArrForAllTbl3=(List) jObj.get("list");	
	    		jArrForAllTbl2=(List) jObj.get("listOfHeader");
	 	        jObjTblDataDtl.put("CustomerWiseTblData",jArrForAllTbl); 
	 	        jObjTblDataDtl.put("TotalTblData",jArrForAllTbl1); 
	 	        jObjTblDataDtl.put("ListOfHeader",jArrForAllTbl2); 
	    	 }
	    	 jObjTblDataDtl.put("strCmbName", strCmbName);
	    		 
	     }  
	     if(strTabName.equalsIgnoreCase("Top Spenders"))
	     {
	    	 jArrForAllTbl=(List) jObj.get("TopSpendersTblData");
	    	 jArrForAllTbl1=(List) jObj.get("TotalTblData");
	 		
		     jObjTblDataDtl.put("TopSpendersTblData",jArrForAllTbl); 
		     jObjTblDataDtl.put("TotalTblData",jArrForAllTbl1); 
	     }
	     if(strTabName.equalsIgnoreCase("Non Spenders"))
	     {
	    	 jArrForAllTbl=(List) jObj.get("NonSpendersTblData");
	 		
		     jObjTblDataDtl.put("NonSpendersTblData",jArrForAllTbl);
	     }
	     jObjTblDataDtl.put("strTabName", strTabName);
			return jObjTblDataDtl;
		
	}
	
	@RequestMapping(value = "/loadPOSCustomerMasterDtl", method = RequestMethod.POST)
	public @ResponseBody Map loadPOSCustomerMasterDtl(HttpServletRequest req) throws Exception
	{
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		Map mapCustDtl=new HashMap();
		List listCustDtl=new ArrayList();
		List listCust=new ArrayList();
		String strCustCode=req.getParameter("code");
		StringBuilder sql=new StringBuilder();
		sql.append("select strCustomerCode,strCustomerName from tblcustomermaster where strCustomerCode='" +strCustCode+ "' AND strClientCode='"+ strClientCode +"' ");
		listCust=objBaseService.funGetList(sql, "sql");
		if(listCust.size()>0)
		{
			for(int i=0;i<listCust.size();i++)
			{
				Object[] obj=(Object[])listCust.get(i);
				clsPOSCustomerDtlsOnBill objBean=new clsPOSCustomerDtlsOnBill();
				objBean.setCustomerCode(obj[0].toString());
				objBean.setStrCustomerName(obj[1].toString());
				listCustDtl.add(objBean);
			}
		}
		mapCustDtl.put("CustomerData", listCustDtl);
		return mapCustDtl;
	}
	
	public Map funFillAllTables(Map jObjCustomerHistoryFlash)
	{
    	Map jObj=new HashMap();
    	try
    	{
	    	String posCode=jObjCustomerHistoryFlash.get("posCode").toString();
	    	String reportType=jObjCustomerHistoryFlash.get("reportType").toString();
	    	String selectedTab=jObjCustomerHistoryFlash.get("selectedTab").toString();;
	    	String fromDate=jObjCustomerHistoryFlash.get("fromDate").toString();
	    	String toDate=jObjCustomerHistoryFlash.get("toDate").toString();
	    	String strUserCode=jObjCustomerHistoryFlash.get("UserCode").toString();
	    	if (selectedTab.equalsIgnoreCase("Customer Wise"))
	        {
	        	  String custCode=jObjCustomerHistoryFlash.get("custCode").toString();
	              if (reportType.equalsIgnoreCase("Item Wise")) {
	                 
	            	  jObj = funCustomerWiseItemSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,strUserCode);
	              } 
	              else 
	              {
	                  jObj = funCustomerWiseBillSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,strUserCode);
	              }
	        }
	        if (selectedTab.equalsIgnoreCase("Top Spenders")) 
	        {
	        	String cmbAmount=jObjCustomerHistoryFlash.get("cmbAmount").toString();
	          	String txtAmount=jObjCustomerHistoryFlash.get("txtAmount").toString();
	        	jObj = funTopSpenderWiseSales(posCode,fromDate,toDate,strUserCode,cmbAmount,txtAmount);  
	        }
	        if (selectedTab.equalsIgnoreCase("Non Spenders")) 
	        {
	        	jObj = funNonSpenderWiseSales(posCode,fromDate,toDate);   
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return jObj;
	}
	
	// Item Sales
	private Map funCustomerWiseItemSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String strUserCode) 
    {
    	 StringBuilder sbSqlLiveBill = new StringBuilder();
         StringBuilder sbSqlQFileBill = new StringBuilder();
         StringBuilder sbSqlFilters = new StringBuilder();
         List jList=new ArrayList();
         List list =null;
         List listOfHeader = new ArrayList();
         Map jObjCustomerWiseTblData=new HashMap();
         try 
         {
        	List jArrlistOfBillData=new ArrayList();
        	List jArrlistOfTotalData=new ArrayList();
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);

            sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate)"
                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + strUserCode + "' "
                    + "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

            sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate)"
                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + strUserCode + "' "
                    + "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sbSqlFilters.append(" group by d.strItemName");

            boolean flgRecords = false;
            double qty = 0, amount = 0;
            double totalAmt = 0,totalAmt1 = 0;

            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
            
            Map jobjTotal=new HashMap();
            List listsbSqlLiveBill=objBaseService.funGetList(sbSqlLiveBill, "sql");
            if (listsbSqlLiveBill!=null)
			{
            	for(int i=0; i<listsbSqlLiveBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlLiveBill.get(i);
					clsPOSBillItemDtlBean objBean=new clsPOSBillItemDtlBean();
					objBean.setStrBillNo(obj[0].toString());
					objBean.setDteBillDate(obj[1].toString());
					objBean.setStrCustomerCode(obj[2].toString());
					objBean.setStrCustomerName(obj[3].toString());
					objBean.setStrItemName(obj[4].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
					objBean.setDblAmount(Double.parseDouble(obj[6].toString()));
					jArrlistOfBillData.add(objBean);
					double grandTotal=Double.parseDouble(obj[6].toString());
					totalAmt +=grandTotal ; // Grand Total
				}
			}
            
            List listsbSqlQFileBill=objBaseService.funGetList(sbSqlQFileBill, "sql");
			if (listsbSqlQFileBill!=null)
			{
				for(int i=0; i<listsbSqlQFileBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlQFileBill.get(i);
					clsPOSBillItemDtlBean objBean=new clsPOSBillItemDtlBean();
					objBean.setStrBillNo(obj[0].toString());
					objBean.setDteBillDate(obj[1].toString());
					objBean.setStrCustomerCode(obj[2].toString());
					objBean.setStrCustomerName(obj[3].toString());
					objBean.setStrItemName(obj[4].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
					objBean.setDblAmount(Double.parseDouble(obj[6].toString()));
					jArrlistOfBillData.add(objBean);
					double grandTotal=Double.parseDouble(obj[6].toString());
					totalAmt +=grandTotal ; // Grand Total   
	            }
			}
			jobjTotal.put("totAmt", totalAmt);
			jobjTotal.put("Total", "Total");
            jArrlistOfTotalData.add(jobjTotal);
            
            Map mapOfHeader=new HashMap();
            mapOfHeader.put("BillNo", "Bill No");
            mapOfHeader.put("BillDate", "Bill Date");
            mapOfHeader.put("ItemName","Item Name");
            mapOfHeader.put("Quantity","Quantity");
            mapOfHeader.put("Amount","Amount");
            listOfHeader.add(mapOfHeader);
            
            jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData);  
			jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData);  
			jObjCustomerWiseTblData.put("cmbName", "Item Wise"); 
			jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 
			jObjCustomerWiseTblData.put("listOfHeader", listOfHeader);
         } 
         catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjCustomerWiseTblData;
    }
	
	// Bill Sales
	private Map funCustomerWiseBillSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String strUserCode) 
	{
		StringBuilder sbSqlLiveBill = new StringBuilder();
		StringBuilder sbSqlQFileBill = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		List jList=new ArrayList();
		List listOfHeader=new ArrayList();
		Map jObjCustomerWiseTblData=new HashMap();
       
		try 
		{
			List jArrlistOfBillData=new ArrayList();
			List jArrlistOfTotalData=new ArrayList(); 
			sbSqlLiveBill.setLength(0);
			sbSqlQFileBill.setLength(0);
			sbSqlFilters.setLength(0);
			
			if(!custCode.equalsIgnoreCase("")){
			sbSqlLiveBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                   + " ,f.strPOSName"
                   + ", ifnull(d.strSettelmentDesc,'') as payMode"
                   + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                   + " ,ifnull(c.dblSettlementAmt,0.00)"
                   + " ,ifnull(c.strRemark,'')"
                   + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                   + " from tblbillhd  a "
                   + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                   + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                   + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                   + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                   + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                   + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + " AND a.strCustomerCode='" + custCode + "'");

			sbSqlQFileBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                   + " ,f.strPOSName"
                   + ", ifnull(d.strSettelmentDesc,'') as payMode"
                   + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                   + " ,ifnull(c.dblSettlementAmt,0.00)"
                   + " ,ifnull(c.strRemark,'')"
                   + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                   + " from tblqbillhd a "
                   + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                   + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                   + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                   + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                   + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                   + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + " AND a.strCustomerCode='" + custCode + "'");

           if (!posCode.equals("All")) {
               sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
           }
           
           sbSqlFilters.append(" order by a.strBillNo desc ");
           boolean flgRecords = false;
           double grandTotal = 0;
           double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

           sbSqlLiveBill.append(sbSqlFilters);
           sbSqlQFileBill.append(sbSqlFilters);
           
           List listsbSqlLiveBill=objBaseService.funGetList(sbSqlLiveBill, "sql");
           Map jobjTot=new HashMap();
           if (listsbSqlLiveBill!=null)
           {
        	   for(int i=0; i<listsbSqlLiveBill.size(); i++)
        	   {
					Object[] obj = (Object[]) listsbSqlLiveBill.get(i);
					clsPOSBillItemDtl objBean=new clsPOSBillItemDtl();
					objBean.setBillNo(obj[0].toString());//Bill No
					objBean.setDteBillDate(obj[1].toString());//Bill Date
					objBean.setDteDateCreated(obj[2].toString());//dblDateCreated
					objBean.setPosName(obj[3].toString());//POS Name
					objBean.setSettelmentDesc(obj[4].toString());//settelmentDesc 
					objBean.setSubTotal(Double.parseDouble(obj[5].toString()));//dblSubTotal
					objBean.setDiscountPercentage(Double.parseDouble(obj[6].toString()));//dblDiscountPer
					objBean.setDiscountAmount(Double.parseDouble(obj[7].toString()));//dblDiscountAmt
					objBean.setTaxAmount(Double.parseDouble(obj[8].toString()));//dblTaxAmt
					objBean.setSettlementAmt(Double.parseDouble(obj[9].toString()));//dblSettlementAmt
					objBean.setRemark(obj[10].toString()); //strRemark
					objBean.setTipAmount(Double.parseDouble(obj[11].toString()));//dblTipAmount
					objBean.setDiscountRemark(obj[12].toString());//strDiscountRemark
					objBean.setReason(obj[13].toString());//strReasonName
					jArrlistOfBillData.add(objBean);
					double grandTot=Double.parseDouble(obj[7].toString());
					totalDiscAmt +=grandTot ; // Grand Total     
					totalSubTotal += Double.parseDouble(obj[5].toString());
		            totalTaxAmt += Double.parseDouble(obj[8].toString());
		            totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
		            totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt
		       }
				 
			}
           	
           	List listsbSqlQFileBill=objBaseService.funGetList(sbSqlQFileBill, "sql");
			if (listsbSqlQFileBill!=null)
			{
				for(int i=0; i<listsbSqlQFileBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlQFileBill.get(i);
					clsPOSBillItemDtl objBean=new clsPOSBillItemDtl();
					objBean.setBillNo(obj[0].toString());//Bill No
					objBean.setDteBillDate(obj[1].toString());//Bill Date
					objBean.setDteDateCreated(obj[2].toString());//dblDateCreated
					objBean.setPosName(obj[3].toString());//POS Name
					objBean.setSettelmentDesc(obj[4].toString());//settelmentDesc 
					objBean.setSubTotal(Double.parseDouble(obj[5].toString()));//dblSubTotal
					objBean.setDiscountPercentage(Double.parseDouble(obj[6].toString()));//dblDiscountPer
					objBean.setDiscountAmount(Double.parseDouble(obj[7].toString()));//dblDiscountAmt
					objBean.setTaxAmount(Double.parseDouble(obj[8].toString()));//dblTaxAmt
					objBean.setSettlementAmt(Double.parseDouble(obj[9].toString()));//dblSettlementAmt
					objBean.setRemark(obj[10].toString()); //strRemark
					objBean.setTipAmount(Double.parseDouble(obj[11].toString()));//dblTipAmount
					objBean.setDiscountRemark(obj[12].toString());//strDiscountRemark
					objBean.setReason(obj[13].toString());//strReasonName
					jArrlistOfBillData.add(objBean);
					double grandTot=Double.parseDouble(obj[7].toString());
					totalDiscAmt +=grandTot ; // Grand Total     
					totalSubTotal += Double.parseDouble(obj[5].toString());
			        totalTaxAmt += Double.parseDouble(obj[8].toString());
			        totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
			        totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt
				}
			}
	        jobjTot.put("Total", "Total");
	        jobjTot.put("totalSubTotal", totalSubTotal);
	        jobjTot.put("blank", "");
            jobjTot.put("totalDiscAmt", totalDiscAmt);
            jobjTot.put("totalTaxAmt", totalTaxAmt);
            jobjTot.put("totalSettleAmt", totalSettleAmt);
            jobjTot.put("totalTipAmt", totalTipAmt);
            jArrlistOfTotalData.add(jobjTot);
            
            Map mapOfHeader=new HashMap();
            mapOfHeader.put("BillNo", "Bill No");
            mapOfHeader.put("BillDate", "Bill Date");
            mapOfHeader.put("BillTime","Bill Time");
            mapOfHeader.put("POSName","POS Name");
            mapOfHeader.put("PayMode","Pay Mode");
            mapOfHeader.put("SubTotal","Sub Total");
            mapOfHeader.put("DiscPer","Disc Per");
            mapOfHeader.put("DiscAmt","Disc Amount");
            mapOfHeader.put("TaxAmt","Tax Amount");
            mapOfHeader.put("SalesAmt","Sales Amount");
            mapOfHeader.put("Remark","Remark");
            mapOfHeader.put("Tip","Tip");
            mapOfHeader.put("DiscRemarks","Discount Remarks");
            mapOfHeader.put("Reason","Reason");
            listOfHeader.add(mapOfHeader);
            
	        jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData); 
	        jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData); 
	        jObjCustomerWiseTblData.put("cmbName", "Bill Wise"); 
	        jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 
	        jObjCustomerWiseTblData.put("listOfHeader", listOfHeader);
			}
			else
			{
				sbSqlLiveBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),TIME_FORMAT(time(a.dteDateCreated),'%h:%i:%s') as BillTime"
						+ ",ifnull(a.dblGrandTotal,0),ifnull(e.longMobileNo,0),ifnull(e.strCustomerName,'') "
						+ " from tblbillhd  a "
						+ " ,tblposmaster f  "
						+ " , tblbillsettlementdtl c  "
						+ " , tblsettelmenthd d   "
						+ " , tblcustomermaster e   "			
						+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
						+ "and  a.strPOSCode=f.strPOSCode "
						+ "and a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  "
						+ "and c.strSettlementCode=d.strSettelmentCode "
						+ " and a.strCustomerCode=e.strCustomerCode ");

					sbSqlQFileBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),TIME_FORMAT(time(a.dteDateCreated),'%h:%i:%s') as BillTime"
						+ ",ifnull(a.dblGrandTotal,0),ifnull(e.longMobileNo,0),ifnull(e.strCustomerName,'') "
						+ " from tblqbillhd  a "
						+ " ,tblposmaster f  "
						+ " , tblqbillsettlementdtl c  "
						+ " , tblsettelmenthd d   "
						+ " , tblcustomermaster e   "		
						+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
						+ "and  a.strPOSCode=f.strPOSCode "
						+ "and a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  "
						+ "and c.strSettlementCode=d.strSettelmentCode "
						+ " and a.strCustomerCode=e.strCustomerCode ");

					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sbSqlFilters.append(" order by a.strBillNo desc ");
					boolean flgRecords = false;
					double grandTotal = 0;
					double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

					sbSqlLiveBill.append(sbSqlFilters);
					sbSqlQFileBill.append(sbSqlFilters);
					
					List listOfBillData = new ArrayList();
					Map mapObjTotal = new HashMap();
					double totGrandTotal=0.0;
					List listsbSqlLiveBill=objBaseService.funGetList(sbSqlLiveBill, "sql");
					if (listsbSqlLiveBill!=null)
					{
						for(int i=0; i<listsbSqlLiveBill.size(); i++)
						{
							Object[] obj = (Object[]) listsbSqlLiveBill.get(i);
							clsPOSBillItemDtlBean objBean=new clsPOSBillItemDtlBean();
							objBean.setStrBillNo(obj[0].toString());
							objBean.setDteBillDate(obj[1].toString());
							objBean.setDteDateCreated(obj[2].toString());
							objBean.setDblGrandTotal(Double.parseDouble(obj[3].toString()));
							objBean.setLongMobileNo(obj[4].toString());
							objBean.setStrCustomerName(obj[5].toString());
							listOfBillData.add(objBean);
							totGrandTotal+=Double.parseDouble(obj[3].toString());
						}
					}
					
					List listsbSqlQFileBill=objBaseService.funGetList(sbSqlQFileBill, "sql");
					if (listsbSqlQFileBill!=null)
					{
						for(int i=0; i<listsbSqlQFileBill.size(); i++)
						{
							Object[] obj = (Object[]) listsbSqlQFileBill.get(i);
							clsPOSBillItemDtlBean objBean=new clsPOSBillItemDtlBean();
							objBean.setStrBillNo(obj[0].toString());
							objBean.setDteBillDate(obj[1].toString());
							objBean.setDteDateCreated(obj[2].toString());
							objBean.setDblGrandTotal(Double.parseDouble(obj[3].toString()));
							objBean.setLongMobileNo(obj[4].toString());
							objBean.setStrCustomerName(obj[5].toString());
							listOfBillData.add(objBean);
							totGrandTotal+=Double.parseDouble(obj[3].toString());
						}
					}
					
					Map mapOfHeader=new HashMap();
		            mapOfHeader.put("BillNumber", "Bill Number");
		            mapOfHeader.put("BillDate", "Date");
		            mapOfHeader.put("BillTime","Time");
		            mapOfHeader.put("BillAmount","Bill Amount");
		            mapOfHeader.put("ContactNo","Contact No");
		            mapOfHeader.put("Name","Name");
		            listOfHeader.add(mapOfHeader);
					
					mapObjTotal.put("TotalGrandTotal", totGrandTotal);
					String total="Total";
					mapObjTotal.put("Total",total);
		            jArrlistOfTotalData.add(mapObjTotal);
		            
			        jObjCustomerWiseTblData.put("CustomerWiseTblData", listOfBillData); 
			        jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData); 
			        jObjCustomerWiseTblData.put("cmbName", "Bill Wise"); 
			        jObjCustomerWiseTblData.put("tabName", "Customer Wise");
			        jObjCustomerWiseTblData.put("listOfHeader", listOfHeader);
			}
       } 
       catch (Exception e) {
           e.printStackTrace();
       } 
       finally {
           sbSqlLiveBill = null;
           sbSqlQFileBill = null;
           sbSqlFilters = null;
       }
		return jObjCustomerWiseTblData;
   }
	 
	
	 //Top Spender Details
	private Map funTopSpenderWiseSales(String posCode,String fromDate,String toDate,String webStockUserCode,String cmbAmount,String txtAmount) 
	{
		StringBuilder sbSqlLiveBill = new StringBuilder();
	    StringBuilder sbSqlQFileBill = new StringBuilder();
	    StringBuilder sbSqlFilters = new StringBuilder();
	    Map jObjTopSpendersTblData=new HashMap();
	    int colCount=4,rowCount=0,listSize=0;
	        
	    try 
	    {
	    	List jArrlistOfBillData=new ArrayList();
	        List jArrlistOfTotalData=new ArrayList();
	            
	        sbSqlLiveBill.setLength(0);
	        sbSqlQFileBill.setLength(0);
	        sbSqlFilters.setLength(0);

	        sbSqlLiveBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
	                    + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
	                    + "from tblbillhd a,tblcustomermaster b "
	                    + "where a.strCustomerCode=b.strCustomerCode "
	                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
	                    + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

	         sbSqlQFileBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
	                    + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
	                    + "from tblqbillhd a,tblcustomermaster b "
	                    + "where a.strCustomerCode=b.strCustomerCode "
	                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
	                    + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

	         if (!posCode.equals("All")) {
	        	 sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
	         }

	         sbSqlFilters.append(" GROUP BY a.strBillNo");
	         sbSqlFilters.append(" order by a.strBillNo desc");
	         boolean flgRecords = false;
	         double grandTotal = 0;
	         double totalSettleAmt = 0;
	         sbSqlLiveBill.append(sbSqlFilters);
	         sbSqlQFileBill.append(sbSqlFilters);
	         List listsbSqlLiveBill=objBaseService.funGetList(sbSqlLiveBill, "sql");
	         Map jobjTot=new HashMap();
	         if (listsbSqlLiveBill!=null)
	         {
	        	listSize=listsbSqlLiveBill.size();
	            rowCount = listSize;
	            for(int i=0; i<listsbSqlLiveBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlLiveBill.get(i);
					Map jobj=new HashMap();
						
					int billNo=Integer.parseInt(obj[2].toString());
					jobj.put("strBillNo",billNo);
					
					double dblGrandTotal=Double.parseDouble(obj[3].toString());
					jobj.put("dblGrandTotal",dblGrandTotal);
					jobj.put("LongMobileNo",Array.get(obj, 0));
					jobj.put("StrCustomerName",Array.get(obj, 1));
					jArrlistOfBillData.add(jobj);
	           
					totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
					colCount++;
				}
			}
	         	
	        List listsbSqlQFileBill=objBaseService.funGetList(sbSqlQFileBill, "sql");
	        if (listsbSqlQFileBill!=null)
			{
			 	listSize=listsbSqlQFileBill.size();
				rowCount+=listSize;
				for(int i=0; i<listsbSqlQFileBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlQFileBill.get(i);
					Map jobj=new HashMap();
					int billNo=Integer.parseInt(obj[2].toString());
					jobj.put("strBillNo",billNo);
							
					double dblGrandTotal=Double.parseDouble(obj[3].toString());
					jobj.put("dblGrandTotal",dblGrandTotal);
					jobj.put("LongMobileNo",Array.get(obj, 0));
					jobj.put("StrCustomerName",Array.get(obj, 1));
					jArrlistOfBillData.add(jobj);
			    	
					totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
								
			    }	
			}
			jobjTot.put("totalSettleAmt", totalSettleAmt);
			jobjTot.put("Total", "Total");
			jArrlistOfTotalData.add(jobjTot);
			jObjTopSpendersTblData.put("TopSpendersTblData", jArrlistOfBillData);
			jObjTopSpendersTblData.put("TotalTblData", jArrlistOfTotalData);
			jObjTopSpendersTblData.put("tabName", "Top Spenders"); 
			jObjTopSpendersTblData.put("Col Count", colCount);
			jObjTopSpendersTblData.put("Row Count", rowCount);
	   } 
	   catch (Exception e) 
	   {
		   e.printStackTrace();
	   } 
	   finally
	   {
		   sbSqlLiveBill = null;
	       sbSqlQFileBill = null;
	       sbSqlFilters = null;
	   }
	    return jObjTopSpendersTblData;
	}
	
	
		//Non-Spender Details
    private Map funNonSpenderWiseSales(String posCode,String fromDate,String toDate) 
    {
    	StringBuilder sbSqlLiveBill = new StringBuilder();
        StringBuilder sbSqlQFileBill = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        Map jObjNonSpendersTblData=new HashMap();
        int colCount=3,rowCount=0;
    	/*String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
		String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];*/
        try
        {
            List jArrlistOfBillData=new ArrayList();
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);
            
            sbSqlLiveBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                    + "FROM tblbillhd a,tblcustomermaster b\n"
                    + "WHERE a.strCustomerCode=b.strCustomerCode "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "AND a.dblGrandTotal=0.00");
            sbSqlQFileBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                    + "FROM tblqbillhd a,tblcustomermaster b\n"
                    + "WHERE a.strCustomerCode=b.strCustomerCode "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "AND a.dblGrandTotal=0.00");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sbSqlFilters.append(" GROUP BY b.strCustomerCode");
            sbSqlFilters.append(" order by DATE(a.dteBillDate) desc");
            boolean flgRecords = false;
            double grandTotal = 0;

            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
            
            List listsbSqlLiveBill=objBaseService.funGetList(sbSqlLiveBill, "sql");
            if (listsbSqlLiveBill!=null)
			{
            	rowCount=listsbSqlLiveBill.size();
				for(int i=0; i<listsbSqlLiveBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlLiveBill.get(i);
					Map jobj=new HashMap();
					//jobj.put("longMobileNo",Array.get(obj, 0));
					jobj.put("longMobileNo",obj[0].toString());
					jobj.put("strCustomerName",obj[1].toString());
					jobj.put("strBillNo",obj[2].toString());
					jobj.put("dblGrandTotal",obj[3].toString());
					jobj.put("dteBillDate",obj[4].toString());
					jArrlistOfBillData.add(jobj);
				}
			}
            
            List listsbSqlQFileBill=objBaseService.funGetList(sbSqlQFileBill, "sql");
            if (listsbSqlQFileBill!=null)
			{
            	rowCount=listsbSqlQFileBill.size();
            	if(rowCount==0)
            	{
            		rowCount=listsbSqlLiveBill.size();
            	}
            	
            	for(int i=0; i<listsbSqlQFileBill.size(); i++)
				{
					Object[] obj = (Object[]) listsbSqlQFileBill.get(i);
					Map jobj=new HashMap();
					jobj.put("longMobileNo",obj[0].toString());
					jobj.put("strCustomerName",obj[1].toString());
					jobj.put("strBillNo",obj[2].toString());
					jobj.put("dblGrandTotal",obj[3].toString());
					jobj.put("dteBillDate",obj[4].toString());
					
					jArrlistOfBillData.add(jobj);
					
   				}
			}
            jObjNonSpendersTblData.put("NonSpendersTblData", jArrlistOfBillData);
            jObjNonSpendersTblData.put("tabName", "Non Spenders"); 
            jObjNonSpendersTblData.put("Col Count", colCount);
            jObjNonSpendersTblData.put("Row Count", rowCount);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjNonSpendersTblData;
    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSCustomerHistoryFlash", method = RequestMethod.POST)	
	private ModelAndView funReport(@ModelAttribute("command") clsPOSCustomerHistoryFlashBean objBean,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posName=objBean.getStrPOSName();
		String fromDate=objBean.getDteFromDate();
		String toDate=objBean.getDteToDate();
		String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
		String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
		String reportType="";
		String custCode=objBean.getStrCustCode();
		String custName=objBean.getStrCustName();
		String selectedTab=objBean.getStrTabVal();
		String cmbAmount="",txtAmount="";
		String strUserCode=req.getSession().getAttribute("gUserCode").toString();
		if(selectedTab.equalsIgnoreCase("tab2"))
		{
			selectedTab="Top Spenders";
			reportType=objBean.getStrAmount();
			cmbAmount=objBean.getStrAmount();
			txtAmount=objBean.getStrAmt();
		}
		else
		{
			selectedTab="Non Spenders";
			reportType="Non Spenders";
			cmbAmount="";
			txtAmount="";
		}
			
		String FileName="";
		Map resMap = new LinkedHashMap();
		
		resMap=funGetData(fromDate1,toDate1,posName,selectedTab,strUserCode,cmbAmount,txtAmount,reportType);
		
		List exportList=new ArrayList();	
		
		String dteFromDate=objBean.getDteFromDate();
		String dteToDate=objBean.getDteToDate();
		String reportName="";
		if(selectedTab.equalsIgnoreCase("Top Spenders"))
		{	
			FileName="TopSpenderWiseSales_"+dteFromDate+"_To_"+dteToDate;
			reportName = "Top Spender Wise Sales";
		}
		else
		{
			FileName="NonSpenderWiseSales_"+dteFromDate+"_To_"+dteToDate;	
			reportName = "Non Spender Wise Sales";
		}
		exportList.add(FileName);
		int rowCount=Integer.parseInt(resMap.get("RowCount").toString());
		int colCount=Integer.parseInt(resMap.get("ColCount").toString());
		List<String> list=new ArrayList<String>();
		list=(List)resMap.get("Header");
		List rowlist=new ArrayList();
				
		List<String> listTitelName =new ArrayList<String>();
		  
		listTitelName.add("");
		listTitelName.add(reportName);
		listTitelName.add("");
		exportList.add(listTitelName);	
		
		List<String> listTitel2 =new ArrayList<String>();
		listTitel2.add("");
		listTitel2.add("");
		listTitel2.add("");
		exportList.add(listTitel2);	
				
		String[] headerList = new String[list.size()];
		headerList =  list.toArray(headerList);
		exportList.add(headerList);
		
		for(int i=0;i<resMap.size();i++)
		{
			List DataList=new ArrayList();
			
			if(i<=(rowCount))
			{	
				List ob=(List)resMap.get(i);
				rowlist.add(ob);
			}
		}
		for(int i=0;i<2;i++)
		{
			List DataList=new ArrayList();
			for(int j=0; j<colCount;j++)
			{
				DataList.add(" ");
			}
			rowlist.add(DataList);
		}
		
		if(selectedTab.equalsIgnoreCase("Top Spenders"))
		{	
			List totalList=(List)resMap.get("Total");
			rowlist.add(totalList);
		}
		exportList.add(rowlist);
		return new ModelAndView("styleExcelTitleCellBorderView", "sheetlist", exportList);	
	}
	
	private LinkedHashMap funGetData(String fromDate,String toDate,String posName,String selectedTab,String strUserCode,String cmbAmount,String txtAmount,String reportType)
	{
		String posCode="";
		String longMobileNo="",strCustomerName="",strBillNo="",dblGrandTotal="";
		List jArrForTopSpendersTbl=null;
		List jArrForTotalTbl=null;
		List jArrForNonSpendersTbl=null;
		LinkedHashMap resMap = new LinkedHashMap();
		List<String> colHeader = new ArrayList<String>();
		String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
		String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
		if(posName.equalsIgnoreCase("ALL"))
		{
			posCode="All";
		}
		if(posMap.containsKey(posName))
		{
			posCode=(String) posMap.get(posName);
		}

		Map jObjFillter = new HashMap();
		jObjFillter.put("fromDate", fromDate);
		jObjFillter.put("toDate", toDate);
		jObjFillter.put("posCode", posCode);
		jObjFillter.put("selectedTab",selectedTab);
		jObjFillter.put("UserCode", strUserCode);
		jObjFillter.put("cmbAmount", cmbAmount);
		jObjFillter.put("txtAmount", txtAmount);
		jObjFillter.put("reportType",reportType);
		Map jObj=new HashMap();
		
		jObj = funFillAllTables(jObjFillter);
		double totalSettleAmt = 0.0;
		String tabName=(String) jObj.get("tabName");
		colHeader.add("POS Name");
		colHeader.add(posName);
		colHeader.add("From Date :");
		colHeader.add(fromDate);
		colHeader.add("To Date :");
		colHeader.add(toDate);
		
		List listTotal=new ArrayList();
		if(tabName.equalsIgnoreCase("Top Spenders"))
		{
			
			
			jArrForTotalTbl=(List) jObj.get("TotalTblData");
			for(int i =0 ;i<jArrForTotalTbl.size();i++)
			{
				Map josnObjRet1 = (Map) jArrForTotalTbl.get(i);
			    totalSettleAmt=(double) josnObjRet1.get("totalSettleAmt");
			}
			listTotal=new ArrayList();
			listTotal.add("Total");
			listTotal.add(" ");
			listTotal.add(" ");
			listTotal.add(totalSettleAmt);
		}
				
		int colCount=Integer.parseInt(jObj.get("Col Count").toString());
		int rowCount=Integer.parseInt(jObj.get("Row Count").toString());
		
		
		for( int i=0; i<rowCount; i++)
		{
			if(tabName.equalsIgnoreCase("Top Spenders"))
			{	  
				List allTblData= new ArrayList();
			    //int k=1;

			    jArrForTopSpendersTbl=(List) jObj.get("TopSpendersTblData");
			    allTblData.add("Mobile Number");
			    allTblData.add("Customer Name");
			    allTblData.add("No. Of. Bills");
			    allTblData.add("Sales Amount");
			    resMap.put(i,allTblData);
			    					    		
			    for(int j =0 ;j<jArrForTopSpendersTbl.size();j++)
				{
			    	
			    	List allSpendersTblData= new ArrayList();
			    	allSpendersTblData= new ArrayList();
					Map josnObjRet = (Map) jArrForTopSpendersTbl.get(j);
					allSpendersTblData.add(josnObjRet.get("LongMobileNo"));
					allSpendersTblData.add(josnObjRet.get("StrCustomerName"));
					allSpendersTblData.add(josnObjRet.get("strBillNo"));
					allSpendersTblData.add(josnObjRet.get("dblGrandTotal")); 
					resMap.put((j+1),allSpendersTblData);
				}
			}
			else
			{
				
				List topHeaderData= new ArrayList();
				topHeaderData.add("Mobile No");
				topHeaderData.add("Customer Name");
				topHeaderData.add("Last Transaction Date");
				resMap.put(i,topHeaderData);
				/*List allTblData= new ArrayList();
				int k=1;
			    allTblData.add("Mobile Number");
			    allTblData.add("Customer Name");
			    allTblData.add("Last Transaction Date");
			    resMap.put(""+k,allTblData);*/
			   
			    jArrForNonSpendersTbl=(List) jObj.get("NonSpendersTblData");
			    for(int j =0 ;j<jArrForNonSpendersTbl.size();j++)
				{
			    	List allSpendersTblData= new ArrayList();
					Map josnObjRet = (Map) jArrForNonSpendersTbl.get(j);
					allSpendersTblData.add(josnObjRet.get("longMobileNo"));
					allSpendersTblData.add(josnObjRet.get("strCustomerName"));
					allSpendersTblData.add(josnObjRet.get("dteBillDate"));
					resMap.put((j+1),allSpendersTblData);
				}  
			}
		}
		resMap.put("Header", colHeader);
		resMap.put("ColCount", colCount);
		resMap.put("RowCount", rowCount);
		resMap.put("Total", listTotal);
		
		return resMap;
	  }
}
