package com.mm.tool.batchmatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelImporter {
	private POIFSFileSystem fs;
    private Workbook wb;
    private Sheet sheet;
    
	public Sheet importFromExcel(){
		String filepath = Config.getFILE_PATH();
		try {
			InputStream is = new FileInputStream(filepath);
            //fs = new POIFSFileSystem(is);
			wb = new XSSFWorkbook(is);
            sheet = wb.getSheetAt(0);
            return sheet;
        } catch (IOException e) {
            e.printStackTrace();
    		return null;
        }
	}
	
}
