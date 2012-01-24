package co.geomati.netcdf.utm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import co.geomati.netcdf.dataset.DatasetDoubleVariable;

public class MainUTMVariable implements DatasetDoubleVariable, UTMVariable {

	private String standardName;
	private String name;
	private String longName;
	private String units;
	private ArrayList<Double> data = new ArrayList<Double>();

	public MainUTMVariable(String name) throws IOException, ConverterException {
		this.name = name;
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
	}

	private String queryVocabulary(String name, String vocabularyContent,
			String child) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException {
		return (String) evaluateXPath(vocabularyContent,
				"/vocab/term[sado_term/text() = '" + name + "']/" + child,
				XPathConstants.STRING);
	}

	protected Object evaluateXPath(String content, String xpathExpression,
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

	@Override
	public String getLongName() {
		return longName;
	}

	@Override
	public String getStandardName() {
		return standardName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUnits() {
		return units;
	}

	@Override
	public Number getFillValue() {
		return 0;
	}

	@Override
	public List<Double> getData() {
		return data;
	}

	public void addSample(String value) {
		data.add(Double.parseDouble(value));
	}

}
