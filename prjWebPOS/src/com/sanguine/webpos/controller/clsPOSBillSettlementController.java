package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSettlementBean;
import com.sanguine.webpos.bean.clsPOSSettelementOptions;
import com.sanguine.webpos.bean.clsPOSSettlementDtlsOnBill;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillSettlementDtlModel;

@Controller
public class clsPOSBillSettlementController
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
	clsBaseServiceImpl objBaseServiceImpl;
	
	
	@Autowired
	clsPOSBillingAPIController objBillingAPI;

	@RequestMapping(value = "/frmPOSRestaurantDtl", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String urlHits = "1";
		clsPOSBillSettlementBean objBillSettlementBean = new clsPOSBillSettlementBean();

		try
		{

			String clientCode = request.getSession().getAttribute("gClientCode").toString();
			String  posClientCode = request.getSession().getAttribute("gPOSCode").toString();
			String  posCode = request.getSession().getAttribute("gPOSCode").toString();
			String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			String userCode = request.getSession().getAttribute("gUserCode").toString();
			
			urlHits = request.getParameter("saddr").toString();
			

			String usertype = request.getSession().getAttribute("gUserType").toString();
			boolean isSuperUser = false;
			if (usertype.equalsIgnoreCase("yes"))
			{
				isSuperUser = true;
			}
			else
			{
				isSuperUser = false;
			}
			
			
			
			

			/* Filling model attribute values */
			model.put("urlHits", urlHits);
			String formToBeOpen = "Settle Bill";
			model.put("formToBeOpen", formToBeOpen);

			

			List listSettlementObject = new ArrayList<clsPOSSettelementOptions>();

			JSONObject jObj1 = objBillingAPI.funSettlementMode(clientCode, posCode, isSuperUser);

			JSONArray jArr = new JSONArray();// (JSONArray)jObj.get("SettleDesc");
			Gson gson = new Gson();
			Type objectType = new TypeToken<clsPOSSettelementOptions>()
			{
			}.getType();
			JSONObject jsSettelementOptionsDtl = (JSONObject) jObj1.get("SettleObj");

			Type listType = new TypeToken<List<clsPOSSettelementOptions>>()
			{
			}.getType();
			listSettlementObject = gson.fromJson(jObj1.get("listSettleObj").toString(), listType);

			JSONObject jsSettle = new JSONObject();
			for (int j = 0; j < listSettlementObject.size(); j++)
			{
				// if(jArr.size()==listSettlementObject.size())
				// jsSettle.put(jArr.get(j).toString(),
				// listSettlementObject.get(j));
				jArr.add(listSettlementObject.get(j));
			}
			model.put("ObSettleObject", jsSettle);
			
			objBillSettlementBean.setJsonArrForSettleButtons(jArr);

			objBillSettlementBean.setDteExpiryDate(posDate);

		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		finally
		{
			if ("2".equalsIgnoreCase(urlHits))
			{
				return new ModelAndView("frmWebPOSBilling", "command", objBillSettlementBean);
			}
			else if ("1".equalsIgnoreCase(urlHits))
			{
				return new ModelAndView("frmWebPOSBilling", "command", objBillSettlementBean);
			}
			else
			{
				return null;
			}
		}
	}

	@RequestMapping(value = "/fillUnsettleBillData", method = RequestMethod.GET)
	public @ResponseBody Map funFillUnSettleBill(Map<String, Object> model, HttpServletRequest req)
	{
		List listUnsettlebill = new ArrayList();
		Map hmUnsettleBill = new HashMap();
		try
		{
			String clientCode = req.getSession().getAttribute("gClientCode").toString();
			String strPosCode = req.getSession().getAttribute("loginPOS").toString();
			String posDate = req.getSession().getAttribute("gPOSDate").toString();

			hmUnsettleBill = funFillUnsettleBill(clientCode, strPosCode, posDate);

			String gShowBillsType = hmUnsettleBill.get("gShowBillsType").toString();
			String gCMSIntegrationYN = hmUnsettleBill.get("gCMSIntegrationYN").toString();
			List listData = (List) hmUnsettleBill.get("jArr");

			for (int i = 0; i < listData.size(); i++)
			{
				LinkedList setFillGrid = new LinkedList();
				Map hmtemp = (Map) listData.get(i);
				if (gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
				{
					setFillGrid.add(hmtemp.get("strBillNo").toString());
					setFillGrid.add(hmtemp.get("strTableName").toString());
					setFillGrid.add(hmtemp.get("strWShortName").toString());
					setFillGrid.add(hmtemp.get("strCustomerName").toString());
					setFillGrid.add(hmtemp.get("dteBillDate").toString());
					setFillGrid.add(Double.parseDouble(hmtemp.get("dblGrandTotal").toString()));
					
					listUnsettlebill.add(setFillGrid);
				}
				else
				{
					setFillGrid.add(hmtemp.get("strBillNo").toString());
					setFillGrid.add(hmtemp.get("strTableName").toString());
					setFillGrid.add(hmtemp.get("strCustomerName").toString());
					setFillGrid.add(hmtemp.get("strBuildingName").toString());
					setFillGrid.add(hmtemp.get("strDPName").toString());
					setFillGrid.add(hmtemp.get("dteBillDate").toString());
					setFillGrid.add(Double.parseDouble(hmtemp.get("dblGrandTotal").toString()));
					
					listUnsettlebill.add(setFillGrid);

				}

				hmUnsettleBill.put("listUnsettlebill", listUnsettlebill);
				hmUnsettleBill.put("gShowBillsType", gShowBillsType);
				hmUnsettleBill.put("gCMSIntegrationYN", gCMSIntegrationYN);

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return hmUnsettleBill;
	}

	@RequestMapping(value = "/fillBillSettlementData", method = RequestMethod.GET)
	public ModelAndView funOpenBillSettlement(@ModelAttribute("command") clsPOSBillSettlementBean objBean, Map<String, Object> model, HttpServletRequest request)
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "2";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		model.put("urlHits", urlHits);

		String billNo = objBean.getStrBillNo();
		String selectedTableNo = objBean.getStrTableNo();
		String selectedRowIndex = objBean.getSelectedRow();
		String billType = "";

		String posUrl = clsPOSGlobalFunctionsController.POSWSURL + "/WebPOSBillSettlement/fillRowSelected";
		JSONObject objRows = new JSONObject();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String strPosCode = request.getSession().getAttribute("loginPOS").toString();
		JSONObject jobj = objPOSGlobalFunctionsController.funGetPOSDate(request);
		String posDate = jobj.get("POSDate").toString();
		String isSuperUser = request.getSession().getAttribute("superuser").toString();
		boolean superuser = true;
		if ("YES".equalsIgnoreCase(isSuperUser))
		{
			superuser = true;
		}

		request.setAttribute("billNo", billNo);

		objRows.put("billNo", billNo);
		objRows.put("selectedTableNo", selectedTableNo);
		objRows.put("selectedRowIndex", selectedRowIndex);
		objRows.put("clientCode", clientCode);
		objRows.put("posCode", strPosCode);
		objRows.put("billType", billType);
		objRows.put("superuser", superuser);

		JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posUrl, objRows);

		List listSettlemode = (List) jObj.get("jArrSettlementMode");
		model.put("listSettlemode", listSettlemode);

		// String path=request.getContextPath().toString();
		// try{
		//// String searchUrl="/fillBillSettlementData.html?";
		// res.sendRedirect("fillBillSettlementData.html?");
		//
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmBillSettlement_1", "command", new clsPOSBillSettlementBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmBillSettlement", "command", new clsPOSBillSettlementBean());
		}
		else
		{
			return null;
		}

	}

	public Map funFillUnsettleBill(String clientCode, String posCode, String posDate)
	{
		Map hmReturn = new HashMap();
		List listData = new ArrayList();
		StringBuilder sql = new StringBuilder();
		try
		{
			Map hmBillType = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
			String gShowBillsType = hmBillType.get("gShowBillsType").toString();
			Map hmCMSIntegrationYN = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
			String gCMSIntegrationYN = hmCMSIntegrationYN.get("gCMSIntegrationYN").toString();

			hmReturn.put("gShowBillsType", gShowBillsType);
			hmReturn.put("gCMSIntegrationYN", gCMSIntegrationYN);

			if (gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
			{
				sql.append("select a.strBillNo,ifnull(b.strTableNo,''),ifnull(b.strTableName,''),ifnull(c.strWaiterNo,'')" + " ,ifnull(c.strWShortName,''),ifnull(d.strCustomerCode,''),ifnull(d.strCustomerName,''),a.dblGrandTotal" + " ,DATE_FORMAT(a.dteBillDate,'%h:%i:%s')  " + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo" + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo" + " left outer join tblcustomermaster d on a.strCustomerCode=d.strCustomerCode" + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) " + " and date(a.dteBillDate)='" + posDate + "' " + " and a.strPOSCode='" + posCode + "' ");
			}
			else// Delivery Detail Wise
			{
				sql.append("SELECT a.strBillNo,IFNULL(d.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(f.strDPName,'')" + " ,a.dblGrandTotal,ifnull(g.strTableNo,''),ifnull(g.strTableName,''),DATE_FORMAT(a.dteBillDate,'%h:%i:%s') " + " FROM tblbillhd a " + " left outer join tblhomedeldtl b on a.strBillNo=b.strBillNo " + " LEFT OUTER JOIN tblcustomermaster d ON a.strCustomerCode=d.strCustomerCode " + " left outer join tblbuildingmaster e on d.strBuldingCode=e.strBuildingCode " + " left outer join tbldeliverypersonmaster  f on  f.strDPCode=b.strDPCode " + " left outer join tbltablemaster g on a.strTableNo=g.strTableNo " + " WHERE a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) " + " AND DATE(a.dteBillDate)='" + posDate + "' " + " AND a.strPOSCode='" + posCode + "' " + " group by a.strBillNo");
			}
			List listPendBillData = objBaseService.funGetList(sql, "sql");
			if (listPendBillData.size() > 0)
			{
				for (int i = 0; i < listPendBillData.size(); i++)
				{
					Object[] obj = (Object[]) listPendBillData.get(i);
					Map hmData = new HashMap();
					if (gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
					{
						hmData.put("strBillNo", obj[0].toString());
						hmData.put("strTableName", obj[2].toString());
						hmData.put("strWShortName", obj[4].toString());
						hmData.put("strCustomerName", obj[6].toString());
						hmData.put("dteBillDate", obj[8].toString());
						hmData.put("dblGrandTotal", obj[7].toString());
					}
					else// Delivery Detail Wise
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

			hmReturn.put("jArr", listData);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return hmReturn;
	}
	
	@SuppressWarnings("finally")
	@RequestMapping(value = "/actionBillSettle", method = RequestMethod.POST)
	public ModelAndView printBill(@ModelAttribute("command") clsPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest request) throws Exception
	{
		try
		{
			String clientCode = "",
					POSCode = "",
							posDate = "",
					userCode = "",
					posClientCode = "";
			
			StringBuilder hqlBuilder=new StringBuilder();

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
			
			hqlBuilder.setLength(0);
			hqlBuilder.append(" from clsBillHdModel where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"' and dtBillDate='"+posDate+"'  ");
			List<clsBillHdModel> listOfHd=objBaseService.funGetList(hqlBuilder, "hql");
			
			clsBillHdModel objBillHdModel=listOfHd.get(0);
			List<clsBillDtlModel>listBillDtlModel=objBillHdModel.getListBillDtlModel();
			
			objBillHdModel.setStrTransactionType(objBillHdModel.getStrTransactionType()+","+"Settle Bill");
			
			/* Save bill settlement data */
			List<clsPOSSettlementDtlsOnBill> listObjBillSettlementDtl = objBean.getListSettlementDtlOnBill();
			
			List<clsBillSettlementDtlModel> listBillSettlementDtlModel = new ArrayList<clsBillSettlementDtlModel>();

			for (clsPOSSettlementDtlsOnBill objBillSettlementDtl : listObjBillSettlementDtl)
			{
				clsBillSettlementDtlModel objSettleModel = new clsBillSettlementDtlModel();

				objSettleModel.setStrSettlementCode(objBillSettlementDtl.getStrSettelmentCode());
				objSettleModel.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt());
				objSettleModel.setDblPaidAmt(objBillSettlementDtl.getDblPaidAmt());
				objSettleModel.setStrExpiryDate("");
				objSettleModel.setStrCardName("");
				objSettleModel.setStrRemark("");

				objSettleModel.setStrCustomerCode("");
				objSettleModel.setDblActualAmt(objBillSettlementDtl.getDblActualAmt());
				objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt());
				objSettleModel.setStrGiftVoucherCode("");
				objSettleModel.setStrDataPostFlag("");

				objSettleModel.setStrFolioNo("");
				objSettleModel.setStrRoomNo("");

				listBillSettlementDtlModel.add(objSettleModel);

			}
			
			objBillHdModel.setStrSettelmentMode("");

			if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 0)
			{
				objBillHdModel.setStrSettelmentMode("");
			}
			else if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 1)
			{
				objBillHdModel.setStrSettelmentMode(listObjBillSettlementDtl.get(0).getStrSettelmentDesc());
			}
			else
			{
				objBillHdModel.setStrSettelmentMode("MultiSettle");
			}
			
			objBillHdModel.setListBillSettlementDtlModel(listBillSettlementDtlModel);
			
			/* Save Bill HD */
			objBaseServiceImpl.funSave(objBillHdModel);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return  new ModelAndView("redirect:/frmPOSRestaurantDtl.html?saddr=1");
		}
	}

}
