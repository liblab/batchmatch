package com.mm.tool.batchmatch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class DatabaseManager {

	static private DatabaseManager instance;       // The single instance
    static private int clients;

    private Vector<Driver> drivers = new Vector<Driver>();
    private PrintWriter log;
    private Hashtable<String, DBConnectionPool> pools = new Hashtable<String, DBConnectionPool>();
    
    /**
     * Returns the single instance, creating one if it's the
     * first time this method is called.
     *
     * @return DBConnectionManager The single instance.
     */
    static synchronized public DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        clients++;
        return instance;
    }
    
    /**
     * A private constructor since this is a Singleton
     */
    private DatabaseManager() {
        init();
    }
    
    public static void main(String[] args) {
    	Connection c = DatabaseManager.getInstance().getConnection("mysql");
    	DatabaseManager.getInstance().freeConnection("mysql", c);
	}
    static{
    	getInstance();
    }

    /**
     * Loads properties and initializes the instance with its values.
     */
    private void init() {

    	final String configurepath = "/mmbatchmatchconfig.properties";
	    String path = System.getProperty("user.dir");
	    System.out.println("配置文件路径：" + path+configurepath);
		/* init the configuration file as input stream */
	    //InputStream cfgfileis;
        InputStream is ;
        try {
            is = new BufferedInputStream(new FileInputStream(path+configurepath));
        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("在jar包同级目录下没有找到配置文件，将使用jar包中的默认配置");
            is = this.getClass().getResourceAsStream(configurepath);
        }
        Properties dbProps = new Properties();
        try {
            dbProps.load(is);
        }
        catch (Exception e) {
            System.err.println("Can't read the properties file. " + "Make sure db.properties is in the CLASSPATH");
            return;
        }
        String logFile = dbProps.getProperty("logfile", "DBConnectionManager.log");
        try {
            log = new PrintWriter(new FileWriter(logFile, true), true);
        }
        catch (IOException e) {
            System.err.println("Can't open the log file: " + logFile);
            log = new PrintWriter(System.err);
        }
        loadDrivers(dbProps);
        createPools(dbProps);
    }

    /**
     * Loads and registers all JDBC drivers. This is done by the
     * DBConnectionManager, as opposed to the DBConnectionPool,
     * since many pools may share the same driver.
     *
     * @param props The connection pool properties
     */
    private void loadDrivers(Properties props) {
        String driverClasses = props.getProperty("jdbc.driver");
        StringTokenizer st = new StringTokenizer(driverClasses);
        while (st.hasMoreElements()) {
            String driverClassName = st.nextToken().trim();
            try {
                Driver driver = (Driver) Class.forName(driverClassName).newInstance();
                DriverManager.registerDriver(driver);
                drivers.addElement(driver);
                log("Registered JDBC driver " + driverClassName);
            }
            catch (Exception e) {
                log("Can't register JDBC driver: " +
                    driverClassName + ", Exception: " + e);
            }
        }
    }
    
    /**
     * Creates instances of DBConnectionPool based on the properties.
     * A DBConnectionPool can be defined with the following properties:
     * <PRE>
     * &lt;poolname&gt;.url         The JDBC URL for the database
     * &lt;poolname&gt;.user        A database user (optional)
     * &lt;poolname&gt;.password    A database user password (if user specified)
     * &lt;poolname&gt;.maxconn     The maximal number of connections (optional)
     * </PRE>
     *
     * @param props The connection pool properties
     */
    private void createPools(Properties props) {
        Enumeration<?> propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            if (name.endsWith(".url")) {
                String poolName = name.substring(0, name.lastIndexOf("."));
                String url = props.getProperty(poolName + ".url");
                if (url == null) {
                    log("No URL specified for " + poolName);
                    continue;
                }
                String user = props.getProperty(poolName + ".username");
                String password = props.getProperty(poolName + ".password");
                String maxconn = props.getProperty(poolName + ".maxconn", "0");
                int max;
                try {
                    max = Integer.valueOf(maxconn).intValue();
                }
                catch (NumberFormatException e) {
                    log("Invalid maxconn value " + maxconn + " for " + poolName);
                    max = 0;
                }
                DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, max);
                pools.put(poolName, pool);
                log("Initialized pool " + poolName);
            }
        }
    }
        
    /**
     * Returns an open connection. If no one is available, and the max
     * number of connections has not been reached, a new connection is
     * created.
     *
     * @param name The pool name as defined in the properties file
     * @return Connection The connection or null
     */
    public Connection getConnection(String name) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            return pool.getConnection();
        }
        return null;
    }
    
    /**
     * Returns a connection to the named pool.
     *
     * @param name The pool name as defined in the properties file
     * @param con The Connection
     */
    public void freeConnection(String name, Connection con) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            pool.freeConnection(con);
        }
    }
    
    /**
     * Returns an open connection. If no one is available, and the max
     * number of connections has not been reached, a new connection is
     * created. If the max number has been reached, waits until one
     * is available or the specified time has elapsed.
     *
     * @param name The pool name as defined in the properties file
     * @param time The number of milliseconds to wait
     * @return Connection The connection or null
     */
    public Connection getConnection(String name, long time) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            return pool.getConnection(time);
        }
        return null;
    }
    
    /**
     * Closes all open connections and deregisters all drivers.
     */
    public synchronized void release() {
        // Wait until called by the last client
        if (--clients != 0) {
            return;
        }
        
        Enumeration<DBConnectionPool> allPools = pools.elements();
        while (allPools.hasMoreElements()) {
            DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
            pool.release();
        }
        Enumeration<Driver> allDrivers = drivers.elements();
        while (allDrivers.hasMoreElements()) {
            Driver driver = (Driver) allDrivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log("Deregistered JDBC driver " + driver.getClass().getName());
            }
            catch (SQLException e) {
                log(e, "Can't deregister JDBC driver: " + driver.getClass().getName());
            }
        }
    }
    
    /**
     * Writes a message to the log file.
     */
    private void log(String msg) {
        log.println(new Date() + ": " + msg);
        System.out.println(new Date() + ": " + msg);
    }
    
    /**
     * Writes a message with an Exception to the log file.
     */
    private void log(Throwable e, String msg) {
        log.println(new Date() + ": " + msg);
        e.printStackTrace();
    }
    
}
