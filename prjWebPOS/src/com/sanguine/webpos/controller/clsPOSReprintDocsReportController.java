package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;

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


@Controller
public class clsPOSReprintDocsReportController {
	
	
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	 
	@RequestMapping(value = "/frmPOSReprintDocsReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
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
		Map<String,String> hmPOSData;
		
		hmPOSData=new HashMap<String, String>();
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				hmPOSData.put(obj[0].toString(),obj[1].toString());
			}
		}
		model.put("posList",poslist);
		
		StringBuilder sqlBuilder = new StringBuilder("select DISTINCT strUserCreated from tblaudit");
		List listUser= objBaseService.funGetList(sqlBuilder, "sql");
		List userList = new ArrayList();
		userList.add("All");
		if(listUser!=null)
		{
			for(int i =0 ;i<listUser.size();i++)
			{
			
				userList.add(listUser.get(i).toString());
			}
		}
		model.put("listUser",userList);

		StringBuilder sqlDocumentNoBuilder = new StringBuilder("select distinct strDocNo from tblaudit where strTransactionName='Bill'");
		List  docNoList=objBaseService.funGetList(sqlDocumentNoBuilder,"sql");
		List listOfDocNo = new ArrayList();
		listOfDocNo.add("All");
		if(docNoList!=null)
		{
			for(int i =0 ;i<docNoList.size();i++)
			{
				listOfDocNo.add(docNoList.get(i).toString());
			}
		}
		model.put("DocNoList",listOfDocNo);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSReprintDocsReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSReprintDocsReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptReprintDocsReport", method = RequestMethod.POST)	
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
		String POSCode=req.getSession().getAttribute("loginPOS").toString();
		Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String companyName=req.getSession().getAttribute("gCompanyName").toString();
	
		try
		{
			String strType =objBean.getStrViewType();
			String documentNo = objBean.getStrViewBy();
			String userName = objBean.getStrUserCreated();
			String reportName="";
			if(strType.equalsIgnoreCase("Detail"))
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptReprintReport.jrxml");
			}else{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptReprintSummaryReport.jrxml");
			}
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
	
			String posCode = "ALL";
			posCode = objBean.getStrPOSName();
//			if (!strPOSName.equalsIgnoreCase("ALL"))
//			{
//				posCode = (String) hmPOSData.get(strPOSName);
//			}
			hm.put("posCode", posCode);
			
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String strShiftNo = "ALL";
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
			{
				strShiftNo=objBean.getStrShiftCode();
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", strShiftNo);
			List listData = new ArrayList();
			if(strType.equalsIgnoreCase("Detail"))
			{
				listData = objReportService.funReprintDocsDtailReport(fromDate,toDate,strType,userName,documentNo,"Detail");
			}
			else
			{
				listData = objReportService.funReprintDocsDtailReport(fromDate,toDate,strType,userName,documentNo,"Summary");
			}
			
			
	    	    
	            List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
	            JasperDesign jd = JRXmlLoader.load(reportName);
	    	    JasperReport jr = JasperCompileManager.compileReport(jd);
	    
	            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listData);
	            JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
	            jprintlist.add(print);
					
	            
	            if (jprintlist.size()>0)
			    {
			    	ServletOutputStream servletOutputStream = resp.getOutputStream();
			    	if(objBean.getStrDocType().equals("A4 Size Report"))
			    	{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=ReprintDocsReport_"+fromDate+"_To_"+toDate+"_"+userCode+".pdf");
				    exporter.exportReport();
					servletOutputStream.flush();
					servletOutputStream.close();
			    	}else{
			    	if(objBean.getStrDocType().equals("Excel Report"))
			    	{
			    		JRExporter exporter = new JRXlsExporter();
			    		resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=ReprintDocsReport_"+fromDate+"_To_"+toDate+"_"+userCode+".xls");
					    exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
			    	}
			    	}
			    }else
			    {
			    	 resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			    	resp.getWriter().append("No Record Found");
			 		
			    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	            
		
		System.out.println("Hi");
			
	}
	
	
}
