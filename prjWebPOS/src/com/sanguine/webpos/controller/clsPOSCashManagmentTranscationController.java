package com.sanguine.webpos.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;
import com.sanguine.webpos.bean.clsPOSCashManagmentTranscationBean;
import com.sanguine.webpos.model.clsPOSCashManagmentTranscationModel;
import com.sanguine.webpos.model.clsPOSCashManagmentTranscationModel_ID;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;


@Controller
public class clsPOSCashManagmentTranscationController{

	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objGlobalFun;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	

	Map mapReason=new HashMap();

	//Open POSCashManagmentTranscation
	@RequestMapping(value = "/frmPOSCashManagement", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		String clientCode=request.getSession().getAttribute("gClientCode").toString();

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
		
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCashManagement_1","command", new clsPOSCashManagmentTranscationBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCashManagement","command", new clsPOSCashManagmentTranscationBean());
		}else {
		return null;
		}
	}

	@RequestMapping(value ="/savePOSCashManagmentTranscation", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSCashManagmentTranscationBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String code="";
		System.out.println(objBean);
		try
		{
			
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String posCode=req.getSession().getAttribute("loginPOS").toString();
			double strAmount= objBean.getDblAmount();
			//String Date=objBean.getDteTransDate();
			 String POSDate=req.getSession().getAttribute("gPOSDate").toString();
			// String DateFrom=Date.split("-")[2]+"-"+Date.split("-")[1]+"-"+Date.split("-")[0];
		
			Map mapCashManagementData=new HashMap();
			mapCashManagementData.put("strTransID", objBean.getStrTransID());
			mapCashManagementData.put("strTransType", objBean.getStrTransType());
			mapCashManagementData.put("strTransDate", POSDate);
			mapCashManagementData.put("strAmount", objBean.getDblAmount());
			mapCashManagementData.put("strReaSonCode", objBean.getStrReasonCode());
			mapCashManagementData.put("strCurrencyType", objBean.getStrCurrencyType());
			mapCashManagementData.put("strRemarks", objBean.getStrRemarks());
			mapCashManagementData.put("User", webStockUserCode);
			mapCashManagementData.put("ClientCode", clientCode);
			mapCashManagementData.put("posCode", posCode);
			mapCashManagementData.put("dteDateCreated", objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			mapCashManagementData.put("dteDateEdited", objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			System.out.println("omg="+mapCashManagementData);
			String strCashManagementCode=funSavePOSCashManagmentTranscation(mapCashManagementData);
			String res[]=strCashManagementCode.split("#");
			if(res[1].equals("True"))
			{
				 req.getSession().setAttribute("success", true);
			     req.getSession().setAttribute("successMessage"," "+res[0]); 
			     return new ModelAndView("redirect:/frmPOSCashManagement.html?saddr="+urlHits);	
			}
			else
			{
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+res[0]); 
			    return new ModelAndView("redirect:/frmPOSCashManagement.html?saddr="+urlHits);
			}
	       
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	
	}
	
	
	public String funSavePOSCashManagmentTranscation(Map mapCashManagementData){

		String against="Direct";
		boolean flg=true;
		double balanceAmt=0;
		double rollingAmount=0;
		double rollingAmt=0;		  
		String strCashManagementCode = "";
		try
		{
			   
			strCashManagementCode = mapCashManagementData.get("strTransID").toString();
		    String strTransType = mapCashManagementData.get("strTransType").toString();
		    String strTransDate = mapCashManagementData.get("strTransDate").toString();
		    double dblAmount = Double.parseDouble(mapCashManagementData.get("strAmount").toString());
		    String strReasonCode = mapCashManagementData.get("strReaSonCode").toString();
		    String strCurrencyType = mapCashManagementData.get("strCurrencyType").toString();
		    String strRemarks = mapCashManagementData.get("strRemarks").toString();
		    String user = mapCashManagementData.get("User").toString();
		    String posCode = mapCashManagementData.get("posCode").toString();
		    String clientCode = mapCashManagementData.get("ClientCode").toString();
		    String dateTime = objGlobal.funGetCurrentDateTime("yyyy-MM-dd");
		    strCashManagementCode="0000";
		
		    if(strTransType.equalsIgnoreCase("Rolling"))
		    {
		        
                    if(funCheckUserEntryForRolling(user,strTransDate))
                    {       
                      flg=false;  
                    }
                 
                    Map<String,clsPOSCashManagementDtlBean> hmCashMgmtDtl=funGetCashManagement(strTransDate.split(" ")[0], strTransDate.split(" ")[0],posCode);
                    balanceAmt=funGetBalanceUserWise(strTransDate.split(" ")[0], strTransDate.split(" ")[0], hmCashMgmtDtl, user);  
                    dblAmount=((balanceAmt- Double.parseDouble(mapCashManagementData.get("strAmount").toString())));
                    strTransType="Withdrawl";
                    against="Rolling";
                    rollingAmt= Double.parseDouble(mapCashManagementData.get("strAmount").toString());
                    rollingAmount= Double.parseDouble(mapCashManagementData.get("strAmount").toString());
		    }
		    
		    if(flg)
		    {
		      
				    strCashManagementCode =funGenerateCashManagementCode();
			    	clsPOSCashManagmentTranscationModel objModel = new clsPOSCashManagmentTranscationModel(new clsPOSCashManagmentTranscationModel_ID(strCashManagementCode,clientCode));
			        objModel.setStrTransType(strTransType);
				    objModel.setDteTransDate(strTransDate+" "+dateTime.split(" ")[1]);
				    objModel.setDblAmount(dblAmount);
				    objModel.setStrReasonCode(strReasonCode);
				    objModel.setStrCurrencyType(strCurrencyType);
				    objModel.setStrRemarks(strRemarks);
			        objModel.setStrClientCode(clientCode);
				    objModel.setStrUserCreated(user);
				    objModel.setStrUserEdited(user);
				    objModel.setDteDateCreated(dateTime);
				    objModel.setDteDateEdited(dateTime);
				    objModel.setStrDataPostFlag("N");
				    objModel.setStrAgainst(against);
				    objModel.setStrPOSCode(posCode);
				    objModel.setDblRollingAmt(rollingAmount);
			        strCashManagementCode =funSavePOSCashManagmentTranscation(objModel);
			        strCashManagementCode=strCashManagementCode+"#True";
		    }
		    else
		    {
		    	strCashManagementCode="This user has already entered rolling amount"+"#False";;
		    }
		}
		    
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return strCashManagementCode;

	}
	
	
	
	
	public String funSavePOSCashManagmentTranscation(clsPOSCashManagmentTranscationModel objModel) throws Exception
	{
		objBaseService.funSave(objModel);
		return objModel.getStrTransID();
	}
	
	public String funGenerateCashManagementCode() throws Exception
    {
	    StringBuilder sql = new StringBuilder();
		String customerAreaCode = "";
		sql.append("select ifnull(max(strTransID),0) from tblcashmanagement");
		List list = objBaseService.funGetList(sql, "sql");
		if (!list.get(0).toString().equals("0"))
		{
		    String strCode = "00";
		    String code = list.get(0).toString();
		    StringBuilder sb = new StringBuilder(code);
		    String ss = sb.delete(0, 2).toString();
		    for (int i = 0; i < ss.length(); i++)
		    {
				if (ss.charAt(i) != '0')
				{
				    strCode = ss.substring(i, ss.length());
				    break;
				}
		    }
		    
		    int intCode = Integer.parseInt(strCode);
		    intCode++;
		    if (intCode < 10)
		    {
		    	customerAreaCode = "TR0000" + intCode;
		    }
		    else if (intCode < 100)
		    {
		    	customerAreaCode = "TR000" + intCode;
		    }
		    else if (intCode < 1000)
		    {
		    	customerAreaCode = "TR00" + intCode;
		    }
		    else if (intCode < 10000)
		    {
		    	customerAreaCode = "TR0" + intCode;
		    }
					   
		   
		}
		else
		{
			customerAreaCode = "TR00001";
		}
		return customerAreaCode;
    }
	
	 public boolean funCheckUserEntryForRolling(String User, String Date) throws Exception
	 {
		StringBuilder sql = new StringBuilder();
		boolean flgResult=false;
		sql.append("select strTransID from tblcashmanagement "
            + " where strUserCreated='"+User+"' and strAgainst='Rolling' "
            + "and date(dteTransDate)='"+Date+"'");
		List list = objBaseService.funGetList(sql, "sql");
        if(list.size()>0)
        {
            flgResult=true;
            
        }
        return flgResult;
	 }
	 
	public Map<String,clsPOSCashManagementDtlBean> funGetCashManagement(String fromDate,String toDate, String POSCode) throws Exception
    {
	   StringBuilder sql = new StringBuilder();
	   Map<String,clsPOSCashManagementDtlBean> hmCashMgmtDtl=new HashMap<String,clsPOSCashManagementDtlBean>();
        StringBuilder sbSql=new StringBuilder();
        sbSql.setLength(0);
        
        StringBuilder sbSqlSale=new StringBuilder();
        Set<String> setUsers=new HashSet<String>();
        sbSqlSale.setLength(0);
        sbSqlSale.append("select time(dteTransDate),a.strUserEdited "
            + " from tblcashmanagement a "
            + " where date(a.dteTransDate) between '"+fromDate+"' and '"+toDate+"' and a.strAgainst='Rolling' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " order by a.strUserEdited ");
        List list = objBaseService.funGetList(sbSqlSale, "sql");
        for(int i=0 ;i<list.size();i++ )
    	{
        	Object[] obj = (Object[]) list.get(i);
            setUsers.add(obj[1].toString());
           
            sbSqlSale.setLength(0);
            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
                + " and time(a.dteBillDate) <  '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
                + " and a.strPOSCode='"+POSCode+"' "
                + " group by a.strUserEdited");
           
            List listrsSalesAmt = objBaseService.funGetList(sbSqlSale, "sql");
            for(int i1=0 ;i1<listrsSalesAmt.size();i1++ )
 	    	{
	        	Object[] obj1 = (Object[]) listrsSalesAmt.get(i1);
	        	
	        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0].toString());
                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
                }
            }
      


            sbSqlSale.setLength(0);
            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
                + " and time(a.dteBillDate) < '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
                + " and a.strPOSCode='"+POSCode+"' "
                + " group by a.strUserEdited");
           
            List listrsSalesAmt1 =objBaseService.funGetList(sbSqlSale, "sql");
            for(int i1=0 ;i1<listrsSalesAmt1.size();i1++ )
 	    	{
	        	Object[] obj1 = (Object[]) listrsSalesAmt1.get(i1);
	        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
            }
         
            
            Map<String,Double> hmPostRollingSalesAmt=null;
            sbSqlSale.setLength(0);
            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
                + " and time(a.dteBillDate) > '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
                + " and a.strPOSCode='"+POSCode+"' "
                + " group by a.strUserEdited");
           
            List listrsSalesAmt2 = objBaseService.funGetList(sbSqlSale, "sql");
            for(int i1=0 ;i1<listrsSalesAmt2.size();i1++ )
 	    	{
	        	Object[] obj1 = (Object[]) listrsSalesAmt2.get(i1);
	        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
                    hmPostRollingSalesAmt=new HashMap<String,Double>();
                    if(hmPostRollingSalesAmt.containsKey(obj1[1]))
                    {
                        hmPostRollingSalesAmt.put(obj[0].toString(),hmPostRollingSalesAmt.get(obj[0])+Double.parseDouble(obj1[1].toString()));
                    }
                    else
                    {
                        hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
                    }
                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    hmPostRollingSalesAmt=new HashMap<String,Double>();
                    hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
            }
           


            sbSqlSale.setLength(0);
            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
                + " and time(a.dteBillDate) > '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
                + " and a.strPOSCode='"+POSCode+"' "
                + " group by a.strUserEdited");
            
            List listrsSalesAmt3 = objBaseService.funGetList(sbSqlSale, "sql");
            for(int i1=0 ;i1<listrsSalesAmt3.size();i1++ )
 	    	{
	        	Object[] obj1 = (Object[]) listrsSalesAmt3.get(i1);
	        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
                    hmPostRollingSalesAmt=new HashMap<String,Double>();
                    if(hmPostRollingSalesAmt.containsKey(obj[0]))
                    {
                        hmPostRollingSalesAmt.put(obj[0].toString(),hmPostRollingSalesAmt.get(obj1[0])+Double.parseDouble(obj1[1].toString()));
                    }
                    else
                    {
                        hmPostRollingSalesAmt.put(obj1[0].toString(),Double.parseDouble(obj1[1].toString()));
                    }
                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    hmPostRollingSalesAmt=new HashMap<String,Double>();
                    hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
            }
            
        }
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
            + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
            + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
            + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " group by a.strUserEdited");
       
        List listrsSalesAmt3 = objBaseService.funGetList(sbSqlSale, "sql");
        for(int i1=0 ;i1<listrsSalesAmt3.size();i1++ )
    	{
        	Object[] obj1 = (Object[]) listrsSalesAmt3.get(i1);
            
        	String user=obj1[0].toString();
            if(!setUsers.contains(user))
            {
            	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
            }
        }
   


        sbSqlSale.setLength(0);
        sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
            + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
            + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
            + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " group by a.strUserEdited");
        
        List listrsSalesAmt4 = objBaseService.funGetList(sbSqlSale, "sql");
        for(int i1=0 ;i1<listrsSalesAmt4.size();i1++ )
    	{
        	Object[] obj1 = (Object[]) listrsSalesAmt4.get(i1);
            String user=obj1[0].toString();
            if(!setUsers.contains(user))
            {
            	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
                if(hmCashMgmtDtl.containsKey(obj1[0]))
                {
                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
                }
                else
                {
                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
                    hmCashMgmtDtl.put(obj1[1].toString(),objCashMgmtDtl);
                }
            }
        }

       
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tbladvancereceipthd "
            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
            + " group by strUserEdited ");
        List listrsAdvAmt = objBaseService.funGetList(sbSqlSale, "sql");
        for(int i1=0 ;i1<listrsAdvAmt.size();i1++ )
    	{
        	Object[] obj = (Object[]) listrsAdvAmt.get(i1);
        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
            if(hmCashMgmtDtl.containsKey(obj[0]))
            {
                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(obj[1].toString()));
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
            else
            {
                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(obj[1].toString()));
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
        }
       
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tblqadvancereceipthd "
            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
            + " group by strUserEdited ");
        List listrsAdvAmt1 = objBaseService.funGetList(sbSqlSale, "sql");
        for(int i1=0 ;i1<listrsAdvAmt1.size();i1++ )
    	{
        	Object[] obj = (Object[]) listrsAdvAmt1.get(i1);
        	clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
            if(hmCashMgmtDtl.containsKey(obj[0]))
            {
                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(obj[1].toString()));
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
            else
            {
                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(obj[1].toString()));
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
        }
       
        
        
        sbSql.setLength(0);
        sbSql.append("select strUserEdited,strTransType,sum(dblAmount),sum(dblRollingAmt) "
            + " from tblcashmanagement "
            + " where date(dteTransDate) between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"'  "
            + " group by strUserEdited,strTransType "
            + " order by strTransType");
        
        List listrsCashMgmt = objBaseService.funGetList(sbSql, "sql");
        for(int i1=0 ;i1<listrsCashMgmt.size();i1++ )
    	{
        	Object[] obj = (Object[]) listrsCashMgmt.get(i1);
            double balanceAmt=0;
            clsPOSCashManagementDtlBean objCashMgmtDtl=new clsPOSCashManagementDtlBean();
            if(hmCashMgmtDtl.containsKey(obj[0]))
            {
                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
                balanceAmt+=objCashMgmtDtl.getSaleAmt();
                balanceAmt+=objCashMgmtDtl.getAdvanceAmt();
                
                Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
                if(null!=hmPostRollingSalesAmt)
                {
                    for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
                    {
                        balanceAmt+=entry.getValue();
                    }
                }

                if(((String) obj[1]).equalsIgnoreCase("Float"))
                {
                    objCashMgmtDtl.setFloatAmt(objCashMgmtDtl.getFloatAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Withdrawl"))
                {
                    objCashMgmtDtl.setWithdrawlAmt(objCashMgmtDtl.getWithdrawlAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
                    objCashMgmtDtl.setRollingAmt(objCashMgmtDtl.getRollingAmt()+Double.parseDouble(obj[3].toString()));
                }
                else if(obj[1].toString().equalsIgnoreCase("Refund"))
                {
                    objCashMgmtDtl.setRefundAmt(objCashMgmtDtl.getRefundAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Payments"))
                {
                    objCashMgmtDtl.setPaymentAmt(objCashMgmtDtl.getPaymentAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Transfer In"))
                {
                    objCashMgmtDtl.setTransferInAmt(objCashMgmtDtl.getTransferInAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Transfer Out"))
                {
                    objCashMgmtDtl.setTransferOutAmt(objCashMgmtDtl.getTransferOutAmt()+Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
                }
                objCashMgmtDtl.setBalanceAmt(balanceAmt);
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
            else
            {
                objCashMgmtDtl.setFloatAmt(0);
                objCashMgmtDtl.setWithdrawlAmt(0);
                objCashMgmtDtl.setRollingAmt(0);
                objCashMgmtDtl.setTransferInAmt(0);
                objCashMgmtDtl.setTransferOutAmt(0);
                objCashMgmtDtl.setPaymentAmt(0);
                objCashMgmtDtl.setRefundAmt(0);
                objCashMgmtDtl.setBalanceAmt(0);
                objCashMgmtDtl.setSaleAmt(0);
                balanceAmt+=objCashMgmtDtl.getSaleAmt();
                balanceAmt+=objCashMgmtDtl.getAdvanceAmt();
                Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
                
                if(null!=hmPostRollingSalesAmt)
                {
                    for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
                    {
                        balanceAmt+=entry.getValue();
                    }
                }

                if(obj[1].toString().equalsIgnoreCase("Float"))
                {
                    objCashMgmtDtl.setFloatAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Withdrawl"))
                {
                    objCashMgmtDtl.setWithdrawlAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
                    objCashMgmtDtl.setRollingAmt(Double.parseDouble(obj[3].toString()));
                }
                else if(obj[1].toString().equalsIgnoreCase("Refund"))
                {
                    objCashMgmtDtl.setRefundAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Payments"))
                {
                    objCashMgmtDtl.setPaymentAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Transfer In"))
                {
                    objCashMgmtDtl.setTransferInAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
                }
                else if(obj[1].toString().equalsIgnoreCase("Transfer Out"))
                {
                    objCashMgmtDtl.setTransferOutAmt(Double.parseDouble(obj[2].toString()));
                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
                }
                objCashMgmtDtl.setBalanceAmt(balanceAmt);
                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
            }
        }
       
        
        return hmCashMgmtDtl;
	}
	 
	  public double funGetBalanceUserWise(String fromDate,String toDate,Map<String,clsPOSCashManagementDtlBean> hmCashMgmtDtl,String userCode) throws Exception
	    {
		     double balanceAmt=0;
		        if(hmCashMgmtDtl.containsKey(userCode))
		        {
		        	clsPOSCashManagementDtlBean objCashMgmtDtl=hmCashMgmtDtl.get(userCode);
		            balanceAmt=(objCashMgmtDtl.getSaleAmt()+objCashMgmtDtl.getAdvanceAmt()+objCashMgmtDtl.getFloatAmt()+objCashMgmtDtl.getTransferInAmt())-(objCashMgmtDtl.getWithdrawlAmt()+objCashMgmtDtl.getPaymentAmt()+objCashMgmtDtl.getRefundAmt()+objCashMgmtDtl.getTransferOutAmt());
		            Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
		            if(null!=hmPostRollingSalesAmt)
		            {
		                for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
		                {
		                    balanceAmt+=entry.getValue();
		                }
		            }
		        }
		        return balanceAmt;
		  
	    }
	  
	  
	  public List<clsReasonMasterModel> funGetReasonList()throws Exception
	  {
		  StringBuilder sql = new StringBuilder();
		  List<clsReasonMasterModel> listReason=new ArrayList();
		  sql.append("select strReasonCode,strReasonName from tblreasonmaster where strCashMgmt='Y'");
          List list = objBaseService.funGetList(sql, "sql");
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
