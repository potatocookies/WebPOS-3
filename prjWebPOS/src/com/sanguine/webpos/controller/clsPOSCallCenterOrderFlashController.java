package com.sanguine.webpos.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSOrderDtlBean;
import com.sanguine.webpos.bean.clsPOSOrderHDBean;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.model.clsOrderHdModel;

@Controller
public class clsPOSCallCenterOrderFlashController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private intfBaseService objBaseService;

	Map map = new HashMap();
	private HashMap<Object, Object> mapPOSName;

	@RequestMapping(value = "/frmCallCenterOrderFlash", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		model.put("urlHits", urlHits);

		// function to get all POS list
		JSONArray jPOSArr = objPOSGlobalFunctionsController.funGetAllPOSForMaster(strClientCode);
		mapPOSName = new HashMap<>();
		mapPOSName.put("All", "All");
		for (int cnt = 0; cnt < jPOSArr.size(); cnt++)
		{
			JSONObject jObjPOS = (JSONObject) jPOSArr.get(cnt);
			mapPOSName.put(jObjPOS.get("strPosCode").toString(), jObjPOS.get("strPosName").toString());
		}
		model.put("mapPOSName", mapPOSName);

		if ("2".equalsIgnoreCase(urlHits))
		{

			return new ModelAndView("frmCallCenterOrderFlash_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmCallCenterOrderFlash", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptCallCenterOrderFlash", method = RequestMethod.POST)
	private ModelAndView funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		String clientCode = req.getSession().getAttribute("clientCode").toString();

		String userCode = req.getSession().getAttribute("usercode").toString();

		String strPosCode = req.getSession().getAttribute("loginPOS").toString();

		String fromDate = objBean.getFromDate();

		String toDate = objBean.getToDate();

		String strReportType = objBean.getStrReportType();

		String posName = objBean.getStrPOSName();

		String strDateFilter = objBean.getDteDate();

		String strCustomerCode = objBean.getStrPSPCode();

		String operationType = objBean.getStrOperationType();

		String strOrderMode = objBean.getStrViewType();

		String strStatus = objBean.getStrViewBy();

		Map resMap = new LinkedHashMap();

		// resMap = FunGetData(clientCode, userCode, fromDate, toDate,
		// strDateFilter, strReportType, strCustomerCode, posName,
		// operationType, strOrderMode, strStatus, strPosCode);

		List ExportList = new ArrayList();

		String dteFromDate = objBean.getFromDate();
		String dteToDate = objBean.getToDate();
		String FileName = "AdvanceOrderFlash_" + dteFromDate + "_To_" + dteToDate;

		if (operationType.equals("Item wise"))
		{
			FileName = "AdvOrder_ItemWise";

		}
		else if (operationType.equals("Customer wise"))
		{
			FileName = "AdvOrder_CustomerWise";

		}
		else if (operationType.equals("Bill wise"))
		{
			FileName = "AdvOrder_BillWise";

		}
		else if (operationType.equals("Menu Head wise"))
		{
			FileName = "AdvOrder_MenuHeadWise";

		}
		else if (operationType.toString().equalsIgnoreCase("Group Wise"))
		{
			FileName = "AdvOrder_GroupWise";

		}
		ExportList.add(FileName);

		List List = (List) resMap.get("ColHeader");

		String[] headerList = new String[List.size()];
		for (int i = 0; i < List.size(); i++)
		{
			headerList[i] = (String) List.get(i);
		}

		ExportList.add(headerList);

		List dataList = (List) resMap.get("List");

		for (int i = 0; i < 2; i++)
		{
			List list = new ArrayList();
			for (int j = 0; i < List.size(); i++)
			{
				list.add(" ");
			}
			dataList.add(list);
		}

		List totalList = (List) resMap.get("totalList");
		dataList.add(totalList);

		ExportList.add(dataList);
		return new ModelAndView("excelViewWithReportName", "listWithReportName", ExportList);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@RequestMapping(value = "/funLoadCallCenterOrderFlash", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject FunLoadDaywiseSalesSummary1(HttpServletRequest req)
	{
		LinkedHashMap resMap = new LinkedHashMap();

		String clientCode = req.getSession().getAttribute("gClientCode").toString();

		String gPOSCode = req.getSession().getAttribute("gPOSCode").toString();

		String fromDate = req.getParameter("fromDate");

		String toDate = req.getParameter("toDate");

		String strPosCode = req.getParameter("posCode").toString();

		String reportType = req.getParameter("cmbType").toString();

		fromDate = objGlobalFunctions.funGetDate("yyyy-MM-dd", fromDate);
		toDate = objGlobalFunctions.funGetDate("yyyy-MM-dd", toDate);

		JSONObject objRootJsonObject = new JSONObject();
		if (reportType.equalsIgnoreCase("Summary"))
		{
			objRootJsonObject=funGetSummaryData(clientCode, fromDate, toDate, strPosCode);
		}
		else
		{
			objRootJsonObject=funGetDetailData(clientCode, fromDate, toDate, strPosCode);
		}

		return objRootJsonObject;
	}

	@SuppressWarnings(
	{ "unchecked" })
	private JSONObject funGetSummaryData(String strClientCode, String fromDate, String toDate, String strPOSCode)
	{

		// JSONObject objRootJsonObject =
		// objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL
		// + "/OnlineOrderIntegration/funGetCallCenterOrders?strClientCode=" +
		// clientCode + "&strPOSCode=" + strPosCode + "&dteFromDate=" + fromDate
		// + "&dteToDate=" + toDate);
		//
		// if (objRootJsonObject != null)
		// {
		//
		// }
		//
		// return objRootJsonObject;

		JSONObject rootJsonObject = new JSONObject();

		StringBuilder sqlBuilder = new StringBuilder();

		try
		{

			String posType = "HOPOS";

			String posCodeFromHO = "", posCodeFromOutlet = strPOSCode;
			String propertyPOSCode = strClientCode + "." + posCodeFromOutlet;

			if (posType.equalsIgnoreCase("HOPOS"))
			{
				posCodeFromHO = strPOSCode;
			}
			else
			{
				sqlBuilder.setLength(0);
				sqlBuilder.append("select a.strPosCode,a.strPosName,a.strPropertyPOSCode "
						+ "from tblposmaster a where a.strPropertyPOSCode='" + propertyPOSCode + "' ");
				List list = objBaseService.funGetList(sqlBuilder, "sql");
				if (list != null && list.size() > 0)
				{
					Object[] arrObj = (Object[]) list.get(0);

					posCodeFromHO = arrObj[0].toString();
				}
			}

			JSONArray columnsHeaderList = new JSONArray();
			columnsHeaderList.add("Order No");
			columnsHeaderList.add("Bill No");
			columnsHeaderList.add("Time");
			columnsHeaderList.add("Customer");
			columnsHeaderList.add("Cantact No");
			columnsHeaderList.add("Delivery Boy");
			columnsHeaderList.add("Shipping Address");

			rootJsonObject.put("coumnNames", columnsHeaderList);

			JSONArray listOfOrderHdData = new JSONArray();

			sqlBuilder.setLength(0);
			sqlBuilder.append("SELECT a.strOrderNo,a.strBillNo,CONCAT(LEFT(a.tmeTime,5),RIGHT(a.tmeTime,3))tmeTime,a.strCustomerName,a.strMobileNo "
					+ ",ifnull(b.strDPName,'')strDPName,a.strCustAddressLine1 "
					+ "FROM tblorderhd a "
					+ "left outer join tbldeliverypersonmaster b on a.strDPCode=b.strDPCode  "
					+ "where date(a.dteDate) between '" + fromDate + "' and  '" + toDate + "' ");
			if (!posCodeFromHO.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strPOSCode='" + posCodeFromHO + "' ");
			}
			List list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list != null && list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] arrObj = (Object[]) list.get(i);

					String orderNo = arrObj[0].toString();
					String billNo = arrObj[1].toString();
					String time = arrObj[2].toString();
					String customerName = arrObj[3].toString();
					String mobileNo = arrObj[4].toString();
					String strDPName = arrObj[5].toString();
					String shippingAddress = arrObj[6].toString();

					JSONObject objOrderHDBean = new JSONObject();

					objOrderHDBean.put("strOrderNo", orderNo);
					objOrderHDBean.put("strBillNo", billNo);
					objOrderHDBean.put("tmeTime", time);
					objOrderHDBean.put("strCustomerName", customerName);
					objOrderHDBean.put("strMobileNo", mobileNo);
					objOrderHDBean.put("strDPName", strDPName);
					objOrderHDBean.put("strCustAddressLine1", shippingAddress);
					
					

					listOfOrderHdData.add(objOrderHDBean);
				}

				rootJsonObject.put("listOfOrderHdData", listOfOrderHdData);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return rootJsonObject;
		}
	}

	@SuppressWarnings(
	{ "unchecked" })
	private JSONObject funGetDetailData(String strClientCode, String fromDate, String toDate, String strPOSCode)
	{

		// JSONObject objRootJsonObject =
		// objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL
		// + "/OnlineOrderIntegration/funGetCallCenterOrders?strClientCode=" +
		// clientCode + "&strPOSCode=" + strPosCode + "&dteFromDate=" + fromDate
		// + "&dteToDate=" + toDate);
		//
		// if (objRootJsonObject != null)
		// {
		//
		// }
		//
		// return objRootJsonObject;

		JSONObject rootJsonObject = new JSONObject();

		StringBuilder sqlBuilder = new StringBuilder();

		try
		{

			String posType = "HOPOS";

			String posCodeFromHO = "", posCodeFromOutlet = strPOSCode;
			String propertyPOSCode = strClientCode + "." + posCodeFromOutlet;

			if (posType.equalsIgnoreCase("HOPOS"))
			{
				posCodeFromHO = strPOSCode;
			}
			else
			{
				sqlBuilder.setLength(0);
				sqlBuilder.append("select a.strPosCode,a.strPosName,a.strPropertyPOSCode "
						+ "from tblposmaster a where a.strPropertyPOSCode='" + propertyPOSCode + "' ");
				List list = objBaseService.funGetList(sqlBuilder, "sql");
				if (list != null && list.size() > 0)
				{
					Object[] arrObj = (Object[]) list.get(0);

					posCodeFromHO = arrObj[0].toString();
				}
			}

			JSONArray columnsHeaderList = new JSONArray();
			columnsHeaderList.add("Item");
			columnsHeaderList.add("Rate");
			columnsHeaderList.add("Qty");
			columnsHeaderList.add("Amount");
			columnsHeaderList.add("");

			rootJsonObject.put("coumnNames", columnsHeaderList);

			JSONArray listOfOrderHdData = new JSONArray();

			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strOrderNo,b.strCustomerName,c.strPosName"
					+ ",DATE_FORMAT(a.dteDate,'%d-%m-%Y')dteDate,a.strCustAddressLine1  "
					+ "from tblorderhd a,tblcustomermaster b,tblposmaster c "
					+ "where a.strCustomerCode=b.strCustomerCode "
					+ "and a.strPOSCode=c.strPosCode "
					+ "and date(a.dteDate) between '" + fromDate + "' and  '" + toDate + "' ");
			if (!posCodeFromHO.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strPOSCode='" + posCodeFromHO + "' ");
			}
			List list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list != null && list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] arrObj = (Object[]) list.get(i);

					String orderNo = arrObj[0].toString();
					String customerName = arrObj[1].toString();
					String posName = arrObj[2].toString();
					String orderDate = arrObj[3].toString();
					String shippingAddress = arrObj[4].toString();

					JSONObject objOrderHDBean = new JSONObject();

					objOrderHDBean.put("strOrderNo", orderNo);
					objOrderHDBean.put("strCustomerName", customerName);
					objOrderHDBean.put("strPosName", posName);
					objOrderHDBean.put("dteDate", orderDate);
					objOrderHDBean.put("strCustAddressLine1", shippingAddress);

					JSONArray listOfOrderDtlData = new JSONArray();

					sqlBuilder.setLength(0);
					sqlBuilder.append("select * "
							+ "from tblorderdtl a "
							+ "where a.strOrderNo='" + orderNo + "' ");
					List listDtl = objBaseService.funGetList(sqlBuilder, "sql");
					if (listDtl != null && listDtl.size() > 0)
					{
						for (int j = 0; j < listDtl.size(); j++)
						{
							Object[] arrDtlObj = (Object[]) listDtl.get(j);

							String strItemCode = arrDtlObj[0].toString();
							String strItemName = arrDtlObj[1].toString();
							double dblRate = Double.parseDouble(arrDtlObj[4].toString());
							double dblQuantity = Double.parseDouble(arrDtlObj[5].toString());
							double dblAmount = Double.parseDouble(arrDtlObj[6].toString());
							double dblTaxAmount = Double.parseDouble(arrDtlObj[7].toString());

							JSONObject objOrderDtlBean = new JSONObject();
							objOrderDtlBean.put("strItemCode", strItemCode);
							objOrderDtlBean.put("strItemName", strItemName);
							objOrderDtlBean.put("dblRate", dblRate);
							objOrderDtlBean.put("dblQuantity", dblQuantity);
							objOrderDtlBean.put("dblAmount", dblAmount);
							objOrderDtlBean.put("dblTaxAmount", "");

							listOfOrderDtlData.add(objOrderDtlBean);
						}
					}

					objOrderHDBean.put("listOfOrderDtlData", listOfOrderDtlData);

					listOfOrderHdData.add(objOrderHDBean);
				}

				rootJsonObject.put("listOfOrderHdData", listOfOrderHdData);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return rootJsonObject;
		}
	}

}
