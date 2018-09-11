package com.sanguine.webpos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;
import com.sanguine.webpos.controller.clsPOSDayEndProcess;
import com.sanguine.webpos.model.clsDayEndProcessHdModel;

@Controller
public class clsPOSDayEndUtility {
	
	 @Autowired
	 private clsPOSUtilityController objUtilityController;
	
	 @Autowired
	 private clsBaseServiceImpl objBaseServiceImpl;
	 
	 @Autowired
	 clsPOSSynchronizePOSDataToHO objSynchronizePOSDataToHO;
	 
	 @Autowired
	 clsPOSTextFileGenerationForPrinting2 obTextFileGenerationForPrinting2;
	 
	 @Autowired
	 clsPOSSetupUtility objPOSSetupUtility;
	 
	 int gShiftNo;
	 public static String gShiftEnd="",gDayEnd="N";
	
	 String gLastPOSForDayEnd="",gCMSIntegrationYN="",gCMSPostingType="";
		
		double gTotalDiscounts=0, gTotalCashSales=0,gTotalAdvanceAmt=0,gTotalReceipt=0,
				gTotalPayments=0, gTotalCashInHand=0;
		int gNoOfDiscountedBills=0,gTotalBills=0;
	/*here all Day End functions*/
	public boolean funCheckPendingBills(String posCode,String POSDate)
	{
		boolean flgPendingBills = false;
        try
        {
            StringBuilder sqlPendingBill =new StringBuilder(); 
            sqlPendingBill =new StringBuilder("select count(*) "
                    + "from tblbillhd where date(dteBillDate)='" + POSDate + "' "
                    + "and strBillNo NOT IN(select strBillNo from tblbillsettlementdtl) "
                    + "and strPOSCode='" + posCode + "'");
            //System.out.println(sql_PendingBill);
            List list=objBaseServiceImpl.funGetList(sqlPendingBill, "sql");
            int count=0;
            if(list.size()>0)
        	{
	             count=Integer.parseInt(list.get(0).toString());
	            if (count > 0)
	            {
	                flgPendingBills = true;
	            }
        	}
            
            sqlPendingBill =new StringBuilder("select count(*) from tblbillhd "
                    + " where date(dteBillDate)='" + POSDate + "' and  strTableNo is not NULL and strBillNo"
                    + " NOT IN(select strBillNo from tblbillsettlementdtl) "
                    + " and strPOSCode='" + posCode + "'");
            //System.out.println(sql_PendingBill);
            list=objBaseServiceImpl.funGetList(sqlPendingBill, "sql");
             if(list.size()>0)
	        	{  
	              count=Integer.parseInt(list.get(0).toString());
		            if (count > 0)
		            {
		                flgPendingBills = true;
		            }
	        	}
           }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flgPendingBills;
	}

	public boolean funCheckTableBusy(String posCode)
    {
        boolean flgCheckTable = false;
        try
        {
        	
        	objBaseServiceImpl.funExecuteUpdate("delete from tblitemrtemp where strNCKotYN='Y'", "sql");
        	objBaseServiceImpl.funExecuteUpdate("delete from tblitemrtemp_bck where strNCKotYN='Y'","sql");
        	
        	StringBuilder sb=new StringBuilder("select count(*) from tblitemrtemp where strNCKotYN='N' "
                    + "and strPOSCode='" + posCode + "'");
        	List list=objBaseServiceImpl.funGetList(sb, "sql");
        	int count=0;
        	if(list.size()>0)
        	{
        		count=Integer.parseInt(list.get(0).toString());
	            if (count > 0)
	            {
	                flgCheckTable = true;
	            }
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flgCheckTable;
    }
	
	@SuppressWarnings("finally")
	public boolean isCheckedInMembers(String posCode)
    {
	boolean isCheckedInMembers = false;
	try
	{
	    StringBuilder sql =new StringBuilder( "select a.strRegisterCode,a.strPOSCode,a.strIn,a.strOut "
		    + "from tblregisterinoutplayzone a "
		    + "where a.strOut='N' "
		    + "and a.strPOSCode='" + posCode + "' ");
	    List list=objBaseServiceImpl.funGetList(sql, "sql");
	    if(list!=null && list.size()>0){
	    	isCheckedInMembers = true;
		    	
	    }
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    return isCheckedInMembers;
	}
    }

	public int funGetNextShiftNoForShiftEnd(String posCode, int shiftNo,String strClientCode,String strUserCode,HttpServletRequest req)
    {

        int retvalue = 0;
        int shiftCount = 0;
        String billDate="";
        try
        {
            String billDateSql = "select date(max(dtePOSDate)) from tbldayendprocess where strPOSCode='" + posCode + "'";
            //ResultSet rsDayEnd = clsGlobalVarClass.dbMysql.executeResultSet(billDateSql);
            List list=objBaseServiceImpl.funGetList(new StringBuilder(billDateSql), "sql");
            if(list.size()>0)
            {
                billDate =((Object)list.get(0)).toString();
            }
            String sql = "select count(intShiftCode) from tblshiftmaster where strPOSCode='" + posCode + "'";
            list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
            if(list.size()>0)
            {
            	shiftCount =Integer.parseInt(((Object)list.get(0)).toString());
            }
            if (shiftCount > 0)
            {
                if (shiftNo == shiftCount)
                {
                	gShiftNo=1;  //clsGlobalVarClass.gShiftNo=1;
                    retvalue = funShiftEndProcess("DayEnd", posCode, shiftNo, billDate,strClientCode,strUserCode,req);
                }
                else
                {
                    retvalue = funShiftEndProcess("ShiftEnd", posCode, shiftNo, billDate,strClientCode,strUserCode,req);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return retvalue;
        }
    
    }

	public int funGetNextShiftNo(String posCode, int shiftNo,String strClientCode,String strUserCode,HttpServletRequest req)
    {
        int retvalue = 0;
        try
        {
        	 String billDate="";
            String billDateSql = "select date(max(dtePOSDate)) from tbldayendprocess where strPOSCode='" + posCode + "'";
            List list=objBaseServiceImpl.funGetList(new StringBuilder(billDateSql), "sql");
            if(list.size()>0)
            {
                billDate =((Object)list.get(0)).toString();
 
            }

            retvalue = funShiftEndProcess("DayEnd", posCode, shiftNo, billDate,strClientCode,strUserCode,req);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return retvalue;
        }
    }

	
	  public int funShiftEndProcess(String status, String posCode, int shiftNo, String billDate,String strClientCode,String strUserCode,HttpServletRequest req)
	    {
	        String newStartDate = "";
	        int shiftEnd = 0;
	        int retvalue = 1;
	        try
	        {

	            if (status.equalsIgnoreCase("DayEnd"))//for day end
	            {
	            	
	            	String gLastPOSForDayEnd=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gLastPOSForDayEnd");
	    			
	    			// Transfer Card Balance To Debit Card Revenue Table.
	                if (gLastPOSForDayEnd.equals(posCode))
	                {
	                    Date dt = new Date();
	                    String posDateTemp = billDate + " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	                    funShiftCardBalToRevenueTable(posCode, posDateTemp,strClientCode,strUserCode);
	                }

	                // Post Sales Data to CMS CL and RV Tables.  
	                //commented for Poona Club
	                 String gCMSIntegrationYN=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSIntegrationYN");
	    			 String gCMSPostingType=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSPostingType");
	    			
	                if (gCMSIntegrationYN.equalsIgnoreCase("Y"))
	                {
	                    if (gCMSPostingType.equals("Sanguine CMS"))
	                    {
	                        if (funPostSanguineCMSData(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                    else
	                    {
	                        // Post Sales Data to CMS CL and RV Tables. 
	                        if (funPostBillDataToCMS(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                }

	                // Generate next POS Date / POS Shift Date   
	                String sql = "select count(*) from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                List listDayEndRecord=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	                if(listDayEndRecord.size()>0)
	                {
	                	for(int i=0;i<listDayEndRecord.size();i++)
	                	{
	                	int count=Integer.parseInt(((Object)listDayEndRecord.get(0)).toString());
	                		//Object ob[]=(Object[])listDayEndRecord.get(i);
	                		if (count > 0)
			                {
			                    String tempPOSDate = "";
			                    
			                    sql = "select date(max(dtePOSDate)) from tbldayendprocess "
			                            + "where strPOSCode='" + posCode + "'";
			                    listDayEndRecord=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
			                    Date startDate =new Date();
			                    String shiftDate="";
			                    if(listDayEndRecord.size()>0)
			                    {
			                    	
			                    	tempPOSDate = ((Object)listDayEndRecord.get(0)).toString();
				                    startDate = new SimpleDateFormat("yyyy-MM-dd").parse(tempPOSDate);
				                    shiftDate= ((Object)listDayEndRecord.get(0)).toString();
			                    		
			                    }
			                    if (status.equals("DayEnd"))
			                    {
			                        GregorianCalendar cal = new GregorianCalendar();
			                        cal.setTime(startDate);
			                        cal.add(Calendar.DATE, 1);
			                        newStartDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());
			                      
			                    }
			                    else
			                    {
			                        newStartDate = shiftDate;
			                    }
			
			                    String dayEnd = "N";
			                    int shift = 0;
			                    //clsGlobalVarClass.dbMysql.funStartTransaction();
			                    if (status.equals("DayEnd"))
			                    {
			                        sql = "update tbldayendprocess set strDayEnd='Y',strShiftEnd='Y' "
			                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
			                        objBaseServiceImpl.funExecuteUpdate(sql, "sql");
			                        dayEnd = "Y";
			                    }
			                    else
			                    {
			                        sql = "update tbldayendprocess set strDayEnd='N',strShiftEnd='Y' "
			                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
			                        objBaseServiceImpl.funExecuteUpdate(sql, "sql");
			                        shift = shiftNo;
			                    }
			                    String flgCarryForwardFloatAmtToNextDay = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "flgCarryForwardFloatAmtToNextDay");
			        			
			                    if (flgCarryForwardFloatAmtToNextDay.equalsIgnoreCase("Y"))
			                    {
			                        String reasonCode = "";
			                        sql = "select strReasonCode from tblreasonmaster where strCashMgmt='Y'";
			                      
			                        List listReason=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
			                        
			                        if (listReason.size()>0)
			                        {
			                            reasonCode = ((Object)listReason.get(0)).toString();
			                        }
			                        Date dt = new Date();
			                        String transDateForCashMgmt = newStartDate.split(" ")[0];
			                        transDateForCashMgmt += " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
			                        clsPOSCashManagement objCashMgmt = new clsPOSCashManagement();
			                        Map<String, clsPOSCashManagementDtlBean> hmCashMgmtDtl = objCashMgmt.funGetCashManagement(tempPOSDate, tempPOSDate, posCode);
			
			                        for (Map.Entry<String, clsPOSCashManagementDtlBean> entry : hmCashMgmtDtl.entrySet())
			                        {
			                            String transId = funGenerateNextCode();
			                            clsPOSCashManagementDtlBean objCashMgmtDtl = entry.getValue();
			                            double balanceAmt = (objCashMgmtDtl.getSaleAmt() + objCashMgmtDtl.getAdvanceAmt() + objCashMgmtDtl.getFloatAmt() + objCashMgmtDtl.getTransferInAmt()) - (objCashMgmtDtl.getWithdrawlAmt() + objCashMgmtDtl.getPaymentAmt() + objCashMgmtDtl.getRefundAmt() + objCashMgmtDtl.getTransferOutAmt());
			                            if (null != entry.getValue().getHmPostRollingSalesAmt())
			                            {
			                                for (Map.Entry<String, Double> entryPostRollingSales : entry.getValue().getHmPostRollingSalesAmt().entrySet())
			                                {
			                                    balanceAmt += entryPostRollingSales.getValue();
			                                }
			                            }
			
			                            if (balanceAmt > 0)
			                            {
			                                sql = "insert into tblcashmanagement(strTransID,strTransType,dteTransDate,strReasonCode,strPOSCode"
			                                        + ",dblAmount,strRemarks,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strCurrencyType"
			                                        + ",intShiftCode,strAgainst,dblRollingAmt,strClientCode,strDataPostFlag) "
			                                        + "values ('" + transId + "','Float','" + transDateForCashMgmt + "','" + reasonCode + "'"
			                                        + ",'" + posCode + "','" + balanceAmt + "','Carryforward Float Amt'"
			                                        + ",'" + entry.getKey() + "','" + entry.getKey() + "','" + getCurrentDateTime() + "'"
			                                        + ",'" +getCurrentDateTime() + "','Cash','" + shiftNo + "'"
			                                        + ",'Direct','0','" + strClientCode + "','N')";
			                                
			                               objBaseServiceImpl.funExecuteUpdate(sql, "sql");
			                             }
			                        }
			                    }
			
			                   /* sql = "insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,intShiftCode,strShiftEnd"
			                            + ",strUserCreated,dteDateCreated) "
			                            + "values('" + posCode + "','" + newStartDate + "','N'," + shift + ",''"
			                            + ",'" + strUserCode + "','" + getCurrentDateTime() + "')";
			                    objBaseServiceImpl.funExecuteUpdate(sql, "sql");*/
			                    
			                    clsDayEndProcessHdModel objDayEndProcessHdModel = new clsDayEndProcessHdModel();
			                    objDayEndProcessHdModel.setStrPOSCode(posCode);
			                    objDayEndProcessHdModel.setDtePOSDate(newStartDate);
			                    objDayEndProcessHdModel.setStrDayEnd("N");
			                    objDayEndProcessHdModel.setDblTotalSale(0.00);
			                    objDayEndProcessHdModel.setDblNoOfBill(0);
			                    objDayEndProcessHdModel.setDblNoOfVoidedBill(0);
			                    objDayEndProcessHdModel.setDblNoOfModifyBill(0);
			                    objDayEndProcessHdModel.setDblHDAmt(0);
			                    objDayEndProcessHdModel.setDblDiningAmt(0);
			                    objDayEndProcessHdModel.setDblTakeAway(0);
			                    objDayEndProcessHdModel.setDblFloat(0);
			                    objDayEndProcessHdModel.setDblCash(0);
			                    objDayEndProcessHdModel.setDblAdvance(0);
			                    objDayEndProcessHdModel.setDblTransferIn(0);
			                    objDayEndProcessHdModel.setDblTransferOut(0);
			                    objDayEndProcessHdModel.setDblPayments(0);
			                    objDayEndProcessHdModel.setDblWithdrawal(0);
			                    objDayEndProcessHdModel.setDblTransferOut(0);
			                    objDayEndProcessHdModel.setDblTotalPay(0);
			                    objDayEndProcessHdModel.setDblCashInHand(0);
			                    objDayEndProcessHdModel.setDblRefund(0);
			                    objDayEndProcessHdModel.setDblTotalDiscount(0);
			                    objDayEndProcessHdModel.setDblNoOfDiscountedBill(0);
			                    objDayEndProcessHdModel.setIntShiftCode(shift);
			                    objDayEndProcessHdModel.setStrShiftEnd("");
			                    objDayEndProcessHdModel.setIntTotalPax(0);
			                    objDayEndProcessHdModel.setIntNoOfTakeAway(0);
			                    objDayEndProcessHdModel.setIntNoOfHomeDelivery(0);
			                    objDayEndProcessHdModel.setStrUserCreated(strUserCode);
			                    objDayEndProcessHdModel.setStrUserEdited(strUserCode);
			                    objDayEndProcessHdModel.setDteDateCreated(getCurrentDateTime() );
			                    objDayEndProcessHdModel.setDteDayEndDateTime(getCurrentDateTime() );
			                    objDayEndProcessHdModel.setStrClientCode(strClientCode);
			                    objDayEndProcessHdModel.setStrDataPostFlag("N");
			                    objDayEndProcessHdModel.setIntNoOfNCKOT(0);
			                    objDayEndProcessHdModel.setIntNoOfComplimentaryKOT(0);
			                    objDayEndProcessHdModel.setIntNoOfVoidKOT(0);
			                    objDayEndProcessHdModel.setDblUsedDebitCardBalance(0.00);
			                    objDayEndProcessHdModel.setDblUnusedDebitCardBalance(0.00);
			                    objDayEndProcessHdModel.setStrWSStockAdjustmentNo("");
			                    objDayEndProcessHdModel.setDblTipAmt(0.00);
			                    objDayEndProcessHdModel.setStrExciseBillGeneration("");
			                    objDayEndProcessHdModel.setDblNetSale(0.00);
			                    objDayEndProcessHdModel.setDblGrossSale(0.00);
			                    objDayEndProcessHdModel.setDblAPC(0.00);
			                    
			                    objBaseServiceImpl.funSave(objDayEndProcessHdModel);
	//*********************	                 
			                    req.getSession().setAttribute("gShiftEnd", "");
			        			req.getSession().setAttribute("gDayEnd", "N");
			                    gShiftEnd="";
	                            			   // clsGlobalVarClass.gShiftEnd = "";
			                    gDayEnd="N";	//  clsGlobalVarClass.gDayEnd = "N";
			                  				//  clsGlobalVarClass.setStartDate(newStartDate);
			                  				//  clsGlobalVarClass.funSetPOSDate();
			                  				//   System.out.println("Shift = " + clsGlobalVarClass.gShifts);
			
			                    if (status.equals("ShiftEnd"))
			                    {
			                        //shiftEnd=shiftNo-1;
			                        shiftEnd = shiftNo;
			                    }
			                    else
			                    {
			                        shiftEnd = shiftNo;
			                    }
			
			                    //  Calculate Total Cash Amt, Total Advance Amt, Total Receipts , Total Payments, Ttoal Discount Amt
			                    //  , No of Discounted Bills, No of Total bills.
			                    funCalculateDayEndCash(shiftDate, shiftEnd, posCode);
			                    // Update tbldayendprocess table fields  
			                    
			                    funUpdateDayEndFields(shiftDate, shiftEnd, dayEnd, posCode,strUserCode);
			                    String posDate = billDate;
			
			                    String gPostSalesDataToMMS = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gPostSalesDataToMMS");
			        			
			                    String gItemType = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gItemType");
				        		
			                    // Post POS Item Sale Data to MMS.
			                    if (gPostSalesDataToMMS.equalsIgnoreCase("Y"))
			                    {
			                        String WSStockAdjustmentCode =objSynchronizePOSDataToHO.funPostPOSItemSalesDataAuto(gItemType,posCode,posDate,posDate,strClientCode); 
			                        		
			//                        sql = "update tbldayendprocess set strWSStockAdjustmentNo='" + WSStockAdjustmentCode + "'" + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
			//                        clsGlobalVarClass.dbMysql.execute(sql);
			                        String exbillGenCode =objSynchronizePOSDataToHO.funPostPOSSalesDataToExciseAuto(gItemType,posCode,posDate,posDate,strClientCode);
			                        	//	clsGlobalVarClass.funPostItemSalesDataExcise(posCode, posDate, posDate);
			                    }
			
			                    // Transfer Billing Data from Live Tables To QFile Tables.
			                    funInsertQBillData(posCode,strClientCode);
			
			                  //clear non avail items for today
			        		    String deleteNONAvailItemsDataSql = "delete from tblnonavailableitems where strPOSCode='" + posCode + "';";
			        		    objBaseServiceImpl.funExecuteUpdate(deleteNONAvailItemsDataSql, "sql");
			        		    
			        		    //reset last order no
			        		    objBaseServiceImpl.funExecuteUpdate("update tblinternal set dblLastNo=0 where strTransactionType='OrderNo' ", "sql");

			                    // Post Sales Transaction Data, Inventory Transaction Data, Audit Transaction Data, Customer Masterok
			                    // and Customer Area Master to HO.
			        		    
			                    String gConnectionActive="Y";// direct initialized in global 
			                    String gDataSendFrequency=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gDataSendFrequency");
			                    if (gConnectionActive.equals("Y") && gDataSendFrequency.equalsIgnoreCase("After Day End"))
			                    {
			                        funInvokeHOWebserviceForTrans("All", "Day End",strClientCode,posCode);
			                       funPostCustomerDataToHOPOS(strClientCode,posCode);
			                       funPostCustomerAreaDataToHOPOS(strClientCode,posCode);
			                    }
			
			                    // Post Day End Table Data to HO.    
			                    funPostDayEndData(newStartDate, shift,strClientCode,posCode);
			
			                   if (clsPOSDayEndProcess.gTransactionType != null && clsPOSDayEndProcess.gTransactionType.equalsIgnoreCase("ShiftEnd"))
			                    {
			                        retvalue = funDayEndflash(strClientCode, posCode, billDate, shiftNo,strUserCode);
			                    }
			                   String gEnableBillSeries=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gEnableBillSeries");
			                   String gNewBillSeriesForNewDay=objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gNewBillSeriesForNewDay");
			                if (gEnableBillSeries.equalsIgnoreCase("Y"))
			       		    {
				       			if (gNewBillSeriesForNewDay.equalsIgnoreCase("Y"))
				       			{
				       				objBaseServiceImpl.funExecuteUpdate("update tblbillseries "
				       				    + "set intLastNo=0 "
				       				    + "where (strPOSCode='" + posCode + "' or strPOSCode='All' ) "
				       				    + "and strClientCode='" + strClientCode + "' ","sql");
				       			}
			       		    }
			       		    else
			       		    {
				       			if (gNewBillSeriesForNewDay.equalsIgnoreCase("Y"))
				       			{
				       				objBaseServiceImpl.funExecuteUpdate("update tbllaststoreadvbookingbill set strAdvBookingNo=0  "
				       				    + "where strPOSCode='" + posCode + "' ;","sql");
	
				       				objBaseServiceImpl.funExecuteUpdate("update tblstorelastbill set strBillNo=0  "
				       				    + "where strPOSCode='" + posCode + "' ;","sql");
				       			}
			       		    }

			       		    //send dayend sms                    
			       		    StringBuilder sbsql =new StringBuilder( "select a.strSendSMSYN,a.longMobileNo "
			       			    + "from tblsmssetup a "
			       			    + "where (a.strPOSCode='" + posCode + "' or a.strPOSCode='All') "
			       			    + "and a.strClientCode='" +strClientCode + "' "
			       			    + "and a.strTransactionName='DayEnd' "
			       			    + "and a.strSendSMSYN='Y'; ");
			       		    List listSendsms=objBaseServiceImpl.funGetList(sbsql, "sql");
				       		    if(listSendsms.size()>0){
				       		    	Object[] ob=(Object[])listSendsms.get(0); 
				       		    	String mobileNo = ob[1].toString();//mobileNo
				       		 	
					       			funSendDayEndSMS(mobileNo, posCode, billDate, shiftNo,req);
		
				       		    }
			                }
	                	}
	                }
	            }
	            else //for shift end
	            {
	            	gLastPOSForDayEnd = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gLastPOSForDayEnd");
	    		    // Transfer Card Balance To Debit Card Revenue Table.
	                if (gLastPOSForDayEnd.equals(posCode))
	                {
	                    Date dt = new Date();
	                    String posDateTemp = billDate + " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	                    funShiftCardBalToRevenueTable(posCode, posDateTemp,strClientCode,strUserCode);
	                }
	                gCMSIntegrationYN = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSIntegrationYN");
	    			
	    			gCMSPostingType = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSPostingType");
	    			// Post Sales Data to CMS CL and RV Tables.    
	                if (gCMSIntegrationYN.equals("Y"))
	                {
	                    if (gCMSPostingType.equals("Sanguine CMS"))
	                    {
	                        if (funPostSanguineCMSData(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                    else
	                    {
	                        // Post Sales Data to CMS CL and RV Tables. 
	                        if (funPostBillDataToCMS(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                }

	                // Generate next POS Date / POS Shift Date   
	                String sql = "select count(*) from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                List listDayEndRecord=objBaseServiceImpl.funGetList(new StringBuilder(sql) ,"sql");
	                if(listDayEndRecord.size()>0)
	                {
	                	int count=Integer.parseInt(((Object)listDayEndRecord.get(0)).toString());
	                if (count > 0)
		                {
		                    sql = "select date(max(dtePOSDate)) from tbldayendprocess "
		                            + "where strPOSCode='" + posCode + "'";
		                    listDayEndRecord=objBaseServiceImpl.funGetList(new StringBuilder(sql) ,"sql");
		                    Date startDate=new Date();
		                    String shiftDate ="",tempPOSDate="";
		                    if(listDayEndRecord.size()>0)
		                    {
		                    	shiftDate = ((Object)listDayEndRecord.get(0)).toString();
			                    startDate = new SimpleDateFormat("yyyy-MM-dd").parse(tempPOSDate);
			                }
		                    if (status.equals("DayEnd"))
		                    {
		                        GregorianCalendar cal = new GregorianCalendar();
		                        cal.setTime(startDate);
		                        cal.add(Calendar.DATE, 1);
		                        newStartDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());
		                        
		                    }
		                    else
		                    {
		                        newStartDate = shiftDate;
		                    }
		
		                    String dayEnd = "N";
		                    int shift = 0;
		                    //clsGlobalVarClass.dbMysql.funStartTransaction();
		                    if (status.equals("DayEnd"))
		                    {
		                        sql = "update tbldayendprocess set strDayEnd='Y',strShiftEnd='Y' "
		                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
		                      objBaseServiceImpl.funExecuteUpdate(sql, "sql");
		                        //clsGlobalVarClass.dbMysql.execute(sql);
		                        dayEnd = "Y";
		                    }
		                    else
		                    {
		                        sql = "update tbldayendprocess set strDayEnd='N',strShiftEnd='Y' "
		                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
		                        objBaseServiceImpl.funExecuteUpdate(sql, "sql");
		                        //clsGlobalVarClass.dbMysql.execute(sql);
		                        shift = shiftNo;
		                    }
		                    sql = "insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,intShiftCode,strShiftEnd"
		                            + ",strUserCreated,dteDateCreated) "
		                            + "values('" + posCode + "','" + newStartDate + "','N'," + (shift + 1)
		                            + ",'','" + strUserCode + "','" + getCurrentDateTime() + "')";
		                    objBaseServiceImpl.funExecuteUpdate(sql, "sql");
		                    //clsGlobalVarClass.dbMysql.execute(sql);
		                    
		                    gShiftEnd = "";
		                    gDayEnd = "N";
		                    gShiftNo = (shift + 1);
		                    req.getSession().setAttribute("gShiftEnd", "");
		        			req.getSession().setAttribute("gDayEnd", "N");
		        			
		               //     clsGlobalVarClass.setStartDate(newStartDate);
		                 //   clsGlobalVarClass.funSetPOSDate();
		                 //   System.out.println("Shift = " + gShifts);
		
		                    if (status.equals("ShiftEnd"))
		                    {
		                        //shiftEnd=shiftNo-1;
		                        shiftEnd = shiftNo;
		                    }
		                    else
		                    {
		                        shiftEnd = shiftNo;
		                    }
		
		                    //  Calculate Total Cash Amt, Total Advance Amt, Total Receipts , Total Payments, Ttoal Discount Amt
		                    //  , No of Discounted Bills, No of Total bills.
		                    funCalculateDayEndCash(shiftDate, shiftEnd, posCode);
		
		                    // Update tbldayendprocess table fields     
		                    funUpdateDayEndFields(shiftDate, shiftEnd, dayEnd, posCode,strUserCode);
		                    String posDate = billDate;
		
		                    String gPostSalesDataToMMS = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gPostSalesDataToMMS");
		        			String gItemType = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gItemType");
			        		
		                    // Post POS Item Sale Data to MMS.
		                    if (gPostSalesDataToMMS.equals("Y"))
		                    {
		                    	  String WSStockAdjustmentCode =objSynchronizePOSDataToHO.funPostPOSItemSalesDataAuto(gItemType,posCode,posDate,posDate,strClientCode); 
	                      		//   sql = "update tbldayendprocess set strWSStockAdjustmentNo='" + WSStockAdjustmentCode + "'" + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
		                  		//   clsGlobalVarClass.dbMysql.execute(sql);
		                  		 String exbillGenCode =objSynchronizePOSDataToHO.funPostPOSSalesDataToExciseAuto(gItemType,posCode,posDate,posDate,strClientCode);
		                  		
		                    }
		                    String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gEnableShiftYN");
		        			
		        			String gLockDataOnShiftYN = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gLockDataOnShiftYN");
		        			// Transfer Billing Data from Live Tables To QFile Tables.
		                    if (gEnableShiftYN.equals("Y") && gLockDataOnShiftYN.equals("Y"))
		                    {
		                        funInsertQBillData(posCode,strClientCode);
		                    }
		
		                    // Post Sales Transaction Data, Inventory Transaction Data, Audit Transaction Data, Customer Masterok
		                    // and Customer Area Master to HO.
		                    String gConnectionActive="Y";
		                    if (gConnectionActive.equals("Y"))
		                    {
		                    	
		                    	   funInvokeHOWebserviceForTrans("All", "Day End",strClientCode,posCode);
			                       funPostCustomerDataToHOPOS(strClientCode,posCode);
			                       funPostCustomerAreaDataToHOPOS(strClientCode,posCode);
		                       
		                    }
		
		                    // Post Day End Table Data to HO.    
		                    funPostDayEndData(newStartDate, shift,strClientCode,posCode);
		                    if(clsPOSDayEndProcess.gTransactionType.equalsIgnoreCase("ShiftEnd"))
		                    {
		                        retvalue = funDayEndflash(strClientCode, posCode, billDate, shiftNo,strUserCode);
		                      //  retvalue = funDayEndflash(posCode, billDate, shiftNo);
		                    }
		                }
	                }
	            }
	        }
	        catch (Exception ex)
	        {
	            //clsGlobalVarClass.dbMysql.funRollbackTransaction();
	            ex.printStackTrace();
	        }
	        finally
	        {
	            return retvalue;
	        }
	    }

	  

	    public int funDayEndflash(String clientCode,String posCode, String billDate, int shiftNo,String strUserCode)
	    {
	        try
	        {
	            String filePath = System.getProperty("user.dir");
	            filePath = filePath + "/Temp/Temp_DayEndReport.txt";

	            String gPrintType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gPrintType");
				
	            if (gPrintType.equalsIgnoreCase("Text File"))
	            {
	                
	                obTextFileGenerationForPrinting2.funGenerateTextDayEndReport(posCode, billDate, "", shiftNo,clientCode,strUserCode);
	            }
	            else
	            {
	                
	            	obTextFileGenerationForPrinting2.funGenerateTextDayEndReport(posCode, billDate, "", shiftNo,clientCode,strUserCode);
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            return 1;
	        }
	    }
		

	    public int funShiftCardBalToRevenueTable(String posCode, String posDate,String strClientCode,String strUserCode) throws Exception
	    {
	        String sql = "select a.strCardTypeCode from tbldebitcardtype a "
	                + "where a.intValidityDays=1";
	        List list= objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	        if(list.size()>0)
	        {
	            String cardType =((Object)list.get(0)).toString();

	            sql = "select a.strCardNo,a.dblRedeemAmt "
	                    + " from tbldebitcardmaster a "
	                    + "where a.strCardTypeCode='" + cardType+ "' and a.dblRedeemAmt > 0";
	            List listCD= objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	            if(listCD.size()>0)
	            {
	            	for(int i=0;i<listCD.size();i++)
	            	{
		            		Object ob[]=(Object[])listCD.get(i);
		            		sql = "insert into tbldebitcardrevenue (strCardNo,dblCardAmt,strPOSCode,dtePOSDate,dteDate"
		                        + ",strClientCode,strDataPostFlag,strUserCreated) values"
		                        + "('" + ob[0].toString() + "','" + ob[1].toString() + "','" + posCode + "','" + posDate + "'"
		                        + ",'" + getCurrentDateTime() + "','" + strClientCode + "','N'"
		                        + ",'" + strUserCode + "')";
		            		
		                //System.out.println(sql);
		            	objBaseServiceImpl.funExecuteUpdate(sql, "sql");
		                
		                sql = "update tbldebitcardmaster set dblRedeemAmt=0 where strCardNo='" + ob[0].toString() + "'";
		                objBaseServiceImpl.funExecuteUpdate(sql, "sql");
		                
	            	}
	            }
	       
	        }
	       

	        return 1;
	    }

	    // Function to send bill data to sanguine cms.
	    public int funPostSanguineCMSData(String posCode, String billDate,String ClientCode,String userCode)
	    {
	        int res = 0;
	        String roundOffAccCode = "";
	        double roundOff = 0, creditAmt = 0, debitAmt = 0;
	        try
	        {
	        	String POSName="";
	        	String sqlpos="select strPosName from tblposmaster where strPOSCode='"+posCode+"'";
	        	List listPOS=objBaseServiceImpl.funGetList(new StringBuilder(sqlpos), "sql");
	            if(listPOS.size()>0)
	            {
	            	POSName=((Object)listPOS.get(0)).toString();
	            }
	            JSONObject jObj = new JSONObject();
	            String gCMSPOSCode= objPOSSetupUtility.funGetParameterValuePOSWise(ClientCode,posCode, "gCMSPOSCode");
				
				jObj.put("POSCode", posCode);
	            jObj.put("POSDate", billDate);
	            jObj.put("User", userCode);

	            String sql_SubGroupWise = "SELECT a.strPOSCode, IFNULL(d.strSubGroupCode,'NA'), IFNULL(d.strSubGroupName,'NA'), SUM(b.dblAmount), DATE(a.dteBillDate),d.strAccountCode "
	                    + "FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d  "
	                    + "WHERE a.strPOSCode='" + posCode + "' "
	                    + "and a.strBillNo=b.strBillNo "
	                    + " and b.strItemCode=c.strItemCode "
	                    + " and c.strSubGroupCode=d.strSubGroupCode "
	                    + "GROUP BY d.strSubGroupCode,d.strSubGroupName ";

	            JSONArray arrObjSubGroupwise = new JSONArray();
	            List listSBGroup=objBaseServiceImpl.funGetList(new StringBuilder(sql_SubGroupWise), "sql");
	            if(listSBGroup.size()>0)
	            {
	               for(int i=0;i<listSBGroup.size();i++)
		           {
		        	   Object ob[]=(Object[])listSBGroup.get(i);
		                JSONObject objSubGroupWise = new JSONObject();
		                creditAmt += Double.parseDouble(ob[3].toString());
		                objSubGroupWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
		                objSubGroupWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
		                objSubGroupWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
		                objSubGroupWise.put("DRAmt", 0);
		                objSubGroupWise.put("ClientCode", ClientCode);
		                objSubGroupWise.put("BillDate", ob[4].toString());
		                objSubGroupWise.put("CMSPOSCode", gCMSPOSCode);
		                objSubGroupWise.put("POSCode", posCode);
		                objSubGroupWise.put("BillDateTo",ob[4].toString());
		                objSubGroupWise.put("AccountCode", ob[5].toString());
		                arrObjSubGroupwise.add(objSubGroupWise);
		            }
	            }
	            jObj.put("SubGroupwise", arrObjSubGroupwise);

	            String sql_TaxWise = "SELECT a.strPOSCode,c.strTaxCode,c.strTaxDesc, SUM(b.dblTaxAmount), DATE(a.dteBillDate),c.strAccountCode "
	                    + "FROM tblbillhd a,tblbilltaxdtl b , tbltaxhd c "
	                    + "where a.strPOSCode='" + posCode + "' "
	                    + "and a.strBillNo=b.strBillNo "
	                    + "and b.strTaxCode=c.strTaxCode "
	                    + "GROUP BY c.strTaxCode ";

	            JSONArray arrObjTaxwise = new JSONArray();
	            List listTaxWise=objBaseServiceImpl.funGetList(new StringBuilder(sql_TaxWise), "sql");
	            if(listTaxWise.size()>0)
	            {
	               for(int i=0;i<listTaxWise.size();i++)
		           {
		        	   Object ob[]=(Object[])listTaxWise.get(i);
		               
		               JSONObject objTaxWise = new JSONObject();
		               creditAmt += Double.parseDouble(ob[3].toString());
		                objTaxWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
		                objTaxWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
		                objTaxWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
		                objTaxWise.put("DRAmt", 0);
		                objTaxWise.put("ClientCode", ClientCode);
		                objTaxWise.put("BillDate", ob[4].toString());
		                objTaxWise.put("CMSPOSCode", gCMSPOSCode);
		                objTaxWise.put("POSCode", posCode);
		                objTaxWise.put("BillDateTo",ob[4].toString());
		                objTaxWise.put("AccountCode", ob[5].toString());
		                arrObjTaxwise.add(objTaxWise);
		            }
	            }
	            jObj.put("Taxwise", arrObjTaxwise);

	            String sql_Discount = "select a.strPOSCode,sum(a.dblDiscountAmt),date(a.dteBillDate),b.strRoundOff,b.strTip,b.strDiscount "
	                    + "from tblbillhd a,tblposmaster b "
	                    + "where a.strPOSCode='" + posCode + "' "
	                    + " and a.strPOSCode=b.strPosCode "
	                    + "group by a.strPOSCode";

	            JSONArray arrObjDiscountwise = new JSONArray();
	            List listDiscount=objBaseServiceImpl.funGetList(new StringBuilder(sql_Discount), "sql");
	            if(listDiscount.size()>0)
	            {
	               for(int i=0;i<listDiscount.size();i++)
		           {
		        	   Object ob[]=(Object[])listDiscount.get(i);
		               if (Double.parseDouble(ob[1].toString()) > 0)
			                {
			                    JSONObject objDiscount = new JSONObject();
			                    debitAmt += Double.parseDouble(ob[1].toString());
			                    roundOffAccCode = ob[3].toString();
			                    objDiscount.put("RVCode", ob[0].toString() + "-Discount");
			                    objDiscount.put("RVName", "Discount");
			                    objDiscount.put("CRAmt", 0);
			                    objDiscount.put("DRAmt", Double.parseDouble(ob[1].toString()));
			                    objDiscount.put("ClientCode", ClientCode);
			                    objDiscount.put("BillDate", ob[2].toString());
			                    objDiscount.put("CMSPOSCode", gCMSPOSCode);
			                    objDiscount.put("POSCode", posCode);
			                    objDiscount.put("BillDateTo", ob[2].toString());
			                    objDiscount.put("AccountCode", ob[5].toString());
			                    arrObjDiscountwise.add(objDiscount);
			                }
			            }
	            }    
		         jObj.put("Discountwise", arrObjDiscountwise);

	            String sql_Settlement = "SELECT a.strPOSCode, IFNULL(b.strSettlementCode,''), IFNULL(c.strSettelmentDesc,''), IFNULL(SUM(b.dblSettlementAmt),0), DATE(a.dteBillDate),c.strAccountCode "
	                    + "FROM tblbillhd a,tblbillsettlementdtl b ,tblsettelmenthd c  "
	                    + "WHERE c.strSettelmentType='Member' "
	                    + "AND a.strPOSCode='" + posCode + "' "
	                    + "and a.strBillNo=b.strBillNo "
	                    + "and b.strSettlementCode=c.strSettelmentCode "
	                    + "GROUP BY a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc ";
	            JSONArray arrObjMemberSettlewise = new JSONArray();
	            List listCashSettlement=objBaseServiceImpl.funGetList(new StringBuilder(sql_Settlement), "sql");
	            if(listCashSettlement.size()>0)
	            {
	               for(int i=0;i<listCashSettlement.size();i++)
		           {
		        	   Object ob[]=(Object[])listCashSettlement.get(i);
		                JSONObject objSettlementWise = new JSONObject();
		                debitAmt +=Double.parseDouble(ob[3].toString());
		                
		                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
		                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
		                objSettlementWise.put("CRAmt", 0);
		                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
		                objSettlementWise.put("ClientCode", ClientCode);
		                objSettlementWise.put("BillDate", ob[4].toString());
		                objSettlementWise.put("CMSPOSCode", gCMSPOSCode);
		                objSettlementWise.put("POSCode", posCode);
		                objSettlementWise.put("BillDateTo",ob[4].toString());
		                objSettlementWise.put("AccountCode", ob[5].toString());
		                arrObjMemberSettlewise.add(objSettlementWise);
		                
		            }
	            }
	            jObj.put("MemberSettlewise", arrObjMemberSettlewise);

	            sql_Settlement = "SELECT a.strPOSCode, IFNULL(b.strSettlementCode,''), IFNULL(c.strSettelmentDesc,''), IFNULL(SUM(b.dblSettlementAmt),0), DATE(a.dteBillDate),c.strAccountCode "
	                    + "FROM tblbillhd a,tblbillsettlementdtl b ,tblsettelmenthd c  "
	                    + "WHERE c.strSettelmentType='Cash' "
	                    + "AND a.strPOSCode='" + posCode + "' "
	                    + "and a.strBillNo=b.strBillNo "
	                    + "and b.strSettlementCode=c.strSettelmentCode "
	                    + "GROUP BY a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
	            JSONArray arrObjCashSettlewise = new JSONArray();
	            List listMemberSettlement=objBaseServiceImpl.funGetList(new StringBuilder(sql_Settlement), "sql");
	            if(listMemberSettlement.size()>0)
	            {
	               for(int i=0;i<listMemberSettlement.size();i++)
		           {
		        	   Object ob[]=(Object[])listMemberSettlement.get(i);
		        
		                JSONObject objSettlementWise = new JSONObject();
		                debitAmt +=Double.parseDouble(ob[3].toString());
		                
		                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
		                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
		                objSettlementWise.put("CRAmt", 0);
		                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
		                objSettlementWise.put("ClientCode", ClientCode);
		                objSettlementWise.put("BillDate", ob[4].toString());
		                objSettlementWise.put("CMSPOSCode", gCMSPOSCode);
		                objSettlementWise.put("POSCode", posCode);
		                objSettlementWise.put("BillDateTo",ob[4].toString());
		                objSettlementWise.put("AccountCode", ob[5].toString());
		                arrObjCashSettlewise.add(objSettlementWise);
		            }
	            }
	            jObj.put("CashSettlewise", arrObjCashSettlewise);

	            String sql_MemberCL = "select left(a.strCustomerCode,8),d.strCustomerName,a.strBillNo,date(a.dteBillDate)"
	                    + ",b.dblSettlementAmt,c.strAccountCode "
	                    + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d "
	                    + "where a.strBillNo=b.strBillNo "
	                    + "and b.strSettlementCode=c.strSettelmentCode "
	                    + "and a.strCustomerCode=d.strCustomerCode "
	                    + "and a.strPOSCode='" + posCode + "'  "
	                    + "and c.strSettelmentType='Member'";
	            JSONArray arrObjMemberClData = new JSONArray();
	            List listMemeberCL=objBaseServiceImpl.funGetList(new StringBuilder(sql_MemberCL), "sql");
	            if(listMemeberCL.size()>0)
	            {
	               for(int i=0;i<listMemeberCL.size();i++)
		           {
		        	   Object ob[]=(Object[])listMemeberCL.get(i);
		       
		                JSONObject objMemeberCL = new JSONObject();
		                objMemeberCL.put("DebtorCode", ob[0].toString().trim());
		                objMemeberCL.put("DebtorName", ob[1].toString());
		                objMemeberCL.put("BillNo", ob[2].toString());
		                objMemeberCL.put("BillDate", ob[3].toString());
		                objMemeberCL.put("BillAmt", ob[4].toString());
		                objMemeberCL.put("ClientCode", ClientCode);
		                objMemeberCL.put("CMSPOSCode", gCMSPOSCode);
		                objMemeberCL.put("POSCode", posCode);
		                objMemeberCL.put("POSName", POSName);//posname
		                objMemeberCL.put("BillDateTo", ob[3].toString());
		                objMemeberCL.put("AccountCode", ob[5].toString());
		                arrObjMemberClData.add(objMemeberCL);
		            }
	            }
	            
	            jObj.put("MemberCLData", arrObjMemberClData);

	            String posDate = billDate;
	            roundOffAccCode = "";
	            String sql = "select strRoundOff from tblposmaster where strPOSCode='" + posCode + "' ";
	            List listRF=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");

	            if(listRF.size()>0)
	            {
	            	
	            	roundOffAccCode = ((Object)listRF.get(0)).toString();
	            }
	            
	            JSONArray arrObjRoundOff = new JSONArray();
	            JSONObject objRoundOff = new JSONObject();
	            objRoundOff.put("RVCode", posCode + "-Roff");
	            objRoundOff.put("RVName", POSName + "-Roff");
	            roundOff = debitAmt - creditAmt;
	            if (roundOff < 0)
	            {
	                roundOff = roundOff * (-1);
	                objRoundOff.put("DRAmt", roundOff);
	                objRoundOff.put("CRAmt", 0);
	            }
	            else
	            {
	                objRoundOff.put("DRAmt", 0);
	                objRoundOff.put("CRAmt", roundOff);
	            }
	            objRoundOff.put("ClientCode", ClientCode);
	            objRoundOff.put("BillDate", posDate);
	            objRoundOff.put("CMSPOSCode", gCMSPOSCode);
	            objRoundOff.put("POSCode", posCode);
	            objRoundOff.put("BillDateTo", posDate);
	            objRoundOff.put("AccountCode", roundOffAccCode);
	            arrObjRoundOff.add(objRoundOff);

	            jObj.put("RoundOffDtl", arrObjRoundOff);

	            String gWebBooksWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(ClientCode,posCode, "gWebBooksWebServiceURL");
				
	            String cmsURL = gWebBooksWebServiceURL + "/funPostRevenueToCMS";
	            URL url = new URL(cmsURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            os.write(jObj.toString().getBytes());
	            os.flush();

	            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	            {
	                throw new RuntimeException("Failed : HTTP error code : "
	                        + conn.getResponseCode());
	            }
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "", op = "";
	            System.out.println("Output from Server .... \n");
	            while ((output = br.readLine()) != null)
	            {
	                op += output;
	            }
	            System.out.println(op);
	            conn.disconnect();
	            if (op.equals("false"))
	            {
	                res = 0;
	            }
	            else
	            {
	                res = 1;
	            }
	        }
	        catch (Exception e)
	        {
	            res = 0;
	            e.printStackTrace();
	          //  JOptionPane.showMessageDialog(null, "Check CMS Web Service URL and Internet Connection!!!"); // there is this at null postion
	        }
	        finally
	        {
	            return res;
	        }
	    }
	    
	  // Function to send bill data to others cms.
	  public int funPostBillDataToCMS(String posCode, String billDate,String ClientCode,String userCode) throws Exception
	   {
	       int res = 0;
	       double roundOff = 0, creditAmt = 0, debitAmt = 0;
	       try
	       {
	       	String POSName="";
	       	String sqlpos="select strPosName from tblposmaster where strPOSCode='"+posCode+"'";
	       	   List listPOS=objBaseServiceImpl.funGetList(new StringBuilder(sqlpos), "sql");
	           if(listPOS.size()>0)
	           {
	           	POSName=((Object)listPOS.get(0)).toString();
	           }
	           
	           String CMSPOSCode = objPOSSetupUtility.funGetParameterValuePOSWise(ClientCode,posCode, "gCMSPOSCode");
			   JSONObject jObj = new JSONObject();
	           JSONArray arrObj = new JSONArray();

	           String sql_SubGroupWise = "select a.strPOSCode,ifnull(d.strSubGroupCode,'NA'),ifnull(d.strSubGroupName,'NA')"
	                   + ",sum(b.dblAmount),date(a.dteBillDate) "
	                   + "from tblbillhd a left outer join tblbilldtl b on a.strBillNo=b.strBillNo "
	                   + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
	                   + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
	                   + "where a.strPOSCode='" + posCode + "' "
	                   + "group by d.strSubGroupCode,d.strSubGroupName";
	           //System.out.println(sql_SubGroupWise);
	           List listSBGroup=objBaseServiceImpl.funGetList(new StringBuilder(sql_SubGroupWise), "sql");
	           if(listSBGroup.size()>0)
	           {
	           	
	           for(int i=0;i<listSBGroup.size();i++)
	           {
	        	   	Object ob[]=(Object[])listSBGroup.get(i);
	        	   JSONObject objSubGroupWise = new JSONObject();
	                objSubGroupWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSubGroupWise.put("RVName", POSName + "-" + ob[2].toString());
	                objSubGroupWise.put("CRAmt", ob[3].toString());//double
	                objSubGroupWise.put("DRAmt", 0);
	                objSubGroupWise.put("ClientCode", ClientCode);
	                objSubGroupWise.put("BillDate", ob[4].toString());
	                objSubGroupWise.put("CMSPOSCode", CMSPOSCode);
	                objSubGroupWise.put("POSCode", posCode);
	                objSubGroupWise.put("BillDateTo", ob[4].toString());
	                arrObj.add(objSubGroupWise);
	            }
	           }

	           String sql_TaxWise = "select a.strPOSCode,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount),date(a.dteBillDate) "
	                   + "from tblbillhd a left outer join tblbilltaxdtl b on a.strBillNo=b.strBillNo "
	                   + "left outer join tbltaxhd c on b.strTaxCode=c.strTaxCode "
	                   + "where a.strPOSCode='" + posCode + "' "
	                   + "group by c.strTaxCode";
	           //System.out.println(sql_TaxWise);

	           List listTaxWise=objBaseServiceImpl.funGetList(new StringBuilder(sql_TaxWise), "sql");
	           if(listTaxWise.size()>0)
	           {
	           	
	           for(int i=0;i<listTaxWise.size();i++)
	           {
	        	   Object ob[]=(Object[])listTaxWise.get(i);
	               
	               JSONObject objTaxWise = new JSONObject();
	               creditAmt += Double.parseDouble(ob[3].toString());
	                objTaxWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objTaxWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objTaxWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
	                objTaxWise.put("DRAmt", 0);
	                objTaxWise.put("ClientCode", ClientCode);
	                objTaxWise.put("BillDate", ob[4].toString());
	                objTaxWise.put("CMSPOSCode", CMSPOSCode);
	                objTaxWise.put("POSCode", posCode);
	                objTaxWise.put("BillDateTo",ob[4].toString());
	             
	                arrObj.add(objTaxWise);
	            }
	           }
	          
	           String sql_Discount = "select strPOSCode,sum(dblDiscountAmt),date(dteBillDate) "
	                   + "from tblbillhd "
	                   + "where strPOSCode='" + posCode + "' "
	                   + "group by strPOSCode";
	           
	           List listDiscount=objBaseServiceImpl.funGetList(new StringBuilder(sql_Discount), "sql");
	           if(listDiscount.size()>0)
	           {
	              for(int i=0;i<listDiscount.size();i++)
	           {
	        	   Object ob[]=(Object[])listDiscount.get(i);
	               if (Double.parseDouble(ob[1].toString()) > 0)
		                {
		                    JSONObject objDiscount = new JSONObject();
		                    objDiscount.put("RVCode", ob[0].toString() + "-Discount");
		                    objDiscount.put("RVName", "Discount");
		                    objDiscount.put("CRAmt", 0);
		                    objDiscount.put("DRAmt", Double.parseDouble(ob[1].toString()));
		                    objDiscount.put("ClientCode", ClientCode);
		                    objDiscount.put("BillDate", ob[2].toString());
		                    objDiscount.put("CMSPOSCode",CMSPOSCode);
		                    objDiscount.put("POSCode", posCode);
		                    objDiscount.put("BillDateTo", ob[2].toString());
		                    arrObj.add(objDiscount);
		                }
		            }
	           }    
	          
	           /*
	            String sql_RoundOff="SELECT strPOSCode,sum((dbltaxamt + dblsubtotal) - dblgrandtotal)"
	            + ",date(dteBillDate) "
	            + "from tblbillhd where strPOSCode= '"+clsGlobalVarClass.gPOSCode+"'";
	            ResultSet rsRoundOff=clsGlobalVarClass.dbMysql.executeResultSet(sql_RoundOff);
	            while(rsRoundOff.next())
	            {
	            JSONObject objRoundOff=new JSONObject();
	            objRoundOff.put("RVCode",rsRoundOff.getString(1)+"-Roff");
	            objRoundOff.put("RVName",clsGlobalVarClass.gPOSName+"-Roff");
	            objRoundOff.put("CRAmt",0);
	            objRoundOff.put("DRAmt",rsRoundOff.getDouble(2));
	            objRoundOff.put("ClientCode",clsGlobalVarClass.gClientCode);
	            objRoundOff.put("BillDate",rsRoundOff.getString(3));
	            objRoundOff.put("CMSPOSCode",clsGlobalVarClass.gCMSPOSCode);
	            objRoundOff.put("POSCode",clsGlobalVarClass.gPOSCode);
	            objRoundOff.put("BillDateTo",rsRoundOff.getString(3));
	            arrObj.add(objRoundOff);
	            }
	            rsRoundOff.close();*/
	           String sql_Settlement = "select a.strPOSCode,ifnull(b.strSettlementCode,'')"
	                   + " ,ifnull(c.strSettelmentDesc,''),ifnull(sum(b.dblSettlementAmt),0),date(a.dteBillDate) "
	                   + " from tblbillhd a left outer join tblbillsettlementdtl b on a.strBillNo=b.strBillNo "
	                   + " left outer join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode "
	                   + " where c.strSettelmentType='Member' and a.strPOSCode='" + posCode + "' "
	                   + " group by a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
	           //System.out.println(sql_Settlement);

	           List listCashSettlement=objBaseServiceImpl.funGetList(new StringBuilder(sql_Settlement), "sql");
	           if(listCashSettlement.size()>0)
	           {
	              for(int i=0;i<listCashSettlement.size();i++)
	           {
	        	   Object ob[]=(Object[])listCashSettlement.get(i);
	                JSONObject objSettlementWise = new JSONObject();
	                
	                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objSettlementWise.put("CRAmt", 0);
	                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
	                objSettlementWise.put("ClientCode", ClientCode);
	                objSettlementWise.put("BillDate", ob[4].toString());
	                objSettlementWise.put("CMSPOSCode", CMSPOSCode);
	                objSettlementWise.put("POSCode", posCode);
	                objSettlementWise.put("BillDateTo",ob[4].toString());
	               
	                arrObj.add(objSettlementWise);
	                
	            }
	           }
	          
	           sql_Settlement = "select a.strPOSCode,ifnull(b.strSettlementCode,'')"
	                   + " ,ifnull(c.strSettelmentDesc,''),ifnull(sum(b.dblSettlementAmt),0),date(a.dteBillDate) "
	                   + " from tblbillhd a left outer join tblbillsettlementdtl b on a.strBillNo=b.strBillNo "
	                   + " left outer join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode "
	                   + " where c.strSettelmentType='Cash' and a.strPOSCode='" + posCode + "' "
	                   + " group by a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
	           //System.out.println(sql_Settlement);
	           
	           listCashSettlement=objBaseServiceImpl.funGetList(new StringBuilder(sql_Settlement), "sql");
	           if(listCashSettlement.size()>0)
	           {
	              for(int i=0;i<listCashSettlement.size();i++)
	           {
	        	   Object ob[]=(Object[])listCashSettlement.get(i);
	                JSONObject objSettlementWise = new JSONObject();
	                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objSettlementWise.put("CRAmt", 0);
	                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
	                objSettlementWise.put("ClientCode", ClientCode);
	                objSettlementWise.put("BillDate", ob[4].toString());
	                objSettlementWise.put("CMSPOSCode", CMSPOSCode);
	                objSettlementWise.put("POSCode", posCode);
	                objSettlementWise.put("BillDateTo",ob[4].toString());
	               
	                arrObj.add(objSettlementWise);
	           }
	           }
	         
	           String posDate = billDate;
	           JSONObject objRoundOff = new JSONObject();
	           objRoundOff.put("RVCode", posCode + "-Roff");
	           objRoundOff.put("RVName", POSName + "-Roff");
	           roundOff = debitAmt - creditAmt;
	           if (roundOff < 0)
	           {
	               roundOff = roundOff * (-1);
	               objRoundOff.put("DRAmt", roundOff);
	               objRoundOff.put("CRAmt", 0);
	           }
	           else
	           {
	               objRoundOff.put("DRAmt", 0);
	               objRoundOff.put("CRAmt", roundOff);
	           }
	           objRoundOff.put("ClientCode", ClientCode);
	           objRoundOff.put("BillDate", posDate);
	           objRoundOff.put("CMSPOSCode", CMSPOSCode);
	           objRoundOff.put("POSCode", posCode);
	           objRoundOff.put("BillDateTo", posDate);
	           arrObj.add(objRoundOff);

	           jObj.put("BillInfo", arrObj);
	           //System.out.println(jObj);
	           String gCMSWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(ClientCode,posCode, "gWebBooksWebServiceURL");
			   String cmsURL = gCMSWebServiceURL + "/funPostRVDataToCMS";
	           //System.out.println(cmsURL);
	           URL url = new URL(cmsURL);
	           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	           conn.setDoOutput(true);
	           conn.setRequestMethod("POST");
	           conn.setRequestProperty("Content-Type", "application/json");
	           OutputStream os = conn.getOutputStream();
	           os.write(jObj.toString().getBytes());
	           os.flush();

	           if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	           {
	               throw new RuntimeException("Failed : HTTP error code : "
	                       + conn.getResponseCode());
	           }
	           BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	           String output = "", op = "";
	           System.out.println("Output from Server .... \n");
	           while ((output = br.readLine()) != null)
	           {
	               op += output;
	           }
	           System.out.println(op);
	           conn.disconnect();
	           if (op.equals("false"))
	           {
	               res = 0;
	           }
	           else
	           {
	               JSONObject jObjCL = new JSONObject();
	               JSONArray arrObjCL = new JSONArray();
	               /*String sql_MemberCL="select strCustomerCode,'',strBillNo,date(dteBillDate),dblGrandTotal "
	                + "from tblbillhd "
	                + "where strPOSCode='"+clsGlobalVarClass.gPOSCode+"' "
	                + "and strSettelmentMode='Member'";*/
	               String sql_MemberCL = "select left(a.strCustomerCode,8),d.strCustomerName,a.strBillNo,date(a.dteBillDate),b.dblSettlementAmt "
	                       + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d "
	                       + "where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
	                       + "and a.strCustomerCode=d.strCustomerCode "
	                       + "and a.strPOSCode='" + posCode + "' "
	                       + "and c.strSettelmentType='Member'";
	               //System.out.println(sql_MemberCL);
	               List listMemeberCL=objBaseServiceImpl.funGetList(new StringBuilder(sql_MemberCL), "sql");
	               if(listMemeberCL.size()>0)
	               {
	                  for(int i=0;i<listMemeberCL.size();i++)
	   	           {
	   	        	   Object ob[]=(Object[])listMemeberCL.get(i);
	   	       
	                    JSONObject objMemeberCL = new JSONObject();
	                    objMemeberCL.put("DebtorCode", ob[0].toString().trim());
	                    objMemeberCL.put("DebtorName", ob[1].toString());
	                    objMemeberCL.put("BillNo", ob[2].toString());
	                    objMemeberCL.put("BillDate",ob[3].toString());
	                    objMemeberCL.put("BillAmt", ob[4].toString());
	                    objMemeberCL.put("ClientCode", ClientCode);
	                    objMemeberCL.put("CMSPOSCode", CMSPOSCode);
	                    objMemeberCL.put("POSCode", posCode);
	                    objMemeberCL.put("POSName", POSName);
	                    objMemeberCL.put("BillDateTo", ob[3].toString());
	                    arrObjCL.add(objMemeberCL);
	   	           }
	               }
	               

	               jObjCL.put("MemberCLInfo", arrObjCL);
	               //System.out.println(jObjCL);
	               
	               String cmsURLCL = gCMSWebServiceURL + "/funPostCLDataToCMS";
	               //System.out.println(cmsURLCL);
	               URL urlCL = new URL(cmsURLCL);
	               HttpURLConnection connCL = (HttpURLConnection) urlCL.openConnection();
	               connCL.setDoOutput(true);
	               connCL.setRequestMethod("POST");
	               connCL.setRequestProperty("Content-Type", "application/json");
	               OutputStream osCL = connCL.getOutputStream();
	               osCL.write(jObjCL.toString().getBytes());
	               osCL.flush();

	               if (connCL.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	               {
	                   throw new RuntimeException("Failed : HTTP error code : "
	                           + connCL.getResponseCode());
	               }
	               BufferedReader brCL = new BufferedReader(new InputStreamReader((connCL.getInputStream())));
	               String output1 = "", op1 = "";
	               System.out.println("Output from Server .... \n");
	               while ((output1 = brCL.readLine()) != null)
	               {
	                   op1 += output1;
	               }
	               connCL.disconnect();
	               System.out.println(op1);
	               if (op1.equals("false"))
	               {
	                   res = 0;
	               }
	               else
	               {
	                   res = 1;
	               }
	           }
	       }
	       catch (Exception e)
	       {
	           res = 0;
	           e.printStackTrace();
	         //  JOptionPane.showMessageDialog(null, "Check CMS Web Service URL and Internet Connection!!!"); // there is this at null postion
	       }
	       finally
	       {
	           return res;
	       }
	   }


      
 public String funGenerateNextCode()
  {
      String code = "", transId = "";
      try
      {
          int cn = 0;
          List list=objBaseServiceImpl.funGetList(new StringBuilder("select count(*) from tblcashmanagement"), "sql");
          		if(list.size()>0)
          		{
          			cn=Integer.parseInt(((Object)list.get(0)).toString());
          		}
          if (cn > 0)
          {
        	  list=objBaseServiceImpl.funGetList(new StringBuilder("select max(strTransID) from tblcashmanagement"),"sql");
              if(list.size()>0)
				{
					code=((Object)list.get(0)).toString();		
				}
          	
              int length = code.length();
              String nextCode = code.substring(2, length);
              int nextCount = Integer.parseInt(nextCode);
              nextCount++;
              transId = "TR" + String.format("%05d", nextCount);
          }
          else
          {
              transId = "TR00001";
          }
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
      return transId;
  }

	    
	    
 // Function to calculate total settlement amount and assigns global variables, which are shown on day end/shift end form.
 // This function calculate settlement amount from live tables.    
 public int funCalculateDayEndCash(String posDate, int shiftCode, String posCode)
 {
     double sales = 0.00, totalDiscount = 0.00, totalSales = 0.00, noOfDiscountedBills = 0.00;
     double advCash = 0.00, cashIn = 0.00, cashOut = 0.00, totalFloat = 0.00;
     try
     {
         String sql = "SELECT c.strSettelmentDesc,sum(ifnull(b.dblSettlementAmt,0)),sum(a.dblDiscountAmt),c.strSettelmentType"
                 + " FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                 + " Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
                 + " and date(a.dteBillDate ) ='" + posDate + "' and a.strPOSCode='" + posCode + "'"
                 + " and a.intShiftCode=" + shiftCode
                 + " GROUP BY c.strSettelmentDesc,a.strPosCode";
         //System.out.println(sql);
         List listSettlementAmt=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
         if(listSettlementAmt.size()>0)
         {
         	for(int i=0;i<listSettlementAmt.size();i++)
         	{
         		Object ob[]=(Object[])listSettlementAmt.get(i);
         		 if (ob[3].toString().equalsIgnoreCase("Cash"))
                  {
                      sales = sales + (Double.parseDouble(ob[1].toString().toString()));
                  }
                  totalDiscount = totalDiscount + (Double.parseDouble(ob[2].toString().toString()));
                  totalSales = totalSales + (Double.parseDouble(ob[1].toString().toString()));
         	}
         }
         
         gTotalDiscounts = totalDiscount;
         gTotalCashSales = totalSales;
        

         sql = "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
                 + "Where date(dteBillDate ) ='" + posDate + "' and strPOSCode='" + posCode + "' "
                 + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftCode
                 + " GROUP BY strPosCode";
         
         List listTotalDiscountBills=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
         if(listTotalDiscountBills.size()>0)
         {
         	Object ob[]=(Object[])listTotalDiscountBills.get(0);
             gNoOfDiscountedBills = Integer.parseInt(ob[0].toString());            	
         }
         
         sql = "select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + posDate + "' and "
                 + "strPOSCode='" + posCode + "' and intShiftCode=" + shiftCode + " "
                 + " GROUP BY strPosCode";
         
         List listTotalBills=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
         if(listTotalBills.size()>0)
         {
         //	Object ob[]=(Object[])listTotalBills.get(0);
         	gTotalBills = Integer.parseInt(((Object)listTotalBills.get(0)).toString());            	

         }
         gTotalCashSales = sales;
         
         sql = "select count(dblAdvDeposite) from tbladvancereceipthd "
                 + "where dtReceiptDate='" + posDate + "' and intShiftCode=" + shiftCode;
         
         List listTotalAdvance=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
         int cntAdvDeposite=0;
         if(listTotalAdvance.size()>0)
         {
         	
         	cntAdvDeposite = Integer.parseInt(((Object)listTotalAdvance.get(0)).toString()); 	
         }
        
         if (cntAdvDeposite > 0)
         {
             //sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
             sql = "select sum(b.dblAdvDepositesettleAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                     + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                     + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                     + "and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftCode;

             listTotalAdvance=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
            if(listTotalAdvance.size()>0)
	          {
	          	    advCash = Double.parseDouble(((Object)listTotalAdvance.get(0)).toString());
	                gTotalAdvanceAmt = advCash;
	          }

         }
         

         //sql="select strTransType,sum(dblAmount) from tblcashmanagement where dteTransDate='"+posDate+"'"
         //    + " and strPOSCode='"+globalVarClass.gPOSCode+"' group by strTransType";
         sql = "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
                 + "where date(dteTransDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                 + "and intShiftCode=" + shiftCode + " group by strTransType,strCurrencyType";
        
         List listCashTransaction=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
         
         if(listCashTransaction.size()>0)
         {
          for(int i=0;i<listCashTransaction.size();i++)
          {
          	Object ob[]=(Object[])listCashTransaction.get(i);
              if (ob[0].toString().equals("Float"))
              {
                  cashIn = cashIn + (Double.parseDouble(ob[1].toString()));
              }
              if (ob[0].toString().equals("Transfer In"))
              {
                  cashIn = cashIn + (Double.parseDouble(ob[1].toString()));
              }

              if (ob[0].toString().equals("Withdrawal"))
              {
                  cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
              }
              if (ob[0].toString().equals("Transfer Out"))
              {
                  cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
              }
              if (ob[0].toString().equals("Payments"))
              {
                  cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
              }
              if (ob[0].toString().equals("Refund"))
              {
                  cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
              }
          }
         }
         cashIn = cashIn + advCash + sales;
         gTotalReceipt = cashIn;
         gTotalPayments = cashOut;
         double inHandCash = (cashIn) - cashOut;
         gTotalCashInHand = inHandCash;
     }
     catch (Exception e)
     {
         e.printStackTrace();
     }
     return 1;
 }
  
 // Function to update values in tbldayendprocess table.
  // This function updates values from Live tables.    
  public int funUpdateDayEndFields(String posDate, int shiftNo, String dayEnd, String posCode,String userCode)
  {
      try
      {
          String sql = "update tbldayendprocess set dblTotalSale = IFNULL((select sum(b.dblSettlementAmt) "
                  + "TotalSale from tblbillhd a,tblbillsettlementdtl b "
                  + "where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                  + "a.strPOSCode = '" + posCode + "' and a.intShiftCode=" + shiftNo + "),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_1=="+sql);
          //Query 
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          
        
          sql = "update tbldayendprocess set dteDayEndDateTime='" + getCurrentDateTime() + "'"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                  + "and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_2=="+sql);
          
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          
          sql = "update tbldayendprocess set strUserEdited='" + userCode + "'"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_3=="+sql);
          
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");

          sql = "update tbldayendprocess set dblNoOfBill = IFNULL((select count(*) NoOfBills "
                  + "from tblbillhd where Date(dteBillDate) = '" + posDate + "' and "
                  + "strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + "),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "' "
                  + " and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_4=="+sql);
          
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");

          sql = "update tbldayendprocess set dblNoOfVoidedBill = IFNULL((select count(DISTINCT strBillNo) "
                  + "NoOfVoidBills from tblvoidbillhd where date(dteModifyVoidBill) = " + "'" + posDate + "'"
                  + " and strPOSCode = '" + posCode + "' and strTransType = 'VB'"
                  + " and intShiftCode=" + shiftNo + "),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_5=="+sql);
          
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");

          sql = "update tbldayendprocess set dblNoOfModifyBill = IFNULL((select count(DISTINCT b.strBillNo) "
                  + "NoOfModifiedBills from tblbillhd a,tblvoidbillhd b where a.strBillNo=b.strBillNo"
                  + " and Date(b.dteModifyVoidBill) = '" + posDate + "' and b.strPOSCode='" + posCode + "'"
                  + " and b.strTransType = 'MB' and a.intShiftCode=" + shiftNo + "),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_6=="+sql);
          
         objBaseServiceImpl.funExecuteUpdate(sql,"sql");

          sql = "update tbldayendprocess set dblHDAmt=IFNULL((select sum(a.dblGrandTotal) HD from tblbillhd a,"
                  + "tblhomedelivery b where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                  + "a.strPOSCode = '" + posCode + "' and a.intShiftCode=" + shiftNo + "), 0) "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_7=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblDiningAmt=IFNULL(( select sum(dblGrandTotal) Dining"
                  + " from tblbillhd where strTakeAway='No' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + "  and strBillNo NOT IN (select strBillNo from tblhomedelivery where strBillNo is not NULL) and intShiftCode=" + shiftNo + "),0)"
                  + "  where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "' "
                  + "and intShiftCode=" + shiftNo;
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");             //System.out.println("UpdateDayEndQuery_8==" + sql);

          sql = "update tbldayendprocess set dblTakeAway=IFNULL((select sum(dblGrandTotal) TakeAway from tblbillhd"
                  + " where strTakeAway='Yes' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo + "),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;

          //System.out.println("UpdateDayEndQuery_9=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblFloat=IFNULL((select sum(dblAmount) TotalFloats from tblcashmanagement "
                  + "where strTransType='Float' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo + ""
                  + " group by strTransType),0) "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_10=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblTransferIn=IFNULL((select sum(dblAmount) TotalTransferIn from tblcashmanagement "
                  + "where strTransType='Transfer In' and date(dteTransDate) = '" + posDate + "'"
                  + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo
                  + " group by strTransType),0) "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_11=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblTransferOut=IFNULL((select sum(dblAmount) TotalTransferOut from tblcashmanagement "
                  + "where strTransType='Transfer Out' and date(dteTransDate) = '" + posDate + "'"
                  + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                  + " group by strTransType),0) "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_12=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblWithdrawal=IFNULL(( select sum(dblAmount) TotalWithdrawals from tblcashmanagement "
                  + "where strTransType='Withdrawal' and date(dteTransDate) = '" + posDate + "' "
                  + "and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                  + " group by strTransType),0) "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_13=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblRefund=IFNULL(( select sum(dblAmount) TotalRefunds from tblcashmanagement "
                  + " where strTransType='Refund' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                  + " and intShiftCode=" + shiftNo + " group by strTransType),0)"
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_14=="+sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblPayments=IFNULL(( select sum(dblAmount) TotalPayments from tblcashmanagement "
                  + "where strTransType='Payments' and date(dteTransDate) = '" + posDate + "'"
                  + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                  + " group by strTransType),0) "
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_15=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblAdvance=IFNULL((select sum(b.dblAdvDepositesettleAmt) "
                  + "from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                  + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                  + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                  + "and c.strSettelmentType='Cash' and intShiftCode=" + shiftNo + "),0)"
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_16=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblTotalReceipt=" + gTotalReceipt
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_17=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblTotalPay=" + gTotalPayments
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_18=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblCashInHand=" + gTotalCashInHand
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_19=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblCash=" + gTotalCashSales
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println(sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblTotalDiscount=" + gTotalDiscounts
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_21=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set dblNoOfDiscountedBill=" + gNoOfDiscountedBills
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_22=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set intTotalPax=IFNULL((select sum(intPaxNo)"
                  + " from tblbillhd where date(dteBillDate ) ='" + posDate + "' and intShiftCode=" + shiftNo + ""
                  + " and strPOSCode='" + posCode + "'),0)"
                  + " where date(dtePOSDate)='" + posDate + "' "
                  + "and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("UpdateDayEndQuery_23=="+sql);

          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
          sql = "update tbldayendprocess set intNoOfTakeAway=(select count(strTakeAway)"
                  + "from tblbillhd where date(dteBillDate )='" + posDate + "' and intShiftCode=" + shiftNo + ""
                  + " and strPOSCode='" + posCode + "' and strTakeAway='Yes')"
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("update int takeawy==" + sql);
          
          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
                       sql = "update tbldayendprocess set intNoOfHomeDelivery=(select COUNT(strBillNo)from tblhomedelivery where date(dteDate)='" + posDate + "' and strPOSCode='" + posCode + "' )"
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("update int homedelivry:==" + sql);

          objBaseServiceImpl.funExecuteUpdate(sql,"sql");
                       
          // Update Day End Table with Used Card Balance    
          double debitCardAmtUsed = 0;
          sql = "select sum(b.dblSettlementAmt) "
                  + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                  + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                  + " and date(a.dteBillDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                  + " and c.strSettelmentType='Debit Card' "
                  + " group by a.strPOSCode,date(a.dteBillDate),c.strSettelmentType;";
           List listUsedDCAmt=objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
          	if(listUsedDCAmt.size()>0)
          	{
          		debitCardAmtUsed = Double.parseDouble(((Object)listUsedDCAmt.get(0)).toString());	
          	}
          
          	sql = "update tbldayendprocess set dblUsedDebitCardBalance=" + debitCardAmtUsed + " "
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                  + " and intShiftCode=" + shiftNo;
          	
            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
       
          // Update Day End Table with UnUsed Card Balance    
          double debitCardAmtUnUsed = 0;
          sql = "select sum(dblCardAmt) from tbldebitcardrevenue "
                  + " where strPOSCode='" + posCode + "' and date(dtePOSDate)='" + posDate + "' "
                  + " group by strPOSCode,date(dtePOSDate);";
       
          List listUnUsedDCAmt=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
          	if(listUnUsedDCAmt.size()>0)
          	{
          		debitCardAmtUnUsed = Double.parseDouble(((Object)listUnUsedDCAmt.get(0)).toString());	
          	}
       
          sql = "update tbldayendprocess set dblUnusedDebitCardBalance=" + debitCardAmtUnUsed + " "
                  + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                  + " and intShiftCode=" + shiftNo;
       	
        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
   
          sql = "UPDATE tbldayendprocess SET dblTipAmt= IFNULL(( "
                  + "SELECT SUM(dblTipAmount) "
                  + "FROM tblbillhd "
                  + "WHERE DATE(dteBillDate) ='" + posDate + "' AND intShiftCode='" + shiftNo + "' AND strPOSCode='" + posCode + "'),0) "
                  + "WHERE DATE(dtePOSDate)='" + posDate + "' AND strPOSCode='" + posCode + "' AND intShiftCode='" + shiftNo + "' ";
       	
        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
   

          //update no. of complementary bills
          sql = "update tbldayendprocess set intNoOfComplimentaryKOT=(select COUNT(distinct(a.strBillNo))"
                  + "from  tblbillhd a,tblbillcomplementrydtl b "
                  + "where a.strBillNo=b.strBillNo "
                  + "and date(b.dteBillDate)='" + posDate + "' and a.strPOSCode='" + posCode + "') "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("intNoOfComplimentaryKOT:==" + sql);
       	
        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
   

          //update no. of void KOTs
          sql = "update tbldayendprocess set intNoOfVoidKOT=(select count(distinct(a.strKOTNo)) "
                  + "from tblvoidkot a "
                  + "where a.strPOSCode='" + posCode + "' "
                  + "and date(a.dteVoidedDate)='" + posDate + "') "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("intNoOfVoidKOT:==" + sql);
       	
        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
   
          //update no. of NC KOTs
          sql = "update tbldayendprocess set intNoOfNCKOT=(select count(distinct(a.strKOTNo)) "
                  + "from tblnonchargablekot a "
                  + "where a.strPOSCode='" + posCode + "' "
                  + "and date(a.dteNCKOTDate)='" + posDate + "') "
                  + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
          //System.out.println("intNoOfNCKOT:==" + sql);
       	objBaseServiceImpl.funExecuteUpdate(sql,"sql");
   
       	sql = "UPDATE tbldayendprocess SET dblNetSale = IFNULL((SELECT SUM(a.dblSubTotal)-SUM(a.dblDiscountAmt) as NetTotal "
    		    + " FROM tblbillhd a "
    		    + " WHERE DATE(a.dteBillDate) = '" + posDate + "' "
    		    + " AND  a.strPOSCode = '" + posCode + "' AND a.intShiftCode='" + shiftNo + "'),0) "
    		    + " WHERE DATE(dtePOSDate)='" + posDate + "' AND strPOSCode = '" + posCode + "' AND intShiftCode=" + shiftNo;
    	objBaseServiceImpl.funExecuteUpdate(sql,"sql");

    	    sql = "update tbldayendprocess set dblGrossSale = IFNULL((select sum(b.dblSettlementAmt) "
    		    + "TotalSale from tblbillhd a,tblbillsettlementdtl b "
    		    + "where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
    		    + "a.strPOSCode = '" + posCode + "' and a.intShiftCode=" + shiftNo + "),0)"
    		    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
    		    + " and intShiftCode=" + shiftNo;
    	    //System.out.println("UpdateDayEndQuery_1=="+sql);
    		objBaseServiceImpl.funExecuteUpdate(sql,"sql");

    	    sql = "UPDATE tbldayendprocess SET dblAPC = IFNULL(( "
    		    + " SELECT SUM(a.dblGrandTotal)/SUM(a.intPaxNo) as APC"
    		    + " FROM tblbillhd a "
    		    + " WHERE  DATE(a.dteBillDate) = '" + posDate + "' "
    		    + " AND  a.strPOSCode = '" + posCode + "' AND a.intShiftCode='" + shiftNo + "'),0) "
    		    + " WHERE DATE(dtePOSDate)='" + posDate + "' AND strPOSCode = '" + posCode + "' AND intShiftCode=" + shiftNo;
    		objBaseServiceImpl.funExecuteUpdate(sql,"sql");
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
      return 1;
  }

  public boolean funInsertQBillData(String posCode,String clientCode)
	 {
	    boolean flgResult = false;
	
	    try
	    {
	        String sqlAdvRecDtl = "delete from tblqadvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvRecDtl,"sql");
	       
	        sqlAdvRecDtl = "insert into tblqadvancereceiptdtl "
	                + "(select * from tbladvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')))";
	       objBaseServiceImpl.funExecuteUpdate(sqlAdvRecDtl,"sql");
	        
	        sqlAdvRecDtl = "delete from tbladvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	       
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvRecDtl,"sql");
	        //System.out.println("Adv Rec Dtl");
	
	        String sqlAdvRecHd = "delete from tblqadvancereceipthd where strReceiptNo in "
	                + " (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        //System.out.println(sqlAdvRecHd);
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvRecHd,"sql");
	       
	        
	        sqlAdvRecHd = "insert into tblqadvancereceipthd "
	                + "(select * from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        //System.out.println(sqlAdvRecHd);
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvRecHd,"sql");
	        
	        sqlAdvRecHd = "delete from tbladvancereceipthd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        //System.out.println(sqlAdvRecHd);
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvRecHd,"sql");
	        //System.out.println("Adv Rec Hd");
	
	        String sqlAdvBookDtl = "delete from tblqadvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookDtl,"sql");
	    
	        sqlAdvBookDtl = "insert into tblqadvbookbilldtl "
	                + " (select * from tbladvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookDtl,"sql");
	        
	        sqlAdvBookDtl = "delete from tbladvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookDtl,"sql");
	
	        String sqlAdvBookCharDtl = "delete from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookCharDtl,"sql");
	        
	        sqlAdvBookCharDtl = "insert into tblqadvbookbillchardtl "
	                + " (select * from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookCharDtl,"sql");
	        
	        sqlAdvBookCharDtl = "delete from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookCharDtl,"sql");
	        
	        //System.out.println("Adv Char Dtl");
	
	        String sqlAdvBookModDtl = "delete from tblqadvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookModDtl,"sql");
	
	        sqlAdvBookModDtl = "insert into tblqadvordermodifierdtl "
	                + " (select * from tbladvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookModDtl,"sql");
	
	        
	        sqlAdvBookModDtl = "delete from tbladvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookModDtl,"sql");
	
	        //System.out.println("Adv Mod Dtl");
	
	        String sqlAdvBookHd = "delete from tblqadvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookHd,"sql");
	     
	        sqlAdvBookHd = "insert into tblqadvbookbillhd "
	                + " (select * from tbladvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	     
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookHd,"sql");
	
	        sqlAdvBookHd = "delete from tbladvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        //System.out.println("Adv Hd");
	        objBaseServiceImpl.funExecuteUpdate(sqlAdvBookHd,"sql");
	
	        String qSqlBillDtl = "delete from tblqbilldtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDtl,"sql");
	
	        qSqlBillDtl = "insert into tblqbilldtl (select * from tblbilldtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDtl,"sql");
	
	        qSqlBillDtl = "delete from tblbilldtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDtl,"sql");
	
	        //System.out.println("Bill Dtl");
	
	        String qSqlBillSettDtl = "delete from tblqbillsettlementdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillSettDtl,"sql");
	
	        qSqlBillSettDtl = "insert into tblqbillsettlementdtl (select * from tblbillsettlementdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillSettDtl,"sql");
	        
	        qSqlBillSettDtl = "delete from tblbillsettlementdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillSettDtl,"sql");
	        //System.out.println("Bill Sett Dtl");
	
	        String qSqlBillModDtl = "delete from tblqbillmodifierdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillModDtl,"sql");
	        
	        qSqlBillModDtl = "insert into tblqbillmodifierdtl (select * from tblbillmodifierdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillModDtl,"sql");
	
	        qSqlBillModDtl = "delete from tblbillmodifierdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillModDtl,"sql");
	
	        //System.out.println("Bill Mod Dtl");
	
	        String qSqlBillTaxDtl = "delete from tblqbilltaxdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillTaxDtl,"sql");
	
	        qSqlBillTaxDtl = "insert into tblqbilltaxdtl (select * from tblbilltaxdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillTaxDtl,"sql");
	        
	        qSqlBillTaxDtl = "delete from tblbilltaxdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillTaxDtl,"sql");        //System.out.println("Bill Tax Dtl");
	
	        //discount dtl tables
	        String qSqlBillDiscDtl = "delete from tblqbilldiscdtl where strPOSCode='" + posCode + "'"
	                + "and strBillNo in (select strBillNo from tblbilldiscdtl where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDiscDtl,"sql");
	        
	        qSqlBillDiscDtl = "insert into tblqbilldiscdtl (select * from tblbilldiscdtl "
	                + "where strPOSCode='" + posCode + "') ";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDiscDtl,"sql");
	        
	        qSqlBillDiscDtl = "delete from tblbilldiscdtl where strPOSCode='" + posCode + "'";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillDiscDtl,"sql");
	        
	        String qSqlBillPromoDtl = "delete from tblqbillpromotiondtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillPromoDtl,"sql");
	        qSqlBillPromoDtl = "insert into tblqbillpromotiondtl (select * from tblbillpromotiondtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillPromoDtl,"sql");
	        qSqlBillPromoDtl = "delete from tblbillpromotiondtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillPromoDtl,"sql");
	        //System.out.println("Bill Promo Dtl");
	
	        String qSqlBillComplementoryDtl = "delete from tblqbillcomplementrydtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillComplementoryDtl,"sql");
	        
	        qSqlBillComplementoryDtl = "insert into tblqbillcomplementrydtl (select * from tblbillcomplementrydtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillComplementoryDtl,"sql");
	        
	        qSqlBillComplementoryDtl = "delete from tblbillcomplementrydtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillComplementoryDtl,"sql");
	       
	        String qSqlBillHd = "delete from tblqbillhd where strPOSCode='" + posCode + "'"
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillHd,"sql");
	        
	        qSqlBillHd = "insert into tblqbillhd (select * from tblbillhd "
	                + "where strClientCode='" + clientCode + "' and strPOSCode='" + posCode + "') ";
	        objBaseServiceImpl.funExecuteUpdate(qSqlBillHd,"sql");
	        
	        qSqlBillHd = "delete from tblbillhd where strPOSCode='" + posCode + "'";
	        
	         objBaseServiceImpl.funExecuteUpdate(qSqlBillHd,"sql");
	        //System.out.println("Bill HD");
	        flgResult = true;
	
	    }
	    catch (Exception e)
	    {
	        flgResult = false;
	       // JOptionPane.showMessageDialog(null, "Qfile Data Posting failed!!!");
	        e.printStackTrace();
	    }
	    finally
	    {
	        return flgResult;
	    }
	 }

	public void funInvokeHOWebserviceForTrans(String transType, String formName,String clientCode,String POSCode)
	{

		try{
			String gHOPOSType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
				
		    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
	    	String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	List list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	 
 	// send client code and pos code for getting global veriable data 
		    if(gHOCommunication.equals("Y"))//true
		    {
		        if (transType.equalsIgnoreCase("Sales"))
		        {
		            //objSynchData.funPostSaleDataToHO(formName);
		        	objSynchronizePOSDataToHO.funPostSalesDataToHOInBulk(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("PlaceOrder"))
		        {
		            objSynchronizePOSDataToHO.funPostPlaceOrderDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("Audit"))
		        {
		        	objSynchronizePOSDataToHO.funPostAuditDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("Inventory"))
		        {
		        	objSynchronizePOSDataToHO.funPostInventoryDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("AdvanceOrder"))
		        {
		        	objSynchronizePOSDataToHO.funPostAdvOrderDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("All"))
		        {
		        	objSynchronizePOSDataToHO.funPostAuditDataToHO(formName,clientCode,POSCode);
		        	objSynchronizePOSDataToHO.funPostSaleDataToHO(formName,clientCode,POSCode);
		        	objSynchronizePOSDataToHO.funPostInventoryDataToHO(formName,clientCode,POSCode);
		        	
		        	funPostCustomerDataToHOPOS(clientCode,POSCode);
		        	funPostCustomerAreaDataToHOPOS(clientCode,POSCode);
		        }
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
}
	
	public void funPostCustomerDataToHOPOS(String clientCode,String POSCode)
	{
		 try
		 {
			String gHOPOSType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
			if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
		 	String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	List list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	    	
	        if(gHOCommunication.equals("Y"))
	        {
	        
	            if (objSynchronizePOSDataToHO.funPostCustomerMasterDataToHO())
	            {
	            	objBaseServiceImpl.funExecuteUpdate("update tblcustomermaster set "
	            			+ "strDataPostFlag='Y'", "sql");
	            }
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    finally
	    {
	    }
	}

	public void funPostCustomerAreaDataToHOPOS(String clientCode,String POSCode)
{
		try
	    {
	          
			String gHOPOSType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
			if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
	
		    String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	List list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	    	
	        if(gHOCommunication.equals("Y"))
	        {
	                boolean flgCustAreaMaster = objSynchronizePOSDataToHO.funPostCustomerAreaMaster();
	                if (flgCustAreaMaster)
	                {
	                	objBaseServiceImpl.funExecuteUpdate("update tblbuildingmaster set strDataPostFlag='Y'", "sql");
	                    System.out.println("cust area master flg=" + flgCustAreaMaster);
	                }
	                boolean flgDelCharges = objSynchronizePOSDataToHO.funPostDelChargesMaster();
	                if (flgDelCharges)
	                {
	                	objBaseServiceImpl.funExecuteUpdate("update tblareawisedc set strDataPostFlag='Y'","sql");
	                	System.out.println("cust area Del charges flg=" + flgDelCharges);
	                }
	            }
	      }
    catch (Exception e)
    {
        e.printStackTrace();
    }
}
	
	public void funPostDayEndData(String newStartDate,int shiftCode,String strClientCode,String posCode)
	  {
		 try
		 {  
			 	String gHOPOSType= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gHOPOSType");
			    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
			    {
			        return;
			    }
			 	
			    String gHOCommunication="";
		    	String sql="select strHOCommunication from tblconfig where strClientCode='"+strClientCode+"'";
		    	List list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
		    	if(list.size()>0){
		    		gHOCommunication=(String) list.get(0);	 
		    	}
		    	
		        if(gHOCommunication.equals("Y"))
		        {
	                if (objSynchronizePOSDataToHO.funPostDayEndData(newStartDate,shiftCode,strClientCode,posCode))
		             {
	                	objBaseServiceImpl.funExecuteUpdate("update tbldayendprocess set strDataPostFlag='Y' where strDayEnd='Y'","sql");
	                 }
	                	objSynchronizePOSDataToHO.funPostCashManagementData(strClientCode,posCode);
		            }
		        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	

    private void funSendDayEndSMS(String mobileNo, String posCode, String posDate, int shiftNo,HttpServletRequest req)
    {
	try
	{
	    //clsUtility2 objUtility2 = new clsUtility2();
	    StringBuilder mainSMSBuilder = new StringBuilder();
	    //DecimalFormat decimalFormat = new DecimalFormat("0.##");
	    long netTotal = 0, grossTotal = 0, totalDisc = 0, totalAPC = 0;
	    int totalPax = 0;

	    StringBuilder sqlSettelementBrkUP =new StringBuilder("select SUM(a.dblSubTotal)-sum(a.dblDiscountAmt) as NetTotal,SUM(a.dblGrandTotal) as GrossSales "
		    + ",SUM(a.intBillSeriesPaxNo) as TotalPax,SUM(a.dblSubTotal)/SUM(a.intBillSeriesPaxNo) as APC  "
		    + "from  tblqbillhd a  "
		    + "where a.strPOSCode='"+posCode+"'  "
		    + "and date(a.dteBillDate)='"+posDate+"'  "
		    + "and a.intShiftCode='"+shiftNo+"' ");
	    
	    List listSettelementBrkUP = objBaseServiceImpl.funGetList(sqlSettelementBrkUP, "sql");
	    if (listSettelementBrkUP.size()>0)
	    {
	    	Object[] obj=(Object[])listSettelementBrkUP.get(0);
				netTotal = Long.parseLong(obj[0].toString());//netTotal
				grossTotal = Long.parseLong(obj[1].toString());//grossTotal
				totalPax =  Integer.parseInt(obj[2].toString());//totalPax
				totalAPC =  Long.parseLong(obj[3].toString());//totalAPC
	    }
	    
	    mainSMSBuilder.append("Day_End");
	    mainSMSBuilder.append(" ,Date:" + posDate);
	    mainSMSBuilder.append(" ,POS:" + req.getSession().getAttribute("gPOSName"));
	    mainSMSBuilder.append(" ,Shift:" + shiftNo);
	    mainSMSBuilder.append(" ,User:" + req.getSession().getAttribute("gUserCode"));
	    if (totalPax > 0)
	    {
		totalAPC = netTotal / totalPax;
	    }

	    mainSMSBuilder.append("     ");
	    mainSMSBuilder.append(" ,NET SALE:" + String.valueOf(Math.rint(netTotal)));
	    mainSMSBuilder.append(" ,GROSS SALE:" + String.valueOf(Math.rint(grossTotal)));
	    mainSMSBuilder.append(" ,PAX:" + String.valueOf(Math.rint(totalPax)));
	    mainSMSBuilder.append(" ,APC:" + String.valueOf(Math.rint(totalAPC)));

	    /**
	     * MTD sales
	     */
	    String[] arrDay = posDate.split("-");
	    String fromDate = "";
	    if (Integer.valueOf(arrDay[2]) > 1)
	    {
		fromDate = arrDay[0] + "-" + arrDay[1] + "-" + "01";
	    }
	    double monthTotalSales = 0.00, monthNetTotal = 0.00, monthGrossTotal = 0.00, monthTotalPax = 0, monthTotalAPC = 0;
	    StringBuilder sqlUpToDateForCurrentMonthBrkUP =new StringBuilder("select SUM(a.dblSubTotal) as NetTotal,SUM(a.dblGrandTotal) as GrossSales "
		    + ",SUM(a.intBillSeriesPaxNo) as TotalPax,SUM(a.dblSubTotal)/SUM(a.intBillSeriesPaxNo) as APC  "
		    + "from  tblqbillhd a  "
		    + "where a.strPOSCode='" + posCode + "'  "
		    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + posDate + "'  "
		    + "and a.intShiftCode='" + shiftNo + "' ");
	    List listMonthBkp = objBaseServiceImpl.funGetList(sqlUpToDateForCurrentMonthBrkUP, "sql");
	    if (listMonthBkp.size()>0)
	    {
	    	for(int i=0;i<listMonthBkp.size();i++){
	    		Object[] obj=(Object[])listMonthBkp.get(i);

	    		monthNetTotal =Double.parseDouble(obj[0].toString()); //monthNetTotal
	    		monthGrossTotal =Double.parseDouble(obj[1].toString());//monthGrossTotal
	    		monthTotalPax = Double.parseDouble(obj[2].toString()); //monthTotalPax
	    		monthTotalAPC =Double.parseDouble(obj[3].toString());  //monthTotalAPC
	    		
//              /**
//              * (x to y APC)/y days
//              */
//             monthTotalAPC = monthTotalAPC / Integer.parseInt(arrDay[2]);

	    	}
	    	
	    }


	    mainSMSBuilder.append("     ");
	    mainSMSBuilder.append(" ,MTD NET SALE:" + String.valueOf(Math.rint(monthNetTotal)));
	    mainSMSBuilder.append(" ,MTD GROSS SALE:" + String.valueOf(Math.rint(monthGrossTotal)));
	    mainSMSBuilder.append(" ,MTD PAX:" + String.valueOf(Math.rint(monthTotalPax)));
	    mainSMSBuilder.append(" ,MTD APC:" + String.valueOf(Math.rint(monthTotalAPC)));

	    /**
	     * group wise sales
	     */
	    StringBuilder sqlBuilder = new StringBuilder();
	    Map<String, Double> mapGroupSales = new HashMap<>();

	    //live
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt) NetTotal "
		    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo  "
		    + "AND a.strPOSCode=f.strPOSCode  "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND b.strItemCode=e.strItemCode  "
		    + "AND c.strGroupCode=d.strGroupCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode  "
		    + "AND DATE(a.dteBillDate) BETWEEN '" + posDate + "' AND '" + posDate + "' "
		    + "AND a.strPOSCode='" + posCode + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName ");
	    
	    List listGroupSales = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if (listGroupSales.size()>0)
	    {
	    	for(int i=0;i<listGroupSales.size();i++){
	    		Object[] obj=(Object[])listGroupSales.get(i);
	    		
	    		String groupName = obj[1].toString();//groupName
	    		double groupNetTotal =Double.parseDouble(obj[3].toString()); //netTotal
	    		if (mapGroupSales.containsKey(groupName))
	    		{
	    		    mapGroupSales.put(groupName, mapGroupSales.get(groupName) + groupNetTotal);
	    		}
	    		else
	    		{
	    		    mapGroupSales.put(groupName, groupNetTotal);
	    		}
	    	    
	    	}
	    }
	    
	    //modifiers
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt) NetTotal "
		    + "FROM tblbillhd a,tblbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo  "
		    + "AND a.strPOSCode=f.strPOSCode  "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND left(b.strItemCode,7)=e.strItemCode  "
		    + "AND c.strGroupCode=d.strGroupCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode  "
		    + "AND DATE(a.dteBillDate) BETWEEN '" + posDate + "' AND '" + posDate + "' "
		    + "AND a.strPOSCode='" + posCode + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName ");
	    
	    listGroupSales = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if (listGroupSales.size()>0)
	    {
	    	for(int i=0;i<listGroupSales.size();i++){
	    		Object[] obj=(Object[])listGroupSales.get(i);
	    		
	    		String groupName = obj[1].toString();//groupName
	    		double groupNetTotal =Double.parseDouble(obj[3].toString());//netTotal
	    		if (mapGroupSales.containsKey(groupName))
	    		{
	    		    mapGroupSales.put(groupName, mapGroupSales.get(groupName) + groupNetTotal);
	    		}
	    		else
	    		{
	    		    mapGroupSales.put(groupName, groupNetTotal);
	    		}
	    	}
	    }
	  	    //Q
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt) NetTotal "
		    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo  "
		    + "AND a.strPOSCode=f.strPOSCode  "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND b.strItemCode=e.strItemCode  "
		    + "AND c.strGroupCode=d.strGroupCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode  "
		    + "AND DATE(a.dteBillDate) BETWEEN '" + posDate + "' AND '" + posDate + "' "
		    + "AND a.strPOSCode='" + posCode + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName ");
	    listGroupSales = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if (listGroupSales.size()>0)
	    {
	    	for(int i=0;i<listGroupSales.size();i++){
	    		Object[] obj=(Object[])listGroupSales.get(i);
	    		String groupName = obj[1].toString();//groupName
	    		double groupNetTotal =Double.parseDouble(obj[3].toString());//netTotal
	    		if (mapGroupSales.containsKey(groupName))
	    		{
	    		    mapGroupSales.put(groupName, mapGroupSales.get(groupName) + groupNetTotal);
	    		}
	    		else
	    		{
	    		    mapGroupSales.put(groupName, groupNetTotal);
	    		}
	    	    
	    	}
	    }
	   
	    //modifiers
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt) NetTotal "
		    + "FROM tblqbillhd a,tblqbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo  "
		    + "AND a.strPOSCode=f.strPOSCode  "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND left(b.strItemCode,7)=e.strItemCode  "
		    + "AND c.strGroupCode=d.strGroupCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode  "
		    + "AND DATE(a.dteBillDate) BETWEEN '" + posDate + "' AND '" + posDate + "' "
		    + "AND a.strPOSCode='" + posCode + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName ");
	    listGroupSales = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if (listGroupSales.size()>0)
	    {
	    	for(int i=0;i<listGroupSales.size();i++){
	    		Object[] obj=(Object[])listGroupSales.get(i);

	    		String groupName = obj[1].toString();//groupName
	    		double groupNetTotal =Double.parseDouble(obj[3].toString());//netTotal
	    		if (mapGroupSales.containsKey(groupName))
	    		{
	    		    mapGroupSales.put(groupName, mapGroupSales.get(groupName) + groupNetTotal);
	    		}
	    		else
	    		{
	    		    mapGroupSales.put(groupName, groupNetTotal);
	    		}
	    	    
	    	}
	    }

	    mainSMSBuilder.append("     ");
	    mainSMSBuilder.append("Group Wise Net Sales:");
	    mainSMSBuilder.append("     ");
	    for (Map.Entry<String, Double> entry : mapGroupSales.entrySet())
	    {
		String groupName = entry.getKey();
		double groupNetTotal = entry.getValue();

		mainSMSBuilder.append(" ," + groupName + ":" + String.valueOf(Math.rint(groupNetTotal)));
	    }

	    System.out.println("Day end SMS-->\n" + mainSMSBuilder);

	    ArrayList<String> mobileNumberList = new ArrayList<String>();
	    String mobNos[] = mobileNo.split(",");
	    for (String mn : mobNos)
	    {
		mobileNumberList.add(mn);
	    }
////	    ///boolean isSend = funSendBulkSMS(mobileNumberList, mainSMSBuilder.toString());
//	    System.out.println("day end msg sent->" + isSend);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    public String getCurrentDateTime()
	    {
	        Date currentDate = new Date();
	        String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
	            + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
	        return strCurrentDate;
	    }



}
