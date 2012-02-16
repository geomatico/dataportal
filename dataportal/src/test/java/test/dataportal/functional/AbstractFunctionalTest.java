package test.dataportal.functional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.mail.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.dataportal.SystemSingleton;
import org.dataportal.datasources.Mail;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class AbstractFunctionalTest extends TestCase {

	protected static final String DB_PATH = "target/test";
	protected Server server;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deleteIfExists(new File(DB_PATH + ".trace.db"));
		deleteIfExists(new File(DB_PATH + ".h2.db"));

		SystemSingleton.setPersistenceUnit("functional-tests");
		SystemSingleton.setMail(new Mail() {
			@Override
			public void send(String to, String subject, String template,
					Map<String, String> vars) throws MessagingException,
					IOException {
				/*
				 * Don't send in testing. We just look directly on the database
				 */
			}
		});

		server = new Server(8080);
		WebAppContext context = new WebAppContext();
		context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		context.setResourceBase("src/main/webapp/");
		context.setContextPath("/dataportal");
		context.setParentLoaderPriority(true);

		server.setHandler(context);
		server.start();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.stop();
	}

	private void deleteIfExists(File dbFile) {
		if (dbFile.exists()) {
			assertTrue(dbFile.delete());
		}
	}

	protected Object evaluateXPath(String ret, String xpathExpression,
			QName nodeType) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(ret.getBytes()));
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath xpath = pathFactory.newXPath();
		XPathExpression expr = xpath.compile(xpathExpression);
		Object result = expr.evaluate(doc, nodeType);
		return result;
	}

	protected String nodeList2String(NodeList down1, String rootNodeName)
			throws ParserConfigurationException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		Document newXmlDocument = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element root = newXmlDocument.createElement(rootNodeName);
		newXmlDocument.appendChild(root);
		for (int i = 0; i < down1.getLength(); i++) {
			Node node = down1.item(i);
			Node copyNode = newXmlDocument.importNode(node, true);
			root.appendChild(copyNode);
		}

		StringWriter sw = new StringWriter();
		Transformer serializer = TransformerFactory.newInstance()
				.newTransformer();
		serializer.transform(new DOMSource(root), new StreamResult(sw));
		String result = sw.toString();
		return result;
	}

}
