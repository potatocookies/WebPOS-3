
package com.sanguine.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mysql.jdbc.Connection;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.webpos.bean.clsPOSReportBean;
import com.sanguine.webpos.controller.clsPOSGlobalFunctionsController;
import com.sanguine.webpos.util.clsPOSSetupUtility;



@Controller
public class clsGlobalFunctions
{
	public String currentDateTime,currentDate;
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;
 
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	

	@Autowired
	private ServletContext servletContext;

	@Autowired
	static
	clsPOSSetupUtility objPOSSetupUtility;
	
	public final String NARRATION="strNarration";
	public final String COMPANY_NAME="strCompanyName";
	public final String USER_CODE="strUserCode";
	public final String AGAINST="strAgainst";
	public final String AUTHORISE="strAuthorise";
	public final String FROM_LOCATION="strFromLoc";
	public final String TO_LOCATION="strToLoc";
	public final String FROM_DATE="dtFromDate";
	public final String TO_DATE="dtToDate";
	Map <String,Double>hmChildNodes;
	
	/*@Value("${database.urlWebStocks}")
	public String conUrl;
	
	@Value("${database.urlExcise}")
	String urlExcise;
	
	@Value("${database.urlCRM}")
	public String urlCRM;
	
	@Value("${database.urlwebbooks}")
	public String urlwebbooks;
	
	@Value("${database.urlwebclub}")
	public String urlwebclub;
	
	@Value("${database.user}")
	public String urluser;
	
	@Value("${database.password}")
	public String urlPassword;*/
	
	public static String conUrl;
	public static String urlExcise;
	public static String urlCRM;
	public static String urlwebbooks;
	public static String urlwebclub;
	public static String urlwebpms;
	public static String urluser;
	public static String urlPassword;
	
	public List listChildNodes1;
	
	public static String POSWSURL="http://localhost:8080/prjSanguineWebService";
	
	static{

		try {

			Properties prop = new Properties();
			Resource resource = new ClassPathResource("resources/database.properties");
			InputStream input = resource.getInputStream();
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			//conUrl = prop.getProperty("database.urlWebStocks");
			
			//changed for WebPOS connection
			conUrl = prop.getProperty("database.urlWebPOS");
			urluser = prop.getProperty("database.user");
			urlPassword = prop.getProperty("database.password");
			
			input.close();
			resource.exists();
			
		}catch(Exception e){
			System.out.println("Property File Not Found in Global Functions");
			e.printStackTrace();
		}
	}
	
	public long funGetLastNo(String tableName,String masterName,String columnName)
	{
		return objGlobalFunctionsService.funGetLastNo(tableName, masterName, columnName, "");
	}
	
	
	
	public  long funCompareTime(Date d2, Date d1)
    {
        long diff = 0;
        try
        {
            diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            String time = diffHours + ":" + diffMinutes + ":" + diffSeconds;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return diff;
        }
    }
	
	
	
	public String funGetCurrentDateTime(String pattern)
	{
		Date dt=new Date();
		if(pattern.equals("yyyy-MM-dd"))
		{
			currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
		}
		else
		{
			currentDateTime=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dt);
		}
		return currentDateTime;
	}
	

	public String funGetCurrentDate(String pattern)
	{
		Date dt=new Date();
		currentDate= new SimpleDateFormat(pattern).format(dt);
		return currentDate;
	}
	
	
	public String funGetDate(String pattern,String date)
	{
		String retDate=date;
		
		if(pattern.equals("yyyy-MM-dd"))  // From JSP to Database yyyy-MM-dd
		{
			String[] spDate=date.split("-");
			retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
		}
		else if(pattern.equals("yyyy/MM/dd")) // From Database to JSP
		{
			String[] sp=date.split(" ");
			String[] spDate=sp[0].split("-");
			retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
		}else if(pattern.equals("dd-MM-yyyy")) // For Jasper Report show
		{
			String[] sp=date.split(" ");
			String[] spDate=sp[0].split("-");
			retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
		}
		return retDate;
	}
	
	
	public long funGetMonth()
	{
		return new Date().getMonth();
	}
	
	public String funGetAlphabet(int no)
	{
		String[] alphabets= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		return alphabets[no];
	}
	
	
	
	public String[] funGetAlphabetSet()
	{
		String[] alphabetSet= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		return alphabetSet;
	}
	
	@SuppressWarnings("deprecation")
	public String funGetTransactionCode(String initCode,String propCode,String startYear)
	{
		String transCode="";
		transCode=propCode;
		Date dt=new Date();
		String years=String.valueOf((dt.getYear()+1900)-Integer.parseInt(startYear));
		transCode=propCode+initCode+funGetAlphabet(Integer.parseInt(years))+funGetAlphabet(dt.getMonth());
		return transCode;
	}
	
	public String funGetTime(String inputTime, String pattern)
	{
		String time="";
		
		if(pattern.equals("hh:mm:ss") )
		{
			String[] arrTime=inputTime.split(":");
			int hours=Integer.parseInt(arrTime[0]);
			int min=Integer.parseInt(arrTime[1].substring(1,2));
			String ampm=arrTime[1].substring(2,4);
			if(ampm.equalsIgnoreCase("pm"))
			{
				hours+=12;
			}
		}
		
		return time;
	}
	
	
	
	public String[] funGetSplitedDate(String date)
	{
		return date.split("/");
	}
	
	
	public String funIfNull(String input,String defaultValue,String assignedValue)
	{
		String op="notnull";
		if(null==input)
		{
			op=defaultValue;
		}
		else
		{
			op=assignedValue;
		}
		return op;
	}
	
	public Date funStringToDate(String strDate) {

		Date date = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			date = formatter.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List funGetList(String query)
	{
		return objGlobalFunctionsService.funGetList(query);		
	}
	
	
	public Object funSetDefaultValue(Object value, Object defaultValue)
	{
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}else if(value !=null && !(value instanceof String)){
			return value;
		}else{
			return  defaultValue;
		}
		//return value !=null && (value instanceof String && value.toString().length()>0) ? value : defaultValue;
	}
	// you are login in which model that will connect you of that database 
	public Connection funGetConnection(HttpServletRequest req)
	{
		Connection con=null;
		String modelno=req.getSession().getAttribute("moduleNo").toString();		
				
	    try
	    {
	    	switch(modelno)
	    	{
	    	case "1" :
		        con = (Connection) DriverManager.getConnection(conUrl+"?user="+urluser+"&password="+urlPassword);
		        break;
		        
	    	case "2":
	    		con = (Connection) DriverManager.getConnection(urlExcise+"?user="+urluser+"&password="+urlPassword);
//	    		con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/dbwebexcise?user=root&password=root");
		        break;
	    		
	    	case "3":	
	    		con = (Connection) DriverManager.getConnection(urlwebpms+"?user="+urluser+"&password="+urlPassword);
		        break;
	    		
	    	case "4":
	    		con = (Connection) DriverManager.getConnection(urlwebclub+"?user="+urluser+"&password="+urlPassword);
		        break;
	    		
	    	case "5":
	    		con = (Connection) DriverManager.getConnection(urlwebbooks+"?user="+urluser+"&password="+urlPassword);
		        break;
	    		
	    	case "6":
	    		con = (Connection) DriverManager.getConnection(conUrl+"?user="+urluser+"&password="+urlPassword);
		        break;
	    		
//	    	case "7":
//	    		con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/dbwebmms?user=root&password=root");
//		        break;
		        
		    default :
		    con = (Connection) DriverManager.getConnection(conUrl+"?user="+urluser+"&password="+urlPassword);
	        break;
	    	}
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Error in conection");
	    }
	    return con;
	}
	
	
	@SuppressWarnings("rawtypes")
	public boolean funCheckName(String Name,String strClientCode,String formName)
	{
		List countList=objGlobalFunctionsService.funCheckName(Name,strClientCode,formName);
		int count=Integer.parseInt(countList.get(0).toString());
		if(count>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}



	
	
	
	// Function used to sort HashMap on hashmap values
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap funSortByValues(HashMap map) 
	{ 
	      List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	 }

	public Map funGetCommonHashMapForJasperReport(clsPOSReportBean objBean, HttpServletRequest req, HttpServletResponse resp)
	{
		Map hm = new HashMap<>();

		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		String userCode = req.getSession().getAttribute("gUserCode").toString();
		String companyName = req.getSession().getAttribute("gCompanyName").toString();
		String POSCode=req.getSession().getAttribute("loginPOS").toString();	
		String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
		String fromDateToDisplay = objBean.getFromDate();
		String toDateToDisplay = objBean.getToDate();
		String shiftNo = "ALL";
		shiftNo = req.getSession().getAttribute("gShiftNo").toString();
		String fromDate = objBean.getFromDate().split("-")[2] + "-" + objBean.getFromDate().split("-")[1] + "-" + objBean.getFromDate().split("-")[0];

		String toDate = objBean.getToDate().split("-")[2] + "-" + objBean.getToDate().split("-")[1] + "-" + objBean.getToDate().split("-")[0];

		String type = objBean.getStrDocType();
		String strPOSName = objBean.getStrPOSName();
		final String gDecimalFormatString = funGetGlobalDecimalFormatString(clientCode,POSCode);

		hm.put("posName", strPOSName);
		hm.put("imagePath", imagePath);
		hm.put("clientName", companyName);

		hm.put("fromDate", fromDate);
		hm.put("toDate", toDate);

		hm.put("fromDateToDisplay", fromDateToDisplay);
		hm.put("toDateToDisplay", toDateToDisplay);
		hm.put("shiftNo", shiftNo);
		hm.put("userName", userCode);
		hm.put("type", type);
		hm.put("decimalFormaterForDoubleValue", gDecimalFormatString);
		hm.put("decimalFormaterForIntegerValue", "0");
		return hm;
	}
	

    public static String funGetGlobalDecimalFormatString(String strClientCode,String POSCode)
    {

	StringBuilder decimalFormatBuilderForDoubleValue = new StringBuilder("0");
	int gNoOfDecimalPlace = Integer.parseInt(clsPOSGlobalFunctionsController.hmPOSSetupValues.get("dblNoOfDecimalPlace").toString());
	for (int i = 0; i < gNoOfDecimalPlace; i++)
	{
	    if (i == 0)
	    {
		decimalFormatBuilderForDoubleValue.append(".0");
	    }
	    else
	    {
		decimalFormatBuilderForDoubleValue.append("0");
	    }
	}
	return decimalFormatBuilderForDoubleValue.toString();
    }
	
	 public JSONObject funPOSTMethodUrlJosnObjectData(String strUrl,JSONObject objRows)
		{
			JSONObject josnObjRet = new JSONObject();
			
			try {
					URL url = new URL(strUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setDoOutput(true);
		            conn.setRequestMethod("POST");
		            conn.setRequestProperty("Content-Type", "application/json");
		            OutputStream os = conn.getOutputStream();
		            os.write(objRows.toString().getBytes());
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
		            System.out.println("Result= " + op);
		            conn.disconnect();
					
		            JSONParser parser = new JSONParser();
					Object obj = parser.parse(op);
					josnObjRet = (JSONObject) obj;
		        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return josnObjRet;
			
		}

	   public JSONObject funGETMethodUrlJosnObjectData(String strUrl)
		{
			JSONObject jObj = null;;
			try {
				
				URL url = new URL(strUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output = "", op = "";
				while ((output = br.readLine()) != null)
				{
				    op += output;
				}
				System.out.println("Obj="+op);
				conn.disconnect();
									
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(op);
				jObj = (JSONObject) obj; 
		        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jObj;
			
		}



}






