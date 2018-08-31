package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSTableReservationBean;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel;
import com.sanguine.webpos.model.clsCustomerAreaMasterModel_ID;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.model.clsCustomerMasterModel_ID;
import com.sanguine.webpos.model.clsTableReservationModel;
import com.sanguine.webpos.model.clsTableReservationModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSTransactionService;
import com.sanguine.webpos.util.clsPOSUtilityController;



@Controller
public class clsPOSTableReservationController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSTransactionService objTransService;
	
	
	@RequestMapping(value = "/frmPOSTableReservation", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSTableReservationBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
		
		Map mapPOS=new HashMap();
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		List list=objMasterService.funFillPOSCombo(clientCode);
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			mapPOS.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
		}
		model.put("posList",mapPOS);
		return new ModelAndView("frmPOSTableReservation");
		
		 
	}
	
	@RequestMapping(value = "/saveTableReservation", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSTableReservationBean objBean,BindingResult result,HttpServletRequest req)
	{
		
		String posCode="";
		try
		{
			
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
	
			Map hmData=new HashMap();
			hmData.put("resCode", objBean.getStrReservationCode());
			hmData.put("CustName", objBean.getStrCustName());
			hmData.put("CustCode", objBean.getStrCustCode());
			hmData.put("intPax", objBean.getIntPax());
			hmData.put("strSmokingYN", objBean.getStrSmokingYN());
			hmData.put("ContactNo", objBean.getStrContactNo());
			hmData.put("City", objBean.getStrCity());
			hmData.put("BldgCode", objBean.getStrBldgCode());
			hmData.put("BldgName", objBean.getStrBldgName());
			String[] date= objBean.getDteDate().split(" ");
			hmData.put("resDate",date[0]);
			String strHH=objBean.getStrHH();
			String strMM=objBean.getStrMM();
			String strAMPM=objBean.getStrAMPM();
			String resTime=strHH + ":" + strMM + ":00";
			hmData.put("resTime", resTime);
			hmData.put("strAMPM", strAMPM);
			hmData.put("strInfo", objBean.getStrInfo());
			hmData.put("strTableNo", objBean.getStrTableNo());
			hmData.put("POSCode", objBean.getStrPOS());
			hmData.put("User", webStockUserCode);
			hmData.put("ClientCode", clientCode);
			String reservationCode=funSaveTableReservation(hmData);
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+reservationCode);
									
			return new ModelAndView("redirect:/frmPOSTableReservation.html");
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadReservationDefault", method = RequestMethod.GET)
	public @ResponseBody List funGetReservationDefault(HttpServletRequest req)
	{
		String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		String date=req.getParameter("date");
		Map hmDefaultReservationData=new HashMap();
		hmDefaultReservationData =funGetReservationDefault(date,loginPosCode);
		List listResDtl=(List)hmDefaultReservationData.get("ReservationDtl");
	    return listResDtl;
   }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadTableReservationDtl", method = RequestMethod.GET)
	public @ResponseBody List funGetTableReservationDtl(HttpServletRequest req)
	{
		 String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		 String fromDate=req.getParameter("fromDate");
		 String toDate=req.getParameter("toDate");
		 String fromTime=req.getParameter("fromTime");
		 String toTime=req.getParameter("toTime");
		 Map hmResData=new HashMap();
		 hmResData =funGetTableReservationDtl(objGlobal.funGetDate("yyyy/MM/dd", fromDate),objGlobal.funGetDate("yyyy/MM/dd", toDate),fromTime,toTime,loginPosCode);
		 List listReservatiobDtl=(List)hmResData.get("ReservationDtl");
	     return listReservatiobDtl;
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/funCancelTableReservation", method = RequestMethod.GET)
	public @ResponseBody String funCancelTableReservation(HttpServletRequest req)
	{
		 String res="";
		 String reservationNo=req.getParameter("reservationNo");
		 String tableNo=req.getParameter("tableNo");
		 funCancelTableReservation(reservationNo,tableNo);
	     return res;
   }
	
	@RequestMapping(value = "/loadPOSTableReservationData", method = RequestMethod.GET)
	public @ResponseBody clsPOSTableReservationBean funSetSearchFields(@RequestParam("resCode") String resCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSTableReservationBean objPOSReservationBean = null;
		String posName="";
		Map hmResDetails=new HashMap();
		hmResDetails=funGetPOSReservationData(resCode,clientCode);
		
		List listResDtl=(List) hmResDetails.get("POSTableReservation");
		if(null!=listResDtl)
		{
			objPOSReservationBean = new clsPOSTableReservationBean();
			objPOSReservationBean.setStrReservationCode((String) listResDtl.get(0));
			objPOSReservationBean.setStrCustCode((String) listResDtl.get(1));
			objPOSReservationBean.setStrCustName((String) listResDtl.get(2));
			objPOSReservationBean.setStrBldgCode((String) listResDtl.get(3));
			objPOSReservationBean.setStrBldgName((String) listResDtl.get(4));
			objPOSReservationBean.setStrCity((String) listResDtl.get(5));
			objPOSReservationBean.setStrContactNo((String) listResDtl.get(6));
			objPOSReservationBean.setStrTableNo((String) listResDtl.get(7));
			objPOSReservationBean.setDteDate((String) listResDtl.get(8));
			
			String resTime=(String) listResDtl.get(9);
			String[] time=resTime.split(":");
			String HH=time[0];
		
			String MM=time[1];
		
			objPOSReservationBean.setStrHH(HH);
			objPOSReservationBean.setStrMM(MM);
			objPOSReservationBean.setIntPax(Long.parseLong(listResDtl.get(10).toString()));
			objPOSReservationBean.setStrSmokingYN((String) listResDtl.get(11));
			objPOSReservationBean.setStrInfo((String) listResDtl.get(12));
			objPOSReservationBean.setStrTableName((String) listResDtl.get(13));
			objPOSReservationBean.setStrAMPM((String) listResDtl.get(14));
			objPOSReservationBean.setStrPOS((String) listResDtl.get(15));
			
			
		}
		
		if(null==objPOSReservationBean)
		{
			objPOSReservationBean = new clsPOSTableReservationBean();
			objPOSReservationBean.setStrReservationCode("Invalid Code");
		}
		
		return objPOSReservationBean;
	}
	
	
	

   public String funSaveTableReservation(Map hmData){
	String resCode = "";
	try
	{
	    
		resCode = hmData.get("resCode").toString();
		String contactNo = hmData.get("ContactNo").toString();
		String custCode = hmData.get("CustCode").toString();
		String custName = hmData.get("CustName").toString();
	    int intPax = Integer.valueOf(hmData.get("intPax").toString());
	    String strSmokingYN = hmData.get("strSmokingYN").toString();
	    String strCity = hmData.get("City").toString();
	    String resDate = hmData.get("resDate").toString();
	    String resTime = hmData.get("resTime").toString();
		String customerAreaCode = hmData.get("BldgCode").toString();
		String bldgName = hmData.get("BldgName").toString();
	    String strAMPM = hmData.get("strAMPM").toString();
	    String strInfo = hmData.get("strInfo").toString();
	    String strTableNo = hmData.get("strTableNo").toString();
	    String strPOSCode = hmData.get("POSCode").toString();
	    String user = hmData.get("User").toString();
	    String clientCode = hmData.get("ClientCode").toString();
	    String dateTime = objGlobal.funGetCurrentDateTime("yyyy-MM-dd");
	    if(customerAreaCode.trim().isEmpty())
	   	{
	        Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, strPOSCode, "gCustAreaCompulsory"); 
	        if(objSetupParameter.get("gCustAreaCompulsory").toString().equalsIgnoreCase("Y"))
	    	{
	    		List list=objUtilityController.funGetDocumentCode("POSCustAreaMaster");
		    	if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "00";
				    String code = list.get(0).toString();
				    StringBuilder sb = new StringBuilder(code);
				    String ss = sb.delete(0, 2).toString();
				    for (int i = 0; i < ss.length(); i++)
				    {
						if (ss.charAt(i) != '0')
						{
						    strCode = ss.substring(i, ss.length());
						    break;
						}
				    }
				    
				    int intCode = Integer.parseInt(strCode);
				    intCode++;
				    if (intCode < 10)
				    {
				    	customerAreaCode = "B000000" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	customerAreaCode = "B00000" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	customerAreaCode = "B0000" + intCode;
				    }
				    else if (intCode < 10000)
				    {
				    	customerAreaCode = "B000" + intCode;
				    }
				    else if (intCode < 100000)
				    {
				    	customerAreaCode = "B00" + intCode;
				    }
				    else if (intCode < 1000000)
				    {
				    	customerAreaCode = "B0" + intCode;
				    }
				   
				}
				else
				{
					customerAreaCode = "B0000001";
				}
	
	   	    	
	   	        clsCustomerAreaMasterModel obj = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
		   	    obj.setStrBuildingName(bldgName);
		   	    obj.setStrAddress("");
		   	    obj.setDblHomeDeliCharge(0);
		   	    obj.setStrZoneCode("");
		   	    obj.setDblDeliveryBoyPayOut(0);
		   	    obj.setStrClientCode(clientCode);
		   	    obj.setStrUserCreated(user);
		   	    obj.setStrUserEdited(user);
		   	    obj.setDteDateCreated(dateTime);
		   	    obj.setDteDateEdited(dateTime);
		   	    obj.setStrDataPostFlag("N");
		   	    objBaseService.funSave(obj);
	   	    }
	   
	   	}
    	else
    	{
    		clsCustomerAreaMasterModel obj = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
   	   	    obj.setStrBuildingName(bldgName);
   	   	    obj.setStrAddress("");
   	   	    obj.setDblHomeDeliCharge(0);
   	   	    obj.setStrZoneCode("");
   	   	    obj.setDblDeliveryBoyPayOut(0);
   	   	    obj.setStrClientCode(clientCode);
   	   	    obj.setStrUserCreated(user);
   	   	    obj.setStrUserEdited(user);
   	   	    obj.setDteDateCreated(dateTime);
   	   	    obj.setDteDateEdited(dateTime);
   	   	    obj.setStrDataPostFlag("N");
   	   	    objBaseService.funSave(obj);
         }
	    //SaveUpdate Customer 
	     long lastNo = 1;
	     boolean isExistCust=false;
	     String propertCode=clientCode.substring(4);
		 custCode=objTransService.funCheckCustomerExist(contactNo);
		 if (custCode.trim().isEmpty())
		 {
			 List list=objUtilityController.funGetDocumentCode("POSCustomerMaster");
			 if (!list.get(0).toString().equals("0"))
				{
	    	 	String strCode = "00";
	    	 	String code = list.get(0).toString();
                StringBuilder sb = new StringBuilder(code);
                strCode = sb.substring(1,sb.length());
                lastNo = Long.parseLong(strCode);
                lastNo++;
                custCode = propertCode+"C" + String.format("%07d", lastNo);				   
			}
			else	 
			 {
				custCode = propertCode + "C" + String.format("%07d", lastNo);
			 }
		 }
		 else
		 {	
			   isExistCust=true;
		 }
		 
		 funSaveCustomerModel(custCode,clientCode,custName,customerAreaCode,bldgName,strCity,contactNo,user,dateTime,isExistCust);
	      
	  //saveUpdate Table Reservation  
	    if (resCode.trim().isEmpty())
	    {
	    	resCode = objTransService.funGenerateReservationCode();
	    	
	    }
	    clsTableReservationModel objModel=new clsTableReservationModel(new clsTableReservationModel_ID(resCode,custCode,clientCode));
	    objModel.setStrAMPM(strAMPM);
  	    objModel.setStrPosCode(strPOSCode);
  	    objModel.setStrSmoking(strSmokingYN);
  	    objModel.setStrSpecialInfo(strInfo);
  	    objModel.setStrTableNo(strTableNo);
  	    objModel.setIntPax(intPax);
  	    objModel.setDteDateCreated(dateTime);
  	    objModel.setDteDateEdited(dateTime);
  	    objModel.setStrDataPostFlag("");
  	    objModel.setStrUserCreated(user);
  	    objModel.setStrUserEdited(user);
  	    objModel.setDteResDate(objGlobal.funGetDate("yyyy/MM/dd", resDate));
  	    objModel.setStrCustomerCode(custCode);
  	    objModel.setTmeResTime(resTime);
  	    objModel.setStrDataPostFlag("N");
  	    objTransService.funSaveReservation(objModel);	
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return resCode; 
    }
   
   
   
   public Map funGetReservationDefault(String date, String loginPosCode)
	{ 
	   StringBuilder sql=new StringBuilder();
	   Map hmData=new HashMap();
		try{
			
			sql.append("select b.longMobileNo,b.strCustomerName,a.strSmoking,ifnull(c.strTableName,''),a.intPax "
                   + ",a.dteResDate,TIME_FORMAT(a.tmeResTime, '%r'),a.strSpecialInfo,ifnull(c.strTableNo,''),a.strResCode "
                   + "from tblreservation a "
                   + "left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode  "
                   + "left outer join tbltablemaster c on a.strTableNo=c.strTableNo  "
                   + "where date(a.dteResDate) between '" + date + "' and '" + date + "' "
                   + "and a.strPosCode='"+loginPosCode+"'");
			 List list = objBaseService.funGetList(sql, "sql");
			 List listData=new ArrayList();
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						 List listRowData=new ArrayList();
						 listRowData.add(obj[0]);
						 listRowData.add(obj[1]);
						 listRowData.add(obj[2]);
						 listRowData.add(obj[3]);
						 listRowData.add(obj[4]);
						 listRowData.add(obj[5]);
						 listRowData.add(obj[6]);
						 listRowData.add(obj[7]);
						 listRowData.add(obj[8]);
						 listRowData.add(obj[9]);
						 listData.add(listRowData);
					}
					hmData.put("ReservationDtl", listData);
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return hmData;
			}
	 }
   
     
   
   public Map funGetTableReservationDtl(String fromDate,String toDate, String fromTime, String toTime,  String loginPosCode)
	{
	    StringBuilder sql=new StringBuilder();
		Map hmTableData=new HashMap();
		try{
		
			sql.append("select b.longMobileNo,b.strCustomerName,a.strSmoking,c.strTableName,a.intPax ,a.dteResDate,TIME_FORMAT(a.tmeResTime, '%r'),a.strSpecialInfo,c.strTableNo,a.strResCode "
                   + "from tblreservation a "
                   + "left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode  "
                   + "left outer join tbltablemaster c on a.strTableNo=c.strTableNo  "
                   + "where date(a.dteResDate) between '" + fromDate + "' and '" + toDate + "' "
                   + "and a.strPosCode='"+loginPosCode+"'"                    
                   + "and  TIME_FORMAT(a.tmeResTime,'%T') >= '" + fromTime + "'and TIME_FORMAT(a.tmeResTime,'%T') <= '" + toTime + "' ");
			
			List list = objBaseService.funGetList(sql, "sql");
			List listData=new ArrayList();
			if (list.size()>0)
			 {
					for(int i=0; i<list.size(); i++)
					{
						 Object[] obj=(Object[])list.get(i);
						 List listRowData=new ArrayList();
						 listRowData.add(obj[0]);
						 listRowData.add(obj[1]);
						 listRowData.add(obj[2]);
						 listRowData.add(obj[3]);
						 listRowData.add(obj[4]);
						 listRowData.add(obj[5]);
						 listRowData.add(obj[6]);
						 listRowData.add(obj[7]);
						 listRowData.add(obj[8]);
						 listRowData.add(obj[9]);
						 listData.add(listRowData);
					 }
			  }
			 
			  hmTableData.put("ReservationDtl", listData);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return hmTableData;
			}
	}
   
   
   
   public void funCancelTableReservation(String reservationNo, String tableNo)
	{
	   StringBuilder sql=new StringBuilder();
	   try
	        {
		      sql.append("delete from tblreservation where strResCode='" + reservationNo + "' ");
		      objBaseService.funExecuteUpdate(sql.toString(),"sql");
	         if(tableNo!=null)  
	         {
	        	sql.setLength(0); 
	        	sql.append("update tbltablemaster set strStatus='Normal' "
                       + " where strTableNo='" + tableNo + "' "
                       + " and strStatus='Reserve' ");
	            objBaseService.funExecuteUpdate(sql.toString(),"sql");
	         } 
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	  }
   
   

   public Map funGetPOSReservationData(String resCode,String clientCode)throws Exception
   {
   	StringBuilder sql=new StringBuilder();
   	Map hmTableData=new HashMap();
   	List listDataRow = new ArrayList();
   	sql.append("SELECT a.strResCode,b.strCustomerCode,b.strCustomerName, IFNULL(b.strBuldingCode,''), "
   			+ " IFNULL(b.strBuildingName,''),b.strCity,b.longMobileNo,a.strTableNo,a.dteResDate,"
   			+ " a.tmeResTime,a.intPax,a.strSmoking,a.strSpecialInfo,IFNULL(d.strTableName,''),a.strAMPM,a.strPosCode"
            + " from tblreservation a "
            + " left outer join tblcustomermaster b on  a.strCustomerCode=b.strCustomerCode "
            + " left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode "
            + " LEFT OUTER JOIN tbltablemaster d ON a.strTableNo=d.strTableNo"
            + " where a.strCustomerCode<>'' and a.strClientCode='" +clientCode + "' and a.strResCode='"+resCode+"'");
		
		 List list=objBaseService.funGetList(sql, "sql");	
		 if(null != list)	
		 {
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] objArr = (Object[]) list.get(cnt);
			    listDataRow.add(objArr[0].toString());
			    listDataRow.add(objArr[1].toString());
			    listDataRow.add(objArr[2].toString());
			    listDataRow.add(objArr[3].toString());
			    listDataRow.add(objArr[4].toString());
			    listDataRow.add(objArr[5].toString());
			    listDataRow.add(objArr[6].toString());
			    listDataRow.add(objArr[7].toString());
			    listDataRow.add(objArr[8].toString());
			    listDataRow.add(objArr[9].toString());
			    listDataRow.add(objArr[10].toString());
			    listDataRow.add(objArr[11].toString());
			    listDataRow.add(objArr[12].toString());
			    listDataRow.add(objArr[13].toString());
			    listDataRow.add(objArr[14].toString());
			    listDataRow.add(objArr[15].toString());
			    
			    hmTableData.put("POSTableReservation", listDataRow);
			}
		 }
		 return hmTableData;
    }
   
   
  
   public void funSaveCustomerModel(String custCode, String clientCode,String custName,String customerAreaCode,String bldgName,String strCity,String contactNo,String user,String dateTime,boolean isExistCust) throws Exception
   {
	  clsCustomerMasterModel objCustModel=null;
	  if(isExistCust)
	  {
		    objCustModel = (clsCustomerMasterModel) objMasterService.funSelectedCustomerMasterData(custCode, clientCode);
	  }
	  else
	  {
		    objCustModel = new clsCustomerMasterModel(new clsCustomerMasterModel_ID(custCode, clientCode));
		    objCustModel.setStrCustomerName(custName);
		    objCustModel.setStrBuldingCode(customerAreaCode);
		    objCustModel.setStrBuildingName(bldgName);
		    objCustModel.setStrStreetName("");
		    objCustModel.setStrLandmark("");
		    objCustModel.setStrArea("");
		    objCustModel.setStrCity(strCity);
		    objCustModel.setStrState("");
		    objCustModel.setIntPinCode("");
		    objCustModel.setLongMobileNo(contactNo);
		    objCustModel.setStrOfficeBuildingCode("");
		    objCustModel.setStrOfficeBuildingName("");
		    objCustModel.setStrOfficeStreetName("");
		    objCustModel.setStrOfficeLandmark("N");
		    objCustModel.setStrOfficeArea("");
		    objCustModel.setStrOfficeCity("");
		    objCustModel.setStrOfficePinCode("");
		    objCustModel.setStrOfficeState("");
		    objCustModel.setStrOfficeNo("");
		    objCustModel.setStrOfficeAddress("N");
		    objCustModel.setStrExternalCode("");
		    objCustModel.setStrCustomerType("");
		    objCustModel.setDteDOB("");    
		    objCustModel.setStrGender("");
		    objCustModel.setDteAnniversary("");
		    objCustModel.setStrEmailId("");
		    objCustModel.setStrUserCreated(user);
		    objCustModel.setStrUserEdited(user);
		    objCustModel.setDteDateCreated(dateTime);
		    objCustModel.setDteDateEdited(dateTime);
		    objCustModel.setStrCRMId("N");
		    objCustModel.setStrCustAddress("N");
		    objCustModel.setStrDataPostFlag("N");
	  }
	  objBaseService.funSave(objCustModel);
	   
   }
   
   
	 
}
