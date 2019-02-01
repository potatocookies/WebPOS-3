

package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSCostCenterBean;
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
public class clsPOSCostCenterWiseReportController {
	
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
	 clsPOSMasterService objMasterService;
	 
	 @Autowired
	 clsPOSReportService objReportService;
	
	 Map posMap=new HashMap();
	 Map costCenterMap=new HashMap();
	 
	@RequestMapping(value = "/frmPOSCostCenterWiseReport", method = RequestMethod.GET)
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
		
		List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				posMap.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList",poslist);
		
		List costCenterList = new ArrayList();
		costCenterList.add("ALL");
		List listOfCostCenter = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfCostCenter!=null)
		{
			for(int i =0 ;i<listOfCostCenter.size();i++)
			{
				Object[] obj = (Object[]) listOfCostCenter.get(i);
				costCenterList.add( obj[1].toString());
				costCenterMap.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("costCenterList",costCenterList);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCostCenterWiseReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCostCenterWiseReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSCostCenterWiseReport", method = RequestMethod.POST)	
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req,String source)
	{
		try
		{
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					posMap.put( obj[1].toString(), obj[0].toString());
				}
			}
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = (String) posMap.get(strPOSName);
			}
			hm.put("posCode", posCode);
			
			String strCostCenterName = objBean.getStrSGName();
			String costCenterCode = "ALL";
			if (!strCostCenterName.equalsIgnoreCase("ALL"))
			{
				costCenterCode = (String) costCenterMap.get(strCostCenterName);
			}
			hm.put("costCenterCode", costCenterCode);
			
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String strShiftNo = "1";
			String strReportType = objBean.getStrReportType();
			List<clsPOSCostCenterBean> listOfCostCenterDtl = new LinkedList<>();
			
			if(strReportType.equalsIgnoreCase("Summary"))
			{
				listOfCostCenterDtl =objReportService.funProcessCoseCenterWiseSummaryReport(posCode,fromDate,toDate,strReportType,strShiftNo,costCenterCode,strUserCode);
				String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCostCenterWiseSummaryReport1.jrxml");

				JasperDesign jd = JRXmlLoader.load(reportName);
				JasperReport jr = JasperCompileManager.compileReport(jd);
				List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
				JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCostCenterDtl);
				JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
				jprintlist.add(print);
				String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
				String extension=".pdf";
				if (!objBean.getStrDocType().equals("PDF"))
				{
					objBean.setStrDocType("EXCEL");
					extension=".xls";
				}	
				String fileName = "CostCenterWiseSummaryReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
				filePath=filePath+"/"+fileName;
				if (jprintlist.size() > 0)
				{
					ServletOutputStream servletOutputStream = resp.getOutputStream();
					if (objBean.getStrDocType().equals("PDF"))
					{
						JRExporter exporter = new JRPdfExporter();
						resp.setContentType("application/pdf");
						exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=CostCenterWiseSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
						exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
					}
					else
					{
						JRExporter exporter = new JRXlsExporter();
						resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						if(null!=source && source.equals("DayEndMail"))
							exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath));
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=CostCenterWiseSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
						exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
					}
				}
				else
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					resp.getWriter().append("No Record Found");

				}
	            
			}
			else
			{
				listOfCostCenterDtl = objReportService.funProcessCostCenterWiseDetailReport( posCode, fromDate, toDate, strReportType, strShiftNo, costCenterCode, strUserCode);
				String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCostCenterSalesWiseDetailReport1.jrxml");
				
				JasperDesign jd = JRXmlLoader.load(reportName);
				JasperReport jr = JasperCompileManager.compileReport(jd);
				List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
				JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCostCenterDtl);
				JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
				jprintlist.add(print);
				String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
				String extension=".pdf";
				if (!objBean.getStrDocType().equals("PDF"))
				{
					objBean.setStrDocType("EXCEL");
					extension=".xls";
				}	
				String fileName = "CostCenterWiseDetailReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
				filePath=filePath+"/"+fileName;
				if (jprintlist.size() > 0)
				{
					ServletOutputStream servletOutputStream = resp.getOutputStream();
					if (objBean.getStrDocType().equals("PDF"))
					{
						JRExporter exporter = new JRPdfExporter();
						resp.setContentType("application/pdf");
						exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=CostCenterWiseDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
						exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
					}
					else
					{
						JRExporter exporter = new JRXlsExporter();
						resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						if(null!=source && source.equals("DayEndMail"))
							exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath));
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=CostCenterWiseDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
						exporter.exportReport();
						/*servletOutputStream.flush();
						servletOutputStream.close();*/
					}
				}
				else
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					resp.getWriter().append("No Record Found");

				}
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	            
		
		System.out.println("Hi");
			
	}
	
}
