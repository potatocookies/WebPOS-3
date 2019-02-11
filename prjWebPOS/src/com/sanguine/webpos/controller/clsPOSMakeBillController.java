package com.sanguine.webpos.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSMakeBillBean;
import com.sanguine.webpos.util.clsPOSSortMapOnValue;

@Controller
public class clsPOSMakeBillController {
	@Autowired
	private clsPOSGlobalFunctionsController	objPOSGlobal;
	@Autowired
	private clsGlobalFunctions	objGlobal;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	


	@RequestMapping(value = "/frmPOSMakeBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		clsPOSBillSettlementBean objBillSettlementBean = new clsPOSBillSettlementBean();
	
		
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("loginPOS").toString();
		String posDate=request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		

		// get and set menu item pricing for direct biller in json
		
	
		 JSONObject jObj =  funLoadTableDtl( clientCode, posCode);
		 JSONArray jsonArrForTableDtl =(JSONArray)jObj.get("tableDtl");	
		 String areaName=(String)jObj.get("areaName");	
		
		 objBillSettlementBean.setJsonArrForTableDtl(jsonArrForTableDtl);

		 model.put("areaName",areaName);
		 model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
		 model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		 model.put("gPrintType", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("PrintType"));
		 
		 String operationFrom = "Make Bill";
		 model.put("operationFrom", operationFrom);
			
		 String formToBeOpen="Make Bill";
		 model.put("formToBeOpen", formToBeOpen);
			
		 
		 return new ModelAndView("frmWebPOSBilling", "command", objBillSettlementBean);
		
		
	}
	
	@RequestMapping(value = "/funLoadTableForArea", method = RequestMethod.GET)
	public @ResponseBody JSONObject funLoadTableForArea(@RequestParam("areaCode") String areaCode,HttpServletRequest request)
	{
		String posCode = request.getSession().getAttribute("loginPOS").toString();
		JSONObject jObj = funLoadTableForArea( areaCode,  posCode);
		
		return jObj;
	}
	
	@RequestMapping(value = "/funFillItemTableDtl", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillItemTableDtl(@RequestParam("tableNo") String tableNo,HttpServletRequest request)
	{
	     String clientCode = request.getSession().getAttribute("gClientCode").toString();

		String posCode = request.getSession().getAttribute("loginPOS").toString();
		JSONObject jObj = funFillItemTableDtl( tableNo, posCode,clientCode);
		
		return jObj;
	}
	
	
	@SuppressWarnings("finally")
	public JSONObject funLoadTableDtl(String clientCode,String posCode)
	{
		List list =null;
		String    sql;
	   Map<String, Integer> hmTableSeq= new HashMap<String, Integer>();
		JSONObject jObjTableData=new JSONObject();
		StringBuilder sqlBuilder = new StringBuilder();
		try {

			sqlBuilder.setLength(0);
			sqlBuilder.append( " select a.strTableNo from tblitemrtemp a,tbltablemaster b  "
                    + " where a.strTableNo=b.strTableNo "
                    + " and a.strPosCode='" + posCode + "' and a.strNCKOTYN='N' and a.strTableNo!=null"
                    + " group by a.strTableNo ");
			list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj=(Object[])list.get(i);
					  String sqlUpdate = " update tbltablemaster set strStatus='Occupied' "
		                        + " where strTableNo='" + obj[0].toString() + "' ";   
					  objBaseServiceImpl.funExecuteUpdate(sqlUpdate, "sql");
			    }
			}
			String areaName="";
			sqlBuilder.setLength(0);
			sqlBuilder.append( "select strAreaCode from tblareamaster "
	                    + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
	                    + " and strAreaName='All' "
	                    + " order by strAreaCode");
				list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if (list.size()>0)
				{
					areaName="All";
				}
			  jObjTableData.put("areaName", areaName);
			  
				sqlBuilder.setLength(0);
			  	sqlBuilder.append("select strCMSIntegrationYN ,strTreatMemberAsTable from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
				List list1=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
				String gCMSIntegrationYN="";
				String gTreatMemberAsTable="";
				if(list1!=null && list1.size()>0)
				{				
					Object[] obj=(Object[])list1.get(0);
					gCMSIntegrationYN=obj[0].toString();
					gTreatMemberAsTable=obj[1].toString();
				}
    	    if(gCMSIntegrationYN.equalsIgnoreCase("Y"))
    	    {
        	    if(gTreatMemberAsTable.equalsIgnoreCase("Y"))
        	    {
        	    	sqlBuilder.setLength(0);
    			  	sqlBuilder.append( "select strTableNo,strTableName from tbltablemaster "
    	                          + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
    	                          + " and strOperational='Y'"
    	                          + " AND strStatus='Occupied' "
    	                          + " order by strTableName");
    	         }
    	              else
    	              {
    	            	  sqlBuilder.setLength(0);
    	  			  	sqlBuilder.append( "select strTableNo,strTableName,intSequence from tbltablemaster "
    	                          + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
    	                          + " and strOperational='Y' "
    	                          + " AND strStatus='Occupied' "
    	                          + " order by intSequence");
    	              }
    	    }
    	    else
            {
    	    	sqlBuilder.setLength(0);
			  	sqlBuilder.append( "select strTableNo,strTableName,intSequence from tbltablemaster "
                        + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' "
                        + " AND strStatus='Occupied' "
                        + " order by intSequence");
            }
			    
			
			list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
			 JSONArray jArrData=new JSONArray();
			
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						
						   hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int)obj[2]);
					        
				    }
				}
			 clsGlobalFunctions objGlobal=new clsGlobalFunctions();
			 clsPOSSortMapOnValue SortMapOnValue= new clsPOSSortMapOnValue();
			  hmTableSeq = SortMapOnValue.funSortMapOnValues(hmTableSeq);
			   Object[] arrObjTables = hmTableSeq.entrySet().toArray();
			   jArrData=new JSONArray();
	            for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
	            {
	                
	                if (cntTable == hmTableSeq.size())
	                {
	                    break;
	                }
	                String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
	                String tblNo = tblInfo.split("!")[0];
	                String tableName=tblInfo.split("!")[1];
	                sqlBuilder.setLength(0);
				  	sqlBuilder.append( "select strTableNo,strStatus,intPaxNo from tbltablemaster "
	                        + " where strTableNo='" + tblNo + "' "
	                        + " and strOperational='Y' "
	                        + " order by intSequence");
	               
	               
	    			list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    			 
	    			
	    			 if (list.size()>0)
	    				{
	    					
	    						Object[] obj=(Object[])list.get(0);
	    					
	    						JSONObject jobj=new JSONObject();
	    						   
	    						jobj.put("strTableName",tableName);
	    						jobj.put("strTableNo", obj[0].toString());
	    						jobj.put("strStatus", obj[1].toString());
	    						jobj.put("intPaxNo", obj[2].toString());
	    						jArrData.add(jobj);
	    				}
	            }
	            jObjTableData.put("tableDtl",jArrData);
	             
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	
	@SuppressWarnings("finally")
	public JSONObject funLoadTableForArea(String areaCode, String posCode) {
		List list =null;
		String    sql,areaName="";
	   Map<String, Integer> hmTableSeq= new HashMap<String, Integer>();
		JSONObject jObjTableData=new JSONObject();
		StringBuilder sqlBuilder = new StringBuilder();
		try {

			sqlBuilder.setLength(0);
			sqlBuilder.append("select strAreaName from tblareamaster where strAreaCode='" + areaCode + "'");
		list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		if (list.size()>0)
		{
			areaName=(String)list.get(0);
		}
		if(areaName.equalsIgnoreCase("All"))
	    {
			sqlBuilder.setLength(0);
			sqlBuilder.append( "select strTableNo,strTableName,intSequence from tbltablemaster "
                     + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
                     + " and strStatus='Occupied' and strOperational='Y' "
                     + " order by intSequence ");
	    }
	    else
        {
	    	sqlBuilder.setLength(0);
			sqlBuilder.append( "select strTableNo,strTableName,intSequence from tbltablemaster "
                     + " where strAreaCode='" + areaCode + "' "
                     + " and (strPOSCode='" + posCode + "' or strPOSCode='All') "
                     + " and strStatus='Occupied' and strOperational='Y' "
                     + " order by intSequence ");
        }
		    
		list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		 JSONArray jArrData=new JSONArray();
		
		 if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj=(Object[])list.get(i);
				
					
					   hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int)obj[2]);
				        
			    }
			}
		 
		 clsPOSSortMapOnValue SortMapOnValue= new clsPOSSortMapOnValue();
		  hmTableSeq = SortMapOnValue.funSortMapOnValues(hmTableSeq);
		   Object[] arrObjTables = hmTableSeq.entrySet().toArray();
		   jArrData=new JSONArray();
            for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
            {
                //System.out.println("Counter="+cntTable+"\tStart="+startIndex+"\tTotal Size="+totalSize);
                if (cntTable == hmTableSeq.size())
                {
                    break;
                }
                String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
                String tblNo = tblInfo.split("!")[0];
                String tableName=tblInfo.split("!")[1];
                sqlBuilder.setLength(0);
    			sqlBuilder.append( "select strTableNo,strStatus,intPaxNo from tbltablemaster "
                        + " where strTableNo='" + tblNo + "' "
                        + " and strOperational='Y' "
                        + " order by intSequence");
               
              
    			list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
    			 
    			
    			 if (list.size()>0)
    				{
    					
    						Object[] obj=(Object[])list.get(0);
    					
    						JSONObject jobj=new JSONObject();
    						   
    						jobj.put("strTableName",tableName);
    						jobj.put("strTableNo", obj[0].toString());
    						jobj.put("strStatus", obj[1].toString());
    						jobj.put("intPaxNo", obj[2].toString());
    						jArrData.add(jobj);
    				}
            }
            jObjTableData.put("tableDtl",jArrData);
             
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObjTableData;
		}
	}
	
	@SuppressWarnings("finally")
	public JSONObject funFillItemTableDtl(String tableNo,String posCode,String clientCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		StringBuilder sqlBuilder = new StringBuilder();
		try {

			sqlBuilder.setLength(0);
			sqlBuilder.append("select strItemName,sum(dblItemQuantity),sum(dblAmount),strWaiterNo,intPaxNo "
                    + " from tblitemrtemp "
                    + " where strPosCode='" + posCode + "' "
                    + " and strTableNo='" + tableNo + "' "
                    + " and strNCKOTYN='N' "
                    + " group by strItemCode "
                    + " order by strSerialNo ");
				 list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				 if (list.size()>0)
				 {
					 
					 JSONArray jArrData=new JSONArray();
							
					String strWaiterNo="";
					String intPaxNo="0";
					double total=0;
					 for(int i=0; i<list.size(); i++)
					 {
						 Object[] obj=(Object[])list.get(i);
						 JSONObject objSettle=new JSONObject();
						
						 objSettle.put("strItemName",obj[0].toString());
							 objSettle.put("dblItemQuantity",obj[1]);
							 objSettle.put("dblAmount",obj[2]);
							 
							 BigDecimal dblAmount=(BigDecimal)obj[2];
							 total=total+dblAmount.doubleValue();
							 strWaiterNo=obj[3].toString();
							 intPaxNo=obj[4].toString();
							
						
							 
			                 jArrData.add(objSettle);
						 
					 }
					 if ("null".equalsIgnoreCase(strWaiterNo))
		                {
						 jObjTableData.put("strWaiterName","");
		                }
					 else
					 {
						 sqlBuilder.setLength(0);
						 sqlBuilder.append("select strWShortName from tblwaitermaster where strWaiterNo='" + strWaiterNo + "'");
						 list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
						 if (list.size()>0)
						 {
							 jObjTableData.put("strWaiterName",list.get(0).toString());
						 }
					 }
					 jObjTableData.put("itemDtl",jArrData);
					 jObjTableData.put("intPaxNo",intPaxNo);
					 jObjTableData.put("total",total);
					

					 sqlBuilder.setLength(0);
					 sqlBuilder.append("select tm.strAreaCode from tbltablemaster tm where tm.strTableNo='"+tableNo+"' and tm.strClientCode='"+clientCode+"' ");
					 list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					 if (list.size()>0)
					 {
						 jObjTableData.put("strAreaCode",list.get(0).toString());
					 }

				 }		
					
			
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	
	
	
	

}
