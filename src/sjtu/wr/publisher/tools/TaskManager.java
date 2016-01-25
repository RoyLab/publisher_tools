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

import net.sf.jcgm.core.MarkerColour;
import sjtu.wr.utils.DbUtil;
import sjtu.wr.utils.FileNameOp;
import sjtu.wr.utils.OperateXMLByDOM;

public class TaskManager {
	
	public static String[] SEARCH_CLASS = {"pnr", "nsn", "para",
			"figure", "table", "step", "warning", "caution"};
	
	private static final String pmcReg = "PMC-.*\\.xml";
	private static final String ddnReg = "DDN-.*\\.xml";
	private static final String ddnTableName = "t_ddn";

	private DbUtil dbCon = null;
	private String dbName = null;
	private String srcDir = null;
	
	void operateTask(String input, String output, String name){
		
		boolean result;
		
		dbCon = new DbUtil();
		Connection con = null;
		try {
			con = dbCon.getCon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		result = addDDNFileMap(con, input, output, name);
		
		if (!result){
			try {
				dbCon.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		result = indexDM(con);
		

	}
	
	File[] getPMFiles()	{
		return null;
	}
	
	boolean indexDM(Connection con){
		
		File[] pms = getPMFiles();
		for (File pm: pms){
			
		}
		
		return true;
	}
	
	void insertCodeFilePair(PreparedStatement pstmt, String code, String file) throws SQLException{
	
		pstmt.setString(1, code);
		pstmt.setString(2, file);
		pstmt.executeUpdate();
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
	
	boolean addDDNFileMap(Connection con, String input, String output, String name)
	{
		File[] ddns = findMatchedFile(input, ddnReg, true);
		
		// TODO 可能不止一个DDN
		if (ddns == null || ddns.length == 0)
		{
			System.err.println("未找到ddn文件列表！");
			return false;
		}
		
		System.out.println("找到DDN目录： " + ddns[0]);
		
		srcDir = FileNameOp.makeDirName(input);
		dbName = "db_" + name;
		File ddn = ddns[0];
		
		long time = ddn.lastModified();
		Date date = new Date(time);
		Format simpleFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		String ddnTime = simpleFormat.format(date);
		
		date = new Date();
		String updateTime = simpleFormat.format(date);
		
		// 添加记录到publisher当中，并添加新的数据库
		PreparedStatement pstmt = null;
		Statement stmt = null;
		try {
			pstmt = con.prepareStatement("insert into t_projectlist "
					+ "values(null,?,?,?,?,?,?);");
			
			pstmt.setString(1, name);
			pstmt.setString(2, dbName);
			pstmt.setString(3, updateTime);
			pstmt.setString(4, ddnTime);
			pstmt.setString(5, srcDir);
			pstmt.setString(6, ddn.getName());
			pstmt.executeUpdate();
			pstmt.close();
			
			stmt = con.createStatement();
			DbUtil.createDBAndSwitch(stmt, dbName);
			
			stmt.executeUpdate("CREATE TABLE t_filecodemap"
					+ "(`id` INT NOT NULL AUTO_INCREMENT,"
					+ "`code` VARCHAR(45) NOT NULL,"
					+ "`file` VARCHAR(90) NULL, "
					+ "PRIMARY KEY (`id`), UNIQUE INDEX "
					+ "`idnew_table_UNIQUE` (`id` ASC));");
			
			stmt.executeUpdate("CREATE TABLE `t_dmrecord` ("+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`dmc` varchar(128) DEFAULT NULL,"+
				  "`name` varchar(128) DEFAULT NULL," + 
				  "`modified` datetime DEFAULT NULL,"+
				  "`content` text,"+
				  "`html` text,"+
				  "`security` int(11) DEFAULT NULL,"+
				  "`language` varchar(10) DEFAULT NULL,"+
				  "`associateFile` varchar(256) NOT NULL,"+
				  "PRIMARY KEY (`id`),"+
				  "UNIQUE KEY `id_UNIQUE` (`id`),"+
				  "UNIQUE KEY `dmc_UNIQUE` (`dmc`)"+
				  ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			
		} catch (SQLException e1) {
			try {
				pstmt.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			e1.printStackTrace();
			return false;
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
		String ddnfilen = null;
		String dmcoricn = null;

		try {
			pstmt = con.prepareStatement("insert into t_filecodemap "
					+ "values(null,?,?);");
			while (itr != null){
				if (itr.getNodeType() != Node.TEXT_NODE){
					
					String nodeName = itr.getNodeName();
					
					switch (nodeName) {
					case "dmcoricn":
						dmcoricn = itr.getTextContent();
						insertCodeFilePair(pstmt, dmcoricn, ddnfilen);
						break;
					case "ddnfilen":
						ddnfilen = itr.getTextContent();
						break;
					default:
						break;
					}
				}
					
				itr = itr.getNextSibling();
			}
			pstmt.close();
			
		} catch (SQLException e) {
			try {
				pstmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		

		
		return true;
	}
}
