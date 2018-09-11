package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
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

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;
import com.sanguine.webpos.util.clsPOSDayEndUtility;
import com.sanguine.webpos.util.clsPOSSendMail;
import com.sanguine.webpos.util.clsPOSSetupUtility;

@Controller
public class clsPOSDayEndProcessConsolidate {
//frmPOSShiftEndProcessConsolidate
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	 @Autowired
	 private ServletContext servletContext;
	 @Autowired
	intfBaseService objBaseService;
	 
	 @Autowired
	 clsPOSSetupUtility objPOSSetupUtility;

	 @Autowired 
	 clsPOSSendMail objSendMail;
	 @Autowired 
	 clsPOSDayEndUtility objPOSDayEndUtility;
	 public static String loginPOS;
	 public static JSONObject jsonConsolidateDayEndReturn=new JSONObject();
		
	 String strPOSCode="",strPOSName="",strPOSDate="",strClientCode="",userCode="",strShiftNo="";

	 public static String gTransactionType = "",gDayEndReportForm="";
	 int shiftNo=0,noOfDiscountedBills=0;
	 double sales = 0,cashIn = 0,cashOut = 0,totalSales = 0,totalWithdrawl = 0, 
				totalTransIn = 0,totalTransOuts = 0,totalPayments = 0, totalFloat = 0,
				advCash=0, totalDiscount=0,dblApproxSaleAmount=0;
	 
	 String shiftEnd="",dayEnd="",emailReport="";
	 StringBuilder sql=new StringBuilder();
	 
	@RequestMapping(value="/frmPOSShiftEndProcessConsolidate" ,method=RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request,ModelMap modelmap)
	{
		
		clsPOSDayEndProcessBean objDayEndProcessBean= new clsPOSDayEndProcessBean();
	 	JSONArray jArrDayEnd=new JSONArray();
	 	JSONObject DayEndProcessData= new JSONObject();
	 	strPOSCode=request.getSession().getAttribute("loginPOS").toString();
	 	loginPOS=strPOSCode;
	 	String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		strShiftNo="1";
		strClientCode=request.getSession().getAttribute("gClientCode").toString();
		userCode=request.getSession().getAttribute("gUserCode").toString();
		strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		strPOSDate=request.getSession().getAttribute("gPOSDate").toString();
		

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
			return new ModelAndView("frmPOSShiftEndProcessConsolidate_1","command", objDayEndProcessBean);
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSShiftEndProcessConsolidate","command", objDayEndProcessBean);
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
		JSONArray lastJson= new JSONArray();
		JSONArray jArr=new JSONArray();
		JSONArray jArrtmp=new JSONArray();
			
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
		sql.setLength(0);
        sql.append("select strSettelmentDesc from tblsettelmenthd where strSettelmentType='Cash'");
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
		sql.setLength(0);
		 sql.append("SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt),c.strSettelmentType "
                    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + "Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
                    + " and date(a.dteBillDate ) ='" + strPOSDate + "' "
                    + " and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftNo + " GROUP BY c.strSettelmentDesc");
		 listSql = objBaseService.funGetList(sql, "sql");
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
    	  sql.setLength(0);
          sql.append( "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
                  + "Where date(dteBillDate ) ='" + strPOSDate + "' "
                  + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftNo);
          List listTotDiscBill  = objBaseService.funGetList(sql, "sql");
         	if (listTotDiscBill.size() > 0) 
			{
				for (int i = 0; i < listTotDiscBill.size(); i++) 
				{
					
					Object[] obj = (Object[]) listTotDiscBill.get(i);
					 noOfDiscountedBills = Integer.parseInt(obj[0].toString());
				}
			}
		    	
	          int totalBillNo = 0;
	          sql.setLength(0);
	          sql.append("select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "' and "
	                  + " intShiftCode='" + shiftNo+"'");
	           List listTotBill =  objBaseService.funGetList(sql, "sql");
		    	if (listTotBill.size() > 0) 
				{
		    		totalBillNo = Integer.parseInt(listTotBill.get(0).toString());
				}
		    	
		        // jsonDayEndTot
		        
		         JSONObject jsonOb =new JSONObject();
		         jsonOb.put("0", "Total Sales");
		         jsonOb.put("1", totalSales);
		         jsonOb.put("8", totalBillNo);
		     //    jsonOb.put("1", discountRecords);
		         
		         //jArrDayEndTot.put(jsonOb);
		         sql.setLength(0);
		         sql.append("select count(dblAdvDeposite) from tbladvancereceipthd "
		                 + "where dtReceiptDate='" + strPOSDate + "' and intShiftCode=" + shiftNo);
		          List listTotalAdvance =  objBaseService.funGetList(sql, "sql");
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
				        	 sql.setLength(0);
					         sql.append("select sum(b.dblAdvDepositesettleAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
				                     + "where date(a.dtReceiptDate)='" + strPOSDate + "'"
				                     + "' and intShiftCode=" + shiftNo + " and c.strSettelmentCode=b.strSettlementCode "
				                     + "and a.strReceiptNo=b.strReceiptNo and c.strSettelmentType='Cash'");
				             System.out.println(sql);
	
				             listTotalAdvance =  objBaseService.funGetList(sql, "sql");
				             if (listTotalAdvance.size() > 0) 
								{
				            	 Object[] obj = (Object[]) listSql.get(0);
				            	 advCash = Double.parseDouble(obj[0].toString());
								}
				             JSONObject jr=(JSONObject) jArrDayEnd.get(0);
				             jr.put("4",advCash );
				           }
					}

			    	 sql.setLength(0);
			         sql.append("select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
		                 + "where dteTransDate='" + strPOSDate + "' "
		                 + "and intShiftCode=" + shiftNo
		                 + " group by strTransType,strCurrencyType");
		         //System.out.println(sql);
		         List listTransaction =  objBaseService.funGetList(sql, "sql");
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
		
			    	sql.setLength(0);sql.append("select sum(intPaxNo) from tblbillhd where intShiftCode=" + shiftNo + " "
			                 + "and date(dteBillDate ) ='" + strPOSDate + "'");// + "and strPOSCode='" + strPOSCode + "'";
			         //System.out.println(sql);
			    	 String totalPax="";
			    	  List listTotalPax  =  objBaseService.funGetList(sql, "sql");
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
	       sql.setLength(0);sql.append("SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt) "
	                + "FROM tblbillhd a, tblbillsettlementdtl b"
	                + ", tblsettelmenthd c Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
	                + " and date(a.dteBillDate ) ='" + strPOSDate + "' "
	                + " and intShiftCode=" + shiftNo
	                + " GROUP BY c.strSettelmentDesc,a.strPosCode");
	        //System.out.println(sql);
	          List listSettlementSale =objBaseService.funGetList(sql, "sql");
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
		         sql.setLength(0);
		         sql.append("SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
		                  + "Where date(dteBillDate ) ='" + strPOSDate + "'  "
		                  + "and dblDiscountAmt > 0.00 ");
		          List listTotalDiscountBills =objBaseService.funGetList(sql, "sql");
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
			         sql.setLength(0);sql.append("select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + strPOSDate + "'");
		
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
		 
        StringBuilder sql_FillTable =new StringBuilder("select b.strTableName,sum(a.dblAmount) "
                + " from tblitemrtemp a,tbltablemaster b "
                + " where a.strTableNo=b.strTableNo and a.strNCKotYN='N' "
                + " group by a.strTableNo");
        JSONObject jsonOb=new JSONObject();
        List listSaleprog =  objBaseService.funGetList(sql_FillTable, "sql");
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
		 
		 sql.setLength(0);
		 sql.append("select a.strBillNo,c.strTableName,a.dblGrandTotal "
	                + " from tblbillhd a,tbltablemaster c "
	                + " where  date(a.dteBillDate)='" + strPOSDate+ "' "
	                + " and a.strTableNo=c.strTableNo and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ");
		 
		  List listUnsettledBills =  objBaseService.funGetList(sql, "sql");
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
	       
	    	sql.setLength(0);
			 sql.append("select a.strBillNo,a.dblGrandTotal "
	                + " from tblbillhd a "
	                + " where a.strTableNo='' and  date(a.dteBillDate)='" + strPOSDate + "' "
	                + " and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ");
	        
	         List listUnBillsDirectBiller =  objBaseService.funGetList(sql, "sql");
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
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/consolidateStartDayProcess",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funConsolidateStartDayProcess(HttpServletRequest req)
	{
		JSONObject jsonDayStart=new JSONObject();
		 jsonDayStart.put("DayStart", "Day Not Start");
		//String strDayStart="N";
		try{
			//funShiftStartProcess();
			String ShiftNo="1";
			funShiftStartProcess(ShiftNo);
			String shiftEnd="", DayEnd="", shiftNo="";
			
			req.getSession().setAttribute("gShiftEnd","N");
		    req.getSession().setAttribute("gDayEnd","N");
		    req.getSession().setAttribute("gShiftNo",shiftNo);	
			  jsonDayStart.put("DayStart", "Day Started Successfully");
			  //strDayStart="Y";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jsonDayStart;//new ModelAndView("frmPOSShiftEndProcessConsolidate");//return jsonDayStart; //new clsDayEndProcessBean();
	}
	
	public JSONObject funShiftStartProcess(String shift) 
    {
		JSONObject jObj=new JSONObject();
		try{
				
				
				int shiftNo=Integer.parseInt(shift);
				String strPOS="";
				StringBuilder sql = new StringBuilder("select strPOSCode from tblposmaster where strOperationalYN='Y' ");
				List listPOS=objBaseService.funGetList(sql, "sql");
				if(listPOS.size()>0)
				{
					
					for(int i=0;i<listPOS.size();i++)
					{
						strPOS=listPOS.get(i).toString();
						sql.setLength(0);
						sql.append("update tbldayendprocess set strShiftEnd='N' "
							    + "where strPOSCode='" +strPOS + "' and strDayEnd='N' and strShiftEnd=''");
						objBaseService.funExecuteUpdate(sql.toString(), "sql");
					}
				}
	            if (shiftNo == 0)
					{
					    shiftNo++;
					}
	            sql.setLength(0);
	            sql.append("select strPOSCode from tblposmaster where strOperationalYN='Y' ");
	            listPOS=objBaseService.funGetList(sql, "sql");
				if(listPOS.size()>0)
				{
					for(int i=0;i<listPOS.size();i++)
					{
						strPOS=listPOS.get(i).toString();
						sql.setLength(0);
						sql.append( "update tbldayendprocess set intShiftCode= " + shiftNo + " "
		                        + "where strPOSCode='" + strPOS + "' and strShiftEnd='N' and strDayEnd='N'");
		            
					}
				}
				
			
				

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return jObj;
    }  
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/ConsolidateEndDayProcess",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funConsolidateEndDayProcess(@RequestParam("emailReport") String EmailReport, HttpServletRequest req)
	{
		
		 userCode=req.getSession().getAttribute("gUserCode").toString();
		 strClientCode=req.getSession().getAttribute("gClientCode").toString();
		 strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		String ShiftNo="1";
		
		String shiftEnd="", DayEnd="", strShiftNo="";
		
		funConsolidateDayEndProcess( strPOSCode, ShiftNo, userCode, strPOSDate, strClientCode, EmailReport,req);
		 
		req.getSession().setAttribute("gDayEnd","Y");
		jsonConsolidateDayEndReturn.put("msg", "Succesfully Day End");
		//shiftEnd=jObj.get("shiftEnd").toString();
		//DayEnd=jObj.get("DayEnd").toString();
		///ShiftNo=jObj.get("shiftNo").toString();
     
		return jsonConsolidateDayEndReturn;//new ModelAndView("frmPOSShiftEndProcessConsolidate");
	}
	
	//btnShiftEndMouseClicked()..jpos
		public void funConsolidateDayEndProcess(String strPOSCode,String shiftNo,String strUserCode,String POSDate,String strClientCode,String EmailReport, HttpServletRequest req)
		{
			try{
				loginPOS=strPOSCode; //need login poscode in text file gen... 
				//this.strUserCode=strUserCode;
				this.strPOSCode=strPOSCode;
				strPOSDate=POSDate;
				this.shiftNo=Integer.parseInt(shiftNo);
				String gEnableShiftYN = objPOSSetupUtility.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
				
				//All table config data loaded.. DBConnfig File
				/*JSONObject jsonData=objPOSConfigSettingService.funLoadPOSConfigSetting(strClientCode);
			     JSONArray jArr=(JSONArray)jsonData.get("configSetting");
			     jsonConfig=jArr.getJSONObject(0);*/
			     
				if(gEnableShiftYN.equals("Y"))
				{
					//obBackupDatabase.funTakeBackUpDB(strClientCode);
	                funShiftEndButtonClicked(req);
				}
				else
				{
					 
				     //strOS
//					 if (jsonConfig.get("strOS").toString().equalsIgnoreCase("Windows"))
//	                 {
//						 obBackupDatabase.funTakeBackUpDB(strClientCode);// clsGlobalVarClass.funBackupDatabase();
//	                 }
					funShiftEndButtonClicked(req);
				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//return jsonConsolidateDayEndReturn;
		}
		
		private void funShiftEndButtonClicked(HttpServletRequest req)
	    {
	        try
	        {
	        	String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");
				
	            if (gEnableShiftYN.equals("Y"))//for shift wise
	            {
	                boolean flgDayEnd = false;
	                sql.setLength(0);
	                sql.append("select strPosCode from tbldayendprocess  "
	                        + " where intShiftCode>0 and strShiftEnd='N' "
	                        + " and date(dtePOSDate) = '" + strPOSDate + "' "
	                        + " ORDER by strPosCode ");
	                List listPOS=objBaseService.funGetList(sql, "sql");
					if(listPOS.size()>0)
					{
						for(int i=0;i<listPOS.size();i++)
						{
							funShiftEnd(listPOS.get(i).toString(),req);
						}
					}
	               
					sql.setLength(0);
					sql.append("select  sum(dblTotalSale),sum(dblTotalDiscount),sum(dblPayments) "
	                        + " from tbldayendprocess where date(dtePOSDate)='" + strPOSDate + "' "
	                        + " and strDayEnd='Y'");
					List listTot=objBaseService.funGetList(sql, "sql");
					if(listTot.size()>0)
					{
						for(int i=0;i<listTot.size();i++)
						{
							Object obTotData[]=(Object[]) listTot.get(i);
							
							String filePath = System.getProperty("user.dir");
		                    filePath = filePath + "/Temp/Temp_DayEndReport.txt";

		                    String gPrintType = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gPrintType");
		        			//String gPrintType=jsPrintType.get("gPrintType").toString();
		                    if (gPrintType.equalsIgnoreCase("Text File"))
		                    {
		                    	
		                       // clsTextFileGenerationForPrinting2 obj = new clsTextFileGenerationForPrinting2();
		                    	//obTextFileGenerationForPrinting2.funGenerateTextDayEndReport("All", strPOSDate, "", shiftNo,strClientCode,strUserCode);
		                        //String posCode, String billDate, String reprint, int shiftNo,String clientCode,String userCode
		                    }
		                    objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
		                       
		                   // new clsSendMail().funSendMail(Double.parseDouble(obTotData[0].toString()),Double.parseDouble(obTotData[1].toString()), Double.parseDouble(obTotData[2].toString()), filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
		                    //obSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
						}
					}
	             
	            }
	            else
	            {
	                boolean flgDayEnd = false;
	                sql.setLength(0);
	                sql.append("select strPosCode from tbldayendprocess  "
	                        + " where intShiftCode>0 and strShiftEnd='N' "
	                        + " and date(dtePOSDate) = '" + strPOSDate + "' "
	                        + " ORDER by strPosCode ");
	                
	                List listPOS=objBaseService.funGetList(sql, "sql");
					if(listPOS.size()>0)
					{
						for(int i=0;i<listPOS.size();i++)
						{
							funShiftEnd(listPOS.get(i).toString(),req);
						}
					}
	              
	                sql.setLength(0);
	                sql.append("select  sum(dblTotalSale),sum(dblTotalDiscount),sum(dblPayments) "
	                        + " from tbldayendprocess where date(dtePOSDate)='" + strPOSDate + "' "
	                        + " and strDayEnd='Y'");
	                
	                List listTot=objBaseService.funGetList(sql, "sql");
					if(listTot.size()>0)
					{
						for(int i=0;i<listTot.size();i++)
						{
							Object obTotData[]=(Object[]) listTot.get(i);
	  					    String filePath = System.getProperty("user.dir");
		                    filePath = filePath + "/Temp/Temp_DayEndReport.txt";
		                    String gPrintType = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gPrintType");
		        			
		                    if (gPrintType.equalsIgnoreCase("Text File"))
		                    {
		                      //  clsTextFileGenerationForPrinting2 obj = new clsTextFileGenerationForPrinting2();
		                    //	obTextFileGenerationForPrinting2.funGenerateTextDayEndReport("All", strPOSDate, "", shiftNo,strClientCode,strUserCode);
		                    	 
		                    }
		                    objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
			                   
		                    //new clsSendMail().funSendMail(rsTotData.getDouble(1), rsTotData.getDouble(2), rsTotData.getDouble(3), filePath);
		                    //obSendMail.funSendMail(Double.parseDouble(obTotData[0].toString()),Double.parseDouble(obTotData[1].toString()), Double.parseDouble(obTotData[2].toString()), filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
						}
					}
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }

		 private void funShiftEnd(String posCode,HttpServletRequest req)
		    {
		        try
		        {
		        	String gEnableShiftYN = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gEnableShiftYN");
					
		            if (gEnableShiftYN.equals("Y"))//for shift wise
		            {
		                sql.setLength(0);
		                sql.append("delete from tblitemrtemp where strTableNo='null'");
		                objBaseService.funExecuteUpdate(sql.toString(),"sql");
		                
		                //clsGlobalVarClass.gDayEndReportForm = "DayEndReport";
		                clsPOSDayEndProcess.gDayEndReportForm = "DayEndReport";
		                jsonConsolidateDayEndReturn.put("gDayEndReportForm","DayEndReport");
		                
		               sql.setLength(0);
		               sql.append("select date(max(dtePOSDate)),intShiftCode"
		                        + " from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'"
		                        + " and (strShiftEnd='' or strShiftEnd='N')");
		                
		                List listShift=objBaseService.funGetList(sql, "sql");
		                if(listShift.size()>0)
		                {
		                	for(int i=0;i<listShift.size();i++)
		                	{
		                		Object ob[]=(Object[])listShift.get(i);
		        	            shiftNo = Integer.parseInt(ob[1].toString());	
		                	}
		                	
		                }

		                sql.setLength(0);
		                sql.append("update tbltablemaster set strStatus='Normal' "
		                        + " where strPOSCode='" + posCode + "' ");

		                //                sql = "update tbldayendprocess set strShiftEnd='Y'"
		                //                        + " where strPOSCode='" + posCode + "' and strDayEnd='N'";
		                //                clsGlobalVarClass.dbMysql.execute(sql);
		                objPOSDayEndUtility.funGetNextShiftNoForShiftEnd(posCode, shiftNo,strClientCode, userCode,req);
		                //String posCode, int shiftNo,String strClientCode,String strUserCode
		            }
		            else
		            {

		                sql.setLength(0);
		                sql.append("delete from tblitemrtemp where strTableNo='null'");
		                objBaseService.funExecuteUpdate(sql.toString(), "sql");
		                //clsGlobalVarClass.dbMysql.execute(sql);

		                //clsGlobalVarClass.gDayEndReportForm = "DayEndReport";
		                clsPOSDayEndProcess.gDayEndReportForm = "DayEndReport";
		                jsonConsolidateDayEndReturn.put("gDayEndReportForm","DayEndReport");

		                sql.setLength(0); 
		                sql.append("select date(max(dtePOSDate)),intShiftCode"
		                        + " from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'"
		                        + " and (strShiftEnd='' or strShiftEnd='N')");
		                List listShift=objBaseService.funGetList(sql, "sql");
		                if(listShift.size()>0)
		                {
		                	for(int i=0;i<listShift.size();i++)
		                	{
		                		Object ob[]=(Object[])listShift.get(i);
		        	            shiftNo = Integer.parseInt(ob[1].toString());	
		                	}
		                	
		                }
		                else
		                {
		                    shiftNo++;
		                }

		                sql.setLength(0);
		                sql.append("update tbltablemaster set strStatus='Normal' "
		                        + " where strPOSCode='" + posCode + "'");

		                sql.setLength(0);
		                sql.append("update tbldayendprocess set strShiftEnd='Y'"
		                        + " where strPOSCode='" + posCode + "' and strDayEnd='N'");
		                objBaseService.funExecuteUpdate(sql.toString(),"sql");
		                
		                objPOSDayEndUtility.funGetNextShiftNo(posCode, shiftNo,strClientCode,userCode,req);
		            }
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/ConsolidateCheckBillSettleBusyTable",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funCheckBillSettleBusyTable(HttpServletRequest req)
	{
		JSONObject jsonObj=new JSONObject();
		try{
				
//			strPOSCode=req.getSession().getAttribute("loginPOS").toString();
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
	
			String ShiftNo="1";
			boolean pendingBills=false,busyTable=false;
			sql.setLength(0);
            sql.append("select strPOSCode from tblposmaster where strOperationalYN='Y' ");
            List listPOS=objBaseService.funGetList(sql, "sql");
			if(listPOS.size()>0)
			{
				for(int i=0;i<listPOS.size();i++)
				{
					pendingBills=objPOSDayEndUtility.funCheckPendingBills(listPOS.get(i).toString(), strPOSDate);
					busyTable=objPOSDayEndUtility.funCheckTableBusy(listPOS.get(i).toString());
					if(pendingBills==false || busyTable ==false){
						break;
					}
				}
			}
			
			jsonObj.put("PendingBills", pendingBills);
			jsonObj.put("BusyTables", busyTable);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jsonObj; 
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
