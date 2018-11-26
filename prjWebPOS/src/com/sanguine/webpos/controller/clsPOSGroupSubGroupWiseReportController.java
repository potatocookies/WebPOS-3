package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupItemBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsShiftMasterModel;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;

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

@Controller
public class clsPOSGroupSubGroupWiseReportController {

	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
	 clsPOSMasterService objMasterService;
	 
	 @Autowired
	 clsPOSReportService objReportService;
	 
	 @Autowired
	 private clsSetupService objSetupService;
	 
	 Map<String,String> mapPOS=new HashMap<String,String>();
	 Map<String,String> mapSubGrp=new HashMap<String,String>();
	 Map<String,String> mapGroup=new HashMap<String,String>();
	
	 @RequestMapping(value = "/frmPOSGroupSubGroupWiseReport", method = RequestMethod.GET)
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
				mapPOS.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList",poslist);
		
		List listSubGroup = new ArrayList<String>();
		listSubGroup.add("ALL");
		List listOfSubGroup = objMasterService.funLoadAllSubGroup(strClientCode);
		if(listOfSubGroup!=null)
		{
			for(int i =0 ;i<listOfSubGroup.size();i++)
			{
				clsSubGroupMasterHdModel objModel = (clsSubGroupMasterHdModel) listOfSubGroup.get(i);
				listSubGroup.add(objModel.getStrSubGroupName());
				mapSubGrp.put(objModel.getStrSubGroupName(), objModel.getStrSubGroupCode());
			}
		}
		model.put("listSubGroupName",listSubGroup);
		
		List listGroup = new ArrayList<String>();
		listGroup.add("ALL");
		List listOfGroup = objMasterService.funLoadAllGroupDetails(strClientCode);
		if(listOfGroup!=null)
		{
			for(int i =0 ;i<listOfGroup.size();i++)
			{
				clsGroupMasterModel objModel = (clsGroupMasterModel) listOfGroup.get(i);
				listGroup.add(objModel.getStrGroupName());
				mapGroup.put(objModel.getStrGroupName(), objModel.getStrGroupCode());
			}
		}
		model.put("listGroupName",listGroup);
		
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
				shiftList.add(objShiftModel.getIntShiftCode());
			}
		}
		model.put("shiftList",shiftList);
		
		List listSelectType = new ArrayList();
		listSelectType.add("Summary");
		listSelectType.add("Detail");
		model.put("listSelectType",listSelectType);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSGroupSubGroupWiseReport_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSGroupSubGroupWiseReport","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptGroupSubGroupWiseReport", method = RequestMethod.POST)	
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req,String source)
	{
		
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		String POSCode=req.getSession().getAttribute("loginPOS").toString();	
		List<clsPOSGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsPOSGroupSubGroupItemBean>();
		try
		{
			
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptGroupSubGroupWiseReport.jrxml");
			List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
			String strFromdate=objBean.getFromDate().split("-")[2]+"-"+objBean.getFromDate().split("-")[1]+"-"+objBean.getFromDate().split("-")[0];
			String strToDate=objBean.getToDate().split("-")[2]+"-"+objBean.getToDate().split("-")[1]+"-"+objBean.getToDate().split("-")[0]; 
			String strSubGrpName =objBean.getStrSGName();
			String strPOSName =objBean.getStrPOSName();
			String strGroupName =objBean.getStrGroupName();
			String posCode= "ALL";
			String subGroupCode= "ALL";
			String groupCode= "ALL";
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			
			if(!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode=mapPOS.get(strPOSName);// funGetPOSCode(strPOSName);
			}
			hm.put("posCode", posCode);
			
			if(!strSubGrpName.equalsIgnoreCase("ALL"))
			{
				subGroupCode= mapSubGrp.get(strSubGrpName);//funGetSGCode(strSGName);
			}
			hm.put("posCode", subGroupCode);
			
			if(!strGroupName.equalsIgnoreCase("ALL"))
			{
				groupCode=mapGroup.get(strGroupName); //funGetGCode(strGroupName);
			}
			hm.put("posCode", groupCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "ALL";
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
			{
				shiftNo=objBean.getStrShiftCode();
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", shiftNo);
			if(objBean.getStrReportType().equalsIgnoreCase("Detail"))
			{	
				listOfGroupSubGroupWiseSales = objReportService.funProcessGroupSubGroupWiseDetailReport(posCode,fromDate,toDate,shiftNo,subGroupCode,groupCode,userCode,objSetupParameter.get("gEnableShiftYN").toString());
			}
			else
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptGroupSubGroupWiseSummaryReport.jrxml");
				listOfGroupSubGroupWiseSales = objReportService.funProcessGroupSubGroupWiseSummaryReport(posCode,fromDate,toDate,shiftNo,subGroupCode,groupCode,userCode,objSetupParameter.get("gEnableShiftYN").toString());
			}
		
	    	 	JasperDesign jd = JRXmlLoader.load(reportName);
	    	 	JasperReport jr = JasperCompileManager.compileReport(jd);
	    	 	JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfGroupSubGroupWiseSales);
	            JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
	            jprintlist.add(print);
	            String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
				String extension=".pdf";
				if (!objBean.getStrDocType().equals("PDF"))
				{
					objBean.setStrDocType("EXCEL");
					extension=".xls";
				}	
				String fileName = "GroupSubGroupWiseSalesReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
				filePath=filePath+"/"+fileName;	
	            
	            if (jprintlist.size()>0)
			    {
			    	ServletOutputStream servletOutputStream = resp.getOutputStream();
			    	if(objBean.getStrDocType().equals("PDF"))
			    	{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=GroupSubGroupWiseSalesReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".pdf");
				    exporter.exportReport();
					servletOutputStream.flush();
					servletOutputStream.close();
			    	}else
			    	{
			    		JRExporter exporter = new JRXlsExporter();
			    		resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						if(null!=source && source.equals("DayEndMail"))
							exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath));
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=GroupSubGroupWiseSalesReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".xls");
					    exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
			    	}
			    }else
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
