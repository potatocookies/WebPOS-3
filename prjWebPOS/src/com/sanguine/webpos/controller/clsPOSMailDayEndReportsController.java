package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.webpos.controller.clsPOSBillReportController;
import com.sanguine.webpos.util.clsPOSSendMail;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSUtilityController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;
import com.sanguine.webpos.bean.clsPOSReportBean;

@Controller
public class clsPOSMailDayEndReportsController {

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	intfBaseService objBaseService;
	
	@Autowired
	private clsPOSUtilityController objUtility;
	
	@Autowired
	private clsPOSBillReportController objPOSBillReportController;
	
	@Autowired
	private clsPOSItemWiseReportController objPOSItemWiseReportController;
	
	@Autowired
	private clsPOSGroupWiseReportController objPOSGroupWiseReportController; 
	
	@Autowired
	private clsPOSSubGroupWiseReportController objPOSSubGroupWiseReportController;
	
	@Autowired
	private clsPOSOperatorWiseReportController objPOSOperatorWiseReportController;
	
	@Autowired
	private clsPOSSettlementWiseReportController objPOSSettlementWiseReportController;
	
	@Autowired
	private clsPOSVoidBillReportController objPOSVoidBillReportController;

	@Autowired
	private clsPOSTaxWiseReportController objPOSTaxWiseReportController;
	
	@Autowired
	private clsPOSDiscountWiseReportController objPOSDiscountWiseReportController;
	
	@Autowired
	private clsPOSGroupSubGroupWiseReportController objPOSGroupSubGroupWiseReportController;
	
	@Autowired
	private clsPOSComplimentarySettlementReportController objPOSComplimentarySettlementReportController;
	
	@Autowired
	private clsPOSNonChargeableSettlementReportController objPOSNonChargeableSettlementReportController;
	
	@Autowired
	private clsPOSAuditorReportController objPOSAuditorReportController;
	
	@Autowired
	private clsPOSTaxBreakupSummaryReport objPOSTaxBreakupSummaryReport;
	
	@Autowired
	private clsPOSMenuHeadWiseReportController objPOSMenuHeadWiseReportController;
	
	@Autowired
	private clsPOSWaiterWiseItemReportController objPOSWaiterWiseItemReportController;
	
	@Autowired
	private clsPOSWaiterWiseIncentivesReportController objPOSWaiterWiseIncentivesReportController;  
	
	@Autowired
	private clsPOSDailyCollectionReportController objPOSDailyCollectionReportController;
	
	@Autowired
	private clsPOSDailySalesReportController objPOSDailySalesReportController;
	
	@Autowired
	private clsPOSVoidKOTReportController objPOSVoidKOTReportController;
	
	@Autowired
	private clsPOSGuestCreditReportController objPOSGuestCreditReportController;
	
	@Autowired
	private clsPOSSubGroupWiseSummaryReportController objPOSSubGroupWiseSummaryReportController;
	
	@Autowired
	private clsPOSItemWiseConsumptionReportController objPOSItemWiseConsumptionReportController;
	
	@Autowired
	private clsPOSCostCenterWiseReportController objPOSCostCenterWiseReportController;
	
	@Autowired
	private clsPOSAverageItemsPerBillReportController objPOSAverageItemsPerBillReportController;
	
	@Autowired
	private clsPOSCashManagementFlashController objPOSCashManagementFlashController;
	
	@Autowired
	private clsPOSTableWisePaxReportController objPOSTableWisePaxReportController;
	
	@Autowired
	private clsPOSAveragePerCoverReportController objPOSAveragePerCoverReportController;
	
	@Autowired
	private clsPOSAverageTicketValueReportController objPOSAverageTicketValueReportController;
	
	@Autowired
	private clsPOSSalesSummaryFlashController objPOSSalesSummaryFlashController;
	
	@Autowired
	private clsPOSCounterWiseSalesReportController objPOSCounterWiseSalesReportController;
	
	@Autowired
	private clsPOSWaiterWiseItemWiseIncentiveReportController objPOSWaiterWiseItemWiseIncentiveReportController;
	
	@Autowired
	private  clsPOSWiseSalesReportController objPOSWiseSalesReportController;
	
	@Autowired
	private  clsPOSDayWiseSalesSummaryFlashController objPOSDayWiseSalesSummaryFlashController;
	
	@Autowired
	private  clsPOSBillWiseSettlementSalesSummaryFlashController objPOSBillWiseSettlementSalesSummaryFlashController;
	
	@Autowired
	private clsPOSDayEndFlashReportController objPOSDayEndFlashReportController;
	
	@Autowired
	private clsPOSAuditFlashController objPOSAuditFlashController;
	
	@Autowired
	private clsPOSRevenueHeadSalesReportController objPOSRevenueHeadSalesReportController;
	
	@Autowired
	clsPOSSendMail objPOSSendMail;
	
	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;
	
	@RequestMapping(value="/frmPOSMailDayEndReports",method= RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model,HttpServletRequest req)
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMailDayEndReports_1","command", new clsPOSDayEndProcessBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMailDayEndReports","command", new clsPOSDayEndProcessBean());
		}else {
			return null;
		}
	}
	
	//all reports data from DB through web service 
		@RequestMapping(value ="/loadAllMailDayEndReports",method =RequestMethod.GET)
		public @ResponseBody List<clsPOSDayEndProcessBean> funLoadAllReportsName(HttpServletRequest request)
		{
			List<clsPOSDayEndProcessBean> listbean=new ArrayList<clsPOSDayEndProcessBean>();
			clsPOSDayEndProcessBean obBean;
			
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();
			String POSCode=request.getSession().getAttribute("loginPOS").toString();
			
			Map mapObj = funLoadAllReportsName(POSCode,strClientCode);
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
				
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			alReportName= gson.fromJson(mapObj.get("ReportName").toString(), listType);
			alCheckRpt= gson.fromJson(mapObj.get("CheckReport").toString(), listType);
			for(int i=0;i<alReportName.size();i++)
			{
				obBean=new clsPOSDayEndProcessBean();
				obBean.setStrReportName(alReportName.get(i).toString());
				if(alCheckRpt.size()==alReportName.size())
				{
					obBean.setStrReportCheck(Boolean.parseBoolean(alCheckRpt.get(i).toString()));
				}
				else
				{
					obBean.setStrReportCheck(Boolean.parseBoolean("false"));
				}
				
				listbean.add(obBean);
			}
			return listbean;
		}
		
		@RequestMapping(value = "/MailDayEndReport", method = RequestMethod.POST)
		public String funGetSelectedMailReport(@ModelAttribute("command") @Valid clsPOSDayEndProcessBean objBean,BindingResult result,HttpServletRequest req,
												HttpServletResponse resp)
		{
			Map jsMailReportData=new HashMap();
			String urlHits="2";
			String userCode=req.getSession().getAttribute("gUserCode").toString();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		 	String strPOSCode=req.getSession().getAttribute("loginPOS").toString();

			clsPOSDayEndProcessBean obDayEndd;
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
			if(!result.hasErrors())
			{
				List<clsPOSDayEndProcessBean> listMailReport =objBean.getListMailReport();
				for(int i=0;i<listMailReport.size();i++)
				{
					obDayEndd=listMailReport.get(i);
					//alReportName.add(obDayEndd.getStrReportName());
					if(obDayEndd.getStrReportCheck()==null)
					{
						alCheckRpt.add(false);
					}else{
						alReportName.add(obDayEndd.getStrReportName());
						alCheckRpt.add(obDayEndd.getStrReportCheck());
					}
				}
				
			}
			String fromDate=objBean.getFromDate();
			String toDate=objBean.getToDate();
	 		
			funSendEmailClicked(alReportName,fromDate,toDate,strPOSDate,strPOSCode,strClientCode,userCode,resp,req);
			
			return "redirect:/frmPOSMailDayEndReports.html";
		}
		
		public Map funLoadAllReportsName(String strPOSCode,String strClientCode)
		{
			Map mapReportNames=new HashMap<>();
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
			try{
			
				StringBuilder  sql= new StringBuilder("select a.strModuleName,a.strFormName from tblforms a "
	                    + "where a.strModuleType='R' "
	                    + "order by a.intSequence;");
				 
	             List listRPT= objBaseService.funGetList(sql, "sql");
	             if(listRPT.size()>0)
	             {
	             	for(int i=0;i<listRPT.size();i++)
	             	{
	             		Object[] ob=(Object[])listRPT.get(i);
	             		alReportName.add(ob[0].toString());
	             	}
	             }
	             
	             sql.setLength(0);
	             sql.append("select  strPOSCode,strReportName,date(dtePOSDate) "
	                    + "from tbldayendreports "
	                    + "where strPOSCode='"+strPOSCode+"' "
	                    + "and strClientCode='" + strClientCode + "';");
	             
	             List RPT= objBaseService.funGetList(sql, "sql");
	             
	             if(RPT.size()>0)
	             {
	             	for(int i=0;i<RPT.size();i++)
	             	{
	             		Object[] ob=(Object[])RPT.get(i);
	             		 String reportName=ob[1].toString();
	             		for (int j = 0; j < alReportName.size(); j++)
	                    {
	             			if(alCheckRpt.size()==alReportName.size())
	             			{
		                       if(alReportName.get(j)!=null && alReportName.get(j).toString().equalsIgnoreCase(reportName))                        
		                        {
		                        	alCheckRpt.set(j, Boolean.parseBoolean("true"));
		                        }
		                       
	             			}
	             			else{
	             				 if(alReportName.get(j)!=null && alReportName.get(j).toString().equalsIgnoreCase(reportName))                        
	 	                        {
	 	                        	alCheckRpt.add(j, Boolean.parseBoolean("true"));
	 	                        }
	 	                        else{
	 	                        	alCheckRpt.add(j, Boolean.parseBoolean("false"));
	 	                        }
	             			}
	                    }
	             	}
	             }
	             
	             		Gson gson = new Gson();
				 	    Type type = new TypeToken<List<String>>() {}.getType();
			            String ReportName = gson.toJson(alReportName, type);
			            String CheckReport = gson.toJson(alCheckRpt, type);
			            mapReportNames.put("ReportName", ReportName);
			            mapReportNames.put("CheckReport", CheckReport);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return mapReportNames;
		}
		
	public void funSendEmailClicked(ArrayList alReportName,String fromDate, String toDate,String posDate,String posCode,
				String clientCode,String UserCode,HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			Date dt=new Date();
			String currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			String sql=new String();
			StringBuilder sqlBuilder = new StringBuilder();
			String posName=req.getSession().getAttribute("gPOSName").toString();
			sqlBuilder.setLength(0);
			sqlBuilder.append("insert into tbldayendreports "
			+ "(strPOSCode,strClientCode,strReportName,dtePOSDate,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strDataPostFlag) "
			+ "values ");
			int count = 0;
			objUtility.funCreateReportFolder("DayEndMailReports");
			
			for (int r = 0; r < alReportName.size(); r++)
			{
			if (count == 0)
			{
			sqlBuilder.append("('" + posCode + "','" + clientCode + "','" + alReportName.get(r).toString() + "'"
				+ ",'" + posDate + "','" + UserCode + "','" + UserCode + "'"
				+ ",'" + currentDateTime + "','" + currentDateTime + "','N')");
			count++;
			}
			else
			{
			sqlBuilder.append(",('" + posCode + "','" + clientCode + "','" +alReportName.get(r).toString() + "'"
				+ ",'" + posDate + "','" + UserCode + "','" + UserCode + "'"
				+ ",'" + currentDateTime+ "','" + currentDateTime + "','N')");
			count++;
			}
				funGenerateReport(alReportName.get(r).toString(),resp,req,fromDate,toDate);
			}
			File[] files = new File("D:/setup/eclipse/DayEndMailReports").listFiles();
			String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gReceiverEmailIds");
			int check=objPOSSendMail.funSendMail(gReceiverEmailIds, files,clientCode,posCode,posName,posDate);
			
			objBaseService.funExecuteUpdate("delete from tbldayendreports where strPOSCode='" + posCode + "' and strClientCode='" + clientCode + "' ", "sql");
			objBaseService.funExecuteUpdate(sqlBuilder.toString(), "sql");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void funGenerateReport(String reportName,HttpServletResponse resp, HttpServletRequest req,String fromDate,String toDate) throws Exception
	{
		String PosCode= req.getSession().getAttribute("loginPOS").toString();
		String posName = objUtility.funGetPOSName(PosCode);
		String strDocType="xls";
		String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		
		clsPOSReportBean objBean = new clsPOSReportBean();
		//objBean.setStrPosName(posName);
		objBean.setStrDocType(strDocType);
		objBean.setFromDate(fromDate);
		objBean.setToDate(toDate);
		objBean.setStrPOSName(posName);
		objBean.setStrPOSCode(PosCode);
		objBean.setStrSGName("ALL");
		objBean.setStrSettleCode("ALL");
		objBean.setStrReportType("Summary");
		if (reportName.equalsIgnoreCase("Bill Wise Report".toUpperCase()))
		{
			objPOSBillReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Item Wise Report".toUpperCase()))
		{
			objBean.setStrType("Yes");
			objPOSItemWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Group Wise Report".toUpperCase()))
		{
			objPOSGroupWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("SubGroupWise Report".toUpperCase()))
		{
			objPOSSubGroupWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("OperatorWise Report".toUpperCase()))
		{
			objBean.setStrDocType("Excel");
			objPOSOperatorWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("SettlementWise Report".toUpperCase()))
		{
			objPOSSettlementWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Void Bill Report".toUpperCase()))
		{
			objBean.setStrReportType("Summary");
			objBean.setStrReasonCode("ALL");
			objPOSVoidBillReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Cost Centre Report".toUpperCase()))
		{
			objPOSCostCenterWiseReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Tax Wise Report".toUpperCase()))
		{
			objPOSTaxWiseReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Cash Mgmt Report".toUpperCase()))
		{
			clsPOSCashManagementDtlBean obj=new clsPOSCashManagementDtlBean();
			obj.setFromDate("01-10-2018");
			obj.setToDate("09-10-2018");
			obj.setUserCode("Detail");
			obj.setPosCode("P01");
			obj.setUserName("All");
			objPOSCashManagementFlashController.funReport(obj,resp,req,"DayEndMail");
		}
		/*else if (reportName.equalsIgnoreCase("Audit Flash".toUpperCase()))
		{
			objBean.setStrReasonMaster("ALL");
			objBean.setStrPSPCode("Modified Bill");
			objBean.setStrSort("Bill");
			objBean.setStrType("All");
			objBean.setStrPOSName("P01");
			objPOSAuditFlashController.funExportReportForDayEndMail(objBean,resp,req);
		}
		else if (reportName.equalsIgnoreCase("Day End Flash".toUpperCase()))
		{
			objPOSDayEndFlashReportController.funExportReportForDayEndMail(objBean, resp, req);
		}*/
		else if (reportName.equalsIgnoreCase("AvgItemPerBill".toUpperCase()))
		{
			objPOSAverageItemsPerBillReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("AvgPerCover".toUpperCase()))
		{
			objBean.setStrPosWise("NO");
			objBean.setStrDateWise("NO");
			objBean.setStrWShortName("ALL");
			objBean.setStrViewType("Net Sale");
			objBean.setStrReportType("Summary");
			objBean.setStrDocType("xls");
			objPOSAveragePerCoverReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("AvgTicketValue".toUpperCase()))
		{
			objPOSAverageTicketValueReportController.funReport(objBean,resp,req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Discount Report".toUpperCase()))
		{
			objBean.setStrViewType("Summary");
			objPOSDiscountWiseReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Group-SubGroup Wise Report".toUpperCase()))
		{
			objBean.setStrGroupName("ALL");
			objBean.setStrReportType("Summary");
			objPOSGroupSubGroupWiseReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Complimentary Settlement Report".toUpperCase()))
		{
			objBean.setStrReasonCode("ALL");
			objBean.setStrViewType("Summary");
			objPOSComplimentarySettlementReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Counter Wise Sales Report".toUpperCase()))
		{
			objBean.setStrType("Menu Wise");
			objPOSCounterWiseSalesReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Non Chargable KOT Report".toUpperCase()))
		{
			objBean.setStrReasonMaster("ALL");
			objPOSNonChargeableSettlementReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Auditor Report".toUpperCase()))
		{
			objPOSAuditorReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Tax Breakup Summary Report".toUpperCase()))
		{
			objPOSTaxBreakupSummaryReport.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Menu Head Wise".toUpperCase()))
		{
			objPOSMenuHeadWiseReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("WaiterWiseItemReport".toUpperCase()))
		{
			objBean.setStrWShortName("ALL");
			objPOSWaiterWiseItemReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("WaiterWiseIncentivesReport".toUpperCase()))
		{
			objPOSWaiterWiseIncentivesReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Waiter Wise Item Wise Incentives Report".toUpperCase()))
		{
			objBean.setStrGroupName("ALL");
			objPOSWaiterWiseItemWiseIncentiveReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		/*else if (reportName.equalsIgnoreCase("DeliveryboyIncentive".toUpperCase()))
		{
			funGenerateDeliveryBoyIncentivesExcelReport(posCode, posName, fromDate, toDate);
		}
		else if (reportName.equalsIgnoreCase("Sales Summary Flash".toUpperCase()))
		{
			objBean.setStrPayMode("ALL");
			objPOSSalesSummaryFlashController.funReport(objBean, resp, req);
		}*/
		else if (reportName.equalsIgnoreCase("POS Wise Sales".toUpperCase()))
		{
			objBean.setStrViewType("Item Wise");
			objPOSWiseSalesReportController.funExportReportForDayEndMail(objBean, resp, req);
		}
		else if (reportName.equalsIgnoreCase("Daily Collection Report".toUpperCase()))
		{
			objPOSDailyCollectionReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Daily Sales Report".toUpperCase()))
		{
			objPOSDailySalesReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Void KOT Report".toUpperCase()))
		{
			objBean.setStrReportType("ALL");
			objPOSVoidKOTReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Guest Credit Report".toUpperCase()))
		{
			objPOSGuestCreditReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("SubGroupWiseSummaryReport".toUpperCase()))
		{
			objPOSSubGroupWiseSummaryReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		/*else if (reportName.equalsIgnoreCase("DayWiseSalesSummaryFlash".toUpperCase()))
		{
			objBean.setStrOperationType("ALL");
			objBean.setStrSettlementName("ALL");
			objBean.setStrViewBy("Item's Group Wise");
			objBean.setStrGroupName("ALL");
			objPOSDayWiseSalesSummaryFlashController.funExportReportForDayEndMail(objBean, resp, req);
		}
		else if (reportName.equalsIgnoreCase("BillWiseSettlementSalesSummaryFlash".toUpperCase()))
		{
			objBean.setStrOperationType("ALL");
			objBean.setStrSettlementName("ALL");
			objBean.setStrViewBy("Item's Group Wise");
			objBean.setStrGroupName("ALL");
			objPOSBillWiseSettlementSalesSummaryFlashController.funExportReportForDayEndMail(objBean, resp, req);
		}*/
		else if (reportName.equalsIgnoreCase("Revenue Head Wise Item Sales".toUpperCase()))
		{
			objBean.setStrRevenueHead("ALL");
			objPOSRevenueHeadSalesReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		/*else if (reportName.equalsIgnoreCase("Managers Report".toUpperCase()))
		{
		
		}*/
		else if (reportName.equalsIgnoreCase("Item Wise Consumption".toUpperCase()))
		{
			objBean.setStrGroupName("ALL");
			objBean.setStrViewBy("Menu Head");
			objBean.setStrOperationType("Yes");
			objPOSItemWiseConsumptionReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		else if (reportName.equalsIgnoreCase("Table Wise Pax Report".toUpperCase()))
		{
			objPOSTableWisePaxReportController.funReport(objBean, resp, req,"DayEndMail");
		}
		/*else if (reportName.equalsIgnoreCase("Posting Report".toUpperCase()))
		{
		
		}*/
		
	}
}
