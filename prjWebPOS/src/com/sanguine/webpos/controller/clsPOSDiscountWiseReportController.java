package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSDiscountComparator;
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
public class clsPOSDiscountWiseReportController
{

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
	
	Map map = new HashMap();

	@RequestMapping(value = "/frmPOSDiscountReport.html", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request) throws Exception
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
				map.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList", poslist);
		
		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSDiscountWiseReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSDiscountWiseReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSDiscountWiseSales", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		
		try
		{
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			String reportName;
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String reportType = objBean.getStrDocType();
			String reportViewType = objBean.getStrViewType();
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					map.put( obj[1].toString(), obj[0].toString());
				}
			}
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = (String) map.get(strPOSName);
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
			
			if(reportViewType.equalsIgnoreCase("Summary"))
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillDiscountReport.jrxml");
				List<clsPOSBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();

				listOfBillItemDtl = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "Summary","",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                JasperDesign jd = JRXmlLoader.load(reportName);
        		JasperReport jr = JasperCompileManager.compileReport(jd);
                List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
        		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfBillItemDtl);
        		JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
        		jprintlist.add(print);
        		String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
    			String extension=".pdf";
    			if (!objBean.getStrDocType().equals("PDF"))
    			{
    				objBean.setStrDocType("EXCEL");
    				extension=".xls";
    			}	
    			String fileName = "DiscountWiseReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
        				resp.setHeader("Content-Disposition", "inline;filename=DiscountWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
        				resp.setHeader("Content-Disposition", "inline;filename=DiscountWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillDiscountDetailReport.jrxml");
				double totalGrossSales=0.0;
                List<clsPOSBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();

                listOfBillItemDtl = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "Detail","",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                
                List listLiveGross = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "Detail","liveGross",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                if (listLiveGross.size()>0)
                {
                	BigDecimal settleAmt = (BigDecimal) listLiveGross.get(0);	
                    totalGrossSales += settleAmt.doubleValue();
                	
                }
                
                //q
                
                listLiveGross = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "Detail","qGross",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                
                if (listLiveGross.size()>0)
                {
                    BigDecimal bd = (BigDecimal) listLiveGross.get(0);
                	totalGrossSales += bd.doubleValue();
                }
              

                Comparator<clsPOSBillItemDtlBean> billDateComparator = new Comparator<clsPOSBillItemDtlBean>()
                {

                    @Override
                    public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2)
                    {
                        return o1.getDteBillDate().compareTo(o2.getDteBillDate());
                    }
                };

                Comparator<clsPOSBillItemDtlBean> billNoComparator = new Comparator<clsPOSBillItemDtlBean>()
                {

                    @Override
                    public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2)
                    {
                        return o1.getStrBillNo().compareTo(o2.getStrBillNo());
                    }
                };

                Comparator<clsPOSBillItemDtlBean> itemCodeComparator = new Comparator<clsPOSBillItemDtlBean>()
                {

                    @Override
                    public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2)
                    {
                        return o1.getStrItemCode().substring(0, 7).compareTo(o2.getStrItemCode().substring(0, 7));
                    }
                };

                Collections.sort(listOfBillItemDtl, new clsPOSDiscountComparator(
                        billDateComparator, billNoComparator, itemCodeComparator
                ));

                //for group wise sales
                
                TreeMap<String, clsPOSGroupSubGroupWiseSales> mapGroupWiseSales = new TreeMap<String, clsPOSGroupSubGroupWiseSales>();
                double totalDiscount = 0.00, totalNetRevenue = 0.00, totalSubTotal = 0.00;

                List listGroupWiseSales = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "GroupWise","live",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                if(listGroupWiseSales.size()>0)
                {
                	for(int i=0;i<listGroupWiseSales.size();i++)
                	{
                	Object[] obj = (Object[]) listGroupWiseSales.get(i);	
                    String groupCode = obj[0].toString();
                    double netTotal = Double.parseDouble(obj[3].toString());
                    double subTotal = Double.parseDouble(obj[7].toString());
                    double discAmt = Double.parseDouble(obj[8].toString());

                    totalNetRevenue += netTotal;
                    totalSubTotal += subTotal;
                    totalDiscount += discAmt;

                    if (mapGroupWiseSales.containsKey(groupCode))
                    {
                        clsPOSGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

                        clsPOSGroupSubGroupWiseSales objNewGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));

                        objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
                        objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
                        objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

                    }
                    else
                    {
                        clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
                    }
                	}
                }
              
                listGroupWiseSales = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "GroupWise","modLive",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                
                if(listGroupWiseSales.size()>0)
                {
                	for(int i=0;i<listGroupWiseSales.size();i++)
                	{
                	Object[] obj = (Object[]) listGroupWiseSales.get(i);	
                    String groupCode = obj[0].toString();
                    double netTotal = Double.parseDouble(obj[3].toString());
                    double subTotal = Double.parseDouble(obj[7].toString());
                    double discAmt = Double.parseDouble(obj[8].toString());

                    totalNetRevenue += netTotal;
                    totalSubTotal += subTotal;
                    totalDiscount += discAmt;

                    if (mapGroupWiseSales.containsKey(groupCode))
                    {
                        clsPOSGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

                        clsPOSGroupSubGroupWiseSales objNewGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));

                        objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
                        objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
                        objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

                    }
                    else
                    {
                        clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
                    }
                }
                }
               

                listGroupWiseSales = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "GroupWise","qFile",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                if(listGroupWiseSales.size()>0)
                {
                	for(int i=0;i<listGroupWiseSales.size();i++)
                	{
                		Object[] obj = (Object[]) listGroupWiseSales.get(i);	
                        String groupCode = obj[0].toString();
                        double netTotal = Double.parseDouble(obj[3].toString());
                        double subTotal = Double.parseDouble(obj[7].toString());
                        double discAmt = Double.parseDouble(obj[8].toString());

                    totalNetRevenue += netTotal;
                    totalSubTotal += subTotal;
                    totalDiscount += discAmt;

                    if (mapGroupWiseSales.containsKey(groupCode))
                    {
                        clsPOSGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

                        clsPOSGroupSubGroupWiseSales objNewGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));

                        objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
                        objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
                        objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

                    }
                    else
                    {
                        clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
                    }
                	}
                }
                
                listGroupWiseSales = objReportService.funProcessDiscountWiseReport(strPOSCode, fromDate, toDate, "GroupWise","modQFile",strUserCode,strShiftNo,objSetupParameter.get("gEnableShiftYN").toString());
                if(listGroupWiseSales.size()>0)
                {
                	for(int i=0;i<listGroupWiseSales.size();i++)
                	{
                		Object[] obj = (Object[]) listGroupWiseSales.get(i);	
                        String groupCode = obj[0].toString();
                        double netTotal = Double.parseDouble(obj[3].toString());
                        double subTotal = Double.parseDouble(obj[7].toString());
                        double discAmt = Double.parseDouble(obj[8].toString());

                    totalNetRevenue += netTotal;
                    totalSubTotal += subTotal;
                    totalDiscount += discAmt;

                    if (mapGroupWiseSales.containsKey(groupCode))
                    {
                        clsPOSGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

                        clsPOSGroupSubGroupWiseSales objNewGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));

                        objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
                        objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
                        objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

                    }
                    else
                    {
                        clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales(
                        		obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
                    }
                	}
                }
               

                hm.put("totalGrossRevenue", totalGrossSales);
                hm.put("subTotal", totalSubTotal);
                hm.put("totalNetRevenue", totalNetRevenue);
                hm.put("totalDiscount", totalDiscount);
                
                JasperDesign jd = JRXmlLoader.load(reportName);
        		JasperReport jr = JasperCompileManager.compileReport(jd);
        		List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
        		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfBillItemDtl);
        		JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
        		jprintlist.add(print);
        		String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
    			String extension=".pdf";
    			if (!objBean.getStrDocType().equals("PDF"))
    			{
    				objBean.setStrDocType("EXCEL");
    				extension=".xls";
    			}	
    			String fileName = "DiscountWiseReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
        				resp.setHeader("Content-Disposition", "inline;filename=DiscountWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
        				resp.setHeader("Content-Disposition", "inline;filename=DiscountWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
			
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("Hi");

	}
	
}