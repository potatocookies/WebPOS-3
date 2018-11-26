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
import com.sanguine.webpos.bean.clsCounterDtlBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsCounterComparator;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSCounterWiseSalesReportController
{
	@Autowired
	private clsPOSMasterService objMasterService;

	@Autowired
	private clsSetupService objSetupService;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private intfBaseService objBaseService;


	Map hmPOSData = new HashMap<String, String>();

	@RequestMapping(value = "/frmPOSCounterWiseSalesReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
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
		poslist.add("All");

		hmPOSData=new HashMap<String, String>();
		List jArryPosList;
		try
		{
			jArryPosList = objMasterService.funFullPOSCombo(strClientCode);
			for(int i =0 ;i<jArryPosList.size();i++)
			{
				Object[] obj = (Object[]) jArryPosList.get(i);
				poslist.add(obj[1].toString());
				hmPOSData.put(obj[1].toString(), obj[0].toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		model.put("posList",poslist);

		//for pos date
		String strPosCode=request.getSession().getAttribute("loginPOS").toString();
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCounterWiseSalesReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCounterWiseSalesReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
	}


	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptCounterWiseSalesReport", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		String selectType=objBean.getStrType();
		String reportType=objBean.getStrDocType();
		String fromDate=objBean.getFromDate();
		String toDate= objBean.getToDate();
		Map companyName=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gClientName");
		String reportName="";
		String imagePath="";
		try{
			if(objBean.getStrType().equals("Menu Wise"))
			{

				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCounterWiseMenuHeadSales.jrxml");
				imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");//

			}
			else if(objBean.getStrType().equals("Group Wise"))
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCounterWiseGroupReport.jrxml");
				imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");//

			}
			else
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptCounterWiseSubGroupReport.jrxml");
				imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");//

			}
			List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();

			String strFromdate=objBean.getFromDate().split("-")[2]+"-"+objBean.getFromDate().split("-")[1]+"-"+objBean.getFromDate().split("-")[0];
			String strToDate=objBean.getToDate().split("-")[2]+"-"+objBean.getToDate().split("-")[1]+"-"+objBean.getToDate().split("-")[0]; 
			String strShiftNo=req.getSession().getAttribute("gShifts").toString();
			String strPOSCode=req.getSession().getAttribute("loginPOS").toString();
			String strPOSName =objBean.getStrPOSName();
			posCode= "All";
			List listOfPos = objMasterService.funFullPOSCombo(clientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					hmPOSData.put( obj[1].toString(), obj[0].toString());
				}
			}
			if(!strPOSName.equalsIgnoreCase("All"))
			{
				posCode= hmPOSData.get(strPOSName).toString();
			}
			HashMap hm = new HashMap();
			hm.put("posCode", posCode);
			hm.put("posName", strPOSName);
			hm.put("imagePath", imagePath);
			hm.put("clientName", companyName);
			hm.put("fromDateToDisplay", strFromdate);
			hm.put("toDateToDisplay", strToDate);
			hm.put("shiftCode", "1");
			hm.put("userName", userCode);

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			List listOfCounterSales = funCounterWiseSalesReport(strFromdate, strToDate,strPOSCode,strShiftNo,userCode,reportType);

			JRBeanCollectionDataSource beanCollectionDataSource;
			if(objBean.getStrType().equals("Menu Wise"))
			{
				beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCounterSales);
			}
			else if(objBean.getStrType().equals("Group Wise"))
			{
				beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCounterSales);
			}
			else
			{
				beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCounterSales);
			}

			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
			jprintlist.add(print);
			String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
			String extension=".pdf";
			if (!objBean.getStrDocType().equals("PDF"))
			{
				objBean.setStrDocType("EXCEL");
				extension=".xls";
			}	
			String fileName = "CounterWiseSalesReport_"+ fromDate + "_To_" + toDate + "_" + userCode + extension;
			filePath=filePath+"/"+fileName;
			if (jprintlist.size()>0)
			{
				ServletOutputStream servletOutputStream = resp.getOutputStream();
				if(objBean.getStrDocType().equals("PDF"))
				{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=CounterWiseSalesReport_"+ reportType +"_"+ strFromdate+"_To_"+strToDate+"_"+userCode+".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=CounterWiseSalesReport_"+ reportType +"_"+strFromdate+"_To_"+strToDate+"_"+userCode+".xls");
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public List funCounterWiseSalesReport(String fromDate, String toDate,String strPOSCode, String strShiftNo, String strUserCode,
					String selectType) 
	{
		Map hmCounterWiseSales=new HashMap();
		List jArr = new ArrayList();
		List jOBjRet = new ArrayList();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQfile = new StringBuilder();
		StringBuilder sbFilter = new StringBuilder();
		
		List<clsCounterDtlBean> listOfSubGroupWiseCounterData = new ArrayList<>();
		List<clsCounterDtlBean> listOfCounterWiseMenuHeadWiseData = new ArrayList<>();
		List<clsCounterDtlBean> listOfCounterWiseGroupWiseData = new ArrayList<>();
		
		if (selectType.equalsIgnoreCase("Menu Wise")) 
		{
			sbSqlLive.setLength(0);
			sbSqlQfile.setLength(0);
			sbFilter.setLength(0);
			try{
			sbSqlLive
			.append(" select ifnull(b.strCounterCode,'NA') as strCounterCode ,"
					+ " ifnull(d.strCounterName,'NA') as   strCounterName ,ifNull(e.strMenuCode,'NA') as strMenuCode,"
					+ " ifnull(e.strMenuName,'NA') as strMenuName,  b.dblRate,sum(b.dblquantity) as dblquantity ,"
					+ " sum(b.dblamount) as dblamount"
					+ " from tblbillhd a ,tblbilldtl b ,tblmenuitempricingdtl c ,tblcounterhd d,tblmenuhd e "
					+ " where a.strBillNo=b.strBillNo and b.stritemcode = c.strItemCode and b.strCounterCode=d.strCounterCode "
					+ " and a.strPOSCode = c.strPosCode "
					+ " and c.strMenuCode=e.strMenuCode and date(a.dteBillDate) between '"
					+ fromDate + "' and '" + toDate + "' ");

			sbSqlQfile
			.append(" select ifnull(b.strCounterCode,'NA') as strCounterCode ,"
					+ " ifnull(d.strCounterName,'NA') as   strCounterName ,ifNull(e.strMenuCode,'NA') as strMenuCode,"
					+ " ifnull(e.strMenuName,'NA') as strMenuName,  b.dblRate,sum(b.dblquantity) as dblquantity ,"
					+ " sum(b.dblamount) as dblamount"
					+ " from tblqbillhd a ,tblqbilldtl b ,tblmenuitempricingdtl c ,tblcounterhd d,tblmenuhd e "
					+ " where a.strBillNo=b.strBillNo and b.stritemcode = c.strItemCode and b.strCounterCode=d.strCounterCode "
					+ " and a.strPOSCode = c.strPosCode "
					+ " and c.strMenuCode=e.strMenuCode and date(a.dteBillDate) between '"
					+ fromDate + "' and '" + toDate + "' ");

			if (!strPOSCode.equals("All")) {
				sbFilter.append(" AND a.strPoscode = '" + strPOSCode + "' ");
			}
			sbFilter.append(" and a.intShiftCode = '" + strShiftNo + "' ");

			sbFilter.append("  and a.strAdvBookingNo ='' "
					+ " group by b.strCounterCode,d.strCounterName, e.strMenuCode,e.strMenuName "
					+ " order by d.strCounterName,e.strMenuName  ");
			sbSqlLive.append(sbFilter);
			sbSqlQfile.append(sbFilter);
			
			List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listsbSqlLive.size() > 0) {
				for (int i = 0; i < listsbSqlLive.size(); i++) {
					Object[] ob = (Object[]) listsbSqlLive.get(i);
					clsCounterDtlBean obj = new clsCounterDtlBean();
					obj.setStrCounterCode(ob[0].toString());
					obj.setStrCounterName(ob[1].toString());
					obj.setStrMenuCode(ob[2].toString());
					obj.setStrMenuName(ob[3].toString());
					obj.setDblRate(Double.parseDouble(ob[4].toString()));
					obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
					obj.setDblAmount(Double.parseDouble(ob[6].toString()));

					listOfCounterWiseMenuHeadWiseData.add(obj);
				}
			}
			// QData
			List listsbSqlQfile = objBaseService.funGetList(sbSqlQfile, "sql");
			if (listsbSqlQfile.size() > 0) {
				for (int i = 0; i < listsbSqlQfile.size(); i++) {
					Object[] ob = (Object[]) listsbSqlQfile.get(i);
					clsCounterDtlBean obj = new clsCounterDtlBean();
					obj.setStrCounterCode(ob[0].toString());
					obj.setStrCounterName(ob[1].toString());
					obj.setStrMenuCode(ob[2].toString());
					obj.setStrMenuName(ob[3].toString());
					obj.setDblRate(Double.parseDouble(ob[4].toString()));
					obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
					obj.setDblAmount(Double.parseDouble(ob[6].toString()));

					listOfCounterWiseMenuHeadWiseData.add(obj);
				}
			}
			Comparator<clsCounterDtlBean> counterCodeComparator = new Comparator<clsCounterDtlBean>() {

				@Override
				public int compare(clsCounterDtlBean o1, clsCounterDtlBean o2) {
					return o1.getStrCounterCode().compareTo(
							o2.getStrCounterCode());
				}
			};
			Comparator<clsCounterDtlBean> menuCodeComparator = new Comparator<clsCounterDtlBean>() {

				@Override
				public int compare(clsCounterDtlBean o1, clsCounterDtlBean o2) {
					return o1.getStrMenuCode().compareTo(o2.getStrMenuCode());
				}
			};
			Collections.sort(listOfCounterWiseMenuHeadWiseData,
					new clsCounterComparator(counterCodeComparator,
							menuCodeComparator));
		
		}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
		else if (selectType.equalsIgnoreCase("Group Wise")) 
		{
			sbSqlLive.setLength(0);
			sbSqlQfile.setLength(0);
			sbFilter.setLength(0);
			try {
				sbSqlLive
				.append(" select ifnull(d.strCounterCode,'NA') as strCounterCode ,ifnull(d.strCounterName,'NA') as strCounterName , "
						+ " ifNull(h.strGroupCode,'NA') as strGroupCode,ifnull(h.strGroupName,'NA') as strGroupName,b.dblRate,sum(b.dblquantity) as dblquantity ,sum(b.dblamount) as dblamount"
						+ " from tblbillhd a ,tblbilldtl b,tblcounterhd d, tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						+ " where a.strBillNo=b.strBillNo and b.strCounterCode=d.strCounterCode "
						+ " and b.stritemcode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode"
						+ " and g.strGroupCode=h.strGroupCode and date(a.dteBillDate) between '"
						+ fromDate + "' and '" + toDate + "'  ");

				sbSqlQfile
				.append(" select ifnull(d.strCounterCode,'NA') as strCounterCode ,ifnull(d.strCounterName,'NA') as strCounterName , "
						+ " ifNull(h.strGroupCode,'NA') as strGroupCode,ifnull(h.strGroupName,'NA') as strGroupName,b.dblRate,sum(b.dblquantity) as dblquantity ,sum(b.dblamount) as dblamount"
						+ " from tblqbillhd a ,tblqbilldtl b,tblcounterhd d, tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						+ " where a.strBillNo=b.strBillNo and b.strCounterCode=d.strCounterCode "
						+ " and b.stritemcode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode"
						+ " and g.strGroupCode=h.strGroupCode and date(a.dteBillDate) between '"
						+ fromDate + "' and '" + toDate + "'  ");

				if (!strPOSCode.equals("All")) {
					sbFilter.append(" AND a.strPoscode = '" + strPOSCode + "' ");
				}

				sbFilter.append(" and a.intShiftCode = '" + strShiftNo + "' ");
				sbFilter.append(" and a.strAdvBookingNo ='' "
						+ " group by d.strCounterCode,d.strCounterName, h.strGroupName,g.strSubGroupCode "
						+ " order by d.strCounterName,h.strGroupName  ");

				sbSqlLive.append(sbFilter);
				sbSqlQfile.append(sbFilter);

				

				// live data
				List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listsbSqlLive.size() > 0) {
					for (int i = 0; i < listsbSqlLive.size(); i++) {
						Object[] ob = (Object[]) listsbSqlLive.get(i);
						clsCounterDtlBean obj = new clsCounterDtlBean();
						obj.setStrCounterCode(ob[0].toString());
						obj.setStrCounterName(ob[1].toString());
						obj.setStrGroupCode(ob[2].toString());
						obj.setStrGroupName(ob[3].toString());
						obj.setDblRate(Double.parseDouble(ob[4].toString()));
						obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
						obj.setDblAmount(Double.parseDouble(ob[6].toString()));

						listOfCounterWiseGroupWiseData.add(obj);
					}
				}

				// QData
				List listsbSqlQfile = objBaseService.funGetList(sbSqlQfile, "sql");
				if (listsbSqlQfile.size() > 0) {
					for (int i = 0; i < listsbSqlQfile.size(); i++) {
						Object[] ob = (Object[]) listsbSqlQfile.get(i);
						clsCounterDtlBean obj = new clsCounterDtlBean();
						obj.setStrCounterCode(ob[0].toString());
						obj.setStrCounterName(ob[1].toString());
						obj.setStrGroupCode(ob[2].toString());
						obj.setStrGroupName(ob[3].toString());
						obj.setDblRate(Double.parseDouble(ob[4].toString()));
						obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
						obj.setDblAmount(Double.parseDouble(ob[6].toString()));

						listOfCounterWiseGroupWiseData.add(obj);
					}
				}
				Comparator<clsCounterDtlBean> counterCodeComparator = new Comparator<clsCounterDtlBean>() {

					@Override
					public int compare(clsCounterDtlBean o1,
							clsCounterDtlBean o2) {
						return o1.getStrCounterCode().compareTo(
								o2.getStrCounterCode());
					}
				};
				Comparator<clsCounterDtlBean> groupCodeComparator = new Comparator<clsCounterDtlBean>() {

					@Override
					public int compare(clsCounterDtlBean o1,
							clsCounterDtlBean o2) {
						return o1.getStrGroupCode().compareToIgnoreCase(
								o2.getStrGroupCode());
					}
				};
				Comparator<clsCounterDtlBean> groupNameComparator = new Comparator<clsCounterDtlBean>() {

					@Override
					public int compare(clsCounterDtlBean o1,
							clsCounterDtlBean o2) {
						return o1.getStrGroupName().compareToIgnoreCase(
								o2.getStrGroupName());
					}
				};
				Collections.sort(listOfCounterWiseGroupWiseData,
						new clsCounterComparator(counterCodeComparator,
								groupCodeComparator, groupNameComparator));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		else 
		{
			sbSqlLive.setLength(0);
			sbSqlQfile.setLength(0);
			sbFilter.setLength(0);
			try {
				sbSqlLive
				.append(" select ifnull(d.strCounterCode,'NA') as strCounterCode ,ifnull(d.strCounterName,'NA') as strCounterName , "
						+ " e.strSubGroupCode,e.strSubGroupName, "
						+ " b.dblRate,sum(b.dblquantity) as dblquantity ,sum(b.dblamount) as dblamount"
						+ " from tblbillhd a ,tblbilldtl b,tblitemmaster c ,tblcounterhd d ,tblsubgrouphd e "
						+ " where a.strBillNo=b.strBillNo and b.strCounterCode=d.strCounterCode "
						+ " and b.stritemcode=c.strItemCode and c.strSubGroupCode=e.strSubGroupCode "
						+ " and date(a.dteBillDate) between '"
						+ fromDate + "' and '" + toDate + "'  ");

				sbSqlQfile
				.append(" select ifnull(d.strCounterCode,'NA') as strCounterCode ,ifnull(d.strCounterName,'NA') as strCounterName , "
						+ " e.strSubGroupCode,e.strSubGroupName, "
						+ " b.dblRate,sum(b.dblquantity) as dblquantity ,sum(b.dblamount) as dblamount"
						+ " from tblqbillhd a ,tblqbilldtl b,tblitemmaster c ,tblcounterhd d ,tblsubgrouphd e "
						+ " where a.strBillNo=b.strBillNo and b.strCounterCode=d.strCounterCode "
						+ " and b.stritemcode=c.strItemCode and c.strSubGroupCode=e.strSubGroupCode "
						+ " and date(a.dteBillDate) between '"
						+ fromDate + "' and '" + toDate + "'  ");

				if (!strPOSCode.equals("All")) {
					sbFilter.append(" AND a.strPoscode = '" + strPOSCode + "' ");
				}

				sbFilter.append(" and a.intShiftCode = '" + strShiftNo + "' ");
				sbFilter.append(" and a.strAdvBookingNo ='' "
						+ " group by d.strCounterCode,d.strCounterName, e.strSubGroupCode,e.strSubGroupName "
						+ " order by d.strCounterName,e.strSubGroupName   ");
				sbSqlLive.append(sbFilter);
				sbSqlQfile.append(sbFilter);

			

				// live data
				List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listsbSqlLive.size() > 0) {
					for (int i = 0; i < listsbSqlLive.size(); i++) {
						Object[] ob = (Object[]) listsbSqlLive.get(i);
						clsCounterDtlBean obj = new clsCounterDtlBean();
						obj.setStrCounterCode(ob[0].toString());
						obj.setStrCounterName(ob[1].toString());
						obj.setStrSubGroupCode(ob[2].toString());
						obj.setStrSubGroupName(ob[3].toString());
						obj.setDblRate(Double.parseDouble(ob[4].toString()));
						obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
						obj.setDblAmount(Double.parseDouble(ob[6].toString()));

						listOfSubGroupWiseCounterData.add(obj);
					}
				}

				// QData
				List listsbSqlQfile = objBaseService.funGetList(sbSqlQfile, "sql");
				if (listsbSqlQfile.size() > 0) {
					for (int i = 0; i < listsbSqlQfile.size(); i++) {
						Object[] ob = (Object[]) listsbSqlQfile.get(i);
						clsCounterDtlBean obj = new clsCounterDtlBean();
						obj.setStrCounterCode(ob[0].toString());
						obj.setStrCounterName(ob[1].toString());
						obj.setStrSubGroupCode(ob[2].toString());
						obj.setStrSubGroupName(ob[3].toString());
						obj.setDblRate(Double.parseDouble(ob[4].toString()));
						obj.setDblQuantity(Double.parseDouble(ob[5].toString()));
						obj.setDblAmount(Double.parseDouble(ob[6].toString()));

						listOfSubGroupWiseCounterData.add(obj);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		hmCounterWiseSales.put("listOfSubGroupWiseCounterData", listOfSubGroupWiseCounterData);
		hmCounterWiseSales.put("listOfCounterWiseGroupWiseData", listOfCounterWiseGroupWiseData);
		hmCounterWiseSales.put("listOfCounterWiseMenuHeadWiseData", listOfCounterWiseMenuHeadWiseData);
		return jOBjRet;
	}
	
	
}
