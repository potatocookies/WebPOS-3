package com.sanguine.webpos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemTaxDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSBuyPromotionItemDtl;
import com.sanguine.webpos.bean.clsPOSGetPromotionItemDtl;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.bean.clsPOSTaxCalculationBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;


@Controller
public class clsPOSUtilityController 
{
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	
	@Autowired
	clsPOSSendMail obSendMail; 

	@Autowired 
	clsPOSSetupUtility objPOSSetupUtility;
	
	@Autowired
	intfBaseService objBaseService;
	
	@Autowired
	private clsSetupService objSetupService;
	
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
  		  sqlBuilder.append("select ifnull(strTaxIndicator,'NA') from tblitemmaster "
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
    		  sqlBuilder.append( "select ifnull(strTaxIndicator,'NA') from tblitemmaster "
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
                            sqlTaxOnTax.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent,a.dblAmount,a.dteValidFrom,a.dteValidTo,a.strTaxOnGD,a.strTaxCalculation,ifnull(a.strTaxIndicator,'NA') "
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
	  	sqlBuilder.append ( "select a.strTaxCode,b.dblPercent,ifnull(b.strTaxIndicator,'NA'),b.strTaxCalculation,b.strTaxOnGD,b.strTaxOnTax "
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
    	  	sqlBuilder.append ( "select a.strItemCode,a.dblAmount,ifnull(b.strTaxIndicator,'NA'),a.strKOTNo,a.dblDiscountAmt "
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
        			
        			for(int j=0;j<listItemModDtl.size();j++)
        			{
        				Object []objItemModDtl=(Object[])listItemModDtl.get(j);
        	               
                        itemAmt += Double.parseDouble(objItemModDtl[0].toString());
                        if (taxOnGD.equalsIgnoreCase("Discount"))
                        {
                            itemAmt -=  Double.parseDouble(objItemModDtl[1].toString());
                        }
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
			    
			    
	  public Map funGetPrinterList()
		{
			 Map hmData=new HashMap();
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
		            hmData.put("printerList", printerList);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
	 
			 return hmData;
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
	  
	  
	  
	  
	  


public Map funGenerateTextDayEndReportPreview(String posCode, String billDate, String reprint,String clientCode,String gDayEndReportForm,String webStockUserCode)
	{
	 	Map hmRet=new HashMap();
        List listData =new ArrayList();
        StringBuilder sql = new StringBuilder(); 
		 try
		 {
		   Map mapData = new HashMap();
		   if ("reprint".equalsIgnoreCase(reprint))
	       {
		      mapData.put("duplicate", "[DUPLICATE]");
		   }
		   mapData.put("reportHeading","DAY END REPORT");
		   
		   sql.append("select  a.strPOSCode,b.strPosName,date(a.dtePOSDate),time(a.dteDayEndDateTime),a.dblTotalSale,\n"
                   + "a.dblFloat,a.dblCash,a.dblAdvance,  a.dblTransferIn,a.dblTotalReceipt,a.dblPayments,\n"
                   + "a.dblWithDrawal,a.dblTransferOut,a.dblTotalPay,  a.dblCashInHand,a.dblHDAmt,\n"
                   + "a.dblDiningAmt,a.dblTakeAway,a.dblNoOfBill,a.dblNoOfVoidedBill,\n"
                   + "a.dblNoOfModifyBill,a.dblRefund  ,a.dblTotalDiscount,\n"
                   + "a.intTotalPax,a.intNoOfTakeAway,a.intNoOfHomeDelivery,\n"
                   + "a.strUserCreated,a.strUserEdited, a.intNoOfNCKOT,a.intNoOfComplimentaryKOT,a.intNoOfVoidKOT from tbldayendprocess a ,  "
                   + " tblposmaster b where b.strPosCode=a.strPosCode "
                   + " and a.strPOSCode='"+posCode+"' and date(a.dtePOSDate)='"+billDate+"'");

           List listDayend =objBaseService.funGetList(sql, "sql");
           String posCode1,posDate,posName,dayEndBy;
           BigDecimal homeDelivery;
           BigDecimal dining,takeAway,totalSales;
           BigDecimal discount,floatval,cash,advance,transferIn,totalReceipt;
           BigDecimal payment,withdrawal,transferOut,refund,totalPayments;
           BigDecimal cashInHand;
           BigDecimal noOfBills,noOfVoidedBills,noOfModifiedBills;
           int noOfPax;
		   int noOfHomeDel;
		   int noOfNcKot;
		   int noOfTakeAway;
           int noOfComplimentaryBills;
		   int noOfVoidKot;
	        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
	        SimpleDateFormat ft1 = new SimpleDateFormat("hh:mm a ");
            if (listDayend!=null)
            {
               	for(int i=0; i<listDayend.size(); i++)
            	{
          		Object[] obj = (Object[]) listDayend.get(i);
          		posCode1 = (String) Array.get(obj,0);
          		mapData.put("posCode1", posCode1);
          		
          		posName = (String) Array.get(obj,1);
          		mapData.put("posName", posName);
          		
          		Date dt = (Date) Array.get(obj,2);
          		posDate = ft.format(dt);
           		mapData.put("posDate", posDate);
           		
           		totalSales = (BigDecimal) Array.get(obj,4);
          		mapData.put("totalSales", totalSales);
           		
          		floatval = (BigDecimal) Array.get(obj,5);
          		mapData.put("floatval", floatval);
          		
          		cash = (BigDecimal) Array.get(obj,6);
          		mapData.put("cash", cash);
          		
          		advance = (BigDecimal) Array.get(obj,7);
          		mapData.put("advance", advance);


          		transferIn = (BigDecimal) Array.get(obj,8);
          		mapData.put("transferIn", transferIn);

          		totalReceipt = (BigDecimal) Array.get(obj,9);
          		mapData.put("totalReceipt", totalReceipt);

          		payment = (BigDecimal) Array.get(obj,10);
          		mapData.put("payment", payment);

          		withdrawal = (BigDecimal) Array.get(obj,11);
          		mapData.put("withdrawal", withdrawal);

          		transferOut = (BigDecimal) Array.get(obj,12);
          		mapData.put("transferOut", transferOut);

          		totalPayments = (BigDecimal) Array.get(obj,13);
          		mapData.put("totalPayments", totalPayments);
          		
          		cashInHand = (BigDecimal) Array.get(obj,14);
          		mapData.put("cashInHand", cashInHand);
          		
          		homeDelivery = (BigDecimal) Array.get(obj,15);
          		mapData.put("homeDelivery", homeDelivery);
          		
          		dining = (BigDecimal) Array.get(obj,16);
          		mapData.put("dining", dining);
          		
          		takeAway = (BigDecimal) Array.get(obj,17);
          		mapData.put("takeAway", takeAway);
          		
          		noOfBills = (BigDecimal) Array.get(obj,18);
          		mapData.put("noOfBills", noOfBills);
          		
          		noOfVoidedBills = (BigDecimal) Array.get(obj,19);
          		mapData.put("noOfVoidedBills", noOfVoidedBills);
          		
          		noOfModifiedBills = (BigDecimal) Array.get(obj,20);
          		mapData.put("noOfModifiedBills", noOfModifiedBills);

          		refund = (BigDecimal) Array.get(obj,21);
          		mapData.put("refund", refund);
          		
          		discount = (BigDecimal) Array.get(obj,22);
          		mapData.put("discount", discount);
          		

          		noOfPax = (int) Array.get(obj,23);
          		mapData.put("noOfPax", noOfPax);

          		noOfTakeAway = (int) Array.get(obj,24);
          		mapData.put("noOfTakeAway", noOfTakeAway);
          		
          		noOfHomeDel = (int) Array.get(obj,25);
          		mapData.put("noOfHomeDel", noOfHomeDel);
          		
          		dayEndBy =(String) Array.get(obj,27);
           		mapData.put("dayEndBy", dayEndBy);
          		
          		noOfNcKot = (int) Array.get(obj,28);
          		mapData.put("noOfNcKot", noOfNcKot);
          		
          		noOfComplimentaryBills = (int) Array.get(obj,29);
          		mapData.put("noOfComplimentaryBills", noOfComplimentaryBills);
          		
          		noOfVoidKot = (int) Array.get(obj,30);
          		mapData.put("noOfVoidKot", noOfVoidKot);
          		
          		
            	}
            }
            mapData.put("BILLING SETTLEMENT BREAK UP","BILLING SETTLEMENT BREAK UP");
            String sql_SettelementBrkUP = "";
            List listSettelementBrkUP = new ArrayList();
            sql.setLength(0);
			 if(gDayEndReportForm.equalsIgnoreCase("DayEndReport"))
             {
				 sql.append("select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
                        + " from  tblbillhd a, tblbillsettlementdtl b, tblsettelmenthd c  "
                        + " where a.strBillNo = b.strBillNo"
                        + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode='"+posCode+"' "
                        + " and date(a.dteBillDate)='"+billDate+"' "
                        + " GROUP BY c.strSettelmentDesc;");
            }
            else
            {
            	sql.append("select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
                        + " from  tblqbillhd a, tblqbillsettlementdtl b, tblsettelmenthd c  "
                        + " where a.strBillNo = b.strBillNo"
                        + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode='"+posCode+"' "
                        + " and date(a.dteBillDate) ='"+billDate+"' "
                        + " GROUP BY c.strSettelmentDesc;");
            }
            
			 List listSettelementBrkUp =objBaseService.funGetList(sql, "sql");
	         String settlementDesc="";
	         BigDecimal settlementAmount=null;
	         double totalAmt=0.00,settlementAmt=0.00;
	         if (listSettelementBrkUp!=null)
	         {
	        	
	               	for(int i=0; i<listSettelementBrkUp.size(); i++)
	            	{
	               		Map mapList=new HashMap();
		          		Object[] obj = (Object[]) listSettelementBrkUp.get(i);
		          		settlementDesc = (String) Array.get(obj,0);
		          		settlementAmount = (BigDecimal) Array.get(obj,1);
		          		settlementAmt = settlementAmount.doubleValue();
		          		totalAmt += settlementAmt;
		          		mapList.put("settlementDesc", settlementDesc);
		          		mapList.put("settlementAmt", settlementAmt);
	                	listSettelementBrkUP.add(mapList);
		            }
		            mapData.put("Total", totalAmt);
	               
	         }  
	         
	         sql.setLength(0);
	         String billHd = "tblbillhd", billTaxDtl = "tblbilltaxdtl";
	         List listSettelementTaxDtl = new ArrayList();
             if (!gDayEndReportForm.equals("DayEndReport"))
             {
                 billHd = "tblqbillhd";
                 billTaxDtl = "tblqbilltaxdtl";
             }
         	sql.append("select b.strTaxDesc, sum(a.dblTaxableAmount), sum(a.dblTaxAmount) "
                     + "from " + billTaxDtl + " a, tbltaxhd b "
                     + "Where a.strTaxCode = b.strTaxCode and strBillNo IN "
                     + "(select strBillNo from " + billHd + " where strPOSCode = '"+posCode+"' and date(dteBillDate)= '"+billDate+"' ) "
                     + "Group By b.strTaxDesc");
            
            mapData.put("taxDesc", "   TAX Des             Taxable   Tax Amt   ");
            String taxDesc="";
            BigDecimal taxableAmt=null,taxAmt=null;
            double taxableAmount=0.00,taxAmount=0.00;
            List listSettelementTax =objBaseService.funGetList(sql, "sql");
	         if (listSettelementTax!=null)
	         {
	        	 
	               	for(int i=0; i<listSettelementTax.size(); i++)
	            	{
	               		Map mapList = new HashMap();
		          		Object[] obj = (Object[]) listSettelementTax.get(i);
		          		taxDesc = (String) Array.get(obj,0);
		          		taxableAmt = (BigDecimal) Array.get(obj,1);
		          		taxableAmount = taxableAmt.doubleValue();
		          		taxAmt  = (BigDecimal) Array.get(obj,2);
		          		taxAmount = taxAmt.doubleValue();
		          		mapList.put("taxDesc", taxDesc);
		          		mapList.put("taxableAmount", taxableAmount);
		          		mapList.put("taxAmount", taxAmount);
		          		listSettelementTaxDtl.add(mapList);
	            	}
	               	
	         }
	         sql.setLength(0);
	       //group wise subtotal
	            String sqlBuilder="";
	            List listGroupAmtWithTaxDtl = new ArrayList();
	            Map<String, clsPOSGroupSubGroupWiseSales> mapGroupWiseData = new HashMap<>();

	            //live group data
	            
	            sql.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
	                    + ", '" + webStockUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
	                    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
	                    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
	                    + "WHERE a.strBillNo=b.strBillNo "
	                    + "AND a.strPOSCode=f.strPOSCode "
	                    + "AND a.strClientCode=b.strClientCode "
	                    + "AND b.strItemCode=e.strItemCode "
	                    + "AND c.strGroupCode=d.strGroupCode "
	                    + "AND d.strSubGroupCode=e.strSubGroupCode "
	                    + "AND a.strPOSCode = '" + posCode + "' "
	                    + "AND DATE(a.dteBillDate)='" + billDate + "'"
	                    + "AND a.intShiftCode='1' "
	                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
	             List listGroupData =objBaseService.funGetList(sql, "sql");
		         if (listGroupData!=null)
		         {
		               	for(int i=0; i<listGroupData.size(); i++)
		            	{
		          		Object[] obj = (Object[]) listGroupData.get(i);
		          		String groupCode = (String) Array.get(obj,0);
		          		String groupName = (String) Array.get(obj,1);
		          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
		          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
		          		
		          		
		          		if (mapGroupWiseData.containsKey(groupCode))
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		                }
		                else
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales();
		                    objGroupWiseSales.setGroupCode(groupCode);
		                    objGroupWiseSales.setGroupName(groupName);
		                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

		                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		                }
		            	}
		         }      	
		         
		            sql.setLength(0);
		            sql.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
		                    + ",'" + webStockUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
		                    + "FROM tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
		                    + "WHERE a.strBillNo=b.strBillNo "
		                    + "AND a.strPOSCode=f.strPosCode "
		                    + "AND a.strClientCode=b.strClientCode  "
		                    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
		                    + "AND d.strSubGroupCode=e.strSubGroupCode "
		                    + "AND e.strGroupCode=c.strGroupCode  "
		                    + "AND b.dblamount>0 "
		                    + "AND a.strPOSCode = '" + posCode + "' "
		                    + "AND a.intShiftCode='1' "
		                    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
		                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
		            listGroupData =objBaseService.funGetList(sql, "sql");
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         } 
			         sql.setLength(0);
			         sql.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
			                    + ", '" + webStockUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
			                    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
			                    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
			                    + "WHERE a.strBillNo=b.strBillNo "
			                    + "AND a.strPOSCode=f.strPOSCode "
			                    + "AND a.strClientCode=b.strClientCode "
			                    + "AND b.strItemCode=e.strItemCode "
			                    + "AND c.strGroupCode=d.strGroupCode "
			                    + "AND d.strSubGroupCode=e.strSubGroupCode "
			                    + "AND a.strPOSCode = '" + posCode + "' "
			                    + "AND DATE(a.dteBillDate)='" + billDate + "'"
			                    + "AND a.intShiftCode='1' "
			                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
            
			         listGroupData =objBaseService.funGetList(sql, "sql");
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         }
			         sql.setLength(0);
			         sql.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
			                    + ",'" + webStockUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
			                    + "FROM tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
			                    + "WHERE a.strBillNo=b.strBillNo "
			                    + "AND a.strPOSCode=f.strPosCode "
			                    + "AND a.strClientCode=b.strClientCode  "
			                    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
			                    + "AND d.strSubGroupCode=e.strSubGroupCode "
			                    + "AND e.strGroupCode=c.strGroupCode  "
			                    + "AND b.dblamount>0 "
			                    + "AND a.strPOSCode = '" + posCode + "' "
			                    + "AND a.intShiftCode='1' "
			                    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
			                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
			         listGroupData =objBaseService.funGetList(sql, "sql");
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales = new clsPOSGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         }      	
			         
			         if (mapGroupWiseData.size() > 0)
			         {
			        	 mapData.put("GroupAmtWithTax", "   Group                 Amount With Tax   ");
			        	 for (clsPOSGroupSubGroupWiseSales objGroupWiseSales : mapGroupWiseData.values())
			             {
			        		 Map mapList = new HashMap();
			        		 String groupName = objGroupWiseSales.getGroupName();
			        		 double dblNetTotalPlusTax = Math.rint(objGroupWiseSales.getDblNetTotalPlusTax());
			        		 mapList.put("groupName", groupName);
			        		 mapList.put("dblNetTotalPlusTax", dblNetTotalPlusTax);
			        		 listGroupAmtWithTaxDtl.add(mapList);    
			             }
			         }
			         mapData.put("listGroupAmtWithTaxDtl", listGroupAmtWithTaxDtl);
			         mapData.put("listSettelementBrkUP",listSettelementBrkUP);
			         mapData.put("listSettelementTaxDtl", listSettelementTaxDtl); 
			      	 listData.add(mapData);
			     	 hmRet.put("listData", listData);	         
		 }
		
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return hmRet;
	    }
	 
	
	   



public Map funPrintBill(String voucherNo, String billDate, boolean flgReprint, String POSCode,String clientCode,String posName,String webStockUserCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
{
    Map mapResult = new HashMap();
    try
    {
        String reprintYN = "";
        if (flgReprint)
        {
            reprintYN = "Reprint";
        }
        
        Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gBillFormatType");
		  
				
        if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 1"))
        {
        	mapResult=funGenerateBillForJasperFormat1(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        }
        else if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 2"))
        {
        	mapResult=funGenerateBillForJasperFormat2(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        }
        else if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 3"))
        {
        	mapResult = funGenerateBillForJasperFormat3(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        }

    }
    catch (Exception e)
    {
       
        e.printStackTrace();
    }
    finally
    {
    	return mapResult;
    }
}

public Map funGenerateBillForJasperFormat1(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
{
    HashMap hm = new HashMap();
    Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
    Map jObjRet=new HashMap();
    
    List listData =new ArrayList();
    List listData1 =new ArrayList();
    StringBuilder sql = new StringBuilder(); 
	
   
    String Linefor5 = "  --------------------------------------";
    try
    {
    	Map hmData = new HashMap();
    	Map jObj=new HashMap();
        String user = "";
        String billhd;
        String billdtl;
        String billModifierdtl;
        String billSettlementdtl;
        String billtaxdtl;
        String billDscFrom = "tblbilldiscdtl";
        String billPromoDtl = "tblbillpromotiondtl";
        String billType = " ";

        if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
        {
            billhd = "tblqbillhd";
            billdtl = "tblqbilldtl";
            billModifierdtl = "tblqbillmodifierdtl";
            billSettlementdtl = "tblqbillsettlementdtl";
            billtaxdtl = "tblqbilltaxdtl";
            billDscFrom = "tblqbilldiscdtl";
            billPromoDtl = "tblqbillpromotiondtl";
        }
        else
        {
            if ("sales report".equalsIgnoreCase(formName))
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billDscFrom = "tblbilldiscdtl";
                billPromoDtl = "tblbillpromotiondtl";

                long dateDiff =funCompareDate(billDate,billDate);
                if (dateDiff > 0)
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }

                sql.append("select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ");
                List listBillTable =objBaseService.funGetList(sql, "sql");
    			int billCnt;
    			String billCount="";
    			if (listBillTable!=null)
 				{
 					for(int i=0; i<listBillTable.size(); i++)
 					{
 						
 						billCount =(String) listBillTable.get(i);
                 
 					}
 				}
                 billCnt = Integer.parseInt(billCount);
                if (billCnt == 0)
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }
            }
            else
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billPromoDtl = "tblbillpromotiondtl";
            }
        }
       
        BigDecimal subTotal = null;
        BigDecimal grandTot = null;
        BigDecimal advAmount = null;
        BigDecimal deliveryCharge = null;
        String customerCode = "";
        double grandTotal=0.00;
        boolean flag_DirectBiller = false;
        if (clientCode.equalsIgnoreCase("117.001"))
        {
            if (POSCode.equals("P01"))
            {
            	hmData.put("posWiseHeading", "THE PREM'S HOTEL");
            }
            else if (POSCode.equals("P02"))
            {
            	hmData.put("posWiseHeading", "SWIG");
            }
        }

        boolean isReprint = false;
        if ("reprint".equalsIgnoreCase(reprint))
        {
            isReprint = true;
            hmData.put("duplicate", "[DUPLICATE]");
        }
        if (transType.equals("Void"))
        {
        	hmData.put("voidedBill", "VOIDED BILL");
        }

        boolean flag_isHomeDelvBill = false;
        sql.setLength(0);
        sql.append("select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                + "from tblhomedelivery where strBillNo='" + billNo + "'");
        List rs_HomeDelivery =objBaseService.funGetList(sql, "sql");
        String billNumber="",customerName="",customerAddress = null;
         if (null!=rs_HomeDelivery)
         {
         	Map jObj2=new HashMap();
         	for(int i=0; i<rs_HomeDelivery.size(); i++)
			{
       		Object[] obj = (Object[]) rs_HomeDelivery.get(i);
		
		
       		billNumber = (String) Array.get(obj,0);
       		customerCode=(String)Array.get(obj,1);
       		customerAddress=(String)Array.get(obj,4);
    	   
			}
         }

         List listOfHomeDeliveryDtl =new ArrayList();
        clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
        if (rs_HomeDelivery.size()!=0)
        {
        if (null!=rs_HomeDelivery)
        {
            flag_isHomeDelvBill = true;
           
            billType = "HOME DELIVERY";

            String SQL_CustomerDtl = "";
            
            sql.setLength(0);
            
            if (customerAddress.equals("Home"))
            {
            	sql.append("select a.strCustomerName,a.strCustAddress,a.strStreetName"
                        + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
                        + " from tblcustomermaster a left outer join tblbuildingmaster b "
                        + " on a.strBuldingCode=b.strBuildingCode "
                        + " where a.strCustomerCode='"+customerCode+"' ");
            }
            else
            {
            	sql.append("select a.strCustomerName,a.strCustAddress,a.strOfficeStreetName"
                        + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
                        + " from tblcustomermaster a "
                        + " where a.strCustomerCode='"+customerCode+"' ");
            }
          
            List rs_CustomerDtl =objBaseService.funGetList(sql, "sql");
           String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
             if (null!=rs_CustomerDtl)
             {
            	 Map jObjList=new HashMap();
             	for(int i=0; i<rs_CustomerDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
    		
    		
           		custName = (String) Array.get(obj,0);
           		custAddress=(String)Array.get(obj,1);
           		officeStreetName=(String)Array.get(obj, 2);
           		officeLandmark=(String)Array.get(obj, 3);
           		officeArea=(String)Array.get(obj, 4);
           		officeCity=(String)Array.get(obj, 5);
           		officePinCode=(String)Array.get(obj, 6);
           		mobileNo=(String)Array.get(obj, 7);
    			}
             	hmData.put("NAME",custName);
             	
             	jObjList.put("NAME", custName.toUpperCase());
             
             	
                // Building Name 
                String fulAddress = "";
                String fulAddress1 = "";
                String fullAddress = "";
                String add = custAddress;
                int strlen = add.length();
                String add1 = "";
                if (strlen < 28)
                {
                    add1 = add.substring(0, strlen);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                }
                else
                {
                    add1 = add.substring(0, 28);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                  
                }

                // Street Name    
                String street = officeStreetName;
                String street1;
                int streetlen = street.length();
                for (int i = 0; i <= streetlen; i++)
                {
                    int end = 0;
                    end = i + 28;
                    if (streetlen > end)
                    {
                        street1 = street.substring(i, end);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                        }
                        fulAddress += "Street    :" + street1.toUpperCase();
                        i = end;
                    }
                    else
                    {
                        street1 = street.substring(i, streetlen);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                            fulAddress += "Street    :" + street1.toUpperCase();
                            i = streetlen + 1;
                        }
                    }
                }
                // Landmark Name    
                if (officeLandmark.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }

                    }
                    fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
                }

                // Area Name    
                if (officeArea.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }
                    }
                }

                // City Name    
                if (officeCity.trim().length() > 0)
                {
                    if (!officeCity.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeCity;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeCity;
                        }
                    }
                    fulAddress1 += "City    :" + officeCity.toUpperCase();
                }

                // Pin Code    
                if (officePinCode.trim().length() > 0)
                {
                    if (!officePinCode.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officePinCode;
                        }
                        else
                        {
                            fullAddress += "," + " " + officePinCode;
                        }
                    }
                    fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
                }
                
                 jObjList.put("Address" ,fullAddress);
//                listOfHomeDeliveryDtl.put(jObjList);

                hmData.put("FullAddress", fullAddress);
                if (mobileNo.isEmpty())
                {
                	Map jObjList1=new HashMap();
                	hmData.put("MOBILE_NO", "");
                	
                    objBillDtl = new clsPOSBillDtl();
                    jObjList.put("MOBILE_NO"," ");
//                    listOfHomeDeliveryDtl.put(jObjList);
                }
                else
                {
                	Map jObjList2=new HashMap();
                	hmData.put("MOBILE_NO", mobileNo);
                    objBillDtl = new clsPOSBillDtl();
                    jObjList.put("MOBILE_NO",mobileNo);
//                    listOfHomeDeliveryDtl.put(jObjList);
                }
                listOfHomeDeliveryDtl.add(jObjList);
            }
          

            if (null != officeStreetName && officeStreetName.trim().length() > 0)
            {
            	Map jObjList=new HashMap();
            	String[] delBoys = officeStreetName.split(",");
                StringBuilder strIN = new StringBuilder("(");
                for (int i = 0; i < delBoys.length; i++)
                {
                    if (i == 0)
                    {
                        strIN.append("'" + delBoys[i] + "'");
                    }
                    else
                    {
                        strIN.append(",'" + delBoys[i] + "'");
                    }
                }
                strIN.append(")");
                sql.setLength(0);
                sql.append("select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ");
               
                List rs_DeliveryBoyDtl = objBaseService.funGetList(sql, "sql");
                String dpName="";
               if (null!=rs_DeliveryBoyDtl)
               {
               
                strIN.setLength(0);
                for(int i=0; i<rs_DeliveryBoyDtl.size(); i++)
    			{
                	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
    		
    		
                	dpName = (String) Array.get(obj,2);
        	  
               
                    if (i == 0)
                    {
                        strIN.append(dpName.toUpperCase());
                    }
                    else
                    {
                        strIN.append("," + dpName.toUpperCase());
                    }
                }
               } 

                if (strIN.toString().isEmpty())
                {
                	hmData.put("DELV BOY", "");
                }
                else
                {
                	hmData.put("DELV BOY", "Delivery Boy : " + strIN);
                    objBillDtl = new clsPOSBillDtl();
                    jObjList.put("Delivery Boy : ",strIN);
                    listOfHomeDeliveryDtl.add(jObjList);
                }
              
            }
            else
            {
            	hmData.put("DELV BOY", "");
            }
        }
    }
       
        int result = funPrintTakeAwayForJasper(billhd, billNo);
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
        String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
        String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
        String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
        String cityName = objSetupParameter.get("gCityName").toString();
        
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
        String clientName = objSetupParameter.get("gClientName").toString();
        
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
        String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
          
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
        String clientEmail = objSetupParameter.get("gClientEmail").toString();
        
        if (result == 1)
        {
            billType = "Take Away";
        }
         objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
		    
		    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
        {
		    	hmData.put("TAX_INVOICE", "TAX INVOICE");
        }
		  
        if ( clientCode.equals("047.001") && POSCode.equals("P03"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
        else if (clientCode.equals("047.001") && POSCode.equals("P02"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
         
        else if (clientCode.toString().equals("092.001") || clientCode.toString().equals("092.002") || clientCode.toString().equals("092.003"))//Shree Sound Pvt. Ltd.
        {
        	hmData.put("ClientName", "SSPL");
        	hmData.put("ClientAddress1",clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            
            if (cityName.trim().length() > 0)
            {
                hmData.put("ClientCity", cityName);
            }
        }
        else
        {
        	hmData.put("ClientName", clientName);
        	hmData.put("ClientAddress1", clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            if (cityName.trim().length() > 0)
            {
            	hmData.put("ClientCity", cityName);
            }
        }

        hmData.put("TEL NO", clientTelNo.toString());
        hmData.put("EMAIL ID", clientEmail);
        hmData.put("Line", Linefor5);

        
        String SQL_BillHD = "",billDt="",qty="";
        String waiterName = "";
        Date dt = null; 
        double quantity=0;
        String waiterNo = "";
        String tblName = "";
        String advDeposite="";
        String sqlTblName = "";
        String tabNo = "";
        String posName="";
        int paxNo = 0;
        boolean flag_DirectBillerBlill = false;
        boolean flgComplimentaryBill = false;
        
       sql.setLength(0);
        
       sql.append("select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
                + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ");
        List listSettlementType=objBaseService.funGetList(sql, "sql");
        if (null!=listSettlementType)
        {
            flgComplimentaryBill = true;
        }
        List listBillHD=objBaseService.funGetList(sql, "sql");
        sql.setLength(0);
        if (funIsDirectBillerBill(billNo, billhd))
        {
            flag_DirectBillerBlill = true;
            sql.append("select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
                    + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                    + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "where a.strBillNo='"+billNo+"'  ");
            flag_DirectBiller = true;
            listBillHD=objBaseService.funGetList(sql, "sql");
           
            if (null!=listBillHD)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		 dt = (Date) Array.get(obj,0);
//           		tabNo=(String) Array.get(obj,0);
    			}
            }	
           // rs_BillHD.next();
        }
        else
        {
        	sql.append("select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
                    + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                    + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                    + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ");
             listBillHD=objBaseService.funGetList(sql, "sql");
          
            if (null!=listBillHD)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
    		
	                tabNo = (String) Array.get(obj,0);
           		waiterNo=(String)Array.get(obj,1);
           		 dt=(Date)Array.get(obj,2);
           		
           		subTotal = (BigDecimal) Array.get(obj,5);
           		grandTot = (BigDecimal) Array.get(obj,7);
                user = (String) Array.get(obj,11);
                deliveryCharge = (BigDecimal) Array.get(obj,12);
                advAmount = (BigDecimal) Array.get(obj,13);
//                advDeposite=(String) Array.get(obj,13);
               
                
                paxNo=(int) Array.get(obj,16);
                
    			}
               
                if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
                {
                    waiterNo = "";
                }
                else
                {
                   
                sql.setLength(0);
                  sql.append("select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'");
                  List listQuery=objBaseService.funGetList(sql, "sql");
                   if (listQuery!=null)
	                   {
	                	for(int i=0; i<listQuery.size(); i++)
		    			{
	                		waiterName = (String) listQuery.get(0);      		
		    			}  
                    }
                 }
            }
            sql.setLength(0);
            sql.append("select strTableName from tbltablemaster where strTableNo='"+tabNo+"'");
            List listTblName=objBaseService.funGetList(sql, "sql");
          
            if (null!=listTblName)
            {
            	for(int i=0; i<listTblName.size(); i++)
	    			{
	           		
	           		tblName = (String) listTblName.get(0);           		
	    			}
                
            }
       
     }
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
		    
		    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
        // funPrintTakeAway(billhd, billNo, BillOut);
        if (flag_DirectBillerBlill)
        {
        	if (null!=listBillHD)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
           		subTotal = (BigDecimal)Array.get(obj,3);
           		grandTot = (BigDecimal)Array.get(obj,5);
           		grandTotal=grandTot.doubleValue();
                user = (String)Array.get(obj,9);
                deliveryCharge =(BigDecimal)Array.get(obj,10);
                advAmount = (BigDecimal)Array.get(obj,11);
                posName = (String)Array.get(obj,13);
//           		tabNo=(String) Array.get(obj,0);
                
    			}
           }
        	hmData.put("POS", posName);
        	hmData.put("BillNo", billNo);

            if (printTimeOnBillYN.equals("Y"))
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                 tabNo = ft.format(dt);
                hmData.put("DATE_TIME", tabNo);
            }
            else
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                tabNo = ft.format(dt);
                hmData.put("DATE_TIME", tabNo);
            }
            
            

            
        }
        else
        {
        	hmData.put("TABLE NAME", tblName);

            if (waiterName.trim().length() > 0)
            {
            	hmData.put("waiterName", waiterName);
            }
            hmData.put("POS",posName); 
            hmData.put("BillNo", billNo);
            hmData.put("PaxNo", paxNo);

            if (printTimeOnBillYN.equals("Y"))
            {
            	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
            	billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            	
            }
            else
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            }
            if (null!=listBillHD)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
            subTotal = (BigDecimal)Array.get(obj,5);
            grandTot = (BigDecimal)Array.get(obj,7);
            grandTotal=grandTot.doubleValue();
            user = (String)Array.get(obj,11);
            deliveryCharge = (BigDecimal)Array.get(obj,12);
            advAmount = (BigDecimal)Array.get(obj,13);
    			}
            }
        }

    
        List listOfBillDetail =new ArrayList();

        sql.setLength(0);
        sql.append("select sum(a.dblQuantity),a.strItemName "
        		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
                + " from " + billdtl + " a "
                 + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
                + " group by a.strItemCode");
               
        List listBillDtl = objBaseService.funGetList(sql, "sql");
      
        BigDecimal saleQuantity = null;
		BigDecimal amount=null;
		double saleQty=0.00;
        String itemCode="",itemName="";
        if (null!=listBillDtl)
        {
        	Map jObjList=new HashMap();
        	for(int i=0; i<listBillDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillDtl.get(i);
           		saleQuantity = (BigDecimal) Array.get(obj,0);
           		saleQty = saleQuantity.doubleValue();
           		itemName = (String) Array.get(obj,1);
           		amount = (BigDecimal) Array.get(obj,2); 
           		itemCode = (String) Array.get(obj,3); 
    			}
            
        
           
        	 sql.setLength(0);
             sql.append("select dblQuantity from " + billPromoDtl + " "
                    + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' "
                    + " and strPromoType='ItemWise' ");
            List listPromoItems=objBaseService.funGetList(sql, "sql");
            
            if (null!=listPromoItems)
            {
            	for(int i=0; i<listPromoItems.size(); i++)
	    			{
	           			Object[] obj = (Object[]) listPromoItems.get(i);
	           			quantity = (double) Array.get(obj,0);
	           			saleQty -= quantity;
	    			}
            }
            

             qty = String.valueOf(saleQty);
            if (qty.contains("."))
            {
                String decVal = qty.substring(qty.length() - 2, qty.length());
                if (Double.parseDouble(decVal) == 0)
                {
                    qty = qty.substring(0, qty.length() - 2);
                }
            }

            if (saleQty > 0)
            {
            	if (null!=listBillDtl)
                {
                	
                	for(int i=0; i<listBillDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillDtl.get(i);
		           		Map jObjList1=new HashMap();
		           		saleQuantity = (BigDecimal) Array.get(obj,0);
		           		saleQty = saleQuantity.doubleValue();
		           		itemName = (String) Array.get(obj,1);
		           		amount = (BigDecimal) Array.get(obj,2); 
		           		itemCode = (String) Array.get(obj,3); 
		           		jObjList1.put("qty", qty);
	                	jObjList1.put("amount", amount);
	                	jObjList1.put("itemName", itemName);
	                	listOfBillDetail.add(jObjList1);
		    			}
                	
//                    listOfBillDetail.put(jObjList);
                }	
            	
            	
            

                objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
 			    
 			    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
                
 			   sql.setLength(0);
 		       sql.append("select count(*) "
                        + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ");
                if (printZeroAmtModifierOnBill.equals("N"))
                {
                	 sql.append(" and  dblAmount !=0.00 ");
                }
               List listCount=objBaseService.funGetList(sql, "sql");
               String countntRec="";
               int cntRecord = 0;
               if(null!=listCount)
               {
            	   for(int i=0; i<listPromoItems.size(); i++)
	    			{
            	   Object[] obj = (Object[]) listPromoItems.get(i);
            	   countntRec = (String) Array.get(obj,0);   
            	    cntRecord = Integer.parseInt(countntRec);
	    			}
               }
               
                if (cntRecord > 0)
                {
                   sql.setLength(0);
      		       sql.append("select strModifierName,dblQuantity,dblAmount "
                            + " from " + billModifierdtl + " "
                            + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ");
                    if (printZeroAmtModifierOnBill.equals("N"))
                    {
                    	sql.append(" and  dblAmount !=0.00 ");
                    }
                   List listmodifierRecord=objBaseService.funGetList(sql, "sql");
                   String modifierName="";
                   double amt=0;
                   
                   if(null!=listmodifierRecord)
                    {
                	   for(int i=0; i<listmodifierRecord.size(); i++)
		    			{
                	   Object[] obj = (Object[]) listmodifierRecord.get(i);
                	   modifierName= (String) Array.get(obj,0);  
                	   quantity = (double) Array.get(obj,1); 
                	   amt=(double) Array.get(obj,2); 
                	    
		    			}
                        if (flgComplimentaryBill)
                        {
                        	jObjList.put("quantity", quantity);
                        	jObjList.put("amount", 0);
                        	jObjList.put("modifierName", modifierName.toUpperCase()); 
                        	
                            listOfBillDetail.add(jObjList);
                        }
                        else
                        {
                        	jObjList.put("quantity", quantity);
                        	jObjList.put("amount", amt);
                        	jObjList.put("modifierName", modifierName.toUpperCase());
                            	
                                listOfBillDetail.add(jObjList);
                            
                        }
                    }
                  
                }
            }
        }
       

        funPrintPromoItemsInBill(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

        List listOfDiscountDtl =new ArrayList();
        sql.setLength(0);
	    sql.append("select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
                + "from " + billDscFrom + " a ,tblreasonmaster b "
                + "where  a.strDiscReasonCode=b.strReasonCode "
                + "and a.strBillNo='" + billNo + "' ");
        List listDisc=objBaseService.funGetList(sql, "sql");

        boolean flag = true;
        double dbl=0,discAmt=0;
        String discAmount="";
        String discOnValue="",reasonName="",discRemarks="";
       if(null!=listDisc)
        {
    	   Map jObjList=new HashMap();
    	   for(int i=0; i<listDisc.size(); i++)
			{
    		   Object[] obj = (Object[]) listDisc.get(i);
    		   dbl= (double) Array.get(obj,0);
    		   discAmt= (double) Array.get(obj,1);
    		   discAmount= (String)Array.get(obj,1);
    		   discOnValue= (String) Array.get(obj,3);
    		   reasonName= (String) Array.get(obj,4);
    		   discRemarks= (String) Array.get(obj,5);
			}  
            if (flag)
            {
            	 jObjList=new HashMap();
            	jObjList.put("Discount","Discount");
//               
//                listOfDiscountDtl.put(jObjList);
                flag = false;
            }
            
            String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
            if (discText.length() > 30)
            {
                discText = discText.substring(0, 30);
            }
            else
            {
                discText = String.format("%-30s", discText);
            }

            String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
            hmData.put("Discount", discText + " " + discountOnItem);
          
            jObjList.put("discText", discText);
            jObjList.put("discAmt", discAmt);
//           
//            listOfDiscountDtl.put(jObjList);

            jObjList.put("Reason", reasonName);
           // listOfDiscountDtl.put(jObjList);

           
            jObjList.put("Remark",discRemarks);
            listOfDiscountDtl.add(jObjList);
        }

//        List<clsPOSBillDtl> listOfTaxDetail = new ArrayList<>();
        List listOfTaxDetail =new ArrayList();
        sql.setLength(0);
	    sql.append("select b.strTaxDesc,sum(a.dblTaxAmount) "
                + " from " + billtaxdtl + " a,tbltaxhd b "
                + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                + " group by a.strTaxCode");
       
        List listTax=objBaseService.funGetList(sql, "sql");
        String taxDesc="";
        double taxAmount=0;
        
        if(listTax.size()!=0)
        {
        if(null!=listTax)
        {
        	 Map jObjList=new HashMap();
        	for(int i=0; i<listTax.size(); i++)
			{
    		   Object[] obj = (Object[]) listTax.get(i);
    		   taxDesc= (String) Array.get(obj,0);
    		   taxAmount= (double) Array.get(obj,1);
    		  
			}  
            if (flgComplimentaryBill)
            {
              
            	jObjList.put("taxAmount", 0);
                jObjList.put("taxDesc", taxDesc);
                listOfTaxDetail.add(jObjList);
            }
            else
            {
            	jObjList.put("taxAmount", taxAmount);
            	jObjList.put("taxDesc", taxDesc);
                listOfTaxDetail.add(jObjList);
                listOfTaxDetail.add(jObjList);
            }
        }
        }
//        List<clsPOSBillDtl> listOfGrandTotalDtl = new ArrayList<>();
        List listOfGrandTotalDtl =new ArrayList();
        if (grandTotal> 0)
        {
        	 Map jObjList=new HashMap();
            objBillDtl = new clsPOSBillDtl();
            objBillDtl.setDblAmount(grandTotal);
            
            jObjList.put("grandTotal", grandTotal);
            listOfGrandTotalDtl.add(jObjList);
        }

//        List<clsPOSBillDtl> listOfSettlementDetail = new ArrayList<>();
        List listOfSettlementDetail =new ArrayList();
        //settlement breakup part
        sql.setLength(0);
	    sql.append("select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
                + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
                + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode");
        List listBill_Settlement=objBaseService.funGetList(sql, "sql");
       double settleAmt=0;
       String settleDesc="";
       
       if(null!=listBill_Settlement)
        {
    	   Map jObjList=new HashMap();
    	   for(int i=0; i<listBill_Settlement.size(); i++)
			{
    		   Object[] obj = (Object[]) listBill_Settlement.get(i);
    		   settleAmt= (double) Array.get(obj,0);
    		   settleDesc= (String) Array.get(obj,1);
    		  
			 
    	   
            if (flgComplimentaryBill)
            {
            	
                jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",0.00);
               // listOfSettlementDetail.put(jObjList);
            }
            else
            {
//                objBillDtl = new clsPOSBillDtl();
            	//jObjList = new HashMap();
               jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",settleAmt);
               // listOfSettlementDetail.put(jObjList);
            }
            listOfSettlementDetail.add(jObjList);
			} 
    	   
		} 
      
       
       sql.setLength(0);
	   sql.append("select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
                + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
                + " group by strBillNo");
        List listTenderAmt=objBaseService.funGetList(sql, "sql");
        double paidAmt=0,refundAmt=0;
        
        if (null!=listTenderAmt)
        {
        	 Map jObjList=new HashMap();
        	 for(int i=0; i<listTenderAmt.size(); i++)
    			{
        		   Object[] obj = (Object[]) listTenderAmt.get(i);
        		   paidAmt= (double) Array.get(obj,0);
        		   refundAmt= (double) Array.get(obj,2);
        		  
    			
            	if (flgComplimentaryBill)
                {
            		jObjList.put("PAID AMT","PAID AMT");
                   jObjList.put("paidAmt",0.00);
                }
                else
                {
                    jObjList.put("PAID AMT","PAID AMT");
                    jObjList.put("paidAmt",paidAmt);
                    if (refundAmt > 0)
                    {
                        jObjList.put("REFUND AMT","REFUND AMT");
                        jObjList.put("refundAmt",refundAmt);
                    }
                }
            	listOfSettlementDetail.add(jObjList);
    			}	           
        	 }
   
        if (flag_isHomeDelvBill)
        {
        	 sql.setLength(0);
     		 sql.append("select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'");
             List listCount=objBaseService.funGetList(sql, "sql");
            long count = 0;
            if (listCount!=null)
            {
            	for(int i=0; i<listCount.size(); i++)
    			{
            		count=((BigInteger)listCount.get(0)).longValue();
    			}  
            }
           
            hmData.put("CUSTOMER_COUNT", count);
        }

        List listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        
        Map jObjList=new HashMap();
        List listOfFooterDtl =new ArrayList();
        
        
        jObjList.put("Thank", "THANK YOU AND VISIT AGAIN !!!");
        listOfFooterDtl.add(jObjList);

        hmData.put("BillType", billType);
        hmData.put("listOfItemDtl", listOfBillDetail);
        hmData.put("listOfTaxDtl", listOfTaxDetail);
        hmData.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
        hmData.put("listOfServiceVatDetail", listOfServiceVatDetail);
        hmData.put("listOfFooterDtl", listOfFooterDtl);
        hmData.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
        hmData.put("listOfDiscountDtl", listOfDiscountDtl);
        hmData.put("listOfSettlementDetail", listOfSettlementDetail);
        
        int lengthListOfHomeDeliveryDtl=0;
        lengthListOfHomeDeliveryDtl = listOfHomeDeliveryDtl.size();
        listData.add(hmData);
        jObjRet.put("listData", listData);
        jObjRet.put("listOfBillDetail", listOfBillDetail);
        jObjRet.put("result", result);
        jObjRet.put("lengthListOfHomeDeliveryDtl", lengthListOfHomeDeliveryDtl);
        jObjRet.put("format", "Jasper1");
        jObjRet.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
        
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return jObjRet;
}


public Map funGenerateBillForJasperFormat2(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
{
	HashMap hm = new HashMap();
	Map json = new HashMap();
	 List listData =new ArrayList();
	 StringBuilder sql = new StringBuilder(); 
     
	  long listOfHomeDeliveryDtlSize=0;    
   // clsUtility objUtility = new clsUtility();
	Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
    String Linefor5 = "  --------------------------------------";
    try
    {
//    	Map jObjList=new HashMap();
    	Map hmData = new HashMap();
    	 Map jObj=new HashMap();
//    	 List<clsPOSBillDtl> listOfDiscountDtl = new ArrayList<>();
    	
        String user = "";
        String billhd;
        String billdtl;
        String billModifierdtl;
        String billSettlementdtl;
        String billtaxdtl;
        String billDscFrom = "tblbilldiscdtl";
        String billPromoDtl = "tblbillpromotiondtl";
        String billType = " ";

        if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
        {
            billhd = "tblqbillhd";
            billdtl = "tblqbilldtl";
            billModifierdtl = "tblqbillmodifierdtl";
            billSettlementdtl = "tblqbillsettlementdtl";
            billtaxdtl = "tblqbilltaxdtl";
            billDscFrom = "tblqbilldiscdtl";
            billPromoDtl = "tblqbillpromotiondtl";
        }
        else
        {
            if ("sales report".equalsIgnoreCase(formName))
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billDscFrom = "tblbilldiscdtl";
                billPromoDtl = "tblbillpromotiondtl";

                long dateDiff =funCompareDate(billDate,billDate);
                if (dateDiff > 0)
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }

                sql.setLength(0);
	       		sql.append("select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ");
               
        			List listBillTable = objBaseService.funGetList(sql, "sql");
        			int billCnt;
        			String billCount="";
        			if (listBillTable!=null)
     				{
     					for(int i=0; i<listBillTable.size(); i++)
     					{
     						
     						billCount =(String) listBillTable.get(i);
                     
     					}
     				}
                     billCnt = Integer.parseInt(billCount);
                    if (billCnt == 0)
                    {
                        billhd = "tblqbillhd";
                        billdtl = "tblqbilldtl";
                        billModifierdtl = "tblqbillmodifierdtl";
                        billSettlementdtl = "tblqbillsettlementdtl";
                        billtaxdtl = "tblqbilltaxdtl";
                        billDscFrom = "tblqbilldiscdtl";
                        billPromoDtl = "tblqbillpromotiondtl";
                    }
            }
            else
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billPromoDtl = "tblbillpromotiondtl";
            }
        }
        
        BigDecimal subTotal = null;
        BigDecimal grandTot = null;
        BigDecimal advAmount = null;
        BigDecimal deliveryCharge = null;
        String customerCode = "";
        double grandTotal=0.00;
        boolean flag_DirectBiller = false;

        if (clientCode.equalsIgnoreCase("117.001"))
        {
            if (POSCode.equals("P01"))
            {
            	hmData.put("posWiseHeading", "THE PREM'S HOTEL");
            }
            else if (POSCode.equals("P02"))
            {
            	hmData.put("posWiseHeading", "SWIG");
            }
        }

        boolean isReprint = false;
        if ("reprint".equalsIgnoreCase(reprint))
        {
            isReprint = true;
            hmData.put("duplicate", "[DUPLICATE]");
        }
        if (transType.equals("Void"))
        {
        	hmData.put("voidedBill", "VOIDED BILL");
        }

        boolean flag_isHomeDelvBill = false;
        sql.setLength(0);
		sql.append("select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                + "from tblhomedelivery where strBillNo='" + billNo + "'");
        List rs_HomeDelivery =objBaseService.funGetList(sql, "sql");
        String billNumber="",customerName="",customerAddress = null;
         if (rs_HomeDelivery!=null)
         {
         	Map jObj2=new HashMap();
         	for(int i=0; i<rs_HomeDelivery.size(); i++)
			{
       		Object[] obj = (Object[]) rs_HomeDelivery.get(i);	
       		billNumber = (String) Array.get(obj,0);
       		customerCode=(String)Array.get(obj,1);
       		customerAddress=(String)Array.get(obj,4);
    	   
			}
         }
         List listOfHomeDeliveryDtl =new ArrayList();
       
        Map jObjList=new HashMap();
        
        String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
        if(rs_HomeDelivery.size()!=0)
        {
        if (rs_HomeDelivery!=null)
        {
            flag_isHomeDelvBill = true;
           
            billType = "HOME DELIVERY";

            String SQL_CustomerDtl = "";

            if (customerAddress.equals("Home"))
            {
            	  sql.setLength(0);
	       		  sql.append("select a.strCustomerName,a.strCustAddress,a.strStreetName"
                        + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
                        + " from tblcustomermaster a left outer join tblbuildingmaster b "
                        + " on a.strBuldingCode=b.strBuildingCode "
                        + " where a.strCustomerCode='"+customerCode+"' ");
            }
            else
            {
            	  sql.setLength(0);
	       		  sql.append("select a.strCustomerName,a.strCustAddress,a.strOfficeStreetName"
                        + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
                        + " from tblcustomermaster a "
                        + " where a.strCustomerCode='"+customerCode+"' ");
            }
            List rs_CustomerDtl =objBaseService.funGetList(sql, "sql");
          
             if (rs_CustomerDtl!=null)
             {
            	 jObjList = new HashMap();
             	for(int i=0; i<rs_CustomerDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
    		
    		
           		custName = (String) Array.get(obj,0);
           		custAddress=(String)Array.get(obj,1);
           		officeStreetName=(String)Array.get(obj, 2);
           		officeLandmark=(String)Array.get(obj, 3);
           		officeArea=(String)Array.get(obj, 4);
           		officeCity=(String)Array.get(obj, 5);
           		officePinCode=(String)Array.get(obj, 6);
           		mobileNo=(String)Array.get(obj, 7);
    			}
             	hmData.put("NAME", custName);
//             	jObjList = new HashMap();
               jObjList.put("NAME",custName.toUpperCase());
               // listOfHomeDeliveryDtl.put(jObjList);
                // Building Name 
                String fulAddress = "";
                String fulAddress1 = "";
                String fullAddress = "";
                String add = custAddress;
                int strlen = add.length();
                String add1 = "";
                if (strlen < 28)
                {
                    add1 = add.substring(0, strlen);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                }
                else
                {
                    add1 = add.substring(0, 28);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                    //objBillDtl.setStrItemName("ADDRESS    :"+add1.toUpperCase());
                    // listOfHomeDeliveryDtl.add(objBillDtl);
                }

                // Street Name    
                String street = officeStreetName;
                String street1;
                int streetlen = street.length();
                for (int i = 0; i <= streetlen; i++)
                {
                    int end = 0;
                    end = i + 28;
                    if (streetlen > end)
                    {
                        street1 = street.substring(i, end);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                        }
                        fulAddress += "Street    :" + street1.toUpperCase();
                        i = end;
                    }
                    else
                    {
                        street1 = street.substring(i, streetlen);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                            fulAddress += "Street    :" + street1.toUpperCase();
                            i = streetlen + 1;
                        }
                    }
                }
                // Landmark Name    
                if (officeLandmark.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }

                    }
                    fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
                }

                // Area Name    
                if (officeArea.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }
                    }
                }

                // City Name    
                if (officeCity.trim().length() > 0)
                {
                    if (!officeCity.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeCity;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeCity;
                        }
                    }
                    fulAddress1 += "City    :" + officeCity.toUpperCase();
                }

                // Pin Code    
                if (officePinCode.trim().length() > 0)
                {
                    if (!officePinCode.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officePinCode;
                        }
                        else
                        {
                            fullAddress += "," + " " + officePinCode;
                        }
                    }
                    fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
                }
//                objBillDtl = new clsPOSBillDtl();
             //   jObjList = new HashMap();
               jObjList.put("Address",fullAddress);
               // listOfHomeDeliveryDtl.put(jObjList);

                hmData.put("FullAddress", fullAddress);
                if (mobileNo.isEmpty())
                {
                	hmData.put("MOBILE_NO", "");
//                	jObjList = new HashMap();
                	jObjList.put("MOBILE_NO"," ");
                    //listOfHomeDeliveryDtl.put(jObjList);
                }
                else
                {
                	hmData.put("MOBILE_NO", mobileNo);
//                	jObjList = new HashMap();
                	jObjList.put("MOBILE_NO",mobileNo);
                    //listOfHomeDeliveryDtl.put(jObjList);
                }
                listOfHomeDeliveryDtl.add(jObjList);
            }
        }

            if (null != officeStreetName  && officeStreetName .trim().length() > 0)
            {
                String[] delBoys = officeStreetName .split(",");
                StringBuilder strIN = new StringBuilder("(");
                for (int i = 0; i < delBoys.length; i++)
                {
                    if (i == 0)
                    {
                        strIN.append("'" + delBoys[i] + "'");
                    }
                    else
                    {
                        strIN.append(",'" + delBoys[i] + "'");
                    }
                }
                strIN.append(")");
                sql.setLength(0);
	       		sql.append("select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ");
                List rs_DeliveryBoyDtl = objBaseService.funGetList(sql, "sql");
                String dpName="";
               if (rs_DeliveryBoyDtl!=null)
               {
                strIN.setLength(0);
                for(int i=0; i<rs_DeliveryBoyDtl.size(); i++)
    			{
                	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
    		
    		
                	dpName = (String) Array.get(obj,2);
        	  
               
                   if (i == 0)
                    {
                        strIN.append(dpName.toUpperCase());
                    }
                    else
                    {
                        strIN.append("," + dpName.toUpperCase());
                    }
                }
               }
                if (strIN.toString().isEmpty())
                {
                	hmData.put("DELV BOY", "");
                }
                else
                {
                	hmData.put("DELV BOY", "Delivery Boy : " + strIN);
                	//jObjList = new HashMap();
                	jObjList.put("DELV BOY",strIN);
                    listOfHomeDeliveryDtl.add(jObjList);
                }
               
            }
            else
            {
            	hmData.put("DELV BOY", "");
            }
            //listOfHomeDeliveryDtl.put(jObjList);
           // listOfHomeDeliveryDtlSize=listOfHomeDeliveryDtl.length();
        }
    

        int result = funPrintTakeAwayForJasper(billhd, billNo);
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
        String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
        String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
        String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
         
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
        String cityName = objSetupParameter.get("gCityName").toString();
        
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
        String clientName = objSetupParameter.get("gClientName").toString();
        
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
        String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
          
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
        String clientEmail = objSetupParameter.get("gClientEmail").toString();
        
        
        if (result == 1)
        {
            billType = "Take Away";
        }
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
	    
	    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
        {
	    	hmData.put("TAX_INVOICE", "TAX INVOICE");
        }
        if ( clientCode.equals("047.001") && POSCode.equals("P03"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
        else if (clientCode.equals("047.001") && POSCode.equals("P02"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
        else if (clientCode.toString().equals("092.001") || clientCode.toString().equals("092.002") || clientCode.toString().equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
        {
        	hmData.put("ClientName", "SSPL");
        	hmData.put("ClientAddress1",clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            if (cityName.trim().length() > 0)
            {
            	hmData.put("ClientCity", cityName);
            }
        }
        else
        {
        	hmData.put("ClientName",clientName);
        	hmData.put("ClientAddress1", clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            if (cityName.trim().length() > 0)
            {
            	hmData.put("ClientCity", cityName);
            }
        }

        hmData.put("TEL NO", clientTelNo);
        hmData.put("EMAIL ID", clientEmail);
        hmData.put("Line", Linefor5);

        String SQL_BillHD = "",billDt="",qty="";
        String waiterName = "";
        Date dt = null; 
        double quantity=0;
        String waiterNo = "";
        String tblName = "";
        String advDeposite="";
        String sqlTblName = "";
        String tabNo = "";
        String posName="";
        int paxNo = 0;
        boolean flag_DirectBillerBlill = false;
        boolean flgComplimentaryBill = false;
        List listBillHD=objBaseService.funGetList(sql, "sql");
        sql.setLength(0);
		sql.append("select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
                + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ");
        List listSettlementType=objBaseService.funGetList(sql, "sql");
        if (listSettlementType!=null)
        {
            flgComplimentaryBill = true;
        }
      

        if (funIsDirectBillerBill(billNo, billhd))
        {
            flag_DirectBillerBlill = true;
            sql.setLength(0);
    		sql.append("select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
                    + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                    + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "where a.strBillNo='"+billNo+"' ");
            flag_DirectBiller = true;
            listBillHD=objBaseService.funGetList(sql, "sql");
           
            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		 dt = (Date) Array.get(obj,0);
//           		tabNo=(String) Array.get(obj,0);
    			}
            }	
        }
        else
        {
        	sql.setLength(0);
     		sql.append("select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
                    + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                    + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                    + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ");
            listBillHD=objBaseService.funGetList(sql, "sql");
           
            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
    		
    		
           		tabNo = (String) Array.get(obj,0);
           		waiterNo=(String)Array.get(obj,1);
           		 dt=(Date)Array.get(obj,2);
           		
           		subTotal = (BigDecimal) Array.get(obj,5);
           		grandTot = (BigDecimal) Array.get(obj,7);
                user = (String) Array.get(obj,11);
                deliveryCharge = (BigDecimal) Array.get(obj,12);
                advAmount = (BigDecimal) Array.get(obj,13);
    			}
             
                if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
                {
                    waiterNo = "";
                }
                else
                {
                    
                   sql.setLength(0);
             	   sql.append("select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'");
                   List listQuery=objBaseService.funGetList(sql, "sql");
                       if (listQuery!=null)
   	                   {
   	                	for(int i=0; i<listQuery.size(); i++)
   		    			{
   		           	
   	   		           	waiterName = (String)listQuery.get(0);           		
   		    			}
                        
                        }
                     }
                }

            sql.setLength(0);
     		sql.append("select strTableName from tbltablemaster where strTableNo='"+tabNo+"'");
            List listTblName=objBaseService.funGetList(sql, "sql");
          
            if (listTblName!=null)
            {
            	for(int i=0; i<listTblName.size(); i++)
	    			{
	           		
	           		tblName = (String) listTblName.get(0);           		
	    			}
                
            }
        }

        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
	    
	    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
       
        // funPrintTakeAway(billhd, billNo, BillOut);
        if (flag_DirectBillerBlill)
        {
        	if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
           		subTotal = (BigDecimal)Array.get(obj,3);
           		grandTot = (BigDecimal)Array.get(obj,5);
           		grandTotal=grandTot.doubleValue();
                user = (String)Array.get(obj,9);
                deliveryCharge =(BigDecimal)Array.get(obj,10);
                advAmount = (BigDecimal)Array.get(obj,11);
                posName = (String)Array.get(obj,13);
//           		tabNo=(String) Array.get(obj,0);
    			}
            }
        	hmData.put("POS", posName);
        	hmData.put("BillNo", billNo);

            if (printTimeOnBillYN.equals("Y"))
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                tabNo = ft.format(dt);
                hmData.put("DATE_TIME",tabNo);
            }
            else
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                tabNo = ft.format(dt);
                hmData.put("DATE_TIME",tabNo);
            }

        }
        else
        {
        	hmData.put("TABLE NAME", tblName);

            if (waiterName.trim().length() > 0)
            {
            	hmData.put("waiterName", waiterName);
            }
            hmData.put("POS",posName);
            hmData.put("BillNo", billNo);
            hmData.put("PaxNo", paxNo);

            if (printTimeOnBillYN.equals("Y"))
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            }
            else
            {
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            }
            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
            subTotal = (BigDecimal)Array.get(obj,5);
            grandTot = (BigDecimal)Array.get(obj,7);
            grandTotal=grandTot.doubleValue();
            user = (String)Array.get(obj,11);
            deliveryCharge = (BigDecimal)Array.get(obj,12);
            advAmount = (BigDecimal)Array.get(obj,13);
    			}
            }
        }


        List listOfBillDetail = new ArrayList();

        sql.setLength(0);
 		sql.append("select sum(a.dblQuantity),a.strItemName "
        		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,a.dblRate,sum(a.dblDiscountAmt)  "
                    + " from " + billdtl + " a "
                     + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
                    + " group by a.strItemCode");
         List listBillDtl = objBaseService.funGetList(sql, "sql");
      
        BigDecimal saleQuantity = null;
		BigDecimal amount=null;
		double saleQty=0.00;
        String itemCode="",itemName="";

      if (listBillDtl!=null)
        {
//      	Map jObjList=new HashMap();
      	for(int i=0; i<listBillDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillDtl.get(i);
           		saleQuantity = (BigDecimal) Array.get(obj,0);
           		saleQty = saleQuantity.doubleValue();
           		itemName = (String) Array.get(obj,1);
           		amount = (BigDecimal) Array.get(obj,2); 
           		itemCode = (String) Array.get(obj,3); 
    			}
      	
	      	sql.setLength(0);
	 		sql.append("select dblQuantity from " + billPromoDtl + " "
                    + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' "
                    + " and strPromoType='ItemWise' ");
            List listPromoItems= objBaseService.funGetList(sql, "sql");
            
            if (listPromoItems!=null)
            {
            	for(int i=0; i<listPromoItems.size(); i++)
	    			{
	           			Object[] obj = (Object[]) listPromoItems.get(i);
	           			quantity = (double) Array.get(obj,0);
	           			saleQty -= quantity;
	    			}
            }
            qty = String.valueOf(saleQty);
            if (qty.contains("."))
            {
                String decVal = qty.substring(qty.length() - 2, qty.length());
                if (Double.parseDouble(decVal) == 0)
                {
                    qty = qty.substring(0, qty.length() - 2);
                }
            }
           double dblAmount=0.00,rate=0.00,discountAmt=0.00;
           BigDecimal bd=null,bd1=null,bd2=null;
            if (saleQty > 0)
            {
            	
            	if (listBillDtl!=null)
                {
                	
                	for(int i=0; i<listBillDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillDtl.get(i);
		           		itemName = (String) Array.get(obj,1);
		           		bd=(BigDecimal) Array.get(obj, 2);
		           		bd1=(BigDecimal) Array.get(obj, 5);
		           		bd2=(BigDecimal)Array.get(obj, 6);
		           		
		           		
		           		dblAmount = bd.doubleValue(); // The double you want
		           		rate = bd1.doubleValue(); 
		           		discountAmt = bd2.doubleValue(); 
		           		jObjList = new HashMap();
		           		jObjList.put("saleQty",saleQty);
		           		jObjList.put("dblAmount",dblAmount);
		           		jObjList.put("itemName",itemName);
		           		jObjList.put("rate",rate);
		           		jObjList.put("discountAmt",discountAmt);

	                    listOfBillDetail.add(jObjList);

		    			}
                }	
                
            	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
     			    
     		    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
                    
     		   sql.setLength(0);
   	 		   sql.append("select count(*) "
                        + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ");
                if (printZeroAmtModifierOnBill.equals("N"))
                {
                	sql.append(" and  dblAmount !=0.00 ");
                }
                  List listCount= objBaseService.funGetList(sql, "sql");
                   String countntRec="";
                   int cntRecord = 0;
                   if(listCount!=null)
                   {
                	   for(int i=0; i<listPromoItems.size(); i++)
		    			{
                	   Object[] obj = (Object[]) listPromoItems.get(i);
                	   countntRec = (String) Array.get(obj,0);   
                	    cntRecord = Integer.parseInt(countntRec);
		    			}
                   }
                
                if (cntRecord > 0)
                {
                	 sql.setLength(0);
         	 		  sql.append("select strModifierName,dblQuantity,dblAmount,dblRate,dblDiscAmt "
                            + " from " + billModifierdtl + " "
                            + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"'");
                    if (printZeroAmtModifierOnBill.equals("N"))
                    {
                    	 sql.append(" and  dblAmount !=0.00 ");
                    }
                       List listmodifierRecord=objBaseService.funGetList(sql, "sql");
                       String modifierName="";
                       double amt=0;
                       
                       if(listmodifierRecord!=null)
                        {
                    	   for(int i=0; i<listmodifierRecord.size(); i++)
			    			{
	                	   Object[] obj = (Object[]) listmodifierRecord.get(i);
	                	   modifierName= (String) Array.get(obj,0);  
	                	   quantity = (double) Array.get(obj,1); 
	                	   amt=(double) Array.get(obj,2); 
	                	   rate=(double) Array.get(obj,3);
	                	   discountAmt=(double) Array.get(obj,4);
			    			}
                          
                        if (flgComplimentaryBill)
                        {
                        	jObjList = new HashMap();
                            jObjList.put("quantity",quantity);
                            jObjList.put("amt",0);
                            jObjList.put("modifierName",modifierName.toUpperCase());
                            jObjList.put("rate",rate);
                            jObjList.put("discountAmt",discountAmt);
                            listOfBillDetail.add(jObjList);
                        }
                        else
                        {
                        	jObjList = new HashMap();
                        	 jObjList.put("quantity",quantity);
                        	 jObjList.put("amt",amt);
                        	 jObjList.put("modifierName",modifierName.toUpperCase());
                        	 jObjList.put("rate",rate);
                        	 jObjList.put("discountAmt",discountAmt);
                            listOfBillDetail.add(jObjList);
                        }
                    }
                   
                }
            }
    }
        
           
        funPrintPromoItemsInBillFormat2(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

//        List<clsPOSBillDtl> listOfDiscountDtl = new ArrayList<>();
        List listOfDiscountDtl = new ArrayList();
        sql.setLength(0);
		 sql.append("select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
                + "from " + billDscFrom + " a ,tblreasonmaster b "
                + "where  a.strDiscReasonCode=b.strReasonCode "
                + "and a.strBillNo='" + billNo + "' ");
        List listDisc=objBaseService.funGetList(sql, "sql");

        boolean flag = true;
        double dbl=0,discAmt=0;
        String discAmount="";
        String discOnValue="",reasonName="",discRemarks="";
       if(listDisc!=null)
        {
    	   jObjList = new HashMap();
    	   for(int i=0; i<listDisc.size(); i++)
			{
    		   Object[] obj = (Object[]) listDisc.get(i);
    		   dbl= (double) Array.get(obj,0);
    		   discAmt= (double) Array.get(obj,1);
    		   discAmount= (String)Array.get(obj,2);
    		   discOnValue= (String) Array.get(obj,3);
    		   reasonName= (String) Array.get(obj,4);
    		   discRemarks= (String) Array.get(obj,5);
			}  
      
            if (flag)
            {
            	  
            	jObjList = new HashMap();
            	jObjList.put("Discount","Discount");
                //listOfDiscountDtl.put(jObjList);
                flag = false;
            }
            
            String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
            if (discText.length() > 30)
            {
                discText = discText.substring(0, 30);
            }
            else
            {
                discText = String.format("%-30s", discText);
            }

            String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
            hmData.put("Discount", discText + " " + discountOnItem);
            jObjList.put("discText",discText);
            //jObjList = new HashMap();
            jObjList.put("discAmt",discAmt);
           // listOfDiscountDtl.put(jObjList);

            //jObjList = new HashMap();
            jObjList.put("Reason",reasonName);
            //listOfDiscountDtl.put(jObjList);

            //jObjList = new HashMap();
            jObjList.put("Remark",discRemarks);
            listOfDiscountDtl.add(jObjList);
        }

       List listOfTaxDetail = new ArrayList();
       sql.setLength(0);
	   sql.append("select b.strTaxDesc,sum(a.dblTaxAmount) "
                + " from " + billtaxdtl + " a,tbltaxhd b "
                + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                + " group by a.strTaxCode");
        List listTax=objBaseService.funGetList(sql, "sql");
        String taxDesc="";
        double taxAmount=0;
        
        if(listTax!=null)
        {
        	for(int i=0; i<listTax.size(); i++)
			{
    		   Object[] obj = (Object[]) listTax.get(i);
    		   taxDesc= (String) Array.get(obj,0);
    		   taxAmount= (double) Array.get(obj,1);
    		  
			
        	
            if (flgComplimentaryBill)
            {
            	jObjList = new HashMap();
            	jObjList.put("taxAmount",0);
                jObjList.put("taxDesc",taxDesc);
                listOfTaxDetail.add(jObjList);
            }
            else
            {
            	jObjList = new HashMap();
            	jObjList.put("taxAmount",taxAmount);
                jObjList.put("taxDesc",taxDesc);
                listOfTaxDetail.add(jObjList);
            }
			}
        }
       
//        List<clsPOSBillDtl> listOfGrandTotalDtl = new ArrayList<>();
        List listOfGrandTotalDtl = new ArrayList();
        if (grandTotal > 0)
        {
        	jObjList = new HashMap();
        	jObjList.put("grandTotal",grandTotal);
            listOfGrandTotalDtl.add(jObjList);
        }

//        List<clsPOSBillDtl> listOfSettlementDetail = new ArrayList<>();
        List listOfSettlementDetail = new ArrayList();
        //settlement breakup part
       sql.setLength(0);
 	   sql.append("select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
                + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
                + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode");
         List listBill_Settlement=objBaseService.funGetList(sql, "sql");
       double settleAmt=0;
       String settleDesc="";
       
       if(listBill_Settlement!=null)
        {
    	  jObjList=new HashMap();
    	   for(int i=0; i<listBill_Settlement.size(); i++)
			{
    		   Object[] obj = (Object[]) listBill_Settlement.get(i);
    		   settleAmt= (double) Array.get(obj,0);
    		   settleDesc= (String) Array.get(obj,1);
    		  
			 
    	   
            if (flgComplimentaryBill)
            {
            	
                jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",0.00);
               // listOfSettlementDetail.put(jObjList);
            }
            else
            {
//                objBillDtl = new clsPOSBillDtl();
            	//jObjList = new HashMap();
               jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",settleAmt);
               // listOfSettlementDetail.put(jObjList);
            }
            listOfSettlementDetail.add(jObjList);
			} 
    	   
        }
       

       sql.setLength(0);
 	   sql.append("select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
                + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
                + " group by strBillNo") ;
        List listTenderAmt=objBaseService.funGetList(sql, "sql");
        double paidAmt=0,refundAmt=0;
        
        if (listTenderAmt!=null)
        {  
        	//jObjList = new HashMap();
        	for(int i=0; i<listTenderAmt.size(); i++)
			{
    		   Object[] obj = (Object[]) listTenderAmt.get(i);
    		   paidAmt= (double) Array.get(obj,0);
    		   refundAmt= (double) Array.get(obj,2);
    		  
			
        	if (flgComplimentaryBill)
            {
        		//jObjList = new HashMap();
        		jObjList.put("PAID AMT","PAID AMT");
               jObjList.put("paidAmt",0.00);
                //listOfSettlementDetail.put(jObjList);
            }
            else
            {
            	//jObjList = new HashMap();
                jObjList.put("PAID AMT","PAID AMT");
                jObjList.put("paidAmt",paidAmt);
                //listOfSettlementDetail.put(jObjList);
                if (refundAmt > 0)
                {
                	//jObjList = new HashMap();
                    jObjList.put("REFUND AMT","REFUND AMT");
                    jObjList.put("refundAmt",refundAmt);
                   // listOfSettlementDetail.put(jObjList);
                }
            }
        	listOfSettlementDetail.add(jObjList);
			}
        	
        }
        
        if (flag_isHomeDelvBill)
        {
        	sql.setLength(0);
       	    sql.append("select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'");
            List listCount=objBaseService.funGetList(sql, "sql");
            long count = 0;
            if (listCount!=null)
            {
            	for(int i=0; i<listCount.size(); i++)
    			{
        		   //Object[] obj = (Object[]) listCount.get(i);
//        		   String cnt= (String) Array.get(obj,0);
//        		   
            		count=((BigInteger)listCount.get(0)).longValue();
        		  
    			}  
            }
            hmData.put("CUSTOMER_COUNT", count);
        }

        sql.setLength(0);
   	    sql.append("select b.longMobileNo,b.strCustomerName "
                + "from " + billhd + " a,tblcustomermaster b "
                + "where a.strCustomerCode=b.strCustomerCode "
                + "and a.strBillNo='" + billNo + "'");
        List listCount=objBaseService.funGetList(sql, "sql");
      long count = 0;
     
//        List<clsPOSBillDtl> listOfCustomerDtl = new ArrayList<>();
      List listOfCustomerDtl = new ArrayList(); 
        if (listCount!=null)
        {
        	for(int i=0; i<listCount.size(); i++)
			{
    		   Object[] obj = (Object[]) listCount.get(i);
//    		   String cnt= (String) Array.get(obj,0);
//    		   
        		mobileNo=(String) Array.get(obj,0);
        		customerName=(String) Array.get(obj,1);
			}  
        	jObjList = new HashMap();
           jObjList.put("CUSTOMER NAME:",customerName);
           // listOfCustomerDtl.put(jObjList);
            
//            jObjList = new HashMap();
            jObjList.put("mobileNo",mobileNo);
            listOfCustomerDtl.add(jObjList);
          
        }

        List listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
//        List<clsPOSBillDtl> listOfFooterDtl = new ArrayList<>();
        List listOfFooterDtl = new ArrayList();
        jObjList = new HashMap();
        jObjList.put("Thank","THANK YOU AND VISIT AGAIN !!!");
        listOfFooterDtl.add(jObjList);

        hmData.put("BillType", billType);
        hmData.put("listOfItemDtl", listOfBillDetail);
        hmData.put("listOfTaxDtl", listOfTaxDetail);
        hmData.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
        hmData.put("listOfServiceVatDetail", listOfServiceVatDetail);
        hmData.put("listOfFooterDtl", listOfFooterDtl);
        hmData.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
        hmData.put("listOfDiscountDtl", listOfDiscountDtl);
        hmData.put("listOfSettlementDetail", listOfSettlementDetail);
        hmData.put("listOfCustomerDtl", listOfCustomerDtl);
        listData.add(hmData);
      
        json.put("listOfBillDetail", listOfBillDetail);
        json.put("format", "Jasper2");
        json.put("result", result);
        json.put("listData", listData);
        json.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
//        json.put("listOfHomeDeliveryDtlSize", listOfHomeDeliveryDtlSize);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    finally
    {
    	return json;
    }
} 
public Map funGenerateBillForJasperFormat3(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
{

    HashMap hm = new HashMap();
    Map json = new HashMap();
	List listData =new ArrayList();
	StringBuilder sql = new StringBuilder();   
    
   // clsUtility objUtility = new clsUtility();
    Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
    String Linefor5 = "  --------------------------------------";
    try
    {
//    	Map jObjList=new HashMap();
    	Map hmData = new HashMap();
        String user = "";
        String billhd;
        String billdtl;
        String billModifierdtl;
        String billSettlementdtl;
        String billtaxdtl;
        String billDscFrom = "tblbilldiscdtl";
        String billPromoDtl = "tblbillpromotiondtl";
        String billType = " ";

        if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
        {
            billhd = "tblqbillhd";
            billdtl = "tblqbilldtl";
            billModifierdtl = "tblqbillmodifierdtl";
            billSettlementdtl = "tblqbillsettlementdtl";
            billtaxdtl = "tblqbilltaxdtl";
            billDscFrom = "tblqbilldiscdtl";
            billPromoDtl = "tblqbillpromotiondtl";
        }
        else
        {
            if ("sales report".equalsIgnoreCase(formName))
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billDscFrom = "tblbilldiscdtl";
                billPromoDtl = "tblbillpromotiondtl";

                long dateDiff = funCompareDate(billDate, billDate);
                if (dateDiff > 0)
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }

                sql.setLength(0);
	       		sql.append("select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ");
                List listBillTable = objBaseService.funGetList(sql, "sql");
    			int billCnt;
    			String billCount="";
    			if (listBillTable!=null)
 				{
 					for(int i=0; i<listBillTable.size(); i++)
 					{
 						
 						billCount =(String) listBillTable.get(i);
                 
 					}
 				}
                 billCnt = Integer.parseInt(billCount);
                if (billCnt == 0)
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }
            }
            else
            {
                billhd = "tblbillhd";
                billdtl = "tblbilldtl";
                billModifierdtl = "tblbillmodifierdtl";
                billSettlementdtl = "tblbillsettlementdtl";
                billtaxdtl = "tblbilltaxdtl";
                billPromoDtl = "tblbillpromotiondtl";
            }
      }
      
        BigDecimal subTotal = null;
        BigDecimal grandTot = null;
        BigDecimal advAmount = null;
        BigDecimal deliveryCharge = null;
        String customerCode = "";
        double grandTotal=0.00;
        boolean flag_DirectBiller = false;
        if (clientCode.equals("117.001"))
        {
            if (POSCode.equals("P01"))
            {
            	hmData.put("posWiseHeading", "THE PREM'S HOTEL");
            }
            else if (POSCode.equals("P02"))
            {
            	hmData.put("posWiseHeading", "SWIG");
            }
        }

        boolean isReprint = false;
        if ("reprint".equalsIgnoreCase(reprint))
        {
            isReprint = true;
            hmData.put("duplicate", "[DUPLICATE]");
        }
        if (transType.equals("Void"))
        {
        	hmData.put("voidedBill", "VOIDED BILL");
        }

        boolean flag_isHomeDelvBill = false;
        sql.setLength(0);
   		sql.append("select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                + "from tblhomedelivery where strBillNo='"+billNo+"'");
        List rs_HomeDelivery =objBaseService.funGetList(sql, "sql");
        String billNumber="",customerName="",customerAddress = null;
         if (rs_HomeDelivery!=null)
         {
         	Map jObj2=new HashMap();
         	for(int i=0; i<rs_HomeDelivery.size(); i++)
			{
       		Object[] obj = (Object[]) rs_HomeDelivery.get(i);
		
		
       		billNumber = (String) Array.get(obj,0);
       		customerCode=(String)Array.get(obj,1);
       		customerAddress=(String)Array.get(obj,4);
    	   
			}
         }
//        List<clsPOSBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
         List listOfHomeDeliveryDtl =new ArrayList();
       
        Map jObjList=new HashMap();
        if (rs_HomeDelivery.size()!=0)
        {
        	if (rs_HomeDelivery!=null)
            {
            flag_isHomeDelvBill = true;
          
            billType = "HOME DELIVERY";

            String SQL_CustomerDtl = "";

            if (customerAddress.equals("Home"))
            {
            	sql.setLength(0);
	       		sql.append("select a.strCustomerName,a.strBuildingName,a.strStreetName"
                        + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
                        + " from tblcustomermaster a left outer join tblbuildingmaster b "
                        + " on a.strBuldingCode=b.strBuildingCode "
                        + " where a.strCustomerCode='"+customerCode+"'");
            }
            else
            {
            	sql.setLength(0);
	       		sql.append("select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
                        + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
                        + " from tblcustomermaster a "
                        + " where a.strCustomerCode='"+customerCode+"'");
            }
            List rs_CustomerDtl =objBaseService.funGetList(sql, "sql");
           String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
           if (rs_CustomerDtl!=null)
             {
            	 jObjList = new HashMap();
             	for(int i=0; i<rs_CustomerDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
    		
    		
           		custName = (String) Array.get(obj,0);
           		custAddress=(String)Array.get(obj,1);
           		officeStreetName=(String)Array.get(obj, 2);
           		officeLandmark=(String)Array.get(obj, 3);
           		officeArea=(String)Array.get(obj, 4);
           		officeCity=(String)Array.get(obj, 5);
           		officePinCode=(String)Array.get(obj, 6);
           		mobileNo=(String)Array.get(obj, 7);
    			}
             	hmData.put("NAME", custName);
//             	jObjList = new HashMap();
               jObjList.put("NAME",custName.toUpperCase());
               // listOfHomeDeliveryDtl.put(jObjList);
                // Building Name 
                String fulAddress = "";
                String fulAddress1 = "";
                String fullAddress = "";
                String add = custAddress;
                int strlen = add.length();
                String add1 = "";
                if (strlen < 28)
                {
                    add1 = add.substring(0, strlen);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                }
                else
                {
                    add1 = add.substring(0, 28);
                    if (!add1.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + add1;
                        }
                        else
                        {
                            fullAddress += "," + " " + add1;
                        }
                    }
                    fulAddress += "ADDRESS    :" + add1.toUpperCase();
                    //objBillDtl.setStrItemName("ADDRESS    :"+add1.toUpperCase());
                    // listOfHomeDeliveryDtl.add(objBillDtl);
                }

                // Street Name    
                String street = officeStreetName;
                String street1;
                int streetlen = street.length();
                for (int i = 0; i <= streetlen; i++)
                {
                    int end = 0;
                    end = i + 28;
                    if (streetlen > end)
                    {
                        street1 = street.substring(i, end);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                        }
                        fulAddress += "Street    :" + street1.toUpperCase();
                        i = end;
                    }
                    else
                    {
                        street1 = street.substring(i, streetlen);
                        if (!street1.isEmpty())
                        {
                            if (fullAddress.isEmpty())
                            {
                                fullAddress += " " + street1;
                            }
                            else
                            {
                                fullAddress += "," + " " + street1;
                            }

                            fulAddress += "Street    :" + street1.toUpperCase();
                            i = streetlen + 1;
                        }
                    }
                }
                // Landmark Name    
                if (officeLandmark.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }

                    }
                    fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
                }

                // Area Name    
                if (officeArea.trim().length() > 0)
                {
                    if (!officeLandmark.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeLandmark;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeLandmark;
                        }
                    }
                }

                // City Name    
                if (officeCity.trim().length() > 0)
                {
                    if (!officeCity.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officeCity;
                        }
                        else
                        {
                            fullAddress += "," + " " + officeCity;
                        }
                    }
                    fulAddress1 += "City    :" + officeCity.toUpperCase();
                }

                // Pin Code    
                if (officePinCode.trim().length() > 0)
                {
                    if (!officePinCode.isEmpty())
                    {
                        if (fullAddress.isEmpty())
                        {
                            fullAddress += " " + officePinCode;
                        }
                        else
                        {
                            fullAddress += "," + " " + officePinCode;
                        }
                    }
                    fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
                }
                jObjList.put("Address",fullAddress);

                hmData.put("FullAddress", fullAddress);
                if (mobileNo.isEmpty())
                {
                	hmData.put("MOBILE_NO", "");
//                	jObjList = new HashMap();
                	jObjList.put("MOBILE_NO"," ");
                    //listOfHomeDeliveryDtl.put(jObjList);
                }
                else
                {
                	hmData.put("MOBILE_NO", mobileNo);
//                	jObjList = new HashMap();
                	jObjList.put("MOBILE_NO",mobileNo);
                    //listOfHomeDeliveryDtl.put(jObjList);
                }
                listOfHomeDeliveryDtl.add(jObjList);
            }

            if (null != officeStreetName  && officeStreetName.trim().length() > 0)
            {
                String[] delBoys = officeStreetName.split(",");
                StringBuilder strIN = new StringBuilder("(");
                for (int i = 0; i < delBoys.length; i++)
                {
                    if (i == 0)
                    {
                        strIN.append("'" + delBoys[i] + "'");
                    }
                    else
                    {
                        strIN.append(",'" + delBoys[i] + "'");
                    }
                }
                
                strIN.append(")");
                sql.setLength(0);
	       		sql.append("select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ");
                List rs_DeliveryBoyDtl =objBaseService.funGetList(sql, "sql");
                String dpName="";
               if (rs_DeliveryBoyDtl!=null)
               {
            	   strIN.setLength(0);
                for (int i = 0; i<rs_DeliveryBoyDtl.size(); i++)
                {
                	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
	        		
	        		
                	dpName = (String) Array.get(obj,2);
                	if (i == 0)
                    {
                        strIN.append(dpName.toUpperCase());
                    }
                    else
                    {
                        strIN.append("," + dpName.toUpperCase());
                    }
                }
               }
                if (strIN.toString().isEmpty())
                {
                	hmData.put("DELV BOY", "");
                }
                else
                {
                	hmData.put("DELV BOY", "Delivery Boy : " + strIN);
                	//jObjList = new HashMap();
                	jObjList.put("DELV BOY",strIN);
                    listOfHomeDeliveryDtl.add(jObjList);
                }
               
            }
            else
            {
            	hmData.put("DELV BOY", "");
            }
        }
        }
            int result = funPrintTakeAwayForJasper(billhd, billNo);
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
            String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
             
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
            String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
             
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
            String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
             
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
            String cityName = objSetupParameter.get("gCityName").toString();
            
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
            String clientName = objSetupParameter.get("gClientName").toString();
            
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
            String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
              
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
            String clientEmail = objSetupParameter.get("gClientEmail").toString();
            
        if (result == 1)
        {
            billType = "Take Away";
        }
        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
		    
		    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
		    {
		    	hmData.put("TAX_INVOICE", "TAX INVOICE");
        }
        if (clientCode.equals("047.001") && POSCode.equals("P03"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
        else if (clientCode.equals("047.001") && POSCode.equals("P02"))
        {
        	hmData.put("ClientName", "SHRI SHAM CATERERS");
            String cAddr1 = "Flat No.7, Mon Amour,";
            String cAddr2 = "Thorat Colony,Prabhat Road,";
            String cAddr3 = " Erandwane, Pune 411 004.";
            String cAddr4 = "Approved Caterers of";
            String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
            hmData.put("ClientAddress1", cAddr1 + cAddr2);
            hmData.put("ClientAddress2", cAddr3 + cAddr4);
            hmData.put("ClientAddress3", cAddr5);
        }
        else if (clientCode.equals("092.001") ||clientCode.equals("092.002") || clientCode.equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
        {
        	hmData.put("ClientName", "SSPL");
        	hmData.put("ClientAddress1", clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            if (cityName.trim().length() > 0)
            {
            	hmData.put("ClientCity", cityName);
            }
        }
        else
        {
        	hmData.put("ClientName", clientName);
        	hmData.put("ClientAddress1", clientAddress1);
        	hmData.put("ClientAddress2", clientAddress2);
        	hmData.put("ClientAddress3", clientAddress3);

            if (cityName.trim().length() > 0)
            {
            	hmData.put("ClientCity", cityName);
            }
        }

        hmData.put("TEL NO", String.valueOf(clientTelNo));
        hmData.put("EMAIL ID", clientEmail);
        hmData.put("Line", Linefor5);

       
        String SQL_BillHD = "",billDt="",qty="";
        String waiterName = "";
        Date dt = null; 
        double quantity=0;
        String waiterNo = "";
        String tblName = "";
        String advDeposite="";
        String sqlTblName = "";
        String tabNo = "";
        String posName="";
        int paxNo = 0;
        boolean flag_DirectBillerBlill = false;
        boolean flgComplimentaryBill = false;
        List listBillHD=objBaseService.funGetList(sql, "sql");
        sql.setLength(0);
   		sql.append("select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
                + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ");
        List listSettlementType=objBaseService.funGetList(sql, "sql");
        if (listSettlementType!=null)
        {
        	if(listSettlementType.size()!=0)
        	{	
            flgComplimentaryBill = true;
        	}
        }	
      

        if (funIsDirectBillerBill(billNo, billhd))
        {
            flag_DirectBillerBlill = true;
            sql.setLength(0);
       		sql.append("select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
                    + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                    + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
                    + "where a.strBillNo='"+billNo+"' ");
            flag_DirectBiller = true;
            listBillHD=objBaseService.funGetList(sql, "sql");
           
            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		 dt = (Date) Array.get(obj,0);
//           		tabNo=(String) Array.get(obj,0);
    			}
            }	
        }     
        else
        {
        	sql.setLength(0);
       		sql.append("select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                    + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
                    + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                    + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                    + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ");
             listBillHD=objBaseService.funGetList(sql, "sql");
           
            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
    		
    		
           		tabNo = (String) Array.get(obj,0);
           		waiterNo=(String)Array.get(obj,1);
           		 dt=(Date)Array.get(obj,2);
           		
           		subTotal = (BigDecimal) Array.get(obj,5);
           		grandTot = (BigDecimal) Array.get(obj,7);
                user = (String) Array.get(obj,11);
                deliveryCharge = (BigDecimal) Array.get(obj,12);
                advAmount = (BigDecimal) Array.get(obj,13);
//                advDeposite=(String) Array.get(obj,13);
//                posName=(String) Array.get(obj,15);
//                String paxNumber=(String) Array.get(obj,16);
//                paxNo=Integer.parseInt(paxNumber);
    			}
              
                if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
                {
                    waiterNo = "";
                }
                else
                {
                    
                	sql.setLength(0);
    	       		sql.append("select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'");
                    List listQuery=objBaseService.funGetList(sql, "sql");
                       if (listQuery!=null)
   	                   {
   	                	for(int i=0; i<listQuery.size(); i++)
   		    			{
   	                		waiterName = (String) listQuery.get(0);
//   	   		           	waiterName = (String) Array.get(obj,0);           		
   		    			}
                        
                        }
                     }
            }

            sql.setLength(0);
       		sql.append("select strTableName from tbltablemaster where strTableNo='"+tabNo+"'");
            List listTblName=objBaseService.funGetList(sql, "sql");
          
            if (listTblName!=null)
            {
            	for(int i=0; i<listTblName.size(); i++)
	    			{
            		tblName = (String) listTblName.get(0);
	           		 		
	    			}
                
            }
        }

        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
		    
		    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
       
        // funPrintTakeAway(billhd, billNo, BillOut);
//        List<clsPOSBillDtl> listSubTotal = new ArrayList<>();
		    List listSubTotal = new ArrayList();
        if (flag_DirectBillerBlill)
        {

        	if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
           		subTotal = (BigDecimal)Array.get(obj,3);
           		grandTot = (BigDecimal)Array.get(obj,5);
           		grandTotal=grandTot.doubleValue();
                user = (String)Array.get(obj,9);
                deliveryCharge =(BigDecimal)Array.get(obj,10);
                advAmount = (BigDecimal)Array.get(obj,11);
                posName = (String)Array.get(obj,13);
//           		tabNo=(String) Array.get(obj,0);
    			}
            }
        	hmData.put("POS", posName);
        	hmData.put("BillNo", billNo);

            if (printTimeOnBillYN.equals("Y"))
            {
            	 SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                 tabNo = ft.format(dt);
                 hmData.put("DATE_TIME",tabNo);
            }
            else
            {
            	 SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                 tabNo = ft.format(dt);
                 hmData.put("DATE_TIME",tabNo);
            }
             jObjList = new HashMap();
            jObjList.put("subTotal",subTotal);
            listSubTotal.add(jObjList);
          
            
        }
        else
        {
        	hmData.put("TABLE NAME", tblName);

            if (waiterName.trim().length() > 0)
            {
            	hmData.put("waiterName", waiterName);
            }
            hmData.put("POS", posName);
            hmData.put("BillNo", billNo);
            hmData.put("PaxNo", paxNo);

            if (printTimeOnBillYN.equals("Y"))
            {
            	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
            	billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            }
            else
            {
            	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
            	billDt = ft.format(dt);
                hmData.put("DATE_TIME", billDt);
            }

            if (listBillHD!=null)
            {
            	for(int i=0; i<listBillHD.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillHD.get(i);
           		
            subTotal = (BigDecimal)Array.get(obj,5);
            grandTot = (BigDecimal)Array.get(obj,7);
            grandTotal=grandTot.doubleValue();
            user = (String)Array.get(obj,11);
            deliveryCharge = (BigDecimal)Array.get(obj,12);
            advAmount = (BigDecimal)Array.get(obj,13);
    			}
            } 
            jObjList = new HashMap();
            jObjList.put("subTotal",subTotal);
            listSubTotal.add(jObjList);
            
        }

//        List<clsPOSBillDtl> listOfFoodBillDetail = new ArrayList<>();
        List listOfFoodBillDetail = new ArrayList();
//        List<clsPOSBillDtl> listOfLiqourBillDetail = new ArrayList<>();
        List listOfLiqourBillDetail = new ArrayList();
//        List<clsPOSBillDtl> listGeneralBillDtl = new ArrayList<>();
        List listGeneralBillDtl = new ArrayList();
//        String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName as ItemLine1"
//                + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
//                + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,b.strItemType  "
//                + " from " + billdtl + " a,tblitemmaster b  "
//                + " where a.strBillNo=? "
//                + " and a.strItemCode=b.strItemCode "
//                + " and a.tdhYN='N' "
//                + " group by a.strItemCode ;";
        sql.setLength(0);
   		sql.append("select sum(a.dblQuantity),a.strItemName "
        		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,b.strItemType"
                 + " from " + billdtl + " a,tblitemmaster b  "
                 + " where a.strBillNo='"+billNo+"'"
                 + " and a.strItemCode=b.strItemCode "
    			 + " and a.tdhYN='N' "
                 + " group by a.strItemCode");
        List listBillDtl = objBaseService.funGetList(sql, "sql");
      
        BigDecimal saleQuantity = null;
		BigDecimal amount=null;
		double saleQty=0.00;
        String itemCode="",itemName="",itemType="";

        if (listBillDtl!=null)
        {
        	
//        	Map jObjList=new HashMap();
        	for(int i=0; i<listBillDtl.size(); i++)
    			{
           		Object[] obj = (Object[]) listBillDtl.get(i);
           		saleQuantity = (BigDecimal) Array.get(obj,0);
           		saleQty = saleQuantity.doubleValue();
           		itemName = (String) Array.get(obj,1);
           		amount = (BigDecimal) Array.get(obj,2); 
           		itemCode = (String) Array.get(obj,3); 
           		itemType= (String) Array.get(obj,5); 
                if (itemType.equalsIgnoreCase("Food"))
                {
                    listGeneralBillDtl = listOfFoodBillDetail;
                }
                else
                {
                    listGeneralBillDtl = listOfLiqourBillDetail;
                }
    		
        	
        	
      

            sql.setLength(0);
	       	sql.append("select dblQuantity from " + billPromoDtl + " "
                    + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "'");
            List listPromoItems=objBaseService.funGetList(sql, "sql");
            
            if (listPromoItems!=null)
            {
            	for(int cnt=0; cnt<listPromoItems.size(); cnt++)
	    			{
	           			Object[] objPromoItems = (Object[]) listPromoItems.get(cnt);
	           			quantity = (double) Array.get(objPromoItems,0);
	           			saleQty -= quantity;
	    			}
            }
            qty = String.valueOf(saleQty);
            if (qty.contains("."))
            {
                String decVal = qty.substring(qty.length() - 2, qty.length());
                if (Double.parseDouble(decVal) == 0)
                {
                    qty = qty.substring(0, qty.length() - 2);
                }
            }
           double dblAmount=0.00,rate=0.00,discountAmt=0.00;
           BigDecimal bd=null,bd1=null,bd2=null;
            if (saleQty > 0)
            {
            	
//            	if (listBillDtl!=null)
//                {
//                	
//                	for(int j=0; j<listBillDtl.size(); j++)
//		    			{
//		           		Object[] objBillDtl = (Object[]) listBillDtl.get(i);
		           		itemName = (String) Array.get(obj,1);
		           		bd=(BigDecimal) Array.get(obj, 2);
		           				    		           		
		           		dblAmount = bd.doubleValue(); // The double you want
		           		
		           		jObjList = new HashMap();
		           		jObjList.put("saleQty",saleQty);
		           		jObjList.put("dblAmount",dblAmount);
		           		jObjList.put("itemName",itemName);
		           		
		           		listGeneralBillDtl.add(jObjList);

//		    			}
//                }	
//               

            	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
     			    
     		    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
              
     		   sql.setLength(0);
	       	   sql.append("select count(*) "
                        + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ");
                if (printZeroAmtModifierOnBill.equals("N"))
                {
                	sql.append(" and  dblAmount !=0.00 ");
                }
               List listCount=objBaseService.funGetList(sql, "sql");
                   String countntRec="";
                   int cntRecord = 0;
                   if(listCount!=null)
                   {
                	   for(int a=0; a<listPromoItems.size(); a++)
		    			{
                	   Object[] objPromoItems = (Object[]) listPromoItems.get(a);
                	   countntRec = (String) Array.get(objPromoItems,0);   
                	    cntRecord = Integer.parseInt(countntRec);
		    			}
                   }
                
                if (cntRecord > 0)
                {
                	sql.setLength(0);
    	       		sql.append("select strModifierName,dblQuantity,dblAmount "
                            + " from " + billModifierdtl + " "
                            + " where strBillNo=? and left(strItemCode,7)=? ");
                    if (printZeroAmtModifierOnBill.equals("N"))
                    {
                    	sql.append(" and  dblAmount !=0.00 ");
                    }
                    List listmodifierRecord=objBaseService.funGetList(sql, "sql");
                       String modifierName="";
                       double amt=0;
                       
                       if(listmodifierRecord!=null)
                        {
                    	   for(int b=0; b<listmodifierRecord.size(); b++)
			    			{
	                	   Object[] objmodifierRecord = (Object[]) listmodifierRecord.get(b);
	                	   modifierName= (String) Array.get(objmodifierRecord,0);  
	                	   quantity = (double) Array.get(objmodifierRecord,1); 
	                	   amt=(double) Array.get(objmodifierRecord,2); 
	                	   }
                          
                        if (flgComplimentaryBill)
                        {
                        	jObjList = new HashMap();
                            jObjList.put("quantity",quantity);
                            jObjList.put("amt",0);
                            jObjList.put("modifierName",modifierName.toUpperCase());
                            
                            listGeneralBillDtl.add(jObjList);
                        }
                        else
                        {
                        	jObjList = new HashMap();
                        	 jObjList.put("quantity",quantity);
                        	 jObjList.put("amt",amt);
                        	 jObjList.put("modifierName",modifierName.toUpperCase());
                        	
                        	 listGeneralBillDtl.add(jObjList);
                        }
                    }
                   
                }
            }
    }
    }
        funPrintPromoItemsInBill(billNo, 4, listGeneralBillDtl);  // Print Promotion Items in Bill for this billno.

//        List<clsPOSBillDtl> listOfDiscountDtl = new ArrayList<>();
        List listOfDiscountDtl = new ArrayList();
        sql.setLength(0);
   		sql.append("select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
                + "from " + billDscFrom + " a ,tblreasonmaster b "
                + "where  a.strDiscReasonCode=b.strReasonCode "
                + "and a.strBillNo='" + billNo + "' ");
        List listDisc=objBaseService.funGetList(sql, "sql");

        boolean flag = true;
        double dbl=0,discAmt=0;
        String discAmount="";
        String discOnValue="",reasonName="",discRemarks="";
       if(listDisc!=null)
        {
    	   jObjList = new HashMap();
    	   for(int i=0; i<listDisc.size(); i++)
			{
    		   Object[] obj = (Object[]) listDisc.get(i);
    		   dbl= (double) Array.get(obj,0);
    		   discAmt= (double) Array.get(obj,1);
    		   discAmount= (String)Array.get(obj,2);
    		   discOnValue= (String) Array.get(obj,3);
    		   reasonName= (String) Array.get(obj,4);
    		   discRemarks= (String) Array.get(obj,5);
			}  
      
            if (flag)
            {
            	  
            	jObjList = new HashMap();
            	jObjList.put("Discount","Discount");
                //listOfDiscountDtl.put(jObjList);
                flag = false;
            }
            
            String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
            if (discText.length() > 30)
            {
                discText = discText.substring(0, 30);
            }
            else
            {
                discText = String.format("%-30s", discText);
            }

            String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
            hmData.put("Discount", discText + " " + discountOnItem);
            jObjList.put("discText",discText);
            //jObjList = new HashMap();
            jObjList.put("discAmt",discAmt);
           // listOfDiscountDtl.put(jObjList);

            //jObjList = new HashMap();
            jObjList.put("Reason",reasonName);
            //listOfDiscountDtl.put(jObjList);

            //jObjList = new HashMap();
            jObjList.put("Remark",discRemarks);
            listOfDiscountDtl.add(jObjList);
        }

//        List<clsPOSBillDtl> listOfTaxDetail = new ArrayList<>();
        List listOfTaxDetail = new ArrayList(); 
        sql.setLength(0);
   		sql.append("select b.strTaxDesc,sum(a.dblTaxAmount) "
                + " from " + billtaxdtl + " a,tbltaxhd b "
                + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                + " group by a.strTaxCode");
         List listTax=objBaseService.funGetList(sql, "sql");
        String taxDesc="";
        BigDecimal taxAmt=null;
        double taxAmount=0;
        
        if(listTax!=null)
        {
        	for(int i=0; i<listTax.size(); i++)
			{
    		   Object[] obj = (Object[]) listTax.get(i);
    		   taxDesc= (String) Array.get(obj,0);
    		   taxAmt= (BigDecimal) Array.get(obj,1);
    		   taxAmount = taxAmt.doubleValue();
			
        	
            if (flgComplimentaryBill)
            {
            	jObjList = new HashMap();
            	jObjList.put("taxAmount",0);
                jObjList.put("taxDesc",taxDesc);
                listOfTaxDetail.add(jObjList);
            }
            else
            {
            	jObjList = new HashMap();
            	jObjList.put("taxAmount",taxAmount);
                jObjList.put("taxDesc",taxDesc);
                listOfTaxDetail.add(jObjList);
            }
			}
        }
//        List<clsPOSBillDtl> listOfGrandTotalDtl = new ArrayList<>();
        List listOfGrandTotalDtl = new ArrayList();
        if (grandTotal > 0)
        {
        	jObjList = new HashMap();
        	jObjList.put("grandTotal",grandTotal);
            listOfGrandTotalDtl.add(jObjList);
        }

        List listOfSettlementDetail = new ArrayList();
        //settlement breakup part
        sql.setLength(0);
   		sql.append("select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
                + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
                + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode");
        List listBill_Settlement=objBaseService.funGetList(sql, "sql");
       double settleAmt=0;
       String settleDesc="";
       
       if(listBill_Settlement!=null)
        {
    	  jObjList=new HashMap();
    	   for(int i=0; i<listBill_Settlement.size(); i++)
			{
    		   Object[] obj = (Object[]) listBill_Settlement.get(i);
    		   settleAmt= (double) Array.get(obj,0);
    		   settleDesc= (String) Array.get(obj,1);
    		  
			 
    	   
            if (flgComplimentaryBill)
            {
            	
                jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",0.00);
               // listOfSettlementDetail.put(jObjList);
            }
            else
            {
//                objBillDtl = new clsPOSBillDtl();
            	//jObjList = new HashMap();
               jObjList.put("settleDesc",settleDesc);
               jObjList.put("settleAmt",settleAmt);
               // listOfSettlementDetail.put(jObjList);
            }
            listOfSettlementDetail.add(jObjList);
			} 
    	   
        }
       sql.setLength(0);
  	   sql.append("select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
                + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
                + " group by strBillNo");
        List listTenderAmt=objBaseService.funGetList(sql, "sql");
        double paidAmt=0,refundAmt=0;
        
        if (listTenderAmt!=null)
        {  
        	//jObjList = new HashMap();
        	for(int i=0; i<listTenderAmt.size(); i++)
			{
    		   Object[] obj = (Object[]) listTenderAmt.get(i);
    		   paidAmt= (double) Array.get(obj,0);
    		   refundAmt= (double) Array.get(obj,2);
    		  
			
        	if (flgComplimentaryBill)
            {
        		//jObjList = new HashMap();
        		jObjList.put("PAID AMT","PAID AMT");
               jObjList.put("paidAmt",0.00);
                //listOfSettlementDetail.put(jObjList);
            }
            else
            {
            	//jObjList = new HashMap();
                jObjList.put("PAID AMT","PAID AMT");
                jObjList.put("paidAmt",paidAmt);
                //listOfSettlementDetail.put(jObjList);
                if (refundAmt > 0)
                {
                	//jObjList = new HashMap();
                    jObjList.put("REFUND AMT","REFUND AMT");
                    jObjList.put("refundAmt",refundAmt);
                   // listOfSettlementDetail.put(jObjList);
                }
            }
        	listOfSettlementDetail.add(jObjList);
			}
        	
        }
        if (flag_isHomeDelvBill)
        {
        	sql.setLength(0);
       		sql.append("select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'");
            List listCount=objBaseService.funGetList(sql, "sql");
            long count = 0;
            if (listCount!=null)
            {
            	for(int i=0; i<listCount.size(); i++)
    			{
        		   //Object[] obj = (Object[]) listCount.get(i);
//        		   String cnt= (String) Array.get(obj,0);
//        		   
            		count=((BigInteger)listCount.get(0)).longValue();
        		  
    			}  
            }
            hmData.put("CUSTOMER_COUNT", count);
        }

        List listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
//        List<clsPOSBillDtl> listOfFooterDtl = new ArrayList<>();
        List listOfFooterDtl = new ArrayList();
        jObjList = new HashMap();
        jObjList.put("Thank","THANK YOU AND VISIT AGAIN !!!");
        listOfFooterDtl.add(jObjList);

        hmData.put("BillType", billType);
        hmData.put("listOfFoodBillDetail", listOfFoodBillDetail);
        hmData.put("listOfLiqourBillDetail", listOfLiqourBillDetail);
        hmData.put("listOfTaxDtl", listOfTaxDetail);
        hmData.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
        hmData.put("listOfServiceVatDetail", listOfServiceVatDetail);
        hmData.put("listOfFooterDtl", listOfFooterDtl);
        hmData.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
        hmData.put("listOfDiscountDtl", listOfDiscountDtl);
        hmData.put("listOfSettlementDetail", listOfSettlementDetail);
        hmData.put("listSubTotal", listSubTotal);
        listData.add(hmData);

        json.put("listOfFoodBillDetail", listOfFoodBillDetail);
        json.put("format", "Jasper3");
        json.put("result", result);
        json.put("listData", listData);
        json.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
    }  
    catch (Exception e)
    {
        e.printStackTrace();
    }
	return json;
}  



	public List funPrintServiceVatNoForJasper(String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo) throws IOException
	{
		List listOfServiceVatDetail = new ArrayList();
	    clsPOSBillDtl objBillDtl = null;
	    try
	    {
	    Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gUseVatAndServiceTaxFromPos");   
		String useVatAndServiceTaxFromPos=(String)objSetupParameter.get("gUseVatAndServiceTaxFromPos");
	    if (useVatAndServiceTaxFromPos.equals("Y"))
	    {
	    	Map hmData=new HashMap();
	
	        if (PrintVatNoPOS.equals("Y"))
	        {
	            objBillDtl = new clsPOSBillDtl();
	            hmData.put("Vat No. :" , vatNo);
	            listOfServiceVatDetail.add(hmData);
	        }
	        if (printServiceTaxNo.equals("Y"))
	        {
	        	hmData.put("Service Tax No.:" ,serviceTaxNo);
	            listOfServiceVatDetail.add(hmData);
	        }
	    }
	    else
	    {
	    	Map hmData=new HashMap();
	        if (PrintVatNoPOS != null)
	        {
	        	hmData.put("Vat No. :",vatNo);
	            listOfServiceVatDetail.add(hmData);
	        }
	        if (serviceTaxNo.equalsIgnoreCase(""))
	        {
	        	hmData.put("Service Tax No.:",serviceTaxNo);
	            listOfServiceVatDetail.add(hmData);
	        }
	    }
	  }
	   catch(Exception e)
	    {
		   e.printStackTrace();
	    }
	    
	    return listOfServiceVatDetail;
	}

	
	
	public long funCompareDate(String fromDate, String toDate)
    {
        long diff = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try
        {
            d1 = format.parse(fromDate);
            d2 = format.parse(toDate);
            diff = d2.getTime() - d1.getTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return diff;
    }

	
	 private int funPrintTakeAwayForJasper(String billHdTableName, String billNo) throws Exception
	    {
		 StringBuilder sql = new StringBuilder(); 
	     int res = 0;
	     sql.append("select strOperationType from " + billHdTableName + " "
	                + " where strBillNo='" + billNo + "'");
	       List rsBill = objBaseService.funGetList(sql, "sql");
	       String operationType="";
	        if(rsBill!=null)
	        {
	        	for(int i=0; i<rsBill.size(); i++)
				{
	        		operationType = (String) rsBill.get(0);
	        		if(operationType.equals("TakeAway"))
	        		{
	        			res = 1;
	        		}
	        	}
	        	
	        }
	        return res;
	    }
	 
	 
	 
	 private boolean funIsDirectBillerBill(String billNo, String billhd)
	    {
		    StringBuilder sql = new StringBuilder(); 
	        boolean flgIsDirectBillerBill = false;
	        try
	        {
	          sql.append("select strTableNo,strOperationType "
	                    + " from " + billhd + " where strBillNo='"+billNo+"'  ");
	        
	          List listIsDirectBillBill=objBaseService.funGetList(sql, "sql");
	          String tableNo="",operationType="";
	          if(listIsDirectBillBill!=null)
	          {
	        	  for(int i=0; i<listIsDirectBillBill.size(); i++)
					{
		        		Object[] obj = (Object[]) listIsDirectBillBill.get(i);
		        		tableNo = (String) Array.get(obj,0);
		        		
		        	}	
	                if (tableNo != null && tableNo.trim().isEmpty())
	                {
	                    flgIsDirectBillerBill = true;
	                }
	            }
	           
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return flgIsDirectBillerBill;
	    }
	 
	 
	 
	 
	  
	  private int funPrintPromoItemsInBill(String billNo, int billPrintSize, List listOfBillDetail) throws Exception
	    {
		  
		  Map jObjBillDtl=new HashMap();
		  StringBuilder sql = new StringBuilder(); 
		  sql.append("select b.strItemName,a.dblQuantity,'0',dblRate "
	                + " from tblbillpromotiondtl a,tblitemmaster b "
	                + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ");
           List listBillPromoItemDtl =objBaseService.funGetList(sql, "sql");
	       String itemName="";
	       double qty=0,amt=0;
	        clsPOSBillDtl objBillDtl = null;
	        if(listBillPromoItemDtl!=null)
	          {
	        	for(int i=0; i<listBillPromoItemDtl.size(); i++)
				{
	        		Object[] obj = (Object[]) listBillPromoItemDtl.get(i);
			
	        		itemName = (String) Array.get(obj,0);
	        		qty = (double) Array.get(obj,1);
	        		amt = (double) Array.get(obj,2);
	        		jObjBillDtl.put("qty", qty);
		        	jObjBillDtl.put("amt", amt);
		        	jObjBillDtl.put("itemName", itemName.toUpperCase());
		            listOfBillDetail.add(jObjBillDtl);
	        	}
	        	
	        }
	        return 1;
	    }

	
	  private int funPrintPromoItemsInBillFormat2(String billNo, int billPrintSize,  List listOfBillDetail) throws Exception
	    {
		  StringBuilder sql = new StringBuilder(); 
		  sql.append("select b.strItemName,a.dblQuantity,'0',dblRate "
	                + " from tblbillpromotiondtl a,tblitemmaster b "
	                + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ");
	        List listBillPromoItemDtl =objBaseService.funGetList(sql, "sql");
	        clsPOSBillDtl objBillDtl = null;
		       String itemName="";
		       double qty=0,amt=0;
		       
		        if(listBillPromoItemDtl!=null)
		          {
		        	for(int i=0; i<listBillPromoItemDtl.size(); i++)
					{
		        		Object[] obj = (Object[]) listBillPromoItemDtl.get(i);
				
		        		itemName = (String) Array.get(obj,0);
		        		qty = (double) Array.get(obj,1);
		        		amt = (double) Array.get(obj,2);
		        		objBillDtl = new clsPOSBillDtl();
			            objBillDtl.setDblQuantity(qty);
			            objBillDtl.setDblAmount(amt);
			            objBillDtl.setStrItemName(itemName);
			            listOfBillDetail.add(objBillDtl);
		        	}
		        	
		        }
		   return 1;     
		        
	    }
	  
	  
	 
}
