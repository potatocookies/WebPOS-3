
package com.sanguine.webpos.controller;

import java.net.URLEncoder;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAssignHomeDeliveryBean;
import com.sanguine.webpos.bean.clsPOSMultiBillSettleInCashBean;
import com.sanguine.webpos.model.clsSettlementMasterModel;
import com.sanguine.webpos.model.clsTableMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSTransactionService;

@Controller
public class clsPOSMultiBillSettleInCashController {
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired
	private clsPOSTransactionService objTransactionService;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	Map map=new HashMap();

	@RequestMapping(value = "/frmPOSMultiBillSettle", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSAssignHomeDeliveryBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		
		model.put("urlHits",urlHits);
		List list =funLoadUnsettleBillDtlData( request);
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		model.put("tblheader",list.get(0));
		model.put("details",list.get(1));
		
		Map hmPayMode = new HashMap();
		List listPayMode = new ArrayList();
		list=objMasterService.funLoadSettlementDtl(strClientCode);
		listPayMode.add("All");
		hmPayMode.put("All", "All");
		clsSettlementMasterModel objModel = null;
		for(int cnt=0;cnt<list.size();cnt++)
		{
			objModel = (clsSettlementMasterModel) list.get(cnt);
			listPayMode.add(objModel.getStrSettelmentDesc());
			hmPayMode.put(objModel.getStrSettelmentCode(), objModel.getStrSettelmentDesc());
		}
		model.put("PayMode",listPayMode);	
		
		
		Map hmTableName = new HashMap();
		List listTableName = new ArrayList();
		list=objMasterService.funGetTableList(strClientCode);
		listPayMode.add("All");
		hmTableName.put("All", "All");
		List tmpList = new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			clsTableMasterModel obj = new clsTableMasterModel();
			obj = (clsTableMasterModel)list.get(i);
			hmTableName.put(obj.getStrTableNo(), obj.getStrTableName());
			

		}
		/*for(int cnt=0;cnt<list.size();cnt++)
		{
			objModel = (clsSettlementMasterModel) list.get(cnt);
			listPayMode.add(objModel.getStrSettelmentDesc());
			hmTableName.put(objModel.getStrSettelmentCode(), objModel.getStrSettelmentDesc());
		}*/
		model.put("Table",hmTableName);	

		
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMultiBillSettle_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMultiBillSettle");
		}else {
			return null;
		}
	}
	
	@RequestMapping(value = "/loadUnsettleBillDtlData", method = RequestMethod.GET)
	public @ResponseBody List  funLoadUnsettleBillDtlData(HttpServletRequest req)
	{
		List listmain =new ArrayList();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		
		String posDate= req.getSession().getAttribute("gPOSDate").toString(); 
		List<clsPOSMultiBillSettleInCashBean> listMenuHedaData=new ArrayList<clsPOSMultiBillSettleInCashBean>();
		Map mapUnsettleBillData=new HashMap();
		
		/*String posURL1 =clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSTransactions/funGetSettleBillDtlData1"
		+ "?clientCode="+clientCode+"&posCode="+posCode+"&posDate="+URLEncoder.encode(posDate);
		JSONObject hmData=objGlobal.funGETMethodUrlJosnObjectData(posURL1);
		*/
		
		Map hmData=objTransactionService.funGetSettleBillDtlData(clientCode,posCode,URLEncoder.encode(posDate));
		List  listUnsettleBillList=(List) hmData.get("UnsettleBillDtl");
		
		  String  strDataType=(String) hmData.get("DataType");
		  
		  List<String> list =new ArrayList();
	        if(strDataType.equals("TableDetailWise"))
	        {
	        	list.add("Bill NO");
	        	list.add("Table");
	        	list.add("Waiter");
	        	list.add("Customer");
	        	list.add("Time");
	        	list.add("Amount");
	        	list.add("Select");
	        	listmain.add(list);
	        }
	        else
	        {
	        	list.add("Bill NO");
	        	list.add("Table");
	        	list.add("Customer");
	        	list.add("Area");
	        	list.add("Delivery Boy");
	        	list.add("Time");
	        	list.add("Amount");
	        	list.add("Select");
	        	
	        	listmain.add(list);
	        }
	        
	        if(strDataType.equals("TableDetailWise"))
	        {
	        	if(null!=listUnsettleBillList)
				{
					for(int cnt=0;cnt<listUnsettleBillList.size();cnt++)
					{
						Map jobj=(Map) listUnsettleBillList.get(cnt);
						clsPOSMultiBillSettleInCashBean objUnsettleBillDtl = new clsPOSMultiBillSettleInCashBean();
						objUnsettleBillDtl.setStrBillNo((String)jobj.get("strBillNo"));
						 map.put(jobj.get("strTableName"), jobj.get("strTableNo"));
						//objUnsettleBillDtl.put("strTableName", strTableName);
						objUnsettleBillDtl.setStrTableName((String)jobj.get("strTableName"));						
						objUnsettleBillDtl.setStrWShortName((String)jobj.get("strWShortName"));
						objUnsettleBillDtl.setStrCustomerName((String)jobj.get("strCustomerName"));				
						objUnsettleBillDtl.setDteBillDate((String)jobj.get("dteBillDate"));
						objUnsettleBillDtl.setDblGrandTotal((long)jobj.get("dblGrandTotal"));
						
						listMenuHedaData.add(objUnsettleBillDtl);
					}
					
					listmain.add(listMenuHedaData);
				}
	        }
	        else
	        {
	        if(null!=listUnsettleBillList)
			{
				for(int cnt=0;cnt<listUnsettleBillList.size();cnt++)
				{
					Map jobj=(Map) listUnsettleBillList.get(cnt);
					clsPOSMultiBillSettleInCashBean objUnsettleBillDtl = new clsPOSMultiBillSettleInCashBean();
					objUnsettleBillDtl.setStrBillNo((String)jobj.get("strBillNo"));
					 map.put(jobj.get("strTableName"), jobj.get("strTableNo"));
					//objUnsettleBillDtl.put("strTableName", strTableName);
					objUnsettleBillDtl.setStrTableName((String)jobj.get("strTableName"));		
					objUnsettleBillDtl.setStrCustomerName((String)jobj.get("strCustomerName"));
					objUnsettleBillDtl.setStrBuildingName((String)jobj.get("strBuildingName"));
					objUnsettleBillDtl.setStrDPName((String)jobj.get("strDPName"));
					objUnsettleBillDtl.setDteBillDate((String)jobj.get("dteBillDate"));
					System.out.println((String)jobj.get("strBillNo")+" "+jobj.get("dblGrandTotal") );
					objUnsettleBillDtl.setDblGrandTotal(Double.parseDouble(jobj.get("dblGrandTotal").toString()));
					
					listMenuHedaData.add(objUnsettleBillDtl);
				}
				
				listmain.add(listMenuHedaData);
			}
		
	        }
				        
	        Map mapUnsettleBillDataDtl=new HashMap<>();       
	        mapUnsettleBillDataDtl.put("UnsettleBillDtl",listUnsettleBillList);
	        mapUnsettleBillDataDtl.put("DataType",strDataType);
	       
 			return listmain;
			
	}

	@RequestMapping(value = "/settlePOSMultiBill", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMultiBillSettleInCashBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		String posCode="";
		String userCode="";
		String tableNo="";
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			posCode=req.getSession().getAttribute("loginPOS").toString();	
		    String posDate= req.getSession().getAttribute("gPOSDate").toString(); 
			
			
//Menu Head Data
		    
		    List<clsPOSMultiBillSettleInCashBean> list=objBean.getListUnsettleBillDtl();
		    List jArrList = new ArrayList<>();
		    for(int i=0; i<list.size(); i++)
		    {
		    	clsPOSMultiBillSettleInCashBean obj= new clsPOSMultiBillSettleInCashBean();
		    	obj=(clsPOSMultiBillSettleInCashBean)list.get(i);
		    	if (obj.getStrSelectedData() != null)
				  {
					  if (obj.getStrSelectedData().toString().equalsIgnoreCase("Tick"))
					  {
			    		Map mapData = new HashMap<>();
			    		
			    		mapData.put("BillNo",obj.getStrBillNo());
			    		mapData.put("dblSettleAmt",obj.getDblGrandTotal());
			    		String tableName=obj.getStrTableName();
				
						if(map.containsKey(tableName))
						{
						tableNo=(String) map.get(tableName);		
						}
	
						mapData.put("TableName", tableName);
			    		mapData.put("TableNo", tableNo);		
			    		mapData.put("GrandTotal",obj.getDblGrandTotal());
	
			    		jArrList.add(mapData);
		    	     }
				  }
		    }
		    Map mainMap = new HashMap<>();
		    mainMap.put("UnsettleBillDetails",jArrList );
		    mainMap.put("ClientCode", clientCode);
		    mainMap.put("POSDate", posDate);
		    mainMap.put("User", webStockUserCode);
		    mainMap.put("POSCode", posCode);
		    objTransactionService.funSettleBills(mainMap);

		    String output = "", op = "";
		    /*String posURL = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSTransactions/funSettleBills";
			URL url = new URL(posURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(mainMap.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
  
            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            */
            System.out.println("Result= " + op);
            //conn.disconnect();
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+op);
									
			return new ModelAndView("redirect:/frmPOSMultiBillSettle.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
}

