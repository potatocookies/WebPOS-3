package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsPOSDayEndFlashBean;
import com.sanguine.webpos.bean.clsPOSItemModifierMasterBean;
import com.sanguine.webpos.bean.clsPOSTaxWaiseBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsExportToExcel;
import com.sanguine.webpos.util.clsPOSGroupWiseComparator;

@Controller
public class clsPOSDayEndFlashReportController {
	
	  @Autowired
		private clsGlobalFunctions objGlobalFunctions;
		
		 @Autowired
		 private ServletContext servletContext;
		 
		 @Autowired
		 private clsPOSMasterService objMasterService;
		 
		 @Autowired
		 private clsPOSReportService objReportService;
		 
		 @Autowired
		 private clsExportToExcel objExportToExcel;
		
		 Map map=new HashMap();
		
		@RequestMapping(value = "/frmPOSDayEndFlash", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
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
			List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
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
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSDayEndFlashReport_1","command", new clsPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSDayEndFlashReport","command", new clsPOSReportBean());
			}else {
				return null;
			}
		}

		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/processDayEndFlashReport1", method = RequestMethod.POST)	
		public ModelAndView funProcessDayEndFlashReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)throws Exception
		{
			List exportList=funGetReportData(req, objBean);
			return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
		}	
		
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/processDayEndFlashReport", method = RequestMethod.POST)	
		public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
		{
			List exportList=funGetReportData(req, objBean);
			objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
			return true;
		}	
		
		private List funGetReportData(HttpServletRequest req, clsPOSReportBean objBean) throws Exception
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String strPOSName =objBean.getStrPOSName();
			
		    String strFromdate=objBean.getFromDate();
			String strToDate=objBean.getToDate();
			Map resMap = new LinkedHashMap();
		
			resMap=funGetData(clientCode,strFromdate,strToDate,strPOSName);
		
			List exportList=new ArrayList();	
			
			String fileName="DayEndFlash"+strFromdate+"_To_"+strToDate;
		
			exportList.add(fileName);
						
			List list=(List)resMap.get("listcol");
		
			String[] headerList = new String[list.size()];
			for(int i = 0; i < list.size(); i++)
			{
				headerList[i]=(String)list.get(i);
			}
		
			exportList.add(headerList);
		
			List dataList=(List)resMap.get("List");
			List totalList=(List)resMap.get("totalList");
		
			dataList.add(totalList);
				
			exportList.add(dataList);
		
			return exportList;	
	}
		
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadDayEndFlash"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadBulItemkPricingMaster(HttpServletRequest req)
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	               
		    String strFromdate=req.getParameter("fromDate");
		 
			String strToDate=req.getParameter("toDate");
							
			String posName=req.getParameter("posName");
			
			resMap=funGetData(clientCode,strFromdate,strToDate,posName);
	        return resMap;
	    }

			
		@SuppressWarnings({ "unchecked" })
		private LinkedHashMap funGetData(String clientCode,String strFromdate,String strToDate,String strPOSName)
		{									
						  LinkedHashMap resMap = new LinkedHashMap();
						  String posCode="All";
						  StringBuilder sbSql = new StringBuilder();  	
					      String fromDate1=strFromdate.split("-")[2]+"-"+strFromdate.split("-")[1]+"-"+strFromdate.split("-")[0];
					      String toDate1=strToDate.split("-")[2]+"-"+strToDate.split("-")[1]+"-"+strToDate.split("-")[0];
					      if(!strPOSName.equalsIgnoreCase("ALL"))
					      {
							if(map.containsKey(strPOSName))
							{
								 posCode=(String) map.get(strPOSName);
							}
					      }
					      double amtTotal=0, netTotal=0, paxTotal=0;
					      List colHeader = new ArrayList();
					      double sumtSale = 0.00, sumFloat = 0.00, sumCash = 0.00, sumAdvance = 0.00, sumTransferIn = 0.00, sumTotalReceipt = 0.00;
				          double sumPay = 0.00, sumWithDrawal = 0.00, sumTransferOut = 0.00, sumTotalPay = 0.00, sumCashInhand = 0.00;
				          double sumHdAmt = 0.00, SumDining = 0.00, sumTaleAway = 0.00, sumNoOfBill = 0.00, SumNoOfVoidedBill = 0.00, SumNoOfModifyBill = 0.00, sumRefund = 0.00;
				          double sumNetSale = 0.00, sumGrossSale = 0.00, sumAPC = 0.00; 
				          
				          List list =new ArrayList();
				          List totalList=new ArrayList();
				          totalList.add("Total");
				          totalList.add(" ");
				          List listDayEndData = objReportService.funProcessDayEndReport(posCode,fromDate1,toDate1);
				            DecimalFormat decFormat = new DecimalFormat("0");
				            DecimalFormat decFormatFor2Decimal = new DecimalFormat("0.00");
				            if(listDayEndData.size()>0)
				            {
				            	for(int i=0;i<listDayEndData.size();i++)
				            	{
				            		Object[] obj=(Object[]) listDayEndData.get(i);
				            		List arrList=new ArrayList();
				            		arrList.add(obj[0].toString());
									arrList.add(obj[1].toString());
									arrList.add(Double.parseDouble(obj[2].toString()));
									arrList.add(Double.parseDouble(obj[3].toString()));
									arrList.add(Double.parseDouble(obj[4].toString()));
									arrList.add(Double.parseDouble(obj[5].toString()));
									arrList.add(Double.parseDouble(obj[6].toString()));
									arrList.add(Double.parseDouble(obj[7].toString()));
									arrList.add(Double.parseDouble(obj[8].toString()));
									arrList.add(Double.parseDouble(obj[9].toString()));
									arrList.add(Double.parseDouble(obj[10].toString()));
									arrList.add(Double.parseDouble(obj[11].toString()));
									arrList.add(Double.parseDouble(obj[12].toString()));
									arrList.add(Double.parseDouble(obj[13].toString()));
									arrList.add(Double.parseDouble(obj[14].toString()));
									arrList.add(Double.parseDouble(obj[15].toString()));
									arrList.add(Double.parseDouble(obj[16].toString()));
									arrList.add(Double.parseDouble(obj[17].toString()));
									arrList.add(Double.parseDouble(obj[18].toString()));
									arrList.add(Double.parseDouble(obj[19].toString()));
									arrList.add(obj[20].toString());
									arrList.add(obj[21].toString());
									arrList.add(Double.parseDouble(obj[22].toString()));
									arrList.add(Double.parseDouble(obj[23].toString()));
									arrList.add(Double.parseDouble(obj[24].toString()));
									
									list.add(arrList);
									sumtSale = sumtSale + Double.parseDouble(obj[5].toString());
									sumFloat = sumFloat +  Double.parseDouble(obj[6].toString());
									sumCash = sumCash +  Double.parseDouble(obj[7].toString());
									sumAdvance = sumAdvance +  Double.parseDouble(obj[8].toString());
									sumTransferIn = sumTransferIn +  Double.parseDouble(obj[9].toString());
									sumTotalReceipt = sumTotalReceipt +  Double.parseDouble(obj[10].toString());
									sumPay = sumPay +  Double.parseDouble(obj[11].toString());
									sumWithDrawal +=  Double.parseDouble(obj[12].toString());
									sumTransferOut +=  Double.parseDouble(obj[13].toString());
									sumTotalPay +=  Double.parseDouble(obj[15].toString());
									sumCashInhand +=  Double.parseDouble(obj[16].toString());
									sumHdAmt +=  Double.parseDouble(obj[2].toString());
									SumDining +=  Double.parseDouble(obj[3].toString());
									sumTaleAway +=  Double.parseDouble(obj[4].toString());
									sumNoOfBill +=  Double.parseDouble(obj[17].toString());
									SumNoOfVoidedBill +=  Double.parseDouble(obj[18].toString());
									SumNoOfModifyBill +=  Double.parseDouble(obj[19].toString());
									sumRefund +=  Double.parseDouble(obj[14].toString());
									sumNetSale +=  Double.parseDouble(obj[22].toString());
									sumGrossSale +=  Double.parseDouble(obj[23].toString());
									sumAPC +=  Double.parseDouble(obj[24].toString());
								}
				            	
				            	totalList.add(sumHdAmt);
				            	totalList.add(SumDining);
				            	totalList.add(sumTaleAway);
				            	totalList.add(sumtSale);
				            	totalList.add(sumFloat);
				            	totalList.add(sumCash);
				            	totalList.add(sumAdvance);
				            	totalList.add(sumTransferIn);
				            	totalList.add(sumTotalReceipt);
				            	totalList.add(sumPay);
				            	totalList.add(sumWithDrawal);
				            	totalList.add(sumTransferOut);
				            	totalList.add(sumRefund);
				            	totalList.add(sumTotalPay);
				            	totalList.add(sumCashInhand);
				            	totalList.add(sumNoOfBill);
				            	totalList.add(SumNoOfVoidedBill);
				            	totalList.add(SumNoOfModifyBill);
				            	totalList.add("");
				            	totalList.add("");
				            	totalList.add(sumNetSale);
				            	totalList.add(sumGrossSale);
				            	totalList.add(sumAPC);
								
				            	List listcol=new ArrayList();

								listcol.add("pos");
								listcol.add("pos Date");
								listcol.add("HD Amt");
								listcol.add("Dining Amt");
								listcol.add("DTake Away");
								listcol.add("Total Sale");
								listcol.add("Float");
								listcol.add("Cash");
								listcol.add("Advance");
								listcol.add("Transfer In");
								listcol.add("Total Receipt");
								listcol.add("Pay");
								listcol.add("With Drawal");
								listcol.add("Tranf Out");
								listcol.add("Refund");
								listcol.add("Total Pay");
								listcol.add("Cash In Hand");
								listcol.add("No Of Bill");
								listcol.add("No Of Voided Bill");
								listcol.add("No Of Modify Bil");
								listcol.add("Stock Adjustment No");
								listcol.add("Excise Bill Generation");
								listcol.add("Net Sale");
								listcol.add("Gross Sale");
								listcol.add("APC");
								
								resMap.put("listcol", listcol);
								resMap.put("List", list);
								resMap.put("totalList", totalList);
					        
				            }
					        return resMap;
				  }
		
		}


