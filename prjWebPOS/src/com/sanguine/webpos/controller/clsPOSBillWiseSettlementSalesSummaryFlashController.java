
package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
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
import com.sanguine.webpos.util.clsExportToExcel;


@Controller
public class clsPOSBillWiseSettlementSalesSummaryFlashController {
	
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private ServletContext servletContext;
	
	 @Autowired
	 private clsPOSMasterService objMasterService;
	 
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private clsExportToExcel objExportToExcel;
	 
	Map posMap=new HashMap();
	Map settlementMap=new HashMap();
	Map groupMap=new HashMap();
	@RequestMapping(value = "/frmPOSBillWiseSettlementSalesSummaryFlash", method = RequestMethod.GET)
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
		List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
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
				settlementMap.put(objModel.getStrSettelmentCode(),objModel.getStrSettelmentDesc());
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
				groupMap.put(objModel.getStrGroupCode(),objModel.getStrGroupName());
			}
		}
		model.put("groupList",groupMap);
		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmPOSBillWiseSettlementSalesSummaryFlash_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSBillWiseSettlementSalesSummaryFlash","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processPOSBillWiseSettlementSalesSummeryFlash1", method = RequestMethod.POST)	
	public ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processPOSBillWiseSettlementSalesSummeryFlash", method = RequestMethod.POST)	
	public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req) throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
		return true;
	}
	
	private List funGetReportData(HttpServletRequest req, clsPOSReportBean objBean) throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String fromDate=objBean.getFromDate();
		String toDate=objBean.getToDate();
		String operationType=objBean.getStrOperationType();
		String settlementName=objBean.getStrSettlementName();
		String posName=objBean.getStrPOSName();
		String viewBy=objBean.getStrViewBy();
		String groupName = objBean.getStrGroupName();
		Map resMap = new LinkedHashMap();
			
		resMap=funGetData(clientCode,fromDate,toDate,operationType,settlementName,posName,viewBy,groupName,userCode);
		
		List exportList=new ArrayList();	
		
		String dteFromDate=objBean.getFromDate();
		String dteToDate=objBean.getToDate();
		String FileName="BillWiseSettlementSalesSummeryFlash_"+dteFromDate+"_To_"+dteToDate;
		
		exportList.add(FileName);
		int rowCount=(int)resMap.get("RowCount");
		int colCount=(int)resMap.get("ColCount");					
		List List=(List)resMap.get("Header");
		List rowlist=new ArrayList();
		String[] headerList = new String[List.size()];
		for(int i = 0; i < List.size(); i++){
			headerList[i]=(String)List.get(i);
		}
		
		exportList.add(headerList);
		for(int i=0;i<resMap.size();i++)
		{
			List dataList=new ArrayList();
		
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
			else if(i==1)
				totalHeaderList.add(" ");
			else 
				totalHeaderList.add(headerList[i]);
		}
		
		
		
		rowlist.add(totalHeaderList);
		
		List totalList=(List)resMap.get("Total");
		rowlist.add(totalList);
		
		exportList.add(rowlist);
		return exportList;	
	}
	
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadBillwiseSettlementSalesSummary"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadBillwiseSettlementSalesSummary1(HttpServletRequest req) throws Exception
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	        String userCode=req.getSession().getAttribute("gUserCode").toString();
	       
		    String fromDate=req.getParameter("fromDate");
		    
		    String viewBy=req.getParameter("viewBy");
		    
			String toDate=req.getParameter("toDate");
		
			String operationType=req.getParameter("operationType");
			String settlementName=req.getParameter("settlementName");
			String posName=req.getParameter("posName");
			String groupName = req.getParameter("groupName");
			resMap=funGetData(clientCode,fromDate,toDate,operationType,settlementName,posName,viewBy,groupName,userCode);
	        return resMap;
			
			
		
	    }


	  private LinkedHashMap funGetData(String clientCode, String fromDate,String toDate, String operationType,String settlementName,String posName, String viewBy,String groupName,String userCode) throws Exception
	  {
		  
			  LinkedHashMap resMap = new LinkedHashMap();
		     

			     
		        List colHeader = new ArrayList();
		       
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
			    if(viewBy.equalsIgnoreCase("BILL REGISTER"))
				{
			    	resMap = objReportService.funGetBillWiseSettlementBillRegisterSalesSummary(fromDate1,toDate1,viewBy,operationType,settlementCode,posCode,posName,groupName,userCode,clientCode);	
				}
			    else
			    {	
			    	resMap = objReportService.funGetBillWiseSettlementSalesSummary(fromDate1,toDate1,viewBy,operationType,settlementCode,posCode,posName,groupName);
			    }
			    
			    List listTot = new ArrayList();
				List jColHeaderArr = (List) resMap.get("Col Header");
				colHeader=jColHeaderArr;
				int colCount=(int)resMap.get("Col Count");
				int rowCount=(int)resMap.get("Row Count");
				List list =  (List) resMap.get("listData");
				List listTotal=new ArrayList();

				listTotal.add("Total");
				listTotal.add(" ");
				listTotal.add(posName);
			      for(int i=0; i<rowCount; i++)
			        {
			        	   resMap.put(""+i,(List)list.get(i));
			        }
			     // long columns=colCount-1;
			      
			String prevColHeader=""; 
			if(viewBy.equalsIgnoreCase("BILL REGISTER"))
			{
				listTot =  (List) resMap.get("listTotal");
			}
			else if(viewBy.equalsIgnoreCase("ITEM'S GROUP WISE"))
			{    
				for(int i=3;i<colCount-1;i++)
				{
					double total=0.00;
					for(int j=0; j<rowCount; j++)
					{
						List arr=(List)list.get(j);
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
//					if(total==0)
//					{
//						int len=listTotal.indexOf(total);
//						listTotal.remove(len);
//						colHeader.remove(i);
//						
//						colCount--;
//						for(int j=0; j<rowCount; j++)
//						{
//							List arr=(List)list.get(j);
//							arr.remove(i);
//							  resMap.put(""+j,arr);
//						}
//						i--;
//					}
				}
	  		}
			else
			{    
				for(int i=3;i<colCount-1;i++)
				{
					double total=0.00;
					for(int j=0; j<rowCount; j++)
					{
						List arr=(List)list.get(j);
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
	  		}
				resMap.put("Header", colHeader);
				resMap.put("ColCount", colCount);
				resMap.put("RowCount", rowCount);
				if(!viewBy.equalsIgnoreCase("BILL REGISTER"))
				{
					resMap.put("Total", listTotal);
				}
				else
				{
					resMap.put("Total",listTot);
				}
				
			
		  return resMap;

	  }
	  
}
