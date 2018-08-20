package com.sanguine.webpos.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSMenuHeadBean;
import com.sanguine.webpos.model.clsMenuHeadMasterModel;
import com.sanguine.webpos.model.clsMenuHeadMasterModel_ID;
import com.sanguine.webpos.model.clsSubMenuHeadMasterModel;
import com.sanguine.webpos.model.clsSubMenuHeadMasterModel_ID;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSMenuHeadController {
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsPOSUtilityController objUtilityController;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	private intfBaseService objSer;
	
	
	@RequestMapping(value = "/frmPOSMenuHead", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		//return new ModelAndView("frmPOSMenuMaster");
		
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMenuHead_1","command", new clsPOSMenuHeadBean());
		}
		else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSMenuHead","command", new clsPOSMenuHeadBean());
		}
		else
			return null;
	}
	
	
	@RequestMapping(value = "/saveMenuHeadMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSMenuHeadBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		// save data of Menu Head
		if(objBean.getStrSubMenuHeadName()!="")
		{	try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
			String subMenuCode = objBean.getStrSubMenuHeadCode();
			
			if (subMenuCode.trim().isEmpty())
			{
				//subMenuCode = objSubMenuHeadMasterDao.funGenerateSubMenuCode();
				List list=objUtilityController.funGetDocumentCode("POSSubMenuHead");
				if (!list.get(0).toString().equals("0"))
				{
					String strCode = "0";
					String code = list.get(0).toString();
					StringBuilder sb = new StringBuilder(code);
					String ss = sb.delete(0, 2).toString();
					for (int i = 0; i < ss.length(); i++)
					{
						if (ss.charAt(i) != '0')
						{
							strCode = ss.substring(i, ss.length());
							break;
						}
					}

					int intCode = Integer.parseInt(strCode);
					intCode++;
					if (intCode < 10)
					{
						subMenuCode = "SM00000" + intCode;
					}
					else if (intCode < 100)
					{
						subMenuCode = "SM0000" + intCode;
					}
					else if (intCode < 1000)
					{
						subMenuCode = "SM000" + intCode;
					}
					else if (intCode < 10000)
					{
						subMenuCode = "SM00" + intCode;
					}
					else if (intCode < 100000)
					{
						subMenuCode = "SM0" + intCode;
					}
					else if (intCode < 1000000)
					{
						subMenuCode = "SM" + intCode;
					}
				}
				else
				{
					subMenuCode = "SM000001";
				}
			}
			clsSubMenuHeadMasterModel objModel = new clsSubMenuHeadMasterModel(new clsSubMenuHeadMasterModel_ID(subMenuCode, clientCode));
			objModel.setStrSubMenuHeadCode(subMenuCode);
			objModel.setStrSubMenuHeadName(objBean.getStrSubMenuHeadName());
			objModel.setStrSubMenuHeadShortName(objBean.getStrSubMenuHeadShortName());
			objModel.setStrMenuCode(objBean.getStrMenuHeadCode());
			objModel.setStrSubMenuOperational(objBean.getStrSubMenuOperational());
			objModel.setStrUserCreated(webStockUserCode);
			objModel.setStrUserEdited(webStockUserCode);
			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));

			objMasterService.funSaveUpdateSubMenuHeadMasterData(objModel);//funSaveSubMenuMaster(objModel);

			
						
			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage"," "+subMenuCode);
									
			return new ModelAndView("redirect:/frmPOSMenuHead.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}				
		  
		}
		// save for Sub menu code
		else if(objBean.getStrMenuHeadName()!="")
		{
			try
			  {
				urlHits=req.getParameter("saddr").toString();
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				String webStockUserCode=req.getSession().getAttribute("gUserCode").toString();
				String menuCode = objBean.getStrMenuHeadCode();
				if (menuCode.trim().isEmpty())
				{
					//MenuCode = objMenuHeadMasterDao.funGenerateMenuCode();
					List list=objUtilityController.funGetDocumentCode("POSMenuHeadMaster");
					
					if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						String code = list.get(0).toString();
						StringBuilder sb = new StringBuilder(code);
						String ss = sb.delete(0, 1).toString();
						for (int i = 0; i < ss.length(); i++)
						{
							if (ss.charAt(i) != '0')
							{
								strCode = ss.substring(i, ss.length());
								break;
							}
						}
						int intCode = Integer.parseInt(strCode);
						intCode++;
						if (intCode < 10)
						{
							menuCode = "M00000" + intCode;
						}
						else if (intCode < 100)
						{
							menuCode = "M0000" + intCode;
						}
						else if (intCode < 1000)
						{
							menuCode = "M000" + intCode;
						}
						else if (intCode < 10000)
						{
							menuCode = "M00" + intCode;
						}
						else if (intCode < 100000)
						{
							menuCode = "M0" + intCode;
						}
						else if (intCode < 1000000)
						{
							menuCode  = "M" + intCode;
						}
					}
					else
					{
						menuCode = "M000001";
					}
				}
				clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel(new clsMenuHeadMasterModel_ID(menuCode, clientCode));
				objModel.setStrMenuName(objBean.getStrMenuHeadName());
				objModel.setStrOperational(objBean.getStrOperational());
				objModel.setStrUserCreated(webStockUserCode);
				objModel.setStrUserEdited(webStockUserCode);
				objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				objModel.setStrDataPostFlag("N");
				objModel.setImgImage(funBlankBlob());
				objMasterService.funSaveUpdateMenuHeadMasterData(objModel);
		
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+menuCode);
										
				return new ModelAndView("redirect:/frmPOSMenuHead.html?saddr="+urlHits);
			  }
			  catch(Exception ex)
			  {
				urlHits="1";
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			  }
			
		}
		else
		{
			return new ModelAndView("redirect:/frmFail.html");
		}
			
 }
	
	

	@RequestMapping(value = "/loadPOSMenuHeadMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSMenuHeadBean funSetSearchFields(@RequestParam("POSMenuHeadCode") String menuHeadCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSMenuHeadBean objPOSMenuHeadBean=new clsPOSMenuHeadBean();
		clsMenuHeadMasterModel objMenuHeadMasterModel = objMasterService.funSelectedMenuHeadMasterData(menuHeadCode, clientCode);
		objPOSMenuHeadBean=new clsPOSMenuHeadBean();
		objPOSMenuHeadBean.setStrMenuHeadCode(objMenuHeadMasterModel.getStrMenuCode());
		objPOSMenuHeadBean.setStrMenuHeadName(objMenuHeadMasterModel.getStrMenuName());
		objPOSMenuHeadBean.setStrOperational(objMenuHeadMasterModel.getStrOperational());
		objPOSMenuHeadBean.setStrOperationType("U");
		
		if(null==objPOSMenuHeadBean)
		{
			objPOSMenuHeadBean=new clsPOSMenuHeadBean();
			objPOSMenuHeadBean.setStrMenuHeadCode("Invalid Code");
		}
	
	
		return objPOSMenuHeadBean;
	}
	
	@RequestMapping(value = "/loadPOSSubMenuHeadMasterData", method = RequestMethod.GET)
	public @ResponseBody clsPOSMenuHeadBean funSetSubMenuFields(@RequestParam("POSSubMenuHeadCode") String subMenuHeadCode,HttpServletRequest req)throws Exception
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSMenuHeadBean objPOSMenuHeadBean=new clsPOSMenuHeadBean();
		clsSubMenuHeadMasterModel objMenuHeadMasterModel = objMasterService.funSelectedSubMenuHeadMasterData(subMenuHeadCode, clientCode);
		objPOSMenuHeadBean.setStrSubMenuHeadCode(objMenuHeadMasterModel.getStrSubMenuHeadCode());
		objPOSMenuHeadBean.setStrSubMenuHeadName(objMenuHeadMasterModel.getStrSubMenuHeadName());
		objPOSMenuHeadBean.setStrSubMenuHeadShortName(objMenuHeadMasterModel.getStrSubMenuHeadShortName());
		objPOSMenuHeadBean.setStrMenuHeadCode(objMenuHeadMasterModel.getStrMenuCode());
		objPOSMenuHeadBean.setStrSubMenuOperational(objMenuHeadMasterModel.getStrSubMenuOperational());
		objPOSMenuHeadBean.setStrOperationType("U");
		
		if(null==objPOSMenuHeadBean)
		{
			objPOSMenuHeadBean=new clsPOSMenuHeadBean();
			objPOSMenuHeadBean.setStrMenuHeadCode("Invalid Code");
		}
	
		return objPOSMenuHeadBean;
	}
	@RequestMapping(value ="/checkMenuName" ,method =RequestMethod.GET)
	public  @ResponseBody boolean funCheckMenuName(@RequestParam("menuName")  String menuName,@RequestParam("menuCode")  String menuCode,HttpServletRequest req) 
	{
		String clientCode =req.getSession().getAttribute("gClientCode").toString();

		int count=objPOSGlobal.funCheckName(menuName,menuCode,clientCode,"POSMenuHead");
		if(count>0)
		 return false;
		else
			return true;
		
	}
	@RequestMapping(value ="/checkSubMenuName" ,method =RequestMethod.GET)
	public  @ResponseBody boolean checkSubMenuName(@RequestParam("subMenuName") String subMenuName,@RequestParam("subMenuCode") String subMenuCode,HttpServletRequest req) 
	{
		String clientCode =req.getSession().getAttribute("gClientCode").toString();

		int count=objPOSGlobal.funCheckName(subMenuName,subMenuCode,clientCode,"POSSubMenuHead");
		if(count>0)
		 return false;
		else
			return true;
		
	}

	
	@RequestMapping(value = "/loadMenuHeadData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSMenuHeadBean> funGetMenuHeadData(HttpServletRequest req)
	{
		List<clsPOSMenuHeadBean> lstMenuDtl=new ArrayList<clsPOSMenuHeadBean>();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsPOSMenuHeadBean objPOSMenuHeadBean=null;
		try
		{
		clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel();
	    JSONObject jObjLoadData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		    List list =objSer.funLoadAll(objModel,clientCode);
			clsMenuHeadMasterModel objMenuHeadModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objMenuHeadModel = (clsMenuHeadMasterModel) list.get(cnt);
				objPOSMenuHeadBean=new clsPOSMenuHeadBean();
                
				objPOSMenuHeadBean.setStrMenuHeadCode(objMenuHeadModel.getStrMenuCode());
				objPOSMenuHeadBean.setStrMenuHeadName(objMenuHeadModel.getStrMenuName());
				
				lstMenuDtl.add(objPOSMenuHeadBean);
			}
		
		if(null==objPOSMenuHeadBean)
		{
			objPOSMenuHeadBean=new clsPOSMenuHeadBean();
			objPOSMenuHeadBean.setStrMenuHeadCode("Data not found");
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return lstMenuDtl;
	}
	
	@RequestMapping(value = "/loadMenuHeadDtlData", method = RequestMethod.GET)
	public @ResponseBody List<clsPOSMenuHeadBean> funLoadMenuHeadDtlData(HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		
		
		JSONArray jArrMenuHeadList=null;
		List<clsPOSMenuHeadBean> listMenuHedaData=new ArrayList<clsPOSMenuHeadBean>();
		JSONObject jObjMenuHeadData=new JSONObject();
		clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel(); 
		try
		{
		List list =objSer.funLoadAll(objModel,clientCode);
			clsMenuHeadMasterModel objMenuHeadModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objMenuHeadModel = (clsMenuHeadMasterModel) list.get(cnt);
				clsPOSMenuHeadBean objMenuHeadDtl = new clsPOSMenuHeadBean();
				objMenuHeadDtl.setStrMenuHeadCode(objMenuHeadModel.getStrMenuCode());
				objMenuHeadDtl.setStrMenuHeadName(objMenuHeadModel.getStrMenuName());
				String strOperational = objMenuHeadModel.getStrOperational();
				if(strOperational.equalsIgnoreCase("Y"))
					{
					objMenuHeadDtl.setStrOperational("Y");
					}
					else
					{
						objMenuHeadDtl.setStrOperational("N");
					}
				
				listMenuHedaData.add(objMenuHeadDtl);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			return listMenuHedaData;
	}
	
	private Blob funBlankBlob()
	 {
		 Blob blob=new Blob() {
			
			@Override
			public void truncate(long len) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int setBytes(long pos, byte[] bytes, int offset, int len)
					throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int setBytes(long pos, byte[] bytes) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public OutputStream setBinaryStream(long pos) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long position(Blob pattern, long start) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long position(byte[] pattern, long start) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long length() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public byte[] getBytes(long pos, int length) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getBinaryStream(long pos, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getBinaryStream() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void free() throws SQLException {
				// TODO Auto-generated method stub
				
			}
		};
		 return blob;
	 }
	 


}
