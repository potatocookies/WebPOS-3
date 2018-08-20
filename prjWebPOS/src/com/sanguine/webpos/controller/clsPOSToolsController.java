package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import com.sanguine.base.service.clsPOSToolsService;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpos.bean.clsPOSConfigSettingBean;
import com.sanguine.webpos.bean.clsPOSDatabaseBackupBean;
import com.sanguine.webpos.bean.clsPOSFormMasterBean;
import com.sanguine.webpos.model.clsPOSConfigSettingHdModel;
import com.sanguine.webpos.model.clsPOSConfigSettingModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;

@Controller
public class clsPOSToolsController {

		
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService; 
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	clsPOSToolsService objPOSToolsService;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	intfBaseService obBaseService;
	
	
	Map map=new HashMap();
	@RequestMapping(value = "/frmPOSClearMasterTransaction", method = RequestMethod.GET)
	public ModelAndView funOpenPOSTools(Map<String, Object> model,HttpServletRequest req){
		
		
		String urlHits="1";
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		model.put("headerName","Transaction List");
		
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSClearMasterTransaction");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSClearMasterTransaction");
		}else {
			return null;
		}
		
	}
	
	@RequestMapping(value="/loadPosName",method =RequestMethod.GET)
	public @ResponseBody List funLoadPropertyMaster(HttpServletRequest req )throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		List posList = new ArrayList();
		posList.add("All");
		map.put("All", "All");
		List list=objMasterService.funFillPOSCombo(clientCode);
		for(int cnt =0 ;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			posList.add(Array.get(obj, 1).toString());
			map.put(Array.get(obj, 0).toString(), Array.get(obj, 1).toString());
		}
		return posList;		
		
	}
	
	/**
	 * Open Structure Update Form
	 * @param req
	 * @return
	 */
		@RequestMapping(value = "/frmPOSTools", method = RequestMethod.GET)
		public ModelAndView funOpenStructureUpdateForm(Map<String, Object> model,HttpServletRequest req){
			
			
			String urlHits="1";
			try{
				urlHits=req.getParameter("saddr").toString();
			}catch(NullPointerException e){
				urlHits="1";
			}
			model.put("urlHits",urlHits);
			
			
			if("2".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSTools_1");
			}else if("1".equalsIgnoreCase(urlHits)){
				return new ModelAndView("frmPOSTools");
			}else {
				return null;
			}
			
					
		}
	
	@RequestMapping(value = "/posUpdateStructure",  method = RequestMethod.GET)
	public @ResponseBody String funPOSUpdateStructure(HttpServletRequest req) throws Exception{
		Map hmData= new HashMap();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		String dateCreated = objGlobal.funGetCurrentDateTime("yyyy-MM-dd");
		String dateEdited = objGlobal.funGetCurrentDateTime("yyyy-MM-dd");
		
		hmData.put("clientCode", clientCode);
		hmData.put("userCode", userCode);
		hmData.put("dateCreated", dateCreated);
		hmData.put("dateEdited", dateEdited);
		hmData.put("posCode", posCode);
		
		Map hmRet=objPOSToolsService.funUpdateStructure(hmData);
		return "Structure Update Successfully";
	}
	
	@RequestMapping(value = "/POSClearTransaction",  method = RequestMethod.GET)
	public @ResponseBody String funPOSClearTransaction(@RequestParam(value="frmName") String frmName ,@RequestParam(value="posName") String posName,@RequestParam(value="fromDate") String fromDate,@RequestParam(value="toDate") String toDate,@RequestParam(value="userName") String userName,@RequestParam(value="chkAllSelected") String chkAllSelected,HttpServletRequest req){
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String loginPosCode=req.getSession().getAttribute("loginPOS").toString();
		
		String str[]=frmName.split(",");
		List listData = new ArrayList();
		for(int i=0;i<str.length;i++)
		{
		  Map hmData = new HashMap();
		  String formName = str[i];
		  hmData.put("formName",formName);
		  listData.add(hmData);
		}
		
		String posCode = "";
		if(map.containsKey(posName))
		{
			posCode=(String) map.get(posName);	
		}
		
		Map hmDataDtl = new HashMap();
		
		hmDataDtl.put("clientCode", clientCode);
		hmDataDtl.put("chkAllSelected",chkAllSelected);
		hmDataDtl.put("posCode", posCode);
		hmDataDtl.put("fromDate", fromDate);
		hmDataDtl.put("toDate", toDate);
		hmDataDtl.put("userName", userName);
		hmDataDtl.put("posDate", req.getSession().getAttribute("gPOSDate"));
		hmDataDtl.put("str", listData);
		Map hmReturn=objPOSToolsService.funCleanTransaction(hmDataDtl);
		String result = hmReturn.get("return").toString();
		if(result.equalsIgnoreCase("true"))
		{
			System.out.println("Transaction Clear Successfully");
		}
		return "Transaction Clear Successfully";
	}
	
	
	@RequestMapping(value = "/POSClearMaster",  method = RequestMethod.GET)
	public @ResponseBody String funPOSClearMaster(@RequestParam(value="frmName") String frmName,HttpServletRequest req){
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String str[]=frmName.split(",");
		
		List listData = new ArrayList();
		for(int i=0;i<str.length;i++)
		{
			Map hmData = new HashMap();
			String formName = str[i];
			hmData.put("formName",formName);
			listData.add(hmData);
		}
		
		Map hmDataDtl = new HashMap();
		hmDataDtl.put("clientCode", clientCode);
		hmDataDtl.put("listData", listData);
		Map hmRet=objPOSToolsService.funCleanMaster(hmDataDtl);
		String msg = (String)hmRet.get("return");
		System.out.println("Master Clear Successfully");

		return "Master Clear Successfully";
	}
	
	
	
	@RequestMapping(value = "/frmOpenPOSConfigSetting", method = RequestMethod.GET)
	public ModelAndView funOpenPOSConfigSetting(Map<String, Object> model,HttpServletRequest req){
		
		clsPOSConfigSettingBean objBean=new clsPOSConfigSettingBean();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		try {
			objBean = funLoadConfigData(clientCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSConfigSetting" , "command" , objBean);
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSConfigSetting", "command" , objBean);
		}else {
			return null;
		}
		
	}
	
	
	@RequestMapping(value = "/frmOpenPOSDBBackup", method = RequestMethod.GET)
	public ModelAndView frmOpenPOSDBBackup(Map<String, Object> model,HttpServletRequest req){
		
		clsPOSDatabaseBackupBean objBean=new clsPOSDatabaseBackupBean();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		
		model.put("urlHits",urlHits);
		
		String backupPath = System.getProperty("user.dir") + "\\DBBackup";
		model.put("backupPath", backupPath);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDataBaseBackup" , "command" , objBean);
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDataBaseBackup", "command" , objBean);
		}else {
			return null;
		}
		
	}
	
	@RequestMapping(value = "/savePOSConfigSetting",  method = RequestMethod.POST)
	public ModelAndView funSavePOSConfigSetting(@ModelAttribute("command") @Valid clsPOSConfigSettingBean objBean,BindingResult result,HttpServletRequest req) throws Exception
	{
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
	    clsPOSConfigSettingHdModel objModel = new clsPOSConfigSettingHdModel(new clsPOSConfigSettingModel_ID(clientCode));
		objModel.setStrServer(objBean.getStrServer());
		objModel.setStrDBName(objBean.getStrDBName());
		objModel.setStrUserID(objBean.getStrUserID());
		objModel.setStrPassword(objBean.getStrPassword());
		objModel.setStrIPAddress(objBean.getStrIPAddress());
		objModel.setStrPort(objBean.getStrPort());
		objModel.setStrBackupPath(objBean.getStrBackupPath());
		objModel.setStrExportPath(objBean.getStrExportPath());
		objModel.setStrImagePath(objBean.getStrImagePath());
		objModel.setStrHOWebServiceUrl(objBean.getStrHOWebServiceUrl());
		objModel.setStrMMSWebServiceUrl(objBean.getStrMMSWebServiceUrl());
		objModel.setStrOS(objBean.getStrOS());
		objModel.setStrDefaultPrinter(objBean.getStrDefaultPrinter());
		objModel.setStrPrinterType(objBean.getStrPrinterType());
		objModel.setStrTouchScreenMode(objBean.getStrTouchScreenMode());
		objModel.setStrServerFilePath(objBean.getStrServerFilePath());
		objModel.setStrSelectWaiterFromCardSwipe(objBean.getStrSelectWaiterFromCardSwipe());
		objModel.setStrMySQBackupFilePath(objBean.getStrMySQBackupFilePath());
		objModel.setStrHOCommunication(objBean.getStrHOCommunication());
		objModel.setStrAdvReceiptPrinter(objBean.getStrAdvReceiptPrinter());
	    String res=obBaseService.funSave(objModel);
		
		req.getSession().setAttribute("success", true);
		req.getSession().setAttribute("successMessage"," "+res);						
		return new ModelAndView("frmPOSConfigSetting", "command" , objBean);
		
	}
	
	
	private clsPOSConfigSettingBean funLoadConfigData(String strClientCode) throws Exception
	{
		clsPOSConfigSettingBean objBean = new clsPOSConfigSettingBean();
        clsPOSConfigSettingHdModel objModel = null;
	    List list=null;
		list = obBaseService.funLoadAll(new clsPOSConfigSettingHdModel(),strClientCode);
	
	    if (list.size() > 0)
		{
    	    objModel = (clsPOSConfigSettingHdModel) list.get(0);
			objBean.setStrServer(objModel.getStrServer());
    		objBean.setStrDBName(objModel.getStrDBName());
    		objBean.setStrUserID(objModel.getStrUserID());
    		objBean.setStrPassword(objModel.getStrPassword());
    		objBean.setStrIPAddress(objModel.getStrIPAddress());
    		objBean.setStrPort(objModel.getStrPort());
    		objBean.setStrBackupPath(objModel.getStrBackupPath());
    		objBean.setStrExportPath(objModel.getStrExportPath());
    		objBean.setStrImagePath(objModel.getStrImagePath());
    		objBean.setStrHOWebServiceUrl(objModel.getStrHOWebServiceUrl());
    		objBean.setStrMMSWebServiceUrl(objModel.getStrMMSWebServiceUrl());
    		objBean.setStrOS(objModel.getStrOS());
    		objBean.setStrDefaultPrinter(objModel.getStrDefaultPrinter() );
    		objBean.setStrPrinterType(objModel.getStrPrinterType());
    		objBean.setStrTouchScreenMode(objModel.getStrTouchScreenMode());
    		objBean.setStrServerFilePath(objModel.getStrServerFilePath());
    		objBean.setStrSelectWaiterFromCardSwipe(objModel.getStrSelectWaiterFromCardSwipe());
    		objBean.setStrMySQBackupFilePath(objModel.getStrMySQBackupFilePath());
    		objBean.setStrHOCommunication(objModel.getStrHOCommunication());
    		objBean.setStrAdvReceiptPrinter(objModel.getStrAdvReceiptPrinter());
		}
 
	    return objBean;
		
	}
	
	
	
	@RequestMapping(value = "/loadMastersData", method = RequestMethod.GET)
	public @ResponseBody Map funLoadMenuHeadDtlData(@RequestParam(value="strHeadingType") String strType,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		Map hmRet = new HashMap();
		String posUrl="";
		List listData=null;
		List<clsPOSFormMasterBean> listMenuHedaData=new ArrayList<clsPOSFormMasterBean>();
		Map hmData=new HashMap();
		
		if(strType.equalsIgnoreCase("Master"))
		{	
			hmData=objPOSToolsService.funClearMaster();
		}
		else
		{
			hmData= objPOSToolsService.funClearTransaction();
		}
			
		listData=(List) hmData.get("listData");
	    
		hmRet.put("masterDtl",listData);
		return hmRet;
	}
	
	

	@RequestMapping(value = "/loadDBBackupData", method = RequestMethod.GET)
	public @ResponseBody String funSavePOSDataBaseBackup(HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String userName = req.getParameter("userName").toString();
		String password = req.getParameter("password").toString();
		String dataBase = req.getParameter("dataBase").toString();
		String backupPath = req.getParameter("backupPath").toString();
		String posCode=req.getSession().getAttribute("loginPOS").toString();
		String posDate=req.getSession().getAttribute("gPOSDate").toString();
	    posDate=posDate.split(" ")[0];
		
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		
		Map hmRet = new TreeMap<>();	
		Map hmDtl = new HashMap();
		
		hmDtl.put("clientCode", clientCode);
		hmDtl.put("backupPath", backupPath);
		hmDtl.put("posCode", posCode);
		hmDtl.put("posDate", posDate);
		hmDtl.put("userCode", userCode);

		String result="";
		hmRet=objPOSToolsService.funDBBackup(hmDtl);
		if(hmRet.get("return").toString().equalsIgnoreCase("true"))
		{
			result="Database Backup Successfully..";
		}
		else
		{
			result="Failed To Database Backup";
		}
	     
		return result;
	}	
}
