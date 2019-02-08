

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;


import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.bean.clsPOSRevenueHeadWiseSalesReportBean;
import com.sanguine.webpos.bean.clsRevenueHeadWiseSalesReportBean;
import com.sanguine.webpos.comparator.clsRevenueHeadComparator;
import com.sanguine.webpos.sevice.clsPOSMasterService;



@Controller
public class clsPOSRevenueHeadSalesReportController {


	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	intfBaseService objBaseService;

	Map map=new HashMap();

	@RequestMapping(value = "/frmPOSRevenueHeadWiseItemSalesReport", method = RequestMethod.GET)
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
		model.put("posList",poslist);

		List revenueHeadList = new ArrayList<String>();
		revenueHeadList.add("ALL");

		List listRevenue =objMasterService.funFillAllRevenuHeadCombo();
		if(null!=listRevenue)
		{
			for(int i =0; i<listRevenue.size();i++)
			{
				String revenueHead = (String) listRevenue.get(i);
				revenueHeadList.add(revenueHead);
			}
		}
		model.put("revenueHeadList",revenueHeadList);

		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSRevenueHeadWiseItemSalesReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSRevenueHeadWiseItemSalesReport","command", new clsPOSReportBean());
		}else {
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSRevenueHeadWiseItemSalesReport", method = RequestMethod.POST)	
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req
			,String source)
	{

		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String companyName=req.getSession().getAttribute("gCompanyName").toString();

		List listLive = null;
		List listQFile = null;
		List listModLive = null;
		List listModQFile =null;
		try
		{
			String strReportType =objBean.getStrReportType();
			String reportName;
			if(strReportType.equalsIgnoreCase("Summary"))
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptRevenueHeadWiseSummaryReport.jrxml");
			else
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptRevenueHeadWiseReport.jrxml");
			String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");

			List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
			String strFromdate=objBean.getFromDate().split("-")[2]+"-"+objBean.getFromDate().split("-")[1]+"-"+objBean.getFromDate().split("-")[0];
			String strToDate=objBean.getToDate().split("-")[2]+"-"+objBean.getToDate().split("-")[1]+"-"+objBean.getToDate().split("-")[0]; 
			String strRevenueHead=objBean.getStrRevenueHead();
			String strPOSName =objBean.getStrPOSName();
			String shiftNo="1";
			String posCode= "ALL";
			if(map.containsKey(strPOSName))
			{
				posCode=(String) map.get(strPOSName);
			}


			Map jObjFillter = new HashMap();
			jObjFillter.put("strFromdate", strFromdate);
			jObjFillter.put("strToDate", strToDate);
			jObjFillter.put("posCode", posCode);
			jObjFillter.put("strShiftNo","1");
			jObjFillter.put("revenueHead",strRevenueHead);
			jObjFillter.put("reportType",strReportType);
			jObjFillter.put("userCode", userCode);


			Map jObj = funRevenueHeadWiseItemSalesReportDtl(strFromdate,strToDate,posCode, shiftNo,strRevenueHead, strReportType, userCode);
			List<clsRevenueHeadWiseSalesReportBean> list =new ArrayList<clsRevenueHeadWiseSalesReportBean>();
			List jarr = (List) jObj.get("jArr");
			for(int i=0;i<jarr.size();i++)
			{
				Map jObjtemp =(Map) jarr.get(i);

				clsRevenueHeadWiseSalesReportBean objClsGroupWaiseSalesBean=new clsRevenueHeadWiseSalesReportBean();
				objClsGroupWaiseSalesBean.setStrRevenueHead(jObjtemp.get("strRevenueHead").toString());
				objClsGroupWaiseSalesBean.setStrMenuName(jObjtemp.get("strMenuName").toString());
				objClsGroupWaiseSalesBean.setStrItemName(jObjtemp.get("strItemName").toString());
				objClsGroupWaiseSalesBean.setDblAmount(Double.parseDouble(jObjtemp.get("dblAmount").toString()));
				objClsGroupWaiseSalesBean.setDblQuantity(Double.parseDouble(jObjtemp.get("dblQuantity").toString()));
				objClsGroupWaiseSalesBean.setStrItemCode(jObjtemp.get("strItemCode").toString());

				list.add(objClsGroupWaiseSalesBean);
			}
			Comparator<clsRevenueHeadWiseSalesReportBean> groupComparator = new Comparator<clsRevenueHeadWiseSalesReportBean>()
					{
				@Override
				public int compare(clsRevenueHeadWiseSalesReportBean o1, clsRevenueHeadWiseSalesReportBean o2)
				{
					return o1.getStrRevenueHead().compareToIgnoreCase(o2.getStrRevenueHead());
				}
					};

					Collections.sort(list, new clsRevenueHeadComparator(groupComparator));


					HashMap hm = new HashMap();
					hm.put("posCode", posCode);
					hm.put("posName", strPOSName);
					hm.put("imagePath", imagePath);
					hm.put("clientName", companyName);
					hm.put("fromDateToDisplay", strFromdate);
					hm.put("toDateToDisplay", strToDate);
					hm.put("shiftNo", "1");
					hm.put("userName", userCode);

					JasperDesign jd = JRXmlLoader.load(reportName);
					JasperReport jr = JasperCompileManager.compileReport(jd);

					JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
					JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
					jprintlist.add(print);
					
					String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
					String extension=".pdf";
					if (!objBean.getStrDocType().equals("PDF"))
					{
						objBean.setStrDocType("EXCEL");
						extension=".xls";
					}	
					String fileName = "RevenueHeadWiseSalesReport_"+ strFromdate + "_To_" + strToDate + "_" + userCode + extension;
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
							resp.setHeader("Content-Disposition", "inline;filename=RevenueHeadWiseSalesReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".pdf");
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
							resp.setHeader("Content-Disposition", "inline;filename=RevenueHeadWiseSalesReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".xls");
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
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		System.out.println("Hi");
	}

	

	public Map funRevenueHeadWiseItemSalesReportDtl(String fromDate,String toDate, String strPOSCode, String strShiftNo,
			String revenueHead, String reportType, String strUserCode) 
	{
		List listRet = new ArrayList();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sbSqlModFilters = new StringBuilder();
		StringBuilder sbSqlModLive = new StringBuilder();
		StringBuilder sbSqlQModFile = new StringBuilder();
		List jArr = new JSONArray();
		Map jOBjRet = new HashMap();

		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);
		sbSqlModFilters.setLength(0);
		sbSqlModLive.setLength(0);
		sbSqlQModFile.setLength(0);

		try {
			sbSqlQFile
			.append("select e.strRevenueHead,d.strMenuName,a.strItemName, SUM(a.dblQuantity), SUM(a.dblAmount),a.strItemCode "
					+ "from tblqbilldtl a,tblqbillhd b ,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
					+ "where a.strBillNo=b.strBillNo  "
					+ "and a.strItemCode=c.strItemCode "
					+ "and c.strMenuCode=d.strMenuCode "
					+ "and a.strItemCode=e.strItemCode "
					+ "and c.strPosCode=if(c.strPosCode='All','All',b.strPOSCode) "
					+ "AND (b.strAreaCode=c.strAreaCode or c.strAreaCode='A001') ");
			sbSqlLive
			.append("select e.strRevenueHead,d.strMenuName,a.strItemName, SUM(a.dblQuantity), SUM(a.dblAmount),a.strItemCode "
					+ "from tblbilldtl a,tblbillhd b ,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
					+ "where a.strBillNo=b.strBillNo  "
					+ "and a.strItemCode=c.strItemCode "
					+ "and c.strMenuCode=d.strMenuCode "
					+ "and a.strItemCode=e.strItemCode "
					+ "and c.strPosCode=if(c.strPosCode='All','All',b.strPOSCode) "
					+ "AND (b.strAreaCode=c.strAreaCode or c.strAreaCode='A001') ");

			sbSqlFilters.append(" AND date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append("and b.strPosCode='" + strPOSCode + "' ");

			}
			if (!revenueHead.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" and e.strRevenueHead='" + revenueHead
						+ "' ");

			}
			sbSqlFilters.append(" and b.intShiftCode = '" + strShiftNo + "' ");
			
			if (reportType.equalsIgnoreCase("Summary")) {
				sbSqlFilters.append(" group by e.strRevenueHead,d.strMenuCode "
						+ " order by e.strRevenueHead,d.strMenuCode ");
			}

			if (reportType.equalsIgnoreCase("Details")) {
				sbSqlFilters
				.append(" group by e.strRevenueHead,d.strMenuCode,a.strItemName"
						+ " order by e.strRevenueHead,d.strMenuCode,a.strItemName");
			}

			sbSqlModLive
			.append("SELECT e.strRevenueHead,d.strMenuName,a.strModifierName, SUM(a.dblQuantity), SUM(a.dblAmount),a.strItemCode "
					+ "FROM tblbillmodifierdtl a,tblbillhd b,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
					+ "WHERE a.strBillNo=b.strBillNo  "
					+ "AND left(a.strItemCode,7)=c.strItemCode  "
					+ "AND c.strMenuCode=d.strMenuCode  "
					+ "AND left(a.strItemCode,7)=e.strItemCode  "
					+ "AND c.strPosCode= IF(c.strPosCode='All','All',b.strPOSCode)  "
					+ "AND (b.strAreaCode=c.strAreaCode or c.strAreaCode='A001')  "
					+ "AND DATE(b.dteBillDate)  BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

			sbSqlQModFile
			.append("SELECT e.strRevenueHead,d.strMenuName,a.strModifierName, SUM(a.dblQuantity), SUM(a.dblAmount),a.strItemCode "
					+ "FROM tblqbillmodifierdtl a,tblqbillhd b,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
					+ "WHERE a.strBillNo=b.strBillNo  "
					+ "AND left(a.strItemCode,7)=c.strItemCode  "
					+ "AND c.strMenuCode=d.strMenuCode  "
					+ "AND left(a.strItemCode,7)=e.strItemCode  "
					+ "AND c.strPosCode= IF(c.strPosCode='All','All',b.strPOSCode)  "
					+ "AND (b.strAreaCode=c.strAreaCode or c.strAreaCode='A001')  "
					+ "AND DATE(b.dteBillDate)  BETWEEN '"
					+ fromDate
					+ "' AND '" + toDate + "' ");


			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSqlModFilters.append("and b.strPosCode='" + strPOSCode + "' ");

			}
			if (!revenueHead.equalsIgnoreCase("All")) {
				sbSqlModFilters.append(" and e.strRevenueHead='" + revenueHead
						+ "' ");

			}

			sbSqlModFilters.append(" and b.intShiftCode = '" + strShiftNo + "' ");
			
			if (reportType.equalsIgnoreCase("Summary")) {
				sbSqlModFilters.append("GROUP BY e.strRevenueHead,d.strMenuCode "
						+ "ORDER BY e.strRevenueHead,d.strMenuCode ");
			}

			if (reportType.equalsIgnoreCase("Details")) {
				sbSqlModFilters
				.append("GROUP BY e.strRevenueHead,d.strMenuCode,a.strModifierName "
						+ "ORDER BY e.strRevenueHead,d.strMenuCode,a.strModifierName ");
			}

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sbSqlModLive.append(sbSqlModFilters);
			sbSqlQModFile.append(sbSqlModFilters);

	        List listsbSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listsbSqlLive.size() > 0) {

				for (int i = 0; i < listsbSqlLive.size(); i++) {
					Object[] obj = (Object[]) listsbSqlLive.get(i);
					Map jObj = new HashMap();
					jObj.put("strRevenueHead", obj[0].toString());
					jObj.put("strMenuName", obj[1].toString());
					jObj.put("strItemName", obj[2].toString());
					jObj.put("dblQuantity", obj[3].toString());
					jObj.put("dblAmount", obj[4].toString());
					jObj.put("strItemCode", obj[5].toString());

					jArr.add(jObj);
				}
				// jOBjRet.put("jArr", jArr);
			}
			// listRet.add(listSqlLive);
			List listsbSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listsbSqlQFile.size() > 0) {

				for (int i = 0; i < listsbSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listsbSqlQFile.get(i);
					Map jObj = new HashMap();
					jObj.put("strRevenueHead", obj[0].toString());
					jObj.put("strMenuName", obj[1].toString());
					jObj.put("strItemName", obj[2].toString());
					jObj.put("dblQuantity", obj[3].toString());
					jObj.put("dblAmount", obj[4].toString());
					jObj.put("strItemCode", obj[5].toString());

					jArr.add(jObj);
				}
				// jOBjRet.put("jArr", jArr);
			}
			// listRet.add(listSqlQFile);
			List listsbSqlModLive = objBaseService.funGetList(sbSqlModLive, "sql");
			if (listsbSqlModLive.size() > 0) {

				for (int i = 0; i < listsbSqlModLive.size(); i++) {
					Object[] obj = (Object[]) listsbSqlModLive.get(i);
					JSONObject jObj = new JSONObject();
					jObj.put("strRevenueHead", obj[0].toString());
					jObj.put("strMenuName", obj[1].toString());
					jObj.put("strItemName", obj[2].toString());
					jObj.put("dblQuantity", obj[3].toString());
					jObj.put("dblAmount", obj[4].toString());
					jObj.put("strItemCode", obj[5].toString());

					jArr.add(jObj);
				}
				// jOBjRet.put("jArr", jArr);
			}
			// listRet.add(listSqlModLive);
			List listsbSqlQModFile = objBaseService.funGetList(sbSqlQModFile, "sql");		
			if (listsbSqlQModFile.size() > 0) {

				for (int i = 0; i < listsbSqlQModFile.size(); i++) {
					Object[] obj = (Object[]) listsbSqlQModFile.get(i);
					JSONObject jObj = new JSONObject();
					jObj.put("strRevenueHead", obj[0].toString());
					jObj.put("strMenuName", obj[1].toString());
					jObj.put("strItemName", obj[2].toString());
					jObj.put("dblQuantity", obj[3].toString());
					jObj.put("dblAmount", obj[4].toString());
					jObj.put("strItemCode", obj[5].toString());

					jArr.add(jObj);
				}
				// jOBjRet.put("jArr", jArr);
			}

			// listRet.add(listSqlModQFile);
			jOBjRet.put("jArr", jArr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jOBjRet;

	}
}
