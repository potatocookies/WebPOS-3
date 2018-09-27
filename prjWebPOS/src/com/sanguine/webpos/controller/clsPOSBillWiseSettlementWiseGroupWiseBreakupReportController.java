package com.sanguine.webpos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsManagerReportBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsPOSSendMail;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSBillWiseSettlementWiseGroupWiseBreakupReportController {

	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	

	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	@Autowired
	private clsPOSUtilityController objUtility;
	
	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;
	
	@Autowired
	clsPOSSendMail objSendMail;
	 
	Map<String,String> hmPOSData;
	 @RequestMapping(value = "/frmBillWiseSettlementWiseGroupWiseBreakup", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
		{
		 
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
			String POSCode=request.getSession().getAttribute("loginPOS").toString();	
			String urlHits="1";
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			List poslist = new ArrayList();
			poslist.add("ALL");
			
			hmPOSData=new HashMap<String, String>();
			List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					poslist.add( obj[1].toString());
					hmPOSData.put(obj[0].toString(),obj[1].toString());
				}
			}
			model.put("posList",poslist);
			
			 Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			 model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());
			
			String posDate = request.getSession().getAttribute("gPOSDate").toString();
			request.setAttribute("POSDate", posDate);
			 
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmBillWiseSettlementWiseGroupWiseBreakup_1","command", new clsPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmBillWiseSettlementWiseGroupWiseBreakup","command", new clsPOSReportBean());
			}else {
				return null;
			}
			 
		}
	 
	 
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptPOSBillWiseSettlementWiseGroupWiseBreakup", method = RequestMethod.POST)	
		private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			try
			{
				
				String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
				String POSCode=req.getSession().getAttribute("loginPOS").toString();
				Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
				String posCode = "ALL";
				posCode=objBean.getStrPOSName();
				hm.put("posCode", posCode);
				
				String fromDate = hm.get("fromDate").toString();
				String toDate = hm.get("toDate").toString();
				String strUserCode = hm.get("userName").toString();
				String strPOSCode = posCode;
				String strShiftNo = "ALL";
				Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
				if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
				{
					strShiftNo=objBean.getStrShiftCode();
				}
				hm.remove("shiftNo");
				hm.put("shiftNo", strShiftNo);
				List list = new ArrayList();
				list.add(1);
				objUtility.funCreateTempFolder();

			    String filePath = System.getProperty("user.dir");
			    File file = new File(filePath + File.separator + "Temp" + File.separator + "Bill Wise Settlement Wise Group Wise Breakup.txt");
			    PrintWriter pw = new PrintWriter(file);

			    String dashedLineOf150Chars = "------------------------------------------------------------------------------------------------------------------------------------------------------";

			    pw.println(hm.get("clientName"));
			    String gClientAddress2 = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,POSCode, "gClientAddress2");
			    String gClientAddress3 = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,POSCode, "gClientAddress3");
			    if (gClientAddress2.trim().length() > 0)
			    {
				pw.println(gClientAddress2);
			    }
			    if (gClientAddress3.trim().length() > 0)
			    {
				pw.println(gClientAddress3);
			    }
			    pw.println("Report : Bill Wise Settlement Wise Group Wise Tax Breakup");
			    //   pw.println("Reporting Date:" + "  " + fromDate + " " + "To" + " " + toDate);
			    pw.println();
				
				int ret = funBillWiseSettlementWiseGroupWiseBreakup(fromDate,toDate,posCode,pw);
				String printOS = "Windows";
				String printerType = "Inbuild";
				 pw.println();
				 pw.println();
				 if ("linux".equalsIgnoreCase(printOS))
				 {
					 pw.println("V");//Linux
				 }
				 else if ("windows".equalsIgnoreCase(printOS))
				 {
					if ("Inbuild".equalsIgnoreCase(printerType))
					{
					    pw.println("V");
					}
					else
					{
					    pw.println("m");//windows
					}
				 }

			    pw.flush();
			    pw.close();
			    
			    resp.setContentType("text/plain");
			    
		        
			    resp.setHeader("Content-disposition","attachment; filename=Bill Wise Settlement Wise Group Wise Breakup.txt"); // Used to name the download file and its format
			   // resp.setHeader("Content-disposition","filename=Bill Wise Settlement Wise Group Wise Breakup.txt"); 
		       OutputStream out = resp.getOutputStream();
		       FileInputStream in = new FileInputStream(file);
		       byte[] buffer = new byte[4096];
		       int length;
		       while ((length = in.read(buffer)) > 0){
		          out.write(buffer, 0, length);
		       }
		       in.close();
		       out.flush();
			   
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		    
			System.out.println("Hi");
				
		}
		
		@RequestMapping(value = "/sendReportOnMail", method = RequestMethod.POST)
		public @ResponseBody int funSendReportOnMail(@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate,@RequestParam("posCode") String posCode,HttpServletRequest req,HttpServletResponse resp)throws Exception
		{
			int i=1;
			try
			{
			String companyName = req.getSession().getAttribute("gCompanyName").toString();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
			String POSCode=req.getSession().getAttribute("loginPOS").toString();
			String posDate = req.getSession().getAttribute("gPOSDate").toString();
			objUtility.funCreateTempFolder();

		    String filePath = System.getProperty("user.dir");
		    File file = new File(filePath + File.separator + "Temp" + File.separator + "Bill Wise Settlement Wise Group Wise Breakup.txt");
		    PrintWriter pw = new PrintWriter(file);

		    String dashedLineOf150Chars = "------------------------------------------------------------------------------------------------------------------------------------------------------";

		    pw.println(companyName);
		    String gClientAddress2 = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,POSCode, "gClientAddress2");
		    String gClientAddress3 = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,POSCode, "gClientAddress3");
		    if (gClientAddress2.trim().length() > 0)
		    {
			pw.println(gClientAddress2);
		    }
		    if (gClientAddress3.trim().length() > 0)
		    {
			pw.println(gClientAddress3);
		    }
		    pw.println("Report : Bill Wise Settlement Wise Group Wise Tax Breakup");
		    pw.println();
		    String posName="All";
			if(hmPOSData.containsKey(posCode))
			{
				posName = hmPOSData.get(posCode);
			}
			String[] frmDate = fromDate.split("-");
			String[] toDte = toDate.split("-");
			int ret = funBillWiseSettlementWiseGroupWiseBreakup(frmDate[2]+"-"+frmDate[1]+"-"+frmDate[0],toDte[2]+"-"+toDte[1]+"-"+toDte[0],posCode,pw);

			 pw.println();
			 pw.println();
			 String printOS = "Windows";
			String printerType = "Inbuild";
			 pw.println();
			 pw.println();
			 if ("linux".equalsIgnoreCase(printOS))
			 {
				 pw.println(" V ");//Linux
			 }
			 else if ("windows".equalsIgnoreCase(printOS))
			 {
				if ("Inbuild".equalsIgnoreCase(printerType))
				{
				    pw.println(" V ");
				}
				else
				{
				    pw.println(" m ");//windows
				}
			 }

		    pw.flush();
		    pw.close();
		    resp.setContentType("text/plain");
		    
	        
		   // resp.setHeader("Content-disposition","attachment; filename=Bill Wise Settlement Wise Group Wise Breakup.txt"); // Used to name the download file and its format
		   resp.setHeader("Content-disposition","filename=Bill Wise Settlement Wise Group Wise Breakup.txt"); 
	       OutputStream out = resp.getOutputStream();
	       FileInputStream in = new FileInputStream(file);
	       byte[] buffer = new byte[4096];
	       int length;
	       while ((length = in.read(buffer)) > 0){
	          out.write(buffer, 0, length);
	       }
	       in.close();
	       out.flush();
		    String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,POSCode, "gReceiverEmailIds");
		    objSendMail.funSendMail(gReceiverEmailIds,file.getAbsolutePath(),strClientCode,POSCode,posName,posDate);
		    i=1;
			}
			catch(Exception e)
			{
				i=0;
				e.printStackTrace();
			}
		    return i;
		}
		

		public int funBillWiseSettlementWiseGroupWiseBreakup(String fromDate,String toDate,String posCode, PrintWriter pw) throws Exception
		{
			StringBuilder sbSqlLiveFile = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			sbSqlLiveFile.setLength(0);
			sbSqlLiveFile.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as date "
				+ " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
				+ "where  "
				+ "a.strBillNo=b.strBillNo  and a.strClientCode=b.strClientCode  "
				+ "and b.strSettlementCode=c.strSettelmentCode "
				+ "and date(a.dteBillDate) between  '" + fromDate + "' and '" + toDate + "' ");

			if (!posCode.equalsIgnoreCase("All"))
			{
			    sbSqlLiveFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sbSqlLiveFile.append("order BY date(a.dteBillDate),c.strSettelmentDesc ");
			System.out.println(sbSqlLiveFile);
//			ResultSet rsSettleManager = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlLiveFile.toString());
			pw.println("Reporting Bill:" + "  " + fromDate + " " + "To" + " " + toDate);
			String dashedLineOf150Chars = "------------------------------------------------------------------------------------------------------------------------------------------------------";
			pw.println(dashedLineOf150Chars);//line
			pw.println();
			pw.println("BILL WISE SETTLEMENT WISE GROUP WISE TAX BREAKUP");
			pw.println();
			pw.println("---------------------------");

			
			String firstBill = "", lastBill = "";

			String sqlTip = "", sqlNoOfBill = "", sqlDiscount = "";

			Map<String, Map<String, clsManagerReportBean>> mapBillWiseData = new TreeMap<String, Map<String, clsManagerReportBean>>();
			Map<String, Map<String, String>> mapBillWiseSettlementNames = new TreeMap<String, Map<String, String>>();
			Map<String, Map<String, String>> mapBillWiseTaxNames = new TreeMap<String, Map<String, String>>();
			Map<String, Map<String, String>> mapBillWiseGroupNames = new TreeMap<String, Map<String, String>>();

			int maxSettlementNameLength = 0;
			int maxGroupNameLength = 0;
			int maxTaxNameLength = 0;

			Map<String, Integer> mapGroupNameWithLength = new TreeMap<>();
			Map<String, Integer> mapTaxNameWithLength = new TreeMap<>();

			int cntTax = 1;
			double totalTaxAmt = 0.00;
			double totalSettleAmt = 0.00;
			double totalDiscAmt = 0.00;
			double totalTipAmt = 0.00;
			double totalRoundOffAmt = 0.00;
			double totalBills = 0;

			sbSqlLiveFile.setLength(0);
			sbSqlLiveFile.append(" select a.strBillNo,c.strSettelmentCode,c.strSettelmentDesc,b.dblSettlementAmt,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
				+ " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
				+ " where a.strBillNo=b.strBillNo "
				+ " and date(a.dteBillDate)=date(b.dteBillDate) "
				+ " and b.strSettlementCode=c.strSettelmentCode "
				+ " and a.strClientCode=b.strClientCode "//and a.strSettelmentMode!='MultiSettle'
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and c.strSettelmentType!='Complementary' "
				+ " ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sbSqlLiveFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sbSqlLiveFile.append(" order BY a.strBillNo,c.strSettelmentDesc ");
			System.out.println(sbSqlLiveFile);

			List listSettleManager = objBaseService.funGetList(sbSqlLiveFile, "sql");
			if(listSettleManager.size()>0)
			{
				for(int i=0;i<listSettleManager.size();i++)
				{	
				Object[] obj = (Object[])listSettleManager.get(i);	
			    String strBillNo = obj[0].toString();
			    String settlementCode = obj[1].toString();
			    String settlementDesc = obj[2].toString();
			    double settleAmt = Double.parseDouble(obj[3].toString());
			    String billDate = obj[4].toString();

			    if (settlementDesc.length() > maxSettlementNameLength)
			    {
				maxSettlementNameLength = settlementDesc.length();
			    }

			    totalSettleAmt = totalSettleAmt + settleAmt;

			    if (mapBillWiseSettlementNames.containsKey(strBillNo))
			    {
				Map<String, String> mapSettlementNames = mapBillWiseSettlementNames.get(strBillNo);

				mapSettlementNames.put(settlementCode, settlementDesc);
			    }
			    else
			    {
				Map<String, String> mapSettlementNames = new TreeMap<>();

				mapSettlementNames.put(settlementCode, settlementDesc);

				mapBillWiseSettlementNames.put(strBillNo, mapSettlementNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				//put settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey(settlementCode))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get(settlementCode);
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);

				    mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrSettlementCode(settlementCode);
				    objManagerReportBean.setStrSettlementDesc(settlementDesc);
				    objManagerReportBean.setDblSettlementAmt(settleAmt);

				    mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
				}
				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalSettlementAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalSettlementAmt");
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);

				    mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
				    objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
				    objManagerReportBean.setDblSettlementAmt(settleAmt);

				    mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
				}

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				//put settlement dtl
				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrSettlementCode(settlementCode);
				objManagerReportBean.setStrSettlementDesc(settlementDesc);
				objManagerReportBean.setDblSettlementAmt(settleAmt);

				mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);

				//put total settlement dtl
				objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
				objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
				objManagerReportBean.setDblSettlementAmt(settleAmt);

				mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			} 
			}
			

			sbSqlQFile.setLength(0);
			sbSqlQFile.append(" select a.strBillNo,c.strSettelmentCode,c.strSettelmentDesc,b.dblSettlementAmt,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
				+ " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
				+ " where a.strBillNo=b.strBillNo "
				+ " and date(a.dteBillDate)=date(b.dteBillDate) "
				+ " and b.strSettlementCode=c.strSettelmentCode "
				+ " and a.strClientCode=b.strClientCode "//and a.strSettelmentMode!='MultiSettle' 
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and c.strSettelmentType!='Complementary' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sbSqlQFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sbSqlQFile.append(" order BY a.strBillNo,c.strSettelmentDesc ");
			listSettleManager = objBaseService.funGetList(sbSqlQFile, "sql");

			if(listSettleManager.size()>0)
			{
				for(int i=0;i<listSettleManager.size();i++)
				{
				Object[] obj = (Object[])listSettleManager.get(i);
			    String strBillNo = obj[0].toString();
			    String settlementCode = obj[1].toString();
			    String settlementDesc = obj[2].toString();
			    double settleAmt = Double.parseDouble(obj[3].toString());
			    String billDate = obj[4].toString();

			    if (settlementDesc.length() > maxSettlementNameLength)
			    {
				maxSettlementNameLength = settlementDesc.length();
			    }

			    totalSettleAmt = totalSettleAmt + settleAmt;

			    if (mapBillWiseSettlementNames.containsKey(strBillNo))
			    {
				Map<String, String> mapSettlementNames = mapBillWiseSettlementNames.get(strBillNo);

				mapSettlementNames.put(settlementCode, settlementDesc);
			    }
			    else
			    {
				Map<String, String> mapSettlementNames = new TreeMap<>();

				mapSettlementNames.put(settlementCode, settlementDesc);

				mapBillWiseSettlementNames.put(strBillNo, mapSettlementNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				//put settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey(settlementCode))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get(settlementCode);
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);

				    mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrSettlementCode(settlementCode);
				    objManagerReportBean.setStrSettlementDesc(settlementDesc);
				    objManagerReportBean.setDblSettlementAmt(settleAmt);

				    mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
				}
				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalSettlementAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalSettlementAmt");
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);

				    mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
				    objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
				    objManagerReportBean.setDblSettlementAmt(settleAmt);

				    mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
				}

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
				//put settlement dtl

				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrSettlementCode(settlementCode);
				objManagerReportBean.setStrSettlementDesc(settlementDesc);
				objManagerReportBean.setDblSettlementAmt(settleAmt);

				mapBillWiseSettlementWiseData.put(settlementCode, objManagerReportBean);

				//put total settlement dtl
				objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
				objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
				objManagerReportBean.setDblSettlementAmt(settleAmt);

				mapBillWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			

			/**
			 * live taxes
			 */
			StringBuilder sqlTax = new StringBuilder();
			sqlTax.setLength(0);
			sqlTax.append("select a.strBillNo,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount) "
				+ " from tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
				+ " where a.strBillNo=b.strBillNo "
				+ " and date(a.dteBillDate)=date(b.dteBillDate) "
				+ " and b.strTaxCode=c.strTaxCode "
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
				+ " and a.strClientCode=b.strClientCode ");
			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlTax.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlTax.append(" group by a.strBillNo,c.strTaxCode");
			List listTaxDtl1 = objBaseService.funGetList(sqlTax, "sql");
			if(listTaxDtl1.size()>0)
			{
				for(int i=0;i<listTaxDtl1.size();i++)
				{
				Object[] obj = (Object[]) listTaxDtl1.get(i);	
			    String strBillNo = obj[0].toString();
			    String taxCode = obj[1].toString();
			    String taxDesc = obj[2].toString();
			    double taxAmt = Double.parseDouble(obj[3].toString());

			    mapTaxNameWithLength.put(taxDesc, taxDesc.length());
			    if (taxDesc.length() > maxTaxNameLength)
			    {
				maxTaxNameLength = taxDesc.length();
			    }

			    totalTaxAmt = totalTaxAmt + taxAmt;

			    if (mapBillWiseTaxNames.containsKey(strBillNo))
			    {
				Map<String, String> mapTaxNames = mapBillWiseTaxNames.get(strBillNo);

				mapTaxNames.put(taxCode, taxDesc);
			    }
			    else
			    {
				Map<String, String> mapTaxNames = new TreeMap<>();

				mapTaxNames.put(taxCode, taxDesc);

				mapBillWiseTaxNames.put(strBillNo, mapTaxNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				//put tax dtl
				if (mapBillWiseSettlementWiseData.containsKey(taxCode))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get(taxCode);
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);

				    mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrTaxCode(taxCode);
				    objManagerReportBean.setStrTaxDesc(taxDesc);
				    objManagerReportBean.setDblTaxAmt(taxAmt);

				    mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);
				}

				//put total tax dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalTaxAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalTaxAmt");
				    objManagerReportBean.setDblTaxAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);

				    mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrTaxCode("TotalTaxAmt");
				    objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
				    objManagerReportBean.setDblTaxAmt(taxAmt);

				    mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
				}

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrTaxCode(taxCode);
				objManagerReportBean.setStrTaxDesc(taxDesc);
				objManagerReportBean.setDblTaxAmt(taxAmt);

				//put total tax dtl
				objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrTaxCode("TotalTaxAmt");
				objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
				objManagerReportBean.setDblTaxAmt(taxAmt);

				mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			

			/**
			 * Q taxes
			 */
			sqlTax.setLength(0);
			sqlTax.append("select a.strBillNo,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount) "
				+ " from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c "
				+ " where a.strBillNo=b.strBillNo "
				+ " and date(a.dteBillDate)=date(b.dteBillDate) "
				+ " and b.strTaxCode=c.strTaxCode "
				+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
				+ " and a.strClientCode=b.strClientCode ");
			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlTax.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlTax.append( " group by a.strBillNo,c.strTaxCode");
			listTaxDtl1 = objBaseService.funGetList(sqlTax, "sql");
			if(listTaxDtl1.size()>0)
			{
				for(int i=0;i<listTaxDtl1.size();i++)
				{
				Object[] obj = (Object[])listTaxDtl1.get(i);	
			    String strBillNo = obj[0].toString();
			    String taxCode = obj[1].toString();
			    String taxDesc = obj[2].toString();
			    double taxAmt = Double.parseDouble(obj[3].toString());

			    if (taxDesc.length() > maxTaxNameLength)
			    {
				maxTaxNameLength = taxDesc.length();
			    }
			    mapTaxNameWithLength.put(taxDesc, taxDesc.length());

			    totalTaxAmt = totalTaxAmt + taxAmt;

			    if (mapBillWiseTaxNames.containsKey(strBillNo))
			    {
				Map<String, String> mapTaxNames = mapBillWiseTaxNames.get(strBillNo);

				mapTaxNames.put(taxCode, taxDesc);
			    }
			    else
			    {
				Map<String, String> mapTaxNames = new TreeMap<>();

				mapTaxNames.put(taxCode, taxDesc);

				mapBillWiseTaxNames.put(strBillNo, mapTaxNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				//put tax dtl
				if (mapBillWiseSettlementWiseData.containsKey(taxCode))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get(taxCode);
				    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);

				    mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrTaxCode(taxCode);
				    objManagerReportBean.setStrTaxDesc(taxDesc);
				    objManagerReportBean.setDblTaxAmt(taxAmt);

				    mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);
				}

				//put total tax dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalTaxAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalTaxAmt");
				    objManagerReportBean.setDblTaxAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);

				    mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrTaxCode("TotalTaxAmt");
				    objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
				    objManagerReportBean.setDblTaxAmt(taxAmt);

				    mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
				}

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrTaxCode(taxCode);
				objManagerReportBean.setStrTaxDesc(taxDesc);
				objManagerReportBean.setDblTaxAmt(taxAmt);

				objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrTaxCode("TotalTaxAmt");
				objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
				objManagerReportBean.setDblTaxAmt(taxAmt);

				mapBillWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(taxCode, objManagerReportBean);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			

			//set discount,roundoff,tip
			sbSqlLiveFile.setLength(0);
			sbSqlLiveFile.append(" SELECT sum(a.dblDiscountAmt),sum(a.dblRoundOff),sum(a.dblTipAmount),a.strBillNo "
				+ " from tblbillhd a , tblbillsettlementdtl b "
				+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and a.strBillNo=b.strBillNo ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sbSqlLiveFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sbSqlLiveFile.append(" group by a.strBillNo ");
			System.out.println(sbSqlLiveFile);

			listSettleManager = objBaseService.funGetList(sbSqlLiveFile, "sql");
			if(listSettleManager.size()>0)
			{
				for(int i=0;i<listSettleManager.size();i++)
				{
				Object[] obj =(Object[]) listSettleManager.get(i);	
			    double discAmt = Double.parseDouble(obj[0].toString());//discAmt
			    double roundOffAmt =Double.parseDouble(obj[1].toString());//roundOff
			    double tipAmt = Double.parseDouble(obj[2].toString());//tipAmt
			    totalDiscAmt = totalDiscAmt + discAmt;
			    totalRoundOffAmt = totalRoundOffAmt + roundOffAmt;//roundOff
			    totalTipAmt = totalTipAmt + tipAmt;//tipAmt
			    String strBillNo = obj[3].toString();//billDate

			    //discount
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("DiscAmt"))
				{
				    clsManagerReportBean objDiscAmt = mapBillWiseSettlementWiseData.get("DiscAmt");
				    objDiscAmt.setDblDiscAmt(objDiscAmt.getDblDiscAmt() + discAmt);
				}
				else
				{
				    clsManagerReportBean objDiscAmt = new clsManagerReportBean();
				    objDiscAmt.setDblDiscAmt(discAmt);

				    mapBillWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objDiscAmt = new clsManagerReportBean();
				objDiscAmt.setDblDiscAmt(discAmt);

				mapBillWiseSettlementWiseData.put("DiscAmt", objDiscAmt);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }

			    //roundoff
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("RoundOffAmt"))
				{
				    clsManagerReportBean objRoundOffAmt = mapBillWiseSettlementWiseData.get("RoundOffAmt");
				    objRoundOffAmt.setDblRoundOffAmt(objRoundOffAmt.getDblRoundOffAmt() + roundOffAmt);
				}
				else
				{
				    clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
				    objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);

				    mapBillWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
				objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);

				mapBillWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }

			    //tip
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("TipAmt"))
				{
				    clsManagerReportBean objTipAmt = mapBillWiseSettlementWiseData.get("TipAmt");
				    objTipAmt.setDblTipAmt(objTipAmt.getDblTipAmt() + tipAmt);
				}
				else
				{
				    clsManagerReportBean objTipAmt = new clsManagerReportBean();
				    objTipAmt.setDblTipAmt(tipAmt);

				    mapBillWiseSettlementWiseData.put("TipAmt", objTipAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objTipAmt = new clsManagerReportBean();
				objTipAmt.setDblTipAmt(tipAmt);

				mapBillWiseSettlementWiseData.put("TipAmt", objTipAmt);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }

			}
			}
			

			sbSqlQFile.setLength(0);
			sbSqlQFile.append(" SELECT sum(a.dblDiscountAmt),sum(a.dblRoundOff),sum(a.dblTipAmount),a.strBillNo "
				+ " from tblqbillhd a, tblqbillsettlementdtl b"
				+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
				+ " and a.strBillNo=b.strBillNo ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sbSqlQFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sbSqlQFile.append(" group by a.strBillNo ");
			System.out.println(sbSqlQFile);

			listSettleManager = objBaseService.funGetList(sbSqlQFile, "sql");
			if(listSettleManager.size()>0)
			{
				for(int i=0;i<listSettleManager.size();i++)
				{
				Object[] obj = (Object[]) listSettleManager.get(i);	
			    double discAmt = Double.parseDouble(obj[0].toString());//discAmt
			    double roundOffAmt =  Double.parseDouble(obj[1].toString());//roundOff
			    double tipAmt =  Double.parseDouble(obj[2].toString());//tipAmt
			    // int noOfBills = rsSettleManager.getInt(4);//bill count
			    totalDiscAmt = totalDiscAmt + discAmt;
			    totalRoundOffAmt = totalRoundOffAmt + roundOffAmt;//roundOff
			    totalTipAmt = totalTipAmt + tipAmt;//tipAmt
			    //  totalBills = totalBills + noOfBills;//bill count
			    String strBillNo = obj[3].toString();//billDate

			    //discount
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("DiscAmt"))
				{
				    clsManagerReportBean objDiscAmt = mapBillWiseSettlementWiseData.get("DiscAmt");
				    objDiscAmt.setDblDiscAmt(objDiscAmt.getDblDiscAmt() + discAmt);
				}
				else
				{
				    clsManagerReportBean objDiscAmt = new clsManagerReportBean();
				    objDiscAmt.setDblDiscAmt(discAmt);

				    mapBillWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objDiscAmt = new clsManagerReportBean();
				objDiscAmt.setDblDiscAmt(discAmt);

				mapBillWiseSettlementWiseData.put("DiscAmt", objDiscAmt);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }

			    //roundoff
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("RoundOffAmt"))
				{
				    clsManagerReportBean objRoundOffAmt = mapBillWiseSettlementWiseData.get("RoundOffAmt");
				    objRoundOffAmt.setDblRoundOffAmt(objRoundOffAmt.getDblRoundOffAmt() + roundOffAmt);
				}
				else
				{
				    clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
				    objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);

				    mapBillWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
				objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);

				mapBillWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }

			    //tip
			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);
				if (mapBillWiseSettlementWiseData.containsKey("TipAmt"))
				{
				    clsManagerReportBean objTipAmt = mapBillWiseSettlementWiseData.get("TipAmt");
				    objTipAmt.setDblTipAmt(objTipAmt.getDblTipAmt() + tipAmt);
				}
				else
				{
				    clsManagerReportBean objTipAmt = new clsManagerReportBean();
				    objTipAmt.setDblTipAmt(tipAmt);

				    mapBillWiseSettlementWiseData.put("TipAmt", objTipAmt);
				}

			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objTipAmt = new clsManagerReportBean();
				objTipAmt.setDblTipAmt(tipAmt);

				mapDateWiseSettlementWiseData.put("TipAmt", objTipAmt);

				mapBillWiseData.put(strBillNo, mapDateWiseSettlementWiseData);
			    }
			   
			}
			}
			

			/**
			 * fill live date wise group wise data
			 */
			StringBuilder sqlGroupData = new StringBuilder();

			sqlGroupData.setLength(0);
			sqlGroupData.append("select  a.strBillNo,e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscountAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscountAmt)NetTotal "
				+ "from tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e "
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) "
				+ "and b.strItemCode=c.strItemCode "
				+ "and c.strSubGroupCode=d.strSubGroupCode "
				+ "and d.strGroupCode=e.strGroupCode "
				+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sqlGroupData.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlGroupData.append("group by  a.strBillNo,e.strGroupCode ");
			List lisrGroupsData = objBaseService.funGetList(sqlGroupData, "sql");
			if(lisrGroupsData.size()>0)
			{
				for(int i=0;i<lisrGroupsData.size();i++)
				{
				Object[] obj = (Object[])lisrGroupsData.get(i);	
			    String strBillNo = obj[0].toString();//date
			    String groupCode = obj[1].toString();//groupCode
			    String groupName = obj[2].toString();//groupName
			    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
			    double discount = Double.parseDouble(obj[4].toString()); //discount
			    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

			    if (groupName.length() > maxGroupNameLength)
			    {
				maxGroupNameLength = groupName.length();
			    }
			    mapGroupNameWithLength.put(groupName, groupName.length());

			    if (mapBillWiseGroupNames.containsKey(strBillNo))
			    {
				Map<String, String> mapGroupNames = mapBillWiseGroupNames.get(strBillNo);

				mapGroupNames.put(groupCode, groupName);
			    }
			    else
			    {
				Map<String, String> mapGroupNames = new TreeMap<>();

				mapGroupNames.put(groupCode, groupName);

				mapBillWiseGroupNames.put(strBillNo, mapGroupNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				if (mapBillWiseSettlementWiseData.containsKey(groupCode))
				{
				    clsManagerReportBean objGroupDtl = mapBillWiseSettlementWiseData.get(groupCode);

				    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
				    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
				    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
				}
				else
				{
				    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				    objGroupDtl.setStrGroupCode(groupCode);
				    objGroupDtl.setStrGroupName(groupName);
				    objGroupDtl.setDblSubTotal(subTotal);
				    objGroupDtl.setDblDisAmt(discount);
				    objGroupDtl.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);
				}

				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalGroupAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
				    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				    objManagerReportBean.setStrGroupName("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(subTotal);
				    objManagerReportBean.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				objGroupDtl.setStrGroupCode(groupCode);
				objGroupDtl.setStrGroupName(groupName);
				objGroupDtl.setDblSubTotal(subTotal);
				objGroupDtl.setDblDisAmt(discount);
				objGroupDtl.setDblNetTotal(netTotal);

				//put total settlement dtl
				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				objManagerReportBean.setStrGroupName("TotalGroupAmt");
				objManagerReportBean.setDblSubTotal(subTotal);
				objManagerReportBean.setDblNetTotal(netTotal);

				mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}

			/**
			 * fill live modifiers date wise group wise data
			 */
			sqlGroupData.setLength(0);
			sqlGroupData.append("select a.strBillNo,e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscAmt)NetTotal "
				+ "from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e "
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) "
				+ "and left(b.strItemCode,7)=c.strItemCode "
				+ "and c.strSubGroupCode=d.strSubGroupCode "
				+ "and d.strGroupCode=e.strGroupCode "
				+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sqlGroupData.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlGroupData.append("group by a.strBillNo,e.strGroupCode ");
			List listGroupsData = objBaseService.funGetList(sqlGroupData, "sql");
			if(listGroupsData.size()>0)
			{
				for(int i=0;i<listGroupsData.size();i++)
				{
				Object[] obj = (Object[])listGroupsData.get(i);	
			    String strBillNo = obj[0].toString();//date
			    String groupCode = obj[1].toString();//groupCode
			    String groupName = obj[2].toString();//groupName
			    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
			    double discount = Double.parseDouble(obj[4].toString()); //discount
			    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

			    if (groupName.length() > maxGroupNameLength)
			    {
				maxGroupNameLength = groupName.length();
			    }
			    mapGroupNameWithLength.put(groupName, groupName.length());

			    if (mapBillWiseGroupNames.containsKey(strBillNo))
			    {
				Map<String, String> mapGroupNames = mapBillWiseGroupNames.get(strBillNo);

				mapGroupNames.put(groupCode, groupName);
			    }
			    else
			    {
				Map<String, String> mapGroupNames = new TreeMap<>();

				mapGroupNames.put(groupCode, groupName);

				mapBillWiseGroupNames.put(strBillNo, mapGroupNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				if (mapBillWiseSettlementWiseData.containsKey(groupCode))
				{
				    clsManagerReportBean objGroupDtl = mapBillWiseSettlementWiseData.get(groupCode);

				    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
				    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
				    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
				}
				else
				{
				    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				    objGroupDtl.setStrGroupCode(groupCode);
				    objGroupDtl.setStrGroupName(groupName);
				    objGroupDtl.setDblSubTotal(subTotal);
				    objGroupDtl.setDblDisAmt(discount);
				    objGroupDtl.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);
				}

				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalGroupAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
				    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				    objManagerReportBean.setStrGroupName("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(subTotal);
				    objManagerReportBean.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				objGroupDtl.setStrGroupCode(groupCode);
				objGroupDtl.setStrGroupName(groupName);
				objGroupDtl.setDblSubTotal(subTotal);
				objGroupDtl.setDblDisAmt(discount);
				objGroupDtl.setDblNetTotal(netTotal);

				//put total settlement dtl
				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				objManagerReportBean.setStrGroupName("TotalGroupAmt");
				objManagerReportBean.setDblSubTotal(subTotal);
				objManagerReportBean.setDblNetTotal(netTotal);

				mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			

			/**
			 * fill Q date wise group wise data
			 */
			sqlGroupData.setLength(0);
			sqlGroupData.append("select a.strBillNo,e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscountAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscountAmt)NetTotal "
				+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e "
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) "
				+ "and b.strItemCode=c.strItemCode "
				+ "and c.strSubGroupCode=d.strSubGroupCode "
				+ "and d.strGroupCode=e.strGroupCode "
				+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sqlGroupData.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlGroupData.append("group by a.strBillNo,e.strGroupCode ");
			listGroupsData = objBaseService.funGetList(sqlGroupData, "sql");
			if(listGroupsData.size()>0)
			{
				for(int i=0;i<listGroupsData.size();i++)
				{
				Object[] obj = (Object[]) listGroupsData.get(i);	
				String strBillNo = obj[0].toString();//date
			    String groupCode = obj[1].toString();//groupCode
			    String groupName = obj[2].toString();//groupName
			    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
			    double discount = Double.parseDouble(obj[4].toString()); //discount
			    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

			    if (groupName.length() > maxGroupNameLength)
			    {
				maxGroupNameLength = groupName.length();
			    }
			    mapGroupNameWithLength.put(groupName, groupName.length());

			    if (mapBillWiseGroupNames.containsKey(strBillNo))
			    {
				Map<String, String> mapGroupNames = mapBillWiseGroupNames.get(strBillNo);

				mapGroupNames.put(groupCode, groupName);
			    }
			    else
			    {
				Map<String, String> mapGroupNames = new TreeMap<>();

				mapGroupNames.put(groupCode, groupName);

				mapBillWiseGroupNames.put(strBillNo, mapGroupNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				if (mapBillWiseSettlementWiseData.containsKey(groupCode))
				{
				    clsManagerReportBean objGroupDtl = mapBillWiseSettlementWiseData.get(groupCode);

				    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
				    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
				    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
				}
				else
				{
				    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				    objGroupDtl.setStrGroupCode(groupCode);
				    objGroupDtl.setStrGroupName(groupName);
				    objGroupDtl.setDblSubTotal(subTotal);
				    objGroupDtl.setDblDisAmt(discount);
				    objGroupDtl.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);
				}

				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalGroupAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
				    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				    objManagerReportBean.setStrGroupName("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(subTotal);
				    objManagerReportBean.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				objGroupDtl.setStrGroupCode(groupCode);
				objGroupDtl.setStrGroupName(groupName);
				objGroupDtl.setDblSubTotal(subTotal);
				objGroupDtl.setDblDisAmt(discount);
				objGroupDtl.setDblNetTotal(netTotal);

				//put total settlement dtl
				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				objManagerReportBean.setStrGroupName("TotalGroupAmt");
				objManagerReportBean.setDblSubTotal(subTotal);
				objManagerReportBean.setDblNetTotal(netTotal);

				mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			

			/**
			 * fill Q modifiers date wise group wise data
			 */
			sqlGroupData.setLength(0);
			sqlGroupData.append("select a.strBillNo,e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscAmt)NetTotal "
				+ "from tblqbillhd a,tblqbillmodifierdtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e "
				+ "where a.strBillNo=b.strBillNo "
				+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) "
				+ "and left(b.strItemCode,7)=c.strItemCode "
				+ "and c.strSubGroupCode=d.strSubGroupCode "
				+ "and d.strGroupCode=e.strGroupCode "
				+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All"))
			{
			    sqlGroupData.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlGroupData.append("group by a.strBillNo,e.strGroupCode ");
			listGroupsData = objBaseService.funGetList(sqlGroupData, "sql");
			if(listGroupsData.size()>0)
			{
				for(int i=0;i<listGroupsData.size();i++)
				{
				Object[] obj = (Object[])listGroupsData.get(i);	
			    String strBillNo = obj[0].toString();//date
			    String groupCode = obj[1].toString();//groupCode
			    String groupName = obj[2].toString();//groupName
			    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
			    double discount = Double.parseDouble(obj[4].toString()); //discount
			    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

			    if (groupName.length() > maxGroupNameLength)
			    {
				maxGroupNameLength = groupName.length();
			    }
			    mapGroupNameWithLength.put(groupName, groupName.length());

			    if (mapBillWiseGroupNames.containsKey(strBillNo))
			    {
				Map<String, String> mapGroupNames = mapBillWiseGroupNames.get(strBillNo);

				mapGroupNames.put(groupCode, groupName);
			    }
			    else
			    {
				Map<String, String> mapGroupNames = new TreeMap<>();

				mapGroupNames.put(groupCode, groupName);

				mapBillWiseGroupNames.put(strBillNo, mapGroupNames);
			    }

			    if (mapBillWiseData.containsKey(strBillNo))
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = mapBillWiseData.get(strBillNo);

				if (mapBillWiseSettlementWiseData.containsKey(groupCode))
				{
				    clsManagerReportBean objGroupDtl = mapBillWiseSettlementWiseData.get(groupCode);

				    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
				    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
				    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
				}
				else
				{
				    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				    objGroupDtl.setStrGroupCode(groupCode);
				    objGroupDtl.setStrGroupName(groupName);
				    objGroupDtl.setDblSubTotal(subTotal);
				    objGroupDtl.setDblDisAmt(discount);
				    objGroupDtl.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);
				}

				//put total settlement dtl
				if (mapBillWiseSettlementWiseData.containsKey("TotalGroupAmt"))
				{
				    clsManagerReportBean objManagerReportBean = mapBillWiseSettlementWiseData.get("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
				    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
				else
				{
				    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				    objManagerReportBean.setStrGroupName("TotalGroupAmt");
				    objManagerReportBean.setDblSubTotal(subTotal);
				    objManagerReportBean.setDblNetTotal(netTotal);

				    mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
				}
			    }
			    else
			    {
				Map<String, clsManagerReportBean> mapBillWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();

				clsManagerReportBean objGroupDtl = new clsManagerReportBean();
				objGroupDtl.setStrGroupCode(groupCode);
				objGroupDtl.setStrGroupName(groupName);
				objGroupDtl.setDblSubTotal(subTotal);
				objGroupDtl.setDblDisAmt(discount);
				objGroupDtl.setDblNetTotal(netTotal);

				//put total settlement dtl
				clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
				objManagerReportBean.setStrGroupCode("TotalGroupAmt");
				objManagerReportBean.setStrGroupName("TotalGroupAmt");
				objManagerReportBean.setDblSubTotal(subTotal);
				objManagerReportBean.setDblNetTotal(netTotal);

				mapBillWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);

				mapBillWiseSettlementWiseData.put(groupCode, objGroupDtl);

				mapBillWiseData.put(strBillNo, mapBillWiseSettlementWiseData);
			    }
			}
			}
			
			/**
			 * new logic for gross sales
			 */
			Map<String, Map<String, Double>> mapSettelemtWiseGroupBreakup = new TreeMap<>();
			Map<String, Map<String, Double>> mapSettelemtWiseTaxBreakup = new TreeMap<>();
			/**
			 * new logic for gross sales
			 */

			if (mapBillWiseData.size() > 0)
			{
			    for (Map.Entry<String, Map<String, clsManagerReportBean>> entrySet : mapBillWiseData.entrySet())
			    {
				String strBillNo = entrySet.getKey();
				Map<String, clsManagerReportBean> mapBillWiseGroupTaxSettlementData = entrySet.getValue();

				clsManagerReportBean objTotalSettlementAmt = mapBillWiseGroupTaxSettlementData.get("TotalSettlementAmt");
				double totalSettlementAmt = 0;
				if (objTotalSettlementAmt != null)
				{
				    totalSettlementAmt = objTotalSettlementAmt.getDblSettlementAmt();
				}
				clsManagerReportBean objTotalTaxAmt = mapBillWiseGroupTaxSettlementData.get("TotalTaxAmt");
				totalTaxAmt = 0;
				if (objTotalTaxAmt != null)
				{
				    totalTaxAmt = objTotalTaxAmt.getDblTaxAmt();
				}

				clsManagerReportBean objTotalGroupAmt = mapBillWiseGroupTaxSettlementData.get("TotalGroupAmt");
				//double totalGroupSubTotal = objTotalGroupAmt.getDblSubTotal();
				double totalGroupNetTotal = 0;
				if (objTotalGroupAmt != null)
				{
				    totalGroupNetTotal = objTotalGroupAmt.getDblNetTotal();
				}

				int maxLineCount = 0;
				String labelSettlement = "SETTLEMENT          |";

				String horizontalTotalLabel = "  TOTALS   |";

				pw.println();
				pw.print(strBillNo);

				Map<String, String> mapBillWiseTaxeNames = mapBillWiseTaxNames.get(strBillNo);
				if (mapBillWiseTaxeNames != null)
				{
				    if (mapBillWiseGroupNames.containsKey(strBillNo))
				    {
					Map<String, String> mapGroupNames = mapBillWiseGroupNames.get(strBillNo);
					for (Map.Entry<String, String> entryGroupNames : mapGroupNames.entrySet())
					{

					    String groupCode = entryGroupNames.getKey();
					    String groupName = entryGroupNames.getValue();
					    if (groupName.length() > maxGroupNameLength)
					    {
						maxGroupNameLength = groupName.length();
					    }

					    clsManagerReportBean objGroupDtl = mapBillWiseGroupTaxSettlementData.get(groupCode);
					    //double groupSubTotal = objGroupDtl.getDblSubTotal();
					    double groupNetTotal = objGroupDtl.getDblNetTotal();

					    /**
					     * print a line
					     */
					    int lineCount = funGetLineCount(strBillNo, labelSettlement, groupName, horizontalTotalLabel, mapBillWiseData, mapBillWiseSettlementNames, mapBillWiseTaxNames);
					    pw.println();
					    for (int i = 0; i < lineCount; i++)
					    {
						pw.print("-");
					    }
					    if (lineCount > maxLineCount)
					    {
						maxLineCount = lineCount;
					    }

					    /**
					     * print header line
					     */
					    pw.println();
					    pw.print(objUtility.funPrintTextWithAlignment(labelSettlement, labelSettlement.length(), "Left"));
					    pw.print(objUtility.funPrintTextWithAlignment(groupName + "|", groupName.length(), "Left"));
					    if (mapBillWiseTaxeNames != null)
					    {
						for (String taxDesc : mapBillWiseTaxeNames.values())
						{
						    String labelTaxDesc = taxDesc + "|";
						    pw.print(objUtility.funPrintTextWithAlignment(labelTaxDesc, labelTaxDesc.length(), "Left"));
						}
					    }
					    pw.print(objUtility.funPrintTextWithAlignment(horizontalTotalLabel, horizontalTotalLabel.length(), "Left"));

					    /**
					     * print settlement wise data
					     */
					    pw.println();
					    Map<String, String> mapSettlementNames = mapBillWiseSettlementNames.get(strBillNo);

					    if (mapSettlementNames != null)
					    {
						for (Map.Entry<String, String> entrySettlements : mapSettlementNames.entrySet())
						{
						    String settlementCode = entrySettlements.getKey();
						    String settlementName = entrySettlements.getValue();

						    double horizontalTotalAmt = 0.00;

						    clsManagerReportBean objSettlementDtl = mapBillWiseGroupTaxSettlementData.get(settlementCode);

						    double groupSubTotalForThisSettlement = 0.00;
						    if (totalSettlementAmt > 0)
						    {
							groupSubTotalForThisSettlement = (groupNetTotal / totalSettlementAmt) * objSettlementDtl.getDblSettlementAmt();
						    }
						    horizontalTotalAmt += groupSubTotalForThisSettlement;

						    //new added for groups
						    if (mapSettelemtWiseGroupBreakup.containsKey(settlementName))
						    {
							Map<String, Double> mapGroupBreakup = mapSettelemtWiseGroupBreakup.get(settlementName);
							if (mapGroupBreakup.containsKey(groupName))
							{
							    mapGroupBreakup.put(groupName, mapGroupBreakup.get(groupName) + groupSubTotalForThisSettlement);

							    mapSettelemtWiseGroupBreakup.put(settlementName, mapGroupBreakup);
							}
							else
							{
							    mapGroupBreakup.put(groupName, groupSubTotalForThisSettlement);

							    mapSettelemtWiseGroupBreakup.put(settlementName, mapGroupBreakup);
							}
						    }
						    else
						    {
							Map<String, Double> mapGroupBreakup = new TreeMap<String, Double>();

							mapGroupBreakup.put(groupName, groupSubTotalForThisSettlement);

							mapSettelemtWiseGroupBreakup.put(settlementName, mapGroupBreakup);
						    }

						    pw.println();
						    pw.print(objUtility.funPrintTextWithAlignment(settlementName, labelSettlement.length(), "Left"));
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupSubTotalForThisSettlement) + "|"), groupName.length(), "Right"));
						    if (mapBillWiseTaxeNames != null)
						    {
							for (Map.Entry<String, String> entryTaxNames : mapBillWiseTaxeNames.entrySet())
							{
							    String taxCode = entryTaxNames.getKey();
							    String taxName = entryTaxNames.getValue();

							    String labelTaxDesc = taxName + "|";

							    clsManagerReportBean objTaxDtl = mapBillWiseGroupTaxSettlementData.get(taxCode);
							    double taxAmt = objTaxDtl.getDblTaxAmt();

							    double taxWiseGroupTotal = funGetTaxWiseGroupTotal(strBillNo, taxCode, mapBillWiseGroupTaxSettlementData);

							    double taxAmtForThisTax = 0.00;
							    boolean isApplicable = isApplicableTaxOnGroup(taxCode, groupCode);

							    if (taxWiseGroupTotal > 0 && isApplicable)
							    {
								taxAmtForThisTax = (taxAmt / taxWiseGroupTotal) * groupSubTotalForThisSettlement;
							    }
							    horizontalTotalAmt += taxAmtForThisTax;

							    //new added for taxes
							    String key = settlementName + "!" + groupName + "!" + taxName;
							    if (mapSettelemtWiseTaxBreakup.containsKey(settlementName))
							    {
								Map<String, Double> mapTaxBreakup = mapSettelemtWiseTaxBreakup.get(settlementName);
								if (mapTaxBreakup.containsKey(key))
								{
								    mapTaxBreakup.put(key, mapTaxBreakup.get(key) + taxAmtForThisTax);

								    mapSettelemtWiseTaxBreakup.put(settlementName, mapTaxBreakup);
								}
								else
								{
								    mapTaxBreakup.put(key, taxAmtForThisTax);

								    mapSettelemtWiseTaxBreakup.put(settlementName, mapTaxBreakup);
								}
							    }
							    else
							    {
								Map<String, Double> mapTaxBreakup = new TreeMap<String, Double>();

								mapTaxBreakup.put(key, taxAmtForThisTax);

								mapSettelemtWiseTaxBreakup.put(settlementName, mapTaxBreakup);
							    }

							    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmtForThisTax)) + "|", labelTaxDesc.length(), "Right"));
							}
						    }
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(horizontalTotalAmt)) + "|", horizontalTotalLabel.length(), "Right"));
						}
					    }
					    /**
					     * print total line
					     */
					    pw.println();
					    for (int i = 0; i < lineCount; i++)
					    {
						pw.print("-");
					    }
					    pw.println();
					    pw.print(objUtility.funPrintTextWithAlignment(groupName.toUpperCase() + " TOTALS", labelSettlement.length(), "Left"));
					    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupNetTotal)) + "|", groupName.length(), "Right"));
					    if (mapBillWiseTaxeNames != null)
					    {
						for (Map.Entry<String, String> entryTaxNames : mapBillWiseTaxeNames.entrySet())
						{
						    String taxCode = entryTaxNames.getKey();
						    String taxName = entryTaxNames.getValue();

						    String labelTaxDesc = taxName + "|";
						    double taxAmt = 0.00;

						    boolean isApplicable = isApplicableTaxOnGroup(taxCode, groupCode);
						    if (isApplicable)
						    {
							double taxWiseGroupTotal = funGetTaxWiseGroupTotal(strBillNo, taxCode, mapBillWiseGroupTaxSettlementData);
							clsManagerReportBean objTaxDtl = mapBillWiseGroupTaxSettlementData.get(taxCode);
							double totalTaxAmtForGroup = objTaxDtl.getDblTaxAmt();

							if (taxWiseGroupTotal > 0)
							{
							    taxAmt = (totalTaxAmtForGroup / taxWiseGroupTotal) * groupNetTotal;
							}
						    }
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmt)) + "|", labelTaxDesc.length(), "Right"));
						}
					    }
					    pw.println();
					    for (int i = 0; i < lineCount; i++)
					    {
						pw.print("-");
					    }
					    pw.println();
					    pw.println();

					}
				    }
				    else
				    {
					continue;
				    }
				}
				/**
				 * print total line
				 */
				pw.println();
				for (int i = 0; i < maxLineCount; i++)
				{
				    pw.print("-");
				}
				pw.println();
				pw.print(objUtility.funPrintTextWithAlignment(strBillNo + " TOTALS", labelSettlement.length(), "Left"));
				pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalGroupNetTotal)) + "|", maxGroupNameLength, "Right"));
				double BillTotal = totalGroupNetTotal;
				if (mapBillWiseTaxeNames != null)
				{
				    for (Map.Entry<String, String> entryTaxNames : mapBillWiseTaxeNames.entrySet())
				    {
					String taxCode = entryTaxNames.getKey();
					String taxName = entryTaxNames.getValue();

					String labelTaxDesc = "  " + taxName + "|";

					clsManagerReportBean objTaxDtl = mapBillWiseGroupTaxSettlementData.get(taxCode);
					double totalTaxAmtForGroup = objTaxDtl.getDblTaxAmt();
					BillTotal += totalTaxAmtForGroup;
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalTaxAmtForGroup)) + "|", labelTaxDesc.length(), "Right"));
				    }
				}

				pw.print(objUtility.funPrintTextWithAlignment(Math.rint(BillTotal) + "|", horizontalTotalLabel.length(), "Center"));
				pw.println();
				for (int i = 0; i < maxLineCount; i++)
				{
				    pw.print("-");
				}
				pw.println();
				pw.println();
				
				System.out.println(strBillNo);
			    }
			    
			}


			return 1;
		 
		}
		
		private boolean isApplicableTaxOnGroup(String taxCode, String groupCode)
	    {
		boolean isApplicable = false;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder("select a.strTaxCode,a.strGroupCode,a.strGroupName,a.strApplicable "
			    + "from tbltaxongroup a "
			    + "where a.strTaxCode='" + taxCode + "' "
			    + "and a.strGroupCode='" + groupCode + "' "
			    + "and a.strApplicable='true' ");
		    List listIsApplicable = objBaseService.funGetList(sqlBuilder, "sql");
		    if (listIsApplicable.size()>0)
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
		
		 private double funGetTaxWiseGroupTotal(String billDate, String taxCode, Map<String, clsManagerReportBean> mapBillWiseGroupTaxSettlementData)
		    {
			double taxWiseGroupTotal = 0.00;

			try
			{
			    StringBuilder sql = new StringBuilder("select distinct(b.strGroupCode),b.strGroupName,a.strTaxOnGD "
				    + "from tbltaxhd a,tbltaxongroup b "
				    + "where a.strTaxCode=b.strTaxCode "
				    + "and b.strTaxCode='" + taxCode + "' "
				    + "and b.strApplicable='true' ");
			    List listIsApplicable = objBaseService.funGetList(sql, "sql");
			    if(listIsApplicable.size()>0)
			    {
					for(int i=0;i<listIsApplicable.size();i++)
					{
						Object[] obj = (Object[])listIsApplicable.get(i);
			    		String groupCode = obj[0].toString();//groupCode
						String taxOnGD = obj[2].toString();//taxOnGD
			
						if (mapBillWiseGroupTaxSettlementData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapBillWiseGroupTaxSettlementData.get(groupCode);
						    if (taxOnGD.equalsIgnoreCase("Gross"))
						    {
							taxWiseGroupTotal += objGroupDtl.getDblSubTotal();
						    }
						    else
						    {
							taxWiseGroupTotal += objGroupDtl.getDblNetTotal();
						    }
						}
			    	}
			    }
			   
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
			finally
			{
			    return taxWiseGroupTotal;
			}
		    }
	 
		 private int funGetLineCount(String billNo, String labelSettlement, String labelGroupName, String horizontalTotalLabel, Map<String, Map<String, clsManagerReportBean>> mapBillWiseData, Map<String, Map<String, String>> mapBillWiseSettlemetNames, Map<String, Map<String, String>> mapBillWiseTaxNames)
		    {

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(labelSettlement);
			stringBuilder.append(labelGroupName);

			Map<String, String> map = mapBillWiseTaxNames.get(billNo);
			if (map != null)
			{
			    for (String taxDesc : map.values())
			    {
				String labelTaxDesc = taxDesc + "|";
				stringBuilder.append(labelTaxDesc);
			    }
			}
			stringBuilder.append(horizontalTotalLabel);

			return stringBuilder.length();
		    }
		
}
