package com.sanguine.webpos.controller;

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
import com.sanguine.webpos.comparator.clsCustomerLedgerComparator;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSCustomerLedgerController
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
	
	@RequestMapping(value = "/frmCustomerLedger", method = RequestMethod.GET)
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
		
		List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				hmPOSData.put( obj[0].toString(), obj[1].toString());
			}
		}
		model.put("posList", hmPOSData);
		
		Map hmCustData= new HashMap<String,String>();
		List <clsCustomerMasterModel> listOfCust = objMasterService.funLoadAllCustomerModel(strClientCode);
		if(listOfCust!=null)
		{
			for(int i =0 ;i<listOfCust.size();i++)
			{
				clsCustomerMasterModel obj = (clsCustomerMasterModel) listOfCust.get(i);
				hmCustData.put(obj.getStrCustomerCode(), obj.getStrCustomerName());
				
			}
		}
		model.put("listCustomer",hmCustData);
		List sgNameList = new ArrayList<String>();
		sgNameList.add("ALL");

		model.put("sgNameList", sgNameList);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmCustomerLedger_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmCustomerLedger", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptCustomerLedger", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			List<clsPOSGroupWaiseSalesBean> list = new ArrayList<>();
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCustomerLedger.jrxml");
			String strClientCode = req.getSession().getAttribute("gClientCode").toString();
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strCustomer=objBean.getStrCustomer();
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
			list = funCustomerLedgerReport(hm,objSetupParameter.get("gEnableShiftYN").toString(),strCustomer,clientCode);
			
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
					resp.setHeader("Content-Disposition", "inline;filename=Customer Ledger_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=Customer Ledger_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
	
	public List funCustomerLedgerReport(Map hm,String shiftEnableYN, String strCustomerCode,String clientCode)
    {
		List<clsPOSBillDtl> listOfCustomerLedger = new ArrayList<>();
        try
        {
        	String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCustomerLedger.jrxml");

        	String fromDate = hm.get("fromDate").toString();
    	    String toDate = hm.get("toDate").toString();
    	    String posCode = hm.get("posCode").toString();
    	    String shiftNo = hm.get("shiftNo").toString();
    	    String posName = hm.get("posName").toString();

    	    clsCustomerMasterModel objModel=objMasterService.funSelectedCustomerMasterData(strCustomerCode, clientCode);
    	    String strCustomerName=objModel.getStrCustomerName();
    	    
    	    String fromDateToDisplay = hm.get("fromDateToDisplay").toString();
    	    String toDateToDisplay = hm.get("toDateToDisplay").toString();
          
    	    StringBuilder sbSqlFilters = new StringBuilder();
    	    StringBuilder sbSqlFilters1 = new StringBuilder();

    	    StringBuilder sbSqlLive = new StringBuilder("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date( a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
    		    + " ,d.longMobileNo,a.strRemarks "
    		    + " FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
    		    + " WHERE a.strBillNo=b.strBillNo "
    		    + " AND b.strSettlementCode=c.strSettelmentCode "
    		    + " and date(a.dtebilldate)=date(b.dtebilldate) "
    		    + " and a.strClientCode=b.strClientCode "
    		    + " AND c.strSettelmentType='Credit' "
    		    + " and a.strCustomerCode=d.strCustomerCode "
    		    + " and a.strCustomerCode='" + strCustomerCode + "' "
    		    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

    	    StringBuilder sbSqlQFile = new StringBuilder("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate ,a.strClientCode, SUM(b.dblSettlementAmt)"
    		    + " ,d.longMobileNo,a.strRemarks "
    		    + " FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d"
    		    + " WHERE a.strBillNo=b.strBillNo "
    		    + " AND b.strSettlementCode=c.strSettelmentCode "
    		    + " and date(a.dtebilldate)=date(b.dtebilldate) "
    		    + " and a.strClientCode=b.strClientCode "
    		    + " AND c.strSettelmentType='Credit' "
    		    + " and a.strCustomerCode=d.strCustomerCode"
    		    + " and a.strCustomerCode='" + strCustomerCode  + "' "
    		    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
            

            if (!posCode.equalsIgnoreCase("All"))
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

    	    StringBuilder sqlModLive = new StringBuilder("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
    		    + " ,c.longMobileNo,a.strRemarks "
    		    + " from tblbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c "
    		    + " where a.strBillNo=b.strBillNo "
    		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
    		    + " and a.strClientCode=b.strClientCode "
    		    + " AND a.strCustomerCode=c.strCustomerCode "
    		    + " and a.strCustomerCode='" + strCustomerCode  + "' "
    		    + " and DATE(b.dteReceiptDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

    	    StringBuilder sqlModQFile = new StringBuilder("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo,DATE_FORMAT(date(b.dteReceiptDate),'%d-%m-%Y'),b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName,a.strCustomerCode,a.strClientCode"
    		    + " ,c.longMobileNo,a.strRemarks "
    		    + " from tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c  "
    		    + " where a.strBillNo=b.strBillNo "
    		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
    		    + " and a.strClientCode=b.strClientCode "
    		    + " AND a.strCustomerCode=c.strCustomerCode "
    		    + " and a.strCustomerCode='" + strCustomerCode  + "' "
    		    + " and DATE(b.dteReceiptDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
    	    
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

    	    clsPOSBillDtl objOpeningBalBean = new clsPOSBillDtl();
    	    StringBuilder sqlOpeningBalBuilder = new StringBuilder();
    	    
    	    sqlOpeningBalBuilder.setLength(0);
    	    sqlOpeningBalBuilder.append("SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName,a.strBillNo "
    		    + ",'" + fromDateToDisplay + "' dteBillDate ,a.strClientCode "
    		    + ", SUM(b.dblSettlementAmt) ,d.longMobileNo,a.strRemarks   "
    		    + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d  "
    		    + "WHERE a.strBillNo=b.strBillNo   "
    		    + "AND b.strSettlementCode=c.strSettelmentCode   "
    		    + "and date(a.dtebilldate)=date(b.dtebilldate)  "
    		    + "and a.strClientCode=b.strClientCode   "
    		    + "AND c.strSettelmentType='Credit'   "
    		    + "and a.strCustomerCode=d.strCustomerCode  "
    		    + "and a.strCustomerCode='" + strCustomerCode  + "'   "
    		    + "and date( a.dteBillDate ) < '" + fromDate + "'    ");
    	    
    	    if (!posCode.equals("All"))
    	    {
    	    	sqlOpeningBalBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
    	    }
    	    if (shiftEnableYN.equalsIgnoreCase("Y"))
    	    {
	    		if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
	    		{
	    		    sqlOpeningBalBuilder.append(" and a.intShiftCode = '" + shiftNo + "' ");
	    		}
    	    }
    	    sqlOpeningBalBuilder.append("GROUP BY a.strCustomerCode ");
    	    List listsqlOpeningBalBuilder= objBaseService.funGetList(sqlOpeningBalBuilder, "sql");
    	    if (listsqlOpeningBalBuilder.size() > 0)
			{
    	    	clsPOSBillDtl objOpeningBean;
    	    	for (int i = 0; i < listsqlOpeningBalBuilder.size(); i++) {
					Object[] obj = (Object[]) listsqlOpeningBalBuilder.get(i);
					objOpeningBean=new clsPOSBillDtl();
					objOpeningBalBean.setStrBillNo("Opening Balance");  //Bill No
		    		objOpeningBalBean.setDteBillDate(obj[4].toString());   //Bill Date
		    		objOpeningBalBean.setDblBillAmt(Double.parseDouble(obj[6].toString()));
					
    	    	}
    	    }
    	    sqlOpeningBalBuilder.setLength(0);
    	    sqlOpeningBalBuilder.append("SELECT a.strPOSCode,b.strBillNo,b.strReceiptNo, DATE_FORMAT(DATE(b.dteReceiptDate),'%d-%m-%Y')  "
    		    + ",b.strSettlementName, SUM(b.dblReceiptAmt),b.strChequeNo,b.strBankName,b.strRemarks,c.strCustomerName  "
    		    + ",a.strCustomerCode,a.strClientCode,c.longMobileNo,a.strRemarks  "
    		    + "FROM tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c  "
    		    + "WHERE a.strBillNo=b.strBillNo   "
    		    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
    		    + "AND a.strClientCode=b.strClientCode   "
    		    + "AND a.strCustomerCode=c.strCustomerCode   "
    		    + "AND a.strCustomerCode='" + strCustomerCode  + "'   "
    		    + "AND DATE(b.dteReceiptDate) < '" + fromDate + "'   ");
    	    
    	    if (!posCode.equals("All"))
    	    {
    	    	sqlOpeningBalBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
    	    }
    	    if (shiftEnableYN.equalsIgnoreCase("Y"))
	    	    {
	    		if (shiftEnableYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
	    		{
	    		    sqlOpeningBalBuilder.append(" and a.intShiftCode = '" + shiftNo + "' ");
	    		}
    	    }
    	    sqlOpeningBalBuilder.append("GROUP BY a.strCustomerCode ");
    	    listsqlOpeningBalBuilder = objBaseService.funGetList(sqlOpeningBalBuilder, "sql");
    	    if (listsqlOpeningBalBuilder.size() > 0)
			{
    	    	for (int i = 0; i < listsqlOpeningBalBuilder.size(); i++) {
					Object[] obj = (Object[]) listsqlOpeningBalBuilder.get(i);
					objOpeningBalBean.setDblAmount(Double.parseDouble(obj[5].toString()));   //Receipt Amount
    	    	}
			}
    	    
    	    listOfCustomerLedger.add(objOpeningBalBean);
    	    
    	    List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listsbSqlLive.size() > 0)
			{
				clsPOSBillDtl objBean;
				for (int i = 0; i < listsbSqlLive.size(); i++) {
					Object[] obj = (Object[]) listsbSqlLive.get(i);
					objBean=new clsPOSBillDtl();
					objBean.setStrCustomerName(obj[2].toString());   //Customer Name
		    		objBean.setStrCustomerCode(obj[1].toString());
		    		objBean.setStrReceiptNo("");  //Receipt No
		    		objBean.setStrBillNo(obj[3].toString());  //Bill No
		    		objBean.setDteBillDate(obj[4].toString());   //Bill Date
		    		objBean.setDblBillAmt(Double.parseDouble(obj[6].toString()));
		    		objBean.setLongMobileNo(Long.parseLong(obj[7].toString()));
		    		objBean.setStrRemarks(obj[8].toString());
		    		totalAmt = Double.parseDouble(obj[6].toString());
		    		balanceAmt = totalAmt;
		    		objBean.setDblBalanceAmt(0.00);

		    		listOfCustomerLedger.add(objBean);	
				}
			}
			
			List listsbSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listsbSqlQFile.size() > 0)
			{
				clsPOSBillDtl objBean;
				for (int i = 0; i < listsbSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listsbSqlQFile.get(i);
					objBean=new clsPOSBillDtl();
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
		    		objBean.setStrReceiptNo(""); 

		    		listOfCustomerLedger.add(objBean);
				}
			}
			
			double receiptAmt = 0.00;
			List listsqlModLive = objBaseService.funGetList(sqlModLive, "sql");
			if (listsqlModLive.size() > 0)
			{
				clsPOSBillDtl objBean;
				for (int i = 0; i < listsqlModLive.size(); i++) {
					Object[] obj = (Object[]) listsqlModLive.get(i);
					objBean=new clsPOSBillDtl();
					objBean.setStrBillNo(obj[2].toString());  //Bill No
					objBean.setStrReceiptNo(obj[2].toString());  //Receipt No
					objBean.setDteReceiptDate(obj[3].toString());
					objBean.setDteBillDate(obj[3].toString());   //Bill Date
					objBean.setStrCustomerName(obj[9].toString());   //Customer Name
		    		objBean.setStrCustomerCode(obj[10].toString());
		    		objBean.setDblAmt(Double.parseDouble(obj[5].toString()));
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
		    		    listOfCustomerLedger.add(objBean);
		    		}
				}
			}
			
			List listsqlModQFile = objBaseService.funGetList(sqlModQFile, "sql");
			if (listsqlModQFile.size() > 0)
			{
				clsPOSBillDtl objBean;
				for (int i = 0; i < listsqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listsqlModQFile.get(i);
					objBean=new clsPOSBillDtl();
					objBean.setStrBillNo(obj[2].toString());  //Bill No
					objBean.setStrReceiptNo(obj[2].toString());  //Receipt No
					objBean.setDteReceiptDate(obj[3].toString());
					objBean.setDteBillDate(obj[3].toString());   //Bill Date
					objBean.setStrCustomerName(obj[9].toString());   //Customer Name
		    		objBean.setStrCustomerCode(obj[10].toString());
		    		objBean.setDblAmt(Double.parseDouble(obj[5].toString()));
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
		    		    listOfCustomerLedger.add(objBean);
		    		}
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
			
			Comparator<clsPOSBillDtl> billDate = new Comparator<clsPOSBillDtl>()
			{
				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				{
				    return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
				}
			};
			
			Collections.sort(listOfCustomerLedger, new clsCustomerLedgerComparator(billDate));
    	    
          
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return listOfCustomerLedger;
    }
	
	
	
}
