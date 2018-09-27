package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationBean;
import com.sanguine.webpos.bean.clsPOSVoidKotBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.util.clsPOSUtilityController;
@Controller
public class clsPOSVoidKotController {

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired 
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	clsPOSUtilityController objUtility;
	
	String manualKOTNo=""; 
	String deleteMode="";
	@RequestMapping(value = "/frmPOSVoidKot", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		String strPosCode=request.getSession().getAttribute("loginPOS").toString();
		model.put("urlHits",urlHits);
	    Map<String, String> mapTableCombo=	funLoadTableData(strPosCode);
	    List listTableCombo =new ArrayList();
	    listTableCombo.add("All Tables");
	    for(Map.Entry map:mapTableCombo.entrySet())
	    {
	    	listTableCombo.add(map.getKey());
	    	
	    }
	    model.put("tableData", listTableCombo);
	    List listReson=funLoadResonCode();
	    model.put("listReson", listReson);
		//return new ModelAndView("frmPOSGroupMaster");
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidKot_1","command", new clsPOSVoidKotBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidKot","command", new clsPOSVoidKotBean());
		}else {
			return null;
		}
		 
	}

	public Map funLoadTableData(String strPosCode){
		
		Map mapTableCombo = new HashMap<String, String>();	
		List jArry = new ArrayList();
		try {
			
		   
		    Map jObj =funLoadTable(strPosCode);
			List jarr = (List) jObj.get("jArr");
			for(int i=0;i<jarr.size();i++)
			{
				Map jObjtemp =(Map) jarr.get(i);
				mapTableCombo.put(jObjtemp.get("strTableName").toString(), jObjtemp.get("strTableNo").toString());
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mapTableCombo;
	}
	
	public List funLoadResonCode(){
		
		List listResonCombo = new ArrayList();	
		
		try {
			
		   
		    Map jObj =funLoadReson();
			List jarr = (List) jObj.get("jArr");
			for(int i=0;i<jarr.size();i++)
			{
				Map jObjtemp =(Map) jarr.get(i);
				listResonCombo .add( jObjtemp.get("resoncode").toString());
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return listResonCombo;
	}
			
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/fillRGridData", method = RequestMethod.GET)
	public @ResponseBody LinkedList funFillRGridData(HttpServletRequest req){	
		
		String tableNo="";
		String strPosCode=req.getSession().getAttribute("loginPOS").toString();
		String tableName=req.getParameter("tableName");
		Map mapFillGrid=new HashMap();
		LinkedList listFillGrid=new LinkedList();
		try {
			
			
			if(!tableName.equalsIgnoreCase("All Tables"))
			{
				Map<String,String> mapTableData= funLoadTableData(strPosCode);
				tableNo=mapTableData.get(tableName);	
			}
			
			
		
		    
		    Map jObj =funFillHelpGrid(tableName,tableNo,strPosCode);
			List jarr = (List) jObj.get("jArr");
			
			for(int i=0;i<jarr.size();i++)
			{
				Map jObjtemp =(Map) jarr.get(i);
				LinkedList setFillGrid=new LinkedList();
				setFillGrid.add(jObjtemp.get("strKOTNo").toString());
				setFillGrid.add( jObjtemp.get("strTableName").toString());
				setFillGrid.add( jObjtemp.get("strWShortName").toString());
				setFillGrid.add( jObjtemp.get("strTakeAwayYesNo").toString());
				setFillGrid.add( jObjtemp.get("strUserCreated").toString());
//				setFillGrid.add(Integer.parseInt(jObjtemp.get("intPaxNo").toString()));
				setFillGrid.add( Double.parseDouble(jObjtemp.get("dblAmount").toString()));
//				setFillGrid.add( jObjtemp.get("strManualKOTNo").toString());
			
				listFillGrid.add(setFillGrid);
				
				
				
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return listFillGrid;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/fillItemDataTable", method = RequestMethod.GET)
	@ResponseBody
	public Map funFillItemTableData(HttpServletRequest req){	
		
		
		
	            String KotNo = req.getParameter("kot");
	        	
	    		String strPosCode=req.getSession().getAttribute("loginPOS").toString();
//	    		String tableNo=req.getParameter("tableNo");
	    		String tableNo="";
	    		String tableName=req.getParameter("cmbTableName").toString();
	    		Map mapFilltable=new HashMap();
	    		LinkedList listFillItemGrid=new LinkedList();
	    		try {
	    			
	    			if(!tableName.equalsIgnoreCase("All Tables"))
	    			{
	    				Map<String,String> mapTableData= funLoadTableData(strPosCode);
	    				tableNo=mapTableData.get(tableName);	
	    			}
	    			
	    			
	    			Map jObj = fillItemTableData( KotNo, tableNo, strPosCode, tableName);
	    		    List jarr = (List) jObj.get("jArr");
	    			for(int i=0;i<jarr.size();i++)
	    			{
	    			    
	    				Map jObjtemp =(Map) jarr.get(i);
	    				LinkedList setFillGrid=new LinkedList();
	    				setFillGrid.add(jObjtemp.get("strItemName").toString());
	    				setFillGrid.add(Double.parseDouble( jObjtemp.get("dblItemQuantity").toString()));
	    				setFillGrid.add( Double.parseDouble(jObjtemp.get("dblAmount").toString()));
	    				setFillGrid.add( jObjtemp.get("strItemCode").toString());
	    				setFillGrid.add( jObjtemp.get("strUserCreated").toString());
	    				setFillGrid.add( jObjtemp.get("dteDateCreated").toString());
	    				
	    				listFillItemGrid.add(setFillGrid);
	    			
	    			}
	    			
	    			double totalAmount = Double.parseDouble(jObj.get("totalAmount").toString());
	    			double  tax=Double.parseDouble(jObj.get("taxAmt").toString());
	    			double subTotalAmt=Double.parseDouble(jObj.get("subTotalAmt").toString());
	    			String userCreated=jObj.get("userCreated").toString();
	    			mapFilltable.put("listFillItemGrid", listFillItemGrid);
	    			mapFilltable.put("totalAmount", totalAmount);
	    			mapFilltable.put("taxAmt", tax);
	    			mapFilltable.put("KotNo", KotNo);
	    			mapFilltable.put("userCreated", userCreated);
	    			mapFilltable.put("subTotalAmt", subTotalAmt);
	    			
	        }
	        catch (Exception e)
	        {
	          
	            e.printStackTrace();
	        }
		return mapFilltable;
		
	}

	@RequestMapping(value = "/doneButtonClick", method = RequestMethod.POST)
	public  @ResponseBody String funDoneButtonClick1(HttpServletRequest req){

		 String result="";
	try {

		 String voidedDate=req.getSession().getAttribute("gPOSDate").toString();
		 String strPosCode=req.getSession().getAttribute("loginPOS").toString();
		String[] arrVoidedItemList = req.getParameterValues("voidedItemList");

		String delKotNo=req.getParameter("delKotNo").toString();
		String tableNo=req.getParameter("delTableNo").toString();
		String reasonCode=req.getParameter("reasonCode").toString();
		String remarks=req.getParameter("remarks").toString();

		double taxAmt=Double.parseDouble(req.getParameter("taxAmt").toString());
	
			Map<String,String> mapTableData= funLoadTableData(strPosCode);
			String delTableNo=mapTableData.get(tableNo);	

		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
       
			 String delItemCode="",delItemName="";		
				double delItemQuantity=0,amount=0,remainingAmount=0,remainingItemQuantity=0;
				if (arrVoidedItemList.length > 0)
		        {
		            for (int cnt = 0; cnt < arrVoidedItemList.length; cnt++)
		            {
		                String []item= arrVoidedItemList[cnt].split(",");
		                for(String itemData:item)
		                {
		                	delItemCode=itemData.split("#")[0];
		                	delItemName=itemData.split("#")[1];
		                    remainingItemQuantity=Double.parseDouble(itemData.split("#")[3]);
		                    remainingAmount=Double.parseDouble(itemData.split("#")[4]);
		                    delItemQuantity=Double.parseDouble(itemData.split("#")[5]);
		                    amount=Double.parseDouble(itemData.split("#")[6]);
	                      	Map jObj = funDoneButtonClick( reasonCode, delTableNo, delKotNo, remarks,
	         					 userCode, clientCode, voidedDate, taxAmt,delItemCode,delItemName,delItemQuantity,amount);
	         			 	result=jObj.get("true").toString();
		         		
		                   
		                }
		                	
		            }

		        }	 
			 
			 
		}  catch (Exception e)
        {
	          
            e.printStackTrace();
        }
		

		return result;	
		
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/fullVoidKotButtonClick", method = RequestMethod.GET)
	@ResponseBody
	public String funFullVoidKot(HttpServletRequest req)
	{
		String result="";
		try {
			
			 Map jservice=new HashMap();
			 String voidedDate=req.getSession().getAttribute("gPOSDate").toString();
			 String strPosCode=req.getSession().getAttribute("loginPOS").toString();
			String delKotNo=req.getParameter("delKotNo").toString();
			String reasonCode=req.getParameter("reasonCode").toString();
			String remarks=req.getParameter("remarks").toString();
//			String reasonCode,String Kot,String strClientCode,String voidedDate,String userCode,String voidKOTRemark
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String userCode=req.getSession().getAttribute("gUserCode").toString();

				Map jObj = funClickFullVoidKot( reasonCode, delKotNo, clientCode, voidedDate, userCode, remarks) ; 
				 result=jObj.get("sucessfully").toString();
			}  catch (Exception e)
	        {
		          
	            e.printStackTrace();
	        }
		return result;
			
		
		
	}
	
	public Map funLoadTable(String strPosCode)
	{
		Map tableData = new HashMap();
		StringBuilder sqlBuilder = new StringBuilder();
		List jArr=new ArrayList();
		try {

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select b.strTableNo,a.strTableName "
                + "from tbltablemaster a,tblitemrtemp b "
                + "where a.strTableNo=b.strTableNo "
                + "and  (a.strPOSCode='" + strPosCode + "' OR a.strPOSCode='All') and strNCKotYN='N'  "
                + "group by b.strTableNo "
                + "order by a.strTableName ");
		
		
		
	  	    List listSql1= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	  	  if(listSql1.size()>0)
	  	      {
	  		
	  	    		
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		Map jObj=new HashMap();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
	  	    	jObj.put("strTableNo",obj1[0].toString());
	  	    	jObj.put("strTableName",obj1[1].toString());
	  	    	jArr.add(jObj);
	  	        }
	    		
	  		
 	    }
	  	  tableData.put("jArr", jArr);
		}catch(Exception e)
		{
			
		}
		return tableData;
	}
	
    public Map funLoadReson()
    {
    	Map jObjReturn=new  HashMap(); 
    	List jarr=new ArrayList();
        String favoritereason = null;
        try
        {

        	String[] arrReason;
            int reasoncount = 0, i = 0;
            String resonCode="";
           	StringBuilder 	sqlBuilder=new StringBuilder();
           	sqlBuilder.setLength(0);
	 	       	sqlBuilder.append( "select count(strReasonName) from tblreasonmaster where strKot='Y'" );
       	    List listSql=  objBaseServiceImpl.funGetList(sqlBuilder, "sql");
       	    if(listSql.size()>0)
       	     {
         	    for(int cnt=0;cnt<listSql.size();cnt++)
         	    {
         		
        	    Object obj = (Object) listSql.get(cnt);
               reasoncount = Integer.parseInt(obj.toString());
                }
       	     }
            if (reasoncount > 0)
            {
                arrReason = new String[reasoncount];
           
	            	sqlBuilder.setLength(0);
		 	       	sqlBuilder.append( "select strReasonName from tblreasonmaster where strKot='Y'");
              
           	    List listSqlReason=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
           	    if(listSqlReason.size()>0)
           	     {
             	    for(int j=0;j<listSqlReason.size();j++)
             	    {
             		Map jobj=new  HashMap();
            	    Object obj = (Object ) listSqlReason.get(j);
            	    i = 0;
            	    jobj.put("resoncode",obj.toString() );
            	    jarr.add(jobj); 
//            	    arrReason[i] =obj[0].toString();
//                    i++;
                    }
           	     }
               
                jObjReturn.put("jArr", jarr);
            }
        }
        catch (Exception e)
        {
          
            e.printStackTrace();
        }
        return jObjReturn;
    }
    
    
	public Map funFillHelpGrid(String tableName,String tableNo,String strPosCode)
	{
		Map jObjReturn=new HashMap();
		List jArr=new ArrayList();
		
	
		try
        {
		  	StringBuilder 	sqlBuilder=new StringBuilder();
           	sqlBuilder.setLength(0);
	 	       	sqlBuilder.append(  "select a.strKOTNo,a.strTableNo,b.strTableName,c.strWShortName"
                    + ",a.strPrintYN,a.strTakeAwayYesNo,a.strUserCreated,a.intPaxNo"
                    + ",sum(a.dblAmount),a.strManualKOTNo "
                    + "from tblitemrtemp a left outer join tbltablemaster b "
                    + "on a.strTableNo=b.strTableNo  left outer join  tblwaitermaster c "
                    + "on a.strWaiterNo=c.strWaiterNo  "
                    + "where a.strTableNo=b.strTableNo and a.strNCKotYN='N' and a.strPOSCode='" + strPosCode + "'");
            if (!tableName.toString().equals("All Tables"))
            {
//                String tbNo = mapTableCombo.get(ctableName.toString());
            	sqlBuilder.append(" and a.strTableNo='" + tableNo + "'");
            }
            sqlBuilder.append( " group by a.strKOTNo");
         

          
	  	    List listSql1= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	  	    if(listSql1.size()>0)
	  	      {
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		Map jObj=new HashMap();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
            
	  	    	jObj.put("strKOTNo", obj1[0].toString());
	  	    	jObj.put("strTableNo", obj1[1].toString());
	  	    	jObj.put("strTableName", obj1[2].toString());
	  	    	jObj.put("strWShortName", obj1[3].toString());
	  	    	jObj.put("strPrintYN", obj1[4].toString());
	  	    	jObj.put("strTakeAwayYesNo", obj1[5].toString());
	  	    	jObj.put("strUserCreated", obj1[6].toString());
	  	    	jObj.put("intPaxNo",Integer.parseInt( obj1[7].toString()));
	  	    	jObj.put("dblAmount", Double.parseDouble(obj1[8].toString()));
	  	    	jObj.put("strManualKOTNo", obj1[9].toString());
	  	    	jArr.add(jObj);
	  	    	manualKOTNo=obj1[9].toString();
	  	    	
//                String KotNo = rs.getString("a.strKOTNo");
//                String strTableName = rs.getString("b.strTableName");
//                String waiterName = rs.getString("c.strWShortName");
//                String takeAway = rs.getString("a.strTakeAwayYesNo");
//                String user = rs.getString("a.strUserCreated");
//                String pax = rs.getString("a.intPaxNo");
//                double dblAmount = rs.getDouble("sum(a.dblAmount)");
//                manualKOTNo = rs.getString("a.strManualKOTNo");
       
            }
	  	    }
	  	  
	  	jObjReturn.put("jArr",jArr);
        }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
		
		return jObjReturn;
	}
	
	
	
	public Map fillItemTableData(String KotNo,String tableNo1,String strPosCode,String tableName)
	{
		Map jObjReturn=new HashMap();
		List jArr=new ArrayList();
		String sql="";
		String POSDate = "";
        String POSCode = "";
        String tableNo="";
        String areaCode="";
		try
        {

		 StringBuilder 	sqlBuilder=new StringBuilder();
         sqlBuilder.setLength(0);
	 	 sqlBuilder.append(  "select count(*) from tblitemrtemp "
            + "where strKOTNo='" + KotNo + "' and strPOSCode='" + strPosCode + "' and strNCKotYN='N' ");
       
 	    List listSql1=  objBaseServiceImpl.funGetList(sqlBuilder, "sql");
 	   
   		   Map jObj=new HashMap();
 	    	Object obj1 = (Object) listSql1.get(0);
 	    
      
       int cnt =Integer.parseInt(obj1.toString());
       if (cnt > 0)
       {
    		sqlBuilder.setLength(0);
 	       	sqlBuilder.append( "select strItemName,dblItemQuantity,dblAmount,strUserCreated,dteDateCreated,strItemCode"
                + " ,strPOSCode,strTableNo "
                + " from tblitemrtemp "
                + " where strKOTNo='" + KotNo + "' and strPOSCode='" + strPosCode + "' and strNCKotYN='N' ");
        double subTotalAmt = 0.00;
        String userCreated="";
        

        List<clsPOSItemDetailFrTaxBean> arrListItemDtl = new ArrayList<clsPOSItemDetailFrTaxBean>();
       
  	    List listSql=  objBaseServiceImpl.funGetList(sqlBuilder, "sql");
  	     {
    	  for(int j=0 ;j<listSql.size();j++ )
  	       {
        
    		Map jObj1=new HashMap();
   	    	Object[] obj = (Object []) listSql.get(j);
   	  
            jObj1.put("strItemName", obj[0].toString()) ; 
            jObj1.put("dblItemQuantity", Double.parseDouble( obj[1].toString())) ; 
            jObj1.put("dblAmount", Double.parseDouble(obj[2].toString()) ); 
            jObj1.put("strUserCreated", obj[3].toString()) ; 
            jObj1.put("dteDateCreated", obj[4].toString()) ; 
            jObj1.put("strItemCode", obj[5].toString()) ;
            jObj1.put("POSCode", obj[6].toString()) ; 
            jObj1.put("tableNo", obj[7].toString()) ; 
            
   	    	
            String itemName = obj[0].toString();
            double dblQuantity = Double.parseDouble( obj[1].toString());
            double dblAmount = Double.parseDouble(obj[2].toString());
            String itemCode = obj[5].toString();
            subTotalAmt = subTotalAmt + dblAmount;
            userCreated = obj[3].toString(); 
            POSDate = obj[4].toString();
            POSCode = obj[6].toString();
            tableNo =obj[7].toString();

            jArr.add(jObj1);   
            
            clsPOSItemDetailFrTaxBean objItemDtlForTax = new clsPOSItemDetailFrTaxBean();
            objItemDtlForTax.setItemCode(itemCode);
            objItemDtlForTax.setItemName(itemName);
            objItemDtlForTax.setAmount(dblAmount);
            objItemDtlForTax.setDiscAmt(0);
            objItemDtlForTax.setDiscPer(0);
            arrListItemDtl.add(objItemDtlForTax);
  	       }
    	  }
  	       
  	    sqlBuilder.setLength(0);
       	sqlBuilder.append( "select strAreaCode from tbltablemaster where strTableNo='" + tableNo + "';");
  	    List listSqlAreaCode= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
 	    if(!listSqlAreaCode.isEmpty())
 	    {
  	    Object objAreaCode=(Object)listSqlAreaCode.get(0);
        areaCode = objAreaCode.toString();
 	    }
         
         
        double taxAmt = 0;
        List<clsPOSTaxCalculationBean> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtl, POSCode, POSDate, areaCode, "DineIn", subTotalAmt, 0, "Void KOT", "S01");
        if(arrListTaxDtl.size()>0)
        {
        for (int i=0;i<arrListTaxDtl.size();i++)
        {
        	clsPOSTaxCalculationBean objBean = arrListTaxDtl.get(i);
        	if(i==arrListTaxDtl.size()-1)
        	{	
        	taxAmt += objBean.getTaxAmount();
        	}
        }
        }
        arrListTaxDtl = null;
      double totalAmount=  subTotalAmt + taxAmt;
       
        jObjReturn.put("totalAmount", totalAmount);        
        jObjReturn.put("taxAmt", taxAmt);        
        jObjReturn.put("subTotalAmt", subTotalAmt);
        jObjReturn.put("subTotalAmt", subTotalAmt);
        jObjReturn.put("userCreated", userCreated);
        jObjReturn.put("jArr", jArr);

    }
        }
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		 return jObjReturn;
}
	
	
	

	public Map funDoneButtonClick(String resonCode,String selectedTableNo,String lblKOTNo,String remarks,
			String userCode,String clientCode,String voidedDate,double dblTax,String delItemCode,String delItemName,double delItemQuantity,double amount) {

		Map jObjReturn =new HashMap();
		
		
		
		try{
		
		String sql="";
		double singleItemAmount,voidedAmount;
		double voidedQty = 0;
		
	
		
		  StringBuilder 	sqlQuery=new StringBuilder();
		  sqlQuery.setLength(0);
          sqlQuery.append( "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='VoidKot'");
		   double qtyAfterDelete = 0;
             String strType = "DVKot";
             StringBuilder 	sqlBuilder=new StringBuilder();
             sqlBuilder.setLength(0);
    	 	 sqlBuilder.append(  "select strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strUserCreated,dteDateCreated "
                     + "from tblitemrtemp where strItemCode='" + delItemCode + "' "
                     + "and strKOTNo='" + lblKOTNo + "' and strTableNo='" + selectedTableNo + "' and strNCKotYN='N' ");

       	    List listSql=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
       	    if(listSql.size()>0)
       	    {
        	 Object[] obj = (Object []) listSql.get(0);
             qtyAfterDelete = Double.parseDouble(obj[4].toString()) - delItemQuantity;
             singleItemAmount = Double.parseDouble(obj[5].toString())
                     / Double.parseDouble(obj[4].toString());
             if (qtyAfterDelete > 0)
             {
                 voidedQty = delItemQuantity;
                 voidedAmount = amount;
             }
             else
             {
                 voidedQty = delItemQuantity;
                 voidedAmount = amount;
             }

             sql = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode,"
                     + "strUserCreated,dteDateCreated,dteVoidedDate,strClientCode,strRemark) "
                     + "values('" + obj[0].toString() + "','" +obj[1].toString() + "'"
                     + ",'" +obj[2].toString()+ "','" + obj[3].toString() + "',"
                     + "'" + voidedQty + "','" + voidedAmount + "','" + obj[6].toString() + "'"
                     + ",'" +obj[7].toString() + "'," + "'" + obj[8].toString() + "'"
                     + ",'" + strType + "','" + resonCode + "','" + userCode + "'"
                     + ",'" + obj[10].toString() + "'," + "'" + voidedDate + "'" + ""
                     + ",'" + clientCode + "','" + remarks + "' ) ";

             int exc = 0;
             if (userCode.equalsIgnoreCase("super")||userCode.equalsIgnoreCase("sanguine"))
             {
            	 exc= objBaseServiceImpl.funExecuteUpdate(sql, "sql");

             }
             else
             {                            
                List list=objBaseServiceImpl.funGetList(sqlQuery, "sql") ;
                for(int i=0;i<list.size();i++){
                
                	Object[] obj1=(Object[])list.get(i);

                	if (Boolean.parseBoolean(obj1[2].toString()))
                     {
                		 exc= objBaseServiceImpl.funExecuteUpdate(sql, "sql");
                     }
                 
             }
             }
             sql = "Update tblnonchargablekot set dblQuantity='" + qtyAfterDelete + "' where strKOTNo='" + obj[7].toString() + "' and strItemCode='" + obj[2].toString() + "' ";
            objBaseServiceImpl.funExecuteUpdate(sql, "sql");
     		
             if (qtyAfterDelete == 0)
             {
                 sql = "Delete from tblnonchargablekot where strKOTNo='" +obj[7].toString()  + "' and strItemCode='" + obj[2].toString()  + "' ";

                 objBaseServiceImpl.funExecuteUpdate(sql, "sql");
             }
             if (exc > 0)
             {
                 if (qtyAfterDelete > 0)
                 {
                     sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
                             + "values('" + obj[3].toString() + "','"
                             + delItemCode + "','" + delItemQuantity + "','" + obj[0].toString() + "','" + obj[6].toString() + "','" + obj[7].toString() + "')";

                     objBaseServiceImpl.funExecuteUpdate(sql, "sql");
                     sql = "update tblitemrtemp set dblItemQuantity=" + qtyAfterDelete + ", "
                             + " dblAmount=" + (singleItemAmount * qtyAfterDelete) + ", dblTaxAmt='" + dblTax + "' "
                             + " where strItemCode='" +  delItemCode  + "'"
                             + " and strKOTNo='" + lblKOTNo+ "'  and strNCKotYN='N' ";

                     objBaseServiceImpl.funExecuteUpdate(sql, "sql");
                 }
                 else
                 {
                     sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
                             + "values('" +  obj[3].toString() + "','" +  obj[2].toString() + "','"
                             + Double.parseDouble(obj[4].toString()) + "','" + obj[0].toString()  + "','" + obj[6].toString()  + "','" +obj[7].toString()  + "')";
                     objBaseServiceImpl.funExecuteUpdate(sql, "sql");

                     sql = "delete from tblitemrtemp where left(strItemCode,7)='" +delItemCode+ "' "
                             + " and strKOTNo='" + lblKOTNo + "' and strTableNo='" + selectedTableNo + "' and strNCKotYN='N' ";
                    objBaseServiceImpl.funExecuteUpdate(sql, "sql");
                    sqlBuilder.setLength(0);
            	 	sqlBuilder.append("select count(*) from tblitemrtemp where strTableNo='" + selectedTableNo + "'  and strNCKotYN='N' ");
            	    List listSql1= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
            	    if(listSql1.size()>0)
            	    {
                	 Object objdel = (Object) listSql1.get(0);
                     if (Integer.parseInt(objdel.toString()) == 0)
                     {
                         sql = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
                                 + "where strTableNo='" + selectedTableNo + "'";
                         objBaseServiceImpl.funExecuteUpdate(sql, "sql");
                     }
                	}
                }
             }
       	   }
       	 jObjReturn.put("true", "true");
       	   
      
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		 return jObjReturn;
	}
	
	
	public Map funClickFullVoidKot(String favoritereason,String Kot,String strClientCode,String voidedDate,String userCode,String voidKOTRemark) 
	{
		Map jObjReturn=new HashMap();
		 int exc = 0;
		 String sql="";
		 String reasonCode="";
		 String voidBillType = "Full KOT Void";
		 deleteMode = "KOT";
		 String tableNo="";
		 try{
		 StringBuilder sqlQuery=new StringBuilder();
		 sqlQuery.append( "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='VoidKot'");
		 StringBuilder sqlBuilder=new StringBuilder();
		 
		    if (favoritereason != null)
	        {
		    	 sqlBuilder.setLength(0);
         	 	sqlBuilder.append( "select strReasonCode from tblreasonmaster where strReasonName='" + favoritereason + "'");
	       	    List listSqlReason= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	       	    if(listSqlReason.size()>0)
	       	     {
	       	     Object obj = (Object) listSqlReason.get(0);
	       	    reasonCode = obj.toString();
	            }

	    }
		 
		 if (!"".equals(reasonCode))
	     {
	         String strType = "VKot";
	        sqlBuilder.setLength(0);
      	 	sqlBuilder.append ("select strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo,"
	                 + "strKOTNo,intPaxNo,strUserCreated,dteDateCreated from tblitemrtemp "
	                 + "where strKOTNo='" + Kot + "' and strNCKotYN='N' ");
	    
	    	    List listSql= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    	    if(listSql.size()>0)
	    	     {
	      	    for(int i=0;i<listSql.size();i++)
	      	    {
	      		
	     	    Object[] obj = (Object []) listSql.get(i);
	       
	     	   tableNo=obj[0].toString();
	             sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
	                     + "values('" + obj[3].toString()+ "','" +  obj[2].toString() + "','"
	                     + Double.parseDouble(obj[4].toString()) + "','" + obj[0].toString()+ "','" + obj[6].toString() + "'"
	                     + ",'" + obj[7].toString() + "')";
	             //System.out.println(sql);
	             exc=  objBaseServiceImpl.funExecuteUpdate(sql, "sql");

	             sql = "update tblvoidkot  set strVoidBillType='Full KOT Void' "
	                     + "where strKOTNo='" + Kot + "'";
	             exc = objBaseServiceImpl.funExecuteUpdate(sql, "sql");

	             sql = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
	                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode,strUserCreated,"
	                     + "dteDateCreated,dteVoidedDate,strClientCode,strManualKOTNo,strRemark,strVoidBillType) "
	                     + "values('" + obj[0].toString() + "','" +  obj[1].toString() + "'"
	                     + ",'" +  obj[2].toString() + "','" +  obj[3].toString() + "'"
	                     + "," + "'" +  Double.parseDouble(obj[4].toString()) + "','" +  Double.parseDouble(obj[5].toString()) + "'"
	                     + ",'" +  obj[6].toString() + "','" +  obj[7].toString() + "'"
	                     + "," + "'" +  obj[8].toString()+ "','" + strType + "','" + reasonCode + "'"
	                     + ",'" + userCode + "','" +  obj[10].toString() + "'"
	                     + "," + "'" + voidedDate + "','" + strClientCode + "','" + manualKOTNo + "','" + voidKOTRemark + "','"+voidBillType+"' ) ";
	             
	             if (userCode.equalsIgnoreCase("super")||userCode.equalsIgnoreCase("sanguine"))
	             {
	            	 exc=  objBaseServiceImpl.funExecuteUpdate(sql, "sql");
	             }
	             else
	             {      
	         	    List listSql1=objBaseServiceImpl.funGetList(sqlQuery, "sql");
	         	    if(listSql1.size()>0)
	         	     {
	          	         Object[] obj1 = (Object []) listSql1.get(0);                 
	                     if (Boolean.parseBoolean(obj1[0].toString()))
	                     {
	                    	 exc= objBaseServiceImpl.funExecuteUpdate(sql, "sql");
	                     }
	                 }
	             }

	             sql = "Delete from tblnonchargablekot where strKOTNo='" + Kot + "' ";
	             objBaseServiceImpl.funExecuteUpdate(sql, "sql");
	         }
	         }
	         if (exc > 0)
	         {
	        	 
	        	    funDeleteTempItem();
	               
	                if (deleteMode.equals("KOT"))
	                {
	                    funDeleteKOTFromTemp(Kot,tableNo);
	                }
//	             funResetField();
//	             funRemotePrint();
//	             funFillHelpGrid();
	         }
	         jObjReturn.put("sucessfully", "sucessfully");
	         
	     }
		 }catch(Exception e)
			{
				
				e.printStackTrace();
			}
	     return jObjReturn;
	}
	
	private void funDeleteTempItem()
	{
	    try
	    {
	    	objBaseServiceImpl.funExecuteUpdate("delete from tbltempvoidkot ","sql");
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	    

	    private void funDeleteKOTFromTemp(String KotNo,String tableNo)
	    {
	        try
	        {
	        	String sql="";
	            sql = "delete from tblitemrtemp where strKOTNo='" + KotNo + "' and strTableNo='" + tableNo + "' and strNCKotYN='N' ";
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            StringBuilder sqlBuilder=new StringBuilder();
	            sqlBuilder.append( "select count(*) from tblitemrtemp where strTableNo='" + tableNo + "' and strNCKotYN='N' ");
	    	    List listSql=  objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    	    Object obj = (Object) listSql.get(0);
	            int cnt = Integer.parseInt( obj.toString());

	            if (cnt == 0)
	            {
	                String sql_status = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
	                        + "where strTableNo='" + tableNo + "'";
	                objBaseServiceImpl.funExecuteUpdate(sql_status, "sql");

	            }
	            sql = "delete from tblkottaxdtl where strKOTNo='" + KotNo + "' and strTableNo='" + tableNo + "';";
	            objBaseServiceImpl.funExecuteUpdate(sql, "sql");

	            //insert into itemrtempbck table
	   

	        }
	        catch (Exception e)
	        {
	          
	            e.printStackTrace();
	        }
	    }

			 public void funInsertIntoTblItemRTempBck(String tableNo)
			    {
			        try
			        {
			        	String sql =  "delete from tblitemrtemp_bck where strTableNo='" + tableNo + "'  ";
			        	objBaseServiceImpl.funExecuteUpdate(sql, "sql");

			            sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + tableNo + "'  )";
			            objBaseServiceImpl.funExecuteUpdate(sql, "sql");


			        }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			    }
}
