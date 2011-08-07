/**
 * 
 */
package cmima.icos;

import java.util.ArrayList;
import java.util.HashMap;

import cmima.icos.csw.CSWQuery;
import cmima.icos.utils.BBox;
import cmima.icos.utils.RangeDate;
import junit.framework.TestCase;

/**
 * @author Micho Garcia
 * 
 */
public class CSWQueryTest extends TestCase {

	private final String OUPUTSCHEMA = "csw:IsoRecord";
	private final String TYPENAMES = "csw:Record";

	CSWQuery request = new CSWQuery(TYPENAMES, OUPUTSCHEMA);

	/**
	 * TODO Don't work the test only run the method
	 */
	public void testCreateQuery() {
		HashMap<String, Object> parametros = new HashMap<String, Object>();

		// bboxes
		ArrayList<BBox> bboxes = new ArrayList<BBox>();
		String[] coords = { "-10", "50", "10", "40" };
		bboxes.add(new BBox(coords));
		parametros.put("bboxes", bboxes);

		// temporal extent
		RangeDate temporalExtent = new RangeDate("2011-07-10T12:00:00Z",
				"2011-07-15T12:00:00Z");
		parametros.put("temporalExtent", temporalExtent);
		
		// freetext
		parametros.put("text", "prueba de texto");
		
		// variables 
		// TODO

		request.createQuery(parametros);
	}
}