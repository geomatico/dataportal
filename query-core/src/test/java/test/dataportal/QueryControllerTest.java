/**
 * 
 */
package test.dataportal;

import java.util.HashMap;

import org.dataportal.QueryController;

import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class QueryControllerTest extends TestCase {

	private QueryController controlador = new QueryController();

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
				+ "<item><id>icos-sample-1</id><schema>ISO 19115:2003/19139</schema>"
				+ "<title>Global Wind-Wave Forecast Model and Ocean Wave model</title>"
				+ "<summary>This is a sample global ocean file that comes with default Thredds installation. Its global attributes have been modified to conform with Dataset Discovery convention</summary>"
				+ "<geo_extent>POLYGON (( -10 50,10 50,10 40,-10 40))</geo_extent></item></response>";
		
		String testResponse = controlador.ask2gn(params);
		// TODO Modificar el test con respuesta correcta
		//assertTrue(response.trim().equals(testResponse.trim()));
	}
}
