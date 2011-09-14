/**
 * 
 */
package test.dataportal.csw;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.dataportal.csw.CSWGetRecords;
import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;
import org.dataportal.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class CSWGetRecordsTest extends TestCase {

	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	private CSWGetRecords query = new CSWGetRecords("gmd:MD_Metadata",
			"csw:IsoRecord");
	private Document expectedXML;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		InputStream isTestResponse = getClass().getResourceAsStream(
				"/CSWCatalogQueryTestResponse.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		expectedXML = (Document) dBuilder.parse(isTestResponse);

		ArrayList<BBox> bboxes = Utils.extractToBBoxes("[[-10,50,10,40]]");
		parametros.put("bboxes", bboxes);
		String start_date = "2011-07-10";
		String end_date = "2011-07-15";
		RangeDate temporalExtent = new RangeDate(start_date, end_date);
		parametros.put("temporalExtent", temporalExtent);
		String text = "oscar fonts";
		parametros.put("text", text);
		// variables
		String variables[] = {"eastward_wind", "northward_wind"}; 
		parametros.put("variables", variables);
		// pagination
		String start = "0";
		String limit = "25";
		query.setMaxRecords(limit);
		query.setStartPosition(start);
		query.setSort("title");
		query.setDir("ASC");
	}

	/**
	 * Test method for
	 * {@link org.dataportal.csw.CSWGetRecords#createQuery(java.util.HashMap)}
	 * .
	 */
	public void testCreateQuery() {
		String queryResponse = query.createQuery(parametros);
		try {
			InputStream isQueryResponse = new ByteArrayInputStream(
					queryResponse.getBytes("UTF-8"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document testXML = (Document) dBuilder.parse(isQueryResponse);

			CSWNamespaceContext ctx = new CSWNamespaceContext();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(ctx);

			// test init date
			String startDateExpr = "//PropertyIsGreaterThanOrEqualTo/Literal/child::node()";
			Node testStartNode = (Node) xpath.evaluate(startDateExpr, testXML,
					XPathConstants.NODE);
			Node expectedStartNode = (Node) xpath.evaluate(startDateExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedStartNode.getNodeValue(),
					testStartNode.getNodeValue());

			// test end date
			String endDateExpr = "//PropertyIsLessThanOrEqualTo/Literal/child::node()";
			Node testEndNode = (Node) xpath.evaluate(endDateExpr, testXML,
					XPathConstants.NODE);
			Node expectedEndNode = (Node) xpath.evaluate(endDateExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedEndNode.getNodeValue(),
					testEndNode.getNodeValue());

			// test lower corner bbox
			String lowerCornerExpr = "//lowerCorner/child::node()";
			Node testLowerCornerNode = (Node) xpath.evaluate(lowerCornerExpr,
					testXML, XPathConstants.NODE);
			Node expectedLowerCornerNode = (Node) xpath.evaluate(
					lowerCornerExpr, expectedXML, XPathConstants.NODE);
			assertEquals(expectedLowerCornerNode.getNodeValue(),
					testLowerCornerNode.getNodeValue());

			// test upper corner bbox
			String upperCornerExpr = "//upperCorner/child::node()";
			Node testUpperCornerNode = (Node) xpath.evaluate(upperCornerExpr,
					testXML, XPathConstants.NODE);
			Node expectedUpperCornerNode = (Node) xpath.evaluate(
					upperCornerExpr, expectedXML, XPathConstants.NODE);
			assertEquals(expectedUpperCornerNode.getNodeValue(),
					testUpperCornerNode.getNodeValue());

			// text max records pagination
			String maxRecordsExpr = "GetRecords/attribute::maxRecords";
			Node testmaxRecordsNode = (Node) xpath.evaluate(maxRecordsExpr,
					testXML, XPathConstants.NODE);
			Node expectedMaxRecordsNode = (Node) xpath.evaluate(maxRecordsExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedMaxRecordsNode.getNodeValue(),
					testmaxRecordsNode.getNodeValue());

			// test limit records pagination
			String startPositionExpr = "GetRecords/attribute::startPosition";
			Node testStartPositionNode = (Node) xpath.evaluate(
					startPositionExpr, testXML, XPathConstants.NODE);
			Node expectedStartPositionNode = (Node) xpath.evaluate(
					startPositionExpr, expectedXML, XPathConstants.NODE);
			assertEquals(expectedStartPositionNode.getNodeValue(),
					testStartPositionNode.getNodeValue());

		} catch (Exception e) {
			e.printStackTrace();
		}
		// fail("Not yet implemented");
	}

}
