
package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;



@Controller
public class clsPOSDayWiseSalesSummaryFlashController {
	
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private clsPOSReportService objReportService;
	 
	 @Autowired
	 private clsPOSMasterService objMasterService;
	
	 Map posMap=new HashMap();
	 Map settlementMap=new HashMap();
	 Map groupMap=new HashMap();
	@RequestMapping(value = "/frmPOSDayWiseSalesSummaryFlash", method = RequestMethod.GET)
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
		List poslist = new ArrayList();
		poslist.add("ALL");
		List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add(obj[1].toString());
				posMap.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList",poslist);
		
		settlementMap.put("ALL","ALL");
		List listOfSettlement = objMasterService.funFillSettlementCombo(strClientCode);
		if(listOfSettlement!=null)
		{
			for(int i =0 ;i<listOfSettlement.size();i++)
			{
				clsSettlementMasterModel objModel = (clsSettlementMasterModel) listOfSettlement.get(i);
				settlementMap.put( objModel.getStrSettelmentDesc(),objModel.getStrSettelmentCode());
			}
		}
		model.put("settlementList",settlementMap);
		
		groupMap.put("ALL","ALL");
		List listOfGroup = objMasterService.funFillAllGroupList(strClientCode);
		if(listOfGroup!=null)
		{
			for(int i =0 ;i<listOfGroup.size();i++)
			{
				clsGroupMasterModel objModel = (clsGroupMasterModel) listOfGroup.get(i);
				groupMap.put(objModel.getStrGroupName(), objModel.getStrGroupCode());
			}
		}
		model.put("groupList",groupMap);
		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmPOSDayWiseSalesSummaryFlash_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayWiseSalesSummaryFlash","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processDayWiseSalesSummeryFlash", method = RequestMethod.POST)	
	private ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		    String clientCode=req.getSession().getAttribute("gClientCode").toString();
	       
		    String fromDate=objBean.getFromDate();
		 
			String toDate=objBean.getToDate();
		
			String operationType=objBean.getStrOperationType();
			String posName=objBean.getStrPOSName();
			String settlementName=objBean.getStrSettlementName();
			String viewBy=objBean.getStrViewBy();
			String groupName = objBean.getStrGroupName();
			
			String withDiscount=objGlobalFunctions.funIfNull(objBean.getStrWithDiscount(),"N","Y");
			Map resMap = new LinkedHashMap();
			
			resMap=funGetData(clientCode,withDiscount,fromDate,toDate,operationType,settlementName,posName,viewBy,groupName);
		
			List exportList=new ArrayList();	
		
			String dteFromDate=objBean.getFromDate();
			String dteToDate=objBean.getToDate();
			String FileName="DayWiseSalesSummeryFlash_"+dteFromDate+"_To_"+dteToDate;
		
			exportList.add(FileName);
		long rowCount=(int)resMap.get("Row Count");
		long colCount=(int)resMap.get("Col Count");					
		List List=(List)resMap.get("Col Header");
		List rowlist=new ArrayList();
		String[] headerList = new String[List.size()];
		for(int i = 0; i < List.size(); i++){
			headerList[i]=(String)List.get(i);
		}
		
		exportList.add(headerList);
		for(int i=0;i<resMap.size();i++)
		{
			if(i<rowCount)
			{	
			List ob=(List)resMap.get(""+i);
			
			rowlist.add(ob);
			
			
			}
		
		}
		for(int i=0;i<2;i++)
		{
			List dataList=new ArrayList();
			for(int j=0; j<colCount;j++)
			{
				dataList.add(" ");
			}
			rowlist.add(dataList);
		}
		
		//Total Header
		List totalHeaderList=new ArrayList();
		for(int i=0; i<headerList.length; i++)
		{
			if(i==0)
				totalHeaderList.add("TOTAL");
			
			else 
				totalHeaderList.add(headerList[i]);
		}
		
		rowlist.add(totalHeaderList);
		
		List totalList=(List)resMap.get("Total");
		rowlist.add(totalList);
		
		exportList.add(rowlist);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);	
	}
	
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadDaywiseSalesSummary1"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadDaywiseSalesSummary1(HttpServletRequest req)
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	        
	        String withDiscount=req.getParameter("withDiscount");
	       
		    String fromDate=req.getParameter("fromDate");
		 
			String toDate=req.getParameter("toDate");
		
			String operationType=req.getParameter("operationType");
			String settlementName=req.getParameter("settlementName");
			String posName=req.getParameter("posName");
			String groupName = req.getParameter("groupName");
			
			resMap=funGetData(clientCode,withDiscount,fromDate,toDate,operationType,settlementName,posName,"ITEM'S GROUP WISE",groupName);
	        return resMap;
	    }


		
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadDaywiseSalesSummary2"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadDaywiseSalesSummary2(HttpServletRequest req)
	    {
	        LinkedHashMap resMap = new LinkedHashMap();
	     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	       
	        String withDiscount=req.getParameter("withDiscount"); 
	        
		    String fromDate=req.getParameter("fromDate");
		 
			String toDate=req.getParameter("toDate");
		
			String operationType=req.getParameter("operationType");
			String settlementName=req.getParameter("settlementName");
			String posName=req.getParameter("posName");
			String groupName = req.getParameter("groupName");
			resMap=funGetData(clientCode, withDiscount,fromDate,toDate,operationType,settlementName,posName,"NONE",groupName);
	        return resMap;
	    }

	
	  
	  private LinkedHashMap funGetData(String clientCode, String withDiscount, String fromDate,String toDate, String operationType,String settlementName,String posName, String viewBy,String groupName)
	  {
		  
			  LinkedHashMap resMap = new LinkedHashMap();
			  List list = new ArrayList();
			  List colHeader = new ArrayList();
			  int colCount=0,rowCount=0;
		      colHeader.add(" ");
		      
		      String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
		      String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
		      String posCode= "ALL";
		      if(posMap.containsKey(posName))
		      {
				posCode=(String) posMap.get(posName);
		      }
		      String settlementCode= "ALL";
		      if(settlementMap.containsKey(settlementName))
		      {
		    	  settlementCode=(String) settlementMap.get(settlementName);
		      }
		      
		      String groupCode = "ALL";
		      if(groupMap.containsKey(groupName))
		      {
		    	  groupCode = (String) groupMap.get(groupName);
		      }

		      if(viewBy.equalsIgnoreCase("ITEM'S GROUP WISE"))
		      {
			
		    	  resMap = objReportService.funProcessItemGroupWiseDayWiseSalesSummary(withDiscount, fromDate1, toDate1, operationType, settlementCode, posCode, posName,groupCode,groupName);
				
			}
		
			else
			{
				resMap  = objReportService.funProcessDayWiseSalesSummary(withDiscount, fromDate1, toDate1, operationType, settlementCode, posCode, posName,groupCode,groupName);
			}
		      
		     
		      colHeader = (List) resMap.get("Col Header");
		      colCount = (int) resMap.get("Col Count");
		      rowCount = (int) resMap.get("Row Count");
		      
		      list =  (List) resMap.get("listData");
		      System.out.println("List"+list);

				

				List listTotal=new ArrayList();

				listTotal.add("Total");
				
				listTotal.add(posName);
			      for(int i=0; i<rowCount; i++)
			        {
			        	   resMap.put(""+i,list.get(i));
			              
			        }
			    
			
			    String prevColHeader=""; 
				for(int i=2;i<colCount;i++)
				{
					
					double total=0.00;
					for(int j=0; j<rowCount; j++)
					{
						List arr=(List) list.get(j);
						if(prevColHeader.equalsIgnoreCase((String) colHeader.get(i)))
						{
						total+=Double.parseDouble(arr.get(i).toString());
						}
						else
						{
						total+=Double.parseDouble(arr.get(i).toString());
						prevColHeader = (String) colHeader.get(i);
						}
					}
					listTotal.add(total);

						
				}
	  		
				
				
				resMap.put("Header", colHeader);
				resMap.put("ColCount", colCount);
				resMap.put("RowCount", rowCount);
				resMap.put("Total", listTotal);
			
		       
	
		  
		  return resMap;
	  }
}
