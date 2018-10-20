package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;

@Controller
public class clsPOSMailDayEndReportsController {

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	 intfBaseService objBaseService;
	
	@RequestMapping(value="/frmPOSMailDayEndReports",method= RequestMethod.GET)
	public ModelAndView funOpenPOSTools(Map<String,Object> model,HttpServletRequest req)
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMailDayEndReports_1","command", new clsPOSDayEndProcessBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMailDayEndReports","command", new clsPOSDayEndProcessBean());
		}else {
			return null;
		}
	}
	
	//all reports data from DB through web service 
		@RequestMapping(value ="/loadAllMailDayEndReports",method =RequestMethod.GET)
		public @ResponseBody List<clsPOSDayEndProcessBean> funLoadAllReportsName(HttpServletRequest request)
		{
			List<clsPOSDayEndProcessBean> listbean=new ArrayList<clsPOSDayEndProcessBean>();
			clsPOSDayEndProcessBean obBean;
			
			String strClientCode=request.getSession().getAttribute("gClientCode").toString();
			String POSCode=request.getSession().getAttribute("loginPOS").toString();
			
			Map mapObj = funLoadAllReportsName(POSCode,strClientCode);
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
				
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			alReportName= gson.fromJson(mapObj.get("ReportName").toString(), listType);
			alCheckRpt= gson.fromJson(mapObj.get("CheckReport").toString(), listType);
			for(int i=0;i<alReportName.size();i++)
			{
				obBean=new clsPOSDayEndProcessBean();
				obBean.setStrReportName(alReportName.get(i).toString());
				if(alCheckRpt.size()==alReportName.size())
				{
					obBean.setStrReportCheck(Boolean.parseBoolean(alCheckRpt.get(i).toString()));
				}
				else
				{
					obBean.setStrReportCheck(Boolean.parseBoolean("false"));
				}
				
				listbean.add(obBean);
			}
			return listbean;
		}
		
		@RequestMapping(value = "/MailDayEndReport", method = RequestMethod.POST)
		public ModelAndView funGetSelectedMailReport(@ModelAttribute("command") @Valid clsPOSDayEndProcessBean objBean,BindingResult result,HttpServletRequest req)
		{
			JSONObject jsMailReportData=new JSONObject();
			String urlHits="2";
			String userCode=req.getSession().getAttribute("gUserCode").toString();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
			String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		 	String strPOSCode=req.getSession().getAttribute("loginPOS").toString();

			clsPOSDayEndProcessBean obDayEndd;
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
			if(!result.hasErrors())
			{
				List<clsPOSDayEndProcessBean> listMailReport =objBean.getListMailReport();
				for(int i=0;i<listMailReport.size();i++)
				{
					obDayEndd=listMailReport.get(i);
					//alReportName.add(obDayEndd.getStrReportName());
					if(obDayEndd.getStrReportCheck()==null)
					{
						alCheckRpt.add(false);
					}else{
						alReportName.add(obDayEndd.getStrReportName());
						alCheckRpt.add(obDayEndd.getStrReportCheck());
					}
				}
				
			}
			String fromDate=objBean.getFromDate();
			String toDate=objBean.getToDate();
	 		Gson gson = new Gson();
	 	    Type type = new TypeToken<List<String>>() {}.getType();
	        String ReportName = gson.toJson(alReportName, type);
	        String CheckReport = gson.toJson(alCheckRpt, type);
	        jsMailReportData.put("ReportName", ReportName);
	        jsMailReportData.put("fromDate", fromDate);
	        jsMailReportData.put("toDate", toDate);
	        jsMailReportData.put("strShiftNo", "1");
	        jsMailReportData.put("strPOSCode", strPOSCode);
	        jsMailReportData.put("userCode", userCode);
	        jsMailReportData.put("strPOSDate", strPOSDate);
	        jsMailReportData.put("strClientCode", strClientCode);
	        jsMailReportData.put("emailReport", objBean.getMailReport());
	        String Status="true";
	        JSONObject job=new JSONObject();
	         job = objGlobalFunctions.funPOSTMethodUrlJosnObjectData("http://localhost:8080/prjSanguineWebService/WebPOSReport/funMailDayEndReport",jsMailReportData);
	        
			return new ModelAndView("redirect:/frmPOSMailDayEndReports.html?saddr="+urlHits);//"redirect:/frmPOSDayEndDialog.html?saddr="+urlHits
	      
		}
		
		public Map funLoadAllReportsName(String strPOSCode,String strClientCode)
		{
			Map mapReportNames=new HashMap<>();
			ArrayList alReportName=new ArrayList<String>();
			ArrayList alCheckRpt=new ArrayList<Boolean>();
			try{
			
				StringBuilder  sql= new StringBuilder("select a.strModuleName,a.strFormName from tblforms a "
	                    + "where a.strModuleType='R' "
	                    + "order by a.intSequence;");
				 
	             List listRPT= objBaseService.funGetList(sql, "sql");
	             if(listRPT.size()>0)
	             {
	             	for(int i=0;i<listRPT.size();i++)
	             	{
	             		Object[] ob=(Object[])listRPT.get(i);
	             		alReportName.add(ob[0].toString());
	             	}
	             }
	             
	             sql.setLength(0);
	             sql.append("select  strPOSCode,strReportName,date(dtePOSDate) "
	                    + "from tbldayendreports "
	                    + "where strPOSCode='"+strPOSCode+"' "
	                    + "and strClientCode='" + strClientCode + "';");
	             
	             List RPT= objBaseService.funGetList(sql, "sql");
	             
	             if(RPT.size()>0)
	             {
	             	for(int i=0;i<RPT.size();i++)
	             	{
	             		Object[] ob=(Object[])RPT.get(i);
	             		 String reportName=ob[1].toString();
	             		for (int j = 0; j < alReportName.size(); j++)
	                    {
	             			if(alCheckRpt.size()==alReportName.size())
	             			{
		                       if(alReportName.get(j)!=null && alReportName.get(j).toString().equalsIgnoreCase(reportName))                        
		                        {
		                        	alCheckRpt.set(j, Boolean.parseBoolean("true"));
		                        }
		                       
	             			}
	             			else{
	             				 if(alReportName.get(j)!=null && alReportName.get(j).toString().equalsIgnoreCase(reportName))                        
	 	                        {
	 	                        	alCheckRpt.add(j, Boolean.parseBoolean("true"));
	 	                        }
	 	                        else{
	 	                        	alCheckRpt.add(j, Boolean.parseBoolean("false"));
	 	                        }
	             			}
	                    }
	             		//al.add(ob[0].toString());
	             	}
	             }
	             
	             		Gson gson = new Gson();
				 	    Type type = new TypeToken<List<String>>() {}.getType();
			            String ReportName = gson.toJson(alReportName, type);
			            String CheckReport = gson.toJson(alCheckRpt, type);
			            mapReportNames.put("ReportName", ReportName);
			            mapReportNames.put("CheckReport", CheckReport);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return mapReportNames;
		}
		
}
