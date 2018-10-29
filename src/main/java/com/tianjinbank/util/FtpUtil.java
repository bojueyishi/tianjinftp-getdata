package com.tianjinbank.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.tianjinbank.ftp.BootStrap;

public abstract class FtpUtil {
	public static Logger logger = Logger.getLogger(FtpUtil.class);
	
	public static FTPClient initFtpClient() {
		FtpConfig ftpConfig = FtpConfig.getInstance();
		String hostname = ftpConfig.getHostname();
		Integer port = ftpConfig.getPort();
		String username = ftpConfig.getUsername();
		String password = ftpConfig.getPassword();

		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		try {
			logger.info("开始连接ftp服务器:" + hostname + ":" + port);
			ftpClient.connect(hostname, port); // 连接ftp服务器
			ftpClient.login(username, password); // 登录ftp服务器
			ftpClient.sendCommand("OPTS UTF8", "ON");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				logger.error("连接ftp服务器失败:" + hostname + ":" + port);
			}
			logger.info("连接ftp服务器成功:" + hostname + ":" + port);
		} catch (IOException e) {
			ftpClient = null;
			logger.error("初始化ftp客户端失败！");
		}

		return ftpClient;
	}

	public static boolean checkExist(String pathname, String filename) {
		boolean isExist = false;

		FTPClient ftpClient = initFtpClient();
		try {
			// 切换FTP目录
			ftpClient.changeWorkingDirectory(pathname);
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile file : ftpFiles) {
				if (filename.equalsIgnoreCase(file.getName())) {
					isExist = true;
				}
			}
			ftpClient.logout();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return isExist;
	}

	public static boolean downloadFile(String pathname, String filename, String localpath) {
		boolean flag = false;
		OutputStream os = null;
		FTPClient ftpClient = initFtpClient();

		try {
			logger.info("开始下载文件");
			// 切换FTP目录
			ftpClient.changeWorkingDirectory(pathname);
			File localFile = new File(localpath + "/" + filename);
			os = new FileOutputStream(localFile);
			ftpClient.retrieveFile(filename, os);
			os.close();
			ftpClient.logout();
			flag = true;
			logger.info("下载文件成功.");
		} catch (Exception e) {
			logger.error("下载文件失败");
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
