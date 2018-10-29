package com.tianjinbank.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;

import com.tianjinbank.util.DomUtil;
import com.tianjinbank.util.FtpConfig;
import com.tianjinbank.util.FtpUtil;

public class BootStrap {
	public static Logger logger = Logger.getLogger(BootStrap.class);
	
	public static void main(String[] args) throws IOException {
		initConfig();
		//getXmls();
		testOracle();
	}

	public static void initConfig() throws IOException {
		Properties pro = new Properties();
		InputStream in = BootStrap.class.getClassLoader().getResourceAsStream("ftp.properties");
		pro.load(in);
		in.close();

		FtpConfig ftpConfig = FtpConfig.getInstance();
		ftpConfig.setHostname(pro.getProperty("hostname"));
		ftpConfig.setPort(Integer.parseInt(pro.getProperty("port")));
		ftpConfig.setUsername(pro.getProperty("username"));
		ftpConfig.setPassword(pro.getProperty("password"));
	}

	public static void getXmls() {
		// 通过ftp获取远程机器上的xml文件，如果没有则一直轮询
		String filename = "";
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				if(FtpUtil.checkExist(Constants.FTP_PATH, filename)) {
					boolean downloadFlag = FtpUtil.downloadFile(Constants.FTP_PATH, filename, Constants.LOCAL_PATH);
					//下载并解析成功才能停止轮询，否则下一轮继续轮询
					if (downloadFlag) {
						if(analysisXml()) {
							timer.cancel();
						}
					}else {
						logger.error("从ftp下载文件："+ filename + "失败！");
					}
				}
			}
		}, 1000*60);
	}

	public static boolean analysisXml() {
		boolean analysisFlag = false;
		String path = "C:\\Users\\lucasju\\Desktop\\temp\\IBFXEODPrices_201805_外高桥.xml";
		Document document = DomUtil.getReader(path);

		String productRegx = "//xs:element[@name='Product']/xs:complexType/xs:sequence/*";
		XPath xpath = DomUtil.getXpath(document, productRegx);
		List<Node> columnObj = xpath.selectNodes(document);

		Map<String, String> productCols = new HashMap<String, String>();
		columnObj.forEach(eachObj -> {
			Element column = (Element) eachObj;
			productCols.put(column.attribute("name").getText(), column.attribute("type").getText());
		});

		System.out.println(productCols);
		return analysisFlag;
	}
	
	/**
	 * 一个非常标准的连接Oracle数据库的示例代码
	 */
	public static void testOracle()
	{
	    Connection con = null;// 创建一个数据库连接
	    PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
	    ResultSet result = null;// 创建一个结果集对象
	    try
	    {
	        Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
	        System.out.println("开始尝试连接数据库！");
	        String url = "jdbc:oracle:" + "thin:@127.0.0.1:1521:XE";// 127.0.0.1是本机地址，XE是精简版Oracle的默认数据库名
	        String user = "system";// 用户名,系统默认的账户名
	        String password = "147";// 你安装时选设置的密码
	        con = DriverManager.getConnection(url, user, password);// 获取连接
	        System.out.println("连接成功！");
	        String sql = "select * from student where name=?";// 预编译语句，“？”代表参数
	        pre = con.prepareStatement(sql);// 实例化预编译语句
	        pre.setString(1, "小茗同学");// 设置参数，前面的1表示参数的索引，而不是表中列名的索引
	        result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
	        while (result.next())
	            // 当结果集不为空时
	            System.out.println("学号:" + result.getInt("id") + "姓名:"
	                    + result.getString("name"));
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    finally
	    {
	        try
	        {
	            // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
	            // 注意关闭的顺序，最后使用的最先关闭
	            if (result != null)
	                result.close();
	            if (pre != null)
	                pre.close();
	            if (con != null)
	                con.close();
	            System.out.println("数据库连接已关闭！");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	}
}
