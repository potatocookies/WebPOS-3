package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSAddKOTToBillBean;
import com.sanguine.webpos.bean.clsPOSBillDiscountDtl;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillHd;
import com.sanguine.webpos.bean.clsPOSBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementDtl;
import com.sanguine.webpos.bean.clsPOSBillTaxDtl;
import com.sanguine.webpos.bean.clsPOSChangeSettlementBean;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.util.clsPOSSMSSender;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSVoidBillHd;
import com.sanguine.webpos.bean.clsPOSVoidBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSVoidKotBean;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSChangeSettlementController {

	
	@Autowired 
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	clsPOSVoidKotController objVoidController;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	clsPOSUtilityController objUtility;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map mapSettle=new TreeMap();
	Map mapReason=new HashMap();

	@RequestMapping(value = "/frmPOSChangeSettlement", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		mapSettle=funLoadSettelementModeList();
		model.put("settlementList",mapSettle);
		
		List listOfReasons =funGetReasonList();
		if(listOfReasons!=null)
		 {
			for(int i =0 ;i<listOfReasons.size();i++)
			{
				clsReasonMasterModel objModel = (clsReasonMasterModel) listOfReasons.get(i);
				mapReason.put(objModel.getStrReasonCode(),objModel.getStrReasonName());
			}
		}
		model.put("ReasonNameList",mapReason);
		
		
     	if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeSettlement_1","command", new clsPOSChangeSettlementBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeSettlement","command", new clsPOSChangeSettlementBean());
		}else {
			return null;
		}
	}
	
	public Map funLoadSettelementModeList(){
		
		Map mapSettle=new TreeMap();	
		
		try 
		{
			StringBuilder sqlBuilder=new StringBuilder();
 	       	sqlBuilder.append( "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio,strBillPrintOnSettlement "
            + " from tblsettelmenthd where strApplicable='Yes' and strBilling='Yes' ");  
    	    List listSql=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
    	    if(listSql.size()>0)
    	    {
      	       for(int j=0;j<listSql.size();j++)
      	       {
      		     Object obj = (Object ) listSql.get(j);
     	         mapSettle.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString()+"#"+Array.get(obj, 2).toString());
               }
    	     }
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mapSettle;
	}
	
	
	@RequestMapping(value = "/LoadBillDataForChangeSettlement", method = RequestMethod.GET)
	public @ResponseBody clsPOSChangeSettlementBean funLoadTableData(@RequestParam("BillNo") String billNo,@RequestParam("BillDate") String billDate,HttpServletRequest req) throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		clsPOSChangeSettlementBean obj=new clsPOSChangeSettlementBean();
		obj=funGetBillDtl(billNo,posCode,billDate,"");
		return obj;
	}
	
	
	 private clsPOSChangeSettlementBean funGetBillDtl(String billNo,String posCode,String billDate,String tableType) throws Exception
	 {
		    StringBuilder sqlBuilder = new StringBuilder();
		    clsPOSChangeSettlementBean objChangeSettleBean=new clsPOSChangeSettlementBean();
		    List<clsPOSBillSettlementDtl> listOfSettleMode=new ArrayList<>();
			double totalSettleAmt=0;
			String billhd = "tblbillhd";
            String billSettlement = "tblbillsettlementdtl";
            String creditBillReceipthd = "tblcreditbillreceipthd";
            if (tableType.equalsIgnoreCase("QFile"))
            {
                billhd = "tblqbillhd";
                billSettlement = "tblqbillsettlementdtl";
                creditBillReceipthd = "tblqcreditbillreceipthd";
            }
			sqlBuilder.setLength(0);
            sqlBuilder.append("select a.strBillNo as Bill_No,c.strSettelmentDesc as Settlement_Desc,b.strSettlementCode as settleCode,b.dblSettlementAmt  as Settlement_Amount,c.strSettelmentType as settleType  "
            		+ " from " + billhd + " a," + billSettlement + " b, tblsettelmenthd c  "
            		+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
            		+ " and c.strSettelmentType!='Complementary'  and date(a.dteBillDate)=date(b.dteBillDate)  "
            		+ " and a.strBillNo='" + billNo + "' and a.strPOSCode='" + posCode + "'  "
            		+ " and date(a.dteBillDate)='" + billDate + "'  group by a.strBillNo,b.strSettlementCode ");
			
			List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	        if(null!=list)
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					Object[] obj = (Object[]) list.get(cnt);
					clsPOSBillSettlementDtl objSettle=new clsPOSBillSettlementDtl();
					objSettle.setStrSettlementCode(obj[2].toString());
					objSettle.setStrSettlementName(obj[1].toString());
					objSettle.setStrSettlementType(obj[4].toString());
					objSettle.setDblSettlementAmt(Double.valueOf(obj[3].toString()));
					totalSettleAmt+=Double.valueOf(obj[3].toString());
					listOfSettleMode.add(objSettle);
				}
				

				objChangeSettleBean.setStrBillNo(billNo);
				objChangeSettleBean.setDblBillAmount(totalSettleAmt);
				objChangeSettleBean.setListOfBillSettleMode(listOfSettleMode);
			}
		    return objChangeSettleBean;
	 }
	 
	 
	 @RequestMapping(value = "/saveChangeSettlement", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSChangeSettlementBean objBean,BindingResult result,HttpServletRequest req)
		{
			try
			{
				String settleName="",tableType="";
				StringBuilder sqlBuilder =new StringBuilder();
				String posCode = req.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = req.getSession().getAttribute("gClientCode").toString();
	        	String userCode=req.getSession().getAttribute("gUserCode").toString();
	        	String posDate=req.getSession().getAttribute("gPOSDate").toString();
	        	int row=0;
	        	List<clsPOSBillSettlementDtl> listObjBillSettlementDtl = new ArrayList<clsPOSBillSettlementDtl>();
	        	if(objBean.getListOfBillSettleMode().size()>0)
	        	{
	        		for(int cnt=0;cnt<objBean.getListOfBillSettleMode().size();cnt++)
	        		{
	        			clsPOSBillSettlementDtl objSettle=objBean.getListOfBillSettleMode().get(cnt);
	        			if(!objSettle.getStrSettlementType().equals("Balance")) 
	        			{
	        				if (objSettle.getStrSettlementType().equals("Debit Card"))
		                     {
		                         objUtility.funDebitCardTransaction(objBean.getStrBillNo(), "debitCardNo", objSettle.getDblSettlementAmt(), "Settle", posCode, posDate);
		                         objUtility.funUpdateDebitCardBalance("debitCardNo",  objSettle.getDblSettlementAmt(), "Settle");
		                     }
		        			 row++;
		        			 
		        			 objSettle.setStrBillNo(objBean.getStrBillNo());
		        			 objSettle.setDblPaidAmt(objSettle.getDblSettlementAmt());
		        			 objSettle.setStrExpiryDate("");
		        			 objSettle.setStrCardName("");
		        			 objSettle.setStrRemark(objBean.getStrRemark());
		        			 objSettle.setStrClientCode(clientCode);
		        			 objSettle.setStrCustomerCode(objBean.getStrCustomerCode());
		        			 objSettle.setDblActualAmt(0);
		        			 objSettle.setDblRefundAmt(0);
		        			 objSettle.setStrGiftVoucherCode("");
		        			 objSettle.setStrDataPostFlag("N");
		        			 settleName=objSettle.getStrSettlementName();
		                     listObjBillSettlementDtl.add(objSettle);	
	        			}
	        			 
	        		}
	        	   funInsertBillSettlementDtlTable(listObjBillSettlementDtl,tableType,objBean.getStrBillNo(),objBean.getStrBillDate());
	        	   if (row > 1)
	               {
	                   settleName = "MultiSettle";
	               }

	               String billhd = "tblbillhd";
	               String billSettlement = "tblbillsettlementdtl";
	               String creditBillReceipthd = "tblcreditbillreceipthd";
	               if (tableType.equalsIgnoreCase("QFile"))
	               {
	                   billhd = "tblqbillhd";
	                   billSettlement = "tblqbillsettlementdtl";
	                   creditBillReceipthd = "tblqcreditbillreceipthd";
	               }

	               sqlBuilder.append("update " + billhd + " set strSettelmentMode='" + settleName + "',strCustomerCode='"+objBean.getStrCustomerCode()+"'"
	                       + ",strTransactionType=CONCAT(strTransactionType,',','Change Settlement') "
	                       + "where strBillNo='" + objBean.getStrBillNo() + "' "
	                       + "and strPOSCode='" + posCode + "' "
	                       + "and date(dteBillDate)='" + objBean.getStrBillDate() + "' ");
	               objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");	

	               // For Complimentary Bill
	               funClearComplimetaryBillAmt(objBean.getStrBillNo(),tableType,listObjBillSettlementDtl,posCode,clientCode,objBean.getStrReasonCode(),objBean.getStrRemark(),objBean.getStrBillDate(),userCode);
	               //for live data it will calculate at day end time 
	               //this is for QData
	               objUtility.funCalculateDayEndCashForQFile(objBean.getStrBillDate(), 1,req);
	               objUtility.funUpdateDayEndFieldsForQFile(objBean.getStrBillDate(), 1, "Y",req);
	        	}
	        	
	        	
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage","Changed Settlement Successfully");
										
				return new ModelAndView("redirect:/frmPOSChangeSettlement.html");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
	 
	 
	 
	 
	 @RequestMapping(value = "/LoadBillDate", method = RequestMethod.GET)
		public @ResponseBody clsPOSChangeSettlementBean funLoadTableData(@RequestParam("BillNo") String billNo,HttpServletRequest req) throws Exception
		{
			StringBuilder sqlBuilder = new StringBuilder();
			clsPOSChangeSettlementBean objBean=new clsPOSChangeSettlementBean();
			String posCode=req.getSession().getAttribute("loginPOS").toString();
			String billhd = "tblbillhd";
			sqlBuilder.setLength(0);
	        sqlBuilder.append("select a.strBillNo as Bill_No,a.dtBillDate as Bill_Date  "
	        		+ " from " + billhd + " a "
	        		+ " where a.strBillNo='" + billNo + "' and a.strPOSCode='" + posCode + "' ");
			
			List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	        if(null!=list)
			{
				for(int cnt=0;cnt<list.size();cnt++)
				{
					Object[] obj = (Object[]) list.get(cnt);
					objBean.setStrBillDate(obj[1].toString());
					objBean.setStrBillNo(obj[0].toString());
				}
			}
			return objBean;
		}
	 
	 
	   private int funInsertBillSettlementDtlTable(List<clsPOSBillSettlementDtl> listObjBillSettlementDtl,String tableType,String billNo,String billDate ) throws Exception
	    {
	        String billhd = "tblbillhd";
	        String billSettlement = "tblbillsettlementdtl";
	        String creditBillReceipthd = "tblcreditbillreceipthd";
	        StringBuilder sqlBuilder =new StringBuilder();
	        if (tableType.equalsIgnoreCase("QFile"))
	        {
	            billhd = "tblqbillhd";
	            billSettlement = "tblqbillsettlementdtl";
	            creditBillReceipthd = "tblqcreditbillreceipthd";
	        }

	        sqlBuilder.append("delete from " + billSettlement + " "
	                + " where strBillNo='" + billNo + "' "
	                + " and date(dteBillDate)='" + billDate + "' ");
	        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");	

	        sqlBuilder.setLength(0);
	        String sqlInsertBillSettlementDtl = "insert into " + billSettlement + " "
	                + "(strBillNo,strSettlementCode,dblSettlementAmt,dblPaidAmt,strExpiryDate"
	                + ",strCardName,strRemark,strClientCode,strCustomerCode,dblActualAmt"
	                + ",dblRefundAmt,strGiftVoucherCode,strDataPostFlag,dteBillDate) "
	                + "values ";
	        for (clsPOSBillSettlementDtl objBillSettlementDtl : listObjBillSettlementDtl)
	        {
	            sqlInsertBillSettlementDtl += "('" + objBillSettlementDtl.getStrBillNo() + "'"
	                    + ",'" + objBillSettlementDtl.getStrSettlementCode() + "'," + objBillSettlementDtl.getDblSettlementAmt() + ""
	                    + "," + objBillSettlementDtl.getDblPaidAmt() + ",'" + objBillSettlementDtl.getStrExpiryDate() + "'"
	                    + ",'" + objBillSettlementDtl.getStrCardName() + "','" + objBillSettlementDtl.getStrRemark() + "'"
	                    + ",'" + objBillSettlementDtl.getStrClientCode() + "','" + objBillSettlementDtl.getStrCustomerCode() + "'"
	                    + "," + objBillSettlementDtl.getDblActualAmt() + "," + objBillSettlementDtl.getDblRefundAmt() + ""
	                    + ",'" + objBillSettlementDtl.getStrGiftVoucherCode() + "','" + objBillSettlementDtl.getStrDataPostFlag() + "','" + billDate + "'),";
	        }
	        sqlBuilder = new StringBuilder(sqlInsertBillSettlementDtl);
	        int index1 = sqlBuilder.lastIndexOf(",");
	        sqlInsertBillSettlementDtl = sqlBuilder.delete(index1, sqlBuilder.length()).toString();
	        return  objBaseServiceImpl.funExecuteUpdate(sqlInsertBillSettlementDtl,"sql");
	    }
	   
	   
	   
	   private int funClearComplimetaryBillAmt(String billNo,String tableType,List<clsPOSBillSettlementDtl> listObjBillSettlementDtl,String posCode,String clientCode,String reasonCode,String remark,String billDate,String userCode) throws Exception
	    {
		    StringBuilder sqlBuilder =new StringBuilder();
	        String billhd = "tblbillhd";
	        String billSettlement = "tblbillsettlementdtl";
	        String billcomplementrydtl = "tblbillcomplementrydtl";
	        String billdiscdtl = "tblbilldiscdtl";
	        String billdtl = "tblbilldtl";
	        String billmodifierdtl = "tblbillmodifierdtl";
	        String billtaxdtl = "tblbilltaxdtl";
	        String creditBillReceipthd = "tblcreditbillreceipthd";
	        if (tableType.equalsIgnoreCase("QFile"))
	        {
	            billhd = "tblqbillhd";
	            billSettlement = "tblqbillsettlementdtl";
	            billcomplementrydtl = "tblqbillcomplementrydtl";
	            billdiscdtl = "tblqbilldiscdtl";
	            billdtl = "tblqbilldtl";
	            billmodifierdtl = "tblqbillmodifierdtl";
	            billtaxdtl = "tblqbilltaxdtl";
	            creditBillReceipthd = "tblqcreditbillreceipthd";
	        }

	        if (listObjBillSettlementDtl.size() == 1)
	        {
	        	for(int cnt=0;cnt<listObjBillSettlementDtl.size();cnt++)
	        	{
	        		clsPOSBillSettlementDtl objSettleBean=listObjBillSettlementDtl.get(cnt);
	        		if (objSettleBean.getStrSettlementType().equals("Complementary"))
	                {
	                    funInsertBillComplementryDtlTable(billNo,tableType);

	                    sqlBuilder.append("update " + billtaxdtl + " set dblTaxableAmount=0.00,dblTaxAmount=0.00 "
	                            + "where strBillNo='" + billNo + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("update " + billhd + " set dblTaxAmt=0.00,dblSubTotal=0.00"
	                            + ",dblDiscountAmt=0.00,dblDiscountPer=0.00,strReasonCode='" + reasonCode + "'"
	                            + ",strRemarks='" + remark + "',dblDeliveryCharges=0.00"
	                            + ",strCouponCode='coupon',dblGrandTotal=0.00,dblRoundOff=0.00 "
	                            + "where strBillNo='" + billNo + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("update " + billdtl + " set dblAmount=0.00,dblDiscountAmt=0.00,dblDiscountPer=0.00,dblTaxAmount=0.00 "
	                            + "where strBillNo='" + billNo + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("update " + billmodifierdtl + " set dblAmount=0.00,dblDiscPer=0.00,dblDiscAmt=0.00 where strBillNo='" + billNo + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("update " + billSettlement + " set dblSettlementAmt=0.00,dblPaidAmt=0.00 where strBillNo='" + billNo + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + billNo + "' "
	                            + " and strPOSCode='" + posCode + "' "
	                            + " and date(dteBillDate)='" + billDate + "' ");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
	                    sqlBuilder.append("delete from " + billdiscdtl + " where strBillNo='" + billNo + "' ");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                    
	                    //send modified bill MSG
	                    sqlBuilder.setLength(0);
	    	    		sqlBuilder.append("select a.strSendSMSYN,a.longMobileNo "
	                            + "from tblsmssetup a "
	                            + "where (a.strPOSCode='" + posCode + "' or a.strPOSCode='All') "
	                            + "and a.strClientCode='" + clientCode + "' "
	                            + "and a.strTransactionName='ComplementaryBill' "
	                            + "and a.strSendSMSYN='Y'; ");
	    	    		List listBillCustomer=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    	    		if(listBillCustomer.size()>0)
    					{
    					  for(int i=0;i<listBillCustomer.size();i++)
    					  {
    					    Object[] obj = (Object[]) listBillCustomer.get(i);
    					    String mobileNo = obj[1].toString();//mobileNo
	                        funSendComplementaryBillSMS(billNo, mobileNo,posCode,userCode);
    					  }
    					} 
	                   
	                }
	        	}
	        }
	        return 1;
	    }
	   
	   
	   
	   private int funInsertBillComplementryDtlTable(String billNo,String tableType) throws Exception
	    {
		    StringBuilder sqlBuilder =new StringBuilder();
	        String billhd = "tblbillhd";
	        String billSettlement = "tblbillsettlementdtl";
	        String billcomplementrydtl = "tblbillcomplementrydtl";
	        String billdiscdtl = "tblbilldiscdtl";
	        String billdtl = "tblbilldtl";
	        String billmodifierdtl = "tblbillmodifierdtl";
	        String billtaxdtl = "tblbilltaxdtl";
	        String creditBillReceipthd = "tblcreditbillreceipthd";
	        if (tableType.equalsIgnoreCase("QFile"))
	        {
	            billhd = "tblqbillhd";
	            billSettlement = "tblqbillsettlementdtl";
	            billcomplementrydtl = "tblqbillcomplementrydtl";
	            billdiscdtl = "tblqbilldiscdtl";
	            billdtl = "tblqbilldtl";
	            billmodifierdtl = "tblqbillmodifierdtl";
	            billtaxdtl = "tblqbilltaxdtl";
	            creditBillReceipthd = "tblqcreditbillreceipthd";
	        }

	        sqlBuilder.append("delete from " + billcomplementrydtl + " where strBillNo='" + billNo + "'");
	        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	        /*
	         * String sqlInsertBillComDtl = "insert into tblbillcomplementrydtl " +
	         * " select * from tblbilldtl where strBillNo='" + billNo + "' ";
	         */
	        sqlBuilder.setLength(0);
    		sqlBuilder.append("insert into " + billcomplementrydtl + "(strItemCode,strItemName,strBillNo"
	                + ",strAdvBookingNo,dblRate,dblQuantity,dblAmount,dblTaxAmount,dteBilldate,strKOTNo"
	                + ",strClientCode,strCustomerCode,tmeOrderProcessing,strDataPostFlag,strMMSDataPostFlag"
	                + ",strManualKOTNo,tdhYN,strPromoCode,strCounterCode,strWaiterNo,dblDiscountAmt,dblDiscountPer"
	                + ",strSequenceNo,dtBillDate,tmeOrderPickup)"
	                + " select strItemCode,strItemName,strBillNo"
	                + ",strAdvBookingNo,dblRate,dblQuantity,dblAmount,dblTaxAmount,dteBilldate,strKOTNo"
	                + ",strClientCode,strCustomerCode,tmeOrderProcessing,strDataPostFlag,strMMSDataPostFlag"
	                + ",strManualKOTNo,tdhYN,strPromoCode,strCounterCode,strWaiterNo,dblDiscountAmt,dblDiscountPer"
	                + ",strSequenceNo,dtBillDate,tmeOrderPickup from " + billdtl + " where strBillNo='" + billNo + "' ");

	        int ex =objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	        return ex;
	    }
	   
	   
	   
	   private void funSendComplementaryBillSMS(String billNo, String mobileNo,String posCode,String userCode)
	    {

	        try
	        {
	            StringBuilder mainSMSBuilder = new StringBuilder();
	            StringBuilder sqlBuilder =new StringBuilder();
	            DecimalFormat decimalFormat = new DecimalFormat("0.00");

	            mainSMSBuilder.append("ComplementaryBill");
	            mainSMSBuilder.append(" ,Bill_No:" + billNo);
	            mainSMSBuilder.append(" ,POS:" +posCode );
	            mainSMSBuilder.append(" ,User:" + userCode);

	            sqlBuilder.append("select a.strBillNo,TIME_FORMAT(time(a.dteBillDate),'%h:%i')time,c.strReasonName,a.strRemarks,sum(b.dblAmount) "
	                    + "from tblbillhd a "
	                    + "left outer join tblbillcomplementrydtl b on a.strBillNo=b.strBillNo "
	                    + "left outer join tblreasonmaster c on a.strReasonCode=c.strReasonCode "
	                    + "where a.strBillNo='" + billNo + "' "
	                    + "group by a.strBillNo ");
	            
	            List listBillCustomer=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    		if(listBillCustomer.size()>0)
				{
				  for(int i=0;i<listBillCustomer.size();i++)
				  {
				    Object[] obj = (Object[]) listBillCustomer.get(i);
				    mainSMSBuilder.append(" ,Time:" + obj[1].toString());
	                mainSMSBuilder.append(" ,Amount:" + decimalFormat.format(Math.rint(Double.valueOf(obj[4].toString()))));
	                mainSMSBuilder.append(" ,Reason:" + obj[2].toString());
	                mainSMSBuilder.append(" ,Remarks:" + obj[3].toString());
				  }
				}
	            ArrayList<String> mobileNoList = new ArrayList<>();
	            String mobNos[] = mobileNo.split(",");
	            for (String mn : mobNos)
	            {
	                mobileNoList.add(mn);
	            }
	            clsPOSSMSSender objSMSSender = new clsPOSSMSSender(mobileNoList, mainSMSBuilder.toString());
	            objSMSSender.start();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	   
	   
	   
		  public List<clsReasonMasterModel> funGetReasonList()throws Exception
		  {
			  StringBuilder sql = new StringBuilder();
			  List<clsReasonMasterModel> listReason=new ArrayList();
			  sql.append("select strReasonCode,strReasonName from tblreasonmaster where strComplementary='Y'");
	          List list = objBaseServiceImpl.funGetList(sql, "sql");
	          for(int i1=0 ;i1<list.size();i1++ )
	      	  {
	          	  Object[] obj = (Object[]) list.get(i1);
	          	  clsReasonMasterModel objModel=new clsReasonMasterModel();
	          	  objModel.setStrReasonCode(obj[0].toString());
	          	  objModel.setStrReasonName(obj[1].toString());
	          	  listReason.add(objModel);
	      	  }	
	          return listReason;
		  }
	
}