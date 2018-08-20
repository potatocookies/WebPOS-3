package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSBillDetails;
import com.sanguine.webpos.bean.clsPOSCustomerDtlsOnBill;
import com.sanguine.webpos.bean.clsPOSDeliveryBoyMasterBean;
import com.sanguine.webpos.bean.clsPOSDirectBillerBean;
import com.sanguine.webpos.bean.clsPOSItemsDtlsInBill;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSDirectBillerController
{

	

}
