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
import org.springframework.web.servlet.ModelAndView;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationBean;
import com.sanguine.webpos.bean.clsPOSUnsettleBillTransactionBean;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;


@Controller
public class clsPOSUnSettleBillTransactionController {

		@Autowired
		private clsGlobalFunctionsService objGlobalFunctionsService;
		@Autowired
		private clsGlobalFunctions objGlobal;
		
		@Autowired
		private clsPOSGlobalFunctionsController objGlobalFun;
		
		@Autowired
		private clsPOSMasterService objMasterService;
		
		@Autowired
		private intfBaseService objBaseService;
		

		Map mapReason=new HashMap();

		@RequestMapping(value = "/frmUnsettleBill", method = RequestMethod.GET)
		public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception{
			String urlHits="1";
			String clientCode=request.getSession().getAttribute("gClientCode").toString();

			 List listOfReasons = objMasterService.funGetAllReasonMaster(clientCode);
			 mapReason.put("All", "All");
			 if(listOfReasons!=null)
			 {
				for(int i =0 ;i<listOfReasons.size();i++)
				{
					clsReasonMasterModel objModel = (clsReasonMasterModel) listOfReasons.get(i);
					mapReason.put(objModel.getStrReasonCode(),objModel.getStrReasonName());
				}
			}
			model.put("ReasonNameList",mapReason);
			
			try{
				urlHits=request.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmUnsettleBill_1","command", new clsPOSUnsettleBillTransactionBean());
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmUnsettleBill","command", new clsPOSUnsettleBillTransactionBean());
			}else {
			return null;
			}
		}
		
		
		@RequestMapping(value ="/saveUnStettleBill", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSUnsettleBillTransactionBean objBean,BindingResult result,HttpServletRequest req)
		{
			String urlHits="1";
			String strReasoneName = null;
	        String res=""; 
			try
			{
				urlHits=req.getParameter("saddr").toString();
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
				String posCode=req.getSession().getAttribute("loginPOS").toString();
				String strReasonCode= objBean.getStrReasonCode();
				
				if(mapReason.containsKey(strReasonCode))
				{
					strReasoneName=(String) mapReason.get(strReasonCode);
				}

				Map hmData=new HashMap();
				hmData.put("BillNo", objBean.getStrBillNo());
				hmData.put("posCode",posCode);
				hmData.put("posDate", req.getSession().getAttribute("gPOSDate"));
				hmData.put("ReasonCode",strReasonCode);
				hmData.put("ReasoneName",strReasoneName);
				hmData.put("User", webStockUserCode);
				hmData.put("ClientCode", clientCode);
				hmData.put("dteDateCreated", objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				hmData.put("dteDateEdited", objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				System.out.println("omg="+hmData);
				
				funSaveUnStettleBill(hmData);
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+ objBean.getStrBillNo());
										
				return new ModelAndView("redirect:/frmUnsettleBill.html?saddr="+urlHits);
			}
			catch(Exception ex)
			{
				urlHits="1";
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
		
		
		public String funSaveUnStettleBill(Map hmUnsettleBillData){	
			String BillNo = "";
			StringBuilder sql = new StringBuilder();
			try
			{
				
				BillNo = hmUnsettleBillData.get("BillNo").toString();
			    String srtReasoneCode = hmUnsettleBillData.get("ReasonCode").toString();
			    String strReasoneName = hmUnsettleBillData.get("ReasoneName").toString();
			    String posCode = hmUnsettleBillData.get("posCode").toString();
			    String user = hmUnsettleBillData.get("User").toString();
			    String posDate = hmUnsettleBillData.get("posDate").toString();
			    String clientCode = hmUnsettleBillData.get("ClientCode").toString();
			    String dateTime = objGlobal.funGetCurrentDateTime("yyyy-MM-dd");

		            if(!srtReasoneCode.equalsIgnoreCase(""))
		            {
		               sql.setLength(0);
		               sql.append("select dteBillDate,dblGrandTotal,strClientCode,strTableNo,strWaiterNo,strAreaCode"
		                    + ",strPosCode,strOperationType,strSettelmentMode,intShiftCode "
		                    + " from tblbillhd where strBillNo='"+BillNo+"'");
		               List listSqlModLive = objBaseService.funGetList(sql, "sql");
		   			   if(listSqlModLive.size()>0)
			 		    {
			 			   int i;
			 		    
			 			   for( i=0 ;i<listSqlModLive.size();i++ )
			 		       {
			 				   Object[] objM = (Object[]) listSqlModLive.get(i);
			 				  
			 		    	   sql.setLength(0);
			 		    	   sql.append("insert into tblvoidbillhd(strPosCode,strReasonCode,strReasonName,strBillNo,"
		                        + "dblActualAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo,"
		                        + "intShiftCode,strUserCreated,strUserEdited,strClientCode)"
		                        + " values('"+posCode+"','"+srtReasoneCode+"','"+strReasoneName
		                        +"','"+BillNo+"','"+objM[1]+"','"+objM[0]
		                        +"','USBill','"+dateTime+"','"+objM[3]+"','"+objM[4]
		                        +"','"+objM[9]+"','"+user+"','"+user
		                        +"','"+clientCode+"')");
			 		    	   objBaseService.funExecuteUpdate(sql.toString(),"sql");
			 		    	  
			 		    	 
			 		    	 sql.setLength(0);
			 		    	 sql.append("select strBillNo from tblbillsettlementdtl a,tblsettelmenthd b "
		                        +" where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='"+BillNo+"' "
		                        +" and b.strSettelmentType='Debit Card' ");
		                   
			 		    	List listSql = objBaseService.funGetList(sql, "sql");
			 	 		    if(listSql.size()>0)
			 	 		    {
			 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
			 	 		       	{
			 	 					sql.setLength(0);
			                        sql.append("select strCardNo,dblTransactionAmt,strPOSCode,dteBillDate "
			                            + " from tbldebitcardbilldetails where strBillNo='"+BillNo+"' ");
			                        
			                        List list = objBaseService.funGetList(sql, "sql");  
				 	 	 		    if(list.size()>0)
				 	 	 		     {
				 	 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
				 	 	 		       {	
				 	 	 				 Object[] obj = (Object[]) listSqlModLive.get(i);
				 	 	 				 funDebitCardTransaction(BillNo, obj[0].toString(), Double.parseDouble(obj[1].toString()), "Unsettle",posCode,posDate );
				 	 	 				 funUpdateDebitCardBalance(obj[0].toString(),Double.parseDouble(obj[1].toString()), "Unsettle");
				                        }//inner for close
				                     }//inner if close
			 	 		       	}//for close
			 	 		    }//if close
		                   
		 	 	 		     sql.setLength(0);
		                     sql.append("select b.strSettelmentType from tblbillsettlementdtl a,tblsettelmenthd b "
		                        +" where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='"+BillNo+"' ");
		                    
		                     List list1 = objBaseService.funGetList(sql, "sql");   
			  	 	 		  if(list1.size()>0)
			  	 	 		    {
			  	 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
			  	 	 		    
			  	 	 		    	{	
			  	 	 				   Object[] obj = (Object[]) listSqlModLive.get(i);
				                        if(obj[0].toString().equals("Complementary"))
				                        {
				                        	funMoveComplimentaryBillToBillDtl(BillNo,obj[6].toString(),obj[5].toString(),obj[7].toString(),clientCode);
				                        }
			  	 	 		    	}
			                    }
		                    
			  	 	 	        sql.setLength(0);
			                    sql.append("delete from tblbillsettlementdtl where strBillNo='"+BillNo+"'");
			                    int unsettleExc= objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                    
			                    sql.setLength(0);          
			                    sql.append("update tblbillhd set strDataPostFlag='N',strSettelmentMode='' where strBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                    
			                    sql.setLength(0);
			                    sql.append("update tblbilldtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                         
			                    sql.setLength(0); 
			                    sql.append("update tblbillmodifierdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                            
			                    sql.setLength(0);
			                    sql.append("update tblbilltaxdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                    
			                    sql.setLength(0);
			                    sql.append("update tblbillseriesbilldtl set strDataPostFlag='N' where strHdBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                    
			                    sql.setLength(0);
			                    sql.append("update tblbilldiscdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
			                    objBaseService.funExecuteUpdate(sql.toString(),"sql");
			                    
			                    BillNo="Unsettled Succeccfully";
			 	 		  
		                }//for close
			 		   }//1st sql if close
		            }//if close
		            
		        }
		        catch(Exception e)
		        {
		           e.printStackTrace();
		        }
			  return BillNo;
			}
		
		
		
		 public int funDebitCardTransaction(String billNo, String debitCardNo, double debitCardSettleAmt, String transType, String posCode, String posDate)
		    {
			   StringBuilder sql = new StringBuilder();
		        try
		        {
		        	 sql.append("delete from tbldebitcardbilldetails "
		                    + "where strBillNo='" + billNo + "' and strTransactionType='" + transType + "' ");
		        	 objBaseService.funExecuteUpdate(sql.toString(),"sql");
		        

		        	 sql.setLength(0);
	                 sql.append("insert into tbldebitcardbilldetails (strBillNo,strCardNo,"
		                    + "dblTransactionAmt,strPOSCode,dteBillDate,strTransactionType)"
		                    + "values ('" + billNo + "','" + debitCardNo + "','" + debitCardSettleAmt + "'"
		                    + ",'" +posCode + "','" + posDate + "'"
		                    + ",'" + transType + "')");
		        	 objBaseService.funExecuteUpdate(sql.toString(),"sql"); 
			        
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		        return 1;
		    }
		
		    public int funUpdateDebitCardBalance(String debitCardNo, double debitCardSettleAmt, String transType)
		    {
		    	StringBuilder sql = new StringBuilder();
		        try
		        {
		           sql.append("select dblRedeemAmt from tbldebitcardmaster "
		                    + "where strCardNo='" + debitCardNo + "'");
		           List listSqlModLive = objBaseService.funGetList(sql, "sql");   
		 		   if(listSqlModLive.size()>0)
		 		    {		 			  
		 		       for(int i=0 ;i<listSqlModLive.size();i++ )
		 		    	{
		 				   Object[] obj = (Object[]) listSqlModLive.get(i);
			                double amt = Double.parseDouble(obj[1].toString());
			                double updatedBal = amt - debitCardSettleAmt;
			                if (transType.equals("Unsettle"))
			                {
			                    updatedBal = amt + debitCardSettleAmt;
			                }
			                sql.setLength(0);
			                sql.append("update tbldebitcardmaster set dblRedeemAmt='" + updatedBal + "' "
			                        + "where strCardNo='" + debitCardNo + "'");
			                objBaseService.funExecuteUpdate(sql.toString(),"sql"); 
		                 }
		 		    }
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		        return 1;
		    }
		    
		    
		    
		    public int funMoveComplimentaryBillToBillDtl(String billNo,String POSCode,String billAreaCode, String operationTypeForTax, String clientCode) throws Exception
    	    {
		       StringBuilder sql = new StringBuilder();
		       
		        sql.append("delete from tblbilldtl where strBillNo='" + billNo + "'");
    	        objBaseService.funExecuteUpdate(sql.toString(),"sql"); 
    	        
    	        sql.setLength(0);
                sql.append("insert into tblbilldtl "
    	                + " select * from tblbillcomplementrydtl where strBillNo='"+billNo+"' ");
                objBaseService.funExecuteUpdate(sql.toString(),"sql");  
    	        
                sql.setLength(0);
                sql.append("select strItemCode,strItemName,dblAmount,dblDiscountAmt "
    	                + "from tblbilldtl where strBillNo='"+billNo+"' ");
    	        double subTotal=0.0;
    	        double disTotal=0.0;
    	    
    	        List<clsPOSItemDetailFrTaxBean> arrListItemDtls=new ArrayList<clsPOSItemDetailFrTaxBean>();
    	        List listSqlModLive = objBaseService.funGetList(sql, "sql");
		         if(listSqlModLive.size()>0)
		 		   {		 			  
		 		       for(int i=0 ;i<listSqlModLive.size();i++ )
		 		       {
		 				   Object[] obj = (Object[]) listSqlModLive.get(i);
		 				   clsPOSItemDetailFrTaxBean objItemDtl=new clsPOSItemDetailFrTaxBean();
		    	            objItemDtl.setItemCode(obj[0].toString());
		    	            objItemDtl.setItemName(obj[1].toString());
		    	            objItemDtl.setAmount(Double.parseDouble(obj[2].toString()));
		    	            objItemDtl.setDiscAmt(Double.parseDouble(obj[3].toString()));
		    	            subTotal+=Double.parseDouble(obj[2].toString());
		    	            disTotal+=Double.parseDouble(obj[3].toString());
		    	            arrListItemDtls.add(objItemDtl);
		 		    	}
		 		    }
    	       
		 			   
		 	    double disper=0.00;
    	        if(subTotal>0)
    	        {
    	            disper=(disTotal/subTotal)*100;
    	        }
    	        clsPOSUtilityController obj=new clsPOSUtilityController();
    	        List<clsPOSTaxCalculationBean> arrListTaxCal = obj.funCalculateTax(arrListItemDtls,POSCode,"",billAreaCode,operationTypeForTax,0,0,"Tax Regen","Cash");
    	        
    	        
    	        sql.setLength(0);
                sql.append("delete from tblbilltaxdtl where strBillNo='" +billNo + "'");
    	        double taxAmt=0.0;
    	        objBaseService.funExecuteUpdate(sql.toString(),"sql"); 
    	       
    	        for(clsPOSTaxCalculationBean objTaxCalDtl : arrListTaxCal)
    	        {            
    	        	sql.setLength(0);
                    sql.append("insert into tblbilltaxdtl "
    	                + "(strBillNo,strTaxCode,dblTaxableAmount,dblTaxAmount,strClientCode) "
    	                + "values('" + billNo + "','" + objTaxCalDtl.getTaxCode()+ "'"
    	                + "," + objTaxCalDtl.getTaxableAmount() + "," + objTaxCalDtl.getTaxAmount() + ""
    	                + ",'" + clientCode + "')");
    	            taxAmt+=objTaxCalDtl.getTaxAmount();
    	            objBaseService.funExecuteUpdate(sql.toString(),"sql");   
    	        }
    	        double grandTotal=((subTotal+taxAmt)-disTotal);
    	      
    	        sql.setLength(0);
                sql.append("update tblbillhd set dblDiscountAmt='"+disTotal+"',dblDiscountPer='"+disper+"',"
    	            + "dblTaxAmt='"+taxAmt+"',dblSubTotal='"+subTotal+"',dblGrandTotal='"+grandTotal+"' "
    	            + "where strBillNo='"+billNo+"'");
    	        objBaseService.funExecuteUpdate(sql.toString(),"sql");  
    	        
    	        sql.setLength(0);
                sql.append("delete from tblbillcomplementrydtl where strBillNo='" + billNo + "'");
    	        objBaseService.funExecuteUpdate(sql.toString(),"sql");  
    	        
    	        return 0;
    	    }

			


}
