import java.util.HashMap;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import cmima.icos.GnSpeaker;
import junit.framework.TestCase;

/**
 * 
 */

/**
 * @author Micho Garcia
 *
 */
public class TestGnSpeaker extends TestCase {

	/**
	 * Test method for {@link cmima.icos.GnSpeaker#ask2gn()}.
	 */
	public void testAsk2gn() {
		
		HashMap<String, String[]> parametros = new HashMap<String, String[]>();
		String data[] = {"gribtocdl"};
		parametros.put("variables", data);
		
		GnSpeaker Gnhablador = new GnSpeaker(parametros);
		
		try {
			assertEquals(1, Gnhablador.ask2gn());
		} catch (GNLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GNServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
