package com.sanguine.webpos.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSeriesBillDtl;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPOSKOTItemDtl;
import com.sanguine.webpos.bean.clsPOSPromotionItems;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillHdModel_ID;

@Controller
public class clsPOSModifyBillController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;

	@Autowired
	private clsSetupService objSetupService;

	@Autowired
	private intfBaseService objBaseService;
	
	@Autowired
	clsPOSBillingAPIController objBillingAPI;
	
	
	

	private StringBuilder sql = new StringBuilder();
	private Map<String, clsPOSPromotionItems> hmPromoItem = new HashMap<String, clsPOSPromotionItems>();

	
	
	
	
	
	
	
	
	
	
	

	@RequestMapping(value = "/frmPOSModifyBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "1";
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

			String operationFrom = "Modify Bill";
			model.put("operationFrom", operationFrom);
			model.put("operationName", operationFrom);
			model.put("operationType", operationFrom);

		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}

		/* Filling model attribute values */
		model.put("urlHits", urlHits);
		String formToBeOpen = "Modify Bill";
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

	/* fetch all items and modifiers from a bill */

	@SuppressWarnings("finally")
	@RequestMapping(value = "/funGetItemsFromBill", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> funGetItemsFromBill(HttpServletRequest request, @RequestParam("billNo") String billNo)
	{
		List<clsPOSBillDtl> listOfBillItemDetails = new ArrayList<>();
		String operationType="DineIn";
		
		Map<String,Object>map=new HashMap<>();
		
		try
		{
			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			String posClientCode = request.getSession().getAttribute("gPOSCode").toString();
			String posCode = request.getSession().getAttribute("gPOSCode").toString();
			String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			String userCode = request.getSession().getAttribute("gUserCode").toString();
			
			
			sql.setLength(0);
			sql.append("select c.strItemCode,c.strItemName,c.dblRate,c.dblQuantity,c.dblAmount,c.dblTaxAmount,c.dblDiscAmt,c.isModifier,c.strSequenceNo " + ",d.strSubGroupCode,e.strSubGroupName,e.strGroupCode,f.strGroupName " + "from " + "(select a.strItemCode,a.strItemName,a.dblRate,sum(a.dblQuantity)dblQuantity,sum(a.dblAmount)dblAmount,sum(a.dblTaxAmount)dblTaxAmount,sum(a.dblDiscountAmt)dblDiscAmt,'false' isModifier,a.strSequenceNo strSequenceNo " + "from tblbilldtl a " + "where a.strBillNo='" + billNo + "' " + "group by a.strItemCode " + " " + "union all " + " " + "select b.strItemCode,b.strModifierName,b.dblRate,sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount,0.00 dblTaxAmount,sum(b.dblDiscAmt)dblDiscAmt,'true' isModifier,b.strSequenceNo strSequenceNo " + "from tblbillmodifierdtl b " + "where b.strBillNo='" + billNo + "' " + "group by b.strItemCode) c " + ",tblitemmaster d,tblsubgrouphd e,tblgrouphd f " + "where left(c.strItemCode,7)=d.strItemCode " + "and d.strSubGroupCode=e.strSubGroupCode " + "and e.strGroupCode=f.strGroupCode " + "order by c.strItemCode,c.strItemName ");
			List listPendBillData = objBaseService.funGetList(sql, "sql");
			if (listPendBillData != null && listPendBillData.size() > 0)
			{
				for (int i = 0; i < listPendBillData.size(); i++)
				{
					Object[] arrObj = (Object[]) listPendBillData.get(i);

					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrItemCode(arrObj[0].toString());
					objItemDtl.setStrItemName(arrObj[1].toString());

					objItemDtl.setDblRate(Double.parseDouble(arrObj[2].toString()));
					objItemDtl.setDblQuantity(Double.parseDouble(arrObj[3].toString()));
					objItemDtl.setDblAmount(Double.parseDouble(arrObj[4].toString()));
					objItemDtl.setDblTaxAmount(Double.parseDouble(arrObj[5].toString()));
					objItemDtl.setDblDiscountAmt(Double.parseDouble(arrObj[6].toString()));
					objItemDtl.setModifier(Boolean.parseBoolean(arrObj[7].toString()));
					objItemDtl.setSequenceNo(arrObj[8].toString());
					objItemDtl.setStrSubGroupCode(arrObj[9].toString());
					objItemDtl.setStrSubGroupName(arrObj[10].toString());
					objItemDtl.setStrGroupCode(arrObj[11].toString());
					objItemDtl.setStrGroupName(arrObj[12].toString());

					listOfBillItemDetails.add(objItemDtl);
				}
			}
			
			sql.setLength(0);
			sql.append("select a.strOperationType "
					+"from tblbillhd a "
					+"where a.strBillNo='"+billNo+"' "
					+"and date(a.dteBillDate)='"+posDate+"' "
					+"and a.strPOSCode='"+posCode+"'");
			List listOperationType = objBaseService.funGetList(sql, "sql");
			if (listOperationType != null && listOperationType.size() > 0)
			{
				operationType=listOperationType.get(0).toString();
			}
		}
		finally
		{
			map.put("listOfBillItemDetails", listOfBillItemDetails);
			map.put("operationType", operationType);
			
			return map;
		}
	}

	@SuppressWarnings("finally")
	@RequestMapping(value = "/actionModifyBill", method = RequestMethod.POST)
	public ModelAndView printBill(Map<String, Object> model,@ModelAttribute("command") clsPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest request) throws Exception
	{
		try
		{
			String clientCode = "",
					POSCode = "",
							posDate = "",
					userCode = "",
					posClientCode = "";

			clientCode = request.getSession().getAttribute("gClientCode").toString();
			POSCode = request.getSession().getAttribute("gPOSCode").toString();
			posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			userCode = request.getSession().getAttribute("gUserCode").toString();

			String split = posDate;
			String billDateTime = split;
			

			Date dt = new Date();
			String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			String dateTime = posDate + " " + currentDateTime.split(" ")[1];

			StringBuilder sbSql = new StringBuilder();
			sbSql.setLength(0);

			String billNo=objBean.getStrBillNo();
			
			
			int totalPAXNo = 0;
			String tableNo = "";
		

			boolean isBillSeries = false;
			if (clsPOSGlobalFunctionsController.hmPOSSetupValues.get("strEnableBillSeries").toString().equalsIgnoreCase("Y"))
			{
				isBillSeries = true;
			}

			List<clsPOSBillSeriesBillDtl> listBillSeriesBillDtl = new ArrayList<clsPOSBillSeriesBillDtl>();
			
			/**
			 * Filling  item details in a list
			 */
			List<clsPOSKOTItemDtl> listOfWholeKOTItemDtl = new ArrayList<clsPOSKOTItemDtl>();
			
			
			/*Setting billdtl data*/
			sbSql.setLength(0);
			sbSql.append("select * "
						+"from tblbilldtl a "
						+"where a.strBillNo='"+billNo+"' ");
			List listItemKOTDtl = objBaseService.funGetList(sbSql, "sql");
			if (listItemKOTDtl.size() > 0)
			{
				for (int i = 0; i < listItemKOTDtl.size(); i++)
				{
					Object[] arrKOTItem = (Object[]) listItemKOTDtl.get(i);

					String iCode = arrKOTItem[0].toString();
					String iName = arrKOTItem[1].toString();
					String iBillNo = arrKOTItem[2].toString();
					String iAdvBookingNo = arrKOTItem[3].toString();
					double iRate = new Double(arrKOTItem[4].toString());
					double iQty = new Double(arrKOTItem[5].toString());
					double iAmt = new Double(arrKOTItem[6].toString());
					double iTaxAmt = new Double(arrKOTItem[7].toString());
					String iBillDateTime = arrKOTItem[8].toString();					
					String kotNo = arrKOTItem[9].toString();
					arrKOTItem[10].toString();//clientCode
					String iCustCode="";
					if(arrKOTItem[11]!=null)//customerCode
					{
						iCustCode=arrKOTItem[11].toString();
					}
					String orderProcessTime = "00:00:00";
					if(arrKOTItem[12]!=null)//
					{
						orderProcessTime=arrKOTItem[12].toString();
					}
					arrKOTItem[13].toString();//dataPostFlag
					arrKOTItem[14].toString();//MMS dataPostFlag					
					String manualKOTNo = arrKOTItem[15].toString();
					String iTDHYN = arrKOTItem[16].toString();
					String iPromoCode = arrKOTItem[17].toString();
					String iCounterCode = arrKOTItem[18].toString();
					String iWaiterNo = arrKOTItem[19].toString();
					double iDiscAmt = new Double(arrKOTItem[20].toString());
					double iDiscPer = new Double(arrKOTItem[21].toString());
					String iSeqNo = arrKOTItem[22].toString();
					String iBillDate = arrKOTItem[23].toString();					
					String orderPickupTime = "00:00:00";
					if(arrKOTItem[24]!=null)//
					{
						orderPickupTime=arrKOTItem[24].toString();
					}

					

					clsPOSKOTItemDtl objKOTItem = new clsPOSKOTItemDtl();

					objKOTItem.setStrItemCode(iCode);
					objKOTItem.setStrItemName(iName);
					objKOTItem.setDblItemQuantity(iQty);
					objKOTItem.setDblAmount(iAmt);
					objKOTItem.setDblRate(iRate);
					objKOTItem.setStrKOTNo(kotNo);
					objKOTItem.setStrManualKOTNo(manualKOTNo);
					objKOTItem.setStrKOTDateTime(billDateTime);
					objKOTItem.setStrCustomerCode(iCustCode);
					objKOTItem.setStrCustomerName("");
					objKOTItem.setStrPromoCode(iPromoCode);
					objKOTItem.setStrCardNo("");
					objKOTItem.setStrOrderProcessTime(orderProcessTime);
					objKOTItem.setStrOrderPickupTime(orderPickupTime);
					objKOTItem.setStrWaiterNo(iWaiterNo);

					listOfWholeKOTItemDtl.add(objKOTItem);
				}
			}
			
			/*Setting billmodifierdtl data*/
			sbSql.setLength(0);
			sbSql.append("select * "
						+"from tblbillmodifierdtl a "
						+"where a.strBillNo='"+billNo+"' ");
			listItemKOTDtl = objBaseService.funGetList(sbSql, "sql");
			if (listItemKOTDtl.size() > 0)
			{
				for (int i = 0; i < listItemKOTDtl.size(); i++)
				{
					Object[] arrKOTItem = (Object[]) listItemKOTDtl.get(i);

					String iBillNo = arrKOTItem[0].toString();
					String iCode = arrKOTItem[1].toString();
					String iModiCode = arrKOTItem[2].toString();
					String iName = arrKOTItem[3].toString();					
					String iAdvBookingNo = "";
					double iRate = new Double(arrKOTItem[4].toString());
					double iQty = new Double(arrKOTItem[5].toString());
					double iAmt = new Double(arrKOTItem[6].toString());
					double iTaxAmt = 0.00;
					arrKOTItem[7].toString();//clientCode
					String iCustCode=arrKOTItem[8].toString();//customerCode
					arrKOTItem[9].toString();//dataPostFlag
					arrKOTItem[10].toString();//MMS dataPostFlag	
					String defaultModifierSelectedYN = arrKOTItem[11].toString();
					String iSeqNo = arrKOTItem[12].toString();
					double iDiscPer = new Double(arrKOTItem[13].toString());
					double iDiscAmt = new Double(arrKOTItem[14].toString());
					String iBillDate = arrKOTItem[15].toString();
					
					String iBillDateTime = "";					
					String kotNo = "";										
					String orderProcessTime = "";
					String manualKOTNo = "";
					String iTDHYN = "";
					String iPromoCode = "";
					String iCounterCode = "";
					String iWaiterNo = "";
					String orderPickupTime = "";

					

					clsPOSKOTItemDtl objKOTItem = new clsPOSKOTItemDtl();

					objKOTItem.setStrItemCode(iCode);
					objKOTItem.setStrItemName(iName);
					objKOTItem.setDblItemQuantity(iQty);
					objKOTItem.setDblAmount(iAmt);
					objKOTItem.setDblRate(iRate);
					objKOTItem.setStrKOTNo(kotNo);
					objKOTItem.setStrManualKOTNo(manualKOTNo);
					objKOTItem.setStrKOTDateTime(billDateTime);
					objKOTItem.setStrCustomerCode(iCustCode);
					objKOTItem.setStrCustomerName("");
					objKOTItem.setStrPromoCode(iPromoCode);
					objKOTItem.setStrCardNo("");
					objKOTItem.setStrOrderProcessTime(orderProcessTime);
					objKOTItem.setStrOrderPickupTime(orderPickupTime);
					objKOTItem.setStrWaiterNo(iWaiterNo);

					listOfWholeKOTItemDtl.add(objKOTItem);
				}
			}
			
			
			if (isBillSeries)
			{
				/* To save normal bill */
				objBillingAPI.funSaveBill(isBillSeries, "", listBillSeriesBillDtl, billNo, listOfWholeKOTItemDtl, objBean, request, hmPromoItem);
			}
			else
			{
				/* To save normal bill */
				objBillingAPI.funSaveBill(isBillSeries, "", listBillSeriesBillDtl, billNo, listOfWholeKOTItemDtl, objBean, request, hmPromoItem);
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return new ModelAndView("redirect:/frmPOSModifyBill.html");
		}
	}

}
