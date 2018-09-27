package com.sanguine.webpos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class clsPOSSettlementWiseGroupWiseBreakupReportController {

	
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
	 @RequestMapping(value = "/frmSettlementWiseGroupWiseBreakup", method = RequestMethod.GET)
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
				return new ModelAndView("frmSettlementWiseGroupWiseBreakup_1","command", new clsPOSReportBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmSettlementWiseGroupWiseBreakup","command", new clsPOSReportBean());
			}else {
				return null;
			}
			 
		}
	 
	 
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/rptPOSSettlementWiseGroupWiseBreakup", method = RequestMethod.POST)	
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
			    File file = new File(filePath + File.separator + "Temp" + File.separator + "Settlement Wise Group Wise Breakup.txt");
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
			    pw.println("Report :  Date Wise Settlement Wise Group Wise Tax Breakup");
			    pw.println("Reporting Date:" + "  " + fromDate + " " + "To" + " " + toDate);
			    pw.println();
			    pw.println(dashedLineOf150Chars);//line

				int ret = funSettlementWiseGroupWiseBreakup(fromDate,toDate,posCode,pw);

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
					    pw.println(" V ");
					}
					else
					{
					    pw.println(" m ");//windows
					}
				 }

			    pw.flush();
			    pw.close();

			    
			    resp.setContentType("text/plain");
			    
		        
			    resp.setHeader("Content-disposition","attachment; filename=Settlement Wise Group Wise Breakup.txt"); // Used to name the download file and its format
			   // resp.setHeader("Content-disposition","filename=Bill23424.txt"); 
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
		
		@RequestMapping(value = "/sendSettlementReportOnMail", method = RequestMethod.POST)
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
		    File file = new File(filePath + File.separator + "Temp" + File.separator + "Settlement Wise Group Wise Breakup.txt");
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
		    pw.println("Report : Date Wise Settlement Wise Group Wise Tax Breakup");
		    pw.println("Reporting Date:" + "  " + fromDate + " " + "To" + " " + toDate);
		    pw.println();
		    pw.println(dashedLineOf150Chars);//line
		    String posName="All";
			if(hmPOSData.containsKey(posCode))
			{
				posName = hmPOSData.get(posCode);
			}
			String[] frmDate = fromDate.split("-");
			String[] toDte = toDate.split("-");
			int ret = funSettlementWiseGroupWiseBreakup(frmDate[2]+"-"+frmDate[1]+"-"+frmDate[0],toDte[2]+"-"+toDte[1]+"-"+toDte[0],posCode,pw);

			 pw.println();
			 pw.println();
			 String printOS = "Windows";
			String printerType = "Inbuild";
			 pw.println();
			 pw.println();
			 if ("linux".equalsIgnoreCase(printOS))
			 {
				 pw.println(" V ");//Linux
			 }
			 else if ("windows".equalsIgnoreCase(printOS))
			 {
				if ("Inbuild".equalsIgnoreCase(printerType))
				{
				    pw.println(" V");
				}
				else
				{
				    pw.println(" m ");//windows
				}
			 }

		    pw.flush();
		    pw.close();
		    resp.setContentType("text/plain");
		    
	        
		   // resp.setHeader("Content-disposition","attachment; filename=Bill23424.txt"); // Used to name the download file and its format
		   resp.setHeader("Content-disposition","filename=Settlement Wise Group Wise Breakup.txt"); 
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
		

		public int funSettlementWiseGroupWiseBreakup(String fromDate,String toDate,String posCode, PrintWriter pw) throws Exception
		{
				pw.println();
				pw.println("Settlement Wise Group Wise Tax Breakup".toUpperCase());
				pw.println();
				pw.println("---------------------------");

				String sqlTip = "", sqlNoOfBill = "", sqlDiscount = "";
				StringBuilder sbSqlLiveFile = new StringBuilder();
				StringBuilder sbSqlQFile = new StringBuilder();

				Map<String, Map<String, clsManagerReportBean>> mapDateWiseData = new TreeMap<String, Map<String, clsManagerReportBean>>();
				Map<String, Map<String, String>> mapDateWiseSettlementNames = new TreeMap<String, Map<String, String>>();
				Map<String, Map<String, String>> mapDateWiseTaxNames = new TreeMap<String, Map<String, String>>();
				Map<String, Map<String, String>> mapDateWiseGroupNames = new TreeMap<String, Map<String, String>>();

				LinkedHashMap<String, String> mapAllGroups = new LinkedHashMap<>();
				LinkedHashMap<String, String> mapAllTaxes = new LinkedHashMap<>();
				LinkedHashMap<String, String> mapAllSettlements = new LinkedHashMap<>();

				int cntTax = 1;
				double totalTaxAmt = 0.00;
				double totalSettleAmt = 0.00;
				double totalDiscAmt = 0.00;
				double totalTipAmt = 0.00;
				double totalRoundOffAmt = 0.00;
				double totalBills = 0;

				sbSqlLiveFile.setLength(0);
				sbSqlLiveFile.append(" select c.strSettelmentCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
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
				sbSqlLiveFile.append(" GROUP BY date(a.dteBillDate),c.strSettelmentDesc "
					+ " order BY date(a.dteBillDate),c.strSettelmentDesc ");
				System.out.println(sbSqlLiveFile);

				List listSettleManager = objBaseService.funGetList(sbSqlLiveFile, "sql");
				if(listSettleManager.size()>0)
				{
					for(int i=0;i<listSettleManager.size();i++)
					{	
						Object[] obj = (Object[]) listSettleManager.get(i);
					    String settlementCode = obj[0].toString();
					    String settlementDesc = obj[1].toString();
					    double settleAmt = Double.parseDouble(obj[2].toString());
					    String billDate = obj[3].toString();
	
					    totalSettleAmt = totalSettleAmt + settleAmt;
	
					    if (mapDateWiseSettlementNames.containsKey(billDate))
					    {
						Map<String, String> mapSettlementNames = mapDateWiseSettlementNames.get(billDate);
	
						mapSettlementNames.put(settlementCode, settlementDesc);
					    }
					    else
					    {
						Map<String, String> mapSettlementNames = new TreeMap<>();
	
						mapSettlementNames.put(settlementCode, settlementDesc);
	
						mapDateWiseSettlementNames.put(billDate, mapSettlementNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						//put settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey(settlementCode))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get(settlementCode);
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
	
						    mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode(settlementCode);
						    objManagerReportBean.setStrSettlementDesc(settlementDesc);
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						    mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
						}
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalSettlementAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalSettlementAmt");
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
						    objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
						}
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						//put settlement dtl
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode(settlementCode);
						objManagerReportBean.setStrSettlementDesc(settlementDesc);
						objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
	
						//put total settlement dtl
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
						objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
						objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					} 
				}
				

				sbSqlQFile.setLength(0);
				sbSqlQFile.append(" select c.strSettelmentCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
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
				sbSqlQFile.append(" GROUP BY date(a.dteBillDate),c.strSettelmentDesc "
					+ " order BY date(a.dteBillDate),c.strSettelmentDesc ");
				listSettleManager = objBaseService.funGetList(sbSqlQFile, "sql");

				if(listSettleManager.size()>0)
				{
					for(int i=0;i<listSettleManager.size();i++)
					{
						Object[] obj = (Object[]) listSettleManager.get(i);
					    String settlementCode = obj[0].toString();
					    String settlementDesc = obj[1].toString();
					    double settleAmt =Double.parseDouble(obj[2].toString());
					    String billDate = obj[3].toString();
	
					    totalSettleAmt = totalSettleAmt + settleAmt;
	
					    if (mapDateWiseSettlementNames.containsKey(billDate))
					    {
						Map<String, String> mapSettlementNames = mapDateWiseSettlementNames.get(billDate);
	
						mapSettlementNames.put(settlementCode, settlementDesc);
					    }
					    else
					    {
						Map<String, String> mapSettlementNames = new TreeMap<>();
	
						mapSettlementNames.put(settlementCode, settlementDesc);
	
						mapDateWiseSettlementNames.put(billDate, mapSettlementNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						//put settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey(settlementCode))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get(settlementCode);
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
	
						    mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode(settlementCode);
						    objManagerReportBean.setStrSettlementDesc(settlementDesc);
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						    mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
						}
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalSettlementAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalSettlementAmt");
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
						    objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
						}
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
						//put settlement dtl
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode(settlementCode);
						objManagerReportBean.setStrSettlementDesc(settlementDesc);
						objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						mapDateWiseSettlementWiseData.put(settlementCode, objManagerReportBean);
	
						//put total settlement dtl
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("TotalSettlementAmt");
						objManagerReportBean.setStrSettlementDesc("TotalSettlementAmt");
						objManagerReportBean.setDblSettlementAmt(settleAmt);
	
						mapDateWiseSettlementWiseData.put("TotalSettlementAmt", objManagerReportBean);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					} 
				}
				
				/**
				 * live taxes
				 */
				StringBuilder sqlTax = new StringBuilder();
				sqlTax.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount) "
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
				sqlTax.append(" group by date(a.dteBillDate),c.strTaxCode");
				List listTaxDtl1 = objBaseService.funGetList(sqlTax, "sql");
				if(listTaxDtl1.size()>0)
				{
					for(int i=0;i<listTaxDtl1.size();i++)
					{
						Object[] obj = (Object[]) listTaxDtl1.get(i);
					    String billDate = obj[0].toString();
					    String taxCode = obj[1].toString();
					    String taxDesc =obj[2].toString();
					    double taxAmt = Double.parseDouble(obj[3].toString());
	
					    totalTaxAmt = totalTaxAmt + taxAmt;
	
					    if (mapDateWiseTaxNames.containsKey(billDate))
					    {
						Map<String, String> mapTaxNames = mapDateWiseTaxNames.get(billDate);
	
						mapTaxNames.put(taxCode, taxDesc);
					    }
					    else
					    {
						Map<String, String> mapTaxNames = new TreeMap<>();
	
						mapTaxNames.put(taxCode, taxDesc);
	
						mapDateWiseTaxNames.put(billDate, mapTaxNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						//put tax dtl
						if (mapDateWiseSettlementWiseData.containsKey(taxCode))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get(taxCode);
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
	
						    mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode(taxCode);
						    objManagerReportBean.setStrTaxDesc(taxDesc);
						    objManagerReportBean.setDblTaxAmt(taxAmt);
	
						    mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
						}
	
						//put total tax dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalTaxAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalTaxAmt");
						    objManagerReportBean.setDblTaxAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode("TotalTaxAmt");
						    objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
						    objManagerReportBean.setDblTaxAmt(taxAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
						}
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode(taxCode);
						objManagerReportBean.setStrTaxDesc(taxDesc);
						objManagerReportBean.setDblTaxAmt(taxAmt);
	
						//put total tax dtl
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode("TotalTaxAmt");
						objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
						objManagerReportBean.setDblTaxAmt(taxAmt);
	
						mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
				    }
				}
			

				/**
				 * Q taxes
				 */
				sqlTax.setLength(0);
				sqlTax.append( "select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount) "
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
				sqlTax.append( " group by date(a.dteBillDate),c.strTaxCode");
				listTaxDtl1 = objBaseService.funGetList(sqlTax, "sql");
				if(listTaxDtl1.size()>0)
				{
					for(int i=0;i<listTaxDtl1.size();i++)
					{
						Object[] obj = (Object[]) listTaxDtl1.get(i);
						 String billDate = obj[0].toString();
						    String taxCode = obj[1].toString();
						    String taxDesc =obj[2].toString();
						    double taxAmt = Double.parseDouble(obj[3].toString());
	
					    totalTaxAmt = totalTaxAmt + taxAmt;
	
					    if (mapDateWiseTaxNames.containsKey(billDate))
					    {
						Map<String, String> mapTaxNames = mapDateWiseTaxNames.get(billDate);
	
						mapTaxNames.put(taxCode, taxDesc);
					    }
					    else
					    {
						Map<String, String> mapTaxNames = new TreeMap<>();
	
						mapTaxNames.put(taxCode, taxDesc);
	
						mapDateWiseTaxNames.put(billDate, mapTaxNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						//put tax dtl
						if (mapDateWiseSettlementWiseData.containsKey(taxCode))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get(taxCode);
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
	
						    mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode(taxCode);
						    objManagerReportBean.setStrTaxDesc(taxDesc);
						    objManagerReportBean.setDblTaxAmt(taxAmt);
	
						    mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
						}
	
						//put total tax dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalTaxAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalTaxAmt");
						    objManagerReportBean.setDblTaxAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode("TotalTaxAmt");
						    objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
						    objManagerReportBean.setDblTaxAmt(taxAmt);
	
						    mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
						}
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode(taxCode);
						objManagerReportBean.setStrTaxDesc(taxDesc);
						objManagerReportBean.setDblTaxAmt(taxAmt);
	
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode("TotalTaxAmt");
						objManagerReportBean.setStrTaxDesc("TotalTaxAmt");
						objManagerReportBean.setDblTaxAmt(taxAmt);
	
						mapDateWiseSettlementWiseData.put("TotalTaxAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(taxCode, objManagerReportBean);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					}
				}
				

				//set discount,roundoff,tip
				sbSqlLiveFile.setLength(0);
				sbSqlLiveFile.append(" SELECT sum(a.dblDiscountAmt),sum(a.dblRoundOff),sum(a.dblTipAmount),count(*),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
					+ " from tblbillhd a "
					+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sbSqlLiveFile.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sbSqlLiveFile.append(" group by date(a.dteBillDate) ");
				System.out.println(sbSqlLiveFile);

				listSettleManager = objBaseService.funGetList(sbSqlLiveFile, "sql");
				if(listSettleManager.size()>0)
				{
					for(int i=0;i<listSettleManager.size();i++)
					{
						Object[] obj = (Object[]) listSettleManager.get(i);
					    double discAmt = Double.parseDouble(obj[0].toString());//discAmt
					    double roundOffAmt = Double.parseDouble(obj[1].toString());//roundOff
					    double tipAmt = Double.parseDouble(obj[2].toString());//tipAmt
					    int noOfBills = Integer.parseInt(obj[3].toString());//bill count
					    totalDiscAmt = totalDiscAmt + discAmt;
					    totalRoundOffAmt = totalRoundOffAmt + roundOffAmt;//roundOff
					    totalTipAmt = totalTipAmt + tipAmt;//tipAmt
					    totalBills = totalBills + noOfBills;//bill count
					    String billdate = obj[4].toString();//billDate
	
					    //discount
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("DiscAmt"))
						{
						    clsManagerReportBean objDiscAmt = mapDateWiseSettlementWiseData.get("DiscAmt");
						    objDiscAmt.setDblDiscAmt(objDiscAmt.getDblDiscAmt() + discAmt);
						}
						else
						{
						    clsManagerReportBean objDiscAmt = new clsManagerReportBean();
						    objDiscAmt.setDblDiscAmt(discAmt);
	
						    mapDateWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objDiscAmt = new clsManagerReportBean();
						objDiscAmt.setDblDiscAmt(discAmt);
	
						mapDateWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
	
					    //roundoff
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("RoundOffAmt"))
						{
						    clsManagerReportBean objRoundOffAmt = mapDateWiseSettlementWiseData.get("RoundOffAmt");
						    objRoundOffAmt.setDblRoundOffAmt(objRoundOffAmt.getDblRoundOffAmt() + roundOffAmt);
						}
						else
						{
						    clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
						    objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);
	
						    mapDateWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
						objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);
	
						mapDateWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
	
					    //tip
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("TipAmt"))
						{
						    clsManagerReportBean objTipAmt = mapDateWiseSettlementWiseData.get("TipAmt");
						    objTipAmt.setDblTipAmt(objTipAmt.getDblTipAmt() + tipAmt);
						}
						else
						{
						    clsManagerReportBean objTipAmt = new clsManagerReportBean();
						    objTipAmt.setDblTipAmt(tipAmt);
	
						    mapDateWiseSettlementWiseData.put("TipAmt", objTipAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objTipAmt = new clsManagerReportBean();
						objTipAmt.setDblTipAmt(tipAmt);
	
						mapDateWiseSettlementWiseData.put("TipAmt", objTipAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
					    //no of bills
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("NoOfBills"))
						{
						    clsManagerReportBean objNoOfBills = mapDateWiseSettlementWiseData.get("NoOfBills");
						    objNoOfBills.setIntNofOfBills(objNoOfBills.getIntNofOfBills() + noOfBills);
						}
						else
						{
						    clsManagerReportBean objNoOfBills = new clsManagerReportBean();
						    objNoOfBills.setIntNofOfBills(noOfBills);
	
						    mapDateWiseSettlementWiseData.put("NoOfBills", objNoOfBills);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objNoOfBills = new clsManagerReportBean();
						objNoOfBills.setIntNofOfBills(noOfBills);
	
						mapDateWiseSettlementWiseData.put("NoOfBills", objNoOfBills);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
					} 
				}
				

				sbSqlQFile.setLength(0);
				sbSqlQFile.append(" SELECT sum(a.dblDiscountAmt),sum(a.dblRoundOff),sum(a.dblTipAmount),count(*),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y')dteBillDate "
					+ " from tblqbillhd a "
					+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sbSqlQFile.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sbSqlQFile.append(" group by date(a.dteBillDate) ");
				System.out.println(sbSqlQFile);

				listSettleManager = objBaseService.funGetList(sbSqlQFile,"sql");
				if(listSettleManager.size()>0)
				{
					for(int i=0;i<listSettleManager.size();i++)
					{
						Object[] obj = (Object[]) listSettleManager.get(i);
					    double discAmt = Double.parseDouble(obj[0].toString());//discAmt
					    double roundOffAmt = Double.parseDouble(obj[1].toString());//roundOff
					    double tipAmt = Double.parseDouble(obj[2].toString());//tipAmt
					    int noOfBills = Integer.parseInt(obj[3].toString());//bill count
					    totalDiscAmt = totalDiscAmt + discAmt;
					    totalRoundOffAmt = totalRoundOffAmt + roundOffAmt;//roundOff
					    totalTipAmt = totalTipAmt + tipAmt;//tipAmt
					    totalBills = totalBills + noOfBills;//bill count
					    String billdate = obj[4].toString();//billDate
	
					    //discount
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("DiscAmt"))
						{
						    clsManagerReportBean objDiscAmt = mapDateWiseSettlementWiseData.get("DiscAmt");
						    objDiscAmt.setDblDiscAmt(objDiscAmt.getDblDiscAmt() + discAmt);
						}
						else
						{
						    clsManagerReportBean objDiscAmt = new clsManagerReportBean();
						    objDiscAmt.setDblDiscAmt(discAmt);
	
						    mapDateWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objDiscAmt = new clsManagerReportBean();
						objDiscAmt.setDblDiscAmt(discAmt);
	
						mapDateWiseSettlementWiseData.put("DiscAmt", objDiscAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
	
					    //roundoff
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("RoundOffAmt"))
						{
						    clsManagerReportBean objRoundOffAmt = mapDateWiseSettlementWiseData.get("RoundOffAmt");
						    objRoundOffAmt.setDblRoundOffAmt(objRoundOffAmt.getDblRoundOffAmt() + roundOffAmt);
						}
						else
						{
						    clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
						    objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);
	
						    mapDateWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objRoundOffAmt = new clsManagerReportBean();
						objRoundOffAmt.setDblRoundOffAmt(roundOffAmt);
	
						mapDateWiseSettlementWiseData.put("RoundOffAmt", objRoundOffAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
	
					    //tip
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("TipAmt"))
						{
						    clsManagerReportBean objTipAmt = mapDateWiseSettlementWiseData.get("TipAmt");
						    objTipAmt.setDblTipAmt(objTipAmt.getDblTipAmt() + tipAmt);
						}
						else
						{
						    clsManagerReportBean objTipAmt = new clsManagerReportBean();
						    objTipAmt.setDblTipAmt(tipAmt);
	
						    mapDateWiseSettlementWiseData.put("TipAmt", objTipAmt);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objTipAmt = new clsManagerReportBean();
						objTipAmt.setDblTipAmt(tipAmt);
	
						mapDateWiseSettlementWiseData.put("TipAmt", objTipAmt);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
					    //no of bills
					    if (mapDateWiseData.containsKey(billdate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billdate);
						if (mapDateWiseSettlementWiseData.containsKey("NoOfBills"))
						{
						    clsManagerReportBean objNoOfBills = mapDateWiseSettlementWiseData.get("NoOfBills");
						    objNoOfBills.setIntNofOfBills(objNoOfBills.getIntNofOfBills() + noOfBills);
						}
						else
						{
						    clsManagerReportBean objNoOfBills = new clsManagerReportBean();
						    objNoOfBills.setIntNofOfBills(noOfBills);
	
						    mapDateWiseSettlementWiseData.put("NoOfBills", objNoOfBills);
						}
	
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
						clsManagerReportBean objNoOfBills = new clsManagerReportBean();
						objNoOfBills.setIntNofOfBills(noOfBills);
	
						mapDateWiseSettlementWiseData.put("NoOfBills", objNoOfBills);
	
						mapDateWiseData.put(billdate, mapDateWiseSettlementWiseData);
					    }
					} 
				}
				
				/**
				 * fill live date wise group wise data
				 */
				StringBuilder sqlGroupData = new StringBuilder();

				sqlGroupData.setLength(0);
				sqlGroupData.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscountAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscountAmt)NetTotal "
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
				sqlGroupData.append("group by date(a.dteBillDate),e.strGroupCode ");
				List listGroupsData = objBaseService.funGetList(sqlGroupData, "sql");
				if(listGroupsData.size()>0)
				{
					for(int i=0;i<listGroupsData.size();i++)
					{
						Object[] obj = (Object[]) listGroupsData.get(i);
					    String billDate = obj[0].toString();//date
					    String groupCode = obj[1].toString();//groupCode
					    String groupName = obj[2].toString();//groupName
					    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
					    double discount = Double.parseDouble(obj[4].toString()); //discount
					    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal
	
					    if (mapDateWiseGroupNames.containsKey(billDate))
					    {
						Map<String, String> mapGroupNames = mapDateWiseGroupNames.get(billDate);
	
						mapGroupNames.put(groupCode, groupName);
					    }
					    else
					    {
						Map<String, String> mapGroupNames = new TreeMap<>();
	
						mapGroupNames.put(groupCode, groupName);
	
						mapDateWiseGroupNames.put(billDate, mapGroupNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						if (mapDateWiseSettlementWiseData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapDateWiseSettlementWiseData.get(groupCode);
	
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
	
						    mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
						}
	
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalGroupAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
						    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
						    objManagerReportBean.setStrGroupName("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(subTotal);
						    objManagerReportBean.setDblNetTotal(netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
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
	
						mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					}
				}
				

				/**
				 * fill live modifiers date wise group wise data
				 */
				sqlGroupData.setLength(0);
				sqlGroupData.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscAmt)NetTotal "
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
				sqlGroupData.append("group by date(a.dteBillDate),e.strGroupCode ");
				listGroupsData = objBaseService.funGetList(sqlGroupData,"sql");
				if(listGroupsData.size()>0)
				{
					for(int i=0;i<listGroupsData.size();i++)
					{
						Object[] obj = (Object[]) listGroupsData.get(i);
					    String billDate = obj[0].toString();//date
					    String groupCode = obj[1].toString();//groupCode
					    String groupName = obj[2].toString();//groupName
					    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
					    double discount = Double.parseDouble(obj[4].toString()); //discount
					    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

					    if (mapDateWiseGroupNames.containsKey(billDate))
					    {
						Map<String, String> mapGroupNames = mapDateWiseGroupNames.get(billDate);
	
						mapGroupNames.put(groupCode, groupName);
					    }
					    else
					    {
						Map<String, String> mapGroupNames = new TreeMap<>();
	
						mapGroupNames.put(groupCode, groupName);
	
						mapDateWiseGroupNames.put(billDate, mapGroupNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						if (mapDateWiseSettlementWiseData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapDateWiseSettlementWiseData.get(groupCode);
	
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
	
						    mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
						}
	
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalGroupAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
						    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
						    objManagerReportBean.setStrGroupName("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(subTotal);
						    objManagerReportBean.setDblNetTotal(netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
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
	
						mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					}
				}
				
				/**
				 * fill Q date wise group wise data
				 */
				sqlGroupData.setLength(0);
				sqlGroupData.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscountAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscountAmt)NetTotal "
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
				sqlGroupData.append("group by date(a.dteBillDate),e.strGroupCode ");
				listGroupsData = objBaseService.funGetList(sqlGroupData,"sql");
				if(listGroupsData.size()>0)
				{
					for(int i=0;i<listGroupsData.size();i++)
					{
						Object[] obj = (Object[]) listGroupsData.get(i);
					    String billDate = obj[0].toString();//date
					    String groupCode = obj[1].toString();//groupCode
					    String groupName = obj[2].toString();//groupName
					    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
					    double discount = Double.parseDouble(obj[4].toString()); //discount
					    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

					    if (mapDateWiseGroupNames.containsKey(billDate))
					    {
						Map<String, String> mapGroupNames = mapDateWiseGroupNames.get(billDate);
	
						mapGroupNames.put(groupCode, groupName);
					    }
					    else
					    {
						Map<String, String> mapGroupNames = new TreeMap<>();
	
						mapGroupNames.put(groupCode, groupName);
	
						mapDateWiseGroupNames.put(billDate, mapGroupNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						if (mapDateWiseSettlementWiseData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapDateWiseSettlementWiseData.get(groupCode);
	
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
	
						    mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
						}
	
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalGroupAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
						    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
						    objManagerReportBean.setStrGroupName("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(subTotal);
						    objManagerReportBean.setDblNetTotal(netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
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
	
						mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					}    
				}
				

				/**
				 * fill Q modifiers date wise group wise data
				 */
				sqlGroupData.setLength(0);
				sqlGroupData.append("select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName,sum(b.dblAmount)SubTotal,sum(b.dblDiscAmt)Discount,sum(b.dblAmount)-sum(b.dblDiscAmt)NetTotal "
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
				sqlGroupData.append("group by date(a.dteBillDate),e.strGroupCode ");
				listGroupsData =objBaseService.funGetList(sqlGroupData,"sql");
				if(listGroupsData.size()>0)
				{
					for(int i=0;i<listGroupsData.size();i++)
					{
						Object[] obj = (Object[]) listGroupsData.get(i);
					    String billDate = obj[0].toString();//date
					    String groupCode = obj[1].toString();//groupCode
					    String groupName = obj[2].toString();//groupName
					    double subTotal = Double.parseDouble(obj[3].toString()); //subTotal
					    double discount = Double.parseDouble(obj[4].toString()); //discount
					    double netTotal = Double.parseDouble(obj[5].toString()); //netTotal

					    if (mapDateWiseGroupNames.containsKey(billDate))
					    {
						Map<String, String> mapGroupNames = mapDateWiseGroupNames.get(billDate);
	
						mapGroupNames.put(groupCode, groupName);
					    }
					    else
					    {
						Map<String, String> mapGroupNames = new TreeMap<>();
	
						mapGroupNames.put(groupCode, groupName);
	
						mapDateWiseGroupNames.put(billDate, mapGroupNames);
					    }
	
					    if (mapDateWiseData.containsKey(billDate))
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = mapDateWiseData.get(billDate);
	
						if (mapDateWiseSettlementWiseData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapDateWiseSettlementWiseData.get(groupCode);
	
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
	
						    mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
						}
	
						//put total settlement dtl
						if (mapDateWiseSettlementWiseData.containsKey("TotalGroupAmt"))
						{
						    clsManagerReportBean objManagerReportBean = mapDateWiseSettlementWiseData.get("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(objManagerReportBean.getDblSubTotal() + subTotal);
						    objManagerReportBean.setDblNetTotal(objManagerReportBean.getDblNetTotal() + netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrGroupCode("TotalGroupAmt");
						    objManagerReportBean.setStrGroupName("TotalGroupAmt");
						    objManagerReportBean.setDblSubTotal(subTotal);
						    objManagerReportBean.setDblNetTotal(netTotal);
	
						    mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapDateWiseSettlementWiseData = new TreeMap<String, clsManagerReportBean>();
	
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
	
						mapDateWiseSettlementWiseData.put("TotalGroupAmt", objManagerReportBean);
	
						mapDateWiseSettlementWiseData.put(groupCode, objGroupDtl);
	
						mapDateWiseData.put(billDate, mapDateWiseSettlementWiseData);
					    }
					}   
				}
				

				/**
				 * start new logic
				 */
				StringBuilder sqlBillWiseGroupBuilder = new StringBuilder();
				StringBuilder sqlBillWiseSettlementBuilder = new StringBuilder();
				StringBuilder sqlBillWiseTaxBuilder = new StringBuilder();

				Map<String, Map<String, clsManagerReportBean>> mapBillWiseGroupBuilder = new HashMap<>();
				Map<String, Map<String, clsManagerReportBean>> mapBillWiseSettlementBuilder = new HashMap<>();
				Map<String, Map<String, clsManagerReportBean>> mapBillWiseTaxBuilder = new HashMap<>();

				//live bills
				sqlBillWiseGroupBuilder.setLength(0);
				sqlBillWiseGroupBuilder.append("SELECT a.strBillNo,DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName"
					+ ", SUM(b.dblAmount)SubTotal, SUM(b.dblDiscountAmt)Discount, SUM(b.dblAmount)- SUM(b.dblDiscountAmt)NetTotal,sum(b.dblTaxAmount) "
					+ "FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND b.strItemCode=c.strItemCode \n"
					+ "AND c.strSubGroupCode=d.strSubGroupCode \n"
					+ "AND d.strGroupCode=e.strGroupCode \n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseGroupBuilder.append("AND a.strPOSCode='" + posCode + "' ");
				}
				sqlBillWiseGroupBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),e.strGroupCode ");

				List listBillWiseGroupBuilder = objBaseService.funGetList(sqlBillWiseGroupBuilder, "sql");
				if(listBillWiseGroupBuilder.size()>0)
				{
					for(int i=0;i<listBillWiseGroupBuilder.size();i++)
					{	
					    Object[] obj = (Object[]) listBillWiseGroupBuilder.get(i);
						String billNo = obj[0].toString();//billNo
					    String billDate = obj[1].toString();//date
					    String groupCode = obj[2].toString();//groupCode
					    String groupName = obj[3].toString();//groupName
					    double subTotal = Double.parseDouble(obj[4].toString()); //subTotal
					    double discount = Double.parseDouble(obj[5].toString()); //discount
					    double netTotal = Double.parseDouble(obj[6].toString()); //netTotal
					    double taxTotal = Double.parseDouble(obj[7].toString()); //taxTotal
	
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllGroupKey = billNo + "!" + billDate + "!" + "All";
	
					    if (mapBillWiseGroupBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapGroupDtl = mapBillWiseGroupBuilder.get(billNoBillDateKey);
						if (mapGroupDtl.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get(groupCode);
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						    objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
						    objGroupDtl.setDblTaxAmt(taxTotal);
	
						    mapGroupDtl.put(groupCode, objGroupDtl);
						}
	
						if (mapGroupDtl.containsKey("All"))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get("All");
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						    objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
						    objGroupDtl.setDblTaxAmt(taxTotal);
	
						    mapGroupDtl.put("All", objGroupDtl);
						}
	
					    }
					    else
					    {
	
						Map<String, clsManagerReportBean> mapGroupDtl = new HashMap<>();
	
						clsManagerReportBean objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode(groupCode);
						objGroupDtl.setStrGroupName(groupName);
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
						objGroupDtl.setDblTaxAmt(taxTotal);
	
						mapGroupDtl.put(groupCode, objGroupDtl);
	
						objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode("All");
						objGroupDtl.setStrGroupName("All");
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
						objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
	
						mapGroupDtl.put("All", objGroupDtl);
	
						mapBillWiseGroupBuilder.put(billNoBillDateKey, mapGroupDtl);
					    }
					}    
				}
				

				//live bill modifires
				sqlBillWiseGroupBuilder.setLength(0);
				sqlBillWiseGroupBuilder.append("SELECT a.strBillNo,DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName, SUM(b.dblAmount)SubTotal, SUM(b.dblDiscAmt)Discount, SUM(b.dblAmount)- SUM(b.dblDiscAmt)NetTotal\n"
					+ "FROM tblbillhd a,tblbillmodifierdtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND left(b.strItemCode,7)=c.strItemCode \n"
					+ "AND c.strSubGroupCode=d.strSubGroupCode \n"
					+ "AND d.strGroupCode=e.strGroupCode "
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseGroupBuilder.append("AND a.strPOSCode='" + posCode + "' ");
				}
				sqlBillWiseGroupBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),e.strGroupCode ");

				listBillWiseGroupBuilder = objBaseService.funGetList(sqlBillWiseGroupBuilder,"sql");
				if(listBillWiseGroupBuilder.size()>0)
				{
					for(int i=0;i<listBillWiseGroupBuilder.size();i++)
					{	
					    Object[] obj = (Object[]) listBillWiseGroupBuilder.get(i);
						String billNo = obj[0].toString();//billNo
					    String billDate = obj[1].toString();//date
					    String groupCode = obj[2].toString();//groupCode
					    String groupName = obj[3].toString();//groupName
					    double subTotal = Double.parseDouble(obj[4].toString()); //subTotal
					    double discount = Double.parseDouble(obj[5].toString()); //discount
					    double netTotal = Double.parseDouble(obj[6].toString()); //netTotal
					   

					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllGroupKey = billNo + "!" + billDate + "!" + "All";
	
					    if (mapBillWiseGroupBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapGroupDtl = mapBillWiseGroupBuilder.get(billNoBillDateKey);
						if (mapGroupDtl.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get(groupCode);
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
	
						    mapGroupDtl.put(groupCode, objGroupDtl);
						}
	
						if (mapGroupDtl.containsKey("All"))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get("All");
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
	
						    mapGroupDtl.put("All", objGroupDtl);
						}
	
					    }
					    else
					    {
	
						Map<String, clsManagerReportBean> mapGroupDtl = new HashMap<>();
	
						clsManagerReportBean objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode(groupCode);
						objGroupDtl.setStrGroupName(groupName);
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
	
						mapGroupDtl.put(groupCode, objGroupDtl);
	
						objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode("All");
						objGroupDtl.setStrGroupName("All");
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
	
						mapGroupDtl.put("All", objGroupDtl);
	
						mapBillWiseGroupBuilder.put(billNoBillDateKey, mapGroupDtl);
					    }
					}   
				}
				
				//Q bills
				sqlBillWiseGroupBuilder.setLength(0);
				sqlBillWiseGroupBuilder.append("SELECT a.strBillNo,DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName"
					+ ", SUM(b.dblAmount)SubTotal, SUM(b.dblDiscountAmt)Discount, SUM(b.dblAmount)- SUM(b.dblDiscountAmt)NetTotal "
					+ ",sum(b.dblTaxAmount) "
					+ "FROM tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND b.strItemCode=c.strItemCode \n"
					+ "AND c.strSubGroupCode=d.strSubGroupCode \n"
					+ "AND d.strGroupCode=e.strGroupCode \n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseGroupBuilder.append("AND a.strPOSCode='" + posCode + "' ");
				}
				sqlBillWiseGroupBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),e.strGroupCode ");

				listBillWiseGroupBuilder = objBaseService.funGetList(sqlBillWiseGroupBuilder,"sql");
				if(listBillWiseGroupBuilder.size()>0)
				{
					for(int i=0;i<listBillWiseGroupBuilder.size();i++)
					{	
					    Object[] obj = (Object[]) listBillWiseGroupBuilder.get(i);
						String billNo = obj[0].toString();//billNo
					    String billDate = obj[1].toString();//date
					    String groupCode = obj[2].toString();//groupCode
					    String groupName = obj[3].toString();//groupName
					    double subTotal = Double.parseDouble(obj[4].toString()); //subTotal
					    double discount = Double.parseDouble(obj[5].toString()); //discount
					    double netTotal = Double.parseDouble(obj[6].toString()); //netTotal
					    double taxTotal = Double.parseDouble(obj[7].toString()); //taxTotal
						    
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllGroupKey = billNo + "!" + billDate + "!" + "All";
	
					    if (mapBillWiseGroupBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapGroupDtl = mapBillWiseGroupBuilder.get(billNoBillDateKey);
						if (mapGroupDtl.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get(groupCode);
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						    objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
						    objGroupDtl.setDblTaxAmt(taxTotal);
	
						    mapGroupDtl.put(groupCode, objGroupDtl);
						}
	
						if (mapGroupDtl.containsKey("All"))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get("All");
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						    objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
						    objGroupDtl.setDblTaxAmt(taxTotal);
	
						    mapGroupDtl.put("All", objGroupDtl);
						}
	
					    }
					    else
					    {
	
						Map<String, clsManagerReportBean> mapGroupDtl = new HashMap<>();
	
						clsManagerReportBean objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode(groupCode);
						objGroupDtl.setStrGroupName(groupName);
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
						objGroupDtl.setDblTaxAmt(taxTotal);
	
						mapGroupDtl.put(groupCode, objGroupDtl);
	
						objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode("All");
						objGroupDtl.setStrGroupName("All");
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
						objGroupDtl.setDblTaxAmt(objGroupDtl.getDblTaxAmt() + taxTotal);
	
						mapGroupDtl.put("All", objGroupDtl);
	
						mapBillWiseGroupBuilder.put(billNoBillDateKey, mapGroupDtl);
					    }
					}    

				}
			

				//Q bill modifires
				sqlBillWiseGroupBuilder.setLength(0);
				sqlBillWiseGroupBuilder.append("SELECT a.strBillNo,DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),e.strGroupCode,e.strGroupName, SUM(b.dblAmount)SubTotal, SUM(b.dblDiscAmt)Discount, SUM(b.dblAmount)- SUM(b.dblDiscAmt)NetTotal\n"
					+ "FROM tblqbillhd a,tblqbillmodifierdtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND left(b.strItemCode,7)=c.strItemCode \n"
					+ "AND c.strSubGroupCode=d.strSubGroupCode \n"
					+ "AND d.strGroupCode=e.strGroupCode "
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseGroupBuilder.append("AND a.strPOSCode='" + posCode + "' ");
				}
				sqlBillWiseGroupBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),e.strGroupCode ");

				listBillWiseGroupBuilder = objBaseService.funGetList(sqlBillWiseGroupBuilder,"sql");
				if(listBillWiseGroupBuilder.size()>0)
				{
					for(int i=0;i<listBillWiseGroupBuilder.size();i++)
					{	
					    Object[] obj = (Object[]) listBillWiseGroupBuilder.get(i);
						String billNo = obj[0].toString();//billNo
					    String billDate = obj[1].toString();//date
					    String groupCode = obj[2].toString();//groupCode
					    String groupName = obj[3].toString();//groupName
					    double subTotal = Double.parseDouble(obj[4].toString()); //subTotal
					    double discount = Double.parseDouble(obj[5].toString()); //discount
					    double netTotal = Double.parseDouble(obj[6].toString()); //netTotal
					   

					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllGroupKey = billNo + "!" + billDate + "!" + "All";
	
					    if (mapBillWiseGroupBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapGroupDtl = mapBillWiseGroupBuilder.get(billNoBillDateKey);
						if (mapGroupDtl.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get(groupCode);
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
	
						    mapGroupDtl.put(groupCode, objGroupDtl);
						}
	
						if (mapGroupDtl.containsKey("All"))
						{
						    clsManagerReportBean objGroupDtl = mapGroupDtl.get("All");
	
						    objGroupDtl.setDblSubTotal(objGroupDtl.getDblSubTotal() + subTotal);
						    objGroupDtl.setDblDisAmt(objGroupDtl.getDblDisAmt() + discount);
						    objGroupDtl.setDblNetTotal(objGroupDtl.getDblNetTotal() + netTotal);
						}
						else
						{
						    clsManagerReportBean objGroupDtl = new clsManagerReportBean();
						    objGroupDtl.setDblSubTotal(subTotal);
						    objGroupDtl.setDblDisAmt(discount);
						    objGroupDtl.setDblNetTotal(netTotal);
	
						    mapGroupDtl.put("All", objGroupDtl);
						}
	
					    }
					    else
					    {
	
						Map<String, clsManagerReportBean> mapGroupDtl = new HashMap<>();
	
						clsManagerReportBean objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode(groupCode);
						objGroupDtl.setStrGroupName(groupName);
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
	
						mapGroupDtl.put(groupCode, objGroupDtl);
	
						objGroupDtl = new clsManagerReportBean();
	
						objGroupDtl.setStrBillNo(billNo);
						objGroupDtl.setDteBill(billDate);
						objGroupDtl.setStrGroupCode("All");
						objGroupDtl.setStrGroupName("All");
						objGroupDtl.setDblSubTotal(subTotal);
						objGroupDtl.setDblDisAmt(discount);
						objGroupDtl.setDblNetTotal(netTotal);
	
						mapGroupDtl.put("All", objGroupDtl);
	
						mapBillWiseGroupBuilder.put(billNoBillDateKey, mapGroupDtl);
					    }
					}    

				}
				

				//live settlement
				sqlBillWiseSettlementBuilder.setLength(0);
				sqlBillWiseSettlementBuilder.append("SELECT a.strBillNo,c.strSettelmentCode,c.strSettelmentDesc, SUM(b.dblSettlementAmt), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y')dteBillDate\n"
					+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND b.strSettlementCode=c.strSettelmentCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n"
					+ "AND c.strSettelmentType!='Complementary' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseSettlementBuilder.append("AND a.strPOSCode='" + posCode + "'");
				}
				sqlBillWiseSettlementBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),c.strSettelmentDesc\n"
					+ "ORDER BY a.strBillNo,DATE(a.dteBillDate),c.strSettelmentDesc ");
				List listBillWiseSettlement = objBaseService.funGetList(sqlBillWiseSettlementBuilder, "sql");
				if(listBillWiseSettlement.size()>0)
				{
					for(int i=0;i<listBillWiseSettlement.size();i++)
					{
						Object[] obj = (Object[]) listBillWiseSettlement.get(i);
					    String billNo = obj[0].toString();//billNo
					    String settlementCode = obj[1].toString();//settleCode
					    String settlementDesc = obj[2].toString();//sett name
					    double settleAmt = Double.parseDouble(obj[3].toString());//sett amt
					    String billDate = obj[4].toString();//bill date
	
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllSettleKey = billNo + "!" + billDate + "!" + "All";
	
					    //bill wise settlement wise
					    if (mapBillWiseSettlementBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapSettlement = mapBillWiseSettlementBuilder.get(billNoBillDateKey);
	
						if (mapSettlement.containsKey(settlementCode))
						{
						    clsManagerReportBean objManagerReportBean = mapSettlement.get(settlementCode);
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode(settlementCode);
						    objManagerReportBean.setStrSettlementDesc(settlementDesc);
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapSettlement.put(settlementCode, objManagerReportBean);
						}
	
						if (mapSettlement.containsKey("All"))
						{
						    clsManagerReportBean objManagerReportBean = mapSettlement.get("All");
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("All");
						    objManagerReportBean.setStrSettlementDesc("All");
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapSettlement.put("All", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapSettlement = new HashMap<>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode(settlementCode);
						objManagerReportBean.setStrSettlementDesc(settlementDesc);
						objManagerReportBean.setDblSettlementAmt(settleAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapSettlement.put(settlementCode, objManagerReportBean);
	
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("All");
						objManagerReportBean.setStrSettlementDesc("All");
						objManagerReportBean.setDblSettlementAmt(settleAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapSettlement.put("All", objManagerReportBean);
	
						mapBillWiseSettlementBuilder.put(billNoBillDateKey, mapSettlement);
					    }
					}
				}
				

				//Q settlement
				sqlBillWiseSettlementBuilder.setLength(0);
				sqlBillWiseSettlementBuilder.append("SELECT a.strBillNo,c.strSettelmentCode,c.strSettelmentDesc, SUM(b.dblSettlementAmt), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y')dteBillDate\n"
					+ "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND b.strSettlementCode=c.strSettelmentCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n"
					+ "AND c.strSettelmentType!='Complementary' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseSettlementBuilder.append("AND a.strPOSCode='" + posCode + "'");
				}
				sqlBillWiseSettlementBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),c.strSettelmentDesc\n"
					+ "ORDER BY a.strBillNo,DATE(a.dteBillDate),c.strSettelmentDesc ");
				listBillWiseSettlement = objBaseService.funGetList(sqlBillWiseSettlementBuilder,"sql");
				if(listBillWiseSettlement.size()>0)
				{
					for(int i=0;i<listBillWiseSettlement.size();i++)
					{
						Object[] obj = (Object[]) listBillWiseSettlement.get(i);
					    String billNo = obj[0].toString();//billNo
					    String settlementCode = obj[1].toString();//settleCode
					    String settlementDesc = obj[2].toString();//sett name
					    double settleAmt = Double.parseDouble(obj[3].toString());//sett amt
					    String billDate = obj[4].toString();//bill date
	
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllSettleKey = billNo + "!" + billDate + "!" + "All";
	
					    //bill wise settlement wise
					    if (mapBillWiseSettlementBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapSettlement = mapBillWiseSettlementBuilder.get(billNoBillDateKey);
	
						if (mapSettlement.containsKey(settlementCode))
						{
	
						    clsManagerReportBean objManagerReportBean = mapSettlement.get(settlementCode);
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode(settlementCode);
						    objManagerReportBean.setStrSettlementDesc(settlementDesc);
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapSettlement.put(settlementCode, objManagerReportBean);
						}
	
						if (mapSettlement.containsKey("All"))
						{
						    clsManagerReportBean objManagerReportBean = mapSettlement.get("All");
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblSettlementAmt() + settleAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("All");
						    objManagerReportBean.setStrSettlementDesc("All");
						    objManagerReportBean.setDblSettlementAmt(settleAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapSettlement.put("All", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapSettlement = new HashMap<>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode(settlementCode);
						objManagerReportBean.setStrSettlementDesc(settlementDesc);
						objManagerReportBean.setDblSettlementAmt(settleAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapSettlement.put(settlementCode, objManagerReportBean);
	
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("All");
						objManagerReportBean.setStrSettlementDesc("All");
						objManagerReportBean.setDblSettlementAmt(settleAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapSettlement.put("All", objManagerReportBean);
	
						mapBillWiseSettlementBuilder.put(billNoBillDateKey, mapSettlement);
					    }
					}    
				}
				

				//live taxes
				sqlBillWiseTaxBuilder.setLength(0);
				sqlBillWiseTaxBuilder.append("SELECT a.strBillNo,c.strTaxCode,c.strTaxDesc, SUM(b.dblTaxAmount), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y')dteBillDate "
					+ "FROM tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
					+ "WHERE a.strBillNo=b.strBillNo  "
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
					+ "AND b.strTaxCode=c.strTaxCode  "
					+ "AND a.strClientCode=b.strClientCode  "
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseTaxBuilder.append("AND a.strPOSCode='" + posCode + "'");
				}
				sqlBillWiseTaxBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),c.strTaxDesc\n"
					+ "ORDER BY a.strBillNo,DATE(a.dteBillDate),c.strTaxDesc ");
				List listBillWiseTaxes = objBaseService.funGetList(sqlBillWiseTaxBuilder, "sql");
				if(listBillWiseTaxes.size()>0)
				{
					for(int i=0;i<listBillWiseTaxes.size();i++)
					{
						Object[] obj = (Object[]) listBillWiseTaxes.get(i);
					    String billNo = obj[0].toString();//billNo
					    String taxCode = obj[1].toString();//taxCode
					    String taxDesc = obj[2].toString();//taxDesc name
					    double taxAmt = Double.parseDouble(obj[3].toString());//taxAmt amt
					    String billDate =obj[4].toString();//bill date
	
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllSettleKey = billNo + "!" + billDate + "!" + "All";
	
					    //bill wise settlement wise
					    if (mapBillWiseTaxBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapTaxes = mapBillWiseTaxBuilder.get(billNoBillDateKey);
	
						if (mapTaxes.containsKey(taxCode))
						{
						    clsManagerReportBean objManagerReportBean = mapTaxes.get(taxCode);
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode(taxCode);
						    objManagerReportBean.setStrTaxDesc(taxDesc);
						    objManagerReportBean.setDblTaxAmt(taxAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapTaxes.put(taxCode, objManagerReportBean);
						}
	
						if (mapTaxes.containsKey("All"))
						{
						    clsManagerReportBean objManagerReportBean = mapTaxes.get("All");
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("All");
						    objManagerReportBean.setStrSettlementDesc("All");
						    objManagerReportBean.setDblTaxAmt(taxAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapTaxes.put("All", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapTaxes = new HashMap<>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode(taxCode);
						objManagerReportBean.setStrTaxDesc(taxDesc);
						objManagerReportBean.setDblTaxAmt(taxAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapTaxes.put(taxCode, objManagerReportBean);
	
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("All");
						objManagerReportBean.setStrSettlementDesc("All");
						objManagerReportBean.setDblSettlementAmt(taxAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapTaxes.put("All", objManagerReportBean);
	
						mapBillWiseTaxBuilder.put(billNoBillDateKey, mapTaxes);
					    }
					}
				}
				
				//Q taxes
				sqlBillWiseTaxBuilder.setLength(0);
				sqlBillWiseTaxBuilder.append("SELECT a.strBillNo,c.strTaxCode,c.strTaxDesc, SUM(b.dblTaxAmount), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y')dteBillDate "
					+ "FROM tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c "
					+ "WHERE a.strBillNo=b.strBillNo  "
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
					+ "AND b.strTaxCode=c.strTaxCode  "
					+ "AND a.strClientCode=b.strClientCode  "
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBillWiseTaxBuilder.append("AND a.strPOSCode='" + posCode + "'");
				}
				sqlBillWiseTaxBuilder.append("GROUP BY a.strBillNo,DATE(a.dteBillDate),c.strTaxDesc\n"
					+ "ORDER BY a.strBillNo,DATE(a.dteBillDate),c.strTaxDesc ");
				listBillWiseTaxes = objBaseService.funGetList(sqlBillWiseTaxBuilder,"sql");
				if(listBillWiseTaxes.size()>0)
				{
					for(int i=0;i<listBillWiseTaxes.size();i++)
					{
						Object[] obj = (Object[]) listBillWiseTaxes.get(i);
					    String billNo = obj[0].toString();//billNo
					    String taxCode = obj[1].toString();//taxCode
					    String taxDesc = obj[2].toString();//taxDesc name
					    double taxAmt = Double.parseDouble(obj[3].toString());//taxAmt amt
					    String billDate =obj[4].toString();//bill date
					    String billNoBillDateKey = billNo + "!" + billDate;
					    String billNoBillDateAllSettleKey = billNo + "!" + billDate + "!" + "All";
	
					    //bill wise settlement wise
					    if (mapBillWiseTaxBuilder.containsKey(billNoBillDateKey))
					    {
						Map<String, clsManagerReportBean> mapTaxes = mapBillWiseTaxBuilder.get(billNoBillDateKey);
	
						if (mapTaxes.containsKey(taxCode))
						{
						    clsManagerReportBean objManagerReportBean = mapTaxes.get(taxCode);
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrTaxCode(taxCode);
						    objManagerReportBean.setStrTaxDesc(taxDesc);
						    objManagerReportBean.setDblTaxAmt(taxAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapTaxes.put(taxCode, objManagerReportBean);
						}
	
						if (mapTaxes.containsKey("All"))
						{
						    clsManagerReportBean objManagerReportBean = mapTaxes.get("All");
	
						    objManagerReportBean.setDblSettlementAmt(objManagerReportBean.getDblTaxAmt() + taxAmt);
						}
						else
						{
						    clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						    objManagerReportBean.setStrSettlementCode("All");
						    objManagerReportBean.setStrSettlementDesc("All");
						    objManagerReportBean.setDblTaxAmt(taxAmt);
						    objManagerReportBean.setStrBillNo(billNo);
						    objManagerReportBean.setDteBill(billDate);
	
						    mapTaxes.put("All", objManagerReportBean);
						}
					    }
					    else
					    {
						Map<String, clsManagerReportBean> mapTaxes = new HashMap<>();
	
						clsManagerReportBean objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrTaxCode(taxCode);
						objManagerReportBean.setStrTaxDesc(taxDesc);
						objManagerReportBean.setDblTaxAmt(taxAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapTaxes.put(taxCode, objManagerReportBean);
	
						objManagerReportBean = new clsManagerReportBean();
						objManagerReportBean.setStrSettlementCode("All");
						objManagerReportBean.setStrSettlementDesc("All");
						objManagerReportBean.setDblSettlementAmt(taxAmt);
						objManagerReportBean.setStrBillNo(billNo);
						objManagerReportBean.setDteBill(billDate);
	
						mapTaxes.put("All", objManagerReportBean);
	
						mapBillWiseTaxBuilder.put(billNoBillDateKey, mapTaxes);
					    }
					}    
				}
			

				/**
				 * calculation logic
				 */
				Map<String, Map<String, clsManagerReportBean>> mapDateWiseGroupWiseSettlementData = new HashMap<>();
				Map<String, Map<String, clsManagerReportBean>> mapDateWiseGroupWiseSettlementWiseTaxData = new HashMap<>();

				if (mapDateWiseData.size() > 0)
				{
				    for (Map.Entry<String, Map<String, clsManagerReportBean>> billWiseGroupIterator : mapBillWiseGroupBuilder.entrySet())
				    {
					String billNoBillDateKey = billWiseGroupIterator.getKey();
					String billDate = billNoBillDateKey.split("!")[1];

					Map<String, clsManagerReportBean> mapBillGroups = billWiseGroupIterator.getValue();

					for (Map.Entry<String, clsManagerReportBean> entryOfBillGroups : mapBillGroups.entrySet())
					{
					    String groupCode = entryOfBillGroups.getKey();
					    clsManagerReportBean objGroupDtl = entryOfBillGroups.getValue();

					    clsManagerReportBean objAllGroupsDtl = mapBillGroups.get("All");

					    if (!mapBillWiseSettlementBuilder.containsKey(billNoBillDateKey))
					    {
						//System.out.println("billNoBillDateKey->"+billNoBillDateKey);
						break;
					    }

					    Map<String, clsManagerReportBean> mapSettlementDtl = mapBillWiseSettlementBuilder.get(billNoBillDateKey);
					    for (Map.Entry<String, clsManagerReportBean> entryOfBillSettlements : mapSettlementDtl.entrySet())
					    {
						String settlementCode = entryOfBillSettlements.getKey();
						clsManagerReportBean objSettlementDtl = entryOfBillSettlements.getValue();

						clsManagerReportBean objAllSettlementsDtl = mapSettlementDtl.get("All");

						double settlementAmtForThisGroup = 0;
						if (objAllGroupsDtl.getDblNetTotal() > 0)
						{
						    settlementAmtForThisGroup = (objSettlementDtl.getDblSettlementAmt() / objAllGroupsDtl.getDblNetTotal()) * objGroupDtl.getDblNetTotal();
						}
						double netTotalAmtForThisSettlement = 0;
						if (objAllSettlementsDtl.getDblSettlementAmt() > 0)
						{
						    netTotalAmtForThisSettlement = (objSettlementDtl.getDblSettlementAmt() / objAllSettlementsDtl.getDblSettlementAmt()) * objGroupDtl.getDblNetTotal();
						}

						if (mapBillWiseTaxBuilder.containsKey(billNoBillDateKey))
						{
						    Map<String, clsManagerReportBean> mapTaxes = mapBillWiseTaxBuilder.get(billNoBillDateKey);

						    for (Map.Entry<String, clsManagerReportBean> taxEntry : mapTaxes.entrySet())
						    {
							String taxCode = taxEntry.getKey();
							clsManagerReportBean objTax = taxEntry.getValue();
							double taxAmtForThisSettlement = 0;
							if (objAllGroupsDtl.getDblNetTotal() > 0)
							{
							    taxAmtForThisSettlement = (objTax.getDblTaxAmt() / objAllGroupsDtl.getDblNetTotal()) * netTotalAmtForThisSettlement;
							}

							String groupWiseSettlementTaxKey = groupCode + "!" + settlementCode + "!" + taxCode;
							//filltax data
							if (mapDateWiseGroupWiseSettlementWiseTaxData.containsKey(billDate))
							{
							    Map<String, clsManagerReportBean> mapGroupWiseSettlementTaxData = mapDateWiseGroupWiseSettlementWiseTaxData.get(billDate);
							    if (mapGroupWiseSettlementTaxData.containsKey(groupWiseSettlementTaxKey))
							    {
								clsManagerReportBean objGroupWiseSettlementTaxData = mapGroupWiseSettlementTaxData.get(groupWiseSettlementTaxKey);

								objGroupWiseSettlementTaxData.setDblTaxAmt(objGroupWiseSettlementTaxData.getDblTaxAmt() + taxAmtForThisSettlement);

							    }
							    else
							    {
								clsManagerReportBean objGroupWiseSettlementTaxData = new clsManagerReportBean();
								objGroupWiseSettlementTaxData.setStrGroupCode(groupCode);
								objGroupWiseSettlementTaxData.setStrGroupName(objGroupDtl.getStrGroupName());
								objGroupWiseSettlementTaxData.setStrSettlementCode(settlementCode);
								objGroupWiseSettlementTaxData.setStrSettlementDesc(objSettlementDtl.getStrSettlementDesc());
								objGroupWiseSettlementTaxData.setDblTaxAmt(taxAmtForThisSettlement);
								objGroupWiseSettlementTaxData.setStrTaxDesc(objTax.getStrTaxDesc());

								mapGroupWiseSettlementTaxData.put(groupWiseSettlementTaxKey, objGroupWiseSettlementTaxData);

								mapDateWiseGroupWiseSettlementWiseTaxData.put(billDate, mapGroupWiseSettlementTaxData);
							    }
							}
							else//no billdate
							{
							    Map<String, clsManagerReportBean> mapGroupWiseSettlementTaxData = new HashMap<>();
							    if (mapGroupWiseSettlementTaxData.containsKey(groupWiseSettlementTaxKey))
							    {
								clsManagerReportBean objGroupWiseSettlementTaxData = mapGroupWiseSettlementTaxData.get(groupWiseSettlementTaxKey);

								objGroupWiseSettlementTaxData.setDblTaxAmt(objGroupWiseSettlementTaxData.getDblTaxAmt() + taxAmtForThisSettlement);

							    }
							    else
							    {
								clsManagerReportBean objGroupWiseSettlementTaxData = new clsManagerReportBean();
								objGroupWiseSettlementTaxData.setStrGroupCode(groupCode);
								objGroupWiseSettlementTaxData.setStrGroupName(objGroupDtl.getStrGroupName());
								objGroupWiseSettlementTaxData.setStrSettlementCode(settlementCode);
								objGroupWiseSettlementTaxData.setStrSettlementDesc(objSettlementDtl.getStrSettlementDesc());
								objGroupWiseSettlementTaxData.setDblTaxAmt(taxAmtForThisSettlement);
								objGroupWiseSettlementTaxData.setStrTaxDesc(objTax.getStrTaxDesc());

								mapGroupWiseSettlementTaxData.put(groupWiseSettlementTaxKey, objGroupWiseSettlementTaxData);

								mapDateWiseGroupWiseSettlementWiseTaxData.put(billDate, mapGroupWiseSettlementTaxData);
							    }
							}
						    }
						}
						//fill settlement data
						if (mapDateWiseGroupWiseSettlementData.containsKey(billDate))
						{
						    Map<String, clsManagerReportBean> mapGroupWiseSettlementData = mapDateWiseGroupWiseSettlementData.get(billDate);

						    String groupWiseSettlementKey = groupCode + "!" + settlementCode;

						    if (mapGroupWiseSettlementData.containsKey(groupWiseSettlementKey))
						    {
							clsManagerReportBean objGroupWiseSettlementData = mapGroupWiseSettlementData.get(groupWiseSettlementKey);

							objGroupWiseSettlementData.setDblGroupSettlementAmt(objGroupWiseSettlementData.getDblGroupSettlementAmt() + settlementAmtForThisGroup);
							objGroupWiseSettlementData.setDblNetTotal(objGroupWiseSettlementData.getDblNetTotal() + netTotalAmtForThisSettlement);

							mapGroupWiseSettlementData.put(groupWiseSettlementKey, objGroupWiseSettlementData);
						    }
						    else
						    {
							clsManagerReportBean objGroupWiseSettlementData = new clsManagerReportBean();
							objGroupWiseSettlementData.setStrGroupCode(groupCode);
							objGroupWiseSettlementData.setStrGroupName(objGroupDtl.getStrGroupName());
							objGroupWiseSettlementData.setStrSettlementCode(settlementCode);
							objGroupWiseSettlementData.setStrSettlementDesc(objSettlementDtl.getStrSettlementDesc());

							objGroupWiseSettlementData.setStrGroupCodeSettlementCode(groupWiseSettlementKey);
							objGroupWiseSettlementData.setDblGroupSettlementAmt(settlementAmtForThisGroup);
							objGroupWiseSettlementData.setDblNetTotal(netTotalAmtForThisSettlement);

							mapGroupWiseSettlementData.put(groupWiseSettlementKey, objGroupWiseSettlementData);
						    }
						}
						else
						{
						    Map<String, clsManagerReportBean> mapGroupWiseSettlementData = new HashMap<>();

						    String groupWiseSettlementKey = groupCode + "!" + settlementCode;

						    clsManagerReportBean objGroupWiseSettlementData = new clsManagerReportBean();
						    objGroupWiseSettlementData.setStrGroupCode(groupCode);
						    objGroupWiseSettlementData.setStrGroupName(objGroupDtl.getStrGroupName());
						    objGroupWiseSettlementData.setStrSettlementCode(settlementCode);
						    objGroupWiseSettlementData.setStrSettlementDesc(objSettlementDtl.getStrSettlementDesc());

						    objGroupWiseSettlementData.setStrGroupCodeSettlementCode(groupWiseSettlementKey);
						    objGroupWiseSettlementData.setDblGroupSettlementAmt(settlementAmtForThisGroup);
						    objGroupWiseSettlementData.setDblNetTotal(netTotalAmtForThisSettlement);

						    mapGroupWiseSettlementData.put(groupWiseSettlementKey, objGroupWiseSettlementData);

						    mapDateWiseGroupWiseSettlementData.put(billDate, mapGroupWiseSettlementData);
						}
					    }
					}
				    }
				}

				/**
				 * end new logic
				 */
				//priting logic
				LinkedHashMap<String, Map<String, Double>> mapGroupWiseConsolidated = new LinkedHashMap<>();

				LinkedHashMap<String, Double> mapGrandTotalOrderData = new LinkedHashMap<>();
				mapGrandTotalOrderData.put("GROUP TOTALS", 0.00);
				int grandTotalLines = 0;
				if (mapDateWiseData.size() > 0)
				{
				    for (Map.Entry<String, Map<String, clsManagerReportBean>> entrySet : mapDateWiseData.entrySet())
				    {
					String billDate = entrySet.getKey();
					Map<String, clsManagerReportBean> mapDateWiseGroupTaxSettlementData = entrySet.getValue();

					clsManagerReportBean objTotalSettlementAmt = mapDateWiseGroupTaxSettlementData.get("TotalSettlementAmt");
					double totalSettlementAmt = objTotalSettlementAmt.getDblSettlementAmt();

					clsManagerReportBean objTotalTaxAmt = mapDateWiseGroupTaxSettlementData.get("TotalTaxAmt");
					totalTaxAmt = objTotalTaxAmt.getDblTaxAmt();

					clsManagerReportBean objTotalGroupAmt = mapDateWiseGroupTaxSettlementData.get("TotalGroupAmt");
					//double totalGroupSubTotal = objTotalGroupAmt.getDblSubTotal();
					double totalGroupNetTotal = objTotalGroupAmt.getDblNetTotal();

					int maxLineCount = 0;

					String labelSettlement = "SETTLEMENT          |";
					int maxGroupNameLength = 0;
					String horizontalTotalLabel = "  TOTALS   |";

					pw.println();
					pw.print(billDate);

					Map<String, String> mapDateWiseTaxeNames = mapDateWiseTaxNames.get(billDate);

					if (mapDateWiseGroupNames.containsKey(billDate))
					{
					    Map<String, String> mapGroupNames = mapDateWiseGroupNames.get(billDate);
					    for (Map.Entry<String, String> entryGroupNames : mapGroupNames.entrySet())
					    {
						String groupCode = entryGroupNames.getKey();
						String groupName = entryGroupNames.getValue();

						mapAllGroups.put(groupName, groupName);

						if (groupName.length() > maxGroupNameLength)
						{
						    maxGroupNameLength = groupName.length();
						}

						clsManagerReportBean objGroupDtl = mapDateWiseGroupTaxSettlementData.get(groupCode);
						//double groupSubTotal = objGroupDtl.getDblSubTotal();
						double groupNetTotal = objGroupDtl.getDblNetTotal();

						/**
						 * print a line
						 */
						int lineCount = funGetLineCount(billDate, labelSettlement, groupName, horizontalTotalLabel, mapDateWiseData, mapDateWiseSettlementNames, mapDateWiseTaxNames);
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

						for (String taxDesc : mapDateWiseTaxeNames.values())
						{
						    String labelTaxDesc = taxDesc + "|";
						    pw.print(objUtility.funPrintTextWithAlignment(labelTaxDesc, labelTaxDesc.length(), "Left"));

						    mapAllTaxes.put(taxDesc, taxDesc);
						}
						pw.print(objUtility.funPrintTextWithAlignment(horizontalTotalLabel, horizontalTotalLabel.length(), "Left"));

						/**
						 * print settlement wise data
						 */
						pw.println();

						Map<String, String> mapSettlementNames = mapDateWiseSettlementNames.get(billDate);
						for (Map.Entry<String, String> entrySettlements : mapSettlementNames.entrySet())
						{
						    String settlementCode = entrySettlements.getKey();
						    String settlementName = entrySettlements.getValue();

						    mapAllSettlements.put(settlementName, settlementName);

						    double horizontalSettlementTotalAmt = 0.00;

						    clsManagerReportBean objSettlementDtl = mapDateWiseGroupTaxSettlementData.get(settlementCode);

						    double groupSubTotalForThisSettlement = 0.00;
						    if (totalSettlementAmt > 0)
						    {
//			                                groupSubTotalForThisSettlement = (groupNetTotal / totalSettlementAmt) * objSettlementDtl.getDblSettlementAmt();

							Map<String, clsManagerReportBean> mapGroupSettlementData = mapDateWiseGroupWiseSettlementData.get(billDate);
							if (mapGroupSettlementData.containsKey(groupCode + "!" + settlementCode))
							{
							    clsManagerReportBean objGroupWiseSettlementData = mapGroupSettlementData.get(groupCode + "!" + settlementCode);
							    groupSubTotalForThisSettlement = objGroupWiseSettlementData.getDblNetTotal();
							}
							else
							{
							    groupSubTotalForThisSettlement = 0;
							}
						    }
						    horizontalSettlementTotalAmt += groupSubTotalForThisSettlement;

						    pw.println();
						    pw.print(objUtility.funPrintTextWithAlignment(settlementName, labelSettlement.length(), "Left"));
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupSubTotalForThisSettlement) + "|"), groupName.length(), "Right"));

						    /**
						     * setting consolidated Group Wise settlement
						     */
						    if (mapGroupWiseConsolidated.containsKey(groupName))
						    {
							Map<String, Double> mapGroupWiseConsolidatedDtl = mapGroupWiseConsolidated.get(groupName);
							if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName))
							{
							    double oldAmt = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName);

							    mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName, oldAmt + groupSubTotalForThisSettlement);
							}
							else
							{
							    mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName, groupSubTotalForThisSettlement);
							}
						    }
						    else
						    {

							Map<String, Double> mapGroupWiseConsolidatedDtl = new LinkedHashMap<>();
							mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName, groupSubTotalForThisSettlement);

							mapGroupWiseConsolidated.put(groupName, mapGroupWiseConsolidatedDtl);
						    }

						    for (Map.Entry<String, String> entryTaxNames : mapDateWiseTaxeNames.entrySet())
						    {
							String taxCode = entryTaxNames.getKey();
							String taxName = entryTaxNames.getValue();

							String labelTaxDesc = taxName + "|";

							clsManagerReportBean objTaxDtl = mapDateWiseGroupTaxSettlementData.get(taxCode);
							double taxAmt = objTaxDtl.getDblTaxAmt();

							double taxWiseGroupTotal = funGetTaxWiseGroupTotal(billDate, taxCode, mapDateWiseGroupTaxSettlementData);

							double taxAmtForThisTax = 0.00;
							boolean isTaxApplicableOnGroup = false;

							if (isApplicableTaxOnSettlement(taxCode, settlementCode))
							{
							    isTaxApplicableOnGroup = isApplicableTaxOnGroup(taxCode, groupCode);
							}

//			                                if (taxWiseGroupTotal > 0 && isTaxApplicableOnGroup)
//			                                {
//			                                    taxAmtForThisTax = (taxAmt / taxWiseGroupTotal) * groupSubTotalForThisSettlement;
//			                                }
							if (mapDateWiseGroupWiseSettlementWiseTaxData.containsKey(billDate))
							{
							    Map<String, clsManagerReportBean> mapTaxes = mapDateWiseGroupWiseSettlementWiseTaxData.get(billDate);
							    if (mapTaxes.containsKey(groupCode + "!" + settlementCode + "!" + taxCode))
							    {
								clsManagerReportBean objTax = mapTaxes.get(groupCode + "!" + settlementCode + "!" + taxCode);
								taxAmtForThisTax = objTax.getDblTaxAmt();
							    }
							}
							horizontalSettlementTotalAmt += taxAmtForThisTax;
							pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmtForThisTax)) + "|", labelTaxDesc.length(), "Right"));

							/**
							 * setting consolidated Group Wise Taxes
							 */
							if (mapGroupWiseConsolidated.containsKey(groupName))
							{
							    Map<String, Double> mapGroupWiseConsolidatedDtl = mapGroupWiseConsolidated.get(groupName);
							    if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName + "!" + taxName))
							    {
								double oldAmt = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName + "!" + taxName);

								mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + taxName, oldAmt + taxAmtForThisTax);
							    }
							    else
							    {
								mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + taxName, taxAmtForThisTax);
							    }
							}
							else
							{

							    Map<String, Double> mapGroupWiseConsolidatedDtl = new LinkedHashMap<>();
							    mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + taxName, taxAmtForThisTax);

							    mapGroupWiseConsolidated.put(groupName, mapGroupWiseConsolidatedDtl);
							}
						    }
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(horizontalSettlementTotalAmt)) + "|", horizontalTotalLabel.length(), "Right"));

						    /**
						     * setting consolidated Group Wise Totals
						     */
						    if (mapGroupWiseConsolidated.containsKey(groupName))
						    {
							Map<String, Double> mapGroupWiseConsolidatedDtl = mapGroupWiseConsolidated.get(groupName);
							if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName + "!" + "TOTALS"))
							{
							    double oldAmt = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName + "!" + "TOTALS");

							    mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + "TOTALS", oldAmt + horizontalSettlementTotalAmt);
							}
							else
							{
							    mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + "TOTALS", horizontalSettlementTotalAmt);
							}
						    }
						    else
						    {

							Map<String, Double> mapGroupWiseConsolidatedDtl = new LinkedHashMap<>();
							mapGroupWiseConsolidatedDtl.put(groupName + "!" + settlementName + "!" + "TOTALS", horizontalSettlementTotalAmt);

							mapGroupWiseConsolidated.put(groupName, mapGroupWiseConsolidatedDtl);
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

						if (maxLineCount > grandTotalLines)
						{
						    grandTotalLines = maxLineCount;
						}

						double verticleGroupTotalAmt = 0.00;
						pw.print(objUtility.funPrintTextWithAlignment(groupName.toUpperCase() + " TOTALS", labelSettlement.length(), "Left"));
						pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupNetTotal)) + "|", groupName.length(), "Right"));

						verticleGroupTotalAmt += groupNetTotal;

						for (Map.Entry<String, String> entryTaxNames : mapDateWiseTaxeNames.entrySet())
						{
						    String taxCode = entryTaxNames.getKey();
						    String taxName = entryTaxNames.getValue();

						    String labelTaxDesc = taxName + "|";
						    double taxAmt = 0.00;

						    boolean isApplicable = isApplicableTaxOnGroup(taxCode, groupCode);
						    if (isApplicable)
						    {
							double taxWiseGroupTotal = funGetTaxWiseGroupTotal(billDate, taxCode, mapDateWiseGroupTaxSettlementData);
							clsManagerReportBean objTaxDtl = mapDateWiseGroupTaxSettlementData.get(taxCode);
							double totalTaxAmtForGroup = objTaxDtl.getDblTaxAmt();

//			                                if (taxWiseGroupTotal > 0)
//			                                {
//			                                    taxAmt = (totalTaxAmtForGroup / taxWiseGroupTotal) * groupNetTotal;
//			                                }
							if (mapDateWiseGroupWiseSettlementWiseTaxData.containsKey(billDate))
							{
							    Map<String, clsManagerReportBean> mapTaxes = mapDateWiseGroupWiseSettlementWiseTaxData.get(billDate);
							    if (mapTaxes.containsKey(groupCode + "!All!" + taxCode))
							    {
								clsManagerReportBean objTax = mapTaxes.get(groupCode + "!All!" + taxCode);
								taxAmt = objTax.getDblTaxAmt();
							    }
							}
						    }
						    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmt)) + "|", labelTaxDesc.length(), "Right"));

						    verticleGroupTotalAmt += taxAmt;
						}
						pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(verticleGroupTotalAmt)) + "|", horizontalTotalLabel.length(), "Right"));
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
					/**
					 * print total line
					 */
					pw.println();
					double verticleDateTotalAmt = 0.00;
					for (int i = 0; i < maxLineCount; i++)
					{
					    pw.print("-");
					}
					pw.println();
					pw.print(objUtility.funPrintTextWithAlignment(billDate + " TOTALS", labelSettlement.length(), "Left"));
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalGroupNetTotal)) + "|", maxGroupNameLength, "Right"));

					verticleDateTotalAmt += totalGroupNetTotal;

					if (mapGrandTotalOrderData.containsKey("GROUP TOTALS"))
					{
					    double oldNetTotal = mapGrandTotalOrderData.get("GROUP TOTALS");
					    mapGrandTotalOrderData.put("GROUP TOTALS", oldNetTotal + totalGroupNetTotal);
					}

					for (Map.Entry<String, String> entryTaxNames : mapDateWiseTaxeNames.entrySet())
					{
					    String taxCode = entryTaxNames.getKey();
					    String taxName = entryTaxNames.getValue();

					    String labelTaxDesc = "  " + taxName + "|";

					    clsManagerReportBean objTaxDtl = mapDateWiseGroupTaxSettlementData.get(taxCode);
					    double totalTaxAmtForGroup = objTaxDtl.getDblTaxAmt();

					    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalTaxAmtForGroup)) + "|", labelTaxDesc.length(), "Right"));

					    verticleDateTotalAmt += totalTaxAmtForGroup;

					    if (mapGrandTotalOrderData.containsKey(taxName))
					    {
						double oldTaxTotal = mapGrandTotalOrderData.get(taxName);
						mapGrandTotalOrderData.put(taxName, oldTaxTotal + totalTaxAmtForGroup);
					    }
					    else
					    {
						mapGrandTotalOrderData.put(taxName, totalTaxAmtForGroup);
					    }

					}
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(verticleDateTotalAmt)) + "|", horizontalTotalLabel.length(), "Right"));
					if (mapGrandTotalOrderData.containsKey("TOTAL SETTLEMENT"))
					{
					    double oldSettleAmt = mapGrandTotalOrderData.get("TOTAL SETTLEMENT");
					    mapGrandTotalOrderData.put("TOTAL SETTLEMENT", oldSettleAmt + verticleDateTotalAmt);
					}
					else
					{
					    mapGrandTotalOrderData.put("TOTAL SETTLEMENT", verticleDateTotalAmt);
					}

					pw.println();
					for (int i = 0; i < maxLineCount; i++)
					{
					    pw.print("-");
					}
					pw.println();
					pw.println();
				    }

				    /**
				     * Print Group Wise Concolidated
				     */
				    String lblGroupWiseConcolidated = "Group Wise Concolidated";
				    pw.println();
				    pw.print(lblGroupWiseConcolidated);
				    pw.println();
				    for (int i = 0; i < lblGroupWiseConcolidated.length(); i++)
				    {
					pw.print("-");
				    }

				    /**
				     * print a line
				     */
				    int lineCount = funGetLineCount("SETTLEMENT          ", "     GROUP NAME|", mapAllTaxes, "     TOTALS|");
				    pw.println();
				    for (int i = 0; i < lineCount; i++)
				    {
					pw.print("-");
				    }
				    pw.println();

				    String lblSettlement = "SETTLEMENT          ", lblGroupName = "     GROUP NAME|", lblHorizontalTotals = "     TOTALS|";

				    LinkedHashMap<String, Double> mapGroupWiseConsolidatedTotals = new LinkedHashMap<>();
				    for (String groupName : mapAllGroups.values())
				    {
					mapGroupWiseConsolidatedTotals.clear();

					pw.println();
					for (int i = 0; i < lineCount; i++)
					{
					    pw.print("-");
					}

					pw.println();
					pw.print(objUtility.funPrintTextWithAlignment(lblSettlement, lblSettlement.length(), "Left"));
					pw.print(objUtility.funPrintTextWithAlignment(groupName + "|", lblGroupName.length(), "Right"));

					for (String taxDesc : mapAllTaxes.values())
					{
					    String labelTaxDesc = "   " + taxDesc + "|";
					    pw.print(objUtility.funPrintTextWithAlignment(labelTaxDesc, labelTaxDesc.length(), "Right"));
					}
					pw.print(objUtility.funPrintTextWithAlignment(lblHorizontalTotals, lblHorizontalTotals.length(), "Right"));

					pw.println();
					for (int i = 0; i < lineCount; i++)
					{
					    pw.print("-");
					}

					pw.println();
					for (String settlementName : mapAllSettlements.values())
					{
					    pw.println();
					    pw.print(objUtility.funPrintTextWithAlignment(settlementName, lblSettlement.length(), "Left"));

					    Map<String, Double> mapGroupWiseConsolidatedDtl = mapGroupWiseConsolidated.get(groupName);

					    double groupSubTotalForThisSettlement = 0;
					    if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName))
					    {
						groupSubTotalForThisSettlement = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName);
					    }
					    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupSubTotalForThisSettlement) + "|"), lblGroupName.length(), "Right"));

					    if (mapGroupWiseConsolidatedTotals.containsKey(groupName))
					    {
						double oldAmt = mapGroupWiseConsolidatedTotals.get(groupName);
						mapGroupWiseConsolidatedTotals.put(groupName, oldAmt + groupSubTotalForThisSettlement);
					    }
					    else
					    {
						mapGroupWiseConsolidatedTotals.put(groupName, groupSubTotalForThisSettlement);
					    }

					    for (String taxDesc : mapAllTaxes.values())
					    {
						String labelTaxDesc = "   " + taxDesc + "|";
						double taxAmt = 0;
						if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName + "!" + taxDesc))
						{
						    taxAmt = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName + "!" + taxDesc);
						}
						pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmt) + "|"), labelTaxDesc.length(), "Right"));

						if (mapGroupWiseConsolidatedTotals.containsKey(taxDesc))
						{
						    double oldAmt = mapGroupWiseConsolidatedTotals.get(taxDesc);
						    mapGroupWiseConsolidatedTotals.put(taxDesc, oldAmt + taxAmt);
						}
						else
						{
						    mapGroupWiseConsolidatedTotals.put(taxDesc, taxAmt);
						}
					    }
					    double totalAmt = 0.00;
					    if (mapGroupWiseConsolidatedDtl.containsKey(groupName + "!" + settlementName + "!" + "TOTALS"))
					    {
						totalAmt = mapGroupWiseConsolidatedDtl.get(groupName + "!" + settlementName + "!" + "TOTALS");
					    }
					    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalAmt) + "|"), lblHorizontalTotals.length(), "Right"));

					    if (mapGroupWiseConsolidatedTotals.containsKey("TOTALSAMOUNT"))
					    {
						double oldAmt = mapGroupWiseConsolidatedTotals.get("TOTALSAMOUNT");
						mapGroupWiseConsolidatedTotals.put("TOTALSAMOUNT", oldAmt + totalAmt);
					    }
					    else
					    {
						mapGroupWiseConsolidatedTotals.put("TOTALSAMOUNT", totalAmt);
					    }
					}
					pw.println();
					for (int i = 0; i < lineCount; i++)
					{
					    pw.print("-");
					}
					pw.println();
					pw.print(objUtility.funPrintTextWithAlignment(groupName + " TOTALS", lblSettlement.length(), "Left"));
					double groupSubTotalForThisSettlement = mapGroupWiseConsolidatedTotals.get(groupName);
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(groupSubTotalForThisSettlement) + "|"), lblGroupName.length(), "Right"));
					for (String taxDesc : mapAllTaxes.values())
					{
					    String labelTaxDesc = "   " + taxDesc + "|";

					    double taxAmt = mapGroupWiseConsolidatedTotals.get(taxDesc);
					    pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(taxAmt) + "|"), labelTaxDesc.length(), "Right"));
					}
					double totalAmt = mapGroupWiseConsolidatedTotals.get("TOTALSAMOUNT");
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(totalAmt) + "|"), lblHorizontalTotals.length(), "Right"));
					pw.println();
					for (int i = 0; i < lineCount; i++)
					{
					    pw.print("-");
					}

					pw.println();
					pw.println();
					pw.println();
				    }

				    /**
				     * print All Groups Concolidated
				     */
				    pw.println();
				    pw.print("All Groups Consolidated");

				    pw.println();
				    for (int i = 0; i < grandTotalLines; i++)
				    {
					pw.print("-");
				    }
				    pw.println();
				    for (Map.Entry<String, Double> grandTotalsEntry : mapGrandTotalOrderData.entrySet())
				    {
					String label = grandTotalsEntry.getKey();
					pw.print(objUtility.funPrintTextWithAlignment(label + "|", 17, "Right"));
				    }
				    pw.println();
				    for (int i = 0; i < grandTotalLines; i++)
				    {
					pw.print("-");
				    }
				    pw.println();
				    for (Map.Entry<String, Double> grandTotalsEntry : mapGrandTotalOrderData.entrySet())
				    {
					double value = grandTotalsEntry.getValue();
					pw.print(objUtility.funPrintTextWithAlignment(String.valueOf(Math.rint(value)) + "|", 17, "Right"));
				    }

				    pw.println();
				    for (int i = 0; i < grandTotalLines; i++)
				    {
					pw.print("-");
				    }
				    pw.println();
				    pw.println();

				}

			return 1;
		 
		}
		
		 private int funGetLineCount(String billDate, String labelSettlement, String labelGroupName, String horizontalTotalLabel, Map<String, Map<String, clsManagerReportBean>> mapDateWiseData, Map<String, Map<String, String>> mapDateWiseSettlemetNames, Map<String, Map<String, String>> mapDateWiseTaxNames)
		    {

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(labelSettlement);
			stringBuilder.append(labelGroupName);

			Map<String, String> map = mapDateWiseTaxNames.get(billDate);
			for (String taxDesc : map.values())
			{
			    String labelTaxDesc = taxDesc + "|";
			    stringBuilder.append(labelTaxDesc);
			}
			stringBuilder.append(horizontalTotalLabel);

			return stringBuilder.length();
		    }

		    private boolean isApplicableTaxOnGroup(String taxCode, String groupCode)
		    {
			boolean isApplicable = false;
			try
			{
			    StringBuilder sql = new StringBuilder("select a.strTaxCode,a.strGroupCode,a.strGroupName,a.strApplicable "
				    + "from tbltaxongroup a "
				    + "where a.strTaxCode='" + taxCode + "' "
				    + "and a.strGroupCode='" + groupCode + "' "
				    + "and a.strApplicable='true' ");
			    List listIsApplicable = objBaseService.funGetList(sql,"sql");
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

		    private boolean isApplicableTaxOnSettlement(String taxCode, String settlementCode)
		    {
			boolean isApplicable = false;
			try
			{
			    StringBuilder sql = new StringBuilder("select a.strTaxCode,a.strSettlementCode,a.strSettlementName,a.strApplicable "
				    + "from tblsettlementtax a  "
				    + "where a.strTaxCode='" + taxCode + "'  "
				    + "and a.strSettlementCode='" + settlementCode + "'  "
				    + "and a.strApplicable='true' ");
			    List listIsApplicable = objBaseService.funGetList(sql,"sql");
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

		    private double funGetTaxWiseGroupTotal(String billDate, String taxCode, Map<String, clsManagerReportBean> mapDateWiseGroupTaxSettlementData)
		    {
			double taxWiseGroupTotal = 0.00;

			try
			{
			    StringBuilder sql = new StringBuilder("select distinct(b.strGroupCode),b.strGroupName,a.strTaxOnGD "
				    + "from tbltaxhd a,tbltaxongroup b "
				    + "where a.strTaxCode=b.strTaxCode "
				    + "and b.strTaxCode='" + taxCode + "' "
				    + "and b.strApplicable='true' ");
			    List listIsApplicable = objBaseService.funGetList(sql,"sql");
			   if(listIsApplicable.size()>0)
			    {
				   for(int i=0;i<listIsApplicable.size();i++)
				   {
					   Object[] obj = (Object[]) listIsApplicable.get(i);
						String groupCode = obj[0].toString();//groupCode
						String taxOnGD = obj[2].toString();//taxOnGD
		
						if (mapDateWiseGroupTaxSettlementData.containsKey(groupCode))
						{
						    clsManagerReportBean objGroupDtl = mapDateWiseGroupTaxSettlementData.get(groupCode);
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

		    private int funGetLineCount(String settlement__________, String group_name____, LinkedHashMap<String, String> mapAllTaxes, String totals________)
		    {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(settlement__________);
			stringBuilder.append(group_name____);
			stringBuilder.append(totals________);

			for (String taxDesc : mapAllTaxes.values())
			{
			    String labelTaxDesc = "   " + taxDesc + "|";
			    stringBuilder.append(labelTaxDesc);
			}

			return stringBuilder.length();
		    }

		
				
}
