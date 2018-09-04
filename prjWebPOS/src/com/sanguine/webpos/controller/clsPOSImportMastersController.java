package com.sanguine.webpos.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSAreaMasterBean;
import com.sanguine.webpos.sevice.clsPOSMasterService;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

@Controller
public class clsPOSImportMastersController {
	
	@Autowired
	clsPOSMasterService objMasterService;
	
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@RequestMapping(value = "/frmPOSImportExcelFile", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPOSAreaMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request)throws Exception
	{
		
		String urlHits = "1";
		try {
			urlHits = request.getParameter("saddr").toString();
		} catch (NullPointerException e) {
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		
		List listOfMasters = new ArrayList();
		listOfMasters.add("Customer");
		listOfMasters.add("Item");
		
		List listOfIndustryType = new ArrayList();
		listOfIndustryType.add("F&B");
		listOfIndustryType.add("Retail");
		
		model.put("listOfMasters", listOfMasters);
		model.put("listOfIndustryType", listOfIndustryType);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSImportExcelFile_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSImportExcelFile");
		}else {
			return null;
		}
	}

	@RequestMapping(value = "/funExportData", method = RequestMethod.GET)
	public @ResponseBody String funExportData(HttpServletRequest req)throws Exception
	{
		String strRet="";
		try
		{
		    HSSFWorkbook hwb = new HSSFWorkbook();
		    HSSFSheet sheet = hwb.createSheet("new sheet");
		    HSSFRow rowhead = sheet.createRow((short) 0);

		    rowhead.createCell((short) 0).setCellValue("Item Code");
		    rowhead.createCell((short) 1).setCellValue("Item Name");
		    rowhead.createCell((short) 2).setCellValue("Short Name");
		    rowhead.createCell((short) 3).setCellValue("Menu Name");
		    rowhead.createCell((short) 4).setCellValue("SubMenuHead");
		    rowhead.createCell((short) 5).setCellValue("RevenueHead");
		    rowhead.createCell((short) 6).setCellValue("POS Name");
		    rowhead.createCell((short) 7).setCellValue("Sub Group Name");
		    rowhead.createCell((short) 8).setCellValue("Group Name");
		    rowhead.createCell((short) 9).setCellValue("Cost Center");
		    rowhead.createCell((short) 10).setCellValue("Area Name");
		    rowhead.createCell((short) 11).setCellValue("Tax");
		    rowhead.createCell((short) 12).setCellValue("PuChase Rate");
		    rowhead.createCell((short) 13).setCellValue("External Code");
		    rowhead.createCell((short) 14).setCellValue("Item Details");
		    rowhead.createCell((short) 15).setCellValue("Item Type");
		    rowhead.createCell((short) 16).setCellValue("Apply DisCount(Yes/No)");
		    rowhead.createCell((short) 17).setCellValue("StoCk In Enable");
		    rowhead.createCell((short) 18).setCellValue("Sun PriCe");
		    rowhead.createCell((short) 19).setCellValue("Mon PriCe");
		    rowhead.createCell((short) 20).setCellValue("Tue PriCe");
		    rowhead.createCell((short) 21).setCellValue("Wed PriCe");
		    rowhead.createCell((short) 22).setCellValue("Thu PriCe");
		    rowhead.createCell((short) 23).setCellValue("Fri PriCe");
		    rowhead.createCell((short) 24).setCellValue("Sat PriCe");
		    rowhead.createCell((short) 25).setCellValue("Counter");
		    rowhead.createCell((short) 26).setCellValue("Received UOM");
		    rowhead.createCell((short) 27).setCellValue("Recipe UOM");
		    rowhead.createCell((short) 28).setCellValue("Raw Material");

		    rowhead.createCell((short) 29).setCellValue("Hourly Price(YES/NO)");
		    rowhead.createCell((short) 30).setCellValue("From Time(24 HRS)");
		    rowhead.createCell((short) 31).setCellValue("To Time(24 HRS)");

		    StringBuilder sqlBuilder = new StringBuilder("SELECT ifnull(b.strItemCode,'NA'),ifnull(b.strItemName,'NA'),ifnull(b.strShortName,'NA'), IFNULL(c.strMenuName,'NA'),IFNULL(g.strSubMenuHeadShortName,'NA'),ifnull(b.strRevenueHead,'NA') "
			    + ",IFNULL(h.strPosName,'All'),ifnull(e.strSubGroupName,'NA'),ifnull(f.strGroupName,'NA'),IFNULL(d.strCostCenterName,'NA'),IFNULL(i.strAreaName,'NA'),ifnull(b.strTaxIndicator,'NA') "
			    + ",ifnull(b.dblPurchaseRate,0.0),ifnull(b.strExternalCode,'NA'),ifnull(b.strItemDetails,'NA'),ifnull(b.strItemType,'NA'),ifnull(b.strDiscountApply,'NA'),ifnull(b.strStockInEnable,'NA'),IFNULL(a.strPriceSunday,0) "
			    + ",IFNULL(a.strPriceMonday,0),IFNULL(a.strPriceTuesday,0),IFNULL(a.strPriceWednesday,0),IFNULL(a.strPriceThursday,0) "
			    + ",IFNULL(a.strPriceFriday,0),IFNULL(a.strPriceSaturday,0),'NA',ifnull(b.strUOM,'NA'),ifnull(b.strRecipeUOM,'NA'),ifnull(b.strRawMaterial,'NA'),ifnull(a.strHourlyPricing,'NO'),ifnull(a.tmeTimeFrom,'HH:MM:S'),ifnull(a.tmeTimeTo,'HH:MM:S') "
			    + "FROM tblitemmaster b "
			    + "LEFT OUTER JOIN  tblmenuitempricingdtl a ON a.strItemCode=b.strItemCode "
			    + "LEFT OUTER JOIN tblsubgrouphd e ON b.strSubGroupCode=e.strSubGroupCode "
			    + "LEFT OUTER JOIN tblgrouphd f ON e.strGroupCode=f.strGroupCode "
			    + "LEFT OUTER JOIN tblmenuhd c ON a.strMenuCode = c.strMenuCode "
			    + "LEFT OUTER JOIN tblcostcentermaster d ON a.strCostCenterCode=d.strCostCenterCode "
			    + "LEFT OUTER JOIN tblsubmenuhead g ON a.strSubMenuHeadCode=g.strSubMenuHeadCode "
			    + "LEFT OUTER JOIN tblposmaster h ON a.strPosCode=h.strPosCode "
			    + "LEFT OUTER JOIN tblareamaster i ON a.strAreaCode=i.strAreaCode "
			    + "ORDER BY b.strItemCode,a.strPosCode ");
		    System.out.println("sql=" + sqlBuilder.toString());
		    List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		  
		   if(list.size()>0)
		    {
			   for(int i=0;i<list.size();i++)
			   {  
				   Object[] obj = (Object[])list.get(i);
					HSSFRow row = sheet.createRow(i);
					row.createCell((short) 0).setCellValue(obj[0].toString());
					row.createCell((short) 1).setCellValue(obj[1].toString());
					row.createCell((short) 2).setCellValue(obj[2].toString());
					row.createCell((short) 3).setCellValue(obj[3].toString());
					row.createCell((short) 4).setCellValue(obj[4].toString());
					row.createCell((short) 5).setCellValue(obj[5].toString());
					row.createCell((short) 6).setCellValue(obj[6].toString());
					row.createCell((short) 7).setCellValue(obj[7].toString());
					row.createCell((short) 8).setCellValue(obj[8].toString());
					row.createCell((short) 9).setCellValue(obj[9].toString());
					row.createCell((short) 10).setCellValue(obj[10].toString());
					row.createCell((short) 11).setCellValue(obj[11].toString());
					row.createCell((short) 12).setCellValue(obj[12].toString());
					row.createCell((short) 13).setCellValue(obj[13].toString());
					row.createCell((short) 14).setCellValue(obj[14].toString());
					row.createCell((short) 15).setCellValue(obj[15].toString());
					row.createCell((short) 16).setCellValue(obj[16].toString());
					row.createCell((short) 17).setCellValue(obj[17].toString());
					row.createCell((short) 18).setCellValue(obj[18].toString());
					row.createCell((short) 19).setCellValue(obj[19].toString());
					row.createCell((short) 20).setCellValue(obj[20].toString());
					row.createCell((short) 21).setCellValue(obj[21].toString());
					row.createCell((short) 22).setCellValue(obj[22].toString());
					row.createCell((short) 23).setCellValue(obj[23].toString());
					row.createCell((short) 24).setCellValue(obj[24].toString());
					row.createCell((short) 25).setCellValue(obj[25].toString());
					row.createCell((short) 26).setCellValue(obj[26].toString());
					row.createCell((short) 27).setCellValue(obj[27].toString());
					row.createCell((short) 28).setCellValue(obj[28].toString());
		
					row.createCell((short) 29).setCellValue(obj[29].toString());
					row.createCell((short) 30).setCellValue(obj[30].toString());
					row.createCell((short) 31).setCellValue(obj[31].toString());
			   }
			
		    }

		    String filePath = System.getProperty("user.dir");
		    File file = new File(filePath + "/Valid Item Master.xls");
		    FileOutputStream fileOut = new FileOutputStream(file);
		    hwb.write(fileOut);
		    fileOut.close();
		    strRet = "Data Exported Successfully!!!";
		  
		}
		catch (FileNotFoundException ex)
		{
			strRet = "File is already opened please close";
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return strRet;
		
	}
			
	
	@RequestMapping(value ="/importExcelData" ,method =RequestMethod.POST)
	public  @ResponseBody String funImportExcel(@RequestParam("file") MultipartFile excelFile,@RequestParam("masterName") String masterName,@RequestParam("industryType") String industryType,HttpServletRequest req) throws IOException, BiffException 
	{
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posCode = req.getSession().getAttribute("gPOSCode").toString();
		String strRet="";
		boolean flgImport=false;
		 //Creates a workbook object from the uploaded excelfile
		 Workbook workbook = Workbook.getWorkbook(excelFile.getInputStream());
        //Creates a worksheet object representing the first sheet
		 Sheet sheet = workbook.getSheet(0);
		if(masterName.equalsIgnoreCase("Item"))
		{
			if (funCheckEmptyDB())
			{
				
			    if (funReadExcelFile(sheet))
			    {
			    	flgImport = funGenerateCode(industryType,userCode,clientCode,posCode);
			    	if(flgImport)
			    	{
			    		strRet = "Data Imported Successfully";
			    	}
			    }
			    else
			    {
			    	strRet="Invalid Excel File";
			    }
			} 
			else
			{
				strRet = "Data is present in Database, This module requires Blank Database.";
			}
		}
		else
		{
			if (funCheckEmptyDBForCustomer(clientCode))
			{
			    if (funReadExcelAndInsertCustomerData(sheet,userCode,clientCode))
			    {
				flgImport = funGenerateCustMaster(userCode,clientCode);
				flgImport = true;
				if(flgImport)
				{
					strRet="Data Imported Successfully";
				}
			    }
			    else
			    {
			    	strRet = "Invalid Excel File";
			    }
			  
			}
			else
			{
				strRet = "Data is present in Database, This module requires Blank Database.";
			}
			
		    
		}
		
		return strRet;
	}
	
	 private boolean funGenerateCustMaster(String userCode,String clientCode)
	    {
		System.out.println("In Gen Cust Master");
		boolean flgReturn = false;
		funGenerateCustArea(userCode,clientCode);
		return flgReturn;
	    }
	 
	   private boolean funGenerateCustArea(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strBuildingName) from tblcustomermaster");
		    List listBuilding = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    String strZoneCode = "";
		   if(listBuilding.size()>0)
		    {
			 for(int i=0;i<listBuilding.size();i++)
			 {
			Object obj = listBuilding.get(i);	
			docNo++;
			code = "B" + String.format("%07d", docNo);
			sqlBuilder.setLength(0);
		    sqlBuilder.append("insert into tblbuildingmaster (strBuildingCode,strBuildingName,"
				+ "strAddress,strUserCreated,strUserEdited,dteDateCreated,"
				+ "dteDateEdited,strClientCode,strZoneCode) "
				+ "values('" + code + "','" + obj.toString() + "','',"
				+ "'" + userCode + "','" + userCode + "',"
				+ "'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" +objGlobal.funGetCurrentDateTime("yyyy-MM-dd")+ "',"
				+ "'" + clientCode + "','" + strZoneCode + "')");

			int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			if (insert == 1)
			{
				sqlBuilder.setLength(0);
			    sqlBuilder.append("update tblcustomermaster set strBuldingCode='" + code + "' "
				    + "where strBuildingName='" + obj.toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			}
		    }
		    }
		    flgReturn = true;
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	
	public boolean funCheckEmptyDBForCustomer(String clientCode)
    {
	boolean flgResult = false;
	int custCount = 0, buildingCount = 0;

	try
	{
	    StringBuilder sqlBuilder = new StringBuilder();
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("select count(strCustomerCode) from tblcustomermaster "
		    + "where strClientCode='" + clientCode + "'");
	    List listCustomer = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if(listCustomer.size()>0)
	    {
	    	custCount = Integer.parseInt(listCustomer.get(0).toString());
	    }
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("select count(strBuildingCode) from tblbuildingmaster "
		    + "where strClientCode='" + clientCode + "'");
	    List listBuilding = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
	    if(listBuilding.size()>0)
	    {
	    	 buildingCount = Integer.parseInt(listBuilding.get(0).toString());
	    }
	    if (custCount == 0 && buildingCount == 0)
	    {
		flgResult = true;
	    }

	}
	catch (Exception e)
	{
	    flgResult = false;
	    e.printStackTrace();
	}
	finally
	{
	    return flgResult;
	}
    }
	
	 private boolean funReadExcelAndInsertCustomerData(Sheet sheet,String userCode,String clientCode)
	    {
		boolean flgResult = false;
		String query = "";
		
		try
		{
		  
		    
		    // Loop over first 10 column and lines

		    objBaseServiceImpl.funExecuteUpdate("truncate table tblcustomermaster","sql");
		    StringBuilder queryBuilder = new StringBuilder();
		    StringBuilder rowBuilder = new StringBuilder();

		    queryBuilder.setLength(0);
		    rowBuilder.setLength(0);

		    String sqlInsert = "INSERT INTO `tblcustomermaster` "
			    + "(`strCustomerCode`, `strCustomerName`, `strBuldingCode`, `strBuildingName`, `strStreetName`, `strLandmark`, `strArea`,"
			    + " `strCity`, `strState`, `intPinCode`, `longMobileNo`, `longAlternateMobileNo`, `strOfficeBuildingCode`, "
			    + "`strOfficeBuildingName`, `strOfficeStreetName`, `strOfficeLandmark`, `strOfficeArea`, `strOfficeCity`, "
			    + "`strOfficePinCode`, `strOfficeState`, `strOfficeNo`, `strUserCreated`, `strUserEdited`, `dteDateCreated`, "
			    + "`dteDateEdited`, `strDataPostFlag`, `strClientCode`, `strOfficeAddress`, `strExternalCode`, `strCustomerType`, "
			    + "`dteDOB`, `strGender`, `dteAnniversary`, `strEmailId`, `strCRMId`, `strCustAddress`) VALUES ";

		    queryBuilder.append(sqlInsert);

		    long lastNo = 1;
		    String propertCode = clientCode.substring(4);

		    System.out.println("sheet.getRows()->" + sheet.getRows());
		    Integer insertLimit = 500;
		    boolean isEOF = false;
		    for (int row = 1; row < sheet.getRows(); row++)
		    {
			Cell cell = sheet.getCell(0, row);
			String contents = cell.getContents().trim();

			String customerCode = propertCode + "C" + String.format("%07d", lastNo++);

			if (row <= insertLimit)
			{
			    rowBuilder.setLength(0);
			    rowBuilder.append("(");
			    for (int col = 0; col < 36; col++)
			    {
				cell = sheet.getCell(col, row);
				CellType type = cell.getType();
				contents = cell.getContents().trim();
				contents = contents.replaceAll("\\s", " ");
				contents = contents.replaceAll("/", " ");

				contents = contents.replaceAll("'", " ");
				contents = contents.replaceAll("--", " ");
				contents = contents.replaceAll("\\W", " ");
				if (col == 21)//strUserCreated
				{
				    rowBuilder.append("'" + userCode + "',");
				}
				else if (col == 22)//strUserEdited
				{
				    rowBuilder.append("'" + userCode + "',");
				}
				else if (col == 23)//dteDateCreated
				{
				    rowBuilder.append("'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',");
				}
				else if (col == 24)//dteDateEdited
				{
				    rowBuilder.append("'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',");
				}
				else if (col == 25)//strDataPostFlag
				{
				    rowBuilder.append("'N',");
				}
				else if (col == 26)//strClientCode
				{
				    rowBuilder.append("'" + clientCode + "',");
				}
				else if (col == 35)//cust address
				{
				    rowBuilder.append("'" + contents + "'");
				}
				else
				{
				    rowBuilder.append("'" + contents + "',");
				}

			    }
			    rowBuilder.append("),");

			    queryBuilder.append(rowBuilder.toString());

			    if (row == insertLimit)
			    {
				queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));

				objBaseServiceImpl.funExecuteUpdate(queryBuilder.toString(),"sql");

				insertLimit = insertLimit + 500;
				if (insertLimit > sheet.getRows())
				{
				    insertLimit = sheet.getRows();
				}

				queryBuilder.setLength(0);
				queryBuilder.append(sqlInsert);
			    }

			}
			else
			{
			    queryBuilder.deleteCharAt(queryBuilder.lastIndexOf(","));

			    
			    objBaseServiceImpl.funExecuteUpdate(queryBuilder.toString(),"sql");

			    queryBuilder.setLength(0);
			    queryBuilder.append(sqlInsert);
			    rowBuilder.setLength(0);
			    rowBuilder.append("(");
			    for (int col = 0; col < 36; col++)
			    {
				cell = sheet.getCell(col, row);
				CellType type = cell.getType();
				contents = cell.getContents().trim();
				contents = contents.replaceAll("\\s", " ");
				contents = contents.replaceAll("/", ",");
				contents = contents.replaceAll("'", ",");

				if (col == 21)//strUserCreated
				{
				    rowBuilder.append("'" + userCode + "',");
				}
				else if (col == 22)//strUserEdited
				{
				    rowBuilder.append("'" + userCode + "',");
				}
				else if (col == 23)//dteDateCreated
				{
				    rowBuilder.append("'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',");
				}
				else if (col == 24)//dteDateEdited
				{
				    rowBuilder.append("'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',");
				}
				else if (col == 25)//strDataPostFlag
				{
				    rowBuilder.append("'N',");
				}
				else if (col == 26)//strClientCode
				{
				    rowBuilder.append("'" + clientCode + "',");
				}
				else if (col == 35)//cust address
				{
				    rowBuilder.append("'" + contents + "'");
				}
				else
				{
				    rowBuilder.append("'" + contents + "',");
				}

			    }
			    rowBuilder.append("),");
			
			    queryBuilder.append(rowBuilder.toString());

			    insertLimit += insertLimit;
			}
		    }


		    flgResult = true;
		}
		catch (Exception e)
		{
//		    JOptionPane.showMessageDialog(null, "Invalid Excel File");
		    e.printStackTrace();
		}

		return flgResult;
	    }
	
	public boolean funGenerateCode(String industryType,String userCode,String clientCode,String posCode)
    {
	boolean flgReturn = false;
	if (industryType.equals("F"))
	{
	    funGeneratePOS(userCode);
	    funGenerateGroup(userCode,clientCode);
	    funGenerateSubGroup(userCode,clientCode);
	    funGenerateMenuHead(userCode,clientCode);
	    funGenerateSubMenuHead(userCode,clientCode);
	    funGenerateItemMaster(userCode,clientCode);
	    funGenerateCostCenter(userCode,clientCode);
	    funGenerateCounterMasterHd(userCode,clientCode,posCode);
	    funGenerateCounterMasterDtl(userCode,clientCode);
	    funGenerateAreaMaster(userCode,clientCode);
	    funGenerateMenuItemPriceHD(userCode);
	    flgReturn = funGenerateMenuItemPriceDTL(userCode,clientCode);
	}
	else
	{
	    funGenerateItemMasterForRetail(userCode,clientCode);
	    funGenerateGroup(userCode,clientCode);
	    funGenerateSubGroup(userCode,clientCode);
	    funGenerateMenuHead(userCode,clientCode);
	    funGenerateSubMenuHead(userCode,clientCode);
	    funGenerateCostCenter(userCode,clientCode);
	    funGenerateCounterMasterHd(userCode,clientCode,posCode);
	    funGenerateCounterMasterDtl(userCode,clientCode);
	    flgReturn = funGenerateAreaMaster(userCode,clientCode);
	}
	if(flgReturn)
	{
		
	}
	else
	{
		
	}
	return flgReturn;
    }
	
	
	 private boolean funGeneratePOS(String userCode)
	    {
		
		boolean flgReturn = false;
		String code = "";
		long docNo = 0;
		try
		{
		    //  clsGlobalVarClass.dbMysql.execute("truncate table tblposmaster");
		    StringBuilder sqlBuilder = new StringBuilder("select distinct(strPOSName) from tblimportexcel");
		    List listPOSMaster = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listPOSMaster.size()>0)
		    {
	    	for(int i=0;i<listPOSMaster.size();i++)
		    {
//			lblMessage.setText("");
//			lblMessage.setText("Importing POS Master");
	    		Object obj = listPOSMaster.get(i);
			String posName = obj.toString();

			StringBuilder sqlBuilderNameCheck = new StringBuilder(" select a.strPosCode from tblposmaster a where a.strPosName='" + posName + "' ");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if (listNameCheck.size()>0)
			{
			    code = (String) listNameCheck.get(0);
			    StringBuilder strBuilder = new StringBuilder("update tblimportexcel set strPOSCode='" + code + "' "
				    + "where strPOSName='" + posName + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
			}
			else
			{
			    if (posName.length() > 0 && !posName.equalsIgnoreCase("All"))
			    {
				StringBuilder sqlDocBuilder = new StringBuilder(" select ifnull(max(MID(a.strPosCode,2,2)),'0' )as strPosCode "
					+ " from tblposmaster a  ");
				List listOfDocCode = objBaseServiceImpl.funGetList(sqlDocBuilder, "sql");
				if (listOfDocCode.size()>0)
				{
					docNo = Long.parseLong((String)listOfDocCode.get(0)) + 1;
				    code = "P" + String.format("%02d", docNo);
				}
				else
				{
				    docNo++;
				    code = "P" + String.format("%02d", docNo);
				}
				sqlBuilder.setLength(0);
				sqlBuilder.append("insert into tblposmaster(strPosCode,strPosName,strPosType,strDebitCardTransactionYN,"
					+ "strPropertyPOSCode,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited"
					+ ",strCounterWiseBilling,strPrintVatNo,strPrintServiceTaxNo,strVatNo,strServiceTaxNo) "
					+ "values('" + code + "','" + posName + "','Dine In','No',''"
					+ ",'" + userCode + "','" + userCode + "',"
					+ "'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
					+ ",'No','N','N','','')");
				int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
					sqlBuilder.append("update tblimportexcel set strPOSCode='" + code + "' "
					    + "where strPOSName='" + posName + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				}
			    }
			}
		    }
		    }
		   
		    objBaseServiceImpl.funExecuteUpdate("update tblimportexcel  set strPOSCode='All' where strPOSName='All' ","sql");
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	 
	 
	  private boolean funGenerateGroup(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    //clsGlobalVarClass.dbMysql.execute("truncate table tblgrouphd");
		    StringBuilder sqlBuilder = new StringBuilder("select distinct(strGroupName) from tblimportexcel");
		    List listGroup = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listGroup.size()>0)
		    {
		    for(int i=0;i<listGroup.size();i++)
		    {	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Group Master");
		    Object obj = listGroup.get(i);	
			StringBuilder sqlBuilderNameCheck = new StringBuilder(" select a.strGroupCode from tblgrouphd a where a.strGroupName='" + obj.toString() + "' ");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if(listNameCheck.size()>0)
			{
				code = (String) listNameCheck.get(0);
			    sqlBuilder.setLength(0);
				sqlBuilder.append( "update tblimportexcel set strGroupCode='" + code + "' "
				    + "where strGroupName='" + obj.toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			}
			else
			{
			    if (obj.toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strGroupCode,2,7)),'0' )as strGroupCode  from tblgrouphd a   ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "G" + String.format("%07d", docNo);
				}
				else
				{
				    docNo++;
				    code = "G" + String.format("%07d", docNo);
				}
				sqlBuilder.setLength(0);
				sqlBuilder.append("insert into tblgrouphd (strGroupCode,strGroupName,strUserCreated,"
					+ "strUserEdited,dteDateCreated,dteDateEdited,strClientCode)"
					+ "values('" + code + "','" + obj.toString() + "','" + userCode + "',"
					+ "'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',"
					+ "'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "')");
				int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
					sqlBuilder.append("update tblimportexcel set strGroupCode='" + code + "' "
					    + "where strGroupName='" + obj.toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				}
			    }

			}
			
		    }
		    }
		   
		    flgReturn = true;
		}
		catch (Exception e)
		{
		   
		}
		finally
		{
		    return flgReturn;
		}
	    }
	  
	  private boolean funGenerateSubGroup(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    //clsGlobalVarClass.dbMysql.execute("truncate table tblsubgrouphd");
		    StringBuilder sqlBuilder = new StringBuilder("select distinct(strSubGroupName),strGroupCode from tblimportexcel");
		    List listSubGroup = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if (listSubGroup.size()>0)
		    {
	    	for(int i=0;i<listSubGroup.size();i++)
	    	{	
	    	Object[] obj = (Object[])listSubGroup.get(0);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Sub Group Master");
			StringBuilder sqlBuilderNameCheck = new StringBuilder(" select a.strSubGroupCode from tblsubgrouphd a where a.strSubGroupName='" + obj[0].toString() + "' ");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if(listNameCheck.size()>0)
			{
				 code = (String) listNameCheck.get(0);
				    sqlBuilder.setLength(0);
					sqlBuilder.append("update tblimportexcel set strSubGroupCode='" + code + "' "
					    + "where strSubGroupName='" + obj[0].toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			}
			else
			{
			    if (obj[0].toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strSubGroupCode,3,7)),'0' )as strSubGroupCode  from tblsubgrouphd a   ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilderDoc, "sql");

				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "SG" + String.format("%07d", docNo);
				  
				}
				else
				{
				    docNo++;
				    code = "SG" + String.format("%07d", docNo);
				}

				sqlBuilder.setLength(0);
				sqlBuilder.append("insert into tblsubgrouphd (strSubGroupCode,strSubGroupName,strGroupCode,"
					+ "strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode)"
					+ "values('" + code + "','" + obj[0].toString() + "','" + obj[1].toString() + "',"
					+ "'" + userCode + "','" + userCode + "',"
					+ "'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','"
					+ objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "')");
				int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
					sqlBuilder.append("update tblimportexcel set strSubGroupCode='" + code + "' "
					    + "where strSubGroupName='" + obj[0].toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
				}
			    }
			}
			
		    }
		    }
		 
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	  
	  private boolean funGenerateMenuHead(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder("select distinct(strMenuHeadName) from tblimportexcel");
		    List listMenuHead = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listMenuHead.size()>0)
		    {
		    for(int i=0;i<listMenuHead.size();i++)
		    {
		    Object obj = listMenuHead.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Menu Head");
			StringBuilder sqlBuilderNameCheck = new StringBuilder("select a.strMenuCode from tblmenuhd a where a.strMenuName='" + obj.toString() + "'");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if(listNameCheck.size()>0)
			{
			    code = listNameCheck.get(0).toString();
			   sqlBuilder.setLength(0);
			   sqlBuilder.append("update tblimportexcel set strMenuHeadCode='" + code + "' "
				    + "where strMenuHeadName='" + obj.toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(),"sql");
			}
			else
			{
			    if (obj.toString().trim().length() > 0)
			    {
			    sqlBuilder.setLength(0);
			    sqlBuilder.append( " select ifnull(max(MID(a.strMenuCode,2,6)),'0' )as strMenuCode  from tblmenuhd a ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "M" + String.format("%06d", docNo);;
				}
				else
				{
				    docNo++;
				    code = "M" + String.format("%06d", docNo);
				}
				

				sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblmenuhd (strMenuCode,strMenuName,strUserCreated,strUserEdited,"
					+ "dteDateCreated,dteDateEdited,strClientCode,strOperational,imgImage) "
					+ "values('" + code + "','" + obj.toString() + "','" + userCode + "'"
					+ ",'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
					+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "','Y','' )");
				int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql"); 
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
				    sqlBuilder.append("update tblimportexcel set strMenuHeadCode='" + code + "' "
					    + "where strMenuHeadName='" + obj.toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				}
			    }
			    flgReturn = true;
			}
			
		
		    }
		    }
		  

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	  
	  private boolean funGenerateSubMenuHead(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strSubMenuHeadName),strMenuHeadCode from tblimportexcel");
		    List listSubMenuHead = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listSubMenuHead.size()>0)
		    {
		    for(int i=0;i<listSubMenuHead.size();i++)
		    {
		    Object[] obj = (Object[])listSubMenuHead.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Sub MenuHead Master");
			StringBuilder sqlBuilderNameCheck = new StringBuilder("select a.strSubMenuHeadCode from tblsubmenuhead a where a.strSubMenuHeadName='" + obj[0].toString() + "'");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if (listNameCheck.size()>0)
			{
				code = listNameCheck.get(0).toString();
				sqlBuilder.setLength(0);
			    sqlBuilder.append("update tblimportexcel set strSubMenuHeadCode='" + code + "' "
				    + "where strSubMenuHeadName='" + obj.toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			}
			else
			{
				if (obj[0].toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strSubMenuHeadCode,3,6)),'0' )as strSubMenuHeadCode  from tblsubmenuhead a ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilderDoc, "sql");
				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "SM" + String.format("%06d", docNo);
				}
				else
				{
				    docNo++;
				    code = "SM" + String.format("%06d", docNo);
				}

				sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblsubmenuhead (strSubMenuHeadCode,strMenuCode,strSubMenuHeadShortName,"
					+ "strSubMenuHeadName,strSubMenuOperational,strUserCreated,strUserEdited,dteDateCreated,"
					+ "dteDateEdited,strClientCode)"
					+ " values('" + code + "','" + obj[1].toString() + "',''"
					+ ",'" + obj[0].toString().trim() + "','Y','" + userCode + "'"
					+ ",'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
					+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode+"')");
				int insert =objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
				    sqlBuilder.append("update tblimportexcel set strSubMenuHeadCode='" + code + "' "
					    + "where strSubMenuHeadName='" + obj[0].toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				}
			    } 
			}

			flgReturn = true;
		    }
		    }
		   
		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	  
	   private boolean funGenerateItemMaster(String useCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "", stkInEnable = "N", purchaseRate = "0.00", applyDiscount = "Y";
		long docNo = 0;
		try
		{
		    //clsGlobalVarClass.dbMysql.execute("truncate table tblitemmaster");
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strItemName),strSubGroupCode,strStockInEnable,dblPurchaseRate"
			    + ",strExternalCode,strItemDetails,strItemType,strApplyDiscount,strShortName,dblTax,strRevenueHead"
			    + ",strUOM,strRawMaterial,strRecipeUOM "
			    + "from tblimportexcel");
		    List listItemMaster = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		   if(listItemMaster.size()>0)
		    {
			   for(int i=0;i<listItemMaster.size();i++)
			   {
				   Object[] obj = (Object[])listItemMaster.get(i);
//			lblMessage.setText("");
//			lblMessage.setText("Importing Item Master");
			StringBuilder sqlBuilderNameCheck = new StringBuilder("select a.strItemCode from tblitemmaster a where a.strItemName='" + obj[0].toString() + "'");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if (listNameCheck.size()>0)
			{
			    code = (String) listNameCheck.get(0);
			    sqlBuilder.setLength(0);
			    sqlBuilder.append( "update tblimportexcel set strItemCode='" + code + "' "
				    + "where strItemName='" + obj[0].toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			}
			else
			{
			    if (obj[0].toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strItemCode,2,6)),'0' )as strItemCode  from tblitemmaster a  ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilderDoc, "sql");
				
				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "I" + String.format("%06d", docNo);
				}
				else
				{
				    docNo++;
				    code = "I" + String.format("%06d", docNo);
				}

				if (obj[2].toString().equals("Y"))
				{
				    stkInEnable = "Y";
				}
				if (obj[3].toString().trim().length() == 0)
				{
				    purchaseRate = "0.00";
				}
				else
				{
				    purchaseRate = obj[3].toString();
				}

				if (obj[7].toString().trim().length() == 0)
				{
				    applyDiscount = "Y";
				}
				else
				{
				    applyDiscount = obj[7].toString();
				}

				String rawMaterial = obj[12].toString();
				String itemForSale = "Y";
				if (rawMaterial.equalsIgnoreCase("Yes") || rawMaterial.equalsIgnoreCase("Y"))
				{
				    rawMaterial = "Y";
				    itemForSale = "Y";
				}
				else
				{
				    rawMaterial = "N";
				    itemForSale = "N";
				}
				String recipeUOM = obj[13].toString();

				 sqlBuilder.setLength(0);
				    sqlBuilder.append( "insert into tblitemmaster (strItemCode,strItemName,strSubGroupCode,strTaxIndicator"
					+ ",strStockInEnable,dblPurchaseRate,strExternalCode,strItemDetails,strUserCreated"
					+ ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strItemType,strDiscountApply"
					+ ",strShortName,strRevenueHead,strUOM,imgImage,strRawMaterial,strItemForSale,strRecipeUOM)"
					+ " values('" + code + "','" + obj[0].toString() + "','" + obj[1].toString() + "'"
					+ ",'" + obj[9].toString() + "','" + stkInEnable + "','" + purchaseRate + "','" + obj[4].toString() + "'"
					+ ",'" + obj[5].toString() + "','" + useCode + "'"
					+ ",'" + useCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
					+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "'"
					+ ",'" + obj[6].toString() + "','" + applyDiscount + "','" + obj[8].toString() + "'"
					+ ",'" + obj[10].toString() + "','" + obj[11].toString() + "','','" + rawMaterial + "'"
					+ ",'" + itemForSale + "','" + recipeUOM + "')");
				int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				if (insert == 1)
				{
					 sqlBuilder.setLength(0);
					    sqlBuilder.append( "update tblimportexcel set strItemCode='" + code + "' "
					    + "where strItemName='" + obj[0].toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				}
			    }
			}
		    }
		}
		flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	   
	   private boolean funGenerateItemMasterForRetail(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "", stkInEnable = "N", purchaseRate = "0.00", saleRate = "0.00";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strItemName),strSubGroupCode,strStockInEnable,dblPurchaseRate"
			    + ",strExternalCode,strItemDetails,strItemType,strApplyDiscount,strShortName"
			    + ",dblPriceMonday,strRevenueHead,strUOM,strRawMaterial,strRecipeUOM "
			    + "from tblimportexcel");
		    List listItemMaster = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listItemMaster.size()>0)
		    {
		    for(int i=0;i<listItemMaster.size();i++)
		    {
		    	Object[] obj = (Object[])listItemMaster.get(i);
//			lblMessage.setText("");
//			lblMessage.setText("Importing Item Master");
			if (obj[0].toString().trim().length() > 0)
			{
			    if (obj[2].toString().equals("Y"))
			    {
				stkInEnable = "Y";
			    }
			    if (obj[3].toString().trim().length() == 0)
			    {
				purchaseRate = "0.00";
			    }
			    if (obj[9].toString().trim().length() == 0)
			    {
				saleRate = "0.00";
			    }
			    else
			    {
				saleRate = obj[9].toString();
			    }
			    String receivedUOM = obj[11].toString();
			    String rawMaterial = obj[12].toString();
			    String itemForSale = "Y";
			    if (rawMaterial.equalsIgnoreCase("Yes") || rawMaterial.equalsIgnoreCase("Y"))
			    {
				rawMaterial = "Y";
				itemForSale = "Y";
			    }
			    else
			    {
				rawMaterial = "N";
				itemForSale = "N";
			    }
			    String recipeUOM = obj[13].toString();

			    docNo++;
			    code = "I" + String.format("%06d", docNo);
			    sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblitemmaster (strItemCode,strItemName,strSubGroupCode,strTaxIndicator"
				    + ",strStockInEnable,dblPurchaseRate,strExternalCode,strItemDetails,strUserCreated"
				    + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strItemType,strDiscountApply"
				    + ",strShortName,dblSalePrice,strRevenueHead,imgImage,strUOM,strRawMaterial,strRecipeUOM )"
				    + " values('" + code + "','" + obj[0].toString() + "','" + obj[1].toString() + "'"
				    + ",'','" + stkInEnable + "','" + purchaseRate + "','" + obj[4].toString() + "'"
				    + ",'" + obj[5].toString() + "','" + userCode + "'"
				    + ",'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
				    + ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "'"
				    + ",'" + obj[6].toString() + "','" + obj[7].toString() + "'"
				    + ",'" + obj[8].toString() + "'," + saleRate + ",'" + obj[10].toString() + "'"
				    + ",'','" + receivedUOM + "','" + rawMaterial + "','" + recipeUOM + "')");

			    System.out.println(sqlBuilder.toString());
			    int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			    if (insert == 1)
			    {
			    	sqlBuilder.setLength(0);
				    sqlBuilder.append( "update tblimportexcel set strItemCode='" + code + "' "
					+ "where strItemName='" + obj[0].toString() + "'");
				    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			    }
			}
		    }
		}
		 
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }

	   
	   private boolean funGenerateCostCenter(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String code = "";
		long docNo = 0;
		try
		{
		   StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strCostCenterName) from tblimportexcel");
		    
		    List listCostCenter = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listCostCenter.size()>0)
		    {
		    for(int i=0;i<listCostCenter.size();i++)
		    {
		    Object obj = (Object)listCostCenter.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Cost Center Master");

			StringBuilder sqlBuilderNameCheck = new StringBuilder(" select a.strCostCenterCode from tblCostCenterMaster a where a.strCostCenterName='" + obj.toString() + "' ");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlBuilderNameCheck, "sql");
			if(listNameCheck.size()>0)
			{
				code = listNameCheck.get(0).toString();
			    sqlBuilder.setLength(0);
			    sqlBuilder.append("update tblimportexcel set strCostCenterCode='" + code + "' "
				    + "where strCostCenterName='" + obj.toString() + "'");
			    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			}	
			else
			{
			    if (obj.toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strCostCenterCode,2,2)),'0' )as strCostCenterCode "
					+ " from tblCostCenterMaster a  ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilderDoc, "sql");
				
				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "C" + String.format("%02d", docNo);
				}
				else
				{
				    docNo++;
				    code = "C" + String.format("%02d", docNo);
				}

				sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblCostCenterMaster (strCostCenterCode,strCostCenterName,strPrinterPort"
					+ ",strSecondaryPrinterPort,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited"
					+ ",strClientCode,strDataPostFlag)"
					+ " values('" + code + "','" + obj.toString() + "','','','" + userCode + "',"
					+ "'" + userCode + "','" +  objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',"
					+ "'" +  objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "','N')");
				int insert =objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
				    sqlBuilder.append("update tblimportexcel set strCostCenterCode='" + code + "' "
					    + "where strCostCenterName='" + obj.toString() + "'");
				    int update = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				}
			    }
			}
			
		    }
		    }
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }

	   private boolean funGenerateCounterMasterHd(String userCode,String clientCode,String posCode)
	    {
		boolean flgReturn = false;
		String code = "";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strCounterName) from tblimportexcel");
		    List listCounter = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listCounter.size()>0)
		    {
		    for(int i=0;i<listCounter.size();i++)
		    {	
		    Object obj = listCounter.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Counter Master");
			if (obj.toString().trim().length() > 0)
			{
			    docNo++;
			    code = "C" + String.format("%02d", docNo);
			    sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblcounterhd (strCounterCode,strCounterName,strPOSCode,"
				    + "strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strDataPostFlag,strOperational)"
				    + " values('" + code + "','" + obj.toString() + "','" + posCode + "','" + userCode + "',"
				    + "'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "',"
				    + "'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + clientCode + "','N','Yes')");
			    int insert = objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			    if (insert == 1)
			    {
			    	sqlBuilder.setLength(0);
				    sqlBuilder.append("update tblimportexcel set strCounterCode='" + code + "' "
					+ "where strCounterName='" + obj.toString() + "'");
				int update =objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			    }
			}
		    }
		}
		 flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }

	   private boolean funGenerateCounterMasterDtl(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "";
		try
		{
		    //clsGlobalVarClass.dbMysql.execute("truncate table tblcostcentermaster");
		    StringBuilder sqlBuilder = new StringBuilder("select distinct(strMenuHeadCode),strCounterCode from tblimportexcel order by strCounterCode");
		    List listCounter = objBaseServiceImpl.funGetList(sqlBuilder, "sql"); 
		    if(listCounter.size()>0)
		    {
		    for(int i=0;i<listCounter.size();i++)
		    {
		    Object[] obj =(Object[])listCounter.get(i);
//			lblMessage.setText("");
//			lblMessage.setText("Importing Counter Master");
			StringBuilder strBuilder = new StringBuilder( "insert into tblcounterdtl (strCounterCode,strMenuCode,strClientCode)"
					+ " values('" + obj[1].toString() + "','" + obj[0].toString() + "','" + clientCode + "')");
			int insert = objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
		    }
		    }
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	   
	   private boolean funGenerateAreaMaster(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String query = "", code = "";
		long docNo = 0;
		try
		{
		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strAreaName) from tblimportexcel");
		    List listAreaMaster = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listAreaMaster.size()>0)
		    {
		    for(int i=0;i<listAreaMaster.size();i++)
		    {
		    Object obj = listAreaMaster.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Area Master");

			StringBuilder sqlNameCheck = new StringBuilder(" select a.strAreaCode from tblareamaster a where a.strAreaName='" + obj.toString() + "' ");
			List listNameCheck = objBaseServiceImpl.funGetList(sqlNameCheck, "sql");
			if(listNameCheck.size()>0)
			{
				code = listNameCheck.get(0).toString();
				sqlBuilder.setLength(0);
			    sqlBuilder.append( "update tblimportexcel set strAreaCode='" + code + "' "
				    + "where strAreaName='" + obj.toString() + "'");
			   objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
			}
			else
			{
			    if (obj.toString().trim().length() > 0)
			    {
				StringBuilder sqlBuilderDoc = new StringBuilder(" select ifnull(max(MID(a.strAreaCode,2,3)),'0' )as strAreaCode from tblareamaster a  ");
				List listDocCode = objBaseServiceImpl.funGetList(sqlBuilderDoc, "sql");
				if (listDocCode.size()>0)
				{
				    docNo = Long.parseLong(listDocCode.get(0).toString()) + 1;
				    code = "A" + String.format("%03d", docNo);
				}
				else
				{
				    docNo++;
				    code = "A" + String.format("%03d", docNo);
				}
				sqlBuilder.setLength(0);
			    sqlBuilder.append("insert into tblareamaster (strAreaCode,strAreaName,strUserCreated,strUserEdited,"
					+ "dteDateCreated,dteDateEdited)"
					+ "values('" + code + "','" + obj.toString() + "'"
					+ ",'" + userCode + "','" + userCode + "'"
					+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "')");
				int insert =  objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				if (insert == 1)
				{
					sqlBuilder.setLength(0);
				    sqlBuilder.append("update tblimportexcel set strAreaCode='" + code + "' "
					    + "where strAreaName='" + obj.toString() + "'");
				    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
				}
			    }
			}
		    }
		    }
		   
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("update tblinternal set dblLastNo=" + docNo + " where strTransactionType='Area'");
		    objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");

		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	   
	   private boolean funGenerateMenuItemPriceHD(String userCode)
	    {
		boolean flgReturn = false;
		String query = "";
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select distinct(strMenuHeadCode),strMenuHeadName,strPOSCode from tblimportexcel");
		    List listMenuItemPriceHd = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listMenuItemPriceHd.size()>0)
		    {
		    for(int i=0;i<listMenuItemPriceHd.size();i++)
		    {
		    Object[] obj = (Object[])listMenuItemPriceHd.get(i);	
//			lblMessage.setText("");
//			lblMessage.setText("Importing Price Master");
	    	sqlBuilder.setLength(0);
			sqlBuilder.append( "insert into tblmenuitempricinghd(strPosCode,strMenuCode,strMenuName,strUserCreated"
			+ ",strUserEdited,dteDateCreated,dteDateEdited) "
			+ "values('" + obj[2].toString() + "','" + obj[0].toString() + "'"
			+ ",'" + obj[1].toString() + "','" + userCode + "'"
			+ ",'" + userCode + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
			+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "')");
			objBaseServiceImpl.funExecuteUpdate(sqlBuilder.toString(), "sql");
		    }
		    }
		    
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	   
	   private boolean funGenerateMenuItemPriceDTL(String userCode,String clientCode)
	    {
		boolean flgReturn = false;
		String fromDate = "", toDate = "", priceMon = "", priceTue = "", priceWed = "", priceThu = "", priceFri = "", priceSat = "";
		String priceSun = "";
		Date dt = new Date();
		fromDate = (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + " ";
		fromDate += dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();

		toDate = (dt.getYear() + 1901) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + " ";
		toDate += dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();

		String query = "";
		try
		{
		    StringBuilder sqlEmptyPricingTable = new StringBuilder("truncate table tblmenuitempricingdtl");
		    objBaseServiceImpl.funExecuteUpdate(sqlEmptyPricingTable.toString(), "sql");

		    StringBuilder sqlBuilder = new StringBuilder();
		    sqlBuilder.setLength(0);
		    sqlBuilder.append("select distinct(strItemCode),strItemName,strPOSCode,strMenuHeadCode"
			    + ",dblPriceMonday,dblPriceTuesday,dblPriceWednesday,dblPriceThursday,dblPriceFriday"
			    + ",dblPriceSaturday,dblPriceSunday,strCostCenterCode,strAreaCode,strSubMenuHeadCode "
			    + ",strHourlyPricing,tmeTimeFrom,tmeTimeTo "
			    + "from tblimportexcel "
			    + "where (strRawMaterial='N' or strRawMaterial='') ");
		    List listMenuItemPricingDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		    if(listMenuItemPricingDtl.size()>0)
		    {
//			lblMessage.setText("");
//			lblMessage.setText("Importing Price Master");
		    for(int i=0;i<listMenuItemPricingDtl.size();i++)
		    {
		    	Object[] obj = (Object[])listMenuItemPricingDtl.get(i);
			if (obj[4].toString().trim().length() == 0)
			{
			    priceMon = "0.00";
			}
			else
			{
			    priceMon = funFormatPrice(obj[4].toString().trim());
			}

			if (obj[5].toString().trim().length() == 0)
			{
			    priceTue = "0.00";
			}
			else
			{
			    priceTue = funFormatPrice(obj[5].toString().trim());
			}

			if (obj[6].toString().trim().length() == 0)
			{
			    priceWed = "0.00";
			}
			else
			{
			    priceWed = funFormatPrice(obj[6].toString().trim());
			}

			if (obj[7].toString().trim().length() == 0)
			{
			    priceThu = "0.00";
			}
			else
			{
			    priceThu = funFormatPrice(obj[7].toString().trim());
			}

			if (obj[8].toString().trim().length() == 0)
			{
			    priceFri = "0.00";
			}
			else
			{
			    priceFri = funFormatPrice(obj[8].toString().trim());
			}

			if (obj[9].toString().trim().length() == 0)
			{
			    priceSat = "0.00";
			}
			else
			{
			    priceSat = funFormatPrice(obj[9].toString().trim());
			}

			if (obj[10].toString().trim().length() == 0)
			{
			    priceSun = "0.00";
			}
			else
			{
			    priceSun = funFormatPrice(obj[10].toString().trim());
			}

			StringBuilder strBuilder = new StringBuilder("insert into tblmenuitempricingdtl(strItemCode,strItemName,strPosCode,strMenuCode"
				+ ",strPopular,strPriceMonday,strPriceTuesday,strPriceWednesday,strPriceThursday,strPriceFriday"
				+ ",strPriceSaturday,strPriceSunday,dteFromDate,dteToDate,tmeTimeFrom,strAMPMFrom,tmeTimeTo"
				+ ",strAMPMTo,strCostCenterCode,strTextColor,strUserCreated,strUserEdited,dteDateCreated"
				+ ",dteDateEdited,strAreaCode,strSubMenuHeadCode,strHourlyPricing,strClientCode) "
				+ "values('" + obj[0].toString() + "','" + obj[1].toString() + "'"
				+ ",'" + obj[2].toString() + "','" + obj[3].toString() + "'"
				+ ",'N','" + priceMon + "','" + priceTue + "'" + ",'" + priceWed + "','" + priceThu + "'" + ",'" + priceFri + "'"
				+ ",'" + priceSat + "'" + ",'" + priceSun + "'"
				+ ",'" + fromDate + "','" + toDate + "'  "
				+ ",'" + obj[15].toString() + "', 'AM', '" + obj[16].toString() + "', 'AM','" + obj[11].toString() + "','Black'"
				+ ",'" + userCode + "','" + userCode + "'"
				+ ",'" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "','" + objGlobal.funGetCurrentDateTime("yyyy-MM-dd") + "'"
				+ ",'" + obj[12].toString() + "','" + obj[13].toString() + "'"
				+ ",'" + obj[14].toString() + "','" + clientCode + "') ");
					
			int insert =objBaseServiceImpl.funExecuteUpdate(strBuilder.toString(), "sql");
		    }
		    }
		    
		    flgReturn = true;

		}
		catch (Exception e)
		{
		    flgReturn = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgReturn;
		}
	    }
	   private String funFormatPrice(String price)
	    {
		if (price.contains(","))
		{
		    price = price.replace(",", "");
		}
		return price;
	    }
	  
	 private boolean funReadExcelFile(Sheet sheet)
	    {
		boolean flgResult = false;
		String query = "";
		
		try
		{
			
		    // Loop over first 10 column and lines
		    objBaseServiceImpl.funExecuteUpdate("truncate table tblimportexcel", "sql");

		    for (int row = 1; row < sheet.getRows(); row++)
		    {
		    	
			query = "insert into tblimportexcel (strItemCode,strItemName,strShortName,strMenuHeadName,strSubMenuHeadName"
				+ ",strRevenueHead,strPOSName,strSubGroupName,strGroupName,strCostCenterName,strAreaName"
				+ ",dblTax,dblPurchaseRate,strExternalCode,strItemDetails,strItemType,strApplyDiscount"
				+ ",strStockInEnable,dblPriceSunday,dblPriceMonday,dblPriceTuesday,dblPriceWednesday"
				+ ",dblPriceThursday,dblPriceFriday,dblPriceSaturday,strCounterName,strUOM,strRecipeUOM,strRawMaterial"
				+ ",strHourlyPricing,tmeTimeFrom,tmeTimeTo) "
				+ "values(";
//			int noOfColumns = worksheet.getRow(row).getLastCellNum();
			for (int col = 0; col <sheet.getColumns(); col++)
			{
				 Cell cell = sheet.getCell(col, row);
			    CellType type = cell.getType();

			    String name = cell.getContents().trim();
			    //System.out.println(name+"\t"+col+"  "+row);//�
			    name = name.replaceAll("�", "");

			    if (col == 1)
			    {
				if (name.length() > 199)
				{
				    name = name.substring(0, 199);
				}
			    }

			    if (col == 28)
			    {
				if (name == null || name.isEmpty())
				{
				    name = "N";
				}
			    }

			    if (col == 29)
			    {
				if (name == null || name.isEmpty())
				{
				    name = "NO";
				}
			    }

			    if (col == 30)
			    {
				if (name == null || name.isEmpty())
				{
				    name = "HH:MM:S";
				}
			    }
			    if (col == 31)
			    {
				if (name == null || name.isEmpty())
				{
				    name = "HH:MM:S";
				}
			    }

			    if (col > 0)
			    {
				//query+=",'"+cell.getContents().trim()+"'";
				query += ",'" + name + "'";
			    }
			    else
			    {
				//query+="'"+cell.getContents().trim()+"'";
				query += "'" + name + "'";
			    }
			}
			query += ")";
			System.out.println(query);
			objBaseServiceImpl.funExecuteUpdate(query, "sql");
		    }
		    flgResult = true;
		}
		catch (Exception e)
		{
			
		    e.printStackTrace();
		}

		return flgResult;
	    }

	
	  public boolean funCheckEmptyDB()
	    {
		boolean flgResult = false;
		int groupCount = 0, subGroupCount = 0, itemMasterCount = 0, menuHeadCount = 0, subMenuHeadCount = 0, counterCount = 0;
		int costCenterCount = 0, menuItemPricingHd = 0, menuItemPricingDtl = 0;

		try
		{

		    flgResult = true;
		}
		catch (Exception e)
		{
		    flgResult = false;
		    e.printStackTrace();
		}
		finally
		{
		    return flgResult;
		}
	    }
}
