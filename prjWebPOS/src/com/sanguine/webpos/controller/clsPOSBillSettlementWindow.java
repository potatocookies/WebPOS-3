package com.sanguine.webpos.controller;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDetails;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSBillSeriesBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementDtl;
import com.sanguine.webpos.bean.clsPOSCustomerDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSDeliveryBoyMasterBean;
import com.sanguine.webpos.bean.clsPOSDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSSettlementDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSTaxCalculation;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.bean.clsPOSTaxDtlsOnBill;
import com.sanguine.webpos.model.clsBillDiscDtlModel;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillModifierDtlModel;
import com.sanguine.webpos.model.clsBillSettlementDtlModel;
import com.sanguine.webpos.model.clsBillTaxDtl;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSBillSettlementWindow {

	@Autowired
	private clsPOSGlobalFunctionsController	objPOSGlobalFunctionsController;
	@Autowired
	private clsGlobalFunctions				objGlobalFunctions;
	@Autowired
	private clsPOSSettlementGlobalController objSettlementGlobalController;
	public clsPOSBillDetails obBillItem;
	
	@Autowired
	private clsPOSDirectBillerController obPOSDirectBillerController;
	

	public List<clsPOSItemsDtlsInBill> listOfPunchedItemsDtl;
	private HashMap<String, clsPOSSettelementOptions> hmSettlemetnOptions = new HashMap<>();
	clsPOSBillSettlementBean obSettle;
	private List listItemCode;
	JSONArray listReasonCode,listReasonName;
	String clientCode="",posCode="",posDate="",userCode="";
	String customerCode="",customerName="",customerMobile="",billTransType="",operationTypeForTax="",customerType="",cmsMemberName="",advOrderBookingNo = "";
	private String  custAddType,billTypeForTax,billType,delPersonCode,custMobileNoForCRM,takeAway, selectedReasonCode="",discountRemarks="",takeAwayRemarks="";
	boolean flgMakeBill;
	double dblDiscountAmt = 0.00;
	double dblDiscountPer = 0.00, _grandTotal = 0.00;
	double _subTotal = 0.00;
	double _netAmount=0.00;
	double _grandTotalRoundOffBy = 0.00;
	double dblTotalTaxAmt;
	double dblDeliveryCharges=0.0;
	String voucherNo,tableNo,areaCode;
	 Map<String, List<clsPOSItemsDtlsInBill>> hmBillSeriesItemList;
	 Map<String, clsPOSItemsDtlsInBill> hmBillItemDtl = new HashMap<String, clsPOSItemsDtlsInBill>();
	 static String debitCardNo="",settleName="";
	 private List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl;
	 Map<String, clsPOSBillDtl> hmComplimentaryBillItemDtl = new HashMap<String, clsPOSBillDtl>();
	 private List<clsPOSTaxCalculationDtls> arrListTaxCal;
	 
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	@Autowired
	clsPOSUtilityController objUtility;
	
	@Autowired
	intfBaseService objBaseService;
	
	@RequestMapping(value="/frmPOSBillSettlement",method=RequestMethod.GET) 
	public ModelAndView funOpenForm(Model model1,Map<String,Object> model,HttpServletRequest request,@RequestParam("OperationFrom") String operationFrom)//,@RequestParam("listItemDetails") JSONObject listItemDetails)
	{
		
		
		System.out.println("OperationFrom  -"+operationFrom);
		
		obBillItem=objSettlementGlobalController.obBillItem;
		listOfPunchedItemsDtl=new ArrayList<clsPOSItemsDtlsInBill>();
		listOfPunchedItemsDtl=obBillItem.getListItemsDtlInBill(); //load list from Global Controller
		
		listItemCode=new ArrayList<String>();
		List listItemName = new ArrayList<>();
		if(listOfPunchedItemsDtl.size()>0){
			for(clsPOSItemsDtlsInBill obBean:listOfPunchedItemsDtl){
				if(obBean.getItemCode()!=null){
					listItemCode.add(obBean.getItemCode());
					listItemName.add(obBean.getItemName());	
				}
			}
		}
		
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
			customerMobile= request.getParameter("customerMobile").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		
		
		clientCode = request.getSession().getAttribute("clientCode").toString();
		posCode = request.getSession().getAttribute("loginPOS").toString();
		posDate=request.getSession().getAttribute("POSDate").toString().split(" ")[0];
		String userCode=request.getSession().getAttribute("usercode").toString();
		String usertype=request.getSession().getAttribute("usertype").toString();
		obSettle=new clsPOSBillSettlementBean();
		List listSettlementObject=new ArrayList<clsPOSSettelementOptions>();
		try{
			String posURL = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSTransactions/funGetSettleButtons"
					+ "?posCode="+ posCode+"&userCode="+userCode+"&clientCode="+clientCode;
			JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
			JSONArray jArr=new JSONArray();//(JSONArray)jObj.get("SettleDesc");
			Gson gson = new Gson();
			Type objectType = new TypeToken<clsPOSSettelementOptions>() {}.getType();
			JSONObject jsSettelementOptionsDtl =(JSONObject)jObj.get("SettleObj");
			
			 
	        Type listType = new TypeToken<List<clsPOSSettelementOptions>>() {}.getType();
	        listSettlementObject= gson.fromJson(jObj.get("listSettleObj").toString(), listType);
	     
	        JSONObject jsSettle= new JSONObject();
	        for(int j=0;j<listSettlementObject.size();j++){
	        	//if(jArr.size()==listSettlementObject.size())
	        		// jsSettle.put(jArr.get(j).toString(), listSettlementObject.get(j));
	        	jArr.add(listSettlementObject.get(j));
	        }
	        model.put("ObSettleObject", jsSettle);
			obSettle.setJsonArrForSettleButtons(jArr);
			obSettle.setListOfBillItemDtl(listOfPunchedItemsDtl);
			obSettle.setDteExpiryDate(posDate);
			
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		model.put("operationFrom", operationFrom);
		model.put("billDate", posDate);
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		model.put("gPopUpToApplyPromotionsOnBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("PopUpToApplyPromotionsOnBill"));
		model.put("gCreditCardSlipNo", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardSlipNoCompulsoryYN"));
		model.put("gCreditCardExpiryDate", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardExpiryDateCompulsoryYN"));
		
		
		funLoadAllReasonMasterData(request);
		List listDiscountCombo=new ArrayList<List>();
		List listSubGroupName = new ArrayList<>();
        List listSubGroupCode = new ArrayList<>();
        List listGroupName = new ArrayList<>();
        List listGroupCode = new ArrayList<>();
        listDiscountCombo=funLoadItemsGroupSubGroupData();
		if(listDiscountCombo.size()>0){
			listSubGroupName=(List)listDiscountCombo.get(0);
			listSubGroupCode=(List)listDiscountCombo.get(1);
			listGroupName=(List)listDiscountCombo.get(2);
			listGroupCode=(List)listDiscountCombo.get(3);
			
		}
		
		
		List list= obBillItem.getListItemsDtlInBill();
		model.put("listSubGroupName", listSubGroupName);
		model.put("listSubGroupCode", listSubGroupCode);
		model.put("listGroupCode", listGroupCode);
		model.put("listGroupName", listGroupName);
		model.put("listItemCode", listItemCode);
		model.put("listItemName", listItemName);
		model.put("listReasonCode", listReasonCode);
		model.put("listReasonName", listReasonName);
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("listOfBillItemDtl", json);
		StringBuilder sqlBuilder=new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select dblMaxDiscount ,strApplyDiscountOn from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
		List list1 = null;
		try {
			list1 = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		double gMaxDiscount = 0;
		String gApplyDiscountOn = "";
			if(list1!=null && list1.size()>0)
			{				
				Object[] obj=(Object[])list1.get(0);
				gMaxDiscount=Double.parseDouble(obj[0].toString());
				gApplyDiscountOn=obj[1].toString();
			}
		
			
			model1.addAttribute("gMaxDiscount", gMaxDiscount);
			model1.addAttribute("gApplyDiscountOn", gApplyDiscountOn);
		    model1.addAttribute("listOfBillItemDtlModel", json);
		
		return new ModelAndView("frmPOSBillSettlement","command",obSettle);
	}
	

	//send list of selected items to UI
	@RequestMapping(value = "/funTableLoadPunchedItemDetailData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSItemsDtlsInBill> funTableLoadPunchedItemDetailData(HttpServletRequest req,@RequestParam("applyPromotion")String applyPromotion)
	{

		if(obSettle.getListOfBillItemDtl()==null){
			listOfPunchedItemsDtl=obBillItem.getListItemsDtlInBill();			
		}
			// promotion Calculate 
		try
		{
			//funCalculatePromotion(applyPromotion);
			//funCalculateTax();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return listOfPunchedItemsDtl;
	}
	
//	@RequestMapping(value = "/funCalculateTaxInSettlement", method = RequestMethod.POST)
//	 private @ResponseBody List<clsTaxDtlsOnBill> funCalculateTax(HttpServletRequest req)
//	    {
//		 	List<clsTaxDtlsOnBill> listTaxDtlOnBill=new ArrayList<clsTaxDtlsOnBill>();
//		 	 	 if (billTransType.equalsIgnoreCase("Home Delivery"))
//	             {
//	                 operationTypeForTax = "HomeDelivery";
//	             }
//	             else if (obBillItem.getTakeAwayYN().equals("Yes"))
//	             {
//	                 operationTypeForTax = "TakeAway";
//	             }
//	             else
//	             {
//	                 operationTypeForTax = "DineIn";
//	             }
//	    		 
//	           
//	            //customerType=objDirectBillerBean.getStrCustomerType();
//	           
//	        
//	        
//	        JSONObject jsonSelectedItemDtl=new JSONObject();
//            JSONArray jArrClass = new JSONArray();
//            for(int cnt=0;cnt<listOfPunchedItemsDtl.size();cnt++)
//            {
//            	clsItemsDtlsInBill objItemsDtlsInBill=(clsItemsDtlsInBill) listOfPunchedItemsDtl.get(cnt);
//                JSONObject objRows=new JSONObject();
//                if(objItemsDtlsInBill.getItemCode()!=null)
//                {
//	                //objRows.put("strTableNo",objKOTItemDtl.getStrTableNo());
//	                objRows.put("strPOSCode",posCode);
//	                objRows.put("strItemCode",objItemsDtlsInBill.getItemCode());
//	                objRows.put("strItemName",objItemsDtlsInBill.getItemName());
//	                objRows.put("dblItemQuantity",objItemsDtlsInBill.getQuantity());
//	                objRows.put("dblAmount",objItemsDtlsInBill.getAmount());
//	                //  objRows.put("dblRate",objKOTItemDtl.getDblRate());
//	                objRows.put("strClientCode",clientCode);
//	                objRows.put("OperationType",operationTypeForTax);//operationTypeFor Tax
//	                objRows.put("AreaCode","");
//	                objRows.put("POSDate",posDate);
//	                
//	                jArrClass.add(objRows);
//                }
//
//            }
//            jsonSelectedItemDtl.put("TaxDtl", jArrClass);
//
//            //call WebService
//            String strUrl=clsPOSGlobalFunctionsController.POSWSURL+"/APOSIntegration/funCalculateTax";
//            JSONObject jObj =  funCalculateTax(jsonSelectedItemDtl);
//    		JSONArray jArrTaxList=(JSONArray)jObj.get("listOfTax");
//    		String totalTaxAmt=jObj.get("totalTaxAmt").toString();
//    		clsTaxDtlsOnBill objTaxDtl;
//    		JSONObject jsonTax= new JSONObject();
//    		for(int i=0;i<jArrTaxList.size();i++){
//    			objTaxDtl=new clsTaxDtlsOnBill();
//    			jsonTax=(JSONObject)jArrTaxList.get(i);
//    			objTaxDtl.setTaxName(jsonTax.get("TaxName").toString());
//    			objTaxDtl.setTaxAmount(Double.parseDouble(jsonTax.get("TaxAmt").toString()));
//    			objTaxDtl.setTaxCode(jsonTax.get("taxCode").toString());
//    			objTaxDtl.setTaxCalculationType(jsonTax.get("taxCalculationType").toString());
//    			objTaxDtl.setTaxableAmount(Double.parseDouble(jsonTax.get("taxableAmount").toString()));
//            	
//    			listTaxDtlOnBill.add(objTaxDtl);
//    	
//    		}
//           
//            return listTaxDtlOnBill;
//    }

	 /*@RequestMapping(value = "/funLoadPopupPromotionOnBill", method = RequestMethod.GET)
		public @ResponseBody String funLoadPopupPromotionOnBill(HttpServletRequest req)
		{
			String url=clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSBillSettlement/funPopupPromotionOnBill?strClientCode="+clientCode+"&posCode="+posCode;
			JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(url);
			String popupOnPromotion=jObj.get("POPUPWindow").toString();
			return popupOnPromotion;
		}*/
	 
	 public void funCalculatePromotion(String applyPromotion)
	 {
		 	/*customerCode=objDirectBillerBean.getStrCustomerCode();
			customerName=objDirectBillerBean.getCustomerName();
			billTransType=objDirectBillerBean.getBillTransType();
			customerType=objDirectBillerBean.getStrCustomerType();*/
	        JSONObject objPromoItemDtl=new JSONObject();
	        JSONArray arrPromoItems = new JSONArray();
		 try{

				for(clsPOSItemsDtlsInBill obBillItem: listOfPunchedItemsDtl){
					 JSONObject objRows=new JSONObject();

			         objRows.put("itemCode",obBillItem.getItemCode());
			         objRows.put("itemName",obBillItem.getItemName());
			         objRows.put("quantity",obBillItem.getQuantity());
			         objRows.put("amount",obBillItem.getAmount());
			         arrPromoItems.add(objRows);
				}
				
				 objPromoItemDtl.put("PromotionDtl", arrPromoItems);
	             objPromoItemDtl.put("POSCode",posCode);
	             objPromoItemDtl.put("TableNo", "");
	             objPromoItemDtl.put("POSDate", posDate);
	             
	             String strUrl = clsPOSGlobalFunctionsController.POSWSURL+"/APOSIntegration/funCalculatePromotion";
	     		 
	               URL url = new URL(strUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setDoOutput(true);
		            conn.setRequestMethod("POST");
		            conn.setRequestProperty("Content-Type", "application/json");
		            OutputStream os = conn.getOutputStream();
		            os.write(objPromoItemDtl.toString().getBytes());
		            os.flush();
		            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
		            {
		                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		            }
		            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		            String output = "", op = "";

		            while ((output = br.readLine()) != null)
		            {
		                op += output;
		            }
		            System.out.println("Result= " + op);
		            
		            //promoItemWithAmtAndQty=itemCode+"#"+itemName+"#"+String.valueOf(amount)+"#"+String.valueOf(quantity)+"#"+promoCode+"#"+promoType+"#"+String.valueOf(promoFreeAmt)+"#"+String.valueOf(promoFreeQty);	            
		            if(!op.isEmpty())
		            {
		            	List<clsPOSBillItemDtlBean> listOfDirectBillerPromotionItem=new ArrayList<clsPOSBillItemDtlBean>();
		                String[]items=op.split("!");
		                for(int cnt=0;cnt<items.length;cnt++)
		                {
		                    System.out.println("Items:-" + items[cnt]);
		                    String[]subItems=items[cnt].split("#");
		                    if(!subItems[4].isEmpty())
		                    {
		                    	clsPOSBillItemDtlBean objPromoItemDtls=new clsPOSBillItemDtlBean();
		                        objPromoItemDtls.setStrItemCode(subItems[0]);
		                        objPromoItemDtls.setPromoCode(subItems[4]);
		                        objPromoItemDtls.setPromoType(subItems[5]);
		                        objPromoItemDtls.setBillAmt(Double.valueOf(subItems[6]));
		                        objPromoItemDtls.setFreeItemQty(Double.valueOf(subItems[7]));
		                        listOfDirectBillerPromotionItem.add(objPromoItemDtls);
		                    }
		                    //double qty=1.9;

		                 }
		                if(listOfDirectBillerPromotionItem.size()>0)
		                {
		                    //funSetListFromDirectBiller(arrListItemDtl);
		                    if(applyPromotion.equals("true"))
		                    {
		                              
		                    	listOfPunchedItemsDtl.clear();
	                            String[]itemsArr=op.split("!");
	                            for(int cnt=0;cnt<items.length;cnt++)
	                            {
	                                System.out.println("Items:-" + itemsArr[cnt]);
	                                String[]subItems=itemsArr[cnt].split("#");
	                                String[] arr=subItems[3].split("\\.");
	                                double amt=Double.parseDouble(subItems[2]);
	                              //  clsItemsDtlsInBill objDb= new clsItemsDtlsInBill(subItems[0], subItems[1],Integer.parseInt(arr[0]), amt, amt, true,customerCode,customerType,billTransType,"N");
	                               // listOfPunchedItemsDtl.add(objDb);
	                            }

	                            clsPOSItemsDtlsInBill obj;
	                            for (int cnt = 0; cnt < listOfPunchedItemsDtl.size(); cnt++)
	                            {
	                                obj = (clsPOSItemsDtlsInBill) listOfPunchedItemsDtl.get(0);
	                                //customerCode = obj.getStrCustomerCode();
	                               // customerName = obj.getCustomerName();
	                               // operationType = obj.getStrOperationType();
	                                if(billTransType.equals("TakeAway"))
	                                {
	                                    operationTypeForTax="TakeAway";
	                                }
	                                else if(billTransType.equals("DirectBiller"))
	                                {
	                                    operationTypeForTax="DineIn";
	                                }
	                                else
	                                {
	                                    operationTypeForTax="HomeDelivery";
	                                }
	                                //customerType=obj.geustomerType();
	                            }

	                          //  funCalculateTax();

	                        

		                      
		                        
		                    }
		                    else
		                    {
		                       // funCalculateTax();
		                    }
		                }
		                else
		                {
		                //	funCalculateTax();
		                	//return listOfDirectBillerBillItemDtl;

		                }


		            }
		            
		            
		            
			
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 
	public void funLoadAllReasonMasterData(HttpServletRequest request)
	{
		Map<String,String> mapModBill=new HashMap<String, String>();
		Map<String,String> mapComplementry=new HashMap<String, String>();
		Map<String,String> mapDiscount=new HashMap<String, String>();
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		String posURl=clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSTransactions/funLoadAllReasonMasterData"
				+ "?clientCode="+clientCode;
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURl);
		if(jObj!=null){
			JSONObject JObjBill=new JSONObject();
//	    	JSONObject JObjComplementry=new JSONObject();
//	    	JSONObject JObjDiscount=new JSONObject();
	    	JSONArray jArr=new JSONArray();
	    	
	    	jArr=(JSONArray)jObj.get("ModifyBill");
	    	if(jArr!=null){
	    		for(int i=0;i<jArr.size();i++){
		    		JObjBill=(JSONObject)jArr.get(i);
		    		mapModBill.put(JObjBill.get("strReasonCode").toString(),JObjBill.get("strReasonName").toString() );
		    	}
	    	}
	    	
	    	jArr=new JSONArray();
	    	if(jArr!=null){
			 	jArr=(JSONArray)jObj.get("Complementry");
		    	for(int i=0;i<jArr.size();i++){
		    		JObjBill=(JSONObject)jArr.get(i);
		    		mapComplementry.put(JObjBill.get("strReasonCode").toString(),JObjBill.get("strReasonName").toString() );
		    	}
	    	}
	    	jArr=new JSONArray();
	    	if(jArr!=null){
			    jArr=(JSONArray)jObj.get("Discount");
		    	for(int i=0;i<jArr.size();i++){
		    		JObjBill=(JSONObject)jArr.get(i);
		    		mapDiscount.put(JObjBill.get("strReasonCode").toString(),JObjBill.get("strReasonName").toString() );
		    	}
	    	}
	    	listReasonCode=new JSONArray();
	    	listReasonName=new JSONArray();
	    	
	    	jArr=new JSONArray();
	    	if(jArr!=null){
			    jArr=(JSONArray)jObj.get("AllReason");
		    	for(int i=0;i<jArr.size();i++){
		    		JObjBill=(JSONObject)jArr.get(i);
		    		listReasonCode.add(JObjBill.get("strReasonCode"));
		    		listReasonName.add(JObjBill.get("strReasonName"));
		    	}
	    	}
		}
	}
	
	
	public List funLoadItemsGroupSubGroupData()
	{
		List listDiscountCombo=new ArrayList<List>();
		try{
			JSONObject jsonItemListData=new JSONObject();
			Gson gson = new Gson();
	 	    Type type = new TypeToken<List<String>>() {}.getType();
            String alItemCode = gson.toJson(listItemCode, type);
            jsonItemListData.put("ItemCodeList", alItemCode);

            
            String posURl=clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSBillSettlement/funLoadItemsGroupSubGroupData";
    				
    		JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posURl,jsonItemListData);
    		if(jObj!=null)
    		{
    			List listSubGroupName = new ArrayList<>();
	            List listSubGroupCode = new ArrayList<>();
	            List listGroupName = new ArrayList<>();
	            List listGroupCode = new ArrayList<>();
	    		gson = new Gson();
				Type listType = new TypeToken<List<String>>() {}.getType();
				listSubGroupName= gson.fromJson(jObj.get("listSubGroupName").toString(), listType);
				listSubGroupCode= gson.fromJson(jObj.get("listSubGroupCode").toString(), listType);
				listGroupName= gson.fromJson(jObj.get("listGroupName").toString(), listType);
				listGroupCode= gson.fromJson(jObj.get("listGroupCode").toString(), listType);
	
				listDiscountCombo.add(listSubGroupName);
				listDiscountCombo.add(listSubGroupCode);
				listDiscountCombo.add(listGroupName);
				listDiscountCombo.add(listGroupCode);
    		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return listDiscountCombo;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/funMapOfSettlement", method = RequestMethod.POST)
	public @ResponseBody String funLoadMapOfSettlement(HttpServletRequest req)
	{
		//Object str=req.getAttribute("CASH");
		//Map<String,Object> map
		
		String settleName=req.getParameter("settleName");
		String settlementCode=req.getParameter("settlementCode");
		double dblSettlementAmount=Double.parseDouble(req.getParameter("dblSettlementAmount"));
		double paidAmount=Double.parseDouble(req.getParameter("paidAmount"));
		double grandTotal=Double.parseDouble(req.getParameter("grandTotal"));
		double refundAmount=Double.parseDouble(req.getParameter("refundAmount"));
		String settelmentDesc=req.getParameter("settelmentDesc");
		String settleType=req.getParameter("settleType");
		String expiryDate=req.getParameter("expiryDate");
		String cardName=req.getParameter("cardName");
		String remark=req.getParameter("remark");
		String giftVoch=req.getParameter("giftVoch");
		
		if(settleType.equals("Room"))// for adding extra variables
		{
			String txtFolioNo,txtRoomNo,txtGuestCode; // here retrive variable values in jsp parameters  
			txtFolioNo=expiryDate; 
			txtRoomNo=cardName;
			txtGuestCode=giftVoch;
			clsPOSSettelementOptions objSettleOpt = new clsPOSSettelementOptions(settlementCode, dblSettlementAmount, paidAmount, "", settleName, "", "", grandTotal, refundAmount, "",  settelmentDesc, settleType);
			objSettleOpt.setStrFolioNo(txtFolioNo);
			objSettleOpt.setStrRoomNo(txtRoomNo);
			objSettleOpt.setStrGuestCode(txtGuestCode);
			hmSettlemetnOptions.put(settleName, objSettleOpt);
		}
		else
		{
			hmSettlemetnOptions.put(settleName,  new clsPOSSettelementOptions(settlementCode, dblSettlementAmount, paidAmount, expiryDate, settleName, cardName, remark, grandTotal, refundAmount, giftVoch, settelmentDesc, settleType));	
		}
		  
		return "yes";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value ="/funCheckPointsAgainstCustomer",method =RequestMethod.POST)
	public @ResponseBody JSONObject funCheckPointsAgainstCustomer(HttpServletRequest req,@RequestParam("CRMInterface") String CRMInterface,@RequestParam("customerMobile") String customerMobile,@RequestParam("voucherNo") String voucherNo,@RequestParam("txtPaidAmt") String txtPaidAmt)
	{
		String checkPoints = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSBillSettlement/funCheckPointsAgainstCustomer"
				+ "?posCode="+ posCode+"&clientCode="+clientCode+"&CRMInterface="+CRMInterface+"&customerMobile="+customerMobile+"&voucherNo="+voucherNo+"&txtPaidAmt="+txtPaidAmt;
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(checkPoints);
		
		 double totalLoyalityPoints = 0.00, totalReedemedPoints = 0.00,loyalityPoints=0;
		 totalLoyalityPoints=Double.parseDouble(jObj.get("totalLoyalityPoints").toString());
		 totalReedemedPoints=Double.parseDouble(jObj.get("totalReedemedPoints").toString());
		// loyalityPoints=Double.parseDouble(jObj.get("loyalityPoints").toString());
		 
		return jObj;
	}
	

	@SuppressWarnings("rawtypes")
	@RequestMapping(value ="/funGetDebitCardNo",method =RequestMethod.POST)
	public @ResponseBody JSONObject funGetDebitCardNo(HttpServletRequest req,@RequestParam("voucherNo") String voucherNo,@RequestParam("tableNo") String tableNo)
	{
		String debitCardNo = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSBillSettlement/funGetDebitCardNo"
				+ "?posCode="+ posCode+"&clientCode="+clientCode+"&voucherNo="+voucherNo+"&tableNo="+tableNo;
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(debitCardNo);
		
			 
		return jObj;
	}
	/*
	 	@SuppressWarnings("rawtypes")
		@RequestMapping(value = "/charTransectionData", method = RequestMethod.POST,headers = {"Content-type=application/json"})
		public @ResponseBody String funLoadCharBean(@RequestBody Object obj, HttpServletRequest request)
		{
		 	@SuppressWarnings("unused")
			List<clsTransectionProdCharModel> listTransProdChar =new ArrayList<clsTransectionProdCharModel>();
		 
		 
		 return null;
		}*/
	
	
	
	
	@RequestMapping(value ="/actionBillSettlement11",method=RequestMethod.POST)
	public void printBill (@ModelAttribute("command")  clsPOSBillSettlementBean objBean, HttpServletRequest req  ) throws Exception
	{
		
		System.out.println("printBill");
		
		if(objBean!=null){
			System.out.println("printBill");
		}
		String strButtonClicked = "Print";
		 String posDate = req.getSession().getAttribute("POSDate").toString();
		
		  posCode=req.getSession().getAttribute("loginPOS").toString();
		  clientCode=req.getSession().getAttribute("clientCode").toString();
		  userCode=req.getSession().getAttribute("usercode").toString();
//Customer Detail 
        List <clsPOSCustomerDtlsOnBill> listCustomerDtlsOnBill  =obBillItem.getListCustomerDtlOnBill();
    	clsPOSCustomerDtlsOnBill objCustomerDtlsOnBill=listCustomerDtlsOnBill.get(0);
//Delivery Boy Detail
		List<clsPOSDeliveryBoyMasterBean> listDeliveryBoy=obBillItem.getListDeliveryBoyMasterBeanl();
		clsPOSDeliveryBoyMasterBean objDeliveryBoyBean=listDeliveryBoy.get(0);
		 StringBuilder sqlBuilder=new StringBuilder();
    	 String mannualBillNo=objBean.getStrManualBillNo();
    	 
    	 dblDiscountAmt =objBean.getDblDiscountAmt();
    	 dblDiscountPer = objBean.getDblDiscountPer() ;
    	_grandTotal = objBean.getDblGrandTotal();
    	_subTotal = objBean.getDblSubTotal();
//    	_grandTotalRoundOffBy = ;
    	 dblTotalTaxAmt=dblTotalTaxAmt;
    	 dblDeliveryCharges=objBean.getDblDeliveryCharges();
		
	    if ("Direct Biller".equalsIgnoreCase(obBillItem.getBillType().toString()))
        {
//            if (_grandTotal == 0 && hmComplimentaryBillItemDtl.size() > 0)
//            {
//                btnSettle.setEnabled(false);
//                JOptionPane.showMessageDialog(null, "Grand total is 0, Close the form and Use Complimetary Settlement type!!!");
//                return;
//            }
	    
	    	if ("ModifyBill".equalsIgnoreCase(objCustomerDtlsOnBill.getBillTransType()))
            {
              ///////////  dispose();
                new Thread()
                {
                    @Override
                    public void run()
                    {
//                        funModifyBill();
                    }
                }.start();
            }
            else
            {
                if (null != objCustomerDtlsOnBill.getCustomerCode())
                {
                	customerCode = objCustomerDtlsOnBill.getCustomerCode();
                }
                
                
                sqlBuilder.setLength(0);
			  	sqlBuilder.append("select strCMSIntegrationYN  from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
				List list1=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
				 String gCMSIntegrationYN="";
				
				if(list1!=null && list1.size()>0)
				{				
					Object obj=(Object)list1.get(0);
					gCMSIntegrationYN=obj.toString();
					
				}
                if (gCMSIntegrationYN.equalsIgnoreCase("Y"))
                {
                    customerCode = "";////////////////////////objDirectBiller.getCmsMemberCode();
                    cmsMemberName = "";////////////////////////////////////objDirectBiller.getCmsMemberName();
                }

                delPersonCode =objDeliveryBoyBean.getStrDPCode();
                custMobileNoForCRM = "";
                custMobileNoForCRM = objCustomerDtlsOnBill.getCustMobileNo();
//                if (clsGlobalVarClass.gAdvOrderNoForBilling != null)
//                {
//                    advOrderBookingNo = clsGlobalVarClass.gAdvOrderNoForBilling;
//                }
                takeAway = "No";
                if (obBillItem.getTakeAwayYN().equals("Yes"))
                {
                    takeAway = "Yes";
                }
//                objDirectBiller.funResetFields();
//                dispose();
                 double deliveryCharges=objBean.getDblDeliveryCharges();
//                new Thread()
//                {
//                    @Override
//                    public void run()
//                    {
                    	
                        funSaveBillForDBWithOutSettle(takeAway,objBean);
//                    }
//                }.start();
            }
        }
	}
	
//	@RequestMapping(value ="/actionBillSettlement",params={"settleBill"},method=RequestMethod.POST)
//	public void settleBill (@ModelAttribute("command") @Valid clsPOSBillSettlementBean objBean,BindingResult result,HttpServletRequest req,@RequestParam String settleBill)
//	{
//		
//		System.out.println("settleBill");
//		
//		if(objBean!=null){
//			System.out.println("settleBill");
//		}
//		
//
//	}
    private void funSaveBillForDBWithOutSettle(String takeAwayYN, clsPOSBillSettlementBean objBean)
    {
        boolean flgSettle = false;
        try
         {
            //Bill series code 
        	String mannualBillNo=objBean.getStrManualBillNo();
        	double deliveryCharges=objBean.getDblDeliveryCharges();
            Map<String, List<clsPOSItemsDtlsInBill>> mapBillSeries = null;
            
            StringBuilder sqlBuilder=new StringBuilder();
            sqlBuilder.setLength(0);
		  	sqlBuilder.append("select strEnableBillSeries  from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			List list1=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			
			String gEnableBillSeries="";
			
			if(list1!=null && list1.size()>0)
			{				
				Object obj=(Object)list1.get(0);
				gEnableBillSeries=obj.toString();
				
			}
            listBillSeriesBillDtl = new ArrayList<>();
            if (gEnableBillSeries.equals("Y")&& (mapBillSeries = funGetBillSeriesList(objBean.getListOfBillItemDtl())).size() > 0)
            {
                if (mapBillSeries.containsKey("NoBillSeries"))
                {
//                    new frmOkPopUp(null, "Please Create Bill Series", "Bill Series Error", 1).setVisible(true);
//                    return;
                }
                Iterator<Map.Entry<String, List<clsPOSItemsDtlsInBill>>> billSeriesIt = mapBillSeries.entrySet().iterator();
                while (billSeriesIt.hasNext())
                {
                    Map.Entry<String, List<clsPOSItemsDtlsInBill>> billSeriesEntry = billSeriesIt.next();
                    String key = billSeriesEntry.getKey();
                    List<clsPOSItemsDtlsInBill> values = billSeriesEntry.getValue();

                  funGenerateBillNoForBillSeriesForDirectBiller(key, values,objBean, posDate,  posCode, clientCode, userCode);
                }
                //save bill series bill detail
          /*      for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
                {
                    clsBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
                    String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
                    double grandTotal = objBillSeriesBillDtl.getDblGrandTotal();

                    String sqlInsertBillSeriesDtl = "insert into tblbillseriesbilldtl "
                            + "(strPOSCode,strBillSeries,strHdBillNo,strDtlBillNos,dblGrandTotal,strClientCode,strDataPostFlag"
                            + ",strUserCreated,dteCreatedDate,strUserEdited,dteEditedDate,dteBillDate) "
                            + "values ('" + clsGlobalVarClass.gPOSCode + "','" + objBillSeriesBillDtl.getStrBillSeries() + "'"
                            + ",'" + hdBillNo + "','" + funGetBillSeriesDtlBillNos(listBillSeriesBillDtl, hdBillNo) + "'"
                            + ",'" + grandTotal + "'" + ",'" + clsGlobalVarClass.gClientCode + "','N','" + clsGlobalVarClass.gUserCode + "'"
                            + ",'" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.gUserCode + "'"
                            + ",'" + clsGlobalVarClass.getCurrentDateTime() + "','" + objUtility.funGetPOSDateForTransaction() + "')";
                    clsGlobalVarClass.dbMysql.execute(sqlInsertBillSeriesDtl);

                    String sql = "select * "
                            + "from tblbillcomplementrydtl a "
                            + "where a.strBillNo='" + hdBillNo + "' "
                            + "and date(a.dteBillDate)='" + clsGlobalVarClass.gPOSOnlyDateForTransaction + "' "
                            + "and a.strType='Complimentary'; ";
                    ResultSet rsIsComplementary = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                    if (rsIsComplementary.next())
                    {
                        String sqlUpdate = "update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + hdBillNo + "' "
                                + " and strPOSCode='" + clsGlobalVarClass.gPOSCode + "' "
                                + " and date(dteBillDate)='" + clsGlobalVarClass.gPOSOnlyDateForTransaction + "' ";
                        clsGlobalVarClass.dbMysql.execute(sqlUpdate);
                    }
                    rsIsComplementary.close();
               

                for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
                {
                    clsBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
                    String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
                    funSendBillToPrint(hdBillNo, objUtility.funGetOnlyPOSDateForTransaction());
                } }*/
            }
            else//if no bill series
            {
                String strCustomerCode = "";

                if (null != customerCode)
                {
                    strCustomerCode = customerCode;
                }
                String operationType = "DirectBiller";
                String transactionType = "Direct Biller";//For saving different transaction on same Bill in tblBillHd table in database
                funGenerateBillNo();
                clsBillHdModel objBillHd = new clsBillHdModel();
                //last order no
                int intLastOrderNo = objUtility.funGetLastOrderNo();
                Date dt=new Date();
    		    String currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
    		    String dateTime=posDate+" "+currentDateTime.split(" ")[1];
    		    List <clsBillDiscDtlModel>listBillDiscDtlModel= funSaveBillDiscountDetail(voucherNo,objBean,dateTime);
    		    objBillHd.setListBillDiscDtlModel(listBillDiscDtlModel);
                if (customerCode != null)
                {
                    strCustomerCode = customerCode;
//                    if (homeDelivery.equals("Y"))
//                    {
//                        operationType = "HomeDelivery";
//                        transactionType = transactionType + "," + operationType;
//                        Calendar c = Calendar.getInstance();
//                        int hh = c.get(Calendar.HOUR);
//                        int mm = c.get(Calendar.MINUTE);
//                        int ss = c.get(Calendar.SECOND);
//                        int ap = c.get(Calendar.AM_PM);
//                        String ampm = "AM";
//                        if (ap == 1)
//                        {
//                            ampm = "PM";
//                        }
//                        String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;
//
//                        if (delPersonCode != null)
//                        {
//                            String sql_tblhomedelivery = "insert into tblhomedelivery(strBillNo,strCustomerCode"
//                                    + ",strDPCode,dteDate,tmeTime,strPOSCode,strCustAddressLine1,strClientCode,dblHomeDeliCharge) "
//                                    + "values('" + voucherNo + "','" + custCode + "'"
//                                    + ",'" + delPersonCode + "','" + clsGlobalVarClass.gPOSDateForTransaction + "'"
//                                    + ",'" + currentTime + "','" + clsGlobalVarClass.gPOSCode + "','" + custAddType + "'"
//                                    + ",'" + clsGlobalVarClass.gClientCode + "'," + _deliveryCharge + ")";
//                            clsGlobalVarClass.dbMysql.execute(sql_tblhomedelivery);
//                        }
//                        else
//                        {
//                            String sql_tblhomedelivery = "insert into tblhomedelivery(strBillNo,strCustomerCode,dteDate,tmeTime"
//                                    + ",strPOSCode,strCustAddressLine1,strCustAddressLine2,strCustAddressLine3,strCustAddressLine4"
//                                    + ",strCustCity,strClientCode,dblHomeDeliCharge)"
//                                    + " values('" + voucherNo + "','" + custCode + "','"
//                                    + clsGlobalVarClass.gPOSDateForTransaction + "','" + currentTime + "','"
//                                    + clsGlobalVarClass.gPOSCode + "','" + custAddType + "','',''"
//                                    + ",'','','" + clsGlobalVarClass.gClientCode + "'," + _deliveryCharge + ")";
//                            clsGlobalVarClass.dbMysql.execute(sql_tblhomedelivery);
//                        }
//                    }
                }

                if (takeAwayYN.equals("Yes"))
                {
                    operationType = "TakeAway";
                    transactionType = "Direct Biller" + "," + operationType;
                }
                if (takeAway.equals("Yes"))
                {
                    operationType = "TakeAway";
                    transactionType = "Direct Biller" + "," + operationType;
                }

                String counterCode = "NA";
      /*          if (clsGlobalVarClass.gCounterWise.equals("Yes"))
                {
                    if (null != clsGlobalVarClass.gCounterCode)
                    {
                        counterCode = clsGlobalVarClass.gCounterCode;
                    }
                }*/
                double homeDeliveryCharges = 0.00;
                if (deliveryCharges > 0)
                {
                    homeDeliveryCharges =deliveryCharges;
                }
                String waiterNo = "NA";
                if (advOrderBookingNo.trim().length() > 0)
                {
                	sqlBuilder.setLength(0);
        		  	sqlBuilder.append("select strCustomerCode,ifnull(strWaiterNo,'NA') from tbladvbookbillhd "
                            + "where strAdvBookingNo='" + advOrderBookingNo + "'");
                   
        			List listAdvOrder=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
        			
        			
        			
        			if(listAdvOrder!=null && listAdvOrder.size()>0)
        			{				
        				   Object[] obj=(Object[])listAdvOrder.get(0);
        				   strCustomerCode = obj[0].toString();
                           waiterNo = obj[1].toString();
        				
        			}
        		
                }
            	 
                //Insert into tblbillhd table
                
                objBillHd.setStrBillNo(voucherNo);
                objBillHd.setStrAdvBookingNo(advOrderBookingNo);
                objBillHd.setDteBillDate(posDate);
                objBillHd.setStrPOSCode(posCode);
                objBillHd.setStrSettelmentMode("");
                objBillHd.setDblDiscountAmt(dblDiscountAmt);
                objBillHd.setDblDiscountPer(dblDiscountPer);
                objBillHd.setDblTaxAmt(dblTotalTaxAmt);
                objBillHd.setDblSubTotal(_subTotal);
                objBillHd.setDblGrandTotal(_grandTotal);
//        ///////////        objBillHd.setDblGrandTotalRoundOffBy(_grandTotalRoundOffBy);
                objBillHd.setStrTakeAway(takeAway);
                objBillHd.setStrOperationType(operationType);
                objBillHd.setStrUserCreated(userCode);
                objBillHd.setStrUserEdited(userCode);
                objBillHd.setDteDateCreated(currentDateTime);
                objBillHd.setDteDateEdited(currentDateTime);
                objBillHd.setStrClientCode(clientCode);
                objBillHd.setStrTableNo("");
                objBillHd.setStrWaiterNo(waiterNo);
                objBillHd.setStrCustomerCode(strCustomerCode);
                objBillHd.setStrManualBillNo(mannualBillNo);
                objBillHd.setIntShiftCode(0);///////////////////////////
                objBillHd.setIntPaxNo(0);
                objBillHd.setStrDataPostFlag("N");
                objBillHd.setStrReasonCode(selectedReasonCode);
                objBillHd.setStrRemarks("");
                objBillHd.setDblTipAmount(0.0);
                objBillHd.setDteSettleDate(posDate);
                objBillHd.setStrCounterCode(counterCode);
                objBillHd.setDblDeliveryCharges(deliveryCharges);
                objBillHd.setStrAreaCode(obBillItem.getAreaCode());
                objBillHd.setStrDiscountRemark(discountRemarks);
                objBillHd.setStrTakeAwayRemarks(takeAwayRemarks);
                objBillHd.setStrTransactionType(transactionType);
                objBillHd.setIntOrderNo (intLastOrderNo);
                objBillHd.setStrCouponCode("");
                objBillHd.setStrJioMoneyRRefNo("");
                objBillHd.setStrJioMoneyAuthCode("");
                objBillHd.setStrJioMoneyTxnId("");
                objBillHd.setStrJioMoneyTxnDateTime("");
                objBillHd.setStrJioMoneyCardNo("");
                objBillHd.setStrJioMoneyCardType("");
                objBillHd.setDblRoundOff(0.00);
                objBillHd.setIntBillSeriesPaxNo(0);
                objBillHd.setDtBillDate(posDate);
                objBillHd.setIntOrderNo(0);
               
                
                String discountOn = "";
	            String chckDiscounton=objBean.getStrDisountOn();
	            if(chckDiscounton!=null){
	            if (chckDiscounton.equals("Total"))
	            {
	                discountOn = "All";
	            }
	            if (chckDiscounton.equals("item"))
	            {
	                discountOn = "Item";
	            }
	            if (chckDiscounton.equals("group"))
	            {
	                discountOn = "Group";
	            }
	            if (chckDiscounton.equals("subGroup"))
	            {
	                discountOn = "SubGroup";
	            }
	            }
                objBillHd.setStrDiscountOn(discountOn);
                objBillHd.setStrCardNo(debitCardNo);
              
//                funInsertBillHdTable(objBillHd);
                sqlBuilder.setLength(0);
			  	sqlBuilder.append("select strCMSIntegrationYN  from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
				List listCMS=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
				 String gCMSIntegrationYN="";
				
				if(listCMS!=null && listCMS.size()>0)
				{				
					Object obj=(Object)listCMS.get(0);
					gCMSIntegrationYN=obj.toString();
					
				}
                if (gCMSIntegrationYN.equals("Y"))
                {
                    if (customerCode.trim().length() > 0)
                    {
                        String sqlDeleteCustomer = "delete from tblcustomermaster where strCustomerCode='" + customerCode + "' "
                                + "and strClientCode='" + clientCode + "'";
                        objBaseServiceImpl.funExecuteUpdate(sqlDeleteCustomer, "sql")  ;

                        String sqlInsertCustomer = "insert into tblcustomermaster (strCustomerCode,strCustomerName,strUserCreated"
                                + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) "
                                + "values('" + customerCode + "','" + cmsMemberName + "','" +userCode + "','" + userCode + "'"
                                + ",'" + currentDateTime + "','" + currentDateTime + "'"
                                + ",'" + clientCode + "')";
                        objBaseServiceImpl.funExecuteUpdate(sqlInsertCustomer, "sql")  ;
                    }
                }

                // Insert into tblbilldtl table
                List<clsBillDtlModel> listObjBillDtl = new ArrayList<clsBillDtlModel>();
//                clsItemsDtlsInBill objItemsDtlsInBill =obBillItem.getListItemsDtlInBill();
                for (clsPOSItemsDtlsInBill listclsItemRow : objBean.getListOfBillItemDtl())
                {
                	if(!(listclsItemRow.getItemCode().length()>0)||listclsItemRow.getItemCode().equals(""))
                	{
                		break;
                	}
                    if (!listclsItemRow.isModifier())
                    {
                        double rate = 0.00;
                        if (listclsItemRow.getQuantity() == 0)
                        {
                            rate = listclsItemRow.getRate();
                        }
                        else
                        {
                            rate = listclsItemRow.getAmount() / listclsItemRow.getQuantity();
                        }

                        clsBillDtlModel objBillDtl = new clsBillDtlModel();
                        objBillDtl.setStrItemCode(listclsItemRow.getItemCode());
                        objBillDtl.setStrItemName(listclsItemRow.getItemName());
                        objBillDtl.setStrAdvBookingNo("");
//                        objBillDtl.setStrBillNo(voucherNo);
                        objBillDtl.setDblRate(rate);
                        objBillDtl.setDblQuantity(listclsItemRow.getQuantity());
                        objBillDtl.setDblAmount(listclsItemRow.getAmount());
                        objBillDtl.setDblTaxAmount(0);
                        objBillDtl.setDteBillDate(posDate);
                        objBillDtl.setStrKOTNo("");
//                        objBillDtl.setStrClientCode(clientCode);
                        objBillDtl.setStrCounterCode(strCustomerCode);
                        objBillDtl.setTmeOrderProcessing("00:00:00");
                        objBillDtl.setStrDataPostFlag("N");
                        objBillDtl.setStrMMSDataPostFlag("N");
                        objBillDtl.setStrManualKOTNo("");
                        boolean tdYN=listclsItemRow.isTdhYN();
                        if(tdYN)
                        {
                        	  objBillDtl.setTdhYN("Y");	
                        }else{
                        	  objBillDtl.setTdhYN("N");
                        }
                      
                        objBillDtl.setStrPromoCode(listclsItemRow.getPromoCode());
                        objBillDtl.setStrCounterCode(counterCode);
                        objBillDtl.setStrWaiterNo(waiterNo);
                        objBillDtl.setSequenceNo(listclsItemRow.getSeqNo());
                        objBillDtl.setTmeOrderPickup("00:00:00");
                        
                      
                        objBillDtl.setDblDiscountAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
                        objBillDtl.setDblDiscountPer(listclsItemRow.getDiscountPer());
                        listObjBillDtl.add(objBillDtl);
                        // objBaseService.funSave(objBillDtl);
                        
                    }
                }
                	objBillHd.setListBillDtlModel(listObjBillDtl);
                
//                funInsertBillDtlTable(listObjBillDtl);

                // Insert into tblbillmodifierdtl
                List<clsBillModifierDtlModel> listObjBillModBillDtls = new ArrayList<clsBillModifierDtlModel>();
                for (clsPOSItemsDtlsInBill listclsItemRow : objBean.getListOfBillItemDtl() )
                {
                	if(!(listclsItemRow.getItemCode().length()>0)||listclsItemRow.getItemCode().equals(""))
                	{
                		break;
                	}
                    double rate = listclsItemRow.getAmount()/listclsItemRow.getQuantity();
                    double amt = 0.00;
                    boolean flgComplimentaryBill = false;
                    if (hmSettlemetnOptions.size() == 1)
                    {
                        for (clsPOSSettelementOptions obj : hmSettlemetnOptions.values())
                        {
                            if (obj.getStrSettelmentType().equals("Complementary"))
                            {
                                flgComplimentaryBill = true;
                                break;
                            }
                        }
                    }

                    if (!flgComplimentaryBill)
                    {
                        amt = listclsItemRow.getAmount();
              
                    if (listclsItemRow.isModifier())
                    {
                    	clsBillModifierDtlModel objBillModDtl = new clsBillModifierDtlModel();
//                        objBillModDtl.setStrBillNo(voucherNo);
                        objBillModDtl.setStrItemCode(listclsItemRow.getItemCode());
                        objBillModDtl.setStrModifierCode(listclsItemRow.getModifierCode());
                        objBillModDtl.setStrModifierName(listclsItemRow.getItemName());
                        objBillModDtl.setDblRate(rate);
                        objBillModDtl.setDblQuantity(listclsItemRow.getQuantity());
                        StringBuilder sbTemp = new StringBuilder(objBillModDtl.getStrItemCode());
                        if (hmComplimentaryBillItemDtl.containsKey(sbTemp.substring(0, 7).toString()))
                        {
                            amt = 0;
                        }
                        objBillModDtl.setDblAmount(amt);
//                        objBillModDtl.setStrClientCode(clientCode);
                        objBillModDtl.setStrCustomerCode(strCustomerCode);
                        objBillModDtl.setStrDataPostFlag("N");
                        objBillModDtl.setStrMMSDataPostFlag("N");
                        objBillModDtl.setStrDefaultModifierDeselectedYN(listclsItemRow.getStrDefaultModifierDeselectedYN());
                        objBillModDtl.setSequenceNo(listclsItemRow.getSeqNo());
//                        objBillModDtl.setDteBillDate(posDate);
//
//                        String key = listclsItemRow.getItemCode() + "!" + listclsItemRow.getItemName().toUpperCase();
//                        if (hmBillItemDtl.containsKey(key))
//                        {
//                        	clsItemsDtlsInBill objBillItemDtl = hmBillItemDtl.get(key);
                            objBillModDtl.setDblDiscAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
                            objBillModDtl.setDblDiscPer(listclsItemRow.getDiscountPer());
//                        }
//                        else
//                        {
//                            objBillModDtl.setDblDiscAmt(0);
//                            objBillModDtl.setDblDiscPer(0);
//                        }
                        listObjBillModBillDtls.add(objBillModDtl);
                    }
                }
//                objBillHd.setListBillModifierDtlModel(listObjBillModBillDtls);
               
//                funInsertBillModifierDtlTable(listObjBillModBillDtls);

                // insert into tblbilltaxdtl    
                List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();

                for (clsPOSTaxDtlsOnBill objTaxCalculationDtls :obBillItem.getListTaxDtlOnBill())
                {
                    double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();
                    clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
//                    objBillTaxDtl.setStrBillNo(voucherNo);
                    objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
                    objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
                    objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
//                    objBillTaxDtl.setStrClientCode(clientCode);
//                    objBillTaxDtl.setDteBillDate(posDate);

                    listObjBillTaxBillDtls.add(objBillTaxDtl);
//                    objBaseService.funSave(objBillTaxDtl);
                }
              objBillHd.setListBillTaxDtl(listObjBillTaxBillDtls);
                objBaseService.funSave(objBillHd);
             //   funInsertBillTaxDtlTable(listObjBillTaxBillDtls);
             //   clsUtilityController obj = objUtility;
                objUtility.funUpdateBillDtlWithTaxValues(voucherNo, "Live",posDate );

//                lblVoucherNo.setText(voucherNo);

/*                if (clsGlobalVarClass.gKOTPrintingEnableForDirectBiller)
                {
                    if ("Text File".equalsIgnoreCase(clsGlobalVarClass.gPrintType))
                    {
                        funTextFilePrintingKOTForDirectBiller();
                    }
                    else
                    {
                        funTextFilePrintingKOTForDirectBiller();
                    }
                }
                funSendBillToPrint(voucherNo,posDate);

                if (clsGlobalVarClass.gHomeDelSMSYN)
                {
                    funSendSMS(voucherNo, clsGlobalVarClass.gHomeDeliverySMS, "Home Delivery");
                }

                clsGlobalVarClass.gDeliveryCharges = 0.00;
                if (clsGlobalVarClass.gConnectionActive.equals("Y"))
                {
                    if (clsGlobalVarClass.gDataSendFrequency.equals("After Every Bill"))
                    {
                        clsGlobalVarClass.funInvokeHOWebserviceForTrans("Sales", "Bill");
                    }
                }*/
            }
        }
         }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
        finally
        {
            if (flgSettle)
            {
                System.gc();
            }
        }
    }
	
	
    public Map<String, List<clsPOSItemsDtlsInBill>> funGetBillSeriesList(List<clsPOSItemsDtlsInBill> listItemDtl)
    {
          try
           {
        	  StringBuilder sqlBuilder = new StringBuilder();
	          hmBillSeriesItemList = new HashMap<String, List<clsPOSItemsDtlsInBill>>();
	            for (clsPOSItemsDtlsInBill objBillItemDtl : listItemDtl)
	            {
	            	if(!(objBillItemDtl.getItemCode().length()>0)||objBillItemDtl.getItemCode().equals(""))
                	{
                		break;
                	}
	                sqlBuilder.setLength(0);
	    	       	sqlBuilder.append(" select * from tblbillseries where (strPOSCode='" + posCode + "' or strPOSCode='All') ");
	                boolean isExistsBillSeries = false;
	                List listBillSeries=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if(listBillSeries.size()>0)
				{
			    for(int cnt=0;cnt<listBillSeries.size();cnt++)
				{
				  Object [] obj = (Object[]) listBillSeries.get(cnt);
		    	
                	
                	sqlBuilder.setLength(0);
                    sqlBuilder.append("select a.strItemCode,a.strItemName,a.strRevenueHead,b.strPosCode,c.strMenuCode,c.strMenuName "
                            + " ,d.strSubGroupCode,d.strSubGroupName,e.strGroupCode,e.strGroupName "
                            + " from tblitemmaster a,tblmenuitempricingdtl b,tblmenuhd c,tblsubgrouphd d,tblgrouphd e "
                            + " where a.strItemCode=b.strItemCode and b.strMenuCode=c.strMenuCode "
                            + " and a.strSubGroupCode=d.strSubGroupCode and d.strGroupCode=e.strGroupCode ");
                    sqlBuilder.append(" and (b.strPosCode='" + posCode + "' Or b.strPosCode='All') ");
                    sqlBuilder.append(" and a.strItemCode='" + objBillItemDtl.getItemCode().substring(0, 7) + "' ");

                    String billSeriesType = obj[1].toString();
                    String filter = " e.strGroupCode ";
                    if (billSeriesType.equalsIgnoreCase("Group"))
                    {
                        filter = " e.strGroupCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Sub Group"))
                    {
                        filter = " d.strSubGroupCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Menu Head"))
                    {
                        filter = " c.strMenuCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Revenue Head"))
                    {
                        filter = " a.strRevenueHead ";
                    }
                    else
                    {
                        filter = "  ";
                    }
                    sqlBuilder.append(" and " + filter + " IN " + funGetCodes(obj[4].toString()));
                    sqlBuilder.append(" GROUP BY a.strItemCode; ");
                   
                    List listIsExists=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
    				if(listIsExists.size()>0)
    				{
                        isExistsBillSeries = true;
                        if (hmBillSeriesItemList.containsKey(obj[2].toString()))
                        {
                            hmBillSeriesItemList.get(obj[2].toString()).add(objBillItemDtl);
                        }
                        else
                        {
                            List<clsPOSItemsDtlsInBill> listBillSeriesDtl = new ArrayList<clsPOSItemsDtlsInBill>();
                            listBillSeriesDtl.add(objBillItemDtl);
                            hmBillSeriesItemList.put(obj[2].toString(), listBillSeriesDtl);
                        }
                        break;
                    }
                }
		       }
                if (!isExistsBillSeries)
                {
                    if (hmBillSeriesItemList.containsKey("NoBillSeries"))
                    {
                        hmBillSeriesItemList.get("NoBillSeries").add(objBillItemDtl);
                    }
                    else
                    {
                        List<clsPOSItemsDtlsInBill> listBillSeriesDtl = new ArrayList<clsPOSItemsDtlsInBill>();
                        listBillSeriesDtl.add(objBillItemDtl);
                        hmBillSeriesItemList.put("NoBillSeries", listBillSeriesDtl);
                    }
                }
            }
        }
        catch (Exception e)
        {
           
            e.printStackTrace();
        }
        finally
        {
            return hmBillSeriesItemList;
        }
    }
    
    private String funGetCodes(String codes)
    {
        StringBuilder codeBuilder = new StringBuilder("(");
        try
        {
            String code[] = codes.split(",");
            for (int i = 0; i < code.length; i++)
            {
                if (i == 0)
                {
                    codeBuilder.append("'" + code[i] + "'");
                }
                else
                {
                    codeBuilder.append(",'" + code[i] + "'");
                }
            }
            codeBuilder.append(")");
        }
        catch (Exception e)
        {
           
            e.printStackTrace();
        }
        finally
        {
            return codeBuilder.toString();
        }
    }
    
    
	public JSONObject funCalculateTax(JSONObject objKOTTaxData)
	{
    
		String taxAmt="";
	    double subTotalForTax=0;
		double taxAmount=0.0;
		JSONObject jsTaxDtl=new JSONObject(); 
		try {
			
			String posCode="",areaCode="",operationType="",clientCode="";
			
	        List<clsPOSItemDtlForTax> arrListItemDtls=new ArrayList<clsPOSItemDtlForTax>();
	        JSONArray mJsonArray=(JSONArray)objKOTTaxData.get("TaxDtl");
			String sql="";
		    String posDate="";
			 ResultSet rs;
			 JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.size(); i++) 
			{
				clsPOSItemDtlForTax objItemDtl=new clsPOSItemDtlForTax();
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    String itemName=mJsonObject.get("strItemName").toString();
			    String itemCode=mJsonObject.get("strItemCode").toString();
			    System.out.println(itemName);
			    double amt=Double.parseDouble(mJsonObject.get("dblAmount").toString());
			    operationType=mJsonObject.get("OperationType").toString();
			    posCode=mJsonObject.get("strPOSCode").toString();
			    areaCode=mJsonObject.get("AreaCode").toString();
			    posDate=mJsonObject.get("POSDate").toString();
			    clientCode=mJsonObject.get("strClientCode").toString();
			    StringBuilder sqlBuilder= new StringBuilder();
			    if(areaCode.equals("")){
			    	sqlBuilder.setLength(0);
			       	sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') and strClientCode='"+clientCode+"'");
			    	List listAreCode=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					    if(listAreCode.size()>0)
					    {
					    	 for(int cnt=0;cnt<listAreCode.size();cnt++)
				         	    {
					    		 Object obj = (Object) listAreCode.get(cnt);
					    		 areaCode = (obj.toString());
				         	    }
						}
			    }
			   
                objItemDtl.setItemCode(itemCode);
                objItemDtl.setItemName(itemName);
                objItemDtl.setAmount(amt);
                objItemDtl.setDiscAmt(0);
                objItemDtl.setDiscPer(0);
                arrListItemDtls.add(objItemDtl);
                subTotalForTax+=amt;
			   // tableNo=mJsonObject.get("strTableNo").toString();
			    
			}
			
			   Date dt=new Date();            
	            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate();            
	            clsPOSTaxCalculation objTaxCalculation=new clsPOSTaxCalculation();
	            
	           
	            List <clsPOSTaxCalculationDtls> arrListTaxDtl=	objUtility.funCalculateTax(arrListItemDtls,posCode
	                , posDate, areaCode, operationType, subTotalForTax, 0.0,"");
	            JSONArray jAyyTaxList= new JSONArray();
	            JSONObject jsTax; 
	            for(int cnt=0;cnt<arrListTaxDtl.size();cnt++)
	            {
	            	jsTax=new JSONObject(); 
	            	clsPOSTaxCalculationDtls obj=(clsPOSTaxCalculationDtls)arrListTaxDtl.get(cnt);
	            	System.out.println("Tax Dtl= "+obj.getTaxCode()+"\t"+obj.getTaxName()+"\t"+obj.getTaxAmount());
	            	taxAmount+=obj.getTaxAmount();
	            	taxAmt=String.valueOf(taxAmount);
	            	jsTax.put("TaxName", obj.getTaxName());
	            	jsTax.put("TaxAmt", obj.getTaxAmount());
	            	jsTax.put("taxCode", obj.getTaxCode());
	            	jsTax.put("taxCalculationType", obj.getTaxCalculationType());
	            	jsTax.put("taxableAmount", obj.getTaxableAmount());
	            	
	            	jAyyTaxList.add(jsTax);
	            }
	            jsTaxDtl.put("listOfTax", jAyyTaxList);
	            jsTaxDtl.put("totalTaxAmt", taxAmt);
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return jsTaxDtl;//Response.status(201).entity(jsTaxDtl).build();		
	}
	
	
	
	/*  private void funModifyBill()
	    {
	        try
	        {
	            String modifyBillReasonCode = "", modifyBillReasonName = "";

	            if (vComplReasonCode.size() == 0)
	            {
	                JOptionPane.showMessageDialog(this, "No complementary reasons are created");
	                return;
	            }
	            else
	            {
	                if (vModifyReasonName.size() > 0)
	                {
	                    Object[] arrObjReasonName = vModifyReasonName.toArray();
	                    modifyBillReasonName = (String) JOptionPane.showInputDialog(this, "Please Select Modify Bill Reason?", "Reason", JOptionPane.QUESTION_MESSAGE, null, arrObjReasonName, arrObjReasonName[0]);
	                    if (null == modifyBillReasonName)
	                    {
	                        JOptionPane.showMessageDialog(this, "Please Select Modify Bill Reason");
	                        return;
	                    }
	                    else
	                    {
	                        for (int cntReason = 0; cntReason < vModifyReasonCode.size(); cntReason++)
	                        {
	                            if (vModifyReasonName.elementAt(cntReason).toString().equals(modifyBillReasonName))
	                            {
	                                modifyBillReasonCode = vModifyReasonCode.elementAt(cntReason).toString();
	                                break;
	                            }
	                        }
	                    }
	                }
	                else
	                {
	                    JOptionPane.showMessageDialog(this, "No Modify reasons are created");
	                    return;
	                }
	            }

	            String sql = "delete from tblvoidbillhd where strBillNo='" + lblVoucherNo.getText() + "' and strTransType='MB'";
	            clsGlobalVarClass.dbMysql.execute(sql);

	            sql = "insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
	                    + "dblActualAmount,dblModifiedAmount,dteBillDate,strTransType,"
	                    + "dteModifyVoidBill,strTableNo,strWaiterNo,intShiftCode,strUserCreated,"
	                    + "strUserEdited,strClientCode)"
	                    + "(select '" + clsGlobalVarClass.gPOSCode + "','" + modifyBillReasonCode + "' "
	                    + ",'" + modifyBillReasonName + "','" + lblVoucherNo.getText() + "',dblGrandTotal "
	                    + ",'" + _grandTotal + "',dteBillDate,'MB','" + clsGlobalVarClass.getPOSDateForTransaction() + "' "
	                    + ",strTableNo,strWaiterNo,intShiftCode,strUserCreated "
	                    + ",'" + clsGlobalVarClass.gUserCode + "',strClientCode "
	                    + " from tblbillhd where strBillNo='" + lblVoucherNo.getText() + "')";
	            clsGlobalVarClass.dbMysql.execute(sql);

	            clsGlobalVarClass.gReasoncode = "";
	            clsGlobalVarClass.gFavoritereason = "";
	            sql = "insert into tblvoidbilldtl (strPosCode,strReasonCode,strReasonName,"
	                    + "strItemCode,strItemName,strBillNo,intQuantity,dblAmount,dblTaxAmount,dteBillDate,"
	                    + "strTransType,dteModifyVoidBill,intShiftCode,strUserCreated,strClientCode)"
	                    + "(select '" + clsGlobalVarClass.gPOSCode + "','','',strItemCode,strItemName,"
	                    + "strBillNo,dblQuantity,dblAmount,dblTaxAmount,dteBillDate,"
	                    + "'MB','" + clsGlobalVarClass.getPOSDateForTransaction() + "',(select intShiftCode from tblbillhd where strBillNo='" + lblVoucherNo.getText() + "'),'" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.gClientCode + "' "
	                    + "from tblbilldtl where strBillNo='" + lblVoucherNo.getText() + "')";
	            clsGlobalVarClass.dbMysql.execute(sql);

	            //clear old data for this billDiscountDtl table
	            sql = "delete from tblbilldiscdtl where strBillNo='" + voucherNo + "' ";
	            clsGlobalVarClass.dbMysql.execute(sql);
	            funSaveBillDiscountDetail(voucherNo);

	            //For saving different transaction on same Bill in tblBillHd table in database
	            String transactionType = "";
	            sql = "select strTransactionType from tblbillhd where strBillNo='" + voucherNo + "'";
	            ResultSet rsTransactionType = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	            if (rsTransactionType.next())
	            {
	                transactionType = rsTransactionType.getString(1) + ",Modify Bill";
	            }
	            rsTransactionType.close();

	            sql = "update tblbillhd set "
	                    + " dblDiscountAmt='" + dblDiscountAmt + "',dblDiscountPer='" + dblDiscountPer + "'"
	                    + ",dblTaxAmt='" + dblTotalTaxAmt + "'"
	                    + ",dblSubTotal='" + _subTotal + "'"
	                    + ",dblGrandTotal='" + _grandTotal + "'"
	                    + ",dblRoundOff='" + _grandTotalRoundOffBy + "'"
	                    + ",strUserEdited='" + clsGlobalVarClass.gUserCode + "'"
	                    + ",dteDateEdited='" + clsGlobalVarClass.getCurrentDateTime() + "' "
	                    + ",strDataPostFlag='N'"
	                    + ",strReasonCode='" + selectedReasonCode + "'"
	                    + ",strRemarks='" + txtAreaRemark.getText().trim() + "'"
	                    + ",dblTipAmount='" + txtTip.getText() + "'"
	                    + ",strDiscountRemark='" + discountRemarks + "'"
	                    + ",strTransactionType='" + transactionType + "' "
	                    + " where strBillNo='" + lblVoucherNo.getText() + "' ";
	            clsGlobalVarClass.dbMysql.execute(sql);

	            //update billseriesbilldtl grand total
	            if (clsGlobalVarClass.gEnableBillSeries)
	            {
	                clsGlobalVarClass.dbMysql.execute("update tblbillseriesbilldtl set dblGrandTotal='" + _grandTotal + "' where strHdBillNo='" + lblVoucherNo.getText() + "' and date(dteBillDate)='" + clsGlobalVarClass.gPOSOnlyDateForTransaction + "' ");
	            }

	            printstatus = true;

	            if (hmComplimentaryBillItemDtl.size() > 0)
	            {
	                List<clsBillDtl> listObjBillDtl = new ArrayList<clsBillDtl>();
	                String sqlBillDtl = "select * from tblbilldtl "
	                        + " where strBillNo='" + voucherNo + "' and strClientCode='" + clsGlobalVarClass.gClientCode + "'";
	                ResultSet rsBillDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillDtl);
	                while (rsBillDtl.next())
	                {
	                    clsBillDtl objBillDtl = new clsBillDtl();
	                    objBillDtl.setStrItemCode(rsBillDtl.getString(1));
	                    objBillDtl.setStrItemName(rsBillDtl.getString(2));
	                    objBillDtl.setStrAdvBookingNo("");
	                    objBillDtl.setStrBillNo(voucherNo);
	                    objBillDtl.setDblRate(rsBillDtl.getDouble(5));
	                    objBillDtl.setDblQuantity(rsBillDtl.getDouble(6));
	                    objBillDtl.setDblAmount(rsBillDtl.getDouble(7));
	                    objBillDtl.setDblTaxAmount(0);
	                    objBillDtl.setDteBillDate(objUtility.funGetPOSDateForTransaction());
	                    objBillDtl.setStrKOTNo(rsBillDtl.getString(10));
	                    objBillDtl.setStrClientCode(clsGlobalVarClass.gClientCode);
	                    objBillDtl.setTmeOrderProcessing("00:00:00");
	                    objBillDtl.setStrDataPostFlag("N");
	                    objBillDtl.setStrMMSDataPostFlag("N");
	                    objBillDtl.setStrManualKOTNo("");
	                    objBillDtl.setTdhYN(rsBillDtl.getString(17));
	                    objBillDtl.setStrPromoCode(rsBillDtl.getString(18));
	                    objBillDtl.setStrCounterCode(rsBillDtl.getString(19));
	                    objBillDtl.setStrWaiterNo(rsBillDtl.getString(20));
	                    objBillDtl.setSequenceNo(rsBillDtl.getString(23));
	                    objBillDtl.setStrOrderPickupTime(rsBillDtl.getString(25));

	                    listObjBillDtl.add(objBillDtl);
	                }
	                funInsertBillDtlTable(listObjBillDtl);
	            }

	            for (clsBillItemDtl objBillItemDtl : hmBillItemDtl.values())
	            {
	                String key = (objBillItemDtl.getItemCode().contains("M") ? objBillItemDtl.getItemCode() + "!" + objBillItemDtl.getItemName() : objBillItemDtl.getItemCode());
	                String iCode = objBillItemDtl.getItemCode();
	                String iName = objBillItemDtl.getItemName();

	                double discPer = hmBillItemDtl.get(key).getDiscountPercentage();
	                double discAmt = hmBillItemDtl.get(key).getDiscountAmount();
	                sql = "update tblbilldtl set dblDiscountAmt=" + discAmt + "*dblQuantity, dblDiscountPer=" + discPer
	                        + " where strItemCode='" + iCode + "'  and strBillNo='" + lblVoucherNo.getText() + "' ";
	                clsGlobalVarClass.dbMysql.execute(sql);

	                sql = "update tblbillmodifierdtl set dblDiscAmt=" + discAmt + "*dblQuantity, dblDiscPer=" + discPer
	                        + " where strItemCode='" + iCode + "' and strModifierName='" + iName + "' and strBillNo='" + lblVoucherNo.getText() + "' ";
	                clsGlobalVarClass.dbMysql.execute(sql);
	            }

	            sql = "delete from tblbilltaxdtl where strBillNo='" + voucherNo + "'";
	            clsGlobalVarClass.dbMysql.execute(sql);

	            //insert into tblbilltaxdtl    
	            List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();

	            for (clsTaxCalculationDtls objTaxCalculationDtls : arrListTaxCal)
	            {
	                double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();
	                //totalTaxAmt = totalTaxAmt + dblTaxAmt;
	                clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
	                objBillTaxDtl.setStrBillNo(voucherNo);
	                objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
	                objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
	                objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
	                objBillTaxDtl.setStrClientCode(clsGlobalVarClass.gClientCode);
	                objBillTaxDtl.setDteBillDate(clsGlobalVarClass.getPOSDateForTransaction());

	                listObjBillTaxBillDtls.add(objBillTaxDtl);
	            }

	            funInsertBillTaxDtlTable(listObjBillTaxBillDtls);

	            clsUtility obj = new clsUtility();
	            obj.funUpdateBillDtlWithTaxValues(voucherNo, "Live", clsGlobalVarClass.gPOSOnlyDateForTransaction);

	            //funFillTaxBillTable(voucherNo);
	            if (printstatus == true)
	            {
	                frmOkPopUp okOb = new frmOkPopUp(null, "Bill Updated Successfully ", "Success", 1);
	                okOb.setVisible(true);
	                sql = "select count(strBillNo),strCustomerCode "
	                        + " from tblhomedelivery "
	                        + " where strBillNo='" + voucherNo + "'";
	                ResultSet rsHomeDelivery = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	                rsHomeDelivery.next();
	                if (rsHomeDelivery.getInt(1) > 0)
	                {
	                    if (clsGlobalVarClass.gHomeDelSMSYN)
	                    {
	                        funSendSMS(voucherNo, clsGlobalVarClass.gHomeDeliverySMS, "Home Delivery");
	                    }
	                    if (clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File"))
	                    {
	                        objUtility.funPrintBill(voucherNo, objUtility.funGetOnlyPOSDateForTransaction(), false, clsGlobalVarClass.gPOSCode, "print");

	                        if (clsGlobalVarClass.gEnableBillSeries)
	                        {
	                            String reprintBillNo = objUtility2.funGetBillNoOnModifyBill(voucherNo);
	                            objUtility.funPrintBill(reprintBillNo, objUtility.funGetOnlyPOSDateForTransaction(), false, clsGlobalVarClass.gPOSCode, "print");
	                        }
	                    }
	                }
	                else
	                {
	                    funSendBillToPrint(voucherNo, objUtility.funGetOnlyPOSDateForTransaction());
	                }
	                rsHomeDelivery.close();
	            }

	            //send modified bill MSG            
	            sql = "select a.strSendSMSYN,a.longMobileNo "
	                    + "from tblsmssetup a "
	                    + "where (a.strPOSCode='" + clsGlobalVarClass.gPOSCode + "' or a.strPOSCode='All') "
	                    + "and a.strClientCode='" + clsGlobalVarClass.gClientCode + "' "
	                    + "and a.strTransactionName='ModifyBill' "
	                    + "and a.strSendSMSYN='Y'; ";
	            ResultSet rsSendSMS = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	            if (rsSendSMS.next())
	            {
	                funSendModifyBillSMS(voucherNo, rsSendSMS.getString(2));
	            }
	            rsSendSMS.close();

	            dispose();
	            if (clsGlobalVarClass.gConnectionActive.equals("Y"))
	            {
	                if (clsGlobalVarClass.gDataSendFrequency.equals("After Every Bill"))
	                {
	                    clsGlobalVarClass.funInvokeHOWebserviceForTrans("Sales", "Bill");
	                }
	            }
	        }
	        catch (Exception e)
	        {
	            objUtility.funWriteErrorLog(e);
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, e.getMessage(), "Error Code: BS-25", JOptionPane.ERROR_MESSAGE);
	        }
	        finally
	        {
	            System.gc();
	        }
	    }
	  
	  
	  */
	  
	   //generate bill no.
	    private void funGenerateBillNo()
	    {
	        try
	        {
	            long code = 0;
	            StringBuilder sqlBuilder=new StringBuilder();
	            sqlBuilder.setLength(0);
	    	  	sqlBuilder.append ( "select strBillNo from tblstorelastbill where strPosCode='" +posCode + "'");
	          	List listItemDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	        	
	    		if(listItemDtl!=null && listItemDtl.size()>0)
	    		{
	    		
	    		Object objItemDtl=(Object)listItemDtl.get(0);
	            
	                code = Math.round(Double.parseDouble(objItemDtl.toString()));
	                code = code + 1;

	                voucherNo = posCode + String.format("%05d", code);
	                objBaseServiceImpl.funExecuteUpdate( "update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + posCode+ "'","sql");
	            }
	            else
	            {
	                voucherNo =posCode + "00001";
	                sqlBuilder.setLength(0);
	                objBaseServiceImpl.funExecuteUpdate( "insert into tblstorelastbill values('" + posCode + "','1')","sql");
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    
	    private void funGenerateBillNoForBillSeriesForDirectBiller(String billSeriesPrefix, List<clsPOSItemsDtlsInBill> listOfItemDtl,clsPOSBillSettlementBean objBean,String posDate, String posCode,String clientCode,String userCode)
	    {
	        try
	        {
	            List<clsPOSItemDtlForTax> arrListItemDtls = new ArrayList<clsPOSItemDtlForTax>();

	            int billSeriesLastNo = 0;
	            StringBuilder sqlBuilder=new StringBuilder();
	            sqlBuilder.setLength(0);
			  	sqlBuilder.append( "select a.intLastNo "
	                    + "from tblbillseries a "
	                    + "where a.strBillSeries='" + billSeriesPrefix + "' "
	                    + "and (a.strPOSCode='" + posCode + "' OR a.strPOSCode='All'); ");
			  	List lisBillSeriesLastNo=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			    if(lisBillSeriesLastNo.size()>0)
			    {
//			    	 for(int cnt=0;cnt<listAreCode.size();cnt++)
//		         	    {
			    		 Object obj = (Object) lisBillSeriesLastNo.get(0);
	           
	                billSeriesLastNo =Integer.parseInt(obj.toString());
	            }
	            String billSeriesBillNo = billSeriesPrefix + "" + posCode + "" + String.format("%05d", (billSeriesLastNo + 1));

	            //update last bill series last no
	            int a = objBaseServiceImpl.funExecuteUpdate("update tblbillseries "
	                    + "set intLastNo='" + (billSeriesLastNo + 1) + "' "
	                    + "where (strPOSCode='" + posCode + "' OR strPOSCode='All') "
	                    + "and strBillSeries='" + billSeriesPrefix + "' ","sql");
	            //last order no
	            int intLastOrderNo = objUtility.funGetLastOrderNo();

	            hmBillItemDtl.clear();
	            double subTotal = 0.00;
	            for (clsPOSItemsDtlsInBill obj : listOfItemDtl)
	            {
	                if (obj.getItemCode().contains("M"))
	                {
	                    hmBillItemDtl.put(obj.getItemCode() + "!" + obj.getItemName(), obj);
	                }
	                else
	                {
	                    hmBillItemDtl.put(obj.getItemCode(), obj);
	                }
	                subTotal += obj.getAmount();

	                clsPOSItemDtlForTax objItemDtlForTax = new clsPOSItemDtlForTax();
	                objItemDtlForTax.setItemCode(obj.getItemCode());
	                objItemDtlForTax.setItemName(obj.getItemName());
	                objItemDtlForTax.setAmount(obj.getAmount());
	                objItemDtlForTax.setDiscAmt(obj.getDiscountAmt() * obj.getQuantity());
	                arrListItemDtls.add(objItemDtlForTax);
	            }
	            String strCustomerCode = "";

//	            if (null != clsGlobalVarClass.gCustomerCode)
//	            {
//	                strCustomerCode = clsGlobalVarClass.gCustomerCode;
//	            }
	            String operationType = "DirectBiller";

	            voucherNo = billSeriesBillNo;

	            if (customerCode != null)
	            {
//	                strCustomerCode = customerCode;
//	                if (homeDelivery.equals("Y"))
//	                {
//	                    operationType = "HomeDelivery";
//	                    Calendar c = Calendar.getInstance();
//	                    int hh = c.get(Calendar.HOUR);
//	                    int mm = c.get(Calendar.MINUTE);
//	                    int ss = c.get(Calendar.SECOND);
//	                    int ap = c.get(Calendar.AM_PM);
//	                    String ampm = "AM";
//	                    if (ap == 1)
//	                    {
//	                        ampm = "PM";
//	                    }
//	                    String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;
//
//	                    if (delPersonCode != null)
//	                    {
//	                        String sql_tblhomedelivery = "insert into tblhomedelivery(strBillNo,strCustomerCode"
//	                                + ",strDPCode,dteDate,tmeTime,strPOSCode,strCustAddressLine1,strClientCode,dblHomeDeliCharge) "
//	                                + "values('" + voucherNo + "','" + custCode + "'"
//	                                + ",'" + delPersonCode + "','" + clsGlobalVarClass.gPOSDateForTransaction + "'"
//	                                + ",'" + currentTime + "','" + clsGlobalVarClass.gPOSCode + "','" + custAddType + "'"
//	                                + ",'" + clsGlobalVarClass.gClientCode + "'," + _deliveryCharge + ")";
//	                        clsGlobalVarClass.dbMysql.execute(sql_tblhomedelivery);
//	                    }
//	                    else
//	                    {
//	                        String sql_tblhomedelivery = "insert into tblhomedelivery(strBillNo,strCustomerCode,dteDate,tmeTime"
//	                                + ",strPOSCode,strCustAddressLine1,strCustAddressLine2,strCustAddressLine3,strCustAddressLine4"
//	                                + ",strCustCity,strClientCode,dblHomeDeliCharge)"
//	                                + " values('" + voucherNo + "','" + custCode + "','"
//	                                + clsGlobalVarClass.gPOSDateForTransaction + "','" + currentTime + "','"
//	                                + clsGlobalVarClass.gPOSCode + "','" + custAddType + "','',''"
//	                                + ",'','','" + clsGlobalVarClass.gClientCode + "'," + _deliveryCharge + ")";
//	                        clsGlobalVarClass.dbMysql.execute(sql_tblhomedelivery);
//	                    }
//	                }
	            }

	      
	            if (obBillItem.getTakeAwayYN().equals("Yes"))
                {
                    operationType = "TakeAway";
                    
                }
                if (takeAway.equals("Yes"))
                {
                    operationType = "TakeAway";
                   
                }

	            String counterCode = "NA";
//	            if (clsGlobalVarClass.gCounterWise.equals("Yes"))
//	            {
//	                if (null != clsGlobalVarClass.gCounterCode)
//	                {
//	                    counterCode = clsGlobalVarClass.gCounterCode;
//	                }
//	            }
	            double homeDeliveryCharges = 0.00;
	            if (String.valueOf(objBean.getDblDeliveryCharges()).trim().length() > 0)
	            {
	                homeDeliveryCharges =objBean.getDblDeliveryCharges();
	            }
	            String waiterNo = "NA";
	            if (advOrderBookingNo.trim().length() > 0)
	            {
	            	sqlBuilder.setLength(0);
				  	sqlBuilder.append( "select strCustomerCode,ifnull(strWaiterNo,'NA') from tbladvbookbillhd "
	                        + "where strAdvBookingNo='" + advOrderBookingNo + "'");
				  	List lisAdvOrderCustCode=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				    if(lisAdvOrderCustCode.size()>0)
				    {
				    Object[] obj = (Object[]) lisAdvOrderCustCode.get(0);
	                strCustomerCode = obj[0].toString();
	                 waiterNo =obj[1].toString();
				    }
	            }

	            //calculate Tax for bill series wise
	            //funCalculateTaxForBillSeriesWise("direct", listOfItemDtl);
	            double advanceAmount = 0.00;
	            double _deliveryCharge = 0.00;
	            dblTotalTaxAmt = 0;
	            _netAmount = 0.00;
	            _subTotal = 0.00;
	            dblDiscountAmt = 0.00;
	            dblDiscountPer = 0.00;
	            _grandTotal = 0.00;

	            double tempDiscAmt = 0;
	            for (Map.Entry<String, clsPOSItemsDtlsInBill> entry : hmBillItemDtl.entrySet())
	            {
	            	clsPOSItemsDtlsInBill objBillItemDtl = entry.getValue();
	                tempDiscAmt += objBillItemDtl.getDiscountAmt() * objBillItemDtl.getQuantity();
	            }
	            _subTotal = subTotal;
	            
//	            (List<clsPOSItemDetailFrTaxBean> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception

	            arrListTaxCal = objUtility.funCalculateTax(arrListItemDtls, posCode, posDate, areaCode, operationTypeForTax, _subTotal, tempDiscAmt, "");

	            for (clsPOSTaxCalculationDtls objTaxCalculationDtls : arrListTaxCal)
	            {
	                if (objTaxCalculationDtls.getTaxCalculationType().equalsIgnoreCase("Forward"))
	                {
	                    double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();
	                    dblTotalTaxAmt = dblTotalTaxAmt + dblTaxAmt;
	                }
	            }
	            Date dt=new Date();
    		    String currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
    		    String dateTime=posDate+" "+currentDateTime.split(" ")[1];
	            clsBillHdModel objBillHd = new clsBillHdModel();
	            //save bill disc dtl            
	            List<clsBillDiscDtlModel>listBillDiscDtlModel=funSaveBillDiscDtlForBillSeriesForDirectBiller(objBean,userCode,dateTime);
	            objBillHd.setListBillDiscDtlModel(listBillDiscDtlModel);
	            _netAmount = _subTotal - dblDiscountAmt;
	            _grandTotal = _netAmount + dblTotalTaxAmt + _deliveryCharge;
	            _grandTotal = _grandTotal - advanceAmount;
	            _grandTotal = _grandTotal;

	            //start code to calculate roundoff amount and round off by amt
	            Map<String, Double> mapRoundOff = objUtility.funCalculateRoundOffAmount(_grandTotal,posCode);
	            _grandTotal = mapRoundOff.get("roundOffAmt");
	            _grandTotalRoundOffBy = mapRoundOff.get("roundOffByAmt");
	            //end code to calculate roundoff amount and round off by amt

	            settleName = "";
	            ////////
	            
	            List <clsPOSSettlementDtlsOnBill>listSetllementDtl=objBean.getListSettlementDtlOnBill();
	            if (objBean.getStrSettlementType().equalsIgnoreCase("Settle"))
	            {
	                int row = 0;
	                boolean isBillSettled = false;
	                List<clsPOSBillSettlementDtl> listObjBillSettlementDtl = new ArrayList<clsPOSBillSettlementDtl>();
	                double billGrandTotalAmt = _grandTotal;
	                for (clsPOSSettlementDtlsOnBill ob :listSetllementDtl)
	                {
	                    if (ob.getDblPaidAmt() < 1)
	                    {
	                        continue;
	                    }

	                    settleName = ob.getStrSettelmentDesc();
	                    double settleAmt = 0;
	                    if (billGrandTotalAmt > ob.getDblPaidAmt())
	                    {
	                        settleAmt = ob.getDblPaidAmt();
	                        ob.setDblPaidAmt(0.00);
	                    }
	                    else
	                    {
	                        settleAmt = billGrandTotalAmt;
	                        ob.setDblPaidAmt(ob.getDblPaidAmt() - settleAmt);
	                        isBillSettled = true;
	                    }
	                    billGrandTotalAmt = billGrandTotalAmt - settleAmt;

	                    if (ob.getStrSettelmentType().equals("Debit Card"))
	                    {
	                        objUtility.funDebitCardTransaction(voucherNo, debitCardNo, settleAmt, "Settle",posCode,posDate);
	                        objUtility.funUpdateDebitCardBalance(debitCardNo, settleAmt, "Settle");
	                    }

	                    clsPOSBillSettlementDtl objBillSettlementDtl = new clsPOSBillSettlementDtl();
	                    objBillSettlementDtl.setStrBillNo(voucherNo);
	                    objBillSettlementDtl.setStrSettlementCode(ob.getStrSettelmentCode());
	                    objBillSettlementDtl.setDblSettlementAmt(settleAmt);
	                    if (billGrandTotalAmt == 0)
	                    {
	                        objBillSettlementDtl.setDblPaidAmt(settleAmt);
	                        objBillSettlementDtl.setDblActualAmt(settleAmt);
	                    }
	                    else
	                    {
	                        objBillSettlementDtl.setDblPaidAmt(settleAmt);
	                        objBillSettlementDtl.setDblActualAmt(settleAmt);
	                    }

	                    objBillSettlementDtl.setStrExpiryDate("");
	                    objBillSettlementDtl.setStrCardName(ob.getStrCardName());
	                    objBillSettlementDtl.setStrRemark(ob.getStrRemark());
	                    objBillSettlementDtl.setStrClientCode(clientCode);
	                    objBillSettlementDtl.setStrCustomerCode(customerCode);
	                    objBillSettlementDtl.setDblRefundAmt(0);
	                    objBillSettlementDtl.setStrGiftVoucherCode(ob.getStrGiftVoucherCode());
	                    objBillSettlementDtl.setStrDataPostFlag("N");
	                    listObjBillSettlementDtl.add(objBillSettlementDtl);

	                    row++;

	                    if (isBillSettled)
	                    {
	                        break;
	                    }
	                }
	                List<clsBillSettlementDtlModel> listBillSettlementDtlModel=funInsertBillSettlementDtlTable(listObjBillSettlementDtl,userCode,dateTime);
	                objBillHd.setListBillSettlementDtlModel(listBillSettlementDtlModel);
	                funTruncateDebitCardTempTable();

	                if (row > 1)
	                {
	                    settleName = "MultiSettle";
	                }
	            }

//	            if (clsGlobalVarClass.gClientCode.equals("190.001") && billSeriesPrefix.equalsIgnoreCase("L") && strCustomerCode.trim().isEmpty())
//	            {
//	                strCustomerCode = objUtility2.funAutoCustomerSelectionForLiquorBill();
//	            }
	          
	            //Insert into tblbillhd table
    		   
	            objBillHd.setStrBillNo(voucherNo);
	            objBillHd.setStrAdvBookingNo(advOrderBookingNo);
	            objBillHd.setDteBillDate(posDate);
	            objBillHd.setStrPOSCode(posCode);
	            objBillHd.setStrSettelmentMode(settleName);
	            objBillHd.setDblDiscountAmt(dblDiscountAmt);
	            objBillHd.setDblDiscountPer(dblDiscountPer);
	            objBillHd.setDblTaxAmt(dblTotalTaxAmt);
	            objBillHd.setDblSubTotal(_subTotal);
	            objBillHd.setDblGrandTotal(_grandTotal);
//	            objBillHd.setDblGrandTotalRoundOffBy(_grandTotalRoundOffBy);
	            objBillHd.setStrTakeAway(takeAway);
	            objBillHd.setStrOperationType(operationType);
//	            objBillHd.setStrTransactionType();
	            objBillHd.setStrUserCreated(userCode);
	            objBillHd.setStrUserEdited(userCode);
	            objBillHd.setDteDateCreated(currentDateTime);
	            objBillHd.setDteDateEdited(currentDateTime);
	            objBillHd.setStrClientCode(clientCode);
	            objBillHd.setStrTableNo("");
	            objBillHd.setStrWaiterNo(waiterNo);
	            objBillHd.setStrCustomerCode(strCustomerCode);
	            objBillHd.setStrManualBillNo(objBean.getStrManualBillNo());
	            objBillHd.setIntShiftCode(0);
	            objBillHd.setIntPaxNo(0);
	            objBillHd.setStrDataPostFlag("N");
	            objBillHd.setStrReasonCode(selectedReasonCode);
	            objBillHd.setStrRemarks(objBean.getStrRemarks());
	            objBillHd.setDblTipAmount(0.0);
	            objBillHd.setDteSettleDate(currentDateTime);
	            objBillHd.setStrCounterCode(counterCode);
	            objBillHd.setDblDeliveryCharges(_deliveryCharge);
	            objBillHd.setStrAreaCode(obBillItem.getAreaCode());
	            objBillHd.setStrDiscountRemark(discountRemarks);
	            objBillHd.setStrTakeAwayRemarks(takeAwayRemarks);
	            objBillHd.setIntOrderNo(intLastOrderNo);

	            
	            
	            String discountOn = "";
	            String chckDiscounton=objBean.getStrDisountOn();
	            
	            if(chckDiscounton!=null){
	            if (chckDiscounton.equals("Total"))
	            {
	                discountOn = "All";
	            }
	            if (chckDiscounton.equals("item"))
	            {
	                discountOn = "Item";
	            }
	            if (chckDiscounton.equals("group"))
	            {
	                discountOn = "Group";
	            }
	            if (chckDiscounton.equals("subGroup"))
	            {
	                discountOn = "SubGroup";
	            }
	            }
	            objBillHd.setStrDiscountOn(discountOn);
	            objBillHd.setStrCardNo(debitCardNo);
//                objBaseService.funSave(objBillHd);
//	            funInsertBillHdTable(objBillHd);

	            clsPOSBillSeriesBillDtl objBillSeriesBillDtl = new clsPOSBillSeriesBillDtl();
	            objBillSeriesBillDtl.setStrHdBillNo(voucherNo);
	            objBillSeriesBillDtl.setStrBillSeries(billSeriesPrefix);
	            objBillSeriesBillDtl.setDblGrandTotal(_grandTotal);
	            listBillSeriesBillDtl.add(objBillSeriesBillDtl);

	            sqlBuilder.setLength(0);
			  	sqlBuilder.append("select strCMSIntegrationYN  from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
				List listCMS=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
				 String gCMSIntegrationYN="";
				
				if(listCMS!=null && listCMS.size()>0)
				{				
					Object obj=(Object)listCMS.get(0);
					gCMSIntegrationYN=obj.toString();
					
				}
            
	            if (gCMSIntegrationYN.equals("Y"))
	            {
	                if (customerCode.trim().length() > 0)
	                {
	                    String sqlDeleteCustomer  = "delete from tblcustomermaster where strCustomerCode='" + customerCode + "' "
	                            + "and strClientCode='" + clientCode + "'";
	                    objBaseServiceImpl.funExecuteUpdate(sqlDeleteCustomer,"sql");
	                    String sqlInsertCustomer = "insert into tblcustomermaster (strCustomerCode,strCustomerName,strUserCreated"
	                            + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) "
	                            + "values('" + customerCode + "','" + cmsMemberName + "','" + userCode + "','" + userCode + "'"
	                            + ",'" + currentDateTime + "','" +currentDateTime + "'"
	                            + ",'" + clientCode + "')";
	                    objBaseServiceImpl.funExecuteUpdate(sqlInsertCustomer,"sql");
	                }
	            }

	            // Insert into tblbilldtl table
	            List<clsBillDtlModel> listObjBillDtl = new ArrayList<clsBillDtlModel>();
	          
	            for (clsPOSItemsDtlsInBill listclsItemRow : objBean.getListOfBillItemDtl())
	            {
	            	if(!(listclsItemRow.getItemCode().length()>0)||listclsItemRow.getItemCode().equals(""))
                	{
                		break;
                	}
	                String key = (listclsItemRow.isModifier() ? listclsItemRow.getItemCode() + "!" + listclsItemRow.getItemName() : listclsItemRow.getItemCode());

	                if (hmBillItemDtl.containsKey(key))
	                {
//	                    if (!listclsItemRow.isIsModifier())
	                	if (!listclsItemRow.isModifier())
	                    {
	                        double rate = 0.00;
	                        if (listclsItemRow.getQuantity() == 0)
	                        {
	                            rate = listclsItemRow.getRate();
	                        }
	                        else
	                        {
	                            rate = listclsItemRow.getAmount() / listclsItemRow.getQuantity();
	                        }

	                        clsBillDtlModel objBillDtl = new clsBillDtlModel();
	                        objBillDtl.setStrItemCode(listclsItemRow.getItemCode());
	                        objBillDtl.setStrItemName(listclsItemRow.getItemName());
	                        objBillDtl.setStrAdvBookingNo("");
//	                        objBillDtl.setStrBillNo(voucherNo);
	                        objBillDtl.setDblRate(rate);
	                        objBillDtl.setDblQuantity(listclsItemRow.getQuantity());
	                        objBillDtl.setDblAmount(listclsItemRow.getAmount());
	                        objBillDtl.setDblTaxAmount(0);
	                        objBillDtl.setDteBillDate(currentDateTime);
	                        objBillDtl.setStrKOTNo("");
//	                        objBillDtl.setStrClientCode(clientCode);
	                        objBillDtl.setStrCounterCode(strCustomerCode);
	                        objBillDtl.setTmeOrderProcessing("00:00:00");
	                        objBillDtl.setStrDataPostFlag("N");
	                        objBillDtl.setStrMMSDataPostFlag("N");
	                        objBillDtl.setStrManualKOTNo("");
	                        objBillDtl.setTdhYN(listclsItemRow.getTdhComboItemYN());
	                        objBillDtl.setStrPromoCode(listclsItemRow.getPromoCode());
	                        objBillDtl.setStrCounterCode(counterCode);
	                        objBillDtl.setStrWaiterNo(waiterNo);
	                        objBillDtl.setSequenceNo(listclsItemRow.getSeqNo());
	                        objBillDtl.setTmeOrderPickup("00:00:00");

	                        objBillDtl.setDblDiscountAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
	                        objBillDtl.setDblDiscountPer(listclsItemRow.getDiscountPer());
	                        listObjBillDtl.add(objBillDtl);
//	                        objBaseService.funSave(objBillDtl);
	                        
	                    }
	                }
	            }
	            objBillHd.setListBillDtlModel(listObjBillDtl);
//	            funInsertBillDtlTable(listObjBillDtl);
	            
	            // Insert into tblbillmodifierdtl
	            List<clsBillModifierDtlModel> listObjBillModBillDtls = new ArrayList<clsBillModifierDtlModel>();
	            for (clsPOSItemsDtlsInBill listclsItemRow : objBean.getListOfBillItemDtl())
	            {
	                String key = (listclsItemRow.isModifier() ? listclsItemRow.getItemCode() + "!" + listclsItemRow.getItemName() : listclsItemRow.getItemCode());

	                if (hmBillItemDtl.containsKey(key))
	                {
	                    double rate = listclsItemRow.getAmount() / listclsItemRow.getQuantity();
	                    double amt = 0.00;
	                    boolean flgComplimentaryBill = false;
	                    if (hmSettlemetnOptions.size() == 1)
	                    {
	                        for (clsPOSSettelementOptions obj : hmSettlemetnOptions.values())
	                        {
	                            if (obj.getStrSettelmentType().equals("Complementary"))
	                            {
	                                flgComplimentaryBill = true;
	                                break;
	                            }
	                        }
	                    }

	                    if (!flgComplimentaryBill)
	                    {
	                        amt = listclsItemRow.getAmount();
	                    }

	                    if (listclsItemRow.isModifier())
	                    {
	                    	clsBillModifierDtlModel objBillModDtl = new clsBillModifierDtlModel();
//	                        objBillModDtl.setStrBillNo(voucherNo);
	                        objBillModDtl.setStrItemCode(listclsItemRow.getItemCode());
	                        objBillModDtl.setStrModifierCode(listclsItemRow.getModifierCode());
	                        objBillModDtl.setStrModifierName(listclsItemRow.getItemName());
	                        objBillModDtl.setDblRate(rate);
	                        objBillModDtl.setDblQuantity(listclsItemRow.getQuantity());
	                        StringBuilder sbTemp = new StringBuilder(objBillModDtl.getStrItemCode());
	                        if (hmComplimentaryBillItemDtl.containsKey(sbTemp.substring(0, 7).toString()))
	                        {
	                            amt = 0;
	                        }
	                        objBillModDtl.setDblAmount(amt);
//	                        objBillModDtl.setStrClientCode(clientCode);
	                        objBillModDtl.setStrCustomerCode(strCustomerCode);
	                        objBillModDtl.setStrDataPostFlag("N");
	                        objBillModDtl.setStrMMSDataPostFlag("N");
	                        objBillModDtl.setStrDefaultModifierDeselectedYN(listclsItemRow.getStrDefaultModifierDeselectedYN());
	                        objBillModDtl.setSequenceNo(listclsItemRow.getSeqNo());
	                        
	                        objBillModDtl.setDblDiscAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
	                        objBillModDtl.setDblDiscPer(listclsItemRow.getDiscountPer());
//	                        objBillModDtl.setDteBillDate(currentDateTime);
	                        listObjBillModBillDtls.add(objBillModDtl);
//	                        objBaseService.funSave(objBillModDtl);
	                    }
	                }
	            }
	            objBillHd.setListBillModifierDtlModel(listObjBillModBillDtls);

	            // insert into tblbilltaxdtl    
	            List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();
	            //double totalTaxAmt = 0;

	            for (clsPOSTaxCalculationDtls objTaxCalculationDtls : arrListTaxCal)
	            {
	                double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();
	                //totalTaxAmt = totalTaxAmt + dblTaxAmt;
	                clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
//	                objBillTaxDtl.setStrBillNo(voucherNo);
	                objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
	                objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
	                objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
//	                objBillTaxDtl.setStrClientCode(clientCode);
//	                objBillTaxDtl.setDteBillDate(currentDateTime);
	                listObjBillTaxBillDtls.add(objBillTaxDtl);
//	                objBaseService.funSave(objBillTaxDtl);
	               
	            }
	            objBillHd.setListBillTaxDtl(listObjBillTaxBillDtls);
//	            funInsertBillTaxDtlTable(listObjBillTaxBillDtls);
	            clsPOSUtilityController obj = new clsPOSUtilityController();
	            
	            
	            obj.funUpdateBillDtlWithTaxValues(voucherNo, "Live", posDate);

	            // For Complimentary Bill
//	            funClearComplimetaryBillAmt(voucherNo);
//
//	            if (clsGlobalVarClass.gHomeDelSMSYN)
//	            {
//	                funSendSMS(voucherNo, clsGlobalVarClass.gHomeDeliverySMS, "Home Delivery");
//	            }
//	            lblVoucherNo.setText(voucherNo);
//
//	            if (clsGlobalVarClass.gKOTPrintingEnableForDirectBiller)
//	            {
//	                if ("Text File".equalsIgnoreCase(clsGlobalVarClass.gPrintType))
//	                {
//	                    funTextFilePrintingKOTForDirectBiller();
//	                }
//	                else
//	                {
//	                    funTextFilePrintingKOTForDirectBiller();
//	                }
//	            }
//	            dispose();
//	            clsGlobalVarClass.gDeliveryCharges = 0.00;
	        }
	        catch (Exception e)
	        {
	           
	            e.printStackTrace();
	        }
	    }
	        
	        
	        private List funSaveBillDiscDtlForBillSeriesForDirectBiller(clsPOSBillSettlementBean bean,String userCode,String dteCuurentDate)
	        {
	        	List<clsBillDiscDtlModel>listBillDiscDtlModel=new ArrayList<clsBillDiscDtlModel>();
	            try
	            {
	                StringBuilder sqlBillDiscDtl = new StringBuilder();
	                List<clsPOSDiscountDtlsOnBill>listDisc = bean.getListDiscountDtlOnBill();
//	                Iterator<Map.Entry<String, clsBillDiscountDtl>> itDiscEntry = mapBillDiscDtl.entrySet().iterator();
//	                if (itDiscEntry.hasNext())
	                if(listDisc.size()>0)
	                {
	                 clsPOSDiscountDtlsOnBill objBillDisc=listDisc.get(0);
//	                	Map.Entry<String, clsBillDiscountDtl> discEntry = itDiscEntry.next();
//	                    String key = discEntry.getKey();
	                    String discOnType = objBillDisc.getDiscountOnType();
	                    String discOnValue = objBillDisc.getDiscountOnValue();

	                    if (discOnType.equalsIgnoreCase("Total"))
	                    {
//	                        clsBillDiscountDtl objBillDiscDtl = mapBillDiscDtl.get(key);
	                        String remark = objBillDisc.getDiscountRemarks();
	                        String reason = objBillDisc.getDiscountReasonCode();

	                        double tempDiscAmt = 0, tempDiscPer = 0;
	                        List <clsPOSItemsDtlsInBill>listItemsDtlsInBill=bean.getListOfBillItemDtl();
	                        
	                        for (clsPOSItemsDtlsInBill objBillItemDtl:  listItemsDtlsInBill)
	                        {
	                        	if(!(objBillItemDtl.getItemCode().length()>0)||objBillItemDtl.getItemCode().equals(""))
	                        	{
	                        		break;
	                        	}
	                            
	                            tempDiscAmt += objBillItemDtl.getDiscountAmt() * objBillItemDtl.getQuantity();
	                            tempDiscPer = objBillItemDtl.getDiscountPer();
	                        }
	                        if (_subTotal > 0)
	                        {
	                            tempDiscPer = (tempDiscAmt * 100) / _subTotal;
	                        }
	                        dblDiscountAmt = tempDiscAmt;
	                        dblDiscountPer = tempDiscPer;
	                        clsBillDiscDtlModel objDiscModel= new clsBillDiscDtlModel();
//	                        objDiscModel.setStrBillNo(voucherNo);
	                        objDiscModel.setStrPOSCode(posCode);
	                        objDiscModel.setDblDiscAmt(tempDiscAmt);
	                        objDiscModel.setDblDiscPer(tempDiscPer);
	                        objDiscModel.setDblAmount(_subTotal);
	                        objDiscModel.setStrDiscOnType(discOnType);
	                        objDiscModel.setStrDiscOnValue(discOnValue);
	                        objDiscModel.setDteDateCreated(dteCuurentDate);
	                        objDiscModel.setDteDateEdited(dteCuurentDate);
	                        objDiscModel.setStrUserCreated(userCode);
	                        objDiscModel.setStrUserEdited(userCode);
//	                        objDiscModel.setStrClientCode(clientCode);
	                        objDiscModel.setStrDiscReasonCode(reason);
	                        objDiscModel.setStrDiscRemarks(remark);
//	                        objDiscModel.setDteBillDate(dteCuurentDate);
	                        objDiscModel.setStrDataPostFlag("N");
	                        listBillDiscDtlModel.add(objDiscModel);
//	                        objBaseService.funSave(objDiscModel);
	                        //	                        sqlBillDiscDtl.setLength(0);
//	                        sqlBillDiscDtl.append("insert into tblbilldiscdtl values ");
//	                        sqlBillDiscDtl.append("('" + voucherNo + "','" + pos + "','" + tempDiscAmt + "','" + tempDiscPer + "','" + _subTotal + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.gClientCode + "','N','" + clsGlobalVarClass.getPOSDateForTransaction() + "')");
//	                        //save total disc for bill series
//	                        clsGlobalVarClass.dbMysql.execute(sqlBillDiscDtl.toString());

	                    }
	                    else if (discOnType.equalsIgnoreCase("ItemWise"))
	                    {
//	                        itDiscEntry = mapBillDiscDtl.entrySet().iterator();
	                        double totalDiscAmt = 0.00, finalDiscPer = 0.00;
	                     

	                        for ( clsPOSDiscountDtlsOnBill objBillDiscDtl:listDisc)
	                        {
//	                            
	                            discOnType = objBillDiscDtl.getDiscountOnType();
	                            discOnValue = objBillDiscDtl.getDiscountOnValue();
	                            String remark = objBillDiscDtl.getDiscountRemarks();
	                            String reason = objBillDiscDtl.getDiscountReasonCode();

	                            for (clsPOSItemsDtlsInBill objItemDtl : bean.getListOfBillItemDtl())
	                            {
	                            	
	                            	if(!(objItemDtl.getItemCode().length()>0)||objItemDtl.getItemCode().equals(""))
	                            	{
	                            		break;
	                            	}
	                                if (objItemDtl.getItemName().equalsIgnoreCase(discOnValue))
	                                {
	                                    clsBillDiscDtlModel objDiscModel= new clsBillDiscDtlModel();
//	          	                        objDiscModel.setStrBillNo(voucherNo);
	          	                        objDiscModel.setStrPOSCode(posCode);
	          	                        objDiscModel.setDblDiscAmt(objBillDiscDtl.getDiscountAmt());
	          	                        objDiscModel.setDblDiscPer(objBillDiscDtl.getDiscountPer());
	          	                        objDiscModel.setDblAmount(objBillDiscDtl.getDiscountOnAmt());
	          	                        objDiscModel.setStrDiscOnType(discOnType);
	          	                        objDiscModel.setStrDiscOnValue(discOnValue);
	          	                        objDiscModel.setDteDateCreated(dteCuurentDate);
	          	                        objDiscModel.setDteDateEdited(dteCuurentDate);
	          	                        objDiscModel.setStrUserCreated(userCode);
	          	                        objDiscModel.setStrUserEdited(userCode);
//	          	                        objDiscModel.setStrClientCode(clientCode);
	          	                        objDiscModel.setStrDiscReasonCode(reason);
	          	                        objDiscModel.setStrDiscRemarks(remark);
//	          	                        objDiscModel.setDteBillDate(dteCuurentDate);
	          	                        objDiscModel.setStrDataPostFlag("N");
	          	                      listBillDiscDtlModel.add(objDiscModel);
//	          	                      objBaseService.funSave(objDiscModel);
	          	                        
//	                                    sqlBillDiscDtl.setLength(0);
//	                                    sqlBillDiscDtl.append("insert into tblbilldiscdtl values ");
//	                                    sqlBillDiscDtl.append("('" + voucherNo + "','" + clsGlobalVarClass.gPOSCode + "','" + objBillDiscDtl.getDiscAmt() + "','" + objBillDiscDtl.getDiscPer() + "','" + objBillDiscDtl.getDiscOnAmt() + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.gClientCode + "','N','" + clsGlobalVarClass.getPOSDateForTransaction() + "')");
//	                                    //save item wise disc for bill series
//	                                    clsGlobalVarClass.dbMysql.execute(sqlBillDiscDtl.toString());

	                                    totalDiscAmt += objBillDiscDtl.getDiscountAmt();
	                                }
	                            }
	                        }

	                        if (_subTotal == 0.00)
	                        {

	                        }
	                        else
	                        {
	                            finalDiscPer = (totalDiscAmt / _subTotal) * 100;
	                        }
	                        dblDiscountAmt = totalDiscAmt;
	                        dblDiscountPer = finalDiscPer;
	                    }
	                    else if (discOnType.equalsIgnoreCase("GroupWise"))
	                    {
	                    	
		                      
	                       double totalDiscAmt = 0.00, finalDiscPer = 0.00;
	                      

	                        for ( clsPOSDiscountDtlsOnBill objBillDiscDtl:listDisc)
	                        {
//	                            
	                            discOnType = objBillDiscDtl.getDiscountOnType();
	                            discOnValue = objBillDiscDtl.getDiscountOnValue();
	                            String remark = objBillDiscDtl.getDiscountRemarks();
	                            String reason = objBillDiscDtl.getDiscountReasonCode();

	                         
	                            double discPer = objBillDiscDtl.getDiscountPer();

	                            double discAmt = 0.00;
	                            double discOnAmt = 0.00;

	                            for (clsPOSItemsDtlsInBill objItemDtl : bean.getListOfBillItemDtl())
	                            {
	                            	if(!(objItemDtl.getItemCode().length()>0)||objItemDtl.getItemCode().equals(""))
	                            	{
	                            		break;
	                            	}
	                                sqlBillDiscDtl.setLength(0);
	                                sqlBillDiscDtl.append("select a.strItemCode  "
	                                        + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c "
	                                        + "where a.strSubGroupCode=b.strSubGroupCode and b.strGroupCode=c.strGroupCode  "
	                                        + "and a.strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
	                                        + "and c.strGroupName='" + discOnValue + "' ");

	                                List listItemDtl=objBaseServiceImpl.funGetList(sqlBillDiscDtl, "sql");
	                                if (listItemDtl.size()>0)
	                                {
	                                    discOnAmt += objItemDtl.getAmount();
	                                }
	                            }
	                            discAmt = (discPer / 100) * discOnAmt;

	                            totalDiscAmt += discAmt;
	                            if (discAmt > 0)
	                            {
	                            	
	                            	   clsBillDiscDtlModel objDiscModel= new clsBillDiscDtlModel();
//	          	                        objDiscModel.setStrBillNo(voucherNo);
	          	                        objDiscModel.setStrPOSCode(posCode);
	          	                        objDiscModel.setDblDiscAmt(objBillDiscDtl.getDiscountAmt());
	          	                        objDiscModel.setDblDiscPer(objBillDiscDtl.getDiscountPer());
	          	                        objDiscModel.setDblAmount(objBillDiscDtl.getDiscountOnAmt());
	          	                        objDiscModel.setStrDiscOnType(discOnType);
	          	                        objDiscModel.setStrDiscOnValue(discOnValue);
	          	                        objDiscModel.setDteDateCreated(dteCuurentDate);
	          	                        objDiscModel.setDteDateEdited(dteCuurentDate);
	          	                        objDiscModel.setStrUserCreated(userCode);
	          	                        objDiscModel.setStrUserEdited(userCode);
//	          	                        objDiscModel.setStrClientCode(clientCode);
	          	                        objDiscModel.setStrDiscReasonCode(reason);
	          	                        objDiscModel.setStrDiscRemarks(remark);
//	          	                        objDiscModel.setDteBillDate(dteCuurentDate);
	          	                        objDiscModel.setStrDataPostFlag("N");
	          	                      listBillDiscDtlModel.add(objDiscModel);
//	          	                      objBaseService.funSave(objDiscModel);
	          	                        
//	                                sqlBillDiscDtl.setLength(0);
//	                                sqlBillDiscDtl.append("insert into tblbilldiscdtl values ");
//	                                sqlBillDiscDtl.append("('" + voucherNo + "','" + clsGlobalVarClass.gPOSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.gClientCode + "','N','" + clsGlobalVarClass.getPOSDateForTransaction() + "')");
//	                                //save item wise disc for bill series
//	                                clsGlobalVarClass.dbMysql.execute(sqlBillDiscDtl.toString());
	                            }
	                        }
	                        if (_subTotal == 0.00)
	                        {

	                        }
	                        else
	                        {
	                            finalDiscPer = (totalDiscAmt / _subTotal) * 100;
	                        }
	                        dblDiscountAmt = totalDiscAmt;
	                        dblDiscountPer = finalDiscPer;
	                    }
	                    else if (discOnType.equalsIgnoreCase("SubGroupWise"))
	                    {
		                       double totalDiscAmt = 0.00, finalDiscPer = 0.00;
		                        for ( clsPOSDiscountDtlsOnBill objBillDiscDtl:listDisc)
		                        {
		                            discOnType = objBillDiscDtl.getDiscountOnType();
		                            discOnValue = objBillDiscDtl.getDiscountOnValue();
		                            String remark = objBillDiscDtl.getDiscountRemarks();
		                            String reason = objBillDiscDtl.getDiscountReasonCode();
	                                double discPer = objBillDiscDtl.getDiscountPer();

	                            double discAmt = 0.00;
	                            double discOnAmt = 0.00;

	                            for (clsPOSItemsDtlsInBill objItemDtl : bean.getListOfBillItemDtl())
	                            {
	                            	if(!(objItemDtl.getItemCode().length()>0)||objItemDtl.getItemCode().equals(""))
	                            	{
	                            		break;
	                            	}
	                                sqlBillDiscDtl.setLength(0);
	                                sqlBillDiscDtl.append("select a.strItemCode  "
	                                        + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c "
	                                        + "where a.strSubGroupCode=b.strSubGroupCode and b.strGroupCode=c.strGroupCode  "
	                                        + "and a.strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
	                                        + "and b.strSubGroupName='" + discOnValue + "' ");

	                                List listItemDtl=objBaseServiceImpl.funGetList(sqlBillDiscDtl, "sql");
	                                if (listItemDtl.size()>0)
	                                {
	                                    discOnAmt += objItemDtl.getAmount();
	                                }
	                            }
	                            discAmt = (discPer / 100) * discOnAmt;

	                            totalDiscAmt += discAmt;

	                            if (discAmt > 0)
	                            {
	                            	   clsBillDiscDtlModel objDiscModel= new clsBillDiscDtlModel();
//	          	                        objDiscModel.setStrBillNo(voucherNo);
	          	                        objDiscModel.setStrPOSCode(posCode);
	          	                        objDiscModel.setDblDiscAmt(objBillDiscDtl.getDiscountAmt());
	          	                        objDiscModel.setDblDiscPer(objBillDiscDtl.getDiscountPer());
	          	                        objDiscModel.setDblAmount(objBillDiscDtl.getDiscountOnAmt());
	          	                        objDiscModel.setStrDiscOnType(discOnType);
	          	                        objDiscModel.setStrDiscOnValue(discOnValue);
	          	                        objDiscModel.setDteDateCreated(dteCuurentDate);
	          	                        objDiscModel.setDteDateEdited(dteCuurentDate);
	          	                        objDiscModel.setStrUserCreated(userCode);
	          	                        objDiscModel.setStrUserEdited(userCode);
//	          	                        objDiscModel.setStrClientCode(clientCode);
	          	                        objDiscModel.setStrDiscReasonCode(reason);
	          	                        objDiscModel.setStrDiscRemarks(remark);
//	          	                        objDiscModel.setDteBillDate(dteCuurentDate);
	          	                        objDiscModel.setStrDataPostFlag("N");
	          	                      listBillDiscDtlModel.add(objDiscModel);
//	          	                      objBaseService.funSave(objDiscModel);
//	                                sqlBillDiscDtl.setLength(0);
//	                                sqlBillDiscDtl.append("insert into tblbilldiscdtl values ");
//	                                sqlBillDiscDtl.append("('" + voucherNo + "','" + clsGlobalVarClass.gPOSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.gUserCode + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.getCurrentDateTime() + "','" + clsGlobalVarClass.gClientCode + "','N','" + clsGlobalVarClass.getPOSDateForTransaction() + "')");
//	                                //save item wise disc for bill series
//	                                clsGlobalVarClass.dbMysql.execute(sqlBillDiscDtl.toString());
	                            }
	                        }
	                        if (_subTotal == 0.00)
	                        {

	                        }
	                        else
	                        {
	                            finalDiscPer = (totalDiscAmt / _subTotal) * 100;
	                        }
	                        dblDiscountAmt = totalDiscAmt;
	                        dblDiscountPer = finalDiscPer;
	                    }
	                }
	            }
	            catch (Exception e)
	            {
	                
	                e.printStackTrace();
	            }
	            finally{
	            	return listBillDiscDtlModel;
	            }
	           
	        }      
	        


	        private void funTruncateDebitCardTempTable()
	        {
	            try
	            {
	               
	                String sql="delete from tbldebitcardtabletemp where strTableNo='" + tableNo + "'";
	                objBaseServiceImpl.funExecuteUpdate(sql, "sql")  ;
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            }
	        }
	        
	        
	        private List funInsertBillSettlementDtlTable(List<clsPOSBillSettlementDtl> listObjBillSettlementDtl,String userCode,String dtCurrentDate) throws Exception
	        {
	            String sqlDelete = "delete from tblbillsettlementdtl where strBillNo='" + voucherNo + "'";
	            objBaseServiceImpl.funExecuteUpdate(sqlDelete,"sql");
                 List<clsBillSettlementDtlModel> listBillSettlementDtlModel=new ArrayList<clsBillSettlementDtlModel>();
	            for (clsPOSBillSettlementDtl objBillSettlementDtl : listObjBillSettlementDtl)
	            {
	            	clsBillSettlementDtlModel objSettleModel=new clsBillSettlementDtlModel();
//	            	objSettleModel.setStrBillNo( objBillSettlementDtl.getStrBillNo());
	            	objSettleModel.setStrSettlementCode( objBillSettlementDtl.getStrSettlementCode());
	            	objSettleModel.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt());
	            	objSettleModel.setDblPaidAmt( objBillSettlementDtl.getDblPaidAmt());
	            	objSettleModel.setStrExpiryDate( objBillSettlementDtl.getStrExpiryDate());
	            	objSettleModel.setStrCardName(objBillSettlementDtl.getStrCardName());
	            	objSettleModel.setStrRemark(objBillSettlementDtl.getStrRemark());
//	            	objSettleModel.setStrClientCode(objBillSettlementDtl.getStrClientCode());
	            	objSettleModel.setStrCustomerCode(objBillSettlementDtl.getStrCustomerCode());
	            	objSettleModel.setDblActualAmt( objBillSettlementDtl.getDblActualAmt());
	            	objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt() );
	            	objSettleModel.setStrGiftVoucherCode(objBillSettlementDtl.getStrGiftVoucherCode());
	            	objSettleModel.setStrDataPostFlag(objBillSettlementDtl.getStrDataPostFlag());
//	            	objSettleModel.setDteBillDate(dtCurrentDate);
	            	objSettleModel.setStrFolioNo("");
	            	objSettleModel.setStrRoomNo("");
	            	listBillSettlementDtlModel.add(objSettleModel);
//	            	objBaseService.funSave(objSettleModel);
	         
	            }
	            return listBillSettlementDtlModel;
//	            StringBuilder sb1 = new StringBuilder(sqlInsertBillSettlementDtl);
//	            int index1 = sb1.lastIndexOf(",");
//	            sqlInsertBillSettlementDtl = sb1.delete(index1, sb1.length()).toString();
	            
	        }
	        
	     // Function to save discount details in bill. tblbilldiscountdtl    
	        private List funSaveBillDiscountDetail(String voucherNo,clsPOSBillSettlementBean objBean,String date)
	        {
	        	List <clsBillDiscDtlModel>listBillDiscDtlModel=new ArrayList<clsBillDiscDtlModel>();
	            try
	            {
	                double totalDiscAmt = 0.00, finalDiscPer = 0.00;
	                for ( clsPOSDiscountDtlsOnBill objBillDiscDtl:objBean.getListDiscountDtlOnBill())
                    {
	                    String discOnType = objBillDiscDtl.getDiscountOnType();
	                    String discOnValue = objBillDiscDtl.getDiscountOnValue();
	                    String remark = objBillDiscDtl.getDiscountRemarks();
	                    String reason = objBillDiscDtl.getDiscountReasonCode();
	                    double discPer = objBillDiscDtl.getDiscountPer();
	                    double discAmt = objBillDiscDtl.getDiscountAmt();
	                    double discOnAmt = objBillDiscDtl.getDiscountOnAmt();

	              
	                    
	                    clsBillDiscDtlModel objDiscModel= new clsBillDiscDtlModel();
//                        objDiscModel.setStrBillNo(voucherNo);
                        objDiscModel.setStrPOSCode(posCode);
                        objDiscModel.setDblDiscAmt(discAmt);
                        objDiscModel.setDblDiscPer(discPer);
                        objDiscModel.setDblAmount(discOnAmt);
                        objDiscModel.setStrDiscOnType(discOnType);
                        objDiscModel.setStrDiscOnValue(discOnValue);
                        objDiscModel.setDteDateCreated(date);
                        objDiscModel.setDteDateEdited(date);
                        objDiscModel.setStrUserCreated(userCode);
                        objDiscModel.setStrUserEdited(userCode);
//                        objDiscModel.setStrClientCode(clientCode);
                        objDiscModel.setStrDiscReasonCode(reason);
                        objDiscModel.setStrDiscRemarks(remark);
//                        objDiscModel.setDteBillDate(posDate);
                        objDiscModel.setStrDataPostFlag("N");
                        listBillDiscDtlModel.add(objDiscModel);
	                    
//                        objBaseService.funSave(objDiscModel);
	                    
	                    totalDiscAmt += discAmt;
	                }

	                if (_subTotal == 0.00)
	                {
	                }
	                else
	                {
	                    finalDiscPer = (totalDiscAmt / _subTotal) * 100;
	                }
	                dblDiscountAmt = totalDiscAmt;
	                dblDiscountPer = finalDiscPer;
	            }
	            catch (Exception e)
	            {
	             
	                e.printStackTrace();
	            }
	            finally
	            {
	                return listBillDiscDtlModel;
	            }
	        }
      
	        
}