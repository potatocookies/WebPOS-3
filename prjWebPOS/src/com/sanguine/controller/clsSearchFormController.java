package com.sanguine.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.bean.clsFormSearchElements;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.webpos.controller.clsPOSGlobalFunctionsController;
import com.sanguine.webpos.model.clsGroupMasterModel;
import com.sanguine.webpos.model.clsSubGroupMasterHdModel;


@Controller
public class clsSearchFormController
{
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private intfBaseService objBaseService; 
	
	final static Logger logger=Logger.getLogger(clsSearchFormController.class);
	//Variables For WebMMS
	String showLocWiseProdMaster="";
	String strMisCode="";
	String BatchProdCode="";
	String showPrptyWiseProdDoc="N";
	String ShowTransAsc_Desc="";
	String WorkFlowbasedAuth="";
	String PICode="";
	String POCode="";
	String GrnCode="";
	String NonMoveSlowMovingpropCode="";
	String strModule="1";
	private String txtArrivalDate;
	
	// Variable For Excise
	private String txtFromDate = "";
	private String txtToDate = "";
	String licenceCode="";
	String strMenuCode="";
	String strCustomerCode="";
			
	
	//Variable For CRM
	private String strSubConCode="";
	
	@RequestMapping(value = "/searchform", method = RequestMethod.GET)
	public ModelAndView funOpenSearchForm(Map<String, Object> model,@ModelAttribute("formname") String value,BindingResult result,
			@RequestParam(value="formname") String formName,@RequestParam(value="searchText") String search_with, HttpServletRequest req)
	{
		req.getSession().setAttribute("formName", formName);
		req.getSession().setAttribute("searchText", search_with);
		
		showLocWiseProdMaster="";//req.getSession().getAttribute("showLocWiseProdMaster").toString();
		strMisCode="";
		BatchProdCode="";
		showPrptyWiseProdDoc="N";
		ShowTransAsc_Desc="";
		
		
		
		
		
		//Getting Varible From Request For WebPOS
		if(req.getParameter("strMenuCode")!=null)
		{
			strMenuCode=req.getParameter("strMenuCode");
		}
		
		if(req.getParameter("POCode")!=null)
		{
			POCode=req.getParameter("POCode").toString();
		}
		
		//Getting Varible From Request For WebPOS
		if(req.getParameter("strCustomerCode")!=null)
		{
			strCustomerCode=req.getParameter("strCustomerCode");
		}
		
		strModule = "7";
			Map<String,Object> hmSearchData = funGetWebPOSSearchDetail( formName, search_with, req);
			model.put("searchFormTitle", (String)hmSearchData.get("searchFormTitle"));
		return new ModelAndView("frmSearch");
	}
	
	
	@RequestMapping(value = "/loadSearchColumnNames", method = RequestMethod.GET)
	public @ResponseBody List<String> funColumnNames(HttpServletRequest req)
	{
		String formName= req.getSession().getAttribute("formName").toString();
		String search_with= req.getSession().getAttribute("searchText").toString();
		
		Map<String,Object> map= null;
		//map = funGetWebPOSSearchDetail( formName, search_with, req);
			strModule="7";
		
		LinkedList<String> columnName= new LinkedList<String>();
			
			Map<String,Object> hmSearchData = funGetWebPOSSearchDetail( formName, search_with, req);
			String listColumnNames[] = ((String) hmSearchData.get("listColumnNames")).split("\\,");
			for(int i=0;i<listColumnNames.length;i++){
				columnName.add(listColumnNames[i]);
			}
			
		
		return columnName;
	}
	
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	@RequestMapping(value = "/searchData", method = RequestMethod.GET)
	public @ResponseBody LinkedHashMap funSearchFormData(HttpServletRequest req)
	{
		String formName= req.getSession().getAttribute("formName").toString();
		req.getSession().removeAttribute("formName");
		String search_with= req.getSession().getAttribute("searchText").toString();
		req.getSession().removeAttribute("searchText");
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		
		
		LinkedHashMap resMap = new LinkedHashMap();
		Map<String, Object> model = funGetSearchData(formName, search_with,req);
		
		List list = (List) model.get("listRecords");
		LinkedList data = new LinkedList();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				LinkedList ls = new LinkedList();
				clsFormSearchElements objModel = (clsFormSearchElements) list
						.get(i);

				ls.add(objModel.getField1() !=null ? objModel.getField1() :""  );
				ls.add(objModel.getField2() !=null ? objModel.getField2() :""  );
				ls.add(objModel.getField3() !=null ? objModel.getField3() :""  );
				ls.add(objModel.getField4() !=null ? objModel.getField4() :""  );
				ls.add(objModel.getField5() !=null ? objModel.getField5() :""  );
				ls.add(objModel.getField6() !=null ? objModel.getField6() :"" );
				ls.add(objModel.getField7() !=null ? objModel.getField7() :""  );
				ls.add(objModel.getField8() !=null ? objModel.getField8() :"" );
				ls.add(objModel.getField9() !=null ? objModel.getField9() :"" );
				ls.add(objModel.getField10() !=null ? objModel.getField10() :"" );
				ls.add(objModel.getField11() !=null ? objModel.getField11() :"" );
				ls.add(objModel.getField12() !=null ? objModel.getField12() :"" );
				
				ls.add(objModel.getField13() !=null ? objModel.getField13() :"" );
				ls.add(objModel.getField14() !=null ? objModel.getField14() :"" );
				ls.add(objModel.getField15() !=null ? objModel.getField15() :"" );
				ls.add(objModel.getField16() !=null ? objModel.getField16() :"" );
				ls.add(objModel.getField17() !=null ? objModel.getField17() :"" );
				ls.add(objModel.getField18() !=null ? objModel.getField18() :"" );
				ls.add(objModel.getField19() !=null ? objModel.getField19() :"" );

				ls.removeAll(Collections.singleton(null));
				data.add(ls);
			}
		}
		resMap.put("data", data);
		return resMap;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> funGetSearchData(String formName,String search_with,HttpServletRequest req)
	{
		boolean flgQuerySelection=false;
		String columnNames = "";
		String tableName = "";
		String criteria = "";
	    String searchFormName="";
		List <String> listColumnNames;
		
		String multiDocCodeSelection="No";
		
		Map<String,Object> map= null;
		strModule="7";
		
		Map<String, Object> model = new HashMap<String, Object>();
		
			Map<String,Object> hmSearchData = funGetWebPOSSearchDetail( formName, search_with, req);
			
			String tempColmn=(String) hmSearchData.get("listColumnNames");
			searchFormName=(String) hmSearchData.get("searchFormTitle");
			List<String> list=(List) hmSearchData.get("listSearchData");
						
			listColumnNames=new ArrayList(Arrays.asList(tempColmn.split(",")));
			model.put("listColumns",listColumnNames);
			model.put("listRecords", funSetFormSearchElements(list));
			model.put("searchFormName",searchFormName);
			model.put("multipleSelection",multiDocCodeSelection);
		
		
		
		return model;
	}	


	
	

	

	
	/**
	 * WebPOS Search Start
	 * @param formName
	 * @param search_with
	 * @param req
	 * @return
	 */
	private Map<String,Object> funGetWebPOSSearchDetail(String formName,String searchCode,HttpServletRequest req){
		Map<String,Object> mainMap=new HashMap<>();
		
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String criteria = "";
		String listColumnNames="";
		String idColumnName="";
		String searchFormTitle="";
		JSONArray jArrSearchList=null;
		List<Object[]> listSearchData=new ArrayList<Object[]>();
		
		switch(formName)
		{
			case "POSGroupMaster":
			{
				listColumnNames="Group Code,Group Name,Operational";
				searchFormTitle="Group Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSReasonMaster":
			{
				listColumnNames="Reason Code,Reason Name";
				searchFormTitle="Reason Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSCustomerTypeMaster":
			{
				listColumnNames="Customer Type Code,CuStomer Type";
				searchFormTitle="Customer Type Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSZoneMaster":
			{
				listColumnNames="Zone Code,Zone Name";
				searchFormTitle="Zone Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSshiMaster":
			{
				listColumnNames="Shift Code,Pos Code,ShiftDate,Shift Start Time,Shift End Time,Bill Date Type";
				searchFormTitle="Shift Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			
			case "POSAreaMaster":
			{
				listColumnNames="Area Code,Area Name,POS Name";
				searchFormTitle="Area Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSTableMaster":
			{
				listColumnNames="Table No,Table Name,Area Name,Waiter Name,POS Name,Status";
				searchFormTitle="Table Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSWaiterMaster":
			{
				listColumnNames="Waiter No,Waiter ShortName,Operational";
				searchFormTitle="Waiter Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "WebBooksAcountMaster":
			{
				listColumnNames="Account Code,Account Name";
				searchFormTitle="Account Details";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "ExciseLicenseMaster":
			{
				listColumnNames="License Code,License Name";
				searchFormTitle="License Details";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSMenuHeadMaster":
			{
				listColumnNames="Menu Code,Menu Name,Operational";
				searchFormTitle="Menu Head";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSSubMenuHeadMaster":
			{
				listColumnNames="SubMenu Code,SubMenu Name,Operational";
				searchFormTitle="SubMenu Head";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSModifierGroupMaster":
			{
				listColumnNames="Modifier GroupCode,Modifier GroupName,Modifier GroupShortName,Operational";
				searchFormTitle="Modifier GroupMaster";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSItemModifierMaster":
			{
				listColumnNames="Modifier Code,Modifier Name,Description ";
				searchFormTitle="Item Modifier";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSMenuItemMaster":
			{
				listColumnNames="Item Code,Item Name,Item Type,Revenue,Tax Id,External Code,SubGroup Name";
				searchFormTitle="Menu Item Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSMaster":
			{
				listColumnNames="POS Code,POS Name";
				searchFormTitle="POS Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSTaxMaster":
			{
				listColumnNames="Tax Code,Tax Desc";
				searchFormTitle="Tax Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			

			case "POSCustomerMaster":
					{
						listColumnNames="Customer Code,CuStomer Name,Mobile No,Address ";
						searchFormTitle="Customer Master";
						JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
						jArrSearchList=(JSONArray) jObjSearchData.get(formName);
						break;
					}

		   case "POSCustomerAreaMaster":
					{
						listColumnNames="Customer Area Code,CuStomer Area Name, Address";
						searchFormTitle="Customer Area Master";
						JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
						jArrSearchList=(JSONArray) jObjSearchData.get(formName);
						break;
					}
					
				case "POSMenuItemPricingMaster":
			{
				listColumnNames="Id,Item Code,Item Name,POS,Area,Menu,Price Mond,Price Tue,Price Wed,Price Thu,Price Fri,Price Sat,Price Sun "
								+",Popular,From Date,To Date,Cost Center,Sub Menu Head,Hourly Pricing";
				searchFormTitle="Menu Item Pricing Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSSettlementMaster":
			{
				listColumnNames="Settlement Code,Settlement Name ";
				searchFormTitle="Settlement Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSAdvOrderMaster":
			{
				listColumnNames="Adv Order Type Code,Adv Order Type Name ";
				searchFormTitle="Advance Order Type Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
				
			case "POSDeliveryBoyMaster":
			{
				listColumnNames="Delivery Boy Code, Delivery Boy Name ";
				searchFormTitle="Delivery Boy Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSOrderMaster":
			{
				listColumnNames="Order Code, Order Desc ";
				searchFormTitle="Order Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "MenuItemForPrice":
			{
				listColumnNames="Item Code, Item Name, Item Type, RevenueHead, Tax Id, External Code, SubGroup Name ";
				searchFormTitle="Item Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "MenuItemForRecipeChild":
			{
				listColumnNames="Item Code, Item Name, Item Type, RevenueHead, Tax Id, External Code, SubGroup Name ";
				searchFormTitle="Item Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSRecipeMaster":
			{
				listColumnNames="Recipe Code, Item Code, Item Name ";
				searchFormTitle="Recipe Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
		
			case "POSPromotionMaster":
			{
				listColumnNames="Promotion Code, Promotion Name, Promotion On ";
				searchFormTitle="Promotion Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			

			/*case "webstockusermaster":
			 {	
				listColumnNames="User Code,User Name,Super User";
				searchFormTitle="User Master";
				JSONObject jObjSearchData = funGetWebstockUserSearchDetails(clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			 }*/

			case "POSCounterMaster":
			{
				listColumnNames="Counter Type Code,Counter Name,Operational,User,POS";
				searchFormTitle="Counter Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSDebitCardMaster":
			{
				listColumnNames="Card Type Code,Card Name";
				searchFormTitle="Debit Card Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
		

			case "POSItemList":
			{	
				listColumnNames="Item Code,Item Name,Item Type,External Code";
				searchFormTitle="POS Item List";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;	
			}	
			case "PhysicalStock":
			{	
				String posCode=req.getSession().getAttribute("loginPOS").toString();
				listColumnNames="PSP_Code,Item_Code,Item_Name,Created_Date";
				searchFormTitle="Physical Stock Details";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode+"#"+posCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;	
			}	
			case "StockIn":
			{	
				listColumnNames="StockIn_Code,Reason Code,Reason_Name";
				searchFormTitle="StockIn Details";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;	
			}	
			case "StockOut":
			{	
				listColumnNames="StockOut_Code,Reason Code,Reason_Name";
				searchFormTitle="StockOut Details";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;	
			}
		
			case "POSUserMaster":
			{
				listColumnNames="User Code,Swipe Card,Super Type";
				searchFormTitle="User Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSTableReservation":
			{
				listColumnNames="Reservation Code,Customer Name,Building Code,Building Name,City";
				searchFormTitle=" Table Reservation";
				 String strPosCode=req.getSession().getAttribute("loginPOS").toString();
				
			JSONObject jObjSearchData = funGetPOSSearchDetails(formName,strPosCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
		
			case "POSTableReserveMaster":
			{
				listColumnNames="Table No,Table Name,Area Name,Waiter Name,POS Name,Status";
				searchFormTitle="Table Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSFactoryMaster":
			{
				listColumnNames="Factory Code,Factory Name,User Created,User Edited,Date Created";
				searchFormTitle="Factory Master";
			JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}

			case "POSCostCenterMaster":
			{
					listColumnNames="Cost Center Code,Cost Center Name";
					searchFormTitle="Cost Center Master";
					JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
					jArrSearchList=(JSONArray) jObjSearchData.get(formName);
					break;
			}
			
			case "POSTDHOnItem":
			{
				listColumnNames="Item Code,Item Name";
				searchFormTitle="Search Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,strMenuCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			
			
			case "POSLoadTDHData":
			{
				listColumnNames="TDH Code,Description,Menu Code,Item Code,Quantity";
				searchFormTitle="TDH Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSSubGroupMaster":
			{
				listColumnNames="SubGroup Code,SubGroup Name,Group Code,Incentives";
				searchFormTitle="SubGroup Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "POSDiscountMaster":
			{
				listColumnNames="Discount Code,Description,POS,Discount On";
				searchFormTitle="Discount Master";
				clientCode=req.getSession().getAttribute("gClientCode").toString();
				String posDate = req.getSession().getAttribute("gPOSDate").toString();
				String data=clientCode+"#"+posDate;
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,data);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "POSUnSettleBill":
			{   
				String posCode=req.getSession().getAttribute("loginPOS").toString();
				String posDate = req.getSession().getAttribute("gPOSDate").toString();
				listColumnNames="Bill No,Table Name,Grand Total,Settle Mode,User Created,Remarks";
				searchFormTitle="Unsettle Bill";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode+"#"+posCode+"#"+posDate);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			case "BillForChangeSettlement":
			{   
				String posCode=req.getSession().getAttribute("loginPOS").toString();
				String posDate = req.getSession().getAttribute("gPOSDate").toString();
				listColumnNames="Bill No,Settle Mode,Settle Amount";
				searchFormTitle="Bill For Change Settlement";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,clientCode+"#"+posCode+"#"+posDate);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
			case "BillForCreditBillReceipt":
			{
				String posCode=req.getSession().getAttribute("loginPOS").toString();
				listColumnNames="Bill No,Bill Date,Credit Amount";
				searchFormTitle="Search Master";
				JSONObject jObjSearchData = funGetPOSSearchDetails(formName,strCustomerCode+"#"+posCode);
				jArrSearchList=(JSONArray) jObjSearchData.get(formName);
				break;
			}
			
		}
		
		if(null!=jArrSearchList)
		{
			for(int cnt=0;cnt<jArrSearchList.size();cnt++)
			{
				JSONArray jArrSearchRow=(JSONArray) jArrSearchList.get(cnt);
				Object[] arrObj=new Object[jArrSearchRow.size()];
				for(int row=0;row<jArrSearchRow.size();row++)
				{
					arrObj[row]=jArrSearchRow.get(row);
				}
				listSearchData.add(arrObj);
			}
		}
				
		mainMap.put("criteria", criteria);
		mainMap.put("listColumnNames", listColumnNames);
		mainMap.put("idColumnName", idColumnName);
		mainMap.put("searchFormTitle",searchFormTitle);
		mainMap.put("listSearchData",listSearchData);
		return mainMap;
	}
	/*
	 * End WebPOS Search
	 */
	
	private JSONObject funGetPOSSearchDetails(String masterName, String clientCode)
	{
		
		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData=new JSONArray();
		StringBuilder hqlQuery = new StringBuilder();
		try
		{
		    switch(masterName)
		    {
		    	case "POSTaxMaster":
		    		List list=objBaseService.funGetSerachList("getAllTaxMaster",clientCode);
		    		
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						   
						    jArrData.add(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
		    		break;	
		    		
		    	case "POSPromotionMaster":
			    	
		     		  
						 list = objBaseService.funGetSerachList("getAllPromotionMaster",clientCode);
							
						
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						    jArrDataRow.add(obj[2]);
						    jArrData.add(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
						break;
						
		    	case "POSOrderMaster":
					
		    		list = objBaseService.funGetSerachList("getAllOrderMaster",clientCode);
					
					 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						  
						    jArrData.add(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;	
		    	case "POSDeliveryBoyMaster":
					
					
		    		list = objBaseService.funGetSerachList("getAllDeliveryBoyMaster",clientCode);
		    		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						  
						    jArrData.add(jArrDataRow);
						}
		    		 
					jObjSearchData.put(masterName, jArrData);
					break;
					
			    	
		    	case "POSAdvOrderMaster":
					
					
		    		list = objBaseService.funGetSerachList("getAllAdvOrderMaster",clientCode);
						
		    		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						  
						    jArrData.add(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;	
				    	
		     	case "POSSettlementMaster":
					
		     		list = objBaseService.funGetSerachList("getAllSettlementMaster",clientCode);
						
		     		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						  
						    jArrData.add(jArrDataRow);
						}
					jObjSearchData.put(masterName, jArrData);
					break;
					
		     	 case "POSMaster":
						
		     		list = objBaseService.funGetSerachList("getAllPOSMaster",clientCode);
					   
						
		     		for(int cnt=0;cnt<list.size();cnt++)
					{
						Object[] obj = (Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.add(obj[0]);
					    jArrDataRow.add(obj[1]);
					   
					    jArrData.add(jArrDataRow);
					}
					jObjSearchData.put(masterName, jArrData);
					break;
				
		     	case "POSAreaMaster":
					
		     		list = objBaseService.funGetSerachList("getAllAreaMaster",clientCode);
						
		     		 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						    jArrDataRow.add(obj[2]);
						    jArrData.add(jArrDataRow);
						}
		     		jObjSearchData.put(masterName, jArrData);
					break;
				    

				    
			    case "POSWaiterMaster":
					
			    	list = objBaseService.funGetSerachList("getAllWaiterMaster",clientCode);
			    	 for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(obj[0]);
						    jArrDataRow.add(obj[1]);
						    jArrDataRow.add(obj[2]);
						    jArrData.add(jArrDataRow);
						}
		     		jObjSearchData.put(masterName, jArrData);
					
					
					break;
					
		    	case "WebBooksAcountMaster":
		    		//clsWebbooksIntegration objWB = new clsWebbooksIntegration();
		    		//jObjSearchData=objWB.funGetAccountDtl(masterName, clientCode);
		    		break;
		    		
		    	case "ExciseLicenseMaster":
		    		//clsExciseIntegration objEx = new clsExciseIntegration();
		    		//jObjSearchData=objEx.funGetLicenceDtlforPOS(masterName, clientCode);
		    		break;
		    	
			    case "POSCustomerAreaMaster":
			    	
			    	//List list1=objintfBaseService.funGetSerachList("getALLCustomerArea1");
			    	list=objBaseService.funGetSerachList("getALLCustomerArea",clientCode);
			    	for(int i=0 ;i<list.size();i++ )
		 	    	{
			    		Object obj[]=(Object[]) list.get(i);
			    		//clsCustomerAreaMasterModel obj = (clsCustomerAreaMasterModel) list.get(i);
		            	JSONArray jObj= new JSONArray();
		            		                     
		            	jObj.add(obj[0].toString());
		            	jObj.add(obj[1].toString());
		            	jObj.add(obj[2].toString());
		            	
		            	jArrData.add(jObj);
		            }
			    	jObjSearchData.put(masterName,jArrData);
			    break;	
			    
			    case "POSZoneMaster":
			    	
			    	
			    	list=objBaseService.funGetSerachList("getALLZone",clientCode);
			    	for(int i=0 ;i<list.size();i++ )
		 	    	{
			    		//clsZoneMasterModel obj = (clsZoneMasterModel) list.get(i);
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObj= new JSONArray();
		            		                     
		            	jObj.add(obj[0].toString());
		            	jObj.add(obj[1].toString());	            	
		                jArrData.add(jObj);                
		            }
			    	jObjSearchData.put(masterName,jArrData);
			    break;	
			    
			 case "POSshiMaster":
			 	
			 	
			 	list=objBaseService.funGetSerachList("getALLShift",clientCode);
			 	for(int i=0 ;i<list.size();i++ )
			  	{
			 		//clsShiftMasterModel obj = (clsShiftMasterModel) list.get(i);
			 		Object obj[]=(Object[]) list.get(i);
			     	JSONArray jObj= new JSONArray();
			     		                     
			     	jObj.add(obj[0].toString());
			     	jObj.add(obj[1].toString());
			     	jObj.add(obj[2].toString());
			     	jObj.add(obj[3].toString());
			     	jObj.add(obj[4].toString());
			     	jObj.add(obj[5].toString());
			     	
			     	
			         jArrData.add(jObj);		         
			    
			     }
			 	jObjSearchData.put(masterName,jArrData);
			 break;	
			 
			 case "POSReasonMaster":
				 	
				 hqlQuery.setLength(0);
				 hqlQuery.append(" select a.strReasonCode,a.strReasonName from tblreasonmaster a "
				 		+ "where a.strClientCode='"+clientCode+"' order by a.strReasonCode; "); 
				
				list=objBaseService.funGetList(hqlQuery, "sql");
				 
			 	if(list.size()>0)
			 	{
			 		for(int cnt=0;cnt<list.size();cnt++)
					{
				 		//clsReasonMasterModel objModelReason = (clsReasonMasterModel) list.get(cnt);
				 		Object obj[]=(Object[]) list.get(cnt);
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.add(obj[0].toString());
					    jArrDataRow.add(obj[1].toString());
					    jArrData.add(jArrDataRow);
					}
			 	}	 	
			 	jObjSearchData.put(masterName,jArrData);
			 break;	
			 
			 case "POSCustomerTypeMaster":
				 	
				 	
				 	list=objBaseService.funGetSerachList("getALLCustomerType",clientCode);
				 	for(int cnt=0;cnt<list.size();cnt++)
					{
				 		//clsCustomerTypeMasterModel objModel1 = (clsCustomerTypeMasterModel) list.get(cnt);
				 		Object obj[]=(Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.add(obj[0].toString());
					    jArrDataRow.add(obj[1].toString());
					    jArrDataRow.add(obj[2].toString());
					    
					    jArrData.add(jArrDataRow);
					}	 	
				 	jObjSearchData.put(masterName,jArrData);
				 break;	
				 
			 case "POSCustomerMaster":
				 	
				 	
				 	list=objBaseService.funGetSerachList("getALLCustomer",clientCode);
				 	for(int cnt=0;cnt<list.size();cnt++)
					{
				 		//clsCustomerMasterModel objModelCustomer = (clsCustomerMasterModel) list.get(cnt);
				 		Object obj[]=(Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    jArrDataRow.add(obj[0].toString());
					    jArrDataRow.add(obj[1].toString());
					    jArrDataRow.add(obj[2].toString());
					    jArrDataRow.add(obj[3].toString());
					    
					    		
				
					    
					    jArrData.add(jArrDataRow);
					}	
				 	jObjSearchData.put(masterName,jArrData);
				 break;	
				 
			
			    case "POSItemModifierMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(ob[0].toString());
						    jArrDataRow.add(ob[1].toString());
						    jArrDataRow.add(ob[2].toString());
						    
						    jArrData.add(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			    
			    case "POSMenuHeadMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(ob[0].toString());
						    jArrDataRow.add(ob[1].toString());
						    jArrDataRow.add(ob[2].toString());
						    
						    jArrData.add(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			    	
			        
			    case "POSSubMenuHeadMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(ob[0].toString());
						    jArrDataRow.add(ob[1].toString());
						    jArrDataRow.add(ob[2].toString());
						    jArrDataRow.add(ob[3].toString());
						    jArrData.add(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			        break;	
			        
			    case "POSMenuItemMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(ob[0].toString());
						    jArrDataRow.add(ob[1].toString());
						    jArrDataRow.add(ob[2].toString());
						    jArrDataRow.add(ob[3].toString());
						    jArrDataRow.add(ob[4].toString());
						    jArrDataRow.add(ob[5].toString());
						    jArrDataRow.add(ob[6].toString());
						    jArrData.add(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
			    	break;
			    	
			    case "POSModifierGroupMaster":
			    	list=objBaseService.funGetSerachList(masterName,clientCode);
			    	
			        if(list.size()>0)
			        {
			        	for(int i=0;i<list.size();i++)
			        	{
			        		Object ob[]=(Object[]) list.get(i);
			        		 JSONArray jArrDataRow = new JSONArray();
						    jArrDataRow.add(ob[0].toString());
						    jArrDataRow.add(ob[1].toString());
						    jArrDataRow.add(ob[2].toString());
						    jArrDataRow.add(ob[3].toString());
						    jArrData.add(jArrDataRow);
						}
							jObjSearchData.put(masterName, jArrData);
			        	
			        }
				    break;
				    
				    
			    case "POSCostCenterMaster":
			    	list = 	objBaseService.funGetSerachList("POSCostCenter",clientCode);

			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.add(obj[0].toString());
		            	jObjArr.add(obj[1].toString());
		            	jArrData.add(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    case "POSCounterMaster":
			    
			    	list = 	objBaseService.funGetSerachList("POSCounter",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.add(obj[0].toString());
		            	jObjArr.add(obj[1].toString());
		            	jObjArr.add(obj[2].toString());
		            	jObjArr.add(obj[3].toString());
		            	jObjArr.add(obj[4].toString());
		            	jArrData.add(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    
			    case "POSDebitCardMaster":
			    	
			    	list = 	objBaseService.funGetSerachList("POSDebitCard",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
		            	JSONArray jObjArr= new JSONArray();

					    
		            	jObjArr.add(obj[0].toString());
		            	jObjArr.add(obj[1].toString());
		            	
		            	jArrData.add(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;
			    
			    case "POSFactoryMaster":
			    	
			    	list = 	objBaseService.funGetSerachList("POSFactory",clientCode);
			    	for(int i=0;i<list.size();i++)
					{
			    		Object obj[]=(Object[]) list.get(i);
			        	JSONArray jObjArr= new JSONArray();

					    
			        	jObjArr.add(obj[0].toString());
			        	jObjArr.add(obj[1].toString());
			        	jObjArr.add(obj[2].toString());
			        	jObjArr.add(obj[3].toString());
			        	jObjArr.add(obj[4].toString());
			        	jArrData.add(jObjArr);

					    
					}
					jObjSearchData.put(masterName, jArrData);	
			    break;    
			    
			    case "POSRecipeMaster":
				 	
				 	
				 	hqlQuery.append("select a.strRecipeCode ,a.strItemCode, b.strItemName "
		                           + " from tblrecipehd a left outer join tblitemmaster b on a.strItemCode=b.strItemCode "); 
				 	   
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    								
				 		for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.add(objArr[0].toString());//longPricingId
						    jArrDataRow.add(objArr[1].toString());//strItemCode
						    jArrDataRow.add(objArr[2].toString());//strItemName
						    
						    jArrData.add(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
				 	case "POSTableMaster":
						

				 		hqlQuery.setLength(0);
				 		hqlQuery.append("select a.strTableNo ,a.strTableName,"
		                        + "IFNULL(b.strAreaName,'') ,IFNULL(c.strWShortName,'') "
		                        + ",ifnull(d.strPosName,'All') ,a.strStatus  "
		                        + "from tbltablemaster a left outer join tblareamaster b "
		                        + "on a.strAreaCode=b.strAreaCode left outer join tblwaitermaster c "
		                        + "on a.strWaiterNo=c.strWaiterNo "
		                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
		                        + "order by a.strTableName");
			    		
		                		
				 		list=objBaseService.funGetList(hqlQuery, "sql");
				 		
		    		for(int cnt=0;cnt<list.size();cnt++)
					{
						Object[] objArr = (Object[]) list.get(cnt);
					    
					    JSONArray jArrDataRow = new JSONArray();
					    
					    jArrDataRow.add(objArr[0].toString());
					    jArrDataRow.add(objArr[1].toString());
					    jArrDataRow.add(objArr[2].toString());
					    jArrDataRow.add(objArr[3].toString());
					    jArrDataRow.add(objArr[4].toString());
					    jArrDataRow.add(objArr[5].toString());
					    
					    
					    jArrData.add(jArrDataRow);
					}
					jObjSearchData.put(masterName, jArrData);
					    
						break;        
			    
				 	case "POSMenuItemPricingMaster":
				 		hqlQuery.setLength(0);
				 		hqlQuery.append("select a.longPricingId,a.strItemCode,a.strItemName,IF(a.strPosCode='All','All',b.strPosName)strPosName,c.strAreaName,e.strMenuName "
								+",a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,a.strPriceSaturday,a.strPriceSunday "
								+",a.strPopular "
								+",a.dteFromDate,a.dteToDate,d.strCostCenterName,ifnull(f.strSubMenuHeadName,'')strSubMenuHeadName "
								+",a.strHourlyPricing "
								+"from tblmenuitempricingdtl a " 
								+"left outer join  tblsubmenuhead f on a.strSubMenuHeadCode=f.strSubMenuHeadCode "
								+",tblposmaster b,tblareamaster c,tblcostcentermaster d "
								+",tblmenuhd e  "
								+"where (a.strPosCode=b.strPosCode or a.strPosCode='All') "
								+"and a.strAreaCode=c.strAreaCode "
								+"and a.strCostCenterCode=d.strCostCenterCode "
								+"and a.strMenuCode=e.strMenuCode "
								+"GROUP BY a.longPricingId "
								+"ORDER BY a.longPricingId "); 
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    								
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.add(objArr[0].toString());//longPricingId
						    jArrDataRow.add(objArr[1].toString());//strItemCode
						    jArrDataRow.add(objArr[2].toString());//strItemName
						    jArrDataRow.add(objArr[3].toString());//strPosName
						    jArrDataRow.add(objArr[4].toString());//strAreaName
						    jArrDataRow.add(objArr[5].toString());//strMenuName
						    jArrDataRow.add(objArr[6].toString());//strPriceMonday
						    jArrDataRow.add(objArr[7].toString());//strPriceTuesday
						    jArrDataRow.add(objArr[8].toString());//strPriceWednesday
						    jArrDataRow.add(objArr[9].toString());//strPriceThursday
						    jArrDataRow.add(objArr[10].toString());//strPriceFriday
						    jArrDataRow.add(objArr[11].toString());//strPriceSaturday
						    jArrDataRow.add(objArr[12].toString());//strPriceSunday
						    jArrDataRow.add(objArr[13].toString());//strPopular
						    jArrDataRow.add(objArr[14].toString());//dteFromDate
						    jArrDataRow.add(objArr[15].toString());//dteToDate
						    jArrDataRow.add(objArr[16].toString());//strCostCenterName
						    jArrDataRow.add(objArr[17].toString());//strSubMenuHeadName
						    jArrDataRow.add(objArr[18].toString());//strHourlyPricing
						   
						    
						    jArrData.add(jArrDataRow);
						}
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
					 case "POSLoadTDHData":
						 	
						 	
						 	list=objBaseService.funGetSerachList("getALLTDH",clientCode);
						 	for(int cnt=0;cnt<list.size();cnt++)
							{
						 		//clsPOSTDHModel objTDHModel = (clsPOSTDHModel) list.get(cnt);
						 		Object obj[]=(Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    jArrDataRow.add(obj[0].toString());
							    jArrDataRow.add(obj[1].toString());
							    jArrDataRow.add(obj[2].toString());
							    jArrDataRow.add(obj[3].toString());
							    jArrDataRow.add(obj[4].toString());
							    
							    
							    jArrData.add(jArrDataRow);
							}
						 	jObjSearchData.put(masterName,jArrData);
						 break;	
						 
					 
				    		
					 case "POSTDHOnItem":
						 
				    		list=objBaseService.funGetSerachList("getALLItemPricing",clientCode);
				    		
								for(int cnt=0;cnt<list.size();cnt++)
								{
									Object[] obj = (Object[]) list.get(cnt);
								    
								    JSONArray jArrDataRow = new JSONArray();
								    jArrDataRow.add(obj[0]);
								    jArrDataRow.add(obj[1]);
								   
								    jArrData.add(jArrDataRow);
								}
								jObjSearchData.put(masterName, jArrData);
				    		break;	
				    		
					 
					    	
							
					 case "POSWiseItemIncentive":
						 	
				 		 boolean flgPreviousRecordFound=false;
				 		 
				 		hqlQuery.setLength(0);
				 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPOSCode,b.strPosName,a.strIncentiveType,a.dblIncentiveValue "
			                + " FROM tblposwiseitemwiseincentives a  left outer join tblposmaster b on a.strPosCode=b.strPosCode"); 
				 		
				 		if(!clientCode.equalsIgnoreCase("All"))
			            {
				 			
				 			hqlQuery.append(" Where a.strPOSCode='").append(clientCode).append("' "); 
			            }
				 		hqlQuery.append(" order by a.strItemCode ");
			            
						
				 		list=objBaseService.funGetList(hqlQuery, "sql");
			    		if(list.size()>0)
			    		{
			    			 flgPreviousRecordFound=true;
						for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] obj = (Object[]) list.get(cnt);
						    
						    JSONObject jobjDataRow = new JSONObject();
						    
						    jobjDataRow.put("strItemCode", obj[0].toString());
						    jobjDataRow.put("strItemName", obj[1].toString());
						    jobjDataRow.put("strPOSCode", obj[2].toString());
						    jobjDataRow.put("strPosName", obj[3].toString());
						    jobjDataRow.put("strIncentiveType", obj[4].toString());
						    jobjDataRow.put("dblIncentiveValue", obj[5].toString());
							
						   
						    jArrData.add(jobjDataRow);
						}
			    		}
			    		
			    		 if(!flgPreviousRecordFound)
			             {
			    			 hqlQuery.setLength(0);
			    			 	hqlQuery.append("SELECT a.strItemCode,a.strItemName,a.strPosCode,b.strPosName"
			    			 						+ " FROM tblmenuitempricingdtl a  "
			    			 						+ " left outer join tblposmaster b on a.strPosCode=b.strPosCode "); 
			    			 		
			    			 		if(!clientCode.equalsIgnoreCase("All"))
			    		            {
			    			 			
			    			 			hqlQuery.append(" Where a.strPOSCode='").append(clientCode).append("' "); 
			    		            }
			    			 		hqlQuery.append(" order by a.strItemCode ");
			    		            
			    					
			    			 		list=objBaseService.funGetList(hqlQuery, "sql");
			    			 		
			    			 		if(list.size()>0)
			    		    		{
			    			 			
			    					for(int cnt=0;cnt<list.size();cnt++)
			    					{
			    						Object[] obj = (Object[]) list.get(cnt);
			    					    
			    					    JSONObject jobjDataRow = new JSONObject();
			    					    
			    					    jobjDataRow.put("strItemCode", obj[0].toString());
			    					    jobjDataRow.put("strItemName", obj[1].toString());
			    					    jobjDataRow.put("strPOSCode", obj[2].toString());
			    					    jobjDataRow.put("strPosName", obj[3].toString());
			    					    jobjDataRow.put("strIncentiveType", "amt");
			    					    jobjDataRow.put("dblIncentiveValue","0.0");
			    						
			    					   
			    					    jArrData.add(jobjDataRow);
			    					}
			    		    		}
			    		    		
			             }
						jObjSearchData.put(masterName, jArrData);
							
				    	break; 
			    
			    
					 case "POSGroupMaster":
						 hqlQuery.setLength(0);
		    			 hqlQuery.append(" select a.strGroupCode,a.strGroupName,a.strOperationalYN from tblgrouphd a "
		    			 		+ "where a.strClientCode='"+clientCode+"' order by a.strGroupCode; ");
		    			 list=objBaseService.funGetList(hqlQuery, "sql");
		    			 if(list.size()>0)
		    			 {
		    				for(int cnt=0;cnt<list.size();cnt++)
							{
								Object ob[]=(Object[]) list.get(cnt);
								JSONArray jArrDataRow = new JSONArray();
							    jArrDataRow.add(ob[0].toString());
							    jArrDataRow.add(ob[1].toString());
							    jArrDataRow.add(ob[2].toString());
							    jArrData.add(jArrDataRow);
							} 
		    			 }
						jObjSearchData.put(masterName, jArrData);
						break;
							
    	case "MenuItemForPrice":
					    	
				     		hqlQuery.setLength(0);
		    			 	hqlQuery.append(" select a.strItemCode ,a.strItemName ,a.strItemType, a.strRevenueHead ,a.strExternalCode ,b.strSubGroupName "
			                       + " from tblitemmaster a,tblsubgrouphd b "
			                       + " where a.strSubGroupCode=b.strSubGroupCode "
			                       + " and (a.strRawMaterial='N' or a.strItemForSale='Y') "
			                       + " order by a.strItemName");
		    			 	list=objBaseService.funGetList(hqlQuery, "sql");
	    			 		
	    			 		if(list.size()>0)
	    		    		{
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//longPricingId
							    jArrDataRow.add(objArr[1].toString());//strItemCode
							    jArrDataRow.add(objArr[2].toString());//strItemName
							    jArrDataRow.add(objArr[3].toString());//strPosName
							    jArrDataRow.add(objArr[4].toString());//strAreaName
							    jArrDataRow.add(objArr[5].toString());//strMenuName
							  
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break; 
					    	
				    	case "MenuItemForRecipeChild":
					    	
				    		hqlQuery.setLength(0);
		    			 	hqlQuery.append("select a.strItemCode ,a.strItemName ,a.strItemType ,a.strRevenueHead,ifnull(a.strTaxIndicator,''),a.strExternalCode ,b.strSubGroupName  "
			                       + "from tblitemmaster a,tblsubgrouphd b "
			                       + "where a.strSubGroupCode=b.strSubGroupCode "
			                       + "and a.strRawMaterial='Y' "
			                       + "order by a.strItemName;");
		    			 	list=objBaseService.funGetList(hqlQuery, "sql");			
				    		if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//longPricingId
							    jArrDataRow.add(objArr[1].toString());//strItemCode
							    jArrDataRow.add(objArr[2].toString());//strItemName
							    jArrDataRow.add(objArr[3].toString());//strPosName
							    jArrDataRow.add(objArr[4].toString());//strAreaName
							    jArrDataRow.add(objArr[5].toString());//strMenuName
							    jArrDataRow.add(objArr[6].toString());//strPriceMonday
							  
							    
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break;
					    	
				    	case "POSUnSettleBill":
				    		
				    		
				    		String []splitCleientCode=clientCode.split("#");
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select  a.strBillNo ,ifnull(d.strTableName,'ND')"
			                        + " ,a.dblGrandTotal ,c.strSettelmentDesc, a.strUserCreated"
			                        + " , a.strRemarks"
			                        + " from tblbillhd a inner join tblbillsettlementdtl b on a.strbillno=b.strbillno"
			                        + " inner join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode"
			                        + " left outer join tbltablemaster d on a.strTableNo=d.strTableNo"
			                        + " where date(a.dteBillDate)='" + splitCleientCode[2] + "'  and c.strSettelmentType!='Complementary' "
			                        + " and a.strPOSCode='" + splitCleientCode[1] + "'  ") ;
				    		
				    		 hqlQuery.append(" group by a.strbillno order by a.strbillno DESC");
				    		 
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{	 
				    			 for(int cnt=0;cnt<list.size();cnt++)
				    			 {
				    				 Object[] objArr = (Object[]) list.get(cnt);
						    
				    				 JSONArray jArrDataRow = new JSONArray();
						    
				    				 jArrDataRow.add(objArr[0].toString());
				    				 jArrDataRow.add(objArr[1].toString());
				    				 jArrDataRow.add(objArr[2].toString());
				    				 jArrDataRow.add(objArr[3].toString());
				    				 jArrDataRow.add(objArr[4].toString());
				    				 jArrDataRow.add(objArr[5].toString());
						   
				    				 jArrData.add(jArrDataRow);
				    			 }
							}
						      jObjSearchData.put(masterName, jArrData);
						    
							break;
			 
				    	case "POSItemList":
					    	
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strItemCode,a.strItemName, "
				    				+ " a.strItemType ,a.strExternalCode "
				    				+ " from tblitemmaster a,tblsubgrouphd b "
				    				+ " where a.strSubGroupCode=b.strSubGroupCode "
				    				+ " order by a.strItemName "); 
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//item code
							    jArrDataRow.add(objArr[1].toString());//item name
							    jArrDataRow.add(objArr[2].toString());//item type
							    jArrDataRow.add(objArr[3].toString());//external code
							    
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
								
					    	break; 
					    	
					    	
					   	case "PhysicalStock":
						{	
							String []data=clientCode.split("#");
							String posCode=data[1];
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strPSPCode ,b.strItemCode, c.strItemName,a.dteDateCreated "
			                            + " from tblPSPhd a,tblPSPdtl b,tblItemMaster c "
			                            + "  where a.strPSPCode=b.strPSPCode  and b.strItemCode=c.strItemCode "
			                            + "  and a.strPOSCode='" + posCode + "' "
			                            + "  group by a.strPSPCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//psp code
							    jArrDataRow.add(objArr[1].toString());//item code
							    jArrDataRow.add(objArr[2].toString());//item name
							    jArrDataRow.add(objArr[3].toString());//date created
							    
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}	
						case "StockIn":
						{	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strStkInCode ,a.strReasonCode ,b.strReasonName,a.dteDateCreated  "
			                            + "from tblstkinhd a left outer join tblreasonmaster b "
			                            + "on a.strReasonCode=b.strReasonCode "
			                            + "where a.strReasonCode=b.strReasonCode order by a.strStkInCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{						
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//stockIn code
							    jArrDataRow.add(objArr[1].toString());//Reason Code
							    jArrDataRow.add(objArr[2].toString());//Reason name
							    jArrDataRow.add(objArr[3].toString());//date created
							    
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}	
						case "StockOut":
						{	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strStkOutCode ,a.strReasonCode , b.strReasonName  ,a.dteDateCreated  "
			                            + "from tblstkouthd a left outer join tblreasonmaster b "
			                            + "on a.strReasonCode=b.strReasonCode "
			                            + "where a.strReasonCode=b.strReasonCode order by a.strStkOutCode "); 
							
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{					
							for(int cnt=0;cnt<list.size();cnt++)
							{
								Object[] objArr = (Object[]) list.get(cnt);
							    
							    JSONArray jArrDataRow = new JSONArray();
							    
							    jArrDataRow.add(objArr[0].toString());//stockOut code
							    jArrDataRow.add(objArr[1].toString());//Reason Code
							    jArrDataRow.add(objArr[2].toString());//Reason name
							    jArrDataRow.add(objArr[3].toString());//date created
							    
							    jArrData.add(jArrDataRow);
							}
							}
							jObjSearchData.put(masterName, jArrData);
							break;	
						}
						
						case "POSTableReservation":
					    	
							hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strResCode,b.strCustomerName,ifnull(b.strBuldingCode,'') "
			                        + ",ifnull(b.strBuildingName,''),b.strCity "
			                        + "from tblreservation a "
			                        + "left outer join tblcustomermaster b on  a.strCustomerCode=b.strCustomerCode "
			                        + "left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode "
			                        + "where a.strCustomerCode<>'' and a.strPOSCode='" +clientCode + "' ");
				    		
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)	
				    		 {
			    		for(int cnt=0;cnt<list.size();cnt++)
						{
							Object[] objArr = (Object[]) list.get(cnt);
						    
						    JSONArray jArrDataRow = new JSONArray();
						    
						    jArrDataRow.add(objArr[0].toString());
						    jArrDataRow.add(objArr[1].toString());
						    jArrDataRow.add(objArr[2].toString());
						    jArrDataRow.add(objArr[3].toString());
						    jArrDataRow.add(objArr[4].toString());
						   
						    
						    
						    jArrData.add(jArrDataRow);
						}
				    		 }
						jObjSearchData.put(masterName, jArrData);
						    
							break;
				     		
				     	case "POSTableReserveMaster":
							

				     		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strTableNo ,a.strTableName,"
			                        + "IFNULL(b.strAreaName,'') ,IFNULL(c.strWShortName,'') "
			                        + ",ifnull(d.strPosName,'All') ,a.strStatus  "
			                        + "from tbltablemaster a left outer join tblareamaster b "
			                        + "on a.strAreaCode=b.strAreaCode left outer join tblwaitermaster c "
			                        + "on a.strWaiterNo=c.strWaiterNo "
			                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode where a.strStatus!='Reserve' "
			                        + "order by a.strTableName");
				    		
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)	
				    		 {
						    	for(int cnt=0;cnt<list.size();cnt++)
								{
									Object[] objArr = (Object[]) list.get(cnt);
									JSONArray jArrDataRow = new JSONArray();
									jArrDataRow.add(objArr[0].toString());
									jArrDataRow.add(objArr[1].toString());
									jArrDataRow.add(objArr[2].toString());
									jArrDataRow.add(objArr[3].toString());
									jArrDataRow.add(objArr[4].toString());
									jArrDataRow.add(objArr[5].toString());
								    
								    jArrData.add(jArrDataRow);
								}
				    		 }
							jObjSearchData.put(masterName, jArrData);
							break;	
								
				     	 case "POSSubGroupMaster":
							  	list=objBaseService.funGetSerachList(masterName,clientCode);
								clsSubGroupMasterHdModel objModel1 = null;
								for(int cnt=0;cnt<list.size();cnt++)
								{
									Object ob[]=(Object[]) list.get(cnt);
									JSONArray jArrDataRow = new JSONArray();
								    jArrDataRow.add(ob[0].toString());
								    jArrDataRow.add(ob[1].toString());
								    jArrDataRow.add(ob[2].toString());
								    jArrDataRow.add(ob[3].toString());
								    jArrData.add(jArrDataRow);
								}
								jObjSearchData.put(masterName, jArrData);
								break;
				   
				     	case "POSDiscountMaster":
				    		splitCleientCode=clientCode.split("#");
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strDiscCode,a.strDiscName,b.strPosName,a.strDiscOn "
				    				+ "from tbldischd a ,tblposmaster b "
				    				+ "where (a.strPOSCode=b.strPosCode or a.strPOSCode='All') "
				    				+ "and date(a.dteToDate)>='" + splitCleientCode[1] + "' "
				    				+ "order by a.strDiscCode ");
				    		list=objBaseService.funGetList(hqlQuery, "sql");
				    		for(int cnt=0;cnt<list.size();cnt++)
								{
									Object[] obj = (Object[]) list.get(cnt);
								    
								    JSONArray jArrDataRow = new JSONArray();
								    jArrDataRow.add(obj[0]);
								    jArrDataRow.add(obj[1]);
								    jArrDataRow.add(obj[2]);
								    jArrDataRow.add(obj[3]);
								    
								    jArrData.add(jArrDataRow);
								}
								jObjSearchData.put(masterName, jArrData);
				    		break;		
				    		
				    		
                           case "BillForChangeSettlement":
				    		
				    		String []splitClientCode=clientCode.split("#");
				    		hqlQuery.setLength(0);
				    		hqlQuery.append("select a.strBillNo as Bill_No,a.strSettelmentMode as Settlement_Mode,sum(b.dblSettlementAmt) as Settlement_Amount  "
				    				+ " from tblbillhd a,tblbillsettlementdtl b, tblsettelmenthd c  "
				    				+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
				    				+ " and c.strSettelmentType!='Complementary'  and date(a.dteBillDate)=date(b.dteBillDate)  "
				    				+ " and a.strPOSCode='" + splitClientCode[1] + "'  and date(a.dteBillDate)='" + splitClientCode[2] + "'  "
				    				+ " group by a.strBillNo") ;
				    		 
				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
				    		 if(null != list)		
							{	 
				    			 for(int cnt=0;cnt<list.size();cnt++)
				    			 {
				    				 Object[] objArr = (Object[]) list.get(cnt);
						    
				    				 JSONArray jArrDataRow = new JSONArray();
						    
				    				 jArrDataRow.add(objArr[0].toString());
				    				 jArrDataRow.add(objArr[1].toString());
				    				 jArrDataRow.add(objArr[2].toString());
						   
				    				 jArrData.add(jArrDataRow);
				    			 }
							}
						      jObjSearchData.put(masterName, jArrData);
						    
							break;	
							
                           case "BillForCreditBillReceipt":
   				    		
   				    		String []spData=clientCode.split("#");
   				    	    
   				    		//live
   				    		hqlQuery.setLength(0);
   				    		hqlQuery.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
   				                    + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
   				                    + "WHERE a.strBillNo=b.strBillNo  "
   				                    + "AND b.strSettlementCode=c.strSettelmentCode  "
   				                    + "and date(a.dtebilldate)=date(b.dtebilldate)  "
   				                    + "and a.strClientCode=b.strClientCode  "
   				                    + "AND c.strSettelmentType='Credit'  "
   				                    + "AND a.strPOSCode='" + spData[1] + "'  "
   				                    + "AND a.strCustomerCode='" + spData[0] + "' "
   				                    + "GROUP BY a.strBillNo ");
   				    		 
   				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
   				    		 if(null != list)		
   							 {	 
   				    			 for(int cnt=0;cnt<list.size();cnt++)
   				    			 {
   				    				 Object[] objArr = (Object[]) list.get(cnt);
   				    				 String billNo = objArr[0].toString();
   				    				 String filterBillDate = objArr[1].toString();
   				    				 String billDate = objArr[2].toString();
   				    				 clientCode = objArr[3].toString();
   				    				 double creditAmount = Double.valueOf(objArr[4].toString());
   				    				 
	   				    			//remove full paid bills
	   				                //live
   				    				hqlQuery.setLength(0);
   				    				hqlQuery.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
			                          + "from tblqcreditbillreceipthd a "
			                          + "where a.strPOSCode='" + spData[1] + "'  "
			                          + "and a.strBillNo='" + billNo + "' "
			                          + "and date(a.dteBillDate)='" + filterBillDate + "' "
			                          + "and a.strClientCode='" + clientCode + "'  group by a.strBillNo");
   				    				 
	   				    			 List listPaidBill=objBaseService.funGetList(hqlQuery, "sql");	
	   	   				    		 if(null != listPaidBill && listPaidBill.size()>0)		
	   	   							  {	 
	   	   				    			 for(int i=0;i<listPaidBill.size();i++)
	   	   				    			 {
	   	   				    				Object[] objPaidBillArr = (Object[]) listPaidBill.get(i);
	   	   				    			    double totalReceiptAmt = Double.valueOf(objPaidBillArr[4].toString());
			   	   		                    if (Math.rint(creditAmount) == Math.rint(totalReceiptAmt))
			   	   		                    {
			   	   		                        //dont add
			   	   		                    }
			   	   		                    else
			   	   		                    {
			   	   		                    	JSONArray jArrDataRow = new JSONArray();
			   				    				jArrDataRow.add(billNo);
			   				    				jArrDataRow.add(billDate);
			   				    				jArrDataRow.add(creditAmount);
			   				    				jArrDataRow.add(clientCode);
			   				    				jArrData.add(jArrDataRow);
			   	   		                    }
	   	   				    			 }
		   	   						   }
			   	   				       else
				   	   	                {
					   	   	                    JSONArray jArrDataRow = new JSONArray();
			   				    				jArrDataRow.add(billNo);
			   				    				jArrDataRow.add(billDate);
			   				    				jArrDataRow.add(creditAmount);
			   				    				jArrDataRow.add(clientCode);
			   				    				jArrData.add(jArrDataRow);
				   	   	                }
   				    			  }
   							}
   				    		
   				    	//QFile
    				    		hqlQuery.setLength(0);
    				    		hqlQuery.append("SELECT a.strBillNo,date(a.dteBillDate),a.dteBillDate,a.strClientCode, SUM(b.dblSettlementAmt) "
    				                    + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
    				                    + "WHERE a.strBillNo=b.strBillNo  "
    				                    + "AND b.strSettlementCode=c.strSettelmentCode  "
    				                    + "and date(a.dtebilldate)=date(b.dtebilldate)  "
    				                    + "and a.strClientCode=b.strClientCode  "
    				                    + "AND c.strSettelmentType='Credit'  "
    				                    + "AND a.strPOSCode='" + spData[1]  + "'  "
    				                    + "AND a.strCustomerCode='" + spData[0]  + "' "
    				                    + "GROUP BY a.strBillNo ");
    				    		
    				    		
    				    		 list=objBaseService.funGetList(hqlQuery, "sql");	
    				    		 if(null != list)		
    							 {	 
    				    			 for(int cnt=0;cnt<list.size();cnt++)
    				    			 {
    				    				 Object[] objArr = (Object[]) list.get(cnt);
    				    				 String billNo = objArr[0].toString();
    				    				 String filterBillDate = objArr[1].toString();
    				    				 String billDate = objArr[2].toString();
    				    				 clientCode = objArr[3].toString();
    				    				 double creditAmount = Double.valueOf(objArr[4].toString());
    				    				 
 	   				    			//remove full paid bills
 	   				                //live
    				    				hqlQuery.setLength(0);
    				    				hqlQuery.append("select a.strBillNo,date(a.dteBillDate),a.strPOSCode,a.strClientCode,sum(a.dblReceiptAmt) "
 			                          + "from tblqcreditbillreceipthd a "
 			                          + "where a.strPOSCode='" + spData[1] + "'  "
 			                          + "and a.strBillNo='" + billNo + "' "
 			                          + "and date(a.dteBillDate)='" + filterBillDate + "' "
 			                          + "and a.strClientCode='" + clientCode + "'   group by a.strBillNo");
    				    				 
 	   				    			 List listPaidBill=objBaseService.funGetList(hqlQuery, "sql");	
 	   	   				    		 if(null != listPaidBill && listPaidBill.size()>0)		
 	   	   							  {	 
 	   	   				    			 for(int i=0;i<listPaidBill.size();i++)
 	   	   				    			 {
 	   	   				    				Object[] objPaidBillArr = (Object[]) listPaidBill.get(i);
 		   	   				    			double totalReceiptAmt = Double.valueOf(objPaidBillArr[4].toString());
 			   	   		                    if (Math.rint(creditAmount) == Math.rint(totalReceiptAmt))
 			   	   		                    {
 			   	   		                        //dont add
 			   	   		                    }
 			   	   		                    else
 			   	   		                    {
 			   	   		                    	JSONArray jArrDataRow = new JSONArray();
 			   				    				jArrDataRow.add(billNo);
 			   				    				jArrDataRow.add(billDate);
 			   				    				jArrDataRow.add(creditAmount);
 			   				    				jArrDataRow.add(clientCode);
 			   				    				jArrData.add(jArrDataRow);
 			   	   		                    }
 	   	   				    			 }
 		   	   						   }
 			   	   				       else
 				   	   	                {
 					   	   	                    JSONArray jArrDataRow = new JSONArray();
 			   				    				jArrDataRow.add(billNo);
 			   				    				jArrDataRow.add(billDate);
 			   				    				jArrDataRow.add(creditAmount);
 			   				    				jArrDataRow.add(clientCode);
 			   				    				jArrData.add(jArrDataRow);
 				   	   	                }
    				    			  }
    							} 
   				    		 
   						     jObjSearchData.put(masterName, jArrData);
   						    
   							break;
							
							

		    }
			
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return jObjSearchData;
		
		
	}
	
	
	private String getCriteriaQuery(String columnNames, String search_with,String tableName){
		String criteria = "";
		if(columnNames != null && columnNames.trim().length() > 0 &&   search_with != null && search_with.trim().length() > 0  )
		{			
			if(tableName.contains("where")){
				criteria += " and ( ";
			}
			else{
				criteria += " where (";
			}
			for(String columnName: columnNames.split(","))
			{				
				criteria+= columnName + " LIKE '%"+ search_with + "%'  OR ";
				
			}
			criteria = criteria.substring(0,criteria.lastIndexOf("OR")-2);
			criteria=criteria+" )";
		}
		return criteria;
	}
	
	@SuppressWarnings("rawtypes")
	public List<clsFormSearchElements> funSetFormSearchElements(List list)
	{
		List<clsFormSearchElements> listSearchForm=new ArrayList<clsFormSearchElements>();
		for(int i=0;i<list.size();i++)
		{
			Object[] ob=(Object[])list.get(i);
			clsFormSearchElements objSearchForm=null;
			
			switch(ob.length)
			{
				case 2:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString());
					listSearchForm.add(objSearchForm);
					break;
			
				case 3:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 4:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),ob[3].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 5:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),ob[3].toString(),ob[4].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 6:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 7:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 8:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 9:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 10:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 11:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 12:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 13:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 14:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 15:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 16:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 17:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 18:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString()
															,ob[17].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 19:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString()
															,ob[17].toString(),ob[18].toString());
					listSearchForm.add(objSearchForm);
					break;
					
					
				case 20:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString()
															,ob[17].toString(),ob[18].toString(),ob[19].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 21:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString()
															,ob[17].toString(),ob[18].toString(),ob[19].toString(),ob[20].toString());
					listSearchForm.add(objSearchForm);
					break;
					
				case 22:
					objSearchForm=new clsFormSearchElements(ob[0].toString(),ob[1].toString(),ob[2].toString(),
															ob[3].toString(),ob[4].toString(),ob[5].toString(),
															ob[6].toString(),ob[7].toString(),ob[8].toString(),
															ob[9].toString(),ob[10].toString(),ob[11].toString(),ob[12].toString()
															,ob[12].toString(),ob[12].toString(),ob[12].toString(),ob[12].toString()
															,ob[13].toString(),ob[14].toString(),ob[15].toString(),ob[16].toString()
															,ob[17].toString(),ob[18].toString(),ob[19].toString(),ob[20].toString()
															,ob[21].toString());
					listSearchForm.add(objSearchForm);
					break;
					
					
			}			
		}
		return listSearchForm;
	}
	
	
	
	
	
	
	
}
