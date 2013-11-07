package com.mm.tool.batchmatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.format.CellTextFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelReader {
	Sheet sheet;
	int from;
	int to;
	
	public ExcelReader(Sheet sheet, int from, int to){
		this.sheet = sheet;
		this.from = from;
		this.to = to;
	}

	public  Map<String,Integer> readFromSheet(){
		try{
		Map<String,Integer> map = new HashMap<String, Integer>();
		if(sheet == null){
			return map;
		}
		//System.out.println(from + "-" + to);
		for(int i = from; i <= to; i++ ){
			synchronized(this){
				Row row = sheet.getRow(i);
				if(row == null){
					continue;
				}
				Cell cell = row.getCell(1);
				Cell cell2 = row.getCell(2);
				if(cell == null || cell2 == null){
					continue;
				}
				String spid = cell.getStringCellValue();
				cell2.setCellType(Cell.CELL_TYPE_NUMERIC);
				int mediaid = (int) cell2.getNumericCellValue();//Integer.valueOf(cell2.getStringCellValue());
				map.put(spid, mediaid);
		        super.notify();
			}
		}
		System.out.println(map.size());
		return map;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
