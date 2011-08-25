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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.dataportal.csw.CSWCatalogQuery;
import org.dataportal.csw.CSWNamespaceContext;
import org.dataportal.utils.BBox;
import org.dataportal.utils.RangeDate;
import org.dataportal.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import junit.framework.TestCase;

/**
 * @author michogar
 * 
 */
public class CSWCatalogQueryTest extends TestCase {

	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	private CSWCatalogQuery query = new CSWCatalogQuery("gmd:MD_Metadata",
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
		String start = "1";
		parametros.put("start", start);
		String limit = "25";
		parametros.put("limit", limit);
	}

	/**
	 * Test method for
	 * {@link org.dataportal.csw.CSWCatalogQuery#createQuery(java.util.HashMap)}
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

			String startDateExpr = "//PropertyIsGreaterThanOrEqualTo/Literal/child::node()";
			Node testStartNode = (Node) xpath.evaluate(startDateExpr, testXML,
					XPathConstants.NODE);
			Node expectedStartNode = (Node) xpath.evaluate(startDateExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedStartNode.getNodeValue(),
					testStartNode.getNodeValue());

			String endDateExpr = "//PropertyIsLessThanOrEqualTo/Literal/child::node()";
			Node testEndNode = (Node) xpath.evaluate(endDateExpr, testXML,
					XPathConstants.NODE);
			Node expectedEndNode = (Node) xpath.evaluate(endDateExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedEndNode.getNodeValue(),
					testEndNode.getNodeValue());
			
			String lowerCornerExpr = "//lowerCorner/child::node()";
			Node testLowerCornerNode = (Node) xpath.evaluate(lowerCornerExpr, testXML,
					XPathConstants.NODE);
			Node expectedLowerCornerNode = (Node) xpath.evaluate(lowerCornerExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedLowerCornerNode.getNodeValue(),
					testLowerCornerNode.getNodeValue());
			
			String upperCornerExpr = "//upperCorner/child::node()";
			Node testUpperCornerNode = (Node) xpath.evaluate(upperCornerExpr, testXML,
					XPathConstants.NODE);
			Node expectedUpperCornerNode = (Node) xpath.evaluate(upperCornerExpr,
					expectedXML, XPathConstants.NODE);
			assertEquals(expectedUpperCornerNode.getNodeValue(),
					testUpperCornerNode.getNodeValue());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		// fail("Not yet implemented");
	}

}
