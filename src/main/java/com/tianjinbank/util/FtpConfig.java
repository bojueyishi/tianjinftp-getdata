package com.tianjinbank.util;

public class FtpConfig {
	private String hostname;
	private Integer port;
	private String username;
	private String password;

	private static FtpConfig INSTANCE = new FtpConfig();

	public static FtpConfig getInstance() {
		return INSTANCE;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
