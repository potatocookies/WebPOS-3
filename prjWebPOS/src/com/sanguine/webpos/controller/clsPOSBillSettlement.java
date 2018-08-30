package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSettlementsBean;

@Controller
public class clsPOSBillSettlement {
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	@Autowired 
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	
	@RequestMapping(value = "/frmPOSRestaurantDtl", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
	
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmSettleBillFrontEnd_1","command",   new clsPOSBillSettlementsBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmSettleBillFrontEnd","command",   new clsPOSBillSettlementsBean());
		}else {
			return null;
		}
		 
	}
	
	@RequestMapping(value = "/fillUnsettleBillData", method = RequestMethod.GET)
	public @ResponseBody Map funFillUnSettleBill(Map<String, Object> model,HttpServletRequest req)
	{
		List listUnsettlebill= new ArrayList();
		Map hmUnsettleBill= new HashMap();
		try {
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String strPosCode=req.getSession().getAttribute("loginPOS").toString();
			String posDate=req.getSession().getAttribute("gPOSDate").toString();
			hmUnsettleBill=funFillUnsettleBill(clientCode,strPosCode,posDate);
		    String  gShowBillsType = hmUnsettleBill.get("gShowBillsType").toString();
		    String gCMSIntegrationYN =hmUnsettleBill.get("gCMSIntegrationYN").toString();
		    List listData = (List) hmUnsettleBill.get("jArr");
			
			for(int i=0;i<listData.size();i++)
			{
				LinkedList setFillGrid=new LinkedList();
				Map hmtemp =(Map) listData.get(i);
				if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	            {
					setFillGrid.add(hmtemp.get("strBillNo").toString());
					setFillGrid.add( hmtemp.get("strTableName").toString());
					setFillGrid.add( hmtemp.get("strWShortName").toString());
					setFillGrid.add( hmtemp.get("strCustomerName").toString());
					setFillGrid.add( hmtemp.get("dteBillDate").toString());
					setFillGrid.add( Double.parseDouble(hmtemp.get("dblGrandTotal").toString()));
					listUnsettlebill.add(setFillGrid);
	            }else{
	            	setFillGrid.add(hmtemp.get("strBillNo").toString());
					setFillGrid.add( hmtemp.get("strTableName").toString());
					setFillGrid.add( hmtemp.get("strCustomerName").toString());
					setFillGrid.add( hmtemp.get("strBuildingName").toString());
					setFillGrid.add( hmtemp.get("strDPName").toString());
					setFillGrid.add( hmtemp.get("dteBillDate").toString());
					setFillGrid.add( Double.parseDouble(hmtemp.get("dblGrandTotal").toString()));
					listUnsettlebill.add(setFillGrid);
	            	
	            }
				
				
				hmUnsettleBill.put("listUnsettlebill", listUnsettlebill);
				hmUnsettleBill.put("gShowBillsType", gShowBillsType);
				hmUnsettleBill.put("gCMSIntegrationYN", gCMSIntegrationYN);
				
				
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	return hmUnsettleBill;
	}
	
	
	@RequestMapping(value = "/fillBillSettlementData", method = RequestMethod.GET)
	public ModelAndView funOpenBillSettlement(@ModelAttribute("command") clsPOSBillSettlementsBean objBean,Map<String, Object> model,HttpServletRequest request) 
	{
		String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
		String urlHits="2";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);

		String billNo=objBean.getStrBillNo();
		String selectedTableNo=objBean.getStrTableNo();
		String selectedRowIndex=objBean.getSelectedRow();		
		String billType="";
	
		String posUrl = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSBillSettlement/fillRowSelected";
		JSONObject objRows = new JSONObject();
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		String strPosCode=request.getSession().getAttribute("loginPOS").toString();
		JSONObject jobj=objPOSGlobalFunctionsController.funGetPOSDate(request);
		String posDate=jobj.get("POSDate").toString();
		String isSuperUser=request.getSession().getAttribute("superuser").toString();
		boolean superuser=true;
		if("YES".equalsIgnoreCase(isSuperUser))
		{
			superuser=true;
		}
		
		request.setAttribute("billNo", billNo);
	
		
		objRows.put("billNo", billNo);
		objRows.put("selectedTableNo", selectedTableNo);
		objRows.put("selectedRowIndex", selectedRowIndex);
		objRows.put("clientCode", clientCode);
		objRows.put("posCode", strPosCode);
		objRows.put("billType", billType);
		objRows.put("superuser", superuser);
		
		
	    JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posUrl,objRows);
	    
	    List listSettlemode=(List) jObj.get("jArrSettlementMode");
	    model.put("listSettlemode", listSettlemode);
	    
//	    String path=request.getContextPath().toString();
//	    try{
////	    String searchUrl="/fillBillSettlementData.html?";
//	    res.sendRedirect("fillBillSettlementData.html?");
//	    
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmBillSettlement_1","command", new clsPOSBillSettlementsBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmBillSettlement","command",new clsPOSBillSettlementsBean());
		}else {
			return null;
		}
		
		 
	}
	
	
	 public Map funFillUnsettleBill(String clientCode,String posCode,String posDate)
	    {
		 Map hmReturn=new HashMap();
		 List listData=new ArrayList();
		 StringBuilder sql = new StringBuilder();
	        try
	        {
	           Map hmBillType= objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
	           String gShowBillsType=hmBillType.get("gShowBillsType").toString();
	           Map  hmCMSIntegrationYN= objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
	           String gCMSIntegrationYN=hmCMSIntegrationYN.get("gCMSIntegrationYN").toString();
	           
	           hmReturn.put("gShowBillsType",gShowBillsType);
	           hmReturn.put("gCMSIntegrationYN",gCMSIntegrationYN);
	           
	            if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	            {
	            	sql.append("select a.strBillNo,ifnull(b.strTableNo,''),ifnull(b.strTableName,''),ifnull(c.strWaiterNo,'')"
	                    + " ,ifnull(c.strWShortName,''),ifnull(d.strCustomerCode,''),ifnull(d.strCustomerName,''),a.dblGrandTotal"
	                    + " ,DATE_FORMAT(a.dteBillDate,'%h:%i:%s')  "
	                    + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo"
	                    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo"
	                    + " left outer join tblcustomermaster d on a.strCustomerCode=d.strCustomerCode"
	                    + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) "
	                    + " and date(a.dteBillDate)='" + posDate + "' "
	                    + " and a.strPOSCode='" + posCode + "' ");
	            }
	            else//Delivery Detail Wise
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
	                    + " AND DATE(a.dteBillDate)='" +posDate +"' "
	                    + " AND a.strPOSCode='" +posCode+ "' "
	                    + " group by a.strBillNo");
	            }
	            List listPendBillData = objBaseService.funGetList(sql, "sql");
	            if(listPendBillData.size()>0)
	            {
	             for(int i=0;i<listPendBillData.size();i++)
	             {
	            	Object []obj=(Object[]) listPendBillData.get(i);
	                Map hmData =new HashMap();
	                if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	                {
	                    hmData.put("strBillNo", obj[0].toString());
	                	hmData.put("strTableName", obj[2].toString());
	                	hmData.put("strWShortName", obj[4].toString());
	                	hmData.put("strCustomerName", obj[6].toString());
	                	hmData.put("dteBillDate", obj[8].toString());
	                	hmData.put("dblGrandTotal", obj[7].toString());
	                }
	                else//Delivery Detail Wise
	                {
	                	hmData.put("strBillNo", obj[0].toString());
	                	hmData.put("strTableName", obj[6].toString());
	                	hmData.put("strCustomerName", obj[1].toString());
	                	hmData.put("strBuildingName", obj[2].toString());
	                	hmData.put("strDPName", obj[3].toString());
	                	hmData.put("dteBillDate", obj[7].toString());
	                	hmData.put("dblGrandTotal", obj[4].toString());
	                }
	                listData.add(hmData);
	            }
	        }
	          
	            hmReturn.put("jArr",listData);  
	          
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return hmReturn;
	    }
}
