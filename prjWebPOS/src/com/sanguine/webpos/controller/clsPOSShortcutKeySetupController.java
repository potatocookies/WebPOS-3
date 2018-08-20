package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.sanguine.webpos.bean.clsPOSShortcutSetupBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSShortcutKeySetupController 
{
	
	@Autowired
	clsPOSMasterService  objMasterService;
	
	@Autowired
	intfBaseService obBaseService;
	
	List []masterList = new List[13];
	List []transList = new List[13];
	List []reportList = new List[13];
	
	@RequestMapping(value = "/frmPOSShortcutKeySetup", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception
	{
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		Map hmData=objMasterService.funGetFormListByModuleType("M");
		List listData=(List) hmData.get("jArr");
		for(int i=1;i<=12;i++)
		{
			masterList[i]=new ArrayList<>();
			for(int cnt=0;cnt<listData.size();cnt++)
			{
				Map hmChild=(Map) listData.get(cnt);
				masterList[i].add(hmChild.get("moduleName").toString());
			}
			model.put("masterList"+i,masterList[i]);
		}
		funSetSelectedModule("M",masterList);
		
		hmData=objMasterService.funGetFormListByModuleType("T");
		listData=(List) hmData.get("jArr");
		for(int i=1;i<=12;i++)
		{
			transList[i]=new ArrayList<>();
			for(int cnt=0;cnt<listData.size();cnt++)
			{
				Map hmChild=(Map) listData.get(cnt);
				transList[i].add(hmChild.get("moduleName").toString());
			}
			model.put("transList"+i,transList[i]);
		}
		funSetSelectedModule("T",transList);
		
		hmData=objMasterService.funGetFormListByModuleType("R");
		listData=(List) hmData.get("jArr");
		for(int i=1;i<=12;i++)
		{
			reportList[i]=new ArrayList<>();
			for(int cnt=0;cnt<listData.size();cnt++)
			{
				Map hmChild=(Map) listData.get(cnt);
				reportList[i].add(hmChild.get("moduleName").toString());
			}
			model.put("reportList"+i,reportList[i]);
		}
		funSetSelectedModule("R",reportList);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSShortcutKeySetup_1","command", new clsPOSShortcutSetupBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSShortcutKeySetup","command", new clsPOSShortcutSetupBean());
		}else {
			return null;
		}
	}
	
	
	 private void funSetSelectedModule(String moduleType, List[] arrListForms) throws Exception
	   {
	       int cnt=1;
	       StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strModuleName from tblshortcutkeysetup "
		            + " where strModuleType='"+moduleType+"' order by strShortcutKey");
			List listModules = obBaseService.funGetList(sqlBuilder, "sql");	
			if(listModules!=null)
			{
			    for(int i=0;i<listModules.size();i++)
			    {
			    	String moduleName =listModules.get(i).toString();
			    	if(arrListForms[cnt].get(0).toString().equalsIgnoreCase(moduleName))
			    	{}
			    	else
			    	{arrListForms[cnt].remove(moduleName);
			    	 arrListForms[cnt].add(0, moduleName);
			    	}
			    	cnt++;
			   }
	       }
	  }	
	 
	 
	 
		@RequestMapping(value = "/savePOSShortcutKeySetup", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSShortcutSetupBean objBean,BindingResult result,HttpServletRequest req)
		{
			String res="false";
			String urlHits="1";
			
			try
			{
				urlHits=req.getParameter("saddr").toString();
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				funClearOldData();
				res=funSetMastersShotcutKeys(objBean);
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+res);
				
			
				return new ModelAndView("redirect:/frmPOSShortcutKeySetup.html?saddr="+urlHits);
				
				
			}
			catch(Exception ex)
			{
				urlHits="1";
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
		
		
		private String funSetMastersShotcutKeys(clsPOSShortcutSetupBean objBean)
	    {
			String result="false";
	        try
	        {
	            String masterSql = "insert into tblshortcutkeysetup(strShortcutKey,strModuleName,strModuleType)values";
	            for (int i = 1; i <= 12; i++)
	            {
	                switch (i)
	                {
	                    case 1:
	                        masterSql = masterSql + "('112','" + objBean.getStrMasterList1() + "','M') ";
	                        break;
	                    case 2:
	                        masterSql = masterSql + ",('113','" + objBean.getStrMasterList2() + "','M') ";
	                        break;
	                    case 3:
	                        masterSql = masterSql + ",('114','" + objBean.getStrMasterList3()+ "','M') ";
	                        break;
	                    case 4:
	                        masterSql = masterSql + ",('115','" + objBean.getStrMasterList4() + "','M') ";
	                        break;
	                    case 5:
	                        masterSql = masterSql + ",('116','" + objBean.getStrMasterList5() + "','M') ";
	                        break;
	                    case 6:
	                        masterSql = masterSql + ",('117','" + objBean.getStrMasterList6() + "','M') ";
	                        break;
	                    case 7:
	                        masterSql = masterSql + ",('118','" + objBean.getStrMasterList7() + "','M') ";
	                        break;
	                    case 8:
	                        masterSql = masterSql + ",('119','" + objBean.getStrMasterList8() + "','M') ";
	                        break;
	                    case 9:
	                        masterSql = masterSql + ",('120','" + objBean.getStrMasterList9() + "','M') ";
	                        break;
	                    case 10:
	                        masterSql = masterSql + ",('121','" + objBean.getStrMasterList10() + "','M') ";
	                        break;
	                    case 11:
	                        masterSql = masterSql + ",('122','" + objBean.getStrMasterList12() + "','M') ";
	                        break;
	                    case 12:
	                        masterSql = masterSql + ",('123','" + objBean.getStrMasterList12() + "','M') ";
	                        break;
	                }
	            }

	            int i = obBaseService.funExecuteUpdate(masterSql, "sql");
	            if (i > 0)
	            {
	            	result=funSetTransactionsShotcutKeys(objBean);
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return result;
	    }
		
		 private String funSetTransactionsShotcutKeys(clsPOSShortcutSetupBean objBean)
		    {
			 String result="false";
		        try
		        {
		            String transactionSql = "insert into tblshortcutkeysetup(strShortcutKey,strModuleName,strModuleType)values";
		            for (int i = 1; i <= 12; i++)
		            {
		                switch (i)
		                {
		                    case 1:
		                        transactionSql = transactionSql + "('112','" + objBean.getStrTransList1() + "','T') ";
		                        break;
		                    case 2:
		                        transactionSql = transactionSql + ",('113','" +  objBean.getStrTransList2() + "','T') ";
		                        break;
		                    case 3:
		                        transactionSql = transactionSql + ",('114','" +  objBean.getStrTransList3() + "','T') ";
		                        break;
		                    case 4:
		                        transactionSql = transactionSql + ",('115','" +  objBean.getStrTransList4() + "','T') ";
		                        break;
		                    case 5:
		                        transactionSql = transactionSql + ",('116','" +  objBean.getStrTransList5() + "','T') ";
		                        break;
		                    case 6:
		                        transactionSql = transactionSql + ",('117','" +  objBean.getStrTransList6() + "','T') ";
		                        break;
		                    case 7:
		                        transactionSql = transactionSql + ",('118','" +  objBean.getStrTransList7() + "','T') ";
		                        break;
		                    case 8:
		                        transactionSql = transactionSql + ",('119','" +  objBean.getStrTransList8() + "','T') ";
		                        break;
		                    case 9:
		                        transactionSql = transactionSql + ",('120','" +  objBean.getStrTransList9() + "','T') ";
		                        break;
		                    case 10:
		                        transactionSql = transactionSql + ",('121','" +  objBean.getStrTransList10() + "','T') ";
		                        break;
		                    case 11:
		                        transactionSql = transactionSql + ",('122','" +  objBean.getStrTransList11() + "','T') ";
		                        break;
		                    case 12:
		                        transactionSql = transactionSql + ",('123','" +  objBean.getStrTransList12() + "','T') ";
		                        break;
		                }
		            }
		            int i = obBaseService.funExecuteUpdate(transactionSql, "sql");
		            if (i > 0)
		            {
		            	result=funSetReportsShotcutKeys(objBean);
		            }
		        }
		        catch (Exception e)
		        {
		           e.printStackTrace();
		        }
		        return result;
		    }
		 
		 
		 private String funSetReportsShotcutKeys(clsPOSShortcutSetupBean objBean)
		    {
			 String result="false";
		        try
		        {
		            String reportSql = "insert into tblshortcutkeysetup(strShortcutKey,strModuleName,strModuleType)values";
		            for (int i = 1; i <= 12; i++)
		            {
		                switch (i)
		                {
		                    case 1:
		                        reportSql = reportSql + "('112','" + objBean.getStrReportList1() + "','R') ";
		                        break;
		                    case 2:
		                        reportSql = reportSql + ",('113','" + objBean.getStrReportList2()  + "','R') ";
		                        break;
		                    case 3:
		                        reportSql = reportSql + ",('114','" +objBean.getStrReportList3()  + "','R') ";
		                        break;
		                    case 4:
		                        reportSql = reportSql + ",('115','" + objBean.getStrReportList4()  + "','R') ";
		                        break;
		                    case 5:
		                        reportSql = reportSql + ",('116','" + objBean.getStrReportList5()  + "','R') ";
		                        break;
		                    case 6:
		                        reportSql = reportSql + ",('117','" + objBean.getStrReportList6()  + "','R') ";
		                        break;
		                    case 7:
		                        reportSql = reportSql + ",('118','" + objBean.getStrReportList7()  + "','R') ";
		                        break;
		                    case 8:
		                        reportSql = reportSql + ",('119','" + objBean.getStrReportList8()  + "','R') ";
		                        break;
		                    case 9:
		                        reportSql = reportSql + ",('120','" + objBean.getStrReportList9()  + "','R') ";
		                        break;
		                    case 10:
		                        reportSql = reportSql + ",('121','" + objBean.getStrReportList10()  + "','R') ";
		                        break;
		                    case 11:
		                        reportSql = reportSql + ",('122','" + objBean.getStrReportList11()  + "','R') ";
		                        break;
		                    case 12:
		                        reportSql = reportSql + ",('123','" + objBean.getStrReportList12() + "','R') ";
		                        break;
		                }
		            }

		            int i = obBaseService.funExecuteUpdate(reportSql, "sql");
		            if (i > 0)
		            {
		            	result="true";
		            }
		        }
		        catch (Exception e)
		        {
		        	 e.printStackTrace();
		        }
		        return result;
		    }

		  private void funClearOldData()
		    {
		        try
		        {
		            obBaseService.funExecuteUpdate("delete from tblshortcutkeysetup", "sql");;
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
	 
	 
}
