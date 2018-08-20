package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.sanguine.webpos.bean.clsPOSTableMasterBean;
import com.sanguine.webpos.model.clsAreaMasterModel;
import com.sanguine.webpos.model.clsTableMasterModel;
import com.sanguine.webpos.model.clsTableMasterModel_ID;
import com.sanguine.webpos.model.clsWaiterMasterModel;
import com.sanguine.webpos.sevice.clsPOSMasterService;
import com.sanguine.webpos.util.clsPOSUtilityController;

@Controller
public class clsPOSTableMasterController{
	

	
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	clsPOSUtilityController obUtility;
	
	@Autowired
	intfBaseService obBaseService;
	
	//Open POSTableMaster
		@RequestMapping(value = "/frmPOSTableMaster", method = RequestMethod.GET)

		public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSTableMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
		{
				   String clientCode=request.getSession().getAttribute("gClientCode").toString();
			      
		           List listArea=objMasterService.funLoadClientWiseArea(clientCode);
				    //Area List
				     Map areaList = new HashMap<>();
				     areaList.put("All", "All");
				     if(null!=listArea)
				     {
						for (int cnt = 0; cnt < listArea.size(); cnt++)
						{
							clsAreaMasterModel obModel = (clsAreaMasterModel) listArea.get(cnt);
							areaList.put(obModel.getStrAreaCode(), obModel.getStrAreaName());
						}
				     }
				     //POS LIST
					List listPOS=objMasterService.funFullPOSCombo(clientCode);	
					 Map posList = new HashMap<>();
						if(null!=listPOS)
						{
							for (int cnt = 0; cnt < listPOS.size(); cnt++)
							{
								Object Obj[] = (Object[]) listPOS.get(cnt);
								posList.put(Obj[0].toString(),Obj[1].toString());
							}
						}
						//Waiter List
						List listWaiter=objMasterService.funGetWaiterList(clientCode);
						     Map waiterList = new HashMap<>();
						     waiterList.put("All", "All");
						     if(null!=listWaiter)
						     {
						    	 clsWaiterMasterModel obModel;
								for (int cnt = 0; cnt < listWaiter.size(); cnt++)
								{
									obModel=(clsWaiterMasterModel) listWaiter.get(cnt);
									waiterList.put(obModel.getStrWaiterNo(), obModel.getStrWShortName());
								}
						     }
				model.put("areaList", areaList);
				model.put("posList",posList);
				model.put("waiterList", waiterList);
					
		return new ModelAndView("frmPOSTableMaster");
	}
		
		@RequestMapping(value = "/savePOSTableMaster", method = RequestMethod.POST)
		public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSTableMasterBean objBean,BindingResult result,HttpServletRequest req)
		{
			String tableCode = "";
			int intSeq=0;
			try
			{
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				String userCode=req.getSession().getAttribute("gUserCode").toString();
		
				tableCode=objBean.getStrTableNo();
				
				 if (tableCode.trim().isEmpty())
				    {
				    	
				    	List list=obUtility.funGetDocumentCode("POSTableMaster");
						 if (!list.get(0).toString().equals("0"))
							{
								String strCode = "0";
								Object[] obj=(Object[])list.get(0);
								String code = obj[0].toString();
								intSeq= (int)obj[1]+1;
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
									tableCode = "TB000000" + intCode;
								}
								else if (intCode < 100)
								{
									tableCode = "TB00000" + intCode;
								}
								else if (intCode < 1000)
								{
									tableCode = "TB0000" + intCode;
								}
								else if (intCode < 10000)
								{
									tableCode = "TB000" + intCode;
								}
								else if (intCode < 100000)
								{
									tableCode = "TB00" + intCode;
								}
								else if (intCode < 1000000)
								{
									tableCode = "TB0" + intCode;
								}
								else if (intCode < 10000000)
								{
									tableCode = "TB" + intCode;
								}
							}
						    else
						    {
						    	tableCode = "TB0000001";
						    	intSeq=0;
						    }
				    }
				 
				 clsTableMasterModel objModel=new clsTableMasterModel(new clsTableMasterModel_ID(tableCode, clientCode));
				    objModel.setIntSequence(intSeq);
				    objModel.setStrTableName( objBean.getStrTableName());
				    objModel.setStrWaiterNo( objBean.getStrWaiterName());
				    objModel.setStrPOSCode(objBean.getStrPOSCode());
				    objModel.setIntPaxNo(objBean.getIntPaxCapacity());
				    objModel.setStrOperational(objGlobal.funIfNull(objBean.getStrOperational(),"N","Y"));
				    objModel.setStrAreaCode(objBean.getStrAreaName());
				    objModel.setStrStatus("Normal");
				    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
				    objModel.setStrDataPostFlag("N");
				    objModel.setStrUserCreated(userCode);
				    objModel.setStrUserEdited(userCode);
				    objModel.setStrClientCode(clientCode);
				    if(objBean.getStrNCTable().equalsIgnoreCase("No"))
				    {
				    objModel.setStrNCTable("N");
				    }
				    else
				    {
				    objModel.setStrNCTable("Y");	
				    }
				    objMasterService.funSaveTableMaster(objModel);
			
							
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," "+tableCode);
										
				return new ModelAndView("redirect:/frmPOSTableMaster.html");
			}
			catch(Exception ex)
			{
				
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
		
		@RequestMapping(value = "/savePOSTableSequence", method = RequestMethod.POST)
		public ModelAndView funSaveTableSequnces(@ModelAttribute("command") @Valid clsPOSTableMasterBean objBean,BindingResult result,HttpServletRequest req)
		{
			try
			{
				String clientCode=req.getSession().getAttribute("gClientCode").toString();
				
				JSONObject jObjTableMaster=new JSONObject();
				
				//Table Sequence Data
			    
			    List<clsPOSTableMasterBean> list=objBean.getListTableDtl();
			    for(int i=0; i<list.size(); i++)
			    {
			    	clsPOSTableMasterBean obj= new clsPOSTableMasterBean();
			    	
			    	obj=(clsPOSTableMasterBean)list.get(i);
			    	if(null!=obj.getStrTableNo())
		    		{
		    			if(obj.getStrTableNo().contains(","))
		    			{
		    				String[] tblNo=obj.getStrTableNo().split(",");
		    				long sequence=obj.getIntSequence();
					    	String tableNo=tblNo[0];
					    	
					    	clsTableMasterModel obModel=objMasterService.funSelectedTableMasterData(tableNo,clientCode);
					    	obModel.setIntSequence(sequence);
					        objMasterService.funSaveTableMaster(obModel);
					    	
				    		 sequence=obj.getIntSequence()+1;
					    	 tableNo=tblNo[1];
				    		obModel=objMasterService.funSelectedTableMasterData(tableNo,clientCode);
					    	obModel.setIntSequence(sequence);
					        objMasterService.funSaveTableMaster(obModel);
		    			}
		    			else
		    			{
		    				long sequence=obj.getIntSequence();
					    	String tableNo=obj.getStrTableNo();
					    	clsTableMasterModel obModel=objMasterService.funSelectedTableMasterData(tableNo,clientCode);
					    	obModel.setIntSequence(sequence);
					    	 objMasterService.funSaveTableMaster(obModel);
		    			}
		    			
		    		}
			    }
			    
			   							
				req.getSession().setAttribute("success", true);
				req.getSession().setAttribute("successMessage"," Sequence changed");
										
				return new ModelAndView("redirect:/frmPOSTableMaster.html");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return new ModelAndView("redirect:/frmFail.html");
			}
		}
			
	
		@RequestMapping(value = "/loadPOSTableMasterData", method = RequestMethod.GET)
		public @ResponseBody clsPOSTableMasterBean funSetSearchFields(@RequestParam("tableCode") String tableCode,HttpServletRequest req)throws Exception
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			clsPOSTableMasterBean objPOSTableMaster = null;
			clsTableMasterModel obModel=objMasterService.funSelectedTableMasterData(tableCode,clientCode);
			
			try{
				
				if(null!=obModel){
					objPOSTableMaster = new clsPOSTableMasterBean();
					objPOSTableMaster.setStrTableNo(obModel.getStrTableNo());
					objPOSTableMaster.setStrTableName(obModel.getStrTableName());
					
					
					objPOSTableMaster.setStrAreaName(obModel.getStrAreaCode());
					
					
					objPOSTableMaster.setStrWaiterName(obModel.getStrWaiterNo());
					objPOSTableMaster.setIntPaxCapacity(obModel.getIntPaxNo());
					objPOSTableMaster.setStrOperational(obModel.getStrOperational());
					if(obModel.getStrNCTable().equalsIgnoreCase("N"))
					{
					objPOSTableMaster.setStrNCTable("No");
					}
					else
					{
					objPOSTableMaster.setStrNCTable("Yes");
					}
					objPOSTableMaster.setStrPOSCode(obModel.getStrPOSCode());
				}
				if(null==objPOSTableMaster)
				{
					objPOSTableMaster = new clsPOSTableMasterBean();
					objPOSTableMaster.setStrTableNo("Invalid Code");
				}
				
		        
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			return objPOSTableMaster;
		}
	

		@RequestMapping(value = "/loadPOSTableData", method = RequestMethod.GET)
		public @ResponseBody List<clsPOSTableMasterBean> funLoadTableData(HttpServletRequest req)
		{
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			
			List<clsPOSTableMasterBean> listTableData=new ArrayList<clsPOSTableMasterBean>();
			try{
				StringBuilder sql=new StringBuilder("select a.strTableNo,a.strTableName,ifnull(b.strAreaName,''),ifnull(c.strPosName,'All') "
	                    + " from tbltablemaster a left outer join tblareamaster b on a.strAreaCode=b.strAreaCode "
	                    + " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
	                    + "ORDER by a.intSequence");
				
				List list=obBaseService.funGetList(sql, "sql");
				if(list.size()>0){
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						clsPOSTableMasterBean objtbl = new clsPOSTableMasterBean();
						objtbl.setStrTableNo(obj[0].toString());
						objtbl.setStrTableName(obj[1].toString());
						objtbl.setStrAreaName(obj[2].toString());
						objtbl.setStrPOSCode(obj[3].toString());	
						listTableData.add(objtbl);
					}
				}
				

				
			}catch(Exception e){
				e.printStackTrace();
			}
			return listTableData;
		}

		 @RequestMapping(value ="/checkTableName" ,method =RequestMethod.GET)
			public  @ResponseBody boolean funCheckPOSName(@RequestParam("name")  String name,@RequestParam("code")  String code,HttpServletRequest req) 
			{
				String clientCode =req.getSession().getAttribute("gClientCode").toString();

				int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSTableMaster");
				if(count>0)
				 return false;
				else
					return true;
				
			}
}
