/**
 * 
 */
package cmima.icos;

import java.util.ArrayList;
import java.util.HashMap;

import cmima.icos.csw.CSWCatalog;
import cmima.icos.csw.CSWQuery;
import cmima.icos.utils.BBox;
import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class CSWCatalogTest extends TestCase {

	private String OUPUTSCHEMA = "csw:IsoRecord";
	private String TYPENAMES = "gmd:MD_Metadata";

	private CSWCatalog catalogo;
	private String CSWQuery;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		
		catalogo = new CSWCatalog("http://ciclope.cmima.csic.es:8080/geonetworkcmima/srv/en/csw");

		CSWQuery request = new CSWQuery(TYPENAMES, OUPUTSCHEMA);
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		String[] coords = { "-10", "50", "10", "40" };
		bboxes.add(new BBox(coords));
		parametros.put("bboxes", bboxes);
		CSWQuery = request.createQuery(parametros);

	}
	
	public void testSendCatalogRequest() {
		try {
			catalogo.sendCatalogRequest(CSWQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
