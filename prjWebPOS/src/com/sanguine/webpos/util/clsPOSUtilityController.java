package com.sanguine.webpos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemTaxDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSBuyPromotionItemDtl;
import com.sanguine.webpos.bean.clsPOSGetPromotionItemDtl;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.bean.clsPOSTaxCalculationBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;



@Controller

public class clsPOSUtilityController {
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	
	@Autowired
	clsPOSSendMail obSendMail; 

	@Autowired 
	clsPOSSetupUtility objPOSSetupUtility;
	
	 double gTotalCashSales=0.00 ,gNoOfDiscountedBills=0.00,gTotalDiscounts=0.00,gTotalBills=0.00,gTotalAdvanceAmt=0.00;
     double gTotalReceipt=0.00,gTotalPayments=0.00,gTotalCashInHand=0.00;
	
    public List funCalculateTax(List<clsPOSItemDetailFrTaxBean> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception
    {
        return funCheckDateRangeForTax1(arrListItemDtl, POSCode, dtPOSDate, billAreaCode, operationTypeForTax, subTotal, discountAmt, transType, settlementCode);
    }

    private List funCheckDateRangeForTax1(List<clsPOSItemDetailFrTaxBean> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception
    {
        List<clsPOSTaxCalculationBean> arrListTaxDtl = new ArrayList<clsPOSTaxCalculationBean>();
        String taxCode = "", taxName = "", taxOnGD = "", taxCal = "", taxIndicator = "";
        String opType = "", taxAreaCodes = "", taxOnTax = "No", taxOnTaxCode = "";
        double taxPercent = 0.00, taxableAmount = 0.00, taxCalAmt = 0.00;
        objBaseServiceImpl.funExecuteUpdate("truncate table tbltaxtemp;","sql");// Empty Tax Temp Table
        StringBuilder sbSql = new StringBuilder();
        sbSql.setLength(0);
        sbSql.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent"
                + ",a.dblAmount,a.strTaxOnGD,a.strTaxCalculation,ifnull(a.strTaxIndicator,'NA'),a.strAreaCode,a.strOperationType"
                + ",a.strItemType,a.strTaxOnTax,a.strTaxOnTaxCode "
                + "from tbltaxhd a,tbltaxposdtl b "
                + "where a.strTaxCode=b.strTaxCode and b.strPOSCode='" + POSCode + "' ");
        if (transType.equals("Tax Regen"))
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        else
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        sbSql.append(" and a.strTaxOnSP='Sales' "
                + "order by a.strTaxOnTax,a.strTaxDesc");
      
        List listSqlModLive = objBaseServiceImpl.funGetList(sbSql, "sql"); 
		   if(listSqlModLive.size()>0)
		    {
			  
		    
			   for(int i=0 ;i<listSqlModLive.size();i++ )
		    
		    	{
				   Object[] objM = (Object[]) listSqlModLive.get(i);
				  
            taxCode = objM[0].toString();
            taxName = objM[1].toString();
            taxOnGD =objM[6].toString();
            taxCal = objM[7].toString();
            taxIndicator = objM[8].toString();
            taxOnTax = objM[12].toString();
            taxOnTaxCode = objM[13].toString();

            taxPercent = Double.parseDouble(objM[4].toString());
            taxableAmount = 0.00;
            taxCalAmt = 0.00;
            sbSql.setLength(0);
            sbSql.append( "select strAreaCode,strOperationType,strItemType "
                    + "from tbltaxhd where strTaxCode='" + taxCode + "'");
      
            List listSql =objBaseServiceImpl.funGetList(sbSql, "sql");
 		   if(listSql.size()>0)
 		    {
 		 
 				   Object[] obj = (Object[]) listSql.get(0);
               
 				   taxAreaCodes =obj[0].toString();
                opType =obj[1].toString();
            
 		    }
            if (funCheckAreaCode(taxAreaCodes, billAreaCode))
            {
                if (funCheckOperationType(opType, operationTypeForTax))
                {
                    if (funFindSettlementForTax(taxCode, settlementCode))
                    {
                    	clsPOSTaxCalculationBean objTaxDtls = new clsPOSTaxCalculationBean();
                        if (taxIndicator.trim().length() > 0) // For Indicator Based Tax
                        {
                            double taxIndicatorTotal = funGetTaxIndicatorTotal(taxIndicator, arrListItemDtl);
                            if (taxIndicatorTotal > 0)
                            {
                                double discAmt = 0, discPer = 0;//                              
                                discAmt = funGetTaxIndicatorBasedDiscAmtTotal(taxIndicator, arrListItemDtl);
                                if (taxIndicatorTotal > 0)
                                {
                                    discPer = (discAmt / taxIndicatorTotal) * 100;
                                }

                                if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation
                                {
                                    taxIndicatorTotal += funGetTaxAmountForTaxOnTaxForIndicatorTax(taxOnTaxCode, taxIndicatorTotal, arrListTaxDtl);
                                }
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = taxIndicatorTotal;
                                }
                                else
                                {
                                    taxableAmount = taxIndicatorTotal - ((taxIndicatorTotal * discPer) / 100);
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * 100 / (100 + taxPercent);
                                    taxCalAmt = taxableAmount - taxCalAmt;
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                        }
                        else // For Blank Indicator
                        {
                            if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation
                            {
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = subTotal + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, arrListTaxDtl);
                                }
                                else
                                {
                                    subTotal = 0;
                                    double discAmt = 0;
                                    for (clsPOSItemDetailFrTaxBean objItemDtl : arrListItemDtl)
                                    {
                                        if (objItemDtl.getDiscAmt() > 0)
                                        {
                                            discAmt += objItemDtl.getDiscAmt();
                                        }
                                        subTotal += objItemDtl.getAmount();
                                    }
                                    taxableAmount = subTotal - discAmt;
                                    taxableAmount += funGetTaxableAmountForTaxOnTax(taxOnTaxCode, arrListTaxDtl);
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount - (taxableAmount * 100 / (100 + taxPercent));
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                            else
                            {
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = subTotal;
                                }
                                else
                                {
                                    subTotal = 0;
                                    double discAmt = 0;
                                    for (int cn = 0; cn < arrListItemDtl.size(); cn++)
                                    {
                                    	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cn);
                                        //System.out.println("Name= "+objItemDtl.getItemName()+"\tDisc Amt= "+objItemDtl.getDiscAmt());
                                        discAmt += objItemDtl.getDiscAmt();
                                        subTotal += objItemDtl.getAmount();
                                    }
                                    taxableAmount = subTotal - discAmt;
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount - (taxableAmount * 100 / (100 + taxPercent));
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                        }
                    }
                }
            }
        }
    }

        return arrListTaxDtl;
 }
      
    
    private boolean funCheckAreaCode(String taxAreaCodes, String billAreaCode)
    {
        boolean flgTaxOn = false;
        String[] spAreaCode = taxAreaCodes.split(",");
        for (int cnt = 0; cnt < spAreaCode.length; cnt++)
        {
            if (spAreaCode[cnt].equals(billAreaCode))
            {
                flgTaxOn = true;
                break;
            }
        }

        return flgTaxOn;
    }
    
    
    private boolean funCheckOperationType(String taxOpTypes, String operationTypeForTax)
    {
        boolean flgTaxOn = false;
        String[] spOpType = taxOpTypes.split(",");
        for (int cnt = 0; cnt < spOpType.length; cnt++)
        {
            if (spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("HomeDelivery"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("Home Delivery"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("DineIn"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("Dine In"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("TakeAway") && operationTypeForTax.equalsIgnoreCase("TakeAway"))
            {
                flgTaxOn = true;
                break;
            }
        }
        return flgTaxOn;
    }
 
    
    private boolean funFindSettlementForTax(String taxCode, String settlementCode) throws Exception
    {
        boolean flgTaxSettlement = false;
        StringBuilder  sqlBuilder=new StringBuilder();
		  sqlBuilder.append( "select strSettlementCode,strSettlementName "
                + "from tblsettlementtax where strTaxCode='" + taxCode + "' "
                + "and strApplicable='true' and strSettlementCode='" + settlementCode + "'");
     
        
        List listSql = objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		   if(listSql.size()>0)
		    {    
		
            flgTaxSettlement = true;
        
		    }
      
        return flgTaxSettlement;
    }
    
    private double funGetTaxIndicatorTotal(String indicator, List<clsPOSItemDetailFrTaxBean> arrListItemDtl) throws Exception
    {
        String sql_Query = "";
        double indicatorAmount = 0.00;
        for (int cnt = 0; cnt < arrListItemDtl.size(); cnt++)
        {
        	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cnt);
        	StringBuilder  sqlBuilder=new StringBuilder();
  		  sqlBuilder.append("select strTaxIndicator from tblitemmaster "
                    + "where strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
                    + "and strTaxIndicator='" + indicator + "'");
            
            List listSql = objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
    		   if(listSql.size()>0)
    		    {    
    			   for(int i=0 ;i<listSql.size();i++ )
    		       	{
         
                indicatorAmount += objItemDtl.getAmount();
            }
    		}
          
        }
        return indicatorAmount;
    }
    
    
    private double funGetTaxIndicatorBasedDiscAmtTotal(String indicator, List<clsPOSItemDetailFrTaxBean> arrListItemDtl) throws Exception
    {
        String sql_Query = "";
        double discAmt = 0.00;
        for (int cnt = 0; cnt < arrListItemDtl.size(); cnt++)
        {
        	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cnt);
        	StringBuilder  sqlBuilder=new StringBuilder();
    		  sqlBuilder.append( "select strTaxIndicator from tblitemmaster "
                    + "where strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
                    + "and strTaxIndicator='" + indicator + "'");
  
            
            List listSql =  objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
    		   if(listSql.size()>0)
    		    {    
    			   for(int i=0 ;i<listSql.size();i++ )
    		       	{
          
                discAmt += objItemDtl.getDiscAmt();
            }
    	    }
        
        }
        return discAmt;
    }


    private double funGetTaxAmountForTaxOnTaxForIndicatorTax(String taxOnTaxCode, double indicatorTaxableAmt, List<clsPOSTaxCalculationBean> listTaxDtl) throws Exception
    {
        double taxAmt = 0;
        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
        for (clsPOSTaxCalculationBean objTaxCalDtl : listTaxDtl)
        {
            for (int t = 0; t < spTaxOnTaxCode.length; t++)
            {
                if (objTaxCalDtl.getTaxCode().equals(spTaxOnTaxCode[t]))
                {
                    taxAmt += funGetTaxOnTaxAmtForIndicatorTax(spTaxOnTaxCode[t], indicatorTaxableAmt);
                }
            }
        }

        return taxAmt;
    }
    
    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, List<clsPOSTaxCalculationBean> arrListTaxCal) throws Exception
    {
        double taxAmt = 0;
        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
        for (int cnt = 0; cnt < arrListTaxCal.size(); cnt++)
        {
            for (int t = 0; t < spTaxOnTaxCode.length; t++)
            {
            	clsPOSTaxCalculationBean objTaxDtls = arrListTaxCal.get(cnt);
                if (objTaxDtls.getTaxCode().equals(spTaxOnTaxCode[t]))
                {
                    taxAmt += objTaxDtls.getTaxAmount();
                }
            }
        }
        return taxAmt;
    }
    
    private double funGetTaxOnTaxAmtForIndicatorTax(String taxCode, double taxableAmt) throws Exception
    {
        double taxAmt = 0;
    	StringBuilder  sqlBuilder=new StringBuilder();
		  sqlBuilder.append( "select a.strTaxCode,a.strTaxType,a.dblPercent"
                + " ,a.dblAmount,a.strTaxOnGD,a.strTaxCalculation "
                + " from tbltaxhd a "
                + " where a.strTaxOnSP='Sales' and a.strTaxCode='" + taxCode + "'");
        
        List listSql = objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		   if(listSql.size()>0)
		    {    
			   for(int i=0 ;i<listSql.size();i++ )
		       	{
				   Object[] obj = (Object[]) listSql.get(i);
            
				   double taxPercent = Double.parseDouble(obj[2].toString());
		            if (obj[5].toString().equals("Forward")) // Forward Tax Calculation
		            {
		                taxAmt = taxableAmt * (taxPercent / 100);
		            }
		            else // Backward Tax Calculation
		            {
		                taxAmt = taxableAmt * 100 / (100 + taxPercent);
		                taxAmt = taxableAmt - taxAmt;
		            }
		       	}
		    }

        return taxAmt;
    }


	 public String funGetCurrentDate()
	    {
	        Calendar objDate = new GregorianCalendar();
	        String currentDate = (objDate.getTime().getYear() + 1900) + "-" + (objDate.getTime().getMonth() + 1) + "-" + objDate.getTime().getDate();
	        return currentDate;
	    }
	 
	 public String funGetCurrentTime()
	    {
	        String currentTime = "";
	        Date dt = new Date();
	        int hours = dt.getHours();
	        int minutes = dt.getMinutes();
	        if (hours > 12)
	        {
	            //hours=hours-12;
	            currentTime = hours + ":" + minutes + " PM";
	        }
	        else
	        {
	            currentTime = hours + ":" + minutes + " AM";
	        }
	        return currentTime;
	    }

	 public String funGetCurrentDateTime()
	    {
	        Date currentDate = new Date();
	        String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
	            + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
	        return strCurrentDate;
	    }

	 public String funGetDayForPricing()
     {
        String day = "";
       
        String dayNames[] = new DateFormatSymbols().getWeekdays();
        Calendar date2 = Calendar.getInstance();
        String tempday = dayNames[date2.get(Calendar.DAY_OF_WEEK)];
        switch (tempday)
        {
            case "Sunday":
                day = "strPriceSunday";
                break;

            case "Monday":
                day = "strPriceMonday";
                break;

            case "Tuesday":
                day = "strPriceTuesday";
                break;

            case "Wednesday":
                day = "strPriceWednesday";
                break;

            case "Thursday":
                day = "strPriceThursday";
                break;

            case "Friday":
                day = "strPriceFriday";
                break;

            case "Saturday":
                day = "strPriceSaturday";
                break;

            default:
                day = "strPriceSunday";
        }
        return day;
    }

	public double funGetKOTAmtOnTable(String cardNo) throws Exception
	{
	        double KOTAmt = 0;
	       
	        List list;
	        String tableNo = "";
	        StringBuilder sql =new StringBuilder("select sum(dblAmount),strTableNo "
	                + " from tblitemrtemp "
	                + " where strCardNo='" + cardNo + "' and strNCKotYN='N' "
	                + " group by strTableNo;");
	        list=objBaseServiceImpl.funGetList(sql, "sql");
	        if (list.size()>0)
	        {
	        	Object[] obj = (Object[]) list.get(0);	
	            KOTAmt +=Double.parseDouble(obj[0].toString());
	            tableNo = obj[1].toString();
	        }
	    
	        if (!cardNo.isEmpty())
	        {
	            sql =new StringBuilder("select sum(dblTaxAmt) "
	                    + " from tblkottaxdtl "
	                    + " where strTableNo='" + tableNo + "' "
	                    + " group by strTableNo;");
	            list=objBaseServiceImpl.funGetList(sql, "sql");
	            if (list.size()>0)
	            {
	            	Object[] obj = (Object[]) list.get(0);
	                KOTAmt += Double.parseDouble(obj[0].toString());
	            }       
	        }
	        return KOTAmt;
	 }
    public int funGetLastOrderNo()
    {
        int orderNo = 0;
        try
        {
        	 StringBuilder sql =new StringBuilder("select * from tblinternal a  where a.strTransactionType='OrderNo' ");
            
        	  List list=objBaseServiceImpl.funGetList(sql, "sql");
   	        if (list.size()>0)
   	        {
   	        	Object []obj = (Object[]) list.get(0);	
   	        	orderNo =Integer.parseInt(obj[1].toString());
   	        	orderNo++;
   	        	objBaseServiceImpl.funExecuteUpdate("update tblinternal set dblLastNo=(dblLastNo+1) where strTransactionType='OrderNo' ","sql");
   	        }
    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return orderNo;
        }
    }
    
    
    public Map funCalculateRoundOffAmount(double settlementAmt,String posCode)
    {
    	 Map<String, Double> hm = new HashMap<>();
    	try{
       
        StringBuilder sqlBuilder=new StringBuilder();
        sqlBuilder.setLength(0);
	  	sqlBuilder.append("select dblRoundOff  from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
		List list1=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
		
		 double roundOffTo =0.0;
		if(list1!=null && list1.size()>0)
		{				
			Object obj=(Object)list1.get(0);
			roundOffTo=Double.parseDouble(obj.toString());
			
		}
     
        if (roundOffTo == 0.00)
        {
            roundOffTo = 1.00;
        }

        double roundOffSettleAmt = settlementAmt;
        double remainderAmt = (settlementAmt % roundOffTo);
        double roundOffToBy2 = roundOffTo / 2;
        double x = 0.00;

        if (remainderAmt <= roundOffToBy2)
        {
            x = (-1) * remainderAmt;

            roundOffSettleAmt = (Math.floor(settlementAmt / roundOffTo) * roundOffTo);

            //System.out.println(settleAmt + " " + roundOffSettleAmt + " " + x);
        }
        else
        {
            x = roundOffTo - remainderAmt;

            roundOffSettleAmt = (Math.ceil(settlementAmt / roundOffTo) * roundOffTo);

            // System.out.println(settleAmt + " " + roundOffSettleAmt + " " + x);
        }

        hm.put("roundOffAmt", roundOffSettleAmt);
        hm.put("roundOffByAmt", x);

        System.out.println("Original Settl Amt=" + settlementAmt + " RoundOff Settle Amt=" + roundOffSettleAmt + " RoundOff To=" + roundOffTo + " RoundOff By=" + x);
    	 }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	 return hm;
        }
       

    }
    
    
    public List funCalculateTax(List<clsPOSItemDtlForTax> arrListItemDtl,String POSCode
	        ,String dtPOSDate, String billAreaCode, String operationTypeForTax
	        , double subTotal, double discountPer, String transType) throws Exception
	    {
	        return funCheckDateRangeForTax(arrListItemDtl, POSCode, dtPOSDate, billAreaCode, operationTypeForTax, subTotal, discountPer,transType,"S01");
	    }
    
    private List funCheckDateRangeForTax(List<clsPOSItemDtlForTax> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception
    {
		
		
        List<clsPOSTaxCalculationDtls> arrListTaxDtl = new ArrayList<clsPOSTaxCalculationDtls>();
        String taxCode = "", taxName = "", taxOnGD = "", taxCal = "", taxIndicator = "";
        String opType = "", taxAreaCodes = "", taxOnTax = "No", taxOnTaxCode = "";
        double taxPercent = 0.00, taxableAmount = 0.00, taxCalAmt = 0.00;
        
//        StringBuilder sbSql = new StringBuilder();
//        sbSql.setLength(0);
//        sbSql.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent "
//                + ",a.dblAmount,a.strTaxOnGD,a.strTaxCalculation,a.strTaxIndicator "
//                + ",a.strAreaCode "
//                + ",a.strOperationType "
//                + ",a.strItemType,a.strTaxOnTax "
//                + ",a.strTaxOnTaxCode "
//                + "from tbltaxhd a,tbltaxposdtl b "
//                + "where a.strTaxCode=b.strTaxCode and b.strPOSCode='" + POSCode + "' ");
//        if (transType.equals("Tax Regen"))
//        {
//            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
//        }
//        else
//        {
//            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
//        }
//        sbSql.append(" and a.strTaxOnSP='Sales' "
//                + "order by a.strTaxOnTax,a.strTaxDesc");

        StringBuilder sbSql = new StringBuilder();
        sbSql.setLength(0);
        sbSql.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent"
                + ",a.dblAmount,a.strTaxOnGD,a.strTaxCalculation,ifnull(a.strTaxIndicator,' '),a.strAreaCode,a.strOperationType"
                + ",a.strItemType,a.strTaxOnTax,a.strTaxOnTaxCode "
                + "from tbltaxhd a,tbltaxposdtl b "
                + "where a.strTaxCode=b.strTaxCode and b.strPOSCode='" + POSCode + "' ");
        if (transType.equals("Tax Regen"))
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        else
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        sbSql.append(" and a.strTaxOnSP='Sales' "
                + "order by a.strTaxOnTax,a.strTaxDesc");
        
        List listSql= objBaseServiceImpl.funGetList(sbSql, "sql"); 
        if(listSql.size()>0)
	    {    
		   for( int i=0 ;i<listSql.size();i++ )
	       	{
			Object[] obj = (Object[]) listSql.get(i);
            taxCode = obj[0].toString();
            taxName = obj[1].toString();
            taxOnGD = obj[6].toString();
            taxCal = obj[7].toString();
            taxIndicator = obj[8].toString();
            taxOnTax = obj[12].toString();
            taxOnTaxCode = obj[13].toString();
            taxPercent = Double.parseDouble(obj[4].toString());

            taxableAmount = 0.00;
            taxCalAmt = 0.00;

            StringBuilder sqlTaxOn = new StringBuilder();
            sqlTaxOn.append("select strAreaCode,strOperationType,strItemType "
                    + "from tbltaxhd where strTaxCode='" + taxCode + "'");
            List listTaxOn= objBaseServiceImpl.funGetList(sqlTaxOn, "sql"); 
          
            if (listTaxOn.size()>0)
            {
            	for(int j=0 ;j<listTaxOn.size();j++ )
    	       	{
    			 obj = (Object[]) listTaxOn.get(j);
            	taxAreaCodes = obj[0].toString();
                opType = obj[1].toString();
    	       	}
            }
            if (funCheckAreaCode(taxAreaCodes, billAreaCode))
            {
                if (funCheckOperationType(opType, operationTypeForTax))
                {
                    if (funFindSettlementForTax(taxCode, settlementCode))
                    {
                        boolean flgTaxOnGrpApplicable = false;
                        taxableAmount = 0;
                        clsPOSTaxCalculationDtls objTaxDtls = new clsPOSTaxCalculationDtls();

                        if (taxOnGD.equals("Gross"))
                        {
                            //to calculate tax on group of an item
                            for (int j = 0; j < arrListItemDtl.size(); j++)
                            {
                                clsPOSItemDtlForTax objItemDtl = arrListItemDtl.get(j);

                                boolean isApplicable = isTaxApplicableOnItemGroup(taxCode, arrListItemDtl.get(j));
                                if (isApplicable)
                                {
                                    flgTaxOnGrpApplicable = true;
                                    taxableAmount = taxableAmount + objItemDtl.getAmount();

                                    if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation new logic only for same group item
                                    {
                                        taxableAmount = taxableAmount + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, objItemDtl, billAreaCode, operationTypeForTax, settlementCode,POSCode);
                                    }
                                }
                            }


                        }
                        else
                        {
                            subTotal = 0;
                            double discAmt = 0;
                            for (clsPOSItemDtlForTax objItemDtl : arrListItemDtl)
                            {
                                boolean isApplicable = isTaxApplicableOnItemGroup(taxCode, objItemDtl);
                                if (isApplicable)
                                {
                                    flgTaxOnGrpApplicable = true;
                                    if (objItemDtl.getDiscAmt() > 0)
                                    {
                                        discAmt += objItemDtl.getDiscAmt();
                                    }
                                    taxableAmount = taxableAmount + objItemDtl.getAmount();

                                    if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation new logic only for same group item
                                    {
                                        taxableAmount = taxableAmount + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, objItemDtl, billAreaCode, operationTypeForTax, settlementCode,POSCode);
                                    }
                                }
                            }
                            if (taxableAmount > 0)
                            {
                                taxableAmount = taxableAmount - discAmt;
                            }

                        }

                        if (flgTaxOnGrpApplicable)
                        {
                            if (taxCal.equals("Forward")) // Forward Tax Calculation
                            {
                                taxCalAmt = taxableAmount * (taxPercent / 100);
                            }
                            else // Backward Tax Calculation
                            {
                                taxCalAmt = taxableAmount - (taxableAmount * 100 / (100 + taxPercent));
                            }

                            objTaxDtls.setTaxCode(taxCode);
                            objTaxDtls.setTaxName(taxName);
                            objTaxDtls.setTaxableAmount(taxableAmount);
                            objTaxDtls.setTaxAmount(taxCalAmt);
                            objTaxDtls.setTaxCalculationType(taxCal);
                            arrListTaxDtl.add(objTaxDtls);
                        }

                       
                    }
                }
            }
        
	       	}
	    } 
        return arrListTaxDtl;
    }
	
    private boolean isTaxApplicableOnItemGroup(String taxCode, clsPOSItemDtlForTax objItemDtl)throws Exception
    {
	   
        boolean isApplicable = false;
        try
        {
            StringBuilder sql = new StringBuilder(); 
            sql.append("select a.strItemCode,a.strItemName,b.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName,d.strTaxCode,d.strApplicable "
                    + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c,tbltaxongroup d "
                    + "where a.strSubGroupCode=b.strSubGroupCode "
                    + "and b.strGroupCode=c.strGroupCode "
                    + "and c.strGroupCode=d.strGroupCode "
                    + "and a.strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
                    + "and d.strTaxCode='" + taxCode + "' "
                    + "and d.strApplicable='true' ");
            List listSql = objBaseServiceImpl.funGetList(sql, "sql"); 
 		   	if(listSql.size()>0)
 		   	{
                isApplicable = true;
            }
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return isApplicable;
        }
    }
   
    //new logic for tax on tax
    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, clsPOSItemDtlForTax objItemDtl, String billAreaCode, String operationTypeForTax, String settlementCode,String POSCode) throws Exception
    {
    	
        double taxAmt = 0;
        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
        String opType = "", taxAreaCodes = "";

        for (int t = 0; t < spTaxOnTaxCode.length; t++)
        {

            StringBuilder sqlTaxOn = new StringBuilder();
            sqlTaxOn.append( "select a.strAreaCode,a.strOperationType,a.strItemType  "
                    + "from tbltaxhd a,tbltaxposdtl b "
                    + "where a.strTaxCode=b.strTaxCode "
                    + "and a.strTaxCode='" + spTaxOnTaxCode[t] + "' "
                    + "and (b.strPOSCode='" + POSCode + "' or b.strPOSCode='All') ");
            List listTaxOn = objBaseServiceImpl.funGetList(sqlTaxOn, "sql"); 
            if (listTaxOn.size()>0)
            {
                for(int i=0;i<listTaxOn.size();i++)
                {
                Object[] obj =  (Object[]) listTaxOn.get(i);	
            	taxAreaCodes = obj[0].toString();
                opType = obj[1].toString();
                }
                if (funCheckAreaCode(taxAreaCodes, billAreaCode))
                {
                    if (funCheckOperationType(opType, operationTypeForTax))
                    {
                        if (funFindSettlementForTax(spTaxOnTaxCode[t], settlementCode))
                        {

                            StringBuilder sqlTaxOnTax = new StringBuilder();
                            sqlTaxOnTax.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent,a.dblAmount,a.dteValidFrom,a.dteValidTo,a.strTaxOnGD,a.strTaxCalculation,a.strTaxIndicator "
                                    + ",a.strTaxRounded,a.strTaxOnTax,a.strTaxOnTaxCode "
                                    + "from tbltaxhd a "
                                    + "where a.strTaxCode='" + spTaxOnTaxCode[t] + "' ");
                            List listSql = objBaseServiceImpl.funGetList(sqlTaxOnTax, "sql"); 
                            if (listSql.size()>0)
                            {
                                for(int i=0;i<listSql.size();i++)
                                {
                                Object[] obj=(Object[])listSql.get(i);
                                
                            	String taxCode = obj[0].toString();
                                String taxName = obj[1].toString();
                                String taxOnGD = obj[6].toString();
                                String taxCal = obj[7].toString();
                                String taxIndicator = obj[8].toString();
                                String taxOnTax = obj[12].toString();
                                //String taxOnTaxCode = rsTaxOnTax.getString(14);
                                double taxPercent = Double.parseDouble(obj[4].toString());
                                

                                if (taxOnGD.equals("Gross"))
                                {
                                    taxAmt += (taxPercent / 100) * objItemDtl.getAmount();
                                }
                                else//discount
                                {
                                    taxAmt += (taxPercent / 100) * (objItemDtl.getAmount() - objItemDtl.getDiscAmt());
                                }
                                }
                            }
                            

                        }
                    }
                }
            }
        }
        return taxAmt;
    }

    public int funDebitCardTransaction(String billNo, String debitCardNo, double debitCardSettleAmt, String transType,String posCode,String posDate)
    {
        try
        {
            String delete = "delete from tbldebitcardbilldetails "
                    + "where strBillNo='" + billNo + "' and strTransactionType='" + transType + "' ";
            objBaseServiceImpl.funExecuteUpdate(delete, "sql")  ;
         
            //System.out.println(delete);
             
            String sqlDebitCardDetials = "insert into tbldebitcardbilldetails (strBillNo,strCardNo,"
                    + "dblTransactionAmt,strPOSCode,dteBillDate,strTransactionType)"
                    + "values ('" + billNo + "','" + debitCardNo + "','" + debitCardSettleAmt + "'"
                    + ",'" + posCode + "','" + posDate + "'"
                    + ",'" + transType + "')";
            objBaseServiceImpl.funExecuteUpdate(sqlDebitCardDetials, "sql")  ;
        } catch (Exception e)
        {
           
            e.printStackTrace();
        }
        return 1;
    }
    
    
    public int funUpdateDebitCardBalance(String debitCardNo, double debitCardSettleAmt, String transType)
    {
        try
        {
        	  StringBuilder sqlBuilder=new StringBuilder();
              sqlBuilder.setLength(0);
  		  	sqlBuilder.append ("select dblRedeemAmt from tbldebitcardmaster "
                    + "where strCardNo='" + debitCardNo + "'");
  		  List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			
			
			
			if(list!=null && list.size()>0)
			{
				for(int cnt=0;cnt<list.size();cnt++)	
				{
				Object obj=(Object)list.get(cnt);
			
                double amt = Double.parseDouble(obj.toString());
                double updatedBal = amt - debitCardSettleAmt;
                if (transType.equals("Unsettle"))
                {
                    updatedBal = amt + debitCardSettleAmt;
                }
                String sql = "update tbldebitcardmaster set dblRedeemAmt='" + updatedBal + "' "
                        + "where strCardNo='" + debitCardNo + "'";
                objBaseServiceImpl.funExecuteUpdate(sql, "sql")  ;
                }
			}
          
        } catch (Exception e)
        {
           
            e.printStackTrace();
        }
        return 1;
    }
    
    public int funUpdateBillDtlWithTaxValues(String billNo, String billType, String filterBillDate) throws Exception
    {
        Map<String, clsPOSBillItemTaxDtl> hmBillItemTaxDtl = new HashMap<String, clsPOSBillItemTaxDtl>();
        Map<String, clsPOSBillItemTaxDtl> hmBillTaxDtl = new HashMap<String, clsPOSBillItemTaxDtl>();

        String billDtl = "tblbilldtl";
        String billTaxDtl = "tblbilltaxdtl";
        String billModifierDtl = "tblbillmodifierdtl";
        String sql="";

        if (billType.equalsIgnoreCase("QFile"))
        {
            billDtl = "tblqbilldtl";
            billTaxDtl = "tblqbilltaxdtl";
            billModifierDtl = "tblqbillmodifierdtl";
        }

        StringBuilder sqlBuilder=new StringBuilder();
        sqlBuilder.setLength(0);
	  	sqlBuilder.append ( "select a.strTaxCode,b.dblPercent,b.strTaxIndicator,b.strTaxCalculation,b.strTaxOnGD,b.strTaxOnTax "
                + " ,b.strTaxOnTaxCode,a.dblTaxAmount,a.dblTaxableAmount "
                + " from " + billTaxDtl + " a,tbltaxhd b "
                + " where a.strTaxCode=b.strTaxCode "
                + " and a.strBillNo='" + billNo + "' "
                + " and date(a.dteBillDate)='" + filterBillDate + "' "
                + " and a.dblTaxAmount>0 "
                + " and a.dblTaxableAmount>0 "
                + " order by b.strTaxOnTax,b.strTaxCode; ");
	  	List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	
		if(list!=null && list.size()>0)
		{
			for(int cnt=0;cnt<list.size();cnt++)	
			{
			Object []obj=(Object[])list.get(cnt);
       
            String taxCode = obj[0].toString();
            String taxIndicator = obj[2].toString();
            double taxPercentage =  Double.parseDouble(obj[1].toString());
            String taxCalculation = obj[3].toString();
            String taxOnGD = obj[4].toString();
            String taxOnTax = obj[5].toString();
            String taxOnTaxCode = obj[6].toString();
            double billTaxAmt = Double.parseDouble( obj[7].toString());
            double billTaxableAmt = Double.parseDouble( obj[8].toString());

            sqlBuilder.setLength(0);
    	  	sqlBuilder.append ( "select a.strItemCode,a.dblAmount,b.strTaxIndicator,a.strKOTNo,a.dblDiscountAmt "
                    + " from " + billDtl + " a,tblitemmaster b "
                    + " where a.strItemCode=b.strItemCode "
                    + " and a.strBillNo='" + billNo + "'"
                    + " and date(a.dteBillDate)='" + filterBillDate + "'  ");
            
        	List listItemDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
        	
    		if(listItemDtl!=null && listItemDtl.size()>0)
    		{
    			for(int i=0;i<listItemDtl.size();i++)	
    			{
    				Object []objItemDtl=(Object[])listItemDtl.get(i);
    		
                String itemCode = objItemDtl[0].toString();
                double itemAmt =Double.parseDouble(objItemDtl[1].toString());
                double itemDiscAmt =Double.parseDouble(objItemDtl[4].toString());
                String KOTNo = objItemDtl[3].toString();
                double taxAmt = 0;
                if (taxOnGD.equalsIgnoreCase("Discount"))
                {
                    itemAmt -= itemDiscAmt;
                }

                sqlBuilder.setLength(0);
        	  	sqlBuilder.append ( "select sum(dblAmount),sum(dblDiscAmt) "
                        + " from tblbillmodifierdtl "
                        + " where strBillNo='" + billNo + "' "
                        + " and left(strItemCode,7)='" + itemCode + "' "
                        + " and date(dteBillDate)='" + filterBillDate + "' "
                        + " group by left(strItemCode,7)");
                //System.out.println(sql);
              
            	List listItemModDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
            	
        		if(listItemModDtl!=null && listItemModDtl.size()>0)
        		{
        			
        		Object []objItemModDtl=(Object[])listItemModDtl.get(i);
               
                    itemAmt += Double.parseDouble(objItemModDtl[0].toString());
                    if (taxOnGD.equalsIgnoreCase("Discount"))
                    {
                        itemAmt -=  Double.parseDouble(objItemModDtl[1].toString());
                    }

                }
               

                boolean isApplicable = isTaxApplicableOnItemGroup(taxCode, itemCode);
                if (isApplicable)
                {

                    if (taxOnTax.equals("Yes"))
                    {
                        String keyForTaxOnTax = itemCode + "," + KOTNo + "," + taxOnTaxCode;
                        if (hmBillTaxDtl.containsKey(keyForTaxOnTax))
                        {

                        	clsPOSBillItemTaxDtl objBillItemTaxDtl1 = hmBillTaxDtl.get(keyForTaxOnTax);

                            taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt + objBillItemTaxDtl1.getDblTaxAmt());

                        } else
                        {

                            taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt);

                        }

                        clsPOSBillItemTaxDtl objItemTaxDtl = new clsPOSBillItemTaxDtl();
                        objItemTaxDtl.setStrItemCode(itemCode);
                        objItemTaxDtl.setDblTaxAmt(taxAmt);
                        objItemTaxDtl.setStrKOTNo(KOTNo);
                        objItemTaxDtl.setStrBillNo(billNo);

                        String key2 = itemCode + "," + KOTNo + "," + taxCode;
                        String key1 = itemCode + "," + KOTNo;
                        hmBillTaxDtl.put(key2, objItemTaxDtl);

                        clsPOSBillItemTaxDtl objBillItemTaxDtl = new clsPOSBillItemTaxDtl();
                        objBillItemTaxDtl.setStrItemCode(itemCode);
                        objBillItemTaxDtl.setDblTaxAmt(taxAmt);
                        objBillItemTaxDtl.setStrKOTNo(KOTNo);
                        objBillItemTaxDtl.setStrBillNo(billNo);
                        if (hmBillItemTaxDtl.containsKey(key1))
                        {
                            objBillItemTaxDtl = hmBillItemTaxDtl.get(key1);
                            objBillItemTaxDtl.setDblTaxAmt(objBillItemTaxDtl.getDblTaxAmt() + taxAmt);
                        }
                        hmBillItemTaxDtl.put(key1, objBillItemTaxDtl);
                    } else
                    {

                        taxAmt = (billTaxAmt / billTaxableAmt) * itemAmt;

                        clsPOSBillItemTaxDtl objItemTaxDtl = new clsPOSBillItemTaxDtl();
                        objItemTaxDtl.setStrItemCode(itemCode);
                        objItemTaxDtl.setDblTaxAmt(taxAmt);
                        objItemTaxDtl.setStrKOTNo(KOTNo);
                        objItemTaxDtl.setStrBillNo(billNo);

                        String key2 = itemCode + "," + KOTNo + "," + taxCode;
                        String key1 = itemCode + "," + KOTNo;
                        hmBillTaxDtl.put(key2, objItemTaxDtl);

                        clsPOSBillItemTaxDtl objBillItemTaxDtl = new clsPOSBillItemTaxDtl();
                        objBillItemTaxDtl.setStrItemCode(itemCode);
                        objBillItemTaxDtl.setDblTaxAmt(taxAmt);
                        objBillItemTaxDtl.setStrKOTNo(KOTNo);
                        objBillItemTaxDtl.setStrBillNo(billNo);

                        if (hmBillItemTaxDtl.containsKey(key1))
                        {
                            objBillItemTaxDtl = hmBillItemTaxDtl.get(key1);
                            objBillItemTaxDtl.setDblTaxAmt(objBillItemTaxDtl.getDblTaxAmt() + taxAmt);
                        }
                        hmBillItemTaxDtl.put(key1, objBillItemTaxDtl);
                    }
                }
            }
    		}
            
        }
			}
     

        for (Map.Entry<String, clsPOSBillItemTaxDtl> entry : hmBillItemTaxDtl.entrySet())
        {
            sql = "update " + billDtl + " set dblTaxAmount = " + entry.getValue().getDblTaxAmt() + " "
                    + " where strBillNo='" + billNo + "' "
                    + " and strItemCode='" + entry.getValue().getStrItemCode() + "' "
                    + " and strKOTNo='" + entry.getValue().getStrKOTNo() + "' "
                    + " and date(dteBillDate)='" + filterBillDate + "' ";
            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
            //System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().getStrItemCode() + " " + entry.getValue().getDblTaxAmt());
        }
        return 1;
    }
    
    private boolean isTaxApplicableOnItemGroup(String taxCode, String itemCode)
    {
        boolean isApplicable = false;
        try
        {
        	  StringBuilder sqlBuilder=new StringBuilder();
              sqlBuilder.setLength(0);
      	  	sqlBuilder.append ( "select a.strItemCode,a.strItemName,b.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName,d.strTaxCode,d.strApplicable "
                    + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c,tbltaxongroup d "
                    + "where a.strSubGroupCode=b.strSubGroupCode "
                    + "and b.strGroupCode=c.strGroupCode "
                    + "and c.strGroupCode=d.strGroupCode "
                    + "and a.strItemCode='" + itemCode + "' "
                    + "and d.strTaxCode='" + taxCode + "' "
                    + "and d.strApplicable='true' ");
          
    	List listItemDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
        	
    		if(listItemDtl!=null && listItemDtl.size()>0)
    		{
    		
                isApplicable = true;
            }
           
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            return isApplicable;
        }
    }

	

	
	
	
	/////////////////////////////////////////////////////////////////////////////
    public void funGenerateLinkupTextfile(ArrayList<ArrayList<String>> arrUnLinkedItemDtl, String fromDate, String toDate, String posName,String gClientName)
    {
	
	    try
	    {
	        funCreateTempFolder();
	        String filePath = System.getProperty("user.dir");
	        filePath += "/Temp/Temp_ItemUnLinkedItems.txt";
	        File textFile = new File(filePath);
	        PrintWriter pw = new PrintWriter(textFile);
	        pw.println(funPrintTextWithAlignment(" UnLinked Items ", 40, "Center"));
	        pw.println(funPrintTextWithAlignment(gClientName, 40, "Center"));
	        pw.println(funPrintTextWithAlignment(posName, 40, "Center"));
	        pw.println(" ");
	        pw.print(funPrintTextWithAlignment("FromDate:", 10, "Left"));
	        pw.print(funPrintTextWithAlignment(fromDate, 10, "Left"));
	        pw.print(funPrintTextWithAlignment("", 2, "Left"));
	        pw.print(funPrintTextWithAlignment("ToDate:", 8, "Left"));
	        pw.print(funPrintTextWithAlignment(toDate, 10, "Left"));
	        pw.println(" ");
	        pw.println("________________________________________");
	        pw.print(funPrintTextWithAlignment("ItemCode ", 15, "Left"));
	        pw.print(funPrintTextWithAlignment("ItemName", 25, "Left"));
	        pw.println(" ");
	        pw.println("________________________________________");
	        pw.println(" ");
	
	        if (arrUnLinkedItemDtl.size() > 0)
	        {
	            for (int cnt = 0; cnt < arrUnLinkedItemDtl.size(); cnt++)
	            {
	                ArrayList<String> items = arrUnLinkedItemDtl.get(cnt);
	                pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 15, "Left"));
	                pw.print(funPrintTextWithAlignment("" + items.get(1), 25, "Left"));
	                pw.println(" ");
	            }
	        }
	
	        pw.println(" ");
	        pw.println(" ");
	        pw.println(" ");
	        pw.println(" ");
	        pw.println("m");
	
	        pw.flush();
	        pw.close();
	
	       /* clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
	        if (clsGlobalVarClass.gShowBill)
	        {
	            ob.funShowTextFile(textFile, "", "");
	        }*/
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
    }
  
    public String funPrintTextWithAlignment(String text, int totalLength, String alignment)
    {
	StringBuilder sbText = new StringBuilder();
	
	if (alignment.equalsIgnoreCase("Center"))
	{
	    int textLength = text.length();
	    int totalSpace = (totalLength - textLength) / 2;
	    for (int i = 0; i < totalSpace; i++)
	    {
		sbText.append(" ");
	    }
	    sbText.append(text);
	    
	}
	
	else if (alignment.equalsIgnoreCase("Left"))
	{
	    sbText.setLength(0);
	    int textLength = text.length();
	    int totalSpace = (totalLength - textLength);
	    
	    if (totalSpace < 0)
	    {
		sbText.append(text.substring(0, totalLength));
	    }
	    else
	    {
		sbText.append(text);
		
		for (int i = 0; i < totalSpace; i++)
		{
		    sbText.append(" ");
		}
	    }
	    
	}
	else
	{
	    sbText.setLength(0);
	    int textLength = text.length();
	    int totalSpace = (totalLength - textLength);
	    
	    if (totalSpace < 0)
	    {
		sbText.append(text.substring(0, totalLength));
	    }
	    else
	    {
		for (int i = 0; i < totalSpace; i++)
		{
		    sbText.append(" ");
		}
		sbText.append(text);
	    }
	    
	}
	
	return sbText.toString();
    }


	public void funCreateTempFolder()
	{
	    try
	    {
	        String filePath = System.getProperty("user.dir");
	        File file = new File(filePath + "/Temp");
	        if (!file.exists())
	        {
	            file.mkdirs();
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	public String funGetDBBackUpPath(String clientCode)
	{
	   String strDBbckUpPath="";
		try{
			
			 String sql="select strMySQBackupFilePath from tblconfig where strClientCode='"+clientCode+"'";
			 List list=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
			 if(list.size()>0){
				 strDBbckUpPath=(String) list.get(0);	 
			 }	
		}catch(Exception e){
			e.printStackTrace();
		}
 
		 return strDBbckUpPath;
	 }
	
	public String funGetPOSName(String PosCode)
	{
		String posName="";
		try
		{
			String sql="select a.strPosName from tblposmaster a where a.strPosCode='"+PosCode+"'" ;
			List list = objBaseServiceImpl.funGetList(new StringBuilder(sql),"sql");
			if(list.size()>0){
				posName=list.get(0).toString();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return posName;
		}
	}

	 public int funBackupAndMailDB(String backupFilePath,String strClientCode,String posCode,String strPOSName,String strPOSDate) throws Exception
	    {
	        String filePath = System.getProperty("user.dir") + "\\DBBackup\\" + backupFilePath + ".sql";
	        //String filePath = System.getProperty("user.dir")+"/DBBackup/1.sql";
	        File file = new File(filePath);
	        double bytes = file.length();
	        double kilobytes = (bytes / 1024);
	        double megabytes = (kilobytes / 1024);

	        if (megabytes < 25)
	        {
	            obSendMail.funSendMail("sanguineapos@gmail.com", filePath,strClientCode,posCode,strPOSName,strPOSDate);
	        }
	        return 1;
	    }
	
	 public void funPrintBlankSpace(String printWord, BufferedWriter objBWriter, int actualPrintingSize) {
	        try {
	            int wordSize = printWord.length();
	            int availableBlankSpace = actualPrintingSize - wordSize;
	            
	            int leftSideSpace = availableBlankSpace / 2;
	            if (leftSideSpace > 0) {
	                for (int i = 0; i < leftSideSpace; i++) {
	                	objBWriter.write(" ");
	                }
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	 
	 public Map<String, clsPOSPromotionItems> funCalculatePromotions(String transType, String billFromKOTsList, String billNo, List<clsPOSBillDtl> listOfSplitedGridItems,String tableNo,String areaCode,String voucherNo,HttpServletRequest request,List<clsPOSBillSettlementBean> listItemDtl ) throws Exception
	    {
	        boolean flgPromotionOnDiscount = false, flgPromotionOnItems = false;
	        Map<String, List<clsPOSBuyPromotionItemDtl>> hmBuyPromoItems = new HashMap<String, List<clsPOSBuyPromotionItemDtl>>();
	        Map<String, List<clsPOSGetPromotionItemDtl>> hmGetPromoItems = new HashMap<String, List<clsPOSGetPromotionItemDtl>>();
	        Map<String, clsPOSPromotionItems> hmPromoItems = new HashMap<String, clsPOSPromotionItems>();
			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			String posCode = request.getSession().getAttribute("gPOSCode").toString();
			String dtPOSDate=request.getSession().getAttribute("gPOSDate").toString();
	        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Date posDateForTrans = dFormat.parse(dtPOSDate);
	        String day = funGetDayOfWeek(posDateForTrans.getDay());
	        List<String> listBillItems = new ArrayList<String>();
	        String gAreaWisePromotions=objPOSSetupUtility.funGetParameterValuePOSWise( clientCode,  posCode,  "gAreaWisePromotions");
	        StringBuilder sbPromo = new StringBuilder();

	        if (transType.equals("MakeKOT"))
	        {
	            listBillItems.clear();
	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),c.strDays"
	                    + " ,c.tmeFromTime,c.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,sum(a.dblAmount) "
	                    + " ,c.strPromotionOn,c.strGetPromoOn "
	                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
	                    + " and c.strPromoCode=d.strPromoCode "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and a.strTableNo='" + tableNo + "' ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') "
	                    + " and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " group by a.strItemCode,c.strPromoCode "
	                    + " order by a.dblAmount desc");

	           	List listBuyPromo=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listBuyPromo!=null && listBuyPromo.size()>0)
	    		{
	    			for(int i=0;i<listBuyPromo.size();i++)	
	    			{
	    			Object []objBuyPromo=(Object[])listBuyPromo.get(i);
	                listBillItems.add(objBuyPromo[1].toString());
	                double kotItemQty = Double.parseDouble(objBuyPromo[4].toString());
	                double buyPromoItemQty = Double.parseDouble(objBuyPromo[3].toString());

	                if (objBuyPromo[11].toString().equals("MenuHead"))
	                {
	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPromo[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPromo[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                        objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + Double.parseDouble(objBuyPromo[4].toString()));
	                        objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + Double.parseDouble(objBuyPromo[10].toString()));
	                        listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);

	                        //hmBuyPromoItems.put(rsBuyPromoItems.getString(1),listBuyItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPromo[0].toString(), listBuyItemDtl);
	                    }
	                }
	                else
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPromo[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPromo[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);

	                        //hmBuyPromoItems.put(rsBuyPromoItems.getString(1),listBuyItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPromo[0].toString(), listBuyItemDtl);
	                    }

	                }
	            }
	        }
	            

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblGetQty,sum(a.dblItemQuantity),c.strDays,c.tmeFromTime"
	                    + ",c.tmeToTime,a.dteDateCreated,sum(a.dblAmount),b.strPromotionOn,c.longKOTTimeBound "
	                    + " from tblitemrtemp a,tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') and c.strPromoCode=d.strPromoCode "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and a.strTableNo='" + tableNo + "' ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') "
	                    + " and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " group by a.strItemCode,c.strPromoCode "
	                    + " order by a.dblAmount desc ");
	            //System.out.println(sql);
	            List listPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listPromoItems!=null && listPromoItems.size()>0)
	    		{
	    			for(int i=0;i<listPromoItems.size();i++)	
	    			{
	    			Object []objPromoItems=(Object[])listPromoItems.get(i);
	                if (objPromoItems[10].toString().equals("PromoGroup"))
	                {
	                    sbPromo.setLength(0);
	                    String intervalTime = "INTERVAL " + objPromoItems[11].toString() + " HOUR";
	                    sbPromo.append("select sum(dblItemQuantity),sum(dblAmount) "
	                            + " from tblitemrtemp where strTableno='" + tableNo + "' "
	                            + " and dtedatecreated between (select min(dtedatecreated) from tblitemrtemp where strTableno='" + tableNo + "' ) "
	                            + " and (select date_add(min(dtedatecreated)," + intervalTime + ") from tblitemrtemp where strTableno='" + tableNo + "') "
	                            + " and strItemCode='" + objPromoItems[1].toString() + "' "
	                            + " group by strItemCode "
	                            + " order by dtedatecreated");
	                   
	                    List listPGType=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    	        	
	    	    		if(listPGType!=null && listPGType.size()>0)
	    	    		{
	    	    			for(int j=0;j<listPGType.size();j++)	
	    	    			{
	    	    			Object []objPGType=(Object[])listPGType.get(j);
	                      double getPromoItemQty = Double.parseDouble(objPGType[0].toString());
	                        double kotItemQty1 = Double.parseDouble(objPGType[0].toString());
	                        if (getPromoItemQty <= kotItemQty1)
	                        {
	                            String promoCode =objPromoItems[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                    // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	                   
	                }
	                else
	                {
	                    double getPromoItemQty =Double.parseDouble( objPromoItems[3].toString());
	                    double kotItemQty1 = Double.parseDouble( objPromoItems[4].toString());
	                    if (objPromoItems[10].toString().equals("MenuHead"))
	                    {
	                        String promoCode = objPromoItems[0].toString();
	                        List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        else
	                        {
	                            listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        if (null != listGetItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listGetItemDtl, COMPARATORGET);
	                            hmGetPromoItems.put(promoCode, listGetItemDtl);
	                        }
	                    }
	                    else
	                    {
	                        if (getPromoItemQty <= kotItemQty1)
	                        {
	                            String promoCode = objPromoItems[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));            // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	           
	            sbPromo.setLength(0);
	            sbPromo.append("select d.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),d.strDays "
	                    + " ,d.tmeFromTime,d.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,c.strDiscountType,c.dblDiscount "
	                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode  "
	                    + " and d.strPromoCode=e.strPromoCode and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and a.strTableNo='" + tableNo + "' and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') "
	                    + " and c.dblDiscount > 0 and e.strDay='" + day + "' and time(a.dteDateCreated) between e.tmeFromTime and e.tmeToTime ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and d.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" group by a.strItemCode,c.strPromoCode order by a.dblAmount desc");
	            //System.out.println(sql);
	            List listDiscountPromo=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listDiscountPromo!=null && listDiscountPromo.size()>0)
	    		{
	    			for(int j=0;j<listDiscountPromo.size();j++)	
	    			{
	    			Object []objDiscItems=(Object[])listDiscountPromo.get(j);
	                String[] arrKOTDate = objDiscItems[9].toString().split(" ");
	                String KOTDate = arrKOTDate[0].split("-")[2] + "-" + arrKOTDate[0].split("-")[1] + "-" + arrKOTDate[0].split("-")[0];
	                String KOTDateTime = KOTDate + " " + arrKOTDate[1];

	                double kotItemQty = Double.parseDouble(objDiscItems[4].toString());
	                double buyPromoItemQty =Double.parseDouble(objDiscItems[3].toString());

	                //String getItemDtl=rsDiscountPromoItems.getString(12)+"#"+rsDiscountPromoItems.getString(1)+"#Free";
	                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                objPromoItems.setItemCode(objDiscItems[1].toString());
	                objPromoItems.setPromoType("Discount");
	                objPromoItems.setPromoCode(objDiscItems[0].toString());
	                objPromoItems.setDiscType(objDiscItems[10].toString());
	                if (objDiscItems[10].toString().equals("Value"))
	                {
	                    objPromoItems.setDiscAmt(Double.parseDouble(objDiscItems[11].toString()));
	                }
	                else
	                {
	                    objPromoItems.setDiscPer(Double.parseDouble(objDiscItems[11].toString()));
	                }
	                hmPromoItems.put(objDiscItems[1].toString(), objPromoItems);
	                flgPromotionOnDiscount = true;

	            }
	    		}
	         
	        }
	        else if (transType.equals("VoidBill"))
	        {
	            String voidBillPOSCode = "", billAreaCode = "";
	            sbPromo.setLength(0);
	            sbPromo.append( "select strPOSCode,strAreaCode from tblbillhd where strBillNo='" + billNo + "' ");
	            
	            List listVoidPOSCode=objBaseServiceImpl.funGetList(sbPromo, "sql");
	            if (listVoidPOSCode.size()>0)
	            {
	            	Object []objVoidPOSCode=(Object[])listVoidPOSCode.get(0);
	                voidBillPOSCode =objVoidPOSCode[0].toString();
	                billAreaCode = objVoidPOSCode[1].toString();
	            }
	          

	            dFormat = new SimpleDateFormat("yyyy-MM-dd");
	            Date tempPOSDate = dFormat.parse(dtPOSDate);
	            String date = (tempPOSDate.getYear() + 1900) + "-" + (tempPOSDate.getMonth() + 1) + "-" + tempPOSDate.getDate();
	            listBillItems.clear();

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblQuantity) "
	                    + " ,c.strDays,c.tmeFromTime,c.tmeToTime,time(a.dteBillDate),a.dteBillDate,sum(a.dblAmount) "
	                    + " ,c.strPromotionOn,c.strGetPromoOn "
	                    + " from tblbilldtl a,tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and (c.strPOSCode='" + voidBillPOSCode + "' or c.strPOSCode='All') and c.strPromoCode=d.strPromoCode "
	                    + " and date(c.dteFromDate) <= '" + date + "' and date(c.dteToDate) >= '" + date + "' "
	                    + " and a.strBillNo='" + billNo + "' ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + billAreaCode + "'");
	            }
	            sbPromo.append(" and d.strDay='" + day + "' and time(a.dteBillDate) between d.tmeFromTime and d.tmeToTime "
	                    + " group by a.strItemCode "
	                    + " order by a.dblAmount desc");

               List listBuyPromo=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listBuyPromo!=null && listBuyPromo.size()>0)
	    		{
	    			for(int j=0;j<listBuyPromo.size();j++)	
	    			{
	    			Object []objBuyPrmo=(Object[])listBuyPromo.get(j);
	                listBillItems.add(objBuyPrmo[1].toString());

	                double kotItemQty =  Double.parseDouble(objBuyPrmo[4].toString());
	                double buyPromoItemQty =  Double.parseDouble(objBuyPrmo[3].toString());

	                if (objBuyPrmo[11].toString().equals("MenuHead"))
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPrmo[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPrmo[0].toString());
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                        objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + Double.parseDouble(objBuyPrmo[4].toString()));
	                        objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + Double.parseDouble(objBuyPrmo[10].toString()));
	                        listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPrmo[0].toString(), listBuyItemDtl);
	                    }

	                }
	                else
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPrmo[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPrmo[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmo[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmo[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmo[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmo[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmo[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmo[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);

	                        //hmBuyPromoItems.put(rsBuyPromoItems.getString(1),listBuyItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPrmo[0].toString(), listBuyItemDtl);
	                    }

	                }
	            }
	        }


	            String transactionType = "";

	            sbPromo.setLength(0);
	            sbPromo.append( "select strTransactionType from tblbillhd where strBillNo='" + billNo + "' and strPOSCode='" + voidBillPOSCode + "' "
	                    + " and strClientCode='" + posCode + "' ");
	            
	            List listBillDtl=objBaseServiceImpl.funGetList(sbPromo, "sql");
	            if (listBillDtl.size()>0)
	            {
	            	Object []objBillDtl=(Object[])listBillDtl.get(0);
	            	transactionType = objBillDtl[0].toString().split(",")[0];
	            }

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblGetQty,sum(a.dblQuantity),c.strDays,c.tmeFromTime"
	                    + ",c.tmeToTime,a.dteBillDate,sum(a.dblAmount),b.strPromotionOn,c.longKOTTimeBound "
	                    + " from tblbilldtl a,tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and (c.strPOSCode='" + voidBillPOSCode + "' or c.strPOSCode='All') "
	                    + " and c.strPromoCode=d.strPromoCode "
	                    + " and date(c.dteFromDate) <= '" + date + "' and date(c.dteToDate) >= '" + date + "' "
	                    + " and a.strBillNo='" + billNo + "' ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + billAreaCode + "'");
	            }
	            sbPromo.append(" and d.strDay='" + day + "' and time(a.dteBillDate) between d.tmeFromTime and d.tmeToTime "
	                    + " group by a.strItemCode "
	                    + " order by a.dblAmount desc");
	            //System.out.println(sql);
	          
	            List listPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listPromoItems!=null && listPromoItems.size()>0)
	    		{
	    			for(int j=0;j<listPromoItems.size();j++)	
	    			{
	    			Object []objPrmoItems=(Object[])listPromoItems.get(j);
	                if (objPrmoItems[10].toString().equals("PromoGroup"))
	                {
	                    if (!transactionType.equals("Direct Biller"))
	                    {
	                        String intervalTime = "INTERVAL " + objPrmoItems[11].toString() + " HOUR";
	                        sbPromo.setLength(0);
	                        sbPromo.append("select sum(dblItemQuantity),sum(dblAmount) "
	                                + " from tblitemrtemp where strTableno='" + tableNo + "' "
	                                + " and dtedatecreated between (select min(dtedatecreated) from tblitemrtemp where strTableno='" + tableNo + "' ) "
	                                + " and (select date_add(min(dtedatecreated)," + intervalTime + ") from tblitemrtemp where strTableno='" + tableNo + "') "
	                                + " and strItemCode='" + objPrmoItems[1].toString() + "' "
	                                + " group by strItemCode "
	                                + " order by dtedatecreated");
	                        List listPGType=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	        	
	        	    		if(listPGType!=null && listPGType.size()>0)
	        	    		{
	        	    			for(int i=0;i<listPGType.size();i++)	
	        	    			{
	        	    			Object []objPGType=(Object[])listPGType.get(i);
	                            double getPromoItemQty =Double.parseDouble(objPGType[0].toString());
	                            double kotItemQty1 = Double.parseDouble(objPGType[1].toString());
	                            if (getPromoItemQty <= kotItemQty1)
	                            {
	                                String promoCode = objPrmoItems[0].toString();
	                                List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                                if (null != hmGetPromoItems.get(promoCode))
	                                {
	                                    listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                    clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                    objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                                    objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                    objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                    objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                    listGetItemDtl.add(objGetPromoItemDtl);
	                                }
	                                else
	                                {
	                                    listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                    clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                    objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                                    objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                    objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                    objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                    listGetItemDtl.add(objGetPromoItemDtl);
	                                }
	                                if (null != listGetItemDtl)
	                                {
	                                    flgPromotionOnItems = true;
	                                    Collections.sort(listGetItemDtl, COMPARATORGET);
	                                    hmGetPromoItems.put(promoCode, listGetItemDtl);
	                                }
	                            }
	                        }
	                    }

	                    }
	                }
	                else
	                {
	                    double getPromoItemQty = Double.parseDouble(objPrmoItems[3].toString());
	                    double kotItemQty1 = Double.parseDouble(objPrmoItems[4].toString());
	                    if (objPrmoItems[10].toString().equals("MenuHead"))
	                    {
	                        String promoCode =objPrmoItems[0].toString();
	                        List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        else
	                        {
	                            listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        if (null != listGetItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listGetItemDtl, COMPARATORGET);
	                            hmGetPromoItems.put(promoCode, listGetItemDtl);
	                        }
	                    }
	                    else
	                    {
	                        if (getPromoItemQty <= kotItemQty1)
	                        {
	                            String promoCode = objPrmoItems[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	     

	            sbPromo.setLength(0);
	            sbPromo.append("select d.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblQuantity),d.strDays "
	                    + " ,d.tmeFromTime,d.tmeToTime,time(a.dteBillDate),a.dteBillDate,c.strDiscountType,c.dblDiscount "
	                    + " from tblbilldtl a,tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode  "
	                    + " and (d.strPOSCode='" + voidBillPOSCode + "' or d.strPOSCode='All') and d.strPromoCode=e.strPromoCode "
	                    + " and date(d.dteFromDate) <= '" + date + "' and date(d.dteToDate) >= '" + date + "' "
	                    + " and c.dblDiscount > 0 ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and d.strAreaCode='" + billAreaCode + "'");
	            }
	            sbPromo.append(" and e.strDay='" + day + "' and time(a.dteBillDate) between e.tmeFromTime and e.tmeToTime "
	                    + " and a.strBillNo='" + billNo + "' "
	                    + " group by a.strItemCode "
	                    + " order by a.dblAmount desc");
	            //System.out.println(sql);
	           
	            List listDisc=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listDisc!=null && listDisc.size()>0)
	    		{
	    			for(int i=0;i<listDisc.size();i++)	
	    			{
	    			Object []objDisc=(Object[])listDisc.get(i);
	                double kotItemQty =Double.parseDouble( objDisc[4].toString());
	                double buyPromoItemQty = Double.parseDouble( objDisc[3].toString());

	                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                objPromoItems.setItemCode(objDisc[1].toString());
	                objPromoItems.setPromoType("Discount");
	                objPromoItems.setPromoCode(objDisc[0].toString());
	                objPromoItems.setDiscType(objDisc[10].toString());
	                if (objDisc[10].toString().equals("Value"))
	                {
	                    objPromoItems.setDiscAmt(Double.parseDouble( objDisc[11].toString()));
	                }
	                else
	                {
	                    objPromoItems.setDiscPer(Double.parseDouble( objDisc[11].toString()));
	                }
	                hmPromoItems.put(objDisc[1].toString(), objPromoItems);
	                flgPromotionOnDiscount = true;

	            }
	        }
	           
	        }
	        else if (transType.equals("BillFromKOTs"))  // Bill From KOTs block.
	        {
	            listBillItems.clear();
	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),c.strDays"
	                    + ",c.tmeFromTime,c.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,sum(a.dblAmount) "
	                    + ",c.strPromotionOn,c.strGetPromoOn "
	                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and c.strPromoCode=d.strPromoCode and " + billFromKOTsList + ") "
	                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " group by a.strItemCode "
	                    + " order by a.dblAmount desc");
	           
	            List listBuyPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listBuyPromoItems!=null && listBuyPromoItems.size()>0)
	    		{
	    			for(int i=0;i<listBuyPromoItems.size();i++)	
	    			{
	    			Object []objBuyPrmoItm=(Object[])listBuyPromoItems.get(i);
	                listBillItems.add(objBuyPrmoItm[1].toString());
	                double kotItemQty = Double.parseDouble(objBuyPrmoItm[4].toString());
	                double buyPromoItemQty = Double.parseDouble(objBuyPrmoItm[3].toString());

	                if (objBuyPrmoItm[11].toString().equals("MenuHead"))
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPrmoItm[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPrmoItm[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                        objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + Double.parseDouble(objBuyPrmoItm[4].toString()));
	                        objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + Double.parseDouble(objBuyPrmoItm[10].toString()));
	                        listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmoItm[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmoItm[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmoItm[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmoItm[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmoItm[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmoItm[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPrmoItm[0].toString(), listBuyItemDtl);
	                    }

	                }
	                else
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPrmoItm[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPrmoItm[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmoItm[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmoItm[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmoItm[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmoItm[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmoItm[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmoItm[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPrmoItm[0].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPrmoItm[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPrmoItm[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPrmoItm[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPrmoItm[11].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPrmoItm[12].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPrmoItm[0].toString(), listBuyItemDtl);
	                    }

	                }
	            }
	        }
	            

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblGetQty,sum(a.dblItemQuantity),c.strDays,c.tmeFromTime"
	                    + ",c.tmeToTime,a.dteDateCreated,sum(a.dblAmount),b.strPromotionOn "
	                    + " from tblitemrtemp a,tblpromotiondtl b,tblpromotionmaster c, tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and c.strPromoCode=d.strPromoCode and " + billFromKOTsList + ") "
	                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
	                    + " and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') "
	                    + " and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" group by a.strItemCode order by a.dblAmount desc");
	            //System.out.println(sql);
	          
            List listPromItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	        	
	    		if(listPromItems!=null && listPromItems.size()>0)
	    		{
	    			for(int i=0;i<listPromItems.size();i++)	
	    			{
	    			Object []objPrmoItems=(Object[])listPromItems.get(i);
	                double getPromoItemQty = Double.parseDouble(objPrmoItems[3].toString());
	                double kotItemQty1 = Double.parseDouble(objPrmoItems[4].toString());
	                if (objPrmoItems[10].toString().equals("MenuHead"))
	                {
	                    String promoCode = objPrmoItems[0].toString();
	                    List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                    if (null != hmGetPromoItems.get(promoCode))
	                    {
	                        listGetItemDtl = hmGetPromoItems.get(promoCode);
	                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                        objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                        objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                        objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                        listGetItemDtl.add(objGetPromoItemDtl);
	                    }
	                    else
	                    {
	                        listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                        objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                        objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                        objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));          // Total Amount.
	                        listGetItemDtl.add(objGetPromoItemDtl);
	                    }
	                    if (null != listGetItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listGetItemDtl, COMPARATORGET);
	                        hmGetPromoItems.put(promoCode, listGetItemDtl);
	                    }
	                }
	                else
	                {
	                    if (getPromoItemQty <= kotItemQty1)
	                    {
	                        String promoCode = objPrmoItems[0].toString();
	                        List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        else
	                        {
	                            listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPrmoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPrmoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPrmoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPrmoItems[9].toString()));             // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        if (null != listGetItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listGetItemDtl, COMPARATORGET);
	                            hmGetPromoItems.put(promoCode, listGetItemDtl);
	                        }
	                    }
	                }
	            }
	        }
	          
	            sbPromo.setLength(0);
	            sbPromo.append("select d.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),d.strDays"
	                    + ",d.tmeFromTime,d.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,c.strDiscountType,c.dblDiscount "
	                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and c.strPromoCode=d.strPromoCode and " + billFromKOTsList + ") and d.strPromoCode=e.strPromoCode "
	                    + " and (a.strPOSCode=d.strPOSCode or d.strPOSCode='All') "
	                    + " and e.strDay='" + day + "' and time(a.dteDateCreated) between e.tmeFromTime and e.tmeToTime "
	                    + " and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') and c.dblDiscount > 0 ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and d.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" group by a.strItemCode order by a.dblAmount desc");
	            //System.out.println(sql);
	            List listDiscs=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    		if(listDiscs!=null && listDiscs.size()>0)
	    		{
	    			for(int i=0;i<listDiscs.size();i++)	
	    			{
	    			Object []objDisc=(Object[])listDiscs.get(i);
	                double kotItemQty = Double.parseDouble(objDisc[4].toString());
	                double buyPromoItemQty = Double.parseDouble(objDisc[3].toString());

	                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                objPromoItems.setItemCode(objDisc[1].toString());
	                objPromoItems.setPromoType("Discount");
	                objPromoItems.setPromoCode(objDisc[0].toString());
	                objPromoItems.setDiscType(objDisc[10].toString());
	                if (objDisc[10].toString().equals("Value"))
	                {
	                    objPromoItems.setDiscAmt(Double.parseDouble(objDisc[11].toString()));
	                }
	                else
	                {
	                    objPromoItems.setDiscPer(Double.parseDouble(objDisc[11].toString()));
	                }
	                hmPromoItems.put(objDisc[1].toString(), objPromoItems);
	                flgPromotionOnDiscount = true;
	                flgPromotionOnItems = false;
	            }
	        }
	          
	        }
	        else if (transType.equalsIgnoreCase("AddKOTToBill"))//AddKOTToBill
	        {
	            listBillItems.clear();
	            String sqlAppendForBillFromKOTS = "";
	            List<clsPOSBillSettlementBean> listKOTNos = new ArrayList<clsPOSBillSettlementBean>();

	            sqlAppendForBillFromKOTS = "";
	            listKOTNos =listItemDtl;
	            if (!listKOTNos.isEmpty())
	            {
	                boolean first = true;
	                for (clsPOSBillSettlementBean bean : listKOTNos)
	                {
	                    if (first)
	                    {
	                        sqlAppendForBillFromKOTS += "( strKOTNo='" + bean.getStrKOTNo() + "'";
	                        first = false;
	                    }
	                    else
	                    {
	                        sqlAppendForBillFromKOTS += " or ".concat(" strKOTNo='" + bean.getStrKOTNo() + "' ");
	                    }
	                }
	            }

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.Qty),c.strDays "
	                    + " ,d.tmeFromTime,d.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,sum(a.Amt),a.strPOSCode"
	                    + " ,c.strPromotionOn,c.strGetPromoOn "
	                    + " from "
	                    + " (select strItemCode,strItemName,dblQuantity as Qty,dblAmount as Amt ,dblDiscountAmt,dblDiscountPer,'" + posCode + "' as strPOSCode,dteBillDate as dteDateCreated  "
	                    + " from tblbilldtl where strBillNo='" + voucherNo + "' "
	                    + " union all "
	                    + " select strItemCode,strItemName,dblItemQuantity as Qty,dblAmount as Amt ,0,0,strPOSCode,dteDateCreated "
	                    + " from tblitemrtemp r where " + sqlAppendForBillFromKOTS + " )) a,"
	                    + " tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and c.strPromoCode=d.strPromoCode and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " Group By a.strItemCode "
	                    + " order by a.Amt desc; ");
	            List listBuyPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    	    if(listBuyPromoItems!=null && listBuyPromoItems.size()>0)
	    	    {
	    	    	for(int i=0;i<listBuyPromoItems.size();i++)	
	    	    	{
	    	    	Object []objBuyPromoItems=(Object[])listBuyPromoItems.get(i);
	                listBillItems.add(objBuyPromoItems[1].toString());

	                double kotItemQty = Double.parseDouble(objBuyPromoItems[4].toString());
	                double buyPromoItemQty = Double.parseDouble(objBuyPromoItems[3].toString());

	                if (objBuyPromoItems[12].toString().equals("MenuHead"))
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPromoItems[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPromoItems[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                        objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + Double.parseDouble(objBuyPromoItems[4].toString()));
	                        objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + Double.parseDouble(objBuyPromoItems[10].toString()));
	                        listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromoItems[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromoItems[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromoItems[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromoItems[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromoItems[12].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromoItems[13].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPromoItems[0].toString(), listBuyItemDtl);
	                    }

	                }
	                else
	                {

	                    List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                    if (null != hmBuyPromoItems.get(objBuyPromoItems[0].toString()))
	                    {
	                        listBuyItemDtl = hmBuyPromoItems.get(objBuyPromoItems[0].toString());

	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromoItems[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromoItems[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromoItems[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromoItems[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromoItems[12].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromoItems[13].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    else
	                    {
	                        listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                        clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                        objBuyPromoItemDtl.setItemCode(objBuyPromoItems[1].toString());           // Item Code. 
	                        objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objBuyPromoItems[3].toString()));    // Buy Promo Item Qty. 
	                        objBuyPromoItemDtl.setTotalItemQty(Double.parseDouble(objBuyPromoItems[4].toString()));       // Total Item Qty. 
	                        objBuyPromoItemDtl.setTotalAmount(Double.parseDouble(objBuyPromoItems[10].toString()));       // Total Amt. 
	                        objBuyPromoItemDtl.setBuyPromoOn(objBuyPromoItems[12].toString());
	                        objBuyPromoItemDtl.setGetPromoOn(objBuyPromoItems[13].toString());
	                        listBuyItemDtl.add(objBuyPromoItemDtl);
	                    }
	                    if (null != listBuyItemDtl)
	                    {
	                        flgPromotionOnItems = true;
	                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                        hmBuyPromoItems.put(objBuyPromoItems[0].toString(), listBuyItemDtl);
	                    }

	                }
	            }
	        }
	          
	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblGetQty,sum(a.Qty),c.strDays "
	                    + " ,d.tmeFromTime,d.tmeToTime,a.dteDateCreated,sum(a.Amt),a.strPOSCode,b.strPromotionOn,c.longKOTTimeBound "
	                    + " from "
	                    + " (select strItemCode,strItemName,dblQuantity as Qty,dblAmount as Amt ,dblDiscountAmt,dblDiscountPer,'" + posCode + "' as strPOSCode,dteBillDate as dteDateCreated  "
	                    + " from tblbilldtl where strBillNo='" + voucherNo + "' "
	                    + " union all "
	                    + " select strItemCode,strItemName,dblItemQuantity as Qty,dblAmount as Amt ,0,0,strPOSCode,dteDateCreated "
	                    + " from tblitemrtemp r where " + sqlAppendForBillFromKOTS + " )) a"
	                    + " ,tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                    + " where a.strItemCode=b.strPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + " and c.strPromoCode=d.strPromoCode and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
	                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                    + " and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and c.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
	                    + " Group By a.strItemCode "
	                    + " order by a.Amt desc; ");
	            List listPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    	    if(listPromoItems!=null && listPromoItems.size()>0)
	    	    {
	    	    	for(int i=0;i<listPromoItems.size();i++)	
	    	    	{
	    	    	Object []objPromoItems=(Object[])listPromoItems.get(i);
	                if (objPromoItems[11].toString().equals("PromoGroup"))
	                {
	                    String intervalTime = "INTERVAL " + objPromoItems[12].toString() + " HOUR";
	                    sbPromo.setLength(0);
	                    sbPromo.append("select sum(dblItemQuantity),sum(dblAmount) "
	                            + " from tblitemrtemp where strTableno='" + tableNo + "' "
	                            + " and dtedatecreated between (select min(dtedatecreated) from tblitemrtemp where strTableno='" + tableNo + "' ) "
	                            + " and (select date_add(min(dtedatecreated)," + intervalTime + ") from tblitemrtemp where strTableno='" + tableNo + "') "
	                            + " and strItemCode='" + objPromoItems[1].toString() + "' "
	                            + " group by strItemCode "
	                            + " order by dtedatecreated");
	                    List listPGType=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    	    	    if(listPGType!=null && listPGType.size()>0)
	    	    	    {
	    	    	    	for(int j=0;j<listPGType.size();j++)	
	    	    	    	{
	    	    	    	Object []objPGType=(Object[])listPGType.get(i);
	                        double getPromoItemQty =Double.parseDouble(objPGType[0].toString());
	                        double kotItemQty1 = Double.parseDouble(objPGType[1].toString());
	                        if (getPromoItemQty <= kotItemQty1)
	                        {
	                            String promoCode = objPromoItems[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	                   
	                }
	                else
	                {
	                    double getPromoItemQty =Double.parseDouble( objPromoItems[3].toString());
	                    double kotItemQty1 =Double.parseDouble( objPromoItems[4].toString());
	                    if (objPromoItems[11].toString().equals("MenuHead"))
	                    {
	                        String promoCode = objPromoItems[0].toString();
	                        List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        else
	                        {
	                            listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        if (null != listGetItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listGetItemDtl, COMPARATORGET);
	                            hmGetPromoItems.put(promoCode, listGetItemDtl);
	                        }
	                    }
	                    else
	                    {
	                        if (getPromoItemQty <= kotItemQty1)
	                        {
	                            String promoCode =objPromoItems[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(objPromoItems[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPromoItems[3].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPromoItems[4].toString()));           // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPromoItems[9].toString()));           // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	            

	            sbPromo.setLength(0);
	            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.Qty),d.strDays "
	                    + ",d.tmeFromTime,d.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,c.strDiscountType,c.dblDiscount ,a.strPOSCode "
	                    + "from "
	                    + "(select strItemCode,strItemName,dblQuantity as Qty,dblAmount as Amt ,dblDiscountAmt,dblDiscountPer,'" + posCode + "' as strPOSCode,dteBillDate as dteDateCreated  "
	                    + "from tblbilldtl where strBillNo='" + voucherNo + "' "
	                    + "union all  "
	                    + "select strItemCode,strItemName,dblItemQuantity as Qty,dblAmount as Amt ,0,0,strPOSCode,dteDateCreated  "
	                    + "from tblitemrtemp r where " + sqlAppendForBillFromKOTS + " )) a,tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e  "
	                    + "where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
	                    + "and c.strPromoCode=d.strPromoCode and (a.strPOSCode=d.strPOSCode or d.strPOSCode='All') "
	                    + " and d.strPromoCode=e.strPromoCode "
	                    + "and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
	                    + "and (a.strPOSCode='" +posCode+ "'  or  a.strPOSCode='All') and c.dblDiscount > 0  "
	                    + "and e.strDay='" + day + "' and time(a.dteDateCreated) between e.tmeFromTime and e.tmeToTime ");
	            if (gAreaWisePromotions.equalsIgnoreCase("Y"))
	            {
	                sbPromo.append("and d.strAreaCode='" + areaCode + "'");
	            }
	            sbPromo.append(" Group By a.strItemCode order by a.Amt desc; ");
	            //System.out.println(sql);
	            List listPGType=objBaseServiceImpl.funGetList(sbPromo, "sql");
	    	    if(listPGType!=null && listPGType.size()>0)
	    	    {
	    	    	for(int j=0;j<listPGType.size();j++)	
	    	    	{
	    	    	Object []objPGType=(Object[])listPGType.get(j);
	                double kotItemQty = Double.parseDouble(objPGType[4].toString());
	                double buyPromoItemQty = Double.parseDouble(objPGType[3].toString());

	                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                objPromoItems.setItemCode(objPGType[1].toString());
	                objPromoItems.setPromoType("Discount");
	                objPromoItems.setPromoCode(objPGType[0].toString());
	                objPromoItems.setDiscType(objPGType[10].toString());
	                if (objPGType[10].toString().equals("Value"))
	                {
	                    objPromoItems.setDiscAmt(Double.parseDouble(objPGType[11].toString()));
	                }
	                else
	                {
	                    objPromoItems.setDiscPer(Double.parseDouble(objPGType[11].toString()));
	                }
	                hmPromoItems.put(objPGType[1].toString(), objPromoItems);
	                flgPromotionOnDiscount = true;

	            }
	    	    }

	        }
	        else if (transType.equals("SplitBill"))
	        {
	            listBillItems.clear();

	            Date dt = new Date();
	            String currTime = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	            String currentDate = (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
	            String transDateTime = currentDate + " " + currTime;
//	            String posDate = clsGlobalVarClass.getPOSDateForTransaction().split(" ")[0];

	            for (clsPOSBillDtl objItemDtl : listOfSplitedGridItems)
	            {
	                sbPromo.setLength(0);
	                sbPromo.append("select c.strPromoCode,b.strBuyPromoItemCode,b.dblBuyItemQty,c.strDays"
	                        + ",c.tmeFromTime,c.tmeToTime,c.strPromotionOn,c.strGetPromoOn "
	                        + " from tblbuypromotiondtl b,tblpromotionmaster c "
	                        + " where b.strPromoCode=c.strPromoCode "
	                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                        + " and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') "
	                        + " and b.strBuyPromoItemCode='" + objItemDtl.getStrItemCode() + "'");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and c.strAreaCode='" + areaCode + "' ");
	                }

	                List listSplitBillItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
    	    	    if(listSplitBillItems!=null && listSplitBillItems.size()>0)
    	    	    {
    	    	    	Object []objSplitBillItems=(Object[])listSplitBillItems.get(0);
	                    listBillItems.add(objSplitBillItems[1].toString());
	                    if (funCheckDayForPromotion(objSplitBillItems[3].toString(), objSplitBillItems[0].toString(), transDateTime, currentDate))
	                    {
	                        double billItemQty = objItemDtl.getDblQuantity();
	                        double buyPromoItemQty =Double.parseDouble(objSplitBillItems[2].toString());

	                        if (objSplitBillItems[6].toString().equals("MenuHead"))
	                        {
	                            List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                            if (null != hmBuyPromoItems.get(objSplitBillItems[0].toString()))
	                            {
	                                listBuyItemDtl = hmBuyPromoItems.get(objSplitBillItems[0].toString());

	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                                objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + objItemDtl.getDblQuantity());
	                                objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + objItemDtl.getDblAmount());
	                                listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                            }
	                            else
	                            {
	                                listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                                objBuyPromoItemDtl.setItemCode(objSplitBillItems[1].toString());           // Item Code. 
	                                objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));    // Buy Promo Item Qty. 
	                                objBuyPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());          // Total Item Qty. 
	                                objBuyPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());           // Total Amt. 
	                                objBuyPromoItemDtl.setBuyPromoOn(objSplitBillItems[6].toString());
	                                objBuyPromoItemDtl.setGetPromoOn(objSplitBillItems[7].toString());
	                                listBuyItemDtl.add(objBuyPromoItemDtl);
	                            }
	                            if (null != listBuyItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                                hmBuyPromoItems.put(objSplitBillItems[0].toString(), listBuyItemDtl);
	                            }
	                        }
	                        else
	                        {
	                            List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                            if (null != hmBuyPromoItems.get(objSplitBillItems[0].toString()))
	                            {
	                                listBuyItemDtl = hmBuyPromoItems.get(objSplitBillItems[0].toString());
	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                                objBuyPromoItemDtl.setItemCode(objSplitBillItems[1].toString());           // Item Code. 
	                                objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));    // Buy Promo Item Qty. 
	                                objBuyPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());          // Total Item Qty. 
	                                objBuyPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());           // Total Amt. 
	                                objBuyPromoItemDtl.setBuyPromoOn(objSplitBillItems[6].toString());
	                                objBuyPromoItemDtl.setGetPromoOn(objSplitBillItems[7].toString());
	                                listBuyItemDtl.add(objBuyPromoItemDtl);
	                            }
	                            else
	                            {
	                                listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                                objBuyPromoItemDtl.setItemCode(objSplitBillItems[1].toString());           // Item Code. 
	                                objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));    // Buy Promo Item Qty. 
	                                objBuyPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());          // Total Item Qty. 
	                                objBuyPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());           // Total Amt. 
	                                objBuyPromoItemDtl.setBuyPromoOn(objSplitBillItems[6].toString());
	                                objBuyPromoItemDtl.setGetPromoOn(objSplitBillItems[7].toString());
	                                listBuyItemDtl.add(objBuyPromoItemDtl);
	                            }
	                            if (null != listBuyItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                                hmBuyPromoItems.put(objSplitBillItems[0].toString(), listBuyItemDtl);
	                            }
	                        }
	                    }
	                }
	               

	                sbPromo.setLength(0);
	                sbPromo.append("select c.strPromoCode,b.strPromoItemCode,b.dblGetQty,c.strDays,c.tmeFromTime,c.tmeToTime"
	                        + ",b.strPromotionOn,c.longKOTTimeBound "
	                        + " from tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
	                        + " where b.strPromoCode=c.strPromoCode and b.strPromoItemCode='" + objItemDtl.getStrItemCode() + "' "
	                        + " and c.strPromoCode=d.strPromoCode "
	                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                        + " and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') ");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and c.strAreaCode='" + areaCode + "' ");
	                }
	                 listSplitBillItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
    	    	    if(listSplitBillItems!=null && listSplitBillItems.size()>0)
    	    	    {
    	    	    	Object []objSplitBillItems=(Object[])listSplitBillItems.get(0);
	                    listBillItems.add(objSplitBillItems[1].toString());
	                    if (funCheckDayForPromotion(objSplitBillItems[3].toString(), objSplitBillItems[0].toString(), transDateTime, currentDate))
	                    {
	                        if (objSplitBillItems[6].toString().equals("PromoGroup"))
	                        {
	                            String intervalTime = "INTERVAL " + objSplitBillItems[7].toString() + " HOUR";
	                            sbPromo.setLength(0);
	                            sbPromo.append("select sum(dblItemQuantity),sum(dblAmount) "
	                                    + " from tblitemrtemp where strTableno='" + tableNo + "' "
	                                    + " and dtedatecreated between (select min(dtedatecreated) from tblitemrtemp where strTableno='" + tableNo + "' ) "
	                                    + " and (select date_add(min(dtedatecreated)," + intervalTime + ") from tblitemrtemp where strTableno='" + tableNo + "') "
	                                    + " and strItemCode='" + objSplitBillItems[1].toString() + "' "
	                                    + " group by strItemCode "
	                                    + " order by dtedatecreated ");
	                            List listPGType=objBaseServiceImpl.funGetList(sbPromo, "sql");
	            	    	    if(listPGType!=null && listPGType.size()>0)
	            	    	    {
	            	    	    	Object []objPGType=(Object[])listPGType.get(0);   
	            	    	    	double getPromoItemQty =Double.parseDouble(objPGType[0].toString());
	                                double kotItemQty1 = Double.parseDouble(objPGType[1].toString());
	                                if (getPromoItemQty <= kotItemQty1)
	                                {
	                                    String promoCode =objSplitBillItems[0].toString();
	                                    List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                                    if (null != hmGetPromoItems.get(promoCode))
	                                    {
	                                        listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                        objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                        objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                        objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                        listGetItemDtl.add(objGetPromoItemDtl);
	                                    }
	                                    else
	                                    {
	                                        listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                        objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objPGType[0].toString()));               // Get Promo Item Qty.
	                                        objGetPromoItemDtl.setTotalItemQty(Double.parseDouble(objPGType[0].toString()));                  // Total Item Qty.
	                                        objGetPromoItemDtl.setTotalAmount(Double.parseDouble(objPGType[1].toString()));                   // Total Amount.
	                                        listGetItemDtl.add(objGetPromoItemDtl);
	                                    }
	                                    if (null != listGetItemDtl)
	                                    {
	                                        flgPromotionOnItems = true;
	                                        Collections.sort(listGetItemDtl, COMPARATORGET);
	                                        hmGetPromoItems.put(promoCode, listGetItemDtl);
	                                    }
	                                }
	                            }
	                        }
	                            
	                        }
	                        else
	                        {
	                            double billItemQty = objItemDtl.getDblQuantity();
	                            double getPromoItemQty = Double.parseDouble(objSplitBillItems[2].toString());
	                            if (objSplitBillItems[6].toString().equals("MenuHead"))
	                            {
	                                String promoCode = objSplitBillItems[0].toString();
	                                List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                                if (null != hmGetPromoItems.get(promoCode))
	                                {
	                                    listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                    clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                    objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                    objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));        // Get Promo Item Qty.
	                                    objGetPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());              // Total Item Qty.
	                                    objGetPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());               // Total Amount.
	                                    listGetItemDtl.add(objGetPromoItemDtl);
	                                }
	                                else
	                                {
	                                    listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                    clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                    objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                    objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));        // Get Promo Item Qty.
	                                    objGetPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());              // Total Item Qty.
	                                    objGetPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());               // Total Amount.
	                                    listGetItemDtl.add(objGetPromoItemDtl);
	                                }
	                                if (null != listGetItemDtl)
	                                {
	                                    flgPromotionOnItems = true;
	                                    Collections.sort(listGetItemDtl, COMPARATORGET);
	                                    hmGetPromoItems.put(promoCode, listGetItemDtl);
	                                }
	                            }
	                            else
	                            {
	                                if (getPromoItemQty <= billItemQty)
	                                {
	                                    String promoCode = objSplitBillItems[0].toString();
	                                    List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                                    if (null != hmGetPromoItems.get(promoCode))
	                                    {
	                                        listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                        objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));        // Get Promo Item Qty.
	                                        objGetPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());              // Total Item Qty.
	                                        objGetPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());               // Total Amount.
	                                        listGetItemDtl.add(objGetPromoItemDtl);
	                                    }
	                                    else
	                                    {
	                                        listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                        clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                        objGetPromoItemDtl.setItemCode(objSplitBillItems[1].toString());               // Get Item Code
	                                        objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(objSplitBillItems[2].toString()));        // Get Promo Item Qty.
	                                        objGetPromoItemDtl.setTotalItemQty(objItemDtl.getDblQuantity());              // Total Item Qty.
	                                        objGetPromoItemDtl.setTotalAmount(objItemDtl.getDblAmount());               // Total Amount.
	                                        listGetItemDtl.add(objGetPromoItemDtl);
	                                    }
	                                    if (null != listGetItemDtl)
	                                    {
	                                        flgPromotionOnItems = true;
	                                        Collections.sort(listGetItemDtl, COMPARATORGET);
	                                        hmGetPromoItems.put(promoCode, listGetItemDtl);
	                                    }
	                                }
	                            }
	                        }
	                    }
	                
	             

	                sbPromo.setLength(0);
	                sbPromo.append("select d.strPromoCode,b.dblBuyItemQty,d.strDays,d.tmeFromTime,d.tmeToTime,c.strDiscountType"
	                        + ",c.dblDiscount,b.strBuyPromoItemCode "
	                        + " from tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d "
	                        + " where b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode "
	                        + " and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' and c.dblDiscount > 0 "
	                        + " and b.strBuyPromoItemCode='" + objItemDtl.getStrItemCode() + "' ");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and d.strAreaCode='" + areaCode + "' ");
	                }
	                //System.out.println(sql);
	                List listDiscountPromoItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
		    	    if(listDiscountPromoItems!=null && listDiscountPromoItems.size()>0)
		    	    {
		    	    	for(int j=0;j<listDiscountPromoItems.size();j++)	
		    	    	{
		    	    	Object []objDiscountPromoItems=(Object[])listDiscountPromoItems.get(j);
	                    if (funCheckDayForPromotion(objDiscountPromoItems[2].toString(), objDiscountPromoItems[0].toString(), transDateTime, currentDate))
	                    {
	                        double billItemQty = objItemDtl.getDblQuantity();
	                        double buyPromoItemQty = Double.parseDouble(objDiscountPromoItems[1].toString());
	                        if (buyPromoItemQty <= billItemQty)
	                        {
	                            clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                            objPromoItems.setItemCode(objDiscountPromoItems[7].toString());
	                            objPromoItems.setPromoType("Discount");
	                            objPromoItems.setPromoCode(objDiscountPromoItems[0].toString());
	                            objPromoItems.setDiscType(objDiscountPromoItems[5].toString());
	                            if (objDiscountPromoItems[5].toString().equals("Value"))
	                            {
	                                objPromoItems.setDiscAmt(Double.parseDouble(objDiscountPromoItems[6].toString()));
	                            }
	                            else
	                            {
	                                objPromoItems.setDiscPer(Double.parseDouble(objDiscountPromoItems[6].toString()));
	                            }
	                            hmPromoItems.put(objDiscountPromoItems[7].toString(), objPromoItems);
	                            flgPromotionOnDiscount = true;
	                        }
	                    }
	                }
	            }
	              ;
	            }
	        }
	        else   // Direct Biller
	        {
	            listBillItems.clear();
	            Date dt = new Date();
	            //String currTime = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	            String hours = String.valueOf(dt.getHours());
	            String minutes = String.valueOf(dt.getMinutes());
	            String seconds = String.valueOf(dt.getSeconds());
	            if (hours.length() == 1)
	            {
	                hours = "0" + hours;
	            }
	            if (minutes.length() == 1)
	            {
	                minutes = "0" + minutes;
	            }
	            if (seconds.length() == 1)
	            {
	                seconds = "0" + seconds;
	            }
	            listBillItems.clear();

	            String currTime = hours + ":" + minutes + ":" + seconds;
	         
	            
	            List<clsPOSBillSettlementBean> listDirectBillerItemDtl = listItemDtl;
	            String gDirectAreaCode=objPOSSetupUtility.funGetParameterValuePOSWise( clientCode,  posCode,  "gDirectAreaCode");
	            for (clsPOSBillSettlementBean objDirectBillerItems : listDirectBillerItemDtl)
	            {
	                sbPromo.setLength(0);
	                sbPromo.append("select c.strPromoCode,b.strBuyPromoItemCode,b.dblBuyItemQty,c.strDays"
	                        + ",c.tmeFromTime,c.tmeToTime,c.strPromotionOn,c.strGetPromoOn "
	                        + " from tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl e "
	                        + " where b.strPromoCode=c.strPromoCode and c.strPromoCode=e.strPromoCode "
	                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                        + " and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') "
	                        + " and b.strBuyPromoItemCode='" + objDirectBillerItems.getStrItemCode() + "' "
	                        + " and e.strDay='" + day + "' ");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and c.strAreaCode='" + gDirectAreaCode + "' ");
	                }
	                sbPromo.append(" AND TIME(e.tmeFromTime) <='" + currTime + "' "//added
	                        + " AND TIME(e.tmeToTime) >='" + currTime + "' ");//added
	                List listDirectBillerItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
		    	    if(listDirectBillerItems!=null && listDirectBillerItems.size()>0)
		    	    {
		    	    	for(int j=0;j<listDirectBillerItems.size();j++)	
		    	    	{
		    	    	Object []obj=(Object[])listDirectBillerItems.get(j);
	                    listBillItems.add(objDirectBillerItems.getStrItemCode());
	                    double billItemQty = objDirectBillerItems.getDblQuantity();
	                    double buyPromoItemQty = Double.parseDouble(obj[2].toString());

	                    if (obj[6].toString().equals("MenuHead"))
	                    {
	                        List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                        if (null != hmBuyPromoItems.get(obj[0].toString()))
	                        {
	                            listBuyItemDtl = hmBuyPromoItems.get(obj[0].toString());

	                            clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                            objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + objDirectBillerItems.getDblQuantity());
	                            objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + objDirectBillerItems.getDblAmount());
	                            listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                        }
	                        else
	                        {
	                            listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                            clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                            objBuyPromoItemDtl.setItemCode(obj[1].toString());           // Item Code. 
	                            objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(obj[2].toString()));    // Buy Promo Item Qty. 
	                            objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());          // Total Item Qty. 
	                            objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());           // Total Amt. 
	                            objBuyPromoItemDtl.setBuyPromoOn(obj[6].toString());
	                            objBuyPromoItemDtl.setGetPromoOn(obj[7].toString());
	                            listBuyItemDtl.add(objBuyPromoItemDtl);
	                        }
	                        if (null != listBuyItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                            hmBuyPromoItems.put(obj[0].toString(), listBuyItemDtl);
	                        }
	                    }
	                    else
	                    {
	                        List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = null;
	                        if (null != hmBuyPromoItems.get(obj[0].toString()))
	                        {
	                            listBuyItemDtl = hmBuyPromoItems.get(obj[0].toString());

	                            if (obj[6].toString().equals("MenuHead"))
	                            {
	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
	                                objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + objDirectBillerItems.getDblQuantity());
	                                objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + objDirectBillerItems.getDblAmount());
	                                listBuyItemDtl.set(0, objBuyPromoItemDtl);
	                            }
	                            else
	                            {
	                                clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                                objBuyPromoItemDtl.setItemCode(obj[1].toString());           // Item Code. 
	                                objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(obj[2].toString()));    // Buy Promo Item Qty. 
	                                objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());          // Total Item Qty. 
	                                objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());           // Total Amt. 
	                                objBuyPromoItemDtl.setBuyPromoOn(obj[6].toString());
	                                objBuyPromoItemDtl.setGetPromoOn(obj[7].toString());
	                                listBuyItemDtl.add(objBuyPromoItemDtl);
	                            }
	                        }
	                        else
	                        {
	                            listBuyItemDtl = new ArrayList<clsPOSBuyPromotionItemDtl>();
	                            clsPOSBuyPromotionItemDtl objBuyPromoItemDtl = new clsPOSBuyPromotionItemDtl();
	                            objBuyPromoItemDtl.setItemCode(obj[1].toString());           // Item Code. 
	                            objBuyPromoItemDtl.setBuyPromoItemQty(Double.parseDouble(obj[2].toString()));    // Buy Promo Item Qty. 
	                            objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());          // Total Item Qty. 
	                            objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());           // Total Amt. 
	                            objBuyPromoItemDtl.setBuyPromoOn(obj[6].toString());
	                            objBuyPromoItemDtl.setGetPromoOn(obj[7].toString());
	                            listBuyItemDtl.add(objBuyPromoItemDtl);
	                        }
	                        if (null != listBuyItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listBuyItemDtl, COMPARATORBUY);
	                            hmBuyPromoItems.put(obj[0].toString(), listBuyItemDtl);
	                        }
	                    }
	                }
	            }
	               
	            }

	            for (clsPOSBillSettlementBean objDirectBillerItems : listDirectBillerItemDtl)
	            {
	                sbPromo.setLength(0);
	                sbPromo.append("select c.strPromoCode,b.strPromoItemCode,b.dblGetQty,c.strDays,c.tmeFromTime,c.tmeToTime"
	                        + ",b.strPromotionOn "
	                        + " from tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl e "
	                        + " where b.strPromoCode=c.strPromoCode and b.strPromoItemCode='" + objDirectBillerItems.getStrItemCode() + "'"
	                        + " and c.strPromoCode=e.strPromoCode "
	                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
	                        + " and (c.strPOSCode='" + posCode+ "' or c.strPOSCode='All') ");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and c.strAreaCode='" + gDirectAreaCode+ "' ");
	                }
	                sbPromo.append(" and e.strDay='" + day + "' AND TIME(e.tmeFromTime) <='" + currTime + "' AND TIME(e.tmeToTime) >='" + currTime + "' ");
	                List listDirectBillerItems=objBaseServiceImpl.funGetList(sbPromo, "sql");
		    	    if(listDirectBillerItems!=null && listDirectBillerItems.size()>0)
		    	    {
		    	    	for(int j=0;j<listDirectBillerItems.size();j++)	
		    	    	{
		    	    	Object []obj=(Object[])listDirectBillerItems.get(j);
	                    double billItemQty = objDirectBillerItems.getDblQuantity();
	                    double getPromoItemQty = Double.parseDouble(obj[2].toString());
	                    if (obj[6].toString().equals("MenuHead"))
	                    {
	                        String promoCode = obj[0].toString();
	                        List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(obj[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(obj[2].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());              // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());               // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        else
	                        {
	                            listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                            clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                            objGetPromoItemDtl.setItemCode(obj[1].toString());               // Get Item Code
	                            objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(obj[2].toString()));        // Get Promo Item Qty.
	                            objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());              // Total Item Qty.
	                            objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());               // Total Amount.
	                            listGetItemDtl.add(objGetPromoItemDtl);
	                        }
	                        if (null != listGetItemDtl)
	                        {
	                            flgPromotionOnItems = true;
	                            Collections.sort(listGetItemDtl, COMPARATORGET);
	                            hmGetPromoItems.put(promoCode, listGetItemDtl);
	                        }
	                    }
	                    else
	                    {
	                        if (getPromoItemQty <= billItemQty)
	                        {
	                            String promoCode = obj[0].toString();
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = null;
	                            if (null != hmGetPromoItems.get(promoCode))
	                            {
	                                listGetItemDtl = hmGetPromoItems.get(promoCode);
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(obj[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble( obj[2].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());              // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());               // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            else
	                            {
	                                listGetItemDtl = new ArrayList<clsPOSGetPromotionItemDtl>();
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtl = new clsPOSGetPromotionItemDtl();
	                                objGetPromoItemDtl.setItemCode(obj[1].toString());               // Get Item Code
	                                objGetPromoItemDtl.setGetPromoItemQty(Double.parseDouble(obj[2].toString()));        // Get Promo Item Qty.
	                                objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getDblQuantity());              // Total Item Qty.
	                                objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getDblAmount());               // Total Amount.
	                                listGetItemDtl.add(objGetPromoItemDtl);
	                            }
	                            if (null != listGetItemDtl)
	                            {
	                                flgPromotionOnItems = true;
	                                Collections.sort(listGetItemDtl, COMPARATORGET);
	                                hmGetPromoItems.put(promoCode, listGetItemDtl);
	                            }
	                        }
	                    }
	                }
	            }
	               
	            }
	            for (clsPOSBillSettlementBean objDirectBillerItems : listDirectBillerItemDtl)
	            {
	                sbPromo.setLength(0);
	                sbPromo.append("select d.strPromoCode,b.dblBuyItemQty,d.strDays,d.tmeFromTime,d.tmeToTime,c.strDiscountType"
	                        + ",c.dblDiscount,b.strBuyPromoItemCode "
	                        + " from tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
	                        + " where b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode and d.strPromoCode=e.strPromoCode "
	                        + " and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
	                        + " and c.dblDiscount > 0 and d.strPromotionOn!='BillAmount' ");
	                if (gAreaWisePromotions.equalsIgnoreCase("Y"))
		            {
	                    sbPromo.append(" and d.strAreaCode='" + gDirectAreaCode + "' ");
	                }
	                sbPromo.append(" and b.strBuyPromoItemCode='" + objDirectBillerItems.getStrItemCode() + "' and e.strDay='" + day + "' "
	                        + " AND TIME(e.tmeFromTime) <='" + currTime + "' AND TIME(e.tmeToTime) >='" + currTime + "' ");
	                //System.out.println(sql);
	                List listDisc=objBaseServiceImpl.funGetList(sbPromo, "sql");
		    	    if(listDisc!=null && listDisc.size()>0)
		    	    {
		    	    	for(int j=0;j<listDisc.size();j++)	
		    	    	{
		    	    	Object []objDisc=(Object[])listDisc.get(j);
	                    double billItemQty = objDirectBillerItems.getDblQuantity();
	                    double buyPromoItemQty =Double.parseDouble(objDisc[1].toString());
	                    if (buyPromoItemQty <= billItemQty)
	                    {
	                        clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                        objPromoItems.setItemCode(objDisc[7].toString());
	                        objPromoItems.setPromoType("Discount");
	                        objPromoItems.setPromoCode(objDisc[0].toString());
	                        objPromoItems.setDiscType(objDisc[5].toString());
	                        if (objDisc[5].toString().equals("Value"))
	                        {
	                            objPromoItems.setDiscAmt(Double.parseDouble(objDisc[6].toString()));
	                        }
	                        else
	                        {
	                            objPromoItems.setDiscPer(Double.parseDouble(objDisc[6].toString()));
	                        }
	                        hmPromoItems.put(objDisc[7].toString(), objPromoItems);
	                        flgPromotionOnDiscount = true;
	                    }
	                }
	            }
	              
	            }
	        }

	        sbPromo = null;

	        //if (!flgPromotionOnDiscount)
	        if (flgPromotionOnItems)
	        {
	            //Start loop on Buy Promotion items map     
	            for (Map.Entry<String, List<clsPOSBuyPromotionItemDtl>> entry : hmBuyPromoItems.entrySet())
	            {
	                String promoCode = entry.getKey();
	                List<clsPOSBuyPromotionItemDtl> listBuyItemDtl = entry.getValue();
	                double totalBuyQty = 0;
	                for (clsPOSBuyPromotionItemDtl objBuyPromoItemDtl : listBuyItemDtl)
	                {
	                    double buyQty = objBuyPromoItemDtl.getBuyPromoItemQty();   // Buy Qty defined in buy promotion master.
	                    totalBuyQty = objBuyPromoItemDtl.getTotalItemQty();        // Buy Qty defined in buy promotion master.

	                    if (objBuyPromoItemDtl.getBuyPromoOn().equals("Item") && objBuyPromoItemDtl.getGetPromoOn().equals("PromoGroup"))
	                    {
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            for (clsPOSGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
	                            {
	                                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                                objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
	                                objPromoItems.setPromoType("ItemWise");
	                                objPromoItems.setPromoCode(promoCode);
	                                objPromoItems.setDiscType("");
	                                objPromoItems.setDiscAmt(0);
	                                objPromoItems.setDiscPer(0);
	                                objPromoItems.setFreeItemQty(objGetPromoItemDtl.getTotalItemQty());

	                                hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
	                            }
	                        }
	                    }
	                    else // For Menuheads
	                    {
	                        if (null != hmGetPromoItems.get(promoCode))
	                        {
	                            int freeQty = 0, checkFreeQty = 0, totalFreeQty = 0;
	                            List<clsPOSGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
	                            if (listGetItemDtl.size() > 0)
	                            {
	                                clsPOSGetPromotionItemDtl objGetPromoItemDtlTemp = listGetItemDtl.get(0);
	                            }
	                            for (clsPOSGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
	                            {
	                                double getQty = objGetPromoItemDtl.getGetPromoItemQty();
	                                double promoQty = buyQty + getQty;
	                                if (listBillItems.contains(objGetPromoItemDtl.getItemCode()))
	                                {
	                                    if (getQty > buyQty)
	                                    {
	                                        int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
	                                        for (int cn = 0; cn < totalBuyQty; cn++)
	                                        {
	                                            if (totalGetQty >= promoQty)
	                                            {
	                                                freeQty += getQty;
	                                                totalGetQty -= getQty;
	                                            }
	                                            else
	                                            {
	                                                break;
	                                            }
	                                        }
	                                    }
	                                    else
	                                    {
	                                        freeQty = (int) totalBuyQty / (int) promoQty;
	                                    }
	                                }
	                                else
	                                {
	                                    if (getQty > buyQty)
	                                    {
	                                        int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
	                                        for (int cn = 0; cn < totalBuyQty; cn++)
	                                        {
	                                            if (totalGetQty > 0)
	                                            {
	                                                freeQty += getQty;
	                                                totalGetQty -= getQty;
	                                            }
	                                            else
	                                            {
	                                                break;
	                                            }
	                                        }
	                                    }
	                                    else
	                                    {
	                                        freeQty = (int) totalBuyQty / (int) buyQty;
	                                    }
	                                }
	                                freeQty = freeQty - totalFreeQty;

	                                clsPOSPromotionItems objPromoItems = new clsPOSPromotionItems();
	                                objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
	                                objPromoItems.setPromoType("ItemWise");
	                                objPromoItems.setPromoCode(promoCode);
	                                objPromoItems.setDiscType("");
	                                objPromoItems.setDiscAmt(0);
	                                objPromoItems.setDiscPer(0);

	                                int itemQty = (int) objGetPromoItemDtl.getTotalItemQty();
	                                if (itemQty < freeQty)
	                                {
	                                    checkFreeQty = freeQty - itemQty;
	                                    totalFreeQty = totalFreeQty + itemQty;
	                                    objPromoItems.setFreeItemQty(itemQty);
	                                }
	                                else
	                                {
	                                    totalFreeQty = totalFreeQty + freeQty;
	                                    objPromoItems.setFreeItemQty(freeQty);
	                                    checkFreeQty = 0;
	                                }
	                                if (objPromoItems.getFreeItemQty() > 0)
	                                {
	                                    hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
	                                }

	                                if (checkFreeQty < 1)
	                                {
	                                    break;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        return hmPromoItems;
	    }
	    
		 private String funGetDayOfWeek(int day)
		    {
		        String dayOfWeek = "";
		        switch (day)
		        {
		            case 0:
		                dayOfWeek = "Sunday";
		                break;

		            case 1:
		                dayOfWeek = "Monday";
		                break;

		            case 2:
		                dayOfWeek = "Tuesday";
		                break;

		            case 3:
		                dayOfWeek = "Wednesday";
		                break;

		            case 4:
		                dayOfWeek = "Thursday";
		                break;

		            case 5:
		                dayOfWeek = "Friday";
		                break;

		            case 6:
		                dayOfWeek = "Saturday";
		                break;
		        }

		        return dayOfWeek;
		    }

		 
		 private static Comparator<clsPOSBuyPromotionItemDtl> COMPARATORBUY = new Comparator<clsPOSBuyPromotionItemDtl>()
				   {
					 // This is where the sorting happens.
					  public int compare(clsPOSBuyPromotionItemDtl o1, clsPOSBuyPromotionItemDtl o2)
						{
						   return (int) (o2.getTotalAmount() - o1.getTotalAmount());
					    }
				  };
			    
				 private static Comparator<clsPOSGetPromotionItemDtl> COMPARATORGET = new Comparator<clsPOSGetPromotionItemDtl>()
				  {
				    // This is where the sorting happens.
					  public int compare(clsPOSGetPromotionItemDtl o1, clsPOSGetPromotionItemDtl o2)
						 {
						   return (int) (o1.getTotalAmount() - o2.getTotalAmount());
						 }
				  };
				  
				  
					 private boolean funCheckDayForPromotion(String days, String promoCode, String operationTime, String opDate) throws Exception
					    {
						 
					        boolean flgDays = false;
					        Calendar calendar = Calendar.getInstance();
					        Date date = calendar.getTime();
					        String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
					        StringBuilder sbPromo =new StringBuilder();
					        sbPromo.setLength(0);
			                sbPromo.append("select * from tblpromotiondaytimedtl where strDay='" + currentDay + "' and strPromoCode='" + promoCode + "'");
			                List listDisc=objBaseServiceImpl.funGetList(sbPromo, "sql");
				    	    if(listDisc!=null && listDisc.size()>0)
				    	    {
				    	    	for(int j=0;j<listDisc.size();j++)	
				    	    	{
				    	    	Object []objDisc=(Object[])listDisc.get(j);
					            String fromTime = funConvertTime(objDisc[2].toString());
					            String toTime = funConvertTime(objDisc[3].toString());

					            fromTime = opDate + " " + fromTime;
					            toTime = opDate + " " + toTime;

					            long diff1 = funCompareTime(fromTime, operationTime);
					            long diff2 = funCompareTime(operationTime, toTime);
					            if (diff1 > 0 && diff2 > 0)
					            {
					                flgDays = true;
					                break;
					            }
					        }
					    }
					       
					        return flgDays;
					    }
				  
					 public long funCompareTime(String fromDate, String toDate)
					    {
					        long diff = 0;
					        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					        Date d1 = null;
					        Date d2 = null;

					        try
					        {
					            d1 = format.parse(fromDate);
					            d2 = format.parse(toDate);

					            diff = d2.getTime() - d1.getTime();
					            long diffSeconds = diff / 1000 % 60;
					            long diffMinutes = diff / (60 * 1000) % 60;
					            long diffHours = diff / (60 * 60 * 1000) % 24;
					            long diffDays = diff / (24 * 60 * 60 * 1000);
					            String time = diffHours + ":" + diffMinutes + ":" + diffSeconds;
					        }
					        catch (Exception e)
					        {
					            e.printStackTrace();
					        }
					        finally
					        {
					            return diff;
					        }
					    }
					 
					 private String funConvertTime(String time)
					    {
					        String[] arrTime = time.split(":");
					        String convertedTime = "";
					        int hr = Integer.parseInt(arrTime[0]);
					        String min = arrTime[1].split(" ")[0];
					        String ampm = arrTime[1].split(" ")[1];

					        if (hr == 12)
					        {
					            if (ampm.equals("AM"))
					            {
					                hr += 12;
					            }
					        }
					        else
					        {
					            if (ampm.equals("PM"))
					            {
					                hr += 12;
					            }
					        }
					        String hours = String.valueOf(hr);
					        if (hr < 10)
					        {
					            hours = "0" + hours;
					        }
					        convertedTime = hours + ":" + min + ":00";
					        return convertedTime;
					    }
					 
			public double funCalculateDeliveryChages(String buildingCode,double totalBillAmount,String customerCode,String clientCode,String posCode )
			{
				double billAmount = 0.00;
		        String sqlBuilding = "";
		        double deliverycgares=0;
		        try
		        {
		        	StringBuilder sbDeliveryCharges=new StringBuilder();
		            String gSlabBasedHDCharges=objPOSSetupUtility.funGetParameterValuePOSWise( clientCode,  posCode,  "gSlabBasedHDCharges");
		            if (gSlabBasedHDCharges.equals("Y"))
		            {
		                billAmount = totalBillAmount;
		                sbDeliveryCharges.setLength(0);
		                sbDeliveryCharges.append( "select IFNULL(a.dblDeliveryCharges,0.00) "
		                        + " from tblareawisedc a, tblcustomermaster b "
		                        + " where a.strCustTypeCode=b.strCustomerType and a.strBuildingCode='" + buildingCode + "' "
		                        + " and " + billAmount + " >=a.dblBillAmount and " + billAmount + " <= a.dblBillAmount1 "
		                        + " and b.strCustomerCode='" + customerCode + "'");
		            } else
		            {
		            	sbDeliveryCharges.append( "select IFNULL(dblHomeDeliCharge,0.00) from tblbuildingmaster "
		                        + "where strBuildingCode='" + buildingCode + "'");
		            }

		            //System.out.println(sqlBuilding);
		            
		            List listDisc=objBaseServiceImpl.funGetList(sbDeliveryCharges, "sql");
		    	    if(listDisc!=null && listDisc.size()>0)
		    	    {
		    	    	Object objDisc=(Object)listDisc.get(0);
		            	deliverycgares = Double.parseDouble(objDisc.toString());
		    	    } else
		            {
		            	deliverycgares = 0.00;
		            }
		            
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		        return deliverycgares;
				
			}
			
	  public List funGetDocumentCode(String masterName) throws Exception
	    {
			String sql = "";
			List list = new ArrayList();
			
			switch(masterName)
			{
				case "POSMenuHeadMaster":
				    sql = "select ifnull(max(strMenuCode),0) from tblmenuhd";
				    break;
				    
				case "POSItemModifierMaster":
				    sql = "select ifnull(max(strModifierCode),0) from tblmodifiermaster";
				    break;    
				    
				case "POSMenuItemMaster" :
				   sql = "select ifnull(max(strItemCode),0) from tblitemmaster";
				 break;  
				 
				case "POSModifierGroupMaster" :
					   sql = "select ifnull(max(strModifierGroupCode),0) from tblmodifiergrouphd";
				break;
				
				case "POSZoneMaster":
				    sql = "select ifnull(max(strZoneCode),0) from tblzonemaster";
				    break;
				    
				case "POSShiftMaster":
				    sql = "select ifnull(max(intShiftCode),0) from tblshiftmaster";
				    break;
				    
				case "POSCustomerMaster":
				    sql = "select max(right(strCustomerCode,8)) from tblcustomermaster";
				    break;
				    
				case "POSCustomerTypeMaster":
				    sql = "select ifnull(max(strCustTypeCode),0) from tblcustomertypemaster";
				    break;
				    
				case "POSReasoneMaster":
				    sql = "select max(strReasonCode) from tblreasonmaster";
				    break;
				    
				case "POSCounterMaster":
					sql = "select ifnull(max(strCounterCode),0) from tblcounterhd";
					break;   
					
				case "POSDebitCardMaster":
					sql = "select ifnull(max(strCardTypeCode),0) from tbldebitcardtype";
					break;    
					
				case "POSFactoryMaster":
				    sql = "select ifnull(max(a.strFactoryCode),0) strMaxFactoryCode from tblfactorymaster a";
				    break;   
				    
				case "POSCostCenterMaster":
				    sql = "select max(strCostCenterCode) from tblcostcentermaster";
				    break;
				    
				case "POSAreaMaster":
				    sql = "select ifnull(max(strAreaCode),0) from tblareamaster";
				    break;
				    	
				case "POSWaiterMaster":
				    sql = "select ifnull(max(strWaiterNo),0) from tblwaitermaster";
				    break;
			
				    
				case "POSDeliveryBoyMaster":
					sql = "select ifnull(max(strDPCode),0) from tbldeliverypersonmaster";
					break;
					
				case "POSMaster":
					sql = "select ifnull(max(strPosCode),0) from tblposmaster";
					break;
					
				case "POSTaxMaster":
					sql = "select ifnull(max(strTaxCode),0) from tbltaxhd";
					break;

				case "POSPromotionMaster":
					sql = "select ifnull(max(strPromoCode),0) from tblpromotionmaster";
					break;
					
				case "POSTableMaster":
					sql = "select ifnull(max(strTableNo),0),MAX(intSequence) from tbltablemaster";
					break;
					
				case "POSSettlementMaster":
					sql = "select ifnull(max(strSettelmentCode),0) from tblsettelmenthd";
					break;
					
				case "POSAdvOrderTypeMaster"://tblInternal
					sql = "select ifnull(max(strAdvOrderTypeCode),0) from tbladvanceordertypemaster";
					break;
					
				case "POSRecipeMaster"://tblInternal
					sql = "select ifnull(max(strRecipeCode),0) from tblrecipehd";
					break;
					
				case "POSOrderMaster"://tblInternal
					sql = "select ifnull(max(strOrderCode),0) from tblordermaster";
					break;    
				
				case "POSSubGroupMaster" :
					sql="select max(strSubGroupCode) from tblsubgrouphd";
					break;	
					
				case "POSSubMenuHead":
					sql = "select ifnull(max(strSubMenuHeadCode),0) from tblsubmenuhead";
					break;	
					
				case "POSGroupMaster" :
					sql = "select ifnull(max(strGroupCode),0) from tblgrouphd";
					break;	
					
				case "POSCustAreaMaster":
					sql = "select ifnull(max(strBuildingCode),0) from tblbuildingmaster";
				break;
			    
				case "POSTDHMaster":
					sql = "select ifnull(max(strTDHCode),0) from tbltdhhd";
					break;
				case "POSDiscountMaster":
					sql = "select IFNULL(max(strDiscCode),0) from tbldischd";
					break;

					   
					   
			}
			
			 list = objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
			 
			 return list;
	    }  
			    
			    
	  public JSONObject funGetPrinterList()
		{
			 JSONObject jObj=new JSONObject();
			 List<String> printerList=new ArrayList<String>();
			 try
		        {
		            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; // MY FILE IS .txt TYPE
		            PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
		            printerList.add("");
		            for (int i = 0; i < printService.length; i++)
		            {
		                //System.out.println("Printer Names= "+printService[i].getName());
		                printerList.add(printService[i].getName());
		            }
		            jObj.put("printerList", printerList);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
	 
			 return jObj;
		}
		
	  public long funGetDocumentCodeFromInternal(String masterName) throws Exception
		{
			long code=0;
			StringBuilder sql =new StringBuilder("select dblLastNo from tblinternal where strTransactionType='"+masterName+"'");
			List list = objBaseServiceImpl.funGetList(sql, "sql");
			if(list.size()==0)
			{
				sql.setLength(0);
				sql.append("insert into tblinternal values('"+masterName+"'," + 1 + ")");
				objBaseServiceImpl.funExecuteUpdate(sql.toString(), "sql");
				code=1;
			}
			else
			{
				sql.setLength(0);
				code=Long.parseLong(list.get(0).toString());
				code=code+1;
				sql.append("update tblinternal set dblLastNo='" + code + "' where strTransactionType='"+masterName+"'");
				objBaseServiceImpl.funExecuteUpdate(sql.toString(), "sql");
			}
			
			return code;
		}
	  
	  
	  public int funCalculateDayEndCashForQFile(String posDate, int shiftCode,HttpServletRequest req)
	    {
	        double sales = 0.00, totalDiscount = 0.00, totalSales = 0.00, noOfDiscountedBills = 0.00;
	        double advCash = 0.00, cashIn = 0.00, cashOut = 0.00;
	       
	        try
	        {
	        	String posCode = req.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = req.getSession().getAttribute("gClientCode").toString();
	        	StringBuilder sqlBuilder=new StringBuilder();
	        	  sqlBuilder.setLength(0);
		    		sqlBuilder.append( "SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt),c.strSettelmentType"
	                    + " FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
	                    + "Where a.strBillNo = b.strBillNo "
	                    + " AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
	                    + " and b.strSettlementCode = c.strSettelmentCode "
	                    + " and date(a.dteBillDate ) ='" + posDate + "' and a.strPOSCode='" + posCode + "'"
	                    + " and a.intShiftCode=" + shiftCode
	                    + " GROUP BY c.strSettelmentDesc,a.strPosCode");
	            //System.out.println(sql);
//	            ResultSet rsSettlementAmt = clsGlobalVarClass.dbMysql.executeResultSet(sql);

	            List listSettlementAmt=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if(listSettlementAmt.size()>0)
				{
				 for(int cnt=0;cnt<listSettlementAmt.size();cnt++)
			     {
				    Object[] obj = (Object[]) listSettlementAmt.get(cnt);
	                //records[1]=rsSettlementAmt.getString(2);
	                if (obj[3].toString().equalsIgnoreCase("Cash"))
	                {
	                    sales = sales + (Double.parseDouble(obj[1].toString().toString()));
	                }

	                totalSales = totalSales + (Double.parseDouble(obj[1].toString()));
	            }
	        }

	            gTotalCashSales = totalSales;
	           

	            sqlBuilder.setLength(0);
	    		sqlBuilder.append( "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblqbillhd "
	                    + "Where date(dteBillDate ) ='" + posDate + "' and strPOSCode='" +posCode + "' "
	                    + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftCode
	                    + " GROUP BY strPosCode");
	            List listTotalDiscountBills=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if(listTotalDiscountBills.size()>0)
				{
					Object[] obj=(Object [])listTotalDiscountBills.get(0);
	                gNoOfDiscountedBills =Integer.parseInt(obj[0].toString());

	                totalDiscount = totalDiscount + (Double.parseDouble(obj[1].toString()));
	                gTotalDiscounts = totalDiscount;

	            }
	            
				 sqlBuilder.setLength(0);
		    	 sqlBuilder.append("select count(strBillNo) from tblqbillhd where date(dteBillDate ) ='" + posDate + "' and "
	                    + "strPOSCode='" + posCode + " and intShiftCode=" + shiftCode + "' "
	                    + "GROUP BY strPosCode" );

	            List listTotalBills=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if(listTotalBills.size()>0)
				{
	                gTotalBills =Integer.parseInt(listTotalBills.get(0).toString());
	            }

	            gTotalCashSales = sales;
	            sqlBuilder.setLength(0);
	            sqlBuilder.append( "select count(dblAdvDeposite) from tbladvancereceipthd "
	                    + "where dtReceiptDate='" + posDate + "' and intShiftCode=" + shiftCode);
//	            ResultSet rsTotalAdvance = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	         
	            List listTotalAdvance=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	            if(listTotalAdvance.size()>0)
	            {
	            int cntAdvDeposite =Integer.parseInt(listTotalAdvance.get(0).toString());
	            if (cntAdvDeposite > 0)
	            {
	                //sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
	            	sqlBuilder.setLength(0);
			    	 sqlBuilder.append( "select sum(b.dblAdvDepositesettleAmt) "
	                        + "from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
	                        + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" +posCode + "' "
	                        + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
	                        + "and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftCode );
			    	 listTotalAdvance=new ArrayList();
			    	 listTotalAdvance =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	              if(listTotalAdvance.size()>0)
	              {
	                advCash = Double.parseDouble(listTotalAdvance.get(0).toString());
	                gTotalAdvanceAmt = advCash;
	              }
	            }
	        }
	           

	            //sql="select strTransType,sum(dblAmount) from tblcashmanagement where dteTransDate='"+posDate+"'"
	            //    + " and strPOSCode='"+globalVarClass.gPOSCode+"' group by strTransType";
	            sqlBuilder.setLength(0);
		    	 sqlBuilder.append( "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
	                    + "where dteTransDate='" + posDate + "' and strPOSCode='" + posCode + "' "
	                    + "and intShiftCode=" + shiftCode + " group by strTransType,strCurrencyType" );
	            
	            List listCashTransaction=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
              if (listCashTransaction.size()>0)
              {
              	 for(int i=0;i<listCashTransaction.size();i++)
              	 {
              	 Object[] objBillDtl = (Object[]) listCashTransaction.get(i);
	                if (objBillDtl[0].toString().equals("Float") ||objBillDtl[0].toString().equals("Transfer In"))
	                {
	                    cashIn = cashIn + (Double.parseDouble(objBillDtl[1].toString().toString()));
	                }
	                if (objBillDtl[0].toString().equals("Withdrawl") || objBillDtl[0].toString().equals("Transfer Out") || objBillDtl[0].toString().equals("Payments"))
	                {
	                    cashOut = cashOut + (Double.parseDouble(objBillDtl[1].toString()));
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
	  
	  public int funUpdateDayEndFieldsForQFile(String posDate, int shiftNo, String dayEnd,HttpServletRequest req)
	    {
	        try
	        {
	        	String getCurrentDateTime=funGetCurrentDateTime();
	        	String posCode = req.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = req.getSession().getAttribute("gClientCode").toString();
	        	String gUserCode = req.getSession().getAttribute("gUserCode").toString();
	        	
	        	int gShiftNo=1;
	        	String sql = "update tbldayendprocess set dblTotalSale = IFNULL((select sum(b.dblSettlementAmt) "
	                    + "TotalSale from tblqbillhd a,tblqbillsettlementdtl b "
	                    + " where a.strBillNo=b.strBillNo "
	                    + " and date(a.dteBillDate)=date(b.dteBillDate)  "
	                    + " and date(a.dteBillDate) = '" + posDate + "' and "
	                    + " a.strPOSCode = '" + posCode + "' "
	                    + " and a.intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "'"
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_1=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            sql = "update tbldayendprocess set dteDayEndDateTime='" + getCurrentDateTime + "'"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_2=="+sql);

	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            sql = "update tbldayendprocess set strUserEdited='" + gUserCode + "'"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_3=="+sql);

	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblNoOfBill = IFNULL((select count(*) NoOfBills "
	                    + "from tblqbillhd "
	                    + " where Date(dteBillDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_4=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblNoOfVoidedBill = IFNULL((select count(DISTINCT strBillNo) "
	                    + "NoOfVoidBills from tblvoidbillhd "
	                    + " where date(dteModifyVoidBill) = " + "'" + posDate + "'"
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and strTransType = 'VB'"
	                    + " and intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_5=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            sql = "update tbldayendprocess set dblNoOfModifyBill = IFNULL((select count(DISTINCT b.strBillNo) "
	                    + "NoOfModifiedBills "
	                    + " from tblqbillhd a,tblvoidbillhd b "
	                    + " where a.strBillNo=b.strBillNo "
	                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
	                    + " and Date(b.dteModifyVoidBill) = '" + posDate + "' "
	                    + " and b.strPOSCode='" + posCode + "'"
	                    + " and b.strTransType = 'MB' "
	                    + " and a.intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_6=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblHDAmt=IFNULL((select sum(a.dblGrandTotal) HD "
	                    + " from tblqbillhd a,tblhomedelivery b "
	                    + " where a.strBillNo=b.strBillNo "
	                    + " and date(a.dteBillDate) = '" + posDate + "' and "
	                    + " a.strPOSCode = '" + posCode + "' "
	                    + " and a.intShiftCode=" + shiftNo + "), 0) "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_7=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblDiningAmt=IFNULL(( select sum(dblGrandTotal) Dining"
	                    + " from tblqbillhd "
	                    + " where strTakeAway='No' "
	                    + " and date(dteBillDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "'"
	                    + " and strBillNo NOT IN (select strBillNo from tblhomedelivery where strBillNo is not NULL) "
	                    + " and intShiftCode=" + gShiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            //System.out.println("UpdateDayEndQuery_8=="+sql);

	            sql = "update tbldayendprocess set dblTakeAway=IFNULL((select sum(dblGrandTotal) TakeAway from tblqbillhd"
	                    + " where strTakeAway='Yes' "
	                    + " and date(dteBillDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "'"
	                    + " and intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;

	            //System.out.println("UpdateDayEndQuery_9=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblFloat=IFNULL((select sum(dblAmount) TotalFloats from tblcashmanagement "
	                    + "where strTransType='Float' "
	                    + " and date(dteTransDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "'"
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " group by strTransType),0) "
	                    + "where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_10=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblTransferIn=IFNULL((select sum(dblAmount) TotalTransferIn from tblcashmanagement "
	                    + "where strTransType='Transfer In' and dteTransDate = '" + posDate + "'"
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo
	                    + " group by strTransType),0) "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_11=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblTransferOut=IFNULL((select sum(dblAmount) TotalTransferOut from tblcashmanagement "
	                    + " where strTransType='Transfer Out' "
	                    + " and date(dteTransDate) = '" + posDate + "'"
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " group by strTransType),0) "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_12=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblWithdrawal=IFNULL(( select sum(dblAmount) TotalWithdrawals from tblcashmanagement "
	                    + " where strTransType='Withdrawal' "
	                    + " and date(dteTransDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " group by strTransType),0) "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_13=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblRefund=IFNULL(( select sum(dblAmount) TotalRefunds from tblcashmanagement "
	                    + " where strTransType='Refund' "
	                    + " and date(dteTransDate) = '" + posDate + "' "
	                    + " and strPOSCode = '" + posCode + "'"
	                    + " and intShiftCode=" + shiftNo + " group by strTransType),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_14=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblPayments=IFNULL(( select sum(dblAmount) TotalPayments from tblcashmanagement "
	                    + " where strTransType='Payments' "
	                    + " and date(dteTransDate) = '" + posDate + "'"
	                    + " and strPOSCode = '" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " group by strTransType),0) "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_15=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblAdvance=IFNULL((select sum(b.dblAdvDepositesettleAmt) "
	                    + " from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
	                    + " where date(a.dtReceiptDate)='" + posDate + "' "
	                    + " and a.strPOSCode='" + posCode + "' "
	                    + " and c.strSettelmentCode=b.strSettlementCode "
	                    + " and a.strReceiptNo=b.strReceiptNo "
	                    + " and c.strSettelmentType='Cash' "
	                    + " and intShiftCode=" + shiftNo + "),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_16=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblTotalReceipt=" + gTotalReceipt
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_17=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblTotalPay=" + gTotalPayments
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_18=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblCashInHand=" + gTotalCashInHand
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_19=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblCash=" + gTotalCashSales
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println(sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblTotalDiscount=" + gTotalDiscounts
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_21=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set dblNoOfDiscountedBill=" + gNoOfDiscountedBills
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_22=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set intTotalPax=IFNULL((select sum(intBillSeriesPaxNo)"
	                    + " from tblqbillhd "
	                    + " where date(dteBillDate ) ='" + posDate + "' "
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " and strPOSCode='" + posCode + "'),0)"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("UpdateDayEndQuery_23=="+sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set intNoOfTakeAway=(select count(strTakeAway)"
	                    + " from tblqbillhd where date(dteBillDate )='" + posDate + "' "
	                    + " and intShiftCode=" + shiftNo + ""
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and strTakeAway='Yes')"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("update int takeawy==" + sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            sql = "update tbldayendprocess set intNoOfHomeDelivery=(select COUNT(strBillNo)from tblhomedelivery where date(dteDate)='" + posDate + "' and strPOSCode='" + posCode + "' )"
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            //System.out.println("update int homedelivry:==" + sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            // Update Day End Table with Used Card Balance    
	            double debitCardAmtUsed = 0;
	            StringBuilder sqlBuilder =new StringBuilder();
              sqlBuilder.setLength(0);
	    		sqlBuilder.append( "select sum(b.dblSettlementAmt) "
	                    + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
	                    + " where a.strBillNo=b.strBillNo "
	                    + " and b.strSettlementCode=c.strSettelmentCode "
	                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
	                    + " and date(a.dteBillDate)='" + posDate + "' "
	                    + " and a.strPOSCode='" + posCode + "' "
	                    + " and c.strSettelmentType='Debit Card' "
	                    + " group by a.strPOSCode,date(a.dteBillDate),c.strSettelmentType;");
	            
	            List listUsedDCAmt=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
              if (listUsedDCAmt.size()>0)
              {
	                debitCardAmtUsed = Double.parseDouble( listUsedDCAmt.get(0).toString());
	            }
	            
	            sql = "update tbldayendprocess set dblUsedDebitCardBalance=" + debitCardAmtUsed + " "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            // Update Day End Table with UnUsed Card Balance    
	            double debitCardAmtUnUsed = 0;
	            sqlBuilder.setLength(0);
	    		sqlBuilder.append( "select sum(dblCardAmt) from tbldebitcardrevenue "
	                    + " where strPOSCode='" + posCode + "'  "
	                    + " and date(dtePOSDate)='" + posDate + "' "
	                    + " group by strPOSCode,date(dtePOSDate);");
	            
	            List listUnUsed=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
              if (listUnUsed.size()>0)
              {
	                debitCardAmtUnUsed = Double.parseDouble(listUnUsed.get(0).toString());
	            }
	            
	            sql = "update tbldayendprocess set dblUnusedDebitCardBalance=" + debitCardAmtUnUsed + " "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "UPDATE tbldayendprocess SET dblTipAmt= IFNULL(( "
	                    + " SELECT SUM(dblTipAmount) "
	                    + " FROM tblqbillhd "
	                    + " WHERE DATE(dteBillDate) ='" + posDate + "' "
	                    + " AND intShiftCode='" + shiftNo + "' "
	                    + " AND strPOSCode='" + posCode + "'),0) "
	                    + " WHERE DATE(dtePOSDate)='" + posDate + "' "
	                    + " AND strPOSCode='" + posCode + "' "
	                    + " AND intShiftCode='" + shiftNo + "' ";
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");

	            sql = "update tbldayendprocess set intNoOfComplimentaryKOT=(select COUNT(a.strBillNo)"
	                    + " from  tblqbillhd a,tblqbillcomplementrydtl b "
	                    + " where a.strBillNo=b.strBillNo "
	                    + " and date(a.dteBillDate)=date(b.dteBillDate) "
	                    + " and date(b.dteBillDate)='" + posDate + "' "
	                    + " and a.strPOSCode='" + posCode + "') "
	                    + " where date(dtePOSDate)='" + posDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and intShiftCode=" + shiftNo;
//	            System.out.println("intNoOfComplimentaryKOT:==" + sql);
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        return 1;
	    }
	 
	 
}
