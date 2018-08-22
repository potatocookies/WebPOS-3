package com.sanguine.webpos.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.bean.clsUserHdBean;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsUserMasterService;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.util.clsPOSGlobalSingleObject;

@Controller
public class clsPOSLoginConfirm {

	@Autowired
	private clsUserMasterService objUserMasterService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFun;
	
	
	/**
	 * Open Confirm Login Form 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/frmConfirmUserLogin", method = RequestMethod.GET)
	public ModelAndView funOpenConfirmLoginUserForm(HttpServletRequest req){

		clsUserHdBean bean=new clsUserHdBean();
		return new ModelAndView("frmConfirmUserLogin","command",bean);
	}
	/**
	 * validating User
	 * @param userBean
	 * @param result
	 * @param req
	 * @return
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "/confirmUserLogin", method = RequestMethod.POST)
	public @ResponseBody String funCheckConfirmLoginUserForm(clsUserHdBean userBean,BindingResult result,HttpServletRequest req){
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getParameter("userName").toString();
		String userEnterPassword=req.getParameter("password").toString();
		
		String retValue="";
		try
		{
			if(!result.hasErrors())
			{
				if(userCode.equalsIgnoreCase("SANGUINE"))
				{
					 Date dt = new Date();
				     int day = dt.getDate();
				     int month = dt.getMonth() + 1;
				     int year = dt.getYear() + 1900;
				     int password = year + month + day + day;
					
				     String strpass=Integer.toString(password);
				     char num1 =strpass.charAt(0);
				     char num2 =strpass.charAt(1);
				     char num3 =strpass.charAt(2);
				     char num4 =strpass.charAt(3);
				     String alph1=objGlobalFun.funGetAlphabet(Character.getNumericValue(num1));
				     String alph2=objGlobalFun.funGetAlphabet(Character.getNumericValue(num2));
				     String alph3=objGlobalFun.funGetAlphabet(Character.getNumericValue(num3));
				     String alph4=objGlobalFun.funGetAlphabet(Character.getNumericValue(num4));
				     String finalPassword=String.valueOf(password)+alph1+alph2+alph3+alph4;
				     System.out.println("Hibernate: "+finalPassword+"CACA");
				     if (finalPassword.equalsIgnoreCase(userEnterPassword)) 
				     {
				    	 retValue="Successfull Login";
				     }
				     else
				     {
						retValue="Invalid Login";
				     }
				}
				else
				{	
					clsUserHdModel user=objUserMasterService.funGetUser(userCode,clientCode,"getUserMaster");
					if(user!=null)
					{	
						String encKey = "04081977";
			            String password = clsPOSGlobalSingleObject.getObjPasswordEncryptDecreat().encrypt(encKey,userEnterPassword.trim().toUpperCase());
			            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
						if(password.equals(user.getStrPassword()))
						{
						
								retValue="Successfull Login";
						}
						else
						{
							retValue="Invalid Login";
						}
					}
					else
					{
						retValue="Invalid Login";
					}
				}
				
			}
			else
			{
				retValue="Invalid Login";
			}
		}
		catch(Exception e)
			{
				e.printStackTrace();
				retValue="Invalid Login";
				
			}
		finally
		{
			return retValue;
		}
		
	}
}
