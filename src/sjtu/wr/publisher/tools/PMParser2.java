package sjtu.wr.publisher.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sjtu.wr.utils.AsciiSaveUtil;
import sjtu.wr.utils.OperateXMLByDOM;
import sjtu.wr.utils.PropertiesUtil;



public class PMParser2 {
	
	private String dirName = null;
	private String projName = null;
	private File[] pmFiles = null;
	private Connection con = null;
	private String outDir = null;
	
	public PMParser2(File [] pms, String pjName, String dir, String odir, Connection c) {
		pmFiles = pms;
		projName = pjName;
		dirName = dir;
		con = c;
		outDir = odir;
	}
	
	public boolean parse() throws Exception{
		
		// 侧边目录xml文档
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmlDir = builder.newDocument();
		Element root = xmlDir.createElement("root");
		xmlDir.appendChild(root);
		root.setAttribute("text", projName);
		TreeViewDocBuilder dirBuilder = new TreeViewDocBuilder(xmlDir, root);

		for (File pmcFile: pmFiles){
			
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(pmcFile);
			
			if (doc == null) return false;
			dirBuilder.addPM(doc);		
//			TODO comment them for test only.
			List<String> itemList = getRequestedItemList(doc);
			if (itemList == null) continue;
			
			List<File> fileList = getUpdatingFileList(itemList);
			updateContent(fileList);		
		}
		SaveDirectory(xmlDir);

		return true;
	}

	// 检查项目是否存在对应文件，是否需要被更新
	protected List<File> getUpdatingFileList(List<String> fileList){
		
		File dir = new File(dirName);
		File[] xmls = dir.listFiles(new FilenameFilter(){
			public boolean accept(File file, String name) {
				return name.endsWith(".xml");
			}
		});
		
		fileList.sort(null);
		Arrays.sort(xmls);
		
		LinkedList<File> validFiles = new LinkedList<File>();
		int idx = 0, num = xmls.length;
		for (String fileName:fileList){
			Pattern pattern = Pattern.compile(fileName);
			while(idx < num){
				Matcher matcher = pattern.matcher(xmls[idx].getName());
				if (matcher.matches()){
					validFiles.add(xmls[idx]);
					idx ++;
					break;
				}
				idx ++;
			}
			if (idx >= num) break;			
		}
		return validFiles;		
	}
	
	// 打开pmc入口，读取所有的相关dm的名字
	// TO-DO: 检查是否存在无法读取的文件，载入ServletContext
	protected List<String> getRequestedItemList(Document doc) throws Exception{
		
		ArrayList<String> result = new ArrayList<String>();
		NodeList refdms = doc.getElementsByTagName("refdm");
		for (int i = 0; i < refdms.getLength(); i++){
			Node refdm = refdms.item(i);
			if (refdm.getParentNode().getNodeName() == "pmentry"){
				String dmName = "DMC";
				NodeList refdmChildren = refdm.getChildNodes();
				for (int j = 0; j < refdmChildren.getLength(); j++){
					Node code = refdmChildren.item(j);
					if (code.getNodeName() == "dmc"){
						dmName += code.getTextContent().replaceAll("\\s\\s*", ".*");
					} else if (code.getNodeName() == "language"){
						dmName += code.getAttributes().getNamedItem("language").getNodeValue()+"-";
						dmName += code.getAttributes().getNamedItem("country").getNodeValue()+".xml";
					}
				}
				result.add(dmName);
			}
		}
		
		return result;
	}
	
	protected boolean updateContent(List<File> fileList) throws SQLException, SAXException, IOException, ParserConfigurationException {
		
		DmDbWriter dbWriter = new DmDbWriter(con);
		
		dbWriter.initTables();
		for (File file: fileList){
			System.out.println("Insert DM: "+file);
			dbWriter.addDM(file);
		}
		
		dbWriter.destroy();
		return true;
	}

	protected void SaveDirectory(Document doc) throws TransformerException, ParserConfigurationException {
		
		String realPath= outDir;
		
		String xmlStr = OperateXMLByDOM.doc2FormatString(doc);
		AsciiSaveUtil.saveAscii(realPath+"test.xml", xmlStr);

		// hack的方法, 给每个目录加一个空节点
		List<Node> nl2 = new ArrayList<Node>();
		nl2.add(doc.getElementsByTagName("root").item(0));
		NodeList nl = doc.getElementsByTagName("children");
		for (int i = 0; i < nl.getLength(); i++) {
			if (((Element)nl.item(i)).getAttribute("id") == "")
				nl2.add(nl.item(i));
		}
		for (Node node: nl2){
			node.appendChild(doc.createElement("children"));
		}
		
		xmlStr = OperateXMLByDOM.doc2FormatString(doc);
		JSONObject soapDatainJsonObject = XML.toJSONObject(xmlStr);
				String jsonString = soapDatainJsonObject.toString();
		
		// 通过字符替换把这些空节点去掉，以达到产生[]括号对的目的
		String tmp = jsonString.replaceAll(",\\\"\\\"", "");
		String json = "["+tmp.substring(8, tmp.length()-1)+"]";
		AsciiSaveUtil.saveAscii(realPath+"test.json", json);
	}

	protected String getPMName(Document doc){
		// 取得pmc的名字
		String pmcName = "";
		if (doc != null){
			NodeList pmc = doc.getElementsByTagName("pmc");
			if (pmc == null || pmc.getLength() != 1) 
				return null;
			
			NodeList pmcContent = pmc.item(0).getChildNodes();
			for (int i = 0; i < pmcContent.getLength(); i++){
				if (pmcContent.item(i).getNodeName() == "#text") continue;
				pmcName += pmcContent.item(i).getTextContent() + "-";
			}
			
			NodeList pmtitle = doc.getElementsByTagName("pmtitle");
			if (pmtitle == null || pmtitle.getLength() != 1) 
				return null;
			pmcName += pmtitle.item(0).getTextContent();
		}
		
		System.out.println("出版物名称为: " + pmcName);
		return pmcName;
	}
}
