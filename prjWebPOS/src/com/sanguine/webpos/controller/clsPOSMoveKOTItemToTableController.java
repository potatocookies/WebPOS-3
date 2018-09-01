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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSKOTItemdtlBean;
import com.sanguine.webpos.bean.clsPOSMoveKOTItemsToTableBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculation;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.model.clsMakeKOTHdModel;
import com.sanguine.webpos.model.clsMakeKOTModel_ID;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSMoveKOTItemToTableController {

	
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
		try
		{
			String strTableNo=objBean.getStrTableNo();
 		    List<clsPOSKOTItemdtlBean> list=objBean.getItemDtlList();
		    List listData = new ArrayList();
		    if(null!=list)
		    {
			    for(int i=0; i<list.size(); i++)
			    {
			    	clsPOSKOTItemdtlBean obj= new clsPOSKOTItemdtlBean();
			    	obj=(clsPOSKOTItemdtlBean)list.get(i);
			    	
			    		Map mapObjData = new HashMap();
			    		mapObjData=(Map) map.get(obj.getStrItemCode());
			    		mapObjData.remove("dblItemQuantity");
			    		mapObjData.put("dblItemQuantity",obj.getDblItemQuantity());
			    		mapObjData.remove("dblAmount");
			    		mapObjData.put("dblAmount",obj.getDblAmount());
			    		listData.add(mapObjData);
			    }
		    }
			    
		        String strPosCode=req.getSession().getAttribute("loginPOS").toString();
		        String posDate=req.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		        List<clsPOSItemDetailFrTaxBean> arrListItemDtl = new ArrayList<clsPOSItemDetailFrTaxBean>();
				 try{
					 double taxAmt = 0,subTotalAmt=0;
					 for (int i = 0; i < listData.size(); i++) 
						{
						    clsPOSItemDetailFrTaxBean objItemDtl=new clsPOSItemDetailFrTaxBean();
						    Map mapObj = (Map) listData.get(i);
						    String itemCode=(String) mapObj.get("strItemCode");
					    	String itemName=mapObj.get("strItemName").toString();
					    	double itemAmt=Double.parseDouble(mapObj.get("dblAmount").toString());
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
			           
			            for(int i=0; i<listData.size(); i++)
					    {
			            	
			            	Map mapObj = new HashMap();
			            	mapObj=(Map)listData.get(i);
					    	String itemCode=mapObj.get("strItemCode").toString();
					    	String itemName=mapObj.get("strItemName").toString();
					    	double itemQty=Double.parseDouble(mapObj.get("dblItemQuantity").toString());
					    	double itemAmt=Double.parseDouble(mapObj.get("dblAmount").toString());
					    	String waiterNo=mapObj.get("strWaiterNo").toString();
					    	String serialNo=mapObj.get("strSerialNo").toString();
					    	
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
				              StringBuilder sqlBuilder=new StringBuilder("insert into tblkottaxdtl "
				            		  	+ "values ('"+strTableNo+"','"+kotNo+"',"+KOTAmt+","+taxAmt+")");
				            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
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
	          StringBuilder sqlBuilder = new StringBuilder("update tbltablemaster set strStatus='Occupied' where strTableNo='" + tempTableNO + "'");
	         objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
	      } 
	      catch (Exception e){
	          e.printStackTrace();
	      } 
	      finally {
	          System.gc();
	      }
   }
	 
	@RequestMapping(value = "/loadOpenKOTsForMoveKOTItem", method = RequestMethod.GET)
	public @ResponseBody Map loadKOTData(HttpServletRequest req)
	{
		String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		String tableNo=req.getParameter("tableNo");
		
		List list =null;
		Map mapObjKotData=new HashMap();
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
			List listKOTData=new ArrayList();
			if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
			
					Map mapObjSettle=new HashMap();
					mapObjSettle.put("KOTNo",Array.get(obj, 0));
					mapObjSettle.put("TableNo",Array.get(obj, 1));
					listKOTData.add(Array.get(obj, 0));
					map.put(Array.get(obj, 0),Array.get(obj, 1));
				}
	           
				mapObjKotData.put("KOTList", listKOTData);
		      }
		  }catch(Exception ex)
		  {
				ex.printStackTrace();
		  }
			return mapObjKotData;
	
	}

@RequestMapping(value = "/loadKOTItemsDtl", method = RequestMethod.GET)
public @ResponseBody List funGetKOTItemsDtl(HttpServletRequest req)
{
	String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
	String KOTNo=req.getParameter("KOTNo");
	String tableNo=(String)map.get(KOTNo);
	
	List list =null;
	List listData=new ArrayList();
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
					
				
						Map mapObjSettle=new HashMap();
						mapObjSettle.put("strItemName",obj[0]);
						mapObjSettle.put("dblItemQuantity",obj[1]);
						mapObjSettle.put("dblAmount",obj[2]);
						mapObjSettle.put("strItemCode",obj[3]);
						mapObjSettle.put("strWaiterNo",obj[4]);
						mapObjSettle.put("dteDateCreated",obj[5]);
						mapObjSettle.put("strSerialNo",obj[6]);
						mapObjSettle.put("dblRedeemAmt",obj[7]);
						mapObjSettle.put("strCustomerCode",obj[8]);
						listData.add(mapObjSettle);
					}
					for(int i=0; i<listData.size();i++)
			        {
						Map mapObj=(Map) listData.get(i);
						map.put((String)mapObj.get("strItemCode"),mapObj);
				    }
		      }
		
           
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
	
        return listData;
}


@RequestMapping(value = "/getKOTItemMap", method = RequestMethod.POST)
public @ResponseBody String funGetKOTItemMap(HttpServletRequest req )
{
	String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
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
		sqlBuilder.setLength(0);
		sqlBuilder.append("update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'");
        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return kotNo;
 }

@RequestMapping(value = "/funFillExistingKOTDetails", method = RequestMethod.GET)
public @ResponseBody Map funFillExistingKOTDetails(HttpServletRequest req)
{
	Map<String, Map<String, List<String>>> hmExistingKOTItemList = new HashMap<String, Map<String, List<String>>>();
	String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
	String tableNo=req.getParameter("tableNo");
	List listOfItem=new ArrayList();
	List list =null;
	Map mapObjKotData=new HashMap();
	try{
	
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strItemName,dblItemQuantity,dblAmount,strUserCreated,dteDateCreated, "
                + " strItemCode ,strPOSCode,strTableNo,strWaiterNo ,strKOTNo  "
                + " from tblitemrtemp  ");
        if(!tableNo.equals("All"))
        {
        	sqlBuilder.append(" where strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'");
        }
        else
        {  
        	sqlBuilder.append(" where strPOSCode='"+loginPosCode+"'");
        }
       
		list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		
		Map<String, List<String>> mapSelectedItemList=new HashMap();
		if (list!=null)
		{
			for(int i=0; i<list.size(); i++)
			{
				Object[] obj=(Object[])list.get(i);
				String itemCode = "", itemName = "", itemQty = "", itemAmt = "", waiterNo = "", kotNo = "", createdDate = "";
	            itemName = obj[0].toString();
	            itemQty = obj[1].toString();
	            itemAmt = obj[2].toString();
	            itemCode = obj[5].toString();
	            waiterNo = obj[8].toString();
	            kotNo = obj[9].toString();
	            createdDate = obj[4].toString();
	            if (hmExistingKOTItemList.containsKey(kotNo))
	            {
	            	mapSelectedItemList =  hmExistingKOTItemList.get(kotNo);
	                if (mapSelectedItemList.containsKey(itemCode))
	                {
	                	listOfItem = (List) mapSelectedItemList.get(itemCode);
	                }
	                else
	                {
	                	Map mapObjSettle=new HashMap();
						mapObjSettle.put("strItemName",obj[0]);
						mapObjSettle.put("dblItemQuantity",obj[1]);
						mapObjSettle.put("dblAmount",obj[2]);
						mapObjSettle.put("strItemCode",obj[5]);
						mapObjSettle.put("strWaiterNo",obj[8]);
						mapObjSettle.put("dteDateCreated",obj[4]);
						listOfItem.add(mapObjSettle);	
	                }
	            }
	            else
	            {
	                mapSelectedItemList = new HashMap<String, List<String>>();
	                Map mapObjSettle=new HashMap();
					mapObjSettle.put("strItemName",obj[0]);
					mapObjSettle.put("dblItemQuantity",obj[1]);
					mapObjSettle.put("dblAmount",obj[2]);
					mapObjSettle.put("strItemCode",obj[5]);
					mapObjSettle.put("strWaiterNo",obj[8]);
					mapObjSettle.put("dteDateCreated",obj[4]);
					listOfItem.add(mapObjSettle);	
	            }
	            mapSelectedItemList.put(itemCode, listOfItem);
	            hmExistingKOTItemList.put(kotNo, mapSelectedItemList);
	            
	      }
		}
		mapObjKotData.put("KOTList", mapSelectedItemList);
		
	  }catch(Exception ex)
	  {
			ex.printStackTrace();
	  }
		return mapObjKotData;

}


}
