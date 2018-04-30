package iiml.wmp.ibfs.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author mayankch
 */
public class XMLUtility
{

	private final static Logger logger = LoggerFactory.getLogger(XMLUtility.class);
	private static Document dom;

	private static Document getDOM(String xmlString)
	{

		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("utf-8"))));

		}
		catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		}
		catch (SAXException se)
		{
			se.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return dom;

	}

	public static String parseXmlString(String xmlString)
	{

		logger.debug("---------Inside parseXMLString in XMLUtility-------------");
		String data = parseDocument(getDOM(xmlString));

		logger.debug("Parsed XML String is {}", data);
		return data;

	}

	private static String parseDocument(Document dom)
	{
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		String data = "";

		//get a nodelist of <Row> elements
		NodeList nl = docEle.getElementsByTagName("Row");
		if (nl != null && nl.getLength() > 0)
		{
			for (int i = 0; i < nl.getLength(); i++)
			{

				//get the Row element
				Element el = (Element) nl.item(i);
				data = getStringValue(el, "Cell");
				//System.out.println(data);
				break;
			}
		}
		return data;
	}

	public static void writeparseXmlString(String xmlString, String input)
	{

		logger.debug("---------Inside writeparseXmlString in XMLUtility-------------");
		writeParseDocumentToFile(getDOM(xmlString), input);

	}

	private static void writeParseDocumentToFile(Document dom, String input)
	{
		try
		{
			//get the root elememt
			Element docEle = dom.getDocumentElement();
			String data = "";
			FileUtils.writeStringToFile(new File(input), "", false);
			//get a nodelist of <Row> elements
			NodeList nl = docEle.getElementsByTagName("Row");
			if (nl != null && nl.getLength() > 0)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{

					//get the Row element
					Element el = (Element) nl.item(i);
					data = getStringValue(el, "Cell");
					//System.out.println(data);
					FileUtils.writeStringToFile(new File(input), data + "\r\n", true);
					//break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private static String getStringValue(Element ele, String tagName)
	{
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0)
		{
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

}
