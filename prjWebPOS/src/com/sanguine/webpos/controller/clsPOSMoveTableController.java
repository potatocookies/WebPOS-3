package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSMoveTableBean;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;



@Controller
public class clsPOSMoveTableController {
	
	
	 @Autowired 
	 private clsBaseServiceImpl objBaseServiceImpl;
	
	 Map<String, String> map=new HashMap<String, String>();
	 Map<String, String> mapForOccupiedTables=new HashMap<String, String>();
	 List<clsPOSTableMasterBean> listTable=new ArrayList<clsPOSTableMasterBean>();
	@RequestMapping(value = "/frmPOSMoveTable", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
	
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			
			return new ModelAndView("frmPOSMoveTable_1","command", new clsPOSMoveTableBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMoveTable","command", new clsPOSMoveTableBean());
		}else {
			return null;
		}
		 
	}
	
	
	
	
	@RequestMapping(value = "/saveMoveTable", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMoveTableBean objBean,BindingResult result,HttpServletRequest req)
	{
		
		try
		{
			
		    List<clsPOSTableMasterBean> listAllTable=objBean.getListOfAllTable();
		    List<clsPOSTableMasterBean> listOccupiedTable=objBean.getListOfOccupiedTable();
		    String movedFromTableName="",movedFromTableNo="",movedToTableName="",movedToTableNo="";
	    			    
		    if(null!=listOccupiedTable)
		    {
		    	for(int i=0;i<listOccupiedTable.size();i++)
		    	{
		    		clsPOSTableMasterBean objTableDtl =listOccupiedTable.get(i);
		    		movedFromTableName=objTableDtl.getStrTableName();
		    		if(mapForOccupiedTables.size()>0)
		    		{
		    			for(String key: mapForOccupiedTables.keySet()) 
		    			{
		    			    if(mapForOccupiedTables.get(key).equals(movedFromTableName)) 
		    			    {
		    			        movedFromTableNo=key;
		    			    }
		    			}
		    		}
		    	}
		    }	
		    if(null!=listAllTable ) 
		    {
		    	for(int i=0;i<listAllTable.size();i++)
		    	{
		    		clsPOSTableMasterBean objTableDtl =listAllTable.get(i);
		    		movedToTableName=objTableDtl.getStrTableName();
		    		if(map.size()>0)
		    		{
		    			for(String key: map.keySet()) 
		    			{
		    			    if(map.get(key).equals(movedToTableName)) 
		    			    {
		    			    	movedToTableNo=key;
		    			    }
		    			}
		    		}
		    	}
		    	
		    }
		     
		    String retResult="";
		    List list =null;
		    String sql="update tblitemrtemp set strTableNo='"+movedToTableNo+"' "
                    + "where strTableNo='"+movedFromTableNo+"'";
		    objBaseServiceImpl.funExecuteUpdate(sql,"sql");	
			
			
			StringBuilder sqlBuilder = new StringBuilder(); 
			sqlBuilder.append("select strStatus,intPaxNo,strTableName,strPOSCode "
                    + "from tbltablemaster where strTableNo='"+movedFromTableNo+"' ");
		    
			list = objBaseServiceImpl.funGetList(sqlBuilder,"sql");

			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj1=list.get(i);
					    String status=(String) Array.get(obj1, 0);
	                    int pax=(int) Array.get(obj1, 1);
	                    String pos=(String) Array.get(obj1, 3);
	                    
	                    sql="update tbltablemaster set strStatus='"+status+"',intPaxNo="+pax+" "
	                            + "where strTableNo='"+movedToTableNo+"'";
	                    objBaseServiceImpl.funExecuteUpdate(sql,"sql");
	                        
                        sql="update tbltablemaster set strStatus='Normal',intPaxNo=0 "
                            + "where strTableNo='"+movedFromTableNo+"'";
                        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
                        
                        sql="update tblitemrtemp set strPOSCode='"+pos+"' "
                                + " where strTableNo='"+movedToTableNo+"'";
                        objBaseServiceImpl.funExecuteUpdate(sql,"sql");
            			retResult="Tabled Moved From "+movedFromTableNo+" to "+movedToTableNo+" ";
					}
				}
			
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+retResult);
		
			return new ModelAndView("redirect:/frmPOSMoveTable.html?saddr");
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	
	
	@RequestMapping(value = "/LoadMoveTableData", method = RequestMethod.GET)
	public @ResponseBody clsPOSMoveTableBean funLoadTableData(@RequestParam("TableStatus") String tableStatus,HttpServletRequest req)
	{
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		clsPOSMoveTableBean obj=new clsPOSMoveTableBean();
		List<clsPOSTableMasterBean> listTableData=new ArrayList<clsPOSTableMasterBean>();
		List list =null;
		
		try
		{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select strTableNo,strTableName,strStatus from tbltablemaster ");
		if (!tableStatus.equalsIgnoreCase("All"))
	    {
			sqlBuilder.append(" where strStatus='"+tableStatus+"' and strPOSCode='"+posCode+"' ");
	    }
		else
		{
			sqlBuilder.append( " where strPOSCode='"+posCode+"' ");
		}

		sqlBuilder.append( "  order by intSequence ");
	 
		list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		
		 if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj1=list.get(i);
				
					clsPOSTableMasterBean objTableDtl = new clsPOSTableMasterBean();
					
					String tableNo="",tableName="",tblStatus="";
					tableNo= (String) Array.get(obj1, 0);
					tableName= (String) Array.get(obj1, 1);
					tblStatus = (String) Array.get(obj1, 2);
					objTableDtl.setStrTableNo(tableNo);
					objTableDtl.setStrTableName(tableName);	
					objTableDtl.setStrStatus(tblStatus);
					listTableData.add(objTableDtl);
					listTable.add(objTableDtl);
					if(tableStatus.equalsIgnoreCase("Occupied"))
					{
						mapForOccupiedTables.put(tableNo,tableName);
					}
					else
					{
						map.put(tableNo,tableName);	
					}
					
				}
				if(!tableStatus.equals("All"))
				{
					obj.setListOfOccupiedTable(listTableData);
				}
				else
				{
					obj.setListOfAllTable(listTableData);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return obj;
		}

	}
	
	
 private clsPOSMoveTableBean funGetTableList(String posCode,String tableStatus)
 {
	clsPOSMoveTableBean obj=new clsPOSMoveTableBean();
	List<clsPOSTableMasterBean> listTableData=new ArrayList<clsPOSTableMasterBean>();
	List list =null;
	
	try
	{
	StringBuilder sqlBuilder = new StringBuilder();
	sqlBuilder.append("select strTableNo,strTableName,strStatus from tbltablemaster ");
	if (!tableStatus.equalsIgnoreCase("All"))
    {
		sqlBuilder.append(" where strStatus='"+tableStatus+"' and strPOSCode='"+posCode+"' ");
    }
	else
	{
		sqlBuilder.append( " where strPOSCode='"+posCode+"' ");
	}

	sqlBuilder.append( "  order by intSequence ");
 
	list =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	 if (list!=null)
		{
			for(int i=0; i<list.size(); i++)
			{
				Object obj1=list.get(i);
			
				clsPOSTableMasterBean objTableDtl = new clsPOSTableMasterBean();
				String tableNo="",tableName="",tblStatus="";
				tableNo= (String) Array.get(obj1, 0);
				tableName= (String) Array.get(obj1, 1);
				tblStatus = (String) Array.get(obj1, 2);
				objTableDtl.setStrTableNo(tableNo);
				objTableDtl.setStrTableName(tableName);	
				objTableDtl.setStrStatus(tblStatus);
				listTableData.add(objTableDtl);
				listTable.add(objTableDtl);
				if(tableStatus.equalsIgnoreCase("Occupied"))
				{
					mapForOccupiedTables.put(tableName, tableNo);
				}
				else
				{
					map.put(tableName, tableNo);	
				}
				
			}
			if(!tableStatus.equals("All"))
			{
				obj.setListOfOccupiedTable(listTableData);
			}
			else
			{
				obj.setListOfAllTable(listTableData);
			}
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
		return obj;
	}
 }		
	 
		
 
 
         
		

}
