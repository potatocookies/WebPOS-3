package com.sanguine.webpos.sevice;

import org.springframework.beans.factory.annotation.Autowired;
import com.sanguine.base.service.intfBaseService;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.webpos.bean.clsPOSBillDtl;
import com.sanguine.webpos.bean.clsPOSTableMasterBean;


@Service
public class clsPOSMakeKOTService 
{
	@Autowired
	private intfBaseService objBaseService;
	
	public Map funGetCustomerAddress(String mobileNo)
	{
		List list =null;
		Map objSettle=new HashMap<>();
		StringBuilder sql = new StringBuilder(); 
		try{
			
			sql.append("select a.strCustomerCode,a.strCustomerName,a.longMobileNo,a.strBuldingCode,ifnull(a.strCustAddress,'')  "
                    + ",ifnull(a.strStreetName,''),ifnull(a.strLandmark,''),ifnull(a.intPinCode,''),ifnull(a.strCity,''),ifnull(a.strState,'') "
                    + ",a.strOfficeBuildingCode,ifnull(a.strOfficeBuildingName,''),ifnull(a.strOfficeStreetName,''),ifnull(a.strOfficeLandmark,''),ifnull(a.intPinCode,'') "
                    + ",ifnull(a.strOfficeCity,''),ifnull(a.strOfficeState,'') "
                    + ",ifnull(a.strTempAddress,''),ifnull(a.strTempStreet,''),ifnull(a.strTempLandmark,'') "
                    + "from  tblcustomermaster a "
                    + "where longMobileNo='" + mobileNo + "' ");
			//Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);	
			//list = query.list();
			list = objBaseService.funGetList(sql, "sql");
			
 			 if (list.size()>0)
 				{
 				
					Object[] obj=(Object[])list.get(0);
					
					
					objSettle.put("strCustomerName",obj[1].toString());
					objSettle.put("longMobileNo",obj[2].toString());
					objSettle.put("strCustAddress",obj[4].toString());
					objSettle.put("strStreetName",obj[5].toString());
					objSettle.put("strLandmark",obj[6].toString());
					
					objSettle.put("intPinCode",obj[7].toString());
					objSettle.put("strCity",obj[8].toString());
					objSettle.put("strState",obj[9].toString());
					objSettle.put("strOfficeAddress",obj[11].toString());
					objSettle.put("strOfficeStreetName",obj[12].toString());
					objSettle.put("strOfficeLandmark",obj[13].toString());
					objSettle.put("intOfficePinCode",obj[14].toString());
						
					objSettle.put("strOfficeCity",obj[15].toString());
					objSettle.put("strOfficeState",obj[16].toString());
					objSettle.put("strTempAddress",obj[17].toString());
					objSettle.put("strTempStreet",obj[18].toString());
					objSettle.put("strTempLandmark",obj[19].toString());
					
					
				
            }
 			
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				return objSettle;
			}
	}
	
}
