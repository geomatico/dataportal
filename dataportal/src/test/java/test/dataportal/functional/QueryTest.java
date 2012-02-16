package test.dataportal.functional;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class QueryTest extends AbstractFunctionalTest {

	private static final String LIMIT = "9999";
	private static final String START = "0";
	private static final String ORDER_FIELD = "title";
	private static final String ORDER_DIRECTION = "ASC";
	private static final String LANG = "es";
	private Services services;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		services = new Services();
	}

	public void testQuery() throws Exception {
		String ret = services.query(LANG, "", "", "", "", "", START, "2",
				ORDER_FIELD, ORDER_DIRECTION);
		assertTrue(ret.indexOf("<item>") != -1);
	}

	public void testFilterBBox() throws Exception {
		String ret = services.query(LANG, "[[-180, -90, 180, 90]]", "", "", "",
				"", START, LIMIT,
				ORDER_FIELD, ORDER_DIRECTION);
		System.err.println(ret);
		int totalCount = getItemCount(ret).intValue();
		assertTrue(totalCount > 0);

		ret = services.query(LANG, "[[-180, -90, 0, 90]]", "", "", "", "",
				START, LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int halfCount1 = getItemCount(ret).intValue();
		ret = services.query(LANG, "[[0, -90, 180, 90]]", "", "", "", "",
				START, LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int halfCount2 = getItemCount(ret).intValue();
		assertTrue(halfCount1 + halfCount2 >= totalCount);
	}

	public void testFilterDate() throws Exception {
		String ret = services.query(LANG, "", "2010-1-1", "2020-1-1", "", "",
				START, LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int total = getItemCount(ret).intValue();

		ret = services.query(LANG, "", "2010-1-1", "2010-6-1", "", "", START,
				LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int half1 = getItemCount(ret).intValue();
		ret = services.query(LANG, "", "2010-6-1", "2020-1-1", "", "", START,
				LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int half2 = getItemCount(ret).intValue();
		assertTrue(half1 > 0);
		assertTrue(half2 > 0);
		assertTrue(half1 + half2 >= total);
	}

	public void testFilterVariable() throws Exception {
		String ret = services.query(LANG, "", "", "", "sea_water_sigmat", "",
				START,
				LIMIT, ORDER_FIELD, ORDER_DIRECTION);
		int count = getItemCount(ret).intValue();
		assertTrue(count > 0);
	}

	private Number getItemCount(String ret)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		return (Number) evaluateXPath(ret, "count(//response/item)",
				XPathConstants.NUMBER);
	}

}
