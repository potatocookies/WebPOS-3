package com.sanguine.webpos.controller;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.sanguine.base.service.clsSetupService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.model.clsShiftMasterModel;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;

@Controller
public class clsPOSVoidKOTReportController {

	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private ServletContext servletContext;
	 
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctions;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	private Map<String,String> map = new HashMap<String,String>();
	
	@RequestMapping(value = "/frmPOSVoidKOTReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)throws Exception
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
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				map.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList",poslist);
		
		List<String> listReportSubType = new ArrayList<String>();
		listReportSubType.add("All");
		listReportSubType.add("Void KOT");
		listReportSubType.add("Move KOT");
		model.put("listReportSubType",listReportSubType);
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());
		
		List shiftList = new ArrayList();
		shiftList.add("All");
		List listShiftData = objReportService.funGetPOSWiseShiftList(POSCode,request);
		if(listShiftData!=null)
		{
			for(int cnt=0;cnt<listShiftData.size();cnt++)
			{
				clsShiftMasterModel objShiftModel= (clsShiftMasterModel) listShiftData.get(cnt);
				listReportSubType.add(objShiftModel.getIntShiftCode());
			}
		}
		model.put("shiftList",listReportSubType);
		
		String posDate=request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
	
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidKOTReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidKOTReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptVoidKOTReport", method = RequestMethod.POST)	
	private void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		
		try
		{
		String strClientCode = req.getSession().getAttribute("gClientCode").toString();
		String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptVoidKOTReport.jrxml");
		InputStream subReportName = this.getClass().getClassLoader().getResourceAsStream("/WEB-INF/reports/webpos/rptVoidKOTSubReportForWaiterWiseVoidedKOT.jrxml");
		
		Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
		String reportSubType = objBean.getStrReportType();
		String strPOSName = objBean.getStrPOSName();
		String posCode = "ALL";
		if (!strPOSName.equalsIgnoreCase("ALL"))
		{
			posCode = (String) map.get(strPOSName);
		}
		hm.put("posCode", posCode);
		
		String fromDate = hm.get("fromDate").toString();
		String toDate = hm.get("toDate").toString();
		String strUserCode = hm.get("userName").toString();
		String strPOSCode = posCode;
		String strShiftNo = "1";
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, posCode, "gEnableShiftYN");
		if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
		{
			strShiftNo=objBean.getStrShiftCode();
		}
		hm.put("rptVoidKOTSubReportForWaiterWiseVoidedKOT", subReportName);
		
		DecimalFormat decimalFormat2Decimal = new DecimalFormat("0.00");
        DecimalFormat decimalFormat0Decimal = new DecimalFormat("0");

        int totalKOTs = 0, totalItems = 0, totalVoidedKOTs = 0, totalVoidedItems = 0;
        
        List listNoOfKotData = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"liveNoOfKotData");

        if(listNoOfKotData.size()>0)
        {
        	for(int i=0;i<listNoOfKotData.size();i++)
        	{
        	Object[] obj = (Object[]) listNoOfKotData.get(i);
            totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
            double dbl = Double.parseDouble(obj[1].toString());
            totalItems = totalItems + (int)dbl;
        	}
        }
        
       
        List listQFileNoOfKotData = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"qFileNoOfKotData");
        if(listQFileNoOfKotData.size()>0)
        {
        	for(int i=0;i<listQFileNoOfKotData.size();i++)
        	{
        	Object[] obj = (Object[]) listQFileNoOfKotData.get(i);	
            totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
            double dbl = Double.parseDouble(obj[1].toString());
            totalItems = totalItems + (int)dbl;
        	}
        }
        
       
        List ListLiveVoidedBilledKOTs = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"liveVoidedBilledKOTs");
        if(ListLiveVoidedBilledKOTs.size()>0)
        {
        	for(int i=0;i<ListLiveVoidedBilledKOTs.size();i++)
        	{
        	Object[] obj = (Object[]) ListLiveVoidedBilledKOTs.get(i);	
        	totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
        	double dbl = Double.parseDouble(obj[1].toString());
            totalItems = totalItems + (int)dbl;
        	}
        }
        
        
        List listLineVoidedKOTs = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"lineVoidedKOTs");
        if(listLineVoidedKOTs.size()>0)
        {
        	for(int i=0;i<listLineVoidedKOTs.size();i++)
        	{
        	Object[] obj = (Object[]) listLineVoidedKOTs.get(i);	
        	totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
        	double dbl = Double.parseDouble(obj[1].toString());
            totalItems = totalItems + (int)dbl;
        	}
        }
        
        
        List listVoidedKOT = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"voidedKOT");
        if(listVoidedKOT.size()>0)
        {
        	for(int i=0;i<listVoidedKOT.size();i++)
        	{
        	Object[] obj = (Object[]) listVoidedKOT.get(i);	
            totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
            double dbl = Double.parseDouble(obj[1].toString());
            totalItems = totalItems + (int)dbl;
        	}
        }
        
        
        List listNCKOTs = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"ncKots");
        if(listNCKOTs.size()>0)
        {
        	for(int i=0;i<listNCKOTs.size();i++)
        	{	
            Object[] obj = (Object[]) listNCKOTs.get(i);
        	totalKOTs = totalKOTs + Integer.parseInt(obj[0].toString());
        	double dbl = Double.parseDouble(obj[1].toString());
        	totalItems = totalItems + (int)dbl;
        	}
        }
       
        //KOT data
        List<clsPOSVoidBillDtl> listOfVoidKOTData = new ArrayList<clsPOSVoidBillDtl>();
        
        List listVoidData = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"voidKOTData");
        if(listVoidData.size()>0)
        {
        	for(int i=0;i<listVoidData.size();i++)
        	{	
        	Object[] obj = (Object[]) listVoidData.get(i);	
            clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
            objVoidBill.setStrItemCode(obj[0].toString());        //ItemCode
            objVoidBill.setStrItemName(obj[1].toString());        //ItemName
            objVoidBill.setStrTableNo(obj[2].toString());         //Table Name
            objVoidBill.setDblPaidAmt(Double.parseDouble(obj[3].toString()));         //Rate
            objVoidBill.setIntQuantity(Double.parseDouble(obj[4].toString()));        //Qty
            objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString()));          //Amount
            objVoidBill.setStrRemarks(obj[6].toString());         //Remarks   
            objVoidBill.setStrKOTNo(obj[7].toString());           //KOT No  
            objVoidBill.setStrClientCode(obj[8].toString());      //POS Code
            objVoidBill.setStrPosCode(obj[9].toString());        //POS Name 
            objVoidBill.setStrUserCreated(obj[10].toString());    //User Created
            objVoidBill.setDteBillDate(obj[11].toString());       //Voided Date
            objVoidBill.setStrReasonName(obj[12].toString());     //Reason
            objVoidBill.setStrWaiterName(obj[13].toString());     //waiter
            objVoidBill.setStrVoidBillType(obj[14].toString());     //Void Type
            objVoidBill.setDteCreatedDate(obj[15].toString());     //kot time
            objVoidBill.setIntNoOfKot(totalKOTs);
            objVoidBill.setIntNoOfQty(totalItems);

            listOfVoidKOTData.add(objVoidBill);
        	}
        }
       
        //which is not modifiers
        
       List listVoidedKotCount = objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"voidedKotCountForNotModif");
       if(listVoidedKotCount.size()>0)
        {
    	   	for(int i=0;i<listVoidedKotCount.size();i++)
    	   	{
    	   	Object[] obj = (Object[]) listVoidedKotCount.get(i);	
            totalVoidedKOTs = Integer.parseInt(obj[0].toString());
            double dbl = Double.parseDouble(obj[1].toString());
            totalVoidedItems = (int)dbl;
    	   	}
        }
        
        //which is modifiers but chargable
        
         listVoidedKotCount =objReportService.funProcessVoidKotReport(strPOSCode,reportSubType,fromDate,toDate,"voidedKotCountForModif");
        if(listVoidedKotCount.size()>0)
        {
        	for(int i=0;i<listVoidedKotCount.size();i++)
        	{
        	Object[] obj = (Object[]) listVoidedKotCount.get(i);
        	double dbl = Double.parseDouble(obj[1].toString());
            totalVoidedItems = totalVoidedItems + (int)dbl;
        	}
        }
        
        double voidedKotPer = 0.0, voidedItemPer = 0.0;
        double auditTotal = totalVoidedKOTs + totalKOTs;
        if (totalKOTs != 0)
        {
            voidedKotPer = (double) totalVoidedKOTs / (double) totalKOTs * 100;
        }
        else
        {
            voidedKotPer = (double) totalVoidedKOTs;
        }
        if (totalItems != 0)
        {
            voidedItemPer = (double) totalVoidedItems / (double) totalItems * 100;
        }
        else
        {
            voidedItemPer = (double) totalVoidedKOTs;
        }
        voidedKotPer = Double.parseDouble(decimalFormat2Decimal.format(voidedKotPer));
        voidedItemPer = Double.parseDouble(decimalFormat2Decimal.format(voidedItemPer));
        String rptHeading = "Voided KOT Report";
        if (reportSubType.equals("Move KOT"))
        {
            rptHeading = "Moved KOT Report";
        }
        else
        {
            rptHeading = "Voided KOT Report";
        }
        hm.put("rptHeading", rptHeading);
        hm.put("noOfKot", totalKOTs);
        hm.put("voidedKotCount", totalVoidedKOTs);
        hm.put("voidedKotPer", voidedKotPer);
        hm.put("voidedItemPer", voidedItemPer);
        hm.put("voidedItemsCount", totalVoidedItems);
        hm.put("auditTotal", auditTotal);

        Map<String, Map<String, String>> mapWaiterKOTs = new HashMap<>();
        Map<String, Integer> mapWaiterKOTCount = new HashMap<>();
        for (clsPOSVoidBillDtl objKOT : listOfVoidKOTData)
        {
            String waiterName = objKOT.getStrWaiterName();
            String kot = objKOT.getStrKOTNo();

            if (mapWaiterKOTCount.containsKey(waiterName))
            {
                Map<String, String> map = mapWaiterKOTs.get(waiterName);

                String key = waiterName + "!" + kot;
                if (map.containsKey(key))
                {
                    //ignore
                }
                else
                {
                    mapWaiterKOTCount.put(waiterName, mapWaiterKOTCount.get(waiterName) + 1);

                    map.put(key, key);
                }
            }
            else
            {
                Map<String, String> map = new HashMap<>();

                String key = waiterName + "!" + kot;
                map.put(key, key);

                mapWaiterKOTs.put(waiterName, map);

                mapWaiterKOTCount.put(waiterName, 1);

            }

        }
        List<clsPOSVoidBillDtl> listOfWaiterWiseKOT = new ArrayList<>();
        if (!reportSubType.equals("Move KOT"))
        {
            for (Map.Entry<String, Integer> entry : mapWaiterKOTCount.entrySet())
            {
                String waiterName = entry.getKey();
                int kotCount = entry.getValue();

                clsPOSVoidBillDtl objBillDtl = new clsPOSVoidBillDtl();
                objBillDtl.setStrWaiterName(waiterName);
                objBillDtl.setIntNoOfKot(kotCount);
                objBillDtl.setIntTotalVoidKOTs(totalVoidedKOTs);

                double voidKOTPer = 0.00;
                if (totalVoidedKOTs > 0)
                {
                    voidKOTPer = (double) ((double) kotCount / (double) totalVoidedKOTs) * 100;
                }
                objBillDtl.setDblVoidedKOTPer(voidKOTPer);

                listOfWaiterWiseKOT.add(objBillDtl);
            }
        }

        hm.put("listOfWaiterWiseKOT", listOfWaiterWiseKOT);
        
        JasperDesign jd = JRXmlLoader.load(reportName);
		JasperReport jr = JasperCompileManager.compileReport(jd);

		// jp = JasperFillManager.fillReport(jr, hm, new
		// JREmptyDataSource());

		List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfVoidKOTData);
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
				resp.setHeader("Content-Disposition", "inline;filename=VoidKOTReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
				resp.setHeader("Content-Disposition", "inline;filename=VoidKOTReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
