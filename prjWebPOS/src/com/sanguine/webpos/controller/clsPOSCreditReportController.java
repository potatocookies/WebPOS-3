package com.sanguine.webpos.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.POSGlobal.controller.clsGlobalVarClass;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;


@Controller
public class clsPOSCreditReportController
{
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	intfBaseService objBaseService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	
	
	Map hmPOSData = new HashMap<String, String>();
	
	@RequestMapping(value = "/frmCreditReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)throws Exception
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String POSCode=request.getSession().getAttribute("loginPOS").toString();	
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
		model.put("posList", poslist);
		List sgNameList = new ArrayList<String>();
		sgNameList.add("ALL");

		model.put("sgNameList", sgNameList);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSCreditReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSCreditReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptCreditReport", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			List<clsPOSGroupWaiseSalesBean> list = new ArrayList<>();
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditReport.jrxml");
			String strClientCode = req.getSession().getAttribute("gClientCode").toString();
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = (String)hmPOSData.get(strPOSName);
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "ALL";
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
			{
				shiftNo=objBean.getStrShiftCode();
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", shiftNo);
			String reportingdate=objBean.getFromDate()+" to "+objBean.getToDate();
			hm.put("reportingdate", reportingdate);
			
			
			String strSGName = objBean.getStrSGName();
			String sgCode = "ALL";

			String strSGCode = sgCode;
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			list = funCreditReport(hm);
			
			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);


			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
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
					resp.setHeader("Content-Disposition", "inline;filename=CreditReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=CreditReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("Hi");
	}
	
	public List funCreditReport(Map hm)
    {
		List<clsPOSBillDtl> listOfCreditData = new ArrayList<>();	
        try
        {
        	String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditReport.jrxml");

            String fromDate = hm.get("fromDate").toString();
            String toDate = hm.get("toDate").toString();
            
            boolean isDayEndHappend = isDayEndHappened(toDate);
    	    if (!isDayEndHappend)
    	    {
    	    	hm.put("isDayEndHappend", "DAY END NOT DONE.");
    	    }
            
            String posCode = hm.get("posCode").toString();
            String shiftNo = hm.get("shiftNo").toString();
            String posName = hm.get("posName").toString();
            String fromDateToDisplay = hm.get("fromDateToDisplay").toString();
    	    String toDateToDisplay = hm.get("toDateToDisplay").toString();
            
    	    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    	    Date date = new Date();
    	    String printedDate = sdf.format(date);
    	    hm.put("printedDate", printedDate);
    	    hm.put("pageFooterMessage", "END OF Credit Report");

            StringBuilder sqlLiveBuilder = new StringBuilder();
            StringBuilder sqlQBuilder = new StringBuilder();

            sqlLiveBuilder.append("SELECT a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%b-%Y') as billDate,sum(a.dblGrandTotal) billAmt,SUM(b.dblSettlementAmt) CreditAmt,d.strCustomerName\n"
        		    + ",a.strCustomerCode "
        		    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d\n"
        		    + "WHERE a.strBillNo=b.strBillNo AND b.strSettlementCode=c.strSettelmentCode \n"
        		    + "AND DATE(a.dtebilldate)= DATE(b.dtebilldate) \n"
        		    + "AND a.strClientCode=b.strClientCode \n"
        		    + "AND c.strSettelmentType='Credit' \n"
        		    + "AND a.strCustomerCode=d.strCustomerCode \n"
        		    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");

            sqlQBuilder.append("SELECT a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%b-%Y') as billDate,sum(a.dblGrandTotal) billAmt,SUM(b.dblSettlementAmt) CreditAmt,d.strCustomerName\n"
        		    + ",a.strCustomerCode "
        		    + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d\n"
        		    + "WHERE a.strBillNo=b.strBillNo AND b.strSettlementCode=c.strSettelmentCode \n"
        		    + "AND DATE(a.dtebilldate)= DATE(b.dtebilldate) \n"
        		    + "AND a.strClientCode=b.strClientCode \n"
        		    + "AND c.strSettelmentType='Credit' \n"
        		    + "AND a.strCustomerCode=d.strCustomerCode \n"
        		    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");

            if (!posCode.equalsIgnoreCase("All"))
            {
                sqlLiveBuilder.append("and a.strPOSCode='" + posCode + "' ");
                sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
            }

            sqlLiveBuilder.append(" GROUP BY a.strBillNo ORDER BY d.strCustomerName");
            sqlQBuilder.append("GROUP BY a.strBillNo ORDER BY d.strCustomerName");

            //live
            
            List listsqlLiveBuilder = objBaseService.funGetList(sqlLiveBuilder, "sql");
			if (listsqlLiveBuilder.size() > 0)
			{
				clsPOSBillDtl objBean;
				for (int i = 0; i < listsqlLiveBuilder.size(); i++) {
					Object[] obj = (Object[]) listsqlLiveBuilder.get(i);
					objBean=new clsPOSBillDtl();
	                objBean.setStrBillNo(obj[0].toString());
	                objBean.setDteBillDate(obj[1].toString());
	                objBean.setDblBillAmt(Double.parseDouble(obj[2].toString()));
	                objBean.setDblAmount(Double.parseDouble(obj[3].toString()));
	                objBean.setStrCustomerName(obj[4].toString());
	                objBean.setStrCustomerCode(obj[5].toString());
	                listOfCreditData.add(objBean);
				}
			}
           
            //Q
				List listsqlQBuilder = objBaseService.funGetList(sqlQBuilder, "sql");
				if (listsqlQBuilder.size() > 0)
				{
					clsPOSBillDtl objBean;
					for (int i = 0; i < listsqlQBuilder.size(); i++) {
						Object[] obj = (Object[]) listsqlQBuilder.get(i);
						objBean=new clsPOSBillDtl();
						objBean.setStrBillNo(obj[0].toString());
		                objBean.setDteBillDate(obj[1].toString());
		                objBean.setDblBillAmt(Double.parseDouble(obj[2].toString()));
		                objBean.setDblAmount(Double.parseDouble(obj[3].toString()));
		                objBean.setStrCustomerName(obj[4].toString());
		                objBean.setStrCustomerCode(obj[5].toString());
		                listOfCreditData.add(objBean);
		                
					}
					
				}
          
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return listOfCreditData;
    }
	
	public boolean isDayEndHappened(String toDate)
    {

	boolean isDayEndHappend = false;
	try
	{
	    StringBuilder sql = new StringBuilder("select a.strPOSCode,date(a.dtePOSDate),a.strDayEnd "
		    + "from tbldayendprocess a "
		    + "where a.strDayEnd='Y' "
		    + "and a.strPOSCode='" + clsGlobalVarClass.gPOSCode + "' "
		    + "and date(a.dtePOSDate)='" + toDate + "' ");
	   
	    List listsql = objBaseService.funGetList(sql, "sql");
		if (listsql.size() > 0)
		{
	    	isDayEndHappend = true;
	    }
	 
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    
	}
	  return isDayEndHappend;
	
    }
}
