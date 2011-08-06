package cmima.icos;

import cmima.icos.utils.BBox;
import junit.framework.TestCase;

public class BBoxTest extends TestCase {

	public void testBBox() {
		String[] coords = {"30.32","20.56","35.6768","25.3234"};
		BBox bbox = new BBox(coords);
		
		assertEquals(coords[0], bbox.getXmin());
		assertEquals(coords[1], bbox.getYmin());
		assertEquals(coords[2], bbox.getXmax());
		assertEquals(coords[3], bbox.getYmax());
	}

}
