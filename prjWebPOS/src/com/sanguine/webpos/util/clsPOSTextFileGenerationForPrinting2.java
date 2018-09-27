/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;







import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.controller.clsPOSDayEndProcess;


/**
 *
 * @author Vinayak
 */
@Repository
@Transactional(value="webPOSTransactionManager")
public class clsPOSTextFileGenerationForPrinting2
{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
    clsPOSUtilityController obUtilityController;
	
	@Autowired
	clsPOSDayEndUtility objPOSDayEndUtility;
	
	@Autowired
	intfBaseService objBaseService;
	
	@Autowired 
	clsPOSSetupUtility objPOSSetupUtility;
	
	public static String gColumnSize="0";
	String gPrintType="",strOS="",strPrinterType="",strPrinterPort="",PrintKOTYN="",gPrinterQueueStatus="",
			gShowBill="",gShowPrinterErrorMsg="",gMultiBillPrint="",strAdvReceiptPrinter="",
			gOpenCashDrawerAfterBillPrintYN="",gMultipleKOTPrint="",gEnableShiftYN="";
    public void funGenerateTextDayEndReport(String posCode, String billDate, String reprint, int shiftNo,String clientCode,String userCode)
    {
    	funLoadSetupData(clientCode,posCode);
        try
        {
            String dashLinesFor42Chars = "  ----------------------------------------";
            String billHd = "tblqbillhd";
            String billDtl = "tblqbilldtl";
            String billSettlementDtl = "tblqbillsettlementdtl";
            String billTaxDtl = "tblqbilltaxdtl";
            funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_DayEndReport = new File(filePath + "/Temp/Temp_DayEndReport.txt");
            FileWriter fstream_Report = new FileWriter(Text_DayEndReport);
            BufferedWriter bufferedWriter = new BufferedWriter(fstream_Report);
            boolean isReprint = false;
            if ("reprint".equalsIgnoreCase(reprint))
            {
                isReprint = true;
                funPrintBlankSpace("[DUPLICATE]", bufferedWriter);
                bufferedWriter.write("[DUPLICATE]");
                bufferedWriter.newLine();
                billHd = "tblqbillhd";
                billDtl = "tblqbilldtl";
                billSettlementDtl = "tblqbillsettlementdtl";
                billTaxDtl = "tblqbilltaxdtl";
            }
            
            gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gEnableShiftYN");
            if (gEnableShiftYN.equals("Y"))
            {
                funPrintBlankSpace("SHIFT END REPORT", bufferedWriter);
                bufferedWriter.write("SHIFT END REPORT");
            }
            else
            {
                funPrintBlankSpace("DAY END REPORT", bufferedWriter);
                bufferedWriter.write("DAY END REPORT");
            }
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            String sqlDayEnd = "";
           
            Query qDayEnd;
            List listdayEnd;
            if (posCode.equalsIgnoreCase("All"))
            {
                sqlDayEnd = "select  'All' as POSCode,'All' as POSName,date(a.dtePOSDate),time(a.dteDayEndDateTime),sum(a.dblTotalSale), "
                        + " sum(a.dblFloat),sum(a.dblCash),sum(a.dblAdvance),  sum(a.dblTransferIn),sum(a.dblTotalReceipt),sum(a.dblPayments), "
                        + " sum(a.dblWithDrawal),sum(a.dblTransferOut),sum(a.dblTotalPay),  sum(a.dblCashInHand),sum(a.dblHDAmt), "
                        + " sum(a.dblDiningAmt),sum(a.dblTakeAway),sum(a.dblNoOfBill),sum(a.dblNoOfVoidedBill), "
                        + " sum(a.dblNoOfModifyBill),sum(a.dblRefund)  ,sum(a.dblTotalDiscount), "
                        + " sum(a.intTotalPax),sum(a.intNoOfTakeAway),sum(a.intNoOfHomeDelivery),  "
                        + " sum(a.strUserCreated),sum(a.strUserEdited), sum(a.intNoOfNCKOT),sum(a.intNoOfComplimentaryKOT), "
                        + " sum(a.intNoOfVoidKOT),sum(dblUsedDebitCardBalance),sum(dblUnusedDebitCardBalance),sum(a.dblTipAmt) "
                        + " from tbldayendprocess a  "
                        + " where date(a.dtePOSDate) = :param1 and intShiftCode = :param2";
                
             //   PreparedStatement pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlDayEnd);
                qDayEnd=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDayEnd);
                qDayEnd.setParameter("param1", billDate);
                qDayEnd.setParameter("param2", String.valueOf(shiftNo));
                listdayEnd=qDayEnd.list();
            }
            else
            {
                sqlDayEnd = "select  a.strPOSCode,b.strPosName,date(a.dtePOSDate),time(a.dteDayEndDateTime),a.dblTotalSale,\n"
                        + " a.dblFloat,a.dblCash,a.dblAdvance,  a.dblTransferIn,a.dblTotalReceipt,a.dblPayments,\n"
                        + " a.dblWithDrawal,a.dblTransferOut,a.dblTotalPay,  a.dblCashInHand,a.dblHDAmt,\n"
                        + " a.dblDiningAmt,a.dblTakeAway,a.dblNoOfBill,a.dblNoOfVoidedBill,\n"
                        + " a.dblNoOfModifyBill,a.dblRefund  ,a.dblTotalDiscount,\n"
                        + " a.intTotalPax,a.intNoOfTakeAway,a.intNoOfHomeDelivery,\n"
                        + " a.strUserCreated,a.strUserEdited, a.intNoOfNCKOT,a.intNoOfComplimentaryKOT, "
                        + " a.intNoOfVoidKOT,a.dblUsedDebitCardBalance,a.dblUnusedDebitCardBalance,a.dblTipAmt "
                        + " from tbldayendprocess a ,tblposmaster b "
                        + " where b.strPosCode=a.strPosCode "
                        + " and a.strPOSCode = :param1 and date(a.dtePOSDate) = :param2 and intShiftCode = :param3 ;";
              //  PreparedStatement pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlDayEnd);
                qDayEnd=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDayEnd);
                qDayEnd.setParameter("param1", posCode);
                qDayEnd.setParameter("param2", billDate);
                qDayEnd.setParameter("param3", String.valueOf(shiftNo));
                listdayEnd=qDayEnd.list();
                //rsDayend = pst.executeQuery();
            }
            if (listdayEnd.size()>0)
	            {
            	Object obDayEnd[]=(Object[])listdayEnd.get(0);
	                //Header Part
	                bufferedWriter.write("  POS Code    :");
	                bufferedWriter.write(obDayEnd[0].toString());
	                bufferedWriter.newLine();
	
	                bufferedWriter.write("  POS Name    :");
	                bufferedWriter.write(obDayEnd[1].toString());
	                bufferedWriter.newLine();
	
	                if (gEnableShiftYN.equals("Y"))
	                {
	                    bufferedWriter.write("  SHIFT No.    :");
	                    bufferedWriter.write(String.valueOf(shiftNo));
	                    bufferedWriter.newLine();
	                }
	
	                bufferedWriter.write("  POS Date    :");
	                bufferedWriter.write(obDayEnd[2].toString());
	                bufferedWriter.write(" " + obDayEnd[3].toString());
	                bufferedWriter.newLine();
	
	                if (gEnableShiftYN.equals("Y"))
	                {
	                    bufferedWriter.write("  SHIFT End By  :");
	                }
	                else
	                {
	                    bufferedWriter.write("  Day End By  :");
	                }
	
	                bufferedWriter.write(obDayEnd[27].toString());
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                // End Of Header Part
	
	                //Start of Detail Part
	                funWriteTotal(" 1. HOME DELIVERY", obDayEnd[15].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 2. DINING", obDayEnd[16].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 3. TAKE AWAY", obDayEnd[17].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funWriteTotal(" 4. TOTAL SALES", obDayEnd[4].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 5. DISCOUNT", obDayEnd[22].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 6. FLOAT", obDayEnd[5].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 7. CASH",obDayEnd[6].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 8. ADVANCE", obDayEnd[7].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal(" 9. TRANSFER IN",obDayEnd[8].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funWriteTotal("10. TOTAL RECEIPT", obDayEnd[9].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	
	                funWriteTotal("11. PAYMENT", obDayEnd[10].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("12. WITHDRAWAL",obDayEnd[11].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("13. TRANSFER OUT", obDayEnd[12].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("14. REFUND", obDayEnd[21].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funWriteTotal("15. TOTAL PAYMENTS", obDayEnd[13].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funWriteTotal("16. CASH IN HAND", obDayEnd[14].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	
	                funWriteTotal("17. No. OF BILLS",obDayEnd[18].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("18. No. OF VOIDED BILLS", obDayEnd[19].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("19. No. OF MODIFIED BILLS", obDayEnd[20].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("20. NO. OF PAX", obDayEnd[23].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("21. No. OF HOME DEL", obDayEnd[25].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("22. No. OF TAKE AWAY", obDayEnd[24].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("23. No. OF NC KOT", obDayEnd[28].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("24. No. OF COMPLIMENTARY BILLS",obDayEnd[29].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("25. No. OF VOID KOT", obDayEnd[30].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("26. Used Card Balance", obDayEnd[31].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	
	                funWriteTotal("27. Unused Card Balance", obDayEnd[32].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	                //End of Detail Part
	
	                NumberFormat formatter = new DecimalFormat("0.00");
	                //<< tip amount
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funWriteTotal("28. Total Tip Amount", obDayEnd[33].toString(), bufferedWriter, "");
	                bufferedWriter.newLine();
	            //>> tip amount
	
	                //Start of Settlement Brkup
	                double totalAmt = 0.00;
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                funPrintBlankSpace("BILLING SETTLEMENT BREAK UP", bufferedWriter);
	                bufferedWriter.write("BILLING SETTLEMENT BREAK UP");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                
	            
	
	                String sql_SettelementBrkUP = "";
	                List listSettelementBrkUP;
	                Query qSettelementBrkUP;
	                if (posCode.equals("All"))
	                {
	                    if (clsPOSDayEndProcess.gDayEndReportForm.equals("DayEndReport"))
	                    {
	                        sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
	                                + " from  " + billHd + " a, " + billSettlementDtl + " b, tblsettelmenthd c  "
	                                + " where a.strBillNo = b.strBillNo"
	                                + " and b.strSettlementCode = c.strSettelmentCode "
	                                + " and date(a.dteBillDate) = :param1 and a.intShiftCode= :param2 "
	                                + " GROUP BY c.strSettelmentDesc;";
	                    }
	                    else
	                    {
	                        sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
	                                + " from  " + billHd + " a, " + billSettlementDtl + " b, tblsettelmenthd c  "
	                                + " where a.strBillNo = b.strBillNo"
	                                + " and b.strSettlementCode = c.strSettelmentCode "
	                                + " and date(a.dteBillDate) = :param1 and a.intShiftCode = :param2 "
	                                + " GROUP BY c.strSettelmentDesc;";
	                    }
	                    //PreparedStatement pst_SettelementBrkUP = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_SettelementBrkUP);
	                    qSettelementBrkUP=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementBrkUP);
	                    qSettelementBrkUP.setParameter("param1", billDate);
	                    qSettelementBrkUP.setParameter("param2", String.valueOf(shiftNo));
	                    listSettelementBrkUP = qSettelementBrkUP.list();
	                }
	                else
	                {
	                    if (clsPOSDayEndProcess.gDayEndReportForm.equals("DayEndReport"))
	                    {
	                        sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
	                                + " from  " + billHd + " a, " + billSettlementDtl + " b, tblsettelmenthd c  "
	                                + " where a.strBillNo = b.strBillNo"
	                                + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode = :param1 "
	                                + " and date(a.dteBillDate) = :param2 and a.intShiftCode = :param3 "
	                                + " GROUP BY c.strSettelmentDesc;";
	                    }
	                    else
	                    {
	                        sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
	                                + " from  " + billHd + " a, " + billSettlementDtl + " b, tblsettelmenthd c  "
	                                + " where a.strBillNo = b.strBillNo"
	                                + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode = :param1 "
	                                + " and date(a.dteBillDate) = :param2 and a.intShiftCode = :param3 "
	                                + " GROUP BY c.strSettelmentDesc;";
	                    }
	                    //System.out.println(sql_SettelementBrkUP);
	                    qSettelementBrkUP=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementBrkUP);
	                    qSettelementBrkUP.setParameter("param1", posCode);
	                    qSettelementBrkUP.setParameter("param2", billDate);
	                    qSettelementBrkUP.setParameter("param3", String.valueOf(shiftNo));
//	                    if (!clsPOSDayEndProcess.gDayEndReportForm.equals("DayEndReport"))
//	                    {
//	                    	qSettelementBrkUP.setParameter("param3", String.valueOf(shiftNo));
//	                    }
	                    listSettelementBrkUP=qSettelementBrkUP.list();
	                    
	                }
	                if(listSettelementBrkUP.size()>0)	
	                {
	                	for(int i=0;i<listSettelementBrkUP.size();i++)
		                {
	                		Object obsett[]=(Object[])listSettelementBrkUP.get(i);
		                    totalAmt += Double.parseDouble(obsett[1].toString());
		                    funWriteTotal(obsett[0].toString(), obsett[1].toString(), bufferedWriter, "");
		                    bufferedWriter.newLine();
		                }
	            	}	
	                List listSettelementTax;
	                Query qSettelementTax;
	                if (posCode.equals("All"))
	                {
	                    String sql_SettelementTax = "select b.strTaxDesc, sum(a.dblTaxableAmount), sum(a.dblTaxAmount) "
	                            + "from " + billTaxDtl + " a, tbltaxhd b "
	                            + "Where a.strTaxCode = b.strTaxCode and strBillNo IN "
	                            + "(select strBillNo from " + billHd + " where  date(dteBillDate)= :param1 and intShiftCode= :param2 ) "
	                            + "Group By b.strTaxDesc";
	
	                    //System.out.println(sql_SettelementTax);
	                    qSettelementTax=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementTax);
	                    qSettelementTax.setParameter("param1", billDate);
	                    qSettelementTax.setParameter("param2", String.valueOf(shiftNo));
	                    listSettelementTax=qSettelementTax.list();
	
	                }
	                else
	                {
	                    String sql_SettelementTax = "select b.strTaxDesc, sum(a.dblTaxableAmount), sum(a.dblTaxAmount) "
	                            + "from " + billTaxDtl + " a, tbltaxhd b "
	                            + "Where a.strTaxCode = b.strTaxCode and strBillNo IN "
	                            + "(select strBillNo from " + billHd + " where strPOSCode = :param1  and date(dteBillDate) = :param2 and intShiftCode = :param3 ) "
	                            + "Group By b.strTaxDesc";
	
	                    //System.out.println(sql_SettelementTax);
	                    qSettelementTax=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementTax);
	                    qSettelementTax.setParameter("param1", posCode);
	                    qSettelementTax.setParameter("param2", billDate);
	                    qSettelementTax.setParameter("param3", String.valueOf(shiftNo));
	                    listSettelementTax=qSettelementTax.list();
	                }
	
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	
	                funWriteTotal("   TOTAL", formatter.format(totalAmt), bufferedWriter, "");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                bufferedWriter.write("   TAX Des             Taxable   Tax Amt   ");
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
			        if(listSettelementTax.size()>0)
	                {
			        	for(int i=0;i<listSettelementTax.size();i++)
			                {
			        			Object obsett[]=(Object[])listSettelementTax.get(i);
			                    funWriteTextWithBlankLines("  " +obsett[0].toString(), 21, bufferedWriter);
			                    funWriteTextWithBlankLines(obsett[1].toString(), 10, bufferedWriter);
			                    funWriteTextWithBlankLines(obsett[2].toString(), 9, bufferedWriter);
			                    bufferedWriter.newLine();
			                }
	            	}
	                bufferedWriter.newLine();
	                bufferedWriter.write(dashLinesFor42Chars);
	                bufferedWriter.newLine();
	                
  	                
  	                
	                //End of Settlement Brkup
	            }
            
           

            //group wise subtotal
            StringBuilder sqlBuilder = new StringBuilder();                        
            
            Map<String,clsPOSGroupSubGroupWiseSales>mapGroupWiseData=new HashMap<>();
            
            //live group data
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
                    +", '"+userCode+"',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
                    +", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
                    +"FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    +"WHERE a.strBillNo=b.strBillNo "
                    +"AND a.strPOSCode=f.strPOSCode "
                    +"AND a.strClientCode=b.strClientCode "
                    +"AND b.strItemCode=e.strItemCode "
                    +"AND c.strGroupCode=d.strGroupCode "
                    +"AND d.strSubGroupCode=e.strSubGroupCode "
                    +"AND a.strPOSCode = '"+posCode+"' "
                    +"AND DATE(a.dteBillDate)='"+billDate+"'"
                    +"AND a.intShiftCode='"+shiftNo+"' "
                    +"GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
            Query qGroupData=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
            List listGroupData=qGroupData.list();
            if(listGroupData.size()>0)
            {
            	for(int i=0;i<listGroupData.size();i++)
	            {
            		Object obGroup[]=(Object[])listGroupData.get(i);
	                String groupCode=obGroup[0].toString();//groupCode
	                String groupName=obGroup[1].toString();//groupCode
	                double netTotalPlusTax=Double.parseDouble(obGroup[10].toString());//subTotal-disc+tax
	                
	                if(mapGroupWiseData.containsKey(groupCode))
	                {
	                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=mapGroupWiseData.get(groupCode);
	                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax()+netTotalPlusTax);
	                }
	                else
	                {
	                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=new clsPOSGroupSubGroupWiseSales();
	                    objGroupWiseSales.setGroupCode(groupCode);
	                    objGroupWiseSales.setGroupName(groupName);
	                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
	                    
	                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
	                }
	            }
            }
           
             //live modifier group data
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
                    +",'"+userCode+"','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
                    +"FROM tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
                    +"WHERE a.strBillNo=b.strBillNo "
                    +"AND a.strPOSCode=f.strPosCode "
                    +"AND a.strClientCode=b.strClientCode  "
                    +"AND LEFT(b.strItemCode,7)=d.strItemCode  "
                    +"AND d.strSubGroupCode=e.strSubGroupCode "
                    +"AND e.strGroupCode=c.strGroupCode  "
                    +"AND b.dblamount>0 "
                    +"AND a.strPOSCode = '"+posCode+"' "
                    +"AND a.intShiftCode='"+shiftNo+"' "
                    +"AND DATE(a.dteBillDate) = '"+billDate+"' "
                    +"GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");

            qGroupData=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
            listGroupData=qGroupData.list();
            if(listGroupData.size()>0)
            {
            	for(int i=0;i<listGroupData.size();i++)
	            {
            			Object obGroup[]=(Object[])listGroupData.get(i);
            			String groupCode=obGroup[0].toString();//groupCode
       	                String groupName=obGroup[1].toString();//groupCode
		                double netTotalPlusTax=Double.parseDouble(obGroup[10].toString());//subTotal-disc+tax
		                
		                if(mapGroupWiseData.containsKey(groupCode))
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=mapGroupWiseData.get(groupCode);
		                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax()+netTotalPlusTax);
		                }
		                else
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=new clsPOSGroupSubGroupWiseSales();
		                    objGroupWiseSales.setGroupCode(groupCode);
		                    objGroupWiseSales.setGroupName(groupName);
		                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		                    
		                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		                }
		          }
            }
           
            
            //QFile group data
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
                    +", '"+userCode+"',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
                    +", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
                    +"FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
                    +"WHERE a.strBillNo=b.strBillNo "
                    +"AND a.strPOSCode=f.strPOSCode "
                    +"AND a.strClientCode=b.strClientCode "
                    +"AND b.strItemCode=e.strItemCode "
                    +"AND c.strGroupCode=d.strGroupCode "
                    +"AND d.strSubGroupCode=e.strSubGroupCode "
                    +"AND a.strPOSCode = '"+posCode+"' "
                    +"AND DATE(a.dteBillDate)='"+billDate+"'"
                    +"AND a.intShiftCode='"+shiftNo+"' "
                    +"GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
            
	            qGroupData=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
	            listGroupData=qGroupData.list();
	            if(listGroupData.size()>0)
	            {
	            	for(int i=0;i<listGroupData.size();i++)
				    {
	            			Object obGroup[]=(Object[])listGroupData.get(i);
	            			String groupCode=obGroup[0].toString();//groupCode
	       	                String groupName=obGroup[1].toString();//groupCode
			                double netTotalPlusTax=Double.parseDouble(obGroup[10].toString());//subTotal-disc+tax
			                
			                if(mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax()+netTotalPlusTax);
			                }
			                else
			                {
			                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=new clsPOSGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
			                    
			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
				    }
	            }
            
             //QFile modifier group data
            sqlBuilder.setLength(0);
            sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
                    +",'"+userCode+"','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
                    +"FROM tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
                    +"WHERE a.strBillNo=b.strBillNo "
                    +"AND a.strPOSCode=f.strPosCode "
                    +"AND a.strClientCode=b.strClientCode  "
                    +"AND LEFT(b.strItemCode,7)=d.strItemCode  "
                    +"AND d.strSubGroupCode=e.strSubGroupCode "
                    +"AND e.strGroupCode=c.strGroupCode  "
                    +"AND b.dblamount>0 "
                    +"AND a.strPOSCode = '"+posCode+"' "
                    +"AND a.intShiftCode='"+shiftNo+"' "
                    +"AND DATE(a.dteBillDate) = '"+billDate+"' "
                    +"GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
            
            qGroupData=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
            listGroupData=qGroupData.list();
           	if(listGroupData.size()>0)
           	{
            	for(int i=0;i<listGroupData.size();i++)
			    {
            			Object obGroup[]=(Object[])listGroupData.get(i);
            			String groupCode=obGroup[0].toString();//groupCode
       	                String groupName=obGroup[1].toString();//groupCode
		                double netTotalPlusTax=Double.parseDouble(obGroup[10].toString());//subTotal-disc+tax
		                
		                if(mapGroupWiseData.containsKey(groupCode))
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=mapGroupWiseData.get(groupCode);
		                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax()+netTotalPlusTax);
		                }
		                else
		                {
		                    clsPOSGroupSubGroupWiseSales objGroupWiseSales=new clsPOSGroupSubGroupWiseSales();
		                    objGroupWiseSales.setGroupCode(groupCode);
		                    objGroupWiseSales.setGroupName(groupName);
		                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		                    
		                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		                }
		            }
        		}
            
            
            if(mapGroupWiseData.size()>0)
            {
                bufferedWriter.newLine();
                bufferedWriter.write(dashLinesFor42Chars);
                bufferedWriter.newLine();
                bufferedWriter.write("   Group                 Amount With Tax   ");
                bufferedWriter.newLine();
                bufferedWriter.write(dashLinesFor42Chars);
                bufferedWriter.newLine();
                for(clsPOSGroupSubGroupWiseSales objGroupWiseSales:mapGroupWiseData.values())
                {                    
//                    funWriteTextWithBlankLines("  " + objGroupWiseSales.getGroupName(), 21, ReportOut);
//                    funWriteTextWithBlankLines(String.valueOf(Math.rint(objGroupWiseSales.getDblNetTotalPlusTax())), 10, ReportOut);     
                    funWriteTotal(objGroupWiseSales.getGroupName(),String.valueOf(Math.rint(objGroupWiseSales.getDblNetTotalPlusTax())), bufferedWriter, "");
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(dashLinesFor42Chars);
            }
            
            //        
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.newLine();
        	
	    	String sql="select strOS, strPrinterType,strDefaultPrinter,strAdvReceiptPrinter from tblconfig where strClientCode='"+clientCode+"'";
	    	Query query=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    	List list=query.list();
	    	if(list.size()>0){
	    		Object ob[]=(Object[])list.get(0);
	    		strOS=ob[0].toString();
	    		strPrinterType=ob[1].toString();
	    		strPrinterPort=ob[2].toString();
	    		strAdvReceiptPrinter=ob[3].toString();
	    	}
	    	
            
            if ("linux".equalsIgnoreCase(strOS))
            {
                bufferedWriter.write("V ");//Linux
            }
            else if ("windows".equalsIgnoreCase(strOS))
            {
                if ("Inbuild".equalsIgnoreCase(strPrinterType))
                {
                    bufferedWriter.write("V ");
                }
                else
                {
                    bufferedWriter.write(" m");//windows
                }
            }
           
           bufferedWriter.close();
           fstream_Report.close();
            
            if (gShowBill.equals("Y"))
            {
                funShowTextFile(Text_DayEndReport, "", "");
            }
            funPrintToPrinter(strPrinterPort, "", "dayend", "N", isReprint,clientCode,posCode);//clsGlobalVarClass.gBillPrintPrinterPort

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void funLoadSetupData(String clientCode,String posCode)
    {
    	
    	try
    	{
//    		if(posCode.equals("All"))
//    		{
//        		posCode=clsPOSDayEndProcessConsolidate.loginPOS;
//        	}
    		gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gEnableShiftYN");
 			
    		PrintKOTYN = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gPrintKOTYN");
			
			gShowPrinterErrorMsg = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gShowPrinterErrorMsg");
			
			gMultiBillPrint = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gMultiBillPrint");
			
			gColumnSize = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gColumnSize");
			
			gOpenCashDrawerAfterBillPrintYN = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gOpenCashDrawerAfterBillPrintYN");
			
			gShowBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gShowBill");
			
			gPrintType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gPrintType");
			
			gMultipleKOTPrint = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gMultipleKOTPrint");
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    private void funCreateTempFolder()
    {
        try
        {
            String filePath = System.getProperty("user.dir");
            File Text_KOT = new File(filePath + "/Temp");
            if (!Text_KOT.exists())
            {
                Text_KOT.mkdirs();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funPrintBlankSpace(String printWord, BufferedWriter BWOut)
    {
        try
        {
            int wordSize = printWord.length();
            int actualPrintingSize = Integer.parseInt(gColumnSize);
            int availableBlankSpace = actualPrintingSize - wordSize;

            int leftSideSpace = availableBlankSpace / 2;
            if (leftSideSpace > 0)
            {
                for (int i = 0; i < leftSideSpace; i++)
                {
                    BWOut.write(" ");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funPrintToPrinter(String primaryPrinterName, String secPrinterName, String type, String printOnBothPrinters, boolean isReprint,String clientCode,String posCode)
    {
        try
        {
        	
        	 
 			
            String reportname = "";
            String fileName = "";
            if (type.equalsIgnoreCase("kot") || type.equalsIgnoreCase("checkkot"))
            {
                fileName = "Temp/Temp_KOT.txt";
                //fileName = "Temp/Temp_KOT.rtf";
            }
            else if (type.equalsIgnoreCase("dayend"))
            {
                fileName = "Temp/Temp_DayEndReport.txt";
                reportname = "dayend";
            }
            else if (type.equalsIgnoreCase("Adv Receipt"))
            {
                reportname = "Adv Receipt";
            }
            else if (type.equalsIgnoreCase("ItemWiseKOT"))
            {
                fileName = "/Temp/" + fileName + ".txt";
            }
            else
            {
                fileName = "Temp/Temp_Bill.txt";
                reportname = "bill";
            }

            if ("windows".equalsIgnoreCase(strOS))//&& clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File")
            {
                if (type.equalsIgnoreCase("kot"))
                {
                    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
                	
                    if (PrintKOTYN.equals("Y"))
                    {
                        funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters);
                        
                        if (gMultipleKOTPrint.equals("Y"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName);
                            }
                            funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters);
                        }
                    }
                }
                else if (type.equalsIgnoreCase("checkkot"))
                {
                    funPrintCheckKOTWindows(primaryPrinterName);
                }
                else if (type.equalsIgnoreCase("ItemWiseKOT"))
                {
                    funPrintItemWiseKOT(primaryPrinterName, secPrinterName, fileName);
                }
                else
                {
                    funPrintBillWindows(reportname);
                    //Avoid Muliple Bill Printing
                    if (!type.equalsIgnoreCase("dayend"))
                    {
                        if (gMultiBillPrint.equals("Y"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName);
                            }
                            funPrintBillWindows(reportname);
                        }
                    }
                }
            }
            else if ("linux".equalsIgnoreCase(strOS) && gPrintType.equalsIgnoreCase("Text File"))
            {
                if (type.equalsIgnoreCase("kot"))
                {
                    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
                    if (PrintKOTYN.equals("Y"))
                    {
                        Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);

                        if (gMultipleKOTPrint.equals("Y"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName);
                            }
                            process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                        }
                    }
                }
                else if (type.equalsIgnoreCase("checkkot"))
                {
                    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                }
                else if (type.equalsIgnoreCase("ItemWiseKOT"))
                {
                    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                }
                else
                {
                    //Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                    Process process = Runtime.getRuntime().exec("lpr -P " + strPrinterPort + " " + fileName, null);
                    if (!type.equalsIgnoreCase("dayend"))
                    {
                        if (gMultiBillPrint.equals("Y"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName);
                            }
                            process = Runtime.getRuntime().exec("lpr -P " + strPrinterPort+ " " + fileName, null);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funAppendDuplicate(String fileName)
    {
        try
        {
            File fileKOTPrint = new File(fileName);
//            RandomAccessFile f = new RandomAccessFile(fileKOTPrint, "rw");
//            f.seek(0); // to the beginning                  
//            BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF8"));            
//            funPrintBlankSpace("[DUPLICATE]", KotOut);            
//            KotOut.write("[DUPLICATE]");              
//            KotOut.newLine();            
//            KotOut.close();
//            f.close();                                    

            String filePath = System.getProperty("user.dir");
            filePath += "/Temp/Temp_KOT2.txt";
            File fileKOTPrint2 = new File(filePath);
            BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint2), "UTF8"));
            funPrintBlankSpace("[DUPLICATE]", KotOut);
            KotOut.write("[DUPLICATE]");
            KotOut.newLine();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileKOTPrint)));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                KotOut.write(line);
                KotOut.newLine();
            }
            br.close();
            KotOut.close();

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.CREATE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funWriteTotal(String title, String total, BufferedWriter out, String format)
    {
        try
        {
            int counter = 0;
            out.write("  ");
            counter = counter + 2;
            int length = title.length();
            out.write(title);
            counter = counter + length;
            funWriteFormattedAmt(counter, total, out, format);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funWriteTextWithBlankLines(String text, int len, BufferedWriter out) throws Exception
    {
        int remLen = len - text.trim().length();
        out.write(text);
        for (int cn = 0; cn < remLen; cn++)
        {
            out.write(" ");
        }
    }

    public void funShowTextFile(File file, String formName, String printerInfo)
    {
        try
        {
            String data = "";
            FileReader fread = new FileReader(file);
            //BufferedReader KOTIn = new BufferedReader(fread);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line = "";
            while ((line = KOTIn.readLine()) != null)
            {
                data = data + line + "\n";
            }
            String fileName = file.getName();
            String name = "";
            if (formName.trim().length() > 0)
            {
                name = formName;
            }
            if ("Temp_DayEndReport.txt".equalsIgnoreCase(fileName))
            {
                name = "DayEnd";
            }
           // new frmShowTextFile(data, name, file, printerInfo).setVisible(true);
            fread.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funPrintKOTWindows(String primaryPrinterName, String secPrinterName, String printOnBothPrinters)
    {
        String filePath = System.getProperty("user.dir");
        String fileName = (filePath + "/Temp/Temp_KOT.txt");
        //String fileName = (filePath + "/Temp/Temp_KOT.rtf");
        try
        {
            int printerIndex = 0;
            String printerStatus = "Not Found";
            System.out.println("Primary Name=" + primaryPrinterName);
            System.out.println("Sec Name=" + secPrinterName);

            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            primaryPrinterName = primaryPrinterName.replaceAll("#", "\\\\");
            secPrinterName = secPrinterName.replaceAll("#", "\\\\");

            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
            for (int i = 0; i < printService.length; i++)
            {
                System.out.println("Service=" + printService[i].getName() + "\tPrim P=" + primaryPrinterName);
                String printerServiceName = printService[i].getName();

                if (primaryPrinterName.equalsIgnoreCase(printerServiceName))
                {
                    System.out.println("Printer=" + primaryPrinterName);
                    printerIndex = i;
                    printerStatus = "Found";
                    break;
                }
            }

            if (printerStatus.equals("Found"))
            {
                DocPrintJob job = printService[printerIndex].createPrintJob();
                FileInputStream fis = new FileInputStream(fileName);
                DocAttributeSet das = new HashDocAttributeSet();
                Doc doc = new SimpleDoc(fis, flavor, das);
                job.print(doc, pras);
                String printerInfo = "";

                PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
                for (Attribute a : att.toArray())
                {
                    String attributeName;
                    String attributeValue;
                    attributeName = a.getName();
                    attributeValue = att.get(a.getClass()).toString();
                    if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
                    {
                        gPrinterQueueStatus = attributeValue;
                        printerInfo = primaryPrinterName + "!" + attributeValue;
                        //System.out.println(attributeName + " : " + attributeValue);
                    }
                }
                if (printOnBothPrinters.equals("Y"))
                {
                    funPrintOnSecPrinter(secPrinterName, fileName);
                }
            }
            else
            {
                funPrintOnSecPrinter(secPrinterName, fileName);
                //JOptionPane.showMessageDialog(null,primaryPrinterName+" Printer Not Found");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (gShowPrinterErrorMsg.equals("Y"))
            {
                try
                {
                    funPrintOnSecPrinter(secPrinterName, fileName);
                }
                catch (Exception ex)
                {
                 //   JOptionPane.showMessageDialog(null, "Secondary Printer Error= " + ex.getMessage());
                }
                //JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void funPrintOnSecPrinter(String secPrinterName, String fileName) throws Exception
    {
        String printerStatus = "Not Found";
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        int printerIndex = 0;
        for (int i = 0; i < printService.length; i++)
        {
            System.out.println("Service=" + printService[i].getName() + "\tSec P=" + secPrinterName);
            String printerServiceName = printService[i].getName();

            if (secPrinterName.equalsIgnoreCase(printerServiceName))
            {
                System.out.println("Sec Printer=" + secPrinterName);
                printerIndex = i;
                printerStatus = "Found";
                break;
            }
        }
        if (printerStatus.equals("Found"))
        {
            String printerInfo = "";
            DocPrintJob job = printService[printerIndex].createPrintJob();
            FileInputStream fis = new FileInputStream(fileName);
            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(fis, flavor, das);
            job.addPrintJobListener(new clsPOSTextFileGenerationForPrinting2.MyPrintJobListener());
            job.print(doc, pras);

            PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
            for (Attribute a : att.toArray())
            {
                String attributeName;
                String attributeValue;
                attributeName = a.getName();
                attributeValue = att.get(a.getClass()).toString();
                if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
                {
                    gPrinterQueueStatus = attributeValue;
                    printerInfo = secPrinterName + "!" + attributeValue;
                }
                System.out.println(attributeName + " : " + attributeValue);
            }
           /* if (gShowBill.equals("Y"))
            {
                funShowTextFile(new File(fileName), "", printerInfo);
            }*/
        }
        else
        {
          //  JOptionPane.showMessageDialog(null, secPrinterName + " Printer Not Found");
        }
    }

    private void funPrintCheckKOTWindows(String printerName)
    {
        try
        {
            int printerIndex = 0;
            String filePath = System.getProperty("user.dir");
            String filename = (filePath + "/Temp/Temp_KOT.txt");
            String billPrinterName = strPrinterPort;//clsGlobalVarClass.gBillPrintPrinterPort;
            billPrinterName = billPrinterName.replaceAll("#", "\\\\");
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
            for (int i = 0; i < printService.length; i++)
            {
                System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
                if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
                {
                    System.out.println("Bill Printer Sel=" + billPrinterName);
                    printerIndex = i;
                    break;
                }
            }

            DocPrintJob job = printService[printerIndex].createPrintJob();
            FileInputStream fis = new FileInputStream(filename);
            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(fis, flavor, das);
            job.print(doc, pras);
        }
        catch (Exception e)
        {
            if (gShowPrinterErrorMsg.equals("Y"))
            {
                //JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void funPrintItemWiseKOT(String primaryPrinterName, String secPrinterName, String fileName)
    {
        String filePath = System.getProperty("user.dir");
        fileName = (filePath + "/Temp/" + fileName + ".txt");

        try
        {
            String billPrinterName = strPrinterPort;

            billPrinterName = billPrinterName.replaceAll("#", "\\\\");
            int printerIndex = 0;
            PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
            for (int i = 0; i < printService.length; i++)
            {
                System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
                if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
                {
                    System.out.println("ItemWise KOT Printer found=>" + billPrinterName);
                    printerIndex = i;
                    break;
                }
            }
            //PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            //DocPrintJob job = defaultService.createPrintJob();
            DocPrintJob job = printService[printerIndex].createPrintJob();
            FileInputStream fis = new FileInputStream(fileName);

            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(fis, flavor, das);
            job.print(doc, printerReqAtt);
            System.out.println("Print Job Sent->" + fileName);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (gShowPrinterErrorMsg.equals("Y"))
            {
               // JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 02", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * printBillWindows() method print to Default Printer. No Parameter required
     */
    private void funPrintBillWindows(String type)
    {
        try
        {
            //System.out.println("Print Bill");
            String filePath = System.getProperty("user.dir");
            String fileName = "";
            String billPrinterName = strPrinterPort;// clsGlobalVarClass.gBillPrintPrinterPort;

            if (type.equalsIgnoreCase("bill"))
            {
                fileName = (filePath + "/Temp/Temp_Bill.txt");
            }
            else if (type.equalsIgnoreCase("Adv Receipt"))
            {
                fileName = (filePath + "/Temp/Temp_Bill.txt");
                billPrinterName =strAdvReceiptPrinter;// clsGlobalVarClass.gAdvReceiptPrinterPort;
            }
            else if (type.equalsIgnoreCase("dayend"))
            {
                fileName = (filePath + "/Temp/Temp_DayEndReport.txt");
            }

            billPrinterName = billPrinterName.replaceAll("#", "\\\\");
            int printerIndex = 0;
            PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
            for (int i = 0; i < printService.length; i++)
            {
                System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
                if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
                {
                    System.out.println("Bill Printer Sel=" + billPrinterName);
                    printerIndex = i;
                    break;
                }
            }
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            //DocPrintJob job = defaultService.createPrintJob();
            DocPrintJob job = printService[printerIndex].createPrintJob();
            FileInputStream fis = new FileInputStream(fileName);
            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(fis, flavor, das);
            job.print(doc, printerReqAtt);

            if (gOpenCashDrawerAfterBillPrintYN.equals("Y"))
            {
               // objUtility.funInvokeSampleJasper();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (gShowPrinterErrorMsg.equals("Y"))
            {
                //JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 02", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /*
     *Please Do Not modify Space in this Function 
     *Ritesh 10 Sept 214
     *
     *
     */

    private void funWriteFormattedAmt(int counter, String Amount, BufferedWriter out, String format)
    {
        try
        {
            int space = 30;
            if (format.equals("Format3"))
            {
                space = 29;
            }
            if (format.equals("Format4"))
            {
                space = 34;
            }
            if (format.equals("Format5"))
            {
                space = 29;
            }
            if (format.equals("Format6"))
            {
                space = 30;
            }
            if (format.equals("Format11"))
            {
                space = 12;
            }
            if (format.equals("Format13"))
            {
                space = 29;
            }
            int usedSpace = space - counter;
            for (int i = 0; i < usedSpace; i++)
            {
                out.write(" ");
            }
            out.write("  ");
            String tempAmount = Amount;

            int length = tempAmount.length();
            switch (length)
            {
                case 1:
                    out.write("        " + tempAmount);//8
                    break;
                case 2:
                    out.write("       " + tempAmount);//7
                    break;
                case 3:
                    out.write("      " + tempAmount);//6
                    break;
                case 4:
                    out.write("     " + tempAmount);//5
                    break;
                case 5:
                    out.write("    " + tempAmount);//4
                    break;
                case 6:
                    out.write("   " + tempAmount);//3
                    break;
                case 7:
                    out.write("  " + tempAmount);//2
                    break;
                case 8:
                    out.write(" " + tempAmount);//1
                    break;
                case 9:
                    out.write(tempAmount);//0
                    break;
                default:
                    out.write(tempAmount);//0
                    break;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    class MyPrintJobListener implements PrintJobListener
    {

        public void printDataTransferCompleted(PrintJobEvent pje)
        {
            System.out.println("printDataTransferCompleted");
        }

        public void printJobCanceled(PrintJobEvent pje)
        {
            System.out.println("The print job was cancelled");
        }

        public void printJobCompleted(PrintJobEvent pje)
        {
            System.out.println("The print job was completed");
        }

        public void printJobFailed(PrintJobEvent pje)
        {
            System.out.println("The print job has failed");
        }

        public void printJobNoMoreEvents(PrintJobEvent pje)
        {
        }

        public void printJobRequiresAttention(PrintJobEvent pje)
        {
        }
    }
    
    
    
    
    
}
