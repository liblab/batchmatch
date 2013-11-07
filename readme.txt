功能：
	将excel中维护的匹配关系批量固化到数据库中，格式如下：
	广告活动ID	spID	广告位id
	1002858	3xzhw	43
	1002858	3xzi+	43
	...

程序性质：
	java实现的可运行jar包，独立于mm系统，直接操作数据库。
	导入excel
	解析excel
	连接mm数据库mysql
	数据检查与导入

程序运行准备：
	java运行环境
	mysql账号
	配置文件放在jar同目录
	excel数据文件

配置文件说明：
	#数据库驱动，不需要修改
	jdbc.driver=com.mysql.jdbc.Driver com.microsoft.sqlserver.jdbc.SQLServerDriver
	
	#mm数据库信息，根据实际情况修改
	mysql.url=jdbc:mysql://localhost:3306/STC?characterEncoding=utf8&autoReconnect=true
	mysql.username=root
	mysql.password=
	mysql.maxconn=50
	
	#sqlserver信息，没有用到，不需要修改
	sqlserver.url=jdbc:sqlserver://10.100.3.53:1433;DatabaseName=lcslog
	sqlserver.username=sa
	sqlserver.password=123456
	sqlserver.maxconn=50
	
	#数据库连接日志文件，根据需要修改
	logfile=DBConnectionManager.log
	
	#自动匹配用户id
	matchuserid=295
	
	#excel文件路径，填写实际路径
	filepath=/home/contentsync/batchmatch.xlsx
	
运行：
	java -jar batchMatch.jar 参数
	运行准备完成后即可执行，如下为参数列表：
	 -bm,--batchmatch        批量匹配
	 -ck,--check             导入前检查数据
	 -cku,--checkunmatched   导入后检查检查数据
	 -h,--help               打印帮助信息