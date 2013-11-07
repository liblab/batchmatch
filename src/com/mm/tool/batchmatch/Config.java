package com.mm.tool.batchmatch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mysql.jdbc.util.PropertiesDocGenerator;

public class Config {



	private final String configurepath = "/mmbatchmatchconfig.properties";

	private final String KEY_FILE_APTH="filepath";
	private final String KEY_MATCH_USERID = "matchuserid";


	/* fill the value of the properties */
	private static String FILE_PATH;
	private static String MATCH_USERID;
   
	static{
		new Config();
	}
	
	private Config(){
	    String path = System.getProperty("user.dir");
	    System.out.println("配置文件路径：" + path+configurepath);
		/* init the configuration file as input stream */
	    InputStream cfgfileis;
        try {
            cfgfileis = new BufferedInputStream(new FileInputStream(path+configurepath));
        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("在jar包同级目录下没有找到配置文件，将使用jar包中的默认配置");
            cfgfileis = this.getClass().getResourceAsStream(configurepath);
        }
		//InputStream cfgfileis = this.getClass().getResourceAsStream(path+configurepath);
        /* init the input stream as properties*/
		Properties property = new Properties();
		try {
			property.load(cfgfileis);
			Config.FILE_PATH=property.getProperty(this.KEY_FILE_APTH);
			Config.MATCH_USERID=property.getProperty(this.KEY_MATCH_USERID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getFILE_PATH() {
		return FILE_PATH;
	}
	public static String getMatchUserId() {
		return MATCH_USERID;
	}

}
