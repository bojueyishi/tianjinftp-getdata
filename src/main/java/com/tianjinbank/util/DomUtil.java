package com.tianjinbank.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public abstract class DomUtil {
	public static Document getReader(String fileName) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(new File(fileName));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}
	
	public static XPath getXpath(Document document, String xpath) {
		Map<String, String> map = new HashMap<String, String>();  
        map.put("xs","http://www.w3.org/2001/XMLSchema");  
        XPath xpathRet = document.createXPath(xpath);  
        xpathRet.setNamespaceURIs(map);  
        return xpathRet;
        
	}
	
	public static XPath getXpathForNode(Node node, String xpath) {
		Map<String, String> map = new HashMap<String, String>();  
        map.put("xs","http://www.w3.org/2001/XMLSchema");  
        XPath xpathRet = node.createXPath(xpath);  
        xpathRet.setNamespaceURIs(map);  
        return xpathRet;
        
	}
}
