package com.mm.tool.batchmatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Main {
    
    private static Options opts = new Options();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    
	    buildOptions();
        //printUsage();
        processArgs(args);

	}
    
    private static void buildOptions() {

        Option config = new Option("c", "config", false, "查看配置文件中所有信息");
        config.setArgName("配置信息");
        //opts.addOption(config);
        
        Option optHelp = new Option("h", "help", false, "打印帮助信息");
        opts.addOption(optHelp);

        Option importContent = new Option("bm", "batchmatch", false, "批量匹配");
        importContent.setArgName("批量匹配");
        opts.addOption(importContent);
        
        Option checkData = new Option("ck", "check", false, "导入前检查数据");
        checkData.setArgName("导入前检查数据");
        opts.addOption(checkData);

        Option checkUnmatched = new Option("cku", "checkunmatched", false, "导入后检查数据");
        checkData.setArgName("导入后检查数据");
        opts.addOption(checkUnmatched);

    }

    @SuppressWarnings("static-access")
    private static  void processArgs(String[] args) {
        CommandLineParser parser = new PosixParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(opts, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
        }
        
        if (cl.hasOption("h")) {
            printUsage();
        }else if(cl.hasOption("c")){
            StringBuffer info = new StringBuffer();
            System.out.println("配置文件信息：\n");
            System.out.println(info.toString());
            
        }else if(cl.hasOption("bm")){
        	batchMatch1();
        }else if(cl.hasOption("ck")){
        	check();
        }else if(cl.hasOption("cku")){
        	checkUnMatched();
        }else{
            //cl.getArgs() == null || cl.getArgs().length == 0 ||
            printUsage();//syncContent();//importContent();
            System.out.println("请使用参数");
            batchMatch();
            //checkUnMatched();
            //check();
            //checkSpotsMatchedDate();
            //34835  89871
        }
//        System.out.println(System.getProperties().toString());
        //System.exit(0);
        return;
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BatchMatch", opts);
    }
    
	/**
	 * 多线程批量导入内容
	 */
    private static void batchMatch(){

        ExcelImporter importer =new ExcelImporter();
        Sheet sheet = importer.importFromExcel();
        int num = sheet.getPhysicalNumberOfRows();
        System.out.println("总行数：" + num);
        int count = 0;
        int time = num / 5000 + 1;
        //System.out.println(time);
        for(int i = 0; i < time; i++){
        	count ++;
        	Processor p = new Processor(sheet, i*5000, i*5000+4999, new ThreadListener() {
				@Override
				public void notifySuccess(String o) {
					System.out.println(o);
					//return o + " " + Thread.currentThread().getId();
				}
			});
        	new Thread(p, ""+i).start();
        	//System.out.println("此刻i的值：" + i);
        }
/*        new Processor(null, 0, 4999, new ThreadListener() {
			@Override
			public void notifySuccess(String o) {
				System.out.println(o);
				//return o + " " + Thread.currentThread().getId();
			}
		}, new HashMap<String, Integer>()).start();*/
        System.out.println("共"+ count +"个线程");
    }
    /**
	 * 检查数据正确性
	 */
    private static void check(){
    	Map<String, Integer> map = getDataMap();
        DealData dealData = new DealData();
        try {
        	dealData.check(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(map.size());
    }
    
    /**
   	 * 检查11-5号就已经匹配好的，而且跟所给匹配关系一致的数据
   	 */
       private static void checkSpotsMatchedDate(){
    	   Map<String, Integer> map = getDataMap();
           DealData dealData = new DealData();
           try {
	           	dealData.checkSpotsMatchedDate(map);
	   		} catch (Exception e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
           System.out.println(map.size());
       }
       
    /**
	 * 导入后检查没有匹配的数据
	 */
    private static void checkUnMatched(){
    	Map<String, Integer> map = getDataMap();
        DealData dealData = new DealData();
        try {
        	dealData.checkUnMatched(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(map.size());
    }
    
    /**
     * 解析excel数据到map中
     * @return
     */
    public static Map<String, Integer> getDataMap(){
        ExcelImporter importer =new ExcelImporter();
        Sheet sheet = importer.importFromExcel();
        int num = sheet.getPhysicalNumberOfRows();
        System.out.println("总行数1：" + num);
		Map<String,Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i < num; i++){
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
//			if("3yGt3".equals(spid)){
//				System.out.println(spid);
//			}
			cell2.setCellType(Cell.CELL_TYPE_STRING);
			//int mediaid = (int) cell2.getNumericCellValue();
			String mediaid = cell2.getStringCellValue();
			if(mediaid != null){
				mediaid = mediaid.replace("\n", "");
				mediaid = mediaid.replace("\t", "");
				mediaid = mediaid.replace(" ", "");
				try{
					map.put(spid, Integer.valueOf(mediaid));
				}catch (Exception e) {
					System.out.println("mediaid:" + mediaid +"解析错误");
					continue;
				}
			}
			//System.out.println("");
        }
        System.out.println(map.size());
        return map;
    }
    
    /**
	 * 单线程批量导入内容
	 */
    private static void batchMatch1(){
    	System.out.println("开始解析excel："+ new Date());
    	long t1 = System.currentTimeMillis();
    	Map<String, Integer> map = getDataMap();
        long t2 = System.currentTimeMillis();
        System.out.println("解析excel完毕，耗时：" +(t2-t1)/1000 + "秒");
        DealData dealData = new DealData();
        try {
			dealData.deal(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        long t3 = System.currentTimeMillis();
        System.out.println("导入完毕，耗时：" +(t3-t2)/1000 + "秒");
    	System.out.println("结束时间："+ new Date());
    }
}
