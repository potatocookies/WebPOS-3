package com.sanguine.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.POSLicence.controller.clsClientDetails;
import com.POSLicence.controller.clsEncryptDecryptClientCode;
import com.sanguine.bean.clsClientBean;
import com.sanguine.bean.clsUserHdBean;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsUserMasterService;

@Controller
@SessionAttributes("userdetails")
public class clsClientLoginController
{

	final static Logger logger = Logger.getLogger(clsUserController.class);
	private String strModule = "1";

	@Autowired
	private clsSetupMasterService objSetupMasterService;

	@Value("${applicationType}")
	String applicationType;

	@RequestMapping(value = "/validateClient", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest req, @Valid clsClientBean objClientBean, BindingResult result, ModelMap map)
	{
		ModelAndView objMV = null;
		try
		{
			if (result.hasErrors())
			{
				map.put("invalid", "1");
				objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
			}
			else
			{
				if (objClientBean != null)
				{
					if (objClientBean.getStrClientCode() != null && objClientBean.getStrClientCode().trim().length() > 0 && objClientBean.getStrPassword() != null && objClientBean.getStrPassword().trim().length() > 0)
					{

						String clientCodeFromDB = objClientBean.getStrClientCode();
						String clientPasswordFromDB = objClientBean.getStrPassword();

						String encryptedClientCodeFromDB = clsEncryptDecryptClientCode.funEncryptClientCode(clientCodeFromDB);
						String encryptedClientPasswordFromDB = clsEncryptDecryptClientCode.funEncryptClientCode(clientPasswordFromDB);

						clsClientDetails.funAddClientCodeAndName();

						String decryptedClientCodeFromHm = clsEncryptDecryptClientCode.funDecryptClientCode(clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).getId());
						String decryptedClientNameFromHm = clsEncryptDecryptClientCode.funDecryptClientCode(clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).Client_Name);
						String decryptedClientPasswordFromHm = clsEncryptDecryptClientCode.funDecryptClientCode(clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).getStrClientPassword());

						SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

						Date systemDate = dFormat.parse(dFormat.format(new Date()));

						String encryptedExpDate = clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).expiryDate;
						String decryptedExpDate = clsEncryptDecryptClientCode.funDecryptClientCode(encryptedExpDate);

						Date WebStockExpiryDate = dFormat.parse(decryptedExpDate);
						if (systemDate.compareTo(WebStockExpiryDate) <= 0)
						{
							if (clientCodeFromDB.equalsIgnoreCase(decryptedClientCodeFromHm) && clientPasswordFromDB.equalsIgnoreCase(decryptedClientPasswordFromHm))
							{
								req.getSession().setAttribute("gClientCode",clientCodeFromDB);
								req.getSession().setAttribute("gCompanyName",decryptedClientNameFromHm);								
								//req.getSession().setAttribute("gStartDate", startDate);
								
								
								Map<String, String> moduleMap = new TreeMap<String, String>();
								moduleMap.put("7-WebPOS", "webpos_module_icon.png");

								req.getSession().setAttribute("moduleNo", strModule);
								req.getSession().setAttribute("moduleMap", moduleMap);

								return new ModelAndView("frmLogin", "command", new clsUserHdBean());
							}
							else
							{
								map.put("invalid", "1");
								objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
							}

						}
						else
						{
							map.put("LicenceExpired", "1");
							objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
						}
					}
					else
					{
						map.put("invalid", "1");
						objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
					}
				}
				else
				{
					map.put("invalid", "1");
					objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			map.put("invalid", "1");
			objMV = new ModelAndView("frmClientLogin", "command", new clsClientBean());
		}
		return objMV;
	}

}
