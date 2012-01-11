package test.dataportal.functional;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class QueryTest extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return "search";
	}

	public void testQuery() throws Exception {
		String ret = callService(new String[] { "lang", "bboxes", "start_date",
				"end_date", "variables", "text", "start", "limit", "sort",
				"dir" }, new String[] { "es", "", "", "", "", "", "1", "2",
				"title", "ASC" });
		assertTrue(ret.indexOf("<item>") != -1);
	}

	public void testFilterBBox() throws Exception {
		String ret = queryBboxes("[[-180, -90, 180, 90]]");
		int totalCount = getItemCount(ret).intValue();
		assertTrue(totalCount > 0);

		ret = queryBboxes("[[-180, -90, 0, 90]]");
		int halfCount1 = getItemCount(ret).intValue();
		ret = queryBboxes("[[0, -90, 180, 90]]");
		int halfCount2 = getItemCount(ret).intValue();
		assertTrue(halfCount1 + halfCount2 >= totalCount);
	}

	public void testFilterDate() throws Exception {
		String ret = queryDates("2010-1-1", "2020-1-1");
		int total = getItemCount(ret).intValue();

		ret = queryDates("2010-1-1", "2010-6-1");
		int half1 = getItemCount(ret).intValue();
		ret = queryDates("2010-6-1", "2020-1-1");
		int half2 = getItemCount(ret).intValue();
		assertTrue(half1 > 0);
		assertTrue(half2 > 0);
		assertTrue(half1 + half2 >= total);
	}

	public void testFilterVariable() throws Exception {
		String ret = queryVariable("depth");
		int count = getItemCount(ret).intValue();
		assertTrue(count > 0);
	}

	private Number getItemCount(String ret)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(ret.getBytes()));
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath xpath = pathFactory.newXPath();
		XPathExpression expr = xpath.compile("count(//response/item)");
		Number count = (Number) expr.evaluate(doc, XPathConstants.NUMBER);
		return count;
	}

	private String queryBboxes(String bboxes) throws IOException, HttpException {
		return query(bboxes, "", "", "");
	}

	private String query(String bboxes, String start_date, String end_date,
			String variable) throws IOException, HttpException {
		return callService(new String[] { "lang", "bboxes", "start_date",
				"end_date", "variables", "text", "start", "limit", "sort",
				"dir" }, new String[] { "es", bboxes, start_date, end_date,
				variable, "", "1", "9999999", "title", "ASC" });
	}

	private String queryDates(String startDate, String endDate)
			throws IOException, HttpException {
		return query("", startDate, endDate, "");
	}

	private String queryVariable(String variable) throws IOException,
			HttpException {
		return query("", "", "", variable);
	}
}
