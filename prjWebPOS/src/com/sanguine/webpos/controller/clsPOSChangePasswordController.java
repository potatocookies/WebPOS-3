package com.sanguine.webpos.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.webpos.bean.clsPOSChangePasswordBean;

@Controller
public class clsPOSChangePasswordController {
	
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
	
	
	
	

	
	
	

	
}
