package application;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class List {
	private Document _listFile;
	private XPath _xPath;
	private String _listFilePath;

	public List() {
		/*
		 * Need to retrieve the save path indicated
		 * in the settings page.
		 */
		
		//createListFileIfNotExist();		
		_xPath = getNewXPath();
	}

	private void createListFileIfNotExist() {
		File file = new File(Constant.PATH_DEFAULT_LIST_FILE);
		
		boolean shouldCreate = !(file.exists());
		if (shouldCreate) {
			prepareNewList();
		}
		
		boolean toCreateNewXML = false;
		_listFile = getXMLDocument(toCreateNewXML);
	}
	
	private Document getXMLDocument(boolean toCreateNewXML) {
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = 
					documentFactory.newDocumentBuilder();
			
			if (toCreateNewXML) {
				return documentBuilder.newDocument(); 
			} else {
				return documentBuilder.parse(Constant.PATH_DEFAULT_LIST_FILE);
			}
		} catch (ParserConfigurationException | 
				 SAXException | IOException exception) {
			exception.printStackTrace();
		}		
		
		return null;
	}

	private XPath getNewXPath() {
        XPathFactory xpathFactory = XPathFactory.newInstance();

        return xpathFactory.newXPath();
	}

	private void prepareNewList() {
		try {
			boolean toCreateNewXML = true;
			Document document = getXMLDocument(toCreateNewXML);
									
			Element root = document.createElement(Constant.TAG_FILE);
			document.appendChild(root);
			
			Element nextId = document.createElement(Constant.TAG_NEXT_ID);
			Text nextIdText = document.createTextNode("1");
			nextId.appendChild(nextIdText);
			root.appendChild(nextId);
			
			Element categories = document.
								 createElement(Constant.TAG_CATEGORIES);
			root.appendChild(categories);
			
			Element category = document.createElement(Constant.TAG_CATEGORY);
			Text categoryText = document.
								createTextNode(Constant.CATEGORY_UNCATEGORISED);
			category.appendChild(categoryText);
			categories.appendChild(category);
			
			Element tasks = document.createElement(Constant.TAG_TASKS);
			root.appendChild(tasks);			

			// Create XML file
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = 
					new StreamResult(new File(Constant.PATH_DEFAULT_LIST_FILE));
			transformer.transform(domSource, streamResult);
			
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
	
	public int readNextId() {
		int nextId = 0;
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_NEXT_ID);
			
			Double value = (Double)expression.
						   evaluate(_listFile, XPathConstants.NUMBER);
			
			nextId = value.intValue();
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return nextId;
	}

}
