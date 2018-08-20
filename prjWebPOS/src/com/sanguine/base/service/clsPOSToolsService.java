package com.sanguine.base.service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sanguine.base.dao.clsPOSToolsDao;



@Service("clsPOSToolsService")
public class clsPOSToolsService{
	@Autowired
	private clsPOSToolsDao objPOSToolsDao;

	public Map funDBBackup(Map jObj) {
		Map hmRet = new TreeMap<>();
		hmRet = objPOSToolsDao.funDBBackup(jObj);
		return hmRet;
		}
	
	public Map funClearMaster()
	{
		return objPOSToolsDao.funClearMaster();
	}
	
	public Map funClearTransaction()
	{
		return objPOSToolsDao.funClearTransaction();
	}
	
	public Map funUpdateStructure(Map hmData) throws Exception {
		// TODO Auto-generated method stub
		return objPOSToolsDao.funUpdateStructure(hmData);
	}
	
	public Map funCleanMaster(Map hmData) 
	{
		Map hmRet = new HashMap();	
		hmRet = objPOSToolsDao.funCleanMaster(hmData);
		return hmRet;
	}
		
	public Map funCleanTransaction(Map hmData) 
	{
		Map hmRet = new HashMap();	
		hmRet = objPOSToolsDao.funCleanTransaction(hmData);
		return hmRet;
	}

}
