package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSBillComparator;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


@Controller
public class clsPOSTableWisePaxReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctions;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;

	HashMap hmPOSData = new HashMap<String, String>();

	@RequestMapping(value = "/frmPOSTableWisePaxReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)throws Exception
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		List poslist = new ArrayList();
		poslist.add("ALL");
		List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				hmPOSData.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList", poslist);
		List sgNameList = new ArrayList<String>();
		sgNameList.add("ALL");

		model.put("sgNameList", sgNameList);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSTableWisePaxReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSTableWisePaxReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSTableWisePaxReport", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptTableWisePaxReport.jrxml");

			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
			String posCode = "ALL";
			List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					hmPOSData.put( obj[1].toString(), obj[0].toString());
				}
			}
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName).toString();
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "1";

			Map mapMultiSettleBills = new HashMap();
			Map<String, List<String>> mapTablePaxList = new HashMap<String, List<String>>();
			List<String> arrListTablePax = null;
            List<clsPOSBillDtl> arrListPaxData = null;

			List list = objReportService.funProcessTableWisePaxReport( posCode, fromDate, toDate, shiftNo, "liveData");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);
					
					int totalPaxNo = 0;
		            if (null != mapTablePaxList.get(objArr[0].toString()))
	                {
	                    String existPax = "";
	                    arrListTablePax = mapTablePaxList.get(objArr[0].toString());
	                    for (int cnt = 0; cnt < arrListTablePax.size(); cnt++)
	                    {
	                        String[] param = arrListTablePax.get(cnt).split("#");
	                        existPax = param[2];
	                        int pax = Integer.parseInt(objArr[2].toString());
	                        totalPaxNo += Integer.parseInt(existPax) + pax;
	                    }
	                    arrListTablePax.remove(objArr[0].toString() + "#" + objArr[1].toString() + "#" + existPax);
	                    arrListTablePax.add(objArr[0].toString() + "#" + objArr[1].toString() + "#" + totalPaxNo);
	                    mapTablePaxList.remove(objArr[0].toString());
	                }
	                else
	                {
	                    arrListTablePax = new ArrayList<String>();
	                    arrListTablePax.add(objArr[0].toString() + "#" + objArr[1].toString() + "#" +objArr[2].toString());
	                }

	                if (null != arrListTablePax)
	                {
	                    mapTablePaxList.put(objArr[0].toString(), arrListTablePax);
	                }
				}
			}

			// QFile
			
			list = objReportService.funProcessTableWisePaxReport( posCode, fromDate, toDate, shiftNo, "qData");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					int totalPaxNo = 0;
	                if (null != mapTablePaxList.get(objArr[0].toString()))
	                {
	                    String existPax = "";
	                    arrListTablePax = mapTablePaxList.get(objArr[0].toString());
	                    for (int cnt = 0; cnt < arrListTablePax.size(); cnt++)
	                    {
	                        String[] param = arrListTablePax.get(cnt).split("#");
	                        existPax = param[2];
	                        int pax = Integer.parseInt(objArr[2].toString());
	                        totalPaxNo += Integer.parseInt(existPax) + pax;
	                    }
	                    arrListTablePax.remove(objArr[0].toString() + "#" + objArr[1].toString() + "#" + existPax);
	                    arrListTablePax.add(objArr[0].toString() + "#" +objArr[1].toString() + "#" + totalPaxNo);
	                    mapTablePaxList.remove(objArr[0].toString());
	                }
	                else
	                {
	                    arrListTablePax = new ArrayList<String>();
	                    arrListTablePax.add(objArr[0].toString() + "#" + objArr[1].toString() + "#" + objArr[2].toString());
	                }

	                if (null != arrListTablePax)
	                {
	                    mapTablePaxList.put(objArr[0].toString(), arrListTablePax);
	                }
				}
			}

			if (mapTablePaxList.size() > 0)
            {
                arrListPaxData = new ArrayList<clsPOSBillDtl>();

                for (Map.Entry<String, List<String>> entry : mapTablePaxList.entrySet())
                {
                    List<String> listOfTablePax = entry.getValue();
                    for (int i = 0; i < listOfTablePax.size(); i++)
                    {

                        String[] tablePaxData = listOfTablePax.get(i).split("#");
                        clsPOSBillDtl objPaxData = new clsPOSBillDtl();
                        objPaxData.setStrBillNo(entry.getKey());
                        objPaxData.setStrTableName(tablePaxData[1]);
                        objPaxData.setIntPAXBillSeriesNo(Integer.parseInt(tablePaxData[2]));
                        arrListPaxData.add(objPaxData);
                    }

                }
            }
			
			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(arrListPaxData);
			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
			jprintlist.add(print);
			String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
			String extension=".pdf";
			if (!objBean.getStrDocType().equals("PDF"))
			{
				objBean.setStrDocType("EXCEL");
				extension=".xls";
			}	
			String fileName = "TableWisePaxReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
					resp.setHeader("Content-Disposition", "inline;filename=TableWisePaxReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=TableWisePaxReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("Hi");

	}
}
