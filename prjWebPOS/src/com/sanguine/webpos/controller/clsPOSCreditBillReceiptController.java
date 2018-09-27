package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSettlementDtl;
import com.sanguine.webpos.bean.clsPOSChangeSettlementBean;
import com.sanguine.webpos.bean.clsPOSCreditBillReceiptBean;

@Controller
public class clsPOSCreditBillReceiptController
{
	@Autowired
	clsBaseServiceImpl objBaseService;
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	Map mapSettle=new TreeMap();
	
	@RequestMapping(value = "/frmPOSCreditBillReceipt", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		mapSettle=funLoadSettelementModeList();
		model.put("settlementList",mapSettle);
		
     	if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCreditBillReceipt_1","command", new clsPOSCreditBillReceiptBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCreditBillReceipt","command", new clsPOSCreditBillReceiptBean());
		}else {
			return null;
		}
	}
	
	
	
	@RequestMapping(value = "/getCreditBillReceiptData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSCreditBillReceiptBean>  funLoadTableData(@RequestParam("BillNo") String billNo,@RequestParam("CustomerCode") String customerCode,HttpServletRequest req) throws Exception
	{
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		List<clsPOSCreditBillReceiptBean> listOfCreditBillReceipts=funGetBillDtl(billNo,posCode,customerCode);
		return listOfCreditBillReceipts;
	}
	
	
	 private List<clsPOSCreditBillReceiptBean>  funGetBillDtl(String billNo,String posCode,String customerCode) throws Exception
	 {
		    String clientCode="";
		    StringBuilder sqlBuilder = new StringBuilder();
		    List<clsPOSCreditBillReceiptBean> listOfCreditBillReceipts=new ArrayList<>();
		   
		  //live
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
		             + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
		             + "WHERE a.strBillNo=b.strBillNo  "
		             + "AND b.strSettlementCode=c.strSettelmentCode  "
		             + "and date(a.dtebilldate)=date(b.dtebilldate)  "
		             + "and a.strClientCode=b.strClientCode  "
		             + "and c.strSettelmentType='Credit'  "
		             + "and a.strPOSCode='" + posCode + "'  "
		             + "and a.strCustomerCode='" + customerCode + "' and a.strBillNo='"+billNo+"' "
		             + "GROUP BY a.strBillNo ");
		  	 
		  	 List list=objBaseService.funGetList(sqlBuilder, "sql");	
		  	 if(null != list && list.size()>0)		
		  	 {	 
		  		 for(int cnt=0;cnt<list.size();cnt++)
		  		 {
		  			 Object[] objArr = (Object[]) list.get(cnt);
		  			 String filterBillDate = objArr[1].toString();
		  			 String billDate = objArr[2].toString();
		  			 clientCode = objArr[3].toString();
		  			 double creditAmount = Double.valueOf(objArr[4].toString());
		  			 
		  			//remove full paid bills
		             //live
		  			sqlBuilder.setLength(0);
		  			sqlBuilder.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
		            + "from tblqcreditbillreceipthd a "
		            + "where a.strPOSCode='" + posCode + "'  "
		            + "and a.strBillNo='" + billNo + "' "
		            + "and date(a.dteBillDate)='" + filterBillDate + "' "
		            + "and a.strClientCode='" + clientCode + "' GROUP BY a.strBillNo ");
		  			 
		  			 List listPaidBill=objBaseService.funGetList(sqlBuilder, "sql");	
		  	    		 if(null != listPaidBill && listPaidBill.size()>0)		
		  				  {	 
		  	    			 for(int i=0;i<listPaidBill.size();i++)
		  	    			 {
		  	    				Object[] objPaidBillArr = (Object[]) listPaidBill.get(i);
		  	    			    double totalReceiptAmt = Double.valueOf(objPaidBillArr[4].toString());
		  	                    if (Math.rint(creditAmount) == Math.rint(totalReceiptAmt))
		  	                    {
		  	                        //dont add
		  	                    }
		  	                    else
		  	                    {
		  	                    	clsPOSCreditBillReceiptBean objCreditBillReceipt = new clsPOSCreditBillReceiptBean();
		  	                        objCreditBillReceipt.setStrBillNo(billNo);
		  	                        objCreditBillReceipt.setDteBillDate(billDate);
		  	                        objCreditBillReceipt.setDblCreditAmount(creditAmount);
		  	                        objCreditBillReceipt.setStrClientCode(clientCode);
		  	                        listOfCreditBillReceipts.add(objCreditBillReceipt);
		  	                    }
		  	    			 }
		  				   }
		  			       else
		  	                {
		    	                    clsPOSCreditBillReceiptBean objCreditBillReceipt = new clsPOSCreditBillReceiptBean();
		    	                    objCreditBillReceipt.setStrBillNo(billNo);
		    	                    objCreditBillReceipt.setDteBillDate(billDate);
		    	                    objCreditBillReceipt.setDblCreditAmount(creditAmount);
		    	                    objCreditBillReceipt.setStrClientCode(clientCode);
		    	                    listOfCreditBillReceipts.add(objCreditBillReceipt);
		  	                }
		  		  }
		  	}
		  	
		  //QFile
		  	sqlBuilder.setLength(0);
		  	sqlBuilder.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
		              + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
		              + "WHERE a.strBillNo=b.strBillNo  "
		              + "and b.strSettlementCode=c.strSettelmentCode  "
		              + "and date(a.dtebilldate)=date(b.dtebilldate)  "
		              + "and a.strClientCode=b.strClientCode  "
		              + "and c.strSettelmentType='Credit'  "
		              + "and a.strPOSCode='" + posCode  + "'  "
		              + "and a.strCustomerCode='" + customerCode + "' and a.strBillNo='"+billNo+"'"
		              + "GROUP BY a.strBillNo ");
		  	
		  	
		  	 list=objBaseService.funGetList(sqlBuilder, "sql");	
		  	 if(null != list && list.size()>0)		
		  	 {	 
		  		 for(int cnt=0;cnt<list.size();cnt++)
		  		 {
		  			 Object[] objArr = (Object[]) list.get(cnt);
		  			 String filterBillDate = objArr[1].toString();
		  			 String billDate = objArr[2].toString();
		  			 clientCode = objArr[3].toString();
		  			 double creditAmount = Double.valueOf(objArr[4].toString());
		  			 
		  			//remove full paid bills
		              //live
		  			sqlBuilder.setLength(0);
		  			sqlBuilder.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
		             + "from tblqcreditbillreceipthd a "
		             + "where a.strPOSCode='" + posCode + "'  "
		             + "and a.strBillNo='" + billNo + "' "
		             + "and date(a.dteBillDate)='" + filterBillDate + "' "
		             + "and a.strClientCode='" + clientCode + "'  GROUP BY a.strBillNo");
		  			 
		  			 List listPaidBill=objBaseService.funGetList(sqlBuilder, "sql");	
	  	    		 if(null != listPaidBill && listPaidBill.size()>0)		
	  				  {	 
	  	    			 for(int i=0;i<listPaidBill.size();i++)
	  	    			 {
	  	    				Object[] objPaidBillArr = (Object[]) listPaidBill.get(i);
	  		    			double totalReceiptAmt = Double.valueOf(objPaidBillArr[4].toString());
	  	                    if (Math.rint(creditAmount) == Math.rint(totalReceiptAmt))
	  	                    {
	  	                        //dont add
	  	                    }
	  	                    else
	  	                    {
	  	                    	clsPOSCreditBillReceiptBean objCreditBillReceipt = new clsPOSCreditBillReceiptBean();
	  	                        objCreditBillReceipt.setStrBillNo(billNo);
	  	                        objCreditBillReceipt.setDteBillDate(billDate);
	  	                        objCreditBillReceipt.setDblCreditAmount(creditAmount);
	  	                        objCreditBillReceipt.setStrClientCode(clientCode);
	  	                        listOfCreditBillReceipts.add(objCreditBillReceipt);
	  	                    }
	  	    			 }
	  				   }
	  			       else
	  	                {
	     	                    clsPOSCreditBillReceiptBean objCreditBillReceipt = new clsPOSCreditBillReceiptBean();
	     	                    objCreditBillReceipt.setStrBillNo(billNo);
	     	                    objCreditBillReceipt.setDteBillDate(billDate);
	     	                    objCreditBillReceipt.setDblCreditAmount(creditAmount);
	     	                    objCreditBillReceipt.setStrClientCode(clientCode);
	     	                    listOfCreditBillReceipts.add(objCreditBillReceipt);
	  	                }
		  		  }
		  	} 
		  
	  return listOfCreditBillReceipts;
	 }
	 
	 
	 
	 
	 
	 @RequestMapping(value = "/saveCreditReceipt", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSCreditBillReceiptBean objBean,BindingResult result,HttpServletRequest req)
		{
			try
			{
				StringBuilder sqlBuilder =new StringBuilder();
				String posCode = req.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = req.getSession().getAttribute("gClientCode").toString();
	        	String userCode=req.getSession().getAttribute("gUserCode").toString();
	        	String posDate=req.getSession().getAttribute("gPOSDate").toString();
	        	String receiptNo = funGenerateReceiptNo();
	        	String chequeNo = "";
	            String chequeDate = "1990-01-01 00:00:00";
	        	
	        	
	        	sqlBuilder.append("insert into tblqcreditbillreceipthd(strReceiptNo,strBillNo,strPOSCode,dteReceiptDate,dblReceiptAmt,intShiftCode"
	                    + ",strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strDataPostFlag,dteBillDate"
	                    + ",strSettlementCode,strSettlementName,strChequeNo,strBankName,dteChequeDate,strRemarks)"
	                    + "values "
	                    + "('" + receiptNo + "','" + objBean.getStrBillNo() + "','" + posCode+ "','" + posDate + "','" + objBean.getDblReceiptAmount() + "','1',"
	                    + "'" + userCode + "','" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "','N'"
	                    + ",'" + objBean.getDteBillDate() + "','" + objBean.getStrSettleCode() + "','" + objBean.getStrSettleName() + "'"
	                    + ",'','','" + chequeDate + "','" + objBean.getStrRemark() + "')");

	        	objBaseService.funExecuteUpdate(sqlBuilder.toString(),"sql");
		
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage","Entry Added Successfully");
									
			return new ModelAndView("redirect:/frmPOSCreditBillReceipt.html");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
	 
	   
	 
	 private String funGenerateReceiptNo()
	  {
        String receiptNo = "";
        StringBuilder sqlBuilder =new StringBuilder();
        try
        {
        	sqlBuilder.append("select count(dblLastNo) from tblinternal where strTransactionType='CreditReceipt'");
        	List listBill=objBaseService.funGetList(sqlBuilder, "sql");
    		if(null != listBill && listBill.size()>0)
			{
			  for(int i=0;i<listBill.size();i++)
			  {
			    if (!listBill.get(0).toString().equals("0"))
				{
			    	sqlBuilder.setLength(0);
			    	sqlBuilder.append("select dblLastNo from tblinternal where strTransactionType='CreditReceipt'");
			    	List listReceipt=objBaseService.funGetList(sqlBuilder, "sql");
		    		if(listReceipt.size()>0)
					{
					  for(int cnt=0;cnt<listReceipt.size();cnt++)
					  {
						  int code = Integer.parseInt(listReceipt.get(cnt).toString());
						  code = code + 1;
			              receiptNo = "CR" + String.format("%05d", code);
			              //clsGlobalVarClass.gUpdatekot = true;
			              //clsGlobalVarClass.gKOTCode = code;
			              sqlBuilder.setLength(0);
			              sqlBuilder.append("update tblinternal set dblLastNo='" + code + "' where strTransactionType='CreditReceipt'");
			              objBaseService.funExecuteUpdate(sqlBuilder.toString(),"sql");
					  }
					}
	               
	            }
	            else
	            {
	                receiptNo = "CR00001";
	                //clsGlobalVarClass.gUpdatekot = false;
	                sqlBuilder.setLength(0);
		            sqlBuilder.append("insert into tblinternal values('CreditReceipt'," + 1 + ")");
		            objBaseService.funExecuteUpdate(sqlBuilder.toString(),"sql");
	            }
			  }
			}  
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return receiptNo;
	  }
	
		public Map funLoadSettelementModeList(){
			
			Map mapSettle=new TreeMap();	
			
			try 
			{
				StringBuilder sqlBuilder=new StringBuilder();
	 	       	sqlBuilder.append( "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio,strBillPrintOnSettlement "
	            + " from tblsettelmenthd where strApplicable='Yes' and strCreditReceiptYN='Y' ");  
	    	    List listSql=objBaseService.funGetList(sqlBuilder, "sql");
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
	 
	
}
