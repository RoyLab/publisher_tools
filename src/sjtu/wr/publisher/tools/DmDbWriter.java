package sjtu.wr.publisher.tools;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class DmDbWriter {
	
	private Connection con = null;
	
	private static String sqlCreateTableMain = "CREATE TABLE `t_main` ("+
			  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
			  "`dmc` varchar(128) DEFAULT NULL,"+
			  "`name` varchar(128) DEFAULT NULL," + 
			  "`modified` datetime NOT NULL DEFAULT '2000-01-01 11:11:11',"+
			  "`content` text,"+
			  "`html` text,"+
			  "`security` int(11) DEFAULT NULL,"+
			  "`language` varchar(10) DEFAULT NULL,"+
			  "`associateFile` varchar(256) NOT NULL,"+
			  "PRIMARY KEY (`id`),"+
			  "UNIQUE KEY `id_UNIQUE` (`id`),"+
			  "UNIQUE KEY `dmc_UNIQUE` (`dmc`)"+
			") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

	
	private static String sqlCreateTableContentTemplate = "CREATE TABLE `db_pm`.`t_?` ("+
			  "`id` INT NOT NULL AUTO_INCREMENT,"+
			  "`content` TEXT NULL,"+
			  "`dmId` INT NOT NULL,"+
			  "PRIMARY KEY (`id`),"+
			  "UNIQUE INDEX `idnew_table_UNIQUE` (`id` ASC),"+
			  "INDEX `fk_idx` (`dmId` ASC),"+
			  "CONSTRAINT `fk_?` "+
			    "FOREIGN KEY (`dmId`) "+
			    "REFERENCES `db_pm`.`t_main` (`id`) "+
			    "ON DELETE NO ACTION "+
			    "ON UPDATE NO ACTION);";
	
	private static String sqlDropTableTemplate = "DROP TABLE IF EXISTS t_?;";

	private static String sqlInsertMain="insert into t_main values(null,?,?,?,?,?,?,?,?);";
	private static String sqlInsertTemplate="insert into t_? values(null,?,?);";
	private static String sqlQueryDMId="select Id from t_main where dmc=?;";
	
	public DmDbWriter(Connection connection){
		con = connection;
	}

	public void initTables() throws SQLException {
		
		Statement pstmt = con.createStatement();
		for (int i = 0; i < TaskManager.SEARCH_CLASS.length; i++){
			pstmt.executeUpdate(sqlDropTableTemplate.replace("?", TaskManager.SEARCH_CLASS[i]));
		}
		pstmt.executeUpdate(sqlDropTableTemplate.replace("?", "main"));
		
		pstmt.executeUpdate(sqlCreateTableMain);
		for (int i = 0; i < TaskManager.SEARCH_CLASS.length; i++){
			pstmt.executeUpdate(sqlCreateTableContentTemplate.replaceAll("\\?", TaskManager.SEARCH_CLASS[i]));
		}
		pstmt.close();
	}
	
	public int getDMId(DmDbDoc doc) throws SQLException{
		PreparedStatement pstmt = con.prepareStatement(sqlQueryDMId);
		pstmt.setString(1, doc.getDmc());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) return rs.getInt(1);
		return -1;
	}
	
	public boolean addDM(File file) throws SQLException, SAXException, IOException, ParserConfigurationException{
		
		if (con == null){
			System.err.println("Disconnect from JDBC.");
		}
		
		DMParser dmParser = new DMParser(file.getAbsolutePath());
		DmDbDoc dmDoc = dmParser.parse();
		
		try {
			con.setAutoCommit(false);
			PreparedStatement pstmt=con.prepareStatement(sqlInsertMain);
			pstmt.setString(1, dmDoc.getDmc());
			pstmt.setString(2, dmDoc.getName());
			pstmt.setString(3, dmDoc.getModified());
			pstmt.setString(4, dmDoc.getContent());
			pstmt.setString(5, dmDoc.getHtml());
			pstmt.setInt(6, dmDoc.getSecurity());
			pstmt.setString(7, dmDoc.getLanguage());
			pstmt.setString(8, dmDoc.getAssociateFile());
			pstmt.executeUpdate();
			pstmt.close();
			
			dmDoc.setId(getDMId(dmDoc));
			
			for (int i = 0; i < TaskManager.SEARCH_CLASS.length; i++) {
				pstmt=con.prepareStatement(sqlInsertTemplate.replaceFirst("\\?",TaskManager.SEARCH_CLASS[i]));
				pstmt.setInt(2, dmDoc.getId());
				for (int j = 0; j < dmDoc.getComponents()[i].length; j++) {
					pstmt.setString(1, dmDoc.getComponents()[i][j]);
					pstmt.executeUpdate();
				}
				pstmt.close();
			}
			
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
		}finally{
			con.commit();
		}
		return true;
	}
	
	public void destroy() throws SQLException{
		if (con != null) con.close();
	}
}
