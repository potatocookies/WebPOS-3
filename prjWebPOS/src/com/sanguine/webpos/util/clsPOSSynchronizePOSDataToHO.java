/**
 * 
 */
package com.sanguine.webpos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.base.service.clsBaseServiceImpl;

/**
 * @author Vinayak
 *
Apr 14, 2017  11:20:04 AM
 */
@Controller
@Transactional(value = "webPOSTransactionManager")
public class clsPOSSynchronizePOSDataToHO {
	 @Autowired
	 private clsPOSUtilityController objUtilityController;
	
	 @Autowired
	 private clsBaseServiceImpl objBaseServiceImpl;
	 
	 @Autowired
	 private  clsPOSSetupUtility objPOSSetupUtility;
	
	public String gConnectionActive;
    private String fromDate="", toDate="",gSanguineWebServiceURL="";

    public int funPostSaleDataToHO(String formName,String clientCode,String POSCode)
    {
        try
        {
        	gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gSanguineWebServiceURL");
            
            funPostBillHdData(formName);
            funPostBillDtlData(formName);
            funPostBillSettlementDtlData(formName);
            funPostBillModifierDtlData(formName);
            funPostBillTaxDtlData(formName);
            funPostBillPromotionDtlData(formName);
            funPostBillDiscountDtlData(formName);
            funPostBillCRMPointsData(formName);
            funPostBillComplimentryDtlData(formName);
            funPostBillSeriesDtlData(formName,clientCode,POSCode);
            funPostAdvOrderHdData(formName,clientCode,POSCode);
            funPostAdvOrderDtlData(formName,clientCode,POSCode);
            funPostAdvOrderCharDtlData(formName,clientCode,POSCode);
            funPostAdvOrderModifierDtlData(formName,clientCode,POSCode);
            funPostAdvReceiptHdData(formName,clientCode,POSCode);
            funPostAdvReceiptDtlData(formName,clientCode,POSCode);
            funPostHomeDeliveryData(formName,clientCode,POSCode);
            funPostHomeDeliveryDtlData(formName,clientCode,POSCode);
            //funPostPlaceOrderHdData(formName);
            //funPostPlaceOrderDtlData(formName);
            //funPostPlaceAdvanceOrderDtlData(formName);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection refused: connect"))
            {
                gConnectionActive = "N";
                //JOptionPane.showMessageDialog(null, "Connection is lost to HO please check!!!");
            }
            else
            {
              //  JOptionPane.showMessageDialog(null, "Error while posting data to HO!!!");
            }
            e.printStackTrace();
        }
        return 1;
    }
    
    
    private int funPostBillHdData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "", queryForCount = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbillhd where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl) limit 2000";

            queryForCount = "select count(strBillNo) from tblbillhd where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl) limit 2000";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbillhd where strDataPostFlag='N' limit 2000";

            queryForCount = "select count(strBillNo) from tblqbillhd where strDataPostFlag='N' limit 2000";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select * from tblbillhd "
                    + "where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and strDataPostFlag='N' limit 2000";

            queryForCount = "select count(strBillNo) from tblbillhd "
                    + "where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and strDataPostFlag='N' limit 2000";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select * from tblqbillhd "
                    + "where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and strDataPostFlag='N' limit 2000";

            queryForCount = "select count(strBillNo) from tblqbillhd "
                    + "where date(dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and strDataPostFlag='N' limit 2000";
        }

        int count = 0;
        
        List listCount=objBaseServiceImpl.funGetList(new StringBuilder(queryForCount), "sql");
        
        if(listCount.size()>0)
        {
            count = Integer.parseInt(((Object)listCount.get(0)).toString());
        }
       
        if (count > 2000)
        {
            count = count / 2000;
            count = count + 1;
        }
        else
        {
            count = 1;
        }
        System.out.println("Bill Hd Count=" + count);

        boolean flgDataPosting = false;
        Query qPostBill;
        List listPostBill;
        for (int cnt = 0; cnt < count; cnt++)
        {
        	listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        	
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();
            updateBills = "";
            if(listPostBill.size()>0)
            {
            	for(int i=0;i<listPostBill.size();i++)
		            {
            			Object ob[]=(Object[])listPostBill.get(i);
		                JSONObject objRows = new JSONObject();
		                
		                updateBills += ",'" +ob[0].toString() + "'";
		
		                objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		                objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
		                objRows.put("BillDate",ob[2].toString());// rs.getString("dteBillDate"));
		                objRows.put("POSCode",ob[3].toString());// rs.getString("strPOSCode"));
		                objRows.put("SettelmentMode",ob[4].toString());// rs.getString("strSettelmentMode"));
		                objRows.put("DiscountAmt",ob[5].toString());// rs.getString("dblDiscountAmt"));
		                objRows.put("DiscountPer",ob[6].toString());// rs.getString("dblDiscountPer"));
		                objRows.put("TaxAmt",ob[7].toString());// rs.getString("dblTaxAmt"));
		                objRows.put("SubTotal",ob[8].toString());// rs.getString("dblSubTotal"));
		                objRows.put("GrandTotal",ob[9].toString());// rs.getString("dblGrandTotal"));
		                objRows.put("TakeAway",ob[10].toString());// rs.getString("strTakeAway"));
		
		                objRows.put("OperationType",ob[11].toString());// rs.getString("strOperationType"));
		                objRows.put("UserCreated",ob[12].toString());// rs.getString("strUserCreated"));
		                objRows.put("UserEdited",ob[13].toString());// rs.getString("strUserEdited"));
		                objRows.put("DateCreated",ob[14].toString());// rs.getString("dteDateCreated"));
		                objRows.put("DateEdited",ob[15].toString());// rs.getString("dteDateEdited"));
		                objRows.put("ClientCode",ob[16].toString());// rs.getString("strClientCode"));
		
		                objRows.put("TableNo",ob[17].toString());// rs.getString("strTableNo"));
		                objRows.put("WaiterNo",ob[18].toString());// rs.getString("strWaiterNo"));
		                objRows.put("CustomerCode",ob[19].toString());// rs.getString("strCustomerCode"));
		                objRows.put("ManualBillNo",ob[20].toString());// rs.getString("strManualBillNo"));
		                objRows.put("ShiftCode",ob[21].toString());// rs.getString("intShiftCode"));
		                objRows.put("PaxNo",ob[22].toString());// rs.getString("intPaxNo"));
		
		                objRows.put("DataPostFlag",ob[23].toString());// rs.getString("strDataPostFlag"));
		                objRows.put("ReasonCode",ob[24].toString());// rs.getString("strReasonCode"));
		                objRows.put("Remarks",ob[25].toString());// rs.getString("strRemarks"));
		                objRows.put("TipAmount",ob[26].toString());// rs.getString("dblTipAmount"));
		                objRows.put("SettleDate",ob[27].toString());// rs.getString("dteSettleDate"));
		                objRows.put("CounterCode",ob[28].toString());// rs.getString("strCounterCode"));
		                objRows.put("DeliveryCharges",ob[29].toString());// rs.getString("dblDeliveryCharges"));
		                objRows.put("CouponCode",ob[30].toString());// rs.getString("strCouponCode"));
		                objRows.put("AreaCode",ob[31].toString());// rs.getString("strAreaCode"));
		                objRows.put("DiscountRemark",ob[32].toString());// rs.getString("strDiscountRemark"));
		                objRows.put("TakeAwayRemark",ob[33].toString());// rs.getString("strTakeAwayRemarks"));
		                objRows.put("DiscountOn",ob[34].toString());// rs.getString("strDiscountOn"));
		                objRows.put("CardNo",ob[35].toString());// rs.getString("strCardNo"));
		                objRows.put("TransType",ob[36].toString());// rs.getString("strTransactionType"));
		
		                arrObj.add(objRows);
		            }
        	}
         
            objJson.put("BillHdInfo", arrObj);

            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillHd=" + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblbillhd set strDataPostFlag='Y'"
                    		+ " where strBillNo in (" + updateBills + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbillhd set strDataPostFlag='Y'"
                    		+ " where strBillNo in (" + updateBills + ")","sql");
                }
                flgDataPosting = true;
            }
            else
            {
                flgDataPosting = false;
            }
        }
        if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
        {
            if (flgDataPosting)
            {
                JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
            }
        }

        return 1;
    }
   
    private int funPostBillDtlData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "", queryForCount = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbilldtl where strDataPostFlag='N' "
                    + "and strBillNo IN (select strBillNo from tblbillsettlementdtl) limit 2000 ";

            queryForCount = "select count(strBillNo) from tblbilldtl where strDataPostFlag='N' "
                    + "and strBillNo IN (select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbilldtl where strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(strBillNo) from tblqbilldtl where strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbilldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblbillhd a,tblbilldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbilldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblqbillhd a,tblqbilldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        //System.out.println(query);
        int count = 0;
        List listCount=objBaseServiceImpl.funGetList(new StringBuilder(queryForCount), "sql");
      
        if(listCount.size()>0)
        {
            count = Integer.parseInt(((Object)listCount.get(0)).toString());
        }
        if (count > 2000)
        {
            count = count / 2000;
            count = count + 1;
        }
        else
        {
            count = 1;
        }
        System.out.println("Bill Dtl Count=" + count);

        boolean flgDataPosting = false;

        int rowCount = 0;
        Query qPostBill;
        List listPostBill;
       
        for (int cnt = 0; cnt < count; cnt++)
        {
            listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        	JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();
            updateBills = "";
            String updateItemCodes = "";
            String updateKOTNos = "";
            if(listPostBill.size()>0)
            {
            	for(int i=0;i<listPostBill.size();i++)
		            {
            			Object ob[]=(Object[])listPostBill.get(i);
		                JSONObject objRows = new JSONObject();
		                updateBills += ",'" + ob[2].toString() + "'";
		                updateItemCodes += ",'" + ob[0].toString() + "'";
		                updateKOTNos += ",'" + ob[9].toString() + "'";
		
		                objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));
		                objRows.put("ItemName",ob[1].toString());// rs.getString("strItemName"));
		                objRows.put("BillNo", ob[2].toString());//rs.getString("strBillNo"));
		                objRows.put("AdvBookingNo",ob[3].toString());// rs.getString("strAdvBookingNo"));
		                objRows.put("Rate",ob[4].toString());// rs.getString("dblRate"));
		                objRows.put("Quantity",ob[5].toString());// rs.getString("dblQuantity"));
		                objRows.put("Amount",ob[6].toString());// rs.getString("dblAmount"));
		                objRows.put("TaxAmount",ob[7].toString());// rs.getString("dblTaxAmount"));
		                objRows.put("BillDate",ob[8].toString());// rs.getString("dteBillDate"));
		                objRows.put("KOTNo",ob[9].toString());// rs.getString("strKOTNo")); 9
		                objRows.put("ClientCode",ob[10].toString());// rs.getString("strClientCode"));
		                objRows.put("CustomerCode",ob[11].toString());// rs.getString("strCustomerCode"));
		                objRows.put("OrderProcessing",ob[12].toString());// rs.getString("tmeOrderProcessing"));
		                objRows.put("DataPostFlag",ob[13].toString());// rs.getString("strDataPostFlag"));
		                objRows.put("MMSDataPostFlag",ob[14].toString());// rs.getString("strMMSDataPostFlag"));
		                objRows.put("ManualKOTNo",ob[15].toString());// rs.getString("strManualKOTNo"));
		                objRows.put("tdhYN",ob[16].toString());// rs.getString("tdhYN"));
		                objRows.put("PromoCode",ob[17].toString());// rs.getString("strPromoCode"));
		                objRows.put("CounterCode",ob[18].toString());// rs.getString("strCounterCode"));
		                objRows.put("WaiterNo",ob[19].toString());// rs.getString("strWaiterNo"));
		                objRows.put("DiscountAmt",ob[20].toString());// rs.getString("dblDiscountAmt"));
		                objRows.put("DiscountPer",ob[21].toString());// rs.getString("dblDiscountPer"));
		
		                //System.out.println("qBill=" + rs.getString("strBillNo"));
		                arrObj.add(objRows);
		
		                rowCount++;
		            }
        		}

            objJson.put("BillDtlInfo", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillDtl= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                StringBuilder sbUpdateItemCode = new StringBuilder(updateItemCodes);
                StringBuilder sbUpdateKOTNos = new StringBuilder(updateKOTNos);

                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    updateItemCodes = sbUpdateItemCode.delete(0, 1).toString();
                    updateKOTNos = sbUpdateKOTNos.delete(0, 1).toString();

                    //System.out.println("Billsss="+updateBills);
                    //System.out.println("update tblbilldtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ") and strItemCode in ("+updateItemCodes+")");
                    //System.out.println("update tblqbilldtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ") and strItemCode in ("+updateItemCodes+")");
                    String q = "update tblqbilldtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strItemCode in (" + updateItemCodes + ") "
                            + " and strKOTNo in (" + updateKOTNos + ") ";
                    objBaseServiceImpl.funExecuteUpdate(q,"sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblbilldtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strItemCode in (" + updateItemCodes + ") "
                            + " and strKOTNo in (" + updateKOTNos + ") limit 2000","sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbilldtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strItemCode in (" + updateItemCodes + ") "
                            + " and strKOTNo in (" + updateKOTNos + ") limit 2000","sql");
                    
                }
                flgDataPosting = true;
            }
            else
            {
                flgDataPosting = false;
            }

            System.out.println("Row Count= " + rowCount);
        }

        if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
        {
            if (flgDataPosting)
            {
                JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
            }
        }

        return 1;
    }

    private int funPostBillModifierDtlData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbillmodifierdtl where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbillmodifierdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbillmodifierdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbillmodifierdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        List listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
     
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        	if(listPostBill.size()>0)
        	{
        	for(int i=0;i<listPostBill.size();i++)
		        {
        			Object ob[]=(Object[])listPostBill.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString() + "'";
		            objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
		            objRows.put("ModifierCode",ob[2].toString());// rs.getString("strModifierCode"));
		            objRows.put("ModifierName",ob[3].toString());// rs.getString("strModifierName"));
		            objRows.put("Rate",ob[4].toString());// rs.getString("dblRate"));
		            objRows.put("Quantity",ob[5].toString());// rs.getString("dblQuantity"));
		            objRows.put("Amount",ob[6].toString());// rs.getString("dblAmount"));
		            objRows.put("ClientCode",ob[7].toString());// rs.getString("strClientCode"));
		            objRows.put("CustomerCode",ob[8].toString());// rs.getString("strCustomerCode"));
		            objRows.put("DataPostFlag",ob[9].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("MMSDataPostFlag",ob[10].toString());// rs.getString("strMMSDataPostFlag"));
		            objRows.put("DefaultModifierDeselectedYN",ob[11].toString());// rs.getString("strDefaultModifierDeselectedYN"));
		            objRows.put("strSequenceNo",ob[12].toString());// rs.getString("strSequenceNo"));
		            objRows.put("dblDiscPer",ob[13].toString());// rs.getString("dblDiscPer"));
		            objRows.put("dblDiscAmt",ob[14].toString());// rs.getString("dblDiscAmt"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    		}

        if (flgDataForPosting)
        {
            objJson.put("BillModifierDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillMod= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblbillmodifierdtl set strDataPostFlag='Y' "
                    		+ "where strBillNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqbillmodifierdtl set strDataPostFlag='Y'"
                    		+ " where strBillNo in (" + updateBills + ")", "sql");
                
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostBillSettlementDtlData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "", queryForCount = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbillsettlementdtl where strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(strBillNo) from tblbillsettlementdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbillsettlementdtl where strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(strBillNo) from tblqbillsettlementdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbillsettlementdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblbillhd a,tblbillsettlementdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbillsettlementdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblqbillhd a,tblqbillsettlementdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }

        int count = 0;
        List listCount=objBaseServiceImpl.funGetList(new StringBuilder(queryForCount),"sql");
      
        if(listCount.size()>0)
        {
            count = Integer.parseInt(((Object)listCount.get(0)).toString());
        }
       
        if (count > 2000)
        {
            count = count / 2000;
            count = count + 1;
        }
        else
        {
            count = 1;
        }
        System.out.println("Bill Sett Dtl Count=" + count);

        boolean flgDataPosting = false;
        Query qPostBill;
        List listPostBill;
        for (int cnt = 0; cnt < count; cnt++)
        {
        	listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query),"sql");
         
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();

            updateBills = "";
            String updateSettlementCodes = "";
            if(listPostBill.size()>0)
            {
            	for(int i=0;i<listPostBill.size();i++)
		            {
            			Object ob[]=(Object[])listPostBill.get(i);
				        JSONObject objRows = new JSONObject();
		                updateBills += ",'" + ob[0].toString() + "'";
		                updateSettlementCodes += ",'" + ob[1].toString() + "'";
		                
		                objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		                objRows.put("SettlementCode",ob[1].toString());// rs.getString("strSettlementCode"));
		                objRows.put("SettlementAmt",ob[2].toString());// rs.getString("dblSettlementAmt"));
		                objRows.put("PaidAmt",ob[3].toString());// rs.getString("dblPaidAmt"));
		                objRows.put("ExpiryDate",ob[4].toString());// rs.getString("strExpiryDate"));
		                objRows.put("CardName",ob[5].toString());// rs.getString("strCardName"));
		                objRows.put("Remark",ob[6].toString());// rs.getString("strRemark"));
		                objRows.put("ClientCode",ob[7].toString());// rs.getString("strClientCode"));
		                objRows.put("CustomerCode",ob[8].toString());// rs.getString("strCustomerCode"));
		                objRows.put("ActualAmt",ob[9].toString());// rs.getString("dblActualAmt"));
		                objRows.put("RefundAmt",ob[10].toString());// rs.getString("dblRefundAmt"));
		                objRows.put("GiftVoucherCode",ob[11].toString());// rs.getString("strGiftVoucherCode"));
		                objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		                objRows.put("FolioNo",ob[13].toString());// rs.getString("strFolioNo"));
		                objRows.put("RoomNo",ob[14].toString());// rs.getString("strRoomNo"));
		
		                arrObj.add(objRows);
		            }
            }
            

            objJson.put("BillSettlementDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillSettle= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                StringBuilder sbUpdateSettlements = new StringBuilder(updateSettlementCodes);

                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    updateSettlementCodes = sbUpdateSettlements.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);

                    objBaseServiceImpl.funExecuteUpdate("update tblbillsettlementdtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strSettlementCode in (" + updateSettlementCodes + ") ","sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbillsettlementdtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strSettlementCode in (" + updateSettlementCodes + ") ","sql");
                    
                }
                flgDataPosting = true;
            }
            else
            {
                flgDataPosting = true;
            }
        }

        if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
        {
            if (flgDataPosting)
            {
                JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
            }
        }

        return 1;
    }

    private int funPostBillTaxDtlData(String formName) throws Exception
    {
        String updateBills = "", updateTaxCodes = "";
        String query = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbilltaxdtl where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbilltaxdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbilltaxdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbilltaxdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        List listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listPostBill.size()>0)
    	{
        	for(int i=0;i<listPostBill.size();i++)
	        {
    			Object ob[]=(Object[])listPostBill.get(i);
	            JSONObject objRows = new JSONObject();
	        
	            updateBills += ",'" + ob[0].toString() + "'";
	            updateTaxCodes += ",'" +  ob[1].toString() + "'";
	
	            objRows.put("BillNo",  ob[0].toString());//rs.getString("strBillNo"));
	            objRows.put("TaxCode", ob[1].toString());//rs.getString("strTaxCode"));
	            objRows.put("TaxableAmount",ob[2].toString());// rs.getString("dblTaxableAmount"));
	            objRows.put("TaxAmount", ob[3].toString());//rs.getString("dblTaxAmount"));
	            objRows.put("ClientCode",ob[4].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[5].toString());// rs.getString("strDataPostFlag"));
	            arrObj.add(objRows);
	            flgDataForPosting = true;
	        	}
    		}
        if (flgDataForPosting)
        {
            objJson.put("BillTaxDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillTax= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                StringBuilder sbUpdateTax = new StringBuilder(updateTaxCodes);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    updateTaxCodes = sbUpdateTax.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblbilltaxdtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strTaxCode in (" + updateTaxCodes + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbilltaxdtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strTaxCode in (" + updateTaxCodes + ")", "sql");
                    
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostBillDiscountDtlData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "", queryForCount = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbilldiscdtl where strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(strBillNo) from tblbilldiscdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbilldiscdtl where strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(strBillNo) from tblqbilldiscdtl where strDataPostFlag='N' ";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbilldiscdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblbillhd a,tblbilldiscdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbilldiscdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N' limit 2000 ";

            queryForCount = "select count(b.strBillNo) from tblqbillhd a,tblqbilldiscdtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }

        int count = 0;
        List listCount=objBaseServiceImpl.funGetList(new StringBuilder(queryForCount), "sql");
      
        if(listCount.size()>0)
        {
            count = Integer.parseInt(((Object)listCount.get(0)).toString());
        }
       if (count > 2000)
        {
            count = count / 2000;
            count = count + 1;
        }
        else
        {
            count = 1;
        }
        System.out.println("Bill Disc Dtl Count=" + count);
        boolean flgDataPosting = false;
        Query qPostBill;
        List listPostBill;
        for (int cnt = 0; cnt < count; cnt++)
        {
        	listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();

            updateBills = "";
            if(listPostBill.size()>0)
            {
            	for(int i=0;i<listPostBill.size();i++)
		           {
            			Object ob[]=(Object[])listPostBill.get(i);
		                JSONObject objRows = new JSONObject();
		                updateBills += ",'" + ob[0].toString() + "'";
		                objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		                objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
		                objRows.put("DiscAmt",ob[2].toString());// rs.getString("dblDiscAmt"));
		                objRows.put("DiscPer",ob[3].toString());// rs.getString("dblDiscPer"));
		                objRows.put("DiscOnAmt",ob[4].toString());// rs.getString("dblDiscOnAmt"));
		                objRows.put("DiscOnType",ob[5].toString());// rs.getString("strDiscOnType"));
		                objRows.put("DiscOnValue",ob[6].toString());// rs.getString("strDiscOnValue"));
		                objRows.put("DiscOnReasonCode",ob[7].toString());// rs.getString("strDiscReasonCode"));
		                objRows.put("DiscRemarks",ob[8].toString());// rs.getString("strDiscRemarks"));
		                objRows.put("UserCreated",ob[9].toString());// rs.getString("strUserCreated"));
		                objRows.put("UserEdited",ob[10].toString());// rs.getString("strUserEdited"));
		                objRows.put("DateCreated",ob[11].toString());// rs.getString("dteDateCreated"));
		                objRows.put("DateEdited",ob[12].toString());// rs.getString("dteDateEdited"));
		                objRows.put("ClientCode",ob[13].toString());// rs.getString("strClientCode"));
		
		                arrObj.add(objRows);
		            }
            }

            objJson.put("BillDiscountDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillDisc= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblbilldiscdtl set strDataPostFlag='Y'"
                    		+ " where strBillNo in (" + updateBills + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbilldiscdtl set strDataPostFlag='Y' "
                    		+ "where strBillNo in (" + updateBills + ")", "sql");
                    
                    objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
                }
                flgDataPosting = true;
            }
            else
            {
                flgDataPosting = true;
            }
        }

        if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
        {
            if (flgDataPosting)
            {
                //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
            }
        }

        return 1;
    }
   
    private int funPostBillCRMPointsData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblcrmpoints "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblcrmpoints "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblqbillsettlementdtl)";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblcrmpoints b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblcrmpoints b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
       
        List listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listPostBill.size()>0)
    	{
        	for(int i=0;i<listPostBill.size();i++)
			  {
		   			Object ob[]=(Object[])listPostBill.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString() + "'";
		            objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		            objRows.put("CRMPoints",ob[1].toString());// rs.getString("dblPoints"));
		            objRows.put("TransactionId",ob[2].toString());// rs.getString("strTransactionId"));
		            objRows.put("OutletUID",ob[3].toString());// rs.getString("strOutletUID"));
		            objRows.put("RedeemedAmt",ob[4].toString());// rs.getString("dblRedeemedAmt"));
		            objRows.put("CustomerMobileNo",ob[5].toString());// rs.getString("longCustMobileNo"));
		            objRows.put("ClientCode",ob[6].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[7].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("Value",ob[8].toString());// rs.getString("dblValue"));
		            objRows.put("CustomerId",ob[9].toString());// rs.getString("strCustomerId"));
		            objRows.put("BillDate",ob[10].toString());// rs.getString("dteBillDate"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		       }
    	}

        if (flgDataForPosting)
        {
            objJson.put("CRMBillPoints", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillCRM= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblcrmpoints set strDataPostFlag='Y' "
                    		+ "where strBillNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                 //   JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }
    
    private int funPostBillPromotionDtlData(String formName) throws Exception
    {
        String updateBills = "", updateItemCodes = "", updatePromoCodes = "";
        String query = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbillpromotiondtl "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbillpromotiondtl "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblqbillsettlementdtl)";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbillpromotiondtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbillpromotiondtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        System.out.println(query);
        List listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
          
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listPostBill.size()>0)
    	{
        	for(int i=0;i<listPostBill.size();i++)
			{
	   			Object ob[]=(Object[])listPostBill.get(i);
	            JSONObject objRows = new JSONObject();
	            updateBills += ",'" + ob[0].toString() + "'";
	            updateItemCodes += ",'" + ob[1].toString() + "'";
	            updatePromoCodes += ",'" + ob[2].toString() + "'";
	
	            objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
	            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
	            objRows.put("PromotionCode",ob[2].toString());// rs.getString("strPromotionCode"));
	            objRows.put("Quantity",ob[3].toString());// rs.getString("dblQuantity"));
	            objRows.put("Rate",ob[4].toString());// rs.getString("dblRate"));
	            objRows.put("ClientCode",ob[5].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[6].toString());// rs.getString("strDataPostFlag"));
	            objRows.put("PromoType",ob[7].toString());// rs.getString("strPromoType"));
	            objRows.put("Amount",ob[8].toString());// rs.getString("dblAmount"));
	            objRows.put("DiscPer",ob[9].toString());// rs.getString("dblDiscountPer"));
	            objRows.put("DiscAmount",ob[10].toString());// rs.getString("dblDiscountAmt"));
	
	            arrObj.add(objRows);
	            flgDataForPosting = true;
	        }
    	}
        

        if (flgDataForPosting)
        {
            objJson.put("BillPromotionDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillPromo= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                StringBuilder sbUpdateItems = new StringBuilder(updateItemCodes);
                StringBuilder sbUpdatePromoCodes = new StringBuilder(updatePromoCodes);

                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    updateItemCodes = sbUpdateItems.delete(0, 1).toString();
                    updatePromoCodes = sbUpdatePromoCodes.delete(0, 1).toString();
 
                    objBaseServiceImpl.funExecuteUpdate("update tblbillpromotiondtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strItemCode in (" + updateItemCodes + ") "
                            + " and strPromotionCode in (" + updatePromoCodes + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbillpromotiondtl set strDataPostFlag='Y' "
                            + " where strBillNo in (" + updateBills + ") and strItemCode in (" + updateItemCodes + ") "
                            + " and strPromotionCode in (" + updatePromoCodes + ")", "sql");

                    //dbMysql.execute("update tblbillpromotiondtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                 //   JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }
    
    private int funPostBillComplimentryDtlData(String formName) throws Exception
    {
        String updateBills = "";
        String query = "";

        if (formName.equals("Bill"))
        {
            query = "select * from tblbillcomplementrydtl "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
        }
        else if (formName.equals("Day End"))
        {
            query = "select * from tblqbillcomplementrydtl "
                    + "where strDataPostFlag='N' "
                    + "and strBillNo IN(select strBillNo from tblqbillsettlementdtl)";
        }
        else if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblbillcomplementrydtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        else if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblqbillcomplementrydtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        List listPostBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
          
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listPostBill.size()>0)
    	{
        	for(int i=0;i<listPostBill.size();i++)
					{
			   			Object ob[]=(Object[])listPostBill.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[2].toString() + "'";
		            objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));
		            objRows.put("ItemName",ob[0].toString());// rs.getString("strItemName"));
		            objRows.put("BillNo",ob[2].toString());// rs.getString("strBillNo"));
		            objRows.put("AdvBookingNo",ob[3].toString());// rs.getString("strAdvBookingNo"));
		            objRows.put("Rate",ob[4].toString());// rs.getString("dblRate"));
		            objRows.put("Quantity",ob[5].toString());// rs.getString("dblQuantity"));
		            objRows.put("Amount",ob[6].toString());// rs.getString("dblAmount"));
		            objRows.put("TaxAmount",ob[7].toString());// rs.getString("dblTaxAmount"));
		            objRows.put("BillDate",ob[8].toString());// rs.getString("dteBillDate"));
		            objRows.put("KOTNo",ob[9].toString());// rs.getString("strKOTNo"));
		            objRows.put("ClientCode",ob[10].toString());// rs.getString("strClientCode"));
		            objRows.put("CustomerCode",ob[11].toString());// rs.getString("strCustomerCode"));
		            objRows.put("OrderProcessing",ob[12].toString());// rs.getString("tmeOrderProcessing"));
		            objRows.put("DataPostFlag",ob[13].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("MMSDataPostFlag",ob[14].toString());// rs.getString("strMMSDataPostFlag"));
		            objRows.put("ManualKOTNo",ob[15].toString());// rs.getString("strManualKOTNo"));
		            objRows.put("tdhYN",ob[16].toString());// rs.getString("tdhYN"));
		            objRows.put("PromoCode",ob[17].toString());// rs.getString("strPromoCode"));
		            objRows.put("CounterCode",ob[18].toString());// rs.getString("strCounterCode"));
		            objRows.put("WaiterNo",ob[19].toString());// rs.getString("strWaiterNo"));
		            objRows.put("DiscountAmt",ob[20].toString());// rs.getString("dblDiscountAmt"));
		            objRows.put("DiscountPer",ob[21].toString());// rs.getString("dblDiscountPer"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    	}
        

        if (flgDataForPosting)
        {
            objJson.put("BillComplimentryDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("BillPromo= " + op);
            conn.disconnect();
            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    objBaseServiceImpl.funExecuteUpdate("update tblbillcomplementrydtl set strDataPostFlag='Y' "
                    		+ "where strBillNo in (" + updateBills + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqbillcomplementrydtl set strDataPostFlag='Y'"
                    		+ " where strBillNo in (" + updateBills + ")", "sql");
                    
                    //dbMysql.execute("update tblbillpromotiondtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

 // Function to Post Customer Master Data to HO
    public boolean funPostCustomerMasterDataToHO()
    {
        boolean flgResult = false;
        StringBuilder sql = new StringBuilder();
       

        try
        {
            JSONObject rootObject = new JSONObject();
            JSONArray dataObjectArray = new JSONArray();

            sql.append("select * from tblcustomermaster where strDataPostFlag='N'");
            List listCustInfo=objBaseServiceImpl.funGetList(sql, "sql");
            if(listCustInfo.size()>0)
            {
            		for(int i=0;i<listCustInfo.size();i++)
		            {
            			Object ob[]=(Object[])listCustInfo.get(i);
		                JSONObject dataObject = new JSONObject();
		                dataObject.put("CustomerCode", ob[0].toString());
		                dataObject.put("CustomerName",ob[1].toString());// rsCustInfo.getString("strCustomerName"));
		                dataObject.put("BuldingCode",ob[2].toString());// rsCustInfo.getString("strBuldingCode"));
		                dataObject.put("BuildingName",ob[3].toString());// rsCustInfo.getString("strBuildingName"));
		                dataObject.put("StreetName",ob[4].toString());// rsCustInfo.getString("strStreetName"));
		                dataObject.put("Landmark",ob[5].toString());// rsCustInfo.getString("strLandmark"));
		                dataObject.put("Area",ob[6].toString());// rsCustInfo.getString("strArea"));
		                dataObject.put("City",ob[7].toString());// rsCustInfo.getString("strCity"));
		                dataObject.put("State",ob[8].toString());// rsCustInfo.getString("strState"));
		                dataObject.put("PinCode",ob[9].toString());// rsCustInfo.getString("intPinCode"));
		                dataObject.put("MobileNo",ob[10].toString());// rsCustInfo.getString("longMobileNo"));
		                dataObject.put("AlternateMobileNo",ob[11].toString());// rsCustInfo.getString("longAlternateMobileNo"));
		                dataObject.put("OfficeBuildingCode",ob[12].toString());// rsCustInfo.getString("strOfficeBuildingCode"));
		                dataObject.put("OfficeBuildingName",ob[13].toString());// rsCustInfo.getString("strOfficeBuildingName"));
		                dataObject.put("OfficeStreetName",ob[14].toString());// rsCustInfo.getString("strOfficeStreetName"));
		                dataObject.put("OfficeLandmark",ob[15].toString());// rsCustInfo.getString("strOfficeLandmark"));
		                dataObject.put("OfficeArea",ob[16].toString());// rsCustInfo.getString("strOfficeArea"));
		                dataObject.put("OfficeCity",ob[17].toString());// rsCustInfo.getString("strOfficeCity"));
		                dataObject.put("OfficePinCode",ob[18].toString());// rsCustInfo.getString("strOfficePinCode"));
		                dataObject.put("OfficeState",ob[19].toString());// rsCustInfo.getString("strOfficeState"));
		                dataObject.put("OfficeNo",ob[20].toString());// rsCustInfo.getString("strOfficeNo"));
		                dataObject.put("UserCreated",ob[21].toString());// rsCustInfo.getString("strUserCreated"));
		                dataObject.put("UserEdited",ob[22].toString());// rsCustInfo.getString("strUserEdited"));
		                dataObject.put("DateCreated",ob[23].toString());// rsCustInfo.getString("dteDateCreated"));
		                dataObject.put("DateEdited",ob[24].toString());// rsCustInfo.getString("dteDateEdited"));24
		                dataObject.put("ClientCode",ob[26].toString());// rsCustInfo.getString("strClientCode"));26
		                dataObject.put("DataPostFlag",ob[25].toString());// rsCustInfo.getString("strDataPostFlag"));25
		                dataObject.put("OfficeAddress",ob[27].toString());// rsCustInfo.getString("strOfficeAddress"));27
		                dataObject.put("ExternalCode",ob[28].toString());// rsCustInfo.getString("strExternalCode"));
		                dataObject.put("CustomerType",ob[29].toString());// rsCustInfo.getString("strCustomerType"));
		                dataObject.put("DOB",ob[30].toString());// rsCustInfo.getString("dteDOB"));
		                dataObject.put("Gender",ob[31].toString());// rsCustInfo.getString("strGender"));
		                dataObject.put("Anniversary",ob[32].toString());// rsCustInfo.getString("dteAnniversary"));
		                dataObject.put("EmailId",ob[33].toString());// rsCustInfo.getString("strEmailId"));
		                dataObject.put("CRMId",ob[34].toString());// rsCustInfo.getString("strCRMId"));34
		
		                dataObjectArray.add(dataObject);
		            }
        		}
            rootObject.put("tblcustomermaster", dataObjectArray);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostMasterData";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(rootObject.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("CustData flg=" + op);
            conn.disconnect();
            flgResult = Boolean.parseBoolean(op);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return flgResult;
        }
    }  

    public boolean funPostCustomerAreaMaster()
    {
        boolean flgResult = false;
        StringBuilder sql = new StringBuilder();
        
        try
        {
            JSONObject rootObject = new JSONObject();
            JSONArray dataObjectArray = new JSONArray();

            sql.setLength(0);
            sql.append("select * from tblbuildingmaster where strDataPostFlag='N'");
            List listCustAreaMaster=objBaseServiceImpl.funGetList(sql, "sql");
            if(listCustAreaMaster.size()>0)
            {
            		for(int i=0;i<listCustAreaMaster.size();i++)
		            {
            			Object ob[]=(Object[])listCustAreaMaster.get(i);
				        JSONObject dataObject = new JSONObject();
		
		                dataObject.put("BuildingCode",ob[0].toString());
		                dataObject.put("BuildingName",ob[1].toString());//
		                dataObject.put("Address",ob[1].toString());// rsCustAreaMaster.getString("strAddress"));
		                dataObject.put("UserCreated",ob[1].toString());// rsCustAreaMaster.getString("strUserCreated"));
		                dataObject.put("UserEdited",ob[1].toString());// rsCustAreaMaster.getString("strUserEdited"));
		                dataObject.put("DateCreated", ob[1].toString());//rsCustAreaMaster.getString("dteDateCreated"));
		                dataObject.put("DateEdited", ob[1].toString());//rsCustAreaMaster.getString("dteDateEdited"));
		                dataObject.put("HomeDeliCharge",ob[1].toString());// rsCustAreaMaster.getString("dblHomeDeliCharge"));
		                dataObject.put("ClientCode",ob[1].toString());// rsCustAreaMaster.getString("strClientCode"));
		                dataObject.put("DataPostFlag", ob[1].toString());//rsCustAreaMaster.getString("strDataPostFlag"));
		
		                dataObjectArray.add(dataObject);
		            }
            }
            rootObject.put("tblbuildingmaster", dataObjectArray);

            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostMasterData";
            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(rootObject.toString().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "Updated successfully: ";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            conn.disconnect();
            flgResult = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            sql = null;
            return flgResult;
        }
    }
    
    public boolean funPostDelChargesMaster()
    {
        boolean flgResult = false;
        StringBuilder sql = new StringBuilder();
      

        try
        {
            sql.setLength(0);
            sql.append("select * from tblareawisedc where strDataPostFlag='N'");
            List listCustAreaDelCharges=objBaseServiceImpl.funGetList(sql, "sql");
            JSONObject rootObject = new JSONObject();
            JSONArray dataObjectArray = new JSONArray();

            if(listCustAreaDelCharges.size()>0)
            {
            	for(int i=0;i<listCustAreaDelCharges.size();i++)
			        {
	            		Object ob[]=(Object[])listCustAreaDelCharges.get(i);
		                JSONObject dataObject = new JSONObject();
		
		                dataObject.put("BuildingCode", ob[0].toString());
		                dataObject.put("Kilometers",ob[1].toString());// rsCustAreaDelCharges.getString("dblKilometers"));
		                dataObject.put("Symbol",ob[2].toString());// rsCustAreaDelCharges.getString("strSymbol"));
		                dataObject.put("BillAmount",ob[3].toString());// rsCustAreaDelCharges.getString("dblBillAmount"));
		                dataObject.put("BillAmount1",ob[4].toString());// rsCustAreaDelCharges.getString("dblBillAmount1"));
		                dataObject.put("DeliveryCharges",ob[5].toString());// rsCustAreaDelCharges.getString("dblDeliveryCharges"));
		                dataObject.put("UserCreated",ob[6].toString());// rsCustAreaDelCharges.getString("strUserCreated"));
		                dataObject.put("UserEdited",ob[7].toString());// rsCustAreaDelCharges.getString("strUserEdited"));
		                dataObject.put("DateCreated",ob[8].toString());// rsCustAreaDelCharges.getString("dteDateCreated"));
		                dataObject.put("DateEdited",ob[9].toString());// rsCustAreaDelCharges.getString("dteDateEdited"));
		                dataObject.put("ClientCode",ob[10].toString());// rsCustAreaDelCharges.getString("strClientCode"));
		                dataObject.put("DataPostFlag",ob[11].toString());// rsCustAreaDelCharges.getString("strDataPostFlag"));
		
		                dataObjectArray.add(dataObject);
		            }
            }
	            rootObject.put("tblareawisedc", dataObjectArray);
	
	            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostMasterData";
	            URL url = new URL(hoURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            os.write(rootObject.toString().getBytes());
	            os.flush();
	
	            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	            {
	                throw new RuntimeException("Failed : HTTP error code : "
	                        + conn.getResponseCode());
	            }
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "", op = "Updated successfully: ";
	
	            while ((output = br.readLine()) != null)
	            {
	                op += output;
	            }
	            conn.disconnect();
	            flgResult = true;
	        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            
            sql = null;
            return flgResult;
        }
    }
    
    public boolean funPostDayEndData(String newStartDate, int shiftCode,String ClientCode,String POSCode)
	    {
	        boolean flgResult = false;
	        StringBuilder sql = new StringBuilder();
	        
	        Map<String, String> hmDayEndData = new HashMap<String, String>();
	        try
	        {
	            sql.setLength(0);
	            sql.append("select * from tbldayendprocess where strDayEnd='Y' and strDataPostFlag='N' "
	                    + " and strPOSCode='" + POSCode + "' ");
	            List listDayEnd=objBaseServiceImpl.funGetList(sql, "sql");

	            JSONObject rootObject = new JSONObject();
	            JSONArray dataObjectArray = new JSONArray();
	            if(listDayEnd.size()>0)
	            {
	            	for(int i=0;i<listDayEnd.size();i++)
		            {
	            		Object ob[]=(Object[])listDayEnd.get(i);
		                JSONObject dataObject = new JSONObject();
		
		                hmDayEndData.put(ob[1].toString(), ob[0].toString());
		                dataObject.put("POSCode",ob[0].toString());// rsDayEnd.getString("strPOSCode"));
		                dataObject.put("POSDate",ob[1].toString());// rsDayEnd.getString("dtePOSDate"));
		                dataObject.put("DayEnd",ob[2].toString());// rsDayEnd.getString("strDayEnd"));
		                dataObject.put("TotalSale",ob[3].toString());// rsDayEnd.getString("dblTotalSale"));
		                dataObject.put("NoOfBill",ob[4].toString());// rsDayEnd.getString("dblNoOfBill"));
		                dataObject.put("NoOfVoidedBill",ob[5].toString());// rsDayEnd.getString("dblNoOfVoidedBill"));
		                dataObject.put("NoOfModifyBill",ob[6].toString());// rsDayEnd.getString("dblNoOfModifyBill"));
		                dataObject.put("HDAmt",ob[7].toString());// rsDayEnd.getString("dblHDAmt"));
		                dataObject.put("DiningAmt",ob[8].toString());// rsDayEnd.getString("dblDiningAmt"));
		                dataObject.put("TakeAway",ob[9].toString());// rsDayEnd.getString("dblTakeAway"));
		                dataObject.put("Float",ob[10].toString());// rsDayEnd.getString("dblFloat"));
		                dataObject.put("Cash",ob[11].toString());// rsDayEnd.getString("dblCash"));
		                dataObject.put("Advance",ob[12].toString());// rsDayEnd.getString("dblAdvance"));
		                dataObject.put("TransferIn",ob[13].toString());// rsDayEnd.getString("dblTransferIn"));
		                dataObject.put("TotalReceipt",ob[14].toString());// rsDayEnd.getString("dblTotalReceipt"));
		                dataObject.put("Payments",ob[15].toString());// rsDayEnd.getString("dblPayments"));
		                dataObject.put("Withdrawal",ob[16].toString());// rsDayEnd.getString("dblWithdrawal"));
		                dataObject.put("TransferOut",ob[17].toString());// rsDayEnd.getString("dblTransferOut"));
		                dataObject.put("TotalPay",ob[18].toString());// rsDayEnd.getString("dblTotalPay"));
		                dataObject.put("CashInHand",ob[19].toString());// rsDayEnd.getString("dblCashInHand"));
		                dataObject.put("Refund",ob[20].toString());// rsDayEnd.getString("dblRefund"));
		                dataObject.put("TotalDiscount",ob[21].toString());// rsDayEnd.getString("dblTotalDiscount"));
		                dataObject.put("NoOfDiscountedBill",ob[22].toString());// rsDayEnd.getString("dblNoOfDiscountedBill"));
		                dataObject.put("ShiftCode",ob[23].toString());// rsDayEnd.getString("intShiftCode"));
		                dataObject.put("ShiftEnd",ob[24].toString());// rsDayEnd.getString("strShiftEnd"));
		                dataObject.put("TotalPax",ob[25].toString());// rsDayEnd.getString("intTotalPax"));
		                dataObject.put("NoOfTakeAway",ob[26].toString());// rsDayEnd.getString("intNoOfTakeAway"));
		                dataObject.put("NoOfHomeDelivery",ob[27].toString());// rsDayEnd.getString("intNoOfHomeDelivery"));
		                dataObject.put("UserCreated",ob[28].toString());// rsDayEnd.getString("strUserCreated"));
		                dataObject.put("DateCreated",ob[29].toString());// rsDayEnd.getString("dteDateCreated"));
		                dataObject.put("DayEndDateTime",ob[30].toString());// rsDayEnd.getString("dteDayEndDateTime"));
		                dataObject.put("UserEdited",ob[31].toString());// rsDayEnd.getString("strUserEdited"));
		                dataObject.put("DataPostFlag",ob[32].toString());// rsDayEnd.getString("strDataPostFlag"));
		                dataObject.put("NoOfNCKOT",ob[33].toString());// rsDayEnd.getString("intNoOfNCKOT"));
		                dataObject.put("NoOfComplimentaryKOT",ob[34].toString());// rsDayEnd.getString("intNoOfComplimentaryKOT"));
		                dataObject.put("NoOfVoidKOT",ob[35].toString());// rsDayEnd.getString("intNoOfVoidKOT"));
		                dataObject.put("UsedDebitCardBalance",ob[36].toString());// rsDayEnd.getString("dblUsedDebitCardBalance"));
		                dataObject.put("UnusedDebitCardBalance",ob[37].toString());// rsDayEnd.getString("dblUnusedDebitCardBalance"));
		                dataObject.put("WSStockAdjustmentNo",ob[38].toString());// rsDayEnd.getString("strWSStockAdjustmentNo"));
		                dataObject.put("TipAmt",ob[39].toString());// rsDayEnd.getString("dblTipAmt"));
		                dataObject.put("strExciseBillGeneration",ob[40].toString());// rsDayEnd.getString("strExciseBillGeneration"));
		                
		                
		                
		                dataObject.put("StartDate", newStartDate);
		                dataObject.put("NewShiftCode", shiftCode);
		                dataObject.put("ClientCode", ClientCode);
		                dataObjectArray.add(dataObject);
		            }
	            }
	            rootObject.put("tbldayendprocess", dataObjectArray);
	
	            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostMasterData";
	            URL url = new URL(hoURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            os.write(rootObject.toString().getBytes());
	            os.flush();
	
	            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	            {
	                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	            }
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "", op = "";
	            while ((output = br.readLine()) != null)
	            {
	                op += output;
	            }
	            System.out.println("Day End Posting= " + op);
	            if (op.equalsIgnoreCase("true"))
	            {
	                StringBuilder sbSql = new StringBuilder();
	                for (Map.Entry<String, String> entry : hmDayEndData.entrySet())
	                {
	                    sbSql.setLength(0);
	                    sbSql.append("update tbldayendprocess set strDataPostFlag='Y' "
	                            + " where strPOSCode='" + entry.getValue() + "' and dtePOSDate='" + entry.getKey() + "' ");
	                    objBaseServiceImpl.funExecuteUpdate(sql.toString(), "sql");
	                }
	                sbSql = null;
	            }
	            conn.disconnect();
	            flgResult = true;
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	           
	            sql = null;
	            return flgResult;
	        }
	    }

    public int funPostCashManagementData(String ClientCode,String POSCode) throws Exception
    {
        String transId = "";
        String query = "";

        query = "select * from tblcashmanagement where strDataPostFlag='N' limit 2000";
        List listCash=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();
        transId = "";
        if(listCash.size()>0)
        {
        	for(int i=0;i<listCash.size();i++)
	        {
        		Object ob[]=(Object[])listCash.get(i);
	            JSONObject objRows = new JSONObject();
	            
	            transId += ",'" + ob[0].toString() + "'";
	
	            objRows.put("TransId",ob[0].toString());// rs.getString("strTransID"));
	            objRows.put("TransType",ob[1].toString());// rs.getString("strTransType"));
	            objRows.put("TransDate",ob[2].toString());// rs.getString("dteTransDate"));
	            objRows.put("ReasonCode",ob[3].toString());// rs.getString("strReasonCode"));
	            objRows.put("POSCode",ob[4].toString());// rs.getString("strPOSCode"));
	            objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
	            objRows.put("Remarks",ob[6].toString());// rs.getString("strRemarks"));
	            objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
	            objRows.put("UserEdited",ob[8].toString());// rs.getString("strUserEdited"));
	            objRows.put("DateCreated",ob[9].toString());// rs.getString("dteDateCreated"));
	            objRows.put("DateEdited",ob[10].toString());// rs.getString("dteDateEdited"));10
	            objRows.put("ClientCode",ob[14].toString());// rs.getString("strClientCode"));14
	            objRows.put("CurrencyType",ob[11].toString());// rs.getString("strCurrencyType"));11
	            objRows.put("ShiftCode",ob[12].toString());// rs.getString("intShiftCode"));12
	            objRows.put("Against",ob[13].toString());// rs.getString("strAgainst"));
	            objRows.put("RollingAmt",ob[14].toString());// rs.getString("dblRollingAmt"));
	            objRows.put("DataPostFlag",ob[16].toString());// rs.getString("strDataPostFlag"));16
	
	            arrObj.add(objRows);
	        }
        }
        
        objJson.put("CashManagementData", arrObj);

        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Cash management=" + op);
        conn.disconnect();

        boolean flgDataPosting = false;
        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(transId);
            if (transId.length() > 0)
            {
                transId = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblcashmanagement set"
                		+ " strDataPostFlag='Y' where strTransId in (" + transId + ")", "sql");
            }
            flgDataPosting = true;
        }
        else
        {
            flgDataPosting = false;
        }
        if (flgDataPosting)
        {
            //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
        }

        return 1;
    }

    
    //Sychronoze pos data to Ho
    public String funPostPOSItemSalesDataAuto(String itemType, String posCode, String dateFrom, String dateTo,String gClientCode)
   {
	fromDate=dateFrom;
	toDate=dateTo;
        boolean flgResult = false;
        String WSStockAdjustmentCode = "";
        String sqlLiveLinkupFile = "", sqlQLinkupFile = "";
        ArrayList<ArrayList<String>> arrUnLinkedItemDtl = new ArrayList<ArrayList<String>>();
        try
        {
            String WSLocationCode = "";
            String posName = "";

            String sqlLocation = " Select strWSLocationCode from tblPOSMaster where strPOSCode='" + posCode + "' ; ";
            List listLoc=objBaseServiceImpl.funGetList(new StringBuilder(sqlLocation), "sql");
            if(listLoc.size()>0)
            {
           	 WSLocationCode = ((Object)listLoc.get(0)).toString();
            }
            String filter1 = "";
            if (posCode.equalsIgnoreCase("All"))
            {
                filter1 = " and date(a.dteBillDate) between '" + dateFrom + "' and '" + dateTo + "' ";
            }
            else
            {
                filter1 = " and a.strPOSCode='" + posCode + "' and date(a.dteBillDate) between '" + dateFrom + "' and '" + dateTo + "' ";
            }

            sqlLiveLinkupFile = " select b.strItemCode,c.strItemName,d.strPosName "
                    + " from tblbillhd a,tblbilldtl b,tblitemmaster c,tblposmaster d "
                    + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
                    + " and a.strPOSCode=d.strPosCode "
                    + " and b.strItemCode not in (select strItemCode from tblitemmasterlinkupdtl) "
                    + filter1;
            if (!itemType.equals("Both"))
            {
                sqlLiveLinkupFile += " and c.strItemType='" + itemType + "' ";
            }
            sqlLiveLinkupFile += " group by b.strItemCode,d.strPosCode ";

            sqlQLinkupFile = " select b.strItemCode,c.strItemName,d.strPosName "
                    + " from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblposmaster d  "
                    + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
                    + " and a.strPOSCode=d.strPosCode "
                    + " and b.strItemCode not in (select strItemCode from tblitemmasterlinkupdtl) "
                    + filter1;
            if (!itemType.equals("Both"))
            {
                sqlQLinkupFile += " and c.strItemType='" + itemType + "' ";
            }
            sqlQLinkupFile += " group by b.strItemCode,d.strPosCode ";

            //System.out.println(sqlLiveLinkupFile);
            //System.out.println(sqlQLinkupFile);
            
            List listLiveLinkupDtl=objBaseServiceImpl.funGetList(new StringBuilder(sqlLiveLinkupFile), "sql");
            if(listLiveLinkupDtl.size()>0)
            {
	             for(int i=0;i<listLiveLinkupDtl.size();i++)
	             {
	            	 Object ob[]=(Object[])listLiveLinkupDtl.get(i);
	                 ArrayList<String> arrUnLinkedItem = new ArrayList<String>();
	                 arrUnLinkedItem.add(ob[0].toString());
	                 arrUnLinkedItem.add(ob[1].toString());
	                 posName = ob[2].toString();
	                 arrUnLinkedItemDtl.add(arrUnLinkedItem);
	             }
	           
            }
            
            List listQLinkupDtl=objBaseServiceImpl.funGetList(new StringBuilder(sqlQLinkupFile), "sql");
            if(listQLinkupDtl.size()>0)
            {
           	 for(int i=0;i<listQLinkupDtl.size();i++)
	             {
	            	 Object ob[]=(Object[])listQLinkupDtl.get(i);
	                 ArrayList<String> arrUnLinkedItem = new ArrayList<String>();
	                 arrUnLinkedItem.add(ob[0].toString());
	                 arrUnLinkedItem.add(ob[1].toString());
	                 posName = ob[2].toString();
	                 arrUnLinkedItemDtl.add(arrUnLinkedItem);
	             }
            }
            
        
            if (arrUnLinkedItemDtl.size() > 0)
            {
              //  JOptionPane.showMessageDialog(null, "some Items needs to be linkup with web stock product code");
            	objUtilityController.funGenerateLinkupTextfile(arrUnLinkedItemDtl, dateFrom, dateTo, posName,gClientCode);
            }
            else
            {

                String queryDayEnd = "select a.strPOSCode,a.dtePOSDate,a.intShiftCode,ifnull(a.strWSStockAdjustmentNo,'') strWSStockAdjustmentNo,a.strDayEnd,a.strShiftEnd "
                        + "from tbldayendprocess a "
                        + "where date(a.dtePOSDate) between '" + dateFrom + "' and '" + dateTo + "' "
                        + "and a.strDayEnd='Y' "
                        + "and a.strShiftEnd='Y' "
                        + "and a.strPOSCode='" + posCode + "' ";


                List listDayEndData=objBaseServiceImpl.funGetList(new StringBuilder(queryDayEnd), "sql");
                if(listDayEndData.size()>0)
                {
               	 for(int i=0;i<listDayEndData.size();i++)
		    	        {
               		 	Object obDayEnd[]=(Object[])listDayEndData.get(i);
               		 	String dayEndPOSCode = obDayEnd[0].toString();//posCode
		                     String posDayEndDate = obDayEnd[1].toString();//posDayEndDate
		                     String posShiftEndCode = obDayEnd[2].toString();//posShiftEndCode
		                     String posDayEndWSStockAdjNo = obDayEnd[3].toString();//posWSStockAdjNo
		                     //*****************************************************************//
		
		                     JSONObject rootObject = new JSONObject();
		                     JSONArray dataObjectArray = new JSONArray();
		                     String sql = "", qFileSql = "";
		                     String filter = "";
		                     if (dayEndPOSCode.equalsIgnoreCase("All"))
		                     {
		                         filter = " and date(a.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' ";
		                     }
		                     else
		                     {
		                         filter = " and a.strPOSCode='" + posCode + "' "
		                                 + " and c.strPOSCode='" + posCode + "' "
		                                 + " and a.intShiftCode='" + posShiftEndCode + "' "
		                                 + " and date(a.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' ";
		                     }
		
		                     if (itemType.equals("Both"))
		                     {
		                         sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "', c.strWSProductCode "
		                                 + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c "
		                                 + "where a.strBillNo=b.strBillNo  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate ";
		
		                         qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "', c.strWSProductCode "
		                                 + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c "
		                                 + "where a.strBillNo=b.strBillNo  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate ";
		                     }
		                     else if (itemType.equals("Liquor"))
		                     {
		                         sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		                                 + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                                 + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                                 + "and b.strItemCode=d.strItemCode "
		                                 + "and d.strItemType='Liquor'  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate ";
		
		                         qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		                                 + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                                 + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                                 + "and b.strItemCode=d.strItemCode "
		                                 + "and d.strItemType='Liquor'  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate ";
		                     }
		                     else
		                     {
		                         sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		                                 + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                                 + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                                 + "and b.strItemCode=d.strItemCode "
		                                 + "and d.strItemType='Food'  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate ";
		
		                         qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                                 + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		                                 + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                                 + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                                 + "and b.strItemCode=d.strItemCode "
		                                 + "and d.strItemType='Food'  and b.strItemCode=c.strItemCode "
		                                 + filter
		                                 + "group by b.strItemCode order by a.dteBillDate";
		                     }
		                     //System.out.println(sql);
		                     //System.out.println(qFileSql);
		
		                     List listItemSales=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
		                     if(listItemSales.size()>0)
		                     {
		                    	 for(int j=0;j<listItemSales.size();j++)
			                     {
		                 		 	Object obItemSale[]=(Object[])listItemSales.get(i);

			                         JSONObject dataObject = new JSONObject();
			                         dataObject.put("posItemCode", obItemSale[0].toString());
			                         dataObject.put("posItemName", obItemSale[1].toString());
			                         dataObject.put("quantity", obItemSale[2].toString());
			                         dataObject.put("rate",obItemSale[3].toString());
			                         dataObject.put("posCode", obItemSale[4].toString());
			                         dataObject.put("billDate", obItemSale[5].toString());
			                         dataObject.put("clientCode", obItemSale[6].toString());
			                         dataObject.put("wsProdCode",obItemSale[7].toString());
			
			                         dataObjectArray.add(dataObject);
			                     }
		                     }
		
		                      listItemSales=objBaseServiceImpl.funGetList(new StringBuilder(qFileSql), "sql");
		                     if(listItemSales.size()>0)
		                     {
		                    	 for(int j=0;j<listItemSales.size();j++)
			                     {
		                 		 	Object obItemSale[]=(Object[])listItemSales.get(i);

			                         JSONObject dataObject = new JSONObject();
			                         dataObject.put("posItemCode", obItemSale[0].toString());
			                         dataObject.put("posItemName", obItemSale[1].toString());
			                         dataObject.put("quantity", obItemSale[2].toString());
			                         dataObject.put("rate",obItemSale[3].toString());
			                         dataObject.put("posCode", obItemSale[4].toString());
			                         dataObject.put("billDate", obItemSale[5].toString());
			                         dataObject.put("clientCode", obItemSale[6].toString());
			                         dataObject.put("wsProdCode",obItemSale[7].toString());
			
			                         dataObjectArray.add(dataObject);
			                     }
		                     }
		                     
		                     if (dataObjectArray.size() > 0)
		                     {
		                         rootObject.put("MemberPOSSalesInfo", dataObjectArray);
		                         rootObject.put("WSLocation", WSLocationCode);
		                         rootObject.put("DayEndWSStockAdjNo", posDayEndWSStockAdjNo);
		                         
		                         String gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(gClientCode,posCode, "gSanguineWebServiceURL");
				        		 
		                         //localhost:8080/prjSanguineWebService/ExciseIntegration/funPostPOSSaleData
		                         String hoURL = gSanguineWebServiceURL + "/MMSIntegrationAuto/funPostPOSSaleDataAuto";
		
		                         URL url = new URL(hoURL);
		                         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		                         conn.setDoOutput(true);
		                         conn.setRequestMethod("POST");
		                         conn.setRequestProperty("Content-Type", "application/json");
		                         OutputStream os = conn.getOutputStream();
		                         os.write(rootObject.toString().getBytes());
		                         os.flush();
		
		                         if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
		                         {
		                             throw new RuntimeException("Failed : HTTP error code : "
		                                     + conn.getResponseCode());
		                         }
		                         BufferedReader br = new BufferedReader(new InputStreamReader(
		                                 (conn.getInputStream())));
		
		                         String output = "", op = "Updated successfully:";
		                         while ((output = br.readLine()) != null)
		                         {
		                             op += output;
		                         }
		
		                         //System.out.println(op);
		                         WSStockAdjustmentCode = op.split(":")[1];
		                         //System.out.println("wsStockAdjustmentCode:"+WSStockAdjustmentCode);
		                         conn.disconnect();
		                         //***********************update day end WebStocks Adjustment*******************************//
		                         String queryUpdateDayEndWSAdjNo = "update tbldayendprocess "
		                                 + "set strWSStockAdjustmentNo='" + WSStockAdjustmentCode + "' "
		                                 + "where strPOSCode='" + posCode + "' "
		                                 + "and dtePOSDate='" + posDayEndDate + "' "
		                                 + "and intShiftCode='" + posShiftEndCode + "' ";
		                         int k=objBaseServiceImpl.funExecuteUpdate(queryUpdateDayEndWSAdjNo, "sql");
		                         WSStockAdjustmentCode = "";
		                         /*
		                          if(op.split(":")[1]!=null && op.split(":")[1].trim().equalsIgnoreCase("true"))
		                          {
		                          flgResult = true;
		                          }*/
		//                         if (!(op.equals("NA")))
		//                         {
		//                             flgResult = true;
		//                         }
		//                     if (flgResult)
		//                     {
		//                         String updateLiveBillDtl = "update tblbilldtl a join "
		//                                 + " ( select b.strBillNo as BillNo,b.strPOSCode as POSCode"
		//                                 + ", b.dteBillDate as BillDate from tblbillhd b "
		//                                 + " where date(b.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                                 + " and a.intShiftCode='"+posShiftEndCode+"' ) c ,tblitemmaster d   "
		//                                 + " on a.strbillno=c.BillNo "
		//                                 + " set a.strMMSDataPostFlag='Y' "
		//                                 + " where date(c.BillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                                 + " and a.strItemCode=d.strItemCode "
		//                                 + " and c.intShiftCode='"+posShiftEndCode+"' ";
		//                         
		//                         if (itemType.equalsIgnoreCase("FOOD"))
		//                         {
		//                             updateLiveBillDtl += " and d.strItemType='FOOD' ";
		//                         }
		//                         if (itemType.equalsIgnoreCase("Liquor"))
		//                         {
		//                             updateLiveBillDtl += " and d.strItemType='Liquor' ";
		//                         }
		//                         if (!posCode.equalsIgnoreCase("All"))
		//                         {
		//                             updateLiveBillDtl += " and c.POSCode='" + posCode + "' ";
		//                         }
		//                         dbMysql.execute(updateLiveBillDtl);
		 //
		//                         String updateQFileBillDtl = "update tblqbilldtl a join "
		//                                 + " ( select b.strBillNo as BillNo,b.strPOSCode as POSCode"
		//                                 + ", b.dteBillDate as BillDate from tblqbillhd b "
		//                                 + " where date(b.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                                 + " and a.intShiftCode='"+posShiftEndCode+"' ) c ,tblitemmaster d "
		//                                 + " on a.strbillno=c.BillNo "
		//                                 + " set a.strMMSDataPostFlag='Y' "
		//                                 + " where date(c.BillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                                 + " and a.strItemCode=d.strItemCode "
		//                                 + " and c.intShiftCode='"+posShiftEndCode+"' ";
		//                         
		//                                                 
		//                         if (itemType.equalsIgnoreCase("FOOD"))
		//                         {
		//                             updateQFileBillDtl += " and d.strItemType='FOOD' ";
		//                         }
		//                         if (itemType.equalsIgnoreCase("Liquor"))
		//                         {
		//                             updateQFileBillDtl += " and d.strItemType='Liquor' ";
		//                         }
		 //
		//                         if (!posCode.equalsIgnoreCase("All"))
		//                         {
		//                             updateQFileBillDtl += " and c.POSCode='" + posCode + "' ";
		//                         }
		//                         dbMysql.execute(updateQFileBillDtl);
		//                         
		//                         
		//                        
		//                         
		//                     }
		
		                     }
		                     flgResult = true;
		                 }
   	        }
            }
        }
        catch (Exception e)
        {
            flgResult = false;
            WSStockAdjustmentCode = "";
            e.printStackTrace();
        }
        finally
        {
            return WSStockAdjustmentCode;
        }
    }
   
	//*****For Excise*******//  
    public String funPostPOSSalesDataToExciseAuto(String itemType, String posCode, String dateFrom, String dateTo,String clientCode)
	{
	fromDate=dateFrom;
	toDate=dateTo;
	    boolean flgResult = false;
	    String exciseBillNo = "";
	    String sqlLiveLinkupFile = "", sqlQLinkupFile = "";
	    ArrayList<ArrayList<String>> arrUnLinkedItemDtl = new ArrayList<ArrayList<String>>();
	    try
	    {
	        String exciseLicencceCode = "";
	        String posName = "";

	        String sqlLocation = " Select strExciseLicenceCode from tblPOSMaster where strPOSCode='" + posCode + "' ; ";
	        List listLoc=objBaseServiceImpl.funGetList(new StringBuilder(sqlLocation), "sql");
	        if(listLoc.size()>0)
	        {
	        	exciseLicencceCode = ((Object)listLoc.get(0)).toString();
	        }
	       
	        String filter1 = "";
	        if (posCode.equalsIgnoreCase("All"))
	        {
	            filter1 = " and date(a.dteBillDate) between '" + dateFrom + "' and '" + dateTo + "' ";
	        }
	        else
	        {
	            filter1 = " and a.strPOSCode='" + posCode + "' and date(a.dteBillDate) between '" + dateFrom + "' and '" + dateTo + "' ";
	        }

	        sqlLiveLinkupFile = " select b.strItemCode,c.strItemName,d.strPosName "
	                + " from tblbillhd a,tblbilldtl b,tblitemmaster c,tblposmaster d "
	                + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
	                + " and a.strPOSCode=d.strPosCode "
	                + " and b.strItemCode not in (select strItemCode from tblitemmasterlinkupdtl) "
	                + filter1;
//	        if (!itemType.equals("Both"))
//	        {
	        sqlLiveLinkupFile += " and c.strItemType='Liquor' ";
	        // }
	        sqlLiveLinkupFile += " group by b.strItemCode,d.strPosCode ";

	        sqlQLinkupFile = " select b.strItemCode,c.strItemName,d.strPosName "
	                + " from tblqbillhd a,tblqbilldtl b,tblitemmaster c,tblposmaster d  "
	                + " where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
	                + " and a.strPOSCode=d.strPosCode "
	                + " and b.strItemCode not in (select strItemCode from tblitemmasterlinkupdtl) "
	                + filter1;
//	        if (!itemType.equals("Both"))
//	        {
	        sqlQLinkupFile += " and c.strItemType='Liquor' ";
	        // }
	        sqlQLinkupFile += " group by b.strItemCode,d.strPosCode ";

	        //System.out.println(sqlLiveLinkupFile);
	        //System.out.println(sqlQLinkupFile);
	        List listLiveLinkupDtl=objBaseServiceImpl.funGetList(new StringBuilder(sqlLiveLinkupFile), "sql");
	        if(listLiveLinkupDtl.size()>0)
	        {
	            for(int i=0;i<listLiveLinkupDtl.size();i++)
	            {
	           	 Object ob[]=(Object[])listLiveLinkupDtl.get(i);
	                ArrayList<String> arrUnLinkedItem = new ArrayList<String>();
	                arrUnLinkedItem.add(ob[0].toString());
	                arrUnLinkedItem.add(ob[1].toString());
	                posName = ob[2].toString();
	                arrUnLinkedItemDtl.add(arrUnLinkedItem);
	            }
	          
	        }
	        List listQLinkupDtl=objBaseServiceImpl.funGetList(new StringBuilder(sqlQLinkupFile), "sql");
	        if(listQLinkupDtl.size()>0)
	        {
	       	 for(int i=0;i<listQLinkupDtl.size();i++)
	            {
	           	 Object ob[]=(Object[])listQLinkupDtl.get(i);
	                ArrayList<String> arrUnLinkedItem = new ArrayList<String>();
	                arrUnLinkedItem.add(ob[0].toString());
	                arrUnLinkedItem.add(ob[1].toString());
	                posName = ob[2].toString();
	                arrUnLinkedItemDtl.add(arrUnLinkedItem);
	            }
	        }

	        if (arrUnLinkedItemDtl.size() > 0)
	        {
	           // JOptionPane.showMessageDialog(null, "some Items needs to be linkup with web stock product code");
	        	objUtilityController.funGenerateLinkupTextfile(arrUnLinkedItemDtl, dateFrom, dateTo, posName,clientCode);
	        }
	        else
	        {

	            String queryDayEnd = "select a.strPOSCode,a.dtePOSDate,a.intShiftCode,ifnull(a.strExciseBillGeneration,'') strExciseBillGeneration,a.strDayEnd,a.strShiftEnd "
	                    + "from tbldayendprocess a "
	                    + "where date(a.dtePOSDate) between '" + dateFrom + "' and '" + dateTo + "' "
	                    + "and a.strDayEnd='Y' "
	                    + "and a.strShiftEnd='Y' "
	                    + "and a.strPOSCode='" + posCode + "' ";
	            List listDayEndData=objBaseServiceImpl.funGetList(new StringBuilder(queryDayEnd), "sql");
	            if(listDayEndData.size()>0)
	            {
	                for(int i=0;i<listDayEndData.size();i++)
		            {
	                	Object obDataEnd[]=(Object[])listDayEndData.get(i);
		                String dayEndPOSCode = obDataEnd[0].toString();//posCode
		                String posDayEndDate = obDataEnd[1].toString();//posDayEndDate
		                String posShiftEndCode =obDataEnd[2].toString();//posShiftEndCode
		                String posDayEndExciseBillGen = obDataEnd[3].toString();//posExciseBillGeneration
		                //*****************************************************************//
		
		                JSONObject rootObject = new JSONObject();
		                JSONArray dataObjectArray = new JSONArray();
		                String sql = "", qFileSql = "";
		                String filter = "";
		                if (dayEndPOSCode.equalsIgnoreCase("All"))
		                {
		                    filter = " and date(a.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' ";
		                }
		                else
		                {
		                    filter = " and a.strPOSCode='" + posCode + "' "
		                            + " and c.strPOSCode='" + posCode + "' "
		                            + " and a.intShiftCode='" + posShiftEndCode + "' "
		                            + " and date(a.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' ";
		                }
		//
		//                if (itemType.equals("Both"))
		//                {
		//                    sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		//                            + ",date(a.dteBillDate),'" + gClientCode + "', c.strWSProductCode "
		//                            + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c "
		//                            + "where a.strBillNo=b.strBillNo  and b.strItemCode=c.strItemCode "
		//                            + filter
		//                            + "group by b.strItemCode order by a.dteBillDate ";
		//
		//                    qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		//                            + ",date(a.dteBillDate),'" + gClientCode + "', c.strWSProductCode "
		//                            + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c "
		//                            + "where a.strBillNo=b.strBillNo  and b.strItemCode=c.strItemCode "
		//                            + filter
		//                            + "group by b.strItemCode order by a.dteBillDate ";
		//                }
		//                else if (itemType.equals("Liquor"))
		//                {
		                sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                        + ",date(a.dteBillDate),'" + clientCode + "' ,c.strExciseBrandCode "
		                        + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                        + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                        + "and b.strItemCode=d.strItemCode "
		                        + "and d.strItemType='Liquor'  and b.strItemCode=c.strItemCode "
		                        + filter
		                        + "group by b.strItemCode order by a.dteBillDate ";
		
		                qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		                        + ",date(a.dteBillDate),'" + clientCode + "' ,c.strExciseBrandCode "
		                        + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		                        + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		                        + "and b.strItemCode=d.strItemCode "
		                        + "and d.strItemType='Liquor'  and b.strItemCode=c.strItemCode "
		                        + filter
		                        + "group by b.strItemCode order by a.dteBillDate ";
		//                }
		//                else
		//                {
		//                    sql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		//                            + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		//                            + "from tblbillhd a,tblbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		//                            + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		//                            + "and b.strItemCode=d.strItemCode "
		//                            + "and d.strItemType='Food'  and b.strItemCode=c.strItemCode "
		//                            + filter
		//                            + "group by b.strItemCode order by a.dteBillDate ";
		//
		//                    qFileSql = "select b.strItemCode,b.strItemName,sum(b.dblQuantity),b.dblRate,a.strPOSCode"
		//                            + ",date(a.dteBillDate),'" + gClientCode + "' ,c.strWSProductCode "
		//                            + "from tblqbillhd a,tblqbilldtl b, tblitemmasterlinkupdtl c,tblitemmaster d "
		//                            + "where a.strBillNo=b.strBillNo and b.strItemCode=c.strItemCode "
		//                            + "and b.strItemCode=d.strItemCode "
		//                            + "and d.strItemType='Food'  and b.strItemCode=c.strItemCode "
		//                            + filter
		//                            + "group by b.strItemCode order by a.dteBillDate";
		//                }
		//                //System.out.println(sql);
		                //System.out.println(qFileSql);
		
		                List listItemSales=objBaseServiceImpl.funGetList(new StringBuilder(sql), "sql");
		                if(listItemSales.size()>0)
		                {
		                	for(int j=0;j<listItemSales.size();j++)
			                {
		                		
		                    	Object obItemSale[]=(Object[])listItemSales.get(i);

			                    JSONObject dataObject = new JSONObject();
			                    dataObject.put("posItemCode",obItemSale[0].toString());
			                    dataObject.put("posItemName", obItemSale[1].toString());
			                    dataObject.put("quantity",obItemSale[2].toString());
			                    dataObject.put("rate", obItemSale[3].toString());
			                    dataObject.put("posCode", obItemSale[4].toString());
			                    dataObject.put("billDate", obItemSale[5].toString());
			                    dataObject.put("clientCode", obItemSale[6].toString());
			                    dataObject.put("exciseBrandCode",obItemSale[7].toString());
			
			                    dataObjectArray.add(dataObject);
			                }

		                }
		                 listItemSales=objBaseServiceImpl.funGetList(new StringBuilder(qFileSql), "sql");
		                if(listItemSales.size()>0)
		                {
		                	for(int j=0;j<listItemSales.size();j++)
			                {
		                		
		                    	Object obItemSale[]=(Object[])listItemSales.get(i);

			                    JSONObject dataObject = new JSONObject();
			                    dataObject.put("posItemCode",obItemSale[0].toString());
			                    dataObject.put("posItemName", obItemSale[1].toString());
			                    dataObject.put("quantity",obItemSale[2].toString());
			                    dataObject.put("rate", obItemSale[3].toString());
			                    dataObject.put("posCode", obItemSale[4].toString());
			                    dataObject.put("billDate", obItemSale[5].toString());
			                    dataObject.put("clientCode", obItemSale[6].toString());
			                    dataObject.put("exciseBrandCode",obItemSale[7].toString());
			
			                    dataObjectArray.add(dataObject);
			                }

		                }
		              
		                if (dataObjectArray.size() > 0)
		                {
		                    rootObject.put("MemberPOSSalesInfo", dataObjectArray);
		                    rootObject.put("exciseLicencceCode", exciseLicencceCode);
		                    rootObject.put("DayEndExciseBillGen", posDayEndExciseBillGen);
		                    String gSanguineWebServiceURLL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,posCode, "gSanguineWebServiceURL");
			        		//localhost:8080/prjSanguineWebService/ExciseIntegration/funPostPOSSaleData
		                    String hoURL = gSanguineWebServiceURL + "/ExciseIntegrationAuto/funPostExciseSaleDataAuto";
		
		                    URL url = new URL(hoURL);
		                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		                    conn.setDoOutput(true);
		                    conn.setRequestMethod("POST");
		                    conn.setRequestProperty("Content-Type", "application/json");
		                    OutputStream os = conn.getOutputStream();
		                    os.write(rootObject.toString().getBytes());
		                    os.flush();
		
		                    if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
		                    {
		                        throw new RuntimeException("Failed : HTTP error code : "
		                                + conn.getResponseCode());
		                    }
		                    BufferedReader br = new BufferedReader(new InputStreamReader(
		                            (conn.getInputStream())));
		
		                    String output = "", op = "Updated successfully:";
		                    while ((output = br.readLine()) != null)
		                    {
		                        op += output;
		                    }
		
		                    //System.out.println(op);
		                    exciseBillNo = op.split(":")[1];
		                    //System.out.println("wsStockAdjustmentCode:"+WSStockAdjustmentCode);
		                    conn.disconnect();
		                    //***********************update day end WebStocks Adjustment*******************************//
		                    String queryUpdateDayEndWSAdjNo = "update tbldayendprocess "
		                            + "set strExciseBillGeneration='" + exciseBillNo + "' "
		                            + "where strPOSCode='" + posCode + "' "
		                            + "and dtePOSDate='" + posDayEndDate + "' "
		                            + "and intShiftCode='" + posShiftEndCode + "' ";
		                   
		                    int k = objBaseServiceImpl.funExecuteUpdate(queryUpdateDayEndWSAdjNo, "sql");
		
		                    /*
		                     if(op.split(":")[1]!=null && op.split(":")[1].trim().equalsIgnoreCase("true"))
		                     {
		                     flgResult = true;
		                     }*/
		                    if (!(op.equals("NA")))
		                    {
		                        flgResult = true;
		                    }
		//                if (flgResult)
		//                {
		//                    String updateLiveBillDtl = "update tblbilldtl a join "
		//                            + " ( select b.strBillNo as BillNo,b.strPOSCode as POSCode"
		//                            + ", b.dteBillDate as BillDate from tblbillhd b "
		//                            + " where date(b.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                            + " and a.intShiftCode='"+posShiftEndCode+"' ) c ,tblitemmaster d   "
		//                            + " on a.strbillno=c.BillNo "
		//                            + " set a.strMMSDataPostFlag='Y' "
		//                            + " where date(c.BillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                            + " and a.strItemCode=d.strItemCode "
		//                            + " and c.intShiftCode='"+posShiftEndCode+"' ";
		//                    
		//                    if (itemType.equalsIgnoreCase("FOOD"))
		//                    {
		//                        updateLiveBillDtl += " and d.strItemType='FOOD' ";
		//                    }
		//                    if (itemType.equalsIgnoreCase("Liquor"))
		//                    {
		//                        updateLiveBillDtl += " and d.strItemType='Liquor' ";
		//                    }
		//                    if (!posCode.equalsIgnoreCase("All"))
		//                    {
		//                        updateLiveBillDtl += " and c.POSCode='" + posCode + "' ";
		//                    }
		//                    dbMysql.execute(updateLiveBillDtl);
		//
		//                    String updateQFileBillDtl = "update tblqbilldtl a join "
		//                            + " ( select b.strBillNo as BillNo,b.strPOSCode as POSCode"
		//                            + ", b.dteBillDate as BillDate from tblqbillhd b "
		//                            + " where date(b.dteBillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                            + " and a.intShiftCode='"+posShiftEndCode+"' ) c ,tblitemmaster d "
		//                            + " on a.strbillno=c.BillNo "
		//                            + " set a.strMMSDataPostFlag='Y' "
		//                            + " where date(c.BillDate) between '" + posDayEndDate + "' and '" + posDayEndDate + "' "
		//                            + " and a.strItemCode=d.strItemCode "
		//                            + " and c.intShiftCode='"+posShiftEndCode+"' ";
		//                    
		//                                            
		//                    if (itemType.equalsIgnoreCase("FOOD"))
		//                    {
		//                        updateQFileBillDtl += " and d.strItemType='FOOD' ";
		//                    }
		//                    if (itemType.equalsIgnoreCase("Liquor"))
		//                    {
		//                        updateQFileBillDtl += " and d.strItemType='Liquor' ";
		//                    }
		//
		//                    if (!posCode.equalsIgnoreCase("All"))
		//                    {
		//                        updateQFileBillDtl += " and c.POSCode='" + posCode + "' ";
		//                    }
		//                    dbMysql.execute(updateQFileBillDtl);
		//                    
		//                    
		//                   
		//                    
		//                }
		
		                }
		
		            }
		        }
	       }

	    }
	    catch (Exception e)
	    {
	        flgResult = false;
	        exciseBillNo = "";
	        e.printStackTrace();
	    }
	    finally
	    {
	        return exciseBillNo;
	    }
	}

    public int funPostSalesDataToHOInBulk(String formName,String clientCode,String POSCode)
    {
    try
    {
        funPostSalesDataPart1(clientCode,POSCode);
        funPostBillSeriesDtlData(formName,clientCode,POSCode);
        funPostAdvOrderHdData(formName,clientCode,POSCode);
        funPostAdvOrderDtlData(formName,clientCode,POSCode);
        funPostAdvOrderCharDtlData(formName,clientCode,POSCode);
        funPostAdvOrderModifierDtlData(formName,clientCode,POSCode);
        funPostAdvReceiptHdData(formName,clientCode,POSCode);
        funPostAdvReceiptDtlData(formName,clientCode,POSCode);
        funPostHomeDeliveryData(formName,clientCode,POSCode);
        funPostHomeDeliveryDtlData(formName,clientCode,POSCode);
    }
    
    catch (Exception e)
    {
        System.out.println(e.getMessage());
        if (e.getMessage().equals("Connection refused: connect"))
        {
        	/*JSONObject jsHOPOSType = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
		String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();*/
        	
        		gConnectionActive = "N";
           // JOptionPane.showMessageDialog(null, "Connection is lost to HO please check!!!");
        }
        else
        {
            //JOptionPane.showMessageDialog(null, "Error while posting data to HO!!!");
        }
        e.printStackTrace();
    }
    return 1;
}	

	public int funPostSalesDataPart1(String clientCode,String POSCode) throws Exception
		{
		    boolean flgDataForPosting = false;
		    String query = "select * from tblbillhd where strDataPostFlag='N' "
		            + "and strBillNo IN(select strBillNo from tblbillsettlementdtl) limit 2000";
		
		    List list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		    JSONObject objJson = new JSONObject();
		    JSONArray arrObjBillHd = new JSONArray();
		    String updateBills = "";
		    if(list.size()>0)
		    {
			        for(int i=0;i<list.size();i++)
			    {
			        JSONObject objRows = new JSONObject();
			        
			        Object ob[]=(Object[])list.get(i);
			        
			        updateBills += ",'" + ob[0].toString() + "'";
			
			        objRows.put("BillNo", ob[0].toString());
			        objRows.put("AdvBookingNo", ob[1].toString());
			        objRows.put("BillDate", ob[2].toString());
			        objRows.put("POSCode", ob[3].toString());
			        objRows.put("SettelmentMode", ob[4].toString());
			        objRows.put("DiscountAmt", ob[5].toString());
			        objRows.put("DiscountPer",ob[6].toString());
			        objRows.put("TaxAmt", ob[7].toString());
			        objRows.put("SubTotal", ob[8].toString());
			        objRows.put("GrandTotal", ob[9].toString());
			        objRows.put("TakeAway", ob[10].toString());
			
			        objRows.put("OperationType", ob[11].toString());
			        objRows.put("UserCreated", ob[12].toString());
			        objRows.put("UserEdited", ob[13].toString());
			        objRows.put("DateCreated",ob[14].toString());
			        objRows.put("DateEdited", ob[15].toString());
			        objRows.put("ClientCode", ob[16].toString());
			
			        objRows.put("TableNo", ob[17].toString());
			        objRows.put("WaiterNo", ob[18].toString());
			        objRows.put("CustomerCode", ob[19].toString());
			        objRows.put("ManualBillNo", ob[20].toString());
			        objRows.put("ShiftCode", ob[21].toString());
			        objRows.put("PaxNo", ob[22].toString());
			
			        objRows.put("DataPostFlag", ob[23].toString());
			        objRows.put("ReasonCode", ob[24].toString());
			        objRows.put("Remarks", ob[25].toString());
			        objRows.put("TipAmount", ob[26].toString());
			        objRows.put("SettleDate", ob[27].toString());
			        objRows.put("CounterCode", ob[28].toString());
			        objRows.put("DeliveryCharges", ob[29].toString());
			        objRows.put("CouponCode", ob[30].toString());
			        objRows.put("AreaCode", ob[31].toString());
			        objRows.put("DiscountRemark", ob[32].toString());
			        objRows.put("TakeAwayRemark", ob[33].toString());
			        objRows.put("DiscountOn", ob[34].toString());
			        objRows.put("CardNo", ob[35].toString());
			        objRows.put("TransType", ob[36].toString());
			
			        arrObjBillHd.add(objRows);
			        flgDataForPosting = true;
			    }
		    }
		   
		    objJson.put("BillHdInfo", arrObjBillHd);
		
		    query = "select * from tblbilldtl where strDataPostFlag='N' "
		            + "and strBillNo IN(select strBillNo from tblbillsettlementdtl) limit 2000";
		    
		     list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		     JSONArray arrObjBillDtl = new JSONArray();
		    String updateBillDtl = "";
		    if(list.size()>0)
		    {
		    	  for(int i=0;i<list.size();i++)
				    {
				        JSONObject objRows = new JSONObject();
				        
				        Object ob[]=(Object[])list.get(i);
			        updateBillDtl += ",'" + ob[2].toString() + "'";
			
			        objRows.put("ItemCode", ob[0].toString());
			        objRows.put("ItemName", ob[1].toString());
			        objRows.put("BillNo", ob[2].toString());
			        objRows.put("AdvBookingNo", ob[3].toString());
			        objRows.put("Rate", ob[4].toString());
			        objRows.put("Quantity",ob[5].toString());
			        objRows.put("Amount", ob[6].toString());
			        objRows.put("TaxAmount", ob[7].toString());
			        objRows.put("BillDate", ob[8].toString());
			        objRows.put("KOTNo", ob[9].toString());
			        objRows.put("ClientCode", ob[10].toString());
			        objRows.put("CustomerCode", ob[11].toString());
			        objRows.put("OrderProcessing",ob[12].toString());
			        objRows.put("DataPostFlag", ob[13].toString());
			        objRows.put("MMSDataPostFlag",ob[14].toString());
			        objRows.put("ManualKOTNo", ob[15].toString());
			        objRows.put("tdhYN",ob[16].toString());
			        objRows.put("PromoCode",ob[17].toString());
			        objRows.put("CounterCode", ob[18].toString());
			        objRows.put("WaiterNo",ob[19].toString());
			        objRows.put("DiscountAmt", ob[20].toString());
			        objRows.put("DiscountPer", ob[21].toString());
			
			        //System.out.println("qBill=" + rs.getString("strBillNo"));
			        arrObjBillDtl.add(objRows);
			        flgDataForPosting = true;
			    }

		}
		    objJson.put("BillDtlInfo", arrObjBillDtl);
		
		    String updateBillModDtl = "";
		    query = "select * from tblbillmodifierdtl where strDataPostFlag='N' "
		            + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
		    	
		     list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		     JSONArray arrObjBillModifierDtl = new JSONArray();
		     if(list.size()>0)
			    {
		    	  for(int i=0;i<list.size();i++)
				    {
		    		    Object ob[]=(Object[])list.get(i);
				        JSONObject objRows = new JSONObject();
				        updateBillModDtl += ",'" + ob[0].toString() + "'";
				        objRows.put("BillNo", ob[0].toString());
				        objRows.put("ItemCode", ob[1].toString());
				        objRows.put("ModifierCode", ob[2].toString());//rs.getString("strModifierCode"));
				        objRows.put("ModifierName", ob[3].toString());//rs.getString("strModifierName"));
				        objRows.put("Rate", ob[4].toString());//rs.getString("dblRate"));
				        objRows.put("Quantity",ob[5].toString()); //rs.getString("dblQuantity"));
				        objRows.put("Amount", ob[6].toString());//rs.getString("dblAmount"));
				        objRows.put("ClientCode",ob[7].toString()); ///.getString("strClientCode"));
				        objRows.put("CustomerCode", ob[8].toString());//rs.getString("strCustomerCode"));
				        objRows.put("DataPostFlag",ob[9].toString());// rs.getString("strDataPostFlag"));
				        objRows.put("MMSDataPostFlag",ob[10].toString());// rs.getString("strMMSDataPostFlag"));
				        objRows.put("DefaultModifierDeselectedYN",ob[11].toString());// rs.getString("strDefaultModifierDeselectedYN"));
				        objRows.put("strSequenceNo",ob[12].toString());// rs.getString("strSequenceNo"));
				        objRows.put("dblDiscPer",ob[13].toString());// rs.getString("dblDiscPer"));
				        objRows.put("dblDiscAmt", ob[14].toString());//rs.getString("dblDiscAmt"));
				
				        arrObjBillModifierDtl.add(objRows);
				        flgDataForPosting = true;
			    }
			}
		     objJson.put("BillModifierDtl", arrObjBillModifierDtl);
		
		    query = "select * from tblbilldiscdtl where strDataPostFlag='N' "
		            + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
		    list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		    JSONArray arrObjBillDiscDtl = new JSONArray();
		
		    String updateBillDiscDtl = "";
		    if(list.size()>0)
		    {
	    	  for(int i=0;i<list.size();i++)
			    {
	    		  Object ob[]=(Object[])list.get(i);
				        JSONObject objRows = new JSONObject();
				        updateBillDiscDtl += ",'" +  ob[0].toString() + "'";
				        objRows.put("BillNo", ob[0].toString());//
				        objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
				        objRows.put("DiscAmt",ob[2].toString());// rs.getString("dblDiscAmt"));
				        objRows.put("DiscPer", ob[3].toString());//rs.getString("dblDiscPer"));
				        objRows.put("DiscOnAmt", ob[4].toString());//rs.getString("dblDiscOnAmt"));
				        objRows.put("DiscOnType",ob[5].toString());// rs.getString("strDiscOnType"));
				        objRows.put("DiscOnValue",ob[6].toString());// rs.getString("strDiscOnValue"));
				        objRows.put("DiscOnReasonCode", ob[7].toString());//rs.getString("strDiscReasonCode"));
				        objRows.put("DiscRemarks",ob[8].toString());// rs.getString("strDiscRemarks"));
				        objRows.put("UserCreated",ob[9].toString());// rs.getString("strUserCreated"));
				        objRows.put("UserEdited",ob[10].toString());// rs.getString("strUserEdited"));
				        objRows.put("DateCreated",ob[11].toString());// rs.getString("dteDateCreated"));
				        objRows.put("DateEdited", ob[12].toString());//rs.getString("dteDateEdited"));
				        objRows.put("ClientCode", ob[13].toString());//rs.getString("strClientCode"));
				        
				        arrObjBillDiscDtl.add(objRows);
				        flgDataForPosting = true;
				    }
			}
		    
		    objJson.put("BillDiscountDtl", arrObjBillDiscDtl);
		
		    String updateBillTaxDtl = "";
		    query = "select * from tblbilltaxdtl where strDataPostFlag='N' "
		            + "and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
		     list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		    
		    JSONArray arrObjBillTaxDtl = new JSONArray();
		    if(list.size()>0)
		    {
	    	  for(int i=0;i<list.size();i++)
			    {
	    		  Object ob[]=(Object[])list.get(i);
		  
			        JSONObject objRows = new JSONObject();
			        updateBillTaxDtl += ",'" + ob[0].toString()+ "'";
			
			        objRows.put("BillNo", ob[0].toString());//
			        objRows.put("TaxCode",  ob[1].toString());//rs.getString("strTaxCode"));
			        objRows.put("TaxableAmount", ob[2].toString());// rs.getString("dblTaxableAmount"));
			        objRows.put("TaxAmount", ob[3].toString());// rs.getString("dblTaxAmount"));
			        objRows.put("ClientCode", ob[4].toString());// rs.getString("strClientCode"));
			        objRows.put("DataPostFlag", ob[5].toString());// rs.getString("strDataPostFlag"));
			        arrObjBillTaxDtl.add(objRows);
			        flgDataForPosting = true;
			    }
		    }
		   
		    objJson.put("BillTaxDtl", arrObjBillTaxDtl);
		
		    String updateBillComplDtl = "";
		    query = "select * from tblbillcomplementrydtl "
		            + "where strDataPostFlag='N' and strBillNo IN(select strBillNo from tblbillsettlementdtl)";
		
		    list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		    
		    JSONArray arrObjBillComplDtl = new JSONArray();
		    if(list.size()>0)
		    {
	    	  for(int i=0;i<list.size();i++)
			    {
	    		  Object ob[]=(Object[])list.get(i);
				   
				        JSONObject objRows = new JSONObject();
				        updateBillComplDtl += ",'" +  ob[2].toString()+ "'";
				        objRows.put("ItemCode",  ob[0].toString());//
				        objRows.put("ItemName", ob[1].toString());//rs.getString("strItemName"));
				        objRows.put("BillNo", ob[2].toString());//rs.getString("strBillNo"));
				        objRows.put("AdvBookingNo",ob[3].toString());// rs.getString("strAdvBookingNo"));
				        objRows.put("Rate",ob[4].toString());// rs.getString("dblRate"));
				        objRows.put("Quantity",ob[5].toString());// rs.getString("dblQuantity"));
				        objRows.put("Amount", ob[6].toString());// rs.getString("dblAmount"));
				        objRows.put("TaxAmount", ob[7].toString());// rs.getString("dblTaxAmount"));
				        objRows.put("BillDate", ob[8].toString());// rs.getString("dteBillDate"));
				        objRows.put("KOTNo", ob[9].toString());// rs.getString("strKOTNo"));
				        objRows.put("ClientCode", ob[10].toString());// rs.getString("strClientCode"));
				        objRows.put("CustomerCode", ob[11].toString());// rs.getString("strCustomerCode"));
				        objRows.put("OrderProcessing", ob[12].toString());// rs.getString("tmeOrderProcessing"));
				        objRows.put("DataPostFlag", ob[13].toString());// rs.getString("strDataPostFlag"));
				        objRows.put("MMSDataPostFlag", ob[14].toString());// rs.getString("strMMSDataPostFlag"));
				        objRows.put("ManualKOTNo", ob[15].toString());// rs.getString("strManualKOTNo"));
				        objRows.put("tdhYN", ob[16].toString());// rs.getString("tdhYN"));
				        objRows.put("PromoCode", ob[17].toString());// rs.getString("strPromoCode"));
				        objRows.put("CounterCode", ob[18].toString());// rs.getString("strCounterCode"));
				        objRows.put("WaiterNo", ob[19].toString());// rs.getString("strWaiterNo"));
				        objRows.put("DiscountAmt", ob[20].toString());// rs.getString("dblDiscountAmt"));
				        objRows.put("DiscountPer", ob[21].toString());// rs.getString("dblDiscountPer"));
				
				        arrObjBillComplDtl.add(objRows);
				        flgDataForPosting = true;
				    }
		    }
		    objJson.put("BillComplimentryDtl", arrObjBillComplDtl);
		
		    query = "select * from tblbillsettlementdtl where strDataPostFlag='N' limit 2000 ";
		    list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
		    JSONArray arrObjBillSettleDtl = new JSONArray();
		    String updateBillSettlementDtl = "";
		
		    if(list.size()>0)
		    {
	    	  for(int i=0;i<list.size();i++)
				    {
			    		  Object ob[]=(Object[])list.get(i);
				        JSONObject objRows = new JSONObject();
				        updateBillSettlementDtl += ",'" +  ob[0].toString() + "'";
				        objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
				        objRows.put("SettlementCode",ob[1].toString());// rs.getString("strSettlementCode"));
				        objRows.put("SettlementAmt",ob[2].toString());// rs.getString("dblSettlementAmt"));
				        objRows.put("PaidAmt",ob[3].toString());// rs.getString("dblPaidAmt"));
				        objRows.put("ExpiryDate",ob[4].toString());// rs.getString("strExpiryDate"));
				        objRows.put("CardName",ob[5].toString());// rs.getString("strCardName"));
				        objRows.put("Remark",ob[6].toString());// rs.getString("strRemark"));
				        objRows.put("ClientCode",ob[7].toString());// rs.getString("strClientCode"));
				        objRows.put("CustomerCode",ob[8].toString());// rs.getString("strCustomerCode"));
				        objRows.put("ActualAmt",ob[9].toString());// rs.getString("dblActualAmt"));
				        objRows.put("RefundAmt",ob[10].toString());// rs.getString("dblRefundAmt"));
				        objRows.put("GiftVoucherCode",ob[11].toString());// rs.getString("strGiftVoucherCode"));
				        objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
				        objRows.put("FolioNo",ob[13].toString());// rs.getString("strFolioNo"));
				        objRows.put("RoomNo",ob[14].toString());// rs.getString("strRoomNo"));
				
				        arrObjBillSettleDtl.add(objRows);
				        flgDataForPosting = true;
			     }
		    }
		    objJson.put("BillSettlementDtl", arrObjBillSettleDtl);
		
		    
		    if (flgDataForPosting)
		    {
		    	gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gSanguineWebServiceURL");
			    String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostSalesDataToHOPOS";
		        
		        URL url = new URL(hoURL);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setDoOutput(true);
		        conn.setRequestMethod("POST");
		        conn.setRequestProperty("Content-Type", "application/json");
		        OutputStream os = conn.getOutputStream();
		        os.write(objJson.toString().getBytes());
		        os.flush();
		        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
		        {
		            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		        }
		        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		        String output = "", op = "";
		
		        while ((output = br.readLine()) != null)
		        {
		            op += output;
		        }
		        System.out.println("BillData=" + op);
		        conn.disconnect();
		
		        String[] arrSalesTable = op.split(",");
		        Map<String, String> hm = new HashMap<String, String>();
		        for (int cn = 0; cn < arrSalesTable.length; cn++)
		        {
		            if (!arrSalesTable[cn].trim().isEmpty())
		            {
		                hm.put(arrSalesTable[cn].trim(), arrSalesTable[cn].trim());
		            }
		        }
		
		        if (hm.size() > 0)
		        {
		            StringBuilder sbUpdate = new StringBuilder(updateBills);
		            if (updateBills.length() > 0)
		            {
		                if (hm.containsKey("BillHd"))
		                {
		                    updateBills = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                  objBaseServiceImpl.funExecuteUpdate("update tblbillhd set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")", "sql");
		                 //   dbMysql.execute("update tblbillhd set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillDtl);
		            if (updateBillDtl.length() > 0)
		            {
		                if (hm.containsKey("BillDtl"))
		                {
		                    updateBillDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbilldtl set strDataPostFlag='Y' where strBillNo in (" + updateBillDtl + ")", "sql");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillModDtl);
		            if (updateBillModDtl.length() > 0)
		            {
		                if (hm.containsKey("BillModDtl"))
		                {
		                    updateBillModDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbillmodifierdtl set strDataPostFlag='Y' where strBillNo in (" + updateBillModDtl + ")", "sql");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillDiscDtl);
		            if (updateBillDiscDtl.length() > 0)
		            {
		                if (hm.containsKey("BillDiscDtl"))
		                {
		                    updateBillDiscDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbilldiscdtl set strDataPostFlag='Y' where strBillNo in (" + updateBillDiscDtl + ")", "sql");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillTaxDtl);
		            if (updateBillTaxDtl.length() > 0)
		            {
		                if (hm.containsKey("BillTaxDtl"))
		                {
		                    updateBillTaxDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbilltaxdtl set strDataPostFlag='Y' where strBillNo in (" + updateBillTaxDtl + ")", "sql");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillComplDtl);
		            if (updateBillComplDtl.length() > 0)
		            {
		                if (hm.containsKey("BillComplDtl"))
		                {
		                    updateBillComplDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbillcomplementrydtl set strDataPostFlag='Y' where strBillNo in (" + updateBillComplDtl + ")", "sql");
		                }
		            }
		            sbUpdate = new StringBuilder(updateBillSettlementDtl);
		            if (updateBillSettlementDtl.length() > 0)
		            {
		                if (hm.containsKey("BillSettleDtl"))
		                {
		                    updateBillSettlementDtl = sbUpdate.delete(0, 1).toString();
		                    //System.out.println("Billsss="+updateBills);
		                    objBaseServiceImpl.funExecuteUpdate("update tblbillsettlementdtl set strDataPostFlag='Y' where strBillNo in (" + updateBillSettlementDtl + ")", "sql");
		                }
		            }
		        }
		    }
		
		    return 1;
		}

    private int funPostBillSeriesDtlData(String formName,String clientCode,String POSCode) throws Exception
	    {
	        String updateBills = "";
	        String query = "", queryForCount = "";

	        if (formName.equals("Bill"))
	        {
	            query = "select * from tblbillseriesbilldtl where strDataPostFlag='N' "
	                    + "and strHdBillNo IN (select strBillNo from tblbillsettlementdtl) limit 2000 ";

	            queryForCount = "select count(strHdBillNo) from tblbillseriesbilldtl where strDataPostFlag='N' "
	                    + "and strHdBillNo IN (select strBillNo from tblbillsettlementdtl)";
	        }
	        else if (formName.equals("Day End"))
	        {
	            query = "select * from tblbillseriesbilldtl where strDataPostFlag='N' limit 2000 ";

	            queryForCount = "select count(strHdBillNo) from tblbillseriesbilldtl where strDataPostFlag='N'";
	        }
	        else if (formName.equals("ManuallyLive"))
	        {
	            query = "select b.* from tblbillhd a,tblbillseriesbilldtl b "
	                    + " where a.strBillNo=b.strHdBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and b.strDataPostFlag='N' limit 2000 ";

	            queryForCount = "select count(b.strBillNo) from tblbillhd a,tblbillseriesbilldtl b "
	                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and b.strDataPostFlag='N'";
	        }
	        else if (formName.equals("ManuallyQFile"))
	        {
	            query = "select b.* from tblqbillhd a,tblbillseriesbilldtl b "
	                    + " where a.strBillNo=b.strHdBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and b.strDataPostFlag='N' limit 2000 ";

	            queryForCount = "select count(b.strBillNo) from tblqbillhd a,tblbillseriesbilldtl b "
	                    + " where a.strBillNo=b.strHdBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + " and b.strDataPostFlag='N'";
	        }
	        //System.out.println(query);

	        int count = 0;
	        List listCount=objBaseServiceImpl.funGetList(new StringBuilder(queryForCount), "sql");
	        
	        if (listCount.size()>0)
	        {
	            count = Integer.parseInt(((Object)listCount.get(0)).toString());
	        }
	        

	        if (count > 2000)
	        {
	            count = count / 2000;
	            count = count + 1;
	        }
	        else
	        {
	            count = 1;
	        }
	        System.out.println("Bill Series Dtl Count=" + count);
	        boolean flgDataPosting = false;
	        int rowCount = 0;
	        for (int cnt = 0; cnt < count; cnt++)
	        {
	        	
	        	List list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
	            JSONObject objJson = new JSONObject();
	            JSONArray arrObj = new JSONArray();

	            updateBills = "";
	            if(list.size()>0)
	            {
	            for(int i=0;i<list.size();i++)
		            {
		                JSONObject objRows = new JSONObject();
		                Object ob[]=(Object[])list.get(0);
		                updateBills += ",'" + ob[2].toString() + "'";
	
		                objRows.put("POSCode",ob[0].toString());// rs.getString("strPOSCode"));
		                objRows.put("BillSeries", ob[1].toString());//rs.getString("strBillSeries"));
		                objRows.put("HdBillNo", ob[2].toString());//rs.getString("strHdBillNo"));
		                objRows.put("DtlBillNo", ob[3].toString());//rs.getString("strDtlBillNos"));
		                objRows.put("GrandTotal", ob[4].toString());//rs.getString("dblGrandTotal"));
		                objRows.put("ClientCode",ob[5].toString());// rs.getString("strClientCode"));
		                objRows.put("DataPostFlag", ob[6].toString());//rs.getString("strDataPostFlag"));
		                objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
		                objRows.put("CreatedDate",ob[8].toString());// rs.getString("dteCreatedDate"));
		                objRows.put("UserEdited", ob[9].toString());//rs.getString("strUserEdited"));
		                objRows.put("EditedDate",ob[10].toString());// rs.getString("dteEditedDate"));
	
		                //System.out.println("qBill=" + rs.getString("strBillNo"));
		                arrObj.add(objRows);
	
		                rowCount++;
		            }
	        	}
	            
	            objJson.put("BillSeriesDtlInfo", arrObj);
	            gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gSanguineWebServiceURL");
				String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

	            URL url = new URL(hoURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            os.write(objJson.toString().getBytes());
	            os.flush();
	            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
	            {
	                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	            }
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "", op = "";

	            while ((output = br.readLine()) != null)
	            {
	                op += output;
	            }
	            System.out.println("BillSeriesDtl= " + op);
	            conn.disconnect();
	            if (op.equals("true"))
	            {
	                StringBuilder sbUpdate = new StringBuilder(updateBills);

	                if (updateBills.length() > 0)
	                {
	                    updateBills = sbUpdate.delete(0, 1).toString();

	                    objBaseServiceImpl.funExecuteUpdate("update tblbillseriesbilldtl set strDataPostFlag='Y' where strHdBillNo in (" + updateBills + ") ", "sql");
	                    //dbMysql.execute(qry);

	                    //qry="update tblbillseriesbilldtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ") ";
	                    //dbMysql.execute(qry);
	                }
	                flgDataPosting = true;
	            }
	            else
	            {
	                flgDataPosting = false;
	            }

	            System.out.println("Row Count= " + rowCount);
	        }

	        if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
	        {
	            if (flgDataPosting)
	            {
	              //  JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
	            }
	        }

	        return 1;
	    }

    private int funPostAdvOrderHdData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblqadvbookbillhd where strDataPostFlag='N'";
        List list=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(list.size()>0)
        {
        	for(int i=0;i<list.size();i++)
		        {
        			Object ob[]=(Object[])list.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString() + "'";
		
		            objRows.put("AdvBookingNo", ob[0].toString());
		            objRows.put("AdvBookingDate", ob[1].toString());//2
		            objRows.put("OrderFor",  ob[2].toString());//rs.getString("dteOrderFor"));
		            objRows.put("POSCode", ob[3].toString());// rs.getString("strPOSCode"));
		            objRows.put("SettelmentMode", ob[4].toString());// rs.getString("strSettelmentMode"));
		            objRows.put("DiscountAmt",  ob[5].toString());//rs.getString("dblDiscountAmt"));
		            objRows.put("DiscountPer", ob[6].toString());// rs.getString("dblDiscountPer"));
		            objRows.put("TaxAmt",ob[7].toString());// rs.getString("dblTaxAmt"));
		            objRows.put("SubTotal",ob[8].toString());// rs.getString("dblSubTotal"));
		            objRows.put("GrandTotal",ob[9].toString());// rs.getString("dblGrandTotal"));9
		
		            objRows.put("UserCreated",ob[10].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[11].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[12].toString());// rs.getString("dteDateCreated"));
		            objRows.put("DateEdited",ob[13].toString());// rs.getString("dteDateEdited"));13
		
		            objRows.put("CustomerCode",ob[15].toString());// rs.getString("strCustomerCode"));15
		            objRows.put("ShiftCode",ob[16].toString());// rs.getString("intShiftCode"));
		            objRows.put("Message",ob[17].toString());// rs.getString("strMessage"));
		            objRows.put("Shape",ob[18].toString());// rs.getString("strShape"));
		
		            objRows.put("Note",ob[19].toString());// rs.getString("strNote"));19
		            objRows.put("DeliveryTime",ob[21].toString());// rs.getString("strDeliveryTime"));21
		            objRows.put("WaiterNo",ob[22].toString());// rs.getString("strWaiterNo"));
		            objRows.put("HomeDelivery",ob[23].toString());// rs.getString("strHomeDelivery"));
		            objRows.put("HomeDelCharges",ob[24].toString());// rs.getString("dblHomeDelCharges"));
		            objRows.put("OrderType",ob[25].toString());// rs.getString("strOrderType"));
		            objRows.put("ManualAdvOrderNo",ob[26].toString());// rs.getString("strManualAdvOrderNo"));
		            objRows.put("ImageName",ob[27].toString());// rs.getString("strImageName"));
		            objRows.put("SpecialsymbolImage",ob[28].toString());// rs.getString("strSpecialsymbolImage"));
		            objRows.put("UrgentOrderYN",ob[29].toString());// rs.getString("strUrgentOrder"));29
		
		            objRows.put("ClientCode",ob[14].toString());// rs.getString("strClientCode"));14
		            objRows.put("DataPostFlag",ob[20].toString());// rs.getString("strDataPostFlag"));20
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    	}

        query = "select * from tbladvbookbillhd where strDataPostFlag='N'";
        List qlist =objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        if(qlist.size()>0)
        {
        	for(int i=0;i<qlist.size();i++)
		        {
        		
        		Object ob[]=(Object[])list.get(i);
	            JSONObject objRows = new JSONObject();
	            updateBills += ",'" + ob[0].toString() + "'";
	
	            objRows.put("AdvBookingNo", ob[0].toString());
	            objRows.put("AdvBookingDate", ob[1].toString());//2
	            objRows.put("OrderFor",  ob[2].toString());//rs.getString("dteOrderFor"));
	            objRows.put("POSCode", ob[3].toString());// rs.getString("strPOSCode"));
	            objRows.put("SettelmentMode", ob[4].toString());// rs.getString("strSettelmentMode"));
	            objRows.put("DiscountAmt",  ob[5].toString());//rs.getString("dblDiscountAmt"));
	            objRows.put("DiscountPer", ob[6].toString());// rs.getString("dblDiscountPer"));
	            objRows.put("TaxAmt",ob[7].toString());// rs.getString("dblTaxAmt"));
	            objRows.put("SubTotal",ob[8].toString());// rs.getString("dblSubTotal"));
	            objRows.put("GrandTotal",ob[9].toString());// rs.getString("dblGrandTotal"));9
	
	            objRows.put("UserCreated",ob[10].toString());// rs.getString("strUserCreated"));
	            objRows.put("UserEdited",ob[11].toString());// rs.getString("strUserEdited"));
	            objRows.put("DateCreated",ob[12].toString());// rs.getString("dteDateCreated"));
	            objRows.put("DateEdited",ob[13].toString());// rs.getString("dteDateEdited"));13
	
	            objRows.put("CustomerCode",ob[15].toString());// rs.getString("strCustomerCode"));15
	            objRows.put("ShiftCode",ob[16].toString());// rs.getString("intShiftCode"));
	            objRows.put("Message",ob[17].toString());// rs.getString("strMessage"));
	            objRows.put("Shape",ob[18].toString());// rs.getString("strShape"));
	
	            objRows.put("Note",ob[19].toString());// rs.getString("strNote"));19
	            objRows.put("DeliveryTime",ob[21].toString());// rs.getString("strDeliveryTime"));21
	            objRows.put("WaiterNo",ob[22].toString());// rs.getString("strWaiterNo"));
	            objRows.put("HomeDelivery",ob[23].toString());// rs.getString("strHomeDelivery"));
	            objRows.put("HomeDelCharges",ob[24].toString());// rs.getString("dblHomeDelCharges"));
	            objRows.put("OrderType",ob[25].toString());// rs.getString("strOrderType"));
	            objRows.put("ManualAdvOrderNo",ob[26].toString());// rs.getString("strManualAdvOrderNo"));
	            objRows.put("ImageName",ob[27].toString());// rs.getString("strImageName"));
	            objRows.put("SpecialsymbolImage",ob[28].toString());// rs.getString("strSpecialsymbolImage"));
	            objRows.put("UrgentOrderYN",ob[29].toString());// rs.getString("strUrgentOrder"));29
	
	            objRows.put("ClientCode",ob[14].toString());// rs.getString("strClientCode"));14
	            objRows.put("DataPostFlag",ob[20].toString());// rs.getString("strDataPostFlag"));20
	
	            arrObj.add(objRows);
	            flgDataForPosting = true;
		        }
    	}
        if (flgDataForPosting)
        {
            objJson.put("AdvBookBillHd", arrObj);
            gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gSanguineWebServiceURL");
		
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Order Hd= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvbookbillhd set strDataPostFlag='Y'"
                    		+ " where strAdvBookingNo in (" + updateBills + ")", "sql");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvbookbillhd set strDataPostFlag='Y'"
                    		+ " where strAdvBookingNo in (" + updateBills + ")", "sql");

                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostAdvOrderDtlData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblqadvbookbilldtl where strDataPostFlag='N'";
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        	if(listAdv.size()>0)
        	{
        		for(int i=0;i<listAdv.size();i++)
		        {
        			Object ob[]=(Object [])listAdv.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[2].toString() + "'";
		
		            objRows.put("AdvBookingNo", ob[2].toString());//
		            objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));0
		            objRows.put("ItemName", ob[1].toString());//rs.getString("strItemName"));1
		            objRows.put("Quantity", ob[3].toString());//rs.getString("dblQuantity"));3
		            objRows.put("Weight", ob[10].toString());//rs.getString("dblWeight"));10
		            objRows.put("Amount", ob[4].toString());//rs.getString("dblAmount"));4
		            objRows.put("TaxAmount",ob[5].toString());// rs.getString("dblTaxAmount"));
		            objRows.put("AdvBookingDate",ob[6].toString());// rs.getString("dteAdvBookingDate"));
		            objRows.put("OrderFor",ob[7].toString());// rs.getString("dteOrderFor"));
		            objRows.put("CustomerCode", ob[9].toString());//rs.getString("strCustomerCode"));9
		            objRows.put("ClientCode",ob[8].toString());// rs.getString("strClientCode"));8
		            objRows.put("DataPostFlag",ob[11].toString());// rs.getString("strDataPostFlag"));11
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
        
    		}

        query = "select * from tbladvbookbilldtl where strDataPostFlag='N'";
        listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
	        {
    			Object ob[]=(Object [])listAdv.get(i);
	            JSONObject objRows = new JSONObject();
	            updateBills += ",'" + ob[2].toString() + "'";
	
	            objRows.put("AdvBookingNo", ob[2].toString());//
	            objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));0
	            objRows.put("ItemName", ob[1].toString());//rs.getString("strItemName"));1
	            objRows.put("Quantity", ob[3].toString());//rs.getString("dblQuantity"));3
	            objRows.put("Weight", ob[10].toString());//rs.getString("dblWeight"));10
	            objRows.put("Amount", ob[4].toString());//rs.getString("dblAmount"));4
	            objRows.put("TaxAmount",ob[5].toString());// rs.getString("dblTaxAmount"));
	            objRows.put("AdvBookingDate",ob[6].toString());// rs.getString("dteAdvBookingDate"));
	            objRows.put("OrderFor",ob[7].toString());// rs.getString("dteOrderFor"));
	            objRows.put("CustomerCode", ob[9].toString());//rs.getString("strCustomerCode"));9
	            objRows.put("ClientCode",ob[8].toString());// rs.getString("strClientCode"));8
	            objRows.put("DataPostFlag",ob[11].toString());// rs.getString("strDataPostFlag"));11
	
	            arrObj.add(objRows);
	            flgDataForPosting = true;
	        }
    	}
        

        if (flgDataForPosting)
        {
            objJson.put("AdvBookBillDtl", arrObj);
            gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,POSCode, "gSanguineWebServiceURL");
			 
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Order Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvbookbilldtl set strDataPostFlag='Y' where strAdvBookingNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvbookbilldtl set strDataPostFlag='Y' where strAdvBookingNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostAdvOrderCharDtlData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tbladvbookbillchardtl where strDataPostFlag='N'";
        
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
       
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
       if(listAdv.size()>0)
        {
    	   for(int i=0;i<listAdv.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listAdv.get(i);
		            
		            updateBills += ",'" + ob[1].toString() + "'";
		
		            objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
		            objRows.put("ItemCode", ob[0].toString());//rs.getString("strItemCode"));
		            objRows.put("CharCode", ob[2].toString());//rs.getString("strCharCode"));
		            objRows.put("CharValues", ob[3].toString());//rs.getString("strCharValues"));
		            objRows.put("ClientCode", ob[4].toString());//rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[5].toString()); //rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    		}

        query = "select * from tblqadvbookbillchardtl where strDataPostFlag='N'";
        listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
         if(listAdv.size()>0)
         {
     	   for(int i=0;i<listAdv.size();i++)
 		        {
 		            JSONObject objRows = new JSONObject();
 		            Object ob[]=(Object[])listAdv.get(i);
 		            
 		            updateBills += ",'" + ob[1].toString() + "'";
 		
 		            objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
 		            objRows.put("ItemCode", ob[0].toString());//rs.getString("strItemCode"));
 		            objRows.put("CharCode", ob[2].toString());//rs.getString("strCharCode"));
 		            objRows.put("CharValues", ob[3].toString());//rs.getString("strCharValues"));
 		            objRows.put("ClientCode", ob[4].toString());//rs.getString("strClientCode"));
 		            objRows.put("DataPostFlag",ob[5].toString()); //rs.getString("strDataPostFlag"));
 		
 		            arrObj.add(objRows);
 		            flgDataForPosting = true;
 		        }
     	}

        if (flgDataForPosting)
        {
            objJson.put("AdvBookBillCharDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Order Char Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvbookbillchardtl set strDataPostFlag='Y' where strAdvBookingNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvbookbillchardtl set strDataPostFlag='Y' where strAdvBookingNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                  //  JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }
    
    private int funPostAdvOrderModifierDtlData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblqadvordermodifierdtl where strDataPostFlag='N'";
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
  
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listAdv.size()>0)	
        {
        	 for(int i=0;i<listAdv.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listAdv.get(i);
		           
		            updateBills += ",'" + ob[0].toString() + "'";
		
		            objRows.put("AdvBookingNo",ob[0].toString());// rs.getString("strAdvOrderNo"));
		            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
		            objRows.put("ModifierCode",ob[2].toString());// rs.getString("strModifierCode"));
		            objRows.put("ModifierName",ob[3].toString());// rs.getString("strModifierName"));
		            objRows.put("Quantity",ob[4].toString());// rs.getString("dblQuantity"));
		            objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
		            objRows.put("ClientCode",ob[6].toString());// rs.getString("strClientCode"));
		            objRows.put("CustomerCode",ob[7].toString());// rs.getString("strCustomerCode"));
		            objRows.put("UserCreated",ob[8].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[9].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[10].toString());// rs.getString("dteDateCreated"));
		            objRows.put("DateEdited",ob[11].toString());// rs.getString("dteDateEdited"));
		            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    		}

        query = "select * from tbladvordermodifierdtl where strDataPostFlag='N'";
        listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        if(listAdv.size()>0)
         {
        	 for(int i=0;i<listAdv.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listAdv.get(i);
		           
		            updateBills += ",'" + ob[0].toString() + "'";
		
		            objRows.put("AdvBookingNo",ob[0].toString());// rs.getString("strAdvOrderNo"));
		            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
		            objRows.put("ModifierCode",ob[2].toString());// rs.getString("strModifierCode"));
		            objRows.put("ModifierName",ob[3].toString());// rs.getString("strModifierName"));
		            objRows.put("Quantity",ob[4].toString());// rs.getString("dblQuantity"));
		            objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
		            objRows.put("ClientCode",ob[6].toString());// rs.getString("strClientCode"));
		            objRows.put("CustomerCode",ob[7].toString());// rs.getString("strCustomerCode"));
		            objRows.put("UserCreated",ob[8].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[9].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[10].toString());// rs.getString("dteDateCreated"));
		            objRows.put("DateEdited",ob[11].toString());// rs.getString("dteDateEdited"));
		            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
    	}
       

        if (flgDataForPosting)
        {
            objJson.put("AdvBookBillModifierDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Order Mod Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvordermodifierdtl set strDataPostFlag='Y' where strAdvOrderNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvordermodifierdtl set strDataPostFlag='Y' where strAdvOrderNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostAdvReceiptHdData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblqadvancereceipthd where strDataPostFlag='N'";
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
       
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listAdv.get(i);
		            updateBills += ",'" + ob[0].toString()+ "'";
		
		            objRows.put("ReceiptNo", ob[0].toString());//rs.getString("strReceiptNo"));
		            objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
		            objRows.put("POSCode",ob[2].toString());// rs.getString("strPOSCode"));
		            objRows.put("SettelmentMode",ob[3].toString());// rs.getString("strSettelmentMode"));
		            objRows.put("ReceiptDate",ob[4].toString());// rs.getString("dtReceiptDate"));
		            objRows.put("AdvDeposite",ob[5].toString());// rs.getString("dblAdvDeposite"));
		            objRows.put("ShiftCode",ob[6].toString());// rs.getString("intShiftCode"));
		            objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[8].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[9].toString());// rs.getString("dtDateCreated"));
		            objRows.put("DateEdited",ob[10].toString());// rs.getString("dtDateEdited"));
		            objRows.put("ClientCode",ob[11].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
        	}

        query = "select * from tbladvancereceipthd where strDataPostFlag='N'";
        listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listAdv.get(i);
		            updateBills += ",'" + ob[0].toString()+ "'";
		
		            objRows.put("ReceiptNo", ob[0].toString());//rs.getString("strReceiptNo"));
		            objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
		            objRows.put("POSCode",ob[2].toString());// rs.getString("strPOSCode"));
		            objRows.put("SettelmentMode",ob[3].toString());// rs.getString("strSettelmentMode"));
		            objRows.put("ReceiptDate",ob[4].toString());// rs.getString("dtReceiptDate"));
		            objRows.put("AdvDeposite",ob[5].toString());// rs.getString("dblAdvDeposite"));
		            objRows.put("ShiftCode",ob[6].toString());// rs.getString("intShiftCode"));
		            objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[8].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[9].toString());// rs.getString("dtDateCreated"));
		            objRows.put("DateEdited",ob[10].toString());// rs.getString("dtDateEdited"));
		            objRows.put("ClientCode",ob[11].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
        	}

        if (flgDataForPosting)
        {
            objJson.put("AdvanceReceiptHd", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Rec Hd= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvancereceipthd set strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvancereceipthd set strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                  //  JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }


    private int funPostAdvReceiptDtlData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblqadvancereceiptdtl where strDataPostFlag='N'";
        
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
	        {
	            JSONObject objRows = new JSONObject();
	            Object ob[]=(Object[])listAdv.get(i);
	            updateBills += ",'" + ob[0].toString() + "'";
	
	            objRows.put("ReceiptNo",ob[0].toString());// rs.getString("strReceiptNo"));
	            objRows.put("SettlementCode",ob[1].toString());// rs.getString("strSettlementCode"));
	            objRows.put("CardNo",ob[2].toString());// rs.getString("strCardNo"));
	            objRows.put("Expirydate",ob[3].toString());// rs.getString("strExpirydate"));
	            objRows.put("ChequeNo",ob[4].toString());// rs.getString("strChequeNo"));
	            objRows.put("ChequeDate",ob[5].toString());// rs.getString("dteCheque"));
	            objRows.put("BankName",ob[6].toString());// rs.getString("strBankName"));
	            objRows.put("AdvDepositesettleAmt",ob[7].toString());// rs.getString("dblAdvDepositesettleAmt"));
	            objRows.put("Remark",ob[8].toString());// rs.getString("strRemark"));
	            objRows.put("PaidAmt",ob[9].toString());// rs.getString("dblPaidAmt"));
	            objRows.put("Installment",ob[12].toString());// rs.getString("dteInstallment"));12
	            objRows.put("ClientCode",ob[10].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[11].toString());// rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	            flgDataForPosting = true;
	        }
        }
       

        query = "select * from tbladvancereceiptdtl where strDataPostFlag='N'";
        
        listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
	        {
	            JSONObject objRows = new JSONObject();
	            Object ob[]=(Object[])listAdv.get(i);
	            updateBills += ",'" + ob[0].toString() + "'";
	
	            objRows.put("ReceiptNo",ob[0].toString());// rs.getString("strReceiptNo"));
	            objRows.put("SettlementCode",ob[1].toString());// rs.getString("strSettlementCode"));
	            objRows.put("CardNo",ob[2].toString());// rs.getString("strCardNo"));
	            objRows.put("Expirydate",ob[3].toString());// rs.getString("strExpirydate"));
	            objRows.put("ChequeNo",ob[4].toString());// rs.getString("strChequeNo"));
	            objRows.put("ChequeDate",ob[5].toString());// rs.getString("dteCheque"));
	            objRows.put("BankName",ob[6].toString());// rs.getString("strBankName"));
	            objRows.put("AdvDepositesettleAmt",ob[7].toString());// rs.getString("dblAdvDepositesettleAmt"));
	            objRows.put("Remark",ob[8].toString());// rs.getString("strRemark"));
	            objRows.put("PaidAmt",ob[9].toString());// rs.getString("dblPaidAmt"));
	            objRows.put("Installment",ob[12].toString());// rs.getString("dteInstallment"));12
	            objRows.put("ClientCode",ob[10].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[11].toString());// rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	            flgDataForPosting = true;
	        }
        }
        if (flgDataForPosting)
        {
            objJson.put("AdvanceReceiptDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Adv Rec Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    objBaseServiceImpl.funExecuteUpdate("update tbladvancereceiptdtl set strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                    objBaseServiceImpl.funExecuteUpdate("update tblqadvancereceiptdtl set strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostHomeDeliveryData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblhomedelivery where strDataPostFlag='N'";
        if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblhomedelivery b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblhomedelivery b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        
        if(listAdv.size()>0)
        {
        	for(int i=0;i<listAdv.size();i++)
				{
        			Object ob[]=(Object[])listAdv.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString() + "'";
		
		            objRows.put("BillNo", ob[0].toString());//rs.getString("strBillNo"));
		            objRows.put("CustomerCode", ob[1].toString());// rs.getString("strCustomerCode"));
		            objRows.put("DPCode", ob[2].toString());// rs.getString("strDPCode"));
		            objRows.put("POSCode", ob[5].toString());// rs.getString("strPOSCode"));5
		            objRows.put("Date", ob[3].toString());// rs.getString("dteDate"));3
		            objRows.put("Time", ob[4].toString());// rs.getString("tmeTime"));4
		            objRows.put("CustAddress1", ob[6].toString());// rs.getString("strCustAddressLine1"));
		            objRows.put("CustAddress2", ob[7].toString());// rs.getString("strCustAddressLine2"));
		            objRows.put("CustAddress3", ob[8].toString());// rs.getString("strCustAddressLine3"));
		            objRows.put("CustAddress4", ob[9].toString());// rs.getString("strCustAddressLine4"));
		            objRows.put("CustCity", ob[10].toString());// rs.getString("strCustCity"));
		            objRows.put("DataPostFlag", ob[11].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("ClientCode", ob[12].toString());// rs.getString("strClientCode"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
        }
 

        if (flgDataForPosting)
        {
            objJson.put("HomeDelivery", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Home Del= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    objBaseServiceImpl.funExecuteUpdate("update tblhomedelivery set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                   // JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    private int funPostHomeDeliveryDtlData(String formName,String clientCode,String POSCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblhomedeldtl where strDataPostFlag='N'";
        if (formName.equals("ManuallyLive"))
        {
            query = "select b.* from tblbillhd a,tblhomedeldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        if (formName.equals("ManuallyQFile"))
        {
            query = "select b.* from tblqbillhd a,tblhomedeldtl b "
                    + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
                    + " and b.strDataPostFlag='N'";
        }
        List listAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        boolean flgDataForPosting = false;
        if(listAdv.size()>0)
        {
		        for(int i=0;i<listAdv.size();i++)
		        {
		        	Object ob[]=(Object[])listAdv.get(i);
		            JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString() + "'";
		
		            objRows.put("BillNo", ob[0].toString());//rs.getString("strBillNo"));
		            objRows.put("DPCode", ob[1].toString());//rs.getString("strDPCode"));
		            objRows.put("ClientCode",ob[2].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[3].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		            flgDataForPosting = true;
		        }
        }
       
        if (flgDataForPosting)
        {
            objJson.put("HomeDeliveryDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Home Del= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblhomedeldtl set strDataPostFlag='Y' "
                            + "where strBillNo in (" + updateBills + ")", "sql");
                }
                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                   // JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    public int funPostPlaceOrderDataToHO(String formName,String clientCode,String PosCode)
    {
        try
        {
            funPostPlaceOrderData(formName,clientCode,PosCode);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection refused: connect"))
            {
                gConnectionActive = "N";
              //  JOptionPane.showMessageDialog(null, "Connection is lost to HO please check!!!");
            }
            else
            {
               // JOptionPane.showMessageDialog(null, "Error while posting data to HO!!!");
            }
            e.printStackTrace();
        }
        return 1;
    }
    
    private int funPostPlaceOrderData(String formName,String clientCode,String PosCode) throws Exception
    {
        String updateOrders = "";
        JSONObject objJson = new JSONObject();
        JSONArray arrObjPlaceOrderHd = new JSONArray();
        String query = "select * from tblplaceorderhd where strDataPostFlag='N'";
        List listPlaceOrd=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        
        boolean flgDataForPosting = false;
        if(listPlaceOrd.size()>0)
        {
        	for(int i=0;i<listPlaceOrd.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listPlaceOrd.get(i);
		            updateOrders += ",'" + ob[0].toString() + "'";
		
		            objRows.put("OrderCode",ob[0].toString());// rs.getString("strOrderCode"));
		            objRows.put("SOCode", ob[1].toString());//rs.getString("strSOCode"));
		            objRows.put("SODate",ob[2].toString());// rs.getString("dteSODate"));
		            objRows.put("OrderDate", ob[3].toString());//rs.getString("dteOrderDate"));
		            objRows.put("UserCreated",ob[4].toString());// rs.getString("strUserCreated"));
		            objRows.put("DateCreated",ob[5].toString());// rs.getString("dteDateCreated"));
		            objRows.put("ClientCode", ob[6].toString());//rs.getString("strClientCode"));
		            objRows.put("CloseSO", ob[7].toString());//rs.getString("strCloseSO"));
		            objRows.put("DCCode",ob[8].toString());// rs.getString("strDCCode"));
		            objRows.put("OrderTypeCode", ob[9].toString());//rs.getString("strOrderTypeCode"));
		            objRows.put("DataPostFlag", ob[10].toString());//rs.getString("strDataPostFlag"));
		            objRows.put("OrderType", ob[11].toString());//rs.getString("strOrderType"));
		
		            arrObjPlaceOrderHd.add(objRows);
		            flgDataForPosting = true;
		        }
        	}

        String updateOrderDtl = "";
        query = " select * from tblplaceorderdtl where strDataPostFlag='N' ";
        listPlaceOrd=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONArray arrObjPlaceOrderDtl = new JSONArray();

        if(listPlaceOrd.size()>0)
        {
        	for(int i=0;i<listPlaceOrd.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listPlaceOrd.get(i);
		            updateOrderDtl += ",'" +  ob[0].toString() + "'";

		            objRows.put("OrderCode",  ob[0].toString());//rs.getString("strOrderCode"));
		            objRows.put("ProductCode", ob[1].toString());//rs.getString("strProductCode"));
		            objRows.put("ItemCode",ob[2].toString());// rs.getString("strItemCode"));
		            objRows.put("Quantity",ob[3].toString());// rs.getString("dblQty"));
		            objRows.put("StockQty", ob[4].toString());//rs.getString("dblStockQty"));
		            objRows.put("ClientCode",ob[5].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag", ob[6].toString());//rs.getString("strDataPostFlag"));
		            objRows.put("AdvOrderNo",ob[7].toString());// rs.getString("strAdvOrderNo"));
		
		            arrObjPlaceOrderDtl.add(objRows);
		            flgDataForPosting = true;
		        }
        }
        

        String updateAdvOrderDtl = "";
        query = " select * from tblplaceorderadvorderdtl where strDataPostFlag='N' ";
        listPlaceOrd=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONArray arrObjPlaceOrderAdvOrderDtl = new JSONArray();

        if(listPlaceOrd.size()>0)
        {
        	for(int i=0;i<listPlaceOrd.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listPlaceOrd.get(i);
		            updateAdvOrderDtl += ",'" + ob[0].toString() + "'";
		
		            objRows.put("AdvOrderNo", ob[0].toString());//rs.getString("strAdvOrderNo"));
		            objRows.put("OrderDate", ob[1].toString());// rs.getString("dteOrderDate"));
		            objRows.put("ClientCode",  ob[2].toString());//rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",  ob[3].toString());//rs.getString("strDataPostFlag"));
		            objRows.put("OrderType", ob[4].toString());// rs.getString("strOrderType"));
		
		            arrObjPlaceOrderAdvOrderDtl.add(objRows);
		            flgDataForPosting = true;
		        }
        }

        if (flgDataForPosting)
        {
            objJson.put("PlaceOrderHd", arrObjPlaceOrderHd);
            objJson.put("PlaceOrderDtl", arrObjPlaceOrderDtl);
            objJson.put("PlaceAdvOrderDtl", arrObjPlaceOrderAdvOrderDtl);
            gSanguineWebServiceURL= objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,PosCode, "gSanguineWebServiceURL");
			
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostPlaceOrderDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Place Order Hd= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateOrders);
                if (updateOrders.length() > 0)
                {
                    updateOrders = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblplaceorderhd set strDataPostFlag='Y' where strOrderCode in (" + updateOrders + ")", "sql");
                }
                sbUpdate = new StringBuilder(updateOrderDtl);
                if (updateOrderDtl.length() > 0)
                {
                    updateOrderDtl = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblplaceorderdtl set strDataPostFlag='Y' where strOrderCode in (" + updateOrderDtl + ")", "sql");
                }
                sbUpdate = new StringBuilder(updateAdvOrderDtl);
                if (updateAdvOrderDtl.length() > 0)
                {
                    updateAdvOrderDtl = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblplaceorderadvorderdtl set strDataPostFlag='Y' where strAdvOrderNo in (" + updateAdvOrderDtl + ")", "sql");
                }

                if (formName.equals("ManuallyLive") || formName.equals("ManuallyQFile"))
                {
                    //JOptionPane.showMessageDialog(null, "Data Posted Successfully!!!");
                }
            }
        }

        return 1;
    }

    
    public int funPostAuditDataToHO(String formName,String clientCode,String PosCode)
    {
        try
        {
            funPostVoidBillHdData(clientCode,PosCode);
            funPostVoidBillDtlData(clientCode,PosCode);
            funPostVoidKOTData();
            funPostVoidBillModifierDtlData(clientCode,PosCode);
            //
            int i = funPostVoidAdvOrderBillHdData();
            i = funPostVoidAdvOrderBillDtlData();
            i = funPostVoidAdvOrderModifierDtlData();
            i = funPostVoidAdvOrderBillCharDtlData();
            i = funPostVoidAdvOrderReceiptHdData();
            i = funPostVoidAdvOrderReceiptDtlData();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }
    private int funPostVoidBillHdData(String clientCode,String PosCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblvoidbillhd where strDataPostFlag='N' ";
        List listVoidBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        	if(listVoidBill.size()>0)
        	{
        		for(int i=0;i<listVoidBill.size();i++)
		        {
		            JSONObject objRows = new JSONObject();
		            Object ob[]=(Object[])listVoidBill.get(i);
		            
		            updateBills += ",'" +ob[3].toString() + "'";
		            objRows.put("BillNo",ob[3].toString());// rs.getString("strBillNo"));;3
		            objRows.put("PosCode",ob[0].toString());// rs.getString("strPosCode"));;
		            objRows.put("ReasonCode",ob[1].toString());// rs.getString("strReasonCode"));;
		            objRows.put("ReasonName",ob[2].toString());// rs.getString("strReasonName"));;
		            objRows.put("ActualAmount",ob[4].toString());// rs.getString("dblActualAmount"));;
		            objRows.put("ModifiedAmount",ob[5].toString());// rs.getString("dblModifiedAmount"));;
		            objRows.put("BillDate",ob[6].toString());// rs.getString("dteBillDate"));;
		            objRows.put("TransType",ob[7].toString());// rs.getString("strTransType"));;
		            objRows.put("ModifyVoidBill",ob[8].toString());// rs.getString("dteModifyVoidBill"));;
		            objRows.put("TableNo",ob[9].toString());// rs.getString("strTableNo"));;
		            objRows.put("WaiterNo",ob[10].toString());// rs.getString("strWaiterNo"));;
		            objRows.put("ShiftCode",ob[11].toString());// rs.getString("intShiftCode"));;
		            objRows.put("UserCreated",ob[12].toString());// rs.getString("strUserCreated"));;
		            objRows.put("UserEdited",ob[13].toString());// rs.getString("strUserEdited"));;
		            objRows.put("ClientCode",ob[14].toString());// rs.getString("strClientCode"));;
		            objRows.put("DataPostFlag",ob[15].toString());// rs.getString("strDataPostFlag"));;
		            objRows.put("Remark",ob[16].toString());// rs.getString("strRemark"));;
		
		            arrObj.add(objRows);
		        }

        	}
        objJson.put("VoidBillHd", arrObj);
        gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,PosCode, "gSanguineWebServiceURL");
		
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Void Bill Hd= " + op);
        conn.disconnect();
        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateBills);
            if (updateBills.length() > 0)
            {
                updateBills = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblvoidbillhd set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")","sql");
            }
        }
        return 1;
    }

    private int funPostVoidBillDtlData(String clientCode,String PosCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblvoidbilldtl where strDataPostFlag='N' ";
        List listVoidBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

    	if(listVoidBill.size()>0)
    	{
    		for(int i=0;i<listVoidBill.size();i++)
	        {
	          
	            Object ob[]=(Object[])listVoidBill.get(i);
	            JSONObject objRows = new JSONObject();
	            updateBills += ",'" + ob[5].toString() + "'";
	            objRows.put("BillNo", ob[5].toString());//rs.getString("strBillNo"));5
	            objRows.put("PosCode",ob[1].toString());// rs.getString("strPosCode"));
	            objRows.put("ReasonCode",ob[2].toString());// rs.getString("strReasonCode"));
	            objRows.put("ReasonName",ob[3].toString());// rs.getString("strReasonName"));
	            objRows.put("ItemCode",ob[4].toString());// rs.getString("strItemCode"));
	            objRows.put("ItemName",ob[5].toString());// rs.getString("strItemCode"));
	            objRows.put("Quantity",ob[6].toString());// rs.getString("intQuantity"));
	            objRows.put("Amount",ob[7].toString());// rs.getString("dblAmount"));
	            objRows.put("TaxAmount",ob[8].toString());// rs.getString("dblTaxAmount"));
	            objRows.put("BillDate",ob[9].toString());// rs.getString("dteBillDate"));
	            objRows.put("TransType",ob[10].toString());// rs.getString("strTransType"));
	            objRows.put("ModifyVoidBill",ob[11].toString());// rs.getString("dteModifyVoidBill"));
	            objRows.put("SettlementCode",ob[12].toString());// rs.getString("strSettlementCode"));
	            objRows.put("SettlementAmt",ob[13].toString());// rs.getString("dblSettlementAmt"));
	            objRows.put("PaidAmt",ob[14].toString());// rs.getString("dblPaidAmt"));
	            objRows.put("TableNo",ob[15].toString());// rs.getString("strTableNo"));
	            objRows.put("WaiterNo",ob[16].toString());// rs.getString("strWaiterNo"));
	            objRows.put("ShiftCode",ob[17].toString());// rs.getString("intShiftCode"));
	            objRows.put("UserCreated",ob[18].toString());// rs.getString("strUserCreated"));
	            objRows.put("ClientCode",ob[19].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[20].toString());// rs.getString("strDataPostFlag"));
	            objRows.put("KOTNo",ob[21].toString());// rs.getString("strKOTNo"));
	            arrObj.add(objRows);
	        }
    	}

        
        objJson.put("VoidBillDtl", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Void Bill Dtl= " + op);
        conn.disconnect();
        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateBills);
            if (updateBills.length() > 0)
            {
                updateBills = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblvoidbilldtl set strDataPostFlag='Y' where strBillNo in (" + updateBills + ")", "sql");
            }
        }
        return 1;
    }

    private int funPostVoidBillModifierDtlData(String clientCode,String PosCode) throws Exception
    {
        String updateBills = "";
        String query = "select * from tblvoidmodifierdtl where strDataPostFlag='N' limit 1000 ";

        List listVoidBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();
        if(listVoidBill.size()>0)
    	{
    		for(int i=0;i<listVoidBill.size();i++)
	        {
    			Object ob[]=(Object[])listVoidBill.get(i);
	                JSONObject objRows = new JSONObject();
		            updateBills += ",'" + ob[0].toString()+ "'";
		
		            objRows.put("BillNo",ob[0].toString());// rs.getString("strBillNo"));
		            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
		            objRows.put("ModifierCode",ob[2].toString());// rs.getString("strModifierCode"));
		            objRows.put("ModifierName",ob[3].toString());// rs.getString("strModifierName"));
		            objRows.put("Quantity",ob[4].toString());// rs.getString("dblQuantity"));
		            objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
		            objRows.put("ClientCode",ob[6].toString());// rs.getString("strClientCode"));
		            objRows.put("CustomerCode",ob[7].toString());// rs.getString("strCustomerCode"));
		            objRows.put("Remarks",ob[9].toString());// rs.getString("strRemarks"));9
		            objRows.put("DataPostFlag",ob[8].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("ReasonCode",ob[10].toString());// rs.getString("strReasonCode"));
		            arrObj.add(objRows);
		        }

    	}
        objJson.put("VoidBillModifierDtl", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Void Mod Bill Dtl= " + op);
        conn.disconnect();
        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateBills);
            if (updateBills.length() > 0)
            {
                updateBills = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblvoidmodifierdtl set strDataPostFlag='Y' "
                        + " where strBillNo in (" + updateBills + ")", "sql");
            }
        }
        return 1;
    }

    private int funPostVoidKOTData() throws Exception
    {
        String updateKOT = "";
        String query = "select * from tblvoidkot where strDataPostFlag='N' ";
        List listVoidBill=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        if(listVoidBill.size()>0)
    	{
    		for(int i=0;i<listVoidBill.size();i++)
		    {
	    			Object ob[]=(Object[])listVoidBill.get(i);
		        JSONObject objRows = new JSONObject();
	            updateKOT += ",'" + ob[7].toString() + "'";
	            objRows.put("KOTNo", ob[7].toString());//rs.getString("strKOTNo"));7
	            objRows.put("TableNo",ob[0].toString());// rs.getString("strTableNo"));
	            objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
	            objRows.put("ItemCode",ob[2].toString());// rs.getString("strItemCode"));
	            objRows.put("ItemName",ob[3].toString());// rs.getString("strItemCode"));
	            objRows.put("ItemQuantity",ob[4].toString());// rs.getString("dblItemQuantity"));
	            objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
	            objRows.put("WaiterNo",ob[6].toString());// rs.getString("strWaiterNo"));
	            objRows.put("PaxNo",ob[8].toString());// rs.getString("intPaxNo"));
	            objRows.put("Type",ob[9].toString());// rs.getString("strType"));
	            objRows.put("ReasonCode",ob[10].toString());// rs.getString("strReasonCode"));
	            objRows.put("UserCreated",ob[11].toString());// rs.getString("strUserCreated"));
	            objRows.put("DateCreated",ob[12].toString());// rs.getString("dteDateCreated"));
	            objRows.put("VoidedDate",ob[13].toString());// rs.getString("dteVoidedDate"));
	            objRows.put("DataPostFlag",ob[14].toString());// rs.getString("strTableNo"));
	            objRows.put("ClientCode",ob[15].toString());// rs.getString("strClientCode"));
	            objRows.put("ManualKOTNo",ob[16].toString());// rs.getString("strManualKOTNo"));
	            objRows.put("PrintKOT",ob[17].toString());// rs.getString("strPrintKOT"));
	
	            arrObj.add(objRows);
	        }

    	}
        objJson.put("VoidKot", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Void KOT= " + op);
        conn.disconnect();
        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateKOT);
            if (updateKOT.length() > 0)
            {
                updateKOT = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblvoidkot set strDataPostFlag='Y' where strKOTNo in (" + updateKOT + ")", "sql");
            }
        }
        return 1;
    }

    private int funPostVoidAdvOrderBillHdData()
    {
        try
        {
            String updateBills = "";
            String query = "select * from tblvoidadvbookbillhd where strDataPostFlag='N'";
            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();

            JSONArray arrObj = new JSONArray();
            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
		            {
            		Object ob[]=(Object[])listqVoidAdv.get(i);
		                JSONObject objRows = new JSONObject();
		                updateBills += ",'" + ob[0].toString() + "'";
		
		                objRows.put("AdvBookingNo",ob[0].toString());// rs.getString("strAdvBookingNo"));
		                objRows.put("AdvBookingDate",ob[1].toString());// rs.getString("dteAdvBookingDate"));
		                objRows.put("OrderFor",ob[2].toString());// rs.getString("dteOrderFor"));
		                objRows.put("POSCode",ob[3].toString());// rs.getString("strPOSCode"));
		                objRows.put("SettelmentMode",ob[4].toString());// rs.getString("strSettelmentMode"));
		                objRows.put("DiscountAmt",ob[5].toString());// rs.getString("dblDiscountAmt"));
		                objRows.put("DiscountPer",ob[6].toString());// rs.getString("dblDiscountPer"));
		                objRows.put("TaxAmt",ob[7].toString());// rs.getString("dblTaxAmt"));
		                objRows.put("SubTotal",ob[8].toString());// rs.getString("dblSubTotal"));
		                objRows.put("GrandTotal",ob[9].toString());// rs.getString("dblGrandTotal"));
		
		                objRows.put("UserCreated",ob[10].toString());// rs.getString("strUserCreated"));
		                objRows.put("UserEdited",ob[11].toString());// rs.getString("strUserEdited"));
		                objRows.put("DateCreated",ob[12].toString());// rs.getString("dteDateCreated"));
		                objRows.put("DateEdited",ob[13].toString());// rs.getString("dteDateEdited"));13
		
		                objRows.put("CustomerCode",ob[15].toString());// rs.getString("strCustomerCode"));15
		                objRows.put("ShiftCode",ob[16].toString());// rs.getString("intShiftCode"));
		                objRows.put("Message",ob[17].toString());// rs.getString("strMessage"));
		                objRows.put("Shape",ob[18].toString());// rs.getString("strShape"));
		
		                objRows.put("Note",ob[19].toString());// rs.getString("strNote"));19
		                objRows.put("DeliveryTime",ob[21].toString());// rs.getString("strDeliveryTime"));21
		                objRows.put("WaiterNo",ob[22].toString());// rs.getString("strWaiterNo"));
		                objRows.put("HomeDelivery",ob[23].toString());// rs.getString("strHomeDelivery"));
		                objRows.put("HomeDelCharges",ob[24].toString());// rs.getString("dblHomeDelCharges"));
		                objRows.put("OrderType",ob[25].toString());// rs.getString("strOrderType"));
		                objRows.put("ManualAdvOrderNo",ob[26].toString());// rs.getString("strManualAdvOrderNo"));
		                objRows.put("ImageName",ob[27].toString());// rs.getString("strImageName"));
		                objRows.put("SpecialsymbolImage",ob[28].toString());// rs.getString("strSpecialsymbolImage"));28
		
		                objRows.put("ClientCode",ob[14].toString());// rs.getString("strClientCode"));14
		                objRows.put("DataPostFlag",ob[20].toString());// rs.getString("strDataPostFlag"));20
		                objRows.put("UrgentOrder",ob[29].toString());// rs.getString("strUrgentOrder"));29
		                objRows.put("ReasonCode",ob[30].toString());// rs.getString("strReasonCode"));
		                objRows.put("Remark",ob[31].toString());// rs.getString("strRemark"));
		
		                arrObj.add(objRows);
		            }
        	}

            objJson.put("VoidAdvOrderBillHd", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Bill Hd= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvbookbillhd set strDataPostFlag='Y' "
                            + "where strAdvBookingNo in (" + updateBills + ")", "sql");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }

    private int funPostVoidAdvOrderBillDtlData()
    {
        try
        {
            String updateBills = "";
            String query = "select * from tblvoidadvbookbilldtl where strDataPostFlag='N'";
            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();

            JSONArray arrObj = new JSONArray();
            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
	            {
            		Object ob[]=(Object[])listqVoidAdv.get(i);
	           
	                JSONObject objRows = new JSONObject();
	                updateBills += ",'" + ob[2].toString() + "'";
	
	                objRows.put("AdvBookingNo", ob[2].toString());//rs.getString("strAdvBookingNo"));2
	                objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));
	                objRows.put("ItemName",ob[1].toString());// rs.getString("strItemName"));
	                objRows.put("Quantity",ob[3].toString());// rs.getString("dblQuantity"));3
	                objRows.put("Weight",ob[10].toString());// rs.getString("dblWeight"));10
	                objRows.put("Amount",ob[4].toString());// rs.getString("dblAmount"));4
	                objRows.put("TaxAmount",ob[5].toString());// rs.getString("dblTaxAmount"));
	                objRows.put("AdvBookingDate",ob[6].toString());// rs.getString("dteAdvBookingDate"));
	                objRows.put("OrderFor",ob[7].toString());// rs.getString("dteOrderFor"));
	                objRows.put("CustomerCode",ob[9].toString());// rs.getString("strCustomerCode"));9
	                objRows.put("ClientCode",ob[8].toString());// rs.getString("strClientCode"));8
	                objRows.put("DataPostFlag",ob[11].toString());// rs.getString("strDataPostFlag"));11
	
	                arrObj.add(objRows);
	            }
        	}

            objJson.put("VoidAdvOrderBillDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Bill Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvbookbilldtl set strDataPostFlag='Y' where strAdvBookingNo in (" + updateBills + ")", "sql");
                }
                
            }

            return 1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }

    private int funPostVoidAdvOrderModifierDtlData()
    {
        try
        {
            String updateBills = "";
            String query = "select * from tblvoidadvordermodifierdtl where strDataPostFlag='N'";
            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();

            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
		            {
	            		Object ob[]=(Object[])listqVoidAdv.get(i);
		               JSONObject objRows = new JSONObject();
	                updateBills += ",'" + ob[0].toString() + "'";
	
	                objRows.put("AdvBookingNo",ob[0].toString());// rs.getString("strAdvOrderNo"));
	                objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
	                objRows.put("ModifierCode",ob[2].toString());// rs.getString("strModifierCode"));
	                objRows.put("ModifierName",ob[3].toString());// rs.getString("strModifierName"));
	                objRows.put("Quantity",ob[4].toString());// rs.getString("dblQuantity"));
	                objRows.put("Amount",ob[5].toString());// rs.getString("dblAmount"));
	                objRows.put("ClientCode",ob[6].toString());// rs.getString("strClientCode"));
	                objRows.put("CustomerCode",ob[7].toString());// rs.getString("strCustomerCode"));
	                objRows.put("UserCreated",ob[8].toString());// rs.getString("strUserCreated"));
	                objRows.put("UserEdited",ob[9].toString());// rs.getString("strUserEdited"));
	                objRows.put("DateCreated",ob[10].toString());// rs.getString("dteDateCreated"));
	                objRows.put("DateEdited",ob[11].toString());// rs.getString("dteDateEdited"));
	                objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
	
	                arrObj.add(objRows);
		            }
            }

            objJson.put("VoidAdvOrderModifierDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Mod Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvordermodifierdtl set strDataPostFlag='Y' where strAdvOrderNo in (" + updateBills + ")", "sql");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }

    private int funPostVoidAdvOrderBillCharDtlData()
    {
        try
        {
            String updateItems = "", updateAdvOrderNo = "";
            String query = "select * from tblvoidadvbookbillchardtl where strDataPostFlag='N'";
            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();

            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
			      {
			            Object ob[]=(Object[])listqVoidAdv.get(i);
				        JSONObject objRows = new JSONObject();
		                updateItems += ",'" + ob[0].toString() + "'";
		                updateAdvOrderNo += ",'" +ob[1].toString()+ "'";
		
		                objRows.put("ItemCode", ob[0].toString());//rs.getString("strItemCode"));
		                objRows.put("AdvBookingNo",ob[1].toString());// rs.getString("strAdvBookingNo"));
		                objRows.put("CharCode", ob[2].toString());//rs.getString("strCharCode"));
		                objRows.put("CharValues", ob[3].toString());//rs.getString("strCharValues"));
		                objRows.put("ClientCode", ob[4].toString());//rs.getString("strClientCode"));
		                objRows.put("DataPostFlag", ob[5].toString());//rs.getString("strDataPostFlag"));
		
		                arrObj.add(objRows);
		            }
            }

            objJson.put("VoidAdvOrderBillCharDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Bill Char Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateItems);
                StringBuilder sbUpdateAdvOrderNo = new StringBuilder(updateAdvOrderNo);
                if (updateItems.length() > 0)
                {
                    updateItems = sbUpdate.delete(0, 1).toString();
                    updateAdvOrderNo = sbUpdateAdvOrderNo.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    
                    System.out.println("update tblvoidadvbookbillchardtl set strDataPostFlag='Y' "
                            + " where strItemCode in (" + updateItems + ") and strAdvBookingNo in(" + updateAdvOrderNo + ") ");
                    
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvbookbillchardtl set strDataPostFlag='Y' "
                            + " where strItemCode in (" + updateItems + ") and strAdvBookingNo in(" + updateAdvOrderNo + ") ", "sql");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }
    
    private int funPostVoidAdvOrderReceiptHdData()
    {
        try
        {
            String updateBills = "";
            String query = "select * from tblvoidadvancereceipthd where strDataPostFlag='N'";

            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
            JSONObject objJson = new JSONObject();

            JSONArray arrObj = new JSONArray();
            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
			      {
            		Object ob[]=(Object[])listqVoidAdv.get(i);
	                JSONObject objRows = new JSONObject();
	                updateBills += ",'" + ob[0].toString()+ "'";
	
	                objRows.put("ReceiptNo", ob[0].toString());// rs.getString("strReceiptNo"));
	                objRows.put("AdvBookingNo", ob[1].toString());// rs.getString("strAdvBookingNo"));
	                objRows.put("POSCode", ob[2].toString());// rs.getString("strPOSCode"));
	                objRows.put("SettelmentMode", ob[3].toString());// rs.getString("strSettelmentMode"));
	                objRows.put("ReceiptDate", ob[4].toString());// rs.getString("dtReceiptDate"));
	                objRows.put("AdvDeposite", ob[5].toString());// rs.getString("dblAdvDeposite"));
	                objRows.put("ShiftCode", ob[6].toString());// rs.getString("intShiftCode"));
	                objRows.put("UserCreated", ob[7].toString());// rs.getString("strUserCreated"));
	                objRows.put("UserEdited", ob[8].toString());// rs.getString("strUserEdited"));
	                objRows.put("DateCreated", ob[9].toString());// rs.getString("dtDateCreated"));
	                objRows.put("DateEdited", ob[10].toString());// rs.getString("dtDateEdited"));
	                objRows.put("ClientCode", ob[11].toString());// rs.getString("strClientCode"));
	                objRows.put("DataPostFlag", ob[12].toString());// rs.getString("strDataPostFlag"));
	
	                arrObj.add(objRows);
	            }
            }
            

            objJson.put("VoidAdvOrderReceiptHd", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Rec Hd= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvancereceipthd set strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }
    
    private int funPostVoidAdvOrderReceiptDtlData()
    {
        try
        {
            String updateBills = "";
            String query = "select * from tblvoidadvancereceiptdtl where strDataPostFlag='N'";
            List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
           
            JSONObject objJson = new JSONObject();
            JSONArray arrObj = new JSONArray();
            if(listqVoidAdv.size()>0)
            {
            	for(int i=0;i>listqVoidAdv.size();i++)
            	 {
		            	
            			Object ob[]=(Object[])listqVoidAdv.get(i);
			            JSONObject objRows = new JSONObject();
		                updateBills += ",'" + ob[0].toString() + "'";
		
		                objRows.put("ReceiptNo",ob[0].toString() );// rs.getString("strReceiptNo"));
		                objRows.put("SettlementCode",ob[1].toString() );// rs.getString("strSettlementCode"));
		                objRows.put("CardNo",ob[2].toString() );// rs.getString("strCardNo"));
		                objRows.put("Expirydate",ob[3].toString() );// rs.getString("strExpirydate"));
		                objRows.put("ChequeNo",ob[4].toString() );// rs.getString("strChequeNo"));
		                objRows.put("ChequeDate",ob[5].toString() );// rs.getString("dteCheque"));
		                objRows.put("BankName",ob[6].toString() );// rs.getString("strBankName"));6
		                objRows.put("AdvDepositesettleAmt",ob[7].toString() );// rs.getString("dblAdvDepositesettleAmt"));
		                objRows.put("Remark",ob[8].toString() );// rs.getString("strRemark"));
		                objRows.put("PaidAmt",ob[9].toString() );// rs.getString("dblPaidAmt"));9
		                objRows.put("Installment",ob[12].toString() );// rs.getString("dteInstallment"));12
		                objRows.put("ClientCode",ob[10].toString() );// rs.getString("strClientCode"));10
		                objRows.put("DataPostFlag",ob[11].toString() );// rs.getString("strDataPostFlag"));11
		
		                arrObj.add(objRows);
		            }
            }

            objJson.put("VoidAdvOrderReceiptDtl", arrObj);
            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

            URL url = new URL(hoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(objJson.toString().getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";

            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println("Void Adv Order Rec Dtl= " + op);
            conn.disconnect();

            if (op.equals("true"))
            {
                StringBuilder sbUpdate = new StringBuilder(updateBills);
                if (updateBills.length() > 0)
                {
                    updateBills = sbUpdate.delete(0, 1).toString();
                    //System.out.println("Billsss="+updateBills);
                    objBaseServiceImpl.funExecuteUpdate("update tblvoidadvancereceiptdtl set "
                    		+ "strDataPostFlag='Y' where strReceiptNo in (" + updateBills + ")", "sql");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }

  
    public int funPostInventoryDataToHO(String formName,String clientCode,String PosCode)
    {
        try
        {
            funPostStockInHdData( clientCode, PosCode);
            funPostStockInDtlData();
            funPostStockOutHdData();
            funPostStockOutDtlData();
            funPostPhysicalStockHdData();
            funPostPhysicalStockDtlData();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }

    private int funPostStockInHdData(String clientCode,String PosCode) throws Exception
    {
        String updateStockNo = "";
        String query = "select * from c where strDataPostFlag='N'";
        List listStock=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();
        if(listStock.size()>0)
        {
        	for(int i=0;i<listStock.size();i++)
		        {
        			Object ob[]=(Object[])listStock.get(i);
		            JSONObject objRows = new JSONObject();
		            updateStockNo += ",'" + ob[0].toString() + "'";
		
		            objRows.put("StkInCode",ob[0].toString());// rs.getString("strStkInCode"));
		            objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
		            objRows.put("StkInDate",ob[2].toString());// rs.getString("dteStkInDate"));
		            objRows.put("ReasonCode",ob[3].toString());// rs.getString("strReasonCode"));
		            objRows.put("PurchaseBillNo",ob[4].toString());// rs.getString("strPurchaseBillNo"));
		            objRows.put("PurchaseBillDate",ob[5].toString());// rs.getString("dtePurchaseBillDate"));
		            objRows.put("ShiftCode",ob[6].toString());// rs.getString("intShiftCode"));
		            objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited",ob[8].toString());// rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[9].toString());// rs.getString("dteDateCreated"));
		            objRows.put("DateEdited",ob[10].toString());// rs.getString("dteDateEdited"));
		            objRows.put("ClientCode",ob[11].toString());// rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
		
		            arrObj.add(objRows);
		        }
        }
       
        objJson.put("StkInHd", arrObj);
        gSanguineWebServiceURL = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode,PosCode, "gSanguineWebServiceURL");
         
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Stk In Hd= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateStockNo);
            if (updateStockNo.length() > 0)
            {
                updateStockNo = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblstkinhd set strDataPostFlag='Y' "
                		+ "where strStkInCode in (" + updateStockNo + ")", "sql");
            }
        }

        return 1;
    }

    private int funPostStockInDtlData() throws Exception
    {
        String updateBills = "";
        String query = "select * from tblstkindtl where strDataPostFlag='N'";
        List listStock=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();

        JSONArray arrObj = new JSONArray();
        if(listStock.size()>0)
        {
        	for(int i=0;i<listStock.size();i++)
			  {
	        	Object ob[]=(Object[])listStock.get(i);
			    JSONObject objRows = new JSONObject();
	            updateBills += ",'" +  ob[0].toString()  + "'";
	
	            objRows.put("StkInCode",  ob[0].toString());// rs.getString("strStkInCode"));
	            objRows.put("ItemCode", ob[1].toString());//rs.getString("strItemCode"));
	            objRows.put("Quantity",ob[2].toString());// rs.getString("dblQuantity"));
	            objRows.put("PurchaseRate", ob[3].toString());//rs.getString("dblPurchaseRate"));
	            objRows.put("Amount",ob[4].toString());// rs.getString("dblAmount"));
	            objRows.put("ClientCode", ob[5].toString());//rs.getString("strClientCode"));
	            objRows.put("DataPostFlag", ob[6].toString());//rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	        }
        }

       
        objJson.put("StkInDtl", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Stk In Dtl= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateBills);
            if (updateBills.length() > 0)
            {
                updateBills = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblstkindtl set strDataPostFlag='Y'"
                		+ " where strStkInCode in (" + updateBills + ")", "sql");
            }
        }

        return 1;
    }

    private int funPostStockOutHdData() throws Exception
    {
        String updateStockNo = "";
        String query = "select * from tblstkouthd where strDataPostFlag='N'";
        List listStock=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();

        JSONArray arrObj = new JSONArray();

        if(listStock.size()>0)
        {
        	for(int i=0;i<listStock.size();i++)
        	{
		       	Object ob[]=(Object[])listStock.get(i);
	            JSONObject objRows = new JSONObject();
	            updateStockNo += ",'" + ob[0].toString() + "'";
	
	            objRows.put("StkOutCode",ob[0].toString());// rs.getString("strStkOutCode"));
	            objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
	            objRows.put("StkOutDate",ob[2].toString());// rs.getString("dteStkOutDate"));
	            objRows.put("ReasonCode",ob[3].toString());// rs.getString("strReasonCode"));
	            objRows.put("PurchaseBillNo",ob[4].toString());// rs.getString("strPurchaseBillNo"));
	            objRows.put("PurchaseBillDate",ob[5].toString());// rs.getString("dtePurchaseBillDate"));
	            objRows.put("ShiftCode",ob[6].toString());// rs.getString("intShiftCode"));
	            objRows.put("UserCreated",ob[7].toString());// rs.getString("strUserCreated"));
	            objRows.put("UserEdited",ob[8].toString());// rs.getString("strUserEdited"));
	            objRows.put("DateCreated",ob[9].toString());// rs.getString("dteDateCreated"));
	            objRows.put("DateEdited",ob[10].toString());// rs.getString("dteDateEdited"));
	            objRows.put("ClientCode",ob[11].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[12].toString());// rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	        }
        }

        
        objJson.put("StkOutHd", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Stk Out Hd= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateStockNo);
            if (updateStockNo.length() > 0)
            {
                updateStockNo = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblstkouthd set strDataPostFlag='Y' "
                		+ "where strStkOutCode in (" + updateStockNo + ")", "sql");
            }
        }

        return 1;
    }

    private int funPostStockOutDtlData() throws Exception
    {
        String updateStockNo = "";
        String query = "select * from tblstkoutdtl where strDataPostFlag='N'";
        List listStock=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();

        if(listStock.size()>0)
        {
        	for(int i=0;i<listStock.size();i++)
        	{
		       	Object ob[]=(Object[])listStock.get(i);
		       	JSONObject objRows = new JSONObject();
	            updateStockNo += ",'" + ob[0].toString() + "'";
	
	            objRows.put("StkOutCode",ob[0].toString());// rs.getString("strStkOutCode"));
	            objRows.put("ItemCode",ob[1].toString());// rs.getString("strItemCode"));
	            objRows.put("Quantity",ob[2].toString());// rs.getString("dblQuantity"));
	            objRows.put("PurchaseRate", ob[3].toString());//rs.getString("dblPurchaseRate"));
	            objRows.put("Amount",ob[4].toString());// rs.getString("dblAmount"));
	            objRows.put("ClientCode", ob[5].toString());//rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[6].toString());// rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	        }
        }

        objJson.put("StkOutDtl", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("Stk Out Dtl= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updateStockNo);
            if (updateStockNo.length() > 0)
            {
                updateStockNo = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblstkoutdtl set strDataPostFlag='Y' "
                		+ "where strStkOutCode in (" + updateStockNo + ")", "sql");
            }
        }

        return 1;
    }

    private int funPostPhysicalStockHdData() throws Exception
    {
        String updatePhyStkNo = "";
        String query = "select * from tblpsphd where strDataPostFlag='N'";
        List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();
        if(listqVoidAdv.size()>0)
        {
        		for(int i=0;i<listqVoidAdv.size();i++)
		        {
        			Object ob[]=(Object[])listqVoidAdv.get(i);
		            JSONObject objRows = new JSONObject();
		            updatePhyStkNo += ",'" + ob[0].toString() + "'";
		
		            objRows.put("PSPCode", ob[0].toString());//rs.getString("strPSPCode"));
		            objRows.put("POSCode",ob[1].toString());// rs.getString("strPOSCode"));
		            objRows.put("StkInCode",ob[2].toString());// rs.getString("strStkInCode"));
		            objRows.put("StkOutCode",ob[3].toString());// rs.getString("strStkOutCode"));
		            objRows.put("BillNo", ob[4].toString());//rs.getString("strBillNo"));
		            objRows.put("StkInAmt",ob[5].toString());// rs.getString("dblStkInAmt"));
		            objRows.put("SaleAmt", ob[6].toString());//rs.getString("dblSaleAmt"));
		            objRows.put("ShiftCode",ob[7].toString());// rs.getString("intShiftCode"));
		            objRows.put("UserCreated",ob[8].toString());// rs.getString("strUserCreated"));
		            objRows.put("UserEdited", ob[9].toString());//rs.getString("strUserEdited"));
		            objRows.put("DateCreated",ob[10].toString());// rs.getString("dteDateCreated"));
		            objRows.put("DateEdited", ob[11].toString());//rs.getString("dteDateEdited"));
		            objRows.put("ClientCode", ob[12].toString());//rs.getString("strClientCode"));
		            objRows.put("DataPostFlag",ob[13].toString());// rs.getString("strDataPostFlag"));
		            objRows.put("ReasonCode",ob[14].toString());// rs.getString("strReasonCode"));
		            objRows.put("Remarks", ob[15].toString());//rs.getString("strRemarks"));
		
		            arrObj.add(objRows);
		        }
        }

        
        objJson.put("PspHd", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("PSP Hd= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updatePhyStkNo);
            if (updatePhyStkNo.length() > 0)
            {
                updatePhyStkNo = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblpsphd set strDataPostFlag='Y' "
                		+ "where strPSPCode in (" + updatePhyStkNo + ")", "sql");
            }
        }

        return 1;
    }

    private int funPostPhysicalStockDtlData() throws Exception
    {
        String updatePhyStkNo = "";
        String query = "select * from tblpspdtl where strDataPostFlag='N'";
        List listqVoidAdv=objBaseServiceImpl.funGetList(new StringBuilder(query), "sql");
        JSONObject objJson = new JSONObject();
        JSONArray arrObj = new JSONArray();
        if(listqVoidAdv.size()>0)
        {
        	for(int i=0;i<listqVoidAdv.size();i++)
		     {
        		Object ob[]=(Object[])listqVoidAdv.get(i);
		        JSONObject objRows = new JSONObject();
	            updatePhyStkNo += ",'" + ob[0].toString() + "'";
	
	            objRows.put("PSPCode",  ob[0].toString());//rs.getString("strPSPCode"));
	            objRows.put("ItemCode",ob[0].toString());// rs.getString("strItemCode"));
	            objRows.put("PhyStk",ob[0].toString());// rs.getString("dblPhyStk"));
	            objRows.put("CompStk",ob[0].toString());// rs.getString("dblCompStk"));
	            objRows.put("Variance",ob[0].toString());// rs.getString("dblVariance"));
	            objRows.put("VarianceAmt",ob[0].toString());// rs.getString("dblVairanceAmt"));
	            objRows.put("ClientCode",ob[0].toString());// rs.getString("strClientCode"));
	            objRows.put("DataPostFlag",ob[0].toString());// rs.getString("strDataPostFlag"));
	
	            arrObj.add(objRows);
	        }
        }	

        
        objJson.put("PspDtl", arrObj);
        String hoURL = gSanguineWebServiceURL + "/POSIntegration/funPostTransactionDataToHOPOS";

        URL url = new URL(hoURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(objJson.toString().getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
        {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "", op = "";

        while ((output = br.readLine()) != null)
        {
            op += output;
        }
        System.out.println("PSP Dtl= " + op);
        conn.disconnect();

        if (op.equals("true"))
        {
            StringBuilder sbUpdate = new StringBuilder(updatePhyStkNo);
            if (updatePhyStkNo.length() > 0)
            {
                updatePhyStkNo = sbUpdate.delete(0, 1).toString();
                //System.out.println("Billsss="+updateBills);
                objBaseServiceImpl.funExecuteUpdate("update tblpspdtl set strDataPostFlag='Y' "
                		+ "where strPSPCode in (" + updatePhyStkNo + ")", "sql");
            }
        }

        return 1;
    }  


    public int funPostAdvOrderDataToHO(String formName,String clientCode,String PosCode)
    {
        try
        {
            funPostAdvOrderHdData(formName, clientCode, PosCode);
            funPostAdvOrderDtlData(formName,clientCode, PosCode);
            funPostAdvOrderCharDtlData(formName,clientCode, PosCode);
            funPostAdvOrderModifierDtlData(formName,clientCode, PosCode);
            funPostAdvReceiptHdData(formName,clientCode, PosCode);
            funPostAdvReceiptDtlData(formName,clientCode, PosCode);
            funPostHomeDeliveryData(formName,clientCode, PosCode);
            funPostHomeDeliveryDtlData(formName,clientCode, PosCode);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection refused: connect"))
            {
                gConnectionActive = "N";
              //  JOptionPane.showMessageDialog(null, "Connection is lost to HO please check!!!");
            }
            else
            {
              //  JOptionPane.showMessageDialog(null, "Error while posting data to HO!!!");
            }
            e.printStackTrace();
        }
        return 1;
    }

}
