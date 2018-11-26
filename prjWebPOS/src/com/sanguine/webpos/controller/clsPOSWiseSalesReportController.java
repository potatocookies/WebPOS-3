
package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsPOSItemModifierMasterBean;
import com.sanguine.webpos.bean.clsPOSTaxWaiseBean;
import com.sanguine.webpos.bean.clsPOSWiseSalesReportBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsExportToExcel;
import com.sanguine.webpos.util.clsPOSGroupWiseComparator;

@Controller
public class clsPOSWiseSalesReportController {

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
	
	Map map=new HashMap();
	
	@RequestMapping(value = "/frmPOSWiseSalesComparison", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
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
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				map.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList",poslist);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWiseSalesReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSWiseSalesReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processPOSWiseSalesReport1", method = RequestMethod.POST)	
	public ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processPOSWiseSalesReport", method = RequestMethod.POST)	
	public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
		return true;
	}
	
	private List funGetReportData(HttpServletRequest req, clsPOSReportBean objBean) throws Exception
	{
		    String clientCode=req.getSession().getAttribute("gClientCode").toString();
		    String fromDate=objBean.getFromDate();
		 	String toDate=objBean.getToDate();
		 	String strViewType=objBean.getStrViewType();
			
			Map resMap = new LinkedHashMap();
			
			resMap=funGetData(clientCode,fromDate,toDate,strViewType);
			
			List exportList=new ArrayList();	
			
			String FileName="POSWiseSalesReport_"+fromDate+"_To_"+toDate;
		
			exportList.add(FileName);
						
			List List=(List)resMap.get("listcol");
		
			String[] headerList = new String[List.size()];
			for(int i = 0; i < List.size(); i++){
				headerList[i]=(String)List.get(i);
			}
		
			exportList.add(headerList);
		
			List dataList=(List)resMap.get("List");
			List totalList=(List)resMap.get("totalList");
		
			dataList.add(totalList);
				
			exportList.add(dataList);
			
			return exportList;	
	}
	
		    
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadPOSWiseSalesReport"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadPOSWiseSalesReport(HttpServletRequest req)
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
		    String clientCode=req.getSession().getAttribute("gClientCode").toString();
	       
		    String fromDate=req.getParameter("fromDate");
		 
			String toDate=req.getParameter("toDate");
		
			String strViewType=req.getParameter("strViewTypedata");
			
			resMap=funGetData(clientCode,fromDate,toDate,strViewType);
	       
			return resMap;
	    }
			
			
	    
	 
	 
	 private LinkedHashMap funGetData(String clientCode, String fromDate,String toDate, String strViewType)
	  {
		 
			  LinkedHashMap resMap = new LinkedHashMap();
		     
		       String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
				
			   String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];

	          resMap = objReportService.funProcessPosWiseSalesReport(fromDate1,toDate1,strViewType);
	            
		  return resMap;
	  }
}

