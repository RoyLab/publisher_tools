package sjtu.wr.publisher.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import sjtu.wr.utils.AsciiSaveUtil;
import sjtu.wr.utils.OperateXMLByDOM;
import sjtu.wr.utils.PropertiesUtil;

public class PMParser {
	
	private File[] pmFiles = null;
	private String outDir = null;
	
	public PMParser(File [] pms, String pjName, String odir) {
		pmFiles = pms;
		outDir = odir;
	}
	
	public boolean parse() throws Exception{
		
		// 侧边目录xml文档
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmlDir = builder.newDocument();
		Element root = xmlDir.createElement("root");
		xmlDir.appendChild(root);
		root.setAttribute("text", PropertiesUtil.getInstance().getValue("projectName"));
		TreeViewDocBuilder dirBuilder = new TreeViewDocBuilder(xmlDir, root);

		for (File pmcFile: pmFiles){
			
			System.out.println("添加出版物: " + pmcFile.getName());
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(pmcFile);
			
			if (doc == null) return false;
			dirBuilder.addPM(doc);			
		}
		SaveDirectory(xmlDir);
		return true;
	}

	protected void SaveDirectory(Document doc) throws TransformerException, ParserConfigurationException {
		
		String xmlStr = OperateXMLByDOM.doc2FormatString(doc);
		AsciiSaveUtil.saveAscii(outDir+"tree.xml", xmlStr);

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
		AsciiSaveUtil.saveAscii(outDir+"tree.json", json);
	}
}
