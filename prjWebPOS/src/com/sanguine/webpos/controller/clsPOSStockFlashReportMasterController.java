package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.sanguine.webpos.bean.clsPOSPSPDtl;
import com.sanguine.webpos.bean.clsPOSPhysicalStockPostingBean;
import com.sanguine.webpos.bean.clsPOSUserAccessBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSStockFlashReportMasterController 
{
	
	  @Autowired
		private clsGlobalFunctions objGlobalFunctions;
		
	  @Autowired
	  private ServletContext servletContext;
		 
	  @Autowired
	  clsPOSMasterService objMasterService;
	  
	  @Autowired
	  private intfBaseService objBaseService;
		 
	  private Vector  vItemCode = new java.util.Vector();
		
	  Map map=new HashMap();
	  Map<String,String> hmPOSData;
		
		@RequestMapping(value = "/frmPOSStockFlashReport", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
		{
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			List poslist = new ArrayList();
			poslist.add("ALL");
			
			hmPOSData=new HashMap<String, String>();
			List list=new ArrayList();
			try
			{
				list = objMasterService.funFullPOSCombo(strClientCode);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			hmPOSData.put("All", "All");
			
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
				poslist.add(Array.get(obj, 1).toString());
				hmPOSData.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
			}
			model.put("posList",poslist);
			
			List typeList = new ArrayList();
			typeList.add("Both");
			typeList.add("Raw Material");
			typeList.add("Menu Item");
			model.put("typeList",typeList);
			
			List reportTypeList = new ArrayList();
			reportTypeList.add("Stock");
			reportTypeList.add("Raw ReOrder");
			model.put("reportTypeList",reportTypeList);
			
			Map groupMap = new HashMap<String,String>();
			groupMap.put("ALL","ALL");
		
			try
			{
				List <clsGroupMasterModel> GroupList = objMasterService.funLoadGrouptData(strClientCode);
			    for(int i =0; i<GroupList.size();i++)
			    {
			    	clsGroupMasterModel obj = (clsGroupMasterModel) GroupList.get(i);
			    	groupMap.put("strGroupName",obj.getStrGroupName());
			    }
			}
			catch(Exception e){e.printStackTrace();}
			model.put("groupMap",groupMap);
		
			List viewByList = new ArrayList();
			viewByList.add("Positive");
			viewByList.add("Negative");
			viewByList.add("Both");
			model.put("viewByList",viewByList);
			
			List showZeroBalList = new ArrayList();
			showZeroBalList.add("Yes");
			showZeroBalList.add("No");
			model.put("showZeroBalList",showZeroBalList);
			
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSStockFlashReport_1","command", new clsPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSStockFlashReport","command", new clsPOSReportBean());
			}else {
				return null;
			}
		}

		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadPOSStockFlash"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadStockFlashReport(HttpServletRequest req)
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	        String PosCode=req.getSession().getAttribute("loginPOS").toString();
		    String strFromdate=req.getParameter("fromDate");
			String strToDate=req.getParameter("toDate");
			String posName=req.getParameter("posName");
			String type=req.getParameter("type");
			String reportType=req.getParameter("reportType");
			String groupwise=req.getParameter("groupwise");
			String showStockWith=req.getParameter("showStockWith");
			String showZeroBalStockYN=req.getParameter("showZeroBalStockYN");
			String time=req.getParameter("time");
			if(time.equals("first"))
	        {
				String strPosCode=req.getSession().getAttribute("loginPOS").toString();
				String posDate=req.getSession().getAttribute("gPOSDate").toString();
				String [] arrDate=posDate.split(" ");
				strFromdate=arrDate[0].split("-")[2]+"-"+arrDate[0].split("-")[1]+"-"+arrDate[0].split("-")[0];
				strToDate=arrDate[0].split("-")[2]+"-"+arrDate[0].split("-")[1]+"-"+arrDate[0].split("-")[0];		
	        }
			
			 if (reportType.equals("Stock"))
		     {
				resMap=funGetStockFlashData(clientCode,strFromdate,PosCode,strToDate,posName,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
		     }
		     else
		     {
		        resMap=funGetReorderStockFlashData(clientCode,strFromdate,strToDate,posName,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
		     }
			return resMap;
	    }

			
		@SuppressWarnings({ "unchecked" })
		private LinkedHashMap funGetStockFlashData(String clientCode,String strFromdate,String PosCode,String strToDate,String strPOSName,
				String type,String reportType,String groupwise,String showStockWith,String showZeroBalStockYN)
		{									
		   LinkedHashMap resMap = new LinkedHashMap();
		  
		   double amtTotal=0, netTotal=0, paxTotal=0;
	     
	        List colHeader = new ArrayList();
	        String posCode="All";
	        String fromDate1=strFromdate.split("-")[2]+"-"+strFromdate.split("-")[1]+"-"+strFromdate.split("-")[0];
			String toDate1=strToDate.split("-")[2]+"-"+strToDate.split("-")[1]+"-"+strToDate.split("-")[0];
		
			if(!strPOSName.equalsIgnoreCase("ALL"))
			 {
				if(map.containsKey(strPOSName))
				{
					 posCode=(String) map.get(strPOSName);
				}
				
			 }
			
			Map jObj = funStockFlashReportDtl(strFromdate, strToDate,PosCode,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
			List list =new ArrayList();
			List totalList=new ArrayList();
			List jarr = (ArrayList) jObj.get("jArr");
			
			totalList.add("Total :");
			
			 if(null!=jarr)
			 {
				if(jarr.size()>0)
				{
					 for(int i=0;i<jarr.size();i++)
					 {
						Map jObjtemp =(Map) jarr.get(i);
						List arrList=new ArrayList();
						arrList.add(jObjtemp.get("groupName").toString());
						arrList.add(jObjtemp.get("subGroupName").toString());
						arrList.add(jObjtemp.get("itemName").toString());
						arrList.add(jObjtemp.get("Opening Stock").toString());
						arrList.add(jObjtemp.get("Stock In").toString());
						arrList.add(jObjtemp.get("Stock Out").toString());
						arrList.add(jObjtemp.get("Sale").toString());
						arrList.add(jObjtemp.get("Balance").toString());	
						list.add(arrList);
					 }
					 
					   Map jObjTotal =(Map) jObj.get("jObjTatol");;
					   totalList.add(Double.parseDouble(jObjTotal.get("sumOpeningStock").toString()));
					   totalList.add(Double.parseDouble(jObjTotal.get("sumStockIn").toString()));
					   totalList.add(Double.parseDouble(jObjTotal.get("sumStockOut").toString()));
					   totalList.add(Double.parseDouble(jObjTotal.get("sumSaleAmt").toString()));
					   totalList.add(Double.parseDouble(jObjTotal.get("sumBalAmt").toString()));
				 }
			
		      }
				List arrListHeader = null;
				arrListHeader=new ArrayList();
		  		arrListHeader.add("Group");
		  		arrListHeader.add("SubGroup");
		  		arrListHeader.add("Item Name");
		  		arrListHeader.add("Opg Stock");
		  		arrListHeader.add("Stock In");
		  		arrListHeader.add("Stock Out");
		  		arrListHeader.add("Sale");
		  		arrListHeader.add("Bal");
		  		
				resMap.put("listHeader", arrListHeader);
				resMap.put("listDetails", list);
				resMap.put("totalList", totalList);
				
			return resMap;	  
		  }
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/generateProductionStockEntry"}, method=RequestMethod.POST)
	    @ResponseBody
		public clsPOSReportBean funLoadTableData(HttpServletRequest req)
		  {
			List itemList=null;
			clsPOSReportBean obj=new clsPOSReportBean();
			String urlHits="1";
			try
			{
				if(vItemCode.size()>0)
				{
					String clientCode=req.getSession().getAttribute("clientCode").toString();
					String webStockUserCode=req.getSession().getAttribute("usercode").toString();
					String posCode=req.getSession().getAttribute("loginPOS").toString();
					String type=req.getParameter("type");
				}
				
			return obj;	
			}
			catch(Exception ex)
			{
				urlHits="1";
				ex.printStackTrace();
				return obj;
			}
			
		}
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptPOSStockFlashReport", method = RequestMethod.POST)	
		private ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String posName =objBean.getStrPOSName();
			String PosCode=req.getSession().getAttribute("loginPOS").toString();
		    String strFromdate=objBean.getFromDate();
			String strToDate=objBean.getToDate();
			String type=objBean.getStrType();
			String reportType=objBean.getStrReportType();
			String groupwise=objBean.getStrGroupName();
			String showStockWith=objBean.getStrShowStock();
			String showZeroBalStockYN=objBean.getStrShowBalStock();
			
			Map resMap = new LinkedHashMap();
			 if (reportType.equals("Stock"))
		        {
				 	resMap=funGetStockFlashData(clientCode,strFromdate,PosCode,strToDate,posName,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
		        }
		        else
		        {
		        	resMap=funGetReorderStockFlashData(clientCode,strFromdate,strToDate,posName,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
		        	
		        }
				
			List ExportList=new ArrayList();	
			
			String FileName="Stock Flash "+strFromdate+"_To_"+strToDate;
		
			ExportList.add(FileName);
							
			List List=(List)resMap.get("listHeader");
			
			String[] headerList = new String[List.size()];
			for(int i = 0; i < List.size(); i++){
				headerList[i]=(String)List.get(i);
			}
			
			ExportList.add(headerList);
			
			List dataList=(List)resMap.get("listDetails");
			List totalList=(List)resMap.get("totalList"); 	
			
			dataList.add(totalList);
					
			ExportList.add(dataList);
			
		    return new ModelAndView("excelViewWithReportName", "listWithReportName", ExportList);	
	}
		
		public Map funStockFlashReportDtl(String fromDate, String toDate,String strPOSCode,String type,String reportType,String groupName,String balStockSign,String showZeroBalStk) 
		{
			List listRet = new ArrayList();
			StringBuilder sbSql = new StringBuilder();
			List jArr = new ArrayList();
			Map jObjTatol = new HashMap();
			Map jOBjRet = new HashMap();
			double sumOpeningStock = 0.00, sumStockIn = 0.00, sumStockOut = 0.00,sumSaleAmt = 0.00,sumBalAmt=0.00,
					openProductionQty=0.00,sumReorderQty=0.00;
		
			sbSql.setLength(0);
		
			try 
			{
		 
				if(reportType.equals("Stock"))
				{
					sbSql.append(" select strGroupName,strSubgroupName,strItemName,strPOSCode"
		                    + " ,intOpening,intIn,intOut,intSale,intBalance "
		                    + " from tblitemcurrentstk "
		                    + " where strGroupName=if('All'='" + groupName.trim() + "'"
		                    + ",strGroupName,'" + groupName.trim() + "') ");
				
					if (balStockSign.equalsIgnoreCase("Positive"))
			        {
			            if (showZeroBalStk.equals("Yes"))
			            {
			            	sbSql.append(" and intBalance >= 0 ");
			            }
			            else
			            {
			            	sbSql.append(" and intBalance > 0 ");
			            }
			        }
			        else if (balStockSign.equalsIgnoreCase("Negative"))
			        {
			            if (showZeroBalStk.equals("Yes"))
			            {
			            	sbSql.append(" and intBalance <= 0 ");
			            }
			            else
			            {
			            	sbSql.append(" and intBalance < 0 ");
			            }
			        }
					sbSql.append(" order by strItemName");
			        System.out.println(sbSql);
			        
			        List listsbSql = objBaseService.funGetList(sbSql, "sql");
					if (listsbSql.size() > 0) 
					{
						for (int i = 0; i < listsbSql.size(); i++) 
						{
							Object[] obj = (Object[]) listsbSql.get(i);
							Map jObj = new HashMap();
							jObj.put("groupName", obj[0].toString());//Group
							jObj.put("subGroupName", obj[1].toString());//Subgroup
							jObj.put("itemName", obj[2].toString());//Item Name
							jObj.put("Opening Stock", obj[4].toString());//Op Stock
							jObj.put("Stock In", obj[5].toString());//Stk In
							jObj.put("Stock Out", obj[6].toString());//Stk Out
							jObj.put("Sale", obj[7].toString());//Sale
							jObj.put("Balance", obj[8].toString());//Balance
							
							sumOpeningStock =sumOpeningStock+ Double.parseDouble(obj[4].toString());
							sumStockIn = sumStockIn+ Double.parseDouble(obj[5].toString()); 
							sumStockOut = sumStockOut+ Double.parseDouble(obj[6].toString());
							sumSaleAmt = sumSaleAmt+ Double.parseDouble(obj[7].toString());
							sumBalAmt=sumBalAmt+ Double.parseDouble(obj[8].toString());
							
			                jArr.add(jObj);
						}
						jObjTatol.put("sumOpeningStock", sumOpeningStock);
						jObjTatol.put("sumStockIn", sumStockIn);
						jObjTatol.put("sumStockOut", sumStockOut);
						jObjTatol.put("sumSaleAmt", sumSaleAmt);
						jObjTatol.put("sumBalAmt", sumBalAmt);	
				   }
				}
				jOBjRet.put("jObjTatol", jObjTatol);
				jOBjRet.put("jArr", jArr);
			}
			 catch (Exception ex) {
				ex.printStackTrace();
			}
		
			return jOBjRet;
		}
		
		public LinkedHashMap funGetReorderStockFlashData(String clientCode,String fromDate, String toDate,String strPOSName,String type,String reportType,String groupwise,String showStockWith,String showZeroBalStockYN) 
		{
			LinkedHashMap resMap = new LinkedHashMap();
			  
			   double amtTotal=0, netTotal=0, paxTotal=0;
		     
		        List colHeader = new ArrayList();
		        String posCode="All";
		        String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
				String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
			
				if(!strPOSName.equalsIgnoreCase("ALL"))
				 {
					if(map.containsKey(strPOSName))
					{
						 posCode=(String) map.get(strPOSName);
					}
				 }
				
				Map jObj = funStockFlashReportDtl(fromDate, toDate,posCode,type,reportType,groupwise,showStockWith,showZeroBalStockYN);
				List list =new ArrayList();
				List totalList=new ArrayList();
				List jarr = (ArrayList) jObj.get("jArr");
				
				totalList.add("Total :");
				if(null!=jarr)
				{
					if(jarr.size()>0)
					{
						for(int i=0;i<jarr.size();i++)
						 {
							JSONObject jObjtemp =(JSONObject) jarr.get(i);
							List arrList=new ArrayList();
							arrList.add(jObjtemp.get("groupName").toString());
							arrList.add(jObjtemp.get("subGroupName").toString());
							arrList.add(jObjtemp.get("itemName").toString());
							arrList.add(jObjtemp.get("Opening Stock").toString());
							arrList.add(jObjtemp.get("Stock In").toString());
							arrList.add(jObjtemp.get("Stock Out").toString());
							arrList.add(jObjtemp.get("Sale").toString());
							arrList.add(jObjtemp.get("Balance").toString());
							
							vItemCode.add(jObjtemp.get("itemCode").toString());
														
							list.add(arrList);
						 }
						
						   JSONObject jObjTotal =(JSONObject) jObj.get("jObjTatol");;
						   totalList.add(Double.parseDouble(jObjTotal.get("sumOpeningStock").toString()));
						   totalList.add(Double.parseDouble(jObjTotal.get("sumStockIn").toString()));
						   totalList.add(Double.parseDouble(jObjTotal.get("sumStockOut").toString()));
						   totalList.add(Double.parseDouble(jObjTotal.get("sumSaleAmt").toString()));
						   totalList.add(Double.parseDouble(jObjTotal.get("sumBalAmt").toString()));
						   totalList.add(Math.rint(Double.parseDouble(jObjTotal.get("openProductionQty").toString())));
						   totalList.add("    ");
						   totalList.add("    ");
						   totalList.add(Double.parseDouble(jObjTotal.get("sumReorderQty").toString()));   
					}
			    }
				
				List arrListHeader = null;
				arrListHeader=new ArrayList();
		  		arrListHeader.add("Group");
		  		arrListHeader.add("SubGroup");
		  		arrListHeader.add("Item Name");
		  		arrListHeader.add("Opg Stock");
		  		arrListHeader.add("Stock In");
		  		arrListHeader.add("Stock Out");
		  		arrListHeader.add("Sale");
		  		arrListHeader.add("Bal");
		  		arrListHeader.add("Order Qty");
		  		arrListHeader.add("Min Level");
		  		arrListHeader.add("Max Level");
		  		arrListHeader.add("ReOrder Qty");
	
					
				resMap.put("listHeader", arrListHeader);
				resMap.put("listDetails", list);
				resMap.put("totalList", totalList);
				
			return resMap;
		}					
}
		
		



