package sjtu.wr.publisher.tools;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeViewDocBuilder {
	
	private Document docTarget = null;
	private Node rootNode = null;
	
	public TreeViewDocBuilder(Document target, Node root) {
		docTarget = target;
		rootNode = root;
	}
	
	public void addPM(Document raw) throws ParserConfigurationException{
		
		NodeList contents = raw.getElementsByTagName("content");
		if (contents.getLength() < 1) return;
		
		Node content = contents.item(0);
		Element pmroot = docTarget.createElement("children");
		pmroot.setAttribute("text", raw.getElementsByTagName("pmtitle").item(0).getTextContent());
		rootNode.appendChild(pmroot);
				
		createPmentryNode(content, pmroot, docTarget);
	}
	
	protected void createPmentryNode(Node raw, Node target, Document doc){
		
		NodeList childNodes = raw.getChildNodes();
		
		for (int i = 0; i < childNodes.getLength(); i++){
			Node item = childNodes.item(i);
			if (item.getNodeName() == "pmentry"){
				Element child = doc.createElement("children");
				target.appendChild(child);
				child.setAttribute("text", getFilterName(item));
				createPmentryNode(item, child, doc);
				continue;
			}
			
			if (item.getNodeName() == "refdm"){
				Element child = doc.createElement("children");
				target.appendChild(child);
				child.setAttribute("text", getDmtitle(item));
				child.setAttribute("id", getDMCText(item));
				continue;
			}
		}
	}
	
	protected String getDMCText(Node node){
		
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++){
			Node item = nodes.item(i);
			String name = item.getNodeName();
			if (name == "dmc"){
				String [] raw = item.getTextContent().trim().split("\\s\\s*");
				return raw[0] + '-' + raw[1] + '-' + raw[2] + '-' + raw[3] + raw[4] + '-' + raw[5]
								+ '-' + raw[6] + raw[7] + '-' + raw[8] + raw[9]	+ '-' + raw[10];
			}
		}
		return null;
	}
	
	protected String getFilterName(Node node){
		
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++){
			Node item = nodes.item(i);
			String name = item.getNodeName();
			if (name == "title"){
				return item.getTextContent().trim();
			}
		}
		return null;		
	}
	
	protected String getDmtitle(Node node){
		
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++){
			Node item = nodes.item(i);
			String name = item.getNodeName();
			if (name == "dmtitle"){
				return item.getTextContent().trim().replaceAll("\\s\\s*", "-");
			}
		}
		return null;		
	}
}
