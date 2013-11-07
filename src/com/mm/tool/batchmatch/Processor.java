package com.mm.tool.batchmatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Processor implements Runnable{

	Sheet sheet;
	ThreadLocal<Integer> localfrom = new ThreadLocal<Integer>();
	int from;
	int to;
	ThreadListener listener;
	//Map<String, Integer> map;
	Map<String,Integer> map = new HashMap<String, Integer>();
	public Processor(Sheet sheet, int from, int to, ThreadListener listener){
		this.sheet = sheet;
		this.from = from;
		this.to = to;
		this.listener = listener;
		//this.map = map;
	}

	private void process(){
		//ExcelReader reader = new ExcelReader(sheet, from, to);
		try{
			//Map<String,Integer> map = new HashMap<String, Integer>();
			if(sheet != null){
				
				System.out.println(from + "-" + to);
				for(int i = localfrom.get(); i <= to; i++ ){
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
			}
//			return map;
			}catch (Exception e) {
				e.printStackTrace();
			}
			
//			return null;
		//TODO 插入数据库
		if(map != null){
			//System.out.println("processor "+ Thread.currentThread().getId() + "result map size" + map.size());
			//listener.notifySuccess("map is null");
		}
		listener.notifySuccess("processor "+ Thread.currentThread().getId() +" completed");
		super.notify();
	}
	
	public void run(){
		//localfrom.set(this.from);
		//System.out.println("in importer thread" + Thread.currentThread().getId() + ":" + from + "-" + to);
/*		if(sheet == null){
			System.out.println(Thread.currentThread().getId() + "interrupted");
			Thread.interrupted();
		}*/
		//System.out.println(from + "-" + to);
		for(int i = from; i <= to; i++ ){
//				synchronized (sheet) {
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
				//System.out.println(Thread.currentThread().getId() + " " + j);
				
//			}
		}
        System.out.println(map.size()+","+from +"," + to);
		//System.out.println(j);

		//process();
		//System.out.println("importer thread" + Thread.currentThread().getId() + " complete");
	}
}
