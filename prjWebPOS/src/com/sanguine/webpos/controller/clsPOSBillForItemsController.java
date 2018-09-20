package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSBillSeriesBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillHdModel_ID;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSTextFileGenerator;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSBillForItemsController
{

	@Autowired
	intfBaseService objBaseService;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;

	@Autowired
	private clsGlobalFunctions objGlobal;

	@Autowired
	private clsPOSUtilityController objUtility;

	@Autowired
	private clsPOSSetupUtility objPOSSetupUtility;

	@Autowired
	clsPOSTextFileGenerator objTextFileGeneration;

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	@Autowired
	private clsPOSBillingAPIController objBillingAPI;
	
	
	

	private StringBuilder sql = new StringBuilder();
	private Map<String, clsPOSPromotionItems> hmPromoItem = new HashMap<String, clsPOSPromotionItems>();

	@RequestMapping(value = "/frmBillForItems", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "1";
		Map mapPOS = new HashMap();

		try
		{
			urlHits = request.getParameter("saddr").toString();

			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			String posClientCode = request.getSession().getAttribute("gPOSCode").toString();
			String posCode = request.getSession().getAttribute("gPOSCode").toString();
			String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			String userCode = request.getSession().getAttribute("gUserCode").toString();

			model.put("gPOSCode", posCode);
			model.put("gClientCode", clientCode);

			model.put("urlHits", urlHits);
			model.put("billNo", "");
			model.put("billDate", posDate.split("-")[2] + "-" + posDate.split("-")[1] + "-" + posDate.split("-")[0]);

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select strPOSCode,strPOSName from tblposmaster");
			List list = objBaseService.funGetList(sqlBuilder, "sql");

			mapPOS.put("All", "All");
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				Object obj = list.get(cnt);
				mapPOS.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());

			}

			Map mapGetBussyTableNo = new TreeMap();
			mapGetBussyTableNo.put("All", "All");
			sqlBuilder.setLength(0);
			sqlBuilder.append("select distinct a.strTableNo,b.strTableName,b.strStatus " + "from tblitemrtemp a,tbltablemaster b " + "where a.strTableNo=b.strTableNo " + "and a.strPOSCode='" + posCode + "' " + "and a.strNCKotYN='N' " + "order by b.intSequence ");
			list = objBaseService.funGetList(sqlBuilder, "sql");
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				Object obj = list.get(cnt);
				mapGetBussyTableNo.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
			}

			model.put("posList", mapPOS);
			model.put("bussyTableList", mapGetBussyTableNo);

			List listReson = funLoadResonCode();
			model.put("listReson", listReson);

		}
		catch (Exception e)
		{
			urlHits = "1";
		}

		/* Filling model attribute values */

		model.put("transactionType", "Bill For Items");
		model.put("urlHits", urlHits);
		String formToBeOpen = "Bill For Items";
		model.put("formToBeOpen", formToBeOpen);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmWebPOSBilling", "command", new clsPOSBillSettlementBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmWebPOSBilling", "command", new clsPOSBillSettlementBean());
		}
		else
		{
			return null;
		}

	}
	
	@SuppressWarnings("finally")
	@RequestMapping(value = "/funGetItemsForTable", method = RequestMethod.GET)
	public @ResponseBody Map funFillItemForSelectedTable(@RequestParam("bussyTableNo") String bussyTableNo ,HttpServletRequest req) throws Exception
	{
		Map<String,Object>map=new HashMap<>();
		
		try
		{
			List itemList = new ArrayList();
			
			
			String posCode = req.getSession().getAttribute("gPOSCode").toString();
			StringBuilder sqlBuilderTableItemDtl = new StringBuilder();
			
			sqlBuilderTableItemDtl.setLength(0);
			sqlBuilderTableItemDtl.append("SELECT a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), SUM(a.dblAmount),a.strWaiterNo "
			+",c.strSubGroupCode,c.strSubGroupName,d.strGroupCode,d.strGroupName " 
			+"FROM tblitemrtemp a,tblitemmaster b,tblsubgrouphd c,tblgrouphd d " 
			+"WHERE left(a.strItemCode,7)=b.strItemCode " 
			+"and b.strSubGroupCode=c.strSubGroupCode " 
			+"and c.strGroupCode=d.strGroupCode " 
			+"and strTableNo='"+bussyTableNo+"'  " 
			+"AND (strPosCode='"+posCode+"' OR strPosCode='All')  " 
			+"AND strNcKotYN='N' " 
			+"GROUP BY a.strItemCode " 
			+"ORDER BY a.strItemCode ");
			
			 List listBussyTableItems = objBaseService.funGetList(sqlBuilderTableItemDtl,"sql");
			 if(listBussyTableItems.size()>0)
			 {
				 for(int i=0;i<listBussyTableItems.size();i++)
				 {
					 Object[] obj = (Object[])listBussyTableItems.get(i);
					 
					 clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
					 
					 objBean.setStrItemCode(obj[0].toString());//item code
					 objBean.setStrItemName(obj[1].toString());//item name
					 objBean.setDblRate(Double.parseDouble(obj[2].toString()));//rate
					 objBean.setDblQuantity(Double.parseDouble(obj[3].toString()));//item quantity
					 objBean.setDblAmount(Double.parseDouble(obj[4].toString()));//amount					 
					 objBean.setStrWaiterCode(obj[5].toString()); //waiter number
					 objBean.setStrSubGroupCode(obj[6].toString()); 
					 objBean.setStrSubGroupName(obj[7].toString());
					 objBean.setStrGroupCode(obj[8].toString()); 
					 objBean.setStrGroupName(obj[9].toString());
					 
					 
					 itemList.add(objBean);
				 }
			 }
			 
			 map.put("itemList", itemList);
			 
			 
			 
			 List kotWiseItemList = new ArrayList();
							 
			 
			 sqlBuilderTableItemDtl.setLength(0);
				sqlBuilderTableItemDtl.append("SELECT a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), SUM(a.dblAmount),a.strWaiterNo "
				+",c.strSubGroupCode,c.strSubGroupName,d.strGroupCode,d.strGroupName,a.strKOTNo " 
				+"FROM tblitemrtemp a,tblitemmaster b,tblsubgrouphd c,tblgrouphd d " 
				+"WHERE left(a.strItemCode,7)=b.strItemCode " 
				+"and b.strSubGroupCode=c.strSubGroupCode " 
				+"and c.strGroupCode=d.strGroupCode " 
				+"and strTableNo='"+bussyTableNo+"'  " 
				+"AND (strPosCode='"+posCode+"' OR strPosCode='All')  " 
				+"AND strNcKotYN='N' " 
				+"GROUP BY a.strKOTNo,a.strItemCode " 
				+"ORDER BY a.strItemCode ");
				  listBussyTableItems = objBaseService.funGetList(sqlBuilderTableItemDtl,"sql");
				 if(listBussyTableItems.size()>0)
				 {
					 for(int i=0;i<listBussyTableItems.size();i++)
					 {
						 Object[] obj = (Object[])listBussyTableItems.get(i);
						 
						 clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						 
						 objBean.setStrItemCode(obj[0].toString());//item code
						 objBean.setStrItemName(obj[1].toString());//item name
						 objBean.setDblRate(Double.parseDouble(obj[2].toString()));//rate
						 objBean.setDblQuantity(Double.parseDouble(obj[3].toString()));//item quantity
						 objBean.setDblAmount(Double.parseDouble(obj[4].toString()));//amount					 
						 objBean.setStrWaiterCode(obj[5].toString()); //waiter number
						 objBean.setStrSubGroupCode(obj[6].toString()); 
						 objBean.setStrSubGroupName(obj[7].toString());
						 objBean.setStrGroupCode(obj[8].toString()); 
						 objBean.setStrGroupName(obj[9].toString());						 
						 objBean.setStrKOTNo(obj[10].toString());
						 
						 
						 kotWiseItemList.add(objBean);
					 }
				 }
				 
				 map.put("kotWiseItemList", kotWiseItemList);
			 
			 
			 
			 
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return map;
		}		
	}

	public List funLoadResonCode()
	{

		List listResonCombo = new ArrayList();

		try
		{

			Map mapObj = funLoadReson();
			List list = (List) mapObj.get("jArr");
			for (int i = 0; i < list.size(); i++)
			{
				Map jObjtemp = (Map) list.get(i);
				listResonCombo.add(jObjtemp.get("resoncode").toString());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return listResonCombo;
	}

	public Map funLoadReson()
	{
		Map objReturn = new HashMap();
		List jarr = new ArrayList();
		String favoritereason = null;
		try
		{

			String[] arrReason;
			int reasoncount = 0, i = 0;
			String resonCode = "";
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select count(strReasonName) from tblreasonmaster where strKot='Y'");
			List listSql = objBaseService.funGetList(sqlBuilder, "sql");
			if (listSql.size() > 0)
			{
				for (int cnt = 0; cnt < listSql.size(); cnt++)
				{

					Object obj = (Object) listSql.get(cnt);
					reasoncount = Integer.parseInt(obj.toString());
				}
			}
			if (reasoncount > 0)
			{
				arrReason = new String[reasoncount];

				sqlBuilder.setLength(0);
				sqlBuilder.append("select strReasonName from tblreasonmaster where strKot='Y'");

				List listSqlReason = objBaseService.funGetList(sqlBuilder, "sql");
				if (listSqlReason.size() > 0)
				{
					for (int j = 0; j < listSqlReason.size(); j++)
					{
						Map jobj = new HashMap();
						Object obj = (Object) listSqlReason.get(j);
						i = 0;
						jobj.put("resoncode", obj.toString());
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
	
	@RequestMapping(value = "/actionForBillForItems", method = RequestMethod.GET)
	public ModelAndView printBill(@ModelAttribute("command") clsPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest request) throws Exception
	{			
		try
		{
			
			
			String clientCode = "",
					POSCode = "",
					POSDate = "",
					userCode = "",
					posClientCode = "";
			int totalPAXNo = objBean.getIntPaxNo();
			String tableNo = objBean.getStrTableNo();

			clientCode = request.getSession().getAttribute("gClientCode").toString();
			POSCode = request.getSession().getAttribute("gPOSCode").toString();
			POSDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			userCode = request.getSession().getAttribute("gUserCode").toString();

			String split = POSDate;
			String billDateTime = split;
			String custCode = "";

			Date dt = new Date();
			String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			String dateTime = POSDate + " " + currentDateTime.split(" ")[1];

			boolean isBillSeries = false;
			if (clsPOSGlobalFunctionsController.hmPOSSetupValues.get("strEnableBillSeries").toString().equalsIgnoreCase("Y"))
			{
				isBillSeries = true;
			}

			/**
			 * Filling KOT item details in a list
			 */
			List<clsPOSKOTItemDtl> listOfWholeKOTItemDtl = new ArrayList<clsPOSKOTItemDtl>();

			StringBuilder sbSql = new StringBuilder();
			sbSql.setLength(0);
			sbSql.append("select strItemCode,upper(strItemName),dblItemQuantity " + " ,dblAmount,strKOTNo,strManualKOTNo,concat(Time(dteDateCreated),''),strCustomerCode " + " ,strCustomerName,strCounterCode,strWaiterNo,strPromoCode,dblRate,strCardNo,concat(tmeOrderProcessing,''),concat(tmeOrderPickup,'') " + ",strWaiterNo " + " from tblitemrtemp " + " where strPosCode='" + POSCode + "' " + " and strTableNo='" + tableNo + "' " + " and strNCKotYN='N' " + " order by strTableNo ASC");
			List listItemKOTDtl = objBaseServiceImpl.funGetList(sbSql, "sql");
			if (listItemKOTDtl.size() > 0)
			{
				for (int i = 0; i < listItemKOTDtl.size(); i++)
				{
					Object[] arrKOTItem = (Object[]) listItemKOTDtl.get(i);

					String iCode = arrKOTItem[0].toString();
					String iName = arrKOTItem[1].toString();
					double iQty = new Double(arrKOTItem[2].toString());
					String iAmt = arrKOTItem[3].toString();

					double rate = Double.parseDouble(arrKOTItem[12].toString());
					String kotNo = arrKOTItem[4].toString();
					String manualKOTNo = arrKOTItem[5].toString();
					billDateTime = split + " " + arrKOTItem[6].toString();
					custCode = arrKOTItem[7].toString();
					String custName = arrKOTItem[8].toString();
					String promoCode = arrKOTItem[11].toString();
					String cardNo = arrKOTItem[13].toString();
					String orderProcessTime = arrKOTItem[14].toString();
					String orderPickupTime = arrKOTItem[15].toString();

					String waiterNo = arrKOTItem[16].toString();

					clsPOSKOTItemDtl objKOTItem = new clsPOSKOTItemDtl();

					objKOTItem.setStrItemCode(iCode);
					objKOTItem.setStrItemName(iName);
					objKOTItem.setDblItemQuantity(iQty);
					objKOTItem.setDblAmount(Double.parseDouble(iAmt));
					objKOTItem.setDblRate(rate);
					objKOTItem.setStrKOTNo(kotNo);
					objKOTItem.setStrManualKOTNo(manualKOTNo);
					objKOTItem.setStrKOTDateTime(billDateTime);
					objKOTItem.setStrCustomerCode(custCode);
					objKOTItem.setStrCustomerName(custName);
					objKOTItem.setStrPromoCode(promoCode);
					objKOTItem.setStrCardNo(cardNo);
					objKOTItem.setStrOrderProcessTime(orderProcessTime);
					objKOTItem.setStrOrderPickupTime(orderPickupTime);
					objKOTItem.setStrWaiterNo(waiterNo);

					listOfWholeKOTItemDtl.add(objKOTItem);
				}
			}
			
			
			/**
			 * Filling  item details in a list to be billed
			 */
			List<clsPOSKOTItemDtl> listOfItemsToBeBilled = new ArrayList<clsPOSKOTItemDtl>();
			List<clsPOSItemsDtlsInBill>listOfItemsToBeProcess=objBean.getListOfBillItemDtl();
			
			
			for(clsPOSItemsDtlsInBill objItemToBeBill:listOfItemsToBeProcess)
			{
				String itemCode=objItemToBeBill.getItemCode();
				String itemName=objItemToBeBill.getItemName();
				double qty=objItemToBeBill.getQuantity();
				
				//
				for(clsPOSKOTItemDtl KOTItemDtl:listOfWholeKOTItemDtl)
				{
					
					clsPOSKOTItemDtl objKOTItem=KOTItemDtl.clone();
					
					String kotItemCode=objKOTItem.getStrItemCode();
					String kotItemName=objKOTItem.getStrItemName();
					double kotItemQty=objKOTItem.getDblItemQuantity();
					
					if(itemCode.equalsIgnoreCase(kotItemCode))
					{
						if(qty==kotItemQty)
						{
							listOfItemsToBeBilled.add(objKOTItem);
							
							KOTItemDtl.setDblItemQuantity(0);
							KOTItemDtl.setDblAmount(KOTItemDtl.getDblRate()*KOTItemDtl.getDblItemQuantity());
						}
						else if(qty<kotItemQty)
						{																				
							objKOTItem.setDblItemQuantity(qty);
							objKOTItem.setDblAmount(objKOTItem.getDblRate()*qty);
							
							listOfItemsToBeBilled.add(objKOTItem);
							
							double calQty=(kotItemQty-qty);
							KOTItemDtl.setDblItemQuantity(calQty);
							KOTItemDtl.setDblAmount(KOTItemDtl.getDblRate()*KOTItemDtl.getDblItemQuantity());
						}
						else if(qty>kotItemQty)
						{
							listOfItemsToBeBilled.add(objKOTItem);
							
							KOTItemDtl.setDblItemQuantity(0);
							KOTItemDtl.setDblAmount(KOTItemDtl.getDblRate()*KOTItemDtl.getDblItemQuantity());
						}
							
					}
				}
			}
			
			
			
			
			

			/**
			 * Bill series code
			 */

			Map<String, List<clsPOSKOTItemDtl>> mapBillSeries = null;
			List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl = new ArrayList<clsPOSBillSeriesBillDtl>();

			if (isBillSeries)
			{
				if ((mapBillSeries = objBillingAPI.funGetBillSeriesList(listOfItemsToBeBilled, POSCode)).size() > 0)
				{
					if (mapBillSeries.containsKey("NoBillSeries"))
					{
						result.addError(new ObjectError("BillSeries", "Please Create Bill Series"));
						
						return new ModelAndView("/frmWebPOSBilling.html");
					}
					// to calculate PAX per bill if there is a bill series or bill splited
					Map<Integer, Integer> mapPAXPerBill = objBillingAPI.funGetPAXPerBill(totalPAXNo, mapBillSeries.size());

					Iterator<Map.Entry<String, List<clsPOSKOTItemDtl>>> billSeriesIt = mapBillSeries.entrySet().iterator();
					int billCount = 0;
					while (billSeriesIt.hasNext())
					{
						Map.Entry<String, List<clsPOSKOTItemDtl>> billSeriesEntry = billSeriesIt.next();
						String key = billSeriesEntry.getKey();
						List<clsPOSKOTItemDtl> listOfItemsBillSeriesWise = billSeriesEntry.getValue();

						int intBillSeriesPaxNo = 0;
						if (mapPAXPerBill.containsKey(billCount))
						{
							intBillSeriesPaxNo = mapPAXPerBill.get(billCount);
						}

						String billSeriesBillNo = objBillingAPI.funGetBillSeriesBillNo(key, POSCode);

						/* To save billseries bill */
						objBillingAPI.funSaveBill(isBillSeries, key, listBillSeriesBillDtl, billSeriesBillNo, listOfItemsBillSeriesWise, objBean, request,hmPromoItem);

						billCount++;
					}				

					// save bill series bill detail
					for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
					{
						clsPOSBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
						String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
						double grandTotal = objBillSeriesBillDtl.getDblGrandTotal();

						String sqlInsertBillSeriesDtl = "insert into tblbillseriesbilldtl " + "(strPOSCode,strBillSeries,strHdBillNo,strDtlBillNos,dblGrandTotal,strClientCode,strDataPostFlag" + ",strUserCreated,dteCreatedDate,strUserEdited,dteEditedDate,dteBillDate) " + "values ('" + POSCode + "','" + objBillSeriesBillDtl.getStrBillSeries() + "'" + ",'" + hdBillNo + "','" + objBillingAPI.funGetBillSeriesDtlBillNos(listBillSeriesBillDtl, hdBillNo) + "'" + ",'" + grandTotal + "'" + ",'" + clientCode + "','N','" + userCode + "'" + ",'" + currentDateTime + "','" + userCode + "'" + ",'" + currentDateTime + "','" + POSDate + "')";
						objBaseService.funExecuteUpdate(sqlInsertBillSeriesDtl, "sql");

						sbSql.setLength(0);
						sbSql.append("select * " + "from tblbillcomplementrydtl a " + "where a.strBillNo='" + hdBillNo + "' " + "and date(a.dteBillDate)='" + POSDate + "' " + "and a.strType='Complimentary' ");
						List listCompli = objBaseServiceImpl.funGetList(sbSql, "sql");
						if (listCompli != null && listCompli.size() > 0)
						{
							String sqlUpdate = "update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + hdBillNo + "' " + " and strPOSCode='" + POSCode + "' " + " and date(dteBillDate)='" + POSDate + "' ";
							objBaseService.funExecuteUpdate(sqlUpdate, "sql");
						}

					}
					
					
					boolean flagBillForItems = true;
					// clear temp kot table
					if (flagBillForItems)
					{
						 
						for(clsPOSKOTItemDtl KOTItemDtl:listOfWholeKOTItemDtl)
						{
							String kotItemCode=KOTItemDtl.getStrItemCode();
							String kotItemName=KOTItemDtl.getStrItemName();
							double kotItemQty=KOTItemDtl.getDblItemQuantity();
							
							if(kotItemQty<=0)
							{
								String sqlUpdate = "delete from tblitemrtemp  "
									    + "where strItemCode='" + kotItemCode + "' "
									    + "and strTableNo='" + tableNo + "' "
									    + "and strNCKotYN='N' "
									    + "and strPOSCode='" + POSCode + "' ";
								objBaseService.funExecuteUpdate(sqlUpdate, "sql");
							}
							else
							{
								String sqlUpdate = "update tblitemrtemp  " 
										+"set dblItemQuantity='"+kotItemQty+"' " 
										+",dblAmount=(dblRate*"+kotItemQty+") " 
										+"where strTableNo='"+tableNo+"' "
										+"and strItemCode='" + kotItemCode + "' " 
										+"and strNCKotYN='N' " 
										+"and strPOSCode='"+POSCode+"' ";
								objBaseService.funExecuteUpdate(sqlUpdate, "sql");
							}
						}
												
					}
					else
					{
						objBillingAPI.funClearRTempTable(tableNo, POSCode);
					}

					for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
					{
						clsPOSBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
						String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
						boolean flgHomeDelPrint = objBillSeriesBillDtl.isFlgHomeDelPrint();

						/* printing bill............... */
						objTextFileGeneration.funGenerateAndPrintBill(hdBillNo, POSCode, clientCode);
					}

				}
			}
			else // No Bill Series
			{
				String voucherNo = objBillingAPI.funGenerateBillNo(POSCode);

				/* To save normal bill */
				objBillingAPI.funSaveBill(isBillSeries, "", listBillSeriesBillDtl, voucherNo, listOfItemsToBeBilled, objBean, request,hmPromoItem);
				
				boolean flagBillForItems = true;
				// clear temp kot table
				if (flagBillForItems)
				{
					for(clsPOSKOTItemDtl KOTItemDtl:listOfWholeKOTItemDtl)
					{
						String kotItemCode=KOTItemDtl.getStrItemCode();
						String kotItemName=KOTItemDtl.getStrItemName();
						double kotItemQty=KOTItemDtl.getDblItemQuantity();
						
						if(kotItemQty<=0)
						{
							String sqlUpdate = "delete from tblitemrtemp  "
								    + "where strItemCode='" + kotItemCode + "' "
								    + "and strTableNo='" + tableNo + "' "
								    + "and strNCKotYN='N' "
								    + "and strPOSCode='" + POSCode + "' ";
							objBaseService.funExecuteUpdate(sqlUpdate, "sql");
						}
						else
						{
							String sqlUpdate = "update tblitemrtemp  " 
									+"set dblItemQuantity='"+kotItemQty+"' " 
									+",dblAmount=(dblRate*"+kotItemQty+") " 
									+"where strTableNo='"+tableNo+"' "
									+"and strItemCode='" + kotItemCode + "' " 
									+"and strNCKotYN='N' " 
									+"and strPOSCode='"+POSCode+"'";
							objBaseService.funExecuteUpdate(sqlUpdate, "sql");
						}
					}
				}
				else
				{
					objBillingAPI.funClearRTempTable(tableNo, POSCode);
				}

				/* printing bill............... */
				objTextFileGeneration.funGenerateAndPrintBill(voucherNo, POSCode, clientCode);
			}
			
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return new ModelAndView("redirect:/frmBillForItems.html?saddr=1");
		}										
	}

}
