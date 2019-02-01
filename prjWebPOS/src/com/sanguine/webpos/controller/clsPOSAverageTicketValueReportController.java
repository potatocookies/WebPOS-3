package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


@Controller
public class clsPOSAverageTicketValueReportController
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

	HashMap hmPOSData = new HashMap<String, String>();

	@RequestMapping(value = "/frmPOSATV", method = RequestMethod.GET)
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
				hmPOSData.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList", poslist);
				
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSATV_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSATV", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/frmPOSATV", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptAVT1.jrxml");
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
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
			String shiftNo = "ALL";
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
			{
				shiftNo=objBean.getStrShiftCode();
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", shiftNo);

			funInsertData(fromDate,toDate,posCode,shiftNo,objSetupParameter.get("gEnableShiftYN").toString());
					    JasperDesign jd = JRXmlLoader.load(reportName);
    		JasperReport jr = JasperCompileManager.compileReport(jd);
            List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
            JasperPrint print = JasperFillManager.fillReport(jr, hm,new clsGlobalFunctions().funGetConnection(req));
            jprintlist.add(print);
            String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
			String extension=".pdf";
			if (!objBean.getStrDocType().equals("PDF"))
			{
				objBean.setStrDocType("EXCEL");
				extension=".xls";
			}	
			String fileName = "AvgTicketValueReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
					resp.setHeader("Content-Disposition", "inline;filename=AvgTicketValueReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=AvgTicketValueReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
	
	public void funInsertData(String fromDate,String toDate,String posCode,String shiftNo,String enableShiftYN) throws Exception
	{
		StringBuilder filter = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		
		if (!posCode.equalsIgnoreCase("All"))
		{
		    filter.append(" and b.strPOSCode='" + posCode + "' ");
		}
		if (enableShiftYN.equalsIgnoreCase("Y"))
		{
		    if (enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
		    {
			filter.append(" and b.intShiftCode ='" + shiftNo.toString() + "' ");
		    }
		}
		sb.setLength(0);
		sb.append("Insert into tblatvreport(strPosCode,strPosName,dteDate,dblDiningAmt)"
			+ " select * from ("
			+ " select  b.strPOSCode,c.strPOSName,b.dteBillDate,sum(dblQuantity) as ItemQty "
			+ " from tblBillDtl a, tblBillHd b,tblposmaster c"
			+ " Where b.strOperationType='DineIn' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL "
			+ " select  b.strPOSCode,c.strPOSName,b.dteBillDate,sum(dblQuantity) as ItemQty "
			+ " from tblqBillDtl a, tblqBillHd b,tblposmaster c"
			+ " Where b.strOperationType='DineIn' and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" ) d");
		objBaseService.funExecuteUpdate(sb.toString(),"sql");
		//Dine In  
		sb.setLength(0);
		sb.append("select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblBillHd b,tblPOSMaster c "
			+ " Where b.strOperationType='DineIn' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL ");
		sb.append("select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblqBillHd b,tblPOSMaster c "
			+ " Where b.strOperationType='DineIn' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");

		List list = objBaseService.funGetList(sb,"sql");
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			Object[] obj = (Object[]) list.get(i);	
		    sb.setLength(0);
		    sb.append("update tblatvreport set dblDiningNoBill='" + obj[2].toString() + "' ,strPOSName='" + obj[3].toString() + "' "
			    + "where dteDate = '" + obj[0].toString() + "'  and strPOSCode ='" + obj[1].toString() + "'");
		    objBaseService.funExecuteUpdate(sb.toString(),"sql");
			}
		}
		//Home Delivery
		sb.setLength(0);
		sb.append("select  b.strPOSCode,b.dteBillDate,  sum(dblQuantity) as ItemQty "
			+ " from tblBillDtl a, tblBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='HomeDelivery' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL ");
		sb.append("select  b.strPOSCode,b.dteBillDate,  sum(dblQuantity) as ItemQty "
			+ " from tblqBillDtl a, tblqBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='HomeDelivery' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");

		list = objBaseService.funGetList(sb,"sql");
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{	
			Object[] obj = (Object[]) list.get(i);	
		    sb.setLength(0);
		    sb.append("update tblatvreport set dblHDAmt='" + obj[2].toString() + "' "
			    + " where dteDate = '" + obj[1].toString() + "'  and strPOSCode ='" + obj[0].toString() + "'");
		    objBaseService.funExecuteUpdate(sb.toString(),"sql");
			}
		}
		//no of home delivery bills
		sb.setLength(0);
		sb.append("select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblBillHd b,tblPOSMaster c "
			+ " Where b.strOperationType='HomeDelivery' "
			+ " and b.strPOSCode=c.strPOSCode"
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL ");
		sb.append(" select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblqBillHd b ,tblPOSMaster c"
			+ " Where b.strOperationType='HomeDelivery'"
			+ " and b.strPOSCode=c.strPOSCode"
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");

		list = objBaseService.funGetList(sb,"sql");
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			Object[] obj = (Object[]) list.get(i);	
		    sb.setLength(0);
		    sb.append("update tblatvreport set dblHDNoBill='" + obj[2].toString() + "' ,strPosName='" + obj[3].toString() + "' "
			    + " where dteDate = '" + obj[0].toString() + "'  and strPOSCode ='" + obj[1].toString() + "'");
		    objBaseService.funExecuteUpdate(sb.toString(),"sql");
			}
		}
		//Take Away
		sb.setLength(0);
		sb.append("select  b.strPOSCode,c.strPOSName,b.dteBillDate,  sum(dblQuantity) as ItemQty "
			+ " from tblBillDtl a, tblBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='TakeAway' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL ");
		sb.append("select  b.strPOSCode,c.strPOSName,b.dteBillDate,  sum(dblQuantity) as ItemQty "
			+ " from tblqBillDtl a, tblqBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='TakeAway' "
			+ " and  Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strBillNo = b.strBillNo "
			+ " and b.strPOSCode=c.strPOSCode");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");

		list = objBaseService.funGetList(sb,"sql");
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			Object[] obj = (Object[]) list.get(i);	
		    sb.setLength(0);
		    sb.append("update tblatvreport set dblTAAmt='" + obj[3].toString() + "',strPosName='" + obj[1].toString() + "' "
			    + " where dteDate = '" + obj[2].toString() + "'  and strPOSCode ='" + obj[0].toString() + "'");
		    objBaseService.funExecuteUpdate(sb.toString(),"sql");
			}
		}
		//no  of take away bills
		sb.setLength(0);
		sb.append("select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='TakeAway' "
			+ " and Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");
		sb.append(" UNION ALL ");
		sb.append("select b.dteBillDate,  b.strPOSCode, Count(*) as NoOfBills,c.strPOSName "
			+ " from tblqBillHd b,tblPOSMaster c"
			+ " Where b.strOperationType='TakeAway' "
			+ " and Date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and b.strPOSCode=c.strPOSCode ");
		sb.append(filter + " group by date(b.dteBillDate),c.strPOSCode");

		list = objBaseService.funGetList(sb,"sql");
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			Object[] obj = (Object[]) list.get(i);	
		    sb.setLength(0);
		    sb.append("update tblatvreport set dblTANoBill='" + obj[2].toString() + "' ,strPosName='" + obj[3].toString() + "'"
			    + "where dteDate = '" + obj[0].toString() + "'  and strPOSCode ='" + obj[1].toString() + "'");
		   objBaseService.funExecuteUpdate(sb.toString(),"sql");
			}
		}
		sb.setLength(0);
		sb.append("update tblatvreport set dblDiningAvg=  dblDiningAmt/dblDiningNoBill, dblHDAvg= dblHDAmt/dblHDNoBill, dblTAAvg= dblTAAmt/dblTANoBill");
		 objBaseService.funExecuteUpdate(sb.toString(),"sql");
	}	
		
}
