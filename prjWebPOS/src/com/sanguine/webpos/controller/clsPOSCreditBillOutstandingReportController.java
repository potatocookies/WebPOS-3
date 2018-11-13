package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
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

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSCreditBillReportComparatror;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;

@Controller 
public class clsPOSCreditBillOutstandingReportController
{
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	clsPOSReportService objReportService;
	
	@Autowired
	intfBaseService objBaseService;
	
	DecimalFormat gDecimalFormat ;
	
	Map<String, String> hmPOSData = new HashMap<String,String>();
	
	
	@RequestMapping(value = "/frmPOSCreditBillOutstandingReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)throws Exception
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String POSCode=request.getSession().getAttribute("loginPOS").toString();	
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
			String gDecimalFormatString = objGlobalFunctions.funGetGlobalDecimalFormatString(strClientCode,POSCode);
			gDecimalFormat=new DecimalFormat(gDecimalFormatString);
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
			return new ModelAndView("frmPOSCreditBillOutstandingReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSCreditBillOutstandingReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSCreditBillOutstandingReport", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			List listLive = null;
			List listQFile = null;
			List listModLive = null;
			List listModQFile = null;
			List<clsPOSGroupWaiseSalesBean> list = new ArrayList<>();
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditBillOutstandingSummaryReport.jrxml");
			String strClientCode = req.getSession().getAttribute("gClientCode").toString();
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String reportType=objBean.getStrReportType();
			String strReportType=objBean.getStrDocType();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName);
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
			String strSGName = objBean.getStrSGName();
			String sgCode = "ALL";
			

			String strSGCode = sgCode;
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			list = funCreditBillReport(fromDate,toDate,shiftNo,objSetupParameter.get("gEnableShiftYN").toString(),hm,reportType);
			
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
					resp.setHeader("Content-Disposition", "inline;filename=GroupWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=GroupWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
	
	
	 public List funCreditBillReport(String fromDate,String toDate,String shiftNo,String shiftEnableYN,Map hm,String reportType)
	 {
		 List<clsPOSBillDtl> listOfCreditBillReport = new ArrayList<clsPOSBillDtl>();
		try
		{	
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditBillOutstandingSummaryReport.jrxml");
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			

			StringBuilder sbSqlFilters = new StringBuilder(); 
			StringBuilder sbSqlFilters1 = new StringBuilder();
			
			String posCode = hm.get("posCode").toString();
			String posName = hm.get("posName").toString();

			String fromDateToDisplay = hm.get("fromDateToDisplay").toString();
			String toDateToDisplay = hm.get("toDateToDisplay").toString();
			
			
		    if (reportType.equalsIgnoreCase("Detail"))
		    {
				sbSqlLive.append("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date( a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
					+ " ,d.longMobileNo,a.strRemarks "
					+ " FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
					+ " WHERE a.strBillNo=b.strBillNo "
					+ " AND b.strSettlementCode=c.strSettelmentCode "
					+ " and date(a.dtebilldate)=date(b.dtebilldate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND c.strSettelmentType='Credit' "
					+ " and a.strCustomerCode=d.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	
				sbSqlQFile.append("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
					+ " ,d.longMobileNo,a.strRemarks "
					+ " FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
					+ " WHERE a.strBillNo=b.strBillNo "
					+ " AND b.strSettlementCode=c.strSettelmentCode "
					+ " and date(a.dtebilldate)=date(b.dtebilldate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND c.strSettelmentType='Credit' "
					+ " and a.strCustomerCode=d.strCustomerCode"
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

				if (!posCode.equals("All"))
				{
				    sbSqlFilters1.append(" AND a.strPOSCode = '" + posCode + "' ");
				}
				if (shiftEnableYN.equalsIgnoreCase("Y"))
				{
				    if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
				    {
				    	sbSqlFilters1.append(" and a.intShiftCode = '" + shiftNo + "' ");
				    }
				}
				sbSqlFilters1.append(" GROUP BY a.strCustomerCode,a.strBillNo ");
	
				sqlModLive.append("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
					+ " ,c.longMobileNo,a.strRemarks "
					+ " from tblbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c "
					+ " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND a.strCustomerCode=c.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

				sqlModQFile.append("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
					+ " ,c.longMobileNo,a.strRemarks "
					+ " from tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c  "
					+ " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND a.strCustomerCode=c.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
	
				if (!posCode.equals("All"))
				{
				    sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
				}
				if (shiftEnableYN.equalsIgnoreCase("Y"))
				{
				    if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
				    {
				    	sbSqlFilters.append(" and a.intShiftCode = '" + shiftNo + "' ");
				    }
				}
				sbSqlFilters.append(" group by b.strBillNo,b.strReceiptNo ");
	
				sbSqlLive=sbSqlLive.append(sbSqlFilters1);
				sbSqlQFile=sbSqlQFile.append(sbSqlFilters1);
				sqlModLive=sqlModLive.append(sbSqlFilters);
				sqlModQFile=sqlModQFile.append(sbSqlFilters);
				double balanceAmt = 0.00, totalAmt = 0.00;
				
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++) {
						Object[] obj = (Object[]) listSqlLive.get(i);
						clsPOSBillDtl objBean = new clsPOSBillDtl();
						objBean.setStrCustomerName(obj[2].toString());
						objBean.setStrCustomerCode(obj[1].toString());
					    objBean.setStrReceiptNo("");  //Receipt No
					    objBean.setStrBillNo(obj[3].toString());  //Bill No
					    objBean.setDteBillDate(obj[4].toString());   //Bill Date
					    objBean.setDblBillAmt(Double.parseDouble(obj[6].toString()));
					    objBean.setLongMobileNo(Long.parseLong(obj[7].toString()));
					    objBean.setStrRemarks(obj[8].toString());
					    totalAmt =  Double.parseDouble(obj[6].toString());
					    balanceAmt = totalAmt;
					    objBean.setDblBalanceAmt(0.00);
					    
					    listOfCreditBillReport.add(objBean);
					}
				}
				
				List listsbSqlQFile=objBaseService.funGetList(sbSqlQFile, "sql");
				if (listsbSqlQFile.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++) {
						Object[] obj = (Object[]) listSqlLive.get(i);
						clsPOSBillDtl objBean = new clsPOSBillDtl();
					    objBean.setStrCustomerName(obj[2].toString());   //Customer Name
					    objBean.setStrCustomerCode(obj[1].toString());
					    objBean.setStrBillNo(obj[3].toString());  //Bill No
					    objBean.setDteBillDate(obj[4].toString());   //Bill Date
					    objBean.setDblBillAmt(Double.parseDouble(obj[6].toString()));
					    objBean.setLongMobileNo(Long.parseLong(obj[7].toString()));
					    objBean.setStrRemarks(obj[8].toString());
					    totalAmt = Double.parseDouble(obj[6].toString());
					    balanceAmt = totalAmt;
					    objBean.setDblBalanceAmt(0.00);
					    objBean.setStrReceiptNo("");  //Receipt No
	
					    listOfCreditBillReport.add(objBean);
						
					}
				}
		
				double receiptAmt = 0.00;
				
				List listsqlModLive=objBaseService.funGetList(sqlModLive, "sql");
				if (listsbSqlQFile.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++) {
						Object[] obj = (Object[]) listSqlLive.get(i);
						clsPOSBillDtl objBean = new clsPOSBillDtl();
					    objBean.setStrBillNo(obj[1].toString());   //Bill No
					    objBean.setStrReceiptNo(obj[2].toString());  //Receipt No
					    objBean.setDteReceiptDate(obj[3].toString());
					    objBean.setStrCustomerName(obj[9].toString());
					    objBean.setStrCustomerCode(obj[10].toString());
					    objBean.setDblAmount(Double.parseDouble(obj[5].toString()));   //Receipt Amount
					    objBean.setStrSettlementName(obj[4].toString());
					    objBean.setStrChequeNo(obj[6].toString());
					    objBean.setStrBankName(obj[7].toString());
					    objBean.setStrRemark(obj[8].toString());
					    objBean.setLongMobileNo(Long.parseLong(obj[12].toString()));
					    objBean.setStrRemarks(obj[13].toString());
					    receiptAmt = Double.parseDouble(obj[5].toString());
					    balanceAmt = balanceAmt - receiptAmt;
					    objBean.setDblBalanceAmt(balanceAmt);
					    
					    if (!obj[2].toString().equalsIgnoreCase(""))
					    {
					    	listOfCreditBillReport.add(objBean);
					    }
					}
				}
				List listsqlModQFile=objBaseService.funGetList(sqlModQFile, "sql");
				if (listsqlModQFile.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					clsPOSBillDtl objBean = new clsPOSBillDtl();
				    objBean.setStrBillNo(obj[1].toString());   //Bill No
				    objBean.setStrReceiptNo(obj[2].toString());  //Receipt No
				    objBean.setDteReceiptDate(obj[3].toString());
				    objBean.setStrCustomerName(obj[9].toString());
				    objBean.setStrCustomerCode(obj[10].toString());
				    objBean.setDblAmount(Double.parseDouble(obj[5].toString()));   //Receipt Amount
				    objBean.setStrSettlementName(obj[4].toString());
				    objBean.setStrChequeNo(obj[6].toString());
				    objBean.setStrBankName(obj[7].toString());
				    objBean.setStrRemark(obj[8].toString());
				    objBean.setLongMobileNo(Long.parseLong(obj[12].toString()));
				    objBean.setStrRemarks(obj[13].toString());
				    receiptAmt = Double.parseDouble(obj[5].toString());
				    balanceAmt = balanceAmt - receiptAmt;
				    objBean.setDblBalanceAmt(balanceAmt);
				    if (!obj[2].toString().equalsIgnoreCase(""))
				    {
				    	listOfCreditBillReport.add(objBean);
				    }
				}
			
				Comparator<clsPOSBillDtl> customerComparator = new Comparator<clsPOSBillDtl>()
				{
	
				    @Override
				    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				    {
				    	return o1.getStrCustomerCode().compareToIgnoreCase(o2.getStrCustomerCode());
				    }
				};
				
				Comparator<clsPOSBillDtl> billComparator = new Comparator<clsPOSBillDtl>()
				{
	
				    @Override
				    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				    {
				    	return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				    }
				};
	
				Comparator<clsPOSBillDtl> receiptComparator = new Comparator<clsPOSBillDtl>()
				{
	
				    @Override
				    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				    {
				    	return o1.getStrReceiptNo().compareToIgnoreCase(o2.getStrReceiptNo());
				    }
				};
	
				Comparator<clsPOSBillDtl> remarkComparator = new Comparator<clsPOSBillDtl>()
				{
	
				    @Override
				    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				    {
				    	return o1.getStrRemarks().compareToIgnoreCase(o2.getStrRemarks());
				    }
				};

				Collections.sort(listOfCreditBillReport, new clsPOSCreditBillReportComparatror(
						customerComparator,
						billComparator,
						receiptComparator,
						remarkComparator
						));	
	
			    }
		    }
			    else//summary
			    {
			    	reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCreditBillOutstandingSummaryReport.jrxml");
	
					fromDate = hm.get("fromDate").toString();
					toDate = hm.get("toDate").toString();
					posCode = hm.get("posCode").toString();
					shiftNo = hm.get("shiftNo").toString();
					posName = hm.get("posName").toString();
		
					fromDateToDisplay = hm.get("fromDateToDisplay").toString();
					toDateToDisplay = hm.get("toDateToDisplay").toString();
	
				
				Map<String, clsPOSBillDtl> mapCustomerData = new HashMap<>();

			
				sbSqlLive.setLength(0);
				sbSqlLive.append("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date( a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
					+ " ,d.longMobileNo,a.strRemarks "
					+ " FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
					+ " WHERE a.strBillNo=b.strBillNo "
					+ " AND b.strSettlementCode=c.strSettelmentCode "
					+ " and date(a.dtebilldate)=date(b.dtebilldate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND c.strSettelmentType='Credit' "
					+ " and a.strCustomerCode=d.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				
				sbSqlQFile.setLength(0);
				sbSqlQFile.append("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
					+ " ,d.longMobileNo,a.strRemarks "
					+ " FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
					+ " WHERE a.strBillNo=b.strBillNo "
					+ " AND b.strSettlementCode=c.strSettelmentCode "
					+ " and date(a.dtebilldate)=date(b.dtebilldate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND c.strSettelmentType='Credit' "
					+ " and a.strCustomerCode=d.strCustomerCode"
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

				if (!posCode.equals("All"))
				{
				    sbSqlFilters1.append(" AND a.strPOSCode = '" + posCode + "' ");
				}
				if (shiftEnableYN.equalsIgnoreCase("Y"))
				{
				    if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
				    {
				    	sbSqlFilters1.append(" and a.intShiftCode = '" + shiftNo + "' ");
				    }
				}
				sbSqlFilters1.append(" GROUP BY a.strCustomerCode ");
				
				sqlModLive.setLength(0);
				sqlModLive.append("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
					+ " ,c.longMobileNo,a.strRemarks "
					+ " from tblbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c "
					+ " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND a.strCustomerCode=c.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
				
				sqlModQFile.setLength(0);
				sqlModQFile.append("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
					+ " ,c.longMobileNo,a.strRemarks "
					+ " from tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c  "
					+ " where a.strBillNo=b.strBillNo "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strClientCode=b.strClientCode "
					+ " AND a.strCustomerCode=c.strCustomerCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

				if (!posCode.equals("All"))
				{
				    sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
				}
				if (shiftEnableYN.equalsIgnoreCase("Y"))
				{
				    if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
				    {
				    	sbSqlFilters.append(" and a.intShiftCode = '" + shiftNo + "' ");
				    }
				}
				sbSqlFilters.append(" group by a.strCustomerCode ");
	
				sbSqlLive=sbSqlLive.append(sbSqlFilters1);
				sbSqlQFile=sbSqlQFile.append(sbSqlFilters1);
				sqlModLive=sqlModLive.append(sbSqlFilters);
				sqlModQFile=sqlModQFile.append(sbSqlFilters);
				double balanceAmount = 0.00, tottalCreditAmt = 0.00, tottalReceivedAmt = 0.00;
			
				List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listsbSqlLive.size() > 0)
				{
					for (int i = 0; i < listsbSqlLive.size(); i++) {
						Object[] obj = (Object[]) listsbSqlLive.get(i);
				
				    String custCode = obj[1].toString();
				
	
				    tottalCreditAmt = tottalCreditAmt+Double.parseDouble(obj[6].toString());
				    balanceAmount = +tottalCreditAmt;
					
				    if (mapCustomerData.containsKey(custCode))
				    {
				    	clsPOSBillDtl objB = mapCustomerData.get(custCode);
	
						objB.setDblBillAmt(objB.getDblBillAmt() + Double.parseDouble(obj[6].toString()));
				    }
				    else
				    {
						clsPOSBillDtl objBe = new clsPOSBillDtl();
						objBe.setStrCustomerName(obj[2].toString());   //Customer Name
						objBe.setStrCustomerCode(obj[1].toString());
		
						objBe.setDblBillAmt(Double.parseDouble(obj[6].toString()));
						objBe.setLongMobileNo(Long.parseLong(obj[7].toString()));
		
						mapCustomerData.put(custCode, objBe);
				    }
	
					}
				}
				
				
				List listsbSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
				if (listsbSqlQFile.size() > 0)
				{
					for (int i = 0; i < listsbSqlQFile.size(); i++) {
						Object[] obj = (Object[])listsbSqlQFile.get(i);
			
				    String custCode = obj[1].toString();
				   
	
				    tottalCreditAmt = tottalCreditAmt+Double.parseDouble(obj[6].toString());

				    if (mapCustomerData.containsKey(custCode))
				    {
				    	clsPOSBillDtl objBean = mapCustomerData.get(custCode);
	
				    	objBean.setDblBillAmt(objBean.getDblBillAmt() + Double.parseDouble(obj[6].toString()));
				    }
				    else
				    {
						clsPOSBillDtl objBean = new clsPOSBillDtl();
						objBean.setStrCustomerName(obj[2].toString());   //Customer Name
						objBean.setStrCustomerCode(obj[1].toString());
		
						objBean.setDblBillAmt(Double.parseDouble(obj[6].toString()));
						objBean.setLongMobileNo(Long.parseLong(obj[7].toString()));
		
						mapCustomerData.put(custCode, objBean);
				    }
				    }
				}
				
				List listsqlModLive = objBaseService.funGetList(sqlModLive, "sql");
				if (listsqlModLive.size() > 0)
				{
					for (int i = 0; i < listsqlModLive.size(); i++) {
					Object[] obj = (Object[]) listsqlModLive.get(i);
					
				    String custCode = obj[10].toString();
				    
	
				    if (mapCustomerData.containsKey(custCode))
				    {
						clsPOSBillDtl objBean = mapCustomerData.get(custCode);
		
						objBean.setDblAmount(objBean.getDblAmount() + Double.parseDouble(obj[5].toString()));   //Receipt Amount
				    }
				    else
				    {
						clsPOSBillDtl objBean = new clsPOSBillDtl();
		
						objBean.setStrCustomerName(obj[9].toString());
						objBean.setStrCustomerCode(obj[10].toString());
						objBean.setDblAmount(Double.parseDouble(obj[5].toString()));   //Receipt Amount
		
						tottalReceivedAmt = tottalReceivedAmt+Double.parseDouble(obj[5].toString());
		
						mapCustomerData.put(custCode, objBean);
	
				    }
					}
				}
				
				List listsqlModQFile = objBaseService.funGetList(sqlModQFile, "sql");
				if (listsqlModQFile.size() > 0)
				{
					for (int i = 0; i < listsqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listsqlModQFile.get(i);
					
				    String custCode = obj[10].toString();
				    
	
				    if (mapCustomerData.containsKey(custCode))
				    {
				    	clsPOSBillDtl objBean = mapCustomerData.get(custCode);
	
				    	objBean.setDblAmount(objBean.getDblAmount() + Double.parseDouble(obj[5].toString()));   //Receipt Amount
				    }
				    else
				    {
						clsPOSBillDtl objBean = new clsPOSBillDtl();
		
						objBean.setStrCustomerName(obj[9].toString());
						objBean.setStrCustomerCode(obj[10].toString());
						objBean.setDblAmount(Double.parseDouble(obj[5].toString()));   //Receipt Amount
		
						tottalReceivedAmt = tottalReceivedAmt + Double.parseDouble(obj[5].toString());
		
						mapCustomerData.put(custCode, objBean);
	
				    }
				}
				}
				
				for (clsPOSBillDtl objBean : mapCustomerData.values()) {
					listOfCreditBillReport.add(objBean);
				}
				
				Comparator<clsPOSBillDtl> customerComparator = new Comparator<clsPOSBillDtl>()
				{
				    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				    {
				    	return o1.getStrCustomerName().compareToIgnoreCase(o2.getStrCustomerName());
				    }
				};
			
		 
		}
		
		}
		catch(Exception e) 
		{
		    e.printStackTrace();
		}
		
		return listOfCreditBillReport;
	 }		
			
		
}
