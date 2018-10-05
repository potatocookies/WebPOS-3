package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAssignHomeDeliveryBean;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSReprintDocumentsBean;
import com.sanguine.webpos.model.clsPOSMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.sevice.clsPOSReportService;
import com.sanguine.webpos.sevice.clsPOSTransactionService;

@Controller
public class clsPOSReprintController 
{
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	clsPOSMasterService objMasterService;
	@Autowired
	clsPOSTransactionService objTransService;
	
	@Autowired
	clsPOSReportService objReportService;
	
	
	Map mapPOSName=new HashMap();
	@RequestMapping(value = "/frmPOSReprint", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSAssignHomeDeliveryBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
			
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		String posCode=request.getSession().getAttribute("loginPOS").toString();
		
		List posList= new ArrayList();
		String strPOSName="";
		List list=objMasterService.funFillPOSCombo(clientCode);
		mapPOSName.put("All", "All");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			posList.add(Array.get(obj, 1).toString());
			mapPOSName.put(Array.get(obj, 1).toString(),Array.get(obj, 0).toString());
			if(posCode.equals(Array.get(obj, 0).toString()))
			{
				strPOSName=Array.get(obj, 1).toString();
			}
		}
		model.put("posList",posList);
		request.setAttribute("posName", strPOSName);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSReprint_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSReprint");
		}else {
			return null;
		}
}
	
	@RequestMapping(value = "/loadFunExecute", method = RequestMethod.GET)
	public @ResponseBody Map loadFunExecute(HttpServletRequest req)
	{
		List listmain =new ArrayList();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode="";
		if(mapPOSName.containsKey(req.getParameter("posName")))
		{
			posCode=(String) mapPOSName.get(req.getParameter("posName"));
		}
		String operationType=req.getParameter("operationType");
		String kotFor=req.getParameter("kotFor");
	
		List<clsPOSReprintDocumentsBean> listReprintDocsData=new ArrayList<clsPOSReprintDocumentsBean>();
   
        Map mapReprintDtl=new HashMap();
        Map<String,List<clsPOSBillDtl>> hmReprintDtl=objTransService.funExecute(posCode, operationType, kotFor);
        List<clsPOSBillDtl> listPreprintDtl=hmReprintDtl.get("strOperation");
        mapReprintDtl.put("AllTblData",listPreprintDtl);
        mapReprintDtl.put("strOpr",kotFor);
              
	 	return mapReprintDtl;
				
		
	}
	
//	@RequestMapping(value = "/funViewButtonPressed", method = RequestMethod.GET)
//	public @ResponseBody JSONObject funViewButtonPressed(HttpServletRequest req)
//	{
//		String transactionType=req.getParameter("transactionType");
//		String kotFor=req.getParameter("kotFor");
//		String docNo=req.getParameter("code");
//		JSONObject jObjData=new JSONObject();
//		String posUrl = "http://localhost:8080/prjSanguineWebService/WebPOSTransactions/funViewButtonPressed"
//				+ "?docNo="+docNo+"&transactionType="+transactionType+"&kotFor="+kotFor;
//		jObjData =objGlobal.funGETMethodUrlJosnObjectData(posUrl);
//		
//		return jObjData;
//		
//	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/frmViewData", method = RequestMethod.GET)	
	public void funOpenFormViewData(@ModelAttribute("command") clsPOSReprintDocumentsBean objBean, HttpServletResponse resp,HttpServletRequest request) throws Exception
	{
		
		String clientCode=request.getSession().getAttribute("gClientCode").toString();
		String posCode=request.getSession().getAttribute("loginPOS").toString();
		String kotFor=request.getParameter("kotFor");
		String code=request.getParameter("code");
		String transactionType=request.getParameter("transactionType");
		String posName=null;
		String webStockUserCode=request.getSession().getAttribute("gUserCode").toString();
		String strDocType="PDF";
		String companyName=request.getSession().getAttribute("gCompanyName").toString();
		
		String posDate= request.getSession().getAttribute("gPOSDate").toString();
		String PrintVatNoPOS="",vatNo="",printServiceTaxNo="",serviceTaxNo="";

		clsPOSMasterModel objModel=objMasterService.funSelectedPOSMasterData(posCode, clientCode);
		posName=objModel.getStrPosName();
		
		List<List<clsPOSBillDtl>> listData = new ArrayList<>();
		
		try
		{
			
		    String KOT="",kotType="",dublicate="",POS="",hostName="",costCenter="",PAX="",DATE_TIME="",KOTorNC="",tableNo="",waiterName="";
			
			
		    Map mapViewData=objReportService.funViewButtonPressed(code,transactionType,kotFor,posCode,
					clientCode, posName,webStockUserCode,posDate,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
			

			List<clsPOSBillDtl> list =new ArrayList<>();
			List listMain=new ArrayList<>();
			List listMain1=new ArrayList<>();
			List listBillDtl=new ArrayList<>();
			String format="";
			String noOfLines ="", imagePath="";
			 HashMap hm = new HashMap();
			 
			
			String reportName="";
			if(kotFor.equalsIgnoreCase("Dina"))
			{
				reportName= servletContext.getRealPath("/WEB-INF/reports/webpos/rptGenrateKOTJasperReport.jrxml");
				 imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
				 listMain=(List) mapViewData.get("listData");
				 noOfLines =  (String) mapViewData.get("gNoOfLinesInKOTPrint"); 
				 listMain1 = (List) mapViewData.get("listOfKOTDetail");
				 if(null!=listMain)
					{	
						for(int i=0;i<listMain.size();i++)
						{
							//JSONObject jObjtemp =(JSONObject) jarr.get(i);
							 Map mapTemp=(Map) listMain.get(i);
							 KOTorNC = mapTemp.get("KOTorNC").toString();
							 tableNo = mapTemp.get("tableNo").toString();
							 KOT=mapTemp.get("KOT").toString();
							 kotType=mapTemp.get("KOTType").toString();
							 dublicate=mapTemp.get("dublicate").toString();
							 POS=mapTemp.get("POS").toString();
							 hostName=mapTemp.get("KOTFrom").toString(); 
							 waiterName=mapTemp.get("waiterName").toString();
							 costCenter=mapTemp.get("costCenter").toString();
							 PAX=mapTemp.get("PAX").toString();
							 DATE_TIME=mapTemp.get("DATE_TIME").toString();
						} 
					}	
					if(null!=listMain1 && listMain1.size()>0)
					{
						list=listMain1;
					 }
						for (int cntLines = 0; cntLines < Integer.parseInt(noOfLines); cntLines++)
			            {
			                clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
			                objBillDtl.setDblQuantity(0);
			                objBillDtl.setStrItemName("");
			                list.add(objBillDtl);
			            }
						
						
				            hm.put("KOT", KOT);
				            hm.put("KOTorNC",KOTorNC);
				            hm.put("dublicate",dublicate );
				            hm.put("tableNo", tableNo);
				            hm.put("POS",POS );
				            hm.put("waiterName", waiterName);
				            hm.put("KOT From",hostName );
				            hm.put("costCenter", costCenter);
				            hm.put("PAX", PAX);
				            hm.put("DATE_TIME", DATE_TIME);
				            hm.put("KOTType", kotType);
				            hm.put("imagePath", imagePath);
				            hm.put("clientName", companyName);
				            hm.put("shiftNo", "1");
				            hm.put("userName", webStockUserCode);
				            hm.put("listOfItemDtl", list);
						
				            listData.add(list);
			}
			else if(kotFor.equalsIgnoreCase("DirectBiller"))
			{
				
				int result =  (int) mapViewData.get("result"); 
				format =  (String) mapViewData.get("format"); 
				listMain1 = (List) mapViewData.get("listOfBillDetail");
				List listItemDtl = new ArrayList<>();
				 
				String posWiseHeading="",duplicate="",BillType="",ClientName="",ClientAddress1="",ClientAddress2="",ClientAddress3="",ClientCity="",TEL_NO="";
				String EMAIL_ID="",TAX_INVOICE="",BillNo="",Totl="",name="",address="",mobile_no="",footer="";
				String taxAmount="",taxDesc="",discount="",discText="",discAmt="",reason="",remark="";
				
				clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
				listMain = (List) mapViewData.get("listData");
				List<clsPOSBillDtl> listOfGrandTotal =new ArrayList<>();
				List<clsPOSBillDtl> listOfFoodBillDetail = new ArrayList<>();
				List<clsPOSBillDtl> listOfLiqourBillDetail = new ArrayList<>();
				List<clsPOSBillDtl> listOfHomeDeliveryDtl =new ArrayList<>();
				List<clsPOSBillDtl> listOfServiceVatDetail =new ArrayList<>();
				List<clsPOSBillDtl> listOfFooterDtl =new ArrayList<>();
				List<clsPOSBillDtl> listOfTaxDtl = new ArrayList<>();
				List<clsPOSBillDtl> listOfDiscountDtl = new ArrayList<>();
				List<clsPOSBillDtl> listSubTotal = new ArrayList<>();
				List<clsPOSBillDtl> listOfSettlementDetail = new ArrayList<>();
				
				if(null!=listMain)
				{	
					for(int i=0;i<listMain.size();i++)
					{
						Map mapTemp =(Map) listMain.get(i);
						
						List listTotal = (List) mapTemp.get("listOfGrandTotalDtl");
						
						if(null!=listTotal)
						{	
							for(i=0;i<listTotal.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapjTot= (Map) listTotal.get(i);
								objBillDtl.setDblAmount(Double.parseDouble(mapjTot.get("grandTotal").toString())); 
								
								 listOfGrandTotal.add(objBillDtl);
								
							}
						}	
						
						List listHomeDel = (List) mapTemp.get("listOfHomeDeliveryDtl");
						
						if(null!=listHomeDel)
						{	
							for(i=0;i<listHomeDel.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								 Map mapHomeDel= (Map) listHomeDel.get(i);
								 objBillDtl.setStrItemName("Name:"+mapHomeDel.get("NAME").toString());
								 listOfHomeDeliveryDtl.add(objBillDtl);
								
								 objBillDtl = new clsPOSBillDtl();
								 objBillDtl.setStrItemName("Address:"+mapHomeDel.get("Address").toString());
								 listOfHomeDeliveryDtl.add(objBillDtl);
								
								 objBillDtl = new clsPOSBillDtl();
								 objBillDtl.setStrItemName("Mobile No"+mapHomeDel.get("MOBILE_NO").toString());
								 listOfHomeDeliveryDtl.add(objBillDtl);
								 
							}
						}
						
						List listServiceTaxDtl = (List) mapTemp.get("listOfServiceVatDetail");
						
						if(null!=listServiceTaxDtl)
						{	
							for(i=0;i<listServiceTaxDtl.size();i++)
							{
								 objBillDtl=new clsPOSBillDtl();
								 Map mapServieTaxDtl= (Map) listServiceTaxDtl.get(i);
								 objBillDtl.setStrItemName("Service Tax No.    :"+mapServieTaxDtl.get("Service Tax No.:").toString());	
								 listOfServiceVatDetail.add(objBillDtl);
							}
						}
						
						List listFooterDtl = (List) mapTemp.get("listOfFooterDtl");
						
						if(null!=listFooterDtl)
						{	
							for(i=0;i<listFooterDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapFooterDtl= (Map) listFooterDtl.get(i);
								objBillDtl.setStrItemName(mapFooterDtl.get("Thank").toString());	
								 listOfFooterDtl.add(objBillDtl);
							}
						}
						List listTaxDtl = (List) mapTemp.get("listOfTaxDtl");
						
						if(null!=listTaxDtl)
						{	
							for(i=0;i<listTaxDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapTax= (Map) listTaxDtl.get(i);
								 objBillDtl=new clsPOSBillDtl();
								
								 objBillDtl.setDblAmount(Double.parseDouble(mapTax.get("taxAmount").toString()));
								 objBillDtl.setStrItemName(mapTax.get("taxDesc").toString());
								
								 listOfTaxDtl.add(objBillDtl);
							}
						}	
						
						List listDiscountDtl= (List) mapTemp.get("listOfDiscountDtl");
						
						if(null!=listDiscountDtl)
						{	
							for(i=0;i<listDiscountDtl.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapDiscount= (Map) listDiscountDtl.get(i);
								objBillDtl.setStrItemName(mapDiscount.get("Discount").toString());
								objBillDtl.setStrItemName(mapDiscount.get("discText").toString());
								objBillDtl.setDblAmount(Double.parseDouble(mapDiscount.get("discAmt").toString()));
								objBillDtl.setStrItemName(mapDiscount.get("Reason").toString());
								objBillDtl.setStrItemName(mapDiscount.get("Remark").toString());
								listOfDiscountDtl.add(objBillDtl);
								
							}
						}
						List listSettlementDtl = (List) mapTemp.get("listOfSettlementDetail");
						
						if(null!=listSettlementDtl)
						{	
							for(i=0;i<listSettlementDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapSettlementDtl= (Map) listSettlementDtl.get(i);
								if((!mapSettlementDtl.get("settleDesc").toString().equals("PAID AMT")) && (!mapSettlementDtl.get("settleDesc").toString().equals("REFUND AMT")))
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("settleAmt").toString()));	 
								}
								else if((mapSettlementDtl.get("settleDesc").toString().equals("PAID AMT")))
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("paidAmt").toString()));
								}
								else
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("refundAmt").toString()));
								}
									
								listOfSettlementDetail.add(objBillDtl);
								
							}
						}
						
						List listLiqur = (List) mapTemp.get("listOfLiqourBillDetail");
						if(null!=listLiqur)
						{
							for( i=0;i<listLiqur.size();i++)
							{
								 Map mapTemp1 =(Map) listLiqur.get(i);
								
								clsPOSBillDtl objclsPOSBillDtl=new clsPOSBillDtl();
								
								objclsPOSBillDtl.setDblQuantity(Double.parseDouble(mapTemp1.get("saleQty").toString()));
								objclsPOSBillDtl.setStrItemName(mapTemp1.get("itemName").toString());
								objclsPOSBillDtl.setDblAmount(Double.parseDouble(mapTemp1.get("dblAmount").toString()));
								listOfLiqourBillDetail.add(objclsPOSBillDtl);
							}
						 }
						
						
						List listSubTotalDtl= (List) mapTemp.get("listSubTotal");
						
						if(null!=listSubTotalDtl)
						{	
							for(i=0;i<listSubTotalDtl.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapSubTotal= (Map) listSubTotalDtl.get(i);
								objBillDtl.setDblAmount(Double.parseDouble(mapSubTotal.get("subTotal").toString()));
								listSubTotal.add(objBillDtl);
							}
						}
//						
						
						if (clientCode.equalsIgnoreCase("117.001"))
						{
							posWiseHeading = mapTemp.get("posWiseHeading").toString();
							
						}
						duplicate = mapTemp.get("duplicate").toString();
						posName = mapTemp.get("POS").toString();
						BillType=mapTemp.get("BillType").toString();
						ClientName=mapTemp.get("ClientName").toString();
						ClientAddress1=mapTemp.get("ClientAddress1").toString();
						ClientAddress2=mapTemp.get("ClientAddress2").toString();
						ClientAddress3=mapTemp.get("ClientAddress3").toString(); 
						ClientCity=mapTemp.get("ClientCity").toString();
						TEL_NO=mapTemp.get("TEL NO").toString();
						EMAIL_ID=mapTemp.get("EMAIL ID").toString();
						TAX_INVOICE=mapTemp.get("TAX_INVOICE").toString();
						DATE_TIME = mapTemp.get("DATE_TIME").toString();
						BillNo = mapTemp.get("BillNo").toString();
						
					} 
				}
				
				if(format.equalsIgnoreCase("Jasper1"))
				{	
				if(null!=listMain1)
				{
					for(int i=0;i<listMain1.size();i++)
					{
						 Map mapTemp1 =(Map) listMain1.get(i);
						
						clsPOSBillDtl objclsPOSBillDtl=new clsPOSBillDtl();
						
						objclsPOSBillDtl.setDblQuantity(Double.parseDouble(mapTemp1.get("qty").toString()));
						objclsPOSBillDtl.setStrItemName(mapTemp1.get("itemName").toString());
						objclsPOSBillDtl.setDblAmount(Double.parseDouble(mapTemp1.get("amount").toString()));
						list.add(objclsPOSBillDtl);
					}
				 }
				
				hm.put("posWiseHeading", posWiseHeading);
				hm.put("duplicate", duplicate);
				hm.put("POS", posName);
				hm.put("ClientName", ClientName);
				hm.put("TAX_INVOICE", TAX_INVOICE);
				hm.put("ClientAddress1", ClientAddress1);
				hm.put("ClientAddress2", ClientAddress2);
				hm.put("ClientAddress3", ClientAddress3);
				hm.put("ClientCity", ClientCity);
				hm.put("TEL NO", TEL_NO);
				hm.put("EMAIL ID", EMAIL_ID);
				hm.put("BillNo", BillNo);
				hm.put("DATE_TIME", DATE_TIME);
				hm.put("BillType", BillType);
				hm.put("listOfItemDtl", list);
				hm.put("listOfTaxDtl", listOfTaxDtl);
				hm.put("listOfGrandTotalDtl", listOfGrandTotal);
				hm.put("listOfServiceVatDetail", listOfServiceVatDetail);
				hm.put("listOfFooterDtl", listOfFooterDtl);
				hm.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
				hm.put("listOfDiscountDtl", listOfDiscountDtl);
				hm.put("listOfSettlementDetail", listOfSettlementDetail);
				
				
//	            hm.put("listOfItemDtl", list);
			
	            listData.add(list);
				
				
//			if(format.equalsIgnoreCase("Jasper1"))	
//			{	
	            if (listOfHomeDeliveryDtl.size() > 0)
	            {
	                reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillFormat5JasperReportHD.jrxml");
					imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
	            }
	            else if (result == 1)
	            {
	                reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillFormat5JasperReportNormalBill.jrxml");
					imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
	            }
				else
	            {
	                reportName =servletContext.getRealPath( "/WEB-INF/reports/webpos/rptBillFormat5JasperReportNormalBill.jrxml");
					imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
	            }
	            
              }
			
			}
			else if(kotFor.equalsIgnoreCase("Bill"))
			{
				int result =  (int) mapViewData.get("result"); 
//				long listOfHDSize= (long) jObj.get("listOfHDSize");
				 format =  (String) mapViewData.get("format"); 
				
				
				 listMain1 = (List) mapViewData.get("listOfBillDetail");
				
				
				 
				 List listItemDtl = new ArrayList<>();
				 
//				long lengthListOfHomeDeliveryDtl =  (long) jObj.get("lengthListOfHomeDeliveryDtl"); 
				
				String posWiseHeading="",duplicate="",BillType="",ClientName="",ClientAddress1="",ClientAddress2="",ClientAddress3="",ClientCity="",TEL_NO="";
				String EMAIL_ID="",TAX_INVOICE="",BillNo="",Totl="",name="",address="",mobile_no="",footer="";
				String taxAmount="",taxDesc="",discount="",discText="",discAmt="",reason="",remark="";
				
				clsPOSBillDtl objBillDtl = new clsPOSBillDtl();
				listMain = (List) mapViewData.get("listData");
				List<clsPOSBillDtl> listOfGrandTotal =new ArrayList<>();
				List<clsPOSBillDtl> listOfFoodBillDetail = new ArrayList<>();
				List<clsPOSBillDtl> listOfLiqourBillDetail = new ArrayList<>();
				List<clsPOSBillDtl> listOfHomeDeliveryDtl =new ArrayList<>();
				List<clsPOSBillDtl> listOfServiceVatDetail =new ArrayList<>();
				List<clsPOSBillDtl> listOfFooterDtl =new ArrayList<>();
				List<clsPOSBillDtl> listOfTaxDtl = new ArrayList<>();
				List<clsPOSBillDtl> listOfDiscountDtl = new ArrayList<>();
				List<clsPOSBillDtl> listSubTotal = new ArrayList<>();
				List<clsPOSBillDtl> listOfSettlementDetail = new ArrayList<>();
				
				if(null!=listMain)
				{	
					for(int i=0;i<listMain.size();i++)
					{
						Map mapTemp =(Map) listMain.get(i);
						
						List listTotal = (List) mapTemp.get("listOfGrandTotalDtl");
						
						if(null!=listTotal)
						{	
							for(i=0;i<listTotal.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapjTot= (Map) listTotal.get(i);
								objBillDtl.setDblAmount(Double.parseDouble(mapjTot.get("grandTotal").toString())); 
								
								 listOfGrandTotal.add(objBillDtl);
								
							}
						}	
						
						List listHomeDel = (List) mapTemp.get("listOfHomeDeliveryDtl");
						
						if(null!=listHomeDel)
						{	
							for(i=0;i<listHomeDel.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapHomeDel= (Map) listHomeDel.get(i);
								objBillDtl.setStrItemName("Name:"+mapHomeDel.get("NAME").toString());
								listOfHomeDeliveryDtl.add(objBillDtl);
								
								objBillDtl = new clsPOSBillDtl();
								objBillDtl.setStrItemName("Address:"+mapHomeDel.get("Address").toString());
								listOfHomeDeliveryDtl.add(objBillDtl);
								
								objBillDtl = new clsPOSBillDtl();
								objBillDtl.setStrItemName("Mobile No"+mapHomeDel.get("MOBILE_NO").toString());
								 listOfHomeDeliveryDtl.add(objBillDtl);
								 
							}
						}
						
						List listServiceTaxDtl = (List) mapTemp.get("listOfServiceVatDetail");
						
						if(null!=listServiceTaxDtl)
						{	
							for(i=0;i<listServiceTaxDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapServieTaxDtl= (Map) listServiceTaxDtl.get(i);
								 objBillDtl.setStrItemName("Service Tax No.    :"+mapServieTaxDtl.get("Service Tax No.:").toString());	
								 listOfServiceVatDetail.add(objBillDtl);
							}
						}
						
						List listFooterDtl = (List) mapTemp.get("listOfFooterDtl");
						
						if(null!=listFooterDtl)
						{	
							for(i=0;i<listFooterDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapFooterDtl= (Map) listFooterDtl.get(i);
								objBillDtl.setStrItemName(mapFooterDtl.get("Thank").toString());	
								 listOfFooterDtl.add(objBillDtl);
							}
						}
						List listTaxDtl = (List) mapTemp.get("listOfTaxDtl");
						
						if(null!=listTaxDtl)
						{	
							for(i=0;i<listTaxDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapTax= (Map) listTaxDtl.get(i);
								 objBillDtl=new clsPOSBillDtl();
								
								 objBillDtl.setDblAmount(Double.parseDouble(mapTax.get("taxAmount").toString()));
								 objBillDtl.setStrItemName(mapTax.get("taxDesc").toString());
								
								 listOfTaxDtl.add(objBillDtl);
							}
						}	
						
						List listDiscountDtl= (List) mapTemp.get("listOfDiscountDtl");
						
						if(null!=listDiscountDtl)
						{	
							for(i=0;i<listDiscountDtl.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapDiscount= (Map) listDiscountDtl.get(i);
								objBillDtl.setStrItemName(mapDiscount.get("Discount").toString());
								objBillDtl.setStrItemName(mapDiscount.get("discText").toString());
								objBillDtl.setDblAmount(Double.parseDouble(mapDiscount.get("discAmt").toString()));
								objBillDtl.setStrItemName(mapDiscount.get("Reason").toString());
								objBillDtl.setStrItemName(mapDiscount.get("Remark").toString());
								listOfDiscountDtl.add(objBillDtl);
								
							}
						}
						List listSettlementDtl = (List) mapTemp.get("listOfSettlementDetail");
						
						if(null!=listSettlementDtl)
						{	
							for(i=0;i<listSettlementDtl.size();i++)
							{
								objBillDtl=new clsPOSBillDtl();
								Map mapSettlementDtl= (Map) listSettlementDtl.get(i);
								if((!mapSettlementDtl.get("settleDesc").toString().equals("PAID AMT")) && (!mapSettlementDtl.get("settleDesc").toString().equals("REFUND AMT")))
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("settleAmt").toString()));	 
								}
								else if((mapSettlementDtl.get("settleDesc").toString().equals("PAID AMT")))
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("paidAmt").toString()));
								}
								else
								{
									objBillDtl.setStrItemName(mapSettlementDtl.get("settleDesc").toString());	
									objBillDtl.setDblAmount(Double.parseDouble(mapSettlementDtl.get("refundAmt").toString()));
								}
									
								listOfSettlementDetail.add(objBillDtl);
								
							}
						}
						
						List listLiqur = (List) mapTemp.get("listOfLiqourBillDetail");
						if(null!=listLiqur)
						{
							for( i=0;i<listLiqur.size();i++)
							{
								 Map mapTemp1 =(Map) listLiqur.get(i);
								
								clsPOSBillDtl objclsPOSBillDtl=new clsPOSBillDtl();
								
								objclsPOSBillDtl.setDblQuantity(Double.parseDouble(mapTemp1.get("saleQty").toString()));
								objclsPOSBillDtl.setStrItemName(mapTemp1.get("itemName").toString());
								objclsPOSBillDtl.setDblAmount(Double.parseDouble(mapTemp1.get("dblAmount").toString()));
								listOfLiqourBillDetail.add(objclsPOSBillDtl);
							}
						 }
						
						
						List listSubTotalDtl= (List) mapTemp.get("listSubTotal");
						
						if(null!=listSubTotalDtl)
						{	
							for(i=0;i<listSubTotalDtl.size();i++)
							{
								 objBillDtl = new clsPOSBillDtl();
								Map mapSubTotal= (Map) listSubTotalDtl.get(i);
								objBillDtl.setDblAmount(Double.parseDouble(mapSubTotal.get("subTotal").toString()));
								listSubTotal.add(objBillDtl);
							}
						}
//						
						
						if (clientCode.equalsIgnoreCase("117.001"))
						{
							posWiseHeading = mapTemp.get("posWiseHeading").toString();
							
						}
						duplicate = mapTemp.get("duplicate").toString();
						posName = mapTemp.get("POS").toString();
						BillType=mapTemp.get("BillType").toString();
						ClientName=mapTemp.get("ClientName").toString();
						ClientAddress1=mapTemp.get("ClientAddress1").toString();
						ClientAddress2=mapTemp.get("ClientAddress2").toString();
						ClientAddress3=mapTemp.get("ClientAddress3").toString(); 
						ClientCity=mapTemp.get("ClientCity").toString();
						TEL_NO=mapTemp.get("TEL NO").toString();
						EMAIL_ID=mapTemp.get("EMAIL ID").toString();
						TAX_INVOICE=mapTemp.get("TAX_INVOICE").toString();
						DATE_TIME = mapTemp.get("DATE_TIME").toString();
						BillNo = mapTemp.get("BillNo").toString();
						
					} 
				}
				
				if(format.equalsIgnoreCase("Jasper1"))
				{	
					if(null!=listMain1)
					{
						for(int i=0;i<listMain1.size();i++)
						{
							 Map mapTemp1 =(Map) listMain1.get(i);
							
							clsPOSBillDtl objclsPOSBillDtl=new clsPOSBillDtl();
							
							objclsPOSBillDtl.setDblQuantity(Double.parseDouble(mapTemp1.get("qty").toString()));
							objclsPOSBillDtl.setStrItemName(mapTemp1.get("itemName").toString());
							objclsPOSBillDtl.setDblAmount(Double.parseDouble(mapTemp1.get("amount").toString()));
							list.add(objclsPOSBillDtl);
						}
					 }
				
					hm.put("posWiseHeading", posWiseHeading);
					hm.put("duplicate", duplicate);
					hm.put("POS", posName);
					hm.put("ClientName", ClientName);
					hm.put("TAX_INVOICE", TAX_INVOICE);
					hm.put("ClientAddress1", ClientAddress1);
					hm.put("ClientAddress2", ClientAddress2);
					hm.put("ClientAddress3", ClientAddress3);
					hm.put("ClientCity", ClientCity);
					hm.put("TEL NO", TEL_NO);
					hm.put("EMAIL ID", EMAIL_ID);
					hm.put("BillNo", BillNo);
					hm.put("DATE_TIME", DATE_TIME);
					hm.put("BillType", BillType);
					hm.put("listOfItemDtl", list);
					hm.put("listOfTaxDtl", listOfTaxDtl);
					hm.put("listOfGrandTotalDtl", listOfGrandTotal);
					hm.put("listOfServiceVatDetail", listOfServiceVatDetail);
					hm.put("listOfFooterDtl", listOfFooterDtl);
					hm.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
					hm.put("listOfDiscountDtl", listOfDiscountDtl);
					hm.put("listOfSettlementDetail", listOfSettlementDetail);
					
					
	//	            hm.put("listOfItemDtl", list);
				
		            listData.add(list);
	            
		            if (listOfHomeDeliveryDtl.size() > 0)
		            {
		                reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillFormat5JasperReportHD.jrxml");
						imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
		            }
		            else if (result == 1)
		            {
		                reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillFormat5JasperReportNormalBill.jrxml");
						imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
		            }
					else
		            {
		                reportName =servletContext.getRealPath( "/WEB-INF/reports/webpos/rptBillFormat5JasperReportNormalBill.jrxml");
						imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
		            }
				}
				
			}
			else
			{
				listMain = (List) mapViewData.get("jArr");
				
				String reportHeading="",duplicate="",discount="",posCode1="",POSName="",POSDt="",totalSales="",floatval="";
				String cash="",advance="",transferIn="",totalReceipt="",payment="",withdrawal="",transferOut="";
				String totalPayments="",cashInHand="",homeDelivery="",dining="",takeAway="",noOfBills="";
				String noOfVoidedBills="",noOfModifiedBills="",refund="",noOfPax="",noOfTakeAway="";
				String noOfHomeDel="",dayEndBy="",noOfNcKot="",noOfComplimentaryBills="",noOfVoidKot="",Total="";
				String groupName="",settlementDesc="";
				double taxableAmount=0.00,dblNetTotalPlusTax=0.00;
				double settlementAmt=0.00;
				
				List listGroupAmtWithTaxDtl =new ArrayList<>();
				List listSettelementBrkUP = new ArrayList<>();
				List listSettelementTaxDtl = new ArrayList<>();
				
				if(null!=listMain)
				{	
					for(int i=0;i<listMain.size();i++)
					{
						Map mapTemp =(Map) listMain.get(i);
						
						List listGroupAmtTax = (List) mapTemp.get("listGroupAmtWithTaxDtl");
						
						if(null!=listGroupAmtTax)
						{	
							for(i=0;i<listGroupAmtTax.size();i++)
							{
								Map mapTot= (Map) listGroupAmtTax.get(i);
								clsPOSReprintDocumentsBean objGroupDtl = new clsPOSReprintDocumentsBean();
								objGroupDtl.setStrgroupName(mapTot.get("groupName").toString());
								objGroupDtl.setDblNetTotalPlusTax(Double.parseDouble(mapTot.get("dblNetTotalPlusTax").toString()));
								
								listGroupAmtWithTaxDtl.add(objGroupDtl);
								
							}
						}	
						
						List listSettlementBrkUp = (List) mapTemp.get("listSettelementBrkUP");
						
						if(null!=listSettlementBrkUp)
						{	
							for(i=0;i<listSettlementBrkUp.size();i++)
							{
								Map mapSettleBrkUp= (Map) listSettlementBrkUp.get(i);
								clsPOSReprintDocumentsBean objBillSettleBrkUp = new clsPOSReprintDocumentsBean();
								objBillSettleBrkUp.setStrSettleDesc(mapSettleBrkUp.get("settlementDesc").toString());
								objBillSettleBrkUp.setDblSettleAmt(Double.parseDouble(mapSettleBrkUp.get("settlementAmt").toString()));
			                   
								listSettelementBrkUP.add(objBillSettleBrkUp);
								 
							}
						}
						
						List listSettleTaxDtl = (List) mapTemp.get("listSettelementTaxDtl");
						
						if(null!=listSettleTaxDtl)
						{	
							for(i=0;i<listSettleTaxDtl.size();i++)
							{
								Map mapSettleTaxDtl= (Map) listSettleTaxDtl.get(i);
								clsPOSReprintDocumentsBean objSettleTaxDtl = new clsPOSReprintDocumentsBean();
								objSettleTaxDtl.setStrTaxDesc(mapSettleTaxDtl.get("taxDesc").toString());
								objSettleTaxDtl.setDblTaxableAmount(Double.parseDouble(mapSettleTaxDtl.get("taxableAmount").toString()));
								objSettleTaxDtl.setDblTaxAmount(Double.parseDouble(mapSettleTaxDtl.get("taxAmount").toString()));
								
								listSettelementTaxDtl.add(objSettleTaxDtl);
							}
						}
						
						
						
						reportHeading = mapTemp.get("reportHeading").toString();
						duplicate = mapTemp.get("duplicate").toString();
						posCode = mapTemp.get("posCode1").toString();
						posName = mapTemp.get("posName").toString();
						posDate = mapTemp.get("posDate").toString();
						totalSales=mapTemp.get("totalSales").toString();
						floatval=mapTemp.get("floatval").toString();
						cash=mapTemp.get("cash").toString();
						advance=mapTemp.get("advance").toString();
						transferIn=mapTemp.get("transferIn").toString(); 
						totalReceipt=mapTemp.get("totalReceipt").toString();
						payment=mapTemp.get("payment").toString();
						withdrawal=mapTemp.get("withdrawal").toString();
						transferOut=mapTemp.get("transferOut").toString();
						totalPayments = mapTemp.get("totalPayments").toString();
						cashInHand = mapTemp.get("cashInHand").toString();
						homeDelivery = mapTemp.get("homeDelivery").toString();
						dining = mapTemp.get("dining").toString();
						takeAway = mapTemp.get("takeAway").toString();
						noOfBills = mapTemp.get("noOfBills").toString();
						noOfVoidedBills = mapTemp.get("noOfVoidedBills").toString();
						noOfModifiedBills = mapTemp.get("noOfModifiedBills").toString();
						refund = mapTemp.get("refund").toString();
						discount = mapTemp.get("discount").toString();
						noOfPax = mapTemp.get("noOfPax").toString();
						noOfTakeAway = mapTemp.get("noOfTakeAway").toString();
						noOfHomeDel = mapTemp.get("noOfHomeDel").toString();
						dayEndBy = mapTemp.get("dayEndBy").toString();
						noOfNcKot = mapTemp.get("noOfNcKot").toString();
						noOfComplimentaryBills = mapTemp.get("noOfComplimentaryBills").toString();
						noOfVoidKot = mapTemp.get("noOfVoidKot").toString();
						Total = mapTemp.get("Total").toString();
						
					} 
				}
				
				
				
				hm.put("reportHeading", reportHeading);
				hm.put("duplicate", duplicate);
				hm.put("posName", posName);
				hm.put("posCode", posCode);
				hm.put("posDate", posDate);
				hm.put("totalSales", totalSales);
				hm.put("floatval", floatval);
				hm.put("cash", cash);
				hm.put("advance", advance);
				hm.put("transferIn", transferIn);
				hm.put("totalReceipt", totalReceipt);
				hm.put("payment", payment);
				hm.put("withdrawal", withdrawal);
				hm.put("transferOut", transferOut);
				hm.put("totalPayments", totalPayments);
				hm.put("cashInHand", cashInHand);
				hm.put("homeDelivery", homeDelivery);
				hm.put("dining", dining);
				hm.put("takeAway", takeAway);
				hm.put("noOfBills", noOfBills);
				hm.put("noOfVoidedBills", noOfVoidedBills);
				hm.put("noOfModifiedBills", noOfModifiedBills);
				hm.put("refund", refund);
			    hm.put("discount", discount);
			    hm.put("noOfPax", noOfPax);
			    hm.put("noOfTakeAway", noOfTakeAway);
			    hm.put("noOfHomeDel", noOfHomeDel);
			    hm.put("dayEndBy", dayEndBy);
			    hm.put("noOfNcKot", noOfNcKot);
			    hm.put("noOfComplimentaryBills", noOfComplimentaryBills);
			    hm.put("noOfVoidKot", noOfVoidKot);
			    hm.put("Total", Total);
			    hm.put("listGroupAmtWithTaxDtl", listGroupAmtWithTaxDtl);
			    hm.put("listSettelementTaxDtl", listSettelementTaxDtl);
			    hm.put("listSettelementBrkUP", listSettelementBrkUP);
			    
			    listData.add(listGroupAmtWithTaxDtl);
			    
			    reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptDayEndReprintDocsJasperReport.jrxml");
				imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
			}
			
			   List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
	
	            
			   JasperDesign jd = JRXmlLoader.load(reportName);
	    	   JasperReport jr = JasperCompileManager.compileReport(jd);
	    	   
	            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listData,false);
	            JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
	            jprintlist.add(print);
					
	            
	            if (jprintlist.size()>0)
			    {
			    	ServletOutputStream servletOutputStream = resp.getOutputStream();
			    	if(strDocType.equals("PDF"))
			    	{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=GenrateKOTJasperReport_"+webStockUserCode+".pdf");
				    exporter.exportReport();
					servletOutputStream.flush();
					servletOutputStream.close();
			    	}else
			    	{
			    		JRExporter exporter = new JRXlsExporter();
			    		resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=GenrateKOTJasperReport_"+webStockUserCode+".xls");
					    exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
			    	}
			    }else
			    {
			    	 resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			    	resp.getWriter().append("No Record Found");
			 		
			    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	            
		
		System.out.println("Hi");
			
	}
}
