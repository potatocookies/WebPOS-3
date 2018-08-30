package com.sanguine.webpos.controller;

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
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillHd;
import com.sanguine.webpos.util.clsPOSSetupUtility;

@Controller
public class clsPOSChangeCustomerOnBillController {
	

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired 
	clsPOSSetupUtility objPOSSetupUtility;
	
	@RequestMapping(value = "/frmPOSChangeCustomerOnBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) {
		String urlHits="1";
	
		
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeCustomerOnBill_1","command", new clsPOSBillDtl());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSChangeCustomerOnBill","command", new clsPOSBillDtl());
		}else {
		return null;
		}
	}
	
		
			  
	  @RequestMapping(value = "/loadBillForChangeCustomer", method = RequestMethod.GET)
	  public @ResponseBody List funFillRGridData(HttpServletRequest request){	
	  String searchText="";
	  List listFillGrid=new ArrayList();
        try
        {
        	String posCode = request.getSession().getAttribute("gPOSCode").toString();
        	String posDate=request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
    		StringBuilder sqlBuilder=new StringBuilder(); 
    		sqlBuilder.append( "select a.strBillNo,TIME_FORMAT(time(a.dteBillDate),'%h:%i') as dteBillDate,ifnull(b.strTableName,''),a.strPOSCode "
                + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                + " where a.strPOSCode='"+posCode+"' AND(  a.strBillNo Like'%" + searchText + "%' or a.dteBillDate like '%" + searchText + "%' "
                + " or b.strTableName like '%" + searchText + "%') "
                + " and date(dteBillDate)='" + posDate + "' "
                + " order by a.strTableNo ");
    	 	List listOfBill=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listOfBill.size()>0)
		    {
		    	 for(int cnt=0;cnt<listOfBill.size();cnt++)
	         	    {
		    		 Object[] obj = (Object[]) listOfBill.get(cnt);
          
              
    				List setFillGrid=new ArrayList();
    				setFillGrid.add(obj[0].toString());
    				setFillGrid.add( obj[1].toString());
    				setFillGrid.add( obj[2].toString());
    				listFillGrid.add(setFillGrid);
                
            }
		    }
            

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return listFillGrid;
    }
			  
	  @RequestMapping(value = "/loadSelectedBillItem", method = RequestMethod.GET)
	  private @ResponseBody Map funFillItemGrid(@RequestParam("billNo") String billNo,HttpServletRequest request)
	    {
		  List listFillGrid=new ArrayList();
	      Map  hmDataFillInGrid=new HashMap();
	        try
	        {
	            
	        	String posCode = request.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = request.getSession().getAttribute("gClientCode").toString();
	            List<ArrayList<Object>> arrListItemDtls = new ArrayList<ArrayList<Object>>();

	            StringBuilder sqlBuilder=new StringBuilder(); 
	    		sqlBuilder.append("select strItemName,strBillNo,dblQuantity,dblAmount,dteBillDate,strItemCode,strKOTNo "
	                + " from tblbilldtl where strBillNo='" + billNo + "' ;");
	           
	        	List listBillDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if(listBillDtl.size()>0)
				{
				 for(int cnt=0;cnt<listBillDtl.size();cnt++)
			     {
				    Object[] obj = (Object[]) listBillDtl.get(cnt);
		          
	                ArrayList<Object> arrListItemRow = new ArrayList<Object>();
	                String itemCode = obj[5].toString();
	                
	                List setFillGrid=new ArrayList();
    				setFillGrid.add( obj[0].toString());
    				setFillGrid.add( obj[2].toString());
    				setFillGrid.add( obj[3].toString());
    				setFillGrid.add( obj[5].toString());
    				setFillGrid.add( obj[6].toString());
    				listFillGrid.add(setFillGrid);
    				
	                arrListItemRow.add(obj[5].toString());
	                arrListItemRow.add(obj[3].toString());
	                arrListItemDtls.add(arrListItemRow);
	                sqlBuilder.setLength(0);
		    		sqlBuilder.append( "select strModifierName,dblQuantity,dblAmount,strItemCode,strModifierCode from tblbillmodifierdtl "
	                      + "where strItemCode='" + itemCode + "' and strBillNo='" + billNo + "' ;");
	              
                  	List listModBillDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					if(listModBillDtl.size()>0)
					{
					 for(int i=0;i<listModBillDtl.size();i++)
				     {
					    Object[] objModItem = (Object[]) listModBillDtl.get(i);
                    String modItemCode = objModItem[4].toString() + objModItem[3].toString();
                
                    setFillGrid=new ArrayList();
    				setFillGrid.add( obj[0].toString());
    				setFillGrid.add( obj[1].toString());
    				setFillGrid.add( obj[2].toString());
    				setFillGrid.add( modItemCode);
    				setFillGrid.add( "");
    				listFillGrid.add(setFillGrid);
	                 
	                 }
			        }
	            }
	        }
	           
	            
	            double discountAmt = 0;
	            double discountPer = 0;
	            double subTotal = 0;
	            double grandTotal = 0;
	            double taxAmt = 0;
	            String userCreated="",strCustomerCode="",strCustomerName="";
	        	String gShowItemDetailsGrid=objPOSSetupUtility.funGetParameterValuePOSWise( clientCode,  posCode,  "gShowItemDetailsGrid");
	          
	          
	            sqlBuilder.setLength(0);
	    		sqlBuilder.append("select a.dblTaxAmt,a.dblSubTotal,a.dblGrandTotal,a.strUserCreated ,a.dblDiscountAmt"
	                + ",a.dblDiscountPer ,ifnull(b.strCustomerCode,'ND') as strCustomerCode,ifnull(b.strCustomerName,'ND') as strCustomerName "
	                + "from tblbillhd a left outer join  tblcustomermaster b on  a.strCustomerCode=b.strCustomerCode "
	                + " where a.strBillNo='" + billNo + "'");
	          
	            List listBillCustomer=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			    if(listBillCustomer.size()>0)
				{
				  for(int cnt=0;cnt<listBillCustomer.size();cnt++)
				  {
				    Object[] obj = (Object[]) listBillCustomer.get(cnt);
	            	
	                taxAmt =Double.parseDouble(obj[0].toString());
	                subTotal = Double.parseDouble(obj[1].toString());
	                grandTotal = Double.parseDouble(obj[2].toString());;
	                userCreated = obj[3].toString();
	                discountAmt = Double.parseDouble(obj[4].toString());
	                discountPer = Double.parseDouble(obj[5].toString());
	                strCustomerCode = obj[6].toString();
	                strCustomerName = obj[7].toString();

	              }
	           }
			    hmDataFillInGrid.put("gridData", listFillGrid);
			    hmDataFillInGrid.put("taxAmt", taxAmt);
			    hmDataFillInGrid.put("subTotal", subTotal);
			    hmDataFillInGrid.put("grandTotal", grandTotal);
			    hmDataFillInGrid.put("userCreated", userCreated);
			    hmDataFillInGrid.put("discountAmt", discountAmt);
			    hmDataFillInGrid.put("discountPer", discountPer);
			    hmDataFillInGrid.put("strCustomerCode", strCustomerCode);
			    hmDataFillInGrid.put("strCustomerName", strCustomerName);
			    hmDataFillInGrid.put("gShowItemDetailsGrid", gShowItemDetailsGrid);
			    
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return hmDataFillInGrid;
	    }

		@RequestMapping(value = "/saveChangeCustomerOnBill", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSBillHd objBean,BindingResult result,HttpServletRequest req)
		{
			try
			{
				String posCode = req.getSession().getAttribute("gPOSCode").toString();
	        	String clientCode = req.getSession().getAttribute("gClientCode").toString();
				String gCMSIntegrationYN=objPOSSetupUtility.funGetParameterValuePOSWise( clientCode,  posCode,  "gCMSIntegrationYN");
				String strCustomerCode = objBean.getStrCustomerCode();
				if(gCMSIntegrationYN.equalsIgnoreCase("Y"))
				{
				  if(strCustomerCode.equalsIgnoreCase("ND"))
		            {
					  strCustomerCode="";
		            }
		            StringBuilder sqlBuilder = new StringBuilder("update tblbillhd set strCustomerCode='" + strCustomerCode + "' \n"
		                + "where strClientCode='" + clientCode+ "' and strPOSCode='" + posCode + "' and strBillNo='" + objBean.getStrBillNo() + "' ");
		           objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

				}
				else
				{
					
		            StringBuilder sqlBuilder = new StringBuilder("update tblbillhd set strCustomerCode='" + strCustomerCode + "' \n"
		                + "where strClientCode='" + clientCode + "' and strPOSCode='" + posCode + "' and strBillNo='" + objBean.getStrBillNo() + "' ");
		            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
		            
		            sqlBuilder.setLength(0);
		            sqlBuilder.append("update tblhomedelivery set strCustomerCode='" + strCustomerCode + "' \n"
		                + "where strClientCode='" + clientCode + "' and strPOSCode='" + posCode + "' and strBillNo='" + objBean.getStrBillNo() + "' ");
		            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");           
		           
				}
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage","Cusomer Updated Successfully");
										
				return new ModelAndView("redirect:/frmPOSChangeCustomerOnBill.html");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
		
		
}