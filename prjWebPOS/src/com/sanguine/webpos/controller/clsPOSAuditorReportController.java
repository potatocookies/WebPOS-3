package com.sanguine.webpos.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsSetupService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSOperatorDtl;
import com.sanguine.webpos.bean.clsPOSVoidBillDtl;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.comparator.clsPOSBillComparator;
import com.sanguine.webpos.comparator.clsPOSOperatorComparator;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;


@Controller
public class clsPOSAuditorReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctions;
	
	@Autowired
	private clsPOSMasterService objMasterService;
	
	@Autowired
	private clsPOSReportService objReportService;
	
	@Autowired
	private intfBaseService objBaseService;
	
	 @Autowired
	 private clsSetupService objSetupService;

	HashMap hmPOSData = new HashMap<String, String>();

	@RequestMapping(value = "/frmPOSAuditorReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)throws Exception
	{
		String strClientCode = request.getSession().getAttribute("gClientCode").toString();
		String POSCode=request.getSession().getAttribute("loginPOS").toString();	
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
		List poslist = new ArrayList();
		poslist.add("ALL");
		List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
		if(listOfPos!=null)
		{
			for(int i =0 ;i<listOfPos.size();i++)
			{
				Object[] obj = (Object[]) listOfPos.get(i);
				poslist.add( obj[1].toString());
				hmPOSData.put( obj[1].toString(), obj[0].toString());
			}
		}
		model.put("posList", poslist);
		List sgNameList = new ArrayList<String>();
		sgNameList.add("ALL");

		model.put("sgNameList", sgNameList);
		
		String posDate = request.getSession().getAttribute("gPOSDate").toString();
		request.setAttribute("POSDate", posDate);
		
		Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
		model.put("gEnableShiftYN",objSetupParameter.get("gEnableShiftYN").toString());

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSAuditorReport_1", "command", new clsPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSAuditorReport", "command", new clsPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSAuditor", method = RequestMethod.POST)
	public void funReport(@ModelAttribute("command") clsPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req,String source)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptAuditorReport.jrxml");
			String POSCode=req.getSession().getAttribute("loginPOS").toString();	
			String strClientCode=req.getSession().getAttribute("gClientCode").toString();	
			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			List listOfPos = objMasterService.funFullPOSCombo(strClientCode);
			if(listOfPos!=null)
			{
				for(int i =0 ;i<listOfPos.size();i++)
				{
					Object[] obj = (Object[]) listOfPos.get(i);
					hmPOSData.put( obj[1].toString(), obj[0].toString());
				}
			}
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName).toString();
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "ALL";
			Map objSetupParameter=objSetupService.funGetParameterValuePOSWise(strClientCode, POSCode, "gEnableShiftYN");
			if(objSetupParameter.get("gEnableShiftYN").toString().equals("Y"))
			{
				shiftNo=objBean.getStrShiftCode();
			}
			hm.remove("shiftNo");
			hm.put("shiftNo", shiftNo);

			StringBuilder sqlLive = new StringBuilder();
			StringBuilder sqlQFile = new StringBuilder();
            StringBuilder sbSqlDisLive = new StringBuilder();
            StringBuilder sbSqlQDisFile = new StringBuilder();
            StringBuilder sbSqlDisFilters = new StringBuilder();
            List<clsPOSOperatorDtl> listOperatorDtl = new ArrayList<>();

            sbSqlDisLive.setLength(0);
            sbSqlQDisFile.setLength(0);
            sbSqlDisFilters.setLength(0);

            String MinBillNo = "";
            String MaxBillNo = "";
            String TotalDiscount = "";
            StringBuilder sql = new StringBuilder();
            sql.setLength(0);
            sql.append("select min(a.strBillNo),max(a.strBillNo),sum(a.dblDiscountAmt)\n"
                    + "from vqbillhd  a \n"
                    + "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' \n"
                    + "Order By a.strBillNo");
           List listAuditorReport = objBaseService.funGetList(sql, "sql");
            if(listAuditorReport.size()>0)
            {
            	for(int i=0;i<listAuditorReport.size();i++)
            	{
            	Object[] obj = (Object[]) listAuditorReport.get(i);	
                MinBillNo = obj[0].toString();
                MaxBillNo = obj[1].toString();
                TotalDiscount = obj[2].toString();
            	}
            }
           
            hm.put("minBillNo", MinBillNo);
            hm.put("maxBillNo", MaxBillNo);
            hm.put("totalDiscount", TotalDiscount);

            sqlLive.setLength(0);
            sqlLive.append(" SELECT a.strBillNo, IFNULL(d.strSettelmentDesc,'ND') AS payMode, IFNULL(a.dblSubTotal,0.00) AS subTotal\n"
                    + ", a.dblTaxAmt,a.dblDiscountAmt, IFNULL(c.dblSettlementAmt,0.00) AS settleAmt\n"
                    + ", IFNULL(e.strCustomerName,'') AS CustomerName,(a.dblSubTotal-a.dblDiscountAmt)NetTotal\n"
                    + ",d.dblThirdPartyComission,d.strComissionType,d.strComissionOn,a.intBillSeriesPaxNo "
                    + " from tblbillhd  a "
                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo  and date(a.dteBillDate)=date(c.dteBillDate) "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and d.strSettelmentType!='Complementary'");

            if (!posCode.equalsIgnoreCase("All"))
            {
            	sqlLive.append(" and a.strPOSCode= '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
            	sqlLive.append(" and a.intShiftCode= '" + shiftNo + "' ");
            }

            sqlLive.append(" Order By d.strSettelmentDesc");

            sqlQFile.setLength(0);
            sqlQFile.append(" SELECT a.strBillNo, IFNULL(d.strSettelmentDesc,'ND') AS payMode, IFNULL(a.dblSubTotal,0.00) AS subTotal\n"
                    + ", a.dblTaxAmt,a.dblDiscountAmt, IFNULL(c.dblSettlementAmt,0.00) AS settleAmt\n"
                    + ", IFNULL(e.strCustomerName,'') AS CustomerName,(a.dblSubTotal-a.dblDiscountAmt)NetTotal\n"
                    + ",d.dblThirdPartyComission,d.strComissionType,d.strComissionOn,a.intBillSeriesPaxNo "
                    + " from tblqbillhd  a "
                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo  and date(a.dteBillDate)=date(c.dteBillDate) "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and d.strSettelmentType!='Complementary' ");

            if (!posCode.equalsIgnoreCase("All"))
            {
            	sqlQFile.append(" and a.strPOSCode= '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
            	sqlQFile.append(" and a.intShiftCode= '" + shiftNo + "' ");
            }

            sqlQFile.append( " Order By d.strSettelmentDesc");

            Map<String, Map<String, clsPOSOperatorDtl>> hmOperatorWiseSales = new HashMap<String, Map<String, clsPOSOperatorDtl>>();
            Map<String, clsPOSOperatorDtl> hmSettlementDtl = null;
            clsPOSOperatorDtl objOperatorWiseSales = null;

            List listOperator = objBaseService.funGetList(sqlLive, "sql");
            if(listOperator.size()>0)
            {
            	for(int i=0;i<listOperator.size();i++)
            	{
            	Object[] obj = (Object[]) listOperator.get(i);	
                if (hmOperatorWiseSales.containsKey(obj[0].toString()))
                {
                    hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
                    if (hmSettlementDtl.containsKey(obj[1].toString()))
                    {
                        objOperatorWiseSales = hmSettlementDtl.get(obj[1].toString());
                        objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt() + Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblGrossAmt(objOperatorWiseSales.getDblGrossAmt() + Double.parseDouble(obj[5].toString()));

                        double comissionOn = 0.00;
                        if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblNetTotal();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblGrossAmt();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                        {
                            comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                        }

                        double comission = 0.00;
                        if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                        {
                            comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                        }
                        else//amt
                        {
                            comission = objOperatorWiseSales.getDblThirdPartyComission();
                        }

                        objOperatorWiseSales.setDblComission(comission);
                    }
                    else
                    {
                        objOperatorWiseSales = new clsPOSOperatorDtl();
                        objOperatorWiseSales.setStrUserCode(obj[0].toString());
                        objOperatorWiseSales.setStrSettlementDesc(obj[1].toString());
                        objOperatorWiseSales.setStrUserName(obj[6].toString());
                        objOperatorWiseSales.setDblSubTotal(0);
                        objOperatorWiseSales.setDblTaxAmt(0);
                        objOperatorWiseSales.setDiscountAmt(0);
                        objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblGrossAmt(Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblNetTotal(Double.parseDouble(obj[7].toString()));

                        objOperatorWiseSales.setDblThirdPartyComission(Double.parseDouble(obj[8].toString()));
                        objOperatorWiseSales.setStrComissionType(obj[9].toString());
                        objOperatorWiseSales.setStrComissionOn(obj[10].toString());
                        objOperatorWiseSales.setIntBillSeriesPaxNo(Double.parseDouble(obj[11].toString()));

                        double comissionOn = 0.00;
                        if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblNetTotal();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblGrossAmt();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                        {
                            comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                        }

                        double comission = 0.00;
                        if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                        {
                            comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                        }
                        else//amt
                        {
                            comission = objOperatorWiseSales.getDblThirdPartyComission();
                        }

                        objOperatorWiseSales.setDblComission(comission);

                    }
                    hmSettlementDtl.put(obj[1].toString(), objOperatorWiseSales);
                }
                else
                {

                    objOperatorWiseSales = new clsPOSOperatorDtl();
                    objOperatorWiseSales.setStrUserCode(obj[0].toString());
                    objOperatorWiseSales.setStrSettlementDesc(obj[1].toString());
                    objOperatorWiseSales.setStrUserName(obj[6].toString());
                    objOperatorWiseSales.setDblSubTotal(0);
                    objOperatorWiseSales.setDblTaxAmt(0);
                    objOperatorWiseSales.setDiscountAmt(0);
                    objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[5].toString()));
                    objOperatorWiseSales.setDblGrossAmt(Double.parseDouble(obj[5].toString()));
                    objOperatorWiseSales.setDblNetTotal(Double.parseDouble(obj[7].toString()));

                    objOperatorWiseSales.setDblThirdPartyComission(Double.parseDouble(obj[8].toString()));
                    objOperatorWiseSales.setStrComissionType(obj[9].toString());
                    objOperatorWiseSales.setStrComissionOn(obj[10].toString());
                    objOperatorWiseSales.setIntBillSeriesPaxNo(Double.parseDouble(obj[11].toString()));

                    double comissionOn = 0.00;
                    if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                    {
                        comissionOn = objOperatorWiseSales.getDblNetTotal();
                    }
                    else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                    {
                        comissionOn = objOperatorWiseSales.getDblGrossAmt();
                    }
                    else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                    {
                        comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                    }

                    double comission = 0.00;
                    if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                    {
                        comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                    }
                    else//amt
                    {
                        comission = objOperatorWiseSales.getDblThirdPartyComission();
                    }

                    objOperatorWiseSales.setDblComission(comission);

                    hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
                    hmSettlementDtl.put(obj[1].toString(), objOperatorWiseSales);
                }
                hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
            }
            }
          

            listOperator = objBaseService.funGetList(sqlQFile, "sql");
            if(listOperator.size()>0)
            {
            	for(int i=0;i<listOperator.size();i++)
            	{
            	Object[] obj = (Object[]) listOperator.get(i);	
                if (hmOperatorWiseSales.containsKey(obj[0].toString()))
                {
                    hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
                    if (hmSettlementDtl.containsKey(obj[1].toString()))
                    {
                        objOperatorWiseSales = hmSettlementDtl.get(obj[1].toString());
                        objOperatorWiseSales.setSettleAmt(objOperatorWiseSales.getSettleAmt() + Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblGrossAmt(objOperatorWiseSales.getDblGrossAmt() + Double.parseDouble(obj[5].toString()));

                        double comissionOn = 0.00;
                        if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblNetTotal();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblGrossAmt();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                        {
                            comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                        }

                        double comission = 0.00;
                        if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                        {
                            comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                        }
                        else//amt
                        {
                            comission = objOperatorWiseSales.getDblThirdPartyComission();
                        }

                        objOperatorWiseSales.setDblComission(comission);
                    }
                    else
                    {
                        objOperatorWiseSales = new clsPOSOperatorDtl();
                        objOperatorWiseSales.setStrUserCode(obj[0].toString());
                        objOperatorWiseSales.setStrSettlementDesc(obj[1].toString());
                        objOperatorWiseSales.setStrUserName(obj[6].toString());
                        objOperatorWiseSales.setDblSubTotal(0);
                        objOperatorWiseSales.setDblTaxAmt(0);
                        objOperatorWiseSales.setDiscountAmt(0);
                        objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblGrossAmt(Double.parseDouble(obj[5].toString()));
                        objOperatorWiseSales.setDblNetTotal(Double.parseDouble(obj[7].toString()));

                        objOperatorWiseSales.setDblThirdPartyComission(Double.parseDouble(obj[8].toString()));
                        objOperatorWiseSales.setStrComissionType(obj[9].toString());
                        objOperatorWiseSales.setStrComissionOn(obj[10].toString());
                        objOperatorWiseSales.setIntBillSeriesPaxNo(Double.parseDouble(obj[11].toString()));

                        double comissionOn = 0.00;
                        if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblNetTotal();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                        {
                            comissionOn = objOperatorWiseSales.getDblGrossAmt();
                        }
                        else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                        {
                            comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                        }

                        double comission = 0.00;
                        if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                        {
                            comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                        }
                        else//amt
                        {
                            comission = objOperatorWiseSales.getDblThirdPartyComission();
                        }

                        objOperatorWiseSales.setDblComission(comission);

                    }
                    hmSettlementDtl.put(obj[1].toString(), objOperatorWiseSales);
                }
                else
                {

                    objOperatorWiseSales = new clsPOSOperatorDtl();
                    objOperatorWiseSales.setStrUserCode(obj[0].toString());
                    objOperatorWiseSales.setStrSettlementDesc(obj[1].toString());
                    objOperatorWiseSales.setStrUserName(obj[6].toString());
                    objOperatorWiseSales.setDblSubTotal(0);
                    objOperatorWiseSales.setDblTaxAmt(0);
                    objOperatorWiseSales.setDiscountAmt(0);
                    objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[5].toString()));
                    objOperatorWiseSales.setDblGrossAmt(Double.parseDouble(obj[5].toString()));
                    objOperatorWiseSales.setDblNetTotal(Double.parseDouble(obj[7].toString()));

                    objOperatorWiseSales.setDblThirdPartyComission(Double.parseDouble(obj[8].toString()));
                    objOperatorWiseSales.setStrComissionType(obj[9].toString());
                    objOperatorWiseSales.setStrComissionOn(obj[10].toString());
                    objOperatorWiseSales.setIntBillSeriesPaxNo(Double.parseDouble(obj[11].toString()));

                    double comissionOn = 0.00;
                    if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Net Amount"))
                    {
                        comissionOn = objOperatorWiseSales.getDblNetTotal();
                    }
                    else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("Gross Amount"))
                    {
                        comissionOn = objOperatorWiseSales.getDblGrossAmt();
                    }
                    else if (objOperatorWiseSales.getStrComissionOn().equalsIgnoreCase("No. Of PAX"))
                    {
                        comissionOn = objOperatorWiseSales.getIntBillSeriesPaxNo();
                    }

                    double comission = 0.00;
                    if (objOperatorWiseSales.getStrComissionType().equalsIgnoreCase("Per"))
                    {
                        comission = (objOperatorWiseSales.getDblThirdPartyComission() / 100) * comissionOn;
                    }
                    else//amt
                    {
                        comission = objOperatorWiseSales.getDblThirdPartyComission();
                    }

                    objOperatorWiseSales.setDblComission(comission);

                    hmSettlementDtl = new HashMap<String, clsPOSOperatorDtl>();
                    hmSettlementDtl.put(obj[1].toString(), objOperatorWiseSales);
                }
                hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
            }
            }
           
            sbSqlDisLive.append("SELECT b.strBillNo, b.strPOSCode, c.strPOSName "
                    + ",sum(b.dblSubTotal),sum(b.dblDiscountAmt),sum(b.dblTaxAmt),'SANGUINE' "
                    + " FROM tblbillhd b "
                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode  "
                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

            sbSqlQDisFile.append(" SELECT b.strBillNo, b.strPOSCode, c.strPOSName"
                    + ",sum(b.dblSubTotal),sum(b.dblDiscountAmt),sum(b.dblTaxAmt),'SANGUINE' "
                    + " FROM tblqbillhd b "
                    + " inner join tblposmaster c on b.strPOSCode=c.strPOSCode "
                    + " WHERE date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

            if (!posCode.equalsIgnoreCase("All"))
            {
                sbSqlDisFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
                sbSqlDisFilters.append(" AND b.intShiftCode = '" + shiftNo + "' ");
            }

            sbSqlDisFilters.append(" GROUP BY b.strBillNo, b.strPosCode");

            sbSqlDisLive.append(sbSqlDisFilters);
            sbSqlQDisFile.append(sbSqlDisFilters);

            double dis = 0;

            List listOperatorDis = objBaseService.funGetList(sbSqlDisLive, "sql");
            if(listOperatorDis.size()>0)
            {
            	for(int i=0;i<listOperatorDis.size();i++)
            	{
            	Object[] obj = (Object[]) listOperatorDis.get(i);	
                if (hmOperatorWiseSales.containsKey(obj[0].toString()))
                {
                    hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
                    Set<String> setKeys = hmSettlementDtl.keySet();
                    for (String keys : setKeys)
                    {
                        objOperatorWiseSales = hmSettlementDtl.get(keys);
                        objOperatorWiseSales.setDblSubTotal(objOperatorWiseSales.getDblSubTotal() + Double.parseDouble(obj[3].toString()));
                        objOperatorWiseSales.setDblTaxAmt(objOperatorWiseSales.getDblTaxAmt() + Double.parseDouble(obj[5].toString()));
                        dis = objOperatorWiseSales.getDiscountAmt();
                        objOperatorWiseSales.setDiscountAmt(dis + Double.parseDouble(obj[4].toString()));
                        hmSettlementDtl.put(keys, objOperatorWiseSales);
                        break;
                    }
                    hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
                }
            }
            }
           
            listOperatorDis = objBaseService.funGetList(sbSqlQDisFile, "sql");
            if(listOperatorDis.size()>0)
            {
            	for(int i=0;i<listOperatorDis.size();i++)
            	{	
	                Object[] obj = (Object[]) listOperatorDis.get(i);
            		if (hmOperatorWiseSales.containsKey(obj[0].toString()))
	                {
	                    hmSettlementDtl = hmOperatorWiseSales.get(obj[0].toString());
	                    Set<String> setKeys = hmSettlementDtl.keySet();
	                    for (String keys : setKeys)
	                    {
	                        objOperatorWiseSales = hmSettlementDtl.get(keys);
	                        objOperatorWiseSales.setDblSubTotal(objOperatorWiseSales.getDblSubTotal() + Double.parseDouble(obj[3].toString()));
	                        objOperatorWiseSales.setDblTaxAmt(objOperatorWiseSales.getDblTaxAmt() + Double.parseDouble(obj[5].toString()));
	                        dis = objOperatorWiseSales.getDiscountAmt();
	                        objOperatorWiseSales.setDiscountAmt(dis + Double.parseDouble(obj[4].toString()));
	                        hmSettlementDtl.put(keys, objOperatorWiseSales);
	                        break;
	                    }
	                    hmOperatorWiseSales.put(obj[0].toString(), hmSettlementDtl);
	                }
            	}
            }
         

            for (Map.Entry<String, Map<String, clsPOSOperatorDtl>> entry : hmOperatorWiseSales.entrySet())
            {
                Map<String, clsPOSOperatorDtl> hmOpSettlementDtl = entry.getValue();
                for (Map.Entry<String, clsPOSOperatorDtl> entryOp : hmOpSettlementDtl.entrySet())
                {
                	clsPOSOperatorDtl objOperatorDtl = entryOp.getValue();
                    listOperatorDtl.add(objOperatorDtl);
                }
            }

            sqlLive.setLength(0);
            sqlLive.append(" select ifnull(d.strSettelmentDesc,'') as payMode "
                    + " ,sum(c.dblSettlementAmt) "
                    + " from tblbillhd a "
                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and d.strSettelmentType!='Complementary' ");

            if (!posCode.equalsIgnoreCase("All"))
            {
            	sqlLive.append( " and a.strPOSCode= '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
            	sqlLive.append( " and a.intShiftCode= '" + shiftNo + "' ");
            }

            sqlLive.append( " Group By d.strSettelmentDesc ");

            sqlQFile.setLength(0);
            sqlQFile.append(" select ifnull(d.strSettelmentDesc,'') as payMode "
                    + " ,sum(c.dblSettlementAmt) "
                    + " from tblqbillhd a "
                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and date(a.dteBillDate)=date(c.dteBillDate) "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
                    + " and d.strSettelmentType!='Complementary' ");

            if (!posCode.equalsIgnoreCase("All"))
            {
            	 sqlQFile.append( " and a.strPOSCode= '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
            	 sqlQFile.append( " and a.intShiftCode= '" + shiftNo + "' ");
            }

            sqlQFile.append( " Group By d.strSettelmentDesc ");

            int previousListIndex = 0;
            List<clsPOSOperatorDtl> listSettleDetail = new ArrayList<>();
            listOperator = objBaseService.funGetList(sqlLive, "sql");
           if(listOperator.size()>0)
            {
        	   for(int i=0;i<listOperator.size();i++)
        	   {	   
        		   Object[] obj = (Object[]) listOperator.get(i);
	                boolean flgFound = false;
	                if (!obj[0].toString().isEmpty())
	                {
	                    objOperatorWiseSales = new clsPOSOperatorDtl();
	                    if (listSettleDetail.size() > 0)
	                    {
	                        for (int cnt = 0; cnt < listSettleDetail.size(); cnt++)
	                        {
	                            clsPOSOperatorDtl objPreviousList = listSettleDetail.get(cnt);
	                            if (objPreviousList.getStrSettlementDesc().equals(obj[0].toString()))
	                            {
	                                double settleAmount = objPreviousList.getSettleAmt() + Double.parseDouble(obj[1].toString());
	                                objOperatorWiseSales.setStrSettlementDesc(obj[0].toString());
	                                objOperatorWiseSales.setSettleAmt(settleAmount);
	                                flgFound = true;
	                                previousListIndex = cnt;
	                            }
	                        }
	
	                    }
	                    if (flgFound)
	                    {
	                        listSettleDetail.remove(previousListIndex);
	                        listSettleDetail.add(objOperatorWiseSales);
	                    }
	                    else
	                    {
	                        objOperatorWiseSales.setStrSettlementDesc(obj[0].toString());
	                        objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[1].toString()));
	                        listSettleDetail.add(objOperatorWiseSales);
	                    }
	                }
        	   }

            }

            listOperator = objBaseService.funGetList(sqlQFile, "sql");
            if(listOperator.size()>0)
            {
            	for(int i=0;i<listOperator.size();i++)
            	{
            		Object[] obj = (Object[]) listOperator.get(i);
	                boolean flgFound = false;
	                if (!obj[0].toString().isEmpty())
	                {
	                    objOperatorWiseSales = new clsPOSOperatorDtl();
	                    if (listSettleDetail.size() > 0)
	                    {
	                        for (int cnt = 0; cnt < listSettleDetail.size(); cnt++)
	                        {
	                            clsPOSOperatorDtl objPreviousList = listSettleDetail.get(cnt);
	                            if (objPreviousList.getStrSettlementDesc().equals(obj[0].toString()))
	                            {
	                                double settleAmount = objPreviousList.getSettleAmt() + Double.parseDouble(obj[1].toString());
	                                objOperatorWiseSales.setStrSettlementDesc(obj[0].toString());
	                                objOperatorWiseSales.setSettleAmt(settleAmount);
	                                flgFound = true;
	                                previousListIndex = cnt;
	
	                            }
	                        }
	
	                    }
	                    if (flgFound)
	                    {
	                        listSettleDetail.remove(previousListIndex);
	                        listSettleDetail.add(objOperatorWiseSales);
	                    }
	                    else
	                    {
	                        objOperatorWiseSales.setStrSettlementDesc(obj[0].toString());
	                        objOperatorWiseSales.setSettleAmt(Double.parseDouble(obj[1].toString()));
	                        listSettleDetail.add(objOperatorWiseSales);
	                    }
	                }
            	}
            }
           
            sqlLive.setLength(0);
            sqlLive.append(" select ifnull(a.strBillNo,''),a.strReasonName,a.strRemark,a.strUserCreated as CreatedUser,a.dblActualAmount,a.strUserEdited as VoidedUser "
                    + " from tblvoidbillhd a "
                    + " where date(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + " and strTransType='VB' ");

            if (!posCode.equalsIgnoreCase("All"))
            {
            	 sqlLive.append(" and a.strPosCode= '" + posCode + "' ");
            }
            if (!shiftNo.equalsIgnoreCase("All"))
            {
            	 sqlLive.append( " and a.intShiftCode= '" + shiftNo + "' ");
            }

            sqlLive.append( " Order By a.strBillNo ");

            List<clsPOSOperatorDtl> listVoidBillDetail = new ArrayList<>();
            listOperator = objBaseService.funGetList(sqlLive, "sql");
            if(listOperator.size()>0)
            {
            	for(int i=0;i<listOperator.size();i++)
            	{
            	Object[] obj = (Object[]) listOperator.get(i);	
                objOperatorWiseSales = new clsPOSOperatorDtl();
                objOperatorWiseSales.setStrUser(obj[0].toString());
                objOperatorWiseSales.setStrUserName(obj[3].toString());//created user
                objOperatorWiseSales.setReason(obj[1].toString());
                objOperatorWiseSales.setRemark(obj[2].toString());
                objOperatorWiseSales.setDblBillAmount(Double.parseDouble(obj[4].toString()));
                objOperatorWiseSales.setStrVoidedUser(obj[5].toString());//voided user

                listVoidBillDetail.add(objOperatorWiseSales);
            	}
            }
            
            Comparator<clsPOSOperatorDtl> settleModeComparator = new Comparator<clsPOSOperatorDtl>()
            {

                @Override
                public int compare(clsPOSOperatorDtl o1, clsPOSOperatorDtl o2)
                {
                    return o1.getStrSettlementDesc().compareTo(o2.getStrSettlementDesc());
                }
            };
            Comparator<clsPOSOperatorDtl> billWiseComparator = new Comparator<clsPOSOperatorDtl>()
            {

                @Override
                public int compare(clsPOSOperatorDtl o1, clsPOSOperatorDtl o2)
                {
                    return o1.getStrUserCode().compareTo(o2.getStrUserCode());
                }
            };

            Collections.sort(listOperatorDtl, new clsPOSOperatorComparator(settleModeComparator, billWiseComparator));

            hm.put("listOfOperatorDtl", listOperatorDtl);
            hm.put("listOfBillSettleDtl", listSettleDetail);
            hm.put("listOfVoidBillDtl", listVoidBillDetail);
            List list = new ArrayList();
            list.add("1");
			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);
			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
			jprintlist.add(print);
			String filePath = System.getProperty("user.dir")+ "/DayEndMailReports/";
			String extension=".pdf";
			if (!objBean.getStrDocType().equals("PDF"))
			{
				objBean.setStrDocType("EXCEL");
				extension=".xls";
			}	
			String fileName = "AuditorReport_"+ fromDate + "_To_" + toDate + "_" + strUserCode + extension;
			filePath=filePath+"/"+fileName;
			if (jprintlist.size() > 0)
			{
				ServletOutputStream servletOutputStream = resp.getOutputStream();
				if (objBean.getStrDocType().equals("PDF"))
				{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=AuditorReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
					exporter.exportReport();
					servletOutputStream.flush();
					servletOutputStream.close();
				}
				else
				{
					JRExporter exporter = new JRXlsExporter();
					resp.setContentType("application/xlsx");
					exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
					if(null!=source && source.equals("DayEndMail"))
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath));
					exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=AuditorReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
					exporter.exportReport();
					/*servletOutputStream.flush();
					servletOutputStream.close();*/
				}
			}
			else
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter().append("No Record Found");

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("Hi");

	}

}
