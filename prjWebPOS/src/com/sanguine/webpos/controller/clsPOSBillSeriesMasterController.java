package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillSeriesDtlBean;
import com.sanguine.webpos.bean.clsPOSBillSeriesTypeMasterBean;
import com.sanguine.webpos.bean.clsPOSGroupMasterBean;
import com.sanguine.webpos.model.clsBillSeriesHdModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;


@Controller
public class clsPOSBillSeriesMasterController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsBaseServiceImpl objBaseServiceImpl;

	@Autowired
	clsPOSMasterService objMasterService;

	Map map = new HashMap();
	Map<String, String> mapCodeWithName = new HashMap<String, String>();
	Map<String, String> mapNameWithCode = new HashMap<String, String>();
	Map<String, String> mapSelectedCodeWithName = new HashMap<String, String>();
	Map<Integer, List<String>> mapBillSeriesCodeList = new HashMap();
	Map<Integer, List<String>> mapBillSeriesNameList = new HashMap();
	String selectedPOSCode;

	@RequestMapping(value = "/frmBillSeriesMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(
			@ModelAttribute("command") @Valid clsPOSBillSeriesTypeMasterBean objBean,
			BindingResult result, Map<String, Object> model,
			HttpServletRequest request) {
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();

			model.put("urlHits", urlHits);

			String clientCode = request.getSession().getAttribute("gClientCode")
					.toString();

			List list = objMasterService.funFillPOSCombo(clientCode);

			map.put("All", "All");
			if (list != null) {
				for (int cnt = 0; cnt < list.size(); cnt++) {
					Object obj = list.get(cnt);

					map.put(Array.get(obj, 0), Array.get(obj, 1));
				}
			}
			model.put("posList", map);

		} catch (Exception e) {
			urlHits = "1";
		}

		if ("2".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmBillSeriesMaster_1");
		} else if ("1".equalsIgnoreCase(urlHits)) {
			return new ModelAndView("frmBillSeriesMaster");
		} else {
			return null;
		}

	}

	@RequestMapping(value = "loadSetSelectedBillSeries", method = RequestMethod.GET)
	public @ResponseBody List funGetSelectedBillSeries(
			@RequestParam("posCode") String posCode, HttpServletRequest req) {
		List list = new ArrayList();
		try {
			String type = "";
			StringBuilder sqlBillSeries = new StringBuilder();
			sqlBillSeries
					.append("select a.strType from tblbillseries a where a.strPOSCode='"
							+ posCode + "' group by a.strType  ");
			List listOfType = objBaseServiceImpl.funGetList(sqlBillSeries,
					"sql");
			if (listOfType != null) {
				for (int i = 0; i < listOfType.size(); i++) {
					type = (String) listOfType.get(i);
					clsPOSBillSeriesTypeMasterBean objBean = new clsPOSBillSeriesTypeMasterBean();
					objBean.setStrType(type);
					list.add(objBean);
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@RequestMapping(value = "funLoadOldBillSeries", method = RequestMethod.GET)
	public @ResponseBody List funLoadOldBillSeries(
			@RequestParam("posCode") String posCode) {
		List listOfOldBillSeriesDtl = new ArrayList();
		try {
			StringBuilder sqlLoadBillSeries = new StringBuilder();
			sqlLoadBillSeries
					.append("select a.strType,a.strBillSeries,a.strCodes,a.strNames,a.strPrintGTOfOtherBills,strPrintInclusiveOfTaxOnBill "
							+ " from tblbillseries a where strPOSCode='"
							+ posCode + "' ");
			int serialNo = 1;
			mapSelectedCodeWithName.clear();
			mapBillSeriesCodeList.clear();
			mapBillSeriesNameList.clear();
			List list = objBaseServiceImpl.funGetList(sqlLoadBillSeries, "sql");
			if (list != null) {
				for (int cnt = 0; cnt < list.size(); cnt++) {
					Object obj = list.get(cnt);
					List<String> listOfCodes = new ArrayList<>();
					List<String> listOfNames = new ArrayList<>();
					String[] codes = Array.get(obj, 2).toString().split(",");
					String[] names = Array.get(obj, 3).toString().split(",");
					StringBuilder billSeriesBuilder = new StringBuilder();
					for (int i = 0; i < codes.length; i++) {
						listOfCodes.add(codes[i]);
						listOfNames.add(names[i]);
						mapSelectedCodeWithName.put(codes[i], names[i]);
						if (i == 0) {
							billSeriesBuilder.append(names[i]);
						} else {
							billSeriesBuilder.append(",");
							billSeriesBuilder.append(names[i]);
						}
					}
					if (billSeriesBuilder.length() > 0) {
						String strBillSeries = Array.get(obj, 1).toString();

						String printGTOfOtherBills = "false", printIncOfAllTaxes = "false";
						if (Array.get(obj, 4).toString().equalsIgnoreCase("Y")) {
							printGTOfOtherBills = "true";
						} else {
							printGTOfOtherBills = "false";
						}

						if (Array.get(obj, 5).toString().equalsIgnoreCase("Y")) {
							printIncOfAllTaxes = "true";
						} else {
							printIncOfAllTaxes = "false";
						}

						clsPOSBillSeriesDtlBean objBean = new clsPOSBillSeriesDtlBean();
						objBean.setSerialNo(serialNo);
						objBean.setStrBillSeries(strBillSeries);
						objBean.setStrNames(billSeriesBuilder.toString());
						objBean.setStrPrintGTOfOtherBills(printGTOfOtherBills);
						objBean.setStrPrintInclusiveOfTaxOnBill(printIncOfAllTaxes);
						listOfOldBillSeriesDtl.add(objBean);

					}
					mapBillSeriesCodeList.put((serialNo), listOfCodes);
					mapBillSeriesNameList.put((serialNo), listOfNames);
					serialNo = serialNo + 1;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfOldBillSeriesDtl;
	}

	@RequestMapping(value = "/funClickOnAddButton", method = RequestMethod.GET)
	public @ResponseBody Map funClickOnAddButton(
			@RequestParam("itemName") String itemName,
			@RequestParam("itemCode") String itemCode,
			@RequestParam("serialNo") int serialNo, HttpServletRequest req) {
		StringBuilder billSeriesBuilder = new StringBuilder();
		billSeriesBuilder.setLength(0);
		List<String> listOfCodes = new ArrayList<>();
		List<String> listOfNames = new ArrayList<>();

		listOfCodes.add(itemCode);
		listOfNames.add(itemName);
		mapSelectedCodeWithName.put(itemCode, itemName);

		mapBillSeriesCodeList.put(serialNo, listOfCodes);
		mapBillSeriesNameList.put(serialNo, listOfNames);
		return mapSelectedCodeWithName;
	}

	@RequestMapping(value = "/loadBillSeriesTypeData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSGroupMasterBean> funFillSelectedBillSeriesTypeDtlTable(
			@RequestParam("billSeriesType") String billSeriesType,
			HttpServletRequest req) throws Exception
	 {
		String clientCode = req.getSession().getAttribute("gClientCode")
				.toString();
		List colList = new ArrayList();
		boolean addFilter = false;
		StringBuilder sqlQuery = new StringBuilder();
		mapNameWithCode.clear();
		mapCodeWithName.clear();

		Iterator<Map.Entry<String, String>> it = mapSelectedCodeWithName
				.entrySet().iterator();
		String filter = "(''";
		if (mapSelectedCodeWithName.size() > 0) {
			addFilter = true;
		}
		while (it.hasNext()) {
			filter += ",'" + it.next().getKey() + "' ";
		}
		filter += ") ";

		List listRet = new ArrayList();
		if (billSeriesType.equalsIgnoreCase("Group")) {
			List listOfGroupName = objMasterService
					.funGetAllGroupNamesForBillSeries(clientCode, addFilter,
							filter);
			if (listOfGroupName != null) {
				for (int i = 0; i < listOfGroupName.size(); i++) {
					Object obj = listOfGroupName.get(i);
					clsPOSBillSeriesTypeMasterBean objBean = new clsPOSBillSeriesTypeMasterBean();
					objBean.setStrGroupName(Array.get(obj, 0).toString());
					objBean.setStrGroupCode(Array.get(obj, 1).toString());
					listRet.add(objBean);
					mapNameWithCode.put(Array.get(obj, 0).toString(), Array
							.get(obj, 1).toString());
					mapCodeWithName.put(Array.get(obj, 1).toString(), Array
							.get(obj, 1).toString());
				}
			}

		} else if (billSeriesType.equalsIgnoreCase("Sub Group")) {
			List listOfSubGroupName = objMasterService
					.funGetAllSubGroupNamesForBillSeries(clientCode, addFilter,
							filter);
			if (listOfSubGroupName != null) {
				for (int i = 0; i < listOfSubGroupName.size(); i++) {
					Object obj = listOfSubGroupName.get(i);
					clsPOSBillSeriesTypeMasterBean objBean = new clsPOSBillSeriesTypeMasterBean();
					objBean.setStrGroupName(Array.get(obj, 0).toString());
					objBean.setStrGroupCode(Array.get(obj, 1).toString());
					listRet.add(objBean);
					mapNameWithCode.put(Array.get(obj, 0).toString(), Array
							.get(obj, 1).toString());
					mapCodeWithName.put(Array.get(obj, 1).toString(), Array
							.get(obj, 0).toString());
				}
			}

		} else if (billSeriesType.equalsIgnoreCase("Menu Head")) {
			List listOfMenuHeadName = objMasterService
					.funGetAllMenuHeadNamesForBillSeries(clientCode, addFilter,
							filter);
			if (listOfMenuHeadName != null) {
				for (int i = 0; i < listOfMenuHeadName.size(); i++) {
					Object obj = listOfMenuHeadName.get(i);
					clsPOSBillSeriesTypeMasterBean objBean = new clsPOSBillSeriesTypeMasterBean();
					objBean.setStrGroupName(Array.get(obj, 0).toString());
					objBean.setStrGroupCode(Array.get(obj, 1).toString());
					listRet.add(objBean);
					mapNameWithCode.put(Array.get(obj, 0).toString(), Array
							.get(obj, 1).toString());
					mapCodeWithName.put(Array.get(obj, 1).toString(), Array
							.get(obj, 0).toString());
				}
			}

		} else if (billSeriesType.equalsIgnoreCase("Revenue Head")) {

			List listOfRevenueHeadName = objMasterService
					.funGetAllRevenueHeadNamesForBillSeries(clientCode,
							addFilter, filter);
			if (listOfRevenueHeadName != null) {
				for (int i = 0; i < listOfRevenueHeadName.size(); i++) {
					Object obj = listOfRevenueHeadName.get(i);
					clsPOSBillSeriesTypeMasterBean objBean = new clsPOSBillSeriesTypeMasterBean();
					objBean.setStrGroupName(Array.get(obj, 0).toString());
					objBean.setStrGroupCode(Array.get(obj, 1).toString());
					listRet.add(objBean);
					mapNameWithCode.put(Array.get(obj, 0).toString(), Array
							.get(obj, 1).toString());
					mapCodeWithName.put(Array.get(obj, 1).toString(), Array
							.get(obj, 0).toString());
				}

			}
		}
		return listRet;
	}

	@RequestMapping(value = "/savePOSBillSeriesMasterData", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(
			@ModelAttribute("command") @Valid clsPOSBillSeriesTypeMasterBean objBean,
			BindingResult result, HttpServletRequest req) {
		String urlHits = "1";
		String userCode = "";
		try {

			String clientCode = req.getSession().getAttribute("gClientCode")
					.toString();
			String webStockUserCode = req.getSession()
					.getAttribute("gUserCode").toString();
			String selectedPosName = objBean.getStrPOSName();
			if (map.containsKey(selectedPosName)) {
				selectedPOSCode = (String) map.get(selectedPosName);
			}

			String dateCreated = "";
			List<clsPOSBillSeriesDtlBean> list = objBean.getListBillSeriesDtl();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					clsPOSBillSeriesDtlBean obj = list.get(i);
					Integer key = obj.getSerialNo();
					StringBuilder sqlBuilder = new StringBuilder();
					List<String> listOfCodes = mapBillSeriesCodeList.get(key);
					List<String> listOfNames = mapBillSeriesNameList.get(key);

					if (funIsExistsBillSeries(i, listOfCodes, listOfNames,
							selectedPOSCode, obj.getStrBillSeries(), objBean,
							webStockUserCode, dateCreated)) {
						continue;
					}
					String printGTOfOtherBill = "N";
					if (objBean.getStrPrintGTOfOtherBills() != null
							&& Boolean.parseBoolean(objBean
									.getStrPrintGTOfOtherBills())) {
						printGTOfOtherBill = "Y";
					} else {
						printGTOfOtherBill = "N";
					}

					String printInclusiveOfAllTaxesOnBill = "N";
					if (objBean.getStrPrintInclusiveOfTaxOnBill() != null
							&& Boolean.parseBoolean(objBean
									.getStrPrintInclusiveOfTaxOnBill())) {
						printInclusiveOfAllTaxesOnBill = "Y";
					} else {
						printInclusiveOfAllTaxesOnBill = "N";
					}

					clsBillSeriesHdModel objModel = new clsBillSeriesHdModel();
					objModel.setStrPOSCode(selectedPOSCode);
					objModel.setStrType(objBean.getStrType());
					objModel.setStrBillSeries(obj.getStrBillSeries());
					objModel.setIntLastNo(0);
					objModel.setStrCodes(funGetCodeList(listOfCodes));
					objModel.setStrNames(funGetNameList(listOfNames));
					objModel.setStrUserCreated(webStockUserCode);
					objModel.setStrUserEdited(webStockUserCode);
					objModel.setDteCreatedDate(objGlobal
							.funGetCurrentDateTime("yyyy-MM-dd"));
					objModel.setDteEditedDate(objGlobal
							.funGetCurrentDateTime("yyyy-MM-dd"));
					objModel.setStrDataPostFlag("N");
					objModel.setStrClientCode(clientCode);
					objModel.setStrPropertyCode(clientCode + "."
							+ selectedPOSCode);
					objModel.setStrPrintGTOfOtherBills(printGTOfOtherBill);
					objModel.setStrPrintInclusiveOfTaxOnBill(printInclusiveOfAllTaxesOnBill);
					objBaseServiceImpl.funSave(objModel);
				

					req.getSession().setAttribute("success", true);

					req.getSession().setAttribute("successMessage", " ");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/frmBillSeriesMaster.html?saddr="
				+ urlHits);
	}

	private boolean funIsExistsBillSeries(int row, List<String> listOfCodes,
			List<String> listOfNames, String posCode, String billSeries,
			clsPOSBillSeriesTypeMasterBean objBean, String userCode,
			String clientCode) {
		boolean isExists = false;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select * from tblbillseries a "
					+ "where (a.strPOSCode='" + posCode
					+ "' or a.strPOSCode='All')  and a.strBillSeries='"
					+ billSeries + "' ");
			List listOfExistBillSeries = objBaseServiceImpl.funGetList(sql,
					"sql");

			if (listOfExistBillSeries.size() != 0) {
				isExists = true;
			}
			if (isExists) {

				List<clsPOSBillSeriesDtlBean> list = objBean
						.getListBillSeriesDtl();
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						clsPOSBillSeriesDtlBean obj = list.get(i);

						String printGTOfOtherBill = "N";
						if (obj.getStrPrintGTOfOtherBills() != null
								&& Boolean.parseBoolean(obj
										.getStrPrintGTOfOtherBills())) {
							printGTOfOtherBill = "Y";
						} else {
							printGTOfOtherBill = "N";
						}
						String printInclusiveOfAllTaxesOnBill = "N";
						if (obj.getStrPrintInclusiveOfTaxOnBill() != null
								&& Boolean.parseBoolean(obj
										.getStrPrintInclusiveOfTaxOnBill())) {
							printInclusiveOfAllTaxesOnBill = "Y";
						} else {
							printInclusiveOfAllTaxesOnBill = "N";
						}

						StringBuilder sqlUpdate = new StringBuilder("update tblbillseries a "
								+ "set a.strCodes='"
								+ funGetCodeList(listOfCodes)
								+ "' "
								+ ", a.strNames='"
								+ funGetNameList(listOfNames)
								+ "' "
								+ ", a.strUserEdited='"
								+ userCode
								+ "'"
								+ ", a.strType='"
								+ objBean.getStrType()
								+ "' "
								+ ", a.dteEditedDate='"
								+ objGlobal.funGetCurrentDateTime("yyyy-MM-dd")
								+ "' "
								+ ", a.strPrintGTOfOtherBills='"
								+ printGTOfOtherBill
								+ "' "
								+ ", a.strPrintInclusiveOfTaxOnBill='"
								+ printInclusiveOfAllTaxesOnBill
								+ "' "
								+ ",a.strPOSCode='"
								+ selectedPOSCode
								+ "' "
								+ " where a.strBillSeries='"
								+ obj.getStrBillSeries()
								+ "' "
								+ " and (a.strPOSCode='"
								+ selectedPOSCode
								+ "' or a.strPOSCode='All')  ");
						objBaseServiceImpl.funExecuteUpdate(sqlUpdate.toString(), "sql");
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return isExists;
		}
	}

	private String funGetCodeList(List<String> listOfCodes) {
		StringBuilder sqlCodeBuilder = new StringBuilder();
		for (int i = 0; i < listOfCodes.size(); i++) {
			if (i == 0) {
				sqlCodeBuilder.append(listOfCodes.get(i));
			} else {
				sqlCodeBuilder.append("," + listOfCodes.get(i));
			}
		}

		return sqlCodeBuilder.toString();
	}

	private String funGetNameList(List<String> listOfNames) {
		StringBuilder sqlNameBuilder = new StringBuilder();
		for (int i = 0; i < listOfNames.size(); i++) {
			if (i == 0) {
				sqlNameBuilder.append(listOfNames.get(i));
			} else {
				sqlNameBuilder.append("," + listOfNames.get(i));
			}
		}

		return sqlNameBuilder.toString();
	}

}
