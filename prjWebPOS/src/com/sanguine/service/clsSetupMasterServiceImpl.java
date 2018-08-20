package com.sanguine.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.dao.clsSetupMasterDao;
import com.sanguine.model.clsCompanyLogoModel;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.model.clsProcessSetupModel;
import com.sanguine.model.clsPropertySetupModel;
import com.sanguine.model.clsTreeMasterModel;
import com.sanguine.webpos.model.clsSetupHdModel;


@Service("clsSetupMasterService")
@Transactional(value="webPOSTransactionManager",propagation = Propagation.REQUIRED, readOnly = false)
public class clsSetupMasterServiceImpl implements clsSetupMasterService {
	
	@Autowired
	clsSetupMasterDao objSetupMasterDao;

	@Override
	
	public void funAddUpdate(clsCompanyMasterModel object)
	{
		objSetupMasterDao.funAddUpdate(object);
	}
	
	@Override
	
	public clsCompanyMasterModel funGetObject(String clientCode) {
		
		return objSetupMasterDao.funGetObject(clientCode);
	}

/*
 * (non-Javadoc)
 * @see com.sanguine.service.clsSetupMasterService#funDeleteProcessSetup(java.lang.String, java.lang.String)
 * delete all process setup from tblprocesssetup before insert
 */
	@Override
	
	public void funDeleteProcessSetup(String propertyCode, String clientCode) {
		 objSetupMasterDao.funDeleteProcessSetup(propertyCode, clientCode);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.sanguine.service.clsSetupMasterService#funAddUpdateProcessSetup(com.sanguine.model.clsProcessSetupModel)
	 * service to 
	 */
	
	@Override
	
	public void funAddUpdateProcessSetup(clsProcessSetupModel ProcessSetupModel) {
		objSetupMasterDao.funAddUpdateProcessSetup(ProcessSetupModel);
		
	}

	
	@Override
	
	public List<clsTreeMasterModel> funGetProcessSetupForms() {
		
		return objSetupMasterDao.funGetProcessSetupForms();
	}

	@Override
	
	public List<clsProcessSetupModel> funGetProcessSetupModelList(
			String propertyCode, String clientCode) {
		
		return objSetupMasterDao.funGetProcessSetupModelList(propertyCode, clientCode);
	}

	
	@Override
	
	public void funDeleteWorkFlowAutorization(String propertyCode,String clientCode) {
		objSetupMasterDao.funDeleteWorkFlowAutorization(propertyCode, clientCode);
	}


	
	@Override
	
	public void funDeleteWorkFlowForslabBasedAuth(String propertyCode,String clientCode) {
		objSetupMasterDao.funDeleteWorkFlowForslabBasedAuth(propertyCode, clientCode);
	}




	@Override
	
	public clsPropertySetupModel funGetObjectPropertySetup(String propertyCode,
			String clientCode) {
		return objSetupMasterDao.funGetObjectPropertySetup(propertyCode, clientCode);
	}

	@Override
	
	public void funAddUpdatePropertySetupModel(
			clsPropertySetupModel PropertySetupModel) {
		objSetupMasterDao.funAddUpdatePropertySetupModel(PropertySetupModel);
		
	}





	@Override
	
	public List<clsCompanyMasterModel> funGetListCompanyMasterModel() {
		// TODO Auto-generated method stub
		return objSetupMasterDao.funGetListCompanyMasterModel();
	}

	@Override
	
	public void funSaveUpdateCompanyLogo(clsCompanyLogoModel comLogo) {
		objSetupMasterDao.funSaveUpdateCompanyLogo(comLogo);
		
	}

	@Override
	
	public clsCompanyLogoModel funGetCompanyLogoObject(String strCompanyCode) {
		
		return objSetupMasterDao.funGetCompanyLogoObject(strCompanyCode);
	}

	@Override
	
	public List<clsTreeMasterModel> funGetAuditForms() {
		return objSetupMasterDao.funGetAuditForms();
	}	
	
	@Override
	
	public List<clsCompanyMasterModel> funGetListCompanyMasterModel(String clientCode)
	{
		return objSetupMasterDao.funGetListCompanyMasterModel(clientCode);
	}
	
	@Override
	
	public List<clsSetupHdModel> funGetListSetupModel() {
		// TODO Auto-generated method stub
		return objSetupMasterDao.funGetListSetupModel();
	}
}
