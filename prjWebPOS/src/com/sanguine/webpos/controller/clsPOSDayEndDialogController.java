package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;

@Controller
public class clsPOSDayEndDialogController {
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
		intfBaseService objBaseService;
	
	@RequestMapping(value = "/frmPOSDayEndDialog", method = RequestMethod.GET)
	public ModelAndView funOpenPOSTools(Map<String, Object> model,HttpServletRequest req)
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndDialog","command", new clsPOSDayEndProcessBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndDialog","command", new clsPOSDayEndProcessBean());
		}else {
			return null;
		}
		
	}
	//all reports data from DB through web service 
	@RequestMapping(value ="/loadAllReportsforMail",method =RequestMethod.GET)
	public @ResponseBody List<clsPOSDayEndProcessBean> funLoadAllReportsName(HttpServletRequest request)
	{
		List<clsPOSDayEndProcessBean> listbean=new ArrayList<clsPOSDayEndProcessBean>();
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();
		String strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		
		listbean=funLoadAllReportsName(strPOSCode, strClientCode);
		return listbean;
	}
	
	@RequestMapping(value = "/DayEndMailReport", method = RequestMethod.POST)
	public ModelAndView funGetSelectedMailReport(@ModelAttribute("command") @Valid clsPOSDayEndProcessBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="2";
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
	 	String strPOSCode=req.getSession().getAttribute("loginPOS").toString();

		if(!result.hasErrors())
		{
			List<clsPOSDayEndProcessBean> listMailReport =objBean.getListMailReport();
			funSendDayEndMailReports(listMailReport, userCode, strClientCode, strPOSDate, strPOSCode);
		}
		return new ModelAndView("redirect:/frmPOSDayEndDialog.html?saddr="+urlHits);
	}
	
	public List<clsPOSDayEndProcessBean> funLoadAllReportsName(String strPOSCode,String strClientCode)
	{
		List<clsPOSDayEndProcessBean> listReports=new ArrayList<>();
		try{
			clsPOSDayEndProcessBean obBean=new clsPOSDayEndProcessBean();
			StringBuilder sbSql=new StringBuilder("select a.strModuleName,a.strFormName from tblforms a "
                    + "where a.strModuleType='R' "
                    + "order by a.intSequence;");
			 List listRPT=objBaseService.funGetList(sbSql, "sql");
             if(listRPT.size()>0)
             {
             	for(int i=0;i<listRPT.size();i++)
             	{
             		obBean=new clsPOSDayEndProcessBean();
             		Object[] ob=(Object[])listRPT.get(i);
             		obBean.setStrReportName(ob[0].toString());
             		listReports.add(obBean);
             	}
             }
             sbSql.setLength(0);
             sbSql.append("select  strPOSCode,strReportName,date(dtePOSDate) "
                    + "from tbldayendreports "
                    + "where strPOSCode='"+strPOSCode+"' "
                    + "and strClientCode='" + strClientCode + "';");
             
             listRPT=objBaseService.funGetList(sbSql, "sql");
             if(listRPT.size()>0)
             {
             	for(int i=0;i<listRPT.size();i++)
             	{
             		Object[] ob=(Object[])listRPT.get(i);
             		 String reportName=ob[1].toString();
             		 for(int j = 0; j < listReports.size(); j++){
             			if(listReports.get(j).getStrReportName()!=null && listReports.get(j).getStrReportName().equalsIgnoreCase(reportName))                        
                        {
             				listReports.get(j).setStrReportCheck(true);
                        }
             		 }
             	}
             }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listReports;
	}
	
	public void funSendDayEndMailReports(List<clsPOSDayEndProcessBean> listMailReport ,String userCode,String strClientCode,String strPOSDate,String strPOSCode)
	{
		try{
			StringBuilder sbSql=new StringBuilder ();
			
			Date dtCurrent = new Date(); 
		 	String currentTime = dtCurrent.getHours() + ":" + dtCurrent.getMinutes() + ":" + dtCurrent.getSeconds();
		 	String POSDateforRTransaction=strPOSDate+" "+currentTime;
		 	
		 	objBaseService.funExecuteUpdate("truncate table tbldayendreports;", "sql");
		 	for(int j=0;j<listMailReport.size();j++)
		 	{
		 		if(listMailReport.get(j).getStrReportCheck()!=null && listMailReport.get(j).getStrReportCheck()){
		 			sbSql.setLength(0);
			 		sbSql.append("INSERT INTO tbldayendreports (strPOSCode, strClientCode, strReportName, dtePOSDate, "
			 				+ "strUserCreated,strUserEdited, dteDateCreated, dteDateEdited,strDataPostFlag)"
			 				+ " VALUES ('"+strPOSCode+"', '"+strClientCode+"', '"+listMailReport.get(j).getStrReportName()+"', '"+strPOSDate+"',"
			 						+ " '"+userCode+"','"+userCode+"', '"+POSDateforRTransaction+"', '"+POSDateforRTransaction+"','N');");
			 		
			 		objBaseService.funExecuteUpdate(sbSql.toString(), "sql");
			 		
		 		}
		 	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}