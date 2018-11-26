package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.nashorn.internal.ir.RuntimeNode.Request;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSBillReportBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsExportToExcel;

@Controller
public class clsPOSSalesSummaryFlashController {

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctions;
	
	@Autowired
    private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsExportToExcel objExportToExcel;
	
	Map<String,String> mapPos = new HashMap<String,String>();

	@RequestMapping(value = "/frmPOSSalesSummaryFlash", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,
			HttpServletRequest request) throws Exception
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		List poslist = new ArrayList();
		poslist.add("ALL");
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				mapPos.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList", poslist);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		List payModeList = new ArrayList<String>();
		payModeList.add("ALL");
	    Map hmPayData=  funGetPaymentMode("ALL");
	    List list=(List)hmPayData.get("payName");
	    for (int i = 0; i < list.size(); i++) 
	    {
			payModeList.add(list.get(i));
		}
		model.put("payModeList", payModeList);
		

		if ("2".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmPOSSalesSummaryFlash_1", "command",
					new clsPOSReportBean());
		} else if ("1".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmPOSSalesSummaryFlash", "command",
					new clsPOSReportBean());
		} else {
			return null;
		}

	}
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadColumnData", method = RequestMethod.GET)	
	private @ResponseBody List funColumnData(HttpServletResponse resp,HttpServletRequest req)
	{
		List listPayMode=new ArrayList();
		try{
			
			String payMode= req.getParameter("payMode").toString() ;
			Map hmPayData=  funGetPaymentMode(payMode);
			listPayMode=(List)hmPayData.get("payName");
		}
		catch(Exception e){
			
		}
		return listPayMode;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processSalesSummeryFlash1", method = RequestMethod.POST)	
	public ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processSalesSummeryFlash", method = RequestMethod.POST)	
	public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
		return true;
	}
	
	private List funGetReportData(HttpServletRequest req, clsPOSReportBean objBean) throws Exception
	{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
		    String posName=objBean.getStrPOSName();
			String strFromdate=objBean.getFromDate();
		    String strToDate=objBean.getToDate();
			String payName = objBean.getStrPayMode();
			String posCode= "ALL";
			String payCode="ALL";
			Map hmPayData= funGetPaymentMode(payName);
			if(!posName.equalsIgnoreCase("ALL"))
			{
				posCode= mapPos.get(posName);
			}
			
			if(!payName.equalsIgnoreCase("ALL"))
			{
				
				Map hmPayCode= (Map)hmPayData.get("payCode");
				payCode=(String) hmPayCode.get(payName);
				
			}
			List listPayMode=(List)hmPayData.get("payName");
			String strReportType = objBean.getStrDocType();
			LinkedHashMap resMap = new LinkedHashMap();
			
			String fromDate1=strFromdate.split("-")[2]+"-"+strFromdate.split("-")[1]+"-"+strFromdate.split("-")[0];
				
			String toDate1=strToDate.split("-")[2]+"-"+strToDate.split("-")[1]+"-"+strToDate.split("-")[0];
				
			resMap=funGetData(strReportType,payCode,fromDate1,toDate1,posCode,listPayMode);
		
			List exportList=new ArrayList();	
		
			String dteFromDate=objBean.getFromDate();
			String dteToDate=objBean.getToDate();
			String fileName="SalesSummeryFlash_"+dteFromDate+"_To_"+dteToDate;
		
			exportList.add(fileName);
						
			List list=(List)resMap.get("listcol");
		
			String[] headerList = new String[list.size()];
			for(int i = 0; i < list.size(); i++)
			{
				headerList[i]=(String)list.get(i);
			}
		
			exportList.add(headerList);
		
			List dataList=(List)resMap.get("List");
			List totalList=(List)resMap.get("totalList");
		
			dataList.add(totalList);
				
			exportList.add(dataList);
		
		return exportList;	
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/loadPaymentData", method = RequestMethod.GET)	
	private @ResponseBody LinkedHashMap funReport(HttpServletResponse resp,HttpServletRequest req)
	{
		 LinkedHashMap resMap = new LinkedHashMap();
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptsalesFlashSummary.jrxml");
			 String fromDate= req.getParameter("fromDate").toString() ;		
             String toDate=req.getParameter("toDate").toString() ;
			 String strFromdate=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
			
			String strToDate=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0]; 
			
			String payName=req.getParameter("payMode").toString();
			
		
			String strPOSName=req.getParameter("posName").toString();

			String strReportType=req.getParameter("reportType").toString();
			
			String posCode= "ALL";
			String payCode="ALL";
			Map hmPayData= funGetPaymentMode(payName);
			if(!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode= mapPos.get(strPOSName);
			}
			
			if(!payName.equalsIgnoreCase("ALL"))
			{
				
				Map hmPayCode= (Map)hmPayData.get("payCode");
				payCode=(String) hmPayCode.get(payName);
				
			}
			List listPayMode=(List)hmPayData.get("payName");
			resMap = funGetData(strReportType,payCode,strFromdate,strToDate,posCode,listPayMode);
						
				
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return resMap;
	
	}
	
	public LinkedHashMap funGetData(String strReportType,String payCode,String strFromdate,String strToDate,String posCode,List listPayMode)
	{
		DecimalFormat decimalFormtFor2DecPoint = new DecimalFormat("0.00");
		LinkedHashMap resMap = new LinkedHashMap();
		List listcol=new ArrayList();
		List list =null;
		
        List totalList=new ArrayList();
        totalList.add("Total");
        totalList.add(" ");
        totalList.add(" ");
		List listData = new ArrayList();
		Map hmSettelmentDesc=new HashMap();
		if(strReportType.equalsIgnoreCase("Daily"))
		 {
			if(payCode.equals("ALL"))
	        {
				listData = objReportService.funProcessSalesSummaryReport( payCode, strReportType, strFromdate, strToDate, posCode);
	        }
			else
			{
				listData = objReportService.funProcessSalesSummaryReport( payCode, strReportType, strFromdate, strToDate, posCode);
			}
			 int rowCount=0;
			 int j=0;
	  	    if(listData.size()>0)
	  	    {
	  	    	list =new ArrayList();
	  	    	List arrList =null;
	  	    	listcol.add("PosCode");
				listcol.add("PosName");
				listcol.add("PosDate");
	  	    	for(int i=0;i<listData.size();i++)
	  	    	{
	  	    		Object[] obj = (Object[]) listData.get(i);
	  	    		hmSettelmentDesc = objReportService.funProcessSalesSummaryReport( obj[0].toString(),obj[2].toString(),"dailySettleAmt");
	  	    		System.out.println(hmSettelmentDesc);
	  	    		arrList =new ArrayList();
	  	    		arrList.add(obj[0].toString());
	  	    		arrList.add(obj[1].toString());
	  	    		arrList.add(obj[2].toString());
					
										
					if(listPayMode.size()>0)
					{
						double total=0.00;
						int pos=2;
						for(j=0;j<listPayMode.size();j++)
						{
							Object objData = (Object) listPayMode.get(j);
							String settlementName = objData.toString();
							if(hmSettelmentDesc.containsKey(settlementName))
							{
								double settleAmt=Double.valueOf(hmSettelmentDesc.get(settlementName).toString());
								arrList.add(String.valueOf(decimalFormtFor2DecPoint.format(settleAmt)));
								if(i>0)
								{
									String amt=totalList.get(pos+1).toString();
									totalList.set(pos+1,String.valueOf(decimalFormtFor2DecPoint.format(settleAmt+Double.valueOf(amt))));
								}
								else
								{
									totalList.add(String.valueOf(decimalFormtFor2DecPoint.format(settleAmt)));
								}
							}
							else
							{
								arrList.add("0.00");
								if(i>0)
								{
									String amt=totalList.get(pos+1).toString();
									totalList.set(pos+1,String.valueOf(decimalFormtFor2DecPoint.format(0+Double.valueOf(amt))));
								}
								else
								{
									totalList.add("0");
								}
							}
							
							if(i==0)
							{
								listcol.add(listPayMode.get(j));
							}
							pos++;
						}
					}
//					list.add(arrList);
					list.add(arrList);
	  	    	}
	  	    	resMap.put("listcol", listcol);
				resMap.put("List", list);
				resMap.put("totalList", totalList);
	  	    }	
		 }
		else
		{
			if(payCode.equals("ALL"))
	        {
				listData = objReportService.funProcessSalesSummaryReport( payCode, strReportType, strFromdate, strToDate, posCode);
	        }
			else
			{
				listData = objReportService.funProcessSalesSummaryReport( payCode, strReportType, strFromdate, strToDate, posCode);
			}
			if(listData.size()>0)
	  	    {
				int j=0;
				list =new ArrayList();
				List arrList =null;
				listcol.add("PosName");
				listcol.add("Month");
				listcol.add("Year");
				for(int i=0;i<listData.size();i++)
	  	    	{
					Object[] obj = (Object[]) listData.get(i);
	  	    	
	  	    		hmSettelmentDesc = objReportService.funProcessSalesSummaryReport( obj[0].toString(),obj[2].toString(),"otherSettleAmt");
	  	    		System.out.println(hmSettelmentDesc);
	  	    		arrList = new ArrayList();
	  	    		arrList.add(obj[1].toString());
	  	    		arrList.add(obj[2].toString());
	  	    		arrList.add(obj[3].toString());
					
					
					if(listPayMode.size()>0)
					{
						double total=0.00;
						int pos=2;
						for(j=0;j<listPayMode.size();j++)
						{
							Object objData = (Object) listPayMode.get(j);
							String settlementName = objData.toString();
							if(hmSettelmentDesc.containsKey(settlementName))
							{
								double settleAmt=Double.valueOf(hmSettelmentDesc.get(settlementName).toString());
								arrList.add(String.valueOf(decimalFormtFor2DecPoint.format(settleAmt)));
								if(i>0)
								{
									String amt=totalList.get(pos+1).toString();
									totalList.set(pos+1,String.valueOf(decimalFormtFor2DecPoint.format(settleAmt+Double.valueOf(amt))));
								}
								else
								{
									totalList.add(String.valueOf(decimalFormtFor2DecPoint.format(settleAmt)));
								}
							}
							else
							{
								arrList.add("0.00");
								if(i>0)
								{
									String amt=totalList.get(pos+1).toString();
									totalList.set(pos+1,String.valueOf(decimalFormtFor2DecPoint.format(0+Double.valueOf(amt))));
								}
								else
								{
									totalList.add("0");
								}
							}
							
							if(i==0)
							{
								listcol.add(listPayMode.get(j));
							}
							pos++;
						}
					}
					list.add(arrList);
	  	    	}
				resMap.put("listcol", listcol);
				resMap.put("List", list);
				resMap.put("totalList", totalList);
	  	    }	
			
		}
		return resMap;
	}
	

	public Map funGetPaymentMode(String payName) 
	{
		List listPayMode = new ArrayList<String>();
		Map hmPayData=new HashMap();
		Map hmPayCode=new HashMap();
		
		String sqlPaymentMode="";
		if(payName.equalsIgnoreCase("ALL"))
		{
		sqlPaymentMode = "select strSettelmentDesc,strSettelmentCode from jpos.tblsettelmenthd order by strSettelmentDesc";
		}else{
			sqlPaymentMode = "select strSettelmentDesc,strSettelmentCode  from jpos.tblsettelmenthd where strSettelmentDesc='"+payName+"' order by strSettelmentDesc";
		}
		List list=objGlobalFunctionsService.funGetList(sqlPaymentMode,"sql");
		if(!list.isEmpty())
		{
			for(int i=0;i<list.size();i++)
			{
			Object[] arrObj=(Object[])list.get(i);
			listPayMode.add(arrObj[0].toString());
			hmPayCode.put(arrObj[0].toString(), arrObj[1].toString());
			}
		}
		hmPayData.put("payName",listPayMode);
		hmPayData.put("payCode", hmPayCode);
		return hmPayData;
	}
}
