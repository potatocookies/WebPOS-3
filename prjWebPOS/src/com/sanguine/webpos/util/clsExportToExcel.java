package com.sanguine.webpos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;

@Controller
public class clsExportToExcel
{
	public void funGenerateExcelFile(List dataList, HttpServletRequest request, HttpServletResponse response, String excelFileExtension)throws Exception {

		// get data model which is passed by the Spring container
		
		
		String reportName= (String) dataList.get(0);
		String[] HeaderList= (String[]) dataList.get(1);
		//response.setContentType("application/vnd.ms-excel");
		//response.setHeader("Content-disposition", "attachment; filename="+reportName.trim() +"."+excelFileExtension);
			
		System.out.println(request.getContextPath());
		String filePath = System.getProperty("user.dir");
		File file = new File(filePath + "/DayEndMailReports/"+ reportName.trim() +"."+excelFileExtension);
        file.createNewFile();
        
		Workbook workBook=funGetWorkBook(excelFileExtension);
		
	    List listStock = new ArrayList();
	    try{
	    	listStock =(List) dataList.get(2);
	    }catch(Exception e){
	    	listStock=new ArrayList();
	    }
	        
	    //create a new Excel sheet
	    Sheet sheet = workBook.createSheet("Sheet");
	    sheet.setDefaultColumnWidth(20);
	         
	    // create style for header cells
	    CellStyle style = workBook.createCellStyle();
	    Font font = workBook.createFont();
	    font.setFontName("Arial");
	    style.setFillForegroundColor(HSSFColor.BLUE.index);
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    font.setColor(HSSFColor.WHITE.index);
	    style.setFont(font);
	         
	    // create header row
	    Row headerRow = sheet.createRow(0);
	    for (int rowCount = 0;rowCount<HeaderList.length;rowCount++)
	    {
	    	headerRow.createCell(rowCount).setCellValue(HeaderList[rowCount].toString());
	    	headerRow.getCell(rowCount).setCellStyle(style);
	    }
	        	        
	    // create data rows
	    // aRow is  add Row
	    int ColrowCount = 1  ;
	    for(int rowCount=0;rowCount<listStock.size();rowCount++)
		{
	    	Row aRow = sheet.createRow(ColrowCount++);
			List arrObj=(List) listStock.get(rowCount);
			for(int Count=0;Count<arrObj.size();Count++)
			{
				if(null!=arrObj.get(Count) && arrObj.get(Count).toString().length()>0)
				{			
					if(isNumeric(arrObj.get(Count).toString()))
					{
						aRow.createCell(Count).setCellValue(Double.parseDouble(arrObj.get(Count).toString()));
					}
					else
					{
						aRow.createCell(Count).setCellValue(arrObj.get(Count).toString());
					}
				}
				else
				{
					aRow.createCell(Count).setCellValue("");
				}
			}
		}
	    
	    FileOutputStream outputStream = new FileOutputStream(file);
	    workBook.write(outputStream);
	    outputStream.flush();
	}
	
	
	private Workbook funGetWorkBook(String excelFilePath) throws IOException {
	    
		Workbook workbook = null;
	    if (excelFilePath.equals("xlsx")) {
	        workbook = new XSSFWorkbook();
	    } else if (excelFilePath.equals("xls")) {
	        workbook = new HSSFWorkbook();
	    } else {
	        throw new IllegalArgumentException("The specified file is not Excel file");
	    }
	 
	    return workbook;
	}
	
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
