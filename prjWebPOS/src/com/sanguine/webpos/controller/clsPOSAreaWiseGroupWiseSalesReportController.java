package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import com.POSReport.controller.clsGroupSubGroupItemBean;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSBillComparator;
import com.sanguine.webpos.comparator.clsPOSGroupSubGroupWiseSalesComparator;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


@Controller
public class clsPOSAreaWiseGroupWiseSalesReportController
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
	private clsSetupService objSetupService;
	
	@Autowired
	intfBaseService objBaseService;

	

	@RequestMapping(value = "/frmAreaWiseGroupWiseSales", method = RequestMethod.GET)
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
		HashMap hmPOSData = new HashMap<String, String>();
		hmPOSData.put("ALL", "ALL");
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				hmPOSData.put( obj[0].toString(), obj[1].toString());
			}
		}
		model.put("posList", hmPOSData);
		
		Map hmAreaData = new TreeMap<>();
		List listOfArea = objMasterService.funGetAllAreaForMaster(strClientCode);
		if(listOfArea!=null)
		{
			for(int i =0 ;i<listOfArea.size();i++)
			{
				clsAreaMasterModel objModel = (clsAreaMasterModel) listOfArea.get(i);
				hmAreaData.put( objModel.getStrAreaCode(), objModel.getStrAreaName());
			}
		}
		model.put("areaList", hmAreaData);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmAreaWiseGroupWiseSales_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmAreaWiseGroupWiseSales", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSAreaWiseGroupWiseReport", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptAreaWiseGroupWiseSalesReport.jrxml");
			String strClientCode = req.getSession().getAttribute("gClientCode").toString();
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			posCode = objBean.getStrPOSName();
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = hm.get("shiftNo").toString();
			String areaName = objBean.getStrAreaCode();
			List<clsPOSGroupSubGroupWiseSales> listOfGroupWise = new ArrayList<clsPOSGroupSubGroupWiseSales>();
			listOfGroupWise = funAreaWiseGrouWiseSales(fromDate,toDate,strUserCode,strPOSCode,shiftNo,strClientCode,areaName);
			
			
			
			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfGroupWise);
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
					resp.setHeader("Content-Disposition", "inline;filename=AreaWiseGroupWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=AreaWiseGroupWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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

	public List funAreaWiseGrouWiseSales(String fromDate,String toDate,String strUserCode,String strPOSCode,String shiftNo,String strClientCode,String areaName) throws Exception
	{
		StringBuilder sbSqlLive = new StringBuilder();
	    StringBuilder sbSqlQFile = new StringBuilder();
	    StringBuilder sbSqlFilters = new StringBuilder();
	    Map mapPOSDtlForGroupSubGroup = new LinkedHashMap<>();
	    List<clsGroupSubGroupItemBean> listOfGroupWiseSales = new ArrayList<clsGroupSubGroupItemBean>();
	    List<clsPOSGroupSubGroupWiseSales> listOfGroupWise = new ArrayList<clsPOSGroupSubGroupWiseSales>();
	    sbSqlLive.setLength(0);
	    sbSqlQFile.setLength(0);
	    sbSqlFilters.setLength(0);
	    
	    Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, strPOSCode, "gEnableShiftYN");

	    sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
		    + " ,sum( b.dblAmount)-sum(b.dblDiscountAmt) "
		    + " ,f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
		    + " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount),a.strAreaCode,g.strAreaName  "
		    + " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
		    + " ,tblitemmaster e,tblposmaster f,tblareamaster g "
		    + " where a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + " and a.strPOSCode=f.strPOSCode  "
		    + " and a.strClientCode=b.strClientCode "
		    + " and b.strItemCode=e.strItemCode "
		    + " and c.strGroupCode=d.strGroupCode "
		    + " and d.strSubGroupCode=e.strSubGroupCode "
		    + " and a.strAreaCode=g.strAreaCode ");

	    sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
		    + " ,sum( b.dblAmount)-sum(b.dblDiscountAmt) "
		    + " ,f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
		    + " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount),a.strAreaCode,g.strAreaName  "
		    + " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
		    + " ,tblitemmaster e,tblposmaster f,tblareamaster g "
		    + " where a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + " and a.strPOSCode=f.strPOSCode  "
		    + " and a.strClientCode=b.strClientCode   "
		    + " and b.strItemCode=e.strItemCode "
		    + " and c.strGroupCode=d.strGroupCode "
		    + " and d.strSubGroupCode=e.strSubGroupCode "
		    + " and a.strAreaCode=g.strAreaCode ");

	    StringBuilder sqlModLive = new StringBuilder();
	    sqlModLive.append("select c.strGroupCode,c.strGroupName"
		    + " ,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
		    + " ,'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
		    + " sum(b.dblAmount)-sum(b.dblDiscAmt),a.strAreaCode,g.strAreaName  "
		    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
		    + " ,tblsubgrouphd e,tblgrouphd c,tblareamaster g "
		    + " where a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + " and a.strPOSCode=f.strPosCode  "
		    + " and a.strClientCode=b.strClientCode  "
		    + " and LEFT(b.strItemCode,7)=d.strItemCode "
		    + " and d.strSubGroupCode=e.strSubGroupCode "
		    + " and e.strGroupCode=c.strGroupCode "
		    + " and a.strAreaCode=g.strAreaCode "
		    + " and b.dblamount>0 ");

	    StringBuilder sqlModQFile = new StringBuilder();
	    sqlModQFile.append("select c.strGroupCode,c.strGroupName"
		    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
		    + ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
		    + " sum(b.dblAmount)-sum(b.dblDiscAmt),a.strAreaCode,g.strAreaName "
		    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
		    + ",tblsubgrouphd e,tblgrouphd c,tblareamaster g "
		    + " where a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + " and a.strPOSCode=f.strPosCode   "
		    + " and a.strClientCode=b.strClientCode   "
		    + " and LEFT(b.strItemCode,7)=d.strItemCode "
		    + " and d.strSubGroupCode=e.strSubGroupCode "
		    + " and e.strGroupCode=c.strGroupCode "
		    + " and a.strAreaCode=g.strAreaCode "
		    + " and b.dblamount>0 ");

	    sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	    if (!strPOSCode.equals("All"))
	    {
		sbSqlFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
	    }

	    if (objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
	    {
		if (objSetupParameter.get("gEnableShiftYN").toString().equals("Y") && (!shiftNo.equalsIgnoreCase("All")))
		{
		    sbSqlFilters.append(" and a.intShiftCode = '" + shiftNo + "' ");
		}
	    }

	    if (!areaName.equalsIgnoreCase("All"))
	    {
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("select a.strAreaCode,a.strAreaName "
			+ "from tblareamaster a  "
			+ "where a.strAreaName='" + areaName + "' ");
		List listAreaCode = objBaseService.funGetList(strBuilder, "sql");
		if (listAreaCode.size()>0)
		{
		    sbSqlFilters.append("AND a.strAreaCode='" + listAreaCode.get(0) + "' ");
		}
		
	    }
	    sbSqlFilters.append(" GROUP BY a.strAreaCode,c.strGroupCode, c.strGroupName, a.strPoscode "
		    + " order BY g.strAreaName,c.strGroupName ");

	    sbSqlLive.append(sbSqlFilters);
	    sbSqlQFile.append(sbSqlFilters);

	    sqlModLive.append(" " + sbSqlFilters);
	    sqlModQFile.append( " " + sbSqlFilters);

	    Map<String, Map<String, clsPOSGroupSubGroupWiseSales>> mapAreaWiseGroupSale = new HashMap<>();

	    List listGroupWiseSales = objBaseService.funGetList(sbSqlLive,"sql");
	    if(listGroupWiseSales.size()>0)
	    {
	    	for(int i=0;i<listGroupWiseSales.size();i++)
	    	{	
	    		Object[] obj = (Object[]) listGroupWiseSales.get(i);
				String area = obj[12].toString();
				String group = obj[1].toString();
		
				if (mapAreaWiseGroupSale.containsKey(area))
				{
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = mapAreaWiseGroupSale.get(area);
				    if (mapGroup.containsKey(group))
				    {
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroup.get(group);
		
					objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
					objGroupCodeDtl.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[7].toString()));
					objGroupCodeDtl.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
					objGroupCodeDtl.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[3].toString()));
		
				    }
				    else
				    {
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
					objGroupCodeDtl.setStrAreaName(area);
		
					mapGroup.put(group, objGroupCodeDtl);
				    }
		
				}
				else
				{
				    clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
				    objGroupCodeDtl.setStrAreaName(area);
		
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = new HashMap<>();
				    mapGroup.put(group, objGroupCodeDtl);
		
				    mapAreaWiseGroupSale.put(area, mapGroup);
		
				}

	    	}
	    }
	    

	    listGroupWiseSales = objBaseService.funGetList(sqlModLive,"sql");
	    if(listGroupWiseSales.size()>0)
	    {
	    	for(int i=0;i<listGroupWiseSales.size();i++)
	    	{	
	    		Object[] obj = (Object[]) listGroupWiseSales.get(i);
	    		String area = obj[12].toString();
				String group = obj[1].toString();
		
				if (mapAreaWiseGroupSale.containsKey(area))
				{
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = mapAreaWiseGroupSale.get(area);
				    if (mapGroup.containsKey(group))
				    {
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroup.get(group);
		
					objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
					objGroupCodeDtl.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[7].toString()));
					objGroupCodeDtl.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
					objGroupCodeDtl.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[3].toString()));
		
				    }
				    else
				    {
				    	clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
						
					objGroupCodeDtl.setStrAreaName(area);
		
					mapGroup.put(group, objGroupCodeDtl);
				    }
		
				}
				else
				{
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
					
				    objGroupCodeDtl.setStrAreaName(area);
		
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = new HashMap<>();
				    mapGroup.put(group, objGroupCodeDtl);
		
				    mapAreaWiseGroupSale.put(area, mapGroup);
		
				}
	    	}

	    }
	    

	    listGroupWiseSales = objBaseService.funGetList(sbSqlQFile,"sql");
	    if(listGroupWiseSales.size()>0)
	    {
	    	for(int i=0;i<listGroupWiseSales.size();i++)
	    	{	
	    		Object[] obj = (Object[]) listGroupWiseSales.get(i);
	    		String area = obj[12].toString();
				String group = obj[1].toString();
		
				if (mapAreaWiseGroupSale.containsKey(area))
				{
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = mapAreaWiseGroupSale.get(area);
				    if (mapGroup.containsKey(group))
				    {
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroup.get(group);
		
					objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
					objGroupCodeDtl.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[7].toString()));
					objGroupCodeDtl.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
					objGroupCodeDtl.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[3].toString()));
		
				    }
				    else
				    {
				    clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
					objGroupCodeDtl.setStrAreaName(area);
		
					mapGroup.put(group, objGroupCodeDtl);
				    }
		
				}
				else
				{
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
				    objGroupCodeDtl.setStrAreaName(area);
		
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = new HashMap<>();
				    mapGroup.put(group, objGroupCodeDtl);
		
				    mapAreaWiseGroupSale.put(area, mapGroup);
		
				}
	    	}	

	    }
	   

	    listGroupWiseSales = objBaseService.funGetList(sqlModQFile,"sql");
	    while (listGroupWiseSales.size()>0)
	    {
	    	for(int i=0;i<listGroupWiseSales.size();i++)
	    	{	
	    		Object[] obj = (Object[]) listGroupWiseSales.get(i);
	    		String area = obj[12].toString();
				String group = obj[1].toString();
		
				if (mapAreaWiseGroupSale.containsKey(area))
				{
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = mapAreaWiseGroupSale.get(area);
				    if (mapGroup.containsKey(group))
				    {
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroup.get(group);
		
					objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
					objGroupCodeDtl.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[7].toString()));
					objGroupCodeDtl.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
					objGroupCodeDtl.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[3].toString()));
		
				    }
				    else
				    {
				    	clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
					objGroupCodeDtl.setStrAreaName(area);
		
					mapGroup.put(group, objGroupCodeDtl);
				    }
		
				}
				else
				{
					clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(obj[0].toString(), obj[1].toString(), obj[4].toString(), Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[7].toString()),Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[8].toString()), Double.parseDouble(obj[10].toString()));
				    objGroupCodeDtl.setStrAreaName(area);
		
				    Map<String, clsPOSGroupSubGroupWiseSales> mapGroup = new HashMap<>();
				    mapGroup.put(group, objGroupCodeDtl);
		
				    mapAreaWiseGroupSale.put(area, mapGroup);
		
				}
	    	}	

	    }
	   
	    Comparator<clsPOSGroupSubGroupWiseSales> areaNameComparator = new Comparator<clsPOSGroupSubGroupWiseSales>()
	    {

		@Override
		public int compare(clsPOSGroupSubGroupWiseSales o1, clsPOSGroupSubGroupWiseSales o2)
		{
		    return o1.getStrAreaName().compareToIgnoreCase(o2.getStrAreaName());
		}
	    };

	    Comparator<clsPOSGroupSubGroupWiseSales> groupNameComparator = new Comparator<clsPOSGroupSubGroupWiseSales>()
	    {

		@Override
		public int compare(clsPOSGroupSubGroupWiseSales o1, clsPOSGroupSubGroupWiseSales o2)
		{
		    return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
		}
	    };

	    listOfGroupWise.clear();
	    for (Map<String, clsPOSGroupSubGroupWiseSales> mapGroup : mapAreaWiseGroupSale.values())
	    {
		for (clsPOSGroupSubGroupWiseSales objBean : mapGroup.values())
		{
		    listOfGroupWise.add(objBean);
		}
	    }

	    Collections.sort(listOfGroupWise, new clsPOSGroupSubGroupWiseSalesComparator(areaNameComparator, groupNameComparator));
		
	    return listOfGroupWise;

	}

}
