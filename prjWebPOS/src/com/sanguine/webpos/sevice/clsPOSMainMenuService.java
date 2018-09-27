package com.sanguine.webpos.sevice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.model.clsDayEndProcessHdModel;

@Service
public class clsPOSMainMenuService {

	@Autowired
	intfBaseService objBaseService;
	
	public List funGetMainMenuForms(String moduleType,boolean superUser,String POSCode,String userCode,String clientCode,String searchFrom) throws Exception
	{
		StringBuilder sbSql=new StringBuilder();
		if(userCode.equalsIgnoreCase("Sanguine"))
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sbSql.append(" select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	    				+ " FROM tblforms a WHERE (a.strModuleType='T' OR a.strModuleType='U' OR strModuleName='Customer Master') "
	    				+ " AND a.strModuleName!='NCKOT' AND a.strModuleName!='Complimentry Settlement'  "
	    				+ " AND a.strModuleName!='Discount On Bill' AND a.strModuleName!='NCKOT' "
	    				+ " AND a.strModuleName!='Take Away' AND a.strModuleName LIKE '%"+searchFrom+"%';");
	    	}
	    	else if (moduleType.equals("M"))
	    	{
	    		sbSql.append(" SELECT DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	    				+ " FROM tblforms a WHERE (a.strModuleType='M' OR a.strModuleType='U') "
	    				+ " AND a.strModuleName<>'ReOrderTime' AND a.strModuleName<>'Customer Master' "
	    				+ " AND a.strModuleName LIKE '%"+searchFrom+"%'  ");
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	    		sbSql.append(" SELECT DISTINCT  a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	        			+ " FROM tblforms a  WHERE (a.strModuleType='R' OR a.strModuleType='U') "
	        			+ " AND a.strModuleName LIKE '%"+searchFrom+"%'; ");
	    	}
	    }else  if(superUser)
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sbSql.append("select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	          	    + "from tblforms a,tblsuperuserdtl b  "
	           		+ " where b.strUserCode='"+userCode+"' and a.strModuleName=b.strFormName "
	           		+ " and  (a.strModuleType='T' or a.strModuleType='U'  or strModuleName='Customer Master') "
	           		+ " AND a.strModuleName LIKE '%"+searchFrom+"%' "
	           	    + "order by b.intSequence ");
	    	}
	    	else if (moduleType.equals("M"))
	    	{
	    		sbSql.append("select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	          	    + " from tblforms a,tblsuperuserdtl b "
	           		+ " where b.strUserCode='"+userCode+"' and a.strModuleName=b.strFormName "
	           		+ " and (a.strModuleType='M' or a.strModuleType='U') AND a.strModuleName LIKE '%"+searchFrom+"%' "
	           	    + " order by b.intSequence ");
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	    		sbSql.append("select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	        	    + " from tblforms a,tblsuperuserdtl b  "
	        		+ " where b.strUserCode='"+userCode+"' and a.strModuleName=b.strFormName "
	        		+ " and (a.strModuleType='R' or a.strModuleType='U') AND a.strModuleName LIKE '%"+searchFrom+"%' "
	        	    + " order by b.intSequence ");
	    	}
	    	else
	    	{
	    		sbSql.append("select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName "
	    			+ "from tblforms a,tblsuperuserdtl b "
	    			+ "where b.strUserCode='"+userCode+"' "
	    			+ "and a.strModuleName=b.strFormName "
	    			+ "and (a.strModuleName='Direct Biller' or a.strModuleName='Make KOT' "
	    			+ "or a.strModuleName='VoidKot' "
	    			+ "or a.strModuleName='Make Bill' or a.strModuleName='Sales Report' "
	    			+ "or a.strModuleName='Reprint' or a.strModuleName='SettleBill' "
	    			+ "or a.strModuleName='TableStatusReport' or a.strModuleName='NCKOT' "
	    	        + "or a.strModuleName='Take Away' or a.strModuleName='Table Reservation' "
	    			+ "or a.strModuleName='POS Wise Sales' or a.strModuleName='Customer Order' "
	    			+ "or a.strModuleName='Day End' or a.strModuleName='KDSForKOTBookAndProcess' "
	    			+ "or a.strModuleName='Kitchen Process System' ) "
	    			+ "AND a.strModuleName LIKE '%"+searchFrom+"%' ");																		  
	    	}
	    }
	    else
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sbSql.append(" select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName  "
	    			+ " from tblforms a,tbluserdtl b  "
	    	        + " where (a.strModuleType='T' or a.strModuleType='U' or strModuleName='Customer Master')  "
	    	        + " and b.strUserCode='"+userCode+"'  "
	    	        + " and a.strModuleName=b.strFormName  "
	    	        + " and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	    	        + " or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true') "
	    	        + " AND a.strModuleName LIKE '%"+searchFrom+"%' "
	    	        + " order by b.intSequence ");
	    	}
	    	else if(moduleType.equals("M"))
	    	{
	    		sbSql.append(" select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName  "
	          	      +  "from tblforms a,tbluserdtl b  "
	          	      +  "where (a.strModuleType='M' or a.strModuleType='U')  "
	          	      +  "and b.strUserCode='"+userCode+"'  "
	          	      +  "and a.strModuleName=b.strFormName  "
	          	      +  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	          	      +  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true') AND a.strModuleName LIKE '%"+searchFrom+"%' "
	          	      +  "order by b.intSequence ");
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	    		sbSql.append(" select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName  "
	    			+  "from tblforms a,tbluserdtl b  "
	            	+  "where (a.strModuleType='R' or a.strModuleType='U' )  "
	            	+  "and b.strUserCode='"+userCode+"'  "
	            	+  "and a.strModuleName=b.strFormName  "
	            	+  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	            	+  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true') AND a.strModuleName LIKE '%"+searchFrom+"%' "
	            	+  "order by b.intSequence ");
	    	}
	    	else 
	    	{
	    		sbSql.append("select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping, a.strShortName  "
					+ " from tblforms a,tbluserdtl b "
					+ " where (a.strModuleType='T'  or a.strModuleType='U' or a.strModuleType='R'  or a.strModuleName='Customer Master')  "
					+ " and a.strModuleName!='Complimentry Settlement' and a.strModuleName!='Discount On Bill'  "
					+ " and b.strUserCode='"+userCode+"' and a.strModuleName like '%%' and a.strModuleName=b.strFormName "
					+ " and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
					+ " or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true' or b.strTLA='true' ) "
					+ " and a.strModuleName in ('Direct Biller','Make KOT','VoidKot'"
					+ ",'Make Bill','Sales Report','Reprint','SettleBill','TableStatusReport'"
					+ ",'NCKOT','Take Away','Table Reservation','POS Wise Sales','Customer Order'"
					//+ ",'Non Available Items','Mini Make KOT','Day End','KDSForKOTBookAndProcess','Kitchen Process System') "
					+ ",'Day End','KDSForKOTBookAndProcess','Kitchen Process System') "
					+ "AND a.strModuleName LIKE '%"+searchFrom+"%' "
					+ " order by b.intSequence");
	    	}
	    }
		
		List list=null;
    	if(sbSql.length()>0)
    	{
    		list=objBaseService.funGetList(sbSql, "sql");
    	}
    	
    	return list;
	}
	
	
	
	public Map<String,String> funGetPOSWiseDayEndData(String POSCode,String userCode, String clientCode)
	{
		Map<String,String> hmDayEndDetails=new HashMap<String,String>();
		try
		{
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select count(*) from tblsettelmenthd where strClientCode='"+clientCode+"' ");
			List listSet=objBaseService.funGetList(sbSql, "sql");
			if (listSet.size() > 0)
			{
				String cnt=listSet.get(0).toString();
				if(Integer.parseInt(cnt)>0)
				{
					Boolean gShifts = false;
					sbSql.setLength(0);
					sbSql.append("select count(intShiftCode) from tblshiftmaster where strPOSCode='" + POSCode + "' and strClientCode='"+clientCode+"' ");
		            
		            List listShiftCode=objBaseService.funGetList(sbSql, "sql");
		            if(listShiftCode.size()>0)
			        {
		            	if (Integer.parseInt(listShiftCode.get(0).toString()) > 0)
			            {
		            		gShifts = true;
			            }
			            else
			            {
			            	gShifts = false;
			            }
			        }
		            
		            hmDayEndDetails.put("gShifts", String.valueOf(gShifts));
					       	
		            sbSql.setLength(0);
		            sbSql.append("select count(*) from tbldayendprocess "
		            	+ " where strPOSCode='" + POSCode + "' "
		            			+ " and strDayEnd='N' "
		            			+ " and (strShiftEnd='' or strShiftEnd='N') "
		            			+ " and strClientCode='"+clientCode+"' ");
			        listSet=objBaseService.funGetList(sbSql, "sql");
					int countEnd=0;
					if (listSet.size() > 0)
					{
						countEnd = Integer.parseInt(listSet.get(0).toString());
					}
					if (countEnd > 0)
			        {
						sbSql.setLength(0);
			            sbSql.append("select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd"
				            + " from tbldayendprocess where strPOSCode='"+ POSCode + "' "
				            		+ "and strDayEnd='N' "
				            + " and (strShiftEnd='' or strShiftEnd='N') "
				            + " and strClientCode='"+clientCode+"'  ");
			            listSet=objBaseService.funGetList(sbSql, "sql");
			 			if (listSet.size() > 0)
			 			{
			 				Object obSett[]=(Object[]) listSet.get(0);
 			 				hmDayEndDetails.put("startDate", obSett[0].toString());
			 				hmDayEndDetails.put("ShiftNo", obSett[1].toString());
			 				hmDayEndDetails.put("ShiftEnd", obSett[2].toString());
			 				hmDayEndDetails.put("DayEnd", obSett[3].toString());
			 						 
			 				if(gShifts)
				            {
			 					sbSql.setLength(0);
					            sbSql.append("select intShiftCode,strBillDateTimeType from tblshiftmaster "
			 						+ "where strPOSCode='" + POSCode + "' "
			 								+ " and intShiftCode=" + Integer.parseInt(obSett[1].toString()) +""
			 										+ " and strClientCode='"+clientCode+"' ");
					            List listShiftInfo=objBaseService.funGetList(sbSql, "sql");
					 	 		if (listShiftInfo.size() > 0)
					 	 		{
					 	 			Object obShift[]=(Object[]) listShiftInfo.get(0);
					 	 			hmDayEndDetails.put("gBillDateTimeType", obShift[1].toString());
					 	 							                    
				            	}
			                }
			                
			            }
			 			
					}
					else
	                {			                	
	                	int h, m, sec, d, min, y, lblCount, cntNavigate;
	                    String dteCreated, time, date;
	                	Date endDt = new Date();
	                    String todayDate = (endDt.getYear() + 1900) + "-" + (endDt.getMonth() + 1) + "-" + endDt.getDate();
	                    java.util.Date curDt = new java.util.Date();
	                    d = curDt.getDate();
	                    m = curDt.getMonth() + 1;
	                    y = curDt.getYear() + 1900;
	                    h = curDt.getHours();
	                    min = curDt.getMinutes();
	                    sec = curDt.getSeconds();
	                    time = h + ":" + min + ":" + sec;
	                    date = y + "-" + m + "-" + d;
	                    dteCreated = date + " " + time;
	                      
	                    hmDayEndDetails.put("startDate", todayDate);
	                    
	                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	                    Date bDate = dFormat.parse(todayDate);
	                    long posTime = bDate.getTime();
	                    Date currDate = new Date();
	                    long currTime = currDate.getTime();
	                    long diffTime = currTime - posTime;
	                    long diffDays = diffTime / (24 * 60 * 60 * 1000);
	                    
	                    clsDayEndProcessHdModel objDayEndProcessHdModel = new clsDayEndProcessHdModel();
	                    objDayEndProcessHdModel.setStrPOSCode(POSCode);
	                    objDayEndProcessHdModel.setDtePOSDate(todayDate);
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
	                    objDayEndProcessHdModel.setIntShiftCode(0);
	                    objDayEndProcessHdModel.setStrShiftEnd("");
	                    objDayEndProcessHdModel.setIntTotalPax(0);
	                    objDayEndProcessHdModel.setIntNoOfTakeAway(0);
	                    objDayEndProcessHdModel.setIntNoOfHomeDelivery(0);
	                    objDayEndProcessHdModel.setStrUserCreated(userCode);
	                    objDayEndProcessHdModel.setStrUserEdited(userCode);
	                    objDayEndProcessHdModel.setDteDateCreated(dteCreated);
	                    objDayEndProcessHdModel.setDteDayEndDateTime(dteCreated);
	                    objDayEndProcessHdModel.setStrClientCode(clientCode);
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
	                    
	                    objBaseService.funSave(objDayEndProcessHdModel);
	                   
	 					hmDayEndDetails.put("ShiftEnd", "");
	 					hmDayEndDetails.put("DayEnd", "N");
	 					hmDayEndDetails.put("ShiftNo","0");
	                }
					
				}
			}
			else
			{
				 //new frmOkPopUp(null, "Settlement Type is not Present", "Warning", 1).setVisible(true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return hmDayEndDetails;
	}
}
