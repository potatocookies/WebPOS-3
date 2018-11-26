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
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSKOTAnalysisBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.bean.clsPOSWaiterAnalysisBean;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.util.clsExportToExcel;



@Controller
public class clsPOSAuditFlashController { 
	 @Autowired
	 private clsPOSReportService objReportService;
	 
	 @Autowired
	 private clsPOSMasterService objMasterService;
	 
	 @Autowired
	 private clsExportToExcel objExportToExcel;
	 
	 Map posMap=new HashMap();
	 Map userMap=new HashMap();
	 Map reasonMap = new HashMap<>();
	
	@RequestMapping(value = "/frmPOSAuditFlash", method = RequestMethod.GET)
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
		
		userMap.put("All", "All");
		List listOfUser = objMasterService.funFillUserCombo(strClientCode);
		if(listOfUser!=null)
		{
			for(int i =0 ;i<listOfUser.size();i++)
			{
				clsUserHdModel objModel= (clsUserHdModel) listOfUser.get(i);
				userMap.put(objModel.getStrUserCode(), objModel.getStrUserName());
			}
		}
		model.put("userList",userMap);
		
		List listOfReasons = objMasterService.funGetAllReasonMaster(strClientCode);
		reasonMap.put("All", "All");
		if(listOfReasons!=null)
		{
			for(int i =0 ;i<listOfReasons.size();i++)
			{
				clsReasonMasterModel objModel = (clsReasonMasterModel) listOfReasons.get(i);
				reasonMap.put(objModel.getStrReasonCode(),objModel.getStrReasonName());
			}
		}
		model.put("ReasonMasterList",reasonMap);
		
		List listType = new ArrayList();
		listType.add("Summary");
		listType.add("Detail");
		model.put("listType",listType);
		
		List listSorting = new ArrayList();
		listSorting.add("Bill");
		listSorting.add("Amount");
		model.put("listSorting", listSorting);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);

		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmPOSAuditFlash_1","command", new clsPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSAuditFlash","command", new clsPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processAuditFlash1", method = RequestMethod.POST)	
	public ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", exportList);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/processAuditFlash", method = RequestMethod.POST)	
	public boolean funExportReportForDayEndMail(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)throws Exception
	{
		List exportList=funGetReportData(req, objBean);
		objExportToExcel.funGenerateExcelFile(exportList, req, resp,"xls");
		return true;
	}
	
	private List funGetReportData(HttpServletRequest req, clsPOSReportBean objBean) throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
	    String fromDate=objBean.getFromDate();
	    String toDate=objBean.getToDate();
	    String strReportType=objBean.getStrReportType();
		String strUserName=objBean.getStrSGName();
		String userCode= "ALL";
		if (!strUserName.equalsIgnoreCase("ALL"))
		{
			userCode = (String) userMap.get(strUserName);
		}
		String posCode= "ALL";
		posCode=objBean.getStrPOSName();
		String posName = "ALL";
		if (!posCode.equalsIgnoreCase("ALL"))
		{
			posName = (String) posMap.get(posCode);
		}
		String strReasonName=objBean.getStrReasonMaster();
		String reasonCode= "ALL";
		if (!strReasonName.equalsIgnoreCase("ALL"))
		{
			reasonCode = (String) reasonMap.get(strReasonName);
		}
		String auditType=objBean.getStrPSPCode();
		String strSorting = objBean.getStrSort();
		String strType = objBean.getStrType();
		
		Map resMap = new LinkedHashMap();
		
		resMap=funGetData(clientCode,strUserName,fromDate,toDate,strReportType,strReasonName,posName,auditType, strSorting, strType,posCode,reasonCode,userCode);
	
		List exportList=new ArrayList();	
	
		String dteFromDate=objBean.getFromDate();
		String dteToDate=objBean.getToDate();
		String FileName="AuditFlash_"+dteFromDate+"_To_"+dteToDate;
	
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
		dataList.add(totalList);
		
		exportList.add(dataList);
		return exportList;	
	}
	
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value={"/loadAuditFlash"}, method=RequestMethod.GET)
	    @ResponseBody
	    public Map funLoadDaywiseSalesSummary1(HttpServletRequest req)
	    {
		  LinkedHashMap resMap = new LinkedHashMap();
		     
	        
	        String clientCode=req.getSession().getAttribute("gClientCode").toString();
	        
	        String strUserName=req.getParameter("strUserName");
	        String userCode= "ALL";
			if (!strUserName.equalsIgnoreCase("ALL"))
			{
				userCode = (String) userMap.get(strUserName);
			}
	       
		    String fromDate=req.getParameter("fromDate");
		 
			String toDate=req.getParameter("toDate");
		
			String strReportType=req.getParameter("strReportType");
			String posCode= "ALL";
			posCode=req.getParameter("posName");
			String posName = "ALL";
			if (!posCode.equalsIgnoreCase("ALL"))
			{
				posName = (String) posMap.get(posCode);
			}
			
			String strReasonName=req.getParameter("strReason");
			String reasonCode= "ALL";
			if (!strReasonName.equalsIgnoreCase("ALL"))
			{
				reasonCode = (String) reasonMap.get(strReasonName);
			}
			
			String auditType=req.getParameter("auditType");
			
			String strSorting = req.getParameter("strSorting");
			
			String strType = req.getParameter("strType");
			
			resMap=funGetData(clientCode,strUserName,fromDate,toDate,strReportType,strReasonName,posName,auditType, strSorting, strType,posCode,reasonCode,userCode);
	        return resMap;
	    }

	  
	  @SuppressWarnings({ "unchecked" })
	private LinkedHashMap funGetData(String clientCode, String strUserName, String fromDate,String toDate, String strReportType,String strReason, String posName, String auditType,String strSorting,String strType,String posCode,String reasonCode,String userCode)
	  {									
			  LinkedHashMap resMap = new LinkedHashMap();
			  List listArrColHeader = new ArrayList();
			  List totalList = new ArrayList();
			  List listArr = new ArrayList();
			  List list = new ArrayList();
			  double amtTotal=0, netTotal=0, paxTotal=0;
			  double sumBillAmt = 0.00, sumNewAmt = 0.00;
		       double sumQty = 0.00, discAmt = 0.0,sumTotalAmt=0.00;
		        List colHeader = new ArrayList();
		         
			    String fromDate1=fromDate.split("-")[2]+"-"+fromDate.split("-")[1]+"-"+fromDate.split("-")[0];
				
				String toDate1=toDate.split("-")[2]+"-"+toDate.split("-")[1]+"-"+toDate.split("-")[0];
				
				
				list = objReportService.funProcessAuditFlashReport(fromDate1, toDate1, posName, strUserName, strReportType, strReason, auditType, clientCode, strSorting, strType,posCode,reasonCode,userCode);
				if(list.size()>0)
				{
					switch(auditType)
					{
					case "Modified Bill":
					{
				   if(strReportType.equals("Summary"))
					{
					   	
					   	listArrColHeader.add("Bill No");
						listArrColHeader.add("Bill Date");
						listArrColHeader.add("Entry Time");
						listArrColHeader.add("Modify Time");
						listArrColHeader.add("Bill Amt");
						listArrColHeader.add("New Amt");
						listArrColHeader.add("Discount");
						listArrColHeader.add("User Created");
						listArrColHeader.add("User Edited");
						listArrColHeader.add("Reason");
						listArrColHeader.add("Remarks");
						
				for(int i=0;i<list.size();i++)
				{
					clsPOSBillItemDtlBean objBillItemDtlBean =  (clsPOSBillItemDtlBean) list.get(i);
				
					//clsAuditFlashBean objClsGroupWaiseSalesBean=new clsAuditFlashBean();
					List arrList=new ArrayList();
					arrList.add(objBillItemDtlBean.getStrBillNo());
                    arrList.add(objBillItemDtlBean.getDteBillDate());
                    arrList.add(objBillItemDtlBean.getStrEntryTime());
                    arrList.add(objBillItemDtlBean.getStrModifiyTime());
                    arrList.add(objBillItemDtlBean.getDblAmount());
                    arrList.add(objBillItemDtlBean.getDblAmountTemp());
                    arrList.add(objBillItemDtlBean.getDblDiscountAmt());
                    arrList.add(objBillItemDtlBean.getStrUserCreated());
                    arrList.add(objBillItemDtlBean.getStrUserEdited());
                    arrList.add(objBillItemDtlBean.getStrReasonName());
                    arrList.add(objBillItemDtlBean.getStrRemark());
					listArr.add(arrList);
					
					sumBillAmt = sumBillAmt + objBillItemDtlBean.getDblAmount();
                    sumNewAmt = sumNewAmt + objBillItemDtlBean.getDblAmountTemp();
                    discAmt += objBillItemDtlBean.getDblDiscountAmt();
                    
				}
				totalList.add("Total");
				totalList.add("");
				totalList.add("");
				totalList.add("");
				totalList.add(sumBillAmt);
				totalList.add(sumNewAmt);
				totalList.add(discAmt);
				totalList.add(" ");
				totalList.add("");
				totalList.add("");
				totalList.add("");
				resMap.put("ColHeader", listArrColHeader);
				resMap.put("listArr", listArr);
				resMap.put("totalList", totalList);
			   }
				   else
				   {
					   	listArrColHeader.add("Bill No");
						listArrColHeader.add("Bill Date");
						listArrColHeader.add("Entry Time");
						listArrColHeader.add("Modify Time");
						listArrColHeader.add("Item Name");
						listArrColHeader.add("Qty");
						listArrColHeader.add("Amount");
						listArrColHeader.add("User Created");
						listArrColHeader.add("User Edited");
						listArrColHeader.add("Remarks");
					   
						for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrBillNo());
			                arrList.add(objBean.getDteBillDate());
							arrList.add(objBean.getStrEntryTime());
							arrList.add(objBean.getStrModifiyTime());
							arrList.add(objBean.getStrItemName());
							arrList.add(objBean.getDblQuantity());
							arrList.add(objBean.getBillAmt());
							arrList.add(objBean.getStrUserCreated());
							arrList.add(objBean.getStrUserEdited());
							arrList.add(objBean.getStrRemark());
							
							listArr.add(arrList);
							sumQty = sumQty + objBean.getDblQuantity();
		                    sumBillAmt = sumBillAmt + objBean.getBillAmt();
		                    
						}
						totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add(sumQty); 
						totalList.add(sumBillAmt);
						totalList.add("");
						totalList.add("");
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
						resMap.put("totalList", totalList);
					   }
				   
			   }
			   break;
					case "Voided Bill":
			   {
				   if(strReportType.equals("Summary"))
					{   
				
					listArrColHeader.add("POS");
	                listArrColHeader.add("POS Name");
	                listArrColHeader.add("Bill No");
	                listArrColHeader.add("Bill Date");
	                listArrColHeader.add("Voided Date");
	                listArrColHeader.add("Entry Time");
	                listArrColHeader.add("Voided Time");
	                listArrColHeader.add("Amt");
	                listArrColHeader.add("User Edited");
	                listArrColHeader.add("Reason");
	                listArrColHeader.add("Remarks");
					
					
	                for(int i=0;i<list.size();i++)
					{
	                clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
					List arrList=new ArrayList();
					
					arrList.add(objBean.getStrPosCode());
					arrList.add(objBean.getStrPosName());
					arrList.add(objBean.getStrBillNo());
					arrList.add(objBean.getDteBillDate());
					arrList.add(objBean.getDteVoidedDate());
					arrList.add(objBean.getStrEntryTime());
					arrList.add(objBean.getStrVoidedTime());
					arrList.add(objBean.getDblAmountTemp());
					arrList.add(objBean.getStrUserEdited());
					arrList.add(objBean.getStrReasonName());
					arrList.add(objBean.getStrRemark());
					
					listArr.add(arrList);
					sumTotalAmt = sumTotalAmt + objBean.getDblAmountTemp();
					}
				totalList.add("Total");
                totalList.add("");
                totalList.add("");
                totalList.add("");
                totalList.add("");
                totalList.add("");
                totalList.add("");
                totalList.add(sumTotalAmt);
                totalList.add("");
                totalList.add("");
                totalList.add("");
				resMap.put("listArr", listArr);
				resMap.put("ColHeader", listArrColHeader);
				resMap.put("totalList", totalList);
				
			   }
				   else
				   {
					   	listArrColHeader.add("POS");
					 	listArrColHeader.add("POS Name");
					 	listArrColHeader.add("Bill No");
					 	listArrColHeader.add("Bill Date");
					 	listArrColHeader.add("Voided Date");
					 	listArrColHeader.add("Entry Time");
					 	listArrColHeader.add("Voided Time");
					 	listArrColHeader.add("Item Name");
					 	listArrColHeader.add("Qty");
					 	listArrColHeader.add("Amt");
					 	listArrColHeader.add("User Edited");
					 	listArrColHeader.add("Reason");
					 	listArrColHeader.add("Remarks");

					   
					   for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrPosCode());
		                    arrList.add(objBean.getStrPosName());
		                    arrList.add(objBean.getStrBillNo());
		                    arrList.add(objBean.getDteBillDate());
		                    arrList.add(objBean.getDteVoidedDate());
		                    arrList.add(objBean.getStrEntryTime());
		                    arrList.add(objBean.getStrVoidedTime());
		                    arrList.add(objBean.getStrItemName());
		                    arrList.add(objBean.getDblQuantity());
		                    arrList.add(objBean.getDblAmount());
		                    arrList.add(objBean.getStrUserEdited());
		                    arrList.add(objBean.getStrReasonName());
		                    arrList.add(objBean.getStrRemark());
							
		                    listArr.add(arrList);
							
							sumQty = sumQty + objBean.getDblQuantity();
	                        sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
	                       
						}
					   
	                    totalList.add("Total");
	                    totalList.add("");
	                    totalList.add("");
	                    totalList.add("");
	                    totalList.add("");
	                    totalList.add("");
	                    totalList.add(sumQty);
	                    totalList.add(sumTotalAmt);
	                    totalList.add("");
	                    totalList.add("");
	                    totalList.add("");
						resMap.put("totalList", totalList);
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
					   }
				   
			   }
			   break;
					case "Voided Advanced Order":
			   {
				   if(strReportType.equals("Summary"))
					{ 
					   	listArrColHeader.add("Bill No");
						listArrColHeader.add("Bill Date");
						listArrColHeader.add("Voided Date");
						listArrColHeader.add("Entry Time");
						listArrColHeader.add("Voided Time");
						listArrColHeader.add("Amt");
						listArrColHeader.add("User Edited");
						listArrColHeader.add("Reason");

					   
				for(int i=0;i<list.size();i++)
				{
					clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
				
					List arrList=new ArrayList();
					arrList.add(objBean.getStrBillNo());
					arrList.add(objBean.getDteBillDate());
					arrList.add(objBean.getDteVoidedDate());
					arrList.add(objBean.getStrEntryTime());
					arrList.add(objBean.getStrVoidedTime());
					arrList.add(objBean.getDblModifiedAmount());
					arrList.add(objBean.getStrUserEdited());
					arrList.add(objBean.getStrReasonName());
					listArr.add(arrList);
					sumTotalAmt = sumTotalAmt + objBean.getDblModifiedAmount();
				}
				totalList.add("Total");
                totalList.add("");
                totalList.add(sumTotalAmt);
				resMap.put("listArr", listArr);
				resMap.put("ColHeader", listArrColHeader);
				resMap.put("totalList", totalList);
			   }
				   else
				   {
					   	listArrColHeader.add("Bill No");
						listArrColHeader.add("Bill Date");
						listArrColHeader.add("Voided Date");
						listArrColHeader.add("Entry Time");
						listArrColHeader.add("Voided Time");
						listArrColHeader.add("Item Name");
						listArrColHeader.add("Qty");
						listArrColHeader.add("Amt");
						listArrColHeader.add("User Edited");
						listArrColHeader.add("Reason");
						listArrColHeader.add("Remarks");
					   
					   for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean =  (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrBillNo());
		                    arrList.add(objBean.getDteBillDate());
		                    arrList.add(objBean.getDteVoidedDate());
		                    arrList.add(objBean.getStrEntryTime());
		                    arrList.add(objBean.getStrVoidedTime());
		                    arrList.add(objBean.getStrItemName());
		                    arrList.add(objBean.getDblQuantity());
		                    arrList.add(objBean.getDblAmount());
		                    arrList.add(objBean.getStrUserEdited());
		                    arrList.add(objBean.getStrReasonName());
		                    arrList.add(objBean.getStrRemark());
		                    listArr.add(arrList);
							sumQty = sumQty + objBean.getDblQuantity();
		                    sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
		                    
						}
					   	totalList.add("Total");
						totalList.add(sumQty);
						totalList.add(sumTotalAmt);
						resMap.put("totalList", totalList);
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("listArr", listArr);
					   }   
				   
				}
			   break;
					case "Line Void":
					{
						listArrColHeader.add("POS");
						listArrColHeader.add("Line Voided Date");
						listArrColHeader.add("Line Voided Time");
						listArrColHeader.add("Item Name");

						listArrColHeader.add("Qty");
						listArrColHeader.add("Amt");
						listArrColHeader.add("KOT No");
						listArrColHeader.add("User Created");
						
						for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrPosName());
			                arrList.add(objBean.getDteVoidedDate());
			                arrList.add(objBean.getStrVoidedTime());
			                arrList.add(objBean.getStrItemName());
			                arrList.add(objBean.getDblQuantity());
			                arrList.add(objBean.getBillAmt());
			                arrList.add(objBean.getStrKOTNo());
			                arrList.add(objBean.getStrUserEdited());
			                listArr.add(arrList);
							sumQty = sumQty + objBean.getDblQuantity();
			                sumTotalAmt = sumTotalAmt + objBean.getBillAmt();
			                
						}
						totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
						totalList.add(sumQty);
		                totalList.add(sumTotalAmt);
		                totalList.add(" ");
						resMap.put("listArr", listArr);
						resMap.put("ColHeader", listArrColHeader);
						resMap.put("totalList", totalList);
					}
					break;
					case "Voided KOT":
					{
						double pax = 0.00;
						   if(strReportType.equals("Summary"))
							{ 
							   	listArrColHeader.add("POS");
								listArrColHeader.add("Table");
								listArrColHeader.add("Waiter");
								listArrColHeader.add("KOT No");
								listArrColHeader.add("Pax");
								listArrColHeader.add("Amount");
								listArrColHeader.add("Reason");
								listArrColHeader.add("User Created");
								listArrColHeader.add("Date Created");
								listArrColHeader.add("Remarks");
							   
						for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrPosName());
			                arrList.add(objBean.getStrTableName());
			                arrList.add(objBean.getStrWaiterName());
			                arrList.add(objBean.getStrKOTNo());
			                arrList.add(objBean.getIntPaxNo());
			                arrList.add(objBean.getDblAmount());
			                arrList.add(objBean.getStrReasonName());
			                arrList.add(objBean.getStrUserCreated());
			                arrList.add(objBean.getDteDateCreated());
			                arrList.add(objBean.getStrRemark());
			                listArr.add(arrList);
							pax = pax + objBean.getIntPaxNo();
		                    sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
		                  
						}
						totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
		                totalList.add(pax);
		                totalList.add(sumTotalAmt);
		                totalList.add( " ");
						resMap.put("totalList", totalList);
						resMap.put("listArr", listArr);
						resMap.put("ColHeader", listArrColHeader);
					   }
						   
					   else
					   {
						   	listArrColHeader.add("POS");
				            listArrColHeader.add("Table");
				            listArrColHeader.add("Waiter");
				            listArrColHeader.add("KOT No");
				            listArrColHeader.add("Item Name");
				            listArrColHeader.add("Pax");
				            listArrColHeader.add("Qty");
				            listArrColHeader.add("Amount");
				            listArrColHeader.add("Reason");
				            listArrColHeader.add("User Created");
				            listArrColHeader.add("Date Created");
				            listArrColHeader.add("Remarks");
						   for(int i=0;i<list.size();i++)
							{
								clsPOSBillItemDtlBean objBean =  (clsPOSBillItemDtlBean) list.get(i);
							
								List arrList=new ArrayList();
								arrList.add(objBean.getStrPosName());
				                arrList.add(objBean.getStrTableName());
				                arrList.add(objBean.getStrWaiterName());
				                arrList.add(objBean.getStrKOTNo());
				                arrList.add(objBean.getStrItemName());
				                arrList.add(objBean.getIntPaxNo());
				                arrList.add(objBean.getDblQuantity());
				                arrList.add(objBean.getDblAmount());
				                arrList.add(objBean.getStrReasonName());
				                arrList.add(objBean.getStrUserCreated());
				                arrList.add(objBean.getDteDateCreated());
				                arrList.add(objBean.getStrRemark());
				                listArr.add(arrList);
								pax = pax + objBean.getIntPaxNo();
			                    sumQty = sumQty + objBean.getDblQuantity();
			                    sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
			                    
							}
						   totalList.add("Total");
						   totalList.add("");
						   totalList.add("");
						   totalList.add("");
						   totalList.add("");
						   totalList.add(pax);
			               totalList.add(sumQty);
			               totalList.add(sumTotalAmt);
			               totalList.add("");
			               totalList.add("");
			               totalList.add("");
			               totalList.add("");
			               
			               resMap.put("listArr", listArr);
			               resMap.put("totalList", totalList);
			               resMap.put("ColHeader", listArrColHeader);
						   }   
						   
						}
					break;
					case "Time Audit":
					{
						listArrColHeader.add("Bill No");
						listArrColHeader.add("Bill Date");
						listArrColHeader.add("KOT Time");
						listArrColHeader.add("Bill Time");
						listArrColHeader.add("Settle Time");
						listArrColHeader.add("Difference");
						listArrColHeader.add("User Created");
						listArrColHeader.add("User Edited");
						listArrColHeader.add("Remarks");
						
						for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrBillNo());
							arrList.add(objBean.getDteBillDate());
							arrList.add(objBean.getStrKotTime());
							arrList.add(objBean.getStrEntryTime());
							arrList.add(objBean.getStrVoidedTime());
							arrList.add(objBean.getStrDifference());
							arrList.add(objBean.getStrUserCreated());
							arrList.add(objBean.getStrUserEdited());
							arrList.add(objBean.getStrRemark());
							listArr.add(arrList);
							
						}
						totalList.add(" "); 
						totalList.add(" ");
						resMap.put("totalList", totalList);
						resMap.put("listArr", listArr);
						resMap.put("ColHeader", listArrColHeader);
						
						
					}
					break;
					case "KOT Analysis":
					{
						if(strReportType.equals("Summary"))
						{ 
						int noOfKOTs = 0;
						listArrColHeader.add("KOT");
						listArrColHeader.add("Opearation");
						listArrColHeader.add("Date");
						listArrColHeader.add("Time");
						listArrColHeader.add("Bill No.");
						listArrColHeader.add("Table");
						listArrColHeader.add("Waiter");
						listArrColHeader.add("Reason");
						listArrColHeader.add("Remarks");
						
						for(int i=0;i<list.size();i++)
						{
							clsPOSKOTAnalysisBean objBean = (clsPOSKOTAnalysisBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrKOTNo());
							arrList.add(objBean.getStrOperationType());
							arrList.add(objBean.getDteKOTDate());
							arrList.add(objBean.getTmeKOTTime());
							arrList.add(objBean.getStrBillNo());
							arrList.add(objBean.getStrTableName());
							arrList.add(objBean.getStrWaiterName());
							arrList.add(objBean.getStrReasonName());
							arrList.add(objBean.getStrRemarks());
							listArr.add(arrList);
							noOfKOTs++;
							
						}
						totalList.add("No. Of KOTs");
			            totalList.add("");
			            totalList.add("");
			            totalList.add("");
			            totalList.add("");
			            totalList.add(noOfKOTs);
			            totalList.add("");
			            totalList.add("");
			            totalList.add("");
			            resMap.put("totalList",totalList);
						resMap.put("listArr", listArr);
						resMap.put("ColHeader", listArrColHeader);
						
						}
						else
						{
							double totalQuantity = 0.0;
							listArrColHeader.add("KOT");
							listArrColHeader.add("Opearation");
							listArrColHeader.add("Date");
							listArrColHeader.add("Time");
							listArrColHeader.add("Bill No.");
							listArrColHeader.add("Item");
							listArrColHeader.add("Qty");
							listArrColHeader.add("Table");
							listArrColHeader.add("Waiter");
							listArrColHeader.add("Reason");
							listArrColHeader.add("Remarks");
							
							for(int i=0;i<list.size();i++)
							{
								clsPOSKOTAnalysisBean objBean = (clsPOSKOTAnalysisBean) list.get(i);
							
								List arrList=new ArrayList();
								arrList.add(objBean.getStrKOTNo());
								arrList.add(objBean.getStrOperationType());
								arrList.add(objBean.getDteKOTDate());
								arrList.add(objBean.getTmeKOTTime());
								arrList.add(objBean.getStrBillNo());
								arrList.add(objBean.getStrItemName());
								arrList.add(objBean.getDblQty());
								arrList.add(objBean.getStrTableName());
								arrList.add(objBean.getStrWaiterName());
								arrList.add(objBean.getStrReasonName());
								arrList.add(objBean.getStrRemarks());
								listArr.add(arrList);
								totalQuantity += objBean.getDblQty();
								
							}
							totalList.add("Total Qty");
				            totalList.add("");
				            totalList.add("");
				            totalList.add("");
				            totalList.add("");
				            totalList.add("");
				            totalList.add(totalQuantity);
				            totalList.add("");
				            totalList.add("");
				            totalList.add("");
				            totalList.add("");
				            resMap.put("totalList",totalList);
				            resMap.put("listArr", listArr);
							resMap.put("ColHeader", listArrColHeader);
						}
					}
					break;
					case "Moved KOT":
					{
						sumQty = 0.00;
		                sumTotalAmt = 0.00;
		                double pax = 0;
						   if(strReportType.equals("Summary"))
							{ 
							  	listArrColHeader.add("POS");
								listArrColHeader.add("Table");
								listArrColHeader.add("Waiter");
								listArrColHeader.add("KOT No");
								listArrColHeader.add("Pax");
								listArrColHeader.add("Amount");
								listArrColHeader.add("Reason");
								listArrColHeader.add("User Created");
								listArrColHeader.add("Date Created"); 
						for(int i=0;i<list.size();i++)
						{
							clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrPosName());
	                		arrList.add(objBean.getStrTableName());
	                		arrList.add(objBean.getStrWaiterName());
	                		arrList.add(objBean.getStrKOTNo());
	                		arrList.add(objBean.getIntPaxNo());
	                		arrList.add(objBean.getDblAmount());
	                		arrList.add(objBean.getStrReasonName());
	                		arrList.add(objBean.getStrUserCreated());
	                		arrList.add(objBean.getDteDateCreated());
	                		listArr.add(arrList);
							pax = pax + objBean.getIntPaxNo();
	                		sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
	                		
						}
						totalList.add("Total");
						totalList.add("");
						totalList.add("");
						totalList.add("");
			            totalList.add(pax);
			            totalList.add(sumTotalAmt);
			            totalList.add("");
			            totalList.add("");
			            totalList.add("");
			            resMap.put("totalList",totalList);
						resMap.put("listArr", listArr);
						resMap.put("ColHeader", listArrColHeader);
					   }
						   else
						   {
							   	listArrColHeader.add("POS");
								listArrColHeader.add("Table");
								listArrColHeader.add("Waiter");
								listArrColHeader.add("KOT No");
								listArrColHeader.add("Item Name");
								listArrColHeader.add("Pax");
								listArrColHeader.add("Qty");
								listArrColHeader.add("Amount");
								listArrColHeader.add("Reason");
								listArrColHeader.add("User Created");
								listArrColHeader.add("Date Created");
								listArrColHeader.add("Remarks");
								
							   for(int i=0;i<list.size();i++)
								{
									clsPOSBillItemDtlBean objBean = (clsPOSBillItemDtlBean) list.get(i);
								
									List arrList=new ArrayList();
									arrList.add(objBean.getStrPosName());
					                arrList.add(objBean.getStrTableName());
					                arrList.add(objBean.getStrWaiterName());
					                arrList.add(objBean.getStrKOTNo());
					                arrList.add(objBean.getStrItemName());
					                arrList.add(objBean.getIntPaxNo());
					                arrList.add(objBean.getDblQuantity());
					                arrList.add(objBean.getDblAmount());
					                arrList.add(objBean.getStrReasonName());
					                arrList.add(objBean.getStrUserCreated());
					                arrList.add(objBean.getDteDateCreated());
					                arrList.add(objBean.getStrRemark());
					                listArr.add(arrList);
									pax = pax + objBean.getIntPaxNo();
				                    sumQty = sumQty + objBean.getDblQuantity();
				                    sumTotalAmt = sumTotalAmt + objBean.getDblAmount();
				                   
								}
							   totalList.add("Total");
							   totalList.add("");
							   totalList.add("");
							   totalList.add("");
							   totalList.add("");
				               totalList.add(pax);
				               totalList.add(sumQty);
				               totalList.add(sumTotalAmt);
				               totalList.add("");
				               totalList.add("");
				               totalList.add("");
				               totalList.add("");
				               resMap.put("totalList",totalList);
				               resMap.put("listArr", listArr);
				               resMap.put("ColHeader", listArrColHeader);
							   }   
						   
						}
					break;
					case "Waiter Audit":
					{
						listArrColHeader.add("Waiter Name");
						listArrColHeader.add("No Of KOT");
						listArrColHeader.add("No Of Void KOT");
						listArrColHeader.add("No Of Void KOT%");
						listArrColHeader.add("No Of Move KOT");
						listArrColHeader.add("No Of Move KOT%");
						
						double totNoOfKot = 0.0, totNoOfVoidKot = 0.0, totNoOfMoveKot = 0.0;
						int noOfKot = 0;
				        double noOfVoidKotPer = 0.0, noOfMoveKotPer = 0.0; 
						for(int i=0;i<list.size();i++)
						{
							 clsPOSWaiterAnalysisBean objBean = (clsPOSWaiterAnalysisBean) list.get(i);
						
							List arrList=new ArrayList();
							arrList.add(objBean.getStrWaiterName());
			                arrList.add(objBean.getNoOfKot());
			                arrList.add(objBean.getNoOfVoidKot());
			                arrList.add(objBean.getNoOfVoidKotPer());
			                arrList.add(objBean.getNoOfMoveKot());
			                arrList.add(objBean.getNoOfMoveKotPer());
			                listArr.add(arrList);
							
							noOfKot = objBean.getNoOfKot();
				            totNoOfKot = totNoOfKot + objBean.getNoOfKot();
				            noOfVoidKotPer = objBean.getNoOfVoidKot();
				            totNoOfVoidKot = totNoOfVoidKot + objBean.getNoOfVoidKot();
				            noOfMoveKotPer = objBean.getNoOfMoveKot();
				            totNoOfMoveKot = totNoOfMoveKot + objBean.getNoOfMoveKot();
				            if (noOfKot > 0)
				            {
				              noOfVoidKotPer = ((noOfVoidKotPer / noOfKot) * 100);
				              noOfMoveKotPer = ((noOfMoveKotPer / noOfKot) * 100);
				            }
				                
						}	
							totalList.add( "Total");
				            totalList.add(totNoOfKot);
				            totalList.add(totNoOfVoidKot);
				            totalList.add("");
				            totalList.add(totNoOfMoveKot);
				            totalList.add("");
				            resMap.put("totalList",totalList);
				            resMap.put("listArr",listArr);
				            resMap.put("ColHeader", listArrColHeader);
						
					}
					}//end of switch

			}// end of if(jArr.size()>0)
				
				
		        return resMap;
	  }
	  
	  
}
