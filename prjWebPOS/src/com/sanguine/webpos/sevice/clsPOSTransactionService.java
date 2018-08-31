package com.sanguine.webpos.sevice;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;
import com.sanguine.webpos.model.clsTableReservationModel;



@Service
public class clsPOSTransactionService 
{

	@Autowired
	private intfBaseService objBaseService;
	
	@Autowired
	private clsSetupService objSetupService;
	
	private boolean billPrintOnSettlement = false;
	
	//private clsUtilityFunctions objUtility;

	public List funGetKOTDtlForAddKOTTOBill(String posCode, String tableName)
	{
		
		List listRet = new ArrayList<>();
		
		try{
		
			StringBuilder sql = new StringBuilder(); 
			sql.append("select distinct(a.strKOTNo),a.strTableNo,b.strTableName "
	                + "from tblitemrtemp a,tbltablemaster b "
	                + "where a.strPOSCode='" + posCode + "' "
	                + "and a.strNCKOTYN='N'  "
	                + "and a.strTableNo=b.strTableNo ");
			if (!tableName.equalsIgnoreCase("All"))
	        {
	            sql.append("and b.strTableName like '%" + tableName + "%' ");
	        }

	        sql.append("order by a.strKOTNo ");
	        
			List list = objBaseService.funGetList(sql, "sql");
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[]) list.get(i);
						clsPOSBillDtl objBean=new clsPOSBillDtl();
						objBean.setStrKOTNo(obj[0].toString());
						objBean.setStrTableNo(obj[1].toString());
						objBean.setStrTableName(obj[2].toString());
						listRet.add(objBean);
					}
					
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return listRet;
			}
	}
	
	public List funGetUnsettleBillList(String posCode)
	{
		
		List listRet =new ArrayList<>();
		
		try{
		
			StringBuilder sql = new StringBuilder(); 
			sql.append("select strBillNo from tblbillhd"
	                + " where strBillNo not in (select strBillNo from tblbillsettlementdtl) and strTableNo<>'' "
	                + " and strOperationType='DineIn' and strPOSCode='" + posCode + "' ");
		
			List list = objBaseService.funGetList(sql, "sql");
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						//Object[] obj=(Object[])list.get(i);
						clsPOSBillDtl objBean = new clsPOSBillDtl();
						objBean.setStrBillNo((String) list.get(i));
						listRet.add(objBean);
					}
					
			     }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return listRet;
			}
	}
	

	
	

   public Map funGetCustomerHistory(String custCode,String fromDate,String toDate)
   {
	    StringBuilder sbSqlLiveBill=new StringBuilder();
        StringBuilder sbSqlQFileBill=new StringBuilder();
        StringBuilder sbSql=new StringBuilder();
        String strBillNo="";
		Map mainMapData=new HashMap<>();
		List mainArrListData=new ArrayList<>();
		List childList=new ArrayList();
		Map childMap=new HashMap();
		List list =null;
       try 
       {
       	   sbSqlLiveBill.setLength(0);
           sbSqlQFileBill.setLength(0);
           sbSql.setLength(0);
           sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate),DATE_FORMAT(a.dteBillDate,'%H:%i'),a.dblGrandTotal,b.strItemName,"
                    + "b.dblQuantity,b.dblAmount,b.strItemCode\n from tblbillhd a,tblbilldtl b "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode \n" +                          
                        "and a.strCustomerCode='"+custCode+"' \n" +
                        "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' ORDER BY date(a.dteBillDate) DESC");
            
            sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate),DATE_FORMAT(a.dteBillDate,'%H:%i'),a.dblGrandTotal,b.strItemName,"
                    + "b.dblQuantity,b.dblAmount,b.strItemCode\n from tblqbillhd a,tblqbilldtl b "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode \n" +                           
                        "and a.strCustomerCode='"+custCode+"' \n" +
                        "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' ORDER BY date(a.dteBillDate) DESC");
           
            //Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
 			//list = query.list();
            list = objBaseService.funGetList(sbSqlLiveBill, "sql");
 			if (list.size() > 0) 
 			{
 				int cnt=0;
 				
 				for(int i=0;i<list.size();i++)
 				{
 					Object[] obj=(Object[])list.get(i);
 					
 					if(!strBillNo.equals(obj[0].toString()))
                   { 
 						childMap.put("strBillNo",obj[0].toString());
 						childMap.put("billDate",obj[1].toString());
 						childMap.put("billTime",obj[2].toString());
 						childMap.put("grandTotal",obj[3]);
 						
 						cnt++;
 						Map jRow=new HashMap<>();
 						jRow.put("strItemName", obj[4].toString());
 						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
 						jRow.put("dblAmount", obj[6]);
 						jRow.put("strItemCode", obj[7].toString());
 						childList.add(jRow);
 						
 						sbSql.append("select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                + " from tblbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'");
 						//query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
 			  			//List modList = query.list();
 						List modList = objBaseService.funGetList(sbSql, "sql");
 			  			if (modList.size() > 0) 
 			  			{
 			  				for(int j=0;j<modList.size();j++)
 			  				{
 			  					Object[] obj1=(Object[])modList.get(j);
 			  					
 			  					jRow=new HashMap();
 			  					jRow.put("strItemName", obj1[0].toString());
 			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
 			  					jRow.put("dblAmount", obj1[3]);
 			  					jRow.put("strItemCode", obj1[4].toString());
 			  					childList.add(jRow);
 			  				}
 			  			}
 						
                   }
 					else
 					{
 						Map jRow=new HashMap();
 						jRow.put("strItemName", obj[4].toString());
 						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
 						jRow.put("dblAmount", obj[6]);
 						jRow.put("strItemCode", obj[7].toString());
 						childList.add(jRow);
 						
 						sbSql.append("select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                + " from tblbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'");
 						//query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
 			  			//List modList = query.list();
 						List modList = objBaseService.funGetList(sbSql, "sql");
 			  			if (modList.size() > 0) 
 			  			{
 			  				for(int j=0;j<modList.size();j++)
 			  				{
 			  					Object[] obj1=(Object[])modList.get(j);
 			  					
 			  					jRow=new HashMap();
 			  					jRow.put("strItemName", obj1[0].toString());
 			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
 			  					jRow.put("dblAmount", obj1[3]);
 			  					jRow.put("strItemCode", obj1[4].toString());
 			  					childList.add(jRow);
 			  				}
 			  			}
 					}
 					if((!strBillNo.equals(obj[0].toString())) && cnt>0)
                   {
 			  			childMap.put("billItemDtl", childList);
 			  		    mainArrListData.add(childMap);
 			  			childList=new ArrayList<>();
 			  			childMap=new HashMap();
                   }
 			  		strBillNo=obj[0].toString();
 				}
 			}
         
 			//query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
			//list = query.list();
 			list = objBaseService.funGetList(sbSqlQFileBill, "sql");
			if (list.size() > 0) 
			{
				boolean flag=false;
				String firstBill="";
				for(int i=0;i<list.size();i++)
				{
					Object[] obj=(Object[])list.get(i);
					if(strBillNo.equals(""))
							firstBill=obj[0].toString();
					if(!obj[0].toString().equals(firstBill)&& !flag)
                   {
 			  			childMap.put("billItemDtl", childList);
 			  			mainArrListData.add(childMap);
 			  			childList=new ArrayList<>();
 			  			childMap=new HashMap();
 			  			flag=true;
                   }
					else if((!strBillNo.equals(obj[0].toString())) && flag)
                   {
 			  			childMap.put("billItemDtl", childList);
 			  			mainArrListData.add(childMap);
 			  			childList=new ArrayList<>();
 			  			childMap=new HashMap();
                   }
					
					if(!strBillNo.equals(obj[0].toString()))
                  { 
						
						
						childMap.put("strBillNo",obj[0].toString());
						childMap.put("billDate",obj[1].toString());
						childMap.put("billTime",obj[2].toString());
						childMap.put("grandTotal",obj[3]);
						
						
						Map jRow=new HashMap<>();
						jRow.put("strItemName", obj[4].toString());
						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
						jRow.put("dblAmount", obj[6]);
						jRow.put("strItemCode", obj[7].toString());
						childList.add(jRow);
						
						sbSql.append("select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                + " from tblqbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'");
						//query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			  			//List modList = query.list();
			  			List modList = objBaseService.funGetList(sbSql, "sql");
			  			if (modList.size() > 0) 
			  			{
			  				for(int j=0;j<modList.size();j++)
			  				{
			  					Object[] obj1=(Object[])modList.get(j);
			  					
			  					jRow=new HashMap();
			  					jRow.put("strItemName", obj1[0].toString());
			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
			  					jRow.put("dblAmount", obj1[3]);
			  					jRow.put("strItemCode", obj1[4].toString());
			  					childList.add(jRow);
			  				}
			  			}
						
                  }
					else
					{
						Map jRow=new HashMap();
						jRow.put("strItemName", obj[4].toString());
						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
						jRow.put("dblAmount", obj[6]);
						jRow.put("strItemCode", obj[7].toString());
						childList.add(jRow);
						
						sbSql.append("select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                + " from tblqbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'");
						//query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			  			//List modList = query.list();
						List modList = objBaseService.funGetList(sbSql, "sql");
			  			if (modList.size() > 0) 
			  			{
			  				for(int j=0;j<modList.size();j++)
			  				{
			  					Object[] obj1=(Object[])modList.get(j);
			  					
			  					jRow=new HashMap();
			  					jRow.put("strItemName", obj1[0].toString());
			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
			  					jRow.put("dblAmount", obj1[3]);
			  					jRow.put("strItemCode", obj1[4].toString());
			  					childList.add(jRow);
			  				}
			  			}
			  		
			  		
					}
					
					
 					strBillNo=obj[0].toString();
									
				}
			}
			mainMapData.put("CustomerHistory",mainArrListData);
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

       return mainMapData;
   }
   
   
   public Map funFillAllTables(Map jObjCustomerHistoryFlash)
  	{
      	Map jObj=new HashMap();
      	try
      	{
      	String posCode=jObjCustomerHistoryFlash.get("posCode").toString();
      	String reportType=jObjCustomerHistoryFlash.get("reportType").toString();
      	String selectedTab=jObjCustomerHistoryFlash.get("selectedTab").toString();
      	String fromDate=jObjCustomerHistoryFlash.get("fromDate").toString();
      	String toDate=jObjCustomerHistoryFlash.get("toDate").toString();
      	
      	String webStockUserCode=jObjCustomerHistoryFlash.get("webStockUserCode").toString();
      	
      	
          if (selectedTab.equalsIgnoreCase("Customer Wise"))
          {
          	  String custCode=jObjCustomerHistoryFlash.get("custCode").toString();
                if (reportType.equalsIgnoreCase("Item Wise")) {
                   
              	  jObj = funCustomerWiseItemSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,webStockUserCode);
                } 
                else 
                {
                    
              	  jObj = funCustomerWiseBillSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,webStockUserCode);
                }
                
                						
            }
            if (selectedTab.equalsIgnoreCase("Top Spenders")) 
            {
          	  String cmbAmount=jObjCustomerHistoryFlash.get("cmbAmount").toString();
              String txtAmount=jObjCustomerHistoryFlash.get("txtAmount").toString();
          	  jObj = funTopSpenderWiseSales(posCode,fromDate,toDate,webStockUserCode,cmbAmount,txtAmount);
                
            }
            if (selectedTab.equalsIgnoreCase("Non Spenders")) 
            {
          	  jObj = funNonSpenderWiseSales(posCode,fromDate,toDate);
                
            }
      	}
      	catch(Exception e)
      	{
      		e.printStackTrace();
      	}
  		return jObj;
  		
  	}
   
   
   
   private Map funCustomerWiseItemSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String webStockUserCode) 
   {
   	   StringBuilder sbSqlLiveBill = new StringBuilder();
       StringBuilder sbSqlQFileBill = new StringBuilder();
       StringBuilder sbSqlFilters = new StringBuilder();
       List list =null;
       Map jObjCustomerWiseTblData=new HashMap();

       try 
       {
       	List jArrlistOfBillData=new ArrayList<>();
       	List jArrlistOfTotalData=new ArrayList<>();
        String sql = "";
           
           sbSqlLiveBill.setLength(0);
           sbSqlQFileBill.setLength(0);
           sbSqlFilters.setLength(0);

           sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate)"
                   + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                   + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
                   + "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
                   + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                   + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                   + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

           sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate)"
                   + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                   + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
                   + "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
                   + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                   + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                   + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

           if (!posCode.equals("All")) {
               sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
           }

           sbSqlFilters.append(" group by d.strItemName");

           boolean flgRecords = false;
           double qty = 0, amount = 0;
           double totalAmt = 0,totalAmt1 = 0;

           sbSqlLiveBill.append(sbSqlFilters);
           sbSqlQFileBill.append(sbSqlFilters);
          // Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
	      // list = query.list();
           list = objBaseService.funGetList(sbSqlLiveBill, "sql");
		   Map jobjTotal=new HashMap();
           if (list!=null)
			{
           	
	           for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					Map jobj=new HashMap();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("customerCode",Array.get(obj, 2));
					jobj.put("customerName",Array.get(obj, 3));
					jobj.put("itemName",Array.get(obj, 4));
					jobj.put("dblQuantity",Array.get(obj, 5));
					jobj.put("dblAmount",Array.get(obj, 6));
					jArrlistOfBillData.add(jobj);
					double grandTotal=Double.parseDouble(obj[6].toString());
                    totalAmt +=grandTotal ; // Grand Total    
               }
				
			}
			
           //query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());	
		   //List list1 = query.list();
           List list1 = objBaseService.funGetList(sbSqlQFileBill, "sql");
          
			if (list1!=null)
			{
				//Map jobjTotal=new Map();
				for(int i=0; i<list1.size(); i++)
				{
					Object[] obj = (Object[]) list1.get(i);
					
					
					Map jobj=new HashMap();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("customerCode",Array.get(obj, 2));
					jobj.put("customerName",Array.get(obj, 3));
					jobj.put("itemName",Array.get(obj, 4));
					jobj.put("dblQuantity",Array.get(obj, 5));
					jobj.put("dblAmount",Array.get(obj, 6));
					jArrlistOfBillData.add(jobj);
					double grandTotal=Double.parseDouble(obj[6].toString());

	                
	                totalAmt +=grandTotal ; // Grand Total   
	                
				}
				
			}
			jobjTotal.put("totAmt", totalAmt);
			jobjTotal.put("Total", "Total");
            jArrlistOfTotalData.add(jobjTotal);
           
//			jArrlistOfTotalData.put("Total");
			jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData);  
			jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData);  
			jObjCustomerWiseTblData.put("cmbName", "Item Wise"); 
			jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 

        
       } 
        catch (Exception e) {
           e.printStackTrace();
       } 
       finally {
           sbSqlLiveBill = null;
           sbSqlQFileBill = null;
           sbSqlFilters = null;
       }
		return jObjCustomerWiseTblData;
   }
	
   
  	
      
   /**
    * Top Spender Details
    */
   private Map funTopSpenderWiseSales(String posCode,String fromDate,String toDate,String webStockUserCode,String cmbAmount,String txtAmount) 
   {
       StringBuilder sbSqlLiveBill = new StringBuilder();
       StringBuilder sbSqlQFileBill = new StringBuilder();
       StringBuilder sbSqlFilters = new StringBuilder();
       Map jObjTopSpendersTblData=new HashMap();
       int colCount=4,rowCount=0,listSize=0;
       
       try {
           
           String sql = "";
           List jArrlistOfBillData=new ArrayList<>();
           List jArrlistOfTotalData=new ArrayList<>();
           
           sbSqlLiveBill.setLength(0);
           sbSqlQFileBill.setLength(0);
           sbSqlFilters.setLength(0);

           sbSqlLiveBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
                   + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
                   + "from tblbillhd a,tblcustomermaster b "
                   + "where a.strCustomerCode=b.strCustomerCode "
                   + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

           sbSqlQFileBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
                   + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
                   + "from tblqbillhd a,tblcustomermaster b "
                   + "where a.strCustomerCode=b.strCustomerCode "
                   + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

           if (!posCode.equals("All")) {
               sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
           }

           sbSqlFilters.append(" GROUP BY a.strBillNo");
           sbSqlFilters.append(" order by a.strBillNo desc");
           boolean flgRecords = false;
           double grandTotal = 0;
           double totalSettleAmt = 0;
           sbSqlLiveBill.append(sbSqlFilters);
           sbSqlQFileBill.append(sbSqlFilters);
           
           
           //Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
	      // List list = query.list();
           
           List list = objBaseService.funGetList(sbSqlLiveBill, "sql");
			
			Map jobjTot=new HashMap();
           if (list!=null)
			{
           	listSize=list.size();
           	rowCount = listSize;
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					Map jobj=new HashMap();
					
					int billNo=Integer.parseInt(obj[2].toString());
					jobj.put("strBillNo",billNo);
				
					double dblGrandTotal=Double.parseDouble(obj[3].toString());
					jobj.put("dblGrandTotal",dblGrandTotal);
				
					jobj.put("LongMobileNo",Array.get(obj, 0));
					
					jobj.put("StrCustomerName",Array.get(obj, 1));
					
//					jobj.put("strBillNo",Array.get(obj, 2));
//					jobj.put("dblGrandTotal",Array.get(obj, 3));
					jArrlistOfBillData.add(jobj);
          
					totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
					colCount++;
//					jobjTot.put("totalSettleAmt", totalSettleAmt);
//					jArrlistOfTotalData.put(jobjTot);
				}
			}
			 //query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
			// List list1 = query.list();
             
              List list1 = objBaseService.funGetList(sbSqlQFileBill, "sql");
           
		      if (list1!=null)
		      {
		    	  listSize=list1.size();
		    	  rowCount +=listSize;
		    	  for(int i=0; i<list1.size(); i++)
						{
							Object[] obj = (Object[]) list1.get(i);
							
							
							Map jobj=new HashMap();
							//Map jobjTot=new Map();
							
							int billNo=Integer.parseInt(obj[2].toString());
							jobj.put("strBillNo",billNo);
						
							double dblGrandTotal=Double.parseDouble(obj[3].toString());
							jobj.put("dblGrandTotal",dblGrandTotal);
							
							jobj.put("LongMobileNo",Array.get(obj, 0));
							
							jobj.put("StrCustomerName",Array.get(obj, 1));
						
//							jobj.put("StrBillNo",Array.get(obj, 2));
//							jobj.put("DblGrandTotal",Array.get(obj, 3));
							jArrlistOfBillData.add(jobj);
		           
							totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
							
		            }	
				
				}
		      jobjTot.put("totalSettleAmt", totalSettleAmt);
		      jobjTot.put("Total", "Total");
			  jArrlistOfTotalData.add(jobjTot);
//		      jArrlistOfTotalData.put("Total");
		      jObjTopSpendersTblData.put("TopSpendersTblData", jArrlistOfBillData);
		      jObjTopSpendersTblData.put("TotalTblData", jArrlistOfTotalData);
		      jObjTopSpendersTblData.put("tabName", "Top Spenders"); 
		      jObjTopSpendersTblData.put("Col Count", colCount);
		      jObjTopSpendersTblData.put("Row Count", rowCount);
          
       } 
       catch (Exception e) 
       {
           e.printStackTrace();
       } 
       finally
       {
           sbSqlLiveBill = null;
           sbSqlQFileBill = null;
           sbSqlFilters = null;
       }
		return jObjTopSpendersTblData;
   }

   /**
    * Non-Spender Details
    */
   private Map funNonSpenderWiseSales(String posCode,String fromDate,String toDate) 
   {
       StringBuilder sbSqlLiveBill = new StringBuilder();
       StringBuilder sbSqlQFileBill = new StringBuilder();
       StringBuilder sbSqlFilters = new StringBuilder();
       Map jObjNonSpendersTblData=new HashMap();
       int colCount=3,rowCount=0;
       
       try {
           
           String sql = "";
           List jArrlistOfBillData=new ArrayList<>();
           
           sbSqlLiveBill.setLength(0);
           sbSqlQFileBill.setLength(0);
           sbSqlFilters.setLength(0);


           sbSqlLiveBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                   + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                   + "FROM tblbillhd a,tblcustomermaster b\n"
                   + "WHERE a.strCustomerCode=b.strCustomerCode "
                   + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                   + "AND a.dblGrandTotal=0.00");

           sbSqlQFileBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                   + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                   + "FROM tblqbillhd a,tblcustomermaster b\n"
                   + "WHERE a.strCustomerCode=b.strCustomerCode "
                   + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                   + "AND a.dblGrandTotal=0.00");

           if (!posCode.equals("All")) {
               sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
           }

           sbSqlFilters.append(" GROUP BY b.strCustomerCode");
           sbSqlFilters.append(" order by DATE(a.dteBillDate) desc");
           boolean flgRecords = false;
           double grandTotal = 0;

           sbSqlLiveBill.append(sbSqlFilters);
           sbSqlQFileBill.append(sbSqlFilters);

          // Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
		  //List list = query.list();
		   List list = objBaseService.funGetList(sbSqlLiveBill, "sql");
		   
           if (list!=null)
			{
           	rowCount=list.size();
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					Map jobj=new HashMap();
					
					jobj.put("longMobileNo",Array.get(obj, 0));
				
					jobj.put("strCustomerName",Array.get(obj, 1));
				
					jobj.put("strBillNo",Array.get(obj, 2));
				
					jobj.put("dblGrandTotal",Array.get(obj, 3));
					
					jobj.put("dteBillDate",Array.get(obj, 4));
				
					jArrlistOfBillData.add(jobj);
					
					
				}
			}
           
            //query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());	
			//List list1 = query.list();
           List list1 = objBaseService.funGetList(sbSqlQFileBill, "sql");
           if (list1!=null)
			{
           	rowCount=list1.size();
           	for(int i=0; i<list1.size(); i++)
				{
					Object[] obj = (Object[]) list1.get(i);
					
					
					Map jobj=new HashMap();
					
					jobj.put("longMobileNo",Array.get(obj, 0));
					
					jobj.put("strCustomerName",Array.get(obj, 1));
					
					jobj.put("strBillNo",Array.get(obj, 2));
					jobj.put("dblGrandTotal",Array.get(obj, 3));
					jobj.put("dteBillDate",Array.get(obj, 4));
					
					jArrlistOfBillData.add(jobj);
					
  				}
			}
           jObjNonSpendersTblData.put("NonSpendersTblData", jArrlistOfBillData);
           jObjNonSpendersTblData.put("tabName", "Non Spenders"); 
           jObjNonSpendersTblData.put("Col Count", colCount);
           jObjNonSpendersTblData.put("Row Count", rowCount);
       } 
       catch (Exception e) 
       {
           e.printStackTrace();
       } 
       finally 
       {
           sbSqlLiveBill = null;
           sbSqlQFileBill = null;
           sbSqlFilters = null;
       }
		return jObjNonSpendersTblData;
   }
	
   
   
   private Map funCustomerWiseBillSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String webStockUserCode) {
   	StringBuilder sbSqlLiveBill = new StringBuilder();
       StringBuilder sbSqlQFileBill = new StringBuilder();
       StringBuilder sbSqlFilters = new StringBuilder();
       Map jObjCustomerWiseTblData=new HashMap();
       
       try {
          
           String sql = "";
           List jArrlistOfBillData=new ArrayList<>();
           List jArrlistOfTotalData=new ArrayList<>();

           sbSqlLiveBill.setLength(0);
           sbSqlQFileBill.setLength(0);
           sbSqlFilters.setLength(0);

           	sbSqlLiveBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                   + " ,f.strPOSName"
                   + ", ifnull(d.strSettelmentDesc,'') as payMode"
                   + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                   + " ,ifnull(c.dblSettlementAmt,0.00)"
                   + " ,ifnull(c.strRemark,'')"
                   + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                   + " from tblbillhd  a "
                   + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                   + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                   + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                   + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                   + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                   + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + " AND a.strCustomerCode='" + custCode + "'");

           	sbSqlQFileBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                   + " ,f.strPOSName"
                   + ", ifnull(d.strSettelmentDesc,'') as payMode"
                   + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                   + " ,ifnull(c.dblSettlementAmt,0.00)"
                   + " ,ifnull(c.strRemark,'')"
                   + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                   + " from tblqbillhd a "
                   + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                   + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                   + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                   + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                   + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                   + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                   + " AND a.strCustomerCode='" + custCode + "'");

           if (!posCode.equals("All")) {
               sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
           }

           // sbSqlFilters.append(" GROUP BY b.strCustomerCode");
           sbSqlFilters.append(" order by a.strBillNo desc ");
           boolean flgRecords = false;
           double grandTotal = 0;
           double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

           sbSqlLiveBill.append(sbSqlFilters);
           sbSqlQFileBill.append(sbSqlFilters);
          
           //Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());	
		   //List list = query.list();
		   List list = objBaseService.funGetList(sbSqlLiveBill, "sql");
		   
			Map jobjTot=new HashMap();
           if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					//Map jobjTot=new Map();
					Map jobj=new HashMap();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("dblDateCreated",Array.get(obj, 2));
					jobj.put("posName",Array.get(obj, 3));
					jobj.put("settelmentDesc",Array.get(obj, 4));
					jobj.put("dblSubTotal",Array.get(obj, 5));
					jobj.put("dblDiscountPer",Array.get(obj, 6));
					jobj.put("dblDiscountAmt",Array.get(obj, 7));
					jobj.put("dblTaxAmt",Array.get(obj, 8));
					jobj.put("dblSettlementAmt",Array.get(obj, 9));
					jobj.put("strRemark",Array.get(obj, 10));
					jobj.put("dblTipAmount",Array.get(obj, 11));
					jobj.put("strDiscountRemark",Array.get(obj, 12));
					jobj.put("strReasonName",Array.get(obj, 13));
					jArrlistOfBillData.add(jobj);
					double grandTot=Double.parseDouble(obj[7].toString());

               
					 totalDiscAmt +=grandTot ; // Grand Total     
					 totalSubTotal += Double.parseDouble(obj[5].toString());
		                totalTaxAmt += Double.parseDouble(obj[8].toString());
		                totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
		                totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt
		                
		               
           }
				 
			}

		// query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
		 //List list1 = query.list();
           List list1 = objBaseService.funGetList(sbSqlQFileBill, "sql");
	            if (list1!=null)
				{
					for(int i=0; i<list1.size(); i++)
					{
						Object[] obj = (Object[]) list1.get(i);
						
						//Map jobjTot=new Map();
						Map jobj=new HashMap();
						
						jobj.put("billNo",Array.get(obj, 0));
						jobj.put("billDate",Array.get(obj, 1));
						jobj.put("dblDateCreated",Array.get(obj, 2));
						jobj.put("posName",Array.get(obj, 3));
						jobj.put("settelmentDesc",Array.get(obj, 4));
						jobj.put("dblSubTotal",Array.get(obj, 5));
						jobj.put("dblDiscountPer",Array.get(obj, 6));
						jobj.put("dblDiscountAmt",Array.get(obj, 7));
						jobj.put("dblTaxAmt",Array.get(obj, 8));
						jobj.put("dblSettlementAmt",Array.get(obj, 9));
						jobj.put("strRemark",Array.get(obj, 10));
						jobj.put("dblTipAmount",Array.get(obj, 11));
						jobj.put("strDiscountRemark",Array.get(obj, 12));
						jobj.put("strReasonName",Array.get(obj, 13));
						jArrlistOfBillData.add(jobj);
						double grandTot=Double.parseDouble(obj[7].toString());

	                
						 totalDiscAmt +=grandTot ; // Grand Total     
						 totalSubTotal += Double.parseDouble(obj[5].toString());
			                totalTaxAmt += Double.parseDouble(obj[8].toString());
			                totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
			                totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt

			                
					}
				}
	            jobjTot.put("Total", "Total");
	            jobjTot.put("totalSubTotal", totalSubTotal);
	            jobjTot.put("blank", "");
               
               jobjTot.put("totalDiscAmt", totalDiscAmt);
               
               jobjTot.put("totalTaxAmt", totalTaxAmt);
               jobjTot.put("totalSettleAmt", totalSettleAmt);
               jobjTot.put("totalTipAmt", totalTipAmt);
               
               jArrlistOfTotalData.add(jobjTot);
           
//	            jArrlistOfTotalData.put("Total");
//	            jArrlistOfTotalData.put("");
	            jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData); 
	            jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData); 
	            jObjCustomerWiseTblData.put("cmbName", "Bill Wise"); 
	            jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 


       } 
       catch (Exception e) {
           e.printStackTrace();
       } 
       finally {
           sbSqlLiveBill = null;
           sbSqlQFileBill = null;
           sbSqlFilters = null;
       }
		return jObjCustomerWiseTblData;
   }

   
   
	@SuppressWarnings("finally")
	public Map funGetSettleBillDtlData(String clientCode,String posCode,String posDate)
	{
		List list =null;
		Map mapData=new HashMap<>();
		try
		{
			StringBuilder sql = new StringBuilder();

		   Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType"); 
		   if (objSetupParameter.get("gShowBillsType").toString().equalsIgnoreCase("Table Detail Wise"))
           {
			   sql.append("select a.strBillNo,ifnull(b.strTableNo,''),ifnull(b.strTableName,''),ifnull(c.strWaiterNo,'') "
                   + " ,ifnull(c.strWShortName,''),ifnull(d.strCustomerCode,''),ifnull(d.strCustomerName,''),a.dblGrandTotal "
                   + " ,DATE_FORMAT(a.dteBillDate,'%h:%i:%s')  "
                   + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                   + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
                   + " left outer join tblcustomermaster d on a.strCustomerCode=d.strCustomerCode "
                   + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) "
                   + " and date(a.dteBillDate)='" + posDate+ "' "
                   + " and a.strPOSCode='" +posCode+ "' ");
           }
			else
			{
				sql.append("SELECT a.strBillNo,IFNULL(d.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(f.strDPName,'')"
	                        + " ,a.dblGrandTotal,ifnull(g.strTableNo,''),ifnull(g.strTableName,''),DATE_FORMAT(a.dteBillDate,'%h:%i:%s') "
	                        + " FROM tblbillhd a "
	                        + " left outer join tblhomedeldtl b on a.strBillNo=b.strBillNo "
	                        + " LEFT OUTER JOIN tblcustomermaster d ON a.strCustomerCode=d.strCustomerCode "
	                        + " left outer join tblbuildingmaster e on d.strBuldingCode=e.strBuildingCode "
	                        + " left outer join tbldeliverypersonmaster  f on  f.strDPCode=b.strDPCode "
	                        + " left outer join tbltablemaster g on a.strTableNo=g.strTableNo "
	                        + " WHERE a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) "
	                        + " AND DATE(a.dteBillDate)='" + posDate+ "' "
	                        + " AND a.strPOSCode='" + posCode + "' "
	                        + " group by a.strBillNo");
			}
			
			
			//Query rsPendingBills = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			//list = rsPendingBills.list();
			 list = objBaseService.funGetList(sql, "sql");
			
			Object[] obj=null ;
			List listData=new ArrayList<>();
			if (objSetupParameter.get("gShowBillsType").toString().equalsIgnoreCase("Table Detail Wise"))
            {
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						obj = (Object[]) list.get(i);
						Map objDeliverBoy=new HashMap<>();
	
						objDeliverBoy.put("strBillNo",Array.get(obj, 0));
						objDeliverBoy.put("strTableName",Array.get(obj, 2));
						objDeliverBoy.put("strWShortName",Array.get(obj, 4));
						objDeliverBoy.put("strTableNo",Array.get(obj, 1));
						objDeliverBoy.put("strCustomerName",Array.get(obj, 6));
						objDeliverBoy.put("dteBillDate",Array.get(obj, 8));
						objDeliverBoy.put("dblGrandTotal",Array.get(obj, 7));
						listData.add(objDeliverBoy);
					}
					mapData.put("UnsettleBillDtl", listData);
					mapData.put("DataType", "TableDetailWise");
				}
			 
            }
			 else
			 {
				 if (list!=null)
					{
						for(int i=0; i<list.size(); i++)
						{
							obj = (Object[]) list.get(i);
							Map objDeliverBoy=new HashMap();	
							objDeliverBoy.put("strBillNo",Array.get(obj, 0));
							objDeliverBoy.put("strTableName",Array.get(obj, 6));
							objDeliverBoy.put("strTableNo",Array.get(obj, 5));
							objDeliverBoy.put("strCustomerName",Array.get(obj, 1));
							objDeliverBoy.put("strBuildingName",Array.get(obj, 2));
							objDeliverBoy.put("strDPName",Array.get(obj, 3));
							objDeliverBoy.put("dteBillDate",Array.get(obj, 7));
							objDeliverBoy.put("dblGrandTotal",Array.get(obj, 4));
					
							listData.add(objDeliverBoy);
						}
						mapData.put("UnsettleBillDtl", listData);
						mapData.put("DataType", "DeliveryBoyDetailWise");
					}
			 }
			 
			//rsPendingBills = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
		}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return mapData;
			}
	}
	
	
	
	public void funSettleBills(Map mapSettleBills)
	{
	 try	
	 {
		Map hmCashSettlementDtl;
		String billNo="";
		String dblSettleAmt="";
		String tableName="";
		String tableNo="";
			
		String posDate = mapSettleBills.get("POSDate").toString();
		String clientCode = mapSettleBills.get("ClientCode").toString();
		String userCode = mapSettleBills.get("User").toString();
		String posCode = mapSettleBills.get("POSCode").toString();
		
		List unsettleBillDtl=(List)mapSettleBills.get("UnsettleBillDetails");
        for (int i = 0; i < unsettleBillDtl.size(); i++) 
        { 
        	Map childJSONObject = (Map) unsettleBillDtl.get(i);
        	billNo= childJSONObject.get("BillNo").toString();
        	dblSettleAmt=childJSONObject.get("dblSettleAmt").toString();
        	tableName=childJSONObject.get("TableName").toString();
        	tableNo=childJSONObject.get("TableNo").toString();
        }
	     
        StringBuilder sbSql=new StringBuilder();
			
        sbSql.append("select a.strSettelmentCode,a.strSettelmentDesc,a.strBillPrintOnSettlement "
                + "from tblsettelmenthd a "
                + "where a.strSettelmentType='Cash' "
                + "limit 1;");
		//Query rsCashSttlementDtl1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(rsCashSttlementDtl);
		//List list = rsCashSttlementDtl1.list();
		List list = objBaseService.funGetList(sbSql, "sql");
		String cashSettlementCode=null;
		String strExpiryDate="";
		String strCardName="";
		String strRemark="Multi Bill Settle";
		String strCustomerCode="";
		String dblRefundAmt="0.00";
		String strGiftVoucherCode="";
		String strDataPostFlag="N";
		if (list!=null)
		{
			for(int i=0; i<list.size(); i++)
			{
				Object[] obj = (Object[]) list.get(i);
				cashSettlementCode = obj[0].toString();
				String strBillPrintOnSettlement = obj[2].toString();
				
				 hmCashSettlementDtl=new HashMap<>();	
				
				hmCashSettlementDtl.put("cashSettlementCode",Array.get(obj, 0));
				hmCashSettlementDtl.put("cashSettlementName",Array.get(obj, 1));
				if (strBillPrintOnSettlement.toString().equalsIgnoreCase("Y"))
                {
                    billPrintOnSettlement = true;
                }
                else
                {
                    billPrintOnSettlement = false;
                }
        }
		}
		String sqlDelete = "delete from tblbillsettlementdtl where strBillNo='" + billNo + "'";
		//Query q1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete);
		
		objBaseService.funExecuteUpdate(sqlDelete, "sql");
       
        String sqlInsertBillSettlementDtl = "insert into tblbillsettlementdtl"
                + "(strBillNo,strSettlementCode,dblSettlementAmt,dblPaidAmt,strExpiryDate"
                + ",strCardName,strRemark,strClientCode,strCustomerCode,dblActualAmt"
                + ",dblRefundAmt,strGiftVoucherCode,strDataPostFlag,dteBillDate) "
                + "values ";
        if(list!=null)
        {
        	for (int i = 0; i < list.size(); i++) 
        	{
        		sqlInsertBillSettlementDtl += "('" + billNo + "'"
                        + ",'" + cashSettlementCode + "'," + dblSettleAmt + ""
                        + "," + dblSettleAmt + ",'" + strExpiryDate + "'"
                        + ",'" + strCardName + "','" + strRemark + "'"
                        + ",'" + clientCode + "','" + strCustomerCode + "'"
                        + "," + dblSettleAmt + "," + dblRefundAmt + ""
                        + ",'" + strGiftVoucherCode + "','" + strDataPostFlag + "','"+posDate+"'),";
       	}
		}
        StringBuilder sb1 = new StringBuilder(sqlInsertBillSettlementDtl);
        int index1 = sb1.lastIndexOf(",");
        sqlInsertBillSettlementDtl = sb1.delete(index1, sb1.length()).toString();
        //Query q2 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertBillSettlementDtl);
        //q2.executeUpdate();
        objBaseService.funExecuteUpdate(sqlInsertBillSettlementDtl, "sql");
        
        String sql = "update tblbillhd set strSettelmentMode='CASH' "
                + ",strUserEdited='" + userCode + "', dteSettleDate='" + posDate + "' "
                + ",strRemarks='Multi Bill Settle' "
                + "where strBillNo='" + billNo + "'";
       // Query qUpdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
       // qUpdate.executeUpdate();
        objBaseService.funExecuteUpdate(sql, "sql");
        
        if (!tableNo.isEmpty() && !tableName.isEmpty())
        {
        	String tableStatus = "Normal";
        	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        	 sbSql.setLength(0);
        	 sbSql.append("select a.strCustomerCode,CONCAT(a.tmeResTime,' ',a.strAMPM) as reservationtime from tblreservation a "
	                    + " where a.strTableNo='" + tableNo + "' "
	                    + " and date(a.dteResDate)='" + posDate + "' "
	                    + " order by a.strResCode desc "
	                    + " limit 1 ");
	                   //Query rsReserve = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDate) ;
	            	   //List listReserve = rsReserve.list();
        	 
        	 			List listReserve = objBaseService.funGetList(sbSql, "sql");
	            		
	            		if(listReserve!=null)
	                    {
	                    	for (int i = 0; i < listReserve.size(); i++) 
	                    	{
	                    		Object[] obj = (Object[]) list.get(i);
	                    		 hmCashSettlementDtl=new HashMap<>();	
	                    		 String res=(String) listReserve.get(1);
	                    		 Date reservationDateTime = simpleDateFormat.parse(res);
	                             Date posDateTime = new Date();
	                             String strPOSTime = String.format("%tr", posDateTime);
	                             posDateTime = simpleDateFormat.parse(strPOSTime);

	                             if (posDateTime.getTime() > reservationDateTime.getTime())
	                             {
	                                 tableStatus = "Normal";
	                             }
	                             else
	                             {
	                                 tableStatus = "Reserve";
	                             }
		                    }
		                }
	            		 Query rsCount =null;
	            		String sql_updateTableStatus = "";
	            		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSIntegrationYN"); 
	  	        	    if(objSetupParameter.get("gInrestoPOSIntegrationYN").equals("Y"))
	  	        	    {
	                         if (tableStatus.equalsIgnoreCase("Reserve"))
	                         {
	                        	 tableStatus = "Normal";
	                         }
	                     }
	  	        	  if ("Normal".equalsIgnoreCase(tableStatus))
	  	              {
	  	                sql_updateTableStatus = "select count(*) from tblitemrtemp where strTableNo='" + tableNo + "';";
	  	               // rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                objBaseService.funExecuteUpdate(sql_updateTableStatus, "sql");
	  	                List listCount = rsCount.list();
	  	                BigInteger cnt = (BigInteger) listCount.get(0);
	  	                int count = cnt.intValue();
	  	                if (count == 0)
	  	                {
	  	                    // no table present in tblitemrtemp so update it to normal
	  	                    sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "',intPaxNo=0 where strTableNo='" + tableNo + "'";
	  	                    //rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                    objBaseService.funExecuteUpdate(sql_updateTableStatus, "sql");
	  	                }
	  	                else
	  	                {
	  	                	tableStatus = "Occupied";
	  	                    sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "' where strTableNo='" + tableNo + "'";
	  	                    //rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                    objBaseService.funExecuteUpdate(sql_updateTableStatus, "sql");
	  	                }
	  	            }
	  	        	else
	  	            {
	  	                sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "',intPaxNo=0 where strTableNo='" + tableNo + "'";
	  	                //rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                objBaseService.funExecuteUpdate(sql_updateTableStatus, "sql");
	  	            }
	  	        	if(objSetupParameter.get("gInrestoPOSIntegrationYN").toString().equalsIgnoreCase("Y"))
	  	            {
	  	                //objUtility.funUpdateTableStatusToInrestoApp(tableNo, tableName, tableStatus,clientCode,posCode);
	  	            }

	        }
        Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gBillSettleSMSYN"); 
  	    
        if (objSetupParameter.get("gBillSettleSMSYN").toString().equalsIgnoreCase("Y"))
        {
        	String billSettleSMS=(String) objSetupParameter.get("gBillSettleSMSYN");
           // objUtility.funSendSMS(billNo, billSettleSMS, "",clientCode,posCode);
        }

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//			return jObjAssignHomeDeliveryMaster;
		}	
	}
	
	
	
	
	   public Map<String,List<clsPOSBillDtl>> funExecute(String posCode,String operationType,String kotFor)
	   	{
	    	Map<String,List<clsPOSBillDtl>> hmReprintDtl=new HashMap();
	    	List<clsPOSBillDtl> listReprintDtl=new ArrayList<>();
	    	List list = null;
	    	StringBuilder sql = new StringBuilder(); 
	    	try
	    	{
	    	if (operationType.equalsIgnoreCase("KOT"))
	        {
	    		//kotFor="Dina";
	            if (kotFor.equalsIgnoreCase("Dina"))
	            {
	            	sql.append("select a.strKOTNo,TIME_FORMAT(time(a.dteDateCreated),'%h:%i') as Time "
	                        + ",IFNULL(c.strWShortName,'NA'),ifnull(b.strTableName,'')"
	                        + ",a.intPaxNo,a.strUserEdited ,a.dblAmount "
	                        + "from tblitemrtemp a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
	                        + "left outer join tblwaitermaster c  on a.strWaiterNo=c.strWaiterNo "
	                        + "where a.strPOSCode='" + posCode + "' "
	                        + "group by a.strKOTNo,a.strTableNo  "
	                        + "order by a.strKOTNo desc");
	       			list=objBaseService.funGetList(sql, "sql");
	    			
	    			 if (list!=null)
	    				{
	    					for(int i=0; i<list.size(); i++)
	    					{
	    						Object[] obj = (Object[]) list.get(i);
	    						clsPOSBillDtl objBillDtl=new clsPOSBillDtl();
	    						objBillDtl.setStrKOTNo(Array.get(obj, 0).toString());
	    						objBillDtl.setTmeBillTime(Array.get(obj, 1).toString());
	    						objBillDtl.setStrWShortName(Array.get(obj, 2).toString());
	    						objBillDtl.setStrTableName(Array.get(obj, 3).toString());
	    						objBillDtl.setSequenceNo(Array.get(obj, 4).toString());
	    						objBillDtl.setStrCustomerName(Array.get(obj, 5).toString());
	    						objBillDtl.setDblAmount(Double.valueOf(Array.get(obj, 6).toString()));
	    						listReprintDtl.add(objBillDtl);
	    					}
	    					hmReprintDtl.put("strOperation", listReprintDtl);
	       				}
	            }
	            else if (kotFor.equalsIgnoreCase("DirectBiller"))
	            {
	            	sql.setLength(0);
	            	sql.append("select a.strbillno ,TIME_FORMAT(time(a.dteBillDate),'%h:%i') as Time  ,b.strPOSName "
	                        + ",a.dblGrandTotal "
	                        + " from tblbillhd a ,tblposmaster b "
	                        + " where a.strPOSCode='" + posCode + "' and a.strPOSCode=b.strPOSCode "
	                        + " and a.strTableNo='' Or a.strTableNo='TB0000'  "
	                        + " order by a.strbillno DESC");
	            	list=objBaseService.funGetList(sql, "sql");
	    			
	    			 if (list!=null)
	    				{
	    					for(int i=0; i<list.size(); i++)
	    					{
	    						Object[] obj = (Object[]) list.get(i);
	    						clsPOSBillDtl objBillDtl=new clsPOSBillDtl();
	    						objBillDtl.setStrBillNo(Array.get(obj, 0).toString());
	    						objBillDtl.setTmeBillTime(Array.get(obj, 1).toString());
	    						objBillDtl.setStrPosName(Array.get(obj, 2).toString());
	    						objBillDtl.setDblAmount(Double.valueOf(Array.get(obj, 3).toString()));
	    						listReprintDtl.add(objBillDtl);
	    						
	    					}
	    					hmReprintDtl.put("strOperation", listReprintDtl);
	    				}
	            }
	        }
	    	else if (operationType.equalsIgnoreCase("Bill"))
	        {
	    		sql.setLength(0);
	    		sql.append("select a.strbillno ,ifnull(b.strTableName,'ND'),TIME_FORMAT(time(a.dteBillDate),'%h:%i') as Time "
	                    + ",a.strPOSCode ,a.dblGrandTotal  "
	                    + "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + "where a.strPOSCode='" + posCode + "' "
	                    + "order by a.strbillno DESC");
	    		list=objBaseService.funGetList(sql, "sql");
				 
				 if (list!=null)
					{
						for(int i=0; i<list.size(); i++)
						{
							Object[] obj = (Object[]) list.get(i);
							clsPOSBillDtl objBillDtl=new clsPOSBillDtl();
	   						objBillDtl.setStrBillNo(Array.get(obj, 0).toString());
	   						objBillDtl.setStrTableName(Array.get(obj, 1).toString());
	   						objBillDtl.setTmeBillTime(Array.get(obj, 2).toString());
	   						objBillDtl.setDblAmount(Double.valueOf(Array.get(obj, 4).toString()));
	   						listReprintDtl.add(objBillDtl);
						}
						hmReprintDtl.put("strOperation", listReprintDtl);
				
					}

	        }
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	finally
	    	{
	         return hmReprintDtl;
	    	}
	   	}
	    
	 
	
	   public void funSaveReservation(clsTableReservationModel objBaseModel)throws Exception
		 {
		     objBaseService.funSave(objBaseModel); 	
			 StringBuilder sql=new StringBuilder();
		  	 sql.append("update tbltablemaster set strStatus='Reserve' where strTableNo='" + objBaseModel.getStrTableNo() + "' ");
		  	 objBaseService.funExecuteUpdate(sql.toString(),"sql");
		 }
	   
	   
	   public String funGenerateReservationCode()
	   {
	     String strResCode = "";
	     StringBuilder sql=new StringBuilder();
		 try
		  {
			   sql.append("select ifnull(max(strResCode),0) from tblreservation");
		       List list = objBaseService.funGetList(sql, "sql");
		       if(list.size()>0)
		        {
		        	if (!list.get(0).toString().equals("0"))
				   	{
				   		String strCode = "0";
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
				   			strResCode = "RS000000" + intCode;
				   		}
				   		else if (intCode < 100)
				   		{
				   			strResCode = "RS00000" + intCode;
				   		}
				   		else if (intCode < 1000)
				   		{
				   			strResCode = "RS0000" + intCode;
				   		}
				   		else if (intCode < 10000)
				   		{
				   			strResCode = "RS000" + intCode;
				   		}
				   		else if (intCode < 100000)
				   		{
				   			strResCode = "RS00" + intCode;
				   		}
				   		else if (intCode < 1000000)
				   		{
				   			strResCode = "RS0" + intCode;
				   		}
				   		else if (intCode < 10000000)
				   		{
				   			strResCode = "RS" + intCode;
				   		}
				  }
				  else
				  {
				   	  strResCode= "RS" + String.format("%06d", 1);
				  }
		        }
	
		   }
		   catch (Exception e)
		   {
		       e.printStackTrace();
		   }

	   return strResCode;
	 }
	   
	   
	 public String funCheckCustomerExist(String contactNo)
	 {
	   	String strCustomerCode="";
	    StringBuilder sql=new StringBuilder();
	   	try{
	   	
	   		sql.append("select strCustomerCode,strCustomerName,strBuldingCode,strBuildingName,strCity from tblcustomermaster  where longMobileNo='" + contactNo + "' ");     
	   	    List list = objBaseService.funGetList(sql, "sql");
	   		if (list.size()>0)
   			{
   			 Object[] obj=(Object[])list.get(0);
   			 strCustomerCode=obj[0].toString();
   			}
	              
	   		}catch(Exception ex)
	   		{
	   			ex.printStackTrace();
	   		}
	   		finally
	   		{
	   			return strCustomerCode;
	   		}
	 }   
}
