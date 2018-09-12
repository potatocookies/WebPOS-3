package com.sanguine.webpos.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSDayEndProcessBean;
import com.sanguine.webpos.util.clsPOSBackupDatabase;
import com.sanguine.webpos.util.clsPOSDayEndUtility;
import com.sanguine.webpos.util.clsPOSSendMail;
import com.sanguine.webpos.util.clsPOSSetupUtility;
@Controller

public class clsPOSDayEndWithoutDetails 
{
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController; 
	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired 
	 clsPOSBackupDatabase obBackupDatabase;
	 @Autowired
	 clsPOSSetupUtility objPOSSetupUtility;
	 @Autowired 
	 clsPOSDayEndUtility objPOSDayEndUtility;

	 @Autowired
	 intfBaseService objBaseService;
 
	 @Autowired 
	 clsPOSSendMail objSendMail;
	 
	 
	String strPOSCode="";
	@RequestMapping(value="/frmPOSDayEndWithoutDetails",method=RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String,Object> model,HttpServletRequest request,ModelMap modelMap)
	{
		clsPOSDayEndProcessBean objDayEndProcessBean= new clsPOSDayEndProcessBean();
		strPOSCode=request.getSession().getAttribute("loginPOS").toString();
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndWithoutDetails_1","command", objDayEndProcessBean);
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDayEndWithoutDetails","command", objDayEndProcessBean);
		}else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/startDayProcessWithoutDetails",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funStartDayProcessWithoutDetails(HttpServletRequest req)
	{
		JSONObject jsonDayStart=new JSONObject();
		try{
		
			 jsonDayStart.put("DayStart", "Day Not Start");
			 int shiftNo=1;
			
			StringBuilder sql =new StringBuilder("update tbldayendprocess set strShiftEnd='N' "
	                + "where strPOSCode='" + strPOSCode + "' and strDayEnd='N' and strShiftEnd=''");
			
			objBaseService.funExecuteUpdate(sql.toString(),"sql");
			if (shiftNo == 0)
	        {
	            shiftNo++;
	        }
			sql.setLength(0);
	        sql.append("update tbldayendprocess set intShiftCode= " + shiftNo + " "
	                + "where strPOSCode='" + strPOSCode + "' and strShiftEnd='N' and strDayEnd='N'");
	        objBaseService.funExecuteUpdate(sql.toString(),"sql");
	        req.getSession().setAttribute("gShiftEnd","N");
		    req.getSession().setAttribute("gDayEnd","N");
		    req.getSession().setAttribute("gShiftNo",shiftNo);
		    jsonDayStart.put("DayStart", "Day Started Successfully");
	        
			}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonDayStart;//new ModelAndView("frmPOSDayEndWithoutDetails");//return jsonDayStart; //new clsDayEndProcessBean();
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/blankDayEndDayProcess",  method = RequestMethod.GET)
	public @ResponseBody JSONObject funBlankDayEndDayProcess(HttpServletRequest req)
	{
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String strClientCode=req.getSession().getAttribute("gClientCode").toString();
		String strPOSDate=req.getSession().getAttribute("gPOSDate").toString();
		String ShiftNo="1";
		JSONObject jobj=new JSONObject();
		
		try{
			jobj= funDayEndProcessWithoutDetails( strPOSCode,  ShiftNo, userCode, strPOSDate, strClientCode, req);
			req.getSession().setAttribute("gDayEnd","Y");
			jobj.put("msg", "Succesfully Day End");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jobj.put("msg", "Day End Not Done");
			return jobj;
		}
		//return new ModelAndView("frmPOSDayEndWithoutDetails");
	}
	

	public JSONObject funDayEndProcessWithoutDetails(String strPOSCode, String strShiftNo,String strUserCode,String POSDate,String strClientCode,HttpServletRequest req)
	{
		clsPOSDayEndProcess.gTransactionType = "ShiftEnd"; // For DayEndWithoutDetails to create day end textfile
		JSONObject objJSON=new JSONObject();
		int shiftNo=1;
		try
        {
			double totalSales=0, totalDiscount=0, totalPayments=0;
			String gEnableShiftYN= objPOSSetupUtility.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
			if (gEnableShiftYN.equals("Y"))//for shift wise
            {
                StringBuilder sql =new StringBuilder("select date(max(dtePOSDate)),intShiftCode "
                        + " from tbldayendprocess "
                        + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N' "
                        + " and (strShiftEnd='' or strShiftEnd='N') ");
                
                List listShift=objBaseService.funGetList(sql, "sql");
                if(listShift.size()>0)
                {
                	for(int i=0;i<listShift.size();i++)
                	{
                		Object[] ob=(Object[])listShift.get(i);
                		shiftNo = Integer.parseInt(ob[1].toString());		
                	}
                }
                
                objBaseService.funExecuteUpdate("delete from tblitemrtemp where strTableNo='null' ", "sql");
                
                clsPOSDayEndProcess.gDayEndReportForm = "DayEndReport";
                //here check busy table and pending bills.. those already checked on UI
                objJSON.put("gDayEndReportForm", "DayEndReport");

                String gDayEnd="N";
				if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
			    {
                        String backupFilePath="";
                        if ("Windows".equalsIgnoreCase("Windows"))//clsPosConfigFile.gPrintOS.equalsIgnoreCase("Windows"))
                        {
                            backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);
                        }
                        sql.setLength(0);
                        sql.append("update tbltablemaster set strStatus='Normal' "
                                + " where strPOSCode='" + strPOSCode + "'");
                        objBaseService.funExecuteUpdate(sql.toString(),"sql");
                        
                        /*if (clsGlobalVarClass.gGenrateMI)
                        {
                            frmGenrateMallInterfaceText objGenrateMallInterfaceText = new frmGenrateMallInterfaceText();
                            objGenrateMallInterfaceText.funWriteToFile(posDate, posDate, "Current", "Y");
                        }*/
                        objPOSDayEndUtility.funGetNextShiftNoForShiftEnd(strPOSCode, shiftNo,strClientCode,strUserCode,req);
                        
                        String filePath = System.getProperty("user.dir");
                        filePath = filePath + "/Temp/Temp_DayEndReport.txt";
//                        File file = new File(filePath);
//                        if (!file.exists())
//                        {
//                            file.mkdir();
//                        }
                        sql.setLength(0);
                        sql.append("select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
                        		+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';");
                        
                        List list=objBaseService.funGetList(sql, "sql");
                        String strPOSName="All";// by default
                        String strPOSDate="";
                        if(list.size()>0)
                        {
                        	Object ob[]=(Object[])list.get(0);
                        	strPOSDate= ob[1].toString().split("// ")[0];
                        	strPOSName= ob[0].toString();
                        }
                        objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
                           
                        if ("Windows".equalsIgnoreCase("Windows"))
                        {
                            //objUtility.funBackupAndMailDB(backupFilePath);
                            //objUtility.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
                        }  
                }
				
				objJSON.put("result", "success");
            }
            else
            {
                StringBuilder sql = new StringBuilder("select date(max(dtePOSDate)),intShiftCode"
                        + " from tbldayendprocess "
                        + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'"
                        + " and (strShiftEnd='' or strShiftEnd='N')");
                
                List listShift=objBaseService.funGetList(sql, "sql");
                if(listShift.size()>0)
                {
                	for(int i=0;i<listShift.size();i++)
                	{
                		Object[] ob=(Object[])listShift.get(i);
                		shiftNo = Integer.parseInt(ob[1].toString());		
                	}
                }
                else
                {
                    shiftNo++;
                }
                objBaseService.funExecuteUpdate("delete from tblitemrtemp where strTableNo='null' ","sql");
                
                clsPOSDayEndProcess.gDayEndReportForm = "DayEndReport";
                String gDayEnd="N";
                if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
			    {
                            String backupFilePath="";
                            if ("Windows".equalsIgnoreCase("Windows"))
                            {
                            	backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);
                            }
                            sql.setLength(0);
                            sql.append("update tbltablemaster set strStatus='Normal' "
                                    + " where strPOSCode='" + strPOSCode + "'");
                            objBaseService.funExecuteUpdate(sql.toString(),"sql");
                            sql.setLength(0);
                            sql.append("update tbldayendprocess set strShiftEnd='Y' "
                                    + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'");
                            objBaseService.funExecuteUpdate(sql.toString(),"sql");
                            
                            /*if (clsGlobalVarClass.gGenrateMI)
                            {
                                frmGenrateMallInterfaceText objGenrateMallInterfaceText = new frmGenrateMallInterfaceText();
                                objGenrateMallInterfaceText.funWriteToFile(posDate, posDate, "Current", "Y");
                            }*/
                            objPOSDayEndUtility.funGetNextShiftNo(strPOSCode, shiftNo,strClientCode,strUserCode,req);
                            
                            //btnShiftEnd.setEnabled(false);

                            String filePath = System.getProperty("user.dir");
                            filePath = filePath + "/Temp/Temp_DayEndReport.txt";
//                            File file = new File(filePath);
//                            if (!file.exists())
//                            {
//                                file.mkdir();
//                            }
                            sql.setLength(0);
                            sql.append("select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
	                        		+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';");
                            
	                        List list=objBaseService.funGetList(sql, "sql");
	                        String strPOSName="All",strPOSDate="";// by default
	                        if(list.size()>0)
	                        {
	                        	Object ob[]=(Object[])list.get(0);
	                        	strPOSDate= ob[1].toString().split("// ")[0];
	                        	strPOSName= ob[0].toString();
	                        }
	                        objSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                        if ("Windows".equalsIgnoreCase("Windows"))
	                        {
	                            //objUtility.funBackupAndMailDB(backupFilePath);
	                          //  objUtility.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                        }
                          
                        }
                objJSON.put("result", "success");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		return objJSON;
	}

}
