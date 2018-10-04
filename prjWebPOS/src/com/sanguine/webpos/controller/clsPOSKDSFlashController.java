package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSKOTAnalysisBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.bean.clsPOSWaiterAnalysisBean;
import com.sanguine.webpos.model.clsCostCenterMasterModel;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.model.clsWaiterMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


	
@Controller
public class clsPOSKDSFlashController 
{ 
	 @Autowired
	 private clsPOSReportService objReportService;
	 
	 @Autowired
	 private clsPOSMasterService objMasterService;
	
	 Map posMap=new TreeMap();
	 Map costCenterMap = new TreeMap();
	 Map waiterMap = new TreeMap();
	@RequestMapping(value = "/frmKDSFlash", method = RequestMethod.GET)
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
		
		 
		posMap.put("All", "All");
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				posMap.put(obj[0].toString(), obj[1].toString());
			}
		}
		model.put("posList",posMap);
		
		List listReportType=new ArrayList();
		listReportType.add("Cost Center");
		listReportType.add("Group");
		listReportType.add("SubGroup");
		listReportType.add("Menu Head");
		model.put("listReportType",listReportType);
		
		List listOfCostCenter = objMasterService.funGetAllCostCenterMaster(strClientCode);
		costCenterMap.put("All", "All");
		if(listOfCostCenter!=null)
		{
			for(int i =0 ;i<listOfCostCenter.size();i++)
			{
				clsCostCenterMasterModel objModel = (clsCostCenterMasterModel) listOfCostCenter.get(i);
				costCenterMap.put(objModel.getStrCostCenterCode(),objModel.getStrCostCenterName());
			}
		}
		model.put("listCostCenter",costCenterMap);
		
		List listOfWaiter = objMasterService.funGetAllWaitersForMaster(strClientCode);
		waiterMap.put("All", "All");
		if(listOfWaiter!=null)
		{
			for(int i =0 ;i<listOfWaiter.size();i++)
			{
				clsWaiterMasterModel objModel = (clsWaiterMasterModel) listOfWaiter.get(i);
				waiterMap.put(objModel.getStrWaiterNo(),objModel.getStrWShortName());
			}
		}
		model.put("listWaiter",waiterMap);
		
		List listType = new ArrayList();
		listType.add("Detail");
		model.put("listType",listType);
		
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);

		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmKDSFlash_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmKDSFlash","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}	
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadKDSFlash"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadKDSFlash(HttpServletRequest req)
	    {
		    LinkedHashMap resMap = new LinkedHashMap();
		    resMap.put("status","Found");
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	     
		    String fromDate=req.getParameter("fromDate");
		 
			String toDate=req.getParameter("toDate");
		
			String reportType=req.getParameter("reportType");
			String posCode=req.getParameter("posName");
			String costCenterCode=req.getParameter("costCenterName");
			String waiterNo=req.getParameter("waiterName");
			String strType = req.getParameter("strType");
			
			resMap=funGetData(clientCode,fromDate,toDate,reportType,posCode,costCenterCode,waiterNo,strType);
	        if(resMap.size()==0)
	        {
	        	resMap.put("status","Not Found");
	        }
	        
			return resMap;
	    }
	  
	  
	  
		@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/processKDSFlash", method = RequestMethod.POST)	
		private ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
		{
			LinkedHashMap resMap = new LinkedHashMap();
		    resMap.put("status","Found");
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();    
		    String fromDate=objBean.getFromDate();	 
			String toDate=objBean.getToDate();
			String reportType=objBean.getStrReportType();
			String posCode=objBean.getStrPOSName();
			String costCenterCode=objBean.getStrItemName();
			String waiterNo=objBean.getStrWShortName();
			String strType =objBean.getStrType();
			
			resMap=funGetData(clientCode,fromDate,toDate,reportType,posCode,costCenterCode,waiterNo,strType);
			List exportList=new ArrayList();	
			if(resMap.size()!=0)
	        {
				String dteFromDate=objBean.getFromDate();
				String dteToDate=objBean.getToDate();
				String FileName="KDSFlash_"+dteFromDate+"_To_"+dteToDate;
			
				exportList.add(FileName);
								
				List List=(List)resMap.get("ColHeader");
				
				String[] headerList = new String[List.size()];
				for(int i = 0; i < List.size(); i++){
					headerList[i]=(String)List.get(i);
				}
				
				exportList.add(headerList);
				
				List dataList=(List)resMap.get("listArr");
				for(int i=0;i<2;i++)
				{
					List list=new ArrayList();
					for(int j = 0; i < List.size(); i++)
					{
						list.add(" ");
					}
					dataList.add(list);
				}
				
				List totalList=(List)resMap.get("totalList");
				for(int j = 0; j < totalList.size(); j++)
				{
					List subTotalList=new ArrayList();
					subTotalList.add(totalList.get(j));
					dataList.add(subTotalList);
				}
				
				
				exportList.add(dataList);
	        }
			return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);	
		}
	
	
	  
	  @SuppressWarnings({ "unchecked" })
		private LinkedHashMap funGetData(String clientCode,String fromDate,String toDate, String reportType,String posCode, String costCenterCode, String waiterNo,String strType)
		  {									
				 LinkedHashMap resMap = new LinkedHashMap();
				 List listArrColHeader = new ArrayList();
				 List totalList = new ArrayList();
				 List listArr = new ArrayList();
				 List list = new ArrayList();
				 double value = 0.0;;
		         List colHeader = new ArrayList();
		         
			     String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];			
				 String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
				 Map hmKDSData=new TreeMap();	
				 hmKDSData = objReportService.funProcessKDSFlashReport(clientCode,fromDate1,toDate1,reportType,posCode,costCenterCode,waiterNo,strType);
				 if(hmKDSData.size()>0)
				 {
					 list=(List) hmKDSData.get("listData");
					 if(list.size()>0)
					 {
						if(reportType.equals("Group")) 
						{
							listArrColHeader.add("Group Name");
							listArrColHeader.add("Quantity");
							listArrColHeader.add("Average Process Time");
									
							for(int i=0;i<list.size();i++)
							{
								clsPOSBillDtl objBillDtlBean =  (clsPOSBillDtl) list.get(i);
								List arrList=new ArrayList();
								arrList.add(objBillDtlBean.getStrGroupName());
			                    arrList.add(objBillDtlBean.getDblQuantity());
			                    arrList.add(objBillDtlBean.getTmeOrderProcessing());
								listArr.add(arrList);
							}
							
							totalList.add("Total Orders                        :"+value+"          Delayed Orders                    :"+value+"          Delay Orders Per :"+value);
							totalList.add("Average Prcessing Time              :"+hmKDSData.get("avgProTime")+"          Average Target Time               :"+value);
							totalList.add("Weighted Avg. Actual Time           :"+value+"          Weighted Avg. Target Time         :"+value);
							totalList.add("Minimum Preocess Time               :"+value+"          Maximum Process Time              :"+value);	
							totalList.add("Minimum Delayed Time                :"+value+"          Maximum Delayed Time              :"+value);
							totalList.add("Delayed Order Target Time Average   :"+value+"          Delayed Order Target Average Per  :"+value);
							
						}
						else if(reportType.equals("SubGroup"))
						{
							listArrColHeader.add("Bill No");
							listArrColHeader.add("KOT No");
							listArrColHeader.add("Waiter Name");
							listArrColHeader.add("Item Name");
							listArrColHeader.add("Qty");
							listArrColHeader.add("KOT Time");
							listArrColHeader.add("Process Time");
							listArrColHeader.add("PickUp Time");
							listArrColHeader.add("KOT-Process Time");
							listArrColHeader.add("Process-PickUp Time");
									
							for(int i=0;i<list.size();i++)
							{
								clsPOSBillDtl objBillDtlBean =  (clsPOSBillDtl) list.get(i);
								List arrList=new ArrayList();
								
								if(objBillDtlBean.getStrBillNo()!=null)
								{
									arrList.add(objBillDtlBean.getStrBillNo());
				                    arrList.add(objBillDtlBean.getStrKOTNo());
				                    arrList.add(objBillDtlBean.getStrWShortName());
				                    arrList.add(objBillDtlBean.getStrItemName());
				                    arrList.add(objBillDtlBean.getDblQuantity());
				                    arrList.add(objBillDtlBean.getTmeBillTime());
				                    arrList.add(objBillDtlBean.getTmeOrderProcessing());
				                    arrList.add(objBillDtlBean.getTmeBillSettleTime());
				                    arrList.add(objBillDtlBean.getStrProcessTimeDiff());
				                    arrList.add(objBillDtlBean.getStrPickUpTimeDiff());
										
								}
								else
								{
									arrList.add(objBillDtlBean.getStrSubGroupName());
				                    arrList.add(objBillDtlBean.getStrSubGroupCode());
								}
								listArr.add(arrList);
							}
							
							totalList.add("Total Orders                        :"+hmKDSData.get("noOfItemsCount")+"          Delayed Orders                    :"+hmKDSData.get("finalDelayedOrder")+"          Delay Orders Per :"+hmKDSData.get("totOrderePer"));
							totalList.add("Average Prcessing Time              :"+hmKDSData.get("avgProTime")+"          Average Target Time               :"+hmKDSData.get("totOrdTarAvg"));
							totalList.add("Weighted Avg. Actual Time           :"+hmKDSData.get("weightedAvgActualTime")+"          Weighted Avg. Target Time         :"+hmKDSData.get("weightedAvgTargetTime"));
							totalList.add("Minimum Preocess Time               :"+hmKDSData.get("minProcessTime")+"          Maximum Process Time              :"+hmKDSData.get("maxProcessTime"));	
							totalList.add("Minimum Delayed Time                :"+hmKDSData.get("minDelayedTime")+"          Maximum Delayed Time              :"+hmKDSData.get("maxDelayedTime"));
							totalList.add("Delayed Order Target Time Average   :"+hmKDSData.get("delayOrderTargAvg")+"          Delayed Order Target Average Per  :"+hmKDSData.get("totDelayOrderTotAvgPer"));
						}
						else if(reportType.equals("Menu Head"))
						{
							listArrColHeader.add("Menu Head Name");
							listArrColHeader.add("Quantity");
							listArrColHeader.add("Average Process Time");
									
							for(int i=0;i<list.size();i++)
							{
								clsPOSBillDtl objBillDtlBean =  (clsPOSBillDtl) list.get(i);
								List arrList=new ArrayList();
								arrList.add(objBillDtlBean.getStrGroupName());
			                    arrList.add(objBillDtlBean.getDblQuantity());
			                    arrList.add(objBillDtlBean.getTmeOrderProcessing());
								listArr.add(arrList);
							}
							
							totalList.add("Total Orders                        :"+value+"          Delayed Orders                    :"+value+"          Delay Orders Per :"+value);
							totalList.add("Average Prcessing Time              :"+hmKDSData.get("avgProTime")+"          Average Target Time               :"+value);
							totalList.add("Weighted Avg. Actual Time           :"+value+"          Weighted Avg. Target Time         :"+value);
							totalList.add("Minimum Preocess Time               :"+value+"          Maximum Process Time              :"+value);	
							totalList.add("Minimum Delayed Time                :"+value+"          Maximum Delayed Time              :"+value);
							totalList.add("Delayed Order Target Time Average   :"+value+"          Delayed Order Target Average Per  :"+value);
						}
						else
						{
							listArrColHeader.add("Bill No");
							listArrColHeader.add("KOT No");
							listArrColHeader.add("Waiter Name");
							listArrColHeader.add("Item Name");
							listArrColHeader.add("Qty");
							listArrColHeader.add("KOT Time");
							listArrColHeader.add("Process Time");
							listArrColHeader.add("PickUp Time");
							listArrColHeader.add("KOT-Process Time");
							listArrColHeader.add("Process-PickUp Time");
							listArrColHeader.add("Min Time");
							listArrColHeader.add("Max Time");
									
							for(int i=0;i<list.size();i++)
							{
								clsPOSBillDtl objBillDtlBean =  (clsPOSBillDtl) list.get(i);
								List arrList=new ArrayList();
								
								if(objBillDtlBean.getStrBillNo()!=null)
								{
									arrList.add(objBillDtlBean.getStrBillNo());
				                    arrList.add(objBillDtlBean.getStrKOTNo());
				                    arrList.add(objBillDtlBean.getStrWShortName());
				                    arrList.add(objBillDtlBean.getStrItemName());
				                    arrList.add(objBillDtlBean.getDblQuantity());
				                    arrList.add(objBillDtlBean.getTmeBillTime());
				                    arrList.add(objBillDtlBean.getTmeOrderProcessing());
				                    arrList.add(objBillDtlBean.getTmeBillSettleTime());
				                    arrList.add(objBillDtlBean.getStrProcessTimeDiff());
				                    arrList.add(objBillDtlBean.getStrPickUpTimeDiff());
				                    arrList.add(objBillDtlBean.getStrItemProcessTime());
				                    arrList.add(objBillDtlBean.getStrItemTargetTime());
										
								}
								else
								{
									arrList.add(objBillDtlBean.getStrSubGroupName());
				                    arrList.add(objBillDtlBean.getStrSubGroupCode());
								}
								listArr.add(arrList);
			                    
							}
							
							totalList.add("Total Orders                        :"+hmKDSData.get("noOfItemsCount")+"          Delayed Orders                    :"+hmKDSData.get("finalDelayedOrder")+"          Delay Orders Per :"+hmKDSData.get("totOrderePer"));
							totalList.add("Average Prcessing Time              :"+hmKDSData.get("avgProTime")+"          Average Target Time               :"+hmKDSData.get("totOrdTarAvg"));
							totalList.add("Weighted Avg. Actual Time           :"+hmKDSData.get("weightedAvgActualTime")+"          Weighted Avg. Target Time         :"+hmKDSData.get("weightedAvgTargetTime"));
							totalList.add("Minimum Preocess Time               :"+hmKDSData.get("minProcessTime")+"          Maximum Process Time              :"+hmKDSData.get("maxProcessTime"));	
							totalList.add("Minimum Delayed Time                :"+hmKDSData.get("minDelayedTime")+"          Maximum Delayed Time              :"+hmKDSData.get("maxDelayedTime"));
							totalList.add("Delayed Order Target Time Average   :"+hmKDSData.get("delayOrderTargAvg")+"          Delayed Order Target Average Per  :"+hmKDSData.get("totDelayOrderTotAvgPer"));
							
						}
								   	
					   	
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
						resMap.put("totalList", totalList);
					}
				 }
				 
				 
				 
				 
				return resMap;
		  }
	

}
