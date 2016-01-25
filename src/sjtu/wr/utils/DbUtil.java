package sjtu.wr.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Statement;

public class DbUtil {
	
	public static void createDBAndSwitch(Statement stmt, String db) throws SQLException
	{
		stmt.executeUpdate("DROP DATABASE IF EXISTS " + db + ";");
		stmt.executeUpdate("CREATE DATABASE " + db + ";");
		stmt.executeUpdate("USE "+ db + ";");
	}
	
	public Connection getCon() throws Exception{
		Class.forName(PropertiesUtil.getValue("jdbcName"));
		Connection con=DriverManager.getConnection(
				PropertiesUtil.getValue("UserDbUrl"), 
				PropertiesUtil.getValue("dbUserName"), 
				PropertiesUtil.getValue("dbPassword"));
		
		return con;
	}
	
	public void closeCon(Connection con)throws Exception{
		if(con!=null){
			con.close();
		}
	}
	
	
	
	public static void test(String[] args) {
		DbUtil userdbUtil=new DbUtil();
		Connection con = null;
		try {
			con = userdbUtil.getCon();
			System.out.println("数据库连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		} finally
		{
			try {
				userdbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
