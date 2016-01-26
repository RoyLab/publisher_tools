package sjtu.wr.publisher.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sjtu.wr.utils.DbUtil;
import sjtu.wr.utils.FileNameOp;
import sjtu.wr.utils.OperateXMLByDOM;
import sjtu.wr.utils.XSLTTransformer;

public class TaskManager {
	
	public final static String[] SEARCH_CLASS = {"pnr", "nsn", "para",
			"figure", "table", "step", "warning", "caution"};
	
	public final static String PIC_FORMAT =
			"jpg|jpeg|bmp|ico|gif|tif|tiff|png|"
			+"cgm|iso|CATDrawing|CATDrawing|CATPart|CATProduct|"
			+"ipt|iam|par|cfg|SLDPRT|SLDDRW|SLDASM|prt|"
			+"dwg|dgn|dst|asm|pcb|emf|wmf|ddb|sch|PDF|vsd";
	
	private String projName = null;
	
	public String getProjName() {
		return projName;
	}

	private String dbName = null;
	private DbUtil dbCon = null;
	private String srcDir = null;
	private String outDir = null;
	
	public void operateTask(String input, String output, String name) throws Exception{
		
		boolean result = false;
		srcDir = FileNameOp.makeDirName(input);
		dbName = "db_" + name;
		outDir = FileNameOp.makeDirName(output);;
		projName = name;
		dbCon = new DbUtil();
		
		Connection con = null;
		try {
			con = dbCon.getCon();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Statement stmt = con.createStatement();
		stmt.executeUpdate("USE db_test;");
		stmt.close();
		
		result = addDDNFileMap(con, input, output, name);
		if (!result)
			throw new Exception();
		
		result = processPMCs(con);
		if (!result)
			throw new Exception();
		
		result = processDMCs(con);
		if (!result)
			throw new Exception();
		
		result = processICNs(con);
		if (!result)
			throw new Exception();
		
		System.out.println("发布完成！");
	}
	
	private File[] getFileList(Statement stmt, String type) throws SQLException	{

		ResultSet res = stmt.executeQuery("select file from t_filecodemap where type='" + type + "';");
		ArrayList<File> files = new ArrayList<File>();
		while (res.next()){
			files.add(new File(srcDir + res.getString(1)));
		}
		return files.toArray(new File[files.size()]);
	}
	
	private boolean addDDNFileMap(Connection con, String input, String output, String name)
	{
		File[] ddns = findMatchedFile(input, "DDN-.*\\.xml", true);
		
		// TODO 可能不止一个DDN
		if (ddns == null || ddns.length == 0)
		{
			System.err.println("未找到ddn文件列表！");
			return false;
		}
		
		System.out.println("找到DDN目录： " + ddns[0].getName());
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
					+ "`type` VARCHAR(10) NOT NULL DEFAULT 'Unknown' , "
					+ "PRIMARY KEY (`id`), UNIQUE INDEX "
					+ "`idnew_table_UNIQUE` (`id` ASC));");
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			try {
				pstmt.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		// 逐条添加文件编码映射
		System.out.println("添加文件编码映射...");
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
	
		boolean error = false;
		int count = 0;
		try {
			pstmt = con.prepareStatement("insert into t_filecodemap "
					+ "values(null,?,?,?);");
			while (itr != null){
				if (itr.getNodeType() != Node.TEXT_NODE){
					
					String nodeName = itr.getNodeName();
					
					switch (nodeName) {
					case "dmcoricn":
						dmcoricn = itr.getTextContent();
						count++;
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
			e.printStackTrace();
			error = true;
			
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("添加成功，DDN中包含共"+ count + "个文件!");
		
		return !error;
	}

	private boolean processPMCs(Connection con) throws Exception{
		
		Statement stmt = con.createStatement();
		File[] pms = getFileList(stmt, "PMC");
		if (pms == null || pms.length < 1)
			throw new Exception("PMC not found.");
		
		PMParser pm = new PMParser(pms, projName, outDir);
		boolean result = pm.parse();
		stmt.close();
	
		return result;
	}
	
	private boolean processDMCs(Connection con) throws Exception{
		 
		Statement stmt = con.createStatement();
		File[] dms = getFileList(stmt, "DMC");
		if (dms == null || dms.length < 1)
			throw new Exception("DMC not found.");
		stmt.close();
		
		System.out.println("添加全文索引，生成html，共个"+ dms.length +"文件！");
		DmDbWriter dbWriter = new DmDbWriter(con);
		dbWriter.initTables();
		Transformer xformer = XSLTTransformer.createTransformerWithPath(new File(this.getClass().
				getResource("/sjtu/wr/publisher/xslts/dm.xslt").getFile()));
		
		for (File file: dms){
			DMParser dmParser = new DMParser(file);
			DmDbDoc dmDoc = dmParser.parse();
			dbWriter.addDM(dmDoc);
			File out = new File(outDir + dmDoc.getHtml());
			FileWriter writer = new FileWriter(out);
			XSLTTransformer.xsl2StreamWithPath(new FileInputStream(file), writer, xformer);
		}
		return true;
	}
	
	private boolean processICNs(Connection con) throws Exception{
		
		Statement stmt = con.createStatement();
		File[] icns = getFileList(stmt, "ICN");
		if (icns == null || icns.length < 1)
			throw new Exception("ICN not found.");
		stmt.close();
		
		File[] pics = findMatchedFile(icns, "ICN-.*\\.("+GenThumbnails.READABLE_FORMAT+")", false);
		
		File file = new File(outDir + "thumb/"); 
		if(file!=null&&!file.exists()){ 
			file.mkdirs(); 
		} 
		
		GenThumbnails.genThumbnails(pics, file);
		for (File pic: pics){
			System.out.println(pic.getName());
		}
		
		return true;
	}
	
	private void insertCodeFilePair(PreparedStatement pstmt, String code, String file) throws SQLException{
	
		pstmt.setString(1, code);
		pstmt.setString(2, file);
		pstmt.setString(3, getDDNType(file));
		pstmt.executeUpdate();
	}
	
	private File[] findMatchedFile(File[] files, String regexp, boolean isSingle) {
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		
		ArrayList<File> matchedFiles = new ArrayList<File>();
		for (File file: files){
			Matcher m = pattern.matcher(file.getName());
			if (m.matches()){
				matchedFiles.add(file);
				if (isSingle) {
					return matchedFiles.toArray(new File[1]);
				}
			}
		}
		return matchedFiles.toArray(new File[matchedFiles.size()]);
	}
	
	private File[] findMatchedFile(String path, String regexp, boolean isSingle) {
		
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
		{
			System.err.println(path + ", 路径不存在或不是一个目录名！");
			return null;
		}
		
		File[] files = dir.listFiles();
		return findMatchedFile(files, regexp, isSingle);
	}
	
	private String getDDNType(String fileName){
		int ptr = fileName.indexOf('-');
		String type = fileName.substring(0, ptr);
		switch (type){
		case "ICN":
		case "PMC":
		case "DMC":
			return type;
		default:
			return "Unknown";
		}
	}
}
