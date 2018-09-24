package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
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
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
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
public class clsPOSBlindSettlementWiseReportController {

	
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
	
	 Map<String,String> hmPOSData;
	 
	 @RequestMapping(value = "/frmPOSBlindSettlementWiseReport1", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
		{
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
			String POSCode=request.getSession().getAttribute("loginPOS").toString();	
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
			List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					poslist.add( obj[1].toString());
					hmPOSData.put( obj[1].toString(), obj[0].toString());
				}
			}
			model.put("posList",poslist);
			
			 Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			 model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());
			
			String posDate = request.getSession().getAttribute("gPOSDate").toString();
			request.setAttribute("POSDate", posDate);
			 
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSBlindSettlementWiseReport1_1","command", new clsPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSBlindSettlementWiseReport1","command", new clsPOSReportBean());
			}else {
				return null;
			}
			 
		}
	 
	 
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptPOSBlindSettlementWiseSales", method = RequestMethod.POST)	
		private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			try
			{
				String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBlindSettelementWiseSalesReport.jrxml");
				String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
				String POSCode=req.getSession().getAttribute("loginPOS").toString();
				Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
				String strPOSName = objBean.getStrPOSName();
				String posCode = "ALL";
				if (!strPOSName.equalsIgnoreCase("ALL"))
				{
					posCode = (String) hmPOSData.get(strPOSName);
				}
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
				
				StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sqlFilter = new StringBuilder();
	            DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
	            DecimalFormat decimalFormat0Dec = new DecimalFormat("0");

	            List listSqlLiveData = objReportService.funProcessLiveBlindSettlementWiseReport(posCode,fromDate,toDate,"live",objSetupParameter.get("gEnableShiftYN").toString(),strShiftNo);

	            List<clsPOSBillItemDtlBean> listOfSettlementData = new ArrayList<clsPOSBillItemDtlBean>();
	            double grossRevenue = 0;
	            if(listSqlLiveData.size()>0)
	            {
	            	for(int i=0;i<listSqlLiveData.size();i++)
	            	{
	            	Object[] obj = (Object[]) listSqlLiveData.get(i);	
	                clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
	                objBillItemDtlBean.setStrPosCode(obj[0].toString());
	                objBillItemDtlBean.setStrSettelmentMode(obj[1].toString());
	                objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[2].toString()));
	                objBillItemDtlBean.setStrPosName(obj[3].toString());
	                objBillItemDtlBean.setNoOfBills(Integer.parseInt(obj[4].toString()));

	                grossRevenue += Double.parseDouble(obj[2].toString());
	                listOfSettlementData.add(objBillItemDtlBean);
	            	}
	            }

	            List listSqlQFileData = objReportService.funProcessLiveBlindSettlementWiseReport(posCode,fromDate,toDate,"qFile",objSetupParameter.get("gEnableShiftYN").toString(),strShiftNo);
	            if(listSqlQFileData.size()>0)
	            {
	            	for(int i=0;i<listSqlQFileData.size();i++)
	            	{
	            	Object[] obj = (Object[]) listSqlQFileData.get(i);
	            	clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
	                objBillItemDtlBean.setStrPosCode(obj[0].toString());
	                objBillItemDtlBean.setStrSettelmentMode(obj[1].toString());
	                objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[2].toString()));
	                objBillItemDtlBean.setStrPosName(obj[3].toString());
	                objBillItemDtlBean.setNoOfBills(Integer.parseInt(obj[4].toString()));

	                grossRevenue += Double.parseDouble(obj[2].toString());
	                listOfSettlementData.add(objBillItemDtlBean);
	            	}
	            }

	            hm.put("grossRevenue", grossRevenue);

	            
	            JasperDesign jd = JRXmlLoader.load(reportName);
				JasperReport jr = JasperCompileManager.compileReport(jd);

				List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
				JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfSettlementData);
				JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
				jprintlist.add(print);

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
						resp.setHeader("Content-Disposition", "inline;filename=BlindSettlementWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=BlindSettlementWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		    
			System.out.println("Hi");
				
		}
	 
		
}
