package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;
import com.sanguine.webpos.bean.clsPOSTableStatusReportBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSTableStatusReportController 
{
	@Autowired
	intfBaseService obBaseService;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map map=new HashMap();
	Map mapPOS=new HashMap();
	Map mapArea=new HashMap();

	@RequestMapping(value = "/frmPOSTableStatusReport", method = RequestMethod.GET)
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
		
		List poslist = new ArrayList();
		poslist.add("All");		
		List list=objMasterService.funFillPOSCombo(strClientCode);
		mapPOS.put("All", "All");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			poslist.add(Array.get(obj, 1).toString());
			mapPOS.put(Array.get(obj, 1).toString(),Array.get(obj, 0).toString());
		}
		model.put("posList",poslist);
		
		List arealist = new ArrayList();
		arealist.add("All");
		list=objMasterService.funGetAllAreaForMaster(strClientCode);
		mapArea.put("All", "All");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			clsAreaMasterModel objModel=(clsAreaMasterModel)list.get(cnt);
			arealist.add(objModel.getStrAreaName());
			mapArea.put(objModel.getStrAreaName(),objModel.getStrAreaCode());
		}
		model.put("areaList",arealist);
		
		
		List statuslist = new ArrayList();
		statuslist.add("All"); 
		statuslist.add("Vacant");
		statuslist.add("Billed");
		statuslist.add("Occupied");
		model.put("statuslist",statuslist);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSTableStatusReport_1","command", new clsPOSTableStatusReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSTableStatusReport","command", new clsPOSTableStatusReportBean());
		}else {
			return null;
		}
	}
	
	
	@RequestMapping(value = "/loadTableStatusReportData", method = RequestMethod.GET)
	public @ResponseBody Map loadTableStatusReportData(@RequestParam("POSCode") String POSCode,@RequestParam("Status") String status,@RequestParam("AreaCode") String areaCode,HttpServletRequest req)
	{
		String posDate=req.getSession().getAttribute("gPOSDate").toString();
		
		Map mapBillingInProgData=new JSONObject();
		try
		{
			//String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
			String timeDifference="";
			int pax=0;
        	
			List list =null;
			StringBuilder sqlBuilder = new StringBuilder();
			
			sqlBuilder.append(" select a.strTableNo, a.strTableName, a.strStatus,a.intPaxNo"
	               	+ " from tbltablemaster a left outer join tblitemrtemp b on a.strTableNo=b.strTableNo "
	               	+ " where a.strOperational='Y' ");
					if(!status.equals("All"))
		            {
						if(status.equalsIgnoreCase("Vacant"))
						{
							status="Normal";
						}
		            	sqlBuilder.append(" and a.strStatus='"+status+"' ");
		            }
		            if(!POSCode.equals("All"))
		            {
		            	sqlBuilder.append(" and a.strPOSCode='" + mapPOS.get(POSCode) + "' ");
		            }
		            if(!areaCode.equals("All"))
		            {
		            	sqlBuilder.append(" and a.strAreaCode='" + mapArea.get(areaCode) + "' ");
		            }
		            sqlBuilder.append(" group by a.strTableNo "
	               	+ "	order by a.intSequence, a.strTableName  ");
			
			
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 List<clsPOSTableMasterBean> listOfAllTable=new ArrayList<>();
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					clsPOSTableMasterBean objBean=new clsPOSTableMasterBean();
					objBean.setStrTableNo(obj[0].toString());
					objBean.setStrTableName(obj[1].toString());
					objBean.setStrStatus(obj[2].toString());
					pax=Integer.valueOf(obj[3].toString());
					objBean.setIntPaxCapacity(pax);
					
					if(obj[2].toString().equals("Occupied"))
	            	{
	            		 timeDifference = funGetTimeDiffInFirstKOTAndCurrentTime(obj[0].toString());
	                     if (timeDifference.startsWith("-"))
	                     {
	                    	 timeDifference = "";
	                     }
	            	}
	            	else if(obj[2].toString().equals("Billed"))
	            	{
	            		timeDifference = funGetTimeDiffInBilledAndCurrentTime(obj[0].toString(),posDate);
	                    if (timeDifference.startsWith("-"))
	                    {
	                    	timeDifference = "";
	                    }
	            	}
					objBean.setTmeTimeDiff(timeDifference);
					/*Map mapTable=new HashMap<>();;
					mapTable.put("TableNo",obj[0].toString());
					mapTable.put("TableName",obj[1].toString());
					mapTable.put("PaxNo",Integer.valueOf(obj[2].toString()));
				    jArrData.add(mapTable);
				    */
					listOfAllTable.add(objBean);
				}
	           
		      }
		    /*if(null!=jArrData)
	        {
	        	for(int i=0; i<jArrData.size();i++)
	        	{
					Map jobj=(Map) jArrData.get(i);
					map.put((String)jobj.get("TableName"),(String)jobj.get("TableNo"));
	        	}
	        }
		    */
		    mapBillingInProgData.put("TableList",listOfAllTable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mapBillingInProgData;
   }
	
	
	/*@RequestMapping(value = "/unlockTable", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMoveTableBean objBean,BindingResult result,HttpServletRequest req)
	{	
		String urlHits="1";
		String retResult="Failed to unlocked table";
	    
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("usercode").toString();
			
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
	*/
	
	private String funGetTimeDiffInFirstKOTAndCurrentTime(String tableNo)
    {
	    String timeDiffInFirstKOTAndCurrentTime = "";
	    StringBuilder sqlBuilder = new StringBuilder();
		
        try
        {
        	sqlBuilder.append("select TIME_FORMAT(TIMEDIFF(CURRENT_TIME(),time(dteDateCreated)),'%i:%s'),strKOTNo "
                    + "from tblitemrtemp  "
                    + "where strTableNo='" + tableNo + "' "
                    + "group by strKOTNo asc "
                    + "limit 1 ");
        	
        	List list = obBaseService.funGetList(sqlBuilder,"sql");
			if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					timeDiffInFirstKOTAndCurrentTime=obj[0].toString();
				}
			 }
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
        finally
        {
            return timeDiffInFirstKOTAndCurrentTime;
        }
    }
	
	
	private String funGetTimeDiffInBilledAndCurrentTime(String tableNo,String posDate)
    {
		String timeDiffInLastBilledAndCurrentTime = "";
		StringBuilder sqlBuilder = new StringBuilder();
        try
        {
        	
        	//String posDate=objMasterService.funGetPOSTransactionDate(posCode);
//		    String POSDate="";
//		    for(int cnt=0;cnt<posDate.size();cnt++)
//			{
//				POSDate=posDate.get(cnt).toString();
//			}
//		   
		    sqlBuilder.append("select TIME_FORMAT(TIMEDIFF(CURRENT_TIME(),time(dteBillDate)),'%i:%s'),a.strBillNo "
                    + "from tblbillhd a "
                    + "where date(a.dteBillDate)='" + posDate + "' "
                    + "and a.strTableNo='" + tableNo + "' "
                    + "and a.strBillNo not in(select strBillNo from tblbillsettlementdtl) "
                    + "order by a.dteBillDate desc "
                    + "limit 1; ");
            List list = obBaseService.funGetList(sqlBuilder,"sql");
			if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					timeDiffInLastBilledAndCurrentTime=obj[0].toString();
				}
			 }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return timeDiffInLastBilledAndCurrentTime;
        }
    }

}
