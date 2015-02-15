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
	private XPath _xPath;
	private String _listFilePath;

	public List() {		
		_listFilePath = Main.storage.readSavePath();		
		createListFileIfNotExist();		
		_xPath = getNewXPath();
	}
	
	public String get_listFilePath() {
		return _listFilePath;
	}

	public void set_listFilePath(String listFilePath) {
		_listFilePath = listFilePath;
	}

	private void createListFileIfNotExist() {
		File file = new File(_listFilePath);
		String result = Constant.MSG_SAVE_SUCCESS;
		
		boolean shouldCreate = !(file.exists());
		if (shouldCreate) {
			result = prepareNewList();
		}
	}
	
	private XPath getNewXPath() {
        XPathFactory xpathFactory = XPathFactory.newInstance();

        return xpathFactory.newXPath();
	}

	private String prepareNewList() {
		Document document = createXMLDocument();
								
		Element root = document.createElement(Constant.TAG_FILE);
		document.appendChild(root);
		
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
		
		String result = Main.storage.writeFile(document, _listFilePath);
		return result;
	}
		
	private Document createXMLDocument() {
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = 
					documentFactory.newDocumentBuilder();
			
			return documentBuilder.newDocument(); 
			
		} catch (ParserConfigurationException exception) {
			exception.printStackTrace();
		}		
		
		return null;
	}
}
