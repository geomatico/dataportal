/**
 * 
 */
package test.dataportal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.dataportal.QueryController;
import org.dataportal.csw.DataPortalNS;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class QueryControllerTest extends TestCase {

	private QueryController controlador;
	private Document expectedXML;
	private HashMap<String, String[]> params;

	@Override
	protected void setUp() throws Exception {

		controlador = new QueryController();

		InputStream isTestResponse = getClass().getResourceAsStream(
				"/testResponse2Client.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		expectedXML = (Document) dBuilder.parse(isTestResponse);

		params = new HashMap<String, String[]>();

		String[] bboxes = { "[]" };
		params.put("bboxes", bboxes);
		String[] start_date = { "" };
		String[] end_date = { "" };
		params.put("start_date", start_date);
		params.put("end_date", end_date);
		String[] text = { "oceans" };
		params.put("text", text);
		String[] start = { "0" };
		params.put("start", start);
		String[] limit = { "25" };
		params.put("limit", limit);
		String[] variables = { "" };
		params.put("variables", variables);
		String[] sort = { "title" };
		params.put("sort", sort);
		String[] dir = { "ASC" };
		params.put("dir", dir);

		super.setUp();
	}


	/**
	 * @throws Exception
	 */
	public void testAsk2gnWithId() throws Exception {

		String[] id = { "98428023-edc0-4091-a0ea-54b39803a70d" };
		params.put("id", id);

		controlador.ask2gn(params);

	}
	
	/**
	 * @throws Exception
	 */
	public void testAsk2gn() throws Exception {

		String controllerResponse = controlador.ask2gn(params);

		InputStream isTestResponse = new ByteArrayInputStream(
				controllerResponse.getBytes("UTF-8"));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document controllerXML = (Document) dBuilder.parse(isTestResponse);

		DataPortalNS ctx = new DataPortalNS();

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(ctx);

		// variables
		String variablesExpr = "response/item/variables/child::node()";
		Node testVariablesNode = (Node) xpath.evaluate(variablesExpr,
				controllerXML, XPathConstants.NODE);
		Node expectedVariablesNode = (Node) xpath.evaluate(variablesExpr,
				expectedXML, XPathConstants.NODE);

		// TODO modificar estos asserts
		// assertEquals(expectedVariablesNode.getNodeValue(),
		// testVariablesNode.getNodeValue());

	}
}
