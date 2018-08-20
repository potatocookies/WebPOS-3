package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSCashManagementDtlBean;


@Controller
public class clsPOSCashManagementFlashController 
{
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	 private ServletContext servletContext;
	
	Map map=new HashMap();
	@RequestMapping(value = "/frmPOSCashMgmtReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model, HttpServletRequest request){
		String urlHits="1";
		String posCode=request.getSession().getAttribute("loginPOS").toString();
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		List<Object> posList= new ArrayList<Object>();
		JSONArray jArrList=new JSONArray();
			 jArrList =objPOSGlobal.funGetAllPOSForMaster(clientCode);
			for(int i =0 ;i<jArrList.size();i++)
			{
				JSONObject josnObjRet = (JSONObject) jArrList.get(i);
				posList.add(josnObjRet.get("strPosName"));
				map.put(josnObjRet.get("strPosName"), josnObjRet.get("strPosCode"));
			}
			
		model.put("posList",posList);
		
	 model.put("posDate", request.getSession().getAttribute("gPOSDate"));  	
		
		
		Map mapAmount = new HashMap<>();
		
		mapAmount.put("<=", "<=");
		mapAmount.put(">=", ">=");
		mapAmount.put("=", "=");
		model.put("mapAmount",mapAmount);
		
		Map mapReportType = new HashMap<>();
		
		mapReportType.put("Detail", "Detail");
		mapReportType.put("Summary", "Summary");
		
		model.put("mapReportType",mapReportType);
		
		Map mapTransType = new HashMap<>();
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
	

}	
	
	