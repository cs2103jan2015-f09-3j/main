package application;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

public class Storage {
	private static Transformer _transformer;
	private static XPath _xPath;
	
	public Storage() {		
		try {
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			_transformer = transformerFactory.newTransformer();
			
			_xPath = getNewXPath();
		} catch (TransformerConfigurationException exception) {
			exception.printStackTrace();
		}
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
		Document settingDoc = getSettingDocument();	
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_SETTING + "/" +
								   Constant.TAG_SETTING_NEXT_ID);
			
			Double value = (Double)expression.
						   evaluate(settingDoc, XPathConstants.NUMBER);
			
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
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = 
					documentFactory.newDocumentBuilder();
			
			return documentBuilder.parse(Main.list.getListFilePath()); 
			
		} catch (ParserConfigurationException |
				SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}
	
	public Document getSettingDocument() {
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = 
					documentFactory.newDocumentBuilder();
			
			return documentBuilder.parse(Constant.PATH_SETTING); 
			
		} catch (ParserConfigurationException |
				SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}
}