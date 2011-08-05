/**
 * 
 */
package cmima.icos;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import cmima.icos.csw.Catalog;
import cmima.icos.csw.CatalogRequest;
import cmima.icos.utils.BBox;
import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class CatalogTest extends TestCase {

	private String OUPUTSCHEMA = "csw:IsoRecord";
	private String TYPENAMES = "gmd:MD_Metadata";

	private String CSWQuery;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {

		super.setUp();

		CatalogRequest request = new CatalogRequest(OUPUTSCHEMA, TYPENAMES);
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		String[] coords = { "-10", "50", "10", "40" };
		bboxes.add(new BBox(coords));
		parametros.put("bboxes", bboxes);
		this.CSWQuery = request.createQuery(parametros);

	}

	public void testSendCatalogRequest() {
		try {
			Catalog catalogo = new Catalog(
					"http://ciclope.cmima.csic.es:8080/geonetworkcmima/srv/en/csw");
			catalogo.sendCatalogRequest(this.CSWQuery);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
