


package com.sanguine.webpos.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;


import com.sanguine.webpos.bean.clsPOSOperatorDtl;
import com.sanguine.webpos.bean.clsPOSOperatorWiseReportBean;
import com.sanguine.webpos.bean.clsPOSRevenueHeadWiseSalesReportBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSOperatorComparator;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsPOSOperatorWiseComparator;
import com.sanguine.webpos.util.clsPOSRevenueHeadWiseComparator;



@Controller
public class clsPOSOperatorWiseReportController {
	
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	Map posMap=new HashMap();
	Map userMap=new HashMap();
	Map SettlementMap=new HashMap();
	
	@RequestMapping(value = "/frmPOSOperatorWiseReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
	{
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		
		 posMap.put("All","All");
		 List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					posMap.put( obj[1].toString(), obj[0].toString());
				}
			}
		model.put("posList",posMap);
		
		 
		 userMap.put("All","All");
		 List userList = objMasterService.funFillUserCombo(strClientCode);
		 if(userList.size()>0)
		 {
			 for(int i=0;i<userList.size();i++)
			 {
				 clsUserHdModel objModel = (clsUserHdModel) userList.get(i);
				 userMap.put(objModel.getStrUserCode(),objModel.getStrUserName());
			 }
		 }
		model.put("userList",userMap);
		
		
		 SettlementMap.put("All","All");
		 List settlementList = objMasterService.funFillSettlementCombo(strClientCode);
		 if(settlementList.size()>0)
		 {
			 for(int i=0;i<settlementList.size();i++)
			 {
				 clsSettlementMasterModel objModel = (clsSettlementMasterModel) settlementList.get(i);
				 SettlementMap.put(objModel.getStrSettelmentCode(), objModel.getStrSettelmentDesc());
			 }
		 }
		 model.put("settlementList",SettlementMap);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSOperatorWiseReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSOperatorWiseReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSOperatorWiseReport", method = RequestMethod.POST)	
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		
		
		try
		{
			
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String reportType = objBean.getStrViewType();
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = (String) posMap.get(strPOSName);
			}
			hm.put("posCode", posCode);
			
			String settleName = objBean.getStrReportType();
			String settleCode = "ALL";
			if (!settleName.equalsIgnoreCase("ALL"))
			{
				settleCode = (String) posMap.get(settleName);
			}
			hm.put("settleCode", settleCode);
			
			String userName = objBean.getStrPOSName();
			String userCode = "ALL";
			if (!userName.equalsIgnoreCase("ALL"))
			{
				userCode = (String) posMap.get(userName);
			}
			hm.put("userCode", userCode);
			
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String strShiftNo = "1";
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptOpearatorWiseSettlementReport.jrxml");
			
            DecimalFormat decimalFormat2Decimal = new DecimalFormat("0.00");

            Map<String, List<clsPOSOperatorDtl>> mapSettlementWiseBills = new TreeMap<String, List<clsPOSOperatorDtl>>();
            double totalNetRevenue = 0.00;
            List listSettlementWiseBills = new ArrayList();
            //for Live
            listSettlementWiseBills = objReportService.funProcessLiveDataForOperatorWiseReport(posCode,fromDate,toDate,userCode,strShiftNo,settleCode);
            if(listSettlementWiseBills.size()>0)
            {
            	for(int i=0;i<listSettlementWiseBills.size();i++)
            	{
            	Object[] obj = (Object[]) listSettlementWiseBills.get(i);	
                String billNo = obj[0].toString();
                String billDate = obj[0].toString();
                String billNoDateKey = billNo + "!" + billDate;

                if (mapSettlementWiseBills.containsKey(billNoDateKey))
                {
                    List<clsPOSOperatorDtl> listOfOperatorWiseSettlementDtl = mapSettlementWiseBills.get(billNoDateKey);

                    clsPOSOperatorDtl objOperatorDtl = new clsPOSOperatorDtl();

                    objOperatorDtl.setStrUserCode(obj[2].toString());
                    objOperatorDtl.setStrUserName(obj[3].toString());
                    objOperatorDtl.setStrPOSName(obj[5].toString());
                    objOperatorDtl.setStrSettlementDesc(obj[9].toString());
                    objOperatorDtl.setSettleAmt(Double.parseDouble(obj[10].toString()));

                    listOfOperatorWiseSettlementDtl.add(objOperatorDtl);

                    mapSettlementWiseBills.put(billNoDateKey, listOfOperatorWiseSettlementDtl);
                }
                else
                {

                    totalNetRevenue += totalNetRevenue;
                    clsPOSOperatorDtl objOperatorDtl = new clsPOSOperatorDtl();

                    objOperatorDtl.setStrUserCode(obj[2].toString());
                    objOperatorDtl.setStrUserName(obj[3].toString());
                    objOperatorDtl.setStrPOSName(obj[4].toString());
                    objOperatorDtl.setDblSubTotal(Double.parseDouble(obj[5].toString()));
                    objOperatorDtl.setDiscountAmt(Double.parseDouble(obj[6].toString()));
                    objOperatorDtl.setDblNetTotal(Double.parseDouble(obj[7].toString()));
                    objOperatorDtl.setDblTaxAmt(Double.parseDouble(obj[8].toString()));
                    objOperatorDtl.setStrSettlementDesc(obj[9].toString());
                    objOperatorDtl.setSettleAmt(Double.parseDouble(obj[10].toString()));

                    List<clsPOSOperatorDtl> listOfOperatorWiseSettlementDtl = new LinkedList<>();
                    listOfOperatorWiseSettlementDtl.add(objOperatorDtl);

                    mapSettlementWiseBills.put(billNoDateKey, listOfOperatorWiseSettlementDtl);
                }
            	}
            }
            

            //For Q
            listSettlementWiseBills = objReportService.funProcessQFileDataForOperatorWiseReport(posCode,fromDate,toDate,userCode,strShiftNo,settleCode);
            if(listSettlementWiseBills.size()>0)
            {
            	for(int i=0;i<listSettlementWiseBills.size();i++)
            	{
            	Object[] obj = (Object[]) listSettlementWiseBills.get(i);	
                String billNo = obj[0].toString();
                String billDate = obj[0].toString();
                String billNoDateKey = billNo + "!" + billDate;

                if (mapSettlementWiseBills.containsKey(billNoDateKey))
                {
                    List<clsPOSOperatorDtl> listOfOperatorWiseSettlementDtl = mapSettlementWiseBills.get(billNoDateKey);

                    clsPOSOperatorDtl objOperatorDtl = new clsPOSOperatorDtl();

                    objOperatorDtl.setStrUserCode(obj[2].toString());
                    objOperatorDtl.setStrUserName(obj[3].toString());
                    objOperatorDtl.setStrPOSName(obj[4].toString());
                    objOperatorDtl.setStrSettlementDesc(obj[9].toString());
                    objOperatorDtl.setSettleAmt(Double.parseDouble(obj[10].toString()));

                    listOfOperatorWiseSettlementDtl.add(objOperatorDtl);

                    mapSettlementWiseBills.put(billNoDateKey, listOfOperatorWiseSettlementDtl);
                }
                else
                {
                    totalNetRevenue += totalNetRevenue;
                    clsPOSOperatorDtl objOperatorDtl = new clsPOSOperatorDtl();

                    objOperatorDtl.setStrUserCode(obj[2].toString());
                    objOperatorDtl.setStrUserName(obj[3].toString());
                    objOperatorDtl.setStrPOSName(obj[4].toString());
                    objOperatorDtl.setDblSubTotal(Double.parseDouble(obj[5].toString()));
                    objOperatorDtl.setDiscountAmt(Double.parseDouble(obj[6].toString()));
                    objOperatorDtl.setDblNetTotal(Double.parseDouble(obj[7].toString()));
                    objOperatorDtl.setDblTaxAmt(Double.parseDouble(obj[8].toString()));
                    objOperatorDtl.setStrSettlementDesc(obj[9].toString());
                    objOperatorDtl.setSettleAmt(Double.parseDouble(obj[10].toString()));

                    List<clsPOSOperatorDtl> listOfOperatorWiseSettlementDtl = new LinkedList<>();
                    listOfOperatorWiseSettlementDtl.add(objOperatorDtl);

                    mapSettlementWiseBills.put(billNoDateKey, listOfOperatorWiseSettlementDtl);
                }
            	}
            }
           

            Map<String, clsPOSOperatorDtl> mapUserCodeWise = new HashMap<>();
            for (List<clsPOSOperatorDtl> listOfSettlementWiseBill : mapSettlementWiseBills.values())
            {
                for (clsPOSOperatorDtl objNewDtl : listOfSettlementWiseBill)
                {

                    String user = objNewDtl.getStrUserCode();
                    String settlementName = objNewDtl.getStrSettlementDesc();
                    String key = user + "!" + settlementName;
                    if (mapUserCodeWise.containsKey(key))
                    {
                        clsPOSOperatorDtl objOldDtl = mapUserCodeWise.get(key);

                        objNewDtl.setDblSubTotal(objNewDtl.getDblSubTotal() + objOldDtl.getDblSubTotal());
                        objNewDtl.setDiscountAmt(objNewDtl.getDiscountAmt() + objOldDtl.getDiscountAmt());
                        objNewDtl.setDblNetTotal(objNewDtl.getDblNetTotal() + objOldDtl.getDblNetTotal());
                        objNewDtl.setSettleAmt(objNewDtl.getSettleAmt() + objOldDtl.getSettleAmt());

                        mapUserCodeWise.put(key, objNewDtl);
                    }
                    else
                    {
                        mapUserCodeWise.put(key, objNewDtl);
                    }
                }
            }

            totalNetRevenue = 0;
            List<clsPOSOperatorDtl> listOfOperatorWiseSettlementDtl = new LinkedList<>();
            Map<String, clsPOSOperatorDtl> mapUserWiseNetTotal = new HashMap<>();

            for (clsPOSOperatorDtl objOperator : mapUserCodeWise.values())
            {
                String userCodeKey = objOperator.getStrUserCode();

                listOfOperatorWiseSettlementDtl.add(objOperator);
                totalNetRevenue = totalNetRevenue + objOperator.getDblNetTotal();

                if (mapUserWiseNetTotal.containsKey(userCodeKey))
                {
                    clsPOSOperatorDtl objUserWiseNetTotal = mapUserWiseNetTotal.get(userCodeKey);
                    objUserWiseNetTotal.setDblNetTotal(objUserWiseNetTotal.getDblNetTotal() + objOperator.getDblNetTotal());
                }
                else
                {
                    clsPOSOperatorDtl objUserWiseNetTotal = new  clsPOSOperatorDtl();
                    objUserWiseNetTotal.setStrUserCode(objOperator.getStrUserCode());
                    objUserWiseNetTotal.setDblNetTotal(objOperator.getDblNetTotal());

                    mapUserWiseNetTotal.put(userCodeKey, objUserWiseNetTotal);
                }

            }

            Comparator<clsPOSOperatorDtl> userCodeComparator = new Comparator<clsPOSOperatorDtl>()
            {

                @Override
                public int compare(clsPOSOperatorDtl o1, clsPOSOperatorDtl o2)
                {
                    return o1.getStrUserCode().compareToIgnoreCase(o2.getStrUserCode());
                }
            };

            Comparator<clsPOSOperatorDtl> settlementNameComparator = new Comparator<clsPOSOperatorDtl>()
            {

                @Override
                public int compare(clsPOSOperatorDtl o1, clsPOSOperatorDtl o2)
                {
                    return o1.getStrSettlementDesc().compareToIgnoreCase(o2.getStrSettlementDesc());
                }
            };

            Collections.sort(listOfOperatorWiseSettlementDtl, new clsPOSOperatorComparator(
                    userCodeComparator,
                    settlementNameComparator)
            );

            hm.put("totalNetRevenue", totalNetRevenue);
            
            JasperDesign jd = JRXmlLoader.load(reportName);
    		JasperReport jr = JasperCompileManager.compileReport(jd);
            List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
    		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfOperatorWiseSettlementDtl);
    		JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
    		jprintlist.add(print);

    		if (jprintlist.size() > 0)
    		{
    			ServletOutputStream servletOutputStream = resp.getOutputStream();
    			if (objBean.getStrDocType().equals("PDF"))
    			{
    				JRExporter exporter = new JRPdfExporter();
    				resp.setContentType("application/pdf");
    				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
    				exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
    				exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
    				resp.setHeader("Content-Disposition", "inline;filename=OperatorWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
    				exporter.exportReport();
    				servletOutputStream.flush();
    				servletOutputStream.close();
    			}
    			else
    			{
    				JRExporter exporter = new JRXlsExporter();
    				resp.setContentType("application/xlsx");
    				exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
    				exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
    				exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
    				resp.setHeader("Content-Disposition", "inline;filename=OperatorWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
    				exporter.exportReport();
    				servletOutputStream.flush();
    				servletOutputStream.close();
    			}
    		}
    		else
    		{
    			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    			resp.getWriter().append("No Record Found");

    		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	            
		
		System.out.println("Hi");
			
	}
	
	
	
	
	
	
	

}