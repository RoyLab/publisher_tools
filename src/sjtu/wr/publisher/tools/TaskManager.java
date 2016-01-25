package sjtu.wr.publisher.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sjtu.wr.utils.DbUtil;
import sjtu.wr.utils.OperateXMLByDOM;

public class TaskManager {
	
	private static final String pmcReg = "PMC-.*\\.xml";
	private static final String ddnReg = "DDN-.*\\.xml";
	private static final String ddnTableName = "t_ddn";
	
	
	private static final String sqlDropTableTemplate = "DROP TABLE IF EXISTS t_?;";
	private static final String sqlInsertProject ="insert into t_projectlist values(null,?,?,?,?,?);";
	
	private DbUtil dbCon = null;
	
	void operateTask(String input, String output, String name){
		
		boolean result;
		
		result = addDDNFileMap(input, output, name);
		if (!result) return;
	}
	
	
	
	File[] findMatchedFile(File[] files, String regexp, boolean isSingle)
	{
		Pattern pattern = Pattern.compile(ddnReg);
		
		ArrayList<File> matchedFiles = new ArrayList<File>();
		File []result = null;
		for (File file: files){
			Matcher m = pattern.matcher(file.getName());
			if (m.matches()){
				matchedFiles.add(file);
				if (isSingle)
				{
					result = matchedFiles.toArray(new File[1]);
					return result;
				}
			}
		}
		return result;
	}
	
	File[] findMatchedFile(String path, String regexp, boolean isSingle)
	{
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
		{
			System.err.println(path + ", 路径不存在或不是一个目录名！");
			return null;
		}
		
		File[] files = dir.listFiles();
		return findMatchedFile(files, regexp, isSingle);
	}
	
	boolean addDDNFileMap(String input, String output, String name)
	{
		File[] ddns = findMatchedFile(input, ddnReg, true);
		
		// TODO 可能不止一个DDN
		if (ddns == null || ddns.length == 0)
		{
			System.err.println("未找到ddn文件列表！");
			return false;
		}
		
		System.out.println("找到DDN目录： " + ddns[0]);
		
		String dbName = "db_" + name;
		File ddn = ddns[0];
		
		long time = ddn.lastModified();
		Date date = new Date(time);
		Format simpleFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		String ddnTime = simpleFormat.format(date);
		
		date = new Date();
		String updateTime = simpleFormat.format(date);
		

		dbCon = new DbUtil();
		Connection con = null;
		try {
			con = dbCon.getCon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 添加记录到publisher当中，并添加新的数据库
		PreparedStatement pstmt = null;
		Statement stmt = null;
		try {
			pstmt = con.prepareStatement(sqlInsertProject);
			pstmt.setString(1, name);
			pstmt.setString(2, dbName);
			pstmt.setString(3, updateTime);
			pstmt.setString(4, ddnTime);
			pstmt.setString(5, ddn.getAbsolutePath());
			pstmt.executeUpdate();
			
			stmt = con.createStatement();
			DbUtil.createDBAndSwitch(stmt, dbName);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally{
			try {
				pstmt.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// 逐条添加文件编码映射
		Document ddnDom = OperateXMLByDOM.file2doc(ddn);
		NodeList delivlsts = ddnDom.getElementsByTagName("delivlst");
		if (delivlsts.getLength() != 1){
			System.err.println("delivlist node number: " + delivlsts.getLength());
			return false;
		}
		
		Node delivlst = delivlsts.item(0);
		Node itr = delivlst.getFirstChild();
		while (true)
		{
			itr.
		}
		
		try {
			dbCon.closeCon(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
