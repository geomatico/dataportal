package co.geomati.netcdf.utm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import co.geomati.netcdf.ConverterException;

public abstract class MainUTMVariable implements
		UTMVariable {

	private String standardName;
	private String name;
	private String longName;
	private String units;

	public MainUTMVariable(String name, String standardName, String longName,
			String units) {
		this.name = name;
		this.standardName = standardName;
		this.longName = longName;
		this.units = units;
	}

	public static MainUTMVariable create(String name) throws IOException,
			ConverterException {
		name = name.toLowerCase();
		String standardName;
		String longName;
		String units;
		URL vocabularyURL = new URL(
				"http://ciclope.cmima.csic.es:8080/dataportal/xml/vocabulario.xml");
		InputStream vocStream = vocabularyURL.openStream();
		String vocabulary = IOUtils.toString(vocStream);
		try {
			standardName = queryVocabulary(name, vocabulary, "nc_term");
			longName = queryVocabulary(name, vocabulary, "nc_long_term");
			units = queryVocabulary(name, vocabulary, "nc_units");
		} catch (Exception e) {
			throw new ConverterException("Cannot query vocabulary", e);
		}

		if (units.equals("seconds since 1970-01-01 00:00:00")) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			return new MainIntUTMVariable(name, standardName, longName, units,
					sf);
		} else {
			return new MainDoubleUTMVariable(name, standardName, longName,
					units);
		}
	}

	private static String queryVocabulary(String name,
			String vocabularyContent,
			String child) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException {
		return (String) evaluateXPath(vocabularyContent,
				"/vocab/term[sado_term/text() = '" + name + "']/" + child,
				XPathConstants.STRING);
	}

	protected static Object evaluateXPath(String content,
			String xpathExpression,
			QName nodeType) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(content
				.getBytes()));
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath xpath = pathFactory.newXPath();
		XPathExpression expr = xpath.compile(xpathExpression);
		Object result = expr.evaluate(doc, nodeType);
		return result;
	}

	public String getLongName() {
		return longName;
	}

	public String getStandardName() {
		return standardName;
	}

	public String getName() {
		return name;
	}

	public String getUnits() {
		return units;
	}

	public Number getFillValue() {
		return 0;
	}

}
