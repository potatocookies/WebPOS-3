package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSMoveKOTBean;
@Controller
public class clsPOSMoveKOTController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;
	
	List  listTableNo=new ArrayList<String>();
	Map map=new HashMap();
	List openKOTList=new ArrayList();
	List openTableList=new ArrayList();
	String posDate="",strPosCode="",clientCode="",userCode="";
	@RequestMapping(value = "/frmPOSMoveKOT", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSMoveKOTBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		
	    strPosCode=request.getSession().getAttribute("loginPOS").toString();
	    posDate=request.getSession().getAttribute("gPOSDate").toString();
		clientCode=request.getSession().getAttribute("gClientCode").toString();
		userCode=request.getSession().getAttribute("gUserCode").toString();
		
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster");
			List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			Map posList = new HashMap<>();
			posList.put("All", "All");
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
				
				posList.put(Array.get(obj, 0), Array.get(obj, 1));
			}
			model.put("posList", posList);
			
			JSONArray jArrData=new JSONArray();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
	            + "where strPOSCode='" + strPosCode + "' and strOperational='Y'");
			list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			Map tableList = new HashMap<>();
			tableList.put("All", "All");
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object obj=list.get(cnt);
			    tableList.put(Array.get(obj, 0), Array.get(obj, 1));
			}
			
			Map treeMap = new TreeMap<>(tableList);
	        model.put("tableList",treeMap);
	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
			return new ModelAndView("frmPOSMoveKOT");
		
		 
	}
	@RequestMapping(value = "/saveMoveKOT", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMoveKOTBean objBean,BindingResult result,HttpServletRequest req,@RequestParam("KOTNo") String KOTNo,@RequestParam("tableName") String tableNoTo,@RequestParam("selectedIndx") int selectedIndx)
	{
		
		try
		{
			//String tableNoTo=(String) listTableNo.get(selectedIndx);
			String openTableNo = (String) map.get(KOTNo);
			
			List list=null;
			int pax=0;
			String sql="";/*update tblitemrtemp set strTableNo='"+tableNoTo+"' "
	                	+ "where strKOTNo='"+KOTNo+"'";
			objBaseServiceImpl.funExecuteUpdate(sql,"sql");	*/
	           
	        StringBuilder sqlBuilder = new StringBuilder();   
	        sqlBuilder.append("select strStatus,intPaxNo from tbltablemaster "
	                + " where strTableNo='"+openTableNo+"'");
	             
	             list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
	            if(list.size()>0)
	            {
	            	Object[] objS = (Object[]) list.get(0);
					String status=objS[0].toString();
	                
	                pax=Integer.parseInt(objS[1].toString());
	                sql="update tbltablemaster set strStatus='"+status+"',intPaxNo="+pax+" "
	                    + " where strTableNo='"+tableNoTo+"'";
	                objBaseServiceImpl.funExecuteUpdate(sql,"sql");		
	            }
	            sqlBuilder.setLength(0);
	            sqlBuilder.append("select a.strItemCode,a.strItemName,sum(a.dblItemQuantity),sum(a.dblAmount),dteDateCreated,a.strWaiterNo "
	                    + "from tblitemrtemp a "
	                    + "where strKOTNo='" + KOTNo + "' "
	                    + "group by a.strItemCode ");
	            
	            List listFromTableItems=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	            String itemCode = "", itemName = "", waiterNo = "",createdDate="";
	            String strType = "MVKot", voidedDate = funGetVodidedDate();
	            double quantity = 0.0, amount = 0.0;
	            
	            if(listFromTableItems.size()>0){
	            	for(int i=0;i<listFromTableItems.size();i++){
	            		Object ob[]=(Object[])listFromTableItems.get(i);
	            		
	            		itemCode =ob[0].toString();
		                itemName = ob[1].toString();
		                quantity = Double.parseDouble(ob[2].toString());
		                amount = Double.parseDouble(ob[3].toString());
		                createdDate = ob[4].toString();
		                waiterNo = ob[5].toString();

		                String insertQuery = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode, "
		                        + " strItemName,dblItemQuantity,dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode, "
		                        + " strUserCreated,dteDateCreated,dteVoidedDate,strClientCode,strRemark,strVoidBillType ) "
		                        + " values ";

		                insertQuery += "('" + tableNoTo + "','" + strPosCode + "','" + itemCode + "','" + itemName + "',"
		                        + "'" + quantity + "','" + amount + "','" + waiterNo + "','" + KOTNo + "','" + pax + "','" + strType + "'"
		                        + ",'','" + userCode + "','" + createdDate + "','" + voidedDate + "'"
		                        + ",'" + clientCode + "','','Move KOT') ";
		                
		                objBaseServiceImpl.funExecuteUpdate(insertQuery, "sql");
	            	}
	            	
	            }
	           
	            sql = "update tblitemrtemp set strTableNo='" + tableNoTo + "' "
	                    + "where strKOTNo='" + KOTNo + "' ";
	            objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	            
	            sqlBuilder.setLength(0);
	            sqlBuilder.append("select strPOSCode from tbltablemaster "
	                + " where strTableNo='"+tableNoTo+"'");
	            
	            list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
	            if(list.size()>0)
	            {
	            	String posCode = (String) list.get(0);
				    sql="update tblitemrtemp set strPOSCode='"+posCode+"' "
	                    + " where strKOTNo='"+KOTNo+"'";
	                objBaseServiceImpl.funExecuteUpdate(sql,"sql");	
	            }
	            
	            sqlBuilder.setLength(0);
	            sqlBuilder.append("select strKOTNo from tblitemrtemp where strTableNo='"+openTableNo+"' and strNCKotYN='N' ");
	            list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
	            if(list.size()==0)
	            {
	                sql="update tbltablemaster set strStatus='Normal',intPaxNo=0 "
	                    + "where strTableNo='"+openTableNo+"'";
	                objBaseServiceImpl.funExecuteUpdate(sql,"sql");	
	            }
	             //insert into itemrtempbck tabl
	           

	            funInsertIntoTblItemRTempBck(tableNoTo);
	            funInsertIntoTblItemRTempBck(openTableNo);
	            
			
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+ openTableNo + " Shifted to " + tableNoTo);

									
			return new ModelAndView("redirect:/frmPOSMoveKOT.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}

	@RequestMapping(value = "/loadKOTData", method = RequestMethod.GET)
	public @ResponseBody JSONObject loadKOTData(HttpServletRequest req)
	{
		JSONObject jObjKOTData=new JSONObject();
		try
		{
				String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
				String tableNo=req.getParameter("tableNo");
				String posCode=req.getParameter("gPOSCode");
				List list =null;
			
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select distinct(strKOTNo),strTableNo from tblitemrtemp ");
	        if(!tableNo.equals("All"))
	        {
	            if(null==posCode || posCode.equalsIgnoreCase("All")){
	            	sqlBuilder.append(" where strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"' and strNCKOTYN='N' ");
	            }
	            else{
	            	sqlBuilder.append(" where strTableNo='"+tableNo+"' and strPOSCode='"+posCode+"' and strNCKOTYN='N'  ");
	            }                
	        }
	        else
	        {        
	            if(null==posCode || posCode.equalsIgnoreCase("All")){
	            	sqlBuilder.append(" where strPOSCode='"+loginPosCode+"' and strNCKOTYN='N' ");
	            }
	            else{
	            	sqlBuilder.append(" where strPOSCode='"+posCode+"' and strNCKOTYN='N' ");
	            }                
	        }
			 list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
			 JSONArray jArrData=new JSONArray();
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
					jArrData.add(objSettle);
				}
	           
		      }
		    if(null!=jArrData)
	        {
	        	for(int i=0; i<jArrData.size();i++)
	        	{
					JSONObject jobj=(JSONObject) jArrData.get(i);
					map.put((String)jobj.get("KOTNo"),(String)jobj.get("TableNo"));
	        	}
	        }
	        jObjKOTData.put("KOTList",jArrKOTData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObjKOTData;
}

@RequestMapping(value = "/loadTableData", method = RequestMethod.GET)
public @ResponseBody JSONArray funGetTableDtl(HttpServletRequest req)
{
	String posCode=req.getSession().getAttribute("gPOSCode").toString();
	listTableNo.clear();
	List list =null;
	JSONObject jObjTableData=new JSONObject();
	JSONArray jArrData=new JSONArray();
	try{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
                		+ "where strOperational='Y' ");
        if(!posCode.equals("All"))
        {
        	sqlBuilder.append(" and strPOSCode='"+posCode+"' ");
        }
        sqlBuilder.append(" order by intSequence ;");
    	list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
        if (list.size()>0)
		{
			for(int i=0; i<list.size(); i++)
			{
				Object[] obj = (Object[]) list.get(i);
				JSONObject objSettle=new JSONObject();
				objSettle.put("TableNo",obj[0].toString());
				objSettle.put("TableName",obj[1].toString());
				listTableNo.add(obj[0].toString());
				sqlBuilder.setLength(0);
				sqlBuilder.append("select strTableNo,strStatus from tbltablemaster where strTableNo='"+obj[0].toString()+"'");
				List sList = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
				Object[] objS = (Object[]) sList.get(0);
				String status=objS[1].toString();
				objSettle.put("Status",status);
				int pax=0;
				if(status.equals("Occupied"))
                {
				  	sqlBuilder.setLength(0);
				  	sqlBuilder.append("select intPaxNo from tblitemrtemp where strTableNo='"+obj[0].toString()+"' ");
                    List pList = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
                    if(pList.size()>0)
                    {
                    	pax=(int) pList.get(0);
                    }
                }
			  	objSettle.put("Pax",pax);
				jArrData.add(objSettle);
			}
           	jObjTableData.put("TableDtl", jArrData);
	      }
		            
		}
	catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jArrData;
		}
   }

private String funGetVodidedDate()
{
    String voidDate = null;
    try
    {
        java.util.Date dt = new java.util.Date();
        String time = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
        String bdte = posDate;
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date bDate = dFormat.parse(bdte);
        voidDate = (bDate.getYear() + 1900) + "-" + (bDate.getMonth() + 1) + "-" + bDate.getDate();
        voidDate += " " + time;

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    finally
    {
        return voidDate;
    }
}


	public void funInsertIntoTblItemRTempBck(String tableNo)
	{
	    try
	    {
	    	String sql = "delete from tblitemrtemp_bck where strTableNo='" + tableNo + "'  ";
	        objBaseServiceImpl.funExecuteUpdate(sql,"sql");	
			 
	        sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + tableNo + "'  )";
	        objBaseServiceImpl.funExecuteUpdate(sql,"sql");	
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}
