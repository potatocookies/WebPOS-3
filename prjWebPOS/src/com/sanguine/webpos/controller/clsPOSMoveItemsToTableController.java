package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsMakeKotItemDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSMoveItemsToTableController {

	@Autowired
	intfBaseService objBaseService;
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired 
	private clsPOSUtilityController objPOSUtilityController;

	@RequestMapping(value = "/frmMoveItemsToTable", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSBillItemDtlBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){

		String strPosCode=request.getSession().getAttribute("loginPOS").toString();
		Map mapPOS=new HashMap();
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster");
			List list=objBaseService.funGetList(sqlBuilder, "sql");
			
			mapPOS.put("All", "All");
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
				mapPOS.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
				
			}
			
			Map mapGetBussyTableNo = new TreeMap();
			mapGetBussyTableNo.put("All", "All");
			sqlBuilder.setLength(0);
			sqlBuilder.append("select distinct a.strTableNo,b.strTableName,b.strStatus "
				+ "from tblitemrtemp a,tbltablemaster b "
				+ "where a.strTableNo=b.strTableNo "
				+ "and a.strPOSCode='" + strPosCode + "' "
				+ "and a.strNCKotYN='N' "
				+ "order by b.intSequence ");
			list=objBaseService.funGetList(sqlBuilder, "sql");
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
				mapGetBussyTableNo.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
			}
			
			model.put("posList", mapPOS);
			model.put("bussyTableList", mapGetBussyTableNo);
			
			 List listReson=funLoadResonCode();
			 model.put("listReson", listReson);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return new ModelAndView("frmMoveItemsToTable");

	}
	
	@RequestMapping(value = "/loadAllTableData", method = RequestMethod.GET)
	public @ResponseBody List funGetAllTableDtl(HttpServletRequest req)
	{
		String posCode=req.getSession().getAttribute("gPOSCode").toString();
		
		List list =null;
		Map mapObjTableData=new HashMap();
		List listData=new ArrayList();
		try{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
					+ "where strOperational='Y' ");
			if(!posCode.equals("All"))
			{
				sqlBuilder.append(" and strPOSCode='"+posCode+"' ");
			}
			sqlBuilder.append(" order by intSequence ;");
			list = objBaseService.funGetList(sqlBuilder,"sql");
			if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					Map objMapSettle=new HashMap();
					objMapSettle.put("TableNo",obj[0].toString());
					objMapSettle.put("TableName",obj[1].toString());
				
					sqlBuilder.setLength(0);
					sqlBuilder.append("select strTableNo,strStatus from tbltablemaster where strTableNo='"+obj[0].toString()+"'");
					List sList = objBaseService.funGetList(sqlBuilder,"sql");
					Object[] objS = (Object[]) sList.get(0);
					String status=objS[1].toString();
					objMapSettle.put("Status",status);
					int pax=0;
					if(status.equals("Occupied"))
					{
						sqlBuilder.setLength(0);
						sqlBuilder.append("select intPaxNo from tblitemrtemp where strTableNo='"+obj[0].toString()+"' ");
						List pList = objBaseService.funGetList(sqlBuilder,"sql");
						if(pList.size()>0)
						{
							pax=(int) pList.get(0);
						}
					}
					objMapSettle.put("Pax",pax);
					listData.add(objMapSettle);
				}
				mapObjTableData.put("TableDtl", listData);
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return listData;
		}
	}
	
	@RequestMapping(value = "/fillItemForSelectedTable", method = RequestMethod.GET)
	public @ResponseBody List funFillItemForSelectedTable(@RequestParam("bussyTableNo") String bussyTableNo ,HttpServletRequest req) throws Exception
	{
		List listRet = new ArrayList();
		String posCode = req.getSession().getAttribute("gPOSCode").toString();
		StringBuilder sqlBuilderTableItemDtl = new StringBuilder(" select strItemCode,strItemName,dblRate,sum(dblItemQuantity),sum(dblAmount),sum(a.dblTaxAmt),a.strWaiterNo,dblFiredQty "
			    + "from tblitemrtemp a  "
			    + "where strTableNo='" + bussyTableNo + "'  "
			    + "and (strPosCode='" + posCode + "' or strPosCode='All')  "
			    + "and strNcKotYN='N' "
			    + "group by a.strItemCode "
			    + "order by strKOTNo desc ,strSerialNo ");
		 List listBussyTableItems = objBaseService.funGetList(sqlBuilderTableItemDtl,"sql");
		 if(listBussyTableItems.size()>0)
		 {
			 for(int i=0;i<listBussyTableItems.size();i++)
			 {
				 Object[] obj = (Object[])listBussyTableItems.get(i);
				 clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
				 objBean.setStrItemCode(obj[0].toString());//item code
				 objBean.setStrItemName(obj[1].toString());//item name
				 objBean.setDblAmount(Double.parseDouble(obj[2].toString()));//rate
				 objBean.setDblQuantity(Double.parseDouble(obj[3].toString()));//item quantity
				 objBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));//amount
				 objBean.setDblTaxAmt(Double.parseDouble(obj[5].toString()));//tax amount
				 objBean.setStrWaiterName(obj[6].toString()); //waiter number
				 objBean.setDblModifiedAmount(Double.parseDouble(obj[7].toString())); //fired qty
				 listRet.add(objBean);
			 }
		 }
		
		 return listRet;
		 
	}
	
	public List funLoadResonCode(){
		
		List listResonCombo = new ArrayList();	
		
		try {

		   
		    Map mapObj =funLoadReson();
			List list = (List) mapObj.get("jArr");
			for(int i=0;i<list.size();i++)
			{
				Map jObjtemp =(Map) list.get(i);
				listResonCombo .add( jObjtemp.get("resoncode").toString());
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return listResonCombo;
	}
	
	public Map funLoadReson()
    {
    	Map objReturn=new  HashMap(); 
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
       	    List listSql=  objBaseService.funGetList(sqlBuilder, "sql");
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
              
           	    List listSqlReason=objBaseService.funGetList(sqlBuilder, "sql");
           	    if(listSqlReason.size()>0)
           	     {
             	    for(int j=0;j<listSqlReason.size();j++)
             	    {
             		Map jobj=new  HashMap();
            	    Object obj = (Object ) listSqlReason.get(j);
            	    i = 0;
            	    jobj.put("resoncode",obj.toString() );
            	    jarr.add(jobj); 

                    }
           	     }
               
           	    objReturn.put("jArr", jarr);
            }
        }
        catch (Exception e)
        {
          
            e.printStackTrace();
        }
        return objReturn;
    }
	
	@RequestMapping(value = "/saveMoveItemsToTable", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSBillItemDtlBean objBean,BindingResult result,HttpServletRequest req)
	{
		String toTableName ="",fromTableName="";
		String posCode = req.getSession().getAttribute("gPOSCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String fromTableNo = objBean.getStrTableName();
		String toTableNo = objBean.getStrCustomerCode();
		try
		{
			StringBuilder sqlBuilderToTblName = new StringBuilder("select a.strTableName from tbltablemaster a where a.strTableNo='"+toTableNo+"'");
			List list = objBaseService.funGetList(sqlBuilderToTblName, "sql");
			if(list.size()>0)
			{
				toTableName = list.get(0).toString();
			}
			
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strStatus,intPaxNo,strTableName "
				    + " from tbltablemaster "
				    + " where strTableNo='" + fromTableNo + "'");
			   list = objBaseService.funGetList(sqlBuilder, "sql");
			    int pax = 0;
			    if (list.size()>0)
			    {
			    for(int i=0;i<list.size();i++)
			    {
			    	Object[] obj = (Object[])list.get(i);
			    	String status = obj[0].toString();
					fromTableName = obj[2].toString();
					pax = Integer.parseInt(obj[1].toString());
					sqlBuilder.setLength(0);
					sqlBuilder.append("update tbltablemaster set strStatus='" + status + "',intPaxNo=" + pax + " "
						+ " where strTableNo='" + toTableNo + "'");
					objBaseService.funExecuteUpdate(sqlBuilder.toString(), "sql");
				    
			    }
				}
			    
			    List<clsMakeKotItemDtl> listOfItemsForFromTable = new ArrayList<clsMakeKotItemDtl>();
			    List<clsMakeKotItemDtl> listOfItemsForToTable = new ArrayList<clsMakeKotItemDtl>();
			    String oldKOTNo = "";

			    StringBuilder sqlBuilderOldKOTNo = new StringBuilder("select a.strKOTNo "
				    + "from tblitemrtemp a  "
				    + "where strTableNo='" + fromTableNo + "'  "
				    + "and strPosCode='" + posCode + "' "
				    + "and strNcKotYN='N' "
				    + "limit 1 ");
			    List listOfOldKOTNo = objBaseService.funGetList(sqlBuilderOldKOTNo, "sql");
			    if (listOfOldKOTNo.size()>0)
			    {
				oldKOTNo = (String) listOfOldKOTNo.get(0);
			    }
			    
			    String newKOTNo = funGenerateKOTNo();
			    
			    List listOfBussyTablDtl = objBean.getListBusyTableDtl();
			    if(listOfBussyTablDtl.size()>0)
			    {
			    	for(int i=0;i<listOfBussyTablDtl.size();i++)
			    	{
			    		clsPOSBillItemDtlBean obj = (clsPOSBillItemDtlBean)listOfBussyTablDtl.get(i);
			    		String itemName = obj.getStrItemName();
			    		double itemRate = obj.getDblAmount();
			    		double itemQty = obj.getDblQuantity();
			    		double moveQty =obj.getFreeItemQty();
			    		boolean isItemSelected = false;
			    		if(obj.getStrApplicableYN()!=null)
			    		{	
			    		isItemSelected = obj.getStrApplicableYN();
			    		}
			    		String itemCode = obj.getStrItemCode();
			    		String waiterNo = obj.getStrWaiterName();
			    		double firedQty = obj.getDblModifiedAmount();

			    		if (isItemSelected)
			    		{
			    		    itemQty = itemQty - moveQty;
			    		}
			    		
			    		//for original items
			    		double itemAmt = itemRate * itemQty;
			    		clsMakeKotItemDtl objItemForFromTable = new clsMakeKotItemDtl(String.valueOf(i), oldKOTNo, fromTableNo, waiterNo, itemName, itemCode, itemQty, itemAmt, pax, "Y", "N", false, "", "", "", "N", itemRate);
			    		objItemForFromTable.setDblFiredQty(firedQty);

			    		listOfItemsForFromTable.add(objItemForFromTable);

			    		//for move items 
			    		if (isItemSelected)
			    		{
			    		    double moveItemAmt = itemRate * moveQty;
			    		    clsMakeKotItemDtl objItemForToTable = new clsMakeKotItemDtl(String.valueOf(i), newKOTNo, toTableNo, waiterNo, itemName, itemCode, moveQty, moveItemAmt, pax, "Y", "N", false, "", "", "", "N", itemRate);
			    		    objItemForToTable.setDblFiredQty(firedQty);

			    		    listOfItemsForToTable.add(objItemForToTable);
			    		}
			    		
			    	}
			    }
			    funUpdateDataInTempTable("Update", listOfItemsForFromTable, fromTableNo,posCode,userCode);
			    funInsertDataInTempTable("Insert", listOfItemsForToTable,posCode,userCode);

			    sqlBuilder.setLength(0);
				sqlBuilder.append("select strKOTNo,dteDateCreated from tblitemrtemp where strTableNo='" + fromTableNo + "' and strNCKotYN='N' ");
			    List listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			    if(listOfData.size()>0)
			    {
			    	
			    }
			    else
			    {
			    	sqlBuilder.setLength(0);
					sqlBuilder.append( "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
					+ "where strTableNo='" + fromTableNo + "'");
					objBaseService.funExecuteUpdate(sqlBuilder.toString(), "sql");
			    }

			    //insert into itemrtempbck tabl
			    objPOSUtilityController.funInsertIntoTblItemRTempBck(fromTableNo);
			    objPOSUtilityController.funInsertIntoTblItemRTempBck(toTableNo);
			    
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage", "Item Shifted to " + toTableNo);


			return new ModelAndView("redirect:/frmMoveItemsToTable.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	  private void funInsertDataInTempTable(String type, List<clsMakeKotItemDtl> listOfMakeKOTItemDtl,String posCode,String userCode)
	    {
		//private synchronized void fun_Insert_Data_tblitemrtemp() {
		try
		{
		    String homeDelivery = "No", customerName = "", customerCode = "";

		    String takeAway = "No";

		    String counterCode = "NA";
//		    if (clsGlobalVarClass.gCounterWise.equals("Yes"))
//		    {
//			counterCode = clsGlobalVarClass.gCounterCode;
//		    }

		    String tableNo = "";
		    String KOTNo = "";
		    double KOTAmt = 0, taxAmt = 0;
		    String delBoyCode = "";

		    String insertQuery = "insert into tblitemrtemp (strSerialNo,strTableNo,strCardNo,dblRedeemAmt,strPosCode,strItemCode"
			    + ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
			    + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
			    + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
			    + ",dblRate,dblTaxAmt,strDelBoyCode,dblFiredQty ) values ";
		    for (clsMakeKotItemDtl listItemDtl : listOfMakeKOTItemDtl)
		    {

			tableNo = listItemDtl.getTableNo();
			KOTNo = listItemDtl.getKOTNo();

			insertQuery += "('" + listItemDtl.getSequenceNo() + "','" + listItemDtl.getTableNo() + "'"
				+ ",'','0.00','" + posCode + "'"
				+ ",'" + listItemDtl.getItemCode() + "','" + homeDelivery + "','" + customerCode + "'"
				+ ",'" + listItemDtl.getItemName() + "','" + listItemDtl.getQty() + "','" + listItemDtl.getAmt() + "'"
				+ ",'" + listItemDtl.getWaiterNo() + "','" + listItemDtl.getKOTNo() + "'"
				+ ",'" + listItemDtl.getPaxNo() + "','" + listItemDtl.getPrintYN() + "'"
				+ ",'" + userCode + "','" + userCode + "'"
				+ ",'" +  objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" +  objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
				+ ",'" + takeAway + "','N','" + customerName + "','" + counterCode + "'"
				+ ",'" + listItemDtl.getItemRate() + "','0.00','" + delBoyCode + "','"+listItemDtl.getDblFiredQty()+"'),";
			KOTAmt += listItemDtl.getAmt();
		    }
		    StringBuilder sb = new StringBuilder(insertQuery);
		    int index = sb.lastIndexOf(",");
		    insertQuery = sb.delete(index, sb.length()).toString();
		    //System.out.println(insertQuery);
		    objBaseService.funExecuteUpdate(insertQuery, "sql");
		    
		    StringBuilder sqlBuilder = new StringBuilder("insert into tblkottaxdtl "
			    + "values ('" + tableNo + "','" + KOTNo + "'," + KOTAmt + "," + taxAmt + ")");
		    objBaseService.funExecuteUpdate(sqlBuilder.toString(), "sql");

		    //insert into itemrtempbck table
		    objPOSUtilityController.funInsertIntoTblItemRTempBck(tableNo);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
//		    JOptionPane.showMessageDialog(null, "Error In Print KOT-" + e.getMessage());
		}
	    }

	
	 private void funUpdateDataInTempTable(String type, List<clsMakeKotItemDtl> listOfMakeKOTItemDtl, String fromTableNo,String posCode,String userCode)
	    {

		try
		{

		    for (clsMakeKotItemDtl itemDtl : listOfMakeKOTItemDtl)
		    {

			String itemCode = itemDtl.getItemCode();
			double itemQty = itemDtl.getQty();

			if (itemQty <= 0)
			{
			    StringBuilder sqlBuilderDeleteQuery = new StringBuilder("delete from tblitemrtemp  "
				    + "where strItemCode='" + itemCode + "' "
				    + "and strTableNo='" + fromTableNo + "' "
				    + "and strNCKotYN='N' ");
			    objBaseService.funExecuteUpdate(sqlBuilderDeleteQuery.toString(), "sql");

			}
			else
			{
			    StringBuilder sqlBuilderDeleteQuery = new StringBuilder("delete from tblitemrtemp  "
				    + "where strItemCode='" + itemCode + "' "
				    + "and strTableNo='" + fromTableNo + "' "
				    + "and strNCKotYN='N' ");
			    objBaseService.funExecuteUpdate(sqlBuilderDeleteQuery.toString(), "sql");

			    StringBuilder sqlBuilderInsertQuery = new StringBuilder();
			    sqlBuilderInsertQuery.setLength(0);
			    sqlBuilderInsertQuery.append("insert into tblitemrtemp (strSerialNo,strTableNo,strCardNo,dblRedeemAmt,strPosCode,strItemCode"
				    + ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
				    + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
				    + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
				    + ",dblRate,dblTaxAmt,strDelBoyCode,dblFiredQty ) values ");

			    sqlBuilderInsertQuery.append("('" + itemDtl.getSequenceNo() + "','" + fromTableNo + "'"
				    + ",'','0.00','" + posCode + "'"
				    + ",'" + itemDtl.getItemCode() + "','NO','' "
				    + ",'" + itemDtl.getItemName() + "','" + itemQty + "','" + itemDtl.getAmt() + "'"
				    + ",'" + itemDtl.getWaiterNo() + "','" + itemDtl.getKOTNo() + "'"
				    + ",'" + itemDtl.getPaxNo() + "','" + itemDtl.getPrintYN() + "'"
				    + ",'" + userCode + "','" + userCode + "'"
				    + ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
				    + ",'','N','','' "
				    + ",'" + itemDtl.getItemRate() + "','0.00','','"+itemDtl.getDblFiredQty()+"')");

			    objBaseService.funExecuteUpdate(sqlBuilderInsertQuery.toString(), "sql");
			}
		    }

		    //insert into itemrtempbck table
		    objPOSUtilityController.funInsertIntoTblItemRTempBck(fromTableNo);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
//		    JOptionPane.showMessageDialog(null, "Error In Print KOT-" + e.getMessage());
		}
	    }
	 
	
	private String funGenerateKOTNo()
    {
	String kotNo = "";
	boolean gUpdatekot=false;
	try
	{
	    long code = 0;
	    StringBuilder sqlBuilder = new StringBuilder("select dblLastNo from tblinternal where strTransactionType='KOTNo'");
	    List listOfKOT = objBaseService.funGetList(sqlBuilder, "sql");
	    if (listOfKOT.size()>0)
	    {
		code = Long.parseLong(listOfKOT.get(0).toString());
		code = code + 1;
		kotNo = "KT" + String.format("%07d", code);
//		clsGlobalVarClass.gUpdatekot = true;
//		clsGlobalVarClass.gKOTCode = code;
	    }
	    else
	    {
		kotNo = "KT0000001";
//		clsGlobalVarClass.gUpdatekot = false;
	    }
	  
	    StringBuilder sql = new StringBuilder("update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'");
	    objBaseService.funExecuteUpdate(sql.toString(), "sql");

	}
	catch (Exception e)
	{
	     e.printStackTrace();
	}
	return kotNo;
    }

	
}
