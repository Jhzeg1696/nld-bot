package com.jhzeg.nldbot.utilities;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class DatabasePool {
	private static BasicDataSource dataSource = new BasicDataSource();
	
	static {
		dataSource.setUrl("jdbc:mysql://54.227.139.204:3306/nld_bot?useUnicode=yes&characterEncoding=UTF-8");
		dataSource.setUsername("nld");
		dataSource.setPassword("nldbot");
		dataSource.setMinIdle(5);
		dataSource.setMaxIdle(10);
		dataSource.setMaxOpenPreparedStatements(100);
	}
	
	public static Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
	
	private DatabasePool() {
	
	}
}
