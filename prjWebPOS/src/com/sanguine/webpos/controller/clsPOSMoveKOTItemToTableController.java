package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSKOTItemdtlBean;
import com.sanguine.webpos.bean.clsPOSMoveKOTItemsToTableBean;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculation;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.model.clsMakeKOTHdModel;
import com.sanguine.webpos.model.clsMakeKOTModel_ID;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSMoveKOTItemToTableController {

	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;

	Map map=new HashMap();
	List openKOTList=new ArrayList();
	List openTableList=new ArrayList();
	@RequestMapping(value = "/frmPOSMoveKOTItemToTable", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSMoveKOTItemsToTableBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		
	    String loginPosCode=request.getSession().getAttribute("loginPOS").toString();
        String clientCode=request.getSession().getAttribute("gClientCode").toString();
		
		try
		{
			Map tableList = new HashMap<>();
			tableList.put("All", "Select Table");
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
	            + "where strPOSCode='" + loginPosCode + "' and strOperational='Y' order by intSequence");
			List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
			    tableList.put(Array.get(obj, 0), Array.get(obj, 1));
			}
		
			Map treeMap = new TreeMap<>(tableList);
			model.put("tableList",treeMap);
			
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
		            + " where strStatus='Occupied' and strPOSCode='"+loginPosCode+"' ");
			
			list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			
			 Map busyTblList = new HashMap<>();
			 busyTblList.put("All", "Select Table");
			 	if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
						busyTblList.put(Array.get(obj, 0), Array.get(obj, 1));
					}
		         }
		 model.put("busyTblList",busyTblList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return new ModelAndView("frmPOSMoveKOTItemToTable");
	}

	@RequestMapping(value = "/saveMoveKOTItemsToTable", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMoveKOTItemsToTableBean objBean,BindingResult result,HttpServletRequest req)
	{
		 String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		 String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
		 String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
		try
		{
			String strTableNo=objBean.getStrTableNo();
 		    List<clsPOSKOTItemdtlBean> list=objBean.getItemDtlList();
		    JSONArray jArrList = new JSONArray();
		    if(null!=list)
		    {
			    for(int i=0; i<list.size(); i++)
			    {
			    	clsPOSKOTItemdtlBean obj= new clsPOSKOTItemdtlBean();
			    	obj=(clsPOSKOTItemdtlBean)list.get(i);
			    	
			    		JSONObject jObjData = new JSONObject();
			    		jObjData=(JSONObject)map.get(obj.getStrItemCode());
			    		jObjData.remove("dblItemQuantity");
			    		jObjData.put("dblItemQuantity",obj.getDblItemQuantity());
			    		jObjData.remove("dblAmount");
			    		jObjData.put("dblAmount",obj.getDblAmount());
			    		jArrList.add(jObjData);
			    }
		    }
			    
		        String strPosCode=req.getSession().getAttribute("loginPOS").toString();
		        String posDate=req.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		        List<clsPOSItemDetailFrTaxBean> arrListItemDtl = new ArrayList<clsPOSItemDetailFrTaxBean>();
				 try{
					 double taxAmt = 0,subTotalAmt=0;
					 for (int i = 0; i < jArrList.size(); i++) 
						{
						    clsPOSItemDetailFrTaxBean objItemDtl=new clsPOSItemDetailFrTaxBean();
						    JSONObject jObj = (JSONObject) jArrList.get(i);
						    String itemCode=(String) jObj.get("strItemCode");
					    	String itemName=jObj.get("strItemName").toString();
					    	double itemAmt=Double.parseDouble(jObj.get("dblAmount").toString());
					    	subTotalAmt=subTotalAmt+itemAmt;
					    	objItemDtl.setItemCode(itemCode);
					    	objItemDtl.setItemName(itemName);
					    	objItemDtl.setAmount(itemAmt);
					    	objItemDtl.setDiscAmt(0);
					    	objItemDtl.setDiscPer(0);
					    	arrListItemDtl.add(objItemDtl);
						}   
					
					 List list1 = null;
					 if(arrListItemDtl.size()>0)
		             {
		                 String areaCode="";
		                 StringBuilder sqlBuilder = new StringBuilder();
		                 sqlBuilder.append("select strAreaCode from tbltablemaster where strTableNo='" + strTableNo + "' ");
		                 list1 = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

		     			 if (list1!=null)
		     			 {
		                     areaCode = (String)list1.get(0);
		                 }
		     			  clsPOSTaxCalculation objTaxCalculation=new clsPOSTaxCalculation();
		                 List<clsPOSTaxCalculationDtls> arrListTaxDtl = objUtilityController.funCalculateTax(arrListItemDtl, loginPosCode, posDate, areaCode, "DineIn", subTotalAmt, 0.0, "Make KOT", "Cash");
		                 for (clsPOSTaxCalculationDtls objTaxDtl : arrListTaxDtl)
		            
		                 {
		                     taxAmt += objTaxDtl.getTaxAmount();
		                 }
		                 arrListTaxDtl = null;
		             }
					  	String kotNo=funGenerateKOTNo();
			            double KOTAmt=0;
			            int cnt=0;
			            
			            for(int i=0; i<jArrList.size(); i++)
					    {
			            	String redeemAmt="";
			            	JSONObject jObj = new JSONObject();
					    	jObj=(JSONObject)jArrList.get(i);
					    	String itemCode=jObj.get("strItemCode").toString();
					    	String itemName=jObj.get("strItemName").toString();
					    	double itemQty=Double.parseDouble(jObj.get("dblItemQuantity").toString());
					    	double itemAmt=Double.parseDouble(jObj.get("dblAmount").toString());
					    	String waiterNo=jObj.get("strWaiterNo").toString();
					    	String createdDate=jObj.get("dteDateCreated").toString();
					    	String serialNo=jObj.get("strSerialNo").toString();
					    	
					    	clsMakeKOTHdModel objModel=new clsMakeKOTHdModel(new clsMakeKOTModel_ID(serialNo, strTableNo, itemCode, itemName, kotNo));
							   
					    		objModel.setDocCode(kotNo);
							    objModel.setStrActiveYN("");
							    objModel.setStrCardNo("");
							    objModel.setStrCardType(" ");
							    objModel.setStrCounterCode("");
							    objModel.setStrCustomerCode("");
							    objModel.setStrCustomerName("");
							    objModel.setStrDelBoyCode("");
							    objModel.setStrHomeDelivery("");
							    
							    objModel.setStrManualKOTNo(" ");
							    objModel.setStrNCKotYN("N");
							    objModel.setStrOrderBefore(" ");
							    objModel.setStrPOSCode(loginPosCode);
							    objModel.setStrPrintYN("Y");
							    objModel.setStrPromoCode(" ");
							    objModel.setStrReason("");
							    objModel.setStrWaiterNo(waiterNo);
							    objModel.setStrTakeAwayYesNo("");
							    objModel.setDblAmount(itemAmt);
							    objModel.setDblBalance(0.00);
							    objModel.setDblCreditLimit(0.00);
							    objModel.setDblItemQuantity(itemQty);
							    objModel.setDblRate(0.00);
							    objModel.setDblRedeemAmt(0);
							    objModel.setDblTaxAmt(taxAmt);
							    objModel.setIntId(0);
							    objModel.setIntPaxNo(1);
							    
							    objModel.setDteDateCreated(posDate);
							    objModel.setDteDateEdited(posDate);
							 
							    objModel.setStrUserCreated(webStockUserCode);
							    objModel.setStrUserEdited(webStockUserCode);
							    
							    objBaseServiceImpl.funSave(objModel);
					    	
			             }
			            if(taxAmt>0)
			            {
				              String sql="insert into tblkottaxdtl "
				            		  	+ "values ('"+strTableNo+"','"+kotNo+"',"+KOTAmt+","+taxAmt+")";
				            objBaseServiceImpl.funExecuteUpdate(sql, "sql");
			            }
			            funUpdateKOT(strTableNo,kotNo);
			            
			        }catch(Exception e)
			        {
			            e.printStackTrace();
			        }
			
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage","");

									
			return new ModelAndView("redirect:/frmPOSMoveKOTItemToTable.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	private void funUpdateKOT(String tempTableNO, String KOTNo) 
    {
	    try{
	          String sql = "update tbltablemaster set strStatus='Occupied' where strTableNo='" + tempTableNO + "'";
	         objBaseServiceImpl.funExecuteUpdate(sql, "sql");
	      } 
	      catch (Exception e){
	          e.printStackTrace();
	      } 
	      finally {
	          System.gc();
	      }
   }
	 
	@RequestMapping(value = "/loadOpenKOTsForMoveKOTItem", method = RequestMethod.GET)
	public @ResponseBody JSONObject loadKOTData(HttpServletRequest req)
	{
		String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		String tableNo=req.getParameter("tableNo");
		
		List list =null;
		JSONObject jObjKotData=new JSONObject();
		try{
		
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select distinct(strKOTNo),strTableNo from tblitemrtemp ");
            if(!tableNo.equals("All"))
            {
            	sqlBuilder.append(" where strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'");
            }
            else
            {  
            	sqlBuilder.append(" where strPOSCode='"+loginPosCode+"'");
            }
			
			list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			JSONArray jArrKOTData=new JSONArray();
			if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
			
					JSONObject objSettle=new JSONObject();
					objSettle.put("KOTNo",Array.get(obj, 0));
					objSettle.put("TableNo",Array.get(obj, 1));
					jArrKOTData.add(Array.get(obj, 0));
					map.put(Array.get(obj, 0),Array.get(obj, 1));
				}
	           
				jObjKotData.put("KOTList", jArrKOTData);
		      }
		  }catch(Exception ex)
		  {
				ex.printStackTrace();
		  }
			return jObjKotData;
	
	}

@RequestMapping(value = "/loadKOTItemsDtl", method = RequestMethod.GET)
public @ResponseBody JSONArray funGetKOTItemsDtl(HttpServletRequest req)
{
	String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
	String KOTNo=req.getParameter("KOTNo");
	String tableNo=(String)map.get(KOTNo);
	
	List list =null;
	JSONArray jArrData=new JSONArray();
	try{
	
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select count(*) from tblitemrtemp "
				  + "where strKOTNo='" + KOTNo + "' ");
        if(!tableNo.equals("All")){
        	sqlBuilder.append(" and strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'");                
        }
        else{
        	sqlBuilder.append( " and strPOSCode='" + loginPosCode + "' ");
        }
		list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		if (list!=null)
			{
			 	sqlBuilder.setLength(0);
			 	sqlBuilder.append("select strItemName,dblItemQuantity,dblAmount,strItemCode,strWaiterNo,dteDateCreated"
                        + " ,strSerialNo,dblRedeemAmt,strCustomerCode,strPOSCode,strTableNo "
                        + " from tblitemrtemp "
                        + " where strKOTNo='" + KOTNo + "' ");
                if(!tableNo.equals("All")){
                	sqlBuilder.append( " and strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'");                
                }
                else{
                	sqlBuilder.append(" and strPOSCode='" + loginPosCode + "' ");
                }
                sqlBuilder.append(" order by strSerialNo");
                
                	list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("strItemName",obj[0]);
						objSettle.put("dblItemQuantity",obj[1]);
						objSettle.put("dblAmount",obj[2]);
						objSettle.put("strItemCode",obj[3]);
						objSettle.put("strWaiterNo",obj[4]);
						objSettle.put("dteDateCreated",obj[5]);
						objSettle.put("strSerialNo",obj[6]);
						objSettle.put("dblRedeemAmt",obj[7]);
						objSettle.put("strCustomerCode",obj[8]);
						jArrData.add(objSettle);
					}
					for(int i=0; i<jArrData.size();i++)
			        {
						JSONObject jobj=(JSONObject) jArrData.get(i);
						map.put((String)jobj.get("strItemCode"),jobj);
				    }
		      }
		
           
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
	
        return jArrData;
}


@RequestMapping(value = "/getKOTItemMap", method = RequestMethod.POST)
public @ResponseBody String funGetKOTItemMap(HttpServletRequest req )
{
	String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
	Map arrKOTNo=req.getParameterMap();
	
	return loginPosCode;
}

private String funGenerateKOTNo()
{
    String kotNo = "";
    try
    {
    	BigInteger code = BigInteger.valueOf(0);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select dblLastNo from tblinternal where strTransactionType='KOTNo'");
        List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

		 	if (list!=null)
			{
				code = (BigInteger)list.get(0);
	            code = code.add(BigInteger.valueOf(1));
	            kotNo = "KT" + String.format("%07d", code);
	         }
		 	else
	        {
	            kotNo = "KT0000001";
	        }
       
		String sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'";
        objBaseServiceImpl.funExecuteUpdate(sql, "sql");

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return kotNo;
 }


}
