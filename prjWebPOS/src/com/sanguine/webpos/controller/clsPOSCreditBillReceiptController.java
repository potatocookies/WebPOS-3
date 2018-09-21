package com.sanguine.webpos.controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.webpos.bean.clsPOSChangeSettlementBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSCreditBillReceiptController
{
	@Autowired
	clsPOSChangeSettlementController objPOSChangeSettlement;
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
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		mapSettle=objPOSChangeSettlement.funLoadSettelementModeList();
		model.put("settlementList",mapSettle);
		
     	if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCreditBillReceipt_1","command", new clsPOSChangeSettlementBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCreditBillReceipt","command", new clsPOSChangeSettlementBean());
		}else {
			return null;
		}
	}
}


/*
//live
	hqlQuery.setLength(0);
	hqlQuery.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
           + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
           + "WHERE a.strBillNo=b.strBillNo  "
           + "AND b.strSettlementCode=c.strSettelmentCode  "
           + "and date(a.dtebilldate)=date(b.dtebilldate)  "
           + "and a.strClientCode=b.strClientCode  "
           + "AND c.strSettelmentType='Credit'  "
           + "AND a.strPOSCode='" + spData[1] + "'  "
           + "AND a.strCustomerCode='" + spData[0] + "' "
           + "GROUP BY a.strBillNo ");
	 
	 list=objBaseService.funGetList(hqlQuery, "sql");	
	 if(null != list)		
	 {	 
		 for(int cnt=0;cnt<list.size();cnt++)
		 {
			 Object[] objArr = (Object[]) list.get(cnt);
			 String billNo = objArr[0].toString();
			 String filterBillDate = objArr[1].toString();
			 String billDate = objArr[2].toString();
			 clientCode = objArr[3].toString();
			 double creditAmount = Double.valueOf(objArr[4].toString());
			 
			//remove full paid bills
           //live
			hqlQuery.setLength(0);
			hqlQuery.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
          + "from tblqcreditbillreceipthd a "
          + "where a.strPOSCode='" + spData[1] + "'  "
          + "and a.strBillNo='" + billNo + "' "
          + "and date(a.dteBillDate)='" + filterBillDate + "' "
          + "and a.strClientCode='" + clientCode + "' ");
			 
			 List listPaidBill=objBaseService.funGetList(hqlQuery, "sql");	
	    		 if(null != listPaidBill)		
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
	                    	clsCreditBillReceipt objCreditBillReceipt = new clsCreditBillReceipt();
	                        objCreditBillReceipt.setStrBillNo(billNo);
	                        objCreditBillReceipt.setDteBillDate(billDate);
	                        objCreditBillReceipt.setDblCreditAmount(creditAmount);
	                        objCreditBillReceipt.setStrClientCode(clientCode);
	                        listOfCreditBillReceipts.add(objCreditBillReceipt);
		                    JSONArray jArrDataRow = new JSONArray();
	    				jArrDataRow.add(billNo);
	    				jArrDataRow.add(billDate);
	    				jArrDataRow.add(creditAmount);
	    				jArrDataRow.add(clientCode);
	    				jArrDataRow.add(objCreditBillReceipt);
	    				jArrData.add(jArrDataRow);
	                    }
	    			 }
				   }
			       else
	                {
  	                    clsCreditBillReceipt objCreditBillReceipt = new clsCreditBillReceipt();
  	                    objCreditBillReceipt.setStrBillNo(billNo);
  	                    objCreditBillReceipt.setDteBillDate(billDate);
  	                    objCreditBillReceipt.setDblCreditAmount(creditAmount);
  	                    objCreditBillReceipt.setStrClientCode(clientCode);
  	                    listOfCreditBillReceipts.add(objCreditBillReceipt);
  	                    JSONArray jArrDataRow = new JSONArray();
	    				jArrDataRow.add(billNo);
	    				jArrDataRow.add(billDate);
	    				jArrDataRow.add(creditAmount);
	    				jArrDataRow.add(clientCode);
	    				jArrDataRow.add(objCreditBillReceipt);
	    				jArrData.add(jArrDataRow);
	                }
		  }
	}
	
//QFile
	hqlQuery.setLength(0);
	hqlQuery.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
            + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
            + "WHERE a.strBillNo=b.strBillNo  "
            + "AND b.strSettlementCode=c.strSettelmentCode  "
            + "and date(a.dtebilldate)=date(b.dtebilldate)  "
            + "and a.strClientCode=b.strClientCode  "
            + "AND c.strSettelmentType='Credit'  "
            + "AND a.strPOSCode='" + spData[1]  + "'  "
            + "AND a.strCustomerCode='" + spData[0]  + "' "
            + "GROUP BY a.strBillNo ");
	
	
	 list=objBaseService.funGetList(hqlQuery, "sql");	
	 if(null != list)		
	 {	 
		 for(int cnt=0;cnt<list.size();cnt++)
		 {
			 Object[] objArr = (Object[]) list.get(cnt);
			 String billNo = objArr[0].toString();
			 String filterBillDate = objArr[1].toString();
			 String billDate = objArr[2].toString();
			 clientCode = objArr[3].toString();
			 double creditAmount = Double.valueOf(objArr[4].toString());
			 
			//remove full paid bills
            //live
			hqlQuery.setLength(0);
			hqlQuery.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
           + "from tblqcreditbillreceipthd a "
           + "where a.strPOSCode='" + spData[1] + "'  "
           + "and a.strBillNo='" + billNo + "' "
           + "and date(a.dteBillDate)='" + filterBillDate + "' "
           + "and a.strClientCode='" + clientCode + "' ");
			 
			 List listPaidBill=objBaseService.funGetList(hqlQuery, "sql");	
	    		 if(null != listPaidBill)		
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
	                    	clsCreditBillReceipt objCreditBillReceipt = new clsCreditBillReceipt();
	                        objCreditBillReceipt.setStrBillNo(billNo);
	                        objCreditBillReceipt.setDteBillDate(billDate);
	                        objCreditBillReceipt.setDblCreditAmount(creditAmount);
	                        objCreditBillReceipt.setStrClientCode(clientCode);
	                        listOfCreditBillReceipts.add(objCreditBillReceipt);
		                    JSONArray jArrDataRow = new JSONArray();
	    				jArrDataRow.add(billNo);
	    				jArrDataRow.add(billDate);
	    				jArrDataRow.add(creditAmount);
	    				jArrDataRow.add(clientCode);
	    				jArrDataRow.add(objCreditBillReceipt);
	    				jArrData.add(jArrDataRow);
	                    }
	    			 }
				   }
			       else
	                {
   	                    clsCreditBillReceipt objCreditBillReceipt = new clsCreditBillReceipt();
   	                    objCreditBillReceipt.setStrBillNo(billNo);
   	                    objCreditBillReceipt.setDteBillDate(billDate);
   	                    objCreditBillReceipt.setDblCreditAmount(creditAmount);
   	                    objCreditBillReceipt.setStrClientCode(clientCode);
   	                    listOfCreditBillReceipts.add(objCreditBillReceipt);
   	                    JSONArray jArrDataRow = new JSONArray();
	    				jArrDataRow.add(billNo);
	    				jArrDataRow.add(billDate);
	    				jArrDataRow.add(creditAmount);
	    				jArrDataRow.add(clientCode);
	    				jArrDataRow.add(objCreditBillReceipt);
	    				jArrData.add(jArrDataRow);
	                }
		  }
	} 
	 
    jObjSearchData.put(masterName, jArrData);
   */