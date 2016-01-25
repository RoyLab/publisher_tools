package sjtu.wr.publisher.tools;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sjtu.wr.utils.OperateXMLByDOM;


public class DMParser {
	
	private File file = null;
	
	public DMParser(String path) {
		file = new File(path);
		if (!file.exists() || file.isDirectory()){
			System.err.println(path+" is not a valid file");
			file = null;
		}
	}

	public DmDbDoc parse() throws SAXException, IOException, ParserConfigurationException{
		
		Document doc = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(file);

		DmDbDoc dmDoc = new DmDbDoc();
		dmDoc.setDmc(file.getName().substring(4,32).replaceAll("-", ""));
		dmDoc.setName(doc.getElementsByTagName("techname").item(0).getTextContent()+" - "+
				doc.getElementsByTagName("infoname").item(0).getTextContent());
		
		dmDoc.setModified(getModifiedTime(file));
		
		Node content = doc.getElementsByTagName("content").item(0);
		dmDoc.setContent(getTextContent(content));
		
		String html = OperateXMLByDOM.doc2String(doc);
		dmDoc.setHtml(html);
		
		dmDoc.setSecurity(1);
		dmDoc.setLanguage("zh_CN");
		dmDoc.setAssociateFile(file.getAbsolutePath());
		
		for (int i = 0; i < TaskManager.SEARCH_CLASS.length; i++) {
			NodeList nl = doc.getElementsByTagName(TaskManager.SEARCH_CLASS[i]);
			dmDoc.getComponents()[i] = new String[nl.getLength()];
			for (int j = 0; j < nl.getLength(); j++) {
				dmDoc.getComponents()[i][j] = getTextContent(nl.item(j));
			}
		}
		
		return dmDoc;
	}
	
	public String getModifiedTime(File file){
		
		long time = file.lastModified();
		Date d = new Date(time);
		Format simpleFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		String dateString = simpleFormat.format(d);
		return dateString;
	}
	
	public String getTextContent(Node node){
		return node.getTextContent().trim().replaceAll("\\s\\s*", " ");
	}
}
