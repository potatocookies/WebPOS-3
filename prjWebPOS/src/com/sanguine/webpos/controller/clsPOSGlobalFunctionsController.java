package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSSalesFlashReportsBean;
import com.sanguine.webpos.model.clsPOSMasterModel;

@Controller
public class clsPOSGlobalFunctionsController
{
	@Autowired
	clsGlobalFunctions objWSGlobalFunctions;
	
	@Autowired 
	intfBaseService obBaseService; 
/*	public static String wsServerIp;
	
	public static String wsServerPortNo;*/

	public static String wsServerIp;
	
	public static String wsServerPortNo;
	
	public static String urlSqlDump;
	
	
	@Value("${wsServerIp}")
	public void setWsServerIp(String wsServerIp) {
		clsPOSGlobalFunctionsController.wsServerIp = wsServerIp;
		setPOSWSURL("http://"+wsServerIp+":"+wsServerPortNo+"/prjSanguineWebService");
	}

	@Value("${wsServerPortNo}")
	public void setWsServerPortNo(String wsServerPortNo) {
		clsPOSGlobalFunctionsController.wsServerPortNo = wsServerPortNo;
		setPOSWSURL("http://"+wsServerIp+":"+wsServerPortNo+"/prjSanguineWebService");
	}
	
	@Value("${urlSqlDump}")
	public void seturlSqlDump(String urlSqlDump) {
		clsPOSGlobalFunctionsController.urlSqlDump = urlSqlDump;
	}
	
	public void setPOSWSURL(String pOSWSURL) {
		clsPOSGlobalFunctionsController.POSWSURL = pOSWSURL;
	}
	public static String POSWSURL;

	public static Map<Object,Object> hmPOSSetupValues=new HashMap<Object,Object>();
	// End



	public int funCheckName(String name,String code,String clientCode,String formName)
	{
		
		String sql = "";
		List list = new ArrayList();
		String posURL = null;
		int cnt=0;
		try {
			
			switch(formName)
			{
			case "POSAreaMaster":
				if(!code.equals("")){
					sql="select count(*) from tblareamaster a where a.strAreaCode !='"+ code+"' and a.strAreaName='" + name + "'and a.strClientCode='" + clientCode + "'";
				}else{
					sql = "select count(LOWER(strAreaName)) from tblareamaster where strAreaName='" + name + "' and strClientCode='" + clientCode + "'";
				}
			    break;
			case "POSMaster":
				if(!code.equals("")){
					sql="select count(*) from tblposmaster a where a.strPosCode !='"+ code+"' and a.strPosName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
			
			    sql = "select count(LOWER(strPosName)) from tblposmaster where strPosName='" + name +  "'";
				}
			break;
			case "POSWaiterMaster":
				if(!code.equals("")){
					sql="select count(*) from tblwaitermaster a where a.strWaiterNo !='"+ code+"' and a.strWShortName='" + name + "' a.and strClientCode='" + clientCode + "'";
				}else{
			    sql = "select count(LOWER(strWShortName)) from tblwaitermaster where strWShortName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSTableMaster":
				if(!code.equals("")){
					sql="select count(*) from tbltablemaster a where a.strTableNo !='"+ code+"' and a.strTableName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
				sql = "select count(LOWER(strTableName)) from tbltablemaster where strTableName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSSettlementMaster":
				if(!code.equals("")){
					sql="select count(*) from tblsettelmenthd a where a.strSettelmentCode !='"+ code+"' and a.strSettelmentDesc='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strSettelmentDesc)) from tblsettelmenthd where strSettelmentDesc='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSAdvanceOrderMaster":
				if(!code.equals("")){
					sql="select count(*) from tbladvanceordertypemaster a where a.strAdvOrderTypeCode !='"+ code+"' and a.strAdvOrderTypeName='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strAdvOrderTypeName)) from tbladvanceordertypemaster where strAdvOrderTypeName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			  case "POSDeliveryBoyMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldeliverypersonmaster a where a.strDPCode !='"+ code+"' and a.strDPName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strDPName)) from tbldeliverypersonmaster where strDPName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSOrderMaster":
				if(!code.equals("")){
					sql="select count(*) from tblordermaster a where a.strOrderCode !='"+ code+"' and a.strOrderDesc='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strOrderDesc)) from tblordermaster where strOrderDesc='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSPromotionMaster":
				if(!code.equals("")){
					sql="select count(*) from tblpromotionmaster a where a.strPromoCode !='"+ code+"' and a.strPromoName='" + name + "' and a.strClientCode='" + clientCode + "'";							
				}else{
				sql = "select count(LOWER(strPromoName)) from tblpromotionmaster where strPromoName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSCounterMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcounterhd a where a.strCounterCode !='"+ code+"' and a.strCounterName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strCounterName)) from tblcounterhd where strCounterName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSDebitCardTypeMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldebitcardtype a where a.strCardTypeCode !='"+ code+"' and a.strCardName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strCardName)) from tbldebitcardtype where strCardName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break; 
			    
			case "POSRegisterDebitCardMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldebitcardmaster a where a.strCardTypeCode !='"+ code+"' and a.strCardString='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql ="select strCardNo from tbldebitcardmaster where strCardString=='" + name + "'";
				}			
			break;
			
			case "POSZoneMaster":
				if(!code.equals("")){
					sql="select count(*) from tblzonemaster a where a.strZoneCode !='"+ code+"' and a.strZoneName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strZoneName)) from tblzonemaster where strZoneName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			break;
			case "POSCustomerTypeMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcustomertypemaster a where a.strCustTypeCode !='"+ code+"' and a.strCustType='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
					sql = "select count(LOWER(strCustType)) from tblcustomertypemaster where strCustType='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSCustomerMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcustomermaster a where a.strCustomerCode !='"+ code+"' and a.strCustomerName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
			
					sql = "select count(LOWER(strExternalCode)) from tblcustomermaster where strExternalCode='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			    
			case "POSMenuHead":
				if(!code.equals("")){
					sql="select count(*) from tblmenuhd a where a.strMenuCode !='"+ code+"' and a.strMenuName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
				sql = "select count(LOWER(strMenuName)) from tblmenuhd where strMenuName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			
			case "POSSubMenuHead":
				if(!code.equals("")){
					sql="select count(*) from tblsubmenuhead a where a.strSubMenuHeadCode !='"+ code+"' and a.strSubMenuHeadName='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strSubMenuHeadName)) from tblsubmenuhead where strSubMenuHeadName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			    break;
			    
			case "POSModifier":
				if(!code.equals("")){
					sql="select count(*) from tblmodifiermaster a where a.strModifierCode !='"+ code+"' and a.strModifierName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strModifierName)) from tblmodifiermaster where strModifierName='" + URLDecoder.decode("-->"+name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			    break;
			    
			case "POSMenuItem":
				if(!code.equals("")){
					sql="select count(*) from tblitemmaster a where a.strItemCode !='"+ code+"' and a.strItemName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strItemName)) from tblitemmaster where strItemName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}break;
			    
			case "POSModGroup":
				if(!code.equals("")){
					sql="select count(*) from tblmodifiergrouphd a where a.strModifierGroupCode !='"+ code+"' and a.strModifierGroupName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strModifierGroupName)) from tblmodifiergrouphd where strModifierGroupName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;    
			    
			case "POSFactoryMaster":
				if(!code.equals("")){
					sql="select count(*) from tblfactorymaster a where a.strFactoryCode !='"+ code+"' and a.strFactoryName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strFactoryName)) from tblfactorymaster where strFactoryName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break; 
			    
			case "POSCostCenterMaster":
				if(!code.equals("")){
				sql="select count(*) from tblcostcentermaster a where a.strCostCenterCode !='"+ code+"' and a.strCostCenterName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strCostCenterName)) from tblcostcentermaster where strCostCenterName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;  
			    
			case "POSGroupMaster":
				if(!code.equals("")){
					sql="select count(*) from tblgrouphd a where a.strGroupCode !='"+ code+"' and a.strGroupName='" + name + "' and a.strClientCode='" + clientCode + "'";					
					}else{
					sql = "select count(LOWER(strGroupName)) from tblgrouphd where strGroupName='" + name + "' and strClientCode='" + clientCode + "'";
					}
				break;
			    
		
    
			    		    
			}
			 list = obBaseService.funGetList(new StringBuilder(sql), "sql");
			 
			 if(list.size()>0){
				 cnt= Integer.parseInt(list.get(0).toString());	 
			 }
			 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cnt;
	}

	 public void funStartSocketBat()
	    {
	        try
	        {
	            funStopSocket();
	            BufferedWriter objToken = new BufferedWriter(new FileWriter("SocketServer.bat"));
	            String Path = System.getProperty("user.dir");
	            String Script = "\"" + Path + "\\JioMoneySocket.exe" + "\"" + " /Protocol IPv4 /Port " + "5150";
	            objToken.write(Script);
	            objToken.close();
	            Runtime.getRuntime().exec("cmd /c start SocketServer.bat");
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }
	 
	 public void funStopSocket()
	    {
	        try
	        {
	            Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 
	 public String funMakeTransaction(String Data, String requestType, String mid, String tid, String Amount, String Environment, String IP, String PORT,String posCode,String secretKey)
	    {
	        //Socket Base transaction start
	        try

	        {
	            String host = IP;	//IP address of the server
	            int port = Integer.parseInt(PORT);	//Port on which the socket is going to connect
	           
	           //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	            clsPOSJioMoneyEncryption objJio = new clsPOSJioMoneyEncryption();
	            String response = "";
	            StringBuilder Res = new StringBuilder();
	            String encryptedData = objJio.encrypt(Data, secretKey);
	            /*String finalRequest = encryptedData.toString() + "|" + request.getMid() + "|" + request.getTid() + "|" + request.getAmount() + "|" + request.getRequestType() +
	             "|" + "TESTING" + "|" + null;*/
	            String SendData = encryptedData + "|" + mid + "|" + tid + "|" + Amount + "|" + requestType + "|" + Environment + "|" + null;
	            System.out.println("Request String:" + SendData);
	            try (Socket s = new Socket(host, port)) //Creating socket class
	            {
	                DataOutputStream dout = new DataOutputStream(s.getOutputStream());	//creating outputstream to send data to server
	                DataInputStream din = new DataInputStream(s.getInputStream());	//creating inputstream to receive data from server
	                dout.writeUTF(SendData);	//Sending the finalRequest String to server
	                //System.out.println("Request sent to Server...");
	                dout.flush();	//Flush the streams

	                byte[] bs = new byte[10024];
	                din.read(bs);

	                char c;
	                for (byte b : bs)
	                {
	                    c = (char) b;
	                    response = Res.append("").append(c).toString();
	                    //response = c+;
	                    // System.out.println("Server Response:\n" + response);
	                }

	                dout.close();	//Closing the output stream
	                din.close();	//Closing the input stream
	            } //creating outputstream to send data to server

	            String strRes = response.trim();
	            JSONParser jsonParser = new JSONParser();
	            JSONObject jsonObject = (JSONObject) jsonParser.parse(strRes);
	            String token = (String) jsonObject.get("newToken");
	            if (token != null)
	            {
	            	 clsPOSJioMoneyEncryption objDecpt = new clsPOSJioMoneyEncryption();
	 	            String getToken = objDecpt.Decrypt(token, secretKey);
	               
	 	            String posURL = POSWSURL+"/WebPOSSetup/funSetToken"
	        					+ "?token="+token+"&posCode="+ posCode +"&mid="+mid;
	        	
	        	objWSGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
	            }
	            return response;
	        }
	        catch (Exception e)
	        {
	            System.out.println("Exception:" + e);
	            return null;
	        }
	        ////Socket Base transaction end
	    }
	 
	
	 public static Comparator<clsPOSSalesFlashReportsBean> COMPARATOR = new Comparator<clsPOSSalesFlashReportsBean>()
	    {
	        // This is where the sorting happens.
	        public int compare(clsPOSSalesFlashReportsBean o1, clsPOSSalesFlashReportsBean o2)
	        {
	         return (int) (o2.getSeqNo() - o1.getSeqNo());
	        }
	    };
	    
	    
	    
	   
	

}
