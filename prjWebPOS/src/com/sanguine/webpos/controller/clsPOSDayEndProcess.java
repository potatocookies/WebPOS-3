package com.sanguine.webpos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;
import com.sanguine.webpos.bean.clsPOSOperatorWiseReportBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.util.clsPOSBackupDatabase;
import com.sanguine.webpos.util.clsPOSDayEndUtility;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSSendMail;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSDayEndProcess {

	@Autowired
	intfBaseService objBaseService;
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController; 
	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsPOSUtilityController objUtilityController;

	@Autowired 
	clsPOSDayEndUtility objPOSDayEndUtility;

	@Autowired 
	clsPOSBackupDatabase obBackupDatabase;

	@Autowired 
	clsPOSSendMail objSendMail;

	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;

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


	String strPOSCode="",strPOSName="",strPOSDate="",strClientCode="",userCode="",strShiftNo="";
	public static Map mapDayEndReturn=new HashMap();
	public static String gTransactionType = "",gDayEndReportForm="";
	int shiftNo=0,noOfDiscountedBills=0;
	double sales = 0,cashIn = 0,cashOut = 0,totalSales = 0,totalWithdrawl = 0, 
			totalTransIn = 0,totalTransOuts = 0,totalPayments = 0, totalFloat = 0,
			advCash=0, totalDiscount=0,dblApproxSaleAmount=0;

	String shiftEnd="",dayEnd="",emailReport="";
	StringBuilder sql=new StringBuilder();


	@RequestMapping(value = "/frmPOSDayEndProcess", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request,ModelMap modelmap) throws Exception
	{ 
		clsPOSDayEndProcessBean objDayEndProcessBean= new clsPOSDayEndProcessBean();
		List listArrDayEnd=new ArrayList();
		Map mapDayEndProcessData= new HashMap();
		strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		strClientCode=request.getSession().getAttribute("gClientCode").toString();
		userCode=request.getSession().getAttribute("gUserCode").toString();
		strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		strPOSDate=request.getSession().getAttribute("gPOSDate").toString();
		strShiftNo=request.getSession().getAttribute("gShiftNo").toString();

		StringBuilder sql =new StringBuilder("select dtePOSDate,intShiftCode from tbldayendprocess "
				+ "where strDayEnd='N' and strPOSCode='" + strPOSCode + "' and (strShiftEnd='' or strShiftEnd='N')   ");
		try{
			List listShiftNo = objBaseService.funGetList(sql, "sql");

			if (listShiftNo.size()>0)
			{
				Object[] obj=(Object[]) listShiftNo.get(0);
				strShiftNo = obj[1].toString();
			}
		}catch(Exception e){

		}

		Map mapDayEnd= new HashMap();
		Map mapSettlement= new HashMap();
		Map mapSalesInProg= new HashMap();
		Map mapUnSettleBill= new HashMap();

		mapDayEndProcessData=funLoadDayEndData();
		mapDayEnd=(Map) mapDayEndProcessData.get("DayEnd");
		mapSettlement=(Map) mapDayEndProcessData.get("Settlement");
		mapSalesInProg=(Map) mapDayEndProcessData.get("SalesInProg");
		mapUnSettleBill=(Map) mapDayEndProcessData.get("UnSettleBill");

		ArrayList al=new ArrayList<ArrayList<String>>();
		listArrDayEnd=(List) mapDayEnd.get("DayEndArr");
		objDayEndProcessBean.setListDayEnd(listArrDayEnd);
		objDayEndProcessBean.setTotalpax(mapDayEnd.get("totalPax").toString());
		objDayEndProcessBean.setListDayEndTotal((List) mapDayEnd.get("DayEndJArrTot"));

		objDayEndProcessBean.setListSettlement((List) mapSettlement.get("jArrSettlement"));
		objDayEndProcessBean.setListSettlementTotal((List) mapSettlement.get("jArrsettlementTot"));

		objDayEndProcessBean.setListSalesInProg((List) mapSalesInProg.get("SalesInProgress"));

		objDayEndProcessBean.setListUnSettlebill((List) mapUnSettleBill.get("UnSettleBill"));

		objDayEndProcessBean.setTotal(mapUnSettleBill.get("total").toString());


		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndProcess_1","command", objDayEndProcessBean);
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndProcess","command", objDayEndProcessBean);
		}else {
			return null;
		}
	}

	public Map funLoadDayEndData() throws Exception
	{
		Map mapDayEndDataProcess= new HashMap();
		Map mapDayEndData= new HashMap();
		Map mapSettlement= new HashMap();
		Map mapSalesInProg= new HashMap();
		Map mapUnSettleBill= new HashMap();


		try{
			Map dayEndDataWS= funFillCurrencyGrid();
			Map settlementWS= funFillSettlementWiseSalesGrid();
			Map salesInProgWS= funFillTableSaleInProgress();
			Map unSettleBillws= funFillTableUnsettleBills();

			dblApproxSaleAmount=0;

			mapDayEndData.put("DayEndArr",funGetJsonArrRowDayEnd((List)dayEndDataWS.get("tblDayEnd")));// listLastJson);
			mapDayEndData.put("totalPax", dayEndDataWS.get("totalPax").toString());
			mapDayEndData.put("DayEndJArrTot", funGetJsonArrRowDayEnd((List)dayEndDataWS.get("TotalDayEnd")));

			mapSettlement.put("jArrSettlement", funGetJsonArrRowSettlement((List)settlementWS.get("settlement")));
			mapSettlement.put("jArrsettlementTot", funGetJsonArrRowSettlement((List)settlementWS.get("settlementTot")));

			mapSalesInProg.put("SalesInProgress",funGetJsonArrRowSalesOfProg((List)salesInProgWS.get("salesInProg")));

			mapUnSettleBill.put("UnSettleBill",funGetJsonArrRowSettlement((List)unSettleBillws.get("jArrUnSettle")));
			mapUnSettleBill.put("total", unSettleBillws.get("ApproxSaleAmount").toString());
			mapDayEndDataProcess.put("DayEnd", mapDayEndData);
			mapDayEndDataProcess.put("Settlement", mapSettlement);
			mapDayEndDataProcess.put("SalesInProg", mapSalesInProg);
			mapDayEndDataProcess.put("UnSettleBill", mapUnSettleBill);

		}catch(Exception e){
			e.printStackTrace();
		}



		return mapDayEndDataProcess;
	}

	public Map funFillCurrencyGrid()throws Exception
	{
		totalSales=0;
		List listArrDayEnd=new ArrayList();
		List listDayEndTot=new ArrayList();
		Map mapDayEnd =new HashMap();
		Map mapDayEndTot =new HashMap();
		//List listDayEnd=new ArrayList<>();
		sql =new StringBuilder("select strSettelmentDesc from tblsettelmenthd where strSettelmentType='Cash'");
		List listSql = objBaseService.funGetList(sql, "sql");
		if (listSql.size() > 0) 
		{
			for (int i = 0; i < listSql.size(); i++) 
			{
				Map mapOb =new HashMap();
				List dataList=new ArrayList();
				String str= (String) listSql.get(i);

				mapOb.put("0",str.toString());
				mapOb.put("1","0.00");
				mapOb.put("2","0.00");
				mapOb.put("3","0.00");
				mapOb.put("4","0.00");
				mapOb.put("5","0.00");
				mapOb.put("6","0.00");
				mapOb.put("7","0.00");
				mapOb.put("8","0.00");
				mapOb.put("9","0.00");
				mapOb.put("10","0");

				listArrDayEnd.add(mapOb);


			}

			mapDayEnd.put("tblDayEnd", listArrDayEnd);
		}

		sql =new StringBuilder("SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt),c.strSettelmentType "
				+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
				+ "Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
				+ " and date(a.dteBillDate ) ='" + strPOSDate + "' and a.strPOSCode='" + strPOSCode + "'"
				+ " and c.strSettelmentType='Cash' and a.intShiftCode=" + strShiftNo + " GROUP BY c.strSettelmentDesc,a.strPosCode");

		listSql =objBaseService.funGetList(sql, "sql");
		if (listSql.size() > 0) 
		{
			for (int i = 0; i < listSql.size(); i++) 
			{
				List dataList=new ArrayList();
				Object[] obj = (Object[]) listSql.get(i);

				if (obj[0].toString().equals("Cash"))
				{
					sales = sales + (Double.parseDouble(obj[1].toString().toString()));
				}
				totalDiscount = totalDiscount + (Double.parseDouble(obj[2].toString().toString()));

				totalSales = totalSales + (Double.parseDouble(obj[1].toString().toString()));

				for (int cntDayEndTable = 0; cntDayEndTable < listArrDayEnd.size(); cntDayEndTable++)
				{
					Map jr=(Map) listArrDayEnd.get(cntDayEndTable);

					if(jr.get("0").toString().equals(obj[0].toString()))
					{
						jr.put("1",obj[1].toString());

					}

				}
			}
		}

		noOfDiscountedBills = 0;
		sql = new StringBuilder("SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
				+ "Where date(dteBillDate ) ='" + strPOSDate + "' and strPOSCode='" + strPOSCode + "' "
				+ "and dblDiscountAmt > 0.00 and intShiftCode=" + strShiftNo
				+ " GROUP BY strPosCode");
		List listTotDiscBill = objBaseService.funGetList(sql, "sql");
		if (listTotDiscBill.size() > 0) 
		{
			for (int i = 0; i < listTotDiscBill.size(); i++) 
			{

				Object[] obj = (Object[]) listTotDiscBill.get(i);
				noOfDiscountedBills = Integer.parseInt(obj[0].toString());
			}
		}

		int totalBillNo = 0;
		sql =new StringBuilder("select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "' and "
				+ "strPOSCode='" + strPOSCode + " and intShiftCode=" + strShiftNo
				+ "' GROUP BY strPosCode");

		List listTotBill = objBaseService.funGetList(sql, "sql");
		if (listTotBill.size() > 0) 
		{
			for (int i = 0; i < listTotBill.size(); i++) 
			{
				Object[] obj = (Object[]) listTotBill.get(i);
				totalBillNo = Integer.parseInt(obj[0].toString());
			}
		}



		// mapDayEndTot

		Map mapOb =new HashMap();
		mapOb.put("0", "Total Sales");
		mapOb.put("1", totalSales);
		mapOb.put("8", totalBillNo);
		//    mapOb.put("1", discountRecords);

		//jArrDayEndTot.put(mapOb);

		sql =new StringBuilder( "select count(dblAdvDeposite) from tbladvancereceipthd "
				+ "where dtReceiptDate='" + strPOSDate + "' and intShiftCode=" + strShiftNo);
		List listTotalAdvance = objBaseService.funGetList(sql, "sql");
		if (listTotalAdvance.size() > 0) 
		{ 
			int count =0;
			for (int i = 0; i < listTotalAdvance.size(); i++) 
			{
				String str= String.valueOf(listTotalAdvance.get(i));
				count = Integer.parseInt(str);
			}



			if (count > 0)
			{
				//sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
				sql =new StringBuilder( "select sum(b.dblAdvDepositesettleAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
						+ "where date(a.dtReceiptDate)='" + strPOSDate + "' and a.strPOSCode='" + strPOSCode
						+ "' and intShiftCode=" + strShiftNo + " and c.strSettelmentCode=b.strSettlementCode "
						+ "and a.strReceiptNo=b.strReceiptNo and c.strSettelmentType='Cash'");
				System.out.println(sql);

				listTotalAdvance = objBaseService.funGetList(sql, "sql");
				if (listTotalAdvance.size() > 0) 
				{
					Object[] obj = (Object[]) listSql.get(0);
					advCash = Double.parseDouble(obj[0].toString());
				}
				Map jr=(Map) listArrDayEnd.get(0);
				jr.put("4",advCash );
			}
		}

		sql =new StringBuilder( "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
				+ "where dteTransDate='" + strPOSDate + "' and strPOSCode='" + strPOSCode + "' "
				+ "and intShiftCode=" + strShiftNo
				+ " group by strTransType,strCurrencyType");
		//System.out.println(sql);
		List listTransaction = objBaseService.funGetList(sql, "sql");
		if (listTransaction.size() > 0) 
		{
			for(int i=0;i<listTransaction.size();i++)
			{
				Object[] obj = (Object[]) listTransaction.get(i);

				for (int cntDayEndTable = 0; cntDayEndTable < listArrDayEnd.size(); cntDayEndTable++)
				{
					if (obj[0].toString().equals("Float"))
					{
						Map job=(Map) listArrDayEnd.get(cntDayEndTable);
						if (job.get("0").toString().equals(obj[2].toString()))
						{
							totalFloat += Double.parseDouble(obj[1].toString());
							job.put("2",obj[1].toString());
							cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
						}
					}
					else if (obj[0].toString().equals("Transfer In"))
					{
						Map job=(Map) listArrDayEnd.get(cntDayEndTable);
						if (job.get(0).toString().equals(obj[2].toString()))
						{
							totalTransIn +=Double.parseDouble(obj[1].toString());
							job.put("3",obj[1].toString());
							cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
						}
					}
					else if (obj[0].toString().equals("Payments"))
					{
						Map job=(Map) listArrDayEnd.get(cntDayEndTable);
						if (job.get(0).toString().equals(obj[2].toString()))
						{
							totalPayments += Double.parseDouble(obj[1].toString());
							job.put("6",obj[1].toString());
							cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
						}
					}
					else if (obj[0].toString().equals("Transfer Out"))
					{
						Map job=(Map) listArrDayEnd.get(cntDayEndTable);
						if (job.get(0).toString().equals(obj[2].toString()))
						{
							totalTransOuts += Double.parseDouble(obj[1].toString());
							job.put("7",obj[1].toString());
							cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
						}
					}
					else if (obj[0].toString().equals("Withdrawl"))
					{
						Map job=(Map) listArrDayEnd.get(cntDayEndTable);
						if (job.get(0).toString().equals(obj[2].toString()))
						{
							totalWithdrawl += Double.parseDouble(obj[1].toString());
							job.put("8",obj[1].toString());
							cashOut = cashOut + (Double.parseDouble(obj[1].toString()));
						}
					}
				}
			}
		}

		sql =new StringBuilder( "select sum(intPaxNo) from tblbillhd where intShiftCode=" + strShiftNo + " "
				+ "and date(dteBillDate ) ='" + strPOSDate + "'" + "and strPOSCode='" + strPOSCode + "'");
		//System.out.println(sql);
		String totalPax="";
		List listTotalPax = objBaseService.funGetList(sql, "sql");
		if(listTotalPax.get(0) !=null)
		{
			if (listTotalPax.size() > 0) 
			{

				totalPax = listTotalPax.get(0).toString();

			}
		}
		mapDayEnd.put("totalPax", totalPax);

		cashIn = cashIn + advCash + sales;
		mapOb.put("2", totalFloat);
		mapOb.put("3", totalTransIn);
		mapOb.put("4", advCash);
		mapOb.put("5", cashIn);
		mapOb.put("6", totalPayments);
		mapOb.put("7", totalTransOuts);
		mapOb.put("8", totalWithdrawl);
		mapOb.put("9", cashOut);
		mapOb.put("10","");

		listDayEndTot.add(mapOb);

		double inHandCash = (cashIn) - cashOut;

		mapDayEnd.put("TotalDayEnd", listDayEndTot);

		double totalReceipts = 0.00, totalPayments = 0.00, balance = 0.00;
		for (int cntDayEndTable = 0; cntDayEndTable < listArrDayEnd.size(); cntDayEndTable++)
		{
			Map job=(Map) listArrDayEnd.get(cntDayEndTable);
			totalReceipts = Double.parseDouble(job.get("1").toString())
					+ Double.parseDouble(job.get("2").toString())
					+ Double.parseDouble(job.get("3").toString())
					+ Double.parseDouble(job.get("4").toString());

			totalPayments = Double.parseDouble(job.get("6").toString())
					+ Double.parseDouble(job.get("7").toString())
					+ Double.parseDouble(job.get("8").toString());
			balance = totalReceipts - totalPayments;
			job.put("10", balance);

		}
		return mapDayEnd;
	}

	public Map funFillSettlementWiseSalesGrid() throws Exception
	{

		Map mapSettlement =new HashMap();
		Map mapSettlementTot =new HashMap();
		List listSettt=new ArrayList();
		List listSetttTot=new ArrayList();
		totalDiscount = 0;
		totalSales = 0;
		sql =new StringBuilder( "SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt) "
				+ "FROM tblbillhd a, tblbillsettlementdtl b"
				+ ", tblsettelmenthd c Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
				+ " and date(a.dteBillDate ) ='" + strPOSDate + "' and a.strPOSCode='" + strPOSCode + "'"
				+ " and intShiftCode=" + strShiftNo
				+ " GROUP BY c.strSettelmentDesc,a.strPosCode");
		//System.out.println(sql);
		List listSettlementSale = objBaseService.funGetList(sql, "sql");
		if (listSettlementSale.size() > 0) 
		{
			for(int i=0;i<listSettlementSale.size();i++)
			{
				Object[] obj = (Object[]) listSettlementSale.get(i);
				Map js=new HashMap();
				js.put("0",obj[0].toString());
				js.put("1",obj[1].toString());
				js.put("2",obj[2].toString());

				totalDiscount = totalDiscount + (Double.parseDouble(obj[2].toString()));
				totalSales = totalSales + (Double.parseDouble(obj[1].toString()));
				listSettt.add(js);
			}
		}

		noOfDiscountedBills = 0;
		sql =new StringBuilder( "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
				+ "Where date(dteBillDate ) ='" + strPOSDate + "' and strPOSCode='" + strPOSCode + "' "
				+ "and dblDiscountAmt > 0.00 GROUP BY strPosCode");

		List listTotalDiscountBills = objBaseService.funGetList(sql, "sql");
		if (listTotalDiscountBills.size() > 0) 
		{
			for(int i=0;i<listTotalDiscountBills.size();i++)
			{
				Object[] obj = (Object[]) listTotalDiscountBills.get(i);
				noOfDiscountedBills =Integer.parseInt(obj[0].toString());
			}
		}
		//System.out.println("Discounts="+totalDiscount+"\tTotal Bills="+noOfDiscountedBills);
		int totalBillNo = 0;
		sql =new StringBuilder( "select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "' and "
				+ "strPOSCode='" + strPOSCode + "' GROUP BY strPosCode");

		List listTotalBills = objBaseService.funGetList(sql, "sql");
		if (listTotalBills.size() > 0) 
		{
			totalBillNo = Integer.parseInt(String.valueOf(listTotalBills.get(0)));
		}

		Map job=new HashMap();
		job.put("0","Total Sales");
		job.put("1",totalSales);
		job.put("2",totalBillNo);
		listSetttTot.add(job);

		job=new HashMap();
		job.put("0","Total Discount");
		job.put("1",totalDiscount);
		job.put("2",noOfDiscountedBills);
		listSetttTot.add(job);


		//tblSettlementWiseSalesTotal
		/*if (listSetttTot.size() > 0)
			        {
			    		JSONObject jo=(JSONObject) listSetttTot.get(0);
			    		jo.put("2", totalBillNo);

			        }*/
		dblApproxSaleAmount += totalSales;

		mapSettlement.put("settlement", listSettt);
		mapSettlement.put("settlementTot", listSetttTot);
		return mapSettlement;
	}

	public Map funFillTableSaleInProgress() throws Exception
	{
		Map mapSaleInProgress =new HashMap();
		List listSalesInProgress=new ArrayList();
		double dblSaleInProgressAmount = 0.00;


		String sql_FillTable = "select b.strTableName,sum(a.dblAmount) "
				+ " from tblitemrtemp a,tbltablemaster b "
				+ " where a.strTableNo=b.strTableNo and a.strNCKotYN='N' and a.strPOSCode='" + strPOSCode + "' "
				+ " group by a.strTableNo";
		Map mapOb=new HashMap();
		List listSaleprog = objBaseService.funGetList(new StringBuilder(sql_FillTable), "sql");
		if (listSaleprog.size() > 0) 
		{
			for(int i=0;i<listSaleprog.size();i++)
			{
				Object[] obj = (Object[]) listSaleprog.get(i);
				dblSaleInProgressAmount += Double.parseDouble(obj[1].toString());
				Map jOb=new HashMap();
				jOb.put("0", obj[0].toString());
				jOb.put("1", obj[1].toString());

				listSalesInProgress.add(jOb);

			}
		}
		mapOb.put("0","");
		mapOb.put("1","");
		listSalesInProgress.add(mapOb);
		mapOb=new HashMap();
		mapOb.put("0","Total");
		mapOb.put("1",dblSaleInProgressAmount);
		listSalesInProgress.add(mapOb);
		dblApproxSaleAmount += dblSaleInProgressAmount;
		mapSaleInProgress.put("salesInProg", listSalesInProgress);
		return mapSaleInProgress;
	}

	public Map funFillTableUnsettleBills() throws Exception
	{
		Map mapUnSettleBill =new HashMap();
		List listUnSettleBill=new ArrayList();
		double unSetteledBillAmount = 0.00;

		String sqlUnsettledBillsDina = "select a.strBillNo,c.strTableName,a.dblGrandTotal "
				+ " from tblbillhd a,tbltablemaster c "
				+ " where  date(a.dteBillDate)='" + strPOSDate+ "' "
				+ " and a.strTableNo=c.strTableNo and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) "
				+ " and a.strPOSCode='" + strPOSCode + "'";

		List listUnsettledBills = objBaseService.funGetList(new StringBuilder(sqlUnsettledBillsDina), "sql");
		if (listUnsettledBills.size() > 0) 
		{
			for(int i=0;i<listUnsettledBills.size();i++)
			{
				Object[] obj = (Object[]) listUnsettledBills.get(i);
				unSetteledBillAmount += Double.parseDouble(obj[2].toString());
				Map jb=new HashMap();
				jb.put("0",obj[0].toString());
				jb.put("1",obj[1].toString());
				jb.put("2",obj[2].toString());

				listUnSettleBill.add(jb);
			}
		}

		String sqlUnsettledBillDirectBiller = "select a.strBillNo,a.dblGrandTotal "
				+ " from tblbillhd a "
				+ " where a.strTableNo='' and  date(a.dteBillDate)='" + strPOSDate + "' "
				+ " and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) "
				+ " and a.strPOSCode='" + strPOSCode + "'";

		List listUnBillsDirectBiller = objBaseService.funGetList(new StringBuilder(sqlUnsettledBillDirectBiller), "sql");
		if (listUnBillsDirectBiller.size() > 0) 
		{
			for(int i=0;i<listUnBillsDirectBiller.size();i++)
			{
				Object[] obj = (Object[]) listUnBillsDirectBiller.get(i);
				unSetteledBillAmount +=  Double.parseDouble(obj[1].toString());
				Map jb=new HashMap();
				jb.put("0",obj[0].toString());
				jb.put("1","Direct Biller");
				jb.put("2",obj[1].toString());

				listUnSettleBill.add(jb);

			}
		}

		Map mapOb=new HashMap();
		mapOb.put("0","");
		mapOb.put("1","");
		mapOb.put("2","");
		listUnSettleBill.add(mapOb);
		mapOb=new HashMap();
		mapOb.put("0","Total");
		mapOb.put("1","");
		mapOb.put("2",unSetteledBillAmount);
		listUnSettleBill.add(mapOb);

		mapUnSettleBill.put("jArrUnSettle", listUnSettleBill);

		dblApproxSaleAmount += unSetteledBillAmount;
		mapUnSettleBill.put("ApproxSaleAmount", dblApproxSaleAmount);

		return mapUnSettleBill;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/StartDayProcess",  method = RequestMethod.GET)
	public @ResponseBody Map funDayEndStart(HttpServletRequest req)
	{
		String strShiftNo="1";

		Map mapDayStart=new HashMap();
		mapDayStart.put("DayStart", "Day Not Start");

		try{
			String gShifts=req.getSession().getAttribute("gShifts").toString();
			strPOSCode=req.getSession().getAttribute("loginPOS").toString();
			if (gShifts.equalsIgnoreCase("true"))
			{
				sql =new StringBuilder("update tbldayendprocess set strShiftEnd='N' "
						+ "where strPOSCode='" + strPOSCode + "' and strDayEnd='N' and strShiftEnd=''");

				objBaseService.funExecuteUpdate(sql.toString(), "sql");
				sql.setLength(0);
				sql.append("select count(intShiftCode) from tblshiftmaster where strPOSCode='" + strPOSCode + "'");
				List listShiftNoCount =objBaseService.funGetList(sql, "sql");
				if(listShiftNoCount.size()>0){

					int shiftCount =Integer.parseInt(((Object)listShiftNoCount.get(0)).toString());
					if (shiftCount > 0)
					{
						if (shiftNo == shiftCount)
						{
							shiftNo = 1;
						}
					}

				}
				req.getSession().setAttribute("gShiftEnd","N");
				req.getSession().setAttribute("gDayEnd","N");
				req.getSession().setAttribute("gShiftNo",shiftNo);
				mapDayStart.put("DayStart", "Shift Started Successfully");
			}else{

				int shiftNo=Integer.parseInt(strShiftNo);
				String sql = "update tbldayendprocess set strShiftEnd='N' "
						+ "where strPOSCode='" + strPOSCode + "' and strDayEnd='N' and strShiftEnd=''";
				objBaseService.funExecuteUpdate(sql,"sql");
				if (shiftNo == 0)
				{
					shiftNo++;
				}
				sql = "update tbldayendprocess set intShiftCode= " + shiftNo + " "
						+ "where strPOSCode='" + strPOSCode + "' and strShiftEnd='N' and strDayEnd='N'";
				objBaseService.funExecuteUpdate(sql,"sql");

				req.getSession().setAttribute("gShiftEnd","N");
				req.getSession().setAttribute("gDayEnd","N");
				req.getSession().setAttribute("gShiftNo",shiftNo);
				mapDayStart.put("DayStart", "Day Started Successfully");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return mapDayStart;//new ModelAndView("frmPOSDayEndProcess");//return mapDayStart; //new clsDayEndProcessBean();
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/CheckBillSettleBusyTable",  method = RequestMethod.GET)
	public @ResponseBody Map funCheckBillSettleBusyTable(HttpServletRequest req)
	{
		Map mapObj=new HashMap();
		try{

			String strPOSCode=req.getSession().getAttribute("gPOSCode").toString();
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
			boolean pendingBills,busyTable;
			pendingBills=objPOSDayEndUtility.funCheckPendingBills(strPOSCode, strPOSDate);
			busyTable=objPOSDayEndUtility.funCheckTableBusy(strPOSCode);
			mapObj.put("PendingBills", pendingBills);
			mapObj.put("BusyTables", busyTable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return mapObj; 
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/EndDayProcess",  method = RequestMethod.GET)
	public @ResponseBody Map funDayEndProcess(@RequestParam("emailReport") String EmailReport, HttpServletRequest req,HttpServletResponse resp)
	{

		userCode=req.getSession().getAttribute("gUserCode").toString();
		strClientCode=req.getSession().getAttribute("gClientCode").toString();
		strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		String shiftNo="1";

		String shiftEnd="", DayEnd="", strShiftNo="";
		emailReport=EmailReport;
		gTransactionType = "ShiftEnd";// Declared in main menu in spos
		try{
			String gEnableShiftYN = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");

			if (gEnableShiftYN.equalsIgnoreCase("Y"))//gEnableShiftYN.equalsIgnoreCase("Y")
			{
				funDoShiftEnd(strClientCode,strPOSCode,strPOSDate,userCode,req,resp);//for shift enable
			}
			else
			{
				funShiftEnd(strClientCode,strPOSCode,strPOSDate,userCode,req,resp);//for shift disable
			}

			req.getSession().setAttribute("gDayEnd","Y");
			mapDayEndReturn.put("msg", "Succesfully Day End");
		}catch(Exception e){
			mapDayEndReturn.put("msg", "Day End Not Done");
		}
		

		return mapDayEndReturn;
	}

	private Map funDoShiftEnd(String strClientCode,String strPOSCode,String POSDate,String strUserCode,HttpServletRequest req,HttpServletResponse resp)
	{
		String sql="";
		try
		{
			sql = "delete from tblitemrtemp where strTableNo='null'";
			objBaseService.funExecuteUpdate(sql,"sql");

			gDayEndReportForm = "DayEndReport";

			boolean doCheckPendingOperations = true;
			String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");
			String gLockDataOnShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gLockDataOnShiftYN");
			if (gEnableShiftYN.equals("Y") && gLockDataOnShiftYN.equals("Y"))
			{
				//if shift is enable and  shift live data to q is enable(gLockDataOnShiftYN==true)
				// check pending bills and kots
				doCheckPendingOperations = true;
			}
			else if (gEnableShiftYN.equals("Y") && !gLockDataOnShiftYN.equals("Y"))
			{
				//if shift is enable and don't shift live data to q is enablegLockDataOnShiftYN==false)
				//don't check pending bills and kots
				doCheckPendingOperations = false;
			}
			if (objPOSDayEndUtility.funCheckPendingBills(strPOSCode, strPOSDate))
			{
				//JOptionPane.showMessageDialog(this, "Please settle pending bills");
				//mapDayEndReturn.put("", "");
				mapDayEndReturn.put("msg","Please settle pending bills");
				return mapDayEndReturn;
			}
			else if (objPOSDayEndUtility.funCheckTableBusy(strPOSCode))
			{
				mapDayEndReturn.put("msg","Sorry Tables are Busy Now");
				return mapDayEndReturn;
			}
			else if (objPOSDayEndUtility.isCheckedInMembers(strPOSCode))
			{
				mapDayEndReturn.put("msg","Please check out the playzone members.");
				return mapDayEndReturn;
			}
			else
			{
				String sqlShift = "select date(max(dtePOSDate)),intShiftCode"
						+ " from tbldayendprocess where strPOSCode='" + strPOSCode + "' and strDayEnd='N'"
						+ " and (strShiftEnd='' or strShiftEnd='N')";

				List listShiftNo=objBaseService.funGetList(new StringBuilder(sqlShift), "sql");
				if(listShiftNo.size()>0)
				{
					for(int i=0;i<listShiftNo.size();i++)
					{
						Object[] ob=(Object[])listShiftNo.get(i);
						shiftNo = Integer.parseInt(ob[1].toString());		
					}

				}

				String gDayEnd=req.getSession().getAttribute("gDayEnd").toString();
				if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
				{

					//database backup
					String backupFilePath = "";
					if ("Windows".equalsIgnoreCase("Windows"))//clsPosConfigFile.gPrintOS.equalsIgnoreCase("Windows"))
					{
						backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);
					}                        
					sql = "update tbltablemaster set strStatus='Normal' "
							+ " where strPOSCode='" + strPOSCode + "' ";
					objBaseService.funExecuteUpdate(sql,"sql");

					sql = "update tbldayendprocess set strShiftEnd='Y'"
							+ " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'";
					objBaseService.funExecuteUpdate(sql,"sql");

					//generate MI
					String gGenrateMI = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gGenrateMI");

					if (gGenrateMI.equalsIgnoreCase("Y"))
					{
						//frmGenrateMallInterfaceText objGenrateMallInterfaceText = new frmGenrateMallInterfaceText();
						//  objGenrateMallInterfaceText.funWriteToFile(posDate, posDate, "Current", "Y");
					}

					objPOSDayEndUtility.funGetNextShiftNoForShiftEnd(strPOSCode, shiftNo,strClientCode,strUserCode,req);
					//btnShiftEnd.setEnabled(false);
					String filePath = System.getProperty("user.dir");
					filePath = filePath + "/Temp/Temp_DayEndReport.txt";
					//ption = JOptionPane.showConfirmDialog(this, "Do You Want To Email Reports?");
					if (emailReport.equals("Y"))
					{
						funSendDayEndReports(strClientCode,strPOSCode,POSDate,req,resp);
					}
					else
					{
						//delete old reports
						funCreateReportFolder();             
					}
					//send mail sales amount after shift end
					String sqlPOSData="select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
							+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';";

					List list=objBaseService.funGetList(new StringBuilder(sqlPOSData), "sql");
					String strPOSName="All";// by default
					if(list.size()>0)
					{

						Object ob[]=(Object[])list.get(0);
						strPOSDate= ob[1].toString().split("// ")[0];
						strPOSName= ob[0].toString();

					}
					objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
					if ("Windows".equalsIgnoreCase("Windows"))
					{

						objUtilityController.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
					}                        

					//System.exit(0);
				}
			}
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

		return mapDayEndReturn;
	}

	private Map funShiftEnd(String strClientCode,String strPOSCode,String strPOSDate,String strUserCode,HttpServletRequest req,HttpServletResponse resp)
	{
		try
		{
			String sqld = "delete from tblitemrtemp where strTableNo='null'";
			objBaseService.funExecuteUpdate(sqld,"sql" );

			gDayEndReportForm = "DayEndReport";
			mapDayEndReturn.put("gDayEndReportForm","DayEndReport");
			if (objPOSDayEndUtility.funCheckPendingBills(strPOSCode,strPOSDate))
			{
				//JOptionPane.showMessageDialog(this, "Please settle pending bills");
				mapDayEndReturn.put("msg","Please settle pending bills");
				return mapDayEndReturn;

			}
			else if (objPOSDayEndUtility.funCheckTableBusy(strPOSCode))
			{
				//JOptionPane.showMessageDialog(this, "Sorry Tables are Busy Now");
				mapDayEndReturn.put("msg","Sorry Tables are Busy Now");
				return mapDayEndReturn;
			}
			else
			{
				String sqlShift = "select date(max(dtePOSDate)),intShiftCode"
						+ " from tbldayendprocess where strPOSCode='" + strPOSCode + "' and strDayEnd='N'"
						+ " and (strShiftEnd='' or strShiftEnd='N')";
				List listShiftNo=objBaseService.funGetList(new StringBuilder(sqlShift), "sql");
				if(listShiftNo.size()>0)
				{	
					for(int i=0;i<listShiftNo.size();i++)
					{
						Object[] ob=(Object[])listShiftNo.get(i);
						shiftNo = Integer.parseInt(ob[1].toString());		
					}

				}
				else
				{
					shiftNo++;
				}
				String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");
				String gDayEnd="N";
				if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
				{

					// int option = JOptionPane.showConfirmDialog(this, "Do you want to End Day?");

					String backupFilePath = "";
					if ("Windows".equalsIgnoreCase("Windows"))//clsPosConfigFile.gPrintOS.equalsIgnoreCase("Windows"))
					{
						backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);//funBackupDatabase
					}                        

					String  sqlEx = "update tbltablemaster set strStatus='Normal' "
							+ " where strPOSCode='" + strPOSCode + "' ";
					objBaseService.funExecuteUpdate(sqlEx, "sql");

					sqlEx = "update tbldayendprocess set strShiftEnd='Y'"
							+ " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'";
					objBaseService.funExecuteUpdate(sqlEx, "sql");

					//clsGlobalVarClass.dbMysql.execute(sql);
					objPOSDayEndUtility.funGetNextShiftNo(strPOSCode, shiftNo,strClientCode,strUserCode,req);
					// btnShiftEnd.setEnabled(false);

					// new clsManagersReport().funGenerateManagersReport(posDate, posDate, clsGlobalVarClass.gPOSCode);                        

					String filePath = System.getProperty("user.dir");
					filePath = filePath + "/Temp/Temp_DayEndReport.txt";
					//** Need to validate Email report need   	               
					if (emailReport.equals("Y"))
					{
						funSendDayEndReports(strClientCode,strPOSCode,strPOSDate,req,resp);
					}
					else
					{
						//delete old reports
						funCreateReportFolder();             
					}

					// send client code & pos code for getting global variable
					String sqlPOSData="select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
							+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';";
					List list=objBaseService.funGetList(new StringBuilder(sqlPOSData), "sql");
					String strPOSName="All";// by default
					if(list.size()>0)
					{
						Object ob[]=(Object[])list.get(0);
						strPOSDate= ob[1].toString().split("// ")[0];
						strPOSName= ob[0].toString();
					}
					objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);

					if ("Windows".equalsIgnoreCase("Windows"))// by default it windows
					{
						//objUtilityController.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
					}                       
					//objUtility = null;

					//  System.exit(0);
				}

			}
		}
		catch (Exception e)
		{
			//   objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		return mapDayEndReturn;
	}

	private void funSendDayEndReports(String strClientCode,String strPOSCode,String strPOSDate,HttpServletRequest req,HttpServletResponse resp)
	{              
		try
		{      
			if(strPOSName.equals("")){
				strPOSName=objUtilityController.funGetPOSName(strPOSCode);   
			}
			funCreateReportFolder();                
			//Get selected report from db to send mail
			List list=objBaseService.funGetList(new StringBuilder("select  strPOSCode,strReportName,date(dtePOSDate) "
					+ "from tbldayendreports "
					+ "where strPOSCode='" + strPOSCode + "' "
					+ "and strClientCode='" + strClientCode + "' "),"sql");
			if(list.size()>0)
			{
				String rpName="";
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[]) list.get(i);
					rpName=ob[1].toString();
					funGenerateReport(strPOSCode,strPOSName,strPOSDate,strPOSDate,rpName,req,resp);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}        

		funCreateZipFolder();
		String filePath = System.getProperty("user.dir");
		String zipFile = filePath + "/DayEndMailReports.zip";

		try
		{
			String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
			String strPOSName=objUtilityController.funGetPOSName(strPOSCode);
			objSendMail.funSendMail(gReceiverEmailIds, zipFile,strClientCode,strPOSCode,strPOSName,strPOSDate);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}	


	private void funCreateReportFolder()
	{
		try
		{
			String filePath = System.getProperty("user.dir");
			File file = new File(filePath + "/DayEndMailReports");

			System.out.println("reports path=" + file.toPath());
			if (file.exists())
			{
				// Get all files in the folder
				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++)
				{
					// Delete each file in the folder
					files[i].delete();
				}
				// Delete the folder
				// file.delete();
			}
			else
			{
				file.mkdir();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void funCreateZipFolder()
	{
		try
		{
			String filePath = System.getProperty("user.dir");
			String zipFile = filePath + "/DayEndMailReports.zip";
			File dir = new File(filePath + "/DayEndMailReports");

			//create byte buffer
			byte[] buffer = new byte[1024];

			//create object of FileOutputStream
			FileOutputStream fout = new FileOutputStream(zipFile);
			//create object of ZipOutputStream from FileOutputStream
			ZipOutputStream zout = new ZipOutputStream(fout);
			//check to see if this directory exists
			if (!dir.isDirectory())
			{
				System.out.println(dir.getName() + " is not a directory");
			}
			else
			{
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					//create object of FileInputStream for source file
					FileInputStream fin = new FileInputStream(files[i]);
					zout.putNextEntry(new ZipEntry(files[i].getName()));
					/*
					 * After creating entry in the zip file, actually
					 * write the file.
					 */
					int length;
					while ((length = fin.read(buffer)) > 0)
					{
						zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					//close the InputStream
					fin.close();
				}
			}
			//close the ZipOutputStream
			zout.close();
			System.out.println("Zip file has been created!");
		}
		catch (Exception ioe)
		{
			System.out.println("IOException :" + ioe);
		}
	}


	private void funGenerateReport(String posCode,String posName,String fromDate,String toDate,String reportName,HttpServletRequest req,HttpServletResponse resp)
	{
		try
		{
			//String posCode= req.getSession().getAttribute("loginPOS").toString();
			posName = objUtilityController.funGetPOSName(posCode);
			String strDocType="xls";
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();

			String strFromDate=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
			String strToDate=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
			clsPOSReportBean objBean = new clsPOSReportBean();
			//objBean.setStrPosName(posName);
			objBean.setStrDocType(strDocType);
			objBean.setFromDate(strFromDate);
			objBean.setToDate(strToDate);
			objBean.setStrPOSName(posName);
			objBean.setStrPOSCode(posCode);
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
				obj.setFromDate(fromDate);
				obj.setToDate(toDate);
				obj.setUserCode("Detail");
				obj.setPosCode(posCode);
				obj.setUserName("All");
				objPOSCashManagementFlashController.funReport(obj,resp,req,"DayEndMail");
			}
			else if (reportName.equalsIgnoreCase("Audit Flash".toUpperCase()))
			{
				objBean.setStrReasonMaster("ALL");
				objBean.setStrPSPCode("Modified Bill");
				objBean.setStrSort("Bill");
				objBean.setStrType("All");
				objBean.setStrPOSName(posCode);
				objPOSAuditFlashController.funExportReportForDayEndMail(objBean,resp,req);
			}
			else if (reportName.equalsIgnoreCase("Advance Order Flash".toUpperCase()))
			{

			}
			else if (reportName.equalsIgnoreCase("Stock In Out Flash".toUpperCase()))
			{

			}
			else if (reportName.equalsIgnoreCase("Day End Flash".toUpperCase()))
			{
				objPOSDayEndFlashReportController.funExportReportForDayEndMail(objBean, resp, req);
			}
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
			else if (reportName.equalsIgnoreCase("DebitCardFlashReports".toUpperCase()))
			{

			}
			else if (reportName.equalsIgnoreCase("Promotion Flash".toUpperCase()))
			{

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
			else if (reportName.equalsIgnoreCase("Loyalty Point Report".toUpperCase()))
			{

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
			else if (reportName.equalsIgnoreCase("Order Analysis Report".toUpperCase()))
			{
				funGenerateOrderAnalysisExcelReport(posCode, posName, fromDate, toDate);
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
			else if (reportName.equalsIgnoreCase("DeliveryboyIncentive".toUpperCase()))
			{
				funGenerateDeliveryBoyIncentivesExcelReport(posCode, posName, fromDate, toDate);
			}
			else if (reportName.equalsIgnoreCase("Sales Summary Flash".toUpperCase()))
			{
				objBean.setStrPayMode("ALL");
				objPOSSalesSummaryFlashController.funReport(objBean, resp, req);
			}
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
			else if (reportName.equalsIgnoreCase("UnusedCardBalanceReport".toUpperCase()))
			{
				funGenerateUnUsedCardBalanceExcelReport(posCode, posName, fromDate, toDate);
			}
			else if (reportName.equalsIgnoreCase("DayWiseSalesSummaryFlash".toUpperCase()))
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
			}
			else if (reportName.equalsIgnoreCase("Revenue Head Wise Item Sales".toUpperCase()))
			{
				objBean.setStrRevenueHead("ALL");
				objPOSRevenueHeadSalesReportController.funReport(objBean, resp, req,"DayEndMail");
			}
			else if (reportName.equalsIgnoreCase("Managers Report".toUpperCase()))
			{

			}
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
			else if (reportName.equalsIgnoreCase("Posting Report".toUpperCase()))
			{

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private void funGenerateOrderAnalysisExcelReport(String posCode, String posName, String fromDate, String toDate)
	{
		try
		{

			Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
			List<String> arrListTotal = new ArrayList<String>();
			List<String> arrHeaderList = new ArrayList<String>();
			double totalNCQty = 0;
			double totalCompQty = 0;
			double totalSaleQty = 0;
			double totalVoidQty = 0;
			double totalAmt = 0;
			double totalDis = 0;
			double totalCostVal = 0;
			double totalVoidKOTQty = 0;
			double totalPer = 0;

			clsOrderAnalysisColumns objOrderAnalysis = null;
			Map<String, clsOrderAnalysisColumns> hmOrderAnalysisData = new HashMap<String, clsOrderAnalysisColumns>();
			StringBuilder sbSql = new StringBuilder();
			sbSql.setLength(0);
			sbSql.append("select c.strItemCode,c.strItemName,b.dblRate,sum(b.dblQuantity),b.strKOTNo"
					+ ",(b.dblRate*sum(b.dblQuantity)) TotalAmt,c.dblPurchaseRate,a.dblDiscountAmt "
							+ "from tblbillhd a,tblbilldtl b,tblitemmaster c "
							+ "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
							+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='" + posCode + "' ");
			}
			sbSql.append("group by c.strItemCode order by c.strItemName; ");

			List list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);

					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setSaleQty((objOrderAnalysis.getSaleQty() + Double.parseDouble(ob[3].toString())));
					}
					else
					{
						objOrderAnalysis = new clsOrderAnalysisColumns();
						objOrderAnalysis.setSaleQty(Double.parseDouble(ob[3].toString()));
					}
					objOrderAnalysis.setItemCode(ob[0].toString());
					objOrderAnalysis.setItemName(ob[1].toString());
					objOrderAnalysis.setItemSaleRate(Double.parseDouble(ob[2].toString()));
					objOrderAnalysis.setItemPurchaseRate(Double.parseDouble(ob[6].toString()));
					objOrderAnalysis.setKOTNo(ob[4].toString());
					objOrderAnalysis.setNCQty(0);
					objOrderAnalysis.setTotalDiscountAmt(Double.parseDouble(ob[7].toString()));
					objOrderAnalysis.setVoidKOTQty(0);
					objOrderAnalysis.setVoidQty(0);
					double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
					objOrderAnalysis.setFinalItemQty(finalQty);
					objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * Double.parseDouble(ob[2].toString()));
					objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * Double.parseDouble(ob[6].toString()));

					hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);

				}
			}
			sbSql.setLength(0);
			sbSql.append("select c.strItemCode,c.strItemName,b.dblRate,sum(b.dblQuantity),b.strKOTNo"
					+ ",(b.dblRate*sum(b.dblQuantity)) TotalAmt,c.dblPurchaseRate,a.dblDiscountAmt "
							+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster c "
									+ "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
											+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='" + posCode + "' ");
			}
			sbSql.append("group by c.strItemCode order by c.strItemName; ");

			list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setSaleQty((objOrderAnalysis.getSaleQty() + Double.parseDouble(ob[3].toString())));
					}
					else
					{
						objOrderAnalysis = new clsOrderAnalysisColumns();
						objOrderAnalysis.setSaleQty(Double.parseDouble(ob[3].toString()));
					}
					objOrderAnalysis.setItemCode(ob[0].toString());
					objOrderAnalysis.setItemName(ob[1].toString());
					objOrderAnalysis.setItemSaleRate(Double.parseDouble(ob[2].toString()));
					objOrderAnalysis.setItemPurchaseRate(Double.parseDouble(ob[6].toString()));
					objOrderAnalysis.setKOTNo(ob[4].toString());
					objOrderAnalysis.setNCQty(0);
					objOrderAnalysis.setTotalDiscountAmt(Double.parseDouble(ob[7].toString()));
					objOrderAnalysis.setVoidKOTQty(0);
					objOrderAnalysis.setVoidQty(0);
					double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
					objOrderAnalysis.setFinalItemQty(finalQty);
					objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * Double.parseDouble(ob[2].toString()));
					objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * Double.parseDouble(ob[6].toString()));

					hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);

				}
			}

			sbSql.setLength(0);
			sbSql.append("select a.strItemCode,sum(a.dblQuantity) "
					+ "from tblnonchargablekot a "
					+ "where date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='" + posCode + "' ");
			}
			sbSql.append("group by a.strItemCode");

			list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setNCQty(Double.parseDouble(ob[1].toString()));
						double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
						objOrderAnalysis.setFinalItemQty(finalQty);
						objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemSaleRate());
						objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemPurchaseRate());
						hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);
					}
				}
			}
			System.out.println(sbSql);

			sbSql.setLength(0);
			sbSql.append("select a.strItemCode,sum(a.intQuantity) "
					+ "from tblvoidbilldtl a "
					+ "where a.dteBillDate between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='' ");
			}
			sbSql.append("group by a.strItemCode");

			list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setVoidQty(Double.parseDouble(ob[1].toString()));
						double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
						objOrderAnalysis.setFinalItemQty(finalQty);
						objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemSaleRate());
						objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemPurchaseRate());
						hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);
					}
				}
			}

			sbSql.setLength(0);
			sbSql.append("select a.strItemCode,sum(a.dblItemQuantity) "
					+ "from tblvoidkot a,tblitemmaster b "
							+ "where a.strItemCode=b.strItemCode and date(a.dteVoidedDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='" + posCode + "' ");
			}
			sbSql.append("group by a.strItemCode");

			list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setVoidKOTQty(Double.parseDouble(ob[1].toString()));
						double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
						objOrderAnalysis.setFinalItemQty(finalQty);
						objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemSaleRate());
						objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemPurchaseRate());
						hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);

					} 		
				}
			}
			System.out.println(sbSql);

			sbSql.setLength(0);
			sbSql.append("select b.stritemcode,sum(b.dblQuantity)  "
					+ " from tblbillhd a,tblbilldtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e  ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h  "
							+ " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode  "
							+ " and a.strPOSCode=e.strPosCode and b.strItemCode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode  "
							+ " and g.strGroupCode=h.strGroupCode and d.strSettelmentType='Complementary'  "
							+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

			if (!posCode.equals("All"))
			{
				sbSql.append("and a.strPOSCode='" + posCode + "' ");
			}
			sbSql.append("group by b.strItemCode,e.strPOSName order by a.dteBillDate ");

			list=objBaseService.funGetList(sbSql, "sql");
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					if (null != hmOrderAnalysisData.get(ob[0].toString()))
					{
						objOrderAnalysis = hmOrderAnalysisData.get(ob[0].toString());
						objOrderAnalysis.setCompQty(Double.parseDouble(ob[0].toString()));
						double finalQty = (objOrderAnalysis.getSaleQty() - (objOrderAnalysis.getVoidQty() + objOrderAnalysis.getVoidKOTQty()));
						objOrderAnalysis.setFinalItemQty(finalQty);
						objOrderAnalysis.setTotalAmt(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemSaleRate());
						objOrderAnalysis.setTotalCostValue(objOrderAnalysis.getSaleQty() * objOrderAnalysis.getItemPurchaseRate());
						hmOrderAnalysisData.put(ob[0].toString(), objOrderAnalysis);
					}

				}
			}
			objBaseService.funExecuteUpdate("truncate table tblorderanalysis","sql");
			double totalSaleAmt = 0, totalCostValue = 0;
			for (Map.Entry<String, clsOrderAnalysisColumns> entry : hmOrderAnalysisData.entrySet())
			{

				clsOrderAnalysisColumns objOrderAnalysis1 = entry.getValue();
				sbSql.setLength(0);
				sbSql.append("insert into tblorderanalysis values('" + objOrderAnalysis1.getItemName() + "'"
						+ ",'" + objOrderAnalysis1.getItemSaleRate() + "','" + objOrderAnalysis1.getSaleQty() + "'"
						+ ",'" + objOrderAnalysis1.getNCQty() + "','" + objOrderAnalysis1.getVoidQty() + "'"
						+ ",'" + objOrderAnalysis1.getVoidKOTQty() + "','" + objOrderAnalysis1.getKOTNo() + "'"
						+ ",'" + objOrderAnalysis1.getItemPurchaseRate() + "','" + objOrderAnalysis1.getTotalAmt() + "'"
						+ ",'" + objOrderAnalysis1.getTotalCostValue() + "','" + objOrderAnalysis1.getTotalDiscountAmt() + "'"
						+ ",'" + userCode + "','0','0','" + objOrderAnalysis1.getCompQty() + "')");
				System.out.println(objOrderAnalysis1.getItemCode() + "\t" + sbSql);

				objBaseService.funExecuteUpdate(sbSql.toString(),"sql");

				totalSaleAmt += objOrderAnalysis1.getTotalAmt();
				totalCostValue += objOrderAnalysis1.getTotalCostValue();
			}

			String pattern = "###.##";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			for (Map.Entry<String, clsOrderAnalysisColumns> entry : hmOrderAnalysisData.entrySet())
			{

				clsOrderAnalysisColumns objOrderAnalysis1 = entry.getValue();
				sbSql.setLength(0);
				double per = 0;
				if (totalSaleAmt > 0)
				{
					per = Double.parseDouble(decimalFormat.format((objOrderAnalysis1.getSaleQty() / totalSaleAmt) * 100));
				}
				double costValuePer = 0;
				if (totalCostValue > 0)
				{
					costValuePer = Double.parseDouble(decimalFormat.format((objOrderAnalysis1.getSaleQty() / totalCostValue) * 100));
				}
				sbSql.append("update tblorderanalysis set strField13='" + per + "',strField14='" + costValuePer + "' "
						+ "where strField1='" + objOrderAnalysis1.getItemName() + "'");
				objBaseService.funExecuteUpdate(sbSql.toString(),"sql");
			}

			sql =new StringBuilder("select strField1 as ItemName,strField2 as ItemRate,strField3 as SaleQty "
					+ " ,strField4 as NCQty,strField5 as VoidQty,strField6 as VoidKOTQty,strField7 "
					+ " ,strField8 as dblPurchaseRate ,strField9 as TotalAmount, "
					+ " strField10 as TotalCostValue,strField11 as TotalDiscount,strField13 as TotalAmtPer "
					+ " ,strField14 as TotalCostValuePer,strField15 as CompQty "
					+ " from tblorderanalysis ");

			List list1=objBaseService.funGetList(sql, "sql");
			if(list1.size()>0)
			{
				int j=0;
				for(int i=0;i<list1.size();i++)
				{
					Object[] ob=(Object[])list1.get(i);
					List<String> arrListItem = new ArrayList<String>();
					arrListItem.add(ob[0].toString());  // Item Name
					arrListItem.add(ob[1].toString());  // Rate
					arrListItem.add(ob[2].toString());  // Sale Qty
					arrListItem.add(ob[3].toString());  // NC Qty 
					arrListItem.add(ob[4].toString());  // Void Qty 
					arrListItem.add(ob[5].toString());  // Void KOT Qty
					arrListItem.add(ob[13].toString());// Comp Qty
					arrListItem.add(ob[7].toString());  // Purchase Rate 
					arrListItem.add(ob[8].toString());  // Total Amt                     
					arrListItem.add(ob[10].toString()); // Total Discount
					Double totalAmtPer = (Double.parseDouble(ob[11].toString()) * 100);
					arrListItem.add(String.valueOf(totalAmtPer)); // Total Amt Per
					arrListItem.add(ob[9].toString());  // Total Cost Value                     
					arrListItem.add(ob[12].toString()); // Total Cost Value Per                    

					totalNCQty = totalNCQty + Double.parseDouble(ob[3].toString());
					totalSaleQty = totalSaleQty + Double.parseDouble(ob[2].toString());
					totalVoidQty = totalVoidQty + Double.parseDouble(ob[4].toString());
					totalVoidKOTQty = totalVoidKOTQty + Double.parseDouble(ob[5].toString());
					totalCompQty = totalCompQty + Double.parseDouble(ob[13].toString());
					totalAmt = totalAmt + Double.parseDouble(ob[8].toString());
					totalDis = totalDis + Double.parseDouble(ob[10].toString());
					totalCostVal = totalCostVal + Double.parseDouble(ob[9].toString());

					totalPer = totalPer + (Double.parseDouble(ob[11].toString()) * 100);
					mapExcelItemDtl.put(j, arrListItem);
					j++;

				}
			}

			arrListTotal.add(String.valueOf(Math.rint(totalSaleQty)) + "#" + "3");
			arrListTotal.add(String.valueOf(Math.rint(totalNCQty)) + "#" + "4");
			arrListTotal.add(String.valueOf(Math.rint(totalVoidQty)) + "#" + "5");
			arrListTotal.add(String.valueOf(Math.rint(totalVoidKOTQty)) + "#" + "6");
			arrListTotal.add(String.valueOf(Math.rint(totalCompQty)) + "#" + "7");
			arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "9");
			arrListTotal.add(String.valueOf(Math.rint(totalDis)) + "#" + "10");
			arrListTotal.add(String.valueOf(Math.rint(totalPer)) + "#" + "11");
			arrListTotal.add(String.valueOf(Math.rint(totalCostValue)) + "#" + "12");

			arrHeaderList.add("Serial No");
			arrHeaderList.add("Item Name");
			arrHeaderList.add("Rate");
			arrHeaderList.add("Sale Qty");
			arrHeaderList.add("NC Qty");
			arrHeaderList.add("Void Qty");
			arrHeaderList.add("Void KOT");
			arrHeaderList.add("Comp Qty");
			arrHeaderList.add("Purchase rate");
			arrHeaderList.add("Total Amount");
			arrHeaderList.add("Total Discount");
			arrHeaderList.add("% To Total");
			arrHeaderList.add("Total Cost Value");
			arrHeaderList.add("Food Cost");

			List<String> arrparameterList = new ArrayList<String>();
			arrparameterList.add("Order Analysis Report");
			arrparameterList.add("POS" + " : " + posName);
			arrparameterList.add("FromDate" + " : " + fromDate);
			arrparameterList.add("ToDate" + " : " + toDate);
			arrparameterList.add(" ");
			arrparameterList.add(" ");

			funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "orderAnalysisExcelSheet");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void funGenerateDeliveryBoyIncentivesExcelReport(String posCode, String posName, String fromDate, String toDate)
	{
		try
		{
			Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
			List<String> arrListTotal = new ArrayList<String>();
			List<String> arrHeaderList = new ArrayList<String>();
			double totalAmount = 0;
			double totalQty = 0;
			double totalIncentiveAmt = 0;

			String dpCode = "All";

			//	            if (cmbReportMode.getSelectedItem().equals("Detail"))
			if (true)
			{

				sql = new StringBuilder(" select a.strBillNo,date(c.dteBillDate) as BillDate,TIME_FORMAT(time(dteBillDate),\"%r\") as BillTime "
						+ " ,e.strDPName,b.dblDBIncentives as dblValue,ifnull(h.strBuildingName,'') as Area,ifnull(date(c.dteSettleDate),'') as SettledDate,ifnull(TIME_FORMAT(time(c.dteSettleDate),\"%r\"),'') as SettledTime "
						+ " from tblhomedelivery a,tblhomedeldtl b,vqbillhd c "
						+ " ,tblareawisedelboywisecharges d,tbldeliverypersonmaster e "
						+ " ,tblcustomermaster g "
						+ " left outer join tblbuildingmaster h on g.strBuldingCode=h.strBuildingCode "
						+ " where a.strBillNo=b.strBillNo "
						+ " and a.strBillNo=c.strBillNo "
						+ " and b.strDPCode= d.strDeliveryBoyCode "
						+ " and d.strDeliveryBoyCode=e.strDPCode "
						+ " and c.strCustomerCode=g.strCustomerCode "
						+ " and g.strBuldingCode=d.strCustAreaCode ");
				if (!dpCode.equals("All"))
				{
					sql.append(" b.strDPCode= '" + dpCode + "' ");
				}
				if (!posCode.equals("All"))
				{
					sql.append(" and c.strPOSCode= '" + posCode + "' ");
				}
				sql.append(" and Date(c.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");

				List list=objBaseService.funGetList(sql, "sql");
				int p = 1;
				if(list.size()>0)
				{	
					for(int i=0;i<list.size();i++)
					{
						Object[] ob=(Object[])list.get(i);

						List<String> arrListItem = new ArrayList<String>();
						arrListItem.add(ob[0].toString());
						arrListItem.add(ob[1].toString());
						arrListItem.add(ob[2].toString());
						arrListItem.add(ob[3].toString());
						arrListItem.add(ob[4].toString());
						arrListItem.add(ob[5].toString());
						arrListItem.add(ob[6].toString());

						totalIncentiveAmt = totalIncentiveAmt + Double.parseDouble(ob[4].toString());

						mapExcelItemDtl.put(p, arrListItem);

						p++;

					}
				}
				arrListTotal.add(String.valueOf(Math.rint(totalIncentiveAmt)) + "#" + "5");
				arrHeaderList.add("Serial No");
				arrHeaderList.add("Bill No");
				arrHeaderList.add("Bill Date ");
				arrHeaderList.add("Bill Time");
				arrHeaderList.add("DP Name");
				arrHeaderList.add("Amount");
				arrHeaderList.add("Area");
				arrHeaderList.add("Settle Date");

				List<String> arrparameterList = new ArrayList<String>();
				arrparameterList.add("Delivery Boy Incentives Details Report");
				arrparameterList.add("POS" + " : " + posName);
				arrparameterList.add("FromDate" + " : " + fromDate);
				arrparameterList.add("ToDate" + " : " + toDate);
				arrparameterList.add("DP Name" + " : All");
				arrparameterList.add(" ");

				funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "deleveryBoyIncentiveDtlExcelSheet");

			}
			else
			{
				sql=new StringBuilder(" select a.strBillNo,date(c.dteBillDate) as BillDate, date(f.dteOrderFor) as OrderDate "
						+ " ,f.strDeliveryTime,g.strCustomerName,e.strDPName,c.dblSubTotal,b.dblDBIncentives as dblValue "
						+ " from tblhomedelivery a,tblhomedeldtl b,tblbillhd c "
						+ " ,tblareawisedelboywisecharges d,tbldeliverypersonmaster e,tbladvbookbillhd f "
						+ " ,tblcustomermaster g "
						+ " where a.strBillNo=b.strBillNo "
						+ " and a.strBillNo=c.strBillNo "
						+ " and b.strDPCode= d.strDeliveryBoyCode "
						+ " and d.strDeliveryBoyCode=e.strDPCode "
						+ " and c.strAdvBookingNo=f.strAdvBookingNo "
						+ " and c.strCustomerCode=g.strCustomerCode "
						+ " and g.strBuldingCode=d.strCustAreaCode ");
				if (!dpCode.equals("All"))
				{
					sql.append(" b.strDPCode= '" + dpCode + "' ");
				}
				if (!posCode.equals("All"))
				{
					sql.append(" and c.strPOSCode= '" + posCode + "' ");
				}
				sql.append(" and Date(c.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");

				List list=objBaseService.funGetList(sql, "sql");
				int p = 1;
				if(list.size()>0)
				{	
					for(int i=0;i<list.size();i++)
					{
						Object[] ob=(Object[])list.get(i);
						List<String> arrListItem = new ArrayList<String>();
						arrListItem.add(ob[0].toString());
						arrListItem.add(ob[1].toString());
						arrListItem.add(ob[2].toString());
						arrListItem.add(ob[4].toString());
						arrListItem.add(ob[5].toString());
						arrListItem.add(ob[6].toString());
						arrListItem.add(ob[7].toString());

						totalAmount = totalAmount + Double.parseDouble(ob[6].toString());
						totalIncentiveAmt = totalIncentiveAmt + Double.parseDouble(ob[7].toString());

						mapExcelItemDtl.put(p, arrListItem);

						p++;
					}
				}
				arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "6");
				arrListTotal.add(String.valueOf(Math.rint(totalIncentiveAmt)) + "#" + "7");

				arrHeaderList.add("Serial No");
				arrHeaderList.add("Bill No");
				arrHeaderList.add("Bill Date");
				arrHeaderList.add("Order Date");
				arrHeaderList.add("Customer Name");
				arrHeaderList.add("DP Name");
				arrHeaderList.add("Amount");
				arrHeaderList.add("Incentive Amt");

				List<String> arrparameterList = new ArrayList<String>();
				arrparameterList.add("Delivery Boy Incentives Summary Report");
				arrparameterList.add("POS" + " : " + posName);
				arrparameterList.add("FromDate" + " : " + fromDate);
				arrparameterList.add("ToDate" + " : " + toDate);
				arrparameterList.add("DP Name" + " : All");
				arrparameterList.add(" ");

				funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "deleveryBoyIncentiveSummaryExcelSheet");

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void funGenerateUnUsedCardBalanceExcelReport(String posCode, String posName, String fromDate, String toDate)
	{
		try
		{

			Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
			List<String> arrListTotal = new ArrayList<String>();
			List<String> arrHeaderList = new ArrayList<String>();
			double totalBalance = 0;

			sql =new StringBuilder("select a.dtePOSDate as posDate,a.strCardNo,a.strUserCreated,"
					+ " ifnull(sum(a.dblCardAmt),0.00) as balance "
					+ " from tbldebitcardrevenue a "
					+ " where date(a.dtePOSDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " group by a.dtePOSDate ");

			List list=objBaseService.funGetList(sql, "sql");
			int p = 1;
			if(list.size()>0)
			{	
				for(int i=0;i<list.size();i++)
				{
					Object[] ob=(Object[])list.get(i);
					List<String> arrListItem = new ArrayList<String>();
					arrListItem.add(ob[0].toString());
					arrListItem.add(ob[1].toString());
					arrListItem.add(ob[2].toString());
					arrListItem.add(ob[3].toString());
					arrListItem.add(" ");
					arrListItem.add(" ");
					arrListItem.add(" ");

					totalBalance = totalBalance + Double.parseDouble(ob[3].toString());

					mapExcelItemDtl.put(p, arrListItem);

					p++;
				}
			}
			arrListTotal.add(String.valueOf(Math.rint(totalBalance)) + "#" + "4");

			arrHeaderList.add("Serial No");
			arrHeaderList.add("Card No");
			arrHeaderList.add("User Name");
			arrHeaderList.add("POS Date");
			arrHeaderList.add("Balance");
			arrHeaderList.add(" ");
			arrHeaderList.add(" ");
			arrHeaderList.add("");

			List<String> arrparameterList = new ArrayList<String>();
			arrparameterList.add("Unused Card Balance Report");
			arrparameterList.add("POS" + " : " + posName);
			arrparameterList.add("FromDate" + " : " + fromDate);
			arrparameterList.add("ToDate" + " : " + toDate);
			arrparameterList.add(" ");
			arrparameterList.add(" ");

			funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "unusedCardBalanceExcelSheet");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void funCreateExcelSheet(List<String> parameterList, List<String> headerList, Map<Integer, List<String>> map, List<String> totalList, String fileName)
	{
		String filePath = System.getProperty("user.dir");
		File file = new File(filePath + File.separator + "Reports" + File.separator + fileName + ".xls");
		try
		{
			WritableWorkbook workbook1 = Workbook.createWorkbook(file);//import jxl jar
			WritableSheet sheet1 = workbook1.createSheet("First Sheet", 0);
			WritableFont cellFont = new WritableFont(WritableFont.COURIER, 14);
			cellFont.setBoldStyle(WritableFont.BOLD);
			WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
			WritableFont headerCellFont = new WritableFont(WritableFont.ARIAL, 10);
			headerCellFont.setBoldStyle(WritableFont.BOLD);
			WritableCellFormat headerCell = new WritableCellFormat(headerCellFont);

			for (int j = 0; j <= parameterList.size(); j++)
			{

				Label l0 = new Label(2, 0, parameterList.get(0), cellFormat);
				Label l1 = new Label(0, 2, parameterList.get(1), headerCell);
				Label l2 = new Label(1, 2, parameterList.get(2), headerCell);
				Label l3 = new Label(2, 2, parameterList.get(3), headerCell);
				Label l4 = new Label(0, 3, parameterList.get(4), headerCell);
				Label l5 = new Label(1, 3, parameterList.get(5), headerCell);

				sheet1.addCell(l0);
				sheet1.addCell(l1);
				sheet1.addCell(l2);
				sheet1.addCell(l3);
				sheet1.addCell(l4);
				sheet1.addCell(l5);

			}

			for (int j = 0; j < headerList.size(); j++)
			{
				Label lblHeader = new Label(j, 5, headerList.get(j), headerCell);
				sheet1.addCell(lblHeader);
			}

			int i = 7;
			for (Map.Entry<Integer, List<String>> entry : map.entrySet())
			{
				Label lbl0 = new Label(0, i, entry.getKey().toString());
				List<String> nameList = map.get(entry.getKey());
				for (int j = 0; j < nameList.size(); j++)
				{
					int colIndex = j + 1;
					Label lblData = new Label(colIndex, i, nameList.get(j));
					sheet1.addCell(lblData);
					sheet1.setColumnView(i, 15);
				}
				sheet1.addCell(lbl0);
				i++;
			}

			for (int j = 0; j < totalList.size(); j++)
			{
				String[] l0 = new String[10];
				for (int c = 0; c < totalList.size(); c++)
				{
					l0 = totalList.get(c).split("#");
					int pos = Integer.parseInt(l0[1]);
					Label lable0 = new Label(pos, i + 1, l0[0], headerCell);
					sheet1.addCell(lable0);
				}
				Label labelTotal = new Label(0, i + 1, "TOTAL:", headerCell);
				sheet1.addCell(labelTotal);
			}
			workbook1.write();
			workbook1.close();

			/*if (!dayEnd.equalsIgnoreCase("Yes"))
	    	    {
	    		Desktop dt = Desktop.getDesktop();
	    		dt.open(file);
	    	    }*/
			//	            Desktop dt = Desktop.getDesktop();
			//	            dt.open(file);
		}
		catch (Exception ex)
		{
			//   JOptionPane.showMessageDialog(null, ex.getMessage());
			ex.printStackTrace();
		}
	}


	class clsOrderAnalysisColumns
	{

		private String itemName;

		private String itemCode;

		private String KOTNo;

		private double saleQty;

		private double CompQty;

		private double NCQty;

		private double voidQty;

		private double compliQty;

		private double voidKOTQty;

		private double itemSaleRate;

		private double itemPurchaseRate;

		private double totalAmt;

		private double totalCostValue;

		private double totalDiscountAmt;

		private double finalItemQty;

		public String getItemName()
		{
			return itemName;
		}

		public void setItemName(String itemName)
		{
			this.itemName = itemName;
		}

		public String getItemCode()
		{
			return itemCode;
		}

		public void setItemCode(String itemCode)
		{
			this.itemCode = itemCode;
		}

		public String getKOTNo()
		{
			return KOTNo;
		}

		public void setKOTNo(String KOTNo)
		{
			this.KOTNo = KOTNo;
		}

		public double getSaleQty()
		{
			return saleQty;
		}

		public void setSaleQty(double saleQty)
		{
			this.saleQty = saleQty;
		}

		public double getNCQty()
		{
			return NCQty;
		}

		public void setNCQty(double NCQty)
		{
			this.NCQty = NCQty;
		}

		public double getVoidQty()
		{
			return voidQty;
		}

		public void setVoidQty(double voidQty)
		{
			this.voidQty = voidQty;
		}

		public double getVoidKOTQty()
		{
			return voidKOTQty;
		}

		public void setVoidKOTQty(double voidKOTQty)
		{
			this.voidKOTQty = voidKOTQty;
		}

		public double getItemSaleRate()
		{
			return itemSaleRate;
		}

		public void setItemSaleRate(double itemSaleRate)
		{
			this.itemSaleRate = itemSaleRate;
		}

		public double getItemPurchaseRate()
		{
			return itemPurchaseRate;
		}

		public void setItemPurchaseRate(double itemPurchaseRate)
		{
			this.itemPurchaseRate = itemPurchaseRate;
		}

		public double getTotalAmt()
		{
			return totalAmt;
		}

		public void setTotalAmt(double totalAmt)
		{
			this.totalAmt = totalAmt;
		}

		public double getTotalCostValue()
		{
			return totalCostValue;
		}

		public void setTotalCostValue(double totalCostValue)
		{
			this.totalCostValue = totalCostValue;
		}

		public double getTotalDiscountAmt()
		{
			return totalDiscountAmt;
		}

		public void setTotalDiscountAmt(double totalDiscountAmt)
		{
			this.totalDiscountAmt = totalDiscountAmt;
		}

		public double getFinalItemQty()
		{
			return finalItemQty;
		}

		public void setFinalItemQty(double finalItemQty)
		{
			this.finalItemQty = finalItemQty;
		}

		public double getCompliQty()
		{
			return compliQty;
		}

		public void setCompliQty(double compliQty)
		{
			this.compliQty = compliQty;
		}

		public double getCompQty()
		{
			return CompQty;
		}

		public void setCompQty(double CompQty)
		{
			this.CompQty = CompQty;
		}

	}


	public List funGetJsonArrRowDayEnd(List listArr)
	{
		List listLastJson= new ArrayList();
		for(int i=0;i<listArr.size();i++)
		{

			Map mapOb=(Map) listArr.get(i);
			String str="";
			//JSONArray jArrtmp=new JSONArray();
			ArrayList al=new ArrayList<>();
			for(int j=0;j<11;j++){
				str=String.valueOf(j);
				al.add(mapOb.get(str).toString());
			}
			listLastJson.add(al);
		}
		return listLastJson;
	}
	public List funGetJsonArrRowSettlement(List listArr)
	{
		List listLastJson= new ArrayList();
		for(int i=0;i<listArr.size();i++)
		{

			Map mapOb=(Map) listArr.get(i);
			String str="";
			List listTmp=new ArrayList();
			for(int j=0;j<3;j++){
				str=String.valueOf(j);
				listTmp.add(mapOb.get(str));
			}
			listLastJson.add(listTmp);
		}
		return listLastJson;
	}

	public List funGetJsonArrRowSalesOfProg(List listArr)
	{
		List listLastJson= new ArrayList();
		for(int i=0;i<listArr.size();i++)
		{

			Map mapOb=(Map) listArr.get(i);
			String str="";
			List listTmp=new ArrayList();
			for(int j=0;j<2;j++){
				str=String.valueOf(j);
				listTmp.add(mapOb.get(str));
			}
			listLastJson.add(listTmp);
		}
		return listLastJson;
	}


}
