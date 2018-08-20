package com.sanguine.service;



import java.util.List;

import com.sanguine.model.clsCompanyLogoModel;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.model.clsProcessSetupModel;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.model.clsTreeMasterModel;
import com.sanguine.webpos.model.clsSetupHdModel;

public interface clsSetupMasterService {
	public void funAddUpdate(clsCompanyMasterModel object);
	
	/*public  List<clsCompanySetupModel> funGetCompanyInfo();*/
	public clsCompanyMasterModel funGetObject(String clientCode);
	
	public List<clsCompanyMasterModel> funGetListCompanyMasterModel();
	
	public void funDeleteProcessSetup(String propertyCode,String clientCode);
	
	public void funAddUpdateProcessSetup(clsProcessSetupModel ProcessSetupModel);
	
	
	public List<clsTreeMasterModel> funGetProcessSetupForms();
	
	public List<clsProcessSetupModel> funGetProcessSetupModelList(String propertyCode,String clientCode);
	
	
	public void funDeleteWorkFlowAutorization(String propertyCode,String clientCode);
	


	public void funDeleteWorkFlowForslabBasedAuth(String propertyCode,String clientCode);
	
	
	
	public clsPropertySetupModel funGetObjectPropertySetup(String propertyCode,String clientCode);
	
	public void funAddUpdatePropertySetupModel(clsPropertySetupModel PropertySetupModel);
	
	
	

	public void funSaveUpdateCompanyLogo(clsCompanyLogoModel comLogo);
	public clsCompanyLogoModel funGetCompanyLogoObject(String strCompanyCode);

	public List<clsTreeMasterModel> funGetAuditForms();
	
	public List<clsCompanyMasterModel> funGetListCompanyMasterModel(String clientCode);
	
	public List<clsSetupHdModel> funGetListSetupModel();
}
