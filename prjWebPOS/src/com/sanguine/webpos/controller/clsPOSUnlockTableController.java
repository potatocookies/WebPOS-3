package com.sanguine.webpos.controller;

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

import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSMoveTableBean;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;
import com.sanguine.webpos.bean.clsPOSUserAccessBean;

@Controller
public class clsPOSUnlockTableController 
{
	@Autowired
	intfBaseService obBaseService;
	
	Map map=new HashMap();

	@RequestMapping(value = "/frmUnlockTable", method = RequestMethod.GET)
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
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmUnlockTable_1","command", new clsPOSMoveTableBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmUnlockTable","command", new clsPOSMoveTableBean());
		}else {
			return null;
		}
	}
	
	
	@RequestMapping(value = "/loadBillingInProgressData", method = RequestMethod.GET)
	public @ResponseBody Map loadBillingInProgressData(HttpServletRequest req)
	{
		Map mapBillingInProgData=new JSONObject();
		try
		{
			String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
			List list =null;
			StringBuilder sqlBuilder = new StringBuilder();
			
			sqlBuilder.append("select a.strTableNo,b.strTableName from tblitemrtemp a,tbltablemaster b "
                + " where a.strTableNo=b.strTableNo and a.strTableStatus='BillingInProgress' ");
			 if(!loginPosCode.equalsIgnoreCase("All"))
			 {
				 sqlBuilder.append(" and b.strPOSCode='" + loginPosCode + "' ");
	         } 
			 sqlBuilder.append(" group by a.strTableNo order by b.intSequence ");
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 List jArrData=new ArrayList<>();
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					Map mapTable=new HashMap<>();;
					mapTable.put("TableNo",obj[0].toString());
					mapTable.put("TableName",obj[1].toString());
					jArrData.add(mapTable);
				}
	           
		      }
		    if(null!=jArrData)
	        {
	        	for(int i=0; i<jArrData.size();i++)
	        	{
					Map jobj=(Map) jArrData.get(i);
					map.put((String)jobj.get("TableName"),(String)jobj.get("TableNo"));
	        	}
	        }
		    mapBillingInProgData.put("TableList",jArrData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mapBillingInProgData;
   }
	
	
	@RequestMapping(value = "/unlockTable", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMoveTableBean objBean,BindingResult result,HttpServletRequest req)
	{	
		String urlHits="1";
		String retResult="Failed to unlocked table";
	    
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			
		    clsPOSUserAccessBean obj=null;
		    int res=1;
		    List<clsPOSTableMasterBean> listAllTable=objBean.getListOfAllTable();
		    if(listAllTable.size()>0)
		    {
		    	for(int cnt=0;cnt<listAllTable.size();cnt++)
		    	{
		    	  clsPOSTableMasterBean objTableBean=listAllTable.get(cnt);
		    	   String[] arrNoBillingTable = objTableBean.getStrTableName().split(",");
                    for (int cn = 0; cn < arrNoBillingTable.length; cn++)
                    {
                    	if(map.containsKey(arrNoBillingTable[cn]))
                    	{
                    		String sql = "update tblitemrtemp set strTableStatus='Normal' where strTableNo in ('"+map.get(arrNoBillingTable[cn])+"');";
        		    		res=obBaseService.funExecuteUpdate(sql.toString(), "sql");
                    	}
                    }
                    retResult="Unlocked table successfully... ";
		    	}
		    }
		   	
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+retResult);
		
			return new ModelAndView("redirect:/frmUnlockTable.html?saddr="+urlHits);
			
			
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
		

	
}
