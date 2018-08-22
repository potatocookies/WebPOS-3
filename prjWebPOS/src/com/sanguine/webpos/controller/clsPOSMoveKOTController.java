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
import com.sanguine.webpos.bean.clsPOSMoveKOTBean;
@Controller
public class clsPOSMoveKOTController {

	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;

	List  listTableNo=new ArrayList<String>();
	Map map=new HashMap();
	List openKOTList=new ArrayList();
	List openTableList=new ArrayList();
	Map tableList = new HashMap<>();
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

			sqlBuilder.setLength(0);
			sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
					+ "where strPOSCode='" + strPosCode + "' and strOperational='Y'");
			list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");

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
			String openTableNo = (String) map.get(KOTNo);

			List list=null;
			int pax=0;
			String sql="";
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
	public @ResponseBody Map loadKOTData(HttpServletRequest req)
	{
		Map mapOfKOTData=new HashMap();
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
			List listData=new ArrayList();
			List listOfKOTData=new ArrayList();
			if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
					Map objMap=new HashMap();
					objMap.put("KOTNo",Array.get(obj, 0));
					objMap.put("TableNo",Array.get(obj, 1));
					listOfKOTData.add(Array.get(obj, 0));
					listData.add(objMap);
				}

			}
			if(null!=listData)
			{
				for(int i=0; i<listData.size();i++)
				{
					Map objMapData=(Map) listData.get(i);
					map.put((String)objMapData.get("KOTNo"),(String)objMapData.get("TableNo"));
				}
			}
			mapOfKOTData.put("KOTList",listOfKOTData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mapOfKOTData;
	}

	@RequestMapping(value = "/loadTableData", method = RequestMethod.GET)
	public @ResponseBody List funGetTableDtl(HttpServletRequest req)
	{
		String posCode=req.getSession().getAttribute("gPOSCode").toString();
		listTableNo.clear();
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
			list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
			if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					Map objMapSettle=new HashMap();
					objMapSettle.put("TableNo",obj[0].toString());
					objMapSettle.put("TableName",obj[1].toString());
					listTableNo.add(obj[0].toString());
					sqlBuilder.setLength(0);
					sqlBuilder.append("select strTableNo,strStatus from tbltablemaster where strTableNo='"+obj[0].toString()+"'");
					List sList = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
					Object[] objS = (Object[]) sList.get(0);
					String status=objS[1].toString();
					objMapSettle.put("Status",status);
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


	@RequestMapping(value = "/loadSearchTableData", method = RequestMethod.GET)
	public @ResponseBody List funGetSearchTable(HttpServletRequest req)
	{
		String posCode=req.getSession().getAttribute("gPOSCode").toString();
		String searchTableName = req.getParameter("tblSearch"); 
		listTableNo.clear();
		List list =null;
		Map mapObjTableData=new HashMap();
		List listData=new ArrayList();
		try{
			StringBuilder sqlBuilder = new StringBuilder();

			if(searchTableName!="")
			{
				sqlBuilder.setLength(0);
				sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
						+ "where strOperational='Y' and strTableName like '"+searchTableName+"%'");
				if(!posCode.equals("All"))
				{
					sqlBuilder.append(" and strPOSCode='"+posCode+"' ");
				}
				sqlBuilder.append(" order by intSequence ;");
			}
			else
			{
				sqlBuilder.setLength(0);
				sqlBuilder.append("select strTableNo,strTableName from tbltablemaster "
						+ "where strPOSCode='" + strPosCode + "' and strOperational='Y'");
			}
			list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
			if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					Map mapObj=new HashMap();
					mapObj.put("TableNo",obj[0].toString());
					mapObj.put("TableName",obj[1].toString());
					listTableNo.add(obj[0].toString());
					sqlBuilder.setLength(0);
					sqlBuilder.append("select strTableNo,strStatus from tbltablemaster where strTableNo='"+obj[0].toString()+"'");
					List sList = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
					Object[] objS = (Object[]) sList.get(0);
					String status=objS[1].toString();
					mapObj.put("Status",status);
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
					mapObj.put("Pax",pax);
					listData.add(mapObj);
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

}
