package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsExportToExcel;



@Controller
public class clsPOSCashManagementFlashController 
{
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	clsBaseServiceImpl objBaseService;
	
	@Autowired
	private clsExportToExcel objExportToExcel;
	
	Map mapPOS=new TreeMap();
	
	@RequestMapping(value = "/frmPOSCashMgmtReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request, Object objMasterService)throws Exception
	{
		String urlHits="1";
		String POSCode=request.getSession().getAttribute("loginPOS").toString();
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		List list=this.objMasterService.funFillPOSCombo(clientCode);
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			mapPOS.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
		}
		model.put("posList",mapPOS);
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		Map mapAmount = new HashMap<>();
		
		mapAmount.put("<=", "<=");
		mapAmount.put(">=", ">=");
		mapAmount.put("=", "=");
		model.put("mapAmount",mapAmount);
		
		Map mapReportType = new TreeMap();
		
		mapReportType.put("Detail", "Detail");
		mapReportType.put("Summary", "Summary");
		
		model.put("mapReportType",mapReportType);
		
		Map mapTransType = new TreeMap();
		mapTransType.put("All", "All");
		mapTransType.put("Transfer In", "Transfer In");
		mapTransType.put("Float", "Float");
		mapTransType.put("Refund", "Refund");
		mapTransType.put("Withdrawal", "Withdrawal");
		mapTransType.put("Payments", "Payments");
		mapTransType.put("Transfer Out", "Transfer Out");
		model.put("mapTransType",mapTransType);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCashMgmtReport_1","command", new clsPOSCashManagementDtlBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCashMgmtReport","command", new clsPOSCashManagementDtlBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getCashManagementDetailReport", method = RequestMethod.GET)	
	private @ResponseBody Map funGetCashManagemntDetlReport(HttpServletResponse resp,HttpServletRequest req)
	{
		Map hmCashMngmtDtl=new HashMap();
		try{
			
			String fromDate= req.getParameter("fromDate").toString();
			String toDate= req.getParameter("toDate").toString();
			String strReportType= req.getParameter("strReportType").toString();
			String posCode= req.getParameter("posCode").toString();
			String transType= req.getParameter("transType").toString();
			hmCashMngmtDtl=funCashManagementFlashForDetail(fromDate,toDate,strReportType,posCode,transType);
		}
		catch(Exception e){
			
		}
		return hmCashMngmtDtl;
	}
	
	
	
	
	
	 /**
     * this Function is used for billWiseReport
     */
    private Map funCashManagementFlashForDetail(String frmDate,String toDte,String strReportType,String posCode,String transType)
    {
      Map hmCashMngmtDtl=new HashMap();
	    
     try
	 {
    	String fromDate= frmDate.split("-")[2]+"-"+frmDate.split("-")[1]+"-"+frmDate.split("-")[0];
    	String toDate= toDte.split("-")[2]+"-"+toDte.split("-")[1]+"-"+toDte.split("-")[0];
        String rollingEntryTime = "";
	    boolean flgPostRollingSales = false;
	    Map<String, clsTempSalesAmt> hmCashSalesAmt = new HashMap<String, clsTempSalesAmt>();
	    double amount = 0;
	    StringBuilder sbSqlSale = new StringBuilder();
	    sbSqlSale.setLength(0);
	    sbSqlSale.append("select a.strTransType,time(a.dteTransDate) "
		    + " from tblcashmanagement a "
		    + " where date(a.dteTransDate) between '" + fromDate + "' and '" + toDate + "' "
		    + " and a.strAgainst='Rolling' "
		    + " and a.strPOSCode='" + posCode + "' "
		    + " group by time(a.dteTransDate)");
	    
	    List listRollingEntry=objBaseService.funGetList(sbSqlSale, "sql");
	    if(listRollingEntry!=null && listRollingEntry.size()>0)
	    {
  	       for(int j=0;j<listRollingEntry.size();j++)
  	       {
  		     Object[] obj = (Object[] ) listRollingEntry.get(j);
  		     flgPostRollingSales = true;
			 rollingEntryTime = obj[1].toString();
			 sbSqlSale.setLength(0);
			 sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
				+ " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
				+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
				+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and a.strPOSCode='" + posCode + "' "
				+ " and time(a.dteBillDate) < '" + obj[1].toString() + "' "
				+ " group by a.strUserEdited");
			 
			 List listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
			 if(listSaleAmt.size()>0)
			    {
		  	       for(int cnt=0;cnt<listSaleAmt.size();cnt++)
		  	       {
		  		     Object[] objSale = (Object[] ) listSaleAmt.get(cnt);
		  		     clsTempSalesAmt objCashSales = new clsTempSalesAmt();
				     objCashSales.setUserCode(objSale[0].toString());
				     objCashSales.setTransType("Sale");
				     objCashSales.setDate(objSale[2].toString().split("-")[2] + "-" + objSale[2].toString().split("-")[1] + "-" + objSale[2].toString().split("-")[0]);
				     objCashSales.setPOS(objSale[3].toString());
				     objCashSales.setReason("");
				     objCashSales.setRemarks("");
				     objCashSales.setAmount(Double.parseDouble(objSale[1].toString()));
				     hmCashSalesAmt.put(objSale[0].toString(), objCashSales);
		  	       }
			    }
			   
			   
			   sbSqlSale.setLength(0);
			   sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
					+ " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
					+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
					+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
					+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " and a.strPOSCode='" + posCode + "' "
					+ " and time(a.dteBillDate) < '" + obj[1].toString() + "' "
					+ " group by a.strUserEdited");
			   
			   listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
			   if(listSaleAmt.size()>0)
			    {
		  	       for(int cnt=0;cnt<listSaleAmt.size();cnt++)
		  	       {
		  		        Object[] objSale = (Object[] ) listSaleAmt.get(cnt);
		  		    
			  		    clsTempSalesAmt objCashSales = new clsTempSalesAmt();
					    if (hmCashSalesAmt.containsKey(objSale[0].toString()))
					    {
							objCashSales = hmCashSalesAmt.get(objSale[0].toString());
							objCashSales.setAmount(objCashSales.getAmount() + Double.parseDouble(objSale[1].toString()));
							hmCashSalesAmt.put(objSale[0].toString(), objCashSales);
					    }
					    else
					    {
							objCashSales.setUserCode(objSale[0].toString());
							objCashSales.setTransType("Sale");
							objCashSales.setDate(objSale[2].toString().split("-")[2] + "-" + objSale[2].toString().split("-")[1] + "-" + objSale[2].toString().split("-")[0]);
							objCashSales.setPOS(objSale[3].toString());
							objCashSales.setReason("");
							objCashSales.setRemarks("");
							objCashSales.setAmount(Double.parseDouble(objSale[1].toString()));
							hmCashSalesAmt.put(objSale[0].toString(), objCashSales);
				       }
		  	       }
			    }
			 
            }
	     }
	    else
	    {
	    	sbSqlSale.setLength(0);
			sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
				+ " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
				+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
				+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and a.strPOSCode='" + posCode + "' "
				+ " group by a.strUserEdited");
			
			 List listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
		     if(listSaleAmt!=null && listSaleAmt.size()>0)
			    {
		  	       for(int j=0;j<listSaleAmt.size();j++)
		  	       {
		  		     Object[] objSale = (Object[] ) listSaleAmt.get(j);
		  		     clsTempSalesAmt objCashSales = new clsTempSalesAmt();
				     objCashSales.setUserCode(objSale[0].toString());
				     objCashSales.setTransType("Sale");
				     objCashSales.setDate(objSale[2].toString().split("-")[2] + "-" + objSale[2].toString().split("-")[1] + "-" +objSale[2].toString().split("-")[0]);
				     objCashSales.setPOS(objSale[3].toString());
				     objCashSales.setReason("");
				     objCashSales.setRemarks("");
				     objCashSales.setAmount(Double.parseDouble(objSale[1].toString()));
				     hmCashSalesAmt.put(objSale[0].toString(), objCashSales);
		  	       }
			    }
		     
		     
		     sbSqlSale.setLength(0);
			 sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
					+ " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
					+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
					+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
					+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " and a.strPOSCode='" + posCode + "' "
					+ " group by a.strUserEdited"); 
			 
			 
			 listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
			   if(listSaleAmt.size()>0)
			    {
		  	       for(int cnt=0;cnt<listSaleAmt.size();cnt++)
		  	       {
		  		        Object[] objSale = (Object[] ) listSaleAmt.get(cnt);
		  		        
		  		        clsTempSalesAmt objCashSales = new clsTempSalesAmt();
			  		    if (hmCashSalesAmt.containsKey(objSale[0].toString()))
			  		    {
			  			objCashSales = hmCashSalesAmt.get(objSale[0].toString());
			  			objCashSales.setAmount(objCashSales.getAmount() + Double.parseDouble(objSale[1].toString()));
			  			hmCashSalesAmt.put(objSale[1].toString(), objCashSales);
			  		    }
			  		    else
			  		    {
			  			objCashSales.setUserCode(objSale[0].toString());
			  			objCashSales.setTransType("Sale");
			  			objCashSales.setDate(objSale[2].toString().split("-")[2] + "-" + objSale[2].toString().split("-")[1] + "-" + objSale[2].toString().split("-")[0]);
			  			objCashSales.setPOS(objSale[3].toString());
			  			objCashSales.setReason("");
			  			objCashSales.setRemarks("");
			  			objCashSales.setAmount(Double.parseDouble(objSale[1].toString()));
			  			hmCashSalesAmt.put(objSale[0].toString(), objCashSales);
		  		    }
		  	       }
			 }
	    	
	    }
	    
	    
	    List<clsPOSCashManagementDtlBean> listCashMgmtDtl=new ArrayList();
	    for (Map.Entry<String, clsTempSalesAmt> entry : hmCashSalesAmt.entrySet())
	    {
	    	clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
	    	objBean.setUserCode(entry.getValue().getUserCode());//userCode
	    	objBean.setPosCode(entry.getValue().getTransType());//transType
	    	objBean.setFromDate(entry.getValue().getDate());    //date
	    	objBean.setPosName(entry.getValue().getPOS());      //posName
	    	objBean.setToDate(entry.getValue().getReason());    //reason
	    	objBean.setUserName(entry.getValue().getRemarks()); //remark
	    	objBean.setBalanceAmt(entry.getValue().getAmount()); //amount
	    	
		    amount += entry.getValue().getAmount();
		    listCashMgmtDtl.add(objBean);
	    }

	    sbSqlSale.setLength(0);
	    sbSqlSale.append("select a.strUserEdited,sum(a.dblAdvDeposite),a.dtReceiptDate,b.strPOSName  "
		    + " from tbladvancereceipthd a,tblposmaster b "
		    + " where a.strPOSCode=b.strPOSCode "
		    + " and a.dtReceiptDate between '" + fromDate + "' and '" + toDate + "' "
		    + " and a.strPOSCode='" + posCode + "' "
		    + " group by a.strUserEdited ");
	    
	    List listAdvAmt=objBaseService.funGetList(sbSqlSale, "sql");
	    if(listAdvAmt.size()>0)
	     {
  	       for(int cnt=0;cnt<listAdvAmt.size();cnt++)
  	       {
  		        Object[] objAdvAmt = (Object[] ) listAdvAmt.get(cnt);
  		        clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
		    	objBean.setUserCode(objAdvAmt[0].toString());//userCode
		    	objBean.setPosCode("Advance");//transType
		    	objBean.setFromDate(objAdvAmt[2].toString().split("-")[2] + "-" + objAdvAmt[2].toString().split("-")[1] + "-" + objAdvAmt[2].toString().split("-")[0]);    //date
		    	objBean.setPosName(objAdvAmt[3].toString());      //posName
		    	objBean.setToDate("");    //reason
		    	objBean.setUserName(""); //remark
		    	objBean.setBalanceAmt(Double.valueOf(objAdvAmt[1].toString())); //amount
		    	
			    amount += Double.valueOf(objAdvAmt[1].toString());
			    listCashMgmtDtl.add(objBean);
  	       }
	     }
	  
	    

	    sbSqlSale.setLength(0);
	    sbSqlSale.append("select a.strUserEdited,sum(a.dblAdvDeposite),a.dtReceiptDate,b.strPOSName  "
		    + " from tblqadvancereceipthd a,tblposmaster b "
		    + " where a.strPOSCode=b.strPOSCode "
		    + " and a.dtReceiptDate between '" + fromDate + "' and '" + toDate + "' "
		    + " and a.strPOSCode='" + posCode + "' "
		    + " group by a.strUserEdited ");
	    listAdvAmt=objBaseService.funGetList(sbSqlSale, "sql");
	    if(listAdvAmt.size()>0)
	     {
  	       for(int cnt=0;cnt<listAdvAmt.size();cnt++)
  	       {
  		        Object[] objAdvAmt = (Object[] ) listAdvAmt.get(cnt);
  		        clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
		    	objBean.setUserCode(objAdvAmt[0].toString());//userCode
		    	objBean.setPosCode("Advance");//transType
		    	objBean.setFromDate(objAdvAmt[2].toString().split("-")[2] + "-" + objAdvAmt[2].toString().split("-")[1] + "-" + objAdvAmt[2].toString().split("-")[0]);    //date
		    	objBean.setPosName(objAdvAmt[3].toString());      //posName
		    	objBean.setToDate("");    //reason
		    	objBean.setUserName(""); //remark
		    	objBean.setBalanceAmt(Double.valueOf(objAdvAmt[1].toString())); //amount
		    	
			    amount += Double.valueOf(objAdvAmt[1].toString());
			    listCashMgmtDtl.add(objBean);
  	       }
	     }

	    if (transType.equals("All"))
	    {
		sbSqlSale.setLength(0);
		sbSqlSale.append("select a.strUserEdited,a.strTransType,date(a.dteTransDate),ifnull(c.strPosName,'All')"
			+ " ,b.strReasonName,a.strRemarks,a.dblAmount "
			+ " from tblcashmanagement a inner join tblreasonmaster b on a.strReasonCode=b.strReasonCode "
			+ " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
			+ " where date(a.dteTransDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strPOSCode='" + posCode + "' "
			+ " order by a.strUserEdited,a.strTransType ");
	    }
	    else
	    {
		sbSqlSale.setLength(0);
		sbSqlSale.append("select a.strUserEdited,a.strTransType,date(a.dteTransDate),ifnull(c.strPosName,'All')"
			+ " ,b.strReasonName,a.strRemarks,a.dblAmount "
			+ " from tblcashmanagement a inner join tblreasonmaster b on a.strReasonCode=b.strReasonCode "
			+ " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
			+ " where date(a.dteTransDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strPOSCode='" + posCode + "' "
			+ " and a.strTransType='" + transType + "' "
			+ " order by a.strUserEdited,a.strTransType ");
	    }
	   List listCashMgmnt=objBaseService.funGetList(sbSqlSale, "sql");
	    if(listCashMgmnt.size()>0)
	     {
  	       for(int cnt=0;cnt<listCashMgmnt.size();cnt++)
  	       {
  		        Object[] objCashMgmt = (Object[] ) listCashMgmnt.get(cnt);
  		        clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
		    	objBean.setUserCode(objCashMgmt[0].toString());//userCode
		    	objBean.setPosCode(objCashMgmt[1].toString());//transType
		    	objBean.setFromDate(objCashMgmt[2].toString().split("-")[2] + "-" + objCashMgmt[2].toString().split("-")[1] + "-" + objCashMgmt[2].toString().split("-")[0]);    //date
		    	objBean.setPosName(objCashMgmt[3].toString());      //posName
		    	objBean.setToDate(objCashMgmt[4].toString());    //reason
		    	objBean.setUserName(objCashMgmt[5].toString()); //remark
		    	objBean.setBalanceAmt(Double.valueOf(objCashMgmt[6].toString())); //amount
		    	
			    amount += Double.valueOf(objCashMgmt[6].toString());
			    String transactionType = objCashMgmt[1].toString();
				if (transactionType.equalsIgnoreCase("Float"))
				{
				    amount += Double.parseDouble(objCashMgmt[6].toString());
				}
				else if (transactionType.equalsIgnoreCase("Transfer In"))
				{
				    amount += Double.parseDouble(objCashMgmt[6].toString());
				}
				else
				{
				    amount -= Double.parseDouble(objCashMgmt[6].toString());
				}
				
				listCashMgmtDtl.add(objBean);
  	       }
	     }
	    
	    
	    if (flgPostRollingSales)
	    {
		  hmCashSalesAmt = new HashMap<String, clsTempSalesAmt>();
		  sbSqlSale.setLength(0);
		  sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
			+ " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
			+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
			+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
			+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strPOSCode='" + posCode + "' "
			+ " and time(a.dteBillDate) > '" + rollingEntryTime + "' "
			+ " group by a.strUserEdited");
		  
		  List listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
		    if(listSaleAmt.size()>0)
		     {
	  	       for(int cnt=0;cnt<listSaleAmt.size();cnt++)
	  	       {
	  		        Object[] objSaleAmt = (Object[] ) listSaleAmt.get(cnt);
	  		        clsTempSalesAmt objCashSales = new clsTempSalesAmt();
		  		    objCashSales.setUserCode(objSaleAmt[0].toString());
		  		    objCashSales.setTransType("Sale After Rolling");
		  		    objCashSales.setDate(objSaleAmt[2].toString().split("-")[2] + "-" + objSaleAmt[2].toString().split("-")[1] + "-" + objSaleAmt[2].toString().split("-")[0]);
		  		    objCashSales.setPOS(objSaleAmt[3].toString());
		  		    objCashSales.setReason("");
		  		    objCashSales.setRemarks("");
		  		    objCashSales.setAmount(Double.parseDouble(objSaleAmt[1].toString()));
		  		    hmCashSalesAmt.put(objSaleAmt[0].toString(), objCashSales);
	  	       }
		     }
		

		sbSqlSale.setLength(0);
		sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt),date(a.dteBillDate),d.strPOSName "
			+ " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
			+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
			+ " and a.strPOSCode=d.strPOSCode and c.strSettelmentType='Cash' "
			+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			+ " and a.strPOSCode='" + posCode + "' "
			+ " and time(a.dteBillDate) > '" + rollingEntryTime + "' "
			+ " group by a.strUserEdited");
		
		listSaleAmt=objBaseService.funGetList(sbSqlSale, "sql");
	    if(listSaleAmt.size()>0)
	     {
  	       for(int cnt=0;cnt<listSaleAmt.size();cnt++)
  	       {
  		        Object[] objSaleAmt = (Object[] ) listSaleAmt.get(cnt);
  		        clsTempSalesAmt objCashSales = new clsTempSalesAmt();
	  		    if (hmCashSalesAmt.containsKey(objSaleAmt[0].toString()))
	  		    {
	  			objCashSales = hmCashSalesAmt.get(objSaleAmt[0].toString());
	  			objCashSales.setAmount(objCashSales.getAmount() + Double.parseDouble(objSaleAmt[1].toString()));
	  			hmCashSalesAmt.put(objSaleAmt[0].toString(), objCashSales);
	  		    }
	  		    else
	  		    {
	  			objCashSales.setUserCode(objSaleAmt[0].toString());
	  			objCashSales.setTransType("Sale After Rolling");
	  			objCashSales.setDate(objSaleAmt[2].toString().split("-")[2] + "-" + objSaleAmt[2].toString().split("-")[1] + "-" + objSaleAmt[2].toString().split("-")[0]);
	  			objCashSales.setPOS(objSaleAmt[3].toString());
	  			objCashSales.setReason("");
	  			objCashSales.setRemarks("");
	  			objCashSales.setAmount(Double.parseDouble(objSaleAmt[1].toString()));
	  			hmCashSalesAmt.put(objSaleAmt[0].toString(), objCashSales);
	  		    }
  	       }
	     }
	    
		

		 for (Map.Entry<String, clsTempSalesAmt> entry : hmCashSalesAmt.entrySet())
		  {
		    clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
	    	objBean.setUserCode(entry.getValue().getUserCode());//userCode
	    	objBean.setPosCode(entry.getValue().getTransType());//transType
	    	objBean.setFromDate(entry.getValue().getDate());    //date
	    	objBean.setPosName(entry.getValue().getPOS());      //posName
	    	objBean.setToDate(entry.getValue().getReason());    //reason
	    	objBean.setUserName(entry.getValue().getRemarks()); //remark
	    	objBean.setBalanceAmt(entry.getValue().getAmount()); //amount
	    	
		    amount += entry.getValue().getAmount();
		    listCashMgmtDtl.add(objBean);
		  }
		
	    }
	    
	    
	    hmCashMngmtDtl.put("Total", amount);
	    hmCashMngmtDtl.put("listData", listCashMgmtDtl);
	    
	 }
	 catch(Exception e)
	 {
		e.printStackTrace();	  
	 }
    
      return hmCashMngmtDtl; 
    
  }   
    
    
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getCashManagementSummaryReport", method = RequestMethod.GET)	
	private @ResponseBody Map funGetCashManagemntSummaryReport(HttpServletResponse resp,HttpServletRequest req)
	{
		Map hmCashMngmtDtl=new HashMap();
		try{
			
			String fromDate= req.getParameter("fromDate").toString();
			String toDate= req.getParameter("toDate").toString();
			String strReportType= req.getParameter("strReportType").toString();
			String posCode= req.getParameter("posCode").toString();
			String transType= req.getParameter("transType").toString();
			hmCashMngmtDtl=funCashManagementFlashForSummary(fromDate,toDate,strReportType,posCode,transType);
		}
		catch(Exception e){
			
		}
		return hmCashMngmtDtl;
	}
	
	
	
	
	
	 /**
     * this Function is used for summary Report
     */
    private Map funCashManagementFlashForSummary(String frmDate,String toDte,String strReportType,String posCode,String transType)
    {
      Map hmCashMngmtDtl=new HashMap();
      List<clsPOSCashManagementDtlBean> listCashMgmtDtl=new ArrayList(); 
	     try
		 {
	    	String fromDate= frmDate.split("-")[2]+"-"+frmDate.split("-")[1]+"-"+frmDate.split("-")[0];
	    	String toDate= toDte.split("-")[2]+"-"+toDte.split("-")[1]+"-"+toDte.split("-")[0];
	        String rollingEntryTime = "";
		    Map<String, clsTempSalesAmt> hmCashSalesAmt = new HashMap<String, clsTempSalesAmt>();
		    double amount = 0;
		    StringBuilder sbSqlSale = new StringBuilder();
		  
		    if (transType.equals("All"))
		    {
			sbSqlSale.setLength(0);
			sbSqlSale.append("select a.strUserEdited,a.strTransType,date(a.dteTransDate),ifnull(c.strPosName,'All')"
				+ " ,sum(a.dblAmount) "
				+ " from tblcashmanagement a inner join tblreasonmaster b on a.strReasonCode=b.strReasonCode "
				+ " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
				+ " where date(a.dteTransDate) between '" + fromDate + "' and '" + toDate + "' and a.strPOSCode='" + posCode + "' "
				+ " group by a.strUserEdited,a.strTransType "
				+ " order by a.strUserEdited");
		    }
		    else
		    {
			sbSqlSale.setLength(0);
			sbSqlSale.append("select a.strUserEdited,a.strTransType,date(a.dteTransDate),ifnull(c.strPosName,'All')"
				+ " ,sum(a.dblAmount) "
				+ " from tblcashmanagement a inner join tblreasonmaster b on a.strReasonCode=b.strReasonCode "
				+ " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
				+ " where date(a.dteTransDate) between '" + fromDate + "' and '" + toDate + "' and a.strPOSCode='" + posCode + "' "
				+ " and a.strTransType='" + transType + "' "
				+ " group by a.strUserEdited,a.strTransType "
				+ " order by a.strUserEdited");
		    }
		   List listCashMgmnt=objBaseService.funGetList(sbSqlSale, "sql");
		    if(listCashMgmnt.size()>0)
		     {
	  	       for(int cnt=0;cnt<listCashMgmnt.size();cnt++)
	  	       {
	  		        Object[] objCashMgmt = (Object[] ) listCashMgmnt.get(cnt);
	  		        clsPOSCashManagementDtlBean objBean=new clsPOSCashManagementDtlBean();
			    	objBean.setUserCode(objCashMgmt[0].toString());//userCode
			    	objBean.setPosCode(objCashMgmt[1].toString());//transType
			    	objBean.setFromDate(objCashMgmt[2].toString().split("-")[2] + "-" + objCashMgmt[2].toString().split("-")[1] + "-" + objCashMgmt[2].toString().split("-")[0]);    //date
			    	objBean.setPosName(objCashMgmt[3].toString());      //posName
			    	objBean.setBalanceAmt(Double.valueOf(objCashMgmt[6].toString())); //amount
				    amount += Double.valueOf(objCashMgmt[6].toString());
					listCashMgmtDtl.add(objBean);
	  	       }
		     }
		    
		    hmCashMngmtDtl.put("Total", amount);
		    hmCashMngmtDtl.put("listData", listCashMgmtDtl);
		 }
		 catch(Exception e)
		 {
			e.printStackTrace();	  
		 }
      return hmCashMngmtDtl; 
    }  
    
    
    
  //rptPOSSalesReport
 	
 	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/exportCashManagement1", method = RequestMethod.POST)	
	public ModelAndView funReport(@ModelAttribute("command") clsPOSCashManagementDtlBean objBean, HttpServletResponse resp,HttpServletRequest req,String source) throws Exception
	{
 		List exportList=funGetReportData(req,objBean);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
	}
 	
 	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/exportCashManagement", method = RequestMethod.POST)	
	public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSCashManagementDtlBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
	{
 		List exportList=funGetReportData(req,objBean);
		objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
		return true;
	}
 	
 	private List funGetReportData(HttpServletRequest req, clsPOSCashManagementDtlBean objBean) throws Exception
	{
 		String fromDate= objBean.getFromDate();
		String toDate= objBean.getToDate();
		String strReportType= objBean.getUserCode();
		String posCode= objBean.getPosCode();
		String transType= objBean.getUserName();
 		
	    Map resMap = new LinkedHashMap();
		resMap=funGetData(fromDate,toDate,strReportType,posCode,transType);
        List exportList=new ArrayList();	
	
		String dteFromDate=objBean.getFromDate();
		String dteToDate=objBean.getToDate();
		String FileName="CashManagementFlash_"+dteFromDate+"_To_"+dteToDate;
	
		exportList.add(FileName);
		if(resMap!=null && resMap.size()>0)
		{
			List List=(List)resMap.get("ColHeader");
			
			String[] headerList = new String[List.size()];
			for(int i = 0; i < List.size(); i++){
				headerList[i]=(String)List.get(i);
			}
			
			exportList.add(headerList);
			
			List dataList=(List)resMap.get("listArr");
			for(int i=0;i<2;i++)
			{
				List list=new ArrayList();
				for(int j = 0; i < List.size(); i++)
				{
					list.add(" ");
				}
				dataList.add(list);
			}
			
			List totalList=(List)resMap.get("totalList");
			dataList.add(totalList);
			
			exportList.add(dataList);	
		}
		return  exportList;
	}
 	
 	private LinkedHashMap funGetData(String fromDate,String toDate,String strReportType,String posCode,String transType)
	{
 		  LinkedHashMap resMap = new LinkedHashMap();
		  List listArrColHeader = new ArrayList();
		  List totalList = new ArrayList();
		  List listArr = new ArrayList();
		  double amtTotal=0;
	      List colHeader = new ArrayList();
		      if(strReportType.equals("Detail"))
		      {
		    	  listArrColHeader.add("User");
		    	  listArrColHeader.add("Transaction Type");
		    	  listArrColHeader.add("Date");
		    	  listArrColHeader.add("POS");
		    	  listArrColHeader.add("Reason");
		    	  listArrColHeader.add("Remark");
		    	  listArrColHeader.add("Amount");
		    	  Map hmCashMngmtDtl=funCashManagementFlashForDetail(fromDate,toDate,strReportType,posCode,transType); 
		    	  List<clsPOSCashManagementDtlBean> listCashMgmtDtl=(List<clsPOSCashManagementDtlBean>) hmCashMngmtDtl.get("listData");
		    	  if(listCashMgmtDtl.size()>0)
		    	  {
		    		  
			    	  
		    		  for(int i=0;i<listCashMgmtDtl.size();i++)
						{
		    			  clsPOSCashManagementDtlBean objBillItemDtlBean =  (clsPOSCashManagementDtlBean) listCashMgmtDtl.get(i);
		    			  List arrList=new ArrayList();
						   arrList.add(objBillItemDtlBean.getUserCode());
		                   arrList.add(objBillItemDtlBean.getPosCode());
		                   arrList.add(objBillItemDtlBean.getFromDate());
		                   arrList.add(objBillItemDtlBean.getPosName());
		                   arrList.add(objBillItemDtlBean.getToDate());
		                   arrList.add(objBillItemDtlBean.getUserName());
		                   arrList.add(objBillItemDtlBean.getBalanceAmt());
		                   listArr.add(arrList);
						}
		    		  
		    		    totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add(hmCashMngmtDtl.get("Total"));
		    		 
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
						resMap.put("totalList", totalList);
		    	  }		
		      }
		      else
		      {
	    		  listArrColHeader.add("User");
		    	  listArrColHeader.add("Transaction Type");
		    	  listArrColHeader.add("Date");
		    	  listArrColHeader.add("POS");
		    	  listArrColHeader.add("Amount");
		    	  Map hmCashMngmtDtl=funCashManagementFlashForSummary(fromDate,toDate,strReportType,posCode,transType); 
		    	  List<clsPOSCashManagementDtlBean> listCashMgmtDtl=(List<clsPOSCashManagementDtlBean>) hmCashMngmtDtl.get("listData");
		    	  if(listCashMgmtDtl.size()>0)
		    	  {

		    		  for(int i=0;i<listCashMgmtDtl.size();i++)
						{
		    			   clsPOSCashManagementDtlBean objBillItemDtlBean =  (clsPOSCashManagementDtlBean) listCashMgmtDtl.get(i);
		    			   List arrList=new ArrayList();
						   arrList.add(objBillItemDtlBean.getUserCode());
		                   arrList.add(objBillItemDtlBean.getPosCode());
		                   arrList.add(objBillItemDtlBean.getFromDate());
		                   arrList.add(objBillItemDtlBean.getPosName());
		                   arrList.add(objBillItemDtlBean.getToDate());
		                   arrList.add(objBillItemDtlBean.getUserName());
		                   arrList.add(objBillItemDtlBean.getBalanceAmt());
		                   listArr.add(arrList);
		    			  
		    			  
						}
		    		  
		    		    totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add(hmCashMngmtDtl.get("Total"));
		    		 
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
						resMap.put("totalList", totalList);
		    	  }	
		      }
		     
		  return resMap;
	  }
   

}    
    
    
    class clsTempSalesAmt
    {

        private String userCode;

        private String transType;

        private String date;

        private String POS;

        private String reason;

        private String remarks;

        private double amount;

        public String getUserCode()
        {
    	return userCode;
        }

        public void setUserCode(String userCode)
        {
    	this.userCode = userCode;
        }

        public String getTransType()
        {
    	return transType;
        }

        public void setTransType(String transType)
        {
    	this.transType = transType;
        }

        public String getDate()
        {
    	return date;
        }

        public void setDate(String date)
        {
    	this.date = date;
        }

        public String getPOS()
        {
    	return POS;
        }

        public void setPOS(String POS)
        {
    	this.POS = POS;
        }

        public String getReason()
        {
    	return reason;
        }

        public void setReason(String reason)
        {
    	this.reason = reason;
        }

        public String getRemarks()
        {
    	return remarks;
        }

        public void setRemarks(String remarks)
        {
    	this.remarks = remarks;
        }

        public double getAmount()
        {
    	return amount;
        }

        public void setAmount(double amount)
        {
    	this.amount = amount;
        }
	
        
    }

	
	