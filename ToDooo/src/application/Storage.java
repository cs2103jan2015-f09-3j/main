package application;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Storage {
	private static Transformer _transformer;
	private static XPath _xPath;
	private static DocumentBuilder _documentBuilder;
	
	public Storage() {		
		initTransformer();			
		_xPath = getNewXPath();
		_documentBuilder = getNewDocBuilder();
	}
	
	private void initTransformer() {
		try {
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			
			_transformer = transformerFactory.newTransformer();
			_transformer.setOutputProperty(OutputKeys.INDENT, 
					   					   Constant.XML_OUTPUT_INDENT);
			_transformer.setOutputProperty(Constant.XML_OUTPUT_INDENT_PROPERTY, 
										   Constant.XML_OUTPUT_INDENT_AMOUNT);
		} catch (TransformerConfigurationException exception) {
			exception.printStackTrace();
		}
	}
	
	private DocumentBuilder getNewDocBuilder() {		
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			
			return documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	private XPath getNewXPath() {
        XPathFactory xpathFactory = XPathFactory.newInstance();

        return xpathFactory.newXPath();
	}
	
	public String writeFile(Document document) {
		return writeFile(document, Main.list.getListFilePath());
	}
	
	public String writeFile(Document document, String savePath) {
		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = 
					new StreamResult(new File(savePath));
			_transformer.transform(domSource, streamResult);
		} catch (TransformerException exception) {
			return Constant.MSG_SAVE_FAIL;
		}
		
		return Constant.MSG_SAVE_SUCCESS;
	}

	public int readNextId() {
		int nextId = 0;
		Document fileDoc = getFileDocument();	
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_NEXT_ID);
			
			Double value = (Double)expression.
						   evaluate(fileDoc, XPathConstants.NUMBER);
			
			nextId = value.intValue();
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return nextId;
	}

	public String readSavePath() {
		String savePath = null;
		Document settingDoc = getSettingDocument();		
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_SETTING + "/" +
								   Constant.TAG_SETTING_SAVE);
			
			savePath = (String)expression.evaluate(settingDoc, 
										   XPathConstants.STRING);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return savePath;
	}

	public Document getFileDocument() {
		try {
			return _documentBuilder.parse(Main.list.getListFilePath()); 			
		} catch (SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}
	
	public Document getSettingDocument() {
		try {			
			return _documentBuilder.parse(Constant.PATH_SETTING); 			
		} catch (SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}

	public Task deleteTaskFromFileById(String targetId) {
		Document fileDoc = getFileDocument();	
		Task removedTask = null;
		
		NodeList nodes = getNodesById(fileDoc, targetId);
		if (nodes.getLength() > 0) {
			Node targetNode = nodes.item(Constant.START_INDEX);
			removedTask = XmlManager.transformNodeToTask(targetNode);
			
			targetNode.getParentNode().removeChild(targetNode);
			
			cleanAndWriteFile(fileDoc);
		} 
		
		return removedTask;
	}

	public void cleanAndWriteFile() {
		Document document = getFileDocument();
		cleanAndWriteFile(document);
	}

	public void cleanAndWriteFile(Document document) {
		try {
			NodeList nodes = (NodeList) _xPath.evaluate(Constant.XML_WHITESPACE_NODE_XPATH, 
														document, XPathConstants.NODESET);
			
			for (int i = 0; i < nodes.getLength(); i++) {
			    Node node = nodes.item(i);
			    node.getParentNode().removeChild(node);
			}
			
			writeFile(document);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
	}

	public NodeList getNodesById(Document document, String targetId) {
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_TASKS + "/" +
								   Constant.TAG_TASK + "[@" +
								   Constant.TAG_ATTRIBUTE_ID + "='" + 
								   targetId + "']");
			
			return (NodeList)expression.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}	

	public boolean hasCategoryInFile(Document document, String category) {
		Node node = null;
		String text = null;
		boolean hasCategory = false;
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_CATEGORIES);
			
			NodeList nodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			for (int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);
				text = node.getTextContent();
				
				if (text.equalsIgnoreCase(category)) {
					hasCategory = true;
					break;
				}
			}
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return hasCategory;
	}
	
	public String getOriginalTextById(String targetId) {
		String originalText = null;
		Document fileDoc = getFileDocument();
		NodeList nodes = getNodesById(fileDoc, targetId);
		
		if (nodes.getLength() > 0) {
			Element targetNode = (Element) nodes.item(Constant.START_INDEX);
			originalText =  XmlManager.getTextByTagName(targetNode, Constant.TAG_ORIGINAL);
		} 
		
		return originalText;
	}
}