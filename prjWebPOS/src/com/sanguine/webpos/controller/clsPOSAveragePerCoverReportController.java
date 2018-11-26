package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
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
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAPCReport;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSOperatorDtl;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSBillComparator;
import com.sanguine.webpos.comparator.clsPOSGroupSubGroupWiseSalesComparator;
import com.sanguine.webpos.comparator.clsPOSOperatorComparator;
import com.sanguine.webpos.comparator.clsWaiterWiseAPCComparator;
import com.sanguine.webpos.model.clsWaiterMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


@Controller
public class clsPOSAveragePerCoverReportController
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
	
	@Autowired
	private intfBaseService objBaseService;
	
	@Autowired
	private clsSetupService objSetupService;
	 
	@Autowired 
	clsGlobalFunctions objGlobalFun;

	HashMap hmPOSData = new HashMap<String, String>();
	HashMap hmWaiterData = new HashMap<String, String>();
	@RequestMapping(value = "/frmPOSAPC", method = RequestMethod.GET)
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
		
		List waiterlist = new ArrayList();
		waiterlist.add("ALL");
		List listOfWaiters = objMasterService.funGetAllWaitersForMaster(strClientCode);
		if(listOfWaiters!=null)
		{
			for(int i =0 ;i<listOfWaiters.size();i++)
			{
				clsWaiterMasterModel objModel = (clsWaiterMasterModel) listOfWaiters.get(i);
				waiterlist.add(objModel.getStrWShortName());
				hmWaiterData.put(objModel.getStrWaiterNo(),objModel.getStrWShortName());
			}
		}
		model.put("waiterlist", waiterlist);
				
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSAPC_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSAPC", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	List<clsPOSAPCReport> listOfDtl = new LinkedList<clsPOSAPCReport>(); 
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSAvgPerCover", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		try
		{
			String reportName = "";
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName).toString();
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String strPOSWise = objBean.getStrPosWise();
			String strDateWise = objBean.getStrDateWise();
			String strWShortName = objBean.getStrWShortName();
			String strReportMode = objBean.getStrReportType();
			String strAPCOn = objBean.getStrViewType();
			
			if(strReportMode.equalsIgnoreCase("Summary"))
			{
				if (objBean.getStrDocType().equals("PDF"))
				{
					String shiftNo = "All", shiftCode = "All";

					hm.put("shiftNo", shiftNo);
					hm.put("shiftCode", shiftCode);
					hm.put("dateWise", "No");
					if (strDateWise.equalsIgnoreCase("Yes"))
					{
					    hm.put("dateWise", "Yes");
					}
					hm.put("decimalFormaterForDoubleValue", "0.00");
					StringBuilder decimalFormatBuilderForDoubleValue = new StringBuilder("0");
					final String gDecimalFormatString = objGlobalFun.funGetGlobalDecimalFormatString(strClientCode,POSCode);
					
					hm.put("decimalFormaterForDoubleValue", gDecimalFormatString.toString());
					hm.put("decimalFormaterForIntegerValue", "0");

					reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptAPCSummary.jrxml");
					funInsertDataForSummary(fromDate,toDate,strPOSCode,strPOSWise,strDateWise,strWShortName,strAPCOn,strPOSName);
				   
					JasperDesign jd = JRXmlLoader.load(reportName);
		    		JasperReport jr = JasperCompileManager.compileReport(jd);
		            List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
		            JasperPrint print = JasperFillManager.fillReport(jr, hm, new JREmptyDataSource());
		            jprintlist.add(print);
		            String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
					String extension=".pdf";
					if (!objBean.getStrDocType().equals("PDF"))
					{
						objBean.setStrDocType("EXCEL");
						extension=".xls";
					}	
					String fileName = "AvgPerCoverSummaryReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
							resp.setHeader("Content-Disposition", "inline;filename=AvgPerCoverSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
							resp.setHeader("Content-Disposition", "inline;filename=AvgPerCoverSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
			else //for detail
			{
				
					String shiftNo = "All", shiftCode = "All";
					hm.put("strDateWise",strDateWise );
					hm.put("shiftNo", shiftNo);
					hm.put("shiftCode", shiftCode);
					if (strWShortName.equalsIgnoreCase("Yes"))
					{
					    hm.put("waiter", "Waiter Name");
					}
					
					hm.put("decimalFormaterForDoubleValue", "0.00");
					StringBuilder decimalFormatBuilderForDoubleValue = new StringBuilder("0");
					final String gDecimalFormatString = objGlobalFun.funGetGlobalDecimalFormatString(strClientCode,POSCode);
					
					hm.put("decimalFormaterForDoubleValue", gDecimalFormatString.toString());
					hm.put("decimalFormaterForIntegerValue", "0");
					reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptAPCDetail.jrxml");
				    double dinningAmount = funInsertDataForDetailAPC(fromDate,toDate,strPOSCode,strPOSWise,strDateWise,strWShortName,strAPCOn,strPOSName);
				    hm.put("dinningAmt", dinningAmount);
				    
				    JasperDesign jd = JRXmlLoader.load(reportName);
					JasperReport jr = JasperCompileManager.compileReport(jd);
					List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
					JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfDtl);
					JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
					jprintlist.add(print);
					String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
					String extension=".pdf";
					if (!objBean.getStrDocType().equals("PDF"))
					{
						objBean.setStrDocType("EXCEL");
						extension=".xls";
					}	
					String fileName = "AvgPerCoverDetailReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
							resp.setHeader("Content-Disposition", "inline;filename=AvgPerCoverDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
							resp.setHeader("Content-Disposition", "inline;filename=AvgPerCoverDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
	
	  public void funInsertDataForSummary(String fromDate,String toDate,String strPOSCode,String strPOSWise,String strDateWise,String strWShortName,String strAPCOn,String strPOSName) throws Exception
	    {

		objBaseService.funExecuteUpdate("truncate table tblatvreport","sql");
		String apcOnField = "ifnull(sum(a.dblGrandTotal),0.0)";
		if (strAPCOn.equalsIgnoreCase("Net Sale"))
		{
		    apcOnField = "ifnull(sum(a.dblSubTotal)-sum(a.dblDiscountAmt),0.0)";
		}
		else
		{
		    apcOnField = "ifnull(sum(a.dblGrandTotal),0.0)";
		}

		StringBuilder sqlNonComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlFilter = new StringBuilder();

		String posCode = "a.strPOSCode";
		String posName = "d.strPosName";
		if (strPOSCode.equalsIgnoreCase("All") && strPOSWise.equalsIgnoreCase("No"))
		{
		    posCode = "'All'";
		    posName = "'All'";
		}

		//for not multi settle 
		sqlNonComplimentaryBuilder.append("select " + posCode + " ," + posName + ",date(a.dteBillDate) as Date," + apcOnField + " as DiningAmt,sum(intBillSeriesPaxNo),'0' "
			+ "from vqbillhd a,vqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
			+ "where Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ "and a.strPOSCode=d.strPosCode "
			+ "and a.strBillNo=b.strBillNo "
			+ "and b.strSettlementCode=c.strSettelmentCode "
			+ "and a.strOperationType='DineIn' "
			+ "and date(a.dteBillDate)=date(b.dteBillDate) "
			+ "and c.strSettelmentType<>'Complementary' "
			+ "and a.strSettelmentMode!='MultiSettle'  ");

		sqlComplimentaryBuilder.append("select " + posCode + " ," + posName + ",ifnull(date(a.dteBillDate),'')  as Date," + apcOnField + " as DiningAmt,ifnull(sum(intBillSeriesPaxNo),0),'0' "
			+ "from vqbillhd a,vqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
			+ "where Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ "and a.strPOSCode=d.strPosCode "
			+ "and a.strBillNo=b.strBillNo "
			+ "and b.strSettlementCode=c.strSettelmentCode "
			+ "and a.strOperationType='DineIn' "
			+ "and date(a.dteBillDate)=date(b.dteBillDate) "
			+ "and c.strSettelmentType='Complementary' "
			+ "");

		if (!strPOSCode.equalsIgnoreCase("All"))
		{
		    sqlFilter.append("and a.strPOSCode='" + strPOSCode + "' ");
		}

		if (strPOSWise.equalsIgnoreCase("Yes") || strDateWise.equalsIgnoreCase("Yes"))
		{
		    if (strPOSWise.equalsIgnoreCase("Yes") && strDateWise.equalsIgnoreCase("Yes"))
		    {
			sqlFilter.append("group by a.strPOSCode,date(a.dteBillDate) ");
			sqlFilter.append("order by a.strPOSCode,date(a.dteBillDate) ");
		    }
		    else if (strPOSWise.equalsIgnoreCase("Yes"))
		    {
			sqlFilter.append("group by a.strPOSCode ");
			sqlFilter.append("order by a.strPOSCode ");
		    }
		    else if (strDateWise.equalsIgnoreCase("Yes"))
		    {
			sqlFilter.append("group by date(a.dteBillDate) ");
			sqlFilter.append("order by date(a.dteBillDate) ");
		    }
		}
		else
		{
		    sqlFilter.append(" ");
		}

		sqlNonComplimentaryBuilder.append(sqlFilter);
		sqlComplimentaryBuilder.append(sqlFilter);

		Map<String, clsPOSAPCReport> mapAPCReport = new HashMap<>();
		List listNonComplementary = objBaseService.funGetList(sqlNonComplimentaryBuilder,"sql");
		if(listNonComplementary.size()>0)
		{
			for(int i=0;i<listNonComplementary.size();i++)
			{	
				Object[] obj = (Object[]) listNonComplementary.get(i);
			    String key = "";
			    if (strPOSWise.equalsIgnoreCase("Yes") || strDateWise.equalsIgnoreCase("Yes"))
			    {
				if (strPOSWise.equalsIgnoreCase("Yes") && strDateWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[0].toString() + "!" + obj[2].toString();//posCode+date
				}
				else if (strPOSWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[0].toString();//posCode
				}
				else if (strDateWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[2].toString();//date
				}
			    }
			    else
			    {
				sqlFilter.append(" ");
			    }
	
			    if (mapAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapAPCReport.get(key);
	
				objAPCReport.setDblDiningAmt(objAPCReport.getDblDiningAmt() + Double.parseDouble(obj[3].toString()));//dining amt
				objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + Double.parseDouble(obj[4].toString()));//PAX
	
				mapAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiningAmt(Double.parseDouble(obj[3].toString()));//dining amt
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[4].toString()));//PAX
	
				mapAPCReport.put(key, objAPCReport);
			    }
			}

		}
		

		//for multi settle 
		sqlNonComplimentaryBuilder.setLength(0);
		sqlNonComplimentaryBuilder.append("select " + posCode + " ," + posName + ",ifnull(date(a.dteBillDate),'') as Date," + apcOnField + " as DiningAmt,ifnull(sum(intBillSeriesPaxNo),0),'0' "
			+ "from vqbillhd a,tblposmaster d "
			+ "where Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ "and a.strPOSCode=d.strPosCode "
			+ "and a.strOperationType='DineIn' "
			+ "and a.strSettelmentMode='MultiSettle'  ");
		sqlNonComplimentaryBuilder.append(sqlFilter);

		listNonComplementary = objBaseService.funGetList(sqlNonComplimentaryBuilder,"sql");
		if(listNonComplementary.size()>0)
		{
			for(int i=0;i<listNonComplementary.size();i++)
			{
				Object[] obj = (Object[]) listNonComplementary.get(i);
			    String key = "";
			    if (strPOSWise.equalsIgnoreCase("Yes") || strDateWise.equalsIgnoreCase("Yes"))
			    {
				if (strPOSWise.equalsIgnoreCase("Yes") && strDateWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[0].toString() + "!" + obj[2].toString();//posCode+date
				}
				else if (strPOSWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[0].toString();//posCode
				}
				else if (strDateWise.equalsIgnoreCase("Yes"))
				{
				    key = obj[2].toString();//date
				}
			    }
			    else
			    {
				sqlFilter.append(" ");
			    }
	
			    if (mapAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapAPCReport.get(key);
	
				objAPCReport.setDblDiningAmt(objAPCReport.getDblDiningAmt() + Double.parseDouble(obj[3].toString()));//dining amt
				objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + Double.parseDouble(obj[4].toString()));//PAX
	
				mapAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiningAmt(Double.parseDouble(obj[3].toString()));//dining amt
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[4].toString()));//PAX
	
				mapAPCReport.put(key, objAPCReport);
			    }
			}

		}
		
		//truncate
		objBaseService.funExecuteUpdate("truncate tblatvreport","sql");
		//insert non complimentary sales

		for (clsPOSAPCReport objAPCReport : mapAPCReport.values())
		{
		    //insert non complimentary sales
			objBaseService.funExecuteUpdate("Insert into tblatvreport "
			    + "(strPosCode,strPosName,dteDate,dblDiningAmt,dblDiningNoBill,dblHDNoBill) "
			    + "values('" + objAPCReport.getStrPOSCode() + "','" + objAPCReport.getStrPOSName() + "','" + objAPCReport.getDteBillDate() + "','" + objAPCReport.getDblDiningAmt() + "','" + objAPCReport.getDblPAXNo() + "','0') ","sql");
		}

		objBaseService.funExecuteUpdate("update tblatvreport set dblDiningAvg=  dblDiningAmt/dblDiningNoBill","sql");
		//complimenary
		List listComplimentarySales = objBaseService.funGetList(sqlComplimentaryBuilder,"sql");
		if(listComplimentarySales.size()>0)
		{
			for(int i=0;i<listComplimentarySales.size();i++)
			{
				Object[] obj = (Object[]) listComplimentarySales.get(i);
				objBaseService.funExecuteUpdate("update tblatvreport set dblHDNoBill='" + obj[4].toString() + "' "
					    + " where strPosCode='" + obj[0].toString() + "' and dteDate='" + obj[2].toString() + "'  ","sql");
			}
		
		}
		
	    }
	
	
	  public double funInsertDataForDetailAPC(String fromDate,String toDate,String strPOSCode,String strPOSWise,String strDateWise,String strWShortName,String strAPCOn,String strPOSName) throws Exception
	    {
		 
		double dinningAmt = 0.00;
		objBaseService.funExecuteUpdate("truncate table tblatvreport","sql");

		String waiter = strWShortName;
		String waiterCode = "";
		if (strWShortName.equalsIgnoreCase("All"))
		{
		    waiterCode = "All";
		}
		else
		{
		    waiterCode = waiter.split(" ")[1];
		}


		String apcOnField = "a.dblGrandTotal";
		if (strAPCOn.equalsIgnoreCase("Net Sale"))
		{
		    apcOnField = "a.dblSubTotal-a.dblDiscountAmt";
		}
		else
		{
		    apcOnField = "a.dblGrandTotal";
		}

		StringBuilder sqlLiveNonComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlQNonComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlLiveComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlQComplimentaryBuilder = new StringBuilder();
		StringBuilder sqlFilter = new StringBuilder();

		sqlLiveNonComplimentaryBuilder.append("SELECT a.strPOSCode,d.strPosName, DATE(a.dteBillDate) AS DATE,a.strBillNo,a.dblDiscountAmt AS Discount,a.dblSubTotal AS subTotal\n"
			+ ", SUM(intBillSeriesPaxNo), sum(a.dblSubTotal-a.dblDiscountAmt) AS netTotal, a.dblSubTotal-a.dblDiscountAmt AS grandTotal,'0'\n"
			+ ",e.strWShortName\n"
			+ "FROM tblbillhd a\n"
			+ "join tblbillsettlementdtl b on a.strBillNo=b.strBillNo \n"
			+ "join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			+ "join tblposmaster d on a.strPOSCode=d.strPosCode \n"
			+ "left outer join tblwaitermaster e on a.strWaiterNo = e.strWaiterNo \n"
			+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'  \n"
			+ "AND a.strOperationType='DineIn' \n"
			+ "AND c.strSettelmentType<>'Complementary' \n"
			+ "AND a.strSettelmentMode!='MultiSettle' ");
		if (!strPOSCode.equalsIgnoreCase("All"))
		{
		    sqlLiveNonComplimentaryBuilder.append("and a.strPOSCode='" + strPOSCode + "' ");
		}
		sqlLiveNonComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");

		sqlQNonComplimentaryBuilder.append("SELECT a.strPOSCode,d.strPosName, DATE(a.dteBillDate) AS DATE,a.strBillNo,a.dblDiscountAmt AS Discount,a.dblSubTotal AS subTotal\n"
			+ ", SUM(intBillSeriesPaxNo), sum(a.dblSubTotal-a.dblDiscountAmt) AS netTotal, a.dblSubTotal-a.dblDiscountAmt AS grandTotal,'0'\n"
			+ ",e.strWShortName\n"
			+ "FROM tblqbillhd a\n"
			+ "join tblqbillsettlementdtl b on a.strBillNo=b.strBillNo \n"
			+ "join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			+ "join tblposmaster d on a.strPOSCode=d.strPosCode \n"
			+ "left outer join tblwaitermaster e on a.strWaiterNo = e.strWaiterNo \n"
			+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'  \n"
			+ "AND a.strOperationType='DineIn' \n"
			+ "AND c.strSettelmentType<>'Complementary' \n"
			+ "AND a.strSettelmentMode!='MultiSettle' ");
		if (!strPOSCode.equalsIgnoreCase("All"))
		{
		    sqlQNonComplimentaryBuilder.append("and a.strPOSCode='" + strPOSCode + "' ");
		}
		sqlQNonComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");

		sqlLiveComplimentaryBuilder.append("select a.strPOSCode ,d.strPosName,date(a.dteBillDate) as Date,a.strBillNo,"
			+ "a.dblDiscountAmt as Discount,a.dblSubTotal as subTotal,sum(intBillSeriesPaxNo), " + apcOnField + " as netTotal "
			+ ", " + apcOnField + "  as grandTotal,'0',e.strWShortName "
			+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d,tblwaitermaster e "
			+ "where Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ "and a.strPOSCode=d.strPosCode "
			+ "and a.strBillNo=b.strBillNo "
			+ "and b.strSettlementCode=c.strSettelmentCode "
			+ "and a.strOperationType='DineIn' "
			+ "and date(a.dteBillDate)=date(b.dteBillDate) "
			+ "and c.strSettelmentType='Complementary' "
			+ "and a.strWaiterNo = e.strWaiterNo ");
		if (!strWShortName.equalsIgnoreCase("All"))
		{

		    sqlLiveComplimentaryBuilder.append(" and a.strWaiterNo='" + waiterCode + "'");
		}
		sqlLiveComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");
//	                + "");
		sqlQComplimentaryBuilder.append("select a.strPOSCode ,d.strPosName,date(a.dteBillDate) as Date,a.strBillNo,"
			+ "a.dblDiscountAmt as Discount,a.dblSubTotal as subTotal,sum(intBillSeriesPaxNo),  " + apcOnField + " as netTotal"
			+ ", " + apcOnField + " as grandTotal,'0',e.strWShortName "
			+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d,tblwaitermaster e "
			+ "where Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ "and a.strPOSCode=d.strPosCode "
			+ "and a.strBillNo=b.strBillNo "
			+ "and b.strSettlementCode=c.strSettelmentCode "
			+ "and a.strOperationType='DineIn' "
			+ "and date(a.dteBillDate)=date(b.dteBillDate) "
			+ "and c.strSettelmentType='Complementary' "
			+ "and a.strWaiterNo = e.strWaiterNo ");
		if (!strWShortName.equalsIgnoreCase("All"))
		{

		    sqlQComplimentaryBuilder.append(" and a.strWaiterNo='" + waiterCode + "'");
		}
		sqlQComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");

		Map<String, clsPOSAPCReport> mapNonComplementaryBillWiseAPCReport = new HashMap<>();
		Map<String, clsPOSAPCReport> mapComplementaryAPCReport = new HashMap<>();

		List listNonComplementary = objBaseService.funGetList(sqlLiveNonComplimentaryBuilder,"sql");
		if(listNonComplementary.size()>0)
		{
			for(int i=0;i<listNonComplementary.size();i++)
			{
				Object[] obj = (Object[]) listNonComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
	
			    String key = pos + "!" + billDate + "!" + billNo + "!" + waiterName;
	
			    if (mapNonComplementaryBillWiseAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapNonComplementaryBillWiseAPCReport.get(key);
	
				objAPCReport.setGrandTotal(objAPCReport.getGrandTotal() + Double.parseDouble(obj[8].toString()));//net total
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[6].toString()));//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			}

		}
		
		List listQNonComplementary = objBaseService.funGetList(sqlQNonComplimentaryBuilder,"sql");
		if(listQNonComplementary.size()>0)
		{
			for(int i=0;i<listQNonComplementary.size();i++)
			{	
				Object[] obj = (Object[]) listQNonComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
	
			    String key = pos + "!" + billDate + "!" + billNo + "!" + waiterName;
	
			    if (mapNonComplementaryBillWiseAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapNonComplementaryBillWiseAPCReport.get(key);
	
				objAPCReport.setNetTotal(objAPCReport.getNetTotal() + Double.parseDouble(obj[7].toString()));//net total
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[6].toString()));//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			}
		}

		apcOnField = "a.dblGrandTotal";
		if (strAPCOn.equalsIgnoreCase("Net Sale"))
		{
		    apcOnField = "a.dblSubTotal-a.dblDiscountAmt";
		}
		else
		{
		    apcOnField = "a.dblGrandTotal";
		}

		//for only MultiSettle bills
		sqlLiveNonComplimentaryBuilder.setLength(0);
		sqlLiveNonComplimentaryBuilder.append("SELECT a.strPOSCode,d.strPosName, DATE(a.dteBillDate) AS DATE,a.strBillNo,a.dblDiscountAmt AS Discount,a.dblSubTotal AS subTotal "
			+ ", SUM(intBillSeriesPaxNo), " + apcOnField + " AS netTotal, " + apcOnField + " AS grandTotal,'0',e.strWShortName "
			+ "FROM tblbillhd a,tblposmaster d,tblwaitermaster e "
			+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'  "
			+ "AND a.strPOSCode=d.strPosCode  "
			+ "AND a.strOperationType='DineIn'  "
			+ "AND a.strWaiterNo = e.strWaiterNo "
			+ "and a.strSettelmentMode='MultiSettle' ");
		if (!strPOSCode.equalsIgnoreCase("All"))
		{
		    sqlLiveNonComplimentaryBuilder.append("and a.strPOSCode='" + strPOSCode + "' ");
		}
		sqlLiveNonComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");
		listNonComplementary = objBaseService.funGetList(sqlLiveNonComplimentaryBuilder,"sql");
		if(listNonComplementary.size()>0)
		{
			for(int i=0;i<listNonComplementary.size();i++)
			{
				Object[] obj = (Object[]) listNonComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
	
			    String key = pos + "!" + billDate + "!" + billNo + "!" + waiterName;
	
			    if (mapNonComplementaryBillWiseAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapNonComplementaryBillWiseAPCReport.get(key);
	
				objAPCReport.setGrandTotal(objAPCReport.getGrandTotal() + Double.parseDouble(obj[8].toString()));//net total
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[6].toString()));//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			}

		}
		
		//Q
		sqlQNonComplimentaryBuilder.setLength(0);
		sqlQNonComplimentaryBuilder.append("SELECT a.strPOSCode,d.strPosName, DATE(a.dteBillDate) AS DATE,a.strBillNo,a.dblDiscountAmt AS Discount,a.dblSubTotal AS subTotal "
			+ ", SUM(intBillSeriesPaxNo), " + apcOnField + " AS netTotal, " + apcOnField + " AS grandTotal,'0',e.strWShortName "
			+ "FROM tblqbillhd a,tblposmaster d,tblwaitermaster e "
			+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'  "
			+ "AND a.strPOSCode=d.strPosCode  "
			+ "AND a.strOperationType='DineIn'  "
			+ "AND a.strWaiterNo = e.strWaiterNo "
			+ "and a.strSettelmentMode='MultiSettle' ");
		if (!strPOSCode.equalsIgnoreCase("All"))
		{
		    sqlQNonComplimentaryBuilder.append("and a.strPOSCode='" + strPOSCode + "' ");
		}
		sqlQNonComplimentaryBuilder.append(" group by a.strPOSCode,date(a.dteBillDate),a.strBillNo ");
		listQNonComplementary = objBaseService.funGetList(sqlQNonComplimentaryBuilder,"sql");
		if(listQNonComplementary.size()>0)
		{
			for(int i=0;i<listQNonComplementary.size();i++)
			{
				Object[] obj = (Object[]) listQNonComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
	
			    String key = pos + "!" + billDate + "!" + billNo + "!" + waiterName;
	
			    if (mapNonComplementaryBillWiseAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapNonComplementaryBillWiseAPCReport.get(key);
	
				objAPCReport.setNetTotal(objAPCReport.getNetTotal() + Double.parseDouble(obj[7].toString()));//net total
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(Double.parseDouble(obj[6].toString()));//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapNonComplementaryBillWiseAPCReport.put(key, objAPCReport);
			    }
			}

		}
		
		//for complementary sales        
		List listComplementary = objBaseService.funGetList(sqlLiveComplimentaryBuilder,"sql");
		if(listComplementary.size()>0)
		{
			for(int i=0;i<listComplementary.size();i++)
			{
				Object[] obj = (Object[]) listComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
			    double dblPaxNo = Double.parseDouble(obj[6].toString());
	
			    String key = pos + "!" + billDate + "!" + waiterName;
	
			    if (mapComplementaryAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapComplementaryAPCReport.get(key);
	
				objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + dblPaxNo);//PAX No
	
				mapComplementaryAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(dblPaxNo);//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapComplementaryAPCReport.put(key, objAPCReport);
			    }
			}

		}
		
		listComplementary = objBaseService.funGetList(sqlQComplimentaryBuilder,"sql");
		if(listComplementary.size()>0)
		{
			for(int i=0;i<listComplementary.size();i++)
			{
				Object[] obj = (Object[]) listComplementary.get(i);
			    String pos = strPOSCode;
			    String billDate = obj[2].toString();
			    String billNo = obj[3].toString();
			    String waiterName = obj[10].toString();
			    double dblPaxNo = Double.parseDouble(obj[6].toString());
	
			    String key = pos + "!" + billDate + "!" + waiterName;
	
			    if (mapComplementaryAPCReport.containsKey(key))
			    {
				clsPOSAPCReport objAPCReport = mapComplementaryAPCReport.get(key);
	
				objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + dblPaxNo);//PAX No
	
				mapComplementaryAPCReport.put(key, objAPCReport);
			    }
			    else
			    {
				clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
				objAPCReport.setStrPOSCode(obj[0].toString());//posCode
				objAPCReport.setStrPOSName(obj[1].toString());//posName
				objAPCReport.setDteBillDate(obj[2].toString());//date
				objAPCReport.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));//discount
				objAPCReport.setDblSubTotal(Double.parseDouble(obj[5].toString()));//subtotal
				objAPCReport.setDblPAXNo(dblPaxNo);//PAX
				objAPCReport.setNetTotal(Double.parseDouble(obj[7].toString()));//net total
				objAPCReport.setGrandTotal(Double.parseDouble(obj[8].toString()));//grandtotal
				objAPCReport.setStrWaiterName(obj[10].toString());
	
				mapComplementaryAPCReport.put(key, objAPCReport);
			    }
			}    

		}
		
		//truncate
		objBaseService.funExecuteUpdate("truncate tblatvreport","sql");

		Map<String, clsPOSAPCReport> mapNonComplementaryWaiterWiseAPCReport = new HashMap<>();
		for (clsPOSAPCReport objBillWiseAPCReport : mapNonComplementaryBillWiseAPCReport.values())
		{
		    String pos = objBillWiseAPCReport.getStrPOSCode();
		    String billDate = objBillWiseAPCReport.getDteBillDate();
		    String waiterName = objBillWiseAPCReport.getStrWaiterName();
		    double dblPaxNo = objBillWiseAPCReport.getDblPAXNo();
		    double netTotal = objBillWiseAPCReport.getNetTotal();

		    String key = pos + "!" + billDate + "!" + waiterName;

		    if (mapNonComplementaryWaiterWiseAPCReport.containsKey(key))
		    {
			clsPOSAPCReport objWaiterWiseAPCReport = mapNonComplementaryWaiterWiseAPCReport.get(key);

			objWaiterWiseAPCReport.setNetTotal(objWaiterWiseAPCReport.getNetTotal() + netTotal);//net total
			objWaiterWiseAPCReport.setDblPAXNo(objWaiterWiseAPCReport.getDblPAXNo() + dblPaxNo);//PAX No

			mapNonComplementaryWaiterWiseAPCReport.put(key, objWaiterWiseAPCReport);
		    }
		    else
		    {
			mapNonComplementaryWaiterWiseAPCReport.put(key, objBillWiseAPCReport);
		    }
		}

		for (clsPOSAPCReport objAPCReport : mapNonComplementaryWaiterWiseAPCReport.values())
		{
		    //insert non complimentary sales
		    objBaseService.funExecuteUpdate("Insert into tblatvreport "
			    + "(strPosCode,strPosName,dteDate,dblDiningAmt,dblDiningNoBill,dblHDNoBill,strWaiterName) "
			    + "values('" + objAPCReport.getStrPOSCode() + "','" + objAPCReport.getStrPOSName() + "','" + objAPCReport.getDteBillDate() + "'"
			    + ",'" + objAPCReport.getNetTotal() + "','" + objAPCReport.getDblPAXNo() + "','0','" + objAPCReport.getStrWaiterName() + "') ","sql");
		}

		//complimenary
		for (clsPOSAPCReport objCompliAPC : mapComplementaryAPCReport.values())
		{

		    //insert non complimentary sales
			objBaseService.funExecuteUpdate("Insert into tblatvreport "
			    + "(strPosCode,strPosName,dteDate,dblDiningAmt,dblDiningNoBill,dblHDNoBill,strWaiterName,dblDiningAvg) "
			    + "values('" + objCompliAPC.getStrPOSCode() + "','" + objCompliAPC.getStrPOSName() + "','" + objCompliAPC.getDteBillDate() + "'"
			    + ",'0.00','0','" + objCompliAPC.getDblPAXNo() + "','" + objCompliAPC.getStrWaiterName() + "','0.00') ","sql");
		}

		objBaseService.funExecuteUpdate("update tblatvreport set dblDiningAvg=  dblDiningAmt/dblDiningNoBill","sql");
		objBaseService.funExecuteUpdate("update tblatvreport  "
			+ "set dblDiningAvg=0 "
			+ "where dblDiningAvg is null;","sql");

		StringBuilder sqlTempTbl = new StringBuilder();

		sqlTempTbl.append("SELECT\n"
			+ "     a.`strPosCode` AS strPosCode,\n"
			+ "     DATE_FORMAT(date(a.`dteDate`),'%d-%m-%Y') AS dteDate, "
			+ "     sum(a.`dblDiningAmt`) AS dblDiningAmt,\n"
			+ "     sum(a.`dblDiningNoBill`) AS dblDiningNoBill,\n"
			+ "     sum(a.`dblDiningAvg`) AS dblDiningAvg,\n"
			+ "     sum(a.`dblHDAmt`) AS dblHDAmt,\n"
			+ "     sum(a.`dblHDNoBill`) AS dblHDNoBill,\n"
			+ "     a.`dblHdAvg` AS dblHdAvg,\n"
			+ "     a.`dblTAAmt` AS dblTAAmt,\n"
			+ "     a.`dblTANoBill` AS dblTANoBill,\n"
			+ "     a.`dblTAAvg` AS dblTAAvg,    \n"
			+ "     a.`strPosName` AS strPosName,\n"
			+ "    a.`strWaiterName` AS strWaiterName\n"
			+ " FROM\n"
			+ "     `tblatvreport` a");

		String waiterName = "";
		if (strWShortName.equalsIgnoreCase("All"))
		{
		    waiterCode = "All";
		}
		else
		{
		    waiterName = waiter.split(" ")[0];
		    waiterCode = waiter.split(" ")[1];
		}

		if (!strWShortName.equalsIgnoreCase("All"))
		{
		    sqlTempTbl.append(" where a.strWaiterName = '" + waiterName + "'");

		}
		sqlTempTbl.append(" group by a.strPosCode,a.dteDate,a.strWaiterName ");
		List listOfData = objBaseService.funGetList(sqlTempTbl,"sql");
		listOfDtl.clear();
		if(listOfData.size()>0)
		{
			for(int i=0;i<listOfData.size();i++)
			{
				Object[] obj = (Object[]) listOfData.get(i);
			    clsPOSAPCReport objAPCReport = new clsPOSAPCReport();
	
			    objAPCReport.setStrPOSCode(obj[0].toString());//posCode 
			    objAPCReport.setDteBillDate(obj[1].toString());//posDate 
			    objAPCReport.setNetTotal(Double.parseDouble(obj[2].toString()));//dinningAmount 
			    objAPCReport.setDblDiningNoBill(Double.parseDouble(obj[3].toString()));//dinningNoBill 
			    objAPCReport.setDblHDNoBill(Double.parseDouble(obj[6].toString()));//dinningAvg
			    objAPCReport.setDblDiningAvg(Double.parseDouble(obj[4].toString()));//hdAmt
			    objAPCReport.setStrPOSName(obj[11].toString());//posName
			    objAPCReport.setStrWaiterName(obj[12].toString());//waiterName
			    dinningAmt = dinningAmt + Double.parseDouble(obj[2].toString());
	
			    listOfDtl.add(objAPCReport);
			}
		}
		
		Comparator<clsPOSAPCReport> posComparator = new Comparator<clsPOSAPCReport>()
		{

		    @Override
		    public int compare(clsPOSAPCReport o1, clsPOSAPCReport o2)
		    {
			return o2.getStrPOSName().compareToIgnoreCase(o1.getStrPOSName());
		    }
		};

		Comparator<clsPOSAPCReport> dateComparator = new Comparator<clsPOSAPCReport>()
		{

		    @Override
		    public int compare(clsPOSAPCReport o1, clsPOSAPCReport o2)
		    {
			return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
		    }
		};
		Comparator<clsPOSAPCReport> waiterComparator = new Comparator<clsPOSAPCReport>()
		{

		    @Override
		    public int compare(clsPOSAPCReport o1, clsPOSAPCReport o2)
		    {
			return o2.getStrWaiterName().compareToIgnoreCase(o1.getStrWaiterName());
		    }
		};

		Collections.sort(listOfDtl, new clsWaiterWiseAPCComparator(posComparator, dateComparator, waiterComparator));
		return dinningAmt;

	    }
	

}
