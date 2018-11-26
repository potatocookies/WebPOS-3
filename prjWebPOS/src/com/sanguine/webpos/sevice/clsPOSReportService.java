package com.sanguine.webpos.sevice;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtl;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSBillSettlementDtl;
import com.sanguine.webpos.bean.clsPOSCommonBeanDtl;
import com.sanguine.webpos.bean.clsPOSCostCenterBean;
import com.sanguine.webpos.bean.clsPOSGenericBean;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupItemBean;
import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsPOSGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsPOSItemWiseConsumption;
import com.sanguine.webpos.bean.clsPOSKOTAnalysisBean;
import com.sanguine.webpos.bean.clsPOSOperatorDtl;
import com.sanguine.webpos.bean.clsPOSSalesFlashColumns;
import com.sanguine.webpos.bean.clsPOSSalesFlashReportsBean;
import com.sanguine.webpos.bean.clsPOSTaxCalculationDtls;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSWaiterAnalysisBean;
import com.sanguine.webpos.bean.clsReprintDocs;
import com.sanguine.webpos.comparator.clsPOSBillComparator;
import com.sanguine.webpos.comparator.clsPOSBillComplimentaryComparator;
import com.sanguine.webpos.comparator.clsPOSCostCenterComparator;
import com.sanguine.webpos.comparator.clsPOSGroupSubGroupComparator;
import com.sanguine.webpos.comparator.clsPOSGroupSubGroupWiseSalesComparator;
import com.sanguine.webpos.comparator.clsPOSItemConsumptionComparator;
import com.sanguine.webpos.comparator.clsPOSSalesFlashComparator;
import com.sanguine.webpos.comparator.clsSalesFlashComparator;
import com.sanguine.webpos.comparator.clsVoidBillComparator;
import com.sanguine.webpos.comparator.clsWaiterWiseSalesComparator;
import com.sanguine.webpos.model.clsShiftMasterModel;
import com.sanguine.webpos.util.clsPOSGroupWiseComparator;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Service
public class clsPOSReportService {

	@Autowired
	intfBaseService objBaseService;

	Map<String, List<Map<String, clsPOSBillSettlementDtl>>> mapPOSDtlForSettlement;
	Map<String, Map<String, clsPOSBillItemDtl>> mapPOSItemDtl;
	Map<String, Map<String, clsPOSBillItemDtl>> mapPOSMenuHeadDtl;
	Map<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> mapPOSDtlForGroupSubGroup;
	Map<String, Map<String, com.sanguine.webpos.bean.clsPOSCommonBeanDtl>> mapPOSWaiterWiseSales;
	Map<String, Map<String, com.sanguine.webpos.bean.clsPOSCommonBeanDtl>> mapPOSDeliveryBoyWise;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSCostCenterWiseSales;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSTableWiseSales;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSHourlyWiseSales;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSAreaWiseSales;
	Map<String, clsPOSCommonBeanDtl> mapPOSDayWiseSales;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSModifierWiseSales;
	Map<String, Map<String, clsPOSCommonBeanDtl>> mapPOSMonthWiseSales;
	Map<String, List<clsPOSOperatorDtl>> mapOperatorDtls;
	Map<String, String> mapDocCode;
	Map<String, Double> mapPOSDocSales;
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;

	double TotSale = 0;

	public List funProcessDayEndReport(String posCode, String fromDate1, String toDate1) {
		StringBuilder sbSql = new StringBuilder();
		List list = new ArrayList();
		try {
			sbSql.append(
					"select b.strPOSName,DATE_FORMAT(date(a.dtePOSDate),'%d-%m-%Y'),dblHDAmt,dblDiningAmt,dblTakeAway,dblTotalSale,dblFloat"
							+ ",dblCash,dblAdvance,dblTransferIn,dblTotalReceipt,dblPayments,dblWithdrawal,dblTransferOut,dblRefund"
							+ ",dblTotalPay,dblCashInHand,dblNoOfBill,dblNoOfVoidedBill,dblNoOfModifyBill,strWSStockAdjustmentNo,strExciseBillGeneration "
							+ " ,a.dblNetSale,a.dblGrossSale,a.dblAPC"
							+ " from tbldayendprocess a,tblposmaster b where a.strPOSCode=b.strPOSCode ");

			if ("All".equals(posCode)) {
				sbSql.append(" and date(a.dtePOSDate) between '" + fromDate1 + "' and '" + toDate1 + "' ");
			} else {
				String temp = posCode;
				StringBuilder sb = new StringBuilder(temp);
				int len = temp.length();
				int lastInd = sb.lastIndexOf(" ");
				String POSCode = sb.substring(lastInd + 1, len).toString();
				sbSql.append(" and a.strPOSCode='" + POSCode + "' and date(a.dtePOSDate) between '" + fromDate1
						+ "' and '" + toDate1 + "'");
			}
			list = objBaseService.funGetList(sbSql, "sql");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<clsPOSBillDtl> funProcessComplimentaryDetailReport(String strPosCode, String dteFromDate,
			String dteToDate, String strReasonCode, String strShiftNo) {
		List<clsPOSBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQBill = new StringBuilder();
		StringBuilder sqlLiveModifierBuilder = new StringBuilder();
		StringBuilder sqlQModifierBuilder = new StringBuilder();
		try {
			String enableShiftYN="N";
			sbSqlLive.setLength(0);
			sbSqlQBill.setLength(0);
			sqlLiveModifierBuilder.setLength(0);
			sqlQModifierBuilder.setLength(0);

			// live data
			sbSqlLive.append(
					"SELECT IFNULL(a.strBillNo,''), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate, IFNULL(b.strItemName,'') "
							+ ",sum(b.dblQuantity),b.dblRate,sum(b.dblQuantity*b.dblRate) AS dblAmount, IFNULL(f.strPosName,'') "
							+ ", IFNULL(g.strWShortName,'NA') AS strWShortName, IFNULL(e.strReasonName,''), IFNULL(a.strRemarks,'') "
							+ ", IFNULL(i.strGroupName,'') AS strGroupName, IFNULL(b.strKOTNo,'') "
							+ ",a.strPOSCode, IFNULL(h.strTableName,'') AS strTableName, IFNULL(b.strItemCode,'        ') "
							+ "FROM tblbillhd a " + "Inner JOIN tblbillcomplementrydtl b ON a.strBillNo = b.strBillNo "
							+ "left outer JOIN tblreasonmaster e ON a.strReasonCode = e.strReasonCode " + "LEFT OUTER "
							+ "JOIN tblposmaster f ON a.strPOSCode=f.strPosCode " + "LEFT OUTER  "
							+ "JOIN tblwaitermaster g ON a.strWaiterNo=g.strWaiterNo " + "LEFT OUTER "
							+ "JOIN tbltablemaster h ON a.strTableNo=h.strTableNo " + "LEFT OUTER "
							+ "JOIN tblitemcurrentstk i ON b.strItemCode=i.strItemCode "
							+ "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

			// live modifiers
			sqlLiveModifierBuilder.append(
					"select ifnull(a.strBillNo,''),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate,b.strModifierName, sum(b.dblQuantity), b.dblRate,sum(b.dblQuantity*b.dblRate) as dblAmount"
							+ " ,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, a.strRemarks,ifnull(i.strGroupName,'') as strGroupName, "
							+ " ifnull(j.strKOTNo,''),a.strPOSCode,ifnull(h.strTableName,'') as strTableName,ifnull(b.strItemCode,'        ')  "
							+ " from tblbillhd a" + " INNER JOIN  tblbillmodifierdtl b on a.strBillNo = b.strBillNo"
							+ " left outer join  tblbillsettlementdtl c on a.strBillNo = c.strBillNo"
							+ " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
							+ " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
							+ " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
							+ " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
							+ " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
							+ " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
							+ " left outer join  tblbilldtl j on b.strBillNo = j.strBillNo  "
							+ " where d.strSettelmentType = 'Complementary' ");

			// Q data
			sbSqlQBill.append(
					"SELECT IFNULL(a.strBillNo,''), DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate, IFNULL(b.strItemName,'') "
							+ ",sum(b.dblQuantity),b.dblRate,sum(b.dblQuantity*b.dblRate) AS dblAmount, IFNULL(f.strPosName,'') "
							+ ", IFNULL(g.strWShortName,'NA') AS strWShortName, IFNULL(e.strReasonName,''), IFNULL(a.strRemarks,'') "
							+ ", IFNULL(i.strGroupName,'') AS strGroupName, IFNULL(b.strKOTNo,'') "
							+ ",a.strPOSCode, IFNULL(h.strTableName,'') AS strTableName, IFNULL(b.strItemCode,'        ') "
							+ "FROM tblqbillhd a "
							+ "INNER JOIN tblqbillcomplementrydtl b ON a.strBillNo = b.strBillNo "
							+ "left outer JOIN tblreasonmaster e ON a.strReasonCode = e.strReasonCode " + "LEFT OUTER "
							+ "JOIN tblposmaster f ON a.strPOSCode=f.strPosCode " + "LEFT OUTER  "
							+ "JOIN tblwaitermaster g ON a.strWaiterNo=g.strWaiterNo " + "LEFT OUTER "
							+ "JOIN tbltablemaster h ON a.strTableNo=h.strTableNo " + "LEFT OUTER "
							+ "JOIN tblitemcurrentstk i ON b.strItemCode=i.strItemCode "
							+ "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

			// Q modifiers
			sqlQModifierBuilder.append(
					"select ifnull(a.strBillNo,''),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate,b.strModifierName,sum(b.dblQuantity), b.dblRate,sum(b.dblQuantity*b.dblRate,0) as dblAmount,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName,ifnull(e.strReasonName,'') as strReasonName, a.strRemarks,ifnull(i.strGroupName,'') as strGroupName,\n"
							+ "ifnull(j.strKOTNo,''),a.strPOSCode,ifnull(h.strTableName,'') as strTableName,ifnull(b.strItemCode,'        ')  "
							+ " from tblqbillhd a" + " INNER JOIN  tblqbillmodifierdtl b on a.strBillNo = b.strBillNo"
							+ " left outer join  tblqbillsettlementdtl c on a.strBillNo = c.strBillNo"
							+ " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
							+ " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
							+ " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
							+ " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
							+ " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
							+ " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
							+ " left outer join  tblqbilldtl j on b.strBillNo = j.strBillNo  "
							+ " where d.strSettelmentType = 'Complementary' ");

			if (!strPosCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
			}
			if (!strReasonCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
			}
			
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");

			sbSqlLive.append("  " + " group by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode "
					+ " order by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode ");
			sbSqlQBill.append("  " + " group by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode "
					+ " order by a.strPOSCode,a.strBillNo,b.strKOTNo,b.strItemCode ");
			sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate
					+ "'  " + " group by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName "
					+ " order by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName ");
			sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate
					+ "'  " + " group by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName "
					+ " order by a.strPOSCode,a.strBillNo,left(b.strItemCode,7),b.strModifierName ");

			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrBillNo(obj[0].toString());
					objItemDtl.setDteBillDate(obj[1].toString());
					objItemDtl.setStrItemName(obj[2].toString());
					objItemDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));// itemQty
					objItemDtl.setDblModQuantity(0);// modifierQty
					objItemDtl.setDblRate(Double.parseDouble(obj[4].toString()));
					objItemDtl.setDblAmount(Double.parseDouble(obj[5].toString()));
					objItemDtl.setStrPosName(obj[6].toString());
					objItemDtl.setStrWShortName(obj[7].toString());
					objItemDtl.setStrReasonName(obj[8].toString());
					objItemDtl.setStrRemarks(obj[9].toString());
					objItemDtl.setStrGroupName(obj[10].toString());
					objItemDtl.setStrKOTNo(obj[11].toString());
					objItemDtl.setStrPOSCode(obj[12].toString());
					objItemDtl.setStrTableName(obj[13].toString());
					objItemDtl.setStrItemCode(obj[14].toString());

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			// QFile
			List listSqlQBill = objBaseService.funGetList(sbSqlQBill, "sql");
			if (listSqlQBill.size() > 0) {
				for (int i = 0; i < listSqlQBill.size(); i++) {
					Object[] obj = (Object[]) listSqlQBill.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrBillNo(obj[0].toString());
					objItemDtl.setDteBillDate(obj[1].toString());
					objItemDtl.setStrItemName(obj[2].toString());
					objItemDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));// itemQty
					objItemDtl.setDblModQuantity(0);// modifierQty
					objItemDtl.setDblRate(Double.parseDouble(obj[4].toString()));
					objItemDtl.setDblAmount(Double.parseDouble(obj[5].toString()));
					objItemDtl.setStrPosName(obj[6].toString());
					objItemDtl.setStrWShortName(obj[7].toString());
					objItemDtl.setStrReasonName(obj[8].toString());
					objItemDtl.setStrRemarks(obj[9].toString());
					objItemDtl.setStrGroupName(obj[10].toString());
					objItemDtl.setStrKOTNo(obj[11].toString());
					objItemDtl.setStrPOSCode(obj[12].toString());
					objItemDtl.setStrTableName(obj[13].toString());
					objItemDtl.setStrItemCode(obj[14].toString());

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			Comparator<clsPOSBillDtl> posNameComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
				}
			};

			Comparator<clsPOSBillDtl> billDateComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
				}
			};
			Comparator<clsPOSBillDtl> billNoComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				}
			};
			Comparator<clsPOSBillDtl> kotNoComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
				}
			};
			Comparator<clsPOSBillDtl> itemCodeComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
				}
			};

			Collections.sort(listOfCompliItemDtl, new clsPOSBillComplimentaryComparator(posNameComparator,
					billDateComparator, billNoComparator, kotNoComparator, itemCodeComparator));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfCompliItemDtl;

	}

	public List<clsPOSBillDtl> funProcessComplimentarySummaryReport(String strPosCode, String dteFromDate,
			String dteToDate, String strReasonCode, String strShiftNo) {
		List<clsPOSBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQBill = new StringBuilder();
		StringBuilder sqlLiveModifierBuilder = new StringBuilder();
		StringBuilder sqlQModifierBuilder = new StringBuilder();
		try {
			String enableShiftYN="N";
			sbSqlLive.setLength(0);
			sbSqlQBill.setLength(0);
			sqlLiveModifierBuilder.setLength(0);
			sqlQModifierBuilder.setLength(0);

			sbSqlLive.append(
					"select ifnull(a.strBillNo,'')as strBillNo, ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblRate*b.dblQuantity), 0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, ifnull(a.strRemarks,'') as strRemarks  "
							+ "from tblbillhd a   "
							+ "INNER JOIN tblbillcomplementrydtl b on a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode   "
							+ "left outer join tblposmaster f on a.strPOSCode=f.strPosCode   "
							+ "left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo  "
							+ "left outer join tbltablemaster h on  a.strTableNo=h.strTableNo  "
							+ "left outer join tblitemcurrentstk i on b.strItemCode=i.strItemCode  "
							+ "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");
			// live modifiers
			sqlLiveModifierBuilder.append(
					"select ifnull(a.strBillNo,''),ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblQuantity*b.dblRate),0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,''), ifnull(a.strRemarks,'') as strRemarks "
							+ " from tblbillhd a" + " INNER JOIN  tblbillmodifierdtl b on a.strBillNo = b.strBillNo"
							+ " left outer join  tblbillsettlementdtl c on a.strBillNo = c.strBillNo"
							+ " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
							+ " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
							+ " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
							+ " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
							+ " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
							+ " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
							+ " left outer join  tblbilldtl j on b.strBillNo = j.strBillNo  "
							+ " where d.strSettelmentType = 'Complementary' ");

			// Q data
			sbSqlQBill.append(
					"select ifnull(a.strBillNo,'')as strBillNo, ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,ifnull(sum(b.dblRate*b.dblQuantity), 0) as dblAmount ,ifnull(f.strPosName,'') as strPosName,ifnull(g.strWShortName,'NA') as strWShortName, ifnull(e.strReasonName,'') as strReasonName, ifnull(a.strRemarks,'') as strRemarks  "
							+ "from tblqbillhd a   "
							+ "INNER JOIN  tblqbillcomplementrydtl b on a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode   "
							+ "left outer join tblposmaster f on a.strPOSCode=f.strPosCode   "
							+ "left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo  "
							+ "left outer join tbltablemaster h on  a.strTableNo=h.strTableNo  "
							+ "left outer join tblitemcurrentstk i on b.strItemCode=i.strItemCode  "
							+ "where  date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' ");

			// Q modifiers
			sqlQModifierBuilder.append(
					"select ifnull(a.strBillNo,''),ifnull(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'') as dteBillDate,sum(b.dblQuantity*b.dblRate) as dblAmount ,ifnull(f.strPosName,''),ifnull(g.strWShortName,'NA') as strWShortName, e.strReasonName, a.strRemarks "
							+ " from tblqbillhd a" + " INNER JOIN  tblqbillmodifierdtl b on a.strBillNo = b.strBillNo"
							+ " left outer join  tblqbillsettlementdtl c on a.strBillNo = c.strBillNo"
							+ " left outer join  tblsettelmenthd d on c.strSettlementCode = d.strSettelmentCode "
							+ " left outer join tblreasonmaster e on  a.strReasonCode = e.strReasonCode "
							+ " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
							+ " left outer join tblwaitermaster g on a.strWaiterNo=g.strWaiterNo"
							+ " left outer join tbltablemaster h on  a.strTableNo=h.strTableNo"
							+ " left outer join tblitemcurrentstk i on left(b.strItemCode,7)=i.strItemCode"
							+ " left outer join  tblqbilldtl j on b.strBillNo = j.strBillNo  "
							+ " where d.strSettelmentType = 'Complementary' ");

			if (!strPosCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
			}
			if (!strReasonCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			
			

			sbSqlLive.append("  " + " group by a.strPOSCode,a.strBillNo " + " order by a.strPOSCode,a.strBillNo ");
			sbSqlQBill.append("  " + " group by a.strPOSCode,a.strBillNo " + " order by a.strPOSCode,a.strBillNo ");
			sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate
					+ "'  " + " group by a.strPOSCode,a.strBillNo " + " order by a.strPOSCode,a.strBillNo ");
			sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate
					+ "'  " + " group by a.strPOSCode,a.strBillNo " + " order by a.strPOSCode,a.strBillNo ");

			// live data
			List ListSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (ListSqlLive.size() > 0) {
				for (int i = 0; i < ListSqlLive.size(); i++) {
					Object[] obj = (Object[]) ListSqlLive.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrBillNo(obj[0].toString());
					objItemDtl.setDteBillDate(obj[1].toString());
					objItemDtl.setDblAmount(Double.parseDouble(obj[2].toString()));
					objItemDtl.setStrPosName(obj[3].toString());
					objItemDtl.setStrWShortName(obj[4].toString());
					objItemDtl.setStrReasonName(obj[5].toString());
					objItemDtl.setStrRemarks(obj[6].toString());

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			// QFile
			List listSqlQBill = objBaseService.funGetList(sbSqlQBill, "sql");
			if (listSqlQBill.size() > 0) {
				for (int i = 0; i < listSqlQBill.size(); i++) {
					Object[] obj = (Object[]) listSqlQBill.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrBillNo(obj[0].toString());
					objItemDtl.setDteBillDate(obj[1].toString());
					objItemDtl.setDblAmount(Double.parseDouble(obj[2].toString()));
					objItemDtl.setStrPosName(obj[3].toString());
					objItemDtl.setStrWShortName(obj[4].toString());
					objItemDtl.setStrReasonName(obj[5].toString());
					objItemDtl.setStrRemarks(obj[6].toString());

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			Comparator<clsPOSBillDtl> posNameComparator = new Comparator<clsPOSBillDtl>() {
				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
				}
			};

			Comparator<clsPOSBillDtl> billDateComparator = new Comparator<clsPOSBillDtl>() {
				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
				}
			};
			Comparator<clsPOSBillDtl> billNoComparator = new Comparator<clsPOSBillDtl>() {
				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				}
			};

			Collections.sort(listOfCompliItemDtl,
					new clsPOSBillComplimentaryComparator(posNameComparator, billDateComparator, billNoComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfCompliItemDtl;
	}

	public List<clsPOSBillDtl> funProcessComplimentaryGroupWiseReport(String strPosCode, String dteFromDate,
			String dteToDate, String strReasonCode, String strShiftNo) {
		List<clsPOSBillDtl> listOfCompliItemDtl = new ArrayList<>();
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQBill = new StringBuilder();
		StringBuilder sqlLiveModifierBuilder = new StringBuilder();
		StringBuilder sqlQModifierBuilder = new StringBuilder();
		String enableShiftYN="N";
		sbSqlLive.setLength(0);
		sbSqlQBill.setLength(0);
		sqlLiveModifierBuilder.setLength(0);
		sqlQModifierBuilder.setLength(0);
		try {
			// live data
			sbSqlLive.append("SELECT e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strItemName,b.dblRate"
					+ ", SUM(b.dblQuantity) AS dblQnty,SUM(b.dblRate* b.dblQuantity) AS dblAmount "
					+ "FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
					+ "WHERE a.strBillNo = b.strBillNo  " + "AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
					+ "AND a.strPOSCode=e.strPosCode  " + "AND b.strItemCode=f.strItemCode  "
					+ "AND f.strSubGroupCode=g.strSubGroupCode  " + "AND g.strGroupCode=h.strGroupCode  ");

			// live modifiers
			sqlLiveModifierBuilder
					.append(" select e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strModifierName"
							+ ",b.dblRate,sum(b.dblQuantity),SUM(b.dblRate*b.dblQuantity) as dblAmount"
							+ " from tblbillhd a,tblbillmodifierdtl b,tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
							+ " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h" + " where a.strBillNo = b.strBillNo "
							+ " and  a.strBillNo = c.strBillNo " + " and c.strSettlementCode = d.strSettelmentCode "
							+ " and  a.strPOSCode=e.strPosCode  " + " and left(b.strItemCode,7)=f.strItemCode"
							+ " and f.strSubGroupCode=g.strSubGroupCode" + " and g.strGroupCode=h.strGroupCode"
							+ " and d.strSettelmentType='Complementary' ");

			// Q data
			sbSqlQBill.append("SELECT e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strItemName,b.dblRate"
					+ ", SUM(b.dblQuantity) AS dblQnty,SUM(b.dblRate* b.dblQuantity) AS dblAmount "
					+ "FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
					+ "WHERE a.strBillNo = b.strBillNo  " + "AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
					+ "AND a.strPOSCode=e.strPosCode  " + "AND b.strItemCode=f.strItemCode  "
					+ "AND f.strSubGroupCode=g.strSubGroupCode  " + "AND g.strGroupCode=h.strGroupCode  ");

			// Q modifiers
			sqlQModifierBuilder
					.append("select e.strPosName,h.strGroupCode,h.strGroupName,b.strItemCode,b.strModifierName"
							+ ",b.dblRate,sum(b.dblQuantity),SUM(b.dblRate*b.dblQuantity) as dblAmount"
							+ " from tblqbillhd a,tblqbillmodifierdtl b,tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e \n"
							+ " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h" + " where a.strBillNo = b.strBillNo "
							+ " and  a.strBillNo = c.strBillNo " + " and c.strSettlementCode = d.strSettelmentCode "
							+ " and  a.strPOSCode=e.strPosCode  " + " and left(b.strItemCode,7)=f.strItemCode"
							+ " and f.strSubGroupCode=g.strSubGroupCode" + " and g.strGroupCode=h.strGroupCode"
							+ " and d.strSettelmentType='Complementary'");

			if (!strPosCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sbSqlQBill.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
				sqlQModifierBuilder.append(" AND a.strPOSCode = '" + strPosCode + "' ");
			}
			if (!strReasonCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sbSqlQBill.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlLiveModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
				sqlQModifierBuilder.append(" and a.strReasonCode='" + strReasonCode + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}

			sbSqlLive.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
					+ " group by h.strGroupCode,b.strItemCode" + " order by h.strGroupCode,b.strItemCode;");
			sbSqlQBill.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
					+ " group by h.strGroupCode,b.strItemCode" + " order by h.strGroupCode,b.strItemCode;");
			sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate
					+ "' " + " group by h.strGroupCode,b.strItemCode,b.strModifierName"
					+ " order by h.strGroupCode,b.strItemCode;");
			sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + dteFromDate + "' and '" + dteToDate + "' "
					+ " group by h.strGroupCode,b.strItemCode,b.strModifierName"
					+ " order by h.strGroupCode,b.strItemCode;");

			// live data
			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrPosName(obj[0].toString());
					objItemDtl.setStrGroupCode(obj[1].toString());
					objItemDtl.setStrGroupName(obj[2].toString());
					objItemDtl.setStrItemCode(obj[3].toString());
					objItemDtl.setStrItemName(obj[4].toString());
					objItemDtl.setDblRate(Double.parseDouble(obj[5].toString()));
					objItemDtl.setDblQuantity(Double.parseDouble(obj[6].toString()));
					objItemDtl.setDblAmount(Double.parseDouble(obj[7].toString()));

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			// QFile
			List listSqlQBill = objBaseService.funGetList(sbSqlQBill, "sql");
			if (listSqlQBill.size() > 0) {
				for (int i = 0; i < listSqlQBill.size(); i++) {
					Object[] obj = (Object[]) listSqlQBill.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrPosName(obj[0].toString());
					objItemDtl.setStrGroupCode(obj[1].toString());
					objItemDtl.setStrGroupName(obj[2].toString());
					objItemDtl.setStrItemCode(obj[3].toString());
					objItemDtl.setStrItemName(obj[4].toString());
					objItemDtl.setDblRate(Double.parseDouble(obj[5].toString()));
					objItemDtl.setDblQuantity(Double.parseDouble(obj[6].toString()));
					objItemDtl.setDblAmount(Double.parseDouble(obj[7].toString()));

					listOfCompliItemDtl.add(objItemDtl);
				}
			}

			Comparator<clsPOSBillDtl> posNameComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrPosName().compareToIgnoreCase(o2.getStrPosName());
				}
			};
			Comparator<clsPOSBillDtl> groupNameComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrGroupName().compareToIgnoreCase(o2.getStrGroupName());
				}
			};

			Comparator<clsPOSBillDtl> itemNameComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrItemName().compareToIgnoreCase(o2.getStrItemName());
				}
			};

			Collections.sort(listOfCompliItemDtl,
					new clsPOSBillComplimentaryComparator(posNameComparator, groupNameComparator, itemNameComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfCompliItemDtl;
	}

	public List<clsPOSItemWiseConsumption> funProcessItemWiseConsumptionReport(String posCode, String fromDate,
			String toDate, String groupCode, String costCenterCode, String strShiftNo, String printZeroAmountModi) {
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbSqlMod = new StringBuilder();
		StringBuilder sbFilters = new StringBuilder();
		Map<String, clsPOSItemWiseConsumption> hmItemWiseConsumption = new HashMap<String, clsPOSItemWiseConsumption>();
		String costCenterCd = "", costCenterNm = "";
		int sqlNo = 0;
		String enableShiftYN="N";
		List<clsPOSItemWiseConsumption> list = new ArrayList<clsPOSItemWiseConsumption>();
		try {
			// Code for Sales Qty for bill detail and bill modifier live & q data
			// for Sales Qty for bill detail live data
			String amount = "SUM(b.dblamount)";
		    String rate = "b.dblRate";
		    String discAmt = "SUM(b.dblDiscountAmt)";
			
			sbSql.setLength(0);

			sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), "+amount+","+rate+", e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
				    + ",i.strCostCenterCode,j.strCostCenterName "
				    + "FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
				    + "WHERE a.strBillNo=b.strBillNo  "
				    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
				    + "AND a.strPOSCode=e.strPosCode  "
				    + "AND b.strItemCode=f.strItemCode  "
				    + "AND f.strSubGroupCode=g.strSubGroupCode  "
				    + "AND g.strGroupCode=h.strGroupCode  "
				    + "and b.strItemCode=i.strItemCode "
				    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
				    + " and i.strHourlyPricing='NO' ");
			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
			}
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}

			sbSql.append(" group by b.strItemCode,a.strBillNo order by j.strCostCenterCode,b.strItemName");
			List listSqlLive = objBaseService.funGetList(sbSql, "sql");
			if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] obj = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) 
					{
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						objItemWiseConsumption.setSaleQty(
								objItemWiseConsumption.getSaleQty() + Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt()
								+ (Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString())));
						objItemWiseConsumption.setSubTotal(
								objItemWiseConsumption.getSubTotal() + Double.parseDouble(obj[4].toString()));
					} 
					else 
					{
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setSaleQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setComplimentaryQty(0);
						objItemWiseConsumption.setNcQty(0);
						objItemWiseConsumption.setSubTotal(Double.parseDouble(obj[3].toString()));
						objItemWiseConsumption.setDiscAmt(Double.parseDouble(obj[6].toString()));
						objItemWiseConsumption.setSaleAmt(
								Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString()));
						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setPromoQty(0);
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[11].toString());
						objItemWiseConsumption.setCostCenterName(obj[12].toString());
						costCenterCd = obj[11].toString();
						costCenterNm = obj[12].toString();
						objItemWiseConsumption.setExternalCode(obj[10].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);

					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}
					sbSqlMod.setLength(0);
					String dblDiscAmount = "b.dblDiscAmt";
					if (printZeroAmountModi.equalsIgnoreCase("Yes")) {
						// for Sales Qty for bill modifier live data

						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
						    + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType!='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						    + " group by b.strItemCode,b.strModifierName ");
					} else {
						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
						    + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e"
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType!='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' AND b.dblamount>0"
						    + " group by b.strItemCode,b.strModifierName ");
					}

					List listSqlMod = objBaseService.funGetList(sbSqlMod, "sql");
					if (listSqlMod.size() > 0) {
						for (int j = 0; j < listSqlMod.size(); j++) 
						{
							Object[] objMod = (Object[]) listSqlMod.get(j);
							if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString())) 
							{
								objItemWiseConsumption = hmItemWiseConsumption
										.get(objMod[0].toString() + "!" + objMod[1].toString());
								objItemWiseConsumption.setSaleQty(
										objItemWiseConsumption.getSaleQty() + Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt()
										+ ((Double.parseDouble(objMod[3].toString()))
												- Double.parseDouble(objMod[6].toString())));
								objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal()
										+ Double.parseDouble(objMod[3].toString()));

							} 
							else 
							{
								sqlNo++;
								objItemWiseConsumption = new clsPOSItemWiseConsumption();
								objItemWiseConsumption.setItemCode(objMod[0].toString());
								objItemWiseConsumption.setItemName(objMod[1].toString());
								objItemWiseConsumption.setSubGroupName(objMod[7].toString());
								objItemWiseConsumption.setGroupName(objMod[8].toString());
								objItemWiseConsumption.setSaleQty(Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setComplimentaryQty(0);
								objItemWiseConsumption.setNcQty(0);
								objItemWiseConsumption.setSubTotal(Double.parseDouble(objMod[3].toString()));
								objItemWiseConsumption.setDiscAmt(Double.parseDouble(objMod[6].toString()));
								objItemWiseConsumption.setSaleAmt(Double.parseDouble(objMod[3].toString())
										- Double.parseDouble(objMod[6].toString()));
								objItemWiseConsumption.setPOSName(objMod[5].toString());
								objItemWiseConsumption.setPromoQty(0);
								objItemWiseConsumption.setSeqNo(sqlNo);
								objItemWiseConsumption.setCostCenterCode(costCenterCd);
								objItemWiseConsumption.setCostCenterName(costCenterNm);
								objItemWiseConsumption.setExternalCode(objMod[10].toString());
								double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
								// objItemWiseConsumption.setTotalQty(totalRowQty);
								objItemWiseConsumption.setTotalQty(0);

							}
							if (null != objItemWiseConsumption) {
								hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(),
										objItemWiseConsumption);
							}

						}
					}

				}
			}

			// for Sales Qty for bill detail q data
			sbSql.setLength(0);

			sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), "+amount+","+rate+", e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE a.strBillNo=b.strBillNo  "
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
			    + "AND a.strPOSCode=e.strPosCode  "
			    + "AND b.strItemCode=f.strItemCode  "
			    + "AND f.strSubGroupCode=g.strSubGroupCode  "
			    + "AND g.strGroupCode=h.strGroupCode  "
			    + "and b.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + " and i.strHourlyPricing='NO' ");
			
			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,b.strItemName");

			List listSqlQBill = objBaseService.funGetList(sbSql, "sql");
			if (listSqlQBill.size() > 0) 
			{
				for (int i = 0; i < listSqlQBill.size(); i++) 
				{
					Object[] obj = (Object[]) listSqlQBill.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) 
					{
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						objItemWiseConsumption.setSaleQty(
								objItemWiseConsumption.getSaleQty() + Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt()
								+ (Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString())));
						objItemWiseConsumption.setSubTotal(
								objItemWiseConsumption.getSubTotal() + Double.parseDouble(obj[3].toString()));

					} 
					else 
					{
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setSaleQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setComplimentaryQty(0);
						objItemWiseConsumption.setNcQty(0);
						objItemWiseConsumption.setSubTotal(Double.parseDouble(obj[3].toString()));
						objItemWiseConsumption.setDiscAmt(Double.parseDouble(obj[6].toString()));
						objItemWiseConsumption.setSaleAmt(
								Double.parseDouble(obj[3].toString()) - Double.parseDouble(obj[6].toString()));
						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setPromoQty(0);
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[11].toString());
						objItemWiseConsumption.setCostCenterName(obj[12].toString());
						costCenterCd = obj[11].toString();
						costCenterNm = obj[12].toString();
						objItemWiseConsumption.setExternalCode(obj[10].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));

					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}
					sbSqlMod.setLength(0);
					String dblDiscAmount = "b.dblDiscAmt";
					if (printZeroAmountModi.equalsIgnoreCase("Yes"))// Tjs brew works dont want modifiers details
					{
						// Code for Sales Qty for modifier live & q data

						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
						    + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType!='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
						    + " and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						    + " group by b.strItemCode,b.strModifierName ");
					} else {
						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
						    + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType!='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
						    + " and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' And  b.dblamount >0"
						    + " group by b.strItemCode,b.strModifierName ");
					}
					sbSqlMod.append(sbFilters);

					List listQBillMod = objBaseService.funGetList(sbSqlMod, "sql");
					if (listQBillMod.size() > 0) {
						for (int j = 0; j < listQBillMod.size(); j++) 
						{
							Object[] objMod = (Object[]) listQBillMod.get(j);
							if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString())) {
								objItemWiseConsumption = hmItemWiseConsumption
										.get(objMod[0].toString() + "!" + objMod[1].toString());
								objItemWiseConsumption.setSaleQty(
										objItemWiseConsumption.getSaleQty() + Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setSaleAmt(
										objItemWiseConsumption.getSaleAmt() + (Double.parseDouble(objMod[3].toString())
												- Double.parseDouble(objMod[6].toString())));
								objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal()
										+ Double.parseDouble(objMod[3].toString()));
								// objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() +
								// rsSalesMod.getDouble(3));
							} else {
								sqlNo++;
								objItemWiseConsumption = new clsPOSItemWiseConsumption();
								objItemWiseConsumption.setItemCode(objMod[0].toString());
								objItemWiseConsumption.setItemName(objMod[1].toString());
								objItemWiseConsumption.setSubGroupName(objMod[7].toString());
								objItemWiseConsumption.setGroupName(objMod[8].toString());
								objItemWiseConsumption.setSaleQty(Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setComplimentaryQty(0);
								objItemWiseConsumption.setNcQty(0);
								objItemWiseConsumption.setSubTotal(Double.parseDouble(objMod[3].toString()));
								objItemWiseConsumption.setDiscAmt(Double.parseDouble(objMod[6].toString()));
								objItemWiseConsumption.setSaleAmt(Double.parseDouble(objMod[3].toString())
										- Double.parseDouble(objMod[6].toString()));
								objItemWiseConsumption.setPOSName(objMod[5].toString());
								objItemWiseConsumption.setPromoQty(0);
								objItemWiseConsumption.setSeqNo(sqlNo);
								objItemWiseConsumption.setCostCenterCode(costCenterCd);
								objItemWiseConsumption.setCostCenterName(costCenterNm);
								objItemWiseConsumption.setExternalCode(objMod[10].toString());
								double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
								// objItemWiseConsumption.setTotalQty(totalRowQty);
								objItemWiseConsumption.setTotalQty(0);
								objItemWiseConsumption.setItemRate(Double.parseDouble(objMod[4].toString()));
							}
							if (null != objItemWiseConsumption) {
								hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(),
										objItemWiseConsumption);
							}
						}

					}
				}
			}

			// Code for Complimentary Qty for live & q bill detail and bill modifier data
			// for Complimentary Qty for live bill detail
			sbSql.setLength(0);

			sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity), "+amount+","+rate+",e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE a.strBillNo=b.strBillNo  "
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
			    + "AND a.strPOSCode=e.strPosCode  "
			    + "AND b.strItemCode=f.strItemCode  "
			    + "AND f.strSubGroupCode=g.strSubGroupCode  "
			    + "AND g.strGroupCode=h.strGroupCode  "
			    + "and b.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + " and i.strHourlyPricing='NO' ");

			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			
			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,b.strItemName");
			// System.out.println(sbSql);

			List listSqlLiveBillComplimentary = objBaseService.funGetList(sbSql, "sql");
			if (listSqlLiveBillComplimentary.size() > 0) {
				for (int i = 0; i < listSqlLiveBillComplimentary.size(); i++) 
				{
					Object[] obj = (Object[]) listSqlLiveBillComplimentary.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) {
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						objItemWiseConsumption.setComplimentaryQty(
								objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble(obj[2].toString()));

						objItemWiseConsumption.setSaleQty(
								objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));

					} else {
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleQty(0);
						objItemWiseConsumption.setNcQty(0);

						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setPromoQty(0);
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[11].toString());
						objItemWiseConsumption.setCostCenterName(obj[12].toString());
						costCenterCd = obj[11].toString();
						costCenterNm = obj[12].toString();
						objItemWiseConsumption.setExternalCode(obj[10].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));
						/// System.out.println("New= " + rsComplimentary.getString(1) +
						/// objItemWiseConsumption.getComplimentaryQty());
					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}

					sbSqlMod.setLength(0);
					String dblDiscAmount = "b.dblDiscAmt";
					if (printZeroAmountModi.equalsIgnoreCase("Yes"))// Tjs brew works dont want modifiers details
					{
						// for Complimentary Qty for live bill modifier

						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
						    + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo  "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType='Complementary' "
						    + " and left(b.strItemCode,7)='" +  obj[0].toString() + "' "
						    + " and a.strBillNo='" +  obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						    + " group by b.strItemCode,b.strModifierName ");
					} else {
						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
						    + " from tblbillhd a,tblbillmodifierdtl b, tblbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
						    + " and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' "
						    + " AND '" + toDate + "' AND  b.dblamount >0"
						    + " group by b.strItemCode,b.strModifierName ");
					}
					sbSqlMod.append(sbFilters);
					// System.out.println(sbSqlMod);

					List listSqlQbillModComplimentary = objBaseService.funGetList(sbSqlMod, "sql");
					if (listSqlQbillModComplimentary.size() > 0) {
						for (int j = 0; j < listSqlQbillModComplimentary.size(); j++) {
							Object[] objMod = (Object[]) listSqlQbillModComplimentary.get(j);
							if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString())) {
								objItemWiseConsumption = hmItemWiseConsumption
										.get(objMod[0].toString() + "!" + objMod[1].toString());
								objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty()
										+ Double.parseDouble(objMod[2].toString()));

							} else {
								sqlNo++;
								objItemWiseConsumption = new clsPOSItemWiseConsumption();
								objItemWiseConsumption.setItemCode(objMod[0].toString());
								objItemWiseConsumption.setItemName(objMod[1].toString());
								objItemWiseConsumption.setSubGroupName(objMod[7].toString());
								objItemWiseConsumption.setGroupName(objMod[8].toString());
								objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setSaleQty(0);
								objItemWiseConsumption.setNcQty(0);

								objItemWiseConsumption.setPOSName(objMod[5].toString());
								objItemWiseConsumption.setSeqNo(sqlNo);
								objItemWiseConsumption.setPromoQty(0);
								objItemWiseConsumption.setCostCenterCode(costCenterCd);
								objItemWiseConsumption.setCostCenterName(costCenterNm);
								objItemWiseConsumption.setExternalCode(objMod[10].toString());
								// System.out.println("New= " + rsModComplimentary.getString(1) +
								// objItemWiseConsumption.getComplimentaryQty());
								double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
								// objItemWiseConsumption.setTotalQty(totalRowQty);
								objItemWiseConsumption.setTotalQty(0);
								objItemWiseConsumption.setItemRate(Double.parseDouble(objMod[4].toString()));
							}
							if (null != objItemWiseConsumption) {
								hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(),
										objItemWiseConsumption);
							}
						}
					}

				}
			}

			// for Complimentary Qty for q bill details
			sbSql.setLength(0);

			sbSql.append("SELECT b.stritemcode,upper(b.stritemname), SUM(b.dblQuantity),"+amount+","+rate+",e.strposname,"+discAmt+""
			    + ",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE a.strBillNo=b.strBillNo  "
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
			    + "AND a.strPOSCode=e.strPosCode  "
			    + "AND b.strItemCode=f.strItemCode  "
			    + "AND f.strSubGroupCode=g.strSubGroupCode  "
			    + "AND g.strGroupCode=h.strGroupCode  "
			    + "and b.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + " and i.strHourlyPricing='NO' ");
			
			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and h.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,b.strItemName");
			// System.out.println(sbSql);

			List listQBillComplimentary = objBaseService.funGetList(sbSql, "sql");
			if (listQBillComplimentary.size() > 0) {
				for (int i = 0; i < listQBillComplimentary.size(); i++) {
					Object[] obj = (Object[]) listQBillComplimentary.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) {
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						objItemWiseConsumption.setComplimentaryQty(
								objItemWiseConsumption.getComplimentaryQty() + Double.parseDouble(obj[2].toString()));

						objItemWiseConsumption.setSaleQty(
								objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));

					} else {
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleQty(0);
						objItemWiseConsumption.setNcQty(0);

						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setPromoQty(0);
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[11].toString());
						objItemWiseConsumption.setCostCenterName(obj[12].toString());
						costCenterCd = obj[11].toString();
						costCenterNm = obj[12].toString();
						objItemWiseConsumption.setExternalCode(obj[10].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));
					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}

					sbSqlMod.setLength(0);
					String dblDiscAmount = "b.dblDiscAmt";
					if (printZeroAmountModi.equalsIgnoreCase("Yes"))// Tjs brew works dont want modifiers details
					{
						// for Complimentary Qty for q bill modifier

						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
						    + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString() + "' "
						    + " and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						    + " group by b.strItemCode,b.strModifierName ");
					} else {
						sbSqlMod.append("select b.strItemCode,upper(b.strModifierName),b.dblQuantity,"+amount+","+rate+""
						    + " ,e.strposname,"+dblDiscAmount+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode"
						    + " from tblqbillhd a,tblqbillmodifierdtl b, tblqbillsettlementdtl c,tblsettelmenthd d,tblposmaster e "
						    + " ,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
						    + " where a.strBillNo=b.strBillNo "
						    + " and date(a.dteBillDate)=date(b.dteBillDate) "
						    + " and a.strBillNo=c.strBillNo "
						    + " and date(a.dteBillDate)=date(c.dteBillDate) "
						    + " and c.strSettlementCode=d.strSettelmentCode "
						    + " and a.strPOSCode=e.strPosCode "
						    + " and left(b.strItemCode,7)=f.strItemCode "
						    + " and f.strSubGroupCode=g.strSubGroupCode "
						    + " and g.strGroupCode=h.strGroupCode "
						    + " and d.strSettelmentType='Complementary' "
						    + " and left(b.strItemCode,7)='" + obj[0].toString()  + "' "
						    + " and a.strBillNo='" + obj[9].toString() + "' "
						    + " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						    + " AND  b.dblamount >0"
						    + " group by b.strItemCode,b.strModifierName ");
					}
					sbSqlMod.append(sbFilters);
					// System.out.println(sbSqlMod);

					List listQBillModComplimentary = objBaseService.funGetList(sbSqlMod, "sql");
					if (listQBillModComplimentary.size() > 0) {
						for (int j = 0; j < listQBillModComplimentary.size(); j++) {
							Object[] objMod = (Object[]) listQBillModComplimentary.get(j);
							if (null != hmItemWiseConsumption.get(objMod[0].toString() + "!" + objMod[1].toString())) {
								objItemWiseConsumption = hmItemWiseConsumption
										.get(objMod[0].toString() + "!" + objMod[1].toString());
								objItemWiseConsumption.setComplimentaryQty(objItemWiseConsumption.getComplimentaryQty()
										+ Double.parseDouble(objMod[2].toString()));

							} else {
								sqlNo++;
								objItemWiseConsumption = new clsPOSItemWiseConsumption();
								objItemWiseConsumption.setItemCode(objMod[0].toString());
								objItemWiseConsumption.setItemName(objMod[0].toString());
								objItemWiseConsumption.setSubGroupName(objMod[7].toString());
								objItemWiseConsumption.setGroupName(objMod[8].toString());
								objItemWiseConsumption.setComplimentaryQty(Double.parseDouble(objMod[2].toString()));
								objItemWiseConsumption.setSaleQty(0);
								objItemWiseConsumption.setNcQty(0);

								objItemWiseConsumption.setPOSName(objMod[5].toString());
								objItemWiseConsumption.setPromoQty(0);
								objItemWiseConsumption.setSeqNo(sqlNo);
								objItemWiseConsumption.setCostCenterCode(costCenterCd);
								objItemWiseConsumption.setCostCenterName(costCenterNm);
								objItemWiseConsumption.setExternalCode(objMod[10].toString());
								double totalRowQty = Double.parseDouble(objMod[2].toString()) + 0 + 0 + 0;
								// objItemWiseConsumption.setTotalQty(totalRowQty);
								objItemWiseConsumption.setTotalQty(0);
								objItemWiseConsumption.setItemRate(Double.parseDouble(objMod[4].toString()));
							}
							if (null != objItemWiseConsumption) {
								hmItemWiseConsumption.put(objMod[0].toString() + "!" + objMod[1].toString(),
										objItemWiseConsumption);
							}
						}
					}

				}
			}

			// Code for NC Qty
			sbSql.setLength(0);

			sbSql.append("SELECT a.stritemcode,upper(b.stritemname), SUM(a.dblQuantity), SUM(a.dblQuantity*a.dblRate),a.dblRate, c.strposname,0 AS DiscAmt,d.strSubGroupName,e.strGroupName,b.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblnonchargablekot a, tblitemmaster b, tblposmaster c,tblsubgrouphd d,tblgrouphd e,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE LEFT(a.strItemCode,7)=b.strItemCode  "
			    + "AND a.strPOSCode=c.strPosCode  "
			    + "AND b.strSubGroupCode=d.strSubGroupCode  "
			    + "AND d.strGroupCode=e.strGroupCode  "
			    + "and a.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "and i.strCostCenterCode=j.strCostCenterCode "
			    + "AND DATE(a.dteNCKOTDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + " and i.strHourlyPricing='NO' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}

			sbSql.append(" group by a.strItemCode order by j.strCostCenterCode,b.strItemName");
			// System.out.println(sbSql);

			List listNCKOT = objBaseService.funGetList(sbSql, "sql");
			if (listNCKOT.size() > 0) {
				for (int i = 0; i < listNCKOT.size(); i++) {
					Object[] obj = (Object[]) listNCKOT.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) {
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						objItemWiseConsumption
								.setNcQty(objItemWiseConsumption.getNcQty() + Double.parseDouble(obj[2].toString()));

					} else {
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setNcQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleQty(0);
						objItemWiseConsumption.setComplimentaryQty(0);

						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setPromoQty(0);
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[10].toString());
						objItemWiseConsumption.setCostCenterName(obj[11].toString());
						costCenterCd = obj[10].toString();
						costCenterNm = obj[11].toString();
						objItemWiseConsumption.setExternalCode(obj[9].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));
					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}
				}
			}

			// Code for promotion Qty for Q
			sbSql.setLength(0);
			sbSql.append("SELECT b.strItemCode,upper(c.strItemName), SUM(b.dblQuantity), "+amount+","+rate+",f.strPosName,0,d.strSubGroupName,e.strGroupName,c.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblqbillhd a,tblqbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE a.strBillNo=b.strBillNo  "
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
			    + "AND b.strItemCode=c.strItemCode  "
			    + "AND c.strSubGroupCode=d.strSubGroupCode  "
			    + "AND d.strGroupCode=e.strGroupCode  "
			    + "AND a.strPOSCode=f.strPosCode  "
			    + "and b.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + " and i.strHourlyPricing='NO' ");
			
			 sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
					    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSql.append(" group by b.strItemCode  order by j.strCostCenterCode,c.strItemName");
			// System.out.println(sbSql);

			List listPromotionQQty = objBaseService.funGetList(sbSql, "sql");
			if (listPromotionQQty.size() > 0) {
				for (int i = 0; i < listPromotionQQty.size(); i++) {
					Object[] obj = (Object[]) listPromotionQQty.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) {
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						double saleQty = objItemWiseConsumption.getSaleQty();
						if (saleQty > 0) {
							objItemWiseConsumption.setSaleQty(
									objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));
							objItemWiseConsumption.setTotalQty(
									objItemWiseConsumption.getTotalQty() - Double.parseDouble(obj[2].toString()));
						}

						objItemWiseConsumption.setPromoQty(
								objItemWiseConsumption.getPromoQty() + Double.parseDouble(obj[2].toString()));
						double qty = objItemWiseConsumption.getTotalQty();
						// objItemWiseConsumption.setTotalQty(qty +
						// objItemWiseConsumption.getPromoQty());
					} else {
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setNcQty(0);
						objItemWiseConsumption.setPromoQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleQty(0);
						objItemWiseConsumption.setComplimentaryQty(0);

						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[10].toString());
						objItemWiseConsumption.setCostCenterName(obj[11].toString());
						objItemWiseConsumption.setExternalCode(obj[9].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));

					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}
				}
			}

			// Code for promotion Qty for live
			sbSql.setLength(0);
			sbSql.append("SELECT b.strItemCode,upper(c.strItemName), SUM(b.dblQuantity), "+amount+","+rate+",f.strPosName,0,d.strSubGroupName,e.strGroupName,c.strExternalCode "
			    + ",i.strCostCenterCode,j.strCostCenterName "
			    + "FROM tblbillhd a,tblbillpromotiondtl b,tblitemmaster c,tblsubgrouphd d,tblgrouphd e,tblposmaster f,tblmenuitempricingdtl i,tblcostcentermaster j "
			    + "WHERE a.strBillNo=b.strBillNo  "
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
			    + "AND b.strItemCode=c.strItemCode  "
			    + "AND c.strSubGroupCode=d.strSubGroupCode  "
			    + "AND d.strGroupCode=e.strGroupCode  "
			    + "AND a.strPOSCode=f.strPosCode  "
			    + "and b.strItemCode=i.strItemCode "
			    + "and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + " and i.strHourlyPricing='NO' ");
			
			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSql.append(" AND a.strPOSCode = '" + posCode + "' ");
			}

			if (!groupCode.equalsIgnoreCase("All")) {
				sbSql.append(" and e.strGroupCode = '" + groupCode + "' ");
			}

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
			}
			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSql.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSql.append(" group by b.strItemCode order by j.strCostCenterCode,c.strItemName");
			// System.out.println(sbSql);

			List listQBillPromotion = objBaseService.funGetList(sbSql, "sql");
			if (listQBillPromotion.size() > 0) {
				for (int i = 0; i < listQBillPromotion.size(); i++) {
					Object[] obj = (Object[]) listQBillPromotion.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString())) {
						objItemWiseConsumption = hmItemWiseConsumption.get(obj[0].toString() + "!" + obj[1].toString());
						double saleQty = objItemWiseConsumption.getSaleQty();
						if (saleQty > 0) {
							objItemWiseConsumption.setSaleQty(
									objItemWiseConsumption.getSaleQty() - Double.parseDouble(obj[2].toString()));
							objItemWiseConsumption.setTotalQty(
									objItemWiseConsumption.getTotalQty() - Double.parseDouble(obj[2].toString()));
						}

						objItemWiseConsumption.setPromoQty(
								objItemWiseConsumption.getPromoQty() + Double.parseDouble(obj[2].toString()));
						double qty = objItemWiseConsumption.getTotalQty();
						// objItemWiseConsumption.setTotalQty(qty +
						// objItemWiseConsumption.getPromoQty());
					} else {
						sqlNo++;
						objItemWiseConsumption = new clsPOSItemWiseConsumption();
						objItemWiseConsumption.setItemCode(obj[0].toString());
						objItemWiseConsumption.setItemName(obj[1].toString());
						objItemWiseConsumption.setSubGroupName(obj[7].toString());
						objItemWiseConsumption.setGroupName(obj[8].toString());
						objItemWiseConsumption.setNcQty(0);
						objItemWiseConsumption.setPromoQty(Double.parseDouble(obj[2].toString()));
						objItemWiseConsumption.setSaleQty(0);
						objItemWiseConsumption.setComplimentaryQty(0);

						objItemWiseConsumption.setPOSName(obj[5].toString());
						objItemWiseConsumption.setSeqNo(sqlNo);
						objItemWiseConsumption.setCostCenterCode(obj[10].toString());
						objItemWiseConsumption.setCostCenterName(obj[11].toString());
						objItemWiseConsumption.setExternalCode(obj[9].toString());
						double totalRowQty = Double.parseDouble(obj[2].toString()) + 0 + 0 + 0;
						// objItemWiseConsumption.setTotalQty(totalRowQty);
						objItemWiseConsumption.setTotalQty(0);
						objItemWiseConsumption.setItemRate(Double.parseDouble(obj[4].toString()));
					}
					if (null != objItemWiseConsumption) {
						hmItemWiseConsumption.put(obj[0].toString() + "!" + obj[1].toString(), objItemWiseConsumption);
					}
				}
			}

			for (Map.Entry<String, clsPOSItemWiseConsumption> entry : hmItemWiseConsumption.entrySet()) {
				clsPOSItemWiseConsumption objItemComp = entry.getValue();
				double totalRowQty = objItemComp.getSaleQty() + objItemComp.getComplimentaryQty()
						+ objItemComp.getNcQty() + objItemComp.getPromoQty();
				objItemComp.setTotalQty(totalRowQty);
				list.add(objItemComp);
			}

			// sort list
			// Collections.sort(list,
			// clsItemWiseConsumption.comparatorItemConsumptionColumnDtl);
			Comparator<clsPOSItemWiseConsumption> posNameComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					return o1.getPOSName().compareToIgnoreCase(o2.getPOSName());
				}
			};
			Comparator<clsPOSItemWiseConsumption> costCenterNameComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					return o1.getCostCenterName().compareToIgnoreCase(o2.getCostCenterName());
				}
			};

			Comparator<clsPOSItemWiseConsumption> groupNameComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
				}
			};

			Comparator<clsPOSItemWiseConsumption> subGroupNameComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					return o1.getSubGroupName().compareToIgnoreCase(o2.getSubGroupName());
				}
			};

			Comparator<clsPOSItemWiseConsumption> itemCodeComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					return o1.getItemName().compareToIgnoreCase(o2.getItemName());
				}
			};

			Comparator<clsPOSItemWiseConsumption> seqNoComparator = new Comparator<clsPOSItemWiseConsumption>() {

				@Override
				public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2) {
					int seqNo1 = o1.getSeqNo();
					int seqNo2 = o2.getSeqNo();

					if (seqNo1 == seqNo2) {
						return 0;
					} else if (seqNo1 > seqNo2) {
						return 1;
					} else {
						return -1;
					}
				}
			};

			Collections.sort(list, new clsPOSItemConsumptionComparator(posNameComparator, costCenterNameComparator,
					groupNameComparator, subGroupNameComparator, itemCodeComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	
	
	
	public List funProcessQFileSettlementWiseReport(String posCode, String fromDate, String toDate) {

		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sqlFilter = new StringBuilder();
		DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
		DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
		List listSqlQFileData = new ArrayList();
		try {

			sbSqlQFile.setLength(0);
			sbSqlQFile.append(
					"select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
							+ ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
							+ "from tblsettelmenthd a "
							+ "left outer join tblqbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '"
							+ fromDate + "' AND '" + toDate + "' "
							+ "left outer join tblqbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");

			sqlFilter.append(" where a.strSettelmentType!='Complementary' " + "and a.strApplicable='Yes' ");

			if (!"All".equalsIgnoreCase(posCode)) {
				sqlFilter.append("and  c.strPosCode='" + posCode + "' ");
			}

			sqlFilter.append("group by a.strSettelmentCode " + "order by b.dblSettlementAmt desc ");

			sbSqlQFile.append(sqlFilter);

			listSqlQFileData = objBaseService.funGetList(sbSqlQFile, "sql");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSqlQFileData;
	}

	public List funProcessLiveSettlementWiseReport(String posCode, String fromDate, String toDate) {
		StringBuilder sbSqlLive = new StringBuilder();

		StringBuilder sqlFilter = new StringBuilder();
		DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
		DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
		List listSqlLiveData = new ArrayList();
		try {
			sbSqlLive.setLength(0);
			sbSqlLive.append(
					"select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
							+ ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
							+ "from tblsettelmenthd a "
							+ "left outer join tblbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '"
							+ fromDate + "' AND '" + toDate + "' "
							+ "left outer join tblbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");

			sqlFilter.append(" where a.strSettelmentType!='Complementary' " + "and a.strApplicable='Yes' ");

			if (!"All".equalsIgnoreCase(posCode)) {
				sqlFilter.append("and  c.strPosCode='" + posCode + "' ");
			}

			sqlFilter.append("group by a.strSettelmentCode " + "order by b.dblSettlementAmt desc ");

			sbSqlLive.append(sqlFilter);

			listSqlLiveData = objBaseService.funGetList(sbSqlLive, "sql");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSqlLiveData;
	}

	public List<clsPOSGroupSubGroupItemBean> funProcessSubGroupWiseReport(String posCode, String fromDate,
			String toDate, String strUserCode, String strShiftNo,String enableShiftYN) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();
		List<clsPOSGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsPOSGroupSubGroupItemBean>();
		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);
		sqlModLive.setLength(0);
		sqlModQFile.setLength(0);

		try {
			sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
					+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
					+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
					+ "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d " + ",tblposmaster f "
					+ "where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPOSCode  " + " and a.strClientCode=b.strClientCode   "
					+ "and b.strItemCode=d.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode ");

			sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
					+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
					+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
					+ "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d " + ",tblposmaster f "
					+ "where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPOSCode " + " and a.strClientCode=b.strClientCode   "
					+ "and b.strItemCode=d.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode ");

			sqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
					+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d" + ",tblsubgrouphd c"
					+ " where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode  " + " and a.strClientCode=b.strClientCode  "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode " + " and d.strSubGroupCode=c.strSubGroupCode "
					+ " and b.dblamount>0 ");

			sqlModQFile.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
					+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt) "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d" + ",tblsubgrouphd c"
					+ " where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode " + " and a.strClientCode=b.strClientCode  "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode " + " and d.strSubGroupCode=c.strSubGroupCode "
					+ " and b.dblamount>0 ");

			sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			sbSqlFilters.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters);
			sqlModQFile.append(sbSqlFilters);

			List listSqlLiveData = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLiveData.size() > 0) {
				for (int i = 0; i < listSqlLiveData.size(); i++) {
					Object[] obj = (Object[]) listSqlLiveData.get(i);
					clsPOSGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString()); // SubGroup Code
					objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString()); // SubGroup Name
					objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString())); // Qty
					objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString())); // sub total
					objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString())); // amt-disAmt
					objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString())); // dis amt
					objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString()); // POS Name

					listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
				}
			}

			List listLiveModData = objBaseService.funGetList(sqlModLive, "sql");
			if (listLiveModData.size() > 0) {
				for (int i = 0; i < listLiveModData.size(); i++) {
					Object[] obj = (Object[]) listLiveModData.get(i);
					clsPOSGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString()); // SubGroup Code
					objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString()); // SubGroup Name
					objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString())); // Qty
					objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString())); // sub total
					objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString())); // amt-disAmt
					objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString())); // dis amt
					objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString()); // POS Name

					listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
				}
			}

			List listQfileData = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listQfileData.size() > 0) {
				for (int i = 0; i < listQfileData.size(); i++) {
					Object[] obj = (Object[]) listQfileData.get(i);
					clsPOSGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString()); // SubGroup Code
					objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString()); // SubGroup Name
					objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString())); // Qty
					objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString())); // sub total
					objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString())); // amt-disAmt
					objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString())); // dis amt
					objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString()); // POS Name

					listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
				}
			}

			List listQfileModData = objBaseService.funGetList(sqlModQFile, "sql");
			if (listQfileModData.size() > 0) {
				for (int i = 0; i < listQfileModData.size(); i++) {
					Object[] obj = (Object[]) listQfileModData.get(i);
					clsPOSGroupSubGroupItemBean objBeanGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objBeanGroupSubGroupItemBean.setStrItemCode(obj[0].toString()); // SubGroup Code
					objBeanGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());// SubGroup Name
					objBeanGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[2].toString())); // Qty
					objBeanGroupSubGroupItemBean.setDblSubTotal(Double.parseDouble(obj[3].toString())); // sub total
					objBeanGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[7].toString())); // amt-disAmt
					objBeanGroupSubGroupItemBean.setDblDisAmt(Double.parseDouble(obj[8].toString())); // dis amt
					objBeanGroupSubGroupItemBean.setStrPOSName(obj[4].toString()); // POS Name

					listOfGroupSubGroupWiseSales.add(objBeanGroupSubGroupItemBean);
				}
			}

			Comparator<clsPOSGroupSubGroupItemBean> subGroupNameComparator = new Comparator<clsPOSGroupSubGroupItemBean>() {

				@Override
				public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2) {
					return o1.getStrSubGroupName().compareToIgnoreCase(o2.getStrSubGroupName());
				}
			};

			Collections.sort(listOfGroupSubGroupWiseSales, subGroupNameComparator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfGroupSubGroupWiseSales;
	}

	public List<clsPOSTaxCalculationDtls> funProcessTaxWiseReport(String posCode, String fromDate, String toDate,
			String strShiftNo) {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder sqlQBuilder = new StringBuilder();
		String enableShiftYN="N";
		List<clsPOSTaxCalculationDtls> listOfTaxData = new ArrayList<clsPOSTaxCalculationDtls>();
		try {
			// live
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"SELECT a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate, b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname\n"
							+ "FROM tblBillHd a\n"
							+ "INNER JOIN tblBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
							+ "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode\n" + "LEFT OUTER\n"
							+ "JOIN tblposmaster d ON a.strposcode=d.strposcode\n"
							+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and  '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
			}

			List lisSqlLive = objBaseService.funGetList(sqlBuilder, "sql");

			if (lisSqlLive.size() > 0) {
				for (int i = 0; i < lisSqlLive.size(); i++) {
					Object[] obj = (Object[]) lisSqlLive.get(i);
					clsPOSTaxCalculationDtls objBeanTaxCalculation = new clsPOSTaxCalculationDtls();
					objBeanTaxCalculation.setStrBillNo(obj[0].toString());
					objBeanTaxCalculation.setDteBillDate(obj[1].toString());
					objBeanTaxCalculation.setTaxCode(obj[2].toString());
					objBeanTaxCalculation.setStrTaxDesc(obj[3].toString());
					objBeanTaxCalculation.setStrPOSCode(obj[4].toString());
					objBeanTaxCalculation.setTaxableAmount(Double.parseDouble(obj[5].toString()));
					objBeanTaxCalculation.setTaxAmount(Double.parseDouble(obj[6].toString()));
					objBeanTaxCalculation.setDblGrandTotal(Double.parseDouble(obj[7].toString()));
					objBeanTaxCalculation.setStrPOSName(obj[8].toString());

					listOfTaxData.add(objBeanTaxCalculation);
				}
			}

			sqlQBuilder.setLength(0);
			sqlQBuilder.append(
					"SELECT a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate, b.strTaxCode, c.strTaxDesc, a.strPOSCode, b.dblTaxableAmount, b.dblTaxAmount, a.dblGrandTotal,d.strposname\n"
							+ "FROM tblqBillHd a\n"
							+ "INNER JOIN tblqBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
							+ "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode\n" + "LEFT OUTER\n"
							+ "JOIN tblposmaster d ON a.strposcode=d.strposcode\n"
							+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and  '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
			}

			List listQBillData = objBaseService.funGetList(sqlQBuilder, "sql");
			if (listQBillData.size() > 0) {
				for (int i = 0; i < listQBillData.size(); i++) {
					Object[] obj = (Object[]) listQBillData.get(i);
					clsPOSTaxCalculationDtls objBeanTaxCalculation = new clsPOSTaxCalculationDtls();
					objBeanTaxCalculation.setStrBillNo(obj[0].toString());
					objBeanTaxCalculation.setDteBillDate(obj[1].toString());
					objBeanTaxCalculation.setTaxCode(obj[2].toString());
					objBeanTaxCalculation.setStrTaxDesc(obj[3].toString());
					objBeanTaxCalculation.setStrPOSCode(obj[4].toString());
					objBeanTaxCalculation.setTaxableAmount(Double.parseDouble(obj[5].toString()));
					objBeanTaxCalculation.setTaxAmount(Double.parseDouble(obj[6].toString()));
					objBeanTaxCalculation.setDblGrandTotal(Double.parseDouble(obj[7].toString()));
					objBeanTaxCalculation.setStrPOSName(obj[8].toString());

					listOfTaxData.add(objBeanTaxCalculation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfTaxData;
	}

	public List<clsPOSVoidBillDtl> funProcessVoidBillSummaryReport(String posCode, String fromDate, String toDate,
			String strShiftNo, String reasonCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsPOSVoidBillDtl> listOfVoidBillData = new ArrayList<clsPOSVoidBillDtl>();
		
		try {
			// Bill detail data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate, "
							+ " Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime, "
							+ " SUM(b.dblAmount) AS BillAmount,a.strReasonName as Reason,a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,a.strRemark,a.strVoidBillType "
							+ " from tblvoidbillhd a,tblvoidbilldtl b " + " where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and b.strTransType='VB' "
							+ " and a.strTransType='VB' " + " and (a.dblModifiedAmount)>0 "
							+ " and Date(a.dteModifyVoidBill)  Between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");	
			}
			sqlBuilder.append(" group by a.strBillNo ");

			List listLiveVoidBillData = objBaseService.funGetList(sqlBuilder, "sql");

			if (listLiveVoidBillData.size() > 0) {
				for (int i = 0; i < listLiveVoidBillData.size(); i++) {
					Object[] obj = (Object[]) listLiveVoidBillData.get(i);
					String billDate = obj[1].toString();
					String dateParts[] = billDate.split("-");
					String dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

					String voidedBillDate = obj[1].toString();
					String dateParts1[] = voidedBillDate.split("-");
					String dteVoidedBillDate = dateParts1[2] + "-" + dateParts1[1] + "-" + dateParts1[0];

					clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
					objVoidBill.setStrBillNo(obj[0].toString()); // BillNo
					objVoidBill.setDteBillDate(dteBillDate); // Bill Date
					objVoidBill.setStrWaiterNo(dteVoidedBillDate); // Voided Date
					objVoidBill.setStrTableNo(obj[3].toString()); // Entry Time
					objVoidBill.setStrSettlementCode(obj[4].toString()); // Voided Time
					objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString())); // Bill Amount
					objVoidBill.setStrReasonName(obj[6].toString()); // Reason
					objVoidBill.setStrVoidedUser(obj[7].toString()); // User voided
					objVoidBill.setStrUserCreated(obj[8].toString()); // User Created
					objVoidBill.setStrRemarks(obj[9].toString()); // Remarks
					objVoidBill.setStrVoidBillType(obj[10].toString()); // Void Bill Type

					listOfVoidBillData.add(objVoidBill);
				}
			}

			// Bill Modifier data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strBillNo,Date(a.dteBillDate) as BillDate,Date(a.dteModifyVoidBill) as VoidedDate "
							+ " ,Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime "
							+ " ,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
							+ " from tblvoidbillhd a, tblvoidmodifierdtl b " + " where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and a.strTransType='VB' "
							+ " and (a.dblModifiedAmount)>0 " + " and Date(a.dteModifyVoidBill) Between '" + fromDate
							+ "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append(" and a.strPosCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append(" and a.intShiftCode='" + strShiftNo + "'  ");
			}
			sqlBuilder.append(" group by a.strBillNo  ");

			List listQBillVoidData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillVoidData.size() > 0) {
				for (int i = 0; i < listQBillVoidData.size(); i++) {
					Object[] obj = (Object[]) listQBillVoidData.get(i);
					String billDate = obj[1].toString();
					String dateParts[] = billDate.split("-");
					String dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

					String voidedBillDate = obj[1].toString();
					String dateParts1[] = voidedBillDate.split("-");
					String dteVoidedBillDate = dateParts1[2] + "-" + dateParts1[1] + "-" + dateParts1[0];

					clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
					objVoidBill.setStrBillNo(obj[0].toString()); // BillNo
					objVoidBill.setDteBillDate(dteBillDate); // Bill Date
					objVoidBill.setStrWaiterNo(dteVoidedBillDate); // Voided Date
					objVoidBill.setStrTableNo(obj[3].toString()); // Entry Time
					objVoidBill.setStrSettlementCode(obj[4].toString()); // Voided Time
					objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString())); // Bill Amount
					objVoidBill.setStrReasonName(obj[6].toString()); // Reason
					objVoidBill.setStrVoidedUser(obj[7].toString()); // User Edited
					objVoidBill.setStrUserCreated(obj[8].toString()); // User Created
					objVoidBill.setStrRemarks(obj[9].toString()); // Remarks
					objVoidBill.setStrVoidBillType(obj[10].toString()); // Void Bill Type

					listOfVoidBillData.add(objVoidBill);
				}
			}

			Comparator<clsPOSVoidBillDtl> reasonNameComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getStrReasonName().compareToIgnoreCase(o2.getStrReasonName());
				}
			};

			Comparator<clsPOSVoidBillDtl> billDateComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
				}
			};

			Comparator<clsPOSVoidBillDtl> billNoComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				}
			};

			Collections.sort(listOfVoidBillData,
					new clsVoidBillComparator(reasonNameComparator, billDateComparator, billNoComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfVoidBillData;

	}

	public List<clsPOSVoidBillDtl> funProcessVoidBillDetailReport(String posCode, String fromDate, String toDate,
			String strShiftNo, String reasonCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsPOSVoidBillDtl> listOfVoidBillData = new ArrayList<clsPOSVoidBillDtl>();
		
		try {
			// Bill detail data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as BillDate,DATE_FORMAT(a.dteModifyVoidBill,'%d-%m-%Y') as VoidedDate, "
							+ " TIME_FORMAT(a.dteBillDate, '%H:%i') AS EntryTime, TIME_FORMAT(a.dteModifyVoidBill, '%H:%i') VoidedTime,b.strItemName, "
							+ " sum(b.intQuantity),sum(b.dblAmount) as BillAmount,b.strReasonName as Reason, "
							+ " a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
							+ " from tblvoidbillhd a,tblvoidbilldtl b" + " where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and b.strTransType='VB' "
							+ " and a.strTransType='VB'  " + " and (a.dblModifiedAmount)>0 "
							+ " and Date(a.dteModifyVoidBill) Between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
				
			}
			sqlBuilder.append("group by a.strBillNo,b.strItemCode ");

			List listLiveBillVoidData = objBaseService.funGetList(sqlBuilder, "sql");

			if (listLiveBillVoidData.size() > 0) {
				for (int i = 0; i < listLiveBillVoidData.size(); i++) {
					Object[] obj = (Object[]) listLiveBillVoidData.get(i);
					clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
					objVoidBill.setStrBillNo(obj[0].toString()); // BillNo
					objVoidBill.setDteBillDate(obj[1].toString()); // Bill Date
					objVoidBill.setStrWaiterNo(obj[2].toString()); // Voided Date
					objVoidBill.setStrTableNo(obj[3].toString()); // Entry Time
					objVoidBill.setStrSettlementCode(obj[4].toString()); // Voided Time
					objVoidBill.setStrItemName(obj[5].toString()); // ItemName
					objVoidBill.setIntQuantity(Double.parseDouble(obj[6].toString())); // Quantity
					objVoidBill.setDblAmount(Double.parseDouble(obj[7].toString())); // Bill Amount
					objVoidBill.setStrReasonName(obj[8].toString()); // Reason
					objVoidBill.setStrVoidedUser(obj[9].toString()); // User Edited
					objVoidBill.setStrUserCreated(obj[10].toString()); // User Created
					objVoidBill.setStrRemarks(obj[11].toString()); // Remarks
					objVoidBill.setStrVoidBillType(obj[12].toString()); // Void Bill Type

					listOfVoidBillData.add(objVoidBill);
				}
			}

			// Bill Modifier data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as BillDate,DATE_FORMAT(a.dteModifyVoidBill,'%d-%m-%Y') as VoidedDate, "
							+ " TIME_FORMAT(a.dteBillDate, '%H:%i') AS EntryTime, TIME_FORMAT(a.dteModifyVoidBill, '%H:%i') VoidedTime,b.strModifierName, "
							+ " sum(b.dblQuantity),sum(b.dblAmount) as BillAmount,ifnull(c.strReasonName,'NA') as Reason, "
							+ " a.strUserEdited AS VoidedUser,a.strUserCreated CreatedUser,b.strRemarks,a.strVoidBillType "
							+ " from tblvoidbillhd a,tblvoidmodifierdtl b "
							+ " left outer join tblreasonmaster c on b.strReasonCode=c.strReasonCode "
							+ " where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ " and a.strTransType='VB' " + " and (a.dblModifiedAmount)>0 "
							+ " and Date(a.dteModifyVoidBill) Between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append(" and a.strPosCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append(" and a.intShiftCode='" + strShiftNo + "'  ");	
			}
			sqlBuilder.append(" group by a.strBillNo,b.strModifierCode  ");

			List listQBillVoidData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillVoidData.size() > 0) {
				for (int i = 0; i < listQBillVoidData.size(); i++) {
					Object[] obj = (Object[]) listQBillVoidData.get(i);
					clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
					objVoidBill.setStrBillNo(obj[0].toString()); // BillNo
					objVoidBill.setDteBillDate(obj[1].toString()); // Bill Date
					objVoidBill.setStrWaiterNo(obj[2].toString()); // Voided Date
					objVoidBill.setStrTableNo(obj[3].toString()); // Entry Time
					objVoidBill.setStrSettlementCode(obj[4].toString()); // Voided Time
					objVoidBill.setStrItemName(obj[5].toString()); // ItemName
					objVoidBill.setIntQuantity(Double.parseDouble(obj[6].toString())); // Quantity
					objVoidBill.setDblAmount(Double.parseDouble(obj[7].toString())); // Bill Amount
					objVoidBill.setStrReasonName(obj[8].toString()); // Reason
					objVoidBill.setStrVoidedUser(obj[9].toString()); // User Edited
					objVoidBill.setStrUserCreated(obj[10].toString()); // User Created
					objVoidBill.setStrRemarks(obj[11].toString()); // Remarks
					objVoidBill.setStrVoidBillType(obj[12].toString()); // Void Bill Type

					listOfVoidBillData.add(objVoidBill);
				}
			}

			Comparator<clsPOSVoidBillDtl> reasonNameComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getStrReasonName().compareToIgnoreCase(o2.getStrReasonName());
				}
			};

			Comparator<clsPOSVoidBillDtl> billDateComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getDteBillDate().compareToIgnoreCase(o2.getDteBillDate());
				}
			};

			Comparator<clsPOSVoidBillDtl> billNoComparator = new Comparator<clsPOSVoidBillDtl>() {

				@Override
				public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2) {
					return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				}
			};

			Collections.sort(listOfVoidBillData,
					new clsVoidBillComparator(reasonNameComparator, billDateComparator, billNoComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfVoidBillData;
	}

	public List<clsPOSBillDtl> funProcessWaiterWiseIncentivesSummaryReport(String posCode, String fromDate,
			String toDate, String strShiftNo,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder sqlQBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		List<clsPOSBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();
		
		try {
			// Q Data
			sqlQBuilder.setLength(0);
			sqlQBuilder.append(
					"select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount,"
							+ "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,a.strBillNo,d.strIncentives "
							+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
							+ "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strItemCode=c.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode "
							+ "and a.strWaiterNo=e.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}
			sqlQBuilder.append("group by e.strWaiterNo " + "order by e.strWFullName ");

			List listSqlLiveWaiterWiseItemSales = objBaseService.funGetList(sqlQBuilder, "sql");
			if (listSqlLiveWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listSqlLiveWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					objBillDtlBean.setStrWaiterNo(obj[0].toString());
					objBillDtlBean.setStrWShortName(obj[1].toString());
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[3].toString()));
					objBillDtlBean.setDblIncentive(Double.parseDouble(obj[4].toString()));
					objBillDtlBean.setStrBillNo(obj[5].toString());
					objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[6].toString()));

					listOfWaiterWiseItemSales.add(objBillDtlBean);
				}
			}

			// Live Data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount,"
							+ "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,a.strBillNo,d.strIncentives "
							+ "from tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
							+ "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strItemCode=c.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode "
							+ "and a.strWaiterNo=e.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}

			sqlBuilder.append("group by e.strWaiterNo " + "order by e.strWFullName ");

			List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listQBillWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					objBillDtlBean.setStrWaiterNo(obj[0].toString());
					objBillDtlBean.setStrWShortName(obj[1].toString());
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[3].toString()));
					objBillDtlBean.setDblIncentive(Double.parseDouble(obj[4].toString()));
					objBillDtlBean.setStrBillNo(obj[5].toString());
					objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[6].toString()));

					listOfWaiterWiseItemSales.add(objBillDtlBean);
				}
			}

			Comparator<clsPOSBillDtl> waiterCodeComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrWShortName().compareTo(o2.getStrWShortName());
				}
			};

			Collections.sort(listOfWaiterWiseItemSales, new clsWaiterWiseSalesComparator(waiterCodeComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfWaiterWiseItemSales;
	}

	public List<clsPOSBillDtl> funProcessWaiterWiseIncentivesDetailReport(String posCode, String fromDate,
			String toDate, String strShiftNo,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder sqlQBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		List<clsPOSBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();
		
		try {
			// Q Data
			sqlQBuilder.setLength(0);
			sqlQBuilder.append(
					"select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,d.strSubGroupCode,d.strSubGroupName,a.strBillNo "
							+ ",sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount, "
							+ "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,round(d.strIncentives,2) as strIncentivePer,a.strBillNo "
							+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
							+ "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strItemCode=c.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode "
							+ "and a.strWaiterNo=e.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlQBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlQBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");		
			}

			sqlQBuilder.append("group by e.strWaiterNo,a.strBillNo " + "order by e.strWFullName,a.strBillNo ");

			List listLiveBillWaiterWiseItemSales = objBaseService.funGetList(sqlQBuilder, "sql");
			if (listLiveBillWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listLiveBillWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listLiveBillWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					objBillDtlBean.setStrWaiterNo(obj[0].toString());
					objBillDtlBean.setStrWShortName(obj[1].toString());
					objBillDtlBean.setStrSubGroupCode(obj[2].toString());
					objBillDtlBean.setStrSubGroupName(obj[3].toString());
					objBillDtlBean.setStrBillNo(obj[4].toString());
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[6].toString()));
					objBillDtlBean.setDblIncentive(Double.parseDouble(obj[7].toString()));
					objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[8].toString()));
					objBillDtlBean.setStrBillNo(obj[9].toString());

					listOfWaiterWiseItemSales.add(objBillDtlBean);
				}
			}

			// Live Data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select e.strWaiterNo,ifnull(e.strWShortName,'ND')strWShortName,d.strSubGroupCode,d.strSubGroupName,a.strBillNo "
							+ ",sum(b.dblQuantity)dblQuantity,sum(b.dblAmount)dblAmount, "
							+ "round(sum(b.dblAmount)*(d.strIncentives/100),2)dblIncentives,round(d.strIncentives,2) as strIncentivePer,a.strBillNo "
							+ "from tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d,tblwaitermaster e "
							+ "where date(a.dtebilldate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strItemCode=c.strItemCode " + "and c.strSubGroupCode=d.strSubGroupCode "
							+ "and a.strWaiterNo=e.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}

			sqlBuilder.append("group by e.strWaiterNo,a.strBillNo " + "order by e.strWFullName,a.strBillNo ");

			List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listQBillWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					objBillDtlBean.setStrWaiterNo(obj[0].toString());
					objBillDtlBean.setStrWShortName(obj[1].toString());
					objBillDtlBean.setStrSubGroupCode(obj[2].toString());
					objBillDtlBean.setStrSubGroupName(obj[3].toString());
					objBillDtlBean.setStrBillNo(obj[4].toString());
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[5].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[6].toString()));
					objBillDtlBean.setDblIncentive(Double.parseDouble(obj[7].toString()));
					objBillDtlBean.setDblIncentivePer(Double.parseDouble(obj[8].toString()));
					objBillDtlBean.setStrBillNo(obj[9].toString());

					listOfWaiterWiseItemSales.add(objBillDtlBean);
				}
			}

			Comparator<clsPOSBillDtl> waiterCodeComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrWShortName().compareTo(o2.getStrWShortName());
				}
			};

			Comparator<clsPOSBillDtl> billNoComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrBillNo().compareTo(o2.getStrBillNo());
				}
			};

			Comparator<clsPOSBillDtl> subGroupCodeComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrSubGroupCode().compareTo(o2.getStrSubGroupCode());
				}
			};

			Collections.sort(listOfWaiterWiseItemSales,
					new clsWaiterWiseSalesComparator(waiterCodeComparator, billNoComparator, subGroupCodeComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfWaiterWiseItemSales;

	}

	public List<clsPOSBillDtl> funProcessWaiterWiseItemReport(String posCode, String fromDate, String toDate,
			String strShiftNo, String waiterCode) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsPOSBillDtl> listOfWaiterWiseItemSales = new ArrayList<>();
		String enableShiftYN="N";
		
		try {
			// Q Data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemCode,b.strItemName,b.dblRate,sum(b.dblQuantity),sum(b.dblAmount),c.strWaiterNo,c.strWShortName "
							+ "from tblqbillhd a,tblqbilldtl b,tblwaitermaster c "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strWaiterNo=c.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}
			if (!waiterCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and c.strWaiterNo='" + waiterCode + "' ");
			}
			sqlBuilder.append("group by c.strWaiterNo,b.strItemCode " + "order by c.strWFullName,b.strItemCode ");

			List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listQBillWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listQBillWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBeanBillDtl = new clsPOSBillDtl();

					objBeanBillDtl.setStrItemCode(obj[0].toString());
					objBeanBillDtl.setStrItemName(obj[1].toString());
					objBeanBillDtl.setDblRate(Double.parseDouble(obj[2].toString()));
					objBeanBillDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));
					objBeanBillDtl.setDblAmount(Double.parseDouble(obj[4].toString()));
					objBeanBillDtl.setStrWaiterNo(obj[5].toString());
					objBeanBillDtl.setStrWShortName(obj[6].toString());

					listOfWaiterWiseItemSales.add(objBeanBillDtl);
				}
			}

			// Live Data
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemCode,b.strItemName,b.dblRate,sum(b.dblQuantity),sum(b.dblAmount),c.strWaiterNo,c.strWShortName "
							+ "from tblbillhd a,tblbilldtl b,tblwaitermaster c " + "where date(a.dteBillDate) between '"
							+ fromDate + "' and '" + toDate + "' " + "and a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strWaiterNo=c.strWaiterNo ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
            }
			if (!waiterCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and c.strWaiterNo='" + waiterCode + "' ");
			}
			sqlBuilder.append("group by c.strWaiterNo,b.strItemCode " + "order by c.strWFullName,b.strItemCode ");

			List listSqlLiveWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listSqlLiveWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listSqlLiveWaiterWiseItemSales.size(); i++) {
					Object[] obj = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBeanBillDtl = new clsPOSBillDtl();

					objBeanBillDtl.setStrItemCode(obj[0].toString());
					objBeanBillDtl.setStrItemName(obj[1].toString());
					objBeanBillDtl.setDblRate(Double.parseDouble(obj[2].toString()));
					objBeanBillDtl.setDblQuantity(Double.parseDouble(obj[3].toString()));
					objBeanBillDtl.setDblAmount(Double.parseDouble(obj[4].toString()));
					objBeanBillDtl.setStrWaiterNo(obj[5].toString());
					objBeanBillDtl.setStrWShortName(obj[6].toString());

					listOfWaiterWiseItemSales.add(objBeanBillDtl);
				}
			}

			Comparator<clsPOSBillDtl> waiterCodeComparator = new Comparator<clsPOSBillDtl>() {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrWShortName().compareTo(o2.getStrWShortName());
				}
			};

			Comparator<clsPOSBillDtl> itemCodeCodeComparator = new Comparator<clsPOSBillDtl>() {
				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2) {
					return o1.getStrItemCode().substring(0, 7).compareTo(o2.getStrItemCode().substring(0, 7));
				}
			};
			Collections.sort(listOfWaiterWiseItemSales,
					new clsWaiterWiseSalesComparator(waiterCodeComparator, itemCodeCodeComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfWaiterWiseItemSales;
	}

	public LinkedHashMap funProcessPosWiseSalesReport(String fromDate, String toDate, String strViewType) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		DecimalFormat decFormatFor2Decimal = new DecimalFormat("0.00");
		mapDocCode = new HashMap<String, String>();
		mapPOSDocSales = new HashMap<String, Double>();
		mapPOSItemDtl = new LinkedHashMap<>();


		LinkedHashMap resMap = new LinkedHashMap();
		double total = 0.0;

		List list = new ArrayList();
		List listcol = new ArrayList();
		List totalList = new ArrayList();
		totalList.add("Total");
		totalList.add("");

		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);
		sqlModLive.setLength(0);
		sqlModQfile.setLength(0);

		try 
		{
			if (strViewType.equalsIgnoreCase("ITEM WISE")) 
			{
				listcol.add("Item Code");
				listcol.add("Item Name");
				sbSqlLive.append("select strPOSCode,strPOSName from tblposmaster order by strPOSName");
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0) 
				{
					for (int i = 0; i < listSqlLive.size(); i++) 
					{
						Object[] obj = (Object[]) listSqlLive.get(i);
						listcol.add(obj[1].toString());
						totalList.add(0.0);
					}
				}
				listcol.add("Total");
				sbSqlLive.setLength(0);
				sbSqlLive.append("  select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity),sum(a.dblTaxAmount) "
					+ " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount), "
					+ " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
					+ " from tblbilldtl a,tblbillhd b,tblposmaster c "
					+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
					+ " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate   ");

				sbSqlQFile.append(" select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity),sum(a.dblTaxAmount) "
					+ " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount), "
					+ " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
					+ " from tblqbilldtl a,tblqbillhd b,tblposmaster c "
					+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
					+ " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate   ");

				sqlModLive.append(" select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0.0', "
					+ " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount), "
					+ " sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode  "
					+ " from tblbillmodifierdtl a,tblbillhd b,tblposmaster c  "
					+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  " + " and a.dblamount>0  "
					+ " and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate  ");

				sqlModQfile.append(" select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0', "
					+ " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount), "
					+ " sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
					+ " from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d "
					+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode "
					+ " and a.strItemCode=d.strItemCode and a.dblamount>0 " + " and date( b.dteBillDate ) BETWEEN '"
					+ fromDate + "' AND '" + toDate + "'   "
					+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate  ");
				
				funGenerateItemWiseSales(sbSqlLive);
				funGenerateItemWiseSales(sbSqlQFile);
				funGenerateItemWiseSales(sqlModLive);
				funGenerateItemWiseSales(sqlModQfile);
				Iterator<Map.Entry<String, String>> docIterator = mapDocCode.entrySet().iterator();
				while (docIterator.hasNext())
				{
				    Map.Entry entry = docIterator.next();
				    String docCode = entry.getKey().toString();
				    String docName = entry.getValue().toString();
				    int colSize = listcol.size()-1;
				    int lastCol = colSize - 1;
				    List arrList = new ArrayList();
					arrList.add(docCode.toString());
					arrList.add(docName.toString());
				    int i = 2;
				    double posWiseTotalSales = 0.00;
				    for (int col = i; col < colSize; col++)
				    {
						double sales = 0.00;
						if (mapPOSDocSales.containsKey(listcol.get(col).toString() + "!" + docCode))
						{
							sales = Double.parseDouble(mapPOSDocSales.get(listcol.get(col).toString() + "!" + docCode).toString());
						    arrList.add(String.valueOf(decFormatFor2Decimal.format(sales)));
						    double totalPosAmt=Double.valueOf(totalList.get(col).toString());
						    totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += sales;
						    i++;
						}
						else
						{
							arrList.add("0.0");
							double totalPosAmt=Double.valueOf(totalList.get(col).toString());
							totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += 0.00;
						    i++;
						}
				    }
				    arrList.add(String.valueOf(decFormatFor2Decimal.format(posWiseTotalSales)));
				    total += posWiseTotalSales;
					list.add(arrList);
				}
				
			}
			else if (strViewType.equalsIgnoreCase("GROUP WISE")) 
			{
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);
				sqlModLive.setLength(0);
				sqlModQfile.setLength(0);
				
				listcol.add("Group Code");
				listcol.add("Group Name");
				sbSqlLive.append("select strPOSCode,strPOSName from tblposmaster order by strPOSName");
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0) 
				{
					for (int i = 0; i < listSqlLive.size(); i++) 
					{
						Object[] obj = (Object[]) listSqlLive.get(i);
						listcol.add(obj[1].toString());
						totalList.add(0.0);
					}
				}
				listcol.add("Total");
				sbSqlLive.setLength(0);

				sbSqlLive.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
					+ " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode " + " and date( a.dteBillDate ) BETWEEN '"
					+ fromDate + "' AND '" + toDate + "' "
					+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

				sbSqlQFile.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
					+ " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode  " + " and date( a.dteBillDate ) BETWEEN '"
					+ fromDate + "' AND '" + toDate + "' "
					+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode   ");

				sqlModLive.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,"
					+ " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode  and d.strSubGroupCode=e.strSubGroupCode "
					+ " and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

				sqlModQfile.append(" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' , "
					+ " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
					+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");
	
				funGenerateGroupWiseSales(sbSqlLive);
				funGenerateGroupWiseSales(sbSqlQFile);
				funGenerateGroupWiseSales(sqlModLive);
				funGenerateGroupWiseSales(sqlModQfile);
				Iterator<Map.Entry<String, String>> docIterator = mapDocCode.entrySet().iterator();
				while (docIterator.hasNext())
				{
				    Map.Entry entry = docIterator.next();
				    String docCode = entry.getKey().toString();
				    String docName = entry.getValue().toString();

				    int colSize = listcol.size()-1;
				    int lastCol = colSize - 1;
				    List arrList = new ArrayList();
					arrList.add(docCode.toString());
					arrList.add(docName.toString());
				    int i = 2;
				    double posWiseTotalSales = 0.00;
				    for (int col = i; col < colSize; col++)
				    {
						double sales = 0.00;
						if (mapPOSDocSales.containsKey(listcol.get(col).toString() + "!" + docCode))
						{
							sales = Double.parseDouble(mapPOSDocSales.get(listcol.get(col).toString() + "!" + docCode).toString());
						    arrList.add(String.valueOf(decFormatFor2Decimal.format(sales)));
						    double totalPosAmt=Double.valueOf(totalList.get(col).toString());
						    totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += sales;
						    i++;
						}
						else
						{
							arrList.add("0.0");
							double totalPosAmt=Double.valueOf(totalList.get(col).toString());
							totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += 0.00;
						    i++;
						}
				    }
				    arrList.add(String.valueOf(decFormatFor2Decimal.format(posWiseTotalSales)));
				    total += posWiseTotalSales;
					list.add(arrList);
				}

			} else if (strViewType.equalsIgnoreCase("SUB GROUP WISE")) {
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);
				sqlModLive.setLength(0);
				sqlModQfile.setLength(0);
				
				listcol.add("Sub Group Code");
				listcol.add("Sub Group Name");
				sbSqlLive.append("select strPOSCode,strPOSName from tblposmaster order by strPOSName");
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0) 
				{
					for (int i = 0; i < listSqlLive.size(); i++) 
					{
						Object[] obj = (Object[]) listSqlLive.get(i);
						listcol.add(obj[1].toString());
						totalList.add(0.0);
					}
				}
				listcol.add("Total");
				sbSqlLive.setLength(0);

				sbSqlLive.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate , "
					+ " sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
					+ " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
					+ " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

				sbSqlQFile.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , "
					+ " sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
					+ " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
					+ " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

				sqlModLive.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
					+ " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode  ");

				sqlModQfile.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
					+ " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
					+ " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

				funGenerateGroupWiseSales(sbSqlLive);
				funGenerateGroupWiseSales(sbSqlQFile);
				funGenerateGroupWiseSales(sqlModLive);
				funGenerateGroupWiseSales(sqlModQfile);
				Iterator<Map.Entry<String, String>> docIterator = mapDocCode.entrySet().iterator();
				while (docIterator.hasNext())
				{
				    Map.Entry entry = docIterator.next();
				    String docCode = entry.getKey().toString();
				    String docName = entry.getValue().toString();

				    int colSize = listcol.size()-1;
				    int lastCol = colSize - 1;
				    List arrList = new ArrayList();
					arrList.add(docCode.toString());
					arrList.add(docName.toString());
				    int i = 2;
				    double posWiseTotalSales = 0.00;
				    for (int col = i; col < colSize; col++)
				    {
						double sales = 0.00;
						if (mapPOSDocSales.containsKey(listcol.get(col).toString() + "!" + docCode))
						{
							sales = Double.parseDouble(mapPOSDocSales.get(listcol.get(col).toString() + "!" + docCode).toString());
						    arrList.add(String.valueOf(decFormatFor2Decimal.format(sales)));
						    double totalPosAmt=Double.valueOf(totalList.get(col).toString());
						    totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += sales;
						    i++;
						}
						else
						{
							arrList.add("0.0");
							double totalPosAmt=Double.valueOf(totalList.get(col).toString());
							totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += 0.00;
						    i++;
						}
				    }
				    arrList.add(String.valueOf(decFormatFor2Decimal.format(posWiseTotalSales)));
				    total += posWiseTotalSales;
					list.add(arrList);
				}
			} else if (strViewType.equalsIgnoreCase("MENU HEAD WISE")) {
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);
				sqlModLive.setLength(0);
				sqlModQfile.setLength(0);
				
				listcol.add("Menu Head Code");
				listcol.add("Menu Head Name");
				sbSqlLive.append("select strPOSCode,strPOSName from tblposmaster order by strPOSName");
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0) 
				{
					for (int i = 0; i < listSqlLive.size(); i++) 
					{
						Object[] obj = (Object[]) listSqlLive.get(i);
						listcol.add(obj[1].toString());
						totalList.add(0.0);
					}
				}
				listcol.add("Total");
				sbSqlLive.setLength(0);

				sbSqlLive.append(" SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
						+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode   "
						+ " FROM tblbilldtl a " + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode  "
						+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
						+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
						+ " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

				sbSqlQFile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
						+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
						+ " FROM tblqbilldtl a " + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
						+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
						+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
						+ " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

				sqlModLive.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
						+ " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode "
						+ " FROM tblbillmodifierdtl a " + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
						+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode  "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
						+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						+ " and a.dblAmount>0   " + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
						+ " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

				sqlModQfile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
					+ " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
					+ " FROM tblqbillmodifierdtl a "
					+ " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
					+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode"
					+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.dblAmount>0    " + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
					+ " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

				funGenerateMenuHeadWiseSales(sbSqlLive);
				funGenerateMenuHeadWiseSales(sbSqlQFile);
				funGenerateMenuHeadWiseSales(sqlModLive);
				funGenerateMenuHeadWiseSales(sqlModQfile);
				Iterator<Map.Entry<String, String>> docIterator = mapDocCode.entrySet().iterator();
				while (docIterator.hasNext())
				{
				    Map.Entry entry = docIterator.next();
				    String docCode = entry.getKey().toString();
				    String docName = entry.getValue().toString();

				    int colSize = listcol.size()-1;
				    int lastCol = colSize - 1;
				    List arrList = new ArrayList();
					arrList.add(docCode.toString());
					arrList.add(docName.toString());
				    int i = 2;
				    double posWiseTotalSales = 0.00;
				    for (int col = i; col < colSize; col++)
				    {
						double sales = 0.00;
						if (mapPOSDocSales.containsKey(listcol.get(col).toString() + "!" + docCode))
						{
							sales = Double.parseDouble(mapPOSDocSales.get(listcol.get(col).toString() + "!" + docCode).toString());
						    arrList.add(String.valueOf(decFormatFor2Decimal.format(sales)));
						    double totalPosAmt=Double.valueOf(totalList.get(col).toString());
						    totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += sales;
						    i++;
						}
						else
						{
							arrList.add("0.0");
							double totalPosAmt=Double.valueOf(totalList.get(col).toString());
							totalList.set(col, String.valueOf(decFormatFor2Decimal.format(totalPosAmt+sales)));
						    posWiseTotalSales += 0.00;
						    i++;
						}
				    }
				    arrList.add(String.valueOf(decFormatFor2Decimal.format(posWiseTotalSales)));
				    total += posWiseTotalSales;
					list.add(arrList);
				}
			}
			else if (strViewType.equalsIgnoreCase("POS WISE")) {
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);
				totalList = new ArrayList();
				totalList.add("Total");

				sbSqlLive.append("select  b.strPosCode,b.strPosName,sum(a.dblGrandTotal) "
						+ "from tblbillhd a,tblposmaster b "
						+ "where a.strPOSCode=b.strPosCode "
						+ "and  date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						+ "group by b.strPosCode,b.strPosName "
						+ "order by b.strPosCode,b.strPosName;   ");

				sbSqlQFile.append("select  b.strPosCode,b.strPosName,sum(a.dblGrandTotal) "
						+ "from tblqbillhd a,tblposmaster b "
						+ "where a.strPOSCode=b.strPosCode "
						+ "and  date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
						+ "group by b.strPosCode,b.strPosName "
						+ "order by b.strPosCode,b.strPosName;   ");

				
				List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
				if (listSqlLive.size() > 0) {
					for (int i = 0; i < listSqlLive.size(); i++) 
					{
						Object[] obj = (Object[]) listSqlLive.get(i);
						/*List arrList = new ArrayList();

						arrList.add(obj[1].toString());
						arrList.add(decFormatFor2Decimal.format(new BigDecimal(obj[2].toString()).doubleValue()));
						total += Double.parseDouble(obj[2].toString());
						list.add(arrList);
						*/
						if (mapPOSDocSales.containsKey(obj[1].toString()))
						{
						    double oldSalesAmt = mapPOSDocSales.get(obj[1].toString());
						    mapPOSDocSales.put(obj[1].toString(), oldSalesAmt + new BigDecimal(obj[2].toString()).doubleValue());
						}
						else
						{
						    mapPOSDocSales.put(obj[1].toString(),new BigDecimal(obj[2].toString()).doubleValue());
						}
					}
				}

				List listSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
				if (listSqlQFile.size() > 0) {
					for (int i = 0; i < listSqlQFile.size(); i++) {
						Object[] obj = (Object[]) listSqlQFile.get(i);
						/*List arrList = new ArrayList();

						arrList.add(obj[1].toString());
						arrList.add(decFormatFor2Decimal.format(new BigDecimal(obj[2].toString()).doubleValue()));
						total += Double.parseDouble(obj[2].toString());
						list.add(arrList);
						*/
						if (mapPOSDocSales.containsKey(obj[1].toString()))
						{
						    double oldSalesAmt = mapPOSDocSales.get(obj[1].toString());
						    mapPOSDocSales.put(obj[1].toString(), oldSalesAmt + new BigDecimal(obj[2].toString()).doubleValue());
						}
						else
						{
						    mapPOSDocSales.put(obj[1].toString(),new BigDecimal(obj[2].toString()).doubleValue());
						}
					}
				}
				
				Iterator<Map.Entry<String, Double>> docIterator = mapPOSDocSales.entrySet().iterator();
				while (docIterator.hasNext())
				{
				    Map.Entry entry = docIterator.next();
				    List arrList = new ArrayList();
					arrList.add(entry.getKey().toString());
					arrList.add(decFormatFor2Decimal.format(entry.getValue()));
					total += Double.parseDouble(entry.getValue().toString());
					list.add(arrList);

				}
				
				listcol.add("POS Name");
				listcol.add("Sale");
			} 
			totalList.add(String.valueOf(decFormatFor2Decimal.format(total)));

			resMap.put("List", list);
			resMap.put("totalList", totalList);
			resMap.put("listcol", listcol);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resMap;

	}
	
	
    private void funGenerateItemWiseSales(StringBuilder sbSql)
    {
	try
	{
		List listSql = objBaseService.funGetList(sbSql, "sql");
		if (listSql.size() > 0) 
		{
			for (int i = 0; i < listSql.size(); i++) 
			{
				Object[] obj = (Object[]) listSql.get(i);
				String itemCode = obj[0].toString();//itemCode
				String itemName = obj[1].toString();//itemName
				String posName =  obj[2].toString();//posName
				double qty = Double.valueOf(obj[3].toString());//qty
				double salesAmt = Double.valueOf(obj[7].toString());//salesAmount
				double subTotal = Double.valueOf(obj[5].toString());//sunTotal
				double discAmt =Double.valueOf(obj[8].toString());//discount
				String date = obj[9].toString();//date
				String posCode = obj[10].toString();//posCode

				mapDocCode.put(itemCode, itemName);
				if (mapPOSDocSales.containsKey(posName + "!" + itemCode))
				{
				    double oldSalesAmt = mapPOSDocSales.get(posName + "!" + itemCode);
				    mapPOSDocSales.put(posName + "!" + itemCode, oldSalesAmt + salesAmt);
				}
				else
				{
				    mapPOSDocSales.put(posName + "!" + itemCode, salesAmt);
				}

				if (mapPOSItemDtl.containsKey(posCode))
				{
				    Map<String, clsPOSBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
				    if (mapItemDtl.containsKey(itemCode))
				    {
				    	clsPOSBillItemDtl objItemDtl = mapItemDtl.get(itemCode);
						objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
						objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
						objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
						objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
				    }
				    else
				    {
				    	clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
				    	mapItemDtl.put(itemCode, objItemDtl);
				    }
				}
				else
				{
				    Map<String, clsPOSBillItemDtl> mapItemDtl = new LinkedHashMap<>();
				    clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
				    mapItemDtl.put(itemCode, objItemDtl);
				    mapPOSItemDtl.put(posCode, mapItemDtl);
				}
			}
		}
		
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
   }
    
    private void funGenerateGroupWiseSales(StringBuilder sbSql)
    {
		try
		{
	
			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) 
			{
				for (int i = 0; i < listSql.size(); i++) 
				{
					Object[] obj = (Object[]) listSql.get(i);
					String subGroupCode =obj[0].toString();
					String subGroupName = obj[1].toString();
					String posName = obj[4].toString();
					double salesAmt = Double.valueOf(obj[7].toString());
					String posCode = obj[9].toString();
			
					mapDocCode.put(subGroupCode, subGroupName);
					if (mapPOSDocSales.containsKey(posName + "!" + subGroupCode))
					{
					    double oldSalesAmt = mapPOSDocSales.get(posName + "!" + subGroupCode);
					    mapPOSDocSales.put(posName + "!" + subGroupCode, oldSalesAmt + salesAmt);
					}
					else
					{
					    mapPOSDocSales.put(posName + "!" + subGroupCode, salesAmt);
					}
				}
			}
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
   }

    
    private void funGenerateMenuHeadWiseSales(StringBuilder sbSql)
    {
		try
		{
			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) 
			{
				for (int i = 0; i < listSql.size(); i++) 
				{
					Object[] obj = (Object[]) listSql.get(i);
					String posCode = obj[9].toString();//posCode
					String posName = obj[4].toString();//posName
					String menuCode = obj[0].toString();//menuCode
					String menuName = obj[1].toString();//menuName                
					double qty = Double.valueOf(obj[2].toString());//qty
					double salesAmt = Double.valueOf(obj[7].toString());//salesAmt
					double subTotal = Double.valueOf(obj[3].toString());//subTotal
					double discAmt = Double.valueOf(obj[8].toString());//disc                 
			
					mapDocCode.put(menuCode, menuName);
					if (mapPOSDocSales.containsKey(posName + "!" + menuCode))
					{
					    double oldSalesAmt = mapPOSDocSales.get(posName + "!" + menuCode);
					    mapPOSDocSales.put(posName + "!" + menuCode, oldSalesAmt + salesAmt);
					}
					else
					{
					    mapPOSDocSales.put(posName + "!" + menuCode, salesAmt);
					}
				}
			}
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
    }
	
	
	
	

	public List funProcessPosWiseGroupWiseReport(String fromDate, String toDate) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
		double total = 0.0;

		try {
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);
			sqlModLive.setLength(0);
			sqlModQfile.setLength(0);

			sbSqlLive.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
					+ " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode " + " and date( a.dteBillDate ) BETWEEN '" + fromDate
					+ "' AND '" + toDate + "' " + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

			sbSqlQFile.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt) ,f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
					+ " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode and c.strGroupCode=d.strGroupCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode  " + " and date( a.dteBillDate ) BETWEEN '" + fromDate
					+ "' AND '" + toDate + "' " + " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode   ");

			sqlModLive.append(
					" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,"
							+ " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode  and d.strSubGroupCode=e.strSubGroupCode "
							+ " and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");

			sqlModQfile.append(
					" select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' , "
							+ " sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c  "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
							+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode  and b.dblamount>0  "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
							+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode  ");
			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listSqlQFile.size() > 0) {
				for (int i = 0; i < listSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlQFile.get(i);
					List arrList = new ArrayList();
					total += Double.parseDouble(obj[7].toString());
					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());

					list.add(arrList);
				}
			}

			List listSqlModLive = objBaseService.funGetList(sqlModLive, "sql");
			if (listSqlModLive.size() > 0) {
				for (int i = 0; i < listSqlModLive.size(); i++) {
					Object[] obj = (Object[]) listSqlModLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlModQFile = objBaseService.funGetList(sqlModQfile, "sql");
			if (listSqlModQFile.size() > 0) {
				for (int i = 0; i < listSqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlModQFile.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List funProcessDayEndSubGroupWiseReport(String fromDate, String toDate) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
		double total = 0.0;
		try {
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);
			sqlModLive.setLength(0);
			sqlModQfile.setLength(0);

			sbSqlLive.append(
					" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate , "
							+ " sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
							+ " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
							+ " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode  "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

			sbSqlQFile.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , "
					+ " sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode "
					+ " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f  "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
					+ " and b.strItemCode=d.strItemCode  and c.strSubGroupCode=d.strSubGroupCode "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

			sqlModLive.append(" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
					+ " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
					+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'   "
					+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode  ");

			sqlModQfile.append(
					" select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
							+ " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
							+ " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
							+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode   ");

			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listSqlQFile.size() > 0) {
				for (int i = 0; i < listSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlQFile.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlModLive = objBaseService.funGetList(sqlModLive, "sql");
			if (listSqlModLive.size() > 0) {
				for (int i = 0; i < listSqlModLive.size(); i++) {
					Object[] obj = (Object[]) listSqlModLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlModQFile = objBaseService.funGetList(sqlModQfile, "sql");
			if (listSqlModQFile.size() > 0) {
				for (int i = 0; i < listSqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlModQFile.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List funProcessPosWiseMenuHeadWiseReport(String fromDate, String toDate) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQfile = new StringBuilder();
		List list = new ArrayList();
		double total = 0.0;
		try {
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);
			sqlModLive.setLength(0);
			sqlModQfile.setLength(0);

			sbSqlLive.append(" SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
					+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode   "
					+ " FROM tblbilldtl a " + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode  "
					+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
					+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
					+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
					+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
					+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
					+ " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

			sbSqlQFile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
					+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
					+ " FROM tblqbilldtl a " + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode  "
					+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
					+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
					+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
					+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
					+ " order by b.strPoscode, d.strMenuCode,e.strMenuName   ");

			sqlModLive.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
					+ " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode "
					+ " FROM tblbillmodifierdtl a " + " left outer join tblbillhd b on a.strBillNo=b.strBillNo "
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode  "
					+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
					+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
					+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.dblAmount>0   " + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
					+ " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

			sqlModQfile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity), "
					+ " sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
					+ " FROM tblqbillmodifierdtl a " + " left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode "
					+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode"
					+ " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.dblAmount>0    " + " Group by b.strPoscode, d.strMenuCode,e.strMenuName "
					+ " order by b.strPoscode, d.strMenuCode,e.strMenuName  ");

			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listSqlQFile.size() > 0) {
				for (int i = 0; i < listSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlQFile.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlModLive = objBaseService.funGetList(sqlModLive, "sql");
			if (listSqlModLive.size() > 0) {
				for (int i = 0; i < listSqlModLive.size(); i++) {
					Object[] obj = (Object[]) listSqlModLive.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

			List listSqlModQFile = objBaseService.funGetList(sqlModQfile, "sql");
			if (listSqlModQFile.size() > 0) {
				for (int i = 0; i < listSqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlModQFile.get(i);
					List arrList = new ArrayList();

					arrList.add(obj[0].toString());
					arrList.add(obj[1].toString());
					arrList.add(obj[4].toString());
					arrList.add(obj[7].toString());
					total += Double.parseDouble(obj[7].toString());
					list.add(arrList);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List funProcessDailyCollection(String posCode, String fromDate, String toDate, String strShiftNo) {
		List<clsPOSBillItemDtlBean> listOfBillData = new ArrayList<clsPOSBillItemDtlBean>();
		Map mapMultiSettleBills = new HashMap();
		String enableShiftYN="N";

		try {
			StringBuilder sqlBuilder = new StringBuilder();
			DecimalFormat decimalFormat2Deci = new DecimalFormat("0.00");

			// live
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"SELECT a.strBillNo, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate,b.strPosName "
							+ ", IFNULL(d.strSettelmentDesc,'') AS strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt "
							+ ", SUM(c.dblSettlementAmt) AS dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
							+ ",ifnull(e.strTableName,''),a.strUserEdited,ifnull(f.strCustomerName,'') "
							+ "FROM tblbillhd a " + "join tblposmaster b on a.strPOSCode=b.strPOSCode  "
							+ "join tblbillsettlementdtl c on a.strBillNo=c.strBillNo AND DATE(a.dteBillDate)= DATE(c.dteBillDate) AND a.strClientCode=c.strClientCode "
							+ "join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode  "
							+ "left outer join tbltablemaster e on a.strTableNo=e.strTableNo "
							+ "left outer join tblcustomermaster f on a.strCustomerCode=f.strCustomerCode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
				
            }
			sqlBuilder.append("GROUP BY a.strClientCode, DATE(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
					+ "ORDER BY d.strSettelmentCode ");

			List listSqlLiveData = objBaseService.funGetList(sqlBuilder, "sql");

			if (listSqlLiveData.size() > 0) {
				for (int i = 0; i < listSqlLiveData.size(); i++) {
					Object[] obj = (Object[]) listSqlLiveData.get(i);
					String key = obj[0].toString() + "!" + obj[1].toString();

					clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
					if (mapMultiSettleBills.containsKey(key))// billNo
					{
						objBillItemDtlBean.setStrBillNo(obj[0].toString());
						objBillItemDtlBean.setDteBillDate(obj[1].toString());
						objBillItemDtlBean.setStrPosName(obj[2].toString());
						objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
						objBillItemDtlBean.setDblDiscountAmt(0.00);
						objBillItemDtlBean.setDblTaxAmt(0.00);
						objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
						objBillItemDtlBean.setDblSubTotal(0.00);
						objBillItemDtlBean.setIntBillSeriesPaxNo(0);
					} else {
						objBillItemDtlBean.setStrBillNo(obj[0].toString());
						objBillItemDtlBean.setDteBillDate(obj[1].toString());
						objBillItemDtlBean.setStrPosName(obj[2].toString());
						objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
						objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));
						objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[5].toString()));
						objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
						objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[7].toString()));
						objBillItemDtlBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[9].toString()));
						objBillItemDtlBean.setStrItemCode(obj[10].toString());
						objBillItemDtlBean.setStrDiscType(obj[11].toString());
						objBillItemDtlBean.setStrItemName(obj[12].toString());

						if (objBillItemDtlBean.getDblSubTotal() > 0) {
							objBillItemDtlBean.setDblDiscountPer(
									(objBillItemDtlBean.getDblDiscountAmt() / objBillItemDtlBean.getDblSubTotal())
											* 100);
						}
					}
					listOfBillData.add(objBillItemDtlBean);

					if (obj[8].toString().equalsIgnoreCase("MultiSettle")) {
						mapMultiSettleBills.put(key, obj[0].toString());
					}
				}
			}

			// QFile
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"SELECT a.strBillNo, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS dteBillDate,b.strPosName "
							+ ", IFNULL(d.strSettelmentDesc,'') AS strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt "
							+ ", SUM(c.dblSettlementAmt) AS dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode,intBillSeriesPaxNo "
							+ ",ifnull(e.strTableName,''),a.strUserEdited,ifnull(f.strCustomerName,'') "
							+ "FROM tblqbillhd a " + "join tblposmaster b on a.strPOSCode=b.strPOSCode  "
							+ "join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo AND DATE(a.dteBillDate)= DATE(c.dteBillDate) AND a.strClientCode=c.strClientCode "
							+ "join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode  "
							+ "left outer join tbltablemaster e on a.strTableNo=e.strTableNo "
							+ "left outer join tblcustomermaster f on a.strCustomerCode=f.strCustomerCode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "'  ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
		     }
			sqlBuilder.append("GROUP BY a.strClientCode, DATE(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
					+ "ORDER BY d.strSettelmentCode ");

			List listSqlQBillData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listSqlQBillData.size() > 0) {
				for (int i = 0; i < listSqlQBillData.size(); i++) {
					Object[] obj = (Object[]) listSqlQBillData.get(i);
					String key = obj[0].toString() + "!" + obj[1].toString();

					clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
					if (mapMultiSettleBills.containsKey(key))// billNo
					{
						objBillItemDtlBean.setStrBillNo(obj[0].toString());
						objBillItemDtlBean.setDteBillDate(obj[1].toString());
						objBillItemDtlBean.setStrPosName(obj[2].toString());
						objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
						objBillItemDtlBean.setDblDiscountAmt(0.00);
						objBillItemDtlBean.setDblTaxAmt(0.00);
						objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
						objBillItemDtlBean.setDblSubTotal(0.00);
						objBillItemDtlBean.setIntBillSeriesPaxNo(0);
					} else {
						objBillItemDtlBean.setStrBillNo(obj[0].toString());
						objBillItemDtlBean.setDteBillDate(obj[1].toString());
						objBillItemDtlBean.setStrPosName(obj[2].toString());
						objBillItemDtlBean.setStrSettelmentMode(obj[3].toString());
						objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString()));
						objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[5].toString()));
						objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[6].toString()));
						objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[7].toString()));
						objBillItemDtlBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[9].toString()));
						objBillItemDtlBean.setStrItemCode(obj[10].toString());
						objBillItemDtlBean.setStrDiscType(obj[11].toString());
						objBillItemDtlBean.setStrItemName(obj[12].toString());

						if (objBillItemDtlBean.getDblSubTotal() > 0) {
							objBillItemDtlBean.setDblDiscountPer(
									(objBillItemDtlBean.getDblDiscountAmt() / objBillItemDtlBean.getDblSubTotal())
											* 100);
						}
					}
					listOfBillData.add(objBillItemDtlBean);

					if (obj[8].toString().equalsIgnoreCase("MultiSettle")) {
						mapMultiSettleBills.put(key, obj[0].toString());
					}
				}
			}

			Comparator<clsPOSBillItemDtlBean> settlementModeComparator = new Comparator<clsPOSBillItemDtlBean>() {

				@Override
				public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2) {
					return o2.getStrSettelmentMode().compareToIgnoreCase(o1.getStrSettelmentMode());
				}
			};

			Comparator<clsPOSBillItemDtlBean> billDateComparator = new Comparator<clsPOSBillItemDtlBean>() {

				@Override
				public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2) {
					return o2.getDteBillDate().compareToIgnoreCase(o1.getDteBillDate());
				}
			};

			Comparator<clsPOSBillItemDtlBean> billNoComparator = new Comparator<clsPOSBillItemDtlBean>() {

				@Override
				public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2) {
					return o1.getStrBillNo().compareToIgnoreCase(o2.getStrBillNo());
				}
			};

			Collections.sort(listOfBillData,
					new clsPOSBillComparator(settlementModeComparator, billDateComparator, billNoComparator));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfBillData;
	}

	public List funProcessDailyCollectionForVoidBillDataReport(String posCode, String fromDate, String toDate,
			String strShiftNo) {
		List<clsPOSVoidBillDtl> listOfVoidBillData = new ArrayList<clsPOSVoidBillDtl>();
		StringBuilder sqlBuilder = new StringBuilder();
		String enableShiftYN="N";
		try {
			// Bill detail data
			sqlBuilder.setLength(0);
			sqlBuilder.append("SELECT a.strBillNo "
					+ ",DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate, DATE_FORMAT(DATE(a.dteModifyVoidBill),'%d-%m-%Y') AS VoidedDate "
					+ ",TIME(a.dteBillDate) AS EntryTime, TIME(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount AS BillAmount "
					+ ",a.strReasonName AS Reason,a.strUserEdited AS UserEdited,a.strUserCreated,a.strRemark "
					+ " from tblvoidbillhd a,tblvoidbilldtl b " + " where a.strBillNo=b.strBillNo "
					+ " and b.strTransType='VB' " + " and a.strTransType='VB' "
					+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and Date(a.dteModifyVoidBill)  Between '"
					+ fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "'  ");
				
		    }
			sqlBuilder.append(" group by a.dteBillDate,a.strBillNo ");

			List listVoidData = objBaseService.funGetList(sqlBuilder, "sql");

			if (listVoidData.size() > 0) {
				for (int i = 0; i < listVoidData.size(); i++) {
					Object[] obj = (Object[]) listVoidData.get(i);
					clsPOSVoidBillDtl objVoidBill = new clsPOSVoidBillDtl();
					objVoidBill.setStrBillNo(obj[0].toString()); // BillNo
					objVoidBill.setDteBillDate(obj[1].toString()); // Bill Date
					objVoidBill.setStrWaiterNo(obj[2].toString()); // Voided Date
					objVoidBill.setStrTableNo(obj[3].toString()); // Entry Time
					objVoidBill.setStrSettlementCode(obj[4].toString()); // Voided Time
					objVoidBill.setDblAmount(Double.parseDouble(obj[5].toString())); // Bill Amount
					objVoidBill.setStrReasonName(obj[6].toString()); // Reason
					objVoidBill.setStrClientCode(obj[7].toString()); // User Edited
					objVoidBill.setStrUserCreated(obj[8].toString()); // User Created
					objVoidBill.setStrRemarks(obj[9].toString()); // Remarks

					listOfVoidBillData.add(objVoidBill);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfVoidBillData;
	}

	public List funProcessDailySalesReport(String posCode, String fromDate, String toDate, String strShiftNo,
			String userCode,String enableShiftYN) {
		List<clsPOSBillItemDtlBean> listOfDailySaleData = new ArrayList<clsPOSBillItemDtlBean>();
		StringBuilder sbSqlBillWise = new StringBuilder();
		StringBuilder sbSqlBillWiseQFile = new StringBuilder();
		
		try {
			sbSqlBillWise.setLength(0);
			sbSqlBillWise
					.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
							+ ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode"
							+ ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt"
							+ ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
							+ ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
							+ ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')" + ",a.dblTipAmount,'" + userCode
							+ "',a.strDiscountRemark,'' " + "from tblbillhd  a "
							+ "left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
							+ "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
							+ "left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlBillWise.append(" and a.strPOSCode='" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlBillWise.append(" AND a.intShiftCode = '" + strShiftNo + "' ");		
		    }
			sbSqlBillWise.append(" order by a.strBillNo desc");
			// System.out.println("Bill Wise Report Live Query="+sbSqlBillWise);

			sbSqlBillWiseQFile.setLength(0);
			sbSqlBillWiseQFile
					.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
							+ ",ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
							+ ",ifnull(a.dblSubTotal,0.00),a.dblDiscountPer,a.dblDiscountAmt,a.dblTaxAmt "
							+ ",ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated "
							+ ",a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges "
							+ ",ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')" + ",a.dblTipAmount,'" + userCode
							+ "',a.strDiscountRemark,'' " + "from tblqbillhd  a "
							+ "left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
							+ "left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
							+ "left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlBillWiseQFile.append(" and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbSqlBillWiseQFile.append(" AND a.intShiftCode = '" + strShiftNo + "' ");		
		    }
			sbSqlBillWiseQFile.append(" order by a.strBillNo desc");

			List listLiveData = objBaseService.funGetList(sbSqlBillWise, "sql");
			if (listLiveData.size() > 0) {
				for (int i = 0; i < listLiveData.size(); i++) {
					Object[] obj = (Object[]) listLiveData.get(i);
					clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
					objBillItemDtlBean.setStrBillNo(obj[0].toString()); // BillNo
					objBillItemDtlBean.setDteBillDate(obj[1].toString()); // Bill Date
					objBillItemDtlBean.setStrItemCode(obj[3].toString()); // Table Name
					objBillItemDtlBean.setStrPosName(obj[4].toString()); // POS Name
					objBillItemDtlBean.setStrSettelmentMode(obj[5].toString()); // Settle Mode
					objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[6].toString())); // Sub Total
					objBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[7].toString())); // Disc Per
					objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[8].toString())); // Disc Amt
					objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[9].toString())); // Tax Amt
					objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[10].toString())); // Settle Amt
					objBillItemDtlBean.setStrDiscType(obj[11].toString()); // User Created
					objBillItemDtlBean.setStrDiscValue(obj[13].toString()); // Date Created
					objBillItemDtlBean.setStrItemName(obj[20].toString()); // Customer Name
					objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[18].toString())); // Delivery Charges
					listOfDailySaleData.add(objBillItemDtlBean);
				}
			}

			List listQFileData = objBaseService.funGetList(sbSqlBillWiseQFile, "sql");
			if (listQFileData.size() > 0) {
				for (int i = 0; i < listQFileData.size(); i++) {
					Object[] obj = (Object[]) listQFileData.get(i);
					clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
					objBillItemDtlBean.setStrBillNo(obj[0].toString()); // BillNo
					objBillItemDtlBean.setDteBillDate(obj[1].toString()); // Bill Date
					objBillItemDtlBean.setStrItemCode(obj[3].toString()); // Table Name
					objBillItemDtlBean.setStrPosName(obj[4].toString()); // POS Name
					objBillItemDtlBean.setStrSettelmentMode(obj[5].toString()); // Settle Mode
					objBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[6].toString())); // Sub Total
					objBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[7].toString())); // Disc Per
					objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[8].toString())); // Disc Amt
					objBillItemDtlBean.setDblTaxAmt(Double.parseDouble(obj[9].toString())); // Tax Amt
					objBillItemDtlBean.setDblSettlementAmt(Double.parseDouble(obj[10].toString())); // Settle Amt
					objBillItemDtlBean.setStrDiscType(obj[11].toString()); // User Created
					objBillItemDtlBean.setStrDiscValue(obj[13].toString()); // Date Created
					objBillItemDtlBean.setStrItemName(obj[20].toString()); // Customer Name
					objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[18].toString())); // Delivery Charges
					listOfDailySaleData.add(objBillItemDtlBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfDailySaleData;
	}
	
	

	public List funProcessSubGroupWiseSummaryReport(String posCode, String fromDate, String toDate, String strShiftNo,
			String strUserCode,String enableShiftYN) {
		List listRet = new ArrayList();
		List<clsPOSGroupSubGroupWiseSales> listOfData = new ArrayList<clsPOSGroupSubGroupWiseSales>();
		try {
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sql = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			Map<String, clsPOSGroupSubGroupWiseSales> mapSubGroupWiseDtl = new HashMap<>();

			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);
			sql.setLength(0);
			sqlModLive.setLength(0);
			sqlModQFile.setLength(0);

			sbSqlQFile.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
					+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
					+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
					+ "from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d " + ",tblposmaster f "
					+ "where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPOSCode " + "and b.strItemCode=d.strItemCode "
					+ "and c.strSubGroupCode=d.strSubGroupCode ");

			sbSqlLive.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
					+ ", sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
					+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt)"
					+ "from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d " + ",tblposmaster f "
					+ "where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPOSCode " + "and b.strItemCode=d.strItemCode "
					+ "and c.strSubGroupCode=d.strSubGroupCode ");

			sqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName" + ",'" + strUserCode + "','0' ,'0.00','0.00' "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d" + ",tblsubgrouphd c"
					+ " where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode " + " and left(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=c.strSubGroupCode " + "  ");

			sqlModQFile.append("select c.strSubGroupCode,c.strSubGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount),f.strPOSName" + ",'" + strUserCode + "','0' ,'0.00','0.00' "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d" + ",tblsubgrouphd c"
					+ " where a.strBillNo=b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
					+ " and a.strPOSCode=f.strPosCode " + " and left(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=c.strSubGroupCode " + "  ");

			sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
			    sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
		    
			sbSqlFilters.append(" group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters);
			sqlModQFile.append(sbSqlFilters);

			List listLiveData = objBaseService.funGetList(sbSqlLive, "sql");
			if (listLiveData.size() > 0) {
				for (int i = 0; i < listLiveData.size(); i++) {
					Object[] obj = (Object[]) listLiveData.get(i);
					clsPOSGroupSubGroupWiseSales objDtlBean = new clsPOSGroupSubGroupWiseSales();
					String subGroupCode = obj[0].toString();

					if (mapSubGroupWiseDtl.containsKey(subGroupCode)) {
						objDtlBean = mapSubGroupWiseDtl.get(subGroupCode);
						objDtlBean.setQty(objDtlBean.getQty() + Double.parseDouble(obj[2].toString()));
						objDtlBean.setSubTotal(objDtlBean.getSubTotal() + Double.parseDouble(obj[7].toString()));
						objDtlBean.setDiscAmt(objDtlBean.getDiscAmt() + Double.parseDouble(obj[8].toString()));
						objDtlBean.setDblNetTotal(objDtlBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
						mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
					} else {
						objDtlBean = new clsPOSGroupSubGroupWiseSales();
						objDtlBean.setSubGroupCode(obj[0].toString());
						objDtlBean.setSubGroupName(obj[1].toString());
						objDtlBean.setPosName(obj[4].toString());
						objDtlBean.setQty(Double.parseDouble(obj[2].toString()));
						objDtlBean.setSubTotal(Double.parseDouble(obj[7].toString()));
						objDtlBean.setDiscAmt(Double.parseDouble(obj[8].toString()));
						objDtlBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));

						mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
					}
				}
			}

			List listQData = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listQData.size() > 0) {
				for (int i = 0; i < listQData.size(); i++) {
					Object[] obj = (Object[]) listQData.get(i);
					clsPOSGroupSubGroupWiseSales objDtlBean = new clsPOSGroupSubGroupWiseSales();
					String subGroupCode = obj[0].toString();

					if (mapSubGroupWiseDtl.containsKey(subGroupCode)) {
						objDtlBean = mapSubGroupWiseDtl.get(subGroupCode);
						objDtlBean.setQty(objDtlBean.getQty() + Double.parseDouble(obj[2].toString()));
						objDtlBean.setSubTotal(objDtlBean.getSubTotal() + Double.parseDouble(obj[7].toString()));
						objDtlBean.setDiscAmt(objDtlBean.getDiscAmt() + Double.parseDouble(obj[8].toString()));
						objDtlBean.setDblNetTotal(objDtlBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
						mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
					} else {
						objDtlBean = new clsPOSGroupSubGroupWiseSales();
						objDtlBean.setSubGroupCode(obj[0].toString());
						objDtlBean.setSubGroupName(obj[1].toString());
						objDtlBean.setPosName(obj[4].toString());
						objDtlBean.setQty(Double.parseDouble(obj[2].toString()));
						objDtlBean.setSubTotal(Double.parseDouble(obj[7].toString()));
						objDtlBean.setDiscAmt(Double.parseDouble(obj[8].toString()));
						objDtlBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));

						mapSubGroupWiseDtl.put(subGroupCode, objDtlBean);
					}
				}
			}
			Comparator<clsPOSGroupSubGroupWiseSales> subGroupCodeComparator = new Comparator<clsPOSGroupSubGroupWiseSales>() {

				@Override
				public int compare(clsPOSGroupSubGroupWiseSales o1, clsPOSGroupSubGroupWiseSales o2) {
					return o1.getSubGroupCode().compareToIgnoreCase(o2.getSubGroupCode());
				}
			};
			Comparator<clsPOSGroupSubGroupWiseSales> subGroupNameComparator = new Comparator<clsPOSGroupSubGroupWiseSales>() {

				@Override
				public int compare(clsPOSGroupSubGroupWiseSales o1, clsPOSGroupSubGroupWiseSales o2) {
					return o1.getSubGroupName().compareToIgnoreCase(o2.getSubGroupName());
				}
			};

			listOfData.addAll(mapSubGroupWiseDtl.values());

			Collections.sort(listOfData,
					new clsPOSGroupSubGroupWiseSalesComparator(subGroupCodeComparator, subGroupNameComparator));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfData;
	}

	public List funProcessTaxSummaryForSubGroupWiseSummaryReport(String posCode, String fromDate, String toDate) {
		List<clsPOSTaxCalculationDtls> listOfTaxData = new ArrayList<clsPOSTaxCalculationDtls>();
		try {
			StringBuilder sqlTax = new StringBuilder();
			sqlTax.append("select c.strTaxDesc,sum(b.dblTaxableAmount),sum(b.dblTaxAmount) \n"
					+ "from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c\n"
					+ "where a.strBillNo=b.strBillNo and b.strTaxCode=c.strTaxCode\n"
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'\n");
			if (!posCode.equals("All")) {
				sqlTax.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			sqlTax.append(" group by b.strTaxCode");
			List listTax = objBaseService.funGetList(sqlTax, "sql");
			if (listTax.size() > 0) {
				for (int i = 0; i < listTax.size(); i++) {
					Object[] obj = (Object[]) listTax.get(i);
					clsPOSTaxCalculationDtls objBean = new clsPOSTaxCalculationDtls();
					objBean.setStrTaxDesc(obj[0].toString());
					objBean.setTaxableAmount(Double.parseDouble(obj[1].toString()));
					objBean.setTaxAmount(Double.parseDouble(obj[2].toString()));
					listOfTaxData.add(objBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfTaxData;
	}

	public List funProcessSettlementSummaryForSubGroupWiseSummaryReport(String posCode, String fromDate,
			String toDate) {
		List<clsPOSBillItemDtlBean> listOfSettlementData = new ArrayList<clsPOSBillItemDtlBean>();
		StringBuilder sqlSettlement = new StringBuilder();
		try {
			sqlSettlement.append("select c.strSettelmentDesc,sum(b.dblSettlementAmt) \n"
					+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c\n"
					+ "where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode\n"
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'\n");
			if (!posCode.equals("All")) {
				sqlSettlement.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			sqlSettlement.append(" group by b.strSettlementCode,c.strSettelmentDesc");
			List listSettlementData = objBaseService.funGetList(sqlSettlement, "sql");
			if (listSettlementData.size() > 0) {
				for (int i = 0; i < listSettlementData.size(); i++) {
					Object[] obj = (Object[]) listSettlementData.get(i);
					clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
					objBean.setStrSettelmentDesc(obj[0].toString());
					objBean.setDblSettlementAmt(Double.parseDouble(obj[1].toString()));
					listOfSettlementData.add(objBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfSettlementData;
	}

	public List funProcessLiveBlindSettlementWiseReport(String posCode, String fromDate, String toDate, String qType,String enableShiftYN,String strShiftNo) {
		StringBuilder sbSqlLive = new StringBuilder();

		StringBuilder sqlFilter = new StringBuilder();
		DecimalFormat decimalFormat2Dec = new DecimalFormat("0.00");
		DecimalFormat decimalFormat0Dec = new DecimalFormat("0");
		List listSqlLiveData = new ArrayList();
		try {
			if (qType.equalsIgnoreCase("live")) {
				sbSqlLive.setLength(0);
				sbSqlLive
						.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname,count(*) "
								+ "FROM tblbillhd a, tblbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
								+ "Where a.strBillNo = b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
								+ " and a.strClientCode=b.strClientCode " + "and a.strposcode=d.strposcode "
								+ "and b.strSettlementCode = c.strSettelmentCode "
								+ " and c.strSettelmentType!='Complementary' AND c.strSettelmentType!='cash' ");
				sqlFilter.append("and date(a.dteBillDate ) BETWEEN  '" + fromDate + "' AND '" + toDate + "' ");
				if (!"All".equalsIgnoreCase(posCode)) {
					sqlFilter.append("and  a.strPosCode='" + posCode + "' ");
				}
				if(enableShiftYN.equalsIgnoreCase("Y"))
				{	
				 sqlFilter.append(" and a.intShiftCode = '" + strShiftNo + "' ");
				}	
				sqlFilter.append("GROUP BY c.strSettelmentDesc, a.strPosCode");

				sbSqlLive.append(sqlFilter);

				listSqlLiveData = objBaseService.funGetList(sbSqlLive, "sql");
			} else {
				sbSqlLive.setLength(0);
				sbSqlLive
						.append("SELECT a.strPosCode,c.strSettelmentDesc,sum(b.dblSettlementAmt),d.strposname,count(*) "
								+ "FROM tblqbillhd a, tblqbillsettlementdtl b, tblsettelmenthd c ,tblposmaster d "
								+ "Where a.strBillNo = b.strBillNo " + " and date(a.dteBillDate)=date(b.dteBillDate) "
								+ " and a.strClientCode=b.strClientCode " + "and a.strposcode=d.strposcode "
								+ "and b.strSettlementCode = c.strSettelmentCode "
								+ " and c.strSettelmentType!='Complementary' AND c.strSettelmentType!='cash' ");

				sqlFilter.append("and date(a.dteBillDate ) BETWEEN  '" + fromDate + "' AND '" + toDate + "' ");
				if (!"All".equalsIgnoreCase(posCode)) {
					sqlFilter.append("and  a.strPosCode='" + posCode + "' ");
				}

				if(enableShiftYN.equalsIgnoreCase("Y"))
				{	
				 sqlFilter.append(" and a.intShiftCode = '" + strShiftNo + "' ");
				}	

				sqlFilter.append("GROUP BY c.strSettelmentDesc, a.strPosCode");

				sbSqlLive.append(sqlFilter);

				listSqlLiveData = objBaseService.funGetList(sbSqlLive, "sql");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSqlLiveData;
	}

	public List funProcessLiveDataForOperatorWiseReport(String posCode, String fromDate, String toDate, String userCode,
			String strShiftNo, String settleCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List listSettlementWiseBills = new ArrayList();

		try {
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"SELECT a.strBillNo,date(a.dteBillDate),ifnull(e.strUserCode,'SANGUINE'),a.strUserEdited,b.strPOSName "
							+ ", IFNULL(a.dblSubTotal,0.00)dblSubTotal "
							+ ", IFNULL(a.dblDiscountAmt,0.00)dblDiscountAmt,(dblSubTotal-dblDiscountAmt)dblNetTotal"
							+ ",a.dblTaxAmt, IFNULL(d.strSettelmentDesc,'') strSettelmentDesc "
							+ ", IFNULL(sum(c.dblSettlementAmt),0.00)dblSettlementAmt,a.strSettelmentMode "
							+ "FROM tblbillhd a " + "LEFT OUTER JOIN tblposmaster b ON a.strPOSCode=b.strPOSCode "
							+ "LEFT OUTER JOIN tblbillsettlementdtl c ON a.strBillNo=c.strBillNo AND a.strClientCode=c.strClientCode AND DATE(a.dteBillDate)= DATE(c.dteBillDate) "
							+ "LEFT OUTER JOIN tblsettelmenthd d ON c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tbluserhd e on a.strUserEdited=e.strUserCode "
							+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			if (!userCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("  and e.strUserCode='" + userCode + "'");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			
			if (!settleCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("  and d.strSettelmentCode='" + settleCode + "'");
			}

			sqlBuilder.append("group by a.dteBillDate,a.strBillNo,c.strSettlementCode "
					+ "order by e.strUserCode,a.dteBillDate ");
			listSettlementWiseBills = objBaseService.funGetList(sqlBuilder, "sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSettlementWiseBills;
	}

	public List funProcessQFileDataForOperatorWiseReport(String posCode, String fromDate, String toDate,
			String userCode, String strShiftNo, String settleCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List listSettlementWiseBills = new ArrayList();

		try {
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"SELECT a.strBillNo,date(a.dteBillDate),ifnull(e.strUserCode,'SANGUINE'),a.strUserEdited,b.strPOSName "
							+ ", IFNULL(a.dblSubTotal,0.00)dblSubTotal "
							+ ", IFNULL(a.dblDiscountAmt,0.00)dblDiscountAmt,(dblSubTotal-dblDiscountAmt)dblNetTotal"
							+ ",a.dblTaxAmt, IFNULL(d.strSettelmentDesc,'') strSettelmentDesc "
							+ ", IFNULL(sum(c.dblSettlementAmt),0.00)dblSettlementAmt,a.strSettelmentMode "
							+ "FROM tblqbillhd a " + "LEFT OUTER JOIN tblposmaster b ON a.strPOSCode=b.strPOSCode "
							+ "LEFT OUTER JOIN tblqbillsettlementdtl c ON a.strBillNo=c.strBillNo AND a.strClientCode=c.strClientCode AND DATE(a.dteBillDate)= DATE(c.dteBillDate) "
							+ "LEFT OUTER JOIN tblsettelmenthd d ON c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tbluserhd e on a.strUserEdited=e.strUserCode "
							+ "WHERE DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
			}
			if (!userCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("  and e.strUserCode='" + userCode + "'");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
			
			if (!settleCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("  and d.strSettelmentCode='" + settleCode + "'");
			}

			sqlBuilder.append("group by a.dteBillDate,a.strBillNo,c.strSettlementCode "
					+ "order by e.strUserCode,a.dteBillDate ");

			listSettlementWiseBills = objBaseService.funGetList(sqlBuilder, "sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSettlementWiseBills;
	}

	public List funProcessGuestCreditReport(String posCode, String fromDate, String toDate) {
		StringBuilder sqlLiveBuilder = new StringBuilder();
		StringBuilder sqlQBuilder = new StringBuilder();
		List<clsPOSBillDtl> listOfGuestCreditData = new ArrayList<>();

		try {
			sqlLiveBuilder.append(
					"select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) "
							+ ",h.strPosName,d.strSettelmentDesc,a.strKOTNo,b.strPOSCode,b.strRemarks,ifnull(e.strTableName,'') as strTableName"
							+ ",f.strCustomerName,ifnull(g.strWShortName,'') as strWShortName,b.dblDeliveryCharges "
							+ ",a.dblDiscountAmt,a.dblTaxAmount,(a.dblAmount-a.dblDiscountAmt+a.dblTaxAmount)GrandTotal "
							+ ",f.longMobileNo,ifnull(i.strReasonName,'') " + "from tblbilldtl a "
							+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
							+ "left outer join tblcustomermaster f on c.strCustomerCode=f.strCustomerCode "
							+ "left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
							+ "left outer join tblposmaster h on b.strPOSCode=h.strPosCode "
							+ "left outer join tblreasonmaster i on b.strReasonCode=i.strReasonCode "
							+ "where date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "'  "
							+ "and d.strSettelmentType='Credit' ");

			sqlQBuilder.append(
					"select a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,date(a.dteBillDate) "
							+ ",h.strPosName,d.strSettelmentDesc,a.strKOTNo,b.strPOSCode,b.strRemarks,ifnull(e.strTableName,'') as strTableName"
							+ ",f.strCustomerName,ifnull(g.strWShortName,'') as strWShortName,b.dblDeliveryCharges "
							+ ",a.dblDiscountAmt,a.dblTaxAmount,(a.dblAmount-a.dblDiscountAmt+a.dblTaxAmount)GrandTotal "
							+ ",f.longMobileNo,ifnull(i.strReasonName,'') " + "from tblqbilldtl a "
							+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo  and date(a.dteBillDate)=date(c.dteBillDate) "
							+ "left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
							+ "left outer join tbltablemaster e on b.strTableNo=e.strTableNo "
							+ "left outer join tblcustomermaster f on c.strCustomerCode=f.strCustomerCode "
							+ "left outer join tblwaitermaster g on b.strWaiterNo=g.strWaiterNo "
							+ "left outer join tblposmaster h on b.strPOSCode=h.strPosCode "
							+ "left outer join tblreasonmaster i on b.strReasonCode=i.strReasonCode "
							+ "where date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "'  "
							+ "and d.strSettelmentType='Credit' ");

			if (!posCode.equalsIgnoreCase("All")) {
				sqlLiveBuilder.append("and b.strPOSCode='" + posCode + "' ");
				sqlQBuilder.append("and b.strPOSCode='" + posCode + "' ");
			}

			sqlLiveBuilder.append("group by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode "
					+ "order by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode ");
			sqlQBuilder.append("group by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode "
					+ "order by b.strPOSCode,a.strBillNo,a.strKOTNo,a.strItemCode ");

			// live
			List listLiveData = objBaseService.funGetList(sqlLiveBuilder, "sql");
			if (listLiveData.size() > 0) {
				for (int i = 0; i < listLiveData.size(); i++) {
					Object[] obj = (Object[]) listLiveData.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();
					String dteBillDate = "";
					String billDate = obj[6].toString();
					String dateParts[] = billDate.split("-");
					dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

					objBillDtlBean.setStrBillNo(obj[0].toString());
					objBillDtlBean.setStrItemCode(obj[1].toString());
					objBillDtlBean.setStrItemName(obj[2].toString());
					objBillDtlBean.setDblRate(Double.parseDouble(obj[3].toString()));
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[5].toString()));
					objBillDtlBean.setDteBillDate(dteBillDate);
					objBillDtlBean.setStrPosName(obj[7].toString());
					objBillDtlBean.setStrSettlementName(obj[8].toString());
					objBillDtlBean.setStrKOTNo(obj[9].toString());
					objBillDtlBean.setStrPOSCode(obj[10].toString());
					objBillDtlBean.setStrRemarks(obj[11].toString());
					objBillDtlBean.setStrTableName(obj[12].toString());
					objBillDtlBean.setStrCustomerName(obj[13].toString());
					objBillDtlBean.setStrWShortName(obj[14].toString());
					objBillDtlBean.setDblDelCharges(Double.parseDouble(obj[15].toString()));
					objBillDtlBean.setDblDiscountAmt(Double.parseDouble(obj[16].toString()));// disc
					objBillDtlBean.setDblTaxAmount(Double.parseDouble(obj[17].toString()));// tax
					objBillDtlBean.setDblBillAmt(Double.parseDouble(obj[18].toString()));// grandtotal
					objBillDtlBean.setLongMobileNo(Long.parseLong(obj[19].toString()));
					objBillDtlBean.setStrReasonName(obj[20].toString());

					listOfGuestCreditData.add(objBillDtlBean);
				}
			}

			// Q
			List listQFileData = objBaseService.funGetList(sqlQBuilder, "sql");
			if (listQFileData.size() > 0) {
				for (int i = 0; i < listQFileData.size(); i++) {
					Object[] obj = (Object[]) listQFileData.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();
					String dteBillDate = "";
					String billDate = obj[6].toString();
					String dateParts[] = billDate.split("-");
					dteBillDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

					objBillDtlBean.setStrBillNo(obj[0].toString());
					objBillDtlBean.setStrItemCode(obj[1].toString());
					objBillDtlBean.setStrItemName(obj[2].toString());
					objBillDtlBean.setDblRate(Double.parseDouble(obj[3].toString()));
					objBillDtlBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
					objBillDtlBean.setDblAmount(Double.parseDouble(obj[5].toString()));
					objBillDtlBean.setDteBillDate(dteBillDate);
					objBillDtlBean.setStrPosName(obj[7].toString());
					objBillDtlBean.setStrSettlementName(obj[8].toString());
					objBillDtlBean.setStrKOTNo(obj[9].toString());
					objBillDtlBean.setStrPOSCode(obj[10].toString());
					objBillDtlBean.setStrRemarks(obj[11].toString());
					objBillDtlBean.setStrTableName(obj[12].toString());
					objBillDtlBean.setStrCustomerName(obj[13].toString());
					objBillDtlBean.setStrWShortName(obj[14].toString());
					objBillDtlBean.setDblDelCharges(Double.parseDouble(obj[15].toString()));
					objBillDtlBean.setDblDiscountAmt(Double.parseDouble(obj[16].toString()));// disc
					objBillDtlBean.setDblTaxAmount(Double.parseDouble(obj[17].toString()));// tax
					objBillDtlBean.setDblBillAmt(Double.parseDouble(obj[18].toString()));// grandtotal
					objBillDtlBean.setLongMobileNo(Long.parseLong(obj[19].toString()));
					objBillDtlBean.setStrReasonName(obj[20].toString());

					listOfGuestCreditData.add(objBillDtlBean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfGuestCreditData;

	}

	public List funProcessItemMasterListing() {
		List<clsPOSBillDtl> listOfItemMasterListing = new ArrayList<>();
		StringBuilder sqlBuilder = new StringBuilder();

		try {
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strItemCode,a.strItemName,b.strSubGroupName,c.strGroupName,ifnull(a.strTaxIndicator,'')  "
							+ "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c "
							+ "where a.strSubGroupCode=b.strSubGroupCode " + "and b.strGroupCode=c.strGroupCode "
							+ "group by c.strGroupCode,b.strSubGroupCode,a.strItemCode,a.strItemName "
							+ "order by c.strGroupCode,b.strSubGroupCode,a.strItemCode,a.strItemName ");

			List listItemMaster = objBaseService.funGetList(sqlBuilder, "sql");
			if (listItemMaster.size() > 0) {
				for (int i = 0; i < listItemMaster.size(); i++) {
					Object[] obj = (Object[]) listItemMaster.get(i);
					clsPOSBillDtl objItemDtl = new clsPOSBillDtl();

					objItemDtl.setStrItemCode(obj[0].toString());
					objItemDtl.setStrItemName(obj[1].toString());
					objItemDtl.setStrSubGroupName(obj[2].toString());
					objItemDtl.setStrGroupName(obj[3].toString());
					objItemDtl.setStrTaxIndicator(obj[4].toString());

					listOfItemMasterListing.add(objItemDtl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfItemMasterListing;

	}

	public List funProcessAuditFlashReport(String fromDate, String toDate, String posName, String userName,
			String strReportType, String reasonName, String auditType, String clientCode, String strSorting,
			String strType, String strPOSCode, String reasonCode, String userCode) {
		StringBuilder sbSql = new StringBuilder();
		List listArrColHeader = new ArrayList();
		LinkedHashMap resMap = new LinkedHashMap();
		List totalList = new ArrayList();
		List listArr = new ArrayList();
		Map map = new HashMap();
		int colCount = 5;

		try {
			double sumBillAmt = 0.00, sumNewAmt = 0.00;
			double sumQty = 0.00, discAmt = 0.0, sumTotalAmt = 0.00;
			switch (auditType) {

			// Modified Bill
			case "Modified Bill":

				if (strReportType.equalsIgnoreCase("Summary")) {

					sbSql.setLength(0);
					sbSql.append(
							"select a.strBillNo as BillNo, DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate ,"
									+ " DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as ModifiedDate ,TIME_FORMAT(time(a.dteBillDate),'%h:%i') as EntryTime , "
									+ " TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') as ModifyTime,a.dblActualAmount as BillAmt ,"
									+ " a.dblModifiedAmount as NetAmt,a.strUserCreated as UserCreated, "
									+ " a.strUserEdited as UserEdited,ifnull(b.strReasonName,'') as ReasonName,ifnull(a.strRemark,'')"
									+ " ,(a.dblActualAmount-a.dblModifiedAmount) as DiscAmt "
									+ " from tblvoidbillhd a left outer join tblreasonmaster b on a.strReasonCode=b.strReasonCode ");
					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strUserCreated='" + userCode + "'"
								+ " and a.strPOSCode='" + strPOSCode + "'  and a.strreasonCode='" + reasonCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strPOSCode='" + strPOSCode + "'  and  "
								+ "a.strreasonCode='" + reasonCode + "' group by a.strBillNo,a.dteModifyVoidBill");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strUserCreated='" + userCode + "'  "
								+ "and a.strreasonCode='" + reasonCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strUserCreated='" + userCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {

						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' and a.strPOSCode='" + strPOSCode + "' "
								+ "group by a.strBillNo,a.dteModifyVoidBill");
					} else {
						sbSql.append(" where a.strTransType='MB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' group by a.strBillNo,a.dteModifyVoidBill");
					}
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append(" and a.strVoidBillType ='" + strType + "' ");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append(" and a.strVoidBillType ='Bill Void' ");
					} else {
						sbSql.append(" and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' ) ");
					}
					if (strSorting.equalsIgnoreCase("BILL")) {
						sbSql.append(" order by a.strBillNo");
					} else {
						sbSql.append(" order by a.dblActualAmount");
					}
					List listSql = objBaseService.funGetList(sbSql, "sql");

					if (listSql.size() > 0) {

						for (int i = 0; i < listSql.size(); i++) {
							Object[] obj = (Object[]) listSql.get(i);
							clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
							objBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBillItemDtlBean.setStrEntryTime(obj[3].toString());
							objBillItemDtlBean.setStrModifiyTime(obj[4].toString());
							objBillItemDtlBean.setDblAmount(Double.parseDouble(obj[5].toString()));
							objBillItemDtlBean.setDblAmountTemp(Double.parseDouble(obj[6].toString()));
							objBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[11].toString()));
							objBillItemDtlBean.setStrUserCreated(obj[7].toString());
							objBillItemDtlBean.setStrUserEdited(obj[8].toString());
							objBillItemDtlBean.setStrReasonName(obj[9].toString());
							objBillItemDtlBean.setStrRemark(obj[10].toString());

							listArr.add(objBillItemDtlBean);

							sumBillAmt = sumBillAmt + Double.parseDouble(obj[5].toString());
							sumNewAmt = sumNewAmt + Double.parseDouble(obj[6].toString());
							discAmt += Double.parseDouble(obj[11].toString());
						}

					}
					totalList.add("Total");
					totalList.add(sumBillAmt);
					totalList.add(sumNewAmt);
					totalList.add(discAmt);
					totalList.add(" ");
					resMap.put("totalList", totalList);
					resMap.put("listArr", listArr);
					resMap.put("ColHeader", listArrColHeader);
				} else {

					sbSql.setLength(0);
					sbSql.append(
							"select a.strBillNo,DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as ModifiedDate,"
									+ "Time(a.dteBillDate) EntryTime,Time(a.dteModifyVoidBill) ModifiedTime,a.strItemName,a.intQuantity,sum(a.dblAmount) as Amount,"
									+ "b.strUserCreated as Usercreated,b.strUserEdited as UserEdited,ifnull(b.strRemark,'') "
									+ " from tblvoidbilldtl a, tblvoidbillhd b ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(
								"where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
										+ fromDate + "' and '" + toDate + "' " + "and a.strPOSCode='" + strPOSCode
										+ "' and " + "a.strUserCreated='" + userCode + "' and a.strreasonCode='"
										+ reasonCode + "' " + "group by a.strItemName,a.strBillNo ");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(
								"where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
										+ fromDate + "' and '" + toDate + "' and  a.strUserCreated='" + userCode
										+ "' and a.strreasonCode='" + reasonCode + "' "
										+ "group by a.strItemName,a.strBillNo ");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(
								"where a.strBillNo=b.strBillNo and a.strTransType='MB' and Date(a.dteModifyVoidBill) between '"
										+ fromDate + "' and '" + toDate + "'" + " and a.strPOSCode='" + strPOSCode
										+ "' and a.strreasonCode='" + reasonCode + "' "
										+ "group by a.strItemName,a.strBillNo ");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strPOSCode='" + strPOSCode + "' and a.strUserCreated='" + userCode + "' "
								+ "group by a.strItemName,a.strBillNo ");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strPOSCode='" + strPOSCode + "'  " + "group by a.strItemName,a.strBillNo ");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
								+ "and  a.strUserCreated='" + userCode + "' " + "group by a.strItemName,a.strBillNo ");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
								+ "and  a.strreasonCode='" + reasonCode + "' " + "group by a.strItemName,a.strBillNo ");
					} else {
						sbSql.append("where a.strBillNo=b.strBillNo and a.strTransType='MB' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' "
								+ "group by a.strItemName,a.strBillNo ");
					}
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append("and b.strVoidBillType ='" + strType + "' ");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append("and b.strVoidBillType ='Bill Void' ");
					} else {
						sbSql.append("and (b.strVoidBillType = 'Bill Void' or b.strVoidBillType = 'ITEM VOID') ");
					}
					if (strSorting.equalsIgnoreCase("BILL")) {
						sbSql.append(" order by a.strBillNo asc");
					} else {
						sbSql.append(" order by sum(a.dblAmount) asc");
					}

					List listSql = objBaseService.funGetList(sbSql, "sql");

					if (listSql.size() > 0) {

						for (int i = 0; i < listSql.size(); i++) {
							Object[] obj = (Object[]) listSql.get(i);
							clsPOSBillItemDtlBean objAuditFlashBean = new clsPOSBillItemDtlBean();
							objAuditFlashBean.setStrBillNo(obj[0].toString());
							objAuditFlashBean.setDteBillDate(obj[1].toString());
							objAuditFlashBean.setStrEntryTime(obj[3].toString());
							objAuditFlashBean.setStrModifiyTime(obj[4].toString());
							objAuditFlashBean.setStrItemName(obj[5].toString());
							objAuditFlashBean.setDblQuantity(Integer.parseInt(obj[6].toString()));
							objAuditFlashBean.setBillAmt(Double.parseDouble(obj[7].toString()));
							objAuditFlashBean.setStrUserCreated(obj[8].toString());
							objAuditFlashBean.setStrUserEdited(obj[9].toString());
							objAuditFlashBean.setStrRemark(obj[10].toString());

							listArr.add(objAuditFlashBean);

							sumQty = sumQty + Double.parseDouble(obj[6].toString());
							sumBillAmt = sumBillAmt + Double.parseDouble(obj[7].toString());
						}

					}

				}
				break;

			case "Voided Bill":
				StringBuilder sbSqlMod = new StringBuilder();
				List<clsPOSBillItemDtlBean> arrListVoidBillWise = new ArrayList<clsPOSBillItemDtlBean>();

				sbSql.setLength(0);
				if (strReportType.equalsIgnoreCase("Summary")) {

					sbSql.setLength(0);
					sbSql.append(
							"select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
									+ "TIME_FORMAT(time(a.dteBillDate),'%h:%i')  As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime, a.dblModifiedAmount,"
									+ "a.strUserEdited as UserEdited, a.strReasonName as Reason,ifnull(a.strRemark,'')"
									+ ",b.strPosCode,b.strPosName " + " from tblvoidbillhd a,tblposmaster b "
									+ " where a.strTransType='VB'  " + " and a.strPosCode=b.strPosCode ");
					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and " + "strUserCreated='" + userCode
								+ "' and strreasonCode='" + reasonCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and a.strPOSCode='" + strPOSCode + "' and " + "strreasonCode='" + reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and a.strUserCreated='" + userCode + "' " + "and strreasonCode='" + reasonCode
								+ "' and Date(a.dteModifyVoidBill) " + "between '" + fromDate + "' and '" + toDate
								+ "' ");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and a.strPOSCode='" + strPOSCode + "' " + "and strUserCreated='" + userCode
								+ "'  and Date(a.dteModifyVoidBill) " + "between '" + fromDate + "' and '" + toDate
								+ "' ");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and a.strPOSCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append("  and strUserCreated='" + userCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					} else {
						sbSql.append(
								"  and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");
					}
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='" + strType + "'");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='Bill Void'");
					} else {
						sbSql.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
					}
					sbSql.append(" group by a.strBillNo,date(a.dteBillDate) ");

					if (strSorting.equalsIgnoreCase("BILL")) {
						sbSql.append(" order by a.strBillNo");

						List listData = objBaseService.funGetList(sbSql, "sql");
						if (listData.size() > 0) {
							for (int i = 0; i < listData.size(); i++) {
								Object[] obj = (Object[]) listData.get(i);
								double amountTemp = Double.parseDouble(obj[5].toString());
								String billNo = obj[0].toString();
								StringBuilder sql = new StringBuilder();
								sql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
								List list2 = objBaseService.funGetList(sql, "sql");
								int count = 0;
								if (list2.size() > 0) {
									count =(new BigInteger(list2.get(0).toString())).intValue();
								}
								if (count > 0) {
									sql.setLength(0);
									sql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='"
											+ billNo + "' ");
									list2 = objBaseService.funGetList(sql, "sql");
									Double temp = 0.0;
									if (list2.size() > 0) {
										temp = (new BigDecimal(list2.get(0).toString())).doubleValue();
									}
									amountTemp = amountTemp + temp;
								}

								clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
								objBillItemDtlBean.setStrPosCode(obj[9].toString());
								objBillItemDtlBean.setStrPosName(obj[10].toString());
								objBillItemDtlBean.setStrBillNo(obj[0].toString());
								objBillItemDtlBean.setDteBillDate(obj[1].toString());
								objBillItemDtlBean.setDteVoidedDate(obj[2].toString());
								objBillItemDtlBean.setStrEntryTime(obj[3].toString());//
								objBillItemDtlBean.setStrVoidedTime(obj[4].toString());
								objBillItemDtlBean.setDblAmountTemp(amountTemp);
								objBillItemDtlBean.setStrUserEdited(obj[6].toString());
								objBillItemDtlBean.setStrReasonName(obj[7].toString());
								objBillItemDtlBean.setStrRemark(obj[8].toString());
								arrListVoidBillWise.add(objBillItemDtlBean);
								sumTotalAmt = sumTotalAmt + amountTemp;
							}

						}

					} else {

						List listData = objBaseService.funGetList(sbSql, "sql");
						if (listData.size() > 0) {
							for (int i = 0; i < listData.size(); i++) {
								Object[] obj = (Object[]) listData.get(i);
								clsPOSBillItemDtlBean objBillItemDtlBean = new clsPOSBillItemDtlBean();
								double amountTemp = Double.parseDouble(obj[5].toString());
								String billNo = obj[0].toString();
								// obj.setDblAmount(amountTemp);
								// obj.setStrBillNo(billNo);
								StringBuilder sql = new StringBuilder();
								sql.append("Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
								List list2 = objBaseService.funGetList(sql, "sql");
								int count = 0;
								if (list2.size() > 0) {
									count = (int) list2.get(0);
								}
								if (count > 0) {
									sql.append("select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='"
											+ billNo + "' ");
									list2 = objBaseService.funGetList(sql, "sql");
									Double temp = 0.0;
									if (list2.size() > 0) {
										temp = (Double) list2.get(0);
									}
									amountTemp = amountTemp + temp;
									// obj.setDblAmount(amountTemp);
								}
								objBillItemDtlBean.setStrPosCode(obj[9].toString());
								objBillItemDtlBean.setStrPosName(obj[10].toString());
								objBillItemDtlBean.setStrBillNo(obj[0].toString());
								objBillItemDtlBean.setDteBillDate(obj[1].toString());
								objBillItemDtlBean.setDteVoidedDate(obj[2].toString());
								objBillItemDtlBean.setStrEntryTime(obj[3].toString());//
								objBillItemDtlBean.setStrVoidedTime(obj[4].toString());
								objBillItemDtlBean.setDblAmountTemp(amountTemp);
								objBillItemDtlBean.setStrUserEdited(obj[6].toString());
								objBillItemDtlBean.setStrReasonName(obj[7].toString());
								objBillItemDtlBean.setStrRemark(obj[8].toString());

								arrListVoidBillWise.add(objBillItemDtlBean);
								sumTotalAmt = sumTotalAmt + amountTemp;
							}
						}

					}
					Comparator<clsPOSBillItemDtlBean> compareBillItem = new Comparator<clsPOSBillItemDtlBean>() {

						@Override
						public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2) {
							double dblAmount = o1.getDblAmountTemp();
							double dblAmount2 = o2.getDblAmountTemp();

							if (dblAmount == dblAmount2) {
								return 0;
							} else if (dblAmount > dblAmount2) {
								return 1;
							} else {
								return -1;
							}

						}
					};

					Collections.sort(arrListVoidBillWise, compareBillItem);
					for (clsPOSBillItemDtlBean obj : arrListVoidBillWise) {
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						objBean.setStrPosCode(obj.getStrPosCode());
						objBean.setStrPosName(obj.getStrPosName());
						objBean.setStrBillNo(obj.getStrBillNo());
						objBean.setDteBillDate(obj.getDteBillDate());
						objBean.setDteVoidedDate(obj.getDteVoidedDate());
						objBean.setStrEntryTime(obj.getStrEntryTime());
						objBean.setStrVoidedTime(obj.getStrVoidedTime());
						objBean.setDblAmountTemp(obj.getDblAmountTemp());
						objBean.setStrUserEdited(obj.getStrUserEdited());
						objBean.setStrReasonName(obj.getStrReasonName());
						objBean.setStrRemark(obj.getStrRemark());

						listArr.add(objBean);
					}

				} else {
					sbSql.setLength(0);
					sbSql.append(
							"(select a.strBillNo as strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
									+ "TIME_FORMAT(time(a.dteBillDate),'%h:%i') As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strItemName,"
									+ "b.intQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'')"
									+ ",c.strPosCode,c.strPosName "
									+ "from tblvoidbillhd a,tblvoidbilldtl b,tblposmaster c "
									+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "and a.strPosCode=c.strPosCode ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
								+ "between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and b.strTransType='VB' and a.strUserCreated='" + userCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill)"
								+ " between '" + fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and b.strTransType='VB' and Date(a.dteModifyVoidBill) " + "between '" + fromDate
								+ "' and '" + toDate + "'");
					}
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='" + strType + "'");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='Bill Void'");
					} else {
						sbSql.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
					}

					sbSql.append(" group by a.strBillNo,b.strItemCode)");

					sbSqlMod.setLength(0);
					sbSqlMod.append(
							"(select a.strBillNo as strBillNo,DATE_FORMAT(Date(a.dteBillDate),'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill),'%d-%m-%Y') as VoidedDate,"
									+ "TIME_FORMAT(time(a.dteBillDate),'%h:%i')  As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strModifierName,"
									+ "b.dblQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'')"
									+ ",c.strPosCode,c.strPosName "
									+ "from tblvoidbillhd a,tblvoidmodifierdtl b,tblposmaster c "
									+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "and a.strPosCode=c.strPosCode ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strPOSCode='" + strPOSCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
								+ "between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='VB' and a.strUserCreated='" + userCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill)"
								+ " between '" + fromDate + "' and '" + toDate + "'");
					} else {
						sbSqlMod.append(" and a.strTransType='VB' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "'");
					}
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSqlMod.append("and a.strVoidBillType ='" + strType + "'");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSqlMod.append("and a.strVoidBillType ='Bill Void'");
					} else {
						sbSqlMod.append("and (a.strVoidBillType = 'Bill Void' or a.strVoidBillType = 'ITEM VOID' )");
					}

					sbSqlMod.append(" group by a.strBillNo,b.strModifierCode)");
					if (strSorting.equalsIgnoreCase("BILL")) {
						sbSqlMod.append(" order by strBillNo");
					} else {
						sbSqlMod.append(" order by BillAmount");
					}

					StringBuilder sql = new StringBuilder();
					sql.append(sbSql.toString() + " union " + sbSqlMod.toString());
					List listData = objBaseService.funGetList(sql, "sql");
					if (listData.size() > 0) {
						for (int i = 0; i < listData.size(); i++) {
							Object[] obj = (Object[]) listData.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							objBean.setStrPosCode(obj[11].toString());
							objBean.setStrPosName(obj[12].toString());
							objBean.setStrBillNo(obj[0].toString());
							objBean.setDteBillDate(obj[1].toString());
							objBean.setDteVoidedDate(obj[2].toString());
							objBean.setStrEntryTime(obj[3].toString());
							objBean.setStrVoidedTime(obj[4].toString());
							objBean.setStrItemName(obj[5].toString());
							objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
							objBean.setStrUserEdited(obj[8].toString());
							objBean.setStrReasonName(obj[9].toString());
							objBean.setStrRemark(obj[10].toString());
							listArr.add(objBean);

							sumQty = sumQty + Double.parseDouble(obj[6].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
						}
					}

				}
				break;
			case "Voided Advanced Order":

				sbSqlMod = new StringBuilder();
				List<clsPOSBillItemDtlBean> arrListVoidAdvOrder = new ArrayList<clsPOSBillItemDtlBean>();
				sbSql.setLength(0);

				if (strReportType.equalsIgnoreCase("Summary")) {

					sbSql.setLength(0);
					sbSql.append(
							"select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y')  as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y') as VoidedDate,"
									+ "Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount,"
									+ "a.strUserEdited as UserEdited, a.strReasonName as Reason"
									+ " from tblvoidbillhd a ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and "
								+ "strUserCreated='" + userCode + "' and strreasonCode='" + reasonCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and  "
								+ "strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strUserCreated='" + userCode + "' "
								+ "and strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' "
								+ "and strUserCreated='" + userCode + "'  and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strUserCreated='" + userCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else {
						sbSql.append(" where a.strTransType='AOVB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' ");
					}
					sbSql.append(" group by a.strBillNo");
					if (strSorting.equalsIgnoreCase("BILL")) {
						sbSql.append(" order by a.strBillNo");
						List listData = objBaseService.funGetList(sbSql, "sql");
						if (listData.size() > 0) {
							for (int i = 0; i < listData.size(); i++) {
								clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
								Object[] obj = (Object[]) listData.get(i);
								double amountTemp = Double.parseDouble(obj[5].toString());
								String billNo = obj[0].toString();
								sbSql.setLength(0);
								sbSql.append(
										"Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
								List list2 = objBaseService.funGetList(sbSql, "sql");
								int count = 0;
								if (list2.size() > 0) {
									count = (int) list2.get(0);
								}
								if (count > 0) {
									sbSql.setLength(0);
									sbSql.append(
											"select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='"
													+ billNo + "' ");
									list2 = objBaseService.funGetList(sbSql, "sql");
									Double temp = 0.0;
									if (list2.size() > 0) {
										temp = (Double) list2.get(0);
									}
									amountTemp = amountTemp + temp;
								}
								objBean.setStrBillNo(obj[0].toString());
								objBean.setDteBillDate(obj[1].toString());
								objBean.setDteVoidedDate(obj[2].toString());
								objBean.setStrEntryTime(obj[3].toString());
								objBean.setStrVoidedTime(obj[4].toString());
								objBean.setDblModifiedAmount(amountTemp);
								objBean.setStrUserEdited(obj[6].toString());
								objBean.setStrReasonName(obj[7].toString());
								arrListVoidAdvOrder.add(objBean);

								sumTotalAmt = sumTotalAmt + amountTemp;
							}
						}

					} else {
						List listData = objBaseService.funGetList(sbSql, "sql");
						if (listData.size() > 0) {
							for (int i = 0; i < listData.size(); i++) {
								Object[] obj = (Object[]) listData.get(i);
								clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
								double amountTemp = Double.parseDouble(obj[5].toString());
								String billNo = obj[0].toString();
								sbSql.setLength(0);
								sbSql.append(
										"Select count(*) from tblvoidmodifierdtl where strBillNo='" + billNo + "' ");
								List list2 = objBaseService.funGetList(sbSql, "sql");
								int count = 0;
								if (list2.size() > 0) {
									count = (int) list2.get(0);
								}
								if (count > 0) {
									sbSql.setLength(0);
									sbSql.append(
											"select ROUND(SUM(dblAmount))from tblvoidmodifierdtl where strBillNo ='"
													+ billNo + "' ");
									list2 = objBaseService.funGetList(sbSql, "sql");
									Double temp = 0.0;
									if (list2.size() > 0) {
										temp = (Double) list2.get(0);
									}
									amountTemp = amountTemp + temp;
								}

								objBean.setStrBillNo(obj[0].toString());
								objBean.setDteBillDate(obj[1].toString());
								objBean.setDteVoidedDate(obj[2].toString());
								objBean.setStrEntryTime(obj[3].toString());
								objBean.setStrVoidedTime(obj[4].toString());
								objBean.setDblModifiedAmount(amountTemp);
								objBean.setStrUserEdited(obj[6].toString());
								objBean.setStrReasonName(obj[7].toString());
								arrListVoidAdvOrder.add(objBean);
								sumTotalAmt = sumTotalAmt + amountTemp;
							}
						}
					}
					Comparator<clsPOSBillItemDtlBean> compareBillItem = new Comparator<clsPOSBillItemDtlBean>() {
						@Override
						public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2) {
							double dblAmount = o1.getDblModifiedAmount();
							double dblAmount2 = o2.getDblModifiedAmount();

							if (dblAmount == dblAmount2) {
								return 0;
							} else if (dblAmount > dblAmount2) {
								return 1;
							} else {
								return -1;
							}
						}
					};
					Collections.sort(arrListVoidAdvOrder, compareBillItem);
					for (clsPOSBillItemDtlBean obj : arrListVoidAdvOrder) {
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						objBean.setStrBillNo(obj.getStrBillNo());
						objBean.setDteBillDate(obj.getDteBillDate());
						objBean.setDteVoidedDate(obj.getDteVoidedDate());
						objBean.setStrEntryTime(obj.getStrEntryTime());
						objBean.setStrVoidedTime(obj.getStrVoidedTime());
						objBean.setDblModifiedAmount(obj.getDblModifiedAmount());
						objBean.setStrUserEdited(obj.getStrUserEdited());
						objBean.setStrReasonName(obj.getStrReasonName());
						listArr.add(objBean);

					}

				} else {

					StringBuilder sqlFilter = new StringBuilder();
					sbSql.setLength(0);
					sbSql.append(
							"select a.strBillNo,DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y') as VoidedDate,"
									+ "TIME_FORMAT(time(a.dteBillDate),'%h:%i') As EntryTime,TIME_FORMAT(time(a.dteModifyVoidBill),'%h:%i') VoidedTime,b.strItemName,"
									+ "b.intQuantity,b.dblAmount as BillAmount,a.strReasonName as Reason,a.strUserEdited as UserEdited,ifnull(a.strRemark,'') "
									+ " from tblvoidbillhd a ");
					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and "
								+ "strUserCreated='" + userCode + "' and strreasonCode='" + reasonCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and  "
								+ "strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strUserCreated='" + userCode + "' "
								+ "and strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' "
								+ "and strUserCreated='" + userCode + "'  and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" where a.strTransType='AOVB' and strUserCreated='" + userCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "' ");

					} else {
						sbSql.append(" where a.strTransType='AOVB' and Date(a.dteModifyVoidBill) between '" + fromDate
								+ "' and '" + toDate + "' ");
					}
					sbSql.append(" group by a.strBillNo,b.strItemCode");

					sbSqlMod.setLength(0);
					sbSqlMod.append(
							"select a.strBillNo, DATE_FORMAT(Date(a.dteBillDate) ,'%d-%m-%Y') as BillDate,DATE_FORMAT(Date(a.dteModifyVoidBill) ,'%d-%m-%Y')  as VoidedDate,"
									+ "Time(a.dteBillDate) As EntryTime,Time(a.dteModifyVoidBill) VoidedTime,b.strModifierName,"
									+ "b.dblQuantity,b.dblAmount ,a.strReasonName,a.strUserEdited "
									+ "from tblvoidbillhd a,tblvoidmodifierdtl b where a.strBillNo=b.strBillNo ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and b.strPosCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and a.strreasonCode='" + reasonCode
								+ "' and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' and "
								+ "Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strreasonCode='" + reasonCode + "' "
								+ "and Date(a.dteModifyVoidBill) between '" + fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' "
								+ "and a.strUserCreated='" + userCode + "' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strPosCode='" + strPOSCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill) "
								+ "between '" + fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSqlMod.append(" and a.strTransType='AOVB' and a.strUserCreated='" + userCode + "' "
								+ "and a.strreasonCode='" + reasonCode + "' and Date(a.dteModifyVoidBill)"
								+ " between '" + fromDate + "' and '" + toDate + "'");
					} else {
						sbSqlMod.append(" and a.strTransType='AOVB' and Date(a.dteModifyVoidBill) " + "between '"
								+ fromDate + "' and '" + toDate + "'");
					}
					sbSqlMod.append(" group by a.strBillNo,b.strModifierCode");
					StringBuilder sql = new StringBuilder();
					sql.append(
							"SELECT strBillNo, BillDate,  VoidedDate, EntryTime, VoidedTime,strItemName,intQuantity,BillAmount,Reason,"
									+ "UserEdited, strRemark" + "from( " + sbSql.toString() + " union "
									+ sbSqlMod.toString() + ")d");
					sqlFilter.setLength(0);
					if (strSorting.equalsIgnoreCase("BILL")) {

						sqlFilter.append(" order by a.strBillNo");

					} else {
						sqlFilter.append(" order by b.dblAmount");
					}
					sql.append(sqlFilter);
					List listData = objBaseService.funGetList(sql, "sql");
					if (listData.size() > 0) {
						for (int i = 0; i < listData.size(); i++) {
							Object[] obj = (Object[]) listData.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							objBean.setStrBillNo(obj[0].toString());
							objBean.setDteBillDate(obj[1].toString());
							objBean.setDteVoidedDate(obj[2].toString());
							objBean.setStrEntryTime(obj[3].toString());
							objBean.setStrVoidedTime(obj[4].toString());
							objBean.setStrItemName(obj[5].toString());
							objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
							objBean.setStrUserEdited(obj[8].toString());
							objBean.setStrReasonName(obj[9].toString());
							objBean.setStrRemark(obj[10].toString());
							listArr.add(objBean);

							sumQty = sumQty + Double.parseDouble(obj[6].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
						}
					}

				}
				break;

			case "Line Void":

				sbSql.setLength(0);
				sbSql.append(
						"select b.strPosName,DATE_FORMAT(Date(a.dteDateCreated),'%d-%m-%Y'),TIME_FORMAT(time(a.dteDateCreated),'%h:%i') "
								+ " ,a.strItemName,a.dblItemQuantity,a.dblAmount,a.strKOTNo,a.strUserCreated  "
								+ " from tbllinevoid a,tblposmaster b " + " where a.strPosCode=b.strPosCode ");

				if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strUserCreated='" + userCode + "' " + "and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and  a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and  Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
				}
				if ("Amount".equalsIgnoreCase(strSorting)) {
					sbSql.append(" order by a.dblAmount");
				}

				List listData = objBaseService.funGetList(sbSql, "sql");
				if (listData.size() > 0) {
					for (int i = 0; i < listData.size(); i++) {
						Object[] obj = (Object[]) listData.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						objBean.setStrPosName(obj[0].toString());
						objBean.setDteVoidedDate(obj[1].toString());
						objBean.setStrVoidedTime(obj[2].toString());
						objBean.setStrItemName(obj[3].toString());
						objBean.setDblQuantity(Double.parseDouble(obj[4].toString()));
						objBean.setBillAmt(Double.parseDouble(obj[5].toString()));
						objBean.setStrKOTNo(obj[6].toString());
						objBean.setStrUserEdited(obj[7].toString());
						listArr.add(objBean);

						sumQty = sumQty + Double.parseDouble(obj[4].toString());
						sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
					}
				}

				break;

			case "Voided KOT":
				sbSqlMod = new StringBuilder();

				sbSql.setLength(0);
				if (strReportType.equalsIgnoreCase("Summary")) {

					double pax = 0.00;

					resMap.put("ColHeader", listArrColHeader);

					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo,a.intPaxNo,"
							+ " sum(a.dblAmount),c.strReasonName,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
							+ " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
							+ ",tblreasonmaster c,tblposmaster d,tbltablemaster e "
							+ " where a.strreasonCode=c.strreasonCode "
							+ " and a.strPOSCode=d.strPOSCode and a.strTableNo=e.strTableNo ");
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append(" and a.strVoidBillType ='" + strType + "' ");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append(" and a.strVoidBillType ='Full KOT Void' ");
					} else {
						sbSql.append(" and (a.strVoidBillType = 'Full KOT Void' or a.strVoidBillType = 'ITEM VOID' ) ");
					}

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
					}
					sbSql.append(" Group By a.strPOSCode,a.strTableNo,b.strWShortName,a.strKOTNo,a.intPaxNo,"
							+ "c.strReasonName,a.strUserCreated");
					if ("Amount".equalsIgnoreCase(strSorting)) {
						sbSql.append(" order by sum(a.dblAmount)");
					}

					List list = objBaseService.funGetList(sbSql, "sql");
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							Object[] obj = (Object[]) list.get(i);
							objBean.setStrPosName(obj[0].toString());
							objBean.setStrTableName(obj[1].toString());
							objBean.setStrWaiterName(obj[2].toString());
							objBean.setStrKOTNo(obj[3].toString());
							objBean.setIntPaxNo(Integer.parseInt(obj[4].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[5].toString()));
							objBean.setStrReasonName(obj[6].toString());
							objBean.setStrUserCreated(obj[7].toString());
							objBean.setDteDateCreated(obj[8].toString());
							objBean.setStrRemark(obj[9].toString());
							listArr.add(objBean);

							pax = pax + Integer.parseInt(obj[4].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
						}
					}

				} else {

					double pax = 0.00;

					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo "
							+ " ,a.strItemName,a.intPaxNo,a.dblItemQuantity,a.dblAmount,c.strReasonName "
							+ " ,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
							+ " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
							+ " ,tblreasonmaster c,tblposmaster d,tbltablemaster e "
							+ " where a.strreasonCode=c.strreasonCode and a.strPOSCode=d.strPOSCode "
							+ " and a.strTableNo=e.strTableNo ");
					if ("Item Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='" + strType + "' ");
					} else if ("Full Void".equalsIgnoreCase(strType)) {
						sbSql.append("and a.strVoidBillType ='Full KOT Void' ");
					} else {
						sbSql.append("and (a.strVoidBillType = 'Full KOT Void' or a.strVoidBillType = 'ITEM VOID' ) ");
					}

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)
							&& !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
					}

					if ("Amount".equalsIgnoreCase(strSorting)) {
						sbSql.append(" order by a.dblAmount");
					}

					List listOfData = objBaseService.funGetList(sbSql, "sql");
					if (listOfData.size() > 0) {
						for (int i = 0; i < listOfData.size(); i++) {
							Object[] obj = (Object[]) listOfData.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							objBean.setStrPosName(obj[0].toString());
							objBean.setStrTableName(obj[1].toString());
							objBean.setStrWaiterName(obj[2].toString());
							objBean.setStrKOTNo(obj[3].toString());
							objBean.setStrItemName(obj[4].toString());
							objBean.setIntPaxNo(Integer.parseInt(obj[5].toString()));
							objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
							objBean.setStrReasonName(obj[8].toString());
							objBean.setStrUserCreated(obj[9].toString());
							objBean.setDteDateCreated(obj[10].toString());
							objBean.setStrRemark(obj[11].toString());
							listArr.add(objBean);

							pax = pax + Integer.parseInt(obj[5].toString());
							sumQty = sumQty + Double.parseDouble(obj[6].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
						}
					}

				}
				break;

			case "Time Audit":

				sbSql.setLength(0);
				sbSql.append("SELECT a.strbillno, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate "
						+ ",TIME_FORMAT(TIME(a.dteBillDate),'%h:%i') AS BillTime, TIME_FORMAT(TIME(b.dteBillDate),'%h:%i') AS KOTTime "
						+ ",TIME_FORMAT(TIME(a.dteSettleDate),'%h:%i')SettleTime, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
						+ ",DATE_FORMAT(DATE(a.dteSettleDate),'%d-%m-%Y')SettleDate,a.strUserCreated,a.strUserEdited, IFNULL(a.strRemarks,'') "
						+ ",concat(SEC_TO_TIME(TIMESTAMPDIFF(second,a.dteBillDate,a.dteSettleDate)),'') AS diffInBillnSettled  "
						+ "from tblbillhd a, tblbilldtl b where a.strBillNo=b.strBillNo ");

				if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode + "' and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");
				}
				sbSql.append(" group by a.strBillNo");

				List listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) {

					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						objBean.setStrBillNo(obj[0].toString());
						objBean.setDteBillDate(obj[1].toString());
						objBean.setStrKotTime(obj[3].toString());
						objBean.setStrEntryTime(obj[2].toString());
						objBean.setStrVoidedTime(obj[4].toString());
						objBean.setStrDifference(obj[10].toString());
						objBean.setStrUserCreated(obj[7].toString());
						objBean.setStrUserEdited(obj[8].toString());
						objBean.setStrRemark(obj[9].toString());

						listArr.add(objBean);

					}

				}

				sbSql.setLength(0);
				sbSql.append("SELECT a.strbillno, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate "
						+ ",TIME_FORMAT(TIME(a.dteBillDate),'%h:%i') AS BillTime, TIME_FORMAT(TIME(b.dteBillDate),'%h:%i') AS KOTTime "
						+ ",TIME_FORMAT(TIME(a.dteSettleDate),'%h:%i')SettleTime, DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
						+ ",DATE_FORMAT(DATE(a.dteSettleDate),'%d-%m-%Y')SettleDate,a.strUserCreated,a.strUserEdited, IFNULL(a.strRemarks,'') "
						+ ",concat(SEC_TO_TIME(TIMESTAMPDIFF(second,a.dteBillDate,a.dteSettleDate)),'') AS diffInBillnSettled  "
						+ "from tblqbillhd a, tblqbilldtl b where a.strBillNo=b.strBillNo ");

				if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode + "' and a.strPosCode='" + strPOSCode
							+ "' and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
				} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strPosCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName)) {
					sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
							+ fromDate + "' and '" + toDate + "'");
				} else {
					sbSql.append(" and Date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");
				}
				sbSql.append(" group by a.strBillNo");

				listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) {

					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
						objBean.setStrBillNo(obj[0].toString());
						objBean.setDteBillDate(obj[1].toString());
						objBean.setStrKotTime(obj[3].toString());
						objBean.setStrEntryTime(obj[2].toString());
						objBean.setStrVoidedTime(obj[4].toString());
						objBean.setStrDifference(obj[10].toString());
						objBean.setStrUserCreated(obj[7].toString());
						objBean.setStrUserEdited(obj[8].toString());
						objBean.setStrRemark(obj[9].toString());

						listArr.add(objBean);
					}

				}

				break;

			case "KOT Analysis":
				StringBuilder sbSqlLive = new StringBuilder();
				StringBuilder sbSqlQFile = new StringBuilder();
				StringBuilder sbFilters = new StringBuilder();
				int noOfKOTs = 0;
				List<clsPOSKOTAnalysisBean> listOfKOTAnalysis = new LinkedList<clsPOSKOTAnalysisBean>();
				String operation = "Billed KOT";

				if (strReportType.equalsIgnoreCase("Summary")) {

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}

					// live billed KOTs
					sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
							+ "from tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and b.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>0 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "' ");

					sbSqlLive.append(sbFilters);

					sbSqlLive.append("group by a.strBillNo,b.strKOTNo " + "order by a.strBillNo,b.strKOTNo");

					List listBilledKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listBilledKOTs.size() > 0) {
						for (int i = 0; i < listBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[1].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[3].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[4].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[7].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// Q billed KOTs
					sbSqlQFile.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
							+ "from tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and b.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>0 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "'  ");
					sbSqlQFile.append(sbFilters);
					sbSqlQFile.append("group by a.strBillNo,b.strKOTNo " + "order by a.strBillNo,b.strKOTNo");

					listBilledKOTs = objBaseService.funGetList(sbSqlQFile, "sql");
					if (listBilledKOTs.size() > 0) {
						for (int i = 0; i < listBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[1].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[3].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[4].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[7].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// voided billed KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}
					sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo,b.strTransType "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
							+ ",b.strReasonName,b.strRemarks "
							+ "from tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and a.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>2 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strBillNo,b.strKOTNo " + "order by a.strBillNo,b.strKOTNo");
					List listVoidedBilledKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listVoidedBilledKOTs.size() > 0) {
						for (int i = 0; i < listVoidedBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listVoidedBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							operation = obj[1].toString();
							if (obj[1].toString().equalsIgnoreCase("VB")) {
								operation = "Void Bill";
							}
							if (obj[1].toString().equalsIgnoreCase("USBill")) {
								operation = "Unseetled Bill";
							}
							if (obj[1].toString().equalsIgnoreCase("MB")) {
								operation = "Modified Bill";
							}

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[4].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[5].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[6].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[7].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[8].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[9].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[10].toString());// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// line voided KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "Line Void";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode= '" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}

					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Line Void' strOperationType "
									+ ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
									+ "from tbllinevoid a " + "where LENGTH(a.strKOTNo)>2 "
									+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo " + "order by a.strKOTNo");
					List listLineVoidedKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listLineVoidedKOTs.size() > 0) {
						for (int i = 0; i < listLineVoidedKOTs.size(); i++) {
							Object[] obj = (Object[]) listLineVoidedKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName("");// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName("");// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// voided KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "Void KOT";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}
					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,a.strType strOperationType "
									+ ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
									+ ",b.strTableName,c.strWShortName,d.strReasonName,a.strRemark "
									+ "from tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d "
									+ "where a.strTableNo=b.strTableNo  " + "and a.strWaiterNo=c.strWaiterNo "
									+ "and a.strReasonCode=d.strReasonCode " + "and LENGTH(a.strKOTNo)>2 "
									+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo,a.strType " + "order by a.strKOTNo");
					List listVoidedKOT = objBaseService.funGetList(sbSqlLive, "sql");
					if (listVoidedKOT.size() > 0) {
						for (int i = 0; i < listVoidedKOT.size(); i++) {
							Object[] obj = (Object[]) listVoidedKOT.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							if (obj[1].toString().equalsIgnoreCase("VKot")) {
								operation = "Void KOT";
							} else if (obj[1].toString().equalsIgnoreCase("MVKot")) {
								operation = "Move KOT";
							} else {
								operation = "Void KOT";
							}
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName(obj[4].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[5].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[6].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[7].toString());// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// NC KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "NC KOT";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}

					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'NC KOT' strOperationType "
									+ ",DATE_FORMAT(date(a.dteNCKOTDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteNCKOTDate),'%h:%i')tmeKOTTime "
									+ ",a.strTableNo,b.strTableName,c.strReasonCode,c.strReasonName,a.strRemark "
									+ "from tblnonchargablekot a,tbltablemaster b,tblreasonmaster c "
									+ "where LENGTH(a.strKOTNo)>2 " + "and a.strTableNo=b.strTableNo "
									+ "and a.strReasonCode=c.strReasonCode " + "and Date(a.dteNCKOTDate) between '"
									+ fromDate + "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo " + "order by a.strKOTNo");
					List listNCKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listNCKOTs.size() > 0) {
						for (int i = 0; i < listNCKOTs.size(); i++) {
							Object[] obj = (Object[]) listNCKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName("");// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[7].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[8].toString());// remarks

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// sorting
					Comparator<clsPOSKOTAnalysisBean> kotComaparator = new Comparator<clsPOSKOTAnalysisBean>() {

						@Override
						public int compare(clsPOSKOTAnalysisBean o1, clsPOSKOTAnalysisBean o2) {
							return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
						}
					};
					Collections.sort(listOfKOTAnalysis, kotComaparator);
					// sorting//

					// fill table data
					for (clsPOSKOTAnalysisBean objKOTAnalysisBean : listOfKOTAnalysis) {
						clsPOSKOTAnalysisBean objBean = new clsPOSKOTAnalysisBean();
						objBean.setStrKOTNo(objKOTAnalysisBean.getStrKOTNo());
						objBean.setStrOperationType(objKOTAnalysisBean.getStrOperationType());
						objBean.setDteKOTDate(objKOTAnalysisBean.getDteKOTDate());
						objBean.setTmeKOTTime(objKOTAnalysisBean.getTmeKOTTime());
						objBean.setStrBillNo(objKOTAnalysisBean.getStrBillNo());
						objBean.setStrTableName(objKOTAnalysisBean.getStrTableName());
						objBean.setStrWaiterName(objKOTAnalysisBean.getStrWaiterName());
						objBean.setStrReasonName(objKOTAnalysisBean.getStrReasonName());
						objBean.setStrRemarks(objKOTAnalysisBean.getStrRemarks());
						listArr.add(objBean);

						noOfKOTs++;
					}

				} else {

					listOfKOTAnalysis = new LinkedList<clsPOSKOTAnalysisBean>();
					double totalQuantity = 0.0;

					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "Billed KOT";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}

					// live billed KOTs
					sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName"
							+ ",b.strItemCode,b.strItemName,sum(b.dblQuantity) "
							+ "from tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and b.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>0 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "' ");

					sbSqlLive.append(sbFilters);

					sbSqlLive.append(
							"group by a.strBillNo,b.strKOTNo,b.strItemCode " + "order by a.strBillNo,b.strKOTNo");

					List listBilledKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listBilledKOTs.size() > 0) {
						for (int i = 0; i < listBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[1].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[3].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[4].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[7].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks
							objKOTAnalysisBean.setStrItemCode(obj[8].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[9].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// Q billed KOTs
					sbSqlQFile.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName"
							+ ",b.strItemCode,b.strItemName,sum(b.dblQuantity) "
							+ "from tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and b.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>0 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "'  ");
					sbSqlQFile.append(sbFilters);
					sbSqlQFile.append(
							"group by a.strBillNo,b.strKOTNo,b.strItemCode " + "order by a.strBillNo,b.strKOTNo");

					listBilledKOTs = objBaseService.funGetList(sbSqlQFile, "sql");
					if (listBilledKOTs.size() > 0) {
						for (int i = 0; i < listBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[1].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[2].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[3].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[4].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[6].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[7].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks
							objKOTAnalysisBean.setStrItemCode(obj[8].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[9].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// voided billed KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}
					sbSqlLive.append("select if(b.strKOTNo='','DirectBiller',b.strKOTNo)strKOTNo,b.strTransType "
							+ ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(b.dteBillDate),'%h:%i')tmeKOTTime "
							+ ",a.strBillNo,a.strTableNo,c.strTableName,b.strWaiterNo,if(d.strWShortName='','ShortName',d.strWShortName)strWShortName "
							+ ",b.strReasonName,b.strRemarks" + ",b.strItemCode,b.strItemName,sum(b.intQuantity) "
							+ "from tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d "
							+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and a.strTableNo=c.strTableNo " + "and a.strWaiterNo=d.strWaiterNo "
							+ "and LENGTH(b.strKOTNo)>2 " + "and Date(a.dteBillDate) between '" + fromDate + "' and '"
							+ toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append(
							"group by a.strBillNo,b.strKOTNo,b.strItemCode " + "order by a.strBillNo,b.strKOTNo");
					List listVoidedBilledKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listVoidedBilledKOTs.size() > 0) {
						for (int i = 0; i < listVoidedBilledKOTs.size(); i++) {
							Object[] obj = (Object[]) listVoidedBilledKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							operation = obj[1].toString();
							if (obj[1].toString().equalsIgnoreCase("VB")) {
								operation = "Void Bill";
							}
							if (obj[1].toString().equalsIgnoreCase("USBill")) {
								operation = "Unseetled Bill";
							}
							if (obj[1].toString().equalsIgnoreCase("MB")) {
								operation = "Modified Bill";
							}

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo(obj[4].toString());// billNo
							objKOTAnalysisBean.setStrTableNo(obj[5].toString());// tableNO
							objKOTAnalysisBean.setStrTableName(obj[6].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo(obj[7].toString());// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[8].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[9].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[10].toString());// remarks
							objKOTAnalysisBean.setStrItemCode(obj[11].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[12].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[13].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// line voided KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "Line Void";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}

					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Line Void' strOperationType "
									+ ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime"
									+ ",a.strItemCode,a.strItemName,sum(a.dblItemQuantity) " + "from tbllinevoid a "
									+ "where LENGTH(a.strKOTNo)>2 " + "and Date(a.dteDateCreated) between '" + fromDate
									+ "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo,a.strItemCode " + "order by a.strKOTNo");
					List listLineVoidedKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listLineVoidedKOTs.size() > 0) {
						for (int i = 0; i < listLineVoidedKOTs.size(); i++) {
							Object[] obj = (Object[]) listLineVoidedKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName("");// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName("");// waiterName
							objKOTAnalysisBean.setStrReasonName("");// reason
							objKOTAnalysisBean.setStrRemarks("");// remarks
							objKOTAnalysisBean.setStrItemCode(obj[4].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[5].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[6].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// voided KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "Void KOT";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}
					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'Void KOT' strOperationType "
									+ ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteDateCreated),'%h:%i')tmeKOTTime "
									+ ",b.strTableName,c.strWShortName,d.strReasonName,a.strRemark"
									+ ",a.strItemCode,a.strItemName,sum(a.dblItemQuantity) "
									+ "from tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d "
									+ "where a.strTableNo=b.strTableNo  " + "and a.strWaiterNo=c.strWaiterNo "
									+ "and a.strReasonCode=d.strReasonCode " + "and LENGTH(a.strKOTNo)>2 "
									+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo,a.strItemCode " + "order by a.strKOTNo");
					List listVoidedKOT = objBaseService.funGetList(sbSqlLive, "sql");
					if (listVoidedKOT.size() > 0) {
						for (int i = 0; i < listVoidedKOT.size(); i++) {
							Object[] obj = (Object[]) listVoidedKOT.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName(obj[4].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName(obj[5].toString());// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[6].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[7].toString());// remarks
							objKOTAnalysisBean.setStrItemCode(obj[8].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[9].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[10].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// NC KOTs
					sbSqlLive.setLength(0);
					sbSqlQFile.setLength(0);
					sbFilters.setLength(0);
					operation = "NC KOT";

					if (!"All".equalsIgnoreCase(posName)) {
						sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
					}
					if (!"All".equalsIgnoreCase(userName)) {
						sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
					}
					if (!reasonName.equalsIgnoreCase("All")) {
						sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
					}

					sbSqlLive.append(
							"select if(a.strKOTNo='','DirectBiller',a.strKOTNo)strKOTNo,'NC KOT' strOperationType "
									+ ",DATE_FORMAT(date(a.dteNCKOTDate),'%d-%m-%Y') dteKOTDate,TIME_FORMAT(time(a.dteNCKOTDate),'%h:%i')tmeKOTTime "
									+ ",a.strTableNo,b.strTableName,c.strReasonCode,c.strReasonName,a.strRemark"
									+ ",a.strItemCode,d.strItemName,sum(a.dblQuantity) "
									+ "from tblnonchargablekot a,tbltablemaster b,tblreasonmaster c,tblitemmaster d "
									+ "where LENGTH(a.strKOTNo)>2 " + "and a.strTableNo=b.strTableNo "
									+ "and a.strReasonCode=c.strReasonCode " + "and a.strItemCode=d.strItemCode "
									+ "and Date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' ");
					sbSqlLive.append(sbFilters);
					sbSqlLive.append("group by a.strKOTNo,a.strItemCode " + "order by a.strKOTNo");
					List listNCKOTs = objBaseService.funGetList(sbSqlLive, "sql");
					if (listNCKOTs.size() > 0) {
						for (int i = 0; i < listNCKOTs.size(); i++) {
							Object[] obj = (Object[]) listNCKOTs.get(i);
							clsPOSKOTAnalysisBean objKOTAnalysisBean = new clsPOSKOTAnalysisBean();

							objKOTAnalysisBean.setStrKOTNo(obj[0].toString());// kotNO
							objKOTAnalysisBean.setStrOperationType(operation);// operation
							objKOTAnalysisBean.setDteKOTDate(obj[2].toString());// date
							objKOTAnalysisBean.setTmeKOTTime(obj[3].toString());// time
							objKOTAnalysisBean.setStrBillNo("");// billNo
							objKOTAnalysisBean.setStrTableNo("");// tableNO
							objKOTAnalysisBean.setStrTableName(obj[5].toString());// tableName
							objKOTAnalysisBean.setStrWaiterNo("");// waiterNo
							objKOTAnalysisBean.setStrWaiterName("");// waiterName
							objKOTAnalysisBean.setStrReasonName(obj[7].toString());// reason
							objKOTAnalysisBean.setStrRemarks(obj[8].toString());// remarks
							objKOTAnalysisBean.setStrItemCode(obj[9].toString());// itemCode
							objKOTAnalysisBean.setStrItemName(obj[10].toString());// itemName
							objKOTAnalysisBean.setDblQty(Double.parseDouble(obj[11].toString()));// itemQty

							listOfKOTAnalysis.add(objKOTAnalysisBean);
						}
					}

					// sorting
					Comparator<clsPOSKOTAnalysisBean> kotComaparator = new Comparator<clsPOSKOTAnalysisBean>() {

						@Override
						public int compare(clsPOSKOTAnalysisBean o1, clsPOSKOTAnalysisBean o2) {
							return o1.getStrKOTNo().compareToIgnoreCase(o2.getStrKOTNo());
						}
					};
					Collections.sort(listOfKOTAnalysis, kotComaparator);
					// sorting//

					// fill table data
					for (clsPOSKOTAnalysisBean objKOTAnalysisBean : listOfKOTAnalysis) {
						clsPOSKOTAnalysisBean objBean = new clsPOSKOTAnalysisBean();
						objBean.setStrKOTNo(objKOTAnalysisBean.getStrKOTNo());
						objBean.setStrOperationType(objKOTAnalysisBean.getStrOperationType());
						objBean.setDteKOTDate(objKOTAnalysisBean.getDteKOTDate());
						objBean.setTmeKOTTime(objKOTAnalysisBean.getTmeKOTTime());
						objBean.setStrBillNo(objKOTAnalysisBean.getStrBillNo());
						objBean.setStrItemName(objKOTAnalysisBean.getStrItemName());
						objBean.setDblQty(objKOTAnalysisBean.getDblQty());
						objBean.setStrTableName(objKOTAnalysisBean.getStrTableName());
						objBean.setStrWaiterName(objKOTAnalysisBean.getStrWaiterName());
						objBean.setStrReasonName(objKOTAnalysisBean.getStrReasonName());
						objBean.setStrRemarks(objKOTAnalysisBean.getStrRemarks());
						listArr.add(objBean);

						totalQuantity += objKOTAnalysisBean.getDblQty();
					}

				}

				break;

			case "Moved KOT":
				int pax = 0;
				if (strReportType.equalsIgnoreCase("Summary")) {

					sumQty = 0.00;
					sumTotalAmt = 0.00;
					pax = 0;

					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo,a.intPaxNo,"
							+ " sum(a.dblAmount),c.strReasonName,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y') "
							+ " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
							+ ",tblreasonmaster c,tblposmaster d,tbltablemaster e "
							+ " where a.strreasonCode=c.strreasonCode "
							+ " and a.strPOSCode=d.strPOSCode and a.strTableNo=e.strTableNo "
							+ " and a.strType='MVKot' ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
					}
					sbSql.append(" Group By a.strPOSCode,a.strTableNo,b.strWShortName,a.strKOTNo,a.intPaxNo,"
							+ "c.strReasonName,a.strUserCreated");
					// System.out.println(sbSql.toString());
					List list = objBaseService.funGetList(sbSql, "sql");
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							Object[] obj = (Object[]) list.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							objBean.setStrPosName(obj[0].toString());
							objBean.setStrTableName(obj[1].toString());
							objBean.setStrWaiterName(obj[2].toString());
							objBean.setStrKOTNo(obj[3].toString());
							objBean.setIntPaxNo(Integer.parseInt(obj[4].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[5].toString()));
							objBean.setStrReasonName(obj[6].toString());
							objBean.setStrUserCreated(obj[7].toString());
							objBean.setDteDateCreated(obj[8].toString());
							listArr.add(objBean);
							pax = pax + Integer.parseInt(obj[4].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[5].toString());
						}
					}
					resMap.put("listArr", listArr);
				} else {
					listArrColHeader.add("POS");
					listArrColHeader.add("Table");
					listArrColHeader.add("Waiter");
					listArrColHeader.add("KOT No");
					listArrColHeader.add("Item Name");
					listArrColHeader.add("Pax");
					listArrColHeader.add("Qty");
					listArrColHeader.add("Amount");
					listArrColHeader.add("Reason");
					listArrColHeader.add("User Created");
					listArrColHeader.add("Date Created");
					listArrColHeader.add("Remarks");
					resMap.put("ColHeader", listArrColHeader);

					sbSql.setLength(0);
					sbSql.append("select d.strPOSName,e.strTableName,b.strWShortName,a.strKOTNo "
							+ " ,a.strItemName,a.intPaxNo,a.dblItemQuantity,a.dblAmount,c.strReasonName "
							+ " ,a.strUserCreated,DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y'),ifnull(a.strRemark,'') "
							+ " from tblvoidkot a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo "
							+ " ,tblreasonmaster c,tblposmaster d,tbltablemaster e "
							+ " where a.strreasonCode=c.strreasonCode and a.strPOSCode=d.strPOSCode "
							+ " and a.strTableNo=e.strTableNo and a.strType='MVKot' ");

					if (!"All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and a.strUserCreated='" + userCode + "' "
								+ "and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "' "
								+ "and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if (!"All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && "All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strPOSCode='" + strPOSCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else if ("All".equalsIgnoreCase(posName) && !"All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strUserCreated='" + userCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "' and a.strreasonCode='" + reasonCode + "'");
					} else if ("All".equalsIgnoreCase(posName) && "All".equalsIgnoreCase(userName) && !"All".equalsIgnoreCase(reasonName)) {
						sbSql.append(" and a.strreasonCode='" + reasonCode + "' and Date(a.dteDateCreated) between '"
								+ fromDate + "' and '" + toDate + "'");
					} else {
						sbSql.append(" and Date(a.dteDateCreated) between '" + fromDate + "' and '" + toDate + "'");
					}

					List list = objBaseService.funGetList(sbSql, "sql");
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							Object[] obj = (Object[]) list.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
							objBean.setStrPosName(obj[0].toString());
							objBean.setStrTableName(obj[1].toString());
							objBean.setStrWaiterName(obj[2].toString());
							objBean.setStrKOTNo(obj[3].toString());
							objBean.setStrItemName(obj[4].toString());
							objBean.setIntPaxNo(Integer.parseInt(obj[5].toString()));
							objBean.setDblQuantity(Double.parseDouble(obj[6].toString()));
							objBean.setDblAmount(Double.parseDouble(obj[7].toString()));
							objBean.setStrReasonName(obj[8].toString());
							objBean.setStrUserCreated(obj[9].toString());
							objBean.setDteDateCreated(obj[10].toString());
							objBean.setStrRemark(obj[11].toString());
							listArr.add(objBean);

							pax = pax + Integer.parseInt(obj[5].toString());
							sumQty = sumQty + Double.parseDouble(obj[6].toString());
							sumTotalAmt = sumTotalAmt + Double.parseDouble(obj[7].toString());
						}
					}

				}

				break;

			case "Waiter Audit":

				Map<String, clsPOSWaiterAnalysisBean> mapWaiterWise = new HashMap();
				Map<String, String> mapKotsSave = new HashMap();
				List<clsPOSWaiterAnalysisBean> listOfWaiterAnalysis = new LinkedList<clsPOSWaiterAnalysisBean>();
				sbSqlLive = new StringBuilder();
				sbSqlQFile = new StringBuilder();
				sbFilters = new StringBuilder();

				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				operation = "Billed KOT";

				if (!"All".equalsIgnoreCase(posName)) {
					sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!"All".equalsIgnoreCase(userName)) {
					sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
				}
				if (!reasonCode.equalsIgnoreCase("All")) {
					sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
				}

				// live billed KOTs
				sbSqlLive.append("SELECT d.strWaiterNo,d.strWShortName,d.strWFullName,b.strKOTNo\n"
						+ "FROM tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d\n"
						+ "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) AND a.strTableNo=c.strTableNo "
						+ "AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 AND DATE(a.dteBillDate) BETWEEN '"
						+ fromDate + "' and '" + toDate + "'\n ");

				sbSqlLive.append(sbFilters);

				sbSqlLive.append("GROUP BY b.strWaiterNo,b.strKOTNo\n" + "order by d.strWShortName,b.strKOTNo");

				List listBilledKOTs = objBaseService.funGetList(sbSqlLive, "sql");
				if (listBilledKOTs.size() > 0) {
					for (int i = 0; i < listBilledKOTs.size(); i++) {
						Object[] obj = (Object[]) listBilledKOTs.get(i);
						String waiterNo = obj[0].toString();
						String kotNo = obj[3].toString();

						if (mapWaiterWise.containsKey(waiterNo)) {
							clsPOSWaiterAnalysisBean objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
							if (!mapKotsSave.containsKey(kotNo)) {
								objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
							}
						} else {
							clsPOSWaiterAnalysisBean objWaiterAnalysisBean = new clsPOSWaiterAnalysisBean();

							objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
							objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
							objWaiterAnalysisBean.setNoOfKot(1);
							objWaiterAnalysisBean.setNoOfVoidKot(0);
							objWaiterAnalysisBean.setNoOfMoveKot(0);
							listOfWaiterAnalysis.add(objWaiterAnalysisBean);
							mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
							mapKotsSave.put(kotNo, kotNo);
						}
					}
				}

				// Q billed KOTs
				sbSqlQFile.append("SELECT d.strWaiterNo,d.strWShortName,d.strWFullName,b.strKOTNo\n"
						+ "FROM tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d\n"
						+ "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) AND a.strTableNo=c.strTableNo "
						+ "AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 AND DATE(a.dteBillDate) BETWEEN '"
						+ fromDate + "' and '" + toDate + "'\n ");

				sbSqlQFile.append(sbFilters);
				sbSqlQFile.append("GROUP BY b.strWaiterNo,b.strKOTNo\n" + "order by d.strWShortName,b.strKOTNo");

				listBilledKOTs = objBaseService.funGetList(sbSqlQFile, "sql");
				if (listBilledKOTs.size() > 0) {
					for (int i = 0; i < listBilledKOTs.size(); i++) {
						Object[] obj = (Object[]) listBilledKOTs.get(i);
						String waiterNo = obj[0].toString();
						String kotNo = obj[3].toString();

						if (mapWaiterWise.containsKey(waiterNo)) {
							clsPOSWaiterAnalysisBean objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
							if (!mapKotsSave.containsKey(kotNo)) {
								objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
							}
						} else {
							clsPOSWaiterAnalysisBean objWaiterAnalysisBean = new clsPOSWaiterAnalysisBean();

							objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
							objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
							objWaiterAnalysisBean.setNoOfKot(1);
							objWaiterAnalysisBean.setNoOfVoidKot(0);
							objWaiterAnalysisBean.setNoOfMoveKot(0);
							listOfWaiterAnalysis.add(objWaiterAnalysisBean);
							mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
							mapKotsSave.put(kotNo, kotNo);
						}
					}
				}

				// voided KOTs
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				operation = "Void KOT";

				if (!"All".equalsIgnoreCase(posName)) {
					sbFilters.append("and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!"All".equalsIgnoreCase(userName)) {
					sbFilters.append(" and a.strUserCreated='" + userCode + "' ");
				}
				if (!reasonName.equalsIgnoreCase("All")) {
					sbFilters.append(" and a.strReasonCode='" + reasonCode + "' ");
				}

				sbSqlLive.append(
						"SELECT c.strWaiterNo,c.strWShortName,c.strWFullName,a.strType strOperationType,a.strKOTNo\n"
								+ "FROM tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d\n"
								+ "WHERE a.strTableNo=b.strTableNo AND a.strWaiterNo=c.strWaiterNo AND a.strReasonCode=d.strReasonCode \n"
								+ "AND LENGTH(a.strKOTNo)>2 AND DATE(a.dteDateCreated) BETWEEN '" + fromDate + "' and '"
								+ toDate + "' ");

				sbSqlLive.append(sbFilters);
				sbSqlLive.append("group by a.strWaiterNo,a.strKOTNo " + "order by c.strWFullName,a.strKOTNo");
				List listVoidedKOT = objBaseService.funGetList(sbSqlLive, "sql");
				int noOfKot = 0;
				double noOfVoidKotPer = 0.0, noOfMoveKotPer = 0.0;
				if (listVoidedKOT.size() > 0) {
					for (int i = 0; i < listVoidedKOT.size(); i++) {
						Object[] obj = (Object[]) listVoidedKOT.get(i);
						clsPOSWaiterAnalysisBean objWaiterAnalysisBean = new clsPOSWaiterAnalysisBean();

						if (obj[3].toString().equalsIgnoreCase("VKot")) {
							operation = "Void KOT";
						} else if (obj[3].toString().equalsIgnoreCase("MVKot")) {
							operation = "Move KOT";
						} else {
							operation = "Void KOT";
						}

						String waiterNo = obj[0].toString();
						String kotNo = obj[4].toString();

						if (mapWaiterWise.containsKey(waiterNo)) {
							objWaiterAnalysisBean = mapWaiterWise.get(waiterNo);
							if (operation.equalsIgnoreCase("Void KOT")) {
								objWaiterAnalysisBean.setNoOfVoidKot(objWaiterAnalysisBean.getNoOfVoidKot() + 1);
							} else {
								objWaiterAnalysisBean.setNoOfMoveKot(objWaiterAnalysisBean.getNoOfMoveKot() + 1);
							}
							if (!mapKotsSave.containsKey(kotNo)) {
								objWaiterAnalysisBean.setNoOfKot(objWaiterAnalysisBean.getNoOfKot() + 1);
							}
						} else {
							objWaiterAnalysisBean.setStrWaiterNo(obj[0].toString());
							objWaiterAnalysisBean.setStrWaiterName(obj[2].toString());
							objWaiterAnalysisBean.setNoOfKot(1);
							if (operation.equalsIgnoreCase("Void KOT")) {
								objWaiterAnalysisBean.setNoOfVoidKot(1);
							} else {
								objWaiterAnalysisBean.setNoOfMoveKot(1);
							}
							listOfWaiterAnalysis.add(objWaiterAnalysisBean);
							mapWaiterWise.put(waiterNo, objWaiterAnalysisBean);
							mapKotsSave.put(kotNo, kotNo);
						}
					}
				}

				// sorting
				Comparator<clsPOSWaiterAnalysisBean> kotComaparator = new Comparator<clsPOSWaiterAnalysisBean>() {

					@Override
					public int compare(clsPOSWaiterAnalysisBean o1, clsPOSWaiterAnalysisBean o2) {
						return o1.getStrWaiterName().compareToIgnoreCase(o2.getStrWaiterName());
					}
				};
				Collections.sort(listOfWaiterAnalysis, kotComaparator);
				// sorting//

				// fill table data
				DecimalFormat df1 = new DecimalFormat("0");
				double totNoOfKot = 0.0, totNoOfVoidKot = 0.0, totNoOfMoveKot = 0.0;
				for (Map.Entry<String, clsPOSWaiterAnalysisBean> entrySet : mapWaiterWise.entrySet()) {
					clsPOSWaiterAnalysisBean objWaiterAnalysisBean = entrySet.getValue();
					clsPOSWaiterAnalysisBean objBean = new clsPOSWaiterAnalysisBean();
					noOfKot = objWaiterAnalysisBean.getNoOfKot();
					totNoOfKot = totNoOfKot + objWaiterAnalysisBean.getNoOfKot();
					noOfVoidKotPer = objWaiterAnalysisBean.getNoOfVoidKot();
					totNoOfVoidKot = totNoOfVoidKot + objWaiterAnalysisBean.getNoOfVoidKot();
					noOfMoveKotPer = objWaiterAnalysisBean.getNoOfMoveKot();
					totNoOfMoveKot = totNoOfMoveKot + objWaiterAnalysisBean.getNoOfMoveKot();
					if (noOfKot > 0) {
						noOfVoidKotPer = ((noOfVoidKotPer / noOfKot) * 100);
						noOfMoveKotPer = ((noOfMoveKotPer / noOfKot) * 100);
					}
					objBean.setStrWaiterName(objWaiterAnalysisBean.getStrWaiterName());
					objBean.setNoOfKot(noOfKot);
					objBean.setNoOfVoidKot(objWaiterAnalysisBean.getNoOfVoidKot());
					objBean.setNoOfVoidKotPer(noOfVoidKotPer);
					objBean.setNoOfMoveKot(objWaiterAnalysisBean.getNoOfMoveKot());
					objBean.setNoOfMoveKotPer(noOfMoveKotPer);
					listArr.add(objBean);

				}

				break;

			}// end of switch
		} // end of try
		catch (Exception e) {
			e.printStackTrace();
		}
		return listArr;
	}

	public List funProcessCoseCenterWiseSummaryReport(String posCode, String fromDate, String toDate,
			String strReportType, String strShiftNo, String costCenterCode, String userCode) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlModLive = new StringBuilder();
		StringBuilder sbSqlModQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		List<clsPOSCostCenterBean> listOfCostCenterDtl = new LinkedList<>();
		String gAreaWisePricing = "";

		try {
			StringBuilder sqlAreaWisePrice = new StringBuilder();
			sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
			List listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
			if (listAraWiePrice.size() > 0) {
				gAreaWisePricing = (String) listAraWiePrice.get(0);
			}
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);

			// Live Sql
			sbSqlLive.append(
					"SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
							+ ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscountAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscountAmt)dblSalesAmount "
							+ " from tblbilldtl c,tblbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
							+ " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.strBillNo = d.strBillNo and d.strPOSCode = e.strPOSCode and b.strItemCode = c.strItemCode "
							+ " and (b.strposcode =d.strposcode  or b.strPosCode='All') and a.strCostCenterCode = b.strCostCenterCode "
							+ " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlLive.append(" and d.strAreaCode=b.strAreaCode ");
			}

			// QFile Sql
			sbSqlQFile.append(
					"SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
							+ ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscountAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscountAmt)dblSalesAmount "
							+ " from tblqbilldtl c,tblqbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
							+ " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.strBillNo = d.strBillNo " + " and d.strPOSCode = e.strPOSCode "
							+ " and b.strItemCode = c.strItemCode "
							+ " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
							+ " and a.strCostCenterCode = b.strCostCenterCode "
							+ " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlQFile.append(" and d.strAreaCode=b.strAreaCode ");
			}

			sbSqlModLive.append(
					"SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
							+ ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscAmt)dblSalesAmount  "
							+ " from tblbillmodifierdtl c,tblbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
							+ " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.strBillNo = d.strBillNo " + " and d.strPOSCode = e.strPOSCode "
							+ " and b.strItemCode = left(c.strItemCode,7) "
							+ " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
							+ " and a.strCostCenterCode = b.strCostCenterCode " + " and c.dblAmount>0 "
							+ " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlModLive.append(" and d.strAreaCode=b.strAreaCode ");
			}

			sbSqlModQFile.append(
					"SELECT e.strPosCode,e.strPOSName,ifnull(a.strCostCenterCode,'ND')strCostCenterCode,ifnull(a.strCostCenterName,'ND')strCostCenterName "
							+ ",sum(c.dblAmount)dblSubTotal,sum(c.dblDiscAmt) dblDiscountAmt,sum( c.dblAmount )-sum(c.dblDiscAmt)dblSalesAmount  "
							+ " from tblqbillmodifierdtl c,tblqbillhd d,tblposmaster e ,tblmenuitempricingdtl b,tblcostcentermaster a "
							+ " where date( d.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.strBillNo = d.strBillNo " + " and d.strPOSCode = e.strPOSCode "
							+ " and b.strItemCode = left(c.strItemCode,7) "
							+ " and (b.strposcode =d.strposcode  or b.strPosCode='All') "
							+ " and a.strCostCenterCode = b.strCostCenterCode " + " and c.dblAmount>0 "
							+ " and c.strClientCode=d.strClientCode and b.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlModQFile.append(" and d.strAreaCode=b.strAreaCode ");
			}

			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND d.strPOSCode = '" + posCode + "' ");
			}
			// sbSqlFilters.append(" and b.intShiftCode ='" + strShiftNo + "' ");

			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" and a.strCostCenterCode='" + costCenterCode + "' ");
			}
			sbSqlFilters.append("GROUP BY e.strPOSName,b.strCostCenterCode,a.strCostCenterName "
					+ "order BY e.strPOSName,b.strCostCenterCode,a.strCostCenterName ");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sbSqlModLive.append(sbSqlFilters.toString());
			sbSqlModQFile.append(sbSqlFilters.toString());

			// live data
			List listCostCenterData = objBaseService.funGetList(sbSqlLive, "sql");
			if (listCostCenterData.size() > 0) {
				for (int i = 0; i < listCostCenterData.size(); i++) {
					Object[] obj = (Object[]) listCostCenterData.get(i);
					clsPOSCostCenterBean objCostCenterBean = new clsPOSCostCenterBean();

					objCostCenterBean.setStrPOSCode(obj[0].toString());// posCode
					objCostCenterBean.setStrPOSName(obj[1].toString());// posName
					objCostCenterBean.setStrCostCenterCode(obj[2].toString());// costCenterCode
					objCostCenterBean.setStrCostCenterName(obj[3].toString());// costCenterName
					objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));// subTotal
					objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));// discount
					objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));// salesAmount

					listOfCostCenterDtl.add(objCostCenterBean);
				}
			}

			// live modifier data
			listCostCenterData = objBaseService.funGetList(sbSqlModLive, "sql");
			if (listCostCenterData.size() > 0) {
				for (int i = 0; i < listCostCenterData.size(); i++) {
					Object[] obj = (Object[]) listCostCenterData.get(i);
					clsPOSCostCenterBean objCostCenterBean = new clsPOSCostCenterBean();

					objCostCenterBean.setStrPOSCode(obj[0].toString());// posCode
					objCostCenterBean.setStrPOSName(obj[1].toString());// posName
					objCostCenterBean.setStrCostCenterCode(obj[2].toString());// costCenterCode
					objCostCenterBean.setStrCostCenterName(obj[3].toString());// costCenterName
					objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));// subTotal
					objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));// discount
					objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));// salesAmount
					listOfCostCenterDtl.add(objCostCenterBean);
				}
			}

			// Q data
			listCostCenterData = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listCostCenterData.size() > 0) {
				for (int i = 0; i < listCostCenterData.size(); i++) {
					Object[] obj = (Object[]) listCostCenterData.get(i);
					clsPOSCostCenterBean objCostCenterBean = new clsPOSCostCenterBean();

					objCostCenterBean.setStrPOSCode(obj[0].toString());// posCode
					objCostCenterBean.setStrPOSName(obj[1].toString());// posName
					objCostCenterBean.setStrCostCenterCode(obj[2].toString());// costCenterCode
					objCostCenterBean.setStrCostCenterName(obj[3].toString());// costCenterName
					objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));// subTotal
					objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));// discount
					objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));// salesAmount
					listOfCostCenterDtl.add(objCostCenterBean);
				}
			}

			// Q modifier data
			listCostCenterData = objBaseService.funGetList(sbSqlModQFile, "sql");
			if (listCostCenterData.size() > 0) {
				for (int i = 0; i < listCostCenterData.size(); i++) {
					Object[] obj = (Object[]) listCostCenterData.get(i);
					clsPOSCostCenterBean objCostCenterBean = new clsPOSCostCenterBean();
					objCostCenterBean.setStrPOSCode(obj[0].toString());// posCode
					objCostCenterBean.setStrPOSName(obj[1].toString());// posName
					objCostCenterBean.setStrCostCenterCode(obj[2].toString());// costCenterCode
					objCostCenterBean.setStrCostCenterName(obj[3].toString());// costCenterName
					objCostCenterBean.setDblSubTotal(Double.parseDouble(obj[4].toString()));// subTotal
					objCostCenterBean.setDblDiscAmount(Double.parseDouble(obj[5].toString()));// discount
					objCostCenterBean.setDblSalesAmount(Double.parseDouble(obj[6].toString()));// salesAmount
					listOfCostCenterDtl.add(objCostCenterBean);
				}
			}

			Comparator<clsPOSCostCenterBean> posCodeComparator = new Comparator<clsPOSCostCenterBean>() {

				@Override
				public int compare(clsPOSCostCenterBean o1, clsPOSCostCenterBean o2) {
					return o1.getStrPOSCode().compareTo(o2.getStrPOSCode());
				}
			};
			Comparator<clsPOSCostCenterBean> costCenterCodeComparator = new Comparator<clsPOSCostCenterBean>() {

				@Override
				public int compare(clsPOSCostCenterBean o1, clsPOSCostCenterBean o2) {
					return o1.getStrCostCenterCode().compareTo(o2.getStrCostCenterCode());
				}
			};

			Collections.sort(listOfCostCenterDtl,
					new clsPOSCostCenterComparator(posCodeComparator, costCenterCodeComparator));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfCostCenterDtl;
	}

	public List funProcessCostCenterWiseDetailReport(String posCode, String fromDate, String toDate,
			String strReportType, String strShiftNo, String costCenterCode, String userCode) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlModLive = new StringBuilder();
		StringBuilder sbSqlModQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		List listOfCostCenterDtl = new ArrayList();
		String gAreaWisePricing = "";

		try {
			StringBuilder sqlAreaWisePrice = new StringBuilder();
			sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
			List listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
			if (listAraWiePrice.size() > 0) {
				gAreaWisePricing = (String) listAraWiePrice.get(0);
			}

			sbSqlQFile.setLength(0);
			sbSqlQFile.append("SELECT ifnull(f.strCostCenterName,''),a.strItemName,sum(a.dblQuantity), sum(a.dblAmount)"
					+ " ,b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
					+ " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt)\n"
					+ " FROM tblqbilldtl a inner join tblqbillhd b on a.strBillNo=b.strBillNo \n"
					+ " inner join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlQFile.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
					+ " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSqlQFile.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
			}
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlQFile.append(" and b.strPOSCode='" + posCode + "' ");
			}

			sbSqlLive.setLength(0);
			sbSqlLive.append("SELECT ifnull(f.strCostCenterName,''),a.strItemName,sum(a.dblQuantity), sum(a.dblAmount)"
					+ " ,b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
					+ " ,sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt)\n"
					+ " FROM tblbilldtl a inner join tblbillhd b on a.strBillNo=b.strBillNo \n"
					+ " inner join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlLive.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
					+ " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
			}
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlLive.append(" and b.strPOSCode='" + posCode + "' ");
				sbSqlQFile.append(" and b.strPOSCode='" + posCode + "' ");
			}

			sbSqlLive.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode,a.strItemName");
			sbSqlQFile.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode,a.strItemName");
			System.out.println("detail live=" + sbSqlQFile);
			System.out.println("detail Q=" + sbSqlLive);

			List listOfData = objBaseService.funGetList(sbSqlLive, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					clsPOSCostCenterBean objBean = new clsPOSCostCenterBean();
					objBean.setStrCostCenterName(obj[0].toString());
					objBean.setStrItemName(obj[1].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
					objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
					objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
					listOfCostCenterDtl.add(objBean);
				}
			}

			listOfData = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					clsPOSCostCenterBean objBean = new clsPOSCostCenterBean();
					objBean.setStrCostCenterName(obj[0].toString());
					objBean.setStrItemName(obj[1].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
					objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
					objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
					listOfCostCenterDtl.add(objBean);
				}
			}

			sbSqlModLive.setLength(0);
			sbSqlModLive.append("SELECT ifnull(f.strCostCenterName,''),a.strModifierName,sum(a.dblQuantity)"
					+ " ,sum(a.dblAmount),b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
					+ " ,sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt) "
					+ " FROM tblbillmodifierdtl a inner join tblbillhd b on a.strBillNo=b.strBillNo "
					+ " inner join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)  = d.strItemCode "
					+ " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlModLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlModLive.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
					+ " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " and a.dblAmount>0 ");
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSqlModLive.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
			}

			sbSqlModQFile.setLength(0);
			sbSqlModQFile.append("SELECT ifnull(f.strCostCenterName,''),a.strModifierName,sum(a.dblQuantity) "
					+ " ,sum(a.dblAmount),b.strPOSCode,'" + userCode + "',ifnull(d.strPriceMonday,0.00)"
					+ " ,sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt) "
					+ " FROM tblqbillmodifierdtl a inner join tblqbillhd b on a.strBillNo=b.strBillNo "
					+ " inner join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)  = d.strItemCode "
					+ " and (b.strposcode =d.strposcode  or d.strPosCode='All') and d.strHourlyPricing='NO' ");
			if (gAreaWisePricing.equals("Y")) {
				sbSqlModQFile.append(" and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlModQFile.append(" inner join tblcostcentermaster f on d.strCostCenterCode=f.strCostCenterCode "
					+ " where date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ " and a.dblAmount>0 ");
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sbSqlModQFile.append(" and d.strCostCenterCode='" + costCenterCode + "' ");
			}
			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlModQFile.append(" and b.strPOSCode='" + posCode + "' ");
				sbSqlModLive.append(" and b.strPOSCode='" + posCode + "' ");
			}

			sbSqlModQFile.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode, d.strItemName");
			sbSqlModLive.append(" Group by f.strCostCenterCode,b.strPoscode, a.strItemCode, d.strItemName");

			listOfData = objBaseService.funGetList(sbSqlModLive, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					clsPOSCostCenterBean objBean = new clsPOSCostCenterBean();
					objBean.setStrCostCenterName(obj[0].toString());
					objBean.setStrItemName(obj[1].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
					objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
					objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
					listOfCostCenterDtl.add(objBean);
				}
			}

			listOfData = objBaseService.funGetList(sbSqlModQFile, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					clsPOSCostCenterBean objBean = new clsPOSCostCenterBean();
					objBean.setStrCostCenterName(obj[0].toString());
					objBean.setStrItemName(obj[1].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
					objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
					objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
					listOfCostCenterDtl.add(objBean);
				}
			}

			// Non chargable kots
			StringBuilder sqlNonChargableKOts = new StringBuilder();

			sqlNonChargableKOts.append(
					"select ifnull(c.strCostCenterName,''),b.strItemName,sum(a.dblQuantity),0.00 as dblAmount,a.strPOSCode,'user',a.dblRate,0.00 as dblAmt_dblDisc,0.00 as dblDisc "
							+ "from tblnonchargablekot a,tblmenuitempricingdtl b,tblcostcentermaster c "
							+ "where date(a.dteNCKOTDate) between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strItemCode=b.strItemCode "
							+ "and (a.strposcode =b.strposcode  or b.strPosCode='All') "
							+ "and b.strCostCenterCode=c.strCostCenterCode and b.strHourlyPricing='NO' ");
			if (!costCenterCode.equalsIgnoreCase("All")) {
				sqlNonChargableKOts.append(" and b.strCostCenterCode='" + costCenterCode + "' ");
			}
			if (!posCode.equalsIgnoreCase("All")) {
				sqlNonChargableKOts.append(" and a.strPOSCode='" + posCode + "' ");
			}
			sqlNonChargableKOts.append("group by a.strItemCode,b.strItemName,c.strCostCenterCode,c.strCostCenterName ");
			sqlNonChargableKOts.append("order by a.strItemCode,b.strItemName,c.strCostCenterCode,c.strCostCenterName ");

			listOfData = objBaseService.funGetList(sqlNonChargableKOts, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					clsPOSCostCenterBean objBean = new clsPOSCostCenterBean();
					objBean.setStrCostCenterName(obj[0].toString());
					objBean.setStrItemName(obj[1].toString());
					objBean.setDblQuantity(Double.parseDouble(obj[2].toString()));
					objBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
					objBean.setDblDiscAmount(Double.parseDouble(obj[8].toString()));
					objBean.setDblSalesAmount(Double.parseDouble(obj[7].toString()));
					listOfCostCenterDtl.add(objBean);
				}
			}

			Comparator<clsPOSCostCenterBean> costCenterCodeComparator = new Comparator<clsPOSCostCenterBean>() {

				@Override
				public int compare(clsPOSCostCenterBean o1, clsPOSCostCenterBean o2) {
					return o1.getStrCostCenterCode().compareTo(o2.getStrCostCenterCode());
				}
			};

			Collections.sort(listOfCostCenterDtl, new clsPOSCostCenterComparator(costCenterCodeComparator));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listOfCostCenterDtl;
	}

	public List funProcessBillWiseReport(String posCode, String fromDate, String toDate, String shiftNo,
			String queryType) {

		StringBuilder sqlBuilder = new StringBuilder();
		List listRet = new ArrayList();
		try {
			// live
			if (queryType.equalsIgnoreCase("liveData")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append(
						"select a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate ,b.strPosName, "
								+ "ifnull(d.strSettelmentDesc,'') as strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt  "
								+ ",sum(c.dblSettlementAmt) as dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode as settlementType,intBillSeriesPaxNo "
								+ "from  tblbillhd a,tblposmaster b,tblbillsettlementdtl c,tblsettelmenthd d "
								+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "' "
								+ "and a.strPOSCode=b.strPOSCode " + "and a.strBillNo=c.strBillNo "
								+ "and c.strSettlementCode=d.strSettelmentCode "
								+ "and date(a.dteBillDate)=date(c.dteBillDate) "
								+ "and a.strClientCode=c.strClientCode ");
				if (!posCode.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
				}
				if (!shiftNo.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
				}
				sqlBuilder.append(
						"GROUP BY b.strPosName,a.strClientCode,date(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
								+ "ORDER BY a.strSettelmentMode,a.strBillNo ASC ");
				listRet = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (queryType.equalsIgnoreCase("qData")) {
				// QFile
				sqlBuilder.setLength(0);
				sqlBuilder.append(
						"select a.strBillNo,DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y') as dteBillDate ,b.strPosName, "
								+ "ifnull(d.strSettelmentDesc,'') as strSettelmentMode,a.dblDiscountAmt,a.dblTaxAmt   "
								+ ",sum(c.dblSettlementAmt) as dblSettlementAmt,a.dblSubTotal,a.strSettelmentMode as settlementType,intBillSeriesPaxNo "
								+ "from  tblqbillhd a,tblposmaster b,tblqbillsettlementdtl c,tblsettelmenthd d "
								+ "where date(a.dteBillDate) between '" + fromDate + "' and  '" + toDate + "' "
								+ "and a.strPOSCode=b.strPOSCode " + "and a.strBillNo=c.strBillNo "
								+ "and c.strSettlementCode=d.strSettelmentCode "
								+ "and date(a.dteBillDate)=date(c.dteBillDate) "
								+ "and a.strClientCode=c.strClientCode ");
				if (!posCode.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
				}
				if (!shiftNo.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
				}
				sqlBuilder.append(
						"GROUP BY b.strPosName,a.strClientCode,date(a.dteBillDate),a.strBillNo,d.strSettelmentCode "
								+ "ORDER BY a.strSettelmentMode,a.strBillNo ASC ");
				listRet = objBaseService.funGetList(sqlBuilder, "sql");
			} else {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT a.strBillNo "
						+ ",DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') AS BillDate, DATE_FORMAT(DATE(a.dteModifyVoidBill),'%d-%m-%Y') AS VoidedDate "
						+ ",TIME(a.dteBillDate) AS EntryTime, TIME(a.dteModifyVoidBill) VoidedTime, a.dblModifiedAmount AS BillAmount "
						+ ",a.strReasonName AS Reason,a.strUserEdited AS UserEdited,a.strUserCreated,a.strRemark "
						+ " from tblvoidbillhd a,tblvoidbilldtl b " + " where a.strBillNo=b.strBillNo "
						+ " and b.strTransType='VB' " + " and a.strTransType='VB' "
						+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and Date(a.dteModifyVoidBill)  Between '"
						+ fromDate + "' and '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
				}
				if (!shiftNo.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.intShiftCode='" + shiftNo + "'  ");
				}
				sqlBuilder.append(" group by a.dteBillDate,a.strBillNo ");
				listRet = objBaseService.funGetList(sqlBuilder, "sql");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listRet;
	}

	public List funProcessGroupSubGroupWiseDetailReport(String posCode, String fromDate, String toDate, String shiftNo,
			String subGroupCode, String groupCode, String userCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsPOSGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsPOSGroupSubGroupItemBean>();
		try {
			Map<String, clsPOSGroupSubGroupItemBean> mapItemDtl = new HashMap<>();
			clsPOSGroupSubGroupItemBean objGroupSubGroupItemBean = null;

			// QFile
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
							+ ",ifnull(sum(b.dblAmount),0) as Amount,b.strItemCode " + "from tblqbillhd a "
							+ "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
							+ "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
							+ "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
							+ "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
							+ "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
							+ "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode
							+ "') ");
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append("Group By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName "
							+ "order By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName");
			List listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);

					String itemCode = obj[5].toString();

					if (mapItemDtl.containsKey(itemCode)) {
						objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

						objGroupSubGroupItemBean.setDblQuantity(
								objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble(obj[3].toString())));
						objGroupSubGroupItemBean.setDblAmount(
								objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					} else {
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					}
				}
			}

			// QFile modifiers
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
							+ ",ifnull(sum(f.dblAmount),0) as Amount,f.strItemCode " + "from tblqbillhd a "
							+ "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblqbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
							+ "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
							+ "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
							+ "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
							+ "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
							+ "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
							+ "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode
							+ "') " );
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append( "and b.strItemCode=left(f.strItemCode,7) " + "and f.dblAmount>0 "
							+ "Group By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName "
							+ "order By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					String itemCode = obj[5].toString();

					if (mapItemDtl.containsKey(itemCode)) {
						objGroupSubGroupItemBean = mapItemDtl.get(itemCode);
						objGroupSubGroupItemBean.setDblQuantity(
								objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble(obj[3].toString())));
						objGroupSubGroupItemBean.setDblAmount(
								objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					} else {
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					}
				}
			}

			// Live
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
							+ ",ifnull(sum(b.dblAmount),0) as Amount,b.strItemCode " + "from tblbillhd a "
							+ "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
							+ "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
							+ "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
							+ "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
							+ "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
							+ "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode
							+ "') "); 
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}				
			sqlBuilder.append("Group By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName "
							+ "order By e.strGroupName ,d.strSubGroupName,b.strItemCode,b.strItemName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					String itemCode = obj[5].toString();

					if (mapItemDtl.containsKey(itemCode)) {
						objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

						objGroupSubGroupItemBean.setDblQuantity(
								objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble(obj[3].toString())));
						objGroupSubGroupItemBean.setDblAmount(
								objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					} else {
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					}
				}
			}

			// Live modifiers
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
							+ ",ifnull(sum(f.dblAmount),0) as Amount,f.strItemCode " + "from tblbillhd a "
							+ "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "left outer join tblbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
							+ "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
							+ "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
							+ "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
							+ "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
							+ "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
							+ "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
							+ "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode
							+ "') " );
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append("and b.strItemCode=left(f.strItemCode,7) " + "and f.dblAmount>0 "
							+ "Group By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName "
							+ "order By e.strGroupName ,d.strSubGroupName,f.strItemCode,f.strModifierName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					String itemCode = obj[5].toString();

					if (mapItemDtl.containsKey(itemCode)) {
						objGroupSubGroupItemBean = mapItemDtl.get(itemCode);

						objGroupSubGroupItemBean.setDblQuantity(
								objGroupSubGroupItemBean.getDblQuantity() + (Double.parseDouble(obj[3].toString())));
						objGroupSubGroupItemBean.setDblAmount(
								objGroupSubGroupItemBean.getDblAmount() + (Double.parseDouble(obj[4].toString())));

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					} else {
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						objGroupSubGroupItemBean.setStrItemCode(obj[5].toString());

						mapItemDtl.put(itemCode, objGroupSubGroupItemBean);
					}
				}
			}

			Comparator<clsPOSGroupSubGroupItemBean> groupComparator = new Comparator<clsPOSGroupSubGroupItemBean>() {

				@Override
				public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2) {
					return o1.getStrGroupName().compareToIgnoreCase(o2.getStrGroupName());
				}
			};

			Comparator<clsPOSGroupSubGroupItemBean> subGroupComparator = new Comparator<clsPOSGroupSubGroupItemBean>() {

				@Override
				public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2) {
					return o1.getStrSubGroupName().compareToIgnoreCase(o2.getStrSubGroupName());
				}
			};

			Comparator<clsPOSGroupSubGroupItemBean> codeComparator = new Comparator<clsPOSGroupSubGroupItemBean>() {

				@Override
				public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2) {
					return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
				}
			};

			listOfGroupSubGroupWiseSales.addAll(mapItemDtl.values());

			Collections.sort(listOfGroupSubGroupWiseSales,
					new clsPOSGroupSubGroupComparator(groupComparator, subGroupComparator, codeComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listOfGroupSubGroupWiseSales;
	}

	public List funProcessGroupSubGroupWiseSummaryReport(String posCode, String fromDate, String toDate, String shiftNo,
			String subGroupCode, String groupCode, String userCode,String enableShiftYN) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsPOSGroupSubGroupItemBean> listOfGroupSubGroupWiseSales = new ArrayList<clsPOSGroupSubGroupItemBean>();
		try {
			clsPOSGroupSubGroupItemBean objGroupSubGroupItemBean = null;

			// QFile
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
						    + ",IFNULL(SUM(b.dblAmount)- SUM(b.dblDiscountAmt),0)  as Amount "
						    + "from tblqbillhd a "
						    + "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
						    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
						    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
						    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
						    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
						    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
						    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') ");
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append("Group By e.strGroupName ,d.strSubGroupName "
							+ "order By e.strGroupName ,d.strSubGroupName");
			List listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) 
			{
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
					objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
					objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
					objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
					objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
					listOfGroupSubGroupWiseSales.add(objGroupSubGroupItemBean);
					}
			}
			

			// QFile modifiers
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
						    + ", IFNULL(SUM(f.dblAmount)-SUM(f.dblDiscAmt),0)  as Amount "
						    + "from tblqbillhd a "
						    + "left outer join tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						    + "left outer join tblqbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
						    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
						    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
						    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
						    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
						    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
						    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
						    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') " );
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append( "and b.strItemCode=left(f.strItemCode,7) " + "and f.dblAmount>0 "
							+ "Group By e.strGroupName ,d.strSubGroupName "
							+ "order By e.strGroupName ,d.strSubGroupName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						listOfGroupSubGroupWiseSales.add(objGroupSubGroupItemBean);
					
				}
			}

			// Live
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select b.strItemName,d.strSubGroupName,e.strGroupName ,ifnull(sum(b.dblQuantity),0) as Quantity "
				    + ",IFNULL(SUM(b.dblAmount)- SUM(b.dblDiscountAmt),0)  as Amount "
				    + "from tblbillhd a "
				    + "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
				    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
				    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
				    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
				    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
				    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
				    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
				    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') "); 
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}				
			sqlBuilder.append("Group By e.strGroupName ,d.strSubGroupName "
							+ "order By e.strGroupName ,d.strSubGroupName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					
					objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
					objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
					objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
					objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
					objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
					objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
					listOfGroupSubGroupWiseSales.add(objGroupSubGroupItemBean);
				
				}
			}

			// Live modifiers
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select f.strModifierName,d.strSubGroupName,e.strGroupName ,ifnull(sum(f.dblQuantity),0) as Quantity "
						    + ", IFNULL(SUM(f.dblAmount)-SUM(f.dblDiscAmt),0)  as Amount "
						    + "from tblbillhd a "
						    + "left outer join tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						    + "left outer join tblbillmodifierdtl f on b.strBillNo=f.strBillNo  and date(a.dteBillDate)=date(f.dteBillDate) "
						    + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
						    + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
						    + "left outer join tblgrouphd e on d.strGroupCode=e.strGroupCode "
						    + "where  date(a.dteBillDate)  between '" + fromDate + "' and '" + toDate + "' "
						    + "and a.strPoscode=if('" + posCode + "'='All', a.strPoscode,'" + posCode + "') "
						    + "and e.strGroupCode=if('" + groupCode + "'='All',e.strGroupCode,'" + groupCode + "') "
						    + "and d.strSubGroupCode=if('" + subGroupCode + "'='All',d.strSubGroupCode,'" + subGroupCode + "') " );
			if(enableShiftYN.equalsIgnoreCase("Y") && (!shiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode=if('" + shiftNo + "'='All',a.intShiftCode,'" + shiftNo + "')");
			}
			sqlBuilder.append("and b.strItemCode=left(f.strItemCode,7) " + "and f.dblAmount>0 "
							+ "Group By e.strGroupName ,d.strSubGroupName "
							+ "order By e.strGroupName ,d.strSubGroupName");
			listOfData = objBaseService.funGetList(sqlBuilder, "sql");
			if (listOfData.size() > 0) {
				for (int i = 0; i < listOfData.size(); i++) {
					Object[] obj = (Object[]) listOfData.get(i);
					
						objGroupSubGroupItemBean = new clsPOSGroupSubGroupItemBean();
						objGroupSubGroupItemBean.setStrItemName(obj[0].toString());
						objGroupSubGroupItemBean.setStrSubGroupName(obj[1].toString());
						objGroupSubGroupItemBean.setStrGroupName(obj[2].toString());
						objGroupSubGroupItemBean.setDblQuantity(Double.parseDouble(obj[3].toString()));
						objGroupSubGroupItemBean.setDblAmount(Double.parseDouble(obj[4].toString()));
						listOfGroupSubGroupWiseSales.add(objGroupSubGroupItemBean);
					
				}
			}
			Comparator<clsPOSGroupSubGroupItemBean> groupComparator = new Comparator<clsPOSGroupSubGroupItemBean>()
		    {

			@Override
			public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2)
			{
			    return o1.getStrGroupName().compareToIgnoreCase(o2.getStrGroupName());
			}
		    };

		    Comparator<clsPOSGroupSubGroupItemBean> subGroupComparator = new Comparator<clsPOSGroupSubGroupItemBean>()
		    {

			@Override
			public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2)
			{
			    return o1.getStrSubGroupName().compareToIgnoreCase(o2.getStrSubGroupName());
			}
		    };

		    Collections.sort(listOfGroupSubGroupWiseSales, new clsPOSGroupSubGroupComparator(groupComparator, subGroupComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listOfGroupSubGroupWiseSales;
	}

	
	public List funProcessGroupWiseReport(String strPOSCode, String fromDate, String toDate, String strUserCode,
			String strShiftNo, String strSGCode,String enableShiftYN) {
		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sbSqlFilters = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();
		List<clsPOSGroupWaiseSalesBean> list = new ArrayList<>();

		sbSqlLive.setLength(0);
		sbSqlQFile.setLength(0);
		sbSqlFilters.setLength(0);

		try {
			sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
					+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) " + ",f.strPosName, '" + strUserCode + "'"
					+ ",b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
					+ "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
					+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
					+ ",tblitemmaster e,tblposmaster f "
					+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
					+ "and a.strClientCode=b.strClientCode " + "and b.strItemCode=e.strItemCode "
					+ "and c.strGroupCode=d.strGroupCode " + "and d.strSubGroupCode=e.strSubGroupCode ");

			sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
					+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) " + ",f.strPosName, '" + strUserCode
					+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
					+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d" + ",tblitemmaster e,tblposmaster f "
					+ "where a.strBillNo=b.strBillNo " + "and a.strPOSCode=f.strPOSCode  "
					+ "and a.strClientCode=b.strClientCode   " + "and b.strItemCode=e.strItemCode "
					+ "and c.strGroupCode=d.strGroupCode " + "and d.strSubGroupCode=e.strSubGroupCode ");

			sqlModLive.append("select c.strGroupCode,c.strGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
					+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd e,tblgrouphd c "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
					+ "and a.strClientCode=b.strClientCode  " + " and LEFT(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode " + "and e.strGroupCode=c.strGroupCode "
					+ " and b.dblamount>0 ");

			sqlModQFile.append("select c.strGroupCode,c.strGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
					+ "','0' " + ",sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt) "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd e,tblgrouphd c " + " where a.strBillNo=b.strBillNo "
					+ "and a.strPOSCode=f.strPosCode   " + "and a.strClientCode=b.strClientCode   "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode " + " and d.strSubGroupCode=e.strSubGroupCode "
					+ "and e.strGroupCode=c.strGroupCode " + " and b.dblamount>0 ");

			sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
			}
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
			    sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
			}
		    
			
			if (!strSGCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append("AND d.strSubGroupCode='" + strSGCode + "' ");
			}
			sbSqlFilters.append(" Group BY c.strGroupCode, c.strGroupName, a.strPoscode ");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters);
			sqlModQFile.append(sbSqlFilters);

			Map<String, clsPOSGroupWaiseSalesBean> mapGroup = new HashMap<>();

			List listSqlLive = objBaseService.funGetList(sbSqlLive, "sql");
			if (listSqlLive.size() > 0) {
				for (int i = 0; i < listSqlLive.size(); i++) {
					Object[] obj = (Object[]) listSqlLive.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
//					double salesAmt = Double.parseDouble(obj[3].toString());

					if (mapGroup.containsKey(groupCode)) {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					} else {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsPOSGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}

				}
			}

			List listSqlQFile = objBaseService.funGetList(sbSqlQFile, "sql");
			if (listSqlQFile.size() > 0) {

				for (int i = 0; i < listSqlQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlQFile.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
//					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode)) {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					} else {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsPOSGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			List listSqlModLive = objBaseService.funGetList(sqlModLive, "sql");
			if (listSqlModLive.size() > 0) {

				for (int i = 0; i < listSqlModLive.size(); i++) {
					Object[] obj = (Object[]) listSqlModLive.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
//					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode)) {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					} else {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsPOSGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			List listSqlModQFile = objBaseService.funGetList(sqlModQFile, "sql");
			if (listSqlModQFile.size() > 0) {
				for (int i = 0; i < listSqlModQFile.size(); i++) {
					Object[] obj = (Object[]) listSqlModQFile.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
//					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode)) {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						//objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					} else {
						clsPOSGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsPOSGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
//						objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(netTotal);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			for (clsPOSGroupWaiseSalesBean objGroupWaiseSalesBean : mapGroup.values()) {
				list.add(objGroupWaiseSalesBean);
			}

			Comparator<clsPOSGroupWaiseSalesBean> groupComparator = new Comparator<clsPOSGroupWaiseSalesBean>() {

				@Override
				public int compare(clsPOSGroupWaiseSalesBean o1, clsPOSGroupWaiseSalesBean o2) {
					return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
				}
			};

			Collections.sort(list, new clsPOSGroupWiseComparator(groupComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	
	public List funProcessItemWiseReport(String posCode, String fromDate, String toDate, String shiftNo,
			String printComplimentaryYN, String type, String strUserCode) {
		List list = new ArrayList();
		StringBuilder sqlLive = new StringBuilder();
		StringBuilder sqlLiveCompli = new StringBuilder();
		StringBuilder sqlQFile = new StringBuilder();
		StringBuilder sqlQCompli = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();

		try 
		 {
			
		    String taxAmt = "sum(a.dblTaxAmount)";
		    String amt = "sum(a.dblAmount)";
		    String subTotAmt = "sum(a.dblAmount)-sum(a.dblDiscountAmt)";
		    String discAmt = "sum(a.dblDiscountAmt)";
		    String amount = "sum(a.dblAmount)";
		    String subTotAmount = "sum(a.dblAmount)-sum(a.dblDiscAmt)";
		    String discAmount = "sum(a.dblDiscAmt)";
			String sqlFilters = "";

			if (!posCode.equalsIgnoreCase("All")) {
				sqlFilters += " AND b.strPOSCode = '" + posCode + "' ";
			}
			if (!shiftNo.equalsIgnoreCase("All")) {
				sqlFilters += " AND b.intShiftCode = '" + shiftNo + "' ";
			}

			if (type.equalsIgnoreCase("Live")) 
			{
				sqlLive.append("select a.strItemCode,a.strItemName,c.strPOSName"
			    + ",sum(a.dblQuantity)," + taxAmt + "\n"
			    + "," + amt + "," + subTotAmt + "," + discAmt + ",DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode+ "'\n"
			    + "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + " and a.strClientCode=b.strClientCode ");
				sqlLive.append(sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ");
				list = objBaseService.funGetList(sqlLive, "sql");

			} else if (type.equalsIgnoreCase("LiveCompli")) 
			{
				sqlLiveCompli.append("select a.strItemCode,a.strItemName,c.strPOSName"
			    + ",sum(a.dblQuantity)," + taxAmt + "\n"
			    + "," + amt + "," + subTotAmt + "," + discAmt + ",DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode+ "'\n"
			    + "from tblbillcomplementrydtl a,tblbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + " and a.strClientCode=b.strClientCode ");
				sqlLiveCompli.append("  GROUP BY a.strItemCode,a.strItemName ");
				list = objBaseService.funGetList(sqlLiveCompli, "sql");

			} 
			else if (type.equalsIgnoreCase("QFile")) 
			{
				sqlQFile.append( "select a.strItemCode,a.strItemName,c.strPOSName"
			    + ",sum(a.dblQuantity)," + taxAmt + "\n"
			    + "," + amt + "," + subTotAmt + "," + discAmt + ",DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode+ "'\n"
			    + "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + " and a.strClientCode=b.strClientCode ");
				sqlQFile.append("  GROUP BY a.strItemCode,a.strItemName ");
				list = objBaseService.funGetList(sqlQFile, "sql");

			} 
			else if (type.equalsIgnoreCase("QCompli")) 
			{
				sqlQCompli.append("select a.strItemCode,a.strItemName,c.strPOSName"
			    + ",sum(a.dblQuantity)," + taxAmt + "\n"
			    + "," + amt + "," + subTotAmt + "," + discAmt + ",DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
			    + "from tblqbillcomplementrydtl a,tblqbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + " and a.strClientCode=b.strClientCode ");
				sqlQCompli.append("  GROUP BY a.strItemCode,a.strItemName ");
				list = objBaseService.funGetList(sqlQCompli, "sql");

			} 
			else if (type.equalsIgnoreCase("ModLive")) 
			{
				sqlModLive.append("select a.strItemCode,a.strModifierName,c.strPOSName"
			    + ",sum(a.dblQuantity),'0'," + amount + "," + subTotAmount + "," + discAmount + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
			    + "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and a.dblamount>0 \n"
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
			    + " and a.strClientCode=b.strClientCode  ");
				sqlModLive.append("  GROUP BY a.strItemCode,a.strModifierName ");
				list = objBaseService.funGetList(sqlModLive, "sql");

			} 
			else if (type.equalsIgnoreCase("ModQFile")) 
			{
				sqlModQFile.append("select a.strItemCode,a.strModifierName,c.strPOSName"
			    + ",sum(a.dblQuantity),'0'," + amount + "," + subTotAmount + "," + discAmount + ",DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode+ "'\n"
			    + "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
			    + "where a.strBillNo=b.strBillNo "
			    + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
			    + "and b.strPOSCode=c.strPosCode "
			    + "and a.dblamount>0 \n"
			    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
			    + "and a.strClientCode=b.strClientCode  ");
		        sqlModQFile.append("  GROUP BY a.strItemCode,a.strModifierName ");
				list = objBaseService.funGetList(sqlModQFile, "sql");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	
	public List funProcessMenuHeadWiseReport(String posCode, String fromDate, String toDate, String strUserCode,
			String shiftNo) {
		List<clsPOSGenericBean> listOfMenuHead = new ArrayList<clsPOSGenericBean>();
		try {
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			String gAreaWisePricing = "";
			List listAraWiePrice = new ArrayList();

			StringBuilder sqlAreaWisePrice = new StringBuilder();
			sqlAreaWisePrice.append("select a.strAreaWisePricing from tblsetup a");
			listAraWiePrice = objBaseService.funGetList(sqlAreaWisePrice, "sql");
			if (listAraWiePrice.size() > 0) {
				gAreaWisePricing = (String) listAraWiePrice.get(0);
			}

			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);

			sbSqlQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode
					+ "',sum(a.dblRate),sum(a.dblAmount) ,sum(a.dblDiscountAmt) " + "FROM tblqbilldtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y")) {
				sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			sbSqlLive.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode
					+ "',sum(a.dblRate) ,sum(a.dblAmount),sum(a.dblDiscountAmt) " + " FROM tblbilldtl a\n"
					+ " left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y")) {
				sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			sqlModLive.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode
					+ "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) " + "FROM tblbillmodifierdtl a\n"
					+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ");

			if (gAreaWisePricing.equalsIgnoreCase("Y")) {
				sqlModLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sqlModLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sqlModLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
					+ "' and a.dblAmount>0 " + " and a.strClientCode=b.strClientCode ");

			sqlModQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode
					+ "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) " + "FROM tblqbillmodifierdtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ");

			if (gAreaWisePricing.equalsIgnoreCase("Y")) {
				sqlModQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sqlModQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sqlModQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
					+ "' and a.dblAmount>0 " + " and a.strClientCode=b.strClientCode ");

			if (!posCode.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
			}
			if (!shiftNo.equalsIgnoreCase("All")) {
				sbSqlFilters.append(" AND b.intShiftCode = '" + shiftNo + "' ");
			}
			sbSqlFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive.append(sbSqlFilters.toString());
			sqlModQFile.append(sbSqlFilters.toString());

			Map<String, clsPOSGenericBean> mapMenuDtl = new HashMap<String, clsPOSGenericBean>();

			// live data
			List list = objBaseService.funGetList(sbSqlLive, "sql");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode)) {
						clsPOSGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					} else {
						clsPOSGenericBean obj = new clsPOSGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}

			// live modifiers
			list = objBaseService.funGetList(sqlModLive, "sql");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode)) {
						clsPOSGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					} else {
						clsPOSGenericBean obj = new clsPOSGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// Q data
			list = objBaseService.funGetList(sbSqlQFile, "sql");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode)) {
						clsPOSGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					} else {
						clsPOSGenericBean obj = new clsPOSGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// live modifiers
			list = objBaseService.funGetList(sqlModQFile, "sql");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode)) {
						clsPOSGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					} else {
						clsPOSGenericBean obj = new clsPOSGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// convert to list

			for (clsPOSGenericBean objBean : mapMenuDtl.values()) {
				listOfMenuHead.add(objBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (List) listOfMenuHead;
	}

	public List funProcessNonChargableKotSettlementReport(String posCode, String fromDate, String toDate,
			String reasonCode) {
		List<clsPOSBillDtl> listOfNCKOTData = new ArrayList<clsPOSBillDtl>();
		try {
			StringBuilder sqlBuilder = new StringBuilder();

			// live
			sqlBuilder.setLength(0);
			sqlBuilder.append(
					"select a.strKOTNo, DATE_FORMAT(a.dteNCKOTDate,'%d-%m-%Y %H:%i'), a.strTableNo, b.strReasonName,d.strPosName,\n"
							+ "a.strRemark,  a.strItemCode, c.strItemName, a.dblQuantity, a.dblRate, a.dblQuantity * a.dblRate as Amount\n"
							+ ",e.strTableName\n"
							+ "from tblnonchargablekot a, tblreasonmaster b, tblitemmaster c,tblposmaster d,tbltablemaster e\n"
							+ "where  a.strReasonCode = b.strReasonCode \n" + "and a.strTableNo=e.strTableNo \n"
							+ "and a.strItemCode = c.strItemCode  and a.strPosCode=d.strPOSCode\n"
							+ "and date(a.dteNCKOTDate) between '" + fromDate + "' and  '" + toDate + "'\n ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "'  ");
			}

			List list = objBaseService.funGetList(sqlBuilder, "sql");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] objArr = (Object[]) list.get(i);

					clsPOSBillDtl obj = new clsPOSBillDtl();

					obj.setStrKOTNo(objArr[0].toString());
					obj.setDteNCKOTDate(objArr[1].toString());
					obj.setStrTableNo(objArr[2].toString());
					obj.setStrReasonName(objArr[3].toString());
					obj.setStrPosName(objArr[4].toString());
					obj.setStrRemarks(objArr[5].toString());
					obj.setStrItemCode(objArr[6].toString());
					obj.setStrItemName(objArr[7].toString());
					obj.setDblQuantity(Double.parseDouble(objArr[8].toString()));
					obj.setDblRate(Double.parseDouble(objArr[9].toString()));
					obj.setDblAmount(Double.parseDouble(objArr[10].toString()));
					obj.setStrTableName(objArr[11].toString());

					listOfNCKOTData.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfNCKOTData;
	}

	public List funProcessSalesSummaryReport(String payCode, String reportType, String fromDate, String toDate,
			String posCode) {
		StringBuilder sb = new StringBuilder();
		List list = new ArrayList();

		List<HashMap> listRet = new ArrayList<HashMap>();
		try {

			if (reportType.equalsIgnoreCase("Daily")) {
				if (payCode.equals("ALL")) {
					sb.setLength(0);
					sb.append("select a.strPOSCode,c.strPosName,date(a.dteBillDate)"
							+ ",sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
							+ " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
							+ " where a.strSettlementCode=b.strSettelmentCode " + " and a.strPOSCode=c.strPosCode"
							+ " and date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
					if (!posCode.equals("ALL")) {
						sb.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sb.append(" group by a.strPOSCode,date(a.dteBillDate) order by date(a.dteBillDate);");
				} else {
					sb.setLength(0);
					sb.append("select a.strPOSCode,c.strPosName,date(a.dteBillDate)"
							+ ",sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
							+ " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
							+ " where a.strSettlementCode=b.strSettelmentCode " + " and a.strPOSCode=c.strPosCode"
							+ " and date(dteBillDate) between '" + fromDate + "' and '" + toDate
							+ "' and b.strSettelmentCode='" + payCode + "' ");
					if (!posCode.equals("ALL")) {
						sb.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sb.append(" group by a.strPOSCode,date(a.dteBillDate) order by date(a.dteBillDate);");
				}
				list = objBaseService.funGetList(sb, "sql");

			} else {
				if (payCode.equals("ALL")) {
					sb.setLength(0);
					sb.append("select a.strPOSCode,c.strPOSName,monthname(date(a.dteBillDate)),year(date(a.dteBillDate)) "
					+ " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
					+ " where a.strSettlementCode=b.strSettelmentCode "
					+ " and a.strPOSCode=c.strPOSCode " + " and date(dteBillDate) between '"
					+ fromDate + "' and '" + toDate + "' ");
					if (!posCode.equals("ALL")) {
						sb.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sb.append("  group by a.strPOSCode,month(date(dteBillDate))"
							+ " order by a.strPOSCode,month(date(dteBillDate)) ");

				} else {

					sb.setLength(0);
					sb.append("select a.strPOSCode,c.strPOSName,monthname(date(a.dteBillDate)),year(date(a.dteBillDate)) "
					+ " from vqbillhdsettlementdtl a,tblsettelmenthd b,tblposmaster c "
					+ " where a.strSettlementCode=b.strSettelmentCode "
					+ " and a.strPOSCode=c.strPOSCode " + " and date(dteBillDate) between '"
					+ fromDate + "' and '" + toDate + "' and b.strSettelmentCode='" + payCode + "' ");
					if (!posCode.equals("ALL")) {
						sb.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sb.append("  group by a.strPOSCode,month(date(dteBillDate))"
							+ " order by a.strPOSCode,month(date(dteBillDate)) ");
				}
				list = objBaseService.funGetList(sb, "sql");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public Map funProcessSalesSummaryReport(String strposCode, String strBillDate, String settleAmtType) {
		List listRet = new ArrayList();
		StringBuilder sb = new StringBuilder();
		HashMap hmSettelmentDesc = new HashMap();
		try {
			if (settleAmtType.equalsIgnoreCase("dailySettleAmt")) {
				sb.setLength(0);
				sb.append("select a.strPOSCode,date(a.dteBillDate)"
						+ ",b.strSettelmentDesc,sum(a.dblSettlementAmt),sum(a.dblSettlementAmt) "
						+ " from vqbillhdsettlementdtl a,tblsettelmenthd b "
						+ " where a.strSettlementCode=b.strSettelmentCode " + " and date(dteBillDate) = '" + strBillDate
						+ "' and a.strPOSCode='" + strposCode + "' "
						+ " group by a.strPOSCode,date(a.dteBillDate),b.strSettelmentDesc "
						+ " order by a.strPOSCode,date(a.dteBillDate),b.strSettelmentDesc;");

				List listSql1 = objBaseService.funGetList(sb, "sql");
				if (listSql1.size() > 0) {
					List listSettlementAmt = new ArrayList();

					for (int j = 0; j < listSql1.size(); j++) {
						Object[] obj1 = (Object[]) listSql1.get(j);
						hmSettelmentDesc.put(obj1[2].toString(), obj1[4].toString());
					}

				}
			} else {
				sb.setLength(0);
				sb.append("select a.strPOSCode,date(a.dteBillDate)"
						+ ",b.strSettelmentDesc,sum(a.dblSettlementAmt),sum(a.dblGrandTotal) "
						+ " from vqbillhdsettlementdtl a,tblsettelmenthd b "
						+ " where a.strSettlementCode=b.strSettelmentCode " + " and monthname(date(dteBillDate)) ='"
						+ strBillDate + "' and a.strPOSCode='" + strposCode + "' "
						+ " group by a.strPOSCode,month(date(dteBillDate)),b.strSettelmentDesc  "
						+ " order by a.strPOSCode,month(date(dteBillDate)),b.strSettelmentDesc ;");

				List listSql1 = objBaseService.funGetList(sb, "sql");
				if (listSql1.size() > 0) {
					List listSettlementAmt = new ArrayList();

					for (int j = 0; j < listSql1.size(); j++) {
						Object[] obj1 = (Object[]) listSql1.get(j);
						hmSettelmentDesc.put(obj1[2].toString(), obj1[4].toString());
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmSettelmentDesc;
	}

	public List funProcessVoidKotReport(String posCode, String reportSubType, String fromDate, String toDate,
			String qType) {
		StringBuilder sqlBuilder = new StringBuilder();
		List list = new ArrayList();
		try {

			if (qType.equalsIgnoreCase("liveNoOfKotData")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct b.strKOTNo), SUM(b.dblQuantity)\n"
						+ " FROM tblbillhd a,tblbilldtl b,tbltablemaster c,tblwaitermaster d\n"
						+ " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strTableNo=c.strTableNo AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 \n"
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("qFileNoOfKotData")) {
				StringBuilder sqlQBuilder = new StringBuilder();
				sqlQBuilder.setLength(0);
				sqlQBuilder.append("SELECT COUNT(distinct b.strKOTNo), ifnull(SUM(b.dblQuantity),0) \n"
						+ " FROM tblqbillhd a,tblqbilldtl b,tbltablemaster c,tblwaitermaster d\n"
						+ " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strTableNo=c.strTableNo AND b.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>0 \n"
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'");
				list = objBaseService.funGetList(sqlQBuilder, "sql");
			} else if (qType.equalsIgnoreCase("liveVoidedBilledKOTs")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct b.strKOTNo),ifnull(sum(b.intQuantity),0) \n"
						+ "FROM tblvoidbillhd a,tblvoidbilldtl b,tbltablemaster c,tblwaitermaster d\n"
						+ "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ "AND a.strTableNo=c.strTableNo AND a.strWaiterNo=d.strWaiterNo AND LENGTH(b.strKOTNo)>2 \n"
						+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("lineVoidedKOTs")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
						+ "FROM tbllinevoid a\n" + "WHERE LENGTH(a.strKOTNo)>2 AND DATE(a.dteDateCreated) BETWEEN '"
						+ fromDate + "' and '" + toDate + "'\n");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("voidedKOT")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
						+ "FROM tblvoidkot a,tbltablemaster b,tblwaitermaster c,tblreasonmaster d\n"
						+ "WHERE a.strTableNo=b.strTableNo AND a.strWaiterNo=c.strWaiterNo "
						+ " AND a.strReasonCode=d.strReasonCode \n" + "AND LENGTH(a.strKOTNo)>2 "
						+ " and LEFT(a.strItemName,3)!='-->' " + " AND DATE(a.dteDateCreated) BETWEEN '" + fromDate
						+ "' and '" + toDate + "' and a.strType!='MVKot';\n");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("ncKots")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblQuantity),0) \n"
						+ "FROM tblnonchargablekot a,tbltablemaster b,tblreasonmaster c\n"
						+ "WHERE LENGTH(a.strKOTNo)>2 AND a.strTableNo=b.strTableNo AND a.strReasonCode=c.strReasonCode \n"
						+ "AND DATE(a.dteNCKOTDate) BETWEEN '" + fromDate + "' and '" + toDate + "'\n");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("voidKOTData")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("select a.strItemCode,a.strItemName,ifnull(d.strTableName,''),"
						+ " (a.dblAmount/a.dblItemQuantity) as dblRate,sum(a.dblItemQuantity)as dblItemQuantity,sum(a.dblAmount) as dblAmount "
						+ " ,a.strRemark,a.strKOTNo,a.strPosCode,b.strPosName,a.strUserCreated ,DATE_FORMAT(a.dteVoidedDate,'%d-%m-%Y %H:%i'),ifnull(c.strReasonName,'')"
						+ ",ifnull(e.strWShortName,''),if(a.strVoidBillType='N','Move KOT',a.strVoidBillType),DATE_FORMAT(a.dteDateCreated,'%d-%m-%Y %H:%i'),if(LEFT(a.strItemName,3)='-->','Y','N') isModifier  "
						+ " from tblvoidkot a " + " left outer join tblposmaster b on a.strPOSCode=b.strPosCode "
						+ " left outer join tblreasonmaster c on a.strReasonCode=c.strReasonCode "
						+ " left outer join tbltablemaster d on a.strTableNo=d.strTableNo"
						+ " left outer join tblwaitermaster e on a.strWaiterNo=e.strWaiterNo "
						+ " where date(a.dteVoidedDate) Between '" + fromDate + "' and '" + toDate + "' " + "  ");
				if (!posCode.equalsIgnoreCase("All")) {
					sqlBuilder.append("and a.strPosCode='" + posCode + "' ");
				}
				if (reportSubType.equals("Void KOT")) {
					sqlBuilder.append(" and (a.strType='VKot' or a.strType='DVKot') ");
				} else if (reportSubType.equals("Move KOT")) {
					sqlBuilder.append(" and a.strType='MVKot' ");
				}

				sqlBuilder.append(" group by a.strposcode,a.strusercreated,a.strkotno,a.strItemCode "
						+ " having  dblAmount>if(isModifier='Y',0,-1) "
						+ " order by a.strposcode,a.strusercreated,a.strkotno,a.strItemCode ");

				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("voidedKotCountForNotModif")) {
				// which is not modifiers
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
						+ "FROM tblvoidkot a \n" + "WHERE DATE(a.dteVoidedDate) BETWEEN '" + fromDate + "' and '"
						+ toDate + "' " + " and LEFT(a.strItemName,3)!='-->' " + " and a.strType!='MVKot' ");

				list = objBaseService.funGetList(sqlBuilder, "sql");
			} else if (qType.equalsIgnoreCase("voidedKotCountForModif")) {
				// which is modifiers but chargable
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT COUNT(distinct a.strKOTNo),ifnull(sum(a.dblItemQuantity),0) \n"
						+ "FROM tblvoidkot a \n" + "WHERE DATE(a.dteVoidedDate) BETWEEN '" + fromDate + "' and '"
						+ toDate + "' " + " and LEFT(a.strItemName,3)='-->' " + "and a.dblAmount>0 "
						+ " and a.strType!='MVKot' ");
				list = objBaseService.funGetList(sqlBuilder, "sql");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List funProcessTaxBreakUpSummaryReport(String posCode, String fromDate, String toDate, String strShiftNo) {
		List<clsPOSTaxCalculationDtls> listOfTaxDtl = new LinkedList<>();
		try {
			Map<String, clsPOSTaxCalculationDtls> mapTaxDtl = new HashMap<>();
			StringBuilder sqlTaxBuilder = new StringBuilder();
			StringBuilder sqlMenuBreakupBuilder = new StringBuilder();
			DecimalFormat decimalFormat2Decimal = new DecimalFormat("0.00");

			// live tax
			sqlTaxBuilder.setLength(0);
			sqlTaxBuilder.append(
					"SELECT b.strTaxCode,c.strTaxDesc,sum(b.dblTaxableAmount) as dblTaxableAmount,sum(b.dblTaxAmount) as dblTaxAmount "
							+ "FROM tblBillHd a "
							+ "INNER JOIN tblBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
							+ "LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlTaxBuilder.append("and a.strPOSCode='" + posCode + "'  ");
			}
			sqlTaxBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			sqlTaxBuilder.append("group by c.strTaxCode,c.strTaxDesc ");

			List listSqlLiveTaxDtl = objBaseService.funGetList(sqlTaxBuilder, "sql");
			if (listSqlLiveTaxDtl.size() > 0) {
				for (int i = 0; i < listSqlLiveTaxDtl.size(); i++) {
					Object[] obj = (Object[]) listSqlLiveTaxDtl.get(i);
					if (mapTaxDtl.containsKey(obj[0].toString()))// taxCode
					{
						clsPOSTaxCalculationDtls objTaxCalculationDtlBean = mapTaxDtl.get(obj[0].toString());
						objTaxCalculationDtlBean.setTaxableAmount(
								objTaxCalculationDtlBean.getTaxableAmount() + Double.parseDouble(obj[2].toString()));
						objTaxCalculationDtlBean.setTaxAmount(
								objTaxCalculationDtlBean.getTaxAmount() + Double.parseDouble(obj[3].toString()));
					} else {
						clsPOSTaxCalculationDtls objTaxCalculationDtlBean = new clsPOSTaxCalculationDtls();

						objTaxCalculationDtlBean.setTaxCode(obj[0].toString());
						objTaxCalculationDtlBean.setTaxName(obj[1].toString());
						objTaxCalculationDtlBean.setTaxableAmount(Double.parseDouble(obj[2].toString()));
						objTaxCalculationDtlBean.setTaxAmount(Double.parseDouble(obj[3].toString()));

						mapTaxDtl.put(obj[0].toString(), objTaxCalculationDtlBean);

					}
				}
			}
			// Q tax
			sqlTaxBuilder.setLength(0);
			sqlTaxBuilder.append(
					"SELECT b.strTaxCode,c.strTaxDesc,sum(b.dblTaxableAmount) as dblTaxableAmount,sum(b.dblTaxAmount) as dblTaxAmount "
							+ "FROM tblqBillHd a "
							+ "INNER JOIN tblqBillTaxDtl b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
							+ "LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
							+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!posCode.equalsIgnoreCase("All")) {
				sqlTaxBuilder.append("and a.strPOSCode='" + posCode + "'  ");
			}
			sqlTaxBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			sqlTaxBuilder.append("group by c.strTaxCode,c.strTaxDesc ");

			List listSqlQBillTaxDtl = objBaseService.funGetList(sqlTaxBuilder, "sql");
			if (listSqlQBillTaxDtl.size() > 0) {
				for (int i = 0; i < listSqlQBillTaxDtl.size(); i++) {
					Object[] obj = (Object[]) listSqlQBillTaxDtl.get(i);
					if (mapTaxDtl.containsKey(obj[0].toString()))// taxCode
					{
						clsPOSTaxCalculationDtls objTaxCalculationDtlBean = mapTaxDtl.get(obj[0].toString());
						objTaxCalculationDtlBean.setTaxableAmount(
								objTaxCalculationDtlBean.getTaxableAmount() + Double.parseDouble(obj[2].toString()));
						objTaxCalculationDtlBean.setTaxAmount(
								objTaxCalculationDtlBean.getTaxAmount() + Double.parseDouble(obj[3].toString()));
					} else {
						clsPOSTaxCalculationDtls objTaxCalculationDtlBean = new clsPOSTaxCalculationDtls();

						objTaxCalculationDtlBean.setTaxCode(obj[0].toString());
						objTaxCalculationDtlBean.setTaxName(obj[1].toString());
						objTaxCalculationDtlBean.setTaxableAmount(Double.parseDouble(obj[2].toString()));
						objTaxCalculationDtlBean.setTaxAmount(Double.parseDouble(obj[3].toString()));

						mapTaxDtl.put(obj[0].toString(), objTaxCalculationDtlBean);

					}
				}
			}

			for (clsPOSTaxCalculationDtls objTaxDtl : mapTaxDtl.values()) {
				listOfTaxDtl.add(objTaxDtl);
			}
			Comparator<clsPOSTaxCalculationDtls> taxNameComparator = new Comparator<clsPOSTaxCalculationDtls>() {

				@Override
				public int compare(clsPOSTaxCalculationDtls o1, clsPOSTaxCalculationDtls o2) {
					return o1.getTaxName().compareToIgnoreCase(o2.getTaxName());
				}
			};

			Collections.sort(listOfTaxDtl, taxNameComparator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfTaxDtl;
	}

	public List funProcessDiscountWiseReport(String posCode, String fromDate, String toDate, String type,
			String querySubType, String strUserCode, String strShiftNo,String enableShiftYN) {
		List<clsPOSBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();
		StringBuilder sbSqlLiveDisc = new StringBuilder();
		StringBuilder sbSqlQFileDisc = new StringBuilder();

		try {

			if (type.equalsIgnoreCase("Summary")) {
				sbSqlLiveDisc.setLength(0);
				sbSqlLiveDisc.append(
						"select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
								+ " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
								+ " from \n" + " tblbillhd a\n"
								+ " left outer join tblbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
								+ " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
								+ " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
								+ " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
								+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
								+ " and a.strClientCode=b.strClientCode ");

				sbSqlQFileDisc.setLength(0);
				sbSqlQFileDisc.append(
						"select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
								+ " ,IFNULL(c.strReasonName,'') as strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
								+ " from \n" + " tblqbillhd a\n"
								+ " left outer join tblqbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
								+ " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
								+ " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
								+ " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
								+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
								+ " and a.strClientCode=b.strClientCode ");

				if (!posCode.equalsIgnoreCase("All")) {
					sbSqlLiveDisc.append(" and a.strPOSCode='" + posCode + "' ");
					sbSqlQFileDisc.append(" and a.strPOSCode='" + posCode + "' ");
				}

				List listLiveDisc = objBaseService.funGetList(sbSqlLiveDisc, "sql");
				if (listLiveDisc.size() > 0) {
					for (int i = 0; i < listLiveDisc.size(); i++) {
						Object[] obj = (Object[]) listLiveDisc.get(i);
						clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

						objBeanBillItemDtlBean.setStrPosName(obj[0].toString()); // POSName
						objBeanBillItemDtlBean.setDteBillDate(obj[1].toString()); // BillDate
						objBeanBillItemDtlBean.setStrBillNo(obj[2].toString()); // BillNo
						objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[3].toString()));// DiscPer
						objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString())); // DiscAmt
						objBeanBillItemDtlBean.setDblBillDiscPer(Double.parseDouble(obj[5].toString()));// DiscOnAmt
						objBeanBillItemDtlBean.setStrDiscType(obj[6].toString()); // DiscType
						objBeanBillItemDtlBean.setStrDiscValue(obj[7].toString()); // DiscValue
						objBeanBillItemDtlBean.setStrItemCode(obj[8].toString()); // DiscReason
						objBeanBillItemDtlBean.setStrItemName(obj[9].toString()); // DiscRemark
						objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[10].toString())); // SubTotal
						objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[11].toString())); // GrandTotal
						objBeanBillItemDtlBean.setStrSettelmentMode(obj[12].toString()); // UserEdited

						listOfBillItemDtl.add(objBeanBillItemDtlBean);
					}
				}

				List listQfileDisc = objBaseService.funGetList(sbSqlQFileDisc, "sql");
				if (listQfileDisc.size() > 0) {
					for (int i = 0; i < listQfileDisc.size(); i++) {
						Object[] obj = (Object[]) listQfileDisc.get(i);
						clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

						objBeanBillItemDtlBean.setStrPosName(obj[0].toString()); // POSName
						objBeanBillItemDtlBean.setDteBillDate(obj[1].toString()); // BillDate
						objBeanBillItemDtlBean.setStrBillNo(obj[2].toString()); // BillNo
						objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[3].toString()));// DiscPer
						objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[4].toString())); // DiscAmt
						objBeanBillItemDtlBean.setDblBillDiscPer(Double.parseDouble(obj[5].toString()));// DiscOnAmt
						objBeanBillItemDtlBean.setStrDiscType(obj[6].toString()); // DiscType
						objBeanBillItemDtlBean.setStrDiscValue(obj[7].toString()); // DiscValue
						objBeanBillItemDtlBean.setStrItemCode(obj[8].toString()); // DiscReason
						objBeanBillItemDtlBean.setStrItemName(obj[9].toString()); // DiscRemark
						objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[10].toString())); // SubTotal
						objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[11].toString())); // GrandTotal
						objBeanBillItemDtlBean.setStrSettelmentMode(obj[12].toString()); // UserEdited

						listOfBillItemDtl.add(objBeanBillItemDtlBean);
					}
				}
			} else if (type.equalsIgnoreCase("Detail")) {
				StringBuilder sqlModiBuilder = new StringBuilder();
				StringBuilder sqlItemBuilder = new StringBuilder();

				if (type.equalsIgnoreCase("Detail") && querySubType.equalsIgnoreCase("")) {
					sqlItemBuilder.setLength(0);
					sqlItemBuilder.append(
							"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
									+ ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer  "
									+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
									+ ",ifnull(e.strUserEdited,'') " + "from tblbillhd a "
									+ "inner join  tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
									+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
									+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
									+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
									+ "and b.dblDiscountPer>0 ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
						sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
					}

					sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
							+ "order by a.strBillNo,b.strItemCode,b.strItemName");
					List listLiveDisc = objBaseService.funGetList(sqlItemBuilder, "sql");
					if (listLiveDisc.size() > 0) {
						for (int i = 0; i < listLiveDisc.size(); i++) {
							Object[] obj = (Object[]) listLiveDisc.get(i);
							clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

							objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
							objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
							objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
							objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
							objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
							objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
							objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
							objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
							objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
							objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
							objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
							objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); // UserEdited

							listOfBillItemDtl.add(objBeanBillItemDtlBean);
						}
					}

					// live modifiers
					sqlModiBuilder.setLength(0);
					sqlModiBuilder.append(
							"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
									+ ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
									+ " ,ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
									+ ",ifnull(e.strUserEdited,'') " + "from tblbillhd a "
									+ "inner join  tblbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
									+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
									+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
									+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
									+ "and b.dblDiscPer>0 ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
						sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
					}

					sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
							+ "order by a.strBillNo,b.strItemCode,b.strModifierName");
					listLiveDisc = objBaseService.funGetList(sqlModiBuilder, "sql");
					if (listLiveDisc.size() > 0) {
						for (int i = 0; i < listLiveDisc.size(); i++) {
							Object[] obj = (Object[]) listLiveDisc.get(i);
							clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

							objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
							objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
							objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
							objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
							objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
							objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
							objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
							objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
							objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
							objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
							objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
							objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); // UserEdited

							listOfBillItemDtl.add(objBeanBillItemDtlBean);
						}
					}

					// QFile
					sqlItemBuilder.setLength(0);
					sqlItemBuilder.append(
							"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
									+ ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer "
									+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
									+ ",ifnull(e.strUserEdited,'') " + "from tblqbillhd a "
									+ "inner join  tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
									+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
									+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
									+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
									+ "and b.dblDiscountPer>0 ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
						sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
					}

					sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
							+ "order by a.strBillNo,b.strItemCode,b.strItemName");
					List listQDisc = objBaseService.funGetList(sqlItemBuilder, "sql");
					if (listQDisc.size() > 0) {
						for (int i = 0; i < listQDisc.size(); i++) {
							Object[] obj = (Object[]) listQDisc.get(i);
							clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

							objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
							objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
							objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
							objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
							objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
							objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
							objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
							objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
							objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
							objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
							objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
							objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); // UserEdited
							listOfBillItemDtl.add(objBeanBillItemDtlBean);
						}
					}

					// QFile modifiers
					sqlModiBuilder.setLength(0);
					sqlModiBuilder.append(
							"select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
									+ ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
									+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
									+ ",ifnull(e.strUserEdited,'') " + "from tblqbillhd a "
									+ "inner join  tblqbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
									+ "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
									+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
									+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode  "
									+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
									+ "and b.dblDiscPer>0 ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
						sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
					}
					sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
							+ "order by a.strBillNo,b.strItemCode,b.strModifierName");
					listQDisc = objBaseService.funGetList(sqlModiBuilder, "sql");
					if (listQDisc.size() > 0) {
						for (int i = 0; i < listQDisc.size(); i++) {
							Object[] obj = (Object[]) listQDisc.get(i);
							clsPOSBillItemDtlBean objBeanBillItemDtlBean = new clsPOSBillItemDtlBean();

							objBeanBillItemDtlBean.setStrBillNo(obj[0].toString());
							objBeanBillItemDtlBean.setDteBillDate(obj[1].toString());
							objBeanBillItemDtlBean.setStrPosName(obj[2].toString());
							objBeanBillItemDtlBean.setDblSubTotal(Double.parseDouble(obj[3].toString()));
							objBeanBillItemDtlBean.setDblGrandTotal(Double.parseDouble(obj[4].toString()));
							objBeanBillItemDtlBean.setStrItemCode(obj[5].toString());
							objBeanBillItemDtlBean.setStrItemName(obj[6].toString());
							objBeanBillItemDtlBean.setDblQuantity(Double.parseDouble(obj[7].toString()));
							objBeanBillItemDtlBean.setDblAmount(Double.parseDouble(obj[8].toString()));
							objBeanBillItemDtlBean.setDblDiscountAmt(Double.parseDouble(obj[9].toString()));
							objBeanBillItemDtlBean.setDblDiscountPer(Double.parseDouble(obj[10].toString()));
							objBeanBillItemDtlBean.setStrReasonName(obj[12].toString());
							objBeanBillItemDtlBean.setStrDiscountRemark(obj[13].toString());
							objBeanBillItemDtlBean.setStrSettelmentMode(obj[14].toString()); // UserEdited
							listOfBillItemDtl.add(objBeanBillItemDtlBean);
						}
					}
				} else if (querySubType.equalsIgnoreCase("liveGross")) {
					sqlItemBuilder.setLength(0);
					sqlItemBuilder.append("select sum(a.dblSettlementAmt) "
							+ "from tblbillsettlementdtl a,tblbillhd b,tblposmaster c "
							+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strPOSCode=c.strPosCode " + "and date(a.dteBillDate) between '" + fromDate
							+ "' and '" + toDate + "'  ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
					}
					listOfBillItemDtl = objBaseService.funGetList(sqlItemBuilder, "sql");
				} else if (querySubType.equalsIgnoreCase("qGross")) {
					sqlItemBuilder.setLength(0);
					sqlItemBuilder.append("select ifnull(sum(a.dblSettlementAmt),0) "
							+ "from tblqbillsettlementdtl a,tblqbillhd b,tblposmaster c "
							+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
							+ "and b.strPOSCode=c.strPosCode " + "and date(a.dteBillDate) between '" + fromDate
							+ "' and '" + toDate + "'  ");
					if (!posCode.equalsIgnoreCase("All")) {
						sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
					}
					listOfBillItemDtl = objBaseService.funGetList(sqlItemBuilder, "sql");
				}

			} else {
				StringBuilder sbSqlLive = new StringBuilder();
				StringBuilder sbSqlQFile = new StringBuilder();
				StringBuilder sbSqlFilters = new StringBuilder();
				StringBuilder sqlModLive = new StringBuilder();
				StringBuilder sqlModQFile = new StringBuilder();

				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);
				sqlModLive.setLength(0);
				sqlModQFile.setLength(0);

				sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equalsIgnoreCase("All")) {
					sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
				}
				if(enableShiftYN.equalsIgnoreCase("Y"))
				{	
				sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
				}
				sbSqlFilters.append(" Group BY c.strGroupCode ");

				if (querySubType.equalsIgnoreCase("live")) {
					sbSqlQFile.setLength(0);
					sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
							+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) " + ",f.strPosName, '" + strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
							+ "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
							+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
							+ ",tblitemmaster e,tblposmaster f " + "where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and a.strPOSCode=f.strPOSCode  "
							+ " and a.strClientCode=b.strClientCode " + "and b.strItemCode=e.strItemCode "
							+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");
					sbSqlQFile.append(sbSqlFilters);
					listOfBillItemDtl = objBaseService.funGetList(sbSqlQFile, "sql");
				} else if (querySubType.equalsIgnoreCase("qFile")) {
					sbSqlQFile.setLength(0);
					sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
							+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) " + ",f.strPosName, '" + strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
							+ " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
							+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
							+ ",tblitemmaster e,tblposmaster f " + "where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and a.strPOSCode=f.strPOSCode  "
							+ " and a.strClientCode=b.strClientCode   " + "and b.strItemCode=e.strItemCode "
							+ "and c.strGroupCode=d.strGroupCode " + " and d.strSubGroupCode=e.strSubGroupCode ");
					sbSqlQFile.append(sbSqlFilters);
					listOfBillItemDtl = objBaseService.funGetList(sbSqlQFile, "sql");
				} else if (querySubType.equalsIgnoreCase("modLive")) {
					sqlModLive.append("select c.strGroupCode,c.strGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
							+ " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c " + " where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and a.strPOSCode=f.strPosCode  "
							+ " and a.strClientCode=b.strClientCode  " + " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode " + " and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 ");
					sqlModLive.append(sbSqlFilters);
					listOfBillItemDtl = objBaseService.funGetList(sqlModLive, "sql");
				} else if (querySubType.equalsIgnoreCase("modQFile")) {
					sqlModQFile.append("select c.strGroupCode,c.strGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
							+ " sum(b.dblAmount)-sum(b.dblDiscAmt) "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c " + " where a.strBillNo=b.strBillNo "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) " + " and a.strPOSCode=f.strPosCode   "
							+ " and a.strClientCode=b.strClientCode   " + " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode " + " and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 ");
					sqlModQFile.append(sbSqlFilters);
					listOfBillItemDtl = objBaseService.funGetList(sqlModQFile, "sql");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfBillItemDtl;

	}
	public LinkedHashMap funProcessItemGroupWiseDayWiseSalesSummary(String withDiscount, String fromDate, String toDate,
			String strOperationType, String strSettlementCode, String strPOSCode, String strPOSName, String groupCode,
			String groupName) {
		StringBuilder sbSql = new StringBuilder();
		List listColDataArr = new ArrayList();
		List listGroupArr = new ArrayList();
		List listTaxArr = new ArrayList();
		List listSettleArr = new ArrayList();

		List listColHeaderArr = new ArrayList();
		List listDateArr = new ArrayList();
		LinkedHashMap mapRet = new LinkedHashMap();

		Map map = new HashMap();

		listColHeaderArr.add("DATE");
		listColHeaderArr.add("POS");
		int colCount = 2;
		try {
			// Q Date and POS
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
					+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblqbilldtl d ,tblitemmaster e,tblsubgrouphd f,tblgrouphd g  "
					+ "where  a.strBillNo=b.strBillNo  and a.strBillNo=d.strBillNo and b.strBillNo=d.strBillNo "
					+ "and d.strItemCode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=g.strGroupCode  "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strSettlementCode=c.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and f.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) " + "order by date(a.dteBillDate); ");
			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {

					listDateArr.add((String) listSql.get(i));

				}

			}

			// Live Bill Date
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
					+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblbilldtl d ,tblitemmaster e,tblsubgrouphd f,tblgrouphd g "
					+ "where  a.strBillNo=b.strBillNo  and a.strBillNo=d.strBillNo and b.strBillNo=d.strBillNo "
					+ "and d.strItemCode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=g.strGroupCode "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strSettlementCode=c.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and f.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) " + "order by date(a.dteBillDate); ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					listDateArr.add(obj.toString());

				}

			}

			// jColDataArr.add("DATE", jDateArr);

			// Add Group Column
			StringBuilder sqlGroups = new StringBuilder();

			// live groups
			sqlGroups.setLength(0);
			sqlGroups.append("select  g.strGroupCode,g.strGroupName " + "from tblbillhd a,tblbilldtl b,tblitemmaster e "
					+ ",tblsubgrouphd f ,tblgrouphd g  " + "where a.strBillNo=b.strBillNo "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strItemCode=e.strItemCode "
					+ "and e.strSubGroupCode=f.strSubGroupCode " + "and f.strGroupCode=g.strGroupCode "
					+ "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sqlGroups.append(" and g.strGroupCode='" + groupName + "' ");
			}

			sqlGroups.append(" GROUP BY g.strGroupCode,g.strGroupName ");
			listSql = objBaseService.funGetList(sqlGroups, "sql");
			Map<String, String> mapOfGroups = new HashMap<String, String>();
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					mapOfGroups.put(obj[1].toString(), obj[1].toString());
					listGroupArr.add(obj[1].toString());
					listColHeaderArr.add(obj[1].toString());
					colCount++;
				}
			}

			// q groups
			sqlGroups.setLength(0);
			sqlGroups.append("select  g.strGroupCode,g.strGroupName "
					+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster e " + ",tblsubgrouphd f ,tblgrouphd g  "
					+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and b.strItemCode=e.strItemCode " + "and e.strSubGroupCode=f.strSubGroupCode "
					+ "and f.strGroupCode=g.strGroupCode " + "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '"
					+ fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sqlGroups.append(" and g.strGroupCode='" + groupName + "' ");
			}

			sqlGroups.append(" GROUP BY g.strGroupCode,g.strGroupName ");
			listSql = objBaseService.funGetList(sqlGroups, "sql");

			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					if (mapOfGroups.containsKey(obj[1].toString())) {

					} else {
						listGroupArr.add(obj[1].toString());
						listColHeaderArr.add(obj[1].toString());
						colCount++;
					}
				}
			}

			// fill Live settlement whose amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "AND b.strSettlementCode=c.strSettelmentCode  " + "and b.dblSettlementAmt>0 "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			Map mapOfSettlement = new HashMap();
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					mapOfSettlement.put(obj.toString(), obj.toString());
					listColHeaderArr.add(obj.toString());
					listSettleArr.add(obj.toString());
					colCount++;
				}
			}

			// fill Q settlement whoes amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "AND b.strSettlementCode=c.strSettelmentCode  " + "and b.dblSettlementAmt>0 "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY c.strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					if (mapOfSettlement.containsKey(obj.toString())) {

					} else {
						listColHeaderArr.add(obj.toString());
						listSettleArr.add(obj.toString());
						colCount++;
					}
				}
			}
			// Tax Column
			String taxCalType = "";
			StringBuilder sqlTax = new StringBuilder();
			sqlTax.setLength(0);
			// live tax
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");
			Map mapOfTax = new HashMap();
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					mapOfTax.put(obj[1].toString(), obj[1].toString());
					taxCalType = obj[2].toString();
					listColHeaderArr.add(obj[1].toString());
					listTaxArr.add(obj[1].toString());
					colCount++;
				}
			}

			sqlTax.setLength(0);
			// Q tax
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblqbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					if (mapOfTax.containsKey(obj[1].toString())) {

					} else {
						taxCalType = obj[2].toString();
						listColHeaderArr.add(obj[1].toString());
						listTaxArr.add(obj[1].toString());
						colCount++;
					}
				}
			}

			// Grand Total
			listColHeaderArr.add("GRAND Total");
			StringBuilder sqlGrandTotal = new StringBuilder();
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(b.dblSettlementAmt) "
					+ "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c " + "WHERE a.strBillNo=b.strBillNo  "
					+ "AND b.strSettlementCode=c.strSettelmentCode  " + "and b.dblSettlementAmt>0 "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlGrandTotal.append("GROUP BY DATE(a.dteBillDate) " + "ORDER BY DATE(a.dteBillDate); ");

			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			int size = listSql.size();

			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				List jRowArr = new ArrayList();

				jRowArr.add(listDateArr.get(tblRow));
				jRowArr.add(strPOSName);
				for (int i = 2; i <= colCount; i++) {
					jRowArr.add(i, 0.00);
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);

						}

					}
					map.put(tblRow, jRowArr);
				}

			}

			// Live
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(b.dblSettlementAmt) "
					+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + "WHERE a.strBillNo=b.strBillNo  "
					+ "AND b.strSettlementCode=c.strSettelmentCode  " + "and b.dblSettlementAmt>0 "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGrandTotal.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlGrandTotal.append("GROUP BY DATE(a.dteBillDate) " + "ORDER BY DATE(a.dteBillDate); ");
			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			size = listSql.size();

			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <= colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);

						}

					}
					map.put(tblRow, jRowArr);
				}

			}

			String columnForSalesAmount = "sum(b.dblAmount) ";
			if (withDiscount.equalsIgnoreCase("Y")) {
				columnForSalesAmount = "sum(b.dblAmount) ";
			} else {
				columnForSalesAmount = "sum(b.dblAmount)-sum(b.dblDiscountAmt) ";
			}

			String columnForModiSalesAmount = "SUM(h.dblAmount) ";
			if (withDiscount.equalsIgnoreCase("Y")) {
				columnForModiSalesAmount = "SUM(h.dblAmount) ";
			} else {
				columnForModiSalesAmount = "SUM(h.dblAmount)-sum(h.dblDiscAmt) ";
			}

			// fill Q data group
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForSalesAmount
					+ ",sum(b.dblDiscountAmt),sum(b.dblAmount)-sum(b.dblDiscountAmt) "
					+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster e " + ",tblsubgrouphd f ,tblgrouphd g  "
					+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and b.strItemCode=e.strItemCode " + "and e.strSubGroupCode=f.strSubGroupCode "
					+ "and f.strGroupCode=g.strGroupCode " + "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '"
					+ fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and g.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName; ");

			listSql = objBaseService.funGetList(sbSql, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}
			// Q Modifier Group data
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForModiSalesAmount
					+ ",sum(h.dblDiscAmt),sum(h.dblAmount)-sum(h.dblDiscAmt) "
					+ "FROM tblqbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblqbillmodifierdtl h "
					+ "WHERE a.strBillNo=h.strBillNo  " + "and date(a.dteBillDate)=date(h.dteBillDate) "
					+ "AND e.strSubGroupCode=f.strSubGroupCode  " + "AND f.strGroupCode=g.strGroupCode  "
					+ "and h.dblAmount>0 " + "AND a.strBillNo=h.strBillNo  "
					+ "AND e.strItemCode=LEFT(h.strItemCode,7) " + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate
					+ "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and g.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName ");

			listSql = objBaseService.funGetList(sbSql, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			// Settlement Data
			StringBuilder sqlTransRecords = new StringBuilder();
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode
					+ "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
					+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c " + "where a.strBillNo=b.strBillNo "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strSettlementCode=c.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[2].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[3].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[3].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}

							}
						} else {
							continue;
						}
					}

				}

			}

			// Tax Data
			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
					+ "from " + "tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c " + "where a.strBillNo=b.strBillNo "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strTaxCode=c.strTaxCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode " + "order by date(a.dteBillDate),b.strTaxCode; ");

			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[2].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// Live Group Data
			sqlGroups.setLength(0);
			sqlGroups.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName," + columnForSalesAmount
					+ ",sum(b.dblDiscountAmt),sum(b.dblAmount)-sum(b.dblDiscountAmt) "
					+ "from tblbillhd a,tblbilldtl b,tblitemmaster e " + ",tblsubgrouphd f ,tblgrouphd g  "
					+ "where a.strBillNo=b.strBillNo " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and b.strItemCode=e.strItemCode " + "and e.strSubGroupCode=f.strSubGroupCode "
					+ "and f.strGroupCode=g.strGroupCode " + "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '"
					+ fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlGroups.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName  ");

			listSql = objBaseService.funGetList(sqlGroups, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[2].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// Live Modifier Group Data
			sqlGroups.setLength(0);
			sqlGroups.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),g.strGroupName,"
					+ columnForModiSalesAmount + ",sum(h.dblDiscAmt),sum(h.dblAmount)-sum(h.dblDiscAmt) "
					+ "FROM tblbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblbillmodifierdtl h "
					+ "WHERE a.strBillNo=h.strBillNo  " + "and date(a.dteBillDate)=date(h.dteBillDate) "
					+ "AND e.strSubGroupCode=f.strSubGroupCode  " + "AND f.strGroupCode=g.strGroupCode  "
					+ "and h.dblAmount>0 " + "AND a.strBillNo=h.strBillNo  "
					+ "AND e.strItemCode=LEFT(h.strItemCode,7) " + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate
					+ "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlGroups.append("GROUP BY DATE(a.dteBillDate),g.strGroupCode,g.strGroupName ");
			listSql = objBaseService.funGetList(sqlGroups, "sql");

			String strAmt = "";

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[2].toString()));
										map.put(tblRow, jRowArr);
									} else {

										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTransRecords.setLength(0);
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode
					+ "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
					+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + "where a.strBillNo=b.strBillNo "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strSettlementCode=c.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[2].toString()))

								{

									if (0.00 == (double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[3].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[3].toString())
												+ (double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
					+ "from " + "tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + "where a.strBillNo=b.strBillNo "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and b.strTaxCode=c.strTaxCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode " + "order by date(a.dteBillDate),b.strTaxCode; ");

			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{

									if (0.00 == (double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(String.valueOf(value));
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			colCount++;
			listColHeaderArr.add("Discount".toUpperCase());
//			colCount++;
			
			
			sbSql.setLength(0);
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblDiscountAmt),sum(a.dblRoundOff) "
			    + "FROM tblqbillhd a  "
			    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");	  
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
		    }
		   
		    sbSql.append("GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
		
			listSql = objBaseService.funGetList(sbSql, "sql");
			Map mapOfDiscount = new HashMap();
			
			String discount = "Discount";
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <=colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
							
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Discount")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							mapOfDiscount.put(discount, discount);
							jRowArr.add(i, 0.00);
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
//							colCount++;
						}
						
						

					}

				}
				map.put(tblRow, jRowArr);

			}

			
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblDiscountAmt),sum(a.dblRoundOff) "
			    + "FROM tblbillhd a "
			    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");	 
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
		    }
		   
		    sbSql.append("GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
			
			
			listSql = objBaseService.funGetList(sbSql, "sql");
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <= colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
							
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Discount")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							jRowArr.add(i, 0.00);
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
//							if (mapOfDiscount.containsKey(discount)) {
//
//							} else {
//								colCount++;
//							}
						}
						
						

					}

				}
				map.put(tblRow, jRowArr);

			}
			
			
			listColHeaderArr.add("Round Off".toUpperCase());
			colCount++;
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblDiscountAmt),sum(a.dblRoundOff) "
			    + "FROM tblqbillhd a "
			    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");	 
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
		    }
		   
		    sbSql.append("GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
			
			listSql = objBaseService.funGetList(sbSql, "sql");
			
			Map mapOfRoundOff = new HashMap();
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <= colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Round Off")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							jRowArr.add(i, 0.00);
							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
							mapOfRoundOff.put("Round Off", "Round Off");
//							colCount++;
						}
						

					}

				}
				map.put(tblRow, jRowArr);

			}

			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblDiscountAmt),sum(a.dblRoundOff) "
			    + "FROM tblbillhd a "
			    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");	 
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
		    }
		   
		    sbSql.append("GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
			
			listSql = objBaseService.funGetList(sbSql, "sql");
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {

				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <= colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
							
					
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Round Off")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							jRowArr.add(i, 0.00);
							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
//							if (mapOfRoundOff.containsKey("Round Off")) {
//
//							} else {
//								colCount++;
//							}
						}
						

					}

				}
				map.put(tblRow, jRowArr);

			}

			// fill Q data group
			listColHeaderArr.add("Net Total".toUpperCase());
			colCount++;
			Double totNetTotal = 0.0;
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblGrandTotal),sum(a.dblSubTotal)-sum(a.dblDiscountAmt)"
				    + "FROM tblqbillhd a "
				    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			    if (!strOperationType.equalsIgnoreCase("All"))
			    {
			    	sbSql.append( "and a.strOperationType='" + strOperationType + "' ");
			    }
			    if (!strPOSCode.equalsIgnoreCase("All"))
			    {
			    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			    }

			    sbSql.append( "GROUP BY DATE(a.dteBillDate) "
				    + "ORDER BY DATE(a.dteBillDate); ");
		
			listSql = objBaseService.funGetList(sbSql, "sql");
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				Double netTotal = 0.0;
				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <=colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Net Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							if (netTotal == 0.0) {
								jRowArr.add(i, 0.00);
							}
							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							netTotal = value;
							totNetTotal = netTotal;
							jRowArr.remove(i);
							jRowArr.add(i, value);

						}

					}

				}
				map.put(tblRow, jRowArr);

			}

			// fill Live data 
			sbSql.setLength(0);
			sbSql.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblGrandTotal),sum(a.dblSubTotal)-sum(a.dblDiscountAmt)"
				    + "FROM tblbillhd a "
				    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			    if (!strOperationType.equalsIgnoreCase("All"))
			    {
			    	sbSql.append( "and a.strOperationType='" + strOperationType + "' ");
			    }
			    if (!strPOSCode.equalsIgnoreCase("All"))
			    {
			    	sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			    }

			    sbSql.append( "GROUP BY DATE(a.dteBillDate) "
				    + "ORDER BY DATE(a.dteBillDate); ");
		
			listSql = objBaseService.funGetList(sbSql, "sql");
			size = listSql.size();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				Double netTotal = 0.0;
				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i <=colCount; i++) {
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Net Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							if (netTotal == 0.0) {
								jRowArr.add(i, 0.00);
							}
							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							netTotal = value;
							totNetTotal = netTotal;
							jRowArr.remove(i);
							jRowArr.add(i, value);

						}

					}

				}
				map.put(tblRow, jRowArr);

			}

			List listData = new ArrayList();
			mapRet.put("Col Header", listColHeaderArr);
			mapRet.put("Col Count", colCount);
			mapRet.put("Row Count", listDateArr.size());
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				List jArr = new ArrayList();
				jArr = (List) map.get(tblRow);
				listData.add(jArr);
				mapRet.put("listData", listData);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return mapRet;

	}

	public LinkedHashMap funProcessDayWiseSalesSummary(String withDiscount, String fromDate, String toDate,
			String strOperationType, String strSettlementCode, String strPOSCode, String strPOSName, String groupCode,
			String groupName) {
		StringBuilder sbSql = new StringBuilder();
		List listColDataArr = new ArrayList();
		List listGroupArr = new ArrayList();
		List listTaxArr = new ArrayList();
		List listSettleArr = new ArrayList();

		List listColHeaderArr = new ArrayList();
		List listDateArr = new ArrayList();
		LinkedHashMap jOBjRet = new LinkedHashMap();

		Map map = new HashMap();

		listColHeaderArr.add("DATE");
		listColHeaderArr.add("POS");
		int colCount = 3;
		try {
			// Q Date and POS
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
					+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
					+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ "and a.strBillNo=b.strBillNo " + "and b.strSettlementCode=c.strSettelmentCode ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) " + "order by date(a.dteBillDate); ");
			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {

					listDateArr.add((String) listSql.get(i));

				}

			}

			// Live Bill Date
			sbSql.setLength(0);
			sbSql.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y') "
					+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
					+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
					+ "and a.strBillNo=b.strBillNo " + "and b.strSettlementCode=c.strSettelmentCode ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("group by date(a.dteBillDate) " + "order by date(a.dteBillDate); ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);

					listDateArr.add(obj.toString());

				}

			}

			// jColDataArr.add("DATE", jDateArr);

			// Add Group Column
			StringBuilder sqlGroups = new StringBuilder();

			// fill Live settlement whose amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "AND b.strSettlementCode=c.strSettelmentCode  "
					+ "and b.dblSettlementAmt>0 " + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			Map mapOfSettlement = new HashMap();
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					mapOfSettlement.put(obj.toString(), obj.toString());
					listColHeaderArr.add(obj.toString());
					listSettleArr.add(obj.toString());
					colCount++;
				}
			}

			// fill Q settlement whoes amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "AND b.strSettlementCode=c.strSettelmentCode  "
					+ "and b.dblSettlementAmt>0 " + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY c.strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					if (mapOfSettlement.containsKey(obj.toString())) {

					} else {
						listColHeaderArr.add(obj.toString());
						listSettleArr.add(obj.toString());
						colCount++;
					}
				}
			}
			// Tax Column
			String taxCalType = "";
			StringBuilder sqlTax = new StringBuilder();
			sqlTax.setLength(0);
			// live tax
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");
			Map mapOfTax = new HashMap();
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					mapOfTax.put(obj[1].toString(), obj[1].toString());
					taxCalType = obj[2].toString();
					listColHeaderArr.add(obj[1].toString());
					listTaxArr.add(obj[1].toString());
					colCount++;
				}
			}

			sqlTax.setLength(0);
			// Q tax
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblqbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					if (mapOfTax.containsKey(obj[1].toString())) {

					} else {
						taxCalType = obj[2].toString();
						listColHeaderArr.add(obj[1].toString());
						listTaxArr.add(obj[1].toString());
						colCount++;
					}
				}
			}

			// Grand Total
			listColHeaderArr.add("GRAND Total");
			colCount++;
			listColHeaderArr.add("Net Total");
			StringBuilder sqlGrandTotal = new StringBuilder();
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblGrandTotal),sum(a.dblSubTotal)-sum(a.dblDiscountAmt)"
					    + "FROM tblqbillhd a "
					    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		    if (!strOperationType.equalsIgnoreCase("All"))
		    {
		    	sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
		    }
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sqlGrandTotal.append( " and a.strPOSCode='" + strPOSCode + "' ");
		    }

		    sqlGrandTotal.append( "GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
			
			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			int size = listSql.size();
			Map mapOfGrandTotal = new HashMap();
			Map mapOfNetTotal = new HashMap();
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				List jRowArr = new ArrayList();

				jRowArr.add(listDateArr.get(tblRow));
				jRowArr.add(strPOSName);
				for (int i = 2; i < colCount; i++) {
					jRowArr.add(i, 0.00);
					for (int j = 0; j < size; j++) {
						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
							mapOfGrandTotal.put("GRAND Total", "GRAND Total");
							
						}
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Net Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
							mapOfNetTotal.put("Net Total", "Net Total");

						}

					}
					map.put(tblRow, jRowArr);
				}

			}

			// Live
			sqlGrandTotal.setLength(0);
			sqlGrandTotal.append("SELECT DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),sum(a.dblGrandTotal),sum(a.dblSubTotal)-sum(a.dblDiscountAmt)"
					    + "FROM tblbillhd a "
					    + "WHERE date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		    if (!strOperationType.equalsIgnoreCase("All"))
		    {
		    	sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
		    }
		    if (!strPOSCode.equalsIgnoreCase("All"))
		    {
		    	sqlGrandTotal.append( " and a.strPOSCode='" + strPOSCode + "' ");
		    }

		    sqlGrandTotal.append( "GROUP BY DATE(a.dteBillDate) "
			    + "ORDER BY DATE(a.dteBillDate); ");
			
			listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
			size = listSql.size();

			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				List jRowArr = new ArrayList();
				jRowArr = (List) map.get(tblRow);
				for (int i = 3; i < colCount; i++) {

					for (int j = 0; j < size; j++) {

						Object[] obj = (Object[]) listSql.get(j);
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {

							Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);
//							if (mapOfGrandTotal.containsKey("GRAND Total")) {
//
//							} else {
//								colCount++;
//							}
						}
						if (listColHeaderArr.get(i).toString().equalsIgnoreCase("Net Total")
								&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {

							Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
							jRowArr.remove(i);
							jRowArr.add(i, value);

						}

					}
					map.put(tblRow, jRowArr);
				}
			}

			// Settlement Data
			StringBuilder sqlTransRecords = new StringBuilder();
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode
					+ "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
					+ "from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c " + "where a.strBillNo=b.strBillNo "
					+ "and b.strSettlementCode=c.strSettelmentCode " + "and date(a.dteBillDate) between '" + fromDate
					+ "' and '" + toDate + "' ");

			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[2].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[3].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[3].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}

							}
						} else {
							continue;
						}
					}

				}

			}

			// Tax Data
			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
					+ "from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c,tblqbillsettlementdtl d,tblsettelmenthd e  "
					+ "where a.strBillNo=b.strBillNo " + "and b.strTaxCode=c.strTaxCode "
					+ "and a.strBillNo=d.strBillNo " + "and d.strSettlementCode=e.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All"))
		    {
				sqlTax.append(" and e.strSettelmentCode='" + strSettlementCode + "' ");
		    }
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode " + "order by date(a.dteBillDate),b.strTaxCode; ");

			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == (Double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[2].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTransRecords.setLength(0);
			sqlTransRecords.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),'" + strPOSCode
					+ "',c.strSettelmentDesc,sum(b.dblSettlementAmt) "
					+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + "where a.strBillNo=b.strBillNo "
					+ "and b.strSettlementCode=c.strSettelmentCode " + "and date(a.dteBillDate) between '" + fromDate
					+ "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by date(a.dteBillDate),c.strSettelmentDesc "
					+ "order by date(a.dteBillDate),c.strSettelmentDesc; ");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[2].toString()))

								{

									if (0.00 == (double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, Double.parseDouble(obj[3].toString()));
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[3].toString())
												+ (double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			sqlTax.setLength(0);
			sqlTax.append("select DATE_FORMAT(DATE(a.dteBillDate),'%d-%m-%Y'),c.strTaxDesc,sum(b.dblTaxAmount) "
					+ "from " + "tblbillhd a,tblbilltaxdtl b,tbltaxhd c,tblbillsettlementdtl d,tblsettelmenthd e   "
					+ "where a.strBillNo=b.strBillNo " + "and b.strTaxCode=c.strTaxCode "
					+ "and a.strBillNo=d.strBillNo " + "and d.strSettlementCode=e.strSettelmentCode "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All"))
		    {
				sqlTax.append(" and e.strSettelmentCode='" + strSettlementCode + "' ");
		    }
			sqlTax.append("group by date(a.dteBillDate),b.strTaxCode " + "order by date(a.dteBillDate),b.strTaxCode; ");

			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
						List jRowArr = new ArrayList();
						jRowArr = (List) map.get(tblRow);
						if (listDateArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 2; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{

									if (0.00 == (double) jRowArr.get(tblCol)) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ (Double) jRowArr.get(tblCol);
										jRowArr.remove(tblCol);
										jRowArr.add(String.valueOf(value));
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			List listData = new ArrayList();
			jOBjRet.put("Col Header", listColHeaderArr);
			jOBjRet.put("Col Count", colCount);
			jOBjRet.put("Row Count", listDateArr.size());
			for (int tblRow = 0; tblRow < listDateArr.size(); tblRow++) {
				List jArr = new ArrayList();
				jArr = (List) map.get(tblRow);
				listData.add(jArr);
				jOBjRet.put("listData", listData);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jOBjRet;

	}

	public LinkedHashMap funGetBillWiseSettlementSalesSummary(String fromDate, String toDate, String viewBy,
			String strOperationType, String strSettlementCode, String strPOSCode, String strPOSName, String groupName) {

		StringBuilder sbSql = new StringBuilder();
		List listColDataArr = new ArrayList();
		List listGroupArr = new ArrayList();
		List listTaxArr = new ArrayList();
		List listSettleArr = new ArrayList();
		List listBillNoArr = new ArrayList();
		List listColHeaderArr = new ArrayList();
		List listDateArr = new ArrayList();
		LinkedHashMap mapRet = new LinkedHashMap();
		StringBuilder sqlGroups = new StringBuilder();
		Map map = new HashMap();
		listColHeaderArr.add("Bill NO");
		listColHeaderArr.add("DATE");
		listColHeaderArr.add("POS");
		int colCount = 5;
		try {

			// Q Bill No and POS
			sbSql.setLength(0);
			sbSql.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as date " + "from "
					+ "tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblqbilldtl d ,tblitemmaster e,tblsubgrouphd f,tblgrouphd g "
					+ "where  a.strBillNo=b.strBillNo  and a.strBillNo=d.strBillNo and b.strBillNo=d.strBillNo "
					+ "and d.strItemCode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=g.strGroupCode  "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strClientCode=b.strClientCode  "
					+ "and b.strSettlementCode=c.strSettelmentCode " + "and date(a.dteBillDate) between '" + fromDate
					+ "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and f.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("group by a.strClientCode,a.strBillNo "
					+ "order by a.strClientCode,a.strPOSCOde,a.strBillNo,a.dteBillDate; ");

			List listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);

					listBillNoArr.add(obj[0].toString());
					listDateArr.add(obj[1].toString());

				}

			}

			// Live Bill No and POS
			sbSql.setLength(0);
			sbSql.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as date " + "from "
					+ "tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblbilldtl d ,tblitemmaster e,tblsubgrouphd f,tblgrouphd g  "
					+ "where  a.strBillNo=b.strBillNo  and a.strBillNo=d.strBillNo and b.strBillNo=d.strBillNo "
					+ "and d.strItemCode=e.strItemCode and e.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=g.strGroupCode "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strClientCode=b.strClientCode "
					+ "and b.strSettlementCode=c.strSettelmentCode " + "and date(a.dteBillDate) between '" + fromDate
					+ "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			if (!groupName.equalsIgnoreCase("All")) {
				sbSql.append(" and f.strGroupCode='" + groupName + "' ");
			}
			sbSql.append("group by a.strClientCode,a.strBillNo "
					+ "order by a.strClientCode,a.strPOSCOde,a.strBillNo,a.dteBillDate; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);

					listBillNoArr.add(obj[0].toString());
					listDateArr.add(obj[1].toString());

				}

			}
			// jColDataArr.add("BILL NO", jBillNoArr);
			//
			// jColDataArr.add("DATE", jDateArr);

			// Add Group Column

			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				sqlGroups.setLength(0);
				sqlGroups.append("select a.strGroupName from tblgrouphd a ");
				if (!groupName.equalsIgnoreCase("All")) {
					sqlGroups.append("where a.strGroupCode='" + groupName + "' ");
				}

				listSql = objBaseService.funGetList(sqlGroups, "sql");

				if (listSql.size() > 0) {

					for (int i = 0; i < listSql.size(); i++) {
						Object obj = (Object) listSql.get(i);

						listGroupArr.add(obj.toString());
						listColHeaderArr.add(obj.toString());
						colCount++;
					}
				}
			}
			// fill Live settlement whose amt>0
			sbSql.setLength(0);
			Map mapOfSettlement = new HashMap();
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and a.strClientCode=b.strClientCode   " + "AND b.strSettlementCode=c.strSettelmentCode   "
					+ "and b.dblSettlementAmt>0 " + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY c.strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					mapOfSettlement.put(obj.toString(), obj.toString());
					listColHeaderArr.add(obj.toString());
					listSettleArr.add(obj.toString());
					colCount++;
				}
			}

			// fill Q settlement whoes amt>0
			sbSql.setLength(0);
			sbSql.append("SELECT c.strSettelmentDesc " + "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
					+ "WHERE a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and a.strClientCode=b.strClientCode   " + "AND b.strSettlementCode=c.strSettelmentCode "
					+ "and b.dblSettlementAmt>0 " + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sbSql.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sbSql.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sbSql.append("GROUP BY strSettelmentDesc " + "ORDER BY c.strSettelmentDesc; ");
			listSql = objBaseService.funGetList(sbSql, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object obj = (Object) listSql.get(i);
					if (!mapOfSettlement.containsKey(obj.toString())) {
						listColHeaderArr.add(obj.toString());
						listSettleArr.add(obj.toString());
						colCount++;
					}
				}
			}
			// Tax Column
			String taxCalType = "";
			Map mapOfTax = new HashMap();
			StringBuilder sqlTax = new StringBuilder();
			// Live Tax
			sqlTax.setLength(0);
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					mapOfTax.put(obj[1].toString(), obj[1].toString());
					listColHeaderArr.add(obj[1].toString());
					listTaxArr.add(obj[1].toString());
					taxCalType = obj[2].toString();
					colCount++;
				}
			}

			sqlTax.setLength(0);
			sqlTax.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
					+ "from tbltaxhd a,tblqbilltaxdtl b " + "where a.strTaxCode=b.strTaxCode "
					+ "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			listSql = objBaseService.funGetList(sqlTax, "sql");
			if (listSql.size() > 0) {

				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					if (!mapOfTax.containsKey(obj[1].toString())) {
						listColHeaderArr.add(obj[1].toString());
						listTaxArr.add(obj[1].toString());
						taxCalType = obj[2].toString();
						colCount++;
					}
				}
			}

			listColHeaderArr.add("GRAND Total");

			StringBuilder sqlGrandTotal = new StringBuilder();
			// Q grand total
			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				listColHeaderArr.add("CARD NO");
				sqlGrandTotal.setLength(0);
				sqlGrandTotal.append("SELECT a.strBillNo,sum(b.dblSettlementAmt),b.strCardName  "
						+ "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
						+ "WHERE a.strBillNo=b.strBillNo    " + "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and a.strClientCode=b.strClientCode    " + "AND b.strSettlementCode=c.strSettelmentCode  "
						+ "AND a.strPOSCode=d.strPosCode  " + "and b.dblSettlementAmt>0 "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(sqlGrandTotal + " and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlGrandTotal
						.append("GROUP BY a.strClientCode,a.strBillNo " + "ORDER BY a.strClientCode,a.strBillNo; ");

				listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
				int size = listSql.size();

				for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
					List jRowArr = new ArrayList();
					jRowArr.add(listBillNoArr.get(tblRow));
					jRowArr.add(listDateArr.get(tblRow));
					jRowArr.add(strPOSName);
					for (int i = 3; i < colCount; i++) {
						jRowArr.add(i, 0.00);
						for (int j = 0; j < size; j++) {
							Object[] obj = (Object[]) listSql.get(j);
							if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								jRowArr.remove(i);
								jRowArr.add(i, obj[1].toString());
							} else if (listColHeaderArr.get(i).toString().equalsIgnoreCase("CARD NO")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								jRowArr.remove(i);
								jRowArr.add(i, obj[2].toString());
							}
						}

					}

					map.put(tblRow, jRowArr);
				}
			} else {
				sqlGrandTotal.setLength(0);
				sqlGrandTotal.append("SELECT a.strBillNo,sum(b.dblSettlementAmt) "
						+ "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
						+ "WHERE a.strBillNo=b.strBillNo  " + "AND b.strSettlementCode=c.strSettelmentCode  "
						+ "AND a.strPOSCode=d.strPosCode  " + "and b.dblSettlementAmt>0 "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlGrandTotal.append("GROUP BY a.strBillNo " + "ORDER BY a.strBillNo;");

				listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
				int size = listSql.size();

				for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
					List jRowArr = new ArrayList();
					jRowArr.add(listBillNoArr.get(tblRow));
					jRowArr.add(listDateArr.get(tblRow));
					jRowArr.add(strPOSName);
					for (int i = 3; i < colCount - 1; i++) {
						jRowArr.add(i, 0.00);
						for (int j = 0; j < size; j++) {
							Object[] obj = (Object[]) listSql.get(j);
							if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								jRowArr.remove(i);
								jRowArr.add(i, obj[1].toString());
							}
						}

					}

					map.put(tblRow, jRowArr);
				}
			}

			// Live
			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				sqlGrandTotal.setLength(0);
				sqlGrandTotal.append("SELECT a.strBillNo,sum(b.dblSettlementAmt),b.strCardName  "
						+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
						+ "WHERE a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and a.strClientCode=b.strClientCode   " + "AND b.strSettlementCode=c.strSettelmentCode  "
						+ "AND a.strPOSCode=d.strPosCode  " + "and b.dblSettlementAmt>0 "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlGrandTotal
						.append("GROUP BY a.strClientCode,a.strBillNo " + "ORDER BY a.strClientCode,a.strBillNo; ");

				listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
				int size = listSql.size();

				for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {

					List jRowArr = (List) map.get(tblRow);
					for (int i = 3; i < colCount; i++) {

						for (int j = 0; j < size; j++) {
							Object[] obj = (Object[]) listSql.get(j);
							if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								Double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
								jRowArr.remove(i);
								jRowArr.add(i, value);
								map.put(tblRow, jRowArr);
							} else if (listColHeaderArr.get(i).toString().equalsIgnoreCase("CARD NO")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								if (!obj[2].toString().equalsIgnoreCase("")) {
									Double value = Double.parseDouble(obj[2].toString()) + (double) jRowArr.get(i);
									jRowArr.remove(i);
									jRowArr.add(i, value);
								}

							}

						}

					}
					map.put(tblRow, jRowArr);
				}
			} else {
				sqlGrandTotal.setLength(0);
				sqlGrandTotal.append("SELECT a.strBillNo,sum(b.dblSettlementAmt) "
						+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
						+ "WHERE a.strBillNo=b.strBillNo  " + "AND b.strSettlementCode=c.strSettelmentCode  "
						+ "AND a.strPOSCode=d.strPosCode  " + "and b.dblSettlementAmt>0 "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGrandTotal.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlGrandTotal.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlGrandTotal.append("GROUP BY a.strBillNo " + "ORDER BY a.strBillNo;");

				listSql = objBaseService.funGetList(sqlGrandTotal, "sql");
				int size = listSql.size();

				for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {

					List jRowArr = (List) map.get(tblRow);
					for (int i = 3; i < colCount - 1; i++) {
						for (int j = 0; j < size; j++) {
							Object[] obj = (Object[]) listSql.get(j);
							if (listColHeaderArr.get(i).toString().equalsIgnoreCase("GRAND Total")
									&& jRowArr.get(0).toString().equalsIgnoreCase(obj[0].toString())) {
								double value = Double.parseDouble(obj[1].toString()) + (double) jRowArr.get(i);
								jRowArr.remove(i);
								jRowArr.add(i, value);

							}

						}

					}
					map.put(tblRow, jRowArr);
				}
			}
			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				String columnNameForBillDtl = "sum(b.dblAmount)-sum(b.dblDiscountAmt)";
				if (taxCalType.equalsIgnoreCase("Backward")) {
					columnNameForBillDtl = "sum(b.dblAmount)-sum(b.dblDiscountAmt)-sum(b.dblTaxAmount)";
				}
				// fill Q data group
				sbSql.setLength(0);
				sbSql.append("select a.strBillNo,g.strGroupName," + columnNameForBillDtl + " "
						+ "from tblqbillhd a,tblqbilldtl b,tblitemmaster e " + ",tblsubgrouphd f ,tblgrouphd g "
						+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and a.strClientCode=b.strClientCode  " + "and b.strItemCode=e.strItemCode "
						+ "and e.strSubGroupCode=f.strSubGroupCode " + "and f.strGroupCode=g.strGroupCode "
						+ "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate
						+ "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sbSql.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!groupName.equalsIgnoreCase("All")) {
					sqlGroups.append(" and g.strGroupCode='" + groupName + "' ");
				}
				sbSql.append("GROUP BY a.strClientCode,a.strBillNo,g.strGroupCode,g.strGroupName ");

				listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) {
					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
							List jRowArr = (List) map.get(tblRow);
							if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
								for (int tblCol = 3; tblCol < colCount; tblCol++) {

									if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

									{
										if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, obj[2].toString());
											map.put(tblRow, jRowArr);
										} else {
											Double value = Double.parseDouble(obj[2].toString())
													+ (double) jRowArr.get(tblCol);
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, value);
											map.put(tblRow, jRowArr);
										}

									}
								}

							} else {
								continue;
							}
						}

					}

				}
				// Q Modifier Group data
				sbSql.setLength(0);
				sbSql.append("SELECT a.strBillNo,g.strGroupName, SUM(h.dblAmount)- SUM(h.dblDiscAmt) "
						+ "FROM tblqbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblqbillmodifierdtl h "
						+ "WHERE a.strBillNo=h.strBillNo  " + "and date(a.dteBillDate)=date(h.dteBillDate) "
						+ "AND a.strClientCode=h.strClientCode " + "AND e.strSubGroupCode=f.strSubGroupCode "
						+ "AND f.strGroupCode=g.strGroupCode " + "AND a.strBillNo=h.strBillNo "
						+ "AND e.strItemCode=LEFT(h.strItemCode,7) " + "and h.dblAmount>0 "
						+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sbSql.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sbSql.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!groupName.equalsIgnoreCase("All")) {
					sqlGroups.append(" and g.strGroupCode='" + groupName + "' ");
				}
				sbSql.append("GROUP BY a.strClientCode,a.strBillNo,g.strGroupCode,g.strGroupName ");
				listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) {
					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
							List jRowArr = (List) map.get(tblRow);
							if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
								for (int tblCol = 3; tblCol < colCount; tblCol++) {

									if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

									{
										if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, obj[2].toString());
											map.put(tblRow, jRowArr);
										} else {
											Double value = Double.parseDouble(obj[2].toString())
													+ Double.valueOf(jRowArr.get(tblCol).toString());
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, value);
											map.put(tblRow, jRowArr);
										}
										break;
									}
								}

							} else {
								continue;
							}
						}

					}

				}
				// Live Group Data
				sqlGroups.setLength(0);
				sqlGroups.append("select a.strBillNo,g.strGroupName," + columnNameForBillDtl + " "
						+ "from tblbillhd a,tblbilldtl b,tblitemmaster e " + ",tblsubgrouphd f ,tblgrouphd g "
						+ "where a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and a.strClientCode=b.strClientCode  " + "and b.strItemCode=e.strItemCode "
						+ "and e.strSubGroupCode=f.strSubGroupCode " + "and f.strGroupCode=g.strGroupCode "
						+ "AND b.dblAmount>0  " + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate
						+ "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				sqlGroups.append("GROUP BY a.strClientCode,a.strBillNo,g.strGroupCode,g.strGroupName ");

				listSql = objBaseService.funGetList(sqlGroups, "sql");

				if (listSql.size() > 0) {
					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
							List jRowArr = (List) map.get(tblRow);
							if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
								for (int tblCol = 3; tblCol < colCount; tblCol++) {
									if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

									{
										if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, obj[2].toString());
											map.put(tblRow, jRowArr);
										} else {
											Double value = Double.parseDouble(obj[2].toString())
													+ (double) jRowArr.get(tblCol);
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, value);
											map.put(tblRow, jRowArr);
										}
										break;
									}
								}

							} else {
								continue;
							}
						}
					}
				}

				// Live Modifier Group Data
				sqlGroups.setLength(0);
				sqlGroups.append("SELECT a.strBillNo,g.strGroupName, SUM(h.dblAmount)- SUM(h.dblDiscAmt) "
						+ "FROM tblbillhd a,tblitemmaster e,tblsubgrouphd f,tblgrouphd g,tblbillmodifierdtl h "
						+ "WHERE a.strBillNo=h.strBillNo  " + "and date(a.dteBillDate)=date(h.dteBillDate) "
						+ "AND a.strClientCode=h.strClientCode " + "AND e.strSubGroupCode=f.strSubGroupCode "
						+ "AND f.strGroupCode=g.strGroupCode " + "AND a.strBillNo=h.strBillNo "
						+ "AND e.strItemCode=LEFT(h.strItemCode,7) " + "and h.dblAmount>0 "
						+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlGroups.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlGroups.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				sqlGroups.append("GROUP BY a.strClientCode,a.strBillNo,g.strGroupCode,g.strGroupName ");

				listSql = objBaseService.funGetList(sqlGroups, "sql");

				if (listSql.size() > 0) {
					for (int i = 0; i < listSql.size(); i++) {
						Object[] obj = (Object[]) listSql.get(i);
						for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
							List jRowArr = (List) map.get(tblRow);
							if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
								for (int tblCol = 3; tblCol < colCount; tblCol++) {
									if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

									{
										if (0.00 == Double.valueOf( jRowArr.get(tblCol).toString())) {
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, obj[2].toString());
											map.put(tblRow, jRowArr);
										} else {
											Double value = Double.parseDouble(obj[2].toString())
													+ Double.valueOf(jRowArr.get(tblCol).toString());
											jRowArr.remove(tblCol);
											jRowArr.add(tblCol, value);
											map.put(tblRow, jRowArr);
										}
										break;
									}
								}

							} else {
								continue;
							}
						}
					}
				}
			}
			// Settlement Data
			StringBuilder sqlTransRecords = new StringBuilder();
			sqlTransRecords.setLength(0);
			sqlTransRecords.append("select a.strBillNo,c.strSettelmentDesc,sum(b.dblSettlementAmt) " + "from "
					+ "tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c " + "where  "
					+ "a.strBillNo=b.strBillNo  " + "and date(a.dteBillDate)=date(b.dteBillDate) "
					+ "and a.strClientCode=b.strClientCode   " + "and b.strSettlementCode=c.strSettelmentCode "
					+ "and b.dblSettlementAmt>0 " + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate
					+ "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by a.strClientCode,a.strBillNo,b.strSettlementCode "
					+ "order by a.strBillNo,b.strSettlementCode;");
			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
						List jRowArr = (List) map.get(tblRow);
						if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ Double.valueOf(jRowArr.get(tblCol).toString());
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}

			// Tax Data
			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				sqlTax.setLength(0);
				sqlTax.append("select a.strBillNo,c.strTaxDesc,sum(b.dblTaxAmount) " + "from "
						+ "tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c " + "where a.strBillNo=b.strBillNo  "
						+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strClientCode=b.strClientCode   "
						+ "and b.strTaxCode=c.strTaxCode " + "and date(a.dteBillDate) between '" + fromDate + "' and '"
						+ toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				sqlTax.append("group by a.strClientCode,a.strBillNo,b.strTaxCode "
						+ "order by a.strClientCode,a.strBillNo,b.strTaxCode;  ");
			} else {
				sqlTax.setLength(0);
				sqlTax.append("select a.strBillNo,c.strTaxDesc,sum(b.dblTaxAmount) " + "from "
						+ "tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c,tblqbillsettlementdtl d,tblsettelmenthd e "
						+ "where a.strBillNo=b.strBillNo " + "and b.strTaxCode=c.strTaxCode "
						+ "and a.strBillNo=d.strBillNo " + "and d.strSettlementCode=e.strSettelmentCode "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlTax.append("group by a.strBillNo,b.strTaxCode " + "order by a.strBillNo,b.strTaxCode; ");

			}
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
						List jRowArr = (List) map.get(tblRow);
						if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == Double.valueOf( jRowArr.get(tblCol).toString())) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ Double.valueOf(jRowArr.get(tblCol).toString());
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// live settlement data
			sqlTransRecords.setLength(0);
			sqlTransRecords.append("select a.strBillNo,c.strSettelmentDesc,sum(b.dblSettlementAmt) " + "from "
					+ "tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + "where  " + "a.strBillNo=b.strBillNo  "
					+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strClientCode=b.strClientCode   "
					+ "and b.strSettlementCode=c.strSettelmentCode " + "and b.dblSettlementAmt>0 "
					+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			if (!strOperationType.equalsIgnoreCase("All")) {
				sqlTransRecords.append("and a.strOperationType='" + strOperationType + "' ");
			}
			if (!strPOSCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and a.strPOSCode='" + strPOSCode + "' ");
			}
			if (!strSettlementCode.equalsIgnoreCase("All")) {
				sqlTransRecords.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
			}
			sqlTransRecords.append("group by a.strClientCode,a.strBillNo,b.strSettlementCode "
					+ "order by a.strClientCode,a.strBillNo,b.strSettlementCode;");

			listSql = objBaseService.funGetList(sqlTransRecords, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
						List jRowArr = (List) map.get(tblRow);
						if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {
								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ Double.valueOf( jRowArr.get(tblCol).toString());
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}
				}
			}

			// live tax data
			if (viewBy.equalsIgnoreCase("ITEM'S GROUP WISE")) {
				sqlTax.setLength(0);
				sqlTax.append("select a.strBillNo,c.strTaxDesc,sum(b.dblTaxAmount) " + "from "
						+ "tblbillhd a,tblbilltaxdtl b,tbltaxhd c  " + "where a.strBillNo=b.strBillNo  "
						+ "and date(a.dteBillDate)=date(b.dteBillDate) " + "and a.strClientCode=b.strClientCode   "
						+ "and b.strTaxCode=c.strTaxCode " + "and date(a.dteBillDate) between '" + fromDate + "' and '"
						+ toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				sqlTax.append("group by a.strClientCode,a.strBillNo,b.strTaxCode "
						+ "order by a.strClientCode,a.strBillNo,b.strTaxCode;  ");
			} else {
				sqlTax.setLength(0);
				sqlTax.append("select a.strBillNo,c.strTaxDesc,sum(b.dblTaxAmount) " + "from "
						+ "tblbillhd a,tblbilltaxdtl b,tbltaxhd c,tblbillsettlementdtl d,tblsettelmenthd e "
						+ "where a.strBillNo=b.strBillNo " + "and b.strTaxCode=c.strTaxCode "
						+ "and a.strBillNo=d.strBillNo " + "and d.strSettlementCode=e.strSettelmentCode "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				if (!strOperationType.equalsIgnoreCase("All")) {
					sqlTax.append("and a.strOperationType='" + strOperationType + "' ");
				}
				if (!strPOSCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}
				if (!strSettlementCode.equalsIgnoreCase("All")) {
					sqlTax.append(" and c.strSettelmentCode='" + strSettlementCode + "' ");
				}
				sqlTax.append("group by a.strBillNo,b.strTaxCode " + "order by a.strBillNo,b.strTaxCode;   ");
			}
			listSql = objBaseService.funGetList(sqlTax, "sql");

			if (listSql.size() > 0) {
				for (int i = 0; i < listSql.size(); i++) {
					Object[] obj = (Object[]) listSql.get(i);
					for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
						List jRowArr = (List) map.get(tblRow);
						if (listBillNoArr.get(tblRow).toString().equalsIgnoreCase(obj[0].toString())) {
							for (int tblCol = 3; tblCol < colCount; tblCol++) {

								if (listColHeaderArr.get(tblCol).toString().equalsIgnoreCase(obj[1].toString()))

								{
									if (0.00 == Double.valueOf(jRowArr.get(tblCol).toString())) {
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, obj[2].toString());
										map.put(tblRow, jRowArr);
									} else {
										Double value = Double.parseDouble(obj[2].toString())
												+ Double.valueOf(jRowArr.get(tblCol).toString());
										jRowArr.remove(tblCol);
										jRowArr.add(tblCol, value);
										map.put(tblRow, jRowArr);
									}
									break;
								}
							}

						} else {
							continue;
						}
					}

				}

			}
			List listData = new ArrayList();
			mapRet.put("Col Header", listColHeaderArr);
			mapRet.put("Col Count", colCount);
			mapRet.put("Row Count", listBillNoArr.size());
			for (int tblRow = 0; tblRow < listBillNoArr.size(); tblRow++) {
				List jArr = (List) map.get(tblRow);
				mapRet.put("" + tblRow, jArr);
				listData.add(jArr);
				mapRet.put("listData", listData);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return mapRet;

	}

	public Map funSalesReport(String fromDate, String toDate, String strPOSCode, String strShiftNo, String strUserCode,
			String field, String strPayMode, String strOperator, String strFromBill, String strToBill,
			String reportType, String Type, String Customer, String ConsolidatePOS, String ReportName,
			String LoginPOSCode,String enableShiftYN) 
	{

		DecimalFormat decimalFormat;
		decimalFormat = new DecimalFormat("#.##");
		String AreaWisePricing = "Y";

		double totalDiscAmt = 0, totalSubTotalDWise = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

		List listData = new ArrayList();
		Map mapReturn = new HashMap();
		List<clsPOSSalesFlashReportsBean> arrListSalesReport;
		BigDecimal totalAmount, temp, temp1, Disc;
		int rowCount = 0;

		double totalSale = 0;

		double totalQty = 0;
		double subTotal = 0.00;
		double discountTotal = 0.00;
		StringBuilder sbSqlLiveBill = new StringBuilder();
		StringBuilder sbSqlQFileBill = new StringBuilder();
		StringBuilder sqlModLive = new StringBuilder();
		StringBuilder sqlModQFile = new StringBuilder();
		StringBuilder sbFilters = new StringBuilder();
		StringBuilder sbSqlDisFilters = new StringBuilder();

		Map map = new HashMap();
		List listColHeader = new ArrayList();
		int colCount = 0;
		try {
			switch (ReportName) {

			case "SettlementWise":
				listColHeader.add("POS");
				listColHeader.add("Settlement Mode");
				listColHeader.add("Sales Amount");
				listColHeader.add("Sales %");
				colCount = 4;
				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");

				sbSqlLiveBill.setLength(0);
				sbSqlQFileBill.setLength(0);
				sqlModLive.setLength(0);
				sqlModQFile.setLength(0);
				sbFilters.setLength(0);
				
				if (field.equals("dteBillDate")) {
					field = "c.dteBillDate";
				} else {
					field = "date(c.dteBillDate)";
				}

				sbSqlLiveBill.append(
						"SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
								+ " , IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'" + strUserCode + "'"
								+ " ,b.strSettelmentType " + " from " + " tblbillsettlementdtl a "
								+ " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
								+ " LEFT OUTER JOIN tblbillhd c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
								+ " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode " + " WHERE " + field
								+ " BETWEEN '" + fromDate + "' AND '" + toDate + "' " + "AND a.dblSettlementAmt>0 ");

				sbSqlQFileBill.append(
						"SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
								+ " ,IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'" + strUserCode + "' "
								+ " ,b.strSettelmentType " + " from " + " tblqbillsettlementdtl a "
								+ " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
								+ " LEFT OUTER JOIN tblqbillhd c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
								+ " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode " + " WHERE " + field
								+ " BETWEEN '" + fromDate + "' AND '" + toDate + "' " + " AND a.dblSettlementAmt>0 ");

				if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
					sbFilters.append(
							"  AND d.strPOSCode = '" + strPOSCode + "' and c.strUserCreated='" + strOperator + "' ");
				} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
					sbFilters.append(" AND d.strPOSCode = '" + strPOSCode + "'");
				} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
					sbFilters.append("  and c.strUserCreated='" + strOperator + "'");
				}
				if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
				{
					sbFilters.append(" AND c.intShiftCode = '" + strShiftNo + "' ");
				}
				

				if (strFromBill.length() == 0 && strToBill.length() == 0) {
				} else {
					sbFilters.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
				}
				if (!strPayMode.equalsIgnoreCase("All")) {
					sbFilters.append(" and b.strSettelmentDesc='" + strPayMode + "' ");
				}

				if (ConsolidatePOS.equalsIgnoreCase("Y")) {
					sbFilters.append(" GROUP BY b.strSettelmentDesc ");
				} else {
					sbFilters.append(" GROUP BY b.strSettelmentDesc, d.strPosCode");
				}
				sbSqlLiveBill.append(" ").append(sbFilters);
				sbSqlQFileBill.append(" ").append(sbFilters);

				mapPOSDtlForSettlement = new LinkedHashMap<String, List<Map<String, clsPOSBillSettlementDtl>>>();

				List listSettlementWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
				funGenerateSettlementWiseSales(listSettlementWiseSales);

				List listSettlementWiseSalesQ = objBaseService.funGetList(sbSqlQFileBill, "sql");
				funGenerateSettlementWiseSales(listSettlementWiseSalesQ);

				Iterator<Map.Entry<String, List<Map<String, clsPOSBillSettlementDtl>>>> it = mapPOSDtlForSettlement
						.entrySet().iterator();
				List<clsPOSBillSettlementDtl> lstTemp = new ArrayList<clsPOSBillSettlementDtl>();

				List<clsPOSSalesFlashReportsBean> arrTempListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
				List listStockFlashModel = new ArrayList();

				while (it.hasNext()) {
					Map.Entry<String, List<Map<String, clsPOSBillSettlementDtl>>> entry = it.next();
					List<Map<String, clsPOSBillSettlementDtl>> listOfSettelment = entry.getValue();

					for (int i = 0; i < listOfSettelment.size(); i++) {

						clsPOSBillSettlementDtl objSettlementDtl = listOfSettelment.get(i).entrySet().iterator().next()
								.getValue();
						lstTemp.add(objSettlementDtl);
						totalSale += objSettlementDtl.getDblSettlementAmt();
						clsPOSSalesFlashReportsBean obSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						obSalesFlashColumns.setStrField1(objSettlementDtl.getPosName());
						obSalesFlashColumns.setStrField2(objSettlementDtl.getStrSettlementName());
						obSalesFlashColumns.setStrField3(String.valueOf(objSettlementDtl.getDblSettlementAmt()));

						arrTempListSalesReport.add(obSalesFlashColumns);
						List DataList = new ArrayList<>();
						DataList.add(objSettlementDtl.getPosName());
						DataList.add(objSettlementDtl.getStrSettlementName());
						DataList.add(objSettlementDtl.getDblSettlementAmt());
						map.put(rowCount, DataList);
						rowCount++;
					}

				}
				try {
					BigDecimal bigtotalSale = new BigDecimal(totalSale);
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListSalesReport = gson.toJson(arrTempListSalesReport, type);
					mapReturn.put("ListSettlementWiseSales", gsonarrTempListSalesReport);
					mapReturn.put("TotalSale", bigtotalSale);
					mapReturn.put("ColHeader", listColHeader);
					mapReturn.put("colCount", colCount);
					mapReturn.put("RowCount", rowCount);
					// mapReturn.put("listStockFlashModel", listStockFlashModel);

					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List list = (List) map.get(tblRow);
						list.add((Double.parseDouble(list.get(2).toString())
								/ Double.parseDouble(bigtotalSale.toString())) * 100);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						mapReturn.put("" + tblRow, list);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "BillWise":
				List listRet = new ArrayList();
				listColHeader.add("Bill No");
				listColHeader.add("Date");
				listColHeader.add("Bill time");
				listColHeader.add("Table Name");
				listColHeader.add("Cust Name");
				listColHeader.add("POS");
				listColHeader.add("Pay Mode");
				listColHeader.add("Delivery Charge");
				listColHeader.add("Sub Total");
				listColHeader.add("Disc %");
				listColHeader.add("Disc Amt");
				listColHeader.add("TAX Amt");
				listColHeader.add("Sales Amt");
				listColHeader.add("Remark");
				listColHeader.add("Tip");
				listColHeader.add("Disc Remark");
				listColHeader.add("Reason");
				colCount = 17;
				
				if (field.equals("dteBillDate")) {
					field = "a.dteBillDate";
				} else {
					field = "date(a.dteBillDate)";
				}

				sbSqlLiveBill.setLength(0);
				sbSqlLiveBill.append(
						"select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
								+ " ,ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
								+ " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt "
								+ " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated "
								+ " ,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo "
								+ " ,a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA') "
								+ " ,a.dblTipAmount,'" + strUserCode
								+ "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
								+ " from tblbillhd  a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
								+ " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
								+ " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
								+ " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
								+ " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
								+ " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode " + " where "
								+ field + " between '" + fromDate + "' and '" + toDate + "'");

				if (!strPOSCode.equals("All")) {
					sbSqlLiveBill.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}

				if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
				{
					sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
				}

				if (!strOperator.equals("All")) {
					sbSqlLiveBill.append(" and  a.strUserCreated='" + strOperator + "' ");
				}
				if (!strPayMode.equals("All")) {
					sbSqlLiveBill.append(" and d.strSettelmentCode='" + strPayMode + "' ");
				}
				if (strFromBill.trim().length() > 0 && strToBill.trim().length() > 0) {
					sbSqlLiveBill.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
				}
				sbSqlLiveBill.append(" order by a.strBillNo desc ");

				sbSqlQFileBill.setLength(0);
				sbSqlQFileBill.append(
						"select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
								+ " ,ifnull(b.strTableName,'') as TableName,f.strPOSName" + ""
								+ ", ifnull(d.strSettelmentDesc,'') as payMode"
								+ " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
								+ " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
								+ " ,a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
								+ " ,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')" + " ,a.dblTipAmount,'"
								+ strUserCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
								+ " from tblqbillhd a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
								+ " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
								+ " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
								+ " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
								+ " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
								+ " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode " + " where "
								+ field + " between '" + fromDate + "' and '" + toDate + "'");

				if (!strPOSCode.equals("All")) {
					sbSqlQFileBill.append(" and a.strPOSCode='" + strPOSCode + "' ");
				}

				if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
				{
					sbSqlQFileBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
				}

				if (!strOperator.equals("All")) {
					sbSqlQFileBill.append(" and  a.strUserCreated='" + strOperator + "' ");
				}
				if (!strPayMode.equals("All")) {
					sbSqlQFileBill.append(" and d.strSettelmentCode='" + strPayMode + "' ");
				}
				if (strFromBill.trim().length() > 0 && strToBill.trim().length() > 0) {
					sbSqlQFileBill.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
				}
				sbSqlQFileBill.append(" order by a.strBillNo desc ");

				boolean flgRecords = false;

				Map<String, List<clsPOSSalesFlashReportsBean>> hmBillWiseSales = new HashMap<String, List<clsPOSSalesFlashReportsBean>>();
				int seqNo = 1;

				// for live Data
				List listBillWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

				if (listBillWiseSales.size() > 0) {
					for (int i = 0; i < listBillWiseSales.size(); i++) {
						Object[] obj = (Object[]) listBillWiseSales.get(i);
						List<clsPOSSalesFlashReportsBean> arrListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
						flgRecords = true;
						String[] spDate = obj[1].toString().split("-");
						String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];// billDate

						clsPOSSalesFlashReportsBean objSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objSalesFlashColumns.setStrField1(obj[0].toString());
						objSalesFlashColumns.setStrField2(billDate);
						objSalesFlashColumns.setStrField3(obj[2].toString());
						objSalesFlashColumns.setStrField4(obj[3].toString());
						objSalesFlashColumns.setStrField5(obj[20].toString());// CustName
						objSalesFlashColumns.setStrField6(obj[4].toString());
						objSalesFlashColumns.setStrField7(obj[5].toString());
						objSalesFlashColumns.setStrField8(obj[18].toString());
						objSalesFlashColumns.setStrField9(obj[6].toString());
						objSalesFlashColumns.setStrField10(obj[7].toString());
						objSalesFlashColumns.setStrField11(obj[8].toString());
						objSalesFlashColumns.setStrField12(obj[9].toString());
						objSalesFlashColumns.setStrField13(obj[10].toString());
						objSalesFlashColumns.setStrField14(obj[19].toString());
						objSalesFlashColumns.setStrField15(obj[21].toString());
						objSalesFlashColumns.setStrField16(obj[23].toString());
						objSalesFlashColumns.setStrField17(obj[24].toString());
						objSalesFlashColumns.setSeqNo(seqNo++);

						if (null != hmBillWiseSales.get(obj[0].toString())) {
							arrListBillWiseSales = hmBillWiseSales.get(obj[0].toString());
							objSalesFlashColumns.setStrField9("0");
							objSalesFlashColumns.setStrField10("0");
							objSalesFlashColumns.setStrField11("0");
							objSalesFlashColumns.setStrField12("0");
							objSalesFlashColumns.setStrField15("0");
						}
						arrListBillWiseSales.add(objSalesFlashColumns);
						hmBillWiseSales.put(obj[0].toString(), arrListBillWiseSales);

						totalDiscAmt += Double.parseDouble(objSalesFlashColumns.getStrField11());
						totalSubTotalDWise += Double.parseDouble(objSalesFlashColumns.getStrField9());
						totalTaxAmt += Double.parseDouble(objSalesFlashColumns.getStrField12());
						totalSettleAmt += Double.parseDouble(objSalesFlashColumns.getStrField13());// Grand Total
						totalTipAmt += Double.parseDouble(objSalesFlashColumns.getStrField15());// tip Amt

					}
				}

				// for qfile data
				List listBillWiseSalesQ = objBaseService.funGetList(sbSqlQFileBill, "sql");
				if (listBillWiseSalesQ.size() > 0) {
					for (int i = 0; i < listBillWiseSalesQ.size(); i++) {
						Object[] obj = (Object[]) listBillWiseSalesQ.get(i);
						List<clsPOSSalesFlashReportsBean> arrListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
						flgRecords = true;

						String[] spDate = obj[1].toString().split("-");
						String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];// billDate

						clsPOSSalesFlashReportsBean objSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objSalesFlashColumns.setStrField1(obj[0].toString());
						objSalesFlashColumns.setStrField2(billDate);
						objSalesFlashColumns.setStrField3(obj[2].toString());
						objSalesFlashColumns.setStrField4(obj[3].toString());
						objSalesFlashColumns.setStrField5(obj[20].toString());// Cust Name
						objSalesFlashColumns.setStrField6(obj[4].toString());
						objSalesFlashColumns.setStrField7(obj[6].toString());
						objSalesFlashColumns.setStrField8(obj[18].toString());
						objSalesFlashColumns.setStrField9(obj[6].toString());
						objSalesFlashColumns.setStrField10(obj[7].toString());
						objSalesFlashColumns.setStrField11(obj[8].toString());
						objSalesFlashColumns.setStrField12(obj[9].toString());
						objSalesFlashColumns.setStrField13(obj[10].toString());
						objSalesFlashColumns.setStrField14(obj[19].toString());
						objSalesFlashColumns.setStrField15(obj[21].toString());
						objSalesFlashColumns.setStrField16(obj[23].toString());
						objSalesFlashColumns.setStrField17(obj[24].toString());
						objSalesFlashColumns.setSeqNo(seqNo++);

						if (null != hmBillWiseSales.get(obj[0].toString())) {
							arrListBillWiseSales = hmBillWiseSales.get(obj[0].toString());
							objSalesFlashColumns.setStrField9("0");
							objSalesFlashColumns.setStrField10("0");
							objSalesFlashColumns.setStrField11("0");
							objSalesFlashColumns.setStrField12("0");
							objSalesFlashColumns.setStrField15("0");
						}
						arrListBillWiseSales.add(objSalesFlashColumns);
						hmBillWiseSales.put(obj[0].toString(), arrListBillWiseSales);

						totalDiscAmt += Double.parseDouble(objSalesFlashColumns.getStrField11());
						totalSubTotalDWise += Double.parseDouble(objSalesFlashColumns.getStrField9());
						totalTaxAmt += Double.parseDouble(objSalesFlashColumns.getStrField12());
						totalSettleAmt += Double.parseDouble(objSalesFlashColumns.getStrField13());// Grand Total
						totalTipAmt += Double.parseDouble(objSalesFlashColumns.getStrField15());// tip Amt
					}
				}
				System.out.println("Tip Amount->" + totalTipAmt);
				List<clsPOSSalesFlashReportsBean> arrTempListBillWiseSales = new ArrayList<clsPOSSalesFlashReportsBean>();
				for (Map.Entry<String, List<clsPOSSalesFlashReportsBean>> entry : hmBillWiseSales.entrySet()) {
					for (clsPOSSalesFlashReportsBean objSalesFlashColumns : entry.getValue()) {
						clsPOSSalesFlashReportsBean objTempSalesFlashColumns = new clsPOSSalesFlashReportsBean();
						objTempSalesFlashColumns.setStrField1(objSalesFlashColumns.getStrField1());
						objTempSalesFlashColumns.setStrField2(objSalesFlashColumns.getStrField2());
						objTempSalesFlashColumns.setStrField3(objSalesFlashColumns.getStrField3());
						objTempSalesFlashColumns.setStrField4(objSalesFlashColumns.getStrField4());
						objTempSalesFlashColumns.setStrField5(objSalesFlashColumns.getStrField5());
						objTempSalesFlashColumns.setStrField6(objSalesFlashColumns.getStrField6());
						objTempSalesFlashColumns.setStrField7(objSalesFlashColumns.getStrField7());
						objTempSalesFlashColumns.setStrField8(objSalesFlashColumns.getStrField8());
						objTempSalesFlashColumns.setStrField9(objSalesFlashColumns.getStrField9());
						objTempSalesFlashColumns.setStrField10(objSalesFlashColumns.getStrField10());
						objTempSalesFlashColumns.setStrField11(objSalesFlashColumns.getStrField11());
						objTempSalesFlashColumns.setStrField12(objSalesFlashColumns.getStrField12());
						objTempSalesFlashColumns.setStrField13(objSalesFlashColumns.getStrField13());
						objTempSalesFlashColumns.setStrField14(objSalesFlashColumns.getStrField14());
						objTempSalesFlashColumns.setStrField15(objSalesFlashColumns.getStrField15());
						objTempSalesFlashColumns.setStrField16(objSalesFlashColumns.getStrField16());
						objTempSalesFlashColumns.setStrField17(objSalesFlashColumns.getStrField17());
						objTempSalesFlashColumns.setSeqNo(objSalesFlashColumns.getSeqNo());
						arrTempListBillWiseSales.add(objTempSalesFlashColumns);

						List DataList = new ArrayList<>();
						DataList.add(objSalesFlashColumns.getStrField1());
						DataList.add(objSalesFlashColumns.getStrField2());
						DataList.add(objSalesFlashColumns.getStrField3());
						DataList.add(objSalesFlashColumns.getStrField4());
						DataList.add(objSalesFlashColumns.getStrField5());
						DataList.add(objSalesFlashColumns.getStrField6());
						DataList.add(objSalesFlashColumns.getStrField7());
						DataList.add(objSalesFlashColumns.getStrField8());
						DataList.add(objSalesFlashColumns.getStrField9());
						DataList.add(objSalesFlashColumns.getStrField10());
						DataList.add(objSalesFlashColumns.getStrField11());
						DataList.add(objSalesFlashColumns.getStrField12());
						DataList.add(objSalesFlashColumns.getStrField13());
						DataList.add(objSalesFlashColumns.getStrField14());
						DataList.add(objSalesFlashColumns.getStrField15());
						DataList.add(objSalesFlashColumns.getStrField16());
						DataList.add(objSalesFlashColumns.getStrField17());
						map.put(rowCount, DataList);
						rowCount++;
					}
				}

				// sort arrTempListBillWiseSales
				Collections.sort(arrTempListBillWiseSales, clsPOSSalesFlashComparator.COMPARATOR);
				System.out.print("@Dao " + arrTempListBillWiseSales.size());
				try {
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListBillWiseSales = gson.toJson(arrTempListBillWiseSales, type);
					mapReturn.put("TempListBillWiseSales", gsonarrTempListBillWiseSales);
					mapReturn.put("totalDiscAmt", totalDiscAmt);
					mapReturn.put("totalSubTotal", totalSubTotalDWise);
					mapReturn.put("totalTaxAmt", totalTaxAmt);
					mapReturn.put("totalSettleAmt", totalSettleAmt);
					mapReturn.put("totalTipAmt", totalTipAmt);
					mapReturn.put("ColHeader", listColHeader);
					mapReturn.put("colCount", colCount);
					mapReturn.put("RowCount", rowCount);

					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List list = (List) map.get(tblRow);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						mapReturn.put("" + tblRow, list);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemWise":
				listColHeader.add("Item Name");
				listColHeader.add("POS ");
				listColHeader.add("Quantity");
				listColHeader.add("sub Total ");
				listColHeader.add("Sales Amount ");
				listColHeader.add("Discount");
				colCount = 6;

				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");

				sbSqlLiveBill.setLength(0);
				sbSqlQFileBill.setLength(0);
				sqlModLive.setLength(0);
				sqlModQFile.setLength(0);
				sbFilters.setLength(0);

				if (field.equals("dteBillDate")) {
					field = "b.dteBillDate";
				} else {
					field = "date(b.dteBillDate)";
				}
				sbSqlLiveBill.append("select a.strItemCode,a.strItemName,c.strPOSName"
						+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n" + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'"
						+ strUserCode + "' "
						+ ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
						+ "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
						+ "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode and a.strClientCode=b.strClientCode "
						+ "and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

				sbSqlQFileBill.append("select a.strItemCode,a.strItemName,c.strPOSName"
						+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n" + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'"
						+ strUserCode + "' "
						+ ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
						+ "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
						+ "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode and a.strClientCode=b.strClientCode "
						+ "and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

				sqlModLive.append("select a.strItemCode,a.strModifierName,c.strPOSName"
						+ " ,sum(a.dblQuantity),'0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
						+ " ,sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
						+ " from tblbillmodifierdtl a,tblbillhd b,tblposmaster c,tblitemmaster d\n"
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode and a.strClientCode=b.strClientCode "
						+ " and left(a.strItemCode,7)=d.strItemCode \n" + " and " + field + " BETWEEN '" + fromDate
						+ "' AND '" + toDate + "' ");

				sqlModQFile.append("select a.strItemCode,a.strModifierName,c.strPOSName"
						+ " ,sum(a.dblQuantity),'0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
						+ " ,sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
						+ " from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d\n"
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode and a.strClientCode=b.strClientCode "
						+ " and left(a.strItemCode,7)=d.strItemCode \n" + " and " + field + " BETWEEN '" + fromDate
						+ "' AND '" + toDate + "' ");

				if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
					sbFilters.append(
							" AND b.strPOSCode = '" + strPOSCode + "' and b.strUserCreated='" + strOperator + "' ");
				} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
					sbFilters.append(" AND b.strPOSCode = '" + strPOSCode + "' ");
				} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
					sbFilters.append(" AND b.strUserCreated='" + strOperator + "' ");
				}
				
				if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
				{
					sbFilters.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
				}

				if (strFromBill.length() == 0 && strToBill.length() == 0) {
				} else {
					sbFilters.append(" and a.strbillno between '" + strFromBill + "' " + " and '" + strToBill + "'");
				}

				sbFilters.append(" group by a.strItemCode,c.strPOSName " + " order by b.dteBillDate ");
				sbSqlLiveBill.append(" " + sbFilters);
				sbSqlQFileBill.append(" " + sbFilters);
				sqlModLive.append(" " + sbFilters);
				sqlModQFile.append(" " + sbFilters);

				mapPOSItemDtl = new LinkedHashMap<>();

				List listItemWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

				funGenerateItemWiseSales(listItemWiseSales, fromDate, toDate, strPOSCode, strShiftNo, strUserCode,
						field, strPayMode, strOperator, strFromBill, strToBill, reportType, Type, Customer,
						ConsolidatePOS, ReportName);

				List listItemWiseSalesQ = objBaseService.funGetList(sbSqlQFileBill, "sql");

				funGenerateItemWiseSales(listItemWiseSalesQ, fromDate, toDate, strPOSCode, strShiftNo, strUserCode,
						field, strPayMode, strOperator, strFromBill, strToBill, reportType, Type, Customer,
						ConsolidatePOS, ReportName);

				Set<Entry<String, Map<String, clsPOSBillItemDtl>>> set = mapPOSItemDtl.entrySet();
				List<Entry<String, Map<String, clsPOSBillItemDtl>>> list = new ArrayList<Entry<String, Map<String, clsPOSBillItemDtl>>>(
						set);

				Collections.sort(list, new Comparator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>>() {

					@Override
					public int compare(Entry<String, Map<String, clsPOSBillItemDtl>> o1,
							Entry<String, Map<String, clsPOSBillItemDtl>> o2) {

						Iterator<Entry<String, clsPOSBillItemDtl>> it1 = o1.getValue().entrySet().iterator();
						Iterator<Entry<String, clsPOSBillItemDtl>> it2 = o2.getValue().entrySet().iterator();

						if (it1.hasNext()) {
							if (it1.next().getValue().getItemCode().substring(0, 7)
									.equalsIgnoreCase(it1.next().getValue().getItemCode().substring(0, 7))) {
								return 0;
							} else {
								return 1;
							}
						}
						return 0;
					}

				});

				Iterator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet()
						.iterator();

				arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

				while (posIterator.hasNext()) {
					Map<String, clsPOSBillItemDtl> mapItemDtl = posIterator.next().getValue();
					Iterator<Map.Entry<String, clsPOSBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
					while (itemIterator.hasNext()) {
						clsPOSBillItemDtl objGroupDtl = itemIterator.next().getValue();
						clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
						obj.setStrField1(objGroupDtl.getItemName());// itemName
						obj.setStrField2(objGroupDtl.getPosName());// posName
						obj.setStrField3(String.valueOf(objGroupDtl.getQuantity()));// qty
						obj.setStrField4(String.valueOf(objGroupDtl.getSubTotal()));// sunTotal
						obj.setStrField5(String.valueOf(objGroupDtl.getAmount()));// salesAmount
						obj.setStrField6(String.valueOf(objGroupDtl.getDiscountAmount()));// discount
						arrListSalesReport.add(obj);

						List DataList = new ArrayList<>();
						DataList.add(objGroupDtl.getItemName());
						DataList.add(objGroupDtl.getPosName());
						DataList.add(objGroupDtl.getQuantity());
						DataList.add(objGroupDtl.getSubTotal());
						DataList.add(objGroupDtl.getAmount());
						DataList.add(objGroupDtl.getDiscountAmount());
						map.put(rowCount, DataList);
						rowCount++;

						totalQty = totalQty + objGroupDtl.getQuantity();
						temp1 = new BigDecimal(objGroupDtl.getAmount());
						totalAmount = totalAmount.add(temp1);
						subTotal = subTotal + objGroupDtl.getSubTotal();
						discountTotal = discountTotal + objGroupDtl.getDiscountAmount();

					}
				}
				try {
					Gson gson = new Gson();
					Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
					}.getType();
					String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
					mapReturn.put("ListItemWiseSales", gsonarrTempListSalesReport);
					mapReturn.put("totalQty", totalQty);
					mapReturn.put("totalAmount", totalAmount);
					mapReturn.put("subTotal", subTotal);
					mapReturn.put("discountTotal", discountTotal);
					mapReturn.put("ColHeader", listColHeader);
					mapReturn.put("colCount", colCount);
					mapReturn.put("RowCount", rowCount);
					for (int tblRow = 0; tblRow < map.size(); tblRow++) {
						List listmap = (List) map.get(tblRow);
						System.out.println("map.get(tblRow)" + map.get(tblRow));
						mapReturn.put("" + tblRow, listmap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MenuHeadWise":

				listColHeader.add("Menu Name");
				listColHeader.add("POS ");
				listColHeader.add("Quantity");
				listColHeader.add("sub Total ");
				listColHeader.add("Sales Amount ");
				listColHeader.add("Discount");
				listColHeader.add("Sales (%)");
				colCount = 7;
				String sql;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "b.dteBillDate";
					} else {
						field = "date(b.dteBillDate)";
					}

					sbSqlQFileBill.append(
							"SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode
									+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
									+ "FROM tblqbilldtl a\n"
									+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
									+ " and b.strposcode =d.strposcode ");

					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sbSqlQFileBill.append("and b.strAreaCode= d.strAreaCode ");
					}
					sbSqlQFileBill.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sbSqlQFileBill.append(" where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					sbSqlLiveBill.append(
							"SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode
									+ "',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
									+ " FROM tblbilldtl a\n"
									+ " left outer join tblbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
									+ " and b.strposcode =d.strposcode ");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sbSqlLiveBill.append("and b.strAreaCode= d.strAreaCode ");
					}
					sbSqlLiveBill.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sbSqlLiveBill.append(" where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					sqlModLive.append(
							"SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode
									+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
									+ "FROM tblbillmodifierdtl a\n"
									+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
									+ " and b.strposcode =d.strposcode ");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sqlModLive.append("and b.strAreaCode= d.strAreaCode ");
					}
					sqlModLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sqlModLive.append(
							" where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 ");

					sqlModQFile.append(
							"SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
									+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode
									+ "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
									+ "FROM tblqbillmodifierdtl a\n"
									+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
									+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
									+ " and b.strposcode =d.strposcode ");

					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing.equals("Y")
					{
						sqlModQFile.append("and b.strAreaCode= d.strAreaCode ");
					}
					sqlModQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
					sqlModQFile.append(
							" where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0  ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" AND b.strPOSCode = '" + strPOSCode + "' and d.strUserCreated='"
								+ strOperator.toString() + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND b.strPOSCode = '" + strPOSCode + "'");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" and b.strUserCreated='" + strOperator.toString() + "'");
					}
					if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
						// sql_Filters+=" Group by b.strPoscode, d.strMenuCode,e.strMenuName";
					} else {
						sbFilters.append(" and b.strBillNo between '" + strFromBill + "' and '" + strToBill + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
					}

					sbFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");
					sbFilters.append(" order by b.strPoscode, d.strMenuCode,e.strMenuName");

					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);
					sqlModLive.append(sbFilters);
					sqlModQFile.append(sbFilters);

					mapPOSMenuHeadDtl = new LinkedHashMap<>();

					List listMenuHeadWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					listMenuHeadWiseSales = objBaseService.funGetList(sqlModLive, "sql");
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					listMenuHeadWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					listMenuHeadWiseSales = objBaseService.funGetList(sqlModQFile, "sql");
					funGenerateMenuHeadWiseSales(listMenuHeadWiseSales);

					Iterator<Map.Entry<String, Map<String, clsPOSBillItemDtl>>> posIterator1 = mapPOSMenuHeadDtl
							.entrySet().iterator();
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					totalSale = 0;
					while (posIterator1.hasNext()) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = posIterator1.next().getValue();
						Iterator<Map.Entry<String, clsPOSBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
						while (itemIterator.hasNext()) {
							clsPOSBillItemDtl objGroupDtl = itemIterator.next().getValue();
							clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
							obj.setStrField1(objGroupDtl.getMenuName());// menuName
							obj.setStrField2(objGroupDtl.getPosName());// posName

							obj.setStrField3(String.valueOf(objGroupDtl.getQuantity()));// qty
							obj.setStrField4(String.valueOf(objGroupDtl.getAmount()));// salesAmt
							obj.setStrField5(String.valueOf(objGroupDtl.getSubTotal()));// subTotal
							obj.setStrField6(String.valueOf(objGroupDtl.getDiscountAmount()));// discAmt
							arrListSalesReport.add(obj);

							List DataList = new ArrayList<>();
							DataList.add(objGroupDtl.getMenuName());
							DataList.add(objGroupDtl.getPosName());
							DataList.add(objGroupDtl.getQuantity());
							DataList.add(objGroupDtl.getSubTotal());
							DataList.add(objGroupDtl.getAmount());
							DataList.add(objGroupDtl.getDiscountAmount());
							map.put(rowCount, DataList);
							rowCount++;

							totalQty = totalQty + objGroupDtl.getQuantity();
							totalSale += objGroupDtl.getAmount();
							subTotal = subTotal + objGroupDtl.getSubTotal();
							discountTotal = discountTotal + objGroupDtl.getDiscountAmount();

						}
					}

					try {
						BigDecimal big = new BigDecimal(totalSale);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListMenuHeadWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("totalAmount", big);
						mapReturn.put("subTotal", subTotal);
						mapReturn.put("discountTotal", discountTotal);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format(
									(Double.parseDouble(listmap.get(4).toString()) / Double.parseDouble(big.toString()))
											* 100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "GroupWise":
				listColHeader.add("Group Name");
				listColHeader.add("POS ");
				listColHeader.add("Quantity");
				listColHeader.add("sub Total ");
				listColHeader.add("Net Total");
				listColHeader.add("Discount");
				listColHeader.add("Sales (%)");
				colCount = 7;

				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}

					sbSqlLiveBill.append(
							"SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),sum( b.dblAmount)-sum(b.dblDiscountAmt) "
									+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount) "
									+ ",sum(b.dblDiscountAmt),a.strPOSCode,sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount) "
									+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
									+ ",tblitemmaster e,tblposmaster f "
									+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ "and b.strItemCode=e.strItemCode "
									+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");

					sbSqlQFileBill.append(
							"SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity),sum( b.dblAmount)-sum(b.dblDiscountAmt) "
									+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount) "
									+ ",sum(b.dblDiscountAmt),a.strPOSCode,sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount) "
									+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
									+ ",tblitemmaster e,tblposmaster f "
									+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
									+ "and b.strItemCode=e.strItemCode "
									+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");

					sqlModLive.append("select c.strGroupCode,c.strGroupName,sum(b.dblQuantity)"
							+ ",sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'" + strUserCode + "','0'"
							+ ",sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,sum(b.dblAmount)-sum(b.dblDiscAmt) "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 " + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ");
					sqlModQFile.append("select c.strGroupCode,c.strGroupName,sum(b.dblQuantity)"
							+ ",sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName,'" + strUserCode + "'"
							+ ",'0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,sum(b.dblAmount)-sum(b.dblDiscAmt) "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd e,tblgrouphd c "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode "
							+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode "
							+ " and b.dblamount>0 " + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(
								" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='" + strOperator + "' ");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" and a.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}

					if (ConsolidatePOS.equals("Y")) {
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "'"
									+ " GROUP BY c.strGroupCode, c.strGroupName ");
						} else {

							sbFilters.append(" WHERE " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ "and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'"
									+ " GROUP BY c.strGroupCode, c.strGroupName ");
						}
					} else {
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
						} else {
							sbFilters.append(" WHERE " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ "and a.strBillNo between '" + strFromBill + "' and '" + strFromBill + "'"
									+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
						}
					}

					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);
					sqlModLive.append(sbFilters);
					sqlModQFile.append(sbFilters);

					mapPOSDtlForGroupSubGroup = new LinkedHashMap<>();
					subTotal = 0.00;
					discountTotal = 0.00;

					List listGroupWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateGroupWiseSales(listGroupWiseSales);

					listGroupWiseSales = objBaseService.funGetList(sqlModLive, "sql");
					funGenerateGroupWiseSales(listGroupWiseSales);

					listGroupWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateGroupWiseSales(listGroupWiseSales);

					listGroupWiseSales = objBaseService.funGetList(sqlModQFile, "sql");
					funGenerateGroupWiseSales(listGroupWiseSales);

					double totalSalesAmt = 0, totalGrandTotal = 0;
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>>> subGroupIt1 = mapPOSDtlForGroupSubGroup
							.entrySet().iterator();
					while (subGroupIt1.hasNext()) {
						Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> entry = subGroupIt1.next();
						String posCode = entry.getKey();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = entry.getValue();
						for (int i = 0; i < listOfGroup.size(); i++) {
							if (ConsolidatePOS.equalsIgnoreCase("Y")) {
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup.get(i).entrySet().iterator()
										.next().getValue();

								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// POSName
																			// ...........pending
								obj.setStrField3(String.valueOf(objGroupDtl.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl.getSubTotal()));// subTotal
								obj.setStrField6(String.valueOf(objGroupDtl.getDiscAmt()));// discAmt

								Object[] arrObjRows = { objGroupDtl.getGroupName(), objGroupDtl.getQty(),
										objGroupDtl.getSalesAmt(), objGroupDtl.getSubTotal(),
										objGroupDtl.getDiscAmt() };

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								totalSalesAmt += objGroupDtl.getSalesAmt();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal + objGroupDtl.getDiscAmt();
								totalGrandTotal += objGroupDtl.getGrandTotal();
								arrListSalesReport.add(obj);

							} else {
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup.get(i).entrySet().iterator()
										.next().getValue();
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// POSName
								obj.setStrField3(String.valueOf(objGroupDtl.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl.getSubTotal()));// subTotal
								obj.setStrField6(String.valueOf(objGroupDtl.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								totalSalesAmt += objGroupDtl.getSalesAmt();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal + objGroupDtl.getDiscAmt();
								totalGrandTotal += objGroupDtl.getGrandTotal();
								arrListSalesReport.add(obj);
							}
						}
					}
					try {
						BigDecimal bigtotalSalesAmt = new BigDecimal(totalSalesAmt);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListGroupWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("totalAmount", bigtotalSalesAmt);
						mapReturn.put("subTotal", subTotal);
						mapReturn.put("discountTotal", discountTotal);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())
									/ Double.parseDouble(bigtotalSalesAmt.toString())) * 100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "SubGroupWise":

				listColHeader.add("Sub Group Name");
				listColHeader.add("POS ");
				listColHeader.add("Quantity");
				listColHeader.add("sub Total ");
				listColHeader.add("Sales Amount");
				listColHeader.add("Discount");
				listColHeader.add("Sales (%)");
				colCount = 7;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");

					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}

					sbSqlQFileBill.append("SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
							+ " , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode"
							+ " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d " + " ,tblposmaster f "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
							+ " and b.strItemCode=d.strItemCode " + " and c.strSubGroupCode=d.strSubGroupCode ");

					sbSqlLiveBill.append(" SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity ) "
							+ " , sum( b.dblAmount )-sum(b.dblDiscountAmt), f.strPosName,'" + strUserCode
							+ "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode"
							+ " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d " + " ,tblposmaster f "
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and a.strClientCode=b.strClientCode "
							+ " and b.strItemCode=d.strItemCode " + " and c.strSubGroupCode=d.strSubGroupCode ");
					sqlModLive.append("select c.strSubGroupCode,c.strSubGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode "
							+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd c"
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode " + " and d.strSubGroupCode=c.strSubGroupCode "
							+ " and b.dblamount>0 " + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ");

					sqlModQFile.append("select c.strSubGroupCode,c.strSubGroupName"
							+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode
							+ "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode "
							+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
							+ ",tblsubgrouphd c"
							+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode and a.strClientCode=b.strClientCode "
							+ " and LEFT(b.strItemCode,7)=d.strItemCode " + " and d.strSubGroupCode=c.strSubGroupCode "
							+ " and b.dblamount>0 " + " and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate
							+ "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(
								" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" and a.strUserCreated='" + strOperator + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}

					if (ConsolidatePOS.equals("Y")) {
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " group by c.strSubGroupCode, c.strSubGroupName");
						} else {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "' "
									+ " group by c.strSubGroupCode, c.strSubGroupName");
						}
					} else {
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "'"
									+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode ");
						} else {
							sbFilters.append(" and " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "' "
									+ " group by c.strSubGroupCode, c.strSubGroupName, a.strPoscode");
						}
					}
					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);
					sqlModLive.append(sbFilters);
					sqlModQFile.append(sbFilters);

					mapPOSDtlForGroupSubGroup = new LinkedHashMap<>();
					subTotal = 0.00;
					discountTotal = 0.00;

					List listSubGroupWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					listSubGroupWiseSales = objBaseService.funGetList(sqlModLive, "sql");

					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					listSubGroupWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");

					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					listSubGroupWiseSales = objBaseService.funGetList(sqlModQFile, "sql");
					funGenerateSubGroupWiseSales(listSubGroupWiseSales);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					double salesAmt = 0;
					Iterator<Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>>> iteratorPOS = mapPOSDtlForGroupSubGroup
							.entrySet().iterator();
					while (iteratorPOS.hasNext()) {
						Map.Entry<String, List<Map<String, clsPOSGroupSubGroupWiseSales>>> entry = iteratorPOS.next();
						String posCode = entry.getKey();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = entry.getValue();
						for (int i = 0; i < listOfGroup.size(); i++) {
							if (ConsolidatePOS.equalsIgnoreCase("Y")) {
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup.get(i).entrySet().iterator()
										.next().getValue();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// pos
								obj.setStrField3(String.valueOf(objGroupDtl.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl.getSubTotal()));// subtotal
								obj.setStrField6(String.valueOf(objGroupDtl.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;

								totalQty = totalQty + objGroupDtl.getQty();
								temp1 = temp1.add(new BigDecimal(objGroupDtl.getSubTotal()));
								salesAmt += salesAmt + objGroupDtl.getSubTotal();
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal + objGroupDtl.getDiscAmt();
								arrListSalesReport.add(obj);
							} else {
								clsPOSSalesFlashReportsBean obj = new clsPOSSalesFlashReportsBean();
								clsPOSGroupSubGroupWiseSales objGroupDtl = listOfGroup.get(i).entrySet().iterator()
										.next().getValue();
								obj.setStrField1(objGroupDtl.getGroupName());// groupName
								obj.setStrField2(objGroupDtl.getPosName());// pos
								obj.setStrField3(String.valueOf(objGroupDtl.getQty()));// qty
								obj.setStrField4(String.valueOf(objGroupDtl.getSalesAmt()));// salesAmount
								obj.setStrField5(String.valueOf(objGroupDtl.getSubTotal()));// subtotal
								obj.setStrField6(String.valueOf(objGroupDtl.getDiscAmt()));// discAmt

								List DataList = new ArrayList<>();
								DataList.add(objGroupDtl.getGroupName());
								DataList.add(objGroupDtl.getPosName());
								DataList.add(objGroupDtl.getQty());
								DataList.add(objGroupDtl.getSalesAmt());
								DataList.add(objGroupDtl.getSubTotal());
								DataList.add(objGroupDtl.getDiscAmt());
								map.put(rowCount, DataList);
								rowCount++;
								totalQty = totalQty + objGroupDtl.getQty();
								temp1 = temp1.add(new BigDecimal(objGroupDtl.getSubTotal()));
								subTotal = subTotal + objGroupDtl.getSubTotal();
								discountTotal = discountTotal + objGroupDtl.getDiscAmt();
								arrListSalesReport.add(obj);
							}
						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListSubGroupWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("SalesAmt", temp1);
						mapReturn.put("subTotal", subTotal);
						mapReturn.put("discountTotal", discountTotal);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())
									/ Double.parseDouble(temp1.toString())) * 100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "CustWise":

				listColHeader.add("Customer Name");
				listColHeader.add("No Of Bills");
				listColHeader.add("Sales Amount");
				colCount = 3;
				if (reportType.equalsIgnoreCase("Item Wise")) {

					try {

						sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate)"
								+ ",c.strCustomerCode,c.strCustomerName,d.strItemName"
								+ ",sum(b.dblQuantity),sum(b.dblAmount),'" + strUserCode + "' "
								+ "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
								+ "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
								+ "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + Customer + "'"
								+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate)"
								+ ",c.strCustomerCode,c.strCustomerName,d.strItemName"
								+ ",sum(b.dblQuantity),sum(b.dblAmount),'" + strUserCode + "' "
								+ "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
								+ "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
								+ "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + Customer + "'"
								+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbFilters.append(" and a.strPOSCode='" + strPOSCode + "' ");
						}
						if (!strOperator.equals("All")) {
							sbFilters.append(" and  a.strUserCreated='" + strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbFilters.append(" and a.strSettelmentMode='" + strPayMode + "' ");
						}
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {

						} else {
							sbFilters.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
						}

						if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
						{
							sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
						}

						sbFilters.append(" group by a.strBillNo");

						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbFilters);
						sbSqlQFileBill.append(sbFilters);

						sql = "truncate table tbltempsalesflash1;";
						objBaseService.funExecuteUpdate(sql, "sql");

						String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
								+ ",strpaymode,dblsubtotal,struser) " + "(" + sbSqlLiveBill + ");";
						String sqlInsertQFileBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
								+ ",strpaymode,dblsubtotal,struser) " + "(" + sbSqlQFileBill + ");";
						objBaseService.funExecuteUpdate(sqlInsertLiveBillSales, "sql");
						objBaseService.funExecuteUpdate(sqlInsertQFileBillSales, "sql");

						sbSqlLiveBill.setLength(0);
						sbSqlLiveBill.append("select * from tbltempsalesflash1 where strUser='" + strUserCode + "'");
						List listSG = objBaseService.funGetList(sbSqlLiveBill, "sql");
						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);

								objsales.setStrField1(obj[0].toString());// Bill
																			// No
								String tempBillDate = obj[1].toString();
								String[] spDate = tempBillDate.split("-");
								String Billdate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];// Bill Date
								objsales.setStrField2(Billdate);
								objsales.setStrField3(obj[2].toString()); // Cust
																			// Code
								objsales.setStrField4(obj[3].toString());// Cust
																			// Name
								objsales.setStrField5(obj[4].toString());// Item
																			// Name
								objsales.setStrField6(obj[5].toString());// Qty
								objsales.setStrField7(obj[6].toString());// Amount

								List DataList = new ArrayList<>();
								DataList.add(obj[3].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());

								map.put(rowCount, DataList);
								rowCount++;
								qty += Double.parseDouble(obj[5].toString());
								amount += Double.parseDouble(obj[6].toString());
								arrListSalesReport.add(objsales);
							}
						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
							mapReturn.put("ListCustWiseSales", gsonarrTempListSalesReport);
							mapReturn.put("totalQty", qty);
							mapReturn.put("SalesAmt", amount);
							mapReturn.put("ColHeader", listColHeader);
							mapReturn.put("colCount", colCount);
							mapReturn.put("RowCount", rowCount);
							for (int tblRow = 0; tblRow < map.size(); tblRow++) {
								List listmap = (List) map.get(tblRow);
								// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/salesAmt)*100));
								mapReturn.put("" + tblRow, listmap);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (reportType.equalsIgnoreCase("Customer Wise")) {

					try {
						sql = "";

						sbSqlLiveBill.setLength(0);
						sbSqlQFileBill.setLength(0);
						sbFilters.setLength(0);

						sbSqlLiveBill.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
								+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00')" + ",'"
								+ strUserCode + "',b.longMobileNo,b.dteDOB " + "from tblbillhd a,tblcustomermaster b "
								+ "where a.strCustomerCode=b.strCustomerCode " + "and date(a.dteBillDate) between '"
								+ fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
								+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00')" + ",'"
								+ strUserCode + "',b.longMobileNo,b.dteDOB " + "from tblqbillhd a,tblcustomermaster b "
								+ "where a.strCustomerCode=b.strCustomerCode " + "and date(a.dteBillDate) between '"
								+ fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbFilters.append(" and a.strPOSCode='" + strPOSCode + "' ");
						}

						if (!strOperator.equals("All")) {
							sbFilters.append(" and  a.strUserCreated='" + strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbFilters.append(" and a.strSettelmentMode='" + strPayMode + "' ");
						}
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
						} else {
							sbFilters.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
						}
						
						if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
						{
							sbFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						}

						sbFilters.append(" GROUP BY b.strCustomerCode ");

						double grandTotal = 0;
						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbFilters);
						sbSqlQFileBill.append(sbFilters);

						sbSqlLiveBill.setLength(0);
						sbSqlLiveBill.append("select * from tbltempsalesflash1 where strUser='" + strUserCode + "'");

						List listSG = objBaseService.funGetList(sbSqlLiveBill, "sql");
						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								flgRecords = true;

								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);

								objsales.setStrField1(obj[0].toString()); // Cust
																			// Code
								objsales.setStrField2(obj[1].toString());// Cust
																			// Name
								objsales.setStrField3(obj[2].toString());// Count
								objsales.setStrField4(obj[3].toString());// Grand
																			// tot

								grandTotal += Double.parseDouble(obj[3].toString());
								arrListSalesReport.add(objsales);

							}
						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
							mapReturn.put("ListCustWiseSales", gsonarrTempListSalesReport);
							mapReturn.put("grandTotal", grandTotal);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {

					try {
						sbSqlLiveBill.setLength(0);
						sbSqlQFileBill.setLength(0);
						sbFilters.setLength(0);

						sbSqlLiveBill.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
								+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'" + strUserCode
								+ "' " + "from tblbillhd a,tblcustomermaster b "
								+ "where a.strCustomerCode=b.strCustomerCode " + "and date(a.dteBillDate) between '"
								+ fromDate + "' and '" + toDate + "'");

						sbSqlQFileBill.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
								+ ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'" + strUserCode
								+ "' " + "from tblqbillhd a,tblcustomermaster b "
								+ "where a.strCustomerCode=b.strCustomerCode " + "and date(a.dteBillDate) between '"
								+ fromDate + "' and '" + toDate + "'");

						if (!strPOSCode.equals("All")) {
							sbFilters.append(" and a.strPOSCode='" + strPOSCode + "' ");
						}
						if (!strOperator.equals("All")) {
							sbFilters.append(" and  a.strUserCreated='" + strOperator + "' ");
						}
						if (!strPayMode.equals("All")) {
							sbFilters.append(" and a.strSettelmentMode='" + strPayMode + "' ");
						}
						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
						} else {
							sbFilters.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
						}

						if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
						{
							sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
						}

						sbFilters.append(" GROUP BY b.strCustomerCode");
						double grandTotal = 0;
						double qty = 0, amount = 0;

						sbSqlLiveBill.append(sbFilters);
						sbSqlQFileBill.append(sbFilters);
						sql = "truncate table tbltempsalesflash1;";
						objBaseService.funExecuteUpdate(sql, "sql");

						String sqlInsertLiveBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) " + "(" + sbSqlLiveBill
								+ ");";
						String sqlInsertQFileBillSales = "insert into tbltempsalesflash1 "
								+ "(strbillno,dtebilldate,tmebilltime,strtablename,struser) " + "(" + sbSqlQFileBill
								+ ");";

						objBaseService.funExecuteUpdate(sqlInsertLiveBillSales, "sql");
						objBaseService.funExecuteUpdate(sqlInsertQFileBillSales, "sql");
						sbSqlLiveBill.setLength(0);

						sbSqlLiveBill.append("select * from tbltempsalesflash1 where strUser='" + strUserCode + "'");
						List listSG = objBaseService.funGetList(sbSqlLiveBill, "sql");

						arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
						int billCount = 0;

						if (listSG.size() > 0) {
							for (int i = 0; i < listSG.size(); i++) {
								flgRecords = true;

								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								Object[] obj = (Object[]) listSG.get(i);
								objsales.setStrField1(obj[1].toString());// Cust
																			// Name
								objsales.setStrField2(obj[2].toString());// count
								objsales.setStrField3(obj[3].toString());// Grand
																			// Total
								List DataList = new ArrayList<>();
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());

								map.put(rowCount, DataList);
								rowCount++;
								billCount += Integer.parseInt(obj[2].toString());
								grandTotal += Double.parseDouble(obj[3].toString());
								arrListSalesReport.add(objsales);
							}

						}
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
							}.getType();
							String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
							mapReturn.put("ListCustWiseSales", gsonarrTempListSalesReport);
							mapReturn.put("billCount", billCount);
							mapReturn.put("grandTotal", grandTotal);
							mapReturn.put("ColHeader", listColHeader);
							mapReturn.put("colCount", colCount);
							mapReturn.put("RowCount", rowCount);
							for (int tblRow = 0; tblRow < map.size(); tblRow++) {
								List listmap = (List) map.get(tblRow);
								// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/salesAmt)*100));
								mapReturn.put("" + tblRow, listmap);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case "WaiterWise":
				listColHeader.add("POS");
				listColHeader.add("Waiter Full Name");
				listColHeader.add("Waiter Short Name");
				listColHeader.add("Sales Amount");

				colCount = 4;
				try {

					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sqlModLive.setLength(0);
					sqlModQFile.setLength(0);
					sbFilters.setLength(0);

					sbSqlLiveBill.append("select c.strPosName,b.strWShortName,b.strWFullName"
							+ ",SUM(d.dblSettlementAmt),count(*),'" + strUserCode + "',b.strWaiterNo,c.strPosCode "
							+ " from tblbillhd a,tblwaitermaster b, tblposmaster c,tblbillsettlementdtl d "
							+ " where a.strWaiterNo=b.strWaiterNo " + " and a.strPOSCode=c.strPosCode "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and a.strBillNo=d.strBillNo " + " and date(a.dteBillDate)=date(d.dteBillDate) "
							+ " and a.strClientCode=d.strClientCode ");

					sbSqlQFileBill.append("select c.strPosName,b.strWShortName,b.strWFullName"
							+ ",SUM(d.dblSettlementAmt),count(*),'" + strUserCode + "',b.strWaiterNo,c.strPosCode "
							+ " from tblqbillhd a,tblwaitermaster b, tblposmaster c,tblqbillsettlementdtl d "
							+ " where a.strWaiterNo=b.strWaiterNo " + " and a.strPOSCode=c.strPosCode "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
							+ " and a.strBillNo=d.strBillNo " + " and date(a.dteBillDate)=date(d.dteBillDate) "
							+ " and a.strClientCode=d.strClientCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and a.strPOSCode='" + strPOSCode + "' ");

						sbSqlQFileBill.append(" and a.strPOSCode='" + strPOSCode + "' ");
					}
					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					

					sbSqlLiveBill.append(" group by a.strWaiterNo,a.strPOSCode");
					sbSqlQFileBill.append(" group by a.strWaiterNo,a.strPOSCode");
					mapPOSWaiterWiseSales = new LinkedHashMap<>();

					List listWaiterWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateWaiterWiseSales(listWaiterWiseSales);

					listWaiterWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateWaiterWiseSales(listWaiterWiseSales);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorWaiter = mapPOSWaiterWiseSales
							.entrySet().iterator();
					while (posIteratorWaiter.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = posIteratorWaiter.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> itemIterator = mapWaiterDtl.entrySet()
								.iterator();
						while (itemIterator.hasNext()) {
							clsPOSCommonBeanDtl objWaiterDtl = itemIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objWaiterDtl.getPosName());
							objsales.setStrField2(objWaiterDtl.getWaiterShortName());
							objsales.setStrField3(objWaiterDtl.getWaiterFullName());
							objsales.setStrField4(String.valueOf(objWaiterDtl.getSaleAmount()));

							arrListSalesReport.add(objsales);
							totalSale += objWaiterDtl.getSaleAmount();
							List DataList = new ArrayList<>();
							DataList.add(objWaiterDtl.getPosName());
							DataList.add(objWaiterDtl.getWaiterFullName());
							DataList.add(objWaiterDtl.getWaiterShortName());
							DataList.add(objWaiterDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListWaiterWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("TotalAmount", totalSale);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "DeliveryBoyWise":
				listColHeader.add("Delivery Boy Name");
				listColHeader.add("POS");
				listColHeader.add("Sales Amount");
				listColHeader.add("Delivery Charges");
				colCount = 4;
				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbSqlLiveBill.append("select a.strDPCode,b.strDPName,d.strPOSName"
							+ " ,sum(c.dblGrandTotal),sum(a.dblHomeDeliCharge),'" + strUserCode + "',a.strPOSCode "
							+ " from tblhomedelivery a,tbldeliverypersonmaster b,tblbillhd c, tblposmaster d "
							+ " WHERE a.strBillNo=c.strBillNo and a.strDPCode=b.strDPCode "
							+ " and c.strPOSCode=d.strPOSCode ");

					sbSqlQFileBill.append("select a.strDPCode,b.strDPName,d.strPOSName"
							+ " ,sum(c.dblGrandTotal),sum(a.dblHomeDeliCharge),'" + strUserCode + "',a.strPOSCode "
							+ " from tblhomedelivery a,tbldeliverypersonmaster b,tblqbillhd c, tblposmaster d "
							+ " WHERE a.strBillNo=c.strBillNo and a.strDPCode=b.strDPCode "
							+ " and c.strPOSCode=d.strPOSCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
						sbSqlQFileBill.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND c.intShiftCode = '" + strShiftNo + "' ");
					}
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND c.intShiftCode = '" + strShiftNo + "' ");
					}

					sbSqlLiveBill.append(" and date(a.dteDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
							+ " GROUP BY a.strDPCode");
					sbSqlQFileBill.append(" and date(a.dteDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
							+ " GROUP BY a.strDPCode");
					double totalAmt = 0;

					mapPOSDeliveryBoyWise = new LinkedHashMap<>();

					List listDeliveryBoyWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateDelBoyWiseSales(listDeliveryBoyWiseSales);

					listDeliveryBoyWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateDelBoyWiseSales(listDeliveryBoyWiseSales);

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorDelBoy = mapPOSDeliveryBoyWise
							.entrySet().iterator();
					while (posIteratorDelBoy.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = posIteratorDelBoy.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> itemIterator = mapDBDtl.entrySet().iterator();
						while (itemIterator.hasNext()) {
							clsPOSCommonBeanDtl objDBDtl = itemIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objDBDtl.getDbName());
							objsales.setStrField2(objDBDtl.getPosName());
							objsales.setStrField3(String.valueOf(objDBDtl.getSaleAmount()));
							objsales.setStrField4(String.valueOf(objDBDtl.getDelCharges()));
							arrListSalesReport.add(objsales);
							totalAmt += objDBDtl.getSaleAmount();
							List DataList = new ArrayList<>();
							DataList.add(objDBDtl.getDbName());
							DataList.add(objDBDtl.getPosName());
							DataList.add(objDBDtl.getSaleAmount());
							DataList.add(objDBDtl.getDelCharges());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListDelBoyWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("TotalAmount", totalAmt);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "CostCenterWise":
				listColHeader.add("Cost Center Name");
				listColHeader.add("POS");
				listColHeader.add("Quantity");
				listColHeader.add("Sub Total");
				listColHeader.add("Sales Amount");
				listColHeader.add("Discount");
				listColHeader.add("Sales (%)");
				colCount = 7;
				try {
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					if (field.equals("dteBillDate")) {
						field = "d.dteBillDate";
					} else {
						field = "date(d.dteBillDate)";
					}
					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sqlModLive.setLength(0);
					sqlModQFile.setLength(0);
					sbFilters.setLength(0);

					sbSqlLiveBill.append("SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum( c.dblAmount )-sum(c.dblDiscountAmt), e.strPOSName,'" + strUserCode + "' "
							+ ",c.dblRate  ,sum(c.dblAmount),sum(c.dblDiscountAmt),e.strPosCode  "
							+ " from tblbilldtl c left outer join tblbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode = c.strItemCode \n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sbSqlLiveBill.append(" and d.strAreaCode=b.strAreaCode ");
					}

					// QFile Sql
					sbSqlQFileBill.append("SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum( c.dblAmount )-sum(c.dblDiscountAmt), e.strPOSName,'" + strUserCode + "'"
							+ ",c.dblRate ,sum(c.dblAmount),sum(c.dblDiscountAmt),e.strPosCode "
							+ " from tblqbilldtl c left outer join tblqbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode = c.strItemCode \n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if ("Y".equals("Y")) {
						sbSqlQFileBill.append(" and d.strAreaCode=b.strAreaCode ");
					}

					sqlModLive.append("SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum(c.dblAmount)-sum(c.dblDiscAmt), e.strPOSName,'" + strUserCode + "'"
							+ ",c.dblRate ,sum( c.dblAmount ),sum(c.dblDiscAmt),e.strPosCode "
							+ " from tblbillmodifierdtl c left outer join tblbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode =LEFT(c.strItemCode,7)\n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.dblAmount>0");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sqlModLive.append(" and d.strAreaCode=b.strAreaCode ");
					}
					sqlModQFile.append("SELECT ifnull(a.strCostCenterCode,'ND')"
							+ ", ifnull(a.strCostCenterName,'ND') ,sum( c.dblQuantity )"
							+ " ,sum(c.dblAmount)-sum(c.dblDiscAmt), e.strPOSName,'" + strUserCode + "'"
							+ ",c.dblRate ,sum( c.dblAmount ),sum(c.dblDiscAmt),e.strPosCode "
							+ " from tblqbillmodifierdtl c left outer join tblqbillhd d on c.strBillNo = d.strBillNo "
							+ " and c.strClientCode=d.strClientCode "
							+ " left outer join tblposmaster e on d.strPOSCode = e.strPOSCode "
							+ " left outer join tblmenuitempricingdtl b on b.strItemCode =LEFT(c.strItemCode,7) \n"
							+ " and b.strposcode =d.strposcode\n"
							+ " left outer join tblcostcentermaster a on a.strCostCenterCode = b.strCostCenterCode\n"
							+ " where " + field + " BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and c.dblAmount>0");
					if (AreaWisePricing.equals("Y"))// clsGlobalVarClass.gAreaWisePricing
					{
						sqlModQFile.append(" and d.strAreaCode=b.strAreaCode ");
					}

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(
								" AND d.strPOSCode = '" + strPOSCode + "' and d.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND d.strPOSCode = '" + strPOSCode + "'");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" and d.strUserCreated='" + strOperator + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND d.intShiftCode = '" + strShiftNo + "' ");
					}

					if (strFromBill.length() == 0 && strToBill.length() == 0) {
						sbFilters.append(" GROUP BY b.strCostCenterCode,a.strCostCenterName, e.strPOSName,c.dblRate");
					} else {
						sbFilters.append(" and d.strBillNo between '" + strFromBill + "' and '" + strToBill + "' "
								+ "GROUP BY b.strCostCenterCode,a.strCostCenterName, e.strPOSName,c.dblRate");
					}

					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);
					sqlModLive.append(sbFilters);
					sqlModQFile.append(sbFilters);

					subTotal = 0.00;
					discountTotal = 0.00;

					mapPOSCostCenterWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					List listCostCenterWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					listCostCenterWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					listCostCenterWiseSales = objBaseService.funGetList(sqlModLive, "sql");
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					listCostCenterWiseSales = objBaseService.funGetList(sqlModQFile, "sql");
					funGenerateCostCenterWiseSales(listCostCenterWiseSales);

					double totalAmt = 0;
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorCost = mapPOSCostCenterWiseSales
							.entrySet().iterator();
					while (posIteratorCost.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = posIteratorCost.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> ccIterator = mapCCDtl.entrySet().iterator();
						while (ccIterator.hasNext()) {
							clsPOSCommonBeanDtl objCCDtl = ccIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objCCDtl.getCostCenterName());// ccName
							objsales.setStrField2(objCCDtl.getPosName());// posName
							objsales.setStrField3(String.valueOf(objCCDtl.getQty()));// Qty
							objsales.setStrField4(String.valueOf(objCCDtl.getSaleAmount()));// salesAmt
							objsales.setStrField5(String.valueOf(objCCDtl.getSubTotal()));// subTotal
							objsales.setStrField6(String.valueOf(objCCDtl.getDiscAmount()));// discAmt
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objCCDtl.getCostCenterName());
							DataList.add(objCCDtl.getPosName());
							DataList.add(objCCDtl.getQty());
							DataList.add(objCCDtl.getSaleAmount());
							DataList.add(objCCDtl.getSubTotal());
							DataList.add(objCCDtl.getDiscAmount());
							map.put(rowCount, DataList);
							rowCount++;

							totalQty = totalQty + objCCDtl.getQty();
							totalAmt = totalAmt + objCCDtl.getSaleAmount();
							subTotal = subTotal + objCCDtl.getSubTotal();
							discountTotal = discountTotal + objCCDtl.getDiscAmount();

						}
					}
					try {
						BigDecimal bigtotalAmt = new BigDecimal(totalAmt);
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListCostCentWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("totalAmt", bigtotalAmt);
						mapReturn.put("subTotal", subTotal);
						mapReturn.put("discountTotal", discountTotal);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())
									/ Double.parseDouble(bigtotalAmt.toString())) * 100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case "HomeDeliveryWise":
				listColHeader.add("Bill No");
				listColHeader.add("POS");
				listColHeader.add("Date");
				listColHeader.add("Settle Mode");
				listColHeader.add("Delivery Charges");
				listColHeader.add("Disc Amt");
				listColHeader.add("Tax Amt");
				listColHeader.add("Amount");
				listColHeader.add("Customer Name ");
				listColHeader.add("Bulding");
				listColHeader.add("Delv Boy");
				colCount = 11;
				try {
					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					BigDecimal sumDisc = new BigDecimal("0.00");
					BigDecimal sumtax = new BigDecimal("0.00");
					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);

					sbSqlLiveBill.append(
							"SELECT ifnull(a.strBillNo,''),ifnull(f.strPosName,''),ifnull(DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),''),ifnull(b.strSettelmentMode,'') "
									+ " ,ifnull(b.dblDeliveryCharges,'') ,ifnull(b.dblDiscountAmt,''),ifnull(b.dblTaxAmt,''),ifnull(b.dblGrandTotal,'') ,"
									+ " ifnull(c.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(d.strDPName,''),'"
									+ strUserCode + "' "
									+ " FROM tblhomedelivery a INNER JOIN tblbillhd b ON a.strBillNo = b.strBillNo "
									+ " INNER JOIN tblcustomermaster c ON a.strCustomerCode = c.strCustomerCode "
									+ " left OUTER Join tbldeliverypersonmaster d on a.strDPCode=d.strDPCode "
									+ " left OUTER Join tblbuildingmaster e on e.strBuildingCode=c.strBuldingCode"
									+ " left outer join tblposmaster f on b.strPOSCode=f.strPosCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" AND b.strPOSCode = '" + strPOSCode + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
					}

					sbSqlQFileBill.append(
							"SELECT ifnull(a.strBillNo,''),ifnull(f.strPosName,''),ifnull(DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),''),ifnull(b.strSettelmentMode,'') "
									+ " ,ifnull(b.dblDeliveryCharges,'') ,ifnull(b.dblDiscountAmt,''),ifnull(b.dblTaxAmt,''),ifnull(b.dblGrandTotal,'') ,"
									+ " ifnull(c.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(d.strDPName,''),'"
									+ strUserCode + "' "
									+ " FROM tblhomedelivery a INNER JOIN tblqbillhd b ON a.strBillNo = b.strBillNo "
									+ " INNER JOIN tblcustomermaster c ON a.strCustomerCode = c.strCustomerCode "
									+ " left OUTER Join tbldeliverypersonmaster d on a.strDPCode=d.strDPCode "
									+ " left OUTER Join tblbuildingmaster e on e.strBuildingCode=c.strBuldingCode "
									+ " left outer join tblposmaster f on b.strPOSCode=f.strPosCode "
									+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");
					if (!strPOSCode.equals("All")) {
						sbSqlQFileBill.append(" AND b.strPOSCode = '" + strPOSCode + "'");
					}

					sbSqlQFileBill.append(" AND b.intShiftCode = '" + strShiftNo + "' ");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listHomeDelWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					try {
						if (listHomeDelWiseSales.size() > 0) {
							for (int i = 0; i < listHomeDelWiseSales.size(); i++) {

								Object[] obj = (Object[]) listHomeDelWiseSales.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								objsales.setStrField1(obj[0].toString());// bilNo
								objsales.setStrField2(obj[1].toString());// posName
								objsales.setStrField3(obj[2].toString());// billDate
								objsales.setStrField4(obj[3].toString());// settleMode
								objsales.setStrField5(obj[4].toString());// delCharges
								objsales.setStrField6(obj[5].toString());// disc
								objsales.setStrField7(obj[6].toString());// taxAmt
								objsales.setStrField8(obj[7].toString());// totalAmt
								objsales.setStrField9(obj[8].toString());// custName
								objsales.setStrField10(obj[9].toString());// address
								objsales.setStrField11(obj[10].toString());// delBoy
								sumDisc = sumDisc.add(new BigDecimal(obj[5].toString()));
								sumtax = sumtax.add(new BigDecimal(obj[6].toString()));
								temp1 = temp1.add(new BigDecimal(obj[7].toString()));
								arrListSalesReport.add(objsales);

								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								DataList.add(obj[4].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());
								DataList.add(obj[7].toString());
								DataList.add(obj[8].toString());
								DataList.add(obj[9].toString());
								DataList.add(obj[10].toString());
								map.put(rowCount, DataList);
								rowCount++;

							}
						}
					} catch (Exception e) {

					}

					listHomeDelWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					try {
						if (listHomeDelWiseSales.size() > 0) {
							for (int i = 0; i < listHomeDelWiseSales.size(); i++) {
								Object[] obj = (Object[]) listHomeDelWiseSales.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								objsales.setStrField1(obj[0].toString());// bilNo
								objsales.setStrField2(obj[1].toString());// posName
								objsales.setStrField3(obj[2].toString());// billDate
								objsales.setStrField4(obj[3].toString());// settleMode
								objsales.setStrField5(obj[4].toString());// delCharges
								objsales.setStrField6(obj[5].toString());// disc
								objsales.setStrField7(obj[6].toString());// taxAmt
								objsales.setStrField8(obj[7].toString());// totalAmt
								objsales.setStrField9(obj[8].toString());// custName
								objsales.setStrField10(obj[9].toString());// address
								objsales.setStrField11(obj[10].toString());// delBoy
								sumDisc = sumDisc.add(new BigDecimal(obj[5].toString()));
								sumtax = sumtax.add(new BigDecimal(obj[6].toString()));
								temp1 = temp1.add(new BigDecimal(obj[7].toString()));
								arrListSalesReport.add(objsales);

								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								DataList.add(obj[4].toString());
								DataList.add(obj[5].toString());
								DataList.add(obj[6].toString());
								DataList.add(obj[7].toString());
								DataList.add(obj[8].toString());
								DataList.add(obj[9].toString());
								DataList.add(obj[10].toString());
								map.put(rowCount, DataList);
								rowCount++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListHomeDelWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("sumDisc", sumDisc);
						mapReturn.put("sumtax", sumtax);
						mapReturn.put("SalesAmt", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/totalAmt)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TableWise":
				listColHeader.add("POS ");
				listColHeader.add("Table Name");
				listColHeader.add("sales Amount");
				colCount = 3;
				totalQty = new Double("0.00");
				totalAmount = new BigDecimal("0.00");
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");

				sbSqlLiveBill.setLength(0);
				sbSqlQFileBill.setLength(0);
				try {

					sbSqlQFileBill.append("select c.strPOSName,b.strTableName,'0',SUM(d.dblSettlementAmt),count(*)"
							+ ",'" + strPOSCode + "','" + strUserCode + "','0' ,'ND','ND',a.strTableNo "
							+ " from tblqbillhd a,tbltablemaster b,tblposmaster c,tblqbillsettlementdtl d "
							+ " where date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and a.strTableNo=b.strTableNo " + " and a.strPOSCode=c.strPOSCode"
							+ " and a.strBillNo=d.strBillNo " + " and date(a.dteBillDate)=date(d.dteBillDate) "
							+ " and a.strClientCode=d.strClientCode ");

					sbSqlLiveBill.append("select c.strPOSName,b.strTableName,'0',SUM(d.dblSettlementAmt),count(*)"
							+ ",'" + strPOSCode + "','" + strUserCode + "','0' ,'ND','ND',a.strTableNo "
							+ " from tblbillhd a,tbltablemaster b,tblposmaster c,tblbillsettlementdtl d "
							+ " where date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
							+ " and a.strTableNo=b.strTableNo " + " and a.strPOSCode=c.strPOSCode "
							+ " and a.strBillNo=d.strBillNo " + " and date(a.dteBillDate)=date(d.dteBillDate) "
							+ " and a.strClientCode=d.strClientCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlQFileBill.append(" AND a.strPOSCode = '" + strPOSCode + "'");
						sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "'");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND a.intShiftCode = '" + strShiftNo + "'");
					}
					
					sbSqlLiveBill.append(" group by a.strTableNo ");
					sbSqlQFileBill.append(" group by a.strTableNo ");
					mapPOSTableWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

					List listTableWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateTableWiseSales(listTableWiseSales);

					listTableWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateTableWiseSales(listTableWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorTable = mapPOSTableWiseSales
							.entrySet().iterator();
					while (posIteratorTable.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = posIteratorTable.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> tblIterator = mapTblDtl.entrySet().iterator();
						while (tblIterator.hasNext()) {
							clsPOSCommonBeanDtl objTblDtl = tblIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(objTblDtl.getPosName());
							objsales.setStrField2(objTblDtl.getTableName());
							objsales.setStrField3(String.valueOf(objTblDtl.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String.valueOf(objTblDtl.getSaleAmount())));
							totalSale += objTblDtl.getSaleAmount();
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(objTblDtl.getPosName());
							DataList.add(objTblDtl.getTableName());
							DataList.add(objTblDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListTableWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("SalesAmt", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(3).toString())/totalAmt)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "HourlyWise":
				listColHeader.add("Date Range");
				listColHeader.add("On Of Bills Name");
				listColHeader.add("sales Amount");
				listColHeader.add("sales (%)");
				colCount = 4;
				sbSqlLiveBill.setLength(0);
				sbSqlQFileBill.setLength(0);
				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					sbSqlQFileBill
							.append("select left(right(a.dteDateCreated,8),2),left(right(a.dteDateCreated,8),2) +1"
									+ ",count(*),sum(b.dblSettlementAmt),'" + strPOSCode + "'  " + ",'" + strUserCode
									+ "','0' ,'ND','ND'  \n" + " from tblqbillhd a,tblqbillsettlementdtl b");

					sbSqlLiveBill.append("select left(right(a.dteDateCreated,8),2),left(right(a.dteDateCreated,8),2) +1"
							+ ",count(*),sum(b.dblSettlementAmt),'" + strPOSCode + "'  " + ",'" + strUserCode
							+ "','0' ,'ND','ND'  \n" + " from tblbillhd a,tblbillsettlementdtl b ");

					String EnableShiftYN = "Y";

					if (!strPOSCode.equals("All")) {

						if (EnableShiftYN.equalsIgnoreCase("Y")) {
							sbSqlQFileBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and a.strPOSCode='" + strPOSCode + "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'" + " AND a.intShiftCode = '" + strShiftNo
									+ "'  " + " Group By left(right(a.dteDateCreated,8),2)");

							sbSqlLiveBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode"
									+ " and a.strPOSCode='" + strPOSCode + "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' " + " AND a.intShiftCode = '" + strShiftNo
									+ "' " + " Group By left(right(a.dteDateCreated,8),2)");
						} else {
							sbSqlQFileBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "and a.strPOSCode='" + strPOSCode + "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "' "
									+ " Group By left(right(a.dteDateCreated,8),2)");

							sbSqlLiveBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ "and a.strPOSCode='" + strPOSCode + "' and date(a.dteBillDate) BETWEEN '"
									+ fromDate + "' AND '" + toDate + "'"
									+ " Group By left(right(a.dteDateCreated,8),2)");
						}
					} else {

						if (EnableShiftYN.equalsIgnoreCase("Y")) {
							sbSqlQFileBill.append("  WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " AND a.intShiftCode = '" + strShiftNo + "'  "
									+ " Group By left(right(a.dteDateCreated,8),2)");

							sbSqlLiveBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " AND a.intShiftCode = '" + strShiftNo + "' "
									+ " Group By left(right(a.dteDateCreated,8),2)");
						} else {
							sbSqlQFileBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " Group By left(right(a.dteDateCreated,8),2)");

							sbSqlLiveBill.append(" WHERE a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
									+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
									+ " Group By left(right(a.dteDateCreated,8),2)");
						}
					}

					mapPOSHourlyWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listHourWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					funGenerateHourlyWiseSales(listHourWiseSales);

					listHourWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateHourlyWiseSales(listHourWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorHour = mapPOSHourlyWiseSales
							.entrySet().iterator();
					while (posIteratorHour.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = posIteratorHour.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> hrsIterator = mapHrlyDtl.entrySet().iterator();
						while (hrsIterator.hasNext()) {
							clsPOSCommonBeanDtl objHrsDtl = hrsIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objHrsDtl.getStartHrs() + "-" + objHrsDtl.getEndHrs());
							objsales.setStrField2(String.valueOf(objHrsDtl.getNoOfBills()));
							objsales.setStrField3(String.valueOf(objHrsDtl.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String.valueOf(objHrsDtl.getSaleAmount())));
							// totalSale+=objHrsDtl.getSaleAmount();
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(objHrsDtl.getStartHrs() + "-" + objHrsDtl.getEndHrs());
							DataList.add(objHrsDtl.getNoOfBills());
							DataList.add(objHrsDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;

						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListHourWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("SalesAmt", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())
									/ Double.parseDouble(temp1.toString())) * 100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			// //

			case "AreaWise":
				try {

					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);

					if (field.equals("dteBillDate")) {
						field = "a.dteBillDate";
					} else {
						field = "date(a.dteBillDate)";
					}
					sbSqlQFileBill.append("select d.strPosName,c.strAreaName,'0', SUM(b.dblSettlementAmt),'"
							+ LoginPOSCode + "' " + " ,'" + strUserCode + "','0','ND','ND',a.strPosCode,a.strAreaCode "
							+ " from tblqbillhd a,tblqbillsettlementdtl b,tblareamaster c,tblposmaster d " + " where "
							+ field + " between '" + fromDate + "' and '" + toDate + "' "
							+ " and a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ " and a.strAreaCode=c.strAreaCode " + " and a.strPOSCode=d.strPosCode ");
					sbSqlLiveBill.append("select d.strPosName,c.strAreaName,'0', SUM(b.dblSettlementAmt),'"
							+ LoginPOSCode + "' " + " ,'" + strUserCode + "','0','ND','ND',a.strPosCode,a.strAreaCode "
							+ " from tblbillhd a,tblbillsettlementdtl b,tblareamaster c,tblposmaster d " + " where "
							+ field + " between '" + fromDate + "' and '" + toDate + "' "
							+ " and a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
							+ " and a.strAreaCode=c.strAreaCode and a.strPOSCode=d.strPosCode ");

					if (!strPOSCode.equals("All")) {
						sbSqlQFileBill.append(" and a.strPOSCode = '" + strPOSCode + "' ");
						sbSqlLiveBill.append(" and  a.strPOSCode = '" + strPOSCode + "' ");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					sbSqlQFileBill.append(" group by a.strAreaCode ");
					sbSqlLiveBill.append(" group by a.strAreaCode ");

					mapPOSAreaWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listAreaWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					funGenerateAreaWiseSales(listAreaWiseSales);
					listAreaWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");

					funGenerateAreaWiseSales(listAreaWiseSales);
					totalSale = 0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorArea = mapPOSAreaWiseSales
							.entrySet().iterator();
					while (posIteratorArea.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = posIteratorArea.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> areaIterator = mapAreaDtl.entrySet()
								.iterator();
						while (areaIterator.hasNext()) {
							clsPOSCommonBeanDtl objAreaDtl = areaIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objAreaDtl.getPosName());
							objsales.setStrField2(objAreaDtl.getAreaName());
							objsales.setStrField3(String.valueOf(objAreaDtl.getSaleAmount()));
							temp1 = temp1.add(new BigDecimal(String.valueOf(objAreaDtl.getSaleAmount())));
							// totalSale+=objAreaDtl.getSaleAmount();
							arrListSalesReport.add(objsales);

						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListAreaWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("SalesAmt", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "DayWiseSales":
				listColHeader.add("Bill date");
				listColHeader.add("No Of Bills");
				listColHeader.add("Sub Total ");
				listColHeader.add("Discount");
				listColHeader.add("Tax Amount");
				listColHeader.add("Grand Amount");
				colCount = 6;

				StringBuilder sbSqlForDiscount = new StringBuilder();
				sbSqlLiveBill.setLength(0);
				temp = new BigDecimal("0.00");
				temp1 = new BigDecimal("0.00");
				double totalDiscount = 0, totAmount = 0;
				totalSubTotalDWise = 0;
				totalTaxAmt = 0;
				int totalNoOfBills = 0;

				try {

					sbSqlLiveBill.setLength(0);
					sbSqlLiveBill.append(
							"select  DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),count(a.strBillNo),sum(a.dblSubTotal)"
									+ ",sum(a.dblDiscountAmt),sum(a.dblTaxAmt),'" + strUserCode
									+ "',date(a.dteBillDate) " + " from tblbillhd a "
									+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and a.strPOSCode='" + strPOSCode + "'");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					sbSqlLiveBill.append(" group by date(a.dteBillDate)");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listDayWise = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listDayWise.size() > 0) {
						for (int i = 0; i < listDayWise.size(); i++) {
							Object[] obj = (Object[]) listDayWise.get(i);
							double settlementAmt = 0;
							sbSqlForDiscount.setLength(0);
							sbSqlForDiscount.append("select sum(b.dblSettlementAmt) "
									+ " from tblbillhd a, tblbillsettlementdtl b " + " where a.strBillNo=b.strBillNo "
									+ " and date(a.dteBillDate) = '" + obj[6].toString() + "' ");
							if (!strPOSCode.equals("All")) {
								sbSqlForDiscount.append(" and a.strPOSCode='" + strPOSCode + "'");
							}

							
							if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
							{
								sbSqlForDiscount.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
							}
							sbSqlForDiscount.append(" group by date(a.dteBillDate)");

							List listDayWiseSt = objBaseService.funGetList(sbSqlForDiscount, "sql");

							if (listDayWiseSt.size() > 0) {
								// Object[] obj1 = (Object[])
								// listDayWiseSt.get(0);
								settlementAmt = Double.parseDouble(listDayWiseSt.get(0).toString());

							}
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString()); // day
							objsales.setStrField2(obj[1].toString()); // noOfBills
							objsales.setStrField3(obj[2].toString()); // subTotal
							objsales.setStrField4(obj[3].toString()); // disc
							objsales.setStrField5(obj[4].toString()); // tax
							objsales.setStrField6(String.valueOf(settlementAmt));// sales

							totalNoOfBills = totalNoOfBills + Integer.parseInt(obj[1].toString());
							totalSubTotalDWise = totalSubTotalDWise + Double.parseDouble(obj[2].toString());
							totalDiscount = totalDiscount + Double.parseDouble(obj[3].toString());
							totalTaxAmt = totalTaxAmt + Double.parseDouble(obj[4].toString());
							totAmount = totAmount + settlementAmt;
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(settlementAmt);
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					sbSqlLiveBill.setLength(0);
					sbSqlLiveBill.append(
							"select DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),count(a.strBillNo),sum(a.dblSubTotal)"
									+ ",sum(a.dblDiscountAmt),sum(a.dblTaxAmt),'" + strUserCode
									+ "',date(a.dteBillDate) " + " from tblqbillhd a "
									+ " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and a.strPOSCode='" + strPOSCode + "'");
					}
					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					sbSqlLiveBill.append(" group by date(a.dteBillDate)");

					listDayWise = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listDayWise.size() > 0) {
						for (int i = 0; i < listDayWise.size(); i++) {
							Object[] obj = (Object[]) listDayWise.get(i);
							double settlementAmt = 0;
							sbSqlForDiscount.setLength(0);
							sbSqlForDiscount.append("select sum(b.dblSettlementAmt) "
									+ " from tblqbillhd a, tblqbillsettlementdtl b " + " where a.strBillNo=b.strBillNo "
									+ " and date(a.dteBillDate) = '" + obj[6].toString() + "' ");
							if (!strPOSCode.equals("All")) {
								sbSqlForDiscount.append(" and a.strPOSCode='" + strPOSCode + "'");
							}
							if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
							{
								sbSqlForDiscount.append(" AND a.intShiftCode = '" + strShiftNo + "' ");	
							}

							sbSqlForDiscount.append(" group by date(a.dteBillDate)");

							List listDayWiseSt = objBaseService.funGetList(sbSqlForDiscount, "sql");

							if (listDayWiseSt.size() > 0) {
								settlementAmt = Double.parseDouble(listDayWiseSt.get(0).toString());

							}
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString()); // day
							objsales.setStrField2(obj[1].toString()); // noOfBills
							objsales.setStrField3(obj[2].toString()); // subTotal
							objsales.setStrField4(obj[3].toString()); // disc
							objsales.setStrField5(obj[4].toString()); // tax
							objsales.setStrField6(String.valueOf(settlementAmt));// sales

							totalNoOfBills = totalNoOfBills + Integer.parseInt(obj[1].toString());
							totalSubTotalDWise = totalSubTotalDWise + Double.parseDouble(obj[2].toString());
							totalDiscount = totalDiscount + Double.parseDouble(obj[3].toString());
							totalTaxAmt = totalTaxAmt + Double.parseDouble(obj[4].toString());
							totAmount = totAmount + settlementAmt;
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(settlementAmt);
							map.put(rowCount, DataList);
							rowCount++;
						}

					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListDayWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalNoOfBills", totalNoOfBills);
						mapReturn.put("totalSubTotal", totalSubTotalDWise);
						mapReturn.put("totalDiscount", totalDiscount);
						mapReturn.put("totalTaxAmt", totalTaxAmt);
						mapReturn.put("totAmount", totAmount);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TaxWiseSales":
				listColHeader.add("Bill No");
				listColHeader.add("Bill date");
				listColHeader.add("Tax Code");
				listColHeader.add("Tax Name ");
				listColHeader.add("Tax Percentage");
				listColHeader.add("Taxable Amount");
				listColHeader.add("Tax Amount");
				colCount = 7;
				try {
					String prevBillNo = "";
					double totalTax = 0, totalTaxableAmt = 0;
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					
					
					sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate),c.strTaxCode"
							+ " ,c.strTaxDesc,b.dblTaxableAmount,b.dblTaxAmount,c.dblPercent"
							+ " ,'" + strUserCode + "' "
							+ " from tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
							+ " where a.strBillNo=b.strBillNo  "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ " and b.strTaxCode=c.strTaxCode "
							+ " and a.strClientCode=b.strClientCode "
							+ " and b.strClientCode=c.strClientCode "
							+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

					sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate),c.strTaxCode"
							+ " ,c.strTaxDesc,b.dblTaxableAmount,b.dblTaxAmount,c.dblPercent "
							+ " ,'" + strUserCode + "' "
							+ " from tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c "
							+ " where a.strBillNo=b.strBillNo  "
							+ " and date(a.dteBillDate)=date(b.dteBillDate) "
							+ " and b.strTaxCode=c.strTaxCode "
							+ " and a.strClientCode=b.strClientCode "
							+ " and b.strClientCode=c.strClientCode "
							+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");

					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and a.strPOSCode='" + strPOSCode + "' ");
						sbSqlQFileBill.append(" and a.strPOSCode='" + strPOSCode + "' ");
					}
											
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");	
					}
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");	
					}

					sbSqlLiveBill.append(" order by a.strBillNo desc");
					sbSqlQFileBill.append(" order by a.strBillNo desc");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

					List listTaxWise = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listTaxWise.size() > 0) {
						for (int i = 0; i < listTaxWise.size(); i++) {
							Object[] obj = (Object[]) listTaxWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString());
							objsales.setStrField2(obj[1].toString());
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[6].toString());
							objsales.setStrField6(obj[4].toString());
							objsales.setStrField7(obj[5].toString());
							totalTax = totalTax + Double.parseDouble(obj[5].toString());
							if (!prevBillNo.equals(obj[0].toString())) {
								totalTaxableAmt = totalTaxableAmt + Double.parseDouble(obj[4].toString());
							}
							prevBillNo = obj[0].toString();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());

							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					// for day end
					listTaxWise = objBaseService.funGetList(sbSqlQFileBill, "sql");

					if (listTaxWise.size() > 0) {
						for (int i = 0; i < listTaxWise.size(); i++) {
							Object[] obj = (Object[]) listTaxWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString());
							objsales.setStrField2(obj[1].toString());
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[6].toString());
							objsales.setStrField6(obj[4].toString());
							objsales.setStrField7(obj[5].toString());
							totalTax = totalTax + Double.parseDouble(obj[5].toString());
							if (!prevBillNo.equals(obj[0].toString())) {
								totalTaxableAmt = totalTaxableAmt + Double.parseDouble(obj[4].toString());
							}
							prevBillNo = obj[0].toString();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());

							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListTaxWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalTaxableAmt", totalTaxableAmt);
						mapReturn.put("totalTax", totalTax);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "TipReport":
				listColHeader.add("Bill No");
				listColHeader.add("date");
				listColHeader.add("Bill Time");
				listColHeader.add("POS Code");
				listColHeader.add("Set Mode ");
				listColHeader.add("Discount %");
				listColHeader.add("Disc Amount");
				listColHeader.add("Sub Total");
				listColHeader.add("Tax Amount");
				listColHeader.add("Tip Amount");
				listColHeader.add("Sales Amount");
				colCount = 11;
				try {
					totalAmount = new BigDecimal("0.00");
					Disc = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					double tipAmountTotal = 0;

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);

					sbSqlLiveBill.append("select strBillNo,left(dteBillDate,10),left(right(dteDateCreated,8),5) as "
							+ "BillTime,strPOSCode,strSettelmentMode,dblDiscountPer,dblDiscountAmt,dblTaxAmt,"
							+ "dblSubTotal,dblTipAmount,dblGrandTotal,strUserCreated,strUserEdited,dteDateCreated,"
							+ "dteDateEdited,strClientCode,strTableNo,strWaiterNo,strCustomerCode from vqbillhd ");
					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlLiveBill.append(
								"where " + field + " between '" + fromDate + "' and '" + toDate + "' and strPOSCode='"
										+ strPOSCode + "' and strUserCreated='" + strOperator + "' and dblTipAmount>0");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlLiveBill.append("where " + field + " between '" + fromDate + "' and  strUserCreated='"
								+ strOperator + "' and dblTipAmount>0");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbSqlLiveBill.append(" where " + field + " between '" + fromDate + "' and '" + toDate
								+ "' and strPOSCode='" + strPOSCode + "' and dblTipAmount>0");
					} else if (strPOSCode.equals("All") && strOperator.equals("All")) {
						sbSqlLiveBill.append("where " + field + " between '" + fromDate + "' and '" + toDate
								+ "'  and dblTipAmount>0");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND intShiftCode = '" + strShiftNo + "' ");
					}
					if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
						sbSqlLiveBill.append(" order by strBillNo desc");
					} else {
						sbSqlLiveBill.append(" and strBillNo between '" + strFromBill + "' and '" + strToBill
								+ "' order by strBillNo desc");
					}

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listTipWise = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listTipWise.size() > 0) {
						for (int i = 0; i < listTipWise.size(); i++) {
							Object[] obj = (Object[]) listTipWise.get(i);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(obj[0].toString());
							String tempBillDate = obj[1].toString();
							String[] spDate = tempBillDate.split("-");
							objsales.setStrField2(spDate[2] + "-" + spDate[1] + "-" + spDate[0]);
							objsales.setStrField3(obj[2].toString());
							objsales.setStrField4(obj[3].toString());
							objsales.setStrField5(obj[4].toString());
							objsales.setStrField6(obj[5].toString());
							objsales.setStrField7(obj[6].toString());
							objsales.setStrField8(obj[8].toString());
							objsales.setStrField9(obj[7].toString());
							objsales.setStrField10(obj[9].toString());
							objsales.setStrField11(obj[10].toString());

							Disc = Disc.add(new BigDecimal(obj[6].toString()));
							temp = temp.add(new BigDecimal(obj[7].toString()));
							subTotal = subTotal + Double.parseDouble(obj[8].toString());
							temp1 = temp1.add(new BigDecimal(obj[10].toString()));
							tipAmountTotal = tipAmountTotal + Double.parseDouble(obj[9].toString());

							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString());
							DataList.add(spDate[2] + "-" + spDate[1] + "-" + spDate[0]);
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							DataList.add(obj[6].toString());
							DataList.add(obj[7].toString());
							DataList.add(obj[8].toString());
							DataList.add(obj[9].toString());
							DataList.add(obj[10].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListTipWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("Disc", Disc);
						mapReturn.put("totalTaxAmt", temp);
						mapReturn.put("subTotal", subTotal);
						mapReturn.put("SalesAmount", temp1);
						mapReturn.put("tipAmountTotal", tipAmountTotal);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemModifierWise":
				listColHeader.add("Modifier Name");
				listColHeader.add("POS");
				listColHeader.add("Quantity");
				listColHeader.add("Sales Amount");
				colCount = 4;
				try {

					sql = "";
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbFilters.setLength(0);

					sbSqlLiveBill.append("SELECT b.strModifierCode, b.strModifierName"
							+ " ,c.strPOSName, sum( b.dblQuantity ), sum( b.dblAmount )" + ",'" + strUserCode
							+ "',a.strposcode " + " FROM tblbillhd a, tblbillmodifierdtl b, tblposmaster c "
							+ " WHERE a.strbillno = b.strbillno and a.strClientCode=b.strClientCode and a.strposcode=c.strposcode "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

					sbSqlQFileBill.append("SELECT b.strModifierCode, b.strModifierName"
							+ " ,c.strPOSName, sum( b.dblQuantity ), sum( b.dblAmount ) " + ",'" + strUserCode
							+ "',a.strposcode " + " FROM tblqbillhd a, tblqbillmodifierdtl b, tblposmaster c "
							+ " WHERE a.strbillno = b.strbillno and a.strClientCode=b.strClientCode and a.strposcode=c.strposcode "
							+ " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='"
								+ strOperator.toString() + "' ");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(" AND a.strUserCreated='" + strOperator + "' ");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}

					if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
					} else {
						sbFilters.append(" and a.strbillno between '" + strFromBill + "' and '" + strToBill + "'");
					}
					sbFilters.append(" GROUP BY a.strposcode, b.strModifierCode, b.strModifierName ");

					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);

					mapPOSModifierWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listModWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					funGenerateModifierWiseSales(listModWiseSales);

					listModWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateModifierWiseSales(listModWiseSales);

					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorMod = mapPOSModifierWiseSales
							.entrySet().iterator();
					while (posIteratorMod.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = posIteratorMod.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> modiIterator = mapModiDtl.entrySet()
								.iterator();
						while (modiIterator.hasNext()) {
							clsPOSCommonBeanDtl objModiDtl = modiIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objModiDtl.getModiName());
							objsales.setStrField2(objModiDtl.getPosName());
							objsales.setStrField3(String.valueOf(objModiDtl.getQty()));
							objsales.setStrField4(String.valueOf(objModiDtl.getSaleAmount()));

							totalQty = totalQty + objModiDtl.getQty();
							temp1 = new BigDecimal(objModiDtl.getSaleAmount());
							totalAmount = totalAmount.add(temp1);
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objModiDtl.getModiName());
							DataList.add(objModiDtl.getPosName());
							DataList.add(objModiDtl.getQty());
							DataList.add(objModiDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListModWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("totalAmount", totalAmount);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MenuHeadWiseWithModifier":
				listColHeader.add("Menu Name");
				listColHeader.add("POS");
				listColHeader.add("Quantity");
				listColHeader.add("Sales Amount");
				colCount = 4;
				try {
					// StringBuilder sbSql = new StringBuilder();
					totalQty = new Double("0.00");
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");
					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					sbSqlLiveBill.setLength(0);
					sbSqlLiveBill.append("select count(*) from vqbillhd where date(dteBillDate) between '" + fromDate
							+ "' and '" + toDate + "'   ");

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND intShiftCode = '" + strShiftNo + "' ");
					}

					List listMenuHeadModWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
					int cnt = 0;
					if (listMenuHeadModWiseSales.size() > 0) {
						// System.out.println(listMenuHeadModWiseSales.get(0));
						cnt = Integer.parseInt(listMenuHeadModWiseSales.get(0).toString());
						// System.out.println(cnt);
					}

					if (cnt > 0) {
						sbSqlLiveBill.setLength(0);
						sbSqlLiveBill.append(
								"select d.strMenuName,e.strPosName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscountAmt),sum(b.dblAmount),d.strMenuCode "
										+ " from tblbillhd a,tblbilldtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strMenuCode=d.strMenuCode "
										+ " and a.strPOSCode=e.strPosCode and a.strPOSCode=c.strPosCode ");
						if (AreaWisePricing.equals("Y")) {
							sbSqlLiveBill.append(" and a.strAreaCode=c.strAreaCode ");
						}
						if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
							sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='"
									+ strOperator + "'");
						} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
							sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "'");
						} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
							sbSqlLiveBill.append(" and a.strUserCreated='" + strOperator + "'");
						}

						
						if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
						{
							sbSqlLiveBill.append(" AND intShiftCode = '" + strShiftNo + "' ");
						}

						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
							sbSqlLiveBill.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
									+ "'" + " group by c.strMenuCode,a.strPOSCode ");
						} else {
							sbSqlLiveBill.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
									+ "' AND a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						}

						listMenuHeadModWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
						// System.out.println(sbSql.toString());
						if (listMenuHeadModWiseSales.size() > 0) {
							for (int i = 0; i < listMenuHeadModWiseSales.size(); i++) {
								Object[] obj = (Object[]) listMenuHeadModWiseSales.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
								sqlModLive.setLength(0);
								sqlModLive.append(" select d.strMenuName,e.strPosName,sum(b.dblQuantity) "
										+ " ,sum(b.dblAmount)-sum(b.dblDiscAmt),sum(b.dblAmount) "
										+ " from tblbillhd a,tblbillmodifierdtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and left(b.strItemCode,7)=c.strItemCode "
										+ " and c.strMenuCode=d.strMenuCode and a.strPOSCode=e.strPosCode "
										+ " and a.strPOSCode=c.strPosCode  and a.strAreaCode=c.strAreaCode "
										+ " and b.dblAmount>0 and c.strMenuCode='" + obj[5].toString() + "' "
										+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");

								
								if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
								{
									sqlModLive.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
								}

								List listModifier = objBaseService.funGetList(sqlModLive, "sql");

								double temp_Modifier_Qty = 0.00;
								double temp_Modifier_Amt = 0.00;
								if (listModifier.size() > 0) {
									for (int k = 0; k < listModifier.size(); k++) {

										Object[] ob = (Object[]) listMenuHeadModWiseSales.get(k);
										temp_Modifier_Qty += Double.parseDouble(ob[2].toString());
										temp_Modifier_Amt += Double.parseDouble(ob[3].toString());
									}
								}
								objsales.setStrField1(obj[0].toString());
								objsales.setStrField2(obj[1].toString());
								objsales.setStrField3(
										String.valueOf((Double.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								objsales.setStrField4(String.valueOf(new BigDecimal(obj[3].toString())
										.add(new BigDecimal(String.valueOf(temp_Modifier_Amt)))));

								temp_Modifier_Qty = 0.00;
								temp_Modifier_Amt = 0.00;
								totalQty = totalQty + new Double(
										String.valueOf((Double.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								temp1 = temp1.add(new BigDecimal(String.valueOf(new BigDecimal(obj[3].toString())
										.add(new BigDecimal(String.valueOf(temp_Modifier_Amt))))));

								arrListSalesReport.add(objsales);
								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								map.put(rowCount, DataList);
								rowCount++;
							}

						}
						sbSqlLiveBill.setLength(0);
						sbSqlLiveBill.append(
								"select d.strMenuName,e.strPosName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscountAmt)"
										+ " ,sum(b.dblAmount),d.strMenuCode "
										+ " from tblqbillhd a,tblqbilldtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode and c.strMenuCode=d.strMenuCode "
										+ " and a.strPOSCode=e.strPosCode and a.strPOSCode=c.strPosCode ");
						if (AreaWisePricing.equals("Y")) {
							sbSqlLiveBill.append(" and a.strAreaCode=c.strAreaCode ");
						}
						if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
							sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='"
									+ strOperator + "'");
						} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
							sbSqlLiveBill.append(" AND a.strPOSCode = '" + strPOSCode + "'");
						} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
							sbSqlLiveBill.append(" and a.strUserCreated='" + strOperator + "'");
						}

						
						if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
						{
							sbSqlLiveBill.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
						}

						if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {

							sbSqlLiveBill.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
									+ "'" + " group by c.strMenuCode,a.strPOSCode ");
						} else {
							sbSqlLiveBill.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate
									+ "' " + " AND a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'"
									+ " group by c.strMenuCode,a.strPOSCode ");
						}

						// System.out.println(sbSql.toString());
						listMenuHeadModWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");
						// System.out.println(sbSql.toString());
						if (listMenuHeadModWiseSales.size() > 0) {
							for (int i = 0; i < listMenuHeadModWiseSales.size(); i++) {
								Object[] obj = (Object[]) listMenuHeadModWiseSales.get(i);
								clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

								sqlModLive.setLength(0);
								sqlModLive.append(" select d.strMenuName,e.strPosName,sum(b.dblQuantity) "
										+ " ,sum(b.dblAmount)-sum(b.dblDiscAmt),sum(b.dblAmount) "
										+ " from tblqbillhd a,tblqbillmodifierdtl b,tblmenuitempricingdtl c,tblmenuhd d,tblposmaster e "
										+ " where a.strBillNo=b.strBillNo and left(b.strItemCode,7)=c.strItemCode "
										+ " and c.strMenuCode=d.strMenuCode and a.strPOSCode=e.strPosCode "
										+ " and a.strPOSCode=c.strPosCode and a.strAreaCode=c.strAreaCode "
										+ " and b.dblAmount>0 and c.strMenuCode='" + obj[5].toString() + "' "
										+ " and date(a.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");

								
								if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
								{
									sqlModLive.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
								}

								List listModifier = objBaseService.funGetList(sqlModLive, "sql");

								double temp_Modifier_Qty = 0.00;
								double temp_Modifier_Amt = 0.00;

								if (listModifier.size() > 0) {
									for (int k = 0; k < listModifier.size(); k++) {

										Object[] ob = (Object[]) listMenuHeadModWiseSales.get(k);
										temp_Modifier_Qty += Double.parseDouble(ob[2].toString());
										temp_Modifier_Amt += Double.parseDouble(ob[3].toString());
									}
								}
								objsales.setStrField1(obj[0].toString());
								objsales.setStrField2(obj[1].toString());
								objsales.setStrField3(
										String.valueOf((Double.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								objsales.setStrField4(String.valueOf(new BigDecimal(obj[3].toString())
										.add(new BigDecimal(String.valueOf(temp_Modifier_Amt)))));

								temp_Modifier_Qty = 0.00;
								temp_Modifier_Amt = 0.00;
								totalQty = totalQty + new Double(
										String.valueOf((Double.parseDouble(obj[2].toString()) + temp_Modifier_Qty)));
								temp1 = temp1.add(new BigDecimal(String.valueOf(new BigDecimal(obj[3].toString())
										.add(new BigDecimal(String.valueOf(temp_Modifier_Amt))))));

								arrListSalesReport.add(objsales);
								List DataList = new ArrayList<>();
								DataList.add(obj[0].toString());
								DataList.add(obj[1].toString());
								DataList.add(obj[2].toString());
								DataList.add(obj[3].toString());
								map.put(rowCount, DataList);
								rowCount++;

							}

						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListMenuHeadModWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalQty", totalQty);
						mapReturn.put("totalAmount", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "ItemHourlyWise":
				listColHeader.add("Time Range");
				listColHeader.add("Item Name");
				listColHeader.add("Quantity");
				listColHeader.add("Item Amount");
				listColHeader.add("Discount");
				colCount = 5;
				try {

					StringBuilder sbSqlModLiveBill = new StringBuilder();
					StringBuilder sbSqlQModFileBill = new StringBuilder();
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sqlModLive.setLength(0);
					sqlModQFile.setLength(0);

					sbSqlLiveBill.append("select left(right(b.dteDateCreated,8),2)"
							+ " ,left(right(b.dteDateCreated,8),2)+1,a.strItemName,sum(a.dblQuantity),"
							+ " sum(a.dblAmount)-sum(a.dblDiscountAmt) as Total,"
							+ " sum(a.dblDiscountAmt) as Discount,'" + strUserCode + "' "
							+ " from tblbilldtl a,tblbillhd b,tblitemmaster c "
							+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strItemCode=c.strItemCode "
							+ " and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlLiveBill.append(" and strPOSCode='" + strPOSCode + "' ");
					}

					
					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlLiveBill.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
					}
					sbSqlLiveBill.append(" group by a.strItemName");

					sbSqlQFileBill.append("select left(right(b.dteDateCreated,8),2)"
							+ " ,left(right(b.dteDateCreated,8),2)+1,a.strItemName,sum(a.dblQuantity),"
							+ " sum(a.dblAmount)-sum(a.dblDiscountAmt) as Total,"
							+ " sum(a.dblDiscountAmt) as Discount,'" + strUserCode + "' "
							+ " from tblqbilldtl a,tblqbillhd b,tblitemmaster c "
							+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strItemCode=c.strItemCode "
							+ " and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sbSqlQFileBill.append(" and strPOSCode='" + strPOSCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlQFileBill.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
					}
					sbSqlQFileBill.append(" group by a.strItemName");

					sqlModLive.append("select left(right(b.dteDateCreated,8),2)"
							+ " , left(right(b.dteDateCreated,8),2)+1,a.strModifierName,sum(a.dblQuantity),"
							+ " sum(a.dblAmount)-sum(a.dblDiscAmt) as Total," + " sum(a.dblDiscAmt) as Discount,'"
							+ strUserCode + "' " + " from tblbillmodifierdtl a,tblbillhd b,tblitemmaster c "
							+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and Left(a.strItemCode,7)=c.strItemCode "
							+ " and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sqlModLive.append(" and strPOSCode='" + strPOSCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sqlModLive.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
						
					}
					sqlModLive.append(" group by a.strModifierName");

					sqlModQFile.append("select left(right(b.dteDateCreated,8),2)"
							+ " , left(right(b.dteDateCreated,8),2)+1,a.strModifierName,sum(a.dblQuantity),"
							+ " sum(a.dblAmount)-sum(a.dblDiscAmt) as Total," + " sum(a.dblDiscAmt) as Discount,'"
							+ strUserCode + "' " + " from tblqbillmodifierdtl a,tblqbillhd b,tblitemmaster c "
							+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and Left(a.strItemCode,7)=c.strItemCode "
							+ " and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
					if (!strPOSCode.equals("All")) {
						sqlModQFile.append(" and strPOSCode='" + strPOSCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sqlModQFile.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
					}
					sqlModQFile.append(" group by a.strModifierName");

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();

					List listItemHourlyWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-" + obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-" + obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;

						}
					}
					listItemHourlyWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-" + obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-" + obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					listItemHourlyWiseSales = objBaseService.funGetList(sqlModLive, "sql");
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-" + obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-" + obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					listItemHourlyWiseSales = objBaseService.funGetList(sqlModQFile, "sql");
					if (listItemHourlyWiseSales.size() > 0) {
						for (int k = 0; k < listItemHourlyWiseSales.size(); k++) {

							Object[] obj = (Object[]) listItemHourlyWiseSales.get(k);
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							objsales.setStrField1(obj[0].toString() + "-" + obj[1].toString());
							objsales.setStrField2(obj[2].toString());
							objsales.setStrField3(obj[3].toString());
							objsales.setStrField4(obj[4].toString());
							objsales.setStrField5(obj[5].toString());
							temp = temp.add(new BigDecimal(obj[3].toString()));
							temp1 = temp1.add(new BigDecimal(obj[4].toString()));
							arrListSalesReport.add(objsales);
							List DataList = new ArrayList<>();
							DataList.add(obj[0].toString() + "-" + obj[1].toString());
							DataList.add(obj[2].toString());
							DataList.add(obj[3].toString());
							DataList.add(obj[4].toString());
							DataList.add(obj[5].toString());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}

					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListItemHourlyWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalAmount", temp);
						mapReturn.put("totalDisc", temp1);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "OperatorWise":
				listColHeader.add("Operator Code");
				listColHeader.add("Operator  Name");
				listColHeader.add("POS");
				listColHeader.add("Payment Mode");
				listColHeader.add("Discount Amount");
				listColHeader.add("Sales Amount");
				colCount = 6;
				mapOperatorDtls = new HashMap<String, List<clsPOSOperatorDtl>>();
				StringBuilder sbSqlDisLive = new StringBuilder();
				StringBuilder sbSqlQDisFile = new StringBuilder();

				try {
					sql = "";
					totalAmount = new BigDecimal("0.00");
					temp = new BigDecimal("0.00");
					temp1 = new BigDecimal("0.00");

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbSqlDisLive.setLength(0);
					sbSqlQDisFile.setLength(0);
					sbFilters.setLength(0);
					sbSqlDisFilters.setLength(0);

					sbSqlLiveBill.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
							+ " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
							+ " FROM tbluserhd a " + " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
							+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
							+ " inner join tblbillsettlementdtl d on b.strBillNo=d.strBillNo and b.strClientCode=d.strClientCode "
							+ " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
							+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					sbSqlQFileBill.append(" SELECT a.strUserCode, a.strUserName, c.strPOSName,e.strSettelmentDesc "
							+ " ,sum(d.dblSettlementAmt),'SANGUINE',c.strPosCode, d.strSettlementCode "
							+ " FROM tbluserhd a " + " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
							+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
							+ " inner join tblqbillsettlementdtl d on b.strBillNo=d.strBillNo and b.strClientCode=d.strClientCode "
							+ " inner join tblsettelmenthd e on d.strSettlementCode=e.strSettelmentCode "
							+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(
								" AND b.strPOSCode = '" + strPOSCode + "' and b.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND b.strPOSCode = '" + strPOSCode + "'");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append("  and b.strUserCreated='" + strOperator + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
						
					}
					sbFilters.append(" GROUP BY a.strUserCode, b.strPosCode, d.strSettlementCode");

					sbSqlLiveBill.append(sbFilters);
					sbSqlQFileBill.append(sbFilters);

					Map<String, Map<String, clsPOSOperatorDtl>> hmOperatorWiseSales = new HashMap<String, Map<String, clsPOSOperatorDtl>>();
					Map<String, clsPOSOperatorDtl> hmSettlementDtl = null;
					clsPOSOperatorDtl objOperatorWiseSales = null;

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listOperatorWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {

							Object[] obj = (Object[]) listOperatorWiseSales.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0].toString())) {
								hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
								if (hmSettlementDtl.containsKey(obj[7].toString())) {
									objOperatorWiseSales = hmSettlementDtl.get(obj[7].toString());
									objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt()
											+ Double.parseDouble(obj[4].toString()));
								} else {
									objOperatorWiseSales = new clsPOSOperatorDtl();
									objOperatorWiseSales.setStrUserCode(obj[0].toString());
									objOperatorWiseSales.setStrUserName(obj[1].toString());
									objOperatorWiseSales.setStrPOSName(obj[2].toString());
									objOperatorWiseSales.setStrSettlementDesc(obj[3].toString());
									objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[4].toString()));
									objOperatorWiseSales.setStrPOSCode(obj[6].toString());
									objOperatorWiseSales.setDiscountAmt(0);
								}
								hmSettlementDtl.put(obj[7].toString(), objOperatorWiseSales);
							} else {
								objOperatorWiseSales = new clsPOSOperatorDtl();
								objOperatorWiseSales.setStrUserCode(obj[0].toString());
								objOperatorWiseSales.setStrUserName(obj[1].toString());
								objOperatorWiseSales.setStrPOSName(obj[2].toString());
								objOperatorWiseSales.setStrSettlementDesc(obj[3].toString());
								objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[4].toString()));
								objOperatorWiseSales.setStrPOSCode(obj[6].toString());
								objOperatorWiseSales.setDiscountAmt(0);

								hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
								hmSettlementDtl.put(obj[7].toString(), objOperatorWiseSales);
							}
							hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
						}

					}

					listOperatorWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {

							Object[] obj = (Object[]) listOperatorWiseSales.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0].toString())) {
								hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
								if (hmSettlementDtl.containsKey(obj[7].toString())) {
									objOperatorWiseSales = hmSettlementDtl.get(obj[7].toString());
									objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt()
											+ Double.parseDouble(obj[4].toString()));
								} else {
									objOperatorWiseSales = new clsPOSOperatorDtl();
									objOperatorWiseSales.setStrUserCode(obj[0].toString());
									objOperatorWiseSales.setStrUserName(obj[1].toString());
									objOperatorWiseSales.setStrPOSName(obj[2].toString());
									objOperatorWiseSales.setStrSettlementDesc(obj[3].toString());
									objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[4].toString()));
									objOperatorWiseSales.setStrPOSCode(obj[6].toString());
									objOperatorWiseSales.setDiscountAmt(0);
								}
								hmSettlementDtl.put(obj[7].toString(), objOperatorWiseSales);
							} else {
								objOperatorWiseSales = new clsPOSOperatorDtl();
								objOperatorWiseSales.setStrUserCode(obj[0].toString());
								objOperatorWiseSales.setStrUserName(obj[1].toString());
								objOperatorWiseSales.setStrPOSName(obj[2].toString());
								objOperatorWiseSales.setStrSettlementDesc(obj[3].toString());
								objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[4].toString()));
								objOperatorWiseSales.setStrPOSCode(obj[6].toString());
								objOperatorWiseSales.setDiscountAmt(0);

								hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
								hmSettlementDtl.put(obj[7].toString(), objOperatorWiseSales);
							}
							hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
						}
					}

					sbSqlDisLive.append("SELECT a.strUserCode, a.strUserName, c.strPOSName"
							+ " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode " + " FROM tbluserhd a "
							+ " INNER JOIN tblbillhd b ON a.strUserCode = b.strUserCreated "
							+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
							+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					sbSqlQDisFile.append("  SELECT a.strUserCode, a.strUserName, c.strPOSName "
							+ " ,sum(b.dblDiscountAmt),'SANGUINE',c.strPosCode " + " FROM tbluserhd a "
							+ " INNER JOIN tblqbillhd b ON a.strUserCode = b.strUserCreated "
							+ " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
							+ " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlDisFilters.append(
								" AND b.strPOSCode = '" + strPOSCode + "' and b.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbSqlDisFilters.append(" AND b.strPOSCode = '" + strPOSCode + "'");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbSqlDisFilters.append("  and b.strUserCreated='" + strOperator.toString() + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbSqlDisFilters.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
						
					}
					sbSqlDisFilters.append(" GROUP BY a.strUserCode, b.strPosCode");

					sbSqlDisLive.append(sbSqlDisFilters);
					sbSqlQDisFile.append(sbSqlDisFilters);

					listOperatorWiseSales = objBaseService.funGetList(sbSqlDisLive, "sql");
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {
							Object[] obj = (Object[]) listOperatorWiseSales.get(k);
							if (hmOperatorWiseSales.containsKey(obj[0].toString())) {
								hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
								Set<String> setKeys = hmSettlementDtl.keySet();
								for (String keys : setKeys) {
									objOperatorWiseSales = hmSettlementDtl.get(keys);
									objOperatorWiseSales.setDiscountAmt(objOperatorWiseSales.getDiscountAmt()
											+ Double.parseDouble(obj[3].toString()));
									hmSettlementDtl.put(keys, objOperatorWiseSales);
									break;
								}
								hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
							}
						}

					}

					listOperatorWiseSales = objBaseService.funGetList(sbSqlQDisFile, "sql");
					if (listOperatorWiseSales.size() > 0) {
						for (int k = 0; k < listOperatorWiseSales.size(); k++) {
							Object[] obj = (Object[]) listOperatorWiseSales.get(k);

							if (hmOperatorWiseSales.containsKey(obj[0].toString())) {
								hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
								Set<String> setKeys = hmSettlementDtl.keySet();
								for (String keys : setKeys) {
									objOperatorWiseSales = hmSettlementDtl.get(keys);
									objOperatorWiseSales.setDiscountAmt(objOperatorWiseSales.getDiscountAmt()
											+ Double.parseDouble(obj[3].toString()));
									hmSettlementDtl.put(keys, objOperatorWiseSales);
									break;
								}
								hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
							}
						}
					}

					double discAmt = 0, totalAmt = 0;
					// Object[] arrObjTableRowData=new Object[6];
					for (Map.Entry<String, Map<String, clsPOSOperatorDtl>> entry : hmOperatorWiseSales.entrySet()) {
						Map<String, clsPOSOperatorDtl> hmOpSettlementDtl = entry.getValue();
						for (Map.Entry<String, clsPOSOperatorDtl> entryOp : hmOpSettlementDtl.entrySet()) {
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();
							clsPOSOperatorDtl objOperatorDtl = entryOp.getValue();

							objsales.setStrField1(objOperatorDtl.getStrUserCode());// userCode
							objsales.setStrField2(objOperatorDtl.getStrUserName());// userName
							objsales.setStrField3(objOperatorDtl.getStrPOSName());// posName
							objsales.setStrField4(objOperatorDtl.getStrSettlementDesc());// payMode
							objsales.setStrField5(String.valueOf(objOperatorDtl.getDiscountAmt()));// disc
							objsales.setStrField6(String.valueOf(objOperatorDtl.getSettleAmt()));// saleAmt
							discAmt += objOperatorDtl.getDiscountAmt();
							totalAmt += objOperatorDtl.getSettleAmt();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objOperatorDtl.getStrUserCode());
							DataList.add(objOperatorDtl.getStrUserName());
							DataList.add(objOperatorDtl.getStrPOSName());
							DataList.add(objOperatorDtl.getStrSettlementDesc());
							DataList.add(objOperatorDtl.getDiscountAmt());
							DataList.add(objOperatorDtl.getSettleAmt());

							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListOperatorWiseSales", gsonarrTempListSalesReport);
						mapReturn.put("totalAmount", totalAmt);
						mapReturn.put("totalDisc", discAmt);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "MonthlySalesFlash":
				listColHeader.add(" Month");
				listColHeader.add("Year");
				listColHeader.add("Total Sales");
				colCount = 3;
				StringBuilder sqlLiveData = new StringBuilder();
				StringBuilder sqlQData = new StringBuilder();
				try {
					// 2016-11-5
					fromDate = fromDate.substring(5, 7);
					toDate = toDate.substring(5, 7);
					Date objDate = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
					fromDate = String.valueOf(objDate.getMonth() + 1);
					objDate = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
					// toDate = String.valueOf(objDate.getMonth() + 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {

					sbSqlLiveBill.setLength(0);
					sbSqlQFileBill.setLength(0);
					sbFilters.setLength(0);

					sbSqlLiveBill
							.append("SELECT c.strPOSName, MONTHNAME(DATE(a.dteBillDate)), YEAR(DATE(a.dteBillDate))"
									+ " ,sum(d.dblSettlementAmt),sum(a.dblGrandTotal),a.strPOSCode"
									+ " ,month(a.dteBillDate) "
									+ " FROM tblbillhd a,tblsettelmenthd b,tblposmaster c,tblbillsettlementdtl d "
									+ " WHERE d.strSettlementCode=b.strSettelmentCode AND a.strBillNo = d.strBillNo "
									+ " AND a.strPOSCode=c.strPOSCode and a.strClientCode=d.strClientCode "
									+ " AND MONTH(DATE(a.dteBillDate)) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					sbSqlQFileBill
							.append("SELECT c.strPOSName, MONTHNAME(DATE(a.dteBillDate)), YEAR(DATE(a.dteBillDate))"
									+ " ,sum(d.dblSettlementAmt),sum(a.dblGrandTotal),a.strPOSCode"
									+ " ,month(a.dteBillDate) "
									+ " FROM tblqbillhd a,tblsettelmenthd b,tblposmaster c,tblqbillsettlementdtl d\n"
									+ " WHERE d.strSettlementCode=b.strSettelmentCode AND a.strBillNo = d.strBillNo "
									+ " AND a.strPOSCode=c.strPOSCode and a.strClientCode=d.strClientCode "
									+ " AND MONTH(DATE(a.dteBillDate)) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

					if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append(
								" AND a.strPOSCode = '" + strPOSCode + "' and a.strUserCreated='" + strOperator + "'");
					} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
						sbFilters.append(" AND a.strPOSCode = '" + strPOSCode + "'");
					} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
						sbFilters.append("  and a.strUserCreated='" + strOperator + "'");
					}

					if (strFromBill.length() == 0 && strToBill.trim().length() == 0) {
					} else {
						sbFilters.append(" and a.strBillNo between '" + strFromBill + "' and '" + strToBill + "'");
					}

					if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					{
						sbFilters.append(" AND a.intShiftCode = '" + strShiftNo + "' ");
					}
					sbFilters.append(" GROUP BY a.strPOSCode, MONTHNAME(DATE(a.dteBillDate)) ");

					sbSqlLiveBill.append(" ").append(sbFilters);
					sbSqlQFileBill.append(" ").append(sbFilters);

					mapPOSMonthWiseSales = new LinkedHashMap<String, Map<String, clsPOSCommonBeanDtl>>();

					arrListSalesReport = new ArrayList<clsPOSSalesFlashReportsBean>();
					List listMonthlyWiseSales = objBaseService.funGetList(sbSqlLiveBill, "sql");

					Object[] arrObj = (Object[]) listMonthlyWiseSales.get(0);
					String a = arrObj[3].toString();
					System.out.println(Double.parseDouble(a));

					funGenerateMonthWiseSales(listMonthlyWiseSales);

					listMonthlyWiseSales = objBaseService.funGetList(sbSqlQFileBill, "sql");
					funGenerateMonthWiseSales(listMonthlyWiseSales);

					Double total = 0.0;
					Iterator<Map.Entry<String, Map<String, clsPOSCommonBeanDtl>>> posIteratorMonth = mapPOSMonthWiseSales
							.entrySet().iterator();
					while (posIteratorMonth.hasNext()) {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = posIteratorMonth.next().getValue();
						Iterator<Map.Entry<String, clsPOSCommonBeanDtl>> monthIterator = mapMonthDtl.entrySet()
								.iterator();
						while (monthIterator.hasNext()) {
							clsPOSCommonBeanDtl objMonthDtl = monthIterator.next().getValue();
							clsPOSSalesFlashReportsBean objsales = new clsPOSSalesFlashReportsBean();

							objsales.setStrField1(objMonthDtl.getMonthName());// Monthname
							objsales.setStrField2(objMonthDtl.getYear());// year
							objsales.setStrField3(String.valueOf(objMonthDtl.getSaleAmount()));// totalamt
							total += objMonthDtl.getSaleAmount();
							arrListSalesReport.add(objsales);

							List DataList = new ArrayList<>();
							DataList.add(objMonthDtl.getMonthName());
							DataList.add(objMonthDtl.getYear());
							DataList.add(objMonthDtl.getSaleAmount());
							map.put(rowCount, DataList);
							rowCount++;
						}
					}
					try {
						Gson gson = new Gson();
						Type type = new TypeToken<List<clsPOSSalesFlashReportsBean>>() {
						}.getType();
						String gsonarrTempListSalesReport = gson.toJson(arrListSalesReport, type);
						mapReturn.put("ListMonthlySales", gsonarrTempListSalesReport);
						mapReturn.put("totalSale", total);
						mapReturn.put("ColHeader", listColHeader);
						mapReturn.put("colCount", colCount);
						mapReturn.put("RowCount", rowCount);
						for (int tblRow = 0; tblRow < map.size(); tblRow++) {
							List listmap = (List) map.get(tblRow);
							// listmap.add(decimalFormat.format((Double.parseDouble(listmap.get(2).toString())/totalSale)*100));
							mapReturn.put("" + tblRow, listmap);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			/*
			 * default: throw new IllegalArgumentException("  report name not match");
			 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapReturn;

	}

	private void funGenerateSettlementWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[0].toString();
					String posName = obj[2].toString();
					String settlementCode = obj[1].toString();
					String settlementDesc = obj[3].toString();
					double settlementAmt = Double.parseDouble(obj[4].toString());
					String settlementType = obj[6].toString();

					if (mapPOSDtlForSettlement.containsKey(posCode)) {
						List<Map<String, clsPOSBillSettlementDtl>> listOfSettlement = mapPOSDtlForSettlement
								.get(posCode);
						boolean isSettlementExists = false;
						int settlementIndex = 0;
						for (int j = 0; j < listOfSettlement.size(); j++) {
							if (listOfSettlement.get(j).containsKey(settlementCode)) {
								isSettlementExists = true;
								settlementIndex = j;
								break;
							}
						}
						if (isSettlementExists) {
							Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = listOfSettlement
									.get(settlementIndex);
							clsPOSBillSettlementDtl objBillSettlementDtl = mapSettlementCodeDtl.get(settlementCode);
							objBillSettlementDtl.setStrSettlementCode(settlementCode);
							objBillSettlementDtl
									.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt() + settlementAmt);
							objBillSettlementDtl.setPosName(posName);
							TotSale = TotSale + settlementAmt;
						} else {
							Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
							clsPOSBillSettlementDtl objBillSettlementDtl = new clsPOSBillSettlementDtl(settlementCode,
									settlementDesc, settlementAmt, posName, settlementType);
							mapSettlementCodeDtl.put(settlementCode, objBillSettlementDtl);
							listOfSettlement.add(mapSettlementCodeDtl);
							TotSale = TotSale + settlementAmt;
						}
					} else {
						List<Map<String, clsPOSBillSettlementDtl>> listOfSettelment = new ArrayList<>();
						Map<String, clsPOSBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
						clsPOSBillSettlementDtl objBillSettlementDtl = new clsPOSBillSettlementDtl(settlementCode,
								settlementDesc, settlementAmt, posName, settlementType);
						mapSettlementCodeDtl.put(settlementCode, objBillSettlementDtl);
						listOfSettelment.add(mapSettlementCodeDtl);
						TotSale = TotSale + settlementAmt;
						mapPOSDtlForSettlement.put(posCode, listOfSettelment);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateItemWiseSales(List list, String fromDate, String toDate, String strPOSCode,
			String strShiftNo, String strUserCode, String field, String strPayMode, String strOperator,
			String strFromBill, String strToBill, String reportType, String Type, String Customer,
			String ConsolidatePOS, String ReportName) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String itemCode = obj[0].toString();// itemCode
					String itemName = obj[1].toString();// itemName
					String posName = obj[2].toString();// posName
					double qty = Double.parseDouble(obj[3].toString());// qty
					double salesAmt = Double.parseDouble(obj[7].toString());// salesAmount
					double subTotal = Double.parseDouble(obj[5].toString());// subTotal
					double discAmt = Double.parseDouble(obj[8].toString());// discount
					String date = obj[9].toString();// date
					String posCode = obj[10].toString();// posCode

					String compare = itemCode;
					if (itemCode.contains("M")) {
						compare = itemName;
					} else {
						compare = itemCode;
					}

					if (mapPOSItemDtl.containsKey(posCode)) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
						if (mapItemDtl.containsKey(compare)) {
							clsPOSBillItemDtl objItemDtl = mapItemDtl.get(compare);
							objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
							objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
							objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
							objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
						} else {
							clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(date, itemCode, itemName, qty,
									salesAmt, discAmt, posName, subTotal);
							mapItemDtl.put(compare, objItemDtl);
						}
					} else {
						Map<String, clsPOSBillItemDtl> mapItemDtl = new LinkedHashMap<>();
						clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(date, itemCode, itemName, qty, salesAmt,
								discAmt, posName, subTotal);
						mapItemDtl.put(compare, objItemDtl);
						mapPOSItemDtl.put(posCode, mapItemDtl);
					}

					if (!itemCode.contains("M")) {
						funCreateModifierQuery(itemCode, fromDate, toDate, strPOSCode, strShiftNo, strUserCode, field,
								strPayMode, strOperator, strFromBill, strToBill, reportType, Type, Customer,
								ConsolidatePOS, ReportName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funCreateModifierQuery(String itemCode, String fromDate, String toDate, String strPOSCode,
			String strShiftNo, String strUserCode, String field, String strPayMode, String strOperator,
			String strFromBill, String strToBill, String reportType, String Type, String Customer,
			String ConsolidatePOS, String ReportName) {
		try {
			String enableShiftYN="N";
			StringBuilder sqlModLive = new StringBuilder();
			StringBuilder sqlModQFile = new StringBuilder();
			StringBuilder sbFilters = new StringBuilder();
			sqlModLive.append("select a.strItemCode,a.strModifierName,c.strPOSName"
					+ ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
					+ ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
					+ "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ "and left(a.strItemCode,7)='" + itemCode + "' ");

			// String pos = funGetSelectedPosCode();
			if (!strPOSCode.equals("All") && !strOperator.equals("All")) {
				sbFilters
						.append(" AND b.strPOSCode = '" + strPOSCode + "' and b.strUserCreated='" + strOperator + "' ");
			} else if (!strPOSCode.equals("All") && strOperator.equals("All")) {
				sbFilters.append(" AND b.strPOSCode = '" + strPOSCode + "' ");
			} else if (strPOSCode.equals("All") && !strOperator.equals("All")) {
				sbFilters.append(" AND b.strUserCreated='" + strOperator.toString() + "' ");
			}
			if (strFromBill.length() == 0 && strToBill.length() == 0) {

			} else {
				sbFilters.append(" and a.strbillno between '" + strFromBill + "' " + " and '" + strToBill + "'");
			}

			
			if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sbFilters.append(" AND b.intShiftCode = '" + strShiftNo + "' ");
			}

			sbFilters.append(" group by a.strItemCode,a.strModifierName,c.strPOSName  " + " order by b.dteBillDate ");

			sqlModLive.append(" " + sbFilters);

			List listModLive = objBaseService.funGetList(sqlModLive, "sql");

			funGenerateItemWiseSales(listModLive, fromDate, toDate, strPOSCode, strShiftNo, strUserCode, field,
					strPayMode, strOperator, strFromBill, strToBill, reportType, Type, Customer, ConsolidatePOS,
					ReportName);

			/*
			 * ResultSet rs=clsGlobalVarClass.dbMysql.executeResultSet(sqlModLive
			 * .toString()); funGenerateItemWiseSales(rs);
			 */

			// qmodifiers
			sqlModQFile.append("select a.strItemCode,a.strModifierName,c.strPOSName"
					+ ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + strUserCode + "' "
					+ ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
					+ "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ "and left(a.strItemCode,7)='" + itemCode + "' ");

			sqlModQFile.append(" " + sbFilters);

			List listModLiveQ = objBaseService.funGetList(sqlModQFile, "sql");
			funGenerateItemWiseSales(listModLiveQ, fromDate, toDate, strPOSCode, strShiftNo, strUserCode, field,
					strPayMode, strOperator, strFromBill, strToBill, reportType, Type, Customer, ConsolidatePOS,
					ReportName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateMenuHeadWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[9].toString();// posCode
					String posName = obj[4].toString();// posName
					String menuCode = obj[0].toString();// menuCode
					String menuName = obj[1].toString();// menuName
					double qty = Double.parseDouble(obj[2].toString());// qty
					double salesAmt = Double.parseDouble(obj[7].toString());// salesAmt
					double subTotal = Double.parseDouble(obj[3].toString());// subTotal
					double discAmt = Double.parseDouble(obj[8].toString());// disc

					if (mapPOSMenuHeadDtl.containsKey(posCode)) {
						Map<String, clsPOSBillItemDtl> mapItemDtl = mapPOSMenuHeadDtl.get(posCode);
						if (mapItemDtl.containsKey(menuCode)) {
							clsPOSBillItemDtl objItemDtl = mapItemDtl.get(menuCode);
							objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
							objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
							objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
							objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
						} else {
							clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(qty, salesAmt, discAmt, posName,
									subTotal, menuCode, menuName);
							mapItemDtl.put(menuCode, objItemDtl);
						}
					} else {
						Map<String, clsPOSBillItemDtl> mapItemDtl = new LinkedHashMap<>();
						clsPOSBillItemDtl objItemDtl = new clsPOSBillItemDtl(qty, salesAmt, discAmt, posName, subTotal,
								menuCode, menuName);
						mapItemDtl.put(menuCode, objItemDtl);
						mapPOSMenuHeadDtl.put(posCode, mapItemDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateGroupWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					if (mapPOSDtlForGroupSubGroup.containsKey(obj[9].toString()))// posCode
					{
						String posCode = obj[9].toString();
						String groupCode = obj[0].toString();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = mapPOSDtlForGroupSubGroup
								.get(posCode);
						boolean isGroupExists = false;
						int groupIndex = 0;
						for (int j = 0; j < listOfGroup.size(); j++) {
							if (listOfGroup.get(j).containsKey(groupCode)) {
								isGroupExists = true;
								groupIndex = j;
								break;
							}
						}
						if (isGroupExists) {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = listOfGroup.get(groupIndex);
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroupCodeDtl.get(groupCode);
							objGroupCodeDtl.setGroupCode(obj[0].toString());
							objGroupCodeDtl.setGroupName(obj[1].toString());
							objGroupCodeDtl.setPosName(obj[4].toString());
							objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
							objGroupCodeDtl
									.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[3].toString()));
							objGroupCodeDtl
									.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[7].toString()));
							objGroupCodeDtl
									.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
							objGroupCodeDtl.setGrandTotal(
									objGroupCodeDtl.getGrandTotal() + Double.parseDouble(obj[10].toString()));
						} else {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
									obj[0].toString(), obj[1].toString(), obj[4].toString(),
									Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),
									Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()),
									Double.parseDouble(obj[10].toString()));
							mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
							listOfGroup.add(mapGroupCodeDtl);
						}
					} else {
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroupDtl = new ArrayList<>();
						Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
						clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
								obj[0].toString(), obj[1].toString(), obj[4].toString(),
								Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),
								Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()),
								Double.parseDouble(obj[10].toString()));
						mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
						listOfGroupDtl.add(mapGroupCodeDtl);
						mapPOSDtlForGroupSubGroup.put(obj[9].toString(), listOfGroupDtl);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateSubGroupWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					if (mapPOSDtlForGroupSubGroup.containsKey(obj[9].toString()))// posCode
					{
						String posCode = obj[9].toString();
						String groupCode = obj[0].toString();
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroup = mapPOSDtlForGroupSubGroup
								.get(posCode);

						boolean isGroupExists = false;
						int groupIndex = 0;
						for (int j = 0; j < listOfGroup.size(); j++) {
							if (listOfGroup.get(j).containsKey(groupCode)) {
								isGroupExists = true;
								groupIndex = j;
								break;
							}
						}
						if (isGroupExists) {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = listOfGroup.get(groupIndex);
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = mapGroupCodeDtl.get(groupCode);
							objGroupCodeDtl.setGroupCode(obj[0].toString());
							objGroupCodeDtl.setGroupName(obj[1].toString());
							objGroupCodeDtl.setPosName(obj[4].toString());
							objGroupCodeDtl.setQty(objGroupCodeDtl.getQty() + Double.parseDouble(obj[2].toString()));
							objGroupCodeDtl
									.setSubTotal(objGroupCodeDtl.getSubTotal() + Double.parseDouble(obj[3].toString()));
							objGroupCodeDtl
									.setSalesAmt(objGroupCodeDtl.getSalesAmt() + Double.parseDouble(obj[7].toString()));
							objGroupCodeDtl
									.setDiscAmt(objGroupCodeDtl.getDiscAmt() + Double.parseDouble(obj[8].toString()));
							objGroupCodeDtl.setGrandTotal(objGroupCodeDtl.getGrandTotal() + 0.00);
						} else {
							Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
							clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
									obj[0].toString(), obj[1].toString(), obj[4].toString(),
									Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),
									Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), 0.00);
							mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
							listOfGroup.add(mapGroupCodeDtl);
						}
					} else {
						List<Map<String, clsPOSGroupSubGroupWiseSales>> listOfGroupDtl = new ArrayList<>();
						Map<String, clsPOSGroupSubGroupWiseSales> mapGroupCodeDtl = new LinkedHashMap<>();
						clsPOSGroupSubGroupWiseSales objGroupCodeDtl = new clsPOSGroupSubGroupWiseSales(
								obj[0].toString(), obj[1].toString(), obj[4].toString(),
								Double.parseDouble(obj[2].toString()), Double.parseDouble(obj[3].toString()),
								Double.parseDouble(obj[7].toString()), Double.parseDouble(obj[8].toString()), 0.00);
						mapGroupCodeDtl.put(obj[0].toString(), objGroupCodeDtl);
						listOfGroupDtl.add(mapGroupCodeDtl);
						mapPOSDtlForGroupSubGroup.put(obj[9].toString(), listOfGroupDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateWaiterWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String waiterCode = obj[6].toString();// waiterNo
					String waiterShortName = obj[1].toString();// waiterShortName
					String waiterFullName = obj[2].toString();// waiterFullName
					String posCode = obj[7].toString();// posCode
					String posName = obj[0].toString();// posName
					double salesAmount = Double.parseDouble(obj[3].toString());// salesAmount
					double noOfBills = Double.parseDouble(obj[4].toString());// bills

					if (mapPOSWaiterWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = mapPOSWaiterWiseSales.get(posCode);
						if (mapWaiterDtl.containsKey(waiterCode)) {
							clsPOSCommonBeanDtl objWaiterDtl = mapWaiterDtl.get(waiterCode);
							objWaiterDtl.setNoOfBills(objWaiterDtl.getNoOfBills() + noOfBills);
							objWaiterDtl.setSaleAmount(objWaiterDtl.getSaleAmount() + salesAmount);
						} else {
							clsPOSCommonBeanDtl objWaiterDtl = new clsPOSCommonBeanDtl(posCode, posName, waiterCode,waiterShortName, waiterFullName, salesAmount, noOfBills);
							mapWaiterDtl.put(waiterCode, objWaiterDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapWaiterDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objWaiterDtl = new clsPOSCommonBeanDtl(posCode, posName, waiterCode,waiterShortName, waiterFullName, salesAmount, noOfBills);
						mapWaiterDtl.put(waiterCode, objWaiterDtl);
						mapPOSWaiterWiseSales.put(posCode, mapWaiterDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateHourlyWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String startHour = obj[0].toString();// startHour
					String endHour = obj[1].toString();// endHour
					double noOfBills = Double.parseDouble(obj[2].toString());
					double saleAmount = Double.parseDouble(obj[3].toString());

					if (mapPOSHourlyWiseSales.containsKey(startHour)) {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = mapPOSHourlyWiseSales.get(startHour);
						if (mapHrlyDtl.containsKey(startHour)) {
							clsPOSCommonBeanDtl objHrlyDtl = mapHrlyDtl.get(startHour);

							objHrlyDtl.setNoOfBills(objHrlyDtl.getNoOfBills() + noOfBills);
							objHrlyDtl.setSaleAmount(objHrlyDtl.getSaleAmount() + saleAmount);
						} else {
							clsPOSCommonBeanDtl objHrlyDtl = new clsPOSCommonBeanDtl(saleAmount, startHour, endHour,
									noOfBills);
							mapHrlyDtl.put(startHour, objHrlyDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapHrlyDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objHrlyDtl = new clsPOSCommonBeanDtl(saleAmount, startHour, endHour,
								noOfBills);
						mapHrlyDtl.put(startHour, objHrlyDtl);
						mapPOSHourlyWiseSales.put(startHour, mapHrlyDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateDelBoyWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String dbCode = obj[0].toString(); // dbCode
					String dbName = obj[1].toString();// dbName
					String posCode = obj[6].toString();// posCode
					String posName = obj[2].toString();// posName
					double salesAmount = Double.parseDouble(obj[3].toString());// salesAmount
					double delCharges = Double.parseDouble(obj[4].toString());// delCharges

					if (mapPOSDeliveryBoyWise.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = mapPOSDeliveryBoyWise.get(posCode);
						if (mapDBDtl.containsKey(dbCode)) {
							clsPOSCommonBeanDtl objDelBoyDtl = mapDBDtl.get(dbCode);
							objDelBoyDtl.setSaleAmount(objDelBoyDtl.getSaleAmount() + salesAmount);
							objDelBoyDtl.setDelCharges(objDelBoyDtl.getDelCharges() + delCharges);
						} else {
							clsPOSCommonBeanDtl objDBDtl = new clsPOSCommonBeanDtl(posCode, posName, salesAmount,
									dbCode, dbName, delCharges);
							mapDBDtl.put(dbCode, objDBDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapDBDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objDBDtl = new clsPOSCommonBeanDtl(posCode, posName, salesAmount, dbCode,
								dbName, delCharges);
						mapDBDtl.put(dbCode, objDBDtl);
						mapPOSDeliveryBoyWise.put(posCode, mapDBDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateCostCenterWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String costCenterCode = obj[0].toString();// ccCode
					String costCenterName = obj[1].toString();// ccName
					String posCode = obj[9].toString();// posCode
					String posName = obj[4].toString();// posName
					double qty = Double.parseDouble(obj[2].toString());// qty
					double subTotal = Double.parseDouble(obj[3].toString());// subTotal
					double salesAmount = Double.parseDouble(obj[7].toString());// salesAmount
					double discAmt = Double.parseDouble(obj[8].toString());// disc

					if (mapPOSCostCenterWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = mapPOSCostCenterWiseSales.get(posCode);
						if (mapCCDtl.containsKey(costCenterCode)) {
							clsPOSCommonBeanDtl objCCDtl = mapCCDtl.get(costCenterCode);

							objCCDtl.setQty(objCCDtl.getQty() + qty);
							objCCDtl.setSubTotal(objCCDtl.getSubTotal() + subTotal);
							objCCDtl.setSaleAmount(objCCDtl.getSaleAmount() + salesAmount);
							objCCDtl.setDiscAmount(objCCDtl.getDiscAmount() + discAmt);
						} else {
							clsPOSCommonBeanDtl objCCDtl = new clsPOSCommonBeanDtl(posCode, posName, qty, salesAmount,
									subTotal, costCenterCode, costCenterName, discAmt);
							mapCCDtl.put(costCenterCode, objCCDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapCCDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objCCDtl = new clsPOSCommonBeanDtl(posCode, posName, qty, salesAmount,
								subTotal, costCenterCode, costCenterName, discAmt);
						mapCCDtl.put(costCenterCode, objCCDtl);

						mapPOSCostCenterWiseSales.put(posCode, mapCCDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateTableWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String posName = obj[0].toString();// posName
					String tableName = obj[1].toString();// tableName
					double saleAmount = Double.parseDouble(obj[3].toString());// salesAmount
					double noOfBills = Double.parseDouble(obj[4].toString());// Bills
					String posCode = obj[5].toString();// posCode
					String tableNo = obj[10].toString();// tableNo

					if (mapPOSTableWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = mapPOSTableWiseSales.get(posCode);
						if (mapTblDtl.containsKey(tableNo)) {
							clsPOSCommonBeanDtl objTblDtl = mapTblDtl.get(tableNo);
							objTblDtl.setNoOfBills(objTblDtl.getNoOfBills() + noOfBills);
							objTblDtl.setSaleAmount(objTblDtl.getSaleAmount() + saleAmount);
						} else {
							clsPOSCommonBeanDtl objTblDtl = new clsPOSCommonBeanDtl(posCode, posName, saleAmount,
									tableNo, noOfBills, tableName);
							mapTblDtl.put(tableNo, objTblDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapTblDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objTblDtl = new clsPOSCommonBeanDtl(posCode, posName, saleAmount, tableNo,
								noOfBills, tableName);
						mapTblDtl.put(tableNo, objTblDtl);

						mapPOSTableWiseSales.put(posCode, mapTblDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateAreaWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[9].toString();// posCode
					String areaCode = obj[10].toString();// areaCode
					String posName = obj[0].toString();// posName
					String areaName = obj[1].toString();// areaName
					double saleAmount = Double.parseDouble(obj[3].toString());

					if (mapPOSAreaWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = mapPOSAreaWiseSales.get(posCode);
						if (mapAreaDtl.containsKey(areaCode)) {
							clsPOSCommonBeanDtl objAreaDtl = mapAreaDtl.get(areaCode);
							objAreaDtl.setSaleAmount(objAreaDtl.getSaleAmount() + saleAmount);
						} else {
							clsPOSCommonBeanDtl objAreaDtl = new clsPOSCommonBeanDtl(posCode, posName, areaCode,
									areaName, saleAmount);
							mapAreaDtl.put(areaCode, objAreaDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapAreaDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objAreaDtl = new clsPOSCommonBeanDtl(posCode, posName, areaCode, areaName,
								saleAmount);
						mapAreaDtl.put(areaCode, objAreaDtl);
						mapPOSAreaWiseSales.put(posCode, mapAreaDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateModifierWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);

					String posCode = obj[6].toString();// posCode
					String posName = obj[2].toString();// posName
					String modiCode = obj[0].toString();// modiCode
					String modiName = obj[1].toString();// modiName
					double qty = Double.parseDouble(obj[3].toString());// qty
					double saleAmount = Double.parseDouble(obj[4].toString());// saleAmount

					if (mapPOSModifierWiseSales.containsKey(posCode)) {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = mapPOSModifierWiseSales.get(posCode);
						if (mapModiDtl.containsKey(modiName)) {
							clsPOSCommonBeanDtl objModiDtl = mapModiDtl.get(modiName);
							objModiDtl.setQty(objModiDtl.getQty() + qty);
							objModiDtl.setSaleAmount(objModiDtl.getSaleAmount() + saleAmount);
						} else {
							clsPOSCommonBeanDtl objModiDtl = new clsPOSCommonBeanDtl(posCode, posName, qty, saleAmount,
									modiCode, modiName);
							mapModiDtl.put(modiName, objModiDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapModiDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objModiDtl = new clsPOSCommonBeanDtl(posCode, posName, qty, saleAmount,
								modiCode, modiName);
						mapModiDtl.put(modiName, objModiDtl);
						mapPOSModifierWiseSales.put(posCode, mapModiDtl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void funGenerateMonthWiseSales(List list) {
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					String posCode = obj[5].toString();// posCode
					String posName = obj[0].toString(); // posName
					String monthCode = obj[6].toString();// monthCode
					String monthName = obj[1].toString();// monthName
					String year = obj[2].toString();// year
					double saleAmount = Double.parseDouble(obj[3].toString()); // saleAmount

					if (mapPOSMonthWiseSales.containsKey(year)) {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = mapPOSMonthWiseSales.get(year);
						if (mapMonthDtl.containsKey(monthCode)) {
							clsPOSCommonBeanDtl objMonthDtl = mapMonthDtl.get(monthCode);
							objMonthDtl.setSaleAmount(objMonthDtl.getSaleAmount() + saleAmount);
							mapMonthDtl.put(monthCode, objMonthDtl);
						} else {
							clsPOSCommonBeanDtl objMonthDtl = new clsPOSCommonBeanDtl(saleAmount, posCode, posName,
									monthCode, monthName, year);
							mapMonthDtl.put(monthCode, objMonthDtl);
						}
					} else {
						Map<String, clsPOSCommonBeanDtl> mapMonthDtl = new LinkedHashMap<>();
						clsPOSCommonBeanDtl objMonthDtl = new clsPOSCommonBeanDtl(saleAmount, posCode, posName,
								monthCode, monthName, year);
						mapMonthDtl.put(monthCode, objMonthDtl);
						mapPOSMonthWiseSales.put(year, mapMonthDtl);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	public Map funViewButtonPressed(String code,String transactionType,String kotFor,String posCode,String clientCode,String posName,String webStockUserCode,String POSDate,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
	{
	 List list = null;
	 String TableNo =null;
	 Map jObjRet=new HashMap<>();
     List jArr =new ArrayList<>();
     Map hmData=new HashMap<>();
 	 StringBuilder sql = new StringBuilder(); 
     Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gPrintType");
	 if (transactionType.equalsIgnoreCase("KOT"))
     {
         if (kotFor.equalsIgnoreCase("Dina"))
         {
        	 try
             {
        		 sql.append("select strTableNo from tblitemrtemp "
                         + "where strKOTNo='" + code + "' "
                         + "group by strKOTNo ");
                list=objBaseService.funGetList(sql, "sql");
     			 
     			 if (list!=null)
     				{
     					for(int i=0; i<list.size(); i++)
     					{
     						TableNo =(String) list.get(i);
     					}
     				}
 			    
 			    /*if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
 			    {
 			    	 hmData=funRemotePrintUsingTextFile(TableNo, code.trim(), "", "Reprint", "Dina", "N",posCode,clientCode,posName,webStockUserCode);
 			    	
                 }
                 else
                 {
                	 hmData=funRemotePrintUsingTextFile(TableNo, code.trim(), "", "Reprint", "Dina", "N",posCode,clientCode,posName,webStockUserCode);
                	
                 }
 			    */
 			   hmData=funRemotePrintUsingTextFile(TableNo, code.trim(), "", "Reprint", "Dina", "N",posCode,clientCode,posName,webStockUserCode);
 			  
             }
        	 catch (Exception e)
             {
                 e.printStackTrace();
             }
        	 code = "";
         }
         else if (kotFor.equalsIgnoreCase("DirectBiller"))
         {
 			    
 			    /*if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
				 {
				 }
				 else
				 {
					 hmData = objUtilityController.funPrintBill(code.trim(), POSDate, true, posCode,clientCode,posName,webStockUserCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
				 }
 			    */
        	    hmData = objUtilityController.funPrintBill(code.trim(), POSDate, true, posCode,clientCode,posName,webStockUserCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        	    code = "";
            }
         } 
         else if (transactionType.equalsIgnoreCase("Bill"))
         {
                 /*if (objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
				 {
				    // funTextFilePreviewBill(docNo);
				 }
				 else
				 {
				 hmData = objUtilityController.funPrintBill(code.trim(), POSDate, true, posCode,clientCode,posName,webStockUserCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
				 }
                 */
        	 hmData = objUtilityController.funPrintBill(code.trim(), POSDate, true, posCode,clientCode,posName,webStockUserCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
        	 code = "";
         }
         else 
         {
             String gDayEndReportForm = "ReprintDayEndReport";
             try
             {

                /* if (objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
                 {
                	// jObj1 = objUtilityFunctions.funGenerateTextDayEndReportPreview(code, POSDate, "reprint");
                 }
                 else
                 {
                	 hmData = objUtilityController.funGenerateTextDayEndReportPreview(posCode, code, "reprint",clientCode,gDayEndReportForm,webStockUserCode);
                 }
                 */
                 hmData = objUtilityController.funGenerateTextDayEndReportPreview(posCode, code, "reprint",clientCode,gDayEndReportForm,webStockUserCode);
             }
             catch (Exception e)
             {
                 e.printStackTrace();
             }
         }
         
   
	return hmData;
	 
}	
	
	
	

	public Map funRemotePrintUsingTextFile(String tableNo, String KOTNo, String billNo, String reprint, String type, String printYN,String POSCode,String clientCode,String posName,String webStockUserCode)
	{
		Map hmData=new HashMap<>();
		StringBuilder sql = new StringBuilder(); 
	    try
	    {
	        String p2="",p3="",p4="",p5="",p6="",p7="";
	        String c2="",c3="",c4="",c5="",c6="";
	        String areaCodeForAll = "";
            sql.append("select strAreaCode from tblareamaster where strAreaName='All'");
            List rsAreaCode=objBaseService.funGetList(sql, "sql");
           
	   		if (rsAreaCode!=null)
			{
				for(int i=0; i<rsAreaCode.size(); i++)
				{
					areaCodeForAll =(String) rsAreaCode.get(i);
                }
			}
   		   sql.setLength(0);
   		   sql.append("select a.strItemName,a.strNCKotYN,d.strCostCenterCode,d.strPrimaryPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName "
                + " ,ifnull(e.strLabelOnKOT,'KOT') strLabelOnKOT "
                + " from tblitemrtemp a "
                + " left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode "
                + " left outer join tblprintersetup d on c.strCostCenterCode=d.strCostCenterCode "
                + " left outer join tblcostcentermaster e on c.strCostCenterCode=e.strCostCenterCode  "
                + " where a.strKOTNo='"+KOTNo+"' and a.strTableNo='"+tableNo+"' and (c.strPosCode='"+POSCode+"' or c.strPosCode='All') "
                + " and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
                + " OR c.strAreaCode ='"+areaCodeForAll+"') "
                + " group by d.strCostCenterCode");
          List list1 =objBaseService.funGetList(sql, "sql");
          if (list1!=null)
		   {
        	  for(int i=0; i<list1.size(); i++)
				{
        		    Object[] obj = (Object[]) list1.get(i);
					p3 = (String) Array.get(obj, 2);
            	  	p2 = (String) Array.get(obj, 1);
            	  	p4 = (String) Array.get(obj, 3);
            	  	p5 = (String) Array.get(obj, 4);
            	  	p6 = (String) Array.get(obj, 5);
            	  	p7 = (String) Array.get(obj, 6);
				}	
			 	Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintType");
 			    hmData=funGenerateJasperForTableWiseKOT("Dina", tableNo, p3, "", areaCodeForAll, KOTNo, reprint, p4, p5, p6, printYN, p2, p7,posName,POSCode,clientCode,webStockUserCode);
 			 }
        }
        catch (Exception ex)
		{
			ex.printStackTrace();
		}
	    return hmData;
	}

	
	
	
	
	 private Map funGenerateJasperForTableWiseKOT(String billingType, String tableNo, String CostCenterCode, String ShowKOT, String AreaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName, String printYN, String NCKotYN, String labelOnKOT,String posName,String POSCode,String clientCode,String webStockUserCode)
	    {
		    Map hmData = new HashMap();
	        Map hm = new HashMap();
	        StringBuilder sql = new StringBuilder();
	        List listData = new ArrayList();
	       // List<List<clsPOSBillDtl>> listData = new ArrayList<>();
	        try
	        {
	            
	            boolean isReprint = false;
	            if ("Reprint".equalsIgnoreCase(Reprint))
	            {
	                isReprint = true;
	                hm.put("dublicate", "[DUPLICATE]");
	            }
	            if ("Y".equalsIgnoreCase(NCKotYN))
	            {
	                hm.put("KOTorNC", "NCKOT");
	            }
	            else
	            {
	                hm.put("KOTorNC", labelOnKOT);
	            }
	            hm.put("POS", posName);
	            hm.put("costCenter", CostCenterName);

	            String tableName = "";
	            int pax = 0;
	            sql.append("select strTableName,intPaxNo "
	                    + " from tbltablemaster "
	                    + " where strTableNo='"+tableNo+"' and strOperational='Y'");
	            
	            List rs_Dina_Table = objBaseService.funGetList(sql, "sql");
                if (rs_Dina_Table!=null)
				 {
        	       for(int i=0; i<rs_Dina_Table.size(); i++)
					{
        	    	   Object[] obj = (Object[]) rs_Dina_Table.get(i);
				       tableName = (String) Array.get(obj,0);
                       pax = (int) Array.get(obj,1);
					}
				 }
	           
	            String itemName = "b.strItemName";
	            Map mapSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintShortNameOnKOT");
			    String pringShortNameOnKOT = (String) mapSetupParameter.get("gPrintShortNameOnKOT");
			    if ("gPrintShortNameOnKOT".equalsIgnoreCase(pringShortNameOnKOT))
	            {
	                itemName = "d.strShortName";
	            }
	            String sqlKOTItems = "";
	            List<clsPOSBillDtl> listOfKOTDetail = new ArrayList<>();
	            mapSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gAreaWisePricing");
			    
	            sql.setLength(0);
	            if (mapSetupParameter.get("gAreaWisePricing").equals("Y"))
	            {
	            	sql.append("select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
	                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
	                        + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNO+"' and b.strCostCenterCode=c.strCostCenterCode "
	                        + " and b.strCostCenterCode='"+CostCenterCode+"' and a.strItemCode=d.strItemCode "
	                        + " and (b.strPOSCode='"+POSCode+"' or b.strPOSCode='All') "
	                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' )) "
	                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
	                        + " order by a.strSerialNo ");
	            }
	            else
	            {
	            	sql.append("select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
	                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
	                        + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNO+"' and b.strCostCenterCode=c.strCostCenterCode "
	                        + " and b.strCostCenterCode='"+CostCenterCode+"' and a.strItemCode=d.strItemCode "
	                        + " and (b.strPOSCode='"+POSCode+"' or b.strPOSCode='All') "
	                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
	                        + " OR b.strAreaCode ='" + AreaCode + "') "
	                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
	                        + " order by a.strSerialNo ");
	            }
	            
	            
	          List list_KOT_Items= objBaseService.funGetList(sql, "sql");
	          String KOTType = "DINE";
	          Map hmTakeAway = new HashMap<String, String>();
	          sql.setLength(0);
	          sql.append("select strTableNo from tblitemrtemp where strTakeAwayYesNo='Yes'");
	          List listTakeAway = objBaseService.funGetList(sql, "sql");
              if (listTakeAway!=null)
			   {
	  	           for(int i=0; i<listTakeAway.size(); i++)
					{
	  	    	       hmTakeAway.put((String) listTakeAway.get(i), "Yes");
					}
			   }
	          
	          if (null != hmTakeAway.get(tableNo))
	           {
	                KOTType = "Take Away";
	            }
	          	String gCounterWise = "No";
	            hm.put("KOTType", KOTType);
	            if (gCounterWise.equals("Yes"))
	            {
	                hm.put("CounterName", "");
	            }
	            hm.put("KOT", KOTNO);
	            hm.put("tableNo", tableName);
	            if (clientCode.equals("124.001"))
	            {
	                hm.put("124.001", tableName);
	            }
	            hm.put("PAX", String.valueOf(pax));

	            sql.setLength(0);
		        sql.append("select strWaiterNo from tblitemrtemp where strKOTNo='"+KOTNO+"'  and strTableNo='"+tableNo+"' group by strKOTNo ");
		        String waiterNo="";
		        List listWaiterDtl = objBaseService.funGetList(sql, "sql");
	              if (listWaiterDtl!=null)
				   {
	            	  for(int i=0; i<listWaiterDtl.size(); i++)
						{
		            		waiterNo =(String) listWaiterDtl.get(i);
		   				}
	            	  
	            	  if (!"null".equalsIgnoreCase(waiterNo) && waiterNo.trim().length() > 0)
		                {
	            		  sql.setLength(0);
	      		          sql.append("select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'");
		                  List listWaiter =objBaseService.funGetList(sql, "sql");
		                  for(int i=0; i<listWaiter.size(); i++)
							{
		                	  hm.put("waiterName",(String) listWaiter.get(i));
							}
		                }
				   }
		      
	            sql.setLength(0);
  		        sql.append("select date(dteDateCreated),time(dteDateCreated) from tblitemrtemp where strKOTNo='"+KOTNO+"'  and strTableNo='"+tableNo+"' group by strKOTNo ");
  		        List listDate =objBaseService.funGetList(sql, "sql");
  		        Date dt = null;
	            Time ti = null;
	            if(listDate!=null)
	            {
	            	for(int i=0; i<listDate.size(); i++)
					{
	            		Object[] obj = (Object[]) listDate.get(i);
	            		dt = (Date) Array.get(obj,0);
	            		ti=(Time)Array.get(obj,1);
	            	}
	              hm.put("DATE_TIME", dt + " " + ti);
	            }
	            
	            InetAddress ipAddress = InetAddress.getLocalHost();
	            String hostName = ipAddress.getHostName();
	            hm.put("KOTFrom", hostName);
	            
	            BigDecimal itemQty=null;
	            String itemNam="",serialNo="",itemCode="";
	            String modifierName="";
	            if (list_KOT_Items!=null)
	             {
	        	   for(int i=0; i<list_KOT_Items.size(); i++)
					{
	           		   Object[] obj = (Object[]) list_KOT_Items.get(i);
	           		   itemQty = (BigDecimal) Array.get(obj,2);
	        	       itemNam=(String)Array.get(obj,1);
	        	       serialNo=(String)Array.get(obj,4);
	        	       itemCode=(String)Array.get(obj, 0);
	        	     
		        	   double d1=itemQty.doubleValue();
		               clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
		               objBillDtl.setDblQuantity(d1);
		               objBillDtl.setStrItemName(itemNam);
		               listOfKOTDetail.add(objBillDtl);
		               sql.setLength(0);
		  		       sql.append("select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
		                        + " where a.strItemCode like'" + itemCode + "M%' and a.strKOTNo='" + KOTNO + "' "
		                        + " and strSerialNo like'" + serialNo + ".%' "
		                        + " group by a.strItemCode,a.strItemName ");
		                //System.out.println(sql_Modifier);
		  		       List listModifierItems =objBaseService.funGetList(sql, "sql");
		               if (listModifierItems!=null && listModifierItems.size()>0)
		                {
		            	   for(int cnt=0; cnt<listModifierItems.size(); cnt++)
			   				{
			              		Object[] objModifier = (Object[]) listModifierItems.get(cnt);
			              		modifierName = (String) Array.get(objModifier,0);
			              		itemQty = (BigDecimal) Array.get(objModifier,1);
			                }
		            	    d1=itemQty.doubleValue();
		                    objBillDtl = new clsPOSBillDtl();
		                   
		                    if (modifierName.startsWith("-->"))
		                    {
		                    	
		                      mapSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintModQtyOnKOT");
		            		  if ((boolean)mapSetupParameter.get("gPrintModQtyOnKOT"))
		                       {
		                            objBillDtl.setDblQuantity(d1);
		                            objBillDtl.setStrItemName(modifierName);
		                        }
		                        else
		                        {
		                            objBillDtl.setDblQuantity(0);
		                            objBillDtl.setStrItemName(modifierName);
		                        }
		                    }
		                    listOfKOTDetail.add(objBillDtl);
		                }
	             }
	          }  
	          mapSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gNoOfLinesInKOTPrint");
		      String noOfLinesOnKOTPrint = (String) mapSetupParameter.get("gNoOfLinesInKOTPrint");
		      hm.put("listOfItemDtl", listOfKOTDetail);
	          listData.add(hm);
	            
              hmData.put("listData", listData);
              hmData.put("gNoOfLinesInKOTPrint", noOfLinesOnKOTPrint);
              hmData.put("listOfKOTDetail", listOfKOTDetail);
	        
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        
	        return hmData;
	    }

	
	
	public LinkedHashMap funGetBillWiseSettlementBillRegisterSalesSummary(String fromDate, String toDate, String viewBy,
			String strOperationType, String strSettlementCode, String strPOSCode, String strPOSName, String groupName,String userCode,String clientCode) throws Exception 
	{
		List listData = new ArrayList();
		DecimalFormat decFormat = new DecimalFormat("0");
	    StringBuilder sbSqlBillWise = new StringBuilder();
	    StringBuilder sbSqlBillWiseQFile = new StringBuilder();
	    List<clsPOSSalesFlashColumns> arrListBillWiseSales = new ArrayList<clsPOSSalesFlashColumns>();
	    List listColHeaderArr = new ArrayList();
	    Map<String, String> mapAllTaxes=new HashMap<String,String>();
	    LinkedHashMap mapRet = new LinkedHashMap();
	    sbSqlBillWise.setLength(0);
	    sbSqlBillWise.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
		    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
		    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt "
		    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated "
		    + " ,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo "
		    + " ,a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA') "
		    + " ,a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA')"
		    + ",a.intShiftCode,a.dblRoundOff,a.intBillSeriesPaxNo,ifnull(i.dblAdvDeposite,0),ifnull(k.strAdvOrderTypeName,'') "
		    + ",ifnull(l.strBillSeries,''),d.strSettelmentType "
		    + " from tblbillhd  a "
		    + " left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
		    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
		    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  and date(a.dteBillDate)=date(c.dteBillDate)  "
		    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
		    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
		    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
		    + " left outer join tbladvancereceipthd i on a.strAdvBookingNo=i.strAdvBookingNo "
		    + " LEFT OUTER JOIN tbladvbookbillhd j ON a.strAdvBookingNo=j.strAdvBookingNo "
		    + " left outer join tbladvanceordertypemaster k on j.strOrderType=k.strAdvOrderTypeCode "
		    + " left outer join tblbillseriesbilldtl l on a.strBillNo=l.strHdBillNo "
		    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
		    + "  ");
	    if (!strOperationType.equalsIgnoreCase("All"))
	    {
		sbSqlBillWise.append("and a.strOperationType='" + strOperationType + "' ");
	    }
	    if (!strPOSCode.equalsIgnoreCase("All"))
	    {
		sbSqlBillWise.append(" and a.strPOSCode='" + strPOSCode + "' ");
	    }
	    if (!strSettlementCode.equalsIgnoreCase("All"))
	    {
		sbSqlBillWise.append(" and c.strSettlementCode='" + strSettlementCode + "' ");
	    }
	    sbSqlBillWise.append("  ");
	    sbSqlBillWise.append(" order by date(a.dteBillDate),a.strBillNo  ");

	    sbSqlBillWiseQFile.setLength(0);
	    sbSqlBillWiseQFile.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
		    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
		    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt "
		    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated "
		    + " ,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo "
		    + " ,a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA') "
		    + " ,a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA')"
		    + ",a.intShiftCode,a.dblRoundOff,a.intBillSeriesPaxNo,ifnull(i.dblAdvDeposite,0),ifnull(k.strAdvOrderTypeName,'') "
		    + ",ifnull(l.strBillSeries,''),d.strSettelmentType "
		    + " from tblqbillhd  a "
		    + " left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
		    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
		    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  and date(a.dteBillDate)=date(c.dteBillDate)  "
		    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
		    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
		    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
		    + " left outer join tblqadvancereceipthd i on a.strAdvBookingNo=i.strAdvBookingNo "
		    + " LEFT OUTER JOIN tblqadvbookbillhd j ON a.strAdvBookingNo=j.strAdvBookingNo "
		    + " left outer join tbladvanceordertypemaster k on j.strOrderType=k.strAdvOrderTypeCode "
		    + " left outer join tblbillseriesbilldtl l on a.strBillNo=l.strHdBillNo "
		    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
		    + "  ");
	    if (!strOperationType.equalsIgnoreCase("All"))
	    {
		sbSqlBillWiseQFile.append("and a.strOperationType='" + strOperationType + "' ");
	    }
	    if (!strPOSCode.equalsIgnoreCase("All"))
	    {
		sbSqlBillWiseQFile.append(" and a.strPOSCode='" + strPOSCode + "' ");
	    }
	    if (!strSettlementCode.equalsIgnoreCase("All"))
	    {
		sbSqlBillWiseQFile.append(" and c.strSettlementCode='" + strSettlementCode + "' ");
	    }
	    sbSqlBillWiseQFile.append("   ");
	    sbSqlBillWiseQFile.append(" order by date(a.dteBillDate),a.strBillNo  ");
	    double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalAdvAmt = 0, totalSettleAmt = 0, totalTipAmt = 0, totalRoundOffAmt = 0;
	    boolean flgRecords = false;
	    int totalPAX = 0;
	    String gCMSIntegrationY = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, strPOSCode, "gCMSIntegrationYN");
	    Map<String, List<clsPOSSalesFlashColumns>> hmBillWiseSales = new HashMap<String, List<clsPOSSalesFlashColumns>>();
	    int seqNo = 1;
	    //for live data
	    List listBillWiseSales =objBaseService.funGetList(sbSqlBillWise,"sql");
	   if(listBillWiseSales.size()>0)
	    {
		   for(int i=0;i<listBillWiseSales.size();i++)
		   {	
			 Object[] obj = (Object[])listBillWiseSales.get(i);  
			 arrListBillWiseSales = new ArrayList<clsPOSSalesFlashColumns>();
		flgRecords = true;
		String[] spDate = obj[1].toString().split("-");
		String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate

		clsPOSSalesFlashColumns objSalesFlashColumns = new clsPOSSalesFlashColumns();
		objSalesFlashColumns.setStrField1(obj[0].toString());
		objSalesFlashColumns.setStrField2(billDate);
		objSalesFlashColumns.setStrField3(obj[2].toString());
		objSalesFlashColumns.setStrField4(obj[3].toString());
		
		if (gCMSIntegrationY.equalsIgnoreCase("Y"))
		{
			objSalesFlashColumns.setStrField5(obj[17].toString());//Member Code
		}
		else
		{
		    objSalesFlashColumns.setStrField5(obj[20].toString());//Cust Name
		}
		objSalesFlashColumns.setStrField6(obj[4].toString());
		objSalesFlashColumns.setStrField7(obj[5].toString());
		objSalesFlashColumns.setStrField8(obj[18].toString());
		objSalesFlashColumns.setStrField9(obj[6].toString());
		objSalesFlashColumns.setStrField10(obj[7].toString());
		objSalesFlashColumns.setStrField11(obj[8].toString());
		objSalesFlashColumns.setStrField12(obj[9].toString());
		objSalesFlashColumns.setStrField13(obj[10].toString());
		objSalesFlashColumns.setStrField14(obj[19].toString());

		objSalesFlashColumns.setStrField15(obj[21].toString());
		objSalesFlashColumns.setStrField16(obj[23].toString());
		objSalesFlashColumns.setStrField17(obj[24].toString());
		objSalesFlashColumns.setStrField18(obj[25].toString());//shift
		objSalesFlashColumns.setStrField19(obj[26].toString());//roundOff
		objSalesFlashColumns.setStrField20(obj[27].toString());//intBillSeriesPaxNo
		objSalesFlashColumns.setStrField21(obj[28].toString());//dblAdvDeposite
		objSalesFlashColumns.setStrField22(obj[29].toString());//strAdvOrderTypeName
		objSalesFlashColumns.setStrField23(obj[30].toString());//bill series

		objSalesFlashColumns.setStrCustomerName(obj[20].toString());
		objSalesFlashColumns.setStrSettlementType(obj[31].toString());//settlement type

		objSalesFlashColumns.setSeqNo(seqNo++);

		if (null != hmBillWiseSales.get(obj[0].toString() + "!" + billDate))
		{
		    arrListBillWiseSales = hmBillWiseSales.get(obj[0].toString() + "!" + billDate);
		    objSalesFlashColumns.setStrField9("0");
		    objSalesFlashColumns.setStrField10("0");
		    objSalesFlashColumns.setStrField11("0");
		    objSalesFlashColumns.setStrField12("0");
		    objSalesFlashColumns.setStrField15("0");
		    objSalesFlashColumns.setStrField19("0");//roundoff
		    objSalesFlashColumns.setStrField20("0");//intBillSeriesPaxNo
		    objSalesFlashColumns.setStrField21("0");//dblAdvDeposite

		    objSalesFlashColumns.setStrField24("MultiSettle");//MultiSettle
		}
		arrListBillWiseSales.add(objSalesFlashColumns);
		hmBillWiseSales.put(obj[0].toString() + "!" + billDate, arrListBillWiseSales);

		totalDiscAmt += Double.parseDouble(objSalesFlashColumns.getStrField11());
		totalSubTotal += Double.parseDouble(objSalesFlashColumns.getStrField9());
		totalTaxAmt += Double.parseDouble(objSalesFlashColumns.getStrField12());
		totalAdvAmt += Double.parseDouble(objSalesFlashColumns.getStrField21());
		totalSettleAmt += Double.parseDouble(objSalesFlashColumns.getStrField13());// Grand Total                
		totalTipAmt += Double.parseDouble(objSalesFlashColumns.getStrField15());// tip Amt  
		totalRoundOffAmt += Double.parseDouble(objSalesFlashColumns.getStrField19());// roundoff Amt  
		totalPAX += Integer.parseInt(objSalesFlashColumns.getStrField20());//intBillSeriesPaxNo

	    }
	}
	   
	 //for qfile data
	 listBillWiseSales =objBaseService.funGetList(sbSqlBillWiseQFile,"sql");
	 if(listBillWiseSales.size()>0)
	 {
		 for(int i=0;i<listBillWiseSales.size();i++)
		 {	
		Object[] obj = (Object[])listBillWiseSales.get(i);  
		arrListBillWiseSales = new ArrayList<clsPOSSalesFlashColumns>();	
		flgRecords = true;

		String[] spDate = obj[1].toString().split("-");
		String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate

		clsPOSSalesFlashColumns objSalesFlashColumns = new clsPOSSalesFlashColumns();
		objSalesFlashColumns.setStrField1(obj[0].toString());
		objSalesFlashColumns.setStrField2(billDate);
		objSalesFlashColumns.setStrField3(obj[2].toString());
		objSalesFlashColumns.setStrField4(obj[3].toString());
		if (gCMSIntegrationY.equalsIgnoreCase("Y"))
		{
		    objSalesFlashColumns.setStrField5(obj[17].toString());//Member Code
		}
		else
		{
		    objSalesFlashColumns.setStrField5(obj[20].toString());//Cust Name
		}
		objSalesFlashColumns.setStrField6(obj[4].toString());
		objSalesFlashColumns.setStrField7(obj[5].toString());
		objSalesFlashColumns.setStrField8(obj[18].toString());
		objSalesFlashColumns.setStrField9(obj[6].toString());
		objSalesFlashColumns.setStrField10(obj[7].toString());
		objSalesFlashColumns.setStrField11(obj[8].toString());
		objSalesFlashColumns.setStrField12(obj[9].toString());
		objSalesFlashColumns.setStrField13(obj[10].toString());
		objSalesFlashColumns.setStrField14(obj[19].toString());
		objSalesFlashColumns.setStrField15(obj[21].toString());
		objSalesFlashColumns.setStrField16(obj[23].toString());
		objSalesFlashColumns.setStrField17(obj[24].toString());
		objSalesFlashColumns.setStrField18(obj[25].toString());//shift
		objSalesFlashColumns.setStrField19(obj[26].toString());//roundOff
		objSalesFlashColumns.setStrField20(obj[27].toString());//intBillSeriesPaxNo
		objSalesFlashColumns.setStrField21(obj[28].toString());//dblAdvDeposite
		objSalesFlashColumns.setStrField22(obj[29].toString());//strAdvOrderTypeName
		objSalesFlashColumns.setStrField23(obj[30].toString());//bill series

		objSalesFlashColumns.setStrCustomerName(obj[20].toString());//customerName
		objSalesFlashColumns.setStrSettlementType(obj[31].toString());//settlement type

//               objSalesFlashColumns.setSeqNo(Integer.parseInt(billNo.split("-")[0]));
		objSalesFlashColumns.setSeqNo(seqNo++);

		if (null != hmBillWiseSales.get(obj[0].toString() + "!" + billDate))
		{
		    arrListBillWiseSales = hmBillWiseSales.get(obj[0].toString() + "!" + billDate);
		    objSalesFlashColumns.setStrField9("0");
		    objSalesFlashColumns.setStrField10("0");
		    objSalesFlashColumns.setStrField11("0");
		    objSalesFlashColumns.setStrField12("0");
		    objSalesFlashColumns.setStrField15("0");
		    objSalesFlashColumns.setStrField19("0");//roundoff
		    objSalesFlashColumns.setStrField20("0");//intBillSeriesPaxNo
		    objSalesFlashColumns.setStrField21("0");//dblAdvDeposite

		    objSalesFlashColumns.setStrField24("MultiSettle");//MultiSettle
		}
		arrListBillWiseSales.add(objSalesFlashColumns);
		hmBillWiseSales.put(obj[0].toString() + "!" + billDate, arrListBillWiseSales);

		totalDiscAmt += Double.parseDouble(objSalesFlashColumns.getStrField11());
		totalSubTotal += Double.parseDouble(objSalesFlashColumns.getStrField9());
		totalTaxAmt += Double.parseDouble(objSalesFlashColumns.getStrField12());
		totalAdvAmt += Double.parseDouble(objSalesFlashColumns.getStrField21());
		totalSettleAmt += Double.parseDouble(objSalesFlashColumns.getStrField13());// Grand Total 
		totalTipAmt += Double.parseDouble(objSalesFlashColumns.getStrField15());// tip Amt  
		totalRoundOffAmt += Double.parseDouble(objSalesFlashColumns.getStrField19());// roundoff Amt  
		totalPAX += Integer.parseInt(objSalesFlashColumns.getStrField20());//intBillSeriesPaxNo
	    }
	}
	
	 List listTotal=new ArrayList();
	 listTotal.add("");
	 listTotal.add("");
	 listTotal.add(totalSubTotal);
	 listTotal.add(totalDiscAmt);
	 listTotal.add(totalTaxAmt);
	 listTotal.add(totalAdvAmt);
	 listTotal.add(totalTipAmt);
	 listTotal.add(totalSettleAmt);
	 listTotal.add("");
	 
		listColHeaderArr.add("Tbl No");
		listColHeaderArr.add("Bill No");
		listColHeaderArr.add("Aomunt");
		listColHeaderArr.add("Disc");

		int cntArrLen = 0;

	    cntArrLen = cntArrLen + 4;
	    
		 mapAllTaxes = new HashMap<>();
		    String taxCalType = "";
		    ///live
		    StringBuilder sb = new StringBuilder();
		    sb.setLength(0);
		    sb.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
			    + "from tbltaxhd a,tblbilltaxdtl b "
			    + "where a.strTaxCode=b.strTaxCode "
			    + "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		    List listAllTaxes =objBaseService.funGetList(sb,"sql");
		    if(listAllTaxes.size()>0)
		    {
			    for(int i=0;i<listAllTaxes.size();i++)
			    {
			    	Object[] obj = (Object[])listAllTaxes.get(i);
					taxCalType = obj[2].toString();
					mapAllTaxes.put( obj[0].toString(), obj[1].toString());
			    }
		    }
		    ///Qfile
		    sb.setLength(0);
		    sb.append("select distinct(a.strTaxCode),a.strTaxDesc,a.strTaxCalculation  "
			    + "from tbltaxhd a,tblqbilltaxdtl b "
			    + "where a.strTaxCode=b.strTaxCode "
			    + "and date(b.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		    listAllTaxes =objBaseService.funGetList(sb,"sql");
		    if(listAllTaxes.size()>0)
		    {
			    for(int i=0;i<listAllTaxes.size();i++)
			    {
			    	Object[] obj = (Object[])listAllTaxes.get(i);
					taxCalType = obj[2].toString();
					mapAllTaxes.put(obj[0].toString(), obj[1].toString());
			    }
		    }
		    for (Map.Entry<String, String> taxEntry : mapAllTaxes.entrySet())
		    {

			String taxCode = taxEntry.getKey();
			String taxName = taxEntry.getValue();

			listColHeaderArr.add(taxName.toUpperCase());
		
			cntArrLen++;

		    }
		    listColHeaderArr.add("Tip");
		    listColHeaderArr.add("Adv.");
		    listColHeaderArr.add("Total");
		    listColHeaderArr.add("Settle");

		    cntArrLen = cntArrLen + 4;


		    for (String key : hmBillWiseSales.keySet())
		    {
			List<clsPOSSalesFlashColumns> listOfBills = hmBillWiseSales.get(key);

			for (clsPOSSalesFlashColumns objSalesFlashColumns : listOfBills)
			{
			    Map<String, clsPOSTaxCalculationDtls> mapOfTaxes = new HashMap<>();
			    for (Map.Entry<String, String> taxEntry : mapAllTaxes.entrySet())
			    {
				String taxCode = taxEntry.getKey();
				String taxName = taxEntry.getValue();

				clsPOSTaxCalculationDtls objTax = new clsPOSTaxCalculationDtls();
				objTax.setTaxCode(taxCode);
				objTax.setTaxName(taxName);
				objTax.setTaxAmount(0.00);

				mapOfTaxes.put(taxName, objTax);
			    }

			    objSalesFlashColumns.setMapOfTaxes(mapOfTaxes);
			}
		    }
		    StringBuilder sqlBuilderTax = new StringBuilder();
		    sqlBuilderTax.append("select a.strBillNo,c.strTaxDesc,b.dblTaxAmount,date(a.dteBillDate) "
				    + "from "
				    + "tblqbillhd a,tblqbilltaxdtl b,tbltaxhd c,tblqbillsettlementdtl d,tblsettelmenthd e "
				    + "where a.strBillNo=b.strBillNo "
				    + "and b.strTaxCode=c.strTaxCode "
				    + "and a.strBillNo=d.strBillNo "
				    + "and d.strSettlementCode=e.strSettelmentCode "
				    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
			    if (!strOperationType.equalsIgnoreCase("All"))
			    {
			    	sqlBuilderTax.append("and a.strOperationType='" + strOperationType + "' ");
			    }
			    if (!strPOSCode.equalsIgnoreCase("All"))
			    {
			    	sqlBuilderTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
			    }
			    if (!strSettlementCode.equalsIgnoreCase("All"))
			    {
			    	sqlBuilderTax.append( " and e.strSettelmentCode='" + strSettlementCode + "' ");
			    }
			    sqlBuilderTax.append("group by a.strBillNo,b.strTaxCode "
				    + "order by a.strBillNo,b.strTaxCode;  ");
			   List listSales = objBaseService.funGetList(sqlBuilderTax,"sql");
			    if(listSales.size()>0)
			    {
			    for(int i=0;i<listSales.size();i++)
			    {
			    Object[] obj = (Object[])listSales.get(i);	
				String[] spDate = obj[3].toString().split("-");
				String billNo = obj[0].toString();
				String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate
				String billKey = billNo + "!" + billDate;

				List<clsPOSSalesFlashColumns> listOfBills = hmBillWiseSales.get(billKey);
				clsPOSSalesFlashColumns objSalesFlashColumns = listOfBills.get(0);
				Map<String, clsPOSTaxCalculationDtls> mapOfTaxesLocal = objSalesFlashColumns.getMapOfTaxes();
				if (mapOfTaxesLocal.containsKey(obj[1].toString()))
				{
					clsPOSTaxCalculationDtls objTax = mapOfTaxesLocal.get(obj[1].toString());
				    objTax.setTaxAmount(Double.parseDouble(obj[2].toString()));
				}
			    }
			    }

			    sqlBuilderTax.setLength(0);
			    sqlBuilderTax.append("select a.strBillNo,c.strTaxDesc,b.dblTaxAmount,date(a.dteBillDate) "
					    + "from "
					    + "tblbillhd a,tblbilltaxdtl b,tbltaxhd c,tblbillsettlementdtl d,tblsettelmenthd e "
					    + "where a.strBillNo=b.strBillNo "
					    + "and b.strTaxCode=c.strTaxCode "
					    + "and a.strBillNo=d.strBillNo "
					    + "and d.strSettlementCode=e.strSettelmentCode "
					    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
				    if (!strOperationType.equalsIgnoreCase("All"))
				    {
				    	sqlBuilderTax.append("and a.strOperationType='" + strOperationType + "' ");
				    }
				    if (!strPOSCode.equalsIgnoreCase("All"))
				    {
				    	sqlBuilderTax.append(" and a.strPOSCode='" + strPOSCode + "' ");
				    }
				    if (!strSettlementCode.equalsIgnoreCase("All"))
				    {
				    	sqlBuilderTax.append(" and e.strSettelmentCode='" + strSettlementCode + "' ");
				    }
				    sqlBuilderTax.append("group by a.strBillNo,b.strTaxCode "
					    + "order by a.strBillNo,b.strTaxCode;  ");
				    listSales = objBaseService.funGetList(sqlBuilderTax,"sql");
				    if(listSales.size()>0)
				    {
				    for(int i=0;i<listSales.size();i++)
				    {
				    Object[] obj = (Object[])listSales.get(i);	
					String[] spDate = obj[3].toString().split("-");
					String billNo = obj[0].toString();
					String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate
					String billKey = billNo + "!" + billDate;

					List<clsPOSSalesFlashColumns> listOfBills = hmBillWiseSales.get(billKey);
					clsPOSSalesFlashColumns objSalesFlashColumns = listOfBills.get(0);
					Map<String, clsPOSTaxCalculationDtls> mapOfTaxesLocal = objSalesFlashColumns.getMapOfTaxes();
					if (mapOfTaxesLocal.containsKey(obj[1].toString()))
					{
						clsPOSTaxCalculationDtls objTax = mapOfTaxesLocal.get(obj[1].toString());
					    objTax.setTaxAmount(Double.parseDouble(obj[2].toString()));
					}
				    }
				    }
				    final String BILLSERIESSORTING = "FL";
				    Comparator<clsPOSSalesFlashColumns> BILLSERIES = new Comparator<clsPOSSalesFlashColumns>()
				    {
					// This is where the sorting happens.
					public int compare(clsPOSSalesFlashColumns o1, clsPOSSalesFlashColumns o2)
					{
					    return BILLSERIESSORTING.indexOf(o1.getStrField23()) - BILLSERIESSORTING.indexOf(o2.getStrField23());
					}
				    };

				    Comparator<clsPOSSalesFlashColumns> BILLNO = new Comparator<clsPOSSalesFlashColumns>()
				    {
					// This is where the sorting happens.
					public int compare(clsPOSSalesFlashColumns o1, clsPOSSalesFlashColumns o2)
					{
					    return o1.getStrField1().compareTo(o2.getStrField1());
					}
				    };
				    ArrayList<clsPOSSalesFlashColumns> arrTempListBillWiseSales;
				    arrTempListBillWiseSales = new ArrayList<clsPOSSalesFlashColumns>();
				    for (List<clsPOSSalesFlashColumns> listOfFlashColumns : hmBillWiseSales.values())
				    {
					arrTempListBillWiseSales.addAll(listOfFlashColumns);
				    }

				    //sort arrTempListBillWiseSales 
				    String gEnableBillSeries = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, strPOSCode, "gEnableBillSeries");
				    
				    if (gEnableBillSeries.equalsIgnoreCase("Y"))
				    {
					Collections.sort(arrTempListBillWiseSales, new clsSalesFlashComparator(BILLSERIES, BILLNO));
				    }

				    Vector billsRow = new Vector();
				    billsRow.add("Bills");
				    billsRow.add("");
				    billsRow.add("");
				    billsRow.add("");
				    for (String tax : mapAllTaxes.values())
				    {
					billsRow.add("");
				    }
				    billsRow.add("");
				    billsRow.add("");
				    billsRow.add("");
				    billsRow.add("");

				    listData.add(billsRow);

				    Vector foodBills = new Vector();
				    foodBills.add("Food Bills");
				    foodBills.add("");
				    foodBills.add("");
				    foodBills.add("");
				    for (String tax : mapAllTaxes.values())
				    {
					foodBills.add("");
				    }
				    foodBills.add("");
				    foodBills.add("");
				    foodBills.add("");
				    foodBills.add("");

				    listData.add(foodBills);
				    
				    boolean liquorBillsPrint = true;
				    for (clsPOSSalesFlashColumns objSalesFlashColumns : arrTempListBillWiseSales)
				    {

					if (objSalesFlashColumns.getStrField24() != null && objSalesFlashColumns.getStrField24().equalsIgnoreCase("MultiSettle"))
					{
					    Vector multiSettleRow = new Vector();
					    multiSettleRow.add("Part Settlement");
					    multiSettleRow.add("");
					    multiSettleRow.add("0.00");
					    multiSettleRow.add("0.00");
					    for (String tax : mapAllTaxes.values())
					    {
						multiSettleRow.add("0.00");
					    }
					    multiSettleRow.add("0.00");
					    multiSettleRow.add("0.00");
					    multiSettleRow.add(Double.parseDouble(objSalesFlashColumns.getStrField13()));
					    multiSettleRow.add(objSalesFlashColumns.getStrField7());

					    listData.add(multiSettleRow);

					    continue;
					}

					if (objSalesFlashColumns.getStrField23().equalsIgnoreCase("L") && liquorBillsPrint)
					{
					    Vector liquorBillLabelRow = new Vector();
					    liquorBillLabelRow.add("Liquor Bills");
					    liquorBillLabelRow.add("");
					    liquorBillLabelRow.add("");
					    liquorBillLabelRow.add("");
					    for (String tax : mapAllTaxes.values())
					    {
						liquorBillLabelRow.add("");
					    }
					    liquorBillLabelRow.add("");
					    liquorBillLabelRow.add("");
					    liquorBillLabelRow.add("");
					    liquorBillLabelRow.add("");

					    listData.add(liquorBillLabelRow);

					    liquorBillsPrint = false;
					}

					Vector row = new Vector();
					row.add(objSalesFlashColumns.getStrField4());
					row.add(objSalesFlashColumns.getStrField1());
					row.add(Double.parseDouble(objSalesFlashColumns.getStrField9()));
					row.add(Double.parseDouble(objSalesFlashColumns.getStrField11()));

					Map<String, clsPOSTaxCalculationDtls> mapOfTaxesLocal = objSalesFlashColumns.getMapOfTaxes();
					for (clsPOSTaxCalculationDtls objTaxCalculationDtls : mapOfTaxesLocal.values())
					{
					    row.add(objTaxCalculationDtls.getTaxAmount());
					}

					row.add(Double.parseDouble(objSalesFlashColumns.getStrField15()));
					row.add(Double.parseDouble(objSalesFlashColumns.getStrField21()));
					row.add(Double.parseDouble(objSalesFlashColumns.getStrField13()));
					row.add(objSalesFlashColumns.getStrField7());

					listData.add(row);

					if (Double.parseDouble(objSalesFlashColumns.getStrField11()) > 0)
					{
					    Vector discountRemark = new Vector();
					    discountRemark.add(objSalesFlashColumns.getStrField16());
					    discountRemark.add("");
					    discountRemark.add("");
					    discountRemark.add("");
					    for (String tax : mapAllTaxes.values())
					    {
						discountRemark.add("");
					    }
					    discountRemark.add("");
					    discountRemark.add("");
					    discountRemark.add("");
					    discountRemark.add("");

					    listData.add(discountRemark);
					}
				    }

				    int detailRowCount = listColHeaderArr.size();

				    Vector blankLine = new Vector();
				    blankLine.add("");
				    blankLine.add("");
				    blankLine.add("");
				    blankLine.add("");
				    for (Map.Entry<String, String> taxEntry : mapAllTaxes.entrySet())
				    {
					blankLine.add("");
				    }
				    blankLine.add("");
				    blankLine.add("");
				    blankLine.add("");
				    blankLine.add("");

				    listData.add(blankLine);

				    Vector totalRow = new Vector();
				    totalRow.add("Total");
				    totalRow.add("");
				    totalRow.add(0.00);
				    totalRow.add(0.00);
				    for (Map.Entry<String, String> taxEntry : mapAllTaxes.entrySet())
				    {
					totalRow.add(0.00);
				    }
				    totalRow.add(0.00);
				    totalRow.add(0.00);
				    totalRow.add(0.00);
				    totalRow.add("");

				    listData.add(totalRow);
				    
				    int totalRowNo = listData.size()-2;
					mapRet.put("Col Header", listColHeaderArr);
					mapRet.put("Col Count", cntArrLen);
					mapRet.put("Row Count", totalRowNo);
					mapRet.put("listData", listData);
					mapRet.put("listTotal",listTotal);
					return mapRet;
					
	}
	
	public List<clsShiftMasterModel> funGetPOSWiseShiftList(@RequestParam("POSCode") String posCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List<clsShiftMasterModel> listShiftModel =new ArrayList();
		String posName = null;
		
		StringBuilder sqlShift = new StringBuilder();
		if (posCode.equalsIgnoreCase("All"))
		{
		    sqlShift.append("select max(a.intShiftCode) from tblshiftmaster a group by a.intShiftCode ");
		}
		else
		{
		    sqlShift.append("select a.intShiftCode from tblshiftmaster a where a.strPOSCode='" + posCode + "' ");
		}

		List listSql = objBaseService.funGetList(sqlShift, "sql");
		if(listSql.size()>0)
		{
			for(int i=0 ;i<listSql.size();i++ )
		      {
				   Object[] obj= (Object[]) listSql.get(i);
				   clsShiftMasterModel objModel=new clsShiftMasterModel();
				   objModel.setIntShiftCode(obj[0].toString());
				   listShiftModel.add(objModel);
		      } 	
		}
		return listShiftModel;
	}
	
	public Map funConsolisdatedDiscountWiseReport(String posCode,String fromDate,String toDate,String enableShiftYN,String strShiftNo,String reportType,Map hm) throws Exception
	{
//		HashMap hm = new HashMap();
		Map<Integer, List<String>> mapExcelItemDtl = new HashMap<Integer, List<String>>();
		    List<String> arrListTotal = new ArrayList<String>();
		    List<String> arrHeaderList = new ArrayList<String>();
		    double totalDis = 0, totalDiscValue = 0;
		    double totalAmount = 0, totalSalesNetTotal = 0.0;
		    double totalDisOnAmount = 0;
		    double totalConsolidatedDiscAmt = 0.0;
		    if (reportType.equalsIgnoreCase("Consolidated Discount"))
		    {
		    	List<clsPOSBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();
				StringBuilder sbSqlLiveDisc = new StringBuilder();
				StringBuilder sbSqlQFileDisc = new StringBuilder();
				StringBuilder sqlBuilder = new StringBuilder();
				Map<String, clsPOSBillItemDtlBean> mapReasonDtl = new HashMap<>();

				
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT sum(b.dblDiscountAmt)DiscAmt,g.strReasonCode,g.strReasonName "
					+ ",sum(b.dblAmount)-sum(b.dblDiscountAmt)NetRevenue,sum(a.intBillSeriesPaxNo) "
					+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f,tblreasonmaster g\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND a.strPOSCode=f.strPOSCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND b.strItemCode=e.strItemCode \n"
					+ "AND d.strSubGroupCode=e.strSubGroupCode \n"
					+ "AND c.strGroupCode=d.strGroupCode\n"
					+ "and a.strReasonCode=g.strReasonCode\n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sqlBuilder.append("GROUP BY g.strReasonName;");
				List listLiveDisc = objBaseService.funGetList(sqlBuilder, "sql");
				if(listLiveDisc.size()>0)
				{
					for(int i=0;i<listLiveDisc.size();i++)
					{
						Object[] obj = (Object[]) listLiveDisc.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
		
					    String reasonCode = obj[1].toString();
					    if (reasonCode != null)
					    {
						if (mapReasonDtl.containsKey(reasonCode))
						{
						    objBean = mapReasonDtl.get(reasonCode);
						    objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() +Integer.parseInt(obj[4].toString()));
		
						    mapReasonDtl.put(reasonCode, objBean);
						}
						else
						{
						    objBean = new clsPOSBillItemDtlBean();
						    objBean.setStrReasonName(obj[2].toString());
						    objBean.setDblDiscountAmt(Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
		
						    mapReasonDtl.put(reasonCode, objBean);
						}
					    }
					}
				}

				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT sum(b.dblDiscAmt)DiscAmt,g.strReasonCode,g.strReasonName "
					+ ",sum(b.dblAmount)-sum(b.dblDiscAmt)NetRevenue,0 "
					+ "FROM tblqbillhd a,tblqbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f,tblreasonmaster g\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND a.strPOSCode=f.strPOSCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND left(b.strItemCode,7)=e.strItemCode \n"
					+ "AND d.strSubGroupCode=e.strSubGroupCode \n"
					+ "AND c.strGroupCode=d.strGroupCode\n"
					+ "and a.strReasonCode=g.strReasonCode\n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sqlBuilder.append("GROUP BY g.strReasonName;");

				List listQfileDisc = objBaseService.funGetList(sqlBuilder, "sql");
				if(listQfileDisc.size()>0)
				{
					for(int i=0;i<listQfileDisc.size();i++)
					{
						Object[] obj = (Object[]) listQfileDisc.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
		
					    String reasonCode = obj[1].toString();
					    if (reasonCode != null)
					    {
						if (mapReasonDtl.containsKey(reasonCode))
						{
						    objBean = mapReasonDtl.get(reasonCode);
						    objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() +Integer.parseInt(obj[4].toString()));
		
						    mapReasonDtl.put(reasonCode, objBean);
						}
						else
						{
						    objBean = new clsPOSBillItemDtlBean();
						    objBean.setStrReasonName(obj[2].toString());
						    objBean.setDblDiscountAmt(Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
		
						    mapReasonDtl.put(reasonCode, objBean);
						}
					    }
					}
				}

				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT sum(b.dblDiscountAmt)DiscAmt,g.strReasonCode,g.strReasonName "
					+ ",sum(b.dblAmount)-sum(b.dblDiscountAmt)NetRevenue,sum(a.intBillSeriesPaxNo) "
					+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f,tblreasonmaster g\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND a.strPOSCode=f.strPOSCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND b.strItemCode=e.strItemCode \n"
					+ "AND d.strSubGroupCode=e.strSubGroupCode \n"
					+ "AND c.strGroupCode=d.strGroupCode\n"
					+ "and a.strReasonCode=g.strReasonCode\n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sqlBuilder.append("GROUP BY g.strReasonName;");

				listLiveDisc = objBaseService.funGetList(sqlBuilder, "sql");
				if(listLiveDisc.size()>0)
				{
					for(int i=0;i<listLiveDisc.size();i++)
					{	
						Object[] obj = (Object[]) listLiveDisc.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
					    String reasonCode = obj[1].toString();
					    if (reasonCode != null)
					    {
						if (mapReasonDtl.containsKey(reasonCode))
						{
						    objBean = mapReasonDtl.get(reasonCode);
						    objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
						    mapReasonDtl.put(reasonCode, objBean);
						}
						else
						{
						    objBean = new clsPOSBillItemDtlBean();
						    objBean.setStrReasonName(obj[2].toString());
						    objBean.setDblDiscountAmt(Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
						    mapReasonDtl.put(reasonCode, objBean);
						}
					    }
					}

				}
			
				sqlBuilder.setLength(0);
				sqlBuilder.append("SELECT sum(b.dblDiscAmt)DiscAmt,g.strReasonCode,g.strReasonName "
					+ ",sum(b.dblAmount)-sum(b.dblDiscAmt)NetRevenue,0 "
					+ "FROM tblbillhd a,tblbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f,tblreasonmaster g\n"
					+ "WHERE a.strBillNo=b.strBillNo \n"
					+ "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
					+ "AND a.strPOSCode=f.strPOSCode \n"
					+ "AND a.strClientCode=b.strClientCode \n"
					+ "AND left(b.strItemCode,7)=e.strItemCode \n"
					+ "AND d.strSubGroupCode=e.strSubGroupCode \n"
					+ "AND c.strGroupCode=d.strGroupCode\n"
					+ "and a.strReasonCode=g.strReasonCode\n"
					+ "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' \n");
				if (!posCode.equalsIgnoreCase("All"))
				{
				    sqlBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sqlBuilder.append("GROUP BY g.strReasonName;");

				listQfileDisc = objBaseService.funGetList(sqlBuilder, "sql");
				if(listQfileDisc.size()>0)
				{
					for(int i=0;i<listQfileDisc.size();i++)
					{	
						Object[] obj = (Object[]) listQfileDisc.get(i);
						clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
					    String reasonCode = obj[1].toString();
					    if (reasonCode != null)
					    {
						if (mapReasonDtl.containsKey(reasonCode))
						{
						    objBean = mapReasonDtl.get(reasonCode);
						    objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble( obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
						    mapReasonDtl.put(reasonCode, objBean);
						}
						else
						{
						    objBean = new clsPOSBillItemDtlBean();
						    objBean.setStrReasonName(obj[2].toString());
						    objBean.setDblDiscountAmt( Double.parseDouble(obj[0].toString()));
						    objBean.setDblNetTotal(Double.parseDouble( obj[3].toString()));
						    objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
						    mapReasonDtl.put(reasonCode, objBean);
						}
					    }
					}
				}
				

				List<clsPOSBillItemDtlBean> listOfDiscount = new ArrayList<clsPOSBillItemDtlBean>();
				listOfDiscount.addAll(mapReasonDtl.values());
			  
			  
				//for complimentary 
					mapReasonDtl = new HashMap<>();
					StringBuilder sbSqlLive = new StringBuilder();
					StringBuilder sbSqlQBill = new StringBuilder();
					StringBuilder sqlLiveModifierBuilder = new StringBuilder();
					StringBuilder sqlQModifierBuilder = new StringBuilder();
					sbSqlLive.setLength(0);
					sbSqlQBill.setLength(0);
					sqlLiveModifierBuilder.setLength(0);
					sqlQModifierBuilder.setLength(0);

					//live data
					sbSqlLive.append("select sum(b.dblRate* b.dblQuantity) AS Disc,a.strReasonCode,i.strReasonName "
						+ ",SUM(b.dblRate* b.dblQuantity) AS NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblreasonmaster i "
						+ " WHERE a.strBillNo = b.strBillNo  "
						+ " AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
						+ " AND a.strPOSCode=e.strPosCode  "
						+ " AND b.strItemCode=f.strItemCode  "
						+ " AND f.strSubGroupCode=g.strSubGroupCode and i.strReasonCode=a.strReasonCode "
						+ " AND g.strGroupCode=h.strGroupCode  "
					);

					//Q data
					sbSqlQBill.append("select sum(b.dblRate* b.dblQuantity) AS Disc,a.strReasonCode,i.strReasonName "
						+ ",SUM(b.dblRate* b.dblQuantity) AS NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblreasonmaster i "
						+ " WHERE a.strBillNo = b.strBillNo  "
						+ " AND DATE(a.dteBillDate) =date(b.dteBillDate)  "
						+ " AND a.strPOSCode=e.strPosCode  "
						+ " AND b.strItemCode=f.strItemCode  "
						+ " AND f.strSubGroupCode=g.strSubGroupCode and i.strReasonCode=a.strReasonCode "
						+ " AND g.strGroupCode=h.strGroupCode  ");

					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlLive.append(" AND a.strPOSCode = '" + posCode + "' ");
					    sbSqlQBill.append(" AND a.strPOSCode = '" + posCode + "' ");
					    sqlLiveModifierBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
					    sqlQModifierBuilder.append(" AND a.strPOSCode = '" + posCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sqlLiveModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sqlQModifierBuilder.append(" and a.intShiftCode = '" + strShiftNo + "' ");
					    }
					}
					sbSqlLive.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by a.strReasonCode"
						+ " order by a.strReasonCode;");
					sbSqlQBill.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by a.strReasonCode"
						+ " order by a.strReasonCode;");
					sqlLiveModifierBuilder.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by a.strReasonCode"
						+ " order by a.strReasonCode;");
					sqlQModifierBuilder.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by a.strReasonCode"
						+ " order by a.strReasonCode;");

					List listLiveCompl = objBaseService.funGetList(sbSqlLive, "sql");
					if(listLiveCompl.size()>0)
					{
						for(int i=0;i<listLiveCompl.size();i++)
						{	
						    Object[] obj = (Object[]) listLiveCompl.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String reasonCode = obj[1].toString();
	
						    if (mapReasonDtl.containsKey(reasonCode))
						    {
							objBean = mapReasonDtl.get(reasonCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[2].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						}
					}
					

					List listQfileCompl = objBaseService.funGetList(sbSqlQBill,"sql");
					if(listQfileCompl.size()>0)
					{
						for(int i=0;i<listQfileCompl.size();i++)
						{	
						    Object[] obj = (Object[]) listQfileCompl.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String reasonCode = obj[1].toString();
	
						    if (mapReasonDtl.containsKey(reasonCode))
						    {
							objBean = mapReasonDtl.get(reasonCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[2].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						}
					}
					

					List<clsPOSBillItemDtlBean> listOfComplimentaryDiscount = new ArrayList<clsPOSBillItemDtlBean>();
					listOfComplimentaryDiscount.addAll(mapReasonDtl.values());

			 
				//for promotion
					mapReasonDtl = new HashMap<>();
					StringBuilder sqlLiveData = new StringBuilder();
					StringBuilder sqlQData = new StringBuilder();

					sqlLiveData.append("SELECT sum(a.dblQuantity*a.dblRate) AS Disc,a.strPromotionCode,c.strPromoName "
						+ ",sum(a.dblQuantity*a.dblRate) AS NetRevenue,sum(b.intBillSeriesPaxNo)Pax "
						+ "FROM tblbillpromotiondtl a,tblbillhd b,tblpromotionmaster c\n"
						+ "WHERE a.strBillNo=b.strBillNo and a.strPromotionCode=c.strPromoCode \n"
						+ "AND DATE(b.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");

					sqlQData.append("SELECT sum(a.dblQuantity*a.dblRate) AS Disc,a.strPromotionCode,c.strPromoName "
						+ ",sum(a.dblQuantity*a.dblRate) AS NetRevenue,sum(b.intBillSeriesPaxNo)Pax "
						+ "FROM tblqbillpromotiondtl a,tblqbillhd b,tblpromotionmaster c\n"
						+ "WHERE a.strBillNo=b.strBillNo and a.strPromotionCode=c.strPromoCode \n"
						+ "AND DATE(b.dteBillDate) BETWEEN '" + fromDate + "' and '" + toDate + "' ");
					if (!posCode.equalsIgnoreCase("All"))
					{
					    sqlLiveData.append(" and b.strPOSCode='" + posCode + "' ");
					    sqlQData.append(" and b.strPOSCode='" + posCode + "' ");
					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sqlLiveData.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sqlQData.append(" and a.intShiftCode = '" + strShiftNo + "' ");
					    }
					}
					sqlLiveData.append(" group by a.strPromotionCode ");
					sqlQData.append(" group by a.strPromotionCode ");

					List listLivePromotion = objBaseService.funGetList(sqlLiveData, "sql");
					if(listLivePromotion.size()>0)
					{
						for(int i=0;i<listLivePromotion.size();i++)
						{	
						    Object[] obj = (Object[]) listLivePromotion.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String reasonCode = obj[1].toString();
	
						    if (mapReasonDtl.containsKey(reasonCode))
						    {
							objBean = mapReasonDtl.get(reasonCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[2].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						}
					}
			

					List listQFilePromotion = objBaseService.funGetList(sqlQData, "sql");
					if(listQFilePromotion.size()>0)
					{
						for(int i=0;i<listQFilePromotion.size();i++)
						{	
						    Object[] obj = (Object[]) listQFilePromotion.get(i);
							clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String reasonCode = obj[1].toString();
	
						    if (mapReasonDtl.containsKey(reasonCode))
						    {
							objBean = mapReasonDtl.get(reasonCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[2].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[0].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(reasonCode, objBean);
						    }
						}
					}
					List<clsPOSBillItemDtlBean> listOfPromotionDiscount = new ArrayList<clsPOSBillItemDtlBean>();
					listOfPromotionDiscount.addAll(mapReasonDtl.values());

				//group wise sales complimentary
					sbSqlLive.setLength(0);
					sbSqlQBill.setLength(0);

					//live data
					sbSqlLive.append("SELECT h.strGroupCode,h.strGroupName, SUM(b.dblRate* b.dblQuantity) AS Disc\n"
						+ ", SUM(b.dblRate* b.dblQuantity) AS NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblbillhd a,tblbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h\n"
						+ " WHERE a.strBillNo = b.strBillNo AND DATE(a.dteBillDate) = DATE(b.dteBillDate) AND a.strPOSCode=e.strPosCode "
						+ " AND b.strItemCode=f.strItemCode AND f.strSubGroupCode=g.strSubGroupCode "
						+ " AND g.strGroupCode=h.strGroupCode ");

					//Q data
					sbSqlQBill.append("SELECT h.strGroupCode,h.strGroupName, SUM(b.dblRate* b.dblQuantity) AS Disc\n"
						+ ", SUM(b.dblRate* b.dblQuantity) AS NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblqbillhd a,tblqbillcomplementrydtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h\n"
						+ " WHERE a.strBillNo = b.strBillNo AND DATE(a.dteBillDate) = DATE(b.dteBillDate) AND a.strPOSCode=e.strPosCode "
						+ " AND b.strItemCode=f.strItemCode AND f.strSubGroupCode=g.strSubGroupCode "
						+ " AND g.strGroupCode=h.strGroupCode ");
					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlLive.append(" AND a.strPOSCode = '" + posCode + "' ");
					    sbSqlQBill.append(" AND a.strPOSCode = '" + posCode + "' ");

					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");

					    }
					}
					sbSqlLive.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by h.strGroupCode"
						+ " order by h.strGroupCode;");
					sbSqlQBill.append(" and date(a.dteBillDate) Between '" + fromDate + "' and '" + toDate + "' "
						+ " group by h.strGroupCode"
						+ " order by h.strGroupCode;");

					mapReasonDtl = new HashMap<>();
					List listLiveGroup =objBaseService.funGetList(sbSqlLive, "sql");
					if(listLiveGroup.size()>0)
					{
					    for(int i=0;i<listLiveGroup.size();i++)
					    {	
						    Object[] obj = (Object[]) listLiveGroup.get(i);
					    	totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
						      
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
					    } 
					}
				

					List listQFileGroup = objBaseService.funGetList(sbSqlQBill, "sql");
					if(listQFileGroup.size()>0)
					{
					    for(int i=0;i<listQFileGroup.size();i++)
					    {	
						    Object[] obj = (Object[]) listQFileGroup.get(i);
					    	totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
						      
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
					    } 
					}
					

					//group wise sales promotions
					sbSqlLive.setLength(0);
					sbSqlQBill.setLength(0);
					sqlLiveModifierBuilder.setLength(0);
					sqlQModifierBuilder.setLength(0);
					sbSqlLive.append("SELECT e.strGroupCode,e.strGroupName,sum(a.dblQuantity*a.dblRate) AS Disc\n"
						+ ",sum(a.dblQuantity*a.dblRate) AS NetRevenue,sum(b.intBillSeriesPaxNo)Pax "
						+ " FROM tblbillpromotiondtl a,tblbillhd b,tblpromotionmaster c,tblitemmaster d,tblgrouphd e,tblsubgrouphd f\n"
						+ " WHERE a.strBillNo=b.strBillNo and a.strPromotionCode=c.strPromoCode \n"
						+ " and a.strItemCode=d.strItemCode and d.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=e.strGroupCode\n"
						+ " AND DATE(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");

					sbSqlQBill.append("SELECT e.strGroupCode,e.strGroupName,sum(a.dblQuantity*a.dblRate) AS Disc\n"
						+ ",sum(a.dblQuantity*a.dblRate) AS NetRevenue,sum(b.intBillSeriesPaxNo)Pax "
						+ " FROM tblqbillpromotiondtl a,tblqbillhd b,tblpromotionmaster c,tblitemmaster d,tblgrouphd e,tblsubgrouphd f\n"
						+ " WHERE a.strBillNo=b.strBillNo and a.strPromotionCode=c.strPromoCode \n"
						+ " and a.strItemCode=d.strItemCode and d.strSubGroupCode=f.strSubGroupCode and f.strGroupCode=e.strGroupCode\n"
						+ " AND DATE(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");

					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlLive.append(" and b.strPOSCode='" + posCode + "' ");
					    sbSqlQBill.append(" and b.strPOSCode='" + posCode + "' ");

					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sbSqlLive.append(" and b.intShiftCode = '" + strShiftNo + "' ");
						sbSqlQBill.append(" and b.intShiftCode = '" + strShiftNo + "' ");

					    }
					}
					sbSqlLive.append(" GROUP BY e.strGroupCode, e.strGroupName");
					sbSqlQBill.append(" GROUP BY e.strGroupCode, e.strGroupName");

					listLiveGroup = objBaseService.funGetList(sbSqlLive, "sql");
					if(listLiveGroup.size()>0)
					{
						for(int i=0;i<listLiveGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listLiveGroup.get(i);
							totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
						    
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}
					}
					
					listQFileGroup = objBaseService.funGetList(sbSqlQBill, "sql");
					if(listQFileGroup.size()>0)
					{
						for(int i=0;i<listQFileGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listQFileGroup.get(i);
							totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
						    
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}
					}
					//group wise sales data
					sbSqlLive.setLength(0);
					sbSqlQBill.setLength(0);
					sqlLiveModifierBuilder.setLength(0);
					sqlQModifierBuilder.setLength(0);
					sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,SUM(b.dblDiscountAmt)Disc\n"
						+ ",sum(b.dblAmount)-sum(b.dblDiscountAmt)NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f\n"
						+ " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strPOSCode=f.strPOSCode AND a.strClientCode=b.strClientCode \n"
						+ " AND b.strItemCode=e.strItemCode AND d.strSubGroupCode=e.strSubGroupCode AND c.strGroupCode=d.strGroupCode\n"
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");
					sbSqlQBill.append("SELECT c.strGroupCode,c.strGroupName,SUM(b.dblDiscountAmt)Disc\n"
						+ ",sum(b.dblAmount)-sum(b.dblDiscountAmt)NetRevenue,sum(a.intBillSeriesPaxNo)Pax "
						+ " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f\n"
						+ " WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strPOSCode=f.strPOSCode AND a.strClientCode=b.strClientCode \n"
						+ " AND b.strItemCode=e.strItemCode AND d.strSubGroupCode=e.strSubGroupCode AND c.strGroupCode=d.strGroupCode\n"
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'");

					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlLive.append(" and a.strPOSCode='" + posCode + "' ");
					    sbSqlQBill.append(" and a.strPOSCode='" + posCode + "' ");

					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");

					    }
					}
					sbSqlLive.append(" GROUP BY c.strGroupCode, c.strGroupName");
					sbSqlQBill.append(" GROUP BY c.strGroupCode, c.strGroupName");

					listLiveGroup = objBaseService.funGetList(sbSqlLive, "sql");
					if(listLiveGroup.size()>0)
					{
						for(int i=0;i<listLiveGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listLiveGroup.get(i);
							totalSalesNetTotal += Double.parseDouble(obj[3].toString());
						    totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
	
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}  
					}
				
					listQFileGroup = objBaseService.funGetList(sbSqlQBill, "sql");
					if(listQFileGroup.size()>0)
					{
						for(int i=0;i<listQFileGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listQFileGroup.get(i);
							totalSalesNetTotal += Double.parseDouble(obj[3].toString());
						    totalConsolidatedDiscAmt+=Double.parseDouble(obj[2].toString());
	
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() + Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() + Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt(Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}  
					}
					//group wise sales modifiers
					sbSqlLive.setLength(0);
					sbSqlQBill.setLength(0);
					sqlLiveModifierBuilder.setLength(0);
					sqlQModifierBuilder.setLength(0);
					sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,SUM(b.dblDiscAmt)Disc\n"
						+ ",sum(b.dblAmount)-sum(b.dblDiscAmt)NetRevenue,0 Pax\n"
						+ " FROM tblbillhd a,tblbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f\n"
						+ " WHERE a.strBillNo=b.strBillNo \n"
						+ " AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strPOSCode=f.strPOSCode \n"
						+ " AND a.strClientCode=b.strClientCode \n"
						+ " AND left(b.strItemCode,7)=e.strItemCode \n"
						+ " AND d.strSubGroupCode=e.strSubGroupCode \n"
						+ " AND c.strGroupCode=d.strGroupCode "
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");
					sbSqlQBill.append("SELECT c.strGroupCode,c.strGroupName,SUM(b.dblDiscAmt)Disc\n"
						+ ",sum(b.dblAmount)-sum(b.dblDiscAmt)NetRevenue,0 Pax\n"
						+ " FROM tblqbillhd a,tblqbillmodifierdtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f\n"
						+ " WHERE a.strBillNo=b.strBillNo \n"
						+ " AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
						+ " AND a.strPOSCode=f.strPOSCode \n"
						+ " AND a.strClientCode=b.strClientCode \n"
						+ " AND left(b.strItemCode,7)=e.strItemCode \n"
						+ " AND d.strSubGroupCode=e.strSubGroupCode \n"
						+ " AND c.strGroupCode=d.strGroupCode "
						+ " AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n");

					if (!posCode.equalsIgnoreCase("All"))
					{
					    sbSqlLive.append(" and a.strPOSCode='" + posCode + "' ");
					    sbSqlQBill.append(" and a.strPOSCode='" + posCode + "' ");

					}

					if (enableShiftYN.equalsIgnoreCase("Y"))
					{
					    if (enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
					    {
						sbSqlLive.append(" and a.intShiftCode = '" + strShiftNo + "' ");
						sbSqlQBill.append(" and a.intShiftCode = '" + strShiftNo + "' ");

					    }
					}
					sbSqlLive.append(" GROUP BY c.strGroupCode, c.strGroupName");
					sbSqlQBill.append(" GROUP BY c.strGroupCode, c.strGroupName");

					listLiveGroup = objBaseService.funGetList(sbSqlLive, "sql");
					if(listLiveGroup.size()>0)
					{
						for(int i=0;i<listLiveGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listLiveGroup.get(i);
							totalSalesNetTotal += Double.parseDouble(obj[3].toString());
						     totalConsolidatedDiscAmt+= Double.parseDouble(obj[2].toString());
	
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() +  Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}
					}
			

					listQFileGroup = objBaseService.funGetList(sbSqlQBill,"sql");
					if(listQFileGroup.size()>0)
					{
						for(int i=0;i<listQFileGroup.size();i++)
						{	
						    Object[] obj = (Object[]) listQFileGroup.get(i);
							totalSalesNetTotal += Double.parseDouble(obj[3].toString());
						     totalConsolidatedDiscAmt+= Double.parseDouble(obj[2].toString());
	
						    clsPOSBillItemDtlBean objBean = new clsPOSBillItemDtlBean();
	
						    String groupCode = obj[0].toString();
	
						    if (mapReasonDtl.containsKey(groupCode))
						    {
							objBean = mapReasonDtl.get(groupCode);
	
							objBean.setDblDiscountAmt(objBean.getDblDiscountAmt() +  Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal(objBean.getDblNetTotal() +  Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(objBean.getIntBillSeriesPaxNo() + Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						    else
						    {
							objBean = new clsPOSBillItemDtlBean();
							objBean.setStrReasonName(obj[1].toString());
							objBean.setDblDiscountAmt( Double.parseDouble(obj[2].toString()));
							objBean.setDblNetTotal( Double.parseDouble(obj[3].toString()));
							objBean.setIntBillSeriesPaxNo(Integer.parseInt(obj[4].toString()));
	
							mapReasonDtl.put(groupCode, objBean);
						    }
						}
					}
			
					
					double groupTotal = 0.0;
					List<clsPOSBillItemDtlBean> listOfGroup = new ArrayList<clsPOSBillItemDtlBean>();
					listOfGroup.addAll(mapReasonDtl.values());

					double netRevenuePer = 0.0;
					netRevenuePer = totalConsolidatedDiscAmt / totalSalesNetTotal * 100;
					
					hm.put("listOfComplimentaryDiscount", listOfComplimentaryDiscount);
					hm.put("listOfPromotionDiscount", listOfPromotionDiscount);
					hm.put("listOfDiscount", listOfDiscount);
					hm.put("consolidateDisc", Math.rint(totalConsolidatedDiscAmt));
					hm.put("netRevenue", Math.rint(totalSalesNetTotal));
					hm.put("groupTotal", Math.rint(groupTotal));
					hm.put("listOfGroupWiseDiscount", listOfGroup);
			 
				  
				
					
				
		    }
		
		return hm;
		
	}
	
	public List funReprintDocsDtailReport(String fromDate,String toDate,String strType,String userName,String documentNo,String type) throws Exception
	{
		List<clsReprintDocs> listOfReprintTextData = new ArrayList<clsReprintDocs>();
		if(type.equalsIgnoreCase("Detail"))
		{
			StringBuilder sqlQData = new StringBuilder();
			sqlQData.setLength(0);
			sqlQData.append("select a.strBillNo,DATE_FORMAT(b.dtePOSDate,'%m-%d-%Y'),b.strUserCreated,ifnull(c.strReasonName,''),\n"
				+ " b.strRemarks,a.dblGrandTotal,\n"
				+ "time(b.dtePOSDate)  from tblbillhd a,tblaudit b left outer join tblreasonmaster c  \n"
				+ "on b.strReasonCode=c.strReasonCode  "
				+ " where a.strBillNo=b.strDocNo  "
				+ " and date(b.dtePOSDate) between '" + fromDate + "' and '" + toDate + "' ");

			if (!userName.equals("All"))
			{
			    sqlQData.append(" and b.strUserCreated='" + userName + "'");
			}
			if (!documentNo.equals("All"))
			{
			    sqlQData.append(" and b.strDocNo='" + documentNo + "'");
			}
			List listSettlementWiseQData = objBaseService.funGetList(sqlQData, "sql");

			if(listSettlementWiseQData.size()>0)
			{
				for(int i=0;i<listSettlementWiseQData.size();i++)
				{	
				Object[] obj = (Object[]) listSettlementWiseQData.get(i);	
				clsReprintDocs objReprint = new clsReprintDocs();
			    objReprint.setBillNo(obj[0].toString());
			    objReprint.setDate(obj[1].toString());
			    objReprint.setUser(obj[2].toString());
			    objReprint.setReason(obj[3].toString());
			    objReprint.setRemark(obj[4].toString());
			    objReprint.setTotal(Double.parseDouble(obj[5].toString()));
			    objReprint.setTime(obj[6].toString());
			    listOfReprintTextData.add(objReprint);
				}
			}
			
			
			sqlQData.setLength(0);
			sqlQData.append("select a.strBillNo,DATE_FORMAT(b.dtePOSDate,'%m-%d-%Y'),b.strUserCreated,ifnull(c.strReasonName,''),\n"
				+ " b.strRemarks,a.dblGrandTotal,\n"
				+ "time(b.dtePOSDate)  from tblqbillhd a,tblaudit b left outer join tblreasonmaster c  \n"
				+ "on b.strReasonCode=c.strReasonCode  "
				+ " where a.strBillNo=b.strDocNo  "
				+ " and date(b.dtePOSDate) between '" + fromDate + "' and '" + toDate + "' ");

			if (!userName.equals("All"))
			{
			    sqlQData.append(" and b.strUserCreated='" + userName + "'");
			}
			if (!documentNo.equals("All"))
			{
			    sqlQData.append(" and b.strDocNo='" + documentNo + "'");
			}
			listSettlementWiseQData = objBaseService.funGetList(sqlQData, "sql");

			if(listSettlementWiseQData.size()>0)
			{
				for(int i=0;i<listSettlementWiseQData.size();i++)
				{	
				Object[] obj = (Object[]) listSettlementWiseQData.get(i);	
				clsReprintDocs objReprint = new clsReprintDocs();
			    objReprint.setBillNo(obj[0].toString());
			    objReprint.setDate(obj[1].toString());
			    objReprint.setUser(obj[2].toString());
			    objReprint.setReason(obj[3].toString());
			    objReprint.setRemark(obj[4].toString());
			    objReprint.setTotal(Double.parseDouble(obj[5].toString()));
			    objReprint.setTime(obj[6].toString());
			    listOfReprintTextData.add(objReprint);
				}
			}

		}
		else
		{
			StringBuilder sqlQData = new StringBuilder();

			sqlQData.setLength(0);
			sqlQData.append("select  a.strBillNo,DATE_FORMAT(b.dtePOSDate,'%m-%d-%Y'),b.strUserCreated,a.dblGrandTotal,count(*)as count \n"
				+ "from tblbillhd a,tblaudit b left outer join tblreasonmaster c on b.strReasonCode=c.strReasonCode  where a.strBillNo=b.strDocNo  "
				+ " and date(b.dtePOSDate) between '" + fromDate + "' and '" + toDate + "'");
			if (!userName.equals("All"))
			{
			    sqlQData.append(" and b.strUserCreated='" + userName + "'");
			}
			if (!documentNo.equals("All"))
			{
			    sqlQData.append(" and b.strDocNo='" + documentNo + "'");
			}
			sqlQData.append(" group by a.strBillNo,b.strUserCreated ");

			List listSettlementWiseQData = objBaseService.funGetList(sqlQData, "sql");

			if(listSettlementWiseQData.size()>0)
			{
				for(int i=0;i<listSettlementWiseQData.size();i++)
				{	
				Object[] obj = (Object[]) listSettlementWiseQData.get(i);	
			    clsReprintDocs objReprint = new clsReprintDocs();
			    objReprint.setBillNo(obj[0].toString());
			    objReprint.setDate(obj[1].toString());
			    objReprint.setUser(obj[2].toString());
			    objReprint.setTotal(Double.parseDouble(obj[3].toString()));
			    objReprint.setCount(Integer.parseInt(obj[4].toString()));
			    listOfReprintTextData.add(objReprint);
				}
			}
			sqlQData.setLength(0);
			sqlQData.append("select  a.strBillNo,DATE_FORMAT(b.dtePOSDate,'%m-%d-%Y'),b.strUserCreated,a.dblGrandTotal,count(*)as count \n"
				+ "from tblqbillhd a,tblaudit b left outer join tblreasonmaster c on b.strReasonCode=c.strReasonCode where a.strBillNo=b.strDocNo  "
				+ " and date(b.dtePOSDate) between '" + fromDate + "' and '" + toDate + "'");
			if (!userName.equals("All"))
			{
			    sqlQData.append(" and b.strUserCreated='" + userName + "'");
			}
			if (!documentNo.equals("All"))
			{
			    sqlQData.append(" and b.strDocNo='" + documentNo + "'");
			}
			sqlQData.append(" group by a.strBillNo,b.strUserCreated ");
			listSettlementWiseQData = objBaseService.funGetList(sqlQData, "sql");

			if(listSettlementWiseQData.size()>0)
			{
				for(int i=0;i<listSettlementWiseQData.size();i++)
				{	
				Object[] obj = (Object[]) listSettlementWiseQData.get(i);	
			    clsReprintDocs objReprint = new clsReprintDocs();
			    objReprint.setBillNo(obj[0].toString());
			    objReprint.setDate(obj[1].toString());
			    objReprint.setUser(obj[2].toString());
			    objReprint.setTotal(Double.parseDouble(obj[3].toString()));
			    objReprint.setCount(Integer.parseInt(obj[4].toString()));
			    listOfReprintTextData.add(objReprint);
				}
			}

		}
		
		return listOfReprintTextData;
		
	}
	
	
	
	

	public Map funProcessKDSFlashReport(String clientCode,String fromDate,String toDate,String reportType,String posCode,String costCenterCode,String waiterNo,String strType) 
	{
		StringBuilder sbSql = new StringBuilder();
		List listArr = new ArrayList();
		Map<String, List<clsPOSBillDtl>> hmKDSFlashData = new TreeMap<String, List<clsPOSBillDtl>>();
		int colCount = 5;
		DecimalFormat decimalFormtFor2DecPoint = new DecimalFormat("0.00");
	    Map hmKDSData=new TreeMap();
		try 
		{
			double sumBillAmt = 0.00, sumNewAmt = 0.00;
			double sumQty = 0.00, discAmt = 0.0, sumTotalAmt = 0.00;
			
			if (reportType.equalsIgnoreCase("Group"))
		    {
				sbSql.setLength(0);
				sbSql.append("SELECT a.strBillNo,b.strKOTNo, DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " TIME(b.dteBillDate) as tmeKOTTime,TIME(b.tmeOrderProcessing) as OrderProcessingTime, "
				    + " TIME(b.tmeOrderPickup) as OrderPickupTime,b.strItemCode, "
				    + " if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) )  AS processtimediff, "
				    + " TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)) AS pickuptimediff,"
				    + " e.strCostCenterName,f.strWShortName,g.strGroupName "
				    + " FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " ,tblgrouphd g,tblsubgrouphd h"
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " and DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
				    + " and c.strSubGroupCode = h.strSubGroupCode "
				    + " AND h.strGroupCode = g.strGroupCode ");
				if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and d.strCostCenterCode='" + costCenterCode + "'  ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" + waiterNo + "'  ");
			    }

			    sbSql.append(" GROUP BY g.strGroupCode  ");
			    sbSql.append(" Order By  g.strGroupCode DESC");
			    
			    String time = "", timeForGroupWise = "";
			    long sum = 0, sumOfGroupWise = 0;
			    int i = 0;
			    String date4 = "";
			    int qty = 0, quantity = 0;
			    Date date1;
			    Date dateForGroup;
			    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			    
			    
				List listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) 
				{

					for (int cnt = 0; cnt < listSql.size(); cnt++) 
					{
						Object[] obj = (Object[]) listSql.get(cnt);
						clsPOSBillDtl objBill = new clsPOSBillDtl();

						qty = qty +(new BigDecimal(obj[4].toString())).intValue();
						quantity = quantity +(new BigDecimal(obj[4].toString())).intValue();
						objBill.setDblQuantity(qty);
						objBill.setStrGroupName(obj[15].toString());
						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sum = sum + date1.getTime();

						timeForGroupWise = obj[11].toString();
						dateForGroup = timeFormat.parse(timeForGroupWise);
						sumOfGroupWise = dateForGroup.getTime();

						date4 = timeFormat.format(new Date(sumOfGroupWise / qty));
						objBill.setTmeOrderProcessing(date4);
						//String key=rsSales.getString(3)+"!"+rsSales.getString(14);
						String key = obj[15].toString() + "!" + obj[2].toString();

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);

						i++;
					}

				}
				
				sbSql.setLength(0);
			    sbSql.append("SELECT a.strBillNo,b.strKOTNo,DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " TIME(b.dteBillDate) as tmeKOTTime,TIME(b.tmeOrderProcessing) as OrderProcessingTime, "
				    + " TIME(b.tmeOrderPickup) as OrderPickupTime,b.strItemCode, "
				    + " if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) )  AS processtimediff, "
				    + " TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)) AS pickuptimediff,"
				    + " e.strCostCenterName,f.strWShortName,g.strGroupName "
				    + " FROM tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " ,tblgrouphd g,tblsubgrouphd h"
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " AND DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				    + " and c.strSubGroupCode = h.strSubGroupCode "
				    + " AND h.strGroupCode = g.strGroupCode ");
			    if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and d.strCostCenterCode=='" + costCenterCode + "'   ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" + waiterNo+ "'  ");
			    }
			    sbSql.append(" GROUP BY g.strGroupCode ");
			    sbSql.append(" Order By g.strGroupCode DESC");
			    
			    
			    listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) 
				{

					for (int cnt = 0; cnt < listSql.size(); cnt++) 
					{
						Object[] obj = (Object[]) listSql.get(cnt);
						clsPOSBillDtl objBill = new clsPOSBillDtl();

						qty = qty +(new BigDecimal(obj[4].toString())).intValue();
						quantity = quantity +(new BigDecimal(obj[4].toString())).intValue();
						objBill.setDblQuantity(qty);
						objBill.setStrGroupName(obj[15].toString());
						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sum = sum + date1.getTime();

						timeForGroupWise = obj[11].toString();
						dateForGroup = timeFormat.parse(timeForGroupWise);
						sumOfGroupWise = dateForGroup.getTime();
						date4 = timeFormat.format(new Date(sumOfGroupWise / qty));
						objBill.setTmeOrderProcessing(date4);

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						String key = obj[15].toString() + "!" + obj[2].toString();

						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);

						i++;
					}
				}
			    
			    String date3 = "";
			    if (i > 0)
			    {
				date3 = timeFormat.format(new Date(sum / qty));
			    }
			    System.out.println(hmKDSFlashData.keySet());

			    List<clsPOSBillDtl> arrListDetail=new ArrayList();
			    for (Map.Entry<String, List<clsPOSBillDtl>> entry : hmKDSFlashData.entrySet())
			    {
					for (int cnt = 0; cnt < entry.getValue().size(); cnt++)
					{
						clsPOSBillDtl objBill = entry.getValue().get(cnt);
						arrListDetail.add(objBill);
					    sumQty = sumQty + objBill.getDblQuantity();
					}
					
			    }
				
			    if(arrListDetail.size()>0)
			    {
			    	listArr=arrListDetail;
			    	hmKDSData.put("avgProTime", date3);
			    	hmKDSData.put("listData", listArr);
			    }
				
		    }
			else if (reportType.equalsIgnoreCase("SubGroup"))
		    {
				long masterProcesTime = 0;
				String maxDelayTime = "", minDelayTime = "";

			    String time = "", processTime = "", targetTime = "";
			    long sum = 0, totProcessTime = 0, sumOfDelayOrders = 0, longMasterTarTime = 0, sumOfDelayOrderTargetTime = 0, transProcessTime = 0;
			    long sumOfTotOrdTarAvg = 0, sumMasterProcessTime = 0;
			    long totDelayOrderTotAvg = 0;
			    int countOfDelayOrder = 0;

			    int noOfItemsCount = 0;
			    Date date1, dateProcessTime, itemTargetTme;
			    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			    SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			    fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

			    long minProcessTime = 0, maxProcessTime = 0;
			    long minDelayedTime = 0, maxDelayedTime = 0;
			    long sumOfMasterTarTime = 0, sumOfMasterProcTimeXMasterTarTime = 0, sumOfMasterProcTimeXTransProcTime = 0, sumOfKOTProcTime = 0;
			    boolean isFirstRecord = true;
				if(strType.equalsIgnoreCase("Detail"))
				{
					sbSql.setLength(0);
					sbSql.append("SELECT a.strBillNo,b.strKOTNo, DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " IFNULL(TIME(b.dteBillDate),'00:00:00') as tmeKOTTime,IFNULL(TIME(b.tmeOrderProcessing),'00:00:00') as OrderProcessingTime,  "
				    + " IFNULL(TIME(b.tmeOrderPickup),'00:00:00') as OrderPickupTime,b.strItemCode, "
				    + " IFNULL(if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),"
				    + " TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', "
				    + " TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) ),'00:00:00')  AS processtimediff,  "
				    + " IFNULL( TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),"
				    + " TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)),'00:00:00') AS pickuptimediff, "
				    + " e.strCostCenterName,f.strWShortName,h.strSubGroupName,"
				    + " IFNULL(time(CONCAT('00',':',c.intProcTimeMin,':','00')),'00:00:00')intProcTimeMin ,"
				    + " IFNULL(time(CONCAT('00',':',c.tmeTargetMiss,':','00')),'00:00:00')tmeTargetMiss "
				    + " FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " ,tblsubgrouphd h"
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " and DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
				    + " and c.strSubGroupCode = h.strSubGroupCode ");
				    if (!costCenterCode.equalsIgnoreCase("All"))
				    {
					sbSql.append("and d.strCostCenterCode='" +costCenterCode + "'  ");
				    }
				    if (!posCode.equalsIgnoreCase("All"))
				    {
					sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
				    }
				    if (!waiterNo.equalsIgnoreCase("All"))
				    {
					sbSql.append(" and a.strWaiterNo='" +waiterNo + "'  ");
				    }

				    sbSql.append(" GROUP BY h.strSubGroupCode,b.strItemCode  ");
				    sbSql.append(" Order By h.strSubGroupCode DESC");
				    List listSql = objBaseService.funGetList(sbSql, "sql");

					if (listSql.size() > 0) 
					{

						for (int i = 0; i < listSql.size(); i++) 
						{
							Object[] obj = (Object[]) listSql.get(i);
							clsPOSBillDtl objBill = new clsPOSBillDtl();

							objBill.setStrBillNo(obj[0].toString());           //Bill No
							objBill.setStrKOTNo(obj[1].toString());            //KOT No
							objBill.setDteBillDate(obj[2].toString());        //Kot Date
							objBill.setStrItemName(obj[3].toString());        //Item Name
							objBill.setDblQuantity(Double.valueOf(obj[4].toString()));        //Qty
							objBill.setDblAmount(Double.valueOf(obj[5].toString()));          //Amount
							objBill.setDblBillAmt(Double.valueOf(obj[6].toString()));         //Sub Total
							objBill.setTmeBillTime(obj[7].toString());
							objBill.setTmeOrderProcessing(obj[8].toString()); //Process Time	
							objBill.setTmeBillSettleTime(obj[9].toString()); //Puckup Time	
							objBill.setStrProcessTimeDiff(obj[11].toString()); //diff kot & process Time	
							objBill.setStrPickUpTimeDiff(obj[12].toString()); //diff process & pickup Time
							objBill.setStrItemProcessTime(obj[16].toString()); //item process time
							objBill.setStrCounterCode(obj[13].toString());     // costCenterName
							objBill.setStrWShortName(obj[14].toString());        // waiterName
							objBill.setStrGroupName(obj[15].toString());
							targetTime = obj[17].toString();
							itemTargetTme = timeFormat.parse(targetTime);
							objBill.setStrItemTargetTime(targetTime);        // item target time
							longMasterTarTime = itemTargetTme.getTime();
							sumOfMasterTarTime = sumOfMasterTarTime + longMasterTarTime;
							sumOfTotOrdTarAvg = sumOfTotOrdTarAvg + itemTargetTme.getTime();
							masterProcesTime = timeFormat.parse(obj[16].toString()).getTime();
							
							sumMasterProcessTime = sumMasterProcessTime + masterProcesTime;
							processTime = obj[11].toString();
							dateProcessTime = timeFormat.parse(processTime);
							transProcessTime = dateProcessTime.getTime();

							if (isFirstRecord)
							{
							    minProcessTime = transProcessTime;
							    maxProcessTime = transProcessTime;

							    minDelayedTime = transProcessTime;
							    maxDelayedTime = transProcessTime;
							}

							if (transProcessTime < minProcessTime)
							{
							    minProcessTime = transProcessTime;
							}
							if (transProcessTime > maxProcessTime)
							{
							    maxProcessTime = transProcessTime;
							}

							if (transProcessTime > longMasterTarTime)
							{
							    long delayTime = (transProcessTime - longMasterTarTime);

							    if (delayTime < minDelayedTime)
							    {
								minDelayedTime = delayTime;
							    }

							    if (delayTime > maxDelayedTime)
							    {
								maxDelayedTime = delayTime;
							    }

							    sumOfDelayOrders = sumOfDelayOrders + transProcessTime;
							    sumOfDelayOrderTargetTime = sumOfDelayOrderTargetTime + longMasterTarTime;
							    countOfDelayOrder++;
							}

							noOfItemsCount = noOfItemsCount + 1;
							time = obj[11].toString();
							date1 = timeFormat.parse(time);
							sumOfKOTProcTime = sumOfKOTProcTime + date1.getTime();

							String key = obj[15].toString() + "!" + obj[2].toString();

							List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
							if (hmKDSFlashData.containsKey(key))
							{
							    arrListBillDtl = hmKDSFlashData.get(key);
							    arrListBillDtl.add(objBill);
							}
							else
							{
							    arrListBillDtl.add(objBill);
							}
							hmKDSFlashData.put(key, arrListBillDtl);

							sumOfMasterProcTimeXMasterTarTime = sumOfMasterProcTimeXMasterTarTime + (masterProcesTime * longMasterTarTime);
							sumOfMasterProcTimeXTransProcTime = sumOfMasterProcTimeXTransProcTime + (masterProcesTime * transProcessTime);

							isFirstRecord = false;
						}

					}
					
					sbSql.setLength(0);
				    sbSql.append("SELECT a.strBillNo,b.strKOTNo,DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
					    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
					    + " IFNULL(TIME(b.dteBillDate),'00:00:00') as tmeKOTTime,IFNULL(TIME(b.tmeOrderProcessing),'00:00:00') as OrderProcessingTime,  "
					    + " IFNULL(TIME(b.tmeOrderPickup),'00:00:00') as OrderPickupTime,b.strItemCode, "
					    + " IFNULL(if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),"
					    + " TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', "
					    + " TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) ),'00:00:00')  AS processtimediff,  "
					    + " IFNULL( TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),"
					    + " TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)),'00:00:00') AS pickuptimediff, "
					    + " e.strCostCenterName,f.strWShortName,h.strSubGroupName,"
					    + " IFNULL(time(CONCAT('00',':',c.intProcTimeMin,':','00')),'00:00:00')intProcTimeMin ,"
					    + " IFNULL(time(CONCAT('00',':',c.tmeTargetMiss,':','00')),'00:00:00')tmeTargetMiss "
					    + " FROM tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
					    + " ,tblsubgrouphd h"
					    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
					    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
					    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
					    + " AND DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					    + " and c.strSubGroupCode = h.strSubGroupCode ");
				    if (!costCenterCode.equalsIgnoreCase("All"))
				    {
					sbSql.append("and d.strCostCenterCode='" +costCenterCode + "'  ");
				    }
				    if (!posCode.equalsIgnoreCase("All"))
				    {
					sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
				    }
				    if (!waiterNo.equalsIgnoreCase("All"))
				    {
					sbSql.append(" and a.strWaiterNo='" + waiterNo + "'  ");
				    }
				    sbSql.append(" GROUP BY h.strSubGroupCode,b.strItemCode ");
				    sbSql.append(" Order By h.strSubGroupCode DESC");
				    
				    listSql = objBaseService.funGetList(sbSql, "sql");

					if (listSql.size() > 0) 
					{

						for (int i = 0; i < listSql.size(); i++) 
						{
							Object[] obj = (Object[]) listSql.get(i);
							clsPOSBillDtl objBill = new clsPOSBillDtl();
							objBill.setStrBillNo(obj[0].toString());           //Bill No
							objBill.setStrKOTNo(obj[1].toString());            //KOT No
							objBill.setDteBillDate(obj[2].toString());        //Kot Date
							objBill.setStrItemName(obj[3].toString());        //Item Name
							objBill.setDblQuantity(Double.valueOf(obj[4].toString()));        //Qty
							objBill.setDblAmount(Double.valueOf(obj[5].toString()));          //Amount
							objBill.setDblBillAmt(Double.valueOf(obj[6].toString()));         //Sub Total
							objBill.setTmeBillTime(obj[7].toString());        //Kot Time
							objBill.setTmeOrderProcessing(obj[8].toString()); //Process Time	
							objBill.setTmeBillSettleTime(obj[9].toString()); //Puckup Time	
							objBill.setStrProcessTimeDiff(obj[11].toString()); //diff kot & process Time
							objBill.setStrPickUpTimeDiff(obj[12].toString()); //diff process & pickup Time
							objBill.setStrItemProcessTime(obj[16].toString()); //item process time
							//System.out.println(rsQSales.getString(1)+"   "+rsQSales.getString(2));
							objBill.setStrPickUpTimeDiff(obj[12].toString());  // diff process & pickup Time
							objBill.setStrCounterCode(obj[13].toString());     // costCenterName
							objBill.setStrWShortName(obj[14].toString());        // waiterName
							//objBill.setStrItemProcessTime(obj[16].toString());        // item process time
							targetTime =obj[17].toString();
							itemTargetTme = timeFormat.parse(targetTime);
							objBill.setStrItemTargetTime(targetTime);        // item target time
							longMasterTarTime = itemTargetTme.getTime();
							sumOfMasterTarTime = sumOfMasterTarTime + longMasterTarTime;
							sumOfTotOrdTarAvg = sumOfTotOrdTarAvg + itemTargetTme.getTime();

							masterProcesTime = timeFormat.parse(obj[16].toString()).getTime();
							sumMasterProcessTime = sumMasterProcessTime + masterProcesTime;
							processTime = obj[11].toString();
							dateProcessTime = timeFormat.parse(processTime);
							transProcessTime = dateProcessTime.getTime();

							if (isFirstRecord)
							{
							    minProcessTime = transProcessTime;
							    maxProcessTime = transProcessTime;

							    minDelayedTime = transProcessTime;
							    maxDelayedTime = transProcessTime;
							}

							if (transProcessTime < minProcessTime)
							{
							    minProcessTime = transProcessTime;
							}
							if (transProcessTime > maxProcessTime)
							{
							    maxProcessTime = transProcessTime;
							}

							if (transProcessTime > longMasterTarTime)
							{
							    long delayTime = (transProcessTime - longMasterTarTime);

							    if (delayTime < minDelayedTime)
							    {
								minDelayedTime = delayTime;
							    }

							    if (delayTime > maxDelayedTime)
							    {
								maxDelayedTime = delayTime;
							    }

							    sumOfDelayOrders = sumOfDelayOrders + transProcessTime;
							    sumOfDelayOrderTargetTime = sumOfDelayOrderTargetTime + longMasterTarTime;
							    countOfDelayOrder++;
							}

							noOfItemsCount = noOfItemsCount + 1;
							time = obj[11].toString();
							date1 = timeFormat.parse(time);
							sumOfKOTProcTime = sumOfKOTProcTime + date1.getTime();

							String key = obj[15].toString() + "!" + obj[2].toString();

							List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
							if (hmKDSFlashData.containsKey(key))
							{
							    arrListBillDtl = hmKDSFlashData.get(key);
							    arrListBillDtl.add(objBill);
							}
							else
							{
							    arrListBillDtl.add(objBill);
							}
							hmKDSFlashData.put(key, arrListBillDtl);

							sumOfMasterProcTimeXMasterTarTime = sumOfMasterProcTimeXMasterTarTime + (masterProcesTime * longMasterTarTime);
							sumOfMasterProcTimeXTransProcTime = sumOfMasterProcTimeXTransProcTime + (masterProcesTime * transProcessTime);

							isFirstRecord = false;
						}
					}
					
					
					List<clsPOSBillDtl> arrListDetail=new ArrayList();
				    
					String avgProTime = "", quantity = "", DOAvg = "", totOrdTarAvg = "", delayOrderTargAvg = "";
				    String totDelayOrderTotAvgPer = "", weightedAvgTargetTime = "", weightedAvgActualTime = "";
				    long first = 0, second = 0, longWeightedAvgTargetTime = 0, longWeightedAvgActualTime = 0, longAvgMasterTarTime;
				    double finalDelayedOrder = 0;
				    double totOrderePer = 0;
				    long finalDota = 0;
				    if (!isFirstRecord)
				    {
					finalDelayedOrder = countOfDelayOrder;

					longWeightedAvgTargetTime = sumOfMasterProcTimeXMasterTarTime / sumMasterProcessTime;
					longWeightedAvgActualTime = sumOfMasterProcTimeXTransProcTime / sumMasterProcessTime;
					longAvgMasterTarTime = sumOfMasterTarTime / noOfItemsCount;

					weightedAvgTargetTime = fmt.format(new Date(longWeightedAvgTargetTime));
					weightedAvgActualTime = fmt.format(new Date(longWeightedAvgActualTime));
					first = (sumMasterProcessTime * sumOfMasterTarTime) / sumMasterProcessTime;
					avgProTime = fmt.format(new Date(sumOfKOTProcTime / noOfItemsCount));
					if (noOfItemsCount != 0)
					{
					    avgProTime = fmt.format(new Date(sumOfKOTProcTime / noOfItemsCount));
					    totOrderePer = ((finalDelayedOrder / noOfItemsCount) * 100);
					    totOrdTarAvg = fmt.format(new Date(longAvgMasterTarTime));
					    second = sumOfMasterTarTime / noOfItemsCount;
					}
					else
					{
					    avgProTime = fmt.format(new Date(sumOfKOTProcTime));
					    totOrderePer = ((finalDelayedOrder) * 100);
					    totOrdTarAvg = fmt.format(new Date(sumOfMasterTarTime));
					    second = sumOfMasterTarTime;
					}

					if (countOfDelayOrder != 0)
					{
					    DOAvg = fmt.format(new Date(sumOfDelayOrderTargetTime / countOfDelayOrder));
					    delayOrderTargAvg = fmt.format(new Date(sumOfDelayOrderTargetTime / countOfDelayOrder));
					}
					else
					{
					    DOAvg = fmt.format(new Date(sumOfDelayOrderTargetTime));
					    delayOrderTargAvg = fmt.format(new Date(sumOfDelayOrderTargetTime));

					}

					double finalPer = (double) (longAvgMasterTarTime / (double) longWeightedAvgTargetTime) * 100;

					totDelayOrderTotAvgPer = String.valueOf(decimalFormtFor2DecPoint.format(finalPer));

				    }

				    System.out.println(hmKDSFlashData.keySet());

				    for (Map.Entry<String, List<clsPOSBillDtl>> entry : hmKDSFlashData.entrySet())
				    {
				    	clsPOSBillDtl objBill =new clsPOSBillDtl();
				    	objBill.setStrSubGroupName(entry.getKey().split("!")[0]);
						objBill.setStrSubGroupCode(entry.getKey().split("!")[1]);
						arrListDetail.add(objBill);
						for (int cnt = 0; cnt < entry.getValue().size(); cnt++)
						{
							objBill = entry.getValue().get(cnt);
						    arrListDetail.add(objBill);
						}
				     }
				    
				    if(arrListDetail.size()>0)
				    {
				    	listArr=arrListDetail;
				    	hmKDSData.put("listData", listArr);
				    	hmKDSData.put("avgProTime", avgProTime);
				    	hmKDSData.put("finalDelayedOrder", finalDelayedOrder);
				    	hmKDSData.put("noOfItemsCount", String.valueOf(noOfItemsCount));
				    	hmKDSData.put("totOrderePer", String.valueOf(Math.round(totOrderePer) + " %"));
				    	hmKDSData.put("totOrdTarAvg", totOrdTarAvg);
				    	hmKDSData.put("delayOrderTargAvg", delayOrderTargAvg);
				    	hmKDSData.put("totDelayOrderTotAvgPer", String.valueOf(totDelayOrderTotAvgPer) + " %");
				    	hmKDSData.put("weightedAvgActualTime", weightedAvgActualTime);
				    	hmKDSData.put("weightedAvgTargetTime", weightedAvgTargetTime);
				    	hmKDSData.put("minProcessTime", String.valueOf(fmt.format(new Date(minProcessTime))));
				    	hmKDSData.put("maxProcessTime", String.valueOf(fmt.format(new Date(maxProcessTime))));
				    	hmKDSData.put("minDelayedTime", String.valueOf(fmt.format(new Date(minDelayedTime))));
				    	hmKDSData.put("maxDelayedTime", String.valueOf(fmt.format(new Date(maxDelayedTime))));
				    }
				}
				else
				{
					
				}
				
		    }
			
			else if (reportType.equalsIgnoreCase("Menu Head"))
		    {
				List<clsPOSBillDtl> arrListDetail=new ArrayList();
				
				String time = "", timeForGroupWise = "";
			    long sum = 0, sumOfGroupWise = 0;
			    int i = 0;
			    String date4 = "";
			    int qty = 0, quantity = 0;
			    Date date1;
			    Date dateForGroup;
			    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				sbSql.setLength(0);
				sbSql.append("SELECT a.strBillNo,b.strKOTNo, DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " TIME(b.dteBillDate) as tmeKOTTime,TIME(b.tmeOrderProcessing) as OrderProcessingTime, "
				    + " TIME(b.tmeOrderPickup) as OrderPickupTime,b.strItemCode, "
				    + " if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) )  AS processtimediff, "
				    + " TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)) AS pickuptimediff,"
				    + " e.strCostCenterName,f.strWShortName,g.strMenuName "
				    + " FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " ,tblmenuhd g"
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " and DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
				    + " and d.strMenuCode = g.strMenuCode ");
			    if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append("and d.strCostCenterCode='" + costCenterCode + "'  ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" + waiterNo+ "'  ");
			    }
	
			    sbSql.append(" GROUP BY g.strMenuCode  ");
			    sbSql.append(" Order By g.strMenuCode DESC");
				List listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) 
				{
					for (int cnt = 0; cnt < listSql.size(); cnt++) 
					{
						Object[] obj = (Object[]) listSql.get(cnt);
						clsPOSBillDtl objBill = new clsPOSBillDtl();
						qty = qty +(new BigDecimal(obj[4].toString())).intValue();
						quantity = (new BigDecimal(obj[4].toString())).intValue();
						objBill.setDblQuantity(quantity);
						objBill.setStrGroupName(obj[15].toString());
						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sum = sum + date1.getTime();

						timeForGroupWise = obj[11].toString();
						dateForGroup = timeFormat.parse(timeForGroupWise);
						sumOfGroupWise = dateForGroup.getTime();

						date4 = timeFormat.format(new Date(sumOfGroupWise / quantity));
						objBill.setTmeOrderProcessing(date4);
						String key = obj[15].toString() + "!" + obj[2].toString();

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);
						i++;

					}

				}
				
				sbSql.setLength(0);
			    sbSql.append("SELECT a.strBillNo,b.strKOTNo,DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " TIME(b.dteBillDate) as tmeKOTTime,TIME(b.tmeOrderProcessing) as OrderProcessingTime, "
				    + " TIME(b.tmeOrderPickup) as OrderPickupTime,b.strItemCode, "
				    + " if(TIME(b.tmeOrderProcessing)<TIME(b.dteBillDate),ADDTIME(TIME(b.tmeOrderProcessing),TIMEDIFF('24:00:00',TIME(b.dteBillDate))),TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)) )  AS processtimediff, "
				    + " TIMEDIFF(if(TIME(b.tmeOrderPickup)='00:00:00',TIME(b.tmeOrderProcessing),TIME(b.tmeOrderPickup)),TIME(b.tmeOrderProcessing)) AS pickuptimediff,"
				    + " e.strCostCenterName,f.strWShortName,g.strMenuName "
				    + " FROM tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " ,tblmenuhd g"
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " AND DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
				    + " and d.strMenuCode = g.strMenuCode ");
			    if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append("and d.strCostCenterCode='" + costCenterCode + "'  ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" + waiterNo+ "'  ");
			    }
			    sbSql.append(" GROUP BY g.strMenuCode ");
			    sbSql.append(" Order By g.strMenuCode DESC");
			    listSql = objBaseService.funGetList(sbSql, "sql");

				if (listSql.size() > 0) 
				{
					for (int cnt = 0; cnt < listSql.size(); cnt++) 
					{
						Object[] obj = (Object[]) listSql.get(cnt);
						clsPOSBillDtl objBill = new clsPOSBillDtl();
						qty = qty +(new BigDecimal(obj[4].toString())).intValue();
						quantity = (new BigDecimal(obj[4].toString())).intValue();
						
						objBill.setDblQuantity(quantity);
						objBill.setStrGroupName(obj[15].toString());
						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sum = sum + date1.getTime();

						timeForGroupWise = obj[11].toString();
						dateForGroup = timeFormat.parse(timeForGroupWise);
						sumOfGroupWise = dateForGroup.getTime();
						date4 = timeFormat.format(new Date(sumOfGroupWise / quantity));
						objBill.setTmeOrderProcessing(date4);

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						String key = obj[15].toString() + "!" +obj[2].toString();

						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);
						i++;
					}
				}
				
				String date3 = "";
			    if (i > 0)
			    {
				date3 = timeFormat.format(new Date(sum / qty));
			    }
			    System.out.println(hmKDSFlashData.keySet());
			    for (Map.Entry<String, List<clsPOSBillDtl>> entry : hmKDSFlashData.entrySet())
			    {
					for (int cnt = 0; cnt < entry.getValue().size(); cnt++)
					{
						clsPOSBillDtl objBill = entry.getValue().get(cnt);
						arrListDetail.add(objBill);
					    sumQty = sumQty + objBill.getDblQuantity();
					}
					
			    }
				
			    if(arrListDetail.size()>0)
			    {
			    	listArr=arrListDetail;
			    	hmKDSData.put("listData", listArr);
			    	hmKDSData.put("avgProTime", date3);
			    }
		    }
			
			else 
		    {
				long minProcessTime = 0, maxProcessTime = 0;
			    long minDelayedTime = 0, maxDelayedTime = 0;
			    boolean isFirstRecord = true;
			    String maxDelayTime = "", minDelayTime = "";
			    
			    sbSql.setLength(0);
			    sbSql.append("SELECT a.strBillNo,b.strKOTNo, DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " ifnull(TIME(b.dteBillDate),'00:00:00') as tmeKOTTime,ifnull(TIME(b.tmeOrderProcessing),'00:00:00') as OrderProcessingTime, "
				    + " ifnull(TIME(b.tmeOrderPickup),'00:00:00') as OrderPickupTime,b.strItemCode  ,ifnull(if(TIME(b.tmeOrderProcessing)=time('00:00:00'),time('00:00:00'),"
				    + " IF(TIME(b.tmeOrderProcessing)< TIME(b.dteBillDate), ADDTIME(TIME(b.tmeOrderProcessing), TIMEDIFF('24:00:00', TIME(b.dteBillDate))), TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)))),'00:00:00') AS processtimediff  ,"
				    + " ifnull(if(TIME(b.tmeOrderPickup)=time('00:00:00'),time('00:00:00'),IF(TIME(b.tmeOrderPickup)< TIME(b.tmeOrderProcessing), ADDTIME(TIME(b.tmeOrderPickup), TIMEDIFF('24:00:00', TIME(b.tmeOrderProcessing))), TIMEDIFF(IF(TIME(b.tmeOrderPickup)='00:00:00', TIME(b.tmeOrderProcessing), TIME(b.tmeOrderPickup)), TIME(b.tmeOrderProcessing)))),'00:00:00') AS pickuptimediff  ,"
				    + " e.strCostCenterName,f.strWShortName ,"
				    + " ifnull(time(CONCAT('00',':',c.intProcTimeMin,':','00')),'00:00:00')intProcTimeMin ,ifnull(time(CONCAT('00',':',c.tmeTargetMiss,':','00')),'00:00:00')tmeTargetMiss"
				    + " FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " and DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'  ");
			    if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append("and d.strCostCenterCode='" + costCenterCode + "'  ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" + waiterNo+ "'  ");
			    }

			    sbSql.append("GROUP BY a.strBillNo,b.strItemCode ");
			    sbSql.append(" Order By a.strBillNo desc");
				List listSql = objBaseService.funGetList(sbSql, "sql");
				
				String time = "", processTime = "", targetTime = "";
			    long masterProcesTime = 0;
			    long sumOfKOTProcTime = 0, transProcessTime = 0, sumOfDelayOrders = 0, masterTargetTime = 0, sumOfDelayOrderTargetTime = 0;
			    long sumOfMasterTarTime = 0, sumMasterProcessTime = 0, sumOfMasterProcTimeXMasterTarTime = 0, sumOfMasterProcTimeXTransProcTime = 0;
			    long totDelayOrderTotAvg = 0;
			    int countOfDelayOrder = 0;
			    long noOfItemsCount = 0;
			    Date date1, dateProcessTime, itemTargetTme;
			    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			    SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			    fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

				if (listSql.size() > 0) 
				{
					for (int i = 0; i < listSql.size(); i++) 
					{
						Object[] obj = (Object[]) listSql.get(i);
						clsPOSBillDtl objBill = new clsPOSBillDtl();
						objBill.setStrBillNo(obj[0].toString());           //Bill No
						objBill.setStrKOTNo(obj[1].toString());            //KOT No
						objBill.setDteBillDate(obj[2].toString());        //Kot Date
						objBill.setStrItemName(obj[3].toString());        //Item Name
						objBill.setDblQuantity(Double.valueOf(obj[4].toString()));        //Qty
						objBill.setDblAmount(Double.valueOf(obj[5].toString()));          //Amount
						objBill.setDblBillAmt(Double.valueOf(obj[6].toString()));         //Sub Total
						objBill.setTmeBillTime(obj[7].toString());        //Kot Time
						objBill.setTmeOrderProcessing(obj[8].toString()); //Process Time
						objBill.setTmeBillSettleTime(obj[9].toString());  //Puckup Time
						objBill.setStrProcessTimeDiff(obj[11].toString());  // diff kot & process Time
						objBill.setStrPickUpTimeDiff(obj[12].toString());  // diff process & pickup Time
						objBill.setStrCounterCode(obj[13].toString());     // costCenterName
						objBill.setStrWShortName(obj[14].toString());        // waiterName
						objBill.setStrItemProcessTime(obj[15].toString());        // item process time
						targetTime = obj[16].toString();
						itemTargetTme = timeFormat.parse(targetTime);
						objBill.setStrItemTargetTime(targetTime);        // item target time
						masterTargetTime = itemTargetTme.getTime();
						sumOfMasterTarTime = sumOfMasterTarTime + itemTargetTme.getTime();

						masterProcesTime = timeFormat.parse(obj[15].toString()).getTime();
						sumMasterProcessTime = sumMasterProcessTime + masterProcesTime;

						processTime = obj[11].toString();
						dateProcessTime = timeFormat.parse(processTime);
						transProcessTime = dateProcessTime.getTime();

						if (isFirstRecord)
						{
						    minProcessTime = transProcessTime;
						    maxProcessTime = transProcessTime;

						    minDelayedTime = transProcessTime;
						    maxDelayedTime = transProcessTime;
						}

						if (transProcessTime < minProcessTime)
						{
						    minProcessTime = transProcessTime;
						}
						if (transProcessTime > maxProcessTime)
						{
						    maxProcessTime = transProcessTime;
						}

						if (transProcessTime > masterTargetTime)
						{
						    long delayTime = (transProcessTime - masterTargetTime);

						    if (delayTime < minDelayedTime)
						    {
							minDelayedTime = delayTime;
						    }

						    if (delayTime > maxDelayedTime)
						    {
							maxDelayedTime = delayTime;
						    }

						    sumOfDelayOrders = sumOfDelayOrders + transProcessTime;
						    sumOfDelayOrderTargetTime = sumOfDelayOrderTargetTime + masterTargetTime;
						    countOfDelayOrder++;
						}

						noOfItemsCount = noOfItemsCount + 1;
						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sumOfKOTProcTime = sumOfKOTProcTime + date1.getTime();

						String key = obj[13].toString() + "!" + obj[2].toString();

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);

						sumOfMasterProcTimeXMasterTarTime = sumOfMasterProcTimeXMasterTarTime + (masterProcesTime * masterTargetTime);
						sumOfMasterProcTimeXTransProcTime = sumOfMasterProcTimeXTransProcTime + (masterProcesTime * transProcessTime);

						isFirstRecord = false;
					}

				}
				sbSql.setLength(0);
			    sbSql.append("SELECT a.strBillNo,b.strKOTNo,DATE_FORMAT(DATE(b.dteBillDate),'%d-%m-%Y') dteKOTDate,"
				    + " b.strItemName, SUM(b.dblQuantity),SUM(b.dblAmount) AS Amount,sum(b.dblAmount)-sum(b.dblDiscountAmt) AS SubTotal, "
				    + " ifnull(TIME(b.dteBillDate),'00:00:00') as tmeKOTTime,ifnull(TIME(b.tmeOrderProcessing),'00:00:00') as OrderProcessingTime, "
				    + " ifnull(TIME(b.tmeOrderPickup),'00:00:00') as OrderPickupTime,b.strItemCode  ,ifnull(if(TIME(b.tmeOrderProcessing)=time('00:00:00'),time('00:00:00'),"
				    + " IF(TIME(b.tmeOrderProcessing)< TIME(b.dteBillDate), ADDTIME(TIME(b.tmeOrderProcessing), TIMEDIFF('24:00:00', TIME(b.dteBillDate))), TIMEDIFF(IF(TIME(b.tmeOrderProcessing)='00:00:00', TIME(b.dteBillDate), TIME(b.tmeOrderProcessing)), TIME(b.dteBillDate)))),'00:00:00') AS processtimediff  ,"
				    + " ifnull(if(TIME(b.tmeOrderPickup)=time('00:00:00'),time('00:00:00'),IF(TIME(b.tmeOrderPickup)< TIME(b.tmeOrderProcessing), ADDTIME(TIME(b.tmeOrderPickup), TIMEDIFF('24:00:00', TIME(b.tmeOrderProcessing))), TIMEDIFF(IF(TIME(b.tmeOrderPickup)='00:00:00', TIME(b.tmeOrderProcessing), TIME(b.tmeOrderPickup)), TIME(b.tmeOrderProcessing)))),'00:00:00') AS pickuptimediff  ,"
				    + " e.strCostCenterName,f.strWShortName ,"
				    + " ifnull(time(CONCAT('00',':',c.intProcTimeMin,':','00')),'00:00:00')intProcTimeMin ,ifnull(time(CONCAT('00',':',c.tmeTargetMiss,':','00')),'00:00:00')tmeTargetMiss"
				    + " FROM tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
				    + " WHERE a.strBillNo=b.strBillNo AND DATE(a.dtBillDate)= DATE(b.dtBillDate) "
				    + " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode "
				    + " and (a.strPOSCode=d.strPosCode or d.strPosCode='All') and d.strCostCenterCode=e.strCostCenterCode and a.strWaiterNo=f.strWaiterNo "
				    + " AND DATE(a.dtBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			    if (!costCenterCode.equalsIgnoreCase("All"))
			    {
				sbSql.append("and d.strCostCenterCode='" + costCenterCode+ "'  ");
			    }
			    if (!posCode.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strPOSCode='" + posCode + "'  ");
			    }
			    if (!waiterNo.equalsIgnoreCase("All"))
			    {
				sbSql.append(" and a.strWaiterNo='" +waiterNo + "'  ");
			    }
			    sbSql.append("GROUP BY a.strBillNo,b.strItemCode ");
			    sbSql.append(" Order By a.strBillNo desc");
				listSql = objBaseService.funGetList(sbSql, "sql");
				if (listSql.size() > 0) 
				{
					for (int i = 0; i < listSql.size(); i++) 
					{
						Object[] obj = (Object[]) listSql.get(i);
						clsPOSBillDtl objBill = new clsPOSBillDtl();
						objBill.setStrBillNo(obj[0].toString());           //Bill No
						objBill.setStrKOTNo(obj[1].toString());            //KOT No
						objBill.setDteBillDate(obj[2].toString());        //Kot Date
						objBill.setStrItemName(obj[3].toString());        //Item Name
						objBill.setDblQuantity(Double.valueOf(obj[4].toString()));        //Qty
						objBill.setDblAmount(Double.valueOf(obj[5].toString()));          //Amount
						objBill.setDblBillAmt(Double.valueOf(obj[6].toString()));         //Sub Total
						objBill.setTmeBillTime(obj[7].toString());        //Kot Time
						objBill.setTmeOrderProcessing(obj[8].toString()); //Process Time
						objBill.setTmeBillSettleTime(obj[9].toString()); //Puckup Time                
						objBill.setStrProcessTimeDiff(obj[10].toString());  // diff kot & process Time

						//System.out.println(rsQSales.getString(1)+"   "+rsQSales.getString(2));
						objBill.setStrPickUpTimeDiff(obj[12].toString());  // diff process & pickup Time
						objBill.setStrCounterCode(obj[13].toString());     // costCenterName
						objBill.setStrWShortName(obj[14].toString());        // waiterName
						objBill.setStrItemProcessTime(obj[15].toString());        // item process time
						targetTime = obj[16].toString();
						itemTargetTme = timeFormat.parse(targetTime);
						objBill.setStrItemTargetTime(targetTime);        // item target time
						masterTargetTime = itemTargetTme.getTime();
						sumOfMasterTarTime = sumOfMasterTarTime + itemTargetTme.getTime();
						processTime =obj[11].toString();
						dateProcessTime = timeFormat.parse(processTime);
						transProcessTime = dateProcessTime.getTime();

						masterProcesTime = timeFormat.parse(obj[15].toString()).getTime();
						sumMasterProcessTime = sumMasterProcessTime + masterProcesTime;
						if (isFirstRecord)
						{
						    minProcessTime = transProcessTime;
						    maxProcessTime = transProcessTime;

						    minDelayedTime = transProcessTime;
						    maxDelayedTime = transProcessTime;
						}

						if (transProcessTime < minProcessTime)
						{
						    minProcessTime = transProcessTime;
						}
						if (transProcessTime > maxProcessTime)
						{
						    maxProcessTime = transProcessTime;
						}

						if (transProcessTime > masterTargetTime)
						{
						    long delayTime = (transProcessTime - masterTargetTime);

						    if (delayTime < minDelayedTime)
						    {
							minDelayedTime = delayTime;
						    }

						    if (delayTime > maxDelayedTime)
						    {
							maxDelayedTime = delayTime;
						    }

						    sumOfDelayOrders = sumOfDelayOrders + transProcessTime;
						    sumOfDelayOrderTargetTime = sumOfDelayOrderTargetTime + masterTargetTime;
						    countOfDelayOrder++;
						}

						noOfItemsCount = noOfItemsCount + 1;

						time = obj[11].toString();
						date1 = timeFormat.parse(time);
						sumOfKOTProcTime = sumOfKOTProcTime + date1.getTime();

						List<clsPOSBillDtl> arrListBillDtl = new ArrayList<clsPOSBillDtl>();
						//String key=rsQSales.getString(3)+"!"+rsQSales.getString(14);
						String key = obj[13].toString() + "!" + obj[2].toString();

						if (hmKDSFlashData.containsKey(key))
						{
						    arrListBillDtl = hmKDSFlashData.get(key);
						    arrListBillDtl.add(objBill);
						}
						else
						{
						    arrListBillDtl.add(objBill);
						}
						hmKDSFlashData.put(key, arrListBillDtl);

						sumOfMasterProcTimeXMasterTarTime = sumOfMasterProcTimeXMasterTarTime + (masterProcesTime * masterTargetTime);
						sumOfMasterProcTimeXTransProcTime = sumOfMasterProcTimeXTransProcTime + (masterProcesTime * transProcessTime);

						isFirstRecord = false;
					}

				}
				
				String avgProTime = "", quantity = "", DOAvg = "", totOrdTarAvg = "", delayOrderTargAvg = "";
			    String totDelayOrderTotAvgPer = "", weightedAvgTargetTime = "", weightedAvgActualTime = "";
			    long first = 0, second = 0, longWeightedAvgTargetTime = 0, longWeightedAvgActualTime = 0, longAvgMasterTarTime;
			    double finalDelayedOrder = 0;
			    double totOrderePer = 0;
			    long finalDota = 0;
			    if (!isFirstRecord)
			    {
				finalDelayedOrder = countOfDelayOrder;

				longWeightedAvgTargetTime = sumOfMasterProcTimeXMasterTarTime / sumMasterProcessTime;
				longWeightedAvgActualTime = sumOfMasterProcTimeXTransProcTime / sumMasterProcessTime;
				longAvgMasterTarTime = sumOfMasterTarTime / noOfItemsCount;

				weightedAvgTargetTime = fmt.format(new Date(longWeightedAvgTargetTime));
				weightedAvgActualTime = fmt.format(new Date(longWeightedAvgActualTime));
				first = (sumMasterProcessTime * sumOfMasterTarTime) / sumMasterProcessTime;
				avgProTime = fmt.format(new Date(sumOfKOTProcTime / noOfItemsCount));
				if (noOfItemsCount != 0)
				{
				    avgProTime = fmt.format(new Date(sumOfKOTProcTime / noOfItemsCount));
				    totOrderePer = ((finalDelayedOrder / noOfItemsCount) * 100);
				    totOrdTarAvg = fmt.format(new Date(longAvgMasterTarTime));
				    second = sumOfMasterTarTime / noOfItemsCount;
				}
				else
				{
				    avgProTime = fmt.format(new Date(sumOfKOTProcTime));
				    totOrderePer = ((finalDelayedOrder) * 100);
				    totOrdTarAvg = fmt.format(new Date(sumOfMasterTarTime));
				    second = sumOfMasterTarTime;
				}

				if (countOfDelayOrder != 0)
				{
				    DOAvg = fmt.format(new Date(sumOfDelayOrderTargetTime / countOfDelayOrder));
				    delayOrderTargAvg = fmt.format(new Date(sumOfDelayOrderTargetTime / countOfDelayOrder));
				}
				else
				{
				    DOAvg = fmt.format(new Date(sumOfDelayOrderTargetTime));
				    delayOrderTargAvg = fmt.format(new Date(sumOfDelayOrderTargetTime));

				}

				double finalPer = (double) (longAvgMasterTarTime / (double) longWeightedAvgTargetTime) * 100;

				totDelayOrderTotAvgPer = String.valueOf(decimalFormtFor2DecPoint.format(finalPer));

			    }
				
				
				System.out.println(hmKDSFlashData.keySet());
				List<clsPOSBillDtl> arrListDetail=new ArrayList();
			    for (Map.Entry<String, List<clsPOSBillDtl>> entry : hmKDSFlashData.entrySet())
			    {
			    	clsPOSBillDtl objBill =new clsPOSBillDtl();
			    	objBill.setStrSubGroupName(entry.getKey().split("!")[0]);
					objBill.setStrSubGroupCode(entry.getKey().split("!")[1]);
					arrListDetail.add(objBill);
					for (int cnt = 0; cnt < entry.getValue().size(); cnt++)
					{
						objBill = entry.getValue().get(cnt);
					    arrListDetail.add(objBill);
					}
			     }
			    
			    
			    if(arrListDetail.size()>0)
			    {
			    	listArr=arrListDetail;
			    	hmKDSData.put("listData", listArr);
			    	hmKDSData.put("avgProTime", avgProTime);
			    	hmKDSData.put("finalDelayedOrder", finalDelayedOrder);
			    	hmKDSData.put("noOfItemsCount", String.valueOf(noOfItemsCount));
			    	hmKDSData.put("totOrderePer", String.valueOf(Math.round(totOrderePer) + " %"));
			    	hmKDSData.put("totOrdTarAvg", totOrdTarAvg);
			    	hmKDSData.put("delayOrderTargAvg", delayOrderTargAvg);
			    	hmKDSData.put("totDelayOrderTotAvgPer", String.valueOf(totDelayOrderTotAvgPer) + " %");
			    	hmKDSData.put("weightedAvgActualTime", weightedAvgActualTime);
			    	hmKDSData.put("weightedAvgTargetTime", weightedAvgTargetTime);
			    	hmKDSData.put("minProcessTime", String.valueOf(fmt.format(new Date(minProcessTime))));
			    	hmKDSData.put("maxProcessTime", String.valueOf(fmt.format(new Date(maxProcessTime))));
			    	hmKDSData.put("minDelayedTime", String.valueOf(fmt.format(new Date(minDelayedTime))));
			    	hmKDSData.put("maxDelayedTime", String.valueOf(fmt.format(new Date(maxDelayedTime))));
			    	
				   
			    }
		    }		

		} // end of try
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return hmKDSData;
	}

	
	
	public List funProcessTableWisePaxReport(String posCode, String fromDate, String toDate, String shiftNo,
			String queryType) {

		StringBuilder sqlBuilder = new StringBuilder();
		List listRet = new ArrayList();
		
			
		try {
			// live
			if (queryType.equalsIgnoreCase("liveData")) {
				sqlBuilder.setLength(0);
				sqlBuilder.append("select b.strTableNo,b.strTableName,sum(a.intBillSeriesPaxNo) "
                    + " from tblbillhd a,tbltablemaster b "
                    + " where a.strTableNo=b.strTableNo "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.strClientCode=b.strClientCode");
			
				if (!posCode.equalsIgnoreCase("All")) 
				{
					sqlBuilder.append(" and a.strPOSCode = '" + posCode + "' ");
				}
				if (!shiftNo.equalsIgnoreCase("All")) 
				{
					sqlBuilder.append(" and a.intShiftCode = '" + shiftNo + "'  ");
				}
				sqlBuilder.append("group by b.strTableNo,a.strPOSCode");
				listRet = objBaseService.funGetList(sqlBuilder, "sql");
			} 
			else if (queryType.equalsIgnoreCase("qData")) 
			{
				// QFile
				sqlBuilder.setLength(0);
				sqlBuilder.append("select b.strTableNo,b.strTableName,sum(a.intBillSeriesPaxNo) "
                    + " from tblqbillhd a,tbltablemaster b "
                    + " where a.strTableNo=b.strTableNo "
                    + " and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and a.strClientCode=b.strClientCode ");
				
				if (!posCode.equalsIgnoreCase("All")) 
				{
					sqlBuilder.append(" and a.strPOSCode = '" + posCode + "' ");
				}
				if (!shiftNo.equalsIgnoreCase("All")) 
				{
					sqlBuilder.append(" and a.intShiftCode = '" + shiftNo + "'  ");
				}
				sqlBuilder.append("group by b.strTableNo,a.strPOSCode");
				listRet = objBaseService.funGetList(sqlBuilder, "sql");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listRet;
	}
	
	
	
	
	
	

	public List<clsPOSBillDtl> funProcessWaiterWiseItemWiseIncentivesSummaryReport(String posCode, String fromDate,
			String toDate, String strShiftNo,String enableShiftYN,String groupCode,String subGroupCode,String type) 
	  {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder sqlQBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		List<clsPOSBillDtl> listOfItemWiseIncentives = new ArrayList<>();
	    Map<String, clsPOSBillDtl> mapItem = new HashMap<>();
		String waiterShortName = " '' ";
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		waiterShortName = " d.strWShortName ";
	    }
	    String waiterShortNo = " '' ";
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		waiterShortNo = " d.strWaiterNo ";
	    }
		
		try 
		{
			// Q Data
		   sqlBuilder.setLength(0);
		   sqlBuilder.append("SELECT " + waiterShortName + ",b.strItemName,sum(b.dblAmount),c.dblIncentiveValue "
			    + " ,IF(c.strIncentiveType='Amt', (c.dblIncentiveValue)*sum(b.dblQuantity), (c.dblIncentiveValue/100)*sum(b.dblAmount)) as amount, "
			    + " e.strPosName,e.strPosCode,b.strItemCode," + waiterShortNo + ",c.strIncentiveType,sum(b.dblQuantity)  "
			    + " FROM tblqbillhd a,tblqbilldtl b,tblposwiseitemwiseincentives c ");
		   if (!type.equalsIgnoreCase("Item Wise"))
		   {
			sqlBuilder.append(",tblwaitermaster d ");
		   }
		   sqlBuilder.append(",tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
			    + " where a.strBillNo=b.strBillNo "
			    + " and b.strItemCode=c.strItemCode ");
		   if (!type.equalsIgnoreCase("Item Wise"))
		   {
			sqlBuilder.append(" and b.strWaiterNo=d.strWaiterNo ");
		   }
		   sqlBuilder.append(" and a.strPOSCode=e.strPosCode "
			    + " and a.strPOSCode=c.strPOSCode "
			    + " and c.dblIncentiveValue>0 "
			    + " and b.strItemCode=f.strItemCode "
			    + " and f.strSubGroupCode=g.strSubGroupCode "
			    + " and g.strGroupCode=h.strGroupCode "
			    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		   if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}
		   if (!groupCode.equalsIgnoreCase("All"))
		   {
			sqlBuilder.append(" and h.strGroupCode='" + groupCode + "' ");
		   }
		   if (!subGroupCode.equalsIgnoreCase("All"))
		   {
			sqlBuilder.append(" and g.strSubGroupCode='" + subGroupCode + "' ");
		   }

		   sqlBuilder.append("and a.strBillNo not in (select u.strBillNo "
			    + " from tblqbillhd v,tblqbillsettlementdtl u,tblsettelmenthd w "
			    + " where v.strBillNo=u.strBillNo and u.strSettlementCode=w.strSettelmentCode "
			    + " and w.strSettelmentType='Complementary' and date(v.dteBillDate) between '" + fromDate + "' and '" + toDate + "')");
		   if (type.equalsIgnoreCase("Item Wise"))
		   {
			sqlBuilder.append(" group by b.strItemCode ");
			sqlBuilder.append(" order by b.strItemName ");
		   }
		   else if (type.equalsIgnoreCase("Summary"))
		   {
			sqlBuilder.append(" group by b.strWaiterNo");
			sqlBuilder.append(" order by d.strWShortName ");
		   }
		   else
		   {
			sqlBuilder.append(" group by b.strWaiterNo,c.strPOSCode,b.strItemCode ");
			sqlBuilder.append(" order by e.strPosName,d.strWShortName,b.strItemName ");
		   }

			List listSqlLiveWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listSqlLiveWaiterWiseItemSales.size() > 0) 
			{
				for (int i = 0; i < listSqlLiveWaiterWiseItemSales.size(); i++) {
					Object[] objData = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					String itemCode = objData[7].toString();
					if (mapItem.containsKey(itemCode))
					{
					    clsPOSBillDtl obj = mapItem.get(itemCode);

					    obj.setDblQuantity(obj.getDblQuantity() + Double.valueOf(objData[10].toString()));
					    obj.setDblAmount(obj.getDblAmount() + Double.valueOf(objData[2].toString()));
					    obj.setDblIncentive(obj.getDblIncentive() + Double.valueOf(objData[4].toString()));
					}
					else
					{
						clsPOSBillDtl obj = new clsPOSBillDtl();

					    obj.setStrWShortName(objData[0].toString());
					    obj.setStrItemName(objData[1].toString());
					    obj.setStrItemCode(objData[7].toString());
					    obj.setDblAmount(Double.valueOf(objData[2].toString()));
					    obj.setDblIncentivePer(Double.valueOf(objData[3].toString()));
					    obj.setDblIncentive(Double.valueOf(objData[4].toString()));
					    obj.setStrPosName(objData[5].toString());
					    obj.setStrPOSCode(objData[6].toString());
					    obj.setStrWaiterNo(objData[8].toString());
					    obj.setStrRemarks(objData[9].toString());
					    obj.setDblQuantity(Double.valueOf(objData[10].toString()));

					    mapItem.put(itemCode, obj);
					}
				}
			}

			//Live Data
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("SELECT " + waiterShortName + ",b.strItemName,sum(b.dblAmount),c.dblIncentiveValue "
			    + " ,IF(c.strIncentiveType='Amt', (c.dblIncentiveValue)*sum(b.dblQuantity), (c.dblIncentiveValue/100)*sum(b.dblAmount)) as amount, "
			    + " e.strPosName,e.strPosCode,b.strItemCode," + waiterShortNo + ",c.strIncentiveType,sum(b.dblQuantity)  "
			    + " FROM tblbillhd a,tblbilldtl b,tblposwiseitemwiseincentives c ");
		    if (!type.equalsIgnoreCase("Item Wise"))
		    {
			sqlBuilder.append(",tblwaitermaster d ");
		    }
		    sqlBuilder.append(",tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
			    + " where a.strBillNo=b.strBillNo "
			    + " and b.strItemCode=c.strItemCode ");
		    if (!type.equalsIgnoreCase("Item Wise"))
		    {
			sqlBuilder.append(" and b.strWaiterNo=d.strWaiterNo ");
		    }
		    sqlBuilder.append(" and a.strPOSCode=e.strPosCode "
			    + " and a.strPOSCode=c.strPOSCode "
			    + " and c.dblIncentiveValue>0 "
			    + " and b.strItemCode=f.strItemCode "
			    + " and f.strSubGroupCode=g.strSubGroupCode "
			    + " and g.strGroupCode=h.strGroupCode "
			    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' ");
		    if (!posCode.equalsIgnoreCase("All")) {
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			
			if(enableShiftYN.equalsIgnoreCase("Y") && (!strShiftNo.equalsIgnoreCase("All")))
			{
				sqlBuilder.append("and a.intShiftCode='" + strShiftNo + "' ");
			}
		    if (!groupCode.equalsIgnoreCase("All"))
		    {
			sqlBuilder.append(" and h.strGroupCode='" + groupCode + "' ");
		    }
		    if (!subGroupCode.equalsIgnoreCase("All"))
		    {
			sqlBuilder.append(" and g.strSubGroupCode='" + subGroupCode + "' ");
		    }

		    sqlBuilder.append("and a.strBillNo not in (select u.strBillNo "
			    + " from tblbillhd v,tblbillsettlementdtl u,tblsettelmenthd w "
			    + " where v.strBillNo=u.strBillNo and u.strSettlementCode=w.strSettelmentCode "
			    + " and w.strSettelmentType='Complementary' and date(v.dteBillDate) between '" + fromDate + "' and '" + toDate + "')");
		    if (type.equalsIgnoreCase("Item Wise"))
		    {
			sqlBuilder.append(" group by b.strItemCode ");
			sqlBuilder.append(" order by b.strItemName ");
		    }
		    else if (type.equalsIgnoreCase("Summary"))
		    {
			sqlBuilder.append(" group by b.strWaiterNo");
			sqlBuilder.append(" order by d.strWShortName ");
		    }
		    else
		    {
			sqlBuilder.append(" group by b.strWaiterNo,c.strPOSCode,b.strItemCode ");
			sqlBuilder.append(" order by e.strPosName,d.strWShortName,b.strItemName ");
		    }
			List listQBillWaiterWiseItemSales = objBaseService.funGetList(sqlBuilder, "sql");
			if (listQBillWaiterWiseItemSales.size() > 0) {
				for (int i = 0; i < listQBillWaiterWiseItemSales.size(); i++) {
					Object[] objData = (Object[]) listSqlLiveWaiterWiseItemSales.get(i);
					clsPOSBillDtl objBillDtlBean = new clsPOSBillDtl();

					String itemCode = objData[7].toString();
					if (mapItem.containsKey(itemCode))
					{
					    clsPOSBillDtl obj = mapItem.get(itemCode);

					    obj.setDblQuantity(obj.getDblQuantity() + Double.valueOf(objData[10].toString()));
					    obj.setDblAmount(obj.getDblAmount() + Double.valueOf(objData[2].toString()));
					    obj.setDblIncentive(obj.getDblIncentive() + Double.valueOf(objData[4].toString()));
					}
					else
					{
						clsPOSBillDtl obj = new clsPOSBillDtl();

					    obj.setStrWShortName(objData[0].toString());
					    obj.setStrItemName(objData[1].toString());
					    obj.setStrItemCode(objData[7].toString());
					    obj.setDblAmount(Double.valueOf(objData[2].toString()));
					    obj.setDblIncentivePer(Double.valueOf(objData[3].toString()));
					    obj.setDblIncentive(Double.valueOf(objData[4].toString()));
					    obj.setStrPosName(objData[5].toString());
					    obj.setStrPOSCode(objData[6].toString());
					    obj.setStrWaiterNo(objData[8].toString());
					    obj.setStrRemarks(objData[9].toString());
					    obj.setDblQuantity(Double.valueOf(objData[10].toString()));

					    mapItem.put(itemCode, obj);
					}
				}
			}

			Comparator<clsPOSBillDtl> itemNameComparator = new Comparator<clsPOSBillDtl>()
		    {

				@Override
				public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
				{
				    return o1.getStrItemName().compareTo(o2.getStrItemName());
				}
		    };

		    for (clsPOSBillDtl objBillDtl : mapItem.values())
		    {
			   listOfItemWiseIncentives.add(objBillDtl);
		    }

		    Collections.sort(listOfItemWiseIncentives, new clsWaiterWiseSalesComparator(itemNameComparator));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfItemWiseIncentives;
	}

	
	

	
	public List<clsPOSItemWiseConsumption> funProcessItemWiseConsumptionCostCenterReport(String posCode, String fromDate,
			String toDate, String groupCode, String costCenterCode, String strShiftNo, String printZeroAmountModi) {
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbSqlMod = new StringBuilder();
		StringBuilder sbFilters = new StringBuilder();
		Map<String, clsPOSItemWiseConsumption> hmItemWiseConsumption = new HashMap<String, clsPOSItemWiseConsumption>();
		String costCenterCd = "", costCenterNm = "";
		int sqlNo = 0;
		String enableShiftYN="N";
		Map<String, Double> hmMenuHdAmt = new HashMap<String, Double>();
		List<clsPOSItemWiseConsumption> list = new ArrayList<clsPOSItemWiseConsumption>();
		try {
			// Code for Sales Qty for bill detail and bill modifier live & q data
		    // for Sales Qty for bill detail live data  
		    sbSql.setLength(0);
		    String regularAmt = "a.RegularAmt";
		    String compAmt = "b.CompAmt";
		    String dblAmount = "SUM(b.dblamount)";
			
			sbSql.setLength(0);

			sbSql.append(" select  a.strCostCenterName, a.stritemcode, upper(a.itemName),a.RegularQty-IFNULL(b.CompQty,0)RegularQty, "+regularAmt+",  "
			    + "ifnull(b.CompQty,0), ifnull("+compAmt+",0) ,a.strBillNo,a.strMenuName from  "
			    + "(SELECT j.strCostCenterName, b.stritemcode, upper(b.stritemname) itemName, SUM(b.dblQuantity) RegularQty, "+dblAmount+" RegularAmt,a.strBillNo ,k.strMenuName,a.dblUSDConverionRate dblUSDConverionRate"
			    + "  FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and i.strHourlyPricing='NO' ");
			sbSql.append("and i.strCostCenterCode=j.strCostCenterCode "
				    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

			if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			 sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
			
			 sbSql.append("  and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by j.strCostCenterName, b.strItemCode, b.stritemname) a "
			    + " left outer join   "
			    + "(SELECT j.strCostCenterName, b.stritemcode, upper(b.stritemname), SUM(b.dblQuantity) CompQty, "+dblAmount+" CompAmt ,a.strBillNo ,k.strMenuName,a.dblUSDConverionRate dblUSDConverionRate"
			    + "  FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and b.dblAmount = 0  "
			    + " and i.strHourlyPricing='NO' ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
			
		    sbSql.append(" and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by j.strCostCenterName, b.strItemCode, b.stritemname) b "
			    + "on a.strItemCode = b.strItemCode "
			    + " "
			    + "Order by a.strCostCenterName, a.itemName ");
			
		    List listSqlLive = objBaseService.funGetList(sbSql, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + (Double.valueOf(objSale[3].toString())));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[4].toString())));
					    //objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + rsSales.getDouble(4));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSales.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();
					    objItemWiseConsumption.setCostCenterName(objSale[0].toString());
					    objItemWiseConsumption.setItemCode(objSale[1].toString());
					    objItemWiseConsumption.setItemName(objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setComplimentaryQty(Double.valueOf(objSale[5].toString()));
					    objItemWiseConsumption.setComplimentaryAmt(Double.valueOf(objSale[6].toString()));
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[4].toString()));
					    objItemWiseConsumption.setSeqNo(sqlNo);
					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[1].toString() + "!" + objSale[2].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[0].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[0].toString());
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal());
					    }

					}
				}
			}
			
		    
		  //live modifiers
		    String amount = "b.dblamount";
		    String rate="b.dblRate";
		    String discAmt = "b.dblDiscAmt";
		    
		    sbSqlMod.setLength(0);
		    // Code for Sales Qty for modifier live & q data
		    sbSqlMod.append("SELECT b.strItemCode, UPPER(b.strModifierName),b.dblQuantity,"+amount+","+rate+",e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode\n"
			    + ",j.strMenuCode,j.strMenuName,k.strCostCenterName "
			    + "FROM tblbillhd a,tblbillmodifierdtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g\n"
			    + ",tblgrouphd h,tblmenuitempricingdtl i,tblmenuhd j,tblcostcentermaster k "
			    + "WHERE a.strBillNo=b.strBillNo \n"
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			    + "AND a.strPOSCode=e.strPosCode \n"
			    + "AND LEFT(b.strItemCode,7)=f.strItemCode \n"
			    + "AND f.strSubGroupCode=g.strSubGroupCode \n"
			    + "AND g.strGroupCode=h.strGroupCode \n"
			    + "AND LEFT(b.strItemCode,7)=i.strItemCode \n"
			    + "and i.strMenuCode=j.strMenuCode\n"
			    + "and (a.strPOSCode=e.strPosCode or i.strPosCode='All') "
			    + "and i.strCostCenterCode=k.strCostCenterCode "
			    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n"
			    + "  and i.strHourlyPricing='NO'  ");
		    if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
		    {
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    else
		    {
			sbSqlMod.append(" And  b.dblamount >0 ");
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    sbSqlMod.append(sbFilters);
		    
		    listSqlLive = objBaseService.funGetList(sbSqlMod, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[0].toString() + "!" +objSale[1].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[0].toString() + "!" + objSale[1].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[3].toString()) - Double.valueOf(objSale[6].toString())));
					    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.valueOf(objSale[3].toString()));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSalesMod.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();

					    objItemWiseConsumption.setItemCode(objSale[0].toString());
					    objItemWiseConsumption.setItemName(objSale[1].toString());
					    objItemWiseConsumption.setSubGroupName(objSale[7].toString());
					    objItemWiseConsumption.setGroupName(objSale[8].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setComplimentaryQty(0);
					    objItemWiseConsumption.setMenuHead(objSale[12].toString());
					    objItemWiseConsumption.setCostCenterName(objSale[13].toString());
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[3].toString()));
					    double totalRowQty = Double.valueOf(objSale[2].toString()) + 0 + 0 + 0;
					    //objItemWiseConsumption.setTotalQty(totalRowQty);

					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[0].toString() + "!" + objSale[1].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[13].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[13].toString());
						hmMenuHdAmt.put(objSale[13].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[13].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			}
		    
		    
		    
		    
		 // for Sales Qty for bill detail q data 
		    sbSql.setLength(0);
		    sbSql.append(" select  a.strCostCenterName, a.stritemcode, upper(a.itemName),a.RegularQty-IFNULL(b.CompQty,0)RegularQty, "+regularAmt+",  "
			    + "ifnull(b.CompQty,0), ifnull("+compAmt+",0) ,a.strBillNo,a.strMenuName from  "
			    + "(SELECT j.strCostCenterName, b.stritemcode, upper(b.stritemname) itemName, SUM(b.dblQuantity) RegularQty, "+dblAmount+" RegularAmt,a.strBillNo ,k.strMenuName,a.dblUSDConverionRate dblUSDConverionRate"
			    + "  FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and i.strHourlyPricing='NO' ");
//			    + "  and b.dblAmount <> 0  ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }

		    sbSql.append("  and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by j.strCostCenterName, b.strItemCode, b.stritemname) a "
			    + " left outer join   "
			    + "(SELECT j.strCostCenterName, b.stritemcode, upper(b.stritemname), SUM(b.dblQuantity) CompQty, "+dblAmount+" CompAmt ,a.strBillNo ,k.strMenuName,a.dblUSDConverionRate dblUSDConverionRate"
			    + "  FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and b.dblAmount = 0  "
			    + "  and i.strHourlyPricing='NO'  ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    sbSql.append(" and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by j.strCostCenterName, b.strItemCode, b.stritemname) b "
			    + "on a.strItemCode = b.strItemCode "
			    + " "
			    + "Order by a.strCostCenterName, a.itemName ");
		    listSqlLive = objBaseService.funGetList(sbSql, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[4].toString())));

					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();
					    objItemWiseConsumption.setCostCenterName(objSale[0].toString());
					    objItemWiseConsumption.setItemCode(objSale[1].toString());
					    objItemWiseConsumption.setItemName(objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setComplimentaryQty(Double.valueOf(objSale[5].toString()));
					    objItemWiseConsumption.setComplimentaryAmt(Double.valueOf(objSale[6].toString()));
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[4].toString()));
					    objItemWiseConsumption.setSeqNo(sqlNo);
					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[1].toString()+ "!" + objSale[2].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[0].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[0].toString());
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			}
		    
		    
		  //Q modifiers
		    sbSqlMod.setLength(0);
		    // Code for Sales Qty for modifier live & q data
		    sbSqlMod.append("SELECT b.strItemCode, UPPER(b.strModifierName),b.dblQuantity,"+amount+","+rate+",e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode\n"
			    + ",j.strMenuCode,j.strMenuName,k.strCostCenterName "
			    + "FROM tblqbillhd a,tblqbillmodifierdtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g\n"
			    + ",tblgrouphd h,tblmenuitempricingdtl i,tblmenuhd j,tblcostcentermaster k "
			    + "WHERE a.strBillNo=b.strBillNo \n"
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			    + "AND a.strPOSCode=e.strPosCode \n"
			    + "AND LEFT(b.strItemCode,7)=f.strItemCode \n"
			    + "AND f.strSubGroupCode=g.strSubGroupCode \n"
			    + "AND g.strGroupCode=h.strGroupCode \n"
			    + "AND LEFT(b.strItemCode,7)=i.strItemCode \n"
			    + "and i.strMenuCode=j.strMenuCode\n"
			    + "and (a.strPOSCode=e.strPosCode or i.strPosCode='All') "
			    + "and i.strCostCenterCode=k.strCostCenterCode "
			    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			    + "  and i.strHourlyPricing='NO'  "
			    + " ");
		    if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
		    {
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    else
		    {
			sbSqlMod.append(" And  b.dblamount >0 ");
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    sbSqlMod.append(sbFilters);  
		    
		    listSqlLive = objBaseService.funGetList(sbSqlMod, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[0].toString() + "!" +objSale[1].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[0].toString() + "!" + objSale[1].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[3].toString())- Double.valueOf(objSale[6].toString())));
					    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + Double.valueOf(objSale[3].toString()));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSalesMod.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();

					    objItemWiseConsumption.setItemCode(objSale[0].toString());
					    objItemWiseConsumption.setItemName(objSale[1].toString());
					    objItemWiseConsumption.setSubGroupName(objSale[7].toString());
					    objItemWiseConsumption.setGroupName(objSale[8].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setComplimentaryQty(0);
					    objItemWiseConsumption.setMenuHead(objSale[12].toString());
					    objItemWiseConsumption.setCostCenterName(objSale[13].toString());
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[3].toString()));
					    double totalRowQty = Double.valueOf(objSale[2].toString()) + 0 + 0 + 0;
					    //objItemWiseConsumption.setTotalQty(totalRowQty);

					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[0].toString() + "!" + objSale[1].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[13].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[13].toString());
						hmMenuHdAmt.put(objSale[13].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[13].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			} 
		    
		    
		    
		    double totalSaleAmt = 0;

		    for (Map.Entry<String, clsPOSItemWiseConsumption> entry : hmItemWiseConsumption.entrySet())
		    {
		    	clsPOSItemWiseConsumption objItemComp = entry.getValue();
		    	list.add(objItemComp);
		    }

		    Comparator<clsPOSItemWiseConsumption> costCenterComparator = new Comparator<clsPOSItemWiseConsumption>()
		    {

			@Override
			public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2)
			{
			    return o1.getCostCenterName().compareToIgnoreCase(o2.getCostCenterName());
			}
		    };

		    Comparator<clsPOSItemWiseConsumption> itemCodeComparator = new Comparator<clsPOSItemWiseConsumption>()
		    {

			@Override
			public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2)
			{
			    return o1.getItemName().compareToIgnoreCase(o2.getItemName());
			}
		    };

		    Collections.sort(list, new clsPOSItemConsumptionComparator(costCenterComparator, itemCodeComparator
		    ));
		    
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	

	public List<clsPOSItemWiseConsumption> funProcessItemWiseConsumptionMenuHeadReport(String posCode, String fromDate,
			String toDate, String groupCode, String costCenterCode, String strShiftNo, String printZeroAmountModi) 
	  {
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbSqlMod = new StringBuilder();
		StringBuilder sbFilters = new StringBuilder();
		Map<String, clsPOSItemWiseConsumption> hmItemWiseConsumption = new HashMap<String, clsPOSItemWiseConsumption>();
		String costCenterCd = "", costCenterNm = "";
		int sqlNo = 0;
		String enableShiftYN="N";
		Map<String, Double> hmMenuHdAmt = new HashMap<String, Double>();
		List<clsPOSItemWiseConsumption> list = new ArrayList<clsPOSItemWiseConsumption>();
		try {
			// Code for Sales Qty for bill detail and bill modifier live & q data
		    // for Sales Qty for bill detail live data  
		    sbSql.setLength(0);
		    String regularAmt = "a.RegularAmt";
		    String compAmt = "b.CompAmt";
		    String dblAmount = "SUM(b.dblamount)";
			
			sbSql.setLength(0);

			sbSql.append(" select  a.strMenuName, a.stritemcode, upper(a.itemName),a.RegularQty-IFNULL(b.CompQty,0)RegularQty, "+regularAmt+",  "
			    + "ifnull(b.CompQty,0), ifnull("+compAmt+",0) ,a.strBillNo from  "
			    + "(SELECT k.strMenuName, b.stritemcode, upper(b.stritemname) itemName, SUM(b.dblQuantity) RegularQty, "+dblAmount+" RegularAmt,a.strBillNo,a.dblUSDConverionRate dblUSDConverionRate "
			    + "  FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + " and i.strHourlyPricing='NO' ");
//				    + "  and b.dblAmount <> 0  ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    sbSql.append(" and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by k.strMenuName, b.strItemCode, b.stritemname) a left outer join   "
			    + " "
			    + "  "
			    + "(SELECT k.strMenuName, b.stritemcode, upper(b.stritemname), SUM(b.dblQuantity) CompQty, "+dblAmount+" CompAmt ,a.strBillNo,a.dblUSDConverionRate dblUSDConverionRate "
			    + "  FROM tblbillhd a,tblbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and b.dblAmount = 0  "
			    + " and i.strHourlyPricing='NO' ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    sbSql.append("  and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by k.strMenuName, b.strItemCode, b.stritemname) b "
			    + "on a.strItemCode = b.strItemCode "
			    + " "
			    + "Order by a.strMenuName, a.itemName ");
			
		    List listSqlLive = objBaseService.funGetList(sbSql, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[1].toString() + "!" + objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[4].toString())));
					    //objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() + rsSales.getDouble(4));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSales.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();
					    objItemWiseConsumption.setMenuHead(objSale[0].toString());
					    objItemWiseConsumption.setItemCode(objSale[1].toString());
					    objItemWiseConsumption.setItemName(objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setComplimentaryQty(Double.valueOf(objSale[5].toString()));
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[4].toString()));
					    objItemWiseConsumption.setSeqNo(sqlNo);
					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[1].toString() + "!" + objSale[2].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[0].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[0].toString());
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal());
					    }

					}
				}
			}
			
		    
		    // for Sales Qty for bill detail q data 
		    sbSql.setLength(0);

		    sbSql.append(" select  a.strMenuName, a.stritemcode, upper(a.itemName),a.RegularQty-IFNULL(b.CompQty,0)RegularQty, "+regularAmt+",  "
			    + "ifnull(b.CompQty,0), ifnull("+compAmt+",0) ,a.strBillNo from  "
			    + "(SELECT k.strMenuName, b.stritemcode, upper(b.stritemname) itemName, SUM(b.dblQuantity) RegularQty, "+dblAmount+" RegularAmt,a.strBillNo,a.dblUSDConverionRate dblUSDConverionRate "
			    + "  FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + " and i.strHourlyPricing='NO'  ");
//			    + "  and b.dblAmount <> 0  ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    sbSql.append(" and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by k.strMenuName, b.strItemCode, b.stritemname) a left outer join   "
			    + "  "
			    + "(SELECT k.strMenuName, b.stritemcode, upper(b.stritemname), SUM(b.dblQuantity) CompQty, "+dblAmount+" CompAmt ,a.strBillNo,a.dblUSDConverionRate dblUSDConverionRate "
			    + "  FROM tblqbillhd a,tblqbilldtl b, tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h,tblmenuitempricingdtl i,tblcostcentermaster j, tblmenuhd k "
			    + "  WHERE a.strBillNo=b.strBillNo "
			    + "  AND DATE(a.dteBillDate)= DATE(b.dteBillDate)   "
			    + "  AND a.strPOSCode=e.strPosCode "
			    + "  AND b.strItemCode=f.strItemCode "
			    + "  AND f.strSubGroupCode=g.strSubGroupCode "
			    + "  AND g.strGroupCode=h.strGroupCode "
			    + "  and b.strItemCode=i.strItemCode "
			    + "  and (a.strPOSCode=i.strPosCode or i.strPosCode='All') "
			    + "  and i.strCostCenterCode=j.strCostCenterCode "
			    + "  and i.strMenuCode = k.strMenuCode "
			    + "  and b.dblAmount = 0  "
			    + " and i.strHourlyPricing='NO' ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSql.append(" and j.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    sbSql.append("  and DATE(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
			    + "group by k.strMenuName, b.strItemCode, b.stritemname) b "
			    + "on a.strItemCode = b.strItemCode "
			    + "Order by a.strMenuName, a.itemName ");
		    
		    listSqlLive = objBaseService.funGetList(sbSql, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[0].toString() + "!" + objSale[1].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[1].toString()+ "!" + objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() + Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[4].toString())));

					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();
					    objItemWiseConsumption.setMenuHead(objSale[0].toString());
					    objItemWiseConsumption.setItemCode(objSale[1].toString());
					    objItemWiseConsumption.setItemName(objSale[2].toString());
					    objItemWiseConsumption.setSaleQty(Double.valueOf(objSale[3].toString()));
					    objItemWiseConsumption.setComplimentaryQty(Double.valueOf(objSale[5].toString()));
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[4].toString()));
					    objItemWiseConsumption.setSeqNo(sqlNo);
					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[1].toString() + "!" + objSale[2].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[0].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[0].toString());
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[0].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			}
		    
		    
		    //live modifiers
		    String amount = "b.dblamount";
		    String rate = "b.dblRate";
		    String discAmt = "b.dblDiscAmt";
		    
		    sbSqlMod.setLength(0);
		    // Code for Sales Qty for modifier live & q data
		    sbSqlMod.append("SELECT b.strItemCode, UPPER(b.strModifierName),b.dblQuantity,"+amount+","+rate+",e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode\n"
			    + ",j.strMenuCode,j.strMenuName\n"
			    + "FROM tblbillhd a,tblbillmodifierdtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g\n"
			    + ",tblgrouphd h,tblmenuitempricingdtl i,tblmenuhd j\n"
			    + "WHERE a.strBillNo=b.strBillNo \n"
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			    + "AND a.strPOSCode=e.strPosCode \n"
			    + "AND LEFT(b.strItemCode,7)=f.strItemCode \n"
			    + "AND f.strSubGroupCode=g.strSubGroupCode \n"
			    + "AND g.strGroupCode=h.strGroupCode \n"
			    + "AND LEFT(b.strItemCode,7)=i.strItemCode \n"
			    + "and i.strMenuCode=j.strMenuCode\n"
			    + "and (a.strPOSCode=e.strPosCode or i.strPosCode='All')\n"
			    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n"
			    + "  and i.strHourlyPricing='NO' ");
		    
		    
		     if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSqlMod.append(" and i.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
		    {
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    else
		    {
			sbSqlMod.append(" And  b.dblamount >0 ");
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    sbSqlMod.append(sbFilters);

		    listSqlLive = objBaseService.funGetList(sbSqlMod, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[0].toString() + "!" +objSale[1].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[0].toString() + "!" + objSale[1].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() +  Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[3].toString()) -  Double.valueOf(objSale[6].toString())));
					    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() +  Double.valueOf(objSale[3].toString()));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSalesMod.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();

					    objItemWiseConsumption.setItemCode(objSale[0].toString());
					    objItemWiseConsumption.setItemName(objSale[1].toString());
					    objItemWiseConsumption.setSubGroupName(objSale[7].toString());
					    objItemWiseConsumption.setGroupName(objSale[8].toString());
					    objItemWiseConsumption.setSaleQty( Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setComplimentaryQty(0);
					    objItemWiseConsumption.setMenuHead(objSale[12].toString());
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal( Double.valueOf(objSale[3].toString()));
					    double totalRowQty =  Double.valueOf(objSale[2].toString()) + 0 + 0 + 0;
					    //objItemWiseConsumption.setTotalQty(totalRowQty);

					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[0].toString() + "!" + objSale[1].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[12].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[12].toString());
						hmMenuHdAmt.put(objSale[12].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[12].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			}
		    
		    
		    
		    sbSqlMod.setLength(0);
		    // Code for Sales Qty for modifier live & q data
		    sbSqlMod.append("SELECT b.strItemCode, UPPER(b.strModifierName),b.dblQuantity,"+amount+","+rate+",e.strposname,"+discAmt+",g.strSubGroupName,h.strGroupName,a.strBillNo,f.strExternalCode\n"
			    + ",j.strMenuCode,j.strMenuName\n"
			    + "FROM tblqbillhd a,tblqbillmodifierdtl b,tblposmaster e,tblitemmaster f,tblsubgrouphd g\n"
			    + ",tblgrouphd h,tblmenuitempricingdtl i,tblmenuhd j\n"
			    + "WHERE a.strBillNo=b.strBillNo \n"
			    + "AND DATE(a.dteBillDate)= DATE(b.dteBillDate) \n"
			    + "AND a.strPOSCode=e.strPosCode \n"
			    + "AND LEFT(b.strItemCode,7)=f.strItemCode \n"
			    + "AND f.strSubGroupCode=g.strSubGroupCode \n"
			    + "AND g.strGroupCode=h.strGroupCode \n"
			    + "AND LEFT(b.strItemCode,7)=i.strItemCode \n"
			    + "and i.strMenuCode=j.strMenuCode\n"
			    + "and (a.strPOSCode=e.strPosCode or i.strPosCode='All')\n"
			    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'\n"
			    + "  and i.strHourlyPricing='NO' ");
		    if (!costCenterCode.equalsIgnoreCase("All"))
		    {
			sbSqlMod.append(" and i.strCostCenterCode = '" + costCenterCode + "' ");
		    }
		    if (printZeroAmountModi.equalsIgnoreCase("Yes"))//Tjs brew works dont want modifiers details
		    {
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    else
		    {
			sbSqlMod.append(" And  b.dblamount >0 ");
			sbSqlMod.append(" GROUP BY b.strItemCode,b.strModifierName ");
		    }
		    sbSqlMod.append(sbFilters);
		    
		    listSqlLive = objBaseService.funGetList(sbSqlMod, "sql");
		    if (listSqlLive.size() > 0) 
			{
				for (int i = 0; i < listSqlLive.size(); i++) 
				{
					Object[] objSale = (Object[]) listSqlLive.get(i);
					clsPOSItemWiseConsumption objItemWiseConsumption = null;
					if (null != hmItemWiseConsumption.get(objSale[0].toString()+ "!" + objSale[1].toString()))
					{
					    objItemWiseConsumption = hmItemWiseConsumption.get(objSale[0].toString() + "!" + objSale[1].toString());
					    objItemWiseConsumption.setSaleQty(objItemWiseConsumption.getSaleQty() +  Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setSaleAmt(objItemWiseConsumption.getSaleAmt() + (Double.valueOf(objSale[3].toString()) -  Double.valueOf(objSale[6].toString())));
					    objItemWiseConsumption.setSubTotal(objItemWiseConsumption.getSubTotal() +  Double.valueOf(objSale[3].toString()));
					    //objItemWiseConsumption.setTotalQty(objItemWiseConsumption.getTotalQty() + rsSalesMod.getDouble(3));
					}
					else
					{
					    sqlNo++;
					    objItemWiseConsumption = new clsPOSItemWiseConsumption();

					    objItemWiseConsumption.setItemCode(objSale[0].toString());
					    objItemWiseConsumption.setItemName(objSale[1].toString());
					    objItemWiseConsumption.setSubGroupName(objSale[7].toString());
					    objItemWiseConsumption.setGroupName(objSale[8].toString());
					    objItemWiseConsumption.setSaleQty( Double.valueOf(objSale[2].toString()));
					    objItemWiseConsumption.setComplimentaryQty(0);
					    objItemWiseConsumption.setMenuHead(objSale[12].toString());
					    objItemWiseConsumption.setNcQty(0);
					    objItemWiseConsumption.setSubTotal(Double.valueOf(objSale[3].toString()));
					    double totalRowQty =  Double.valueOf(objSale[2].toString()) + 0 + 0 + 0;
					    //objItemWiseConsumption.setTotalQty(totalRowQty);

					}
					if (null != objItemWiseConsumption)
					{
					    hmItemWiseConsumption.put(objSale[0].toString() + "!" + objSale[1].toString(), objItemWiseConsumption);
					    if (null != hmMenuHdAmt.get(objSale[12].toString()))//check menu h
					    {
						double subTot = hmMenuHdAmt.get(objSale[12].toString());
						hmMenuHdAmt.put(objSale[12].toString(), objItemWiseConsumption.getSubTotal() + subTot);
					    }
					    else
					    {
						hmMenuHdAmt.put(objSale[12].toString(), objItemWiseConsumption.getSubTotal());
					    }
					}
				}
			}
		    
		    
		    
		    
		    double totalSaleAmt = 0;
		    for (Map.Entry<String, Double> entry : hmMenuHdAmt.entrySet())
		    {
			totalSaleAmt = totalSaleAmt + entry.getValue();
		    }

		    for (Map.Entry<String, clsPOSItemWiseConsumption> entry : hmItemWiseConsumption.entrySet())
		    {
		    	clsPOSItemWiseConsumption objItemComp = entry.getValue();
				double menuTot = hmMenuHdAmt.get(objItemComp.getMenuHead());
				objItemComp.setMenuHeadPer(Math.rint((objItemComp.getSubTotal() / menuTot) * 100));
				objItemComp.setSubTotalPer(Math.rint((objItemComp.getSubTotal() / totalSaleAmt) * 100));
				list.add(objItemComp);
		    }

		    Comparator<clsPOSItemWiseConsumption> menuHeadComparator = new Comparator<clsPOSItemWiseConsumption>()
		    {

			@Override
			public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2)
			{
			    return o1.getMenuHead().compareToIgnoreCase(o2.getMenuHead());
			}
		    };

		    Comparator<clsPOSItemWiseConsumption> itemCodeComparator = new Comparator<clsPOSItemWiseConsumption>()
		    {

			@Override
			public int compare(clsPOSItemWiseConsumption o1, clsPOSItemWiseConsumption o2)
			{
			    return o1.getItemName().compareToIgnoreCase(o2.getItemName());
			}
		    };

		    Collections.sort(list, new clsPOSItemConsumptionComparator(menuHeadComparator, itemCodeComparator
		    ));
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}	
}