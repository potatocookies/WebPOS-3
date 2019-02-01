package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
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
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;

@Controller
public class clsPOSItemWiseReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	intfBaseService objBaseService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	
	Map<String, String> hmPOSData = new HashMap<String,String>();

	@RequestMapping(value = "/frmPOSItemWiseReport", method = RequestMethod.GET)
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
		poslist.add("All");
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

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSItemWiseReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSItemWiseReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSItemWiseSales", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptitemWiseSalesReport.jrxml");
			StringBuilder sql = new StringBuilder();
			String type = objBean.getStrDocType();
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
			String shiftNo = "1";
			
			String strShiftNo = "ALL",enableShiftYN="N";	
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, posCode, "gEnableShiftYN");
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
				{
					strShiftNo=objBean.getStrShiftCode();
					enableShiftYN=objSetupParameter.get("gEnableShiftYN").toString();
				}
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", strShiftNo);
			hm.put("shiftCode", strShiftNo);
			
			String printComplimentaryYN = objBean.getStrType();
			Map<String, clsPOSBillItemDtlBean> mapItemdtl = new HashMap<>();
			
			String taxCalculation = "Forward";
			sql.append("select b.strTaxCalculation "
			    + "from tblbilltaxdtl a,tbltaxhd b "
			    + "where a.strTaxCode=b.strTaxCode "
			    + "and date(a.dteBillDate) between  '" + fromDate + "' AND '" + toDate + "'  "
			    + "group by b.strTaxCalculation ");
		    List list = objBaseService.funGetList(sql, "sql");
		    if (list!=null && list.size()>0)
		    {
			  taxCalculation = (String) list.get(0);
		    }
		    else
		    {
			    sql.setLength(0);
			    sql.append("select b.strTaxCalculation "
					+ "from tblqbilltaxdtl a,tbltaxhd b "
					+ "where a.strTaxCode=b.strTaxCode "
					+ "and date(a.dteBillDate) between  '" + fromDate + "' AND '" + toDate + "'  "
					+ "group by b.strTaxCalculation ");
			    list = objBaseService.funGetList(sql, "sql");
			    if (list!=null && list.size()>0)
				{
			    	taxCalculation = (String) list.get(0);
				}
		    }

			List listSqlLive = objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"Live",strUserCode);
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }

					}
					else
					{
						clsPOSBillItemDtlBean obj = new clsPOSBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }

						mapItemdtl.put(itemCode, obj);
					}
				}
			}

			listSqlLive = objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"QFile",strUserCode); 
					
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
					}
					else
					{
						clsPOSBillItemDtlBean obj = new clsPOSBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
						mapItemdtl.put(itemCode, obj);
					}
				}
			}

			listSqlLive =  objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"ModLive",strUserCode);
			
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
					}
					else
					{
						clsPOSBillItemDtlBean obj = new clsPOSBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
						mapItemdtl.put(itemCode, obj);
					}
				}
			}
			
			listSqlLive = objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"ModQFile",strUserCode); 
			
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
					}
					else
					{
						clsPOSBillItemDtlBean obj = new clsPOSBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);
						if (taxCalculation.equalsIgnoreCase("Forward"))
					     {
						   obj.setDblGrandTotal((obj.getDblSubTotal() + obj.getDblTaxAmt()));
					     }
					    else
					     {
						   obj.setDblGrandTotal(obj.getDblSubTotal());
					     }
						mapItemdtl.put(itemCode, obj);
					}
				}
			}
			
			
			
			 double roundOff = 0.00;
			 String roundOffAmount = "sum(a.dblRoundOff)dblRoundOff";
			 StringBuilder sqlRoundOff = new StringBuilder("select sum(b.dblRoundOff) "
				    + "from "
			    + "(select " + roundOffAmount + " "
			    + "from tblbillhd a "
			    + "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
			 if (!posCode.equalsIgnoreCase("All"))
			    {
				sqlRoundOff.append("and a.strPOSCode='" + posCode + "' ");
			    }
			 if (!shiftNo.equalsIgnoreCase("All"))
			    {
				sqlRoundOff.append("and a.intShiftCode='" + shiftNo + "'  ");
			    }
			    sqlRoundOff.append("union  "
				    + "select " + roundOffAmount + " "
				    + "from tblqbillhd a "
				    + "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
			 if (!posCode.equalsIgnoreCase("All"))
			    {
				sqlRoundOff.append("and a.strPOSCode='" + posCode + "' ");
			    }
			 if (!shiftNo.equalsIgnoreCase("All"))
			    {
				sqlRoundOff.append("and a.intShiftCode='" + shiftNo + "'  ");
			    }
			    sqlRoundOff.append(") b ");
			    
		    list = objBaseService.funGetList(sqlRoundOff, "sql");
		    if (list!=null && list.size()>0)
			{
		    	for(int cnt=0;cnt<list.size();cnt++)
		    	{
		    		Object obj=list.get(cnt);
		    		roundOff=new BigDecimal(obj.toString()).doubleValue() ;
		    	}
			}
			

		    hm.put("RoundOff", roundOff);
			
			

			/**
			 * substract compli qty
			 */
			if (printComplimentaryYN.equalsIgnoreCase("No"))
			{
				hm.put("Note", "Note:Report does not include complimentary quantities.");
				
				listSqlLive = objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"LiveCompli",strUserCode); 
				
				if (listSqlLive.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++)
					{
						Object[] objArr = (Object[]) listSqlLive.get(i);
						
						String itemCode = objArr[0].toString();
						double qty=Double.parseDouble(objArr[3].toString());
						if (mapItemdtl.containsKey(itemCode))
						{
							clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

							obj.setDblQuantity(obj.getDblQuantity() - qty);
						}
					}
				}
				
				listSqlLive = objReportService.funProcessItemWiseReport( posCode, fromDate, toDate, shiftNo, printComplimentaryYN,"QCompli",strUserCode); 
				
				if (listSqlLive.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++)
					{
						Object[] objArr = (Object[]) listSqlLive.get(i);
						
						String itemCode = objArr[0].toString();
						double qty=Double.parseDouble(objArr[3].toString());
						if (mapItemdtl.containsKey(itemCode))
						{
							clsPOSBillItemDtlBean obj = mapItemdtl.get(itemCode);

							obj.setDblQuantity(obj.getDblQuantity() - qty);
						}
					}
				}
						
			}
			else
			{
				hm.put("Note", "Note:Report contains complimentary quantities.");
			}

			Comparator<clsPOSBillItemDtlBean> itemCodeComparator = new Comparator<clsPOSBillItemDtlBean>()
			{

				@Override
				public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2)
				{
					return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
				}
			};

			List<clsPOSBillItemDtlBean> listOfItemData = new ArrayList<clsPOSBillItemDtlBean>();
			for (clsPOSBillItemDtlBean objItemDtlBean : mapItemdtl.values())
			{
				listOfItemData.add(objItemDtlBean);
			}

			Collections.sort(listOfItemData, itemCodeComparator);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfItemData);
			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
			jprintlist.add(print);
			
			String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
			String extension=".pdf";
			if (!objBean.getStrDocType().equals("PDF"))
         	{
				objBean.setStrDocType("EXCEL");
				extension=".xls";
         	}	
			String fileName = "ItemWiseSalesReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
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
					resp.setHeader("Content-Disposition", "inline;filename=ItemWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=ItemWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
					exporter.exportReport();
//					servletOutputStream.flush();
//					servletOutputStream.close();
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
