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

import jxl.Workbook;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;
import com.sanguine.webpos.bean.clsPOSOperatorWiseReportBean;
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
	 
	 boolean pendingBills,busyTable;
	 String strPOSCode="",strPOSName="",strPOSDate="",strClientCode="",userCode="",strShiftNo="";
	 public static JSONObject jsonDayEndReturn=new JSONObject();
	 public static String gTransactionType = "",gDayEndReportForm="";
	 int shiftNo=0,noOfDiscountedBills=0;
	 double sales = 0,cashIn = 0,cashOut = 0,totalSales = 0,totalWithdrawl = 0, 
				totalTransIn = 0,totalTransOuts = 0,totalPayments = 0, totalFloat = 0,
				advCash=0, totalDiscount=0,dblApproxSaleAmount=0;
	 
	 String shiftEnd="",dayEnd="",emailReport="";
	 StringBuilder sql=new StringBuilder();
	 
	
	 @RequestMapping(value = "/frmPOSDayEndProcess", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request,ModelMap modelmap)
		{ 
		 	clsPOSDayEndProcessBean objDayEndProcessBean= new clsPOSDayEndProcessBean();
		 	JSONArray jArrDayEnd=new JSONArray();
		 	JSONObject DayEndProcessData= new JSONObject();
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
			strShiftNo="1";
			
			JSONObject jsDayEnd= new JSONObject();
			JSONObject jsSettlement= new JSONObject();
			JSONObject jsSalesInProg= new JSONObject();
			JSONObject jsUnSettleBill= new JSONObject();
			
			DayEndProcessData=funLoadDayEndData();
			jsDayEnd=(JSONObject) DayEndProcessData.get("DayEnd");
			jsSettlement=(JSONObject) DayEndProcessData.get("Settlement");
			jsSalesInProg=(JSONObject) DayEndProcessData.get("SalesInProg");
			jsUnSettleBill=(JSONObject) DayEndProcessData.get("UnSettleBill");
			
			ArrayList al=new ArrayList<ArrayList<String>>();
			jArrDayEnd=(JSONArray) jsDayEnd.get("DayEndArr");
			objDayEndProcessBean.setjArrDayEnd(jArrDayEnd);
			objDayEndProcessBean.setTotalpax(jsDayEnd.get("totalPax").toString());
			objDayEndProcessBean.setjArrDayEndTotal((JSONArray) jsDayEnd.get("DayEndJArrTot"));
			
			objDayEndProcessBean.setjArrSettlement((JSONArray) jsSettlement.get("jArrSettlement"));
			objDayEndProcessBean.setjArrSettlementTotal((JSONArray) jsSettlement.get("jArrsettlementTot"));
			
			objDayEndProcessBean.setjArrSalesInProg((JSONArray) jsSalesInProg.get("SalesInProgress"));
			
			objDayEndProcessBean.setjArrUnSettlebill((JSONArray) jsUnSettleBill.get("UnSettleBill"));
		
			objDayEndProcessBean.setTotal(jsUnSettleBill.get("total").toString());
			
			
		 	if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSDayEndProcess_1","command", objDayEndProcessBean);
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSDayEndProcess","command", objDayEndProcessBean);
			}else {
				return null;
			}
		}
	 
	public JSONObject funLoadDayEndData()
	{
		JSONObject DayEndDataProcess= new JSONObject();
		JSONObject DayEndData= new JSONObject();
		JSONObject jsSettlement= new JSONObject();
		JSONObject jsSalesInProg= new JSONObject();
		JSONObject jsUnSettleBill= new JSONObject();
		
	
			try{
				JSONObject DayEndDataWS= funFillCurrencyGrid();
				JSONObject SettlementWS= funFillSettlementWiseSalesGrid();
				JSONObject SalesInProgWS= funFillTableSaleInProgress();
				JSONObject UnSettleBillws= funFillTableUnsettleBills();
				 
				 dblApproxSaleAmount=0;
					
					DayEndData.put("DayEndArr",funGetJsonArrRowDayEnd((JSONArray)DayEndDataWS.get("tblDayEnd")));// lastJson);
					DayEndData.put("totalPax", DayEndDataWS.get("totalPax").toString());
					DayEndData.put("DayEndJArrTot", funGetJsonArrRowDayEnd((JSONArray)DayEndDataWS.get("TotalDayEnd")));
					
					jsSettlement.put("jArrSettlement", funGetJsonArrRowSettlement((JSONArray)SettlementWS.get("settlement")));
					jsSettlement.put("jArrsettlementTot", funGetJsonArrRowSettlement((JSONArray)SettlementWS.get("settlementTot")));
				
					jsSalesInProg.put("SalesInProgress",funGetJsonArrRowSalesOfProg((JSONArray)SalesInProgWS.get("salesInProg")));
				
					jsUnSettleBill.put("UnSettleBill",funGetJsonArrRowSettlement((JSONArray)UnSettleBillws.get("jArrUnSettle")));
					jsUnSettleBill.put("total", UnSettleBillws.get("ApproxSaleAmount").toString());
					DayEndDataProcess.put("DayEnd", DayEndData);
					DayEndDataProcess.put("Settlement", jsSettlement);
					DayEndDataProcess.put("SalesInProg", jsSalesInProg);
					DayEndDataProcess.put("UnSettleBill", jsUnSettleBill);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		
		
			
		return DayEndDataProcess;
	}


	public JSONObject funFillCurrencyGrid()throws Exception
	{
		totalSales=0;
		JSONArray jArrDayEnd=new JSONArray();
		 JSONArray jArrDayEndTot=new JSONArray();
		JSONObject jsonDayEnd =new JSONObject();
		JSONObject jsonDayEndTot =new JSONObject();
		//List listDayEnd=new ArrayList<>();
        sql =new StringBuilder("select strSettelmentDesc from tblsettelmenthd where strSettelmentType='Cash'");
    	List listSql = objBaseService.funGetList(sql, "sql");
    	if (listSql.size() > 0) 
		{
			for (int i = 0; i < listSql.size(); i++) 
			{
				JSONObject jsonOb =new JSONObject();
				List dataList=new ArrayList();
				String str= (String) listSql.get(i);
			
				jsonOb.put("0",str.toString());
				jsonOb.put("1","0.00");
				jsonOb.put("2","0.00");
				jsonOb.put("3","0.00");
				jsonOb.put("4","0.00");
				jsonOb.put("5","0.00");
				jsonOb.put("6","0.00");
				jsonOb.put("7","0.00");
				jsonOb.put("8","0.00");
				jsonOb.put("9","0.00");
				jsonOb.put("10","0");
		
				jArrDayEnd.add(jsonOb);
				
				
				}
			
			jsonDayEnd.put("tblDayEnd", jArrDayEnd);
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
		
			            for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.size(); cntDayEndTable++)
			            {
			            	JSONObject jr=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			            	
			            	if(jr.get("0").toString().equals(obj[0].toString()))
			            	{
			            		jr.put("1", obj[1].toString());

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
		    	
		    	
	 
		        // jsonDayEndTot
		        
		         JSONObject jsonOb =new JSONObject();
		         jsonOb.put("0", "Total Sales");
		         jsonOb.put("1", totalSales);
		         jsonOb.put("8", totalBillNo);
		     //    jsonOb.put("1", discountRecords);
		         
		         //jArrDayEndTot.put(jsonOb);
		         
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
				             JSONObject jr=(JSONObject) jArrDayEnd.get(0);
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
					        
			    			 for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.size(); cntDayEndTable++)
				             {
				                 if (obj[0].toString().equals("Float"))
				                 {
				                	  JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
				                         if (job.get("0").toString().equals(obj[2].toString()))
					                     {
					                    	 totalFloat += Double.parseDouble(obj[1].toString());
					                    	 job.put("2", obj[1].toString());
					                    	 cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
					                     }
				                 }
				                 else if (obj[0].toString().equals("Transfer In"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalTransIn +=Double.parseDouble(obj[1].toString());
				                         job.put("3", obj[1].toString());
				                         cashIn = cashIn + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Payments"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalPayments += Double.parseDouble(obj[1].toString());
				                         job.put("6", obj[1].toString());
				                         cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Transfer Out"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalTransOuts += Double.parseDouble(obj[1].toString());
				                         job.put("7", obj[1].toString());
				                         cashOut = cashOut + (Double.parseDouble(obj[1].toString().toString()));
				                     }
				                 }
				                 else if (obj[0].toString().equals("Withdrawl"))
				                 {
				                	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
			                         if (job.get("0").toString().equals(obj[2].toString()))
				                     {
				                         totalWithdrawl += Double.parseDouble(obj[1].toString());
				                         job.put("8", obj[1].toString());
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
				    	jsonDayEnd.put("totalPax", totalPax);
				    		
				    	 cashIn = cashIn + advCash + sales;
				         jsonOb.put("2", totalFloat);
				         jsonOb.put("3", totalTransIn);
				         jsonOb.put("4", advCash);
				         jsonOb.put("5", cashIn);
				         jsonOb.put("6", totalPayments);
				         jsonOb.put("7", totalTransOuts);
				         jsonOb.put("8", totalWithdrawl);
				         jsonOb.put("9", cashOut);
				         jsonOb.put("10","");
				         
				         jArrDayEndTot.add(jsonOb);
				         
				         double inHandCash = (cashIn) - cashOut;
				         
				         jsonDayEnd.put("TotalDayEnd", jArrDayEndTot);
				    	
				         double totalReceipts = 0.00, totalPayments = 0.00, balance = 0.00;
				         for (int cntDayEndTable = 0; cntDayEndTable < jArrDayEnd.size(); cntDayEndTable++)
				         {
				        	 JSONObject job=(JSONObject) jArrDayEnd.get(cntDayEndTable);
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
	return jsonDayEnd;
	}

	
	public JSONObject funFillSettlementWiseSalesGrid() throws Exception
    {
		
		JSONObject jsonSettlement =new JSONObject();
		JSONObject jsonSettlementTot =new JSONObject();
		JSONArray jArrSettt=new JSONArray();
		JSONArray jArrSetttTot=new JSONArray();
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
			    			 JSONObject js=new JSONObject();
			    			 js.put("0",obj[0].toString());
			    			 js.put("1",obj[1].toString());
				        
				            totalDiscount = totalDiscount + (Double.parseDouble(obj[2].toString()));
				            totalSales = totalSales + (Double.parseDouble(obj[1].toString()));
				            jArrSettt.add(js);
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
			    	
				    	JSONObject job=new JSONObject();
				    	job.put("0","Total Sales");
				    	job.put("1",totalSales);
				    	job.put("2",totalBillNo);
				    	jArrSetttTot.add(job);
				    	
				    	job=new JSONObject();
				    	job.put("0","Total Discount");
				    	job.put("1",totalDiscount);
				    	job.put("2",noOfDiscountedBills);
				    	jArrSetttTot.add(job);
			         
			    	
			         //tblSettlementWiseSalesTotal
			    	if (jArrSettt.size() > 0)
			        {
			    		JSONObject jo=(JSONObject) jArrSettt.get(0);
			    		jo.put("2", totalBillNo);
			            
			        }
			        dblApproxSaleAmount += totalSales;
			        
			        jsonSettlement.put("settlement", jArrSettt);
			        jsonSettlement.put("settlementTot", jArrSetttTot);
		return jsonSettlement;
    }
	      
	public JSONObject funFillTableSaleInProgress() throws Exception
	{
		JSONObject jsonSaleInProgress =new JSONObject();
		JSONArray jArrSalesInProgress=new JSONArray();
		 double dblSaleInProgressAmount = 0.00;
		 

	        String sql_FillTable = "select b.strTableName,sum(a.dblAmount) "
	                + " from tblitemrtemp a,tbltablemaster b "
	                + " where a.strTableNo=b.strTableNo and a.strNCKotYN='N' and a.strPOSCode='" + strPOSCode + "' "
	                + " group by a.strTableNo";
	        JSONObject jsonOb=new JSONObject();
	        List listSaleprog = objBaseService.funGetList(new StringBuilder(sql_FillTable), "sql");
		    	if (listSaleprog.size() > 0) 
				{
			    		for(int i=0;i<listSaleprog.size();i++)
			    		{
			    			 Object[] obj = (Object[]) listSaleprog.get(i);
			    			 dblSaleInProgressAmount += Double.parseDouble(obj[1].toString());
			    			 JSONObject jOb=new JSONObject();
			    			 jOb.put("0", obj[0].toString());
			    			 jOb.put("1", obj[1].toString());
			    			
			    			 jArrSalesInProgress.add(jOb);
			    			
					       }
				}
		    	jsonOb.put("0","");
		    	jsonOb.put("1","");
		    	jArrSalesInProgress.add(jsonOb);
		    	jsonOb=new JSONObject();
		    	jsonOb.put("0","Total");
		    	jsonOb.put("1",dblSaleInProgressAmount);
		    	jArrSalesInProgress.add(jsonOb);
		    	dblApproxSaleAmount += dblSaleInProgressAmount;
		    	jsonSaleInProgress.put("salesInProg", jArrSalesInProgress);
		return jsonSaleInProgress;
	}
		
	public JSONObject funFillTableUnsettleBills() throws Exception
	{
		JSONObject jsonUnSettleBill =new JSONObject();
		JSONArray jArrUnSettleBill=new JSONArray();
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
			             JSONObject jb=new JSONObject();
			             jb.put("0",obj[0].toString());
			             jb.put("1",obj[1].toString());
			             jb.put("2",obj[2].toString());
		               
			             jArrUnSettleBill.add(jb);
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
			    			    JSONObject jb=new JSONObject();
					             jb.put("0",obj[0].toString());
					             jb.put("1","Direct Biller");
					             jb.put("2",obj[1].toString());
				               
					             jArrUnSettleBill.add(jb);
			    			
					     }
				}
		    	
		     JSONObject jsonOb=new JSONObject();
		    	jsonOb.put("0","");
		    	jsonOb.put("1","");
		    	jsonOb.put("2","");
		    	jArrUnSettleBill.add(jsonOb);
		    	jsonOb=new JSONObject();
		    	jsonOb.put("0","Total");
		    	jsonOb.put("1","");
		    	jsonOb.put("2",unSetteledBillAmount);
		    	jArrUnSettleBill.add(jsonOb);
		    	
		    	jsonUnSettleBill.put("jArrUnSettle", jArrUnSettleBill);
		    	
		    	 dblApproxSaleAmount += unSetteledBillAmount;
		    	 jsonUnSettleBill.put("ApproxSaleAmount", dblApproxSaleAmount);
		    	 
		return jsonUnSettleBill;
	}
	
	public JSONObject funGetAllParameterValuesPOSWise(String strPOSCode, String clientCode)
	{
		JSONObject objJsonObject=new JSONObject();
		
		try
		{
			List list=objBaseService.funGetList(new StringBuilder("select strDayEnd,strShiftWiseDayEndYN from tblsetup where (strPOSCode='"+strPOSCode+"'  OR strPOSCode='All') "), "sql");				
			System.out.println(list);
			if(list!=null && list.size()>0)
			{			
				
				Object[] ob= (Object[]) list.get(0); 
				objJsonObject.put("gShiftEnd", ob[1].toString());
				objJsonObject.put("gDayEnd", ob[0].toString());  
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}			
	}
	
	
	public JSONObject funLoadAllReportsName(String strPOSCode,String strClientCode)
	{
		JSONObject jsReportNames=new JSONObject();
		ArrayList alReportName=new ArrayList<String>();
		ArrayList alCheckRpt=new ArrayList<Boolean>();
		try{
		
			sql=new StringBuilder("select a.strModuleName,a.strFormName from tblforms a "
                    + "where a.strModuleType='R' "
                    + "order by a.intSequence;");
			 List listRPT=objBaseService.funGetList(sql, "sql");
             if(listRPT.size()>0)
             {
             	for(int i=0;i<listRPT.size();i++)
             	{
             		Object[] ob=(Object[])listRPT.get(i);
             		alReportName.add(ob[0].toString());
             	}
             }
             
             sql=new StringBuilder("select  strPOSCode,strReportName,date(dtePOSDate) "
                    + "from tbldayendreports "
                    + "where strPOSCode='"+strPOSCode+"' "
                    + "and strClientCode='" + strClientCode + "';");
             
             listRPT=objBaseService.funGetList(sql, "sql");
             if(listRPT.size()>0)
             {
             	for(int i=0;i<listRPT.size();i++)
             	{
             		Object[] ob=(Object[])listRPT.get(i);
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
             		//al.add(ob[0].toString());
             	}
             }
             
             		Gson gson = new Gson();
			 	    Type type = new TypeToken<List<String>>() {}.getType();
		            String ReportName = gson.toJson(alReportName, type);
		            String CheckReport = gson.toJson(alCheckRpt, type);
		            jsReportNames.put("ReportName", ReportName);
		            jsReportNames.put("CheckReport", CheckReport);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jsReportNames;
	}
	//fill old data
	//funSendEmailClicked here get Selected reports and store in tblDayEndReports Table.. 
	public JSONObject funSendDayEndMailReports(JSONObject jsonData)
	{
		ArrayList alReportName=new ArrayList<String>();
		ArrayList alCheckRpt=new ArrayList<Boolean>();
		JSONObject jsonReturn=new JSONObject();
		JSONObject jsonFinal=new JSONObject();
		try{
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			alReportName= gson.fromJson(jsonData.get("ReportName").toString(), listType);
			//alCheckRpt= gson.fromJson(jsonData.get("CheckReport").toString(), listType);
			
			
			userCode=jsonData.get("userCode").toString();
			strClientCode=jsonData.get("strClientCode").toString();
			String strPOSDate=jsonData.get("strPOSDate").toString();
			String strPOSCode=jsonData.get("strPOSCode").toString();
			strPOSName=objUtilityController.funGetPOSName(strPOSCode);
			
			sql=new StringBuilder("select date(max(dtePOSDate)) from tbldayendprocess where strPOSCode='"+ strPOSCode + "' "
					+ " and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N') ");
		 	List list=objBaseService.funGetList(sql, "sql");
		 	if(list.size()>0)
		 	{
		 		strPOSDate=list.get(0).toString();
		 	}
		 	//SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		  	//Date posDateForTrans = dFormat.parse(strPOSDate);
		  	//String POSDateforRTransaction = (posDateForTrans.getYear() + 1900) + "-" + (posDateForTrans.getMonth() + 1) + "-" + posDateForTrans.getDate();
		 	Date dtCurrent = new Date(); 
		 	String currentTime = dtCurrent.getHours() + ":" + dtCurrent.getMinutes() + ":" + dtCurrent.getSeconds();
		 	String POSDateforRTransaction=strPOSDate+" "+currentTime;
		 	
		 	objBaseService.funExecuteUpdate("truncate table tbldayendreports;", "sql");
		 	for(int j=0;j<alReportName.size();j++)
		 	{
		 		sql=new StringBuilder("INSERT INTO tbldayendreports (strPOSCode, strClientCode, strReportName, dtePOSDate, "
		 				+ "strUserCreated,strUserEdited, dteDateCreated, dteDateEdited,strDataPostFlag)"
		 				+ " VALUES ('"+strPOSCode+"', '"+strClientCode+"', '"+alReportName.get(j).toString()+"', '"+strPOSDate+"',"
		 						+ " '"+userCode+"','"+userCode+"', '"+POSDateforRTransaction+"', '"+POSDateforRTransaction+"','N');");
		 		objBaseService.funExecuteUpdate(sql.toString(), "sql");
		 		//String posCode,String posName,String fromDate,String toDate,String reportName
		 		funGenerateReport(strPOSCode,strPOSName,strPOSDate,strPOSDate,alReportName.get(j).toString());
		 	}
		 	jsonReturn.put("Status", "true");
		 	
		 	jsonFinal.put("final", jsonReturn);
		 	 
//		 	  clsGlobalVarClass.dbMysql.execute("delete from tbldayendreports "
//	                    +"where strPOSCode='"+posCode+"' "
//	                    +"and strClientCode='"+clsGlobalVarClass.gClientCode+"' ");
//	            //insert dy end reports             
//	            clsGlobalVarClass.dbMysql.execute(sqlBuilder.toString());

		 	//funSendEmailClicked(strPOSDate,strPOSDate,strPOSDate,strPOSCode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonReturn;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/StartDayProcess",  method = RequestMethod.GET)
	public @ResponseBody ModelAndView funDayEndStart(HttpServletRequest req)
	{
		String strShiftNo="1";

		JSONObject jsonDayStart=new JSONObject();
		 jsonDayStart.put("DayStart", "Day Not Start");
	
		try{
			
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
			
				  req.getSession().setAttribute("ShiftEnd","N");
				  req.getSession().setAttribute("DayEnd","N");
				  jsonDayStart.put("DayStart", "Day Started Successfully");
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
		return new ModelAndView("frmPOSDayEndProcess");//return jsonDayStart; //new clsDayEndProcessBean();
 }
	
	public int funShiftStartProcess() throws Exception
    {
	
		

        return 1;
    }
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/CheckBillSettleBusyTable",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funCheckBillSettleBusyTable(HttpServletRequest req)
	{
		JSONObject jObj=new JSONObject();
		try{
				
			strPOSCode=req.getSession().getAttribute("loginPOS").toString();
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
	
			pendingBills=objPOSDayEndUtility.funCheckPendingBills(strPOSCode, strPOSDate);
			busyTable=objPOSDayEndUtility.funCheckTableBusy(strPOSCode);
			jObj.put("PendingBills", pendingBills);
			jObj.put("BusyTables", busyTable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jObj; 
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/EndDayProcess",  method = RequestMethod.GET)
	public @ResponseBody ModelAndView funDayEndProcess(@RequestParam("emailReport") String EmailReport, HttpServletRequest req)
	{
		
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		String ShiftNo="1";
		
		String shiftEnd="", DayEnd="", strShiftNo="";
		this.emailReport=emailReport;
		gTransactionType = "ShiftEnd";// Declared in main menu in spos
			
		String gEnableShiftYN = objPOSSetupUtility.funGetParameterValuePOSWise(userCode,strPOSCode, "gEnableShiftYN");
				
				 if (gEnableShiftYN.equalsIgnoreCase("Y"))//gEnableShiftYN.equalsIgnoreCase("Y")
			        {
				        funDoShiftEnd(strClientCode,strPOSCode,strPOSDate,userCode);//for shift enable
			        }
				 else
			        {
			            funShiftEnd(strClientCode,strPOSCode,strPOSDate,userCode);//for shift disable
			        }
			
		return new ModelAndView("frmPOSDayEndProcess");
	}
	
	  private JSONObject funDoShiftEnd(String strClientCode,String strPOSCode,String POSDate,String strUserCode)
	    {
		   
	    	//JSONObject jsonShift=new JSONObject();
	    	String sql="";
	        try
	        {
	            sql = "delete from tblitemrtemp where strTableNo='null'";
	            objBaseService.funExecuteUpdate(sql,"sql");
	            
	            gDayEndReportForm = "DayEndReport";
	            

	            if (pendingBills)
	            {
	                //JOptionPane.showMessageDialog(this, "Please settle pending bills");
	            	//jsonDayEndReturn.put("", "");
	            	jsonDayEndReturn.put("msg","Please settle pending bills");
	                return jsonDayEndReturn;
	            }
	            else if (busyTable)
	            {
	             	jsonDayEndReturn.put("msg","Sorry Tables are Busy Now");
	                return jsonDayEndReturn;
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
	                
	                	String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");
						String gDayEnd="N";
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
		                        
		                        objPOSDayEndUtility.funGetNextShiftNoForShiftEnd(strPOSCode, shiftNo,strClientCode,strUserCode);
		                        //btnShiftEnd.setEnabled(false);
		                        String filePath = System.getProperty("user.dir");
		                        filePath = filePath + "/Temp/Temp_DayEndReport.txt";
		                       //ption = JOptionPane.showConfirmDialog(this, "Do You Want To Email Reports?");
		                        if (emailReport.equals("Y"))
		                        {
		                            funSendDayEndReports(strClientCode,strPOSCode,POSDate);
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
	  
		   return jsonDayEndReturn;
		   }

	

	    private JSONObject funShiftEnd(String strClientCode,String strPOSCode,String strPOSDate,String strUserCode)
	    {
	        try
	        {
	            String sqld = "delete from tblitemrtemp where strTableNo='null'";
	            objBaseService.funExecuteUpdate(sqld,"sql" );
	            
	            gDayEndReportForm = "DayEndReport";
	            jsonDayEndReturn.put("gDayEndReportForm","DayEndReport");
	            if (objPOSDayEndUtility.funCheckPendingBills(strPOSCode,strPOSDate))
	            {
	                //JOptionPane.showMessageDialog(this, "Please settle pending bills");
	            	jsonDayEndReturn.put("msg","Please settle pending bills");
	                return jsonDayEndReturn;
	                
	            }
	            else if (objPOSDayEndUtility.funCheckTableBusy(strPOSCode))
	            {
	                //JOptionPane.showMessageDialog(this, "Sorry Tables are Busy Now");
	            	jsonDayEndReturn.put("msg","Sorry Tables are Busy Now");
	                return jsonDayEndReturn;
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
		                         objPOSDayEndUtility.funGetNextShiftNo(strPOSCode, shiftNo,strClientCode,strUserCode);
		                       // btnShiftEnd.setEnabled(false);
		                        
		                       // new clsManagersReport().funGenerateManagersReport(posDate, posDate, clsGlobalVarClass.gPOSCode);                        
		                        
		                        String filePath = System.getProperty("user.dir");
		                        filePath = filePath + "/Temp/Temp_DayEndReport.txt";
		                        //** Need to validate Email report need   	               
		                        if (emailReport.equals("Y"))
		                        {
		                            funSendDayEndReports(strClientCode,strPOSCode,strPOSDate);
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
		                            objUtilityController.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
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
	        return jsonDayEndReturn;
	    }

	 
	  
	   private void funSendDayEndReports(String strClientCode,String strPOSCode,String strPOSDate)
	    {              
	       try
	        {      
	    	   if(strPOSName.equals("")){
	    		   strPOSName=objUtilityController.funGetPOSName(strPOSCode);   
	    	   }
	    	   
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
	        		   funGenerateReport(strPOSCode,strPOSName,strPOSDate,strPOSDate,rpName);
	        	   }
	           }
	          
	    	    funCreateReportFolder();                    
	      
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }        

	        funCreateZipFolder();
	        String filePath = System.getProperty("user.dir");
	        String zipFile = filePath + "/Reports.zip";
	        
	        try
	        {
	        	String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
	      	
	        	String strPOSName=objUtilityController.funGetPOSName(strPOSCode);
				new clsPOSSendMail().funSendMail(gReceiverEmailIds, zipFile,strClientCode,strPOSCode,strPOSName,strPOSDate);
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
	            File file = new File(filePath + "/Reports");

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
	            String zipFile = filePath + "/Reports.zip";
	            File dir = new File(filePath + "/Reports");

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


	    private void funGenerateReport(String posCode,String posName,String fromDate,String toDate,String reportName)
	    {
	        try
	        {
	            if (reportName.equalsIgnoreCase("Bill Wise Report".toUpperCase()))
	            {
	                funGenerateBillWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Item Wise Report".toUpperCase()))
	            {
	                funGenerateItemWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Group Wise Report".toUpperCase()))
	            {
	                funGenerateGroupWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("SubGroupWise Report".toUpperCase()))
	            {
	                funGenerateSubGroupWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("OperatorWise Report".toUpperCase()))
	            {
	                funGenerateOperatorWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("SettlementWise Report".toUpperCase()))
	            {
	                funGenerateSettlementWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Void Bill Report".toUpperCase()))
	            {
	                funGenerateVoidBillExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Cost Centre Report".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Tax Wise Report".toUpperCase()))
	            {
	                funGenerateTaxExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Cash Mgmt Report".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Audit Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Advance Order Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Stock In Out Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Day End Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("AvgItemPerBill".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("AvgPerCover".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("AvgTicketValue".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("DebitCardFlashReports".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Promotion Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Discount Report".toUpperCase()))
	            {
	                funGenerateDiscountExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Group-SubGroup Wise Report".toUpperCase()))
	            {
	                funGenerateGroupSubGroupWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Loyalty Point Report".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Complimentary Settlement Report".toUpperCase()))
	            {
	                funGenerateComplimentaryWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Counter Wise Sales Report".toUpperCase()))
	            {
	                funGenerateCounterWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Non Chargable KOT Report".toUpperCase()))
	            {
	                funGenerateNCKOTExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Order Analysis Report".toUpperCase()))
	            {
	                funGenerateOrderAnalysisExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Auditor Report".toUpperCase()))
	            {
	                funGenerateAuditorExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Tax Breakup Summary Report".toUpperCase()))
	            {
	               // funGenerateTaxBreakUpExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Menu Head Wise".toUpperCase()))
	            {
	                funGenerateMenuHeadWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("WaiterWiseItemReport".toUpperCase()))
	            {
	                funGenerateWaiterWiseItemExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("WaiterWiseIncentivesReport".toUpperCase()))
	            {
	                funGenerateWaiterWiseIncentivesExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("DeliveryboyIncentive".toUpperCase()))
	            {
	                funGenerateDeliveryBoyIncentivesExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Sales Summary Flash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("POS Wise Sales".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Daily Collection Report".toUpperCase()))
	            {
	                funGenerateDailyCollectionExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Daily Sales Report".toUpperCase()))
	            {
	                funGenerateDailySalesExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Void KOT Report".toUpperCase()))
	            {
	                funGenerateVoidKOTExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Guest Credit Report".toUpperCase()))
	            {
	                funGenerateGuestCreditExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("SubGroupWiseSummaryReport".toUpperCase()))
	            {
	                funGenerateSubGroupWiseSummaryExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("UnusedCardBalanceReport".toUpperCase()))
	            {
	                funGenerateUnUsedCardBalanceExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("DayWiseSalesSummaryFlash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("BillWiseSettlementSalesSummaryFlash".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Revenue Head Wise Item Sales".toUpperCase()))
	            {
	                funGenerateRevenueHeadWiseExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Managers Report".toUpperCase()))
	            {

	            }
	            else if (reportName.equalsIgnoreCase("Item Wise Consumption".toUpperCase()))
	            {
	                //funGenerateItemConsumptionExcelReport(posCode, posName, fromDate, toDate);
	            }
	            else if (reportName.equalsIgnoreCase("Table Wise Pax Report".toUpperCase()))
	            {

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
	 
	    private void funGenerateBillWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();

	            double totalTaxAmt = 0;
	            double totalGrandAmount = 0;
	            double subTotal = 0;
	            double discountTotal = 0;
	            sql =new StringBuilder(" select a.strBillNo,d.strPosName,a.strSettelmentMode,sum(c.dblSettlementAmt),a.dblTaxAmt,a.dblDiscountAmt,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') "
	                    + " from vqbillhd a ,tblposmaster d ,vqbillsettlementdtl c "
	                    + " where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'"
	                    + " and a.strposcode=d.strPosCode and a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode and a.intShiftCode='" + shiftNo + "' ");
	            if (!posCode.equals("All"))
	            {
	                sql.append(" AND a.strPOSCode = '" + posCode + "' ");
	            }
	            sql.append(" group BY a.strBillNo ORDER BY a.strBillNo ASC; ");

	            List list=objBaseService.funGetList(sql, "sql");
	    		if(list.size()>0)
	              {	
	        			int j = 1;
		              	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              		 List<String> arrListItem = new ArrayList<String>();
		                     arrListItem.add(ob[0].toString());
		                     arrListItem.add(ob[6].toString());
		                     arrListItem.add(ob[1].toString());
		                     arrListItem.add(ob[2].toString());
		                     arrListItem.add(ob[3].toString());
		                     arrListItem.add(ob[4].toString());
		                     arrListItem.add(ob[5].toString());

		                     totalTaxAmt = totalTaxAmt + Double.parseDouble(ob[4].toString());
		                     totalGrandAmount = totalGrandAmount + Double.parseDouble(ob[3].toString());
		                     discountTotal = discountTotal + Double.parseDouble(ob[5].toString());
		                     mapExcelItemDtl.put(j, arrListItem);
		                     j++;
		              	}
		            }
	            
	            
	            

	            arrListTotal.add(String.valueOf(Math.rint(totalGrandAmount) + "#" + "5"));
	            arrListTotal.add(String.valueOf(Math.rint(totalTaxAmt)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(discountTotal)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("BillDate");
	            arrHeaderList.add("POSName");
	            arrHeaderList.add("Settle Mode");
	            arrHeaderList.add("Grand Total");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add("Discount");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("BillWise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "billWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 
	    private void funGenerateItemWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;
	            double subTotal = 0;
	            double discountTotal = 0;
	            StringBuilder sqlFilters = new StringBuilder();
	            StringBuilder sbSqlBillLive = new StringBuilder();
	            StringBuilder sbSqlQBillFile = new StringBuilder();
	            StringBuilder sbSqlModLive = new StringBuilder();
	            StringBuilder sbSqlQModFile = new StringBuilder();

	            sbSqlBillLive.setLength(0);
	            sbSqlQBillFile.setLength(0);
	            sbSqlModLive.setLength(0);
	            sbSqlQModFile.setLength(0);
	            sqlFilters.setLength(0);

	            sbSqlBillLive.append("select a.strItemCode,a.strItemName,c.strPOSName"
	                    + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
	                    + ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + userCode + "'\n"
	                    + "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlQBillFile.append("select a.strItemCode,a.strItemName,c.strPOSName"
	                    + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
	                    + ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + userCode + "'\n"
	                    + "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlModLive.append("select a.strItemCode,a.strModifierName,c.strPOSName"
	                    + ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + userCode + "'\n"
	                    + "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode and a.dblamount>0 \n"
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlQModFile.append("select a.strItemCode,d.stritemname,c.strPOSName"
	                    + ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + userCode + "'\n"
	                    + "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d\n"
	                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
	                    + " and a.strItemCode=d.strItemCode and a.dblamount>0\n"
	                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            if (!posCode.equals("All"))
	            {
	                sqlFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
	            }

	            sqlFilters.append(" and a.strClientCode=b.strClientCode and b.intShiftCode='" + shiftNo + "'  GROUP BY a.strItemCode");
	            sbSqlBillLive = sbSqlBillLive.append(sqlFilters);
	            sbSqlQBillFile = sbSqlQBillFile.append(sqlFilters);

	            sbSqlModLive = sbSqlModLive.append(sqlFilters);
	            sbSqlQModFile = sbSqlQModFile.append(sqlFilters);

	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash1;","sql");
	            String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
	                    + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
	                    + ",strpaymode,dblsubtotal,dbldiscamt,dtedatecreated,struser) ";
	            
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales + " (" + sbSqlBillLive + ")","sql");
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales + " (" + sbSqlQBillFile + ")","sql");
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales + " (" + sbSqlModLive + ")","sql");
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales + " (" + sbSqlQModFile + ")","sql");
	            sql =new StringBuilder(" select a.strbillno as stritemCode,a.dtebilldate as strItemName,a.tmebilltime, "
	                    + " sum(a.strtablename) as quantity,sum(a.strpaymode) as amount ,a.dblsubtotal as SubTotal,a.dbldiscamt as DisAmt,a.dtedatecreated as BillDate "
	                    + " from tbltempsalesflash1 a left outer join tblitemmaster b on a.strbillno=b.strItemCode "
	                    + " where a.strUser='" + userCode + "' "
	                    + " group by a.strbillno,a.dtebilldate,a.tmebilltime order by dtebilldate ");

	            List list=objBaseService.funGetList(sql, "sql");
	    		if(list.size()>0)
	              {	
	        			int j = 1;
		              	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              		List<String> arrListItem = new ArrayList<String>();
		                    arrListItem.add(ob[1].toString());
		                    arrListItem.add(ob[2].toString());
		                    arrListItem.add(ob[7].toString());
		                    arrListItem.add(ob[3].toString());
		                    arrListItem.add(ob[4].toString());
		                    arrListItem.add(ob[5].toString());
		                    arrListItem.add(ob[6].toString());

		                    totalQty = totalQty + Double.parseDouble(ob[3].toString());
		                    totalAmount = totalAmount + Double.parseDouble(ob[4].toString());
		                    subTotal = subTotal + Double.parseDouble(ob[5].toString());
		                    discountTotal = discountTotal + Double.parseDouble(ob[6].toString());

		                    mapExcelItemDtl.put(j, arrListItem);

		                    j++;
		              	}
	              }
	           
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(subTotal)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(discountTotal)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("ItemName");
	            arrHeaderList.add("POSName");
	            arrHeaderList.add("BillDate");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Sale AMT");
	            arrHeaderList.add("SubTotal");
	            arrHeaderList.add("Discount");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("ItemWise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");
	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "itemWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateGroupWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;
	            double subTotal = 0;
	            double discountTotal = 0;

	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlModLive = new StringBuilder();
	            StringBuilder sbSqlQModFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlModLive.setLength(0);
	            sbSqlQModFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
	                    + ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
	                    + ",f.strPosName, '" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt) "
	                    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
	                    + ",tblitemmaster e,tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=e.strItemCode "
	                    + "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode "
	                    + "and a.strClientCode=b.strClientCode  and a.intShiftCode='" + shiftNo + "' ");

	            sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
	                    + ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
	                    + ",f.strPosName, '" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt) "
	                    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
	                    + ",tblitemmaster e,tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=e.strItemCode "
	                    + "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode "
	                    + "and a.strClientCode=b.strClientCode  and a.intShiftCode='" + shiftNo + "'  ");

	            sbSqlModLive.append("select c.strGroupCode,c.strGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
	                    + ",'" + userCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
	                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd e,tblgrouphd c "
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
	                    + " and b.dblamount>0 and a.strClientCode=b.strClientCode  ");

	            sbSqlQModFile.append("select c.strGroupCode,c.strGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
	                    + ",'" + userCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
	                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd e,tblgrouphd c "
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
	                    + " and b.dblamount>0 and a.strClientCode=b.strClientCode   ");

	            sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
	            }
//	        if (!subGroupCode.equalsIgnoreCase("All"))
//	        {
//	            sbSqlFilters.append("AND d.strSubGroupCode='" + subGroupCode + "' ");
//	        }
	            sbSqlFilters.append(" Group BY c.strGroupCode, c.strGroupName, a.strPoscode ");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);
	            sbSqlModLive.append(sbSqlFilters);
	            sbSqlQModFile.append(sbSqlFilters);

	            //clsSalesFlashReport obj = new clsSalesFlashReport();
	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash;","sql");
	            String sqlInsertLiveBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlLive.toString()+");";
	            
	            String sqlInsertQFileBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlQFile.toString()+");";
	            
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");

	             sqlInsertLiveBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlModLive + ");";

	             sqlInsertQFileBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlQModFile + ");";

	             objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	             objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	             sql =new StringBuilder(" SELECT strname, strposcode, sum(dblquantity), sum(dblamount),sum(dblsubtotal),sum(dbldiscamt)"
	                    + " FROM tbltempsalesflash "
	                    + " group by strcode, strname, strposcode ");

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
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
	                    arrListItem.add(" ");

	                    totalQty = totalQty + Double.parseDouble(ob[2].toString());
	                    totalAmount = totalAmount + Double.parseDouble(ob[3].toString());
	                    subTotal = subTotal + Double.parseDouble(ob[4].toString());
	                    discountTotal = discountTotal + Double.parseDouble(ob[5].toString());
	                    mapExcelItemDtl.put(j, arrListItem);
	                    j++;
	                }

	            }

	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "3");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(subTotal)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(discountTotal)) + "#" + "6");
	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("GroupName");
	            arrHeaderList.add("POSName");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Sale AMT");
	            arrHeaderList.add("SubTotal");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Group Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("SubGroup Name" + " : All");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "groupWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateSubGroupWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;
	            double subTotal = 0;
	            double discountTotal = 0;
	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlModLive = new StringBuilder();
	            StringBuilder sbSqlQModFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlModLive.setLength(0);
	            sbSqlQModFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
	                    + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
	                    + "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d "
	                    + ",tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=d.strItemCode "
	                    + "and c.strSubGroupCode=d.strSubGroupCode and a.strClientCode=b.strClientCode  and a.intShiftCode='" + shiftNo + "' ");

	            sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
	                    + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
	                    + "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d "
	                    + ",tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=d.strItemCode "
	                    + "and c.strSubGroupCode=d.strSubGroupCode and a.strClientCode=b.strClientCode  and a.intShiftCode='" + shiftNo + "' ");

	            sbSqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
	                    + ",'" + userCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
	                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd c"
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=c.strSubGroupCode "
	                    + " and b.dblamount>0 and a.strClientCode=b.strClientCode  ");

	            sbSqlQModFile.append("select c.strSubGroupCode,c.strSubGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
	                    + ",'" + userCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
	                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd c"
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and LEFT(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=c.strSubGroupCode "
	                    + " and b.dblamount>0 and a.strClientCode=b.strClientCode   ");

	            sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
	            }
	            sbSqlFilters.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);
	            sbSqlModLive.append(sbSqlFilters);
	            sbSqlQModFile.append(sbSqlFilters);
	         
	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash;","sql");
	            String sqlInsertLiveBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlLive.toString()+");";
	            
	            String sqlInsertQFileBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlQFile.toString()+");";
	            
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	            
	             sqlInsertLiveBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlModLive + ");";

	             sqlInsertQFileBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlQModFile + ");";
	            
	             objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	             objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");

	             sql =new StringBuilder(" SELECT strname, strposcode, sum(dblquantity), sum(dblamount),sum(dblsubtotal),sum(dbldiscamt)"
	                    + " FROM tbltempsalesflash "
	                    + " group by strcode, strname, strposcode ");

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
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
	                    arrListItem.add(" ");
	                    totalQty = totalQty + Double.parseDouble(ob[2].toString());
	                    totalAmount = totalAmount + Double.parseDouble(ob[3].toString());
	                    subTotal = subTotal + Double.parseDouble(ob[4].toString());
	                    discountTotal = discountTotal + Double.parseDouble(ob[5].toString());
	                    mapExcelItemDtl.put(j, arrListItem);
	                    j++;

	              	}
	            }
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "3");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(subTotal)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(discountTotal)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("SubGroup Name");
	            arrHeaderList.add("POSName");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Sale AMT");
	            arrHeaderList.add("SubTotal");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("SubGroup Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "subGroupWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateOperatorWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            Map<String, List<clsPOSOperatorWiseReportBean>> mapOperatorDtls = new HashMap<String, List<clsPOSOperatorWiseReportBean>>();
	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlDisLive = new StringBuilder();
	            StringBuilder sbSqlQDisFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();
	            StringBuilder sbSqlDisFilters = new StringBuilder();

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlDisLive.setLength(0);
	            sbSqlQDisFile.setLength(0);
	            sbSqlFilters.setLength(0);
	            sbSqlDisFilters.setLength(0);

	            String userCode = "All";
	            String userName = "All";

	            sbSqlLive.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
	                    + " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
	                    + " FROM tbluserhd a "
	                    + " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
	                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
	                    + " inner join tblbillsettlementdtl d on b.strBillNo=d.strBillNo "
	                    + " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlQFile.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
	                    + " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
	                    + " FROM tbluserhd a "
	                    + " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
	                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
	                    + " inner join tblqbillsettlementdtl d on b.strBillNo=d.strBillNo "
	                    + " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            if (!posCode.equals("All") && !userCode.equals("All"))
	            {
	                sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "' and b.strUserCreated='" + userCode + "'");
	            }
	            else if (!posCode.equals("All") && "All".equals("All"))
	            {
	                sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "'");
	            }
	            else if (posCode.equals("All") && !userName.equals("All"))
	            {
	                sbSqlFilters.append("  and b.strUserCreated='" + userCode + "'");
	            }
	            sbSqlFilters.append(" GROUP BY a.strUserCode, b.strPosCode, d.strSettlementCode");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);

	            Map<String, Map<String, clsPOSOperatorWiseReportBean>> hmOperatorWiseSales = new HashMap<String, Map<String, clsPOSOperatorWiseReportBean>>();
	            Map<String, clsPOSOperatorWiseReportBean> hmSettlementDtl = null;
	            clsPOSOperatorWiseReportBean objOperatorWiseSales = null;

	            List list=objBaseService.funGetList(sbSqlLive, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
		              	Object[] ob=(Object[])list.get(i);
		              	  if (hmOperatorWiseSales.containsKey(ob[0].toString()))
		                  {
		              		  	hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
			                      if (hmSettlementDtl.containsKey(ob[7].toString()))
			                      {
			                          objOperatorWiseSales = hmSettlementDtl.get(ob[7].toString());
			                          objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt() + Double.parseDouble(ob[4].toString()));
			                      }
			                      else
			                      {
			                          objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
			                          objOperatorWiseSales.setStrUserCode(ob[0].toString());
			                          objOperatorWiseSales.setStrUserName(ob[1].toString());
			                          objOperatorWiseSales.setStrPOSName(ob[2].toString());
			                          objOperatorWiseSales.setStrSettlementDesc(ob[3].toString());
			                          objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[4].toString()));
			                          objOperatorWiseSales.setStrPOSCode(ob[6].toString());
			                          objOperatorWiseSales.setDiscountAmt(0);
			                      }
			                      hmSettlementDtl.put(ob[7].toString(), objOperatorWiseSales);
		                  }
		                  else
		                  {
		                      objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
	                          objOperatorWiseSales.setStrUserCode(ob[0].toString());
	                          objOperatorWiseSales.setStrUserName(ob[1].toString());
	                          objOperatorWiseSales.setStrPOSName(ob[2].toString());
	                          objOperatorWiseSales.setStrSettlementDesc(ob[3].toString());
	                          objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[4].toString()));
	                          objOperatorWiseSales.setStrPOSCode(ob[6].toString());
	                          objOperatorWiseSales.setDiscountAmt(0);
		
		                      hmSettlementDtl = new HashMap<String, clsPOSOperatorWiseReportBean>();
		                      hmSettlementDtl.put(ob[7].toString(), objOperatorWiseSales);
		                  }
		                  hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
	              	}
	            }
	            list=objBaseService.funGetList(sbSqlQFile, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
		              	Object[] ob=(Object[])list.get(i);
		              	  if (hmOperatorWiseSales.containsKey(ob[0].toString()))
			              {
			              		hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
			                    if (hmSettlementDtl.containsKey(ob[7].toString()))
			                    {
			                        objOperatorWiseSales = hmSettlementDtl.get(ob[7].toString());
			                        objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt() + Double.parseDouble(ob[4].toString()));
			                    }
			                    else
			                    {
			                        objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
			                        objOperatorWiseSales.setStrUserCode(ob[0].toString());
			                        objOperatorWiseSales.setStrUserName(ob[1].toString());
			                        objOperatorWiseSales.setStrPOSName(ob[2].toString());
			                        objOperatorWiseSales.setStrSettlementDesc(ob[3].toString());
			                        objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[4].toString()));
			                        objOperatorWiseSales.setStrPOSCode(ob[6].toString());
			                        objOperatorWiseSales.setDiscountAmt(0);
			                    }
			                   
			                    hmSettlementDtl.put(ob[6].toString(), objOperatorWiseSales);
			              }
		              	 else
		                  {
			                     objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
		                         objOperatorWiseSales.setStrUserCode(ob[0].toString());
		                         objOperatorWiseSales.setStrUserName(ob[1].toString());
		                         objOperatorWiseSales.setStrPOSName(ob[2].toString());
		                         objOperatorWiseSales.setStrSettlementDesc(ob[3].toString());
		                         objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[4].toString()));
		                         objOperatorWiseSales.setStrPOSCode(ob[6].toString());
		                         objOperatorWiseSales.setDiscountAmt(0);
			
			                      hmSettlementDtl = new HashMap<String, clsPOSOperatorWiseReportBean>();
			                      hmSettlementDtl.put(ob[7].toString(), objOperatorWiseSales);
		                  }
		                  hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
		             }
	            }
		        
	            sbSqlDisLive.append("SELECT a.strUserCode, a.strUserName, c.strPOSName"
	                    + " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode "
	                    + " FROM tbluserhd a "
	                    + " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
	                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlQDisFile.append("  SELECT a.strUserCode, a.strUserName, c.strPOSName "
	                    + " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode "
	                    + " FROM tbluserhd a "
	                    + " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
	                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            if (!posCode.equals("All") && !userName.equals("All"))
	            {
	                sbSqlDisFilters.append(" AND b.strPOSCode = '" + posCode + "' and b.strUserCreated='" + userName.toString() + "'");
	            }
	            else if (!posCode.equals("All") && userName.equals("All"))
	            {
	                sbSqlDisFilters.append(" AND b.strPOSCode = '" + posCode + "'");
	            }
	            else if (posCode.equals("All") && !userName.equals("All"))
	            {
	                sbSqlDisFilters.append("  and b.strUserCreated='" + userName + "'");
	            }
	            sbSqlDisFilters.append(" GROUP BY a.strUserCode, b.strPosCode");

	            sbSqlDisLive.append(sbSqlDisFilters);
	            sbSqlQDisFile.append(sbSqlDisFilters);

	            //System.out.println(sbSqlDisLive);
	            //System.out.println(sbSqlQDisFile);
	            list=objBaseService.funGetList(sbSqlDisLive, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
		              	Object[] ob=(Object[])list.get(i);
		              	if (hmOperatorWiseSales.containsKey(ob[0].toString()))
		                {
		                    hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
		                    Set<String> setKeys = hmSettlementDtl.keySet();
		                    for (String keys : setKeys)
		                    {
		                        objOperatorWiseSales = hmSettlementDtl.get(keys);
		                        objOperatorWiseSales.setDiscountAmt(objOperatorWiseSales.getDiscountAmt() + Double.parseDouble(ob[3].toString()));
		                        hmSettlementDtl.put(keys, objOperatorWiseSales);
		                        break;
		                    }
		                    hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
		                }
	              	}
	            }
	            list=objBaseService.funGetList(sbSqlQDisFile, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
		              	Object[] ob=(Object[])list.get(i);
		              	 if (hmOperatorWiseSales.containsKey(ob[0].toString()))
		                 {
		                     hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
		                     Set<String> setKeys = hmSettlementDtl.keySet();
		                     for (String keys : setKeys)
		                     {
		                         objOperatorWiseSales = hmSettlementDtl.get(keys);
		                         objOperatorWiseSales.setDiscountAmt(objOperatorWiseSales.getDiscountAmt() +Double.parseDouble(ob[3].toString()));
		                         hmSettlementDtl.put(keys, objOperatorWiseSales);
		                         break;
		                     }
		                     hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
		                 }
	              	}
	            }
	            objBaseService.funExecuteUpdate("truncate tbltempsalesflash","sql");
	            sql =new StringBuilder("insert into tbltempsalesflash (strcode,strname,strposcode,struser,dblsubtotal,dbldiscamt)values ");

	            double discAmt = 0, totalAmt = 0;
	            Object[] arrObjTableRowData = new Object[6];
	            for (Map.Entry<String, Map<String, clsPOSOperatorWiseReportBean>> entry : hmOperatorWiseSales.entrySet())
	            {
	                int i = 0;
	                Map<String, clsPOSOperatorWiseReportBean> hmOpSettlementDtl = entry.getValue();
	                for (Map.Entry<String, clsPOSOperatorWiseReportBean> entryOp : hmOpSettlementDtl.entrySet())
	                {
	                    clsPOSOperatorWiseReportBean objOperatorDtl = entryOp.getValue();
	                    sql.append("('" + objOperatorDtl.getStrUserCode() + "','" + objOperatorDtl.getStrUserName() + "',"
	                            + " '" + objOperatorDtl.getStrPOSName() + "','" + objOperatorDtl.getStrSettlementDesc() + "',"
	                            + " '" + objOperatorDtl.getSettleAmt() + "','" + objOperatorDtl.getDiscountAmt() + "'),");
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(objOperatorDtl.getStrUserCode());
	                    arrListItem.add(objOperatorDtl.getStrPOSName());
	                    if ("Summary".equals("Summary"))
	                    {
	                        arrListItem.add(objOperatorDtl.getStrPOSName());
	                    }
	                    else
	                    {
//	                        arrListItem.add(rs.getString(10));
	                    }
	                    arrListItem.add(objOperatorDtl.getStrSettlementDesc());
	                    arrListItem.add(String.valueOf(objOperatorDtl.getSettleAmt()));
	                    arrListItem.add(String.valueOf(objOperatorDtl.getDiscountAmt()));
	                    arrListItem.add(" ");

	                    discAmt = discAmt + objOperatorDtl.getDiscountAmt();
	                    totalAmt = totalAmt + objOperatorDtl.getSettleAmt();
	                    mapExcelItemDtl.put(i, arrListItem);
	                    i++;
	                }
	            }

	            arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(discAmt)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill NO.");
	            arrHeaderList.add("User Name");
	            if ("Summary".equals("Summary"))
	            {
	                arrHeaderList.add("POS Name");
	            }
	            else
	            {
	                arrHeaderList.add("Customer Name");
	            }

	            arrHeaderList.add("Settle Mode");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Operator Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("UserName" + " : " + userName);
	            arrparameterList.add(" ");

	            StringBuilder sb = new StringBuilder(sql);
	            int index = sb.lastIndexOf(",");
	            sql = sb.delete(index, sb.length());
	            System.out.println(sql);

	            if (sql.length() > 85)
	            {
	            	objBaseService.funExecuteUpdate(sql.toString(),"sql");
	            }

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "operatorWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateSettlementWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	          
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;

	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sqlFilter = new StringBuilder();

	            sbSqlLive.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname "
	                    + "FROM tblbillhd a, tblbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
	                    + "Where a.strBillNo = b.strBillNo "
	                    + "and a.strposcode=d.strposcode "
	                    + "and b.strSettlementCode = c.strSettelmentCode ");

	            sbSqlQFile.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname "
	                    + "FROM tblqbillhd a, tblqbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
	                    + "Where a.strBillNo = b.strBillNo "
	                    + "and a.strposcode=d.strposcode "
	                    + "and b.strSettlementCode = c.strSettelmentCode ");

	            sqlFilter.append("and date(a.dteBillDate ) BETWEEN  '" + fromDate + "' AND '" + toDate + "' ");
	            if (!"All".equalsIgnoreCase(posCode))
	            {
	                sqlFilter.append("and  a.strPosCode='" + posCode + "' ");
	            }
	            sqlFilter.append("GROUP BY c.strSettelmentDesc, a.strPosCode");

	            sbSqlLive.append(sqlFilter);
	            sbSqlQFile.append(sqlFilter);

	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash;","sql");
	            objBaseService.funExecuteUpdate("insert into tbltempsalesflash(strcode,strname,dblquantity,strposcode)(" + sbSqlLive + ") ","sql");
	            objBaseService.funExecuteUpdate("insert into tbltempsalesflash(strcode,strname,dblquantity,strposcode)(" + sbSqlQFile + ") ","sql");

	            sql =new StringBuilder("select strcode,strname,dblquantity,strposcode from tbltempsalesflash ");
	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(ob[0].toString());
	                    arrListItem.add(ob[1].toString());
	                    arrListItem.add(ob[3].toString());
	                    arrListItem.add(ob[2].toString());
	                    arrListItem.add("");
	                    arrListItem.add("");
	                    arrListItem.add("");

	                    totalQty = totalQty + Double.parseDouble(ob[2].toString());
	                    mapExcelItemDtl.put(j, arrListItem);
	                    j++;
	              	}
	            }
	            
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("POS Code");
	            arrHeaderList.add("Settle Mode");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("");
	            arrHeaderList.add("");
	            arrHeaderList.add("");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Settlement Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "settlemntWiseExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateVoidBillExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalAmount = 0;
	            double totalQty = 0;

//	            if (cmbReportMode.getSelectedIndex() == 0)
	            if (false)
	            {
	            	sql =new StringBuilder( " select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate, "
	                        + " Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime, "
	                        + " a.dblModifiedAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited"
	                        + " , a.strUserCreated,a.strRemark "
	                        + " from tblvoidbillhd a,tblvoidbilldtl b "
	                        + " where a.strBillNo=b.strBillNo and b.strTransType='VB' and a.strTransType='VB' "
	                        + " and Date(a.dteModifyVoidBill) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode= '" + posCode + "' ");
	                }
	                sql.append(" group by a.strBillNo"
	                        + " union "
	                        + " select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate"
	                        + " ,Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime"
	                        + " ,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited"
	                        + " ,a.strUserCreated,b.strRemarks"
	                        + " from tblvoidbillhd a, tblvoidmodifierdtl b "
	                        + " where a.strBillNo=b.strBillNo and a.strTransType='VB' "
	                        + " and Date(a.dteModifyVoidBill) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode= '" + posCode + "' ");
	                }
	                sql.append(" group by a.strBillNo");

	                List list=objBaseService.funGetList(sql, "sql");
	                if(list.size()>0)
	                {
	                	int j = 1;
	                	for(int i=0;i<list.size();i++)
	                  	{
	                  		Object[] ob=(Object[])list.get(i);
	                        List<String> arrListItem = new ArrayList<String>();
	                        arrListItem.add(ob[0].toString());
	                        arrListItem.add(ob[1].toString());
	                        arrListItem.add(ob[2].toString());
	                        arrListItem.add(ob[3].toString());
	                        arrListItem.add(ob[4].toString());
	                        arrListItem.add(ob[6].toString());
	                        arrListItem.add(ob[5].toString());
	                        arrListItem.add(ob[9].toString());

	                        totalAmount = totalAmount + Double.parseDouble(ob[5].toString());

	                        mapExcelItemDtl.put(j, arrListItem);

	                        j++;

	                  	}
	                }
	                arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "7");

	                arrHeaderList.add("Serial No");
	                arrHeaderList.add("Bill No");
	                arrHeaderList.add("BillDate");
	                arrHeaderList.add("Voided Date");
	                arrHeaderList.add("Entry Time");
	                arrHeaderList.add("Voided Time");
	                arrHeaderList.add("Reason");
	                arrHeaderList.add("Amount");
	                arrHeaderList.add("Remarks");

	                List<String> arrparameterList = new ArrayList<String>();
	                arrparameterList.add("Void Bill Summary Report");
	                arrparameterList.add("POS" + " : " + posName);
	                arrparameterList.add("FromDate" + " : " + fromDate);
	                arrparameterList.add("ToDate" + " : " + toDate);
	                arrparameterList.add(" ");
	                arrparameterList.add(" ");

	                funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "voidBillDtlExcelSheet");

	            }
	            else
	            {
	                sql=new StringBuilder(" select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate, "
	                        + " Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime,b.strItemName, "
	                        + " sum(b.intQuantity),sum(b.dblAmount) as BillAmount,b.strReasonName as Reason, "
	                        + " a.strUserEdited as UserEdited, a.strUserCreated,b.strRemarks "
	                        + " from tblvoidbillhd a,tblvoidbilldtl b "
	                        + " where a.strBillNo=b.strBillNo and b.strTransType='VB' and a.strTransType='VB' "
	                        + " and Date(a.dteModifyVoidBill) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode= '" + posCode + "' ");
	                }
	                sql.append(" group by a.strBillNo,b.strItemCode "
	                        + " union "
	                        + " select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate, "
	                        + " Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime,b.strModifierName, "
	                        + " sum(b.dblQuantity),sum(b.dblAmount) as BillAmount,ifnull(c.strReasonName,'NA') as Reason, "
	                        + " a.strUserEdited as UserEdited, a.strUserCreated,b.strRemarks  "
	                        + " from tblvoidbillhd a,tblvoidmodifierdtl b "
	                        + " left outer join tblreasonmaster c on b.strReasonCode=c.strReasonCode "
	                        + " where a.strBillNo=b.strBillNo  "
	                        + " and a.strTransType='VB' and Date(a.dteModifyVoidBill) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode='" + posCode + "' ");
	                }
	                sql.append(" group by a.strBillNo,b.strModifierCode ");

	                List list=objBaseService.funGetList(sql, "sql");
	                if(list.size()>0)
	                {
	                	int j = 1;
	                	for(int i=0;i<list.size();i++)
	                  	{
	                  		Object[] ob=(Object[])list.get(i);
	                  	    List<String> arrListItem = new ArrayList<String>();
	                        arrListItem.add(ob[0].toString());
	                        arrListItem.add(ob[1].toString());
	                        arrListItem.add(ob[2].toString());
	                        arrListItem.add(ob[5].toString());
	                        arrListItem.add(ob[8].toString());
	                        arrListItem.add(ob[6].toString());
	                        arrListItem.add(ob[7].toString());
	                        arrListItem.add(ob[11].toString());

	                        totalQty = totalQty + Double.parseDouble(ob[6].toString());
	                        totalAmount = totalAmount + Double.parseDouble(ob[7].toString());

	                        mapExcelItemDtl.put(j, arrListItem);

	                        j++;


	                  	}
	                }
                    arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "6");
	                arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "7");

	                arrHeaderList.add("Serial No");
	                arrHeaderList.add("Bill No");
	                arrHeaderList.add("BillDate");
	                arrHeaderList.add("Voided Date");
	                arrHeaderList.add("Item Name");
	                arrHeaderList.add("Reason");
	                arrHeaderList.add("Qty");
	                arrHeaderList.add("Amount");
	                arrHeaderList.add("Remarks");

	                List<String> arrparameterList = new ArrayList<String>();
	                arrparameterList.add("Void Bill Detail Report");
	                arrparameterList.add("POS" + " : " + posName);
	                arrparameterList.add("FromDate" + " : " + fromDate);
	                arrparameterList.add("ToDate" + " : " + toDate);
	                arrparameterList.add(" ");
	                arrparameterList.add(" ");

	                funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "voidBillDtlExcelSheet");

	            }

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    } 
	    
	    private void funGenerateTaxExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalGrandTotal = 0;
	            double totalTaxAmt = 0;
	            double totalTaxableAmt = 0;

	            sql.setLength(0);
	            sql =new StringBuilder( " SELECT * FROM (SELECT a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'), b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname "
	                    + " FROM tblBillHd a "
	                    + " INNER JOIN tblBillTaxDtl b ON a.strBillNo = b.strBillNo "
	                    + " INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode"
	                    + " LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
	                    + " WHERE DATE(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sql.append("and a.strPOSCode= '" + posCode + "' ");
	            }

	            sql.append(" UNION ALL "
	                    + " SELECT a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'), b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname "
	                    + " FROM tblqBillHd a "
	                    + " INNER JOIN tblqBillTaxDtl b ON a.strBillNo = b.strBillNo "
	                    + " INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
	                    + " LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
	                    + "  WHERE DATE(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sql.append("and a.strPOSCode= '" + posCode + "' ");
	            }
	            sql.append(" )aaa");

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(ob[0].toString());
	                    arrListItem.add(ob[1].toString());
	                    arrListItem.add(ob[8].toString());
	                    arrListItem.add(ob[3].toString());
	                    arrListItem.add(ob[5].toString());
	                    arrListItem.add(ob[6].toString());
	                    arrListItem.add(ob[7].toString());

	                    totalTaxableAmt = totalTaxableAmt + Double.parseDouble(ob[5].toString());
	                    totalTaxAmt = totalTaxAmt + Double.parseDouble(ob[6].toString());
	                    totalGrandTotal = totalGrandTotal + Double.parseDouble(ob[7].toString());

	                    mapExcelItemDtl.put(j, arrListItem);

	                    j++;


	              	}
	            }
	          

	            arrListTotal.add(String.valueOf(Math.rint(totalTaxableAmt)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalTaxAmt)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalGrandTotal)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("BillDate");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Tax Name");
	            arrHeaderList.add("Taxable Amt");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add("Grand Total");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Tax Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "taxWiseExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    
	    private void funGenerateDiscountExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalDis = 0;
	            double totalAmount = 0;
	            double totalDisOnAmount = 0;

	            StringBuilder sbSqlLiveDisc = new StringBuilder();
	            StringBuilder sbSqlQFileDisc = new StringBuilder();

	            sbSqlLiveDisc.append("select d.strPosName,a.dteBillDate,a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
	                    + " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
	                    + " from \n"
	                    + " tblbillhd a\n"
	                    + " left outer join tblbilldiscdtl b on b.strBillNo=a.strBillNo\n"
	                    + " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
	                    + " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
	                    + " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
	                    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'\n");

	            sbSqlQFileDisc.append("select d.strPosName,a.dteBillDate,a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
	                    + " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
	                    + " from \n"
	                    + " tblqbillhd a\n"
	                    + " left outer join tblqbilldiscdtl b on b.strBillNo=a.strBillNo\n"
	                    + " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
	                    + " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
	                    + " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
	                    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'\n");

	            if (!posCode.equalsIgnoreCase("All"))
	            {
	                sbSqlLiveDisc.append(" and a.strPOSCode='" + posCode + "' ");
	                sbSqlQFileDisc.append(" and a.strPOSCode='" + posCode + "' ");
	            }
	            objBaseService.funExecuteUpdate("truncate tbltempsalesflash1", "sql");

	            String insertLiveSql = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,dbldiscper,dbldiscamt,dblsettlementamt,strtablename"
	                    + ",strposcode,strpaymode,strusercreated,struseredited,dblsubtotal"
	                    + ",dtedatecreated)(" + sbSqlLiveDisc + ")";

	            String insertQSql = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,dbldiscper,dbldiscamt,dblsettlementamt,strtablename,strposcode"
	                    + ",strpaymode,strusercreated,struseredited,dblsubtotal,dtedatecreated "
	                    + ")(" + sbSqlQFileDisc + ")";

	            objBaseService.funExecuteUpdate(insertLiveSql, "sql");
	            objBaseService.funExecuteUpdate(insertLiveSql, "sql");

	            sql =new StringBuilder( " select strbillno ,dtebilldate ,tmebilltime ,strtablename , "
	                    + " strposcode ,strpaymode,dblsubtotal ,dbldiscamt,dbldiscper ,dblsettlementamt , "
	                    + " strusercreated ,struseredited,dtedatecreated ,dtedateedited "
	                    + " from tbltempsalesflash1 "
	                    + " group by strbillno,tmebilltime,dbldiscper "
	                    + " order by strbillno,tmebilltime,dbldiscper ");

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		 Object[] ob=(Object[])list.get(i);
	              	     List<String> arrListItem = new ArrayList<String>();
	                     arrListItem.add(ob[2].toString());
	                     arrListItem.add(ob[1].toString());
	                     arrListItem.add(ob[0].toString());
	                     arrListItem.add(ob[6].toString());
	                     arrListItem.add(ob[7].toString());
	                     arrListItem.add(ob[9].toString());
	                     arrListItem.add(ob[8].toString());
	                     arrListItem.add(ob[5].toString());
	                     arrListItem.add(ob[10].toString());

	                     totalAmount = totalAmount + Double.parseDouble(ob[6].toString());
	                     totalDis = totalDis + Double.parseDouble(ob[7].toString());
	                     totalDisOnAmount = totalDisOnAmount + Double.parseDouble(ob[9].toString());
	                     mapExcelItemDtl.put(j, arrListItem);
	                     j++;

	              	}
	            }
	            
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(totalDis)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalDisOnAmount)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("BillDate");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add("Discount On Amt");
	            arrHeaderList.add("Dis Per");
	            arrHeaderList.add("Reason");
	            arrHeaderList.add("Remark");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Discount Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "discountWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateGroupSubGroupWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            StringBuilder sbSql = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;

	            sbSql.setLength(0);
	            sbSql.append("select b.strItemName,c.strSubGroupName,d.strGroupName , sum(a.dblQuantity)+ifnull(sum(e.dblQuantity),0) as Quantity "
	                    + " ,sum(a.dblAmount)+ifnull(sum(e.dblAmount),0) as Amount , DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
	                    + " from vqbillhddtl a  "
	                    + " left outer join tblitemmaster b on a.strItemCode=b.strItemCode "
	                    + " left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode "
	                    + " left outer join tblgrouphd d on c.strGroupCode=d.strGroupCode "
	                    + " left outer join vqbillmodifierdtl e on a.strBillNo=e.strBillNo and a.stritemcode=left(e.strItemCode,7) "
	                    + " where  a.strItemCode=b.strItemCode and b.strSubGroupCode=c.strSubGroupCode "
	                    + " and c.strGroupCode=d.strGroupCode and date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sbSql.append(" AND a.strPoscode = '" + posCode + "' ");
	            }
//	            if (!groupCodeArr[1].trim().equals("All"))
//	            {
//	                sbSql.append(" and c.strGroupCode='" + groupCodeArr[1].trim() + "' ");
//	            }
//	            if (!subGroupCodeArr[1].trim().equals("All"))
//	            {
//	                sbSql.append(" and c.strSubGroupCode='" + subGroupCodeArr[1].trim() + "' ");
//	            }
	            sbSql.append(" Group By d.strGroupName ,c.strSubGroupName,b.strItemName "
	                    + " order by d.strGroupName,c.strSubGroupName ");

	            List list=objBaseService.funGetList(sbSql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              	     List<String> arrListItem = new ArrayList<String>();
	                     arrListItem.add(ob[0].toString());
	                     arrListItem.add(ob[1].toString());
	                     arrListItem.add(ob[2].toString());
	                     arrListItem.add(ob[5].toString());
	                     arrListItem.add(ob[3].toString());
	                     arrListItem.add(ob[4].toString());
	                     arrListItem.add(" ");

	                     totalQty = totalQty + Double.parseDouble(ob[3].toString());
	                     totalAmount = totalAmount + Double.parseDouble(ob[4].toString());
	                     mapExcelItemDtl.put(j, arrListItem);
	                     j++;

	              	}
	            }
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("ItemName");
	            arrHeaderList.add("SubGroup Name");
	            arrHeaderList.add("Group Name");
	            arrHeaderList.add("BillDate");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Group-SubGroup Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("Group Name" + " : All");
	            arrparameterList.add("SubGroup Name" + " : All");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "subgroupGroupWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateComplimentaryWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            StringBuilder sbSql = new StringBuilder();

	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;
	            double settlementAmt = 0;
	            double grandTotal = 0;

	            sbSql.setLength(0);
	            sbSql.append(" select a.strBillNo, date(a.dteBillDate),b.strItemName, b.dblQuantity, b.dblRate,(b.dblQuantity*b.dblRate) as dblAmount\n"
	                    + ",f.strPosName,ifnull(g.strWShortName,'NA') as strWShortName, e.strReasonName, a.strRemarks,ifnull(i.strGroupName,'')\n"
	                    + "from vqbillhd a\n"
	                    + "left outer join  vqbilldtl b on a.strBillNo = b.strBillNo\n"
	                    + "left outer join  vqbillsettlementdtl c on a.strBillNo = c.strBillNo\n"
	                    + "left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode \n"
	                    + "left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode \n"
	                    + "left outer join tblposmaster f on a.strPOSCode=f.strPosCode \n"
	                    + "left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo\n"
	                    + "left outer join tbltablemaster h on  a.strTableNo=h.strTableNo\n"
	                    + "left outer join tblitemcurrentstk i on b.strItemCode=i.strItemCode\n"
	                    + "where d.strSettelmentType = 'Complementary'  ");

	            if (!posCode.equals("All"))
	            {
	                sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	            }

	            sbSql.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
	                    + " group by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode\n"
	                    + " order by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode ");
	            
	            List list=objBaseService.funGetList(sbSql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(ob[0].toString());
	                    arrListItem.add(ob[1].toString());
	                    arrListItem.add(ob[2].toString());
	                    arrListItem.add(ob[10].toString());
	                    arrListItem.add(ob[3].toString());
	                    arrListItem.add(ob[4].toString());
	                    arrListItem.add(ob[5].toString());
	                    arrListItem.add(ob[6].toString());
	                    arrListItem.add(ob[7].toString());
	                    arrListItem.add(ob[8].toString());
	                    arrListItem.add(ob[9].toString());

	                    totalQty = totalQty + Double.parseDouble(ob[3].toString());
	                    totalAmount = totalAmount + Double.parseDouble(ob[5].toString());
	                    mapExcelItemDtl.put(j, arrListItem);

	                    String sqlMod = " select a.strBillNo, date(a.dteBillDate),b.strModifierName, b.dblQuantity, b.dblRate,(b.dblQuantity*b.dblRate) as dblAmount"
	                            + " ,f.strPosName,ifnull(g.strWShortName,'NA') as strWShortName, e.strReasonName, a.strRemarks,ifnull(i.strGroupName,'') "
	                            + " from vqbillhd a"
	                            + " INNER JOIN  vqbillmodifierdtl b on a.strBillNo = b.strBillNo"
	                            + " left outer join  vqbillsettlementdtl c on a.strBillNo = c.strBillNo"
	                            + " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
	                            + " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
	                            + " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
	                            + " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
	                            + " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
	                            + " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
	                            + " where d.strSettelmentType = 'Complementary' and a.strBillNo='" + ob[0].toString() + "' "
	                            + " and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "'  "
	                            + " group by a.strPOSCode,a.strBillNo,left(b.strItemCode,7) "
	                            + " order by a.strPOSCode,a.strBillNo,left(b.strItemCode,7) ";

	                    List list1=objBaseService.funGetList(new StringBuilder(sqlMod), "sql");
	                    if(list1.size()>0)
	                    {
	                    	int k = 1;
	                    	for(int p=0;i<list1.size();p++)
	                      	{
	                      		Object[] obj=(Object[])list1.get(p);
	                      		j++;
	                            List<String> arrListModItem = new ArrayList<String>();
	                            arrListModItem.add(obj[0].toString());
	                            arrListModItem.add(obj[1].toString());
	                            arrListModItem.add(obj[2].toString());
	                            arrListModItem.add(obj[10].toString());
	                            arrListModItem.add(obj[3].toString());
	                            arrListModItem.add(obj[4].toString());
	                            arrListModItem.add(obj[5].toString());
	                            arrListModItem.add(obj[6].toString());
	                            arrListModItem.add(obj[7].toString());
	                            arrListModItem.add(obj[8].toString());
	                            arrListModItem.add(obj[9].toString());

	                            totalQty = totalQty + Double.parseDouble(obj[3].toString());
	                            totalAmount = totalAmount + Double.parseDouble(obj[5].toString());
	                            mapExcelItemDtl.put(k, arrListModItem);

	                            // mapExcelItemDtl.put(i, arrListItem);
	                            k++;

	                      	}
	                    }
	              	}
	            }
	            

	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("Bill Date");
	            arrHeaderList.add("Item Name");
	            arrHeaderList.add("Group Name");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Rate");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("POS");
	            arrHeaderList.add("Waiter");
	            arrHeaderList.add("Reason");
	            arrHeaderList.add("Remark");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Complimentary SettlementWise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("Reason" + " : All");
	            arrparameterList.add(" ");

	            sbSql = null;

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "ComplimentaryWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateGuestCreditExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmt = 0;
	            double totalDis = 0;

	            sql=new StringBuilder("select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) ,h.strPosName,d.strSettelmentType "
	                    + " ,a.strKOTNo,b.strPOSCode,b.strSettelmentMode,b.dblDiscountAmt,b.dblDiscountPer,b.dblTaxAmt "
	                    + " ,b.dblSubTotal,b.dblGrandTotal,b.strRemarks,e.strTableName,f.strCustomerName,g.strWShortName "
	                    + " from tblbilldtl a left outer join tblbillhd b on a.strBillNo=b.strBillNo "
	                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
	                    + " left outer join tblcustomermaster f on b.strCustomerCode=f.strCustomerCode "
	                    + " left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
	                    + " left outer join tblposmaster h on b.strPOSCode=h.strPosCode ");
	            if (!posCode.equals("All"))
	            {
	                sql.append(" where b.strPOSCode = '" + posCode + "' and d.strSettelmentType='Credit' ");
	            }
	            else
	            {
	                sql.append(" where d.strSettelmentType='Credit' ");

	            }

	            sql.append(" and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " order by b.strPOSCode,a.strBillNo,a.strKOTNo)"
	                    + " union "
	                    + " (select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) ,h.strPosName,d.strSettelmentType "
	                    + " ,a.strKOTNo,b.strPOSCode,b.strSettelmentMode,b.dblDiscountAmt,b.dblDiscountPer,b.dblTaxAmt "
	                    + " ,b.dblSubTotal,b.dblGrandTotal,b.strRemarks,e.strTableName,f.strCustomerName,g.strWShortName "
	                    + " from tblqbilldtl a left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
	                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
	                    + " left outer join tblcustomermaster f on b.strCustomerCode=f.strCustomerCode "
	                    + " left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
	                    + " left outer join tblposmaster h on b.strPOSCode=h.strPosCode ");
	            if (!posCode.equals("All"))
	            {
	                sql.append(" where b.strPOSCode = '" + posCode + "' and d.strSettelmentType='Credit' ");
	            }
	            else
	            {
	                sql.append(" where d.strSettelmentType='Credit' ");

	            }

	            sql.append(" and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " order by b.strPOSCode,a.strBillNo,a.strKOTNo)");

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              		List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(ob[0].toString());
	                    arrListItem.add(ob[6].toString());
	                    arrListItem.add(ob[2].toString());
	                    arrListItem.add(ob[9].toString());
	                    arrListItem.add(ob[4].toString());
	                    arrListItem.add(ob[5].toString());
	                    arrListItem.add(ob[12].toString());

	                    totalQty = totalQty + Double.parseDouble(ob[4].toString());
	                    totalAmt = totalAmt + Double.parseDouble(ob[5].toString());
	                    totalDis = totalDis + Double.parseDouble(ob[12].toString());

	                    mapExcelItemDtl.put(j, arrListItem);

	                    j++;
	              	}
	            }
	            
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalDis)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("Bill Date");
	            arrHeaderList.add("Item Name");
	            arrHeaderList.add("KOT No");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("Discount");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Guest Credit Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "GuestCreditExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateCounterWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            StringBuilder sbSql = new StringBuilder();

	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;

	            sbSql.setLength(0);

	            //if (cmbSettlementMode.getSelectedItem().equals("Menu Wise"))
	            if (true)
	            {
	                sbSql.append(" select ifnull(a.strCounterCode,'NA') as strCounterCode ,ifnull(c.strCounterName,'NA') as  "
	                        + " strCounterName ,ifNull(d.strMenuCode,'NA') as strMenuCode,ifnull(d.strMenuName,'NA') as strMenuName, "
	                        + " a.dblRate,sum(a.dblquantity) as dblquantity ,sum(a.dblamount)as dblamount,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') "
	                        + " from vqbillhddtl a ,tblmenuitempricingdtl b ,tblcounterhd c ,tblmenuhd d "
	                        + " where a.stritemcode = b.strItemCode and a.strCounterCode=c.strCounterCode "
	                        + " and b.strMenuCode=d.strMenuCode and (a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPoscode = '" + posCode + "' ");
	                }
	                sbSql.append(" and a.strAdvBookingNo ='' "
	                        + " group by a.strCounterCode,c.strCounterName, d.strMenuCode,d.strMenuName "
	                        + " order by c.strCounterName,d.strMenuCode ");
	            }
	            //if (cmbSettlementMode.getSelectedItem().toString().equalsIgnoreCase("Sub Group Wise"))
	            if (false)
	            {
	                sbSql.append("select ifnull(c.strCounterCode,'NA') as strCounterCode ,ifnull(c.strCounterName,'NA') as strCounterName , "
	                        + " f.strSubGroupCode,f.strSubGroupName, "
	                        + " a.dblRate,sum(a.dblquantity) as dblquantity ,sum(a.dblamount)as dblamount,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') "
	                        + " from vqbillhddtl a ,tblmenuitempricingdtl b ,tblcounterhd c ,tblmenuhd d ,tblitemmaster e,tblsubgrouphd f "
	                        + " where a.stritemcode = b.strItemCode and a.strCounterCode=c.strCounterCode "
	                        + " and a.stritemcode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode "
	                        + " and b.strMenuCode=d.strMenuCode and (a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPoscode = '" + posCode + "' ");
	                }
	                sbSql.append(" and a.strAdvBookingNo ='' "
	                        + " group by c.strCounterCode,c.strCounterName, f.strSubGroupCode,f.strSubGroupName "
	                        + " order by c.strCounterCode,c.strCounterName,f.strSubGroupCode,f.strSubGroupName ");
	            }

	            //if (cmbSettlementMode.getSelectedItem().toString().equalsIgnoreCase("Group Wise"))
	            if (false)
	            {
	                sbSql.append("select ifnull(c.strCounterCode,'NA') as strCounterCode ,ifnull(c.strCounterName,'NA') as strCounterName , "
	                        + " g.strGroupCode,g.strGroupName, "
	                        + " a.dblRate,sum(a.dblquantity) as dblquantity ,sum(a.dblamount)as dblamount,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') "
	                        + " from vqbillhddtl a ,tblmenuitempricingdtl b ,tblcounterhd c ,tblmenuhd d ,tblitemmaster e,tblsubgrouphd f,tblgrouphd g "
	                        + " where a.stritemcode = b.strItemCode and a.strCounterCode=c.strCounterCode "
	                        + " and a.stritemcode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=g.strGroupCode "
	                        + " and b.strMenuCode=d.strMenuCode and (a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPoscode = '" + posCode + "' ");
	                }
	                sbSql.append(" and a.strAdvBookingNo ='' "
	                        + " group by c.strCounterCode,c.strCounterName, g.strGroupCode,g.strGroupName "
	                        + " order by c.strCounterCode,c.strCounterName,g.strGroupCode,g.strGroupName ");
	            }

	            List list=objBaseService.funGetList(sbSql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              		 List<String> arrListItem = new ArrayList<String>();
	                     arrListItem.add(ob[1].toString());
	                     arrListItem.add(ob[7].toString());
	                     arrListItem.add(ob[3].toString());
	                     arrListItem.add(ob[4].toString());
	                     arrListItem.add(ob[5].toString());
	                     arrListItem.add(ob[6].toString());
	                     arrListItem.add(" ");

	                     totalQty = totalQty + Double.parseDouble(ob[5].toString());
	                     totalAmount = totalAmount + Double.parseDouble(ob[6].toString());
	                     mapExcelItemDtl.put(j, arrListItem);
	                     j++;
	              	}
	            }
	           
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Counter Name");
	            arrHeaderList.add("BillDate");
	            //if (cmbSettlementMode.getSelectedItem().toString().equalsIgnoreCase("Group Wise"))
	            if (false)
	            {
	                arrHeaderList.add("Group Name");
	            }
	            //else if (cmbSettlementMode.getSelectedItem().toString().equalsIgnoreCase("Sub Group Wise"))
	            else if (false)
	            {
	                arrHeaderList.add("SubGroup Name");
	            }
	            else
	            {
	                arrHeaderList.add("Menu Name");
	            }

	            arrHeaderList.add("Rate");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Counter Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("Type" + " : " + "Menu Name");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "counterWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateNCKOTExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            StringBuilder sbSql = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;

	            sbSql.setLength(0);
	            sbSql.append(" select a.strKOTNo, a.dteNCKOTDate, a.strTableNo, b.strReasonName,d.strPosName, "
	                    + " a.strRemark,  a.strItemCode, c.strItemName, a.dblQuantity, a.dblRate, a.dblQuantity * a.dblRate as Amount,e.strTableName "
	                    + " from tblnonchargablekot a, tblreasonmaster b, tblitemmaster c,tblposmaster d,tbltablemaster e "
	                    + " where  a.strReasonCode = b.strReasonCode and a.strTableNo=e.strTableNo "
	                    + " and a.strItemCode = c.strItemCode  and a.strPosCode=d.strPOSCode ");
	            if (!posCode.equals("All"))
	            {
	                sbSql.append(" AND a.strPoscode = '" + posCode + "' ");
	            }
//	            if (!reasonCode.equals("All"))
//	            {
//	                sbSql.append(" and a.strReasonCode ='" + reasonCode + "' ");
//	            }
	            sbSql.append(" and date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' ");

	            List list=objBaseService.funGetList(sbSql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              		List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(ob[0].toString());
	                    arrListItem.add(ob[1].toString());
	                    arrListItem.add(ob[4].toString());
	                    arrListItem.add(ob[2].toString());
	                    arrListItem.add(ob[7].toString());
	                    arrListItem.add(ob[8].toString());
	                    arrListItem.add(ob[10].toString());
	                    arrListItem.add(ob[3].toString());
	                    arrListItem.add(ob[5].toString());

	                    totalQty = totalQty + Double.parseDouble(ob[88].toString());
	                    totalAmount = totalAmount + Double.parseDouble(ob[10].toString());
	                    mapExcelItemDtl.put(j, arrListItem);
	                    j++;

	              	}
	            }
	           
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount) + "#" + "7"));

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("KOT NO.");
	            arrHeaderList.add("NCKOT Date");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Table No");
	            arrHeaderList.add("Rate");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("Reason");
	            arrHeaderList.add("Remarks");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Non Chargable KOT Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("Reason" + " : All");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "NCKOTExcelSheet");
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

	    private void funGenerateAuditorExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            StringBuilder sbSqlDisLive = new StringBuilder();
	            StringBuilder sbSqlQDisFile = new StringBuilder();
	            StringBuilder sbSqlDisFilters = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();

	            sbSqlDisLive.setLength(0);
	            sbSqlQDisFile.setLength(0);
	            sbSqlDisFilters.setLength(0);

	            sql =new StringBuilder(" select a.strBillNo,ifnull(d.strSettelmentDesc,'ND') as payMode,ifnull(a.dblSubTotal,0.00) as subTotal,"
	                    + " a.dblTaxAmt,a.dblDiscountAmt,ifnull(c.dblSettlementAmt,0.00) as settleAmt, "
	                    + " ifnull(e.strCustomerName,'') as CustomerName "
	                    + " from vqbillhd  a "
	                    + " left outer join vqbillsettlementdtl c on a.strBillNo=c.strBillNo  "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
	                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            if (!posCode.equals("All"))
	            {
	                sql.append(" and a.strPOSCode= '" + posCode + "' ");
	            }

	            sql.append(" Order By d.strSettelmentDesc");

	            Map<String, Map<String, clsPOSOperatorWiseReportBean>> hmOperatorWiseSales = new HashMap<String, Map<String, clsPOSOperatorWiseReportBean>>();
	            Map<String, clsPOSOperatorWiseReportBean> hmSettlementDtl = null;
	            clsPOSOperatorWiseReportBean objOperatorWiseSales = null;

	            List list=objBaseService.funGetList(sql, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              		 if (hmOperatorWiseSales.containsKey(ob[0].toString()))
	                     {
	                         hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
	                         if (hmSettlementDtl.containsKey(ob[1].toString()))
	                         {
	                             objOperatorWiseSales = hmSettlementDtl.get(ob[1].toString());
	                             objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt() + Double.parseDouble(ob[5].toString()));
	                         }
	                         else
	                         {
	                             objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
	                             objOperatorWiseSales.setStrUserCode(ob[0].toString());
	                             objOperatorWiseSales.setStrSettlementDesc(ob[1].toString());
	                             objOperatorWiseSales.setStrUserName(ob[6].toString());
	                             objOperatorWiseSales.setStrPOSName(String.valueOf(0));
	                             objOperatorWiseSales.setStrPOSCode(String.valueOf(0));
	                             objOperatorWiseSales.setDiscountAmt(0);
	                             objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[5].toString()));
	                         }
	                         hmSettlementDtl.put(ob[1].toString(), objOperatorWiseSales);
	                     }
	                     else
	                     {

	                         objOperatorWiseSales = new clsPOSOperatorWiseReportBean();
	                         objOperatorWiseSales.setStrUserCode(ob[0].toString());
	                         objOperatorWiseSales.setStrSettlementDesc(ob[1].toString());
	                         objOperatorWiseSales.setStrUserName(ob[6].toString());
	                         objOperatorWiseSales.setStrPOSName(String.valueOf(0));
	                         objOperatorWiseSales.setStrPOSCode(String.valueOf(0));
	                         objOperatorWiseSales.setDiscountAmt(0);
	                         objOperatorWiseSales.setSettleAmt(Double.parseDouble(ob[5].toString()));

	                         hmSettlementDtl = new HashMap<String, clsPOSOperatorWiseReportBean>();
	                         hmSettlementDtl.put(ob[1].toString(), objOperatorWiseSales);
	                     }
	                     hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
	              	}
	            }
	           

	            sbSqlDisLive.append("SELECT b.strBillNo, b.strPOSCode, c.strPOSName "
	                    + ",sum(b.dblSubTotal),sum(b.dblDiscountAmt),sum(b.dblTaxAmt),'SANGUINE' "
	                    + " FROM tblbillhd b inner join tblposmaster c on b.strPOSCode=c.strPOSCode  "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            sbSqlQDisFile.append(" SELECT b.strBillNo, b.strPOSCode, c.strPOSName"
	                    + ",sum(b.dblSubTotal),sum(b.dblDiscountAmt),sum(b.dblTaxAmt),'SANGUINE' "
	                    + " FROM tblqbillhd b inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
	                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

	            if (!posCode.equals("All"))
	            {
	                sbSqlDisFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
	            }

	            sbSqlDisFilters.append(" GROUP BY b.strBillNo, b.strPosCode");

	            sbSqlDisLive.append(sbSqlDisFilters);
	            sbSqlQDisFile.append(sbSqlDisFilters);

	            //System.out.println(sbSqlDisLive);
	            //System.out.println(sbSqlQDisFile);
	            double dis = 0;

		        list=objBaseService.funGetList(sbSqlDisLive, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              	    if (hmOperatorWiseSales.containsKey(ob[0].toString()))
	                    {
	                        hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
	                        Set<String> setKeys = hmSettlementDtl.keySet();
	                        for (String keys : setKeys)
	                        {
	                            objOperatorWiseSales = hmSettlementDtl.get(keys);
	                            objOperatorWiseSales.setStrPOSName(String.valueOf(objOperatorWiseSales.getStrPOSName() + Double.parseDouble(ob[3].toString())));
	                            objOperatorWiseSales.setStrPOSCode(String.valueOf(objOperatorWiseSales.getStrPOSCode() + Double.parseDouble(ob[5].toString())));
	                            dis = objOperatorWiseSales.getDiscountAmt();
	                            objOperatorWiseSales.setDiscountAmt(dis + Double.parseDouble(ob[4].toString()));
	                            hmSettlementDtl.put(keys, objOperatorWiseSales);
	                            break;
	                        }
	                        hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
	                    }
	                
	              		
	              	}
	            }
	            
		        list=objBaseService.funGetList(sbSqlQDisFile, "sql");
	            if(list.size()>0)
	            {
	            	int j = 1;
	            	for(int i=0;i<list.size();i++)
	              	{
	              		Object[] ob=(Object[])list.get(i);
	              		 if (hmOperatorWiseSales.containsKey(ob[0].toString()))
	                     {
	                         hmSettlementDtl = hmOperatorWiseSales.get(ob[0].toString());
	                         Set<String> setKeys = hmSettlementDtl.keySet();
	                         for (String keys : setKeys)
	                         {
	                             objOperatorWiseSales = hmSettlementDtl.get(keys);
	                             objOperatorWiseSales.setStrPOSName(String.valueOf(objOperatorWiseSales.getStrPOSName() + Double.parseDouble(ob[3].toString())));
	                             objOperatorWiseSales.setStrPOSCode(String.valueOf(objOperatorWiseSales.getStrPOSCode() + Double.parseDouble(ob[5].toString())));
	                             dis = objOperatorWiseSales.getDiscountAmt();
	                             objOperatorWiseSales.setDiscountAmt(dis + Double.parseDouble(ob[4].toString()));
	                             hmSettlementDtl.put(keys, objOperatorWiseSales);
	                             break;
	                         }
	                         hmOperatorWiseSales.put(ob[0].toString(), hmSettlementDtl);
	                     }
	              	}
	            }
	           
	            objBaseService.funExecuteUpdate("truncate tbltempsalesflash","sql");
	            
	            String sqlInsert = "insert into tbltempsalesflash (strcode,strname,dblsubtotal,struser,dblamount,dblRate,dbldiscamt)values ";
	            double discAmt = 0, grandAmt = 0, subTotalAmt = 0, taxAmt = 0;
	            Object[] arrObjTableRowData = new Object[6];
	            int i = 0;
	            for (Map.Entry<String, Map<String, clsPOSOperatorWiseReportBean>> entry : hmOperatorWiseSales.entrySet())
	            {
	                Map<String, clsPOSOperatorWiseReportBean> hmOpSettlementDtl = entry.getValue();
	                for (Map.Entry<String, clsPOSOperatorWiseReportBean> entryOp : hmOpSettlementDtl.entrySet())
	                {
	                    clsPOSOperatorWiseReportBean objOperatorDtl = entryOp.getValue();
	                    sqlInsert += "('" + objOperatorDtl.getStrUserCode() + "','" + objOperatorDtl.getStrUserName() + "',"
	                            + " '" + objOperatorDtl.getStrPOSName() + "','" + objOperatorDtl.getStrSettlementDesc() + "',"
	                            + " '" + objOperatorDtl.getSettleAmt() + "','" + objOperatorDtl.getStrPOSCode() + "','" + objOperatorDtl.getDiscountAmt() + "'),";
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(objOperatorDtl.getStrUserCode());
	                    arrListItem.add(objOperatorDtl.getStrSettlementDesc());
	                    arrListItem.add(objOperatorDtl.getStrPOSName());
	                    arrListItem.add(objOperatorDtl.getStrPOSCode());
	                    arrListItem.add(String.valueOf(objOperatorDtl.getDiscountAmt()));
	                    arrListItem.add(String.valueOf(objOperatorDtl.getSettleAmt()));
	                    arrListItem.add(objOperatorDtl.getStrUserName());

	                    discAmt = discAmt + objOperatorDtl.getDiscountAmt();
	                    taxAmt = taxAmt + Double.valueOf(objOperatorDtl.getStrPOSCode());
	                    subTotalAmt = subTotalAmt + Double.valueOf(objOperatorDtl.getStrPOSName());
	                    grandAmt = grandAmt + objOperatorDtl.getSettleAmt();
	                    mapExcelItemDtl.put(i, arrListItem);
	                    i++;

	                }
	            }

	            arrListTotal.add(String.valueOf(Math.rint(subTotalAmt)) + "#" + "3");
	            arrListTotal.add(String.valueOf(Math.rint(taxAmt)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(discAmt)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(grandAmt)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("Pay Mode");
	            arrHeaderList.add("Subtotal");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add("Discount Amt");
	            arrHeaderList.add("Grand Total");
	            arrHeaderList.add("Customer Name");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Auditor Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "audiorExcelSheetReport");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	  /*  
	    private void funGenerateTaxBreakUpExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	        	JSONObject jsonConfig=new JSONObject(); 
	            JSONObject jsonData=objPOSConfigSettingService.funLoadPOSConfigSetting(strClientCode);
	            JSONArray jArr=(JSONArray)jsonData.get("configSetting");
	            jsonConfig=jArr.getJSONObject(0);
	            String exportReportPath=jsonConfig.getString("strExportPath");
	        	
	        	 
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalTaxableAmount = 0;
	            double totalTaxAmt = 0;

	            sql = " select sum(b.dblTaxableAmount),sum(b.dblTaxAmount)"
	                    + " ,c.strTaxDesc  "
	                    + " from vqbillhd a "
	                    + " left Outer join vqbilltaxdtl b on a.strBillNo=b.strBillNo "
	                    + " left outer join tbltaxhd c on b.strTaxCode=c.strTaxCode "
	                    + " left outer join tblposmaster d on a.strPOSCode=d.strPosCode "
	                    + " where date(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ";
	            if (!posCode.equals("All"))
	            {
	                sql += " and a.strPOSCode= '" + posCode + "' ";
	            }
	            sql += " and b.dblTaxableAmount IS NOT NULL "
	                    + " Group By c.strTaxCode,c.strTaxDesc ";
	            
	            Query query=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List list=query.objBaseService.funGetList(sbSqlLive, "sql");
	            int p = 1;
	    		if(list.size()>0)
	              {	
	        			
		              	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              		List<String> arrListItem = new ArrayList<String>();
		                    arrListItem.add(ob[2].toString());
		                    arrListItem.add(ob[1].toString());
		                    arrListItem.add(ob[0].toString());
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    totalTaxAmt = totalTaxAmt + Double.parseDouble(ob[1].toString());
		                    totalTaxableAmount = totalTaxableAmount + Double.parseDouble(ob[0].toString());
		                    mapExcelItemDtl.put(p, arrListItem);
		                    p++;
		              	}
	              }
	         
	            arrListTotal.add(String.valueOf(Math.rint(totalTaxableAmount)) + "#" + "2");
	            arrListTotal.add(String.valueOf(Math.rint(totalTaxAmt)) + "#" + "3");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Tax Desc");
	            arrHeaderList.add("Taxable Amt");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Tax Breakup Summary Report");
	            arrparameterList.add("POS : " + posName);
	            arrparameterList.add("FromDate : " + fromDate);
	            arrparameterList.add("ToDate : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");
	            //objUtility.funCreateExcelSheet(arrparameterList,arrHeaderList,mapExcelItemDtl, arrListTotal,"taxBreakupExcelSheet");
	            File file = new File(exportReportPath + "\\taxBreakupExcelSheet.xls");

	            WritableWorkbook workbook1 = Workbook.createWorkbook(file);
	            WritableSheet sheet1 = workbook1.createSheet("First Sheet", 0);
	            WritableFont cellFont = new WritableFont(WritableFont.COURIER, 14);
	            cellFont.setBoldStyle(WritableFont.BOLD);
	            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	            WritableFont cellFont1 = new WritableFont(WritableFont.COURIER, 12);
	            cellFont1.setBoldStyle(WritableFont.BOLD);
	            WritableCellFormat cellFormat1 = new WritableCellFormat(cellFont1);
	            WritableFont headerCellFont = new WritableFont(WritableFont.TIMES, 10);
	            headerCellFont.setBoldStyle(WritableFont.BOLD);
	            WritableCellFormat headerCell = new WritableCellFormat(headerCellFont);

	            for (int j = 0; j <= arrparameterList.size(); j++)
	            {
	                Label l0 = new Label(1, 0, arrparameterList.get(0), cellFormat);
	                Label l1 = new Label(0, 2, arrparameterList.get(1), headerCell);
	                Label l2 = new Label(1, 2, arrparameterList.get(2), headerCell);
	                Label l3 = new Label(2, 2, arrparameterList.get(3), headerCell);
	                Label l4 = new Label(0, 3, arrparameterList.get(4), headerCell);
	                Label l5 = new Label(1, 3, arrparameterList.get(5), headerCell);

	                sheet1.addCell(l0);
	                sheet1.addCell(l1);
	                sheet1.addCell(l2);
	                sheet1.addCell(l3);
	                sheet1.addCell(l4);
	                sheet1.addCell(l5);
	            }

	            Label labelTaxBreakup = new Label(0, 5, "Tax Breakup Summary", cellFormat1);
	            sheet1.addCell(labelTaxBreakup);
	            sheet1.setColumnView(5, 15);

	            for (int j = 0; j <= arrHeaderList.size(); j++)
	            {
	                Label l0 = new Label(0, 7, arrHeaderList.get(0), headerCell);
	                Label l1 = new Label(1, 7, arrHeaderList.get(1), headerCell);
	                Label l2 = new Label(2, 7, arrHeaderList.get(2), headerCell);
	                Label l3 = new Label(3, 7, arrHeaderList.get(3), headerCell);
	                Label l4 = new Label(4, 7, arrHeaderList.get(4), headerCell);
	                Label l5 = new Label(5, 7, arrHeaderList.get(5), headerCell);
	                Label l6 = new Label(6, 7, arrHeaderList.get(6), headerCell);
	                Label l7 = new Label(7, 7, arrHeaderList.get(7), headerCell);
	                sheet1.addCell(l0);
	                sheet1.addCell(l1);
	                sheet1.addCell(l2);
	                sheet1.addCell(l3);
	                sheet1.addCell(l4);
	                sheet1.addCell(l5);
	                sheet1.addCell(l6);
	                sheet1.addCell(l7);
	            }

	            p = 9;
	            for (Map.Entry<Integer, List<String>> entry : mapExcelItemDtl.entrySet())
	            {
	                Label lbl0 = new Label(0, p, entry.getKey().toString());
	                List<String> nameList = mapExcelItemDtl.get(entry.getKey());
	                for (int j = 0; j <= nameList.size(); j++)
	                {
	                    Label lbl1 = new Label(1, p, nameList.get(0));
	                    Label lbl2 = new Label(2, p, nameList.get(1));
	                    Label lbl3 = new Label(3, p, nameList.get(2));
	                    Label lbl4 = new Label(4, p, nameList.get(3));
	                    Label lbl5 = new Label(5, p, nameList.get(4));
	                    Label lbl6 = new Label(6, p, nameList.get(5));
	                    Label lbl7 = new Label(7, p, nameList.get(6));

	                    sheet1.addCell(lbl1);
	                    sheet1.addCell(lbl2);
	                    sheet1.addCell(lbl3);
	                    sheet1.addCell(lbl4);
	                    sheet1.addCell(lbl5);
	                    sheet1.addCell(lbl6);
	                    sheet1.addCell(lbl7);
	                    sheet1.setColumnView(p, 15);
	                }
	                sheet1.addCell(lbl0);
	                p++;
	            }

	            for (int j = 0; j < arrListTotal.size(); j++)
	            {
	                String[] l0 = new String[10];
	                for (int c = 0; c < arrListTotal.size(); c++)
	                {
	                    l0 = arrListTotal.get(c).split("#");
	                    int position = Integer.parseInt(l0[1]);
	                    Label lable0 = new Label(position, p + 1, l0[0], headerCell);
	                    sheet1.addCell(lable0);
	                }
	                Label labelTotal = new Label(0, p + 1, "TOTAL:", headerCell);
	                sheet1.addCell(labelTotal);
	            }

	            // Menu Head Wise Tax Break up        
	            p += 4;
	            mapExcelItemDtl.clear();
	            arrListTotal.clear();
	            arrHeaderList.clear();

	            sql = " select d.strItemName,sum(d.dblamount) "
	                    + " from vqbillhd a "
	                    + " left Outer join vqbilltaxdtl b on a.strBillNo=b.strBillNo "
	                    + " left outer join tbltaxhd c on b.strTaxCode=c.strTaxCode "
	                    + " left outer join vqbillhddtl d on a.strBillNo=d.strBillNo "
	                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and b.dblTaxableAmount IS NOT NULL ";
	            if (!posCode.equals("All"))
	            {
	                sql += " and a.strPOSCode= '" + posCode + "' ";
	            }
	            sql += " group by d.stritemcode,d.strItemName "
	                    + " order by d.strItemName ";
	            
	            query=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            list=query.objBaseService.funGetList(sbSqlLive, "sql");
	            double totalAmt = 0;
	            int cnt = 1;
	    		if(list.size()>0)
	              {	
	        			
		              	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              	    List<String> arrListItem = new ArrayList<String>();
		                    arrListItem.add(ob[0].toString());
		                    arrListItem.add(ob[1].toString());
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    totalAmt = totalAmt + Double.parseDouble(ob[1].toString());
		                    mapExcelItemDtl.put(cnt, arrListItem);
		                    cnt++;

		              	}
	              }
	            arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "2");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Description");
	            arrHeaderList.add("Amt");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");

	            int h = p - 1;
	            Label labelMenuBreakup = new Label(0, h, "MenuHead Breakup Summary", cellFormat1);
	            sheet1.addCell(labelMenuBreakup);
	            sheet1.setColumnView(h, 15);

	            p = p + 1;
	            for (int j = 0; j <= arrHeaderList.size(); j++)
	            {
	                Label l0 = new Label(0, p, arrHeaderList.get(0), headerCell);
	                Label l1 = new Label(1, p, arrHeaderList.get(1), headerCell);
	                Label l2 = new Label(2, p, arrHeaderList.get(2), headerCell);
	                Label l3 = new Label(3, p, arrHeaderList.get(3), headerCell);
	                Label l4 = new Label(4, p, arrHeaderList.get(4), headerCell);
	                Label l5 = new Label(5, p, arrHeaderList.get(5), headerCell);
	                Label l6 = new Label(6, p, arrHeaderList.get(6), headerCell);
	                Label l7 = new Label(7, p, arrHeaderList.get(7), headerCell);
	                sheet1.addCell(l0);
	                sheet1.addCell(l1);
	                sheet1.addCell(l2);
	                sheet1.addCell(l3);
	                sheet1.addCell(l4);
	                sheet1.addCell(l5);
	                sheet1.addCell(l6);
	                sheet1.addCell(l7);
	            }

	            for (Map.Entry<Integer, List<String>> entry : mapExcelItemDtl.entrySet())
	            {
	                Label lbl0 = new Label(0, p, entry.getKey().toString());
	                List<String> nameList = mapExcelItemDtl.get(entry.getKey());
	                for (int j = 0; j <= nameList.size(); j++)
	                {
	                    Label lbl1 = new Label(1, p, nameList.get(0));
	                    Label lbl2 = new Label(2, p, nameList.get(1));
	                    Label lbl3 = new Label(3, p, nameList.get(2));
	                    Label lbl4 = new Label(4, p, nameList.get(3));
	                    Label lbl5 = new Label(5, p, nameList.get(4));
	                    Label lbl6 = new Label(6, p, nameList.get(5));
	                    Label lbl7 = new Label(7, p, nameList.get(6));

	                    sheet1.addCell(lbl1);
	                    sheet1.addCell(lbl2);
	                    sheet1.addCell(lbl3);
	                    sheet1.addCell(lbl4);
	                    sheet1.addCell(lbl5);
	                    sheet1.addCell(lbl6);
	                    sheet1.addCell(lbl7);
	                    sheet1.setColumnView(p, 15);
	                }
	                sheet1.addCell(lbl0);
	                p++;
	            }

	            for (int j = 0; j < arrListTotal.size(); j++)
	            {
	                String[] l0 = new String[10];
	                for (int c = 0; c < arrListTotal.size(); c++)
	                {
	                    l0 = arrListTotal.get(c).split("#");
	                    int position = Integer.parseInt(l0[1]);
	                    Label lable0 = new Label(position, p + 1, l0[0], headerCell);
	                    sheet1.addCell(lable0);
	                }
	                Label labelTotal = new Label(0, p + 1, "TOTAL:", headerCell);
	                sheet1.addCell(labelTotal);
	            }
//	            workbook1.write();
//	            workbook1.close();
//	            Desktop dt = Desktop.getDesktop();
//	            dt.open(file);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	   */ 
	    private void funGenerateMenuHeadWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmount = 0;
	            double subTotal = 0;
	            double discountTotal = 0;

	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            sbSqlQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
	                    + "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + userCode + "',sum(a.dblRate),sum(a.dblAmount) ,sum(a.dblDiscountAmt) "
	                    + "FROM tblqbilldtl a\n"
	                    + "left outer join tblqbillhd b on a.strBillNo=b.strBillNo \n"
	                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
	                    + "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
	                    + " and b.strposcode =d.strposcode ");
	            
	            String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(userCode,posCode, "gAreaWisePricing");
	            if (gAreaWisePricing.equals("Y"))
	            {
	                sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
	            }
	            sbSqlQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
	            sbSqlQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and a.strClientCode=b.strClientCode ");

	            sbSqlLive.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
	                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + userCode + "',sum(a.dblRate) ,sum(a.dblAmount),sum(a.dblDiscountAmt) "
	                    + " FROM tblbilldtl a\n"
	                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo \n"
	                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
	                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
	                    + " and b.strposcode =d.strposcode ");
	            if (gAreaWisePricing.equals("Y"))
	            {
	                sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
	            }
	            sbSqlLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
	            sbSqlLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                    + " and a.strClientCode=b.strClientCode ");

	            String sqlModLive = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
	                    + "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + userCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
	                    + "FROM tblbillmodifierdtl a\n"
	                    + "left outer join tblbillhd b on a.strBillNo=b.strBillNo \n"
	                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
	                    + "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
	                    + " and b.strposcode =d.strposcode ";

	            if (gAreaWisePricing.equals("Y"))
	            {
	                sqlModLive += "and b.strAreaCode= d.strAreaCode ";
	            }
	            sqlModLive += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
	            sqlModLive += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
	                    + " and a.strClientCode=b.strClientCode ";

	            String sqlModQFile = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
	                    + "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + userCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
	                    + "FROM tblqbillmodifierdtl a\n"
	                    + "left outer join tblqbillhd b on a.strBillNo=b.strBillNo \n"
	                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
	                    + "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
	                    + " and b.strposcode =d.strposcode ";

	            if (gAreaWisePricing.equals("Y"))
	            {
	                sqlModQFile += "and b.strAreaCode= d.strAreaCode ";
	            }
	            sqlModQFile += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
	            sqlModQFile += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
	                    + " and a.strClientCode=b.strClientCode ";

	            if (!posCode.equals("All"))
	            {
	                sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
	            }
	            sbSqlFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);
	            //System.out.println(sbSqlLive);
	            //System.out.println(sbSqlQFile);
	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash;","sql");

	            String sqlInsertLiveBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlLive.toString()+");";
	            
	            String sqlInsertQFileBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlQFile.toString()+");";
	            
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	            
	            sqlModLive = sqlModLive + " " + sbSqlFilters.toString();
	            sqlModQFile = sqlModQFile + " " + sbSqlFilters.toString();
	            //System.out.println(sqlModLive);
	            //System.out.println(sqlModQFile);

	             sqlInsertLiveBillSales = "insert into tbltempsalesflash "
	                    + "(" + sqlModLive + ");";
	             sqlInsertQFileBillSales = "insert into tbltempsalesflash "
	                    + "(" + sqlModQFile + ");";
	             
	             objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	             objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	             sql =new StringBuilder( " SELECT strname, strposcode, sum(dblquantity), sum(dblamount),sum(dblsubtotal),sum(dbldiscamt)"
	                    + " FROM tbltempsalesflash "
	                    + " group by strcode, strname, strposcode ");
	          
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
		                    arrListItem.add(" ");

		                    totalQty = totalQty + Double.parseDouble(ob[2].toString());
		                    totalAmount = totalAmount + Double.parseDouble(ob[3].toString());
		                    subTotal = subTotal + Double.parseDouble(ob[4].toString());
		                    discountTotal = discountTotal + Double.parseDouble(ob[5].toString());
		                    mapExcelItemDtl.put(p, arrListItem);
		                    p++;
		              	}
	              }
	            
	            arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "3");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(subTotal)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(discountTotal)) + "#" + "6");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("MenuName");
	            arrHeaderList.add("POSName");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Sale AMT");
	            arrHeaderList.add("SubTotal");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("MenuHeadWise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "menuWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateWaiterWiseItemExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmt = 0;
	            String WaiterCode = "All";

	            sql =new StringBuilder(" select ifnull(c.strWShortName,'ND') as WaiterName,b.strItemName,d.strPosName,a.dblquantity,a.dblRate,a.dblamount,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),a.strBillNo "
	                    + " from vqbillhddtl a "
	                    + " left outer join tblitemmaster b on a.stritemcode=b.strItemCode "
	                    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo  "
	                    + " left outer join tblposmaster d on a.strPosCode=d.strPosCode "
	                    + " where date(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sql.append(" and a.strPOSCode= '" + posCode + "' ");
	            }
//	            if (!WaiterCode.equals("All"))
//	            {
//	                sql += " and a.strWaiterNo= '" + waiterNo + "' ";
//	            }

	            sql.append(" order by c.strWShortName");

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
		                    arrListItem.add(ob[6].toString());
		                    arrListItem.add(ob[3].toString());
		                    arrListItem.add(ob[4].toString());
		                    arrListItem.add(ob[5].toString());

		                    totalQty = totalQty + Double.parseDouble(ob[3].toString());
		                    totalAmt = totalAmt + Double.parseDouble(ob[5].toString());

		                    mapExcelItemDtl.put(p, arrListItem);

		                    p++;
		              	}
	              }
	           
	    		arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Waiter Name");
	            arrHeaderList.add("Item Name");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Bill Date");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Rate");
	            arrHeaderList.add("Amount");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Waiter Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add("Waiter" + " : All");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "waiterWiseExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateWaiterWiseIncentivesExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalAmount = 0;
	            double totalQty = 0;
	            double totalIncentiveAmt = 0;

//	            if (cmbSettlementMode.getSelectedItem().equals("Summary"))
	            if (false)
	            {
	                sql=new StringBuilder(" select a.strBillNo,a.strWaiterNo,a.WaiterName,sum(a.ItemQty) as Quantity ,sum(a.Amount) as Amount , "
	                        + "sum(a.IncentiveAmt) as IncentiveAmount from  "
	                        + "(select a.strBillNo as strBillNo,ifnull(b.strWaiterNo,'ND') as strWaiterNo,ifnull(b.strWShortName,'ND') as WaiterName,e.strSubGroupName,sum(a.dblQuantity) as ItemQty,sum(a.dblAmount) as Amount,round(sum(a.dblAmount)*(e.strIncentives/100),2) as IncentiveAmt "
	                        + "from vqbillhddtl a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo	"
	                        + "left outer join tblitemmaster d on a.strItemCode=d.strItemCode "
	                        + "left outer join tblsubgrouphd e on d.strSubGroupCode=e.strSubGroupCode "
	                        + " and Date(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode= '" + posCode + "' ");
	                }

	                sql.append(" group by a.strBillNo,b.strWShortName,e.strSubGroupCode) a "
	                        + " group by a.WaiterName,a.strBillNo  ");

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
	    	                     arrListItem.add(" ");

	    	                     totalAmount = totalAmount + Double.parseDouble(ob[4].toString());
	    	                     totalIncentiveAmt = totalIncentiveAmt + Double.parseDouble(ob[5].toString());
	    	                     totalQty = totalQty + Double.parseDouble(ob[3].toString());
	    	                     mapExcelItemDtl.put(p, arrListItem);
	    	                     p++;
	    	              	}
	                  }
	                
	                arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");
	                arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "5");
	                arrListTotal.add(String.valueOf(Math.rint(totalIncentiveAmt)) + "#" + "6");

	                arrHeaderList.add("Serial No");
	                arrHeaderList.add("Bill No");
	                arrHeaderList.add("Waiter Code");
	                arrHeaderList.add("Waiter Name");
	                arrHeaderList.add("Qty");
	                arrHeaderList.add("Amount");
	                arrHeaderList.add("Incentive Amt");
	                arrHeaderList.add(" ");

	                List<String> arrparameterList = new ArrayList<String>();
	                arrparameterList.add("WaiterWise Incentives Summary Report");
	                arrparameterList.add("POS" + " : " + posName);
	                arrparameterList.add("FromDate" + " : " + fromDate);
	                arrparameterList.add("ToDate" + " : " + toDate);
	                arrparameterList.add("");
	                arrparameterList.add("  ");

	                funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "waiterIncentivesSummaryExcelSheet");

	            }
	            else
	            {
	                sql=new StringBuilder(" select a.strBillNo,ifnull(b.strWShortName,'ND') as WaiterName  ,e.strSubGroupName,sum(a.dblQuantity) as ItemQty "
	                        + " ,sum(a.dblAmount) as Amount,e.strIncentives as IncentivePer "
	                        + " ,round(sum(a.dblAmount)*(e.strIncentives/100),2) as IncentiveAmt "
	                        + " from vqbillhddtl a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
	                        + " left outer join tblitemmaster d on a.strItemCode=d.strItemCode "
	                        + " left outer join tblsubgrouphd e on d.strSubGroupCode=e.strSubGroupCode "
	                        + " where date(a.dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sql.append(" and a.strPOSCode= '" + posCode + "' ");
	                }

	                sql.append(" and e.strIncentives>0 "
	                        + " group by a.strBillNo,b.strWShortName,e.strSubGroupCode "
	                        + " order by b.strWShortName ");

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
	    	                    arrListItem.add(ob[6].toString());
	    	                    arrListItem.add(" ");

	    	                    totalAmount = totalAmount + Double.parseDouble(ob[4].toString());
	    	                    totalIncentiveAmt = totalIncentiveAmt + Double.parseDouble(ob[6].toString());
	    	                    totalQty = totalQty + Double.parseDouble(ob[3].toString());

	    	                    mapExcelItemDtl.put(p, arrListItem);

	    	                    p++;
	    	              	}
	                  }
	                
	        		arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");
	                arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "5");
	                arrListTotal.add(String.valueOf(Math.rint(totalIncentiveAmt)) + "#" + "6");

	                arrHeaderList.add("Serial No");
	                arrHeaderList.add("Bill No");
	                arrHeaderList.add("Waiter Name");
	                arrHeaderList.add("SubGroup Name");
	                arrHeaderList.add("Qty");
	                arrHeaderList.add("Amount");
	                arrHeaderList.add("Incentive Amt");
	                arrHeaderList.add(" ");

	                List<String> arrparameterList = new ArrayList<String>();
	                arrparameterList.add("WaiterWise Incentives Details Report");
	                arrparameterList.add("POS" + " : " + posName);
	                arrparameterList.add("FromDate" + " : " + fromDate);
	                arrparameterList.add("ToDate" + " : " + toDate);
	                arrparameterList.add(" ");
	                arrparameterList.add(" ");

	                funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "waiterIncentivesDtlExcelSheet");

	            }

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
	    
	    private void funGenerateDailyCollectionExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalSettleAmt = 0;
	            double totalTax = 0;
	            double totalsubTotal = 0;
	            double totalDisAmt = 0;

	            sql=new StringBuilder(" select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime,ifnull(b.strTableName,'') as TableName, "
	                    + " e.strPosName, ifnull(d.strSettelmentDesc,'') as payMode,ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt, "
	                    + " ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo, "
	                    + " a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(f.strCustomerName,'') as CustName  "
	                    + " from vqbillhd  a "
	                    + " left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + " left outer join vqbillsettlementdtl c on a.strBillNo=c.strBillNo "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tblposmaster e on a.strPOSCode=e.strPosCode "
	                    + " left outer join tblcustomermaster f on a.strCustomerCode=f.strCustomerCode "
	                    + " where date(dteBillDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sql.append(" and e.strPosCode= '" + posCode + "' ");
	            }

	            sql.append(" order by a.strPOSCode asc,d.strSettelmentDesc asc,a.strBillNo ");
	            List list=objBaseService.funGetList(sql, "sql");
	            int p = 1;
	    		if(list.size()>0)
	              {	
	        		  	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              		 List<String> arrListItem = new ArrayList<String>();
		                     arrListItem.add(ob[0].toString());
		                     arrListItem.add(ob[5].toString());
		                     arrListItem.add(ob[4].toString());
		                     arrListItem.add(ob[6].toString());
		                     arrListItem.add(ob[8].toString());
		                     arrListItem.add(ob[9].toString());
		                     arrListItem.add(ob[10].toString());
		                     arrListItem.add(ob[11].toString());
		                     arrListItem.add(ob[20].toString());

		                     totalsubTotal = totalsubTotal + Double.parseDouble(ob[6].toString());
		                     totalDisAmt = totalDisAmt + Double.parseDouble(ob[8].toString());
		                     totalTax = totalTax + Double.parseDouble(ob[9].toString());
		                     totalSettleAmt = totalSettleAmt + Double.parseDouble(ob[10].toString());

		                     mapExcelItemDtl.put(p, arrListItem);
		                     p++;
		              	}
	              }
	            
	            arrListTotal.add(String.valueOf(Math.rint(totalsubTotal)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(totalDisAmt)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalTax)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalSettleAmt)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("Pay Mode");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Sub Total");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add("Settle Amt");
	            arrHeaderList.add("User Name");
	            arrHeaderList.add("Customer Name");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Daily Collection Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "dailyCollectionExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateDailySalesExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	          
	            StringBuilder sbSqlBillWise = new StringBuilder();
	            StringBuilder sbSqlBillWiseQFile = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalSettleAmt = 0;
	            double totalTax = 0;
	            double totalsubTotal = 0;
	            double totalDisAmt = 0;

	            sbSqlBillWise.setLength(0);
	            sbSqlBillWise.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
	                    + ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode"
	                    + ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt"
	                    + ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
	                    + ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
	                    + ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
	                    + ",a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,'' "
	                    + "from tblbillhd  a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
	                    + "left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo "
	                    + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
	                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

	            if (!posCode.equals("All"))
	            {
	                sbSqlBillWise.append(" and a.strPOSCode='" + posCode + "' ");
	            }

	            sbSqlBillWise.append(" order by a.strBillNo desc");
	            //System.out.println("Bill Wise Report Live Query="+sbSqlBillWise);

	            sbSqlBillWiseQFile.setLength(0);
	            sbSqlBillWiseQFile.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
	                    + ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
	                    + ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt "
	                    + ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated "
	                    + ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges "
	                    + ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
	                    + ",a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,'' "
	                    + "from tblqbillhd  a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
	                    + "left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo "
	                    + "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
	                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

	            if (!posCode.equals("All"))
	            {
	                sbSqlBillWiseQFile.append(" and a.strPOSCode='" + posCode + "' ");
	            }
	            sbSqlBillWiseQFile.append(" order by a.strBillNo desc");
	            //System.out.println("Bill Wise Report QFile=" + sbSqlBillWiseQFile);
	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash1;","sql");
	            
	            String sqlInsertLiveBillSales="insert into tbltempsalesflash1("+sbSqlBillWise.toString()+");";
	            String sqlInsertQFileBillSales="insert into tbltempsalesflash1("+sbSqlBillWiseQFile.toString()+");";

	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");

	            String billNo="";
	             sql=new StringBuilder("select * from tbltempsalesflash1 "
	                + " where strbillno in (select strBillNo "
	                + " from tbltempsalesflash1 group by strbillno having count(*) > 1) ");
	        
	             List list=objBaseService.funGetList(sql, "sql");
	             if(list.size()>0)
	               {	
	         		  	for(int i=0;i<list.size();i++)
	 	              	{
	 	              		Object[] ob=(Object[])list.get(i);
		 	              	 if(ob[0].toString().equals(billNo))
		 	                {
		 	                    String sqlUpdate="update tbltempsalesflash1 "
		 	                        + " set dblsubtotal='0',dbldiscper='0',dbldiscamt='0'"
		 	                        + " ,dbltaxamt='0',dbltipamt='0' "
		 	                        + " where strUser='"+userCode+"' "
		 	                        + " and strBillNo='"+billNo+"' and strpaymode='"+ob[5].toString()+"' ";
		 	                     objBaseService.funExecuteUpdate(sqlUpdate,"sql");
		 	                }
		 	                billNo=ob[0].toString();
		 	            }
	               }
	            
	           
	            sql =new StringBuilder(" select a.strbillno,a.dtebilldate,a.tmebilltime,a.strtablename,a.strposcode,a.strpaymode, "
	                    + " CAST(a.dblsubtotal AS DECIMAL(10,2)) as dblsubtotal ,CAST(a.dbldiscper AS DECIMAL(10,2))as dbldiscper ,CAST(a.dbldiscamt AS DECIMAL(10,2))as dbldiscamt "
	                    + " ,CAST(a.dbltaxamt AS DECIMAL(10,2))as dbltaxamt,CAST(a.dblsettlementamt AS DECIMAL(10,2))as dblsettlementamt,a.strusercreated,a.struseredited "
	                    + " ,a.dtedatecreated,a.dtedateedited,a.strclientcode,a.strwaiterno,a.strcustomercode,CAST(a.dbldeliverycharges AS DECIMAL(10,2))as dbldeliverycharges "
	                    + " ,a.strremarks,a.strcustomername,CAST(a.dbltipamt AS DECIMAL(10,2))as dbltipamt,a.strUser "
	                    + " from tbltempsalesflash1 a ");
	            
	            
	            list=objBaseService.funGetList(sql, "sql");
	            int p = 1;
	    		if(list.size()>0)
	              {	
	        		  	for(int i=0;i<list.size();i++)
		              	{
		              		Object[] ob=(Object[])list.get(i);
		              		 List<String> arrListItem = new ArrayList<String>();
		                     arrListItem.add(ob[0].toString());
		                     arrListItem.add(ob[5].toString());
		                     arrListItem.add(ob[22].toString());
		                     arrListItem.add(ob[6].toString());
		                     arrListItem.add(ob[8].toString());
		                     arrListItem.add(ob[9].toString());
		                     arrListItem.add(ob[10].toString());

		                     totalsubTotal = totalsubTotal + Double.parseDouble(ob[6].toString());
		                     totalDisAmt = totalDisAmt + Double.parseDouble(ob[8].toString());
		                     totalTax = totalTax + Double.parseDouble(ob[9].toString());
		                     totalSettleAmt = totalSettleAmt + Double.parseDouble(ob[10].toString());

		                     mapExcelItemDtl.put(p, arrListItem);

		                     p++;

		              	}
	              }
	            arrListTotal.add(String.valueOf(Math.rint(totalsubTotal)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(totalDisAmt)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalTax)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalSettleAmt)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Bill No");
	            arrHeaderList.add("Pay Mode");
	            arrHeaderList.add("User Name");
	            arrHeaderList.add("Sub Total");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add("Tax Amt");
	            arrHeaderList.add("Settle Amt");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Daily Sales Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "dailySalesReportExcelSheet");

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    private void funGenerateVoidKOTExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalAmount = 0;
	            double totalQty = 0;

	            sql =new StringBuilder(" select a.strItemCode,a.strItemName,e.strTableName,(a.dblAmount/a.dblItemQuantity),a.dblItemQuantity,a.dblAmount,a.strRemark,a.strKOTNo "
	                    + ",b.strPosCode,b.strPosName,c.strBillNo,a.strUserCreated ,DATE_FORMAT(date(a.dteVoidedDate),'%d-%m-%Y'),d.strReasonName "
	                    + "from tblvoidkot a "
	                    + "left outer join tblposmaster b on a.strPOSCode=b.strPosCode "
	                    + "left outer join tblbilldtl c on a.strKOTNo=c.strKOTNo "
	                    + "left outer join tblreasonmaster d on a.strReasonCode=d.strReasonCode "
	                    + "left outer join tbltablemaster e on a.strTableNo=e.strTableNo "
	                    + "where date(a.dteVoidedDate) BETWEEN  '" + fromDate + "' and '" + toDate + "' ");

	            if (!posCode.equals("All"))
	            {
	                sql.append(" and b.strPosCode= '" + posCode + "' ");
	            }
	            sql.append(" order by a.strposcode,a.strusercreated,a.strkotno ");

	            List list=objBaseService.funGetList(sql, "sql");
	            int p = 1;
	   			if(list.size()>0)
	             {	
	       		  	for(int i=0;i<list.size();i++)
	              	{
		       		  		Object[] ob=(Object[])list.get(i);
			              	  List<String> arrListItem = new ArrayList<String>();
			                  arrListItem.add(ob[7].toString());
			                  arrListItem.add(ob[9].toString());
			                  arrListItem.add(ob[12].toString());
			                  arrListItem.add(ob[1].toString());
			                  arrListItem.add(ob[11].toString());
			                  arrListItem.add(ob[4].toString());
			                  arrListItem.add(ob[3].toString());
			                  arrListItem.add(ob[5].toString());
			                  arrListItem.add(ob[6].toString());
			                  arrListItem.add(ob[13].toString());
			                  totalQty = totalQty + Double.parseDouble(ob[4].toString());
			                  totalAmount = totalAmount + Double.parseDouble(ob[5].toString());
			                  mapExcelItemDtl.put(p, arrListItem);
			                  p++;
		
		              	}
	             }
	           
	   			arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("KOT No");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Voided Date");
	            arrHeaderList.add("ItemName");
	            arrHeaderList.add("User Name");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Rate");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add("Remark");
	            arrHeaderList.add("Reason");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Void KOT Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "voidKOTExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	    private void funGenerateSubGroupWiseSummaryExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	           
	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlModLive = new StringBuilder();
	            StringBuilder sbSqlQModFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalAmount = 0;
	            double totalQty = 0;
	            double totalsubTotal = 0;
	            double totalDisAmt = 0;

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlModLive.setLength(0);
	            sbSqlQModFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
	                    + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
	                    + "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d "
	                    + ",tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=d.strItemCode "
	                    + "and c.strSubGroupCode=d.strSubGroupCode ");

	            sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
	                    + ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + userCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
	                    + "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d "
	                    + ",tblposmaster f "
	                    + "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode "
	                    + "and b.strItemCode=d.strItemCode "
	                    + "and c.strSubGroupCode=d.strSubGroupCode ");

	            sbSqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName"
	                    + ",'" + userCode + "','0' ,'0.00','0.00' "
	                    + " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd c"
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and  left(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=c.strSubGroupCode "
	                    + " and b.dblamount>0 ");

	            sbSqlQModFile.append("select c.strSubGroupCode,c.strSubGroupName"
	                    + ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName"
	                    + ",'" + userCode + "','0' ,'0.00','0.00' "
	                    + " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
	                    + ",tblsubgrouphd c"
	                    + " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode "
	                    + " and  left(b.strItemCode,7)=d.strItemCode "
	                    + " and d.strSubGroupCode=c.strSubGroupCode "
	                    + " and b.dblamount>0 ");

	            sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
	            }

	            sbSqlFilters.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);
	            sbSqlModLive.append(sbSqlFilters);
	            sbSqlQModFile.append(sbSqlFilters);

	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash;","sql");
	            String sqlInsertLiveBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlLive.toString()+");";
	            
	            String sqlInsertQFileBillSales="insert into tbltempsalesflash "
	                + "("+sbSqlQFile.toString()+");";
	            
	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	             sqlInsertLiveBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlModLive + ");";

	             sqlInsertQFileBillSales = "insert into tbltempsalesflash "
	                    + "(" + sbSqlQModFile + ");";
	             
	             objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	             objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	             sql =new StringBuilder(" select a.strcode,a.strname,a.strposcode,sum(a.dblquantity),sum(a.dblamount),sum(a.dblsubtotal),sum(a.dbldiscamt) "
	                    + " from tbltempsalesflash a "
	                    + " group by a.strcode,a.strname,a.strposcode ");

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
		 	                arrListItem.add(ob[5].toString());
		 	                arrListItem.add(ob[6].toString());
		 	                arrListItem.add(ob[4].toString());
		
		 	                totalQty = totalQty + Double.parseDouble(ob[3].toString());
		 	                totalAmount = totalAmount + Double.parseDouble(ob[4].toString());
		 	                totalsubTotal = totalsubTotal + Double.parseDouble(ob[5].toString());
		 	                totalDisAmt = totalDisAmt + Double.parseDouble(ob[6].toString());
		
		 	                mapExcelItemDtl.put(p, arrListItem);
		
		 	                p++;
	               	}
	              }
	            
	    		arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");
	            arrListTotal.add(String.valueOf(Math.rint(totalsubTotal)) + "#" + "5");
	            arrListTotal.add(String.valueOf(Math.rint(totalDisAmt)) + "#" + "6");
	            arrListTotal.add(String.valueOf(Math.rint(totalAmount)) + "#" + "7");

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("SubGroup Code");
	            arrHeaderList.add("SubGroup Name");
	            arrHeaderList.add("POS Name");
	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Sub Total");
	            arrHeaderList.add("Discount");
	            arrHeaderList.add("Amount");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("SubGroup Summary Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "subGroupSummaryExcelSheet");

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

	    private void funGenerateRevenueHeadWiseExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        try
	        {
	            
	            StringBuilder sbSqlLive = new StringBuilder();
	            StringBuilder sbSqlQFile = new StringBuilder();
	            StringBuilder sbSqlFilters = new StringBuilder();
	            Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	            List<String> arrListTotal = new ArrayList<String>();
	            List<String> arrHeaderList = new ArrayList<String>();
	            double totalQty = 0;
	            double totalAmt = 0;

	            sbSqlLive.setLength(0);
	            sbSqlQFile.setLength(0);
	            sbSqlFilters.setLength(0);

	            sbSqlQFile.append("select e.strRevenueHead,d.strMenuName,a.strItemName, SUM(a.dblQuantity), SUM(a.dblAmount) "
	                    + "from tblqbilldtl a,tblqbillhd b ,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
	                    + "where a.strBillNo=b.strBillNo  "
	                    + "and a.strItemCode=c.strItemCode "
	                    + "and c.strMenuCode=d.strMenuCode "
	                    + "and a.strItemCode=e.strItemCode "
	                    + "and c.strPosCode=if(c.strPosCode='All','All',b.strPOSCode) "
	                    + "and b.strAreaCode=c.strAreaCode ");

	            String sqlModQFile = "SELECT e.strRevenueHead,d.strMenuName,a.strModifierName, SUM(a.dblQuantity), SUM(a.dblAmount) "
	                    + "FROM tblqbillmodifierdtl a,tblqbillhd b,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
	                    + "WHERE a.strBillNo=b.strBillNo  "
	                    + "AND left(a.strItemCode,7)=c.strItemCode  "
	                    + "AND c.strMenuCode=d.strMenuCode  "
	                    + "AND left(a.strItemCode,7)=e.strItemCode  "
	                    + "AND c.strPosCode= IF(c.strPosCode='All','All',b.strPOSCode)  "
	                    + "AND b.strAreaCode=c.strAreaCode  "
	                    + "AND DATE(b.dteBillDate)  BETWEEN '" + fromDate + "' AND '" + toDate + "' ";

	            if (!posCode.equals("All"))
	            {
	                sqlModQFile += " and b.strPosCode='" + posCode + "'  ";
	            }

//	            if (cmbUserName.getSelectedIndex() > 0)
//	            {
//	                sqlModQFile += " and e.strRevenueHead='" + cmbUserName.getSelectedItem().toString() + "' ";
//	            }
	            if ("Detail".equalsIgnoreCase("Summary"))
	            {
	                sqlModQFile += "GROUP BY e.strRevenueHead,d.strMenuCode "
	                        + "ORDER BY e.strRevenueHead,d.strMenuCode ";
	            }
	            else
	            {
	                sqlModQFile += "GROUP BY e.strRevenueHead,d.strMenuCode,a.strModifierName "
	                        + "ORDER BY e.strRevenueHead,d.strMenuCode,a.strModifierName ";
	            }

	            sbSqlLive.append("select e.strRevenueHead,d.strMenuName,a.strItemName, SUM(a.dblQuantity), SUM(a.dblAmount) "
	                    + "from tblbilldtl a,tblbillhd b ,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
	                    + "where a.strBillNo=b.strBillNo  "
	                    + "and a.strItemCode=c.strItemCode "
	                    + "and c.strMenuCode=d.strMenuCode "
	                    + "and a.strItemCode=e.strItemCode "
	                    + "and c.strPosCode=if(c.strPosCode='All','All',b.strPOSCode) "
	                    + "and b.strAreaCode=c.strAreaCode ");

	            String sqlModLive = "SELECT e.strRevenueHead,d.strMenuName,a.strModifierName, SUM(a.dblQuantity), SUM(a.dblAmount) "
	                    + "FROM tblbillmodifierdtl a,tblbillhd b,tblmenuitempricingdtl c,tblmenuhd d,tblitemmaster e "
	                    + "WHERE a.strBillNo=b.strBillNo  "
	                    + "AND left(a.strItemCode,7)=c.strItemCode  "
	                    + "AND c.strMenuCode=d.strMenuCode  "
	                    + "AND left(a.strItemCode,7)=e.strItemCode  "
	                    + "AND c.strPosCode= IF(c.strPosCode='All','All',b.strPOSCode)  "
	                    + "AND b.strAreaCode=c.strAreaCode  "
	                    + "AND DATE(b.dteBillDate)  BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
	            if (!posCode.equals("All"))
	            {
	                sqlModLive += " and b.strPosCode='" + posCode + "'  ";
	            }

//	            if (cmbUserName.getSelectedIndex() > 0)
//	            {
//	                sqlModLive += " and e.strRevenueHead='" + cmbUserName.getSelectedItem().toString() + "' ";
//	            }
	            if ("Detail".equalsIgnoreCase("Summary"))
	            {
	                sqlModLive += "GROUP BY e.strRevenueHead,d.strMenuCode "
	                        + "ORDER BY e.strRevenueHead,d.strMenuCode ";
	            }
	            else
	            {
	                sqlModLive += "GROUP BY e.strRevenueHead,d.strMenuCode,a.strModifierName "
	                        + "ORDER BY e.strRevenueHead,d.strMenuCode,a.strModifierName ";
	            }

	            sbSqlFilters.append(" AND date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	            if (!posCode.equals("All"))
	            {
	                sbSqlFilters.append(" and b.strPosCode='" + posCode + "'  ");
	            }

//	            if (cmbUserName.getSelectedIndex() > 0)
//	            {
//	                sbSqlFilters.append(" and e.strRevenueHead='" + cmbUserName.getSelectedItem().toString() + "' ");
//	            }
	            if ("Detail".equalsIgnoreCase("Summary"))
	            {
	                sbSqlFilters.append(" group by e.strRevenueHead,d.strMenuCode "
	                        + " order by e.strRevenueHead,d.strMenuCode ");
	            }
	            else
	            {
	                sbSqlFilters.append(" group by e.strRevenueHead,d.strMenuCode,a.strItemName"
	                        + " order by e.strRevenueHead,d.strMenuCode,a.strItemName");
	            }
	            sbSqlLive.append(sbSqlFilters);
	            sbSqlQFile.append(sbSqlFilters);

	            System.out.println(sbSqlLive);
	            System.out.println(sbSqlQFile);
	            System.out.println(sqlModLive);
	            System.out.println(sqlModQFile);

	            objBaseService.funExecuteUpdate("truncate table tbltempsalesflash1","sql");
	            String sqlInsertLiveBillSales = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,strtablename,strposcode) "
	                    + "(" + sbSqlLive + ");";

	            String sqlInsertQFileBillSales = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,strtablename,strposcode) "
	                    + "(" + sbSqlQFile + ");";

	            String sqlInsertLiveModiSales = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,strtablename,strposcode) "
	                    + "(" + sqlModLive + ");";

	            String sqlInsertQFileModiSales = "insert into tbltempsalesflash1(strbillno,dtebilldate,tmebilltime,strtablename,strposcode) "
	                    + "(" + sqlModQFile + ");";

	            objBaseService.funExecuteUpdate(sqlInsertLiveBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileBillSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertLiveModiSales,"sql");
	            objBaseService.funExecuteUpdate(sqlInsertQFileModiSales,"sql");

	            if ("Detail".equalsIgnoreCase("Summary"))
	            {
	                sql =new StringBuilder( " select a.strbillno as RevenueHead,a.dtebilldate as MenuName,a.tmebilltime as ItemName,sum(a.strtablename) as Qty "
	                        + " ,sum(a.strposcode) as Amount "
	                        + " from tbltempsalesflash1 a "
	                        + " group by a.strbillno,a.dtebilldate "
	                        + " order by a.strbillno,a.dtebilldate ");
	            }
	            else
	            {
	                sql =new StringBuilder( " select a.strbillno as RevenueHead,a.dtebilldate as MenuName,a.tmebilltime as ItemName,sum(a.strtablename) as Qty "
	                        + " ,sum(a.strposcode) as Amount "
	                        + " from tbltempsalesflash1 a "
	                        + " group by a.strbillno,a.dtebilldate,a.tmebilltime "
	                        + " order by a.strbillno,a.dtebilldate,a.tmebilltime ");
	            }
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
		                    if ("Detail".equalsIgnoreCase("Summary"))
		                    {

		                    }
		                    else
		                    {
		                        arrListItem.add(ob[2].toString());
		                    }

		                    arrListItem.add(ob[3].toString());
		                    arrListItem.add(ob[4].toString());
		                    arrListItem.add(" ");
		                    arrListItem.add(" ");
		                    totalQty = totalQty + Double.parseDouble(ob[3].toString());
		                    totalAmt = totalAmt + Double.parseDouble(ob[4].toString());
		                    mapExcelItemDtl.put(p, arrListItem);
		                    p++;
		                
	              	}
	             }
	            
	            if ("Detail".equalsIgnoreCase("Summary"))
	            {
	                arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "3");
	                arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "4");
	            }
	            else
	            {
	                arrListTotal.add(String.valueOf(Math.rint(totalQty)) + "#" + "4");
	                arrListTotal.add(String.valueOf(Math.rint(totalAmt)) + "#" + "5");
	            }

	            arrHeaderList.add("Serial No");
	            arrHeaderList.add("Revenue Head");
	            arrHeaderList.add("Menu Name");
	            if ("Detail".equalsIgnoreCase("Summary"))
	            {

	            }
	            else
	            {
	                arrHeaderList.add("Item Name");
	            }

	            arrHeaderList.add("Qty");
	            arrHeaderList.add("Amount");
	            arrHeaderList.add(" ");
	            arrHeaderList.add(" ");

	            List<String> arrparameterList = new ArrayList<String>();
	            arrparameterList.add("Revenue Head Wise Report");
	            arrparameterList.add("POS" + " : " + posName);
	            arrparameterList.add("FromDate" + " : " + fromDate);
	            arrparameterList.add("ToDate" + " : " + toDate);
	            arrparameterList.add(" ");
	            arrparameterList.add(" ");

	            funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "revenueHeadWiseExcelSheet");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

	   /* private void funGenerateItemConsumptionExcelReport(String posCode, String posName, String fromDate, String toDate)
	    {
	        int sqlNo = 0;
	        StringBuilder sbSql = new StringBuilder();
	        StringBuilder sbSqlMod = new StringBuilder();
	        StringBuilder sbFilters = new StringBuilder();
	        Query querySalesMod;
	        Map<String, clsItemWiseConsumption> hmItemWiseConsumption = new HashMap<String, clsItemWiseConsumption>();

	        try
	        {
	            String reportName = "Item Wise Consumption Report";
	            if (false)
	            {

	            }
	            else
	            {

	            // Code for Sales Qty for bill detail and bill modifier live & q data
	                // for Sales Qty for bill detail live data  
	                sbSql.setLength(0);
	                sbSql.append("select b.stritemcode,b.stritemname,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	                        + " ,e.strposname,b.dblDiscountAmt,g.strSubGroupName,h.strGroupName "
	                        + " from tblbillhd a,tblbilldtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
	                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	                        + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	                        + " and a.strPOSCode=e.strPosCode and b.strItemCode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	                        + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType!='Complementary' "
	                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemName order by a.strPOSCode,b.strItemName");
	                System.out.println(sbSql);

	                Query query=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString());
	                List list=query.objBaseService.funGetList(sbSqlLive, "sql");
	       			if(list.size()>0)
	                 {	
	       				for(int i=0;i<list.size();i++)
	                  	{
	    	       		  		Object[] obSales=(Object[])list.get(i);
	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    if (null != hmItemWiseConsumption.get(obSales[0].toString()))
	    	                    {
	    	                        objItemWiseConsumption = hmItemWiseConsumption.get(obSales[0].toString());
	    	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSales[2].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSales[3].toString()) - Double.parseDouble(obSales[6].toString())));
	    	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSales[3].toString()));
	    	                    }
	    	                    else
	    	                    {
	    	                        sqlNo++;
	    	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	                        objItemWiseConsumption.setItemCode(obSales[0].toString());
	    	                        objItemWiseConsumption.setItemName(obSales[1].toString());
	    	                        objItemWiseConsumption.setSubGroupName(obSales[7].toString());
	    	                        objItemWiseConsumption.setGroupName(obSales[8].toString());
	    	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obSales[2].toString()));
	    	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	                        objItemWiseConsumption.setNcQty(0);
	    	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obSales[3].toString()));
	    	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSales[6].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSales[3].toString()) - Double.parseDouble(obSales[6].toString()));
	    	                        objItemWiseConsumption.setPOSName(obSales[5].toString());
	    	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obSales[0].toString(), objItemWiseConsumption);
	    	                    }

	    	                    //for Sales Qty for bill modifier live data 
	    	                    sbSqlMod.setLength(0);
	    	                    sbSqlMod.append("select b.strItemCode,b.strModifierName,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	    	                            + " ,e.strposname,b.dblDiscAmt,g.strSubGroupName,h.strGroupName "
	    	                            + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
	    	                            + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	    	                            + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	    	                            + " and a.strPOSCode=e.strPosCode and left(b.strItemCode,7)=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	    	                            + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType!='Complementary' "
	    	                            + " and left(b.strItemCode,7)='" + obSales[0].toString() + "' "
	    	                            + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	    	                    System.out.println(sbSqlMod);

	    	                    querySalesMod=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlMod.toString()); 
	    	                    List listSalesMod=querySalesMod.objBaseService.funGetList(sbSqlLive, "sql");
	    	                    if(listSalesMod.size()>0)
	    	                    {
	    	                    	for(int j=0;j<listSalesMod.size();j++)
	    	                      	{
	    	        	       		  		Object[] obSalesMod=(Object[])listSalesMod.get(j);

	    	    	                        // clsItemWiseConsumption objItemWiseConsumption=null;
	    	    	                        if (null != hmItemWiseConsumption.get(obSalesMod[0].toString()))
	    	    	                        {
	    	    	                            objItemWiseConsumption = hmItemWiseConsumption.get(obSalesMod[0].toString());
	    	    	                            objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSalesMod[2].toString()));
	    	    	                            objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString())));
	    	    	                            objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSalesMod[3].toString()));
	    	    	                        }
	    	    	                        else
	    	    	                        {
	    	    	                            sqlNo++;
	    	    	                            objItemWiseConsumption = new clsItemWiseConsumption();
	    	    	                            objItemWiseConsumption.setItemCode(obSalesMod[0].toString());
	    	    	                            objItemWiseConsumption.setItemName(obSalesMod[1].toString());
	    	    	                            objItemWiseConsumption.setSubGroupName(obSalesMod[7].toString());
	    	    	                            objItemWiseConsumption.setGroupName(obSalesMod[8].toString());
	    	    	                            objItemWiseConsumption.setSaleQty(Double.parseDouble(obSalesMod[2].toString()));
	    	    	                            objItemWiseConsumption.setComplimentaryQty(0);
	    	    	                            objItemWiseConsumption.setNcQty(0);
	    	    	                            objItemWiseConsumption.setSubTotal(Double.parseDouble(obSalesMod[3].toString()));
	    	    	                            objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSalesMod[6].toString()));
	    	    	                            objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString()));
	    	    	                            objItemWiseConsumption.setPOSName(obSalesMod[5].toString());
	    	    	                            objItemWiseConsumption.setSeqNo(sqlNo);

	    	    	                        }
	    	    	                        if (null != objItemWiseConsumption)
	    	    	                        {
	    	    	                            hmItemWiseConsumption.put(obSalesMod[0].toString(), objItemWiseConsumption);
	    	    	                        }

	    	    	                    
	    	                      	}
	    	                    }
	                  		}
	                 }
	               
	                
	                
	                
	                // for Sales Qty for bill detail q data 
	                sbSql.setLength(0);
	                sbSql.append("select b.stritemcode,b.stritemname,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	                        + " ,e.strposname,b.dblDiscountAmt,g.strSubGroupName,h.strGroupName "
	                        + " from tblqbillhd a,tblqbilldtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	                        + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	                        + " and a.strPOSCode=e.strPosCode and b.strItemCode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	                        + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType!='Complementary' "
	                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemName order by a.strPOSCode,b.strItemName");
	                System.out.println(sbSql);

	                query=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString());
	                list=query.objBaseService.funGetList(sbSqlLive, "sql");
	                if(list.size()>0)
	                 {	
	       				for(int i=0;i<list.size();i++)
	                  	{
	    	       		  		Object[] obSales=(Object[])list.get(i);

	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    if (null != hmItemWiseConsumption.get(obSales[0].toString()))
	    	                    {
	    	                    	objItemWiseConsumption = hmItemWiseConsumption.get(obSales[0].toString());
	     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSales[2].toString()));
	     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSales[3].toString()) - Double.parseDouble(obSales[6].toString())));
	     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSales[3].toString()));
	    	                    }
	    	                    else
	    	                    {
	    	                    	sqlNo++;
	    	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	                        objItemWiseConsumption.setItemCode(obSales[0].toString());
	    	                        objItemWiseConsumption.setItemName(obSales[1].toString());
	    	                        objItemWiseConsumption.setSubGroupName(obSales[7].toString());
	    	                        objItemWiseConsumption.setGroupName(obSales[8].toString());
	    	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obSales[2].toString()));
	    	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	                        objItemWiseConsumption.setNcQty(0);
	    	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obSales[3].toString()));
	    	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSales[6].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSales[3].toString()) - Double.parseDouble(obSales[6].toString()));
	    	                        objItemWiseConsumption.setPOSName(obSales[5].toString());
	    	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    	
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obSales[0].toString(), objItemWiseConsumption);
	    	                    }

	    	                    // Code for Sales Qty for modifier live & q data
	    	                    sbSqlMod.setLength(0);
	    	                    sbSqlMod.append("select b.strItemCode,b.strModifierName,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	    	                            + " ,e.strposname,b.dblDiscAmt,g.strSubGroupName,h.strGroupName "
	    	                            + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	    	                            + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	    	                            + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	    	                            + " and a.strPOSCode=e.strPosCode and left(b.strItemCode,7)=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	    	                            + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType!='Complementary' "
	    	                            + " and left(b.strItemCode,7)='" + obSales[0].toString() + "' "
	    	                            + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	    	                    sbSqlMod.append(sbFilters);
	    	                    System.out.println(sbSqlMod);

	    	                    querySalesMod=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlMod.toString()); 
	    	                    List listSalesMod=querySalesMod.objBaseService.funGetList(sbSqlLive, "sql");
	    	                    if(listSalesMod.size()>0)
	    	                    {
	    	                    	for(int j=0;j<listSalesMod.size();j++)
	    	                      	{
	    	        	       		  		Object[] obSalesMod=(Object[])listSalesMod.get(j);
	    	    	                        if (null != hmItemWiseConsumption.get(obSalesMod[0].toString()))
	    	    	                        {
	    	    	                        		objItemWiseConsumption = hmItemWiseConsumption.get(obSalesMod[0].toString());
	    	    	     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSalesMod[2].toString()));
	    	    	     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString())));
	    	    	     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSalesMod[3].toString()));
	    	    	    	                }
	    	    	                        else
	    	    	                        {
	    	    	                            
	    	    	                        	sqlNo++;
	    	    	                            objItemWiseConsumption = new clsItemWiseConsumption();
	    	    	                            objItemWiseConsumption.setItemCode(obSalesMod[0].toString());
	    	    	                            objItemWiseConsumption.setItemName(obSalesMod[1].toString());
	    	    	                            objItemWiseConsumption.setSubGroupName(obSalesMod[7].toString());
	    	    	                            objItemWiseConsumption.setGroupName(obSalesMod[8].toString());
	    	    	                            objItemWiseConsumption.setSaleQty(Double.parseDouble(obSalesMod[2].toString()));
	    	    	                            objItemWiseConsumption.setComplimentaryQty(0);
	    	    	                            objItemWiseConsumption.setNcQty(0);
	    	    	                            objItemWiseConsumption.setSubTotal(Double.parseDouble(obSalesMod[3].toString()));
	    	    	                            objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSalesMod[6].toString()));
	    	    	                            objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString()));
	    	    	                            objItemWiseConsumption.setPOSName(obSalesMod[5].toString());
	    	    	                            objItemWiseConsumption.setSeqNo(sqlNo);
	    	    	                        		
	    	    	                        }
	    	    	                        if (null != objItemWiseConsumption)
	    	    	                        {
	    	    	                            hmItemWiseConsumption.put(obSalesMod[0].toString(), objItemWiseConsumption);
	    	    	                        }
	    	    	                    
	    	                      	}
	    	                    }
	    	                
	                  	}
	                 }
	                 // Code for Complimentary Qty for live & q bill detail and bill modifier data   
	                //for Complimentary Qty for live bill detail
	                sbSql.setLength(0);
	                sbSql.append("select b.stritemcode,b.stritemname,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	                        + " ,e.strposname,b.dblDiscountAmt,g.strSubGroupName,h.strGroupName "
	                        + " from tblbillhd a,tblbilldtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	                        + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	                        + " and a.strPOSCode=e.strPosCode and b.strItemCode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	                        + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType='Complementary' "
	                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemName order by a.strPOSCode,b.strItemName");
	                System.out.println(sbSql);

	                Query queryComple=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString()); 
	                List listComplmnt=queryComple.objBaseService.funGetList(sbSqlLive, "sql");
	                if(listComplmnt.size()>0)
	                {
	                	for(int i=0;i<listComplmnt.size();i++)
	                  	{
	    	       		  		Object[] obComple=(Object[])listComplmnt.get(i);
	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    if (null != hmItemWiseConsumption.get(obComple[0].toString()))
	    	                    {

	    	                    	objItemWiseConsumption = hmItemWiseConsumption.get(obComple[0].toString());
	     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obComple[2].toString()));
	     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obComple[3].toString()) - Double.parseDouble(obComple[6].toString())));
	     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obComple[3].toString()));
	    	                    
	    	                       System.out.println("Old= " + obComple[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	                    }
	    	                    else
	    	                    {
	    	                    	sqlNo++;
	    	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	                        objItemWiseConsumption.setItemCode(obComple[0].toString());
	    	                        objItemWiseConsumption.setItemName(obComple[1].toString());
	    	                        objItemWiseConsumption.setSubGroupName(obComple[7].toString());
	    	                        objItemWiseConsumption.setGroupName(obComple[8].toString());
	    	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obComple[2].toString()));
	    	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	                        objItemWiseConsumption.setNcQty(0);
	    	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obComple[3].toString()));
	    	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obComple[6].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obComple[3].toString()) - Double.parseDouble(obComple[6].toString()));
	    	                        objItemWiseConsumption.setPOSName(obComple[5].toString());
	    	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    	
	    	                       System.out.println("New= " + obComple[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obComple[0].toString(), objItemWiseConsumption);
	    	                    }

	    	                    //for Complimentary Qty for live bill modifier
	    	                    sbSqlMod.setLength(0);
	    	                    sbSqlMod.append("select b.strItemCode,b.strModifierName,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	    	                            + " ,e.strposname,b.dblDiscAmt,g.strSubGroupName,h.strGroupName "
	    	                            + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	    	                            + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	    	                            + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	    	                            + " and a.strPOSCode=e.strPosCode and left(b.strItemCode,7)=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	    	                            + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType='Complementary' "
	    	                            + " and left(b.strItemCode,7)='" + obComple[0].toString() + "' "
	    	                            + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	    	                    sbSqlMod.append(sbFilters);
	    	                    System.out.println(sbSqlMod);

	    	                    Query queryComplMod=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlMod.toString()); 
	    	                    List listComplMod=queryComplMod.objBaseService.funGetList(sbSqlLive, "sql");
	    	                    if(listComplMod.size()>0)
	    	                    {
	    	                    	for(int j=0;j<listComplMod.size();j++)
	    	                      	{
	    	        	       		  		Object[] obCompleMod=(Object[])listComplMod.get(j);

	    	    	                        if (null != hmItemWiseConsumption.get(obCompleMod[0].toString()))
	    	    	                        {
	    	    	                        	objItemWiseConsumption = hmItemWiseConsumption.get(obCompleMod[0].toString());
	    	         	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obCompleMod[2].toString()));
	    	         	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obCompleMod[3].toString()) - Double.parseDouble(obCompleMod[6].toString())));
	    	         	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obCompleMod[3].toString()));
	    	        	                        System.out.println("Old= " + obCompleMod[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	    	                        }
	    	    	                        else
	    	    	                        {
	    	    	                        	sqlNo++;
	    	        	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	        	                        objItemWiseConsumption.setItemCode(obCompleMod[0].toString());
	    	        	                        objItemWiseConsumption.setItemName(obCompleMod[1].toString());
	    	        	                        objItemWiseConsumption.setSubGroupName(obCompleMod[7].toString());
	    	        	                        objItemWiseConsumption.setGroupName(obCompleMod[8].toString());
	    	        	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obCompleMod[2].toString()));
	    	        	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	        	                        objItemWiseConsumption.setNcQty(0);
	    	        	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obCompleMod[3].toString()));
	    	        	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obCompleMod[6].toString()));
	    	        	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obCompleMod[3].toString()) - Double.parseDouble(obCompleMod[6].toString()));
	    	        	                        objItemWiseConsumption.setPOSName(obCompleMod[5].toString());
	    	        	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	    	                          
	       	    	                            System.out.println("New= " + obCompleMod[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	    	                        }
	    	    	                        if (null != objItemWiseConsumption)
	    	    	                        {
	    	    	                            hmItemWiseConsumption.put(obCompleMod[0].toString(), objItemWiseConsumption);
	    	    	                        }
	    	    	                    
	    	                      	}
	    	                    }
	    	                   
	                  	}
	                }
	                //for Complimentary Qty for q bill details
	                sbSql.setLength(0);
	                sbSql.append("select b.stritemcode,b.stritemname,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	                        + " ,e.strposname,b.dblDiscountAmt,g.strSubGroupName,h.strGroupName "
	                        + " from tblqbillhd a,tblqbilldtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	                        + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	                        + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	                        + " and a.strPOSCode=e.strPosCode and b.strItemCode=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	                        + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType='Complementary' "
	                        + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemName order by a.strPOSCode,b.strItemName");
	                System.out.println(sbSql);

	                queryComple=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString()); 
	                listComplmnt=queryComple.objBaseService.funGetList(sbSqlLive, "sql");
	                if(listComplmnt.size()>0)
	                {
	                	for(int i=0;i<listComplmnt.size();i++)
	                  	{
	    	       		  		Object[] obComple=(Object[])listComplmnt.get(i);
	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    
	    	                    if (null != hmItemWiseConsumption.get(obComple[0].toString()))
	    	                    {

	    	                    	objItemWiseConsumption = hmItemWiseConsumption.get(obComple[0].toString());
	     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obComple[2].toString()));
	     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obComple[3].toString()) - Double.parseDouble(obComple[6].toString())));
	     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obComple[3].toString()));
	    	                    
	    	                       System.out.println("Old= " + obComple[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	                    }
	    	                    else
	    	                    {
	    	                    	sqlNo++;
	    	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	                        objItemWiseConsumption.setItemCode(obComple[0].toString());
	    	                        objItemWiseConsumption.setItemName(obComple[1].toString());
	    	                        objItemWiseConsumption.setSubGroupName(obComple[7].toString());
	    	                        objItemWiseConsumption.setGroupName(obComple[8].toString());
	    	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obComple[2].toString()));
	    	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	                        objItemWiseConsumption.setNcQty(0);
	    	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obComple[3].toString()));
	    	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obComple[6].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obComple[3].toString()) - Double.parseDouble(obComple[6].toString()));
	    	                        objItemWiseConsumption.setPOSName(obComple[5].toString());
	    	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    	
	    	                       System.out.println("New= " + obComple[0].toString() + objItemWiseConsumption.getComplimentaryQty());
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obComple[0].toString(), objItemWiseConsumption);
	    	                    }
	    	                   //for Complimentary Qty for q bill modifier 
	    	                    sbSqlMod.setLength(0);
	    	                    sbSqlMod.append("select b.strItemCode,b.strModifierName,sum(b.dblQuantity),sum(b.dblamount),b.dblRate"
	    	                            + " ,e.strposname,b.dblDiscAmt,g.strSubGroupName,h.strGroupName "
	    	                            + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
	    	                            + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
	    	                            + " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and c.strSettlementCode=d.strSettelmentCode "
	    	                            + " and a.strPOSCode=e.strPosCode and left(b.strItemCode,7)=f.strItemCode and f.strSubGroupCode=g.strSubGroupCode "
	    	                            + " and g.strGroupCode=h.strGroupCode and d.strSettelmentType='Complementary' "
	    	                            + " and left(b.strItemCode,7)='" + obComple[0].toString() + "'"
	    	                            + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	    	                    sbSqlMod.append(sbFilters);
	    	                    System.out.println(sbSqlMod);

	    	                    Query queryComplMod=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlMod.toString()); 
	    	                    List listComplMod=queryComplMod.objBaseService.funGetList(sbSqlLive, "sql");
	    	                    if(listComplMod.size()>0)
	    	                    {
	    	                    	for(int j=0;j<listComplMod.size();j++)
	    	                      	{
	    	        	       		  		Object[] obCompleMod=(Object[])listComplMod.get(j);
	    	    	                        if (null != hmItemWiseConsumption.get(obCompleMod[0].toString()))
	    	    	                        {
	    	    	                        	objItemWiseConsumption = hmItemWiseConsumption.get(obCompleMod[0].toString());
	    	         	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obCompleMod[2].toString()));
	    	         	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obCompleMod[3].toString()) - Double.parseDouble(obCompleMod[6].toString())));
	    	         	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obCompleMod[3].toString()));
	    	    	                        }
	    	    	                        else
	    	    	                        {
	    	    	                        	sqlNo++;
	    	        	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	        	                        objItemWiseConsumption.setItemCode(obCompleMod[0].toString());
	    	        	                        objItemWiseConsumption.setItemName(obCompleMod[1].toString());
	    	        	                        objItemWiseConsumption.setSubGroupName(obCompleMod[7].toString());
	    	        	                        objItemWiseConsumption.setGroupName(obCompleMod[8].toString());
	    	        	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obCompleMod[2].toString()));
	    	        	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	        	                        objItemWiseConsumption.setNcQty(0);
	    	        	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obCompleMod[3].toString()));
	    	        	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obCompleMod[6].toString()));
	    	        	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obCompleMod[3].toString()) - Double.parseDouble(obCompleMod[6].toString()));
	    	        	                        objItemWiseConsumption.setPOSName(obCompleMod[5].toString());
	    	        	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	    	                        }
	    	    	                        if (null != objItemWiseConsumption)
	    	    	                        {
	    	    	                            hmItemWiseConsumption.put(obCompleMod[0].toString(), objItemWiseConsumption);
	    	    	                        }
	    	    	                    
	    	                      	}
	    	                    }
	    	                         	                
	    	           	}
	                }
	             
	                // Code for NC Qty    
	                sbSql.setLength(0);
	                sbSql.append("select a.stritemcode,b.stritemname,sum(a.dblQuantity),sum(a.dblQuantity*a.dblRate)"
	                        + ",a.dblRate, c.strposname,0 as DiscAmt,d.strSubGroupName,e.strGroupName "
	                        + " from tblnonchargablekot a, tblitemmaster b, tblposmaster c,tblsubgrouphd d,tblgrouphd e "
	                        + " where left(a.strItemCode,7)=b.strItemCode and a.strPOSCode=c.strPosCode and b.strSubGroupCode=d.strSubGroupCode "
	                        + " and d.strGroupCode=e.strGroupCode "
	                        + " and date(a.dteNCKOTDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemName order by a.strPOSCode,b.strItemName");
	                System.out.println(sbSql);

	                Query queryNCKOT=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlMod.toString()); 
	                List listNCKOT=queryNCKOT.objBaseService.funGetList(sbSqlLive, "sql");
	                if(listNCKOT.size()>0)
	                {
	                	for(int j=0;j<listNCKOT.size();j++)
	                  	{
	    	       		  		Object[] obNCKOT=(Object[])listNCKOT.get(j);
	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    if (null != hmItemWiseConsumption.get(obNCKOT[0].toString()))
	    	                    {
	    	                    	objItemWiseConsumption = hmItemWiseConsumption.get(obNCKOT[0].toString());
	     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obNCKOT[2].toString()));
	     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obNCKOT[3].toString()) - Double.parseDouble(obNCKOT[6].toString())));
	     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obNCKOT[3].toString()));
	    	                     }
	    	                    else
	    	                    {
	    	                    	sqlNo++;
	    	                        objItemWiseConsumption = new clsItemWiseConsumption();
	    	                        objItemWiseConsumption.setItemCode(obNCKOT[0].toString());
	    	                        objItemWiseConsumption.setItemName(obNCKOT[1].toString());
	    	                        objItemWiseConsumption.setSubGroupName(obNCKOT[7].toString());
	    	                        objItemWiseConsumption.setGroupName(obNCKOT[8].toString());
	    	                        objItemWiseConsumption.setSaleQty(Double.parseDouble(obNCKOT[2].toString()));
	    	                        objItemWiseConsumption.setComplimentaryQty(0);
	    	                        objItemWiseConsumption.setNcQty(0);
	    	                        objItemWiseConsumption.setSubTotal(Double.parseDouble(obNCKOT[3].toString()));
	    	                        objItemWiseConsumption.setDiscAmt(Double.parseDouble(obNCKOT[6].toString()));
	    	                        objItemWiseConsumption.setSaleAmt(Double.parseDouble(obNCKOT[3].toString()) - Double.parseDouble(obNCKOT[6].toString()));
	    	                        objItemWiseConsumption.setPOSName(obNCKOT[5].toString());
	    	                        objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obNCKOT[0].toString(), objItemWiseConsumption);
	    	                    }
	    	                
	                  	}
	                }
	                // Code for promotion Qty for Q
	                sbSql.setLength(0);
	                sbSql.append("select b.strItemCode,c.strItemName,sum(b.dblQuantity),sum(b.dblAmount),b.dblRate"
	                        + ",f.strPosName,0,d.strSubGroupName,e.strGroupName "
	                        + " from tblbillhd a,tblbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f "
	                        + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strSubGroupCode=d.strSubGroupCode "
	                        + " and d.strGroupCode=e.strGroupCode and a.strPOSCode=f.strPosCode "
	                        + " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemCode order by a.strPOSCode,c.strItemName");
	                System.out.println(sbSql);

	                querySalesMod =webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString()); 
	                List listsalesMod=querySalesMod.objBaseService.funGetList(sbSqlLive, "sql");
	                if(listsalesMod.size()>0)
	                {
	                	for(int j=0;j<listsalesMod.size();j++)
	                  	{
	    	       		  		Object[] obSalesMod=(Object[])listsalesMod.get(j);
	    	       		  		clsItemWiseConsumption objItemWiseConsumption = null;
	    	       		  		if (null != hmItemWiseConsumption.get(obSalesMod[0].toString()))
		                        {
		                        		objItemWiseConsumption = hmItemWiseConsumption.get(obSalesMod[0].toString());
		     	                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSalesMod[2].toString()));
		     	                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString())));
		     	                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSalesMod[3].toString()));
		    	                }
		                        else
		                        {
		                            
		                        	sqlNo++;
		                            objItemWiseConsumption = new clsItemWiseConsumption();
		                            objItemWiseConsumption.setItemCode(obSalesMod[0].toString());
		                            objItemWiseConsumption.setItemName(obSalesMod[1].toString());
		                            objItemWiseConsumption.setSubGroupName(obSalesMod[7].toString());
		                            objItemWiseConsumption.setGroupName(obSalesMod[8].toString());
		                            objItemWiseConsumption.setSaleQty(Double.parseDouble(obSalesMod[2].toString()));
		                            objItemWiseConsumption.setComplimentaryQty(0);
		                            objItemWiseConsumption.setNcQty(0);
		                            objItemWiseConsumption.setSubTotal(Double.parseDouble(obSalesMod[3].toString()));
		                            objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSalesMod[6].toString()));
		                            objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString()));
		                            objItemWiseConsumption.setPOSName(obSalesMod[5].toString());
		                            objItemWiseConsumption.setSeqNo(sqlNo);
		                        		
		                        }
		                        if (null != objItemWiseConsumption)
		                        {
		                            hmItemWiseConsumption.put(obSalesMod[0].toString(), objItemWiseConsumption);
		                        }
		                }
	                }
	                 // Code for promotion Qty for live
	                sbSql.setLength(0);
	                sbSql.append("select b.strItemCode,c.strItemName,sum(b.dblQuantity),sum(b.dblAmount),b.dblRate"
	                        + ",f.strPosName,0,d.strSubGroupName,e.strGroupName "
	                        + " from tblqbillhd a,tblqbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f "
	                        + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strSubGroupCode=d.strSubGroupCode "
	                        + " and d.strGroupCode=e.strGroupCode and a.strPOSCode=f.strPosCode "
	                        + " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
	                if (!posCode.equals("All"))
	                {
	                    sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
	                }
	                sbSql.append(" group by b.strItemCode order by a.strPOSCode,c.strItemName");
	                System.out.println(sbSql);

	                querySalesMod =webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString()); 
	                listsalesMod=querySalesMod.objBaseService.funGetList(sbSqlLive, "sql");
	                if(listsalesMod.size()>0)
	                {
	                	for(int j=0;j<listsalesMod.size();j++)
	                  	{
	    	       		  		Object[] obSalesMod=(Object[])listsalesMod.get(j);

	    	                    clsItemWiseConsumption objItemWiseConsumption = null;
	    	                    if (null != hmItemWiseConsumption.get(obSalesMod[0].toString()))
	    	                    {
	    	                    	
	    	                    	objItemWiseConsumption = hmItemWiseConsumption.get(obSalesMod[0].toString());
			                        objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.parseDouble(obSalesMod[2].toString()));
			                        objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString())));
			                        objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.parseDouble(obSalesMod[3].toString()));
	    	                        
	    	                    }
	    	                    else
	    	                    {
	    	                    	sqlNo++;
		                            objItemWiseConsumption = new clsItemWiseConsumption();
		                            objItemWiseConsumption.setItemCode(obSalesMod[0].toString());
		                            objItemWiseConsumption.setItemName(obSalesMod[1].toString());
		                            objItemWiseConsumption.setSubGroupName(obSalesMod[7].toString());
		                            objItemWiseConsumption.setGroupName(obSalesMod[8].toString());
		                            objItemWiseConsumption.setSaleQty(Double.parseDouble(obSalesMod[2].toString()));
		                            objItemWiseConsumption.setComplimentaryQty(0);
		                            objItemWiseConsumption.setNcQty(0);
		                            objItemWiseConsumption.setSubTotal(Double.parseDouble(obSalesMod[3].toString()));
		                            objItemWiseConsumption.setDiscAmt(Double.parseDouble(obSalesMod[6].toString()));
		                            objItemWiseConsumption.setSaleAmt(Double.parseDouble(obSalesMod[3].toString()) - Double.parseDouble(obSalesMod[6].toString()));
		                            objItemWiseConsumption.setPOSName(obSalesMod[5].toString());
		                            objItemWiseConsumption.setSeqNo(sqlNo);
	    	                    }
	    	                    if (null != objItemWiseConsumption)
	    	                    {
	    	                        hmItemWiseConsumption.put(obSalesMod[0].toString(), objItemWiseConsumption);
	    	                    }
	    	               }
	                }
	             
	                List<clsItemWiseConsumption> list1 = new ArrayList<clsItemWiseConsumption>();
	                for (Map.Entry<String, clsItemWiseConsumption> entry : hmItemWiseConsumption.entrySet())
	                {
	                    list1.add(entry.getValue());
	                }

	                //sort list 
	                Collections.sort(list1, clsItemWiseConsumption.comparatorItemConsumptionColumnDtl);

	                Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
	                List<String> arrListTotal = new ArrayList<String>();
	                List<String> arrHeaderList = new ArrayList<String>();
	                double totalSaleQty = 0, totalComplimentaryQty = 0, totalNCQty = 0, totalSaleAmt = 0, totalSubTotal = 0, totalPromoQty = 0, totalDiscAmt = 0;

	                int i = 1;
	                for (clsItemWiseConsumption objItemComp : list1)
	                {
	                    List<String> arrListItem = new ArrayList<String>();
	                    arrListItem.add(objItemComp.getGroupName());
	                    arrListItem.add(objItemComp.getSubGroupName());
	                    arrListItem.add(objItemComp.getItemName());
	                    arrListItem.add(objItemComp.getPOSName());
	                    arrListItem.add(String.valueOf(objItemComp.getSaleQty()));
	                    arrListItem.add(String.valueOf(objItemComp.getComplimentaryQty()));
	                    arrListItem.add(String.valueOf(objItemComp.getNcQty()));
	                    arrListItem.add(String.valueOf(objItemComp.getPromoQty()));
	                    arrListItem.add(String.valueOf(objItemComp.getSubTotal()));
	                    arrListItem.add(String.valueOf(objItemComp.getSaleAmt()));
	                    arrListItem.add(String.valueOf(objItemComp.getDiscAmt()));

	                    totalSaleQty += objItemComp.getSaleQty();
	                    totalComplimentaryQty += objItemComp.getComplimentaryQty();
	                    totalNCQty += objItemComp.getNcQty();
	                    totalSaleAmt += objItemComp.getSaleAmt();
	                    totalSubTotal += objItemComp.getSubTotal();
	                    totalPromoQty += objItemComp.getPromoQty();
	                    totalDiscAmt += objItemComp.getDiscAmt();
	                    mapExcelItemDtl.put(i, arrListItem);
	                    i++;
	                }

	                arrListTotal.add(String.valueOf(Math.rint(totalSaleQty)) + "#" + "5");
	                arrListTotal.add(String.valueOf(Math.rint(totalComplimentaryQty)) + "#" + "6");
	                arrListTotal.add(String.valueOf(Math.rint(totalNCQty)) + "#" + "7");
	                arrListTotal.add(String.valueOf(Math.rint(totalPromoQty)) + "#" + "8");
	                arrListTotal.add(String.valueOf(Math.rint(totalSubTotal)) + "#" + "9");
	                arrListTotal.add(String.valueOf(Math.rint(totalSaleAmt)) + "#" + "10");
	                arrListTotal.add(String.valueOf(Math.rint(totalDiscAmt)) + "#" + "11");

	                arrHeaderList.add("Sr. No.");
	                arrHeaderList.add("Group");
	                arrHeaderList.add("Sub Group");
	                arrHeaderList.add("Item Name");
	                arrHeaderList.add("POS");
	                arrHeaderList.add("Sale Qty");
	                arrHeaderList.add("Complimentary Qty");
	                arrHeaderList.add("NC Qty");
	                arrHeaderList.add("Promo Qty");
	                arrHeaderList.add("SubTotal");
	                arrHeaderList.add("Sales Amount");
	                arrHeaderList.add("Discount Amount");

	                List<String> arrparameterList = new ArrayList<String>();
	                arrparameterList.add("Item Consumption Report");
	                arrparameterList.add("POS" + " : " + posName);
	                arrparameterList.add("FromDate" + " : " + fromDate);
	                arrparameterList.add("ToDate" + " : " + toDate);
	                arrparameterList.add(" ");
	                arrparameterList.add(" ");

	                funCreateExcelSheet(arrparameterList, arrHeaderList, mapExcelItemDtl, arrListTotal, "ItemConsumptionExcelSheet");

	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	*/

	    public void funCreateExcelSheet(List<String> parameterList, List<String> headerList, Map<Integer, List<String>> map, List<String> totalList, String fileName)
	    {
	        String filePath = System.getProperty("user.dir");
	        File file = new File(filePath + "\\Reports\\" + fileName + ".xls");
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
	            	
	            	
	            	/*
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
	            */}

	            for (int j = 0; j < headerList.size(); j++)
	            {/*
	                Label lblHeader = new Label(j, 5, headerList.get(j), headerCell);
	                sheet1.addCell(lblHeader);
	            */}

	            int i = 7;
	            for (Map.Entry<Integer, List<String>> entry : map.entrySet())
	            {/*
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
	            */}

	            for (int j = 0; j < totalList.size(); j++)
	            {/*
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
	            */}
	            workbook1.write();
	            workbook1.close();

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

		
		public JSONArray funGetJsonArrRowDayEnd(JSONArray jArr)
		{
			JSONArray lastJson= new JSONArray();
			for(int i=0;i<jArr.size();i++)
			{
				
				 JSONObject JsOb=(JSONObject) jArr.get(i);
				String str="";
				//JSONArray jArrtmp=new JSONArray();
				ArrayList al=new ArrayList<>();
				for(int j=0;j<11;j++){
					str=String.valueOf(j);
					al.add(JsOb.get(str).toString());
				}
				lastJson.add(al);
			}
			return lastJson;
		}
		public JSONArray funGetJsonArrRowSettlement(JSONArray jArr)
		{
			JSONArray lastJson= new JSONArray();
			for(int i=0;i<jArr.size();i++)
			{
				
				 JSONObject JsOb=(JSONObject) jArr.get(i);
				String str="";
				JSONArray jArrtmp=new JSONArray();
				for(int j=0;j<3;j++){
					str=String.valueOf(j);
					jArrtmp.add(JsOb.get(str));
				}
				lastJson.add(jArrtmp);
			}
			return lastJson;
		}
		
		public JSONArray funGetJsonArrRowSalesOfProg(JSONArray jArr)
		{
			JSONArray lastJson= new JSONArray();
			for(int i=0;i<jArr.size();i++)
			{
				
				 JSONObject JsOb=(JSONObject) jArr.get(i);
				String str="";
				JSONArray jArrtmp=new JSONArray();
				for(int j=0;j<2;j++){
					str=String.valueOf(j);
					jArrtmp.add(JsOb.get(str));
				}
				lastJson.add(jArrtmp);
			}
			return lastJson;
		}


}
