/**
 * 
 */
package cmima.icos;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class QueryControllerTest extends TestCase {

	private HashMap<String, String[]> parametros = new HashMap<String, String[]>();
	private QueryController controlador = new QueryController();

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		String[] bboxes = { "[[-10,50,10,40]]" };
		parametros.put("bboxes", bboxes);
		String[] start_date = { "2011-07-10" };
		String[] end_date = { "2011-07-15" };
		parametros.put("start_date", start_date);
		parametros.put("end_date", end_date);
		String[] text = { "oscar fonts" };
		parametros.put("text", text);
		String[] start = { "1" };
		parametros.put("start", start);
		String[] limit = { "25" };
		parametros.put("limit", limit);
	}

	public void testAsk2gn() {
		
		HashMap<String, String[]> params = new HashMap<String, String[]>();
		String[] bboxes = { "[]" };
		params.put("bboxes", bboxes);
		String[] start_date = { "" };
		String[] end_date = { "" };
		params.put("start_date", start_date);
		params.put("end_date", end_date);
		String[] text = { "oscar fonts" };
		params.put("text", text);
		String[] start = { "1" };
		params.put("start", start);
		String[] limit = { "25" };
		params.put("limit", limit);
		
		String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<response totalcount=\"1\" success=\"true\">"
				+ "<data><metadata><id>icos-sample-1</id><schema>ISO 19115:2003/19139</schema>"
				+ "<title>Global Wind-Wave Forecast Model and Ocean Wave model</title>"
				+ "<summary>This is a sample global ocean file that comes with default Thredds installation. Its global attributes have been modified to conform with Dataset Discovery convention</summary>"
				+ "<extent>POLYGON (( -10 50,10 50,10 40,-10 40))</extent></metadata></data></response>";
		
		String testResponse = controlador.ask2gn(params);
		assertTrue(response.trim().equals(testResponse.trim()));
	}
}
