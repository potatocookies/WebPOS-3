package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSStatisticsBean;
import com.sanguine.webpos.bean.clsPOSRevenueHeadWiseSalesReportBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSStatisticsController 
{

	@Autowired
	intfBaseService obBaseService;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map mapPOS=new HashMap();

	@RequestMapping(value = "/frmPOSStatistics", method = RequestMethod.GET)
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
		List list=objMasterService.funFullPOSCombo(strClientCode);
		mapPOS.put("All", "All");
		for(int i=0;i<list.size();i++)
		{
			Object[] obj=(Object[]) list.get(i);
			poslist.add( obj[1].toString());
			mapPOS.put( obj[0].toString(), obj[1].toString());
		}
		model.put("posList",mapPOS);
		
		/*for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			poslist.add(Array.get(obj, 1).toString());
			mapPOS.put(Array.get(obj, 1).toString(),Array.get(obj, 0).toString());
		}
		model.put("posList",poslist);*/
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSStatistics_1","command", new clsPOSStatisticsBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSStatistics","command", new clsPOSStatisticsBean());
		}else {
			return null;
		}
	}
	
	@RequestMapping(value = "/loadSalesAchievedAndInProgressData", method = RequestMethod.GET)
	public @ResponseBody Map loadSalesAchievedAndInProgressData(@RequestParam("POSCode") String POSCode,HttpServletRequest req)
	{
		Map mapSalesAchievedData=new HashMap<>();
		try
		{
			String timeDifference="";
			int pax=0;
        	
			List list =null;
			StringBuilder sqlBuilder = new StringBuilder();
			
			sqlBuilder.append(" select c.strRevenueHead from tblitemmaster c "
                + "group by c.strRevenueHead order by c.strRevenueHead  ");
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 List<clsPOSRevenueHeadWiseSalesReportBean> listOfSalesData=new ArrayList<>();
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					clsPOSRevenueHeadWiseSalesReportBean objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(list.get(i).toString());
					objBean.setDblQuantity(0);
					objBean.setDblAmount(0);
					
			        String amountField = "sum(b.dblamount)";
//			        if (!chkWithDiscount.isSelected())
//			        {
//			            amountField = "sum(b.dblamount)-sum(b.dblDiscountAmt) ";
//			        }
			        StringBuilder sql = new StringBuilder();
			        sql.append("select c.strRevenueHead,sum(b.dblquantity)," + amountField + " "
		                    + " from tblbillhd a,tblbilldtl b,tblitemmaster c "
		                    + " where a.strBillNo=b.strBillNo and b.stritemcode=c.strItemCode ");
		            if (!POSCode.equals("All"))
		            {
		            	sql.append(" and a.strPosCode='" + POSCode + "' ");
		            }
		            sql.append(" group by c.strRevenueHead order by c.strRevenueHead;");
		            List listData = obBaseService.funGetList(sql,"sql");
		            if (listData!=null)
					 {
						for(int cnt=0; cnt<listData.size(); cnt++)
						{
							Object[] objData = (Object[]) listData.get(cnt);
							if (objData[0].toString().trim().equalsIgnoreCase(list.get(i).toString().trim()))
			                {
			                    objBean.setDblQuantity(Double.valueOf(objData[1].toString()));
								objBean.setDblAmount(Double.valueOf(objData[2].toString()));
			                }
						}	
					 }
		            
		            listOfSalesData.add(objBean);
				}
	           
		      }
	
			 List<clsPOSRevenueHeadWiseSalesReportBean> listOfSalesInProgressData=listSalesInProgrssDtl(POSCode);
			 Map<String,List<clsPOSRevenueHeadWiseSalesReportBean>> listOfAvgCoversAndCheck=funCalculateAvgSalesInfo(POSCode);
			 mapSalesAchievedData.put("listOfAvgCoversForSalesAchieved",listOfAvgCoversAndCheck.get("listOfAvgCoversForSalesAchieved"));
			 mapSalesAchievedData.put("listOfAvgCheckForSalesAchieved",listOfAvgCoversAndCheck.get("listOfAvgCheckForSalesAchieved"));
			 mapSalesAchievedData.put("listOfAvgCoversForSalesInProgress",listOfAvgCoversAndCheck.get("listOfAvgCoversForSalesInProgress"));
			 mapSalesAchievedData.put("listOfAvgCheckForSalesInProgress",listOfAvgCoversAndCheck.get("listOfAvgCheckForSalesInProgress"));		 
			 mapSalesAchievedData.put("SalesAchievedData",listOfSalesData);
			 mapSalesAchievedData.put("SalesInProgressData",listOfSalesInProgressData);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mapSalesAchievedData;
   }
	
	
	
	private List listSalesInProgrssDtl(String POSCode)
	{
		List<clsPOSRevenueHeadWiseSalesReportBean> listOfSalesInProgressData=new ArrayList<>();
		
		try
		{
			List list =null;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append(" select c.strRevenueHead from tblitemmaster c "
                + "group by c.strRevenueHead order by c.strRevenueHead  ");
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					clsPOSRevenueHeadWiseSalesReportBean objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(list.get(i).toString());
					objBean.setDblQuantity(0);
					objBean.setDblAmount(0);
					
			        String amountField = "sum(b.dblamount)";
//			        if (!chkWithDiscount.isSelected())
//			        {
//			            amountField = "sum(b.dblamount)-sum(b.dblDiscountAmt) ";
//			        }
			        StringBuilder sql = new StringBuilder();
			        
			        sql.append("select b.strRevenueHead,sum(a.dblItemQuantity),sum(a.dblAmount) "
		                    + " from tblitemrtemp a,tblitemmaster b "
		                    + " where a.stritemcode=b.strItemCode and a.strNCKotYN='N' ");
		            if (!POSCode.equals("All"))
		            {
		            	sql.append(" and a.strPosCode='" + POSCode + "' ");
		            }
		            sql.append(" group by b.strRevenueHead order by b.strRevenueHead;");
			        
			        List listData = obBaseService.funGetList(sql,"sql");
		            if (listData!=null)
					 {
						for(int cnt=0; cnt<listData.size(); cnt++)
						{
							Object[] objData = (Object[]) listData.get(cnt);
							if (objData[0].toString().trim().equalsIgnoreCase(list.get(i).toString().trim()))
			                {
			                    objBean.setDblQuantity(Double.valueOf(objData[1].toString()));
								objBean.setDblAmount(Double.valueOf(objData[2].toString()));
			                }
						}	
					 }
		            
		            listOfSalesInProgressData.add(objBean);
				}
	           
		      }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfSalesInProgressData;
   }
	
	private Map<String,List<clsPOSRevenueHeadWiseSalesReportBean>> funCalculateAvgSalesInfo(String POSCode)
	{
		Map<String,List<clsPOSRevenueHeadWiseSalesReportBean>> listOfAvgCoversAndCheck=new HashMap<>();
		List<clsPOSRevenueHeadWiseSalesReportBean> listOfAvgCoversForSalesAchieved=new ArrayList<>();
		List<clsPOSRevenueHeadWiseSalesReportBean> listOfAvgCheckForSalesAchieved=new ArrayList<>();
		List<clsPOSRevenueHeadWiseSalesReportBean> listOfAvgCoversForSalesInProgress=new ArrayList<>();
		List<clsPOSRevenueHeadWiseSalesReportBean> listOfAvgCheckForSalesInProgress=new ArrayList<>();
		int coversTurned = 0, tablesTurned = 0, coversServed = 0, busyTables = 0;

		try
		{
			
			List list =null;
			StringBuilder sqlBuilder = new StringBuilder();
			//executing query for coversTurned & tablesTurned
			sqlBuilder.append(" select count(distinct(strTableNo)),count(intPaxNo) from tblbillhd  ");
			
			if (!POSCode.equals("All"))
            {
				sqlBuilder.append("  where strPOSCode='" + POSCode + "' ");
            }
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] objData = (Object[]) list.get(i);
					tablesTurned=Integer.valueOf(objData[0].toString());
					coversTurned=Integer.valueOf(objData[1].toString());
				}
			 }
			 
			//executing query for coversServed & busyTables
			sqlBuilder.setLength(0);
			sqlBuilder.append(" select count(distinct(strTableNo)),count(intPaxNo) from tblitemrtemp where strNCKotYN='N'  ");
				
			if (!POSCode.equals("All"))
            {
				sqlBuilder.append("  and strPOSCode='" + POSCode + "'");
            }
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] objData = (Object[]) list.get(i);
					busyTables=Integer.valueOf(objData[0].toString());
					coversServed=Integer.valueOf(objData[1].toString());
				}
			 }
			
			//executing query for avg covers and avg check of sales achieved
			 sqlBuilder.setLength(0);
			 sqlBuilder.append(" select c.strItemType,sum(b.dblamount) "
                + " from tblbillhd a,tblbilldtl b,tblitemmaster c "
                + " where a.strBillNo=b.strBillNo and b.stritemcode=c.strItemCode  ");
			
			if (!POSCode.equals("All"))
            {
				sqlBuilder.append(" and a.strPosCode='" + POSCode + "' ");
            }
			sqlBuilder.append(" group by c.strItemType order by c.strItemType;");
	        
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] objData = (Object[]) list.get(i);
					clsPOSRevenueHeadWiseSalesReportBean objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(objData[0].toString());
					objBean.setDblQuantity(coversTurned);
					objBean.setDblAmount(Math.rint(Double.valueOf(objData[1].toString())/ coversTurned) );
					listOfAvgCoversForSalesAchieved.add(objBean);
					objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(objData[0].toString());
					objBean.setDblQuantity(tablesTurned);
					objBean.setDblAmount(Math.rint(Double.valueOf(objData[1].toString())/ tablesTurned) );
					listOfAvgCheckForSalesAchieved.add(objBean);
				}
		      }
			 
			 
			 
			//executing query for avg covers and avg check of sales in progress
			 sqlBuilder.setLength(0);
			 sqlBuilder.append(" select c.strItemType,sum(a.dblamount) "
                + " from tblitemrtemp a,tblitemmaster c "
                + " where a.stritemcode=c.strItemCode   ");
			
			if (!POSCode.equals("All"))
            {
				sqlBuilder.append(" and a.strPosCode='" + POSCode + "' ");
            }
			sqlBuilder.append(" group by c.strItemType order by c.strItemType;");
	        
			 list = obBaseService.funGetList(sqlBuilder,"sql");
			 if (list!=null)
			 {
				for(int i=0; i<list.size(); i++)
				{
					Object[] objData = (Object[]) list.get(i);
					clsPOSRevenueHeadWiseSalesReportBean objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(objData[0].toString());
					objBean.setDblQuantity(coversServed);
					objBean.setDblAmount(Math.rint(Double.valueOf(objData[1].toString())/ coversServed) );
					listOfAvgCoversForSalesInProgress.add(objBean);
					objBean=new clsPOSRevenueHeadWiseSalesReportBean();
					objBean.setStrRevenueHead(objData[0].toString());
					objBean.setDblQuantity(busyTables);
					objBean.setDblAmount(Math.rint(Double.valueOf(objData[1].toString())/ busyTables) );
					listOfAvgCheckForSalesInProgress.add(objBean);
				}
		      }
			 
			 listOfAvgCoversAndCheck.put("listOfAvgCoversForSalesAchieved", listOfAvgCoversForSalesAchieved);
			 listOfAvgCoversAndCheck.put("listOfAvgCheckForSalesAchieved",listOfAvgCheckForSalesAchieved);
			 listOfAvgCoversAndCheck.put("listOfAvgCoversForSalesInProgress", listOfAvgCoversForSalesInProgress);
			 listOfAvgCoversAndCheck.put("listOfAvgCheckForSalesInProgress",listOfAvgCheckForSalesInProgress);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listOfAvgCoversAndCheck;
   }
}
