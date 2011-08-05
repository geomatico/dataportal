/**
 * 
 */
package cmima.icos;

import java.util.ArrayList;
import java.util.HashMap;

import cmima.icos.csw.CatalogRequest;
import cmima.icos.utils.BBox;
import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class CatalogRequestTest extends TestCase {
	
	private final String OUPUTSCHEMA = "csw:IsoRecord";
	private final String TYPENAMES = "csw:Record";
	
	CatalogRequest request = new CatalogRequest(TYPENAMES, OUPUTSCHEMA);
	
	/**
	 * TODO Don't work the test only run the method
	 */
	public void testCreateQuery(){
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		String[] coords = {"-10","50","10","40"};
		bboxes.add(new BBox(coords));
		parametros.put("bboxes", bboxes);		
		request.createQuery(parametros);
	}
}