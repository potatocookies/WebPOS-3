package com.sanguine.base.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.base.dao.clsSetupDao;


@Service("clsSetupService")
public class clsSetupService
{
	@Autowired
	private clsSetupDao	objSetupDao;

	

	public JSONObject funGetParameterValuePOSWise(String clientCode, String posCode, String parameterName)
	{
		return objSetupDao.funGetParameterValuePOSWise(clientCode,posCode,parameterName);
	}
	
	public JSONObject funGetAllParameterValuesPOSWise(String clientCode, String posCode)
	{
		return objSetupDao.funGetAllParameterValuesPOSWise(clientCode,posCode);
	}
	
	
	public void funSetToken(String token, String posCode, String mid)
	{
		 objSetupDao.funSetToken(token,posCode,mid);
	}
	
	public JSONObject funGetPos(String newPropertyPOSCode)
	{
		return objSetupDao.funGetPos(newPropertyPOSCode);
	}
	
	public JSONObject funGetPOSClientCode()
	{
		return objSetupDao.funGetPOSClientCode();
	}
	
	
}
