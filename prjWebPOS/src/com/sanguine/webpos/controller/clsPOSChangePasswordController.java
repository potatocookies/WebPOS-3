package com.sanguine.webpos.controller;

import java.util.HashMap;
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

import com.sanguine.base.service.intfBaseService;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.webpos.bean.clsPOSAreaMasterBean;
import com.sanguine.webpos.bean.clsPOSChangePasswordBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsAreaMasterModel_ID;
import com.sanguine.webpos.util.clsPOSGlobalSingleObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class clsPOSChangePasswordController {

	@Autowired
	intfBaseService obBaseService;
	
	@RequestMapping(value = "/frmChangePassword", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSChangePasswordBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		String userCode=request.getSession().getAttribute("gUserCode").toString();
		model.put("userCode",userCode);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmChangePassword1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmChangePassword");
		}else {
			return null;
		}
		 
	}
	@RequestMapping(value = "/checkUserName", method = RequestMethod.GET)
	public @ResponseBody String checkUserName(HttpServletRequest req)throws Exception
	{
		String strRet="Success";
		String userCode = req.getParameter("userCode").toString();
		String oldPass = req.getParameter("oldPass").toString();
		String encKey = "04081977";
		oldPass = clsPOSGlobalSingleObject.getObjPasswordEncryptDecreat().encrypt(encKey, oldPass);
		StringBuilder sqlOldPass = new StringBuilder("select a.strUserCode,a.strUserName,a.strPassword  "
		        + "from tbluserhd a "
		        + "where a.strUserCode='" + userCode + "' ");
		List list=obBaseService.funGetList(sqlOldPass, "sql");
		if (list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
				Object[] obj = (Object[]) list.get(i);
			    if (!obj[2].toString().equals(oldPass))
			    {
			    	strRet = "Invalid Old Password.";
			    }
			}
		}
		else
		{
			strRet =  "User Not Found.";
		}
		return strRet;
	
	}
	
	@RequestMapping(value = "/savrOrUpdateUserPassword", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSChangePasswordBean objBean,BindingResult result,HttpServletRequest req)
	{
		String strRet="";
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append( "update tbluserhd set strPassword='" + objBean.getStrNewPass() + "' where strUserCode='" + objBean.getStrUserCode() + "' ");
            List listAffectedRows = obBaseService.funGetList(sqlBuilder, "sql");
            if (listAffectedRows.size() > 0)
            {
            	strRet = "Password Updated Succesfully.";
            }
            Map objMap = new HashMap();
            objMap.put("newPass", objBean.getStrNewPass());
            objMap.put("userCode", objBean.getStrUserCode());
            sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strPOSType from tblsetup a where a.strClientCode='"+clientCode+"' ");
            listAffectedRows = obBaseService.funGetList(sqlBuilder, "sql");
            if(listAffectedRows.size()>0)
            {
            	
            if (listAffectedRows.get(0).equals("Client POS"))
            {
                String hoURL = clsPOSGlobalFunctionsController.POSWSURL+"/APOSIntegration/funUpdateHOUser";
            	
                URL url = new URL(hoURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStream os = conn.getOutputStream();
                os.write(objMap.toString().getBytes());
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
                System.out.println("Password Change=" + op);
                conn.disconnect();

            }
            }
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+strRet);
									
			return new ModelAndView("redirect:/frmChangePassword.html");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	

	
	
	

	
}
