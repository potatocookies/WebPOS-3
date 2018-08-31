package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSItemDtlForTax;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.bean.clsPOSTaxRegenerationBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSTaxRegenerationController {
	
	
	@Autowired 
	clsPOSMasterService  objMasterService ;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	clsPOSUtilityController objUtility;
	
	@Autowired
	private clsPOSSetupUtility objPOSSetupUtility;
	
	@RequestMapping(value = "/frmPOSTaxRegeneration", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request) throws Exception{
		String urlHits="1";
		
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		List posList=objMasterService.funFillPOSCombo(clientCode);
		Map hmPos=new HashMap();
		hmPos.put("All","All");
		if(posList.size()>0)
		{
			for(int i=0;i<posList.size();i++)
			{
				Object[] obj=(Object[])posList.get(0);
				hmPos.put(obj[0].toString(),obj[1].toString());	
			}
		}
    	String posDate=request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		 model.put("posDate", posDate);  	
			model.put("posList",hmPos);
		
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmTaxRegeneration_1","command", new clsPOSTaxRegenerationBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmTaxRegeneration","command", new clsPOSTaxRegenerationBean());
		}else {
		return null;
		}
	}

	
	
	@RequestMapping(value = "/saveTaxREgeneration", method = RequestMethod.POST)
	public ModelAndView funGenerateTax(@ModelAttribute("command") @Valid clsPOSTaxRegenerationBean objBean,BindingResult result,HttpServletRequest req)
	{
                                          
		String urlHits="1";
		String clientCode = req.getSession().getAttribute("gClientCode").toString();		

		try
		{
			urlHits=req.getParameter("saddr").toString();
        String fromDate =objBean.getDteFromDate();
        String []fdate=fromDate.split("-");
        fromDate=fdate[2]+"-"+fdate[1]+"-"+fdate[0];
        String toDate = objBean.getDteToDate();
        String []tdate=toDate.split("-");
        toDate=tdate[2]+"-"+tdate[1]+"-"+tdate[0];
        String poscode=objBean.getStrPOSCode();
        if (poscode.equalsIgnoreCase("All"))
        {
        	List posList=objMasterService.funFillPOSCombo(clientCode);
            for (int i = 0; i < posList.size(); i++)
            {
            	Object[] obj=(Object[])posList.get(i);
                funRegenerateTax(obj[0].toString(), fromDate, toDate,req);
            }
        }
        else
        {
           
            funRegenerateTax(poscode, fromDate, toDate,req);
        }
        req.getSession().setAttribute("success", true);
		req.getSession().setAttribute("successMessage","");
		String posCode = req.getSession().getAttribute("gPOSCode").toString();
         return new ModelAndView("redirect:/frmGetPOSSelection.html?strPosCode="+posCode);
		}catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
        
    }
	
	
	 private int funRegenerateTax(String posCode, String fromDate, String toDate,HttpServletRequest request)
	    {
	        try
	        {
	            String clientCode = request.getSession().getAttribute("gClientCode").toString();
	            
	            StringBuilder sqlBuilder=new StringBuilder(); 
	    		sqlBuilder.append( "select a.strBillNo,ifnull(b.strAreaCode,''),a.strOperationType,ifnull(d.strSettelmentType,'Cash') "
	                    + " ,a.dblSubTotal,a.dblDiscountPer,a.strSettelmentMode,a.dblGrandTotal,c.dblSettlementAmt,date(a.dteBillDate)"
	                    + ",d.strSettelmentCode,a.dteBillDate "
	                    + " from tblqbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and DATE(a.dteBillDate)=DATE(c.dteBillDate) "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and a.strPOSCode='" + posCode + "' ");
	                    //+ " and a.strBillNo='FP0105388' ";
	    		List listOfBill=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
//	            while (rsBills.next())
//	            {
	    		if(listOfBill.size()>0)
				{
				  for(int cnt=0;cnt<listOfBill.size();cnt++)
			      {
				    Object[] objBills = (Object[]) listOfBill.get(cnt);
	                String billNo = objBills[0].toString();
	                String area = objBills[1].toString();
	                if (area.trim().length() == 0)
	                {
	                    area =objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gDirectAreaCode");;
	                }
	                String operationType = objBills[2].toString();
	                double subTotal = Double.parseDouble(objBills[4].toString());
	                double discPer = Double.parseDouble( objBills[5].toString());
	                String filterDate = objBills[9].toString();
	                String settlementCode = objBills[10].toString();
	                String billDate = objBills[11].toString();
	                sqlBuilder.setLength(0);
		    		sqlBuilder.append( "select sum(dblDiscAmt) from tblqbilldiscdtl   "
	                        + " where strBillNo='" + billNo + "' "
	                        + " and date(dteBillDate)='" + filterDate + "' ");
	              
	                List listBillDiscAmt=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	                double dblBillDiscAmt = 0.00;
	                if (listBillDiscAmt.size()>0)
	                {
	                    dblBillDiscAmt = (double) listBillDiscAmt.get(0);
	                }

	              
	                List<clsPOSItemDtlForTax> arrListItemDtlForTax = new ArrayList<clsPOSItemDtlForTax>();
	                sqlBuilder.setLength(0);
		    		sqlBuilder.append( "select strItemCode,strItemName,dblAmount,dblDiscountAmt,dblDiscountPer,strKOTNo "
	                        + "from tblqbilldtl "
	                        + "where strBillNo='" + billNo + "' "
	                        + "and date(dteBillDate)='" + filterDate + "' ");
//	                ResultSet rsBillDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	                List listBillDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	                if (listBillDtl.size()>0)
	                {
	                	 for(int i=0;i<listBillDtl.size();i++)
	                	 {
	                	 Object[] objBillDtl = (Object[]) listBillDtl.get(i);
	                     clsPOSItemDtlForTax objItemDtlForTax = new clsPOSItemDtlForTax();
	                     objItemDtlForTax.setItemCode(objBillDtl[0].toString());
	                     objItemDtlForTax.setItemName(objBillDtl[1].toString());
	                     objItemDtlForTax.setAmount(Double.parseDouble(objBillDtl[2].toString()));
	                     objItemDtlForTax.setDiscAmt(Double.parseDouble( objBillDtl[3].toString()));
	                     objItemDtlForTax.setDiscPer(Double.parseDouble(objBillDtl[4].toString()));

	                     arrListItemDtlForTax.add(objItemDtlForTax);
	                    }
	                }

	                //modifiers
	                sqlBuilder.setLength(0);
		    		sqlBuilder.append( "select strItemCode,strModifierName,dblAmount,dblDiscAmt,dblDiscPer "
	                        + "from tblqbillmodifierdtl "
	                        + "where strBillNo='" + billNo + "' "
	                        + "and date(dteBillDate)='" + filterDate + "' ");
	               
	                List listBillModiDtl=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	                if (listBillModiDtl.size()>0)
	                {
	                	 for(int i=0;i<listBillModiDtl.size();i++)
	                	 {
	                	 Object[] objBillModDtl = (Object[]) listBillModiDtl.get(i);
	                    clsPOSItemDtlForTax objItemDtlForTax = new clsPOSItemDtlForTax();
	                    objItemDtlForTax.setItemCode(objBillModDtl[0].toString());
	                    objItemDtlForTax.setItemName(objBillModDtl[1].toString());
	                    objItemDtlForTax.setAmount(Double.parseDouble(objBillModDtl[2].toString()));
	                    objItemDtlForTax.setDiscAmt(Double.parseDouble(objBillModDtl[3].toString()));
	                    objItemDtlForTax.setDiscPer(Double.parseDouble(objBillModDtl[4].toString()));

	                    arrListItemDtlForTax.add(objItemDtlForTax);
	                }
			      }

	                String settlementType = objBills[3].toString();
	                if (operationType.equalsIgnoreCase("DirectBiller"))
	                {
	                    operationType = "DineIn";
	                }
	                
//	                (List<clsItemDtlForTax> arrListItemDtl,String POSCode,String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountPer, String transType)
	                List<clsPOSTaxCalculationDtls> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtlForTax, posCode, filterDate, area, operationType, subTotal, dblBillDiscAmt, "Tax Regen");
	                System.out.println(arrListTaxDtl.size() + "\t" + operationType + "\t" + area);
	                String sql_BillTaxDtl = "insert into tblqbilltaxdtl "
	                        + "(strBillNo,strTaxCode,dblTaxableAmount,dblTaxAmount,strClientCode,strDataPostFlag,dteBillDate ) "
	                        + " values ";
	                String billTaxDtlData = "";
	                boolean flgData = false;

	                for (clsPOSTaxCalculationDtls objTaxCalDtl : arrListTaxDtl)
	                {
	                    billTaxDtlData += "('" + billNo + "','" + objTaxCalDtl.getTaxCode() + "','" + objTaxCalDtl.getTaxableAmount() + "'"
	                            + ",'" + objTaxCalDtl.getTaxAmount() + "','" + clientCode + "','N','" + billDate + "'),";
	                    flgData = true;
	                }

	                if (flgData)
	                {
	                    System.out.println(billNo);
	                    if (billNo.equals("P0321452"))
	                    {
	                        System.out.println(billNo);
	                    }

	                    sql_BillTaxDtl += " " + billTaxDtlData;
	                    sql_BillTaxDtl = sql_BillTaxDtl.substring(0, (sql_BillTaxDtl.length() - 1));
	                    //System.out.println(sql_BillTaxDtl);

	                    sqlBuilder.setLength(0);
			    		sqlBuilder.append( "delete from tblqbilltaxdtl "
	                            + "where strBillNo='" + billNo + "' "
	                            + "and date(dteBillDate)='" + filterDate + "' ");
	                  
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                    objBaseServiceImpl.funExecuteUpdate(sql_BillTaxDtl,"sql");

	                    try
	                    {
	                        if (objBills[3].toString().equals("Complementary"))
	                        {
	                        	sqlBuilder.setLength(0);
	         		    		sqlBuilder.append( "update tblqbilltaxdtl set dblTaxableAmount=0.00,dblTaxAmount=0.00 "
	                                    + "where strBillNo='" + billNo + "' "
	                                    + "and date(dteBillDate)='" + filterDate + "' ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                            sqlBuilder.setLength(0);
	        		    		sqlBuilder.append( "update tblqbillhd set dblTaxAmt=0.00,dblSubTotal=0.00"
	                                    + ",dblDiscountAmt=0.00,dblDiscountPer=0.00,dblGrandTotal=0.00 "
	                                    + "where strBillNo='" + billNo + "' "
	                                    + "and date(dteBillDate)='" + filterDate + "' ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                            sqlBuilder.setLength(0);
	        		    		sqlBuilder.append( "update tblqbilldtl set dblAmount=0.00,dblDiscountAmt=0.00,dblDiscountPer=0.00 "
	                                    + "where strBillNo='" + billNo + "' "
	                                    + "and date(dteBillDate)='" + filterDate + "' ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                            sqlBuilder.setLength(0);
	        		    		sqlBuilder.append( "update tblqbillmodifierdtl set dblAmount=0.00 where strBillNo='" + billNo + "' "
	                                    + "and date(dteBillDate)='" + filterDate + "' ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                        }
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }

	                    try
	                    {
	                    	 sqlBuilder.setLength(0);
	        		    	 sqlBuilder.append("update tblqbillhd "
	                                + " set dblSubTotal="
	                                + "(select sum(c.dblAmount) "
	                                + "from  "
	                                + "(select a.strItemCode,a.strItemName,a.strBillNo,a.dblAmount,a.dblDiscountAmt,a.dblDiscountPer "
	                                + "from tblqbilldtl a where a.strBillNo='" + billNo + "' and date(a.dteBillDate)='" + filterDate + "'   "
	                                + "union all "
	                                + "select b.strItemCode,b.strModifierName,b.strBillNo,b.dblAmount,b.dblDiscAmt,b.dblDiscPer  "
	                                + "from tblqbillmodifierdtl b where b.strBillNo='" + billNo + "' and date(b.dteBillDate)='" + filterDate + "'  ) "
	                                + "c  "
	                                + " group by c.strBillNo) "
	                                + " where strBillNo='" + billNo + "' "
	                                + " and date(dteBillDate)='" + filterDate + "' ");
	                        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }

	                    try
	                    {
	                    	sqlBuilder.setLength(0);
	        		    	sqlBuilder.append("update tblqbillhd "
	                                + " set dblTaxAmt=(select ifnull(sum(a.dblTaxAmount),0) from tblqbilltaxdtl a where strBillNo='" + billNo + "'  and date(dteBillDate)='" + filterDate + "'  group by strBillNo) "
	                                + " where strBillNo='" + billNo + "'"
	                                + "  and date(dteBillDate)='" + filterDate + "'  ");
	                        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }

	                    try
	                    {
	                    	sqlBuilder.setLength(0);
	        		    	sqlBuilder.append("update tblqbillhd set dblDiscountAmt="
	                                + "(select sum(c.dblDiscountAmt) "
	                                + "from  "
	                                + "(select a.strItemCode,a.strItemName,a.strBillNo,a.dblAmount,a.dblDiscountAmt,a.dblDiscountPer "
	                                + "from tblqbilldtl a where a.strBillNo='" + billNo + "' and date(a.dteBillDate)='" + filterDate + "'   "
	                                + "union all "
	                                + "select b.strItemCode,b.strModifierName,b.strBillNo,b.dblAmount,b.dblDiscAmt,b.dblDiscPer  "
	                                + "from tblqbillmodifierdtl b where b.strBillNo='" + billNo + "' and date(b.dteBillDate)='" + filterDate + "'  ) "
	                                + "c  "
	                                + " group by c.strBillNo) "
	                                + " where strBillNo='" + billNo + "' "
	                                + "  and date(dteBillDate)='" + filterDate + "'  ");
	                        objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                    }

	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }

	                    try
	                    {
	                        if (Double.parseDouble(objBills[4].toString()) > 0)
	                        {
	                        	sqlBuilder.setLength(0);
		        		    	sqlBuilder.append("update tblqbillhd "
	                                    + " set dblDiscountPer=(dblDiscountAmt/dblSubTotal)*100 "
	                                    + " where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                                    + " and strPOSCode='" + posCode + "' "
	                                    + " and strBillNo='" + billNo + "' "
	                                    + " and date(dteBillDate)='" + filterDate + "'  ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                        }
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }

	                    sqlBuilder.setLength(0);
        		    	sqlBuilder.append("update tblqbillhd set dblGrandTotal=round((dblSubTotal-dblDiscountAmt)+dblTaxAmt,0) "
	                            + " where strBillNo='" + billNo + "' "
	                            + "  and date(dteBillDate)='" + filterDate + "'  ");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    /**
	                     * delete double entries of same settlement with No
	                     * multisettle
	                     */
	                    sqlBuilder.setLength(0);
			    		sqlBuilder.append( "select count(b.strSettlementCode) "
	                            + "from tblqbillhd a,tblqbillsettlementdtl b "
	                            + "where a.strBillNo=b.strBillNo "
	                            + "and date(a.dteBillDate)=date(b.dteBillDate) "
	                            + "and a.strBillNo='" + billNo + "' "
	                            + "and date(a.dteBillDate)='" + filterDate + "' "
	                            + "and a.strSettelmentMode<> 'MultiSettle' "
	                            + "group by a.strBillNo,b.strSettlementCode,a.strClientCode "
	                            + "having count(b.strSettlementCode)>1 ");
	                    
		                List listDoubleEntriesOfSettlemts=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		                if (listDoubleEntriesOfSettlemts.size()>0)
		                {
	                        objBaseServiceImpl.funExecuteUpdate("DELETE FROM tblqbillsettlementdtl WHERE strBillNo='" + billNo + "' AND date(dteBillDate)='" + filterDate + "' LIMIT 1","sql");
	                    }
	                    

		                sqlBuilder.setLength(0);
			    		sqlBuilder.append( "update tblqbillsettlementdtl c "
	                            + " join (select a.dblGrandTotal as GrandTotal,a.strBillNo as BillNo,a.dteBillDate "
	                            + " from tblqbillhd a,tblqbillsettlementdtl b "
	                            + " where a.strbillno=b.strbillNo  and date(a.dteBillDate)=date(b.dteBillDate) and a.strBillNo='" + billNo + "' and date(a.dteBillDate)='" + filterDate + "' "
	                            + " and (b.dblSettlementAmt-a.dblGrandTotal) not between -0.01 and 0.01 "
	                            + " and a.strSettelmentMode<> 'MultiSettle') d "
	                            + " on c.strbillno=d.BillNo and date(c.dteBillDate)=date(d.dteBillDate) "
	                            + " set c.dblSettlementAmt = d.GrandTotal "
	                            + "where c.strBillNo='" + billNo + "' "
	                            + "and date(c.dteBillDate)='" + filterDate + "'");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    sqlBuilder.setLength(0);
			    		sqlBuilder.append( "update tblqbillsettlementdtl set dblPaidAmt=round(dblPaidAmt,0),dblActualAmt=round(dblActualAmt,0) "
	                            + " where strBillNo='" + billNo + "' "
	                            + "  and date(dteBillDate)='" + filterDate + "'  ");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    String settlementMode = objBills[6].toString();
	                    double grandTotal =Double.parseDouble(objBills[7].toString());

	                    /**
	                     * to update billhd grandTotal to billSettlementTotal
	                     */
	                    sqlBuilder.setLength(0);
			    		sqlBuilder.append( "select dblGrandTotal from tblqbillhd "
	                            + " where strBillNo='" + billNo + "' "
	                            + " and date(dteBillDate)='" + filterDate + "' ");
	                    List listTempBillHd=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		                if (listTempBillHd.size()>0)
		                {
	                        grandTotal = Double.parseDouble(listTempBillHd.get(0).toString());
	                    }

		                sqlBuilder.setLength(0);
			    		sqlBuilder.append( "select a.strSettlementCode,sum(a.dblSettlementAmt) "
	                            + "from tblqbillsettlementdtl a "
	                            + "where a.strBillNo='" + billNo + "' "
	                            + "and date(a.dteBillDate)='" + filterDate + "' ");
	                    List listBillHdGT=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		                if (listBillHdGT.size()>0)
		                {
		                	Object []obj=(Object[])listBillHdGT.get(0);
	                        double billSettleAmt = Double.parseDouble(obj[1].toString());
	                        double billHDnBillSettleAmtDiff = grandTotal - billSettleAmt;

	                        if (billHDnBillSettleAmtDiff > 0)
	                        {
	                        	sqlBuilder.setLength(0);
	    			    		sqlBuilder.append( "update tblqbillsettlementdtl  "
	                                    + "SET dblSettlementAmt = dblSettlementAmt+" + billHDnBillSettleAmtDiff + " "
	                                    + "where strBillNo='" + billNo + "'  "
	                                    + "AND DATE(dteBillDate)='" + filterDate + "'  "
	                                    + "and strSettlementCode='" + settlementCode + "' ");
	                            objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                        }
	                    }

	                    if (settlementMode.equals("MultiSettle"))
	                    {
	                        if (settlementType.equals("Cash"))
	                        {
	                        	sqlBuilder.setLength(0);
	      			    		sqlBuilder.append(  "select dblGrandTotal from tblqbillhd "
	                                    + " where strBillNo='" + billNo + "' "
	                                    + " and date(dteBillDate)='" + filterDate + "' ");
	                            listBillHdGT=new ArrayList();
	                             listBillHdGT=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    		                if (listBillHdGT.size()>0)
	    		                {
	                                grandTotal =Double.parseDouble(listBillHdGT.get(0).toString());
	                            }
	                           

	                            String settleCode = "";
	                            double settlementAmt = 0;
	                        	sqlBuilder.setLength(0);
	      			    		sqlBuilder.append( "select a.dblSettlementAmt,a.strSettlementCode "
	                                    + "from tblqbillsettlementdtl a,tblsettelmenthd b "
	                                    + "where a.strSettlementCode=b.strSettelmentCode "
	                                    + "and date(a.dteBillDate)='" + filterDate + "'  "
	                                    + "and b.strSettelmentType!='Cash' "
	                                    + "and a.strBillNo='" + billNo + "' ");
	                          
	                            List listSettle=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	  	    		            if (listSettle.size()>0)
	  	    		            {
	  	    		            	Object []obj=(Object[])listSettle.get(0);
	  	    		            
	                                settlementAmt += Double.parseDouble(obj[0].toString());
	                                settleCode = "'" + obj[1].toString() + "',";
	                            }
	                           
	                            if (settleCode.contains(","))
	                            {
	                                settleCode = settleCode.substring(0, settleCode.length() - 1);
	                            }
	                            if (settleCode.length() > 0)
	                            {
	                                double settleAmtForCash = grandTotal - settlementAmt;
	                                System.out.println(settleAmtForCash);
	                                System.out.println(grandTotal);
	                                System.out.println(settlementAmt);
	                                sqlBuilder.setLength(0);
		      			    		sqlBuilder.append("update tblqbillsettlementdtl "
	                                        + "set dblSettlementAmt=" + settleAmtForCash
	                                        + " where strSettlementCode not in(" + settleCode + ") "
	                                        + " and strBillNo='" + billNo + "' "
	                                        + " and date(dteBillDate)='" + filterDate + "' ");
	                                System.out.println(sqlBuilder.toString());
	                                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                            }

	                        }

	                        if (settlementType.equals("Complementary"))
	                        {
	                        	sqlBuilder.setLength(0);
	      			    		sqlBuilder.append( "select a.dblSettlementAmt,a.strSettlementCode "
	                                    + "from tblqbillsettlementdtl a,tblsettelmenthd b "
	                                    + "where a.strSettlementCode=b.strSettelmentCode "
	                                    + "and b.strSettelmentType!='Complementary' "
	                                    + "and a.strBillNo='" + billNo + "' "
	                                    + "and date(dteBillDate)='" + filterDate + "' ");
//	                            ResultSet rsSettle = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	                            List listSettle=objBaseServiceImpl.funGetList(sqlBuilder, "sql");
//	        	                while (rsBillModiDtl.next())
//	        	                {
	        	                if (listSettle.size()>0)
	        	                {
	        	                	 for(int i=0;i<listSettle.size();i++)
	        	                	 {
	        	                	 Object[] objSettle = (Object[]) listSettle.get(i);
	        	                	 sqlBuilder.setLength(0);
			      			    	 sqlBuilder.append("delete from tblqbillsettlementdtl "
	                                        + "where strBillNo='" + billNo + "' "
	                                        + "and strSettlementCode='" + objSettle[1].toString() + "' "
	                                        + "and date(dteBillDate)='" + filterDate + "' ");
	                                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
	                                 }
	        	                }
	                        }
	                    }

	                    sqlBuilder.setLength(0);
     			    	sqlBuilder.append("update tblbillseriesbilldtl a\n"
	                            + "join tblqbillhd b\n"
	                            + "on  a.strHdBillNo=b.strBillNo\n"
	                            + "and date(a.dteBillDate)=date(b.dteBillDate)\n"
	                            + "set a.dblGrandTotal=b.dblGrandTotal\n"
	                            + "where a.strHdBillNo=b.strBillNo\n"
	                            + "and date(a.dteBillDate)=date(b.dteBillDate)\n"
	                            + "and a.strHdBillNo='" + billNo + "'\n"
	                            + "and date(a.dteBillDate)='" + filterDate + "' ");
	                    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                    /*
	                     * String multiSettleBill="update tblqbillhd a,
	                     * tblqbillsettlementdtl b " + "set a.dblgrandtotal =
	                     * sum(b.dblSettlementAmt) " + "where
	                     * a.strBillNo=b.strBillNo and
	                     * a.strSettelmentMode='MultiSettle' " + "group by
	                     * b.strBillNo "; System.out.println(multiSettleBill);
	                     * clsGlobalVarClass.dbMysql.execute(multiSettleBill);
	                     */
	                }

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbillhd set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbilldtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbillsettlementdtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbillmodifierdtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbilltaxdtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbillcomplementrydtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbilldiscdtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                sqlBuilder.setLength(0);
 			    	sqlBuilder.append("update tblqbillpromotiondtl set strDataPostFlag='N' where strBillNo='" + billNo + "' and date(dteBillDate)='" + filterDate + "' ");
	                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

	                objUtility.funUpdateBillDtlWithTaxValues(billNo, "QFile", filterDate);
	            }
	        }
	    		 sqlBuilder.setLength(0);
			    	sqlBuilder.append("update tblqbillhd set dblsubtotal=0,dblTaxAmt=0,dblGrandTotal=0,strDataPostFlag='N' "
	                    + " where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and strPOSCode='" + posCode + "' "
	                    + " and strBillNo Not In (select strBillNo from tblqbilldtl) ");
	            System.out.println(sqlBuilder.toString());
	            System.out.println( objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql"));

	            /*
	             * sql="update tblqbillhd " + " set
	             * dblDiscountAmt=(dblSubTotal*dblDiscountPer)/100 " + " where
	             * date(dteBillDate) between '"+fromDate+"' and '"+toDate+"' " + "
	             * and strPOSCode='"+posCode+"' ";
	             * System.out.println(clsGlobalVarClass.dbMysql.execute(sql));
	             */
	            sqlBuilder.setLength(0);
			    sqlBuilder.append("update tblqbilldtl a join "
	                    + " ( select b.dblDiscountAmt as DisAmt,b.dblDiscountPer as DiscPer,b.strBillNo as BillNo ,"
	                    + " b.strPOSCode as POSCode, b.dteBillDate as BillDate from tblqbillhd b "
	                    + " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
	                    + ") c "
	                    + " on a.strbillno=c.BillNo  and date(a.dteBillDate)=date(c.dteBillDate) "
	                    + " set a.dblDiscountAmt=(a.dblAmount*c.DiscPer)/100,strDataPostFlag='N' "
	                    + " where date(c.BillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and c.POSCode='" + posCode + "' ");
	            //System.out.println(sql);
	            //System.out.println(clsGlobalVarClass.dbMysql.execute(sql));

	            objUtility.funCalculateDayEndCashForQFile(fromDate, 1,request);
	            objUtility.funUpdateDayEndFieldsForQFile(fromDate, 1, "Y",request);

	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        return 1;
	    }

}
