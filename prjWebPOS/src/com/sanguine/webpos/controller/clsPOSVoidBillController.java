package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.math.BigDecimal;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.webpos.bean.clsPOSBillDiscountDtl;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillHd;
import com.sanguine.webpos.bean.clsPOSBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSBillTaxDtl;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationBean;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSVoidBillHd;
import com.sanguine.webpos.bean.clsPOSVoidBillModifierDtl;
import com.sanguine.webpos.bean.clsPOSVoidKotBean;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSVoidBillController {

	
	@Autowired 
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	@Autowired
	clsPOSVoidKotController objVoidController;
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	@Autowired
	clsPOSUtilityController objUtility;
	
	 String areaCode="";
	 String operationTypeForTax="";

	
	List<clsPOSBillModifierDtl> arrListBillModifierDtl= new ArrayList<clsPOSBillModifierDtl>();
	List<clsPOSVoidBillModifierDtl> arrListVoidBillModifierDtl= new ArrayList<clsPOSVoidBillModifierDtl>();
	 List<clsPOSBillDtl> arrListKOTWiseBillDtl= new ArrayList<clsPOSBillDtl>() ;
	 List<clsPOSVoidBillDtl> arrListVoidBillDtl= new ArrayList<clsPOSVoidBillDtl>();
	 List<clsPOSBillHd> arrListBillHd= new ArrayList<clsPOSBillHd>();
	 List<clsPOSBillDiscountDtl> arrListBillDiscDtl= new ArrayList<clsPOSBillDiscountDtl>();
	 List<clsPOSVoidBillHd> arrListVoidBillHd= new ArrayList<clsPOSVoidBillHd>();
	 List<clsPOSBillTaxDtl> arrListBillTaxDtl= new ArrayList<clsPOSBillTaxDtl>();
	  List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
	 int intShiftNo = 0;
	@RequestMapping(value = "/frmPOSVoidBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);

	    List listReson=funLoadResonCode();
	    model.put("listReson", listReson);
     	if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidBill_1","command", new clsPOSVoidKotBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSVoidBill","command", new clsPOSVoidKotBean());
		}else {
			return null;
		}
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
		
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/funGetBillList", method = RequestMethod.GET)
	public @ResponseBody LinkedList funFillRGridData(HttpServletRequest req) {

		String strPosCode = req.getSession().getAttribute("loginPOS")
				.toString();

		LinkedList listFillGrid = new LinkedList();
		try {

			String SearchBillNo = "";
			String posDate = req.getSession().getAttribute("gPOSDate").toString();
			
			Map mapObj = funLoadBillGrid(posDate, strPosCode, SearchBillNo);
			
			List list = (List) mapObj.get("jArr");

			for (int i = 0; i < list.size(); i++) {
				Map hmtemp = (Map) list.get(i);
				LinkedList setFillGrid = new LinkedList();
				setFillGrid.add(hmtemp.get("strBillNo").toString());
				setFillGrid.add(hmtemp.get("dteBillDate").toString().split(" ")[1]);
				setFillGrid.add(Double.parseDouble(hmtemp.get("dblGrandTotal").toString()));
				setFillGrid.add(hmtemp.get("strTableName").toString());
				listFillGrid.add(setFillGrid);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return listFillGrid;

	}
		
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping(value = "/fillBillDtlData", method = RequestMethod.GET)
		public @ResponseBody Map funFillBillDtla(HttpServletRequest req){	
			
			LinkedList listFillGrid=new LinkedList();
			String strPosCode=req.getSession().getAttribute("gPOSCode").toString();
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();
	 		Map mapFilltable=new HashMap();
	 		  arrListBillDtl.clear();
	          arrListBillHd.clear();
	          arrListBillModifierDtl.clear();
	          arrListVoidBillHd.clear();
	          arrListVoidBillDtl.clear();
	          arrListVoidBillModifierDtl.clear();
	          arrListBillDiscDtl.clear();
	          arrListKOTWiseBillDtl.clear();
			
			try {
				
				
				
				String billNo=req.getParameter("billNo");
				String posDate=req.getSession().getAttribute("gPOSDate").toString();
				
			    
			    Map mapObj = funSelectBill( billNo,  strClientCode, strPosCode,  posDate) ;
				List list = (List) mapObj.get("jArr");
				
				for(int i=0;i<list.size();i++)
				{
					Map mapObjtemp =(Map) list.get(i);
					LinkedList setFillGrid=new LinkedList();
				
					setFillGrid.add(mapObjtemp.get("strItemName").toString());
					setFillGrid.add( mapObjtemp.get("dblQuantity").toString());
					setFillGrid.add( Double.parseDouble(mapObjtemp.get("dblAmount").toString()));
					setFillGrid.add( mapObjtemp.get("strItemCode").toString());
					setFillGrid.add( mapObjtemp.get("strKOTNo").toString());
					listFillGrid.add(setFillGrid);
					List jArrMod=(List) mapObjtemp.get("ModifierData");
					for(int j=0;j<jArrMod.size();j++)
					{
						Map mapObjMod= (Map)jArrMod.get(j);
						setFillGrid=new LinkedList();
						setFillGrid.add(mapObjMod.get("modifierName").toString());
						setFillGrid.add( mapObjMod.get("dblQuantityMod").toString());
						setFillGrid.add( Double.parseDouble(mapObjMod.get("dblAmountMod").toString()));
						setFillGrid.add( mapObjMod.get("strModifierCode").toString());
						setFillGrid.add( mapObjMod.get("strItemCodeMod").toString());
						listFillGrid.add(setFillGrid);
							
					}
					
				}
				double totalAmount = Double.parseDouble(mapObj.get("grandTotal").toString());
    			double  tax=Double.parseDouble(mapObj.get("totalTaxAmount").toString());
    			double subTotalAmt=Double.parseDouble(mapObj.get("subTotal").toString());
    			String userCreated=mapObj.get("userCreated").toString();
    			mapFilltable.put("listFillGrid", listFillGrid);
    			mapFilltable.put("totalAmount", totalAmount);
    			mapFilltable.put("taxAmt", tax);
    			mapFilltable.put("userCreated", userCreated);
    			mapFilltable.put("subTotalAmt", subTotalAmt);
				 
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return mapFilltable;
			
		}
		@RequestMapping(value = "/voidItem", method = RequestMethod.POST)
		public @ResponseBody String funVoidBill(HttpServletRequest req)
		{

			 String result="";
			 String userCode=req.getSession().getAttribute("gUserCode").toString();
		try {
		
			String posDate= req.getSession().getAttribute("gPOSDate").toString();
			String strPosCode=req.getSession().getAttribute("loginPOS").toString();
			String billNo=req.getParameter("delbillNo").toString();
			String tableNo=req.getParameter("delTableNo").toString();
			String reasonCode=req.getParameter("reasonCode").toString();
			String remarks=req.getParameter("remarks").toString();
			double taxAmt=Double.parseDouble(req.getParameter("taxAmt").toString());
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String[] arrVoidedItemList = req.getParameterValues("voidedItemList");
			
			
			String itemCode="",itemName="",modItemCode="";		
			double delItemQuantity=0,amount=0,remainingItemQuantity=0,remainingAmount=0;
			
			if (arrVoidedItemList.length > 0)
            {
                for (int cnt = 0; cnt < arrVoidedItemList.length; cnt++)
                {
                    String []item= arrVoidedItemList[cnt].split(",");
                    for(String itemData:item)
                    {
                    	  itemCode=itemData.split("#")[0];
                          itemName=itemData.split("#")[1];
                          modItemCode=itemData.split("#")[2];
                          remainingItemQuantity=Double.parseDouble(itemData.split("#")[3]);
                          remainingAmount=Double.parseDouble(itemData.split("#")[4]);
                          delItemQuantity=Double.parseDouble(itemData.split("#")[5]);
		                  amount=Double.parseDouble(itemData.split("#")[6]);
                          Map mapObj = funVoidItem( reasonCode, tableNo, billNo, remarks, userCode,clientCode,  posDate,  taxAmt,
            						 itemCode, delItemQuantity,amount,itemName, modItemCode, strPosCode) ;
            				
            			  result=mapObj.get("true").toString();
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
		@RequestMapping(value = "/fullVoidBillButtonClick", method = RequestMethod.GET)
		@ResponseBody
		public String funFullVoidBill(HttpServletRequest req)
		{
			String result="";
			try {
			   
				String voidedDate=req.getSession().getAttribute("gPOSDate").toString();
				String strPosCode=req.getSession().getAttribute("loginPOS").toString();
				String billNo=req.getParameter("billNo").toString();
				String reasonCode=req.getParameter("reasonCode").toString();
				String remarks=req.getParameter("remarks").toString();
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				String userCode=req.getSession().getAttribute("gUserCode").toString();

				Map mapObj =funVoidBill( voidedDate, billNo, reasonCode, remarks, userCode, strPosCode, clientCode);
				result=mapObj.get("sucessfully").toString();
				}  catch (Exception e)
		        {
			          
		            e.printStackTrace();
		        }
			return result;
				
			
			
		}
		
	// Code For One Item Void

	public Map funVoidItem(String selectedReasonDesc,String delTableName, String billNo, String remark, String userCode,
			String clientCode, String posDate, double taxAmount,String itemCode, double totalBillQty, double amount,String itemName, String modItemCode, String strPosCode) 
		 {
		 Map mapObjReturn = new HashMap();
		try {
			String delTableNo = funLoadTable(strPosCode, delTableName);
			String voidBillDate;
			double selectedVoidQty = totalBillQty, voidedItemQty = 0.0;

			java.util.Date objDate = new java.util.Date();
			String time = (objDate.getHours()) + ":" + (objDate.getMinutes())
					+ ":" + (objDate.getSeconds());
			voidBillDate = posDate + " " + time;

			voidedItemQty = selectedVoidQty;
			int i = 0;
			int reasoncount = 0;
			String[] reason;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select count(strReasonName) from tblreasonmaster where strVoidBill='Y'");

			List listSqlModDtl = objBaseServiceImpl.funGetList(sqlBuilder,
					"sql");

			if (listSqlModDtl.size() > 0) {
				for (int j = 0; j < listSqlModDtl.size(); j++) {
					Object obj = (Object) listSqlModDtl.get(j);

					reasoncount = Integer.parseInt(obj.toString());
				}
			}
			if (reasoncount > 0) {
				String selectedReasonCode = "";
				reason = new String[reasoncount];
				sqlBuilder.setLength(0);
				sqlBuilder.append("select strReasonName from tblreasonmaster where strVoidBill='Y'");

				List listSqlResName = objBaseServiceImpl.funGetList(sqlBuilder,"sql");
				i = 0;
				if (listSqlResName.size() > 0) {
					for (int j = 0; j < listSqlResName.size(); j++) {
						Object obj = (Object) listSqlResName.get(j);

						reason[i] = obj.toString();
						i++;
					}
				}
				
				if (selectedReasonDesc != null) {
					sqlBuilder.setLength(0);
					sqlBuilder
							.append("select strReasonCode from tblreasonmaster where strReasonName='"
									+ selectedReasonDesc
									+ "' "
									+ "and strVoidBill='Y'");

					List listSqlResCode = objBaseServiceImpl.funGetList(
							sqlBuilder, "sql");
					i = 0;
					if (listSqlResCode.size() > 0) {
						for (int j = 0; j < listSqlResCode.size(); j++) {
							Object obj = (Object) listSqlResCode.get(j);

							selectedReasonCode = obj.toString();
						}
					}

					if (itemName.startsWith("-->")) {

						Iterator<clsPOSBillModifierDtl> billModiIt = arrListBillModifierDtl
								.iterator();
						while (billModiIt.hasNext()) {
							clsPOSBillModifierDtl objBillModDtl = billModiIt
									.next();
							if (objBillModDtl.getStrItemCode().equals(itemCode)
									&& objBillModDtl.getStrModifierName()
											.equals(itemName)) {
								clsPOSVoidBillModifierDtl objVoidBillModDtl = new clsPOSVoidBillModifierDtl();
								objVoidBillModDtl.setStrBillNo(billNo);
								objVoidBillModDtl.setStrItemCode(objBillModDtl
										.getStrItemCode());
								objVoidBillModDtl
										.setStrModifierCode(objBillModDtl
												.getStrModifierCode());
								objVoidBillModDtl
										.setStrModifierName(objBillModDtl
												.getStrModifierName());
								objVoidBillModDtl.setDblQuantity(objBillModDtl
										.getDblQuantity());
								objVoidBillModDtl.setDblAmount(objBillModDtl
										.getDblAmount());
								objVoidBillModDtl
										.setStrClientCode(objBillModDtl
												.getStrClientCode());
								objVoidBillModDtl
										.setStrCustomerCode(objBillModDtl
												.getStrCustomerCode());
								objVoidBillModDtl
										.setStrDataPostFlag(objBillModDtl
												.getStrDataPostFlag());
								objVoidBillModDtl.setStrRemarks(remark);
								objVoidBillModDtl
										.setStrReasonCode(selectedReasonCode);
								billModiIt.remove();
								arrListVoidBillModifierDtl
										.add(objVoidBillModDtl);
								arrListBillModifierDtl.remove(objBillModDtl);
							}
						}
					} else {
						sqlBuilder.setLength(0);
						sqlBuilder.append("select a.strKOTNo "
								+ "from tblbilldtl a " + "where a.strBillNo='"
								+ billNo + "' and a.strItemCode='" + itemCode
								+ "' " + "order by a.strKOTNo desc ");

						List listSqlMakeKot = objBaseServiceImpl.funGetList(
								sqlBuilder, "sql");
						i = 0;
						if (listSqlMakeKot.size() > 0) {
							for (int j = 0; j < listSqlMakeKot.size(); j++) {
								Object obj = (Object) listSqlMakeKot.get(j);
								String maxKOT = "";
								
								if (selectedVoidQty <= 0) {
									break;
								}
								maxKOT = obj.toString();
								int cnt = 0;
								Iterator<clsPOSBillDtl> billDtlIt = arrListKOTWiseBillDtl
										.iterator();
								while (billDtlIt.hasNext()) {
									if (selectedVoidQty <= 0) {
										break;
									}
									clsPOSBillDtl objBillDtl = billDtlIt.next();
									if (objBillDtl.getStrItemCode().equals(
											itemCode)
											&& objBillDtl.getStrKOTNo().equals(
													maxKOT)) {
										boolean flgRecordPresent = false;
										int cntVoid = 0;
										for (clsPOSVoidBillDtl objVoidBillDtl : arrListVoidBillDtl) {
											if (objVoidBillDtl.getStrItemCode()
													.equals(objBillDtl
															.getStrItemCode())
													&& objVoidBillDtl
															.getStrKOTNo()
															.equals(maxKOT)) {
												double voidedQty = 1;
												if (objBillDtl.getDblQuantity() < selectedVoidQty) {
													voidedQty = objVoidBillDtl
															.getIntQuantity()
															+ objBillDtl
																	.getDblQuantity();
												} else {
													voidedQty = objVoidBillDtl
															.getIntQuantity()
															+ selectedVoidQty;
												}

												objVoidBillDtl
														.setIntQuantity(voidedQty);
												double voidedAmt = objBillDtl
														.getDblRate()
														* voidedQty;
												objVoidBillDtl
														.setDblAmount(voidedAmt);
												arrListVoidBillDtl.set(cntVoid,
														objVoidBillDtl);
												flgRecordPresent = true;
												break;
											}
											cntVoid++;
										}

										if (!flgRecordPresent) {
											clsPOSVoidBillDtl objVoidBillDtl = new clsPOSVoidBillDtl();
											double voidedQty = 1;
											if (objBillDtl.getDblQuantity() < selectedVoidQty) {
												voidedQty = objVoidBillDtl
														.getIntQuantity()
														+ objBillDtl
																.getDblQuantity();
											} else {
												voidedQty = objVoidBillDtl
														.getIntQuantity()
														+ selectedVoidQty;
											}
											double voidedAmt = objBillDtl
													.getDblRate() * voidedQty;
											
											double taxAmt = objBillDtl
													.getDblTaxAmount()
													/ totalBillQty;
											objVoidBillDtl.setStrBillNo(billNo);
											objVoidBillDtl
													.setStrPosCode(strPosCode);
											objVoidBillDtl
													.setStrItemCode(itemCode);
											objVoidBillDtl
													.setStrItemName(itemName);
											objVoidBillDtl
													.setIntQuantity(voidedQty);
											objVoidBillDtl
													.setDblAmount(voidedAmt);
											objVoidBillDtl.setDblPaidAmt(0);
											objVoidBillDtl
													.setDblSettlementAmt(0);
											objVoidBillDtl
													.setDblTaxAmount(taxAmt
															* voidedQty);
											objVoidBillDtl
													.setDteBillDate(objBillDtl
															.getDteBillDate());
											objVoidBillDtl
													.setDteModifyVoidBill(voidBillDate);
											objVoidBillDtl
													.setStrTransType("VB");
											objVoidBillDtl
													.setIntShiftCode(intShiftNo);
											objVoidBillDtl
													.setStrClientCode(clientCode);
											objVoidBillDtl
													.setStrDataPostFlag("N");
											objVoidBillDtl.setStrKOTNo(maxKOT);
											objVoidBillDtl
													.setStrUserCreated(userCode);
											objVoidBillDtl
													.setStrReasonCode(selectedReasonCode);
											objVoidBillDtl
													.setStrReasonName(selectedReasonDesc);
											objVoidBillDtl
													.setStrRemarks(remark);
											objVoidBillDtl
													.setStrSettlementCode("");
											objVoidBillDtl.setStrWaiterNo("NA");
											objVoidBillDtl.setStrTableNo("NA");
											arrListVoidBillDtl
													.add(objVoidBillDtl);
										}

										double qty = objBillDtl
												.getDblQuantity()
												- selectedVoidQty;
										if (qty < 1) {
											selectedVoidQty = selectedVoidQty
													- objBillDtl
															.getDblQuantity();
											billDtlIt.remove();
										} else {
											double amt = objBillDtl
													.getDblRate() * qty;
											double discAmtForSingleQty = objBillDtl
													.getDblDiscountAmt()
													/ objBillDtl
															.getDblQuantity();
											double discAmt = discAmtForSingleQty
													* qty;
											
											double taxAmt = objBillDtl
													.getDblTaxAmount()
													/ totalBillQty;
											objBillDtl
													.setDblDiscountAmt(discAmt);
											objBillDtl.setDblQuantity(qty);
											objBillDtl.setDblAmount(amt);
											objBillDtl.setDblTaxAmount(taxAmt
													* qty);
											arrListKOTWiseBillDtl.set(cnt,
													objBillDtl);
											selectedVoidQty = 0;
										}

										Iterator<clsPOSBillModifierDtl> billModiIt = arrListBillModifierDtl
												.iterator();
										while (billModiIt.hasNext()) {
											clsPOSBillModifierDtl objBillModDtl = billModiIt
													.next();
											boolean isItemExistsInBillItemDtl = false;
											for (clsPOSBillDtl billDtl : arrListKOTWiseBillDtl) {
												if (billDtl.getStrItemCode()
														.equalsIgnoreCase(
																itemCode)) {
													isItemExistsInBillItemDtl = true;
													break;
												}
											}
											if (!isItemExistsInBillItemDtl) {
												if (objBillModDtl
														.getStrItemCode()
														.equals(itemCode
																+ ""
																+ objBillModDtl
																		.getStrModifierCode())) {
													clsPOSVoidBillModifierDtl objVoidBillModDtl = new clsPOSVoidBillModifierDtl();
													objVoidBillModDtl
															.setStrBillNo(billNo);
													objVoidBillModDtl
															.setStrItemCode(objBillModDtl
																	.getStrItemCode());
													objVoidBillModDtl
															.setStrModifierCode(objBillModDtl
																	.getStrModifierCode());
													objVoidBillModDtl
															.setStrModifierName(objBillModDtl
																	.getStrModifierName());
													objVoidBillModDtl
															.setDblQuantity(objBillModDtl
																	.getDblQuantity());
													objVoidBillModDtl
															.setDblAmount(objBillModDtl
																	.getDblAmount());
													objVoidBillModDtl
															.setStrClientCode(objBillModDtl
																	.getStrClientCode());
													objVoidBillModDtl
															.setStrCustomerCode(objBillModDtl
																	.getStrCustomerCode());
													objVoidBillModDtl
															.setStrDataPostFlag(objBillModDtl
																	.getStrDataPostFlag());
													objVoidBillModDtl
															.setStrRemarks(remark);
													objVoidBillModDtl
															.setStrReasonCode(selectedReasonCode);
													billModiIt.remove();
													arrListVoidBillModifierDtl
															.add(objVoidBillModDtl);
													arrListBillModifierDtl
															.remove(objBillModDtl);
												}
											}
										}
										
										break;
									}
									cnt++;
								}
							}
						}

					}
					Map mapObjRrturn = funFillItemGrid(billNo, strPosCode,posDate, clientCode, arrListKOTWiseBillDtl,arrListBillModifierDtl, arrListBillHd);
					
					double totalDiscAmt = 0;
					double discPer = 0.00;
					double itemSubTotal = 0;

					// re-calculate discount
					if (arrListBillDiscDtl.size() > 0) {
						Iterator<clsPOSBillDiscountDtl> billDiscIt = arrListBillDiscDtl
								.iterator();

						while (billDiscIt.hasNext()) {
							clsPOSBillDiscountDtl objBillDiscDtl = billDiscIt
									.next();
							String discountOnType = objBillDiscDtl
									.getDiscOnType();
							String discountOnValue = objBillDiscDtl
									.getDiscOnValue();
							double discPerce = objBillDiscDtl.getDiscPer();
							double newDiscAmt = 0;
							double newDiscOnAmt = 0;

							if (discountOnType.equalsIgnoreCase("Total")) {
								// bill dtl
								for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl) {
									newDiscAmt += objBillDtl
											.getDblDiscountAmt();
									newDiscOnAmt += objBillDtl.getDblAmount();
									itemSubTotal += objBillDtl.getDblAmount();
								}
								// modifier dtl
								for (clsPOSBillModifierDtl objBillModifierDtl : arrListBillModifierDtl) {
									newDiscAmt += objBillModifierDtl
											.getDblDiscAmt();
									newDiscOnAmt += objBillModifierDtl
											.getDblAmount();
									itemSubTotal += objBillModifierDtl
											.getDblAmount();
								}
							} else if (discountOnType
									.equalsIgnoreCase("ItemWise")) {
								// bill dtl
								for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl) {
									if (objBillDtl.getStrItemName()
											.equalsIgnoreCase(discountOnValue)) {
										newDiscOnAmt += objBillDtl
												.getDblAmount();
										itemSubTotal += objBillDtl
												.getDblAmount();

										// modifier dtl
										for (clsPOSBillModifierDtl objBillModifierDtl : arrListBillModifierDtl) {
											if (objBillDtl.getStrItemCode()
													.equals(objBillModifierDtl
															.getStrItemCode()
															.substring(0, 7))) {
												newDiscOnAmt += objBillModifierDtl
														.getDblAmount();
												itemSubTotal += objBillModifierDtl
														.getDblAmount();
											}
										}
									}
								}
							} else if (discountOnType
									.equalsIgnoreCase("GroupWise")) {
								// bill dtl
								for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl) {
									if (objBillDtl.getGroupName()
											.equalsIgnoreCase(discountOnValue)) {
										newDiscAmt += objBillDtl
												.getDblDiscountAmt();
										newDiscOnAmt += objBillDtl
												.getDblAmount();
										itemSubTotal += objBillDtl
												.getDblAmount();
									} else {
										itemSubTotal += objBillDtl
												.getDblAmount();
									}
								}
								// modifier dtl
								for (clsPOSBillModifierDtl objBillModifierDtl : arrListBillModifierDtl) {
									if (objBillModifierDtl.getGroupName()
											.equalsIgnoreCase(discountOnValue)) {
										newDiscAmt += objBillModifierDtl
												.getDblDiscAmt();
										newDiscOnAmt += objBillModifierDtl
												.getDblAmount();
										itemSubTotal += objBillModifierDtl
												.getDblAmount();
									} else {
										itemSubTotal += objBillModifierDtl
												.getDblAmount();
									}
								}
							} else if (discountOnType
									.equalsIgnoreCase("SubGroupWise")) {
								// bill dtl
								for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl) {
									if (objBillDtl.getGroupName()
											.equalsIgnoreCase(discountOnValue)) {
										newDiscAmt += objBillDtl
												.getDblDiscountAmt();
										newDiscOnAmt += objBillDtl
												.getDblAmount();
										itemSubTotal += objBillDtl
												.getDblAmount();
									} else {
										itemSubTotal += objBillDtl
												.getDblAmount();
									}
								}
								// modifier dtl
								for (clsPOSBillModifierDtl objBillModifierDtl : arrListBillModifierDtl) {
									if (objBillModifierDtl.getSubGrouName()
											.equalsIgnoreCase(discountOnValue)) {
										newDiscAmt += objBillModifierDtl
												.getDblDiscAmt();
										newDiscOnAmt += objBillModifierDtl
												.getDblAmount();
										itemSubTotal += objBillModifierDtl
												.getDblAmount();
									} else {
										itemSubTotal += objBillModifierDtl
												.getDblAmount();
									}
								}
							}

							// update bill discounr
							if (newDiscOnAmt > 0) {
								newDiscAmt = (discPerce / 100) * newDiscOnAmt;
								objBillDiscDtl.setDiscAmt(newDiscAmt);
								objBillDiscDtl.setDiscOnAmt(newDiscOnAmt);
								totalDiscAmt += newDiscAmt;
							} else {
								billDiscIt.remove();
							}
						}
					}
					itemSubTotal = 0.00;

					// bill dtl
					for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl) {
						itemSubTotal += objBillDtl.getDblAmount();
					}

					// modifier dtl
					for (clsPOSBillModifierDtl objBillModifierDtl : arrListBillModifierDtl) {
						itemSubTotal += objBillModifierDtl.getDblAmount();
					}

					if (itemSubTotal == 0.00) {
						discPer = 0.00;
					} else {
						discPer = (totalDiscAmt / itemSubTotal) * 100;
					}
					clsPOSBillHd objBillHd = arrListBillHd.get(0);
					double totalVoidedAmt = objBillHd.getDblGrandTotal()
							- itemSubTotal;

					clsPOSVoidBillHd objVoidBillHd = new clsPOSVoidBillHd();
					if (arrListVoidBillHd.size() > 0) {
						objVoidBillHd = arrListVoidBillHd.get(0);
						objVoidBillHd.setDblModifiedAmount(objVoidBillHd
								.getDblModifiedAmount() + totalVoidedAmt);
						arrListVoidBillHd.set(0, objVoidBillHd);
					} else {
						objVoidBillHd.setStrBillNo(billNo);
						objVoidBillHd.setStrPosCode(objBillHd.getStrPOSCode());
						objVoidBillHd.setStrReasonCode(selectedReasonCode);
						objVoidBillHd.setStrReasonName(selectedReasonDesc);
						objVoidBillHd.setDblActualAmount(objBillHd
								.getDblGrandTotal());
						objVoidBillHd.setDblModifiedAmount(totalVoidedAmt);
						objVoidBillHd
								.setDteBillDate(objBillHd.getDteBillDate());
						objVoidBillHd.setStrTransType("VB");
						objVoidBillHd.setDteModifyVoidBill(voidBillDate);
						objVoidBillHd.setStrTableNo(objBillHd.getStrTableNo());
						objVoidBillHd
								.setStrWaiterNo(objBillHd.getStrWaiterNo());
						objVoidBillHd.setIntShiftCode(objBillHd
								.getIntShiftCode());
						objVoidBillHd.setStrUserCreated(userCode);
						objVoidBillHd.setStrUserEdited(userCode);
						objVoidBillHd.setStrClientCode(objBillHd
								.getStrClientCode());
						objVoidBillHd.setStrDataPostFlag(objBillHd
								.getStrDataPostFlag());
						objVoidBillHd.setStrRemark(remark);
						arrListVoidBillHd.add(objVoidBillHd);
					}

					objBillHd.setDblSubTotal(itemSubTotal);
					objBillHd.setDblDiscountAmt(totalDiscAmt);
					objBillHd.setDblDiscountPer(discPer);
					double grandTotal = (itemSubTotal + objBillHd
							.getDblTaxAmt()) - totalDiscAmt;
					objBillHd.setDblGrandTotal(grandTotal);
					arrListBillHd.set(0, objBillHd);
					

					if (arrListKOTWiseBillDtl.size() > 0) {
						funInsertVoidData(billNo, userCode);
						funUpdateBillData(billNo, userCode, voidedItemQty,
								itemCode, strPosCode, clientCode, voidBillDate,
								posDate);
						
					} else {
						funInsertVoidData(billNo, userCode);
						StringBuilder updateQuery = new StringBuilder("update tbltablemaster set strStatus='Normal',intPaxNo=0 "
								+ "where strTableNo='" + delTableNo + "'");
						objBaseServiceImpl.funExecuteUpdate(updateQuery.toString(), "sql");

						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbilldtl where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbillhd where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbillmodifierdtl where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbilltaxdtl where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbilldiscdtl where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblhomedelivery where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
						sqlBuilder.setLength(0);
						sqlBuilder.append("Delete from tblbillsettlementdtl where strBillNo='"+ billNo + "'");
						objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
					}
					
				}
			} else {
				
			}
			mapObjReturn.put("true", "true");
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			return mapObjReturn;
		}
	}
		   
		   
		   
		   
			public String funLoadTable(String strPosCode,String  tableName)
			{
				String tableNo="";
			
				try{
					StringBuilder sqlBuilder=new StringBuilder();
					sqlBuilder.setLength(0);
					sqlBuilder.append( "select b.strTableNo,a.strTableName "
		                + "from tbltablemaster a,tblitemrtemp b "
		                + "where a.strTableNo=b.strTableNo "
		                + "and  (a.strPOSCode='" + strPosCode + "' OR a.strPOSCode='All') and strNCKotYN='N' and a.strTableName='"+tableName+"' "
		                + "group by b.strTableNo "
		                + "order by a.strTableName ");
				
				
				  
			  	    List listSql1= objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
			  	  if(listSql1.size()>0)
			  	  {
			  	    Object[] obj1 = (Object[]) listSql1.get(0);
			  	    tableNo=obj1[0].toString();
			  	   }
			  
				}catch(Exception e)
				{
					
				}
				return tableNo;
			}
		   
		   
		    private Map funFillItemGrid(String billNo,String gPOSCode,String dtPOSDate,String clientCode,List<clsPOSBillDtl> arrListBillDtl,
    	    		 List<clsPOSBillModifierDtl> arrListBillModifierDtl,
    	    		List<clsPOSBillHd> arrListBillHd) throws Exception
    	    {
    	     
    	    	 Map hmReturn=new HashMap();
    	    	 List list=new ArrayList();
    	    	
    	        double subTotalForTax = 0;
    	        double totalDiscAmt = 0.00;
    	        List<clsPOSItemDetailFrTaxBean> arrListItemDtls = new ArrayList<clsPOSItemDetailFrTaxBean>();
   	        List<clsPOSTaxCalculationBean> arrListTaxCal = new ArrayList<clsPOSTaxCalculationBean>()   ;
    	        List<clsPOSBillTaxDtl> arrListBillTaxDtl = new ArrayList<clsPOSBillTaxDtl>();
    	        for (clsPOSBillDtl objBillItemDtl : arrListBillDtl)
    	        {
    	        	Map hmObj=new HashMap();
    	        	clsPOSItemDetailFrTaxBean objItemDtl = new clsPOSItemDetailFrTaxBean();
    	           hmObj.put("strItemName",objBillItemDtl.getStrItemName());
                  hmObj.put("dblQuantity", objBillItemDtl.getDblQuantity());
                  hmObj.put("dblAmount",objBillItemDtl.getDblAmount());
                  hmObj.put("strItemCode",objBillItemDtl.getStrItemCode());
                  hmObj.put("strKOTNo",objBillItemDtl.getStrKOTNo());
    	            objItemDtl.setItemCode(objBillItemDtl.getStrItemCode());
    	            objItemDtl.setItemName(objBillItemDtl.getStrItemName());
    	            objItemDtl.setAmount(objBillItemDtl.getDblAmount());
    	            objItemDtl.setDiscAmt(objBillItemDtl.getDblDiscountAmt());
    	            arrListItemDtls.add(objItemDtl);
    	            subTotalForTax += objBillItemDtl.getDblAmount();
    	            totalDiscAmt += objBillItemDtl.getDblDiscountAmt();
    	           List listMod=new ArrayList();   
    	            for (clsPOSBillModifierDtl objBillModDtl : arrListBillModifierDtl)
    	            {
    	                if ((objBillItemDtl.getStrItemCode() + "" + objBillModDtl.getStrModifierCode()).equals(objBillModDtl.getStrItemCode()))
    	                {
    	                    subTotalForTax += objBillModDtl.getDblAmount();
    	                    totalDiscAmt += objBillModDtl.getDblDiscAmt();
                           Map objMapMod=new HashMap();
                           
                           objMapMod.put("modifierName",objBillModDtl.getStrModifierName());
                           objMapMod.put("dblQuantityMod", objBillModDtl.getDblQuantity());
                           objMapMod.put("dblAmountMod",objBillModDtl.getDblAmount());
                           objMapMod.put("strModifierCode",objBillModDtl.getStrModifierCode());
                           objMapMod.put("strItemCodeMod",objBillModDtl.getStrItemCode());
                           objMapMod.put("strKOTNoMod",objBillItemDtl.getStrKOTNo());
                           listMod.add(objMapMod);
    	                    //add modifier items
    	                    clsPOSItemDetailFrTaxBean objModiItemDtl = new clsPOSItemDetailFrTaxBean();
    	                    objModiItemDtl.setItemCode(objBillModDtl.getStrItemCode());
    	                    objModiItemDtl.setItemName(objBillModDtl.getStrModifierName());
    	                    objModiItemDtl.setAmount(objBillModDtl.getDblAmount());
    	                    objModiItemDtl.setDiscAmt(objBillModDtl.getDblDiscAmt());
    	                    arrListItemDtls.add(objModiItemDtl);
    	                
    	                }
    	            }
    	           hmObj.put("ModifierData",listMod);
    	           list.add(hmObj);
    	        }
    	        double subTotal = 0;
    	        double grandTotal = 0;
    	        double discountPer = 0;
    	        String userCreated = "";

    	        arrListTaxCal.clear();
    	       
    	      arrListTaxCal = objUtility.funCalculateTax(arrListItemDtls,gPOSCode, dtPOSDate, areaCode, operationTypeForTax, subTotalForTax, totalDiscAmt, "", "S01");
    	        arrListBillTaxDtl.clear();
    	        double totalTaxAmount = 0;
    	        for (int cnt = 0; cnt < arrListTaxCal.size(); cnt++)
    	        {
    	        	clsPOSTaxCalculationBean objTaxDtl = arrListTaxCal.get(cnt);
    	            if(cnt!=(arrListTaxCal.size() - 1))
    	            {	
    	        	totalTaxAmount += objTaxDtl.getTaxAmount();
    	            }
    	        	clsPOSBillTaxDtl objBillTaxDtl = new clsPOSBillTaxDtl();
    	            objBillTaxDtl.setStrBillNo(billNo);
    	            objBillTaxDtl.setStrTaxCode(objTaxDtl.getTaxCode());
    	            objBillTaxDtl.setDblTaxableAmount(objTaxDtl.getTaxableAmount());
    	            objBillTaxDtl.setDblTaxAmount(objTaxDtl.getTaxAmount());
    	            objBillTaxDtl.setStrClientCode(clientCode);
    	            objBillTaxDtl.setStrDataPostFlag("N");
    	            arrListBillTaxDtl.add(objBillTaxDtl);
    	        }

    	        clsPOSBillHd objBillHd = arrListBillHd.get(0);
    	        subTotal = objBillHd.getDblSubTotal();
    	        grandTotal = objBillHd.getDblGrandTotal();
    	        userCreated = objBillHd.getStrUserCreated();
    	        discountPer = objBillHd.getDblDiscountPer();

    	        grandTotal = (subTotalForTax - objBillHd.getDblDiscountAmt()) + totalTaxAmount;
    	        objBillHd.setDblGrandTotal(subTotalForTax);
    	        objBillHd.setDblTaxAmt(totalTaxAmount);
    	        objBillHd.setDblGrandTotal(grandTotal);
    	        arrListBillHd.set(0, objBillHd);
    	        hmReturn.put("jArr", list);
    	        
    	        hmReturn.put("userCreated", userCreated);
    	        hmReturn.put("subTotal", subTotal);
    	        hmReturn.put("totalTaxAmount", totalTaxAmount);
    	        hmReturn.put("grandTotal", grandTotal);

    	        
    	        
    	        return hmReturn;
    	    }
		   
			  public void funInsertVoidData(String billNo,String userCode) throws Exception
			    {
				  
				  StringBuilder  sqlBuilder=new StringBuilder();
				  sqlBuilder.append( "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='Void Bill'" );
			       
				  StringBuilder sqlSBuilder = new StringBuilder("delete from tblvoidbillhd where strBillNo='" + billNo + "' and strTransType='VB'");
			        objBaseServiceImpl.funExecuteUpdate(sqlSBuilder.toString(), "sql"); 
	                
			        clsPOSVoidBillHd objVoidBillHd = arrListVoidBillHd.get(0);
			        StringBuilder strBuilder = new StringBuilder("insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
			                + "dblActualAmount,dblModifiedAmount,dteBillDate,"
			                + "strTransType,dteModifyVoidBill,strTableNo,strWaiterNo,intShiftCode,"
			                + "strUserCreated,strUserEdited,strClientCode,strRemark) values "
			                + "('" + objVoidBillHd.getStrPosCode() + "','" + objVoidBillHd.getStrReasonCode() + "'"
			                + ",'" + objVoidBillHd.getStrReasonName() + "','" + objVoidBillHd.getStrBillNo() + "'"
			                + ",'" + objVoidBillHd.getDblActualAmount() + "'," + objVoidBillHd.getDblModifiedAmount() + ""
			                + ",'" + objVoidBillHd.getDteBillDate() + "','" + objVoidBillHd.getStrTransType() + "'"
			                + ",'" + objVoidBillHd.getDteModifyVoidBill() + "','" + objVoidBillHd.getStrTableNo() + "'"
			                + ",'" + objVoidBillHd.getStrWaiterNo() + "','" + objVoidBillHd.getIntShiftCode() + "'"
			                + ",'" + objVoidBillHd.getStrUserCreated() + "','" + objVoidBillHd.getStrUserEdited() + "'"
			                + ",'" + objVoidBillHd.getStrClientCode() + "','" + objVoidBillHd.getStrRemark() + "')");
			        //System.out.println(sql);
	                 
		       	       List listSql= objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		       	     
		       	       if(listSql.size()>0)
		       	       {
		         	 
		        	     Object obj = (Object) listSql.get(0);
			     
			            if (Boolean.parseBoolean(obj.toString()))
			            {
			            	 objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql"); 
			            }
		       	       }
			       	   else
			    	   {
			       		objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
			       	   }

		       	    strBuilder.setLength(0);
		       	    strBuilder.append("delete from tblvoidbilldtl where strBillNo='" + billNo + "'");
			        objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql"); 
			        for (clsPOSVoidBillDtl objVoidBillDtl : arrListVoidBillDtl)
			        {
			        	strBuilder.setLength(0);
			       	    strBuilder.append("insert into tblvoidbilldtl(strPosCode,strReasonCode,strReasonName,strItemCode"
			                    + ",strItemName,strBillNo,intQuantity,dblAmount,dblTaxAmount,dteBillDate,"
			                    + "strTransType,dteModifyVoidBill,intShiftCode,strUserCreated,strClientCode"
			                    + ",strKOTNo,strRemarks) "
			                    + "values('" + objVoidBillDtl.getStrPosCode() + "','" + objVoidBillDtl.getStrReasonCode() + "'"
			                    + ",'" + objVoidBillDtl.getStrReasonName() + "','" + objVoidBillDtl.getStrItemCode() + "'"
			                    + ",'" + objVoidBillDtl.getStrItemName() + "','" + objVoidBillDtl.getStrBillNo() + "'"
			                    + ",'" + objVoidBillDtl.getIntQuantity() + "','" + objVoidBillDtl.getDblAmount() + "'"
			                    + ",'" + objVoidBillDtl.getDblTaxAmount() + "','" + objVoidBillDtl.getDteBillDate() + "'"
			                    + ",'" + objVoidBillDtl.getStrTransType() + "'" + ",'" + objVoidBillDtl.getDteModifyVoidBill() + "'"
			                    + "," + objVoidBillDtl.getIntShiftCode() + ",'" + objVoidBillDtl.getStrUserCreated() + "'"
			                    + ",'" + objVoidBillDtl.getStrClientCode() + "','" + objVoidBillDtl.getStrKOTNo() + "'"
			                    + ",'" + objVoidBillDtl.getStrRemarks() + "')");
			            System.out.println(strBuilder.toString());
			           
			            if(listSql.size()>0)
			       	       {
			            	Object obj = (Object) listSql.get(0);
			                if (Boolean.parseBoolean(obj.toString()))
			                {
			                
			                	 objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql"); 
			                }
			            }
			            else
				    	   {
				       		objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
				       	   }
			        }

			        strBuilder.setLength(0);
		       	    strBuilder.append( "delete from tblvoidmodifierdtl where strBillNo='" + billNo + "'");
			        objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql"); 
			        for (clsPOSVoidBillModifierDtl objVoidBillModDtl : arrListVoidBillModifierDtl)
			        {
			        	strBuilder.setLength(0);
			       	    strBuilder.append("insert into tblvoidmodifierdtl(strBillNo,strItemCode,strModifierCode,"
			                    + "strModifierName,dblQuantity,dblAmount,strClientCode,strCustomerCode"
			                    + ",strDataPostFlag,strRemarks,strReasonCode) values "
			                    + "('" + objVoidBillModDtl.getStrBillNo() + "','" + objVoidBillModDtl.getStrItemCode() + "'"
			                    + ",'" + objVoidBillModDtl.getStrModifierCode() + "','" + objVoidBillModDtl.getStrModifierName() + "'"
			                    + ",'" + objVoidBillModDtl.getDblQuantity() + "','" + objVoidBillModDtl.getDblAmount() + "'"
			                    + ",'" + objVoidBillModDtl.getStrClientCode() + "','" + objVoidBillModDtl.getStrCustomerCode() + "'"
			                    + ",'" + objVoidBillModDtl.getStrDataPostFlag() + "','" + objVoidBillModDtl.getStrRemarks() + "'"
			                    + ",'" + objVoidBillModDtl.getStrReasonCode() + "')");
			          
			            if(listSql.size()>0)
			       	       {
			            	Object obj = (Object) listSql.get(0);
			                if (Boolean.parseBoolean(obj.toString()))
			                {
			                	 objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql"); 
			                }
			            }
			            else
				    	   {
				       		objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
				       	   }
			        }
			  
			  
			    }
			  
			  
			  
			  public void funUpdateBillData(String billNo,String userCode,double voidedItemQty,String itemCodeForVoid,
					  String strPOSCode,String strClientCode,String voidBillDate,String posDate) throws Exception
			    {
			        DecimalFormat objDecFormat = new DecimalFormat("####0.00");

			        Date dtCurrent = new Date(); 
				 	String currentTime = dtCurrent.getHours() + ":" + dtCurrent.getMinutes() + ":" + dtCurrent.getSeconds();
				 	String POSDateforRTransaction=posDate;//+" "+currentTime;
			        String sqlDelete = "delete from tblbilldtl where strBillNo='" + billNo + "'";
			        objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql"); 
			        String sqlInsertBillDtl = "insert into tblbilldtl "
			                + "(strItemCode,strItemName,strBillNo,strAdvBookingNo,dblRate"
			                + ",dblQuantity,dblAmount,dblTaxAmount,dteBillDate,strKOTNo"
			                + ",strClientCode,strCustomerCode,tmeOrderProcessing,strDataPostFlag"
			                + ",strMMSDataPostFlag,strManualKOTNo,tdhYN,strPromoCode,strCounterCode"
			                + ",strWaiterNo,dblDiscountAmt,dblDiscountPer,dtBillDate,tmeOrderPickup) "
			                + "values ";
			        for (clsPOSBillDtl objBillDtl : arrListKOTWiseBillDtl)
			        {
			            double amount = objBillDtl.getDblAmount();
			        
			            sqlInsertBillDtl += "('" + objBillDtl.getStrItemCode() + "','" + objBillDtl.getStrItemName() + "'"
			                    + ",'" + objBillDtl.getStrBillNo() + "','" + objBillDtl.getStrAdvBookingNo() + "'," + objBillDtl.getDblRate() + ""
			                    + ",'" + objBillDtl.getDblQuantity() + "','" + objBillDtl.getDblAmount() + "'"
			                    + "," + objBillDtl.getDblTaxAmount() + ",'" + objBillDtl.getDteBillDate() + "'"
			                    + ",'" + objBillDtl.getStrKOTNo() + "','" + objBillDtl.getStrClientCode() + "'"
			                    + ",'" + objBillDtl.getStrCustomerCode() + "','" + objBillDtl.getTmeOrderProcessing() + "'"
			                    + ",'" + objBillDtl.getStrDataPostFlag() + "','" + objBillDtl.getStrMMSDataPostFlag() + "'"
			                    + ",'" + objBillDtl.getStrManualKOTNo() + "','" + objBillDtl.getTdhYN() + "'"
			                    + ",'" + objBillDtl.getStrPromoCode() + "','" + objBillDtl.getStrCounterCode() + "'"
			                    + ",'" + objBillDtl.getStrWaiterNo() + "','" + objBillDtl.getDblDiscountAmt() + "'"
			                    + ",'" + objBillDtl.getDblDiscountPer() + "','" + POSDateforRTransaction + "','" + objBillDtl.getStrOrderPickupTime() + "'),";

			        }
			        if (arrListKOTWiseBillDtl.size() > 0)
			        {
			            StringBuilder sb = new StringBuilder(sqlInsertBillDtl);
			            int index = sb.lastIndexOf(",");
			            sqlInsertBillDtl = sb.delete(index, sb.length()).toString();
			            System.out.println(sqlInsertBillDtl);
			            objBaseServiceImpl.funExecuteUpdate(sqlInsertBillDtl, "sql"); 
			        }
			        StringBuilder  sqlBuilder=new StringBuilder();
			        sqlBuilder.append( "select strItemCode,dblQuantity,strPromotionCode "
			                + " from tblbillpromotiondtl "
			                + " where strBillNo='" + billNo.trim()+ "' and strItemCode='" + itemCodeForVoid + "' " );
	                  
	 	       	       List listSqlProm= objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
	 	       	     
	 	       	       if(listSqlProm.size()>0)
	 	       	       {
	 	        	     Object[] obj = (Object[]) listSqlProm.get(0);
			            if (voidedItemQty < (new BigDecimal(obj[1].toString())).doubleValue())
			            {
			                double qty = voidedItemQty - (new BigDecimal(obj[1].toString())).doubleValue();
			                sqlBuilder.setLength(0);
			                sqlBuilder.append("update tblbillpromotiondtl set dblQuantity='" + qty + "' "
			                        + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "'"
			                        + " and strPromotionCode='" + obj[2].toString()+ "' ");
			                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql"); 
			            }
			            else
			            {
			            	 sqlBuilder.setLength(0);
				             sqlBuilder.append("delete from tblbillpromotiondtl "
			                        + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "'"
			                        + " and strPromotionCode='" + obj[2].toString() + "' ");
			                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql"); 
			            }
			        }

	 	       	   sqlBuilder.setLength(0);
	 	       	sqlBuilder.append( "select strItemCode from tblbilldtl "
			                + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "' ");
			       
			       
		       	       List listSqlbill= objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		       	     
		       	       if(listSqlbill.size()>0)
		       	       {
		        	     sqlDelete = "delete from tblbillpromotiondtl "
			                    + " where strBillNo='" + billNo + "' and strItemCode='" + itemCodeForVoid + "' ";
			            objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql"); 
			        }
			        

			          
			        arrListBillDtl.clear();
			        sqlBuilder.setLength(0);
		 	       	sqlBuilder.append( "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
			                + ",sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,a.strCustomerCode "
			                + ",concat(a.tmeOrderProcessing,''),a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
			                + ",a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
			                + ",c.strSubGroupName,c.strGroupCode,d.strGroupName "
			                + "from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
			                + "where a.strBillNo='" + billNo.trim() + "' and a.strItemCode=b.strItemCode "
			                + "and b.strSubGroupCode=c.strSubGroupCode and c.strGroupCode=d.strGroupCode "
			                + "group by a.strItemCode,a.strItemName,a.strBillNo;" );
			      
		       	    List listSql= objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		       	       if(listSql.size()>0)
		       	       {
		       	    	   for(int j=0;j<listSql.size();j++ )
		       	    	   {
		        	     Object[] obj = (Object[]) listSql.get(0);
		
		
			            clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
			            objBillDtl.setStrItemCode(obj[0].toString());
			            objBillDtl.setStrItemName(obj[1].toString());
			            objBillDtl.setStrBillNo(obj[2].toString());
			            objBillDtl.setStrAdvBookingNo(obj[3].toString());
			            objBillDtl.setDblRate(Double.parseDouble(obj[4].toString()));
			            objBillDtl.setDblQuantity(Double.parseDouble(obj[5].toString()));
			            objBillDtl.setDblAmount(Double.parseDouble(obj[6].toString()));
			            objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7].toString()));
			            objBillDtl.setDteBillDate(obj[8].toString());
			            objBillDtl.setStrKOTNo(obj[9].toString());
			            objBillDtl.setStrClientCode(obj[10].toString());
			            objBillDtl.setStrCustomerCode(obj[11].toString());
			            objBillDtl.setTmeOrderProcessing(obj[12].toString());
			            objBillDtl.setStrDataPostFlag(obj[13].toString());
			            objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
			            objBillDtl.setStrManualKOTNo(obj[15].toString());
			            objBillDtl.setTdhYN(obj[16].toString());
			            objBillDtl.setStrPromoCode(obj[17].toString());
			            objBillDtl.setStrCounterCode(obj[18].toString());
			            objBillDtl.setStrWaiterNo(obj[19].toString());
			            objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20].toString()));
			            objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21].toString()));
			            objBillDtl.setSubGrouName(obj[23].toString());
			            objBillDtl.setGroupName(obj[25].toString());
			            arrListBillDtl.add(objBillDtl);
			        }
		       	    	   }
			        

			        sqlDelete = "delete from tblbillhd where strBillNo='" + billNo.trim() + "' "
			                + "and strPOSCode='" + strPOSCode + "'";
			        objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql"); 
			        clsPOSBillHd objBillHd = arrListBillHd.get(0);
			        objBillHd.setDblSubTotal(objBillHd.getDblSubTotal());
			        objBillHd.setDblGrandTotal(objBillHd.getDblGrandTotal());
			
			        arrListBillHd.set(0, objBillHd);
			        StringBuilder sqlBuilderInsert = new StringBuilder("insert into tblbillhd(strBillNo,strAdvBookingNo,dteBillDate,strPOSCode,strSettelmentMode,"
			                + "dblDiscountAmt,dblDiscountPer,dblTaxAmt,dblSubTotal,dblGrandTotal,strTakeAway,strOperationType"
			                + ",strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode"
			                + ",strTableNo,strWaiterNo,strCustomerCode,strManualBillNo,intShiftCode"
			                + ",intPaxNo,strDataPostFlag,strReasonCode,strRemarks,dblTipAmount,dteSettleDate"
			                + ",strCounterCode,dblDeliveryCharges,strAreaCode,strDiscountRemark,strTakeAwayRemarks,strDiscountOn,dblRoundOff,dtBillDate "
			                + ",intOrderNo ) "
			                + "values('" + objBillHd.getStrBillNo() + "','" + objBillHd.getStrAdvBookingNo() + "'"
			                + ",'" + objBillHd.getDteBillDate() + "','" + objBillHd.getStrPOSCode() + "'"
			                + ",'" + objBillHd.getStrSettelmentMode() + "','" + objDecFormat.format(objBillHd.getDblDiscountAmt()) + "'"
			                + ",'" + objDecFormat.format(objBillHd.getDblDiscountPer()) + "','" + objBillHd.getDblTaxAmt() + "'"
			                + ",'" + objBillHd.getDblSubTotal() + "','" + Math.rint(objBillHd.getDblGrandTotal()) + "'"
			                + ",'" + objBillHd.getStrTakeAway() + "','" + objBillHd.getStrOperationType() + "'"
			                + ",'" + objBillHd.getStrUserCreated() + "','" + objBillHd.getStrUserEdited() + "'"
			                + ",'" + objBillHd.getDteDateCreated() + "','" + objBillHd.getDteDateEdited() + "'"
			                + ",'" + objBillHd.getStrClientCode() + "','" + objBillHd.getStrTableNo() + "'"
			                + ",'" + objBillHd.getStrWaiterNo() + "','" + objBillHd.getStrCustomerCode() + "'"
			                + ",'" + objBillHd.getStrManualBillNo() + "'," + objBillHd.getIntShiftCode() + ""
			                + "," + objBillHd.getIntPaxNo() + ",'" + objBillHd.getStrDataPostFlag() + "','" + objBillHd.getStrReasonCode() + "'"
			                + ",'" + objBillHd.getStrRemarks() + "'," + objBillHd.getDblTipAmount() + ",'" + objBillHd.getDteSettleDate() + "'"
			                + ",'" + objBillHd.getStrCounterCode() + "'," + objBillHd.getDblDeliveryCharges() + ""
			                + ", '" + objBillHd.getStrAreaCode() + "','" + objBillHd.getStrDiscountRemark() + "'"
			                + ",'" + objBillHd.getStrTakeAwayRemarks() + "','" + objBillHd.getStrDiscountOn()+ "','" + objBillHd.getDblGrandTotalRoundOffBy() + "','" + POSDateforRTransaction + "'"
			                + ",'" + objBillHd.getIntLastOrderNo() + "')");
			        objBaseServiceImpl.funExecuteUpdate(sqlBuilderInsert.toString(), "sql"); 
		       

			        //update billseriesbilldtl grand total
			        
		 	       	sqlBuilder.setLength(0);
		 	       	sqlBuilder.append("update tblbillseriesbilldtl set dblGrandTotal='" + objBillHd.getDblGrandTotal() + "' where strHdBillNo='" + billNo + "' "); 
		 	       	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			        sqlDelete = "delete from tblbillmodifierdtl where strBillNo='" + billNo + "'";
			    	objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql");
			        String sqlInsertBillModDtl = "insert into  tblbillmodifierdtl "
			                + "(strBillNo,strItemCode,strModifierCode,strModifierName,dblRate"
			                + ",dblQuantity,dblAmount,strClientCode,strCustomerCode"
			                + ",strDataPostFlag,strMMSDataPostFlag,dblDiscPer,dblDiscAmt )"
			                + " values ";
			        for (clsPOSBillModifierDtl objBillModDtl : arrListBillModifierDtl)
			        {
			            sqlInsertBillModDtl += "('" + objBillModDtl.getStrBillNo() + "','" + objBillModDtl.getStrItemCode() + "'"
			                    + ",'" + objBillModDtl.getStrModifierCode() + "','" + objBillModDtl.getStrModifierName() + "'"
			                    + "," + objBillModDtl.getDblRate() + "," + objBillModDtl.getDblQuantity() + "," + objBillModDtl.getDblAmount() + ""
			                    + ",'" + objBillModDtl.getStrClientCode() + "','" + objBillModDtl.getStrCustomerCode() + "'"
			                    + ",'" + objBillModDtl.getStrDataPostFlag() + "','" + objBillModDtl.getStrMMSDataPostFlag() + "','" + objBillModDtl.getDblDiscPer() + "','" + objBillModDtl.getDblDiscAmt() + "'),";
			        }

			        if (arrListBillModifierDtl.size() > 0)
			        {
			            StringBuilder sb = new StringBuilder(sqlInsertBillModDtl);
			            int index = sb.lastIndexOf(",");
			            sqlInsertBillModDtl = sb.delete(index, sb.length()).toString();
			            objBaseServiceImpl.funExecuteUpdate(sqlInsertBillModDtl.toString(), "sql");
			        }

			        sqlDelete = "delete from tblbilltaxdtl where strBillNo='" + billNo.trim() + "'";
			        objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql");
			        for (clsPOSBillTaxDtl objBillTaxDtl : arrListBillTaxDtl)
			        {
			            StringBuilder sqlInsertTaxDtl = new StringBuilder("insert into tblbilltaxdtl "
			                    + "(strBillNo,strTaxCode,dblTaxableAmount,dblTaxAmount,strClientCode) "
			                    + "values('" + objBillTaxDtl.getStrBillNo() + "','" + objBillTaxDtl.getStrTaxCode() + "'"
			                    + "," + objBillTaxDtl.getDblTaxableAmount() + "," + objBillTaxDtl.getDblTaxAmount() + ""
			                    + ",'" + strClientCode+ "')");
			            objBaseServiceImpl.funExecuteUpdate(sqlInsertTaxDtl.toString(), "sql");
			 	       
			        }

			        //delete all discount
			        objBaseServiceImpl.funExecuteUpdate("delete from tblbilldiscdtl where strBillNo='" + billNo + "'  ","sql");
			        //update bill discount
			        StringBuilder insertDisc = new StringBuilder("insert into tblbilldiscdtl values ");
			        for (int i = 0; i < arrListBillDiscDtl.size(); i++)
			        {
			            clsPOSBillDiscountDtl objBillDiscountDtl = arrListBillDiscDtl.get(i);

			            if (i == 0)
			            {
			                insertDisc.append("('" + billNo + "','" + objBillDiscountDtl.getPOSCode() + "','" + objBillDiscountDtl.getDiscAmt() + "','" + objBillDiscountDtl.getDiscPer() + "','" + objBillDiscountDtl.getDiscOnAmt() + "',"
			                        + "'" + objBillDiscountDtl.getDiscOnType() + "','" + objBillDiscountDtl.getDiscOnValue() + "','" + objBillDiscountDtl.getReason() + "','" + objBillDiscountDtl.getRemark() + "','" + objBillDiscountDtl.getUserCreated() + "',"
			                        + "'" + userCode + "','" + objBillDiscountDtl.getDateCreated() + "','" + voidBillDate + "','" + strClientCode+ "','N')");
			            }
			            else
			            {
			                insertDisc.append(",('" + billNo + "','" + objBillDiscountDtl.getPOSCode() + "','" + objBillDiscountDtl.getDiscAmt() + "','" + objBillDiscountDtl.getDiscPer() + "','" + objBillDiscountDtl.getDiscOnAmt() + "',"
			                        + "'" + objBillDiscountDtl.getDiscOnType() + "','" + objBillDiscountDtl.getDiscOnValue() + "','" + objBillDiscountDtl.getReason() + "','" + objBillDiscountDtl.getRemark() + "','" + objBillDiscountDtl.getUserCreated() + "',"
			                        + "'" + userCode + "','" + objBillDiscountDtl.getDateCreated() + "','" + voidBillDate + "','" + strClientCode+ "','N')");
			            }
			        }
			        //insert new entries
			        if (insertDisc.length() > 35)
			        {
			        	  objBaseServiceImpl.funExecuteUpdate(insertDisc.toString(),"sql");
			        }
			    }
			  
			  
			  /////FullVoid
			  
			  
			  public Map funVoidBill(String posDate,String billNo,String favoritereason,String remark,String userCode,String strPOSCode,String strClientCode)
			    {
					Map objRetrun=new HashMap();
					String voidBillDate="";
					String[] reason;
					String reasoncode = "";
			        try
			        {
			        	 StringBuilder sqlQuery =new StringBuilder();
			        	 sqlQuery.append("select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='Void Bill'");
			            java.util.Date dt = new java.util.Date();
			            String time = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
			            voidBillDate = posDate + " " + time;

			            if (!billNo.isEmpty())
			            {
			            	int i = 0;
			            
			                int reasoncount = 0;
			                StringBuilder sqlBuilder =new StringBuilder();
			                sqlBuilder.setLength(0);
				 	       	sqlBuilder.append( "select count(strReasonName) from tblreasonmaster where strVoidBill='Y'" );
			         	   
					  	    List listSql1=objBaseServiceImpl.funGetList(sqlBuilder,"sql");
					  	  if(listSql1.size()>0)
					  	  {
					  		  for(Object obj1 :listSql1)
					  		  {
					  			  reasoncount = Integer.parseInt(obj1.toString());
					  		  }
					  	  }
			                if (reasoncount > 0)
			                {
			                    reason = new String[reasoncount];
			                    sqlBuilder.setLength(0);
					 	       	sqlBuilder.append( "select strReasonName from tblreasonmaster where strVoidBill='Y'");
						  	    List listSqlResn= objBaseServiceImpl.funGetList(sqlBuilder,"sql");
			                    i = 0;
			                    if(listSqlResn.size()>0)
							  	  {
							  		  for(int j=0;j<listSqlResn.size();j++){
							  			 Object obj1 = (Object) listSqlResn.get(j);
			                        reason[i] =obj1.toString();
			                        i++;
			                    }

			                    if (favoritereason != null)
			                    {
			                    	 sqlBuilder.setLength(0);
							 	       	sqlBuilder.append( "select strReasonCode from tblreasonmaster where strReasonName='" + favoritereason + "' "
			                                + "and strVoidBill='Y'");
							  	    List listSqlResnFav= objBaseServiceImpl.funGetList(sqlBuilder,"sql");
							  	    if(!listSqlResnFav.isEmpty())
							  	    {
							  	    	for(int j=0;j<listSqlResnFav.size();j++)
							  	    	{
							  	    		Object obj=(Object)listSqlResnFav.get(j);
							  	    		reasoncode = obj.toString();
			                        }
							  	    }


			                            String billDate = "";
			                            
			                            String shiftNo = "1";
			                            sqlBuilder.setLength(0);
							 	       	sqlBuilder.append( "select left(dteBillDate,10) ,right(dteDateCreated,8),intShiftCode from tblbillhd"
			                                    + " where strBillNo='" + billNo + "'");
			                            
								  	    List listSqlBillNo= objBaseServiceImpl.funGetList(sqlBuilder,"sql");
								  	    if(!listSqlBillNo.isEmpty())
								  	    {
								  	    	Object[] obj=(Object[])listSqlBillNo.get(0);
								  	    
								  	    	 billDate = obj[0].toString() + " " + obj[1].toString();
				                             shiftNo = obj[2].toString();
				                            
								  	    }
			                       
								  	  sqlBuilder.setLength(0);
							 	       	sqlBuilder.append( "select a.strItemCode,a.strItemName,a.strBillNo,a.dblQuantity,a.dblAmount,"
			                                    + "a.dblTaxAmount,a.dteBillDate,b.strTableNo,a.strKOTNo,b.intShiftCode "
			                                    + "from tblbilldtl a,tblbillhd b "
			                                    + "where a.strBillNo=b.strBillNo and a.strBillNo='" + billNo + "'");
			                            

			                           
								  	    List listSqlItem= objBaseServiceImpl.funGetList(sqlBuilder,"sql");
								  	    if(!listSqlItem.isEmpty())
								  	    {  
								  	    	for(int j=0;j<listSqlItem.size();j++)
								  	    	{	
								  	    	Object[] obj=(Object[])listSqlItem.get(j);
					
				                            String itemCode = obj[0].toString();
			                                String itemname = obj[1].toString();
			                                String billno = obj[2].toString();
			                                String qty =obj[3].toString();
			                                String amount = obj[4].toString();
			                                String taxAmount = obj[5].toString();
			                                billDate = obj[6].toString();
			                                String tableNo =obj[7].toString();
			                                String KOTNo =obj[8].toString();

			                                sqlBuilder.setLength(0);
			                                sqlBuilder.append("insert into tblvoidmodifierdtl(strBillNo,strItemCode,strModifierCode,"
			                                        + "strModifierName,dblQuantity,dblAmount,strClientCode,strCustomerCode"
			                                        + ",strRemarks,strReasonCode)"
			                                        + " (select strBillNo,strItemCode,strModifierCode,strModifierName,dblQuantity,"
			                                        + "dblAmount,strClientCode,strCustomerCode,'" + remark + "','" + reasoncode + "' "
			                                        + "from tblbillmodifierdtl "
			                                        + "where strBillNo='" + billno + "' and left(strItemCode,7)='" + itemCode + "')");
			                                
			                                if (userCode.equalsIgnoreCase(("super")))
			                                {
			                                	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                }
			                                else
			                                {
			                                  
										  	    List listSql= objBaseServiceImpl.funGetList(sqlQuery,"sql");
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    
										  	    	Object obj1=(Object)listSql.get(j);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                        }
			                                    }
			                                }

			                                sqlBuilder.setLength(0);
			                                sqlBuilder.append("insert into tblvoidbilldtl(strPosCode,strReasonCode,strReasonName,strItemCode"
			                                        + ",strItemName,strBillNo,intQuantity,dblAmount,dblTaxAmount,dteBillDate,"
			                                        + "strTransType,dteModifyVoidBill,intShiftCode,strUserCreated,strClientCode"
			                                        + ",strKOTNo,strRemarks) "
			                                        + "values('" + strPOSCode + "','" + reasoncode + "','"
			                                        + favoritereason + "','" + itemCode + "','" + itemname + "','" + billno + "','"
			                                        + qty + "','" + amount + "','" + taxAmount + "','" + billDate + "','" + "VB" + "','"
			                                        + voidBillDate + "'," + shiftNo + ""
			                                        + ",'" + userCode + "','" +strClientCode+ "'"
			                                        + ",'" + KOTNo + "','" + remark + "')");
			                             
			                                if (userCode.equalsIgnoreCase(("super")))
			                                {
			                                	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                }
			                                else
			                                {
											  	    List listSql=objBaseServiceImpl.funGetList(sqlQuery,"sql");
											  	    if(!listSql.isEmpty())
											  	    {  
											  	    	Object obj1=(Object)listSql.get(0);
				                                
				                                        if (Boolean.parseBoolean(obj1.toString()))
				                                        {
				                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");;
				                                        }
				                                    }
			                                }
			                                String tableStatus = funGetTableStatus(tableNo);
			                                if (tableStatus.equalsIgnoreCase("Normal"))
			                                {
			                                    String updateQuery = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
			                                            + "where strTableNo='" + tableNo + "'";
			                                    objBaseServiceImpl.funExecuteUpdate(updateQuery,"sql");
			                                }
			                                else
			                                {
			                                    String updateQuery = "update tbltablemaster set strStatus='" + tableStatus + "' "
			                                            + "where strTableNo='" + tableNo + "'";
			                                    objBaseServiceImpl.funExecuteUpdate(updateQuery,"sql");
			                                    
			                                }
			                            }
								  	    }
								  	  sqlBuilder.setLength(0);
							 	       	sqlBuilder.append( "select * from tblvoidbillhd where strBillNo='" + billNo + "'");
								  	    List listSqlvoidbill= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
								  	    if(!listSqlvoidbill.isEmpty())
								  	    {  
								  	    	Object[] obj=(Object[])listSqlvoidbill.get(0);
			                                sqlBuilder.setLength(0);
			                                sqlBuilder.append("delete from tblvoidbillhd where strBillNo='" + billNo + "'");
			                                objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");

			                                sqlBuilder.setLength(0);
			                                sqlBuilder.append("insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
			                                        + "dblActualAmount,dblModifiedAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo"
			                                        + ",intShiftCode,strUserCreated,strUserEdited,strClientCode,strRemark) "
			                                        + "(select '" + strPOSCode + "','" + reasoncode + "','" + favoritereason + "','"
			                                        + billNo+ "'," + "dblGrandTotal,dblGrandTotal,'" + billDate + "','VB','"
			                                        + voidBillDate + "',strTableNo,strWaiterNo,'" + shiftNo
			                                        + "','" + userCode + "','" + userCode + "',strClientCode,'" + remark + "' "
			                                        + "from tblbillhd where strBillNo='" + billNo + "')");
			                                if (userCode.equalsIgnoreCase(("super")))
			                                {
			                                	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                }
			                                else
			                                {
			                                	  
											  	    List listSql= objBaseServiceImpl.funGetList(sqlQuery, "sql");
											  	    if(!listSql.isEmpty())
											  	    {  
											  	    	Object obj1=(Object)listSql.get(0);
				                                
				                                        if (Boolean.parseBoolean(obj1.toString()))
				                                        {
				                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				                                        }
				                                    }
			                                }

			                                sqlBuilder.setLength(0);
			                                sqlBuilder.append( "update tblvoidbillhd set dblActualAmount=dblActualAmount+" +Double.parseDouble(obj[4].toString()) + ""
			                                        + ",dblModifiedAmount=dblModifiedAmount+" + Double.parseDouble(obj[58].toString())+ " "
			                                        + " where strBillNo='" + billNo + "' ");
			                                if (userCode.equalsIgnoreCase(("super")))
			                                {
			                                	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                }
			                                else
			                                {
											  	    List listSql= objBaseServiceImpl.funGetList(sqlQuery, "sql");
											  	    if(!listSql.isEmpty())
											  	    {  
											  	    	Object obj1=(Object)listSql.get(0);
				                                
				                                        if (Boolean.parseBoolean(obj1.toString()))
				                                        {
				                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				                                        }
				                                    }
			                                }
			                            }
			                            else
			                            {
			                            	 sqlBuilder.setLength(0);
				                                sqlBuilder.append( "insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
			                                        + "dblActualAmount,dblModifiedAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo"
			                                        + ",intShiftCode,strUserCreated,strUserEdited,strClientCode,strRemark) "
			                                        + "(select '" + strPOSCode + "','" + reasoncode + "','" + favoritereason + "','"
			                                        + billNo+ "'," + "dblGrandTotal,dblGrandTotal,'" + billDate + "','VB','"
			                                        + voidBillDate + "',strTableNo,strWaiterNo,'" + shiftNo
			                                        + "','" + userCode + "','" + userCode + "',strClientCode,'" + remark + "' "
			                                        + "from tblbillhd where strBillNo='" + billNo + "')");
			                                if (userCode.equalsIgnoreCase(("super")))
			                                {
			                                	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                }
			                                else
			                                {
											  	    List listSql= objBaseServiceImpl.funGetList(sqlQuery, "sql");
											  	    if(!listSql.isEmpty())
											  	    {  
											  	    	Object obj1=(Object)listSql.get(0);
				                                
				                                        if (Boolean.parseBoolean(obj1.toString()))
				                                        {
				                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				                                        }
				                                    }
			                                }
			                            }
			                            }
			                          
			                    		sqlBuilder.setLength(0);
			                    		sqlBuilder.append( "insert into tblvoidbillsettlementdtl"
			                                    + "(strBillNo,strSettlementCode,dblSettlementAmt,"
			                                    + "dblPaidAmt,strExpiryDate,strCardName,"
			                                    + "strRemark,strClientCode,strCustomerCode,"
			                                    + "dblActualAmt,dblRefundAmt,strGiftVoucherCode)"
			                                    + "(select strBillNo,strSettlementCode,dblSettlementAmt,"
			                                    + "dblPaidAmt,strExpiryDate,strCardName,"
			                                    + "strRemark,strClientCode,strCustomerCode,"
			                                    + "dblActualAmt,dblRefundAmt,strGiftVoucherCode "
			                                    + " from tblbillsettlementdtl where strBillNo='" + billNo + "')");
			                            if (userCode.equalsIgnoreCase(("super")))
		                                {
			                            	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
		                                }
		                                else
		                                {
										  	    List listSql=objBaseServiceImpl.funGetList(sqlQuery, "sql");
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    	Object obj1=(Object)listSql.get(0);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			                                        }
			                                    }
		                                }
		                            }

       
			                    String date = "";
	                            String POSCode ="";
	                            sqlBuilder.setLength(0);
					 	       	sqlBuilder.append( "select date(dteBillDate),strPOSCode from tblbillhd where strBillNo='" + billNo + "' ");
									  	    List listSqlDate= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
									  	 if(!listSqlDate.isEmpty())
									  	 {    
									  	 Object[] obj=(Object[])listSqlDate.get(0);
									  	 date = obj[0].toString();
			                             POSCode = obj[1].toString();
									  	 }
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbilldtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbillhd where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbillmodifierdtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbillsettlementdtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblhomedelivery where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbilltaxdtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbilldiscdtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbillpromotiondtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblbillcomplementrydtl where strBillNo='" + billNo + "'","sql");
									  	objBaseServiceImpl.funExecuteUpdate("Delete from tblhomedeldtl where strBillNo='" + billNo + "'","sql");
									  	
									  	sqlBuilder.setLength(0);
									  	sqlBuilder.append("select strEnableBillSeries from tblsetup where (strPOSCode='"+strPOSCode+"'  OR strPOSCode='All') ");
										List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
										String gEnableShiftYN="";
										if(list!=null && list.size()>0)
										{				
											gEnableShiftYN=list.get(0).toString();
										}
									   
										objRetrun.put("sucessfully", "sucessfully");
									  	if (gEnableShiftYN.equals("Y"))
			                            {

									  		sqlBuilder.setLength(0);
								 	       	sqlBuilder.append( "select a.strPOSCode,a.strHdBillNo,a.strDtlBillNos "
			                                        + "from tblbillseriesbilldtl a "
			                                        + "where a.strHdBillNo='" + billNo + "'"
			                                        + "and a.strPOSCode='" + POSCode + "'  ");
			                            
										  	    List listSqlbill= objBaseServiceImpl.funGetList(sqlBuilder, "sql");
										  	    if(!listSqlbill.isEmpty())
										  	    {  
										  	    	Object[] obj=(Object[])listSqlbill.get(0);
			                              
			                                    String dtlBills = obj[2].toString();
			                                    String arrDtlBills[] = dtlBills.split(",");
			                                    for (int j = 0; j < arrDtlBills.length; j++)
			                                    {

			                                    }
			                                }

			                            }
			                            else
			                            {

			                            }

			                    }
			                }
			                else
			                {

			                }
			        }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			        finally
			        {
			        	return objRetrun;
			        }
			    }
			  
			  
			     private String funGetTableStatus(String tableNo)
			        {
			            String tableStatus = "Normal";
			            try
			            {
			            	StringBuilder 	sqlBuilder=new StringBuilder();
			            	sqlBuilder.setLength(0);
				 	       	sqlBuilder.append( "select strTableNO from tblitemrtemp where strTableNO='" + tableNo + "' ");
					  	    List listSqlbill=  objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					  	    if(!listSqlbill.isEmpty())
					  	    {  
					  	    	Object[] obj=(Object[])listSqlbill.get(0);
					  
			                    tableStatus = "Occupied";
			                }
			            }
			            catch (Exception e)
			            {
			                e.printStackTrace();
			            }
			            finally
			            {
			                return tableStatus;
			            }
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
			     
	public Map funLoadBillGrid(String dtPOSDate, String strPOSCode,
			String searchBillNo) {
		Map objReturn = new HashMap();
		List list = new ArrayList();
		StringBuilder sqlBuilder = new StringBuilder();
		try {

			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strBillNo,CONCAT(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),' ',TIME_FORMAT(time(a.dteBillDate),'%h:%i')) as dteBillDate,a.strSettelmentMode,a.dblTaxAmt,a.dblSubTotal,a.dblGrandTotal"
							+ ",a.strUserCreated,ifnull(b.strTableName,'') "
							+ " from tblbillhd a left outer join tbltablemaster b "
							+ " on a.strTableNo=b.strTableNo "
							+ " where date(a.dteBillDate)='"
							+ dtPOSDate
							+ "' "
							+ " and a.strPOSCode='"
							+ strPOSCode
							+ "' "
							+ " and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ");
			if (searchBillNo.length() > 0) {
				sqlBuilder.append(" and a.strBillNo LIKE '" + searchBillNo
						+ "%'  or a.strBillNo LIKE '%" + searchBillNo + "' ");
			}

			sqlBuilder.append("  and a.strSettelmentMode='' ");
			sqlBuilder.append("  and a.strSettelmentMode='' ");

			List listSql = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSql.size() > 0) {
				for (int cnt = 0; cnt < listSql.size(); cnt++) {

					Object[] obj = (Object[]) listSql.get(cnt);
					Map hmObj = new HashMap();
			
					hmObj.put("strBillNo", obj[0].toString());
					hmObj.put("dteBillDate", obj[1].toString());
					hmObj.put("dblGrandTotal", obj[5].toString());
					hmObj.put("strTableName", obj[7].toString());
					list.add(hmObj);
				}
			}
			objReturn.put("jArr", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objReturn;
	}
			 	
			 	
	public Map funSelectBill(String billNo, String strClientCode,String strPOSCode, String posDate) {

		Map hmRrturn = new HashMap();
		try {

			
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='"+strPOSCode+"'  OR strPOSCode='All') ");
			List list=objBaseServiceImpl.funGetList(sqlBuilder, "sql");			
			String gDirectAreaCode ="";
			if(list!=null && list.size()>0)
			{				
				gDirectAreaCode=list.get(0).toString();
			}
		
		
		

			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strOperationType,a.strTableNo,ifnull(b.strAreaCode,'') "
					+ "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
					+ "where strBillNo='" + billNo + "'");

			List listSql =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSql.size() > 0) {
				for (int cnt = 0; cnt < listSql.size(); cnt++) {

					Object[] obj = (Object[]) listSql.get(cnt);

					operationTypeForTax = "DineIn";
					if (obj[0].toString().equals("Home Delivery")) {
						operationTypeForTax = "HomeDelivery";
					}
					if (obj[0].toString().equals("Take Away")) {
						operationTypeForTax = "TakeAway";
					}
					if (obj[1].toString().trim().length() > 0) {
						areaCode = obj[2].toString();
					} else {
						areaCode = gDirectAreaCode;
					}
				}
			}

			arrListBillDtl.clear();
			arrListBillHd.clear();
			arrListBillModifierDtl.clear();
			arrListVoidBillHd.clear();
			arrListVoidBillDtl.clear();
			arrListVoidBillModifierDtl.clear();
			arrListBillDiscDtl.clear();
			arrListKOTWiseBillDtl.clear();
			sqlBuilder.setLength(0);
			sqlBuilder.append( "select * from tblbillhd " + "where strBillNo='" + billNo
					+ "'");

			List listSqlBillHd =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlBillHd.size() > 0) {
				for (int cnt = 0; cnt < listSqlBillHd.size(); cnt++) {

					Object[] obj = (Object[]) listSqlBillHd.get(cnt);

					clsPOSBillHd objBillHd = new clsPOSBillHd();
					objBillHd.setStrBillNo(obj[0].toString());
					objBillHd.setStrAdvBookingNo(obj[1].toString());
					objBillHd.setDteBillDate(obj[2].toString());
					objBillHd.setStrPOSCode(obj[3].toString());
					objBillHd.setStrSettelmentMode(obj[4].toString());
					objBillHd.setDblDiscountAmt(Double.parseDouble(obj[5].toString()));
					objBillHd.setDblDiscountPer(Double.parseDouble(obj[6].toString()));
					objBillHd.setDblTaxAmt(Double.parseDouble(obj[7].toString()));
					objBillHd.setDblSubTotal(Double.parseDouble(obj[8].toString()));
					objBillHd.setDblGrandTotal(Double.parseDouble(obj[9].toString()));
					objBillHd.setStrTakeAway(obj[10].toString());
					objBillHd.setStrOperationType(obj[11].toString());
					objBillHd.setStrUserCreated(obj[12].toString());
					objBillHd.setStrUserEdited(obj[13].toString());
					objBillHd.setDteDateCreated(obj[14].toString());
					objBillHd.setDteDateEdited(obj[15].toString());
					objBillHd.setStrClientCode(obj[16].toString());
					objBillHd.setStrTableNo(obj[17].toString());
					objBillHd.setStrWaiterNo(obj[18].toString());
					objBillHd.setStrCustomerCode(obj[19].toString());
					objBillHd.setStrManualBillNo(obj[20].toString());
					objBillHd.setIntShiftCode(Integer.parseInt(obj[21].toString()));
					intShiftNo = Integer.parseInt(obj[21].toString());
					objBillHd.setIntPaxNo(Integer.parseInt(obj[22].toString()));
					objBillHd.setStrDataPostFlag(obj[23].toString());
					objBillHd.setStrReasonCode(obj[24].toString());
					objBillHd.setStrRemarks(obj[25].toString());
					objBillHd.setDblTipAmount(Double.parseDouble(obj[26].toString()));
					objBillHd.setDteSettleDate(obj[27].toString());
					objBillHd.setStrCounterCode(obj[28].toString());
					objBillHd.setDblDeliveryCharges(Double.parseDouble(obj[29].toString()));
					objBillHd.setStrCouponCode(obj[30].toString());
					objBillHd.setStrAreaCode(obj[31].toString());
					objBillHd.setStrDiscountRemark(obj[32].toString());
					objBillHd.setStrTakeAwayRemarks(obj[33].toString());
					objBillHd.setStrDiscountOn(obj[34].toString());
					objBillHd.setDblGrandTotalRoundOffBy(Double.parseDouble(obj[43].toString()));
					objBillHd.setIntLastOrderNo(Integer.parseInt(obj[46].toString()));
					arrListBillHd.add(objBillHd);
				}
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
					+ ",sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,IFNULL(a.strCustomerCode,'') "
					+ ",concat(a.tmeOrderProcessing,''),a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
					+ ",a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
					+ ",c.strSubGroupName,c.strGroupCode,d.strGroupName "
					+ "from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
					+ "where a.strBillNo='"
					+ billNo
					+ "'  "
					+ "and a.strItemCode=b.strItemCode "
					+ "and b.strSubGroupCode=c.strSubGroupCode "
					+ "and c.strGroupCode=d.strGroupCode "
					+ "group by a.strItemCode,a.strItemName,a.strBillNo;");

			
			List listSqlBillDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlBillDtl.size() > 0) {
				for (int cnt = 0; cnt < listSqlBillDtl.size(); cnt++) {
					Object[] obj = (Object[]) listSqlBillDtl.get(cnt);

					clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
					String itemCode = obj[0].toString();
					objBillDtl.setStrItemCode(obj[0].toString());
					objBillDtl.setStrItemName(obj[1].toString());
					objBillDtl.setStrBillNo(obj[2].toString());
					objBillDtl.setStrAdvBookingNo(obj[3].toString());
					objBillDtl.setDblRate(Double.parseDouble(obj[4].toString()));
					objBillDtl.setDblQuantity(Double.parseDouble(obj[5].toString()));
					objBillDtl.setDblAmount(Double.parseDouble(obj[6].toString()));
					objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7].toString()));
					objBillDtl.setDteBillDate(obj[8].toString());
					objBillDtl.setStrKOTNo(obj[9].toString());
					objBillDtl.setStrClientCode(obj[10].toString());
					objBillDtl.setStrCustomerCode(obj[11].toString());
					objBillDtl.setTmeOrderProcessing(obj[12].toString());
					objBillDtl.setStrDataPostFlag(obj[13].toString());
					objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
					objBillDtl.setStrManualKOTNo(obj[15].toString());
					objBillDtl.setTdhYN(obj[16].toString());
					objBillDtl.setStrPromoCode(obj[17].toString());
					objBillDtl.setStrCounterCode(obj[18].toString());
					objBillDtl.setStrWaiterNo(obj[19].toString());
					objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20].toString()));
					objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21].toString()));
					objBillDtl.setSubGrouName(obj[23].toString());
					objBillDtl.setGroupName(obj[25].toString());
					arrListBillDtl.add(objBillDtl);

					sqlBuilder.setLength(0);
					sqlBuilder.append( "select a.strBillNo,a.strItemCode,a.strModifierCode,a.strModifierName "
							+ ",a.dblRate,sum(a.dblQuantity),sum(a.dblAmount),a.strClientCode,IFNULL(a.strCustomerCode,'') "
							+ ",a.strDataPostFlag,a.strMMSDataPostFlag,sum(a.dblDiscPer),sum(a.dblDiscAmt) "
							+ ",b.strSubGroupCode ,c.strSubGroupName,c.strGroupCode,d.strGroupName "
							+ "from tblbillmodifierdtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
							+ "where left(a.strItemCode,7)='"
							+ itemCode
							+ "'  "
							+ "and a.strBillNo='"
							+ billNo
							+ "' "
							+ "and left(a.strItemCode,7)=b.strItemCode "
							+ "and b.strSubGroupCode=c.strSubGroupCode "
							+ "and c.strGroupCode=d.strGroupCode  "
							+ "group by a.strItemCode,a.strModifierName ");

					
					List listSqlBillMod = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
					if (listSqlBillMod.size() > 0) {
						for (int i = 0; i < listSqlBillMod.size(); i++) {
							Object[] objMod = (Object[]) listSqlBillMod.get(i);

							clsPOSBillModifierDtl objBillModDtl = new clsPOSBillModifierDtl();
							objBillModDtl.setStrBillNo(objMod[0].toString());
							objBillModDtl.setStrItemCode(objMod[1].toString());
							objBillModDtl.setStrModifierCode(objMod[2].toString());
							objBillModDtl.setStrModifierName(objMod[3].toString());
							objBillModDtl.setDblRate(Double.parseDouble(objMod[4].toString()));
							objBillModDtl.setDblQuantity(Double.parseDouble(objMod[5].toString()));
							objBillModDtl.setDblAmount(Double
									.parseDouble(objMod[6].toString()));
							objBillModDtl
									.setStrClientCode(objMod[7].toString());
							objBillModDtl.setStrCustomerCode(objMod[8]
									.toString());
							objBillModDtl.setStrDataPostFlag(objMod[9]
									.toString());
							objBillModDtl.setStrMMSDataPostFlag(objMod[10]
									.toString());
							objBillModDtl.setDblDiscPer(Double
									.parseDouble(objMod[11].toString()));
							objBillModDtl.setDblDiscAmt(Double
									.parseDouble(objMod[12].toString()));
							objBillModDtl.setSubGrouName(objMod[14].toString());
							objBillModDtl.setGroupName(objMod[16].toString());
							arrListBillModifierDtl.add(objBillModDtl);
						}
					}

				}
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
					+ " ,sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,IFNULL(a.strCustomerCode,'') "
					+ " ,concat(a.tmeOrderProcessing,''),a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
					+ " ,a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
					+ " ,c.strSubGroupName,c.strGroupCode,d.strGroupName,concat(a.tmeOrderPickup,'') "
					+ " from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
					+ " where a.strBillNo='"
					+ billNo
					+ "'  "
					+ " and a.strItemCode=b.strItemCode "
					+ " and b.strSubGroupCode=c.strSubGroupCode "
					+ " and c.strGroupCode=d.strGroupCode "
					+ " group by a.strBillNo,a.strKOTNo,a.strItemCode,a.strItemName "
					+ " order by a.strKOTNo desc,a.strItemCode; ");

			
			List listSqlBill = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlBill.size() > 0) {
				for (int i = 0; i < listSqlBill.size(); i++) {
					Object[] obj = (Object[]) listSqlBill.get(i);

					clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
					objBillDtl.setStrItemCode(obj[0].toString());
					objBillDtl.setStrItemName(obj[1].toString());
					objBillDtl.setStrBillNo(obj[2].toString());
					objBillDtl.setStrAdvBookingNo(obj[3].toString());
					objBillDtl
							.setDblRate(Double.parseDouble(obj[4].toString()));
					objBillDtl.setDblQuantity(Double.parseDouble(obj[5]
							.toString()));
					objBillDtl.setDblAmount(Double.parseDouble(obj[6]
							.toString()));
					objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7]
							.toString()));
					objBillDtl.setDteBillDate(obj[8].toString());
					objBillDtl.setStrKOTNo(obj[9].toString());
					objBillDtl.setStrClientCode(obj[10].toString());
					objBillDtl.setStrCustomerCode(obj[11].toString());
					objBillDtl.setTmeOrderProcessing(obj[12].toString());
					objBillDtl.setStrDataPostFlag(obj[13].toString());
					objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
					objBillDtl.setStrManualKOTNo(obj[15].toString());
					objBillDtl.setTdhYN(obj[16].toString());
					objBillDtl.setStrPromoCode(obj[17].toString());
					objBillDtl.setStrCounterCode(obj[18].toString());
					objBillDtl.setStrWaiterNo(obj[19].toString());
					objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20]
							.toString()));
					objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21]
							.toString()));
					objBillDtl.setSubGrouName(obj[23].toString());
					objBillDtl.setGroupName(obj[25].toString());
					objBillDtl.setStrOrderPickupTime(obj[26].toString());
					arrListKOTWiseBillDtl.add(objBillDtl);
				}

			}
			sqlBuilder.setLength(0);
			sqlBuilder.append( "select * from tblbilldiscdtl where strBillNo='" + billNo
					+ "'");
			
			List listSqlBillDisc = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlBillDisc.size() > 0) {
				for (int i = 0; i < listSqlBillDisc.size(); i++) {
					Object[] obj = (Object[]) listSqlBillDisc.get(i);
					clsPOSBillDiscountDtl objBillDiscDtl = new clsPOSBillDiscountDtl();
					objBillDiscDtl.setBillNo(obj[0].toString());
					objBillDiscDtl.setPOSCode(obj[1].toString());
					objBillDiscDtl.setDiscAmt(Double.parseDouble(obj[2]
							.toString()));
					objBillDiscDtl.setDiscPer(Double.parseDouble(obj[3]
							.toString()));
					objBillDiscDtl.setDiscOnAmt(Double.parseDouble(obj[4]
							.toString()));
					objBillDiscDtl.setDiscOnType(obj[5].toString());
					objBillDiscDtl.setDiscOnValue(obj[6].toString());
					objBillDiscDtl.setReason(obj[7].toString());
					objBillDiscDtl.setRemark(obj[8].toString());
					objBillDiscDtl.setUserCreated(obj[9].toString());
					objBillDiscDtl.setUserEdited(obj[10].toString());
					objBillDiscDtl.setDateCreated(obj[11].toString());
					objBillDiscDtl.setDateEdited(obj[12].toString());
					arrListBillDiscDtl.add(objBillDiscDtl);
				}
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select * from tblvoidbillhd where strBillNo='" + billNo
					+ "'");

			List listSqlVoidBillHd = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlVoidBillHd.size() > 0) {
				for (int i = 0; i < listSqlVoidBillHd.size(); i++) {
					Object[] obj = (Object[]) listSqlVoidBillHd.get(i);

					clsPOSVoidBillHd objVoidBillHd = new clsPOSVoidBillHd();
					objVoidBillHd.setStrPosCode(obj[0].toString());
					objVoidBillHd.setStrReasonCode(obj[1].toString());
					objVoidBillHd.setStrReasonName(obj[2].toString());
					objVoidBillHd.setStrBillNo(obj[3].toString());
					objVoidBillHd.setDblActualAmount(Double.parseDouble(obj[4]
							.toString()));
					objVoidBillHd.setDblModifiedAmount(Double
							.parseDouble(obj[5].toString()));
					objVoidBillHd.setDteBillDate(obj[6].toString());
					objVoidBillHd.setStrTransType(obj[7].toString());
					objVoidBillHd.setDteModifyVoidBill(obj[8].toString());
					objVoidBillHd.setStrTableNo(obj[9].toString());
					objVoidBillHd.setStrWaiterNo(obj[10].toString());
					objVoidBillHd.setIntShiftCode(Integer.parseInt(obj[11]
							.toString()));
					objVoidBillHd.setStrUserCreated(obj[12].toString());
					objVoidBillHd.setStrUserEdited(obj[13].toString());
					objVoidBillHd.setStrClientCode(obj[14].toString());
					objVoidBillHd.setStrDataPostFlag(obj[15].toString());
					objVoidBillHd.setStrRemark(obj[16].toString());
					arrListVoidBillHd.add(objVoidBillHd);
				}
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select * from tblvoidbilldtl where strBillNo='" + billNo
					+ "'");

			List listSqlVoidBillDtl =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlVoidBillDtl.size() > 0) {
				for (int i = 0; i < listSqlVoidBillDtl.size(); i++) {
					Object[] obj = (Object[]) listSqlVoidBillDtl.get(i);

					clsPOSVoidBillDtl objVoidBillDtl = new clsPOSVoidBillDtl();
					objVoidBillDtl.setStrPosCode(obj[0].toString());
					objVoidBillDtl.setStrReasonCode(obj[1].toString());
					objVoidBillDtl.setStrReasonName(obj[2].toString());
					objVoidBillDtl.setStrItemCode(obj[3].toString());
					objVoidBillDtl.setStrItemName(obj[4].toString());
					objVoidBillDtl.setStrBillNo(obj[5].toString());
					objVoidBillDtl.setIntQuantity(Integer.parseInt(obj[6]
							.toString()));
					objVoidBillDtl.setDblAmount(Double.parseDouble(obj[7]
							.toString()));
					objVoidBillDtl.setDblTaxAmount(Double.parseDouble(obj[8]
							.toString()));
					objVoidBillDtl.setDteBillDate(obj[9].toString());
					objVoidBillDtl.setStrTransType(obj[10].toString());
					objVoidBillDtl.setDteModifyVoidBill(obj[11].toString());
					objVoidBillDtl.setStrSettlementCode(obj[12].toString());
					objVoidBillDtl.setDblSettlementAmt(Double
							.parseDouble(obj[13].toString()));
					objVoidBillDtl.setDblPaidAmt(Double.parseDouble(obj[14]
							.toString()));
					objVoidBillDtl.setStrTableNo(obj[15].toString());
					objVoidBillDtl.setStrWaiterNo(obj[16].toString());
					objVoidBillDtl.setIntShiftCode(intShiftNo);
					objVoidBillDtl.setStrUserCreated(obj[18].toString());
					objVoidBillDtl.setStrClientCode(obj[19].toString());
					objVoidBillDtl.setStrDataPostFlag(obj[20].toString());
					objVoidBillDtl.setStrKOTNo(obj[21].toString());
					objVoidBillDtl.setStrRemarks(obj[22].toString());
					arrListVoidBillDtl.add(objVoidBillDtl);
				}
			}

			sqlBuilder.setLength(0);
			sqlBuilder.append( "select * from tblvoidmodifierdtl where strBillNo='" + billNo
					+ "'");

			List listSqlModDtl =objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listSqlModDtl.size() > 0) {
				for (int i = 0; i < listSqlModDtl.size(); i++) {
					Object[] obj = (Object[]) listSqlModDtl.get(i);

					clsPOSVoidBillModifierDtl objVoidBillModDtl = new clsPOSVoidBillModifierDtl();
					objVoidBillModDtl.setStrBillNo(obj[0].toString());
					objVoidBillModDtl.setStrItemCode(obj[1].toString());
					objVoidBillModDtl.setStrModifierCode(obj[2].toString());
					objVoidBillModDtl.setStrModifierName(obj[3].toString());
					objVoidBillModDtl.setDblQuantity(Double.parseDouble(obj[4]
							.toString()));
					objVoidBillModDtl.setDblAmount(Double.parseDouble(obj[5]
							.toString()));
					objVoidBillModDtl.setStrClientCode(obj[6].toString());
					objVoidBillModDtl.setStrCustomerCode(obj[7].toString());
					objVoidBillModDtl.setStrDataPostFlag(obj[8].toString());
					objVoidBillModDtl.setStrRemarks(obj[9].toString());
					objVoidBillModDtl.setStrReasonCode(obj[10].toString());
					arrListVoidBillModifierDtl.add(objVoidBillModDtl);
				}
			}
			hmRrturn = funFillItemGrid(billNo, strPOSCode, posDate,strClientCode, arrListBillDtl, arrListBillModifierDtl,arrListBillHd);
			Gson gson = new Gson();
			Type clsBillModifierDtlType = new TypeToken<List<clsPOSBillModifierDtl>>() {
			}.getType();
			Type clsVoidBillModifierDtlType = new TypeToken<List<clsPOSVoidBillModifierDtl>>() {
			}.getType();
			Type clsBillDtlType = new TypeToken<List<clsPOSBillDtl>>() {
			}.getType();
			Type clsVoidBillDtlType = new TypeToken<List<clsPOSVoidBillDtl>>() {
			}.getType();
			Type clsBillHdType = new TypeToken<List<clsPOSBillHd>>() {
			}.getType();
			Type clsBillDiscountDtlType = new TypeToken<List<clsPOSBillDiscountDtl>>() {
			}.getType();
			Type clsVoidBillHdType = new TypeToken<List<clsPOSVoidBillHd>>() {
			}.getType();
			Type clsBillTaxDtlType = new TypeToken<List<clsPOSBillTaxDtl>>() {
			}.getType();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			return hmRrturn;
		}

	}
			 	
			 	
}
